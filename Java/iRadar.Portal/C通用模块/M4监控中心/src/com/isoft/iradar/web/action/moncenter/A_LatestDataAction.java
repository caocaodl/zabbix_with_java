package com.isoft.iradar.web.action.moncenter;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.bcsub;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_HISTORY_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.UNKNOWN_VALUE;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_stristr;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.ItemsUtil.formatHistoryValue;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 监控列表页面-最新数据
 */
public abstract class A_LatestDataAction extends A_BasedDataAction {
	
	/**
	 * 初始化页面
	 */
	@Override
	protected void doInitPage() {
		page("title", _("Latest data"));
		page("file", getSimpleAction());
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] { "lessor/supervisecenter/others.css" });

		define("RDA_PAGE_DO_REFRESH", 1);
		define("SHOW_TRIGGERS", 1);
		define("SHOW_DATA", 0);
	}
	
	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR						TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"apps",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"application", 					array(T_RDA_STR, O_OPT, P_SYS, null,	   null),
			"groupid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"fullscreen",					array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"select",						array(T_RDA_STR, O_OPT, null,	null,		null),
			"show_without_data",	array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"show_details",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"filter_rst",					array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"filter_set",					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"favobj",						array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",						array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favstate",					array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"toggle_ids",					array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"toggle_open_state",		array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"tenantid", 					array(T_RDA_STR, O_OPT, P_SYS, null,	   null),
			"tenant", 					array(T_RDA_STR, O_OPT, P_SYS, null,	   null),
			"pageType", 					array(T_RDA_STR, O_OPT, P_SYS, null,	   null),
			"actionName", 					array(T_RDA_STR, O_OPT, P_SYS, null,	   null)
		);
		check_fields(getIdentityBean(), fields);
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
		if (hasRequest("favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").$())) {
				CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".filter.state", Nest.value(_REQUEST,"favstate").asInteger(), PROFILE_TYPE_INT);
			} else if ("toggle".equals(Nest.value(_REQUEST,"favobj").$())) {
				// Nest.value($_REQUEST,"toggle_ids").$() can be single id or list of ids,
				// where id xxxx is application id and id 0_xxxx is 0_ + host id
				if (!isArray(Nest.value(_REQUEST,"toggle_ids").$())) {
					if(!empty(Nest.value(_REQUEST,"toggle_ids").asString())){
						if ('_' == Nest.value(_REQUEST,"toggle_ids").asString().charAt(1)) {
							Long hostId = Nest.as(substr(Nest.value(_REQUEST,"toggle_ids").asString(), 2)).asLong();
							CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".toggle_other", Nest.value(_REQUEST,"toggle_open_state").asInteger(), PROFILE_TYPE_INT, hostId);
						} else {
							Long applicationId = Nest.value(_REQUEST,"toggle_ids").asLong();
							CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".toggle", Nest.value(_REQUEST,"toggle_open_state").asInteger(), PROFILE_TYPE_INT, applicationId);
						}
					}
				} else {
					String[] toggleIds = Nest.array(_REQUEST,"toggle_ids").asString();
					if (toggleIds != null && toggleIds.length > 0) {
						for (String toggleId : toggleIds) {
							if (toggleId.charAt(1) == '_') {
								Long hostId = Nest.as(substr(toggleId, 2)).asLong();
								CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".toggle_other", Nest.value(_REQUEST,"toggle_open_state").asInteger(), PROFILE_TYPE_INT, hostId);
							} else {
								Long applicationId = Nest.as(toggleId).asLong();
								CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".toggle", Nest.value(_REQUEST,"toggle_open_state").asInteger(), PROFILE_TYPE_INT, applicationId);
							}
						}
					}
				}
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}
	
	@Override
	public void doAction(SQLExecutor executor) {
		includeSubView("js/monitoring.latest.js");
		
		/* Filter */
		String filterSelect = get_request("select");
		int filterShowWithoutData = get_request("show_without_data", 0);
		int filterShowDetails = get_request("show_details", 0);
		
		if (hasRequest("filter_rst")) {
			filterSelect = "";
			filterShowWithoutData = 0;
			filterShowDetails = 0;
		}
		
		if (hasRequest("filter_set") || hasRequest("filter_rst")) {
			CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".filter.select", filterSelect, PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".filter.show_without_data", filterShowWithoutData, PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor, "web."+getLatestAction()+".filter.show_details", filterShowDetails, PROFILE_TYPE_INT);
		} else {
			filterSelect = (String)CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".filter.select", "");
			filterShowWithoutData = (Integer)CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".filter.show_without_data", 0);
			filterShowDetails = (Integer)CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".filter.show_details", 0);
		}
		
		String tenantid = get_request("tenantid","0");
		
		long hostGroupId = getHostGroupId();		
		boolean isCloudHost = IMonConsts.MON_VM.equals(hostGroupId);
		
		CArray pfparams = map(
			"groups", map("editable", true),
			"hosts", map("templated_hosts", true, "editable", false),
			"hostid", get_request("hostid", null),
			"groupid", hostGroupId,
			"application",get_request("application", null)
		);
		
		if (isCloudHost) {
			pfparams.put("tenants", map("withVms", true));
			pfparams.put("tenantid", get_request("tenantid", "0"));
		}

		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, pfparams);		
		
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		String sortField = getPageSortField(getIdentityBean(), executor);
		String sortOrder = getPageSortOrder(getIdentityBean(), executor);
		
		CArray<Map> applications = array();
		CArray<Map> items = array();

		/*列表页面添加应用集下拉框end*/
		
		// get hosts
		Long[] selectedHostIds = null;
		if (!empty(Nest.value(_REQUEST,"hostid").$())) {
			selectedHostIds = Nest.array(_REQUEST,"hostid").asLong();
		} else if(pageFilter.$("hostsSelected").asBoolean()){
			selectedHostIds = empty(pageFilter.$("hosts").asCArray().keysAsLong())?new Long[0]:new Long[]{pageFilter.$("hosts").asCArray().keysAsLong()[0]};
		} else {
			selectedHostIds = new Long[0];
		}
		
		if(!empty(selectedHostIds)){
			Map pfparamsCL = Clone.deepcopy(pfparams);
			Nest.value(pfparamsCL, "hostid").$(selectedHostIds[0]);
			pageFilter = new CPageFilter(getIdentityBean(), executor, pfparams);		
			Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").asString());
		}
		
		CHostGet options = new CHostGet();
		options.setOutput(new String[] { "name", "hostid", "status" });
		options.setGroupIds(hostGroupId);
		if (selectedHostIds.length > 0) {
			options.setHostIds(selectedHostIds);
		}
		options.setWithMonitoredItems(true);
		options.setPreserveKeys(true);
		if(tenantid != null && tenantid.length()>1){
			options.setFilter("tenantid",tenantid);
		}
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
		
		if (!empty(hosts)) {
			for (Map host : hosts) {
				Nest.value(host,"item_cnt").$(0);
			}
			if (count(hosts) > 1) {
				CArray sortFields = ("host".equals(sortField)) ? array(map("field", "name", "order", sortOrder)) : array("name");
				CArrayHelper.sort(hosts, sortFields);
			}
		}
		
		CTable paging = null;
		// get items(监控项)
		if (!empty(hosts)) {
			CItemGet itemGet = new CItemGet();
			itemGet.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
			itemGet.setOutput(new String[] {"itemid", "name", "type", "value_type", "units", "hostid", "state", "valuemapid", "status", 
					"error", "trends", "history", "delay", "key_", "flags"});
			itemGet.setSelectApplications(new String[] { "applicationid" });
			itemGet.setSelectItemDiscovery(new String[]{"ts_delete"});
			itemGet.setWebItems(true);
			itemGet.setFilter("status", String.valueOf(ITEM_STATUS_ACTIVE));
			itemGet.setPreserveKeys(true);
			items = API.Item(getIdentityBean(), executor).get(itemGet);
//			paging = getPagingLine(getIdentityBean(), executor, items);
			itemGet.setItemIds(rda_objectValues(items, "itemid").valuesAsLong());
			items = API.Item(getIdentityBean(), executor).get(itemGet);
		}
		
		CArray<CArray<Map>> history = null;
		if (!empty(items)) {
			// macros
			items = CMacrosResolverHelper.resolveItemKeys(getIdentityBean(), executor, items);
			items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);
			
			Iterator<Entry<Object, Map>> iterator_items = items.entrySet().iterator();
			// filter items by name
			while(iterator_items.hasNext()) {
				Entry<Object, Map> e = iterator_items.next();
				Map item = e.getValue();
				if (!rda_empty(filterSelect) && rda_stristr(Nest.value(item,"name_expanded").asString(), filterSelect)==null) {
					iterator_items.remove();
				}
			}
			
			if (!empty(items)) {
				// get history
				history = Manager.History(getIdentityBean(), executor).getLast(items, 2, RDA_HISTORY_PERIOD);
				// filter items without history
				if (empty(filterShowWithoutData)) {
					Iterator<Entry<Object, Map>> iterator = items.entrySet().iterator();
					while(iterator.hasNext()) {
						Entry<Object, Map> e = iterator.next();
						Map item = e.getValue();
						if (!isset(Nest.value(history, item.get("itemid")).$())) {
							iterator.remove();
						}
					}
				}
			}
			
			if (!empty(items)) {
				CArray hostIds = array_keys(array_flip(rda_objectValues(items, "hostid")));
				
				// add item last update date for sorting
				for (Entry<Object, Map> e : items.entrySet()) {
					Map item = e.getValue();
					if (history.containsKey(item.get("itemid"))) {
						Nest.value(item,"lastclock").$(Nest.value(history, item.get("itemid"), 0 ,"clock").$());
					}
				}
				
				// sort
				CArray sortFields = null;
				if ("name".equals(sortField)) {
					sortFields = array(map("field", "name_expanded", "order", sortOrder), "itemid");
				} else if ("lastclock".equals(sortField)) {
					sortFields = array(map("field", "lastclock", "order", sortOrder), "name_expanded", "itemid");
				} else {
					sortFields = array("name_expanded", "itemid");
				}
				CArrayHelper.sort(items, sortFields);
				
				// get applications
				CAppGet appGet = new CAppGet();
				appGet.setOutput(API_OUTPUT_EXTEND);
				appGet.setHostIds(hostIds.valuesAsLong());
				appGet.setPreserveKeys(true);
				applications = API.Application(getIdentityBean(), executor).get(appGet);

				if (!empty(applications)) {
					for(Map application:applications) {
						Nest.value(application,"hostname").$(Nest.value(hosts, application.get("hostid"),"name").asString());
						Nest.value(application,"item_cnt").$(0);
					}

					// by default order by application name and application id
					sortFields = ("host".equals(sortField)) ? array(map("field", "hostname", "order", sortOrder)) : array();
					array_push(sortFields, "name", "applicationid");
					CArrayHelper.sort(applications, sortFields);
				}

				if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
					// get templates screen count
					CTemplateScreenGet tsoptions = new CTemplateScreenGet();
					tsoptions.setHostIds(hostIds.valuesAsLong());
					tsoptions.setCountOutput(true);
					tsoptions.setGroupCount(true);
					CArray<Map> screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);

					for(Map screen: screens) {
						hosts.put(screen.get("hostid"), "screens", Nest.value(screen,"rowscount").$());
					}
				}
			}
		}
		
		Map config = null;
		if (!empty(filterShowDetails)) {
			config = select_config(getIdentityBean(), executor);
		}	
		
		/* Display */
		CWidget latestWidget = new CWidget(null, "latest-mon");
		
		CForm headerForm = new CForm("post", getLatestAction());
		headerForm.addVar("groupid", pageFilter.$("groupid").$());	//在页面from中添加groupid记录
		if (isCloudHost) {
			headerForm.addItem(array(SPACE + _("Tenant") + SPACE, pageFilter.getTenantsCB()));
		}
