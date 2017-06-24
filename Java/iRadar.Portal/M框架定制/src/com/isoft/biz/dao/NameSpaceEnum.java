package com.isoft.biz.dao;

import org.apache.commons.lang.NotImplementedException;

public enum NameSpaceEnum {
	SYS_LOG("系统日志流水号"),
	SYS_USER("系统用户流水号"),
	SYS_USER_ROLE("系统用户角色流水号"),
	SYS_USER_ROLE_FUNC("系统用户角色功能流水号"),
	SYS_TENANT("租户流水号"),
	I_INSPECTION_REPORT("巡检报告流水号"),
	I_INSPECTION_HOST("巡检监控设备流水号"),
	OPERATION_SYSTEM("操作系统类型流水号"),
	OPERATION_DEPT("部门流水号"),
	OSKEY("操作系统键值流水号"),
	
	T_NODE("拓扑节点流水号"),
	T_LINE("网络拓扑线路流水号"),
	T_LINK("拓扑线路流水号"),
	T_SUBNET("拓扑子网流水号"),
	T_TOPO_LOCATION("拓扑节点坐标流水号"),
	T_CABINET_NODE("机柜拓扑节点流水号"),
	T_TOPO("拓扑列表流水号"),
	T_PIC("拓扑图片流水号"),
	T_TOPO_PIC("背景图片流水号"),
	T_BIZ_LINE("业务拓扑线路流水号"),
	;
  
	static {
		if (NameSpaceEnum.values().length == 0) {
			System.out.println("根据开发框架约定，请自定义实现 com.isoft.biz.dao.NameSpaceEnum");
			throw new NotImplementedException(
					"根据开发框架约定，请自定义实现 com.isoft.biz.dao.NameSpaceEnum");
		}
	}

	private NameSpaceEnum(String note) {
		this.note = note;
	}

	private String note;

	public String note() {
		return this.note != null ? this.note : "";
	}
}
