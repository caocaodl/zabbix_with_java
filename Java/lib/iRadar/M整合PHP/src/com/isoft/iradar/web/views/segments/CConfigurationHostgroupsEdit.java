package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostgroupsEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostGroupWidget = new CWidget();
		hostGroupWidget.addPageHeader(_("CONFIGURATION OF HOST GROUPS"));

		// create form
		CForm hostGroupForm = new CForm();
		hostGroupForm.setName("hostgroupForm");
		hostGroupForm.addVar("form", Nest.value(data,"form").$());
		if (isset(Nest.value(data,"groupid").$())) {
			hostGroupForm.addVar("groupid", Nest.value(data,"groupid").$());
		}

		// create hostgroup form list
		CFormList hostGroupFormList = new CFormList("hostgroupFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE,
			(!empty(Nest.value(data,"groupid").$()) && Nest.value(data,"group","flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED),
			64
		);
		nameTextBox.attr("autofocus", "autofocus");
		hostGroupFormList.addRow(_("Group name"), nameTextBox);

		// append groups and hosts to form list
		CComboBox groupsComboBox = new CComboBox("twb_groupid", Nest.value(data,"twb_groupid").$(), "submit()");
		groupsComboBox.addItem("0", _("All"));
		for(Map group : (CArray<Map>)Nest.value(data,"db_groups").asCArray()) {
			groupsComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}

		CTweenBox hostsComboBox = new CTweenBox(hostGroupForm, "hosts", Nest.value(data,"hosts").$(), 25);
		for(Map host : (CArray<Map>)Nest.value(data,"db_hosts").asCArray()) {
			if (!isset(Nest.value(data,"hosts",host.get("hostid")).$())) {
				hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString());
			}
		}
		for(Map host : (CArray<Map>)Nest.value(data,"r_hosts").asCArray()) {
			if (isset(Nest.value(data,"r_hosts",host.get("hostid")).$()) && Nest.value(host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
				hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString());
			} else {
				hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString(), true, false);
			}
		}
		hostGroupFormList.addRow(_("Hosts"), hostsComboBox.get(_("Hosts in"), array(_("Other hosts | Group")+SPACE, groupsComboBox)));

		// append tabs to form
		CTabView hostGroupTab = new CTabView();
		hostGroupTab.addTab("hostgroupTab", _("Host group"), hostGroupFormList);
		hostGroupForm.addItem(hostGroupTab);

		// append buttons to form
		if (empty(Nest.value(data,"groupid").$())) {
			hostGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		} else {
			CButtonDelete deleteButton = new CButtonDelete(_("Delete selected group?"), url_param(idBean, "form")+url_param(idBean, "groupid"));
			if (empty(Nest.value(data,"deletableHostGroups").$())) {
				deleteButton.attr("disabled", "disabled");
			}

			hostGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					deleteButton,
					new CButtonCancel())
			));
		}

		hostGroupWidget.addItem(hostGroupForm);

		return hostGroupWidget;
	}

}
