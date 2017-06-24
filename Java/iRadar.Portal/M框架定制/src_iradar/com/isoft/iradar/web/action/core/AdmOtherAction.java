package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_INTERNAL_GROUP;
import static com.isoft.iradar.inc.Defines.RDA_NOT_INTERNAL_GROUP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HostsUtil.setHostGroupInternal;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ProfilesUtil.update_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmOtherAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Other configuration parameters"));
		page("file", "adm.other.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"refresh_unsupported", array(T_RDA_INT, O_NO,	null, BETWEEN(0, 65535), "isset({save})", _("Refresh unsupported items (in sec)")),
			"alert_usrgrpid",			array(T_RDA_INT, O_NO,	null,		DB_ID,				"isset({save})"),
			"discovery_groupid",		array(T_RDA_INT, O_NO,	null,		DB_ID,				"isset({save})"),
			"snmptrap_logging",		array(T_RDA_INT, O_OPT,	null,		IN("1"),			null),
			"save",							array(T_RDA_STR, O_OPT,	P_SYS|P_ACT, null,				null),
			"form_refresh",				array(T_RDA_INT, O_OPT,	null,		null,				null)
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
			
			Map<String, Object> orig_config = select_config(getIdentityBean(), executor, false);
			
			final CArray configs = map(
				"refresh_unsupported", get_request("refresh_unsupported"),
				"alert_usrgrpid", get_request("alert_usrgrpid"),
				"discovery_groupid", get_request("discovery_groupid"),
				"snmptrap_logging", !empty(get_request("snmptrap_logging")) ? 1 : 0
			);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return update_config(getIdentityBean(), executor, configs);
				}
			});
			
			show_messages(result, _("Configuration updated"), _("Cannot update configuration"));
			
			if (result) {
				CArray audit = array(
					_s("Refresh unsupported items (in sec) \"%1$s\".", get_request("refresh_unsupported"))
				);
				
				if (hasRequest("discovery_groupid")) {
					CHostGroupGet options = new CHostGroupGet();
					options.setGroupIds(get_request_asLong("discovery_groupid"));
					options.setEditable(true);
					options.setOutput(new String[]{"groupid", "name"});
					CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(options);
					if (hostGroups!=null && !hostGroups.isEmpty()) {
						Map hostGroup = reset(hostGroups);

						audit.add(_s("Group for discovered hosts \"%1$s\".", Nest.value(hostGroup,"name").$()));

						if (bccomp(Nest.value(hostGroup,"groupid").$(), Nest.value(orig_config,"discovery_groupid").$()) != 0) {
							setHostGroupInternal(getIdentityBean(), executor, new Long[]{Nest.value(orig_config,"discovery_groupid").asLong()}, RDA_NOT_INTERNAL_GROUP);
							setHostGroupInternal(getIdentityBean(), executor, new Long[]{Nest.value(hostGroup,"groupid").asLong()}, RDA_INTERNAL_GROUP);
						}
					}
				}
				
//				if (hasRequest("alert_usrgrpid")) {
//					String userGroupId = get_request("alert_usrgrpid");
//
//					String userGroupName = null;
//					if (!empty(userGroupId)) {
//						String sql = "SELECT u.name FROM usrgrp u WHERE u.usrgrpid=#{usrgrpid}";
//						Map params = new HashMap();
//						params.put("usrgrpid", userGroupId);
//						List<String> datas = executor.executeNameParaQuery(sql, params, String.class);
//						userGroupName = reset(datas);
//					} else {
//						userGroupName = _("None");
//					}
//
//					audit.add( _s("User group for database down message \"%1$s\".", userGroupName));
//				}
//
//				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_IRADAR_CONFIG, implode("; ", audit));
			}
			
			DBend(executor);
		}
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.other.action", "redirect(this.options[this.selectedIndex].value);");
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
			"adm.other.action", _("Other"),
			"adm.operationsystem.action","操作系统类型",
			"users.action",_("notice"),
			"cbn.action?actionType=DEPT", _("DEPT"),
			"cbn.action?actionType=mRoom", _("mRoom"),
			"cbn.action?actionType=Cabinet", _("Cabinet"),
			"cbn.action?actionType=firm", _("FIRM"),
			"adm.operationsystem.action?actionType=otsys", _("otsys")
		));
		form.addItem(cmbConf);

		CWidget cnf_wdgt = new CWidget();
		cnf_wdgt.addPageHeader(SPACE,form);

		CArray data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		if (Nest.value(data,"form_refresh").asBoolean()) {
			Nest.value(data,"config","discovery_groupid").$(get_request("discovery_groupid"));
			Nest.value(data,"config","alert_usrgrpid").$(get_request("alert_usrgrpid"));
			Nest.value(data,"config","refresh_unsupported").$(get_request("refresh_unsupported"));
			Nest.value(data,"config","snmptrap_logging").$(get_request("snmptrap_logging"));
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor, false));
		}

		CHostGroupGet options = new CHostGroupGet();
		options.setOutput(new String[]{"usrgrpid", "name"});
		options.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
		options.setEditable(true);
		Nest.value(data,"discovery_groups").$(API.HostGroup(getIdentityBean(), executor).get(options));
		order_result(Nest.value(data,"discovery_groups").asCArray(), "name");

		Nest.value(data,"alert_usrgrps").$(DBfetchArray(DBselect(executor,
			"SELECT u.usrgrpid,u.name"+
			" FROM usrgrp u"
		)));
		order_result(Nest.value(data,"alert_usrgrps").asCArray(), "name");

		CView otherForm = new CView("administration.general.other.edit", data);
		cnf_wdgt.addItem(otherForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}

}
