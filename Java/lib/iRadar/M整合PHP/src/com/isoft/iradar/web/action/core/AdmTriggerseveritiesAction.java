package com.isoft.iradar.web.action.core;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ProfilesUtil.update_config;
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

public class AdmTriggerseveritiesAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of trigger severities"));
		page("file", "adm.triggerseverities.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				"severity_name_0",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_0",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_name_1",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_1",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_name_2",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_2",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_name_3",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_3",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_name_4",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_4",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_name_5",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"severity_color_5",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"save",				        array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null,	null),
				"form_refresh",		    array(T_RDA_INT, O_OPT,	null,	null,		null)
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
			final CArray configs = map(
					"severity_name_0", get_request("severity_name_0", _("Not classified")),
					"severity_color_0", get_request("severity_color_0", ""),
					"severity_name_1", get_request("severity_name_1", _("Information")),
					"severity_color_1", get_request("severity_color_1", ""),
					"severity_name_2", get_request("severity_name_2", _("Warning")),
					"severity_color_2", get_request("severity_color_2", ""),
					"severity_name_3", get_request("severity_name_3", _("Average")),
					"severity_color_3", get_request("severity_color_3", ""),
					"severity_name_4", get_request("severity_name_4", _("High")),
					"severity_color_4", get_request("severity_color_4", ""),
					"severity_name_5", get_request("severity_name_5", _("Disaster")),
					"severity_color_5", get_request("severity_color_5", "")
			);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return update_config(getIdentityBean(), executor, configs);
				}
			});
			
			show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
		}
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.triggerseverities.action", "redirect(this.options[this.selectedIndex].value);");
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
		cnf_wdgt.addPageHeader(_("CONFIGURATION OF TRIGGER SEVERITIES"), form);

		CArray data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		if (Nest.value(data,"form_refresh").asBoolean()) {
			Nest.value(data,"config","severity_name_0").$(get_request("severity_name_0"));
			Nest.value(data,"config","severity_color_0").$(get_request("severity_color_0", ""));
			Nest.value(data,"config","severity_name_1").$(get_request("severity_name_1"));
			Nest.value(data,"config","severity_color_1").$(get_request("severity_color_1", ""));
			Nest.value(data,"config","severity_name_2").$(get_request("severity_name_2"));
			Nest.value(data,"config","severity_color_2").$(get_request("severity_color_2", ""));
			Nest.value(data,"config","severity_name_3").$(get_request("severity_name_3"));
			Nest.value(data,"config","severity_color_3").$(get_request("severity_color_3", ""));
			Nest.value(data,"config","severity_name_4").$(get_request("severity_name_4"));
			Nest.value(data,"config","severity_color_4").$(get_request("severity_color_4", ""));
			Nest.value(data,"config","severity_name_5").$(get_request("severity_name_5"));
			Nest.value(data,"config","severity_color_5").$(get_request("severity_color_5", ""));
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor,false));
		}

		CView triggerSeverityForm = new CView("administration.general.triggerSeverity.edit", data);
		cnf_wdgt.addItem(triggerSeverityForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}
}
