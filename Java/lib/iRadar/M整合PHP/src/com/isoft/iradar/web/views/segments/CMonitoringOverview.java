package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_LEFT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_COUNT;
import static com.isoft.iradar.inc.FuncsUtil.convertUnitsS;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.ItemsUtil.getItemsDataOverview;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.iradar.inc.TriggersUtil.getTriggersOverview;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CHelp;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CMonitoringOverview extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		int SHOW_TRIGGERS = Nest.as(define("SHOW_TRIGGERS")).asInteger();
		int SHOW_DATA = Nest.as(define("SHOW_DATA")).asInteger();
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		rda_add_post_js("jqBlink.blink();");

		CWidget overviewWidget = new CWidget();

		CComboBox typeComboBox = new CComboBox("type", Nest.value(data,"type").$(), "submit()");
		typeComboBox.addItem(SHOW_TRIGGERS, _("Triggers"));
		typeComboBox.addItem(SHOW_DATA, _("Data"));

		CForm headerForm = new CForm("get");
		headerForm.addItem(array(_("Group"), SPACE, pageFilter.getGroupsCB()));
		headerForm.addItem(array(SPACE, _("Application"), SPACE, pageFilter.getApplicationsCB()));
		headerForm.addItem(array(SPACE, _("Type"), SPACE, typeComboBox));

		overviewWidget.addHeader(_("Overview"), headerForm);

		CTableInfo hintTable = new CTableInfo();
		hintTable.setAttribute("style", "width: 200px");

		if (Nest.value(data,"type").asInteger() == SHOW_TRIGGERS) {
			hintTable.addRow(array(new CCol(SPACE, "normal"), _("OK")));
		}

		for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
			hintTable.addRow(array(getSeverityCell(idBean, executor, i), _("PROBLEM")));
		}

		Map<String, Object> config = select_config(idBean, executor);

		if (Nest.value(data,"type").asInteger() == SHOW_TRIGGERS) {
			// blinking preview in help popup (only if blinking is enabled)
			if (Nest.value(config,"blink_period").asLong() > 0) {
				CRow row = new CRow(null);
				row.addItem(new CCol(SPACE, "normal"));
				for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
					row.addItem(new CCol(SPACE, getSeverityStyle(i)));
				}
				CTable table = new CTable("", "blink overview-mon-severities");
				table.addRow(row);

				// double div necassary for FireFox
				CCol col = new CCol(new CDiv(new CDiv(table), "overview-mon-severities-container"));

				hintTable.addRow(array(col, _s("Age less than %s", convertUnitsS(Nest.value(config,"blink_period").asDouble()))));
			}

			hintTable.addRow(array(new CCol(SPACE), _("No trigger")));
		} else {
			hintTable.addRow(array(new CCol(SPACE), _("OK or no trigger")));
		}

		CHelp help = new CHelp("web.view.action", "right");
		help.setHint(hintTable, "", "", true, false);

		// header right
		overviewWidget.addPageHeader(_("OVERVIEW"), array(
			get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$())),
			SPACE,
			help
		));

		// header left
		CComboBox styleComboBox = new CComboBox("view_style", Nest.value(data,"view_style").$(), "submit()");
		styleComboBox.addItem(STYLE_TOP, _("Top"));
		styleComboBox.addItem(STYLE_LEFT, _("Left"));

		CForm hostLocationForm = new CForm("get");
		hostLocationForm.addVar("groupid", Nest.value(data,"groupid").$());
		hostLocationForm.addItem(array(_("Hosts location"), SPACE, styleComboBox));

		overviewWidget.addHeader(hostLocationForm);

		CTableInfo dataTable = null;
		if (!empty(Nest.value(config,"dropdown_first_entry").$())
				|| pageFilter.$("applicationsSelected").asBoolean()
				|| pageFilter.$("groupsSelected").asBoolean()) {
			if (Nest.value(data,"type").asInteger() == SHOW_DATA) {
				dataTable  = getItemsDataOverview(idBean, executor,
					array_keys(pageFilter.$("hosts").asCArray()).valuesAsLong(),
					pageFilter.$("application").asString(),
					Nest.value(data,"view_style").asInteger()
				);
			} else if (Nest.value(data,"type").asInteger() == SHOW_TRIGGERS) {
				dataTable = getTriggersOverview(idBean, executor,
					array_keys(pageFilter.$("hosts").asCArray()),
					pageFilter.$("application").asString(),
					Nest.value(RadarContext.page(),"file").asString(),
					Nest.value(data,"view_style").asInteger()
				);
			}
		} else {
			dataTable = new CTableInfo(_("No items found."));
		}

		overviewWidget.addItem(dataTable);

		return overviewWidget;
	}

}
