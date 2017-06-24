<?php
/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/


require_once dirname(__FILE__)."/include/config.inc.php";
require_once dirname(__FILE__)."/include/hosts.inc.php";
require_once dirname(__FILE__)."/include/html.inc.php";

Nest.value($page,"title").$() = _("Search");
Nest.value($page,"file").$() = "search.php";
Nest.value($page,"hist_arg").$() = CArray.array();
Nest.value($page,"type").$() = detect_page_type(PAGE_TYPE_HTML);

require_once dirname(__FILE__)."/include/page_header.php";

//		VAR				TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
$fields = CArray.array(
	"type"=>		CArray.array(T_ZBX_INT, O_OPT,	P_SYS,	IN("0,1"),		null),
	"search"=>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,			null),
//ajax
	"favobj"=>		CArray.array(T_ZBX_STR, O_OPT, P_ACT,	null,			null),
	"favref"=>		CArray.array(T_ZBX_STR, O_OPT, P_ACT,  NOT_EMPTY,		"isset({favobj})"),
	"favstate"=>	CArray.array(T_ZBX_INT, O_OPT, P_ACT,  NOT_EMPTY,		null),
);

check_fields($fields);

// ACTION /////////////////////////////////////////////////////////////////////////////
if (isset(Nest.value(_REQUEST,"favobj").$())) {
	if ("hat" == Nest.value(_REQUEST,"favobj").$()) {
		CProfile::update("web.search.hats."._REQUEST["favref"].".state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
	}
}

if ((PAGE_TYPE_JS == Nest.value($page,"type").$()) || (PAGE_TYPE_HTML_BLOCK == Nest.value($page,"type").$())) {
	require_once dirname(__FILE__)."/include/page_footer.php";
	exit();
}


$admin = in_CArray.array(CWebUser::Nest.value($data,"type").$(), CArray.array(
	USER_TYPE_ZABBIX_ADMIN,
	USER_TYPE_SUPER_ADMIN
));
$rows_per_page = CWebUser::Nest.value($data,"rows_per_page").$();

$searchWidget = new CWidget("search_wdgt");

$search = get_request("search", "");

// Header
if (zbx_empty($search)) {
	$search = _("Search pattern is empty");
}
$searchWidget.setClass("header");
$searchWidget.addHeader(CArray.array(
	_("SEARCH").NAME_DELIMITER,
	bold($search)
), SPACE);

// FIND Hosts
$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"search" => CArray.array(
		"name" => $search,
		"dns" => $search,
		"ip" => $search
	),
	"limit" => $rows_per_page,
	"selectGroups" => API_OUTPUT_EXTEND,
	"selectInterfaces" => API_OUTPUT_EXTEND,
	"selectItems" => API_OUTPUT_COUNT,
	"selectTriggers" => API_OUTPUT_COUNT,
	"selectGraphs" => API_OUTPUT_COUNT,
	"selectApplications" => API_OUTPUT_COUNT,
	"selectScreens" => API_OUTPUT_COUNT,
	"selectHttpTests" => API_OUTPUT_COUNT,
	"selectDiscoveries" => API_OUTPUT_COUNT,
	"output" => CArray.array("name", "status"),
	"searchByAny" => true
);
$db_hosts = API.Host().get($params);

order_result($db_hosts, "name");

$hosts = selectByPattern($db_hosts, "name", $search, $rows_per_page);
$hostids = zbx_objectValues($hosts, "hostid");

$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"hostids" => $hostids,
	"editable" => 1
);
$rw_hosts = API.Host().get($params);
$rw_hosts = zbx_toHash($rw_hosts, "hostid");

$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"search" => CArray.array(
		"name" => $search,
		"dns" => $search,
		"ip" => $search
	),
	"countOutput" => 1,
	"searchByAny" => true
);

$overalCount = API.Host().get($params);
$viewCount = count($hosts);

