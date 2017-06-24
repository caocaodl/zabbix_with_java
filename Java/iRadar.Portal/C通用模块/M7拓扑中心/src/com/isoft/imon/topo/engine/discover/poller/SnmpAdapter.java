package com.isoft.imon.topo.engine.discover.poller;

import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.platform.context.ProtocolAdapter;

/**
 * 
 * SNMP适配器
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public final class SnmpAdapter extends ProtocolAdapter<Host> {
	/*
	 * 创建连接 (non-Javadoc)
	 * 
	 * @see
	 * com.isoft.ims.platform.context.ProtocolAdapter#createConnector(com.isoft
	 * .engine.discover.NetElement)
	 */
	public void createConnector(Host host) {
		this.responseTime = -1;
		long startTime = System.currentTimeMillis();

		String sysoid = null;

		SnmpOperator snmp = new SnmpOperator();
		SnmpCredence credence = (SnmpCredence) host.getCredence("SNMP");
		if (credence != null) {
			snmp.setTarget(host.getIpAddress(), credence);
			sysoid = snmp.getOidValue(HostConstants.SYS_OID,
					credence.getVersion());
		}

		if (sysoid == null) {
			try {
				Thread.sleep(6000L);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			sysoid = snmp.getOidValue(HostConstants.SYS_OID,
					credence.getVersion());
		}
		if (sysoid != null) {
			this.connector = snmp;
			this.responseTime = (int) (System.currentTimeMillis() - startTime);
		}
	}

	/*
	 * 释放连接 (non-Javadoc)
	 * 
	 * @see com.isoft.ims.platform.context.ProtocolAdapter#freeConnector()
	 */
	public void freeConnector() {
		this.connector = null;
	}
}
