package com.isoft.biz.web.platform.topo;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.common.util.ItemsKey.*;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.web.bean.Column.column;
import static com.isoft.iradar.web.bean.Key.avg;
import static com.isoft.iradar.web.bean.Key.count;
import static com.isoft.iradar.web.bean.Key.status;
import static com.isoft.iradar.web.bean.Key.sum;
import static com.isoft.iradar.web.bean.Key.value;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveEventDescription;
import static com.isoft.iradar.web.bean.Column.column;
import static com.isoft.iradar.web.bean.Key.avg;
import static com.isoft.iradar.web.bean.Key.count;
import static com.isoft.iradar.web.bean.Key.sum;
import static com.isoft.iradar.web.bean.Key.value;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.bean.Column;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 拓扑监控数据获取
 *
 */
public class MonitorDataAction  extends RadarBaseAction {

	/**
	 * 1:hostMonitorData
	 * 	 获取设备的监控数据,参数：  1.ajaxRequestType=hostMonitorData
	 * 						  2.hostType
	 * 						  3.hostid(数组形式,由前端传递)
	 * 2:hostEventData
	 *  获取设备的告警信息,参数：   1.ajaxRequestType=hostEventData
	 *  					  2.hostid(数组形式,由前端传递)
	 * 3:hostInventoryData
	 *  获取设备的资产信息,参数：   1.ajaxRequestType=hostInventoryData
	 *   					  2.hostid(数组形式,由前端传递)
	 */
	
	public static Map ajaxRequest = map("hostMonitorData"  ,"1",
										"hostEventData"    ,"2",
										"hostInventoryData","3");
	
	public static Map networkColumn = map("columns",array(
					column("RuningTime", 		value(COMMON_NET_SYSUPTIME, 2,true)),
					column("ifNumber", 			value(COMMON_NET_IFNUMBER)),
					column("NetIFInOutERR", 	sum(COMMON_NET_IFINERRORS, true),sum(COMMON_NET_IFOUTERRORS, true)),
					column("netRate", 			sum(COMMON_NET_IFINOCTETS, true),sum(COMMON_NET_IFOUTOCTETS, true))));
	
	public final static CArray CONFIGS = map(
			HostConstants.CATEGORY_VM, map(
				"columns", array(
					column("cpu", 		        value(array(CPU_RATE_VM_LINUX,CPU_RATE_VM_WINDOWS),2,false)),
					column("memory", 	        value(MEMORY_RATE_VM, 2,false)),
					column("upflow", 	        sum(NET_UP_FLOW_VM, true)),
					column("downflow",	        sum(NET_DOWN_FLOW_VM, true)))
			),
			HostConstants.CATEGORY_TOMCAT,map(
				"columns", array(
					column("errorcount", 		value(ERRORCOUNT_TOMCAT)),
					column("activesessions", 	value(ACTIVESESSIONS_TOMCAT)),
					column("curthreadsbusy", 	value(CURRENTTHREADSBUSY)),
					column("heapmemoryusage", 	value(HEAPMEMORYUSAGEUSED)))
			),
			HostConstants.CATEGORY_MYSQL,map(
				"columns", array(
					column("freememory", 		value(FREE_MEMORY_MYSQL,2,true)),
					column("threadsconnected", 	value(THREADS_CONNECTED_MYSQL)))
			),
			HostConstants.CATEGORY_SERVER,map(
				IMonGroup.MON_SERVER_LINUX.id(),map(
					"columns", array(
						column("cpu", 			value(CPU_USER_RATE,2,false)),
						column("memory", 		avg(MEMORY_USELV_KEY, 2,false)),
						column("upflow", 		sum(NET_UP_SPEED, true)),
						column("downflow", 		sum(NET_DOWN_SPEED, true))
					)),
				IMonGroup.MON_SERVER_WINDOWS.id(),map(
					"columns", array(
						column("cpu", 		    value(CPU_USER_RATE_WINDOWS,2,false)),
						column("memory", 	    value(MEMORY_USELV_KEY_WINDOWS, 2,false)),
						column("upflow", 	    sum(NET_UP_FLOW_WINDOWS, true)),
						column("downflow", 	    sum(NET_DOWN_FLOW_WINDOWS, true))
					))
			),
			HostConstants.CATEGORY_ROUTER,      networkColumn,
			HostConstants.CATEGORY_ROUTE_SWITCH,networkColumn,
			HostConstants.CATEGORY_SWITCH,      networkColumn
	);
	
	@Override
	protected void doInitPage() {
		page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		Map response = null;
		String ajaxRequestType = Nest.value(_REQUEST, "ajaxRequestType").asString();
		switch(Nest.value(ajaxRequest, ajaxRequestType).asInteger()){
			case 1:
				response = doGetHostMonitorData(executor);
				break;
			case 2:
				response = doGetHostEventData(executor);
				break;
			case 3:
				response = doGetHostInventoryData(executor);
				break;
		}
		doAjaxDataResponse(response);
		return true;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		// TODO Auto-generated method stub
		
	}
	
