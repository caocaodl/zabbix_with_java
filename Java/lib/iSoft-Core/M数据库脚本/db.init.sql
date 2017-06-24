/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.1.67 : Database - isoft_app
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`isoft_app` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `isoft_app`;

/*Table structure for table `sys_dict` */

DROP TABLE IF EXISTS `sys_dict`;

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

/*Data for the table `sys_dict` */

insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1101,'TENANT_STATUS','激活','Y','1',1,'用户状态:激活');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1102,'TENANT_STATUS','待激活','N','1',2,'用户状态:待激活');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1103,'TENANT_STATUS','禁用:资源未释放','F','1',3,'用户状态:禁用:资源未释放');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1104,'TENANT_STATUS','禁用:资源已释放','R','1',4,'用户状态:禁用:资源已释放');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1201,'USER_STATUS','待激活','N','1',1,'用户状态:待激活');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1202,'USER_STATUS','激活','Y','1',2,'用户状态:激活');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (1203,'USER_STATUS','禁用','F','1',3,'用户状态:禁用');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (2401,'YN','是','1','1',0,'是');
insert  into `sys_dict`(`id`,`type`,`dlabel`,`dkey`,`status`,`seq_no`,`note`) values (2402,'YN','否','0','1',1,'否');

/*Table structure for table `sys_func` */

DROP TABLE IF EXISTS `sys_func`;

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
  `icon` varchar(20) DEFAULT '' COMMENT '菜单图标样式',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `seq_no` int(4) NOT NULL DEFAULT '1' COMMENT '显示顺序',
  `role` int(2) NOT NULL DEFAULT '0' COMMENT '父功能ID',
  `note` varchar(50) DEFAULT '' COMMENT '所属角色',
  PRIMARY KEY (`id`,`role`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_func` */

insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0101','-1','系统管理',NULL,'','Y','N','tree','simple','','','1',2020001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('01010101','0101','角色管理','role','','Y','Y','tree','simple','','','1',2021001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('01010102','0101','操作员管理','user','','Y','Y','tree','simple','','','1',2020001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('01010103','0101','平台档案','tenantProfile','','Y','Y','tree','simple','','','1',2023001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('01010104','0101','日志管理','log','','Y','Y','tree','simple','','','1',2022001,2,'');

/*Table structure for table `sys_func_bt` */

DROP TABLE IF EXISTS `sys_func_bt`;

CREATE TABLE `sys_func_bt` (
  `id` int(50) NOT NULL AUTO_INCREMENT COMMENT '功能ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=2023003 DEFAULT CHARSET=utf8;

/*Data for the table `sys_func_bt` */

insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010001,'01010102','view','查看',NULL,'N','1',2020001,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010003,'01010102','add','新增','view','N','1',2020003,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010004,'01010102','edit','编辑','view','N','1',2020004,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010005,'01010102','active','激活','view','N','1',2020005,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010006,'01010102','forbid','禁用','view','N','1',2020006,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010007,'01010102','resume','启用','view','N','1',2020007,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010008,'01010102','delete','删除','view','N','1',2020008,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1010009,'01010102','grantRole','授权','view','N','1',2020009,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1011001,'01010101','view','查看',NULL,'N','1',2021001,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1011003,'01010101','add','新增','view','N','1',2021003,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1011004,'01010101','edit','编辑','view','N','1',2021004,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1011005,'01010101','delete','删除','view','N','1',2021005,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1011006,'01010101','grantFunc','权限设置','view','N','1',2021006,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1012001,'01010104','view','查看',NULL,'N','1',2022001,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1013001,'01010103','view','查看','view','N','1',2023001,'');
insert  into `sys_func_bt`(`id`,`fid`,`bt_name`,`bt_alias`,`bt_deps`,`bt_extra`,`status`,`seq_no`,`note`) values (1013002,'01010103','edit','编辑','view','N','1',2023002,'');

/*Table structure for table `sys_func_bt_uri` */

DROP TABLE IF EXISTS `sys_func_bt_uri`;

CREATE TABLE `sys_func_bt_uri` (
  `id` int(50) NOT NULL AUTO_INCREMENT COMMENT 'URI ID',
  `fid` varchar(50) NOT NULL DEFAULT '' COMMENT '功能ID',
  `bid` varchar(50) NOT NULL DEFAULT '' COMMENT '按钮ID',
  `entrance` char(1) DEFAULT '' COMMENT '是否模块入口',
  `uri` varchar(150) NOT NULL DEFAULT '' COMMENT '按钮URI',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '是否可用',
  `note` varchar(50) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_fid_entrance` (`fid`,`entrance`)
) ENGINE=InnoDB AUTO_INCREMENT=238 DEFAULT CHARSET=utf8;

