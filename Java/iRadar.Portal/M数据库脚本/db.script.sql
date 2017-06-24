-------------------------------------------------未执行的
--巡检报表添加批次功能，添加批次字段，添加批次表，20150803 冯进冰
alter table i_inspection_report_historys add batchnum bigint DEFAULT '0' NOT NULL after reportid
alter table i_inspection_reports add batchnum bigint DEFAULT '0' NOT NULL after reportid
alter table i_inspection_reports add batch_time INT DEFAULT '0' NOT NULL  after create_time

CREATE TABLE
    i_inspection_report_batch
    (
        tenantid VARCHAR(64) COMMENT '租户id',
        reportbatchid bigint unsigned NOT NULL COMMENT '批次id',
        reportid bigint COMMENT '巡检报告id',
        batchnum bigint COMMENT '巡检报告批次',
        batch_time INT DEFAULT '0' NOT NULL COMMENT '巡检执行时间',
        PRIMARY KEY (reportbatchid),
        CONSTRAINT hosts_templates_1 UNIQUE (reportid, batchnum),
        INDEX hosts_templates_2 (reportbatchid)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

--删除alerts表中events和actions表的中外键约束，20150729 冯进冰
ALTER TABLE alerts DROP FOREIGN KEY  c_alerts_1
ALTER TABLE alerts DROP FOREIGN KEY  c_alerts_2

--租户服务应用添加javahome宏变量，20150715 冯进冰
update  i_t_item set key_='mysql.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=11;
update  i_t_item set key_='mysql.qcache_free[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=12;
update  i_t_item set key_='mysql.threads_connected[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=13;
update  i_t_item set key_='mysql.qcache_total[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=14;
update  i_t_item set key_='mysql.questions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=15;
update  i_t_item set key_='mysql.threads_running[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]' where itemid=16;

update  i_t_item set key_='tomcat.uptime[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]' where itemid=17;
update  i_t_item set key_='tomcat.maxThreads[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]' where itemid=18;
update  i_t_item set key_='tomcat.currentThreadCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]' where itemid=19;
update  i_t_item set key_='tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]' where itemid=20;
-------------------------------------------------初始化脚本已执行的
alter  table i_inspection_report_items add itemname varchar(200) not null after itemid
alter  table i_inspection_report_historys add itemname varchar(200) not null after itemid;
alter  table i_inspection_report_items drop column  item_enum_name;
alter  table i_inspection_report_historys drop column  item_enum_name;

--给巡检添加巡检周期相关字段，20150623 冯进冰
alter table  i_inspection_reports add timeperiod_type INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add every INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add month INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add dayofweek INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add DAY INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add start_time INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add period INT DEFAULT '0' NOT NULL;          
alter table  i_inspection_reports add start_date INT DEFAULT '0' NOT NULL;
alter table  i_inspection_reports add create_time INT DEFAULT '0' NOT NULL;


--给巡检添加active_till字段，20150617 冯进冰
alter table  i_inspection_reports add  active_till VARCHAR(64) DEFAULT NULL after time

--给应用添加type字段，作为租户服务应用分组20150612 冯进冰
ALTER TABLE applications ADD type  varchar(4) DEFAULT NULL  AFTER name

--更新租户服务应用mysql监控指标键值以及名称 20150612 冯进冰
update i_t_item set name='运行时间',key_='mysql.uptime[{$USER},{$PSWD},{$DBIP},{$PORT}]',units='s'  where itemid = 11;
update i_t_item set name='查询缓存可用量',key_='mysql.qcache_free[{$USER},{$PSWD},{$DBIP},{$PORT}]',units='B'  where itemid = 12;
update i_t_item set name='系统会话数',key_='mysql.threads_connected[{$USER},{$PSWD},{$DBIP},{$PORT}]'  where itemid = 13;
update i_t_item set name='查询缓存总量',key_='mysql.qcache_total[{$USER},{$PSWD},{$DBIP},{$PORT}]',units='B'  where itemid = 14;
update i_t_item set name='每秒查询量',key_='mysql.questions[{$USER},{$PSWD},{$DBIP},{$PORT}]',units='qps'  where itemid = 15;
update i_t_item set name='数据库版本',key_='mysql.version[{$USER},{$PSWD},{$DBIP},{$PORT}]'  where itemid = 16;

update i_t_application set name ='运行时间',description='运行时间' where applicationid=7;
update i_t_application set name ='查询缓存可用量',description='查询缓存可用量' where applicationid=8;
update i_t_application set name ='系统会话数',description='系统会话数' where applicationid=9;
update i_t_application set name ='查询缓存总量',description='查询缓存总量' where applicationid=10;
update i_t_application set name ='每秒查询量',description='每秒查询量' where applicationid=11;
update i_t_application set name ='数据库版本',description='数据库版本' where applicationid=12;

--添加云主机状态指标 20150521 冯进冰
INSERT INTO `i_t_item` (`itemid`, `applicationid`, `status`, `name`, `key_`, `units`) VALUES('20','16','1','云主机状态','vm.status','');
INSERT INTO `i_t_application` (`applicationid`, `templateid`, `status`, `name`, `description`) VALUES('16','1','1','云主机状态','云主机状态：ACTIVE:正常,ERROR:异常');

--给巡检报表添加新字段，主表添加巡检人，item表添加hostname和delay，history表添加value和hostname
ALTER TABLE i_inspection_reports ADD username  VARCHAR(100) NOT NULL DEFAULT '' AFTER NAME

ALTER TABLE  i_inspection_report_items ADD hostname VARCHAR(64) NOT NULL DEFAULT '' AFTER hostid
ALTER TABLE  i_inspection_report_items ADD delay INT(11) NOT NULL DEFAULT '0' AFTER item_enum_name

ALTER TABLE i_inspection_report_historys  ADD VALUE VARCHAR(255) NOT NULL DEFAULT '' AFTER item_enum_name 
ALTER TABLE i_inspection_report_historys  ADD hostname VARCHAR(64) NOT NULL DEFAULT '' AFTER hostid 



--修改服务应用类型   20150510  冯进冰  隐藏磁盘使用率和网络上下行
UPDATE i_t_application SET STATUS =0 WHERE applicationid = 4
UPDATE i_t_application SET STATUS =0 WHERE applicationid = 6
--修改服务应用类型   20150510  冯进冰
UPDATE i_t_template SET NAME='cloudhost' WHERE templateid=1;
UPDATE i_t_template SET NAME='mysql' WHERE templateid=2;
UPDATE i_t_template SET NAME='tomcat' WHERE templateid=3;

--给租户监控指标云主机磁盘读写速率、网络速率添加单位    20150510  冯进冰
UPDATE i_t_item SET units='Mb/s' WHERE itemid = 5;
UPDATE i_t_item SET units='Kb/s' WHERE itemid = 6;
UPDATE i_t_item SET units='Mb/s' WHERE itemid = 18;


--添加下列数据，与regexps解决解决服务器发现不了设备问题    20150415  冯进冰
delete from expressions
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',1,1,'^(btrfs|ext2|ext3|ext4|jfs|reiser|xfs|ffs|ufs|jfs|jfs2|vxfs|hfs|ntfs|fat32|zfs)$',3,',',0);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',2,2,'^lo$',4,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',3,3,'^(Physical memory|Virtual memory|Memory buffers|Cached memory|Swap space)$',4,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',4,2,'^Software Loopback Interface',4,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',5,4,'^(hd[a-z]+|sd[a-z]+|vd[a-z]+|dm-[0-9]+|drbd[0-9]+)$',3,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',6,5,'^(eth[0-9]+|enp[0-9]+s[0-9]+|em[0-9]+)$',3,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',7,6,'hrDeviceProcessor$',3,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',8,7,'^Physical memory$',3,',',0);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',9,8,'^(btrfs|ext2|ext3|ext4|jfs|reiser|xfs|ffs|ufs|jfs|jfs2|vxfs|hfs|ntfs|fat32|zfs)$',3,',',1);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',10,9,'^(Loop|WAN|RAS|Bluetooth|Wifi|TAP|vmware|Tunneling|ISATAP|QOS|Virtual|WFP|usb).$',4,',',0);
insert  into `expressions`(`tenantid`,`expressionid`,`regexpid`,`expression`,`expression_type`,`exp_delimiter`,`case_sensitive`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',11,10,'hrStorageFixedDisk',3,',',0);

--proxyz租户代理，添加-  同时删除其他  20150414  冯进冰
delete from tenants
INSERT INTO `tenants` VALUES (1,'5e4d0a6d39a44b9c906a3173b448aa4a','0',0),(2,'-','0',0);

--解决服务器发现不了问题  20150414  冯进冰
SET FOREIGN_KEY_CHECKS = 0;--汇报1217错误，外键错误，添加这个语句即可
DROP TABLE IF EXISTS `regexps`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `regexps` (
  `tenantid` varchar(64) DEFAULT '0',
  `regexpid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `test_string` text NOT NULL,
  PRIMARY KEY (`regexpid`),
  KEY `regexps_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `regexps` */

insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',1,'File systems for discovery','ext3');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',2,'Network interfaces for discovery','eth0');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',3,'Storage devices for SNMP discovery','/boot');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',4,'Disk_Discovery','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',5,'If_Discovery','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',6,'CPU_Discovery','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',7,'Memory_Discovery','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',8,'Patition_Discovery','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',9,'If_Discovery_windows','');
insert  into `regexps`(`tenantid`,`regexpid`,`name`,`test_string`) values ('5e4d0a6d39a44b9c906a3173b448aa4a',10,'Patition_Discovery_windows','');

