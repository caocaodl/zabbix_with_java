package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.platform.topo.CabTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.LineDAO;
import com.isoft.biz.daoimpl.platform.topo.NodeDAO;
import com.isoft.biz.vo.platform.topo.TCabNode;
import com.isoft.framework.persistlayer.SQLExecutor;

/**
 * 机房拓扑
 * @author guzhaohui
 *
 */
public class CabTopoListAction extends TopoAction {
	
	protected String getTopoType() {
		return "cabtopo";
	}
	
	protected String getPageFile() {
		return "TopoCabList.action";
	}
	
	protected String getPageTitle() {
		return "机房拓扑";
	}

	@Override
	protected void delNodeByTopoId(SQLExecutor executor, List<Long> topoIds) {
		CabTopoDAO cabDao = new CabTopoDAO(executor);
		Map<String,Object> tempMap = new HashMap<String,Object>();
		for(Long id : topoIds){
			tempMap.put("topoId", id);
			cabDao.doCabinetNodeDelByTopoId(tempMap);
		}
	}
	
}