/*Data for the table `sys_func_bt_uri` */

insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (101,'01010102','1010001','Y','/platform/UserIndex.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (102,'01010102','1010001','','/platform/UserPage.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (103,'01010102','1010003','','/platform/UserOperAdd.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (104,'01010102','1010004','','/platform/UserOperEdit.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (105,'01010102','1010005','','/platform/UserOperActive.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (106,'01010102','1010006','','/platform/UserOperForbid.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (107,'01010102','1010007','','/platform/UserOperResume.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (108,'01010102','1010008','','/platform/UserOperDel.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (109,'01010102','1010009','','/platform/UserRoleTree.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (110,'01010102','1010009','','/platform/UserGrantRoles.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (111,'01010101','1011001','Y','/platform/RoleIndex.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (112,'01010101','1011001','','/platform/RolePage.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (113,'01010101','1011003','','/platform/RoleOperAdd.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (114,'01010101','1011004','','/platform/RoleOperEdit.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (115,'01010101','1011005','','/platform/RoleOperDel.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (116,'01010101','1011006','','/platform/RoleFuncTree.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (117,'01010101','1011006','','/platform/RoleGrantFuncs.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (118,'01010104','1012001','Y','/platform/LogIndex.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (119,'01010104','1012001','','/platform/LogPage.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (120,'01010103','1013001','Y','/platform/ProfTenant.action','1','');
insert  into `sys_func_bt_uri`(`id`,`fid`,`bid`,`entrance`,`uri`,`status`,`note`) values (121,'01010103','1013002','','/platform/ProfTenantEdit.action','1','');

/*Table structure for table `sys_id` */

DROP TABLE IF EXISTS `sys_id`;

CREATE TABLE `sys_id` (
  `idspace` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `counter` bigint(20) NOT NULL,
  `increment_by` int(11) DEFAULT '100',
  `note` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`idspace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_id` */

insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('FLAVOR_ID',64100,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('IMAGE_ID',9000,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('OS_ORGANIZATION',1290,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('PaaS_TENANT',7100,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_FUNC',180,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_FUNC_BT',2034010,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_FUNC_BY_URL',301,1);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_FUNC_SEQ',2070,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_FUNC_SON',20,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_LOG',321815,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_NOTICE',720,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_PATROL',61,1);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_ROLE',18900,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_ROLE_FUNC',15000,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_SERVICE',6930,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_TENANT',102170,5);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_USER',252400,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('SYS_USER_ROLE',9800,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('TENANT_ORDER',5640,10);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('USERSTRATEGY',2800,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('VDESKTOPS',10200,100);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('VLAN',628,1);
insert  into `sys_id`(`idspace`,`counter`,`increment_by`) values ('VM',19000,100);

/*Table structure for table `sys_logs` */

DROP TABLE IF EXISTS `sys_logs`;

CREATE TABLE `sys_logs` (
  `id` varchar(50) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(50) DEFAULT NULL COMMENT '租户ID',
  `user_id` varchar(50) DEFAULT NULL COMMENT '操作人ID',
  `func_id` varchar(50) DEFAULT NULL COMMENT '功能ID',
  `func_name` varchar(50) DEFAULT NULL COMMENT '功能名称',
  `request_uri` varchar(200) DEFAULT NULL COMMENT '功能URL',
  `func_menu` varchar(50) DEFAULT NULL COMMENT '功能页面的按钮',
  `description` text COMMENT '描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_logs` */

/*Table structure for table `sys_param_warn` */

DROP TABLE IF EXISTS `sys_param_warn`;

CREATE TABLE `sys_param_warn` (
  `id` varchar(50) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `pname` varchar(60) NOT NULL COMMENT '预警参数名称',
  `pvalue` varchar(50) NOT NULL COMMENT '预警参数值',
  `modified_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `modified_user` varchar(50) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(50) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_param_warn` */

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` varchar(50) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `role_name` varchar(50) DEFAULT '' COMMENT '角色名称',
  `role_desc` varchar(50) DEFAULT '' COMMENT '角色描述',
  `deleted` varchar(2) NOT NULL DEFAULT 'N' COMMENT '是否被删除',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `deleted_user` varchar(50) DEFAULT '' COMMENT '删除人',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(50) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(50) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `tid_idx` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18400','102145','sss','test','N',NULL,'','2013-11-27 15:02:35','246000','2013-11-22 10:55:27','246000');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18401','0','role','test','Y','2014-01-03 16:13:32','500','2013-12-10 16:12:34','500','2013-11-25 11:17:42','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18500','0','test123','test','N',NULL,'','2014-01-03 16:14:29','500','2014-01-03 16:12:35','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18700','0','test','test','N',NULL,'','2014-01-09 17:55:20','500','2014-01-03 17:10:13','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18800','0','s',NULL,'N',NULL,'','2014-01-11 16:27:11','500','2014-01-11 16:27:11','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18801','0','a','aaaa','N',NULL,'','2014-01-14 00:13:33','500','2014-01-11 16:28:23','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18802','0','c',NULL,'Y','2014-01-11 16:39:05','500','2014-01-11 16:32:19','500','2014-01-11 16:32:19','500');
insert  into `sys_role`(`id`,`tenant_id`,`role_name`,`role_desc`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('18803','0','测试','ddsss','N',NULL,'','2014-01-19 23:31:42','500','2014-01-11 16:36:53','500');

/*Table structure for table `sys_role_func` */

DROP TABLE IF EXISTS `sys_role_func`;

CREATE TABLE `sys_role_func` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `role_id` varchar(50) DEFAULT '' COMMENT '角色ID',
  `func_id` varchar(50) DEFAULT '' COMMENT '功能ID',
  PRIMARY KEY (`id`),
  KEY `tid_idx` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2597 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_func` */

insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (29,'10210','1900','1001001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (30,'10210','1900','1001002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (31,'10210','1900','1001003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (32,'10210','1900','1001004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (33,'10210','1900','1001005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (34,'10210','1900','1001006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (35,'10210','1900','1001007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (36,'10210','1900','1001008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (37,'10210','1900','1001009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (38,'10210','1900','1001010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (39,'10210','1900','1001011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (40,'10210','1900','1001013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (41,'10210','1900','1002002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (42,'10210','1900','1002004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (43,'10210','1900','1002005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (44,'10210','1900','1002008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (45,'10210','1900','1003001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (46,'10210','1900','1003003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (47,'10210','1900','1003004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (48,'10210','1900','1004001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (49,'10210','1900','1005001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (50,'10210','1900','1005003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (51,'10210','1900','1005005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (52,'10210','1900','1005006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (53,'10210','1900','1005007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (54,'10210','1900','1005008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (55,'10210','1900','1005009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (56,'10210','1900','1005010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (57,'10210','1900','1006001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (58,'10210','1900','1006003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (59,'10210','1900','1006004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (60,'10210','1900','1006006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (61,'10210','1900','1006007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (62,'10210','1900','1006008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (63,'10210','1900','1007001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (64,'10210','1900','1007003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (65,'10210','1900','1007004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (66,'10210','1900','1007005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (67,'10210','1900','1007006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (68,'10210','1900','1007007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (69,'10210','1900','1008001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (70,'10210','1900','1008003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (71,'10210','1900','1008004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (72,'10210','1900','1008005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (73,'10210','1900','1008006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (74,'10210','1900','1008007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (75,'10210','1900','1008008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (76,'10210','1900','1008009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (77,'10210','1900','1009001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (78,'10210','1900','1009003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (79,'10210','1900','1009004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (80,'10210','1900','1009005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (81,'10210','1900','1009006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (82,'10210','1900','1010001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (83,'10210','1900','1011001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (84,'10210','1900','1011002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (249,'102115','18000','1001001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (250,'102115','18000','1001002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (251,'102115','18000','1001003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (252,'102115','18000','1001004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (253,'102115','18000','1001005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (254,'102115','18000','1001006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (255,'102115','18000','1001007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (256,'102115','18000','1001008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (257,'102115','18000','1001009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (258,'102115','18000','1001010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (259,'102115','18000','1001011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (260,'102115','18000','1001013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (261,'102115','18000','1002002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (262,'102115','18000','1002004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (263,'102115','18000','1002005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (264,'102115','18000','1002008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (265,'102115','18000','1003001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (266,'102115','18000','1003003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (267,'102115','18000','1003004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (268,'102100','18100','1001001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (269,'102100','18100','1001002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (270,'102100','18100','1001003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (271,'102100','18100','1001004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (272,'102100','18100','1001005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (273,'102100','18100','1001006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (274,'102100','18100','1001007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (275,'102100','18100','1001008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (276,'102100','18100','1001009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (277,'102100','18100','1001010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (278,'102100','18100','1001011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (279,'102100','18100','1001013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2163,'0','2001','2023001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2164,'0','1801','2011001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2165,'0','1801','2011003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2166,'0','1801','2011004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2167,'0','1801','2011005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2168,'0','1801','2011006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2169,'0','1801','2011007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2170,'0','1801','2011008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2171,'0','1801','2011009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2172,'0','1801','2011101');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2173,'0','1801','2011102');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2174,'0','1801','2011103');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2175,'0','1801','2011104');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2176,'0','1801','2011105');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2177,'0','1801','2011106');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2178,'0','1801','2011107');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2179,'0','1801','2011108');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2180,'0','1801','2011112');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2181,'0','1801','2012001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2182,'0','1801','2012003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2183,'0','1801','2012004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2184,'0','1801','2012008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2185,'0','1801','2012009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2186,'0','1801','2012010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2187,'0','1801','2013001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2188,'0','1801','2013004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2189,'0','1801','2013005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2190,'0','1801','2014001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2191,'0','1801','2014003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2192,'0','1801','2014004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2193,'0','1801','2014005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2194,'0','1801','2015001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2195,'0','1801','2015003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2196,'0','1801','2015004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2197,'0','1801','2016001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2198,'0','1801','2016003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2199,'0','1801','2016004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2200,'0','1801','2016005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2201,'0','1801','2016006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2202,'0','1801','2016007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2203,'0','1801','2017001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2204,'0','1801','2017003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2205,'0','1801','2017004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2206,'0','1801','2018001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2207,'0','1801','2018003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2208,'0','1801','2018004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2209,'0','1801','2019001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2210,'0','1801','2019003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2211,'0','1801','2019006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2212,'0','1801','2023001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2213,'0','1801','2012007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2214,'0','18200','2023001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2215,'0','18300','2001001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2216,'0','18300','2001002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2217,'0','18300','2001003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2218,'0','18300','2002001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2219,'0','18300','2002004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2220,'0','18300','2002005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2221,'0','18300','2002006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2222,'0','18300','2002007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2223,'0','18300','2002008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2224,'0','18300','2002009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2225,'0','18300','2002010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2226,'0','18300','2002011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2227,'0','18300','2002012');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2228,'0','18300','2002013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2229,'0','18300','2002015');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2230,'0','18300','2002016');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2231,'0','18300','2002017');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2232,'0','18300','2002018');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2233,'0','18300','2002020');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2234,'0','18300','2003007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2235,'0','18300','2004001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2236,'0','18300','2004003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2237,'0','18300','2004006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2238,'0','18300','2004008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2239,'0','18300','2004009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2240,'0','18300','2005001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2241,'0','18300','2005003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2242,'0','18300','2005004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2243,'0','18300','2005005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2244,'0','18300','2005006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2245,'0','18300','2005007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2246,'0','18300','2005008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2247,'0','18300','2005009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2248,'0','18300','2006001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2249,'0','18300','2006003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2250,'0','18300','2006004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2251,'0','18300','2007001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2252,'0','18300','2007003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2253,'0','18300','2007004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2254,'0','18300','2007005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2255,'0','18300','2008001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2256,'0','18300','2008007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2257,'0','18300','2008008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2258,'0','18300','2008011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2259,'0','18300','2008012');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2260,'0','18300','2008013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2261,'0','18300','2009001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2262,'0','18300','2009003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2263,'0','18300','2009004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2264,'0','18300','2009005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2265,'0','18300','2010001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2266,'0','18300','2010003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2267,'0','18300','2010004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2268,'0','18300','2010007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2269,'0','18300','2010008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2270,'0','18300','2010009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2271,'0','18300','2003001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2272,'0','18300','2008009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2273,'0','18300','2008010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2276,'102145','18400','1001001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2277,'102145','18400','1001002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2278,'102145','18400','1001003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2279,'102145','18400','1001004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2280,'102145','18400','1001005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2281,'102145','18400','1001006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2282,'102145','18400','1001007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2283,'102145','18400','1001008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2284,'102145','18400','1001009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2285,'102145','18400','1001010');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2286,'102145','18400','1001011');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2287,'102145','18400','1001013');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2288,'102145','18400','1005001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2289,'102145','18400','1005008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2290,'102145','18400','1006001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2291,'102145','18400','1006003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2292,'102145','18400','1006004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2293,'102145','18400','1006006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2294,'102145','18400','1006007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2295,'102145','18400','1006008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2430,'0','18500','2017001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2431,'0','18500','2017003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2432,'0','18500','2017004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2433,'0','18500','2018001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2434,'0','18500','2018003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2435,'0','18500','2018004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2436,'0','18500','2019001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2437,'0','18500','2019003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2438,'0','18500','2019006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2439,'0','18500','2020001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2440,'0','18500','2020003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2441,'0','18500','2020004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2442,'0','18500','2020005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2443,'0','18500','2020006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2444,'0','18500','2020007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2445,'0','18500','2020008');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2446,'0','18500','2020009');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2447,'0','18500','2021001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2448,'0','18500','2021003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2449,'0','18500','2021004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2450,'0','18500','2021005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2451,'0','18500','2021006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2452,'0','18500','2022001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2453,'0','18500','2023001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2454,'0','18500','2023002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2455,'0','18500','2024001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2456,'0','18500','2024002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2457,'0','18500','2024003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2458,'0','18500','2024004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2459,'0','18500','2029001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2460,'0','18500','2030001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2461,'0','18500','2031001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2583,'0','18700','2034000');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2584,'0','18700','2033001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2585,'0','18700','2033002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2586,'0','18700','2033003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2587,'0','18700','2033004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2588,'0','18700','2033005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2589,'0','18700','2033006');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2590,'0','18700','2033007');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2591,'0','18700','2032001');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2592,'0','18700','2032002');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2593,'0','18700','2032003');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2594,'0','18700','2032004');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2595,'0','18700','2032005');
insert  into `sys_role_func`(`id`,`tenant_id`,`role_id`,`func_id`) values (2596,'0','18700','2034001');

/*Table structure for table `sys_tenant` */

DROP TABLE IF EXISTS `sys_tenant`;

CREATE TABLE `sys_tenant` (
  `id` varchar(50) NOT NULL COMMENT '租户ID',
  `name` varchar(100) DEFAULT '' COMMENT '租户名称',
  `role` int(2) NOT NULL DEFAULT '0' COMMENT '1:租户角色，2:运营商角色',
  `contact` varchar(100) DEFAULT '' COMMENT '租户联系人',
  `mobile` varchar(20) DEFAULT '' COMMENT '租户联系电话',
  `email` varchar(100) DEFAULT '' COMMENT '租户联系邮件',
  `address` varchar(100) DEFAULT '' COMMENT '租户联系地址',
  `os_tenant_id` varchar(64) DEFAULT '' COMMENT '对应的Openstack租户ID',
  `postcode` varchar(10) DEFAULT '' COMMENT '租户联系地址邮编',
  `status` varchar(2) NOT NULL DEFAULT 'N' COMMENT '用户状态:激活,未激活',
  `status_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户状态变更时间',
  `status_user` varchar(50) DEFAULT '' COMMENT '状态变更人',
  `deleted` varchar(2) NOT NULL DEFAULT 'N' COMMENT '是否被删除',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `deleted_user` varchar(50) DEFAULT '' COMMENT '删除人',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(50) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(50) DEFAULT '' COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_tenant` */

insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('0','联通研究院',2,'白力','18698682909','li.bai@i-soft.com.cn','中国北京市海淀区农大南路1号，硅谷亮城4号楼5层','af7582b26b6544c6b39f9c725ab5d776','100084','Y','2013-11-19 15:23:11',NULL,'N',NULL,'500','2013-11-14 15:33:53','500','0000-00-00 00:00:00','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102135','wl001',1,'wl','18686515387','wlxyz123@163.com','puhua','','100084','N','2013-11-18 13:22:48','500','Y','2013-11-18 13:22:48','500','2013-11-18 13:13:16','500','2013-11-18 13:13:16','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102140','le_001',1,'le','18798766541','wlxyz123@163.com','puhua','','100084','N','2013-11-18 13:33:06','500','Y','2013-11-18 13:33:06','500','2013-11-18 13:28:52','500','2013-11-18 13:28:52','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102145','puhua',1,'puhua','18000000000','junmao.tang@i-soft.com.cn','北京市海淀区农大南路1号4号楼5层','4e31ebca12ca459cafdd2f1399abd125','100090','Y','2013-12-19 11:08:04','500','N',NULL,'','2013-11-18 13:38:04','500','2013-11-18 13:38:04','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102150','PaaS-Tenant-007000',1,'联通研究院',NULL,'admin@wocloud.com.cn','联通研究院','',NULL,'N','2013-11-19 14:58:11',NULL,'N',NULL,'','2013-11-19 14:58:11',NULL,'2013-11-19 14:58:11',NULL);
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102155','wl_001',1,'wl','18686515387','wlxyz123@163.com','puhua','03c9cc2576a44f1896aaa3e392141796','100084','R','2013-12-02 11:48:29','500','Y','2013-12-02 11:48:29','500','2013-12-02 11:23:11','500','2013-12-02 11:23:11','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102156','wl_002',1,'wl','18686515387','le.wei@i-soft.com.cn','puhua','270b3c5cd3ed47e1bd578a18579effef','100084','Y','2013-12-02 11:51:47','500','N',NULL,'','2013-12-02 11:47:57','500','2013-12-02 11:47:57','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102160','lqh',1,'lqh','18612200693','qihe.liu@i-soft.com.cn','puhua','6297c7d12c5e44209fabbf7e8a6588be','100084','N','2013-12-18 17:04:12','500','N',NULL,'','2013-12-06 14:53:27','500','2013-12-06 14:53:27','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102161','tanie',1,'tanie','18000000000','junmao.tang@i-soft.com.cn','puhua','0107696929304f7c933c4894c3732006',NULL,'D','2013-12-19 16:23:06','500','N',NULL,'','2013-12-18 12:09:12','500','2013-12-18 12:09:12','500');
insert  into `sys_tenant`(`id`,`name`,`role`,`contact`,`mobile`,`email`,`address`,`os_tenant_id`,`postcode`,`status`,`status_at`,`status_user`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`) values ('102165','liuqihe',1,'liuqihe','18612200693','qihe.liu@i-soft.com.cn','puhua','','100086','N','2013-12-18 17:02:57','500','N',NULL,'','2013-12-18 16:57:51','500','2013-12-18 16:57:51','500');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` varchar(50) NOT NULL COMMENT '用户ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `admin` char(1) NOT NULL DEFAULT 'N' COMMENT '管理员',
  `name` varchar(100) DEFAULT '' COMMENT '用户名称',
  `pswd` varchar(100) DEFAULT '' COMMENT '用户密码',
  `mobile` varchar(20) DEFAULT '' COMMENT '用户手机号',
  `email` varchar(100) DEFAULT '' COMMENT '用户邮件',
  `last_login_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '最近登录时间',
  `status` varchar(2) NOT NULL DEFAULT 'N' COMMENT '用户状态:激活,未激活',
  `status_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '用户状态变更时间',
  `deleted` varchar(2) NOT NULL DEFAULT 'N' COMMENT '是否被删除',
  `deleted_at` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `deleted_user` varchar(50) DEFAULT '' COMMENT '删除人',
  `modified_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `modified_user` varchar(50) DEFAULT '' COMMENT '修改人',
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `created_user` varchar(50) DEFAULT '' COMMENT '创建人',
  `portal_tenant_id` varchar(100) DEFAULT NULL,
  `last_login_ip` char(15) DEFAULT NULL COMMENT '最近登录ip',
  `current_login_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '本次登录时间',
  `current_login_ip` char(15) DEFAULT NULL COMMENT '本次登录ip',
  `login_times` smallint(8) NOT NULL DEFAULT '0' COMMENT '总登录次数',
  `islogin` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:离线, 1:在线',
  `loginname` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tid_idx` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('245900','102135','Y','root','ZFn0vjFpTk0+TCqfFhl8EQ==','','wlxyz123@163.com','0000-00-00 00:00:00','Y','0000-00-00 00:00:00','N',NULL,'','2013-11-18 13:15:29','500','2013-11-18 13:15:29','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246000','102145','Y','root','OlCNRCbp0ec=','','junmao.tang@i-soft.com.cn','2013-12-30 09:15:42','Y','0000-00-00 00:00:00','N',NULL,'','2013-11-18 13:55:31','500','2013-11-18 13:55:31','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246100','102145','N','demo','kQyxBGkTVV8+TCqfFhl8EQ==','18000000000','yue.zhao@i-soft.com.cn','0000-00-00 00:00:00','Y','2013-11-25 16:32:44','N',NULL,'','2013-11-25 16:32:44','246000','2013-11-25 16:32:39','246000',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246101','102160','Y','root','OlCNRCbp0ec=','','qihe.liu@i-soft.com.cn','2013-12-18 17:00:33','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-06 14:53:33','500','2013-12-06 14:53:33','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246102','102161','N','lqh','OlCNRCbp0ec=','13456789078','qihe.liu@i-soft.com.cn','2013-12-20 11:27:41','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-20 18:00:29','246102','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246103','102161','Y','root','OlCNRCbp0ec=','','junmao.tang@i-soft.com.cn','2013-12-24 17:03:11','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-18 12:09:51','500','2013-12-18 12:09:51','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('246212','102145','N','testsjh','uZICusd/djo+TCqfFhl8EQ==','18600000001','jiahao.shi@i-soft.com.cn','0000-00-00 00:00:00','F','2013-11-27 13:42:31','Y','2013-11-27 13:42:37','246000','2013-11-27 13:42:31','246000','2013-11-27 13:37:16','246000',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('247300','102155','Y','root','sFaELHyyRys+TCqfFhl8EQ==','','wlxyz123@163.com','0000-00-00 00:00:00','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-02 11:31:48','500','2013-12-02 11:31:48','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('247301','102156','Y','root','OlCNRCbp0ec=','','le.wei@i-soft.com.cn','2014-01-10 14:21:54','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-02 11:51:44','500','2013-12-02 11:51:44','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('250500','102165','Y','root','VuaIyqf6YDo+TCqfFhl8EQ==','','qihe.liu@i-soft.com.cn','0000-00-00 00:00:00','Y','0000-00-00 00:00:00','N',NULL,'','2013-12-18 16:57:55','500','2013-12-18 16:57:55','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('250802','102161','N','le_test_001','OlCNRCbp0ec=','18686515387','le.wei@i-soft.com.cn','2013-12-23 10:55:06','F','2013-12-23 13:53:17','Y','2013-12-23 17:30:41','246103','2013-12-23 13:53:17','246103','2013-12-19 10:13:29','246103',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251200','102161','N','w_test','ML3GRsjI0n0+TCqfFhl8EQ==','13425678976','xiaohua.wu@i-soft.com.cn','0000-00-00 00:00:00','Y','2013-12-20 16:57:12','Y','2013-12-24 11:46:02','246103','2013-12-20 16:57:12','246103','2013-12-20 16:57:04','246103',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251400','102161','N','liuqihe','','13456789078',NULL,'0000-00-00 00:00:00','Y','2013-12-23 11:18:51','Y','2013-12-24 11:46:02','246103','2013-12-23 11:18:51','','2013-12-23 11:18:51','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'qihe.liu433sdfasdf');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251401','102161','N','liuqh2','','13333333333','nomal@qq.com','0000-00-00 00:00:00','Y','2013-12-23 11:19:35','Y','2013-12-24 11:46:02','246103','2013-12-23 11:19:35','246103','2013-12-23 11:18:51','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'qh2.liu');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251402','102161','N','yangjames','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-23 11:18:51','Y','2013-12-24 11:46:02','246103','2013-12-23 11:18:51','','2013-12-23 11:18:51','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'james');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251500','102161','N','le_test_001','OlCNRCbp0ec=','18686515387','wlxyz123@163.com','2013-12-24 09:56:14','F','2013-12-24 10:21:33','Y','2013-12-24 11:46:02','246103','2013-12-24 10:21:33','246103','2013-12-24 09:53:31','246103',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251600','102161','N','testwy1','','15689562556','werertetert@qq.com','0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'testwy1');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251601','102161','N','testwy2','',NULL,'testwy2@qq.com','0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'testwy2');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251602','102161','N','gzq1','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'gzq1');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251603','102161','N','gzq2','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'gzq2');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251604','102161','N','lmm1','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'lmm1');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251605','102161','N','lmm2','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'lmm2');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251606','102161','N','zpp','',NULL,NULL,'0000-00-00 00:00:00','Y','2013-12-24 11:46:02','N',NULL,'','2013-12-24 11:46:02','','2013-12-24 11:46:02','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,'zpp');
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251700','0','N','user1','YrNCamHHnHU+TCqfFhl8EQ==','15811039090','1@126.com','0000-00-00 00:00:00','F','2014-01-06 16:04:31','N',NULL,'','2014-01-06 16:04:31','500','2013-12-27 16:37:36','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251701','102156','N','user1','YU1YRPddikg+TCqfFhl8EQ==','13611222222','yue.zhao@i-soft.com.cn','2014-01-09 16:42:39','Y','2014-01-09 16:08:31','N',NULL,'','2014-01-09 16:08:31','247301','2014-01-09 16:08:25','247301',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251800','0','N',NULL,'',NULL,NULL,'0000-00-00 00:00:00','N','2014-01-03 15:28:26','Y','2014-01-03 16:18:25','500','2014-01-03 15:28:26','600','2014-01-03 15:28:26','600',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251801','0','N',NULL,'',NULL,NULL,'0000-00-00 00:00:00','N','2014-01-03 15:29:37','Y','2014-01-03 16:18:28','500','2014-01-03 15:29:37','600','2014-01-03 15:29:37','600',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('251900','0','N',NULL,'Qf/ZG0a6+6A+TCqfFhl8EQ==',NULL,NULL,'0000-00-00 00:00:00','F','2014-01-03 16:24:47','Y','2014-01-03 16:24:51','500','2014-01-03 16:24:47','500','2014-01-03 15:34:37','600',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('252000','0','N','www','OlCNRCbp0ec=','15898565689','xiaohua.wu@i-soft.com.cn','2014-01-09 17:55:29','Y','2014-01-03 16:19:13','N',NULL,'','2014-01-03 16:21:58','252000','2014-01-03 16:19:08','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('252300','0','N','sss','','18698682908','aa@aa.com','0000-00-00 00:00:00','N','2014-01-19 22:11:47','N',NULL,'','2014-01-19 22:11:47','500','2014-01-19 22:11:47','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('500','0','Y','root','OlCNRCbp0ec=','18000000000','qihe.liu@i-soft.com.cn','2014-01-19 23:31:32','Y','2013-08-14 12:54:26','N',NULL,'','2013-12-20 18:01:21','500','2013-08-14 12:54:21','500',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('508','0','N','jiahao.shi','OlCNRCbp0ec=','','','2013-12-28 16:54:56','Y','0000-00-00 00:00:00','N',NULL,'','0000-00-00 00:00:00','','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('509','0','N','admin','OlCNRCbp0ec=','','','2013-12-28 17:01:43','Y','0000-00-00 00:00:00','N',NULL,'','0000-00-00 00:00:00','','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('511','0','N','qwer','OlCNRCbp0ec=','','','2013-12-31 14:43:58','Y','0000-00-00 00:00:00','N',NULL,'','0000-00-00 00:00:00','','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('512','0','N','123','47OS9cmOGLE+TCqfFhl8EQ==','18626758556','240780848@qq.com','0000-00-00 00:00:00','F','2014-01-03 16:30:22','Y','2014-01-03 16:30:24','500','2014-01-03 16:30:22','500','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);
insert  into `sys_user`(`id`,`tenant_id`,`admin`,`name`,`pswd`,`mobile`,`email`,`last_login_at`,`status`,`status_at`,`deleted`,`deleted_at`,`deleted_user`,`modified_at`,`modified_user`,`created_at`,`created_user`,`portal_tenant_id`,`last_login_ip`,`current_login_at`,`current_login_ip`,`login_times`,`islogin`,`loginname`) values ('513','0','N','sjhsjh','sGOKAaZZ/Tw+TCqfFhl8EQ==','','','0000-00-00 00:00:00','F','2014-01-11 22:59:43','Y','2014-01-11 22:59:51','500','2014-01-11 22:59:43','500','0000-00-00 00:00:00','',NULL,NULL,'0000-00-00 00:00:00',NULL,0,0,NULL);

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` varchar(50) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `role_id` varchar(50) NOT NULL COMMENT '角色ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `tid_idx` (`tenant_id`),
  KEY `rid_idx` (`role_id`),
  KEY `uid_idx` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`id`,`tenant_id`,`role_id`,`user_id`) values ('9300','102145','18400','246100');
insert  into `sys_user_role`(`id`,`tenant_id`,`role_id`,`user_id`) values ('9400','102145','18400','246212');
insert  into `sys_user_role`(`id`,`tenant_id`,`role_id`,`user_id`) values ('9500','0','18500','511');
insert  into `sys_user_role`(`id`,`tenant_id`,`role_id`,`user_id`) values ('9501','0','18500','513');
insert  into `sys_user_role`(`id`,`tenant_id`,`role_id`,`user_id`) values ('9701','0','18700','252000');

/* Function  structure for function  `TOREGEXP` */

/*!50003 DROP FUNCTION IF EXISTS `TOREGEXP` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` FUNCTION `TOREGEXP`(W VARCHAR(1024)) RETURNS varchar(1024) CHARSET utf8
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
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
