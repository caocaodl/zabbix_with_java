package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
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
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"interfaceid",					array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	null ,"平台网络接口"),
			"event_ack_enable",        			array(T_RDA_INT, O_OPT, null,        IN("1"),            null),
			"event_show_max",          			array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999),  "isset({save})", _("Max count of events per trigger to show")),
			"dropdown_first_entry",    		array(T_RDA_INT, O_OPT, null,        IN("0,1,2"),        "isset({save})"),
			"dropdown_first_remember", 	array(T_RDA_INT, O_OPT, null,        IN("1"),            null),
			"max_in_table",            				array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999),  "isset({save})", _("Max count of elements to show inside table cell")),
			"search_limit",            				array(T_RDA_INT, O_OPT, null,        BETWEEN(1, 99999), "isset({save})", _("Search/Filter elements limit")),
			"server_check_interval",   			array(T_RDA_INT, O_OPT, null,        null,               null, _("iRadar server activity check interval")),
			"save",                    					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,               null),
			"form_refresh",            				array(T_RDA_INT, O_OPT, null,        null,               null),
			"form" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,		null)
			
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
					"event_ack_enable", (is_null(get_request("event_ack_enable")) ? 0 : 1),
					"event_expire", get_request("event_expire"),
					"event_show_max", get_request("event_show_max"),
					"dropdown_first_entry", get_request("dropdown_first_entry"),
					"dropdown_first_remember", (is_null(get_request("dropdown_first_remember")) ? 0 : 1),
					"max_in_table", get_request("max_in_table"),
					"search_limit", get_request("search_limit"),
					"server_check_interval", get_request("server_check_interval", 0),
					"hk_services",get_request("interfaceid")//获取隐藏字段接口id并把值赋给config表hk_services字段
				);
				
				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return update_config(getIdentityBean(), executor, configs);
					}
				});
				
				show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
				DBend(executor, result);
		}
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.gui.action", "redirect(this.options[this.selectedIndex].value);");
		cmbConf.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
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
			
		));
		form.addItem(cmbConf);
		CWidget cnf_wdgt = new CWidget();
		cnf_wdgt.addPageHeader("",form);

		CArray data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		if (!empty(Nest.value(data,"form_refresh").$())) {
			Nest.value(data,"config","hk_services").$(get_request("interfaceid"));
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
