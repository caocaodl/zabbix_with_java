package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.platform.topo.LineDAO;
import com.isoft.biz.daoimpl.platform.topo.NodeDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

/**
 * 网络链路拓扑
 * @author guzhaohui
 *
 */
public class NetTopoListAction extends TopoAction {
	
	protected String getTopoType() {
		return "nettopo";
	}
	
	protected String getPageFile() {
		return "TopoNetList.action";
	}
	
	protected String getPageTitle() {
		return "物理链路拓扑";
	}
	
	@Override
    protected void delNodeByTopoId(final SQLExecutor executor,List<Long> topoIds){
		NodeDAO nodeDao = new NodeDAO(executor);
		LineDAO lineDao = new LineDAO(executor);
		Map<String,Object> tempMap = new HashMap<String,Object>();
		for(Long id : topoIds){
			tempMap.put("topoId", id);
			nodeDao.doNetTopoDel(tempMap);
			lineDao.doLineDelByTopoId(tempMap);
		}
	}
}