--删除巡检报告旧表      20150414  冯进冰
drop table i_inspection_history;
drop table i_inspection_history_item;
drop table i_inspection_report;
drop table i_inspection_host_application;

--巡检报告表结构      20150409  倪明明
DROP TABLE IF EXISTS `i_inspection_reports`;
CREATE TABLE `i_inspection_reports` (
  `tenantid` varchar(64) DEFAULT '0' COMMENT '租户id',
  `reportid` bigint(20) NOT NULL DEFAULT '0' COMMENT '巡检报告id',
  `name` varchar(64) DEFAULT NULL COMMENT '巡检报告名称',
  `time` varchar(64) DEFAULT NULL COMMENT '巡检时间',
  `groupid` bigint(20) DEFAULT NULL COMMENT '设备类型id',
  `status` int(1) DEFAULT NULL COMMENT '状态(0:启用 1：停用)',
  `executed` int(1) DEFAULT NULL COMMENT '是否已执行(0:未执行  1：已执行)',
  PRIMARY KEY (`reportid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `i_inspection_report_items`;
CREATE TABLE `i_inspection_report_items` (
  `tenantid` varchar(64) DEFAULT '0' COMMENT '租户id',
  `reportitemid` bigint(20) NOT NULL,
  `reportid` bigint(20) DEFAULT NULL,
  `hostid` bigint(20) DEFAULT NULL COMMENT '设备id',
  `itemid` bigint(20) DEFAULT NULL COMMENT '设备监控项id',
  `item_enum_name` varchar(100) DEFAULT NULL COMMENT '监控项枚举名称',
  PRIMARY KEY (`reportitemid`),
  KEY `c_report_1` (`reportid`),
  CONSTRAINT `c_report_1` FOREIGN KEY (`reportid`) REFERENCES `i_inspection_reports` (`reportid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `i_inspection_report_historys`;
CREATE TABLE `i_inspection_report_historys` (
  `tenantid` varchar(64) DEFAULT NULL COMMENT '租户id',
  `report_historyid` bigint(20) NOT NULL COMMENT '巡检历史id',
  `reportid` bigint(20) DEFAULT NULL COMMENT '巡检报告id',
  `hostid` bigint(20) DEFAULT NULL COMMENT '设备id',
  `itemid` bigint(20) DEFAULT NULL COMMENT '设备监控项id',
  `item_enum_name` varchar(100) DEFAULT NULL,
  `isproblem` int(1) DEFAULT NULL COMMENT '是否正常(0:正常 1：异常)',
  PRIMARY KEY (`report_historyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--数据库t_topo 改了字段topotype 长度  为64
ALTER TABLE   `t_topo`  MODIFY column  `topotype` varchar(64);


--添加监控模型表
DROP TABLE IF EXISTS `i_t_template`;
CREATE TABLE `i_t_template` (
  `templateid` bigint(20) unsigned NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`templateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `i_t_template` VALUES ('1', '1', 'Cloudhost');
INSERT INTO `i_t_template` VALUES ('2', '1', 'mysql');
INSERT INTO `i_t_template` VALUES ('3', '1', 'tomcat');

--添加监控维度表
DROP TABLE IF EXISTS `i_t_application`;

CREATE TABLE `i_t_application` (
  `applicationid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  PRIMARY KEY (`applicationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `i_t_application` VALUES ('1', '1', '1', 'CPU使用率', '监控CPU的使用率比例，包括用户态(User)、内核态(System)、I/O等待(IOWait)、空闲(Idle)等');
INSERT INTO `i_t_application` VALUES ('2', '1', '1', 'CPU负载', '监控Linux服务器的平均负载(load average)，包括最近1分钟、5分钟、15分钟等');
INSERT INTO `i_t_application` VALUES ('3', '1', '1', '内存使用率', '监控内存使用率，对于Linux服务器，包括空闲内存、高速缓存、页面缓存、应用程序内存等');
INSERT INTO `i_t_application` VALUES ('4', '1', '1', '磁盘使用率', '监控所有磁盘分区的空间使用率，包括总空间和已用空间');
INSERT INTO `i_t_application` VALUES ('5', '1', '1', '磁盘读写速率', '监控所有磁盘分区的I/O流量，包括写入和读取');
INSERT INTO `i_t_application` VALUES ('6', '1', '1', '云主机状态', '云主机状态：ACTIVE:正常,ERROR:异常');
INSERT INTO `i_t_application` VALUES ('7', '2', '1', '正常运行时间(秒)', 'MySQL并发链接');
INSERT INTO `i_t_application` VALUES ('8', '2', '1', '缓存空闲内存', 'MySql查询缓存空间使用率');
INSERT INTO `i_t_application` VALUES ('9', '2', '1', '当前连接数', 'MySQL应用大小');
INSERT INTO `i_t_application` VALUES ('10', '2', '1', '总连接数', '总连接数');
INSERT INTO `i_t_application` VALUES ('11', '2', '1', '查询量/秒(QPS)', '查询量/秒(QPS');
INSERT INTO `i_t_application` VALUES ('12', '2', '1', '当前版本', '当前版本');
INSERT INTO `i_t_application` VALUES ('13', '3', '1', '运行时间', '运行时间');
INSERT INTO `i_t_application` VALUES ('14', '3', '1', '最大线程数', '最大线程数');
INSERT INTO `i_t_application` VALUES ('15', '3', '1', '当前线程数', '当前线程数');
INSERT INTO `i_t_application` VALUES ('16', '3', '1', '每秒出错数', '每秒出错数');

--添加监控指标表
DROP TABLE IF EXISTS `i_t_item`;

CREATE TABLE `i_t_item` (
  `itemid` bigint(20) unsigned NOT NULL,
  `applicationid` bigint(20) unsigned DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `units` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `i_t_item` VALUES ('1', '1', '1', 'CPU使用率_Linux', 'cpuUtil', '%');
INSERT INTO `i_t_item` VALUES ('2', '1', '1', 'CPU使用率_Windows', 'wmi.get[root\\\cimv2,Select LoadPercentage from Win32_processor]', '%');
INSERT INTO `i_t_item` VALUES ('3', '2', '1', 'CPU负载', 'system.cpu.load[percpu,avg1]', '');
INSERT INTO `i_t_item` VALUES ('4', '3', '1', '内存使用率', 'vm.memory.size[pused]', '%');
INSERT INTO `i_t_item` VALUES ('5', '4', '1', '磁盘使用率', 'vfs.fs.size[fs,<mode>]', '%');
INSERT INTO `i_t_item` VALUES ('6', '5', '1', '磁盘读速率_Linux', 'vfs.dev.read[,sectors]', 'MB/s');
INSERT INTO `i_t_item` VALUES ('7', '5', '1', '磁盘读速率_Windows', 'wmi.get[root\\\cimv2,Select DiskReadBytesPersec from Win32_PerfFormattedData_PerfDisk_LogicalDisk where Name = \\\'_Total\\\']', 'MB/s');
INSERT INTO `i_t_item` VALUES ('8', '5', '1', '磁盘写速率_Linux', 'vfs.dev.write[,sectors]', 'MB/s');
INSERT INTO `i_t_item` VALUES ('9', '5', '1', '磁盘写速率_Windows', 'wmi.get[root\\\cimv2,Select DiskWriteBytesPersec from Win32_PerfFormattedData_PerfDisk_LogicalDisk where Name = \\\'_Total\\\']', 'MB/s');
INSERT INTO `i_t_item` VALUES ('10', '6', '1', '云主机状态', 'vm.status', '');
INSERT INTO `i_t_item` VALUES ('11', '7', '1', '正常运行时间(秒)', 'DBforBIX.MySQL.Uptime', '%');
INSERT INTO `i_t_item` VALUES ('12', '8', '1', '缓存空闲内存', 'DBforBIX.MySQL.Qcache_free_memory', '%');
INSERT INTO `i_t_item` VALUES ('13', '9', '1', '当前连接数', 'DBforBIX.MySQL.Threads_connected', '');
INSERT INTO `i_t_item` VALUES ('14', '10', '1', '总连接数', 'DBforBIX.MySQL.Connections', '');
INSERT INTO `i_t_item` VALUES ('15', '11', '1', '查询量/秒(QPS)', 'DBforBIX.MySQL.Queries_per_sec', '');
INSERT INTO `i_t_item` VALUES ('16', '12', '1', '当前版本', 'DBforBIX.MySQL.Dbversion', '');
INSERT INTO `i_t_item` VALUES ('17', '13', '1', '运行时间', 'tomcat.uptime[{$TOMCAT_PORT},{$JMX_PORT}]', '');
INSERT INTO `i_t_item` VALUES ('18', '14', '1', '最大线程数', 'tomcat.maxThreads[{$TOMCAT_PORT},{$JMX_PORT}]', '个');
INSERT INTO `i_t_item` VALUES ('19', '15', '1', '当前线程数', 'tomcat.currentThreadCount[{$TOMCAT_PORT},{$JMX_PORT}]', '个');
INSERT INTO `i_t_item` VALUES ('20', '16', '1', '每秒出错数', 'tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT}]', '个');

--2015-03-06 在用PHP导入模型后，修复tenantid为0的问题
SET @ORIG_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

truncate table `actions`;
truncate table `auditlog`;
truncate table `auditlog_details`;
truncate table `conditions`;
truncate table `escalations`;
truncate table `events`;
truncate table `group_prototype`;
truncate table `housekeeper`;
truncate table `ids`;
truncate table `operations`;
truncate table `opmessage`;
truncate table `opmessage_grp`;
truncate table `profiles`;
truncate table `sessions`;
truncate table `user_history`;

UPDATE `applications` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `application_template` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `items` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `item_discovery` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `items_applications` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `hosts` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `host_discovery` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `hosts_groups` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0' ;
UPDATE `hosts_templates` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `hostmacro` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `interface` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `functions` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `triggers` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `trigger_depends` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `trigger_discovery` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `graphs` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `graphs_items` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `group_discovery` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `group_prototype` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `screens` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `screens_items` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `mappings` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `valuemaps` SET tenantid='5e4d0a6d39a44b9c906a3173b448aa4a' WHERE tenantid = '0';
UPDATE `groups` SET tenantid='-' where tenantid <>'|';

SET FOREIGN_KEY_CHECKS=@ORIG_FOREIGN_KEY_CHECKS;




--增加虚拟资源发现表结构
alter table `hosts` add hostid_os varchar(64) comment 'iaas对应设备的id' after hostid;


--创建iradar用户和赋权限
GRANT ALL PRIVILEGES ON iradar.* TO iradar@'localhost' IDENTIFIED BY 'iradar';
GRANT ALL PRIVILEGES ON iradar.* TO iradar@'%' IDENTIFIED BY 'iradar';
GRANT ALL PRIVILEGES ON *.* TO iradar@'%' IDENTIFIED BY 'iradar';
GRANT ALL PRIVILEGES ON *.* TO iradar@'localhost' IDENTIFIED BY 'iradar';
flush privileges;

--更新菜单是否显示
update sys_func set status = 0 where id not like '0003%' and id not like '0004%';