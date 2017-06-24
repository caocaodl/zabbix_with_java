package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_search;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.natsort;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_AUTOMATIC;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_MANUAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.INTERFACE_USE_IP;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.P_UNSET_EMPTY;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtoupper;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HostsUtil.updateHostStatus;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.getAvailabilityTable;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.HostOperationsAction;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.util.StringUtil;


public class HostMonitorAction extends RadarBaseAction {
	
	private boolean exportData;

	@Override
	protected void doInitPage() {
		if (isset(_REQUEST,"go") && "export".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"hosts")) {
			Nest.value(page,"file").$("rda_export_hosts.xml");
			Nest.value(page,"type").$(detect_page_type(PAGE_TYPE_XML));
			this.exportData = true;
		} else {
			this.exportData = false;
			page("title", _("Configuration of hosts"));
			page("file", getAction());
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("hist_arg", new String[] {"groupid"});
			page("scripts", new String[] {"multiselect.js"});
			page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入Ajax异步修改设备状态JS
			page("css", new String[] {"lessor/devicecenter/hostmonitor.css"});
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"moncategory",		    array(T_RDA_STR, O_OPT, P_SYS,	        DB_ID,		null),
			"groups",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"new_groups",			array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"hostids",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groupids",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"applications",			array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groupid",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"templateid",			array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groupid",			    array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"selMaintenanceId",		array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"hostid",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		"isset({form})&&{form}==\"update\""),
			"host",					array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({save})", _("Host name")),
			"visiblename",			array(T_RDA_STR, O_OPT, null,			null,		"isset({save})"),
			"proxy_hostid",			array(T_RDA_INT, O_OPT, P_SYS,		    DB_ID,		null),
			"status",				array(T_RDA_INT, O_OPT, null,			IN("0,1,3"), "isset({save})"),
			"newgroup",				array(T_RDA_STR, O_OPT, null,			null,		null),
			"interfaces",			array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({save})", _(Feature.enableIPMI? (Feature.enableJMX? "Agent or SNMP or JMX or IPMI interface": "Agent or SNMP or IPMI interface"): (Feature.enableJMX? "Agent or SNMP or JMX interface": "Agent or SNMP interface"))),
			"mainInterfaces",		array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"templates",			array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"add_template",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"add_templates", 		array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"templates_rem",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"clear_templates", 		array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"ipmi_authtype",		array(T_RDA_INT, O_OPT, null,			BETWEEN(-1, 6), null),
			"ipmi_privilege",		array(T_RDA_INT, O_OPT, null,			BETWEEN(0, 5), null),
			"ipmi_username",		array(T_RDA_STR, O_OPT, null,			null,		null),
			"ipmi_password",		array(T_RDA_STR, O_OPT, null,			null,		null),
			"mass_replace_tpls", 	array(T_RDA_STR, O_OPT, null,			null,		null),
			"mass_clear_tpls", 		array(T_RDA_STR, O_OPT, null,			null,		null),
			"inventory_mode", 		array(T_RDA_INT, O_OPT, null, 			IN(HOST_INVENTORY_DISABLED+","+HOST_INVENTORY_MANUAL+","+HOST_INVENTORY_AUTOMATIC), null),
			"host_inventory",		array(T_RDA_STR, O_OPT, P_UNSET_EMPTY,	null,		null),
			"macros_rem",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"macros",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"macro_new",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		"isset({macro_add})"),
			"value_new",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		"isset({macro_add})"),
			"macro_add",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"massupdate",			array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"visible",				array(T_RDA_STR, O_OPT, null,			null,		null),
			// actions
			"go",					array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"add_to_group",			array(T_RDA_INT, O_OPT, P_SYS|P_ACT,	DB_ID,		null),
			"delete_from_group", 	array(T_RDA_INT, O_OPT, P_SYS|P_ACT,	DB_ID,		null),
			"unlink",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"unlink_and_clear", 	array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"masssave",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"clone",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			//"full_clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"delete",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"cancel",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"form",					array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"form_refresh",			array(T_RDA_STR, O_OPT, null,			null,		null),
			// filter
			"filter_set",			array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"filter_host",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"filter_ip",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"filter_dns",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"filter_port",			array(T_RDA_STR, O_OPT, null,			null,		null),
			// ajax
			"favobj",				array(T_RDA_STR, O_OPT, P_ACT,			null,		null),
			"favref",				array(T_RDA_STR, O_OPT, P_ACT,			NOT_EMPTY,	"isset({favobj})"),
			"favstate",				array(T_RDA_INT, O_OPT, P_ACT,			NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}")
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("templateid")) && !API.Template(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"templateid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("selMaintenanceId")) && !API.Maintenance(getIdentityBean(), executor).exists(map("maintenanceid", Nest.value(_REQUEST,"selMaintenanceId").asLong()))) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.hosts.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
		/* Filter */
		if (isset(_REQUEST,"filter_set")) {
			Nest.value(_REQUEST,"filter_ip").$(get_request("filter_ip"));
			Nest.value(_REQUEST,"filter_dns").$(get_request("filter_dns"));
			Nest.value(_REQUEST,"filter_host").$(get_request("filter_host"));
			Nest.value(_REQUEST,"filter_port").$(get_request("filter_port"));

			CProfile.update(getIdentityBean(), executor, "web.hosts.filter_ip", Nest.value(_REQUEST,"filter_ip").$(), PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor, "web.hosts.filter_dns", Nest.value(_REQUEST,"filter_dns").$(), PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor, "web.hosts.filter_host", Nest.value(_REQUEST,"filter_host").$(), PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor, "web.hosts.filter_port", Nest.value(_REQUEST,"filter_port").$(), PROFILE_TYPE_STR);
		} else {
			Nest.value(_REQUEST,"filter_ip").$(CProfile.get(getIdentityBean(), executor, "web.hosts.filter_ip"));
			Nest.value(_REQUEST,"filter_dns").$(CProfile.get(getIdentityBean(), executor, "web.hosts.filter_dns"));
			Nest.value(_REQUEST,"filter_host").$(CProfile.get(getIdentityBean(), executor, "web.hosts.filter_host"));
			Nest.value(_REQUEST,"filter_port").$(CProfile.get(getIdentityBean(), executor, "web.hosts.filter_port"));
		}
		CProfile.update(getIdentityBean(), executor, "web.hosts.filter.mon_cate", Nest.value(_REQUEST,"moncategory").$(), PROFILE_TYPE_STR);
		CProfile.update(getIdentityBean(), executor, "web.hosts.filter.mon_template", Nest.value(_REQUEST,"templateid").$(), PROFILE_TYPE_INT);
		CProfile.update(getIdentityBean(), executor, "web.hosts.filter.maintenance", Nest.value(_REQUEST,"selMaintenanceId").$(), PROFILE_TYPE_INT);
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		CArray<String> hostIds = get_request("hosts", array());
		
		/* Export  */
		if (exportData) {
			//FIXME 数据导出待实现
			return;
		}
		
		if (isset(_REQUEST,"add_template") && isset(_REQUEST,"add_templates")) {
			Nest.value(_REQUEST,"templates").$(get_request("templates", array()));
			Nest.value(_REQUEST,"templates").$(array_merge(Nest.value(_REQUEST,"templates").asCArray(), Nest.value(_REQUEST,"add_templates").asCArray()));
		}
		
		if (isset(_REQUEST,"unlink") || isset(_REQUEST,"unlink_and_clear")) {
			Nest.value(_REQUEST,"clear_templates").$(get_request("clear_templates", array()));

			CArray<String> unlinkTemplates = array();

			if (isset(_REQUEST,"unlink")) {
				// templates_rem for old style removal in massupdate form
				if (isset(_REQUEST,"templates_rem")) {
					unlinkTemplates = array_keys(Nest.value(_REQUEST,"templates_rem").asCArray());
				} else if (isArray(Nest.value(_REQUEST,"unlink").$())) {
					unlinkTemplates = array_keys(Nest.value(_REQUEST,"unlink").asCArray());
				}
			} else {
				unlinkTemplates = array_keys(Nest.value(_REQUEST,"unlink_and_clear").asCArray());

				Nest.value(_REQUEST,"clear_templates").$(array_merge(Nest.value(_REQUEST,"clear_templates").asCArray(), unlinkTemplates));
			}

			CArray templates = Nest.value(_REQUEST,"templates").asCArray();
			for(Object templateId : unlinkTemplates) {
				if(in_array(templateId,templates)){
					unset(templates, array_search(templateId, templates));
				}
			}
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"hostid")) {
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"go") && "massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"masssave")) {
			doMassUpdate(executor);
		} else if (isset(_REQUEST,"save")) {
			doSave(executor);
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"hostid")) {
			boolean result = false;
			if(doHostMaintenanceCheck(executor,hostIds.valuesAsLong())){
				DBstart(executor);
				result = !empty(API.Host(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"hostid").asLong()));
				result = DBend(executor, result);
			}else{
				result = false;
				info(_("the maintenance of hosts is not null"));
			}
			
			show_messages(result, _("Host deleted"), _("Cannot delete host"));
			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"hostid");
			}
			unset(_REQUEST,"delete");
			clearCookies(result);
		}  else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			hostIds = get_request("hosts", array());
			boolean goResult=false;
			if(doHostMaintenanceCheck(executor,hostIds.valuesAsLong())){
				DBstart(executor);
				goResult = !empty(API.Host(getIdentityBean(), executor).delete(hostIds.valuesAsLong()));
				goResult = DBend(executor, goResult);
			}else{
				goResult = false;
				info(_("the maintenance of hosts is not null"));
			}
				
			show_messages(goResult, _("Host deleted"), _("Cannot delete host"));
			clearCookies(goResult);
		} else if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean enable =("activate".equals(get_request("go")));
			int status = enable ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED;
			CArray hosts = get_request("hosts", array());

			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hosts.valuesAsLong());
			hoptions.setEditable(true);
			hoptions.setTemplatedHosts(true);
			hoptions.setOutput(new String[]{"hostid"});
			CArray<Map> actHosts = API.Host(getIdentityBean(), executor).get(hoptions);
			actHosts = rda_objectValues(actHosts, "hostid");

			if (!empty(actHosts)) {
				DBstart(executor);
				
				boolean result = updateHostStatus(getIdentityBean(), executor, actHosts.valuesAsLong(), status);
				result = DBend(executor, result);
				
				int updated = count(actHosts);
				String messageSuccess = enable
						? _n(_("dosuccess"), _("dosuccess"), updated)
						: _n(_("dosuccess"), _("dosuccess"), updated);
				String messageFailed = enable
						? _n(_("donot"), _("donot"), updated)
						: _n(_("donot"), _("donot"), updated);
				show_messages(result, messageSuccess, messageFailed);
				clearCookies(result);
			}
		}
		
		/* Display */
		CWidget hostsWidget = new CWidget();

		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"groups", map("real_hosts", true,"editable", true),
			"groupid", get_request("groupid", null)
		));

