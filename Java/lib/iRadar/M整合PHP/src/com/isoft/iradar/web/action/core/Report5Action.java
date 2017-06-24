package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_MONTH;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupTrigger;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Report5Action extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Most busy triggers top 100"));
		page("file", "report5.action");
		page("hist_arg", new String[] { "period" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"period", array(T_RDA_STR,	O_OPT,	P_SYS | P_NZERO,	IN("\"day\",\"week\",\"month\",\"year\""),	 null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		CWidget rprt_wdgt = new CWidget();
		Nest.value(_REQUEST,"period").$(get_request("period", "day"));
		//boolean admin_links = (Nest.as(CWebUser.get("type")).asInteger() == USER_TYPE_IRADAR_ADMIN || Nest.as(CWebUser.get("type")).asInteger() == USER_TYPE_SUPER_ADMIN);

		CForm form = new CForm("get");

		CComboBox cmbPeriod = new CComboBox("period", Nest.value(_REQUEST,"period").$(), "submit()");
		cmbPeriod.addItem("day", _("Day"));
		cmbPeriod.addItem("week", _("Week"));
		cmbPeriod.addItem("month", _("Month"));
		cmbPeriod.addItem("year", _("Year"));
		
		form.addItem(cmbPeriod);
		
		rprt_wdgt.addPageHeader(_("MOST BUSY TRIGGERS TOP 100"));
		
		rprt_wdgt.addHeader(_("Report"), form);
		rprt_wdgt.addItem(BR());
		
		CTableInfo table = new CTableInfo(_("No triggers found."));
		table.setHeader(array(
			_("Host"),
			_("Trigger"),
			_("Severity"),
			_("Number of status changes")
		));
		
		String period = Nest.value(_REQUEST,"period").asString();
		
		int time_dif = SEC_PER_DAY;
		if ("week".equals(period)) {
			time_dif = SEC_PER_WEEK;
		} else if ("month".equals(period)) {
			time_dif = SEC_PER_MONTH;
		} else if ("year".equals(period)) {
			time_dif = SEC_PER_YEAR;
		} else if ("day".equals(period)) {
			time_dif = SEC_PER_DAY;
		}
		
		CArray triggersEventCount = array();
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT e.objectid,count(distinct e.eventid) AS cnt_event" +
				" FROM triggers t,events e" +
				" WHERE t.triggerid=e.objectid" +
					" AND e.source=" + EVENT_SOURCE_TRIGGERS +
					" AND e.object=" + EVENT_OBJECT_TRIGGER +
					" AND e.clock>" + sqlParts.marshalParam("clock", (time() - time_dif));
		
		// add permission filter
		if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
			String userid = Nest.as(CWebUser.get("userid")).asString();
			List<Long> userGroups = getUserGroupsByUserId(getIdentityBean(), executor, userid);
			sql += " AND EXISTS ("+
					"SELECT NULL"+
					" FROM functions f,items i,hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.id=hgg.groupid"+
							" AND "+ sqlParts.dual.dbConditionInt("r.groupid", userGroups.toArray(new Long[0])) +
					" WHERE t.triggerid=f.triggerid"+
						" AND f.itemid=i.itemid"+
						" AND i.hostid=hgg.hostid"+
					" GROUP BY f.triggerid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+")";
		}
		
		sql += " AND "+ sqlParts.where.dbConditionInt("t.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED})+
				" GROUP BY e.objectid"+
				" ORDER BY cnt_event desc";
			
		CArray<Map> result = DBselect(executor, sql, 100, sqlParts.getNamedParams());
		for (Map row : result) {
			Nest.value(triggersEventCount,row.get("objectid")).$(Nest.value(row,"cnt_event").$());
		}
		
		CTriggerGet params = new CTriggerGet();
		params.setTriggerIds(array_keys(triggersEventCount).valuesAsLong());
		params.setOutput(new String[]{"triggerid", "description", "expression", "priority", "flags", "url", "lastchange"});
		params.setSelectItems(new String[]{"hostid", "name", "value_type", "key_"});
		params.setSelectHosts(new String[]{"hostid"});
		params.setExpandDescription(true);
		params.setExpandData(true);
		params.setPreserveKeys(true);
		params.setNopermissions(true);
		CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(params);
			
		CArray hostIds = array();
		
		for (Entry<Object, Map> e : triggers.entrySet()) {
			Object triggerId = e.getKey();
			Map trigger = e.getValue();
			
			Nest.value(hostIds, trigger.get("hostid")).$(Nest.value(trigger, "hostid").$());
			
			CArray triggerItems = array();
			
			CArray<Map> items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").asCArray());
			Nest.value(trigger,"items").$(items);
			
			for (Map item : items) {
				triggerItems.add(map(
					"name", Nest.value(item,"name_expanded").$(),
					"params", map(
						"itemid", Nest.value(item,"itemid").$(),
						"action", in_array(Nest.value(item,"value_type").$(), array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64))
							? "showgraph" : "showvalues"
					)
				));
			}
			
			Nest.value(triggers, triggerId, "items").$(triggerItems);
			Nest.value(triggers, triggerId, "cnt_event").$(Nest.value(triggersEventCount, triggerId).$());
		}
			
		CArrayHelper.sort(triggers, array(
			map("field", "cnt_event",  "order", RDA_SORT_DOWN),
			"host", "description", "priority"
		));
		
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid"});
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setSelectScreens(API_OUTPUT_COUNT);
		hoptions.setPreserveKeys(true);
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
		
		CArray<CArray<Map>> scripts = API.Script(getIdentityBean(), executor).getScriptsByHosts(hostIds.valuesAsLong());
		
		for (Map trigger : triggers) {
			Object hostId = Nest.value(trigger,"hostid").$();
			
			CSpan hostName = new CSpan(Nest.value(trigger,"hostname").$(), "link_menu");
			hostName.setMenuPopup(getMenuPopupHost(Nest.value(hosts, hostId).asCArray(), Nest.value(scripts, hostId).asCArray()));

			CSpan triggerDescription = new CSpan(Nest.value(trigger,"description").$(), "link_menu");
			triggerDescription.setMenuPopup(getMenuPopupTrigger(trigger, Nest.value(trigger,"items").asCArray()));

			table.addRow(array(
				hostName,
				triggerDescription,
				getSeverityCell(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger()),
				Nest.value(trigger, "cnt_event").$()
			));
		}
		
		rprt_wdgt.addItem(table);
		rprt_wdgt.show();
	}

}
