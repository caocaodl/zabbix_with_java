package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ProfilesUtil.update_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmHousekeeperAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of housekeeping"));
		page("file", "adm.housekeeper.action");
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/systemmanage/admhousekeeper.css"});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hk_events_mode",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_events_trigger", 		array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("Trigger event and alert data storage period")),
//			"hk_events_internal", 	array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("Internal event and alert data storage period")),
			"hk_events_discovery",	array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("Network discovery event and alert data storage period")),
//			"hk_events_autoreg", 	array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("Auto-registration event and alert data storage period")),
			"hk_services_mode",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_services", 				array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("IT service data storage period")),
			"hk_audit_mode",			array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_audit", 					array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("Audit data storage period")),
//			"hk_sessions_mode",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
//			"hk_sessions", 				array(T_RDA_INT, O_OPT, null, BETWEEN(1, 99999), null, _("User session data storage period")),
			"hk_history_mode",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_history_global",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_history", 				array(T_RDA_INT, O_OPT, null, BETWEEN(0, 99999), null, _("History data storage period")),
			"hk_trends_mode",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_trends_global",		array(T_RDA_INT, O_OPT, null, IN("0,1"), null),
			"hk_trends", 					array(T_RDA_INT, O_OPT, null, BETWEEN(0, 99999), null, _("Trend data storage period")),
			"save",							array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null, null),
			"form_refresh",				array(T_RDA_INT, O_OPT,	null, null, null)
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
		/* Actions */
		if (isset(_REQUEST,"save")) {
			DBstart(executor);
			
			final CArray configs = map(
					"hk_events_mode", get_request("hk_events_mode", 0),
					"hk_events_trigger", get_request("hk_events_trigger"),
//					"hk_events_internal", get_request("hk_events_internal"),
					"hk_events_discovery", get_request("hk_events_discovery"),
//					"hk_events_autoreg", get_request("hk_events_autoreg"),
					"hk_services_mode", get_request("hk_services_mode", 0),
					"hk_services", get_request("hk_services"),
					"hk_audit_mode", get_request("hk_audit_mode", 0),
					"hk_audit", get_request("hk_audit"),
					"hk_sessions_mode", get_request("hk_sessions_mode", 0),
					"hk_sessions", get_request("hk_sessions"),
					"hk_history_mode", get_request("hk_history_mode", 0),
					"hk_history_global", get_request("hk_history_global", 0),
					"hk_history", get_request("hk_history"),
					"hk_trends_mode", get_request("hk_trends_mode", 0),
					"hk_trends_global", get_request("hk_trends_global", 0),
					"hk_trends", get_request("hk_trends")
			);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return update_config(getIdentityBean(), executor, configs);
				}
			});
			
			show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
			
//			if (result) {
//				CArray msg = array();
//				msg.add(_s("Trigger event and alert data storage period (in days) \"%1$s\".", get_request("hk_events_trigger")));
//				msg.add(_s("Internal event and alert data storage period (in days) \"%1$s\".", get_request("hk_events_internal")));
//				msg.add(_s("Network discovery event and alert data storage period (in days) \"%1$s\".", get_request("hk_events_discovery")));
//				msg.add(_s("Auto-registration event and alert data storage period (in days) \"%1$s\".", get_request("hk_events_autoreg")));
//				msg.add(_s("IT service data storage period (in days) \"%1$s\".", get_request("hk_services")));
//				msg.add(_s("Audit data storage period (in days) \"%1$s\".", get_request("hk_audit")));
//				msg.add(_s("User session data storage period (in days) \"%1$s\".", get_request("hk_sessions")));
//				msg.add(_s("History data storage period (in days) \"%1$s\".", get_request("hk_history")));
//				msg.add(_s("Trend data storage period (in days) \"%1$s\".", get_request("hk_trends")));
//
//				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IRADAR_CONFIG, implode("; ", msg));
//			}
			
			DBend(executor, result);
		}
		
		CArray data = array();
		Nest.value(data,"config").$(select_config(getIdentityBean(), executor));
		
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.housekeeper.action", "redirect(this.options[this.selectedIndex].value);");
		cmbConf.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
