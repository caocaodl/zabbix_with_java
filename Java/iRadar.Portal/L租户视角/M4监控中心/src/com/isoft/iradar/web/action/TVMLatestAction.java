package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.common.util.ItemsKey.CPU_LOAD_VM;
import static com.isoft.iradar.common.util.ItemsKey.CPU_RATE_VM_LINUX;
import static com.isoft.iradar.common.util.ItemsKey.CPU_RATE_VM_WINDOWS;
import static com.isoft.iradar.common.util.ItemsKey.DISK_RATE_VM;
import static com.isoft.iradar.common.util.ItemsKey.DISK_READ_SPEED_VM_LINUX;
import static com.isoft.iradar.common.util.ItemsKey.DISK_READ_SPEED_VM_WINDOWS;
import static com.isoft.iradar.common.util.ItemsKey.DISK_WRITE_SPEED_VM_LINUX;
import static com.isoft.iradar.common.util.ItemsKey.DISK_WRITE_SPEED_VM_WINDOWS;
import static com.isoft.iradar.common.util.ItemsKey.MEMORY_RATE_VM;
import static com.isoft.iradar.common.util.ItemsKey.STATUS_VM;
import static com.isoft.iradar.common.util.ItemsKey.USER_VM;
import static com.isoft.iradar.common.util.ItemsKey.DISK_VM;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.web.Util.THttpconfUtil.doCheckUser;
import static com.isoft.iradar.web.Util.THttpconfUtil.getSysMediaTypes;
import static com.isoft.iradar.web.Util.THttpconfUtil.getUserMediatypes;
import static com.isoft.iradar.web.Util.TvmUtil.getTrggersToTriggerid;
import static com.isoft.iradar.web.Util.TvmUtil.show_messages;
import static com.isoft.iradar.web.bean.Column.column;
import static com.isoft.iradar.web.bean.Key.avg;
import static com.isoft.iradar.web.bean.Key.value;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.common.MonitorDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CObject;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.AlarmUtil;
import com.isoft.iradar.web.Util.JsonUtil;
import com.isoft.iradar.web.Util.THttpconfUtil;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.bean.Column;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


/**  添加租户云主机监察
 * @author HP Pro3330mt
 *
 */
public class TVMLatestAction extends RadarBaseAction {

	private final static String SQL_UPDATE_ITEM ="UPDATE items SET delay=#{delay} WHERE itemid=#{itemid} and tenantid=#{tenantid}";
	private final static String SQL_UPDATE_PARAMS_ITEM ="UPDATE items SET delay=#{delay},params=#{params} WHERE itemid=#{itemid} and tenantid=#{tenantid}";
	public int scrollPagCount = 20;
	
	CArray<Column> columns = array(
		column(_("the status Of VM"), 			       	   value(STATUS_VM)),
		column(_("the user Of Vm"), 			       	   value(USER_VM)),
		column(_("the cpuUsage of host"), 			       value(array(CPU_RATE_VM_LINUX,CPU_RATE_VM_WINDOWS),2,true)),
		column(_("the cpuLoad of host"), 			       value(CPU_LOAD_VM, 2,true)),
		column(_("the memoryUsage of host"), 		       value(MEMORY_RATE_VM, 2,true)),
		column(_("disk_vm"),                             value(DISK_VM, 2,true)),
		column(_("the diskUsage of host"), 			       avg(DISK_RATE_VM,true)),
		column(_("the write and write request BW of host"),value(array(DISK_READ_SPEED_VM_LINUX,DISK_READ_SPEED_VM_WINDOWS),2,true), value(array(DISK_WRITE_SPEED_VM_LINUX,DISK_WRITE_SPEED_VM_WINDOWS),2,true))
	);
	
