package com.isoft.imon.topo.host.discovery;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.huawei.NdpTableEntry;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class NdpLinkWorker extends DiscoveryWorker {
	private final Host host1;

	/**
	 * 构造方法
	 * 
	 * @param host1
	 */
	public NdpLinkWorker(Host host1) {
		super(host1.getIpAddress());
		this.host1 = host1;
	}

	/**
	 * 重写父类的dowork方法
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doWork() {
		CompositeBag<NdpTableEntry> ndpTable = (CompositeBag) this.host1.getBag(NdpTableEntry.class);
		if (ndpTable == null) {
			return;
		}
		for (NdpTableEntry entry : ndpTable.getEntities()) {
			Host host2 = null;
			TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
			// Host host2 = DiscoveryEngine.getEngine().getPool().getHostByBridgeMac(entry.getDeviceId());
			if (topoPool != null) {
				host2 = topoPool.getHostByBridgeMac(entry.getDeviceId());
			}
			if ((host2 == null) || (host2.getId() == this.host1.getId())) {
				continue;
			}
			IfTableEntry ife2 = host2.getIfByDescr(entry.getPortName());
			if (ife2 == null)
				continue;
			String ifDescr = getIfInRemoteNdpTable(this.host1.getBridgeMac(), host2);
			IfTableEntry ife1 = this.host1.getIfByDescr(ifDescr);
			if (ife1 == null)
				continue;
			Link link = Link.createLink(this.host1, ife1, host2, ife2);
			link.setTag("ndp");
			// DiscoveryEngine.getEngine().getPool().addElement(link);
			topoPool.addElement(link);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getIfInRemoteNdpTable(String deviceId, Host remoteHost) {
		CompositeBag<NdpTableEntry> remoteNdpTable = (CompositeBag) remoteHost.getBag(NdpTableEntry.class);
		if ((deviceId == null) || (ImsUtil.isEmpty(remoteNdpTable))) {
			return null;
		}
		for (NdpTableEntry entry : remoteNdpTable.getEntities()) {
			if (deviceId.equals(entry.getDeviceId()))
				return entry.getPortName();
		}
		return null;
	}
}
