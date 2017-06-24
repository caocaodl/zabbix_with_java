package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.jsRedirect;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HtmlUtil.fatal_error;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TrCommentsAction extends RadarBaseAction {
	
	private Map trigger;

	@Override
	protected void doInitPage() {
		page("title", _("Trigger comments"));
		page("file", "tr_comments.action");
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"triggerid",		array(T_RDA_INT, O_MAND, P_SYS,			DB_ID,	null),
			"comments",	array(T_RDA_STR, O_OPT, null,			null,	"isset({save})"),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null)
		);
		check_fields(getIdentityBean(), fields);

		if (!isset(Nest.value(_REQUEST,"triggerid").$())) {
			fatal_error(getIdentityBean(), _("No triggers defined."));
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		CTriggerGet toptions = new CTriggerGet();
		toptions.setTriggerIds(Nest.value(_REQUEST, "triggerid").asLong());
		toptions.setOutput(API_OUTPUT_EXTEND);
		toptions.setExpandDescription(true);
		CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);

		if (empty(triggers)) {
			access_deny();
		}

		trigger = reset(triggers);
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
			Map params = new HashMap();
			params.put("comments", Nest.value(_REQUEST,"comments").$());
			params.put("triggerid", Nest.value(_REQUEST,"triggerid").$());
			boolean result = DBexecute(executor,
				"UPDATE triggers"+
				" SET comments=#{comments}"+
				" WHERE triggerid=#{triggerid}",
				params
			);
			show_messages(result, _("Comment updated"), _("Cannot update comment"));

			Nest.value(trigger,"comments").$(Nest.value(_REQUEST,"comments").$());

			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_TRIGGER,
					_("Trigger")+" ["+Nest.value(_REQUEST,"triggerid").asString()+"] ["+Nest.value(trigger,"description").asString()+"] "+
					_("Comments")+" ["+Nest.value(_REQUEST,"comments").asString()+"]");
			}
		} else if (isset(_REQUEST,"cancel")) {
			jsRedirect("tr_status.action");
			return;
		}

		/* Display */
		CTriggerGet toptions = new CTriggerGet();
		toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
		toptions.setOutput(new String[]{"triggerid"});
		toptions.setEditable(true);
		CArray<Map> triggerEditable = API.Trigger(getIdentityBean(), executor).get(toptions);

		CArray data = map(
			"triggerid", get_request("triggerid"),
			"trigger", trigger,
			"isTriggerEditable", !empty(triggerEditable),
			"isCommentExist", !empty(Nest.value(trigger,"comments").$())
		);

		// render view
		CView triggerCommentView = new CView("monitoring.triggerComment", data);
		triggerCommentView.render(getIdentityBean(), executor);
		triggerCommentView.show();
	}
}