$table = new CTableInfo(_("No hosts found."));
$table.setHeader(CArray.array(
	ZBX_DISTRIBUTED ? new CCol(_("Node")) : null,
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

for($hosts as $hnum => $host) {
	$hostid = Nest.value($host,"hostid").$();

	$interface = reset(Nest.value($host,"interfaces").$());
	Nest.value($host,"ip").$() = Nest.value($interface,"ip").$();
	Nest.value($host,"dns").$() = Nest.value($interface,"dns").$();
	Nest.value($host,"port").$() = Nest.value($interface,"port").$();

	$style = Nest.value($host,"status").$() == HOST_STATUS_NOT_MONITORED ? "on" : null;

	$group = reset(Nest.value($host,"groups").$());
	$link = "groupid=".$group["groupid"]."&hostid=".$hostid."&switch_node=".id2nodeid($hostid);

	$caption = make_decoration(Nest.value($host,"name").$(), $search);

	if ($admin && isset($rw_hosts[$hostid])) {
		$host_link = new CLink($caption, "hosts.php?form=update&".$link, $style);
		$applications_link = CArray.array(
			new CLink(_("Applications"), "applications.php?".$link),
			" (".$host["applications"].")"
		);
		$items_link = CArray.array(
			new CLink(_("Items"), "items.php?filter_set=1&".$link),
			" (".$host["items"].")"
		);
		$triggers_link = CArray.array(
			new CLink(_("Triggers"), "triggers.php?".$link),
			" (".$host["triggers"].")"
		);
		$graphs_link = CArray.array(
			new CLink(_("Graphs"), "graphs.php?".$link),
			" (".$host["graphs"].")"
		);
		$discoveryLink = CArray.array(
			new CLink(_("Discovery"), "host_discovery.php?".$link),
			" (".$host["discoveries"].")"
		);
		$httpTestsLink = CArray.array(
			new CLink(_("Web"), "httpconf.php?".$link),
			" (".$host["httpTests"].")"
		);
	}
	else {
		$host_link = new CSpan($caption, $style);
		$applications_link = _("Applications")." (".$host["applications"].")";
		$items_link = _("Items")." (".$host["items"].")";
		$triggers_link = _("Triggers")." (".$host["triggers"].")";
		$graphs_link = _("Graphs")." (".$host["graphs"].")";
		$discoveryLink = _("Discovery")." (".$host["discoveries"].")";
		$httpTestsLink = _("Web")." (".$host["httpTests"].")";
	}

	$hostip = make_decoration(Nest.value($host,"ip").$(), $search);
	$hostdns = make_decoration(Nest.value($host,"dns").$(), $search);

	$table.addRow(CArray.array(
		get_node_name_by_elid($hostid, true),
		$host_link,
		$hostip,
		$hostdns,
		new CLink(_("Latest data"), "latest.php?".$link),
		new CLink(_("Triggers"), "tr_status.php?".$link),
		new CLink(_("Events"), "events.php?source=".EVENT_SOURCE_TRIGGERS."&".$link),
		new CLink(_("Graphs"), "charts.php?".$link),
		new CLink(_("Screens"), "host_screen.php?hostid=".$hostid),
		new CLink(_("Web"), "httpmon.php?".$link),
		$applications_link,
		$items_link,
		$triggers_link,
		$graphs_link,
		$discoveryLink,
		$httpTestsLink
	));
}

$sysmap_menu = get_icon("menu", CArray.array("menu" => "sysmaps"));

$wdgt_hosts = new CUIWidget("search_hosts", $table, CProfile::get("web.search.hats.search_hosts.state", true));
$wdgt_hosts.setHeader(_("Hosts"), SPACE);
$wdgt_hosts.setFooter(_s("Displaying %1$s of %2$s found", $viewCount, $overalCount));

$searchWidget.addItem(new CDiv($wdgt_hosts));
//----------------


// Find Host groups
$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"output" => API_OUTPUT_EXTEND,
	"selectHosts" => API_OUTPUT_COUNT,
	"selectTemplates" => API_OUTPUT_COUNT,
	"search" => CArray.array("name" => $search),
	"limit" => $rows_per_page
);
$db_hostGroups = API.HostGroup().get($params);
order_result($db_hostGroups, "name");

$hostGroups = selectByPattern($db_hostGroups, "name", $search, $rows_per_page);
$groupids = zbx_objectValues($hostGroups, "groupid");

$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"groupids" => $groupids,
	"editable" => true
);
$rw_hostGroups = API.HostGroup().get($params);
$rw_hostGroups = zbx_toHash($rw_hostGroups, "groupid");

