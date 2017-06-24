package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ScriptsExecAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Scripts"));
		page("file", "scripts_exec.action");
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hostid",	array(T_RDA_INT, O_OPT, P_ACT, DB_ID, null),
			"scriptid",	array(T_RDA_INT, O_OPT, null, DB_ID, null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {		
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		final String scriptId = get_request("scriptid");
		final String hostId = get_request("hostid");

		Map params = new HashMap();
		params.put("scriptid", scriptId);
		Map data = map(
			"message", "",
			"info", DBfetch(DBselect(executor,"SELECT s.name FROM scripts s WHERE s.scriptid=#{scriptid}",params))
		);

		CArray<Map> result = Call(new Wrapper<CArray<Map>>() {
			@Override
			protected CArray<Map> doCall() throws Throwable {
				return API.Script(getIdentityBean(), executor).execute(map(
						"hostid", hostId,
						"scriptid", scriptId
					));
			}
		},null);

		boolean isErrorExist = false;

		if (empty(result)) {
			isErrorExist = true;
		} else if ("failed".equals(Nest.value(result,"response").asString())) {
			error(Nest.value(result,"value").asString());
			isErrorExist = true;
		} else {
			Nest.value(data,"message").$(Nest.value(result,"value").$());
		}

		if (isErrorExist) {
			show_error_message(
				_("Cannot connect to the trapper port of iradar server daemon, but it should be available to run the script.")
			);
		}

		// render view
		CView scriptView = new CView("general.script.execute", data);
		scriptView.render(getIdentityBean(), executor);
		scriptView.show();
	}

}
