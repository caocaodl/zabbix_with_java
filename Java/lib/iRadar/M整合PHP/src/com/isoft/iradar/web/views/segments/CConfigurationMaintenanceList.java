package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_EXPIRED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationMaintenanceList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();

		// create new maintenance button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create maintenance period")));
		maintenanceWidget.addPageHeader(_("CONFIGURATION OF MAINTENANCE PERIODS"), createForm);

		// header
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Group")+SPACE, ((CPageFilter)data.get("pageFilter")).getGroupsCB()));
		maintenanceWidget.addHeader(_("Maintenance periods"), filterForm);
		maintenanceWidget.addHeaderRowNumber();

		// create form
		CForm maintenanceForm = new CForm();
		maintenanceForm.setName("maintenanceForm");

		// create table
		CTableInfo maintenanceTable = new CTableInfo(_("No maintenance periods found."));
		maintenanceTable.setHeader(array(
			new CCheckBox("all_maintenances", false, "checkAll(\""+maintenanceForm.getName()+"\", \"all_maintenances\", \"maintenanceids\");"),
			make_sorting_header(_("Name"), "name"),
			make_sorting_header(_("Type"), "maintenance_type"),
			_("State"),
			_("Description")
		));

		for(Map maintenance :(CArray<Map>)Nest.value(data,"maintenances").asCArray()) {
			Integer maintenanceid = Nest.value(maintenance,"maintenanceid").asInteger();

			CSpan maintenanceStatus = null;
			switch (Nest.value(maintenance,"status").asInteger()) {
				case MAINTENANCE_STATUS_EXPIRED:
					maintenanceStatus  = new CSpan(_x("Expired", "maintenance status"), "red");
					break;
				case MAINTENANCE_STATUS_APPROACH:
					maintenanceStatus = new CSpan(_x("Approaching", "maintenance status"), "blue");
					break;
				case MAINTENANCE_STATUS_ACTIVE:
					maintenanceStatus = new CSpan(_x("Active", "maintenance status"), "green");
					break;
			}

			maintenanceTable.addRow(array(
				new CCheckBox("maintenanceids["+maintenanceid+"]", false, null, maintenanceid),
				new CLink(Nest.value(maintenance,"name").$(), "maintenance.action?form=update&maintenanceid="+maintenanceid),
				!empty(Nest.value(maintenance,"maintenance_type").$()) ? _("No data collection") : _("With data collection"),
				maintenanceStatus,
				Nest.value(maintenance,"description").$()
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected maintenance periods?"));
		goComboBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"maintenanceids\";");

		// append table to form
		maintenanceForm.addItem(array(Nest.value(data,"paging").$(), maintenanceTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		maintenanceWidget.addItem(maintenanceForm);
		return maintenanceWidget;
	}

}
