package com.isoft.iradar.common.util;

import java.util.Map;

import static com.isoft.iradar.Cphp._;

import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.types.CArray;


/**
 * 项目中公用常量
 * @author HP Pro2000MT
 *
 */
public class IMonConsts {
	
	public final static String JS_OPEN_TAB_HEAD = "javascript:window.top.$.workspace.openTab(";
	public final static String JS_OPEN_TAB_TAIL = ")";
	public final static String COMMON_ACTION_PREFIX = "/platform/iradar/";
	public final static String MONITOR_CENTER_HOST_DETAIL = COMMON_ACTION_PREFIX+"host_detail.action?hostid=%1$s&groupid=%2$s";
	public final static String MONITOR_CENTER_OTHER_DETAIL = COMMON_ACTION_PREFIX+"mon_others.action?hostid=%1$s&groupid=%2$s";
	
	public final static String ROLE_LESSOR_MANAGER = "SystemManager";
	public final static String ROLE_TENANT_MANAGER = "TenantManager";
	
	public final static String STYLE_CLASS_MULTLINE = "multiline";
	public final static String STYLE_CLASS_CMD_ROW = "cmd_row";
	public final static String STYLE_CLASS_CMD_TABLE = "cmd_table";
	public final static String STYLE_CLASS_CMD_DIV = "cmd_div";
	//正则表达式
	public static final String RDA_PREG_ZIFU = "([.~@#<>$%\\^\\+\\*&\\\\\\/\\?\\|:\\.{}()';=\"])";  //特殊字符验证
	//自定义告警线
	public final static int T_HTTPCONF_RESPONSE_TIME = 0; //当前响应时间
	public final static int T_HTTPCONF_AVAilABLE_RATE =1; //当日可用率
	public final static int T_HTTPCONF_CONDITION_LT = 0; //小于
	public final static int T_HTTPCONF_CONDITION_GT = 1; //大于
	public final static int T_HTTPCONF_CONDITION_MS = 0; //毫秒
	public final static int T_HTTPCONF_CONDITION_S = 1; //秒
	
	public final static int TOTALSITUATION_FLAG = 0;  //系统健康度面板fusionCharts引用值
	public final static int CPUUSEDRATE_FLAG = 1;	  //CPU利用率fusionCharts引用值
	public final static int MEMORYUSEDRATE_FLAG = 2;  //内存利用率fusionCharts引用值
	public final static int DISKUSEDRATE_FLAG = 3;	  //硬盘利用率fusionCharts引用值
	public final static int NETUSEDRATE_FLAG = 4;	  //网络利用率fusionCharts引用值
	
	public final static int REPOT_SHOWNUM_10 = 5;//报表topN展示数目常量
	
	public final static int ONITORING_FREQUENCY_15 = 900;
	public final static int ONITORING_FREQUENCY_30 = 1800;
	public final static int ONITORING_FREQUENCY_45 = 2700;
	public final static int ONITORING_FREQUENCY_60 = 3600;
	
	
	public final static int T_THRESHOLD_VALUE_LENGTJ = 15;//租户阈值输入长度限制值
	//租户中服务应用类型
	public final static int A_TYPE_VM=1;
	public final static int A_TYPE_MYSQL=2;
	public final static int A_TYPE_TOMCAT=3;
	
	//租户中服务应用添加默认名称前缀
	public final static String APP_NAME_MYSQL = "mysql_";
	public final static String APP_NAME_TOMCAT = "tomcat_";
	
	//巡检报表正常异常属性
	public final static int INSPECTION_ITEM_HISTORY_NORMAL = 0;//巡检报表历史数据监控指标正常
	public final static int INSPECTION_ITEM_HISTORY_ABNORMAL = 1;//巡检报表历史数据监控指标异常
	public final static int INSPECTION_ITEM_HISTORY_OTHER = 2;//巡检报表历史数据监控指标未统计
	
	//运营商系统常用参数
	public static Double CPU_RATE = null;		//CPU利用率
	public static Double MEMORY_RATE = null;	//内存利用率
	public static Double DISK_RATE = null;	//磁盘利用率
	
	//运营商系统资产通用属性
	public final static String DEPT = "dept";				//所属部门
	public final static String MOTOR_ROOM = "motor_room";	//所在机房
	public final static String CABINET = "cabinet";			//所在机柜
	public final static String SYSTEM = "system";			//操作系统
	public final static String FIRM = "firm";				//厂商
	
	public final static String INTERFACE_LIMITNUM = "20";				//厂商
	