	public void doAjaxDataResponse(Map response){
		AjaxResponse ajaxResponse = new AjaxResponse();
		if(Cphp.isset(response))
			ajaxResponse.success(response);
		else
			ajaxResponse.error("nothing");
		ajaxResponse.send();
	}
	
	public Map doGetHostMonitorData(SQLExecutor executor){
		Map response = map();
		CArray<Map> hosts = Nest.value(_REQUEST, "hosts").asCArray();
		doGetHostMonitorData(executor,hosts,response);
		return response;
	}
	
	public void doGetHostMonitorData(SQLExecutor executor,CArray<Map> hosts,Map response){
		for(Map host:hosts){
			String hostType = Nest.value(host, "hostType").asString();
			if(HostConstants.CATEGORY_VM.equals(hostType))
				doGetVmHostMonitorData(executor,response,hostType,array(Nest.value(host, "hostid").asString()));
			else if(HostConstants.CATEGORY_SERVER.equals(hostType))
				doGetServerMonitorData(executor,response,hostType,array(Nest.value(host, "hostid").asString()));
			else 
				doGetCommonMonitorData(executor,response,hostType,array(Nest.value(host, "hostid").asString()));
		}
	}
	
	public void doGetVmHostMonitorData(SQLExecutor executor,Map response,String hostType,CArray hostIds){
		CArray configs = (CArray)CONFIGS.get(hostType);
		Map paraMap = new LinkedMap();
		for(String hostId:hostIds.valuesAsString()){
			paraMap.put("hostid_os", hostId);
			CArray<Map> hosts = doGetHostId(executor,paraMap);
			CArray<Column> columns = (CArray)configs.get("columns");
			if(Cphp.empty(hosts)){
				response.put(hostId, doGetCommonMonitorData(columns,Nest.as(hostId).asLong()));
			}else{
				for(Map host:hosts){
					Long hostid = Nest.value(host, "hostid").asLong();
					String hostid_os = Nest.value(host, "hostid_os").asString();
					response.put(hostid_os, doGetCommonMonitorData(columns,hostid));
				}
			}
		}
	}
	
	public void doGetServerMonitorData(SQLExecutor executor,Map response,String hostType,CArray hostIds){
		CArray configs = (CArray)CONFIGS.get(hostType);
		CArray<Column> columns = null;
		for(long hostId:hostIds.valuesAsLong()){
			CArray groupIds = doGetGroupId(executor,hostId);
			if(groupIds.containsValue(IMonGroup.MON_SERVER_LINUX.id()) || groupIds.containsValue(IMonConsts.MON_CLOUD_CEPH) || groupIds.containsValue(IMonConsts.MON_CLOUD_COMPUTER) || groupIds.containsValue(IMonConsts.MON_CLOUD_CONTROLER) || groupIds.containsValue(IMonConsts.MON_CLOUD_NETWORK) || groupIds.containsValue(IMonConsts.MON_CLOUD_WEB))
				columns = Nest.value(configs, IMonGroup.MON_SERVER_LINUX.id(),"columns").asCArray();
			else if(groupIds.containsValue(IMonGroup.MON_SERVER_WINDOWS.id()))
				columns = Nest.value(configs, IMonGroup.MON_SERVER_WINDOWS.id(),"columns").asCArray();
			else 
				columns = Nest.value(configs, IMonGroup.MON_SERVER_LINUX.id(),"columns").asCArray();
			response.put(hostId, doGetCommonMonitorData(columns,hostId));
		}
	}
	
	public void doGetCommonMonitorData(SQLExecutor executor,Map response,String hostType,CArray hostIds){
		CArray configs = (CArray)CONFIGS.get(hostType);
		CArray<Column> columns = (CArray)configs.get("columns");
		for(long hostId:hostIds.valuesAsLong()){
			response.put(hostId, doGetCommonMonitorData(columns,hostId));
		}
	}
	
	public Map doGetCommonMonitorData(CArray<Column> columns,Long hostid){
		Map data = map();
		for(Column column: columns) {
			Object value = column.cell(hostid);
			if(LatestValueHelper.NA.equals(value)){
				value = 0;
			}
			data.put(column.getName(), value);
		}
		return data;
	}
	
	public CArray<Map> doGetHostId(SQLExecutor executor,Map param){
		CHostGet option = new CHostGet();
		option.setEditable(false);
		option.setFilter(param);
		option.setOutput(new String[]{"hostid","hostid_os"});
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(option);
		return hosts;
	}
	
	public CArray doGetGroupId(SQLExecutor executor,long hostId){
		CHostGroupGet option = new CHostGroupGet();
		option.setHostIds(hostId);
		option.setEditable(false);
		option.setOutput(new String[]{"groupid"});
		CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(option);
		return FuncsUtil.rda_objectValues(groups, "groupid");
	}
	