//		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());
		Nest.value(_REQUEST,"hostid").$(get_request("hostid", 0));

		if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"hosts")) {
			showMassUpdate(executor, hostsWidget);
		}  else if (isset(_REQUEST,"form")) {
			showForm(executor, hostsWidget);
		} else {
			showList(executor, hostsWidget, pageFilter);
		}
		hostsWidget.show();
	}

	/**
	 * @param executor
	 * @param hostsWidget
	 * @param pageFilter
	 */
	private void showList(final SQLExecutor executor, CWidget hostsWidget, CPageFilter pageFilter) {
		// table hosts
		CForm form = new CForm();//定义显示数据表单
		form.setName("hosts");
//		form.addItem(new CVar("groupid", Nest.value(_REQUEST,"groupid").$(), "filter_groupid_id"));
		
		CToolBar tb = new CToolBar(form);
		tb.addSubmit("form", _("Create host"),"","orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("massupdate", _("Mass update"));
		goOption.setAttribute("class", "orange massupdate");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected hosts?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected hosts?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected hosts?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");
		
		CForm frmGroup = new CForm("get");
		/*************************添加设备类型、监控模型ID方法************************************************/
		frmGroup.setAttribute("name", "formone");

		String sortfield = getPageSortField(getIdentityBean(), executor, "name");
		Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
		boolean dropdown_first_remember = 1==Nest.value(config, "dropdown_first_remember").asInteger();
		
		/*CComboBox comboBox= pageFilter.getGroupsCB();
		frmGroup.addItem(array("监控类型"+SPACE, comboBox));*/
		
		//通过监控类别获取
		CArray<String> moncategoryarraystr=new CArray<String>();
		for(Entry<String,String> e:IMonConsts.MON_CATE.entrySet()){
			String key = e.getKey();
			String value = e.getValue();
			moncategoryarraystr.put(key, value);
		}
		String moncategoryid = Nest.as(get_request("moncategory",dropdown_first_remember?doGetMonCateHistoryValue(executor,"web.hosts.filter.mon_cate"):"0")).asString();
		CComboBox monCategory = pageFilter.getCB("moncategory", moncategoryid, moncategoryarraystr);
		frmGroup.addItem(array(SPACE+_("Monitor category")+SPACE, monCategory));
		
		//通过templateid获取模型名称
		CArray<String> arraystr=new CArray<String>();
		CTemplateGet tplGet = new CTemplateGet();
		tplGet.setOutput(new String[]{"templateid", "name"});
		CArray<Map> templates = API.Template(getIdentityBean(), executor).get(tplGet);
		for(Map template : templates){
			String templateids = Nest.value(template, "templateid").asString();
			String templatename = Nest.value(template, "name").asString();
			arraystr.put(templateids, templatename);
		}
		Long templateid = get_request("templateid", dropdown_first_remember?Nest.as(CProfile.get(getIdentityBean(), executor,"web.hosts.filter.mon_template", 0L)).asLong():0L);
		CComboBox temboBox = pageFilter.getCB("templateid", templateid.toString(), arraystr);//定义下来列表
		frmGroup.addItem(array(SPACE+_("Templateid")+SPACE, temboBox));
		
		//添加维护规划表头条件
		CMaintenanceGet opts = new CMaintenanceGet();
		opts.setOutput(new String[]{"maintenanceid","name"});
		CArray<Map> maintens=API.Maintenance(getIdentityBean(), executor).get(opts);
		
		CArray<String> maintenarray=new CArray<String>();
		for(Map mainten : maintens){
			String maintenanceidstr = Nest.value(mainten, "maintenanceid").asString();
			String maintenancenamestr = Nest.value(mainten, "name").asString();
			maintenarray.put(maintenanceidstr, maintenancenamestr);
		}
		
		Long selMaintenanceId = get_request("selMaintenanceId", dropdown_first_remember?Nest.as(CProfile.get(getIdentityBean(), executor,"web.hosts.filter.maintenance", 0L)).asLong():0L);
		CComboBox boBox =pageFilter.getCB("selMaintenanceId", selMaintenanceId.toString(), maintenarray);//定义下来列表
		frmGroup.addItem(array(SPACE+_("Maintenance plan")+SPACE, boBox));
		/******************************添加设备类型、模型监控*************************************************/

		hostsWidget.addHeader(frmGroup);
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		
		hostsWidget.addItem(headerActions);
		hostsWidget.setRootClass("host-list");

		// filter//高级检索表格
		CTable filterTable = new CTable("", "filter");
		
		//添加该字段是为了保证在被引用页面跳转时，查询的还是该模型下的设备
		CInput temone =new CInput("hidden","templateid",templateid.toString());//在检索中添加隐藏的templateid条件
		CInput temtwo =new CInput("hidden","moncategory",moncategoryid);//在检索中添加隐藏的设备类型选中参数
		CInput temfour =new CInput("hidden","selMaintenanceId",selMaintenanceId.toString());//在检索中添加隐藏的维护计划ID选中参数
		CButton filter = new CButton("filter", _("GoFilter"),
				"javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true); chkbxRange.clearSelectedOnFilterChange();"
			);
		filter.useJQueryStyle("main");
		CButton reset = new CButton("reset", _("Reset"), "javascript: clearAllForm(\"rda_filter\");","darkgray");
		reset.useJQueryStyle();
		CArray filtercarray=array(temone,temtwo,temfour);
		
		//处理检索按钮分行问题
		CArray fiterSeconed=array(filter,reset);
		CCol cols = new CCol();
		cols.addItem(fiterSeconed);
		cols.setAttribute("colspan", "4");
		cols.addClass("hostConfig");
		CRow row = new CRow();
		row.addItem(cols);
		filterTable.addRow(getFileterRow(filtercarray));//filtercarray为隐藏参数，同时保证不用换行
		filterTable.addRow(row);
		//高级检索表单
		CForm filterForm = new CForm("post");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addItem(filterTable);
		hostsWidget.addFlicker(filterForm, Nest.as(CProfile.get(getIdentityBean(), executor,"web.hosts.filter.state", 0)).asInteger());

		CTableInfo table = new CTableInfo(_("No hosts found."));//定义显示设备表
		table.setHeader(getHeader(form));//设置表头

		// get Hosts
		CArray<Map> hosts = array();

		String sortorder = getPageSortOrder(getIdentityBean(), executor);
		
		CHostGet options = null;
		if (pageFilter.$("groupsSelected").asBoolean()) {
			options = new CHostGet();
			if(templateid!=0){//如果templateid不为零，说明是被引用数页面跳转引用
				options.setTemplateIds(templateid);
			}
			if(!empty(moncategoryid)){//设备类别，说明为其他引用引用
//				Long[] groupids= MoncategoryUtil.getGroupidByMoncategory(moncategoryid);
				Long[] groupids= MoncategoryUtil.getGroupIdsByPId(executor,moncategoryid).valuesAsLong();
				options.setGroupIds(groupids);
			}
			
			if(empty(moncategoryid)&&!empty(Nest.value(_REQUEST,"groupid").$()))
				options.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
			
			if(selMaintenanceId!=0){
				//通过selMaintenanceId查找到维护计划
				CArray<Map> maintenances = array();
				CMaintenanceGet option = new CMaintenanceGet();
				option.setMaintenanceIds(selMaintenanceId);
				option.setOutput(API_OUTPUT_EXTEND);
				option.setSelectGroups(new String[]{"groupid"});//维护的可以是设备组
				option.setSelectHosts(new String[]{"hostid"});//维护的也可以是单个设备
				maintenances = API.Maintenance(getIdentityBean(), executor).get(option);
				
				CArray<Long> tempList = array();
				for (Map maintenance : maintenances) {//查找到关联的设备和设备的hostid
					//查找单个设备id
					tempList.putAll(Nest.value(maintenance,"hosts").asCArray());
					
					//查找设备组的设备id
					Long[] groupIds = rda_objectValues(Nest.value(maintenance, "groups").asCArray(), "groupid").valuesAsLong();
					CHostGroupGet groupoptions = new CHostGroupGet();
					groupoptions.setGroupIds(groupIds);
					groupoptions.setSelectHosts(new String[]{"hostid"});
					CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(groupoptions);
					
					for (Map hostGroup : hostGroups) {
//						tempList.putAll(Nest.value(hostGroup,"hosts").asCArray());
						CArray<Map> hostsCA = Nest.value(hostGroup,"hosts").asCArray();
						for(Map host:hostsCA){
							if(!rda_objectValues(tempList, "hostid").containsValue(Nest.value(host, "hostid").$())){
								tempList.put(host);
							}
						}
					}
				}
				options.setHostIds(rda_objectValues(tempList, "hostid").valuesAsLong());
			}
			options.setOutput(new String[]{"hostid", "name"});
			options.setEditable(true);
			options.setSortfield(sortfield);
			options.setSortorder(sortorder);
			options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			if(!StringUtil.isEmpty(Nest.value(_REQUEST,"filter_host").asString())){
				options.setSearch("name", Nest.value(_REQUEST,"filter_host").asString());
			}
			if(!StringUtil.isEmpty(Nest.value(_REQUEST,"filter_ip").asString())){
				options.setSearch("ip", Nest.value(_REQUEST,"filter_ip").asString());
			}
			if(!StringUtil.isEmpty(Nest.value(_REQUEST,"filter_dns").asString())){
				options.setSearch("dns", Nest.value(_REQUEST,"filter_dns").asString());
			}
			if(!StringUtil.isEmpty(Nest.value(_REQUEST,"filter_port").asString())){
				options.setSearch("port", Nest.value(_REQUEST,"filter_port").asString());
			}
			hosts = API.Host(getIdentityBean(), executor).get(options);
		} else {
			hosts = array();
		}
		// sorting && paging
		order_result(hosts, sortfield, sortorder);
		CTable paging = getPagingLine(getIdentityBean(), executor, hosts, array("hostid"));

		 //从数据库获取设备列表
		options = new CHostGet();
		options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectParentTemplates(new String[]{"hostid", "name"});
		options.setSelectInterfaces(API_OUTPUT_EXTEND);
		options.setSelectItems(API_OUTPUT_COUNT);
		options.setSelectGraphs(API_OUTPUT_COUNT);
		options.setSelectApplications(API_OUTPUT_COUNT);
		options.setEditable(true);
		hosts = API.Host(getIdentityBean(), executor).get(options);
		order_result(hosts, sortfield, sortorder);

		// selecting linked templates to templates linked to hosts
		CArray templateIds = array();
		for (Map host : hosts) {
			templateIds = array_merge(templateIds, rda_objectValues(Nest.value(host,"parentTemplates").$(), "templateid"));
		}
		templateIds = array_unique(templateIds);

		// get proxy host IDs that that are not 0
		CArray proxyHostIds = array();
		for (Map host : hosts) {
			if (!empty(Nest.value(host,"proxy_hostid").$())) {
				Nest.value(proxyHostIds,host.get("proxy_hostid")).$(Nest.value(host,"proxy_hostid").$());
			}
		}
		
		CArray<Map> proxies = null;
		if (!empty(proxyHostIds)) {
			CProxyGet pxyGet = new CProxyGet();
			pxyGet.setProxyIds(proxyHostIds.valuesAsLong());
			pxyGet.setOutput(new String[]{"host"});
			pxyGet.setPreserveKeys(true);
			proxies = API.Proxy(getIdentityBean(), executor).get(pxyGet);
		}

		for (Map host : hosts) {
			Map iface = reset(Nest.value(host,"interfaces").asCArray());
			String groupid = Nest.value(_REQUEST,"groupid").asString();
			String hostid = Nest.value(host,"hostid").asString();
			String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
			String url= "'"+_("Items")+"', '"+common_action_with_context+"monitor_items.action?filter_set=1&hostid="+hostid+"'";
			CArray items = array(new CLink(_("Items"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE), " ("+Nest.value(host,"items").$()+")");
			
			url= "'"+_("trendchart")+"', '"+common_action_with_context+"monitor_graphs.action?groupid=0&hostid="+hostid+"'";;
			CArray graphs = array(new CLink(_("trendchart"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE), 
					" ("+Nest.value(host,"graphs").$()+")");
			url= "'"+_("Applications")+"', '"+common_action_with_context+"monitor_apps.action?hostid="+hostid+"'";;
			CArray applications = array(new CLink(_("Applications"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE), 
					" ("+Nest.value(host,"applications").$()+")");
			/*设备名称
		     */
			CArray description = array();
			if (isset(proxies,host.get("proxy_hostid"))) {
				description.add(Nest.value(proxies,host.get("proxy_hostid"),"host").asString()+NAME_DELIMITER);
			}
			if (!empty(Nest.value(host,"discoveryRule").$())) {
				description.add(new CLink(Nest.value(host,"discoveryRule","name").$(), "host_prototypes.action?parent_discoveryid="+Nest.value(host,"discoveryRule","itemid").$(), "parent-discovery"));
				description.add(NAME_DELIMITER);
			}
			description.add(new CLink(CHtml.encode(Nest.value(host,"name").asString()), getAction()+"?form=update&hostid="+(Nest.value(host,"hostid").asString()+url_param(getIdentityBean(), "groupid"))));
			/*网络接口*/
			String hostInterface = (Nest.value(iface,"useip").asInteger() == INTERFACE_USE_IP) ? Nest.value(iface,"ip").asString(true) : Nest.value(iface,"dns").asString(true);
			hostInterface += empty(Nest.value(iface,"port").$()) ? "" : NAME_DELIMITER+Nest.value(iface,"port").asString();
			
			//自定义属性接收状态必要值 用于Ajax异步修改设备状态
			CLink statusLink = new CLink(
					statusStr(executor, host),
					"?hosts="+Nest.value(host,"hostid").asString()+"&templateid="+templateid.toString()
					+"&groupid="+pageFilter.$("groupid").asLong()+"&selMaintenanceId="+selMaintenanceId.toString()
					 +(Nest.value(host,"status").asInteger() == HOST_STATUS_MONITORED ? "&go=disable" : "&go=activate"),
					status2style(Nest.value(host,"status").asInteger())
			);
			
			Object _hosts = Nest.value(host,"hostid").asString();
			Object _go = Nest.value(host,"status").asInteger() == HOST_STATUS_MONITORED ? "disable" : "activate";
			Object _templateid = templateid.toString();
			Object _selHostType = pageFilter.$("groupid").asString();
			Object _selMaintenanceId = selMaintenanceId.toString();
			String[] ids = statusLink.getUrl().split("=");
			Object _sid = ids[ids.length-1];
			
			statusLink.setAttribute("hosts", _hosts);
			statusLink.setAttribute("templateid", _templateid);
			statusLink.setAttribute("groupid", _selHostType);
			statusLink.setAttribute("selMaintenanceId", _selMaintenanceId);
			statusLink.setAttribute("go", _go);
			statusLink.setAttribute("sid", _sid);
			statusLink.setAttribute("onclick", "changeMonitorStatus(this)");
			statusLink.setAttribute("href", "javascript:void(0)");

			CDiv statusdiv = new CDiv(statusLink, "switch");
			CCol status = new CCol(statusdiv);
			
			table.addRow(getRowData(executor, host, items, applications, graphs, description, hostInterface, status));
		}
		form.addItem(array(table, paging));
		hostsWidget.addItem(form);
	}

	/**
	 * @param executor
	 * @param hostsWidget
	 */
	private void showForm(final SQLExecutor executor, CWidget hostsWidget) {
		CArray data = array();
		Long hostId = null;
		Map dbHost = null;
		if (!empty(hostId  = get_request("hostid", 0L))) {
			CHostGet options = new CHostGet();
			options.setHostIds(hostId);
			options.setSelectGroups(API_OUTPUT_EXTEND);
			options.setSelectParentTemplates(new String[]{"templateid", "name"});
			options.setSelectMacros(API_OUTPUT_EXTEND);
			options.setSelectInventory(API_OUTPUT_EXTEND);
			options.setSelectDiscoveryRule(new String[]{"name", "itemid"});
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> dbHosts = API.Host(getIdentityBean(), executor).get(options);
			dbHost  = reset(dbHosts);

			CHostIfaceGet ifGet = new CHostIfaceGet();
			ifGet.setHostIds(hostId);
			ifGet.setOutput(API_OUTPUT_EXTEND);
			ifGet.setSelectItems(new String[]{"type"});
			ifGet.setSortfield("interfaceid");
			ifGet.setPreserveKeys(true);
			Nest.value(dbHost,"interfaces").$(API.HostInterface(getIdentityBean(), executor).get(ifGet));

			Nest.value(data,"dbHost").$(dbHost);
		}
		
		prepareEditData(data);

		CView hostForm = new CView("configuration.host.edit", data);
		hostsWidget.addItem(hostForm.render(getIdentityBean(), executor));

		String rootClass = "host-edit";
		if (!empty(get_request("hostid")) && Nest.value(dbHost,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED) {
			rootClass += " host-edit-discovered";
		}
		hostsWidget.setRootClass(rootClass);
	}

	/**
	 * @param executor
	 * @param hostsWidget
	 */
	private void showMassUpdate(final SQLExecutor executor, CWidget hostsWidget) {
		Map data = map(
			"hosts", get_request("hosts", array()),
			"visible", get_request("visible", array()),
			"mass_replace_tpls", get_request("mass_replace_tpls"),
			"mass_clear_tpls", get_request("mass_clear_tpls"),
			"groups", get_request("groups", array()),
			"newgroup", get_request("newgroup", ""),
			"status", get_request("status", HOST_STATUS_MONITORED),
			"proxy_hostid", get_request("proxy_hostid", ""),
			"ipmi_authtype", get_request("ipmi_authtype", -1),
			"ipmi_privilege", get_request("ipmi_privilege", 2),
			"ipmi_username", get_request("ipmi_username", ""),
			"ipmi_password", get_request("ipmi_password", ""),
			"inventory_mode", get_request("inventory_mode", HOST_INVENTORY_DISABLED),
			"host_inventory", get_request("host_inventory", array()),
			"templates", get_request("templates", array())
		);

		// sort templates
		natsort(Nest.value(data,"templates").asCArray());

		// get groups
		CHostGroupGet grpGet = new CHostGroupGet();
		grpGet.setOutput(API_OUTPUT_EXTEND);
		grpGet.setEditable(true);
		Nest.value(data,"all_groups").$(API.HostGroup(getIdentityBean(), executor).get(grpGet));
		order_result(Nest.value(data,"all_groups").asCArray(), "name");

		// get proxies
		Nest.value(data,"proxies").$(DBselect(executor,
			"SELECT h.hostid,h.host"+
			" FROM hosts h"+
			" WHERE h.status IN ("+HOST_STATUS_PROXY_ACTIVE+","+HOST_STATUS_PROXY_PASSIVE+")"
		));
		order_result(Nest.value(data,"proxies").asCArray(), "host");

		// get inventories
		if (Nest.value(data,"inventory_mode").asInteger() != HOST_INVENTORY_DISABLED) {
			Nest.value(data,"inventories").$(getHostInventories());
			Nest.value(data,"inventories").$(rda_toHash(Nest.value(data,"inventories").$(), "db_field"));
		}

		// get templates data
		Nest.value(data,"linkedTemplates").$(null);
		if (!empty(Nest.value(data,"templates").$())) {
			CTemplateGet tplGet = new CTemplateGet();
			tplGet.setTemplateIds(Nest.value(data,"templates").asLong());
			tplGet.setOutput(new String[]{"templateid", "name"});
			CArray<Map> getLinkedTemplates = API.Template(getIdentityBean(), executor).get(tplGet);

			for(Map getLinkedTemplate : getLinkedTemplates) {
				Nest.value(data,"linkedTemplates").asCArray().add(map(
					"id", Nest.value(getLinkedTemplate,"templateid").$(),
					"name", Nest.value(getLinkedTemplate,"name").$()
				));
			}
		}
		CView hostForm = new CView("configuration.host.massupdate", data);
		hostsWidget.addItem(hostForm.render(getIdentityBean(), executor));
	}

	/**
	 * @param executor
	 */
	private void doSave(final SQLExecutor executor) {
		String msgOk = null,msgFail = null;
		try {
			DBstart(executor);
			
			boolean createNew;
			Map hostOld = null;
			//if (isset(_REQUEST,"hostid") && !"full_clone".equals(Nest.value(_REQUEST,"form").asString())) {
			if (isset(_REQUEST,"hostid")) {
				createNew = false;
				msgOk = _("Host updated");
				msgFail = _("Cannot update host");

				CHostGet hoptions = new CHostGet();
				hoptions.setHostIds(get_request_asLong("hostid"));
				hoptions.setEditable(true);
				hoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> hostOlds = API.Host(getIdentityBean(), executor).get(hoptions);
				hostOld = reset(hostOlds);
			} else {
				createNew = true;
				msgOk = _("Host added");
				msgFail = _("Cannot add host");
			}

			// updating an existing discovered host
			Map host = null;
			if (!createNew && Nest.value(hostOld,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED) {
				host = map(
					"hostid", get_request("hostid"),
					"status", get_request("status"),
					"inventory", (Nest.as(get_request("inventory_mode")).asInteger() != HOST_INVENTORY_DISABLED) ? get_request("host_inventory", array()) : array()
				);
			} else {// creating or updating a normal host
				CArray<Map> macros = get_request("macros", array());
				CArray<Map> interfaces = get_request("interfaces", array());
				CArray templates = get_request("templates", array());
				CArray groups = get_request("groups", array());

				CArray linkedTemplates = Clone.deepcopy(templates);
				templates = array();
				for(Object templateId : linkedTemplates) {
					templates.add(map("templateid", templateId));
				}

				for (Entry<Object, Map> e : interfaces.entrySet()) {
				    Object key = e.getKey();
				    Map iface = e.getValue();
					if (rda_empty(Nest.value(iface,"ip").$()) && rda_empty(Nest.value(iface,"dns").$())) {
						unset(iface,key);
						continue;
					}

					if (Nest.value(iface,"isNew").asBoolean()) {
						unset(interfaces.get(key),"interfaceid");
					}
					unset(interfaces.get(key),"isNew");
					Nest.value(interfaces,key,"main").$(0);
				}

				int[] interfaceTypes = new int[]{INTERFACE_TYPE_AGENT, INTERFACE_TYPE_SNMP, INTERFACE_TYPE_JMX, INTERFACE_TYPE_IPMI};
				for(int type : interfaceTypes) {
					if (isset(Nest.value(_REQUEST,"mainInterfaces").asCArray(),type)) {
						Object idx = Nest.value(_REQUEST,"mainInterfaces",type).$();
						if(idx instanceof String && ((String)idx).length()==0){
							idx = 0;
						}
						Nest.value(interfaces,idx,"main").$("1");
					}
				}

				// ignore empty new macros, i.e., macros rows that have not been filled
				for (Entry<Object, Map> e : macros.entrySet()) {
				    Object key = e.getKey();
				    Map macro = e.getValue();
					if (!isset(macro,"hostmacroid") && rda_empty(Nest.value(macro,"macro").$()) && rda_empty(Nest.value(macro,"value").$())) {
						unset(macros,key);
					}
				}

				for (Entry<Object, Map> e : macros.entrySet()) {
				    Object key = e.getKey();
				    Map macro = e.getValue();
					// transform macros to uppercase {_aaa} => {$AAA}
					Nest.value(macros,key,"macro").$(rda_strtoupper(Nest.value(macro,"macro").asString()));
				}

				// create new group
				if (!rda_empty(Nest.value(_REQUEST,"newgroup").$())) {
					CArray<Long[]> newGroup = API.Call(new Wrapper<CArray<Long[]>>() {
						@Override protected CArray<Long[]> doCall() throws Throwable {
							return  API.HostGroup(getIdentityBean(), executor).create(array((Map)map("name", Nest.value(_REQUEST,"newgroup").$())));
						}
					},null);
					if (empty(newGroup)) {
						throw new Exception();
					}

					groups.add(reset(newGroup.get("groupids")));
					
					//获取必要的参数
					CHostGet hoptions = new CHostGet();
					hoptions.setEditable(true);
					hoptions.setHostIds(get_request_asLong("hostid"));
					hoptions.setOutput(new String[]{"hostid"});
					hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
					final CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
						
					CTemplateGet toptions = new CTemplateGet();
					toptions.setTemplateIds(get_request_asLong("hostid"));
					toptions.setOutput(new String[]{"templateid"});
					final CArray<Map>  _templates = API.Template(getIdentityBean(), executor).get(toptions);
					
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(reset(newGroup.get("groupids")));
					hgoptions.setOutput(API_OUTPUT_EXTEND);
					final CArray<Map> _groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
					Map group = reset(_groups);
					
					//添加菜单缓存
					HostgroupsAction.addFuncWithRights(executor, getIdentityBean(), hosts, _templates, _groups, group);
				}

				groups = rda_toObject(groups, "groupid");
				
				host = map(
					"host", Nest.value(_REQUEST ,"host").$(),
					"name", Nest.value(_REQUEST,"visiblename").$(),
					"status", Nest.value(_REQUEST,"status").$(),
					"proxy_hostid", get_request("proxy_hostid", 0),
					"ipmi_authtype", get_request("ipmi_authtype"),
					"ipmi_privilege", get_request("ipmi_privilege"),
					"ipmi_username", get_request("ipmi_username"),
					"ipmi_password", get_request("ipmi_password"),
					"groups", groups,
					"templates", templates,
					"interfaces", interfaces,
					"macros", macros
				);

				if (!createNew) {
					Nest.value(host,"templates_clear").$(rda_toObject(get_request("clear_templates", array()), "templateid"));
				}
			}

			if(doHostInterfacesDupCheck(Nest.value(host, "interfaces").asCArray())){
				CArray<String> macrosMessage = doMacrosCheck();
				if(empty(macrosMessage)){
					Long hostId = null;
					final Map host_final=host;
					if (createNew) {
						CArray<Long[]> chostIds  = API.Call(new Wrapper<CArray<Long[]>>() {
							@Override protected CArray<Long[]> doCall() throws Throwable {
								return API.Host(getIdentityBean(), executor).create(array(host_final));
							}
						},null);

						if (!empty(chostIds)) {
							hostId = reset(chostIds.get("hostids"));
						} else {
							throw new Exception();
						}

						add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_HOST, hostId, Nest.value(host,"host").asString(), null, null, null);
					} else {
						hostId = Nest.value(_REQUEST,"hostid").asLong();
						Nest.value(host,"hostid").$(hostId);
						Boolean result=true;
						result = API.Call(new Wrapper<Boolean>() {
							@Override protected Boolean doCall() throws Throwable {
								return !empty(API.Host(getIdentityBean(), executor).update(array(host_final)));
							}
						});
						if (!result) {
							throw new Exception();
						}

						CHostGet hoptions = new CHostGet();
						hoptions.setHostIds(hostId);
						hoptions.setEditable(true);
						hoptions.setOutput(API_OUTPUT_EXTEND);
						CArray<Map> hostNews = API.Host(getIdentityBean(), executor).get(hoptions);
						Map hostNew = reset(hostNews);

						add_audit_ext(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_HOST, Nest.value(hostNew,"hostid").asLong(), Nest.value(hostNew,"host").asString(), "hosts", hostOld, hostNew);
					}
					
					boolean result = DBend(executor, true);
					show_messages(result, msgOk, msgFail);
					clearCookies(result);

					unset(_REQUEST,"form");
					unset(_REQUEST,"hostid");
				}else{
					CArray<String> macrosMessages = array();
					macrosMessages.putAll(macrosMessage);
					CArray<Map> macros = array();
					macros.putAll(get_request("macros", array()));
					String macro = "";
					for(int i=0,length=macrosMessages.size();i<length;i++){
						if(i!=0){
							macro = macrosMessages.get(i);
							macros.add(map("macro",macro,"value",""));
						}
					}
					Nest.value(_REQUEST, "macros").$(macros);
					info(macrosMessages.valuesAsString());
					show_messages(false, msgOk, msgFail);
				}
			}else{
				info(_("the interface is duplicate"));
				show_messages(false, msgOk, msgFail);
			}
		} catch (Exception e) {
			e.printStackTrace();
			DBend(executor, false);
			show_messages(false, msgOk, msgFail);
		}

		unset(_REQUEST,"save");
	}

	/**
	 * @param executor
	 */
	private void doMassUpdate(final SQLExecutor executor) {
		CArray<String> hostIds;
		hostIds = get_request("hosts", array());
		CArray visible = get_request("visible", array());
		Nest.value(_REQUEST,"proxy_hostid").$(get_request("proxy_hostid", 0));
		Nest.value(_REQUEST,"templates").$(get_request("templates", array()));

		//1代表的含义：用户勾选了groups或new_groups，但是没有选择对应的值
		if((isset(visible,"new_groups")&&empty(Nest.value(_REQUEST, "new_groups").$()))||(isset(visible,"groups")&&empty(Nest.value(_REQUEST, "groups").$()))){
			error(_("please select the group"));
			show_error_message(_("Cannot update hosts"));
		}else{
			try {
				DBstart(executor);
				
				// filter only normal hosts, ignore discovered
				CHostGet hoptions = new CHostGet();
				hoptions.setOutput(new String[]{"hostid"});
				hoptions.setHostIds(hostIds.valuesAsLong());
				hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
				CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
				hosts = map("hosts", hosts);

				String[] properties = new String[]{"proxy_hostid", "ipmi_authtype", "ipmi_privilege", "ipmi_username", "ipmi_password", "status"};
				CArray newValues = array();
				for(String property : properties) {
					if (isset(visible,property)) {
						Nest.value(newValues,property).$(Nest.value(_REQUEST,property).$());
					}
				}

				if (isset(visible,"inventory_mode")) {
					Nest.value(newValues,"inventory_mode").$(get_request("inventory_mode", HOST_INVENTORY_DISABLED));
					Nest.value(newValues,"inventory").$((Nest.value(newValues,"inventory_mode").asInteger() == HOST_INVENTORY_DISABLED)
						? array() : get_request("host_inventory", array()));
				}

				CArray templateIds = array();
				if (isset(visible,"templates")) {
					templateIds = Nest.value(_REQUEST,"templates").asCArray();
				}

				// add new or existing host groups
				CArray newHostGroupIds = array();
				if (isset(visible,"new_groups") && !empty(Nest.value(_REQUEST,"new_groups").$())) {
					if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
						CArray<Map> newGroups = array(); 
						for(Object newGroup : Nest.value(_REQUEST,"new_groups").asCArray()) {
							if (is_array(newGroup) && isset((Map)newGroup,"new")) {
								newGroups.add(map("name", Nest.value((Map)newGroup,"new").$()));
							} else {
								newHostGroupIds.add(newGroup);
							}
						}

						if (!empty(newGroups)) {
							final CArray<Map> newGroups_f = newGroups;
							CArray<Long[]> createdGroups = API.Call(new Wrapper<CArray<Long[]>>() {
								@Override
								protected CArray<Long[]> doCall() throws Throwable {
									return API.HostGroup(getIdentityBean(), executor).create(newGroups_f);
								}
							},null);
							
//								CArray<Long[]> createdGroups = API.HostGroup(getIdentityBean(), executor).create(newGroups);
							if (empty(createdGroups)) {//如果未创建成功，则抛出异常
								throw new Exception();
							}

							newHostGroupIds = !empty(newHostGroupIds)
								? array_merge(newHostGroupIds, CArray.valueOf(createdGroups.get("groupids")))
								: Nest.value(createdGroups,"groupids").asCArray();
						}
					} else {
						newHostGroupIds = get_request("new_groups",array());
					}
				}

				CArray replaceHostGroupsIds = null;
				if (isset(visible,"groups")) {
					if (isset(_REQUEST,"groups")) {
						replaceHostGroupsIds = !empty(newHostGroupIds)
							? array_unique(array_merge(get_request("groups",array()), newHostGroupIds))
							: Nest.value(_REQUEST,"groups").asCArray();
					} else if (!empty(newHostGroupIds)) {
						replaceHostGroupsIds = Clone.deepcopy(newHostGroupIds);
					}

					if (isset(replaceHostGroupsIds)) {
						CHostGroupGet hgoptions = new CHostGroupGet();
						hgoptions.setGroupIds(replaceHostGroupsIds.valuesAsLong());
						hgoptions.setEditable(true);
						hgoptions.setOutput(new String[]{"groupid"});
						Nest.value(hosts,"groups").$(API.HostGroup(getIdentityBean(), executor).get(hgoptions));
					} else {
						Nest.value(hosts,"groups").$(array());
					}
				} else if (!empty(newHostGroupIds)) {
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(newHostGroupIds.valuesAsLong());
					hgoptions.setEditable(true);
					hgoptions.setOutput(new String[]{"groupid"});
					
					//newHostGroups
					API.HostGroup(getIdentityBean(), executor).get(hgoptions);
				}

				if (isset(_REQUEST,"mass_replace_tpls")) {
					if (isset(_REQUEST,"mass_clear_tpls")) {
						CTemplateGet toptions = new CTemplateGet();
						toptions.setHostIds(hostIds.valuesAsLong());
						CArray<Map> hostTemplates = API.Template(getIdentityBean(), executor).get(toptions);

						CArray hostTemplateIds = rda_objectValues(hostTemplates, "templateid");
						CArray templatesToDelete = array_diff(hostTemplateIds, templateIds);

						Nest.value(hosts,"templates_clear").$(rda_toObject(templatesToDelete, "templateid"));
					}

					Nest.value(hosts,"templates").$(templateIds);
				}

				boolean result = !empty(API.Host(getIdentityBean(), executor).massUpdate(array_merge(hosts, newValues)));
				if (!result) {
					throw new Exception();
				}

				CArray add = array();
				if (!empty(templateIds) && isset(visible,"templates")) {
					Nest.value(add,"templates").$(templateIds);
				}

				// add new host groups
				if (!empty(newHostGroupIds) && (!isset(visible,"groups"))) {
					Nest.value(add,"groups").$(rda_toObject(newHostGroupIds, "groupid"));
				}

				if (!empty(add)) {
					Nest.value(add,"hosts").$(Nest.value(hosts,"hosts").$());
					result = !empty(API.Host(getIdentityBean(), executor).massAdd(add));
					if (!result) {
						throw new Exception();
					}
				}
				
				DBend(executor, true);
				
				show_message(_("Hosts updated"));
				clearCookies(true);

				unset(_REQUEST,"massupdate");
				unset(_REQUEST,"form");
				unset(_REQUEST,"hosts");
			} catch (Exception e) {
				DBend(executor, false);
				error(e.getMessage());
				show_error_message(_("Cannot update hosts"));
			}
			unset(_REQUEST,"save");
		}
	}

	protected String getAction(){
		return "hostMonitor.action";
	}
	
	protected void prepareEditData(Map data) {
	}
	
	/**检索条件表单项
	 * @return
	 */
	protected CArray getFileterRow(CArray filtercarray){
		return array(
			new CDiv(array(array(bold(_("Name")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_host", Nest.value(_REQUEST,"filter_host").asString(), 20)),"device_monitor"),
			new CDiv(array(array(bold(_("IP")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_ip", Nest.value(_REQUEST,"filter_ip").asString(), 20)),"device_monitor"),
			new CDiv(array(bold(_("Port")+NAME_DELIMITER), new CTextBox("filter_port", Nest.value(_REQUEST,"filter_port").asString(), 20),
			filtercarray),"device_monitor")//添加隐藏域参数
		);
	}
	
	/**设备列表表头
	 * @param form
	 * @return
	 */
	protected CArray getHeader(CForm form ){
		return array(
			new CCheckBox("all_hosts", false, "checkAll(\""+form.getName()+"\", \"all_hosts\", \"hosts\");"),
			make_sorting_header(_("equipmentname"), "name"),
			_("equipmenttype"),
			_("Applications"),
			_("Items"),
			_("trendchart"),
//			_("Move Events"),//去除活动事件字段
			_("networkinterface"),
			make_sorting_header(_("Status"), "status"),
			_("Availability")
		);
	}
	
	/**给设别列表每列赋值
	 * @param executor
	 * @param host
	 * @param items
	 * @param graphs
	 * @param description
	 * @param hostInterface
	 * @param status
	 * @return
	 */
	protected CArray<Serializable> getRowData(SQLExecutor executor, Map host, CArray items, CArray applications, CArray graphs, CArray description,  String hostInterface,CCol status) {
		/**
		 * @param 关联设备类型
		 */
		Long hostid = Nest.value(host, "hostid").asLong();//获取设备id
		//Long _groupid = Nest.value(host, "groupid").asLong();//获取分组id
		CHostGroupGet params = new CHostGroupGet();
		//params.setGroupIds(IMonGroup.showableGroups().keysAsLong());
		params.setOutput(Defines.API_OUTPUT_EXTEND);
		params.setHostIds(hostid);
		CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(params);//获取设备所属分组
		CArray<Map> results=array();
		for(Map group:groups){//将类型为5的设备类型过滤掉；注意：carray.remove(key)方法不起作用
			Long groupid=Nest.value(group, "groupid").asLong();
			if(IMonConsts.DISCOVERED_HOSTS != groupid){
				results.add(group);
			}
		}
		String type = Cphp.implode(",", FuncsUtil.rda_objectValues(results, "name"));
		
		//活动告警和活动故障
		/*HostOperationsAction hpAction = new HostOperationsAction();
		String alarmNum=hpAction.getEventList(executor,true,Nest.value(host,"hostid").asLong()).get(0) ;//活动告警数目
		String faultNum= hpAction.getEventList(executor,false,Nest.value(host,"hostid").asLong()).get(0);//活动故障数
		
		Object alarmLink = new CLink(_("activeAlarm"), "activealarm.action?groupid="+_groupid+"&hostid="+hostid);
		Object faultLink = new CLink(_("activeFault"), "activefault.action?groupid="+_groupid+"&hostid="+hostid);
		
		if("0".equals(alarmNum)){
			alarmLink = _("Active Trigger");
		}
		if("0".equals(faultNum)){
			faultLink = _("Active Error");
		}
		
		CArray activeEvent = array(alarmLink," ("+alarmNum+")",BR(),faultLink," ("+faultNum+")");*/
		return array(
			new CCheckBox("hosts["+Nest.value(host,"hostid").asString()+"]", false, null, Nest.value(host,"hostid").asString()),
			description,//设备名称
			type,//设备类型
			applications,//监控纬度
			items,//监控指标
			graphs,//图形数据
//			activeEvent,//活动事件---去除该字段
			hostInterface,//网络接口
			status,//状态
			getAvailabilityTable(host)//可用性
		);
	}
	/**将状态值代码转变为状态值对应的中文名，为公共方法，不如如何写入到工具类中，目前先写在这里
	 * @param status
	 * @return
	 */
	public static String status2str(int status) {
		CArray Status = CArray.map(new Object[] { 
			Integer.valueOf(0),  Cphp._("Enabled"), 
			Integer.valueOf(1),  Cphp._("Disabled") 
		});
		if (Status.containsKey(Integer.valueOf(status))) {
			return (String) Status.get(Integer.valueOf(status));
		}
		return Cphp._("Unknown");
	}
	 
	 /**状态值显示启用停用状态
	 * @param status
	 * @return
	 */
	public static String status2style(int status) {
		switch (status) {
		case 0:
			return "enabled";
		case 1:
			return "disabled";
		}
		return "unknown";
	}
	
	/**
	 * 状态文本设置   （状态：启用、停用、维护中）
	 * @param status
	 * @return
	 */
	public static String statusStr(SQLExecutor executor, Map host) {
		String statusCaption = null;
		boolean isstart=false;
		int status = Nest.value(host, "status").asInteger();
		switch(status){
			case Defines.HOST_STATUS_MONITORED:
				if(Defines.HOST_MAINTENANCE_STATUS_ON==Nest.value(host, "maintenance_status").asInteger()){
					statusCaption = Cphp._("maintenanced");
				}else{
					statusCaption = Cphp._("Monitoring");
				}
				break;
			case Defines.HOST_STATUS_NOT_MONITORED:
				statusCaption = Cphp._("Stop Monitoring");
				break;
			default:
				statusCaption = Cphp._("Unknown");
		}
		return statusCaption;
	}
	
	public boolean doHostInterfacesDupCheck(CArray<Map> interfaces){
		CArray<Map> interfacesCheck = Clone.deepcopy(interfaces);
		for(Map ifaceCheck:interfacesCheck){
			unset(ifaceCheck,"interfaceid");
			unset(ifaceCheck,"main");
		}
		int length = interfacesCheck.size();
		int lengthUni = interfacesCheck.unique().size();
		return length==lengthUni;
	}
	
	public CArray<String> doMacrosCheck(){
		CArray groupsToCheck = get_request("groups", array());
		CArray<Map> macrosToCheck = get_request("macros", array());
		CArray macrosCA = FuncsUtil.rda_objectValues(macrosToCheck, "macro");
		boolean dbFlag = false;
		boolean dbMongoFlag = false;
		boolean jmxTomcatFlag = false;
		boolean jmxWebLogicFlag = false;
		boolean jmxWebSphereFlag = false;
		boolean iaasFlag = false;
		CArray<String> macros = array();
		macros.add(_("please add the macros"));
		for(Object groupId:groupsToCheck){
			groupId = Nest.as(groupId).asLong();
			if(!dbFlag){
				if(Feature.dbIds.containsValue(groupId)){
					for(Object macro:Feature.dbMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					dbFlag = true;
				}
			}
			if(!dbMongoFlag){
				if(Feature.dbMongoIds.containsValue(groupId)){
					for(Object macro:Feature.dbMongoMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					dbMongoFlag = true;
				}
			}
			if(!jmxTomcatFlag){
				if(Feature.jmxTomcatIds.containsValue(groupId)){
					for(Object macro:Feature.jmxTomcatMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					jmxTomcatFlag = true;
				}
			}
			if(!jmxWebLogicFlag){
				if(Feature.jmxWebLogicIds.containsValue(groupId)){
					for(Object macro:Feature.jmxWebLogicMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					jmxWebLogicFlag = true;
				}
			}
			if(!jmxWebSphereFlag){
				if(Feature.jmxWebSphereIds.containsValue(groupId)){
					for(Object macro:Feature.jmxWebSphereMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					jmxWebSphereFlag = true;
				}
			}
			if(!iaasFlag){
				if(Feature.iaasIds.containsValue(groupId)){
					for(Object macro:Feature.iaasMacros){
						if(!macrosCA.containsValue(macro)&&!macros.containsValue(macro)){
							macros.add(Nest.as(macro).asString());
						}
					}
					iaasFlag = true;
				}
			}
		}
		return macros.size()==1?null:macros;
	}
	
	String sqlHost = "SELECT maintenanceid FROM maintenances_hosts WHERE hostid IN (#{hostIds})";
	String sqlGroup = "SELECT maintenanceid " +
					  "FROM maintenances_groups " +
					  "WHERE groupid IN (" +
					  "					 SELECT groupid " +
					  "					 FROM hosts_groups " +
					  "					 WHERE hostid IN (#{hostIds}) " +
					  ")";
	public boolean doHostMaintenanceCheck(SQLExecutor executor,Long... hostIds){
		Map paraMap = map("hostIds",hostIds);
		List<Map> dataHost = (List<Map>)executor.executeNameParaQuery(sqlHost, paraMap);
		List<Map> dataGroup = (List<Map>)executor.executeNameParaQuery(sqlGroup, paraMap);
		return (dataHost!=null&&dataHost.size()>0)||(dataGroup!=null&&dataGroup.size()>0)?false:true;
	}
	
	public String doGetMonCateHistoryValue(SQLExecutor executor,String idx){
		String sql = "SELECT p.* FROM profiles p WHERE p.tenantid=#{tenantid} AND p.userid=#{userId} AND p.idx=#{idx}"
				+ " ORDER BY p.userid,p.profileid";
		Map params = new HashMap();
		params.put("tenantid", getIdBean().getTenantId());
		params.put("userId", getIdBean().getUserId());
		params.put("idx", idx);
		List<Map> dbProfiles = executor.executeNameParaQuery(sql, params);
		return (dbProfiles!=null&&dbProfiles.size()>0)?Nest.value(dbProfiles.get(0),CProfile.getFieldByType((Integer)dbProfiles.get(0).get("type"))).asString():"";
	}
	
}