//FIXME			
//			"adm.images.action", _("Images"),
//			"adm.iconmapping.action", _("Icon mapping"),
//			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"cbn.action?actionType=DEPT", _("DEPT"),
			"cbn.action?actionType=mRoom", _("mRoom"),
			"cbn.action?actionType=Cabinet", _("Cabinet"),
			"cbn.action?actionType=firm", _("FIRM"),
			"adm.operationsystem.action?actionType=otsys", _("otsys")
		//	"adm.operationsystem.action","操作系统类型"
		//	"users.action",_("notice")
		));
		form.addItem(cmbConf);
		
		CWidget cnf_wdgt = new CWidget(null, "hk");
		cnf_wdgt.addPageHeader(SPACE,form);

		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));
		
		if (!empty(Nest.value(data,"form_refresh").$())) {
			Nest.value(data,"config","hk_events_mode").$(get_request("hk_events_mode"));
			Nest.value(data,"config","hk_events_trigger").$(isset(Nest.value(_REQUEST,"hk_events_trigger").$())
				? get_request("hk_events_trigger") : Nest.value(data,"config","hk_events_trigger").$());
//			Nest.value(data,"config","hk_events_internal").$(isset(Nest.value(_REQUEST,"hk_events_internal").$())
//				? get_request("hk_events_internal") : Nest.value(data,"config","hk_events_internal").$());
			Nest.value(data,"config","hk_events_discovery").$(isset(Nest.value(_REQUEST,"hk_events_discovery").$())
				? get_request("hk_events_discovery") : Nest.value(data,"config","hk_events_discovery").$());
//			Nest.value(data,"config","hk_events_autoreg").$(isset(Nest.value(_REQUEST,"hk_events_autoreg").$())
//				? get_request("hk_events_autoreg") : Nest.value(data,"config","hk_events_autoreg").$());
			Nest.value(data,"config","hk_services_mode").$(get_request("hk_services_mode"));
			Nest.value(data,"config","hk_services").$(isset(Nest.value(_REQUEST,"hk_services").$())
				? get_request("hk_services") : Nest.value(data,"config","hk_services").$());
			Nest.value(data,"config","hk_audit_mode").$(get_request("hk_audit_mode"));
			Nest.value(data,"config","hk_audit").$(isset(Nest.value(_REQUEST,"hk_audit").$())
				? get_request("hk_audit") : Nest.value(data,"config","hk_audit").$());
			Nest.value(data,"config","hk_sessions_mode").$(get_request("hk_sessions_mode"));
			Nest.value(data,"config","hk_sessions").$(isset(Nest.value(_REQUEST,"hk_sessions").$())
				? get_request("hk_sessions") : Nest.value(data,"config","hk_sessions").$());
			Nest.value(data,"config","hk_history_mode").$(get_request("hk_history_mode"));
			Nest.value(data,"config","hk_history_global").$(get_request("hk_history_global"));
			Nest.value(data,"config","hk_history").$(isset(Nest.value(_REQUEST,"hk_history").$())
				? get_request("hk_history") : Nest.value(data,"config","hk_history").$());
			Nest.value(data,"config","hk_trends_mode").$(get_request("hk_trends_mode"));
			Nest.value(data,"config","hk_trends_global").$(get_request("hk_trends_global"));
			Nest.value(data,"config","hk_trends").$(isset(Nest.value(_REQUEST,"hk_trends").$())
				? get_request("hk_trends") : Nest.value(data,"config","hk_trends").$());
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor, false));
		}

		CView houseKeeperForm = new CView("administration.general.housekeeper.edit", data);
		cnf_wdgt.addItem(houseKeeperForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}

}
