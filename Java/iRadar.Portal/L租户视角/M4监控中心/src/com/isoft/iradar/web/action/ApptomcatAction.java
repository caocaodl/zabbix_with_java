package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.common.util.IMonConsts.APP_NAME_TOMCAT;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_APPLICATION;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.web.Util.THttpconfUtil.doCheckUser;
import static com.isoft.iradar.web.Util.THttpconfUtil.getSysMediaTypes;
import static com.isoft.iradar.web.Util.THttpconfUtil.getUserMediatypes;
import static com.isoft.iradar.web.Util.TvmUtil.getTrggersToTriggerid;
import static com.isoft.iradar.web.Util.TvmUtil.getTriggerDescription;
import static com.isoft.iradar.web.Util.TvmUtil.getTriggerid;
import static com.isoft.iradar.web.Util.TvmUtil.removeDefaultProfix;
import static com.isoft.iradar.web.Util.TvmUtil.show_messages;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import com.isoft.biz.daoimpl.common.MonitorDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.virtualresource.VirtualResourceDao;
import com.isoft.iradar.web.Util.AlarmUtil;
import com.isoft.iradar.web.Util.JsonUtil;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ApptomcatAction extends RadarBaseAction {
	private final static String SQL_UPDATE_ITEM ="UPDATE items SET delay=#{delay},key_ = #{key} WHERE itemid=#{itemid} and tenantid=#{tenantid}";
	private final static String SQL_UPDATE_APPLICATION = " update applications set type="+IMonConsts.A_TYPE_TOMCAT+" where applicationid= #{applicationid} and tenantid=#{tenantid} ";
	
	@Override
	protected void doInitPage() {
		page("title","Tomcat"+_("Service application"));
		page("file", "app_tomcat.action");
		page("hist_arg", new String[] { "groupid", "type" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] {"tenant/edit.css","tenant/supervisecenter/apptomcat.css"});
		page("js", new String[] {"imon/common.tomcat.js"});
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray fields = map(
				"name",			       array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null,"服务应用名称"),
				"belongVirt",	       array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null,"所属云主机"),
				"form",			       array(T_RDA_STR, O_OPT, P_SYS,	null,null),
				"status",		       array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3"), null),
				"go",			       array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
				"delete",		       array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
				"hostid",		       array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
				"applicationid",	   array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
				"hostids",		       array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
				"tsave",			   array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
				"jmxport",			   array(T_RDA_STR, O_OPT, null,	null,		null),
				"tomcatport",		   array(T_RDA_STR, O_OPT, null,	null,		null),
				"javahome",		       array(T_RDA_STR, O_OPT, null,	null,		null),
				"monitoringName",      array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
				"pro_hostid",		   array(T_RDA_STR, O_OPT, null,			null,		null),
				"detatalarraytest",    array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
				"userMediatypes",      array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({tsave})","通知方式不能为空")
			);
			check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void doAction(final SQLExecutor executor) {
		VirtualResourceDao vmdao=new VirtualResourceDao();
		CArray<Map> templateid =new CArray<Map>();
		templateid.put("templateid", IMonConsts.A_TYPE_TOMCAT);
		MonitorDAO monitor =new MonitorDAO(executor);
		List<Map> monitors=monitor.doListMonitor(templateid);
		Map<String,String> keymap=new HashMap<String,String>();
		Map<String,String> namemap=new HashMap<String,String>();
		for (Map map : monitors) {
			keymap.put(map.get("keymain").toString(), map.get("name").toString());
			namemap.put(map.get("name").toString(), map.get("keymain").toString());
		}
		
		 CArray data = array();
	        if(hasRequest("tsave")){
	        	boolean resutl=true;
	        	Long applicationid = get_request("applicationid", 0L);
	        	String jmxport = get_request("jmxport", "9001");
				String tomcatport = get_request("tomcatport", "8080");
				String javahome = get_request("javahome");
	        	String testkey = "tomcat.errorCount["+tomcatport+","+jmxport+","+javahome+"]";
	        	String msgOk= _("saveSuccess"), msgFail =_("saveFail");
	        	String hostid=get_request("belongVirt");
	        	if (TvmUtil.checkSameport(executor,getIdentityBean(),Long.valueOf(hostid),applicationid,testkey,IMonConsts.A_TYPE_TOMCAT)) {
					resutl=false;
					info(_s("Cannot deploy tomcat port, JMX port number, and the java path of the same application in the sanme hosts,please fill in again"));
				}
				if(resutl){
					Map<String,String> virtmap=getVirtMap(getIdentityBean(),executor,0L,"host");
		        	CArray<Long[]> dbApplications = null;
		        	String hostname = virtmap.get(hostid);
		        	try {
						DBstart(executor);
						getVirtMap(getIdentityBean(),executor,0L);
						final Map application = map(
								"name", APP_NAME_TOMCAT+Nest.value(_REQUEST,"name").$(),
								"hostid", hostid);
						if (!empty(applicationid)) {
							Nest.value(application,"applicationid").$(applicationid);
							dbApplications = Call(new Wrapper<CArray<Long[]>>() {
								@Override
								protected CArray<Long[]> doCall() throws Throwable {
									return API.Application(getIdentityBean(), executor).update(array(application));
								}
							}, null);
						} else {
							dbApplications = Call(new Wrapper<CArray<Long[]>>() {
								@Override
								protected CArray<Long[]> doCall() throws Throwable {
									return API.Application(getIdentityBean(), executor).create(array(application));
								}
							}, null);
						}
	                    Long[] appids = dbApplications.get("applicationids");
	                    Long appid = appids[0];
	                    DBUtil.DBexecute(executor, SQL_UPDATE_APPLICATION, map("applicationid",appid,"tenantid",getIdentityBean().getTenantId()));
	                    
						int delay=Nest.value(_REQUEST, "monitoringName").asInteger();//更新频率
						 if(delay==0){
							 delay=IMonConsts.ONITORING_FREQUENCY_15;
						 }
						 Map<String,String> oldnamemap=new HashMap<String,String>();
						 if (!empty(applicationid)) {
							 CItemGet itemGet = new CItemGet();
							 itemGet.setApplicationIds(applicationid);
							 itemGet.setOutput(new String[] {"itemid", "name", "state","key_"});
							 itemGet.setEditable(true);
							 CArray<Map> items = API.Item(getIdentityBean(), executor).get(itemGet);
							 for(Map item : items){
								 String oldkey = Nest.value(item, "key_").asString();
								 String newkey = oldkey.substring(0,oldkey.indexOf("[")+1)+tomcatport+","+jmxport+","+javahome+"]";
								 updateItem(executor,Nest.value(item, "itemid").asLong(),newkey,delay);
								 oldnamemap.put(Nest.value(item, "name").asString(), oldkey);
							 }
						 }else{
							 for(Map.Entry<String, String> entry:keymap.entrySet()){
									String key = Cphp.str_replace(
											CArray.array("{$TOMCAT_PORT}", "{$JMX_PORT}", "{$JAVAHOME}"),
											CArray.array(tomcatport, jmxport,javahome),entry.getKey());
									createItem(executor,entry.getValue(),appid,Long.valueOf(hostid),Defines.ITEM_STATUS_ACTIVE,key,delay);	
							 } 
						 }
						
						String jsonstr=JsonUtil.JsonStr(Nest.value(_REQUEST, "detatalarraytest").asString());
			            JSONObject jsonObject;
			            List<AlarmUtil> alarmlist;
			            if(!"[{}]".equals(jsonstr)){
				    			CTriggerGet toptions = new CTriggerGet();
				    			toptions.setApplicationIds(applicationid);
				    			toptions.setOutput(new String[]{"triggerid"});
				    			CArray<Map> triggerids = API.Trigger(getIdentityBean(), executor).get(toptions);
				    			List<Long> triggeridarr =new ArrayList<Long>();
				    			for(Map triggerid:triggerids){
				    				triggeridarr.add(Nest.value(triggerid, "triggerid").asLong());
				    			}
				    		    alarmlist=JsonUtil.JsonToJavas(new AlarmUtil(),jsonstr);
				    			 for(AlarmUtil alarm:alarmlist){
				    				    boolean iscreate=false;
				    					String indname=alarm.getItemid();
				    					String operator=TvmUtil.getSymbol(alarm.getOperator());
				    					String numerical=alarm.getNumerical();
				    					String gaonum=TvmUtil.getNumStr(alarm.getGaonum());
				    					String isenable=TvmUtil.getStatusStr(alarm.getIsenable());
				    					String oldtype = "";
				    					String type = "";
				    					if (!empty(applicationid)) {
				    						oldtype = oldnamemap.get(alarm.getType());
				    					    type = oldtype.substring(0,oldtype.indexOf("[")+1)+tomcatport+","+jmxport+","+javahome+"]";
				    					}else{
				    						oldtype = namemap.get(alarm.getType());
				    						type = Cphp.str_replace(
													CArray.array("{$TOMCAT_PORT}", "{$JMX_PORT}", "{$JAVAHOME}"),
													CArray.array(tomcatport, jmxport,javahome),
													oldtype);
				    					}
				    					List<Long> removeList = new ArrayList();
				    					for(Long id:triggeridarr){
				    						if(id.toString().equals(indname)){
				    							removeList.add(id);  
				    						}
				    					}
				    					if(removeList.size()>0) 
				    					{ 
				    						triggeridarr.removeAll(removeList); 
				    					} else{
				    						iscreate=true;
				    					} 
				    					String expression = "{"+hostname+":"+type+".count(#"+gaonum+","+numerical+",\""+operator+"\")}";
				    					String description =  getTriggerDescription(get_request("name"),alarm.getType(),gaonum,alarm.getOperator(),numerical,false);
				    					final Map trigger = map(
				    							"triggerid",indname+"123",
				    							"expression", expression,
				    							"description", description,
				    							"priority", Defines.TRIGGER_SEVERITY_WARNING,
				    							"status", isenable,
				    							"type", Defines.TRIGGER_MULT_EVENT_DISABLED,
				    							"comments",hostid.toString(),
				    							"url", ""
				    						);
				    					//保存  返回生成的triggerid
				    					CArray<Long[]> trids=new CArray<Long[]>();
				    					if(iscreate){
				    						trids = Call(new Wrapper<CArray<Long[]>>() {
					    						protected CArray<Long[]> doCall() throws Throwable {
					    							return API.Trigger(getIdentityBean(),executor).create(array(trigger));
					    						}
					    					}, null);
				    					}else{
				    						Nest.value(trigger, "triggerid").$(indname);
				    						trids = Call(new Wrapper<CArray<Long[]>>() {
					    						protected CArray<Long[]> doCall() throws Throwable {
					    							return API.Trigger(getIdentityBean(),executor).update(array(trigger));
					    						}
					    					}, null);
				    					}
				    					
				    					if(!empty(trids)){
				    						Long[] trigids=(Long[]) trids.get("triggerids");
				    						final Long actionid = getActionid(executor, trigids[0]);
				    							
				    						if(!empty(actionid)){
				    							boolean result = Call(new Wrapper<Boolean>() {//删除告警响应
				    								protected Boolean doCall() throws Throwable {
				    									return !empty(API.Action(getIdentityBean(), executor).delete(actionid));
				    								}
				    							});
				    						}
				    						boolean actResult = saveAction(executor, trids);
				    					}
				    			 }
				    			
				    			 if(!empty(triggeridarr)){
			    						for(final Long ids : triggeridarr){
			    							final Long actionid = getActionid(executor, ids);
			    							boolean result = Call(new Wrapper<Boolean>() {//删除告警响应
			    								protected Boolean doCall() throws Throwable {
			    									return !empty(API.Action(getIdentityBean(), executor).delete(actionid));
			    								}
			    							});
			    							
			    							CArray<Long[]> goResult = Call(new Wrapper<CArray<Long[]>>() {
			    								@Override
			    								protected CArray<Long[]> doCall() throws Throwable {
			    									return API.Trigger(getIdentityBean(),executor).delete(ids);
			    								}
			    							},null);
				    					}
			    					}
				    	
			            }
			        	DBend(executor, true);
						show_messages(true, msgOk, msgFail);
					}catch (Exception e) {
						DBend(executor, false);
						e.printStackTrace();
						show_messages(false, msgOk, msgFail);
					}
				}else{
					show_messages(false, msgOk, msgFail);
				}
	        	unset(_REQUEST,"form");
	        }/*else if (str_in_array(get_request("go"), array("activate", "disable"))) {
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
    	}*/else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {//此删除方法只能删除没有关联items的host
    		CArray applications = get_request("applications", array());
			DBstart(executor);
			 boolean goResult=true;
			try{
				CTriggerGet toptions = new CTriggerGet();
    			toptions.setApplicationIds(applications.valuesAsLong());
    			toptions.setOutput(new String[]{"triggerid"});
    			CArray<Map> triggerids = API.Trigger(getIdentityBean(), executor).get(toptions);
				Long[] trig=new Long[triggerids.size()];
				int i=0;
    			for(Map trigger:triggerids){
					trig[i]=Nest.value(trigger, "triggerid").asLong();
					i++;
				}
    			
    			SqlBuilder sqlParts = new SqlBuilder();
    			CArray<Map> actionids = DBselect(executor,
    					" SELECT a.actionid"+
    					" FROM actions a"+
    					" LEFT JOIN triggers t ON a.name=t.triggerid"+
    					" WHERE "+sqlParts.dual.dbConditionInt("t.triggerid", trig ),
				         sqlParts.getNamedParams()
    				);
    			final CArray<Map> aids = rda_objectValues(actionids, "actionid");
    			
    			SqlBuilder appsqlParts = new SqlBuilder();
				CArray<Map> dbApplications = DBselect(executor,
					"SELECT a.applicationid,a.name,a.hostid"+
					" FROM applications a"+
					" WHERE "+appsqlParts.dual.dbConditionInt("a.applicationid", applications.valuesAsLong()),
					appsqlParts.getNamedParams()
				);
				for(final Map dbApplication : dbApplications) {
					if (!isset(applications,dbApplication.get("applicationid"))) {
						continue;
					}

					 CItemGet itemGet = new CItemGet();
					 itemGet.setApplicationIds(Nest.value(dbApplication,"applicationid").asLong());
					 itemGet.setOutput(new String[] {"itemid"});
					 CArray<Map> items = API.Item(getIdentityBean(), executor).get(itemGet);
					 final Long[] itemids=new Long[items.size()];
					 int j=0;
					 for(Map item:items){
						 itemids[j]=Nest.value(item, "itemid").asLong();
						 j++;
					 }
					 if(itemids.length!=0){
						 goResult &= Call(new Wrapper<Boolean>() {
								@Override
								protected Boolean doCall() throws Throwable {
									return !empty(API.Item(getIdentityBean(), executor).delete(itemids));
								}
							});
					 }
					
					
					goResult &= Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Application(getIdentityBean(), executor).delete(Nest.value(dbApplication,"applicationid").asLong()));
						}
					});

					if (goResult) {
						Map host = get_host_by_hostid(getIdentityBean(), executor, Nest.value(dbApplication,"hostid").asLong());

						add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_APPLICATION,
							"Application ["+Nest.value(dbApplication,"name").asString()+"] from host ["+Nest.value(host,"host").asString()+"]");
					}
				}
				
    			if(goResult && !empty(aids)){
    				goResult = Call(new Wrapper<Boolean>() {
    					protected Boolean doCall() throws Throwable {
    						return !empty(API.Action(getIdentityBean(), executor).delete(aids.valuesAsLong()));
    					}
    				});	
    			}
    	    }catch (Exception e) {
    	    	goResult = DBend(executor, goResult);
    	    	e.printStackTrace();
    	    	show_messages(goResult, _("Service application deleted"), _("Cannot delete Service application"));
    	    }
			goResult = DBend(executor, goResult);
			show_messages(goResult, _("Service application deleted"), _("Cannot delete Service application"));
			clearCookies(goResult);
    	}
	    
        if(isset(_REQUEST,"form")){
        	Long applicationid = get_request("applicationid", 0L);
        	int delay=get_request("monitoringName",IMonConsts.ONITORING_FREQUENCY_15);
        	String pro_hostid=get_request("pro_hostid");
        	String name = get_request("name");
   		    Map<String,String> virtmap=getVirtMap(getIdentityBean(),executor,0L);
    		if(empty(applicationid)){
    			CArray<Map> carray=Nest.value(_REQUEST, "detatalarray").asCArray();
    			data=map("applicationid",get_request("applicationid",0L),
    					"name",get_request("name"),
    					"type",get_request("type"),
    					"operator",get_request("operator"),
    					"numerical",get_request("numerical"),
    					"gaonum",get_request("gaonum",1),
    					"isenable",get_request("isenable",0),
    					"monitoringName",delay,
    					"itembtn",get_request("itembtn",1),
    					"pro_hostid",pro_hostid
    					);
    			Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
    			Long triggerid = getTrggersToTriggerid(getIdentityBean(), executor, applicationid,true);
    			Nest.value(data, "userMediatypes").$(getUserMediatypes(executor, triggerid,false));
    			Nest.value(data, "keymap").$(keymap);
    		}else{
				Nest.value(data, "applicationid").$(applicationid);
				Nest.value(data, "name").$(name);
        		Nest.value(data, "addcustom").$(false);
        		Nest.value(data, "chooseitem").$(true);
        		Nest.value(data, "monitoringName").$(delay);
        		Nest.value(data, "itembtn").$(1);
        		Nest.value(data, "gaonum").$(1);
        		Nest.value(data, "isenable").$(0);
        		Nest.value(data, "pro_hostid").$(pro_hostid);
        		
         		CItemGet itemoptions=new CItemGet();
        		itemoptions.setApplicationIds(applicationid);
        		itemoptions.setOutput(new String[]{"itemid","name","key_","status"});
        		Map items = DBfetch((CArray<Map>)API.Item(getIdentityBean(), executor).get(itemoptions));
                String key = Nest.value(items, "key_").asString();
                String[] arrs= key.split(",");
                String tomcatport = arrs[0].substring(arrs[0].indexOf("[")+1, arrs[0].length());
                		///key.substring(key.indexOf("[")+1  , key.indexOf(","));
           		String jmxport = arrs[1];//key.substring(key.indexOf(",")+1  , key.indexOf("]"));
           		String javahome = arrs[2].substring(0,arrs[2].length()-1);//key.substring(key.indexOf(",")+1  , key.indexOf("]"));
           		Nest.value(data, "tomcatport").$(tomcatport);
           		Nest.value(data, "jmxport").$(jmxport);
           		Nest.value(data, "javahome").$(javahome);
           		
        		CTriggerGet toptions = new CTriggerGet();
        		toptions.setApplicationIds(applicationid);
        	    toptions.setOutput(API_OUTPUT_EXTEND);
    			toptions.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
    			toptions.setSelectFunctions(new String[]{"parameter"});
    			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
    			Nest.value(data, "triggers").$(triggers);
	    			
    			Long triggerid = getTriggerid(triggers);
    			Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
    			Nest.value(data, "userMediatypes").$(getUserMediatypes(executor, triggerid,false));

    			CArray detailcarray=map();
        		Nest.value(data, "detailcarray").$(detailcarray);
        		
        		Map<String,String> newkeymap=new HashMap<String,String>();
        		for(Map.Entry<String, String> entry:keymap.entrySet()){
					String keystr = Cphp.str_replace(
							CArray.array("{$TOMCAT_PORT}", "{$JMX_PORT}", "{$JAVAHOME}"),
							CArray.array(tomcatport, jmxport,javahome),
							entry.getKey());
					newkeymap.put(keystr, entry.getValue());
				} 
        		Nest.value(data, "keymap").$(newkeymap);
    		}
    		Nest.value(data, "virtmap").$(virtmap);
    			
    		CView editView = new CView("configuration.tomcat.edit", data);
    		editView.render(getIdentityBean(), executor);
    		editView.show();
    	}else{
		    CWidget tvmwidget=new CWidget();
		    CForm vmForm = new CForm();
		    vmForm.setName("tvmlatest");
			CToolBar tb = new CToolBar(vmForm);
			tb.addSubmit("form",_("Create Serve Apply"),"","orange create");
			
			CArray<CComboItem> goComboBox = array();
			/*CComboItem  goOption = new CComboItem("activate", _("Enable selected"));
			goOption.setAttribute("confirm", _("Enable selected"));
			goOption.setAttribute("class", "orange activate");
			goComboBox.add(goOption);
			
			goOption = new CComboItem("disable", _("Disable selected"));
			goOption.setAttribute("confirm", _("Disable selected"));
			goOption.setAttribute("class", "orange disable");
			goComboBox.add(goOption);*/
			
			CComboItem goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", _("Delete selected"));
			goOption.setAttribute("class", "orange delete");
			goComboBox.add(goOption);
			
			tb.addComboBox(goComboBox);
			rda_add_post_js("chkbxRange.pageGoName = \"applications\";");
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			tvmwidget.addItem(headerActions);
			
			CArray dataTable = this.getHostTable(executor,vmForm);
		    vmForm.addItem(dataTable);
		    tvmwidget.addItem(vmForm);
		    tvmwidget.show();
    	}
	}
	

	/**
	 * 组装设备信息页面
	 * @return
	 */
	public CArray getHostTable(SQLExecutor executor,CForm form){
		int filterShowWithoutData = get_request("show_without_data", 0);
		Map virtmap=getVirtMap(getIdentityBean(),executor,0L);
		String sortfield = getPageSortField(getIdentityBean(), executor,"name");//表头排序功能
		String sortorder;
		if(isset(_REQUEST,"sortorder")){
			sortorder = get_request("sortorder");
			if(empty(sortorder)){
				sortorder="ASC";
			}
		}else{
			sortorder = getPageSortOrder(getIdentityBean(), executor);
		}
		
		
		String sql="select applicationid,hostid,name from applications where tenantid=#{tenantid} and type=3 order by applicationid desc ";
		CArray<Map> apps = DBselect(executor, sql, map("tenantid",getIdentityBean().getTenantId()));
		CTable paging = getPagingLine(getIdentityBean(), executor, apps, array("applicationid"));
		
		
		CTableInfo table = new CTableInfo(_("No values found."));
		CArray mons = array(new CCheckBox("all_applications", false, "checkAll(\""+form.getName()+"\", \"all_applications\", \"applications\");"),
				make_sorting_header(_("Application Name"),"name"),_("Belongs to VM"),_("Application Type"),_("Item Frequency"),_("The last Time"),/*_("Status"),*/_("Operations"));
		table.setHeader(mons);
		order_result(apps, sortfield, sortorder);
		
		Map apptypes= DBfetch(DBselect(executor, "select templateid,name "
				+ "from i_t_template where templateid=#{templateid}", map("templateid",IMonConsts.A_TYPE_TOMCAT)));
		String apptype = Nest.value(apptypes, "name").asString();
		
		for(Map app:apps){
			String appname = removeDefaultProfix(Nest.value(app, "name").asString(),APP_NAME_TOMCAT);
			Long hostid= Nest.value(app, "hostid").asLong();
			Long appid  =Nest.value(app, "applicationid").asLong();
			String datestr="";
			/*CCol status = new CCol(new CDiv(new CLink(
					TvmUtil.status2str(Nest.value(host,"status").asInteger()),
					"?hosts"+SQUAREBRACKETS+"="+Nest.value(host,"hostid").asString()+"&sortorder="+sortorder
					 +(Nest.value(host,"status").asInteger() == HOST_STATUS_MONITORED ? "&go=disable" : "&go=activate"),//添加templateid、设备类型、模型ID参数，保证触发时间后还跳转到本页面
					 TvmUtil.status2style(Nest.value(host,"status").asInteger())
				), "switch"));*/
			
			CArray<Map> itemmaps=new CArray<Map>();
			Long delay=0L;
			if (!empty(appid)) {
				CItemGet itemGet = new CItemGet();
				itemGet.setApplicationIds(appid);
				itemGet.setOutput(new String[] {"itemid", "name", "delay","value_type" });
				itemGet.setPreserveKeys(true);
				itemmaps = API.Item(getIdentityBean(), executor).get(itemGet);
				for(Map item:itemmaps){
					delay=Nest.value(item,"delay").asLong();
					String timestr = TvmUtil.getLastHistoryTime(getIdentityBean(),executor,item);
					if(!empty(timestr)){
						Long timestamp = Long.parseLong(timestr)*1000;  
						datestr = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date(timestamp));  
					}
				}
			}
			String pro_hostid=Nest.value(app, "hostid").asString();
    		CArray edit = array(new CLink(_("monitorConfig"), "app_tomcat.action?form=edit&applicationid="+ appid
    				  +"&monitoringName="+delay+"&pro_hostid="+pro_hostid+"&name="+appname));
    		CArray buttons= array(edit);
    		CCheckBox checkBox = new CCheckBox("applications["+Nest.value(app,"applicationid").asString()+"]", false, null, Nest.value(app,"applicationid").asInteger());
			
			CRow row = new CRow(array(
					checkBox,
					new CLink(appname, "tomcatinfor.action?form=update&templateid="+IMonConsts.A_TYPE_TOMCAT+"&applicationid="+appid+"&hostid="+hostid),
					virtmap.get(pro_hostid),
					apptype,
					MStoMinutes(delay),
					datestr,
				//	"可用性",
					buttons
					));
			table.addRow(row);
		}
		return array(table,paging);
	}
	
	
	/** 更新状态
	 * @param executor
	 * @param status
	 * @param itemid
	 * @return
	 */
