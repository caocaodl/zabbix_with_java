package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CInventoryHostView extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget(null, "inventory-host");

		hostInventoryWidget.addPageHeader(_("HOST INVENTORY"), SPACE);

		CForm hostInventoriesForm = new CForm();

		/* Overview tab */
		CFormList overviewFormList = new CFormList();

		CSpan hostSpan = new CSpan(Nest.value(data,"host","host").$(), "link_menu menu-host");

		hostSpan.setMenuPopup(getMenuPopupHost(
			Nest.value(data,"host").asCArray(),
			Nest.value(data,"hostScripts",Nest.value(data,"host","hostid").$()).asCArray(),
			false
		));

		Object hostName = Nest.value(data,"host","maintenance_status").asInteger() == HOST_MAINTENANCE_STATUS_ON
			? array(hostSpan, SPACE, new CDiv(null, "icon-maintenance-inline"))
			: hostSpan;

		overviewFormList.addRow(_("Host name"), hostName);

		if (!Nest.value(data,"host","host").$().equals(Nest.value(data,"host","name").$())) {
			overviewFormList.addRow(_("Visible name"), new CSpan(Nest.value(data,"host","name").$(), "text-field"));
		}

		CArray<CRow> agentInterfaceRows = array();
		CArray<CRow> snmpInterfaceRows = array();
		CArray<CRow> ipmiInterfaceRows = array();
		CArray<CRow> jmxInterfaceRows = array();

		for(Map iface : (CArray<Map>)Nest.value(data,"host","interfaces").asCArray()) {
			String spanClass = Nest.value(iface,"main").asBoolean() ? " default_interface" : null;

			switch (Nest.value(iface,"type").asInteger()) {
				case INTERFACE_TYPE_AGENT:
					agentInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_SNMP:
					snmpInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_IPMI:
					ipmiInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_JMX:
					jmxInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;
			}
		}

		boolean interfaceTableHeaderSet = false;

		// Agent interface
		if (!empty(agentInterfaceRows)) {
			CTable agentInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup element-row-first interfaces");
			agentInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
			interfaceTableHeaderSet = true;

			for(CRow iface : agentInterfaceRows) {
				agentInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("Agent interfaces"),
				new CDiv(agentInterfacesTable)
			);
		}


		// SNMP interface
		if (!empty(snmpInterfaceRows)) {
			CTable snmpInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				snmpInterfacesTable.addClass("element-row");
			} else {
				snmpInterfacesTable.addClass("element-row-first");
				snmpInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
				interfaceTableHeaderSet = true;
			}

			for(CRow iface : snmpInterfaceRows) {
				snmpInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("SNMP interfaces"),
				new CDiv(snmpInterfacesTable)
			);
		}

		// JMX interface
		if (!empty(jmxInterfaceRows)) {
			CTable jmxInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				jmxInterfacesTable.addClass("element-row");
			} else {
				jmxInterfacesTable.addClass("element-row-first");
				jmxInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
			}

			for(CRow iface : jmxInterfaceRows) {
				jmxInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("JMX interfaces"),
				new CDiv(jmxInterfacesTable)
			);
		}

		// IPMI interface
		if (!empty(ipmiInterfaceRows)) {
			CTable ipmiInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				ipmiInterfacesTable.addClass("element-row");
			} else {
				ipmiInterfacesTable.addClass("element-row-first");
				ipmiInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
				interfaceTableHeaderSet = true;
			}

			for(CRow iface : ipmiInterfaceRows) {
				ipmiInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("IPMI interfaces"),
				new CDiv(ipmiInterfacesTable)
			);
		}

		// inventory (OS, Hardware, Software)
		if (!empty(Nest.value(data,"host","inventory").$())) {
			if (!empty(Nest.value(data,"host","inventory","os").$())) {
				overviewFormList.addRow(
					Nest.value(data,"tableTitles","os","title").$(),
					new CSpan(rda_str2links(Nest.value(data,"host","inventory","os").asString()), "text-field")
				);
			}
			if (!empty(Nest.value(data,"host","inventory","hardware").$())) {
				overviewFormList.addRow(
					Nest.value(data,"tableTitles","hardware","title").$(),
					new CSpan(rda_str2links(Nest.value(data,"host","inventory","hardware").asString()), "text-field")
				);
			}
			if (!empty(Nest.value(data,"host","inventory","software").$())) {
				overviewFormList.addRow(
					Nest.value(data,"tableTitles","software","title").$(),
					new CSpan(rda_str2links(Nest.value(data,"host","inventory","software").asString()), "text-field")
				);
			}
		}

		// latest data
		CArray latestArray = array(
			new CLink(_("Web"), "httpmon.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid")),
			new CLink(_("Latest data"), "latest.action?form=1&select=&show_details=1&filter_set=Filter&hostid="+
				Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"), "overview-link"),
			new CLink(_("Triggers"),
				"tr_status.action?show_triggers=2&ack_status=1&show_events=1&show_events=0&show_details=1"+
				"&txt_select=&show_maintenance=1&hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"), "overview-link"),
			new CLink(_("Events"),
				"events.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid")+"&source="+EVENT_SOURCE_TRIGGERS,
				"overview-link"
			),
			new CLink(_("Graphs"), "charts.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"), "overview-link"),
			new CLink(_("Screens"), "host_screen.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"),
				"overview-link")
		);

		overviewFormList.addRow(_("Latest data"), latestArray);

		// configuration
		Object hostLink = null;
		Object applicationsLink = null;
		Object itemsLink = null;
		Object triggersLink = null;
		Object graphsLink = null;
		Object discoveryLink = null;
		Object webLink = null;
		if (!empty(Nest.value(data,"rwHost").$())) {
			hostLink  = new CLink(_("Host"),
				"hosts.action?form=update&hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			applicationsLink = new CLink(_("Applications"),
				"applications.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			itemsLink = new CLink(_("Items"), "items.action?filter_set=1&hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			triggersLink = new CLink(_("Triggers"), "triggers.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			graphsLink = new CLink(_("Graphs"), "graphs.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			discoveryLink = new CLink(_("Discovery"),
				"host_discovery.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
			webLink = new CLink(_("Web"), "httpconf.action?hostid="+Nest.value(data,"host","hostid").$()+url_param(idBean, "groupid"));
		} else {
			hostLink = _("Host");
			applicationsLink = _("Application");
			itemsLink = _("Items");
			triggersLink = _("Triggers");
			graphsLink = _("Graphs");
			discoveryLink = _("Discovery");
			webLink = _("Web");
		}

		CArray configurationArray = array(
			hostLink,
			new CSpan (array(applicationsLink, SPACE, "("+Nest.value(data,"host","applications").$()+")"), "overview-link"),
			new CSpan (array(itemsLink, SPACE, "("+Nest.value(data,"host","items").$()+")"), "overview-link"),
			new CSpan (array(triggersLink, SPACE, "("+Nest.value(data,"host","triggers").$()+")"), "overview-link"),
			new CSpan (array(graphsLink, SPACE, "("+Nest.value(data,"host","graphs").$()+")"), "overview-link"),
			new CSpan (array(discoveryLink, SPACE, "("+Nest.value(data,"host","discoveries").$()+")"), "overview-link"),
			new CSpan (array(webLink, SPACE, "("+Nest.value(data,"host","httpTests").$()+")"), "overview-link")
		);

		overviewFormList.addRow(_("Configuration"), configurationArray);

		CTabView hostInventoriesTab = new CTabView(map("remember", true));
		hostInventoriesTab.setSelected("0");

		hostInventoriesTab.addTab("overviewTab", _("Overview"), overviewFormList);

		/*
		 * Details tab
		 */
		CFormList detailsFormList = new CFormList();

		boolean inventoryValues = false;
		if (!empty(Nest.value(data,"host","inventory").$())) {
			for (Entry<Object,String> e : ((CArray<String>)Nest.value(data,"host","inventory").asCArray()).entrySet()) {
			    Object key = e.getKey();
			    String value = e.getValue();
				if (!rda_empty(value)) {
					detailsFormList.addRow(
						Nest.value(data,"tableTitles",key,"title").$(),
						new CSpan(rda_str2links(value),
						"text-field"));
					inventoryValues = true;
				}
			}
		}

		if (!inventoryValues) {
			hostInventoriesTab.setDisabled(array(1));
		}

		hostInventoriesTab.addTab("detailsTab", _("Details"), detailsFormList);

		// append tabs and form
		hostInventoriesForm.addItem(hostInventoriesTab);
		hostInventoriesForm.addItem(makeFormFooter(
			null,
			new CButtonCancel(url_param(idBean, "groupid"))
		));
		hostInventoryWidget.addItem(hostInventoriesForm);

		return hostInventoryWidget;
	}

}