//		if(showGroupFilter()){
//			headerForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
//		}
		CComboBox hostsCB = pageFilter.getHostsCB();
		hostsCB.items.remove(0);
		headerForm.addItem(array(SPACE+_("Host")+SPACE, hostsCB));
		
		CForm btnForm = null;
		if(getSimpleAction()!=null){
			btnForm = new CForm("post", getSimpleAction());
			btnForm.addItem(new CSubmit("pageButton", SPACE, null, "details_pic details_pic"));
			btnForm.addVar("groupid", pageFilter.$("groupid").asString());
			btnForm.addVar("hostid", pageFilter.$("hostid").asString());
			if (isCloudHost) {
				btnForm.addVar("tenantid", pageFilter.$("tenantid").asString());
			}
		}
		
		latestWidget.addHeader(headerForm, btnForm);
		
		//表单form(设置表单的action)
		CFormTable filterForm = new CFormTable(null, getLatestAction(), "post");
		filterForm.addVar("groupid", pageFilter.$("groupid").asString());
		filterForm.addVar("hostid", pageFilter.$("hostid").asString());
		if (isCloudHost) {
			filterForm.addVar("tenantid", pageFilter.$("tenantid").asString());
		}
		filterForm.setAttribute("name"," rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addRow(_("Show items with name like"), new CTextBox("select", filterSelect, 20));
		filterForm.addRow(_("Show items without data"), new CCheckBox("show_without_data", filterShowWithoutData>0, null, "1"));
		filterForm.addRow(_("Show details"), new CCheckBox("show_details", !empty(filterShowDetails), null, "1"));
		filterForm.addItemToBottomRow(new CSubmit("filter_set", _("GoFilter")));
		filterForm.addItemToBottomRow(new CButton("filter_rst", _("Reset"),
			"javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1);uri.setArgument(\"groupid\","+pageFilter.$("groupid").asString()+");uri.setArgument(\"hostid\","+pageFilter.$("hostid").asString()+"); location.href = uri.getUrl();","darkgray"));
		
		latestWidget.addFlicker(filterForm, (Integer)CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".filter.state", 1));
		
		// table
		CTableInfo table = new CTableInfo(_("No values found."));
		
		CCol hostHeader = null;
		String hostColumn = null;
		if (!empty(get_request("hostid"))) {
			hostHeader  = null;
			hostColumn  = null;
		} else {
			hostHeader = make_sorting_header(_("Host"), "host");
			hostHeader.addClass("latest-host "+(!empty(filterShowDetails) ? "with-details" : "no-details"));
			hostHeader.setAttribute("title", _("Host"));
			hostColumn = SPACE;
		}
		
		CCol nameHeader = make_sorting_header(_("Name"), "name");
		nameHeader.setAttribute("title", _("Name"));

		CCol lastCheckHeader = make_sorting_header(_("Last check"), "lastclock");
		lastCheckHeader.addClass("latest-lastcheck");
		lastCheckHeader.setAttribute("title", _("Last check"));

		CCol lastValueHeader = new CCol(new CSpan(_("Last value")), "latest-lastvalue");
		lastValueHeader.setAttribute("title", _("Last value"));

		CCol lastDataHeader = new CCol(new CSpan(_x("Change", "noun in latest data")), "latest-data");
		lastDataHeader.setAttribute("title", _x("Change", "noun in latest data"));
		
		if (!empty(filterShowDetails)) {
			CCol intervalHeader = new CCol(new CSpan(_("Interval")), "latest-interval");
			intervalHeader.setAttribute("title", _("Interval"));

			CCol historyHeader = new CCol(new CSpan(_("History")), "latest-history");
			historyHeader.setAttribute("title", _("History"));

			CCol trendsHeader = new CCol(new CSpan(_("Trends")), "latest-trends");
			trendsHeader.setAttribute("title", _("Trends"));

			CCol typeHeader = new CCol(new CSpan(_("Type")), "latest-type");
			typeHeader.setAttribute("title", _("Type"));

			CCol infoHeader = new CCol(null, "latest-info");
			infoHeader.setAttribute("title", _("Info"));

			if(!empty(get_request("hostid"))){				
				table.addClass("latest-details");
			}else{
				table.addClass("detailall");
			}
			table.setHeader(array(
				new CCol(new CDiv(null, "app-list-toggle-all icon-plus-9x9")),
				hostHeader,
				nameHeader,
				intervalHeader,
				historyHeader,
				trendsHeader,
				typeHeader,
				lastCheckHeader,
				lastValueHeader,
				lastDataHeader,
				new CCol(SPACE, "latest-actions"),
				infoHeader
			));
		} else {
			if(!empty(get_request("hostid"))){				
				table.addClass("latest-normal");
			}else{
				table.addClass("normaldisplay");
			}
			table.setHeader(array(
				new CCol(new CDiv(null, "app-list-toggle-all icon-plus-9x9")),
				hostHeader,
				nameHeader,
				lastCheckHeader,
				lastValueHeader,
				lastDataHeader,
				new CCol(SPACE, "latest-actions")
			));
		}
		
		CArray<CArray<CRow>> tab_rows = array();
		List withoutList = new ArrayList();
		for (Entry<Object, Map> e : items.entrySet()) {
			Object key = e.getKey();
			Map item = e.getValue();
			
			if (empty(Nest.value(item,"applications").$())) {
				continue;
			}
			
			Map lastHistory = isset(Nest.value(history, item.get("itemid"), 0).$()) ? Nest.value(history, item.get("itemid"), 0).asCArray() : null;
			Map prevHistory = isset(Nest.value(history, item.get("itemid"), 1).$()) ? Nest.value(history, item.get("itemid"), 1).asCArray() : null;
			
			if (strpos(Nest.value(item,"units").asString(), ",") >-1) {
				String[] segments = explode(",", Nest.value(item,"units").asString());
				Nest.value(item,"units").$(segments[0]);
				Nest.value(item,"unitsLong").$(segments[1]);
			} else {
				Nest.value(item,"unitsLong").$("");
			}
			
			// last check time and last value
			String lastClock = null;
			String lastValue = null;
			if (lastHistory!=null) {
				lastClock  = rda_date2str(_("d M Y H:i:s"), Nest.value(lastHistory,"clock").asLong());
				lastValue  = formatHistoryValue(getIdentityBean(), executor, Nest.value(lastHistory,"value").asString(), item, false);
			} else {
				lastClock = UNKNOWN_VALUE;
				lastValue = UNKNOWN_VALUE;
			}
			
			// change
			int digits = (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT) ? 2 : 0;
			String change = null;
			if (lastHistory!=null && prevHistory!=null
					&& (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64)
					&& (bcsub(Nest.value(lastHistory,"value").asDouble(), Nest.value(prevHistory,"value").asDouble(), digits) != 0)) {

				change  = "";
				if ((Nest.value(lastHistory,"value").asDouble() - Nest.value(prevHistory,"value").asDouble()) > 0) {
					change = "+";
				}

				// for "unixtime" change should be calculated as uptime
				change += convert_units(map(
					"value", bcsub(Nest.value(lastHistory,"value").asDouble(), Nest.value(prevHistory,"value").asDouble(), digits),
					"units", "unixtime".equals(Nest.value(item,"units").$()) ? "uptime" : Nest.value(item,"units").$()
				));
				change = nbsp(change);
			} else {
				change = UNKNOWN_VALUE;
			}
			
			CLink actions = new CLink("");
			if (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64) {
				actions  = new CLink(_("Graph"), "history.action?action=showgraph&itemid="+Nest.value(item,"itemid").$());
			}/* else {
				actions = new CLink(_("History"), "history.action?action=showvalues&itemid="+Nest.value(item,"itemid").$());//历史记录连接隐藏
			}*/
			
			String stateCss = (Nest.value(item,"state").asInteger() == ITEM_STATE_NOTSUPPORTED) ? "unknown" : "";
			
			CRow row = null;
			if (!empty(filterShowDetails)) {
				CTag itemKey = (Nest.value(item,"type").asInteger() == ITEM_TYPE_HTTPTEST || Nest.value(item,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED)
					? new CSpan(Nest.value(item,"key_expanded").$(), "enabled")
					: new CLink(Nest.value(item,"key_expanded").$(), "items.action?form=update&itemid="+Nest.value(item,"itemid").$(), "enabled");

				CArray statusIcons = array();
				if (Nest.value(item,"status").asInteger() == ITEM_STATUS_ACTIVE) {
					CDiv error = null;
					if (rda_empty(Nest.value(item,"error").$())) {
						error  = new CDiv(SPACE, "status_icon iconok");
					} else {
						error = new CDiv(SPACE, "status_icon iconerror");
						error.setHint(Nest.value(item,"error").$(), "", "on");
					}
					statusIcons.add(error);
				}

				Object trendValue = null;
				if (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64) {
					trendValue  = Nest.value(config,"hk_trends_global").asBoolean() ? Nest.value(config,"hk_trends").$() : Nest.value(item,"trends").$();
				} else {
					trendValue = UNKNOWN_VALUE;
				}

				row  = new CRow(array(
					SPACE,
					hostColumn,
					new CCol(new CDiv(array(new CSpan(Nest.value(item,"name_expanded").$()), BR(), itemKey), stateCss+" item")),
					new CCol(new CSpan(
						(Nest.value(item,"type").asInteger() == ITEM_TYPE_SNMPTRAP || Nest.value(item,"type").asInteger() == ITEM_TYPE_TRAPPER)
							? UNKNOWN_VALUE
							: Nest.value(item,"delay").$(),
						stateCss
					)),
					new CCol(new CSpan(Nest.value(config,"hk_history_global").asBoolean() ? Nest.value(config,"hk_history").$() : Nest.value(item,"history").$(), stateCss)),
					new CCol(new CSpan(trendValue, stateCss)),
					new CCol(new CSpan(item_type2str(Nest.value(item,"type").asInteger()), stateCss)),
					new CCol(new CSpan(lastClock, stateCss)),
					new CCol(new CSpan(lastValue, stateCss)),
					new CCol(new CSpan(change, stateCss)),
					actions,
					statusIcons
				));
			} else {
				row = new CRow(array(
					SPACE,
					hostColumn,
					new CCol(new CSpan(Nest.value(item,"name_expanded").$(), stateCss+" item")),
					new CCol(new CSpan(lastClock, stateCss)),
					new CCol(new CSpan(lastValue, stateCss)),
					new CCol(new CSpan(change, stateCss)),
					actions
				));
			}
			
			// add the item row to each application tab
			CArray<Map> itemApplications = Nest.value(item,"applications").asCArray();
			for(Map itemApplication:itemApplications) {
				Object applicationId = Nest.value(itemApplication,"applicationid").$();
				Nest.value(applications, applicationId,"item_cnt").$(Nest.value(applications, applicationId,"item_cnt").asInteger()+1);
				// objects may have different properties, so it's better to use a copy of it
				if(!tab_rows.containsKey(applicationId)){
					tab_rows.put(applicationId,array());
				}
				tab_rows.get(applicationId).add(row);
			}
			withoutList.add(key);
		}
		
		// remove items with applications from the collection
		for(Object key:withoutList){
			items.remove(key);
		}
		
		for (Entry<Object, Map> e : applications.entrySet()) {
			Object appid = e.getKey();
			Map dbApp = e.getValue();
			
			Map host = hosts.get(dbApp.get("hostid"));
			
			if (!isset(tab_rows, appid)){
				continue;
			}
			
			CArray<CRow> appRows = tab_rows.get(appid);
			
			Object openState = CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".toggle", null, Nest.value(dbApp,"applicationid").asLong());
//			Object openState = 1;
			
			CDiv toggle = new CDiv(SPACE, "app-list-toggle icon-plus-9x9");
			if (!empty(openState)) {
				toggle.addClass("icon-minus-9x9");
			}
			toggle.setAttribute("data-app-id", Nest.value(dbApp,"applicationid").$());
			toggle.setAttribute("data-open-state", openState);
			
			CSpan hostName = null;
			
			if (Nest.value(_REQUEST,"hostid").asLong() == 0L) {
				hostName = new CSpan(Nest.value(host,"name").$());
			}
			
			CArray templateids = Nest.value(dbApp, "templateids").asCArray();
			CAppGet option = new CAppGet();
			option.setApplicationIds(templateids.valuesAsLong());
			option.setOutput(new String[]{"applicationid"});
			option.setSelectHosts(new String[]{"name"});
			CArray<Map> appsByTemplateIds = API.Application(getIdBean(), executor).get(option);
			String templateName = "";
			for(Map apps:appsByTemplateIds){
				CArray<Map> templates = Nest.value(apps, "hosts").asCArray();
				for(Map template:templates)
					templateName = templateName.concat(Nest.value(template, "name").asString().concat("/"));
			}
			if(empty(templateName)){
				templateName = templateName.concat(Nest.value(host,"name").asString()).concat("/");
			}	
			// add toggle row
			table.addRow(array(
				toggle,
				hostName,
				new CCol(array(
						bold(templateName.concat(":").concat(Nest.value(dbApp,"name").asString())),
						SPACE+"("+_n("%1$s Item", "%1$s Items", Nest.value(dbApp,"item_cnt").asInteger())+")"
					), null, !empty(filterShowDetails) ? "10" : "5")
			), "odd_row");
			
			// add toggle sub rows
			for (CRow row : appRows) {
				row.setAttribute("parent_app_id", Nest.value(dbApp,"applicationid").$());
				row.addClass("odd_row");
				if (empty(openState)) {
					row.addClass("hidden");
				}
				table.addRow(row);
			}
		}
		
		tab_rows = array();
		for (Map item : items) {
			Map lastHistory = isset(Nest.value(history, item.get("itemid"), 0).$()) ? Nest.value(history, item.get("itemid"), 0).asCArray() : null;
			Map prevHistory = isset(Nest.value(history, item.get("itemid"), 1).$()) ? Nest.value(history, item.get("itemid"), 1).asCArray() : null;
			
			if (strpos(Nest.value(item,"units").asString(), ",") >-1) {
				String[] segments = explode(",", Nest.value(item,"units").asString());
				Nest.value(item,"units").$(segments[0]);
				Nest.value(item,"unitsLong").$(segments[1]);
			} else {
				Nest.value(item,"unitsLong").$("");
			}
			
			// last check time and last value
			String lastClock = null;
			String lastValue = null;
			if (lastHistory!=null) {
				lastClock  = rda_date2str(_("d M Y H:i:s"), Nest.value(lastHistory,"clock").asLong());
				lastValue  = formatHistoryValue(getIdentityBean(), executor, Nest.value(lastHistory,"value").asString(), item, false);
			} else {
				lastClock = UNKNOWN_VALUE;
				lastValue = UNKNOWN_VALUE;
			}
			
			// change
			int digits = (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT) ? 2 : 0;
			String change = null;
			if (isset(Nest.value(lastHistory,"value").$()) && isset(Nest.value(prevHistory,"value").$())
					&& (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64)
					&& (bcsub(Nest.value(lastHistory,"value").asDouble(), Nest.value(prevHistory,"value").asDouble(), digits) != 0)) {

				change  = "";
				if ((Nest.value(lastHistory,"value").asDouble() - Nest.value(prevHistory,"value").asDouble()) > 0) {
					change = "+";
				}

				// for "unixtime" change should be calculated as uptime
				change += convert_units(map(
					"value", bcsub(Nest.value(lastHistory,"value").asDouble(), Nest.value(prevHistory,"value").asDouble(), digits),
					"units", "unixtime".equals(Nest.value(item,"units").$()) ? "uptime" : Nest.value(item,"units").$()
				));
				change = nbsp(change);
			} else {
				change = " - ";
			}
			
			CLink actions = new CLink("");
			if ((Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT) || (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64)) {
				actions = new CLink(_("Graph"), "history.action?action=showgraph&itemid="+Nest.value(item,"itemid").$());
			}/* else{
				actions = new CLink(_("History"), "history.action?action=showvalues&itemid="+Nest.value(item,"itemid").$());
			}*/
			
			String stateCss = (Nest.value(item,"state").asInteger() == ITEM_STATE_NOTSUPPORTED) ? "unknown" : "";
			
			CRow row = null;
			if (!empty(filterShowDetails)) {
				CTag itemKey = (Nest.value(item,"type").asInteger() == ITEM_TYPE_HTTPTEST || Nest.value(item,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED)
					? new CSpan(Nest.value(item,"key_expanded").$(), "enabled")
					: new CLink(Nest.value(item,"key_expanded").$(), "items.action?form=update&itemid="+Nest.value(item,"itemid").$(), "enabled");

				CArray statusIcons = array();
				if (Nest.value(item,"status").asInteger() == ITEM_STATUS_ACTIVE) {
					CDiv error = null;
					if (rda_empty(Nest.value(item,"error").$())) {
						error  = new CDiv(SPACE, "status_icon iconok");
					} else {
						error = new CDiv(SPACE, "status_icon iconerror");
						error.setHint(Nest.value(item,"error").$(), "", "on");
					}
					statusIcons.add(error);
				}

				Object trendValue = null;
				if (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64) {
					trendValue  = Nest.value(config,"hk_trends_global").asBoolean() ? Nest.value(config,"hk_trends").$() : Nest.value(item,"trends").$();
				} else {
					trendValue = UNKNOWN_VALUE;
				}

				row = new CRow(array(
					SPACE,
					hostColumn,
					new CCol(new CDiv(array(new CSpan(Nest.value(item,"name_expanded").$()), BR(), itemKey), stateCss+" item")),
					new CCol(new CSpan(
						(Nest.value(item,"type").asInteger() == ITEM_TYPE_SNMPTRAP || Nest.value(item,"type").asInteger() == ITEM_TYPE_TRAPPER)
							? UNKNOWN_VALUE
							: Nest.value(item,"delay").$(),
						stateCss
					)),
					new CCol(new CSpan(Nest.value(config,"hk_history_global").asBoolean() ? Nest.value(config,"hk_history").$() : Nest.value(item,"history").$(), stateCss)),
					new CCol(new CSpan(trendValue, stateCss)),
					new CCol(new CSpan(item_type2str(Nest.value(item,"type").asInteger()), stateCss)),
					new CCol(new CSpan(lastClock, stateCss)),
					new CCol(new CSpan(lastValue, stateCss)),
					new CCol(new CSpan(change, stateCss)),
					actions,
					statusIcons
				));
			} else {
				row = new CRow(array(
					SPACE,
					hostColumn,
					new CCol(new CSpan(Nest.value(item,"name_expanded").$(), stateCss+" item")),
					new CCol(new CSpan(lastClock, stateCss)),
					new CCol(new CSpan(lastValue, stateCss)),
					new CCol(new CSpan(change, stateCss)),
					actions
				));
			}
			
			Nest.value(item, item.get("hostid"),"item_cnt").$(Nest.value(item, item.get("hostid"),"item_cnt").asInteger()+1);
			if(!tab_rows.containsKey(item.get("hostid"))){
				tab_rows.put(item.get("hostid"),array());
			}
			tab_rows.get(item.get("hostid")).add(row);
		}
		
		for (Entry<Object, Map> e : hosts.entrySet()) {
			Object hostId = e.getKey();
			Map dbHost = e.getValue();
			
			Map host = hosts.get(dbHost.get("hostid"));
			
			if(!isset(tab_rows,hostId)) {
				continue;
			}
			CArray<CRow> appRows = tab_rows.get(hostId);
			
			Object openState = CProfile.get(getIdentityBean(), executor, "web."+getLatestAction()+".toggle_other", null, Nest.value(host,"hostid").asLong());
//			Object openState = 1;
			
			CDiv toggle = new CDiv(SPACE, "app-list-toggle icon-plus-9x9");
			if (!empty(openState)) {
				toggle.addClass("icon-minus-9x9");
			}
			toggle.setAttribute("data-app-id", "0_"+Nest.value(host,"hostid").$());
			toggle.setAttribute("data-open-state", openState);
			
			CSpan hostName = null;
			if (Nest.value(_REQUEST,"hostid").asLong() == 0L) {
				hostName = new CSpan(Nest.value(host,"name").$());
			}
			
			// add toggle row
			table.addRow(array(
				new CCol(toggle),
				hostName,
				new CCol(
					array(
						bold("- "+("other")+" -"),
//						SPACE+"("+_n("%1$s Item", "%1$s Items", Nest.value(dbHost,"item_cnt").$())+")"
						SPACE+"("+_n("%1$s Item", "%1$s Items", appRows.size())+")"
					),
					null, !empty(filterShowDetails) ? "10" : "5"
				)
			), "odd_row");
			
			// add toggle sub rows
			for(CRow row:appRows) {
				row.setAttribute("parent_app_id", "0_"+Nest.value(host,"hostid").$());
				row.addClass("odd_row");
				if (empty(openState)) {
					row.addClass("hidden");
				}
				table.addRow(row);
			}
		}
		
		latestWidget.addItem(array(table,empty(applications)?null:paging));
		latestWidget.show();
	}
}
