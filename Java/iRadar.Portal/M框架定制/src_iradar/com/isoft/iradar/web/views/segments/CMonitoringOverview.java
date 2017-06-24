package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_LEFT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_COUNT;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.FuncsUtil.convertUnitsS;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CHelp;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 监控中心视图_告警列表页面
 * @author HP Pro2000MT
 *
 */
public class CMonitoringOverview extends CViewSegment {

	/**
	 * 封装向页面添加新标志的方法
	 * @param key
	 */
	public static void addJsLocal(String key){
		rda_add_post_js("if(locale) locale['"+key+"']='"+_(key)+"';");
	}
	
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		addJsLocal("Host detail");
		Long _groupid = (Long) Nest.value(data,"groupid").$();				//分组ID 用于确认要检索那个分组的应用集
		int SHOW_TRIGGERS = Nest.as(define("SHOW_TRIGGERS")).asInteger();	//显示报警中心
		int SHOW_DATA = Nest.as(define("SHOW_DATA")).asInteger();			//显示数据
		
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		rda_add_post_js("jqBlink.blink();");

		CWidget overviewWidget = new CWidget();
		boolean isSystem = IMonGroup.showableGroups().containsKey(_groupid);
		//类型 显示详情页面和简单页面
		CButton button = null;
		CInput pageType = null;
		if(Nest.value(data,"pageType").asInteger() == 0){
			button = new CButton("pageButton", SPACE, "submit()","details_pic simpleness_pic");
			button.setName("pageButton");
			pageType = new CInput("hidden", "pageType","1");
		}else{
			button = new CButton("pageButton", SPACE, "submit()","details_pic details_pic");
			button.setName("pageButton");
			pageType = new CInput("hidden", "pageType","0");
		}
		
		//类型(告警中心和数据)
		CComboBox typeComboBox = new CComboBox("pageType", Nest.value(data,"pageType").$(), "submit()");
		typeComboBox.addItem(SHOW_TRIGGERS, _("Triggers"));
		typeComboBox.addItem(SHOW_DATA, _("Data"));