/*	private boolean updateItemStatus(SQLExecutor executor,boolean status,Long itemid){
		boolean isstatus;
		if(status){
			isstatus=activate_item(getIdentityBean(), executor,itemid);
		}else{
			isstatus=disable_item(getIdentityBean(), executor,itemid); 
		}
		return isstatus;
	}*/
	

	
	/**创建监控指标
	 * @param executor
	 * @param name
	 * @param hostid
	 * @param status
	 * @param key
	 * @param delay
	 * @return
	 */
	private boolean createItem(final SQLExecutor executor,String name,Long appid,Long hostid,int status,String key,int delay){
	boolean result=true;
	  try {
		    int delta=0;
		    if("tomcat.errorCount".equals(key.substring(0, key.indexOf("[")))){
		    	delta=1;
		    }
			Long itemid = get_dbid(getIdentityBean(), executor, "items", "itemid",1);
			List itemlist = new ArrayList();
			Map itemdata = new HashMap();
			itemdata.put("itemid", itemid);
			itemdata.put("hostid", hostid);
			itemdata.put("itemname", name);
			itemdata.put("key", key);
			itemdata.put("delay", delay);
			itemdata.put("status", status);
			itemdata.put("description",  key);
			itemdata.put("delta",  delta);
			itemdata.put("tenantid", this.getIdBean().getTenantId());
			itemdata.put("type", Defines.ITEM_TYPE_IRADAR_ACTIVE);
			itemlist.add(itemdata);
			new TvmUtil().createItem(executor,itemlist);
			Map item = DBfetch(DataDriver.getItemId(executor,hostid,key));
			Long itemappid = get_dbid(getIdentityBean(), executor, "items_applications", "itemappid",1);
			result = DBUtil.DBexecute(executor, "INSERT INTO items_applications (tenantid, itemappid, applicationid, itemid) "
                            +" VALUES (#{tenantid},  #{itemappid}, #{applicationid},#{itemid})", map("tenantid",getIdentityBean().getTenantId(),
                            		"itemappid",itemappid,"applicationid",appid,"itemid",itemid	));
		} catch (Exception e) {
			result=false;
		}
		return result;
	}

	private boolean updateItem(final SQLExecutor executor,Long itemid,String newkey,int delay){
		Map item = map(
				"delay", delay,
				"itemid", itemid,
				"key", newkey,
				"tenantid",getIdentityBean().getTenantId());
		boolean result=DBUtil.DBexecute(executor, SQL_UPDATE_ITEM, item);
		return result;
	}
	
    /** 毫秒数改为分钟
     * @param delay
     * @return
     */
    private String MStoMinutes(Long delay){
    	long de=delay/60;
    	return de+_("Minutes");
    }
    
    /** 获取云主机id和name Map 值
     * @param idBean
     * @param executor
     * @param hostid
     * @return
     */
    private Map<String,String> getVirtMap(IIdentityBean idBean, SQLExecutor executor,Long hostid){
    	return getVirtMap(idBean,executor,hostid,null);
    }
    //有用到host的name或host(已变更为UUid)
    private Map<String,String> getVirtMap(IIdentityBean idBean, SQLExecutor executor,Long hostid,String type){
    	//云主机
		CArray<Map> virthosts=array();
		CHostGet virtoption = new CHostGet();
		virtoption.setOutput(new String[]{"hostid", "host", "name", "proxy_hostid"});
		virtoption.setGroupIds(IMonConsts.MON_VM);
		virtoption.setEditable(true);
		virthosts = API.Host(idBean, executor).get(virtoption);
		
		Map<String,String> virtmap=new HashMap<String,String>();
		if("host".equals(type)){
			for(Map virt:virthosts){
				virtmap.put(Nest.value(virt, "hostid").asString(), Nest.value(virt, "host").asString());
			}
		}else{
			for(Map virt:virthosts){
				virtmap.put(Nest.value(virt, "hostid").asString(), Nest.value(virt, "name").asString());
			}
		}
    	return virtmap;
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
	   							"subject", _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
	   							"message",_("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER")),
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
	   		//Set triggerids = triggerid.keySet();
	   		Long[] trigids=(Long[]) triggerid.get("triggerids");
	   		Map condTemp = null;
	   		//for(Object tid : triggerids){
	   		for(Long idl:trigids){
	   			condTemp=new HashMap();
	   			condTemp.put("conditiontype", 2); //2为阀值规则  
	   			condTemp.put("operator", 0);
	   			condTemp.put("value", idl);
	   			
	   			conditions.add(condTemp);
	   		}
	   			
	   		//封装   动作、条件、操作
	   		final Map action = map(
	   				//"name", get_request("name"),
	   				"name",trigids[0].toString(),
	   				"evaltype", ACTION_EVAL_TYPE_AND_OR,
	   				"status", get_request("status", ACTION_STATUS_ENABLED),
	   				"esc_period", 3600,
	   				"def_shortdata", _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
	   				"def_longdata", _("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER"),
	   				"recovery_msg", 0,
	   				"r_shortdata", _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
	   				"r_longdata", _("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER"),
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
   	 * 告警对应的告警响应id
   	 */
   	public Long getActionid(SQLExecutor executor, Long httptestid){
   		Map params=new HashMap();
   		params.put("httptestid", httptestid);
   		Map actionid = DBfetch(DBselect(executor,
   				" SELECT a.actionid"+
   				" FROM actions a"+
   				" LEFT JOIN triggers t ON a.name=t.triggerid"+
   				" WHERE t.triggerid=#{httptestid}",
   				params
   			));
   	   if(empty(actionid)){
   		   return 0L;
   	   }else{
   		   return Long.parseLong(actionid.get("actionid").toString());		
   	   }
   	}	
   	
   	/*public static void main(String[] agrs){
   		String s = Cphp.str_replace(
   				CArray.array("{$t1}", "{$t2}"),
   				CArray.array("12345", "8080"),
   				"tcat.uptimeom[{$t1}, {$t2}]"
   				);
   		String str = s.substring(s.indexOf("[")+1  , s.indexOf(","));
   		String str1 = s.substring(s.indexOf(",")+1  , s.indexOf("]"));
		System.out.println(str1);
		String st="tcat.uptimeom[12345,8080]";
		System.out.println(st.substring(0, st.indexOf("[")+1));
		
		String key="tomcat.errorCount[{$TOMCAT_PORT},{$JMX_PORT}]";
				
		System.out.println("tomcat.errorCount".equals(key.substring(0, key.indexOf("["))));
   	}*/
	
}
