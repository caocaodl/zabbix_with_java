package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_CONDITION_GT;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_RESPONSE_TIME;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCENARIO;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HttpTestUtil.get_httptest_by_httptestid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.web.Util.THttpconfUtil.doCheckUser;
import static com.isoft.iradar.web.Util.THttpconfUtil.getAlarmLineListTable;
import static com.isoft.iradar.web.Util.THttpconfUtil.getAlertLines;
import static com.isoft.iradar.web.Util.THttpconfUtil.getSysMediaTypes;
import static com.isoft.iradar.web.Util.THttpconfUtil.getUserMediatypes;
import static com.isoft.iradar.web.Util.THttpconfUtil.get_alarmLine_form;
import static com.isoft.iradar.web.Util.TvmUtil.show_messages;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class THttpconfAction extends RadarBaseAction {
	@Override
	protected void doInitPage() {
		page("title", "网站监察");
		page("file", "thttpconf.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("css", new String[] {"tenant/edit.css","tenant/supervisecenter/thttpconf.css"});
		if (isset(_REQUEST,"oprationType")) {
			page("type", detect_page_type(PAGE_TYPE_HTML));			
		}else{
			page("js", new String[] {"imon/common.thttpconf.js"});
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid", 				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,				null),
			"group_httptestid",		array(T_RDA_INT, O_OPT, null,	DB_ID,				null),
			// form
			"hostid", 				array(T_RDA_INT, O_OPT, P_SYS, DB_ID,          "isset({save})"),
			"httptestid", 			array(T_RDA_INT, O_NO,  P_SYS, DB_ID,                   "(isset({form})&&({form}==\"update\"))"),
			"name", 				array(T_RDA_STR, O_OPT, null,  NOT_EMPTY,               "isset({save})", _("Name")),
			"url" ,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,			"isset({save})", _("URL")),			
			"delay", 				array(T_RDA_INT, O_OPT, null,  BETWEEN(1, SEC_PER_DAY), "isset({save})", "监控频率"),
			
			//自定义告警线	
			"alarmLines" ,			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,		"isset({save})", "自定义报警线"),	
			"new_alarmLine" ,		array(T_RDA_STR, O_OPT, null,	null,		 null),
			"userMediatypes" ,		array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	 "isset({save})", "告警方式设置"),				
			"del_alarmLineid" ,		array(null,      O_OPT, P_ACT,	NOT_EMPTY,		null),
			"edit_alarmLineid" ,	array(null,      O_OPT, P_ACT,	NOT_EMPTY,		null),	

			"lineid" ,		 		array(T_RDA_STR,      O_OPT, null,	null,		null),
			"item_type" ,		 	array(T_RDA_INT,      O_OPT, null,	null,		null),	
			"condition" ,		 	array(T_RDA_INT,      O_OPT, null,	null,		null),	
			"timeout" ,		 		array(T_RDA_INT,      O_OPT, null,	null,		null),	
			"retry" ,		 		array(T_RDA_INT,      O_OPT, null,	null,		null),	
			"status", 				array(T_RDA_STR, 	  O_OPT, null,  null,       null),
			"avbRate" ,		 		array(T_RDA_INT,      O_OPT, null,	null,		null),
			"oprationType" ,		array(T_RDA_STR, 	  O_OPT, null,  null,	    null),
			"alarmLineid",			array(T_RDA_STR, 	  O_OPT, null,  null,	    null),
			"new_alarmLine_retry",	array(T_RDA_INT, 	  O_OPT, null,  null,	    null),
			"new_alarmLine_status",	array(T_RDA_INT, 	  O_OPT, null,  null,	    null),
			
			// actions
			"go", 					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),		
			"clone", 				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"save", 				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"delete", 				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"cancel", 				array(T_RDA_STR, O_OPT, P_SYS,	null,				null),
			"form", 				array(T_RDA_STR, O_OPT, P_SYS,	null,				null),
			"form_refresh", 		array(T_RDA_INT, O_OPT, null,	null,				null)
		);
				
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		
		if (!empty(Nest.value(_REQUEST,"steps").$())) {
			order_result(Nest.value(_REQUEST,"steps").asCArray(), "no");
		}		
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST, "httptestid") || !empty(Nest.value(_REQUEST, "group_httptestid").$())) {
			CArray testIds = array();
			if (isset(_REQUEST, "httptestid")) {
				testIds = Nest.value(_REQUEST, "httptestid").asCArray();
			}
			if (!empty(Nest.value(_REQUEST, "group_httptestid").$())) {
				testIds = array_merge(testIds,Nest.value(_REQUEST, "group_httptestid").asCArray());
			}
			if (!API.HttpTest(getIdentityBean(), executor).isWritable(testIds.valuesAsLong())) {
				access_deny();
			}
		}
			
		Nest.value(_REQUEST, "go").$(get_request("go", "none"));
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"oprationType")) { //ajax处理   局部更新
			String oprationType = Nest.value(_REQUEST, "oprationType").asString();
			Object obj=null;
			if("new".equals(oprationType)){ //点击新增按钮操作    包含切换操作
				CArray new_alarmLine=map();
				if(isset(_REQUEST, "lineid")){//修改状态下  切换
					Nest.value(new_alarmLine,  "lineid").$(Nest.value(_REQUEST, "lineid").$());
				}
				Nest.value(new_alarmLine,  "item_type").$(get_request("item_type", 0));
				Nest.value(new_alarmLine, "condition").$(get_request("condition", 1));
				Nest.value(new_alarmLine, "timeout").$(get_request("timeout", 15));
				Nest.value(new_alarmLine, "retry").$(get_request("retry", 1));
				Nest.value(new_alarmLine, "status").$(get_request("status", 0));
				Nest.value(new_alarmLine, "avbRate").$(get_request("avbRate", 10));	
				
				obj = get_alarmLine_form(executor, getIdentityBean(), new_alarmLine);	
			}else if("add".equals(oprationType)){ //点击 添加按钮操作
				Nest.value(_REQUEST, "alarmLines").$(get_request("alarmLines", array()));
				Map new_alarmLine = Nest.value(_REQUEST, "new_alarmLine").asCArray();
				//特殊封装   重试次数、状态数据
				Nest.value(_REQUEST, "new_alarmLine", "retry").$(get_request("new_alarmLine_retry", 1));
				Nest.value(_REQUEST, "new_alarmLine", "status").$(get_request("new_alarmLine_status", 0));
							
				if(isset(new_alarmLine, "lineid")){ //为修改的添加
					unset(Nest.value(_REQUEST,"alarmLines").asCArray(), Nest.value(new_alarmLine, "lineid").$());
					array_push(Nest.value(_REQUEST, "alarmLines").asCArray(), new_alarmLine);
				}else{//添加的添加
					array_push(Nest.value(_REQUEST, "alarmLines").asCArray(), new_alarmLine);
				}

				obj = getAlarmLineListTable(Nest.value(_REQUEST, "alarmLines").asCArray());
				
			}else if("edit".equals(oprationType)){ //修改
				CArray edit_alarmLine=map();
				Object alarmLineid = Nest.value(_REQUEST, "alarmLineid").$();
				
				Nest.value(edit_alarmLine, "lineid").$(alarmLineid);
				Nest.value(edit_alarmLine,  "item_type").$(Nest.value(_REQUEST, "alarmLines", alarmLineid, "item_type").$());
				Nest.value(edit_alarmLine, "condition").$(Nest.value(_REQUEST, "alarmLines", alarmLineid, "condition").$());
				Nest.value(edit_alarmLine, "timeout").$(Nest.value(_REQUEST, "alarmLines", alarmLineid, "timeout").$());
				Nest.value(edit_alarmLine, "retry").$(Nest.value(_REQUEST, "alarmLines", alarmLineid, "retry").$());
				Nest.value(edit_alarmLine, "status").$(Nest.value(_REQUEST, "alarmLines",alarmLineid, "status").$());
				Nest.value(edit_alarmLine, "avbRate").$(Nest.value(_REQUEST, "alarmLines",alarmLineid, "avbRate").$());					
				
				obj = get_alarmLine_form(executor, getIdentityBean(), edit_alarmLine);
			}else if("del".equals(oprationType)){ //删除
				unset(Nest.value(_REQUEST,"alarmLines").asCArray(), Nest.value(_REQUEST, "alarmLineid").$());
				
				obj = getAlarmLineListTable(Nest.value(_REQUEST, "alarmLines").asCArray());
			}
			
			Cphp.echo(String.valueOf(obj));
			return true;				
		}

		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		//租户Web服务监控放在 云主机组中
		Nest.value(_REQUEST, "groupid").$(IMonConsts.MON_VM);
		
		if (isset(_REQUEST,"delete") && isset(_REQUEST,"httptestid")) {
			boolean result = false;

			DBstart(executor);
			
			Map params = new HashMap();
			params.put("httptestid", Nest.value(_REQUEST,"httptestid").$());
			Map host = DBfetch(DBselect(executor,
				"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",params));

			Map httptest_data = get_httptest_by_httptestid(executor,Nest.value(_REQUEST,"httptestid").asLong());
			if (!empty(httptest_data)) {
				final Long actionid = getActionid(executor, Nest.value(_REQUEST,"httptestid").asLong());
				
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HttpTest(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"httptestid").asLong()));
					}
				});
				//删除对应的告警响应
				if(result){
					result = Call(new Wrapper<Boolean>() {//删除
						protected Boolean doCall() throws Throwable {
							return !empty(API.Action(getIdentityBean(), executor).delete(actionid));
						}
					});
				}
			}

			DBend(executor, result);
						
			show_messages(result, _("Web scenario deleted"), _("Cannot delete web scenario"));
			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCENARIO, _("WEB PRIFEREWS")+" ["+Nest.value(httptest_data,"name").$()+"] ["+
					Nest.value(_REQUEST,"httptestid").asString()+"] "+_("By host")+" ["+Nest.value(host,"host").$()+"]");
			}
			unset(_REQUEST,"httptestid");
			unset(_REQUEST,"form");
		} else if (isset(_REQUEST,"save")) {
			boolean issamename= true;
			if (isset(_REQUEST,"httptestid")||isset(_REQUEST,"hostid")) {//监察不同主机是否有相同网站监名称
				Map params = new HashMap();
				params.put("name", Nest.value(_REQUEST,"name").$());
				params.put("hostid", Nest.value(_REQUEST,"hostid").$());
				params.put("tenantid", getIdentityBean().getTenantId());
				Map nameExists = DBfetch(DBselect(executor,
					"SELECT ht.name FROM httptest ht"+
					" WHERE ht.name=#{name}"+
						" AND ht.hostid !=#{hostid} and ht.tenantid= #{tenantid}", 1, params));
				issamename=empty(nameExists);
				if (!issamename) {
					info(_s("t_httpconf_name_repeat"));
				}
			}
			
			
			//校验数据是否输入正确
			boolean flag = true;
			if(issamename){
				for(Map alarmLine: (CArray<Map>)Nest.value(_REQUEST, "alarmLines").asCArray()){
					if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_RESPONSE_TIME){//当前响应时间				
						flag = Nest.value(alarmLine, "timeout").asInteger()>0 && Nest.value(alarmLine, "timeout").asInteger()<86400;
						if(!flag){
							info(_s("t_httpconf_response_time"));
							break;
						}
					}else{//当日利用率
						flag =  Nest.value(alarmLine, "avbRate").asInteger()<100;
						if(!flag){
							info(_s("t_httpconf_available_rate"));
							break;
						}
					}	
				}
			}
			
			
			int action;
			String message_true,message_false = null;
			if (isset(_REQUEST,"httptestid")) {
				action = AUDIT_ACTION_UPDATE;
				message_true = _("Web Monitoring updated");
				message_false = _("Cannot update Web Monitoring");
			} else {
				action = AUDIT_ACTION_ADD;
				message_true = _("Web Monitoring added");
				message_false = _("Cannot add Web Monitoring");
			}			
			
			if(flag && issamename){
				try {
					DBstart(executor);
					//封装Web服务监控数据
					final Map httpTest = setHttptest();
				
					if (isset(_REQUEST,"httptestid")) {//修改        全部删除重新插入	
						final Long httptestid = Nest.value(_REQUEST,"httptestid").asLong();
						final Long actionid = getActionid(executor, httptestid);
						
						boolean	result = Call(new Wrapper<Boolean>() { //删除 Web服务监控、阀值规则
								protected Boolean doCall() throws Throwable {
									return !empty(API.HttpTest(getIdentityBean(), executor).delete(httptestid));
								}
							});				
						
						if(result){					
							result = empty(actionid)?true:Call(new Wrapper<Boolean>() {//删除告警响应
								protected Boolean doCall() throws Throwable {
									return !empty(API.Action(getIdentityBean(), executor).delete(actionid));
								}
							});
						}	
					} 
					//保存
					CArray<Long[]> result = Call(new Wrapper<CArray<Long[]>>() {
						protected CArray<Long[]> doCall() throws Throwable {
							return API.HttpTest(getIdentityBean(), executor).create(array(httpTest));
						}
					}, null);

					if (empty(result)) {
						throw new Exception();
					} else {
						clearCookies(!empty(result), Nest.value(_REQUEST,"hostid").asString());
						
						//保存阀值规则
						Nest.value(_REQUEST, "httptestid").$(result.get("httptestids"));
						CArray<Long[]> triggerids = saveTrigger(executor);
						
						//保存告警响应
						if(!empty(triggerids)){
							boolean actResult = saveAction(executor, triggerids);
						}
					}
					
					Long newhttptestid = reset(result.get("httptestids"));				
					

					Map host = get_host_by_hostid(getIdentityBean(), executor,Nest.value(_REQUEST,"hostid").asLong());
					add_audit(getIdentityBean(), executor,action, AUDIT_RESOURCE_SCENARIO, _("WEB PRIFEREWS")+" ["+Nest.value(_REQUEST,"name").asString()+"] ["+newhttptestid+"] "+_("By host")+" ["+Nest.value(host,"host").$()+"]");

					clearCookies(true);
					unset(_REQUEST,"httptestid");
					unset(_REQUEST,"form");
					show_messages(true, message_true);
					DBend(executor, true);
				} catch (Exception e) {
					DBend(executor, false);
					clearCookies(flag);
					String msg = e.getMessage();
					if (!empty(msg)) {
						error(msg);
					}
					show_messages(false, null, message_false);
					clearCookies(flag);
				}				
			}else{
				show_messages(false, message_true, message_false);
				clearCookies(flag);
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_httptestid")) {
			boolean result = true;
			CArray groupHttpTestId = get_request("group_httptestid",array());
			boolean enable = ("activate".equals(get_request("go")));
			final int status = enable ? HTTPTEST_STATUS_ACTIVE : HTTPTEST_STATUS_DISABLED;
			String statusName = enable ? "enabled" : "disabled";
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
			int updated = 0;
			
			DBstart(executor);

			Map params = new HashMap();
			for(final Object id : groupHttpTestId) {
				Map httpTestData = get_httptest_by_httptestid(executor,Nest.as(id).asLong());
				if (empty(httpTestData)) {
					continue;
				}
				result &= Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HttpTest(getIdentityBean(), executor).update(array((Map)map(
								"httptestid", id,
								"status", status
							))));
					}
				});

				if (result) {
					params.put("httptestid", id);
					Map host = DBfetch(DBselect(executor,
						"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",
						params
					));
					add_audit(getIdentityBean(), executor, auditAction, AUDIT_RESOURCE_SCENARIO,
						_("WEB PRIFEREWS")+" ["+Nest.value(httpTestData,"name").$()+"] ["+id+"] "+_("By host")+"["+Nest.value(host,"host").$()+"] "+statusName
					);
				}
				updated++;
			}

			String messageSuccess = enable
				? _n("Web scenario enabled", "Web scenarios enabled", updated)
				: _n("Web scenario disabled", "Web scenarios disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable web scenario", "Cannot enable web scenarios", updated)
				: _n("Cannot disable web scenario", "Cannot disable web scenarios", updated);
			
			result = DBend(executor, result);
			
			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		} /*else if ("clean_history".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_httptestid")) {
			boolean goResult = false;
			CArray group_httptestid = Nest.value(_REQUEST,"group_httptestid").asCArray();

			DBstart(executor);
			
			Map params = new HashMap();
			for(final Object id : group_httptestid) {
				Map httptest_data = get_httptest_by_httptestid(executor,Nest.as(id).asLong());
				if (empty(httptest_data)) {
					continue;
				}
				if (Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return delete_history_by_httptestid(executor,Nest.as(id).asLong());
					}
				})) {
					goResult = true;
					params.put("httptestid", id);
					DBexecute(executor,"UPDATE httptest SET nextcheck=0 WHERE httptestid=#{httptestid}",params);
					Map host = DBfetch(DBselect(executor,
						"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",params));

					add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCENARIO, _("WEB PRIFEREWS")+" ["+Nest.value(httptest_data,"name").$()+"] ["+id+
						"] "+_("By host")+" ["+Nest.value(host,"host").$()+"] history cleared");
				}
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("History cleared"), null);
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		} */else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_httptestid")) {
			boolean goResult = false;
			
			DBstart(executor);
			
			//批量删除时   先获得各Web服务监控对应的告警响应再删除
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> actionids = DBselect(executor,
					" SELECT a.actionid"+
					" FROM actions a"+
					" LEFT JOIN httptest t ON a.name=t.name"+
					" WHERE "+sqlParts.dual.dbConditionInt("t.httptestid", Nest.array(_REQUEST, "group_httptestid").asLong()),
					sqlParts.getNamedParams()
				);
			
			final CArray<Map> aids = rda_objectValues(actionids, "actionid");
			
			
			goResult = Call(new Wrapper<Boolean>() {//删除Web服务监控
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HttpTest(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"group_httptestid").asLong()));
				}
			});
			
			if(goResult && !empty(aids)){ //删除Web服务监控对应的告警响应
				goResult = Call(new Wrapper<Boolean>() {
					protected Boolean doCall() throws Throwable {
						return !empty(API.Action(getIdentityBean(), executor).delete(aids.valuesAsLong()));
					}
				});				
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("Web scenario deleted"), _("Cannot delete web scenario"));
			clearCookies(goResult);
		}

		/* Display */
		if (isset(_REQUEST, "form")) {
			Map params = new HashMap();
			params.put("tenantid", getIdentityBean().getTenantId());
				
			Map data = map(
				"hostid", get_request("hostid", 0),
				"httptestid",get_request("httptestid", null), 
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh")
			);
	
			params = new HashMap();
			
			if ((isset(_REQUEST, "httptestid") && !isset(_REQUEST, "form_refresh"))) {
				params.put("httptestid", Nest.value(_REQUEST,"httptestid").$());
				Map dbHttpTest = DBfetch(DBselect(executor,
					"SELECT ht.name, ht.delay, hs.url"+
					" FROM httptest ht INNER JOIN httpstep hs"+
					" WHERE ht.httptestid=#{httptestid} AND hs.httptestid=ht.httptestid",
					params
				));
				
				Nest.value(data, "name").$(Nest.value(dbHttpTest, "name").$());
				Nest.value(data, "delay").$(Nest.value(dbHttpTest, "delay").$());
				Nest.value(data, "url").$(Nest.value(dbHttpTest, "url").$());
				
				Nest.value(data, "alarmLines").$(getAlertLines(executor, Nest.value(_REQUEST, "httptestid").asLong()));
				
				Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
				Nest.value(data, "userMediatypes").$(getUserMediatypes(executor, Nest.value(_REQUEST, "httptestid").$()));
													
			} else {			
				Nest.value(data, "name").$(get_request("name", ""));
				Nest.value(data, "url").$(get_request("url", ""));
				Nest.value(data, "delay").$(get_request("delay", 60));
				Nest.value(data, "alarmLines").$(get_request("alarmLines", array()));
				
				Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
				Nest.value(data, "userMediatypes").$(getUserMediatypes(executor, Nest.value(_REQUEST, "httptestid").$()));
			}
			Map<String,String> virtmap=getVirtMap(getIdentityBean(),executor,0L);
			Nest.value(data, "virtmap").$(virtmap);
			
			// render view
			CView httpView = new CView("configuration.thttpconf.edit", data);
			httpView.render(getIdentityBean(), executor);
			httpView.show();
		} else {		
		/*	CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
					"groups", map(
						"editable", true
					),
					"hosts", map(
						"editable", true,
						"templated_hosts", true
					),
					"hostid", get_request("hostid"),
					"groupid", get_request("groupid")
				));*/
			
			CArray data = map(
					//"hostid",pageFilter.$("hostid").$(),
					//"pageFilter",pageFilter,					
					"httpTests",array(),
					"paging",null
				);			
			
			//if (pageFilter.$("hostsSelected").asBoolean()) {
				Map<String, Object> config = select_config(getIdentityBean(), executor);
				String sortfield = getPageSortField(getIdentityBean(), executor, "hostname");

				CHttpTestGet options = new CHttpTestGet();
				options.setEditable(true);
				options.setOutput(new String[] { "httptestid" });
				/*if(Nest.value(_REQUEST, "hostid").asLong() !=0){
					options.setHostIds(Nest.value(_REQUEST, "hostid").asLong());
				}*/
				options.setLimit(Nest.value(config, "search_limit").asInteger() + 1);
				
				options.setEditable(true);

				CArray<Map> httpTests = API.HttpTest(getIdentityBean(), executor).get(options);

				order_result(httpTests, sortfield, getPageSortOrder(getIdentityBean(), executor));

				CArray<Map> temp_httpTests =  (CArray<Map>) httpTests.clone();
				CTable paging = getPagingLine(getIdentityBean(), executor, temp_httpTests, array("httptestid"));
				

				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> dbHttpTests = DBselect(executor,
					"SELECT ht.httptestid,ht.name,hs.url,ht.delay,ht.hostid"+
						" FROM httptest ht"+
						" INNER JOIN httpstep hs ON hs.httptestid=ht.httptestid"+
						" WHERE "+sqlParts.dual.dbConditionInt("ht.httptestid", rda_objectValues(temp_httpTests, "httptestid").valuesAsLong()),
					sqlParts.getNamedParams()
				);
			
				httpTests = array();
				for(Map dbHttpTest : dbHttpTests) {
					Nest.value(httpTests,dbHttpTest.get("httptestid")).$(dbHttpTest);
				}
				
				order_result(httpTests, sortfield, getPageSortOrder(getIdentityBean(), executor));

				Nest.value(data, "httpTests").$(httpTests);
				Map<String,String> virtmap=getVirtMap(getIdentityBean(),executor,0L);
				Nest.value(data, "virtmap").$(virtmap);
			//}	
				Nest.value(data, "paging").$(paging);
			// render view
			CView httpView = new CView("configuration.thttpconf.list", data);
			httpView.render(getIdentityBean(), executor);
			httpView.show();
		}
	}
		
	/**
	 * 封装Web服务监控数据
	 */
	public Map setHttptest(){
		Map httpTest = map(
				"name", Nest.value(_REQUEST,"name").$(),
				"delay", Nest.value(_REQUEST,"delay").$(),
				"hostid", Nest.value(_REQUEST,"hostid").$(),
				"status", 0,
				"variables", "",
				"agent", "",
				"authentication", 0,
				"http_user", "",
				"http_password", "",
				"http_proxy", "",
				"retries", 1,
				"steps", array(setStep())
			);
		return httpTest;
	}
	
	/**
	 * 封装Web服务监控步骤数据
	 */
	public Map setStep(){
		Map step = map(
			"name",  get_request("name"),
			"no", 1,
			"url", get_request("url"),
			"timeout", 0,
			"posts", "",
			"required", "",
			"status_codes", "",
			"variables", ""
		);		
		return step;
	}
	
	/**
	 * 保存告警配置
	 */
	public CArray<Long[]> saveTrigger(final SQLExecutor executor){
		CArray<Map> alertLines = Nest.value(_REQUEST, "alarmLines").asCArray();
		//取Web服务监控所在设备名称
		Map params = new HashMap();
		params.put("hostid", Nest.value(_REQUEST, "hostid").$());
		Map hostM = DBfetch(DBselect(executor,
				"SELECT h.host FROM hosts h WHERE h.hostid=#{hostid}", params));		
		String host = hostM.get("host").toString();
		
		CArray<Long[]> savetriggerids = array();
		CArray<Long[]> result=null;
		if(alertLines!=null && !alertLines.isEmpty()){
			String name = Nest.value(_REQUEST, "name").asString();
			
			for(Map alertLine : alertLines){
				if(Nest.value(alertLine, "item_type").asInteger() == T_HTTPCONF_RESPONSE_TIME){ //当前响应时间
					String condition = Nest.value(alertLine, "condition").asInteger()==T_HTTPCONF_CONDITION_GT?"gt":"lt";
					Integer timeout = Nest.value(alertLine, "timeout").asInteger();
					Integer retry = Nest.value(alertLine, "retry").asInteger(); //重试几次
					Integer status = Nest.value(alertLine, "status").asInteger();
					
					String expression = "{"+host+":web.test.time["+name+","+name+",resp].count(#"+retry+","+timeout+",\""+condition+"\")}";
					String description = name+": "+ _("Current ResponseTime")+"　连续　"+String.valueOf(retry)+_("of times")+"　"+TvmUtil.getLt(condition)+"　"+String.valueOf(timeout);
					//拼装trigger数据
					final Map trigger = map(
							"expression", expression,
							"description", expression+"="+description, //描述存放表达式  为修改时拆分方便
							"comments", "网站监控"+description,
							"status", status,
							"type"			, Defines.TRIGGER_MULT_EVENT_DISABLED
						);
					
					//保存  返回生成的triggerid
					result = Call(new Wrapper<CArray<Long[]>>() {
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Trigger(getIdentityBean(), executor).create(array(trigger));
						}
					}, null);
					//triggerid保存  
					Nest.value(savetriggerids, reset(result)[0]).$(reset(result)[0]);
					
				}else{ //当日可用率
					Integer avbRate = Nest.value(alertLine, "avbRate").asInteger();
					Integer status = Nest.value(alertLine, "status").asInteger();
					
					String expression = "{"+host+":web.test.fail["+name+"].avg(1d)}<"+(avbRate/100.0);
	                String description = name+": 当日可用率  小于"+ (avbRate/100.0);
					//拼装trigger数据
					final Map trigger = map(
							"expression", expression,
							"description", expression+"="+description, //描述存放表达式  为修改时拆分方便
							"comments", "网站监控"+description,
							"status", status,
							"type"			, Defines.TRIGGER_MULT_EVENT_DISABLED
						);
					//保存  返回生成的triggerid
					result = Call(new Wrapper<CArray<Long[]>>() {
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Trigger(getIdentityBean(), executor).create(array(trigger));
						}
					}, null);
					//triggerid保存  
					Nest.value(savetriggerids, reset(result)[0]).$(reset(result)[0]);
				}
			}
			
		}		
		return savetriggerids;
	}

	
	/**
	 * 保存动作
	 */
	public boolean saveAction(final SQLExecutor executor, CArray triggerid){	
		//用户对应的告警通知方式
		CArray<Map> userMediatypes= Nest.value(_REQUEST, "userMediatypes").$s();
		
		CArray<Map> mediatypeToUser=array();
		for(Map user: userMediatypes){
			 //因用户是从云平台取得   用户并不一定存在于监控数据库中，  所以对于要发送通知的用户，不存在则要插入到监控数据库中
			doCheckUser(executor, getIdentityBean().getTenantId(),user.get("userid").toString(), user.get("name").toString());
			
			//拼装数据     按通知方式进行人员的整理
			CArray<Map> umtids = Nest.value(user, "mediatypeids").$s();
			Object userid=null;
			for(Object umtid : umtids.keySet()){
				userid = user.get("userid");
				Nest.value(mediatypeToUser, umtid, userid).$s(true).put("userid", userid);
			}			
		}
		if(!empty(mediatypeToUser)){//只有用户选择了事件通知方式才能创建通知事件	
			//依据告警方式不同封装动作操作
			CArray<Map> operations=map();
			for(Entry meUsers : mediatypeToUser.entrySet()){
				//动作操作 公用部分
				CArray<Map> comm_operations=map(
						"evaltype", 0, // 且/或
						"esc_step_to", 1,  //步数
						"opmessage", map(
								"default_msg", 1, 
								"mediatypeid", meUsers.getKey(), 
								"subject",_("TENANT_ACTION_DEFAULT_SUBJ_TRIGGER"),
								"message",_("TENANT_ACTION_DEFAULT_MSG_TRIGGER")),
						"mediatypeid", 0,
						"esc_step_from", 1,
						"action", "create",
						"opmessage_usr", meUsers.getValue(),
						"esc_period", 0,
						"operationtype", 0
				);	
				
				operations.add(comm_operations);
			}
				
			//封装  条件
			CArray<Map> conditions=map();
			Set triggerids = triggerid.keySet();
			Map condTemp = null;
			for(Object tid : triggerids){
				condTemp=new HashMap();
				condTemp.put("conditiontype", 2); //2为阀值规则  
				condTemp.put("operator", 0);
				condTemp.put("value", tid);
				
				conditions.add(condTemp);
			}
				
			//封装   动作、条件、操作
			final Map action = map(
					"name", get_request("name"),
					"evaltype", ACTION_EVAL_TYPE_AND_OR,
					"status", get_request("status", ACTION_STATUS_ENABLED),
					"esc_period", 3600,
					"def_shortdata", _("TENANT_ACTION_DEFAULT_SUBJ_TRIGGER"),
					"def_longdata", _("TENANT_ACTION_DEFAULT_MSG_TRIGGER"),
					"recovery_msg", 0,
					"r_shortdata", _("TENANT_ACTION_DEFAULT_SUBJ_TRIGGER"),
					"r_longdata", _("TENANT_ACTION_DEFAULT_MSG_TRIGGER"),
					"conditions", conditions, 
					"operations", operations,
					"eventsource", 0 //触发器
				);
				
			boolean actResult = !empty(Call(new Wrapper<CArray<Long[]>>() {
				protected CArray<Long[]> doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).create(array(action));
				}
			}, null));
			
			return actResult;	
		}else{
			return false;
		}
	}	
	
	/**
	 * 查询Web服务监控对应的告警响应id
	 */
	public Long getActionid(SQLExecutor executor, Long httptestid){
		Map params=new HashMap();
		params.put("httptestid", httptestid);
		Map actionid = DBfetch(DBselect(executor,
				" SELECT a.actionid"+
				" FROM actions a"+
				" LEFT JOIN httptest t ON a.name=t.name"+
				" WHERE t.httptestid=#{httptestid}",
				params
			));
				
		return empty(actionid)?null:Long.parseLong(actionid.get("actionid").toString());		
	}	
	
	/**
	 * 获得Web服务监控步骤id
	 */
	public Long getStepId(SQLExecutor executor, Long httptestid){
		Map params=new HashMap();
		params.put("httptestid", httptestid);
		Map stepid = DBfetch(DBselect(executor,
				" SELECT s.httpstepid FROM httpstep s WHERE httptestid=#{httptestid}",
				params
			));
				
		return Long.parseLong(stepid.get("httpstepid").toString());					
	}
	
	/**
	 * 设置状态
	 */
	public Integer setStatus(Map alarmLine){
		if(isset(alarmLine,"alarmLineid")){//修改
			return Integer.parseInt(alarmLine.get("status").toString()); //为0指 开启
		}else{//新增
			return isset(alarmLine, "status")?0:1;
		}
	}
	
	   /** 获取云主机id和name Map 值
     * @param idBean
     * @param executor
     * @param hostid
     * @return
     */
    private Map<String,String> getVirtMap(IIdentityBean idBean, SQLExecutor executor,Long hostid){
    	//云主机
		CArray<Map> virthosts=array();
		CHostGet virtoption = new CHostGet();
		virtoption.setOutput(new String[]{"hostid", "name"});
		virtoption.setGroupIds(IMonConsts.MON_VM);
		virtoption.setEditable(true);
		virthosts = API.Host(idBean, executor).get(virtoption);
		Map<String,String> virtmap=new HashMap<String,String>();
		for(Map virt:virthosts){
			virtmap.put(Nest.value(virt, "hostid").asString(), Nest.value(virt, "name").asString());
		}
    	return virtmap;
    }
	
	
}
