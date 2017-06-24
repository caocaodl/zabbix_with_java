package com.isoft.imon.topo.engine.discover.poller.host.cisco;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.bag.host.cisco.CdpTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.SnmpPoller;
import com.isoft.imon.topo.util.CommonUtil;

/**思科发现协议表轮询器
 * @author Administrator
 * @date 2014年8月7日 
 */
public final class CdpTablePoller extends SnmpPoller {
	private static final String[] OIDS = { "1.3.6.1.4.1.9.9.23.1.2.1.1.4",
			"1.3.6.1.4.1.9.9.23.1.2.1.1.7" };

	/* (non-Javadoc)
	 * 采集数据
	 * @see com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement, java.lang.Object)
	 */
	public CompositeBag<CdpTableEntry> collect(Host host, SnmpOperator snmp) {
		int version = CommonUtil.getSnmpVersionByHost(host);
		String[][] table = snmp.getTable(OIDS,version);
		if (table == null)
			return null;

		CompositeBag<CdpTableEntry> bag = new CompositeBag<CdpTableEntry>(host.getId(), getBagName());
		for (String[] item : table) {
			String remoteIp = cacheAddressToIp(item[0]);
			if (remoteIp == null) {
				continue;
			}
			CdpTableEntry entry = new CdpTableEntry();
			entry.setRemoteIpAddr(remoteIp);
			entry.setRemoteIfDescr(item[1]);
			if (!bag.contains(entry)) {
				bag.add(entry);
			}
		}
		return bag;
	}

	/**
	 * 存储地址转化为IP
	 * @param cacheAddr
	 * @return
	 */
	private static String cacheAddressToIp(String cacheAddr) {
		String[] segs = cacheAddr.split(":");
		if ((segs == null) || (segs.length != 4)) {
			return null;
		}

		//String ipAddr = null;
		StringBuffer ipAddr = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			int t = Integer.parseInt(segs[i], 16);
			if (i == 0) {
				//ipAddr = String.valueOf(t);
				ipAddr.append(String.valueOf(t));
			} else {
				//ipAddr = ipAddr + "." + t;
				ipAddr.append(".").append(String.valueOf(t));
			}
		}
		return ipAddr.toString();
	}
}
