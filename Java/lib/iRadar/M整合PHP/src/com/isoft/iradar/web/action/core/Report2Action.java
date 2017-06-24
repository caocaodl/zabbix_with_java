package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AVAILABILITY_REPORT_BY_HOST;
import static com.isoft.iradar.inc.Defines.AVAILABILITY_REPORT_BY_TEMPLATE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.P_UNSET_EMPTY;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_swap;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.calculate_availability;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Report2Action extends RadarBaseAction {
	
	private int availabilityReportMode;
	private Map config;	
	
	@Override
	protected void doInitPage() {
		page("title", _("Availability report"));
		page("file", "report2.action");
		page("hist_arg", new String[] { "mode", "groupid", "hostid", "tpl_triggerid" });
		page("scripts", new String[] { "class.calendar.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"mode",				array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"),		null),
			"hostgroupid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,			null),
			"tpl_triggerid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,			null),
			"triggerid",			array(T_RDA_INT, O_OPT, P_SYS|P_NZERO, DB_ID,	null),
			// filter
			"filter_groupid",    array(T_RDA_INT, O_OPT, P_SYS, DB_ID,			null),
			"filter_hostid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,			null),
			"filter_rst",			array(T_RDA_INT, O_OPT, P_SYS, IN(array(0, 1)),	null),
			"filter_set",			array(T_RDA_STR, O_OPT, P_SYS, null,			null),
			"filter_timesince",	array(T_RDA_STR, O_OPT, P_UNSET_EMPTY, null,	null),
			"filter_timetill",	    array(T_RDA_STR, O_OPT, P_UNSET_EMPTY, null,	null),
			// ajax
			"favobj",				array(T_RDA_STR, O_OPT, P_ACT, null,			null),
			"favref",				array(T_RDA_STR, O_OPT, P_ACT, NOT_EMPTY,		"isset({favobj})"),
			"favstate",			array(T_RDA_INT, O_OPT, P_ACT, NOT_EMPTY,		"isset({favobj})&&\"filter\"=={favobj}")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		availabilityReportMode = get_request("mode", Nest.as(CProfile.get(getIdentityBean(), executor, "web.avail_report.mode", AVAILABILITY_REPORT_BY_HOST)).asInteger());
		CProfile.update(getIdentityBean(), executor, "web.avail_report.mode", Nest.as(availabilityReportMode).asLong(), PROFILE_TYPE_INT);
		
		if (availabilityReportMode == AVAILABILITY_REPORT_BY_TEMPLATE) {
			if (!empty(get_request("hostgroupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostgroupid").asLong())
					|| !empty(get_request("filter_groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"filter_groupid").asLong())
					|| !empty(get_request("filter_hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"filter_hostid").asLong())) {
				access_deny();
			}
			if (!empty(get_request("tpl_triggerid"))) {
				CTriggerGet toptions = new CTriggerGet();
				toptions.setTriggerIds(Nest.value(_REQUEST,"tpl_triggerid").asLong());
				toptions.setOutput(new String[] {"triggerid"});
				toptions.setFilter("flags");
				CArray<Map> trigger = API.Trigger(getIdentityBean(), executor).get(toptions);
				if (empty(trigger)) {
					access_deny();
				}
			}
		} else {
			if (!empty(get_request("filter_groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"filter_groupid").asLong())
					|| !empty(get_request("filter_hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"filter_hostid").asLong())) {
				access_deny();
			}
		}
		if (!empty(get_request("triggerid")) && !API.Trigger(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"triggerid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj") && "filter".equals(Nest.value(_REQUEST,"favobj").$())) {
			CProfile.update(getIdentityBean(), executor, "web.avail_report.filter.state", Nest.value(_REQUEST,"favstate").asInteger(), PROFILE_TYPE_INT);
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
		if (isset(_REQUEST,"filter_rst")) {
			Nest.value(_REQUEST,"filter_groupid").$(0);
			Nest.value(_REQUEST,"filter_hostid").$(0);
			Nest.value(_REQUEST,"filter_timesince").$(0);
			Nest.value(_REQUEST,"filter_timetill").$(0);
		}
		
		config = select_config(getIdentityBean(), executor);
		
		if (Nest.value(config,"dropdown_first_remember").asBoolean()) {
			if (!isset(_REQUEST,"filter_rst")) {
				Nest.value(_REQUEST,"filter_groupid").$(get_request("filter_groupid", CProfile.get(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".groupid", 0)));
				Nest.value(_REQUEST,"filter_hostid").$(get_request("filter_hostid", CProfile.get(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".hostid", 0)));
				Nest.value(_REQUEST,"filter_timesince").$(get_request("filter_timesince", CProfile.get(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".timesince", 0)));
				Nest.value(_REQUEST,"filter_timetill").$(get_request("filter_timetill", CProfile.get(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".timetill", 0)));
			}
			CProfile.update(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".groupid", Nest.value(_REQUEST,"filter_groupid").asLong(), PROFILE_TYPE_ID);
			CProfile.update(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".timesince", Nest.value(_REQUEST,"filter_timesince").asString(), PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".timetill", Nest.value(_REQUEST,"filter_timetill").asString(), PROFILE_TYPE_STR);
		} else if (!isset(_REQUEST,"filter_rst")) {
			Nest.value(_REQUEST,"filter_groupid").$(get_request("filter_groupid", 0));
			Nest.value(_REQUEST,"filter_hostid").$(get_request("filter_hostid", 0));
			Nest.value(_REQUEST,"filter_timesince").$(get_request("filter_timesince", 0));
			Nest.value(_REQUEST,"filter_timetill").$(get_request("filter_timetill", 0));
		}
		
		CProfile.update(getIdentityBean(), executor, "web.avail_report."+availabilityReportMode+".hostid", Nest.value(_REQUEST,"filter_hostid").asLong(), PROFILE_TYPE_ID);
		
		if (Nest.value(_REQUEST,"filter_timetill").asLong() > 0 && Nest.value(_REQUEST,"filter_timesince").asLong() > Nest.value(_REQUEST,"filter_timetill").asLong()) {
			rda_swap(Nest.value(_REQUEST,"filter_timesince"), Nest.value(_REQUEST,"filter_timetill"));
		}
		
		Nest.value(_REQUEST,"filter_timesince").$(rdaDateToTime( !empty(_REQUEST.get("filter_timesince"))
				? Nest.value(_REQUEST,"filter_timesince").asString() : date(TIMESTAMP_FORMAT_ZERO_TIME, time() - SEC_PER_DAY)));
		Nest.value(_REQUEST,"filter_timetill").$(rdaDateToTime( !empty(_REQUEST.get("filter_timetill"))
				? Nest.value(_REQUEST,"filter_timetill").asString() : date(TIMESTAMP_FORMAT_ZERO_TIME, time())));

	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Header */
		CArray<Map> triggerDatas = null;
		
		if(isset(_REQUEST,"triggerid")){
			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
			toptions.setOutput(API_OUTPUT_EXTEND);
			toptions.setSelectHosts(API_OUTPUT_EXTEND);
			toptions.setExpandDescription(true);
			triggerDatas = API.Trigger(getIdentityBean(), executor).get(toptions);
		}
			
		CWidget reportWidget = new CWidget();
		reportWidget.addPageHeader(_("AVAILABILITY REPORT"));
		
		if (!empty(triggerDatas)) {
			Map triggerData = reset(triggerDatas);
			Map host = reset(Nest.value(triggerData,"hosts").asCArray());
		
			Nest.value(triggerData,"hostid").$(Nest.value(host,"hostid").$());
			Nest.value(triggerData,"hostname").$(Nest.value(host,"name").$());
		
			reportWidget.addHeader(array(
				new CLink(Nest.value(triggerData,"hostname").$(), "?filter_groupid="+Nest.value(_REQUEST,"filter_groupid").$()),
				NAME_DELIMITER,
				triggerData.get("description")
			), SPACE);
		
			CTableInfo table = new CTableInfo(null, "graph");
			table.addRow(new CImg("chart4.action?triggerid="+Nest.value(_REQUEST,"triggerid").$()));
		
			reportWidget.addItem(BR());
			reportWidget.addItem(table);
			reportWidget.show();
		} else if (isset(_REQUEST,"filter_hostid")) {
			displayById(executor, reportWidget);
		}
	}

	private void displayById(SQLExecutor executor, CWidget reportWidget) {
		CComboBox modeComboBox = new CComboBox("mode", availabilityReportMode, "submit()");
		modeComboBox.addItem(AVAILABILITY_REPORT_BY_HOST, _("By host"));
		modeComboBox.addItem(AVAILABILITY_REPORT_BY_TEMPLATE, _("By trigger template"));
	
		CForm headerForm = new CForm("get");
		headerForm.addItem(modeComboBox);
	
		reportWidget.addHeader(_("Report"), array(_("Mode")+SPACE, headerForm));
	
		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setOutput(new String[]{"triggerid", "description", "expression", "value"});
		triggerOptions.setExpandDescription(true);
		triggerOptions.setExpandData(true);
		triggerOptions.setMonitored(true);
		triggerOptions.setSelectHosts(API_OUTPUT_EXTEND);
		triggerOptions.setFilter((Map)map());
	
		/* Filter */
		CFormTable filterForm = new CFormTable();
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addVar("config", availabilityReportMode);
		filterForm.addVar("filter_timesince", date(TIMESTAMP_FORMAT, Nest.value(_REQUEST,"filter_timesince").asLong()));
		filterForm.addVar("filter_timetill", date(TIMESTAMP_FORMAT, Nest.value(_REQUEST,"filter_timetill").asLong()));
	
		// report by template
		if (availabilityReportMode == AVAILABILITY_REPORT_BY_TEMPLATE) {
			reportByTemplate(executor, filterForm, triggerOptions);
		} else if (availabilityReportMode == AVAILABILITY_REPORT_BY_HOST) {// report by host
			reportByHost(executor, filterForm, triggerOptions);
		}
	
		// filter period
		CArray timeSinceRow = createDateSelector("filter_timesince", Nest.value(_REQUEST,"filter_timesince").asLong(), "filter_timetill");
		array_unshift(timeSinceRow, _("From"));
		CArray timeTillRow = createDateSelector("filter_timetill", Nest.value(_REQUEST,"filter_timetill").asLong(), "filter_timesince");
		array_unshift(timeTillRow, _("Till"));
	
		CTable filterPeriodTable = new CTable(null, "calendar");
		filterPeriodTable.addRow(timeSinceRow);
		filterPeriodTable.addRow(timeTillRow);
	
		filterForm.addRow(_("Period"), filterPeriodTable);
	
		// filter buttons
		filterForm.addItemToBottomRow(new CSubmit("filter_set",_("GoFilter")));
		filterForm.addItemToBottomRow(new CButton("filter_rst", _("Reset"),
			"javascript: var url = new Curl(location.href); url.setArgument(\"filter_rst\", 1); location.href = url.getUrl();"));
	
		reportWidget.addFlicker(filterForm, Nest.as(CProfile.get(getIdentityBean(), executor, "web.avail_report.filter.state", 0)).asInteger());
	
		/* Triggers */
		CTableInfo triggerTable = new CTableInfo(_("No triggers found."));
		triggerTable.setHeader(array( 
			(Nest.value(_REQUEST,"filter_hostid").asInteger() == 0 || availabilityReportMode == AVAILABILITY_REPORT_BY_TEMPLATE) ? _("Host") : null,
			_("Name"),
			_("Problems"),
			_("Ok"),
			_("Graph")
		));
	
		CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(triggerOptions);
		CArrayHelper.sort(triggers, array("host", "description"));
	
		for(Map trigger: triggers) {
			CArray availability = calculate_availability(getIdentityBean(), executor, Nest.value(trigger,"triggerid").asString(), Nest.value(_REQUEST,"filter_timesince").asLong(), Nest.value(_REQUEST,"filter_timetill").asLong());
	
			triggerTable.addRow(array(
				(Nest.value(_REQUEST,"filter_hostid").asInteger() == 0 || availabilityReportMode == AVAILABILITY_REPORT_BY_TEMPLATE)
					? Nest.value(trigger,"hosts", 0, "name").$() : null,
				new CLink(Nest.value(trigger,"description").$(), "events.action?triggerid="+trigger.get("triggerid")+
					"&source="+EVENT_SOURCE_TRIGGERS
				),
				new CSpan(sprintf("%.4f%%", Nest.value(availability,"true").asDouble()), "on"),
				new CSpan(sprintf("%.4f%%", Nest.value(availability,"false").asDouble()), "off"),
				new CLink(_("Show"), "report2.action?filter_groupid="+_REQUEST.get("filter_groupid")+
					"&filter_hostid="+_REQUEST.get("filter_hostid")+"&triggerid="+Nest.value(trigger,"triggerid").$())
			));
		}
	
		reportWidget.addItem(BR());
		reportWidget.addItem(triggerTable);
		reportWidget.show();
	}

	private void reportByTemplate(SQLExecutor executor, CFormTable filterForm, CTriggerGet triggerOptions) {
		// trigger options
		if (!empty(Nest.value(_REQUEST,"filter_hostid").$()) || !Nest.value(config,"dropdown_first_entry").asBoolean()) {
			CHostGet hoptions = new CHostGet();
			hoptions.setTemplateIds(Nest.value(_REQUEST,"filter_hostid").asLong());
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			triggerOptions.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
		}
		if (isset(_REQUEST,"tpl_triggerid") && !empty(Nest.value(_REQUEST,"tpl_triggerid").$())) {
			triggerOptions.setFilter("templateid", Nest.value(_REQUEST,"tpl_triggerid").asString());
		}
		if (isset(_REQUEST,"hostgroupid") && !empty(Nest.value(_REQUEST,"hostgroupid").$())) {
			triggerOptions.setGroupIds(Nest.value(_REQUEST,"hostgroupid").asLong());
		}

		// filter template group
		CComboBox groupsComboBox = new CComboBox("filter_groupid", Nest.value(_REQUEST,"filter_groupid").$(), "javascript: submit();");
		groupsComboBox.addItem(0, _("all"));

		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setTemplatedHosts(true);
		hgoptions.setWithTriggers(true);
		CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		order_result(groups, "name");

		for(Map group: groups) {
			groupsComboBox.addItem(
				Nest.value(group,"groupid").$(),
				Nest.value(group,"name").asString()
			);
		}
		filterForm.addRow(_("Template group"), groupsComboBox);

		// filter template
		CComboBox templateComboBox = new CComboBox("filter_hostid", Nest.value(_REQUEST,"filter_hostid").$(), "javascript: submit();");
		templateComboBox.addItem(0, _("all"));

		CTemplateGet toptions = new CTemplateGet();
		toptions.setOutput(new String[]{"templateid", "name"});
		if(!empty(Nest.value(_REQUEST,"filter_groupid").$())){
			toptions.setGroupIds(Nest.value(_REQUEST,"filter_groupid").asLong());
		}
		toptions.setWithTriggers(true);
		CArray<Map> templates = API.Template(getIdentityBean(), executor).get(toptions);
		order_result(templates, "name");

		CArray templateIds = array();
		for(Map template: templates) {
			templateIds.put(template.get("templateid"), Nest.value(template,"templateid").$());

			templateComboBox.addItem(
				Nest.value(template,"templateid").$(),
				Nest.value(template,"name").asString()
			);
		}
		filterForm.addRow(_("Template"), templateComboBox);

		// filter trigger
		CComboBox triggerComboBox = new CComboBox("tpl_triggerid", get_request("tpl_triggerid", 0), "javascript: submit()");
		triggerComboBox.addItem(0, _("all"));

		SqlBuilder sqlParts = new SqlBuilder();
		String sqlCondition = empty(Nest.value(_REQUEST,"filter_hostid").$())
				? " AND "+sqlParts.dual.dbConditionInt("h.hostid", templateIds.valuesAsLong())
				: " AND h.hostid="+sqlParts.marshalParam(Nest.value(_REQUEST,"filter_hostid").$());

		String sql =
			"SELECT DISTINCT t.triggerid,t.description,h.name"+
			" FROM triggers t,hosts h,items i,functions f"+
			" WHERE f.itemid=i.itemid"+
				" AND h.hostid=i.hostid"+
				" AND t.status="+TRIGGER_STATUS_ENABLED+
				" AND t.triggerid=f.triggerid"+
				" AND h.status="+HOST_STATUS_TEMPLATE+
				" AND i.status="+ITEM_STATUS_ACTIVE+
					sqlCondition+
			" ORDER BY t.description";
		
		
		CArray<Map> triggers = DBfetchArrayAssoc(DBselect(executor, sql, sqlParts.getNamedParams()), "triggerid");

		for(Map trigger: triggers) {
			String templateName = empty(Nest.value(_REQUEST,"filter_hostid").$()) ? trigger.get("name")+NAME_DELIMITER : "";

			triggerComboBox.addItem(
				Nest.value(trigger,"triggerid").$(),
				StringUtils.join(templateName, trigger.get("description"))
			);
		}

		if (isset(_REQUEST,"tpl_triggerid") && !isset(triggers.get(_REQUEST.get("tpl_triggerid")))) {
			unset(triggerOptions.getFilter(), "templateid");
		}

		filterForm.addRow(_("Template trigger"), triggerComboBox);

		// filter host group
		CComboBox hostGroupsComboBox = new CComboBox("hostgroupid", get_request("hostgroupid", 0), "javascript: submit()");
		hostGroupsComboBox.addItem(0, _("all"));

		hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setHostIds(triggerOptions.getHostIds());
		hgoptions.setMonitoredHosts(true);
		hgoptions.setPreserveKeys(true);
		CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		order_result(hostGroups, "name");

		for(Map hostGroup: hostGroups) {
			hostGroupsComboBox.addItem(
				Nest.value(hostGroup,"groupid").$(),
				Nest.value(hostGroup,"name").asString()
			);
		}

		if (isset(Nest.value(_REQUEST,"hostgroupid").$()) && !isset(hostGroups.get(_REQUEST.get("hostgroupid")))) {
			triggerOptions.setGroupIds((Long[])null);
		}

		filterForm.addRow(_("Filter by host group"), hostGroupsComboBox);
	}
	
	private void reportByHost(SQLExecutor executor, CFormTable filterForm, CTriggerGet triggerOptions) {
		// filter host group
		CComboBox groupsComboBox = new CComboBox("filter_groupid", Nest.value(_REQUEST,"filter_groupid").$(), "javascript: submit();");
		groupsComboBox.addItem(0, _("all"));

		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setMonitoredHosts(true);
		hgoptions.setWithTriggers(true);
		CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		order_result(groups, "name");

		for(Map group: groups) {
			groupsComboBox.addItem(
				Nest.value(group,"groupid").$(),
				Nest.value(group,"name").asString()
			);
		}
		filterForm.addRow(_("Host group"), groupsComboBox);

		// filter host
		CComboBox hostsComboBox = new CComboBox("filter_hostid", Nest.value(_REQUEST,"filter_hostid").$(), "javascript: submit();");
		hostsComboBox.addItem(0, _("all"));

		CHostGet hoptions = new CHostGet();
		if(!empty(Nest.value(_REQUEST,"filter_groupid").$())){
			hoptions.setGroupIds(Nest.value(_REQUEST,"filter_groupid").asLong());
		}
		hoptions.setOutput(new String[]{"hostid", "name"});
		hoptions.setMonitoredHosts(true);
		hoptions.setWithTriggers(true);
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
		order_result(hosts, "name");
		hosts = rda_toHash(hosts, "hostid");

		for(Map host: hosts) {
			hostsComboBox.addItem(
				Nest.value(host,"hostid").$(),
				Nest.value(host,"name").asString()
			);
		}
		filterForm.addRow(_("Host"), hostsComboBox);

		// trigger options
		if (!empty(Nest.value(_REQUEST,"filter_groupid").$()) || !Nest.value(config,"dropdown_first_entry").asBoolean()) {
			triggerOptions.setGroupIds(Nest.value(_REQUEST, "filter_groupid").asLong());
		}
		if (!empty(Nest.value(_REQUEST,"filter_hostid").$()) && isset(hosts.get(_REQUEST.get("filter_hostid"))) || !Nest.value(config,"dropdown_first_entry").asBoolean()) {
			triggerOptions.setHostIds(Nest.value(_REQUEST, "filter_hostid").asLong());
		}
	}

}
