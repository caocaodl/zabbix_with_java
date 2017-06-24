package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.core.RBase.getThemes;
import static com.isoft.iradar.inc.Defines.RDA_DROPDOWN_FIRST_ALL;
import static com.isoft.iradar.inc.Defines.RDA_DROPDOWN_FIRST_NONE;
import static com.isoft.iradar.inc.Defines.SERVER_CHECK_INTERVAL;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralGuiEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.gui.js");

		CComboBox comboTheme = new CComboBox("default_theme", Nest.value(data,"config","default_theme").$(), null, getThemes());

		CComboBox comboDdFirstEntry = new CComboBox("dropdown_first_entry", Nest.value(data,"config","dropdown_first_entry").$());
		comboDdFirstEntry.addItem(RDA_DROPDOWN_FIRST_NONE, _("None"));
		comboDdFirstEntry.addItem(RDA_DROPDOWN_FIRST_ALL, _("All"));

		CFormList guiTab = new CFormList("scriptsTab");
		guiTab.addRow(_("Default theme"), array(comboTheme));
		guiTab.addRow(_("Dropdown first entry"), array(
			comboDdFirstEntry,
			new CCheckBox("dropdown_first_remember", Nest.value(data,"config","dropdown_first_remember").asBoolean(), null, 1),
			_("remember selected")
		));
		guiTab.addRow(_("Search/Filter elements limit"),
			new CNumericBox("search_limit", Nest.value(data,"config","search_limit").asString(), 6));
		guiTab.addRow(_("Max count of elements to show inside table cell"),
			new CNumericBox("max_in_table", Nest.value(data,"config","max_in_table").asString(), 5));
		guiTab.addRow(_("Enable event acknowledges"),
			new CCheckBox("event_ack_enable", Nest.value(data,"config","event_ack_enable").asBoolean(), null, 1));
		guiTab.addRow(_("Show events not older than (in days)"),
			new CTextBox("event_expire", Nest.value(data,"config","event_expire").asString(), 5));
		guiTab.addRow(_("Max count of events per trigger to show"),
			new CTextBox("event_show_max", Nest.value(data,"config","event_show_max").asString(), 5));
		guiTab.addRow(_("Show warning if iRadar server is down"),
			new CCheckBox("server_check_interval", Nest.value(data,"config","server_check_interval").asBoolean(), null, SERVER_CHECK_INTERVAL));

		CTabView guiView = new CTabView();
		guiView.addTab("gui", _("GUI"), guiTab);

		CForm guiForm = new CForm();
		guiForm.setName("guiForm");
		guiForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		guiForm.addItem(guiView);
		guiForm.addItem(makeFormFooter(new CSubmit("save", _("Save"))));

		return guiForm;
	}

}
