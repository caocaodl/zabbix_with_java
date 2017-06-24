package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DISCOVER;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DOWN;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_LOST;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_UP;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.SVC_AGENT;
import static com.isoft.iradar.inc.Defines.SVC_FTP;
import static com.isoft.iradar.inc.Defines.SVC_HTTP;
import static com.isoft.iradar.inc.Defines.SVC_HTTPS;
import static com.isoft.iradar.inc.Defines.SVC_ICMPPING;
import static com.isoft.iradar.inc.Defines.SVC_IMAP;
import static com.isoft.iradar.inc.Defines.SVC_LDAP;
import static com.isoft.iradar.inc.Defines.SVC_NNTP;
import static com.isoft.iradar.inc.Defines.SVC_POP;
import static com.isoft.iradar.inc.Defines.SVC_SMTP;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv1;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv2c;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv3;
import static com.isoft.iradar.inc.Defines.SVC_SSH;
import static com.isoft.iradar.inc.Defines.SVC_TCP;
import static com.isoft.iradar.inc.Defines.SVC_TELNET;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;

@CodeConfirmed("benne.2.2.6")
public class DiscoveryUtil {
	
	private DiscoveryUtil() {
	}
	
	/**
	 * Returns true if the user has the permissions to network discovery.
	 *
	 * @return bool
	 */
	public static boolean check_right_on_discovery() {
		return (CWebUser.getType() >= USER_TYPE_IRADAR_ADMIN);
	}
	
	public static int svc_default_port(int type){
		CArray<Integer> typePort = map(
				SVC_SSH,		    22,
				SVC_LDAP,		389,
				SVC_SMTP,		25,
				SVC_FTP,		    21,
				SVC_HTTP,		80,
				SVC_POP,		    110,
				SVC_NNTP,		119,
				SVC_IMAP,		143,
				SVC_AGENT,	    10050,
				SVC_SNMPv1,	161,
				SVC_SNMPv2c,161,
				SVC_SNMPv3,	 161,
				SVC_HTTPS,	     443,
				SVC_TELNET,	 23
			);
		if (typePort.containsKey(type)) {
			return typePort.get(type);
		} else {
			return 0;
		}
	}
	
	public static CArray<String> discovery_check_type2str() {
		CArray<String> discovery_types = map(
				SVC_SSH, _("SSH"),
				SVC_LDAP, _("LDAP"),
				SVC_SMTP, _("SMTP"),
				SVC_FTP, _("FTP"),
				SVC_HTTP, _("HTTP"),
				SVC_POP, _("POP"),
				SVC_NNTP, _("NNTP"),
				SVC_IMAP, _("IMAP"),
				SVC_TCP, _("TCP"),
				SVC_AGENT, _("iRadar agent"),
				SVC_SNMPv1, _("SNMPv1 agent"),
				SVC_SNMPv2c, _("SNMPv2 agent"),
				SVC_SNMPv3, _("SNMPv3 agent"),
				SVC_ICMPPING, _("ICMP ping"),
				SVC_TELNET, _("Telnet"),
				SVC_HTTPS, _("HTTPS")
			);
		order_result(discovery_types);
		return discovery_types;
	}
	
	public static String discovery_check_type2str(int type) {
		CArray<String> discovery_types = map(
				SVC_SSH, _("SSH"),
				SVC_LDAP, _("LDAP"),
				SVC_SMTP, _("SMTP"),
				SVC_FTP, _("FTP"),
				SVC_HTTP, _("HTTP"),
				SVC_POP, _("POP"),
				SVC_NNTP, _("NNTP"),
				SVC_IMAP, _("IMAP"),
				SVC_TCP, _("TCP"),
				SVC_AGENT, _("iRadar agent"),
				SVC_SNMPv1, _("SNMPv1 agent"),
				SVC_SNMPv2c, _("SNMPv2 agent"),
				SVC_SNMPv3, _("SNMPv3 agent"),
				SVC_ICMPPING, _("ICMP ping"),
				SVC_TELNET, _("Telnet"),
				SVC_HTTPS, _("HTTPS")
			);
		return discovery_types.get(type);
	}
	
	public static String discovery_check2str(int type, String key, String port){
		String external_param = "";
		if (key != null && key.length()>0) {
			switch (type) {
			case SVC_SNMPv1:
			case SVC_SNMPv2c:
			case SVC_SNMPv3:
			case SVC_AGENT:
				external_param = " \""+key+"\"";
				break;
			}
		}
		String result = discovery_check_type2str(type);
		if(empty(port) && !String.valueOf(svc_default_port(type)).equals(port) || SVC_TCP == type){
			result += " (" + port + ")";
		}
		result += external_param;
		return result;
	}
	
	public static String discovery_port2str(int type, int port) {
		int port_def = svc_default_port(type);
		if (port != port_def) {
			return " (" + port + ")";
		}
		return "";
	}
	
	public static CArray<String> discovery_status2str() {
		CArray<String> discoveryStatus = map(
				DRULE_STATUS_ACTIVE, _("Enabled"),
				DRULE_STATUS_DISABLED, _("Disabled"));
		return discoveryStatus;
	}
	
	public static String discovery_status2str(int status) {
		CArray<String> discoveryStatus = map(
				DRULE_STATUS_ACTIVE, _("Enabled"),
				DRULE_STATUS_DISABLED, _("Disabled"));
		if (discoveryStatus.containsKey(status)) {
			return discoveryStatus.get(status);
		} else {
			return _("Unknown");
		}
	}
	
	public static String discovery_status2style(int status) {
		switch (status) {
		case DRULE_STATUS_ACTIVE:
			return "off";
		case DRULE_STATUS_DISABLED:
			return "on";
		default:
			return "unknown";
		}
	}
	
	public static CArray<String> discovery_object_status2str() {
		CArray<String> discoveryStatus = map(
				DOBJECT_STATUS_UP, _x("Up", "discovery status"),
				DOBJECT_STATUS_DOWN, _x("Down", "discovery status"),
				DOBJECT_STATUS_DISCOVER, _("Discovered"),
				DOBJECT_STATUS_LOST, _("Lost")
			);
		FuncsUtil.order_result(discoveryStatus);
		return discoveryStatus;
	}
	
	public static String discovery_object_status2str(int status) {
		CArray<String> discoveryStatus = map(
				DOBJECT_STATUS_UP, _x("Up", "discovery status"),
				DOBJECT_STATUS_DOWN, _x("Down", "discovery status"),
				DOBJECT_STATUS_DISCOVER, _("Discovered"),
				DOBJECT_STATUS_LOST, _("Lost")
			);
		if (discoveryStatus.containsKey(status)) {
			return discoveryStatus.get(status);
		} else {
			return _("Unknown");
		}
	}
	
	public static Map get_discovery_rule_by_druleid(IIdentityBean idBean, SQLExecutor executor, String druleid) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT d.* FROM drules d WHERE "+ sqlParts.dual.dbConditionTenants(idBean, "drules", "d")+
				" AND d.druleid="+sqlParts.marshalParam(druleid);
		return DBfetch(DBselect(executor, sql, 1, sqlParts.getNamedParams()));
	}
	
}
