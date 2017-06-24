package com.isoft.imon.topo.engine.discover.poller.host.huawei;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.huawei.NdpTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.SnmpPoller;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 华为ndp表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class HuaweiNdpTablePoller extends SnmpPoller {
	private static final String[] OIDS = { "1.3.6.1.4.1.2011.6.7.5.6.1.1",
			"1.3.6.1.4.1.2011.6.7.5.6.1.2" };

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public CompositeBag<NdpTableEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		String[][] table = snmp.getTable(OIDS,version);
		if (table == null)
			return null;

		CompositeBag<NdpTableEntry> bag = new CompositeBag<NdpTableEntry>(host.getId(), getBagName());
		for (String[] item : table) {
			NdpTableEntry entry = new NdpTableEntry();
			entry.setDeviceId(item[0]);
			entry.setPortName(item[1]);
			if (!bag.contains(entry))
				bag.add(entry);
		}
		return bag;
	}
}
