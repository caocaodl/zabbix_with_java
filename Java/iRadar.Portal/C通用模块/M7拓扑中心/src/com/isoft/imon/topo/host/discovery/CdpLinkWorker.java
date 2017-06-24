package com.isoft.imon.topo.host.discovery;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.CdpTableEntry;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class CdpLinkWorker extends DiscoveryWorker {
	private final Host host1;

	/**
	 * 构造方法
	 * 
	 * @param host1
	 */
	public CdpLinkWorker(Host host1) {
		super(host1.getIpAddress());
		this.host1 = host1;
	}

	/**
	 * 重写DiscoveryWorker的dowork方法来实现CdpLink的dowork方法
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doWork() {
		CompositeBag<CdpTableEntry> cdpTable = (CompositeBag) this.host1.getBag(CdpTableEntry.class);
		if (cdpTable == null) {
			return;
		}
		for (CdpTableEntry entry : cdpTable.getEntities()) {
			TopoPool pool = DiscoveryEngine.getEngine().getPool();
			if (pool == null) {
				continue;
			}
			Host host2 = pool.getHostByIP(entry.getRemoteIpAddr());
			if ((host2 == null) || (host2.getId() == this.host1.getId())) {
				continue;
			}
			IfTableEntry ife2 = host2.getIfByDescr(entry.getRemoteIfDescr());
			if (ife2 == null)
				continue;
			String ifDescr = getIfInRemoteCdpTable(this.host1.getIpAddress(), host2);
			IfTableEntry ife1 = this.host1.getIfByDescr(ifDescr);
			if (ife1 == null)
				continue;
			Link link = Link.createLink(this.host1, ife1, host2, ife2);
			link.setTag("cdp");
			pool.addElement(link);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getIfInRemoteCdpTable(String localIp, Host remoteHost) {
		CompositeBag<CdpTableEntry> remoteCdpTable = (CompositeBag) remoteHost.getBag(CdpTableEntry.class);
		if (remoteCdpTable == null) {
			return null;
		}
		if (!remoteCdpTable.isEmpty()) {
			for (CdpTableEntry entry : remoteCdpTable.getEntities()) {
				if (entry.getRemoteIpAddr().equals(localIp))
					return entry.getRemoteIfDescr();
			}
		}
		return null;
	}
}