$params = CArray.array(
	"nodeids" => get_current_nodeid(true),
	"search" => CArray.array("name" => $search),
	"countOutput" => 1
);
$overalCount = API.HostGroup().get($params);
$viewCount = count($hostGroups);

$header = CArray.array(
	ZBX_DISTRIBUTED ? new CCol(_("Node")) : null,
	new CCol(_("Host group")),
	new CCol(_("Latest data")),
	new CCol(_("Triggers")),
	new CCol(_("Events")),
	new CCol(_("Graphs")),
	new CCol(_("Web")),
	$admin ? new CCol(_("Hosts")) : null,
	$admin ? new CCol(_("Templates")) : null,
);

$table = new CTableInfo(_("No host groups found."));
$table.setHeader($header);

for($hostGroups as $hnum => $group) {
	$hostgroupid = Nest.value($group,"groupid").$();

	$caption = make_decoration(Nest.value($group,"name").$(), $search);
	$link = "groupid=".$hostgroupid."&hostid=0&switch_node=".id2nodeid($hostgroupid);

	$hostsLink = null;
	$templatesLink = null;
	$hgroup_link = new CSpan($caption);
	if ($admin) {
		if (isset($rw_hostGroups[$hostgroupid])) {
			if (Nest.value($group,"hosts").$()) {
				$hostsLink = CArray.array(
					new CLink(_("Hosts"), "hosts.php?groupid=".$hostgroupid."&switch_node=".id2nodeid($hostgroupid)),
					" (".$group["hosts"].")"
				);
			}
			else {
				$hostsLink = _("Hosts")." (0)";
			}

			if (Nest.value($group,"templates").$()) {
				$templatesLink = CArray.array(
					new CLink(_("Templates"), "templates.php?groupid=".$hostgroupid."&switch_node=".id2nodeid($hostgroupid)),
					" (".$group["templates"].")"
				);
			}
			else {
				$templatesLink = _("Templates")." (0)";
			}

			$hgroup_link = new CLink($caption, "hostgroups.php?form=update&".$link);
		}
		else {
			$hostsLink = _("Hosts");
			$templatesLink = _("Templates");
		}
	}

	$table.addRow(CArray.array(
		get_node_name_by_elid($hostgroupid, true),
		$hgroup_link,
		new CLink(_("Latest data"), "latest.php?".$link),
		new CLink(_("Triggers"), "tr_status.php?".$link),
		new CLink(_("Events"), "events.php?source=".EVENT_SOURCE_TRIGGERS."&".$link),
		new CLink(_("Graphs"), "charts.php?".$link),
		new CLink(_("Web"), "httpmon.php?".$link),
		$hostsLink,
		$templatesLink
	));
}

$wdgt_hgroups = new CUIWidget("search_hostgroup", $table, CProfile::get("web.search.hats.search_hostgroup.state", true));
$wdgt_hgroups.setHeader(_("Host groups"), SPACE);
$wdgt_hgroups.setFooter(_s("Displaying %1$s of %2$s found", $viewCount, $overalCount));

$searchWidget.addItem(new CDiv($wdgt_hgroups));
//----------------

