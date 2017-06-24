package com.isoft.iradar.web.action.moncenter;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.common.util.ItemsKey.*;
import static com.isoft.iradar.web.bean.Column.column;
import static com.isoft.iradar.web.bean.Key.avg;
import static com.isoft.iradar.web.bean.Key.count;
import static com.isoft.iradar.web.bean.Key.status;
import static com.isoft.iradar.web.bean.Key.sum;
import static com.isoft.iradar.web.bean.Key.value;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.types.CArray;

public interface I_LatestDataAction {
	
	public final static String VM_SIMPLE_DATA_ACTION  = "mon_vm.action";
	public final static String VM_LATEST_DATA_ACTION  = "mon_vm_latest.action";
	
	public final static String LINUX_SIMPLE_DATA_ACTION  = "linux_mon_server.action";
	public final static String LINUX_LATEST_DATA_ACTION  = "linux_mon_server_latest.action";
	
	public final static String WINDOWS_SIMPLE_DATA_ACTION  = "windows_mon_server.action";
	public final static String WINDOWS_LATEST_DATA_ACTION  = "windows_mon_server_latest.action";
	
	public final static String NETDEV_SIMPLE_DATA_ACTION  = "mon_common_net_host.action";
	public final static String NETDEV_LATEST_DATA_ACTION  = "mon_common_net_host_latest.action";
	
	public final static String CISCO_SIMPLE_DATA_ACTION  = "mon_net_cisco.action";
	public final static String CISCO_LATEST_DATA_ACTION  = "mon_net_cisco_latest.action";
	
	public final static String HUAWEI_SIMPLE_DATA_ACTION  = "mon_net_huawei_switch.action";
	public final static String HUAWEI_LATEST_DATA_ACTION  = "mon_net_huawei_switch_latest.action";
	
	public final static String ZHONGXING_SIMPLE_DATA_ACTION  = "mon_net_zhongxing_switch.action";
	public final static String ZHONGXING_LATEST_DATA_ACTION  = "mon_net_zhongxing_switch_latest.action";
	
	public final static String MYSQL_SIMPLE_DATA_ACTION  = "mon_db_mysql.action";
	public final static String MYSQL_LATEST_DATA_ACTION  = "mon_db_mysql_latest.action";
	
	public final static String ORACLE_SIMPLE_DATA_ACTION  = "mon_db_oracle.action";
	public final static String ORACLE_LATEST_DATA_ACTION  = "mon_db_oracle_latest.action";
	
	public final static String DB2_SIMPLE_DATA_ACTION  = "mon_db_db2.action";
	public final static String DB2_LATEST_DATA_ACTION  = "mon_db_db2_latest.action";
	
	public final static String MSSQL_SIMPLE_DATA_ACTION  = "mon_db_sqlserver.action";
	public final static String MSSQL_LATEST_DATA_ACTION  = "mon_db_sqlserver_latest.action";
	
	public final static String DM_SIMPLE_DATA_ACTION  = "mon_db_dm.action";
	public final static String DM_LATEST_DATA_ACTION  = "mon_db_dm_latest.action";
	
	public final static String MONGO_SIMPLE_DATA_ACTION  = "mon_db_mongo.action";
	public final static String MONGO_LATEST_DATA_ACTION  = "mon_db_mongo_latest.action";
	
	public final static String TOMCAT_SIMPLE_DATA_ACTION  = "mon_middle_tomcat.action";
	public final static String TOMCAT_LATEST_DATA_ACTION  = "mon_middle_tomcat_latest.action";
	
	public final static String IIS_SIMPLE_DATA_ACTION  = "mon_middle_iis.action";
	public final static String IIS_LATEST_DATA_ACTION  = "mon_middle_iis_latest.action";
	
	public final static String WEBLOGIC_SIMPLE_DATA_ACTION  = "mon_middle_weblogic.action";
	public final static String WEBLOGIC_LATEST_DATA_ACTION  = "mon_middle_weblogic_latest.action";
	
