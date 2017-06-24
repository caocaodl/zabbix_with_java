/*
SQLyog Enterprise - MySQL GUI v7.02 
MySQL - 5.1.73 : Database - isoft_app
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

insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0001','-1','监控中心','monitoring','','Y','N','tree','simple','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010001','0001','监控面板','dashboard','/platform/iradar/dashboard.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010002','0001','发现中心','discovery','/platform/iradar/discovery.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010003','0001','监控矩阵','overview','/platform/iradar/overview.action','Y','Y','','','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010004','0001','采集中心','latestdata','/platform/iradar/latest.action','Y','Y','','','','','1',1000004,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010005','0001','预警中心','triggers','/platform/iradar/tr_status.action','Y','Y','','','','','1',1000005,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010006','0001','事件中心','events','/platform/iradar/events.action','Y','Y','','','','','1',1000006,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010007','0001','图形报表','graphs','/platform/iradar/charts.action','Y','Y','','','','','1',1000007,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010008','0001','网站监控','web','/platform/iradar/httpmon.action','Y','Y','','','','','1',1000008,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010009','0001','服务质量','itsla','/platform/iradar/srv_status.action','Y','Y','','','','','1',1000009,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00010010','0001','监控大屏','screens','/platform/iradar/screens.action','Y','Y','','','','','1',1000010,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0002','-1','拓扑中心','maps','','Y','N','tree','simple','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00020001','0002','网络拓扑','topos','/platform/iradar/maps.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0003','-1','资产中心','inventory','','Y','N','tree','simple','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00030001','0003','资产概览','inventoryoverview','/platform/iradar/hostinventoriesoverview.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00030002','0003','设备管理','hostinventories','/platform/iradar/hostinventories.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0004','-1','运维分析','','','Y','N','tree','simple','','','1',1000004,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00040001','0004','服务器状态','report1','/platform/iradar/report1.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00040002','0004','可用性分析','report2','/platform/iradar/report2.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00040003','0004','TOPx分析','report5','/platform/iradar/report5.action','Y','Y','','','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00040004','0004','柱状报表','report6','/platform/iradar/report6.action','Y','Y','','','','','1',1000004,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0005','-1','系统管理','','','Y','N','tree','simple','','','1',1000005,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050001','0005','系统参数','setting','/platform/iradar/adm.gui.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050002','0005','集群管理','proxies','/platform/iradar/proxies.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050003','0005','认证管理','authentication','/platform/iradar/authentication.action','Y','Y','','','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050004','0005','用户管理','usergrps','/platform/iradar/usergrps.action','Y','Y','','','','','1',1000004,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050005','0005','通知机制','mediatypes','/platform/iradar/media_types.action','Y','Y','','','','','1',1000005,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00050006','0005','工具脚本','scripts','/platform/iradar/scripts.action','Y','Y','','','','','1',1000006,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0006','-1','平台审计','','','Y','N','tree','simple','','','1',1000006,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00060001','0006','日志审计','auditlogs','/platform/iradar/auditlogs.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00060002','0006','消息队列','queue','/platform/iradar/queue.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00060003','0006','通知历史','report4','/platform/iradar/report4.action','Y','Y','','','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('0007','-1','监控配置','','','Y','N','tree','simple','','','1',1000007,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070001','0007','设备分组','hostgroups','/platform/iradar/hostgroups.action','Y','Y','','','','','1',1000001,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070002','0007','监控模型','templates','/platform/iradar/templates.action','Y','Y','','','','','1',1000002,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070003','0007','设备管理','hosts','/platform/iradar/hosts.action','Y','Y','','','','','1',1000003,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070004','0007','维护模式','maintenance','/platform/iradar/maintenance.action','Y','Y','','','','','1',1000004,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070005','0007','网站监控','httpconf','/platform/iradar/httpconf.action','Y','Y','','','','','1',1000005,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070006','0007','分析策略','actionconf','/platform/iradar/actionconf.action','Y','Y','','','','','1',1000006,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070007','0007','监控大屏管理','screenconf','/platform/iradar/screenconf.action','Y','Y','','','','','1',1000007,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070008','0007','幻灯片管理','slideconf','/platform/iradar/slideconf.action','Y','Y','','','','','1',1000008,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070009','0007','拓扑管理','sysmaps','/platform/iradar/sysmaps.action','Y','Y','','','','','1',1000009,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070010','0007','发现策略','discoveryconf','/platform/iradar/discoveryconf.action','Y','Y','','','','','1',1000010,2,'');
insert  into `sys_func`(`id`,`pid`,`func_name`,`func_id`,`func_url`,`entrance`,`leaf`,`render_type`,`render_style`,`render_url`,`icon`,`status`,`seq_no`,`role`,`note`) values ('00070011','0007','运维服务','services','/platform/iradar/services.action','Y','Y','','','','','1',1000011,2,'');

/*Table structure for table `sys_func_bt` */

DROP TABLE IF EXISTS `sys_func_bt`;

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

/*Data for the table `sys_func_bt` */

/*Table structure for table `sys_func_bt_uri` */

DROP TABLE IF EXISTS `sys_func_bt_uri`;

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

/*Data for the table `sys_func_bt_uri` */

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

/*Table structure for table `sys_role_func` */

DROP TABLE IF EXISTS `sys_role_func`;

CREATE TABLE `sys_role_func` (
  `id` bigint(50) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tenant_id` varchar(50) NOT NULL COMMENT '租户ID',
  `role_id` varchar(50) DEFAULT '' COMMENT '角色ID',
  `func_id` varchar(50) DEFAULT '' COMMENT '功能ID',
  PRIMARY KEY (`id`),
  KEY `tid_idx` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_func` */

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
