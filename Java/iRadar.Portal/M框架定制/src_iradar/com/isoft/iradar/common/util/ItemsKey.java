package com.isoft.iradar.common.util;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 监控指标键值
 * 
 * @author HP Pro2000MT
 * 
 */
public enum ItemsKey {

	/**
	 * CPU使用率-服务器linux
	 */
	CPU_USER_RATE(IMonConsts.MON_SERVER_LINUX, "cpuUsage", "CPU使用率"),
	/**
	 * CPU信息-服务器linux
	 */
	CPU_INFO_LINUX(IMonConsts.MON_SERVER_LINUX, "cpuDesc[{#INDEX}]", "CPU信息"),
	/**
	 * CPU负载-服务器linux
	 */
	CPU_LOAD(IMonConsts.MON_SERVER_LINUX, "cpuLoad[{#INDEX}]", "CPU负载"),
	/**
	 * 硬盘写入速率(KB/s)-服务器linux
	 */
	DISK_WRITE_SPEED(IMonConsts.MON_SERVER_LINUX, "diskWriteBPS[{#SNMPVALUE}]", "硬盘写入速率"),
	/**
	 * CPU使用率-服务器windows
	 */
	CPU_USER_RATE_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "perf_counter[\"\\Processor(_Total)\\% Processor Time\"]", "CPU使用率"),
	/**
	 * CPU负载-服务器windows
	 */
	CPU_LOAD_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "system.cpu.load[percpu,avg1]", "CPU负载"),
	/**
	 * CPU型号-服务器windows
	 */
	CPU_MODEL_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "wmi.get[root\\cimv2,Select Name from Win32_processor]", "CPU型号"),
	/**
	 * 已使用内存-服务器windows
	 */
	MEMORY_USED_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "memUsedInBytes[{#SNMPVALUE}]", "已使用内存"),
	/**
	 * 内存总量-服务器windows
	 */
	MEMORY_TOTAL_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "vm.memory.size[total]", "内存总量"),
	/**
	 * 内存使用率-服务器windows
	 */
	MEMORY_USELV_KEY_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "vm.memory.size[pused]", "内存使用率"),
	/**
	 * 硬盘使用率-windows
	 */
	DISK_USELV_KEY_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "vfs.fs.size[{#FSNAME},pused]", "硬盘使用率"),
	/**
	 * 硬盘读取速率-服务器windows
	 */
	DISK_READ_REQUEST_BW_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "perf_counter[\"\\LogicalDisk(_Total)\\Disk Read Bytes/sec\"]", "硬盘读取速率"),
	/**
	 * 硬盘写入速率-服务器linux
	 */
	DISK_WRITE_REQUEST_BW_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "perf_counter[\"\\LogicalDisk(_Total)\\Disk Write Bytes/sec\"]", "硬盘写入速率"),
	/**
	 * 网络上行流量-服务器windows
	 */
	NET_UP_FLOW_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "net.if.out[{#IFNAME}]", "网络上行流量"),
	/**
	 * 网络下行流量-服务器windows
	 */
	NET_DOWN_FLOW_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "net.if.in[{#IFNAME}]", "网络下行流量"),
	/**
	 * 网络下行速率(KB/s)windows
	 */
	NET_DOWN_SPEED_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "netInBPS[{#SNMPVALUE}]", "网络下行速率"),
	/**
	 * 网络上行速率(KB/s)windows
	 */
	NET_UP_SPEED_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "netOutBPS[{#SNMPVALUE}]", "网络上行速率"),
	
	
	/**
	 * CPU使用率-云主机-Linux(Agent方式采集)
	 */
	CPU_RATE_VM_LINUX(IMonConsts.MON_VM, "cpuUtil", "CPU使用率_linux"),
	/**
	 * CPU使用率-云主机-Windows(Agent方式采集)
	 */
	CPU_RATE_VM_WINDOWS(IMonConsts.MON_VM, "wmi.get[root\\cimv2,Select LoadPercentage from Win32_processor]", "CPU使用率"),
	/**
	 * CPU负载-云主机(Agent方式采集)
	 */
	CPU_LOAD_VM(IMonConsts.MON_VM, "system.cpu.load[percpu,avg1]", "CPU负载"),
	/**
	 * CPU型号-云主机—Linux(Agent方式采集)
	 */
	CPU_MODEL_VM_LINUX(IMonConsts.MON_VM, "system.hw.cpu[0,full]", "CPU型号_linux"),
	/**
	 *  CPU型号-云主机—Windows(Agent方式采集)
	 */
	CPU_MODEL_VM_WINDOWS(IMonConsts.MON_VM, "wmi.get[root\\cimv2,Select Name from Win32_processor]", "CPU型号"),
	/**
	 * 内存使用率-云主机(Agent方式采集)
	 */
	MEMORY_RATE_VM(IMonConsts.MON_VM, "vm.memory.size[pused]", "内存使用率"),
	/**
	 * 内存总量-云主机(Agent方式采集)
	 */
	TOTAL_MEMORY_VM(IMonConsts.MON_VM, "vm.memory.size[total]", "内存总量"),
	/**
	 * 物理内存已分配容量-云主机(Agent方式采集)
	 */
	AVAILABEL_MEMORY_VM(IMonConsts.MON_VM, "vm.memory.size[used]", "物理内存已分配容量"),
	/**
	 * 磁盘读取速率-云主机(Agent方式采集)
	 */
	DISK_READ_SPEED_VM(IMonConsts.MON_VM, "vfs.dev.read[,sectors]", "磁盘读取速率"),
	/**
	 * 磁盘读取速率-云主机-Linux(Agent方式采集)
	 */
	DISK_READ_SPEED_VM_LINUX(IMonConsts.MON_VM, "vfs.dev.read[,sectors]", "磁盘读取速率_linux"),
	/**
	 * 磁盘读取速率-云主机-Windows(Agent方式采集)
	 */
	DISK_READ_SPEED_VM_WINDOWS(IMonConsts.MON_VM, "perf_counter[\"\\LogicalDisk(_Total)\\Disk Read Bytes/sec\"]", "磁盘读取速率_windowns"),
	/**
	 * 磁盘写入速率-云主机-Linux(Agent方式采集)
	 */
	DISK_WRITE_SPEED_VM_LINUX(IMonConsts.MON_VM, "vfs.dev.write[,sectors]", "磁盘写入速率_linux"),
	/**
	 * 磁盘写入速率-云主机-Windows(Agent方式采集)
	 */
	DISK_WRITE_SPEED_VM_WINDOWS(IMonConsts.MON_VM, "perf_counter[\"\\LogicalDisk(_Total)\\Disk Write Bytes/sec\"]", "磁盘写入速率"),
	/**
	 * 磁盘利用率-云主机(Agent方式采集)
	 */
	DISK_RATE_VM(IMonConsts.MON_VM, "vfs.fs.size[{#FSNAME},pused]", "磁盘利用率"),
	/**
	 * 网络上行流量-云主机(Agent方式采集)
	 */
	NET_UP_FLOW_VM(IMonConsts.MON_VM, "net.if.out[{#IFNAME}]", "网络上行流量"),
	/**
	 * 网络下行流量-云主机(Agent方式采集)
	 */
	NET_DOWN_FLOW_VM(IMonConsts.MON_VM, "net.if.in[{#IFNAME}]", "网络下行流量"),
	/**
	 * 所属用户-云主机(Trapper方式采集)
	 */
	USER_VM(IMonConsts.MON_VM, "vm.user", "所属用户"),
	/**
	 * 所属租户-云主机(Trapper方式采集)
	 */
	TENANT_VM(IMonConsts.MON_VM, "vm.tenant", "所属租户"),
	/**
	 * 浮动IP-云主机(Trapper方式采集)
	 */
	FLOATINGIPS_VM(IMonConsts.MON_VM, "vm.floatingIps", "浮动IP"),
	/**
	 * 浮动IP-云主机(Trapper方式采集)
	 */
	FIXEDIPS_VM(IMonConsts.MON_VM, "vm.fixedIps", "内网IP"),
	/**
	 * 系统类型-云主机-Linux(Agent方式采集)
	 */
	OSTYPE_VM_LINUX(IMonConsts.MON_VM, "system.sw.os", "系统类型"),
	/**
	 * 系统类型-云主机-Windows(Agent方式采集)
	 */
	OSTYPE_VM_WINDOWS(IMonConsts.MON_VM, "wmi.get[root\\cimv2,Select Caption from Win32_OperatingSystem]", "系统类型"),
	/**
	 * 状态-云主机(Agent方式采集)
	 */
	STATUS_VM(IMonConsts.MON_VM, "vm.status", "云主机状态"),
	/**
	 * 系统详情-云主机(Agent方式采集)
	 */
	UPTIME_VM(IMonConsts.MON_VM, "system.uptime", "系统运行时长"),
	
	/**
	 * 虚拟CPU个数-云主机(Agent方式采集)
	 */
	CPUS_VM(IMonConsts.MON_VM, "vm.vcpus", "虚拟CPU个数"),
	/**
	 * 磁盘容量-云主机(Agent方式采集)
	 */
	DISK_VM(IMonConsts.MON_VM, "vm.disk", "磁盘容量"),
	/**
	 * 所属计算节点-云主机(Agent方式采集)
	 */
	HOSTS_VM(IMonConsts.MON_VM, "vm.host", "所属计算节点"),
	

	
	/**
	 * 系统信息-服务器Linux
	 */
	UPTIME_VM_LINUX(IMonConsts.MON_SERVER_LINUX, "osType", "系统信息"),
	/**
	 * 已使用内存-服务器linux
	 */
	MEMORY_USED_LINUX(IMonConsts.MON_SERVER_LINUX, "memUsedInBytes[{#SNMPVALUE}]", "已使用内存"),
	/**
	 * 内存总量-服务器Linux
	 */
	TOTAL_MEMORY(IMonConsts.MON_SERVER_LINUX, "memTotalInBytes[{#SNMPVALUE}]", "内存总量"),
	/**
	 * 内存总量-服务器windows
	 */
	TOTAL_MEMORY_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "memTotalInBytes[{#SNMPVALUE}]", "内存总量"),
	/**
	 * 内存使用率-服务器linux
	 */
	MEMORY_USELV_KEY(IMonConsts.MON_SERVER_LINUX, "memUsage[{#SNMPVALUE}]", "内存使用率"),
	/**
	 * 磁盘已使用量linux
	 */
	USED_DISK_SPACE_ON(IMonConsts.MON_SERVER_LINUX, "fsUsedInBytes[{#SNMPINDEX}]", "磁盘已使用量"),
	/**
	 * 磁盘已使用量windows
	 */
	USED_DISK_SPACE_ON_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "fsUsage[{#SNMPINDEX}]", "磁盘已使用量"),
	/**
	 * 磁盘总量linux
	 */
	TOTAL_DISK_SPACE_ON(IMonConsts.MON_SERVER_LINUX, "fsTotalInBytes[{#SNMPINDEX}]", "磁盘总量"),
	/**
	 * 磁盘总量windows
	 */
	TOTAL_DISK_SPACE_ON_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "fsTotalInBytes[{#SNMPINDEX}]", "磁盘总量"),
	/**
	 * 硬盘使用率-linux
	 */
	DISK_USELV_KEY(IMonConsts.MON_SERVER_LINUX, "fsUsage[{#SNMPINDEX}]", "硬盘使用率"),
	/**
	 * 硬盘读取速率(KB/s)-服务器linux
	 */
	DISK_READ_SPEED(IMonConsts.MON_SERVER_LINUX, "diskReadBPS[{#SNMPVALUE}]", "硬盘读取速率"),

	/**
	 * 网络上行速率(KB/s)linux
	 */
	NET_UP_SPEED(IMonConsts.MON_SERVER_LINUX, "netOutBPS[{#SNMPVALUE}]", "网络上行速率"),
	
	/**
	 * 网络上行速率(KB/s) CISCO
	 */
	NET_UP_SPEED_NET_CISCO(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]", "网络上行速率"),
	/**
	 * 网络下行速率(KB/s)linux
	 */
	NET_DOWN_SPEED(IMonConsts.MON_SERVER_LINUX, "netInBPS[{#SNMPVALUE}]", "网络下行速率"),
	/**
	 * 网络下行速率(KB/s) CISCO
	 */
	NET_DOWN_SPEED_NET_CISCO(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]", "网络下行速率"),
	/**
	 * 网络上行速率最大值(KB/s)linux
	 */
	NET_UP_SPEED_MAX(IMonConsts.MON_SERVER_LINUX, "netOutMax[{#SNMPVALUE}]", "网络上行速率最大值"),
	/**
	 * 网络上行速率最大值(KB/s)windows
	 */
	NET_UP_SPEED_MAX_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "netOutMax[{#SNMPVALUE}]", "网络上行速率最大值"),
	/**
	 * 网络下行速率最大值(KB/s)linux
	 */
	NET_DOWN_SPEED_MAX(IMonConsts.MON_SERVER_LINUX, "netInMax[{#SNMPVALUE}]", "网络下行速率最大值"),
	/**
	 * 网络下行速率最大值(KB/s)windows
	 */
	NET_DOWN_SPEED_MAX_WINDOWS(IMonConsts.MON_SERVER_WINDOWS, "netInBPS[{#SNMPVALUE}]", "网络下行速率最大值"),
	/**
	 * 网络设备最大带宽 Cisco
	 */
	NET_BANDWIDTH_NET_CISCO(IMonConsts.MON_NET_CISCO, "snmp.agent[IF-MIB::ifHighSpeed.{#SNMPINDEX}]", "网络设备最大带宽"),

	
	/**
	 * 数据库MySQL_当前版本
	 */
	DBVERSION_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.version[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据库版本"),
	/**
	 * 数据库MySQL_表空间列表
	 */
	DATABASES_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.databases[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "表空间列表"),
	/**
	 * 数据库MySQL_运行时间
	 */
	LAST_START_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "运行时间"),
	/**
	 * 数据库MySQL_系统会话个数
	 */
	THREADS_CONNECTED_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.threads_connected[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "系统会话数"),
	/**
	 * 数据库MySQL_运行状态
	 */
	IS_ALIVE_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "运行状态"),
	/**
	 * 数据库MySQL_查询量/秒(QPS)
	 */
	QUERIES_PER_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.questions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "每秒查询量"),
	/**
	 * 数据库MySQL_并发线程数
	 */
	THREADS_RUNNING_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.threads_running[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "并发线程数"),
	/**
	 * 数据库MySQL_缓存总量
	 */
	TOTAL_BLOCKS_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.qcache_total[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "查询缓存总量"),
	/**
	 * 数据库MySQL_缓存可用量
	 */
	FREE_MEMORY_MYSQL(IMonConsts.MON_DB_MySQL, "mysql.qcache_free[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "查询缓存可用量"),
	

	/**
	 * 数据库Sql_Server_缓存命中率
	 */
	DB_SQLSERVER_CACHEHIT(IMonConsts.MON_DB_SQLSERVER, "mssql.cachehit[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "缓存命中率"),
	/**
	 * 数据库Sql_Server_数据库大小
	 */
	DB_SQLSERVER_DBSIZE(IMonConsts.MON_DB_SQLSERVER, "mssql.dbsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据库大小"),
	/**
	 * 数据库Sql_Server_IO挂起数目
	 */
	DB_SQLSERVER_IOPENDING(IMonConsts.MON_DB_SQLSERVER, "mssql.iopending[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "IO挂起数目"),
	/**
	 * 数据库Sql_Server_日志文件大小
	 */
	DB_SQLSERVER_LOGSIZE(IMonConsts.MON_DB_SQLSERVER, "mssql.logsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "日志文件大小"),
	/**
	 * 数据库Sql_Server_日志占用空间大小
	 */
	DB_SQLSERVER_LOGUSEDSIZE(IMonConsts.MON_DB_SQLSERVER, "mssql.logusedsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "日志占用空间大小"),
	/**
	 * 数据库Sql_Server_页读出速率
	 */
	DB_SQLSERVER_PAGEREADS(IMonConsts.MON_DB_SQLSERVER, "mssql.pagereads[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "页读出速率"),
	/**
	 * 数据库Sql_Server_页写入速率
	 */
	DB_SQLSERVER_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.pagewrites[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "页写入速率"),
	/**
	 * 数据库Sql_Server_系统会话个数
	 */
	SESSIONS_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.sessions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "系统会话个数"),
	/**
	 * 数据库Sql_Server_CPU平均使用率
	 */
	CPURATE_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.cpu[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "CPU平均使用率"),
	/**
	 * 数据库Sql_Server_内存使用量
	 */
	MEMORY_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.memory[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "内存使用量"),
	/**
	 * 数据库Sql_Server_SQL Agent状态
	 */
	SQL_AGENT_STATUS_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.sql_agent_status[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "SQL Agent状态"),
	/**
	 * 数据库Sql_Server_读取IO速率
	 */
	PAGEREADS_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.pagereads[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "读取IO速率"),
	/**
	 * 数据库Sql_Server_写入IO速率
	 */
	PAGEWRITES_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.pagewrites[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "写入IO速率"),
	/**
	 * 数据库Sql_Server_运行状态
	 */
	IS_ALIVE_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.is_alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "运行状态"),
	/**
	 * 数据库Sql_Server_日志空间利用率
	 */
	LOG_USED_PERCENT_PAGEWRITES(IMonConsts.MON_DB_SQLSERVER, "mssql.log_used_percent[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "日志空间利用率"),
	
	
	/**
	 * 数据库Oracle_磁盘上读取数据块的数量
	 */
	DBPHYSICALREAD_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.dbphysicalread[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "磁盘上读取数据块的数量"),
	/**
	 * 数据库Oracle_数据文件读取
	 */
	DATAFILE_READS_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.phio_datafile_reads[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据文件读取"),
	/**
	 * 数据库Oracle_数据文件写入
	 */
	DATAFILE_WRITES_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.phio_datafile_writes[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据文件写入"),
	/**
	 * 数据库Oracle_缓冲区命中率
	 */
	HITRATIO_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.dbhitratio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "缓冲区命中率"),
	/**
	 * 数据库Oracle_当前版本
	 */
	VERSION_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.dbversion[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "当前版本"),
	/**
	 * 数据库Oracle_活动会话数
	 */
	SESSION_ACTIVE_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.session_active[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "活动会话数"),
	/**
	 * 数据库Oracle_不活动会话数
	 */
	SESSION_INACTIVE_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.session_inactive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "不活动会话数"),
	/**
	 * 数据库Oracle_系统会话数
	 */
	SESSION_SYSTEM_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.session_system[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "系统会话数"),
	/**
	 * 数据库Oracle_缓冲池命中率
	 */
	BUFFHITRATIO_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.BuffHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "缓冲池命中率"),
	/**
	 * 数据库Oracle_当前会话数
	 */
	USERS_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.session[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "当前会话数"),
	/**
	 * 数据库Oracle_最大会话量
	 */
	MAXSESSION_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.maxsession[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "最大会话量"),
	/**
	 * 数据库Oracle_运行状态
	 */
	ALIVE_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "运行状态"),
	/**
	 * 数据库Oracle_运行时间
	 */
	UPTIME_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "运行时间"),
	/**
	 * 数据库Oracle_表空间利用率
	 */
	TBL_USE_RATE_ORACLE(IMonConsts.MON_DB_Oracle, "oracle.tbl_use_rate[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "表空间利用率"),
	
	/**
	 * 数据库DB2事务数
	 */
	DB_DB2_COMMITS_ATTEMPTED(IMonConsts.MON_DB_DB2, "db2.ApplCommitsAttempted[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "事务数"),
	/**
	 * 数据库DB2排序时间
	 */
	DB_DB2_SORTTIME(IMonConsts.MON_DB_DB2, "db2.SortTime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "排序时间"),
	/**
	 * 数据库DB2日志可用空间大小
	 */
	DB_DB2_TOTALLOGSPAVAIL(IMonConsts.MON_DB_DB2, "db2.TotalLogSpAvail[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "日志可用空间大小"),
	/**
	 * 数据库DB2日志大小
	 */
	DB_DB2_TOTALLOGSPUSED(IMonConsts.MON_DB_DB2, "db2.TotalLogSpUsed[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "日志大小"),
	/**
	 * 数据库DB2数据库版本
	 */
	DB_DB2_VERSION(IMonConsts.MON_DB_DB2, "db2.Version[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据库版本"),
	/**
	 * 数据库DB2数据库运行状态
	 */
	DB_DB2_ALIVE(IMonConsts.MON_DB_DB2, "db2.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "数据库运行状态"),
	/**
	 * 数据库DB2系统会话数
	 */
	DB_DB2_APPLCOUNT(IMonConsts.MON_DB_DB2, "db2.ApplCount[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "系统会话数"),
	/**
	 * 数据库DB2缓冲池命中率
	 */
	DB_DB2_BPHITRATIO(IMonConsts.MON_DB_DB2, "db2.BpHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "缓冲池命中率"),
	/**
	 * 数据库DB2锁平均等待时间
	 */
	DB_DB2_AVEWAITFORLOCK(IMonConsts.MON_DB_DB2, "db2.AveWaitForLock[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]", "锁平均等待时间"),
	
	/**
	 * 数据库DM_正常运行时间
	 */
	UPTIME_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.uptime", "正常运行时间"),
	/**
	 * 数据库DM_活动事务
	 */
	TRANSACTION_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.active_transaction", "活动事务"),
	/**
	 * 数据库DM_进程状态
	 */
	SERVICE_STATE_DM(IMonConsts.MON_DB_DM, "service_state[DmServiceDMSERVER]", "进程状态"),
	/**
	 * 数据库DM_会话连接时间超时个数
	 */
	LINKD_TIME_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.session_linkd_time", "会话连接时间超时个数"),
	/**
	 * 数据库DM_会话对对象锁定时间超时个数
	 */
	LOCK_TIME_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.session_lock_time", "会话对对象锁定时间超时个数"),
	/**
	 * 数据库DM_当前版本
	 */
	VERSION_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.version", "当前版本"),
	/**
	 * 数据库DM_数据库实例状态
	 */
	STATUS_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.status", "数据库实例状态"),
	/**
	 * 数据库DM_表空间使用率
	 */
	TABLE_USAGE_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.table_usage", "表空间使用率 "),
	/**
	 * 数据库DM_临时表空间使用率
	 */
	TEMP_USAGE_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.temp_table_usage", "临时表空间使用率"),
	/**
	 * 数据库DM_当前会话数
	 */
	SESSIONS_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.sessions", "当前会话数"),
	/**
	 * 数据库DM_活跃会话数
	 */
	ACTIVE_SESSION_DM(IMonConsts.MON_DB_DM, "DBforBIX.DM.active_sessions", "活跃会话数"),
	
	/**
	 * 数据库MongoDB使用的物理内存大小
	 */
	DB_MONGODB_MEM_RESIDENT(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[mem,{$MONGOIP},resident]", "使用的物理内存大小"),
	/**
	 * 数据库MongoDB使用的虚拟内存大小
	 */
	DB_MONGODB_MEM_VIRTUAL(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[mem,{$MONGOIP},virtual]", "使用的虚拟内存大小"),
	/**
	 * 数据库MongoDB后台刷新时间
	 */
	DB_MONGODB_BACKGROUNDFLUSHING_TOTAL_MS(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[backgroundFlushing,{$MONGOIP},total_ms]", "后台刷新时间"),
	/**
	 * 数据库MongoDB实例刷新数据到磁盘的操作数
	 */
	DB_MONGODB_BACKGROUNDFLUSHING_FLUSHES(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[backgroundFlushing,{$MONGOIP},flushes]", "实例刷新数据到磁盘的操作数"),
	/**
	 * 数据库MongoDB平均每次刷新执行时间
	 */
	DB_MONGODB_BACKGROUNDFLUSHING_AVERAGE_MS(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[backgroundFlushing,{$MONGOIP},average_ms]", "平均每次刷新执行时间"),
	/**
	 * 数据库MongoDB执行delete次数
	 */
	DB_MONGODB_OPCOUNTERS_DELETE(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},delete]", "执行delete次数"),
	/**
	 * 数据库MongoDB执行insert次数
	 */
	DB_MONGODB_OPCOUNTERS_INSERT(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},insert]", "执行insert次数"),
	/**
	 * 数据库MongoDB执行query次数
	 */
	DB_MONGODB_OPCOUNTERS_QUERY(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},query]", "执行query次数"),
	/**
	 * 数据库MongoDB执行update次数
	 */
	DB_MONGODB_OPCOUNTERS_UPDATE(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},update]", "执行update次数"),
	/**
	 * 数据库MongoDB操作数
	 */
	DB_MONGODB_OPCOUNTERS_COMMAND(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},command]", "操作数"),
	/**
	 * 数据库MongoDB数据量值
	 */
	DB_MONGODB_MEM_MAPPED(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[mem,{$MONGOIP},mapped]", "数据量值"),
	/**
	 * 数据库MongoDB最后一次刷新执行时间
	 */
	DB_MONGODB_BACKGROUNDFLUSHING_LAST_MS(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[backgroundFlushing,{$MONGOIP},last_ms]", "最后一次刷新执行时间"),
	/**
	 * 数据库MongoDB游标执行getMore操作数
	 */
	DB_MONGODB_OPCOUNTERS_GETMORE(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[opcounters,{$MONGOIP},getmore]", "游标执行getMore操作数"),
	/**
	 * 数据库MongoDB系统会话个数
	 */
	DB_MONGODB_CONNECTIONS_CURRENT(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[connections,{$MONGOIP},current]", "系统会话个数"),
	/**
	 * 数据库MongoDB索引不是在内存中被命中的次数
	 */
	DB_MONGODB_INDEXCOUNTERS_MISSES(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[indexCounters,{$MONGOIP},misses]", "索引不是在内存中被命中的次数"),
	/**
	 * 数据库MongoDB索引在内存中的命中次数
	 */
	DB_MONGODB_INDEXCOUNTERS_HITS(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[indexCounters,{$MONGOIP},hits]", "索引在内存中的命中次数"),
	/**
	 * 数据库MongoDB索引数
	 */
	DB_MONGODB_INDEX_COUNT(IMonConsts.MON_DB_MONGODB, "MongoDB.Index[count,{$MONGOIP}]", "索引数"),
	/**
	 * 数据库MongoDB索引计数器被重置的次数
	 */
	DB_MONGODB_INDEXCOUNTERS_RESETS(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[indexCounters,{$MONGOIP},resets]", "索引计数器被重置的次数"),
	/**
	 * 数据库MongoDB网络发送字节数
	 */
	DB_MONGODB_NETWORK_BYTESOUT(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[network,{$MONGOIP},bytesOut]", "网络发送字节数"),
	/**
	 * 数据库MongoDB网络接收字节数
	 */
	DB_MONGODB_NETWORK_BYTESIN(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[network,{$MONGOIP},bytesIn]", "网络接收字节数"),
	/**
	 * 数据库MongoDB访问索引次数
	 */
	DB_MONGODB_INDEXCOUNTERS_ACCESSES(IMonConsts.MON_DB_MONGODB, "MongoDB.Status[indexCounters,{$MONGOIP},accesses]", "访问索引次数"),
	/**
	 * 数据库MongoDB数据库运行状态
	 */
	DB_MONGODB_STATUS(IMonConsts.MON_DB_MONGODB, "MongoDB.status[{$MONGOIP}]", "数据库运行状态"),

	/**
	 * 中间件Tomcat_当前线程数
	 */
	CURRENTTHREADCOUNT_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.currentThreadCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]","当前线程数"),
	/**
	 * 中间件Tomcat_最大线程数
	 */
	MAXTHREADS_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.maxThreads[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "最大线程数"),
	/**
	 * 中间件Tomcat_每秒请求错误数
	 */
	ERRORCOUNT_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "每秒请求错误数"),
	/**
	 * 中间件Tomcat_活动会话数
	 */
	ACTIVESESSIONS_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.activeSessions[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "活动会话数"),
	/**
	 * 中间件Tomcat_总会话数
	 */
	TOTALSESSION_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.sessionCounter[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "总会话数"),
	/**
	 * 中间件Tomcat_运行时间
	 */
	UPTIME_TOMCAT(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.uptime[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "运行时间"),
	/**
	 * 中间件Tomcat_堆当前使用量
	 */
	HEAPMEMORYUSAGEUSED(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.heapMemoryUsageUsed[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "堆当前使用量"),
	/**
	 * 中间件Tomcat_堆最大使用量
	 */
	HEAPMEMORYUSAGEMAX(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.heapMemoryUsageMax[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "堆最大使用量"),
	/**
	 * 中间件Tomcat_繁忙线程数
	 */
	CURRENTTHREADSBUSY(IMonConsts.MON_MIDDLE_TOMCAT, "tomcat.currentThreadsBusy[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]", "繁忙线程数"),
	
	/**
	 * 中间件IIS_ASP.Net应用重启次数
	 */
	MID_IIS_ASPNET_APPLICATION_RESTARTS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\ASP.NET\\Application Restarts\"]", "ASP.Net应用重启次数"),
	/**
	 * 中间件IIS_ASP.Net当前请求数
	 */
	MID_IIS_ASPNET_REQUESTS_CURRENT(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\ASP.NET\\Requests Current\"]", "ASP.Net当前请求数"),
	/**
	 * 中间件IIS_ASP.Net每秒请求数
	 */
	MID_IIS_ASPNET_APPLICATIONS_TOTAL_REQUESTS_SEC(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\ASP.NET Applications(__Total__)\\Requests/Sec\"]", "ASP.Net每秒请求数"),
	/**
	 * 中间件IIS_ASP.Net进程重启次数
	 */
	MID_IIS_ASPNET_WORKER_PROCESS_RESTARTS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\ASP.NET\\Worker Process Restarts\"]", "ASP.Net进程重启次数"),
	/**
	 * 中间件IIS_ASP.Net错误总次数
	 */
	MID_IIS_ASPNET_APPLICATIONS_TOTAL_ERRORS_TOTAL(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\ASP.NET Applications(__Total__)\\Errors Total\"]", "ASP.Net错误总次数"),
	/**
	 * 中间件IIS_IIS Get 请求数/秒
	 */
	MID_IIS_WEBSERVICE_TOTAL_TOTAL_GET_REQUESTS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Total Get Requests\"]", "IIS Get 请求数/秒"),
	/**
	 * 中间件IIS_IIS Head 请求数/秒
	 */
	MID_IIS_WEBSERVICE_TOTAL_TOTAL_HEAD_REQUESTS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Total Head Requests\"]", "IIS Head 请求数/秒"),
	/**
	 * 中间件IIS_IIS Post 请求数/秒
	 */
	MID_IIS_WEBSERVICE_TOTAL_TOTAL_POST_REQUESTS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Total Post Requests\"]", "IIS Post 请求数/秒"),
	/**
	 * 中间件IIS_IIS当前匿名用户数
	 */
	MID_IIS_WEBSERVICE_TOTAL_CURRENT_ANONYMOUS_USERS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Current Anonymous Users\"]", "IIS当前匿名用户数"),
	/**
	 * 中间件IIS_IIS当前连接数
	 */
	MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Current Connections\"]", "IIS当前连接数"),
	/**
	 * 中间件IIS_IIS当前非匿名用户数
	 */
	MID_IIS_WEBSERVICE_TOTAL_CURRENT_NONANONYMOUS_USERS(IMonConsts.MON_MIDDLE_IIS, "perf_counter[\"\\Web Service(_Total)\\Current NonAnonymous Users\"]", "IIS当前非匿名用户数"),
	/**
	 * 中间件IIS_Microsoft IIS: 服务状态
	 */
	MID_IIS_W3SVC(IMonConsts.MON_MIDDLE_IIS, "service_state[W3SVC]", "Microsoft IIS: 服务状态"),
	
	/**
	 * 中间件Weblogic_AdminServer当前JMS服务数
	 */
	MID_WEBLOGIC_JMSSERVERSCURRENTCOUNT(IMonConsts.MON_MIDDLE_WEBLOGIC, "weblogic.jmsServersCurrentCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]", "AdminServer当前JMS服务数"),
	/**
	 * 中间件Weblogic_AdminServer当前JMS连接数
	 */
	MID_WEBLOGIC_JMSCONNECTIONSCURRENTCOUNT(IMonConsts.MON_MIDDLE_WEBLOGIC, "weblogic.jmsConnectionsCurrentCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]", "AdminServer当前JMS连接数"),
	/**
	 * 中间件Weblogic_AdminServer自启动以来JMS最大连接数
	 */
	MID_WEBLOGIC_JMSCONNECTIONSHIGHCOUNT(IMonConsts.MON_MIDDLE_WEBLOGIC, "weblogic.jmsConnectionsHighCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]", "AdminServer自启动以来JMS最大连接数"),
	/**
	 * 中间件Weblogic_AdminServer自启动以来最大JMS服务数
	 */
	MID_WEBLOGIC_JMSSERVERSHIGHCOUNT(IMonConsts.MON_MIDDLE_WEBLOGIC, "weblogic.jmsServersHighCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]", "AdminServer自启动以来最大JMS服务数"),
	/**
	 * 中间件Weblogic_启动时间
	 */
	MID_WEBLOGIC_STARTTIME(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.startTime[0,{$JMX_PORT},{$JAVAHOME}]", "启动时间"),
	/**
	 * 中间件Weblogic_堆当前使用量
	 */
	MID_WEBLOGIC_HEAPMEMORYUSAGEUSED(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.heapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "堆当前使用量"),
	/**
	 * 中间件Weblogic_堆最大使用量
	 */
	MID_WEBLOGIC_HEAPMEMORYUSAGEMAX(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.heapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "堆最大使用量"),
	/**
	 * 中间件Weblogic_持久堆当前使用量
	 */
	MID_WEBLOGIC_PSPERMGENUSAGEUSED(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.psPermGenUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "持久堆当前使用量"),
	/**
	 * 中间件Weblogic_持久堆最大使用量
	 */
	MID_WEBLOGIC_PSPERMGENUSAGEMAX(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.psPermGenUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "持久堆最大使用量"),
	/**
	 * 中间件Weblogic_运行时间
	 */
	MID_WEBLOGIC_UPTIME(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.uptime[0,{$JMX_PORT},{$JAVAHOME}]", "运行时间"),
	/**
	 * 中间件Weblogic_非堆当前使用量
	 */
	MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.nonHeapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "非堆当前使用量"),
	/**
	 * 中间件Weblogic_非堆最大使用量
	 */
	MID_WEBLOGIC_NONHEAPMEMORYUSAGEMAX(IMonConsts.MON_MIDDLE_WEBLOGIC, "tomcat.nonHeapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "非堆最大使用量"),
	
	/**
	 * 中间件Websphere_JVM历史最大线程数
	 */
	MID_WEBSPHERE_JVMPEAKTHREADCOUNT(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.jvmPeakThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]", "JVM历史最大线程数"),
	/**
	 * 中间件Websphere_JVM活动守护线程数
	 */
	MID_WEBSPHERE_JVMDAEMONTHREADCOUNT(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.jvmDaemonThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]", "JVM活动守护线程数"),
	/**
	 * 中间件Websphere_JVM活动线程总数
	 */
	MID_WEBSPHERE_JVMTHREADCOUNT(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.jvmThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]", "JVM活动线程总数"),
	/**
	 * 中间件Websphere_启动时间
	 */
	MID_WEBSPHERE_STARTTIME(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.startTime[0,{$JMX_PORT},{$JAVAHOME}]", "启动时间"),
	/**
	 * 中间件Websphere_堆当前使用量
	 */
	MID_WEBSPHERE_HEAPMEMORYUSAGEUSED(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.heapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "堆当前使用量"),
	/**
	 * 中间件Websphere_堆最大使用量
	 */
	MID_WEBSPHERE_HEAPMEMORYUSAGEMAX(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.heapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "堆最大使用量"),
	/**
	 * 中间件Websphere_JVM堆当前使用量
	 */
	MID_WEBSPHERE_JVMHEAPUSAGEUSED(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.jvmHeapUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "JVM堆当前使用量"),
	/**
	 * 中间件Websphere_JVM堆最大使用量
	 */
	MID_WEBSPHERE_JVMHEAPUSAGEMAX(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.jvmHeapUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "JVM堆最大使用量"),
	/**
	 * 中间件Websphere_杂项非堆当前使用量
	 */
	MID_WEBSPHERE_MISCELLANEOUSNONEHEAPUSAGEUSED(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.miscellaneousNoneHeapUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "杂项非堆当前使用量"),
	/**
	 * 中间件Websphere_杂项非堆最大使用量
	 */
	MID_WEBSPHERE_MISCELLANEOUSNONEHEAPUSAGEMAX(IMonConsts.MON_MIDDLE_WEBSPHERE, "websphere.miscellaneousNoneHeapUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "杂项非堆最大使用量"),
	/**
	 * 中间件Websphere_运行时间
	 */
	MID_WEBSPHERE_UPTIME(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.uptime[0,{$JMX_PORT},{$JAVAHOME}]", "运行时间"),
	/**
	 * 中间件Websphere_非堆当前使用量
	 */
	MID_WEBSPHERE_NONHEAPMEMORYUSAGEUSED(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.nonHeapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]", "非堆当前使用量"),
	/**
	 * 中间件Websphere_非堆最大使用量
	 */
	MID_WEBSPHERE_NONHEAPMEMORYUSAGEMAX(IMonConsts.MON_MIDDLE_WEBSPHERE, "tomcat.nonHeapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]", "非堆最大使用量"),

	/**
	 * 云服务-实例个数
	 */
	VM_COUNT(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.count", "实例个数"),
	/**
	 * 云服务-内存总量
	 */
	VM_MEMORY_TOTAL(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.memory.total", "内存总量"),
	/**
	 * 云服务-内存使用量
	 */
	VM_MEMORY_USED(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.memory.used", "内存使用量"),
	
	/**
	 * 云服务-租户个数
	 */
	VM_TENANT_COUNT(IMonConsts.MON_CLOUD_CONTROLER, "iaas.tenant.count", "租户个数"),
	/**
	 * 云服务-告警个数
	 */
	VM_ALERT_COUNT(IMonConsts.MON_CLOUD_CONTROLER, "iaas.alert.count", "告警个数"),
	/**
	 * 云服务-宿主机个数
	 */
	VM_HYPERVIOR_COUNT(IMonConsts.MON_CLOUD_CONTROLER, "iaas.hypervisorserver.count", "宿主机个数"),
	/**
	 * 云服务-镜像总个数
	 */
	VM_IMAGE_COUNT(IMonConsts.MON_CLOUD_CONTROLER, "iaas.image.count", "镜像总个数"),
	/**
	 * 云服务-虚拟内核已使用
	 */
	VM_CORE_USED(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.core.used", "虚拟内核已使用 "),
	/**
	 * 云服务-卷存储已使用
	 */
	VM_VOL_USED(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vol.used", "卷存储已使用"),
	/**
	 * 云服务-实例个数-故障
	 */
	VM_COUNT_ERROR(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.count.error", "实例个数-故障 "),
	/**
	 * 云服务-虚拟内核总量
	 */
	VM_CORE_TOTAL(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.core.total", "虚拟内核总量"),
	/**
	 * 云服务-实例个数-停止
	 */
	VM_COUNT_STOP(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.count.shutoff", "实例个数-停止"),
	VM_COUNT_ACTIVE(IMonConsts.MON_CLOUD_CONTROLER, "iaas.vm.count.active", "实例个数-活动"),
	/**
	 * 云服务-已用存储大小
	 */
	VM_CINDER_USED(IMonConsts.MON_CLOUD_CONTROLER, "iaas.cinder.used", "存储已用大小"),
	/**
	 * 云服务-存储大小
	 */
	VM_CINDER_TOTAL(IMonConsts.MON_CLOUD_CONTROLER, "iaas.cinder.total", "存储总大小"),
	
	/**
	 * 云服务计算节点-libvirt进程数
	 */
	CLOUD_COMPUTER_PROCNUM_LIBVIRTD(IMonConsts.MON_CLOUD_COMPUTER, "proc.num[libvirtd]", "libvirt进程数"),
	/**
	 * 云服务计算节点-ovs进程数
	 */
	CLOUD_COMPUTER_PROCNUM_OVS(IMonConsts.MON_CLOUD_COMPUTER, "proc.num[python,,,neutron-openvswitch-agent]", "ovs进程数"),
	/**
	 * 云服务计算节点-compute进程数
	 */
	CLOUD_COMPUTER_PROCNUM_COMPUTE(IMonConsts.MON_CLOUD_COMPUTER, "proc.num[python,,,nova-compute]", "compute进程数"),
	/**
	 * 云服务计算节点-ceilometer_compute进程数
	 */
	CLOUD_COMPUTER_PROCNUM_CEILOMETER_COMPUTE(IMonConsts.MON_CLOUD_COMPUTER, "proc.num[python,,,ceilometer-agent-compute]", "ceilometer_compute进程数"),

	
	
	/**
	 * Ceph-mon节点数量
	 */
	CEPH_MON(IMonConsts.MON_CLOUD_CEPH, "ceph.mon", "mon节点数量"),
	/**
	 * Ceph-mds状态
	 */
	CEPH_MDS(IMonConsts.MON_CLOUD_CEPH, "ceph.mds", "mds节点数量"),
	/**
	 * Ceph-osd状态
	 */
	CEPH_OSD(IMonConsts.MON_CLOUD_CEPH, "ceph.osd", "osd节点数量"),	
	/**
	 * Ceph-PG活跃数
	 */
	CEPH_ACTIVE(IMonConsts.MON_CLOUD_CEPH, "ceph.active", "PG活跃数"),
	/**
	 * Ceph-pg总大小
	 */
	CEPH_PG_TOTAL(IMonConsts.MON_CLOUD_CEPH, "ceph.pgtotal", "pg总大小"),
	
	/**
	 * Ceph-处于stale（未知）状态的pg（归置组）的数量
	 */
	CEPH_STALE(IMonConsts.MON_CLOUD_CEPH, "ceph.stale", "处于未知状态的归置组的数量"),
	/**
	 * Ceph-运行且在集群内的osd节点在全部osd节点中的比例
	 */
	CEPH_OSD_IN(IMonConsts.MON_CLOUD_CEPH, "ceph.osd_in", "运行且在集群内的osd节点在全部osd节点中的比例"),
	/**
	 * Ceph-运行的osd节点在全部osd节点中的比例
	 */
	CEPH_OSD_UP(IMonConsts.MON_CLOUD_CEPH, "ceph.osd_up", "运行的osd节点在全部osd节点中的比例"),
	
	
	/**
	 * 通用网络设备-设备名称
	 */
	COMMON_NET_SYSNAME(IMonConsts.MON_COMMON_NET, "sysName", "设备名称"),
	/**
	 * 通用网络设备-运行时间
	 */
	COMMON_NET_SYSUPTIME(IMonConsts.MON_COMMON_NET, "sysUpTime", "运行时间"),
	/**
	 * 通用网络设备-网口数量
	 */
	COMMON_NET_IFNUMBER(IMonConsts.MON_COMMON_NET, "ifNumber", "接口数量"),
	/**
	 * 通用网络设备-网络下行丢包数
	 */
	COMMON_NET_IFINERRORS(IMonConsts.MON_COMMON_NET, "ifInErrors[{#SNMPVALUE}]", "网络下行错包数"),
	/**
	 * 通用网络设备-网络上行丢包数
	 */
	COMMON_NET_IFOUTERRORS(IMonConsts.MON_COMMON_NET, "ifOutErrors[{#SNMPVALUE}]", "网络上行错包数"),
	/**
	 * 通用网络设备-网络下行速率
	 */
	COMMON_NET_IFINOCTETS(IMonConsts.MON_COMMON_NET, "ifInOctets[{#SNMPVALUE}]", "网络下行速率"),
	/**
	 * 通用网络设备-网络上行速率
	 */
	COMMON_NET_IFOUTOCTETS(IMonConsts.MON_COMMON_NET, "ifOutOctets[{#SNMPVALUE}]", "网络上行速率"),
	/**
	 * 通用网络设备-网络设备最大带宽 
	 */
	COMMON_NET_NET_BANDWIDTH(IMonConsts.MON_COMMON_NET, "ifHighSpeed[{#SNMPVALUE}]", "通用网络设备最大带宽"),
	
	
	/**
	 * 云服务网络节点-dhcp进程数
	 */
	CLOUD_NETWORK_PROCNUM_DHCP(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-dhcp-agent]", "dhcp进程数"),
	/**
	 * 云服务网络节点-l3进程数
	 */
	CLOUD_NETWORK_PROCNUM_L3(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-l3-agent]", "l3进程数"),
	/**
	 * 云服务网络节点-lbaas进程数
	 */
	CLOUD_NETWORK_PROCNUM_LBAAS(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-lbaas-agent]", "lbaas进程数"),
	/**
	 * 云服务网络节点-metadata进程数
	 */
	CLOUD_NETWORK_PROCNUM_METADATA(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-metadata-agent]", "metadata进程数"),

	/**
	 * 云服务网络节点-ovs进程数
	 */
	CLOUD_NETWORK_PROCNUM_OVS(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-openvswitch-agent]", "ovs进程数"),
	/**
	 * 云服务网络节点-vpn进程数
	 */
	CLOUD_NETWORK_PROCNUM_VPN(IMonConsts.MON_CLOUD_NETWORK, "proc.num[python,,,neutron-vpn-agent]", "vpn进程数"),
	/**
	 * 云服务网络节点-ovs_vswitchd进程数
	 */
	CLOUD_NETWORK_PROCNUM_OVS_VSWITCHD(IMonConsts.MON_CLOUD_NETWORK, "proc.num[ovs-vswitchd]", "ovs_vswitchd进程数"),

	/**
	 * 云服务网络节点-httpd进程数
	 */
	CLOUD_WEB_PROCNUM_HTTPD(IMonConsts.MON_CLOUD_WEB, "proc.num[httpd]", "httpd进程数"),

	/**
	 * 集群-数据中心数量
	 */
	DCNAME_CLUSTER(IMonConsts.MON_CLUSTER, "dcName[{#ID}]", "数据中心数量"),
	/**
	 * 集群-事件数量
	 */
	DCEVENTS_CLUSTER(IMonConsts.MON_CLUSTER, "dcEvents[{#ID}]", "事件数量"),
	/**
	 * 集群-集群个数
	 */
	CLUSTERDCNAME_CLUSTER(IMonConsts.MON_CLUSTER, "clusterDcName[{#ID}]", "集群个数"),
	/**
	 * 集群-最大内存数
	 */
	CLUSTERMAXMEMREQUEST_CLUSTER(IMonConsts.MON_CLUSTER, "clusterMaxMemRequest[{#ID}]", "最大内存数"),

	/**
	 * 桌面云-活动的云主机数
	 */
	HOSTACTIVEVMCOUNT_DESKTOPC(IMonConsts.MON_DESKTOPC, "hostActiveVmCount[{#ID}]", "活动的云主机数"),
	/**
	 * 桌面云-进程占用的内存
	 */
	HOSTMEMORYUSAGE_DESKTOPC(IMonConsts.MON_DESKTOPC, "hostMemoryUsage[{#ID}]", "进程占用的内存"),
	/**
	 * 桌面云-进程占用的CPU
	 */
	HOSTCPUUSAGE_DESKTOPC(IMonConsts.MON_DESKTOPC, "hostCpuUsage[{#ID}]", "进程占用的CPU"),
	/**
	 * 桌面云-分配的云主机数
	 */
	VMPOOLALLOCVMCOUNT_DESKTOPC(IMonConsts.MON_DESKTOPC, "vmPoolAllocVmCount[{#ID}]", "分配的云主机数"),
	/**
	 * 桌面云-运行的云主机数
	 */
	VMPOOLRUNNINGVMCOUNT_DESKTOPC(IMonConsts.MON_DESKTOPC, "vmPoolRunningVmCount[{#ID}]", "运行的云主机数"),
	/**
	 * 桌面云-空间总量
	 */
	SPTOTALSIZE_DESKTOPC(IMonConsts.MON_DESKTOPC, "spTotalSize[{#ID}]", "空间总量"),
	/**
	 * 桌面云-空间空闲
	 */
	SPFREESIZE_DESKTOPC(IMonConsts.MON_DESKTOPC, "spFreeSize[{#ID}]", "空间空闲"),
	

	/**
	 * Cisco-CPU使用率
	 */
	CISCO_CPU_RATE(IMonConsts.MON_NET_CISCO, "snmp.agent[OLD-CISCO-SYS-MIB::busyPer]", "CPU使用率"),
	/**
	 * Cisco-CPU索引名称
	 */
	CISCO_CPU_NAME(IMonConsts.MON_NET_CISCO, "snmp.agent[CISCO-PROCESS-MIB::cpmCPUTotalPhysicalIndex.{#SNMPINDEX}]", "CPU名称"),
	/**
	 * Cisco-连通性
	 */
	CISCO_ICMPPING(IMonConsts.MON_NET_CISCO, "icmpping", "连通性"),
	/**
	 * Cisco-网络下行速率
	 */
	CISCO_IFINOCTETS(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]", "网络下行速率"),
	/**
	 * Cisco-网络上行速率
	 */
	CISCO_IFOUTOCTETS(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]", "网络上行速率"),
	/**
	 * Cisco-内存已使用大小
	 */
	CISCO_CISCOMEMORYPOOLUSED(IMonConsts.MON_NET_CISCO, "snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]", "内存已使用大小"),
	/**
	 * Cisco-内存空闲大小
	 */
	CISCO_CISCOMEMORYPOOLFREE(IMonConsts.MON_NET_CISCO, "snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolFree.{#SNMPINDEX}]", "内存空闲大小"),
	/**
	 * Cisco-端口使用数(1表示连接 2表示其他情况)
	 */
	CISCO_IFCONNECTORPRESENT(IMonConsts.MON_NET_CISCO, "snmp.agent[IF-MIB::ifConnectorPresent.{#SNMPINDEX}]", "接口使用数"),
	/**
	 * Cisco-网络下行丢包率
	 */	
	CISCO_NET_IFINERRORS(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifInDiscards.{#SNMPINDEX}]", "网络下行丢包率"),
	/**
	 * Cisco-网络上行丢包率
	 */
	CISCO_NET_IFOUTERRORS(IMonConsts.MON_NET_CISCO, "snmp.agent[RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}]", "网络上行丢包率"),

	/**
	 * Huawei-CPU利用率
	 */
	NET_HUAWEI_CPU_RATE(IMonConsts.MON_NET_HUAWEI_SWITCH, "snmp.cpu.Usage", "CPU利用率"),
	/**
	 * Huawei-内存总量
	 */
	NET_HUAWEI_MEM_TOTAL(IMonConsts.MON_NET_HUAWEI_SWITCH, "snmp.memory.size[total]", "内存总量"),
	/**
	 * Huawei-剩余内存
	 */
	NET_HUAWEI_MEM_FREE(IMonConsts.MON_NET_HUAWEI_SWITCH, "snmp.memory.size[free]", "剩余内存"),
	/**
	 * Huawei-CPU最近一分钟平均利用率
	 */
	NET_HUAWEI_CPU_AVG1(IMonConsts.MON_NET_HUAWEI_SWITCH, "snmp.cpu.Usage[all,avg1]", "最近一分钟平均利用率"),
	/**
	 * Huawei-CPU最近五分钟平均利用率
	 */
	NET_HUAWEI_CPU_AVG5(IMonConsts.MON_NET_HUAWEI_SWITCH, "snmp.cpu.Usage[all,avg5]", "最近五分钟平均利用率"),
	
	/**
	 * 云服务控制节点-openstack-keystone
	 */
	CLOUD_OPENSTACK_KEYSTONE(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,keystone-all]",""),
	/**
	 * 云服务控制节点-memcached
	 */
	CLOUD_MEMCACHED(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[memcached]",""),
	/**
	 * 云服务控制节点-mysqld
	 */
	CLOUD_MYSQLD(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[mysqld]",""),
	/**
	 * 云服务控制节点-openstack-glance-api
	 */
	CLOUD_OPENSTACK_GLANCE(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,glance-api]",""),
	/**
	 * 云服务控制节点-openstack-glance-registry
	 */
	CLOUD_OPENSTACK_GLANCE_REGISTRY(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,glance-registry]",""),
	/**
	 * 云服务控制节点-openstack-nova-spicehtml5proxy
	 */
	CLOUD_SPICEHTML5PROXY(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-spicehtml5proxy]",""),
	/**
	 * 云服务控制节点-openstack-nova-novncproxy
	 */
	CLOUD_NOVNCPROXY(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-novncproxy]",""),
	/**
	 * 云服务控制节点-openstack-nova-api
	 */
	CLOUD_NOVA_API(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-api]",""),
	/**
	 * 云服务控制节点-openstack-nova-cert
	 */
	CLOUD_NOVA_CERT(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-cert]",""),
	/**
	 * 云服务控制节点-openstack-nova-consoleauth
	 */
	CLOUD_CONSOLEAUTH(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-consoleauth]",""),
	/**
	 * 云服务控制节点-openstack-nova-scheduler
	 */
	CLOUD_SCHEDULER(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-scheduler]",""),
	/**
	 * 云服务控制节点-openstack-nova-conductor
	 */
	CLOUD_CONDUCTOR(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,nova-conductor]",""),
	/**
	 * 云服务控制节点-neutron-server
	 */
	CLOUD_SERVER(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,neutron-server]",""),
	/**
	 * 云服务控制节点-openstack-cinder-api
	 */
	CLOUD_CINDER_API(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,cinder-api]",""),
	/**
	 * 云服务控制节点-openstack-cinder-scheduler
	 */
	CLOUD_CINDER_SCHEDULER(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,cinder-scheduler]",""),
	/**
	 * 云服务控制节点-openstack-heat-api
	 */
	CLOUD_HEAT_API(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,heat-api\\b]",""),
	/**
	 * 云服务控制节点-openstack-heat-api-cfn
	 */
	CLOUD_API_CFN(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,heat-api-cfn]",""),
	/**
	 * 云服务控制节点-openstack-heat-engine
	 */
	CLOUD_ENGINE(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,heat-engine]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-api
	 */
	CLOUD_CEILOMETER(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-api]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-notification
	 */
	CLOUD_NOTIFICATION(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-agent-notification]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-central
	 */
	CLOUD_CENTRAL(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-agent-central]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-collector
	 */
	CLOUD_COLLECTOR(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-collector]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-alarm-evaluator
	 */
	CLOUD_EVALUATOR(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-alarm-evaluator]",""),
	/**
	 * 云服务控制节点-openstack-ceilometer-alarm-notifier
	 */
	CLOUD_NOTIFIER(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[python,,,ceilometer-alarm-notifier]",""),
	/**
	 * 云服务控制节点-httpd
	 */
	CLOUD_HTTPD(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[httpd]",""),
	/**
	 * 云服务控制节点-sps-api
	 */
	CLOUD_SPS(IMonConsts.MON_CLOUD_CONTROLER,"proc.num[sps-api]",""),
	
	
	
	/**
	 * 云服务计算节点-libvirtd
	 */
	CLOUD_LIBVIRTD(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[libvirtd]",""),
	/**
	 * 云服务计算节点-messagebus
	 */
	CLOUD_MESSAGEBUS(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[dbus-daemon]",""),
	/**
	 * 云服务计算节点-openstack-nova-compute
	 */
	CLOUD_NOVA_COMPUTE(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[python,,,nova-compute]",""),
	/**
	 * 云服务计算节点-neutron-openvswitch-agent
	 */
	CLOUD_OPENVSWITCH_AGENT(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[python,,,neutron-openvswitch-agent]",""),
	/**
	 * 云服务计算节点-openvswitch
	 */
	CLOUD_OPENVSWITCH(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[ovs-vswitchd]",""),
	/**
	 * 云服务计算节点-openstack-cinder-volume
	 */
	CLOUD_VOLUME(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[python,,,cinder-volume]",""),
	/**
	 * 云服务计算节点-tgtd
	 */
	CLOUD_TGTD(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[tgtd]",""),
	/**
	 * 云服务计算节点-openstack-ceilometer-compute
	 */
	CLOUD_CEILOMETER_COMPUTE(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[python,,,ceilometer-agent-compute]",""),
	/**
	 * 云服务计算节点-SPSAGENT
	 */
	CLOUD_SPSAGENT(IMonConsts.MON_CLOUD_COMPUTER,"proc.num[SPSAGENT]",""),
	
	/**
	 * 云服务网络节点-openvswitch
	 */
	CLOUD_NET_OPENVSWITCH(IMonConsts.MON_CLOUD_NETWORK,"proc.num[ovs-vswitchd]",""),
	/**
	 * 云服务网络节点-neutron-openvswitch-agent
	 */
	CLOUD_NET_AGENT(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-openvswitch-agent]",""),
	/**
	 * 云服务网络节点-neutron-l3-agent
	 */
	CLOUD_NET_L3_AGENT(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-l3-agent]",""),
	/**
	 * 云服务网络节点-neutron-dhcp-agent
	 */
	CLOUD_NET_DHCP(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-dhcp-agent]",""),
	/**
	 * 云服务网络节点-neutron-metadata-agent
	 */
	CLOUD_NET_METADATA(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-metadata-agent]",""),
	/**
	 * 云服务网络节点-neutron-vpn-agent
	 */
	CLOUD_NET_VPN(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-vpn-agent]",""),
	/**
	 * 云服务网络节点-neutron-lbaas-agent
	 */
	CLOUD_NET_LBAAS(IMonConsts.MON_CLOUD_NETWORK,"proc.num[python,,,neutron-lbaas-agent]",""),
	/**
	 * 云服务网络节点-ipsec
	 */
	CLOUD_NET_IPSEC(IMonConsts.MON_CLOUD_NETWORK,"proc.num[sh,,,ipsec]",""),
	
	
	/**
	 * 云服务存储节点-ceph-mon
	 */
	CLOUD__CEPH_MON(IMonConsts.MON_CLOUD_CEPH,"ceph.mon",""),
	/**
	 * 云服务存储节点-ceph-osd
	 */
	CLOUD_CEPH_OSD(IMonConsts.MON_CLOUD_CEPH,"ceph.osd",""),
	
	/**
	 * 云服务门户节点-httpd
	 */
	CLOUD_WEB_HTTPD(IMonConsts.MON_CLOUD_CEPH,"proc.num[httpd]",""),
	
	/**
	 * 网络接口上行IPOS-Linux
	 */
	NET_INGERFACE_UPIPOS_LINUX(IMonConsts.MON_SERVER_LINUX,"netOutIOPS[{#SNMPVALUE}]","上行每秒操作数"),
	/**
	 * 网络接口下行IPOS-Linux
	 */
	NET_INGERFACE_DOWNIPOS_LINUX(IMonConsts.MON_SERVER_LINUX,"netInIOPS[{#SNMPVALUE}]","下行每秒操作数"),
	/**
	 * 网络接口上行IPOS-Windows
	 */
	NET_INGERFACE_UPIPOS_WINDOWS(IMonConsts.MON_SERVER_WINDOWS,"net.if.out[{#IFNAME},packets]","上行每秒操作数"),
	/**
	 * 网络接口下行IPOS-Windows
	 */
	NET_INGERFACE_DOWNIPOS_WINDOWS(IMonConsts.MON_SERVER_WINDOWS,"net.if.in[{#IFNAME},packets]","下行每秒操作数"),
	
	
	
	
	/**
	 * 租户简单应用Ftp服务状态_ftp_status
	 */
	SIMP_HTTP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[80]", "HTTP服务状态"),
	/**
	 * 租户简单应用Ftp服务状态_ftp_status
	 */
	SIMP_FTP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[21]", "FTP服务状态"),
	/**
	 * 租户简单应用LDAP服务状态_load_status
	 */
	SIMP_LDAP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[389]", "LDAP服务状态"),
	/**
	 * 租户简单应用LDAP服务状态_load_ssl_status
	 */
	SIMP_LDAP_SSL_STATUS(IMonConsts.MON_VM, "net.tcp.listen[636]", "LDAP ssl服务状态"),
	/**
	 * 租户简单应用NNTP服务状态_nntp_status
	 */
	SIMP_NNTP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[119]", "NNTP服务状态"),
	/**
	 * 租户简单应用NTP服务状态_ntp_status
	 */
	SIMP_NTP_STATUS(IMonConsts.MON_VM, "net.udp.listen[123]", "NTP服务状态"),
	/**
	 * 租户简单应用NTP服务状态_ntp_status windows下(windows的agent不支持net.udp.listen)
	 */
	SIMP_NTP_STATUS_WIN(IMonConsts.MON_VM, "service_state[Windows Time]", "NTP服务状态"),
	/**
	 * 租户简单应用IMAP服务状态_imap_status
	 */
	SIMP_IMAP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[143]", "IMAP服务状态"),
	/**
	 * 租户简单应用POP服务状态_pop_status
	 */
	SIMP_POP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[110]", "POP服务状态"),
	/**
	 * 租户简单应用SMTP服务状态_smtp_status
	 */
	SIMP_SMTP_STATUS(IMonConsts.MON_VM, "net.tcp.listen[25]", "SMTP服务状态"),
	;

	private Long groupId;
	private String name;
	private String value;

	ItemsKey(Long groupId, String value, String name) {
		this.groupId = groupId;
		this.value = value;
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public Long getGroupId() {
		return groupId;
	}

	private static CArray<CArray<ItemsKey>> GROUP_KEYS = null;

	public static CArray<ItemsKey> groupKeys(IMonGroup group) {
		if (GROUP_KEYS == null) {
			GROUP_KEYS = CArray.map();
			for (ItemsKey key : ItemsKey.values()) {
				Nest.value(GROUP_KEYS, key.getGroupId()).$s(true).push(key);
			}
		}
		return GROUP_KEYS.get(group.id());
	}

	/**
	 * 根据key求得显示名称
	 * 
	 * @param value
	 * @return
	 */
	public static Object itemsName(Object key_) {
		for (ItemsKey i : ItemsKey.values()) {
			if (key_.equals(i.value)) {
				return i.name;
			}
		}
		return key_;
	}
}