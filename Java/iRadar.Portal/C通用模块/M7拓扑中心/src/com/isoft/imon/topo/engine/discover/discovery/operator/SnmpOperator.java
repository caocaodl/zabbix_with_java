package com.isoft.imon.topo.engine.discover.discovery.operator;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.host.util.ImsUtil;
import com.isoft.imon.topo.host.util.VarBind;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * SNMP执行器
 * 
 * @author Administrator
 * 
 * @date 2014年8月7日
 */
public class SnmpOperator implements PDUFactory {
	private static Logger LOGGER = Logger.getLogger(SnmpOperator.class);

	protected static Snmp snmp;
	protected CommunityTarget target;
	protected UserTarget userTarget;

	static {
		try {
			TransportMapping transport = new DefaultUdpTransportMapping(new UdpAddress(InetAddress.getByAddress(new byte[]{0x0, 0x0, 0x0, 0x0}), 0));
			snmp = new Snmp(transport);
			snmp.listen();
		} catch (IOException ioe) {
			LOGGER.warn(ioe.getMessage(), ioe);
		}
	}

	/**
	 * 加载 SNMP v3 USM User
	 * 
	 * @param credence
	 * @return void
	 */
	private void addUsmUser(SnmpCredence credence) {
		OctetString securityName = new OctetString(credence.getSecurityName());
		int securityLevel = credence.getSecuritylevel();
		int auth = credence.getAuthprotocol();
		int priv = credence.getPrivprotocol();
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		UsmUser user = null;
		OID authProtocol = null;
		OID privProtocol = null;
		switch (securityLevel) {
		case 0:
		case 1:
			user = new UsmUser(securityName, null, null, null, null);
			break;
		case 2:
			authProtocol = auth == 0 ? AuthMD5.ID : AuthSHA.ID;
			user = new UsmUser(securityName, authProtocol, new OctetString(credence.getAuthpassphrase()), null, null);
			break;
		case 3:
			authProtocol = auth == 0 ? AuthMD5.ID : AuthSHA.ID;
			privProtocol = priv == 0 ? PrivDES.ID : PrivAES128.ID;
			user = new UsmUser(securityName, authProtocol, new OctetString(credence.getAuthpassphrase()), privProtocol, new OctetString(
					credence.getPrivpassphrase()));
			break;
		default:
			break;
		}
		snmp.getUSM().addUser(securityName, user);
	}

	@Override
	public PDU createPDU(Target target) {
		PDU pdu = new PDU();
		pdu.setType(PDU.GET);
		return pdu;
	}

	/**
	 * 根据version获取 PDU
	 * 
	 * @param version
	 * @return PDU
	 */
	public PDU createPDU(int version) {
		PDU pdu = null;
		if (version != SnmpConstants.version3) {
			pdu = new PDU();
		} else {
			pdu = new ScopedPDU();
		}
		return pdu;
	}

	/**
	 * 设置目标
	 * 
	 * @param ipAddress
	 * @param credence
	 */
	public void setTarget(String ipAddress, SnmpCredence credence) {
		String addr = "udp:" + ipAddress + "/" + credence.getPort();
		if (credence.getVersion() != SnmpConstants.version3 && credence.getCommunity() != null) {
			this.target = new CommunityTarget();
			this.target.setAddress(GenericAddress.parse(addr));
			this.target.setCommunity(new OctetString(credence.getCommunity()));
			this.target.setVersion(credence.getVersion());
			this.target.setRetries(credence.getRetries());
			this.target.setTimeout(credence.getTimeout());
		} else if (credence.getVersion() == SnmpConstants.version3 && credence.getSecurityName() != null) {
			addUsmUser(credence);
			this.userTarget = new UserTarget();
			this.userTarget.setAddress(GenericAddress.parse(addr));
			this.userTarget.setSecurityLevel(credence.getSecuritylevel());
			this.userTarget.setSecurityName(new OctetString(credence.getSecurityName()));
			this.userTarget.setVersion(SnmpConstants.version3);
			this.userTarget.setTimeout(credence.getTimeout());
			this.userTarget.setRetries(credence.getRetries());
		}
	}

	/**
	 * 设置写目标
	 * 
	 * @param ipAddress
	 * @param credence
	 */
	public void setTargetForWrite(String ipAddress, SnmpCredence credence) {
		this.target = new CommunityTarget();
		String addr = "udp:" + ipAddress + "/" + credence.getPort();
		this.target.setAddress(GenericAddress.parse(addr));
		if (credence.getWriteCommunity() == null) {
			this.target.setCommunity(new OctetString(""));
		} else {
			this.target.setCommunity(new OctetString(credence.getWriteCommunity()));
		}
		this.target.setVersion(credence.getVersion());
		this.target.setRetries(credence.getRetries());
		this.target.setTimeout(credence.getTimeout());
	}

