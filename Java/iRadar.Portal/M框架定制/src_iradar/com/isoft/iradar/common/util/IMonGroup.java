package com.isoft.iradar.common.util;

import com.isoft.types.CArray;

public enum IMonGroup{
	DISCOVERED_HOSTS		(5		, 	"Discovered hosts", true), 
	TEMPLATES				(19		, 	"Templates", 		true), 
	MON_SERVER_WINDOWS		(102	, 	"服务器Windows", 	false),
	MON_SERVER_LINUX		(101	, 	"服务器Linux", 	false),
	MON_VM					(201	, 	"云主机", 		false),
	MON_NET_CISCO			(301	, 	"网络设备Cisco", 	false),
	MON_COMMON_NET			(302	, 	"网络设备通用", 	false), 
	MON_NET_HUAWEI_SWITCH	(303	, 	"网络设备华为", 	false),
	MON_NET_ZHONGXING_SWITCH(304	, 	"网络设备中兴", 	false),
//	MON_STORAGE				(401	, 	"存储设备", 		false), 
	MON_DB_MYSQL			(501	, 	"数据库MySQL", 	false), 
	MON_DB_ORACLE			(502	, 	"数据库Oracle", 	false),
	MON_DB_DM				(503	, 	"数据库DM", 		false),
	MON_DB_DB2				(504	, 	"数据库DB2", 		false),
	MON_DB_SQLSERVER		(505	, 	"数据库SqlServer", false),
	MON_DB_MONGODB  		(506	, 	"数据库MongoDB", 	false),
	MON_MIDDLE_TOMCAT		(601	, 	"中间件Tomcat", 	false),
	MON_MIDDLE_IIS			(602	, 	"中间件IIS", 		false),
	MON_MIDDLE_WEBLOGIC		(603	, 	"中间件WebLogic", false), 
	MON_MIDDLE_WEBSPHERE	(604	, 	"中间件WebSphere",false), 
	MON_WEB					(701	, 	"Web服务", 		false), 
	MON_CLOUD_CONTROLER		(801	, 	"云控制服务", 		false),
	MON_CLOUD_COMPUTER		(802	, 	"云计算服务", 		false),
	MON_CLOUD_CEPH			(803	, 	"云存储服务", 		false),
	MON_CLOUD_NETWORK		(804	, 	"云网络服务", 		false),
	MON_CLOUD_WEB			(805	, 	"云门户服务", 		false),
	MON_CLUSTER				(901	, 	"集群", 			false), 
	MON_DESKTOPC			(1001	, 	"桌面云",			false)
	;
	
	private Long id;
	private String desc;
	private boolean isSystem;
	private IMonGroup(int id, String desc, boolean isSystem) {
		this.id = Long.valueOf(id);
		this.desc = desc;
		this.isSystem = isSystem;
	}
	public Long id() {return this.id;}
	public String desc() {return this.desc;}
	public boolean isSystem() {return this.isSystem;}
	
	private static CArray<IMonGroup> SYSTEM_GROUPS = null;
	public static CArray<IMonGroup> systemGroups(){
		if(SYSTEM_GROUPS == null) {
			SYSTEM_GROUPS = CArray.map();
			for(IMonGroup group: IMonGroup.values()) {
				if(group.isSystem) {
					SYSTEM_GROUPS.put(group.id, group);
				}
			}
		}
		return SYSTEM_GROUPS;
	}
	
	private static CArray<IMonGroup> SHOWABLE_GROUPS = null;
	public static CArray<IMonGroup> showableGroups(){
		if(SHOWABLE_GROUPS == null) {
			SHOWABLE_GROUPS = CArray.map();
			for(IMonGroup group: IMonGroup.values()) {
				if(!group.isSystem) {
					SHOWABLE_GROUPS.put(group.id, group);
				}
			}
		}
		return SHOWABLE_GROUPS;
	}
}
