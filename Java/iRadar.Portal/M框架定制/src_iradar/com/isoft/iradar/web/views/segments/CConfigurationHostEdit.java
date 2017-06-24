package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_intersect;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.natcasesort;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_ANY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_macros;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.ipmiAuthTypes;
import static com.isoft.iradar.inc.HostsUtil.ipmiPrivileges;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ItemsUtil.itemTypeInterface;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSON;
import com.isoft.iradar.tags.CJSON.Decorator;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.util.StringUtil;

public class CConfigurationHostEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.host.edit.js");

		CTabView divTabs = new CTabView();
		if (!isset(_REQUEST,"form_refresh")) {
			divTabs.setSelected("0");
		}

		CArray host_groups = get_request("groups", array());
		if (isset(_REQUEST,"groupid") && (Nest.value(_REQUEST,"groupid").asLong() > 0) && empty(host_groups)) {
			array_push(host_groups, Nest.value(_REQUEST,"groupid").$());
		}

		String newgroup = get_request("newgroup", "");
		String host = get_request("host", "");
		String visiblename = get_request("visiblename", "");
		Integer status = get_request("status", HOST_STATUS_MONITORED);
		String proxy_hostid = get_request("proxy_hostid", "");
		Integer ipmi_authtype = get_request("ipmi_authtype", -1);
		Integer ipmi_privilege = get_request("ipmi_privilege", 2);
		String ipmi_username = get_request("ipmi_username", "");
		String ipmi_password = get_request("ipmi_password", "");
		CArray macros = get_request("macros", array());
		CArray<Map> ifaces = get_request("interfaces", array());
		CArray<Map> interfaces = new CArray();
		int ifaceIdx = 1;
		for (Map iface : ifaces) {
			iface.remove("main");
			if(StringUtil.isEmpty(Nest.value(iface, "interfaceid").asString())){
				iface.put("isNew", "true");
				iface.put("interfaceid", String.valueOf(ifaceIdx++));
			}
			interfaces.put(Nest.value(iface, "interfaceid").asString(), iface);
		}
		CArray templateIds = get_request("templates", array());
		CArray clear_templates = get_request("clear_templates", array());

		Nest.value(_REQUEST,"hostid").$(get_request("hostid", 0));

		@SuppressWarnings("unused")
		String frm_title = _("Host");
		Map dbHost = null;
		CArray<Map> hostItemsToInventory = null;
		CArray<Map> original_templates = null;
		if (Nest.value(_REQUEST,"hostid").asLong() > 0) {
			dbHost = Nest.value(data,"dbHost").asCArray();

			frm_title += SPACE+" ["+Nest.value(dbHost,"host").asString()+"]";
			original_templates = Nest.value(dbHost,"parentTemplates").asCArray();
			original_templates = rda_toHash(original_templates, "templateid");

			if (isset(Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_AGENT).$())) {
				Object mainAgentId = Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_AGENT).$();
				Nest.value(interfaces,mainAgentId,"main").$("1");
			}
			if (isset(Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_SNMP).$())) {
				Object snmpAgentId = Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_SNMP).$();
				Nest.value(interfaces,snmpAgentId,"main").$("1");
			}
			if (isset(Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_JMX).$())) {
				Object ipmiAgentId = Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_JMX).$();
				Nest.value(interfaces,ipmiAgentId,"main").$("1");
			}
			if (isset(Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_IPMI).$())) {
				Object jmxAgentId = Nest.value(_REQUEST,"mainInterfaces",INTERFACE_TYPE_IPMI).$();
				Nest.value(interfaces,jmxAgentId,"main").$("1");
			}

			// get items that populate host inventory fields
			CItemGet ioptions = new CItemGet();
			ioptions.setFilter("hostid", Nest.value(dbHost,"hostid").asString());
			ioptions.setOutput(new String[]{"inventory_link", "itemid", "hostid", "name", "key_"});
			ioptions.setPreserveKeys(true);
			ioptions.setNopermissions(true);
			hostItemsToInventory = API.Item(idBean, executor).get(ioptions);
			hostItemsToInventory = rda_toHash(hostItemsToInventory, "inventory_link");

			hostItemsToInventory = CMacrosResolverHelper.resolveItemNames(idBean, executor, hostItemsToInventory);
		} else {
			original_templates = array();
		}

		// load data from the DB when opening the full clone form for the first time
		//array("clone", "full_clone")克隆方法容易保存错误以及保存数不对
		boolean cloneFormOpened = (in_array(get_request("form"), array("clone")) && Nest.as(get_request("form_refresh")).asInteger() == 1);
		if (!empty(get_request("hostid")) && (!hasRequest("form_refresh") || cloneFormOpened)) {
			proxy_hostid = Nest.value(dbHost,"proxy_hostid").asString();
			host = Nest.value(dbHost,"host").asString();
			visiblename = Nest.value(dbHost,"name").asString();

			// display empty visible name if equal to host name
			if (visiblename.equals(host)) {
				visiblename = "";
			}

			status = Nest.value(dbHost,"status").asInteger();

			ipmi_authtype = Nest.value(dbHost,"ipmi_authtype").asInteger();
			ipmi_privilege = Nest.value(dbHost,"ipmi_privilege").asInteger();
			ipmi_username = Nest.value(dbHost,"ipmi_username").asString();
			ipmi_password = Nest.value(dbHost,"ipmi_password").asString();

			macros = order_macros(Nest.value(dbHost,"macros").asCArray(), "macro");
			host_groups = rda_objectValues(Nest.value(dbHost,"groups").$(), "groupid");

			templateIds = array();
			for(Map tpl : original_templates) {
				Nest.value(templateIds,tpl.get("templateid")).$(Nest.value(tpl,"templateid").$());
			}

			interfaces = Nest.value(dbHost,"interfaces").asCArray();
			interfaces = Clone.deepcopy(interfaces);
			for (Entry<Object, Map> e : interfaces.entrySet()) {
			    Object hinum = e.getKey();
			    Map iface = e.getValue();
				Nest.value(interfaces,hinum,"items").$(0);
				Nest.value(interfaces,hinum,"items").$(count(Nest.value(dbHost,"interfaces",iface.get("interfaceid"),"items").asCArray()));

				// check if interface has items that require specific interface type, if so type cannot be changed
				int locked = 0;
				for(Map item : (CArray<Map>)Nest.value(dbHost,"interfaces",iface.get("interfaceid"),"items").asCArray()) {
					Integer itemInterfaceType = itemTypeInterface(Nest.value(item,"type").asInteger());
					if (!(empty(itemInterfaceType) || itemInterfaceType == INTERFACE_TYPE_ANY)) {
						locked = 1;
						break;
					}
				}
				Nest.value(interfaces,hinum,"locked").$(locked);
			}
		}

		clear_templates = array_intersect(clear_templates, array_keys(original_templates));
		clear_templates = array_diff(clear_templates, array_keys(templateIds));
		natcasesort(templateIds);

		// whether this is a discovered host
		boolean isDiscovered = (!empty(get_request("hostid")) && Nest.value(dbHost,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED && "update".equals(get_request("form")));

		CForm frmHost = new CForm();
		frmHost.setName("web.hosts.host.action.");
		frmHost.addVar("form", get_request("form", "1"));
		frmHost.addVar("clear_templates", clear_templates);
		CFormList hostList = new CFormList("hostlist");

		if (Nest.value(_REQUEST,"hostid").asLong() > 0 && !"clone".equals(get_request("form"))) {
			frmHost.addVar("hostid", Nest.value(_REQUEST,"hostid").$());
		}
		if (Nest.value(_REQUEST,"groupid").asLong() > 0) {
			frmHost.addVar("groupid", Nest.value(_REQUEST,"groupid").$());
		}

		// LLD rule link
		if (isDiscovered) {
			hostList.addRow(
				_("Discovered by"),
				new CLink(Nest.value(dbHost,"discoveryRule","name").$(),
					"host_prototypes.action?parent_discoveryid="+Nest.value(dbHost,"discoveryRule","itemid").$(),
					"highlight underline weight_normal"
				)
			);
		}

		CTextBox hostTB = new CTextBox("host", host, RDA_TEXTBOX_STANDARD_SIZE, isDiscovered);
		hostTB.setAttribute("maxlength", 18);
		hostTB.setAttribute("autofocus", "autofocus");
		hostList.addRow(_("Host name"), hostTB);

		CTextBox visiblenameTB = new CTextBox("visiblename", visiblename, RDA_TEXTBOX_STANDARD_SIZE, isDiscovered);
		visiblenameTB.setAttribute("maxlength", 64);
		hostList.addRow(_("Visible name"), visiblenameTB);

		// groups for normal hosts
		if (!isDiscovered) {
			CTweenBox grp_tb = new CTweenBox(frmHost, "groups", host_groups, 10);
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setEditable(true);
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> all_groups = API.HostGroup(idBean, executor).get(hgoptions);
			order_result(all_groups, "name");
			for(Map group : all_groups) {
				Long id = Nest.value(group, "groupid").asLong();
				if(IMonGroup.systemGroups().containsKey(id)) {
					continue;
				}
				if(!IMonGroup.MON_VM.id().equals(id))
					grp_tb.addItem(id, Nest.value(group,"name").asString());
			}

			hostList.addRow(_("Groups"), grp_tb.get(_("In groups"), _("Other groups")));

		} else {// groups for discovered hosts
			CComboBox groupBox = new CComboBox("groups");
			groupBox.setAttribute("readonly", true);
			groupBox.setAttribute("size", 10);
			for(Map group : (CArray<Map>)Nest.value(dbHost,"groups").asCArray()) {
				groupBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
			}
			hostList.addRow(_("Groups"), groupBox);
		}

		// interfaces for normal hosts
		if (!isDiscovered) {
			String script = null;
			if (empty(interfaces)) {
				script = "hostInterfacesManager.addNew(\"agent\");";
			} else {
				CJSON json = new CJSON();
				CArray<Map> decorateInterfaces = CJSON.decorate(interfaces, new Decorator(){
					@Override
					protected Object doDecoratorValue(Object key, Object value) {
						if(value == null){
							return null;
						}
						if("locked".equals(key) || "items".equals(key)){
							return value;
						}
						return Nest.as(value).asString();
					}					
				});
				String encodedInterfaces = json.encode(decorateInterfaces);
				script = "hostInterfacesManager.add("+encodedInterfaces+");";
			}
			rda_add_post_js(script);

			// table for agent interfaces with footer
			CTable ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "agentInterfaces");
			ifTab.setAttribute("data-type", "agent");

			// headers with sizes
			CCol iconLabel = new CCol(SPACE, "interface-drag-control");
			CCol ipLabel = new CCol(_("IP address"), "interface-ip");
			CCol dnsLabel = new CCol(_("DNS name"), "interface-dns");
			CCol connectToLabel = new CCol(_("Connect to"), "interface-connect-to");
			CCol portLabel = new CCol(_("Port"), "interface-port");
			CCol defaultLabel = new CCol(_("Default"), "interface-default");
			CCol removeLabel = new CCol(SPACE, "interface-control");
			ifTab.addRow(array(iconLabel, ipLabel, dnsLabel, connectToLabel, portLabel, defaultLabel, removeLabel));

			CSpan helpTextWhenDragInterfaceAgent = new CSpan(_("Drag here to change the type of the interface to \"agent\" type."));
			helpTextWhenDragInterfaceAgent.addClass("dragHelpText");
			CCol buttonCol = new CCol(new CButton("addAgentInterface", _("Add"), null, "link_menu add"), "interface-add-control");
			CCol col = new CCol(helpTextWhenDragInterfaceAgent);
			col.setAttribute("colspan", 6);
			CRow buttonRow = new CRow(array(buttonCol, col));
			buttonRow.setAttribute("id", "agentInterfacesFooter");

			ifTab.addRow(buttonRow);

			hostList.addRow(_("Agent interfaces"), new CDiv(ifTab, "border_dotted objectgroup inlineblock interface-group"), false, null, "interface-row interface-row-first");

			// table for SNMP interfaces with footer
			ifTab = new CTable(null, "formElementTable");
			ifTab.setAttribute("id", "SNMPInterfaces");
			ifTab.setAttribute("data-type", "snmp");

			CSpan helpTextWhenDragInterfaceSNMP = new CSpan(_("Drag here to change the type of the interface to \"SNMP\" type."));
			helpTextWhenDragInterfaceSNMP.addClass("dragHelpText");
			buttonCol = new CCol(new CButton("addSNMPInterface", _("Add"), null, "link_menu add"), "interface-add-control");
			col = new CCol(helpTextWhenDragInterfaceSNMP);
			col.setAttribute("colspan", 6);
			buttonRow = new CRow(array(buttonCol, col));
			buttonRow.setAttribute("id", "SNMPInterfacesFooter");

			ifTab.addRow(buttonRow);

			hostList.addRow(_("SNMP interfaces"), new CDiv(ifTab, "border_dotted inlineblock objectgroup interface-group"), false, null, "interface-row");

			if(Feature.enableJMX) {
				// table for JMX interfaces with footer
				ifTab = new CTable(null, "formElementTable");
				ifTab.setAttribute("id", "JMXInterfaces");
				ifTab.setAttribute("data-type", "jmx");
				CSpan helpTextWhenDragInterfaceJMX = new CSpan(_("Drag here to change the type of the interface to \"JMX\" type."));
				helpTextWhenDragInterfaceJMX.addClass("dragHelpText");
				buttonCol = new CCol(new CButton("addJMXInterface", _("Add"), null, "link_menu add"), "interface-add-control");
				col = new CCol(helpTextWhenDragInterfaceJMX);
				col.setAttribute("colspan", 6);
				buttonRow = new CRow(array(buttonCol, col));
				buttonRow.setAttribute("id", "JMXInterfacesFooter");
				ifTab.addRow(buttonRow);

				hostList.addRow(_("JMX interfaces"), new CDiv(ifTab, "border_dotted objectgroup inlineblock interface-group"), false, null, "interface-row");
			}
			
			if(Feature.enableIPMI) {
				// table for IPMI interfaces with footer
				ifTab = new CTable(null, "formElementTable");
				ifTab.setAttribute("id", "IPMIInterfaces");
				ifTab.setAttribute("data-type", "ipmi");
				CSpan helpTextWhenDragInterfaceIPMI = new CSpan(_("Drag here to change the type of the interface to \"IPMI\" type."));
				helpTextWhenDragInterfaceIPMI.addClass("dragHelpText");
				buttonCol = new CCol(new CButton("addIPMIInterface", _("Add"), null, "link_menu add"), "interface-add-control");
				col = new CCol(helpTextWhenDragInterfaceIPMI);
				col.setAttribute("colspan", 6);
				buttonRow = new CRow(array(buttonCol, col));
				buttonRow.setAttribute("id", "IPMIInterfacesFooter");

				ifTab.addRow(buttonRow);
				hostList.addRow(_("IPMI interfaces"), new CDiv(ifTab, "border_dotted objectgroup inlineblock interface-group"), false, null, "interface-row");
			}
		} else {// interfaces for discovered hosts
			interfaces = array();
			CArray existingInterfaceTypes = array();
			for(Map iface : (CArray<Map>)Nest.value(dbHost,"interfaces").asCArray()) {
				Nest.value(iface,"locked").$(true);
				Nest.value(existingInterfaceTypes,iface.get("type")).$(true);
				Nest.value(interfaces,iface.get("interfaceid")).$(iface);
			}
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
		}

		if(Feature.showProxy) {
			// Proxy
			Object proxyControl = null;
			if (!isDiscovered) {
				proxyControl = new CComboBox("proxy_hostid", proxy_hostid);
				((CComboBox)proxyControl).addItem(0, _("(no proxy)"));

				CProxyGet poptions = new CProxyGet();
				poptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> db_proxies = API.Proxy(idBean, executor).get(poptions);
				order_result(db_proxies, "host");

				for(Map proxy : db_proxies) {
					((CComboBox)proxyControl).addItem(Nest.value(proxy,"proxyid").$(), Nest.value(proxy,"host").asString());
				}
			} else {
				if (!empty(Nest.value(dbHost,"proxy_hostid").$())) {
					CProxyGet poptions = new CProxyGet();
					poptions.setOutput(new String[]{"host", "proxyid"});
					poptions.setProxyIds(Nest.value(dbHost,"proxy_hostid").asLong());
					poptions.setLimit(1);
					CArray<Map> proxies = API.Proxy(idBean, executor).get(poptions);
					Map proxy = reset(proxies);
					proxyControl = new CTextBox("proxy_host", Nest.value(proxy,"host").asString(), null, true);
				} else {
					proxyControl = new CTextBox("proxy_host", _("(no proxy)"), null, true);
				}
			}
			hostList.addRow(_("Monitored by proxy"), proxyControl);
		}else {
			if(!Cphp.empty(proxy_hostid)) {
				hostList.addVar("proxy_hostid", proxy_hostid);
			}
		}
		

		CComboBox cmbStatus = new CComboBox("status", status);
		cmbStatus.addItem(HOST_STATUS_MONITORED, _("Monitored"));
		cmbStatus.addItem(HOST_STATUS_NOT_MONITORED, _("Not monitored"));

		hostList.addRow(_("Status"), cmbStatus);

		divTabs.addTab("hostTab", _("Host"), hostList);

		// templates
		CFormList tmplList = new CFormList("tmpllist",IMonConsts.STYLE_CLASS_MULTLINE);

		// create linked template table
		CTable linkedTemplateTable = new CTable(_("No templates linked."), "formElementTable");
		linkedTemplateTable.attr("id", "linkedTemplateTable");

		CTemplateGet toptions = new CTemplateGet();
		toptions.setTemplateIds(templateIds.valuesAsLong());
		toptions.setOutput(new String[]{"templateid", "name"});
		CArray<Map> linkedTemplates = API.Template(idBean, executor).get(toptions);
		CArrayHelper.sort(linkedTemplates, array("name"));

		// templates for normal hosts
		if (!isDiscovered) {
			linkedTemplateTable.setHeader(array(_("Name"), _("Action operations")));
			CArray ignoredTemplates = array();
			for(Map template : linkedTemplates) {
				tmplList.addVar("templates[]", Nest.value(template,"templateid").asString());

				linkedTemplateTable.addRow(
					array(
						Nest.value(template,"name").$(),
						
						array(
							"取消关联",
							new CSubmit("unlink["+Nest.value(template,"templateid").$()+"]", _("Unlink"), null, "link_menu icon unlink"),
							SPACE,
							SPACE,
							isset(original_templates,Nest.value(template,"templateid").$())
								? "清空数据"+new CSubmit("unlink_and_clear["+Nest.value(template,"templateid").$()+"]", _("Unlink and clear"), null, "link_menu icon unlink_and_clear")
								: SPACE
						)
					),
					null, "conditions_"+Nest.value(template,"templateid").$()
				);

				Nest.value(ignoredTemplates,Nest.value(template,"templateid").$()).$(Nest.value(template,"name").$());
			}

			tmplList.addRow(_("Linked templates"), new CDiv(linkedTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));

			// create new linked template table
			CTable newTemplateTable = new CTable(null, "formElementTable");
			newTemplateTable.attr("id", "newTemplateTable");
			newTemplateTable.attr("style", "min-width: 400px;");

			newTemplateTable.addRow(array(new CMultiSelect(map(
				"name", "add_templates[]",
				"objectName", "templates",
				"ignored", ignoredTemplates,
				"popup", map(
					"parameters", "srctbl=templates&srcfld1=hostid&srcfld2=host&dstfrm="+frmHost.getName()+
						"&dstfld1=add_templates_&templated_hosts=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			))));

			newTemplateTable.addRow(new CRow(new CCol(new CSubmit("add_template", _("Add"), null, "link_menu add"),null,2),"cmd_row"));

			tmplList.addRow(_("Link new templates"), new CDiv(newTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		}
		// templates for discovered hosts
		else {
			linkedTemplateTable.setHeader(array(_("Name")));
			for(Map template : linkedTemplates) {
				linkedTemplateTable.addRow(array(Nest.value(template,"name").$()), null, "conditions_"+Nest.value(template,"templateid").$());
			}

			tmplList.addRow(_("Linked templates"), new CDiv(linkedTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		}

		divTabs.addTab("templateTab", _("Templates"), tmplList);

		if(Feature.enableIPMI) {
			/*
			 * IPMI
			 */
			CFormList ipmiList = new CFormList("ipmilist");

			// normal hosts
			Object cmbIPMIAuthtype = null;
			Object cmbIPMIPrivilege = null;
			if (!isDiscovered) {
				cmbIPMIAuthtype = new CComboBox("ipmi_authtype", ipmi_authtype);
				((CComboBox)cmbIPMIAuthtype).addItems(ipmiAuthTypes());
				((CComboBox)cmbIPMIAuthtype).addClass("openView");
				((CComboBox)cmbIPMIAuthtype).setAttribute("size", 7);
				((CComboBox)cmbIPMIAuthtype).addStyle("width: 170px;");
				ipmiList.addRow(_("Authentication algorithm"), cmbIPMIAuthtype);

				cmbIPMIPrivilege = new CComboBox("ipmi_privilege", ipmi_privilege);
				((CComboBox)cmbIPMIPrivilege).addItems(ipmiPrivileges());
				((CComboBox)cmbIPMIPrivilege).addClass("openView");
				((CComboBox)cmbIPMIPrivilege).setAttribute("size", 5);
				((CComboBox)cmbIPMIPrivilege).addStyle("width: 170px;");
				ipmiList.addRow(_("Privilege level"), cmbIPMIPrivilege);
			} else {// discovered hosts
				cmbIPMIAuthtype = new CTextBox("ipmi_authtype_name", ipmiAuthTypes(Nest.value(dbHost,"ipmi_authtype").asInteger()), RDA_TEXTBOX_SMALL_SIZE, true);
				ipmiList.addRow(_("Authentication algorithm"), cmbIPMIAuthtype);

				cmbIPMIPrivilege = new CTextBox("ipmi_privilege_name", ipmiPrivileges(Nest.value(dbHost,"ipmi_privilege").asInteger()), RDA_TEXTBOX_SMALL_SIZE, true);
				ipmiList.addRow(_("Privilege level"), cmbIPMIPrivilege);
			}

			ipmiList.addRow(_("Username"), new CTextBox("ipmi_username", ipmi_username, RDA_TEXTBOX_SMALL_SIZE, isDiscovered));
			ipmiList.addRow(_("Password"), new CTextBox("ipmi_password", ipmi_password, RDA_TEXTBOX_SMALL_SIZE, isDiscovered));
			divTabs.addTab("ipmiTab", _("IPMI"), ipmiList);
		}

		/*
		 * Macros
		 */
		if (empty(macros)) {
			macros = array(map("macro", "", "value", ""));
		}

		CView macrosView = new CView("common.macros", map(
			"macros", macros,
			"readonly", isDiscovered
		));
		divTabs.addTab("macroTab", _("Macros"), macrosView.render(idBean, executor));

		frmHost.addItem(divTabs);

		/* footer */
		CArray others = array();
		// do not display the clone and delete buttons for clone forms and new host forms
		if (!empty(get_request("hostid")) && !in_array(get_request("form"), array("clone", "full_clone"))) {
			others.add(new CSubmit("clone", _("Clone")));
		}
		others.add(new CButtonCancel(url_param(idBean, "groupid")));
		
		CDiv footer_div = makeFormFooter(new CSubmit("save", _("Save")), others);
		String footerClass = footer_div.getAttribute("class").toString();
		footerClass += " footer_moniter";
		footer_div.setAttribute("class", footerClass);
		
		frmHost.addItem(footer_div);

		return frmHost;
	}

}
