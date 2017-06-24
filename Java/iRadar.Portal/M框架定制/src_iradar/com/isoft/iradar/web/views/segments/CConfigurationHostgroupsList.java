package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostgroupsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostGroupWidget = new CWidget();

		// create form
		CForm hostGroupForm = new CForm();
		hostGroupForm.setName("hostgroupForm");
		
		CToolBar tb = new CToolBar(hostGroupForm);
		if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
			tb.addSubmit("form", _("Create host group"), "", "orange create");
		} else {
			tb.addSubmit("form", _("Create host group")+SPACE+_("(Only super admins can create groups)"), "", "orange create","readonly");
		}
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected host groups?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"groups\";");
		/*rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"groupid").$()+"\";");		
		rda_add_post_js("cookie.prefix = \""+Nest.value(data,"groupid").$()+"\";");*/
		
		// header
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		hostGroupWidget.addItem(headerActions);

		// if any of the groups are about to be deleted, show the status column
		boolean showStatus = false;
		for(Map hostGroup : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
			if (!empty(Nest.value(hostGroup,"groupDiscovery").$()) && !empty(Nest.value(hostGroup,"groupDiscovery","ts_delete").$())) {
				showStatus = true;
				break;
			}
		}

		// create table
		CTableInfo hostGroupTable = new CTableInfo(_("No host groups found."));
		hostGroupTable.setHeader(array(
			new CCheckBox("all_groups", false, "checkAll(\""+hostGroupForm.getName()+"\", \"all_groups\", \"groups\");"),
			make_sorting_header(_("Name"), "name"),
			_("hosts"),
			(showStatus) ? _("Status") : null
		));

		for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
			//列表隐藏 发现设备、模型
			Object gname = Nest.value(group, "name").$();
			if("Discovered hosts".equals(gname) || "Templates".equals(gname)){
				continue;
			}
			
			CArray hostsOutput = array();
			int i = 0;

			/*String url = null;
			for(Map template : (CArray<Map>)Nest.value(group,"templates").asCArray()) {
				i++;

				if (i > Nest.value(data,"config","max_in_table").asInteger()) {
					hostsOutput.add("...");
					hostsOutput.add("//empty for array_pop");
					break;
				}

				url = "templates.action?form=update&templateid="+Nest.value(template,"templateid").$()+"&groupid="+Nest.value(group,"groupid").$();

				hostsOutput.add(new CLink(Nest.value(template,"name").$(), url, "unknown"));
				hostsOutput.add(", ");
			}

			if (!empty(hostsOutput)) {
				array_pop(hostsOutput);

				hostsOutput.add(BR());
				hostsOutput.add(BR());
			}

			for(Map host : (CArray<Map>)Nest.value(group,"hosts").asCArray()) {
				i++;

				if (i > Nest.value(data,"config","max_in_table").asInteger()) {
					hostsOutput.add("...");
					hostsOutput.add("//empty for array_pop");
					break;
				}

				String style = null;
				switch (Nest.value(host,"status").asInteger()) {
					case HOST_STATUS_NOT_MONITORED:
						style = "on";
						url = "hosts.action?form=update&hostid="+Nest.value(host,"hostid").asString()+"&groupid="+Nest.value(group,"groupid").$();
						break;

					default:
						style = null;
						url = "hosts.action?form=update&hostid="+Nest.value(host,"hostid").asString()+"&groupid="+Nest.value(group,"groupid").$();
					break;
				}

				hostsOutput.add(new CLink(Nest.value(host,"name").$(), url, style));
				hostsOutput.add(", ");
			}
			array_pop(hostsOutput);*/

			Integer hostCount = Nest.value(data,"groupCounts",group.get("groupid"),"hosts").asInteger();
			//Integer templateCount = Nest.value(data,"groupCounts",group.get("groupid"),"templates").asInteger();

			// name
			CArray name = array();
			if (!empty(Nest.value(group,"discoveryRule").$())) {
				name.add(new CLink(Nest.value(group,"discoveryRule","name").$(), "host_prototypes.action?parent_discoveryid="+Nest.value(group,"discoveryRule","itemid").$(), "parent-discovery"));
				name.add(NAME_DELIMITER);
			}
			name.add(new CLink(Nest.value(group,"name").$(), "hostgroups.action?form=update&groupid="+Nest.value(group,"groupid").$(),"designation"));

			// status
			CArray status = null;
			if (showStatus) {
				status = array();

				// discovered item lifetime indicator
				if (Nest.value(group,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED && !empty(Nest.value(group,"groupDiscovery","ts_delete").$())) {
					CDiv deleteError = new CDiv(SPACE, "status_icon iconwarning");
					deleteError.setHint(
						_s("The host group is not discovered anymore and will be deleted in %1$s (on %2$s at %3$s).",
							rda_date2age(Nest.value(group,"groupDiscovery","ts_delete").asLong()), rda_date2str(_("d M Y"), Nest.value(group,"groupDiscovery","ts_delete").asLong()),
							rda_date2str(_("H:i:s"), Nest.value(group,"groupDiscovery","ts_delete").asLong())
						));
					status.add(deleteError);
				}
			}
			String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
			String url= "'"+(Nest.value(group,"groupid").asLong()==IMonConsts.MON_VM?_("Monitor center"):_("Hosts"))+"', '"+common_action_with_context+(Nest.value(group,"groupid").asLong()==IMonConsts.MON_VM?"mon_vm.action":("hostMonitor.action?groupid="+Nest.value(group,"groupid").$()))+"'";
			hostGroupTable.addRow(array(
				new CCheckBox("groups["+Nest.value(group,"groupid").$()+"]", false, null, Nest.value(group,"groupid").asInteger()),
				name,
				array(new CLink(Nest.value(group,"groupid").asLong()==IMonConsts.MON_VM?_("Monitor center"):_("Hosts"),  IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"device_type",null,Boolean.TRUE),
				" ("+hostCount+")"),
				(showStatus) ? status : null
			));
		}
		// append table to form
		hostGroupForm.addItem(array(hostGroupTable, Nest.value(data,"paging").$()));

		// append form to widget
		hostGroupWidget.addItem(hostGroupForm);
		return hostGroupWidget;
	}
}
