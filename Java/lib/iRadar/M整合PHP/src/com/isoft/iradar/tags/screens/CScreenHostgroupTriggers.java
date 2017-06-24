package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.BlocksUtil.make_latest_issues;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_DATE_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_HOST_NAME_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_SEVERITY_DESC;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenHostgroupTriggers extends CScreenBase {

	public CScreenHostgroupTriggers(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenHostgroupTriggers(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		CArray params = map(
			"groupids", null,
			"hostids", null,
			"maintenance", null,
			"severity", null,
			"limit", Nest.value(screenitem,"elements").$(),
			"backUrl", pageFile
		);

		// by default triggers are sorted by date desc, do we need to override this?
		switch (Nest.value(screenitem,"sort_triggers").asInteger()) {
			case SCREEN_SORT_TRIGGERS_DATE_DESC:
				Nest.value(params,"sortfield").$("lastchange");
				Nest.value(params,"sortorder").$(RDA_SORT_DOWN);
				break;
			case SCREEN_SORT_TRIGGERS_SEVERITY_DESC:
				Nest.value(params,"sortfield").$("priority");
				Nest.value(params,"sortorder").$(RDA_SORT_DOWN);
				break;
			case SCREEN_SORT_TRIGGERS_HOST_NAME_ASC:
				// a little black magic here - there is no such field "hostname" in "triggers",
				// but API has a special case for sorting by hostname
				Nest.value(params,"sortfield").$("hostname");
				Nest.value(params,"sortorder").$(RDA_SORT_UP);
				break;
		}

		CTag item = null;
		if (Nest.value(screenitem,"resourceid").asLong() > 0) {
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(Nest.value(screenitem,"resourceid").asLong());
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> hostgroups = API.HostGroup(this.idBean, this.executor).get(hgoptions);
			Map hostgroup = reset(hostgroups);

			item = new CSpan(_("Group")+NAME_DELIMITER+Nest.value(hostgroup,"name").$(), "white");
			Nest.value(params,"groupids").$(Nest.value(hostgroup,"groupid").$());
		} else {
			long groupid = get_request("tr_groupid", Nest.as(CProfile.get(this.idBean, this.executor,"web.screens.tr_groupid", 0)).asLong());
			long hostid = get_request("tr_hostid", Nest.as(CProfile.get(this.idBean, this.executor, "web.screens.tr_hostid", 0)).asLong());

			CProfile.update(this.idBean, this.executor, "web.screens.tr_groupid", groupid, PROFILE_TYPE_ID);
			CProfile.update(this.idBean, this.executor, "web.screens.tr_hostid", hostid, PROFILE_TYPE_ID);

			// get groups
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setMonitoredHosts(true);
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> groups = API.HostGroup(this.idBean, this.executor).get(hgoptions);
			order_result(groups, "name");

			// get hosts
			CHostGet hoptions = new CHostGet();
			hoptions.setMonitoredHosts(true);
			hoptions.setOutput(API_OUTPUT_EXTEND);
			if (groupid > 0) {
				hoptions.setGroupIds(groupid);
			}
			CArray<Map> hosts = API.Host(this.idBean, this.executor).get(hoptions);
			hosts = rda_toHash(hosts, "hostid");
			order_result(hosts, "host");

			if (!isset(hosts,hostid)) {
				hostid = 0;
			}

			if (groupid > 0) {
				Nest.value(params,"groupids").$(groupid);
			}
			if (hostid > 0) {
				Nest.value(params,"hostids").$(hostid);
			}

			item = new CForm(null, pageFile);

			CComboBox groupComboBox = new CComboBox("tr_groupid", groupid, "submit()");
			groupComboBox.addItem(0, _("all"));
			for(Map group : groups) {
				groupComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
			}

			CComboBox hostComboBox = new CComboBox("tr_hostid", hostid, "submit()");
			hostComboBox.addItem(0, _("all"));
			for(Map host : hosts) {
				hostComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"host").asString());
			}

			if (this.mode == SCREEN_MODE_EDIT) {
				groupComboBox.attr("disabled", "disabled");
				hostComboBox.attr("disabled", "disabled");
			}

			item.addItem(array(_("Group")+SPACE, groupComboBox));
			item.addItem(array(SPACE+_("Host")+SPACE, hostComboBox));
		}

		Nest.value(params,"screenid").$(screenid);

		CUIWidget output = new CUIWidget("hat_htstatus", make_latest_issues(idBean, executor, params));
		output.setDoubleHeader(array(_("HOST GROUP ISSUES"), SPACE, rda_date2str(_("[H:i:s]")), SPACE), item);

		return getOutput(output);
	}

}
