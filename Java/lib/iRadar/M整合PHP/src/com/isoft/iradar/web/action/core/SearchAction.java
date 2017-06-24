package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.selectByPattern;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.make_decoration;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class SearchAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Search"));
		page("file", "search.action");
		page("hist_arg", new String[] {});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"type",		array(T_RDA_INT, O_OPT,	P_SYS,	IN("0,1"),		null),
			"search",	array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			//ajax
			"favobj",	array(T_RDA_STR, O_OPT, P_ACT,	null,			null),
			"favref",	array(T_RDA_STR, O_OPT, P_ACT,  NOT_EMPTY,		"isset({favobj})"),
			"favstate",array(T_RDA_INT, O_OPT, P_ACT,  NOT_EMPTY,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			if ("hat".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.search.hats."+Nest.value(_REQUEST,"favref").asString()+".state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}
		
		if (PAGE_TYPE_JS == Nest.value(page,"type").asInteger() || PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger()) {
//			require_once dirname(__FILE__)."/include/page_footer.php";
//			exit();
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		boolean admin = in_array(CWebUser.getType(), array(
			USER_TYPE_IRADAR_ADMIN,
			USER_TYPE_SUPER_ADMIN
		));
		int rows_per_page = Nest.value(CWebUser.data(),"rows_per_page").asInteger();

		CWidget searchWidget = new CWidget("search_wdgt");

		String search = get_request("search", "");

		// Header
		if (rda_empty(search)) {
			search = _("Search pattern is empty");
		}
		searchWidget.setClass("header");
		searchWidget.addHeader(array(
			_("SEARCH")+NAME_DELIMITER,
			bold(search)
		), SPACE);

		// FIND Hosts
		CHostGet hostGet = new CHostGet();
		hostGet.setSearch("name", search);
		hostGet.setSearch("dns", search);
		hostGet.setSearch("ip", search);
		hostGet.setLimit(rows_per_page);
		hostGet.setSelectGroups(API_OUTPUT_EXTEND);
		hostGet.setSelectInterfaces(API_OUTPUT_EXTEND);
		hostGet.setSelectItems(API_OUTPUT_COUNT);
		hostGet.setSelectTriggers(API_OUTPUT_COUNT);
		hostGet.setSelectGraphs(API_OUTPUT_COUNT);
		hostGet.setSelectApplications(API_OUTPUT_COUNT);
		hostGet.setSelectScreens(API_OUTPUT_COUNT);
		hostGet.setSelectHttpTests(API_OUTPUT_COUNT);
		hostGet.setSelectDiscoveries(API_OUTPUT_COUNT);
		hostGet.setOutput(new String[]{"name", "status"});
		hostGet.setSearchByAny(true);
		CArray<Map> db_hosts = API.Host(getIdentityBean(), executor).get(hostGet);
		order_result(db_hosts, "name");

		CArray<Map> hosts = selectByPattern(db_hosts, "name", search, rows_per_page);
		CArray hostids = rda_objectValues(hosts, "hostid");

		hostGet = new CHostGet();
		hostGet.setHostIds(hostids.valuesAsLong());
		hostGet.setEditable(true);
		CArray<Map> rw_hosts = API.Host(getIdentityBean(), executor).get(hostGet);
		rw_hosts = rda_toHash(rw_hosts, "hostid");

		hostGet = new CHostGet();
		hostGet.setSearch("name", search);
		hostGet.setSearch("dns", search);
		hostGet.setSearch("ip", search);
		hostGet.setCountOutput(true);
		hostGet.setSearchByAny(true);
		Long overalCount = API.Host(getIdentityBean(), executor).get(hostGet);
		int viewCount = count(hosts);

		CTableInfo table = new CTableInfo(_("No hosts found."));
		table.setHeader(array(
			new CCol(_("Hosts")),
			new CCol(_("IP")),
			new CCol(_("DNS")),
			new CCol(_("Latest data")),
			new CCol(_("Triggers")),
			new CCol(_("Events")),
			new CCol(_("Graphs")),
			new CCol(_("Screens")),
			new CCol(_("Web")),
			new CCol(_("Applications")),
			new CCol(_("Items")),
			new CCol(_("Triggers")),
			new CCol(_("Graphs")),
			new CCol(_("Discovery")),
			new CCol(_("Web"))
		));

		for (Map host : hosts) {
			Long hostid = Nest.value(host,"hostid").asLong();

			Map iface = reset(Nest.value(host,"interfaces").asCArray());
			Nest.value(host,"ip").$(Nest.value(iface,"ip").$());
			Nest.value(host,"dns").$(Nest.value(iface,"dns").$());
			Nest.value(host,"port").$(Nest.value(iface,"port").$());

			String style = Nest.value(host,"status").asInteger() == HOST_STATUS_NOT_MONITORED ? "on" : null;

			Map group = reset(Nest.value(host,"groups").asCArray());
			String link = "groupid="+Nest.value(group,"groupid").$()+"&hostid="+hostid;

			Object caption = make_decoration(Nest.value(host,"name").asString(), search);

			Object host_link = null, applications_link = null, items_link = null, triggers_link = null, graphs_link = null, discoveryLink = null, httpTestsLink = null;
			if (admin && isset(rw_hosts,hostid)) {
				host_link = new CLink(caption, "hosts.action?form=update&"+link, style);
				applications_link = array(
					new CLink(_("Applications"), "applications.action?"+link),
					" ("+Nest.value(host,"applications").$()+")"
				);
				items_link = array(
					new CLink(_("Items"), "items.action?filter_set=1&"+link),
					" ("+Nest.value(host,"items").$()+")"
				);
				triggers_link = array(
					new CLink(_("Triggers"), "triggers.action?"+link),
					" ("+Nest.value(host,"triggers").$()+")"
				);
				graphs_link = array(
					new CLink(_("Graphs"), "graphs.action?"+link),
					" ("+Nest.value(host,"graphs").$()+")"
				);
				discoveryLink = array(
					new CLink(_("Discovery"), "host_discovery.action?"+link),
					" ("+Nest.value(host,"discoveries").$()+")"
				);
				httpTestsLink = array(
					new CLink(_("Web"), "httpconf.action?"+link),
					" ("+Nest.value(host,"httpTests").$()+")"
				);
			} else {
				host_link = new CSpan(caption, style);
				applications_link = _("Applications")+" ("+Nest.value(host,"applications").$()+")";
				items_link = _("Items")+" ("+Nest.value(host,"items").$()+")";
				triggers_link = _("Triggers")+" ("+Nest.value(host,"triggers").$()+")";
				graphs_link = _("Graphs")+" ("+Nest.value(host,"graphs").$()+")";
				discoveryLink = _("Discovery")+" ("+Nest.value(host,"discoveries").$()+")";
				httpTestsLink = _("Web")+" ("+Nest.value(host,"httpTests").$()+")";
			}

			Object hostip = make_decoration(Nest.value(host,"ip").asString(), search);
			Object hostdns = make_decoration(Nest.value(host,"dns").asString(), search);

			table.addRow(array(
				host_link,
				hostip,
				hostdns,
				new CLink(_("Latest data"), "latest.action?"+link),
				new CLink(_("Triggers"), "tr_status.action?"+link),
				new CLink(_("Events"), "events.action?source="+EVENT_SOURCE_TRIGGERS+"&"+link),
				new CLink(_("Graphs"), "charts.action?"+link),
				new CLink(_("Screens"), "host_screen.action?hostid="+hostid),
				new CLink(_("Web"), "httpmon.action?"+link),
				applications_link,
				items_link,
				triggers_link,
				graphs_link,
				discoveryLink,
				httpTestsLink
			));
		}

		//CIcon sysmap_menu = get_icon(executor, "menu", map("menu", "sysmaps"));

		CUIWidget wdgt_hosts = new CUIWidget("search_hosts", table, Nest.as(CProfile.get(getIdentityBean(), executor, "web.search.hats.search_hosts.state", true)).asInteger());
		wdgt_hosts.setHeader(_("Hosts"), SPACE);
		wdgt_hosts.setFooter(_s("Displaying %1s of %2s found", viewCount, overalCount));

		searchWidget.addItem(new CDiv(wdgt_hosts));

		// Find Host groups
		CHostGroupGet hostGroupGet = new CHostGroupGet();
		hostGroupGet.setOutput(API_OUTPUT_EXTEND);
		hostGroupGet.setSelectHosts(API_OUTPUT_COUNT);
		hostGroupGet.setSelectTemplates(API_OUTPUT_COUNT);
		hostGroupGet.setSearch("name", search);
		hostGroupGet.setLimit(rows_per_page);
		CArray<Map> db_hostGroups = API.HostGroup(getIdentityBean(), executor).get(hostGroupGet);
		order_result(db_hostGroups, "name");

		CArray<Map> hostGroups = selectByPattern(db_hostGroups, "name", search, rows_per_page);
		CArray groupids = rda_objectValues(hostGroups, "groupid");

		hostGroupGet = new CHostGroupGet();
		hostGroupGet.setGroupIds(groupids.valuesAsLong());
		hostGroupGet.setEditable(true);
		CArray<Map> rw_hostGroups = API.HostGroup(getIdentityBean(), executor).get(hostGroupGet);
		rw_hostGroups = rda_toHash(rw_hostGroups, "groupid");

		hostGroupGet = new CHostGroupGet();
		hostGroupGet.setSearch("name", search);
		hostGroupGet.setCountOutput(true);
		overalCount = API.HostGroup(getIdentityBean(), executor).get(hostGroupGet);
		viewCount = count(hostGroups);

		CArray header = array(
			new CCol(_("Host group")),
			new CCol(_("Latest data")),
			new CCol(_("Triggers")),
			new CCol(_("Events")),
			new CCol(_("Graphs")),
			new CCol(_("Web")),
			admin ? new CCol(_("Hosts")) : null,
			admin ? new CCol(_("Templates")) : null
		);

		table = new CTableInfo(_("No host groups found."));
		table.setHeader(header);

		for (Map group : hostGroups) {
			Long hostgroupid = Nest.value(group,"groupid").asLong();

			Object caption = make_decoration(Nest.value(group,"name").asString(), search);
			String link = "groupid="+hostgroupid+"&hostid=0";

			Object hostsLink = null;
			Object templatesLink = null;
			Object hgroup_link = new CSpan(caption);
			if (admin) {
				if (isset(rw_hostGroups,hostgroupid)) {
					if (!empty(Nest.value(group,"hosts").$())) {
						hostsLink = array(
							new CLink(_("Hosts"), "hosts.php?groupid="+hostgroupid),
							" ("+Nest.value(group,"hosts").$()+")"
						);
					} else {
						hostsLink = _("Hosts")+" (0)";
					}

					if (!empty(Nest.value(group,"templates").$())) {
						templatesLink = array(
							new CLink(_("Templates"), "templates.php?groupid="+hostgroupid),
							" ("+Nest.value(group,"templates").$()+")"
						);
					} else {
						templatesLink = _("Templates")+" (0)";
					}

					hgroup_link = new CLink(caption, "hostgroups.php?form=update&"+link);
				} else {
					hostsLink = _("Hosts");
					templatesLink = _("Templates");
				}
			}

			table.addRow(array(
				hgroup_link,
				new CLink(_("Latest data"), "latest.php?"+link),
				new CLink(_("Triggers"), "tr_status.php?"+link),
				new CLink(_("Events"), "events.php?source="+EVENT_SOURCE_TRIGGERS+"&"+link),
				new CLink(_("Graphs"), "charts.php?"+link),
				new CLink(_("Web"), "httpmon.php?"+link),
				hostsLink,
				templatesLink
			));
		}

		CUIWidget wdgt_hgroups = new CUIWidget("search_hostgroup", table, Nest.as(CProfile.get(getIdentityBean(), executor,"web.search.hats.search_hostgroup.state", true)).asInteger());
		wdgt_hgroups.setHeader(_("Host groups"), SPACE);
		wdgt_hgroups.setFooter(_s("Displaying %1s of %2s found", viewCount, overalCount));

		searchWidget.addItem(new CDiv(wdgt_hgroups));
		//----------------

		// FIND Templates
		if (admin) {
			CTemplateGet templateGet = new CTemplateGet();
			templateGet.setSearch("name", search);
			templateGet.setOutput(new String[]{"name"});
			templateGet.setSelectGroups(API_OUTPUT_REFER);
			templateGet.setSortfield("name");
			templateGet.setSelectItems(API_OUTPUT_COUNT);
			templateGet.setSelectTriggers(API_OUTPUT_COUNT);
			templateGet.setSelectGraphs(API_OUTPUT_COUNT);
			templateGet.setSelectApplications(API_OUTPUT_COUNT);
			templateGet.setSelectScreens(API_OUTPUT_COUNT);
			templateGet.setSelectHttpTests(API_OUTPUT_COUNT);
			templateGet.setSelectDiscoveries(API_OUTPUT_COUNT);
			templateGet.setLimit(rows_per_page);
			CArray<Map> db_templates = API.Template(getIdentityBean(), executor).get(templateGet);
			order_result(db_templates, "name");

			CArray<Map> templates = selectByPattern(db_templates, "name", search, rows_per_page);
			CArray templateids = rda_objectValues(templates, "templateid");

			templateGet = new CTemplateGet();
			templateGet.setTemplateIds(templateids.valuesAsLong());
			templateGet.setEditable(true);
			CArray<Map> rw_templates = API.Template(getIdentityBean(), executor).get(templateGet);
			rw_templates = rda_toHash(rw_templates, "templateid");

			templateGet = new CTemplateGet();
			templateGet.setSearch("name", search);
			templateGet.setCountOutput(true);
			templateGet.setEditable(true);

			overalCount = API.Template(getIdentityBean(), executor).get(templateGet);
			viewCount = count(templates);

			header = array(
				new CCol(_("Templates")),
				new CCol(_("Applications")),
				new CCol(_("Items")),
				new CCol(_("Triggers")),
				new CCol(_("Graphs")),
				new CCol(_("Screens")),
				new CCol(_("Discovery")),
				new CCol(_("Web"))
			);

			table = new CTableInfo(_("No templates found."));
			table.setHeader(header);

			for (Map template : templates) {
				Long templateid = Nest.value(template,"templateid").asLong();

				Map group = reset(Nest.value(template,"groups").asCArray());
				String link = "groupid="+Nest.value(group,"groupid").$()+"&hostid="+templateid;

				Object caption = make_decoration(Nest.value(template,"name").asString(), search);

				Object template_link = null, applications_link = null, items_link = null, triggers_link = null, graphs_link = null, screensLink = null, discoveryLink = null, httpTestsLink = null;
				if (isset(rw_templates,templateid)) {
					template_link = new CLink(caption, "templates.php?form=update&templateid="+templateid);
					applications_link = array(
						new CLink(_("Applications"), "applications.php?"+link),
						" ("+Nest.value(template,"applications").$()+")"
					);
					items_link = array(
						new CLink(_("Items"), "items.php?filter_set=1&"+link),
						" ("+Nest.value(template,"items").$()+")"
					);
					triggers_link = array(
						new CLink(_("Triggers"), "triggers.php?"+link),
						" ("+Nest.value(template,"triggers").$()+")"
					);
					graphs_link = array(
						new CLink(_("Graphs"), "graphs.php?"+link),
						" ("+Nest.value(template,"graphs").$()+")"
					);
					screensLink = array(
						new CLink(_("Screens"), "screenconf.php?templateid="+templateid),
						" ("+Nest.value(template,"screens").$()+")"
					);
					discoveryLink = array(
						new CLink(_("Discovery"), "host_discovery.php?"+link),
						" ("+Nest.value(template,"discoveries").$()+")"
					);
					httpTestsLink = array(
						new CLink(_("Web"), "httpconf.php?"+link),
						" ("+Nest.value(template,"httpTests").$()+")"
					);
				} else {
					template_link = new CSpan(caption);
					applications_link = _("Applications")+" ("+Nest.value(template,"applications").$()+")";
					items_link = _("Items")+" ("+Nest.value(template,"items").$()+")";
					triggers_link = _("Triggers")+" ("+Nest.value(template,"triggers").$()+")";
					graphs_link = _("Graphs")+" ("+Nest.value(template,"graphs").$()+")";
					screensLink = _("Screens")+" ("+Nest.value(template,"screens").$()+")";
					discoveryLink = _("Discovery")+" ("+Nest.value(template,"discoveries").$()+")";
					httpTestsLink = _("Web")+" ("+Nest.value(template,"httpTests").$()+")";
				}

				table.addRow(array(
					template_link,
					applications_link,
					items_link,
					triggers_link,
					graphs_link,
					screensLink,
					discoveryLink,
					httpTestsLink
				));
			}

			CUIWidget wdgt_templates = new CUIWidget("search_templates", table, Nest.as(CProfile.get(getIdentityBean(), executor,"web.search.hats.search_templates.state", true)).asInteger());
			wdgt_templates.setHeader(_("Templates"), SPACE);
			wdgt_templates.setFooter(_s("Displaying %1s of %2s found", viewCount, overalCount));
			searchWidget.addItem(new CDiv(wdgt_templates));
		}

		searchWidget.show();
	}

}
