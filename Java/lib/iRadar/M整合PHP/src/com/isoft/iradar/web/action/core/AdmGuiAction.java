package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.core.RBase.getThemes;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
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

public class AdmGuiAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of GUI"));
		page("file", "adm.gui.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray themes = array_keys(getThemes());
		
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"default_theme",           			array(T_RDA_STR, O_OPT, null,        IN("\""+implode("\",\"", themes)+"\""), "isset({save})"),
			"event_ack_enable",        			array(T_RDA_INT, O_OPT, null,        IN("1"),            null),
			"event_expire",            				array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999),  "isset({save})", _("Show events not older than (in days)")),
			"event_show_max",          			array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999),  "isset({save})", _("Max count of events per trigger to show")),
			"dropdown_first_entry",    		array(T_RDA_INT, O_OPT, null,        IN("0,1,2"),        "isset({save})"),
			"dropdown_first_remember", 	array(T_RDA_INT, O_OPT, null,        IN("1"),            null),
			"max_in_table",            				array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999),  "isset({save})", _("Max count of elements to show inside table cell")),
			"search_limit",            				array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 999999), "isset({save})", _("Search/Filter elements limit")),
			"server_check_interval",   			array(T_RDA_INT, O_OPT, null,        null,               null, _("iRadar server activity check interval")),
			"save",                    					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,               null),
			"form_refresh",            				array(T_RDA_INT, O_OPT, null,        null,               null)
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
					"default_theme", get_request("default_theme"),
					"event_ack_enable", (is_null(get_request("event_ack_enable")) ? 0 : 1),
					"event_expire", get_request("event_expire"),
					"event_show_max", get_request("event_show_max"),
					"dropdown_first_entry", get_request("dropdown_first_entry"),
					"dropdown_first_remember", (is_null(get_request("dropdown_first_remember")) ? 0 : 1),
					"max_in_table", get_request("max_in_table"),
					"search_limit", get_request("search_limit"),
					"server_check_interval", get_request("server_check_interval", 0)
				);
				
				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return update_config(getIdentityBean(), executor, configs);
					}
				});
				
				show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
				
//				if (result) {
//					CArray msg = array();
//					msg.add(_s("Default theme \"%1$s\".", get_request("default_theme")));
//					msg.add(_s("Event acknowledges \"%1$s\".", get_request("event_ack_enable")));
//					msg.add(_s("Show events not older than (in days) \"%1$s\".", get_request("event_expire")));
//					msg.add(_s("Show events max \"%1$s\".", get_request("event_show_max")));
//					msg.add(_s("Dropdown first entry \"%1$s\".", get_request("dropdown_first_entry")));
//					msg.add(_s("Dropdown remember selected \"%1$s\".", get_request("dropdown_first_remember")));
//					msg.add(_s("Max count of elements to show inside table cell \"%1$s\".", get_request("max_in_table")));
//					msg.add(_s("iRadar server is running check interval \"%1$s\".", get_request("server_check_interval")));
//
//					add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IRADAR_CONFIG, implode("; ", msg));
//				}
				
				DBend(executor, result);
		}
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.gui.action", "redirect(this.options[this.selectedIndex].value);");
		cmbConf.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
//FIXME			
//			"adm.images.action", _("Images"),
//			"adm.iconmapping.action", _("Icon mapping"),
			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"adm.other.action", _("Other")
		));
		form.addItem(cmbConf);

		CWidget cnf_wdgt = new CWidget();
		cnf_wdgt.addPageHeader(_("CONFIGURATION OF GUI"), form);

		CArray data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		if (!empty(Nest.value(data,"form_refresh").$())) {
			Nest.value(data,"config","default_theme").$(get_request("default_theme"));
			Nest.value(data,"config","event_ack_enable").$(get_request("event_ack_enable"));
			Nest.value(data,"config","dropdown_first_entry").$(get_request("dropdown_first_entry"));
			Nest.value(data,"config","dropdown_first_remember").$(get_request("dropdown_first_remember"));
			Nest.value(data,"config","search_limit").$(get_request("search_limit"));
			Nest.value(data,"config","max_in_table").$(get_request("max_in_table"));
			Nest.value(data,"config","event_expire").$(get_request("event_expire"));
			Nest.value(data,"config","event_show_max").$(get_request("event_show_max"));
			Nest.value(data,"config","server_check_enabled").$(get_request("server_check_enabled"));
			Nest.value(data,"config","server_check_interval").$(get_request("server_check_interval", 0));
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor, false));
		}

		CView guiForm = new CView("administration.general.gui.edit", data);
		cnf_wdgt.addItem(guiForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}

}
