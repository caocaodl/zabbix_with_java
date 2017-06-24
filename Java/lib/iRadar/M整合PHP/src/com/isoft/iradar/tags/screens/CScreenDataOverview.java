package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.ItemsUtil.getItemsDataOverview;
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
public class CScreenDataOverview extends CScreenBase {

	public CScreenDataOverview(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenDataOverview(IIdentityBean idBean, SQLExecutor executor, Map options) {
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
		CArray<Map> dbHostGroups = DBselect(executor, "SELECT DISTINCT hg.hostid FROM hosts_groups hg WHERE hg.groupid=#{resourceid}",params);
		for(Map dbHostGroup : dbHostGroups) {
			Nest.value(hostids,dbHostGroup.get("hostid")).$(dbHostGroup.get("hostid"));
		}

		return getOutput(getItemsDataOverview(this.idBean, this.executor, hostids.valuesAsLong(), Nest.value(screenitem,"application").asString(), Nest.value(screenitem,"style").asInteger()));
	}

}
