package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButtonList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationDiscoveryEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		RadarContext.getContext().getRequest().setAttribute("data", data);
		includeSubView("js/configuration.discovery.edit.js");

		CWidget discoveryWidget = new CWidget();
		discoveryWidget.addPageHeader(_("CONFIGURATION OF DISCOVERY RULES"));

		// create form
		CForm discoveryForm = new CForm();
		discoveryForm.setName("discoveryForm");
		discoveryForm.addVar("form", Nest.value(data,"form").$());
		discoveryForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		if (!empty(Nest.value(data,"druleid").$())) {
			discoveryForm.addVar("druleid", Nest.value(data,"druleid").$());
		}

		// create form list
		CFormList discoveryFormList = new CFormList("discoveryFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"drule","name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		discoveryFormList.addRow(_("Name"), nameTextBox);

		// append proxy to form list
		CComboBox proxyComboBox = new CComboBox("proxy_hostid", Nest.value(data,"drule","proxy_hostid").$());
		proxyComboBox.addItem(0, _("No proxy"));
		for(Map proxy:(CArray<Map>)Nest.value(data,"proxies").asCArray()) {
			proxyComboBox.addItem(Nest.value(proxy,"proxyid").$(), Nest.value(proxy,"host").asString());
		}
		discoveryFormList.addRow(_("Discovery by proxy"), proxyComboBox);
		discoveryFormList.addRow(_("IP range"), new CTextBox("iprange", Nest.value(data,"drule","iprange").asString(), RDA_TEXTBOX_SMALL_SIZE));
		discoveryFormList.addRow(_("Delay (in sec)"), new CNumericBox("delay", Nest.value(data,"drule","delay").asString(), 8));

		// append checks to form list
		CTable checkTable = new CTable(null, "formElementTable");
		checkTable.addRow(new CRow(
			new CCol(
				new CButton("newCheck", _("New"), null, "link_menu"),
				null,
				2
			),
			null,
			"dcheckListFooter"
		));
		discoveryFormList.addRow(_("Checks"),
			new CDiv(checkTable, "objectgroup inlineblock border_dotted ui-corner-all", "dcheckList"));

		// append uniqueness criteria to form list
		CRadioButtonList uniquenessCriteriaRadio = new CRadioButtonList("uniqueness_criteria", Nest.value(data,"drule","uniqueness_criteria").asString());
		uniquenessCriteriaRadio.addValue(SPACE+_("IP address"), "-1", true, rda_formatDomId("uniqueness_criteria_ip"));
		discoveryFormList.addRow(_("Device uniqueness criteria"),
			new CDiv(uniquenessCriteriaRadio, "objectgroup inlineblock border_dotted ui-corner-all", "uniqList"));

		// append status to form list
		boolean status = (empty(Nest.value(data,"druleid").$()) && empty(Nest.value(data,"form_refresh").$()))
			? true
			: (Nest.value(data,"drule","status").asInteger() == DRULE_STATUS_ACTIVE);

		discoveryFormList.addRow(_("Enabled"), new CCheckBox("status", status, null, 1));

		// append tabs to form
		CTabView discoveryTabs = new CTabView();
		discoveryTabs.addTab("druleTab", _("Discovery rule"), discoveryFormList);
		discoveryForm.addItem(discoveryTabs);

		// append buttons to form
		CButtonDelete deleteButton = new CButtonDelete(_("Delete discovery rule?"), url_param(idBean, "form")+url_param(idBean, "druleid"));
		if (empty(Nest.value(data,"druleid").$())) {
			deleteButton.setAttribute("disabled", "disabled");
		}
		discoveryForm.addItem(makeFormFooter(
			new CSubmit("save", _("Save")),
			array(
				new CSubmit("clone", _("Clone")),
				deleteButton,
				new CButtonCancel()
			)
		));

		discoveryWidget.addItem(discoveryForm);

		return discoveryWidget;
	}

}
