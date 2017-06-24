package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_intersect;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.natcasesort;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_macros;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTemplateEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTabView divTabs = new CTabView();
		if (!isset(_REQUEST,"form_refresh")) {
			divTabs.setSelected("0");
		}

		Long templateid = get_request("templateid", 0L);
		String host = get_request("template_name", "");
		String visiblename = get_request("visiblename", "");
		String newgroup = get_request("newgroup", "");
		CArray templateIds = get_request("templates", array());
		CArray clear_templates = get_request("clear_templates", array());
		CArray macros = get_request("macros", array());

		@SuppressWarnings("unused")
		String frm_title = _("Template");

		if (templateid > 0) {
			frm_title += SPACE+" ["+Nest.value(data,"dbTemplate","name").$()+"]";
		}
		CForm frmHost = new CForm();
		frmHost.setName("tpl_for");

		frmHost.addVar("form", get_request("form", "1"));
		frmHost.addVar("groupid", Nest.value(_REQUEST,"groupid").$());

		if (!empty(templateid)) {
			frmHost.addVar("templateid", templateid);
		}

		CArray<Map> hosts_linked_to = null;
		if (templateid > 0 && !hasRequest("form_refresh")) {
			host = Nest.value(data,"dbTemplate","host").asString();
			visiblename = Nest.value(data,"dbTemplate","name").asString();

			// display empty visible name if equal to host name
			if (visiblename.equals(host)) {
				visiblename = "";
			}
			macros = order_macros(Nest.value(data,"dbTemplate","macros").asCArray(), "macro");

			// get template hosts from db
			CHostGet params = new CHostGet();
			params.setTemplateIds(templateid);
			params.setEditable(true);
			params.setTemplatedHosts(true);
			hosts_linked_to = API.Host(idBean, executor).get(params);

			hosts_linked_to = rda_objectValues(hosts_linked_to, "hostid");
			hosts_linked_to = rda_toHash(hosts_linked_to, "hostid");
			templateIds = Nest.value(data,"original_templates").asCArray();
		} else {
			hosts_linked_to = get_request("hosts", array());
		}

		clear_templates = array_intersect(clear_templates, array_keys(Nest.value(data,"original_templates").asCArray()));
		clear_templates = array_diff(clear_templates, array_keys(templateIds));
		natcasesort(templateIds);
		frmHost.addVar("clear_templates", clear_templates);

		// TEMPLATE WIDGET {
		CFormList templateList = new CFormList("hostlist");

		// FORM ITEM : Template name text box [  ]
		CTextBox template_nameTB = new CTextBox("template_name", host, 54);
		template_nameTB.setAttribute("maxlength", 64);
		template_nameTB.attr("autofocus", "autofocus");
		templateList.addRow(_("Template name"), template_nameTB);

		CTextBox visiblenameTB = new CTextBox("visiblename", visiblename, 54);
		visiblenameTB.setAttribute("maxlength", 64);
		templateList.addRow(_("Template Visible name"), visiblenameTB);

		//指定Templates设备类型(服务器分组的名称不变情况下)
		CArray _groups = array(map("groupid",IMonGroup.TEMPLATES.id()));
		//根据name获取对应的值
		_groups = rda_objectValues(_groups, "groupid");
		
		// get all Groups
		CTweenBox group_tb = new CTweenBox(frmHost, "groups", _groups, 10);
		group_tb.addItem(IMonGroup.TEMPLATES.id(), "Templates");
		//隐藏设备类型
		templateList.addRow(_("Groups"), group_tb.get(_("In groups"), _("Other groups")), true);	
				
		CHostGroupGet options = new CHostGroupGet();
		options.setEditable(true);
		options.setFilter("internal", 0);
		options.setOnlyHostGroup(true);
		options.setOutput(new String[]{"groupid","name"});
		CArray<Map> all_groups = API.HostGroup(idBean, executor).get(options);
		order_result(all_groups, "name");		
		
		// FORM ITEM : new group text box [  ]
		CTextBox newgroupTB = new CTextBox("newgroup", newgroup);
		newgroupTB.setAttribute("maxlength", 64);
		
		// FORM ITEM : linked Hosts tween box [  ] [  ]
		Long twb_groupid = get_request("twb_groupid", 0L);
		if (twb_groupid == 0) {
			Map gr = reset(all_groups);
			twb_groupid = Nest.value(gr,"groupid").asLong();
		}
		CComboBox cmbGroups = new CComboBox("twb_groupid", twb_groupid, "submit()");
		for(Map group : all_groups) {
			if(IMonConsts.MON_VM==Nest.value(group,"groupid").asLong())
				continue;
			cmbGroups.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}

		CTweenBox host_tb = new CTweenBox(frmHost, "hosts", hosts_linked_to, 20);

		// get hosts from selected twb_groupid combo
		CHostGet hoptions = new CHostGet();
		hoptions.setGroupIds(twb_groupid);
		hoptions.setTemplatedHosts(true);
		hoptions.setEditable(true);
		hoptions.setOutput(API_OUTPUT_EXTEND);
		hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
		CArray<Map> db_hosts = API.Host(idBean, executor).get(hoptions);
		order_result(db_hosts, "name");

		for(Map db_host : db_hosts) {
			if (isset(hosts_linked_to,db_host.get("hostid"))) {
				continue;
			} // add all except selected hosts
			host_tb.addItem(Nest.value(db_host,"hostid").$(), Nest.value(db_host,"name").asString());
		}

		// select selected hosts and add them
		hoptions = new CHostGet();
		hoptions.setHostIds(hosts_linked_to.valuesAsLong());
		hoptions.setTemplatedHosts(true);
		hoptions.setEditable(true);
		hoptions.setOutput(API_OUTPUT_EXTEND);

		db_hosts = API.Host(idBean, executor).get(hoptions);
		order_result(db_hosts, "name");
		for(Map db_host : db_hosts) {
			host_tb.addItem(Nest.value(db_host,"hostid").$(), Nest.value(db_host,"name").asString(), null, (Nest.value(db_host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL));
		}

		templateList.addRow(_("By host"), host_tb.get(_("In"), array(
			_("Hosts|Groups")+SPACE,
			cmbGroups
		)));

		// FULL CLONE {
		if (Nest.value(_REQUEST,"form").$() == "full_clone") {
			// template applications
			CAppGet aoptions = new CAppGet();
			aoptions.setHostIds(templateid);
			aoptions.setInherited(true);
			aoptions.setOutput(API_OUTPUT_EXTEND);
			aoptions.setPreserveKeys(true);
			CArray<Map> templateApps = API.Application(idBean, executor).get(aoptions);
			if (!empty(templateApps)) {
				CArray applicationsList = array();
				for (Entry<Object, Map> e : templateApps.entrySet()) {
				    Object tplAppId = e.getKey();
				    Map templateApp = e.getValue();
					Nest.value(applicationsList,tplAppId).$(Nest.value(templateApp,"name").$());
				}
				order_result(applicationsList);

				CListBox listBox = new CListBox("applications", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(applicationsList);
				templateList.addRow(_("Applications"), listBox);
			}

			// items
			CItemGet ioptions = new CItemGet();
			ioptions.setHostIds(templateid);
			ioptions.setInherited(false);
			ioptions.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
			ioptions.setOutput(new String[]{"itemid", "key_", "name", "hostid"});
			CArray<Map> hostItems = API.Item(idBean, executor).get(ioptions);

			if (!empty(hostItems)) {
				hostItems = CMacrosResolverHelper.resolveItemNames(idBean, executor, hostItems);

				CArray itemsList = array();
				for(Map hostItem : hostItems) {
					Nest.value(itemsList,hostItem.get("itemid")).$(Nest.value(hostItem,"name_expanded").$());
				}
				order_result(itemsList);

				CListBox listBox = new CListBox("items", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(itemsList);

				templateList.addRow(_("Items"), listBox);
			}

			// Triggers
			CTriggerGet toptions = new CTriggerGet();
			toptions.setInherited(false);
			toptions.setHostIds(templateid);
			toptions.setOutput(API_OUTPUT_EXTEND);
			toptions.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
			CArray<Map> hostTriggers = API.Trigger(idBean, executor).get(toptions);
			if (!empty(hostTriggers)) {
				CArray triggersList = array();
				for(Map hostTrigger : hostTriggers) {
					Nest.value(triggersList,hostTrigger.get("triggerid")).$(Nest.value(hostTrigger,"description").$());
				}
				order_result(triggersList);

				CListBox listBox = new CListBox("triggers", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(triggersList);

				templateList.addRow(_("Triggers"), listBox);
			}

			// Graphs
			CGraphGet goptions = new CGraphGet();
			goptions.setInherited(false);
			goptions.setHostIds(templateid);
			goptions.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
			goptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> hostGraphs = API.Graph(idBean, executor).get(goptions);
			if (!empty(hostGraphs)) {
				CArray graphsList = array();
				for(Map hostGraph : hostGraphs) {
					Nest.value(graphsList,hostGraph.get("graphid")).$(Nest.value(hostGraph,"name").$());
				}
				order_result(graphsList);

				CListBox listBox = new CListBox("graphs", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(graphsList);

				templateList.addRow(_("Graphs"), listBox);
			}

			// discovery rules
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setInherited(false);
			droptions.setHostIds(templateid);
			droptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> hostDiscoveryRules = API.DiscoveryRule(idBean, executor).get(droptions);

			if (!empty(hostDiscoveryRules)) {
				hostDiscoveryRules = CMacrosResolverHelper.resolveItemNames(idBean, executor, hostDiscoveryRules);

				CArray discoveryRuleList = array();
				for(Map discoveryRule : hostDiscoveryRules) {
					Nest.value(discoveryRuleList,discoveryRule.get("itemid")).$(Nest.value(discoveryRule,"name_expanded").$());
				}
				order_result(discoveryRuleList);
				CArray hostDiscoveryRuleids = array_keys(discoveryRuleList);

				CListBox listBox = new CListBox("discoveryRules", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(discoveryRuleList);

				templateList.addRow(_("Discovery rules"), listBox);

				// item prototypes
				CItemPrototypeGet ipoptions = new CItemPrototypeGet();
				ipoptions.setHostIds(templateid);
				ipoptions.setDiscoveryIds(hostDiscoveryRuleids.valuesAsLong());
				ipoptions.setInherited(false);
				ipoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> hostItemPrototypes = API.ItemPrototype(idBean, executor).get(ipoptions);

				if (!empty(hostItemPrototypes)) {
					hostItemPrototypes = CMacrosResolverHelper.resolveItemNames(idBean, executor, hostItemPrototypes);

					CArray prototypeList = array();
					for(Map itemPrototype : hostItemPrototypes) {
						Nest.value(prototypeList,itemPrototype.get("itemid")).$(Nest.value(itemPrototype,"name_expanded").$());
					}
					order_result(prototypeList);

					listBox = new CListBox("itemsPrototypes", null, 8);
					listBox.setAttribute("disabled", "disabled");
					listBox.addItems(prototypeList);

					templateList.addRow(_("Item prototypes"), listBox);
				}

				// Trigger prototypes
				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setHostIds(templateid);
				tpoptions.setDiscoveryIds(hostDiscoveryRuleids.valuesAsLong());
				tpoptions.setInherited(false);
				tpoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> hostTriggerPrototypes = API.TriggerPrototype(idBean, executor).get(tpoptions);
				if (!empty(hostTriggerPrototypes)) {
					CArray prototypeList = array();
					for(Map triggerPrototype : hostTriggerPrototypes) {
						Nest.value(prototypeList,triggerPrototype.get("triggerid")).$(Nest.value(triggerPrototype,"description").$());
					}
					order_result(prototypeList);

					listBox = new CListBox("triggerprototypes", null, 8);
					listBox.setAttribute("disabled", "disabled");
					listBox.addItems(prototypeList);

					templateList.addRow(_("Trigger prototypes"), listBox);
				}

				// Graph prototypes
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setHostIds(templateid);
				gpoptions.setDiscoveryIds(hostDiscoveryRuleids.valuesAsLong());
				gpoptions.setInherited(false);
				gpoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> hostGraphPrototypes = API.GraphPrototype(idBean, executor).get(gpoptions);
				if (!empty(hostGraphPrototypes)) {
					CArray prototypeList = array();
					for(Map graphPrototype : hostGraphPrototypes) {
						Nest.value(prototypeList,graphPrototype.get("graphid")).$(Nest.value(graphPrototype,"name").$());
					}
					order_result(prototypeList);

					listBox = new CListBox("graphPrototypes", null, 8);
					listBox.setAttribute("disabled", "disabled");
					listBox.addItems(prototypeList);

					templateList.addRow(_("Graph prototypes"), listBox);
				}
			}

			// screens
			CTemplateScreenGet tsoptions = new CTemplateScreenGet();
			tsoptions.put("inherited", false);
			tsoptions.setTemplateIds(templateid);
			tsoptions.setOutput(new String[]{"screenid", "name"});
			CArray<Map> screens = API.TemplateScreen(idBean, executor).get(tsoptions);
			if (!empty(screens)) {
				CArray screensList = array();
				for(Map screen : screens) {
					Nest.value(screensList,screen.get("screenid")).$(Nest.value(screen,"name").$());
				}
				order_result(screensList);

				CListBox listBox = new CListBox("screens", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(screensList);

				templateList.addRow(_("Screens"), listBox);
			}

			// web scenarios
			CHttpTestGet htoptions = new CHttpTestGet();
			htoptions.setOutput(new String[]{"httptestid", "name"});
			htoptions.setHostIds(templateid);
			htoptions.setInherited(false);
			CArray<Map> httpTests = API.HttpTest(idBean, executor).get(htoptions);

			if (!empty(httpTests)) {
				CArray httpTestList = array();

				for(Map httpTest : httpTests) {
					Nest.value(httpTestList,httpTest.get("httptestid")).$(Nest.value(httpTest,"name").$());
				}

				order_result(httpTestList);

				CListBox listBox = new CListBox("httpTests", null, 8);
				listBox.setAttribute("disabled", "disabled");
				listBox.addItems(httpTestList);
				templateList.addRow(_("Web scenarios"), listBox);
			}
		}

		divTabs.addTab("templateTab", _("Template"), templateList);
		// FULL CLONE }

		// } TEMPLATE WIDGET

		// TEMPLATES{
		CFormList tmplList = new CFormList("tmpllist", IMonConsts.STYLE_CLASS_MULTLINE);

		// create linked template table
		CTable linkedTemplateTable = new CTable(_("No templates linked."), "formElementTable");
		linkedTemplateTable.attr("id", "linkedTemplateTable");
		linkedTemplateTable.attr("style", "min-width: 400px;");
		linkedTemplateTable.setHeader(array(_("Name"), _("Operations")));

		CArray ignoredTemplates = array();
		for(Map template : (CArray<Map>)Nest.value(data,"linkedTemplates").asCArray()) {
			tmplList.addVar("templates[]", Nest.value(template,"templateid").asString());

			linkedTemplateTable.addRow(
				array(
					Nest.value(template,"name").$(),
					array(
						_("cacelassociated"),
						new CSubmit("unlink["+Nest.value(template,"templateid").$()+"]", _("Unlink"), null, "link_menu icon unlink"),
						SPACE,
						SPACE,
						isset(Nest.value(data,"original_templates",template.get("templateid")).$())
							? _("clearData")+ new CSubmit("unlink_and_clear["+Nest.value(template,"templateid").$()+"]", _("Unlink and clear"), null, "link_menu icon unlink_and_clear")
							: SPACE
					)
				),
				null, "conditions_"+Nest.value(template,"templateid").$()
			);

			Nest.value(ignoredTemplates,template.get("templateid")).$(Nest.value(template,"name").$());
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

		newTemplateTable.addRow(new CRow(new CCol(new CSubmit("add_template", _("Add"), null, "link_menu add"),null,3),"cmd_row"));

		tmplList.addRow(_("Link new templates"), new CDiv(newTemplateTable, "objectgroup inlineblock border_dotted ui-corner-all"));

		divTabs.addTab("tmplTab", _("Linked templates"), tmplList);
		// } TEMPLATES

		// macros
		if (empty(macros)) {
			macros = array(map("macro", "", "value", ""));
		}
		CView macrosView = new CView("common.macros", map(
			"macros", macros
		));
		divTabs.addTab("macroTab", _("Macros"), macrosView.render(idBean, executor));

		frmHost.addItem(divTabs);

		// Footer
		CArray others = array();
		if ((templateid > 0) && (!"full_clone".equals(Nest.value(_REQUEST,"form").asString()))) {
			others.add(new CSubmit("clone", _("Clone")));
			//others.add(new CSubmit("full_clone", _("Full clone")));
		}
		others.add(new CButtonCancel(url_param(idBean, "groupid")));

		CDiv saveDiv = makeFormFooter(new CSubmit("save", _("Save")), others);
		String saveClass = saveDiv.getAttribute("class").toString();
		saveClass += " footer_save";
		saveDiv.setAttribute("class", saveClass);
		frmHost.addItem(saveDiv);

		return frmHost;
	}
	
	/**
	 * 根据分组名称获取分组ID
	 * @param groupName
	 * @return
	 */
	private CArray getGroupIdByName(String groupName,SQLExecutor executor){
		String sql = "select g.groupid from groups g where g.name=#{name}";
		Map sqlParam = EasyMap.build(
				"name", groupName
			);
		return DBselect(executor, sql, sqlParam);
	}
	

}
