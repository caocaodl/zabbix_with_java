package com.isoft.imon.topo.host.discovery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.ArpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTable;
import com.isoft.imon.topo.engine.discover.bag.host.FdbTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.WrapFdbTable;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class FdbLinkWorker extends DiscoveryWorker {
	private final List<Host> hosts;

	/**
	 * 构造方法
	 * 
	 * @param hosts
	 */
	public FdbLinkWorker(List<Host> hosts) {
		super("FdbLinkWorker");
		this.hosts = hosts;
	}

	/**
	 * 重写父类的dowork方法
	 */
	public void doWork() {
		for (Host sw : this.hosts) {
			if (sw.getBag(IfTableEntry.class) == null)
				constructIfTable(sw);
		}
		for (Host sw : this.hosts)
			handleBridge(sw);
	}

	private void handleBridge(Host sw) {
		FdbTable fdb = (FdbTable) sw.getBag(FdbTableEntry.class);
		if (fdb == null)
			return;

		WrapFdbTable fdbTable = fdb.getWrapFdbTable();
		for (String curPort : fdbTable.getPorts()) {
			IfTableEntry curIf = sw.getIfByPort(curPort);
			if (curIf == null)
				continue;
			Set<String> macs = fdbTable.getMacsByPort(curPort);
			if (macs != null) {
				List<Host> sws = findBridgeInMacs(macs, sw.getId());
				if (sws.isEmpty()) {
					/**
					 * 1.找到交换机到服务器或路由器的链路
					 */
					findSwitchToRouterLink(sw, curIf, curPort, macs);
				} else {
					/**
					 * 2.找到交换机与交换机之间的链路
					 */
					for (Host endHost : sws) {
						if (endHost.getId() == sw.getId()) {
							continue;
						}
						String endPort = findPortOnEndBridge(endHost, sw);
						if (endPort == null) {
							/**
							 * 如果对端交换机的fdb表中没有本机的bridgeMac,则有可能存在本接口的mac
							 */
							FdbTable _fdb = (FdbTable) endHost.getBag(FdbTableEntry.class);
							if (_fdb == null)
								continue;
							FdbTableEntry fte = (FdbTableEntry) _fdb.getEntry("mac", curIf.getMac());
							if (fte != null)
								endPort = fte.getPort();
						}
						IfTableEntry endIf = endHost.getIfByPort(endPort);
						if ((endIf == null) || (!isNearestBridgeLink(sw, curPort, endHost, endPort)))
							continue;
						if ((curIf.isVirtual()) || (endIf.isVirtual()))
							continue;
						Link link = Link.createLink(sw, curIf, endHost, endIf);
						link.setTag("fdb");
						TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
						// DiscoveryEngine.getEngine().getPool().addElement(link);
						if (topoPool != null) {
							topoPool.addElement(link);
						}
					}
				}
			}
		}
	}

	private boolean isNearestBridgeLink(Host sw1, String port1, Host sw2, String port2) {
		FdbTable macs1Table = (FdbTable) sw1.getBag(FdbTableEntry.class);
		FdbTable macs2Table = (FdbTable) sw2.getBag(FdbTableEntry.class);
		if (macs1Table == null || macs2Table == null) {
			return false;
		}
		Set<String> macs1 = macs1Table.getWrapFdbTable().getMacsByPort(port1);
		Set<String> macs2 = macs2Table.getWrapFdbTable().getMacsByPort(port2);
		if ((macs1 == null) || (macs2 == null) || (macs1.isEmpty()) || (macs2.isEmpty())) {
			return false;
		}
		boolean nearestLink = false;
		for (String curMac1 : macs1)
			if ((curMac1.equals(sw2.getBridgeMac())) || (sw2.getIfByMac(curMac1) != null)) {
				nearestLink = true;
			} else {
				if (curMac1.equals(sw1.getBridgeMac()))
					continue;
				if ((macs2.contains(curMac1)) && (isBridgeMacOfNode(curMac1)))
					return false;
			}
		return nearestLink;
	}

	/**
	 * 遍历所有交换机 1。若MAC集合里直接包括网桥MAC，则此设备满足条件
	 * 2。若该设备是网桥设备，且集合中的MAC是其一个端口的MAC，则此设备满足条件
	 * 
	 * @param macs
	 *            交换机一个端口内所包含的MAC
	 * @param swId
	 *            交换机的ID
	 * @return MAC集合里对应的网桥设备
	 */
	private List<Host> findBridgeInMacs(Set<String> macs, int swId) {
		List<Host> sws = new ArrayList<Host>();
		for (Host _host : this.hosts) {
			if (macs.contains(_host.getBridgeMac()))
				sws.add(_host);
			else {
				for (String _mac : macs) {
					if ((_host.getIfByMac(_mac) == null) || (!HostConstants.isBridge(_host.getCategory())))
						continue;
					sws.add(_host);
					break;
				}
			}
		}

		return sws;
	}

	/**
	 * 通过fdb和arp表查找链路,有可能找到路由或到服务器的链路
	 */
	private void findSwitchToRouterLink(Host startHost, IfTableEntry startIf, String startPort, Set<String> macs) {
		if (startIf.isVirtual())
			return;

		for (Host _host : this.hosts) {
			if (_host.getId() == startHost.getId()) {
				continue;
			}
			for (String mac : macs) {
				IfTableEntry _ife = _host.getIfByMac(mac);
				if ((_ife == null) || (_ife.isVirtual()))
					continue;
				if (!isNearestLink(startHost, startPort, mac)) {
					Logger.getLogger(getClass()).info(startHost.getIpAddress() + "和" + _host.getIpAddress() + "之间可能还有交换机");
				} else {
					Link link = Link.createLink(startHost, startIf, _host, _ife);
					link.setTag("fdb");
					TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
					// DiscoveryEngine.getEngine().getPool().addElement(link);
					if (topoPool != null) {
						topoPool.addElement(link);
					}
				}
			}
		}
	}

	private String findPortOnEndBridge(Host sw1, Host sw2) {
		Bag bag = sw1.getBag(FdbTableEntry.class);
		if (bag == null) {
			return null;
		}
		WrapFdbTable fdbTable = ((FdbTable) bag).getWrapFdbTable();
		for (String port : fdbTable.getPorts()) {
			Set<String> _macs = new HashSet<String>();
			_macs = fdbTable.getMacsByPort(port);
			if (_macs != null) {
				if (_macs.contains(sw2.getBridgeMac())) {
					return port;
				}
				for (String _mac : _macs) {
					if (sw2.getIfByMac(_mac) != null)
						return port;
				}
			}
		}
		return null;
	}

	/**
	 * 因为交换机或路由 的 上一级 的对应端口上的MAC地址数量至少会比其大1， 所以若指定一个交换机A的MAC，且此端口在另一交换机B的FDB表里，
	 * 则可以通过判断所有交换机中，A的MAC在其FDB表的端口上的MAC数量， 若没有一个比B小的，也就是说B的数量是最小的， 那么
	 * A和B就是最接近的链路
	 * 
	 * @param sw
	 *            交换机
	 * @param swPort
	 *            该交换机的端口
	 * @param mac
	 *            该端口中的另一交换机的MAC地址
	 * @return 是否是最近的链路
	 */
	private boolean isNearestLink(Host sw, String swPort, String mac) {
		FdbTable bag = (FdbTable) sw.getBag(FdbTableEntry.class);
		if (bag == null) {
			return false;
		}
		WrapFdbTable wft = bag.getWrapFdbTable();
		Set<String> macs = wft.getMacsByPort(swPort);
		if (macs == null) {
			return false;
		}
		int macsNum = macs.size();
		for (Host _host : this.hosts) {
			if ((_host.getBag(FdbTableEntry.class) != null) && (_host.getId() != sw.getId())) {
				FdbTable _fdb = (FdbTable) _host.getBag(FdbTableEntry.class);
				if (_fdb == null) {
					continue;
				}
				WrapFdbTable _wft = _fdb.getWrapFdbTable();
				String port = _fdb.getPortByMac(mac);
				if (port == null)
					continue;
				Set<String> macsSet = _wft.getMacsByPort(port);
				if (macsSet == null) {
					continue;
				}
				int _macsNum = macsSet.size();
				if (_macsNum < macsNum)
					return false;
			}
		}
		return true;
	}

	private boolean isBridgeMacOfNode(String mac) {
		TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
		if (topoPool == null) {
			return false;
		}
		// for (Host _host : DiscoveryEngine.getEngine().getPool().getHosts()) {
		for (Host _host : topoPool.getHosts()) {
			if ((mac.equals(_host.getBridgeMac())) || (_host.getIfByMac(mac) != null))
				return true;
		}
		return false;
	}

	private void constructIfTable(Host noIfHost) {
		String mac = null;
		TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
		if (topoPool == null) {
			return;
		}
		// for (Host _host : DiscoveryEngine.getEngine().getPool().getHosts()) {
		for (Host _host : topoPool.getHosts()) {
			CompositeBag<?> arpTable = (CompositeBag<?>) _host.getBag(ArpTableEntry.class);
			if (arpTable != null) {
				ArpTableEntry ate = (ArpTableEntry) arpTable.getEntry("ipAddress", noIfHost.getIpAddress());
				if ((ate != null) && (!HostConstants.INVALID_MAC.equals(ate.getMac()))) {
					mac = ate.getMac();
					break;
				}
			}
		}
		if (mac == null)
			return;

		IfTable ifTable = new IfTable();
		ifTable.setElementId(noIfHost.getId());
		IfTableEntry entry = new IfTableEntry();
		entry.setIndex("1");
		entry.setDescr("default-interface");
		entry.setType(300);
		entry.setSpeed(10000L);
		entry.setAdminStatus(1);
		entry.setOperStatus(1);
		entry.setIpAddress(noIfHost.getIpAddress());
		entry.setMac(mac);
		ifTable.add(entry);

		noIfHost.putBag(ifTable);
	}
}