	//设备类型对应ID
	public final static Long DISCOVERED_HOSTS = IMonGroup.DISCOVERED_HOSTS.id(); 
	public final static Long TEMPLATES 		= IMonGroup.TEMPLATES.id(); 		
	public final static Long MON_SERVER_WINDOWS 	= IMonGroup.MON_SERVER_WINDOWS.id();  
	public final static Long MON_SERVER_LINUX 	= IMonGroup.MON_SERVER_LINUX.id();  
	public final static Long MON_VM 		= IMonGroup.MON_VM.id();  			
	public final static Long MON_NET_CISCO 		= IMonGroup.MON_NET_CISCO.id();  
	public final static Long MON_NET_HUAWEI_SWITCH 	= IMonGroup.MON_NET_HUAWEI_SWITCH.id();  
	public final static Long MON_NET_ZHONGXING_SWITCH = IMonGroup.MON_NET_ZHONGXING_SWITCH.id();  
	/**
	 * 系统屏蔽默认的存储设备类型
	 */
//	public final static Long MON_STORAGE 	= IMonGroup.MON_STORAGE .id();  	
	public final static Long MON_DB_MySQL 	= IMonGroup.MON_DB_MYSQL.id();
	public final static Long MON_DB_Oracle	= IMonGroup.MON_DB_ORACLE.id();
	public final static Long MON_DB_DM 		= IMonGroup.MON_DB_DM.id();
	public final static Long MON_DB_DB2 		= IMonGroup.MON_DB_DB2.id();
	public final static Long MON_DB_SQLSERVER 		= IMonGroup.MON_DB_SQLSERVER.id();
	public final static Long MON_DB_MONGODB 		= IMonGroup.MON_DB_MONGODB.id();
	public final static Long MON_MIDDLE_TOMCAT 	= IMonGroup.MON_MIDDLE_TOMCAT.id();  
	public final static Long MON_MIDDLE_IIS 	= IMonGroup.MON_MIDDLE_IIS.id();
	public final static Long MON_MIDDLE_WEBLOGIC 	= IMonGroup.MON_MIDDLE_WEBLOGIC.id();  
	public final static Long MON_MIDDLE_WEBSPHERE 	= IMonGroup.MON_MIDDLE_WEBSPHERE.id();  
	public final static Long MON_WEB 		= IMonGroup.MON_WEB.id();  		
	public final static Long MON_CLOUD_CONTROLER 		= IMonGroup.MON_CLOUD_CONTROLER.id();
	public final static Long MON_CLOUD_COMPUTER 	=   IMonGroup.MON_CLOUD_COMPUTER.id();
	public final static Long MON_CLOUD_CEPH 	= IMonGroup.MON_CLOUD_CEPH.id();
	public final static Long MON_CLOUD_NETWORK 		= IMonGroup.MON_CLOUD_NETWORK.id();
	public final static Long MON_CLOUD_WEB 		= IMonGroup.MON_CLOUD_WEB.id();
	public final static Long MON_CLUSTER 	= IMonGroup.MON_CLUSTER.id();
	public final static Long MON_DESKTOPC 	= IMonGroup.MON_DESKTOPC.id();
	public final static Long MON_COMMON_NET 	= IMonGroup.MON_COMMON_NET.id();
	public final static CArray<Long> MON_GROUP_DEFAULT = CArray.array(MON_SERVER_WINDOWS,MON_SERVER_LINUX,MON_VM,MON_NET_CISCO,MON_NET_HUAWEI_SWITCH,
														 MON_NET_ZHONGXING_SWITCH,MON_DB_MySQL,MON_DB_Oracle,MON_DB_DM,MON_DB_DB2,MON_DB_SQLSERVER,
														 MON_DB_MONGODB,MON_MIDDLE_TOMCAT,MON_MIDDLE_IIS,MON_MIDDLE_WEBLOGIC,MON_MIDDLE_WEBSPHERE,MON_WEB,MON_CLOUD_CONTROLER,MON_CLOUD_COMPUTER,MON_CLOUD_CEPH,MON_CLOUD_NETWORK,
														 MON_CLOUD_WEB,MON_CLUSTER,MON_DESKTOPC,MON_COMMON_NET);
	
	//监控类别 对应sys_func表中的id
	public final static String MON_CATE_SERVER  = "00030002";//监控类别--服务器 
	public final static String MON_CATE_NET_DEV = "00030003";//监控类别--网络设备 
	public final static String MON_CATE_STORAGE = "00030004";//监控类别--存储设备 
	public final static String MON_CATE_PLA_SER = "00030007";//监控类别--平台服务
	public final static String MON_CATE_DB  	= "00030009";//监控类别--数据库 
	public final static String MON_CATE_MIDWARE = "00030010";//监控类别--中间件 
	public final static String MON_CATE_OTHER   = "00030014";//监控类别--其他 
	public final static Map<String,String> MON_CATE = EasyMap.build(MON_CATE_SERVER,_("MON_CATE_SERVER"),
													 				MON_CATE_NET_DEV,_("MON_CATE_NET_DEV"),
													 				MON_CATE_STORAGE,_("MON_CATE_STORAGE"),
													 				MON_CATE_PLA_SER,_("MON_CATE_PLA_SER"),
													 				MON_CATE_DB,_("MON_CATE_DB"),
													 				MON_CATE_MIDWARE,_("MON_CATE_MIDWARE"),
													 				MON_CATE_OTHER,_("MON_CATE_OTHER"));
	
}
