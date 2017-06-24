package com.isoft.imon.topo.host.util;

import java.util.HashMap;
import java.util.Map;

import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.platform.element.Category;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.types.CArray;

/**
 * 一些操作的公用方法
 * 
 * @author soft
 * 
 */
public final class HostConstants {

	public static final long UPDATE_INTERVAL = 600000L;
	public static final String SYS_OID = "1.3.6.1.2.1.1.2.0";
	public static final String SYS_UP_TIME = "1.3.6.1.2.1.1.3.0";
	public static final String SYS_DESCR = "1.3.6.1.2.1.1.1.0";
	public static final String SYS_NAME = "1.3.6.1.2.1.1.5.0";
	public static final String BRIDGE_MAC = "1.3.6.1.2.1.17.1.1.0";
	public static final String BRIDGE_NUM_PORTS = "1.3.6.1.2.1.17.1.2.0";
	public static final String IP_FORWORDING = "1.3.6.1.2.1.4.1.0";
	public static final String IF_NUMBER = "1.3.6.1.2.1.2.1.0";
	public static final String PRINTER_OID = "1.3.6.1.2.1.43.5.1.1.1.1";
	public static final String CISCO_IMAGE_VERSION = "1.3.6.1.4.1.9.9.25.1.1.1.2.5";
	public static final String INVALID_MAC = "00:00:00:00:00:00";
	public static final String[] SNMP_SERVER_STORAGE = {
			"1.3.6.1.2.1.25.2.3.1.2",
			"1.3.6.1.2.1.25.2.3.1.3",
			"1.3.6.1.2.1.25.2.3.1.4",
			"1.3.6.1.2.1.25.2.3.1.5",
			"1.3.6.1.2.1.25.2.3.1.6"
	};

	public static final String[] ENTITY_SERIAL_NUM = {
		"1.3.6.1.2.1.47.1.1.1.1.11"
	};

	public static final String[] IP_OIDS = {
			"1.3.6.1.2.1.4.20.1.1",
			"1.3.6.1.2.1.4.20.1.2",
			"1.3.6.1.2.1.4.20.1.3"
	};

	public static final String[] IF_DISCOVERY_OIDS = {
			"1.3.6.1.2.1.2.2.1.1",
			"1.3.6.1.2.1.2.2.1.2",
			"1.3.6.1.2.1.2.2.1.3",
			"1.3.6.1.2.1.2.2.1.5",
			"1.3.6.1.2.1.2.2.1.6",
			"1.3.6.1.2.1.2.2.1.7",
			"1.3.6.1.2.1.2.2.1.8"
	};

	public static final String[] IF_OIDS = {
			"1.3.6.1.2.1.2.2.1.1",
			"1.3.6.1.2.1.2.2.1.2",
			"1.3.6.1.2.1.2.2.1.3",
			"1.3.6.1.2.1.2.2.1.5",
			"1.3.6.1.2.1.2.2.1.6",
			"1.3.6.1.2.1.2.2.1.7",
			"1.3.6.1.2.1.2.2.1.8",
			"1.3.6.1.2.1.2.2.1.10",
			"1.3.6.1.2.1.2.2.1.16",
			"1.3.6.1.2.1.2.2.1.14",
			"1.3.6.1.2.1.2.2.1.20",
			"1.3.6.1.2.1.2.2.1.13",
			"1.3.6.1.2.1.2.2.1.19",
			"1.3.6.1.2.1.2.2.1.11",
			"1.3.6.1.2.1.2.2.1.17",
			"1.3.6.1.2.1.2.2.1.12",
			"1.3.6.1.2.1.2.2.1.18"
	};

	/* ARP_OIDS is equal to IpNetToMediaTable */
	public static final String[] ARP_OIDS = {
			"1.3.6.1.2.1.4.22.1.1",
			"1.3.6.1.2.1.4.22.1.2",
			"1.3.6.1.2.1.4.22.1.3",
			"1.3.6.1.2.1.4.22.1.4"
	};

