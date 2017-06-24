package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationCopyElements extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget triggersWidget = new CWidget();

		// append host summary to widget header
//		if (!empty(Nest.value(data,"hostid").$())) {
//			String hostTableElement = ("group_graphid".equals(Nest.value(data,"elements_field").asString())) ? "graphs" : "trigger";
//			triggersWidget.addItem(get_header_host_table(idBean, executor, hostTableElement, Nest.value(data,"hostid").asLong(true)));
//		}

//		if (!empty(Nest.value(data,"title").$())) {
//			triggersWidget.addPageHeader(Nest.value(data,"title").$());
//		}

		// create form
		CForm triggersForm = new CForm();
		triggersForm.setName("triggersForm");
		triggersForm.addVar(Nest.value(data,"elements_field").asString(), Nest.value(data,"elements").$());
		triggersForm.addVar("hostid", Nest.value(data,"hostid").$());
		triggersForm.addVar("go", "copy_to");

		// create form list
		CFormList triggersFormList = new CFormList("triggersFormList");

		// append copy types to form list
		CComboBox copyTypeComboBox = new CComboBox("copy_type", Nest.value(data,"copy_type").$(), "submit()");
		copyTypeComboBox.addItem(0, _("Hosts"));
		copyTypeComboBox.addItem(1, _("Host groups"));
		triggersFormList.addRow(_("Target type"), copyTypeComboBox);

		// append groups to form list
		if (Nest.value(data,"copy_type").asInteger() == 0) {
			int filter_groupid=Nest.value(data,"filter_groupid").asInteger();
			CComboBox groupComboBox = new CComboBox("filter_groupid", Nest.value(data,"filter_groupid").$(), "submit()");
			for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
				String name=Nest.value(group,"name").asString();
				groupComboBox.addItem(Nest.value(group,"groupid").$(), name);
				if (empty(Nest.value(data,"filter_groupid").$())) {
					Nest.value(data,"filter_groupid").$(Nest.value(group,"groupid").$());
				}
			 }
			triggersFormList.addRow(_("Group"), groupComboBox);
		}

		// append targets to form list
		CArray targets = array();
		if (Nest.value(data,"copy_type").asInteger() == 0) {
			if(empty(Nest.value(data,"hosts").asCArray())){//getCopyElementsFormData方法首次默认获取discover类型设备为空
				CHostGet hoptions = new CHostGet();
				hoptions.setOutput(new String[] { "groupid", "name" });
				hoptions.setGroupIds(new Long[] { Long.valueOf(Nest.value(data, new Object[] { "filter_groupid" }).asLong()) });
				hoptions.setTemplatedHosts(Boolean.valueOf(true));
				Object hosts = (CArray)API.Host(idBean, executor).get(hoptions);
				Nest.value(data, new Object[] { "hosts" }).$(hosts);
				FuncsUtil.order_result((CArray)hosts, "name");
			}
			for(Map host :(CArray<Map>)Nest.value(data,"hosts").asCArray()) {
				array_push(
					targets,
					array(
						new CCheckBox("copy_targetid["+host.get("hostid")+"]", uint_in_array(Nest.value(host,"hostid").$(), Nest.value(data,"copy_targetid").asCArray()), null, Nest.value(host,"hostid").asInteger()),
						SPACE,
						Nest.value(host,"name").$(),
						BR()
					)
				);
			}
		} else {
			for(Map group :(CArray<Map>)Nest.value(data,"groups").asCArray()) {
				String groupid=Nest.value(group,"groupid").asString();
				array_push(
						targets,
						array(
							new CCheckBox("copy_targetid["+groupid+"]", uint_in_array(Nest.value(group,"groupid").$(), Nest.value(data,"copy_targetid").asCArray()), null, Nest.value(group,"groupid").asInteger()),
							SPACE,
							Nest.value(group,"name").$(),
							BR()
						)
					);
			}
		 }
		if (empty(targets)) {
			array_push(targets, BR());
		}
		triggersFormList.addRow(_("Target"), targets);

		// append tabs to form
		CTabView triggersTab = new CTabView();
		triggersTab.addTab("triggersTab", count(Nest.value(data,"elements").$())+SPACE+_("elements copy to ..."), triggersFormList);
		triggersForm.addItem(triggersTab);

		// append buttons to form
		triggersForm.addItem(makeFormFooter(
			new CSubmit("copy", _("Copy")),
			new CButtonCancel(url_param(idBean, "groupid")+url_param(idBean, "hostid")+url_param(idBean, "config"))
		));

		triggersWidget.addItem(triggersForm);
		return triggersWidget;
	}

}