	public Map doGetHostEventData(SQLExecutor executor){
		Map response = map();
		Map paraMap = new LinkedMap();
		String[] hostIds = Nest.value(_REQUEST, "hostid").asCArray().valuesAsString();
		CTriggerGet triggerop = new CTriggerGet();
		triggerop.setOnly_true(true);
		triggerop.setOutput(Defines.API_OUTPUT_EXTEND);
		triggerop.setWithUnacknowledgedEvents(true);
		triggerop.setSortfield("priority","description");
		triggerop.setSortorder(RDA_SORT_DOWN,RDA_SORT_UP);
		triggerop.setLimit(1);
		triggerop.setEditable(false);
		for(String hostId:hostIds){
			paraMap.put("hostid_os", hostId);
			CArray<Map> hosts = doGetHostId(executor,paraMap);
			long hostid = 0L;
			if(Cphp.empty(hosts))
				hostid = Nest.as(hostId).asLong();
			else
				hostid = Nest.value(hosts,0,"hostid").asLong();
			triggerop.setHostIds(hostid);
			CArray<Map> triggers = API.Trigger(getIdBean(), executor).get(triggerop);
			Map result = new LinkedMap();
			paraMap.put("hostId", hostid);
			if(triggers.size()>0){
				Map trigger = Cphp.reset(triggers.toList());
				String description = CMacrosResolverHelper.resolveTriggerName(getIdBean(), executor, Nest.as(trigger).asCArray());;
				result.put("description", description);
				result.put("priority", Nest.value(trigger,"priority").asInteger());
			}
			result.put("error", new HostExpDAO(executor).doHostExpGet(paraMap));
			response.put(hostId, result);
		}
		return response;
	}
	
	public Map doGetHostInventoryData(SQLExecutor executor){
		Map response = map();
		Long[] hostIds = Nest.value(_REQUEST, "hostid").asCArray().valuesAsLong();
		CHostGet option = new CHostGet();
		option.setOutput(new String[]{"hostid"});
		option.setSelectInventory(API_OUTPUT_EXTEND);
		option.setSelectGroups(API_OUTPUT_EXTEND);
		option.setEditable(false);
		SystemDAO sys = new SystemDAO(executor);
		Map systemType = map();
		for(long hostId:hostIds){
			String contractNumber = null;
			String osFull = null;
			String hardware = null;
			String software = null;
			String urlA = null;
			Map hostInventory = map();
			option.setHostIds(hostId);
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(option);
			for (Entry<Object, Map> e : hosts.entrySet()) {
                Object num = e.getKey();
                Map host = e.getValue();
                
                contractNumber = Nest.value(host,"inventory","contract_number").asString();
                osFull = Nest.value(host,"inventory","os_full").asString();
                hardware = Nest.value(host,"inventory","hardware").asString();
                software = Nest.value(host,"inventory","software").asString();
                urlA = Nest.value(host,"inventory","url_a").asString();
                
                if (empty(osFull)) {
                	osFull = "-";
    			}
    			if (empty(hardware)) {
    				hardware = "-";
    			} else {
    				systemType.put("type", "dept");
    				systemType.put("dkey", hardware);
    				List<Map> all = sys.doSystem(systemType);
    				if(empty(all)){
    					hardware = "-";	
    				}
    				for (Map m : all) {
    					hardware = (String) m.get("dlabel");
    				}
    			}
    			if (empty(software)) {
    				software = "-";
    			} else {
    				systemType.put("type", "motor_room");
    				systemType.put("dkey", software);
    				List<Map> all = sys.doSystem(systemType);
    				if(empty(all)){
    					software = "-";	
    				}
    				for (Map m : all) {
    					software = (String) m.get("dlabel");
    				}
    			}
    			if (empty(urlA)) {
    				urlA = "-";
    			} else {
    				systemType.put("type", "cabinet");
    				systemType.put("dkey", urlA);
    				List<Map> all = sys.doSystem(systemType);
    				if(empty(all)){
    					urlA = "-";	
    				}
    				for (Map m : all) {
    					urlA = (String) m.get("dlabel");
    				}
    			}
    			if (empty(contractNumber)) {
    				contractNumber = "-";
    			}
			}
			hostInventory.put("contractNumber", contractNumber);
			hostInventory.put("osFull", osFull);
			hostInventory.put("hardware", hardware);
			hostInventory.put("software", software);
			hostInventory.put("urlA", urlA);
			response.put(hostId, hostInventory);
		}
		return response;
	}
	
	public Map doSortAndRest(CArray<Map> events){
		CArrayHelper.sort(events, (CArray)array(
				map("field", "clock", "order", RDA_SORT_DOWN),
				map("field", "priority", "order", RDA_SORT_DOWN)
		));
		return Cphp.reset(events);
	}
	
}

