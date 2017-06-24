package com.isoft.imon.topo.host.discovery;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.StpTableEntry;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class StpLinkWorker extends DiscoveryWorker {
	public static final int TYPE_BLOCKING = 12;
	public static final int TYPE_FORWARDING = 15;
	private Host host1;

	/**
	 * 构造方法
	 * 
	 * @param host1
	 */
	public StpLinkWorker(Host host1) {
		super(host1.getIpAddress());
		this.host1 = host1;
	}

	/**
	 * 重写父类的dowork方法
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doWork() {
		CompositeBag<StpTableEntry> stpTable1 = (CompositeBag) this.host1.getBag(StpTableEntry.class);
		if (stpTable1 == null) {
			return;
		}
		TopoPool topoPool = DiscoveryEngine.getEngine().getPool();
		if (topoPool == null) {
			return;
		}
		for (StpTableEntry entry1 : stpTable1.getEntities())
			// for (Host host2 : DiscoveryEngine.getEngine().getPool().getHosts()) {
			for (Host host2 : topoPool.getHosts()) {
				if (host2.getId() == this.host1.getId()) {
					continue;
				}
				CompositeBag stpTable2 = (CompositeBag) host2.getBag(StpTableEntry.class);
				if (ImsUtil.isEmpty(stpTable2)) {
					continue;
				}
				StpTableEntry entry2 = (StpTableEntry) stpTable2.get(entry1.getEntity());
				if (entry2 == null)
					continue;
				IfTableEntry ife1 = this.host1.getIfByPort(entry1.getPort());
				IfTableEntry ife2 = host2.getIfByPort(entry2.getPort());
				if ((ife1 == null) || (ife1.isVirtual()) || (ife2 == null) || (ife2.isVirtual())) {
					continue;
				}
				Link link = Link.createLink(this.host1, ife1, host2, ife2);
				if ((entry1.getState() == 5) && (entry2.getState() == 5)) {
					link.setTag("stp");
					// DiscoveryEngine.getEngine().getPool().addElement(link);
					topoPool.addElement(link);
				}
				if ((entry1.getState() == 2) || (entry2.getState() == 2)) {
					link.setTag("stp");
					link.setBackup(1);
					// DiscoveryEngine.getEngine().getPool().addElement(link);
					topoPool.addElement(link);
				}
			}
	}
}
