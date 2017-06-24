package com.isoft.imon.topo.engine.discover.poller.host;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.engine.discover.bag.StatusBag;
import com.isoft.imon.topo.host.util.NetworkUtil;
import com.isoft.imon.topo.platform.context.ProtocolAdapter;
import com.isoft.imon.topo.platform.element.ElementStatus;
import com.isoft.imon.topo.util.DateUtil;

/**
 * PING模式的轮询器
 * @author ldd
 * 2014-2-17
 */
public final class PingPoller extends Poller<NetElement, Object> {

   /*
    * 进行PING模式的网元轮询
    * (non-Javadoc)
    * @see com.isoft.engine.discover.Poller#polling(com.isoft.engine.discover.NetElement)
    */
	public synchronized Bag polling(NetElement host) {
		StatusBag bag = new StatusBag();
		bag.setElementId(host.getId());
		if (host.getElementStatus() == ElementStatus.Unmanaged) {
			bag.setElementStatus(ElementStatus.Unmanaged);
			bag.setResponseTime(-1);
			//设置不管理状态设备的上次轮询时间和下次轮询时间
			host.getCollectContext().setLastPollingTime(DateUtil.longToTime(this.lastTime));
		} else {
			Integer responseTime = NetworkUtil.jnaPing(host.getIpAddress());			
			boolean reachable = responseTime != null;
			if (reachable) {
				bag.setElementStatus(ElementStatus.Available);
				bag.setResponseTime(responseTime);
			} else {
				bag.setElementStatus(ElementStatus.Down);
				bag.setResponseTime(-1);
			}
			host.setElementStatus(bag.getElementStatus());
			host.getCollectContext().setLastPollingTime(DateUtil.longToTime(this.lastTime));
			host.getCollectContext().setNextPollingTime(DateUtil.longToTime(this.nextTime));
		}
		return bag;
	}

	/*
	 * (non-Javadoc)
	 * 采集数据，返回Null
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public StatusBag collect(NetElement host, Object obj) {
		return null;
	}
   
	/*
	 * 获取协议适配器
	 * (non-Javadoc)
	 * @see com.isoft.engine.discover.Poller#getAdapterClazz()
	 */
	@SuppressWarnings({ "rawtypes" })
	protected Class<? extends ProtocolAdapter> getAdapterClazz() {
		return null;
	}
}
