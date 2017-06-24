package com.isoft.imon.topo.engine.discover.poller;

import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.bag.host.PortTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.poller.host.IfTablePoller;
import com.isoft.imon.topo.engine.discover.poller.host.PortTablePoller;
import com.isoft.imon.topo.engine.discover.poller.host.SnmpPoller;

/**
 * 虚拟局域网轮询器
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public abstract class VlanPoller extends SnmpPoller {

	/**
	 * 获取成员接口
	 * 
	 * @param host
	 * @param portList
	 * @return
	 */
	protected String getMemberIfs(Host host, String portList) {
		if ((portList == null) || (portList.indexOf(":") == -1)) {
			return "";
		}
		StringBuffer portStr = new StringBuffer(100);
		String[] segments = portList.split(":");
		for (int i = 0; i < segments.length; i++) {
			portStr.append(getBinaryString(segments[i]));
		}
		char[] chars = portStr.toString().toCharArray();
		portStr = new StringBuffer(100);
		for (int k = 0; k < chars.length; k++) {
			String port = String.valueOf(k + 1);
			if (chars[k] == '1') {
				IfTableEntry ife = host.getIfByPort(port);
				if (ife != null) {
					portStr.append(ife.getDescr()).append(",");
				}
			}
		}
		return portStr.toString();
	}

	/**
	 * 获取二进制字符串
	 * 
	 * @param hex
	 * @return
	 */
	private String getBinaryString(String hex) {
		String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
		if (binary.length() == 8)
			return binary;

		StringBuffer sb = new StringBuffer(8);
		for (int i = 0; i < 8 - binary.length(); i++) {
			sb.append("0");
		}
		sb.append(binary);

		return sb.toString();
	}

	/**
	 * 获取虚拟局域网接口
	 * 
	 * @param host
	 * @param vlanId
	 * @return
	 */
	protected IfTableEntry getVlanIfe(Host host, String vlanId) {
		IfTableEntry ife = host.getIfByDescr("Vlan-interface" + vlanId);
		if (ife == null) {
			ife = host.getIfByDescr("Vlan" + vlanId);
		}
		if (ife == null) {
			ife = host.getIfByDescr("Vlanif" + vlanId);
		}
		return ife;
	}

	/**
	 * 初始化
	 * 
	 * @param host
	 * @param snmp
	 */
	protected void init(Host host, SnmpOperator snmp) {
		if (host.getBag(PortTableEntry.class) == null) {
			PortTablePoller poller = new PortTablePoller();
			host.putBag(poller.collect(host, snmp));
		}
		if (host.getBag(IfTableEntry.class) == null) {
			IfTablePoller poller = new IfTablePoller();
			host.putBag(poller.collect(host, snmp));
		}
	}
}
