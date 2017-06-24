package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.ALERT_MAX_RETRIES;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_NOT_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
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
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationAuditactsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget auditWidget = new CWidget();

		// header
//		CForm configForm = new CForm("get");
//		CComboBox configComboBox = new CComboBox("config", "auditacts.action");
//		configComboBox.setAttribute("onchange", "javascript: redirect(this.options[this.selectedIndex].value);");
//		configComboBox.addItem("auditlogs.action", _("Logs"));
//		configComboBox.addItem("auditacts.action", _("Actions"));
//		configForm.addItem(configComboBox);
//		
//		auditWidget.addHeader(configForm);

		// create filter
		CForm filterForm = new CForm("get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		CButton filterButton = new CButton("filter", _("GoFilter"), "javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true);");
		filterButton.useJQueryStyle("main");
		CButton resetButton = new CButton("filter_rst", _("Reset"), "javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1); location.href = uri.getUrl();","darkgray");
		resetButton.useJQueryStyle();
		CTable filterTable = new CTable("", "filter");
		filterTable.addRow(array(
			new CDiv(array(
				bold(_("Recipient")),
				SPACE,
				new CTextBox("alias", Nest.value(data,"alias").asString(), 20),
				new CButton("btn1", _("Select"), "return PopUp(\"popup.action?dstfrm="+filterForm.getName()+
					"&dstfld1=alias&srctbl=users&srcfld1=alias&real_hosts=1\");", "filter-select-button"),
					filterButton,resetButton
			),"device_monitor")
		));
		filterForm.addItem(filterTable);

		auditWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor, "web.auditacts.filter.state", 1)).asInteger());
		auditWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor,"web.auditacts.filter.state", 1)).asInteger());

		// create form
		CForm auditForm = new CForm("get");
		auditForm.setName("auditForm");

		// create table
		CTableInfo auditTable = new CTableInfo(_("No audit entries found."));
		auditTable.setHeader(array(
			_("Time"),
			_("Type"),
			_("Status"),
			_("Retries left"),
			_("Recipient(s)"),
			_("Message"),
			_("Error")
		));
		for(Map alert : (CArray<Map>)Nest.value(data,"alerts").asCArray()) {
			Map mediatype = array_pop((CArray<Map>)Nest.value(alert,"mediatypes").asCArray());
			if (Nest.value(mediatype,"mediatypeid").asLong() == 0) {
				mediatype = map("description" , "");
			}

			CSpan status = null;
			CSpan retries = null;
			if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_SENT) {
				if (Nest.value(alert,"alerttype").asInteger() == ALERT_TYPE_MESSAGE) {
					status  = new CSpan(_("sent"), "green");
				} else {
					status = new CSpan(_("executed"), "green");
				}
				retries  = new CSpan(SPACE, "green");
			} else if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_NOT_SENT) {
				status = new CSpan(_("In progress"), "orange");
				retries = new CSpan(ALERT_MAX_RETRIES - Nest.value(alert,"retries").asInteger(), "orange");
			} else {
				status = new CSpan(_("not sent"), "red");
				retries = new CSpan(0, "red");
			}

			CArray message = (Nest.value(alert,"alerttype").asInteger() == ALERT_TYPE_MESSAGE)
				? array(
					bold(_("Subject")+NAME_DELIMITER),
					BR(),
					Nest.value(alert,"subject").$(),
					BR(),
					BR(),
					bold(_("Message")+NAME_DELIMITER),
					BR(),
					rda_nl2br(Nest.value(alert,"message").asString())
				)
				: array(
					bold(_("Command")+NAME_DELIMITER),
					BR(),
					rda_nl2br(Nest.value(alert,"message").asString())
				);

			CSpan error = empty(Nest.value(alert,"error").$()) ? new CSpan(SPACE, "off") : new CSpan(Nest.value(alert,"error").$(), "on");

			auditTable.addRow(array(
				new CCol(rda_date2str(_("d M Y H:i:s"), Nest.value(alert,"clock").asLong()), "top"),
				new CCol(Nest.value(mediatype,"description").$(), "top"),
				new CCol(status, "top"),
				new CCol(retries, "top"),
				new CCol(Nest.value(alert,"sendto").$(), "top"),
				new CCol(message, "wraptext top"),
				new CCol(error, "wraptext top")
			));
		}

		// append table to form
		auditForm.addItem(array(auditTable, Nest.value(data,"paging").$()));

		// append navigation bar js
		Map objData = map(
			"id", "timeline_1",
			"domid", "events",
			"loadSBox", 0,
			"loadImage", 0,
			"loadScroll", 1,
			"dynamic", 0,
			"mainObject", 1,
			"periodFixed", CProfile.get(idBean, executor, "web.auditacts.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		rda_add_post_js("timeControl.addObject(\"events\", "+rda_jsvalue(Nest.value(data,"timeline").asString())+", "+rda_jsvalue(objData)+");");
		rda_add_post_js("timeControl.processObjects();");

		// append form to widget
		auditWidget.addItem(auditForm);

		return auditWidget;
	}

}
