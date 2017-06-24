package com.isoft.imon.topo.engine.discover.discovery;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.NetElementModel;
import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.platform.context.ContextResourcesPool;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * SNMP发现类
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public final class SnmpDiscovery extends SnmpOperator {

	public static final int COLON_SIZE = 5;

	/**
	 * 验证设备
	 * 
	 * @param sysOid
	 * @param sysDescr
	 * @param version
	 * @return
	 */
	public NetElementModel identityDevice(String sysOid, String sysDescr, int version) {
		NetElementModel devm = identifyDeviceModel(version);
		if (devm != null) {
			if (devm.getSymbol() == null) {
				NetElementModel refEm = ContextResourcesPool.getPool().getNetElementModel(sysOid, sysDescr);
				if (refEm != null)
					devm.setSymbol(refEm.getSymbol());
			}
		}

		NetElementModel em = ContextResourcesPool.getPool().getNetElementModel(sysOid, sysDescr);
		if (em == null || HostConstants.CATEGORY_IGNORE.equals(em.getCategory())) {
			return em;
		}
		if (em.getCategory() == null) {
			String category = identifyCategory(version);
			em.setCategory(category);
		}
		if (em.getModel() == null) {
			em.setModel("unknown");
		}
		if (em.getCategory() == null) {
			em.setCategory(HostConstants.CATEGORY_UNKNOWN);
		}

		/*
		 * HP的设备如果在elementModel.xml中注册,elementModel中注册的设备类型会覆盖modelIdentity.
		 * xml注册的设备类型
		 */
		// if (devm != null && em != null) {
		// return em;
		// } else if (devm != null) {
		// return devm;
		// }
		return em;
	}

	/**
	 * 验证类别
	 * 
	 * @param version
	 * @return
	 */
	public String identifyCategory(int version) {
		try {
			String temp;
			int ifNum = 0;
			temp = getOidValue(HostConstants.IF_NUMBER, version);
			if (temp != null)
				ifNum = new Integer(temp).intValue();
			if (ifNum < 2)
				return null;

			int isSwitch = 0;
			int ipForward = 0;
			String bridgeMac = getOidValue(HostConstants.BRIDGE_MAC, version);
			if ((bridgeMac == null) || (HostConstants.INVALID_MAC.equals(bridgeMac))) {
				temp = getOidValue(HostConstants.BRIDGE_NUM_PORTS, version);
				if (temp != null)
					isSwitch = new Integer(temp).intValue();
			} else {
				isSwitch = 1;
			}
			temp = getOidValue(HostConstants.IP_FORWORDING, version);
			if (temp != null)
				ipForward = new Integer(temp).intValue();
			if ((ipForward == 1) && (isSwitch == 0))
				return HostConstants.CATEGORY_ROUTER;
			if ((ipForward == 1) && (isSwitch > 0))
				return HostConstants.CATEGORY_ROUTE_SWITCH;
			if ((ipForward != 1) && (isSwitch > 0))
				return HostConstants.CATEGORY_SWITCH;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 验证设备模型
	 * 
	 * @param version
	 * @return
	 */
	public NetElementModel identifyDeviceModel(int version) {
		Map<String, NetElementModel> modelOids = ContextResourcesPool.getPool().getModelOids();
		for (String oid : modelOids.keySet()) {
			String model = getOidValue(oid, version);
			if (model != null) {
				if ((model.indexOf("IBM") >= 0) || (model.indexOf("System") >= 0))
					model = ImsUtil.getIBMModel(model);
				NetElementModel em = (NetElementModel) CommonUtil.copyBean(NetElementModel.class, modelOids.get(oid));
				if (em != null) {
					em.setModel(model);
				}
				if (this.target != null) {
					Logger.getLogger(SnmpDiscovery.class).info(this.target.getAddress() + ":确定设备型号=" + model);
				} else {
					Logger.getLogger(SnmpDiscovery.class).info(this.userTarget.getAddress() + ":确定设备型号=" + model);
				}

				return em;
			}
		}
		return null;
	}

	/**
	 * 获取系统OID
	 * 
	 * @param version
	 * @return
	 */
	public String getSysOid(int version) {
		return getOidValue(HostConstants.SYS_OID, version);
	}

	/**
	 * 获取系统名
	 * 
	 * @param version
	 * @return
	 */
	public String getSysName(int version) {
		String sysname = getOidValue(HostConstants.SYS_NAME, version);
		if (ImsUtil.isNull(sysname))
			return this.target.getAddress().toString();
		return sysname;
	}

	/**
	 * 获取系统描述
	 * 
	 * @param version
	 * @return
	 */
	public String getSysDescr(int version) {
		String descr = getOidValue(HostConstants.SYS_DESCR, version);
		if (ImsUtil.isNull(descr))
			return "";
		if (descr.split(":").length > COLON_SIZE)
			return getChinese(descr);
		return descr;
	}

	/**
	 * 获取桥连接MAC地址
	 * 
	 * @param version
	 * @return
	 */
	public String getBridgeMac(int version) {
		String mac = getOidValue(HostConstants.BRIDGE_MAC, version);
		if ((ImsUtil.isNull(mac)) || (HostConstants.INVALID_MAC.equals(mac)))
			return null;
		return mac;
	}

	/**
	 * 获取第一个序列号
	 * 
	 * @param version
	 * @return
	 */
	public String getFirstSerialNum(int version) {
		List<TableEvent> rows = createTable(HostConstants.ENTITY_SERIAL_NUM, version);
		if (!CommonUtil.isEmpty(rows)) {
			for (TableEvent row : rows) {
				VariableBinding[] vbs = row.getColumns();
				if (vbs == null)
					continue;
				String num = vbs[0].getVariable().toString();
				if (!CommonUtil.isEmpty(num))
					return num;
			}
		}
		return null;
	}

	/**
	 * 获取思科IOS
	 * 
	 * @param version
	 * @return
	 */
	public String getCiscoIOS(int version) {
		String temp = getOidValue(HostConstants.CISCO_IMAGE_VERSION, version);
		if (temp != null) {
			String[] temps = temp.split("\\$");
			if (temps.length > 1)
				return temps[1];
		}
		return temp;
	}

	/**
	 * 将二进制字符串转换为中文
	 * 
	 * @param octetString
	 * @return
	 */
	public static String getChinese(String octetString) {
		return getChinese(octetString, "GB2312");
	}

	/**
	 * 根据编码方式将二进制字符串转换为中文
	 * 
	 * @param octetString
	 * @param charsetName
	 * @return
	 */
	public static String getChinese(String octetString, String charsetName) {
		if (ImsUtil.isNull(octetString))
			return "";
		try {
			String[] temps = octetString.split(":");
			if (temps.length < COLON_SIZE) {
				return octetString;
			}
			byte[] bs = new byte[temps.length];
			for (int i = 0; i < temps.length; i++) {
				bs[i] = (byte) Integer.parseInt(temps[i], 16);
			}

			return new String(bs, charsetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过IP获取网关
	 * 
	 * @param ipAddr
	 * @param version
	 * @return
	 */
	public String getNetMaskByIp(String ipAddr, int version) {
		String netmask = null;
		List<TableEvent> ipRows = createTable(HostConstants.IP_OIDS, version);
		if (!CommonUtil.isEmpty(ipRows)) {
			for (TableEvent row : ipRows) {
				VariableBinding[] vbs = row.getColumns();
				if (vbs == null)
					continue;

				if (vbs[0].getVariable().toString().equals(ipAddr)) {
					netmask = vbs[2].getVariable().toString();
					break;
				}
			}
		}
		return netmask;
	}

	/**
	 * 通过IP地址获取MAC地址
	 * 
	 * @param ipAddr
	 * @param version
	 * @return
	 */
	public String getMacByIp(String ipAddr, int version) {
		String mac = null;
		List<TableEvent> rows = null;
		rows = createTable(HostConstants.IF_DISCOVERY_OIDS, version);
		if (rows == null) {
			return null;
		}

		IfTable ifTable = new IfTable();
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if ((vb == null) || (vb[0] == null))
				continue;
			IfTableEntry entry = new IfTableEntry();
			entry.setIndex(getValue(vb[0]));
			entry.setMac(getValue(vb[4]));
			if ((entry.getMac() != null) && (entry.getMac().length() != 17)) {
				entry.setMac(null);
			}
			ifTable.add(entry);
		}

		List<TableEvent> ipRows = createTable(HostConstants.IP_OIDS, version);
		if (ipRows != null) {
			for (TableEvent row : ipRows) {
				VariableBinding[] vb = row.getColumns();
				if (vb == null)
					continue;
				IfTableEntry ife = ifTable.getIfByIndex(getValue(vb[1]));
				if (ife != null) {
					// k9扫描，清楚空指针异常
					String ipAddress = getValue(vb[0]);
					if (ipAddress == null) {
						continue;
					}
					if (ipAddress.equals(ipAddr)) {
						mac = ife.getMac();
						break;
					}
				}
			}
		}
		return mac;
	}
}
