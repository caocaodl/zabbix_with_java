package com.isoft.imon.topo.engine.discover.sniffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.log4j.Logger;
import org.snmp4j.mp.SnmpConstants;

import com.isoft.imon.topo.engine.discover.Credence;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.NetElementModel;
import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.host.discovery.RouteWorker;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.context.TopoPool;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.imon.topo.util.NmsException;

/**
 * SNMP主机嗅探器
 * 
 * @author ldd 2014-2-19
 */
public class SnmpHostSniffer extends AbstractHostSniffer {
	Logger logger = Logger.getLogger(SnmpHostSniffer.class);

	/*
	 * 进行嗅探工作 
	 * 
	 * @see com.isoft.engine.discover.Sniffer#doSniff(java.lang.String,
	 * java.lang.String, com.isoft.engine.discover.Credence)
	 */
	public NetElement doSniff(Host host) throws NmsException {
		String ip = host.getIpAddress();
		String alias = host.getAlias(); 
		Credence cred = host.getCredence("SNMP");

		SnmpCredence snmpCred = (SnmpCredence) cred;

		TopoPool pool = PollingPool.getPool();
		
		SnmpDiscovery snmp = new SnmpDiscovery();
		snmp.setTarget(ip, snmpCred);
		int version = snmpCred.getVersion();
		String sysOid = snmp.getSysOid(version);
		if (pool == null || sysOid == null || sysOid.endsWith("0.0.0.0.0.0")) {
			return null;
		}

		String sysDescr = snmp.getSysDescr(version);
		NetElementModel em = snmp.identityDevice(sysOid, sysDescr, version);
		if (em == null || HostConstants.CATEGORY_UNKNOWN.equals(em.getCategory())) {
			logger.info(ip + ":" + sysOid + " is CATEGORY_UNKNOWN");
			return null;
		}
		if (HostConstants.CATEGORY_IGNORE.equals(em.getCategory())) {
			return null;
		}
		if(!HostConstants.CATEGORY.containsValue(em.getCategory())){
			return null;
		}
		String bridgeMac = snmp.getBridgeMac(version);
		String serialNum = snmp.getFirstSerialNum(version);
//		String macAddr = snmp.getMacByIp(ip, version);

		String sysName = snmp.getSysName(version);
		if (CommonUtil.isEmpty(sysName)) {
			sysName = ip;
		}

		host.setCategory(em.getCategory());
		host.setBridgeMac(bridgeMac);
		host.setSysOid(sysOid);
		host.setSysName(sysName);
		host.setSysDescr(sysDescr);
		host.setAlias(alias);
		host.setModel(em.getModel());
		host.setEnterprise(em.getEnterprise());
		host.setSymbol(em.getSymbol());
		if (CommonUtil.isEmpty(host.getAlias())) {
			host.setAlias(host.getSysName());
		}
		host.putCredence(snmpCred);
		host.createContext("SNMP");
		// 采集数据
		collectBags(host, snmp);

		if (host.getCategory().equals(HostConstants.CATEGORY_SERVER)) {
			String netmask = snmp.getNetMaskByIp(host.getIpAddress(), version);
			host.setNetmask(netmask);
		}
		List<NetElement> results = null;
		// 判断是不是一般的网络设备
		if (HostConstants.isGenericNetworkDevice(em.getCategory())) {
			host.setSerialNum(serialNum);
			RouteWorker worker = new RouteWorker(host);
			results = worker.doWork();
		}

		if (CommonUtil.isEmpty(results)) {
			for (NetElement ne : PollingPool.getPool().getElements()) {
				if (HostConstants.CATEGORY_SUBNET.equals(ne.getCategory())) {
					Subnet sn = (Subnet) ne;
					if (!sn.ipInScope(host.getIpAddress())) {
						continue;
					}
					host.addSubnet(sn);
				}
			}
		}
		pool.addElement(host);
		return host;
	}

	/**
	 * 采集数据包
	 * 
	 * @param host
	 * @param snmp
	 *            void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void collectBags(Host host, SnmpDiscovery snmp) {
		List<Poller> pollers = host.getCollectContext().getPollers();
		for (Poller poller : pollers) {
			try {
				host.putBag(poller.collect(host, snmp));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建link链路
	 * 
	 * @param link
	 * @return String
	 */
	public String buildLink(Link link) {
		boolean ok = PollingPool.getPool().addElement(link);
		if (!ok) {
			return "该链路已经存在.";
		}
//		persistElement(link);
//		link.createContext(getPollers(link.getId()), getPolicies(link.getId()));
//		Thread polling = new Thread(new PollingImmediately(link));
//		polling.start();
		return null;
	}

	/*
	 * 获取凭证类型 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Sniffer#getCredenceType()
	 */
	public String getCredenceType() {
		return SnmpCredence.TYPE;
	}
}