	public static final String[] PORT_OIDS = {
			"1.3.6.1.2.1.17.1.4.1.1",
			"1.3.6.1.2.1.17.1.4.1.2"
	};

	public static final String[] Q_BRIDGE_VLAN = {
			"1.3.6.1.2.1.17.7.1.4.2.1.3",
			"1.3.6.1.2.1.17.7.1.4.2.1.5",
			"1.3.6.1.2.1.17.7.1.4.2.1.6"
	};

	public static final String[] HUAWEI_LSW_VLAN = {
			"1.3.6.1.4.1.2011.2.23.1.2.1.1.1.2",
			"1.3.6.1.4.1.2011.2.23.1.2.1.1.1.3",
			"1.3.6.1.4.1.2011.2.23.1.2.1.1.1.4",
			"1.3.6.1.4.1.2011.2.23.1.2.1.1.1.10"
	};

	public static final String[] H3C_RFC1155_VLAN = {
			"1.3.6.1.4.1.25506.8.35.2.1.1.1.1",
			"1.3.6.1.4.1.25506.8.35.2.1.1.1.2",
			"1.3.6.1.4.1.25506.8.35.2.1.1.1.3",
			"1.3.6.1.4.1.25506.8.35.2.1.1.1.4",
			"1.3.6.1.4.1.25506.8.35.2.1.1.1.10"
	};

	public static final String[] HUAWEI_RFC1155_VLAN = {
			"1.3.6.1.4.1.2011.8.35.2.1.1.1.1",
			"1.3.6.1.4.1.2011.8.35.2.1.1.1.2",
			"1.3.6.1.4.1.2011.8.35.2.1.1.1.3",
			"1.3.6.1.4.1.2011.8.35.2.1.1.1.4",
			"1.3.6.1.4.1.2011.8.35.2.1.1.1.10"
	};

	public static final String[] VALN_MEMBER_OIDS = {
		"1.3.6.1.4.1.9.9.68.1.2.1.1.2"
	};

	public static final String[] VLAN_OIDS = {
			"1.3.6.1.4.1.9.9.46.1.3.1.1.2",
			"1.3.6.1.4.1.9.9.46.1.3.1.1.3",
			"1.3.6.1.4.1.9.9.46.1.3.1.1.4"
	};

	public static final String CATEGORY_UNKNOWN = "Unknown";
	public static final String CATEGORY_IGNORE = "Ignore";
	public static final String CATEGORY_ROUTER = "Router";
	public static final String CATEGORY_ROUTE_SWITCH = "RouteSwitch";
	public static final String CATEGORY_SWITCH = "Switch";
	public static final String CATEGORY_FIREWALL = "Firewall";
	public static final String CATEGORY_SERVER = "Server";
	public static final String CATEGORY_LINK = "Link";
	public static final String CATEGORY_SUBNET = "Subnet";
	public static final String CATEGORY_IP = "IP";
	public static final String CATEGORY_VM = "VM";
	public static final String CATEGORY_CLOUD = "CLOUD";
	public static final String CATEGORY_MYSQL = "MYSQL";
	public static final String CATEGORY_TOMCAT = "TOMCAT";
	public static final String CATEGORY_CABINET = "CABINET";
	public static final String CATEGORY_ROOM = "ROOM";
	public static final String CATEGORY_GROUP = "GROUP";
	public static final String CATEGORY_BIZNODE = "BIZNODE";
	public static final String CATEGORY_STORAGE = "STORAGE";

	public static final CArray CATEGORY = CArray.array(CATEGORY_UNKNOWN,CATEGORY_IGNORE,CATEGORY_ROUTER,CATEGORY_ROUTE_SWITCH,CATEGORY_SWITCH,
													   CATEGORY_FIREWALL,CATEGORY_SERVER,CATEGORY_LINK,CATEGORY_SUBNET,CATEGORY_IP,CATEGORY_VM,
													   CATEGORY_CLOUD,CATEGORY_MYSQL,CATEGORY_TOMCAT,CATEGORY_CABINET,CATEGORY_ROOM,CATEGORY_GROUP,
													   CATEGORY_BIZNODE,CATEGORY_STORAGE);
	
