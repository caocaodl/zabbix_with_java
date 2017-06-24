package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.addslashes;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_INTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SIMPLE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_SERVER;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check2str;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicator;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.ItemsUtil.itemValueTypeString;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.insert_js_function;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ScreensUtil.check_screen_recursion;
import static com.isoft.iradar.inc.ScreensUtil.slideshow_accessible;
import static com.isoft.iradar.inc.BlocksUtil.getTriggerLevel;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicator;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicatorStyle;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.invalid_url;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.Feature;
import com.isoft.biz.daoimpl.platform.topo.TopoDAO;
import com.isoft.biz.vo.platform.topo.Topo;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.items.CHelpItems;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupAction extends RadarBaseAction {

	private String srctbl;
	private Integer min_user_type;
	private Boolean error;
	private Integer dstfldCount;
	private Integer srcfldCount;
	private CArray<Integer> allowed_item_types;

	@Override
	protected void doInitPage() {
		// source table name
		srctbl = get_request("srctbl", "");

		// set page title
		if ("hosts".equals(srctbl)) {
			Nest.value(page, "title").$(_("Host"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("templates".equals(srctbl)) {
			Nest.value(page, "title").$(_("Templates"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
			page("css", new String[] {"lessor/dialog/hostgroups.css"});
		} else if ("host_templates".equals(srctbl)) {
			Nest.value(page, "title").$(_("Hosts"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("host_groups".equals(srctbl)) {
			Nest.value(page, "title").$(_("Host groups"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/hostgroups.css"});
		} else if ("proxies".equals(srctbl)) {
			Nest.value(page, "title").$(_("Proxies"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("applications".equals(srctbl)) {
			Nest.value(page, "title").$(_("Applications"));
			min_user_type = USER_TYPE_IRADAR_USER;
		} else if ("triggers".equals(srctbl)) {
//			Nest.value(page, "title").$(_("Triggers"));
			Nest.value(page, "title").$(_("PopupTriggers"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/triggers.css"});
			
		} else if ("usrgrp".equals(srctbl)) {
			Nest.value(page, "title").$(_("User groups"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("users".equals(srctbl)) {
			Nest.value(page, "title").$(_("Users"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
			page("css", new String[] {"lessor/dialog/hostgroups.css"});
		} else if ("items".equals(srctbl)) {
			Nest.value(page, "title").$(_("Items"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/items.css"});			
		} else if ("prototypes".equals(srctbl)) {
			Nest.value(page, "title").$(_("Prototypes"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("help_items".equals(srctbl)) {
			Nest.value(page, "title").$(_("Standard items"));
			min_user_type = USER_TYPE_IRADAR_USER;
		} else if ("screens".equals(srctbl)) {
			Nest.value(page, "title").$(_("Screens"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/hostgroups.css"});
		} else if ("slides".equals(srctbl)) {
			Nest.value(page, "title").$(_("Slide shows"));
			min_user_type = USER_TYPE_IRADAR_USER;
		} else if ("graphs".equals(srctbl)) {
			Nest.value(page, "title").$(_("Graphs"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/interface.css"});		
		} else if ("sysmaps".equals(srctbl)) {
			Nest.value(page, "title").$(_("Maps"));
			min_user_type = USER_TYPE_IRADAR_USER;
			page("css", new String[] {"lessor/dialog/hostgroups.css"});
		} else if ("screens2".equals(srctbl)) {
			Nest.value(page, "title").$(_("Screens"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("nodes".equals(srctbl)) {
			Nest.value(page, "title").$(_("Nodes"));
			min_user_type = USER_TYPE_IRADAR_USER;
		} else if ("drules".equals(srctbl)) {
			Nest.value(page, "title").$(_("Discovery rules"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("dchecks".equals(srctbl)) {
			Nest.value(page, "title").$(_("Discovery checks"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;
		} else if ("scripts".equals(srctbl)) {
			Nest.value(page, "title").$(_("Global scripts"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;			
			page("css", new String[] {"lessor/dialog/scripts.css"});
		} else if("tenant".equals(srctbl)){
			Nest.value(page, "title").$(_("usermessage"));
			min_user_type = USER_TYPE_IRADAR_ADMIN;			//含义待定！
		} else if ("interface".equals(srctbl)) {
			Nest.value(page, "title").$("接口");
			min_user_type = USER_TYPE_IRADAR_ADMIN;
			page("css", new String[] {"lessor/dialog/interface.css"});			
		} else {
			Nest.value(page, "title").$(_("Error"));
			error = true;
		}

		Nest.value(page, "file").$("popup.action");
		define("RDA_PAGE_NO_MENU", 1);
		
		if (isset(error)) {
			invalid_url(getIdentityBean());
		}
		if (min_user_type > Nest.as(CWebUser.get("type")).asInteger()) {
			access_deny();
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// allowed "srcfld*" parameter values for each "srctbl" value
		CArray allowedSrcFields = map(
				"users", "\"usergrpid\", \"id\", \"fullname\",\"alias\", \"userid\"",
				"triggers", "\"description\", \"triggerid\", \"expression\"",
				"items", "\"itemid\", \"name\"",
				"prototypes", "\"itemid\", \"name\", \"flags\"",
				"graphs", "\"graphid\", \"name\"", 
				"sysmaps", "\"sysmapid\", \"name\"",
				"slides", "\"slideshowid\"",
				"help_items", "\"key\"",
				"screens", "\"screenid\"",
				"screens2", "\"screenid\", \"name\"",
				"nodes", "\"nodeid\", \"name\"",
				"drules", "\"druleid\", \"name\"",
				"dchecks", "\"dcheckid\", \"name\"",
				"proxies", "\"hostid\", \"host\"",
				"usrgrp", "\"usrgrpid\", \"name\"",
				"applications",	"\"applicationid\", \"name\"",
				"scripts", "\"scriptid\", \"name\"",
				"hosts", "\"hostid\", \"host\"",
				"templates", "\"hostid\", \"host\"",
				"host_templates", "\"hostid\", \"host\"",
				"host_groups", "\"groupid\", \"name\"",
				"tenant",  "\"tenantid\", \"tenant\", \"id\", \"name\"",
				"interface", "\"interfaceid\", \"interface\", \"-\""
		);
		
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"dstfrm",					array(T_RDA_STR, O_OPT, P_SYS,	NOT_EMPTY,	"!isset({multiselect})"),
			"dstfld1",					array(T_RDA_STR, O_OPT, P_SYS,	NOT_EMPTY,	"!isset({multiselect})"),
			"srctbl",					array(T_RDA_STR, O_MAND, P_SYS,	NOT_EMPTY,	null),
			"srcfld1",					array(T_RDA_STR, O_MAND, P_SYS,	IN(allowedSrcFields.get(srctbl)), null),
			"nodeid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groupid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"tenantName",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"keyname",				 	array(T_RDA_STR, O_OPT, null,	null,		null),
			"group",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"group_with_template",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"hostid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"host",						array(T_RDA_STR, O_OPT, null,	null,		null),
			"parent_discoveryid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"screenid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"templates",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null),
			"host_templates",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null),
			"multiselect",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"submit",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"excludeids",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"only_hostid",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"monitored_hosts",			array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"templated_hosts",			array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"real_hosts",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"normal_only",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_applications",		array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_graphs",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_items",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_simple_graph_items",	array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_triggers",			array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"with_monitored_triggers",	array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"itemtype",					array(T_RDA_INT, O_OPT, null,	null,		null),
			"value_types",				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 15), null),
			"numeric",					array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"reference",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"writeonly",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"noempty",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"select",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"submitParent",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"eventType",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"addressFlag",				array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		
		// unset disabled item types
		allowed_item_types = array(ITEM_TYPE_IRADAR, ITEM_TYPE_IRADAR_ACTIVE, ITEM_TYPE_SIMPLE, ITEM_TYPE_INTERNAL, ITEM_TYPE_AGGREGATE, ITEM_TYPE_SNMPTRAP);
		if (isset(Nest.value(_REQUEST,"itemtype").$()) && !str_in_array(Nest.value(_REQUEST,"itemtype").$(), allowed_item_types)) {
			_REQUEST.remove("itemtype");
		}
		
		// set destination/source fields
		dstfldCount = countRequest("dstfld");
		for (int i = 2; i <= dstfldCount; i++) {
			Nest.value(fields, "dstfld"+i).$(array(T_RDA_STR, O_OPT, P_SYS, null, null));
		}
		srcfldCount = countRequest("srcfld");
		for (int i = 2; i <= srcfldCount; i++) {
			Nest.value(fields, "srcfld"+i).$(array(T_RDA_STR, O_OPT, P_SYS, IN(allowedSrcFields.get(srctbl)), null));
		}
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("only_hostid"))) {
			if (!API.Host(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"only_hostid").asLong())) {
				access_deny();
			}
		} else {
			if ((!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"hostid").asLong()))
					|| (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"groupid").asLong()))) {
				access_deny();
			}
		}
		if (!empty(get_request("parent_discoveryid")) && !API.DiscoveryRule(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"parent_discoveryid").asLong())) {
			access_deny();
		}
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		String dstfrm = get_request("dstfrm", ""); // destination form
		String dstfld1 = get_request("dstfld1", ""); // output field on destination form
		String dstfld2 = get_request("dstfld2", ""); // second output field on destination form
		String dstfld3 = get_request("dstfld3", ""); // third output field on destination form
		String srcfld1 = get_request("srcfld1", ""); // source table field [can be different from fields of source table]
		String srcfld2 = get_request("srcfld2", null); // second source table field [can be different from fields of source table]
		String srcfld3 = get_request("srcfld3", null); //  source table field [can be different from fields of source table]
		int multiselect = get_request("multiselect", 0); // if create popup with checkboxes
		String dstact = get_request("dstact", "");
		String writeonly = get_request("writeonly");
		int withApplications = get_request("with_applications", 0);
		int withGraphs = get_request("with_graphs", 0);
		int withItems = get_request("with_items", 0);
		String noempty = get_request("noempty"); // display/hide \"Empty\" button
		CArray excludeids = rda_toHash(get_request("excludeids", array()));
		String reference = get_request("reference", get_request("srcfld1", "unknown"));
		int realHosts = get_request("real_hosts", 0);
		int monitoredHosts = get_request("monitored_hosts", 0);
		int templatedHosts = get_request("templated_hosts", 0);
		int withSimpleGraphItems = get_request("with_simple_graph_items", 0);
		int withTriggers = get_request("with_triggers", 0);
		int withMonitoredTriggers = get_request("with_monitored_triggers", 0);
		int submitParent = get_request("submitParent", 0);
		String normalOnly = get_request("normal_only");
		String group = get_request("group", "");
		String host = get_request("host", "");
		String onlyHostid = get_request("only_hostid", null);
		String keyName = get_request("keyname", null);
		if (isset(onlyHostid)) {
			Nest.value(_REQUEST,"hostid").$(onlyHostid);
			_REQUEST.remove("groupid");
			_REQUEST.remove("nodeid");
		}
		
		// value types
		Object value_types = null;
		if (!empty(get_request("value_types"))) {
			value_types = Nest.value(_REQUEST,"value_types").$();
		} else if (!empty(get_request("numeric"))) {
			value_types = array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64);
		}
		
		clearCookies(true);

		/* Page filter */
		if (!empty(group)) {
			Map params = new HashMap();
			params.put("groupname", group);
			String sql = "SELECT g.groupid FROM groups g WHERE g.name=#{groupname}";
			List<String> dbGroup = executor.executeNameParaQuery(sql, params, String.class);
			if (dbGroup != null && !dbGroup.isEmpty()) {
				Nest.value(_REQUEST, "groupid").$(dbGroup.get(0));
			}
		}
		
		if(Cphp.in_array(srctbl, array("templates"))) {
			Nest.value(_REQUEST, "groupid").$(IMonConsts.TEMPLATES);
		}
		
		if (!empty(host)) {
			Map params = new HashMap();
			params.put("hostname", host);
			String sql = "SELECT h.hostid FROM hosts h WHERE h.name=#{hostname}";
			List<String> dbHost = executor.executeNameParaQuery(sql, params, String.class);
			if (dbHost != null && !dbHost.isEmpty()) {
				Nest.value(_REQUEST, "hostid").$(dbHost.get(0));
			}
		}
		
		CArray options = map(
			"config", map("select_latest", true, "deny_all", true, "popupDD", true),
			"groups", map(),
			"hosts", map(),
			"groupid", get_request("groupid", null),
			"hostid", get_request("hostid", null)
		);
		
		if (!is_null(writeonly)) {
			Nest.value(options, "groups", "editable").$(true);
			Nest.value(options, "hosts", "editable").$(true);
		}
		
		//String host_status = null;
		Integer templated = null;
		
		if (monitoredHosts > 0) {
			Nest.value(options, "groups", "monitored_hosts").$(true);
			Nest.value(options, "hosts", "monitored_hosts").$(true);
			//host_status = "monitored_hosts";
		} else if (realHosts > 0) {
			Nest.value(options, "groups", "real_hosts").$(true);
			templated = 0;
		} else if (templatedHosts > 0) {
			Nest.value(options, "hosts", "templated_hosts").$(true);
			Nest.value(options, "groups", "templated_hosts").$(true);
			templated = 1;
			//host_status = "templated_hosts";
		} else {
			Nest.value(options,"groups","with_hosts_and_templates").$(true);
			Nest.value(options,"hosts","templated_hosts").$(true); // for hosts templated_hosts comes with monitored and not monitored hosts
		}
		
		if (withApplications > 0) {
			Nest.value(options, "groups", "with_applications").$(true);
			Nest.value(options, "hosts", "with_applications").$(true);
		} else if (withGraphs > 0) {
			Nest.value(options, "groups", "with_graphs").$(true);
			Nest.value(options, "hosts", "with_graphs").$(true);
		} else if (withSimpleGraphItems > 0) {
			Nest.value(options, "groups", "with_simple_graph_items").$(true);
			Nest.value(options, "hosts", "with_simple_graph_items").$(true);
		} else if (withTriggers > 0) {
			Nest.value(options, "groups", "with_triggers").$(true);
			Nest.value(options, "hosts", "with_triggers").$(true);
		} else if (withMonitoredTriggers > 0) {
			Nest.value(options, "groups", "with_monitored_triggers").$(true);
			Nest.value(options, "hosts", "with_monitored_triggers").$(true);
		}
		
		if(Nest.value(_REQUEST, "group_with_template").asBoolean()) {
			Nest.value(options, "groups", CPageFilter.KEY_GROUP_SHOW_TEMPLATE).$(true);
		}
		
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, options);
		
		// get groupid
		Long groupid = null;
		if (pageFilter.$("groupsSelected").asBoolean()) {
			if (pageFilter.$("groupid").asLong() > 0L) {
				groupid = pageFilter.$("groupid").asLong();
					if("hosts".equals(srctbl) && groupid==19l){
						groupid = 0L;
					}
			}
		} else {
			groupid = 0L;
		}
		
		// get hostid
		Long hostid = null;
		if (pageFilter.$("hostsSelected").asBoolean()) {
			if (pageFilter.$("hostid").asLong() > 0L) {
				hostid = pageFilter.$("hostid").asLong();
			}
		} else {
			hostid = 0L;
		}
		
		if (isset(onlyHostid)) {
			hostid = Nest.as(onlyHostid).asLong();
		}
		
		/* Display table header */
		CForm frmTitle = new CForm();
		if (monitoredHosts > 0) {
			frmTitle.addVar("monitored_hosts", 1);
		}
		if (realHosts > 0) {
			frmTitle.addVar("real_hosts", 1);
		}
		if (templatedHosts > 0) {
			frmTitle.addVar("templated_hosts", 1);
		}
		if (withApplications > 0) {
			frmTitle.addVar("with_applications", 1);
		}
		if (withGraphs > 0) {
			frmTitle.addVar("with_graphs", 1);
		}
		if (withItems > 0) {
			frmTitle.addVar("with_items", 1);
		}
		if (withSimpleGraphItems > 0) {
			frmTitle.addVar("with_simple_graph_items", 1);
		}
		if (withTriggers > 0) {
			frmTitle.addVar("with_triggers", 1);
		}
		if (withMonitoredTriggers > 0) {
			frmTitle.addVar("with_monitored_triggers", 1);
		}
		if (value_types!=null) {
			frmTitle.addVar("value_types", value_types);
		}
		if (normalOnly!=null) {
			frmTitle.addVar("normal_only", normalOnly);
		}
		if (hasRequest("excludeids")) {
			frmTitle.addVar("excludeids", get_request("excludeids"));
		}
		if (isset(onlyHostid)) {
			frmTitle.addVar("only_hostid", onlyHostid);
		}
		if (!empty(get_request("screenid"))) {
			frmTitle.addVar("screenid", get_request("screenid"));
		}
		if (!empty(get_request("eventType"))) {
			frmTitle.addVar("eventType", get_request("eventType"));
		}
		if (!empty(get_request("keyname"))) {
			frmTitle.addVar("keyname", get_request("keyname"));
		}
		
		// adding param to a form, so that it would remain when page is refreshed
		frmTitle.addVar("dstfrm", dstfrm);
		frmTitle.addVar("dstact", dstact);
		frmTitle.addVar("srctbl", srctbl);
		frmTitle.addVar("multiselect", multiselect);
		frmTitle.addVar("writeonly", writeonly);
		frmTitle.addVar("reference", reference);
		frmTitle.addVar("submitParent", submitParent);
		frmTitle.addVar("noempty", noempty);
		
		for (int i = 1; i <= dstfldCount; i++) {
			frmTitle.addVar("dstfld" + i, get_request("dstfld" + i));
		}
		for (int i = 1; i <= srcfldCount; i++) {
			frmTitle.addVar("srcfld" + i, get_request("srcfld" + i));
		}
		
		int itemtype = 0;
		/* Only host id */
		if (isset(onlyHostid)) {
			CHostGet hostGet = new CHostGet();
			hostGet.setHostIds(hostid);
			hostGet.setTemplatedHosts(true);
			hostGet.setOutput(new String[]{"hostid", "host"});
			hostGet.setLimit(1);
			CArray<Map>only_hosts = API.Host(getIdentityBean(), executor).get(hostGet);
			Map dbhost = reset(only_hosts);

			CComboBox cmbHosts = new CComboBox("hostid", hostid);
			cmbHosts.addItem(hostid, Nest.value(dbhost,"host").asString());
			cmbHosts.setEnabled(false);
			cmbHosts.setAttribute("title", _("You can not switch hosts for current selection."));
			frmTitle.addItem(array(SPACE, _("Host"), SPACE, cmbHosts));
		} else {
			if (str_in_array(srctbl, array("triggers", "items", "applications", "graphs", "hosts", "host_templates"))) {
				//阀值规则添加名称搜索按钮
				if("items".equals(srctbl)){					
					CInput keyname = new CInput("text", "keyname",get_request("keyname"));
					frmTitle.addItem(array(_("keyName"), SPACE, keyname));
					CSubmit submitButton = new CSubmit("keyNameSearch",_("GoFilter"));
					frmTitle.addItem(submitButton);
				}
				frmTitle.addItem(array(_("Group"), SPACE, pageFilter.getGroupsCB()));
				
				
			}
			if (str_in_array(srctbl, array("help_items"))) {
				itemtype = get_request("itemtype", 0);
				CComboBox cmbTypes = new CComboBox("itemtype", itemtype, "javascript: submit();");
				
				for (Integer type : allowed_item_types) {
					if(Feature.itemTypePopupNeed.containsValue(type))
						cmbTypes.addItem(type, item_type2str(type));
				}
				frmTitle.addItem(array(_("Type"), SPACE, cmbTypes));
			}
			if (str_in_array(srctbl, array("triggers", "items", "applications", "graphs"))) {
				frmTitle.addItem(array(SPACE, _("Host"), SPACE, pageFilter.getHostsCB()));
			}
			
			
		}
		
		if (str_in_array(srctbl, array("applications", "triggers"))) {
			if (rda_empty(noempty)) {
				Object value1 = isset(Nest.value(_REQUEST,"dstfld1").$()) && rda_strpos(Nest.value(_REQUEST,"dstfld1").asString(), "id") >-1 ? 0 : "";
				Object value2 = isset(Nest.value(_REQUEST,"dstfld2").$()) && rda_strpos(Nest.value(_REQUEST,"dstfld2").asString(), "id") >-1 ? 0 : "";
				Object value3 = isset(Nest.value(_REQUEST,"dstfld3").$()) && rda_strpos(Nest.value(_REQUEST,"dstfld3").asString(), "id") >-1 ? 0 : "";

				String epmtyScript = get_window_opener(dstfrm, dstfld1, value1);
				epmtyScript += get_window_opener(dstfrm, dstfld2, value2);
				epmtyScript += get_window_opener(dstfrm, dstfld3, value3);
				epmtyScript += " close_window(); return false;";
				if(!"tenant".equals(srctbl)){				
					frmTitle.addItem(array(SPACE, new CButton("empty", _("Empty"), epmtyScript)));
				}
			}
		}
		
		show_table_header(new CSpan(Nest.value(page,"title").$(), "headeractions_hide"), frmTitle);
		
		insert_js_function("addSelectedValues");
		insert_js_function("addValues");
		insert_js_function("addValue");
		
		if ("usrgrp".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("usrgrpform");
			form.setAttribute("id", "usrgrps");
			
			CTableInfo table = new CTableInfo(_("No user groups found."));
			table.setHeader(array(
				multiselect>0 ? new CCheckBox("all_usrgrps", false, "javascript: checkAll(\""+form.getName()+"\", \"all_usrgrps\", \"usrgrps\");") : null,
				_("Name")
			));
			
			CUserGroupGet params = new CUserGroupGet();
			params.setOutput(API_OUTPUT_EXTEND);
			params.setPreserveKeys(true);
			params.setSortfield("name");
			
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			
			CArray<Map> userGroups = API.UserGroup(getIdentityBean(), executor).get(params);
			if (userGroups != null && !userGroups.isEmpty()) {
				for (Map userGroup : userGroups) {
					CSpan name = new CSpan(Nest.value(userGroup,"name").$(), "link");
					name.attr("id", "spanid"+Nest.value(userGroup,"usrgrpid").$());
					
					String js_action = null;
					if (multiselect>0) {
						js_action = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(userGroup,"usrgrpid").$())+");";
					} else {
						CArray values = map(
							dstfld1, userGroup.get(srcfld1),
							dstfld2, userGroup.get(srcfld2)
						);
						js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
					}
					name.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");
					
					table.addRow(array(
						multiselect>0 ? new CCheckBox("usrgrps["+userGroup.get("usrgrpid")+"]", false, null, Nest.value(userGroup,"usrgrpid").asString()) : null,
						name
					));
				}
			}
			
			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"usrgrps\", "+rda_jsvalue(reference)+");");
				table.setFooter(new CCol(button, "right"));
				insert_js("var popupReference = "+rda_jsvalue(userGroups, true, true)+";");
			}
			
			rda_add_post_js("chkbxRange.pageGoName = \"usrgrps\";");
			form.addItem(table);
			form.show();
		} else if ("users".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("userform");
			form.setAttribute("id", "users");

			CTableInfo table = new CTableInfo(_("No users found."));
			if(!empty(get_request("addressFlag",null))){
				String tabClass = table.getAttribute("class").toString();
				tabClass += " systemlog";
				table.setAttribute("class", tabClass);
			}
			table.setHeader(array(
				new CCol((multiselect>0 ? new CCheckBox("all_users", false, "javascript: checkAll(\""+form.getName()+"\", \"all_users\", \"users\");") : null), null, null,""),
				new CCol(_("Alias"), null, null, "")
			));
			
			CUserGet params = new CUserGet();
			params.setOutput(new String[]{"alias", "name", "surname", "type", "theme", "lang"});
			params.setPreserveKeys(true);
			params.setSortfield("alias");
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			CArray<Map> users = API.User(getIdBean(), executor).get(params);
			if(users!=null && !users.isEmpty()){
				for (Map user : users) {
					CSpan alias = new CSpan(Nest.value(user,"alias").$(), "link");
					alias.attr("id", "spanid"+Nest.value(user,"userid").$());
					
					if (isset(srcfld2) && "fullname".equals(srcfld2)) {
						Nest.value(user, srcfld2).$(Nest.value(user,"alias").$());
					}
					
					String js_action = null;
					if (multiselect>0) {
						js_action  = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(user.get(srcfld1))+");";
					} else {
						CArray values = map(
							dstfld1 , user.get(srcfld1)
						);
						if (isset(srcfld2)) {
							Nest.value(values, dstfld2).$(Nest.value(user,"alias").$());
						}
						js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
					}
					alias.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");
					
					table.addRow(array(
						multiselect>0 ? new CCheckBox("users["+rda_jsvalue(user.get("userid"))+"]", false, null, Nest.value(user,"userid").asString()) : null,
						alias
					));
				}
			}
			
			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"users\", "+rda_jsvalue(reference)+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(users, true, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"users\";");

			form.addItem(table);
			form.show();
		} else if ("templates".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("templateform");
			form.setAttribute("id", "templates");

			CTableInfo table = new CTableInfo(_("No templates found."));
			table.setHeader(array((multiselect>0 ? new CCheckBox("allTemplates", false,
				"javascript: checkAll(\""+form.getName()+"\", \"allTemplates\", \"templates\");") : null), _("Name")
			));

			CTemplateGet params = new CTemplateGet();
			params.setOutput(new String[]{"templateid", "name"});
			params.setGroupIds(groupid);
			params.setPreserveKeys(true);
			params.setSortfield("name");

			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}

			CArray<Map> templates = API.Template(getIdentityBean(), executor).get(params);

			CArray data = array();
			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			if (templates != null && !templates.isEmpty()) {
				for(Map template:templates) {
					String templatename = Nest.value(template,"name").asString();
					if(!Feature.hostGroupsNeedHide.containsValue(templatename)){
						CSpan name = new CSpan(templatename, "link");
						name.attr("id", "spanid"+Nest.value(template,"templateid").$());
		
						String jsAction = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(template,"templateid").$())+", "+ parentId+");";
		
						CCheckBox checkBox = null;
						if (multiselect>0) {
							checkBox  = new CCheckBox("templates["+rda_jsvalue("templateid")+"]", false, null, Nest.value(template,"templateid").asString());
						}
		
						// check for existing
						if (isset(excludeids.get(template.get("templateid")))) {
							if (multiselect>0) {
								checkBox.setChecked(true);
								checkBox.setEnabled("disabled");
							}
							name.removeAttr("class");
						} else {
							name.setAttribute("onclick", jsAction+" jQuery(this).removeAttr(\"onclick\");");
		
							Nest.value(data,template.get("templateid")).$(map(
								"id", Nest.value(template,"templateid").$(),
								"name", Nest.value(template,"name").$(),
								"prefix", ""
							));
						}
		
						table.addRow(array(multiselect>0 ? checkBox : null, name));
					}
				}
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"),
					"javascript: addSelectedValues(\"templates\", "+rda_jsvalue(reference)+", "+parentId+");"
				);
				button.addClass("buttonorange");
				table.setFooter(new CCol(button, "right"));
			}

			insert_js("var popupReference = "+rda_jsvalue(data, true, true)+";");
			rda_add_post_js("chkbxRange.pageGoName = \"templates\";");

			form.addItem(table);
			form.show();
		} else if ("hosts".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("hostform");
			form.setAttribute("id", "hosts");

			CTableInfo table = new CTableInfo(_("No hosts found."));
			table.setHeader(array((multiselect>0 ? new CCheckBox("allHosts", false,
				"javascript: checkAll(\""+form.getName()+"\", \"allHosts\", \"hosts\");") : null), _("Name")
			));

			CHostGet params = new CHostGet();
			params.setOutput(new String[]{"hostid", "name"});
			params.setGroupIds(groupid);
			params.setPreserveKeys(true);
			params.setSortfield("name");

			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}

			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(params);

			CArray data = array();
			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			for (Map dbhost : hosts) {
				CSpan name = new CSpan(Nest.value(dbhost,"name").$(), "link");
				name.attr("id", "spanid"+Nest.value(dbhost,"hostid").$());

				String jsAction = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(dbhost,"hostid").$())+", "+	parentId+");";

				CCheckBox checkBox = null;
				if (multiselect>0) {
					checkBox  = new CCheckBox("hosts["+rda_jsvalue("hostid")+"]", false, null, Nest.value(dbhost,"hostid").asString());
				}

				// check for existing
				if (isset(excludeids.get(dbhost.get("hostid")))) {
					if (multiselect>0) {
						checkBox.setChecked(true);
						checkBox.setEnabled("disabled");
					}
					name.removeAttr("class");
				} else {
					name.setAttribute("onclick", jsAction+" jQuery(this).removeAttr(\"onclick\");");

					Nest.value(data,dbhost.get("hostid")).$(map(
						"id", Nest.value(dbhost,"hostid").$(),
						"name", Nest.value(dbhost,"name").$(),
						"prefix", ""
					));
				}

				table.addRow(array(multiselect>0 ? checkBox : null, name));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"),
					"javascript: addSelectedValues(\"hosts\", "+rda_jsvalue(reference)+", "+parentId+");","buttonorange");
				table.setFooter(new CCol(button, "right"));
			}

			insert_js("var popupReference = "+rda_jsvalue(data, true, true)+";");
			rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");

			form.addItem(table);
			form.show();
		} else if ("host_templates".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("hosttemplateform");
			form.setAttribute("id", "hosts");

			CTableInfo table = new CTableInfo(_("No hosts found."));
			table.setHeader(array((multiselect>0 ? new CCheckBox("allHosts", false,
				"javascript: checkAll(\""+form.getName()+"\", \"allHosts\", \"hosts\");") : null), _("Name")
			));

			CHostGet params = new CHostGet();
			params.setOutput(new String[]{"hostid", "name"});
			params.setGroupIds(groupid);
			params.setTemplatedHosts(true);
			params.setPreserveKeys(true);
			params.setSortfield("name");

			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}

			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(params);

			CArray data = array();
			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			for (Map dbhost : hosts) {
				CSpan name = new CSpan(Nest.value(dbhost,"name").$(), "link");
				name.attr("id", "spanid"+Nest.value(dbhost,"hostid").$());

				String jsAction = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(dbhost,"hostid").$())+", "+ parentId+");";

				CCheckBox checkBox = null;
				if (multiselect>0) {
					checkBox  = new CCheckBox("hosts["+rda_jsvalue("hostid")+"]", false, null, Nest.value(dbhost,"hostid").asString());
				}

				// check for existing
				if (isset(excludeids.get(dbhost.get("hostid")))) {
					if (multiselect>0) {
						checkBox.setChecked(true);
						checkBox.setEnabled("disabled");
					}
					name.removeAttr("class");
				} else {
					name.setAttribute("onclick", jsAction+" jQuery(this).removeAttr(\"onclick\");");
					
					Nest.value(data,dbhost.get("hostid")).$(map(
						"id", Nest.value(dbhost,"hostid").$(),
						"name", Nest.value(dbhost,"name").$(),
						"prefix", ""
					));
				}

				table.addRow(array(multiselect>0 ? checkBox : null, name));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"),
					"javascript: addSelectedValues(\"hosts\", "+rda_jsvalue(reference)+", "+parentId+");"
				);
				table.setFooter(new CCol(button, "right"));
			}

			insert_js("var popupReference = "+rda_jsvalue(data, true, true)+";");
			rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");

			form.addItem(table);
			form.show();
		} else if ("host_groups".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("hostGroupsform");
			form.setAttribute("id", "hostGroups");

			CTableInfo table = new CTableInfo(_("No host groups found."));
			table.setHeader(array((multiselect>0 ? new CCheckBox("allHostGroups", false,
				"javascript: checkAll(\""+form.getName()+"\", \"allHostGroups\", \"hostGroups\");") : null), _("Name")
			));

			CHostGroupGet params = new CHostGroupGet();
			params.setOutput(new String[]{"groupid", "name"});
			params.setPreserveKeys(true);
			params.setSortfield("name");
			params.setEditable(true);
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			CArray<Map> hostgroups = API.HostGroup(getIdentityBean(), executor).get(params);

			CArray data = array();
			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			for (Map hostgroup : hostgroups) {
				if(IMonGroup.MON_VM.id().equals( Nest.value(hostgroup,"groupid").asLong()))
					continue;
				Object objName = Nest.value(hostgroup,"name").$();
				if("Templates".equals(objName) || "Discovered hosts".equals(objName)){
					continue; 
				}
				
				CSpan name = new CSpan(Nest.value(hostgroup,"name").$(), "link");
				name.attr("id", "spanid"+Nest.value(hostgroup,"groupid").$());

				String jsAction = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(hostgroup,"groupid").$())+", "+parentId+");";
				
				CCheckBox checkBox = null;
				if (multiselect>0) {
					checkBox  = new CCheckBox("hostGroups["+rda_jsvalue("groupid")+"]", false, null, Nest.value(hostgroup,"groupid").asString());
				}

				// check for existing
				if (isset(excludeids.get(hostgroup.get("groupid")))) {
					if (multiselect>0) {
						checkBox.setChecked(true);
						checkBox.setEnabled("disabled");
					}
					name.removeAttr("class");
				} else {
					name.setAttribute("onclick", jsAction+" jQuery(this).removeAttr(\"onclick\");");

					Nest.value(data,hostgroup.get("groupid")).$(map(
						"id", Nest.value(hostgroup,"groupid").$(),
						"name", Nest.value(hostgroup,"name").$(),
						"prefix", ""
					));
				}

				table.addRow(array(multiselect>0 ? checkBox : null, name));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"),
					"javascript: addSelectedValues(\"hostGroups\", "+rda_jsvalue(reference)+", "+parentId+");"
				);
				button.addClass("buttonorange");
				table.setFooter(new CCol(button, "right"));
			}

			insert_js("var popupReference = "+rda_jsvalue(data, true, true)+";");
			rda_add_post_js("chkbxRange.pageGoName = \"hostGroups\";");

			form.addItem(table);
			form.show();
		} else if ("help_items".equals(srctbl)) {
			CTableInfo table = new CTableInfo(_("No item keys found."));
			table.setAttribute("class", table.getAttribute("class")+ " standard_n");
			table.setHeader(array(_("Key"), _("Name")));
			CHelpItems helpItems = new CHelpItems();
			CArray<Map> items = helpItems.getByType(itemtype);
			for(Map helpItem:items) {
				Object action = get_window_opener(dstfrm, dstfld1, Nest.value(helpItem,srcfld1).$())+(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, Nest.value(helpItem,srcfld2).$()) : "");
				CSpan name = new CSpan(Nest.value(helpItem,"key").$(), "link");
				name.setAttribute("onclick", action+" close_window(); return false;");
				table.addRow(array(name, Nest.value(helpItem,"description").$()));
			}
			table.show();
		}else if("tenant".equals(srctbl)){	//添加租户查询列表
			CForm form = new CForm();
			form.setName("tenantform");
			form.setAttribute("id", "tenant");

			CTableInfo table = new CTableInfo(_("No values found."));
			table.setHeader(_("Name"));
			
			String name = get_request("tenantName",null);
			name = "".equals(name)?null:name;
			CArray<Map> results=DataDriver.getAllTenants();
			//遍历租户信息
			for(Map map:results){
				Object description = new CSpan(Nest.value(map,"name").$(), "link");
				String js_action = null;
				if (multiselect>0) {
					js_action  = "addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(map,"id").$())+");";
				} else {
					CArray values = map(
						dstfld1, Nest.value(map,srcfld1).$(),
						dstfld2, Nest.value(map,srcfld2).$()
					);
					js_action = "addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); return false;";
				}
				((CSpan)description).setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");
				table.addRow(description);
			}
			form.addItem(table);
			form.show();
			
		} else if ("interface".equals(srctbl)) {//添加系统参数中 平台网络设备接口
			CForm form = new CForm();
			form.setName("interfaceform");
			form.setAttribute("id", "interface");
			
			CTableInfo table = new CTableInfo(_("No values found."));
			table.addRow(array("设备名称","接口"));
			
			//获取接口信息
			CHostIfaceGet params = new CHostIfaceGet();
			params.setOutput(new String[]{"interfaceid","ip","port"});
			params.setSelectHosts(new String[]{"host"});//关联设备信息表，通过设备ID取得设备名称
			params.setEditable(true);
			CArray<Map> interfaces = API.HostInterface(getIdentityBean(), executor).get(params);
			
			//遍历信息
			for (Map ifce : interfaces) {
				//从返回结果中取得设备名称并显示在界面
				Object hostName = "";
				for (Map hostSingle : (CArray<Map>)Nest.value(ifce,"hosts").asCArray()){
					hostName = Nest.value(hostSingle,"host").$();
					break;
				}
				//拼接ip和端口字段显示在界面
				String interfaceName = Nest.value(ifce,"ip").$()+":"+Nest.value(ifce,"port").$();
				Object interfaceElm = new CSpan(interfaceName, "link");
				
				String js_action = null;
				if (multiselect>0) {
					js_action = "addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(ifce,"triggerid").$())+");";
				} else {
					//为界面字段赋值
					CArray values = map(
						dstfld1, Nest.value(ifce,"ip").$()+":"+Nest.value(ifce,"port").$(),//为界面文本框（interface）赋值
						dstfld2, Nest.value(ifce,srcfld2).$()//为隐藏字段interfaceid赋值
					);
					js_action = "addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); return false;";
				}
				((CSpan)interfaceElm).setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				table.addRow(array(
					hostName,
					interfaceElm
				));
			}
			form.addItem(table);
			form.show();
		} 
		else if ("triggers".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("triggerform");
			form.setAttribute("id", "triggers");

			CTableInfo table = new CTableInfo(_("No triggers found."));
			if(!"rda_filter".equals(dstfrm)){				
				String tbClass = table.getAttribute("class").toString();
				tbClass += " dependency";
				table.setAttribute("class", tbClass);
			}
			
			table.setHeader(array(
				multiselect>0 ? new CCheckBox("all_triggers", false, "checkAll(\""+form.getName()+"\", \"all_triggers\", \"triggers\");") : null,
				_("Name"),
				_("Severity"),
				_("Status")
			));

			CTriggerGet params = new CTriggerGet();
			params.setHostIds(hostid);
			params.setOutput(new String[]{"triggerid", "description", "expression", "priority", "status", "state"});
			params.setSelectHosts(new String[]{"hostid", "name"});
			
			params.setSelectDependencies(API_OUTPUT_EXTEND);
			params.setExpandDescription(true);
			params.setSortfield("description");
			if (is_null(hostid)) {
				Nest.value(params,"groupids").$(groupid);
			}
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			if (!is_null(templated)) {
				Nest.value(params,"templated").$(templated);
			}
			if (withMonitoredTriggers>0) {
				Nest.value(params,"monitored").$(true);
			}
			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(params);

			CArray jsTriggers = null;
			if (multiselect>0) {
				jsTriggers = array();
			}

			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			for (Map trigger : triggers) {
				Map dbhost = reset((CArray<Map>)trigger.get("hosts"));
				Nest.value(trigger,"hostname").$(Nest.value(dbhost,"name").$());

				Object description = new CSpan(Nest.value(trigger,"description").$(), "link");
				Nest.value(trigger,"description").$(Nest.value(trigger,"hostname").$()+NAME_DELIMITER+Nest.value(trigger,"description").$());

				String js_action = null;
				if (multiselect>0) {
					js_action  = "addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(trigger,"triggerid").$())+", "+parentId+");";
				} else {
					CArray values = map(
						dstfld1, Nest.value(trigger,srcfld1).$(),
						dstfld2, Nest.value(trigger,srcfld2).$()
					);
					if (isset(srcfld3)) {
						Nest.value(values,dstfld3).$(Nest.value(trigger,dstfld3).$());
					}
					js_action = "addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); return false;";
				}
				((CSpan)description).setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (count(Nest.value(trigger,"dependencies").$()) > 0) {
					description = array(
						description,
						BR(),
						bold(_("Depends on")),
						BR()
					);
					
					CArray<Map> dependentTriggers = Nest.value(trigger,"dependencies").asCArray();

					for (Map dependentTrigger : dependentTriggers) {
						((CArray)description).add(array(CMacrosResolverHelper.resolveTriggerName(getIdentityBean(), executor, CArray.valueOf(dependentTrigger)), BR()));
					}
				}

				table.addRow(array(
					multiselect>0 ? new CCheckBox("triggers["+rda_jsvalue(trigger.get(srcfld1))+"]", false, null, Nest.value(trigger,"triggerid").asString()) : null,
					description,
					getTriggerLevel(Nest.value(trigger,"priority").asInteger(),getIdentityBean(), executor),
					new CSpan(
						triggerIndicator(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger()),
						triggerIndicatorStyle(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger())
					)
				));

				// made to save memory usage
				if (multiselect>0) {
					Nest.value(jsTriggers,trigger.get("triggerid")).$(map(
						"id", Nest.value(trigger,"triggerid").$(),
						"name", Nest.value(trigger,"description").$(),
						"prefix", "",
						"triggerid", Nest.value(trigger,"triggerid").$(),
						"description", Nest.value(trigger,"description").$(),
						"expression", Nest.value(trigger,"expression").$(),
						"priority", Nest.value(trigger,"priority").$(),
						"status", Nest.value(trigger,"status").$(),
						"host", Nest.value(trigger,"hostname").$()
					));
				}
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "addSelectedValues(\"triggers\", "+rda_jsvalue(reference)+", "+parentId+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(jsTriggers, true, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"triggers\";");

			form.addItem(table);
			form.show();
		} else if ("items".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("itemform");
			form.setAttribute("id", "items");

			CTableInfo table = new CTableInfo(_("No items found."));
			if("web.triggerlog.service.action".equals(dstfrm) || "null".equals(dstfrm) || "expression".equals(dstfrm)){
				String tbClass = table.getAttribute("class").toString();
				tbClass += " table_monitorindicator2";
				table.setAttribute("class", tbClass);				
			}else if("graphForm".equals(dstfrm)){
				String tbClass = table.getAttribute("class").toString();
				tbClass += " table_monitorindicator";
				table.setAttribute("class", tbClass);	
			}
			
			CArray header = array(
				pageFilter.$("hostsAll").asBoolean() ? _("Host") : null,
				multiselect>0 ? new CCheckBox("all_items", false, "javascript: checkAll(\""+form.getName()+"\", \"all_items\", \"items\");") : null,
				_("Name"),
				_("Key"),
				_("Type"),
				_("Type of information"),
				_("Status")
			);
			table.setHeader(header);

			CItemGet params = new CItemGet();
			params.setHostIds(hostid);
			if("items".equals(srctbl) && keyName != null && !"".equals(keyName)){
				params.setSearch("name",keyName);
				params.setSearch("key_",keyName);
				params.setSearchByAny(true);
			}
			params.setWebItems(true);
			params.setOutput(new String[]{"itemid", "hostid", "name", "key_", "type", "value_type", "status", "state"});
			params.setSelectHosts(new String[]{"hostid", "name"});
			params.setSortfield("name_expanded");
			if (!is_null(normalOnly)) {
				Nest.value(params,"filter","flags").$(RDA_FLAG_DISCOVERY_NORMAL);
			}
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			if (!is_null(templated) && templated == 1) {
				Nest.value(params,"templated").$(templated);
			}
			if (!is_null(value_types)) {
				Nest.value(params,"filter","value_type").$(value_types);
			}

			CArray<Map> items = API.Item(getIdentityBean(), executor).get(params);

			items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);

			CArray jsItems = null;
			if (multiselect>0) {
				jsItems  = array();
			}

			for (Map item : items) {
				Map dbhost = reset(Nest.value(item,"hosts").asCArray());
				Nest.value(item,"hostname").$(Nest.value(dbhost,"name").$());

				CLink description = new CLink(Nest.value(item,"name_expanded").$(), "#");
				Nest.value(item,"name").$(Nest.value(item,"hostname").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$());

				String js_action = null;
				if (multiselect>0) {
					js_action  = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(item,"itemid").$())+");";
				} else {
					CArray values = array();
					for (int i = 1; i <= dstfldCount; i++) {
						String dstfld = get_request("dstfld"+i);
						String srcfld = get_request("srcfld"+i);

						if (!empty(dstfld) && !empty(item.get(srcfld))) {
							Nest.value(values, dstfld).$(Nest.value(item, srcfld).$());
						}
					}

					// if we need to submit parent window
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+", "+(submitParent>0 ? "true" : "false")+"); return false;";
				}
				description.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				table.addRow(array(
					(hostid > 0) ? null : Nest.value(item,"hostname").$(),
					multiselect>0 ? new CCheckBox("items["+rda_jsvalue(item.get(srcfld1))+"]", false, null, Nest.value(item,"itemid").asString()) : null,
					description,
					Nest.value(item,"key_").$(),
					item_type2str(Nest.value(item,"type").asInteger()),
					itemValueTypeString(Nest.value(item,"value_type").asInteger()),
					new CSpan(itemIndicator(Nest.value(item,"status").asInteger(), Nest.value(item,"state").asInteger()), itemIndicatorStyle(Nest.value(item,"status").asInteger(), Nest.value(item,"state").asInteger()))
				));

				// made to save memory usage
				if (multiselect>0) {
					Nest.value(jsItems,item.get("itemid")).$(map(
						"itemid", Nest.value(item,"itemid").$(),
						"name", Nest.value(item,"name").$(),
						"key_", Nest.value(item,"key_").$(),
						"type", Nest.value(item,"type").$(),
						"value_type", Nest.value(item,"value_type").$(),
						"host", Nest.value(item,"hostname").$()
					));
				}
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"items\", "+rda_jsvalue(reference)+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(jsItems, true, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"items\";");

			form.addItem(table);
			form.show();
		} else if ("prototypes".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("itemform");
			form.setAttribute("id", "items");

			CTableInfo table = new CTableInfo(_("No item prototypes found."));

			CArray header = null;
			if (multiselect>0) {
				header  = array(
					array(new CCheckBox("all_items", false, "javascript: checkAll(\""+form.getName()+"\", \"all_items\", \"items\");"), _("Name")),
					_("Key"),
					_("Type"),
					_("Type of information"),
					_("Status")
				);
			} else {
				header = array(
					_("Name"),
					_("Key"),
					_("Type"),
					_("Type of information"),
					_("Status")
				);
			}
			table.setHeader(header);

			CItemPrototypeGet params= new CItemPrototypeGet();
			params.put("selectHosts",new String[]{"name"});
			params.setDiscoveryIds(get_request_asLong("parent_discoveryid"));
			params.setOutput(API_OUTPUT_EXTEND);
			params.setPreserveKeys(true);
			if (!is_null(value_types)) {
				Nest.value(params,"filter","value_type").$(value_types);
			}

			CArray<Map> items = API.ItemPrototype(getIdentityBean(), executor).get(params);
			items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);
			order_result(items, "name_expanded");

			for (Map item : items) {
				Object description = new CSpan(Nest.value(item,"name_expanded").$(), "link");
				Nest.value(item,"name").$(Nest.value(item,"name").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$());

				String js_action = null;
				if (multiselect>0) {
					js_action  = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(item,"itemid").$())+");";
				} else {
					CArray values = array();
					for (int i = 1; i <= dstfldCount; i++) {
						String dstfld = get_request("dstfld"+i);
						String srcfld = get_request("srcfld"+i);

						if (!empty(dstfld) && !empty(item.get(srcfld))) {
							Nest.value(values,dstfld).$(Nest.value(item,srcfld).$());
						}
					}

					// if we need to submit parent window
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+", "+(submitParent>0 ? "true" : "false")+"); return false;";
				}
				((CSpan)description).setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (multiselect>0) {
					description = new CCol(array(new CCheckBox("items["+rda_jsvalue(item.get(srcfld1))+"]", false, null, Nest.value(item,"itemid").asString()), description));
				}

				table.addRow(array(
					description,
					Nest.value(item,"key_").$(),
					item_type2str(Nest.value(item,"type").asInteger()),
					itemValueTypeString(Nest.value(item,"value_type").asInteger()),
					new CSpan(itemIndicator(Nest.value(item,"status").asInteger()), itemIndicatorStyle(Nest.value(item,"status").asInteger()))
				));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"items\", "+rda_jsvalue(reference)+");");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(items, true, true)+";");
			}

			rda_add_post_js("chkbxRange.pageGoName = \"items\";");

			form.addItem(table);
			form.show();
		} else if ("applications".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("applicationform");
			form.setAttribute("id", "applications");

			CTableInfo table = new CTableInfo(_("No applications found."));
			table.setHeader(array((multiselect>0 ? new CCheckBox("allApplications", false,
				"javascript: checkAll(\""+form.getName()+"\", \"allApplications\", \"applications\");") : null), _("Name")
			));

			CAppGet params = new CAppGet();
			params.setOutput(new String[]{"applicationid", "name"});
			params.setHostIds(hostid);
			params.setExpandData(true);
			params.setSortfield("name");
			if (is_null(hostid)) {
				Nest.value(params,"groupids").$(groupid);
			}
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			if (!is_null(templated)) {
				Nest.value(params,"templated").$(templated);
			}
			CArray<Map> apps = API.Application(getIdentityBean(), executor).get(params);

			CArray data = array();
			String parentId = dstfld1!=null ? rda_jsvalue(dstfld1) : "null";

			for (Map app : apps) {
				CSpan name = new CSpan(Nest.value(app,"name").$(), "link");
				name.attr("id", "spanid"+Nest.value(app,"applicationid").$());

				String jsAction = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(app,"applicationid").$())+", "+parentId+");";

				CCheckBox checkBox = null;
				if (multiselect>0) {
					checkBox  = new CCheckBox("applications["+rda_jsvalue("applicationid")+"]", false, null, Nest.value(app,"applicationid").asString());
				}

				name.setAttribute("onclick", jsAction+" jQuery(this).removeAttr(\"onclick\");");

				Nest.value(data,app.get("applicationid")).$(map(
					"id", Nest.value(app,"applicationid").$(),
					"name", Nest.value(app,"name").$(),
					"prefix", ""
				));

				table.addRow(array(multiselect>0 ? checkBox : null, name));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"applications\", "+rda_jsvalue(reference)+", "+parentId+");");
				table.setFooter(new CCol(button, "right"));
			}

			insert_js("var popupReference = "+rda_jsvalue(data, true, true)+";");
			rda_add_post_js("chkbxRange.pageGoName = \"applications\";");

			form.addItem(table);
			form.show();
		} else if ("nodes".equals(srctbl)) {
			CTableInfo table = new CTableInfo();
			table.setHeader(_("Name"));

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> result = DBselect(executor, 
					"SELECT DISTINCT n.* FROM nodes n ",
					sqlParts.getNamedParams());
			for(Map row : result) {
				String action = get_window_opener(dstfrm, dstfld1, Nest.value(row,srcfld1).$())+(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, Nest.value(row,srcfld2).$()) : "");
				CSpan name = new CSpan(Nest.value(row,"name").$(), "link");
				name.setAttribute("onclick", action+" close_window(); return false;");
				table.addRow(name);
			}
			table.show();
		} else if ("graphs".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("graphform");
			form.setAttribute("id", "graphs");

			CTableInfo table = new CTableInfo(_("No graphs found."));

			CArray header = null;
			if (Nest.as(multiselect).asBoolean()) {
				header = array(
					array(new CCheckBox("all_graphs", false, "javascript: checkAll(\""+form.getName()+"\", \"all_graphs\", \"graphs\");"), _("Description")),
					_("Graph type")
				);
			} else {
				header = array(
					_("Name"),
					_("Graph type")
				);
			}

			table.setHeader(header);

			CArray<Map> graphs = null;
			if (pageFilter.$("hostsSelected").asBoolean()) {
				CGraphGet goptions = new CGraphGet();
				goptions.setHostIds(hostid);
				goptions.setOutput(API_OUTPUT_EXTEND);
				goptions.setSelectHosts(API_OUTPUT_EXTEND);
				goptions.setPreserveKeys(true);
				if (!is_null(writeonly)) {
					goptions.setEditable(true);
				}
				if (!is_null(templated)) {
					goptions.setTemplated(Nest.as(templated).asBoolean());
				}
				graphs = API.Graph(getIdentityBean(), executor).get(goptions);
				order_result(graphs, "name");
			} else {
				graphs = array();
			}

			for(Map graph : graphs) {
				Map chost = reset((CArray<Map>)Nest.value(graph,"hosts").asCArray());
				Nest.value(graph,"hostname").$(Nest.value(chost,"name").$());
				CTag description = new CSpan(Nest.value(graph,"name").$(), "link");
				Nest.value(graph,"name").$(Nest.value(graph,"hostname").$()+NAME_DELIMITER+Nest.value(graph,"name").$());

				String js_action = null;
				if (Nest.as(multiselect).asBoolean()) {
					js_action = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(graph,"graphid").$())+");";
				} else {
					Map values = map(
						dstfld1, Nest.value(graph,srcfld1).$(),
						dstfld2, Nest.value(graph,srcfld2).$()
					);
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
				}
				description.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (Nest.as(multiselect).asBoolean()) {
					description = new CCol(array(new CCheckBox("graphs["+rda_jsvalue(Nest.value(graph,srcfld1).$())+"]", false, null, Nest.value(graph,"graphid").asInteger()), description));
				}

				String graphtype = null;
				switch (Nest.value(graph,"graphtype").asInteger()) {
					case GRAPH_TYPE_STACKED:
						graphtype = _("Stacked");
						break;
					case GRAPH_TYPE_PIE:
						graphtype = _("Pie");
						break;
					case GRAPH_TYPE_EXPLODED:
						graphtype = _("Exploded");
						break;
					default:
						graphtype = _("Normal Graph");
						break;
				}
				table.addRow(array(
					description,
					graphtype
				));
			}

			if (Nest.as(multiselect).asBoolean()) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"graphs\", "+rda_jsvalue(reference)+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(graphs, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"graphs\";");

			form.addItem(table);
			form.show();
		} else if ("sysmaps".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("sysmapform");
			form.setAttribute("id", "sysmaps");

			CTableInfo table = new CTableInfo(_("No maps found."));

			CArray header = null;
			if (Nest.as(multiselect).asBoolean()) {
				header = array(array(new CCheckBox("all_sysmaps", false, "javascript: checkAll(\""+form.getName()+"\", \"all_sysmaps\", \"sysmaps\");"), _("Name")));
			} else {
				header = array(_("Name"));
			}

			table.setHeader(header);

			CMapGet moptions = new CMapGet();
			moptions.setOutput(API_OUTPUT_EXTEND);
			moptions.setPreserveKeys(true);
			if (!is_null(writeonly)) {
				moptions.setEditable(true);
			}
			//网络链路拓扑
			TopoDAO topoDao = new TopoDAO(executor);
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("tenantId", getIdentityBean().getTenantId());
			tempMap.put("userId", getIdentityBean().getUserId());
			tempMap.put("topoType", "nettopo");
//			tempMap.put("limitee", 5);
            List<Topo> topos =  topoDao.doTopoList(tempMap);
			CArray<Map> sysmaps = new CArray<Map>();
			for(Topo t : topos){
				Map<String,Object> tMap = new HashMap<String,Object>();
				tMap.put("sysmapid", t.getId());
				tMap.put("name", t.getTopoName());
				sysmaps.add(tMap);
			}
			order_result(sysmaps, "name");

			for(Map sysmap : sysmaps) {
				Nest.value(sysmap,"node_name").$(isset(Nest.value(sysmap,"node_name").$()) ? "("+Nest.value(sysmap,"node_name").asString()+") " : "");
				//String name = Nest.value(sysmap,"node_name").asString()+Nest.value(sysmap,"name").asString();
				CTag description = new CSpan(Nest.value(sysmap,"name").$(), "link");

				String js_action = null; 
				if (Nest.as(multiselect).asBoolean()) {
					js_action = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(sysmap,"sysmapid").$())+");";
				} else {
					Map values = map(
						dstfld1, Nest.value(sysmap,srcfld1).$(),
						dstfld2, Nest.value(sysmap,srcfld2).$()
					);
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
				}
				if (isset(excludeids,sysmap.get("sysmapid"))) {
					description.removeAttr("class");
				} else {
					description.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");
				}

				if (Nest.as(multiselect).asBoolean()) {
					description = new CCol(array(new CCheckBox("sysmaps["+rda_jsvalue(Nest.value(sysmap,srcfld1).$())+"]", false, null, Nest.value(sysmap,"sysmapid").asInteger()), description));
				}
				table.addRow(description);
			}

			if (Nest.as(multiselect).asBoolean()) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"sysmaps\", "+rda_jsvalue(reference)+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(sysmaps, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"sysmaps\";");

			form.addItem(table);
			form.show();
		} else if ("slides".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("slideform");
			form.setAttribute("id", "slides");

			CTableInfo table = new CTableInfo(_("No slides found."));

			CArray header = null;
			if (Nest.as(multiselect).asBoolean()) {
				header = array(array(new CCheckBox("all_slides", false, "javascript: checkAll(\""+form.getName()+"\", \"all_slides\", \"slides\");"), _("Name")));
			} else {
				header = array(_("Name"));
			}

			table.setHeader(header);

			CArray slideshows = array();
			CArray<Map> dbSlideshows = DBselect(executor,
				"SELECT s.slideshowid,s.name"+
				" FROM slideshows s"
			);
			order_result(dbSlideshows, "name");

			for(Map dbSlideshow : dbSlideshows) {
				if (!slideshow_accessible(getIdentityBean(), executor, Nest.value(dbSlideshow,"slideshowid").asLong(), PERM_READ)) {
					continue;
				}
				Nest.value(slideshows,dbSlideshow.get("slideshowid")).$(dbSlideshow);

				CTag name = new CLink(Nest.value(dbSlideshow,"name").$(), "#");
				String js_action = null;
				if (Nest.as(multiselect).asBoolean()) {
					js_action = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(dbSlideshow,"slideshowid").$())+");";
				} else {
					Map values = map(
						dstfld1, Nest.value(dbSlideshow,srcfld1).$(),
						dstfld2, Nest.value(dbSlideshow,srcfld2).$()
					);
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
				}
				name.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (Nest.as(multiselect).asBoolean()) {
					name = new CCol(array(new CCheckBox("slides["+rda_jsvalue(Nest.value(dbSlideshow,srcfld1).$())+"]", false, null, Nest.value(dbSlideshow,"slideshowid").asString()), name));
				}
				table.addRow(name);
			}

			if (Nest.as(multiselect).asBoolean()) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"slides\", "+rda_jsvalue(reference)+");");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(slideshows, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"slides\";");

			form.addItem(table);
			form.show();
		} else if ("screens".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("screenform");
			form.setAttribute("id", "screens");

			CTableInfo table = new CTableInfo(_("No screens found."));

			CArray header = null;
			if (Nest.as(multiselect).asBoolean()) {
				header = array(
					array(new CCheckBox("all_screens", false, "javascript: checkAll(\""+form.getName()+"\", \"all_screens\", \"screens\");"), _("Name"))
				);
			} else {
				header = array(_("Name"));
			}
			table.setHeader(header);

			CScreenGet soptions = new CScreenGet();
			soptions.setOutput(new String[]{"screenid", "name"});
			soptions.setPreserveKeys(true);
			soptions.setEditable((writeonly == null) ? null: true);
			CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);
			order_result(screens, "name");

			for(Map screen : screens) {
				CTag name = new CSpan(Nest.value(screen,"name").$(), "link");

				String js_action = null;
				if (Nest.as(multiselect).asBoolean()) {
					js_action = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(screen,"screenid").$())+");";
				} else {
					Map values = map(
						dstfld1, Nest.value(screen,srcfld1).$(),
						dstfld2, Nest.value(screen,srcfld2).$()
					);
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
				}
				name.setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (Nest.as(multiselect).asBoolean()) {
					name = new CCol(array(new CCheckBox("screens["+rda_jsvalue(Nest.value(screen,srcfld1).$())+"]", false, null, Nest.value(screen,"screenid").asInteger()), name));
				}
				table.addRow(name);
			}

			if (Nest.as(multiselect).asBoolean()) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"screens\", "+rda_jsvalue(reference)+");","buttonorange");
				table.setFooter(new CCol(button, "right"));

				insert_js("var popupReference = "+rda_jsvalue(screens, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"screens\";");

			form.addItem(table);
			form.show();
		} else if ("screens2".equals(srctbl)) {
			CTableInfo table = new CTableInfo(_("No screens found."));
			table.setHeader(_("Name"));

			CScreenGet soptions = new CScreenGet();
			soptions.setOutput(new String[]{"screenid", "name"});
			soptions.setEditable((writeonly == null) ? null: true);
			CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);
			order_result(screens, "name");

			for(Map screen : screens) {
				if (check_screen_recursion(getIdentityBean(), executor, Nest.value(_REQUEST,"screenid").asLong(), Nest.value(screen,"screenid").asLong())) {
					continue;
				}

				CLink name = new CLink(Nest.value(screen,"name").$(), "#");

				String action = get_window_opener(dstfrm, dstfld1, Nest.value(screen,srcfld1).$())+(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, Nest.value(screen,srcfld2).$()) : "");
				name.setAttribute("onclick", action+" close_window(); return false;");
				table.addRow(name);
			}
			table.show();
		} else if ("drules".equals(srctbl)) {
			CTableInfo table = new CTableInfo(_("No discovery rules found."));
			table.setHeader(_("Name"));

			CArray<Map> result = DBselect(executor,
					"SELECT d.*"+
					" FROM drules d"
			);
			for(Map row : result) {
				String action = get_window_opener(dstfrm, dstfld1, Nest.value(row,srcfld1).$())+(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, Nest.value(row,srcfld2).$()) : "");
				CSpan name = new CSpan(Nest.value(row,"name").$(), "link");
				name.setAttribute("onclick", action+" close_window(); return false;");
				table.addRow(name);
			}
			table.show();
		} else if ("dchecks".equals(srctbl)) {
			CTableInfo table = new CTableInfo(_("No discovery rules found."));
			table.setHeader(_("Name"));

			CDRuleGet droptions = new CDRuleGet();
			droptions.setSelectDChecks(new String[]{"dcheckid", "type", "key_", "ports"});
			droptions.setOutput(new String[]{"name"});
			CArray<Map> dRules = API.DRule(getIdentityBean(), executor).get(droptions);
			for(Map dRule : dRules) {
				for(Map dCheck : (CArray<Map>)Nest.value(dRule,"dchecks").asCArray()) {
					String sname = Nest.value(dRule,"name").asString()+NAME_DELIMITER+discovery_check2str(Nest.value(dCheck,"type").asInteger(), Nest.value(dCheck,"key_").asString(), Nest.value(dCheck,"ports").asString());
					String action = get_window_opener(dstfrm, dstfld1, Nest.value(dCheck,srcfld1).$())+
						(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, sname) : "");
					CSpan name = new CSpan(sname, "link");
					name.setAttribute("onclick", action+" close_window(); return false;");
					table.addRow(name);
				}
			}
			table.show();
		} else if ("proxies".equals(srctbl)) {
			CTableInfo table = new CTableInfo(_("No proxies found."));
			table.setHeader(_("Name"));

			CArray<Map> result = DBselect(executor,
					"SELECT h.hostid,h.host"+
					" FROM hosts h"+
					" WHERE h.status IN ("+HOST_STATUS_PROXY_ACTIVE+","+HOST_STATUS_PROXY_PASSIVE+")"+
					" ORDER BY h.host,h.hostid"
			);
			for(Map row : result) {
				String action = get_window_opener(dstfrm, dstfld1, Nest.value(row,srcfld1).$())+(isset(srcfld2) ? get_window_opener(dstfrm, dstfld2, Nest.value(row,srcfld2).$()) : "");
				CSpan name = new CSpan(Nest.value(row,"host").$(), "link");
				name.setAttribute("onclick", action+" close_window(); return false;");
				table.addRow(name);
			}
			table.show();
		} else if ("scripts".equals(srctbl)) {
			CForm form = new CForm();
			form.setName("scriptform");
			form.attr("id", "scripts");

			CTableInfo table = new CTableInfo(_("No scripts found."));

			CArray header = null;
			if (multiselect>0) {
				header  = array(
					array(new CCheckBox("all_scripts", false, "javascript: checkAll(\""+form.getName()+"\", \"all_scripts\", \"scripts\");"), _("Name")),
					_("Execute on"),
					_("Commands")
				);
			} else {
				header = array(
					_("Name"),
					_("Execute on"),
					_("Commands")
				);
			}
			table.setHeader(header);

			CScriptGet params = new CScriptGet();
			params.setOutput(API_OUTPUT_EXTEND);
			params.setPreserveKeys(true);
			params.setSortfield("name");
			if (is_null(hostid)) {
				Nest.value(params,"groupids").$(groupid);
			}
			if (!is_null(writeonly)) {
				Nest.value(params,"editable").$(true);
			}
			CArray<Map> scripts = API.Script(getIdentityBean(), executor).get(params);

			for (Map script : scripts) {
				Object description = new CLink(Nest.value(script,"name").$(), "#");

				String js_action = null;
				if (multiselect>0) {
					js_action  = "javascript: addValue("+rda_jsvalue(reference)+", "+rda_jsvalue(Nest.value(script,"scriptid").$())+");";
				} else {
					CArray values = map(
						dstfld1, Nest.value(script,srcfld1).$(),
						dstfld2, Nest.value(script,srcfld2).$()
					);
					js_action = "javascript: addValues("+rda_jsvalue(dstfrm)+", "+rda_jsvalue(values)+"); close_window(); return false;";
				}
				((CLink)description).setAttribute("onclick", js_action+" jQuery(this).removeAttr(\"onclick\");");

				if (multiselect>0) {
					description = new CCol(array(new CCheckBox("scripts["+rda_jsvalue(script.get(srcfld1))+"]", false, null, Nest.value(script,"scriptid").asString()), description));
				}

				String scriptExecuteOn = null;
				if (Nest.value(script,"type").asInteger() == RDA_SCRIPT_TYPE_CUSTOM_SCRIPT) {
					switch (Nest.value(script,"execute_on").asInteger()) {
						case RDA_SCRIPT_EXECUTE_ON_AGENT:
							scriptExecuteOn  = _("Agent");
							break;
						case RDA_SCRIPT_EXECUTE_ON_SERVER:
							scriptExecuteOn = _("Server");
							break;
					}
				} else {
					scriptExecuteOn = "";
				}
				table.addRow(array(
					description,
					scriptExecuteOn,
					rda_nl2br(htmlspecialchars(Nest.value(script,"command").asString()))
				));
			}

			if (multiselect>0) {
				CButton button = new CButton("select", _("Select"), "javascript: addSelectedValues(\"scripts\", "+rda_jsvalue(reference)+");");
				table.setFooter(new CCol(button, "right"));
				insert_js("var popupReference = "+rda_jsvalue(scripts, true, true)+";");
			}
			rda_add_post_js("chkbxRange.pageGoName = \"scripts\";");

			form.addItem(table);
			form.show();
		}
	}

	private String get_window_opener(String frame, String field, Object value) {
		if (empty(field)) {
			return "";
		}
		return "try {window.opener.document.getElementById('"+addslashes(field)+"').value='"+addslashes(Nest.as(value).asString())+"'; } catch(e) {throw('Error: Target not found')}\n";
	}

}
