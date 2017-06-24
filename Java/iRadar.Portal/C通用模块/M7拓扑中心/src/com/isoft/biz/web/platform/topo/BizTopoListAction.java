package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.platform.topo.BizLineDAO;
import com.isoft.biz.daoimpl.platform.topo.BizTopoDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

/**
 * 业务拓扑
 * @author guzhaohui
 *
 */
public class BizTopoListAction extends TopoAction {
	
	protected String getTopoType() {
		return "biztopo";
	}
	
	protected String getPageFile() {
		return "topo_client_business.action";
	}
	
	protected String getPageTitle() {
		return "业务拓扑";
	}
	
	@Override
	protected void delNodeByTopoId(SQLExecutor executor, List<Long> topoIds) {
		BizTopoDAO bizDao = new BizTopoDAO(executor);
		BizLineDAO lineDao = new BizLineDAO(executor);
		Map<String,Object> tempMap = new HashMap<String,Object>();
		for(Long id : topoIds){
			tempMap.put("topoId", id);
			bizDao.doTBizNodeDelByTopoId(tempMap);
			lineDao.doLineDelByTopoId(tempMap);
		}
	}
}