	public static final String STATUS_MANAGERED = "1";
	public static final String STATUS_UNMANAGER = "2";
	
	public static final String SUBNET_LOOPBACK = "127.0.0.0";
	
	public static CArray<Long> MON_NET_GROUP = CArray.array(IMonConsts.MON_NET_CISCO,IMonConsts.MON_COMMON_NET,IMonConsts.MON_NET_HUAWEI_SWITCH,IMonConsts.MON_NET_ZHONGXING_SWITCH);
	
	public static CArray<Long> MON_SERVER_GROUP = CArray.array(IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS);
	
	public static CArray<Long> MON_VM_GROUP = CArray.array(IMonConsts.MON_VM);
	
	public static CArray<Long> MON_MYSQL_GROUP = CArray.array(IMonConsts.MON_DB_MySQL);
	
	public static CArray<Long> MON_TOMCAT_GROUP = CArray.array(IMonConsts.MON_MIDDLE_TOMCAT);
	
	public static Map MON_DEVICE_HOSTTYPE = CArray.map(CATEGORY_ROUTER		,MON_NET_GROUP,
													   CATEGORY_ROUTE_SWITCH,MON_NET_GROUP,
													   CATEGORY_SWITCH		,MON_NET_GROUP,
													   CATEGORY_FIREWALL	,MON_NET_GROUP,
													   CATEGORY_SERVER		,MON_SERVER_GROUP,
													   CATEGORY_VM			,MON_VM_GROUP,
													   CATEGORY_MYSQL		,MON_MYSQL_GROUP,
													   CATEGORY_TOMCAT		,MON_TOMCAT_GROUP);

	@SuppressWarnings("serial")
	public static final Map<String, String> categoryMap = new HashMap<String, String>() {
		{
			put(CATEGORY_SERVER, "服务器");
			put(CATEGORY_SWITCH, "交换机");
			put(CATEGORY_ROUTE_SWITCH, "路由交换机");
			put(CATEGORY_ROUTER, "路由器");
			put(CATEGORY_FIREWALL, "防火墙");
		}
	};
	 
	public static String[] getGenericCategories() {
		return new String[] {
				CATEGORY_ROUTER,
				CATEGORY_ROUTE_SWITCH,
				CATEGORY_SWITCH,
				CATEGORY_FIREWALL
		};
	}

	
	public static boolean isGenericNetworkDevice(String category) {
		for (String cate : getGenericCategories()) {
			if (cate.equals(category))
				return true;
		}
		return false;
	}

	public static boolean isBridge(String category) {
		return (category.equals("RouteSwitch")) || (category.equals("Switch"));
	}

	public static Category getRouterCategory() {
		return new Category("Router", "路由器", Host.class);
	}

	public static Category getRouteSwitchCategory() {
		return new Category("RouteSwitch", "路由交换机", Host.class);
	}

	public static Category getSwitchCategory() {
		return new Category("Switch", "交换机", Host.class);
	}

	public static Category getFirewallCategory() {
		return new Category("Firewall", "防火墙", Host.class);
	}

	public static Category getServerCategory() {
		return new Category("Server", "服务器", Host.class);
	}

	public static Category getLinkCategory() {
		return new Category("Link", "链路", Link.class);
	}
	public static Category getSubnetCategory() {
		return new Category("Subnet", "子网", Subnet.class);
	}

	public static boolean isComputingResDevice(String category) {
		for (String cate : getComputingResCategories()) {
			if (cate.equals(category))
				return true;
		}
		return false;
	}
	public static String[] getComputingResCategories() {
		return new String[] { CATEGORY_SERVER };
	}
}