		CForm headerForm = new CForm("get");
		headerForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));					//设备
		//用户自定义界面没有切换按钮
		CForm btnForm = new CForm("get");				//切换按钮form
		if(isSystem){
			CInput actionName = new CInput("hidden", "actionName",Nest.value(data, "actionName").$().toString());
			btnForm.setAction(Nest.value(data, "actionName").$().toString());
			btnForm.addItem(button);																	//切换按钮
			btnForm.addItem(pageType);																	//隐藏域
			headerForm.addItem(actionName);
		}
		Object tenantid = Nest.value(data, "tenantid").$();
		
		CTableInfo hintTable = new CTableInfo();
		hintTable.setAttribute("style", "width: 200px");

		if (Nest.value(data,"type").asInteger() == SHOW_TRIGGERS) {
			hintTable.addRow(array(new CCol(SPACE, "normal"), _("OK")));
		}

		for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
			hintTable.addRow(array(getSeverityCell(idBean, executor, i), _("PROBLEM")));
		}

		Map<String, Object> config = select_config(idBean, executor);

		if (Nest.value(data,"type").asInteger() == SHOW_TRIGGERS) {
			// blinking preview in help popup (only if blinking is enabled)
			if (Nest.value(config,"blink_period").asLong() > 0) {
				CRow row = new CRow(null);
				row.addItem(new CCol(SPACE, "normal"));
				for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
					row.addItem(new CCol(SPACE, getSeverityStyle(i)));
				}
				CTable table = new CTable("", "blink overview-mon-severities");
				table.addRow(row);

				// double div necassary for FireFox
				CCol col = new CCol(new CDiv(new CDiv(table), "overview-mon-severities-container"));

				hintTable.addRow(array(col, _s("Age less than %s", convertUnitsS(Nest.value(config,"blink_period").asDouble()))));
			}

			hintTable.addRow(array(new CCol(SPACE), _("No trigger")));
		} else {
			hintTable.addRow(array(new CCol(SPACE), _("OK or no trigger")));
		}

		CHelp help = new CHelp("web.view.action", "right");
		help.setHint(hintTable, "", "", true, false);

		// header right 添加告警级别提示
		if(isSystem){			
			btnForm.addItem(array(
					get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$())),
					SPACE,
					help
					));		
		}
		
		overviewWidget.addHeader(headerForm,btnForm);
		
		// header left
		CComboBox styleComboBox = new CComboBox("view_style", Nest.value(data,"view_style").$(), "submit()");
		styleComboBox.addItem(STYLE_TOP, _("Top"));
		styleComboBox.addItem(STYLE_LEFT, _("Left"));

		CForm hostLocationForm = new CForm("get");
		hostLocationForm.addVar("groupid", _groupid);			
		hostLocationForm.addItem(array(_("Hosts location"), SPACE, styleComboBox));		//设备位置（现已默认左侧显示）

		overviewWidget.addHeader(hostLocationForm);
		
		/*
		 *  显示监控中心视图:以应用集和设备构成
		 */
		CTableInfo dataTable = null;
		CArray<CTable> dataTableTwo = null;
		if (!empty(Nest.value(config,"dropdown_first_entry").$())|| pageFilter.$("applicationsSelected").asBoolean()|| pageFilter.$("groupsSelected").asBoolean()) {
			CArray _hostIds = array();
			if(Nest.value(data, "hostid").asInteger() > 0){
				_hostIds = Nest.value(data, "hostid").asCArray();
			}
			dataTable = getTriggersOverview(idBean, executor,
				_hostIds,												//传入要查询的设备id
				Nest.value(RadarContext.page(),"file").asString(),
				Nest.value(data,"view_style").asInteger(),null,
				_groupid,
				tenantid
			);
		} else {
			dataTable = new CTableInfo(_("No items found."));
		}
		
		if (empty(dataTable)) {
			overviewWidget.addItem(dataTableTwo);
		} else {

			overviewWidget.addItem(dataTable);
		}

		return overviewWidget;
	}
	
	
	
	/**
	 * Creates and returns the trigger overview table for the given hosts.
	 *
	 * @param array  _hostIds
	 * @param string _application	name of application to filter
	 * @param string _pageFile		the page where the element is displayed
	 * @param int    _viewMode		table display style: either hosts on top, or host on the left side
	 * @param string _screenId		the ID of the screen, that contains the trigger overview table
	 * @param groupid  分组ID 用于查询属于哪个分组的应用集
	 * @param tenantid 租户id
	 * @return CTableInfo
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo getTriggersOverview(IIdentityBean idBean, SQLExecutor executor, CArray _hostIds, String _pageFile, Integer _viewMode, String _screenId,Long groupId,Object tenantid) {
		if (is_null(_viewMode)) {
			_viewMode = asInteger(CProfile.get(idBean, executor, "web.overview.view.style", STYLE_TOP));
		}
		
		//获取服务器监察需要的 应用集
		CAppGet cAppGet = new CAppGet();
		cAppGet.setGroupIds(groupId);												//设置分组为服务器应用集
		if(_hostIds.size()>0){			
			cAppGet.setHostIds(_hostIds.valuesAsLong());
		}
		cAppGet.setOutput(new String[]{"name","applicationid","hostid"});			//选择输出的字段
		CArray<Map> appResult = API.Application(idBean, executor).get(cAppGet);		//应用集查询结果
		
		//获取服务器监察需要的应用集ID
		CArray _arrIds = array();
		for(Map arrId:appResult){
			_arrIds.put(arrId.get("applicationid"), Nest.value(arrId, "applicationid").$());
		}
		
		CArray hostIds = array();		//触发器所需要的监控设备id
		CArray<Map> _hosts = array();	//所有的监控设备
	
		//获取全部的监控设备
		CHostGet cHostGet = new CHostGet();
		cHostGet.setOutput(new String[]{"hostid","name"});
		cHostGet.setGroupIds(groupId);
		if(_hostIds.size()>0){			
			cHostGet.setHostIds(_hostIds.valuesAsLong());
		}
		if(tenantid != null && !"".equals(tenantid)){
			cHostGet.setFilter("tenantid",tenantid);
		}
		cHostGet.setPreserveKeys(true);		
		_hosts = API.Host(idBean, executor).get(cHostGet);	//监控设备集合
		
		for(Map map:_hosts){
			hostIds.put(map.get("hostid"), Nest.value(map, "hostid").$());
		}
		
		//获取触发器信息
		CTriggerGet cTrigger = new CTriggerGet();
		if(hostIds != null){
			cTrigger.setHostIds(hostIds.valuesAsLong());
		}
		cTrigger.setMonitored(true);
		cTrigger.setSkipDependent(true);
		cTrigger.setOutput(API_OUTPUT_EXTEND);
		cTrigger.setSelectHosts(new String[]{"hostid", "name"});
		cTrigger.setSortfield("description");
		cTrigger.setApplicationIds(_arrIds.valuesAsLong());							//获取触发器的时候 确定应用集ID集合
		CArray<Map> _dbTriggers = API.Trigger(idBean, executor).get(cTrigger);		//获取触发器信息
		
		CArray<Map> _triggers = array();											//触发器
		CArray<String> _hostNames = array();										//监控设备名称集合
		
		/*
		 * 实现思路: 判断一台设备是否在某一应用集中存在报警 
		 */
		Map<String, Map> appGroups = EasyList.groupBy(appResult.values(), "name", "hostid");		//应用集分组(根据name分组)
		Map<Long, Map> trigerGroups = EasyList.groupBy(_dbTriggers.values(), "applicationid");		//告警分组(把所有的告警根据applicationid分组)
		
		for(Map _host:_hosts){	 //所有行——循环所有的监控设备
			Long hostId = Nest.value(_host, "hostid").asLong();
			String _host_name = Nest.value(_host, "name").asString();
			
			for(Entry<String, Map> appEntry: appGroups.entrySet()){	 ///所有列——循环所有的监控维度	 
				String app_name = appEntry.getKey();
				Map hostAppMap = appEntry.getValue();
				Object high_applicationid = "";						//获取告警程度最高的应用集id
				
				boolean isHave = hostAppMap.containsKey(hostId);
				if(isHave){//如果当前设备有此监控维度
					List<Map> apps = (List)hostAppMap.get(hostId);
					Long appId = Nest.value(apps.get(0), "applicationid").asLong();
					
					isHave = trigerGroups.containsKey(appId);
					if(isHave){//如果当前监控维度有告警
						List<Map> triggers = (List)trigerGroups.get(appId);
						
						//同一监控维度的不同监控项 根据报警级别最高的来显示
						int maxPriority = Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
						int value = 0;
						for(Map _trigger: triggers){
							int priority = Nest.value(_trigger,"priority").asInteger();
							high_applicationid = Nest.value(_trigger, "applicationid").asLong(); 
							if(priority > maxPriority){
								maxPriority = priority;
								value = Nest.value(_trigger, "value").asInteger();
								if(maxPriority == Defines.TRIGGER_SEVERITY_DISASTER){//如果已达到最高级别，直接跳出
									break;
								}
							}
						}
						_hostNames.put(_host.get("hostid"), Nest.value(_host,"name").$());	//添加设备名称
						_triggers.put(app_name, _host_name, map(
							"hostid", hostId,
							"triggerid", null,
							"value", value,					//根据告警级别显示不同样式
							"lastchange", null,
							"priority", maxPriority,		//优先级
							"flags", null,
							"url", null,
							"hosts", array(_host),
							"showtype",1,					//是否属于触发器表示
							"applicationid",high_applicationid
						));
					}
				}
				
				
				if(!isHave){//如果此设备没有告警设置，则设置默认
					_hostNames.put(_host.get("hostid"), Nest.value(_host,"name").$());	//添加设备名称
					
					//获取没有告警的设备对应的应用集id
					CArray<Map> appCarray = Nest.value(hostAppMap, hostId).asCArray();
					if(appCarray != null){						
						for(Map map:appCarray){
							high_applicationid = map.get("applicationid");	
						}
					}
							
					_triggers.put(app_name, _host_name, map(
						"hostid", hostId,
						"triggerid", null,
						"value", null,
						"lastchange", null,
						"priority", Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED,		//优先级
						"flags", null,
						"url", null,
						"hosts", array(_host),
						"showtype",0,												//是否触发器表示
						"applicationid",high_applicationid
					));
				}
			}
		}
		
		CTableInfo _triggerTable = new CTableInfo(_("No host found"));
		if(_hosts.size() > 0){			
			if(appGroups.size() == 0 || appGroups.isEmpty()){	//如果分组设备中没有监控维度 则匹配相应的提示
				_triggerTable = new CTableInfo(_("No application found"));
				return _triggerTable;
			}
		}
		if (empty(_hostNames)) {
			return _triggerTable;
		}
		_triggerTable.makeVerticalRotation();
	
		order_result(_hostNames);
	
		//默认左侧显示 已去掉显示方式判断代码
		CArray _header = array(new CCol(_("Host"), "center"));

		for(Entry<Object, Map> entry: _triggers.entrySet()) {
			Object _description = entry.getKey();
			_header.add( new CCol(asString(_description), "vertical_rotation") );		//视图中文字显示样式(去掉后 显示旋转后的文字)
		}
		_triggerTable.setHeader(_header, "vertical_header");							

		CArray<CArray<Map>> _scripts = API.Script(idBean, executor).getScriptsByHosts(rda_objectValues(_hosts, "hostid").valuesAsLong());

		for(Entry<Object, String> entry: _hostNames.entrySet()) {						
			Object _hostId = entry.getKey();
			String _hostName = entry.getValue();	
			String _name = "host_list.action?hostid="+_hostId+"&groupid="+groupId;		
			CLink link = new CLink(_hostName, _name);
			CArray _columns = array(link);
			
			for(Map _triggerHosts: _triggers) {		
				CArray triggerCarry = Nest.value(_triggerHosts,_hostName).asCArray();
				Long applicationid = Nest.value(triggerCarry, "applicationid").asLong();
				//根据showtype判断 设备是否存在于报警中 如果没有 则显示默认的样式
				CArray cArry = Nest.value(_triggerHosts, _hostName).asCArray();
				boolean showtype = false;
				boolean is_Null = isset(_triggerHosts.get(_hostName));
				if(is_Null){
					showtype = Nest.value(cArry, "showtype").asString().equals("1")?true:false;
				}
				_columns.add( getTriggerOverviewCells(idBean, executor,
					isset(_triggerHosts.get(_hostName)) ? Nest.value(_triggerHosts, _hostName).asCArray() : null,
					_pageFile,
					_screenId,
					showtype,
					_hostId,			
					applicationid,
					groupId
				));
			}
			_triggerTable.addRow(_columns);
		}
		return _triggerTable;
	}

	
	/**
	 * Creates and returns a trigger status cell for the trigger overview table.
	 *
	 * @see getTriggersOverview()
	 *
	 * @param array  _trigger	触发器
	 * @param string _pageFile		the page where the element is displayed
	 * @param string _screenId
	 * @param flag  	是否是报警信息  true是 false 
	 * @param hostid  设备ID
	 * @param applicationId 应用集Id
	 * @return CCol
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CCol getTriggerOverviewCells(IIdentityBean idBean, SQLExecutor executor, CArray _trigger, String _pageFile, String _screenId,boolean flag,Object hostid,Object appid,Object groupId) {
		Object _ack = null, _css = null, _style = null;
		CArray _desc = array();
	
		Map _config = select_config(idBean, executor);
		
		//设置触发器显示
		if (!empty(_trigger)) {
			_style = "cursor: pointer;";
	
			// problem trigger 触发器报警显示方式
			if (Nest.value(_trigger,"value").asInteger() == TRIGGER_VALUE_TRUE) {
				_css = getSeverityStyle(Nest.value(_trigger,"priority").asInteger());			//匹配报警级别的样式
				_ack = null;
	
			}
			// ok trigger 触发器正常显示方式
			else {
				_css = "normal";
			}
		}
	
		String action = "host_list.action?hostid="+hostid+"&application="+appid+"&groupid="+groupId;	//设置host(设备)链接地址
		CLink link = new CLink(null, action);
		//最终输出的列
		CCol _column = ((isArray(_desc) && count(_desc) > 0) || !empty(_ack))
			? new CCol(link, _css+" hosts")
			: new CCol(link, _css+" hosts");
			if(flag){
				_column.setAttribute("style", _style);
				
				if (!empty(_trigger) && Nest.value(_config,"blink_period").asInteger() > 0 && time() - Nest.value(_trigger,"lastchange").asInteger() < Nest.value(_config,"blink_period").asInteger()) {
					_column.addClass("blink");
					_column.setAttribute("data-toggle-class", _css);
				}
			}else{
				_css = "notrigger";
				_column = new CCol(link,_css+" hosts");
			}
			
	
		return _column;
	}
	
	/**
	 * 过滤监控数据 根据水桶原理 同一应用集中 记录报警程度最高的监控项的数据
	 * @param _triggers		//已被记录的集合
	 * @param app_name		//应用集名称
	 * @param _host_name	//设备名称
	 * @param priority      //优先级
	 * @return
	 */
	protected static boolean priorityFilter(CArray<Map> _triggers,Object app_name,Object _host_name,Object priority) {
		boolean result = false;
		if(_triggers.isEmpty()&& _triggers.size() == 0 || priority.equals(7)){		//第一条数据会被记录进去
			result = true;
			return result;
		}else{
			//根据应用集名称和设备名称判断是否存在相同数据
			CArray<Map> appMap = (CArray<Map>) _triggers.get(app_name);
			if(appMap != null){		//判断是否有此应用集
				Map _host =  appMap.get(_host_name);
				if(_host != null){	//判断是否有相同的设备
					Object pirty = _host.get("priority");
					Integer priorityNow = Integer.parseInt(priority.toString());
					Integer priorityOld = Integer.parseInt(pirty.toString());
					if(priorityNow > priorityOld){		
						result = true;
					}
				}else{
					result = true;
				}	
			}
		}
		return result;
	}
	
	private String getActionName(){
		ActionMapping actionMapping = ServletActionContext.getActionMapping();
		String actionName = actionMapping.getName();
		return actionName+"."+actionMapping.getExtension();
	}
}
