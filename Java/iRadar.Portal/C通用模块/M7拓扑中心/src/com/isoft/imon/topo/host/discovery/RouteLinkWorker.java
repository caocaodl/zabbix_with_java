package com.isoft.imon.topo.host.discovery;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.ArpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.RouteTableEntry;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class RouteLinkWorker extends DiscoveryWorker {
	private final Host host1;

	/**
	 * 构造方法
	 * 
	 * @param host1
	 */
	public RouteLinkWorker(Host host1) {
		super(host1.getIpAddress());
		this.host1 = host1;
	}

	/**
	 * 重写父类的dowork方法
	 */
	@SuppressWarnings("unchecked")
	public void doWork() {
		CompositeBag<RouteTableEntry> routeTable = (CompositeBag<RouteTableEntry>) this.host1.getBag(RouteTableEntry.class);
		if (routeTable == null) {
			return;
		}
		for (RouteTableEntry item : routeTable.getEntities())
			if (item.getType() == 4) {
				/* type 4 means indirect */
				Host host2 = null;
				TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
				// Host host2 = DiscoveryEngine.getEngine().getPool().getHostByIP(item.getNextHop());
				if (topoPool != null) {
					host2 = topoPool.getHostByIP(item.getNextHop());
				}
				if ((host2 == null) || (host2.getId() == this.host1.getId())) {
					continue;
				}
				IfTableEntry ife1 = null;
				if (item.getIfIndex().equals("0")) {
					CompositeBag<?> arpTable = (CompositeBag<?>) this.host1.getBag(ArpTableEntry.class);
					if (arpTable == null)
						continue;
					ArpTableEntry ate = (ArpTableEntry) arpTable.getEntry("ipAddress", item.getNextHop());
					if (ate == null)
						continue;
					ife1 = this.host1.getIfByIndex(ate.getIfIndex());
				} else {
					ife1 = this.host1.getIfByIndex(item.getIfIndex());
				}
				IfTableEntry ife2 = host2.getIfByIP(item.getNextHop());
				if ((ife1 == null) || (ife2 == null) || (ife1.isVirtual()) || (ife2.isVirtual()))
					continue;
				Link link = Link.createLink(this.host1, ife1, host2, ife2);
				link.setTag("route");
				// DiscoveryEngine.getEngine().getPool().addElement(link);
				topoPool.addElement(link);
			}
	}
}
