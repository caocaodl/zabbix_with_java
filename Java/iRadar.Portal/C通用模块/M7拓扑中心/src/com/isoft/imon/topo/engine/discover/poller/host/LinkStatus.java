package com.isoft.imon.topo.engine.discover.poller.host;

import com.isoft.imon.topo.engine.discover.bag.StatusBag;
import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.poller.NullConnectorPoller;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.element.ElementStatus;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 链路状态表
 * 
 * @author Administrator
 * @date 2014年8月7日
 */
public final class LinkStatus extends NullConnectorPoller<Link> {
	/*
	 * (non-Javadoc) 采集链路状态数据
	 * 
	 * @see
	 * com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement
	 * , java.lang.Object)
	 */
	public StatusBag collect(Link link, Object object) {
		// 在修改链路不管理状态下最近一个小时的可用性显示不出不管理状态的灰色图案时发现，由于此处链路在不管理状态下返回null，因此StatusBag包为空，那么在
		// bag_element_status表里面最近一个小时就不会有数据，所以显示不出其不管理状态的灰色图案，故将以下三行代码屏蔽，以后测试如果没有引起别的问题，可将此代码删除
		// if (link.getElementStatus() == ElementStatus.Unmanaged) {
		// return null;
		// }
		Host host1 = PollingPool.getPool().getHostByID(link.getStartId());
		Host host2 = PollingPool.getPool().getHostByID(link.getEndId());

		if ((host1 == null) || (host2 == null) || (host1.getElementStatus() == ElementStatus.Unknown)
				|| (host2.getElementStatus() == ElementStatus.Unknown)) {
			return null;
		}
		StatusBag bag = new StatusBag();
		bag.setElementId(link.getId());
		if ((host1.getElementStatus() == ElementStatus.Unmanaged) || (host2.getElementStatus() == ElementStatus.Unmanaged)) {
			bag.setElementStatus(ElementStatus.Unmanaged);
		} else if ((host1.getElementStatus() == ElementStatus.Down) || (host2.getElementStatus() == ElementStatus.Down)) {
			bag.setElementStatus(ElementStatus.Down);
		} else if ((host1.getElementStatus() == ElementStatus.Unknown) || (host2.getElementStatus() == ElementStatus.Unknown)) {
			bag.setElementStatus(ElementStatus.Unknown);
		} else {
			IfTable ifTable1 = (IfTable) host1.getBag(IfTableEntry.class);
			int status1 = IfTableEntry.OPER_STATUS_UNKNOWN;
			if (ifTable1 != null) {
				synchronized (ifTable1) {
					IfTableEntry ifTableEntry = ifTable1.getIfByIndex(link.getStartIfIndex());
					if (ifTableEntry != null) {
						if (ifTableEntry.getOperStatus() == IfTableEntry.OPER_STATUS_UP) {
							status1 = IfTableEntry.OPER_STATUS_UP;
						} else {
							status1 = IfTableEntry.OPER_STATUS_DOWN;
						}
					}
				}
			}

			IfTable ifTable2 = (IfTable) host2.getBag(IfTableEntry.class);
			int status2 = IfTableEntry.OPER_STATUS_UNKNOWN;
			if (ifTable2 != null) {
				synchronized (ifTable2) {
					IfTableEntry ifTableEntry = ifTable2.getIfByIndex(link.getEndIfIndex());
					if (ifTableEntry != null) {
						if (ifTableEntry.getOperStatus() == IfTableEntry.OPER_STATUS_UP) {
							status2 = IfTableEntry.OPER_STATUS_UP;
						} else {
							status2 = IfTableEntry.OPER_STATUS_DOWN;
						}
					}
				}
			}
			if ((status1 == IfTableEntry.OPER_STATUS_UP) && (status2 == IfTableEntry.OPER_STATUS_UP))
				bag.setElementStatus(ElementStatus.Available);
			else if ((status1 == IfTableEntry.OPER_STATUS_DOWN) || (status2 == IfTableEntry.OPER_STATUS_DOWN))
				bag.setElementStatus(ElementStatus.Down);
			else
				bag.setElementStatus(ElementStatus.Unknown);
		}
		link.setElementStatus(bag.getElementStatus());
		link.getCollectContext().setLastPollingTime(DateUtil.longToTime(this.lastTime));
		link.getCollectContext().setNextPollingTime(DateUtil.longToTime(this.nextTime));
		return bag;
	}
}
