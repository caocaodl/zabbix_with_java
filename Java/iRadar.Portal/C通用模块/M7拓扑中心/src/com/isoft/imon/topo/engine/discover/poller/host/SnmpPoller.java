package com.isoft.imon.topo.engine.discover.poller.host;

import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.SnmpAdapter;
import com.isoft.imon.topo.platform.context.ProtocolAdapter;

public abstract class SnmpPoller extends Poller<Host, SnmpOperator> {
	
	/*获取snmp的协议适配器
	 * (non-Javadoc)
	 * @see com.isoft.engine.discover.Poller#getAdapterClazz()
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends ProtocolAdapter> getAdapterClazz() {
		return SnmpAdapter.class;
	}
}
