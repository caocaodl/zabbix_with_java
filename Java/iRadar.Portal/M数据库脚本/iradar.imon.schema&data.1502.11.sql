-- MySQL dump 10.13  Distrib 5.1.73, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: imon.std
-- ------------------------------------------------------
-- Server version	5.1.73

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `acknowledges`
--

DROP TABLE IF EXISTS `acknowledges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `acknowledges` (
  `tenantid` varchar(64) DEFAULT '0',
  `acknowledgeid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `eventid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `message` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`acknowledgeid`),
  KEY `acknowledges_1` (`userid`),
  KEY `acknowledges_2` (`eventid`),
  KEY `acknowledges_3` (`clock`),
  CONSTRAINT `c_acknowledges_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE,
  CONSTRAINT `c_acknowledges_2` FOREIGN KEY (`eventid`) REFERENCES `events` (`eventid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `acknowledges`
--

LOCK TABLES `acknowledges` WRITE;
/*!40000 ALTER TABLE `acknowledges` DISABLE KEYS */;
/*!40000 ALTER TABLE `acknowledges` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `actions`
--

DROP TABLE IF EXISTS `actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actions` (
  `tenantid` varchar(64) DEFAULT '0',
  `actionid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `eventsource` int(11) NOT NULL DEFAULT '0',
  `evaltype` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `esc_period` int(11) NOT NULL DEFAULT '0',
  `def_shortdata` varchar(255) NOT NULL DEFAULT '',
  `def_longdata` text NOT NULL,
  `recovery_msg` int(11) NOT NULL DEFAULT '0',
  `r_shortdata` varchar(255) NOT NULL DEFAULT '',
  `r_longdata` text NOT NULL,
  PRIMARY KEY (`actionid`),
  KEY `actions_1` (`eventsource`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actions`
--

LOCK TABLES `actions` WRITE;
/*!40000 ALTER TABLE `actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alerts`
--

DROP TABLE IF EXISTS `alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alerts` (
  `tenantid` varchar(64) DEFAULT '0',
  `alertid` bigint(20) unsigned NOT NULL,
  `actionid` bigint(20) unsigned NOT NULL,
  `eventid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) DEFAULT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `mediatypeid` bigint(20) unsigned DEFAULT NULL,
  `sendto` varchar(100) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `message` longtext NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `retries` int(11) NOT NULL DEFAULT '0',
  `error` varchar(128) NOT NULL DEFAULT '',
  `esc_step` int(11) NOT NULL DEFAULT '0',
  `alerttype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`alertid`),
  KEY `alerts_1` (`actionid`),
  KEY `alerts_2` (`clock`),
  KEY `alerts_3` (`eventid`),
  KEY `alerts_4` (`status`,`retries`),
  KEY `alerts_5` (`mediatypeid`),
  KEY `alerts_6` (`userid`),
  CONSTRAINT `c_alerts_3` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE,
  CONSTRAINT `c_alerts_4` FOREIGN KEY (`mediatypeid`) REFERENCES `media_type` (`mediatypeid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerts`
--

LOCK TABLES `alerts` WRITE;
/*!40000 ALTER TABLE `alerts` DISABLE KEYS */;
/*!40000 ALTER TABLE `alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `application_template`
--

DROP TABLE IF EXISTS `application_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `application_template` (
  `tenantid` varchar(64) DEFAULT '0',
  `application_templateid` bigint(20) unsigned NOT NULL,
  `applicationid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`application_templateid`),
  UNIQUE KEY `application_template_1` (`applicationid`,`templateid`),
  KEY `application_template_2` (`templateid`),
  CONSTRAINT `c_application_template_1` FOREIGN KEY (`applicationid`) REFERENCES `applications` (`applicationid`) ON DELETE CASCADE,
  CONSTRAINT `c_application_template_2` FOREIGN KEY (`templateid`) REFERENCES `applications` (`applicationid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_template`
--

LOCK TABLES `application_template` WRITE;
/*!40000 ALTER TABLE `application_template` DISABLE KEYS */;
INSERT INTO `application_template` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,775,764),('5e4d0a6d39a44b9c906a3173b448aa4a',2,776,765);
/*!40000 ALTER TABLE `application_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `applications` (
  `tenantid` varchar(64) DEFAULT '0',
  `applicationid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `type` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`applicationid`),
  UNIQUE KEY `applications_2` (`hostid`,`name`),
  CONSTRAINT `c_applications_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',508,10113,'平台服务监控状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',535,10131,'DB2',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',635,10157,'概览',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',636,10157,'租户配额',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',640,10161,'CPU',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',641,10161,'General',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',642,10161,'配置带宽',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',643,10161,'Connector',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',644,10161,'MAC',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',645,10161,'MTU',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',646,10161,'Status',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',647,10161,'Type',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',648,10161,'实时带宽',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',649,10161,'Broadcast',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',650,10161,'Discarded',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',651,10161,'Error',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',652,10161,'Multicast',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',653,10161,'Unicast',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',654,10161,'Memory Pools',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',661,10163,'CPU',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',662,10163,'内存',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',663,10163,'文件系统',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',664,10163,'磁盘',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',665,10163,'系统信息',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',666,10163,'网络',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',678,10144,'状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',679,10113,'PG',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',680,10113,'RADOS',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',681,10113,'OSD',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',682,10113,'性能',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',683,10132,'等待',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',684,10132,'SGA',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',685,10132,'会话',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',686,10132,'池',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',687,10132,'命中率',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',688,10132,'PGA',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',689,10132,'PHIO',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',690,10132,'LIO',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',691,10132,'归档',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',692,10132,'用户',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',693,10132,'其他',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',697,10164,'ASP.Net',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',698,10164,'服务',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',699,10154,'性能',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',700,10154,'容量',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',702,10154,'其他',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',724,10172,'MongoDB Status',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',727,10175,'平台服务监控状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',728,10176,'平台服务监控状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',730,10178,'平台服务监控状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',731,10179,'MSSQL',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',743,10182,'CPU',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',745,10182,'交换分区',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',746,10182,'内存',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',747,10182,'文件系统',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',748,10182,'磁盘',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',749,10182,'系统信息',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',750,10182,'网络',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',751,10182,'进程',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',752,10183,'CPU',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',753,10183,'云服务',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',754,10183,'交换分区',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',755,10183,'内存',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',756,10183,'文件系统',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',757,10183,'磁盘',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',758,10183,'简单应用',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',759,10183,'系统信息',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',760,10183,'网络',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',761,10183,'进程',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',764,10186,'General',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',765,10186,'Interfaces',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',774,10157,'平台服务监控状态',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',775,10187,'General',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',776,10187,'Interfaces',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',777,10187,'性能',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',778,10188,'性能',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',779,10188,'容量',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',780,10188,'其他',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',781,10189,'性能',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',782,10189,'其他',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',783,10189,'容量',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',784,10161,'ICMP',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',785,10161,'ARP',NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',786,10161,'Route',NULL);
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditlog`
--

DROP TABLE IF EXISTS `auditlog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auditlog` (
  `tenantid` varchar(64) DEFAULT '0',
  `auditid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `action` int(11) NOT NULL DEFAULT '0',
  `resourcetype` int(11) NOT NULL DEFAULT '0',
  `details` varchar(128) NOT NULL DEFAULT '0',
  `ip` varchar(39) NOT NULL DEFAULT '',
  `resourceid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `resourcename` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`auditid`),
  KEY `auditlog_1` (`userid`,`clock`),
  KEY `auditlog_2` (`clock`),
  CONSTRAINT `c_auditlog_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditlog`
--

LOCK TABLES `auditlog` WRITE;
/*!40000 ALTER TABLE `auditlog` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditlog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditlog_details`
--

DROP TABLE IF EXISTS `auditlog_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auditlog_details` (
  `tenantid` varchar(64) DEFAULT '0',
  `auditdetailid` bigint(20) unsigned NOT NULL,
  `auditid` bigint(20) unsigned NOT NULL,
  `table_name` varchar(64) NOT NULL DEFAULT '',
  `field_name` varchar(64) NOT NULL DEFAULT '',
  `oldvalue` text NOT NULL,
  `newvalue` text NOT NULL,
  PRIMARY KEY (`auditdetailid`),
  KEY `auditlog_details_1` (`auditid`),
  CONSTRAINT `c_auditlog_details_1` FOREIGN KEY (`auditid`) REFERENCES `auditlog` (`auditid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditlog_details`
--

LOCK TABLES `auditlog_details` WRITE;
/*!40000 ALTER TABLE `auditlog_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditlog_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `autoreg_host`
--

DROP TABLE IF EXISTS `autoreg_host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autoreg_host` (
  `tenantid` varchar(64) DEFAULT '0',
  `autoreg_hostid` bigint(20) unsigned NOT NULL,
  `proxy_hostid` bigint(20) unsigned DEFAULT NULL,
  `host` varchar(64) NOT NULL DEFAULT '',
  `listen_ip` varchar(39) NOT NULL DEFAULT '',
  `listen_port` int(11) NOT NULL DEFAULT '0',
  `listen_dns` varchar(64) NOT NULL DEFAULT '',
  `host_metadata` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`autoreg_hostid`),
  KEY `autoreg_host_1` (`proxy_hostid`,`host`),
  CONSTRAINT `c_autoreg_host_1` FOREIGN KEY (`proxy_hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autoreg_host`
--

LOCK TABLES `autoreg_host` WRITE;
/*!40000 ALTER TABLE `autoreg_host` DISABLE KEYS */;
/*!40000 ALTER TABLE `autoreg_host` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conditions` (
  `tenantid` varchar(64) DEFAULT '0',
  `conditionid` bigint(20) unsigned NOT NULL,
  `actionid` bigint(20) unsigned NOT NULL,
  `conditiontype` int(11) NOT NULL DEFAULT '0',
  `operator` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`conditionid`),
  KEY `conditions_1` (`actionid`),
  CONSTRAINT `c_conditions_1` FOREIGN KEY (`actionid`) REFERENCES `actions` (`actionid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conditions`
--

LOCK TABLES `conditions` WRITE;
/*!40000 ALTER TABLE `conditions` DISABLE KEYS */;
/*!40000 ALTER TABLE `conditions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `tenantid` varchar(64) DEFAULT '0',
  `configid` bigint(20) unsigned NOT NULL,
  `refresh_unsupported` int(11) NOT NULL DEFAULT '0',
  `work_period` varchar(100) NOT NULL DEFAULT '1-5,00:00-24:00',
  `alert_usrgrpid` bigint(20) unsigned DEFAULT NULL,
  `event_ack_enable` int(11) NOT NULL DEFAULT '1',
  `event_expire` int(11) NOT NULL DEFAULT '7',
  `event_show_max` int(11) NOT NULL DEFAULT '100',
  `default_theme` varchar(128) NOT NULL DEFAULT 'originalblue',
  `authentication_type` int(11) NOT NULL DEFAULT '0',
  `ldap_host` varchar(255) NOT NULL DEFAULT '',
  `ldap_port` int(11) NOT NULL DEFAULT '389',
  `ldap_base_dn` varchar(255) NOT NULL DEFAULT '',
  `ldap_bind_dn` varchar(255) NOT NULL DEFAULT '',
  `ldap_bind_password` varchar(128) NOT NULL DEFAULT '',
  `ldap_search_attribute` varchar(128) NOT NULL DEFAULT '',
  `dropdown_first_entry` int(11) NOT NULL DEFAULT '1',
  `dropdown_first_remember` int(11) NOT NULL DEFAULT '1',
  `discovery_groupid` bigint(20) unsigned NOT NULL,
  `max_in_table` int(11) NOT NULL DEFAULT '50',
  `search_limit` int(11) NOT NULL DEFAULT '1000',
  `severity_color_0` varchar(6) NOT NULL DEFAULT 'DBDBDB',
  `severity_color_1` varchar(6) NOT NULL DEFAULT 'D6F6FF',
  `severity_color_2` varchar(6) NOT NULL DEFAULT 'FFF6A5',
  `severity_color_3` varchar(6) NOT NULL DEFAULT 'FFB689',
  `severity_color_4` varchar(6) NOT NULL DEFAULT 'FF9999',
  `severity_color_5` varchar(6) NOT NULL DEFAULT 'FF3838',
  `severity_name_0` varchar(32) NOT NULL DEFAULT 'Not classified',
  `severity_name_1` varchar(32) NOT NULL DEFAULT 'Information',
  `severity_name_2` varchar(32) NOT NULL DEFAULT 'Warning',
  `severity_name_3` varchar(32) NOT NULL DEFAULT 'Average',
  `severity_name_4` varchar(32) NOT NULL DEFAULT 'High',
  `severity_name_5` varchar(32) NOT NULL DEFAULT 'Disaster',
  `ok_period` int(11) NOT NULL DEFAULT '1800',
  `blink_period` int(11) NOT NULL DEFAULT '1800',
  `problem_unack_color` varchar(6) NOT NULL DEFAULT 'DC0000',
  `problem_ack_color` varchar(6) NOT NULL DEFAULT 'DC0000',
  `ok_unack_color` varchar(6) NOT NULL DEFAULT '00AA00',
  `ok_ack_color` varchar(6) NOT NULL DEFAULT '00AA00',
  `problem_unack_style` int(11) NOT NULL DEFAULT '1',
  `problem_ack_style` int(11) NOT NULL DEFAULT '1',
  `ok_unack_style` int(11) NOT NULL DEFAULT '1',
  `ok_ack_style` int(11) NOT NULL DEFAULT '1',
  `snmptrap_logging` int(11) NOT NULL DEFAULT '1',
  `server_check_interval` int(11) NOT NULL DEFAULT '10',
  `hk_events_mode` int(11) NOT NULL DEFAULT '1',
  `hk_events_trigger` int(11) NOT NULL DEFAULT '365',
  `hk_events_internal` int(11) NOT NULL DEFAULT '365',
  `hk_events_discovery` int(11) NOT NULL DEFAULT '365',
  `hk_events_autoreg` int(11) NOT NULL DEFAULT '365',
  `hk_services_mode` int(11) NOT NULL DEFAULT '1',
  `hk_services` int(11) NOT NULL DEFAULT '365',
  `hk_audit_mode` int(11) NOT NULL DEFAULT '1',
  `hk_audit` int(11) NOT NULL DEFAULT '365',
  `hk_sessions_mode` int(11) NOT NULL DEFAULT '1',
  `hk_sessions` int(11) NOT NULL DEFAULT '365',
  `hk_history_mode` int(11) NOT NULL DEFAULT '1',
  `hk_history_global` int(11) NOT NULL DEFAULT '0',
  `hk_history` int(11) NOT NULL DEFAULT '90',
  `hk_trends_mode` int(11) NOT NULL DEFAULT '1',
  `hk_trends_global` int(11) NOT NULL DEFAULT '0',
  `hk_trends` int(11) NOT NULL DEFAULT '365',
  PRIMARY KEY (`configid`),
  KEY `config_1` (`alert_usrgrpid`),
  KEY `config_2` (`discovery_groupid`),
  CONSTRAINT `c_config_1` FOREIGN KEY (`alert_usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`),
  CONSTRAINT `c_config_2` FOREIGN KEY (`discovery_groupid`) REFERENCES `groups` (`groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES ('-',1,600,'1-5,09:00-18:00;',7,1,7,100,'originalblue',0,'',389,'','','','',1,1,5,10,1000,'DBDBDB','D6F6FF','FFF6A5','FFB689','FF9999','FF3838','Not classified','Information','Warning','Average','High','Disaster',1800,1800,'DC0000','DC0000','00AA00','00AA00',1,1,1,1,1,10,1,120,120,120,120,1,120,1,120,1,2,1,0,90,1,0,120);
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbversion`
--

DROP TABLE IF EXISTS `dbversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbversion` (
  `mandatory` int(11) NOT NULL DEFAULT '0',
  `optional` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbversion`
--

LOCK TABLES `dbversion` WRITE;
/*!40000 ALTER TABLE `dbversion` DISABLE KEYS */;
INSERT INTO `dbversion` VALUES (2020000,2020001);
/*!40000 ALTER TABLE `dbversion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dchecks`
--

DROP TABLE IF EXISTS `dchecks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dchecks` (
  `tenantid` varchar(64) DEFAULT '0',
  `dcheckid` bigint(20) unsigned NOT NULL,
  `druleid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `snmp_community` varchar(255) NOT NULL DEFAULT '',
  `ports` varchar(255) NOT NULL DEFAULT '0',
  `snmpv3_securityname` varchar(64) NOT NULL DEFAULT '',
  `snmpv3_securitylevel` int(11) NOT NULL DEFAULT '0',
  `snmpv3_authpassphrase` varchar(64) NOT NULL DEFAULT '',
  `snmpv3_privpassphrase` varchar(64) NOT NULL DEFAULT '',
  `uniq` int(11) NOT NULL DEFAULT '0',
  `snmpv3_authprotocol` int(11) NOT NULL DEFAULT '0',
  `snmpv3_privprotocol` int(11) NOT NULL DEFAULT '0',
  `snmpv3_contextname` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`dcheckid`),
  KEY `dchecks_1` (`druleid`),
  CONSTRAINT `c_dchecks_1` FOREIGN KEY (`druleid`) REFERENCES `drules` (`druleid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dchecks`
--

LOCK TABLES `dchecks` WRITE;
/*!40000 ALTER TABLE `dchecks` DISABLE KEYS */;
/*!40000 ALTER TABLE `dchecks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dhosts`
--

DROP TABLE IF EXISTS `dhosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dhosts` (
  `tenantid` varchar(64) DEFAULT '0',
  `dhostid` bigint(20) unsigned NOT NULL,
  `druleid` bigint(20) unsigned NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `lastup` int(11) NOT NULL DEFAULT '0',
  `lastdown` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`dhostid`),
  KEY `dhosts_1` (`druleid`),
  CONSTRAINT `c_dhosts_1` FOREIGN KEY (`druleid`) REFERENCES `drules` (`druleid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dhosts`
--

LOCK TABLES `dhosts` WRITE;
/*!40000 ALTER TABLE `dhosts` DISABLE KEYS */;
/*!40000 ALTER TABLE `dhosts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `drules`
--

DROP TABLE IF EXISTS `drules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drules` (
  `tenantid` varchar(64) DEFAULT '0',
  `druleid` bigint(20) unsigned NOT NULL,
  `proxy_hostid` bigint(20) unsigned DEFAULT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `iprange` varchar(255) NOT NULL DEFAULT '',
  `delay` int(11) NOT NULL DEFAULT '3600',
  `nextcheck` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`druleid`),
  KEY `drules_1` (`proxy_hostid`),
  CONSTRAINT `c_drules_1` FOREIGN KEY (`proxy_hostid`) REFERENCES `hosts` (`hostid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `drules`
--

LOCK TABLES `drules` WRITE;
/*!40000 ALTER TABLE `drules` DISABLE KEYS */;
/*!40000 ALTER TABLE `drules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dservices`
--

DROP TABLE IF EXISTS `dservices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dservices` (
  `tenantid` varchar(64) DEFAULT '0',
  `dserviceid` bigint(20) unsigned NOT NULL,
  `dhostid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `lastup` int(11) NOT NULL DEFAULT '0',
  `lastdown` int(11) NOT NULL DEFAULT '0',
  `dcheckid` bigint(20) unsigned NOT NULL,
  `ip` varchar(39) NOT NULL DEFAULT '',
  `dns` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`dserviceid`),
  UNIQUE KEY `dservices_1` (`dcheckid`,`type`,`key_`,`ip`,`port`),
  KEY `dservices_2` (`dhostid`),
  CONSTRAINT `c_dservices_1` FOREIGN KEY (`dhostid`) REFERENCES `dhosts` (`dhostid`) ON DELETE CASCADE,
  CONSTRAINT `c_dservices_2` FOREIGN KEY (`dcheckid`) REFERENCES `dchecks` (`dcheckid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dservices`
--

LOCK TABLES `dservices` WRITE;
/*!40000 ALTER TABLE `dservices` DISABLE KEYS */;
/*!40000 ALTER TABLE `dservices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `escalations`
--

DROP TABLE IF EXISTS `escalations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `escalations` (
  `tenantid` varchar(64) DEFAULT '0',
  `escalationid` bigint(20) unsigned NOT NULL,
  `actionid` bigint(20) unsigned NOT NULL,
  `triggerid` bigint(20) unsigned DEFAULT NULL,
  `eventid` bigint(20) unsigned DEFAULT NULL,
  `r_eventid` bigint(20) unsigned DEFAULT NULL,
  `nextcheck` int(11) NOT NULL DEFAULT '0',
  `esc_step` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `itemid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`escalationid`),
  UNIQUE KEY `escalations_1` (`actionid`,`triggerid`,`itemid`,`escalationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `escalations`
--

LOCK TABLES `escalations` WRITE;
/*!40000 ALTER TABLE `escalations` DISABLE KEYS */;
/*!40000 ALTER TABLE `escalations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `events` (
  `tenantid` varchar(64) DEFAULT '0',
  `eventid` bigint(20) unsigned NOT NULL,
  `source` int(11) NOT NULL DEFAULT '0',
  `object` int(11) NOT NULL DEFAULT '0',
  `objectid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` int(11) NOT NULL DEFAULT '0',
  `acknowledged` int(11) NOT NULL DEFAULT '0',
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`eventid`),
  KEY `events_1` (`source`,`object`,`objectid`,`clock`),
  KEY `events_2` (`source`,`object`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `expressions`
--

DROP TABLE IF EXISTS `expressions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `expressions` (
  `tenantid` varchar(64) DEFAULT '0',
  `expressionid` bigint(20) unsigned NOT NULL,
  `regexpid` bigint(20) unsigned NOT NULL,
  `expression` varchar(255) NOT NULL DEFAULT '',
  `expression_type` int(11) NOT NULL DEFAULT '0',
  `exp_delimiter` varchar(1) NOT NULL DEFAULT '',
  `case_sensitive` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`expressionid`),
  KEY `expressions_1` (`regexpid`),
  CONSTRAINT `c_expressions_1` FOREIGN KEY (`regexpid`) REFERENCES `regexps` (`regexpid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `expressions`
--

LOCK TABLES `expressions` WRITE;
/*!40000 ALTER TABLE `expressions` DISABLE KEYS */;
INSERT INTO `expressions` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,1,'^(btrfs|ext2|ext3|ext4|jfs|reiser|xfs|ffs|ufs|jfs|jfs2|vxfs|hfs|ntfs|fat32|zfs|overlay)$',3,',',0),('5e4d0a6d39a44b9c906a3173b448aa4a',2,2,'^lo$',4,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',3,3,'^(Physical memory|Virtual memory|Memory buffers|Cached memory|Swap space)$',4,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',4,2,'^Software Loopback Interface',4,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',5,4,'^(hd[a-z]+|sd[a-z]+|vd[a-z]+|dm-[0-9]+|drbd[0-9]+)$',3,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',6,5,'^(eth[0-9]+|enp[0-9]+s[0-9]+|em[0-9]+)$',3,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',7,6,'hrDeviceProcessor$',3,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',8,7,'^Physical memory$',3,',',0),('5e4d0a6d39a44b9c906a3173b448aa4a',9,8,'^(btrfs|ext2|ext3|ext4|jfs|reiser|xfs|ffs|ufs|jfs|jfs2|vxfs|hfs|ntfs|fat32|zfs|overlay)$',3,',',1),('5e4d0a6d39a44b9c906a3173b448aa4a',10,9,'^(Loop|WAN|RAS|Bluetooth|Wifi|TAP|vmware|Tunneling|ISATAP|QOS|Virtual|WFP|usb).$',4,',',0),('5e4d0a6d39a44b9c906a3173b448aa4a',11,10,'hrStorageFixedDisk',3,',',0);
/*!40000 ALTER TABLE `expressions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `functions`
--

DROP TABLE IF EXISTS `functions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `functions` (
  `tenantid` varchar(64) DEFAULT '0',
  `functionid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `triggerid` bigint(20) unsigned NOT NULL,
  `function` varchar(12) NOT NULL DEFAULT '',
  `parameter` varchar(255) NOT NULL DEFAULT '0',
  PRIMARY KEY (`functionid`),
  KEY `functions_1` (`triggerid`),
  KEY `functions_2` (`itemid`,`function`,`parameter`),
  CONSTRAINT `c_functions_1` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE,
  CONSTRAINT `c_functions_2` FOREIGN KEY (`triggerid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `functions`
--

LOCK TABLES `functions` WRITE;
/*!40000 ALTER TABLE `functions` DISABLE KEYS */;
INSERT INTO `functions` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',13350,23957,67,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13351,23959,68,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13417,24094,126,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13418,24096,127,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13424,24094,130,'sum','600'),('5e4d0a6d39a44b9c906a3173b448aa4a',13433,24191,136,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13434,24191,137,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13435,24147,138,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13436,24149,139,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13437,24150,140,'str','none'),('5e4d0a6d39a44b9c906a3173b448aa4a',13438,24163,141,'avg','60'),('5e4d0a6d39a44b9c906a3173b448aa4a',13439,24164,141,'avg','60'),('5e4d0a6d39a44b9c906a3173b448aa4a',13440,24162,141,'avg','60'),('5e4d0a6d39a44b9c906a3173b448aa4a',13441,24161,141,'avg','60'),('5e4d0a6d39a44b9c906a3173b448aa4a',13442,24169,142,'str','none'),('5e4d0a6d39a44b9c906a3173b448aa4a',13443,24147,143,'sum','600'),('5e4d0a6d39a44b9c906a3173b448aa4a',13444,24174,144,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13445,24175,144,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13446,24188,145,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13447,24170,145,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13448,24193,146,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13449,24171,146,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13450,24201,147,'str','none'),('5e4d0a6d39a44b9c906a3173b448aa4a',13451,24204,148,'str','none'),('5e4d0a6d39a44b9c906a3173b448aa4a',13636,25047,324,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13637,25043,325,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13638,25044,326,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13639,25042,327,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13640,25053,327,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13641,25048,328,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13642,25045,329,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13643,25062,330,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13644,25030,331,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13645,25030,332,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13646,25033,333,'last','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13647,25034,334,'change','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13648,25433,335,'min','300'),('5e4d0a6d39a44b9c906a3173b448aa4a',13650,25622,337,'diff','0'),('5e4d0a6d39a44b9c906a3173b448aa4a',13651,25795,338,'diff','0');
/*!40000 ALTER TABLE `functions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `globalmacro`
--

DROP TABLE IF EXISTS `globalmacro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `globalmacro` (
  `tenantid` varchar(64) DEFAULT '0',
  `globalmacroid` bigint(20) unsigned NOT NULL,
  `macro` varchar(64) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`globalmacroid`),
  KEY `globalmacro_1` (`macro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `globalmacro`
--

LOCK TABLES `globalmacro` WRITE;
/*!40000 ALTER TABLE `globalmacro` DISABLE KEYS */;
INSERT INTO `globalmacro` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',2,'{$SNMP_COMMUNITY}','public');
/*!40000 ALTER TABLE `globalmacro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `globalvars`
--

DROP TABLE IF EXISTS `globalvars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `globalvars` (
  `tenantid` varchar(64) DEFAULT '0',
  `globalvarid` bigint(20) unsigned NOT NULL,
  `snmp_lastsize` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`globalvarid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `globalvars`
--

LOCK TABLES `globalvars` WRITE;
/*!40000 ALTER TABLE `globalvars` DISABLE KEYS */;
INSERT INTO `globalvars` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,7088840);
/*!40000 ALTER TABLE `globalvars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `graph_discovery`
--

DROP TABLE IF EXISTS `graph_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graph_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `graphdiscoveryid` bigint(20) unsigned NOT NULL,
  `graphid` bigint(20) unsigned NOT NULL,
  `parent_graphid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`graphdiscoveryid`),
  UNIQUE KEY `graph_discovery_1` (`graphid`,`parent_graphid`),
  KEY `graph_discovery_2` (`parent_graphid`),
  CONSTRAINT `c_graph_discovery_1` FOREIGN KEY (`graphid`) REFERENCES `graphs` (`graphid`) ON DELETE CASCADE,
  CONSTRAINT `c_graph_discovery_2` FOREIGN KEY (`parent_graphid`) REFERENCES `graphs` (`graphid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graph_discovery`
--

LOCK TABLES `graph_discovery` WRITE;
/*!40000 ALTER TABLE `graph_discovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `graph_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `graph_theme`
--

DROP TABLE IF EXISTS `graph_theme`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graph_theme` (
  `tenantid` varchar(64) DEFAULT '0',
  `graphthemeid` bigint(20) unsigned NOT NULL,
  `description` varchar(64) NOT NULL DEFAULT '',
  `theme` varchar(64) NOT NULL DEFAULT '',
  `backgroundcolor` varchar(6) NOT NULL DEFAULT 'F0F0F0',
  `graphcolor` varchar(6) NOT NULL DEFAULT 'FFFFFF',
  `graphbordercolor` varchar(6) NOT NULL DEFAULT '222222',
  `gridcolor` varchar(6) NOT NULL DEFAULT 'CCCCCC',
  `maingridcolor` varchar(6) NOT NULL DEFAULT 'AAAAAA',
  `gridbordercolor` varchar(6) NOT NULL DEFAULT '000000',
  `textcolor` varchar(6) NOT NULL DEFAULT '202020',
  `highlightcolor` varchar(6) NOT NULL DEFAULT 'AA4444',
  `leftpercentilecolor` varchar(6) NOT NULL DEFAULT '11CC11',
  `rightpercentilecolor` varchar(6) NOT NULL DEFAULT 'CC1111',
  `nonworktimecolor` varchar(6) NOT NULL DEFAULT 'CCCCCC',
  `gridview` int(11) NOT NULL DEFAULT '1',
  `legendview` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`graphthemeid`),
  KEY `graph_theme_1` (`description`),
  KEY `graph_theme_2` (`theme`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graph_theme`
--

LOCK TABLES `graph_theme` WRITE;
/*!40000 ALTER TABLE `graph_theme` DISABLE KEYS */;
INSERT INTO `graph_theme` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,'Original Blue','originalblue','F0F0F0','FFFFFF','333333','CCCCCC','AAAAAA','000000','222222','AA4444','11CC11','CC1111','E0E0E0',1,1),('5e4d0a6d39a44b9c906a3173b448aa4a',2,'Black & Blue','darkblue','333333','0A0A0A','888888','222222','4F4F4F','EFEFEF','0088FF','CC4444','1111FF','FF1111','1F1F1F',1,1),('5e4d0a6d39a44b9c906a3173b448aa4a',3,'Dark orange','darkorange','333333','0A0A0A','888888','222222','4F4F4F','EFEFEF','DFDFDF','FF5500','FF5500','FF1111','1F1F1F',1,1),('5e4d0a6d39a44b9c906a3173b448aa4a',4,'Classic','classic','F0F0F0','FFFFFF','333333','CCCCCC','AAAAAA','000000','222222','AA4444','11CC11','CC1111','E0E0E0',1,1);
/*!40000 ALTER TABLE `graph_theme` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `graphs`
--

DROP TABLE IF EXISTS `graphs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphs` (
  `tenantid` varchar(64) DEFAULT '0',
  `graphid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `width` int(11) NOT NULL DEFAULT '900',
  `height` int(11) NOT NULL DEFAULT '200',
  `yaxismin` double(16,4) NOT NULL DEFAULT '0.0000',
  `yaxismax` double(16,4) NOT NULL DEFAULT '100.0000',
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `show_work_period` int(11) NOT NULL DEFAULT '1',
  `show_triggers` int(11) NOT NULL DEFAULT '1',
  `graphtype` int(11) NOT NULL DEFAULT '0',
  `show_legend` int(11) NOT NULL DEFAULT '1',
  `show_3d` int(11) NOT NULL DEFAULT '0',
  `percent_left` double(16,4) NOT NULL DEFAULT '0.0000',
  `percent_right` double(16,4) NOT NULL DEFAULT '0.0000',
  `ymin_type` int(11) NOT NULL DEFAULT '0',
  `ymax_type` int(11) NOT NULL DEFAULT '0',
  `ymin_itemid` bigint(20) unsigned DEFAULT NULL,
  `ymax_itemid` bigint(20) unsigned DEFAULT NULL,
  `flags` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`graphid`),
  KEY `graphs_1` (`name`),
  KEY `graphs_2` (`templateid`),
  KEY `graphs_3` (`ymin_itemid`),
  KEY `graphs_4` (`ymax_itemid`),
  CONSTRAINT `c_graphs_1` FOREIGN KEY (`templateid`) REFERENCES `graphs` (`graphid`) ON DELETE CASCADE,
  CONSTRAINT `c_graphs_2` FOREIGN KEY (`ymin_itemid`) REFERENCES `items` (`itemid`),
  CONSTRAINT `c_graphs_3` FOREIGN KEY (`ymax_itemid`) REFERENCES `items` (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graphs`
--

LOCK TABLES `graphs` WRITE;
/*!40000 ALTER TABLE `graphs` DISABLE KEYS */;
INSERT INTO `graphs` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',548,'Archivelog',900,200,0.0000,100.0000,NULL,1,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',549,'DB Size/FileSize',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',550,'Hit Ratio',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',551,'Logical I/O',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',552,'PGA',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',553,'Physical I/O',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',554,'Pin Hit Ratio',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',555,'Shared Pool',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',556,'Session/Process',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',557,'Sessions',900,200,0.0000,100.0000,NULL,1,1,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',558,'SGA Memory Usage',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',559,'Memory Utilization',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',560,'Events Waits',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',606,'Ceph cluster storage',500,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',607,'Ceph Load',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',608,'Ceph space repartition',500,200,0.0000,0.0000,NULL,0,0,2,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',609,'Degraded %',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',610,'Moving PGs',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',611,'OSDs',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',612,'PGS',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',613,'Problem PGs',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',627,'Archivelog',900,200,0.0000,100.0000,NULL,1,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',629,'Hit Ratio',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',638,'Archivelog',900,200,0.0000,100.0000,NULL,1,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',640,'Events Waits',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',641,'Hit Ratio',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',642,'Logical I/O',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',643,'PGA',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',644,'Physical I/O',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',645,'Pin Hit Ratio',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,1,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',646,'Session/Process',900,200,0.0000,100.0000,NULL,0,0,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',647,'Sessions',900,200,0.0000,100.0000,NULL,1,1,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',648,'SGA Memory Usage',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',649,'Shared Pool',900,200,0.0000,100.0000,NULL,0,0,1,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',726,'Switch :: Bandwidth {#SNMPVALUE}',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,2),('5e4d0a6d39a44b9c906a3173b448aa4a',728,'Switch :: MP {#SNMPVALUE}',900,200,0.0000,100.0000,NULL,1,1,1,1,0,0.0000,0.0000,0,0,NULL,NULL,2),('5e4d0a6d39a44b9c906a3173b448aa4a',729,'Switch :: CPU load',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,1,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',730,'MongoDB Background Flushes',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',731,'MongoDB Commands',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',732,'MongoDB Current Connections',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',733,'MongoDB Index Ops',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',734,'MongoDB Memory',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',735,'MongoDB Networks',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',736,'MSSQL SysLoad',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,0),('5e4d0a6d39a44b9c906a3173b448aa4a',738,'Traffic on interface {#SNMPVALUE}',900,200,0.0000,100.0000,NULL,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,2),('5e4d0a6d39a44b9c906a3173b448aa4a',739,'Traffic on interface {#SNMPVALUE}',900,200,0.0000,100.0000,738,1,1,0,1,0,0.0000,0.0000,0,0,NULL,NULL,2);
/*!40000 ALTER TABLE `graphs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `graphs_items`
--

DROP TABLE IF EXISTS `graphs_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `graphs_items` (
  `tenantid` varchar(64) DEFAULT '0',
  `gitemid` bigint(20) unsigned NOT NULL,
  `graphid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `drawtype` int(11) NOT NULL DEFAULT '0',
  `sortorder` int(11) NOT NULL DEFAULT '0',
  `color` varchar(6) NOT NULL DEFAULT '009600',
  `yaxisside` int(11) NOT NULL DEFAULT '0',
  `calc_fnc` int(11) NOT NULL DEFAULT '2',
  `type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gitemid`),
  KEY `graphs_items_1` (`itemid`),
  KEY `graphs_items_2` (`graphid`),
  CONSTRAINT `c_graphs_items_1` FOREIGN KEY (`graphid`) REFERENCES `graphs` (`graphid`) ON DELETE CASCADE,
  CONSTRAINT `c_graphs_items_2` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `graphs_items`
--

LOCK TABLES `graphs_items` WRITE;
/*!40000 ALTER TABLE `graphs_items` DISABLE KEYS */;
INSERT INTO `graphs_items` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',2621,606,23973,1,0,'00EE00',0,1,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2622,606,23974,1,1,'EE0000',0,4,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2623,607,23950,1,1,'C80000',1,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2624,607,23976,0,0,'00C800',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2625,607,23975,0,0,'0000C8',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2626,608,23972,0,0,'00EE00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2627,608,23974,0,1,'EE0000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2628,609,23958,5,0,'CC0000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2629,610,23963,0,0,'C80000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2630,610,23964,0,1,'00C800',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2631,610,23962,0,2,'0000C8',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2632,611,23952,5,0,'00EE00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2633,611,23951,2,1,'CC0000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2634,612,23955,2,1,'00EE00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2635,612,23953,5,0,'0000EE',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2636,613,23957,0,0,'00EE00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2637,613,23959,0,3,'EE0000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2638,613,23960,0,1,'0000C8',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2639,613,23961,0,2,'C800C8',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2640,726,25040,5,1,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2641,726,25039,5,0,'00AA00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2653,728,25061,0,1,'9999FF',0,4,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2654,728,25060,0,0,'66FF66',0,4,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2655,729,25030,0,2,'990000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2656,738,25621,5,0,'00AA00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2657,738,25624,5,1,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2658,730,25387,0,0,'FF3333',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2659,730,25388,0,1,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2660,730,25386,0,1,'33FF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2661,730,25389,0,2,'FFFF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2662,731,25403,0,0,'FF3333',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2663,731,25401,0,1,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2664,731,25400,0,1,'000000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2665,731,25402,0,2,'FF33FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2666,731,25405,0,3,'33FF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2667,731,25404,0,4,'FFFF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2668,732,25390,0,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2669,733,25391,0,0,'FF33FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2670,733,25394,0,0,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2671,733,25393,0,1,'FF3333',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2672,733,25392,0,2,'33FF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2673,734,25397,0,0,'FF3333',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2674,734,25396,0,0,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2675,734,25395,0,1,'33FF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2676,735,25398,1,0,'33FF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2677,735,25399,0,1,'3333FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2680,736,25430,0,0,'C80000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2681,736,25431,0,1,'00C800',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2682,736,25433,0,2,'0000C8',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2683,736,25436,0,3,'C800C8',1,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2684,736,25437,0,4,'00C8C8',1,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2685,638,24149,0,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2686,640,24207,1,0,'9999FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2687,640,24213,1,5,'888888',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2688,640,24212,1,2,'FF00FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2689,640,24209,1,7,'FF9999',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2690,640,24210,1,3,'FFFF00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2691,640,24208,1,0,'CCFFCC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2692,640,24206,1,4,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2693,640,24205,1,6,'00CCCC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2694,640,24211,1,1,'FF3333',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2695,641,24163,0,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2696,641,24164,0,3,'0000CC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2697,641,24162,0,2,'FF99FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2698,641,24161,0,1,'FFFF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2699,642,24166,1,0,'0000FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2700,642,24167,1,2,'FF6666',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2701,642,24165,1,1,'00FFFF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2702,643,24174,0,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2703,644,24176,0,1,'00FFFF',0,7,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2704,644,24178,0,0,'0000CC',0,7,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2705,644,24177,0,2,'FF6666',0,7,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2706,645,24179,0,1,'999900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2707,645,24180,0,2,'990099',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2708,645,24182,0,3,'00CC00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2709,645,24181,0,0,'000099',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2710,646,24193,0,0,'0000FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2711,646,24188,0,1,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2712,647,24194,1,1,'FF00FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2713,647,24191,1,2,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2714,647,24192,1,0,'0000FF',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2715,648,24199,1,4,'CCCC00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2716,648,24200,1,5,'0000CC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2717,648,24195,1,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2718,648,24196,1,1,'CC0000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2719,648,24197,1,2,'00CCCC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2720,648,24198,1,3,'CC00CC',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2721,649,24187,1,4,'CCCC00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2722,649,24183,1,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2723,649,24184,1,1,'990000',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2724,649,24186,1,3,'990099',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2725,649,24185,1,2,'009999',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2726,627,24096,0,0,'009900',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2727,629,24095,0,1,'FFFF33',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2728,739,25794,5,0,'00AA00',0,2,0),('5e4d0a6d39a44b9c906a3173b448aa4a',2729,739,25797,5,1,'3333FF',0,2,0);
/*!40000 ALTER TABLE `graphs_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_discovery`
--

DROP TABLE IF EXISTS `group_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `groupid` bigint(20) unsigned NOT NULL,
  `parent_group_prototypeid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `lastcheck` int(11) NOT NULL DEFAULT '0',
  `ts_delete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`groupid`),
  KEY `c_group_discovery_2` (`parent_group_prototypeid`),
  CONSTRAINT `c_group_discovery_1` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`) ON DELETE CASCADE,
  CONSTRAINT `c_group_discovery_2` FOREIGN KEY (`parent_group_prototypeid`) REFERENCES `group_prototype` (`group_prototypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_discovery`
--

LOCK TABLES `group_discovery` WRITE;
/*!40000 ALTER TABLE `group_discovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_prototype`
--

DROP TABLE IF EXISTS `group_prototype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group_prototype` (
  `tenantid` varchar(64) DEFAULT '0',
  `group_prototypeid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `groupid` bigint(20) unsigned DEFAULT NULL,
  `templateid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`group_prototypeid`),
  KEY `group_prototype_1` (`hostid`),
  KEY `c_group_prototype_2` (`groupid`),
  KEY `c_group_prototype_3` (`templateid`),
  CONSTRAINT `c_group_prototype_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_group_prototype_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`),
  CONSTRAINT `c_group_prototype_3` FOREIGN KEY (`templateid`) REFERENCES `group_prototype` (`group_prototypeid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_prototype`
--

LOCK TABLES `group_prototype` WRITE;
/*!40000 ALTER TABLE `group_prototype` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_prototype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `groups`
--

DROP TABLE IF EXISTS `groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `groups` (
  `tenantid` varchar(64) DEFAULT '0',
  `groupid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `internal` int(11) NOT NULL DEFAULT '0',
  `flags` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`groupid`),
  KEY `groups_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `groups`
--

LOCK TABLES `groups` WRITE;
/*!40000 ALTER TABLE `groups` DISABLE KEYS */;
INSERT INTO `groups` VALUES ('-',5,'Discovered hosts',1,0),('-',19,'Templates',0,0),('-',101,'服务器Linux',0,0),('-',102,'服务器Windows',0,0),('-',201,'云主机',0,0),('-',301,'网络设备Cisco',0,0),('-',302,'网络设备通用',0,0),('-',303,'网络设备华为',0,0),('-',304,'网络设备中兴',0,0),('|',401,'存储设备',0,0),('-',501,'数据库MySQL',0,0),('-',502,'数据库Oracle',0,0),('|',503,'数据库达梦',0,0),('-',504,'数据库DB2',0,0),('-',505,'数据库MSSQL',0,0),('-',506,'数据库MongoDB',0,0),('-',601,'中间件Tomcat',0,0),('-',602,'中间件IIS',0,0),('-',603,'中间件WebLogic',0,0),('-',604,'中间件WebSphere',0,0),('|',701,'Web服务',0,0),('-',801,'云控制服务',0,0),('-',802,'云计算服务',0,0),('-',803,'云存储服务',0,0),('-',804,'云网络服务',0,0),('-',805,'云门户服务',0,0);
/*!40000 ALTER TABLE `groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` double(16,4) NOT NULL DEFAULT '0.0000',
  `ns` int(11) NOT NULL DEFAULT '0',
  KEY `history_1` (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (clock)
(PARTITION p0 VALUES LESS THAN (1438531200) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (1439136000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (1439740800) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (1440345600) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (1440950400) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (1441555200) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (1442160000) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (1442764800) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (1443369600) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (1443974400) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (1444579200) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (1445184000) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (1445788800) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (1446393600) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (1446998400) ENGINE = InnoDB,
 PARTITION p15 VALUES LESS THAN (1447603200) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (1448208000) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (1448812800) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (1449417600) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (1450022400) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (1450627200) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (1451232000) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (1451836800) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (1452441600) ENGINE = InnoDB,
 PARTITION p24 VALUES LESS THAN (1453046400) ENGINE = InnoDB,
 PARTITION p25 VALUES LESS THAN (1453651200) ENGINE = InnoDB,
 PARTITION p26 VALUES LESS THAN (1454256000) ENGINE = InnoDB,
 PARTITION p27 VALUES LESS THAN (1454860800) ENGINE = InnoDB,
 PARTITION p28 VALUES LESS THAN (1455465600) ENGINE = InnoDB,
 PARTITION p29 VALUES LESS THAN (1456070400) ENGINE = InnoDB,
 PARTITION p30 VALUES LESS THAN (1456675200) ENGINE = InnoDB,
 PARTITION p31 VALUES LESS THAN (1457280000) ENGINE = InnoDB,
 PARTITION p32 VALUES LESS THAN (1457884800) ENGINE = InnoDB,
 PARTITION p33 VALUES LESS THAN (1458489600) ENGINE = InnoDB,
 PARTITION p34 VALUES LESS THAN (1459094400) ENGINE = InnoDB,
 PARTITION p35 VALUES LESS THAN (1459699200) ENGINE = InnoDB,
 PARTITION p36 VALUES LESS THAN (1460304000) ENGINE = InnoDB,
 PARTITION p37 VALUES LESS THAN (1460908800) ENGINE = InnoDB,
 PARTITION p38 VALUES LESS THAN (1461513600) ENGINE = InnoDB,
 PARTITION p39 VALUES LESS THAN (1462118400) ENGINE = InnoDB,
 PARTITION p40 VALUES LESS THAN (1462723200) ENGINE = InnoDB,
 PARTITION p41 VALUES LESS THAN (1463328000) ENGINE = InnoDB,
 PARTITION p42 VALUES LESS THAN (1463932800) ENGINE = InnoDB,
 PARTITION p43 VALUES LESS THAN (1464537600) ENGINE = InnoDB,
 PARTITION p44 VALUES LESS THAN (1465142400) ENGINE = InnoDB,
 PARTITION p45 VALUES LESS THAN (1465747200) ENGINE = InnoDB,
 PARTITION p46 VALUES LESS THAN (1466352000) ENGINE = InnoDB,
 PARTITION p47 VALUES LESS THAN (1466956800) ENGINE = InnoDB,
 PARTITION p48 VALUES LESS THAN (1467561600) ENGINE = InnoDB,
 PARTITION p49 VALUES LESS THAN (1468166400) ENGINE = InnoDB,
 PARTITION p50 VALUES LESS THAN (1468771200) ENGINE = InnoDB,
 PARTITION p51 VALUES LESS THAN (1469376000) ENGINE = InnoDB,
 PARTITION p52 VALUES LESS THAN (1469980800) ENGINE = InnoDB,
 PARTITION p53 VALUES LESS THAN (1470585600) ENGINE = InnoDB,
 PARTITION p54 VALUES LESS THAN (1471190400) ENGINE = InnoDB,
 PARTITION p55 VALUES LESS THAN (1471795200) ENGINE = InnoDB,
 PARTITION p56 VALUES LESS THAN (1472400000) ENGINE = InnoDB,
 PARTITION p57 VALUES LESS THAN (1473004800) ENGINE = InnoDB,
 PARTITION p58 VALUES LESS THAN (1473609600) ENGINE = InnoDB,
 PARTITION p59 VALUES LESS THAN (1474214400) ENGINE = InnoDB,
 PARTITION p60 VALUES LESS THAN (1474819200) ENGINE = InnoDB,
 PARTITION p61 VALUES LESS THAN (1475424000) ENGINE = InnoDB,
 PARTITION p62 VALUES LESS THAN (1476028800) ENGINE = InnoDB,
 PARTITION p63 VALUES LESS THAN (1476633600) ENGINE = InnoDB,
 PARTITION p64 VALUES LESS THAN (1477238400) ENGINE = InnoDB,
 PARTITION p65 VALUES LESS THAN (1477843200) ENGINE = InnoDB,
 PARTITION p66 VALUES LESS THAN (1478448000) ENGINE = InnoDB,
 PARTITION p67 VALUES LESS THAN (1479052800) ENGINE = InnoDB,
 PARTITION p68 VALUES LESS THAN (1479657600) ENGINE = InnoDB,
 PARTITION p69 VALUES LESS THAN (1480262400) ENGINE = InnoDB,
 PARTITION p70 VALUES LESS THAN (1480867200) ENGINE = InnoDB,
 PARTITION p71 VALUES LESS THAN (1481472000) ENGINE = InnoDB,
 PARTITION p72 VALUES LESS THAN (1482076800) ENGINE = InnoDB,
 PARTITION p73 VALUES LESS THAN (1482681600) ENGINE = InnoDB,
 PARTITION p74 VALUES LESS THAN (1483286400) ENGINE = InnoDB,
 PARTITION p75 VALUES LESS THAN (1483891200) ENGINE = InnoDB,
 PARTITION p76 VALUES LESS THAN (1484496000) ENGINE = InnoDB,
 PARTITION p77 VALUES LESS THAN (1485100800) ENGINE = InnoDB,
 PARTITION p78 VALUES LESS THAN (1485705600) ENGINE = InnoDB,
 PARTITION p79 VALUES LESS THAN (1486310400) ENGINE = InnoDB,
 PARTITION p80 VALUES LESS THAN (1486915200) ENGINE = InnoDB,
 PARTITION p81 VALUES LESS THAN (1487520000) ENGINE = InnoDB,
 PARTITION p82 VALUES LESS THAN (1488124800) ENGINE = InnoDB,
 PARTITION p83 VALUES LESS THAN (1488729600) ENGINE = InnoDB,
 PARTITION p84 VALUES LESS THAN (1489334400) ENGINE = InnoDB,
 PARTITION p85 VALUES LESS THAN (1489939200) ENGINE = InnoDB,
 PARTITION p86 VALUES LESS THAN (1490544000) ENGINE = InnoDB,
 PARTITION p87 VALUES LESS THAN (1491148800) ENGINE = InnoDB,
 PARTITION p88 VALUES LESS THAN (1491753600) ENGINE = InnoDB,
 PARTITION p89 VALUES LESS THAN (1492358400) ENGINE = InnoDB,
 PARTITION p90 VALUES LESS THAN (1492963200) ENGINE = InnoDB,
 PARTITION p91 VALUES LESS THAN (1493568000) ENGINE = InnoDB,
 PARTITION p92 VALUES LESS THAN (1494172800) ENGINE = InnoDB,
 PARTITION p93 VALUES LESS THAN (1494777600) ENGINE = InnoDB,
 PARTITION p94 VALUES LESS THAN (1495382400) ENGINE = InnoDB,
 PARTITION p95 VALUES LESS THAN (1495987200) ENGINE = InnoDB,
 PARTITION p96 VALUES LESS THAN (1496592000) ENGINE = InnoDB,
 PARTITION p97 VALUES LESS THAN (1497196800) ENGINE = InnoDB,
 PARTITION p98 VALUES LESS THAN (1497801600) ENGINE = InnoDB,
 PARTITION p99 VALUES LESS THAN (1498406400) ENGINE = InnoDB,
 PARTITION p100 VALUES LESS THAN (1499011200) ENGINE = InnoDB,
 PARTITION p101 VALUES LESS THAN (1499616000) ENGINE = InnoDB,
 PARTITION p102 VALUES LESS THAN (1500220800) ENGINE = InnoDB,
 PARTITION p103 VALUES LESS THAN (1500825600) ENGINE = InnoDB,
 PARTITION p104 VALUES LESS THAN (1501430400) ENGINE = InnoDB,
 PARTITION p105 VALUES LESS THAN (1502035200) ENGINE = InnoDB,
 PARTITION p106 VALUES LESS THAN (1502640000) ENGINE = InnoDB,
 PARTITION p107 VALUES LESS THAN (1503244800) ENGINE = InnoDB,
 PARTITION p108 VALUES LESS THAN (1503849600) ENGINE = InnoDB,
 PARTITION p109 VALUES LESS THAN (1504454400) ENGINE = InnoDB,
 PARTITION p110 VALUES LESS THAN (1505059200) ENGINE = InnoDB,
 PARTITION p111 VALUES LESS THAN (1505664000) ENGINE = InnoDB,
 PARTITION p112 VALUES LESS THAN (1506268800) ENGINE = InnoDB,
 PARTITION p113 VALUES LESS THAN (1506873600) ENGINE = InnoDB,
 PARTITION p114 VALUES LESS THAN (1507478400) ENGINE = InnoDB,
 PARTITION p115 VALUES LESS THAN (1508083200) ENGINE = InnoDB,
 PARTITION p116 VALUES LESS THAN (1508688000) ENGINE = InnoDB,
 PARTITION p117 VALUES LESS THAN (1509292800) ENGINE = InnoDB,
 PARTITION p118 VALUES LESS THAN (1509897600) ENGINE = InnoDB,
 PARTITION p119 VALUES LESS THAN (1510502400) ENGINE = InnoDB,
 PARTITION p120 VALUES LESS THAN (1511107200) ENGINE = InnoDB,
 PARTITION p121 VALUES LESS THAN (1511712000) ENGINE = InnoDB,
 PARTITION p122 VALUES LESS THAN (1512316800) ENGINE = InnoDB,
 PARTITION p123 VALUES LESS THAN (1512921600) ENGINE = InnoDB,
 PARTITION p124 VALUES LESS THAN (1513526400) ENGINE = InnoDB,
 PARTITION p125 VALUES LESS THAN (1514131200) ENGINE = InnoDB,
 PARTITION p126 VALUES LESS THAN (1514736000) ENGINE = InnoDB,
 PARTITION p127 VALUES LESS THAN (1515340800) ENGINE = InnoDB,
 PARTITION p128 VALUES LESS THAN (1515945600) ENGINE = InnoDB,
 PARTITION p129 VALUES LESS THAN (1516550400) ENGINE = InnoDB,
 PARTITION p130 VALUES LESS THAN (1517155200) ENGINE = InnoDB,
 PARTITION p131 VALUES LESS THAN (1517760000) ENGINE = InnoDB,
 PARTITION p132 VALUES LESS THAN (1518364800) ENGINE = InnoDB,
 PARTITION p133 VALUES LESS THAN (1518969600) ENGINE = InnoDB,
 PARTITION p134 VALUES LESS THAN (1519574400) ENGINE = InnoDB,
 PARTITION p135 VALUES LESS THAN (1520179200) ENGINE = InnoDB,
 PARTITION p136 VALUES LESS THAN (1520784000) ENGINE = InnoDB,
 PARTITION p137 VALUES LESS THAN (1521388800) ENGINE = InnoDB,
 PARTITION p138 VALUES LESS THAN (1521993600) ENGINE = InnoDB,
 PARTITION p139 VALUES LESS THAN (1522598400) ENGINE = InnoDB,
 PARTITION p140 VALUES LESS THAN (1523203200) ENGINE = InnoDB,
 PARTITION p141 VALUES LESS THAN (1523808000) ENGINE = InnoDB,
 PARTITION p142 VALUES LESS THAN (1524412800) ENGINE = InnoDB,
 PARTITION p143 VALUES LESS THAN (1525017600) ENGINE = InnoDB,
 PARTITION p144 VALUES LESS THAN (1525622400) ENGINE = InnoDB,
 PARTITION p145 VALUES LESS THAN (1526227200) ENGINE = InnoDB,
 PARTITION p146 VALUES LESS THAN (1526832000) ENGINE = InnoDB,
 PARTITION p147 VALUES LESS THAN (1527436800) ENGINE = InnoDB,
 PARTITION p148 VALUES LESS THAN (1528041600) ENGINE = InnoDB,
 PARTITION p149 VALUES LESS THAN (1528646400) ENGINE = InnoDB,
 PARTITION p150 VALUES LESS THAN (1529251200) ENGINE = InnoDB,
 PARTITION p151 VALUES LESS THAN (1529856000) ENGINE = InnoDB,
 PARTITION p152 VALUES LESS THAN (1530460800) ENGINE = InnoDB,
 PARTITION p153 VALUES LESS THAN (1531065600) ENGINE = InnoDB,
 PARTITION p154 VALUES LESS THAN (1531670400) ENGINE = InnoDB,
 PARTITION p155 VALUES LESS THAN (1532275200) ENGINE = InnoDB,
 PARTITION p156 VALUES LESS THAN (1532880000) ENGINE = InnoDB,
 PARTITION p157 VALUES LESS THAN (1533484800) ENGINE = InnoDB,
 PARTITION p158 VALUES LESS THAN (1534089600) ENGINE = InnoDB,
 PARTITION p159 VALUES LESS THAN (1534694400) ENGINE = InnoDB,
 PARTITION p160 VALUES LESS THAN (1535299200) ENGINE = InnoDB,
 PARTITION p161 VALUES LESS THAN (1535904000) ENGINE = InnoDB,
 PARTITION p162 VALUES LESS THAN (1536508800) ENGINE = InnoDB,
 PARTITION p163 VALUES LESS THAN (1537113600) ENGINE = InnoDB,
 PARTITION p164 VALUES LESS THAN (1537718400) ENGINE = InnoDB,
 PARTITION p165 VALUES LESS THAN (1538323200) ENGINE = InnoDB,
 PARTITION p166 VALUES LESS THAN (1538928000) ENGINE = InnoDB,
 PARTITION p167 VALUES LESS THAN (1539532800) ENGINE = InnoDB,
 PARTITION p168 VALUES LESS THAN (1540137600) ENGINE = InnoDB,
 PARTITION p169 VALUES LESS THAN (1540742400) ENGINE = InnoDB,
 PARTITION p170 VALUES LESS THAN (1541347200) ENGINE = InnoDB,
 PARTITION p171 VALUES LESS THAN (1541952000) ENGINE = InnoDB,
 PARTITION p172 VALUES LESS THAN (1542556800) ENGINE = InnoDB,
 PARTITION p173 VALUES LESS THAN (1543161600) ENGINE = InnoDB,
 PARTITION p174 VALUES LESS THAN (1543766400) ENGINE = InnoDB,
 PARTITION p175 VALUES LESS THAN (1544371200) ENGINE = InnoDB,
 PARTITION p176 VALUES LESS THAN (1544976000) ENGINE = InnoDB,
 PARTITION p177 VALUES LESS THAN (1545580800) ENGINE = InnoDB,
 PARTITION p178 VALUES LESS THAN (1546185600) ENGINE = InnoDB,
 PARTITION p179 VALUES LESS THAN (1546790400) ENGINE = InnoDB,
 PARTITION p180 VALUES LESS THAN (1547395200) ENGINE = InnoDB,
 PARTITION p181 VALUES LESS THAN (1548000000) ENGINE = InnoDB,
 PARTITION p182 VALUES LESS THAN (1548604800) ENGINE = InnoDB,
 PARTITION p183 VALUES LESS THAN (1549209600) ENGINE = InnoDB,
 PARTITION p184 VALUES LESS THAN (1549814400) ENGINE = InnoDB,
 PARTITION p185 VALUES LESS THAN (1550419200) ENGINE = InnoDB,
 PARTITION p186 VALUES LESS THAN (1551024000) ENGINE = InnoDB,
 PARTITION p187 VALUES LESS THAN (1551628800) ENGINE = InnoDB,
 PARTITION p188 VALUES LESS THAN (1552233600) ENGINE = InnoDB,
 PARTITION p189 VALUES LESS THAN (1552838400) ENGINE = InnoDB,
 PARTITION p190 VALUES LESS THAN (1553443200) ENGINE = InnoDB,
 PARTITION p191 VALUES LESS THAN (1554048000) ENGINE = InnoDB,
 PARTITION p192 VALUES LESS THAN (1554652800) ENGINE = InnoDB,
 PARTITION p193 VALUES LESS THAN (1555257600) ENGINE = InnoDB,
 PARTITION p194 VALUES LESS THAN (1555862400) ENGINE = InnoDB,
 PARTITION p195 VALUES LESS THAN (1556467200) ENGINE = InnoDB,
 PARTITION p196 VALUES LESS THAN (1557072000) ENGINE = InnoDB,
 PARTITION p197 VALUES LESS THAN (1557676800) ENGINE = InnoDB,
 PARTITION p198 VALUES LESS THAN (1558281600) ENGINE = InnoDB,
 PARTITION p199 VALUES LESS THAN (1558886400) ENGINE = InnoDB,
 PARTITION p200 VALUES LESS THAN (1559491200) ENGINE = InnoDB,
 PARTITION p201 VALUES LESS THAN (1560096000) ENGINE = InnoDB,
 PARTITION p202 VALUES LESS THAN (1560700800) ENGINE = InnoDB,
 PARTITION p203 VALUES LESS THAN (1561305600) ENGINE = InnoDB,
 PARTITION p204 VALUES LESS THAN (1561910400) ENGINE = InnoDB,
 PARTITION p205 VALUES LESS THAN (1562515200) ENGINE = InnoDB,
 PARTITION p206 VALUES LESS THAN (1563120000) ENGINE = InnoDB,
 PARTITION p207 VALUES LESS THAN (1563724800) ENGINE = InnoDB,
 PARTITION p208 VALUES LESS THAN (1564329600) ENGINE = InnoDB,
 PARTITION p209 VALUES LESS THAN (1564934400) ENGINE = InnoDB,
 PARTITION p210 VALUES LESS THAN (1565539200) ENGINE = InnoDB,
 PARTITION p211 VALUES LESS THAN (1566144000) ENGINE = InnoDB,
 PARTITION p212 VALUES LESS THAN (1566748800) ENGINE = InnoDB,
 PARTITION p213 VALUES LESS THAN (1567353600) ENGINE = InnoDB,
 PARTITION p214 VALUES LESS THAN (1567958400) ENGINE = InnoDB,
 PARTITION p215 VALUES LESS THAN (1568563200) ENGINE = InnoDB,
 PARTITION p216 VALUES LESS THAN (1569168000) ENGINE = InnoDB,
 PARTITION p217 VALUES LESS THAN (1569772800) ENGINE = InnoDB,
 PARTITION p218 VALUES LESS THAN (1570377600) ENGINE = InnoDB,
 PARTITION p219 VALUES LESS THAN (1570982400) ENGINE = InnoDB,
 PARTITION p220 VALUES LESS THAN (1571587200) ENGINE = InnoDB,
 PARTITION p221 VALUES LESS THAN (1572192000) ENGINE = InnoDB,
 PARTITION p222 VALUES LESS THAN (1572796800) ENGINE = InnoDB,
 PARTITION p223 VALUES LESS THAN (1573401600) ENGINE = InnoDB,
 PARTITION p224 VALUES LESS THAN (1574006400) ENGINE = InnoDB,
 PARTITION p225 VALUES LESS THAN (1574611200) ENGINE = InnoDB,
 PARTITION p226 VALUES LESS THAN (1575216000) ENGINE = InnoDB,
 PARTITION p227 VALUES LESS THAN (1575820800) ENGINE = InnoDB,
 PARTITION p228 VALUES LESS THAN (1576425600) ENGINE = InnoDB,
 PARTITION p229 VALUES LESS THAN (1577030400) ENGINE = InnoDB,
 PARTITION p230 VALUES LESS THAN (1577635200) ENGINE = InnoDB,
 PARTITION p231 VALUES LESS THAN (1578240000) ENGINE = InnoDB,
 PARTITION p232 VALUES LESS THAN (1578844800) ENGINE = InnoDB,
 PARTITION p233 VALUES LESS THAN (1579449600) ENGINE = InnoDB,
 PARTITION p234 VALUES LESS THAN (1580054400) ENGINE = InnoDB,
 PARTITION p235 VALUES LESS THAN (1580659200) ENGINE = InnoDB,
 PARTITION p236 VALUES LESS THAN (1581264000) ENGINE = InnoDB,
 PARTITION p237 VALUES LESS THAN (1581868800) ENGINE = InnoDB,
 PARTITION p238 VALUES LESS THAN (1582473600) ENGINE = InnoDB,
 PARTITION p239 VALUES LESS THAN (1583078400) ENGINE = InnoDB,
 PARTITION p240 VALUES LESS THAN (1583683200) ENGINE = InnoDB,
 PARTITION p241 VALUES LESS THAN (1584288000) ENGINE = InnoDB,
 PARTITION p242 VALUES LESS THAN (1584892800) ENGINE = InnoDB,
 PARTITION p243 VALUES LESS THAN (1585497600) ENGINE = InnoDB,
 PARTITION p244 VALUES LESS THAN (1586102400) ENGINE = InnoDB,
 PARTITION p245 VALUES LESS THAN (1586707200) ENGINE = InnoDB,
 PARTITION p246 VALUES LESS THAN (1587312000) ENGINE = InnoDB,
 PARTITION p247 VALUES LESS THAN (1587916800) ENGINE = InnoDB,
 PARTITION p248 VALUES LESS THAN (1588521600) ENGINE = InnoDB,
 PARTITION p249 VALUES LESS THAN (1589126400) ENGINE = InnoDB,
 PARTITION p250 VALUES LESS THAN (1589731200) ENGINE = InnoDB,
 PARTITION p251 VALUES LESS THAN (1590336000) ENGINE = InnoDB,
 PARTITION p252 VALUES LESS THAN (1590940800) ENGINE = InnoDB,
 PARTITION p253 VALUES LESS THAN (1591545600) ENGINE = InnoDB,
 PARTITION p254 VALUES LESS THAN (1592150400) ENGINE = InnoDB,
 PARTITION p255 VALUES LESS THAN (1592755200) ENGINE = InnoDB,
 PARTITION p256 VALUES LESS THAN (1593360000) ENGINE = InnoDB,
 PARTITION p257 VALUES LESS THAN (1593964800) ENGINE = InnoDB,
 PARTITION p258 VALUES LESS THAN (1594569600) ENGINE = InnoDB,
 PARTITION p259 VALUES LESS THAN (1595174400) ENGINE = InnoDB,
 PARTITION pmore VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_log`
--

DROP TABLE IF EXISTS `history_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_log` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `timestamp` int(11) NOT NULL DEFAULT '0',
  `source` varchar(64) NOT NULL DEFAULT '',
  `severity` int(11) NOT NULL DEFAULT '0',
  `value` text NOT NULL,
  `logeventid` int(11) NOT NULL DEFAULT '0',
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `history_log_2` (`itemid`,`id`),
  KEY `history_log_1` (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_log`
--

LOCK TABLES `history_log` WRITE;
/*!40000 ALTER TABLE `history_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_str`
--

DROP TABLE IF EXISTS `history_str`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_str` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  `ns` int(11) NOT NULL DEFAULT '0',
  KEY `history_str_1` (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_str`
--

LOCK TABLES `history_str` WRITE;
/*!40000 ALTER TABLE `history_str` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_str` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_str_sync`
--

DROP TABLE IF EXISTS `history_str_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_str_sync` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nodeid` int(11) NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `history_str_sync_1` (`nodeid`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_str_sync`
--

LOCK TABLES `history_str_sync` WRITE;
/*!40000 ALTER TABLE `history_str_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_str_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_sync`
--

DROP TABLE IF EXISTS `history_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_sync` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nodeid` int(11) NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` double(16,4) NOT NULL DEFAULT '0.0000',
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `history_sync_1` (`nodeid`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_sync`
--

LOCK TABLES `history_sync` WRITE;
/*!40000 ALTER TABLE `history_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_text`
--

DROP TABLE IF EXISTS `history_text`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_text` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` text NOT NULL,
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `history_text_2` (`itemid`,`id`),
  KEY `history_text_1` (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_text`
--

LOCK TABLES `history_text` WRITE;
/*!40000 ALTER TABLE `history_text` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_text` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_uint`
--

DROP TABLE IF EXISTS `history_uint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_uint` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` bigint(20) unsigned NOT NULL DEFAULT '0',
  `ns` int(11) NOT NULL DEFAULT '0',
  KEY `history_uint_1` (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (clock)
(PARTITION p0 VALUES LESS THAN (1438531200) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (1439136000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (1439740800) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (1440345600) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (1440950400) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (1441555200) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (1442160000) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (1442764800) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (1443369600) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (1443974400) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (1444579200) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (1445184000) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (1445788800) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (1446393600) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (1446998400) ENGINE = InnoDB,
 PARTITION p15 VALUES LESS THAN (1447603200) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (1448208000) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (1448812800) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (1449417600) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (1450022400) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (1450627200) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (1451232000) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (1451836800) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (1452441600) ENGINE = InnoDB,
 PARTITION p24 VALUES LESS THAN (1453046400) ENGINE = InnoDB,
 PARTITION p25 VALUES LESS THAN (1453651200) ENGINE = InnoDB,
 PARTITION p26 VALUES LESS THAN (1454256000) ENGINE = InnoDB,
 PARTITION p27 VALUES LESS THAN (1454860800) ENGINE = InnoDB,
 PARTITION p28 VALUES LESS THAN (1455465600) ENGINE = InnoDB,
 PARTITION p29 VALUES LESS THAN (1456070400) ENGINE = InnoDB,
 PARTITION p30 VALUES LESS THAN (1456675200) ENGINE = InnoDB,
 PARTITION p31 VALUES LESS THAN (1457280000) ENGINE = InnoDB,
 PARTITION p32 VALUES LESS THAN (1457884800) ENGINE = InnoDB,
 PARTITION p33 VALUES LESS THAN (1458489600) ENGINE = InnoDB,
 PARTITION p34 VALUES LESS THAN (1459094400) ENGINE = InnoDB,
 PARTITION p35 VALUES LESS THAN (1459699200) ENGINE = InnoDB,
 PARTITION p36 VALUES LESS THAN (1460304000) ENGINE = InnoDB,
 PARTITION p37 VALUES LESS THAN (1460908800) ENGINE = InnoDB,
 PARTITION p38 VALUES LESS THAN (1461513600) ENGINE = InnoDB,
 PARTITION p39 VALUES LESS THAN (1462118400) ENGINE = InnoDB,
 PARTITION p40 VALUES LESS THAN (1462723200) ENGINE = InnoDB,
 PARTITION p41 VALUES LESS THAN (1463328000) ENGINE = InnoDB,
 PARTITION p42 VALUES LESS THAN (1463932800) ENGINE = InnoDB,
 PARTITION p43 VALUES LESS THAN (1464537600) ENGINE = InnoDB,
 PARTITION p44 VALUES LESS THAN (1465142400) ENGINE = InnoDB,
 PARTITION p45 VALUES LESS THAN (1465747200) ENGINE = InnoDB,
 PARTITION p46 VALUES LESS THAN (1466352000) ENGINE = InnoDB,
 PARTITION p47 VALUES LESS THAN (1466956800) ENGINE = InnoDB,
 PARTITION p48 VALUES LESS THAN (1467561600) ENGINE = InnoDB,
 PARTITION p49 VALUES LESS THAN (1468166400) ENGINE = InnoDB,
 PARTITION p50 VALUES LESS THAN (1468771200) ENGINE = InnoDB,
 PARTITION p51 VALUES LESS THAN (1469376000) ENGINE = InnoDB,
 PARTITION p52 VALUES LESS THAN (1469980800) ENGINE = InnoDB,
 PARTITION p53 VALUES LESS THAN (1470585600) ENGINE = InnoDB,
 PARTITION p54 VALUES LESS THAN (1471190400) ENGINE = InnoDB,
 PARTITION p55 VALUES LESS THAN (1471795200) ENGINE = InnoDB,
 PARTITION p56 VALUES LESS THAN (1472400000) ENGINE = InnoDB,
 PARTITION p57 VALUES LESS THAN (1473004800) ENGINE = InnoDB,
 PARTITION p58 VALUES LESS THAN (1473609600) ENGINE = InnoDB,
 PARTITION p59 VALUES LESS THAN (1474214400) ENGINE = InnoDB,
 PARTITION p60 VALUES LESS THAN (1474819200) ENGINE = InnoDB,
 PARTITION p61 VALUES LESS THAN (1475424000) ENGINE = InnoDB,
 PARTITION p62 VALUES LESS THAN (1476028800) ENGINE = InnoDB,
 PARTITION p63 VALUES LESS THAN (1476633600) ENGINE = InnoDB,
 PARTITION p64 VALUES LESS THAN (1477238400) ENGINE = InnoDB,
 PARTITION p65 VALUES LESS THAN (1477843200) ENGINE = InnoDB,
 PARTITION p66 VALUES LESS THAN (1478448000) ENGINE = InnoDB,
 PARTITION p67 VALUES LESS THAN (1479052800) ENGINE = InnoDB,
 PARTITION p68 VALUES LESS THAN (1479657600) ENGINE = InnoDB,
 PARTITION p69 VALUES LESS THAN (1480262400) ENGINE = InnoDB,
 PARTITION p70 VALUES LESS THAN (1480867200) ENGINE = InnoDB,
 PARTITION p71 VALUES LESS THAN (1481472000) ENGINE = InnoDB,
 PARTITION p72 VALUES LESS THAN (1482076800) ENGINE = InnoDB,
 PARTITION p73 VALUES LESS THAN (1482681600) ENGINE = InnoDB,
 PARTITION p74 VALUES LESS THAN (1483286400) ENGINE = InnoDB,
 PARTITION p75 VALUES LESS THAN (1483891200) ENGINE = InnoDB,
 PARTITION p76 VALUES LESS THAN (1484496000) ENGINE = InnoDB,
 PARTITION p77 VALUES LESS THAN (1485100800) ENGINE = InnoDB,
 PARTITION p78 VALUES LESS THAN (1485705600) ENGINE = InnoDB,
 PARTITION p79 VALUES LESS THAN (1486310400) ENGINE = InnoDB,
 PARTITION p80 VALUES LESS THAN (1486915200) ENGINE = InnoDB,
 PARTITION p81 VALUES LESS THAN (1487520000) ENGINE = InnoDB,
 PARTITION p82 VALUES LESS THAN (1488124800) ENGINE = InnoDB,
 PARTITION p83 VALUES LESS THAN (1488729600) ENGINE = InnoDB,
 PARTITION p84 VALUES LESS THAN (1489334400) ENGINE = InnoDB,
 PARTITION p85 VALUES LESS THAN (1489939200) ENGINE = InnoDB,
 PARTITION p86 VALUES LESS THAN (1490544000) ENGINE = InnoDB,
 PARTITION p87 VALUES LESS THAN (1491148800) ENGINE = InnoDB,
 PARTITION p88 VALUES LESS THAN (1491753600) ENGINE = InnoDB,
 PARTITION p89 VALUES LESS THAN (1492358400) ENGINE = InnoDB,
 PARTITION p90 VALUES LESS THAN (1492963200) ENGINE = InnoDB,
 PARTITION p91 VALUES LESS THAN (1493568000) ENGINE = InnoDB,
 PARTITION p92 VALUES LESS THAN (1494172800) ENGINE = InnoDB,
 PARTITION p93 VALUES LESS THAN (1494777600) ENGINE = InnoDB,
 PARTITION p94 VALUES LESS THAN (1495382400) ENGINE = InnoDB,
 PARTITION p95 VALUES LESS THAN (1495987200) ENGINE = InnoDB,
 PARTITION p96 VALUES LESS THAN (1496592000) ENGINE = InnoDB,
 PARTITION p97 VALUES LESS THAN (1497196800) ENGINE = InnoDB,
 PARTITION p98 VALUES LESS THAN (1497801600) ENGINE = InnoDB,
 PARTITION p99 VALUES LESS THAN (1498406400) ENGINE = InnoDB,
 PARTITION p100 VALUES LESS THAN (1499011200) ENGINE = InnoDB,
 PARTITION p101 VALUES LESS THAN (1499616000) ENGINE = InnoDB,
 PARTITION p102 VALUES LESS THAN (1500220800) ENGINE = InnoDB,
 PARTITION p103 VALUES LESS THAN (1500825600) ENGINE = InnoDB,
 PARTITION p104 VALUES LESS THAN (1501430400) ENGINE = InnoDB,
 PARTITION p105 VALUES LESS THAN (1502035200) ENGINE = InnoDB,
 PARTITION p106 VALUES LESS THAN (1502640000) ENGINE = InnoDB,
 PARTITION p107 VALUES LESS THAN (1503244800) ENGINE = InnoDB,
 PARTITION p108 VALUES LESS THAN (1503849600) ENGINE = InnoDB,
 PARTITION p109 VALUES LESS THAN (1504454400) ENGINE = InnoDB,
 PARTITION p110 VALUES LESS THAN (1505059200) ENGINE = InnoDB,
 PARTITION p111 VALUES LESS THAN (1505664000) ENGINE = InnoDB,
 PARTITION p112 VALUES LESS THAN (1506268800) ENGINE = InnoDB,
 PARTITION p113 VALUES LESS THAN (1506873600) ENGINE = InnoDB,
 PARTITION p114 VALUES LESS THAN (1507478400) ENGINE = InnoDB,
 PARTITION p115 VALUES LESS THAN (1508083200) ENGINE = InnoDB,
 PARTITION p116 VALUES LESS THAN (1508688000) ENGINE = InnoDB,
 PARTITION p117 VALUES LESS THAN (1509292800) ENGINE = InnoDB,
 PARTITION p118 VALUES LESS THAN (1509897600) ENGINE = InnoDB,
 PARTITION p119 VALUES LESS THAN (1510502400) ENGINE = InnoDB,
 PARTITION p120 VALUES LESS THAN (1511107200) ENGINE = InnoDB,
 PARTITION p121 VALUES LESS THAN (1511712000) ENGINE = InnoDB,
 PARTITION p122 VALUES LESS THAN (1512316800) ENGINE = InnoDB,
 PARTITION p123 VALUES LESS THAN (1512921600) ENGINE = InnoDB,
 PARTITION p124 VALUES LESS THAN (1513526400) ENGINE = InnoDB,
 PARTITION p125 VALUES LESS THAN (1514131200) ENGINE = InnoDB,
 PARTITION p126 VALUES LESS THAN (1514736000) ENGINE = InnoDB,
 PARTITION p127 VALUES LESS THAN (1515340800) ENGINE = InnoDB,
 PARTITION p128 VALUES LESS THAN (1515945600) ENGINE = InnoDB,
 PARTITION p129 VALUES LESS THAN (1516550400) ENGINE = InnoDB,
 PARTITION p130 VALUES LESS THAN (1517155200) ENGINE = InnoDB,
 PARTITION p131 VALUES LESS THAN (1517760000) ENGINE = InnoDB,
 PARTITION p132 VALUES LESS THAN (1518364800) ENGINE = InnoDB,
 PARTITION p133 VALUES LESS THAN (1518969600) ENGINE = InnoDB,
 PARTITION p134 VALUES LESS THAN (1519574400) ENGINE = InnoDB,
 PARTITION p135 VALUES LESS THAN (1520179200) ENGINE = InnoDB,
 PARTITION p136 VALUES LESS THAN (1520784000) ENGINE = InnoDB,
 PARTITION p137 VALUES LESS THAN (1521388800) ENGINE = InnoDB,
 PARTITION p138 VALUES LESS THAN (1521993600) ENGINE = InnoDB,
 PARTITION p139 VALUES LESS THAN (1522598400) ENGINE = InnoDB,
 PARTITION p140 VALUES LESS THAN (1523203200) ENGINE = InnoDB,
 PARTITION p141 VALUES LESS THAN (1523808000) ENGINE = InnoDB,
 PARTITION p142 VALUES LESS THAN (1524412800) ENGINE = InnoDB,
 PARTITION p143 VALUES LESS THAN (1525017600) ENGINE = InnoDB,
 PARTITION p144 VALUES LESS THAN (1525622400) ENGINE = InnoDB,
 PARTITION p145 VALUES LESS THAN (1526227200) ENGINE = InnoDB,
 PARTITION p146 VALUES LESS THAN (1526832000) ENGINE = InnoDB,
 PARTITION p147 VALUES LESS THAN (1527436800) ENGINE = InnoDB,
 PARTITION p148 VALUES LESS THAN (1528041600) ENGINE = InnoDB,
 PARTITION p149 VALUES LESS THAN (1528646400) ENGINE = InnoDB,
 PARTITION p150 VALUES LESS THAN (1529251200) ENGINE = InnoDB,
 PARTITION p151 VALUES LESS THAN (1529856000) ENGINE = InnoDB,
 PARTITION p152 VALUES LESS THAN (1530460800) ENGINE = InnoDB,
 PARTITION p153 VALUES LESS THAN (1531065600) ENGINE = InnoDB,
 PARTITION p154 VALUES LESS THAN (1531670400) ENGINE = InnoDB,
 PARTITION p155 VALUES LESS THAN (1532275200) ENGINE = InnoDB,
 PARTITION p156 VALUES LESS THAN (1532880000) ENGINE = InnoDB,
 PARTITION p157 VALUES LESS THAN (1533484800) ENGINE = InnoDB,
 PARTITION p158 VALUES LESS THAN (1534089600) ENGINE = InnoDB,
 PARTITION p159 VALUES LESS THAN (1534694400) ENGINE = InnoDB,
 PARTITION p160 VALUES LESS THAN (1535299200) ENGINE = InnoDB,
 PARTITION p161 VALUES LESS THAN (1535904000) ENGINE = InnoDB,
 PARTITION p162 VALUES LESS THAN (1536508800) ENGINE = InnoDB,
 PARTITION p163 VALUES LESS THAN (1537113600) ENGINE = InnoDB,
 PARTITION p164 VALUES LESS THAN (1537718400) ENGINE = InnoDB,
 PARTITION p165 VALUES LESS THAN (1538323200) ENGINE = InnoDB,
 PARTITION p166 VALUES LESS THAN (1538928000) ENGINE = InnoDB,
 PARTITION p167 VALUES LESS THAN (1539532800) ENGINE = InnoDB,
 PARTITION p168 VALUES LESS THAN (1540137600) ENGINE = InnoDB,
 PARTITION p169 VALUES LESS THAN (1540742400) ENGINE = InnoDB,
 PARTITION p170 VALUES LESS THAN (1541347200) ENGINE = InnoDB,
 PARTITION p171 VALUES LESS THAN (1541952000) ENGINE = InnoDB,
 PARTITION p172 VALUES LESS THAN (1542556800) ENGINE = InnoDB,
 PARTITION p173 VALUES LESS THAN (1543161600) ENGINE = InnoDB,
 PARTITION p174 VALUES LESS THAN (1543766400) ENGINE = InnoDB,
 PARTITION p175 VALUES LESS THAN (1544371200) ENGINE = InnoDB,
 PARTITION p176 VALUES LESS THAN (1544976000) ENGINE = InnoDB,
 PARTITION p177 VALUES LESS THAN (1545580800) ENGINE = InnoDB,
 PARTITION p178 VALUES LESS THAN (1546185600) ENGINE = InnoDB,
 PARTITION p179 VALUES LESS THAN (1546790400) ENGINE = InnoDB,
 PARTITION p180 VALUES LESS THAN (1547395200) ENGINE = InnoDB,
 PARTITION p181 VALUES LESS THAN (1548000000) ENGINE = InnoDB,
 PARTITION p182 VALUES LESS THAN (1548604800) ENGINE = InnoDB,
 PARTITION p183 VALUES LESS THAN (1549209600) ENGINE = InnoDB,
 PARTITION p184 VALUES LESS THAN (1549814400) ENGINE = InnoDB,
 PARTITION p185 VALUES LESS THAN (1550419200) ENGINE = InnoDB,
 PARTITION p186 VALUES LESS THAN (1551024000) ENGINE = InnoDB,
 PARTITION p187 VALUES LESS THAN (1551628800) ENGINE = InnoDB,
 PARTITION p188 VALUES LESS THAN (1552233600) ENGINE = InnoDB,
 PARTITION p189 VALUES LESS THAN (1552838400) ENGINE = InnoDB,
 PARTITION p190 VALUES LESS THAN (1553443200) ENGINE = InnoDB,
 PARTITION p191 VALUES LESS THAN (1554048000) ENGINE = InnoDB,
 PARTITION p192 VALUES LESS THAN (1554652800) ENGINE = InnoDB,
 PARTITION p193 VALUES LESS THAN (1555257600) ENGINE = InnoDB,
 PARTITION p194 VALUES LESS THAN (1555862400) ENGINE = InnoDB,
 PARTITION p195 VALUES LESS THAN (1556467200) ENGINE = InnoDB,
 PARTITION p196 VALUES LESS THAN (1557072000) ENGINE = InnoDB,
 PARTITION p197 VALUES LESS THAN (1557676800) ENGINE = InnoDB,
 PARTITION p198 VALUES LESS THAN (1558281600) ENGINE = InnoDB,
 PARTITION p199 VALUES LESS THAN (1558886400) ENGINE = InnoDB,
 PARTITION p200 VALUES LESS THAN (1559491200) ENGINE = InnoDB,
 PARTITION p201 VALUES LESS THAN (1560096000) ENGINE = InnoDB,
 PARTITION p202 VALUES LESS THAN (1560700800) ENGINE = InnoDB,
 PARTITION p203 VALUES LESS THAN (1561305600) ENGINE = InnoDB,
 PARTITION p204 VALUES LESS THAN (1561910400) ENGINE = InnoDB,
 PARTITION p205 VALUES LESS THAN (1562515200) ENGINE = InnoDB,
 PARTITION p206 VALUES LESS THAN (1563120000) ENGINE = InnoDB,
 PARTITION p207 VALUES LESS THAN (1563724800) ENGINE = InnoDB,
 PARTITION p208 VALUES LESS THAN (1564329600) ENGINE = InnoDB,
 PARTITION p209 VALUES LESS THAN (1564934400) ENGINE = InnoDB,
 PARTITION p210 VALUES LESS THAN (1565539200) ENGINE = InnoDB,
 PARTITION p211 VALUES LESS THAN (1566144000) ENGINE = InnoDB,
 PARTITION p212 VALUES LESS THAN (1566748800) ENGINE = InnoDB,
 PARTITION p213 VALUES LESS THAN (1567353600) ENGINE = InnoDB,
 PARTITION p214 VALUES LESS THAN (1567958400) ENGINE = InnoDB,
 PARTITION p215 VALUES LESS THAN (1568563200) ENGINE = InnoDB,
 PARTITION p216 VALUES LESS THAN (1569168000) ENGINE = InnoDB,
 PARTITION p217 VALUES LESS THAN (1569772800) ENGINE = InnoDB,
 PARTITION p218 VALUES LESS THAN (1570377600) ENGINE = InnoDB,
 PARTITION p219 VALUES LESS THAN (1570982400) ENGINE = InnoDB,
 PARTITION p220 VALUES LESS THAN (1571587200) ENGINE = InnoDB,
 PARTITION p221 VALUES LESS THAN (1572192000) ENGINE = InnoDB,
 PARTITION p222 VALUES LESS THAN (1572796800) ENGINE = InnoDB,
 PARTITION p223 VALUES LESS THAN (1573401600) ENGINE = InnoDB,
 PARTITION p224 VALUES LESS THAN (1574006400) ENGINE = InnoDB,
 PARTITION p225 VALUES LESS THAN (1574611200) ENGINE = InnoDB,
 PARTITION p226 VALUES LESS THAN (1575216000) ENGINE = InnoDB,
 PARTITION p227 VALUES LESS THAN (1575820800) ENGINE = InnoDB,
 PARTITION p228 VALUES LESS THAN (1576425600) ENGINE = InnoDB,
 PARTITION p229 VALUES LESS THAN (1577030400) ENGINE = InnoDB,
 PARTITION p230 VALUES LESS THAN (1577635200) ENGINE = InnoDB,
 PARTITION p231 VALUES LESS THAN (1578240000) ENGINE = InnoDB,
 PARTITION p232 VALUES LESS THAN (1578844800) ENGINE = InnoDB,
 PARTITION p233 VALUES LESS THAN (1579449600) ENGINE = InnoDB,
 PARTITION p234 VALUES LESS THAN (1580054400) ENGINE = InnoDB,
 PARTITION p235 VALUES LESS THAN (1580659200) ENGINE = InnoDB,
 PARTITION p236 VALUES LESS THAN (1581264000) ENGINE = InnoDB,
 PARTITION p237 VALUES LESS THAN (1581868800) ENGINE = InnoDB,
 PARTITION p238 VALUES LESS THAN (1582473600) ENGINE = InnoDB,
 PARTITION p239 VALUES LESS THAN (1583078400) ENGINE = InnoDB,
 PARTITION p240 VALUES LESS THAN (1583683200) ENGINE = InnoDB,
 PARTITION p241 VALUES LESS THAN (1584288000) ENGINE = InnoDB,
 PARTITION p242 VALUES LESS THAN (1584892800) ENGINE = InnoDB,
 PARTITION p243 VALUES LESS THAN (1585497600) ENGINE = InnoDB,
 PARTITION p244 VALUES LESS THAN (1586102400) ENGINE = InnoDB,
 PARTITION p245 VALUES LESS THAN (1586707200) ENGINE = InnoDB,
 PARTITION p246 VALUES LESS THAN (1587312000) ENGINE = InnoDB,
 PARTITION p247 VALUES LESS THAN (1587916800) ENGINE = InnoDB,
 PARTITION p248 VALUES LESS THAN (1588521600) ENGINE = InnoDB,
 PARTITION p249 VALUES LESS THAN (1589126400) ENGINE = InnoDB,
 PARTITION p250 VALUES LESS THAN (1589731200) ENGINE = InnoDB,
 PARTITION p251 VALUES LESS THAN (1590336000) ENGINE = InnoDB,
 PARTITION p252 VALUES LESS THAN (1590940800) ENGINE = InnoDB,
 PARTITION p253 VALUES LESS THAN (1591545600) ENGINE = InnoDB,
 PARTITION p254 VALUES LESS THAN (1592150400) ENGINE = InnoDB,
 PARTITION p255 VALUES LESS THAN (1592755200) ENGINE = InnoDB,
 PARTITION p256 VALUES LESS THAN (1593360000) ENGINE = InnoDB,
 PARTITION p257 VALUES LESS THAN (1593964800) ENGINE = InnoDB,
 PARTITION p258 VALUES LESS THAN (1594569600) ENGINE = InnoDB,
 PARTITION p259 VALUES LESS THAN (1595174400) ENGINE = InnoDB,
 PARTITION pmore VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_uint`
--

LOCK TABLES `history_uint` WRITE;
/*!40000 ALTER TABLE `history_uint` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_uint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history_uint_sync`
--

DROP TABLE IF EXISTS `history_uint_sync`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `history_uint_sync` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nodeid` int(11) NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` bigint(20) unsigned NOT NULL DEFAULT '0',
  `ns` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `history_uint_sync_1` (`nodeid`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history_uint_sync`
--

LOCK TABLES `history_uint_sync` WRITE;
/*!40000 ALTER TABLE `history_uint_sync` DISABLE KEYS */;
/*!40000 ALTER TABLE `history_uint_sync` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `host_discovery`
--

DROP TABLE IF EXISTS `host_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostid` bigint(20) unsigned NOT NULL,
  `parent_hostid` bigint(20) unsigned DEFAULT NULL,
  `parent_itemid` bigint(20) unsigned DEFAULT NULL,
  `host` varchar(64) NOT NULL DEFAULT '',
  `lastcheck` int(11) NOT NULL DEFAULT '0',
  `ts_delete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`hostid`),
  KEY `c_host_discovery_2` (`parent_hostid`),
  KEY `c_host_discovery_3` (`parent_itemid`),
  CONSTRAINT `c_host_discovery_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_host_discovery_2` FOREIGN KEY (`parent_hostid`) REFERENCES `hosts` (`hostid`),
  CONSTRAINT `c_host_discovery_3` FOREIGN KEY (`parent_itemid`) REFERENCES `items` (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `host_discovery`
--

LOCK TABLES `host_discovery` WRITE;
/*!40000 ALTER TABLE `host_discovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `host_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `host_inventory`
--

DROP TABLE IF EXISTS `host_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `host_inventory` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostid` bigint(20) unsigned NOT NULL,
  `inventory_mode` int(11) NOT NULL DEFAULT '0',
  `type` varchar(64) NOT NULL DEFAULT '',
  `type_full` varchar(64) NOT NULL DEFAULT '',
  `name` varchar(64) NOT NULL DEFAULT '',
  `alias` varchar(64) NOT NULL DEFAULT '',
  `os` varchar(64) NOT NULL DEFAULT '',
  `os_full` varchar(255) NOT NULL DEFAULT '',
  `os_short` varchar(64) NOT NULL DEFAULT '',
  `serialno_a` varchar(64) NOT NULL DEFAULT '',
  `serialno_b` varchar(64) NOT NULL DEFAULT '',
  `tag` varchar(64) NOT NULL DEFAULT '',
  `asset_tag` varchar(64) NOT NULL DEFAULT '',
  `macaddress_a` varchar(64) NOT NULL DEFAULT '',
  `macaddress_b` varchar(64) NOT NULL DEFAULT '',
  `hardware` varchar(255) NOT NULL DEFAULT '',
  `hardware_full` text NOT NULL,
  `software` varchar(255) NOT NULL DEFAULT '',
  `software_full` text NOT NULL,
  `software_app_a` varchar(64) NOT NULL DEFAULT '',
  `software_app_b` varchar(64) NOT NULL DEFAULT '',
  `software_app_c` varchar(64) NOT NULL DEFAULT '',
  `software_app_d` varchar(64) NOT NULL DEFAULT '',
  `software_app_e` varchar(64) NOT NULL DEFAULT '',
  `contact` text NOT NULL,
  `location` text NOT NULL,
  `location_lat` varchar(16) NOT NULL DEFAULT '',
  `location_lon` varchar(16) NOT NULL DEFAULT '',
  `notes` text NOT NULL,
  `chassis` varchar(64) NOT NULL DEFAULT '',
  `model` varchar(64) NOT NULL DEFAULT '',
  `hw_arch` varchar(32) NOT NULL DEFAULT '',
  `vendor` varchar(64) NOT NULL DEFAULT '',
  `contract_number` varchar(64) NOT NULL DEFAULT '',
  `installer_name` varchar(64) NOT NULL DEFAULT '',
  `deployment_status` varchar(64) NOT NULL DEFAULT '',
  `url_a` varchar(255) NOT NULL DEFAULT '',
  `url_b` varchar(255) NOT NULL DEFAULT '',
  `url_c` varchar(255) NOT NULL DEFAULT '',
  `host_networks` text NOT NULL,
  `host_netmask` varchar(39) NOT NULL DEFAULT '',
  `host_router` varchar(39) NOT NULL DEFAULT '',
  `oob_ip` varchar(39) NOT NULL DEFAULT '',
  `oob_netmask` varchar(39) NOT NULL DEFAULT '',
  `oob_router` varchar(39) NOT NULL DEFAULT '',
  `date_hw_purchase` varchar(64) NOT NULL DEFAULT '',
  `date_hw_install` varchar(64) NOT NULL DEFAULT '',
  `date_hw_expiry` varchar(64) NOT NULL DEFAULT '',
  `date_hw_decomm` varchar(64) NOT NULL DEFAULT '',
  `site_address_a` varchar(128) NOT NULL DEFAULT '',
  `site_address_b` varchar(128) NOT NULL DEFAULT '',
  `site_address_c` varchar(128) NOT NULL DEFAULT '',
  `site_city` varchar(128) NOT NULL DEFAULT '',
  `site_state` varchar(64) NOT NULL DEFAULT '',
  `site_country` varchar(64) NOT NULL DEFAULT '',
  `site_zip` varchar(64) NOT NULL DEFAULT '',
  `site_rack` varchar(128) NOT NULL DEFAULT '',
  `site_notes` text NOT NULL,
  `poc_1_name` varchar(128) NOT NULL DEFAULT '',
  `poc_1_email` varchar(128) NOT NULL DEFAULT '',
  `poc_1_phone_a` varchar(64) NOT NULL DEFAULT '',
  `poc_1_phone_b` varchar(64) NOT NULL DEFAULT '',
  `poc_1_cell` varchar(64) NOT NULL DEFAULT '',
  `poc_1_screen` varchar(64) NOT NULL DEFAULT '',
  `poc_1_notes` text NOT NULL,
  `poc_2_name` varchar(128) NOT NULL DEFAULT '',
  `poc_2_email` varchar(128) NOT NULL DEFAULT '',
  `poc_2_phone_a` varchar(64) NOT NULL DEFAULT '',
  `poc_2_phone_b` varchar(64) NOT NULL DEFAULT '',
  `poc_2_cell` varchar(64) NOT NULL DEFAULT '',
  `poc_2_screen` varchar(64) NOT NULL DEFAULT '',
  `poc_2_notes` text NOT NULL,
  PRIMARY KEY (`hostid`),
  CONSTRAINT `c_host_inventory_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `host_inventory`
--

LOCK TABLES `host_inventory` WRITE;
/*!40000 ALTER TABLE `host_inventory` DISABLE KEYS */;
/*!40000 ALTER TABLE `host_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hostmacro`
--

DROP TABLE IF EXISTS `hostmacro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hostmacro` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostmacroid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `macro` varchar(64) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`hostmacroid`),
  UNIQUE KEY `hostmacro_1` (`hostid`,`macro`),
  CONSTRAINT `c_hostmacro_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hostmacro`
--

LOCK TABLES `hostmacro` WRITE;
/*!40000 ALTER TABLE `hostmacro` DISABLE KEYS */;
INSERT INTO `hostmacro` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,10161,'{$SNMP_COMMUNITY}','public'),('5e4d0a6d39a44b9c906a3173b448aa4a',2,10157,'{$PORTAL_IP}','127.0.0.1'),('5e4d0a6d39a44b9c906a3173b448aa4a',3,10154,'{$JMX_PORT}','9001'),('5e4d0a6d39a44b9c906a3173b448aa4a',4,10154,'{$TOMCAT_PORT}','8080'),('5e4d0a6d39a44b9c906a3173b448aa4a',5,10144,'{$USER}','root'),('5e4d0a6d39a44b9c906a3173b448aa4a',6,10144,'{$PSWD}','password'),('5e4d0a6d39a44b9c906a3173b448aa4a',7,10144,'{$PORT}','3306'),('5e4d0a6d39a44b9c906a3173b448aa4a',8,10144,'{$DBIP}','127.0.0.1'),('5e4d0a6d39a44b9c906a3173b448aa4a',9,10187,'{$SNMP_COMMUNITY}','public'),('5e4d0a6d39a44b9c906a3173b448aa4a',10,10189,'{$WEBLOGIC_PORT}','7001'),('5e4d0a6d39a44b9c906a3173b448aa4a',11,10189,'{$JMX_PORT}','9001'),('5e4d0a6d39a44b9c906a3173b448aa4a',12,10189,'{$JAVA_HOME}','/java_bin_path'),('5e4d0a6d39a44b9c906a3173b448aa4a',13,10188,'{$WEBSPHERE_PORT}','9080'),('5e4d0a6d39a44b9c906a3173b448aa4a',14,10188,'{$JMX_PORT}','9001'),('5e4d0a6d39a44b9c906a3173b448aa4a',15,10188,'{$JAVA_HOME}','/java_bin_path'),('5e4d0a6d39a44b9c906a3173b448aa4a',16,10154,'{$JAVA_HOME}','/java_bin_path'),('5e4d0a6d39a44b9c906a3173b448aa4a',17,10190,'{$SNMP_COMMUNITY}','public');
/*!40000 ALTER TABLE `hostmacro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hosts`
--

DROP TABLE IF EXISTS `hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostid` bigint(20) unsigned NOT NULL,
  `hostid_os` varchar(64) DEFAULT NULL COMMENT 'iaas对应设备的id',
  `proxy_hostid` bigint(20) unsigned DEFAULT NULL,
  `host` varchar(64) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `disable_until` int(11) NOT NULL DEFAULT '0',
  `error` varchar(128) NOT NULL DEFAULT '',
  `available` int(11) NOT NULL DEFAULT '0',
  `errors_from` int(11) NOT NULL DEFAULT '0',
  `lastaccess` int(11) NOT NULL DEFAULT '0',
  `ipmi_authtype` int(11) NOT NULL DEFAULT '0',
  `ipmi_privilege` int(11) NOT NULL DEFAULT '2',
  `ipmi_username` varchar(16) NOT NULL DEFAULT '',
  `ipmi_password` varchar(20) NOT NULL DEFAULT '',
  `ipmi_disable_until` int(11) NOT NULL DEFAULT '0',
  `ipmi_available` int(11) NOT NULL DEFAULT '0',
  `snmp_disable_until` int(11) NOT NULL DEFAULT '0',
  `snmp_available` int(11) NOT NULL DEFAULT '0',
  `maintenanceid` bigint(20) unsigned DEFAULT NULL,
  `maintenance_status` int(11) NOT NULL DEFAULT '0',
  `maintenance_type` int(11) NOT NULL DEFAULT '0',
  `maintenance_from` int(11) NOT NULL DEFAULT '0',
  `ipmi_errors_from` int(11) NOT NULL DEFAULT '0',
  `snmp_errors_from` int(11) NOT NULL DEFAULT '0',
  `ipmi_error` varchar(128) NOT NULL DEFAULT '',
  `snmp_error` varchar(128) NOT NULL DEFAULT '',
  `jmx_disable_until` int(11) NOT NULL DEFAULT '0',
  `jmx_available` int(11) NOT NULL DEFAULT '0',
  `jmx_errors_from` int(11) NOT NULL DEFAULT '0',
  `jmx_error` varchar(128) NOT NULL DEFAULT '',
  `name` varchar(64) NOT NULL DEFAULT '',
  `flags` int(11) NOT NULL DEFAULT '0',
  `templateid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`hostid`),
  KEY `hosts_1` (`host`),
  KEY `hosts_2` (`status`),
  KEY `hosts_3` (`proxy_hostid`),
  KEY `hosts_4` (`name`),
  KEY `hosts_5` (`maintenanceid`),
  KEY `c_hosts_3` (`templateid`),
  CONSTRAINT `c_hosts_1` FOREIGN KEY (`proxy_hostid`) REFERENCES `hosts` (`hostid`),
  CONSTRAINT `c_hosts_2` FOREIGN KEY (`maintenanceid`) REFERENCES `maintenances` (`maintenanceid`),
  CONSTRAINT `c_hosts_3` FOREIGN KEY (`templateid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts`
--

LOCK TABLES `hosts` WRITE;
/*!40000 ALTER TABLE `hosts` DISABLE KEYS */;
INSERT INTO `hosts` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',10113,NULL,NULL,'IaaS_Ceph',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','平台存储节点',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10131,NULL,NULL,'DB_DB2',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','数据库DB2',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10132,NULL,NULL,'DB_Oracle',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','数据库Oracle',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10144,NULL,NULL,'DB_MySQL',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','数据库MySQL',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10154,NULL,NULL,'Middleware_Tomcat',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','中间件Tomcat',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10157,NULL,NULL,'IaaS_Controler',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','平台控制服务',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10161,NULL,NULL,'Net Cisco Catalyst Switch',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','网络交换机思科',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10163,NULL,NULL,'Server_Linux',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','服务器Linux',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10164,NULL,NULL,'Middleware_IIS',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','中间件IIS',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10172,NULL,NULL,'DB_MongoDB',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','数据库MongoDB',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10175,NULL,NULL,'IaaS_Computer',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','平台计算服务',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10176,NULL,NULL,'IaaS_Network',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','平台网络服务',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10178,NULL,NULL,'IaaS_Web',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','平台门户服务',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10179,NULL,NULL,'DB_MSSQL',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','数据库MSSQL',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10182,NULL,NULL,'Server_Windows',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','服务器Windows',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10183,NULL,NULL,'VirtualResource_VM',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','云主机',1,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10186,NULL,NULL,'Net Generic Device',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','网络设备',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10187,NULL,NULL,'Net HuaWei Switch',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','网络交换机华为',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10188,NULL,NULL,'Middleware_Websphere',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','中间件Websphere',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10189,NULL,NULL,'Middleware_Weblogic',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','中间件Weblogic',0,NULL),('5e4d0a6d39a44b9c906a3173b448aa4a',10190,NULL,NULL,'Net ZhongXing',3,0,'',0,0,0,0,2,'','',0,0,0,0,NULL,0,0,0,0,0,'','',0,0,0,'','网络设备中兴',0,NULL);
/*!40000 ALTER TABLE `hosts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hosts_groups`
--

DROP TABLE IF EXISTS `hosts_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts_groups` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostgroupid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `groupid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`hostgroupid`),
  UNIQUE KEY `hosts_groups_1` (`hostid`,`groupid`),
  KEY `hosts_groups_2` (`groupid`),
  CONSTRAINT `c_hosts_groups_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_hosts_groups_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts_groups`
--

LOCK TABLES `hosts_groups` WRITE;
/*!40000 ALTER TABLE `hosts_groups` DISABLE KEYS */;
INSERT INTO `hosts_groups` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',158,10113,19),('5e4d0a6d39a44b9c906a3173b448aa4a',176,10131,19),('5e4d0a6d39a44b9c906a3173b448aa4a',177,10132,19),('5e4d0a6d39a44b9c906a3173b448aa4a',189,10144,19),('5e4d0a6d39a44b9c906a3173b448aa4a',199,10154,36),('5e4d0a6d39a44b9c906a3173b448aa4a',207,10157,19),('5e4d0a6d39a44b9c906a3173b448aa4a',211,10161,19),('5e4d0a6d39a44b9c906a3173b448aa4a',213,10163,19),('5e4d0a6d39a44b9c906a3173b448aa4a',215,10154,19),('5e4d0a6d39a44b9c906a3173b448aa4a',216,10164,19),('5e4d0a6d39a44b9c906a3173b448aa4a',225,10172,19),('5e4d0a6d39a44b9c906a3173b448aa4a',228,10175,19),('5e4d0a6d39a44b9c906a3173b448aa4a',229,10176,19),('5e4d0a6d39a44b9c906a3173b448aa4a',231,10178,19),('5e4d0a6d39a44b9c906a3173b448aa4a',232,10179,19),('5e4d0a6d39a44b9c906a3173b448aa4a',235,10182,19),('5e4d0a6d39a44b9c906a3173b448aa4a',236,10183,19),('5e4d0a6d39a44b9c906a3173b448aa4a',239,10186,19),('5e4d0a6d39a44b9c906a3173b448aa4a',240,10187,19),('5e4d0a6d39a44b9c906a3173b448aa4a',241,10188,19),('5e4d0a6d39a44b9c906a3173b448aa4a',242,10189,19),('5e4d0a6d39a44b9c906a3173b448aa4a',243,10190,19);
/*!40000 ALTER TABLE `hosts_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hosts_templates`
--

DROP TABLE IF EXISTS `hosts_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts_templates` (
  `tenantid` varchar(64) DEFAULT '0',
  `hosttemplateid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`hosttemplateid`),
  UNIQUE KEY `hosts_templates_1` (`hostid`,`templateid`),
  KEY `hosts_templates_2` (`templateid`),
  CONSTRAINT `c_hosts_templates_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_hosts_templates_2` FOREIGN KEY (`templateid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts_templates`
--

LOCK TABLES `hosts_templates` WRITE;
/*!40000 ALTER TABLE `hosts_templates` DISABLE KEYS */;
INSERT INTO `hosts_templates` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,10187,10186);
/*!40000 ALTER TABLE `hosts_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `housekeeper`
--

DROP TABLE IF EXISTS `housekeeper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `housekeeper` (
  `tenantid` varchar(64) DEFAULT '0',
  `housekeeperid` bigint(20) unsigned NOT NULL,
  `tablename` varchar(64) NOT NULL DEFAULT '',
  `field` varchar(64) NOT NULL DEFAULT '',
  `value` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`housekeeperid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `housekeeper`
--

LOCK TABLES `housekeeper` WRITE;
/*!40000 ALTER TABLE `housekeeper` DISABLE KEYS */;
/*!40000 ALTER TABLE `housekeeper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `httpstep`
--

DROP TABLE IF EXISTS `httpstep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `httpstep` (
  `tenantid` varchar(64) DEFAULT '0',
  `httpstepid` bigint(20) unsigned NOT NULL,
  `httptestid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `no` int(11) NOT NULL DEFAULT '0',
  `url` varchar(255) NOT NULL DEFAULT '',
  `timeout` int(11) NOT NULL DEFAULT '30',
  `posts` text NOT NULL,
  `required` varchar(255) NOT NULL DEFAULT '',
  `status_codes` varchar(255) NOT NULL DEFAULT '',
  `variables` text NOT NULL,
  PRIMARY KEY (`httpstepid`),
  KEY `httpstep_1` (`httptestid`),
  CONSTRAINT `c_httpstep_1` FOREIGN KEY (`httptestid`) REFERENCES `httptest` (`httptestid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `httpstep`
--

LOCK TABLES `httpstep` WRITE;
/*!40000 ALTER TABLE `httpstep` DISABLE KEYS */;
/*!40000 ALTER TABLE `httpstep` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `httpstepitem`
--

DROP TABLE IF EXISTS `httpstepitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `httpstepitem` (
  `tenantid` varchar(64) DEFAULT '0',
  `httpstepitemid` bigint(20) unsigned NOT NULL,
  `httpstepid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`httpstepitemid`),
  UNIQUE KEY `httpstepitem_1` (`httpstepid`,`itemid`),
  KEY `httpstepitem_2` (`itemid`),
  CONSTRAINT `c_httpstepitem_1` FOREIGN KEY (`httpstepid`) REFERENCES `httpstep` (`httpstepid`) ON DELETE CASCADE,
  CONSTRAINT `c_httpstepitem_2` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `httpstepitem`
--

LOCK TABLES `httpstepitem` WRITE;
/*!40000 ALTER TABLE `httpstepitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `httpstepitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `httptest`
--

DROP TABLE IF EXISTS `httptest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `httptest` (
  `tenantid` varchar(64) DEFAULT '0',
  `httptestid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `applicationid` bigint(20) unsigned DEFAULT NULL,
  `nextcheck` int(11) NOT NULL DEFAULT '0',
  `delay` int(11) NOT NULL DEFAULT '60',
  `status` int(11) NOT NULL DEFAULT '0',
  `variables` text NOT NULL,
  `agent` varchar(255) NOT NULL DEFAULT '',
  `authentication` int(11) NOT NULL DEFAULT '0',
  `http_user` varchar(64) NOT NULL DEFAULT '',
  `http_password` varchar(64) NOT NULL DEFAULT '',
  `hostid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `http_proxy` varchar(255) NOT NULL DEFAULT '',
  `retries` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`httptestid`),
  UNIQUE KEY `httptest_2` (`hostid`,`name`),
  KEY `httptest_1` (`applicationid`),
  KEY `httptest_3` (`status`),
  KEY `httptest_4` (`templateid`),
  CONSTRAINT `c_httptest_1` FOREIGN KEY (`applicationid`) REFERENCES `applications` (`applicationid`),
  CONSTRAINT `c_httptest_2` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_httptest_3` FOREIGN KEY (`templateid`) REFERENCES `httptest` (`httptestid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `httptest`
--

LOCK TABLES `httptest` WRITE;
/*!40000 ALTER TABLE `httptest` DISABLE KEYS */;
/*!40000 ALTER TABLE `httptest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `httptestitem`
--

DROP TABLE IF EXISTS `httptestitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `httptestitem` (
  `tenantid` varchar(64) DEFAULT '0',
  `httptestitemid` bigint(20) unsigned NOT NULL,
  `httptestid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`httptestitemid`),
  UNIQUE KEY `httptestitem_1` (`httptestid`,`itemid`),
  KEY `httptestitem_2` (`itemid`),
  CONSTRAINT `c_httptestitem_1` FOREIGN KEY (`httptestid`) REFERENCES `httptest` (`httptestid`) ON DELETE CASCADE,
  CONSTRAINT `c_httptestitem_2` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `httptestitem`
--

LOCK TABLES `httptestitem` WRITE;
/*!40000 ALTER TABLE `httptestitem` DISABLE KEYS */;
/*!40000 ALTER TABLE `httptestitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_announcement`
--

DROP TABLE IF EXISTS `i_announcement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_announcement` (
  `tenantid` varchar(64) DEFAULT '0',
  `announcementid` int(11) NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0 为启动 1启动 （公告获取）',
  `title` varchar(255) DEFAULT NULL,
  `active_since` int(11) DEFAULT NULL,
  `active_till` int(11) DEFAULT NULL,
  `issuer` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`announcementid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_announcement`
--

LOCK TABLES `i_announcement` WRITE;
/*!40000 ALTER TABLE `i_announcement` DISABLE KEYS */;
/*!40000 ALTER TABLE `i_announcement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_group_templates`
--

DROP TABLE IF EXISTS `i_group_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_group_templates` (
  `tenantid` varchar(64) DEFAULT '0',
  `groupid` bigint(20) NOT NULL COMMENT '设备类型id',
  `templateid` bigint(20) NOT NULL COMMENT '监控模型id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_group_templates`
--

LOCK TABLES `i_group_templates` WRITE;
/*!40000 ALTER TABLE `i_group_templates` DISABLE KEYS */;
INSERT INTO `i_group_templates` VALUES ('0',101,10163),('0',102,10182),('0',201,10183),('0',301,10161),('0',501,10144),('0',502,10132),('0',503,10165),('0',504,10131),('0',505,10179),('0',601,10154),('0',602,10164),('0',801,10157),('0',802,10175),('0',803,10113),('0',804,10176),('0',805,10178),('0',901,10167),('0',1001,10171),('0',302,10186),('0',303,10187),('0',603,10189),('0',604,10188),('0',506,10172),('0',304,10190);
/*!40000 ALTER TABLE `i_group_templates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_inspection_report_batch`
--

DROP TABLE IF EXISTS `i_inspection_report_batch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_inspection_report_batch` (
  `tenantid` varchar(64) DEFAULT NULL COMMENT '租户id',
  `reportbatchid` bigint(20) unsigned NOT NULL COMMENT '批次id',
  `reportid` bigint(20) DEFAULT NULL COMMENT '巡检报告id',
  `batchnum` bigint(20) DEFAULT NULL COMMENT '巡检报告批次',
  `batch_time` int(11) NOT NULL DEFAULT '0' COMMENT '巡检执行时间',
  PRIMARY KEY (`reportbatchid`),
  UNIQUE KEY `hosts_templates_1` (`reportid`,`batchnum`),
  KEY `hosts_templates_2` (`reportbatchid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_inspection_report_batch`
--

LOCK TABLES `i_inspection_report_batch` WRITE;
/*!40000 ALTER TABLE `i_inspection_report_batch` DISABLE KEYS */;
/*!40000 ALTER TABLE `i_inspection_report_batch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_inspection_report_historys`
--

DROP TABLE IF EXISTS `i_inspection_report_historys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_inspection_report_historys` (
  `tenantid` varchar(64) DEFAULT NULL COMMENT '租户id',
  `report_historyid` bigint(20) NOT NULL COMMENT '巡检历史id',
  `reportid` bigint(20) DEFAULT NULL COMMENT '巡检报告id',
  `batchnum` bigint(20) NOT NULL DEFAULT '0' COMMENT '巡检批次',
  `hostid` bigint(20) DEFAULT NULL COMMENT '设备id',
  `hostname` varchar(64) NOT NULL DEFAULT '',
  `itemid` bigint(20) DEFAULT NULL COMMENT '设备监控项id',
  `itemname` varchar(200) NOT NULL COMMENT '监控项名称',
  `VALUE` varchar(255) NOT NULL DEFAULT '',
  `isproblem` int(1) DEFAULT NULL COMMENT '是否正常(0:正常 1：异常)',
  PRIMARY KEY (`report_historyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_inspection_report_historys`
--

LOCK TABLES `i_inspection_report_historys` WRITE;
/*!40000 ALTER TABLE `i_inspection_report_historys` DISABLE KEYS */;
/*!40000 ALTER TABLE `i_inspection_report_historys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_inspection_report_items`
--

DROP TABLE IF EXISTS `i_inspection_report_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_inspection_report_items` (
  `tenantid` varchar(64) DEFAULT '0' COMMENT '租户id',
  `reportitemid` bigint(20) NOT NULL,
  `reportid` bigint(20) DEFAULT NULL,
  `hostid` bigint(20) DEFAULT NULL COMMENT '设备id',
  `hostname` varchar(64) NOT NULL DEFAULT '',
  `itemid` bigint(20) DEFAULT NULL COMMENT '设备监控项id',
  `itemname` varchar(200) NOT NULL COMMENT '监控项名称',
  `delay` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`reportitemid`),
  KEY `c_report_1` (`reportid`),
  CONSTRAINT `c_report_1` FOREIGN KEY (`reportid`) REFERENCES `i_inspection_reports` (`reportid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_inspection_report_items`
--

LOCK TABLES `i_inspection_report_items` WRITE;
/*!40000 ALTER TABLE `i_inspection_report_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `i_inspection_report_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_inspection_reports`
--

DROP TABLE IF EXISTS `i_inspection_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_inspection_reports` (
  `tenantid` varchar(64) DEFAULT '0' COMMENT '租户id',
  `reportid` bigint(20) NOT NULL DEFAULT '0' COMMENT '巡检报告id',
  `batchnum` bigint(20) NOT NULL DEFAULT '0' COMMENT '巡检批次',
  `name` varchar(64) DEFAULT NULL COMMENT '巡检报告名称',
  `username` varchar(100) NOT NULL DEFAULT '',
  `time` varchar(64) DEFAULT NULL COMMENT '巡检时间',
  `active_till` varchar(64) DEFAULT NULL,
  `groupid` bigint(20) DEFAULT NULL COMMENT '设备类型id',
  `status` int(1) DEFAULT NULL COMMENT '状态(0:启用 1：停用)',
  `executed` int(1) DEFAULT NULL COMMENT '是否已执行(0:未执行  1：已执行)',
  `timeperiod_type` int(11) NOT NULL DEFAULT '0',
  `every` int(11) NOT NULL DEFAULT '0',
  `month` int(11) NOT NULL DEFAULT '0',
  `dayofweek` int(11) NOT NULL DEFAULT '0',
  `day` int(11) NOT NULL DEFAULT '0',
  `start_time` int(11) NOT NULL DEFAULT '0',
  `period` int(11) NOT NULL DEFAULT '0',
  `start_date` int(11) NOT NULL DEFAULT '0',
  `create_time` int(11) NOT NULL DEFAULT '0',
  `batch_time` int(11) NOT NULL DEFAULT '0' COMMENT '批次更新时间',
  PRIMARY KEY (`reportid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_inspection_reports`
--

LOCK TABLES `i_inspection_reports` WRITE;
/*!40000 ALTER TABLE `i_inspection_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `i_inspection_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_t_application`
--

DROP TABLE IF EXISTS `i_t_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_t_application` (
  `applicationid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `description` text,
  PRIMARY KEY (`applicationid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_t_application`
--

LOCK TABLES `i_t_application` WRITE;
/*!40000 ALTER TABLE `i_t_application` DISABLE KEYS */;
INSERT INTO `i_t_application` VALUES (1,1,1,'CPU使用率','监控CPU的使用率比例，包括用户态(User)、内核态(System)、I/O等待(IOWait)、空闲(Idle)等'),(2,1,1,'CPU负载','监控Linux服务器的平均负载(load average)，包括最近1分钟、5分钟、15分钟等'),(3,1,1,'内存使用率','监控内存使用率，对于Linux服务器，包括空闲内存、高速缓存、页面缓存、应用程序内存等'),(4,1,1,'磁盘使用率','监控所有磁盘分区的空间使用率，包括总空间和已用空间'),(5,1,1,'磁盘读写速率','监控所有磁盘分区的I/O流量，包括写入和读取'),(6,1,1,'云主机状态','云主机状态：ACTIVE:正常,ERROR:异常    注：云主机状态监控频率为60s,不可更改，请慎用！'),(7,2,1,'运行时间','运行时间'),(8,2,1,'查询缓存可用量','查询缓存可用量'),(9,2,1,'系统会话数','系统会话数'),(10,2,1,'查询缓存总量','查询缓存总量'),(11,2,1,'每秒查询量','每秒查询量'),(12,2,1,'并发线程数','并发线程数'),(13,3,1,'运行时间','运行时间'),(14,3,1,'最大线程数','最大线程数'),(15,3,1,'当前线程数','当前线程数'),(16,3,1,'每秒出错数','每秒出错数');
/*!40000 ALTER TABLE `i_t_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_t_item`
--

DROP TABLE IF EXISTS `i_t_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_t_item` (
  `itemid` bigint(20) unsigned NOT NULL,
  `applicationid` bigint(20) unsigned DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `units` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_t_item`
--

LOCK TABLES `i_t_item` WRITE;
/*!40000 ALTER TABLE `i_t_item` DISABLE KEYS */;
INSERT INTO `i_t_item` VALUES (1,1,1,'CPU使用率_Linux','cpuUtil','%'),(2,1,1,'CPU使用率_Windows','wmi.get[root\\cimv2,Select LoadPercentage from Win32_processor]','%'),(3,2,1,'CPU负载','system.cpu.load[percpu,avg1]',''),(4,3,1,'内存使用率','vm.memory.size[pused]','%'),(5,4,1,'磁盘使用率','vfs.fs.size[fs,<mode>]','%'),(6,5,1,'磁盘读速率_Linux','vfs.dev.read[,sectors]','MB/s'),(7,5,1,'磁盘读速率_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Read Bytes/sec\"]','MB/s'),(8,5,1,'磁盘写速率_Linux','vfs.dev.write[,sectors]','MB/s'),(9,5,1,'磁盘写速率_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Write Bytes/sec\"]','MB/s'),(10,6,1,'云主机状态','vm.status',''),(11,7,1,'运行时间','mysql.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]','uptime'),(12,8,1,'查询缓存可用量','mysql.qcache_free[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]','B'),(13,9,1,'系统会话数','mysql.threads_connected[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',''),(14,10,1,'查询缓存总量','mysql.qcache_total[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]','B'),(15,11,1,'每秒查询量','mysql.questions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]','qps'),(16,12,1,'并发线程数','mysql.threads_running[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',''),(17,13,1,'运行时间','tomcat.uptime[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',''),(18,14,1,'最大线程数','tomcat.maxThreads[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]','个'),(19,15,1,'当前线程数','tomcat.currentThreadCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]','个'),(20,16,1,'每秒出错数','tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]','个');
/*!40000 ALTER TABLE `i_t_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `i_t_template`
--

DROP TABLE IF EXISTS `i_t_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `i_t_template` (
  `templateid` bigint(20) unsigned NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`templateid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `i_t_template`
--

LOCK TABLES `i_t_template` WRITE;
/*!40000 ALTER TABLE `i_t_template` DISABLE KEYS */;
INSERT INTO `i_t_template` VALUES (1,1,'Cloudhost'),(2,1,'mysql'),(3,1,'tomcat');
/*!40000 ALTER TABLE `i_t_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `icon_map`
--

DROP TABLE IF EXISTS `icon_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `icon_map` (
  `tenantid` varchar(64) DEFAULT '0',
  `iconmapid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `default_iconid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`iconmapid`),
  KEY `icon_map_1` (`name`),
  KEY `icon_map_2` (`default_iconid`),
  CONSTRAINT `c_icon_map_1` FOREIGN KEY (`default_iconid`) REFERENCES `images` (`imageid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `icon_map`
--

LOCK TABLES `icon_map` WRITE;
/*!40000 ALTER TABLE `icon_map` DISABLE KEYS */;
/*!40000 ALTER TABLE `icon_map` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `icon_mapping`
--

DROP TABLE IF EXISTS `icon_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `icon_mapping` (
  `tenantid` varchar(64) DEFAULT '0',
  `iconmappingid` bigint(20) unsigned NOT NULL,
  `iconmapid` bigint(20) unsigned NOT NULL,
  `iconid` bigint(20) unsigned NOT NULL,
  `inventory_link` int(11) NOT NULL DEFAULT '0',
  `expression` varchar(64) NOT NULL DEFAULT '',
  `sortorder` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`iconmappingid`),
  KEY `icon_mapping_1` (`iconmapid`),
  KEY `icon_mapping_2` (`iconid`),
  CONSTRAINT `c_icon_mapping_1` FOREIGN KEY (`iconmapid`) REFERENCES `icon_map` (`iconmapid`) ON DELETE CASCADE,
  CONSTRAINT `c_icon_mapping_2` FOREIGN KEY (`iconid`) REFERENCES `images` (`imageid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `icon_mapping`
--

LOCK TABLES `icon_mapping` WRITE;
/*!40000 ALTER TABLE `icon_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `icon_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ids`
--

DROP TABLE IF EXISTS `ids`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ids` (
  `tenantid` varchar(64) DEFAULT '0',
  `nodeid` int(11) NOT NULL DEFAULT '0',
  `table_name` varchar(64) NOT NULL DEFAULT '',
  `field_name` varchar(64) NOT NULL DEFAULT '',
  `nextid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`nodeid`,`table_name`,`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ids`
--

LOCK TABLES `ids` WRITE;
/*!40000 ALTER TABLE `ids` DISABLE KEYS */;
/*!40000 ALTER TABLE `ids` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
  `tenantid` varchar(64) DEFAULT '0',
  `imageid` bigint(20) unsigned NOT NULL,
  `imagetype` int(11) NOT NULL DEFAULT '0',
  `name` varchar(64) NOT NULL DEFAULT '0',
  `image` longblob NOT NULL,
  PRIMARY KEY (`imageid`),
  KEY `images_1` (`imagetype`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interface`
--

DROP TABLE IF EXISTS `interface`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interface` (
  `tenantid` varchar(64) DEFAULT '0',
  `interfaceid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  `main` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL DEFAULT '0',
  `useip` int(11) NOT NULL DEFAULT '1',
  `ip` varchar(64) NOT NULL DEFAULT '127.0.0.1',
  `dns` varchar(64) NOT NULL DEFAULT '',
  `port` varchar(64) NOT NULL DEFAULT '10050',
  PRIMARY KEY (`interfaceid`),
  KEY `interface_1` (`hostid`,`type`),
  KEY `interface_2` (`ip`,`dns`),
  CONSTRAINT `c_interface_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interface`
--

LOCK TABLES `interface` WRITE;
/*!40000 ALTER TABLE `interface` DISABLE KEYS */;
/*!40000 ALTER TABLE `interface` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interface_discovery`
--

DROP TABLE IF EXISTS `interface_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interface_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `interfaceid` bigint(20) unsigned NOT NULL,
  `parent_interfaceid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`interfaceid`),
  KEY `c_interface_discovery_2` (`parent_interfaceid`),
  CONSTRAINT `c_interface_discovery_1` FOREIGN KEY (`interfaceid`) REFERENCES `interface` (`interfaceid`) ON DELETE CASCADE,
  CONSTRAINT `c_interface_discovery_2` FOREIGN KEY (`parent_interfaceid`) REFERENCES `interface` (`interfaceid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interface_discovery`
--

LOCK TABLES `interface_discovery` WRITE;
/*!40000 ALTER TABLE `interface_discovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `interface_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_discovery`
--

DROP TABLE IF EXISTS `item_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemdiscoveryid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  `parent_itemid` bigint(20) unsigned NOT NULL,
  `key_` varchar(255) NOT NULL DEFAULT '',
  `lastcheck` int(11) NOT NULL DEFAULT '0',
  `ts_delete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`itemdiscoveryid`),
  UNIQUE KEY `item_discovery_1` (`itemid`,`parent_itemid`),
  KEY `item_discovery_2` (`parent_itemid`),
  CONSTRAINT `c_item_discovery_1` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE,
  CONSTRAINT `c_item_discovery_2` FOREIGN KEY (`parent_itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_discovery`
--

LOCK TABLES `item_discovery` WRITE;
/*!40000 ALTER TABLE `item_discovery` DISABLE KEYS */;
INSERT INTO `item_discovery` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',515,25004,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',516,25005,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',517,25006,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',518,25007,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',519,25008,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',520,25009,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',521,25010,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',522,25011,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',523,25012,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',524,25013,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',525,25014,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',526,25015,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',527,25016,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',528,25017,25003,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',529,25037,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',530,25038,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',531,25039,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',532,25040,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',533,25041,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',534,25042,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',535,25043,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',536,25044,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',537,25045,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',538,25046,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',539,25047,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',540,25048,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',541,25049,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',542,25050,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',543,25051,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',544,25052,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',545,25053,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',546,25054,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',547,25055,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',548,25056,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',549,25057,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',550,25058,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',552,25060,25036,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',553,25061,25036,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',554,25062,25036,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',575,25109,25103,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',576,25110,25103,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',577,25111,25103,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',578,25112,25103,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',579,25113,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',580,25114,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',581,25115,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',582,25116,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',583,25117,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',584,25118,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',585,25119,25105,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',586,25120,25105,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',587,25121,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',588,25122,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',589,25123,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',590,25124,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',591,25125,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',592,25126,25106,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',593,25127,25107,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',594,25128,25107,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',595,25129,25107,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',596,25130,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',597,25131,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',598,25132,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',599,25133,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',600,25134,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',601,25135,25108,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',749,25534,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',750,25535,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',751,25536,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',752,25537,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',753,25539,25538,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',754,25540,25538,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',755,25541,25538,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',756,25542,25538,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',757,25587,25585,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',758,25588,25585,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',759,25589,25585,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',760,25590,25585,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',761,25591,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',762,25592,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',763,25593,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',764,25594,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',773,25617,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',774,25618,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',775,25619,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',776,25620,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',777,25621,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',778,25622,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',779,25623,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',780,25624,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',782,25759,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',783,25760,25586,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',784,25761,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',785,25762,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',786,25763,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',787,25764,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',788,25765,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',789,25766,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',790,25767,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',791,25768,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',792,25769,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',793,25770,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',794,25771,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',795,25772,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',796,25773,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',797,25784,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',798,25789,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',799,25790,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',800,25791,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',801,25792,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',802,25793,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',803,25794,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',804,25795,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',805,25796,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',806,25797,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',807,25840,25839,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',808,25845,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',809,25846,25533,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',810,25851,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',811,25852,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',812,25853,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',813,25854,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',814,25855,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',815,25856,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',818,25860,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',819,25861,25857,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',820,25862,25857,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',821,25863,25857,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',822,25865,25864,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',823,25866,25864,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',824,25867,25864,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',825,25868,25864,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',826,25873,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',827,25874,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',828,25875,25608,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',829,25876,25788,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',830,25877,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',831,25879,25036,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',832,25880,25036,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',833,25881,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',834,25882,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',835,25883,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',836,25884,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',837,25885,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',838,25886,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',839,25887,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',840,25888,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',841,25889,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',842,25890,25035,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',843,25893,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',844,25894,25104,'',0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',845,25895,25104,'',0,0);
/*!40000 ALTER TABLE `item_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `snmp_community` varchar(64) NOT NULL DEFAULT '',
  `snmp_oid` varchar(255) NOT NULL DEFAULT '',
  `hostid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `delay` int(11) NOT NULL DEFAULT '0',
  `history` int(11) NOT NULL DEFAULT '90',
  `trends` int(11) NOT NULL DEFAULT '365',
  `status` int(11) NOT NULL DEFAULT '0',
  `value_type` int(11) NOT NULL DEFAULT '0',
  `trapper_hosts` varchar(255) NOT NULL DEFAULT '',
  `units` varchar(255) NOT NULL DEFAULT '',
  `multiplier` int(11) NOT NULL DEFAULT '0',
  `delta` int(11) NOT NULL DEFAULT '0',
  `snmpv3_securityname` varchar(64) NOT NULL DEFAULT '',
  `snmpv3_securitylevel` int(11) NOT NULL DEFAULT '0',
  `snmpv3_authpassphrase` varchar(64) NOT NULL DEFAULT '',
  `snmpv3_privpassphrase` varchar(64) NOT NULL DEFAULT '',
  `formula` varchar(255) NOT NULL DEFAULT '1',
  `error` varchar(128) NOT NULL DEFAULT '',
  `lastlogsize` bigint(20) unsigned NOT NULL DEFAULT '0',
  `logtimefmt` varchar(64) NOT NULL DEFAULT '',
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `valuemapid` bigint(20) unsigned DEFAULT NULL,
  `delay_flex` varchar(255) NOT NULL DEFAULT '',
  `params` text NOT NULL,
  `ipmi_sensor` varchar(128) NOT NULL DEFAULT '',
  `data_type` int(11) NOT NULL DEFAULT '0',
  `authtype` int(11) NOT NULL DEFAULT '0',
  `username` varchar(64) NOT NULL DEFAULT '',
  `password` varchar(64) NOT NULL DEFAULT '',
  `publickey` varchar(64) NOT NULL DEFAULT '',
  `privatekey` varchar(64) NOT NULL DEFAULT '',
  `mtime` int(11) NOT NULL DEFAULT '0',
  `flags` int(11) NOT NULL DEFAULT '0',
  `filter` varchar(255) NOT NULL DEFAULT '',
  `interfaceid` bigint(20) unsigned DEFAULT NULL,
  `port` varchar(64) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `inventory_link` int(11) NOT NULL DEFAULT '0',
  `lifetime` varchar(64) NOT NULL DEFAULT '30',
  `snmpv3_authprotocol` int(11) NOT NULL DEFAULT '0',
  `snmpv3_privprotocol` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `snmpv3_contextname` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`itemid`),
  UNIQUE KEY `items_1` (`hostid`,`key_`),
  KEY `items_3` (`status`),
  KEY `items_4` (`templateid`),
  KEY `items_5` (`valuemapid`),
  KEY `items_6` (`interfaceid`),
  CONSTRAINT `c_items_1` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE,
  CONSTRAINT `c_items_2` FOREIGN KEY (`templateid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE,
  CONSTRAINT `c_items_3` FOREIGN KEY (`valuemapid`) REFERENCES `valuemaps` (`valuemapid`),
  CONSTRAINT `c_items_4` FOREIGN KEY (`interfaceid`) REFERENCES `interface` (`interfaceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',23949,0,'','',10113,'ceph-mon','ceph.mon',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23950,0,'','',10113,'Ceph 操作总数','ceph.ops',600,30,90,0,3,'','ops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23951,0,'','',10113,'Ceph IN 状态 OSD 百分比','ceph.osd_in',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23952,0,'','',10113,'Ceph UP 状态 OSD 百分比','ceph.osd_up',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23953,0,'','',10113,'Ceph PG active 状态总数目','ceph.active',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph will process requests to the placement group.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23954,0,'','',10113,'Ceph PG backfill 状态总数目','ceph.backfill',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is scanning and synchronizing the entire contents of a placement group instead of inferring what contents need to be synchronized from the logs of recent operations. Backfill is a special case of recovery.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23955,0,'','',10113,'Ceph PG clean 状态总数目','ceph.clean',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph replicated all objects in the placement group the correct number of times.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23956,0,'','',10113,'Ceph PG creating 状态总数目','ceph.creating',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is still creating the placement group.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23957,0,'','',10113,'Ceph PG degraded 状态总数目','ceph.degraded',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph has not replicated some objects in the placement group the correct number of times yet.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23958,0,'','',10113,'Ceph PG degraded 状态百分比','ceph.degraded_percent',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph has not replicated some objects in the placement group the correct number of times yet.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23959,0,'','',10113,'Ceph PG down 状态总数目','ceph.down',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','A replica with necessary data is down, so the placement group is offline.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23960,0,'','',10113,'Ceph PG incomplete 状态总数目','ceph.incomplete',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph detects that a placement group is missing a necessary period of history from its log. If you see this state, report a bug, and try to start any failed OSDs that may contain the needed information.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23961,0,'','',10113,'Ceph PG inconsistent 状态总数目','ceph.inconsistent',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph detects inconsistencies in the one or more replicas of an object in the placement group (e.g. objects are the wrong size, objects are missing from one replica after recovery finished, etc.).',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23962,0,'','',10113,'Ceph PG peering 状态总数目','ceph.peering',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The placement group is undergoing the peering process',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23963,0,'','',10113,'Ceph PG recovering 状态总数目','ceph.recovering',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is migrating/synchronizing objects and their replicas.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23964,0,'','',10113,'Ceph PG remapped 状态总数目','ceph.remapped',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The placement group is temporarily mapped to a different set of OSDs from what CRUSH specified.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23965,0,'','',10113,'Ceph PG repair 状态总数目','ceph.repair',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is checking the placement group and repairing any inconsistencies it finds (if possible).',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23966,0,'','',10113,'Ceph PG replay 状态总数目','ceph.replay',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The placement group is waiting for clients to replay operations after an OSD crashed.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23967,0,'','',10113,'Ceph PG scrubbing 状态总数目','ceph.scrubbing',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is checking the placement group for inconsistencies.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23968,0,'','',10113,'Ceph PG 拆分','ceph.splitting',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph is splitting the placment group into multiple placement groups. (functional?)',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23969,0,'','',10113,'Ceph PG stale 状态总数目','ceph.stale',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The placement group is in an unknown state - the monitors have not received an update for it since the placement group mapping changed.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23970,0,'','',10113,'Ceph PG 总数','ceph.pgtotal',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','Ceph total placement group number.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23971,0,'','',10113,'Ceph PG wait-backfill 状态总数目','ceph.waitBackfill',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The placement group is waiting in line to start backfill.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23972,0,'','',10113,'Ceph rados 空闲空间大小','ceph.rados_free',600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23973,0,'','',10113,'Ceph rados 空间总大小','ceph.rados_total',600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23974,0,'','',10113,'Ceph rados 已使用空间大小','ceph.rados_used',600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23975,0,'','',10113,'Ceph 读速率','ceph.rdbps',600,30,90,0,3,'','B/s',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',23976,0,'','',10113,'Ceph 写速率','ceph.wrbps',600,30,90,0,3,'','B/s',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24094,0,'','',10131,'数据库运行状态','db2.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24095,0,'','',10131,'事务数','db2.ApplCommitsAttempted[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24096,0,'','',10131,'锁平均等待时间','db2.AveWaitForLock[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','ms',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24097,0,'','',10131,'数据库版本','db2.Version[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',86400,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24098,0,'','',10131,'数据库日志空间可用总大小','db2.TotalLogSpAvail[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24147,0,'','',10132,'运行状态','oracle.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',3,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24148,0,'','',10132,'归档日志空间可用率','oracle.ArchFreeSpace[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24149,0,'','',10132,'归档日志大小','oracle.archive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24150,0,'','',10132,'审计信息','oracle.audit[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24151,0,'','',10132,'缓冲区命中率','oracle.BuffHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24152,0,'','',10132,'非一致性读次数','oracle.dbblockgets[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24153,0,'','',10132,'一致性读次数','oracle.dbconsistentgets[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24156,0,'','',10132,'逻辑读命中率','oracle.dbhitratio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24157,0,'','',10132,'物理读次数','oracle.dbphysicalread[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24159,0,'','',10132,'数据库版本','oracle.dbversion[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24160,0,'','',10132,'字典命中率','oracle.DictHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24161,0,'','',10132,'功能体命中率','oracle.hitratio_body[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24162,0,'','',10132,'SQL 区域命中率','oracle.hitratio_sqlarea[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24163,0,'','',10132,'表/存储过程命中率','oracle.hitratio_table_proc[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24164,0,'','',10132,'触发器命中率','oracle.hitratio_trigger[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24165,0,'','',10132,'逻辑 I/O 块改变速率','oracle.lio_block_changes[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','Blocks/sec',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24166,0,'','',10132,'逻辑 I/O 一致性读速率','oracle.lio_consistent_read[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','Blocks/sec',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24167,0,'','',10132,'逻辑 I/O 当前读速率','oracle.lio_current_read[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','Blocks/sec',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24168,0,'','',10132,'库缓存GET请求命中率','oracle.LibGetHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24169,0,'','',10132,'数据库锁信息','oracle.locks[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24170,0,'','',10132,'最大进程数','oracle.maxprocs[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24171,0,'','',10132,'最大会话数','oracle.maxsession[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24172,0,'','',10132,'锁请求次数','oracle.miss_latch[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24173,0,'','',10132,'数据所占最低百分比','oracle.PctUsed[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24174,0,'','',10132,'PGA','oracle.pga[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24175,0,'','',10132,'session总计可用最大PGA内存','oracle.pga_aggregate_target[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24176,0,'','',10132,'物理 I/O 数据文件读速率','oracle.phio_datafile_reads[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24177,0,'','',10132,'物理 I/O 数据文件写速率','oracle.phio_datafile_writes[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24178,0,'','',10132,'物理 I/O Redo 写速率','oracle.phio_redo_writes[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24179,0,'','',10132,'库缓存中BODY的PIN请求命中率','oracle.pinhitratio_body[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24180,0,'','',10132,'库缓存中SQLAREA的PIN请求命中率','oracle.pinhitratio_sqlarea[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24181,0,'','',10132,'库缓存中TABLE-PROCEDURE的PIN请求命中率','oracle.pinhitratio_table-proc[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24182,0,'','',10132,'库缓存中TRIGGER的PIN请求命中率','oracle.pinhitratio_trigger[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24183,0,'','',10132,'字典高速缓冲区','oracle.pool_dict_cache[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24184,0,'','',10132,'缓冲池空闲内存大小','oracle.pool_free_mem[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24185,0,'','',10132,'库高速缓存大小','oracle.pool_lib_cache[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24186,0,'','',10132,'缓冲池其他项总大小','oracle.pool_misc[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24187,0,'','',10132,'缓冲池 SQL 区域大小','oracle.pool_sql_area[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24188,0,'','',10132,'当前进程数','oracle.procnum[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24189,0,'','',10132,'Redo 配置锁','oracle.RedoAllocationLatch[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24190,0,'','',10132,'回滚缓冲区繁忙率','oracle.RollBuffBusyRate[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24191,0,'','',10132,'活动会话数','oracle.session_active[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24192,0,'','',10132,'非活动会话数','oracle.session_inactive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24193,0,'','',10132,'当前会话数','oracle.session[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24194,0,'','',10132,'系统会话数','oracle.session_system[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24195,0,'','',10132,'SGA 缓冲区高速缓存','oracle.sga_buffer_cache[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24196,0,'','',10132,'SGA 固定大小','oracle.sga_fixed[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24197,0,'','',10132,'SGA java pool缓冲区','oracle.sga_java_pool[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24198,0,'','',10132,'SGA large pool缓冲区','oracle.sga_large_pool[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24199,0,'','',10132,'SGA 日志缓冲区','oracle.sga_log_buffer[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24200,0,'','',10132,'SGA 共享池大小','oracle.sga_shared_pool[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','M',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24201,0,'','',10132,'表空间使用量','oracle.tbl_space[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24202,0,'','',10132,'临时表空间可用率','oracle.TempTSLeftPct[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24203,0,'','',10132,'用户连接数','oracle.userconn[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24204,0,'','',10132,'被锁定用户列表','oracle.users_locked[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24205,0,'','',10132,'控制文件 I/O 等待事件','oracle.waits_controfileio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24206,0,'','',10132,'直接路径读等待事件','oracle.waits_directpath_read[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24207,0,'','',10132,'文件 I/O 等待事件','oracle.waits_file_io[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24208,0,'','',10132,'等待锁','oracle.waits_latch[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24209,0,'','',10132,'日志写等待事件','oracle.waits_logwrite[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24210,0,'','',10132,'多块读等待事件','oracle.waits_multiblock_read[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24211,0,'','',10132,'其他等待事件','oracle.waits_other[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24212,0,'','',10132,'单块读等待事件','oracle.waits_singleblock_read[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24213,0,'','',10132,'SQLNet 等待事件','oracle.waits_sqlnet[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','waits/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24512,0,'','',10144,'查询缓存可用量','mysql.qcache_free[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24519,0,'','',10144,'查询缓存总量','mysql.qcache_total[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24521,0,'','',10144,'每秒查询量','mysql.questions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','qps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24533,0,'','',10144,'系统会话数','mysql.threads_connected[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24535,0,'','',10144,'并发线程数','mysql.threads_running[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24537,0,'','',10144,'运行时间','mysql.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,3,'','uptime',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24539,0,'','',10144,'数据库版本','mysql.version[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24965,0,'','',10157,'ceilometer.openstack-ceilometer-alarm-evaluator','proc.num[python,,,ceilometer-alarm-evaluator]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24966,0,'','',10157,'ceilometer.openstack-ceilometer-alarm-notifier','proc.num[python,,,ceilometer-alarm-notifier]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24967,0,'','',10157,'ceilometer.openstack-ceilometer-api','proc.num[python,,,ceilometer-api]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24968,0,'','',10157,'ceilometer.openstack-ceilometer-collector','proc.num[python,,,ceilometer-collector]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24969,0,'','',10157,'ceilometer.openstack-ceilometer-notification','proc.num[python,,,ceilometer-agent-notification]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24970,0,'','',10157,'cinder.openstack-cinder-api','proc.num[python,,,cinder-api]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24971,0,'','',10157,'cinder.openstack-cinder-scheduler','proc.num[python,,,cinder-scheduler]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24972,0,'','',10157,'glance.openstack-glance-api','proc.num[python,,,glance-api]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24973,0,'','',10157,'glance.openstack-glance-registry','proc.num[python,,,glance-registry]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24974,0,'','',10157,'heat.openstack-heat-api','proc.num[python,,,heat-api\\b]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24975,0,'','',10157,'heat.openstack-heat-api-cfn','proc.num[python,,,heat-api-cfn]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24976,0,'','',10157,'heat.openstack-heat-engine','proc.num[python,,,heat-engine]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24977,2,'','',10157,'告警数','iaas.alert.count',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24978,2,'','',10157,'宿主机总数','iaas.hypervisorserver.count',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24979,2,'','',10157,'宿主机停止数','iaas.hypervisorserver.count.disabled',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24980,2,'','',10157,'宿主机故障数','iaas.hypervisorserver.count.down',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24981,2,'','',10157,'宿主机运行数','iaas.hypervisorserver.count.up',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24982,2,'','',10157,'镜像总数','iaas.image.count',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24983,2,'','',10157,'租户总数','iaas.tenant.count',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24984,2,'','',10157,'虚拟内核总量','iaas.vm.core.total',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24985,2,'','',10157,'虚拟内核已使用量','iaas.vm.core.used',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24986,2,'','',10157,'云主机总数','iaas.vm.count',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24987,2,'','',10157,'云主机运行个数','iaas.vm.count.active',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24988,2,'','',10157,'云主机故障个数','iaas.vm.count.error',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24989,2,'','',10157,'云主机停止个数','iaas.vm.count.shutoff',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24990,2,'','',10157,'内存总量','iaas.vm.memory.total',0,30,90,0,0,'','B',1,0,'',0,'','','1048576','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24991,2,'','',10157,'内存已使用量','iaas.vm.memory.used',600,30,90,0,3,'','B',1,0,'',0,'','','1048576','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24992,2,'','',10157,'卷存储已使用大小','iaas.vol.used',600,30,90,0,3,'','B',1,0,'',0,'','','1073741824','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24993,2,'','',10157,'VXLAN总数','iaas.vxlan.count',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24994,0,'','',10157,'keystone.openstack-keystone','proc.num[python,,,keystone-all]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24995,0,'','',10157,'nova.openstack-nova-api','proc.num[python,,,nova-api]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24996,0,'','',10157,'nova.openstack-nova-cert','proc.num[python,,,nova-cert]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24997,0,'','',10157,'nova.openstack-nova-conductor','proc.num[python,,,nova-conductor]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24998,0,'','',10157,'nova.openstack-nova-consoleauth','proc.num[python,,,nova-consoleauth]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',24999,0,'','',10157,'nova.openstack-nova-novncproxy','proc.num[python,,,nova-novncproxy]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25001,0,'','',10157,'nova.openstack-nova-scheduler','proc.num[python,,,nova-scheduler]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25002,0,'','',10157,'nova.openstack-nova-spicehtml5proxy','proc.num[python,,,nova-spicehtml5proxy]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25003,0,'','',10157,'get_tenants','get_tenants[{$PORTAL_IP}]',60,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25004,2,'','',10157,'floatip.quota[{#TID}]','iaas.floatip.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25005,2,'','',10157,'floatip.used[{#TID}]','iaas.floatip.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25006,2,'','',10157,'router.quota[{#TID}]','iaas.router.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25007,2,'','',10157,'router.used[{#TID}]','iaas.router.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25008,2,'','',10157,'securityrules.quota[{#TID}]','iaas.securityrules.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25009,2,'','',10157,'securityrules.used[{#TID}]','iaas.securityrules.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25010,2,'','',10157,'subnet.quota[{#TID}]','iaas.subnet.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25011,2,'','',10157,'subnet.used[{#TID}]','iaas.subnet.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25012,2,'','',10157,'vm.quota[{#TID}]','iaas.vm.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25013,2,'','',10157,'vm.used[{#TID}]','iaas.vm.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25014,2,'','',10157,'vmcore.quota[{#TID}]','iaas.vmcore.quota[{#TID}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25015,2,'','',10157,'vmcore.used[{#TID}]','iaas.vmcore.used[{#TID}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25016,2,'','',10157,'vmmem.quota[{#TID}]','iaas.vmmem.quota[{#TID}]',60,30,90,0,3,'','B',1,0,'',0,'','','1048576','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25017,2,'','',10157,'vmmem.used[{#TID}]','iaas.vmmem.used[{#TID}]',600,30,90,0,3,'','B',1,0,'',0,'','','1048576','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25029,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::sysContact.0',10161,'设备联系','snmp.agent[RFC1213-MIB::sysContact.0]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The textual identification of the contact person for this managed node, together with information on how to contact this person. If no contact information is known, the value is the zero-length string.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25030,4,'{$SNMP_COMMUNITY}','OLD-CISCO-SYS-MIB::avgBusy5.0',10161,'CPU5分钟占用率','snmp.agent[OLD-CISCO-SYS-MIB::avgBusy5.0]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','5 minute exponentially-decayed moving average of the CPU busy percentage',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25031,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::sysDescr.0',10161,'系统描述','snmp.agent[RFC1213-MIB::sysDescr.0]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','A textual description of the entity. This value should include the full name and version identification of the system\'s hardware type, software operating-system, and networking software.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25032,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::sysLocation.0',10161,'设备位置','snmp.agent[RFC1213-MIB::sysLocation.0]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The physical location of this node (e.g., \'telephone closet, 3rd floor\'). If the location is unknown, the value is the zero-length string.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25033,3,'','',10161,'连通性','icmpping',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25034,4,'{$SNMP_COMMUNITY}','DISMAN-EVENT-MIB::sysUpTimeInstance',10161,'运行时间','snmp.agent[DISMAN-EVENT-MIB::sysUpTimeInstance]',3600,30,90,0,3,'','uptime',1,0,'',0,'','','0.01','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25035,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifDescr',10161,'Interface discovery','snmp.agent[RFC1213-MIB::ifDescr]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25036,4,'{$SNMP_COMMUNITY}','CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolName',10161,'Memory pool discovery','snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolName]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25037,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInBroadcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 广播包转发总数','snmp.agent[IF-MIB::ifInBroadcastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of packets, delivered by this sub-layer to a higher (sub-)layer, which were addressed to a broadcast address at this sub-layer.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of\r\nifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25038,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutBroadcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 广播包请求总数','snmp.agent[IF-MIB::ifOutBroadcastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The total number of packets that higher-level protocols requested be transmitted, and which were addressed to a broadcast address at this sub-layer, including those that were discarded or not sent.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25039,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInOctets.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接收字节数/秒','snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The total number of octets received on the interface, including framing characters.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25040,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOutOctets.{#SNMPINDEX}',10161,'{#SNMPVALUE} 发送字节数/秒','snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The total number of octets transmitted out of the interface, including framing characters.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25041,4,'{$SNMP_COMMUNITY}','IF-MIB::ifConnectorPresent.{#SNMPINDEX}',10161,'{#SNMPVALUE} 当前连接状态','snmp.agent[IF-MIB::ifConnectorPresent.{#SNMPINDEX}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,23,'','','',0,0,'','','','',0,2,'',NULL,'','This object has the value \'true(1)\' if the interface sublayer has a physical connector and the value \'false(2)\' otherwise.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25042,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifAdminStatus.{#SNMPINDEX}',10161,'{#SNMPVALUE} 期望状态','snmp.agent[RFC1213-MIB::ifAdminStatus.{#SNMPINDEX}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,11,'','','',0,0,'','','','',0,2,'',NULL,'','The desired state of the interface. The testing(3) state indicates that no operational packets can be passed. When a managed system initializes, all nterfaces start with\r\nifAdminStatus in the down(2) state. As a result of either explicit management action or per configuration information retained by the managed system, ifAdminStatus is then\r\nchanged to either the up(1) or testing(3) states (or remains in the down(2) state).',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25043,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInDiscards.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接收丢包总数','snmp.agent[RFC1213-MIB::ifInDiscards.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of inbound packets which were chosen to be discarded even though no errors had been detected to prevent their being deliverable to a higher-layer protocol. One possible reason for discarding such a packet could be to free up buffer space.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25044,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInUnknownProtos.{#SNMPINDEX}',10161,'{#SNMPVALUE} 未识别协议的下行丢包总数','snmp.agent[RFC1213-MIB::ifInUnknownProtos.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of packets received via the interface which were discarded because of an unknown or unsupported protocol. For character-oriented or fixed-length interfaces that support protocol multiplexing the number of transmission units received via the interface which were discarded because of an unknown or unsupported protocol. For any interface that does not support protocol multiplexing, this counter will always be 0.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25045,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}',10161,'{#SNMPVALUE} 发送丢包总数','snmp.agent[RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of outbound packets which were chosen to be discarded even though no errors had been detected to prevent their being transmitted. One possible reason for discarding such a packet could be to free up buffer space.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25046,4,'{$SNMP_COMMUNITY}','EtherLike-MIB::dot3StatsDuplexStatus.{#SNMPINDEX}',10161,'{#SNMPVALUE} 双工模式','snmp.agent[EtherLike-MIB::dot3StatsDuplexStatus.{#SNMPINDEX}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,24,'','','',0,0,'','','','',0,2,'',NULL,'','The current mode of operation of the MAC entity. \'unknown\' indicates that the current duplex mode could not be etermined.\r\n\r\nManagement control of the duplex mode is accomplished through the MAU MIB. When an interface does not support autonegotiation, or when autonegotiation is not enabled, the duplex mode is controlled using ifMauDefaultType. When autonegotiation is supported and enabled, duplex mode is controlled using ifMauAutoNegAdvertisedBits. In either case, the currently operating duplex mode is reflected both in this object and in ifMauType.\r\n\r\nNote that this object provides redundant information with ifMauType. Normally, redundant objects are discouraged. However, in this\r\ninstance, it allows a management application to determine the duplex status of an interface without having to know every possible value of ifMauType. This was felt to be sufficiently valuable to justify the redundancy.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25047,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInErrors.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接收错误包总数','snmp.agent[RFC1213-MIB::ifInErrors.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of inbound packets that contained errors preventing them from being deliverable to a higher-layer protocol. For character-oriented or fixed-length interfaces, the number of inbound transmission units that contained errors preventing them from being deliverable to a higher-layer protocol.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25048,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOutErrors.{#SNMPINDEX}',10161,'{#SNMPVALUE} 发送错误包总数','snmp.agent[RFC1213-MIB::ifOutErrors.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of outbound packets that could not be transmitted because of errors. For character-oriented or fixed-length interfaces, the number of outbound transmission units that could not be transmitted because of errors.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25049,4,'{$SNMP_COMMUNITY}','IF-MIB::ifHighSpeed.{#SNMPINDEX}',10161,'{#SNMPVALUE} 最大带宽','snmp.agent[IF-MIB::ifHighSpeed.{#SNMPINDEX}]',86400,30,90,0,3,'','b',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','An estimate of the interface\'s current bandwidth in units of 1,000,000 bits per second. If this object reports a value of `n\' then the speed of the interface is somewhere in the range of `n-500,000\' to `n+499,999\'. For interfaces which do not vary in bandwidth or for those where no accurate estimation can be made, this object should contain the nominal bandwidth. For a sub-layer which has no concept of bandwidth, this object should be zero.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25050,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifMtu.{#SNMPINDEX}',10161,'{#SNMPVALUE} 最大传输单元','snmp.agent[RFC1213-MIB::ifMtu.{#SNMPINDEX}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The size of the largest packet which can be sent/received on the interface, specified in octets. For interfaces that are used for transmitting network datagrams, this is the size of the largest network datagram that can be sent on the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25051,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInMulticastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 组播包转发总数','snmp.agent[IF-MIB::ifInMulticastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of packets, delivered by this sub-layer to a higher (sub-)layer, which were addressed to a multicast address at this sub-layer. For a MAC layer protocol, this includes both Group and Functional addresses.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25052,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutMulticastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 组播包请求总数','snmp.agent[IF-MIB::ifOutMulticastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The total number of packets that higher-level protocols requested be transmitted, and which were addressed to a multicast address at this sub-layer, including those that\r\nwere discarded or not sent. For a MAC layer protocol, this includes both Group and Functional addresses.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25053,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOperStatus.{#SNMPINDEX}',10161,'{#SNMPVALUE} 当前操作状态','snmp.agent[RFC1213-MIB::ifOperStatus.{#SNMPINDEX}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,8,'','','',0,0,'','','','',0,2,'',NULL,'','The current operational state of the interface. The testing(3) state indicates that no operational packets can be passed. If ifAdminStatus is down(2) then ifOperStatus should be down(2). If ifAdminStatus is changed to up(1) then ifOperStatus should change to up(1) if the interface is ready to transmit and receive network traffic; it should change to dormant(5) if the interface is waiting for external actions (such as a serial line waiting for an incoming connection); it should remain in the down(2) state if and only if there is a fault that prevents it from going to the up(1) state; it should remain in the notPresent(6) state if the interface has missing (typically, hardware) components.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25054,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifLastChange.{#SNMPINDEX}',10161,'{#SNMPVALUE} 状态改变最近时间','snmp.agent[RFC1213-MIB::ifLastChange.{#SNMPINDEX}]',600,30,90,0,3,'','uptime',1,0,'',0,'','','0.01','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The value of sysUpTime at the time the interface entered its current operational state. If the current state was entered prior to the last re-initialization of the local network management subsystem, then this object contains a zero value.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25055,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifPhysAddress.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接口MAC地址','snmp.agent[RFC1213-MIB::ifPhysAddress.{#SNMPINDEX}]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The interface\'s address at its protocol sub-layer. For example, for an 802.x interface, this object normally contains a MAC address. The interface\'s media-specific MIB\r\nmust define the bit and byte ordering and the format of the value of this object. For interfaces which do not have such an address (e.g., a serial line), this object should contain an octet string of zero length.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25056,4,'{$SNMP_COMMUNITY}','IF-MIB::ifPromiscuousMode.{#SNMPINDEX}',10161,'{#SNMPVALUE} 混杂模式','snmp.agent[IF-MIB::ifPromiscuousMode.{#SNMPINDEX}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,23,'','','',0,0,'','','','',0,2,'',NULL,'','This object has a value of false(2) if this interface only accepts packets/frames that are addressed to this station. This object has a value of true(1) when the station accepts all packets/frames transmitted on the media. The value true(1) is only legal on certain types of media. If legal, setting this object to a value of true(1) may require the interface to be reset before ecoming effective.\r\n\r\nThe value of ifPromiscuousMode does not affect the reception of broadcast and multicast packets/frames by the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25057,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifType.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接口类型','snmp.agent[RFC1213-MIB::ifType.{#SNMPINDEX}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,25,'','','',0,0,'','','','',0,2,'',NULL,'','The type of interface. Additional values for ifType are assigned by the Internet Assigned Numbers Authority (IANA), through updating the syntax of the IANAifType textual convention.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25058,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 单播包转发包数','snmp.agent[RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of packets, delivered by this sub-layer to a higher (sub-)layer, which were not addressed to a multicast or broadcast address at this sub-layer.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25060,4,'{$SNMP_COMMUNITY}','CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolFree.{#SNMPINDEX}',10161,'{#SNMPVALUE} 内存池空闲大小','snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolFree.{#SNMPINDEX}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','Indicates the number of bytes from the memory pool that are currently unused on the managed device.\r\n\r\nNote that the sum of ciscoMemoryPoolUsed and ciscoMemoryPoolFree is the total amount of memory in the pool',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25061,4,'{$SNMP_COMMUNITY}','CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}',10161,'{#SNMPVALUE} 内存池已使用大小','snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','Indicates the number of bytes from the memory pool that are currently in use by applications on the managed device',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25062,4,'{$SNMP_COMMUNITY}','CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolValid.{#SNMPINDEX}',10161,'{#SNMPVALUE} 内存池数据有效性','snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolValid.{#SNMPINDEX}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,23,'','','',0,0,'','','','',0,2,'',NULL,'','Indicates whether or not the remaining objects in this entry contain accurate data. If an instance of this object has the value false (which in and of itself indicates an internal error condition), the values of the remaining objects in the conceptual row may contain inaccurate information (specifically, the reported values may be less than the actual values).',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25097,4,'{$SNMP_COMMUNITY}','UCD-SNMP-MIB::ssCpuIdle.0',10163,'CPU空闲率','cpuIdle',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25098,15,'','',10163,'CPU使用率','cpuUsage',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(100-last(\"cpuIdle\"))/(1-nodata(\"cpuIdle\",2400))','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25099,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysName.0',10163,'系统名称','osHostName',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25100,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrSystemDate.0',10163,'系统时间','osTime',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25101,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysDescr.0',10163,'操作系统类型','osType',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25102,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrSystemUptime.0',10163,'系统运行时间','osUptime',60,30,90,0,3,'','uptime',1,0,'',0,'','','0.01','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25103,4,'{$SNMP_COMMUNITY}','UCD-DISKIO-MIB::diskIODevice',10163,'Disk_Discovery','Disk_Discovery',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@Disk_Discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25104,4,'{$SNMP_COMMUNITY}','IF-MIB::ifDescr',10163,'If_Discovery','If_Discovery',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@If_Discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25105,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrDeviceType',10163,'listCPU','listCPU',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@CPU_Discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25106,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageDescr',10163,'Memory_Discovery','Memory_Discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@Memory_Discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25107,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrFSType',10163,'Mountpoint_Discovery','Mountpoint_Discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@Patition_Discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25108,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageDescr',10163,'Patition_Discovery','Patition_Discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#SNMPVALUE}:@Storage devices for SNMP discovery',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25109,4,'{$SNMP_COMMUNITY}','UCD-DISKIO-MIB::diskIONRead.{#SNMPINDEX}',10163,'磁盘$1读速率','diskReadBPS[{#SNMPVALUE}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25110,4,'{$SNMP_COMMUNITY}','UCD-DISKIO-MIB::diskIOReads.{#SNMPINDEX}',10163,'磁盘$1每秒读操作数','diskReadIOPS[{#SNMPVALUE}]',600,30,90,0,0,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25111,4,'{$SNMP_COMMUNITY}','UCD-DISKIO-MIB::diskIONWritten.{#SNMPINDEX}',10163,'磁盘$1写速率','diskWriteBPS[{#SNMPVALUE}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25112,4,'{$SNMP_COMMUNITY}','UCD-DISKIO-MIB::diskIOWrites.{#SNMPINDEX}',10163,'磁盘$1每秒写操作数','diskWriteIOPS[{#SNMPVALUE}]',600,30,90,0,0,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25113,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInOctets.{#SNMPINDEX}',10163,'网络$1下行速率','netInBPS[{#SNMPVALUE}]',600,30,90,0,0,'','Bps',1,1,'',0,'','','0.125','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25114,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInUcastPkts.{#SNMPINDEX}',10163,'网络$1下行IOPS','netInIOPS[{#SNMPVALUE}]',600,30,90,0,0,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25115,4,'{$SNMP_COMMUNITY}','IF-MIB::ifHighSpeed.{#SNMPINDEX}',10163,'网络$1下行带宽','netInMax[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25116,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutOctets.{#SNMPINDEX}',10163,'网络$1上行速率','netOutBPS[{#SNMPVALUE}]',600,30,90,0,0,'','Bps',1,1,'',0,'','','0.125','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25117,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutUcastPkts.{#SNMPINDEX}',10163,'网络$1上行IOPS','netOutIOPS[{#SNMPVALUE}]',600,30,90,0,3,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25118,4,'{$SNMP_COMMUNITY}','IF-MIB::ifHighSpeed.{#SNMPINDEX}',10163,'网络$1上行带宽','netOutMax[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25119,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrDeviceDescr.{#SNMPINDEX}',10163,'CPU$1描述','cpuDesc[{#INDEX}]',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25120,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrProcessorLoad.{#SNMPINDEX}',10163,'CPU$1负载','cpuLoad[{#INDEX}]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25121,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageSize.{#SNMPINDEX}',10163,'内存$1的簇总数目','memTotal[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25122,15,'','',10163,'内存{#SNMPVALUE}总量','memTotalInBytes[{#SNMPVALUE}]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"memTotal[{#SNMPVALUE}]\", 14400)*last(\"Units[{#SNMPVALUE}]\", 345600))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25123,15,'','',10163,'内存{#SNMPVALUE}使用率','memUsage[{#SNMPVALUE}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"memUsed[{#SNMPVALUE}]\", 2400) / last(\"memTotal[{#SNMPVALUE}]\", 14400)) *100)/(1-nodata(\"memUsed[{#SNMPVALUE}]\",2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25124,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageUsed.{#SNMPINDEX}',10163,'内存$1已使用簇数目','memUsed[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25125,15,'','',10163,'内存{#SNMPVALUE}使用量','memUsedInBytes[{#SNMPVALUE}]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"memUsed[{#SNMPVALUE}]\", 2400)*last(\"Units[{#SNMPVALUE}]\", 345600))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25126,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageAllocationUnits.{#SNMPINDEX}',10163,'内存$1的簇大小','Units[{#SNMPVALUE}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25127,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrFSStorageIndex.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}编号','fsIndex[{#SNMPINDEX}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25128,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrFSMountPoint.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}挂载点','fsMountPoint[{#SNMPINDEX}]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25129,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrFSType.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}类型','fsType[{#SNMPINDEX}]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25130,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageSize.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}的簇总数目','fsTotal[{#SNMPINDEX}]',3600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25131,15,'','',10163,'分区{#SNMPVALUE}总量','fsTotalInBytes[{#SNMPINDEX}]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"fsTotal[{#SNMPINDEX}]\", 14400)*last(\"Units[{#SNMPINDEX}]\", 345600))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25132,15,'','',10163,'分区{#SNMPVALUE}使用率','fsUsage[{#SNMPINDEX}]',3600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"fsUsed[{#SNMPINDEX}]\", 14400) / last(\"fsTotal[{#SNMPINDEX}]\", 14400)) *100)/(1-nodata(\"fsUsed[{#SNMPINDEX}]\", 14400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25133,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageUsed.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}已使用簇数目','fsUsed[{#SNMPINDEX}]',3600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25134,15,'','',10163,'分区{#SNMPVALUE}使用量','fsUsedInBytes[{#SNMPINDEX}]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"fsUsed[{#SNMPINDEX}]\", 14400)*last(\"Units[{#SNMPINDEX}]\", 345600))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25135,4,'{$SNMP_COMMUNITY}','HOST-RESOURCES-MIB::hrStorageAllocationUnits.{#SNMPINDEX}',10163,'分区{#SNMPVALUE}的簇大小','Units[{#SNMPINDEX}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25136,0,'','',10164,'ASP.Net应用重启次数','perf_counter[\"\\ASP.NET\\Application Restarts\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25137,0,'','',10164,'ASP.Net当前请求数','perf_counter[\"\\ASP.NET\\Requests Current\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25138,0,'','',10164,'ASP.Net每秒请求数','perf_counter[\"\\ASP.NET Applications(__Total__)\\Requests/Sec\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25139,0,'','',10164,'ASP.Net错误总次数','perf_counter[\"\\ASP.NET Applications(__Total__)\\Errors Total\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25140,0,'','',10164,'ASP.Net进程重启次数','perf_counter[\"\\ASP.NET\\Worker Process Restarts\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25141,0,'','',10164,'IIS当前匿名用户数','perf_counter[\"\\Web Service(_Total)\\Current Anonymous Users\"]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25142,0,'','',10164,'IIS当前连接数','perf_counter[\"\\Web Service(_Total)\\Current Connections\"]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25143,0,'','',10164,'IIS当前非匿名用户数','perf_counter[\"\\Web Service(_Total)\\Current NonAnonymous Users\"]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25144,0,'','',10164,'IIS Get 请求数/秒','perf_counter[\"\\Web Service(_Total)\\Total Get Requests\"]',600,30,90,0,0,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25145,0,'','',10164,'IIS Head 请求数/秒','perf_counter[\"\\Web Service(_Total)\\Total Head Requests\"]',600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25146,0,'','',10164,'IIS Post 请求数/秒','perf_counter[\"\\Web Service(_Total)\\Total Post Requests\"]',600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25147,0,'','',10164,'Microsoft IIS: 服务状态','service_state[W3SVC]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,3,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25386,0,'','',10172,'平均每次刷新执行时间','MongoDB.Status[backgroundFlushing,{$MONGOIP},average_ms]',600,30,90,0,0,'','ms',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25387,0,'','',10172,'实例刷新数据到磁盘的操作数','MongoDB.Status[backgroundFlushing,{$MONGOIP},flushes]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25388,0,'','',10172,'最后一次刷新执行时间','MongoDB.Status[backgroundFlushing,{$MONGOIP},last_ms]',600,30,90,0,3,'','ms',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25389,0,'','',10172,'后台刷新时间','MongoDB.Status[backgroundFlushing,{$MONGOIP},total_ms]',600,30,90,0,3,'','ms',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25390,0,'','',10172,'系统会话个数','MongoDB.Status[connections,{$MONGOIP},current]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25391,0,'','',10172,'访问索引次数','MongoDB.Status[indexCounters,{$MONGOIP},accesses]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25392,0,'','',10172,'索引在内存中的命中次数','MongoDB.Status[indexCounters,{$MONGOIP},hits]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25393,0,'','',10172,'索引不是在内存中被命中的次数','MongoDB.Status[indexCounters,{$MONGOIP},misses]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25394,0,'','',10172,'索引计数器被重置的次数','MongoDB.Status[indexCounters,{$MONGOIP},resets]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25395,0,'','',10172,'数据量值','MongoDB.Status[mem,{$MONGOIP},mapped]',3600,30,90,0,3,'','B',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25396,0,'','',10172,'使用的物理内存大小','MongoDB.Status[mem,{$MONGOIP},resident]',600,30,90,0,3,'','B',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25397,0,'','',10172,'使用的虚拟内存大小','MongoDB.Status[mem,{$MONGOIP},virtual]',600,30,90,0,3,'','B',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25398,0,'','',10172,'网络接收字节数','MongoDB.Status[network,{$MONGOIP},bytesIn]',600,30,90,0,3,'','B/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25399,0,'','',10172,'网络发送字节数','MongoDB.Status[network,{$MONGOIP},bytesOut]',600,30,90,0,3,'','B/s',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25400,0,'','',10172,'操作数','MongoDB.Status[opcounters,{$MONGOIP},command]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25401,0,'','',10172,'执行delete次数','MongoDB.Status[opcounters,{$MONGOIP},delete]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25402,0,'','',10172,'游标执行getMore操作数','MongoDB.Status[opcounters,{$MONGOIP},getmore]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25403,0,'','',10172,'执行insert次数','MongoDB.Status[opcounters,{$MONGOIP},insert]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25404,0,'','',10172,'执行query次数','MongoDB.Status[opcounters,{$MONGOIP},query]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25405,0,'','',10172,'执行update次数','MongoDB.Status[opcounters,{$MONGOIP},update]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25415,0,'','',10175,'libvirtd','proc.num[libvirtd]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25416,0,'','',10175,'neutron-openvswitch-agent','proc.num[python,,,neutron-openvswitch-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25417,0,'','',10175,'openstack-nova-compute','proc.num[python,,,nova-compute]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25418,0,'','',10175,'openstack-ceilometer-compute','proc.num[python,,,ceilometer-agent-compute]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25419,0,'','',10176,'network.neutron-dhcp-agent','proc.num[python,,,neutron-dhcp-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25420,0,'','',10176,'network.neutron-l3-agent','proc.num[python,,,neutron-l3-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25421,0,'','',10176,'network.neutron-lbaas-agent','proc.num[python,,,neutron-lbaas-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25422,0,'','',10176,'network.neutron-metadata-agent','proc.num[python,,,neutron-metadata-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25423,0,'','',10176,'network.neutron-openvswitch-agent','proc.num[python,,,neutron-openvswitch-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25425,0,'','',10176,'network.neutron-vpn-agent','proc.num[python,,,neutron-vpn-agent]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25426,0,'','',10176,'network.openvswitch','proc.num[ovs-vswitchd]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25429,0,'','',10178,'horizon.httpd','proc.num[httpd]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25430,0,'','',10179,'运行状态','mssql.is_alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',3,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25431,0,'','',10179,'缓存命中率','mssql.cachehit[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25432,0,'','',10179,'数据库空间总量','mssql.dbsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25433,0,'','',10179,'IO等待请求数','mssql.iopending[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25434,0,'','',10179,'日志空间总量','mssql.logsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25435,0,'','',10179,'日志空间使用量','mssql.logusedsize[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25436,0,'','',10179,'读IO速率','mssql.pagereads[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25437,0,'','',10179,'写IO速率','mssql.pagewrites[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25439,0,'','',10157,'ceilometer.openstack-ceilometer-central','proc.num[python,,,ceilometer-agent-central]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25442,0,'','',10157,'neutron.neutron-server','proc.num[python,,,neutron-server]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25443,0,'','',10113,'ceph-mds','ceph.mds',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25444,0,'','',10113,'ceph-osd','ceph.osd',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25503,0,'','',10182,'进程总数','proc.num[]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25504,0,'','',10182,'CPU负载','system.cpu.load[percpu,avg1]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25505,15,'','',10182,'CPU空闲率','CPUIdle',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(100-last(\"perf_counter[\\\"\\Processor(_Total)\\% Processor Time\\\"]\"))/(1-nodata(\"perf_counter[\\\"\\Processor(_Total)\\% Processor Time\\\"]\",2400))','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25506,0,'','',10182,'CPU使用率','perf_counter[\"\\Processor(_Total)\\% Processor Time\"]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25508,0,'','',10182,'系统当前时间','system.localtime[local]',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25510,0,'','',10182,'swap空闲大小','system.swap.size[,free]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25511,0,'','',10182,'swap使用率','perf_counter[\"\\Memory\\% Committed Bytes In Use\"]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25512,0,'','',10182,'swap总大小','system.swap.size[,total]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25513,0,'','',10182,'系统详情','system.uname',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25514,0,'','',10182,'系统运行时长','system.uptime',60,30,90,0,3,'','uptime',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25521,0,'','',10182,'物理内存已分配容量','vm.memory.size[used]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25522,0,'','',10182,'物理内存使用率','vm.memory.size[pused]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25523,0,'','',10182,'物理内存总容量','vm.memory.size[total]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25527,0,'','',10182,'操作系统类型','wmi.get[root\\cimv2,Select Caption from Win32_OperatingSystem]',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25528,0,'','',10182,'磁盘读请求速率','perf_counter[\"\\LogicalDisk(_Total)\\Disk Read Bytes/sec\"]',600,30,90,0,0,'','Bps',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25529,0,'','',10182,'磁盘读请求IOPS','perf_counter[\"\\LogicalDisk(_Total)\\Disk Reads/sec\"]',600,30,90,0,0,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25530,0,'','',10182,'磁盘写请求速率','perf_counter[\"\\LogicalDisk(_Total)\\Disk Write Bytes/sec\"]',600,30,90,0,0,'','Bps',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25531,0,'','',10182,'磁盘写请求IOPS','perf_counter[\"\\LogicalDisk(_Total)\\Disk Writes/sec\"]',600,30,90,0,0,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25532,0,'','',10182,'CPU型号','wmi.get[root\\cimv2,Select Name from Win32_processor]',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25533,0,'','',10182,'Network interface discovery','net.if.discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#IFNAME}:@Network interfaces for discovery',NULL,'','',0,'3',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25534,0,'','',10182,'网络{#IFNAME}下行速率','net.if.in[{#IFNAME}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25535,0,'','',10182,'网络{#IFNAME}下行IOPS','net.if.in[{#IFNAME},packets]',600,30,90,0,3,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25536,0,'','',10182,'网络{#IFNAME}上行速率','net.if.out[{#IFNAME}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25537,0,'','',10182,'网络{#IFNAME}上行IOPS','net.if.out[{#IFNAME},packets]',600,30,90,0,3,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25538,0,'','',10182,'Mounted filesystem discovery','vfs.fs.discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#FSTYPE}:@File systems for discovery',NULL,'','',0,'3',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25539,0,'','',10182,'分区[{#FSNAME}]总容量','vfs.fs.size[{#FSNAME},total]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25540,0,'','',10182,'分区[{#FSNAME}]已用容量','vfs.fs.size[{#FSNAME},used]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25541,0,'','',10182,'分区[{#FSNAME}]使用率','vfs.fs.size[{#FSNAME},pused]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25542,0,'','',10182,'{#FSTYPE}分区[{#FSNAME}]空闲率','vfs.fs.size[{#FSNAME},pfree]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25543,7,'','',10183,'CPU空闲率','system.cpu.util[,idle]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25544,15,'','',10183,'CPU使用率_Linux','cpuUtil',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(100-last(\"system.cpu.util[,idle]\"))/(1-nodata(\"system.cpu.util[,idle]\",2400))','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25545,7,'','',10183,'CPU使用率_Windows','wmi.get[root\\cimv2,Select LoadPercentage from Win32_processor]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25546,7,'','',10183,'CPU型号_Linux','system.hw.cpu[0,full]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25547,7,'','',10183,'CPU型号_Windows','wmi.get[root\\cimv2,Select Name from Win32_processor]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25548,7,'','',10183,'CPU负载','system.cpu.load[percpu,avg1]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25549,7,'','',10183,'FTP服务状态','net.tcp.listen[21]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25550,7,'','',10183,'IMAP服务状态','net.tcp.listen[143]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25551,7,'','',10183,'LDAP ssl服务状态','net.tcp.listen[636]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25552,7,'','',10183,'LDAP服务状态','net.tcp.listen[389]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25553,7,'','',10183,'NNTP服务状态','net.tcp.listen[119]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25554,7,'','',10183,'NTP服务状态','net.udp.listen[123]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25555,7,'','',10183,'POP服务状态','net.tcp.listen[110]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25556,7,'','',10183,'进程总数','proc.num[]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25558,7,'','',10183,'administrator进程总数','proc.num[,root]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25559,7,'','',10183,'睡眠进程总数','proc.num[,,sleep]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25560,7,'','',10183,'僵尸进程总数','proc.num[,,zomb]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25561,7,'','',10183,'SMTP服务状态','net.tcp.listen[25]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25562,7,'','',10183,'swap空闲大小','system.swap.size[,free]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25563,7,'','',10183,'swap总大小','system.swap.size[,total]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25564,7,'','',10183,'swap使用率','system.swap.size[,pused]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25565,2,'','',10183,'所属用户','vm.user',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25566,2,'','',10183,'所属租户','vm.tenant',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25567,7,'','',10183,'操作系统类型_Linux','system.sw.os',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25568,7,'','',10183,'操作系统类型_Windows','wmi.get[root\\cimv2,Select Caption from Win32_OperatingSystem]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25569,2,'','',10183,'浮动IP','vm.floatingIps',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25570,7,'','',10183,'物理内存使用率','vm.memory.size[pused]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25571,7,'','',10183,'物理内存已分配容量','vm.memory.size[used]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25572,7,'','',10183,'物理内存总容量','vm.memory.size[total]',600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25573,2,'','',10183,'状态','vm.status',60,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25574,7,'','',10183,'磁盘写请求IOPS_Linux','vfs.dev.write[,operations]',600,30,90,0,3,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25575,7,'','',10183,'磁盘写请求IOPS_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Writes/sec\"]',600,30,90,0,0,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25576,7,'','',10183,'磁盘写请求速率_Linux','vfs.dev.write[,sectors]',600,30,90,0,3,'','Bps',1,1,'',0,'','','512','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25577,7,'','',10183,'磁盘写请求速率_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Write Bytes/sec\"]',600,30,90,0,0,'','Bps',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25578,7,'','',10183,'磁盘读请求IOPS_Linux','vfs.dev.read[,operations]',600,30,90,0,3,'','iops',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25579,7,'','',10183,'磁盘读请求IOPS_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Reads/sec\"]',600,30,90,0,0,'','iops',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25580,7,'','',10183,'磁盘读请求速率_Linux','vfs.dev.read[,sectors]',600,30,90,0,3,'','Bps',1,1,'',0,'','','512','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25581,7,'','',10183,'磁盘读请求速率_Windows','perf_counter[\"\\LogicalDisk(_Total)\\Disk Read Bytes/sec\"]',600,30,90,0,0,'','Bps',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25582,7,'','',10183,'系统当前时间','system.localtime[local]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25583,7,'','',10183,'系统详情','system.uname',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25584,7,'','',10183,'系统运行时长','system.uptime',600,30,90,0,3,'','uptime',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25585,7,'','',10183,'Mounted filesystem discovery','vfs.fs.discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#FSTYPE}:@File systems for discovery',NULL,'','',0,'3',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25586,7,'','',10183,'Network interface discovery','net.if.discovery',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,'{#IFNAME}:@Network interfaces for discovery',NULL,'','',0,'3',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25587,7,'','',10183,'{#FSTYPE}分区[{#FSNAME}]空闲率','vfs.fs.size[{#FSNAME},pfree]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25588,7,'','',10183,'分区[{#FSNAME}]使用率','vfs.fs.size[{#FSNAME},pused]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25589,7,'','',10183,'分区[{#FSNAME}]已用容量','vfs.fs.size[{#FSNAME},used]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25590,7,'','',10183,'分区[{#FSNAME}]总容量','vfs.fs.size[{#FSNAME},total]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25591,7,'','',10183,'网络{#IFNAME}上行IOPS','net.if.out[{#IFNAME},packets]',600,30,90,0,3,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25592,7,'','',10183,'网络{#IFNAME}上行速率','net.if.out[{#IFNAME}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25593,7,'','',10183,'网络{#IFNAME}下行IOPS','net.if.in[{#IFNAME},packets]',600,30,90,0,3,'','pps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25594,7,'','',10183,'网络{#IFNAME}下行速率','net.if.in[{#IFNAME}]',600,30,90,0,0,'','Bps',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25601,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysContact.0',10186,'设备联系细节','sysContact',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The textual identification of the contact person for this managed node, together with information on how to contact this person.  If no contact information is known, the value is the zero-length string.',23,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25602,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysDescr.0',10186,'设备系统描述','sysDescr',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','A textual description of the entity.  This value should include the full name and version identification of the system\'s hardware type, software operating-system, and networking software.',14,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25603,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysLocation.0',10186,'设备位置','sysLocation',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The physical location of this node (e.g., `telephone closet, 3rd floor\').  If the location is unknown, the value is the zero-length string.',24,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25604,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysName.0',10186,'设备系统名称','sysName',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','An administratively-assigned name for this managed node. By convention, this is the node\'s fully-qualified domain name.  If the name is unknown, the value is the zero-length string.',3,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25605,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysUpTime.0',10186,'设备运行时间','sysUpTime',60,30,90,0,3,'','uptime',1,0,'',0,'','','0.01','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The time since the network management portion of the system was last re-initialized.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25606,4,'{$SNMP_COMMUNITY}','IF-MIB::ifNumber.0',10186,'网络接口数目','ifNumber',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The number of network interfaces (regardless of their current state) present on this system.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25608,4,'{$SNMP_COMMUNITY}','IF-MIB::ifDescr',10186,'Network interfaces','ifDescr',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','You may also consider using IF-MIB::ifType or IF-MIB::ifAlias for discovery depending on your filtering needs.\r\n\r\n{$SNMP_COMMUNITY} is a global macro.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25617,4,'{$SNMP_COMMUNITY}','IF-MIB::ifAdminStatus.{#SNMPINDEX}',10186,'$1 期望状态','ifAdminStatus[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,11,'','','',0,0,'','','','',0,2,'',NULL,'','The desired state of the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25618,4,'{$SNMP_COMMUNITY}','IF-MIB::ifAlias.{#SNMPINDEX}',10186,'$1 接口别名','ifAlias[{#SNMPVALUE}]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25619,4,'{$SNMP_COMMUNITY}','IF-MIB::ifDescr.{#SNMPINDEX}',10186,'$1 接口描述','ifDescr[{#SNMPVALUE}]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','A textual string containing information about the interface.  This string should include the name of the manufacturer, the product name and the version of the interface hardware/software.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25620,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInErrors.{#SNMPINDEX}',10186,'$1 下行错误包总数','ifInErrors[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of inbound packets that contained errors preventing them from being deliverable to a higher-layer protocol.  For character-oriented or fixed-length interfaces, the number of inbound transmission units that contained errors preventing them from being deliverable to a higher-layer protocol.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25621,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInOctets.{#SNMPINDEX}',10186,'$1 下行速率','ifInOctets[{#SNMPVALUE}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of octets in valid MAC frames received on this interface, including the MAC header and FCS.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25622,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOperStatus.{#SNMPINDEX}',10186,'$1 当前操作状态','ifOperStatus[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,8,'','','',0,0,'','','','',0,2,'',NULL,'','The current operational state of the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25623,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutErrors.{#SNMPINDEX}',10186,'$1 上行错误包总数','ifOutErrors[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of outbound packets that could not be transmitted because of errors. For character-oriented or fixed-length interfaces, the number of outbound transmission units that could not be transmitted because of errors.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25624,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutOctets.{#SNMPINDEX}',10186,'$1 上行速率','ifOutOctets[{#SNMPVALUE}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of octets transmitted in MAC frames on this interface, including the MAC header and FCS.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25692,0,'','',10175,'openstack-cinder-volume','proc.num[python,,,cinder-volume]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25693,0,'','',10175,'openvswitch','proc.num[ovs-vswitchd]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25694,0,'','',10175,'tgtd','proc.num[tgtd]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25695,0,'','',10175,'messagebus','proc.num[dbus-daemon]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25696,0,'','',10175,'spsagent','proc.num[SPSAGENT]',60,30,90,1,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25697,0,'','',10157,'keystone.memcached','proc.num[memcached]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25698,0,'','',10157,'mysql.mysqld','proc.num[mysqld]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25699,0,'','',10157,'sps.sps-api','proc.num[sps-api]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25700,0,'','',10176,'ipsec.ipsec','proc.num[sh,,,ipsec]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,1,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25734,0,'','',10154,'持久堆最大使用量','tomcat.psPermGenUsageMax[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25735,0,'','',10154,'持久堆当前使用量','tomcat.psPermGenUsageUsed[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25736,0,'','',10154,'非堆最大使用量','tomcat.nonHeapMemoryUsageMax[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25737,0,'','',10154,'非堆当前使用量','tomcat.nonHeapMemoryUsageUsed[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25738,0,'','',10154,'堆最大使用量','tomcat.heapMemoryUsageMax[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25739,0,'','',10154,'堆当前使用量','tomcat.heapMemoryUsageUsed[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25740,0,'','',10154,'启动时间','tomcat.startTime[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25741,0,'','',10154,'总会话数','tomcat.sessionCounter[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25742,0,'','',10154,'活动会话数','tomcat.activeSessions[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25743,0,'','',10154,'每秒请求错误数','tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25744,0,'','',10154,'最大线程数','tomcat.maxThreads[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25745,0,'','',10154,'gzip压缩','tomcat.gzip[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',3,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25746,0,'','',10154,'当前线程数','tomcat.currentThreadCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25747,0,'','',10154,'繁忙线程数','tomcat.currentThreadsBusy[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25748,0,'','',10154,'运行时间','tomcat.uptime[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',60,30,90,0,0,'','uptime',1,0,'',0,'','','0.001','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25749,0,'','',10154,'每秒请求数','tomcat.requestCount[{$TOMCAT_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25752,2,'','',10183,'所属计算节点','vm.host',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25753,2,'','',10183,'创建时间','vm.created',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25754,2,'','',10183,'固定IP','vm.fixedIps',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25755,2,'','',10183,'虚拟内核数','vm.vcpus',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25756,2,'','',10183,'内存容量','vm.ram',3600,30,90,0,3,'','B',1,0,'',0,'','','1048576','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25757,2,'','',10183,'磁盘容量','vm.disk',3600,30,90,0,3,'','B',1,0,'',0,'','','1073741824','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25759,7,'','',10183,'网络{#IFNAME}下行流量','net.if.in[{#IFNAME},]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25760,7,'','',10183,'网络{#IFNAME}上行流量','net.if.out[{#IFNAME},]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25761,0,'','',10182,'网络{#IFNAME}上行流量','net.if.out[{#IFNAME},]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25762,0,'','',10182,'网络{#IFNAME}下行流量','net.if.in[{#IFNAME},]',3600,30,90,0,3,'','B',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25763,0,'','',10182,'网络{#IFNAME}总流量','net.if.total[{#IFNAME},]',3600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25764,0,'','',10182,'网络{#IFNAME}总包数','net.if.total[{#IFNAME},packets]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25765,0,'','',10182,'网络{#IFNAME}错包数','net.if.total[{#IFNAME},errors]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25766,15,'','',10182,'网络{#IFNAME}错包率','net.if.total.[{#IFNAME},perrors]',3600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','100*last(\"net.if.total[{#IFNAME},errors]\", 14400)/last(\"net.if.total[{#IFNAME},packets]\", 2400)','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25767,4,'{$SNMP_COMMUNITY}','.1.3.6.1.2.1.2.2.1.16.{#SNMPINDEX}',10163,'网络$1上行流量','netOutBytes[{#SNMPVALUE}]',3600,30,90,0,0,'','B',1,0,'',0,'','','0.125','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25768,4,'{$SNMP_COMMUNITY}','.1.3.6.1.2.1.2.2.1.10.{#SNMPINDEX}',10163,'网络$1下行流量','netInBytes[{#SNMPVALUE}]',600,30,90,0,0,'','B',1,0,'',0,'','','0.125','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25769,15,'','',10163,'网络$1总流量','netTotalBytes[{#SNMPVALUE}]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','last(\"netOutBytes[{#SNMPVALUE}]\", 14400)+last(\"netInBytes[{#SNMPVALUE}]\", 2400)','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25770,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutUcastPkts.{#SNMPINDEX}',10163,'网络$1上行操作数','netOutPkts[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25771,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInErrors.{#SNMPINDEX}',10163,'网络$1下行错包数','netInErrors[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25772,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutErrors.{#SNMPINDEX}',10163,'网络$1上行错包数','netOutErrors[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25773,15,'','',10163,'网络$1错包率','netErrorsRate[{#SNMPVALUE}]',3600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','100*(last(\"netOutErrors[{#SNMPVALUE}]\",2400)+last(\"netInErrors[{#SNMPVALUE}]\",14400))/(last(\"netOutPkts[{#SNMPVALUE}]\",2400)+last(\"netInPkts[{#SNMPVALUE}]\",2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25775,0,'','',10144,'运行状态','mysql.alive[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',3,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25776,0,'','',10144,'表空间列表','mysql.databases[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',86400,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25777,0,'','',10179,'CPU平均使用率','mssql.cpu[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25778,0,'','',10179,'系统会话数','mssql.sessions[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25779,0,'','',10179,'内存使用量','mssql.memory[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25780,0,'','',10179,'SQL Agent状态','mssql.sql_agent_status[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25781,0,'','',10179,'日志空间利用率','mssql.log_used_percent[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25782,0,'','',10132,'运行时间','oracle.uptime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,3,'','uptime',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25783,0,'','',10132,'表空间利用率','oracle.tbl_use_rate[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25784,4,'{$SNMP_COMMUNITY}','IF-MIB::ifHighSpeed.{#SNMPINDEX}',10186,'$1 最大带宽','ifHighSpeed[{#SNMPVALUE}]',86400,30,90,0,3,'','b',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25785,2,'','',10157,'Cinder总大小','iaas.cinder.total',0,30,90,0,0,'','B',1,0,'',0,'','','1073741824','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25786,2,'','',10157,'Cinder可用大小','iaas.cinder.free',0,30,90,0,0,'','B',1,0,'',0,'','','1073741824','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25787,2,'','',10157,'Cinder已用大小','iaas.cinder.used',0,30,90,0,0,'','B',1,0,'',0,'','','1073741824','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25788,4,'{$SNMP_COMMUNITY}','IF-MIB::ifDescr',10187,'Network interfaces','ifDescr',3600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',25608,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','You may also consider using IF-MIB::ifType or IF-MIB::ifAlias for discovery depending on your filtering needs.\r\n\r\n{$SNMP_COMMUNITY} is a global macro.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25789,4,'{$SNMP_COMMUNITY}','IF-MIB::ifAdminStatus.{#SNMPINDEX}',10187,'$1 期望状态','ifAdminStatus[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',25617,11,'','','',0,0,'','','','',0,2,'',NULL,'','The desired state of the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25790,4,'{$SNMP_COMMUNITY}','IF-MIB::ifAlias.{#SNMPINDEX}',10187,'$1 接口别名','ifAlias[{#SNMPVALUE}]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25618,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25791,4,'{$SNMP_COMMUNITY}','IF-MIB::ifDescr.{#SNMPINDEX}',10187,'$1 接口描述','ifDescr[{#SNMPVALUE}]',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25619,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','A textual string containing information about the interface.  This string should include the name of the manufacturer, the product name and the version of the interface hardware/software.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25792,4,'{$SNMP_COMMUNITY}','IF-MIB::ifHighSpeed.{#SNMPINDEX}',10187,'$1 最大带宽','ifHighSpeed[{#SNMPVALUE}]',86400,30,90,0,3,'','b',1,0,'',0,'','','1000000','',0,'',25784,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25793,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInErrors.{#SNMPINDEX}',10187,'$1 下行错误包总数','ifInErrors[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',25620,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of inbound packets that contained errors preventing them from being deliverable to a higher-layer protocol.  For character-oriented or fixed-length interfaces, the number of inbound transmission units that contained errors preventing them from being deliverable to a higher-layer protocol.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25794,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInOctets.{#SNMPINDEX}',10187,'$1 下行速率','ifInOctets[{#SNMPVALUE}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',25621,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of octets in valid MAC frames received on this interface, including the MAC header and FCS.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25795,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOperStatus.{#SNMPINDEX}',10187,'$1 当前操作状态','ifOperStatus[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',25622,8,'','','',0,0,'','','','',0,2,'',NULL,'','The current operational state of the interface.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25796,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutErrors.{#SNMPINDEX}',10187,'$1 上行错误包总数','ifOutErrors[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',25623,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','For packet-oriented interfaces, the number of outbound packets that could not be transmitted because of errors. For character-oriented or fixed-length interfaces, the number of outbound transmission units that could not be transmitted because of errors.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25797,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutOctets.{#SNMPINDEX}',10187,'$1 上行速率','ifOutOctets[{#SNMPVALUE}]',600,30,90,0,3,'','Bps',1,1,'',0,'','','8','',0,'',25624,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of octets transmitted in MAC frames on this interface, including the MAC header and FCS.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25798,4,'{$SNMP_COMMUNITY}','IF-MIB::ifNumber.0',10187,'网络接口数目','ifNumber',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',25606,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The number of network interfaces (regardless of their current state) present on this system.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25799,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysContact.0',10187,'设备联系细节','sysContact',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25601,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The textual identification of the contact person for this managed node, together with information on how to contact this person.  If no contact information is known, the value is the zero-length string.',23,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25800,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysDescr.0',10187,'设备系统描述','sysDescr',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25602,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','A textual description of the entity.  This value should include the full name and version identification of the system\'s hardware type, software operating-system, and networking software.',14,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25801,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysLocation.0',10187,'设备位置','sysLocation',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25603,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The physical location of this node (e.g., `telephone closet, 3rd floor\').  If the location is unknown, the value is the zero-length string.',24,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25802,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysName.0',10187,'设备系统名称','sysName',86400,30,90,0,1,'','',0,0,'',0,'','','1','',0,'',25604,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','An administratively-assigned name for this managed node. By convention, this is the node\'s fully-qualified domain name.  If the name is unknown, the value is the zero-length string.',3,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25803,4,'{$SNMP_COMMUNITY}','SNMPv2-MIB::sysUpTime.0',10187,'设备运行时间','sysUpTime',60,30,90,0,3,'','uptime',1,0,'',0,'','','0.01','',0,'',25605,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The time since the network management portion of the system was last re-initialized.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25804,4,'{$SNMP_COMMUNITY}','1.3.6.1.4.1.2011.6.3.5.1.1.2.0.0.0',10187,'内存总量（字节）','snmp.memory.size[total]',86400,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25805,4,'{$SNMP_COMMUNITY}','1.3.6.1.4.1.2011.6.3.5.1.1.3.0.0.0',10187,'剩余内存（字节）','snmp.memory.size[free]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25806,4,'{$SNMP_COMMUNITY}','1.3.6.1.4.1.2011.6.3.4.1.2.0.0.0',10187,'CPU 利用率','snmp.cpu.Usage',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25807,4,'{$SNMP_COMMUNITY}','1.3.6.1.4.1.2011.6.3.4.1.3.0.0.0',10187,'最近一分钟平均利用率','snmp.cpu.Usage[all,avg1]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25808,4,'{$SNMP_COMMUNITY}','1.3.6.1.4.1.2011.6.3.4.1.4.0.0.0',10187,'最近五分钟平均利用率','snmp.cpu.Usage[all,avg5]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25809,0,'','',10172,'索引数','MongoDB.Index[count,{$MONGOIP}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25810,0,'','',10131,'日志大小','db2.TotalLogSpUsed[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',3600,30,90,0,0,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25811,0,'','',10131,'缓冲池命中率','db2.BpHitRatio[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25812,0,'','',10131,'系统会话数','db2.ApplCount[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25813,0,'','',10131,'排序时间','db2.SortTime[{$USER},{$PSWD},{$DBIP},{$PORT},{$JAVAHOME}]',600,30,90,0,0,'','ms',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25814,0,'','',10172,'数据库运行状态','MongoDB.status[{$MONGOIP}]',60,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,21,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25815,0,'','',10188,'启动时间','tomcat.startTime[0,{$JMX_PORT},{$JAVAHOME}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25816,0,'','',10188,'运行时间','tomcat.uptime[0,{$JMX_PORT},{$JAVAHOME}]',60,30,90,0,0,'','uptime',1,0,'',0,'','','0.001','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25817,0,'','',10188,'非堆最大使用量','tomcat.nonHeapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25818,0,'','',10188,'非堆当前使用量','tomcat.nonHeapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'50/1-7,00:00-24:00','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25819,0,'','',10188,'堆当前使用量','tomcat.heapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25820,0,'','',10188,'堆最大使用量','tomcat.heapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25823,0,'','',10188,'JVM活动线程总数','websphere.jvmThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25824,0,'','',10188,'JVM活动守护线程数','websphere.jvmDaemonThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25825,0,'','',10188,'JVM历史最大线程数','websphere.jvmPeakThreadCount[{$WEBSPHERE_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25826,0,'','',10189,'启动时间','tomcat.startTime[0,{$JMX_PORT},{$JAVAHOME}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25827,0,'','',10189,'运行时间','tomcat.uptime[0,{$JMX_PORT},{$JAVAHOME}]',60,30,90,0,0,'','uptime',1,0,'',0,'','','0.001','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25828,0,'','',10189,'非堆当前使用量','tomcat.nonHeapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25829,0,'','',10189,'非堆最大使用量','tomcat.nonHeapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25830,0,'','',10189,'持久堆当前使用量','tomcat.psPermGenUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25831,0,'','',10189,'持久堆最大使用量','tomcat.psPermGenUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25832,0,'','',10189,'堆当前使用量','tomcat.heapMemoryUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25833,0,'','',10189,'堆最大使用量','tomcat.heapMemoryUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25834,0,'','',10189,'AdminServer当前JMS服务数','weblogic.jmsServersCurrentCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25835,0,'','',10189,'AdminServer自启动以来最大JMS服务数','weblogic.jmsServersHighCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25836,0,'','',10189,'AdminServer当前JMS连接数','weblogic.jmsConnectionsCurrentCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25837,0,'','',10189,'AdminServer自启动以来JMS最大连接数','weblogic.jmsConnectionsHighCount[{$WEBLOGIC_PORT},{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25838,0,'','',10164,'ASP.Net会话数','perf_counter[\"\\ASP.NET Applications(__Total__)\\Sessions Total\"]',600,30,90,0,0,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25839,4,'{$SNMP_COMMUNITY}','CISCO-PROCESS-MIB::cpmCPUTotalPhysicalIndex',10161,'List CPU','snmp.agent[CISCO-PROCESS-MIB::cpmCPUTotalPhysicalIndex]',3600,90,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25840,4,'{$SNMP_COMMUNITY}','CISCO-PROCESS-MIB::cpmCPUTotalPhysicalIndex.{#SNMPINDEX}',10161,'CPU {#SNMPINDEX} 名称','snmp.agent[CISCO-PROCESS-MIB::cpmCPUTotalPhysicalIndex.{#SNMPINDEX}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The entPhysicalIndex of the physical entity for which\r\nthe CPU statistics in this entry are maintained.\r\nThe physical entity can be a CPU chip, a group of CPUs,\r\na CPU card etc. The exact type of this entity is described by\r\nits entPhysicalVendorType value. If the CPU statistics\r\nin this entry correspond to more than one physical entity\r\n(or to no physical entity), or if the entPhysicalTable is\r\nnot supported on the SNMP agent, the value of this object\r\nmust be zero.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25841,4,'{$SNMP_COMMUNITY}','OLD-CISCO-SYS-MIB::busyPer.0',10161,'CPU利用率','snmp.agent[OLD-CISCO-SYS-MIB::busyPer]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25842,15,'','',10161,'CPU平均利用率','cpuUtilAvg',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','avg(\"snmp.agent[OLD-CISCO-SYS-MIB::busyPer]\",3600)','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25843,4,'{$SNMP_COMMUNITY}','.1.3.6.1.4.1.2021.4.3.0',10163,'swap总大小','system.swap.size[,total]',600,30,90,0,0,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25844,4,'{$SNMP_COMMUNITY}','.1.3.6.1.4.1.2021.4.4.0',10163,'swap空闲大小','system.swap.size[,free]',600,30,90,0,0,'','B',1,0,'',0,'','','1024','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25845,0,'','',10182,'网络{#IFNAME}丢包数','net.if.total[{#IFNAME},dropped]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25846,15,'','',10182,'网络{#IFNAME}丢包率','net.if.total.[{#IFNAME},pdropped]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','100*last(\"net.if.total[{#IFNAME},dropped]\", 2400)/last(\"net.if.total[{#IFNAME},packets]\", 2400)','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25847,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::icmpInMsgs.0',10161,'接收的ICMP包数','snmp.agent[RFC1213-MIB::icmpInMsgs.0]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The total number of ICMP messages which the\r\nentity received.  Note that this counter includes\r\nall those counted by icmpInErrors.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25848,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::icmpOutMsgs.0',10161,'发送的ICMP包数','snmp.agent[RFC1213-MIB::icmpOutMsgs.0]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','The total number of ICMP messages which this\r\nentity attempted to send.  Note that this counter\r\nincludes all those counted by icmpOutErrors.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25849,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::icmpOutMsgs.0',10161,'发送的ICMP包率','snmp.agent[icmpOutMsgsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25850,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::icmpInMsgs.0',10161,'接收的ICMP包率','snmp.agent[icmpInMsgsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25851,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 单播包转发速率','snmp.agent[ifInUcastPktsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25852,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 单播包请求速率','snmp.agent[ifOutUcastPktsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25853,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutBroadcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 广播包请求速率','snmp.agent[ifOutBroadcastPktsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25854,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInBroadcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 广播包转发速率','snmp.agent[ifInBroadcastPktsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25855,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutMulticastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 组播包请求速率','snmp.agent[ifOutMulticastPktsRate]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25856,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInMulticastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 组播包转发速率','snmp.agent[ifInMulticastPkts]',3600,30,90,0,3,'','Pkts',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25857,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipNetToMediaIfIndex',10161,'ARP discovery','snmp.agent[RFC1213-MIB::ipNetToMediaIfIndex]',3600,90,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25860,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}',10161,'{#SNMPVALUE} 单播包请求包数','snmp.agent[RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,2,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The total number of packets that higher-level protocols requested be transmitted, and which were not addressed to a multicast or broadcast address at this sub-layer, including those that were discarded or not sent.\r\n\r\nDiscontinuities in the value of this counter can occur at re-initialization of the management system, and at other times as indicated by the value of ifCounterDiscontinuityTime.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25861,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipNetToMediaPhysAddress.{#SNMPINDEX}',10161,'ARP映射 {#SNMPINDEX} 物理地址','snmp.agent[RFC1213-MIB::ipNetToMediaPhysAddress.{#SNMPINDEX}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The media-dependent `physical\' address.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25862,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipNetToMediaNetAddress.{#SNMPINDEX}',10161,'ARP映射 {#SNMPINDEX} 网络地址','snmp.agent[RFC1213-MIB::ipNetToMediaNetAddress.{#SNMPINDEX}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The IpAddress corresponding to the media-\r\ndependent `physical\' address.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25863,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipNetToMediaType.{#SNMPINDEX}',10161,'ARP映射 {#SNMPINDEX} 类型','snmp.agent[RFC1213-MIB::ipNetToMediaType.{#SNMPINDEX}]',3600,30,365,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,27,'','','',0,0,'','','','',0,2,'',NULL,'','The type of mapping.\r\nSetting this object to the value invalid(2) has\r\nthe effect of invalidating the corresponding entry\r\nin the ipNetToMediaTable.  That is, it effectively\r\ndissasociates the interface identified with said\r\nentry from the mapping identified with said entry.\r\nIt is an implementation-specific matter as to\r\nwhether the agent removes an invalidated entry\r\nfrom the table.  Accordingly, management stations\r\nmust be prepared to receive tabular information\r\nfrom agents that corresponds to entries not\r\ncurrently in use.  Proper interpretation of such\r\nentries requires examination of the relevant\r\nipNetToMediaType object.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25864,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipRouteIfIndex',10161,'Route discovery','snmp.agent[RFC1213-MIB::ipRouteIfIndex]',3600,90,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,1,':',NULL,'','The index value which uniquely identifies the\r\nlocal interface through which the next hop of this\r\nroute should be reached.  The interface identified\r\nby a particular value of this index is the same\r\ninterface as identified by the same value of\r\nifIndex',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25865,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipRouteDest.{#SNMPINDEX}',10161,'Route {#SNMPINDEX} 目标IP','snmp.agent[RFC1213-MIB::ipRouteDest.{#SNMPINDEX}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The destination IP address of this route.  An\r\nentry with a value of 0.0.0.0 is considered a\r\ndefault route.  Multiple routes to a single\r\ndestination can appear in the table, but access to\r\nsuch multiple entries is dependent on the table-\r\naccess mechanisms defined by the network\r\nmanagement protocol in use.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25866,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipRouteAge.{#SNMPINDEX}',10161,'Route {#SNMPINDEX} 更新时长','snmp.agent[RFC1213-MIB::ipRouteAge.{#SNMPINDEX}]',3600,30,90,0,3,'','s',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The number of seconds since this route was last\r\nupdated or otherwise determined to be correct.\r\nNote that no semantics of `too old\' can be implied\r\nexcept through knowledge of the routing protocol\r\nby which the route was learned.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25867,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipRouteType.{#SNMPINDEX}',10161,'Route {#SNMPINDEX} 类型','snmp.agent[RFC1213-MIB::ipRouteType.{#SNMPINDEX}]',3600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,26,'','','',0,0,'','','','',0,2,'',NULL,'','The type of route.  Note that the values\r\ndirect(3) and indirect(4) refer to the notion of\r\ndirect and indirect routing in the IP\r\narchitecture.\r\nSetting this object to the value invalid(2) has\r\nthe effect of invalidating the corresponding entry\r\nin the ipRouteTable object.  That is, it\r\neffectively dissasociates the destination\r\nidentified with said entry from the route\r\nidentified with said entry.  It is an\r\nimplementation-specific matter as to whether the\r\nagent removes an invalidated entry from the table.\r\nAccordingly, management stations must be prepared\r\nto receive tabular information from agents that\r\ncorresponds to entries not currently in use.\r\nProper interpretation of such entries requires\r\nexamination of the relevant ipRouteType object.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25868,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ipRouteInfo.{#SNMPINDEX}',10161,'Route {#SNMPINDEX} 信息','snmp.agent[RFC1213-MIB::ipRouteInfo.{#SNMPINDEX}]',3600,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','A reference to MIB definitions specific to the\r\nparticular routing protocol which is responsible\r\nfor this route, as determined by the value\r\nspecified in the route\'s ipRouteProto value.  If\r\nthis information is not present, its value should\r\nbe set to the OBJECT IDENTIFIER {0 0 }, which is\r\na syntatically valid object identifier, and any\r\nconformant implementation of ASN.1 and BER must be\r\nable to generate and recognize this value.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25869,0,'','',10188,'JVM堆当前使用量','websphere.jvmHeapUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25870,0,'','',10188,'JVM堆最大使用量','websphere.jvmHeapUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25871,0,'','',10188,'杂项非堆当前使用量','websphere.miscellaneousNoneHeapUsageUsed[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25872,0,'','',10188,'杂项非堆最大使用量','websphere.miscellaneousNoneHeapUsageMax[0,{$JMX_PORT},{$JAVAHOME}]',600,30,90,0,3,'','B',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25873,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutDiscards.{#SNMPINDEX}',10186,'$1 上行丢包总数','ifOutDiscards[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25874,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutDiscards.{#SNMPINDEX}',10187,'$1 上行丢包总数','ifOutDiscards[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',25873,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25875,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInDiscards.{#SNMPINDEX}',10186,'$1 下行丢包总数','ifInDiscards[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25876,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInDiscards.{#SNMPINDEX}',10187,'$1 下行丢包总数','ifInDiscards[{#SNMPVALUE}]',3600,30,90,0,3,'','',0,1,'',0,'','','1','',0,'',25875,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25877,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInUcastPkts.{#SNMPINDEX}',10163,'网络$1下行操作数','netInPkts[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25878,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::sysName.0',10161,'设备名称','snmp.agent[RFC1213-MIB::sysName.0]',86400,30,365,0,1,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','An administratively-assigned name for this\r\nmanaged node.  By convention, this is the node\'s\r\nfully-qualified domain name.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25879,15,'','',10161,'{#SNMPVALUE} 内存池总容量','snmp.agent[memoryPoolTotal.{#SNMPINDEX}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,23,'','(last(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]\")+last(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolFree.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]\", 2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25880,15,'','',10161,'{#SNMPVALUE} 内存池利用率','snmp.agent[memoryPoolUsage.{#SNMPINDEX}]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,23,'','((last(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]\")/(last(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]\")+last(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolFree.{#SNMPINDEX}]\")))*100)/(1-nodata(\"snmp.agent[CISCO-MEMORY-POOL-MIB::ciscoMemoryPoolUsed.{#SNMPINDEX}]\", 2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25881,15,'','',10161,'{#SNMPVALUE} 接收丢包率','snmp.agent[ifInDiscardsPercent.{#SNMPINDEX}]',3600,30,90,0,3,'','%',0,2,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"snmp.agent[RFC1213-MIB::ifInDiscards.{#SNMPINDEX}]\")/(last(\"snmp.agent[RFC1213-MIB::ifInDiscards.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifInErrors.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifInUnknownProtos.{#SNMPINDEX}]\")))*100)/(1-nodata(\"snmp.agent[RFC1213-MIB::ifInDiscards.{#SNMPINDEX}]\", 14400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25882,15,'','',10161,'{#SNMPVALUE} 发送丢包率','snmp.agent[ifOutDiscardsPercent.{#SNMPINDEX}]',3600,30,90,0,3,'','%',0,2,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"snmp.agent[RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}]\")/(last(\"snmp.agent[RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifOutErrors.{#SNMPINDEX}]\")))*100)/(1-nodata(\"snmp.agent[RFC1213-MIB::ifOutDiscards.{#SNMPINDEX}]\", 14400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25883,4,'{$SNMP_COMMUNITY}','RFC1213-MIB::ifIndex.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接口ID号','snmp.agent[RFC1213-MIB::ifIndex.{#SNMPINDEX}]',86400,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','A unique value for each interface.  Its value\r\nranges between 1 and the value of ifNumber.  The\r\nvalue for each interface must remain constant at\r\nleast from one re-initialization of the entity\'s\r\nnetwork management system to the next re-\r\ninitialization.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25884,4,'{$SNMP_COMMUNITY}','IF-MIB::ifName.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接口名称','snmp.agent[IF-MIB::ifName.{#SNMPINDEX}]',86400,30,365,0,4,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','The textual name of the interface.  The value of this\r\nobject should be the name of the interface as assigned by\r\nthe local device and should be suitable for use in commands\r\nentered at the device\'s `console\'.  This might be a text\r\nname, such as `le0\' or a simple port number, such as `1\',\r\ndepending on the interface naming syntax of the device.  If\r\nseveral entries in the ifTable together represent a single\r\ninterface as named by the device, then each will have the\r\nsame value of ifName.  Note that for an agent which responds\r\nto SNMP queries concerning an interface on some other\r\n(proxied) device, then the value of ifName for such an\r\ninterface is the proxied device\'s local name for it.\r\nIf there is no local name, or this object is otherwise not\r\napplicable, then this object contains a zero-length string.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25885,4,'{$SNMP_COMMUNITY}','IF-MIB::ifSpeed.{#SNMPINDEX}',10161,'{#SNMPVALUE} 接口速率','snmp.agent[IF-MIB::ifSpeed.{#SNMPINDEX}]',600,30,90,0,3,'','b',1,0,'',0,'','','1000000','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','An estimate of the interface\'s current bandwidth\r\nin bits per second.  For interfaces which do not\r\nvary in bandwidth or for those where no accurate\r\nestimation can be made, this object should contain\r\nthe nominal bandwidth.',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25886,15,'','',10161,'{#SNMPVALUE} 接收利用率','snmp.agent[ifInUsage.{#SNMPINDEX}]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]\")*8*100)/last(\"snmp.agent[IF-MIB::ifSpeed.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]\", 2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25887,15,'','',10161,'{#SNMPVALUE} 发送利用率','snmp.agent[ifOutUsage.{#SNMPINDEX}]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','((last(\"snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]\")*8*100)/last(\"snmp.agent[IF-MIB::ifSpeed.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]\", 2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25888,15,'','',10161,'{#SNMPVALUE} 利用率总和','snmp.agent[ifInOutUsage.{#SNMPINDEX}]',600,30,90,0,3,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(((last(\"snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]\")+last(\"snmp.agent[RFC1213-MIB::ifOutOctets.{#SNMPINDEX}]\"))*8*100)/last(\"snmp.agent[IF-MIB::ifSpeed.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[RFC1213-MIB::ifInOctets.{#SNMPINDEX}]\", 2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25889,15,'','',10161,'{#SNMPVALUE} 请求包总数','snmp.agent[ifOutPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"snmp.agent[RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[IF-MIB::ifOutBroadcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[IF-MIB::ifOutMulticastPkts.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[RFC1213-MIB::ifOutUcastPkts.{#SNMPINDEX}]\", 14400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25890,15,'','',10161,'{#SNMPVALUE} 转发包总数','snmp.agent[ifInPkts.{#SNMPINDEX}]',3600,30,90,0,3,'','Pkts',0,0,'',0,'','','1','',0,'',NULL,NULL,'','(last(\"snmp.agent[RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[IF-MIB::ifInBroadcastPkts.{#SNMPINDEX}]\")+last(\"snmp.agent[IF-MIB::ifInMulticastPkts.{#SNMPINDEX}]\"))/(1-nodata(\"snmp.agent[RFC1213-MIB::ifInUcastPkts.{#SNMPINDEX}]\", 14400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25891,0,'','',10182,'CPU个数','system.cpu.num',86400,30,90,0,3,'','个',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25892,7,'','',10183,'HTTP服务状态','net.tcp.listen[80]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25893,4,'{$SNMP_COMMUNITY}','IF-MIB::ifOutDiscards.{#SNMPINDEX}',10163,'网络$1上行丢包数','netOutDiscards[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25894,4,'{$SNMP_COMMUNITY}','IF-MIB::ifInDiscards.{#SNMPINDEX}',10163,'网络$1下行丢包数','netInDiscards[{#SNMPVALUE}]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25895,15,'','',10163,'网络$1丢包率','netDiscardsRate[{#SNMPVALUE}]',3600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','100*(last(\"netOutDiscards[{#SNMPVALUE}]\",2400)+last(\"netInDiscards[{#SNMPVALUE}]\",2400))/(last(\"netOutPkts[{#SNMPVALUE}]\",2400)+last(\"netInPkts[{#SNMPVALUE}]\",2400))','',0,0,'','','','',0,2,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25898,7,'','',10183,'NTP服务状态_Windows','service_state[Windows Time]',600,30,90,0,3,'','',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,''),('5e4d0a6d39a44b9c906a3173b448aa4a',25899,7,'','',10183,'swap使用率_Windows','perf_counter[\"\\Memory\\% Committed Bytes In Use\"]',600,30,90,0,0,'','%',0,0,'',0,'','','1','',0,'',NULL,NULL,'','','',0,0,'','','','',0,0,'',NULL,'','',0,'30',0,0,0,'');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items_applications`
--

DROP TABLE IF EXISTS `items_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items_applications` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemappid` bigint(20) unsigned NOT NULL,
  `applicationid` bigint(20) unsigned NOT NULL,
  `itemid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`itemappid`),
  UNIQUE KEY `items_applications_1` (`applicationid`,`itemid`),
  KEY `items_applications_2` (`itemid`),
  CONSTRAINT `c_items_applications_1` FOREIGN KEY (`applicationid`) REFERENCES `applications` (`applicationid`) ON DELETE CASCADE,
  CONSTRAINT `c_items_applications_2` FOREIGN KEY (`itemid`) REFERENCES `items` (`itemid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items_applications`
--

LOCK TABLES `items_applications` WRITE;
/*!40000 ALTER TABLE `items_applications` DISABLE KEYS */;
INSERT INTO `items_applications` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',9270,508,25443),('5e4d0a6d39a44b9c906a3173b448aa4a',9271,508,23949),('5e4d0a6d39a44b9c906a3173b448aa4a',9272,508,25444),('5e4d0a6d39a44b9c906a3173b448aa4a',9273,681,23951),('5e4d0a6d39a44b9c906a3173b448aa4a',9274,679,23953),('5e4d0a6d39a44b9c906a3173b448aa4a',9275,679,23954),('5e4d0a6d39a44b9c906a3173b448aa4a',9276,679,23955),('5e4d0a6d39a44b9c906a3173b448aa4a',9277,679,23956),('5e4d0a6d39a44b9c906a3173b448aa4a',9278,679,23957),('5e4d0a6d39a44b9c906a3173b448aa4a',9279,679,23958),('5e4d0a6d39a44b9c906a3173b448aa4a',9280,679,23959),('5e4d0a6d39a44b9c906a3173b448aa4a',9281,679,23960),('5e4d0a6d39a44b9c906a3173b448aa4a',9282,679,23961),('5e4d0a6d39a44b9c906a3173b448aa4a',9283,679,23962),('5e4d0a6d39a44b9c906a3173b448aa4a',9284,679,23963),('5e4d0a6d39a44b9c906a3173b448aa4a',9285,679,23964),('5e4d0a6d39a44b9c906a3173b448aa4a',9286,679,23965),('5e4d0a6d39a44b9c906a3173b448aa4a',9287,679,23966),('5e4d0a6d39a44b9c906a3173b448aa4a',9288,679,23967),('5e4d0a6d39a44b9c906a3173b448aa4a',9289,679,23969),('5e4d0a6d39a44b9c906a3173b448aa4a',9290,679,23971),('5e4d0a6d39a44b9c906a3173b448aa4a',9291,679,23970),('5e4d0a6d39a44b9c906a3173b448aa4a',9292,679,23968),('5e4d0a6d39a44b9c906a3173b448aa4a',9293,680,23974),('5e4d0a6d39a44b9c906a3173b448aa4a',9294,680,23972),('5e4d0a6d39a44b9c906a3173b448aa4a',9295,680,23973),('5e4d0a6d39a44b9c906a3173b448aa4a',9296,681,23952),('5e4d0a6d39a44b9c906a3173b448aa4a',9297,682,23976),('5e4d0a6d39a44b9c906a3173b448aa4a',9298,682,23950),('5e4d0a6d39a44b9c906a3173b448aa4a',9299,682,23975),('5e4d0a6d39a44b9c906a3173b448aa4a',9377,743,25532),('5e4d0a6d39a44b9c906a3173b448aa4a',9379,743,25504),('5e4d0a6d39a44b9c906a3173b448aa4a',9382,745,25512),('5e4d0a6d39a44b9c906a3173b448aa4a',9383,745,25510),('5e4d0a6d39a44b9c906a3173b448aa4a',9385,749,25527),('5e4d0a6d39a44b9c906a3173b448aa4a',9386,746,25522),('5e4d0a6d39a44b9c906a3173b448aa4a',9387,746,25521),('5e4d0a6d39a44b9c906a3173b448aa4a',9388,746,25523),('5e4d0a6d39a44b9c906a3173b448aa4a',9395,749,25508),('5e4d0a6d39a44b9c906a3173b448aa4a',9396,749,25513),('5e4d0a6d39a44b9c906a3173b448aa4a',9397,749,25514),('5e4d0a6d39a44b9c906a3173b448aa4a',9398,751,25503),('5e4d0a6d39a44b9c906a3173b448aa4a',9399,747,25542),('5e4d0a6d39a44b9c906a3173b448aa4a',9400,747,25541),('5e4d0a6d39a44b9c906a3173b448aa4a',9401,747,25540),('5e4d0a6d39a44b9c906a3173b448aa4a',9402,747,25539),('5e4d0a6d39a44b9c906a3173b448aa4a',9403,750,25537),('5e4d0a6d39a44b9c906a3173b448aa4a',9404,750,25761),('5e4d0a6d39a44b9c906a3173b448aa4a',9405,750,25536),('5e4d0a6d39a44b9c906a3173b448aa4a',9406,750,25535),('5e4d0a6d39a44b9c906a3173b448aa4a',9407,750,25762),('5e4d0a6d39a44b9c906a3173b448aa4a',9408,750,25534),('5e4d0a6d39a44b9c906a3173b448aa4a',9409,750,25763),('5e4d0a6d39a44b9c906a3173b448aa4a',9411,750,25765),('5e4d0a6d39a44b9c906a3173b448aa4a',9414,661,25097),('5e4d0a6d39a44b9c906a3173b448aa4a',9415,665,25101),('5e4d0a6d39a44b9c906a3173b448aa4a',9416,665,25099),('5e4d0a6d39a44b9c906a3173b448aa4a',9417,665,25100),('5e4d0a6d39a44b9c906a3173b448aa4a',9420,664,25112),('5e4d0a6d39a44b9c906a3173b448aa4a',9421,664,25110),('5e4d0a6d39a44b9c906a3173b448aa4a',9423,666,25117),('5e4d0a6d39a44b9c906a3173b448aa4a',9424,666,25118),('5e4d0a6d39a44b9c906a3173b448aa4a',9425,666,25770),('5e4d0a6d39a44b9c906a3173b448aa4a',9426,666,25767),('5e4d0a6d39a44b9c906a3173b448aa4a',9427,666,25116),('5e4d0a6d39a44b9c906a3173b448aa4a',9428,666,25772),('5e4d0a6d39a44b9c906a3173b448aa4a',9429,666,25114),('5e4d0a6d39a44b9c906a3173b448aa4a',9430,666,25115),('5e4d0a6d39a44b9c906a3173b448aa4a',9431,666,25768),('5e4d0a6d39a44b9c906a3173b448aa4a',9432,666,25113),('5e4d0a6d39a44b9c906a3173b448aa4a',9445,663,25128),('5e4d0a6d39a44b9c906a3173b448aa4a',9446,663,25129),('5e4d0a6d39a44b9c906a3173b448aa4a',9453,640,25030),('5e4d0a6d39a44b9c906a3173b448aa4a',9454,641,25031),('5e4d0a6d39a44b9c906a3173b448aa4a',9455,641,25032),('5e4d0a6d39a44b9c906a3173b448aa4a',9456,641,25029),('5e4d0a6d39a44b9c906a3173b448aa4a',9457,641,25034),('5e4d0a6d39a44b9c906a3173b448aa4a',9458,641,25033),('5e4d0a6d39a44b9c906a3173b448aa4a',9466,646,25046),('5e4d0a6d39a44b9c906a3173b448aa4a',9468,649,25038),('5e4d0a6d39a44b9c906a3173b448aa4a',9469,649,25037),('5e4d0a6d39a44b9c906a3173b448aa4a',9470,646,25053),('5e4d0a6d39a44b9c906a3173b448aa4a',9471,643,25041),('5e4d0a6d39a44b9c906a3173b448aa4a',9472,644,25055),('5e4d0a6d39a44b9c906a3173b448aa4a',9473,647,25057),('5e4d0a6d39a44b9c906a3173b448aa4a',9475,645,25050),('5e4d0a6d39a44b9c906a3173b448aa4a',9476,646,25042),('5e4d0a6d39a44b9c906a3173b448aa4a',9477,650,25044),('5e4d0a6d39a44b9c906a3173b448aa4a',9478,646,25056),('5e4d0a6d39a44b9c906a3173b448aa4a',9479,646,25054),('5e4d0a6d39a44b9c906a3173b448aa4a',9480,652,25052),('5e4d0a6d39a44b9c906a3173b448aa4a',9481,652,25051),('5e4d0a6d39a44b9c906a3173b448aa4a',9483,654,25061),('5e4d0a6d39a44b9c906a3173b448aa4a',9484,654,25060),('5e4d0a6d39a44b9c906a3173b448aa4a',9485,765,25606),('5e4d0a6d39a44b9c906a3173b448aa4a',9486,764,25603),('5e4d0a6d39a44b9c906a3173b448aa4a',9487,764,25604),('5e4d0a6d39a44b9c906a3173b448aa4a',9488,764,25602),('5e4d0a6d39a44b9c906a3173b448aa4a',9489,764,25601),('5e4d0a6d39a44b9c906a3173b448aa4a',9490,764,25605),('5e4d0a6d39a44b9c906a3173b448aa4a',9491,765,25623),('5e4d0a6d39a44b9c906a3173b448aa4a',9492,765,25620),('5e4d0a6d39a44b9c906a3173b448aa4a',9494,765,25622),('5e4d0a6d39a44b9c906a3173b448aa4a',9495,765,25618),('5e4d0a6d39a44b9c906a3173b448aa4a',9496,765,25619),('5e4d0a6d39a44b9c906a3173b448aa4a',9498,765,25617),('5e4d0a6d39a44b9c906a3173b448aa4a',9520,724,25396),('5e4d0a6d39a44b9c906a3173b448aa4a',9521,724,25397),('5e4d0a6d39a44b9c906a3173b448aa4a',9527,724,25401),('5e4d0a6d39a44b9c906a3173b448aa4a',9528,724,25403),('5e4d0a6d39a44b9c906a3173b448aa4a',9529,724,25404),('5e4d0a6d39a44b9c906a3173b448aa4a',9530,724,25405),('5e4d0a6d39a44b9c906a3173b448aa4a',9536,724,25393),('5e4d0a6d39a44b9c906a3173b448aa4a',9537,724,25392),('5e4d0a6d39a44b9c906a3173b448aa4a',9538,724,25394),('5e4d0a6d39a44b9c906a3173b448aa4a',9539,724,25391),('5e4d0a6d39a44b9c906a3173b448aa4a',9730,752,25545),('5e4d0a6d39a44b9c906a3173b448aa4a',9731,752,25546),('5e4d0a6d39a44b9c906a3173b448aa4a',9732,752,25547),('5e4d0a6d39a44b9c906a3173b448aa4a',9733,752,25543),('5e4d0a6d39a44b9c906a3173b448aa4a',9734,752,25548),('5e4d0a6d39a44b9c906a3173b448aa4a',9735,758,25549),('5e4d0a6d39a44b9c906a3173b448aa4a',9736,758,25550),('5e4d0a6d39a44b9c906a3173b448aa4a',9737,758,25551),('5e4d0a6d39a44b9c906a3173b448aa4a',9738,758,25552),('5e4d0a6d39a44b9c906a3173b448aa4a',9739,758,25553),('5e4d0a6d39a44b9c906a3173b448aa4a',9740,758,25554),('5e4d0a6d39a44b9c906a3173b448aa4a',9741,758,25555),('5e4d0a6d39a44b9c906a3173b448aa4a',9742,761,25558),('5e4d0a6d39a44b9c906a3173b448aa4a',9743,758,25561),('5e4d0a6d39a44b9c906a3173b448aa4a',9744,754,25564),('5e4d0a6d39a44b9c906a3173b448aa4a',9745,754,25563),('5e4d0a6d39a44b9c906a3173b448aa4a',9746,754,25562),('5e4d0a6d39a44b9c906a3173b448aa4a',9747,761,25560),('5e4d0a6d39a44b9c906a3173b448aa4a',9748,753,25756),('5e4d0a6d39a44b9c906a3173b448aa4a',9749,753,25753),('5e4d0a6d39a44b9c906a3173b448aa4a',9750,753,25754),('5e4d0a6d39a44b9c906a3173b448aa4a',9751,753,25565),('5e4d0a6d39a44b9c906a3173b448aa4a',9752,753,25566),('5e4d0a6d39a44b9c906a3173b448aa4a',9753,753,25752),('5e4d0a6d39a44b9c906a3173b448aa4a',9754,759,25567),('5e4d0a6d39a44b9c906a3173b448aa4a',9755,759,25568),('5e4d0a6d39a44b9c906a3173b448aa4a',9756,753,25569),('5e4d0a6d39a44b9c906a3173b448aa4a',9757,755,25570),('5e4d0a6d39a44b9c906a3173b448aa4a',9758,755,25571),('5e4d0a6d39a44b9c906a3173b448aa4a',9759,755,25572),('5e4d0a6d39a44b9c906a3173b448aa4a',9760,753,25573),('5e4d0a6d39a44b9c906a3173b448aa4a',9761,761,25559),('5e4d0a6d39a44b9c906a3173b448aa4a',9762,757,25574),('5e4d0a6d39a44b9c906a3173b448aa4a',9764,757,25576),('5e4d0a6d39a44b9c906a3173b448aa4a',9766,757,25578),('5e4d0a6d39a44b9c906a3173b448aa4a',9768,757,25580),('5e4d0a6d39a44b9c906a3173b448aa4a',9770,753,25757),('5e4d0a6d39a44b9c906a3173b448aa4a',9771,759,25582),('5e4d0a6d39a44b9c906a3173b448aa4a',9772,759,25583),('5e4d0a6d39a44b9c906a3173b448aa4a',9773,759,25584),('5e4d0a6d39a44b9c906a3173b448aa4a',9774,753,25755),('5e4d0a6d39a44b9c906a3173b448aa4a',9775,761,25556),('5e4d0a6d39a44b9c906a3173b448aa4a',9776,756,25587),('5e4d0a6d39a44b9c906a3173b448aa4a',9777,756,25588),('5e4d0a6d39a44b9c906a3173b448aa4a',9778,756,25589),('5e4d0a6d39a44b9c906a3173b448aa4a',9779,756,25590),('5e4d0a6d39a44b9c906a3173b448aa4a',9780,760,25591),('5e4d0a6d39a44b9c906a3173b448aa4a',9781,760,25760),('5e4d0a6d39a44b9c906a3173b448aa4a',9782,760,25592),('5e4d0a6d39a44b9c906a3173b448aa4a',9783,760,25593),('5e4d0a6d39a44b9c906a3173b448aa4a',9784,760,25759),('5e4d0a6d39a44b9c906a3173b448aa4a',9785,760,25594),('5e4d0a6d39a44b9c906a3173b448aa4a',9794,662,25126),('5e4d0a6d39a44b9c906a3173b448aa4a',9795,662,25124),('5e4d0a6d39a44b9c906a3173b448aa4a',9796,662,25121),('5e4d0a6d39a44b9c906a3173b448aa4a',9798,663,25127),('5e4d0a6d39a44b9c906a3173b448aa4a',9799,663,25130),('5e4d0a6d39a44b9c906a3173b448aa4a',9800,663,25133),('5e4d0a6d39a44b9c906a3173b448aa4a',9801,663,25135),('5e4d0a6d39a44b9c906a3173b448aa4a',9890,648,25039),('5e4d0a6d39a44b9c906a3173b448aa4a',9891,648,25040),('5e4d0a6d39a44b9c906a3173b448aa4a',9895,765,25624),('5e4d0a6d39a44b9c906a3173b448aa4a',9896,765,25621),('5e4d0a6d39a44b9c906a3173b448aa4a',9902,642,25049),('5e4d0a6d39a44b9c906a3173b448aa4a',9903,765,25784),('5e4d0a6d39a44b9c906a3173b448aa4a',10085,731,25777),('5e4d0a6d39a44b9c906a3173b448aa4a',10086,731,25433),('5e4d0a6d39a44b9c906a3173b448aa4a',10087,731,25780),('5e4d0a6d39a44b9c906a3173b448aa4a',10088,731,25779),('5e4d0a6d39a44b9c906a3173b448aa4a',10089,731,25437),('5e4d0a6d39a44b9c906a3173b448aa4a',10090,731,25432),('5e4d0a6d39a44b9c906a3173b448aa4a',10091,731,25435),('5e4d0a6d39a44b9c906a3173b448aa4a',10092,731,25434),('5e4d0a6d39a44b9c906a3173b448aa4a',10093,731,25781),('5e4d0a6d39a44b9c906a3173b448aa4a',10094,731,25778),('5e4d0a6d39a44b9c906a3173b448aa4a',10095,731,25431),('5e4d0a6d39a44b9c906a3173b448aa4a',10096,731,25436),('5e4d0a6d39a44b9c906a3173b448aa4a',10098,678,24535),('5e4d0a6d39a44b9c906a3173b448aa4a',10099,678,24539),('5e4d0a6d39a44b9c906a3173b448aa4a',10100,678,24512),('5e4d0a6d39a44b9c906a3173b448aa4a',10101,678,24519),('5e4d0a6d39a44b9c906a3173b448aa4a',10102,678,24521),('5e4d0a6d39a44b9c906a3173b448aa4a',10103,678,24533),('5e4d0a6d39a44b9c906a3173b448aa4a',10104,678,25776),('5e4d0a6d39a44b9c906a3173b448aa4a',10105,678,24537),('5e4d0a6d39a44b9c906a3173b448aa4a',10106,678,25775),('5e4d0a6d39a44b9c906a3173b448aa4a',10107,688,24174),('5e4d0a6d39a44b9c906a3173b448aa4a',10108,693,24189),('5e4d0a6d39a44b9c906a3173b448aa4a',10109,688,24175),('5e4d0a6d39a44b9c906a3173b448aa4a',10110,684,24197),('5e4d0a6d39a44b9c906a3173b448aa4a',10111,684,24198),('5e4d0a6d39a44b9c906a3173b448aa4a',10112,684,24200),('5e4d0a6d39a44b9c906a3173b448aa4a',10113,684,24196),('5e4d0a6d39a44b9c906a3173b448aa4a',10114,684,24199),('5e4d0a6d39a44b9c906a3173b448aa4a',10115,684,24195),('5e4d0a6d39a44b9c906a3173b448aa4a',10116,683,24213),('5e4d0a6d39a44b9c906a3173b448aa4a',10117,687,24162),('5e4d0a6d39a44b9c906a3173b448aa4a',10118,693,24153),('5e4d0a6d39a44b9c906a3173b448aa4a',10119,693,24202),('5e4d0a6d39a44b9c906a3173b448aa4a',10120,683,24211),('5e4d0a6d39a44b9c906a3173b448aa4a',10121,687,24161),('5e4d0a6d39a44b9c906a3173b448aa4a',10122,683,24212),('5e4d0a6d39a44b9c906a3173b448aa4a',10123,693,24190),('5e4d0a6d39a44b9c906a3173b448aa4a',10124,683,24210),('5e4d0a6d39a44b9c906a3173b448aa4a',10126,686,24183),('5e4d0a6d39a44b9c906a3173b448aa4a',10127,693,24150),('5e4d0a6d39a44b9c906a3173b448aa4a',10128,687,24168),('5e4d0a6d39a44b9c906a3173b448aa4a',10129,687,24179),('5e4d0a6d39a44b9c906a3173b448aa4a',10130,687,24180),('5e4d0a6d39a44b9c906a3173b448aa4a',10131,687,24181),('5e4d0a6d39a44b9c906a3173b448aa4a',10132,687,24182),('5e4d0a6d39a44b9c906a3173b448aa4a',10133,686,24185),('5e4d0a6d39a44b9c906a3173b448aa4a',10134,691,24149),('5e4d0a6d39a44b9c906a3173b448aa4a',10135,691,24148),('5e4d0a6d39a44b9c906a3173b448aa4a',10136,685,24193),('5e4d0a6d39a44b9c906a3173b448aa4a',10137,693,24188),('5e4d0a6d39a44b9c906a3173b448aa4a',10138,683,24205),('5e4d0a6d39a44b9c906a3173b448aa4a',10139,693,24159),('5e4d0a6d39a44b9c906a3173b448aa4a',10140,693,24169),('5e4d0a6d39a44b9c906a3173b448aa4a',10141,693,24173),('5e4d0a6d39a44b9c906a3173b448aa4a',10142,683,24207),('5e4d0a6d39a44b9c906a3173b448aa4a',10143,683,24209),('5e4d0a6d39a44b9c906a3173b448aa4a',10144,693,24171),('5e4d0a6d39a44b9c906a3173b448aa4a',10145,693,24170),('5e4d0a6d39a44b9c906a3173b448aa4a',10146,685,24191),('5e4d0a6d39a44b9c906a3173b448aa4a',10150,693,24157),('5e4d0a6d39a44b9c906a3173b448aa4a',10151,692,24203),('5e4d0a6d39a44b9c906a3173b448aa4a',10152,683,24206),('5e4d0a6d39a44b9c906a3173b448aa4a',10153,683,24208),('5e4d0a6d39a44b9c906a3173b448aa4a',10154,685,24194),('5e4d0a6d39a44b9c906a3173b448aa4a',10155,687,24151),('5e4d0a6d39a44b9c906a3173b448aa4a',10156,686,24187),('5e4d0a6d39a44b9c906a3173b448aa4a',10157,686,24186),('5e4d0a6d39a44b9c906a3173b448aa4a',10158,686,24184),('5e4d0a6d39a44b9c906a3173b448aa4a',10159,687,24163),('5e4d0a6d39a44b9c906a3173b448aa4a',10160,693,24201),('5e4d0a6d39a44b9c906a3173b448aa4a',10161,693,25783),('5e4d0a6d39a44b9c906a3173b448aa4a',10162,692,24204),('5e4d0a6d39a44b9c906a3173b448aa4a',10163,687,24164),('5e4d0a6d39a44b9c906a3173b448aa4a',10169,687,24156),('5e4d0a6d39a44b9c906a3173b448aa4a',10170,693,24172),('5e4d0a6d39a44b9c906a3173b448aa4a',10171,693,24152),('5e4d0a6d39a44b9c906a3173b448aa4a',10172,685,24192),('5e4d0a6d39a44b9c906a3173b448aa4a',10189,687,24160),('5e4d0a6d39a44b9c906a3173b448aa4a',10190,727,25415),('5e4d0a6d39a44b9c906a3173b448aa4a',10191,727,25695),('5e4d0a6d39a44b9c906a3173b448aa4a',10192,727,25416),('5e4d0a6d39a44b9c906a3173b448aa4a',10193,727,25418),('5e4d0a6d39a44b9c906a3173b448aa4a',10194,727,25692),('5e4d0a6d39a44b9c906a3173b448aa4a',10195,727,25417),('5e4d0a6d39a44b9c906a3173b448aa4a',10196,727,25693),('5e4d0a6d39a44b9c906a3173b448aa4a',10198,727,25694),('5e4d0a6d39a44b9c906a3173b448aa4a',10199,774,24965),('5e4d0a6d39a44b9c906a3173b448aa4a',10200,774,24966),('5e4d0a6d39a44b9c906a3173b448aa4a',10201,774,24967),('5e4d0a6d39a44b9c906a3173b448aa4a',10202,774,25439),('5e4d0a6d39a44b9c906a3173b448aa4a',10203,774,24968),('5e4d0a6d39a44b9c906a3173b448aa4a',10204,774,24969),('5e4d0a6d39a44b9c906a3173b448aa4a',10205,774,24970),('5e4d0a6d39a44b9c906a3173b448aa4a',10206,774,24971),('5e4d0a6d39a44b9c906a3173b448aa4a',10207,774,24972),('5e4d0a6d39a44b9c906a3173b448aa4a',10208,774,24973),('5e4d0a6d39a44b9c906a3173b448aa4a',10209,774,24974),('5e4d0a6d39a44b9c906a3173b448aa4a',10210,774,25699),('5e4d0a6d39a44b9c906a3173b448aa4a',10211,774,24975),('5e4d0a6d39a44b9c906a3173b448aa4a',10212,774,24976),('5e4d0a6d39a44b9c906a3173b448aa4a',10213,774,25697),('5e4d0a6d39a44b9c906a3173b448aa4a',10214,774,24994),('5e4d0a6d39a44b9c906a3173b448aa4a',10215,774,25698),('5e4d0a6d39a44b9c906a3173b448aa4a',10216,774,25442),('5e4d0a6d39a44b9c906a3173b448aa4a',10217,774,24995),('5e4d0a6d39a44b9c906a3173b448aa4a',10218,774,24996),('5e4d0a6d39a44b9c906a3173b448aa4a',10219,774,24997),('5e4d0a6d39a44b9c906a3173b448aa4a',10220,774,24998),('5e4d0a6d39a44b9c906a3173b448aa4a',10221,774,24999),('5e4d0a6d39a44b9c906a3173b448aa4a',10222,774,25001),('5e4d0a6d39a44b9c906a3173b448aa4a',10223,774,25002),('5e4d0a6d39a44b9c906a3173b448aa4a',10224,728,25700),('5e4d0a6d39a44b9c906a3173b448aa4a',10225,728,25419),('5e4d0a6d39a44b9c906a3173b448aa4a',10226,728,25420),('5e4d0a6d39a44b9c906a3173b448aa4a',10227,728,25421),('5e4d0a6d39a44b9c906a3173b448aa4a',10228,728,25422),('5e4d0a6d39a44b9c906a3173b448aa4a',10229,728,25423),('5e4d0a6d39a44b9c906a3173b448aa4a',10230,728,25425),('5e4d0a6d39a44b9c906a3173b448aa4a',10231,728,25426),('5e4d0a6d39a44b9c906a3173b448aa4a',10232,730,25429),('5e4d0a6d39a44b9c906a3173b448aa4a',10236,635,24993),('5e4d0a6d39a44b9c906a3173b448aa4a',10237,635,24989),('5e4d0a6d39a44b9c906a3173b448aa4a',10238,635,24986),('5e4d0a6d39a44b9c906a3173b448aa4a',10239,635,24988),('5e4d0a6d39a44b9c906a3173b448aa4a',10240,635,24987),('5e4d0a6d39a44b9c906a3173b448aa4a',10243,635,24991),('5e4d0a6d39a44b9c906a3173b448aa4a',10245,635,24992),('5e4d0a6d39a44b9c906a3173b448aa4a',10246,635,24977),('5e4d0a6d39a44b9c906a3173b448aa4a',10247,635,24979),('5e4d0a6d39a44b9c906a3173b448aa4a',10248,635,24978),('5e4d0a6d39a44b9c906a3173b448aa4a',10249,635,24980),('5e4d0a6d39a44b9c906a3173b448aa4a',10250,635,24981),('5e4d0a6d39a44b9c906a3173b448aa4a',10251,635,24983),('5e4d0a6d39a44b9c906a3173b448aa4a',10252,635,24985),('5e4d0a6d39a44b9c906a3173b448aa4a',10253,635,24984),('5e4d0a6d39a44b9c906a3173b448aa4a',10254,635,24982),('5e4d0a6d39a44b9c906a3173b448aa4a',10541,636,25004),('5e4d0a6d39a44b9c906a3173b448aa4a',10542,636,25005),('5e4d0a6d39a44b9c906a3173b448aa4a',10543,636,25006),('5e4d0a6d39a44b9c906a3173b448aa4a',10544,636,25007),('5e4d0a6d39a44b9c906a3173b448aa4a',10545,636,25008),('5e4d0a6d39a44b9c906a3173b448aa4a',10546,636,25009),('5e4d0a6d39a44b9c906a3173b448aa4a',10547,636,25010),('5e4d0a6d39a44b9c906a3173b448aa4a',10548,636,25011),('5e4d0a6d39a44b9c906a3173b448aa4a',10549,636,25012),('5e4d0a6d39a44b9c906a3173b448aa4a',10550,636,25013),('5e4d0a6d39a44b9c906a3173b448aa4a',10551,636,25014),('5e4d0a6d39a44b9c906a3173b448aa4a',10552,636,25015),('5e4d0a6d39a44b9c906a3173b448aa4a',10553,636,25016),('5e4d0a6d39a44b9c906a3173b448aa4a',10554,636,25017),('5e4d0a6d39a44b9c906a3173b448aa4a',10555,661,25119),('5e4d0a6d39a44b9c906a3173b448aa4a',10556,661,25120),('5e4d0a6d39a44b9c906a3173b448aa4a',10557,776,25789),('5e4d0a6d39a44b9c906a3173b448aa4a',10558,776,25790),('5e4d0a6d39a44b9c906a3173b448aa4a',10559,776,25791),('5e4d0a6d39a44b9c906a3173b448aa4a',10560,776,25792),('5e4d0a6d39a44b9c906a3173b448aa4a',10561,776,25793),('5e4d0a6d39a44b9c906a3173b448aa4a',10562,776,25794),('5e4d0a6d39a44b9c906a3173b448aa4a',10563,776,25795),('5e4d0a6d39a44b9c906a3173b448aa4a',10564,776,25796),('5e4d0a6d39a44b9c906a3173b448aa4a',10565,776,25797),('5e4d0a6d39a44b9c906a3173b448aa4a',10566,776,25798),('5e4d0a6d39a44b9c906a3173b448aa4a',10567,775,25799),('5e4d0a6d39a44b9c906a3173b448aa4a',10568,775,25800),('5e4d0a6d39a44b9c906a3173b448aa4a',10569,775,25801),('5e4d0a6d39a44b9c906a3173b448aa4a',10570,775,25802),('5e4d0a6d39a44b9c906a3173b448aa4a',10571,775,25803),('5e4d0a6d39a44b9c906a3173b448aa4a',10573,724,25809),('5e4d0a6d39a44b9c906a3173b448aa4a',10575,724,25387),('5e4d0a6d39a44b9c906a3173b448aa4a',10576,724,25402),('5e4d0a6d39a44b9c906a3173b448aa4a',10577,724,25398),('5e4d0a6d39a44b9c906a3173b448aa4a',10578,724,25400),('5e4d0a6d39a44b9c906a3173b448aa4a',10579,724,25399),('5e4d0a6d39a44b9c906a3173b448aa4a',10583,535,25811),('5e4d0a6d39a44b9c906a3173b448aa4a',10584,535,25812),('5e4d0a6d39a44b9c906a3173b448aa4a',10586,535,24095),('5e4d0a6d39a44b9c906a3173b448aa4a',10587,777,25806),('5e4d0a6d39a44b9c906a3173b448aa4a',10588,777,25808),('5e4d0a6d39a44b9c906a3173b448aa4a',10589,777,25807),('5e4d0a6d39a44b9c906a3173b448aa4a',10590,777,25804),('5e4d0a6d39a44b9c906a3173b448aa4a',10591,777,25805),('5e4d0a6d39a44b9c906a3173b448aa4a',10594,535,24097),('5e4d0a6d39a44b9c906a3173b448aa4a',10596,724,25390),('5e4d0a6d39a44b9c906a3173b448aa4a',10597,724,25395),('5e4d0a6d39a44b9c906a3173b448aa4a',10661,752,25544),('5e4d0a6d39a44b9c906a3173b448aa4a',10662,750,25766),('5e4d0a6d39a44b9c906a3173b448aa4a',10664,666,25769),('5e4d0a6d39a44b9c906a3173b448aa4a',10665,666,25773),('5e4d0a6d39a44b9c906a3173b448aa4a',10667,662,25125),('5e4d0a6d39a44b9c906a3173b448aa4a',10668,662,25122),('5e4d0a6d39a44b9c906a3173b448aa4a',10670,663,25134),('5e4d0a6d39a44b9c906a3173b448aa4a',10671,663,25131),('5e4d0a6d39a44b9c906a3173b448aa4a',10673,635,24990),('5e4d0a6d39a44b9c906a3173b448aa4a',10674,697,25136),('5e4d0a6d39a44b9c906a3173b448aa4a',10675,697,25137),('5e4d0a6d39a44b9c906a3173b448aa4a',10676,697,25138),('5e4d0a6d39a44b9c906a3173b448aa4a',10677,697,25140),('5e4d0a6d39a44b9c906a3173b448aa4a',10678,697,25139),('5e4d0a6d39a44b9c906a3173b448aa4a',10685,698,25147),('5e4d0a6d39a44b9c906a3173b448aa4a',10689,535,24094),('5e4d0a6d39a44b9c906a3173b448aa4a',10690,724,25814),('5e4d0a6d39a44b9c906a3173b448aa4a',10691,731,25430),('5e4d0a6d39a44b9c906a3173b448aa4a',10692,693,24147),('5e4d0a6d39a44b9c906a3173b448aa4a',10693,691,25782),('5e4d0a6d39a44b9c906a3173b448aa4a',10694,697,25838),('5e4d0a6d39a44b9c906a3173b448aa4a',10697,640,25842),('5e4d0a6d39a44b9c906a3173b448aa4a',10703,750,25764),('5e4d0a6d39a44b9c906a3173b448aa4a',10704,640,25840),('5e4d0a6d39a44b9c906a3173b448aa4a',10705,640,25841),('5e4d0a6d39a44b9c906a3173b448aa4a',10710,784,25849),('5e4d0a6d39a44b9c906a3173b448aa4a',10711,784,25850),('5e4d0a6d39a44b9c906a3173b448aa4a',10713,653,25058),('5e4d0a6d39a44b9c906a3173b448aa4a',10722,650,25043),('5e4d0a6d39a44b9c906a3173b448aa4a',10723,650,25045),('5e4d0a6d39a44b9c906a3173b448aa4a',10724,651,25048),('5e4d0a6d39a44b9c906a3173b448aa4a',10726,651,25047),('5e4d0a6d39a44b9c906a3173b448aa4a',10732,653,25860),('5e4d0a6d39a44b9c906a3173b448aa4a',10733,653,25852),('5e4d0a6d39a44b9c906a3173b448aa4a',10734,653,25851),('5e4d0a6d39a44b9c906a3173b448aa4a',10735,649,25853),('5e4d0a6d39a44b9c906a3173b448aa4a',10736,649,25854),('5e4d0a6d39a44b9c906a3173b448aa4a',10737,652,25855),('5e4d0a6d39a44b9c906a3173b448aa4a',10738,652,25856),('5e4d0a6d39a44b9c906a3173b448aa4a',10739,784,25847),('5e4d0a6d39a44b9c906a3173b448aa4a',10740,784,25848),('5e4d0a6d39a44b9c906a3173b448aa4a',10741,785,25861),('5e4d0a6d39a44b9c906a3173b448aa4a',10742,785,25862),('5e4d0a6d39a44b9c906a3173b448aa4a',10744,786,25865),('5e4d0a6d39a44b9c906a3173b448aa4a',10745,786,25866),('5e4d0a6d39a44b9c906a3173b448aa4a',10746,786,25867),('5e4d0a6d39a44b9c906a3173b448aa4a',10747,786,25868),('5e4d0a6d39a44b9c906a3173b448aa4a',10748,785,25863),('5e4d0a6d39a44b9c906a3173b448aa4a',10753,765,25873),('5e4d0a6d39a44b9c906a3173b448aa4a',10754,776,25874),('5e4d0a6d39a44b9c906a3173b448aa4a',10755,765,25875),('5e4d0a6d39a44b9c906a3173b448aa4a',10756,776,25876),('5e4d0a6d39a44b9c906a3173b448aa4a',10757,661,25098),('5e4d0a6d39a44b9c906a3173b448aa4a',10758,662,25123),('5e4d0a6d39a44b9c906a3173b448aa4a',10759,663,25132),('5e4d0a6d39a44b9c906a3173b448aa4a',10760,662,25843),('5e4d0a6d39a44b9c906a3173b448aa4a',10761,662,25844),('5e4d0a6d39a44b9c906a3173b448aa4a',10762,666,25877),('5e4d0a6d39a44b9c906a3173b448aa4a',10763,641,25878),('5e4d0a6d39a44b9c906a3173b448aa4a',10764,654,25879),('5e4d0a6d39a44b9c906a3173b448aa4a',10766,654,25880),('5e4d0a6d39a44b9c906a3173b448aa4a',10767,650,25881),('5e4d0a6d39a44b9c906a3173b448aa4a',10768,650,25882),('5e4d0a6d39a44b9c906a3173b448aa4a',10769,644,25883),('5e4d0a6d39a44b9c906a3173b448aa4a',10770,641,25884),('5e4d0a6d39a44b9c906a3173b448aa4a',10772,642,25885),('5e4d0a6d39a44b9c906a3173b448aa4a',10773,648,25886),('5e4d0a6d39a44b9c906a3173b448aa4a',10774,648,25887),('5e4d0a6d39a44b9c906a3173b448aa4a',10775,648,25888),('5e4d0a6d39a44b9c906a3173b448aa4a',10777,641,25890),('5e4d0a6d39a44b9c906a3173b448aa4a',10778,641,25889),('5e4d0a6d39a44b9c906a3173b448aa4a',10780,743,25891),('5e4d0a6d39a44b9c906a3173b448aa4a',10781,635,25786),('5e4d0a6d39a44b9c906a3173b448aa4a',10782,635,25787),('5e4d0a6d39a44b9c906a3173b448aa4a',10783,635,25785),('5e4d0a6d39a44b9c906a3173b448aa4a',10786,727,25696),('5e4d0a6d39a44b9c906a3173b448aa4a',10787,664,25111),('5e4d0a6d39a44b9c906a3173b448aa4a',10789,664,25109),('5e4d0a6d39a44b9c906a3173b448aa4a',10796,666,25771),('5e4d0a6d39a44b9c906a3173b448aa4a',10797,666,25893),('5e4d0a6d39a44b9c906a3173b448aa4a',10798,666,25894),('5e4d0a6d39a44b9c906a3173b448aa4a',10799,666,25895),('5e4d0a6d39a44b9c906a3173b448aa4a',10802,750,25845),('5e4d0a6d39a44b9c906a3173b448aa4a',10803,750,25846),('5e4d0a6d39a44b9c906a3173b448aa4a',10807,758,25892),('5e4d0a6d39a44b9c906a3173b448aa4a',10809,665,25102),('5e4d0a6d39a44b9c906a3173b448aa4a',10810,535,24096),('5e4d0a6d39a44b9c906a3173b448aa4a',10811,535,25813),('5e4d0a6d39a44b9c906a3173b448aa4a',10812,535,24098),('5e4d0a6d39a44b9c906a3173b448aa4a',10813,535,25810),('5e4d0a6d39a44b9c906a3173b448aa4a',10814,758,25898),('5e4d0a6d39a44b9c906a3173b448aa4a',10820,745,25511),('5e4d0a6d39a44b9c906a3173b448aa4a',10821,754,25899),('5e4d0a6d39a44b9c906a3173b448aa4a',10826,748,25531),('5e4d0a6d39a44b9c906a3173b448aa4a',10827,748,25530),('5e4d0a6d39a44b9c906a3173b448aa4a',10828,748,25529),('5e4d0a6d39a44b9c906a3173b448aa4a',10829,748,25528),('5e4d0a6d39a44b9c906a3173b448aa4a',10830,757,25575),('5e4d0a6d39a44b9c906a3173b448aa4a',10831,757,25577),('5e4d0a6d39a44b9c906a3173b448aa4a',10832,757,25579),('5e4d0a6d39a44b9c906a3173b448aa4a',10833,757,25581),('5e4d0a6d39a44b9c906a3173b448aa4a',10834,743,25506),('5e4d0a6d39a44b9c906a3173b448aa4a',10835,743,25505),('5e4d0a6d39a44b9c906a3173b448aa4a',10837,724,25389),('5e4d0a6d39a44b9c906a3173b448aa4a',10838,724,25386),('5e4d0a6d39a44b9c906a3173b448aa4a',10840,689,24178),('5e4d0a6d39a44b9c906a3173b448aa4a',10841,689,24177),('5e4d0a6d39a44b9c906a3173b448aa4a',10842,689,24176),('5e4d0a6d39a44b9c906a3173b448aa4a',10843,698,25144),('5e4d0a6d39a44b9c906a3173b448aa4a',10844,698,25145),('5e4d0a6d39a44b9c906a3173b448aa4a',10845,698,25146),('5e4d0a6d39a44b9c906a3173b448aa4a',10846,698,25141),('5e4d0a6d39a44b9c906a3173b448aa4a',10847,698,25142),('5e4d0a6d39a44b9c906a3173b448aa4a',10848,698,25143),('5e4d0a6d39a44b9c906a3173b448aa4a',10850,724,25388),('5e4d0a6d39a44b9c906a3173b448aa4a',10851,654,25062),('5e4d0a6d39a44b9c906a3173b448aa4a',10852,690,24166),('5e4d0a6d39a44b9c906a3173b448aa4a',10853,690,24165),('5e4d0a6d39a44b9c906a3173b448aa4a',10854,690,24167),('5e4d0a6d39a44b9c906a3173b448aa4a',10855,702,25745),('5e4d0a6d39a44b9c906a3173b448aa4a',10856,702,25740),('5e4d0a6d39a44b9c906a3173b448aa4a',10857,700,25739),('5e4d0a6d39a44b9c906a3173b448aa4a',10858,700,25738),('5e4d0a6d39a44b9c906a3173b448aa4a',10859,699,25746),('5e4d0a6d39a44b9c906a3173b448aa4a',10860,699,25741),('5e4d0a6d39a44b9c906a3173b448aa4a',10861,700,25735),('5e4d0a6d39a44b9c906a3173b448aa4a',10862,700,25734),('5e4d0a6d39a44b9c906a3173b448aa4a',10863,699,25744),('5e4d0a6d39a44b9c906a3173b448aa4a',10864,699,25749),('5e4d0a6d39a44b9c906a3173b448aa4a',10865,699,25743),('5e4d0a6d39a44b9c906a3173b448aa4a',10866,699,25742),('5e4d0a6d39a44b9c906a3173b448aa4a',10867,699,25747),('5e4d0a6d39a44b9c906a3173b448aa4a',10868,702,25748),('5e4d0a6d39a44b9c906a3173b448aa4a',10869,700,25737),('5e4d0a6d39a44b9c906a3173b448aa4a',10870,700,25736),('5e4d0a6d39a44b9c906a3173b448aa4a',10871,781,25834),('5e4d0a6d39a44b9c906a3173b448aa4a',10872,781,25836),('5e4d0a6d39a44b9c906a3173b448aa4a',10873,781,25837),('5e4d0a6d39a44b9c906a3173b448aa4a',10874,781,25835),('5e4d0a6d39a44b9c906a3173b448aa4a',10875,782,25826),('5e4d0a6d39a44b9c906a3173b448aa4a',10876,783,25832),('5e4d0a6d39a44b9c906a3173b448aa4a',10877,783,25833),('5e4d0a6d39a44b9c906a3173b448aa4a',10878,783,25830),('5e4d0a6d39a44b9c906a3173b448aa4a',10879,783,25831),('5e4d0a6d39a44b9c906a3173b448aa4a',10880,782,25827),('5e4d0a6d39a44b9c906a3173b448aa4a',10881,783,25828),('5e4d0a6d39a44b9c906a3173b448aa4a',10882,783,25829),('5e4d0a6d39a44b9c906a3173b448aa4a',10883,778,25825),('5e4d0a6d39a44b9c906a3173b448aa4a',10884,779,25869),('5e4d0a6d39a44b9c906a3173b448aa4a',10885,779,25870),('5e4d0a6d39a44b9c906a3173b448aa4a',10886,778,25824),('5e4d0a6d39a44b9c906a3173b448aa4a',10887,778,25823),('5e4d0a6d39a44b9c906a3173b448aa4a',10888,780,25815),('5e4d0a6d39a44b9c906a3173b448aa4a',10889,779,25819),('5e4d0a6d39a44b9c906a3173b448aa4a',10890,779,25872),('5e4d0a6d39a44b9c906a3173b448aa4a',10891,779,25871),('5e4d0a6d39a44b9c906a3173b448aa4a',10892,780,25816),('5e4d0a6d39a44b9c906a3173b448aa4a',10893,779,25818),('5e4d0a6d39a44b9c906a3173b448aa4a',10894,779,25817),('5e4d0a6d39a44b9c906a3173b448aa4a',10895,779,25820);
/*!40000 ALTER TABLE `items_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenances`
--

DROP TABLE IF EXISTS `maintenances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maintenances` (
  `tenantid` varchar(64) DEFAULT '0',
  `maintenanceid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `maintenance_type` int(11) NOT NULL DEFAULT '0',
  `description` text NOT NULL,
  `active_since` int(11) NOT NULL DEFAULT '0',
  `active_till` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`maintenanceid`),
  KEY `maintenances_1` (`active_since`,`active_till`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenances`
--

LOCK TABLES `maintenances` WRITE;
/*!40000 ALTER TABLE `maintenances` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenances_groups`
--

DROP TABLE IF EXISTS `maintenances_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maintenances_groups` (
  `tenantid` varchar(64) DEFAULT '0',
  `maintenance_groupid` bigint(20) unsigned NOT NULL,
  `maintenanceid` bigint(20) unsigned NOT NULL,
  `groupid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`maintenance_groupid`),
  UNIQUE KEY `maintenances_groups_1` (`maintenanceid`,`groupid`),
  KEY `maintenances_groups_2` (`groupid`),
  CONSTRAINT `c_maintenances_groups_1` FOREIGN KEY (`maintenanceid`) REFERENCES `maintenances` (`maintenanceid`) ON DELETE CASCADE,
  CONSTRAINT `c_maintenances_groups_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenances_groups`
--

LOCK TABLES `maintenances_groups` WRITE;
/*!40000 ALTER TABLE `maintenances_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenances_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenances_hosts`
--

DROP TABLE IF EXISTS `maintenances_hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maintenances_hosts` (
  `tenantid` varchar(64) DEFAULT '0',
  `maintenance_hostid` bigint(20) unsigned NOT NULL,
  `maintenanceid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`maintenance_hostid`),
  UNIQUE KEY `maintenances_hosts_1` (`maintenanceid`,`hostid`),
  KEY `maintenances_hosts_2` (`hostid`),
  CONSTRAINT `c_maintenances_hosts_1` FOREIGN KEY (`maintenanceid`) REFERENCES `maintenances` (`maintenanceid`) ON DELETE CASCADE,
  CONSTRAINT `c_maintenances_hosts_2` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenances_hosts`
--

LOCK TABLES `maintenances_hosts` WRITE;
/*!40000 ALTER TABLE `maintenances_hosts` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenances_hosts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenances_windows`
--

DROP TABLE IF EXISTS `maintenances_windows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maintenances_windows` (
  `tenantid` varchar(64) DEFAULT '0',
  `maintenance_timeperiodid` bigint(20) unsigned NOT NULL,
  `maintenanceid` bigint(20) unsigned NOT NULL,
  `timeperiodid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`maintenance_timeperiodid`),
  UNIQUE KEY `maintenances_windows_1` (`maintenanceid`,`timeperiodid`),
  KEY `maintenances_windows_2` (`timeperiodid`),
  CONSTRAINT `c_maintenances_windows_1` FOREIGN KEY (`maintenanceid`) REFERENCES `maintenances` (`maintenanceid`) ON DELETE CASCADE,
  CONSTRAINT `c_maintenances_windows_2` FOREIGN KEY (`timeperiodid`) REFERENCES `timeperiods` (`timeperiodid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenances_windows`
--

LOCK TABLES `maintenances_windows` WRITE;
/*!40000 ALTER TABLE `maintenances_windows` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenances_windows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mappings`
--

DROP TABLE IF EXISTS `mappings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mappings` (
  `tenantid` varchar(64) DEFAULT '0',
  `mappingid` bigint(20) unsigned NOT NULL,
  `valuemapid` bigint(20) unsigned NOT NULL,
  `value` varchar(64) NOT NULL DEFAULT '',
  `newvalue` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`mappingid`),
  KEY `mappings_1` (`valuemapid`),
  CONSTRAINT `c_mappings_1` FOREIGN KEY (`valuemapid`) REFERENCES `valuemaps` (`valuemapid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mappings`
--

LOCK TABLES `mappings` WRITE;
/*!40000 ALTER TABLE `mappings` DISABLE KEYS */;
INSERT INTO `mappings` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,1,'0','未激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',2,1,'1','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',3,2,'0','Up'),('5e4d0a6d39a44b9c906a3173b448aa4a',4,2,'2','Unreachable'),('5e4d0a6d39a44b9c906a3173b448aa4a',33,3,'0','运行中'),('5e4d0a6d39a44b9c906a3173b448aa4a',34,3,'1','暂停'),('5e4d0a6d39a44b9c906a3173b448aa4a',35,3,'3','暂停中'),('5e4d0a6d39a44b9c906a3173b448aa4a',36,3,'4','继续中'),('5e4d0a6d39a44b9c906a3173b448aa4a',37,3,'5','停止中'),('5e4d0a6d39a44b9c906a3173b448aa4a',38,3,'6','停止'),('5e4d0a6d39a44b9c906a3173b448aa4a',39,3,'7','未知'),('5e4d0a6d39a44b9c906a3173b448aa4a',40,3,'255','未知服务'),('5e4d0a6d39a44b9c906a3173b448aa4a',41,3,'2','启动中'),('5e4d0a6d39a44b9c906a3173b448aa4a',49,9,'1','unknown'),('5e4d0a6d39a44b9c906a3173b448aa4a',50,9,'2','running'),('5e4d0a6d39a44b9c906a3173b448aa4a',51,9,'3','warning'),('5e4d0a6d39a44b9c906a3173b448aa4a',52,9,'4','testing'),('5e4d0a6d39a44b9c906a3173b448aa4a',53,9,'5','down'),('5e4d0a6d39a44b9c906a3173b448aa4a',61,8,'1','up'),('5e4d0a6d39a44b9c906a3173b448aa4a',62,8,'2','down'),('5e4d0a6d39a44b9c906a3173b448aa4a',63,8,'3','testing'),('5e4d0a6d39a44b9c906a3173b448aa4a',64,8,'4','unknown'),('5e4d0a6d39a44b9c906a3173b448aa4a',65,8,'5','dormant'),('5e4d0a6d39a44b9c906a3173b448aa4a',66,8,'6','notPresent'),('5e4d0a6d39a44b9c906a3173b448aa4a',67,8,'7','lowerLayerDown'),('5e4d0a6d39a44b9c906a3173b448aa4a',68,10,'1','Up'),('5e4d0a6d39a44b9c906a3173b448aa4a',69,11,'1','up'),('5e4d0a6d39a44b9c906a3173b448aa4a',70,11,'2','down'),('5e4d0a6d39a44b9c906a3173b448aa4a',71,11,'3','testing'),('5e4d0a6d39a44b9c906a3173b448aa4a',133,2,'3','af'),('5e4d0a6d39a44b9c906a3173b448aa4a',137,20,'0','ONLINE'),('5e4d0a6d39a44b9c906a3173b448aa4a',138,20,'1','RESTORING'),('5e4d0a6d39a44b9c906a3173b448aa4a',139,20,'2','RECOVERING'),('5e4d0a6d39a44b9c906a3173b448aa4a',140,20,'3','RECOVERY PENDING'),('5e4d0a6d39a44b9c906a3173b448aa4a',141,20,'4','SUSPECT'),('5e4d0a6d39a44b9c906a3173b448aa4a',142,20,'5','EMERGENCY'),('5e4d0a6d39a44b9c906a3173b448aa4a',143,20,'6','OFFLINE'),('5e4d0a6d39a44b9c906a3173b448aa4a',144,20,'7','Database Does Not Exist on Server'),('5e4d0a6d39a44b9c906a3173b448aa4a',145,21,'0','No'),('5e4d0a6d39a44b9c906a3173b448aa4a',146,21,'1','Yes'),('5e4d0a6d39a44b9c906a3173b448aa4a',147,22,'1','normal'),('5e4d0a6d39a44b9c906a3173b448aa4a',148,22,'2','warning'),('5e4d0a6d39a44b9c906a3173b448aa4a',149,22,'3','critical'),('5e4d0a6d39a44b9c906a3173b448aa4a',150,22,'4','shutdown'),('5e4d0a6d39a44b9c906a3173b448aa4a',151,22,'5','notPresent'),('5e4d0a6d39a44b9c906a3173b448aa4a',152,22,'6','notFunctioning'),('5e4d0a6d39a44b9c906a3173b448aa4a',153,23,'1','True'),('5e4d0a6d39a44b9c906a3173b448aa4a',154,23,'2','False'),('5e4d0a6d39a44b9c906a3173b448aa4a',155,24,'1','unknown'),('5e4d0a6d39a44b9c906a3173b448aa4a',156,24,'2','halfDuplex'),('5e4d0a6d39a44b9c906a3173b448aa4a',157,24,'3','fullDuplex'),('5e4d0a6d39a44b9c906a3173b448aa4a',158,25,'1','other'),('5e4d0a6d39a44b9c906a3173b448aa4a',159,25,'2','regular1822'),('5e4d0a6d39a44b9c906a3173b448aa4a',160,25,'3','hdh1822'),('5e4d0a6d39a44b9c906a3173b448aa4a',161,25,'4','ddnX25'),('5e4d0a6d39a44b9c906a3173b448aa4a',162,25,'5','rfc877x25'),('5e4d0a6d39a44b9c906a3173b448aa4a',163,25,'6','ethernetCsmacd'),('5e4d0a6d39a44b9c906a3173b448aa4a',164,25,'7','iso88023Csmacd'),('5e4d0a6d39a44b9c906a3173b448aa4a',165,25,'8','iso88024TokenBus'),('5e4d0a6d39a44b9c906a3173b448aa4a',166,25,'9','iso88025TokenRing'),('5e4d0a6d39a44b9c906a3173b448aa4a',167,25,'10','iso88026Man'),('5e4d0a6d39a44b9c906a3173b448aa4a',168,25,'11','starLan'),('5e4d0a6d39a44b9c906a3173b448aa4a',169,25,'12','proteon10Mbit'),('5e4d0a6d39a44b9c906a3173b448aa4a',170,25,'13','proteon80Mbit'),('5e4d0a6d39a44b9c906a3173b448aa4a',171,25,'14','hyperchannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',172,25,'15','fddi'),('5e4d0a6d39a44b9c906a3173b448aa4a',173,25,'16','lapb'),('5e4d0a6d39a44b9c906a3173b448aa4a',174,25,'17','sdlc'),('5e4d0a6d39a44b9c906a3173b448aa4a',175,25,'18','ds1'),('5e4d0a6d39a44b9c906a3173b448aa4a',176,25,'19','e1'),('5e4d0a6d39a44b9c906a3173b448aa4a',177,25,'20','basicISDN'),('5e4d0a6d39a44b9c906a3173b448aa4a',178,25,'21','primaryISDN'),('5e4d0a6d39a44b9c906a3173b448aa4a',179,25,'22','propPointToPointSerial'),('5e4d0a6d39a44b9c906a3173b448aa4a',180,25,'23','ppp'),('5e4d0a6d39a44b9c906a3173b448aa4a',181,25,'24','softwareLoopback'),('5e4d0a6d39a44b9c906a3173b448aa4a',182,25,'25','eon'),('5e4d0a6d39a44b9c906a3173b448aa4a',183,25,'26','ethernet3Mbit'),('5e4d0a6d39a44b9c906a3173b448aa4a',184,25,'27','nsip'),('5e4d0a6d39a44b9c906a3173b448aa4a',185,25,'28','slip'),('5e4d0a6d39a44b9c906a3173b448aa4a',186,25,'29','ultra'),('5e4d0a6d39a44b9c906a3173b448aa4a',187,25,'30','ds3'),('5e4d0a6d39a44b9c906a3173b448aa4a',188,25,'31','sip'),('5e4d0a6d39a44b9c906a3173b448aa4a',189,25,'32','frameRelay'),('5e4d0a6d39a44b9c906a3173b448aa4a',190,25,'33','rs232'),('5e4d0a6d39a44b9c906a3173b448aa4a',191,25,'34','para'),('5e4d0a6d39a44b9c906a3173b448aa4a',192,25,'35','arcnet'),('5e4d0a6d39a44b9c906a3173b448aa4a',193,25,'36','arcnetPlus'),('5e4d0a6d39a44b9c906a3173b448aa4a',194,25,'37','atm'),('5e4d0a6d39a44b9c906a3173b448aa4a',195,25,'38','miox25'),('5e4d0a6d39a44b9c906a3173b448aa4a',196,25,'39','sonet'),('5e4d0a6d39a44b9c906a3173b448aa4a',197,25,'40','x25ple'),('5e4d0a6d39a44b9c906a3173b448aa4a',198,25,'41','iso88022llc'),('5e4d0a6d39a44b9c906a3173b448aa4a',199,25,'42','localTalk'),('5e4d0a6d39a44b9c906a3173b448aa4a',200,25,'43','smdsDxi'),('5e4d0a6d39a44b9c906a3173b448aa4a',201,25,'44','frameRelayService'),('5e4d0a6d39a44b9c906a3173b448aa4a',202,25,'45','v35'),('5e4d0a6d39a44b9c906a3173b448aa4a',203,25,'46','hssi'),('5e4d0a6d39a44b9c906a3173b448aa4a',204,25,'47','hippi'),('5e4d0a6d39a44b9c906a3173b448aa4a',205,25,'48','modem'),('5e4d0a6d39a44b9c906a3173b448aa4a',206,25,'49','aal5'),('5e4d0a6d39a44b9c906a3173b448aa4a',207,25,'50','sonetPath'),('5e4d0a6d39a44b9c906a3173b448aa4a',208,25,'51','sonetVT'),('5e4d0a6d39a44b9c906a3173b448aa4a',209,25,'52','smdsIcip'),('5e4d0a6d39a44b9c906a3173b448aa4a',210,25,'53','propVirtual'),('5e4d0a6d39a44b9c906a3173b448aa4a',211,25,'54','propMultiplexor'),('5e4d0a6d39a44b9c906a3173b448aa4a',212,25,'55','ieee80212'),('5e4d0a6d39a44b9c906a3173b448aa4a',213,25,'56','fibreChannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',214,25,'57','hippiInterface'),('5e4d0a6d39a44b9c906a3173b448aa4a',215,25,'58','frameRelayInterconnect'),('5e4d0a6d39a44b9c906a3173b448aa4a',216,25,'59','aflane8023'),('5e4d0a6d39a44b9c906a3173b448aa4a',217,25,'60','aflane8025'),('5e4d0a6d39a44b9c906a3173b448aa4a',218,25,'61','cctEmul'),('5e4d0a6d39a44b9c906a3173b448aa4a',219,25,'62','fastEther'),('5e4d0a6d39a44b9c906a3173b448aa4a',220,25,'63','isdn'),('5e4d0a6d39a44b9c906a3173b448aa4a',221,25,'64','v11'),('5e4d0a6d39a44b9c906a3173b448aa4a',222,25,'65','v36'),('5e4d0a6d39a44b9c906a3173b448aa4a',223,25,'66','g703at64k'),('5e4d0a6d39a44b9c906a3173b448aa4a',224,25,'67','g703at2mb'),('5e4d0a6d39a44b9c906a3173b448aa4a',225,25,'68','qllc'),('5e4d0a6d39a44b9c906a3173b448aa4a',226,25,'69','fastEtherFX'),('5e4d0a6d39a44b9c906a3173b448aa4a',227,25,'70','channel'),('5e4d0a6d39a44b9c906a3173b448aa4a',228,25,'71','ieee80211'),('5e4d0a6d39a44b9c906a3173b448aa4a',229,25,'72','ibm370parChan'),('5e4d0a6d39a44b9c906a3173b448aa4a',230,25,'73','escon'),('5e4d0a6d39a44b9c906a3173b448aa4a',231,25,'74','dlsw'),('5e4d0a6d39a44b9c906a3173b448aa4a',232,25,'75','isdns'),('5e4d0a6d39a44b9c906a3173b448aa4a',233,25,'76','isdnu'),('5e4d0a6d39a44b9c906a3173b448aa4a',234,25,'77','lapd'),('5e4d0a6d39a44b9c906a3173b448aa4a',235,25,'78','ipSwitch'),('5e4d0a6d39a44b9c906a3173b448aa4a',236,25,'79','rsrb'),('5e4d0a6d39a44b9c906a3173b448aa4a',237,25,'80','atmLogical'),('5e4d0a6d39a44b9c906a3173b448aa4a',238,25,'81','ds0'),('5e4d0a6d39a44b9c906a3173b448aa4a',239,25,'82','ds0Bundle'),('5e4d0a6d39a44b9c906a3173b448aa4a',240,25,'83','bsc'),('5e4d0a6d39a44b9c906a3173b448aa4a',241,25,'84','async'),('5e4d0a6d39a44b9c906a3173b448aa4a',242,25,'85','cnr'),('5e4d0a6d39a44b9c906a3173b448aa4a',243,25,'86','iso88025Dtr'),('5e4d0a6d39a44b9c906a3173b448aa4a',244,25,'87','eplrs'),('5e4d0a6d39a44b9c906a3173b448aa4a',245,25,'88','arap'),('5e4d0a6d39a44b9c906a3173b448aa4a',246,25,'89','propCnls'),('5e4d0a6d39a44b9c906a3173b448aa4a',247,25,'90','hostPad'),('5e4d0a6d39a44b9c906a3173b448aa4a',248,25,'91','termPad'),('5e4d0a6d39a44b9c906a3173b448aa4a',249,25,'92','frameRelayMPI'),('5e4d0a6d39a44b9c906a3173b448aa4a',250,25,'93','x213'),('5e4d0a6d39a44b9c906a3173b448aa4a',251,25,'94','adsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',252,25,'95','radsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',253,25,'96','sdsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',254,25,'97','vdsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',255,25,'98','iso88025CRFPInt'),('5e4d0a6d39a44b9c906a3173b448aa4a',256,25,'99','myrinet'),('5e4d0a6d39a44b9c906a3173b448aa4a',257,25,'100','voiceEM'),('5e4d0a6d39a44b9c906a3173b448aa4a',258,25,'101','voiceFXO'),('5e4d0a6d39a44b9c906a3173b448aa4a',259,25,'102','voiceFXS'),('5e4d0a6d39a44b9c906a3173b448aa4a',260,25,'103','voiceEncap'),('5e4d0a6d39a44b9c906a3173b448aa4a',261,25,'104','voiceOverIp'),('5e4d0a6d39a44b9c906a3173b448aa4a',262,25,'105','atmDxi'),('5e4d0a6d39a44b9c906a3173b448aa4a',263,25,'106','atmFuni'),('5e4d0a6d39a44b9c906a3173b448aa4a',264,25,'107','atmIma'),('5e4d0a6d39a44b9c906a3173b448aa4a',265,25,'108','pppMultilinkBundle'),('5e4d0a6d39a44b9c906a3173b448aa4a',266,25,'109','ipOverCdlc'),('5e4d0a6d39a44b9c906a3173b448aa4a',267,25,'110','ipOverClaw'),('5e4d0a6d39a44b9c906a3173b448aa4a',268,25,'111','stackToStack'),('5e4d0a6d39a44b9c906a3173b448aa4a',269,25,'112','virtualIpAddress'),('5e4d0a6d39a44b9c906a3173b448aa4a',270,25,'113','mpc'),('5e4d0a6d39a44b9c906a3173b448aa4a',271,25,'114','ipOverAtm'),('5e4d0a6d39a44b9c906a3173b448aa4a',272,25,'115','iso88025Fiber'),('5e4d0a6d39a44b9c906a3173b448aa4a',273,25,'116','tdlc'),('5e4d0a6d39a44b9c906a3173b448aa4a',274,25,'117','gigabitEthernet'),('5e4d0a6d39a44b9c906a3173b448aa4a',275,25,'118','hdlc'),('5e4d0a6d39a44b9c906a3173b448aa4a',276,25,'119','lapf'),('5e4d0a6d39a44b9c906a3173b448aa4a',277,25,'120','v37'),('5e4d0a6d39a44b9c906a3173b448aa4a',278,25,'121','x25mlp'),('5e4d0a6d39a44b9c906a3173b448aa4a',279,25,'122','x25huntGroup'),('5e4d0a6d39a44b9c906a3173b448aa4a',280,25,'123','trasnpHdlc'),('5e4d0a6d39a44b9c906a3173b448aa4a',281,25,'124','interleave'),('5e4d0a6d39a44b9c906a3173b448aa4a',282,25,'125','fast'),('5e4d0a6d39a44b9c906a3173b448aa4a',283,25,'126','ip'),('5e4d0a6d39a44b9c906a3173b448aa4a',284,25,'127','docsCableMaclayer'),('5e4d0a6d39a44b9c906a3173b448aa4a',285,25,'128','docsCableDownstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',286,25,'129','docsCableUpstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',287,25,'130','a12MppSwitch'),('5e4d0a6d39a44b9c906a3173b448aa4a',288,25,'131','tunnel'),('5e4d0a6d39a44b9c906a3173b448aa4a',289,25,'132','coffee'),('5e4d0a6d39a44b9c906a3173b448aa4a',290,25,'133','ces'),('5e4d0a6d39a44b9c906a3173b448aa4a',291,25,'134','atmSubInterface'),('5e4d0a6d39a44b9c906a3173b448aa4a',292,25,'135','l2vlan'),('5e4d0a6d39a44b9c906a3173b448aa4a',293,25,'136','l3ipvlan'),('5e4d0a6d39a44b9c906a3173b448aa4a',294,25,'137','l3ipxvlan'),('5e4d0a6d39a44b9c906a3173b448aa4a',295,25,'138','digitalPowerline'),('5e4d0a6d39a44b9c906a3173b448aa4a',296,25,'139','mediaMailOverIp'),('5e4d0a6d39a44b9c906a3173b448aa4a',297,25,'140','dtm'),('5e4d0a6d39a44b9c906a3173b448aa4a',298,25,'141','dcn'),('5e4d0a6d39a44b9c906a3173b448aa4a',299,25,'142','ipForward'),('5e4d0a6d39a44b9c906a3173b448aa4a',300,25,'143','msdsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',301,25,'144','ieee1394'),('5e4d0a6d39a44b9c906a3173b448aa4a',302,25,'145','if-gsn'),('5e4d0a6d39a44b9c906a3173b448aa4a',303,25,'146','dvbRccMacLayer'),('5e4d0a6d39a44b9c906a3173b448aa4a',304,25,'147','dvbRccDownstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',305,25,'148','dvbRccUpstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',306,25,'149','atmVirtual'),('5e4d0a6d39a44b9c906a3173b448aa4a',307,25,'150','mplsTunnel'),('5e4d0a6d39a44b9c906a3173b448aa4a',308,25,'151','srp'),('5e4d0a6d39a44b9c906a3173b448aa4a',309,25,'152','voiceOverAtm'),('5e4d0a6d39a44b9c906a3173b448aa4a',310,25,'153','voiceOverFrameRelay'),('5e4d0a6d39a44b9c906a3173b448aa4a',311,25,'154','idsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',312,25,'155','compositeLink'),('5e4d0a6d39a44b9c906a3173b448aa4a',313,25,'156','ss7SigLink'),('5e4d0a6d39a44b9c906a3173b448aa4a',314,25,'157','propWirelessP2P'),('5e4d0a6d39a44b9c906a3173b448aa4a',315,25,'158','frForward'),('5e4d0a6d39a44b9c906a3173b448aa4a',316,25,'159','rfc1483'),('5e4d0a6d39a44b9c906a3173b448aa4a',317,25,'160','usb'),('5e4d0a6d39a44b9c906a3173b448aa4a',318,25,'161','ieee8023adLag'),('5e4d0a6d39a44b9c906a3173b448aa4a',319,25,'162','bgppolicyaccounting'),('5e4d0a6d39a44b9c906a3173b448aa4a',320,25,'163','frf16MfrBundle'),('5e4d0a6d39a44b9c906a3173b448aa4a',321,25,'164','h323Gatekeeper'),('5e4d0a6d39a44b9c906a3173b448aa4a',322,25,'165','h323Proxy'),('5e4d0a6d39a44b9c906a3173b448aa4a',323,25,'166','mpls'),('5e4d0a6d39a44b9c906a3173b448aa4a',324,25,'167','mfSigLink'),('5e4d0a6d39a44b9c906a3173b448aa4a',325,25,'168','hdsl2'),('5e4d0a6d39a44b9c906a3173b448aa4a',326,25,'169','shdsl'),('5e4d0a6d39a44b9c906a3173b448aa4a',327,25,'170','ds1FDL'),('5e4d0a6d39a44b9c906a3173b448aa4a',328,25,'171','pos'),('5e4d0a6d39a44b9c906a3173b448aa4a',329,25,'172','dvbAsiIn'),('5e4d0a6d39a44b9c906a3173b448aa4a',330,25,'173','dvbAsiOut'),('5e4d0a6d39a44b9c906a3173b448aa4a',331,25,'174','plc'),('5e4d0a6d39a44b9c906a3173b448aa4a',332,25,'175','nfas'),('5e4d0a6d39a44b9c906a3173b448aa4a',333,25,'176','tr008'),('5e4d0a6d39a44b9c906a3173b448aa4a',334,25,'177','gr303RDT'),('5e4d0a6d39a44b9c906a3173b448aa4a',335,25,'178','gr303IDT'),('5e4d0a6d39a44b9c906a3173b448aa4a',336,25,'179','isup'),('5e4d0a6d39a44b9c906a3173b448aa4a',337,25,'180','propDocsWirelessMaclayer'),('5e4d0a6d39a44b9c906a3173b448aa4a',338,25,'181','propDocsWirelessDownstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',339,25,'182','propDocsWirelessUpstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',340,25,'183','hiperlan2'),('5e4d0a6d39a44b9c906a3173b448aa4a',341,25,'184','propBWAp2Mp'),('5e4d0a6d39a44b9c906a3173b448aa4a',342,25,'185','sonetOverheadChannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',343,25,'186','digitalWrapperOverheadChannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',344,25,'187','aal2'),('5e4d0a6d39a44b9c906a3173b448aa4a',345,25,'188','radioMAC'),('5e4d0a6d39a44b9c906a3173b448aa4a',346,25,'189','atmRadio'),('5e4d0a6d39a44b9c906a3173b448aa4a',347,25,'190','imt'),('5e4d0a6d39a44b9c906a3173b448aa4a',348,25,'191','mvl'),('5e4d0a6d39a44b9c906a3173b448aa4a',349,25,'192','reachDSL'),('5e4d0a6d39a44b9c906a3173b448aa4a',350,25,'193','frDlciEndPt'),('5e4d0a6d39a44b9c906a3173b448aa4a',351,25,'194','atmVciEndPt'),('5e4d0a6d39a44b9c906a3173b448aa4a',352,25,'195','opticalChannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',353,25,'196','opticalTransport'),('5e4d0a6d39a44b9c906a3173b448aa4a',354,25,'197','propAtm'),('5e4d0a6d39a44b9c906a3173b448aa4a',355,25,'198','voiceOverCable'),('5e4d0a6d39a44b9c906a3173b448aa4a',356,25,'199','infiniband'),('5e4d0a6d39a44b9c906a3173b448aa4a',357,25,'200','teLink'),('5e4d0a6d39a44b9c906a3173b448aa4a',358,25,'201','q2931'),('5e4d0a6d39a44b9c906a3173b448aa4a',359,25,'202','virtualTg'),('5e4d0a6d39a44b9c906a3173b448aa4a',360,25,'203','sipTg'),('5e4d0a6d39a44b9c906a3173b448aa4a',361,25,'204','sipSig'),('5e4d0a6d39a44b9c906a3173b448aa4a',362,25,'205','docsCableUpstreamChannel'),('5e4d0a6d39a44b9c906a3173b448aa4a',363,25,'206','econet'),('5e4d0a6d39a44b9c906a3173b448aa4a',364,25,'207','pon155'),('5e4d0a6d39a44b9c906a3173b448aa4a',365,25,'208','pon622'),('5e4d0a6d39a44b9c906a3173b448aa4a',366,25,'209','bridge'),('5e4d0a6d39a44b9c906a3173b448aa4a',367,25,'210','linegroup'),('5e4d0a6d39a44b9c906a3173b448aa4a',368,25,'211','voiceEMFGD'),('5e4d0a6d39a44b9c906a3173b448aa4a',369,25,'212','voiceFGDEANA'),('5e4d0a6d39a44b9c906a3173b448aa4a',370,25,'213','voiceDID'),('5e4d0a6d39a44b9c906a3173b448aa4a',371,25,'214','mpegTransport'),('5e4d0a6d39a44b9c906a3173b448aa4a',372,25,'215','sixToFour'),('5e4d0a6d39a44b9c906a3173b448aa4a',373,25,'216','gtp'),('5e4d0a6d39a44b9c906a3173b448aa4a',374,25,'217','pdnEtherLoop1'),('5e4d0a6d39a44b9c906a3173b448aa4a',375,25,'218','pdnEtherLoop2'),('5e4d0a6d39a44b9c906a3173b448aa4a',376,25,'219','opticalChannelGroup'),('5e4d0a6d39a44b9c906a3173b448aa4a',377,25,'220','homepna'),('5e4d0a6d39a44b9c906a3173b448aa4a',378,25,'221','gfp'),('5e4d0a6d39a44b9c906a3173b448aa4a',379,25,'222','ciscoISLvlan'),('5e4d0a6d39a44b9c906a3173b448aa4a',380,25,'223','actelisMetaLOOP'),('5e4d0a6d39a44b9c906a3173b448aa4a',381,25,'224','fcipLink'),('5e4d0a6d39a44b9c906a3173b448aa4a',382,25,'225','rpr'),('5e4d0a6d39a44b9c906a3173b448aa4a',383,25,'226','qam'),('5e4d0a6d39a44b9c906a3173b448aa4a',384,25,'227','lmp'),('5e4d0a6d39a44b9c906a3173b448aa4a',385,25,'228','cblVectaStar'),('5e4d0a6d39a44b9c906a3173b448aa4a',386,25,'229','docsCableMCmtsDownstream'),('5e4d0a6d39a44b9c906a3173b448aa4a',387,25,'230','adsl2'),('5e4d0a6d39a44b9c906a3173b448aa4a',388,25,'231','macSecControlledIF'),('5e4d0a6d39a44b9c906a3173b448aa4a',389,25,'232','macSecUncontrolledIF'),('5e4d0a6d39a44b9c906a3173b448aa4a',390,25,'233','aviciOpticalEther'),('5e4d0a6d39a44b9c906a3173b448aa4a',391,25,'234','atmbond'),('5e4d0a6d39a44b9c906a3173b448aa4a',392,1,'2','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',393,1,'3','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',394,1,'4','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',395,1,'5','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',396,1,'6','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',397,1,'7','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',398,1,'8','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',399,1,'9','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',400,1,'10','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',401,1,'11','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',402,1,'12','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',403,1,'13','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',404,1,'14','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',405,1,'15','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',406,1,'16','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',407,1,'17','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',408,1,'18','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',409,1,'19','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',410,1,'20','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',411,1,'21','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',412,1,'22','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',413,1,'23','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',414,1,'24','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',415,1,'25','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',416,1,'26','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',417,1,'27','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',418,1,'28','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',419,1,'29','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',420,1,'30','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',421,1,'31','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',422,1,'32','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',423,1,'33','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',424,1,'34','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',425,1,'35','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',426,1,'36','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',427,1,'37','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',428,1,'38','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',429,1,'39','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',430,1,'40','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',431,1,'41','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',432,1,'42','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',433,1,'43','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',434,1,'44','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',435,1,'45','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',436,1,'46','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',437,1,'47','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',438,1,'48','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',439,1,'49','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',440,1,'50','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',441,1,'51','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',442,1,'52','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',443,1,'53','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',444,1,'54','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',445,1,'55','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',446,1,'56','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',447,1,'57','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',448,1,'58','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',449,1,'59','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',450,1,'60','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',451,26,'1','其他'),('5e4d0a6d39a44b9c906a3173b448aa4a',452,26,'2','无效'),('5e4d0a6d39a44b9c906a3173b448aa4a',453,26,'3','直连'),('5e4d0a6d39a44b9c906a3173b448aa4a',454,26,'4','非直连'),('5e4d0a6d39a44b9c906a3173b448aa4a',455,27,'1','其他'),('5e4d0a6d39a44b9c906a3173b448aa4a',456,27,'2','无效'),('5e4d0a6d39a44b9c906a3173b448aa4a',457,27,'3','动态'),('5e4d0a6d39a44b9c906a3173b448aa4a',458,27,'4','静态'),('5e4d0a6d39a44b9c906a3173b448aa4a',459,1,'61','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',460,1,'62','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',461,1,'63','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',462,1,'64','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',463,1,'65','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',464,1,'66','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',465,1,'67','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',466,1,'68','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',467,1,'69','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',468,1,'70','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',469,1,'71','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',470,1,'72','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',471,1,'73','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',472,1,'74','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',473,1,'75','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',474,1,'76','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',475,1,'77','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',476,1,'78','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',477,1,'79','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',478,1,'80','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',479,1,'81','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',480,1,'82','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',481,1,'83','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',482,1,'84','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',483,1,'85','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',484,1,'86','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',485,1,'87','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',486,1,'88','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',487,1,'89','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',488,1,'90','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',489,1,'91','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',490,1,'92','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',491,1,'93','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',492,1,'94','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',493,1,'95','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',494,1,'96','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',495,1,'97','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',496,1,'98','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',497,1,'99','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',498,1,'100','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',499,1,'101','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',500,1,'102','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',501,1,'103','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',502,1,'104','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',503,1,'105','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',504,1,'106','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',505,1,'107','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',506,1,'108','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',507,1,'109','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',508,1,'110','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',509,1,'111','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',510,1,'112','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',511,1,'113','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',512,1,'114','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',513,1,'115','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',514,1,'116','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',515,1,'117','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',516,1,'118','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',517,1,'119','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',518,1,'120','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',519,1,'121','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',520,1,'122','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',521,1,'123','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',522,1,'124','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',523,1,'125','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',524,1,'126','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',525,1,'127','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',526,1,'128','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',527,1,'129','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',528,1,'130','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',529,1,'131','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',530,1,'132','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',531,1,'133','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',532,1,'134','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',533,1,'135','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',534,1,'136','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',535,1,'137','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',536,1,'138','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',537,1,'139','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',538,1,'140','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',539,1,'141','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',540,1,'142','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',541,1,'143','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',542,1,'144','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',543,1,'145','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',544,1,'146','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',545,1,'147','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',546,1,'148','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',547,1,'149','激活'),('5e4d0a6d39a44b9c906a3173b448aa4a',548,1,'150','激活');
/*!40000 ALTER TABLE `mappings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media` (
  `tenantid` varchar(64) DEFAULT '0',
  `mediaid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `mediatypeid` bigint(20) unsigned NOT NULL,
  `sendto` varchar(100) NOT NULL DEFAULT '',
  `active` int(11) NOT NULL DEFAULT '0',
  `severity` int(11) NOT NULL DEFAULT '63',
  `period` varchar(100) NOT NULL DEFAULT '1-7,00:00-24:00',
  PRIMARY KEY (`mediaid`),
  KEY `media_1` (`userid`),
  KEY `media_2` (`mediatypeid`),
  CONSTRAINT `c_media_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE,
  CONSTRAINT `c_media_2` FOREIGN KEY (`mediatypeid`) REFERENCES `media_type` (`mediatypeid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media`
--

LOCK TABLES `media` WRITE;
/*!40000 ALTER TABLE `media` DISABLE KEYS */;
/*!40000 ALTER TABLE `media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_type`
--

DROP TABLE IF EXISTS `media_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `media_type` (
  `tenantid` varchar(64) DEFAULT '0',
  `mediatypeid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `description` varchar(100) NOT NULL DEFAULT '',
  `smtp_server` varchar(255) NOT NULL DEFAULT '',
  `smtp_helo` varchar(255) NOT NULL DEFAULT '',
  `smtp_email` varchar(255) NOT NULL DEFAULT '',
  `exec_path` varchar(255) NOT NULL DEFAULT '',
  `gsm_modem` varchar(255) NOT NULL DEFAULT '',
  `username` varchar(255) NOT NULL DEFAULT '',
  `passwd` varchar(255) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`mediatypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_type`
--

LOCK TABLES `media_type` WRITE;
/*!40000 ALTER TABLE `media_type` DISABLE KEYS */;
INSERT INTO `media_type` VALUES ('-',1,1,'电子邮件','','','','imon_mail.sh','','','',0),('-',2,1,'手机短信','','','','imon_sms.sh','','','',1);
/*!40000 ALTER TABLE `media_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_cksum`
--

DROP TABLE IF EXISTS `node_cksum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `node_cksum` (
  `nodeid` int(11) NOT NULL,
  `tablename` varchar(64) NOT NULL DEFAULT '',
  `recordid` bigint(20) unsigned NOT NULL,
  `cksumtype` int(11) NOT NULL DEFAULT '0',
  `cksum` text NOT NULL,
  `sync` char(128) NOT NULL DEFAULT '',
  KEY `node_cksum_1` (`nodeid`,`cksumtype`,`tablename`,`recordid`),
  CONSTRAINT `c_node_cksum_1` FOREIGN KEY (`nodeid`) REFERENCES `nodes` (`nodeid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_cksum`
--

LOCK TABLES `node_cksum` WRITE;
/*!40000 ALTER TABLE `node_cksum` DISABLE KEYS */;
/*!40000 ALTER TABLE `node_cksum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nodes`
--

DROP TABLE IF EXISTS `nodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nodes` (
  `nodeid` int(11) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '0',
  `ip` varchar(39) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '10051',
  `nodetype` int(11) NOT NULL DEFAULT '0',
  `masterid` int(11) DEFAULT NULL,
  PRIMARY KEY (`nodeid`),
  KEY `nodes_1` (`masterid`),
  CONSTRAINT `c_nodes_1` FOREIGN KEY (`masterid`) REFERENCES `nodes` (`nodeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nodes`
--

LOCK TABLES `nodes` WRITE;
/*!40000 ALTER TABLE `nodes` DISABLE KEYS */;
/*!40000 ALTER TABLE `nodes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opcommand`
--

DROP TABLE IF EXISTS `opcommand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opcommand` (
  `tenantid` varchar(64) DEFAULT '0',
  `operationid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `scriptid` bigint(20) unsigned DEFAULT NULL,
  `execute_on` int(11) NOT NULL DEFAULT '0',
  `port` varchar(64) NOT NULL DEFAULT '',
  `authtype` int(11) NOT NULL DEFAULT '0',
  `username` varchar(64) NOT NULL DEFAULT '',
  `password` varchar(64) NOT NULL DEFAULT '',
  `publickey` varchar(64) NOT NULL DEFAULT '',
  `privatekey` varchar(64) NOT NULL DEFAULT '',
  `command` text NOT NULL,
  PRIMARY KEY (`operationid`),
  KEY `opcommand_1` (`scriptid`),
  CONSTRAINT `c_opcommand_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opcommand_2` FOREIGN KEY (`scriptid`) REFERENCES `scripts` (`scriptid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opcommand`
--

LOCK TABLES `opcommand` WRITE;
/*!40000 ALTER TABLE `opcommand` DISABLE KEYS */;
/*!40000 ALTER TABLE `opcommand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opcommand_grp`
--

DROP TABLE IF EXISTS `opcommand_grp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opcommand_grp` (
  `tenantid` varchar(64) DEFAULT '0',
  `opcommand_grpid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `groupid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`opcommand_grpid`),
  KEY `opcommand_grp_1` (`operationid`),
  KEY `opcommand_grp_2` (`groupid`),
  CONSTRAINT `c_opcommand_grp_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opcommand_grp_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opcommand_grp`
--

LOCK TABLES `opcommand_grp` WRITE;
/*!40000 ALTER TABLE `opcommand_grp` DISABLE KEYS */;
/*!40000 ALTER TABLE `opcommand_grp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opcommand_hst`
--

DROP TABLE IF EXISTS `opcommand_hst`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opcommand_hst` (
  `tenantid` varchar(64) DEFAULT '0',
  `opcommand_hstid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `hostid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`opcommand_hstid`),
  KEY `opcommand_hst_1` (`operationid`),
  KEY `opcommand_hst_2` (`hostid`),
  CONSTRAINT `c_opcommand_hst_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opcommand_hst_2` FOREIGN KEY (`hostid`) REFERENCES `hosts` (`hostid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opcommand_hst`
--

LOCK TABLES `opcommand_hst` WRITE;
/*!40000 ALTER TABLE `opcommand_hst` DISABLE KEYS */;
/*!40000 ALTER TABLE `opcommand_hst` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opconditions`
--

DROP TABLE IF EXISTS `opconditions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opconditions` (
  `tenantid` varchar(64) DEFAULT '0',
  `opconditionid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `conditiontype` int(11) NOT NULL DEFAULT '0',
  `operator` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`opconditionid`),
  KEY `opconditions_1` (`operationid`),
  CONSTRAINT `c_opconditions_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opconditions`
--

LOCK TABLES `opconditions` WRITE;
/*!40000 ALTER TABLE `opconditions` DISABLE KEYS */;
/*!40000 ALTER TABLE `opconditions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operations`
--

DROP TABLE IF EXISTS `operations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operations` (
  `tenantid` varchar(64) DEFAULT '0',
  `operationid` bigint(20) unsigned NOT NULL,
  `actionid` bigint(20) unsigned NOT NULL,
  `operationtype` int(11) NOT NULL DEFAULT '0',
  `esc_period` int(11) NOT NULL DEFAULT '0',
  `esc_step_from` int(11) NOT NULL DEFAULT '1',
  `esc_step_to` int(11) NOT NULL DEFAULT '1',
  `evaltype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`operationid`),
  KEY `operations_1` (`actionid`),
  CONSTRAINT `c_operations_1` FOREIGN KEY (`actionid`) REFERENCES `actions` (`actionid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operations`
--

LOCK TABLES `operations` WRITE;
/*!40000 ALTER TABLE `operations` DISABLE KEYS */;
/*!40000 ALTER TABLE `operations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opgroup`
--

DROP TABLE IF EXISTS `opgroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opgroup` (
  `tenantid` varchar(64) DEFAULT '0',
  `opgroupid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `groupid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`opgroupid`),
  UNIQUE KEY `opgroup_1` (`operationid`,`groupid`),
  KEY `opgroup_2` (`groupid`),
  CONSTRAINT `c_opgroup_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opgroup_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opgroup`
--

LOCK TABLES `opgroup` WRITE;
/*!40000 ALTER TABLE `opgroup` DISABLE KEYS */;
/*!40000 ALTER TABLE `opgroup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opmessage`
--

DROP TABLE IF EXISTS `opmessage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opmessage` (
  `tenantid` varchar(64) DEFAULT '0',
  `operationid` bigint(20) unsigned NOT NULL,
  `default_msg` int(11) NOT NULL DEFAULT '0',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `message` text NOT NULL,
  `mediatypeid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`operationid`),
  KEY `opmessage_1` (`mediatypeid`),
  CONSTRAINT `c_opmessage_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opmessage_2` FOREIGN KEY (`mediatypeid`) REFERENCES `media_type` (`mediatypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opmessage`
--

LOCK TABLES `opmessage` WRITE;
/*!40000 ALTER TABLE `opmessage` DISABLE KEYS */;
/*!40000 ALTER TABLE `opmessage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opmessage_grp`
--

DROP TABLE IF EXISTS `opmessage_grp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opmessage_grp` (
  `tenantid` varchar(64) DEFAULT '0',
  `opmessage_grpid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `usrgrpid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`opmessage_grpid`),
  UNIQUE KEY `opmessage_grp_1` (`operationid`,`usrgrpid`),
  KEY `opmessage_grp_2` (`usrgrpid`),
  CONSTRAINT `c_opmessage_grp_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opmessage_grp_2` FOREIGN KEY (`usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opmessage_grp`
--

LOCK TABLES `opmessage_grp` WRITE;
/*!40000 ALTER TABLE `opmessage_grp` DISABLE KEYS */;
/*!40000 ALTER TABLE `opmessage_grp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `opmessage_usr`
--

DROP TABLE IF EXISTS `opmessage_usr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opmessage_usr` (
  `tenantid` varchar(64) DEFAULT '0',
  `opmessage_usrid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  PRIMARY KEY (`opmessage_usrid`),
  UNIQUE KEY `opmessage_usr_1` (`operationid`,`userid`),
  KEY `opmessage_usr_2` (`userid`),
  CONSTRAINT `c_opmessage_usr_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opmessage_usr_2` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `opmessage_usr`
--

LOCK TABLES `opmessage_usr` WRITE;
/*!40000 ALTER TABLE `opmessage_usr` DISABLE KEYS */;
/*!40000 ALTER TABLE `opmessage_usr` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `optemplate`
--

DROP TABLE IF EXISTS `optemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `optemplate` (
  `tenantid` varchar(64) DEFAULT '0',
  `optemplateid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `templateid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`optemplateid`),
  UNIQUE KEY `optemplate_1` (`operationid`,`templateid`),
  KEY `optemplate_2` (`templateid`),
  CONSTRAINT `c_optemplate_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_optemplate_2` FOREIGN KEY (`templateid`) REFERENCES `hosts` (`hostid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `optemplate`
--

LOCK TABLES `optemplate` WRITE;
/*!40000 ALTER TABLE `optemplate` DISABLE KEYS */;
/*!40000 ALTER TABLE `optemplate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles` (
  `tenantid` varchar(64) DEFAULT '0',
  `profileid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `idx` varchar(96) NOT NULL DEFAULT '',
  `idx2` bigint(20) unsigned NOT NULL DEFAULT '0',
  `value_id` bigint(20) unsigned NOT NULL DEFAULT '0',
  `value_int` int(11) NOT NULL DEFAULT '0',
  `value_str` varchar(255) NOT NULL DEFAULT '',
  `source` varchar(96) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`profileid`),
  KEY `profiles_1` (`userid`,`idx`,`idx2`),
  KEY `profiles_2` (`userid`,`profileid`),
  CONSTRAINT `c_profiles_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profiles`
--

LOCK TABLES `profiles` WRITE;
/*!40000 ALTER TABLE `profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proxy_autoreg_host`
--

DROP TABLE IF EXISTS `proxy_autoreg_host`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy_autoreg_host` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `clock` int(11) NOT NULL DEFAULT '0',
  `host` varchar(64) NOT NULL DEFAULT '',
  `listen_ip` varchar(39) NOT NULL DEFAULT '',
  `listen_port` int(11) NOT NULL DEFAULT '0',
  `listen_dns` varchar(64) NOT NULL DEFAULT '',
  `host_metadata` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `proxy_autoreg_host_1` (`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proxy_autoreg_host`
--

LOCK TABLES `proxy_autoreg_host` WRITE;
/*!40000 ALTER TABLE `proxy_autoreg_host` DISABLE KEYS */;
/*!40000 ALTER TABLE `proxy_autoreg_host` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proxy_dhistory`
--

DROP TABLE IF EXISTS `proxy_dhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy_dhistory` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `clock` int(11) NOT NULL DEFAULT '0',
  `druleid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `ip` varchar(39) NOT NULL DEFAULT '',
  `port` int(11) NOT NULL DEFAULT '0',
  `key_` varchar(255) NOT NULL DEFAULT '',
  `value` varchar(255) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `dcheckid` bigint(20) unsigned DEFAULT NULL,
  `dns` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `proxy_dhistory_1` (`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proxy_dhistory`
--

LOCK TABLES `proxy_dhistory` WRITE;
/*!40000 ALTER TABLE `proxy_dhistory` DISABLE KEYS */;
/*!40000 ALTER TABLE `proxy_dhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proxy_history`
--

DROP TABLE IF EXISTS `proxy_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `proxy_history` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `timestamp` int(11) NOT NULL DEFAULT '0',
  `source` varchar(64) NOT NULL DEFAULT '',
  `severity` int(11) NOT NULL DEFAULT '0',
  `value` longtext NOT NULL,
  `logeventid` int(11) NOT NULL DEFAULT '0',
  `ns` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `proxy_history_1` (`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proxy_history`
--

LOCK TABLES `proxy_history` WRITE;
/*!40000 ALTER TABLE `proxy_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `proxy_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `regexps`
--

DROP TABLE IF EXISTS `regexps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `regexps` (
  `tenantid` varchar(64) DEFAULT '0',
  `regexpid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `test_string` text NOT NULL,
  PRIMARY KEY (`regexpid`),
  KEY `regexps_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `regexps`
--

LOCK TABLES `regexps` WRITE;
/*!40000 ALTER TABLE `regexps` DISABLE KEYS */;
INSERT INTO `regexps` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,'File systems for discovery','ext3'),('5e4d0a6d39a44b9c906a3173b448aa4a',2,'Network interfaces for discovery','eth0'),('5e4d0a6d39a44b9c906a3173b448aa4a',3,'Storage devices for SNMP discovery','/boot'),('5e4d0a6d39a44b9c906a3173b448aa4a',4,'Disk_Discovery',''),('5e4d0a6d39a44b9c906a3173b448aa4a',5,'If_Discovery',''),('5e4d0a6d39a44b9c906a3173b448aa4a',6,'CPU_Discovery',''),('5e4d0a6d39a44b9c906a3173b448aa4a',7,'Memory_Discovery',''),('5e4d0a6d39a44b9c906a3173b448aa4a',8,'Patition_Discovery',''),('5e4d0a6d39a44b9c906a3173b448aa4a',9,'If_Discovery_windows',''),('5e4d0a6d39a44b9c906a3173b448aa4a',10,'Patition_Discovery_windows','');
/*!40000 ALTER TABLE `regexps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rights`
--

DROP TABLE IF EXISTS `rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rights` (
  `tenantid` varchar(64) DEFAULT '0',
  `rightid` bigint(20) unsigned NOT NULL,
  `groupid` bigint(20) unsigned NOT NULL,
  `permission` int(11) NOT NULL DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`rightid`),
  KEY `rights_1` (`groupid`),
  KEY `rights_2` (`id`),
  CONSTRAINT `c_rights_1` FOREIGN KEY (`groupid`) REFERENCES `usrgrp` (`usrgrpid`) ON DELETE CASCADE,
  CONSTRAINT `c_rights_2` FOREIGN KEY (`id`) REFERENCES `groups` (`groupid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rights`
--

LOCK TABLES `rights` WRITE;
/*!40000 ALTER TABLE `rights` DISABLE KEYS */;
INSERT INTO `rights` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,7,3,33),('5e4d0a6d39a44b9c906a3173b448aa4a',2,7,3,32),('5e4d0a6d39a44b9c906a3173b448aa4a',3,7,3,5),('5e4d0a6d39a44b9c906a3173b448aa4a',4,7,3,19),('5e4d0a6d39a44b9c906a3173b448aa4a',5,7,3,26),('5e4d0a6d39a44b9c906a3173b448aa4a',6,7,3,25),('5e4d0a6d39a44b9c906a3173b448aa4a',7,7,3,27),('5e4d0a6d39a44b9c906a3173b448aa4a',8,7,3,23),('5e4d0a6d39a44b9c906a3173b448aa4a',9,7,3,24),('5e4d0a6d39a44b9c906a3173b448aa4a',10,7,3,20),('5e4d0a6d39a44b9c906a3173b448aa4a',11,7,3,30),('5e4d0a6d39a44b9c906a3173b448aa4a',12,7,3,29),('5e4d0a6d39a44b9c906a3173b448aa4a',13,7,3,22),('5e4d0a6d39a44b9c906a3173b448aa4a',14,7,3,21),('5e4d0a6d39a44b9c906a3173b448aa4a',15,7,3,28),('5e4d0a6d39a44b9c906a3173b448aa4a',16,7,3,5),('5e4d0a6d39a44b9c906a3173b448aa4a',17,7,3,19),('5e4d0a6d39a44b9c906a3173b448aa4a',18,7,3,26),('5e4d0a6d39a44b9c906a3173b448aa4a',19,7,3,25),('5e4d0a6d39a44b9c906a3173b448aa4a',20,7,3,27),('5e4d0a6d39a44b9c906a3173b448aa4a',21,7,3,23),('5e4d0a6d39a44b9c906a3173b448aa4a',22,7,3,24),('5e4d0a6d39a44b9c906a3173b448aa4a',23,7,3,32),('5e4d0a6d39a44b9c906a3173b448aa4a',24,7,3,33),('5e4d0a6d39a44b9c906a3173b448aa4a',25,7,3,20),('5e4d0a6d39a44b9c906a3173b448aa4a',26,7,3,30),('5e4d0a6d39a44b9c906a3173b448aa4a',27,7,3,29),('5e4d0a6d39a44b9c906a3173b448aa4a',28,7,3,22),('5e4d0a6d39a44b9c906a3173b448aa4a',29,7,3,21),('5e4d0a6d39a44b9c906a3173b448aa4a',30,7,3,28),('3',31,7,3,5),('3',32,7,3,19),('3',33,7,3,701),('3',34,7,3,602),('3',35,7,3,601),('3',36,7,3,201),('3',37,7,3,803),('3',38,7,3,801),('3',39,7,3,804),('3',40,7,3,802),('3',41,7,3,805),('3',42,7,3,401),('3',43,7,3,504),('3',44,7,3,506),('3',45,7,3,501),('3',46,7,3,502),('3',47,7,3,505),('3',48,7,3,503),('3',49,7,3,101),('3',50,7,3,102),('3',52,7,3,301),('3',53,7,3,302),('4',55,7,3,5),('4',56,7,3,19),('4',57,7,3,701),('4',58,7,3,602),('4',59,7,3,601),('4',60,7,3,201),('4',61,7,3,803),('4',62,7,3,801),('4',63,7,3,804),('4',64,7,3,802),('4',65,7,3,805),('4',66,7,3,401),('4',67,7,3,504),('4',68,7,3,506),('4',69,7,3,501),('4',70,7,3,502),('4',71,7,3,505),('4',72,7,3,503),('4',73,7,3,101),('4',74,7,3,102),('4',76,7,3,301),('4',77,7,3,302),('5',79,7,3,5),('5',80,7,3,19),('5',81,7,3,701),('5',82,7,3,602),('5',83,7,3,601),('5',84,7,3,201),('5',85,7,3,803),('5',86,7,3,801),('5',87,7,3,804),('5',88,7,3,802),('5',89,7,3,805),('5',90,7,3,401),('5',91,7,3,504),('5',92,7,3,506),('5',93,7,3,501),('5',94,7,3,502),('5',95,7,3,505),('5',96,7,3,503),('5',97,7,3,101),('5',98,7,3,102),('5',100,7,3,301),('5',101,7,3,302),('6',103,7,3,5),('6',104,7,3,19),('6',105,7,3,701),('6',106,7,3,602),('6',107,7,3,601),('6',108,7,3,201),('6',109,7,3,803),('6',110,7,3,801),('6',111,7,3,804),('6',112,7,3,802),('6',113,7,3,805),('6',114,7,3,401),('6',115,7,3,504),('6',116,7,3,506),('6',117,7,3,501),('6',118,7,3,502),('6',119,7,3,505),('6',120,7,3,503),('6',121,7,3,101),('6',122,7,3,102),('6',124,7,3,301),('6',125,7,3,302),('7',127,7,3,5),('7',128,7,3,19),('7',129,7,3,701),('7',130,7,3,602),('7',131,7,3,601),('7',132,7,3,201),('7',133,7,3,803),('7',134,7,3,801),('7',135,7,3,804),('7',136,7,3,802),('7',137,7,3,805),('7',138,7,3,401),('7',139,7,3,504),('7',140,7,3,506),('7',141,7,3,501),('7',142,7,3,502),('7',143,7,3,505),('7',144,7,3,503),('7',145,7,3,101),('7',146,7,3,102),('7',148,7,3,301),('7',149,7,3,302),('8',151,7,3,5),('8',152,7,3,19),('8',153,7,3,701),('8',154,7,3,602),('8',155,7,3,601),('8',156,7,3,201),('8',157,7,3,803),('8',158,7,3,801),('8',159,7,3,804),('8',160,7,3,802),('8',161,7,3,805),('8',162,7,3,401),('8',163,7,3,504),('8',164,7,3,506),('8',165,7,3,501),('8',166,7,3,502),('8',167,7,3,505),('8',168,7,3,503),('8',169,7,3,101),('8',170,7,3,102),('8',172,7,3,301),('8',173,7,3,302);
/*!40000 ALTER TABLE `rights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `screens`
--

DROP TABLE IF EXISTS `screens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `screens` (
  `tenantid` varchar(64) DEFAULT '0',
  `screenid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `hsize` int(11) NOT NULL DEFAULT '1',
  `vsize` int(11) NOT NULL DEFAULT '1',
  `templateid` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`screenid`),
  KEY `screens_1` (`templateid`),
  CONSTRAINT `c_screens_1` FOREIGN KEY (`templateid`) REFERENCES `hosts` (`hostid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `screens`
--

LOCK TABLES `screens` WRITE;
/*!40000 ALTER TABLE `screens` DISABLE KEYS */;
/*!40000 ALTER TABLE `screens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `screens_items`
--

DROP TABLE IF EXISTS `screens_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `screens_items` (
  `tenantid` varchar(64) DEFAULT '0',
  `screenitemid` bigint(20) unsigned NOT NULL,
  `screenid` bigint(20) unsigned NOT NULL,
  `resourcetype` int(11) NOT NULL DEFAULT '0',
  `resourceid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `width` int(11) NOT NULL DEFAULT '320',
  `height` int(11) NOT NULL DEFAULT '200',
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `colspan` int(11) NOT NULL DEFAULT '0',
  `rowspan` int(11) NOT NULL DEFAULT '0',
  `elements` int(11) NOT NULL DEFAULT '25',
  `valign` int(11) NOT NULL DEFAULT '0',
  `halign` int(11) NOT NULL DEFAULT '0',
  `style` int(11) NOT NULL DEFAULT '0',
  `url` varchar(255) NOT NULL DEFAULT '',
  `dynamic` int(11) NOT NULL DEFAULT '0',
  `sort_triggers` int(11) NOT NULL DEFAULT '0',
  `application` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`screenitemid`),
  KEY `screens_items_1` (`screenid`),
  CONSTRAINT `c_screens_items_1` FOREIGN KEY (`screenid`) REFERENCES `screens` (`screenid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `screens_items`
--

LOCK TABLES `screens_items` WRITE;
/*!40000 ALTER TABLE `screens_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `screens_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scripts`
--

DROP TABLE IF EXISTS `scripts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `scripts` (
  `tenantid` varchar(64) DEFAULT '0',
  `scriptid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `command` varchar(255) NOT NULL DEFAULT '',
  `host_access` int(11) NOT NULL DEFAULT '2',
  `usrgrpid` bigint(20) unsigned DEFAULT NULL,
  `groupid` bigint(20) unsigned DEFAULT NULL,
  `description` text NOT NULL,
  `confirmation` varchar(255) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL DEFAULT '0',
  `execute_on` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`scriptid`),
  KEY `scripts_1` (`usrgrpid`),
  KEY `scripts_2` (`groupid`),
  CONSTRAINT `c_scripts_1` FOREIGN KEY (`usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`),
  CONSTRAINT `c_scripts_2` FOREIGN KEY (`groupid`) REFERENCES `groups` (`groupid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scripts`
--

LOCK TABLES `scripts` WRITE;
/*!40000 ALTER TABLE `scripts` DISABLE KEYS */;
INSERT INTO `scripts` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,'Ping','/bin/ping -c 3 {HOST.CONN} 2>&1',2,NULL,NULL,'','',0,1),('5e4d0a6d39a44b9c906a3173b448aa4a',2,'Traceroute','/bin/traceroute {HOST.CONN} 2>&1',2,NULL,NULL,'','',0,1),('5e4d0a6d39a44b9c906a3173b448aa4a',3,'Detect operating system','sudo /usr/bin/nmap -O {HOST.CONN} 2>&1',2,7,NULL,'','',0,1);
/*!40000 ALTER TABLE `scripts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_alarms`
--

DROP TABLE IF EXISTS `service_alarms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_alarms` (
  `tenantid` varchar(64) DEFAULT '0',
  `servicealarmid` bigint(20) unsigned NOT NULL,
  `serviceid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `value` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`servicealarmid`),
  KEY `service_alarms_1` (`serviceid`,`clock`),
  KEY `service_alarms_2` (`clock`),
  CONSTRAINT `c_service_alarms_1` FOREIGN KEY (`serviceid`) REFERENCES `services` (`serviceid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_alarms`
--

LOCK TABLES `service_alarms` WRITE;
/*!40000 ALTER TABLE `service_alarms` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_alarms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services` (
  `tenantid` varchar(64) DEFAULT '0',
  `serviceid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `algorithm` int(11) NOT NULL DEFAULT '0',
  `triggerid` bigint(20) unsigned DEFAULT NULL,
  `showsla` int(11) NOT NULL DEFAULT '0',
  `goodsla` double(16,4) NOT NULL DEFAULT '99.9000',
  `sortorder` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`serviceid`),
  KEY `services_1` (`triggerid`),
  CONSTRAINT `c_services_1` FOREIGN KEY (`triggerid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_links`
--

DROP TABLE IF EXISTS `services_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_links` (
  `tenantid` varchar(64) DEFAULT '0',
  `linkid` bigint(20) unsigned NOT NULL,
  `serviceupid` bigint(20) unsigned NOT NULL,
  `servicedownid` bigint(20) unsigned NOT NULL,
  `soft` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`linkid`),
  UNIQUE KEY `services_links_2` (`serviceupid`,`servicedownid`),
  KEY `services_links_1` (`servicedownid`),
  CONSTRAINT `c_services_links_1` FOREIGN KEY (`serviceupid`) REFERENCES `services` (`serviceid`) ON DELETE CASCADE,
  CONSTRAINT `c_services_links_2` FOREIGN KEY (`servicedownid`) REFERENCES `services` (`serviceid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_links`
--

LOCK TABLES `services_links` WRITE;
/*!40000 ALTER TABLE `services_links` DISABLE KEYS */;
/*!40000 ALTER TABLE `services_links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services_times`
--

DROP TABLE IF EXISTS `services_times`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `services_times` (
  `tenantid` varchar(64) DEFAULT '0',
  `timeid` bigint(20) unsigned NOT NULL,
  `serviceid` bigint(20) unsigned NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `ts_from` int(11) NOT NULL DEFAULT '0',
  `ts_to` int(11) NOT NULL DEFAULT '0',
  `note` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`timeid`),
  KEY `services_times_1` (`serviceid`,`type`,`ts_from`,`ts_to`),
  CONSTRAINT `c_services_times_1` FOREIGN KEY (`serviceid`) REFERENCES `services` (`serviceid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services_times`
--

LOCK TABLES `services_times` WRITE;
/*!40000 ALTER TABLE `services_times` DISABLE KEYS */;
/*!40000 ALTER TABLE `services_times` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `tenantid` varchar(64) DEFAULT '0',
  `sessionid` varchar(32) NOT NULL DEFAULT '',
  `userid` varchar(64) NOT NULL,
  `lastaccess` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sessionid`),
  KEY `sessions_1` (`userid`,`status`),
  CONSTRAINT `c_sessions_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slides`
--

DROP TABLE IF EXISTS `slides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `slides` (
  `tenantid` varchar(64) DEFAULT '0',
  `slideid` bigint(20) unsigned NOT NULL,
  `slideshowid` bigint(20) unsigned NOT NULL,
  `screenid` bigint(20) unsigned NOT NULL,
  `step` int(11) NOT NULL DEFAULT '0',
  `delay` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`slideid`),
  KEY `slides_1` (`slideshowid`),
  KEY `slides_2` (`screenid`),
  CONSTRAINT `c_slides_1` FOREIGN KEY (`slideshowid`) REFERENCES `slideshows` (`slideshowid`) ON DELETE CASCADE,
  CONSTRAINT `c_slides_2` FOREIGN KEY (`screenid`) REFERENCES `screens` (`screenid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slides`
--

LOCK TABLES `slides` WRITE;
/*!40000 ALTER TABLE `slides` DISABLE KEYS */;
/*!40000 ALTER TABLE `slides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slideshows`
--

DROP TABLE IF EXISTS `slideshows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `slideshows` (
  `tenantid` varchar(64) DEFAULT '0',
  `slideshowid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  `delay` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`slideshowid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slideshows`
--

LOCK TABLES `slideshows` WRITE;
/*!40000 ALTER TABLE `slideshows` DISABLE KEYS */;
/*!40000 ALTER TABLE `slideshows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dict` (
  `id` int(10) NOT NULL,
  `type` varchar(40) NOT NULL,
  `dlabel` varchar(40) NOT NULL,
  `dkey` varchar(40) NOT NULL,
  `status` char(1) NOT NULL,
  `seq_no` int(4) NOT NULL,
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_type_status` (`type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict` DISABLE KEYS */;
INSERT INTO `sys_dict` VALUES (5101,'PM_TOPO_PIC_STATUS','机柜图片','cabinet','1',1,'拓扑图片类型:机柜图片'),(5102,'PM_TOPO_PIC_STATUS','背景图片','backgroup','1',2,'拓扑图片类型:背景图片'),(5103,'PM_TOPO_PIC_STATUS','机房图片','room','1',3,'拓扑图片类型:机房图片'),(5201,'PM_TOPO_PUBLIC_STATUS','公开','Y','1',1,'拓扑公开状态:公开'),(5202,'PM_TOPO_PUBLIC_STATUS','不公开','N','1',2,'拓扑公开状态:不公开'),(5301,'PM_TOPO_TYPE','网络链路拓扑','nettopo','1',1,'拓扑类型:网络拓扑'),(5302,'PM_TOPO_TYPE','机柜拓扑','cabtopo','1',2,'拓扑类型:机房拓扑'),(5303,'PM_TOPO_TYPE','主从拓扑','hosttopo','1',3,'拓扑类型:主从拓扑'),(5304,'PM_TOPO_TYPE','业务拓扑','biztopo','1',4,'拓扑类型:业务拓扑'),(5305,'PM_TOPO_TYPE','虚拟链路拓扑','virtlinktopo','1',5,'拓扑类型:虚拟链路拓扑');
/*!40000 ALTER TABLE `sys_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_func`
--

DROP TABLE IF EXISTS `sys_func`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_func` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT '功能ID',
  `pid` varchar(50) NOT NULL DEFAULT '-1' COMMENT '父功能ID',
  `func_name` varchar(50) NOT NULL DEFAULT '' COMMENT '功能名称',
  `func_id` varchar(50) DEFAULT '' COMMENT '功能号',
  `func_url` varchar(100) NOT NULL DEFAULT '' COMMENT '功能URL',
  `entrance` char(1) NOT NULL DEFAULT 'N' COMMENT '是否入口',
  `leaf` char(1) NOT NULL DEFAULT 'Y' COMMENT '是否叶子',
  `render_type` varchar(20) DEFAULT '' COMMENT '菜单渲染类型',
  `render_style` varchar(40) DEFAULT '' COMMENT '菜单渲染样式',
  `render_url` varchar(100) DEFAULT '' COMMENT '菜单加载地址',
  `icon` varchar(50) DEFAULT '' COMMENT '菜单图标样式',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `seq_no` int(4) NOT NULL DEFAULT '1' COMMENT '显示顺序',
  `role` int(2) NOT NULL DEFAULT '0' COMMENT '父功能ID',
  `note` varchar(50) DEFAULT '' COMMENT '所属角色',
  PRIMARY KEY (`id`,`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_func`
--

LOCK TABLES `sys_func` WRITE;
/*!40000 ALTER TABLE `sys_func` DISABLE KEYS */;
INSERT INTO `sys_func` VALUES ('0001','-1','策略中心','strategy','','Y','N','tree','simple','','strategy','1',1000001,2,''),('00010001','0001','发现策略','discovery','/platform/iradar/discoveryconf.action','Y','Y','','','','discovery','1',1000001,2,''),('00010002','0001','告警策略','analysis','','Y','Y','','','','analysis','1',1000002,2,''),('000100020001','00010002','告警规则','triggers','/platform/iradar/triggers.action','Y','Y','','','','triggers','1',1000001,2,''),('000100020002','00010002','告警响应','triggerAct','/platform/iradar/triggerAct.action','Y','Y','','','','triggerAct','1',1000002,2,''),('00010003','0001','监控模型','templates','/platform/iradar/templates.action','Y','Y','','','','templates','1',1000003,2,''),('00010004','0001','WEB监控策略','web_mon','/platform/iradar/httpconf.action','Y','Y','','','','web_mon','1',1000004,2,''),('0002','-1','设备中心','host','','Y','N','tree','simple','','host','1',1000002,2,''),('00020001','0002','设备监控配置','hostMonitor','/platform/iradar/hostMonitor.action','Y','Y','','','','hostMonitor','1',1000001,2,''),('00020002','0002','资产列表','assetList','/platform/iradar/hostinventories.action','Y','Y','','','','assetList','1',1000002,2,''),('00020003','0002','维护计划','maintenance','/platform/iradar/maintenance.action','Y','Y','','','','maintenance','1',1000003,2,''),('00020004','0002','监控类型','hostgroups','/platform/iradar/hostgroups.action','Y','Y','','','','hostgroups','1',1000004,2,''),('0003','-1','监控中心','monitor','','Y','N','tree','simple','','monitor','1',1000003,2,''),('00030001','0003','物理设备','mon_phy','','Y','Y','','','','mon_phy accordion_title','1',1000001,2,''),('00030002','0003','服务器','mon_server','','Y','Y','','','','mon_server','1',1000002,2,''),('000300020001','00030002','Windows系列','windows_server','/platform/iradar/windows_mon_server.action','Y','Y','','','','windows_mon_server','1',1000001,2,''),('000300020002','00030002','Linux系列','linux_server','/platform/iradar/linux_mon_server.action','Y','Y','','','','linux_mon_server','1',1000002,2,''),('00030003','0003','网络设备','mon_net','','Y','Y','','','','mon_net','1',1000003,2,''),('000300030001','00030003','Cisco系列','cisco_net','/platform/iradar/mon_net_cisco.action','Y','Y','','','','cisco_mon_net','1',1000001,2,''),('000300030002','00030003','华为交换机','huawei_net','/platform/iradar/mon_net_huawei_switch.action','Y','Y','','','','huawei_mon_net','1',1000002,2,''),('000300030003','00030003','中兴网络设备','zhongxing_net','/platform/iradar/mon_net_zhongxing_switch_latest.action','Y','Y','','','','zhongxing_mon_net','1',1000003,2,''),('000300030004','00030003','通用网络设备','common_net','/platform/iradar/mon_common_net_host.action','Y','Y','','','','common_mon_net','1',1000004,2,''),('00030004','0003','存储设备','mon_storage','','Y','Y','','','','mon_storage','1',1000004,2,''),('00030005','0003','虚拟资源','mon_virt','','Y','Y','','','','mon_virt accordion_title','1',1000005,2,''),('00030006','0003','云主机','mon_vm','/platform/iradar/mon_vm.action','Y','Y','','','','mon_vm','1',1000006,2,''),('00030007','0003','平台服务','mon_cloud','','Y','Y','','','','mon_cloud','1',1000007,2,''),('000300070001','00030007','控制服务','mon_cloud_control','/platform/iradar/mon_cloud_control.action','Y','Y','','','','','1',1000001,2,''),('000300070002','00030007','计算服务','mon_cloud_compute','/platform/iradar/mon_cloud_compute.action','Y','Y','','','','','1',1000002,2,''),('000300070003','00030007','存储服务','mon_cloud_ceph','/platform/iradar/mon_cloud_ceph.action','Y','Y','','','','','1',1000003,2,''),('000300070004','00030007','网络服务','mon_cloud_network','/platform/iradar/mon_cloud_network.action','Y','Y','','','','','1',1000004,2,''),('000300070005','00030007','门户服务','mon_cloud_web','/platform/iradar/mon_cloud_web.action','Y','Y','','','','','1',1000005,2,''),('00030008','0003','业务系统','mon_business','','Y','Y','','','','mon_business accordion_title','1',1000008,2,''),('00030009','0003','数据库','mon_db','','Y','Y','','','','mon_db','1',1000009,2,''),('000300090001','00030009','MySQL','mysql_db','/platform/iradar/mon_db_mysql.action','Y','Y','','','','mon_db_mysql','1',1000001,2,''),('000300090002','00030009','Oracle','oracle_db','/platform/iradar/mon_db_oracle.action','Y','Y','','','','mon_db_oracle','1',1000002,2,''),('000300090003','00030009','MSSQL','sqlserver_db','/platform/iradar/mon_db_sqlserver.action','Y','Y','','','','mon_db_sqlserver','1',1000003,2,''),('000300090004','00030009','MongoDB','mongodb_db','/platform/iradar/mon_db_mongo.action','Y','Y','','','','mon_db_mongodb','1',1000004,2,''),('000300090005','00030009','DB2','db2_db','/platform/iradar/mon_db_db2.action','Y','Y','','','','mon_db_db2','1',1000005,2,''),('00030010','0003','中间件','mon_middle','','Y','Y','','','','mon_middle','1',1000010,2,''),('000300100001','00030010','Tomcat','tomcat_middle','/platform/iradar/mon_middle_tomcat.action','Y','Y','','','','mon_middle_tomcat','1',1000001,2,''),('000300100002','00030010','WebLogic','weblogic_middle','/platform/iradar/mon_middle_weblogic.action','Y','Y','','','','mon_middle_weblogic','1',1000002,2,''),('000300100003','00030010','WebSphere','websphere_middle','/platform/iradar/mon_middle_websphere.action','Y','Y','','','','mon_middle_websphere','1',1000003,2,''),('000300100004','00030010','IIS','iis_middle','/platform/iradar/mon_middle_iis.action','Y','Y','','','','mon_middle_iis','1',1000004,2,''),('00030011','0003','Web服务','mon_web','/platform/iradar/mon_web.action','Y','Y','','','','mon_web','1',1000011,2,''),('00030012','0003','业务系统','mon_tenant_business','','Y','Y','','','','mon_tenant_business','1',1000012,2,''),('00030013','0003','其他','mon_other','','Y','Y','','','','mon_other accordion_title','1',1000013,2,''),('00030014','0003','其他','mon_others','','Y','Y','','','','mon_others','1',1000014,2,''),('0004','-1','告警中心','events','/platform/iradar/events.action','Y','Y','','','','event','1',1000004,2,''),('00040001','0004','活动告警','activealarm','/platform/iradar/activealarm.action','Y','Y','','','','activealarm','1',1000001,2,''),('00040002','0004','历史告警','historyalarm','/platform/iradar/historyalarm.action','Y','Y','','','','historyalarm','1',1000002,2,''),('0005','-1','报表中心','report','','Y','N','tree','simple','','report','1',1000005,2,''),('00050001','0005','巡检任务','inspectionReport','/platform/iradar/inspectionReport.action','Y','Y','','','','inspectionReport','1',1000001,2,''),('00050002','0005','性能报表','performance_report','/platform/iradar/performance_report.action','Y','Y','','','','performance_report','1',1000002,2,''),('00050003','0005','告警报表','eventsreport','/platform/iradar/events_report.action','Y','Y','','','','eventsreport','1',1000003,2,''),('00050004','0005','资产报表','inventories_report','/platform/iradar/intvoisport.action','Y','Y','','','','inventories_report','1',1000004,2,''),('00050005','0005','业务报表','business_report','/platform/iradar/business_report.action','Y','Y','','','','business_report','1',1000005,2,''),('0006','-1','拓扑中心','topology','','Y','N','tree','simple','','topology','1',1000006,2,''),('00060001','0006','物理链路拓扑','topo_net','/platform/iradar/NetTopoPhyIndex.action','Y','Y','','','','topo_net','1',1000001,2,''),('00060002','0006','虚拟链路拓扑','topo_virt','/platform/iradar/VirtLinkTopoIndex.action','Y','Y','','','','topo_virt','1',1000002,2,''),('00060003','0006','机房拓扑','topo_computer_room','/platform/iradar/CabTopoTopoIndex.action','Y','Y','','','','topo_computer_room','1',1000003,2,''),('00060004','0006','业务拓扑','topo_admin_biz','/platform/iradar/BizTopoAdminIndex.action','Y','Y','','','','topo_admin_biz','1',1000004,2,''),('0007','-1','工具管理','script','','Y','N','tree','simple','','script','1',1000007,2,''),('00070001','0007','脚本管理','scripts','/platform/iradar/scripts.action','Y','Y','','','','scripts','1',1000001,2,''),('00070002','0007','监控代理','proxies','/platform/iradar/proxies.action','Y','Y','','','','proxies','1',1000002,2,''),('0008','-1','系统管理','system','','Y','N','tree','simple','','system','1',1000008,2,''),('00080001','0008','用户参数','users','/platform/iradar/users.action','Y','Y','','','','users','1',1000001,2,''),('00080002','0008','平台参数','adm.gui','/platform/iradar/adm.gui.action','Y','Y','','','','adm.gui','1',1000002,2,''),('00080003','0008','系统日志','auditlogs','/platform/iradar/auditlogs.action','Y','Y','','','','auditlogs','1',1000003,2,''),('00080004','0008','公告管理','announce','/platform/iradar/announce.action','Y','Y','','','','announce','1',1000004,2,''),('00080005','0008','监控大屏','screens','/platform/iradar/screenconf.action','Y','Y','','','','','1',1000005,2,''),('0009','-1','监控中心','monitor','','Y','N','tree','simple','','monitor','1',1000009,1,''),('00090001','0009','云主机监控','tmon_vm','/platform/iradar/tmon_vm.action','Y','Y','','','','tmon_vm','1',1000001,1,''),('00090002','0009','服务应用监控','tser_app','','Y','Y','','','','tser_app','1',1000002,1,''),('000900020001','00090002','Tomcat','app_tomcat','/platform/iradar/app_tomcat.action','Y','Y','','','','app_tomcat','1',1000001,1,''),('000900020002','00090002','MySQL','app_mysql','/platform/iradar/app_mysql.action','Y','Y','','','','app_mysql','1',1000002,1,''),('000900020003','00090002','简单应用','app_simple','/platform/iradar/app_simple.action','Y','Y','','','','app_simple','1',1000003,1,''),('00090003','0009','网站监控','thttpconf','/platform/iradar/thttpconf.action','Y','Y','','','','mon_client_app','1',1000003,1,''),('0010','-1','告警中心','event','','Y','N','tree','simple','','event','1',1000010,1,''),('00100001','0010','活动告警','tactivealarm','/platform/iradar/tactivealarm.action','Y','Y','','','','tactivealarm','1',1000001,1,''),('00100002','0010','历史告警','thistoryalarm','/platform/iradar/thistoryalarm.action','Y','Y','','','','thistoryalarm','1',1000002,1,''),('0011','-1','报表中心','report','','Y','N','tree','simple','','report','1',1000011,1,''),('00110001','0011','业务报表','tbusiness_report','/platform/iradar/tbusiness_report.action','Y','Y','','','','tbusiness_report','1',1000001,1,''),('0012','-1','拓扑中心','topology','','Y','N','tree','simple','','topology','1',1000012,1,''),('00120001','0012','业务拓扑','topo_client_business','/platform/iradar/BizTopoTenantIndex.action','Y','Y','','','','topo_client_business','1',1000001,1,''),('00120002','0012','虚拟链路拓扑','topo_client_virtlink','/platform/iradar/VirtLinkTopoTenantIndex.action','Y','Y','','','','topo_client_virtlink','1',1000002,1,''),('0013','-1','系统管理','system','','Y','N','tree','simple','','system','1',1000013,1,''),('00130001','0013','通知设置','tenantsystem','/platform/iradar/tenantsystem.action','Y','Y','','','','tenantsystem','1',1000001,1,'');
/*!40000 ALTER TABLE `sys_func` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_func_bt`
--

DROP TABLE IF EXISTS `sys_func_bt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_func_bt` (
  `id` varchar(50) NOT NULL COMMENT '功能ID',
  `fid` varchar(50) NOT NULL DEFAULT '' COMMENT '父功能ID',
  `bt_name` varchar(50) NOT NULL DEFAULT '' COMMENT '功能名称',
  `bt_alias` varchar(40) NOT NULL DEFAULT '' COMMENT '功能别名',
  `bt_deps` varchar(40) DEFAULT '' COMMENT '功能依赖',
  `bt_extra` varchar(1) NOT NULL DEFAULT 'N' COMMENT '功能附加',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `seq_no` int(4) NOT NULL DEFAULT '1' COMMENT '显示顺序',
  `note` varchar(50) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_func_bt`
--

LOCK TABLES `sys_func_bt` WRITE;
/*!40000 ALTER TABLE `sys_func_bt` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_func_bt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_func_bt_uri`
--

DROP TABLE IF EXISTS `sys_func_bt_uri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_func_bt_uri` (
  `id` varchar(50) NOT NULL COMMENT 'URI ID',
  `fid` varchar(50) NOT NULL DEFAULT '' COMMENT '功能ID',
  `bid` varchar(50) NOT NULL DEFAULT '' COMMENT '按钮ID',
  `entrance` char(1) DEFAULT '' COMMENT '是否模块入口',
  `uri` varchar(150) NOT NULL DEFAULT '' COMMENT '按钮URI',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `note` varchar(50) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_fid_entrance` (`fid`,`entrance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_func_bt_uri`
--

LOCK TABLES `sys_func_bt_uri` WRITE;
/*!40000 ALTER TABLE `sys_func_bt_uri` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_func_bt_uri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_id`
--

DROP TABLE IF EXISTS `sys_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_id` (
  `idspace` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `counter` bigint(20) NOT NULL,
  `increment_by` int(11) DEFAULT '100',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idspace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_id`
--

LOCK TABLES `sys_id` WRITE;
/*!40000 ALTER TABLE `sys_id` DISABLE KEYS */;
INSERT INTO `sys_id` VALUES ('OPERATION_DEPT',500,100,'部门流水号'),('OSKEY',500,100,'操作系统键值流水号'),('T_BIZ_LINE',500,100,'业务拓扑线路流水号'),('T_BIZ_NODE',500,100,'业务拓扑节点流水号'),('T_CABINET_NODE',500,100,'机柜拓扑节点流水号'),('T_LINE',500,100,'网络拓扑线路流水号'),('T_LINK',500,100,'链路流水号'),('T_NODE',500,100,'网络拓扑节点流水号'),('T_PIC',500,100,'拓扑图片流水号'),('T_SUBNET',500,100,'拓扑子网流水号'),('T_TOPO',500,100,'拓扑流水号'),('T_TOPO_LOCATION',500,100,'拓扑节点坐标流水号'),('T_TOPO_PIC',500,100,'背景图片流水号');
/*!40000 ALTER TABLE `sys_id` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmap_element_url`
--

DROP TABLE IF EXISTS `sysmap_element_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmap_element_url` (
  `tenantid` varchar(64) DEFAULT '0',
  `sysmapelementurlid` bigint(20) unsigned NOT NULL,
  `selementid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`sysmapelementurlid`),
  UNIQUE KEY `sysmap_element_url_1` (`selementid`,`name`),
  CONSTRAINT `c_sysmap_element_url_1` FOREIGN KEY (`selementid`) REFERENCES `sysmaps_elements` (`selementid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmap_element_url`
--

LOCK TABLES `sysmap_element_url` WRITE;
/*!40000 ALTER TABLE `sysmap_element_url` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmap_element_url` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmap_url`
--

DROP TABLE IF EXISTS `sysmap_url`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmap_url` (
  `tenantid` varchar(64) DEFAULT '0',
  `sysmapurlid` bigint(20) unsigned NOT NULL,
  `sysmapid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL DEFAULT '',
  `elementtype` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sysmapurlid`),
  UNIQUE KEY `sysmap_url_1` (`sysmapid`,`name`),
  CONSTRAINT `c_sysmap_url_1` FOREIGN KEY (`sysmapid`) REFERENCES `sysmaps` (`sysmapid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmap_url`
--

LOCK TABLES `sysmap_url` WRITE;
/*!40000 ALTER TABLE `sysmap_url` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmap_url` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmaps`
--

DROP TABLE IF EXISTS `sysmaps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmaps` (
  `tenantid` varchar(64) DEFAULT '0',
  `sysmapid` bigint(20) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `width` int(11) NOT NULL DEFAULT '600',
  `height` int(11) NOT NULL DEFAULT '400',
  `backgroundid` bigint(20) unsigned DEFAULT NULL,
  `label_type` int(11) NOT NULL DEFAULT '2',
  `label_location` int(11) NOT NULL DEFAULT '0',
  `highlight` int(11) NOT NULL DEFAULT '1',
  `expandproblem` int(11) NOT NULL DEFAULT '1',
  `markelements` int(11) NOT NULL DEFAULT '0',
  `show_unack` int(11) NOT NULL DEFAULT '0',
  `grid_size` int(11) NOT NULL DEFAULT '50',
  `grid_show` int(11) NOT NULL DEFAULT '1',
  `grid_align` int(11) NOT NULL DEFAULT '1',
  `label_format` int(11) NOT NULL DEFAULT '0',
  `label_type_host` int(11) NOT NULL DEFAULT '2',
  `label_type_hostgroup` int(11) NOT NULL DEFAULT '2',
  `label_type_trigger` int(11) NOT NULL DEFAULT '2',
  `label_type_map` int(11) NOT NULL DEFAULT '2',
  `label_type_image` int(11) NOT NULL DEFAULT '2',
  `label_string_host` varchar(255) NOT NULL DEFAULT '',
  `label_string_hostgroup` varchar(255) NOT NULL DEFAULT '',
  `label_string_trigger` varchar(255) NOT NULL DEFAULT '',
  `label_string_map` varchar(255) NOT NULL DEFAULT '',
  `label_string_image` varchar(255) NOT NULL DEFAULT '',
  `iconmapid` bigint(20) unsigned DEFAULT NULL,
  `expand_macros` int(11) NOT NULL DEFAULT '0',
  `severity_min` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sysmapid`),
  KEY `sysmaps_1` (`name`),
  KEY `sysmaps_2` (`backgroundid`),
  KEY `sysmaps_3` (`iconmapid`),
  CONSTRAINT `c_sysmaps_1` FOREIGN KEY (`backgroundid`) REFERENCES `images` (`imageid`),
  CONSTRAINT `c_sysmaps_2` FOREIGN KEY (`iconmapid`) REFERENCES `icon_map` (`iconmapid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmaps`
--

LOCK TABLES `sysmaps` WRITE;
/*!40000 ALTER TABLE `sysmaps` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmaps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmaps_elements`
--

DROP TABLE IF EXISTS `sysmaps_elements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmaps_elements` (
  `tenantid` varchar(64) DEFAULT '0',
  `selementid` bigint(20) unsigned NOT NULL,
  `sysmapid` bigint(20) unsigned NOT NULL,
  `elementid` bigint(20) unsigned NOT NULL DEFAULT '0',
  `elementtype` int(11) NOT NULL DEFAULT '0',
  `iconid_off` bigint(20) unsigned DEFAULT NULL,
  `iconid_on` bigint(20) unsigned DEFAULT NULL,
  `label` varchar(2048) NOT NULL DEFAULT '',
  `label_location` int(11) NOT NULL DEFAULT '-1',
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `iconid_disabled` bigint(20) unsigned DEFAULT NULL,
  `iconid_maintenance` bigint(20) unsigned DEFAULT NULL,
  `elementsubtype` int(11) NOT NULL DEFAULT '0',
  `areatype` int(11) NOT NULL DEFAULT '0',
  `width` int(11) NOT NULL DEFAULT '200',
  `height` int(11) NOT NULL DEFAULT '200',
  `viewtype` int(11) NOT NULL DEFAULT '0',
  `use_iconmap` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`selementid`),
  KEY `sysmaps_elements_1` (`sysmapid`),
  KEY `sysmaps_elements_2` (`iconid_off`),
  KEY `sysmaps_elements_3` (`iconid_on`),
  KEY `sysmaps_elements_4` (`iconid_disabled`),
  KEY `sysmaps_elements_5` (`iconid_maintenance`),
  CONSTRAINT `c_sysmaps_elements_1` FOREIGN KEY (`sysmapid`) REFERENCES `sysmaps` (`sysmapid`) ON DELETE CASCADE,
  CONSTRAINT `c_sysmaps_elements_2` FOREIGN KEY (`iconid_off`) REFERENCES `images` (`imageid`),
  CONSTRAINT `c_sysmaps_elements_3` FOREIGN KEY (`iconid_on`) REFERENCES `images` (`imageid`),
  CONSTRAINT `c_sysmaps_elements_4` FOREIGN KEY (`iconid_disabled`) REFERENCES `images` (`imageid`),
  CONSTRAINT `c_sysmaps_elements_5` FOREIGN KEY (`iconid_maintenance`) REFERENCES `images` (`imageid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmaps_elements`
--

LOCK TABLES `sysmaps_elements` WRITE;
/*!40000 ALTER TABLE `sysmaps_elements` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmaps_elements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmaps_link_triggers`
--

DROP TABLE IF EXISTS `sysmaps_link_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmaps_link_triggers` (
  `tenantid` varchar(64) DEFAULT '0',
  `linktriggerid` bigint(20) unsigned NOT NULL,
  `linkid` bigint(20) unsigned NOT NULL,
  `triggerid` bigint(20) unsigned NOT NULL,
  `drawtype` int(11) NOT NULL DEFAULT '0',
  `color` varchar(6) NOT NULL DEFAULT '000000',
  PRIMARY KEY (`linktriggerid`),
  UNIQUE KEY `sysmaps_link_triggers_1` (`linkid`,`triggerid`),
  KEY `sysmaps_link_triggers_2` (`triggerid`),
  CONSTRAINT `c_sysmaps_link_triggers_1` FOREIGN KEY (`linkid`) REFERENCES `sysmaps_links` (`linkid`) ON DELETE CASCADE,
  CONSTRAINT `c_sysmaps_link_triggers_2` FOREIGN KEY (`triggerid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmaps_link_triggers`
--

LOCK TABLES `sysmaps_link_triggers` WRITE;
/*!40000 ALTER TABLE `sysmaps_link_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmaps_link_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sysmaps_links`
--

DROP TABLE IF EXISTS `sysmaps_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysmaps_links` (
  `tenantid` varchar(64) DEFAULT '0',
  `linkid` bigint(20) unsigned NOT NULL,
  `sysmapid` bigint(20) unsigned NOT NULL,
  `selementid1` bigint(20) unsigned NOT NULL,
  `selementid2` bigint(20) unsigned NOT NULL,
  `drawtype` int(11) NOT NULL DEFAULT '0',
  `color` varchar(6) NOT NULL DEFAULT '000000',
  `label` varchar(2048) NOT NULL DEFAULT '',
  PRIMARY KEY (`linkid`),
  KEY `sysmaps_links_1` (`sysmapid`),
  KEY `sysmaps_links_2` (`selementid1`),
  KEY `sysmaps_links_3` (`selementid2`),
  CONSTRAINT `c_sysmaps_links_1` FOREIGN KEY (`sysmapid`) REFERENCES `sysmaps` (`sysmapid`) ON DELETE CASCADE,
  CONSTRAINT `c_sysmaps_links_2` FOREIGN KEY (`selementid1`) REFERENCES `sysmaps_elements` (`selementid`) ON DELETE CASCADE,
  CONSTRAINT `c_sysmaps_links_3` FOREIGN KEY (`selementid2`) REFERENCES `sysmaps_elements` (`selementid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sysmaps_links`
--

LOCK TABLES `sysmaps_links` WRITE;
/*!40000 ALTER TABLE `sysmaps_links` DISABLE KEYS */;
/*!40000 ALTER TABLE `sysmaps_links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_biz_line`
--

DROP TABLE IF EXISTS `t_biz_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_biz_line` (
  `tenantid` varchar(64) DEFAULT '0',
  `lineid` varchar(10) NOT NULL,
  `topoid` varchar(10) NOT NULL,
  `nodeid` varchar(10) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `tagname` varchar(50) DEFAULT NULL,
  `tonode` varchar(30) DEFAULT NULL,
  `g` varchar(50) DEFAULT NULL,
  `strokeweight` varchar(2) DEFAULT NULL,
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`lineid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_biz_line`
--

LOCK TABLES `t_biz_line` WRITE;
/*!40000 ALTER TABLE `t_biz_line` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_biz_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_biz_node`
--

DROP TABLE IF EXISTS `t_biz_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_biz_node` (
  `nodeid` varchar(10) NOT NULL,
  `topoid` varchar(10) NOT NULL,
  `hostid` varchar(20) DEFAULT NULL,
  `priority` varchar(20) DEFAULT NULL,
  `strokeweight` varchar(20) DEFAULT NULL,
  `fill` varchar(20) DEFAULT NULL,
  `stroke` varchar(20) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `tagname` varchar(200) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `g` varchar(50) DEFAULT NULL,
  `tenantid` varchar(64) NOT NULL COMMENT '租户ID',
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`nodeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_biz_node`
--

LOCK TABLES `t_biz_node` WRITE;
/*!40000 ALTER TABLE `t_biz_node` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_biz_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_cabinet_node`
--

DROP TABLE IF EXISTS `t_cabinet_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_cabinet_node` (
  `tenantid` varchar(64) DEFAULT '0',
  `nodeid` varchar(10) NOT NULL,
  `topoid` varchar(10) NOT NULL,
  `hostid` varchar(20) DEFAULT NULL,
  `priority` bigint(1) DEFAULT NULL,
  `category` varchar(20) DEFAULT NULL,
  `picid` varchar(50) DEFAULT NULL,
  `tagname` varchar(200) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `g` varchar(50) DEFAULT NULL,
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`nodeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_cabinet_node`
--

LOCK TABLES `t_cabinet_node` WRITE;
/*!40000 ALTER TABLE `t_cabinet_node` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_cabinet_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_host_exp`
--

DROP TABLE IF EXISTS `t_host_exp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_host_exp` (
  `tenantid` varchar(64) DEFAULT '0',
  `hostid` bigint(20) NOT NULL,
  `enterprise` varchar(30) DEFAULT NULL,
  `category` varchar(30) DEFAULT NULL,
  `model` varchar(30) DEFAULT NULL,
  `symbol` varchar(64) DEFAULT NULL,
  `sys_oid` varchar(64) DEFAULT NULL,
  `sys_name` varchar(200) DEFAULT NULL,
  `sys_descr` blob,
  `bridge_mac` varchar(17) DEFAULT NULL,
  `serial_num` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `subnet` varchar(50) DEFAULT NULL,
  `error` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`hostid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_host_exp`
--

LOCK TABLES `t_host_exp` WRITE;
/*!40000 ALTER TABLE `t_host_exp` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_host_exp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_line`
--

DROP TABLE IF EXISTS `t_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_line` (
  `tenantid` varchar(64) DEFAULT '0',
  `lineid` varchar(10) NOT NULL,
  `topoid` varchar(10) NOT NULL,
  `tbnailid` varchar(10) DEFAULT NULL,
  `nodeid` varchar(10) NOT NULL,
  `tagname` varchar(64) DEFAULT NULL,
  `tonode` varchar(30) DEFAULT NULL,
  `g` varchar(50) DEFAULT NULL,
  `strokeweight` varchar(2) DEFAULT NULL,
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`lineid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_line`
--

LOCK TABLES `t_line` WRITE;
/*!40000 ALTER TABLE `t_line` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_link`
--

DROP TABLE IF EXISTS `t_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_link` (
  `tenantid` varchar(64) DEFAULT '0',
  `linkid` varchar(10) NOT NULL,
  `start_id` int(10) DEFAULT NULL,
  `start_if_index` varchar(10) DEFAULT NULL,
  `start_if_descr` varchar(300) DEFAULT NULL,
  `start_ip` varchar(15) DEFAULT NULL,
  `start_mac` varchar(30) DEFAULT NULL,
  `end_id` int(10) DEFAULT NULL,
  `end_if_index` varchar(10) DEFAULT NULL,
  `end_if_descr` varchar(300) DEFAULT NULL,
  `end_ip` varchar(15) DEFAULT NULL,
  `end_mac` varchar(30) DEFAULT NULL,
  `band_width` int(10) DEFAULT NULL,
  `type` varchar(30) DEFAULT NULL,
  `tag` varchar(10) DEFAULT NULL,
  `backup` int(10) DEFAULT NULL,
  `traffic_if` int(10) DEFAULT NULL,
  `traffic_direct` int(10) DEFAULT NULL,
  `userid` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`linkid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_link`
--

LOCK TABLES `t_link` WRITE;
/*!40000 ALTER TABLE `t_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_node`
--

DROP TABLE IF EXISTS `t_node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_node` (
  `tenantid` varchar(64) DEFAULT '0',
  `nodeid` varchar(10) NOT NULL,
  `topoid` varchar(10) NOT NULL,
  `hostid` varchar(20) DEFAULT NULL,
  `category` varchar(20) DEFAULT NULL,
  `tbnailid` varchar(10) DEFAULT '-100',
  `tagname` varchar(200) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `g` varchar(64) DEFAULT NULL,
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`nodeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_node`
--

LOCK TABLES `t_node` WRITE;
/*!40000 ALTER TABLE `t_node` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_pic`
--

DROP TABLE IF EXISTS `t_pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_pic` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `category` varchar(32) DEFAULT NULL,
  `url` varchar(600) DEFAULT NULL,
  `width` varchar(10) DEFAULT NULL,
  `height` varchar(10) DEFAULT NULL,
  `userid` varchar(64) DEFAULT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_pic`
--

LOCK TABLES `t_pic` WRITE;
/*!40000 ALTER TABLE `t_pic` DISABLE KEYS */;
INSERT INTO `t_pic` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a','101','系统机柜1','cabinet','mappic/cabinet_1.jpg','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00',''),('5e4d0a6d39a44b9c906a3173b448aa4a','102','系统机柜2','cabinet','mappic/cabinet_2.gif','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00',''),('5e4d0a6d39a44b9c906a3173b448aa4a','103','系统机柜3','cabinet','mappic/cabinet_3.gif','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00',''),('5e4d0a6d39a44b9c906a3173b448aa4a','104','系统机柜4','cabinet','mappic/cabinet_4.gif','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00',''),('5e4d0a6d39a44b9c906a3173b448aa4a','105','系统机柜5','cabinet','mappic/cabinet_5.gif','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00',''),('5e4d0a6d39a44b9c906a3173b448aa4a','106','系统机柜6','cabinet','mappic/cabinet_6.gif','80','200',NULL,'0000-00-00 00:00:00','','0000-00-00 00:00:00','');
/*!40000 ALTER TABLE `t_pic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_subnet`
--

DROP TABLE IF EXISTS `t_subnet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_subnet` (
  `id` varchar(50) DEFAULT NULL,
  `subnetId` varchar(50) DEFAULT NULL COMMENT 'subnetId',
  `alias` varchar(50) DEFAULT NULL COMMENT 'subnet别名',
  `subnetmask` varchar(20) DEFAULT NULL COMMENT 'subnet掩码',
  `vlanId` varchar(20) DEFAULT NULL COMMENT 'vlanID',
  `gateway` varchar(50) DEFAULT NULL COMMENT '网关',
  `ipAddress` varchar(20) DEFAULT NULL COMMENT 'ip地址',
  `netAddress` varchar(20) DEFAULT NULL COMMENT '网络地址',
  `startIp` varchar(20) DEFAULT NULL COMMENT '起始IP',
  `startLongIp` varchar(20) DEFAULT NULL COMMENT '长整型起始IP',
  `endIp` varchar(20) DEFAULT NULL COMMENT '结束IP',
  `endLongIp` varchar(20) DEFAULT NULL COMMENT '长整型结束IP'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_subnet`
--

LOCK TABLES `t_subnet` WRITE;
/*!40000 ALTER TABLE `t_subnet` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_subnet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_topo`
--

DROP TABLE IF EXISTS `t_topo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_topo` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` varchar(10) NOT NULL COMMENT '拓扑ID',
  `toponame` varchar(10) NOT NULL COMMENT '拓扑名称',
  `topotype` varchar(64) DEFAULT NULL,
  `is_public` varchar(2) NOT NULL COMMENT '是否公开',
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_topo`
--

LOCK TABLES `t_topo` WRITE;
/*!40000 ALTER TABLE `t_topo` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_topo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_topo_extend`
--

DROP TABLE IF EXISTS `t_topo_extend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_topo_extend` (
  `id` varchar(20) DEFAULT NULL,
  `topoType` varchar(20) DEFAULT NULL COMMENT '拓扑类型',
  `nodeType` varchar(20) DEFAULT NULL COMMENT '节点类型',
  `hostId` varchar(50) DEFAULT NULL COMMENT '节点Id',
  `tbnailId` varchar(20) DEFAULT NULL COMMENT '缩略图Id',
  `tbnailName` varchar(20) DEFAULT NULL COMMENT '缩略图名称',
  `tbnailNX` varchar(20) DEFAULT NULL COMMENT '创建缩略图前Host的x坐标',
  `tbnailNY` varchar(20) DEFAULT NULL COMMENT '创建缩略图前Host的y坐标',
  `bizTopoId` varchar(20) DEFAULT NULL COMMENT '业务拓扑Id',
  `bizNodeId` varchar(20) DEFAULT NULL COMMENT '业务拓扑业务节点Id',
  `bizNodeName` varchar(20) DEFAULT NULL COMMENT '业务拓扑业务节点名称',
  `bizAreaId` varchar(20) DEFAULT NULL COMMENT '业务拓扑区域Id',
  `width` varchar(20) DEFAULT NULL COMMENT '业务区域宽度',
  `height` varchar(20) DEFAULT NULL COMMENT '业务区域高度',
  `tenantid` varchar(64) DEFAULT NULL COMMENT '用户Id',
  `userid` varchar(64) DEFAULT NULL COMMENT '租户Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_topo_extend`
--

LOCK TABLES `t_topo_extend` WRITE;
/*!40000 ALTER TABLE `t_topo_extend` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_topo_extend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_topo_location`
--

DROP TABLE IF EXISTS `t_topo_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_topo_location` (
  `id` varchar(20) DEFAULT NULL,
  `topoType` varchar(20) DEFAULT NULL COMMENT '节点所属拓扑类型',
  `nodeType` varchar(20) DEFAULT NULL COMMENT '拓扑节点类型',
  `hostId` varchar(50) DEFAULT NULL COMMENT '拓扑节点Id',
  `X` varchar(20) DEFAULT NULL COMMENT '拓扑节点X坐标',
  `Y` varchar(20) DEFAULT NULL COMMENT '拓扑节点Y坐标',
  `tenantId` varchar(50) DEFAULT NULL COMMENT '拓扑所属租户Id',
  `userId` varchar(50) DEFAULT NULL COMMENT '拓扑所属用户Id',
  `topoId` varchar(20) DEFAULT NULL COMMENT '业务拓扑Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_topo_location`
--

LOCK TABLES `t_topo_location` WRITE;
/*!40000 ALTER TABLE `t_topo_location` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_topo_location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_topo_pic`
--

DROP TABLE IF EXISTS `t_topo_pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_topo_pic` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` varchar(10) NOT NULL,
  `topoid` varchar(50) NOT NULL,
  `picid` varchar(32) DEFAULT NULL,
  `userid` varchar(64) NOT NULL COMMENT '用户ID',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(64) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_topo_pic`
--

LOCK TABLES `t_topo_pic` WRITE;
/*!40000 ALTER TABLE `t_topo_pic` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_topo_pic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tenants`
--

DROP TABLE IF EXISTS `tenants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tenants` (
  `id` bigint(20) unsigned NOT NULL,
  `tenantid` varchar(64) DEFAULT NULL,
  `name` varchar(64) DEFAULT ' ',
  `parent` varchar(64) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `proxy_hostid` bigint(20) unsigned DEFAULT NULL,
  `loadfactor` smallint(2) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tenants`
--

LOCK TABLES `tenants` WRITE;
/*!40000 ALTER TABLE `tenants` DISABLE KEYS */;
INSERT INTO `tenants` VALUES (1,'-',' ','0',0,NULL,0,1),(2,'5e4d0a6d39a44b9c906a3173b448aa4a',' ','0',0,NULL,0,1);
/*!40000 ALTER TABLE `tenants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timeperiods`
--

DROP TABLE IF EXISTS `timeperiods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `timeperiods` (
  `tenantid` varchar(64) DEFAULT '0',
  `timeperiodid` bigint(20) unsigned NOT NULL,
  `timeperiod_type` int(11) NOT NULL DEFAULT '0',
  `every` int(11) NOT NULL DEFAULT '0',
  `month` int(11) NOT NULL DEFAULT '0',
  `dayofweek` int(11) NOT NULL DEFAULT '0',
  `day` int(11) NOT NULL DEFAULT '0',
  `start_time` int(11) NOT NULL DEFAULT '0',
  `period` int(11) NOT NULL DEFAULT '0',
  `start_date` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`timeperiodid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timeperiods`
--

LOCK TABLES `timeperiods` WRITE;
/*!40000 ALTER TABLE `timeperiods` DISABLE KEYS */;
/*!40000 ALTER TABLE `timeperiods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trends`
--

DROP TABLE IF EXISTS `trends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trends` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `num` int(11) NOT NULL DEFAULT '0',
  `value_min` double(16,4) NOT NULL DEFAULT '0.0000',
  `value_avg` double(16,4) NOT NULL DEFAULT '0.0000',
  `value_max` double(16,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (clock)
(PARTITION p0 VALUES LESS THAN (1438531200) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (1439136000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (1439740800) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (1440345600) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (1440950400) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (1441555200) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (1442160000) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (1442764800) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (1443369600) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (1443974400) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (1444579200) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (1445184000) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (1445788800) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (1446393600) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (1446998400) ENGINE = InnoDB,
 PARTITION p15 VALUES LESS THAN (1447603200) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (1448208000) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (1448812800) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (1449417600) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (1450022400) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (1450627200) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (1451232000) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (1451836800) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (1452441600) ENGINE = InnoDB,
 PARTITION p24 VALUES LESS THAN (1453046400) ENGINE = InnoDB,
 PARTITION p25 VALUES LESS THAN (1453651200) ENGINE = InnoDB,
 PARTITION p26 VALUES LESS THAN (1454256000) ENGINE = InnoDB,
 PARTITION p27 VALUES LESS THAN (1454860800) ENGINE = InnoDB,
 PARTITION p28 VALUES LESS THAN (1455465600) ENGINE = InnoDB,
 PARTITION p29 VALUES LESS THAN (1456070400) ENGINE = InnoDB,
 PARTITION p30 VALUES LESS THAN (1456675200) ENGINE = InnoDB,
 PARTITION p31 VALUES LESS THAN (1457280000) ENGINE = InnoDB,
 PARTITION p32 VALUES LESS THAN (1457884800) ENGINE = InnoDB,
 PARTITION p33 VALUES LESS THAN (1458489600) ENGINE = InnoDB,
 PARTITION p34 VALUES LESS THAN (1459094400) ENGINE = InnoDB,
 PARTITION p35 VALUES LESS THAN (1459699200) ENGINE = InnoDB,
 PARTITION p36 VALUES LESS THAN (1460304000) ENGINE = InnoDB,
 PARTITION p37 VALUES LESS THAN (1460908800) ENGINE = InnoDB,
 PARTITION p38 VALUES LESS THAN (1461513600) ENGINE = InnoDB,
 PARTITION p39 VALUES LESS THAN (1462118400) ENGINE = InnoDB,
 PARTITION p40 VALUES LESS THAN (1462723200) ENGINE = InnoDB,
 PARTITION p41 VALUES LESS THAN (1463328000) ENGINE = InnoDB,
 PARTITION p42 VALUES LESS THAN (1463932800) ENGINE = InnoDB,
 PARTITION p43 VALUES LESS THAN (1464537600) ENGINE = InnoDB,
 PARTITION p44 VALUES LESS THAN (1465142400) ENGINE = InnoDB,
 PARTITION p45 VALUES LESS THAN (1465747200) ENGINE = InnoDB,
 PARTITION p46 VALUES LESS THAN (1466352000) ENGINE = InnoDB,
 PARTITION p47 VALUES LESS THAN (1466956800) ENGINE = InnoDB,
 PARTITION p48 VALUES LESS THAN (1467561600) ENGINE = InnoDB,
 PARTITION p49 VALUES LESS THAN (1468166400) ENGINE = InnoDB,
 PARTITION p50 VALUES LESS THAN (1468771200) ENGINE = InnoDB,
 PARTITION p51 VALUES LESS THAN (1469376000) ENGINE = InnoDB,
 PARTITION p52 VALUES LESS THAN (1469980800) ENGINE = InnoDB,
 PARTITION p53 VALUES LESS THAN (1470585600) ENGINE = InnoDB,
 PARTITION p54 VALUES LESS THAN (1471190400) ENGINE = InnoDB,
 PARTITION p55 VALUES LESS THAN (1471795200) ENGINE = InnoDB,
 PARTITION p56 VALUES LESS THAN (1472400000) ENGINE = InnoDB,
 PARTITION p57 VALUES LESS THAN (1473004800) ENGINE = InnoDB,
 PARTITION p58 VALUES LESS THAN (1473609600) ENGINE = InnoDB,
 PARTITION p59 VALUES LESS THAN (1474214400) ENGINE = InnoDB,
 PARTITION p60 VALUES LESS THAN (1474819200) ENGINE = InnoDB,
 PARTITION p61 VALUES LESS THAN (1475424000) ENGINE = InnoDB,
 PARTITION p62 VALUES LESS THAN (1476028800) ENGINE = InnoDB,
 PARTITION p63 VALUES LESS THAN (1476633600) ENGINE = InnoDB,
 PARTITION p64 VALUES LESS THAN (1477238400) ENGINE = InnoDB,
 PARTITION p65 VALUES LESS THAN (1477843200) ENGINE = InnoDB,
 PARTITION p66 VALUES LESS THAN (1478448000) ENGINE = InnoDB,
 PARTITION p67 VALUES LESS THAN (1479052800) ENGINE = InnoDB,
 PARTITION p68 VALUES LESS THAN (1479657600) ENGINE = InnoDB,
 PARTITION p69 VALUES LESS THAN (1480262400) ENGINE = InnoDB,
 PARTITION p70 VALUES LESS THAN (1480867200) ENGINE = InnoDB,
 PARTITION p71 VALUES LESS THAN (1481472000) ENGINE = InnoDB,
 PARTITION p72 VALUES LESS THAN (1482076800) ENGINE = InnoDB,
 PARTITION p73 VALUES LESS THAN (1482681600) ENGINE = InnoDB,
 PARTITION p74 VALUES LESS THAN (1483286400) ENGINE = InnoDB,
 PARTITION p75 VALUES LESS THAN (1483891200) ENGINE = InnoDB,
 PARTITION p76 VALUES LESS THAN (1484496000) ENGINE = InnoDB,
 PARTITION p77 VALUES LESS THAN (1485100800) ENGINE = InnoDB,
 PARTITION p78 VALUES LESS THAN (1485705600) ENGINE = InnoDB,
 PARTITION p79 VALUES LESS THAN (1486310400) ENGINE = InnoDB,
 PARTITION p80 VALUES LESS THAN (1486915200) ENGINE = InnoDB,
 PARTITION p81 VALUES LESS THAN (1487520000) ENGINE = InnoDB,
 PARTITION p82 VALUES LESS THAN (1488124800) ENGINE = InnoDB,
 PARTITION p83 VALUES LESS THAN (1488729600) ENGINE = InnoDB,
 PARTITION p84 VALUES LESS THAN (1489334400) ENGINE = InnoDB,
 PARTITION p85 VALUES LESS THAN (1489939200) ENGINE = InnoDB,
 PARTITION p86 VALUES LESS THAN (1490544000) ENGINE = InnoDB,
 PARTITION p87 VALUES LESS THAN (1491148800) ENGINE = InnoDB,
 PARTITION p88 VALUES LESS THAN (1491753600) ENGINE = InnoDB,
 PARTITION p89 VALUES LESS THAN (1492358400) ENGINE = InnoDB,
 PARTITION p90 VALUES LESS THAN (1492963200) ENGINE = InnoDB,
 PARTITION p91 VALUES LESS THAN (1493568000) ENGINE = InnoDB,
 PARTITION p92 VALUES LESS THAN (1494172800) ENGINE = InnoDB,
 PARTITION p93 VALUES LESS THAN (1494777600) ENGINE = InnoDB,
 PARTITION p94 VALUES LESS THAN (1495382400) ENGINE = InnoDB,
 PARTITION p95 VALUES LESS THAN (1495987200) ENGINE = InnoDB,
 PARTITION p96 VALUES LESS THAN (1496592000) ENGINE = InnoDB,
 PARTITION p97 VALUES LESS THAN (1497196800) ENGINE = InnoDB,
 PARTITION p98 VALUES LESS THAN (1497801600) ENGINE = InnoDB,
 PARTITION p99 VALUES LESS THAN (1498406400) ENGINE = InnoDB,
 PARTITION p100 VALUES LESS THAN (1499011200) ENGINE = InnoDB,
 PARTITION p101 VALUES LESS THAN (1499616000) ENGINE = InnoDB,
 PARTITION p102 VALUES LESS THAN (1500220800) ENGINE = InnoDB,
 PARTITION p103 VALUES LESS THAN (1500825600) ENGINE = InnoDB,
 PARTITION p104 VALUES LESS THAN (1501430400) ENGINE = InnoDB,
 PARTITION p105 VALUES LESS THAN (1502035200) ENGINE = InnoDB,
 PARTITION p106 VALUES LESS THAN (1502640000) ENGINE = InnoDB,
 PARTITION p107 VALUES LESS THAN (1503244800) ENGINE = InnoDB,
 PARTITION p108 VALUES LESS THAN (1503849600) ENGINE = InnoDB,
 PARTITION p109 VALUES LESS THAN (1504454400) ENGINE = InnoDB,
 PARTITION p110 VALUES LESS THAN (1505059200) ENGINE = InnoDB,
 PARTITION p111 VALUES LESS THAN (1505664000) ENGINE = InnoDB,
 PARTITION p112 VALUES LESS THAN (1506268800) ENGINE = InnoDB,
 PARTITION p113 VALUES LESS THAN (1506873600) ENGINE = InnoDB,
 PARTITION p114 VALUES LESS THAN (1507478400) ENGINE = InnoDB,
 PARTITION p115 VALUES LESS THAN (1508083200) ENGINE = InnoDB,
 PARTITION p116 VALUES LESS THAN (1508688000) ENGINE = InnoDB,
 PARTITION p117 VALUES LESS THAN (1509292800) ENGINE = InnoDB,
 PARTITION p118 VALUES LESS THAN (1509897600) ENGINE = InnoDB,
 PARTITION p119 VALUES LESS THAN (1510502400) ENGINE = InnoDB,
 PARTITION p120 VALUES LESS THAN (1511107200) ENGINE = InnoDB,
 PARTITION p121 VALUES LESS THAN (1511712000) ENGINE = InnoDB,
 PARTITION p122 VALUES LESS THAN (1512316800) ENGINE = InnoDB,
 PARTITION p123 VALUES LESS THAN (1512921600) ENGINE = InnoDB,
 PARTITION p124 VALUES LESS THAN (1513526400) ENGINE = InnoDB,
 PARTITION p125 VALUES LESS THAN (1514131200) ENGINE = InnoDB,
 PARTITION p126 VALUES LESS THAN (1514736000) ENGINE = InnoDB,
 PARTITION p127 VALUES LESS THAN (1515340800) ENGINE = InnoDB,
 PARTITION p128 VALUES LESS THAN (1515945600) ENGINE = InnoDB,
 PARTITION p129 VALUES LESS THAN (1516550400) ENGINE = InnoDB,
 PARTITION p130 VALUES LESS THAN (1517155200) ENGINE = InnoDB,
 PARTITION p131 VALUES LESS THAN (1517760000) ENGINE = InnoDB,
 PARTITION p132 VALUES LESS THAN (1518364800) ENGINE = InnoDB,
 PARTITION p133 VALUES LESS THAN (1518969600) ENGINE = InnoDB,
 PARTITION p134 VALUES LESS THAN (1519574400) ENGINE = InnoDB,
 PARTITION p135 VALUES LESS THAN (1520179200) ENGINE = InnoDB,
 PARTITION p136 VALUES LESS THAN (1520784000) ENGINE = InnoDB,
 PARTITION p137 VALUES LESS THAN (1521388800) ENGINE = InnoDB,
 PARTITION p138 VALUES LESS THAN (1521993600) ENGINE = InnoDB,
 PARTITION p139 VALUES LESS THAN (1522598400) ENGINE = InnoDB,
 PARTITION p140 VALUES LESS THAN (1523203200) ENGINE = InnoDB,
 PARTITION p141 VALUES LESS THAN (1523808000) ENGINE = InnoDB,
 PARTITION p142 VALUES LESS THAN (1524412800) ENGINE = InnoDB,
 PARTITION p143 VALUES LESS THAN (1525017600) ENGINE = InnoDB,
 PARTITION p144 VALUES LESS THAN (1525622400) ENGINE = InnoDB,
 PARTITION p145 VALUES LESS THAN (1526227200) ENGINE = InnoDB,
 PARTITION p146 VALUES LESS THAN (1526832000) ENGINE = InnoDB,
 PARTITION p147 VALUES LESS THAN (1527436800) ENGINE = InnoDB,
 PARTITION p148 VALUES LESS THAN (1528041600) ENGINE = InnoDB,
 PARTITION p149 VALUES LESS THAN (1528646400) ENGINE = InnoDB,
 PARTITION p150 VALUES LESS THAN (1529251200) ENGINE = InnoDB,
 PARTITION p151 VALUES LESS THAN (1529856000) ENGINE = InnoDB,
 PARTITION p152 VALUES LESS THAN (1530460800) ENGINE = InnoDB,
 PARTITION p153 VALUES LESS THAN (1531065600) ENGINE = InnoDB,
 PARTITION p154 VALUES LESS THAN (1531670400) ENGINE = InnoDB,
 PARTITION p155 VALUES LESS THAN (1532275200) ENGINE = InnoDB,
 PARTITION p156 VALUES LESS THAN (1532880000) ENGINE = InnoDB,
 PARTITION p157 VALUES LESS THAN (1533484800) ENGINE = InnoDB,
 PARTITION p158 VALUES LESS THAN (1534089600) ENGINE = InnoDB,
 PARTITION p159 VALUES LESS THAN (1534694400) ENGINE = InnoDB,
 PARTITION p160 VALUES LESS THAN (1535299200) ENGINE = InnoDB,
 PARTITION p161 VALUES LESS THAN (1535904000) ENGINE = InnoDB,
 PARTITION p162 VALUES LESS THAN (1536508800) ENGINE = InnoDB,
 PARTITION p163 VALUES LESS THAN (1537113600) ENGINE = InnoDB,
 PARTITION p164 VALUES LESS THAN (1537718400) ENGINE = InnoDB,
 PARTITION p165 VALUES LESS THAN (1538323200) ENGINE = InnoDB,
 PARTITION p166 VALUES LESS THAN (1538928000) ENGINE = InnoDB,
 PARTITION p167 VALUES LESS THAN (1539532800) ENGINE = InnoDB,
 PARTITION p168 VALUES LESS THAN (1540137600) ENGINE = InnoDB,
 PARTITION p169 VALUES LESS THAN (1540742400) ENGINE = InnoDB,
 PARTITION p170 VALUES LESS THAN (1541347200) ENGINE = InnoDB,
 PARTITION p171 VALUES LESS THAN (1541952000) ENGINE = InnoDB,
 PARTITION p172 VALUES LESS THAN (1542556800) ENGINE = InnoDB,
 PARTITION p173 VALUES LESS THAN (1543161600) ENGINE = InnoDB,
 PARTITION p174 VALUES LESS THAN (1543766400) ENGINE = InnoDB,
 PARTITION p175 VALUES LESS THAN (1544371200) ENGINE = InnoDB,
 PARTITION p176 VALUES LESS THAN (1544976000) ENGINE = InnoDB,
 PARTITION p177 VALUES LESS THAN (1545580800) ENGINE = InnoDB,
 PARTITION p178 VALUES LESS THAN (1546185600) ENGINE = InnoDB,
 PARTITION p179 VALUES LESS THAN (1546790400) ENGINE = InnoDB,
 PARTITION p180 VALUES LESS THAN (1547395200) ENGINE = InnoDB,
 PARTITION p181 VALUES LESS THAN (1548000000) ENGINE = InnoDB,
 PARTITION p182 VALUES LESS THAN (1548604800) ENGINE = InnoDB,
 PARTITION p183 VALUES LESS THAN (1549209600) ENGINE = InnoDB,
 PARTITION p184 VALUES LESS THAN (1549814400) ENGINE = InnoDB,
 PARTITION p185 VALUES LESS THAN (1550419200) ENGINE = InnoDB,
 PARTITION p186 VALUES LESS THAN (1551024000) ENGINE = InnoDB,
 PARTITION p187 VALUES LESS THAN (1551628800) ENGINE = InnoDB,
 PARTITION p188 VALUES LESS THAN (1552233600) ENGINE = InnoDB,
 PARTITION p189 VALUES LESS THAN (1552838400) ENGINE = InnoDB,
 PARTITION p190 VALUES LESS THAN (1553443200) ENGINE = InnoDB,
 PARTITION p191 VALUES LESS THAN (1554048000) ENGINE = InnoDB,
 PARTITION p192 VALUES LESS THAN (1554652800) ENGINE = InnoDB,
 PARTITION p193 VALUES LESS THAN (1555257600) ENGINE = InnoDB,
 PARTITION p194 VALUES LESS THAN (1555862400) ENGINE = InnoDB,
 PARTITION p195 VALUES LESS THAN (1556467200) ENGINE = InnoDB,
 PARTITION p196 VALUES LESS THAN (1557072000) ENGINE = InnoDB,
 PARTITION p197 VALUES LESS THAN (1557676800) ENGINE = InnoDB,
 PARTITION p198 VALUES LESS THAN (1558281600) ENGINE = InnoDB,
 PARTITION p199 VALUES LESS THAN (1558886400) ENGINE = InnoDB,
 PARTITION p200 VALUES LESS THAN (1559491200) ENGINE = InnoDB,
 PARTITION p201 VALUES LESS THAN (1560096000) ENGINE = InnoDB,
 PARTITION p202 VALUES LESS THAN (1560700800) ENGINE = InnoDB,
 PARTITION p203 VALUES LESS THAN (1561305600) ENGINE = InnoDB,
 PARTITION p204 VALUES LESS THAN (1561910400) ENGINE = InnoDB,
 PARTITION p205 VALUES LESS THAN (1562515200) ENGINE = InnoDB,
 PARTITION p206 VALUES LESS THAN (1563120000) ENGINE = InnoDB,
 PARTITION p207 VALUES LESS THAN (1563724800) ENGINE = InnoDB,
 PARTITION p208 VALUES LESS THAN (1564329600) ENGINE = InnoDB,
 PARTITION p209 VALUES LESS THAN (1564934400) ENGINE = InnoDB,
 PARTITION p210 VALUES LESS THAN (1565539200) ENGINE = InnoDB,
 PARTITION p211 VALUES LESS THAN (1566144000) ENGINE = InnoDB,
 PARTITION p212 VALUES LESS THAN (1566748800) ENGINE = InnoDB,
 PARTITION p213 VALUES LESS THAN (1567353600) ENGINE = InnoDB,
 PARTITION p214 VALUES LESS THAN (1567958400) ENGINE = InnoDB,
 PARTITION p215 VALUES LESS THAN (1568563200) ENGINE = InnoDB,
 PARTITION p216 VALUES LESS THAN (1569168000) ENGINE = InnoDB,
 PARTITION p217 VALUES LESS THAN (1569772800) ENGINE = InnoDB,
 PARTITION p218 VALUES LESS THAN (1570377600) ENGINE = InnoDB,
 PARTITION p219 VALUES LESS THAN (1570982400) ENGINE = InnoDB,
 PARTITION p220 VALUES LESS THAN (1571587200) ENGINE = InnoDB,
 PARTITION p221 VALUES LESS THAN (1572192000) ENGINE = InnoDB,
 PARTITION p222 VALUES LESS THAN (1572796800) ENGINE = InnoDB,
 PARTITION p223 VALUES LESS THAN (1573401600) ENGINE = InnoDB,
 PARTITION p224 VALUES LESS THAN (1574006400) ENGINE = InnoDB,
 PARTITION p225 VALUES LESS THAN (1574611200) ENGINE = InnoDB,
 PARTITION p226 VALUES LESS THAN (1575216000) ENGINE = InnoDB,
 PARTITION p227 VALUES LESS THAN (1575820800) ENGINE = InnoDB,
 PARTITION p228 VALUES LESS THAN (1576425600) ENGINE = InnoDB,
 PARTITION p229 VALUES LESS THAN (1577030400) ENGINE = InnoDB,
 PARTITION p230 VALUES LESS THAN (1577635200) ENGINE = InnoDB,
 PARTITION p231 VALUES LESS THAN (1578240000) ENGINE = InnoDB,
 PARTITION p232 VALUES LESS THAN (1578844800) ENGINE = InnoDB,
 PARTITION p233 VALUES LESS THAN (1579449600) ENGINE = InnoDB,
 PARTITION p234 VALUES LESS THAN (1580054400) ENGINE = InnoDB,
 PARTITION p235 VALUES LESS THAN (1580659200) ENGINE = InnoDB,
 PARTITION p236 VALUES LESS THAN (1581264000) ENGINE = InnoDB,
 PARTITION p237 VALUES LESS THAN (1581868800) ENGINE = InnoDB,
 PARTITION p238 VALUES LESS THAN (1582473600) ENGINE = InnoDB,
 PARTITION p239 VALUES LESS THAN (1583078400) ENGINE = InnoDB,
 PARTITION p240 VALUES LESS THAN (1583683200) ENGINE = InnoDB,
 PARTITION p241 VALUES LESS THAN (1584288000) ENGINE = InnoDB,
 PARTITION p242 VALUES LESS THAN (1584892800) ENGINE = InnoDB,
 PARTITION p243 VALUES LESS THAN (1585497600) ENGINE = InnoDB,
 PARTITION p244 VALUES LESS THAN (1586102400) ENGINE = InnoDB,
 PARTITION p245 VALUES LESS THAN (1586707200) ENGINE = InnoDB,
 PARTITION p246 VALUES LESS THAN (1587312000) ENGINE = InnoDB,
 PARTITION p247 VALUES LESS THAN (1587916800) ENGINE = InnoDB,
 PARTITION p248 VALUES LESS THAN (1588521600) ENGINE = InnoDB,
 PARTITION p249 VALUES LESS THAN (1589126400) ENGINE = InnoDB,
 PARTITION p250 VALUES LESS THAN (1589731200) ENGINE = InnoDB,
 PARTITION p251 VALUES LESS THAN (1590336000) ENGINE = InnoDB,
 PARTITION p252 VALUES LESS THAN (1590940800) ENGINE = InnoDB,
 PARTITION p253 VALUES LESS THAN (1591545600) ENGINE = InnoDB,
 PARTITION p254 VALUES LESS THAN (1592150400) ENGINE = InnoDB,
 PARTITION p255 VALUES LESS THAN (1592755200) ENGINE = InnoDB,
 PARTITION p256 VALUES LESS THAN (1593360000) ENGINE = InnoDB,
 PARTITION p257 VALUES LESS THAN (1593964800) ENGINE = InnoDB,
 PARTITION p258 VALUES LESS THAN (1594569600) ENGINE = InnoDB,
 PARTITION p259 VALUES LESS THAN (1595174400) ENGINE = InnoDB,
 PARTITION pmore VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trends`
--

LOCK TABLES `trends` WRITE;
/*!40000 ALTER TABLE `trends` DISABLE KEYS */;
/*!40000 ALTER TABLE `trends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trends_uint`
--

DROP TABLE IF EXISTS `trends_uint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trends_uint` (
  `tenantid` varchar(64) DEFAULT '0',
  `itemid` bigint(20) unsigned NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `num` int(11) NOT NULL DEFAULT '0',
  `value_min` bigint(20) unsigned NOT NULL DEFAULT '0',
  `value_avg` bigint(20) unsigned NOT NULL DEFAULT '0',
  `value_max` bigint(20) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`itemid`,`clock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
/*!50100 PARTITION BY RANGE (clock)
(PARTITION p0 VALUES LESS THAN (1438531200) ENGINE = InnoDB,
 PARTITION p1 VALUES LESS THAN (1439136000) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (1439740800) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (1440345600) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (1440950400) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (1441555200) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (1442160000) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (1442764800) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (1443369600) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (1443974400) ENGINE = InnoDB,
 PARTITION p10 VALUES LESS THAN (1444579200) ENGINE = InnoDB,
 PARTITION p11 VALUES LESS THAN (1445184000) ENGINE = InnoDB,
 PARTITION p12 VALUES LESS THAN (1445788800) ENGINE = InnoDB,
 PARTITION p13 VALUES LESS THAN (1446393600) ENGINE = InnoDB,
 PARTITION p14 VALUES LESS THAN (1446998400) ENGINE = InnoDB,
 PARTITION p15 VALUES LESS THAN (1447603200) ENGINE = InnoDB,
 PARTITION p16 VALUES LESS THAN (1448208000) ENGINE = InnoDB,
 PARTITION p17 VALUES LESS THAN (1448812800) ENGINE = InnoDB,
 PARTITION p18 VALUES LESS THAN (1449417600) ENGINE = InnoDB,
 PARTITION p19 VALUES LESS THAN (1450022400) ENGINE = InnoDB,
 PARTITION p20 VALUES LESS THAN (1450627200) ENGINE = InnoDB,
 PARTITION p21 VALUES LESS THAN (1451232000) ENGINE = InnoDB,
 PARTITION p22 VALUES LESS THAN (1451836800) ENGINE = InnoDB,
 PARTITION p23 VALUES LESS THAN (1452441600) ENGINE = InnoDB,
 PARTITION p24 VALUES LESS THAN (1453046400) ENGINE = InnoDB,
 PARTITION p25 VALUES LESS THAN (1453651200) ENGINE = InnoDB,
 PARTITION p26 VALUES LESS THAN (1454256000) ENGINE = InnoDB,
 PARTITION p27 VALUES LESS THAN (1454860800) ENGINE = InnoDB,
 PARTITION p28 VALUES LESS THAN (1455465600) ENGINE = InnoDB,
 PARTITION p29 VALUES LESS THAN (1456070400) ENGINE = InnoDB,
 PARTITION p30 VALUES LESS THAN (1456675200) ENGINE = InnoDB,
 PARTITION p31 VALUES LESS THAN (1457280000) ENGINE = InnoDB,
 PARTITION p32 VALUES LESS THAN (1457884800) ENGINE = InnoDB,
 PARTITION p33 VALUES LESS THAN (1458489600) ENGINE = InnoDB,
 PARTITION p34 VALUES LESS THAN (1459094400) ENGINE = InnoDB,
 PARTITION p35 VALUES LESS THAN (1459699200) ENGINE = InnoDB,
 PARTITION p36 VALUES LESS THAN (1460304000) ENGINE = InnoDB,
 PARTITION p37 VALUES LESS THAN (1460908800) ENGINE = InnoDB,
 PARTITION p38 VALUES LESS THAN (1461513600) ENGINE = InnoDB,
 PARTITION p39 VALUES LESS THAN (1462118400) ENGINE = InnoDB,
 PARTITION p40 VALUES LESS THAN (1462723200) ENGINE = InnoDB,
 PARTITION p41 VALUES LESS THAN (1463328000) ENGINE = InnoDB,
 PARTITION p42 VALUES LESS THAN (1463932800) ENGINE = InnoDB,
 PARTITION p43 VALUES LESS THAN (1464537600) ENGINE = InnoDB,
 PARTITION p44 VALUES LESS THAN (1465142400) ENGINE = InnoDB,
 PARTITION p45 VALUES LESS THAN (1465747200) ENGINE = InnoDB,
 PARTITION p46 VALUES LESS THAN (1466352000) ENGINE = InnoDB,
 PARTITION p47 VALUES LESS THAN (1466956800) ENGINE = InnoDB,
 PARTITION p48 VALUES LESS THAN (1467561600) ENGINE = InnoDB,
 PARTITION p49 VALUES LESS THAN (1468166400) ENGINE = InnoDB,
 PARTITION p50 VALUES LESS THAN (1468771200) ENGINE = InnoDB,
 PARTITION p51 VALUES LESS THAN (1469376000) ENGINE = InnoDB,
 PARTITION p52 VALUES LESS THAN (1469980800) ENGINE = InnoDB,
 PARTITION p53 VALUES LESS THAN (1470585600) ENGINE = InnoDB,
 PARTITION p54 VALUES LESS THAN (1471190400) ENGINE = InnoDB,
 PARTITION p55 VALUES LESS THAN (1471795200) ENGINE = InnoDB,
 PARTITION p56 VALUES LESS THAN (1472400000) ENGINE = InnoDB,
 PARTITION p57 VALUES LESS THAN (1473004800) ENGINE = InnoDB,
 PARTITION p58 VALUES LESS THAN (1473609600) ENGINE = InnoDB,
 PARTITION p59 VALUES LESS THAN (1474214400) ENGINE = InnoDB,
 PARTITION p60 VALUES LESS THAN (1474819200) ENGINE = InnoDB,
 PARTITION p61 VALUES LESS THAN (1475424000) ENGINE = InnoDB,
 PARTITION p62 VALUES LESS THAN (1476028800) ENGINE = InnoDB,
 PARTITION p63 VALUES LESS THAN (1476633600) ENGINE = InnoDB,
 PARTITION p64 VALUES LESS THAN (1477238400) ENGINE = InnoDB,
 PARTITION p65 VALUES LESS THAN (1477843200) ENGINE = InnoDB,
 PARTITION p66 VALUES LESS THAN (1478448000) ENGINE = InnoDB,
 PARTITION p67 VALUES LESS THAN (1479052800) ENGINE = InnoDB,
 PARTITION p68 VALUES LESS THAN (1479657600) ENGINE = InnoDB,
 PARTITION p69 VALUES LESS THAN (1480262400) ENGINE = InnoDB,
 PARTITION p70 VALUES LESS THAN (1480867200) ENGINE = InnoDB,
 PARTITION p71 VALUES LESS THAN (1481472000) ENGINE = InnoDB,
 PARTITION p72 VALUES LESS THAN (1482076800) ENGINE = InnoDB,
 PARTITION p73 VALUES LESS THAN (1482681600) ENGINE = InnoDB,
 PARTITION p74 VALUES LESS THAN (1483286400) ENGINE = InnoDB,
 PARTITION p75 VALUES LESS THAN (1483891200) ENGINE = InnoDB,
 PARTITION p76 VALUES LESS THAN (1484496000) ENGINE = InnoDB,
 PARTITION p77 VALUES LESS THAN (1485100800) ENGINE = InnoDB,
 PARTITION p78 VALUES LESS THAN (1485705600) ENGINE = InnoDB,
 PARTITION p79 VALUES LESS THAN (1486310400) ENGINE = InnoDB,
 PARTITION p80 VALUES LESS THAN (1486915200) ENGINE = InnoDB,
 PARTITION p81 VALUES LESS THAN (1487520000) ENGINE = InnoDB,
 PARTITION p82 VALUES LESS THAN (1488124800) ENGINE = InnoDB,
 PARTITION p83 VALUES LESS THAN (1488729600) ENGINE = InnoDB,
 PARTITION p84 VALUES LESS THAN (1489334400) ENGINE = InnoDB,
 PARTITION p85 VALUES LESS THAN (1489939200) ENGINE = InnoDB,
 PARTITION p86 VALUES LESS THAN (1490544000) ENGINE = InnoDB,
 PARTITION p87 VALUES LESS THAN (1491148800) ENGINE = InnoDB,
 PARTITION p88 VALUES LESS THAN (1491753600) ENGINE = InnoDB,
 PARTITION p89 VALUES LESS THAN (1492358400) ENGINE = InnoDB,
 PARTITION p90 VALUES LESS THAN (1492963200) ENGINE = InnoDB,
 PARTITION p91 VALUES LESS THAN (1493568000) ENGINE = InnoDB,
 PARTITION p92 VALUES LESS THAN (1494172800) ENGINE = InnoDB,
 PARTITION p93 VALUES LESS THAN (1494777600) ENGINE = InnoDB,
 PARTITION p94 VALUES LESS THAN (1495382400) ENGINE = InnoDB,
 PARTITION p95 VALUES LESS THAN (1495987200) ENGINE = InnoDB,
 PARTITION p96 VALUES LESS THAN (1496592000) ENGINE = InnoDB,
 PARTITION p97 VALUES LESS THAN (1497196800) ENGINE = InnoDB,
 PARTITION p98 VALUES LESS THAN (1497801600) ENGINE = InnoDB,
 PARTITION p99 VALUES LESS THAN (1498406400) ENGINE = InnoDB,
 PARTITION p100 VALUES LESS THAN (1499011200) ENGINE = InnoDB,
 PARTITION p101 VALUES LESS THAN (1499616000) ENGINE = InnoDB,
 PARTITION p102 VALUES LESS THAN (1500220800) ENGINE = InnoDB,
 PARTITION p103 VALUES LESS THAN (1500825600) ENGINE = InnoDB,
 PARTITION p104 VALUES LESS THAN (1501430400) ENGINE = InnoDB,
 PARTITION p105 VALUES LESS THAN (1502035200) ENGINE = InnoDB,
 PARTITION p106 VALUES LESS THAN (1502640000) ENGINE = InnoDB,
 PARTITION p107 VALUES LESS THAN (1503244800) ENGINE = InnoDB,
 PARTITION p108 VALUES LESS THAN (1503849600) ENGINE = InnoDB,
 PARTITION p109 VALUES LESS THAN (1504454400) ENGINE = InnoDB,
 PARTITION p110 VALUES LESS THAN (1505059200) ENGINE = InnoDB,
 PARTITION p111 VALUES LESS THAN (1505664000) ENGINE = InnoDB,
 PARTITION p112 VALUES LESS THAN (1506268800) ENGINE = InnoDB,
 PARTITION p113 VALUES LESS THAN (1506873600) ENGINE = InnoDB,
 PARTITION p114 VALUES LESS THAN (1507478400) ENGINE = InnoDB,
 PARTITION p115 VALUES LESS THAN (1508083200) ENGINE = InnoDB,
 PARTITION p116 VALUES LESS THAN (1508688000) ENGINE = InnoDB,
 PARTITION p117 VALUES LESS THAN (1509292800) ENGINE = InnoDB,
 PARTITION p118 VALUES LESS THAN (1509897600) ENGINE = InnoDB,
 PARTITION p119 VALUES LESS THAN (1510502400) ENGINE = InnoDB,
 PARTITION p120 VALUES LESS THAN (1511107200) ENGINE = InnoDB,
 PARTITION p121 VALUES LESS THAN (1511712000) ENGINE = InnoDB,
 PARTITION p122 VALUES LESS THAN (1512316800) ENGINE = InnoDB,
 PARTITION p123 VALUES LESS THAN (1512921600) ENGINE = InnoDB,
 PARTITION p124 VALUES LESS THAN (1513526400) ENGINE = InnoDB,
 PARTITION p125 VALUES LESS THAN (1514131200) ENGINE = InnoDB,
 PARTITION p126 VALUES LESS THAN (1514736000) ENGINE = InnoDB,
 PARTITION p127 VALUES LESS THAN (1515340800) ENGINE = InnoDB,
 PARTITION p128 VALUES LESS THAN (1515945600) ENGINE = InnoDB,
 PARTITION p129 VALUES LESS THAN (1516550400) ENGINE = InnoDB,
 PARTITION p130 VALUES LESS THAN (1517155200) ENGINE = InnoDB,
 PARTITION p131 VALUES LESS THAN (1517760000) ENGINE = InnoDB,
 PARTITION p132 VALUES LESS THAN (1518364800) ENGINE = InnoDB,
 PARTITION p133 VALUES LESS THAN (1518969600) ENGINE = InnoDB,
 PARTITION p134 VALUES LESS THAN (1519574400) ENGINE = InnoDB,
 PARTITION p135 VALUES LESS THAN (1520179200) ENGINE = InnoDB,
 PARTITION p136 VALUES LESS THAN (1520784000) ENGINE = InnoDB,
 PARTITION p137 VALUES LESS THAN (1521388800) ENGINE = InnoDB,
 PARTITION p138 VALUES LESS THAN (1521993600) ENGINE = InnoDB,
 PARTITION p139 VALUES LESS THAN (1522598400) ENGINE = InnoDB,
 PARTITION p140 VALUES LESS THAN (1523203200) ENGINE = InnoDB,
 PARTITION p141 VALUES LESS THAN (1523808000) ENGINE = InnoDB,
 PARTITION p142 VALUES LESS THAN (1524412800) ENGINE = InnoDB,
 PARTITION p143 VALUES LESS THAN (1525017600) ENGINE = InnoDB,
 PARTITION p144 VALUES LESS THAN (1525622400) ENGINE = InnoDB,
 PARTITION p145 VALUES LESS THAN (1526227200) ENGINE = InnoDB,
 PARTITION p146 VALUES LESS THAN (1526832000) ENGINE = InnoDB,
 PARTITION p147 VALUES LESS THAN (1527436800) ENGINE = InnoDB,
 PARTITION p148 VALUES LESS THAN (1528041600) ENGINE = InnoDB,
 PARTITION p149 VALUES LESS THAN (1528646400) ENGINE = InnoDB,
 PARTITION p150 VALUES LESS THAN (1529251200) ENGINE = InnoDB,
 PARTITION p151 VALUES LESS THAN (1529856000) ENGINE = InnoDB,
 PARTITION p152 VALUES LESS THAN (1530460800) ENGINE = InnoDB,
 PARTITION p153 VALUES LESS THAN (1531065600) ENGINE = InnoDB,
 PARTITION p154 VALUES LESS THAN (1531670400) ENGINE = InnoDB,
 PARTITION p155 VALUES LESS THAN (1532275200) ENGINE = InnoDB,
 PARTITION p156 VALUES LESS THAN (1532880000) ENGINE = InnoDB,
 PARTITION p157 VALUES LESS THAN (1533484800) ENGINE = InnoDB,
 PARTITION p158 VALUES LESS THAN (1534089600) ENGINE = InnoDB,
 PARTITION p159 VALUES LESS THAN (1534694400) ENGINE = InnoDB,
 PARTITION p160 VALUES LESS THAN (1535299200) ENGINE = InnoDB,
 PARTITION p161 VALUES LESS THAN (1535904000) ENGINE = InnoDB,
 PARTITION p162 VALUES LESS THAN (1536508800) ENGINE = InnoDB,
 PARTITION p163 VALUES LESS THAN (1537113600) ENGINE = InnoDB,
 PARTITION p164 VALUES LESS THAN (1537718400) ENGINE = InnoDB,
 PARTITION p165 VALUES LESS THAN (1538323200) ENGINE = InnoDB,
 PARTITION p166 VALUES LESS THAN (1538928000) ENGINE = InnoDB,
 PARTITION p167 VALUES LESS THAN (1539532800) ENGINE = InnoDB,
 PARTITION p168 VALUES LESS THAN (1540137600) ENGINE = InnoDB,
 PARTITION p169 VALUES LESS THAN (1540742400) ENGINE = InnoDB,
 PARTITION p170 VALUES LESS THAN (1541347200) ENGINE = InnoDB,
 PARTITION p171 VALUES LESS THAN (1541952000) ENGINE = InnoDB,
 PARTITION p172 VALUES LESS THAN (1542556800) ENGINE = InnoDB,
 PARTITION p173 VALUES LESS THAN (1543161600) ENGINE = InnoDB,
 PARTITION p174 VALUES LESS THAN (1543766400) ENGINE = InnoDB,
 PARTITION p175 VALUES LESS THAN (1544371200) ENGINE = InnoDB,
 PARTITION p176 VALUES LESS THAN (1544976000) ENGINE = InnoDB,
 PARTITION p177 VALUES LESS THAN (1545580800) ENGINE = InnoDB,
 PARTITION p178 VALUES LESS THAN (1546185600) ENGINE = InnoDB,
 PARTITION p179 VALUES LESS THAN (1546790400) ENGINE = InnoDB,
 PARTITION p180 VALUES LESS THAN (1547395200) ENGINE = InnoDB,
 PARTITION p181 VALUES LESS THAN (1548000000) ENGINE = InnoDB,
 PARTITION p182 VALUES LESS THAN (1548604800) ENGINE = InnoDB,
 PARTITION p183 VALUES LESS THAN (1549209600) ENGINE = InnoDB,
 PARTITION p184 VALUES LESS THAN (1549814400) ENGINE = InnoDB,
 PARTITION p185 VALUES LESS THAN (1550419200) ENGINE = InnoDB,
 PARTITION p186 VALUES LESS THAN (1551024000) ENGINE = InnoDB,
 PARTITION p187 VALUES LESS THAN (1551628800) ENGINE = InnoDB,
 PARTITION p188 VALUES LESS THAN (1552233600) ENGINE = InnoDB,
 PARTITION p189 VALUES LESS THAN (1552838400) ENGINE = InnoDB,
 PARTITION p190 VALUES LESS THAN (1553443200) ENGINE = InnoDB,
 PARTITION p191 VALUES LESS THAN (1554048000) ENGINE = InnoDB,
 PARTITION p192 VALUES LESS THAN (1554652800) ENGINE = InnoDB,
 PARTITION p193 VALUES LESS THAN (1555257600) ENGINE = InnoDB,
 PARTITION p194 VALUES LESS THAN (1555862400) ENGINE = InnoDB,
 PARTITION p195 VALUES LESS THAN (1556467200) ENGINE = InnoDB,
 PARTITION p196 VALUES LESS THAN (1557072000) ENGINE = InnoDB,
 PARTITION p197 VALUES LESS THAN (1557676800) ENGINE = InnoDB,
 PARTITION p198 VALUES LESS THAN (1558281600) ENGINE = InnoDB,
 PARTITION p199 VALUES LESS THAN (1558886400) ENGINE = InnoDB,
 PARTITION p200 VALUES LESS THAN (1559491200) ENGINE = InnoDB,
 PARTITION p201 VALUES LESS THAN (1560096000) ENGINE = InnoDB,
 PARTITION p202 VALUES LESS THAN (1560700800) ENGINE = InnoDB,
 PARTITION p203 VALUES LESS THAN (1561305600) ENGINE = InnoDB,
 PARTITION p204 VALUES LESS THAN (1561910400) ENGINE = InnoDB,
 PARTITION p205 VALUES LESS THAN (1562515200) ENGINE = InnoDB,
 PARTITION p206 VALUES LESS THAN (1563120000) ENGINE = InnoDB,
 PARTITION p207 VALUES LESS THAN (1563724800) ENGINE = InnoDB,
 PARTITION p208 VALUES LESS THAN (1564329600) ENGINE = InnoDB,
 PARTITION p209 VALUES LESS THAN (1564934400) ENGINE = InnoDB,
 PARTITION p210 VALUES LESS THAN (1565539200) ENGINE = InnoDB,
 PARTITION p211 VALUES LESS THAN (1566144000) ENGINE = InnoDB,
 PARTITION p212 VALUES LESS THAN (1566748800) ENGINE = InnoDB,
 PARTITION p213 VALUES LESS THAN (1567353600) ENGINE = InnoDB,
 PARTITION p214 VALUES LESS THAN (1567958400) ENGINE = InnoDB,
 PARTITION p215 VALUES LESS THAN (1568563200) ENGINE = InnoDB,
 PARTITION p216 VALUES LESS THAN (1569168000) ENGINE = InnoDB,
 PARTITION p217 VALUES LESS THAN (1569772800) ENGINE = InnoDB,
 PARTITION p218 VALUES LESS THAN (1570377600) ENGINE = InnoDB,
 PARTITION p219 VALUES LESS THAN (1570982400) ENGINE = InnoDB,
 PARTITION p220 VALUES LESS THAN (1571587200) ENGINE = InnoDB,
 PARTITION p221 VALUES LESS THAN (1572192000) ENGINE = InnoDB,
 PARTITION p222 VALUES LESS THAN (1572796800) ENGINE = InnoDB,
 PARTITION p223 VALUES LESS THAN (1573401600) ENGINE = InnoDB,
 PARTITION p224 VALUES LESS THAN (1574006400) ENGINE = InnoDB,
 PARTITION p225 VALUES LESS THAN (1574611200) ENGINE = InnoDB,
 PARTITION p226 VALUES LESS THAN (1575216000) ENGINE = InnoDB,
 PARTITION p227 VALUES LESS THAN (1575820800) ENGINE = InnoDB,
 PARTITION p228 VALUES LESS THAN (1576425600) ENGINE = InnoDB,
 PARTITION p229 VALUES LESS THAN (1577030400) ENGINE = InnoDB,
 PARTITION p230 VALUES LESS THAN (1577635200) ENGINE = InnoDB,
 PARTITION p231 VALUES LESS THAN (1578240000) ENGINE = InnoDB,
 PARTITION p232 VALUES LESS THAN (1578844800) ENGINE = InnoDB,
 PARTITION p233 VALUES LESS THAN (1579449600) ENGINE = InnoDB,
 PARTITION p234 VALUES LESS THAN (1580054400) ENGINE = InnoDB,
 PARTITION p235 VALUES LESS THAN (1580659200) ENGINE = InnoDB,
 PARTITION p236 VALUES LESS THAN (1581264000) ENGINE = InnoDB,
 PARTITION p237 VALUES LESS THAN (1581868800) ENGINE = InnoDB,
 PARTITION p238 VALUES LESS THAN (1582473600) ENGINE = InnoDB,
 PARTITION p239 VALUES LESS THAN (1583078400) ENGINE = InnoDB,
 PARTITION p240 VALUES LESS THAN (1583683200) ENGINE = InnoDB,
 PARTITION p241 VALUES LESS THAN (1584288000) ENGINE = InnoDB,
 PARTITION p242 VALUES LESS THAN (1584892800) ENGINE = InnoDB,
 PARTITION p243 VALUES LESS THAN (1585497600) ENGINE = InnoDB,
 PARTITION p244 VALUES LESS THAN (1586102400) ENGINE = InnoDB,
 PARTITION p245 VALUES LESS THAN (1586707200) ENGINE = InnoDB,
 PARTITION p246 VALUES LESS THAN (1587312000) ENGINE = InnoDB,
 PARTITION p247 VALUES LESS THAN (1587916800) ENGINE = InnoDB,
 PARTITION p248 VALUES LESS THAN (1588521600) ENGINE = InnoDB,
 PARTITION p249 VALUES LESS THAN (1589126400) ENGINE = InnoDB,
 PARTITION p250 VALUES LESS THAN (1589731200) ENGINE = InnoDB,
 PARTITION p251 VALUES LESS THAN (1590336000) ENGINE = InnoDB,
 PARTITION p252 VALUES LESS THAN (1590940800) ENGINE = InnoDB,
 PARTITION p253 VALUES LESS THAN (1591545600) ENGINE = InnoDB,
 PARTITION p254 VALUES LESS THAN (1592150400) ENGINE = InnoDB,
 PARTITION p255 VALUES LESS THAN (1592755200) ENGINE = InnoDB,
 PARTITION p256 VALUES LESS THAN (1593360000) ENGINE = InnoDB,
 PARTITION p257 VALUES LESS THAN (1593964800) ENGINE = InnoDB,
 PARTITION p258 VALUES LESS THAN (1594569600) ENGINE = InnoDB,
 PARTITION p259 VALUES LESS THAN (1595174400) ENGINE = InnoDB,
 PARTITION pmore VALUES LESS THAN MAXVALUE ENGINE = InnoDB) */;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trends_uint`
--

LOCK TABLES `trends_uint` WRITE;
/*!40000 ALTER TABLE `trends_uint` DISABLE KEYS */;
/*!40000 ALTER TABLE `trends_uint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trigger_depends`
--

DROP TABLE IF EXISTS `trigger_depends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trigger_depends` (
  `tenantid` varchar(64) DEFAULT '0',
  `triggerdepid` bigint(20) unsigned NOT NULL,
  `triggerid_down` bigint(20) unsigned NOT NULL,
  `triggerid_up` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`triggerdepid`),
  UNIQUE KEY `trigger_depends_1` (`triggerid_down`,`triggerid_up`),
  KEY `trigger_depends_2` (`triggerid_up`),
  CONSTRAINT `c_trigger_depends_1` FOREIGN KEY (`triggerid_down`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE,
  CONSTRAINT `c_trigger_depends_2` FOREIGN KEY (`triggerid_up`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trigger_depends`
--

LOCK TABLES `trigger_depends` WRITE;
/*!40000 ALTER TABLE `trigger_depends` DISABLE KEYS */;
/*!40000 ALTER TABLE `trigger_depends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trigger_discovery`
--

DROP TABLE IF EXISTS `trigger_discovery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trigger_discovery` (
  `tenantid` varchar(64) DEFAULT '0',
  `triggerdiscoveryid` bigint(20) unsigned NOT NULL,
  `triggerid` bigint(20) unsigned NOT NULL,
  `parent_triggerid` bigint(20) unsigned NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`triggerdiscoveryid`),
  UNIQUE KEY `trigger_discovery_1` (`triggerid`,`parent_triggerid`),
  KEY `trigger_discovery_2` (`parent_triggerid`),
  CONSTRAINT `c_trigger_discovery_1` FOREIGN KEY (`triggerid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE,
  CONSTRAINT `c_trigger_discovery_2` FOREIGN KEY (`parent_triggerid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trigger_discovery`
--

LOCK TABLES `trigger_discovery` WRITE;
/*!40000 ALTER TABLE `trigger_discovery` DISABLE KEYS */;
/*!40000 ALTER TABLE `trigger_discovery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `triggers`
--

DROP TABLE IF EXISTS `triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `triggers` (
  `tenantid` varchar(64) DEFAULT '0',
  `triggerid` bigint(20) unsigned NOT NULL,
  `expression` varchar(2048) NOT NULL DEFAULT '',
  `description` varchar(255) NOT NULL DEFAULT '',
  `url` varchar(255) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `value` int(11) NOT NULL DEFAULT '0',
  `priority` int(11) NOT NULL DEFAULT '0',
  `lastchange` int(11) NOT NULL DEFAULT '0',
  `comments` text NOT NULL,
  `error` varchar(128) NOT NULL DEFAULT '',
  `templateid` bigint(20) unsigned DEFAULT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `flags` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`triggerid`),
  KEY `triggers_1` (`status`),
  KEY `triggers_2` (`value`),
  KEY `triggers_3` (`templateid`),
  CONSTRAINT `c_triggers_1` FOREIGN KEY (`templateid`) REFERENCES `triggers` (`triggerid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `triggers`
--

LOCK TABLES `triggers` WRITE;
/*!40000 ALTER TABLE `triggers` DISABLE KEYS */;
INSERT INTO `triggers` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',67,'{13350}>0','Ceph cluster has degraded PGs','',0,0,2,0,'Ceph has not replicated some objects in the placement group the correct number of times yet.','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',68,'{13351}>0','Ceph cluster has down PGs','',0,0,3,0,'At least a replica with necessary data is down, so the placement group is offline.','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',126,'{13417}#1','Alive {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',127,'{13418}>100','Archivelog','',0,0,2,0,'4849474820417263686976656C6F672067656E65726174696F6E','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',130,'{13424}<1','No data received from Orabbix','',0,0,5,0,'4E6F2064617461207265747269657665642066726F6D204F72616262697820666F72206461746162617365','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',136,'{13433}>10','Active Session {HOSTNAME}','',0,0,5,0,'44414E474552','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',137,'{13434}>20','Active Session {HOSTNAME}','',0,0,5,0,'44414E474552','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',138,'{13435}#1','Alive {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',139,'{13436}>100','Archivelog','',0,0,2,0,'4849474820417263686976656C6F672067656E65726174696F6E','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',140,'{13437}=0','Audit on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',141,'{13438}<50|{13439}<50|{13440}<50|{13441}<50','Hit Ratio on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',142,'{13442}=0','Locks on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',143,'{13443}<1','No data received from Orabbix','',0,0,5,0,'4E6F2064617461207265747269657665642066726F6D204F72616262697820666F72206461746162617365','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',144,'({13444}*100/{13445})>90','PGA Alarm on {HOSTNAME}','http://10.10.64.93/overview.php',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',145,'({13446}*100/{13447})>80','Process Alarm on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',146,'({13448}*100/{13449})>80','Session Alarm on {HOSTNAME}','http://10.10.64.93/overview.php',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',147,'{13450}=0','Tablespaces on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',148,'{13451}=0','User Locked on {HOSTNAME}','',0,0,5,0,'20','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',324,'{13636}>0','Inbound packet on interface {#SNMPVALUE} contained error that prevents delivery to a higher-layer protocol','',0,0,2,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',325,'{13637}>0','Inbound packet on interface {#SNMPVALUE} was chosen to be discared','',0,0,2,0,'One possible reason for discarding such a packet could be to free up buffer space','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',326,'{13638}>0','Inbound packet on interface {#SNMPVALUE} was chosen to be discared because of an unknown or unsupported protocol','',0,0,2,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',327,'{13639}#{13640}','Interface {#SNMPVALUE} in undesired state: {ITEM.VALUE2}','',0,0,2,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',328,'{13641}>0','Outbound packet on interface {#SNMPVALUE} could not be transmitted because of error','',0,0,2,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',329,'{13642}>0','Outbound packet on interface {#SNMPVALUE} was chosen to be discared','',0,0,2,0,'One possible reason for discarding such a packet could be to free up buffer space.','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',330,'{13643}#1','Memory pool {#SNMPVALUE} invalid','',0,0,2,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',331,'{13644}>70','CPU is highly loaded','',0,0,2,0,'','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',332,'{13645}>90','CPU is overloaded','',0,0,3,0,'','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',333,'{13646}=0','Device not reachable','',0,0,3,0,'','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',334,'{13647}<0','{HOST.NAME} has just been restarted','',0,0,1,0,'','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',335,'{13648}>100','High number of IOs pending on {HOST.NAME}','',0,0,2,0,'','',NULL,0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',337,'{13650}=1','Operational status was changed on {HOST.NAME} interface {#SNMPVALUE}','',0,0,1,0,'','',NULL,0,0,2),('5e4d0a6d39a44b9c906a3173b448aa4a',338,'{13651}=1','Operational status was changed on {HOST.NAME} interface {#SNMPVALUE}','',0,0,1,0,'','',337,0,0,2);
/*!40000 ALTER TABLE `triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_history`
--

DROP TABLE IF EXISTS `user_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_history` (
  `tenantid` varchar(64) DEFAULT '0',
  `userhistoryid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `title1` varchar(255) NOT NULL DEFAULT '',
  `url1` varchar(255) NOT NULL DEFAULT '',
  `title2` varchar(255) NOT NULL DEFAULT '',
  `url2` varchar(255) NOT NULL DEFAULT '',
  `title3` varchar(255) NOT NULL DEFAULT '',
  `url3` varchar(255) NOT NULL DEFAULT '',
  `title4` varchar(255) NOT NULL DEFAULT '',
  `url4` varchar(255) NOT NULL DEFAULT '',
  `title5` varchar(255) NOT NULL DEFAULT '',
  `url5` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`userhistoryid`),
  UNIQUE KEY `user_history_1` (`userid`),
  CONSTRAINT `c_user_history_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_history`
--

LOCK TABLES `user_history` WRITE;
/*!40000 ALTER TABLE `user_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `tenantid` varchar(64) NOT NULL DEFAULT '0',
  `userid` varchar(64) NOT NULL,
  `alias` varchar(100) NOT NULL DEFAULT '',
  `name` varchar(100) NOT NULL DEFAULT '',
  `surname` varchar(100) NOT NULL DEFAULT '',
  `passwd` char(32) NOT NULL DEFAULT '',
  `url` varchar(255) NOT NULL DEFAULT '',
  `autologin` int(11) NOT NULL DEFAULT '0',
  `autologout` int(11) NOT NULL DEFAULT '900',
  `lang` varchar(5) NOT NULL DEFAULT 'en_GB',
  `refresh` int(11) NOT NULL DEFAULT '30',
  `type` int(11) NOT NULL DEFAULT '1',
  `theme` varchar(128) NOT NULL DEFAULT 'default',
  `attempt_failed` int(11) NOT NULL DEFAULT '0',
  `attempt_ip` varchar(39) NOT NULL DEFAULT '',
  `attempt_clock` int(11) NOT NULL DEFAULT '0',
  `rows_per_page` int(11) NOT NULL DEFAULT '50',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`userid`,`tenantid`),
  KEY `users_1` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a','1','admin','admin','','37C74DC82B72A0816A3DBB74BFF1E560','',1,0,'zh_CN',30,3,'default',0,'192.168.30.112',1434063390,50,1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_groups`
--

DROP TABLE IF EXISTS `users_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users_groups` (
  `tenantid` varchar(64) DEFAULT '0',
  `id` bigint(20) unsigned NOT NULL,
  `usrgrpid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_groups_1` (`usrgrpid`,`userid`),
  KEY `users_groups_2` (`userid`),
  CONSTRAINT `c_users_groups_1` FOREIGN KEY (`usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`) ON DELETE CASCADE,
  CONSTRAINT `c_users_groups_2` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_groups`
--

LOCK TABLES `users_groups` WRITE;
/*!40000 ALTER TABLE `users_groups` DISABLE KEYS */;
INSERT INTO `users_groups` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',2,8,'2'),('5e4d0a6d39a44b9c906a3173b448aa4a',4,7,'1'),('5e4d0a6d39a44b9c906a3173b448aa4a',5,7,'f6bac26c198244ca9c1cb7a663f59970');
/*!40000 ALTER TABLE `users_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usrgrp`
--

DROP TABLE IF EXISTS `usrgrp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usrgrp` (
  `tenantid` varchar(64) DEFAULT '0',
  `usrgrpid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gui_access` int(11) NOT NULL DEFAULT '0',
  `users_status` int(11) NOT NULL DEFAULT '0',
  `debug_mode` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`usrgrpid`),
  KEY `usrgrp_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usrgrp`
--

LOCK TABLES `usrgrp` WRITE;
/*!40000 ALTER TABLE `usrgrp` DISABLE KEYS */;
INSERT INTO `usrgrp` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',7,'iRadar administrators',0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',8,'Guests',0,0,0),('5e4d0a6d39a44b9c906a3173b448aa4a',9,'Disabled',0,1,0),('5e4d0a6d39a44b9c906a3173b448aa4a',11,'Enabled debug mode',0,0,1),('5e4d0a6d39a44b9c906a3173b448aa4a',12,'No access to the frontend',2,0,0);
/*!40000 ALTER TABLE `usrgrp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `valuemaps`
--

DROP TABLE IF EXISTS `valuemaps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `valuemaps` (
  `tenantid` varchar(64) DEFAULT '0',
  `valuemapid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`valuemapid`),
  KEY `valuemaps_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `valuemaps`
--

LOCK TABLES `valuemaps` WRITE;
/*!40000 ALTER TABLE `valuemaps` DISABLE KEYS */;
INSERT INTO `valuemaps` VALUES ('5e4d0a6d39a44b9c906a3173b448aa4a',1,'IaaS Service state'),('5e4d0a6d39a44b9c906a3173b448aa4a',2,'Host status'),('5e4d0a6d39a44b9c906a3173b448aa4a',3,'Windows service state'),('5e4d0a6d39a44b9c906a3173b448aa4a',8,'SNMP interface status (ifOperStatus)'),('5e4d0a6d39a44b9c906a3173b448aa4a',9,'SNMP device status (hrDeviceStatus)'),('5e4d0a6d39a44b9c906a3173b448aa4a',10,'iRadar agent ping status'),('5e4d0a6d39a44b9c906a3173b448aa4a',11,'SNMP interface status (ifAdminStatus)'),('5e4d0a6d39a44b9c906a3173b448aa4a',20,'MS SQL Server database state'),('5e4d0a6d39a44b9c906a3173b448aa4a',21,'0:No, 1:Yes'),('5e4d0a6d39a44b9c906a3173b448aa4a',22,'SNMP (Type: CiscoEnvMonState)'),('5e4d0a6d39a44b9c906a3173b448aa4a',23,'SNMP (Type: TruthValue)'),('5e4d0a6d39a44b9c906a3173b448aa4a',24,'SNMP interface (dot3StatsDuplexStatus)'),('5e4d0a6d39a44b9c906a3173b448aa4a',25,'SNMP interface (ifType)'),('5e4d0a6d39a44b9c906a3173b448aa4a',26,'Route Type'),('5e4d0a6d39a44b9c906a3173b448aa4a',27,'ARP Type');
/*!40000 ALTER TABLE `valuemaps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'imon.std'
--
/*!50003 DROP FUNCTION IF EXISTS `TOREGEXP` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
CREATE DEFINER=`*`@`%` FUNCTION `TOREGEXP`(W VARCHAR(1024)) RETURNS varchar(1024) CHARSET utf8
    SQL SECURITY INVOKER
BEGIN
      SET W = REPLACE(W,'\\','\\\\');
      SET W = REPLACE(W,'*','\\*');
      SET W = REPLACE(W,'{','\\{');
      SET W = REPLACE(W,'}','\\}');
      SET W = REPLACE(W,'[','\\[');
      SET W = REPLACE(W,']','\\]');
      SET W = REPLACE(W,'(','\\(');
      SET W = REPLACE(W,')','\\}');
      SET W = REPLACE(W,'+','\\+');
      SET W = REPLACE(W,'?','\\?');
      SET W = REPLACE(W,'.','\\.');
      SET W = REPLACE(W,'$','\\$');
      SET W = REPLACE(W,'^','\\^');
      SET W = REPLACE(W,'|','\\|');
      RETURN (W);
    END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-04-17  7:12:09
