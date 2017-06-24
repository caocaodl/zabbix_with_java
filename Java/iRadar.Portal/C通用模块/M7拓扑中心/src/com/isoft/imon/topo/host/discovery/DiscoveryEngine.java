package com.isoft.imon.topo.host.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.events.EventCenter;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.RouteTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.StpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.CdpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.huawei.NdpTableEntry;
import com.isoft.imon.topo.engine.discover.element.BcastDomain;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.netmgt.discoveryd.Discoveryd;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.context.TopoPool;
import com.isoft.imon.topo.util.CommonUtil;

public final class DiscoveryEngine {
	private static final Logger LOG = LoggerFactory.getLogger(DiscoveryEngine.class);
	
	private static DiscoveryEngine engine;
	private AtomicInteger workerNum;
	
	/**
	 * 获取引擎
	 * 
	 * @return
	 */
	public static synchronized DiscoveryEngine getEngine() {
		if (engine == null) {
			engine = new DiscoveryEngine();
		}
		return engine;
	}

	/**
	 * 构造方法
	 */
	private DiscoveryEngine() {
		workerNum = new AtomicInteger(0);
		init();
	}
	
	public void init() {
		workerNum.set(0);
	}
	
	public void workStart(){
		workerNum.incrementAndGet();
	}
	
	public void workComplete(){
		if(workerNum.decrementAndGet() == -1) {
			EventCenter.notice(Discoveryd.EVENT_DLINK_COMPLETE);
		}
	}

	public void discoveryLink(Scheduler scheduler) {
		identifyHostSubnetMembership();

		for (Host host : getPool().getHosts()) {
			CompositeBag<?> table;
			//CDP
			table = (CompositeBag) host.getBag(CdpTableEntry.class);
			if (!ImsUtil.isEmpty(table)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Start discoveryLink CDP: " + host);
				}
				scheduler.schedule(0, new CdpLinkWorker(host));
			}
			
			//NDP
			table = (CompositeBag) host.getBag(NdpTableEntry.class);
			if ((!ImsUtil.isEmpty(table)) && (!CommonUtil.isEmpty(host.getBridgeMac()))) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Start discoveryLink NDP: " + host);
				}
				scheduler.schedule(0, new NdpLinkWorker(host));
			}
			
			//ROUTE
			table = (CompositeBag) host.getBag(RouteTableEntry.class);
			if (!ImsUtil.isEmpty(table)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Start discoveryLink ROUTE: " + host);
				}
				scheduler.schedule(0, new RouteLinkWorker(host));
			}
			
			//STP
			table = (CompositeBag) host.getBag(StpTableEntry.class);
			if (!ImsUtil.isEmpty(table)) {
				if(LOG.isDebugEnabled()) {
					LOG.debug("Start discoveryLink STP: " + host);
				}
				scheduler.schedule(0, new StpLinkWorker(host));
			}
		}
		
		//FDB
		for (NetElement element : getPool().getElements()) {
			if (!element.getCategory().equals("Subnet")) {
				continue;
			}
			Subnet subnet = (Subnet) element;
			List<Host> _hosts = new ArrayList<Host>();
			for (Host host : getPool().getHosts()) {
				if ((host.getSubnets() != null) && (host.getSubnets().contains(subnet))) {
					if(LOG.isDebugEnabled()) {
						LOG.debug("Start discoveryLink FDB: " + host);
					}
					_hosts.add(host);
				}
			}
			if (!_hosts.isEmpty()) {
				scheduler.schedule(0, new FdbLinkWorker(_hosts));
			}
		}
		
		workComplete();
	}

	/**
	 * 确认服务器子网的关系
	 */
	private void identifyHostSubnetMembership() {
		boolean hasSubnet = false;
		for (NetElement ne : getPool().getElements()) {
			if ((ne instanceof Subnet)) {
				hasSubnet = true;
				break;
			}
		}
		if (!hasSubnet) {
			/* 当没有选择网络设备的时候不会发现子网。如果没有发现子网，则计算出所有设备的网络号作为子网. */
			/*
			 * List<Subnet> sns = this.discoveryConfig.doConstructSubnets(); for
			 * (Subnet sn:sns) { getPool().addElement((NetElement) sn); for
			 * (Host host:getPool().getHosts()) { host.addSubnet((Subnet) sn); }
			 * }
			 */
			for (Host host : getPool().getHosts()) {
				String netaddr = NetworkUtil.getNetAddress(host.getIpAddress(), host.getNetmask());
				if (!CommonUtil.isEmpty(netaddr)) {
					Subnet sn = new Subnet(host.getIpAddress(), host.getNetmask());
					host.addSubnet(sn);
				}
			}
		} else {
			for (Host host : getPool().getHosts()) {
				if (host.getSubnets() == null)
					attachSubnets(host);
			}

			for (Host host : getPool().getHosts()) {
				if (host.getSubnets() == null)
					continue;
				ArrayList<Subnet> sns = new ArrayList<Subnet>();
				for (Subnet subnet : host.getSubnets()) {
					BcastDomain bd = getPool().getBroadcastDomain(subnet.getNetAddress());
					if ((bd == null) || (bd.getNetAddresses() == null))
						continue;
					for (String na : bd.getNetAddresses()) {
						Subnet _subnet = getPool().getSubnetByIP(na);
						if (_subnet != null && sns != null) {
							sns.add(_subnet);
						}
					}
					if (sns != null) {
						for (Subnet _subnet : sns) {
							if (_subnet.ipInScope(host.getIpAddress())) {
								host.addSubnet(_subnet);
							}
						}
					}
				}
			}
		}
	}

	private void attachSubnets(Host host) {
		List<String> ips = new ArrayList<String>();
		ips.add(host.getIpAddress());
		String[] ifips;
		if ((host.getIfTable() != null) && (host.getIfTable().getEntities() != null)) {
			List<IfTableEntry> ifes = host.getIfTable().getEntities();
			for (IfTableEntry ife : ifes) {
				if (ife.getIpAddress() != null) {
					ifips = ife.getIpAddress().split(",");
					for (String ifip : ifips) {
						if (!ips.contains(ifip))
							ips.add(ifip);
					}
				}
			}
		}

		for (String ip : ips) {
			for (NetElement ne : getEngine().getPool().getElements()) {
				if ("Subnet".equals(ne.getCategory())) {
					Subnet sn = (Subnet) ne;
					if (sn.ipInScope(ip)) {
						host.addSubnet(sn);
					}
				}
			}
		}

		if (host.getSubnets() == null) {
			String netAddress = NetworkUtil.getNetAddress(host.getIpAddress());
			Subnet sn = getEngine().getPool().getSubnetByIP(netAddress);
			if (sn != null) {
				System.out.println("find unnormal subnet:" + host.getIpAddress() + "/" + sn);
				host.addSubnet(sn);
			}
		}
	}

	/**
	 * @return
	 */
	public TopoPool getPool() {
		return PollingPool.getPool();
	}
}
