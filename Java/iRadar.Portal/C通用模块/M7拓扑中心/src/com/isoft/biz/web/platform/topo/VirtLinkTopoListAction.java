package com.isoft.biz.web.platform.topo;
/**
 * 云主机从属拓扑
 * @author guzhaohui
 *
 */
public class VirtLinkTopoListAction extends TopoAction {
	
	protected String getTopoType() {
		return "virtlinktopo";
	}
	
	protected String getPageFile() {
		return "TopoVirtLinkList.action";
	}
	
	protected String getPageTitle() {
		return "虚拟链路拓扑";
	}
}
