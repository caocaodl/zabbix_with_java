package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ProfilesUtil.update_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import java.util.Map;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmTriggerdisplayoptionsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of trigger displaying options"));
		page("file", "adm.triggerdisplayoptions.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR					        TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				// VAR					        TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
				"problem_unack_color",array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"problem_ack_color",	array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"ok_unack_color",			array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"ok_ack_color",				array(T_RDA_STR, O_OPT,	null,	null,		"isset({save})"),
				"problem_unack_style",	array(T_RDA_INT, O_OPT,	null,	IN("1"),	 null),
				"problem_ack_style",		array(T_RDA_INT, O_OPT,	null,	IN("1"),	 null),
				"ok_unack_style",			array(T_RDA_INT, O_OPT,	null,	IN("1"),	 null),
				"ok_ack_style",				array(T_RDA_INT, O_OPT,	null,	IN("1"),	 null),
				//"ok_period",					array(T_RDA_INT, O_OPT,	null,	null,		"isset({save})"),
				//"blink_period",				array(T_RDA_INT, O_OPT,	null,	null,		"isset({save})"),

				"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
				"form",							array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
				"form_refresh",				array(T_RDA_INT, O_OPT,	null,	null,	null)
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
					"ok_period", get_request("ok_period"),
					"blink_period", get_request("blink_period"),
					"problem_unack_color", get_request("problem_unack_color"),
					"problem_ack_color", get_request("problem_ack_color"),
					"ok_unack_color", get_request("ok_unack_color"),
					"ok_ack_color", get_request("ok_ack_color"),
					"problem_unack_style", get_request("problem_unack_style", 0),
					"problem_ack_style", get_request("problem_ack_style", 0),
					"ok_unack_style", get_request("ok_unack_style", 0),
					"ok_ack_style", get_request("ok_ack_style", 0)
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
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.triggerdisplayoptions.action", "redirect(this.options[this.selectedIndex].value);");
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


		CWidget cnf_wdgt = new CWidget();
		cnf_wdgt.addPageHeader(SPACE,form);

		CArray data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		// form has been submitted
		if (Nest.value(data,"form_refresh").asBoolean()) {
			Nest.value(data,"ok_period").$(get_request("ok_period"));
			Nest.value(data,"blink_period").$(get_request("blink_period"));
			Nest.value(data,"problem_unack_color").$(get_request("problem_unack_color"));
			Nest.value(data,"problem_ack_color").$(get_request("problem_ack_color"));
			Nest.value(data,"ok_unack_color").$(get_request("ok_unack_color"));
			Nest.value(data,"ok_ack_color").$(get_request("ok_ack_color"));
			Nest.value(data,"problem_unack_style").$(get_request("problem_unack_style"));
			Nest.value(data,"problem_ack_style").$(get_request("problem_ack_style"));
			Nest.value(data,"ok_unack_style").$(get_request("ok_unack_style"));
			Nest.value(data,"ok_ack_style").$(get_request("ok_ack_style"));
		} else {
			Map<String, Object> config = select_config(getIdentityBean(), executor, false);
			Nest.value(data,"ok_period").$(Nest.value(config,"ok_period").$());
			Nest.value(data,"blink_period").$(Nest.value(config,"blink_period").$());
			Nest.value(data,"problem_unack_color").$(Nest.value(config,"problem_unack_color").$());
			Nest.value(data,"problem_ack_color").$(Nest.value(config,"problem_ack_color").$());
			Nest.value(data,"ok_unack_color").$(Nest.value(config,"ok_unack_color").$());
			Nest.value(data,"ok_ack_color").$(Nest.value(config,"ok_ack_color").$());
			Nest.value(data,"problem_unack_style").$(Nest.value(config,"problem_unack_style").$());
			Nest.value(data,"problem_ack_style").$(Nest.value(config,"problem_ack_style").$());
			Nest.value(data,"ok_unack_style").$(Nest.value(config,"ok_unack_style").$());
			Nest.value(data,"ok_ack_style").$(Nest.value(config,"ok_ack_style").$());
		}

		CView triggerDisplayingForm = new CView("administration.general.triggerDisplayOptions.edit", data);
		cnf_wdgt.addItem(triggerDisplayingForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}

}
