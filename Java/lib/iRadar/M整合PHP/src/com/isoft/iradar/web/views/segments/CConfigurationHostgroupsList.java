package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostgroupsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostGroupWidget = new CWidget();

		// create new hostgroup button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		CSubmit tmpItem = null;
		if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
			tmpItem  = new CSubmit("form", _("Create host group"));
		} else {
			tmpItem = new CSubmit("form", _("Create host group")+SPACE+_("(Only super admins can create groups)"));
			tmpItem.setEnabled(false);
		}
		createForm.addItem(tmpItem);

		hostGroupWidget.addPageHeader(_("CONFIGURATION OF HOST GROUPS"), createForm);

		// header
		hostGroupWidget.addHeader(_("Host groups"));
		hostGroupWidget.addHeaderRowNumber();

		// create form
		CForm hostGroupForm = new CForm();
		hostGroupForm.setName("hostgroupForm");

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
			" # ",
			_("Members"),
			(showStatus) ? _("Status") : null
		));

		for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
			CArray hostsOutput = array();
			int i = 0;

			String url = null;
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
			array_pop(hostsOutput);

			Integer hostCount = Nest.value(data,"groupCounts",group.get("groupid"),"hosts").asInteger();
			Integer templateCount = Nest.value(data,"groupCounts",group.get("groupid"),"templates").asInteger();

			// name
			CArray name = array();
			if (!empty(Nest.value(group,"discoveryRule").$())) {
				name.add(new CLink(Nest.value(group,"discoveryRule","name").$(), "host_prototypes.action?parent_discoveryid="+Nest.value(group,"discoveryRule","itemid").$(), "parent-discovery"));
				name.add(NAME_DELIMITER);
			}
			name.add(new CLink(Nest.value(group,"name").$(), "hostgroups.action?form=update&groupid="+Nest.value(group,"groupid").$()));

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

			hostGroupTable.addRow(array(
				new CCheckBox("groups["+Nest.value(group,"groupid").$()+"]", false, null, Nest.value(group,"groupid").asInteger()),
				name,
				array(
					array(new CLink(_("Templates"), "templates.action?groupid="+Nest.value(group,"groupid").$(), "unknown"), " ("+templateCount+")"),
					BR(),
					array(new CLink(_("Hosts"), "hosts.action?groupid="+Nest.value(group,"groupid").$()), " ("+hostCount+")")
				),
				new CCol(empty(hostsOutput) ? "-" : hostsOutput, "wraptext"),
				(showStatus) ? status : null
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected hosts?"));
		goComboBox.addItem(goOption);
		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable hosts in the selected host groups?"));
		goComboBox.addItem(goOption);
		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected host groups?"));
		goComboBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"groups\";");

		// append table to form
		hostGroupForm.addItem(array(Nest.value(data,"paging").$(), hostGroupTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		hostGroupWidget.addItem(hostGroupForm);
		return hostGroupWidget;
	}

}
