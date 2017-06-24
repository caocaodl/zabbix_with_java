/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.1.73 : Database - iradar
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`iradar` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `iradar`;

/*Table structure for table `acknowledges` */

DROP TABLE IF EXISTS `acknowledges`;

CREATE TABLE `acknowledges` (
  `tenantid` varchar(64) DEFAULT NULL,
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

/*Data for the table `acknowledges` */

/*Table structure for table `alerts` */

DROP TABLE IF EXISTS `alerts`;

CREATE TABLE `alerts` (
  `tenantid` varchar(64) DEFAULT NULL,
  `alertid` bigint(20) unsigned NOT NULL,
  `actionid` bigint(20) unsigned NOT NULL,
  `eventid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  `clock` int(11) NOT NULL DEFAULT '0',
  `mediatypeid` bigint(20) unsigned DEFAULT NULL,
  `sendto` varchar(100) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `message` text NOT NULL,
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
  CONSTRAINT `c_alerts_1` FOREIGN KEY (`actionid`) REFERENCES `actions` (`actionid`) ON DELETE CASCADE,
  CONSTRAINT `c_alerts_2` FOREIGN KEY (`eventid`) REFERENCES `events` (`eventid`) ON DELETE CASCADE,
  CONSTRAINT `c_alerts_3` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE,
  CONSTRAINT `c_alerts_4` FOREIGN KEY (`mediatypeid`) REFERENCES `media_type` (`mediatypeid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `alerts` */

/*Table structure for table `auditlog` */

DROP TABLE IF EXISTS `auditlog`;

CREATE TABLE `auditlog` (
  `tenantid` varchar(64) DEFAULT NULL,
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

/*Data for the table `auditlog` */

/*Table structure for table `media` */

DROP TABLE IF EXISTS `media`;

CREATE TABLE `media` (
  `tenantid` varchar(64) DEFAULT NULL,
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

/*Data for the table `media` */

/*Table structure for table `opmessage_usr` */

DROP TABLE IF EXISTS `opmessage_usr`;

CREATE TABLE `opmessage_usr` (
  `tenantid` varchar(64) DEFAULT NULL,
  `opmessage_usrid` bigint(20) unsigned NOT NULL,
  `operationid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  PRIMARY KEY (`opmessage_usrid`),
  UNIQUE KEY `opmessage_usr_1` (`operationid`,`userid`),
  KEY `opmessage_usr_2` (`userid`),
  CONSTRAINT `c_opmessage_usr_1` FOREIGN KEY (`operationid`) REFERENCES `operations` (`operationid`) ON DELETE CASCADE,
  CONSTRAINT `c_opmessage_usr_2` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `opmessage_usr` */

/*Table structure for table `profiles` */

DROP TABLE IF EXISTS `profiles`;

CREATE TABLE `profiles` (
  `tenantid` varchar(64) DEFAULT NULL,
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

/*Data for the table `profiles` */

/*Table structure for table `sessions` */

DROP TABLE IF EXISTS `sessions`;

CREATE TABLE `sessions` (
  `tenantid` varchar(64) DEFAULT NULL,
  `sessionid` varchar(32) NOT NULL DEFAULT '',
  `userid` varchar(64) NOT NULL,
  `lastaccess` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`sessionid`),
  KEY `sessions_1` (`userid`,`status`),
  CONSTRAINT `c_sessions_1` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sessions` */

/*Table structure for table `user_history` */

DROP TABLE IF EXISTS `user_history`;

CREATE TABLE `user_history` (
  `tenantid` varchar(64) DEFAULT NULL,
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

/*Data for the table `user_history` */

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `tenantid` varchar(64) DEFAULT NULL,
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
  PRIMARY KEY (`userid`),
  KEY `users_1` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `users` */

insert  into `users`(`tenantid`,`userid`,`alias`,`name`,`surname`,`passwd`,`url`,`autologin`,`autologout`,`lang`,`refresh`,`type`,`theme`,`attempt_failed`,`attempt_ip`,`attempt_clock`,`rows_per_page`) values ('b77cff72790044bcb876ff96f7bab7c2','b7ee2cbb2d94454f8924681c007e912d','Admin','iRadar','Administrator','37c74dc82b72a0816a3dbb74bff1e560','',1,0,'en_US',3000,3,'default',0,'192.168.137.1',1421155237,10);

/*Table structure for table `users_groups` */

DROP TABLE IF EXISTS `users_groups`;

CREATE TABLE `users_groups` (
  `tenantid` varchar(64) DEFAULT NULL,
  `id` bigint(20) unsigned NOT NULL,
  `usrgrpid` bigint(20) unsigned NOT NULL,
  `userid` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_groups_1` (`usrgrpid`,`userid`),
  KEY `users_groups_2` (`userid`),
  CONSTRAINT `c_users_groups_1` FOREIGN KEY (`usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`) ON DELETE CASCADE,
  CONSTRAINT `c_users_groups_2` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `users_groups` */

insert  into `users_groups`(`tenantid`,`id`,`usrgrpid`,`userid`) values ('b77cff72790044bcb876ff96f7bab7c2',4,7,'b7ee2cbb2d94454f8924681c007e912d');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
