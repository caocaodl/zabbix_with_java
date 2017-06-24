package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.natsort;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CInventoryHostList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget();

		CForm rForm = new CForm("get");
		rForm.addItem(array(_("Group"), SPACE, ((CPageFilter)data.get("pageFilter")).getGroupsCB()));
		hostInventoryWidget.addPageHeader(_("HOST INVENTORY"), SPACE);
		hostInventoryWidget.addHeader(_("Hosts"), rForm);

		CTable filterTable = new CTable("", "filter");
		// getting inventory fields to make a drop down
		CArray<Map> inventoryFields = getHostInventories(true); // "true" means list should be ordered by title
		CComboBox inventoryFieldsComboBox = new CComboBox("filter_field", Nest.value(data,"filterField").$());
		for(Map inventoryField : inventoryFields) {
			inventoryFieldsComboBox.addItem(
				Nest.value(inventoryField,"db_field").$(),
				Nest.value(inventoryField,"title").asString()
			);
		}
		CComboBox exactComboBox = new CComboBox("filter_exact", Nest.value(data,"filterExact").$());
		exactComboBox.addItem("0", _("like"));
		exactComboBox.addItem("1", _("exactly"));
		filterTable.addRow(array(
			array(
				array(bold(_("Field")), SPACE, inventoryFieldsComboBox),
				array(
					exactComboBox,
					new CTextBox("filter_field_value", Nest.value(data,"filterFieldValue").asString(), 20)
				)
			)
		), "host-inventories");

		CButton filter = new CButton("filter", _("GoFilter"),
			"javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true); chkbxRange.clearSelectedOnFilterChange();"
		);
		filter.useJQueryStyle("main");

		CButton reset = new CButton("reset", _("Reset"), "javascript: clearAllForm(\"rda_filter\");");
		reset.useJQueryStyle();

		CDiv divButtons = new CDiv(array(filter, SPACE, reset));
		divButtons.setAttribute("style", "padding: 4px 0px;");

		CCol footerCol = new CCol(divButtons, "controls");

		filterTable.addRow(footerCol);

		CForm filterForm = new CForm("get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addItem(filterTable);
		hostInventoryWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor, "web.hostinventories.filter.state", 0)).asInteger());
		hostInventoryWidget.addHeaderRowNumber();

		CTableInfo table = new CTableInfo(_("No hosts found."));
		table.setHeader(array(
			make_sorting_header(_("Host"), "name"),
			_("Group"),
			make_sorting_header(_("Name"), "pr_name"),
			make_sorting_header(_("Type"), "pr_type"),
			make_sorting_header(_("OS"), "pr_os"),
			make_sorting_header(_("Serial number A"), "pr_serialno_a"),
			make_sorting_header(_("Tag"), "pr_tag"),
			make_sorting_header(_("MAC address A"), "pr_macaddress_a"))
		);

		for(Map host : (CArray<Map>)Nest.value(data,"hosts").asCArray()) {
			CArray chostGroups = array();
			for(Map group : (CArray<Map>)Nest.value(host,"groups").asCArray()) {
				chostGroups.add(Nest.value(group,"name").$());
			}
			natsort(chostGroups);
			String hostGroups = implode(", ", chostGroups.valuesAsString());

			CArray row = array(
				new CLink(
					Nest.value(host,"name").$(),
					"?hostid="+Nest.value(host,"hostid").asString()+url_param(idBean, "groupid"),
					(Nest.value(host,"status").asInteger() == HOST_STATUS_NOT_MONITORED) ? "not-monitored" : ""
				),
				hostGroups,
				rda_str2links(Nest.value(host,"inventory","name").asString()),
				rda_str2links(Nest.value(host,"inventory","type").asString()),
				rda_str2links(Nest.value(host,"inventory","os").asString()),
				rda_str2links(Nest.value(host,"inventory","serialno_a").asString()),
				rda_str2links(Nest.value(host,"inventory","tag").asString()),
				rda_str2links(Nest.value(host,"inventory","macaddress_a").asString())
			);

			table.addRow(row);
		}

		CArray ctable = array(Nest.value(data,"paging").$(), table, Nest.value(data,"paging").$());
		hostInventoryWidget.addItem(ctable);

		return hostInventoryWidget;
	}

}
