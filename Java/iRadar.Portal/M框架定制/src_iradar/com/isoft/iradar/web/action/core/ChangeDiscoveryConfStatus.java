package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ACTION;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_status2style;
import static com.isoft.iradar.inc.DiscoveryUtil.get_discovery_rule_by_druleid;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.validate_port_list;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 策略中心-发现规则列表状态修改Ajax请求操作
 * @author HP Pro2000MT
 *
 */
public class ChangeDiscoveryConfStatus extends RadarBaseAction{
	private final static int ACTION_EVENT_SOURCE = EVENT_SOURCE_DISCOVERY;
	CArray<Map> dbDRule;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of discovery rules"));
		page("file", "changeDiscoveryStatus.action");
		page("scripts", new String[] { "multiselect.js" });
		page("hist_arg", new String[] {});
		page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//设置请求参数中的数据源
		Nest.value(_REQUEST, "eventsource").$(ACTION_EVENT_SOURCE);
		CArray fields = map(
			"druleid" ,					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name" ,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
			"proxy_hostid" ,			array(T_RDA_INT, O_OPT, null,	DB_ID,		"isset({save})"),
			"iprange" ,					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"delay" ,					array(T_RDA_INT, O_OPT, null,	BETWEEN(1, SEC_PER_WEEK), "isset({save})"),
			"status" ,					array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"uniqueness_criteria" , 	array(T_RDA_STR, O_OPT, null, null,	"isset({save})", _("Device uniqueness criteria")),
			"g_druleid" ,				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"dchecks" ,					array(null, O_OPT, null,		null,		null),
			// actions
			"go" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,			array(T_RDA_INT, O_OPT, null,	null,		null),
			"output" ,					array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxaction" ,				array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxdata" ,				array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			//发现动作
			"evaltype",							array(T_RDA_INT, O_OPT, null, IN(array(ACTION_EVAL_TYPE_AND_OR, ACTION_EVAL_TYPE_AND, ACTION_EVAL_TYPE_OR)), "isset({save})"),
			"esc_period",						array(T_RDA_INT, O_OPT, null,	BETWEEN(60, 999999), null, _("Default operation step duration")),
			"status",								array(T_RDA_INT, O_OPT, null,	IN(array(ACTION_STATUS_ENABLED, ACTION_STATUS_DISABLED)), null),
			"recovery_msg",					array(T_RDA_INT, O_OPT, null,	null,		null),
			"r_shortdata",						array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery subject")),
			"r_longdata",						array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery message")),
			"g_actionid",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"conditions",							array(null,		O_OPT,	null,	null,		null),
			"new_condition",					array(null,		O_OPT,	null,	null,		"isset({add_condition})"),
			"operations",						array(null,		O_OPT,	null,	null,		"isset({save})"),
			"edit_operationid",				array(null,		O_OPT,	P_ACT,	NOT_EMPTY,		null),
			"new_operation",					array(null,		O_OPT,	null,	null,		"isset({add_operation})"),
			"opconditions",						array(null,		O_OPT,	null,	null,		null),
			"new_opcondition",				array(null,		O_OPT,	null,	null,		"isset({add_opcondition})"),
			// actions
			"add_condition",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_condition", 	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"add_operation",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_operation", 	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"add_opcondition",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_opcondition", 	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null)
		);
		check_fields(getIdentityBean(), fields);		
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		Nest.value(_REQUEST,"status").$(isset(_REQUEST,"status") ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED);
		Nest.value(_REQUEST,"dchecks").$(get_request("dchecks", array()));
	}

	@Override
	protected void doPermissions(final SQLExecutor executor) {		
		/** Permissions */
		if (isset(_REQUEST,"druleid")) {
			final CDRuleGet option = new CDRuleGet();
			option.setDruleIds(get_request_asLong("druleid"));
			option.setOutput(new String[]{"name", "proxy_hostid", "iprange", "delay", "status"});
			option.setSelectDChecks(new String[]{"type", "key_", "snmp_community", "ports", "snmpv3_securityname", "snmpv3_securitylevel",
					"snmpv3_authpassphrase", "snmpv3_privpassphrase", "uniq", "snmpv3_authprotocol", "snmpv3_privprotocol",
					"snmpv3_contextname"});
			option.setEditable(true);
			dbDRule = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.DRule(getIdentityBean(), executor).get(option);
				}
			}, array());
			if (empty(dbDRule)) {
				access_deny();
			}
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	/*
	 * 发现策略状态修改，名称列也改变，避免状态改变查询不到设备，造成空异常
	 */
	@Override
	protected boolean doAjax(final SQLExecutor executor) {
		boolean result=true;
		String name = null;
		if(str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_druleid")){
			doListActivateAction(executor);//修改发现策略状态
			
			//获取该发现策略
			Long durleid = get_request("g_druleid",0L);
			final CDRuleGet options = new CDRuleGet();
			options.setDruleIds(durleid);
			options.setOutput(new String[]{"proxy_hostid", "name", "status", "iprange", "delay", "nextcheck"});//增加对nextcheck的获取
			options.setSelectDChecks(new String[]{"type"});
			options.setSelectDHosts(new String[] {"status", "dhostid"});//添加对发现设备的数据获取，当前只需要获取状态就可以了
			options.setEditable(true);
	 		CArray<Map> drules = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.DRule(getIdentityBean(), executor).get(options);
				}
			}, array());
	 		
	 		
	 		final CDServiceGet serviceGet = new CDServiceGet();
			serviceGet.setOutput(new String[]{"dhostid"});
			CArray<Map> dservices = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.DService(getIdentityBean(), executor).get(serviceGet);
				}
			}, array());
			Map dservicesHostidMap = EasyList.asIndexMap(dservices.toList(), "dhostid");			
	 		for(Map drule : drules) {
	 			Object dhostCtn;
	 			CArray<Map> dhosts = Nest.value(drule, "dhosts").asCArray();
	 			
				if(Cphp.empty(dhosts) || Cphp.empty(dservices)) {
					CSpan countCtn = new CSpan("0");
					countCtn.attr("style", "font-size: 0.9em;");
					dhostCtn = countCtn;
				}else {
					int upHostCount=0, downHostCount=0;
					for(Map dhost: dhosts) {//遍历发现策略发现的设备
						Object hostid = Nest.value(dhost, "dhostid").$();
						if(!dservicesHostidMap.containsKey(hostid)){
							continue;
						}
						
						if(Defines.DHOST_STATUS_ACTIVE == Nest.value(dhost, "status").asInteger()) {
							upHostCount++;
						}else {
							downHostCount++;
						}
					}
					CSpan countCtn = new CSpan();
					countCtn.attr("style", "font-size: 0.9em;");
					countCtn.addItem(new CSpan(upHostCount, "green"));
					countCtn.addItem("/");
					countCtn.addItem(new CSpan(downHostCount, "red"));
					String url = "'"+_("the results of discovery")+"', '"+RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX+"discovery.action?druleid="+Nest.value(drule,"druleid").$()+"'";
					dhostCtn = new CLink(countCtn, IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE);
				}
				
				if(Defines.DRULE_STATUS_ACTIVE == Nest.value(drule,"status").asInteger()) {//已启用返回设备名加设备数，已停用只返回设备名称
					name = new CLink(Nest.value(drule,"name").$(), "?form=update&druleid="+Nest.value(drule,"druleid").$(), discovery_status2style(Nest.value(drule,"status").asInteger())).toString()
							+"     ("+dhostCtn.toString()+")";
				}else{
					name = new CLink(Nest.value(drule,"name").$(), "?form=update&druleid="+Nest.value(drule,"druleid").$(), discovery_status2style(Nest.value(drule,"status").asInteger())).toString();
				}
				
	 		}
			echo(name);
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		
	}

	/**
	 * 列表页面启用禁用操作
	 * 
	 * @param executor
	 */
	private void doListActivateAction(SQLExecutor executor) {
		boolean result = true;
		boolean enable = ("activate".equals(get_request("go")));
		int status = enable ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED;
		int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
		int updated = 0;
		CArray actionIds = array();
		
		DBstart(executor);
		
		Map params = new HashMap();
	
		params.put("status", status);
		for(Object druleId : get_request("g_druleid", array())) {
			params.put("druleid", druleId);
			result &= DBexecute(executor,"UPDATE drules SET status=#{status} WHERE druleid=#{druleid}",params);

			if(result) {
				status = enable ? ACTION_STATUS_ENABLED : ACTION_STATUS_DISABLED;
				Long actionid = getActionIdByDiscoveryRuleId(executor, druleId);
				result &= DBexecute(executor,
					"UPDATE actions"+
					" SET status=#{status}"+
					" WHERE actionid=#{actionid}"
				,EasyMap.build(
					"status", status,
					"actionid", actionid
				));
				if (result) {
					actionIds.add(actionid);
				}
			}
			
			if (result) {
				Map druleData = get_discovery_rule_by_druleid(getIdentityBean(), executor,Nest.as(druleId).asString());
				add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_DISCOVERY_RULE, "["+druleId+"] "+Nest.value(druleData,"name").$());
				
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ACTION, " 操作： ["+implode(",", actionIds)+"] "+Nest.value(druleData,"name").$());
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
	}
	
	/**
	 * 通过发现规则ID，获取动作ID
	 * @param executor
	 * @param discoveryRuleId
	 * @return 不存在时，将返回null
	 */
	private Long getActionIdByDiscoveryRuleId(final SQLExecutor executor, Object discoveryRuleId) {
		Long actionId = null;
		if(!empty(discoveryRuleId)) {
			//通过name中关联的规则ID，获取action的ID
			final CActionGet actionGetParams = new CActionGet();
			actionGetParams.setOutput(new String[] {"actionid"});
			actionGetParams.setLimit(1);
			actionGetParams.setFilter("name", discoveryRuleId);
			CArray<Map> actions = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).get(actionGetParams);
				}
			}, array());
			if(!empty(actions)) {
				 actionId = Nest.value(reset(actions), "actionid").asLong();
			}
		}
		return actionId;
	}	
}
