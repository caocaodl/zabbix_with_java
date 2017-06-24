package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_reverse;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_AUTOMATIC;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_MANUAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HostsUtil.ipmiAuthTypes;
import static com.isoft.iradar.inc.HostsUtil.ipmiPrivileges;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSON;
import com.isoft.iradar.tags.CJSON.Decorator;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostPrototypeEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		Map discoveryRule = Nest.value(data,"discovery_rule").asCArray();
		Map hostPrototype = Nest.value(data,"host_prototype").asCArray();
		Map parentHost = Nest.value(data,"parent_host").asCArray();

		includeSubView("js/configuration.host.edit.js");
		includeSubView("js/configuration.host.prototype.edit.js", data);

		CWidget widget = new CWidget(null, "hostprototype-edit");
		widget.addPageHeader(_("CONFIGURATION OF HOST PROTOTYPES"));
		widget.addItem(get_header_host_table(idBean, executor, "hosts", Nest.value(discoveryRule,"hostid").asLong(true), Nest.value(discoveryRule,"itemid").asLong(true)));

		CTabView divTabs = new CTabView();
		if (!isset(Nest.value(_REQUEST,"form_refresh").$())) {
			divTabs.setSelected("0");
		}

		CForm frmHost = new CForm();
		frmHost.setName("hostPrototypeForm.");
		frmHost.addVar("form", get_request("form", "1"));
		frmHost.addVar("parent_discoveryid", Nest.value(discoveryRule,"itemid").$());

		CFormList hostList = new CFormList("hostlist");

		if (!empty(Nest.value(hostPrototype,"templateid").$()) && !empty(Nest.value(data,"parents").$())) {
			CArray parents = array();
			for(Map parent : array_reverse((CArray<Map>)Nest.value(data,"parents").asCArray())) {
				parents.add(new CLink(
					Nest.value(parent,"parentHost","name").$(),
					"?form=update&hostid="+Nest.value(parent,"hostid").$()+"&parent_discoveryid="+Nest.value(parent,"discoveryRule","itemid").$(),
					"highlight underline weight_normal"
				));
				parents.add(SPACE+RARR+SPACE);
			}
			array_pop(parents);
			hostList.addRow(_("Parent discovery rules"), parents);
		}

		if (!empty(Nest.value(hostPrototype,"hostid").$())) {
			frmHost.addVar("hostid", Nest.value(hostPrototype,"hostid").$());
		}

		CTextBox hostTB = new CTextBox("host", Nest.value(hostPrototype,"host").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(hostPrototype,"templateid").asBoolean());
		hostTB.setAttribute("maxlength", 64);
		hostTB.setAttribute("autofocus", "autofocus");
		hostList.addRow(_("Host name"), hostTB);

		String name = (isset(hostPrototype,"name") && isset(hostPrototype,"name") && !Nest.value(hostPrototype,"name").asString().equals(Nest.value(hostPrototype,"host").asString())) ? Nest.value(hostPrototype,"name").asString() : "";
		CTextBox visiblenameTB = new CTextBox("name", name, RDA_TEXTBOX_STANDARD_SIZE, Nest.value(hostPrototype,"templateid").asBoolean());
		visiblenameTB.setAttribute("maxlength", 64);
		hostList.addRow(_("Visible name"), visiblenameTB);

		// display inherited parameters only for hosts prototypes on hosts
		if (Nest.value(parentHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
			CArray<Map> interfaces = array();
			CArray existingInterfaceTypes = array();
			for(Map iface : (CArray<Map>)Nest.value(parentHost,"interfaces").asCArray()) {
				Nest.value(iface,"locked").$(true);
				Nest.value(existingInterfaceTypes,Nest.value(iface,"type").$()).$(true);
				Nest.value(interfaces,Nest.value(iface,"interfaceid").$()).$(iface);
			}
			
			interfaces = CJSON.decorate(interfaces, new Decorator() {
				@Override
				protected Object doDecoratorValue(Object key, Object value) {
					if ("locked".equals(key)) {
						return value;
					}
					if (value != null) {
						return Nest.as(value).asString();
					}
					return value;
				}
			});
			rda_add_post_js("hostInterfacesManager.add("+CJs.encodeJson(interfaces)+");");
			rda_add_post_js("hostInterfacesManager.disable()");

			// table for agent interfaces with footer
			CTable ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "agentInterfaces");
			ifTab.setAttribute("data-type", "agent");

			// header
			ifTab.addRow(array(
				new CCol(SPACE, "interface-drag-control"),
				new CCol(_("IP address"), "interface-ip"),
				new CCol(_("DNS name"), "interface-dns"),
				new CCol(_("Connect to"), "interface-connect-to"),
				new CCol(_("Port"), "interface-port"),
				new CCol(_("Default"), "interface-default"),
				new CCol(SPACE, "interface-control")
			));

			CRow row = new CRow(null, null, "agentInterfacesFooter");
			if (!isset(existingInterfaceTypes,INTERFACE_TYPE_AGENT)) {
				row.addItem(new CCol(null, "interface-drag-control"));
				row.addItem(new CCol(_("No agent interfaces found."), null, 5));
			}
			ifTab.addRow(row);

			hostList.addRow(_("Agent interfaces"), new CDiv(ifTab, "border_dotted objectgroup interface-group"), false, null, "interface-row interface-row-first");

			// table for SNMP interfaces with footer
			ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "SNMPInterfaces");
			ifTab.setAttribute("data-type", "snmp");

			row = new CRow(null, null, "SNMPInterfacesFooter");
			if (!isset(existingInterfaceTypes,INTERFACE_TYPE_SNMP)) {
				row.addItem(new CCol(null, "interface-drag-control"));
				row.addItem(new CCol(_("No SNMP interfaces found."), null, 5));
			}
			ifTab.addRow(row);
			hostList.addRow(_("SNMP interfaces"), new CDiv(ifTab, "border_dotted objectgroup interface-group"), false, null, "interface-row");

			// table for JMX interfaces with footer
			ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "JMXInterfaces");
			ifTab.setAttribute("data-type", "jmx");

			row = new CRow(null, null, "JMXInterfacesFooter");
			if (!isset(existingInterfaceTypes,INTERFACE_TYPE_JMX)) {
				row.addItem(new CCol(null, "interface-drag-control"));
				row.addItem(new CCol(_("No JMX interfaces found."), null, 5));
			}
			ifTab.addRow(row);
			hostList.addRow(_("JMX interfaces"), new CDiv(ifTab, "border_dotted objectgroup interface-group"), false, null, "interface-row");

			// table for IPMI interfaces with footer
			ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "IPMIInterfaces");
			ifTab.setAttribute("data-type", "ipmi");

			row = new CRow(null, null, "IPMIInterfacesFooter");
			if (!isset(existingInterfaceTypes,INTERFACE_TYPE_IPMI)) {
				row.addItem(new CCol(null, "interface-drag-control"));
				row.addItem(new CCol(_("No IPMI interfaces found."), null, 5));
			}
			ifTab.addRow(row);
			hostList.addRow(_("IPMI interfaces"), new CDiv(ifTab, "border_dotted objectgroup interface-group"), false, null, "interface-row interface-row-last");

			// proxy
			CTextBox proxyTb = null;
			if (!empty(Nest.value(parentHost,"proxy_hostid").$())) {
				proxyTb = new CTextBox("proxy_hostid", Nest.value(data,"proxy","host").asString(), null, true);
			} else {
				proxyTb = new CTextBox("proxy_hostid", _("(no proxy)"), null, true);
			}
			hostList.addRow(_("Monitored by proxy"), proxyTb);
		}

		CComboBox cmbStatus = new CComboBox("status", Nest.value(hostPrototype,"status").$());
		cmbStatus.addItem(HOST_STATUS_MONITORED, _("Monitored"));
		cmbStatus.addItem(HOST_STATUS_NOT_MONITORED, _("Not monitored"));

		hostList.addRow(_("Status"), cmbStatus);

		divTabs.addTab("hostTab", _("Host"), hostList);

		// groups
		CFormList groupList = new CFormList("grouplist");

		// existing groups
		CArray groups = array();
		for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
			groups.add(map(
				"id", Nest.value(group,"groupid").$(),
				"name", Nest.value(group,"name").$()
			));
		}
		groupList.addRow(_("Groups"), new CMultiSelect(map(
			"name", "group_links[]",
			"objectName", "hostGroup",
			"objectOptions", map(
				"editable", true,
				"filter", map("flags", RDA_FLAG_DISCOVERY_NORMAL)
			),
			"data", groups,
			"disabled", Nest.value(hostPrototype,"templateid").asBoolean(),
			"popup", map(
				"parameters", "srctbl=host_groups&dstfrm="+frmHost.getName()+"&dstfld1=group_links_"+
					"&srcfld1=groupid&writeonly=1&multiselect=1&normal_only=1",
				"width", 450,
				"height", 450
			)
		)));

		// new group prototypes
		CTable customGroupTable = new CTable(SPACE, "formElementTable");
		customGroupTable.setAttribute("id", "tbl_group_prototypes");

		// buttons
		CButton addButton = new CButton("group_prototype_add", _("Add"), null, "link_menu");
		CCol buttonColumn = new CCol(addButton);
		buttonColumn.setAttribute("colspan", 5);

		CRow buttonRow = new CRow();
		buttonRow.setAttribute("id", "row_new_group_prototype");
		buttonRow.addItem(buttonColumn);

		customGroupTable.addRow(buttonRow);
		CDiv groupDiv = new CDiv(customGroupTable, "objectgroup border_dotted ui-corner-all group-prototypes");
		groupList.addRow(_("Group prototypes"), groupDiv);

		divTabs.addTab("groupTab", _("Groups"), groupList);

		// templates
		CFormList tmplList = new CFormList("tmpllist");

		// create linked template table
		CTable linkedTemplateTable = new CTable(_("No templates linked."), "formElementTable");
		linkedTemplateTable.attr("id", "linkedTemplateTable");
		linkedTemplateTable.attr("style", "min-width: 400px;");
		linkedTemplateTable.setHeader(array(_("Name"), _("Action")));

		CArray ignoreTemplates = array();
		if (!empty(Nest.value(hostPrototype,"templates").$())) {
			for(Map template : (CArray<Map>)Nest.value(hostPrototype,"templates").asCArray()) {
				tmplList.addVar("templates["+Nest.value(template,"templateid").$()+"]", Nest.value(template,"templateid").asString());

				linkedTemplateTable.addRow(array(
					Nest.value(template,"name").$(),
					!Nest.value(hostPrototype,"templateid").asBoolean() ? new CSubmit("unlink["+Nest.value(template,"templateid").$()+"]", _("Unlink"), null, "link_menu") : ""
				));

				Nest.value(ignoreTemplates,Nest.value(template,"templateid").$()).$(Nest.value(template,"name").$());
			}

			tmplList.addRow(_("Linked templates"), new CDiv(linkedTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		}
		// for inherited prototypes with no templates display a text message
		else if (!empty(Nest.value(hostPrototype,"templateid").$())) {
			tmplList.addRow(_("No templates linked."));
		}

		// create new linked template table
		if (!Nest.value(hostPrototype,"templateid").asBoolean()) {
			CTable newTemplateTable = new CTable(null, "formElementTable");
			newTemplateTable.attr("id", "newTemplateTable");
			newTemplateTable.attr("style", "min-width: 400px;");

			newTemplateTable.addRow(array(new CMultiSelect(map(
				"name", "add_templates[]",
				"objectName", "templates",
				"ignored", ignoreTemplates,
				"popup", map(
					"parameters", "srctbl=templates&srcfld1=hostid&srcfld2=host&dstfrm="+frmHost.getName()+
						"&dstfld1=add_templates_&templated_hosts=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			))));

			newTemplateTable.addRow(array(new CSubmit("add_template", _("Add"), null, "link_menu")));

			tmplList.addRow(_("Link new templates"), new CDiv(newTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		}

		divTabs.addTab("templateTab", _("Templates"), tmplList);

		// display inherited parameters only for hosts prototypes on hosts
		if (Nest.value(parentHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
			// IPMI
			CFormList ipmiList = new CFormList("ipmilist");

			CTextBox cmbIPMIAuthtype = new CTextBox("ipmi_authtype", ipmiAuthTypes(Nest.value(parentHost,"ipmi_authtype").asInteger()), RDA_TEXTBOX_SMALL_SIZE, true);
			ipmiList.addRow(_("Authentication algorithm"), cmbIPMIAuthtype);

			CTextBox cmbIPMIPrivilege = new CTextBox("ipmi_privilege", ipmiPrivileges(Nest.value(parentHost,"ipmi_privilege").asInteger()), RDA_TEXTBOX_SMALL_SIZE, true);
			ipmiList.addRow(_("Privilege level"), cmbIPMIPrivilege);

			ipmiList.addRow(_("Username"), new CTextBox("ipmi_username", Nest.value(parentHost,"ipmi_username").asString(), RDA_TEXTBOX_SMALL_SIZE, true));
			ipmiList.addRow(_("Password"), new CTextBox("ipmi_password", Nest.value(parentHost,"ipmi_password").asString(), RDA_TEXTBOX_SMALL_SIZE, true));
			divTabs.addTab("ipmiTab", _("IPMI"), ipmiList);

			// macros
			CView macrosView = new CView("common.macros", map(
				"macros", Nest.value(parentHost,"macros").$(),
				"readonly", true
			));
			divTabs.addTab("macroTab", _("Macros"), macrosView.render(idBean, executor));
		}

		CFormList inventoryFormList = new CFormList("inventorylist");

		// radio buttons for inventory type choice
		int inventoryMode = (isset(Nest.value(hostPrototype,"inventory","inventory_mode").$())) ? Nest.value(hostPrototype,"inventory","inventory_mode").asInteger() : HOST_INVENTORY_DISABLED;
		CRadioButton inventoryDisabledBtn = new CRadioButton("inventory_mode", HOST_INVENTORY_DISABLED, null, "host_inventory_radio_"+HOST_INVENTORY_DISABLED,
			inventoryMode == HOST_INVENTORY_DISABLED
		);
		inventoryDisabledBtn.setEnabled(!Nest.value(hostPrototype,"templateid").asBoolean());

		CRadioButton inventoryManualBtn = new CRadioButton("inventory_mode", HOST_INVENTORY_MANUAL, null, "host_inventory_radio_"+HOST_INVENTORY_MANUAL,
			inventoryMode == HOST_INVENTORY_MANUAL
		);
		inventoryManualBtn.setEnabled(!Nest.value(hostPrototype,"templateid").asBoolean());

		CRadioButton inventoryAutomaticBtn = new CRadioButton("inventory_mode", HOST_INVENTORY_AUTOMATIC, null, "host_inventory_radio_"+HOST_INVENTORY_AUTOMATIC,
			inventoryMode == HOST_INVENTORY_AUTOMATIC
		);
		inventoryAutomaticBtn.setEnabled(!Nest.value(hostPrototype,"templateid").asBoolean());

		CArray inventoryTypeRadioButton = array(
			inventoryDisabledBtn,
			new CLabel(_("Disabled"), "host_inventory_radio_"+HOST_INVENTORY_DISABLED),
			inventoryManualBtn,
			new CLabel(_("Manual"), "host_inventory_radio_"+HOST_INVENTORY_MANUAL),
			inventoryAutomaticBtn,
			new CLabel(_("Automatic"), "host_inventory_radio_"+HOST_INVENTORY_AUTOMATIC)
		);
		inventoryFormList.addRow(new CDiv(inventoryTypeRadioButton, "jqueryinputset"));

		// clearing the float
		CDiv clearFixDiv = new CDiv();
		clearFixDiv.addStyle("clear: both;");
		inventoryFormList.addRow("", clearFixDiv);

		divTabs.addTab("inventoryTab", _("Host inventory"), inventoryFormList);

		frmHost.addItem(divTabs);

		/*
		 * footer
		 */
		CArray others = array();
		if (!empty(Nest.value(hostPrototype,"hostid").$())) {
			CButtonDelete btnDelete = new CButtonDelete(_("Delete selected host prototype?"), url_param(idBean, "form")+url_param(idBean, "hostid")+url_param(idBean, "parent_discoveryid"));
			btnDelete.setEnabled(!Nest.value(hostPrototype,"templateid").asBoolean());

			others.add(new CSubmit("clone", _("Clone")));
			others.add(btnDelete);
		}
		others.add(new CButtonCancel(url_param(idBean, "parent_discoveryid")));

		frmHost.addItem(makeFormFooter(new CSubmit("save", _("Save")), others));

		widget.addItem(frmHost);

		return widget;
	}

}
