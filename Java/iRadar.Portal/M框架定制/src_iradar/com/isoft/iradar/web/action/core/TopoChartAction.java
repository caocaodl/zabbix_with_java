package com.isoft.iradar.web.action.core;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.Mapper.Nest;


public class TopoChartAction extends RadarBaseAction {
	@Override
	protected void doInitPage() {
		_page("file", "topochart.action");
		_page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		
	}
	
	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		String topoId = Nest.value(_REQUEST,"topoId").asString();
		try {
			byte[] bs = TopoUtil.drawNetTopoBreviary(topoId, executor);
			this.getResponse().getOutputStream().write(bs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