	public final static String WEBSPHERE_SIMPLE_DATA_ACTION  = "mon_middle_websphere.action";
	public final static String WEBSPHERE_LATEST_DATA_ACTION  = "mon_middle_websphere_latest.action";
	
	public final static String CLOUDCONTROL_SIMPLE_DATA_ACTION  = "mon_cloud_control.action";
	public final static String CLOUDCONTROL_LATEST_DATA_ACTION  = "mon_cloud_control_latest.action";
	
	public final static String CLOUDCOMPUTE_SIMPLE_DATA_ACTION  = "mon_cloud_compute.action";
	public final static String CLOUDCOMPUTE_LATEST_DATA_ACTION  = "mon_cloud_compute_latest.action";
	
	public final static String CLOUDNETWORK_SIMPLE_DATA_ACTION  = "mon_cloud_network.action";
	public final static String CLOUDNETWORK_LATEST_DATA_ACTION  = "mon_cloud_network_latest.action";
	
	public final static String CLOUDCEPH_SIMPLE_DATA_ACTION  = "mon_cloud_ceph.action";
	public final static String CLOUDCEPH_LATEST_DATA_ACTION  = "mon_cloud_ceph_latest.action";
	
	public final static String CLOUDWEB_SIMPLE_DATA_ACTION  = "mon_cloud_web.action";
	public final static String CLOUDWEB_LATEST_DATA_ACTION  = "mon_cloud_web_latest.action";
	
	public final static String CLUSTER_SIMPLE_DATA_ACTION  = "mon_cluster.action";
	public final static String CLUSTER_LATEST_DATA_ACTION  = "mon_cluster_latest.action";
	
	public final static String DESKCLOUD_SIMPLE_DATA_ACTION  = "mon_desktopc.action";
	public final static String DESKCLOUD_LATEST_DATA_ACTION  = "mon_desktopc_latest.action";
	
	public final static String STORAGE_SIMPLE_DATA_ACTION  = "mon_storage.action";
	public final static String STORAGE_LATEST_DATA_ACTION  = "mon_storage_latest.action";
	
	public final static String OTHERS_LATEST_DATA_ACTION  = "mon_others.action";
	
	long getHostGroupId();

	String getSimpleAction();

	String getLatestAction();
	
