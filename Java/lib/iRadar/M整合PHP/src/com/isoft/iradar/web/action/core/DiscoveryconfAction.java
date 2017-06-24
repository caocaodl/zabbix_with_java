package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.get_discovery_rule_by_druleid;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.validate_port_list;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DiscoveryconfAction extends RadarBaseAction {
	
	CArray<Map> dbDRule;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of discovery rules"));
		page("file", "discoveryconf.action");
		page("hist_arg", new String[] {});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"druleid" ,						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name" ,						array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
			"proxy_hostid" ,			array(T_RDA_INT, O_OPT, null,	DB_ID,		"isset({save})"),
			"iprange" ,					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"delay" ,						array(T_RDA_INT, O_OPT, null,	BETWEEN(1, SEC_PER_WEEK), "isset({save})"),
			"status" ,						array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"uniqueness_criteria" , 	array(T_RDA_STR, O_OPT, null, null,	"isset({save})", _("Device uniqueness criteria")),
			"g_druleid" ,					array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"dchecks" ,					array(null, O_OPT, null,		null,		null),
			// actions
			"go" ,							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,						array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form" ,						array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,			array(T_RDA_INT, O_OPT, null,	null,		null),
			"output" ,						array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxaction" ,				array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxdata" ,					array(T_RDA_STR, O_OPT, P_ACT,	null,		null)
		);
		check_fields(getIdentityBean(), fields);		
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		Nest.value(_REQUEST,"status").$(isset(_REQUEST,"status") ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED);
		Nest.value(_REQUEST,"dchecks").$(get_request("dchecks", array()));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {		
		/** Permissions */
		if (isset(_REQUEST,"druleid")) {
			CDRuleGet option = new CDRuleGet();
			option.setDruleIds(get_request_asLong("druleid"));
			option.setOutput(new String[]{"name", "proxy_hostid", "iprange", "delay", "status"});
			option.setSelectDChecks(new String[]{"type", "key_", "snmp_community", "ports", "snmpv3_securityname", "snmpv3_securitylevel",
					"snmpv3_authpassphrase", "snmpv3_privpassphrase", "uniq", "snmpv3_authprotocol", "snmpv3_privprotocol",
					"snmpv3_contextname"});
			option.setEditable(true);
			dbDRule = API.DRule(getIdentityBean(), executor).get(option);
			if (empty(dbDRule)) {
				access_deny();
			}
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// ajax
		if (isset(_REQUEST,"output") && "ajax".equals(Nest.value(_REQUEST,"output").asString())) {
			AjaxResponse ajaxResponse = new AjaxResponse();

			if (isset(_REQUEST,"ajaxaction") && "validate".equals(Nest.value(_REQUEST,"ajaxaction").asString())) {
				CArray<Map> ajaxData = get_request("ajaxdata", array());
				for(Map check : ajaxData) {
					String field = Nest.value(check,"field").asString();
						if("port".equals(field)){
							if (!validate_port_list(Nest.value(check,"value").asString())) {
								ajaxResponse.error(_("Incorrect port range."));
							}
						} else
							if("itemKey".equals(field)){
							CItemKey itemKey = new CItemKey(Nest.value(check,"value").asString());

							if (!itemKey.isValid()) {
								ajaxResponse.error(_s("Invalid key \"%1$s\": %2$s.", Nest.value(check,"value").$(), itemKey.getError()));
							}
						}
				}
			}
			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Action */
		if (isset(_REQUEST,"save")) {
			CArray<Map> dChecks = get_request("dchecks", array());
			Integer uniq = get_request("uniqueness_criteria", 0);

			for (Entry<Object, Map> e : dChecks.entrySet()) {
			    Object dcnum = e.getKey();
			    //Map check = e.getValue();
				Nest.value(dChecks,dcnum,"uniq").$((uniq == dcnum) ? 1 : 0);
			}

			final Map discoveryRule = map(
				"name", get_request("name"),
				"proxy_hostid", get_request("proxy_hostid"),
				"iprange", get_request("iprange"),
				"delay", get_request("delay"),
				"status", get_request("status"),
				"dchecks", dChecks
			);

			CArray<Long[]> result;
			String msgOk,msgFail;
			if (isset(_REQUEST,"druleid")) {
				Nest.value(discoveryRule,"druleid").$(get_request("druleid"));
				result = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.DRule(getIdentityBean(), executor).update(array(discoveryRule));
					}
				}, null);
				msgOk = _("Discovery rule updated");
				msgFail = _("Cannot update discovery rule");
			} else {
				result = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.DRule(getIdentityBean(), executor).create(array(discoveryRule));
					}
				}, null);
				msgOk = _("Discovery rule created");
				msgFail = _("Cannot create discovery rule");
			}

			show_messages(!empty(result), msgOk, msgFail);

			if (!empty(result)) {
				Long druleid = reset(Nest.array(result,"druleids").asLong());
				add_audit(getIdentityBean(), executor, isset(discoveryRule,"druleid") ? AUDIT_ACTION_UPDATE : AUDIT_ACTION_ADD,
					AUDIT_RESOURCE_DISCOVERY_RULE,
					"["+druleid+"] "+Nest.value(discoveryRule,"name").asString()
				);
				unset(_REQUEST,"form");
				clearCookies(!empty(result));
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"druleid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.DRule(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"druleid").asLong()));
				}
			});
			show_messages(result, _("Discovery rule deleted"), _("Cannot delete discovery rule"));
			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"druleid");
				clearCookies(result);
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_druleid")) {
			boolean result = true;
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED;
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
			int updated = 0;
			
			DBstart(executor);
			
			Map params = new HashMap();
			params.put("status", status);
			for(Object druleId : get_request("g_druleid", array())) {
				params.put("druleid", druleId);
				result &= DBexecute(executor,"UPDATE drules SET status=#{status} WHERE druleid=#{druleid}",params);

				if (result) {
					Map druleData = get_discovery_rule_by_druleid(getIdentityBean(), executor, Nest.as(druleId).asString());
					add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_DISCOVERY_RULE, "["+druleId+"] "+Nest.value(druleData,"name").$());
				}
				updated++;
			}

			String messageSuccess = enable
				? _n("Discovery rule enabled", "Discovery rules enabled", updated)
				: _n("Discovery rule disabled", "Discovery rules disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable discovery rule", "Cannot enable discovery rules", updated)
				: _n("Cannot disable discovery rule", "Cannot disable discovery rules", updated);

			result = DBend(executor, result);
			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_druleid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.DRule(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"g_druleid").asLong()));
				}
			});
			show_messages(result, _("Discovery rules deleted"), _("Cannot delete discovery rules"));
			clearCookies(result);
		}

		/* Display */
		if (isset(_REQUEST, "form")) {
			Map data = map(
				"druleid" , get_request("druleid"),
				"drule" , array(),
				"form" , get_request("form"),
				"form_refresh" , get_request("form_refresh", 0)
			);

			// get drule
			if (isset(data,"druleid") && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"drule").$(reset(dbDRule));
				Nest.value(data,"drule","uniqueness_criteria").$(-1);

				if (!empty(Nest.value(data,"drule","dchecks").$())) {
					CArray<Map> dchecks = Nest.value(data,"drule","dchecks").asCArray();
					for (Map dcheck  : dchecks) {
						if (!empty(Nest.value(dcheck,"uniq").$())) {
							Nest.value(data,"drule","uniqueness_criteria").$(Nest.value(dcheck,"dcheckid").$());
						}
					}
				}
			} else {
				Nest.value(data,"drule","proxy_hostid").$(get_request("proxy_hostid", 0));
				Nest.value(data,"drule","name").$(get_request("name", ""));
				Nest.value(data,"drule","iprange").$(get_request("iprange", "192.168.0.1-254"));
				Nest.value(data,"drule","delay").$(get_request("delay", SEC_PER_HOUR));
				Nest.value(data,"drule","status").$(get_request("status", DRULE_STATUS_ACTIVE));
				Nest.value(data,"drule","dchecks").$(get_request("dchecks", array()));
				Nest.value(data,"drule","nextcheck").$(get_request("nextcheck", 0));
				Nest.value(data,"drule","uniqueness_criteria").$(get_request("uniqueness_criteria", -1));
			}

			if (!empty(Nest.value(data,"drule","dchecks").$())) {
				CArray<Map> dchecks = Nest.value(data,"drule","dchecks").asCArray();
				for (Entry<Object, Map> e : dchecks.entrySet()) {
		            Object id = e.getKey();
		            Map dcheck = e.getValue();
					Nest.value(data,"drule","dchecks",id,"name").$(discovery_check2str(
						Nest.value(dcheck,"type").asInteger(),
						isset(dcheck,"key_") ? Nest.value(dcheck,"key_").asString() : "",
						isset(dcheck,"ports") ? Nest.value(dcheck,"ports").asString() : ""
					));
				}
				order_result(dchecks, "name");
			}

			// get proxies
			CProxyGet options = new CProxyGet();
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> proxies = API.Proxy(getIdentityBean(), executor).get(options);
			Nest.value(data,"proxies").$(proxies);
			order_result(proxies, "host");
			
			// render view
			CView discoveryView = new CView("configuration.discovery.edit", data);
			discoveryView.render(getIdentityBean(), executor);
			discoveryView.show();
		} else {
			Map data = array();

			// get drules
			CDRuleGet options = new CDRuleGet();
			options.setOutput(new String[]{"proxy_hostid", "name", "status", "iprange", "delay"});
			options.setSelectDChecks(new String[]{"type"});
			options.setEditable(true);
			CArray<Map> drules = API.DRule(getIdentityBean(), executor).get(options);
			Nest.value(data,"drules").$(drules);

			if (!empty(drules)) {
				for (Entry<Object, Map> e : drules.entrySet()) {
		            Object key = e.getKey();
		            Map drule = e.getValue();
		            
					// checks
		            CArray checks = array();	            
		            CArray<Map> dchecks = Nest.value(drule,"dchecks").asCArray();
					for (Map check : dchecks) {
						Nest.value(checks,check.get("type")).$(discovery_check_type2str(Nest.value(check,"type").asInteger()));
					}
					order_result(checks);
					Nest.value(drules,key,"checks").$(checks);

					// description
					Nest.value(drules,key,"description").$(array());
					if (!empty(Nest.value(drule,"proxy_hostid").$())) {
						Map proxy = get_host_by_hostid(getIdentityBean(), executor,Nest.value(drule,"proxy_hostid").asLong());
						array_push(Nest.value(drules,key,"description").asCArray(), Nest.value(proxy,"host").asString()+NAME_DELIMITER);
					}
				}

				order_result(drules, getPageSortField(getIdentityBean(), executor,"name"), getPageSortOrder(getIdentityBean(), executor));
			}

			// get paging
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,drules, array("druleid")));

			// render view
			CView discoveryView = new CView("configuration.discovery.list", data);
			discoveryView.render(getIdentityBean(), executor);
			discoveryView.show();
		}
	}

}
