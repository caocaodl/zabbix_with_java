package com.isoft.biz.web.platform.topo;
/**
 * 云主机从属拓扑
 * @author guzhaohui
 *
 */
public class CloudTopoListAction extends TopoAction {
	
	protected String getTopoType() {
		return "hosttopo";
	}
	
	protected String getPageFile() {
		return "TopoCloudList.action";
	}
	
	protected String getPageTitle() {
		return "云主机从属拓扑";
	}
}