// FIND Templates
if ($admin) {
	$params = CArray.array(
		"nodeids" => get_current_nodeid(true),
		"search" => CArray.array("name" => $search),
		"output" => CArray.array("name"),
		"selectGroups" => API_OUTPUT_REFER,
		"sortfield" => "name",
		"selectItems" => API_OUTPUT_COUNT,
		"selectTriggers" => API_OUTPUT_COUNT,
		"selectGraphs" => API_OUTPUT_COUNT,
		"selectApplications" => API_OUTPUT_COUNT,
		"selectScreens" => API_OUTPUT_COUNT,
		"selectHttpTests" => API_OUTPUT_COUNT,
		"selectDiscoveries" => API_OUTPUT_COUNT,
		"limit" => $rows_per_page
	);
	$db_templates = API.Template().get($params);
	order_result($db_templates, "name");

	$templates = selectByPattern($db_templates, "name", $search, $rows_per_page);
	$templateids = zbx_objectValues($templates, "templateid");

	$params = CArray.array(
		"nodeids" => get_current_nodeid(true),
		"templateids" => $templateids,
		"editable" => 1
	);
	$rw_templates = API.Template().get($params);
	$rw_templates = zbx_toHash($rw_templates, "templateid");

	$params = CArray.array(
		"nodeids" => get_current_nodeid(true),
		"search" => CArray.array("name" => $search),
		"countOutput" => 1,
		"editable" => 1
	);

	$overalCount = API.Template().get($params);
	$viewCount = count($templates);

	$header = CArray.array(
		ZBX_DISTRIBUTED ? new CCol(_("Node")) : null,
		new CCol(_("Templates")),
		new CCol(_("Applications")),
		new CCol(_("Items")),
		new CCol(_("Triggers")),
		new CCol(_("Graphs")),
		new CCol(_("Screens")),
		new CCol(_("Discovery")),
		new CCol(_("Web")),
	);

	$table = new CTableInfo(_("No templates found."));
	$table.setHeader($header);

	for($templates as $tnum => $template) {
		$templateid = Nest.value($template,"templateid").$();

		$group = reset(Nest.value($template,"groups").$());
		$link = "groupid=".$group["groupid"]."&hostid=".$templateid."&switch_node=".id2nodeid($templateid);

		$caption = make_decoration(Nest.value($template,"name").$(), $search);

		if (isset($rw_templates[$templateid])) {
			$template_link = new CLink($caption, "templates.php?form=update&"."&templateid=".$templateid."&switch_node=".id2nodeid($templateid));
			$applications_link = CArray.array(
				new CLink(_("Applications"), "applications.php?".$link),
				" (".$template["applications"].")"
			);
			$items_link = CArray.array(
				new CLink(_("Items"), "items.php?filter_set=1&".$link),
				" (".$template["items"].")"
			);
			$triggers_link = CArray.array(
				new CLink(_("Triggers"), "triggers.php?".$link),
				" (".$template["triggers"].")"
			);
			$graphs_link = CArray.array(
				new CLink(_("Graphs"), "graphs.php?".$link),
				" (".$template["graphs"].")"
			);
			$screensLink = CArray.array(
				new CLink(_("Screens"), "screenconf.php?templateid=".$templateid),
				" (".$template["screens"].")"
			);
			$discoveryLink = CArray.array(
				new CLink(_("Discovery"), "host_discovery.php?".$link),
				" (".$template["discoveries"].")"
			);
			$httpTestsLink = CArray.array(
				new CLink(_("Web"), "httpconf.php?".$link),
				" (".$template["httpTests"].")"
			);
		}
		else {
			$template_link = new CSpan($caption);
			$applications_link = _("Applications")." (".$template["applications"].")";
			$items_link = _("Items")." (".$template["items"].")";
			$triggers_link = _("Triggers")." (".$template["triggers"].")";
			$graphs_link = _("Graphs")." (".$template["graphs"].")";
			$screensLink = _("Screens")." (".$template["screens"].")";
			$discoveryLink = _("Discovery")." (".$template["discoveries"].")";
			$httpTestsLink = _("Web")." (".$template["httpTests"].")";
		}

		$table.addRow(CArray.array(
			get_node_name_by_elid($templateid, true),
			$template_link,
			$applications_link,
			$items_link,
			$triggers_link,
			$graphs_link,
			$screensLink,
			$discoveryLink,
			$httpTestsLink
		));
	}

	$wdgt_templates = new CUIWidget("search_templates", $table, CProfile::get("web.search.hats.search_templates.state", true));
	$wdgt_templates.setHeader(_("Templates"), SPACE);
	$wdgt_templates.setFooter(_s("Displaying %1$s of %2$s found", $viewCount, $overalCount));
	$searchWidget.addItem(new CDiv($wdgt_templates));
}
//----------------

$searchWidget.show();

require_once dirname(__FILE__)."/include/page_footer.php";
