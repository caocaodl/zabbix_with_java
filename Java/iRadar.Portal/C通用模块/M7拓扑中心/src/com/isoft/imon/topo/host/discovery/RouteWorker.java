package com.isoft.imon.topo.host.discovery;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.ArpTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.RouteTableEntry;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.element.BcastDomain;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.engine.discover.poller.host.RouteTablePoller;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.context.TopoPool;

public final class RouteWorker {
	private final Host host;

	/**
	 * 构造方法
	 * 
	 * @param host
	 */
	public RouteWorker(Host host) {
		this.host = host;
	}

	@SuppressWarnings("unchecked")
	public List<NetElement> doWork() {
		CompositeBag<RouteTableEntry> table = (CompositeBag<RouteTableEntry>) this.host.getBag(RouteTableEntry.class);
		SnmpDiscovery ds = new SnmpDiscovery();
		SnmpCredence credence = (SnmpCredence) this.host.getCredence("SNMP");
		if (credence == null) {
			return null;
		}
		ds.setTarget(this.host.getIpAddress(), credence);
		RouteTablePoller poller = new RouteTablePoller();
		table = poller.collect(this.host, ds);
		return detectSubnet(table);
	}

	private List<NetElement> detectSubnet(CompositeBag<RouteTableEntry> routeTable) {
		if (ImsUtil.isEmpty(routeTable)) {
			return null;
		}
		TopoPool pool = PollingPool.getPool();
		
		List<NetElement> results = new ArrayList<NetElement>();
		BcastDomain domain = new BcastDomain(this.host.getId());
		for (RouteTableEntry item : routeTable.getEntities()) {
			if (!NetworkUtil.isNetAddress(item.getDest(), item.getMask())) {
				continue;
			}
			IfTableEntry ife = null;
			if (item.getIfIndex().equals("0")) {
				CompositeBag<ArpTableEntry> arpTable = (CompositeBag<ArpTableEntry>) this.host.getBag(ArpTableEntry.class);
				if (arpTable != null) {
					ArpTableEntry ate = (ArpTableEntry) arpTable.getEntry("ipAddress", item.getNextHop());
					if (ate != null)
						ife = this.host.getIfByIndex(ate.getIfIndex());
				}
			} else {
				ife = this.host.getIfByIndex(item.getIfIndex());
			}
			if (ife == null)
				continue;
			// Subnet subnet = pool.getSubnetByIP(item.getDest());
			Subnet subnet = null;
			if (pool != null) {
				subnet = pool.getSubnetByIP(item.getDest());
			}

			if (subnet == null) {
				int id = (int)NetworkUtil.ipToLong(item.getDest());
				subnet = new Subnet(item.getDest(), item.getMask());
				subnet.setId(id);
				subnet.setGateway(item.getNextHop());
				subnet.setAlias(subnet.getNetAddress());
				if (ife.isVirtual()) {
					/* 这里业务逻辑考虑不全，l2vpn、l3vpn不属于vlan子网，to do fix */
					subnet.setVlan(true);
				}

			} else if (!subnet.getNetMask().equals(item.getMask())) {
				long oldMask = NetworkUtil.ipToLong(subnet.getNetMask());
				long newMask = NetworkUtil.ipToLong(item.getMask());
				if (newMask < oldMask) {
					subnet.setNetMask(item.getMask());
					Logger.getLogger(getClass()).info(subnet.getNetAddress() + "'s mask modify:" + item.getMask());
				}
			}
//			if ((cfg != null) && (!cfg.isValidSubnet(subnet))) continue;
			if (pool != null && pool.addElement(subnet))
				results.add(subnet);
			if (item.getType() == RouteTableEntry.ipRouteType.direct)
				this.host.addSubnet(subnet);
			if (item.getProto() == RouteTableEntry.ipRouteProto.local)
				domain.addAddress(item.getDest());
		}
		if (!domain.isEmpty() && pool != null) {
			boolean ok = pool.addBroadcastDomain(domain);
			if (ok)
				results.add(domain);
		}
		return results;
	}
}