	public final static CArray CONFIGS = map(
			LINUX_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_SERVER_LINUX,
				"columns", array(
					column(_("CPU_RATE"), 			value(CPU_USER_RATE,2,true)),
					column(_("CPU_LOAD"), 			avg(CPU_LOAD, 2,true)),
					column(_("memoryRate"), 		avg(MEMORY_USELV_KEY, 2,true)),
					column(_("disk_Rate"), 			avg(DISK_USELV_KEY, 2,true)),
					column(_("disk_read_and_write"),sum(DISK_READ_SPEED, true), 	sum(DISK_WRITE_SPEED, true)),
					column(_("net_Rate"), 			sum(NET_UP_SPEED, true), 		sum(NET_DOWN_SPEED, true))
				)
			),
			WINDOWS_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_SERVER_WINDOWS,
				"columns", array(
					column(_("the cpuUsage of host"), 			       value(CPU_USER_RATE_WINDOWS,2,true)),
					column(_("the cpuLoad of host"), 			       value(CPU_LOAD_WINDOWS, 2,true)),
					column(_("the memoryUsage of host"), 		       value(MEMORY_USELV_KEY_WINDOWS, 2,true)),
					column(_("the diskUsage of host"), 			       avg(DISK_USELV_KEY_WINDOWS, 2,true)),
					column(_("the write and write request BW of host"),value(DISK_READ_REQUEST_BW_WINDOWS, true), value(DISK_WRITE_REQUEST_BW_WINDOWS, true)),
					column(_("the net up and down flow of host"), 	   sum(NET_UP_FLOW_WINDOWS, true), 			sum(NET_DOWN_FLOW_WINDOWS, true))
				)
			),
			VM_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_VM,
				"columns", array(
					column(_("the floatingIps Of VM"), 					value(FLOATINGIPS_VM)),
					column(_("the tenant Of Vm"), 						value(TENANT_VM)),
					column(_("the user Of Vm"), 			       		value(USER_VM)),
					column(_("the cpuUsage of host"), 			       	value(array(CPU_RATE_VM_LINUX,CPU_RATE_VM_WINDOWS),2,true)),
					column(_("the cpuLoad of host"), 			       	value(CPU_LOAD_VM, 2,true)),
					column(_("the memoryUsage of host"), 		       	value(MEMORY_RATE_VM, 2,true)),
					column(_("the diskUsage of host"), 			    	avg(DISK_RATE_VM,true)),
					column(_("the write and write request BW of host"),	value(array(DISK_READ_SPEED_VM_LINUX,DISK_READ_SPEED_VM_WINDOWS),2,true), value(array(DISK_WRITE_SPEED_VM_LINUX,DISK_WRITE_SPEED_VM_WINDOWS),2,true)),
					column(_("the net up and down flow of host"), 	   	sum(NET_UP_FLOW_VM, true), 			sum(NET_DOWN_FLOW_VM, true))
				)
			),
			NETDEV_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_COMMON_NET,
				"columns", array(
					column(_("equipmentname"), 		value(COMMON_NET_SYSNAME)),
					column(_("Runing Time"), 		value(COMMON_NET_SYSUPTIME,true)),
					column(_("ifNumber"), 			value(COMMON_NET_IFNUMBER)),
					column(_("NET_IFIN_OUTERRORS"), sum(COMMON_NET_IFOUTERRORS, true),sum(COMMON_NET_IFINERRORS, true)),
					column(_("net_Rate"), 			sum(COMMON_NET_IFOUTOCTETS, true),sum(COMMON_NET_IFINOCTETS, true))
				)
			),	
			CISCO_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_NET_CISCO,
				"columns", array(
					column(_("CPU_RATE"), 			value(CISCO_CPU_RATE,2,true)),
//					column(_("CPU_NUM"), 			count(CISCO_CPU_NAME)),
					column(_("cisco_icmpping"), 	value(CISCO_ICMPPING,2,true)),
					column(_("ciscomemorypoolfree"),sum(CISCO_CISCOMEMORYPOOLFREE,true)),
					column(_("ciscomemorypoolused"),sum(CISCO_CISCOMEMORYPOOLUSED,true)),
					column(_("ifconnectorpresent"), count(CISCO_IFCONNECTORPRESENT, "1")),
					column(_("ifoutoctets"), 		sum(CISCO_IFOUTOCTETS, true), 		sum(CISCO_IFINOCTETS, true))
				)
			),
			HUAWEI_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_NET_HUAWEI_SWITCH,
				"columns", array(
					column(_("CPU_RATE"), 			value(NET_HUAWEI_CPU_RATE,2,true)),
					column(_("NET_HUAWEI_MEM_FREE"),value(NET_HUAWEI_MEM_FREE,2,true)),
					column(_("NET_HUAWEI_MEM_TOTAL"),value(NET_HUAWEI_MEM_TOTAL,2,true)),
					column(_("Runing Time"), 		value(COMMON_NET_SYSUPTIME,true)),
					column(_("ifNumber"), 			value(COMMON_NET_IFNUMBER)),
					column(_("NET_IFIN_OUTERRORS"), sum(COMMON_NET_IFOUTERRORS, true),sum(COMMON_NET_IFINERRORS, true)),
					column(_("net_Rate"), 			sum(COMMON_NET_IFOUTOCTETS, true),sum(COMMON_NET_IFINOCTETS, true))
				)
			),
			ZHONGXING_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_NET_ZHONGXING_SWITCH,
				"columns", array(
					column(_(""), 			""),
					column(_(""), 			""),
					column(_(""), 			""),
					column(_(""), 			"")
				)
			),
			MYSQL_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DB_MYSQL,
				"columns", array(
					column("查询量/秒",  		value(QUERIES_PER_MYSQL,2,true)),
					column("并发线程数", 		value(THREADS_RUNNING_MYSQL)),
					column("运行时间", 		value(LAST_START_MYSQL,true)),
					column("运行状态", 		value(IS_ALIVE_MYSQL,true)),
					column(_("dbversion"), 	value(DBVERSION_MYSQL))
				)
			),
			ORACLE_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DB_ORACLE,
				"columns", array(
					column("系统会话个数",	 	value(SESSION_SYSTEM_ORACLE)),
					column("活动会话个数", 	 	value(SESSION_ACTIVE_ORACLE)),
					column("不活动会话个数",  	value(SESSION_INACTIVE_ORACLE)),
					column("运行时间", 		value(UPTIME_ORACLE,true)),
					column("运行状态", 		value(ALIVE_ORACLE,true))
				)
			),
			MSSQL_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DB_SQLSERVER,
				"columns", array(
					column(_("db_sqlserver_cachehit"),	 value(DB_SQLSERVER_CACHEHIT,2,true)),
					column(_("db_sqlserver_dbsize"), 	 value(DB_SQLSERVER_DBSIZE,2,true)),
					column(_("db_sqlserver_logsize"), 	 value(DB_SQLSERVER_LOGSIZE,2,true)),
					column(_("db_sqlserver_logusedsize"),value(DB_SQLSERVER_LOGUSEDSIZE,2,true)),
					column("系统会话个数", 					 value(SESSIONS_PAGEWRITES,2,true)),
					column("SQL Agent的状态", 			 value(SQL_AGENT_STATUS_PAGEWRITES)),
					column("读取IO速率", 					 value(PAGEREADS_PAGEWRITES,2,true)),
					column("写入IO速率", 					 value(PAGEWRITES_PAGEWRITES,2,true))
				)
			),
			DB2_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DB_DB2,
				"columns", array(
					column(_("DB_DB2_COMMITS_ATTEMPTED"),value(DB_DB2_COMMITS_ATTEMPTED)),
					column(_("DB_DB2_TOTALLOGSPUSED"), 	 value(DB_DB2_TOTALLOGSPUSED,true)),
					column(_("DB_DB2_SORTTIME"), 		 value(DB_DB2_SORTTIME,true)),
					column(_("DB_DB2_APPLCOUNT"), 		 value(DB_DB2_APPLCOUNT,0,true)),
					column(_("db_status"), 			 	 value(DB_DB2_ALIVE,true))
				)
			),	
			MONGO_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DB_MONGODB,
				"columns", array(
					column(_("db_mongodb_indexes"), 	value(DB_MONGODB_INDEX_COUNT)),
					column(_("db_mongodb_opertions"), 	value(DB_MONGODB_OPCOUNTERS_COMMAND)),
					column(_("db_mongodb_connections"), value(DB_MONGODB_CONNECTIONS_CURRENT)),
					column(_("db_mongodb_refreshTime"), value(DB_MONGODB_BACKGROUNDFLUSHING_TOTAL_MS,true)),
					column(_("db_status"), 				value(DB_MONGODB_STATUS,true))
				)
			),	
			TOMCAT_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_MIDDLE_TOMCAT,
				"columns", array(
					column(_("errorcount_tomcat"), 		value(ERRORCOUNT_TOMCAT)),
					column(_("activesessions_tomcat"), 	value(ACTIVESESSIONS_TOMCAT)),
					column(_("totalsession_tomcat"), 	value(TOTALSESSION_TOMCAT)),
					column(_("currentthreadcount"), 	value(CURRENTTHREADCOUNT_TOMCAT)),
					column(_("currentthreadsbusy"), 	value(CURRENTTHREADSBUSY)),
					column(_("maxthreads"), 			value(MAXTHREADS_TOMCAT)),
					column(_("heapmemoryusageused"), 	value(HEAPMEMORYUSAGEUSED,true)),
					column(_("heapmemoryusagemax"), 	value(HEAPMEMORYUSAGEMAX,true))
				)
			),
			IIS_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_MIDDLE_IIS,
				"columns", array(
					column(ItemsKey.MID_IIS_ASPNET_REQUESTS_CURRENT.getName(), 					value(MID_IIS_ASPNET_REQUESTS_CURRENT)),
					column(ItemsKey.MID_IIS_ASPNET_APPLICATIONS_TOTAL_REQUESTS_SEC.getName(), 	value(MID_IIS_ASPNET_APPLICATIONS_TOTAL_REQUESTS_SEC)),
					column(ItemsKey.MID_IIS_ASPNET_APPLICATIONS_TOTAL_ERRORS_TOTAL.getName(), 	value(MID_IIS_ASPNET_APPLICATIONS_TOTAL_ERRORS_TOTAL)),
					column(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_CURRENT_ANONYMOUS_USERS.getName(), value(MID_IIS_WEBSERVICE_TOTAL_CURRENT_ANONYMOUS_USERS)),
					column(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS.getName(), 	value(MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS)),
					column(ItemsKey.MID_IIS_W3SVC.getName(), 									value(MID_IIS_W3SVC,true))
				)
			),
			WEBLOGIC_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_MIDDLE_WEBLOGIC,
				"columns", array(
					column(_("MID_WEBLOGIC_JMSSERVERSCURRENTCOUNT"), 	value(MID_WEBLOGIC_JMSSERVERSCURRENTCOUNT)),
					column(_("MID_WEBLOGIC_JMSCONNECTIONSCURRENTCOUNT"),value(MID_WEBLOGIC_JMSCONNECTIONSCURRENTCOUNT)),
					column(_("MID_WEBLOGIC_JMSCONNECTIONSHIGHCOUNT"), 	value(MID_WEBLOGIC_JMSCONNECTIONSHIGHCOUNT)),
					column(_("MID_WEBLOGIC_JMSSERVERSHIGHCOUNT"), 		value(MID_WEBLOGIC_JMSSERVERSHIGHCOUNT)),
					column(ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEUSED.getName(), 	value(MID_WEBLOGIC_HEAPMEMORYUSAGEUSED,true)),
					column(ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEUSED.getName(), 		value(MID_WEBLOGIC_PSPERMGENUSAGEUSED,true)),
					column(ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED.getName(), 	value(MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED,true)),
					column(ItemsKey.MID_WEBLOGIC_UPTIME.getName(), 					value(MID_WEBLOGIC_UPTIME,true))
				)
			),
			WEBSPHERE_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_MIDDLE_WEBSPHERE,
				"columns", array(
					column(ItemsKey.MID_WEBSPHERE_JVMPEAKTHREADCOUNT.getName(), 		value(MID_WEBSPHERE_JVMPEAKTHREADCOUNT)),
					column(ItemsKey.MID_WEBSPHERE_JVMDAEMONTHREADCOUNT.getName(), 		value(MID_WEBSPHERE_JVMDAEMONTHREADCOUNT)),
					column(ItemsKey.MID_WEBSPHERE_JVMTHREADCOUNT.getName(), 			value(MID_WEBSPHERE_JVMTHREADCOUNT)),
					column(ItemsKey.MID_WEBSPHERE_HEAPMEMORYUSAGEUSED.getName(), 		value(MID_WEBSPHERE_HEAPMEMORYUSAGEUSED,true)),
					column(ItemsKey.MID_WEBSPHERE_JVMHEAPUSAGEUSED.getName(), 			value(MID_WEBSPHERE_JVMHEAPUSAGEUSED,true)),
					column(ItemsKey.MID_WEBSPHERE_NONHEAPMEMORYUSAGEUSED.getName(), 	value(MID_WEBSPHERE_NONHEAPMEMORYUSAGEUSED,true)),
					column(ItemsKey.MID_WEBSPHERE_UPTIME.getName(), 					value(MID_WEBSPHERE_UPTIME,true))
				)
			),
			CLOUDCONTROL_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLOUD_CONTROLER,
				"columns", array(
					column(_("count"), 			value(VM_COUNT)),
					column(_("memory_total"), 	value(VM_MEMORY_TOTAL, true)),
					column(_("tenant_count"), 	value(VM_TENANT_COUNT)),
					column(_("alert_count"), 	value(VM_ALERT_COUNT)),
					column(_("hypervior_count"),value(VM_HYPERVIOR_COUNT)),
					column(_("image_count"), 	value(VM_IMAGE_COUNT))
				)
			),	
			CLOUDCOMPUTE_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLOUD_COMPUTER,
				"columns", array(
					column(_("iaas_compute_libvirtd"),  		status(CLOUD_COMPUTER_PROCNUM_LIBVIRTD)),
					column(_("iaas_compute_ovs"), 				status(CLOUD_COMPUTER_PROCNUM_OVS)),
					column(_("iaas_compute_compute"),  			status(CLOUD_COMPUTER_PROCNUM_COMPUTE)),
					column(_("iaas_compute_ceilometer_compute"),status(CLOUD_COMPUTER_PROCNUM_CEILOMETER_COMPUTE))
				)
			),	
			CLOUDCEPH_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLOUD_CEPH,
				"columns", array(
					column(_("ceph_mon"), 		value(CEPH_MON)),
					column(_("ceph_mds"), 		value(CEPH_MDS)),
					column(_("ceph_osd"), 		value(CEPH_OSD)),
					column(_("ceph_active"), 	value(CEPH_ACTIVE)),
					column(_("ceph_pg_total"),	value(CEPH_PG_TOTAL))
				)
			),
			CLOUDNETWORK_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLOUD_NETWORK,
				"columns", array(
					column(_("iaas_network_dhcp"),  	status(CLOUD_NETWORK_PROCNUM_DHCP)),
					column(_("iaas_network_l3"), 		status(CLOUD_NETWORK_PROCNUM_L3)),
					column(_("iaas_network_lbaas"),  	status(CLOUD_NETWORK_PROCNUM_LBAAS)),
					column(_("iaas_network_metadata"), 	status(CLOUD_NETWORK_PROCNUM_METADATA)),
					column(_("iaas_network_ovs"),  		status(CLOUD_NETWORK_PROCNUM_OVS))	
				)
			),
			CLOUDWEB_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLOUD_WEB,
				"columns", array(
					column(_("iaas_web_httpd"),  		status(CLOUD_WEB_PROCNUM_HTTPD))
				)
			),
			CLUSTER_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_CLUSTER,
				"columns", array(
					column(_("dcName"), 			  count(DCNAME_CLUSTER, null)),
					column(_("dcEvents"), 			  sum(DCEVENTS_CLUSTER)),
					column(_("clusterDcName"), 		  count(CLUSTERDCNAME_CLUSTER, null)),
					column(_("clusterMaxMemRequest"), sum(CLUSTERMAXMEMREQUEST_CLUSTER))
				)
			),	
			DESKCLOUD_SIMPLE_DATA_ACTION, map(
				"group", IMonGroup.MON_DESKTOPC,
				"columns", array(
					column(_("hostActiveVmCount"), 	  sum(HOSTACTIVEVMCOUNT_DESKTOPC)),
					column(_("hostMemoryUsage"), 	  sum(HOSTMEMORYUSAGE_DESKTOPC)),
					column(_("hostCpuUsage"), 		  value(HOSTCPUUSAGE_DESKTOPC)),
					column(_("vmPoolAllocVmCount"),   sum(VMPOOLALLOCVMCOUNT_DESKTOPC)),
					column(_("vmPoolRunningVmCount"), sum(VMPOOLRUNNINGVMCOUNT_DESKTOPC))
				)
			)	
			/**
			 * 系统屏蔽默认的存储设备类型
			 */
//			STORAGE_SIMPLE_DATA_ACTION, map(
//				"group", IMonGroup.MON_STORAGE,
//				"columns", array(
//					column(_(""), 			""),
//					column(_(""), 			""),
//					column(_(""), 			""),
//					column(_(""), 			"")
//				)
//			)
					
		);

}
