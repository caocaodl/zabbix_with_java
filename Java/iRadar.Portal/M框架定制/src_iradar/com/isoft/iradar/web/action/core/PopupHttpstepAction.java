package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.KEY_PARAM;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupHttpstepAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Step of scenario"));
		Nest.value(page, "file").$("popup_httpstep.action");		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = CArray.map(
			"dstfrm" ,				array(T_RDA_STR, O_MAND, P_SYS,	NOT_EMPTY,			null),
			"stepid" ,				array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(0,65535),	null),
			"list_name" ,			array(T_RDA_STR, O_OPT, P_SYS,	NOT_EMPTY,			"isset({save})&&isset({stepid})"),
//			"name" ,				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY+KEY_PARAM(), "isset({save})", _("Name")),
			"name" ,				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Name")),
			"url" ,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,			"isset({save})", _("URL")),
			"posts" ,				array(T_RDA_STR, O_OPT, null,	null,				"isset({save})"),
			"variables" ,			array(T_RDA_STR, O_OPT, null,	null,				"isset({save})"),
			"timeout" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(0,65535),	"isset({save})", _("Timeout")),
			"required" ,			array(T_RDA_STR, O_OPT, null,	null,				"isset({save})"),
			"status_codes" ,	array(T_RDA_STR, O_OPT, null,	null,				"isset({save})"),
			"templated" ,		array(T_RDA_STR, O_OPT, null, 	null, null),
			"old_name",			array(T_RDA_STR, O_OPT, null, 	null, null),
			"steps_names",		array(T_RDA_STR, O_OPT, null, 	null, null),
			// actions
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"form" ,				array(T_RDA_STR, O_OPT, P_SYS,	null,				null),
			"form_refresh" ,	array(T_RDA_STR, O_OPT, null,	null,				null)
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
	protected void doAction(SQLExecutor executor) {
		// render view
		Map data = new HashMap();
		CView httpPopupView = new CView("configuration.httpconf.popup", data);
		httpPopupView.render(getIdentityBean(), executor);
		httpPopupView.show();
	}

}
