package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_BOTH;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_UNACK;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringDashconf extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget dashconfWidget = new CWidget();
		dashconfWidget.setClass("header");
		dashconfWidget.addPageHeader(_("DASHBOARD CONFIGURATION"));

		// create form
		CForm dashconfForm = new CForm();
		dashconfForm.setName("dashconf");
		dashconfForm.setAttribute("id", "dashform");
		dashconfForm.addVar("filterEnable", Nest.value(data,"isFilterEnable").$());

		// create form list
		CFormList dashconfFormList = new CFormList("dashconfFormList");

		// append filter status to form list
		CSpan filterStatusSpan = null;
		if (Nest.value(data,"isFilterEnable").asBoolean()) {
			filterStatusSpan  = new CSpan(_("Enabled"), "green underline pointer");
			filterStatusSpan.setAttribute("onclick", "create_var(\""+dashconfForm.getName()+"\", \"filterEnable\", 0, true);");
		} else {
			filterStatusSpan = new CSpan(_("Disabled"), "red underline pointer");
			filterStatusSpan.setAttribute("onclick", "$(\"dashform\").enable(); create_var(\""+dashconfForm.getName()+"\", \"filterEnable\", 1, true);");
		}
		dashconfFormList.addRow(_("Dashboard filter"), filterStatusSpan);

		// append host groups to form list
		CComboBox hostGroupsComboBox = new CComboBox("grpswitch", Nest.value(data,"grpswitch").$(), "submit();");
		hostGroupsComboBox.addItem(0, _("All"));
		hostGroupsComboBox.addItem(1, _("Selected"));
		if (!Nest.value(data,"isFilterEnable").asBoolean()) {
			hostGroupsComboBox.setAttribute("disabled", "disabled");
		}
		dashconfFormList.addRow(_("Host groups"), hostGroupsComboBox);

		if (Nest.value(data,"grpswitch").asBoolean()) {
			dashconfFormList.addRow(_("Show selected groups"), new CMultiSelect(map(
				"name", "groupids[]",
				"objectName", "hostGroup",
				"data", Nest.value(data,"groups").$(),
				"disabled", !Nest.value(data,"isFilterEnable").asBoolean(),
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+dashconfForm.getName()+"&dstfld1=groupids_"+
						"&srcfld1=groupid&multiselect=1",
					"width", 450,
					"height", 450
				)
			)));
			dashconfFormList.addRow(_("Hide selected groups"), new CMultiSelect(map(
				"name", "hidegroupids[]",
				"objectName", "hostGroup",
				"data", Nest.value(data,"hideGroups").$(),
				"disabled", !Nest.value(data,"isFilterEnable").asBoolean(),
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+dashconfForm.getName()+"&dstfld1=hidegroupids_"+
						"&srcfld1=groupid&multiselect=1",
					"width", 450,
					"height", 450
				)
			)));
		}

		// append host in maintenance checkbox to form list
		CCheckBox maintenanceCheckBox = new CCheckBox("maintenance", Nest.value(data,"maintenance").asBoolean(), null, "1");
		if (!Nest.value(data,"isFilterEnable").asBoolean()) {
			maintenanceCheckBox.setAttribute("disabled", "disabled");
		}
		dashconfFormList.addRow(_("Hosts"), array(maintenanceCheckBox, _("Show hosts in maintenance")));

		// append trigger severities to form list
		CArray severities = array();
		for(Integer severity : (CArray<Integer>)Nest.value(data,"severities").asCArray()) {
			CCheckBox serverityCheckBox = new CCheckBox("trgSeverity["+severity+"]", isset(Nest.value(data,"severity",severity).$()), "", 1);
			serverityCheckBox.setEnabled(Nest.value(data,"isFilterEnable").asBoolean());
			severities.add(array(serverityCheckBox, getSeverityCaption(idBean, executor, severity)));
			severities.add(BR());
		}
		array_pop(severities);

		dashconfFormList.addRow(_("Triggers with severity"), severities);

		// append problem display to form list
		CComboBox extAckComboBox = new CComboBox("extAck", Nest.value(data,"extAck").$());
		extAckComboBox.addItems((CArray)map(
			EXTACK_OPTION_ALL, _("All"),
			EXTACK_OPTION_BOTH , _("Separated"),
			EXTACK_OPTION_UNACK, _("Unacknowledged only")
		));
		extAckComboBox.setEnabled(Nest.value(data,"isFilterEnable").asBoolean() && Nest.value(data,"config","event_ack_enable").asBoolean());
		if (!Nest.value(data,"config","event_ack_enable").asBoolean()) {
			extAckComboBox.setAttribute("title", _("Event acknowledging disabled"));
		}
		dashconfFormList.addRow(_("Problem display"), extAckComboBox);

		// create tab
		CTabView dashconfTab = new CTabView();
		dashconfTab.addTab("dashconfTab", _("Filter"), dashconfFormList);

		dashconfForm.addItem(dashconfTab);
		dashconfForm.addItem(makeFormFooter(new CSubmit("save", _("Save"))));

		dashconfWidget.addItem(dashconfForm);

		return dashconfWidget;
	}

}
