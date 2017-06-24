package com.isoft;

import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.Defines;
import com.isoft.types.CArray;

import static com.isoft.iradar.Cphp._;

public class Feature extends iFeature{

	private Feature() {
	}
	
	public static boolean originalStyle = true;

	public static String iradarServer = "127.0.0.1";
	public static int iradarPort = 10051;

	public static String defaultTenantId = "-1";
	public static final int defaultOsUserGroup = 7;
	
	public static boolean enableGuestUser = false;
	
	public static boolean enableLoginCheck = true;
	//public static boolean enableGlobalException = true;
	
	public static boolean enableTopo = false;
	public static boolean enableIPMI = false;
	public static boolean enableJMX = false;

	public static boolean ignorePageFooter = false;
	public static boolean ignorePageHeader = false;

	public static boolean showFullscreenIcon = true;
	public static boolean showFavouriteIcon = true;
	public static boolean showExportCsvIcon = true;
	public static boolean showPopupMenu = true;

	public static boolean showProxy = false;
	
	public static String codeLogin = "C";
	
	public static boolean idsUseConnection = false;
	
	/**
	 * 添加监控指标操作，指标类型弹窗(PopupAction:参数srctbl=help_items)中 类型 下拉列表中允许的指标类型
	 */
	public static CArray<Integer> itemTypePopupNeed = CArray.array(Defines.ITEM_TYPE_IRADAR,Defines.ITEM_TYPE_IRADAR_ACTIVE);
	/**
	 * 策略中心--告警策略--告警响应的发送消息操作：用户选择弹窗(PopupAction:参数srctbl=users)中，是否隐藏系统的默认用户admin，以及隐藏开关
	 * 若显示,则将hideSysDefaultUser置为false,否则，置为true
	 */
	public static String sysDefaultUser = "admin";
	public static boolean hideSysDefaultUser = false;
	/**
	 * 定义系统中需要隐藏的设备类型,map类型,GroupId作为key,名称作为value,根据名称过滤,使用hostGroupsNeedHide.containsValue方法,
	 * 根据groupId过滤,使用.containsKey方法
	 */
	public static CArray<String> hostGroupsNeedHide = CArray.map(IMonConsts.DISCOVERED_HOSTS,"Discovered hosts",
																 IMonConsts.TEMPLATES,"Templates",
																 IMonConsts.MON_VM,_("Cloud host"));
	
	public static String LOCALHOSTIPV4 = "127.0.0.1";
	public static CArray<String> LOCALHOSTIPV6CA = CArray.array("0:0:0:0:0:0:0:1","::1");
	
	public static CArray dbMacros = CArray.array("{$USER}","{$PSWD}","{$DBIP}","{$PORT}","{$JAVAHOME}");
	public static CArray dbMongoMacros = CArray.array("{$MONGOIP}");
	public static CArray jmxTomcatMacros = CArray.array("{$TOMCAT_PORT}","{$JMX_PORT}","{$JAVAHOME}");
	public static CArray jmxWebLogicMacros = CArray.array("{$WEBLOGIC_PORT}","{$JMX_PORT}","{$JAVAHOME}");
	public static CArray jmxWebSphereMacros = CArray.array("{$WEBSPHERE_PORT}","{$JMX_PORT}","{$JAVAHOME}");
	public static CArray iaasMacros = CArray.array("{$PORTAL_IP}");
	public static CArray<Long> dbIds = CArray.array(IMonConsts.MON_DB_MySQL,IMonConsts.MON_DB_Oracle,IMonConsts.MON_DB_SQLSERVER,IMonConsts.MON_DB_DB2);
	public static CArray<Long> dbMongoIds = CArray.array(IMonConsts.MON_DB_MONGODB);
	public static CArray<Long> jmxTomcatIds = CArray.array(IMonConsts.MON_MIDDLE_TOMCAT);
	public static CArray<Long> jmxWebLogicIds = CArray.array(IMonConsts.MON_MIDDLE_WEBLOGIC);
	public static CArray<Long> jmxWebSphereIds = CArray.array(IMonConsts.MON_MIDDLE_WEBSPHERE);
	public static CArray<Long> iaasIds = CArray.array(IMonConsts.MON_CLOUD_CONTROLER);
	
}
