package com.isoft.imon.topo.engine.discover.poller.host.huawei;

import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.PortTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.PortTablePoller;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 华为端口表轮询器
 * 
 * @author Administrator
 * @date 2014年8月7日
 */
public final class HuaweiPortTablePoller extends PortTablePoller {
	private static final HuaweiPollerHelper helper = new HuaweiPollerHelper();

	/*
	 * (non-Javadoc) 采集数据
	 * 
	 * @see
	 * com.isoft.engine.discover.poller.host.PortTablePoller#collect(com.isoft
	 * .engine.discover.element.Host,
	 * com.isoft.engine.discover.discovery.operator.SnmpOperator)
	 */
	public CompositeBag<PortTableEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		List<TableEvent> rows = snmp.createTable(HostConstants.PORT_OIDS, version);
		if (rows == null)
			return null;

		CompositeBag<PortTableEntry> table = new CompositeBag<PortTableEntry>(host.getId(), getBagName());
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if (vb == null)
				continue;
			PortTableEntry entry = new PortTableEntry();
			if (helper.getIfIndexToPort().contains(host.getModel())) {
				entry.setIfIndex(snmp.getValue(vb[1]));
				entry.setPort(entry.getIfIndex());
			} else if (helper.getPortToIfIndex().contains(host.getModel())) {
				entry.setPort(snmp.getValue(vb[0]));
				entry.setIfIndex(entry.getPort());
			} else {
				entry.setPort(snmp.getValue(vb[0]));
				entry.setIfIndex(snmp.getValue(vb[1]));
			}
			if (!table.contains(entry))
				table.add(entry);
		}
		return table;
	}
}
