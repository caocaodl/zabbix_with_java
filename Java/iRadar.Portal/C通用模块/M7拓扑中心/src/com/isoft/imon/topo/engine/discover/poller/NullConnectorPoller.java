package com.isoft.imon.topo.engine.discover.poller;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.platform.context.ProtocolAdapter;

/**
 * 空连接轮询器
 * 
 * @author Administrator
 * 
 * @param <TE>
 * @date 2014年8月7日
 */
public abstract class NullConnectorPoller<TE extends NetElement> extends Poller<TE, Object> {

	/*
	 * 采取设备的null轮询值 (non-Javadoc)
	 * 
	 * @see
	 * com.isoft.engine.discover.Poller#polling(com.isoft.engine.discover.NetElement
	 * )
	 */
	public synchronized Bag polling(TE element) {
		try {
			return collect(element, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 获取适配器类 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Poller#getAdapterClazz()
	 */
	@SuppressWarnings("rawtypes")
	protected Class<? extends ProtocolAdapter> getAdapterClazz() {
		return null;
	}
}
