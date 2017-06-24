package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.jsRedirect;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DashconfAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Dashboard configuration"));
		page("file", "dashconf.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] {"multiselect.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR						 TYPE		 OPTIONAL FLAGS	VALIDATION		EXCEPTION
		CArray fields = map(
			"filterEnable",	array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"grpswitch",		array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(0, 1),	null),
			"groupids",		array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"hidegroupids",	array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"trgSeverity",	array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"maintenance",	array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(0, 1),	null),
			"extAck",			array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"form_refresh",	array(T_RDA_INT, O_OPT, P_SYS,	null,			null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS,	null,			null)
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
	public void doAction(SQLExecutor executor) {
		/* Actions  */
		if (isset(_REQUEST,"save")) {
			// filter
			Integer filterEnable = get_request("filterEnable", 0);
			CProfile.update(getIdentityBean(), executor, "web.dashconf.filter.enable", filterEnable, PROFILE_TYPE_INT);

			if (filterEnable == 1) {
				// groups
				CProfile.update(getIdentityBean(), executor, "web.dashconf.groups.grpswitch", Nest.value(_REQUEST,"grpswitch").$(), PROFILE_TYPE_INT);

				if (Nest.value(_REQUEST,"grpswitch").asInteger() == 1) {
					// show groups
					CArray<String> groupIds = get_request("groupids", array());

					CFavorite.remove(getIdentityBean(), executor, "web.dashconf.groups.groupids");
					for(String groupId : groupIds) {
						CFavorite.add(getIdentityBean(), executor, "web.dashconf.groups.groupids", Nest.as(groupId).asLong());
					}

					// hide groups
					CArray<String> hideGroupIds = get_request("hidegroupids", array());

					CFavorite.remove(getIdentityBean(), executor, "web.dashconf.groups.hide.groupids");
					for(String hideGroupId : hideGroupIds) {
						CFavorite.add(getIdentityBean(), executor, "web.dashconf.groups.hide.groupids", Nest.as(hideGroupId).asInteger());
					}
				}

				// hosts
				Nest.value(_REQUEST,"maintenance").$(get_request("maintenance", 0));
				CProfile.update(getIdentityBean(), executor, "web.dashconf.hosts.maintenance", Nest.value(_REQUEST,"maintenance").$(), PROFILE_TYPE_INT);

				// triggers
				Nest.value(_REQUEST,"trgSeverity").$(get_request("trgSeverity", array()));
				Nest.value(_REQUEST,"extAck").$(get_request("extAck", 0));

				CProfile.update(getIdentityBean(), executor, "web.dashconf.triggers.severity", implode(";", array_keys(Nest.value(_REQUEST,"trgSeverity").asCArray())), PROFILE_TYPE_STR);
				CProfile.update(getIdentityBean(), executor, "web.dashconf.events.extAck", Nest.value(_REQUEST,"extAck").$(), PROFILE_TYPE_INT);
			}

			jsRedirect("dashboard.action");
		}

		/* Display */
		CArray data = map(
			"config", select_config(getIdentityBean(), executor)
		);

		if (isset(_REQUEST,"form_refresh")) {
			Nest.value(data,"isFilterEnable").$(get_request("filterEnable", 0));
			Nest.value(data,"maintenance").$(get_request("maintenance", 0));
			Nest.value(data,"extAck").$(get_request("extAck", 0));

			Nest.value(data,"severity").$(get_request("trgSeverity", array()));
			Nest.value(data,"severity").$(array_keys(Nest.value(data,"severity").asCArray()));

			// groups
			Nest.value(data,"grpswitch").$(get_request("grpswitch", 0));
			Nest.value(data,"groupIds").$(get_request("groupids", array()));
			Nest.value(data,"groupIds").$(rda_toHash(Nest.value(data,"groupIds").$()));
			Nest.value(data,"hideGroupIds").$(get_request("hidegroupids", array()));
			Nest.value(data,"hideGroupIds").$(rda_toHash(Nest.value(data,"hideGroupIds").$()));
		} else {
			Nest.value(data,"isFilterEnable").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.filter.enable", 0));
			Nest.value(data,"maintenance").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.hosts.maintenance", 1));
			Nest.value(data,"extAck").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.events.extAck", 0));

			Nest.value(data,"severity").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.triggers.severity", "0;1;2;3;4;5"));
			Nest.value(data,"severity").$(rda_empty(Nest.value(data,"severity").$()) ? array() : explode(";", Nest.value(data,"severity").asString()));

			// groups
			Nest.value(data,"grpswitch").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.groups.grpswitch", 0));
			Nest.value(data,"groupIds").$(CFavorite.get(getIdentityBean(), executor, "web.dashconf.groups.groupids"));
			Nest.value(data,"groupIds").$(rda_objectValues(Nest.value(data,"groupIds").$(), "value"));
			Nest.value(data,"groupIds").$(rda_toHash(Nest.value(data,"groupIds").$()));
			Nest.value(data,"hideGroupIds").$(CFavorite.get(getIdentityBean(), executor, "web.dashconf.groups.hide.groupids"));
			Nest.value(data,"hideGroupIds").$(rda_objectValues(Nest.value(data,"hideGroupIds").$(), "value"));
			Nest.value(data,"hideGroupIds").$(rda_toHash(Nest.value(data,"hideGroupIds").$()));
		}

		Nest.value(data,"severity").$(rda_toHash(Nest.value(data,"severity").$()));
		Nest.value(data,"severities").$(array(
			TRIGGER_SEVERITY_NOT_CLASSIFIED,
			TRIGGER_SEVERITY_INFORMATION,
			TRIGGER_SEVERITY_WARNING,
			TRIGGER_SEVERITY_AVERAGE,
			TRIGGER_SEVERITY_HIGH,
			TRIGGER_SEVERITY_DISASTER
		));

		if (!empty(Nest.value(data,"grpswitch").$())) {
			// show groups
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(Nest.array(data,"groupIds").asLong());
			hgoptions.setOutput(new String[]{"groupid", "name"});
			CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"groups").$(groups);

			CArrayHelper.sort(groups, array(
				map("field", "name", "order", RDA_SORT_UP)
			));

			for (Map group : groups) {
				Nest.value(group,"id").$(Nest.value(group,"groupid").$());
				Nest.value(group,"prefix").$("");
				unset(group,"groupid");
			}

			// hide groups
			hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(Nest.array(data,"hideGroupIds").asLong());
			hgoptions.setOutput(new String[]{"groupid", "name"});
			CArray<Map> hideGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"hideGroups").$(hideGroups);

			CArrayHelper.sort(hideGroups, array(
				map("field", "name", "order", RDA_SORT_UP)
			));

			for(Map group : hideGroups) {
				Nest.value(group,"id").$(Nest.value(group,"groupid").$());
				Nest.value(group,"prefix").$("");
				unset(group,"groupid");
			}
		}

		// render view
		CView dashconfView = new CView("monitoring.dashconf", data);
		dashconfView.render(getIdentityBean(), executor);
		dashconfView.show();
	}

}
