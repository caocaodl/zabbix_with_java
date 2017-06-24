package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.inc.AuditUtil.audit_resource2str;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGOUT;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.operator.COperator.CMapOperator.add;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationAuditlogsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget auditWidget = new CWidget();

		// header
		CForm configForm = new CForm("get");
		CComboBox configComboBox = new CComboBox("config", "auditlogs.action");
		configComboBox.setAttribute("onchange", "javascript: redirect(this.options[this.selectedIndex].value);");
		configComboBox.addItem("auditlogs.action", _("Logs"));
		configComboBox.addItem("auditacts.action", _("Actions"));
		configForm.addItem(configComboBox);
		auditWidget.addPageHeader(_("AUDIT LOGS"), configForm);
		auditWidget.addHeader(_("Logs"));
		auditWidget.addHeaderRowNumber();

		// create filter
		CForm filterForm = new CForm("get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		CTable filterTable = new CTable("", "filter");

		CComboBox actionComboBox = new CComboBox("action", Nest.value(data,"action").$());
		actionComboBox.addItem(-1, _("All"));
		actionComboBox.addItem(AUDIT_ACTION_LOGIN, _("Login"));
		actionComboBox.addItem(AUDIT_ACTION_LOGOUT, _("Logout"));
		actionComboBox.addItem(AUDIT_ACTION_ADD, _("Add"));
		actionComboBox.addItem(AUDIT_ACTION_UPDATE, _("Update"));
		actionComboBox.addItem(AUDIT_ACTION_DELETE, _("Delete"));
		actionComboBox.addItem(AUDIT_ACTION_ENABLE, _("Enable"));
		actionComboBox.addItem(AUDIT_ACTION_DISABLE, _("Disable"));

		CComboBox resourceComboBox = new CComboBox("resourcetype", Nest.value(data,"resourcetype").$());
		resourceComboBox.addItems((CArray)add((CArray)map(-1, _("All")),  audit_resource2str()));

		filterTable.addRow(array(
			array(
				bold(_("User")),
				SPACE,
				new CTextBox("alias", Nest.value(data,"alias").asString(), 20),
				new CButton("btn1", _("Select"), "return PopUp(\"popup.action?dstfrm="+filterForm.getName()+
					"&dstfld1=alias&srctbl=users&srcfld1=alias&real_hosts=1\");", "filter-select-button")
			),
			array(bold(_("Action")), SPACE, actionComboBox),
			array(bold(_("Resource")), SPACE, resourceComboBox)
		));
		CButton filterButton = new CButton("filter", _("GoFilter"), "javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true);");
		filterButton.useJQueryStyle("main");
		CButton resetButton = new CButton("filter_rst", _("Reset"), "javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1); location.href = uri.getUrl();");
		resetButton.useJQueryStyle();
		CDiv buttonsDiv = new CDiv(array(filterButton, SPACE, resetButton));
		buttonsDiv.setAttribute("style", "padding: 4px 0;");

		filterTable.addRow(new CCol(buttonsDiv, "controls", 3));
		filterForm.addItem(filterTable);

		auditWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor,"web.auditlogs.filter.state", 1)).asInteger());
		auditWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor,"web.auditlogs.filter.state", 1)).asInteger());

		// create form
		CForm auditForm = new CForm("get");
		auditForm.setName("auditForm");

		// create table
		CTableInfo auditTable = new CTableInfo(_("No audit entries found."));
		auditTable.setHeader(array(
			_("Time"),
			_("User"),
			_("IP"),
			_("Resource"),
			_("Action"),
			_("ID"),
			_("Description"),
			_("Details")
		));
		for(Map action : (CArray<Map>)Nest.value(data,"actions").asCArray()) {
			Object details = array();
			if (isArray(Nest.value(action,"details").$())) {
				for(Map detail : (CArray<Map>)Nest.value(action,"details").asCArray()) {
					((CArray)details).add(array(Nest.value(detail,"table_name").$()+"."+Nest.value(detail,"field_name").$()+NAME_DELIMITER+Nest.value(detail,"oldvalue").$()+" => "+Nest.value(detail,"newvalue").$(), BR()));
				}
			} else {
				details = Nest.value(action,"details").$();
			}

			auditTable.addRow(array(
				rda_date2str(_("d M Y H:i:s"), Nest.value(action,"clock").asLong()),
				Nest.value(action,"alias").$(),
				Nest.value(action,"ip").$(),
				Nest.value(action,"resourcetype").$(),
				Nest.value(action,"action").$(),
				Nest.value(action,"resourceid").$(),
				Nest.value(action,"resourcename").$(),
				new CCol(details, "wraptext")
			));
		}

		// append table to form
		auditForm.addItem(array(Nest.value(data,"paging").$(), auditTable, Nest.value(data,"paging").$()));

		// append navigation bar js
		CArray objData = map(
			"id", "timeline_1",
			"domid", "events",
			"loadSBox", 0,
			"loadImage", 0,
			"loadScroll", 1,
			"dynamic", 0,
			"mainObject", 1,
			"periodFixed", CProfile.get(idBean, executor, "web.auditlogs.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		rda_add_post_js("timeControl.addObject(\"events\", "+rda_jsvalue(Nest.value(data,"timeline").$())+", "+rda_jsvalue(objData)+");");
		rda_add_post_js("timeControl.processObjects();");

		// append form to widget
		auditWidget.addItem(auditForm);

		return auditWidget;
	}

}
