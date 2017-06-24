package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_NO;
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

public class AdmWorkingtimeAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of working time"));
		page("file", "adm.workingtime.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		String period = get_request("work_period");
		if (period != null && period.length() > 0) {
			period = period.replaceAll(" ", "");
		}
		Nest.value(_REQUEST,"work_period").$(period);
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				"work_period",	array(T_RDA_STR, O_NO,	null,			null,	"isset({save})"),
				"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
				"form_refresh",	array(T_RDA_INT, O_OPT,	null,			null,	null)
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
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return update_config(getIdentityBean(), executor, map("work_period", get_request("work_period")));
				}
			});
			show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
			
//			if (result) {
//				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IRADAR_CONFIG, _s("Working time \"%1$s\".", get_request("work_period")));
//			}
			
			DBend(executor, result);
		}
		
		/* Display */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.workingtime.action", "redirect(this.options[this.selectedIndex].value);");
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

		if (Nest.value(data,"form_refresh").asBoolean()) {
			Nest.value(data,"config","work_period").$(get_request("work_period"));
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor, false));
		}

		CView workingTimeForm = new CView("administration.general.workingtime.edit", data);
		cnf_wdgt.addItem(workingTimeForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}
}
