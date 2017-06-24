package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.TriggersUtil.getTriggersOverview;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenTriggersOverview extends CScreenBase {

	public CScreenTriggersOverview(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenTriggersOverview(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		CArray hostids = array();

		Map params = new HashMap();
		params.put("resourceid", Nest.value(screenitem,"resourceid").$());
		CArray<Map> dbHostGroups = DBselect(executor,
			"SELECT DISTINCT hg.hostid"+
			" FROM hosts_groups hg"+
			" WHERE hg.groupid=#{resourceid}",
			params
		);
		for(Map dbHostGroup : dbHostGroups) {
			Nest.value(hostids,dbHostGroup.get("hostid")).$(dbHostGroup.get("hostid"));
		}

		return getOutput(getTriggersOverview(idBean, executor,hostids, Nest.value(screenitem,"application").asString(),
				pageFile, Nest.value(screenitem,"style").asInteger(), Nest.as(screenid).asString()
		));
	}

}
