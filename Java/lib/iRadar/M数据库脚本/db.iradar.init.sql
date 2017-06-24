/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.6.21-69.0 : Database - iradar
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`iradar` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `iradar`;

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `userid` bigint(20) unsigned NOT NULL,
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

insert  into `users`(`userid`,`alias`,`name`,`surname`,`passwd`,`url`,`autologin`,`autologout`,`lang`,`refresh`,`type`,`theme`,`attempt_failed`,`attempt_ip`,`attempt_clock`,`rows_per_page`) values (1,'Admin','iRadar','Administrator','37c74dc82b72a0816a3dbb74bff1e560','',1,0,'zh_CN',30,3,'default',0,'192.168.14.82',1414323333,10);
insert  into `users`(`userid`,`alias`,`name`,`surname`,`passwd`,`url`,`autologin`,`autologout`,`lang`,`refresh`,`type`,`theme`,`attempt_failed`,`attempt_ip`,`attempt_clock`,`rows_per_page`) values (2,'guest','','','d41d8cd98f00b204e9800998ecf8427e','',0,900,'zh_CN',30,1,'default',0,'',0,50);

/*Table structure for table `users_groups` */

DROP TABLE IF EXISTS `users_groups`;

CREATE TABLE `users_groups` (
  `id` bigint(20) unsigned NOT NULL,
  `usrgrpid` bigint(20) unsigned NOT NULL,
  `userid` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_groups_1` (`usrgrpid`,`userid`),
  KEY `users_groups_2` (`userid`),
  CONSTRAINT `c_users_groups_1` FOREIGN KEY (`usrgrpid`) REFERENCES `usrgrp` (`usrgrpid`) ON DELETE CASCADE,
  CONSTRAINT `c_users_groups_2` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `users_groups` */

insert  into `users_groups`(`id`,`usrgrpid`,`userid`) values (4,7,1);
insert  into `users_groups`(`id`,`usrgrpid`,`userid`) values (2,8,2);

/*Table structure for table `usrgrp` */

DROP TABLE IF EXISTS `usrgrp`;

CREATE TABLE `usrgrp` (
  `usrgrpid` bigint(20) unsigned NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `gui_access` int(11) NOT NULL DEFAULT '0',
  `users_status` int(11) NOT NULL DEFAULT '0',
  `debug_mode` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`usrgrpid`),
  KEY `usrgrp_1` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `usrgrp` */

insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (7,'iRadar administrators',0,0,0);
insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (8,'Guests',0,0,0);
insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (9,'Disabled',0,0,0);
insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (11,'Enabled debug mode',0,0,0);
insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (12,'No access to the frontend',0,0,0);
insert  into `usrgrp`(`usrgrpid`,`name`,`gui_access`,`users_status`,`debug_mode`) values (13,'1',0,0,0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
