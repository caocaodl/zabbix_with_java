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

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
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

public class CConfigurationItemCopy extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget itemWidget = new CWidget();

		if (!empty(Nest.value(data,"hostid").$())) {
			itemWidget.addItem(get_header_host_table(idBean, executor, "items", Nest.value(data,"hostid").asLong(true)));
		}

		itemWidget.addPageHeader(_("CONFIGURATION OF ITEMS"));

		// create form
		CForm itemForm = new CForm();
		itemForm.setName("itemForm");
		itemForm.addVar("group_itemid", Nest.value(data,"group_itemid").$());
		itemForm.addVar("hostid", Nest.value(data,"hostid").$());
		itemForm.addVar("go", "copy_to");

		// create form list
		CFormList itemFormList = new CFormList("itemFormList");

		// append type to form list
		CComboBox copyTypeComboBox = new CComboBox("copy_type", Nest.value(data,"copy_type").$(), "submit()");
		copyTypeComboBox.addItem(0, _("Hosts"));
		copyTypeComboBox.addItem(1, _("Host groups"));
		itemFormList.addRow(_("Target type"), copyTypeComboBox);

		// append targets to form list
		CArray targetList = array();
		if (Nest.value(data,"copy_type").asInteger() == 0) {
			CComboBox groupComboBox = new CComboBox("copy_groupid", Nest.value(data,"copy_groupid").$(), "submit()");
			for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
				groupComboBox.addItem(Nest.value(group,"groupid").$(),Nest.value(group,"name").asString());
			}
			itemFormList.addRow(_("Group"), groupComboBox);

			for(Map host : (CArray<Map>)Nest.value(data,"hosts").asCArray()) {
				array_push(targetList, array(
					new CCheckBox("copy_targetid["+Nest.value(host,"hostid").asString()+"]", uint_in_array(Nest.value(host,"hostid").asString(), Nest.value(data,"copy_targetid").asCArray()), null, Nest.value(host,"hostid").asInteger()),
					SPACE,
					Nest.value(host,"name").$(),
					BR()
				));
			}
		} else {
			for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
				array_push(targetList, array(
					new CCheckBox("copy_targetid["+Nest.value(group,"groupid").asString()+"]", uint_in_array(Nest.value(group,"groupid").asString(), Nest.value(data,"copy_targetid").asCArray()), null, Nest.value(group,"groupid").asInteger()),
					SPACE,
					Nest.value(group,"name").$(),
					BR()
				));
			}
		}
		itemFormList.addRow(_("Target"), !empty(targetList) ? targetList : SPACE);

		// append tabs to form
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", count(Nest.value(data,"group_itemid").$())+" "+_("elements copy to ..."), itemFormList);
		itemForm.addItem(itemTab);

		// append buttons to form
		itemForm.addItem(makeFormFooter(new CSubmit("copy", _("Copy")), new CButtonCancel(url_param(idBean, "groupid")+url_param(idBean, "hostid")+url_param(idBean, "config"))));
		itemWidget.addItem(itemForm);
		return itemWidget;
	}

}
