package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_map;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveEventDescription;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**运维管理action
 * @author HP Pro3330mt
 *
 */
public class HostOperationsAction extends RadarBaseAction {
	
	private int ttriggerid=0;//告警id，用于在最近告警中传值
	private int eeventid=0;//事件id，用于在最近告警中传值
	private String triggerDescription;//最近事件描述
	
	public final static String[] PRIORITIES_WARNING = new String[] {
		""+Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED,
		""+Defines.TRIGGER_SEVERITY_INFORMATION, 
		""+Defines.TRIGGER_SEVERITY_WARNING
	};
	
	public final static String[] PRIORITIES_ERROR = new String[] {
		""+Defines.TRIGGER_SEVERITY_AVERAGE, 
		""+Defines.TRIGGER_SEVERITY_HIGH, 
		""+Defines.TRIGGER_SEVERITY_DISASTER
//		,Defines.TRIGGER_SEVERITY_COUNT
	};

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of hosts"));
		page("file", "operation.action");
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("hist_arg", new String[] {"groupid"});
		page("css", new String[] {"lessor/devicecenter/operation.css"});
	}
	
	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray fields = map(
				"triggerid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"eventid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"hosts",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"groups",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"hostids",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"groupids",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
				"name",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null),
				// actions
				"go",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				// other
				"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"form_refresh",	array(T_RDA_STR, O_OPT, null,	null,		null),
				// ajax
				"favobj",						array(T_RDA_STR, O_OPT, P_ACT,		null,			null),
				"favref",						array(T_RDA_STR, O_OPT, P_ACT,		NOT_EMPTY,		"isset({favobj})"),
				"favstate",					array(T_RDA_INT, O_OPT, P_ACT,		NOT_EMPTY,		"isset({favobj})&&\"filter\"=={favobj}")
			);
			check_fields(getIdentityBean(), fields);
	}
	
	@Override
	protected void doPermissions(SQLExecutor executor) {

	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
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
	protected void doAction(SQLExecutor executor) {
		/* Display */
		CWidget hostsWidget = new CWidget();//定义运维管理界面
		
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups", map("real_hosts", true,"editable", true),
				"groupid", get_request("groupid", null)
			));//定义设备类型条件
		
		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());
		Nest.value(_REQUEST,"hostid").$(get_request("hostid", 0));
		
		//定义列表表头查询表单
		CForm frmGroup = new CForm("get");

		CComboBox temboBox=pageFilter.getGroupsCB();
		List list =temboBox.items;
		int k=list.size();
		for(int i=0;i<k;i++){
			if(list.get(i).toString().contains("Discovered hosts")||list.get(i).toString().contains("Templates")){
				list.remove(i);
				k--;
				i--;
			}
			
		}
		frmGroup.addItem(array(_("Group")+SPACE, temboBox));

		hostsWidget.addHeader(frmGroup);
		
		
		CForm form = new CForm();//定义显示数据表单
		form.setName("hosts");
		
		CToolBar tb = new CToolBar(form);
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		String oldClass = headerActions.getAttribute("class").toString();
		oldClass += " headeractions_hide";
		headerActions.setAttribute("class", oldClass);
		hostsWidget.addItem(headerActions);
		
		CTableInfo table = new CTableInfo(_("No hosts found."));//定义显示设备表
		table.setHeader(getHeader(form));//设置表头
        		
		String sortfield = getPageSortField(getIdentityBean(), executor, "name");//排序字段
		String sortorder = getPageSortOrder(getIdentityBean(), executor);//排序方式
		
		/*获取设备列表,设备列表分页
		 */ 
		CArray<Map> hosts = array();
		Map<String, Object> config = select_config(getIdentityBean(), executor, true);
		CHostGet options = null;
		options = new CHostGet();
		if (pageFilter.$("groupsSelected").asBoolean()) {
			if (pageFilter.$("groupid").asInteger()>0) {//选中的设备类型
				options.setGroupIds(pageFilter.$("groupid").asLong());
			}
			options.setEditable(true);
			options.setSortfield(sortfield);
			options.setSortorder(sortorder);
			options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			hosts = API.Host(getIdentityBean(), executor).get(options);
		}else{
			hosts = array();
		}
		//运维管理当前页
		CTable paging = getPagingLine(getIdentityBean(), executor, hosts, array("hostid"));//定义页面显示值，从几到几及总记录数
		
		options = new CHostGet();
		options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectParentTemplates(new String[]{"hostid", "name"});
		options.setSelectTriggers(API_OUTPUT_COUNT);
		hosts = API.Host(getIdentityBean(), executor).get(options);//得到当前页设备表
		order_result(hosts, sortfield, sortorder);//结果排序
		
		for (Map host : hosts) {//遍历设备表，给每行赋值
			String groupid = Nest.value(_REQUEST,"groupid").asString();//设备分组
			String hostid = Nest.value(host,"hostid").asString();//设备id
			
			/*设备名称*/
			CArray description = array();
			//description.add(new CLink(CHtml.encode(Nest.value(host,"name").asString()), "hostMonitor.action?form=update&hostid="+(Nest.value(host,"hostid").asString()+url_param("groupid"))));
			description.add(CHtml.encode(Nest.value(host,"name").asString()));
			/*状态*/
			CArray status = array();
			String statusCaption;
			switch (Nest.value(host,"status").asInteger()) {
				case HOST_STATUS_MONITORED:
					if (Nest.value(host,"maintenance_status").asInteger() == HOST_MAINTENANCE_STATUS_ON) {
						statusCaption  = _("In maintenance");
					} else {
						statusCaption = _("Monitored");
					}
					break;
				case HOST_STATUS_NOT_MONITORED:
					statusCaption = _("Not monitored");
					break;
				default:
					statusCaption = _("Unknown");
			}
			status.add(statusCaption);
			
			/*是否可用*/
			int available = Nest.value(host, new Object[] { "available" }).asInteger();
			int ipmi = Nest.value(host, new Object[] { "ipmi_available" }).asInteger();
			int snmp = Nest.value(host, new Object[] { "snmp_available" }).asInteger();
			int jmx = Nest.value(host, new Object[] { "jmx_available" }).asInteger();
			
			int availableStr;
			if(available==HOST_AVAILABLE_UNKNOWN && ipmi==HOST_AVAILABLE_UNKNOWN 
					&& snmp==HOST_AVAILABLE_UNKNOWN && jmx==HOST_AVAILABLE_UNKNOWN){//四个available都为零则为未知的
//				availableStr=_("Unknown");//未知的
				availableStr = 2;
			}else if(available== HOST_AVAILABLE_TRUE || ipmi==HOST_AVAILABLE_TRUE 
					|| snmp== HOST_AVAILABLE_TRUE || jmx==HOST_AVAILABLE_TRUE){//四个available其中一个为1则为可用
//				availableStr=_("Available");//可用
				availableStr = 0;
			}else{//其他值则为不可用
//				availableStr=_("Not available");//不可用
				availableStr = 1;
			}
			
			/*活动告警与活动故障合并为最近事件*/
			ArrayList<String>  evenlist=(ArrayList<String>)getEventList(executor,true,Nest.value(host,"hostid").asLong());
			String alarmNum=getEventList(executor,true,Nest.value(host,"hostid").asLong()).get(0) ;//活动告警数目
			/*CArray triggers = array(new CLink(_("activeAlarm"), "activealarm.action?groupid="+groupid+"&hostid="+hostid),
					" ("+alarmNum+")");*/
			
			String faultNum= getEventList(executor,false,Nest.value(host,"hostid").asLong()).get(0);//活动故障数
			
			//活动告警和活动故障
			Object alarmLink = new CLink(_("activeAlarm"), "activealarm.action?groupid="+groupid+"&hostid="+hostid);
			Object faultLink = new CLink(_("activeFault"), "activefault.action?groupid="+groupid+"&hostid="+hostid);
			
			if("0".equals(alarmNum)){
				alarmLink = _("Active Trigger");
			}
			if("0".equals(faultNum)){
				faultLink = _("Active Error");
			}
			
			CArray activeEvent = array(alarmLink," ("+alarmNum+")",BR(),faultLink," ("+faultNum+")");
			/*最近告警*/
			//String triggerdes = evenlist.get(1);
			CArray reEvent = array(new CLink(triggerDescription,"tr_events.action?triggerid="+ttriggerid+"+&eventid="+eeventid));
			
			//给当前表格添加数据
			table.addRow(getRowData(executor, host,description,availableStr,status,activeEvent,reEvent));
		}
		form.addItem(array(table, paging));
		hostsWidget.addItem(form);//给当前页面添加form表单
		
		hostsWidget.show();
	}
	
	/**设备列表表头
	 * @param form
	 * @return
	 */
	protected CArray getHeader(CForm form){
		return array(
				_("equipmentname"),
				_("equipmenttype"),
				_("Availability"),//可用性
				_("Status"),//"维护状态",
				_("Move Events"),
				_("Last Events")
			);
	}

	/** 给页面填充数据方法
	 * @param executor
	 * @param host
	 * @param description
	 * @param isAvailable
	 * @param maintenanceStatus
	 * @param triggers
	 * @param faults
	 * @param reEvent
	 * @return
	 */
	protected CArray<Serializable> getRowData(SQLExecutor executor, Map host,CArray description,int availableStr,
			CArray maintenanceStatus,CArray activeEvent,CArray reEvent) {
		/**
		 * @param 关联设备类型
		 */
		Long hostid = Nest.value(host, "hostid").asLong();//获取设备id
		CHostGroupGet params = new CHostGroupGet();
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
		
		//设置可用显示为切换按钮
		CCol status = new CCol(new CDiv(new CSpan(
			status2str(availableStr),status2style(availableStr)
		), "switch"));
		
		return array(
		//	new CCheckBox("hosts["+Nest.value(host,"hostid").asString()+"]", false, null, Nest.value(host,"hostid").asString()),
			description,//设备名称
			type,//设备类型
			status,//是否可用
			maintenanceStatus,//维护状态
			activeEvent,//活动告警和活动故障合并为活动事件
			reEvent//最近告警
		);
	}

	/**获取活动告警或者活动故障数目以及最近告警描述方法
	 * @param executor sql执行程序
	 * @param isShowWarnings true为告警，false则为故障
	 * @param hostid 设备id，通过该值关联告警或故障表
	 * @return
	 */
	public List<String> getEventList(SQLExecutor executor,boolean isShowWarnings,Long  hostid){
		List<String> eventlist=new ArrayList<String>();
		//定义要查找的告警或者故障表
		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setOutput(new String[]{"triggerid"});
		triggerOptions.setPreserveKeys(true);
		triggerOptions.setMonitored(true);
		if(isShowWarnings) {//是告警，告警级别为0,1,2
			triggerOptions.setFilter("priority", PRIORITIES_WARNING);
		}else {//否则为故障，故障级别为3,4,5,6
			triggerOptions.setFilter("priority", PRIORITIES_ERROR);
		}
		
		//定义要查找的事件表
		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setOutput(new String[]{"eventid", "objectid"});
		eventOptions.setSortfield("clock", "eventid");//排序字段
		eventOptions.setSortorder(RDA_SORT_DOWN);//倒序
		eventOptions.setLimit(100 + 1);//对数据进行限制，提高效率
		//根据事件的条件过滤查询结果，1是活动数据，而非历史数据，这要是区分处理或未处理关键
		eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
		
		/*
		 * 以下是取得告警表和事件表的关键，总体思路是取两者的并集并过滤两者相同结果集
		 */
        CArray filterTriggerIds;
		
		CTriggerGet tget = new CTriggerGet();
		tget.setOutput(new String[]{"triggerid"});
		tget.setHostIds(hostid);//通过设备id关联
		tget.setMonitored(true);
		tget.setPreserveKeys(true);
		if(isShowWarnings) {//是告警，告警级别为0,1,2
			tget.setFilter("priority", PRIORITIES_WARNING);
		}else {//否则为故障，故障级别为3,4,5,6
			tget.setFilter("priority", PRIORITIES_ERROR);
		}
		
		CArray<Map> hostTriggers = API.Trigger(getIdentityBean(), executor).get(tget);
		filterTriggerIds = array_map(new ArrayMapCallback() {
			@Override public Object call(Object... objs) {
				return strval(objs[0]);
			}
		}, array_keys(hostTriggers));
		
		CArray knownTriggerIds = array();
		CArray validTriggerIds = array();
		
		knownTriggerIds = array_combine(filterTriggerIds, filterTriggerIds);
		validTriggerIds = knownTriggerIds;
		
		eventOptions.setHostIds(hostid);
		eventOptions.setObjectIds(validTriggerIds.valuesAsLong());
		/*这段代码比较难懂，也是关键部分*/
		CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);//取得当前设备关联且符合条件的事件表结果
		
		CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

		CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);
		
		if (!empty(unknownTriggerIds)) {
			Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
			CArray<Map> validTriggersFromSlice = API.Trigger(getIdentityBean(), executor).get(triggerOptions);

			for(Map trigger:validTriggersFromSlice) {
				validTriggerIds.put(trigger.get("triggerid"),trigger.get("triggerid"));
			}
		}
		
		CArray<Map> events = array();
		
		for(Map event:allEventsSlice) {
			if (isset(validTriggerIds,event.get("objectid"))) {
				events.add(map("eventid" , Nest.value(event,"eventid").$()));//符合条件的事件，这里只存有其id值
			}
		}
		int number = count(events);//取得事件总数目
		//以下代码可以进行优化，将两个代码可以合并
		
		int i=0;
		//以下方法获取最近告警描述以及告警id和事件id
		if(number!=0){//数目不为零，并且是告警才进行最近告警查询,提高效率
			//通过查找到符合条件的id查询所需的事件数据
			CEventGet eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_TRIGGERS);
			eget.setObject(EVENT_OBJECT_TRIGGER);
			eget.setEventIds(rda_objectValues(events, "eventid").valuesAsLong());
			eget.setOutput(API_OUTPUT_EXTEND);
			eget.setSelectAcknowledges(API_OUTPUT_COUNT);
			eget.setSortfield("clock", "eventid");
			eget.setSortorder(RDA_SORT_DOWN);//按照倒序，保证事件第一条数始终为最近日期
			eget.setNopermissions(true);
			eget.setLimit(1);
			events = API.Event(getIdentityBean(), executor).get(eget); 
			//Map event=reset(Nest.value(events,"objectid").asCArray());
			
			for(Map event:events){
				i++;
				CTriggerGet trigget = new CTriggerGet();
				trigget.setTriggerIds(rda_objectValues(event, "objectid").valuesAsLong());
				trigget.setSelectHosts(new String[]{"hostid"});
				trigget.setOutput(new String[]{"description", "expression", "priority", "flags", "url"});
				CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(trigget);
				triggers = rda_toHash(triggers, "triggerid");
				
				@SuppressWarnings("unchecked")
				CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
				if(Nest.value(event,"eventid").asInteger()>=eeventid){
					triggerDescription = resolveEventDescription(getIdentityBean(), executor, rda_array_merge(trigger, map(
							"clock" , Nest.value(event,"clock").$(),
							"ns" , Nest.value(event,"ns").$()
						)));
					ttriggerid= Nest.value(event,"objectid").asInteger();
					eeventid= Nest.value(event,"eventid").asInteger();
				}
			 if(i==1)
					break;//保证只取最近事件
			}
		}else{
			triggerDescription=null;
		}
		
		if(number>100){//如果数目超过100，则表示为100+
			eventlist.add("100+");
		}else{
			eventlist.add(number+"");
		}
		//eventlist.add(triggerDescription);//给数据添加最近事件描述字段
		return eventlist;
		
	}
	 /**将状态值代码转变为状态值对应的中文名，为公共方法，不如如何写入到工具类中，目前先写在这里
		 * @param status
		 * @return
		 */
	public static String status2str(int status)
    {
	     CArray Status = CArray.map(new Object[] { 
	       Integer.valueOf(0), _("Available"), 
	       Integer.valueOf(1), _("Not available")});
	     if (Status.containsKey(Integer.valueOf(status))) {
	         return (String)Status.get(Integer.valueOf(status));
	        }
	        return Cphp._("Unknown");
    }
	/**状态值显示启用停用状态
	 * @param status
	 * @return
	 */
	public static String status2style(int status)
    {
      switch (status) {
      case 0:
         return "off start";
      case 1:
        return "on stop";
       }
      return "unknown";
    }
}
