package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralOtherEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CFormList otherTab = new CFormList("scriptsTab");

		CComboBox discoveryGroup = new CComboBox("discovery_groupid", Nest.value(data,"config","discovery_groupid").$());
		discoveryGroup.addStyle("display:none");
		for(Map group : (CArray<Map>)Nest.value(data,"discovery_groups").asCArray()) {
			discoveryGroup.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}

		CComboBox alertUserGroup = new CComboBox("alert_usrgrpid", Nest.value(data,"config","alert_usrgrpid").$());
		alertUserGroup.addItem(0, _("None"));
		for(Map usrgrp : (CArray<Map>)Nest.value(data,"alert_usrgrps").asCArray()) {
			alertUserGroup.addItem(Nest.value(usrgrp,"usrgrpid").$(), Nest.value(usrgrp,"name").asString());
		}
		CNumericBox cn=new CNumericBox("refresh_unsupported", Nest.value(data,"config","refresh_unsupported").asString(), 5);
		cn.addStyle("display:none");
		//_("Refresh unsupported items (in sec)")  第一个row的描述
		//_("Group for discovered hosts")  第二个row的描述
		otherTab.addRow("", cn);
		otherTab.addRow("", discoveryGroup);
		otherTab.addRow(_("User group for database down message"), alertUserGroup);
		otherTab.addRow(_("Log unmatched SNMP traps"), new CCheckBox("snmptrap_logging", Nest.value(data,"config","snmptrap_logging").asBoolean(), null, 1));

		CTabView otherView = new CTabView();
		otherView.addTab("other", _("Other parameters"), otherTab);

		CForm otherForm = new CForm();
		otherForm.setName("otherForm");
		otherForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		otherForm.addItem(otherView);
		otherForm.addItem(makeFormFooter(new CSubmit("save", _("Save"))));

		return otherForm;
	}

}
