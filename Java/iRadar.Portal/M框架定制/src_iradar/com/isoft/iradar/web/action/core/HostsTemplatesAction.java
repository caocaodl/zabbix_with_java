package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
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
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
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
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
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
import static com.isoft.iradar.inc.GraphsUtil.copyGraphToHost;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HostsUtil.updateHostStatus;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.getAvailabilityTable;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HttpTestUtil.copyHttpTests;
import static com.isoft.iradar.inc.ItemsUtil.copyApplications;
import static com.isoft.iradar.inc.ItemsUtil.copyItems;
import static com.isoft.iradar.inc.ItemsUtil.httpItemExists;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.copyTriggersToHosts;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
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
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class HostsTemplatesAction extends RadarBaseAction {
	
	private boolean exportData;
	private Long templateid;

	@Override
	protected void doInitPage() {
		if (isset(_REQUEST,"go") && "export".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"hosts")) {
			Nest.value(page,"file").$("rda_export_hosts.xml");
			Nest.value(page,"type").$(detect_page_type(PAGE_TYPE_XML));
			this.exportData = true;
		} else {
			page("title", _("Configuration of hosts"));
			page("file", getAction());
			templateid=Nest.value(_REQUEST,"templateid").asLong();
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("hist_arg", new String[] {"groupid"});
			page("scripts", new String[] {"multiselect.js"});
			this.exportData = false;
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groups",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"new_groups",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"hostids",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groupids",					array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"applications",				array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"groupid",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"hostid",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		"isset({form})&&{form}==\"update\""),
			"host",							array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({save})", _("Host name")),
			"visiblename",				array(T_RDA_STR, O_OPT, null,			null,		"isset({save})"),
			"proxy_hostid",				array(T_RDA_INT, O_OPT, P_SYS,		    DB_ID,		null),
			"status",						array(T_RDA_INT, O_OPT, null,			IN("0,1,3"), "isset({save})"),
			"newgroup",					array(T_RDA_STR, O_OPT, null,			null,		null),
			"interfaces",					array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({save})", _("Agent or SNMP or JMX or IPMI interface")),
			"mainInterfaces",			array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"templates",					array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"add_template",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"add_templates", 			array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"templates_rem",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"clear_templates", 		array(T_RDA_INT, O_OPT, null,			DB_ID,		null),
			"ipmi_authtype",			array(T_RDA_INT, O_OPT, null,			BETWEEN(-1, 6), null),
			"ipmi_privilege",			array(T_RDA_INT, O_OPT, null,			BETWEEN(0, 5), null),
			"ipmi_username",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"ipmi_password",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"mass_replace_tpls", 	array(T_RDA_STR, O_OPT, null,		null,		null),
			"mass_clear_tpls", 		array(T_RDA_STR, O_OPT, null,			null,		null),
			"inventory_mode", 		array(T_RDA_INT, O_OPT, null, 			IN(HOST_INVENTORY_DISABLED+","+HOST_INVENTORY_MANUAL+","+HOST_INVENTORY_AUTOMATIC), null),
			"host_inventory",			array(T_RDA_STR, O_OPT, P_UNSET_EMPTY,	null,		null),
			"macros_rem",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"macros",						array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"macro_new",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		"isset({macro_add})"),
			"value_new",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		"isset({macro_add})"),
			"macro_add",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"massupdate",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"visible",						array(T_RDA_STR, O_OPT, null,			null,		null),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"add_to_group",			array(T_RDA_INT, O_OPT, P_SYS|P_ACT,	DB_ID,		null),
			"delete_from_group", 	array(T_RDA_INT, O_OPT, P_SYS|P_ACT,	DB_ID,		null),
			"unlink",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"unlink_and_clear", 		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"masssave",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"clone",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"full_clone",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,		null,			null),
			"form",							array(T_RDA_STR, O_OPT, P_SYS,		null,			null),
			"form_refresh",				array(T_RDA_STR, O_OPT, null,		null,			null),
			// filter
			"filter_set",					array(T_RDA_STR, O_OPT, P_SYS,		null,			null),
			"filter_host",					array(T_RDA_STR, O_OPT, null,		null,			null),
			"filter_ip",						array(T_RDA_STR, O_OPT, null,		null,			null),
			"filter_dns",					array(T_RDA_STR, O_OPT, null,		null,			null),
			"filter_port",					array(T_RDA_STR, O_OPT, null,		null,			null),
			// ajax
			"favobj",						array(T_RDA_STR, O_OPT, P_ACT,		null,			null),
			"favref",						array(T_RDA_STR, O_OPT, P_ACT,		NOT_EMPTY,		"isset({favobj})"),
			"favstate",					array(T_RDA_INT, O_OPT, P_ACT,		NOT_EMPTY,		"isset({favobj})&&\"filter\"=={favobj}")
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
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
	}

	@Override
	public void doAction(SQLExecutor executor) {
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
		} else if (isset(_REQUEST,"full_clone") && isset(_REQUEST,"hostid")) {
			Nest.value(_REQUEST,"form").$("full_clone");
		} else if (isset(_REQUEST,"go") && "massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"masssave")) {
			hostIds = get_request("hosts", array());
			CArray visible = get_request("visible", array());
			Nest.value(_REQUEST,"proxy_hostid").$(get_request("proxy_hostid", 0));
			Nest.value(_REQUEST,"templates").$(get_request("templates", array()));

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

						if (isset(newGroups)) {
							CArray<Long[]> createdGroups = API.HostGroup(getIdentityBean(), executor).create(newGroups);
							if (!empty(createdGroups)) {
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

				@SuppressWarnings("unused")
				CArray<Map> newHostGroups = null;
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
					newHostGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
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
				show_error_message(_("Cannot update hosts"));
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"save")) {
			String msgOk = null,msgFail = null;
			try {
				DBstart(executor);
				
				boolean createNew;
				Map hostOld = null;
				if (isset(_REQUEST,"hostid") && !"full_clone".equals(Nest.value(_REQUEST,"form").asString())) {
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
							Nest.value(interfaces,Nest.value(_REQUEST,"mainInterfaces",type).$(),"main").$("1");
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
						CArray<Long[]> newGroup = API.HostGroup(getIdentityBean(), executor).create(array((Map)map("name", Nest.value(_REQUEST,"newgroup").$())));
						if (empty(newGroup)) {
							throw new Exception();
						}

						groups.add(reset(newGroup.get("groupids")));
					}

					groups = rda_toObject(groups, "groupid");

					host = map(
						"host", Nest.value(_REQUEST,"host").$(),
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
						"macros", macros,
						"inventory", (Nest.as(get_request("inventory_mode")).asInteger() != HOST_INVENTORY_DISABLED) ? get_request("host_inventory", array()) : null,
						"inventory_mode", get_request("inventory_mode")
					);

					if (!createNew) {
						Nest.value(host,"templates_clear").$(rda_toObject(get_request("clear_templates", array()), "templateid"));
					}
				}

				Long hostId = null;
				if (createNew) {
					CArray<Long[]> chostIds = API.Host(getIdentityBean(), executor).create(array(host));

					if (!empty(chostIds)) {
						hostId = reset(chostIds.get("hostids"));
					} else {
						throw new Exception();
					}

					add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_HOST, hostId, Nest.value(host,"host").asString(), null, null, null);
				} else {
					hostId = Nest.value(_REQUEST,"hostid").asLong();
					Nest.value(host,"hostid").$(hostId);

					if (empty(API.Host(getIdentityBean(), executor).update(array(host)))) {
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

				if ("full_clone".equals(Nest.value(_REQUEST,"form").asString())) {
					Long srcHostId = Nest.as(get_request("hostid")).asLong();

					if (!copyApplications(getIdentityBean(), executor,srcHostId, hostId)) {
						throw new Exception();
					}

					if (!copyItems(getIdentityBean(), executor,srcHostId, hostId)) {
						throw new Exception();
					}

					// copy web scenarios
					if (!copyHttpTests(getIdentityBean(), executor,srcHostId, hostId)) {
						throw new Exception();
					}

					// clone triggers
					CTriggerGet toptions = new CTriggerGet();
					toptions.setOutput(new String[]{"triggerid"});
					toptions.setHostIds(srcHostId);
					toptions.setInherited(false);
					CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
					if (!empty(triggers)) {
						if (!copyTriggersToHosts(getIdentityBean(), executor,rda_objectValues(triggers, "triggerid").valuesAsLong(), new Long[]{hostId}, srcHostId)) {
							throw new Exception();
						}
					}

					// clone discovery rules
					CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
					droptions.setOutput(new String[]{"itemid"});
					droptions.setHostIds(srcHostId);
					droptions.setInherited(false);
					CArray<Map> discoveryRules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
					if (!empty(discoveryRules)) {
						boolean copyDiscoveryRules = API.DiscoveryRule(getIdentityBean(), executor).copy(map(
							"discoveryids", rda_objectValues(discoveryRules, "itemid"),
							"hostids", array(hostId)
						));
						if (!copyDiscoveryRules) {
							throw new Exception();
						}
					}

					CGraphGet goptions = new CGraphGet();
					goptions.setHostIds(srcHostId);
					goptions.setSelectItems(new String[]{"type"});
					goptions.setOutput(API_OUTPUT_EXTEND);
					goptions.setInherited(false);
					goptions.setSelectHosts(new String[]{"hostid"});
					goptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
					CArray<Map> graphs = API.Graph(getIdentityBean(), executor).get(goptions);
					for(Map graph : graphs) {
						if (count(Nest.value(graph,"hosts").$()) > 1) {
							continue;
						}

						if (httpItemExists(Nest.value(graph,"items").asCArray())) {
							continue;
						}

						if (empty(copyGraphToHost(getIdentityBean(), executor, Nest.value(graph,"graphid").asLong(), hostId))) {
							throw new Exception();
						}
					}
				}
				
				boolean result = DBend(executor, true);
				show_messages(result, msgOk, msgFail);
				clearCookies(result);

				unset(_REQUEST,"form");
				unset(_REQUEST,"hostid");
			} catch (Exception e) {
				e.printStackTrace();
				DBend(executor, false);
				show_messages(false, msgOk, msgFail);
			}

			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"hostid")) {
			DBstart(executor);
			
			boolean result = !empty(API.Host(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"hostid").asLong()));
			result = DBend(executor, result);
			
			show_messages(result, _("Host deleted"), _("Cannot delete host"));
			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"hostid");
			}
			unset(_REQUEST,"delete");
			clearCookies(result);
		}  else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			hostIds = get_request("hosts", array());
			
			DBstart(executor);
			
			boolean goResult = !empty(API.Host(getIdentityBean(), executor).delete(hostIds.valuesAsLong()));
			goResult = DBend(executor, goResult);
			
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
					? _n("Host enabled", "Hosts enabled", updated)
					: _n("Host disabled", "Hosts disabled", updated);
				String messageFailed = enable
					? _n("Cannot enable host", "Cannot enable hosts", updated)
					: _n("Cannot disable host", "Cannot disable hosts", updated);
				show_messages(result, messageSuccess, messageFailed);
				clearCookies(result);
			}
		}
		
		/* Display */
		CWidget hostsWidget = new CWidget();

		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"groups", map(
				"real_hosts", true,
				"editable", true
			),
			"groupid", get_request("groupid", null)
		));

		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());
		Nest.value(_REQUEST,"hostid").$(get_request("hostid", 0));

		if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"hosts")) {
			hostsWidget.addPageHeader(_("CONFIGURATION OF HOSTS"));

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
		}  else if (isset(_REQUEST,"form")) {
			hostsWidget.addPageHeader(_("CONFIGURATION OF HOSTS"));

			CArray data = array();
			Long hostId = null;
			Map dbHost = null;
			if (!empty(hostId  = get_request("hostid", 0L))) {
				hostsWidget.addItem(get_header_host_table(getIdentityBean(), executor,"", Nest.value(_REQUEST,"hostid").asLong()));

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
		} else {
			CForm frmForm = new CForm();
			frmForm.cleanItems();
			
			frmForm.addItem(new CDiv(getFormBtns()));
			frmForm.addItem(new CVar("groupid", Nest.value(_REQUEST,"groupid").$(), "filter_groupid_id"));

			CArray<Map> hostcarry = array();//获取模型id对应的设备数
            CHostGet optionsget=new CHostGet();
			optionsget.setTemplateIds(templateid);
			optionsget.setOutput(Defines.API_OUTPUT_EXTEND);
			optionsget.setSelectParentTemplates(new String[]{"hostid", "name"});
			hostcarry = API.Host(getIdentityBean(), executor).get(optionsget);
			CComboBox comboBox=new CComboBox();//定义下来列表
			Set set=new HashSet();
			for(Map host: hostcarry){//通过设备，获取对应的设备类型
				String type=null;
				Long hostid = Nest.value(host, "hostid").asLong();//获取设备id
				
				CHostGroupGet params = new CHostGroupGet();
				params.setOutput(Defines.API_OUTPUT_EXTEND);
				params.setHostIds(hostid);
				CArray<Map> result = API.HostGroup(getIdentityBean(), executor).get(params);//获取设备所属分组
				CArray  obj = Nest.value(result, "0").asCArray();
				if(!empty(obj)){//获取设备类型
					type=Nest.value(obj, "name").asString();
					if(!set.contains(type)){
					   set.add(type);
					   comboBox.addItem(type,type);
					}
				}
			}
			
			CForm frmGroup = new CForm("get");
			//frmGroup.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB(true)));
			frmGroup.addItem(array(_("Group")+SPACE, comboBox));

			hostsWidget.addHeader(SPACE, frmGroup);
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(frmForm));
			hostsWidget.addItem(headerActions);
			hostsWidget.setRootClass("host-list");

			// filter
			CTable filterTable = new CTable("", "filter");
			//将模板id加入检索条件，并显示
			CInput temids =new CInput("text","templateid",templateid.toString());//在设备页面添加templateid参数
			CArray templateidarray=array(_("Templateid")+SPACE+_("like")+NAME_DELIMITER,temids);
			filterTable.addRow(getFileterRow(templateidarray));
			filterTable.addRow(getFileterRow());

			CButton filter = new CButton("filter", _("GoFilter"),
				"javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true); chkbxRange.clearSelectedOnFilterChange();"
			);
			filter.useJQueryStyle("main");

			CButton reset = new CButton("reset", _("Reset"), "javascript: clearAllForm(\"rda_filter\");");
			reset.useJQueryStyle();

			CDiv divButtons = new CDiv(array(filter, SPACE, reset));
			divButtons.setAttribute("style", "padding: 4px 0;");

			filterTable.addRow(new CCol(divButtons, "center", "4"));

			CForm filterForm = new CForm("get");
			filterForm.setAttribute("name", "rda_filter");
			filterForm.setAttribute("id", "rda_filter");
			filterForm.addItem(filterTable);

			hostsWidget.addFlicker(filterForm, Nest.as(CProfile.get(getIdentityBean(), executor,"web.hosts.filter.state", 0)).asInteger());

			// table hosts
			CForm form = new CForm();
			form.setName("hosts");

			CTableInfo table = new CTableInfo(_("No hosts found."));
			table.setHeader(getHeader(form));

			// get Hosts
			CArray<Map> hosts = array();

			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			String sortorder = getPageSortOrder(getIdentityBean(), executor);
			
			CHostGet options = null;
			Map<String, Object> config = select_config(getIdentityBean(), executor, true);
			
			if (pageFilter.$("groupsSelected").asBoolean()) {
				options = new CHostGet();
				options.setTemplateIds(templateid);//在检索中添加templateid条件
				options.setOutput(Defines.API_OUTPUT_EXTEND);
				if (pageFilter.$("groupid").asInteger()>0) {
					options.setGroupIds(pageFilter.$("groupid").asLong());
				}
				options.setEditable(true);
				options.setSortfield(sortfield);
				options.setSortorder(sortorder);
				options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				if(!empty(Nest.value(_REQUEST,"filter_host").$())){
					options.setSearch("name", Nest.value(_REQUEST,"filter_host").asString());
				}
				if(!empty(Nest.value(_REQUEST,"filter_ip").$())){
					options.setSearch("ip", Nest.value(_REQUEST,"filter_ip").asString());
				}
				if(!empty(Nest.value(_REQUEST,"filter_dns").$())){
					options.setSearch("dns", Nest.value(_REQUEST,"filter_dns").asString());
				}
				if(!empty(Nest.value(_REQUEST,"filter_port").$())){
					options.setFilter("port", Nest.value(_REQUEST,"filter_port").asString());
				}
				hosts = API.Host(getIdentityBean(), executor).get(options);
			} else {
				hosts = array();
			}

			// sorting && paging
			order_result(hosts, sortfield, sortorder);
			CTable paging = getPagingLine(getIdentityBean(), executor, hosts, array("hostid"));

			options = new CHostGet();
			//options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
			/*
			 * @author fengjinbing
			 * @describe templateid field link hosts table
			 * @time 2014-12-26
			 * */
			options.setTemplateIds(templateid);
			options.setOutput(Defines.API_OUTPUT_EXTEND);
			options.setSelectParentTemplates(new String[]{"hostid", "name"});
			options.setSelectInterfaces(API_OUTPUT_EXTEND);
			options.setSelectItems(API_OUTPUT_COUNT);
			options.setSelectDiscoveries(API_OUTPUT_COUNT);
			options.setSelectTriggers(API_OUTPUT_COUNT);
			options.setSelectGraphs(API_OUTPUT_COUNT);
			options.setSelectApplications(API_OUTPUT_COUNT);
			options.setSelectHttpTests(API_OUTPUT_COUNT);
			options.setSelectDiscoveryRule(new String[]{"itemid", "name"});
			options.setSelectHostDiscovery(new String[]{"ts_delete"});
			hosts = API.Host(getIdentityBean(), executor).get(options);
			order_result(hosts, sortfield, sortorder);

			// selecting linked templates to templates linked to hosts
			CArray templateIds = array();
			for (Map host : hosts) {
				templateIds = array_merge(templateIds, rda_objectValues(Nest.value(host,"parentTemplates").$(), "templateid"));
			}
			templateIds = array_unique(templateIds);

			CTemplateGet tplGet = new CTemplateGet();
			tplGet.setTemplateIds(templateIds.valuesAsLong());
			tplGet.setSelectParentTemplates(new String[]{"hostid", "name"});
			CArray<Map> templates = API.Template(getIdentityBean(), executor).get(tplGet);
			templates = rda_toHash(templates, "templateid");

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
				CArray applications = array(new CLink(_("Applications"), "policy_apps.action?groupid="+groupid+"&hostid="+hostid),
					" ("+Nest.value(host,"applications").$()+")");
				CArray items = array(new CLink(_("Items"), "policy_items.action?filter_set=1&hostid="+hostid),
					" ("+Nest.value(host,"items").$()+")");
				CArray triggers = array(new CLink(_("Triggers"), "triggers.action?groupid="+groupid+"&hostid="+hostid),
					" ("+Nest.value(host,"triggers").$()+")");
				CArray graphs = array(new CLink(_("Graphs"), "policy_graphs.action?groupid="+groupid+"&hostid="+hostid),
					" ("+Nest.value(host,"graphs").$()+")");
				CArray discoveries = array(new CLink(_("Discovery"), "host_discovery.action?&hostid="+hostid),
					" ("+Nest.value(host,"discoveries").$()+")");
				CArray httpTests = array(new CLink(_("Web"), "httpconf.action?&hostid="+hostid),
					" ("+Nest.value(host,"httpTests").$()+")");

				CArray description = array();

				if (isset(proxies,host.get("proxy_hostid"))) {
					description.add(Nest.value(proxies,host.get("proxy_hostid"),"host").asString()+NAME_DELIMITER);
				}
				if (!empty(Nest.value(host,"discoveryRule").$())) {
					description.add(new CLink(Nest.value(host,"discoveryRule","name").$(), "host_prototypes.action?parent_discoveryid="+Nest.value(host,"discoveryRule","itemid").$(), "parent-discovery"));
					description.add(NAME_DELIMITER);
				}

				description.add(new CLink(CHtml.encode(Nest.value(host,"name").asString()), getAction()+"?form=update&hostid="+(Nest.value(host,"hostid").asString()+url_param(getIdentityBean(), "groupid"))));

				String hostInterface = (Nest.value(iface,"useip").asInteger() == INTERFACE_USE_IP) ? Nest.value(iface,"ip").asString() : Nest.value(iface,"dns").asString();
				hostInterface += empty(Nest.value(iface,"port").$()) ? "" : NAME_DELIMITER+Nest.value(iface,"port").asString();

				String statusScript = null;
				String statusCaption = null;
				String statusClass = null;
				String statusUrl = null;
				switch (Nest.value(host,"status").asInteger()) {
					case HOST_STATUS_MONITORED:
						if (Nest.value(host,"maintenance_status").asInteger() == HOST_MAINTENANCE_STATUS_ON) {
							statusCaption  = _("In maintenance");
							statusClass  = "orange";
						} else {
							statusCaption = _("Monitored");
							statusClass = "enabled";
						}

						statusScript = "return Confirm("+rda_jsvalue(_("Disable host?"))+");";
						statusUrl = getAction()+"?hosts"+SQUAREBRACKETS+"="+Nest.value(host,"hostid").asString()+"&go=disable"+url_param(getIdentityBean(), "groupid");
						break;

					case HOST_STATUS_NOT_MONITORED:
						statusCaption = _("Not monitored");
						statusUrl = getAction()+"?hosts"+SQUAREBRACKETS+"="+Nest.value(host,"hostid").asString()+"&go=activate"+url_param(getIdentityBean(), "groupid");
						statusScript = "return Confirm("+rda_jsvalue(_("Enable host?"))+");";
						statusClass = "disabled";
						break;

					default:
						statusCaption = _("Unknown");
						statusScript = "return Confirm("+rda_jsvalue(_("Disable host?"))+");";
						statusUrl = getAction()+"?hosts"+SQUAREBRACKETS+"="+Nest.value(host,"hostid").asString()+"&go=disable"+url_param(getIdentityBean(), "groupid");
						statusClass = "unknown";
				}

				CLink status = new CLink(statusCaption, statusUrl, statusClass, statusScript);

				Object hostTemplates = null;
				if (empty(Nest.value(host,"parentTemplates").$())) {
					hostTemplates = "-";
				} else {
					hostTemplates = array();
					CArray<Map> parentTemplates = Nest.value(host,"parentTemplates").asCArray();
					order_result(Nest.value(host,"parentTemplates").asCArray(), "name");

					for (Map template : parentTemplates) {
						CArray caption = array();
						caption.add(new CLink(CHtml.encode(Nest.value(template,"name").asString()), "templates.action?form=update&templateid="+Nest.value(template,"templateid").asString(), "unknown"));

						CArray<Map> subParentTemplates = Nest.value(templates,template.get("templateid"),"parentTemplates").asCArray();
						if (!empty(subParentTemplates)) {
							order_result(subParentTemplates, "name");

							caption.add(" (");
							for (Map tpl : subParentTemplates) {
								caption.add(new CLink(CHtml.encode(Nest.value(tpl,"name").asString()),"templates.action?form=update&templateid="+Nest.value(tpl,"templateid").asString(), "unknown"));
								caption.add(", ");
							}
							array_pop(caption);
							caption.add(")");
						}

						((CArray)hostTemplates).add(caption);
						((CArray)hostTemplates).add(", ");
					}

					if (!empty(hostTemplates)) {
						array_pop((CArray)hostTemplates);
					}
				}
				table.addRow(getRowData(executor, host, applications, items, triggers, graphs, discoveries, httpTests, description, hostInterface, status, hostTemplates));
			}

			CComboBox goBox = new CComboBox("go");
			goBox.addItem("export", _("Export selected"));
			goBox.addItem("massupdate", _("Mass update"));
			CComboItem goOption = new CComboItem("activate", _("Enable selected"));
			goOption.setAttribute("confirm", _("Enable selected hosts?"));
			goBox.addItem(goOption);
			goOption = new CComboItem("disable", _("Disable selected"));
			goOption.setAttribute("confirm", _("Disable selected hosts?"));
			goBox.addItem(goOption);
			goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", _("Delete selected hosts?"));
			goBox.addItem(goOption);
			CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
			goButton.setAttribute("id", "goButton");

			rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");

			form.addItem(array(table, paging, get_table_header(array(goBox, goButton))));
			hostsWidget.addItem(form);
		}

		hostsWidget.show();
	}

	protected String getAction(){
		return "hosts_templates.action";
	}
	
	protected void prepareEditData(Map data) {
	}
	
	protected CArray getFormBtns() {
		return array(
				new CSubmit("form", _("Create host"))
//				,new CButton("form", _("Import"), "redirect(\"conf.import.action?rules_preset=host\")")
			);
	}
	
	protected CArray getFileterRow(CArray templateidarray){
		return array(
				templateidarray,
				array(array(bold(_("Name")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_host", Nest.value(_REQUEST,"filter_host").asString(), 20))
			);
		
	}
	
	protected CArray getFileterRow(){
		return array(
				array(array(bold(_("DNS")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_dns", Nest.value(_REQUEST,"filter_dns").asString(), 20)),
				array(array(bold(_("IP")), SPACE+_("like")+NAME_DELIMITER), new CTextBox("filter_ip", Nest.value(_REQUEST,"filter_ip").asString(), 20)),
				array(bold(_("Port")+NAME_DELIMITER), new CTextBox("filter_port", Nest.value(_REQUEST,"filter_port").asString(), 20))
			);
		
	}

	protected CArray getHeader(CForm form ){
		return array(
				new CCheckBox("all_hosts", false, "checkAll(\""+form.getName()+"\", \"all_hosts\", \"hosts\");"),
				make_sorting_header(_("Name"), "name"),
				_("Applications"),
				_("Items"),
				_("Triggers"),
				_("Graphs"),
				_("Discovery"),
				_("Web"),
				_("Interface"),
				_("Templates"),
				make_sorting_header(_("Status"), "status"),
				_("Availability")
			);
	}
	
	/**
	 * @param host
	 * @param applications
	 * @param items
	 * @param triggers
	 * @param graphs
	 * @param discoveries
	 * @param httpTests
	 * @param description
	 * @param hostInterface
	 * @param status
	 * @param hostTemplates
	 * @return
	 */
	protected CArray<Serializable> getRowData(SQLExecutor executor, Map host, CArray applications, CArray items, CArray triggers, CArray graphs,
			CArray discoveries, CArray httpTests, CArray description, String hostInterface, CLink status, Object hostTemplates) {
		return array(
			new CCheckBox("hosts["+Nest.value(host,"hostid").asString()+"]", false, null, Nest.value(host,"hostid").asString()),
			description,
			applications,
			items,
			triggers,
			graphs,
			discoveries,
			httpTests,
			hostInterface ,
			new CCol(hostTemplates, "wraptext"),
			status,
			getAvailabilityTable(host)
		);
	}
	
}