	/**
	 * 创建表
	 * 
	 * @param oids
	 * @param version
	 * @return
	 */
	public List<TableEvent> createTable(String[] oids, int version) {
		TableUtils utils = new TableUtils(snmp, this);
		OID[] columns = new OID[oids.length];
		for (int i = 0; i < oids.length; i++) {
			columns[i] = new OID(oids[i]);
		}
		List<TableEvent> list = new ArrayList<TableEvent>();
		if (version != SnmpConstants.version3) {
			list = utils.getTable(this.target, columns, null, null);
		} else {
			list = utils.getTable(this.userTarget, columns, null, null);
		}
		if (CommonUtil.isEmpty(list)) {
			return null;
		}
		if ((list.size() == 1) && (((TableEvent) list.get(0)).getColumns() == null)) {
			return null;
		}
		return list;
	}

	/**
	 * 获取OID整数值
	 * 
	 * @param oid
	 * @param version
	 * @return
	 */
	public int getOidIntValue(String oid, int version) {
		try {
			return Integer.parseInt(getOidValue(oid, version));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取OID值
	 * 
	 * @param oid
	 * @param version
	 * @return
	 */
	public String getOidValue(String oid, int version) {
		String result = null;
		ResponseEvent response = null;
		try {
			PDU pdu = createPDU(version);
			pdu.setType(PDU.GET);
			if (version != SnmpConstants.version3) {
				pdu.add(new VariableBinding(new OID(oid)));
				response = snmp.send(pdu, this.target);
			} else {
				// snmp v3 operator
				VariableBinding var = new VariableBinding(new OID(oid));
				pdu.add(var);
				response = snmp.send(pdu, this.userTarget);
			}
			if (response != null && response.getResponse() != null) {
				VariableBinding vb = (VariableBinding) response.getResponse().getVariableBindings().elementAt(0);
				result = CommonUtil.getRidQuote(vb.getVariable().toString());
			}
			if (ImsUtil.isNull(result)) {
				result = null;
			}
		} catch (Exception localException) {
			LOGGER.warn(localException.getMessage(), localException);
		}
		return result;
	}

	/**
	 * 获取OID值数组
	 * 
	 * @param oids
	 * @param version
	 * @return
	 */
	public String[] getOidValues(String[] oids, int version) {
		String[] results = (String[]) null;
		try {
			PDU pdu = createPDU(version);
			pdu.setType(PDU.GET);
			for (String oid : oids) {
				pdu.add(new VariableBinding(new OID(oid)));
			}
			ResponseEvent response = null;
			if (version != SnmpConstants.version3) {
				response = snmp.send(pdu, this.target);
			} else {
				response = snmp.send(pdu, this.userTarget);
			}
			int len = oids.length;
			results = new String[len];
			if (response.getResponse() != null) {
				@SuppressWarnings("rawtypes")
				Vector vbs = response.getResponse().getVariableBindings();
				for (int i = 0; i < len; i++) {
					VariableBinding vb = (VariableBinding) vbs.get(i);
					results[i] = CommonUtil.getRidQuote(vb.getVariable().toString());
				}
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return results;
	}

	/**
	 * 获取数据表数组
	 * 
	 * @param oids
	 * @param version
	 * @return
	 */
	public String[][] getTable(String[] oids, int version) {
		List<TableEvent> rows = createTable(oids, version);
		if (rows == null) {
			return null;
		}
		String[][] tableContent = new String[rows.size()][oids.length];
		for (int i = 0; i < rows.size(); i++) {
			TableEvent row = (TableEvent) rows.get(i);
			VariableBinding[] values = row.getColumns();
			if (values == null)
				continue;
			for (int j = 0; j < values.length; j++) {
				VariableBinding value = values[j];
				if (value != null)
					tableContent[i][j] = value.getVariable().toString();
			}
		}
		return tableContent;
	}

	/**
	 * 获取WALK表数组
	 * 
	 * @param oids
	 * @param version
	 * @return
	 */
	public String[][] getWalkTable(String[] oids, int version) {
		String startOid = oids[0] + ".";
		List<VariableBinding> values = new ArrayList<VariableBinding>();
		try {
			PDU pdu = createPDU(version);
			pdu.add(new VariableBinding(new OID(oids[0])));
			pdu.setType(PDU.GETNEXT);
			while (true) {
				ResponseEvent response = null;
				if (version != SnmpConstants.version3) {
					response = snmp.send(pdu, this.target);
				} else {
					response = snmp.send(pdu, this.userTarget);
				}
				if (response == null || response.getResponse() == null) {
					break;
				}
				Vector<?> vbs = response.getResponse().getVariableBindings();
				VariableBinding vb = (VariableBinding) vbs.elementAt(0);
				OID nextOid = vb.getOid();
				if (!nextOid.toString().startsWith(startOid))
					break;
				values.add(vb);
				pdu.clear();
				pdu.add(new VariableBinding(nextOid));
			}

			String[][] _table = getTable(oids, version);
			if ((values.isEmpty()) || (_table == null) || (values.size() != _table.length))
				return null;
			int m = _table[0].length;
			String[][] result = new String[values.size()][oids.length + 1];
			int i = 0;
			for (int n = values.size(); i < n; i++) {
				result[i][0] = ((VariableBinding) values.get(i)).getOid().toString().replace(startOid, "");
				for (int j = 0; j < m; j++) {
					result[i][(j + 1)] = _table[i][j];
				}
			}
			return result;
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取WALK表
	 * 
	 * @param walkOid
	 * @param version
	 * @return
	 */
	public List<VarBind> getWalkTable(String walkOid, int version) {
		List<VariableBinding> values = new ArrayList<VariableBinding>();
		try {
			PDU pdu = createPDU(version);
			pdu.add(new VariableBinding(new OID(walkOid)));
			pdu.setType(PDU.GETNEXT);
			VariableBinding vb;
			while (true) {
				ResponseEvent response = null;
				if (version != SnmpConstants.version3) {
					response = snmp.send(pdu, this.target);
				} else {
					response = snmp.send(pdu, this.userTarget);
				}
				if ((response == null) || (response.getResponse() == null)) {
					break;
				}
				Vector<?> vbs = response.getResponse().getVariableBindings();
				vb = (VariableBinding) vbs.elementAt(0);
				OID nextOid = vb.getOid();
				if (nextOid == null)
					break;
				if (!nextOid.toString().startsWith(walkOid))
					break;
				values.add(vb);
				pdu.clear();
				pdu.add(new VariableBinding(nextOid));
			}

			if (values.isEmpty())
				return null;

			List<VarBind> varbinds = new ArrayList<VarBind>(values.size());
			for (VariableBinding value : values) {
				VarBind varbind = new VarBind();
				varbind.setOid(value.getOid().toString().replace(walkOid + ".", ""));
				varbind.setValue(value.getVariable().toString());
				varbinds.add(varbind);
			}
			return varbinds;
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取系统时间描述
	 * 
	 * @param times
	 * @return
	 */
	public String getSysUpTimeDescr(int[] times) {
		if (times != null) {
			if (times[0] > 0) {
				return times[0] + "天" + times[1] + "小时" + times[2] + "分钟";
			}
			return times[1] + "小时" + times[2] + "分钟";
		}
		return "";
	}

	/**
	 * 获取系统UP时间
	 * 
	 * @param version
	 * @return
	 */
	public int[] getSysUpTime(int version) {
		String upTime = getOidValue(HostConstants.SYS_UP_TIME, version);
		if (upTime == null)
			return null;

		int[] times = new int[3];
		String[] _temp = upTime.split(",");
		if (_temp.length > 1) {
			times[0] = Integer.parseInt(_temp[0].split(" ")[0]);
			String[] _ms = _temp[1].split(":");
			times[1] = Integer.parseInt(_ms[0].trim());
			times[2] = Integer.parseInt(_ms[1].trim());
		} else {
			String[] _ms = upTime.split(":");
			times[0] = 0;
			times[1] = Integer.parseInt(_ms[0].trim());
			times[2] = Integer.parseInt(_ms[1].trim());
		}
		return times;
	}

	/**
	 * 获取整数值
	 * 
	 * @param vb
	 * @return
	 */
	public int getIntValue(VariableBinding vb) {
		try {
			if (vb != null) {
				return Integer.parseInt(vb.getVariable().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取长整型值
	 * 
	 * @param vb
	 * @return
	 */
	public long getLongValue(VariableBinding vb) {
		try {
			if (vb != null) {
				return Long.parseLong(vb.getVariable().toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 获取值
	 * 
	 * @param vb
	 * @return
	 */
	public String getValue(VariableBinding vb) {
		if ((vb == null) || (vb.getVariable() == null))
			return null;
		return CommonUtil.getRidQuote(vb.getVariable().toString());
	}

	/**
	 * 发送
	 * 
	 * @param values
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public ResponseEvent send(VariableBinding[] values, int version) throws IOException {
		PDU pdu = createPDU(version);
		for (int k = 0; k < values.length; k++) {
			pdu.add(values[k]);
		}
		pdu.setType(PDU.SET);
		if (version != SnmpConstants.version3) {
			return snmp.send(pdu, this.target);
		} else {
			return snmp.send(pdu, this.userTarget);
		}
	}

	/**
	 * 发送
	 * 
	 * @param value
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public ResponseEvent send(VariableBinding value, int version) throws IOException {
		PDU pdu = createPDU(version);
		pdu.add(value);
		pdu.setType(PDU.SET);
		if (version != SnmpConstants.version3) {
			return snmp.send(pdu, this.target);
		} else {
			return snmp.send(pdu, this.userTarget);
		}
	}

	/*
	 * 创建PDU (non-Javadoc)
	 * 
	 * @see
	 * org.snmp4j.util.PDUFactory#createPDU(org.snmp4j.mp.MessageProcessingModel
	 * )
	 */
	public PDU createPDU(MessageProcessingModel messageProcessingModel) {
		return null;
	}
}
