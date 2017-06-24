package com.isoft.imon.topo.engine.discover.poller.host;

import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.LinkSpeedBag;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.poller.NullConnectorPoller;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.element.ElementStatus;

/**链路流量
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class LinkSpeed extends NullConnectorPoller<Link> {
	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public LinkSpeedBag collect(Link link, Object object) {
		if (link.getElementStatus() == ElementStatus.Unmanaged) {
			return null;
		}
		Host host1 = PollingPool.getPool().getHostByID(link.getStartId());
		Host host2 = PollingPool.getPool().getHostByID(link.getEndId());
		if(host1 == null || host2 == null){
			return null;
		}
		if ((host1.getElementStatus() == ElementStatus.Down)
				|| (host2.getElementStatus() == ElementStatus.Down)) {
			return null;
		}
		IfTable ifTable1 = (IfTable) host1.getBag(IfTableEntry.class);
		IfTable ifTable2 = (IfTable) host2.getBag(IfTableEntry.class);
		if ((ifTable1 == null) || (ifTable2 == null)) {
			return null;
		}
        IfTableEntry ife = null;
        if (link.getTrafficIf() == 1) {
            ife = ifTable1.getIfByIndex(link.getStartIfIndex());
        } else
          ife = ifTable2.getIfByIndex(link.getEndIfIndex());
        if ((ife == null) || (ife.getOperStatus() != 1)) {
            return null;
        }
		LinkSpeedBag bag = new LinkSpeedBag();
		bag.setElementId(link.getId());
    if (link.getTrafficDirect() == 1) {
      bag.setOutSpeed(ife.getOutSpeed());
      bag.setOutPercentage(ife.getOutPercentage());
      bag.setInSpeed(ife.getInSpeed());
      bag.setInPercentage(ife.getInPercentage());
    } else {
      bag.setOutSpeed(ife.getInSpeed());
      bag.setOutPercentage(ife.getInPercentage());
      bag.setInSpeed(ife.getOutSpeed());
     bag.setInPercentage(ife.getOutPercentage());
    }
    bag.setFrameVolume(ife.getFrameVolume());
    bag.setBroadcastVolume(ife.getBroadcastVolume());
    bag.setDiscardRate(ife.getDiscardRate());
    bag.setErrorRate(ife.getErrorRate());
    return bag;
  }
}