	@Override
	protected void doInitPage() {
		page("title",_("Virtual Monitor"));
		page("file", getSimpleAction());
		page("hist_arg", new String[] { "groupid", "type" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] {"tenant/edit.css","tenant/supervisecenter/tmonvm.css"});
		page("js", new String[] {"imon/common.tvm.js","imon/scrollpagination.js"});
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if("ajax".equals(Nest.value(_REQUEST, "output").asString())){
			CTableInfo table = getHostTable(executor);
			CArray<String> items =  CArray.valueOf(table.items);
			String contenant = CObject.unpack_object(items).toString();
			AjaxResponse ajaxResponse = new AjaxResponse();
			Map response = map("contenant",contenant,
							   "start",empty(contenant)?Nest.value(_REQUEST, "start").asInteger():Nest.value(_REQUEST, "start").asInteger()+scrollPagCount);
			ajaxResponse.success(response);
			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void doAction(final SQLExecutor executor) {
		CArray data = array();
		data.put("templateid", IMonConsts.A_TYPE_VM);
		CArray<Map> templateid =new CArray<Map>();
		templateid.put("templateid", IMonConsts.A_TYPE_VM);
		MonitorDAO monitor =new MonitorDAO(executor);
		List<Map> monitors=monitor.doListMonitor(templateid);
		Map<String,String> keymap=new LinkedHashMap<String,String>();
		Map<String,String> namemap=new LinkedHashMap<String,String>();
		Map<String,String> descriptionmap=new LinkedHashMap<String,String>();
		Map<String,String> appidmap=new LinkedHashMap<String,String>();
		for (Map map : monitors) {
			String namestr=map.get("name").toString();
			String keystr=map.get("keymain").toString();
			String appnamestr=map.get("appname").toString();
			if(!descriptionmap.containsKey(appnamestr)){
				descriptionmap.put(appnamestr, map.get("description").toString());
				appidmap.put(appnamestr, map.get("appid").toString());
			}
			keymap.put(keystr, namestr);
			namemap.put(namestr, keystr);
		}
		
        if(hasRequest("tsave")){
        	Long hostid = get_request("hostid", 0L);
        	String ip=get_request("vmip", "");
        	String msgOk= null, msgFail = null;
        	try {
				DBstart(executor);
				Map host = null;
	        	if(!empty(hostid)){		
					int delay=get_request("monitoringName",0);//更新频率
					CArray<Long> itemids=new CArray<Long>();
					for(Map.Entry<String, String> entry:keymap.entrySet()){ 
						updaeOrcreateItem(executor,entry.getKey(),hostid,delay);
						Long itemid = getItemid(executor,hostid,entry.getKey());
						if(!empty(itemid)){
							itemids.add(itemid);
						}
					}  
					String jsonstr=JsonUtil.JsonStr(Nest.value(_REQUEST, "detatalarraytest").asString());
		            List<AlarmUtil> alarmlist;
		            if(!"[{}]".equals(jsonstr)){
		            	CHostGet hosts=new CHostGet();
		            	hosts.setHostIds(hostid);
		            	hosts.setOutput(new String[]{"host"});
		            	Map hostmap=reset((CArray<Map>)API.Host(getIdentityBean(), executor).get(hosts));
		            	
		    			CTriggerGet toptions = new CTriggerGet();
		    			toptions.setItemIds(itemids.valuesAsLong());
		    			toptions.setOutput(new String[]{"triggerid"});
		    			toptions.setFilter("comments", hostid.toString());
		    			CArray<Map> triggerids = API.Trigger(getIdentityBean(), executor).get(toptions);
		    			List<Long> triggeridarr =new ArrayList<Long>();
		    			for(Map triggerid:triggerids){
		    				triggeridarr.add(Nest.value(triggerid, "triggerid").asLong());
		    			}
		    		    alarmlist=JsonUtil.JsonToJavas(new AlarmUtil(),jsonstr);
		    			for(AlarmUtil alarm:alarmlist){
	    				    boolean iscreate=false;
	    					String indname=alarm.getItemid();
	    					String type=namemap.get(alarm.getType().toString());
	    					String operator=TvmUtil.getSymbol(alarm.getOperator());
	    					String numerical=alarm.getNumerical();
	    					String gaonum=TvmUtil.getNumStr(alarm.getGaonum());
	    					String isenable=TvmUtil.getStatusStr(alarm.getIsenable());
	    					List<Long> removeList = new ArrayList();
	    					for(Long id:triggeridarr){
	    						if(id.toString().equals(indname))
	    							removeList.add(id);  
	    					}
	    					
	    					if(removeList.size()>0) 
	    						triggeridarr.removeAll(removeList); 
	    					else
	    						iscreate=true;
	    					
	    					String expression = "{"+Nest.value(hostmap, "host").asString()+":"+type+".count(#"+gaonum+","+numerical+",\""+operator+"\")}";
	    					String description = TvmUtil.getTriggerDescription(get_request("vmname"), alarm.getType(), gaonum, alarm.getOperator(), numerical,false);
	    					final Map trigger = map(
	    							"expression"	, expression,
	    							"description"	, description,
	    							"priority"		, Defines.TRIGGER_SEVERITY_WARNING,
	    							"status"		, isenable,
	    							"type"			, Defines.TRIGGER_MULT_EVENT_DISABLED,
	    							"comments"		, hostid.toString(),
	    							"url"			, "",
	    							"dependencies"  , array());
	    					
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
	    							boolean result = Call(new Wrapper<Boolean>() {
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
    							CArray<Long[]> goResult = Call(new Wrapper<CArray<Long[]>>() {
									@Override
									protected CArray<Long[]> doCall() throws Throwable {
										return API.Trigger(getIdentityBean(),executor).delete(ids);
									}
    							},null);
	    					}
		    			 }
		            }
	        	}
	        	DBend(executor, true);
				msgOk = _("saveSuccess");
				msgFail = _("saveFail");
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				msgOk = _("saveSuccess");
				msgFail = _("saveFail");
				show_messages(false, msgOk, msgFail);
			}
        	unset(_REQUEST,"form");
        }
	        
        if(isset(_REQUEST,"form")){
        	Long hostid = get_request("hostid", 0L);
        	String vmip=get_request("vmip","");
        	CHostGet options = new CHostGet();
    		options.setOutput(new String[] { "name", "hostid", "status","host" });
    		options.setHostIds(hostid);
    		options.setPreserveKeys(true);		
    		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
    		if(!empty(get_request("name"))){
    			CArray<Map> carray=Nest.value(_REQUEST, "detatalarray").asCArray();
    			data=map("hostid"			,get_request("hostid",0L),
    					 "name"				,get_request("vmname"),
    					 "type"				,get_request("type"),
    					 "operator"			,get_request("operator"),
    					 "numerical"		,get_request("numerical"),
    					 "gaonum"			,get_request("gaonum",1),
    					 "isenable"			,get_request("isenable",0),
    					 "monitoringName"	,get_request("monitoringName",IMonConsts.ONITORING_FREQUENCY_15),
    					 "itembtn"			,get_request("itembtn",1));
    			Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
    			
    			Long triggerid = getTrggersToTriggerid(getIdentityBean(), executor, hostid);
    			Nest.value(data, "userMediatypes").$(getUserMediatypes(executor, triggerid,false));
    		}else{
    			for(Map host:hosts){
    				Nest.value(data, "hostid").$(hostid);
    				Nest.value(data, "vmname").$(Nest.value(host, "name").asString());
	        		Nest.value(data, "host").$(Nest.value(host, "host").asString());
	        		Nest.value(data, "addcustom").$(false);
	        		Nest.value(data, "chooseitem").$(true);
	        		Nest.value(data, "itembtn").$(1);
	        		Nest.value(data, "gaonum").$(1);
	        		Nest.value(data, "isenable").$(0);
	        		Nest.value(data, "vmip").$(vmip);
	   
	        		int delay=0;
	        		for(Map.Entry<String, String> entry:keymap.entrySet()){ 
	        			Long itemid=getItemid(executor,hostid,entry.getKey());
						CItemGet itemGet = new CItemGet();
	    				itemGet.setItemIds(itemid);
	    				itemGet.setOutput(new String[] {"itemid", "name", "delay" });
	    				Map itemmaps = API.Item(getIdentityBean(), executor).get(itemGet);
	    				delay=Nest.value(itemmaps,"0","delay").asInteger();
	    				if(delay>0){
	    					break;
	    				}
					}  
	        		CArray<Long> itemids=array();
	        		for(Map.Entry<String, String> entry:keymap.entrySet()){ 
	        			Long itemid = getItemid(executor,hostid,entry.getKey());
						if(!empty(itemid)){
							itemids.add(itemid);
						}
					}  
	        		
	    			delay=(delay==0?IMonConsts.ONITORING_FREQUENCY_15:TvmUtil.IscontainsDelay(delay)?delay:IMonConsts.ONITORING_FREQUENCY_15);
	    			Nest.value(data, "monitoringName").$(delay);
	    			
	        		CTriggerGet toptions = new CTriggerGet();
	        		toptions.setHostIds(hostid);
	        		toptions.setItemIds(itemids.valuesAsLong());
	        	    toptions.setOutput(API_OUTPUT_EXTEND);
	    			toptions.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
	    			toptions.setSelectFunctions(new String[]{"parameter"});
	    			toptions.setFilter("comments", hostid.toString());
	    			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
	    			CArray<Long> triggerIds = array();
	    			for(Map trigger:triggers){
	    				triggerIds.add(Nest.value(trigger, "triggerid").asLong());
	    			}
	    			Nest.value(data, "triggers").$(triggers);
	    			Nest.value(data, "sysMediatypes").$(getSysMediaTypes(executor));
	    			Nest.value(data, "userMediatypes").$(THttpconfUtil.getUserExistMediatypes(getIdentityBean(),executor, triggerIds));
    			}
    			Nest.value(data, "keymap").$(keymap);
    			Nest.value(data, "descriptionmap").$(descriptionmap);
    			Nest.value(data, "appidmap").$(appidmap);
        		CArray detailcarray=map();
        		Nest.value(data, "detailcarray").$(detailcarray);
    		}
    		CView editView = new CView("configuration.tenantvm.edit", data);
    		editView.render(getIdentityBean(), executor);
    		editView.show();
    	}else{
		    CWidget tvmwidget=new CWidget();
		    
		    CTableInfo dataTable = this.getHostTable(executor);
		    tvmwidget.addItem(array(dataTable));
		    tvmwidget.show();
		    JsUtil.insert_js("jQuery(function() {scrollPagTool(\'scrollPagBody\',\'"+this.getSimpleAction()+"\',\'"+0+"\');});", false);
    	}
	}
	
	
	/**
	 * 组装设备信息页面
	 * @return
	 */
	public CTableInfo getHostTable(SQLExecutor executor){
		CTableInfo table = new CTableInfo(_("No values found."));
		table.attr("id", "scrollPagBody");
		CArray mons = array(make_sorting_header(_("virtualMachineName"), "name"),_("virtualMachineIp"),_("the status Of VM"),_("the user Of Vm"),_("CPU_RATE"),_("CPU_LOAD"),
				_("memoryRate"),_("disk_vm"),_("disk_Rate"),_("disk_read_and_write"),_("Operations"));
		table.setHeader(mons);
		if(empty(Nest.value(_REQUEST, "output").asString()))
			return table;
		
		int filterShowWithoutData = get_request("show_without_data", 0);
		String sortfield = get_request("name", "name");
				//getPageSortField(getIdentityBean(), executor,"name");
		String sortorder;
		if(isset(_REQUEST,"sortorder")){
			sortorder = get_request("sortorder");
			if(empty(sortorder)){
				sortorder="ASC";
			}
		}else{
			sortorder = getPageSortOrder(getIdentityBean(), executor);
		}
	    //下拉式分页
		CArray<Map> hosts=array();
		CHostGet option = new CHostGet();
		option.setGroupIds(IMonConsts.MON_VM);
		option.setOutput(new String[]{"hostid", "name", "status","host"});
		option.setEditable(true);
		option.setSortfield(sortfield);
		option.setSelectInterfaces(new String[] { "ip" });
		option.setSortorder(get_request("sortorder", RDA_SORT_UP));
		option.setPreserveKeys(true);		//以主键作为Map中的key
		hosts = API.Host(getIdentityBean(), executor).get(option,true,Nest.value(_REQUEST, "start").asString()!=null&&0!=Nest.value(_REQUEST, "start").asInteger()?Nest.value(_REQUEST, "start").asInteger():0,EasyObject.asInteger(scrollPagCount));
		
		Map user=CWebUser.data();
		String tenantstr=(String)user.get("tenantid");
		//order_result(hosts, sortfield, sortorder);
		
		for(Map host:hosts){
			String vmip=null;
			CArray<Map> ipmaps = Nest.value(host, "interfaces").asCArray();
			for(Map ipmap:ipmaps){
				vmip=Nest.value(ipmap, "ip").asString();
			}
			
			String hostname = Nest.value(host, "name").asString();
			Long hostid= Nest.value(host, "hostid").asLong();
			CArray cells = array(new CLink(hostname, "tenan.action?form=update&templateid="+IMonConsts.A_TYPE_VM+"&hostid="+Nest.value(host,"hostid").asString()), vmip);
			int i = 0;
			for(Column column: columns) {
				Object text = column.cell(hostid);
				if(i==0){
					String status_vm =(String)text;
					if ("ACTIVE".equals(status_vm) || " ".equals(status_vm)) {
						text = _("ACTIVE"); // 运行
					}else if("SUPENDED".equals(status_vm)){
						text = _("SUPENDED"); // 挂起
					}else if("PAUSED".equals(status_vm)){
						text = _("PAUSED"); // 暂停
					}else if("SHUTOFF".equals(status_vm)){
						text = _("SHUTOFF"); // 关机
					}else if ("--".equals(status_vm)) {
						text = _("Unknown");
					}
				}
				i++;
				CArray<String> attrs = column.attrs();
				if(column.attrs() == null) {
					cells.add(text);
				}else {
					CCol c = new CCol(text);
					for(Entry<Object, String> entry: attrs.entrySet()) {
						c.attr(String.valueOf(entry.getKey()), entry.getValue());
					}
					cells.add(c);
				}
			}
			cells.add(array(array(new CLink(_("triggerConfig"), "tmon_vm.action?form=edit&hostid="+ Nest.value(host, "hostid").asLong()+"&vmip="+vmip))));
			table.addRow(cells);
		}
		return table;
	}
	
	/** 更新或者创建监控指标
	 * @param executor
	 * @param key
	 * @param hostid
	 * @param delay
	 */
	private void updaeOrcreateItem(final SQLExecutor executor,String key,Long hostid,int delay){
		if("cpuUtil".equals(key)){
			CItemGet itemGet = new CItemGet();
			itemGet.setHostIds(hostid);
			itemGet.setOutput(new String[] {"itemid", "name", "params"});
			itemGet.setFilter("key_",key);
			itemGet.setPreserveKeys(true);
			Map items = reset((CArray<Map>)API.Item(getIdentityBean(), executor).get(itemGet));
			String params ="100-last(\"system.cpu.util[,idle]\","+(delay*4)+")";
			final Map item = map("delay", delay,
					"params", params,
					"itemid",Nest.value(items, "itemid").asString(),
					"tenantid",getIdentityBean().getTenantId());
			
			boolean result=DBUtil.DBexecute(executor, SQL_UPDATE_PARAMS_ITEM, item);
		}else{
			Long itemid=getItemid(executor,hostid,key);
			updateItem(executor,itemid,delay);
		}
		  
	}
	
	/**获取监控指标
	 * @param executor
	 * @param hostid
	 * @param key
	 * @return
	 */
	private Map getItem(final SQLExecutor executor,Long hostid,String key){
		CArray<Map> items = array();
		Map item=null;
		if (!empty(hostid)) {
			CItemGet itemGet = new CItemGet();
			itemGet.setHostIds(hostid);
			itemGet.setOutput(new String[] {"itemid", "name", "state"});
			itemGet.setFilter("key_",key);
			itemGet.setPreserveKeys(true);
			items = API.Item(getIdentityBean(), executor).get(itemGet);
			for(Map it:items){
				item=it;
			}
		}
		return item;
	}
	
	/** 获取监控指标id
	 * @param executor
	 * @param hostid
	 * @param key
	 * @return
	 */
	private Long getItemid(final SQLExecutor executor,Long hostid,String key){
		Long itemid=0L;
		CArray<Map> items = array();
		Map item=null;
		if (!empty(hostid)) {
			CItemGet itemGet = new CItemGet();
			itemGet.setHostIds(hostid);
			itemGet.setOutput(new String[] {"itemid", "name", "state"});
			itemGet.setFilter("key_",key);
			itemGet.setPreserveKeys(true);
			items = API.Item(getIdentityBean(), executor).get(itemGet);
			for(Map it:items){
				itemid=Nest.value(it, "itemid").asLong();
			}
		}
		return itemid;
	}
	 
	private boolean updateItem(final SQLExecutor executor,Long itemid,int delay){
		final Map item = map("delay", delay,
				"itemid",itemid,
				"tenantid",getIdentityBean().getTenantId());
		boolean result=DBUtil.DBexecute(executor, SQL_UPDATE_ITEM, item);
		return result;
	}
	
	/**
	 * 保存动作
	 */
	public boolean saveAction(final SQLExecutor executor, CArray triggerIdCA){	
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
//				Nest.value(mediatypeToUser, userid).$s(true).put("userid", userid);
			}			
		}
		if(!empty(mediatypeToUser)){
			Map commOperations=map(
					"evaltype"			, Defines.ACTION_EVAL_TYPE_AND_OR,
					"opmessage"			, map("default_msg"	, 1, 
									 		  "mediatypeid"	, "", 
									 		  "subject"		, _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
									 		  "message"		, _("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER")),
					"mediatypeid"		, Defines.MEDIA_TYPE_EMAIL,
					"action"			, "create",
					"opmessage_usr"		, "",
					"esc_period"		, 0,
					"esc_step_from"		, 1,
					"esc_step_to"		, 1,   
					"operationtype"		, Defines.OPERATION_TYPE_MESSAGE
			);
			Map opmssageUser = map();
			CArray<Map> operations = array();
			for(Entry meUsers : mediatypeToUser.entrySet()){
				Nest.value(commOperations, "opmessage","mediatypeid").$(meUsers.getKey());
				Map userMap = (Map) meUsers.getValue();
				String[] userIds = Nest.as(userMap).asCArray().keys();
				for(String userId:userIds){
//					opmssageUser.put(userId, userMap.get(userId));
					Map operationSub = Clone.deepcopy(commOperations);
					Map opmessageUsrMap = map();
					Map userSub = map("userid",userId);
					opmessageUsrMap.put(userId, userSub);
					Nest.value(operationSub, "opmessage_usr").$(opmessageUsrMap);
//					Nest.value(operationSub, "opmessage_usr",userId,"userid").$(userId);
					operations.add(operationSub);
				}
			}
//			Nest.value(commOperations, "opmessage_usr").$(opmssageUser);
//			CArray<Map> operations = array(commOperations);
			CArray<Map> conditions = array();
			Long[] triggerIds = Nest.value(triggerIdCA, "triggerids").asCArray().valuesAsLong();
			for(Long triggerId:triggerIds){
				conditions.add(map("conditiontype" , Defines.CONDITION_TYPE_TRIGGER,
						   		   "operator"	   , Defines.CONDITION_OPERATOR_EQUAL,
						   		   "value"		   , triggerId));
			}
				
			final Map action = map(
					"name"			, triggerIds[0].toString(),
					"evaltype"		, ACTION_EVAL_TYPE_AND_OR,
					"status"		, get_request("status", ACTION_STATUS_ENABLED),
					"esc_period"	, 3600,
					"def_shortdata"	, _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
					"def_longdata"	, _("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER"),
					"recovery_msg"	, 0,
					"r_shortdata"	, _("TENANT_VM_ACTION_DEFAULT_SUBJ_TRIGGER"),
					"r_longdata"	, _("TENANT_VM_ACTION_DEFAULT_MSG_TRIGGER"),
					"conditions"	, conditions, 
					"operations"	, operations,
					"eventsource"	, Defines.EVENT_SOURCE_TRIGGERS
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
	 * 查询告警对应的告警响应id
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
	
	public static void main(String[] args){
		int delay=30;
		delay=(delay==0?900000:TvmUtil.IscontainsDelay(delay)?delay:900000);
		System.out.println(delay);
	}
	
	public String getSimpleAction(){
		 return "tmon_vm.action";
	}
}
	
