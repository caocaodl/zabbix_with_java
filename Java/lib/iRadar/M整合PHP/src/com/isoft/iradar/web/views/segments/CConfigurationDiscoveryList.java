package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_status2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_status2style;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationDiscoveryList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget discoveryWidget = new CWidget();

		// create new discovery rule button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create discovery rule")));
		discoveryWidget.addPageHeader(_("CONFIGURATION OF DISCOVERY RULES"), createForm);
		discoveryWidget.addHeader(_("Discovery rules"));
		discoveryWidget.addHeaderRowNumber();

		// create form
		CForm discoveryForm = new CForm();
		discoveryForm.setName("druleForm");

		// create table
		CTableInfo discoveryTable = new CTableInfo(_("No discovery rules found."));
		discoveryTable.setHeader(array(
			new CCheckBox("all_drules", false, "checkAll(\""+discoveryForm.getName()+"\", \"all_drules\", \"g_druleid\");"),
			make_sorting_header(_("Name"), "name"),
			_("IP range"),
			_("Delay"),
			_("Checks"),
			_("Status")
		));
		for(Map drule :(CArray<Map>)Nest.value(data,"drules").asCArray()) {
			array_push(Nest.value(drule,"description").asCArray(), new CLink(Nest.value(drule,"name").$(), "?form=update&druleid="+Nest.value(drule,"druleid").$()));

			CCol status = new CCol(new CLink(
				discovery_status2str(Nest.value(drule,"status").asInteger()),
				"?g_druleid"+SQUAREBRACKETS+"="+Nest.value(drule,"druleid").asString()+(Nest.value(drule,"status").asInteger() == DRULE_STATUS_ACTIVE ? "&go=disable" : "&go=activate"),
				discovery_status2style(Nest.value(drule,"status").asInteger())
			));

			discoveryTable.addRow(array(
				new CCheckBox("g_druleid["+Nest.value(drule,"druleid").$()+"]", false, null, Nest.value(drule,"druleid").asInteger()),
				Nest.value(drule,"description").$(),
				Nest.value(drule,"iprange").$(),
				Nest.value(drule,"delay").$(),
				!empty(Nest.value(drule,"checks").$()) ? implode(", ", Nest.array(drule,"checks").asString()) : "",
				status
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected discovery rules?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected discovery rules?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected discovery rules?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id","goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"g_druleid\";");

		// append table to form
		discoveryForm.addItem(array(Nest.value(data,"paging").$(), discoveryTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		discoveryWidget.addItem(discoveryForm);
		return discoveryWidget;
	}

}
