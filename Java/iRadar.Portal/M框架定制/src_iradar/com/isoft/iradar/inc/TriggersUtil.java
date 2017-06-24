package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.PREG_OFFSET_CAPTURE;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_add;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.arsort;
import static com.isoft.iradar.Cphp.asort;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.eval;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.preg_match_all;
import static com.isoft.iradar.Cphp.preg_replace;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.substr_replace;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.AcknowUtil.get_last_event_by_triggerid;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DISCOVER;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DOWN;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_LOST;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_UP;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.EXPRESSION_FUNCTION_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_HOST_ITEM_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_HOST_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_NOT_A_MACRO_ERROR;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_BYTE_SUFFIXES;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_PREG_NUMBER;
import static com.isoft.iradar.inc.Defines.RDA_PREG_PRINT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_TIME_SUFFIXES;
import static com.isoft.iradar.inc.Defines.REGEXP_INCLUDE;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_LEFT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL_BIG;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupTrigger;
import static com.isoft.iradar.inc.FuncsUtil.num2letter;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_itemid;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.italic;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.MapsUtil.get_map_elements;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.validators.CTriggerFunctionValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.jdk.util.regex.IPattern;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.lang.CodeTodo;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;

@CodeConfirmed("blue.2.2.5")
public class TriggersUtil {
	
	public static String getSeverityStyle(int severity) {
		return getSeverityStyle(severity, true);
	}
	
	public static String getSeverityStyle(Integer severity, boolean type) {
		CArray<String> styles = map(
				TRIGGER_SEVERITY_DISASTER, "disaster",
				TRIGGER_SEVERITY_HIGH, "high",
				TRIGGER_SEVERITY_AVERAGE, "average",
				TRIGGER_SEVERITY_WARNING, "warning",
				TRIGGER_SEVERITY_INFORMATION, "information",
				TRIGGER_SEVERITY_NOT_CLASSIFIED, "not_classified"
			);
		if (!type) {
			return "normal";
		} else if (styles.containsKey(severity)) {
			return styles.get(severity);
		} else {
			return "";
		}
	}
	
	public static CArray<String> getSeverityCaption(IIdentityBean idBean, SQLExecutor executor) {
		Map<String,Object> config = select_config(idBean, executor);
		CArray<String> severities = map(
//				TRIGGER_SEVERITY_NOT_CLASSIFIED, _(Nest.value(config,"severity_name_0").asString()),
				TRIGGER_SEVERITY_INFORMATION, _(Nest.value(config,"severity_name_1").asString()),
				TRIGGER_SEVERITY_WARNING, _(Nest.value(config,"severity_name_2").asString()),
				TRIGGER_SEVERITY_AVERAGE, _(Nest.value(config,"severity_name_3").asString()),
//				TRIGGER_SEVERITY_HIGH, _(Nest.value(config,"severity_name_4").asString()),
				TRIGGER_SEVERITY_DISASTER, _(Nest.value(config,"severity_name_5").asString())
			);
		return severities;
	}
	
	public static String getSeverityCaption(IIdentityBean idBean, SQLExecutor executor, Integer severity) {
		Map<String,Object> config = select_config(idBean, executor);
		CArray<String> severities = map(
//				TRIGGER_SEVERITY_NOT_CLASSIFIED, _(Nest.value(config,"severity_name_0").asString()),
				TRIGGER_SEVERITY_INFORMATION, _(Nest.value(config,"severity_name_1").asString()),
				TRIGGER_SEVERITY_WARNING, _(Nest.value(config,"severity_name_2").asString()),
				TRIGGER_SEVERITY_AVERAGE, _(Nest.value(config,"severity_name_3").asString()),
//				TRIGGER_SEVERITY_HIGH, _(Nest.value(config,"severity_name_4").asString()),
				TRIGGER_SEVERITY_DISASTER, _(Nest.value(config,"severity_name_5").asString())
			);
		if (severities.containsKey(severity)) {
			return severities.get(severity);
		} else {
			return _("Unknown");
		}
	}
	
	public static String getSeverityColor(IIdentityBean idBean, SQLExecutor executor, int severity) {
		return getSeverityColor(idBean, executor, severity, TRIGGER_VALUE_TRUE);
	}
	
	public static String getSeverityColor(IIdentityBean idBean, SQLExecutor executor, int severity, int value) {
		if (value == TRIGGER_VALUE_FALSE) {
			return "AAFFAA";
		}
		Map<String, Object> config = select_config(idBean, executor);
		String color = null;
		switch (severity) {
		case TRIGGER_SEVERITY_DISASTER:
			color = Nest.value(config, "severity_color_5").asString();
			break;
		case TRIGGER_SEVERITY_HIGH:
			color = Nest.value(config, "severity_color_4").asString();
			break;
		case TRIGGER_SEVERITY_AVERAGE:
			color = Nest.value(config, "severity_color_3").asString();
			break;
		case TRIGGER_SEVERITY_WARNING:
			color = Nest.value(config, "severity_color_2").asString();
			break;
		case TRIGGER_SEVERITY_INFORMATION:
			color = Nest.value(config, "severity_color_1").asString();
			break;
		case TRIGGER_SEVERITY_NOT_CLASSIFIED:
			color = Nest.value(config, "severity_color_0").asString();
			break;
		default:
			color = Nest.value(config, "severity_color_0").asString();
		}
		return color;
	}

	public static CCol getSeverityCell(IIdentityBean idBean, SQLExecutor executor, Integer severity) {
		return getSeverityCell(idBean, executor, severity, null);
	}
	
	public static CCol getSeverityCell(IIdentityBean idBean, SQLExecutor executor, Integer severity, Object text) {
		return getSeverityCell(idBean, executor, severity, text, false);
	}

	public static CCol getSeverityCell(IIdentityBean idBean, SQLExecutor executor, Integer severity, Object text, boolean force_normal) {
		if (text == null) {
			text = CHtml.encode(getSeverityCaption(idBean, executor, severity));
		}
		return new CCol(text, getSeverityStyle(severity, !force_normal));
	}
	
	/*
	 * retrieve trigger's priority for services
	 */
	public static int get_service_status_of_trigger(IIdentityBean idBean, SQLExecutor executor, String triggerid) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT t.triggerid,t.priority"+
				" FROM triggers t"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
				" AND t.triggerid=#{triggerid}"+sqlParts.marshalParam(triggerid)+
				" AND t.status="+TRIGGER_STATUS_ENABLED+
				" AND t.value="+TRIGGER_VALUE_TRUE;
		Map row =DBfetch(DBselect(executor, sql, 1, sqlParts.getNamedParams()));
		return !empty(Nest.value(row,"priority").$()) ? Nest.value(row,"priority").asInteger() : 0;
	}
	
	/**
	 * Add color style and blinking to an object like CSpan or CDiv depending on trigger status
	 * Settings and colors are kept in "config" database table
	 *
	 * @param mixed _object             object like CSpan, CDiv, etc.
	 * @param int _triggerValue         TRIGGER_VALUE_FALSE or TRIGGER_VALUE_TRUE
	 * @param int _triggerLastChange
	 * @param bool _isAcknowledged
	 * @return void
	 */
	public static void addTriggerValueStyle(IIdentityBean idBean, SQLExecutor executor, CTag object, int triggerValue, int triggerLastChange, boolean isAcknowledged) {
		Map<String, Object> config = select_config(idBean, executor);

		// color of text and blinking depends on trigger value and whether event is acknowledged
		String color = null;
		Integer blinks = null;
		if (triggerValue == TRIGGER_VALUE_TRUE && !isAcknowledged) {
			color = Nest.value(config,"problem_unack_color").asString();
			blinks = Nest.value(config,"problem_unack_style").asInteger();
		} else if (triggerValue == TRIGGER_VALUE_TRUE && isAcknowledged) {
			color = Nest.value(config,"problem_ack_color").asString();
			blinks = Nest.value(config,"problem_ack_style").asInteger();
		} else if (triggerValue == TRIGGER_VALUE_FALSE && !isAcknowledged) {
			color = Nest.value(config,"ok_unack_color").asString();
			blinks = Nest.value(config,"ok_unack_style").asInteger();
		} else if (triggerValue == TRIGGER_VALUE_FALSE && isAcknowledged) {
			color = Nest.value(config,"ok_ack_color").asString();
			blinks = Nest.value(config,"ok_ack_style").asInteger();
		}
		if (isset(color) && isset(blinks)) {
			// color
			object.addStyle("color: #"+color);
			// blinking
			long timeSinceLastChange = time() - triggerLastChange;
			if (!empty(blinks) && timeSinceLastChange < Nest.value(config,"blink_period").asLong()) {
				object.addClass("blink"); // elements with this class will blink
				object.setAttribute("data-time-to-blink", Nest.value(config,"blink_period").asLong() - timeSinceLastChange);
			}
		} else {
			object.addClass("unknown");
		}
	}
	
	public static CArray<String> trigger_value2str() {
		CArray<String> triggerValues = map(
				TRIGGER_VALUE_FALSE, _("Restore"),
				TRIGGER_VALUE_TRUE, _("No Restore"));
		return triggerValues;
	}
	
	/**  时间状态修改为已修复和未修复两种
	 * @param tv
	 * @return
	 */
	public static String trigger_value2str(int tv) {
		CArray<String> triggerValues = map(
				TRIGGER_VALUE_FALSE, _("Restore"),
				TRIGGER_VALUE_TRUE, _("No Restore"));		
		if (triggerValues.containsKey(tv)) {
			return triggerValues.get(tv);
		} else {
			return _("Unknown");
		}
	}
	
	public static CArray<String> discovery_value() {
		CArray<String> array = map(
			DOBJECT_STATUS_UP,  _("UP"),
			DOBJECT_STATUS_DOWN, _("DOWN"),
			DOBJECT_STATUS_DISCOVER, _("DISCOVERED"),
			DOBJECT_STATUS_LOST, _("LOST"));
		return array;
	}
	
	public static String discovery_value(int val) {
		CArray<String> array = map(
			DOBJECT_STATUS_UP,  _("UP"),
			DOBJECT_STATUS_DOWN, _("DOWN"),
			DOBJECT_STATUS_DISCOVER, _("DISCOVERED"),
			DOBJECT_STATUS_LOST, _("LOST"));

		if (array.containsKey(val)) {
			return array.get(val);
		} else {
			return _("Unknown");
		}
	}
	
	public static String discovery_value_style(int val) {
		String style = null;
		switch (val) {
		case DOBJECT_STATUS_UP:
			style = "off";
			break;
		case DOBJECT_STATUS_DOWN:
			style = "on";
			break;
		case DOBJECT_STATUS_DISCOVER:
			style = "off";
			break;
		case DOBJECT_STATUS_LOST:
			style = "unknown";
			break;
		default:
			style = "";
		}
		return style;
	}
	
	public static CArray<Map> getParentHostsByTriggers(IIdentityBean idBean, SQLExecutor executor, CArray<Map> triggers) {
		triggers = Clone.deepcopy(triggers);
		CArray hosts = array();
		CArray<Map> triggerParent = array();

		Long tnum = null;
		Map trigger = null;
		CArray<Map> mirrorTriggers = null;
		while (!empty(triggers)) {
			mirrorTriggers = Clone.deepcopy(triggers);
			for(Entry<Object,Map> e:(Set<Entry<Object,Map>>)mirrorTriggers.entrySet()) {
				tnum = Nest.as(e.getKey()).asLong();
				trigger = e.getValue();
				if (Nest.value(trigger,"templateid").asLong() == 0) {
					if (isset(triggerParent,trigger.get("triggerid"))) {
						for(Object triggerid:Nest.as(triggerParent.get(trigger.get("triggerid"))).asCArray().keySet()) {
							hosts.put(triggerid, Nest.value(trigger,"hosts").$());
						}
					} else {
						hosts.put(trigger.get("triggerid"), Nest.value(trigger,"hosts").$());
					}
					triggers.remove(tnum);
				} else {
					if (isset(triggerParent,trigger.get("triggerid"))) {
						if (!isset(triggerParent,trigger.get("templateid"))) {
							triggerParent.put(trigger.get("templateid"), array());
						}
						Nest.as(triggerParent.get(trigger.get("templateid"))).asCArray().put(trigger.get("triggerid"), 1);
						Nest.as(triggerParent.get(trigger.get("templateid"))).asCArray().putAll(triggerParent.get(trigger.get("triggerid")));
					} else {
						if (!isset(triggerParent,trigger.get("templateid"))) {
							triggerParent.put(trigger.get("templateid"), array());
						}
						Nest.as(triggerParent.get(trigger.get("templateid"))).asCArray().put(trigger.get("triggerid"), 1);
					}
				}
			}
			CTriggerGet tget = new CTriggerGet();
			tget.setTriggerIds(rda_objectValues(triggers, "templateid").valuesAsLong());
			tget.setSelectHosts(new String[]{"hostid", "host", "name", "status"});
			tget.setOutput(new String[]{"triggerid", "templateid"});
			tget.setFilter("flags");
			tget.setNopermissions(true);
			triggers = API.Trigger(idBean, executor).get(tget);
		}
		return hosts;
	}
	
	public static Map get_trigger_by_triggerid(IIdentityBean idBean, SQLExecutor executor, String triggerid) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT t.* FROM triggers t"+
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
							" AND t.triggerid=#{triggerid}"+sqlParts.marshalParam(triggerid);
		Map db_trigger =DBfetch(DBselect(executor, sql, 1, sqlParts.getNamedParams()));
		if (!empty(db_trigger)) {
			return db_trigger;
		}
		error(_s("No trigger with triggerid \"%1$s\".", triggerid));
		return null;
	}
	
	public static CArray<Map> get_hosts_by_triggerid(IIdentityBean idBean, SQLExecutor executor, Long... triggerids) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
				"SELECT DISTINCT h.*"+
				" FROM hosts h,functions f,items i"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				    " AND i.tenantid=f.tenantid"+
				    " AND i.itemid=f.itemid"+
					" AND h.tenantid=i.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND "+sqlParts.dual.dbConditionInt("f.triggerid", triggerids),
				sqlParts.getNamedParams()
			);
	}

	public static CArray<Map> get_triggers_by_hostid(IIdentityBean idBean, SQLExecutor executor, Long hostid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
				"SELECT DISTINCT t.*"+
				" FROM triggers t,functions f,items i"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
				    " AND i.hostid="+sqlParts.marshalParam(hostid)+
					" AND f.tenantid=i.tenantid"+
					" AND f.itemid=i.itemid"+
					" AND f.tenantid=t.tenantid"+
					" AND f.triggerid=t.triggerid",
				sqlParts.getNamedParams()
			);
	}
	
	public static Map get_trigger_by_description(IIdentityBean idBean, SQLExecutor executor, String desc) {
		String[] segs = split(desc, ":", 2);
		String host_name = segs.length > 0 ? segs[0] : null;
		String trigger_description = segs.length > 1 ? segs[1] : null;
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT t.*"+
				" FROM triggers t,items i,functions f,hosts h"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				    " AND h.host="+sqlParts.marshalParam(host_name)+
					" AND i.tenantid=h.tenantid"+
					" AND i.hostid=h.hostid"+
					" AND f.tenantid=i.tenantid"+
					" AND f.itemid=i.itemid"+
					" AND t.tenantid=f.tenantid"+
					" AND t.triggerid=f.triggerid"+
					" AND t.description="+sqlParts.marshalParam(trigger_description)+
				" ORDER BY t.triggerid DESC";
		return DBfetch(DBselect(executor, sql, 1, sqlParts.getNamedParams()));
	}

	// unescape Raw URL
	public static String utf8RawUrlDecode(String source) {
		return urlencode(source);
	}
	
	/**
	 * Copies the given triggers to the given hosts or templates.
	 *
	 * Without the _srcHostId parameter it will only be able to copy triggers that belong to only one host. If the
	 * _srcHostId parameter is not passed, and a trigger has multiple hosts, it will throw an error. If the
	 * _srcHostId parameter is passed, the given host will be replaced with the destination host.
	 *
	 * This function takes care of copied trigger dependencies.
	 * If trigger is copied alongside with trigger on which it depends, then dependencies is replaced directly using new ids,
	 * If there is target host within dependency trigger, algorithm will search for potential matching trigger in target host,
	 * if matching trigger is found, then id from this trigger is used, if not rise exception,
	 * otherwise original dependency will be left.
	 *
	 *
	 * @param int|array _srcTriggerIds triggers which will be copied to _dstHostIds
	 * @param int|array _dstHostIds hosts and templates to whom add triggers, ids not present in DB (host table) will be ignored
	 * @param int _srcHostId host id in which context trigger with multiple hosts will be treated
	 *
	 * @return bool
	 */
	public static boolean copyTriggersToHosts(IIdentityBean idBean, SQLExecutor executor, Long[] srcTriggerIds, Long[] dstHostIds, Long srcHostId) {
		CTriggerGet options = new CTriggerGet();
		options.setTriggerIds(srcTriggerIds);
		options.setOutput(new String[]{"triggerid", "expression", "description", "url", "status", "priority", "comments", "type"});
		options.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
		options.setSelectDependencies(API_OUTPUT_REFER);

		Map srcHost = null;
		if (!empty(srcHostId)) {
			CHostGet hgoptions = new CHostGet();
			hgoptions.setOutput(new String[]{"host"});
			hgoptions.setHostIds(srcHostId);
			hgoptions.setPreserveKeys(true);
			hgoptions.setNopermissions(true);
			hgoptions.setTemplatedHosts(true);
			CArray<Map> dbSrcHosts = API.Host(idBean, executor).get(hgoptions);

			// if provided _srcHostId doesn't match any record in DB, return false
			if (empty(srcHost = reset(dbSrcHosts))) {
				return false;
			}
		}
		// if no _srcHostId provided we will need trigger host "host"
		else {
			options.setSelectHosts(new String[]{"host"});
		}
		CArray<Map> dbSrcTriggers = API.Trigger(idBean, executor).get(options);

		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "host"});
		hoptions.setHostIds(dstHostIds);
		hoptions.setPreserveKeys(true);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> dbDstHosts = API.Host(idBean, executor).get(hoptions);

		CArray newTriggers = array();
		// create each trigger for each host
		for(Map dstHost: dbDstHosts) {
			for(Map srcTrigger: dbSrcTriggers) {
				srcTrigger = Clone.deepcopy(srcTrigger);
				Object host, srcTriggerContextHostId;
				// if _srcHostId provided, get host "host" for explode_exp()
				if (Nest.as(srcHostId).asLong()!=0) {
					host = Nest.value(srcHost,"host").$();
					srcTriggerContextHostId = srcHostId;
				}
				// if _srcHostId not provided, use source trigger first host "host"
				else {
					// if we have multiple hosts in trigger expression and we haven't pointed (_srcHostId) which host to replace, call error
					if (count(Nest.value(srcTrigger,"hosts").$()) > 1) {
						error(_s("Cannot copy trigger \"%1$s:%2$s\", because it has multiple hosts in the expression.",
							Nest.value(srcTrigger,"description").$(), explode_exp(idBean, executor, Nest.value(srcTrigger,"expression").asString())));
						return false;
					}
					host = Nest.value(srcTrigger,"hosts", 0, "host").$();
					srcTriggerContextHostId = Nest.value(srcTrigger,"hosts", 0, "hostid").$();
				}
				// get expression for the new trigger to be added
				Nest.value(srcTrigger,"expression").$(explode_exp(idBean, executor, Nest.value(srcTrigger,"expression").asString(), false, false, asString(host), Nest.value(dstHost,"host").asString()));

				// the dependencies must be added after all triggers are created
				unset(srcTrigger, "dependencies");

				unset(srcTrigger, "templateid");

				CArray<Long[]> result = API.Trigger(idBean, executor).create(array(Clone.deepcopy(srcTrigger)));
				if (empty(result)) {
					return false;
				}

				newTriggers.put(srcTrigger.get("triggerid"), map(
					"newTriggerId", reset(Nest.value(result,"triggerids").asCArray()),
					"newTriggerHostId",  Nest.value(dstHost,"hostid").$(),
					"newTriggerHost",  Nest.value(dstHost,"host").$(),
					"srcTriggerContextHostId", srcTriggerContextHostId,
					"srcTriggerContextHost", host
				));
			}
		}

		CArray depIds = array();
		for(Map srcTrigger: dbSrcTriggers) {
			if (!empty(Nest.value(srcTrigger,"dependencies").$())) {
				for(Map depTrigger: (CArray<Map>)Nest.value(srcTrigger,"dependencies").asCArray()) {
					depIds.add(Nest.value(depTrigger,"triggerid").$());
				}
			}
		}
		
		options = new CTriggerGet();
		options.setTriggerIds(depIds.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setNopermissions(true);
		options.setSelectHosts(new String[]{"hostid"});
		options.setPreserveKeys(true);
		CArray<Map> depTriggers = API.Trigger(idBean, executor).get(options);

		// map dependencies to the new trigger IDs and save
		if (!empty(newTriggers)) {
			CArray<Map> dependencies = array();
			for(Map srcTrigger: dbSrcTriggers) {
				if (!empty(Nest.value(srcTrigger,"dependencies").$())) {
					// get corresponding created trigger id
					Map newTrigger = (Map)newTriggers.get(srcTrigger.get("triggerid"));

					Object depTriggerId;
					for(Map depTrigger: (CArray<Map>)srcTrigger.get("dependencies")) {
						// we have added _depTrigger trigger, and we know corresponding trigger id for newly created trigger
						if (isset(newTriggers.get(depTrigger.get("triggerid")))) {
							// dependency is within same host
							// according to _srcHostId parameter or dep trigger has single host
							if (Nest.value(newTrigger,"srcTriggerContextHostId").$().equals(newTriggers.getNested(depTrigger.get("triggerid"), "srcTriggerContextHostId"))) {
								depTriggerId = newTriggers.getNested(depTrigger.get("triggerid"), "newTriggerId");
							}
							// dependency is to trigger from another host
							else {
								depTriggerId = Nest.value(depTrigger,"triggerid").$();
							}
						}
						// we need to search for _depTrigger trigger if target host is within dependency hosts
						else if (in_array(map("hostid", Nest.value(newTrigger,"srcTriggerContextHostId").$()), (CArray)depTriggers.getNested(depTrigger.get("triggerid"), "hosts"))) {
							// get all possible _depTrigger matching triggers by description
							options = new CTriggerGet();
							options.setHostIds(Nest.value(newTrigger,"newTriggerHostId").asLong());
							options.setOutput(new String[]{"hosts", "triggerid", "expression"});
							options.setFilter("description", Nest.value(depTriggers, depTrigger.get("triggerid"), "description").asString());
							options.setPreserveKeys(true);
							CArray<Map> targetHostTriggersByDescription = API.Trigger(idBean, executor).get(options);

							// compare exploded expressions for exact match
							Object expr1 = explode_exp(idBean, executor, (String)depTriggers.getNested(depTrigger.get("triggerid"), "expression"));
							Object expr2;
							depTriggerId = null; 
							for(Map potentialTargetTrigger: targetHostTriggersByDescription) {
								expr2 = explode_exp(idBean, executor, Nest.value(potentialTargetTrigger,"expression").asString(), false, false, Nest.value(newTrigger,"newTriggerHost").asString(), Nest.value(newTrigger,"srcTriggerContextHost").asString());
								if (expr2.equals(expr1)) {
									// matching trigger has been found
									depTriggerId = Nest.value(potentialTargetTrigger,"triggerid").$();
									break;
								}
							}
							// if matching trigger wasn't found rise exception
							if (is_null(depTriggerId)) {
								expr1 = explode_exp(idBean, executor, Nest.value(srcTrigger,"expression").asString(), false, false, Nest.value(newTrigger,"srcTriggerContextHost").asString(), Nest.value(newTrigger,"newTriggerHost").asString());
								expr2 = explode_exp(idBean, executor, (String)depTriggers.getNested(depTrigger.get("triggerid"), "expression"), false, false, Nest.value(newTrigger,"srcTriggerContextHost").asString(), Nest.value(newTrigger,"newTriggerHost").asString());
								error(_s("Cannot add dependency from trigger \"%1$s:%2$s\" to non existing trigger \"%3$s:%4$s\".",
									Nest.value(srcTrigger,"description").$(), expr1,
									depTriggers.getNested(depTrigger.get("triggerid"), "description"), expr2));
								return false;
							}
						}
						// leave original dependency
						else {
							depTriggerId = Nest.value(depTrigger,"triggerid").$();
						}

						dependencies.add( map(
							"triggerid", Nest.value(newTrigger,"newTriggerId").$(),
							"dependsOnTriggerid", depTriggerId
						) );
					}
				}
			}
			if (!empty(dependencies)) {
				if (empty(API.Trigger(idBean, executor).addDependencies(dependencies))) {
					return false;
				}
			}
		}

		return true;
	}
	
	/**
	 * Function split trigger expresion by "&" and "|", that all elements from first level would be separated.
	 *
	 * @param string _expresion		trigger expresion
	 *
	 * @return array
	 */
	public static String[] splitByFirstLevel(String expresion) {
		char[] chars = isEmpty(expresion) ? new char[0]:expresion.toCharArray();
		int pos = 0;
		int level = 0;
		CArray<String> arrs = array();
		while (pos <chars.length) {
			switch (chars[pos]) {
				case '(':
					++level;
					break;
				case ')':
					--level;
					break;
				case '&':
				case '|':
					if (level==0) {
						arrs.add(trim(expresion.substring(0, pos)));
						expresion = expresion.substring(pos + 1);
						chars = isEmpty(expresion) ? new char[0]:expresion.toCharArray();
						pos = -1;
					}
					break;
				default:
					break;
			}
			++pos;
		}

		if (expresion!=null && expresion.length()>0) {
			arrs.add(trim(expresion));
		}
		return arrs.valuesAsString();
	}
	
	public static String construct_expression(IIdentityBean idBean, SQLExecutor executor, String itemid, CArray<Map> expressions) {
		String complite_expr = "";
		Map item = get_item_by_itemid(executor, itemid);
		Map host = get_host_by_itemid(idBean, executor, itemid);
		String prefix = host.get("host")+":"+item.get("key_")+".";

		if (empty(expressions)) {
			error(_("Expression cannot be empty"));
			return null;
		}

		String RDA_PREG_EXPESSION_FUNC_FORMAT = "^(["+RDA_PREG_PRINT+"]*)([&|]{1})[(]*(([a-zA-Z_.\\$]{6,7})(\\((["+RDA_PREG_PRINT+"]+?){0,1}\\)))(["+RDA_PREG_PRINT+"]*)$";
		CArray functions = map("regexp", 1, "iregexp", 1);
		CArray<Map> expr_array = array();
		int cexpor = 0;
		int startpos = -1;

		for(Map expression: expressions) {
			Nest.value(expression,"value").$(preg_replace("\\s+?(AND){1,2}?\\s+?", "&", Nest.value(expression,"value").asString()));
			Nest.value(expression,"value").$(preg_replace("\\s+?(OR){1,2}?\\s+?", "|", Nest.value(expression,"value").asString()));

			String eq_global;
			if (Nest.value(expression,"type").asInteger() == REGEXP_INCLUDE) {
				if (!empty(complite_expr)) {
					complite_expr+=" | ";
				}
				if (cexpor == 0) {
					startpos = rda_strlen(complite_expr);
				}
				cexpor++;
				eq_global = "#0";
			} else {
				if ((cexpor > 1) & (startpos >= 0)) {
					String head = substr(complite_expr, 0, startpos);
					String tail = substr(complite_expr, startpos);
					complite_expr = head+"("+tail+")";
				}
				cexpor = 0;
				eq_global = "=0";
				if (!empty(complite_expr)) {
					complite_expr+=" & ";
				}
			}

			String exprv = "&"+Nest.value(expression,"value").$();
			exprv = preg_replace("\\s+?(\\&|\\|){1,2}?\\s+?", "$1", exprv);

			expr_array = array();
			int sub_expr_count=0;
			String sub_expr = "";
			int multi = preg_match(".+(&|\\|).+", exprv);

			CArray<String> arr = array();
			while (preg_match(IPattern.compile(RDA_PREG_EXPESSION_FUNC_FORMAT, IPattern.CASE_INSENSITIVE), exprv, arr) != 0) {
				arr.put(4, rda_strtolower((String)arr.get(4)));
				if (!isset(functions.get(arr.get(4)))) {
					error(_("Incorrect function is used")+". ["+expression.get("value")+"]");
					return null;
				}
				expr_array.put(sub_expr_count, "eq", trim(arr.get(2)));
				expr_array.put(sub_expr_count, "regexp", rda_strtolower(arr.get(4))+arr.get(5));

				sub_expr_count++;
				exprv = arr.get(1);
				arr = array();
			}

			if (empty(expr_array)) {
				error(_("Incorrect trigger expression")+". ["+expression.get("value")+"]");
				return null;
			}

			expr_array.put(sub_expr_count-1, "eq", "");

			String sub_eq = "";
			if (multi > 0) {
				sub_eq = eq_global;
			}

			for(Entry<Object, Map> entry: expr_array.entrySet()) {
				Map expr = entry.getValue();
				if (multi > 0) {
					sub_expr = expr.get("eq")+"({"+prefix+expr.get("regexp")+"})"+sub_eq+sub_expr;
				} else {
					sub_expr = expr.get("eq")+"{"+prefix+expr.get("regexp")+"}"+sub_eq+sub_expr;
				}
			}

			if (multi > 0) {
				complite_expr += "("+sub_expr+")";
			} else {
				complite_expr += "(("+sub_expr+")"+eq_global+")";
			}
		}

		if ((cexpor > 1) & (startpos >= 0)) {
			String head = substr(complite_expr, 0, startpos);
			String tail = substr(complite_expr, startpos);
			complite_expr = head+"("+tail+")";
		}

		return complite_expr;
	}
	
	/********************************************************************************
	 *                                                                              *
	 * Purpose: Translate {10}>10 to something like                                 *
	 * localhost:system.cpu.load.last(0)>10                                         *
	 *                                                                              *
	 * Comments: !!! Don't forget sync code with C !!!                              *
	 *                                                                              *
	 *******************************************************************************/
	public static Object explode_exp(IIdentityBean idBean, SQLExecutor executor, String expressionCompressed){
		return explode_exp(idBean, executor, expressionCompressed, false);
	}

	public static Object explode_exp(IIdentityBean idBean, SQLExecutor executor, String expressionCompressed, boolean html){
		return explode_exp(idBean, executor, expressionCompressed, html, false);
	}

	public static Object explode_exp(IIdentityBean idBean, SQLExecutor executor, String expressionCompressed, boolean html, boolean resolveMacro){
		return explode_exp(idBean, executor, expressionCompressed, html, resolveMacro, null);
	}
	
	public static Object explode_exp(IIdentityBean idBean, SQLExecutor executor, String expressionCompressed, boolean html, boolean resolveMacro, String sourceHost){
		return explode_exp(idBean, executor, expressionCompressed, html, resolveMacro, sourceHost, null);
	}
	
	public static Object explode_exp(IIdentityBean idBean, SQLExecutor executor, String expressionCompressed, boolean html, boolean resolveMacro, String sourceHost, String destinationHost){
		Object expressionExpanded = html ? array() : new StringBuilder();
		CArray trigger = array();
		String state = null, userMacro = null, lldMacro = null, functionId = null;
		int max = rda_strlen(expressionCompressed);
		char[] expressionCompresseds = expressionCompressed.toCharArray();
		for (int i = 0; i < max; i++) {
			if (expressionCompresseds[i] == '{') {
				if (expressionCompresseds[i + 1] == '$') {
					state = "USERMACRO";
					userMacro = "";
				} else if (expressionCompresseds[i + 1] == '#') {
					state = "LLDMACRO";
					lldMacro = "";
				} else {
					state = "FUNCTIONID";
					functionId = "";
					continue;
				}
			} else if (expressionCompresseds[i] == '}') {
				if ("USERMACRO".equals(state)) {
					state = "";
					userMacro += "}";
					if (resolveMacro) {
						Map functionData = new IMap();
						functionData.put("expression", userMacro);
						userMacro = CMacrosResolverHelper.resolveTriggerExpressionUserMacro(idBean, executor, CArray.valueOf(functionData));
					}
					if (html) {
						((CArray)expressionExpanded).add(userMacro);
					} else {
						((StringBuilder)expressionExpanded).append(userMacro);
					}
					continue;
				} else if ("LLDMACRO".equals(state)) {
					state = "";
					lldMacro += "}";
					if (html) {
						((CArray)expressionExpanded).add(lldMacro);
					} else {
						((StringBuilder)expressionExpanded).append(lldMacro);
					}
					continue;
				}else if ("TRIGGER.VALUE".equals(functionId)) {
					state = "";
					if (html) {
						((CArray)expressionExpanded).add('{'+functionId+'}');
					} else {
						((StringBuilder)expressionExpanded).append('{'+functionId+'}');
					}
					continue;
				}
				
				state = "";
				boolean error = true;
				
				if (is_numeric(functionId)) {
					SqlBuilder sqlParts = new SqlBuilder();
					Map functionData = DBfetch(DBselect(executor,
						"SELECT h.host,h.hostid,i.itemid,i.key_,f.function,f.triggerid,f.parameter,i.itemid,i.status,i.type,i.flags"+
						" FROM items i,functions f,hosts h"+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
						    " AND f.functionid="+sqlParts.marshalParam(functionId)+
							" AND i.tenantid=f.tenantid"+
							" AND i.itemid=f.itemid"+
							" AND h.tenantid=i.tenantid"+
							" AND h.hostid=i.hostid",
						sqlParts.getNamedParams()
					));
					if (functionData!=null && !functionData.isEmpty()) {
						error = false;
						if (resolveMacro) {
							trigger = CArray.valueOf(functionData);
							
							// expand macros in item key
							CArray<Map> items = CMacrosResolverHelper.resolveItemKeys(idBean, executor, array(functionData));
							Map item = reset(items);

							Nest.value(functionData,"key_").$(Nest.value(item,"key_expanded").$());

							// expand macros in function parameter
							CArray<Map> functionParameters = CMacrosResolverHelper.resolveFunctionParameters(idBean, executor, array(functionData));
							Map _functionParameter = reset(functionParameters);
							Nest.value(functionData,"parameter").$(Nest.value(_functionParameter,"parameter_expanded").$());
						}
						
						if (sourceHost != null && destinationHost != null && sourceHost.equals(Nest.value(functionData,"host").$())) {
							Nest.value(functionData,"host").$(destinationHost);
						}
						
						String style = null;
						if (html) {
							if (Nest.value(functionData,"status").asInteger() == ITEM_STATUS_DISABLED) {
								style  = "disabled";
							} else if (Nest.value(functionData,"status").asInteger() == ITEM_STATUS_ACTIVE) {
								style = "enabled";
							} else {
								style = "unknown";
							}

							CTag link = null;
							if (Nest.value(functionData,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED || Nest.value(functionData,"type").asInteger() == ITEM_TYPE_HTTPTEST) {
								link  = new CSpan(Nest.value(functionData,"host").asString()+":"+Nest.value(functionData,"key_").asString(), style);
							} else if (Nest.value(functionData,"flags").asInteger() == RDA_FLAG_DISCOVERY_PROTOTYPE) {
								link = new CLink(
									Nest.value(functionData,"host").asString()+":"+Nest.value(functionData,"key_").asString(),
									"disc_prototypes.action?form=update&itemid="+Nest.value(functionData,"itemid").asString()+"&parent_discoveryid="+
									Nest.value(trigger,"discoveryRuleid").asString(),
									style
								);
							} else {
								link = new CLink(
									Nest.value(functionData,"host").asString()+":"+Nest.value(functionData,"key_").asString(),
									"items.action?form=update&itemid="+Nest.value(functionData,"itemid").asString(),
									style
								);
							}

							((CArray)expressionExpanded).add(array("{", link,".", HtmlUtil.bold(Nest.value(functionData,"function").asString()+"("), Nest.value(functionData,"parameter").$(), HtmlUtil.bold(")"), "}"));
						} else {
							((StringBuilder)expressionExpanded).append("{"+Nest.value(functionData,"host").asString()+":"+Nest.value(functionData,"key_").asString()+"."+Nest.value(functionData,"function").asString()+"("+Nest.value(functionData,"parameter").asString()+")}");
						}
					}
				}
				
				if (error) {
					if (html) {
						((CArray)expressionExpanded).add(new CSpan("*ERROR*", "on"));
					} else {
						((StringBuilder)expressionExpanded).append("*ERROR*");
					}
				}
				continue;
			}
			
			if ("FUNCTIONID".equals(state)) {
				functionId += expressionCompresseds[i];
			} else if ("USERMACRO".equals(state)) {
				userMacro += expressionCompresseds[i];
			} else if ("LLDMACRO".equals(state)) {
				lldMacro += expressionCompresseds[i];
			} else {
				if (html) {
					((CArray)expressionExpanded).add(expressionCompresseds[i]);
				} else {
					((StringBuilder)expressionExpanded).append(expressionCompresseds[i]);
				}
			}
		}
		return expressionExpanded;
	}
	
	public static <T> T triggerExpression(CArray trigger) {
		return triggerExpression(trigger, false);
	}
	/**
	 * Translate {10}>10 to something like {localhost:system.cpu.load.last(0)}>10.
	 *
	 * @param array _trigger
	 * @param bool  _html
	 *
	 * @return array|string
	 */
	public static <T> T triggerExpression(Map trigger, boolean html) {
		String expression = Nest.value(trigger,"expression").asString();
		Object exp = html ? array() : new StringBuilder();
		String state = "", usermacro = null, lldmacro = null, functionid = null;
		int len = strlen(expression);
		char[] expressions = expression.toCharArray();
		for (int i = 0; i < len; i++) {
			if (expressions[i] == '{') {
				if (expressions[i + 1] == '$') {
					usermacro = "";
					state = "USERMACRO";
				} else if (expressions[i + 1] == '#') {
					lldmacro = "";
					state = "LLDMACRO";
				} else {
					functionid = "";
					state = "FUNCTIONID";
					continue;
				}
			} else if (expressions[i] == '}') {
				if ("USERMACRO".equals(state)) {
					usermacro += "}";
					if (html) {
						((CArray)exp).add(usermacro);
					} else {
						((StringBuilder)exp).append(usermacro);
					}
				} else if ("LLDMACRO".equals(state)) {
					lldmacro += "}";
					if (html) {
						((CArray)exp).add(lldmacro);
					} else {
						((StringBuilder)exp).append(lldmacro);
					}
				} else if ("TRIGGER.VALUE".equals(functionid)) {
					if (html) {
						((CArray)exp).add("{"+functionid+"}");
					} else {
						((StringBuilder)exp).append("{"+functionid+"}");
					}
				}
				else if (is_numeric(functionid) && isset(Nest.value(trigger, "functions", functionid).$())) {
					CArray function_data = Nest.value(trigger, "functions", functionid).asCArray();
					array_add(function_data, Nest.value(trigger, "items", function_data.get("itemid")).asCArray());
					array_add(function_data, Nest.value(trigger, "hosts", function_data.get("hostid")).asCArray());

					if (html) {
						String style = (Nest.value(function_data,"status").asInteger() == ITEM_STATUS_DISABLED) ? "disabled" : "unknown";
						if (Nest.value(function_data,"status").asInteger() == ITEM_STATUS_ACTIVE) {
							style = "enabled";
						}

						Object link = null;
						if (Nest.value(function_data,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED || Nest.value(function_data,"type").asInteger() == ITEM_TYPE_HTTPTEST) {
							link = new CSpan(function_data.get("host")+":"+CHtml.encode(Nest.value(function_data,"key_").asString()), style);
						} else if (Nest.value(function_data,"flags").asInteger() == RDA_FLAG_DISCOVERY_PROTOTYPE) {
							link = new CLink(function_data.get("host")+":"+CHtml.encode(Nest.value(function_data,"key_").asString()),
								"disc_prototypes.action?form=update&itemid="+function_data.get("itemid")+"&parent_discoveryid="+
								Nest.value(trigger,"discoveryRuleid").$(), style);
						} else {
							String url = "'"+_("Items")+"', '"+RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX+"items.action?form=update&itemid="+Nest.value(function_data,"itemid").$()+"'";
							link = new CLink(function_data.get("host")+":"+CHtml.encode(Nest.value(function_data,"key_").asString()),
									IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL), style,null,Boolean.TRUE);
						}
						array_push(
							(CArray)exp,
							array(
								"{",
								link,
								".",
								bold(function_data.get("function")+"("),
								CHtml.encode(Nest.value(function_data,"parameter").asString()),
								bold(")"),
								"}"
							)
						);
					} else {
						((StringBuilder)exp).append("{"+function_data.get("host")+":"+function_data.get("key_")+"."+function_data.get("function")+"("+function_data.get("parameter")+")}");
					}
				} else {
					if (html) {
						array_push((CArray)exp, new CSpan("*ERROR*", "on"));
					} else {
						((StringBuilder)exp).append( "*ERROR*" );
					}
				}

				state = "";
				continue;
			}
			
			if ("FUNCTIONID".equals(state)) {
				functionid += expressions[i];
			} else if ("USERMACRO".equals(state)) {
				usermacro += expressions[i];
			} else if ("LLDMACRO".equals(state)) {
				lldmacro += expressions[i];
			} else {
				if (html) {
					((CArray)exp).add(expressions[i]);
				} else {
					((StringBuilder)exp).append(expressions[i]);
				}
			}
		}
		return (T) (html? exp: ((StringBuilder)exp).toString());
	}
	
	public static void $CODE$SEPARATOR$BEGIN(){}

	public static String implode_exp(IIdentityBean idBean, SQLExecutor executor, String expression, long triggerId) throws Exception {
		return implode_exp(idBean, executor, expression, triggerId, array());
	}
	
	/**
	 * Implodes expression, replaces names and keys with IDs.
	 *
	 * For example: localhost:system.cpu.load.last(0)>10 will be translated to {12}>10 and created database representation.
	 *
	 * @throws Exception if error occurred
	 *
	 * @param string _expression Full expression with host names and item keys
	 * @param numeric _triggerid
	 * @param array optional _hostnames Reference to array which will be filled with unique visible host names.
	 *
	 * @return string Imploded expression (names and keys replaced by IDs)
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String implode_exp(IIdentityBean idBean, SQLExecutor executor, String expression, long triggerId, CArray hostnames) throws Exception {
		CTriggerExpression expressionData = new CTriggerExpression();
		if (!expressionData.parse(expression)) {
			throw new Exception(expressionData.error);
		}
	
		CArray<Object> newFunctions = array();
		CArray<Map> functions = array();
		CArray items = array();
		CTriggerFunctionValidator triggerFunctionValidator = CValidator.init(new CTriggerFunctionValidator(),array());
	
		for(Map exprPart: expressionData.expressions) {
			if (isset(newFunctions.get(exprPart.get("expression")))) {
				continue;
			}
	
			if (!isset(items.getNested(exprPart.get("host"), exprPart.get("item")))) {
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> result = DBselect(executor,
					"SELECT i.itemid,i.value_type,h.name"+
					" FROM items i,hosts h"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
					    " AND i.key_="+sqlParts.marshalParam(Nest.value(exprPart,"item").$())+
						" AND "+sqlParts.where.dbConditionInt("i.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED, RDA_FLAG_DISCOVERY_PROTOTYPE})+
						" AND h.host="+sqlParts.marshalParam(Nest.value(exprPart,"host").$())+
						" AND h.tenantid=i.tenantid"+
						" AND h.hostid=i.hostid",
						sqlParts.getNamedParams()
				);
				Map row;
				if (!empty(row = DBfetch(result))) {
					hostnames.add( Nest.value(row,"name").$() );
					items.put(exprPart.get("host"), exprPart.get("item"), map(
						"itemid", Nest.value(row,"itemid").$(),
						"valueType", row.get("value_type")
					));
				} else {
					throw new Exception(_s("Incorrect item key \"%1$s\" provided for trigger expression on \"%2$s\".",
							Nest.value(exprPart,"item").$(), Nest.value(exprPart,"host").$()));
				}
			}
	
			if (!triggerFunctionValidator.validate(idBean, map(
					"function", Nest.value(exprPart,"function").$(),
					"functionName", Nest.value(exprPart,"functionName").$(),
					"functionParamList", Nest.value(exprPart,"functionParamList").$(),
					"valueType", items.getNested(exprPart.get("host"), exprPart.get("item"), "valueType")))) {
				throw new Exception(triggerFunctionValidator.getError());
			}
	
			newFunctions.put(exprPart.get("expression"), 0);
	
			functions.add(map(
				"itemid", items.getNested(exprPart.get("host"), exprPart.get("item"), "itemid"),
				"triggerid", triggerId,
				"function", Nest.value(exprPart,"functionName").$(),
				"parameter", exprPart.get("functionParam")
			));
		}
	
		CArray functionIds = CDB.insert(idBean, executor,"functions", functions);
	
		int _num = 0;
		for(Entry<Object, Object> entry: newFunctions.entrySet()) {
			 entry.setValue(functionIds.get(_num++));
		}
	
		Map[] ms = new Map[expressionData.expressions.size()];
		expressionData.expressions.toArray(ms);
		
		int index = ms.length-1;
		Map exprPart = ms[index];
		do {
			expression = substr_replace(expression, "{"+newFunctions.get(exprPart.get("expression"))+"}",
					Nest.value(exprPart,"pos").asInteger(), strlen(Nest.value(exprPart,"expression").asString()));
		}
		while ((index--)>0 && !empty(exprPart = ms[index]));
	
		hostnames = array_unique(hostnames);
	
		return expression;
	}
		
	
	/**
	 * Get items from expression.
	 *
	 * @param CTriggerExpression _triggerExpression
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray getExpressionItems(IIdentityBean idBean, SQLExecutor executor, CTriggerExpression triggerExpression) {
		CArray items = array();
		CArray processedFunctions = array();
		CArray processedItems = array();
	
		for(Map expression: triggerExpression.expressions) {
			if (isset(processedFunctions.get(expression.get("expression")))) {
				continue;
			}
	
			if (!isset(processedItems.getNested(expression.get("host"), expression.get("item")))) {
				SqlBuilder sqlParts = new SqlBuilder();
				sqlParts.select.put("i.itemid,i.flags");
				sqlParts.from.put("items i,hosts h");
				sqlParts.where.put("i.key_="+sqlParts.marshalParam(Nest.value(expression,"item").$()));
				sqlParts.where.dbConditionInt("i.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED, RDA_FLAG_DISCOVERY_PROTOTYPE});
				sqlParts.where.put(" h.host="+sqlParts.marshalParam(Nest.value(expression,"host").$()));
				sqlParts.where.put("h.tenantid=i.tenantid");
				sqlParts.where.put("h.hostid=i.hostid");
				
				CArray<Map> dbItems = DBselect(executor, sqlParts);
				Map dbItem;
				if (!empty(dbItem = DBfetch(dbItems))) {
					items.add( dbItem );
					processedItems.put(expression.get("host"), expression.get("item"), true);
				}
			}
	
			processedFunctions.put(expression.get("expression"), true);
		}
	
		return items;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static boolean check_right_on_trigger_by_expression(IIdentityBean idBean, SQLExecutor executor, int _permission, String _expression) {
		CTriggerExpression _expressionData = new CTriggerExpression();
		if (!_expressionData.parse(_expression)) {
			error(_expressionData.error);
			return false;
		}
		CArray<String> _expressionHosts = _expressionData.getHosts();
	
		CHostGet hoptions = new CHostGet();
		hoptions.setFilter("host", _expressionHosts.valuesAsString());
		if(_permission == PERM_READ_WRITE){
			hoptions.setEditable(true);
		}
		hoptions.setOutput(new String[]{"hostid", "host"});
		hoptions.setTemplatedHosts(true);
		hoptions.setPreserveKeys(true);
		CArray<Map> _hosts = API.Host(idBean, executor).get(hoptions);
		_hosts = rda_toHash(_hosts, "host");
	
		for(String _host: _expressionHosts) {
			if (!isset(_hosts.get(_host))) {
				error(_s("Incorrect trigger expression. Host \"%1$s\" does not exist or you have no access to this host.", _host));
				return false;
			}
		}
	
		return true;
	}
		
	@CodeConfirmed("blue.2.2.5")
	public static CArray replace_template_dependencies(IIdentityBean idBean, SQLExecutor executor, CArray<Object> _deps, String _hostid) {
		SqlBuilder sqlParts = null;
		for(Entry<Object, Object> entry: _deps.entrySet()) {
			Object _id = entry.getKey();
			Object _val = entry.getValue();
			
			sqlParts = new SqlBuilder();
			String _sql = "SELECT t.triggerid"+
					" FROM triggers t,functions f,items i"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
					    " AND t.tenantid=f.tenantid"+
					    " AND t.triggerid=f.triggerid"+
						" AND f.tenantid=i.tenantid"+
						" AND f.itemid=i.itemid"+
						" AND t.templateid="+sqlParts.marshalParam(_val)+
						" AND i.hostid="+sqlParts.marshalParam(_hostid);
			Map _db_new_dep = null;
			if (!empty(_db_new_dep = DBfetch(DBselect(executor, _sql, sqlParts.getNamedParams())))) {
				_deps.put(_id, Nest.value(_db_new_dep,"triggerid").$());
			}
		}
	
		return _deps;
	}
			
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo getTriggersOverview(IIdentityBean idBean, SQLExecutor executor, CArray hostIds, String application, String pageFile) {
		return getTriggersOverview(idBean, executor, hostIds, application, pageFile, null);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo getTriggersOverview(IIdentityBean idBean, SQLExecutor executor,CArray hostIds, String application, String pageFile, Integer viewMode) {
		return getTriggersOverview(idBean, executor, hostIds, application, pageFile, viewMode, null);
	}
	
	/**
	 * Creates and returns the trigger overview table for the given hosts.
	 *
	 * @param array  hostIds
	 * @param string application	name of application to filter
	 * @param string pageFile		the page where the element is displayed
	 * @param int     viewMode		table display style: either hosts on top, or host on the left side
	 * @param string screenId		the ID of the screen, that contains the trigger overview table
	 *
	 * @return CTableInfo
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo getTriggersOverview(IIdentityBean idBean, SQLExecutor executor, CArray hostIds, String application, String pageFile, Integer viewMode, String screenId) {
		if (is_null(viewMode)) {
			viewMode = asInteger(CProfile.get(idBean, executor, "web.overview.view.style", STYLE_TOP));
		}
	
		// get application ids
		CArray applicationIds = null;
		if (!"".equals(application)) {
			CAppGet aoptions = new CAppGet();
			aoptions.setHostIds(hostIds.valuesAsLong());
			aoptions.setFilter("name", application);
			aoptions.setOutput(new String[]{"applicationid"});
			CArray<Map> dbApplications = API.Application(idBean, executor).get(aoptions);
			applicationIds = rda_objectValues(dbApplications, "applicationid");
			hostIds = null;
		}
	
		// get triggers
		CTriggerGet toptions = new CTriggerGet();
		if (hostIds != null) {
			toptions.setHostIds(hostIds.valuesAsLong());
		}
		if (applicationIds != null) {
			toptions.setApplicationIds(applicationIds.valuesAsLong());
		}
		toptions.setMonitored(true);
		toptions.setSkipDependent(true);
		toptions.setOutput(API_OUTPUT_EXTEND);
		toptions.setSelectHosts(new String[]{"hostid", "name"});
		toptions.setSortfield("description");
		CArray<Map> dbTriggers = API.Trigger(idBean, executor).get(toptions);
		// get hosts
		hostIds = array();
		for(Map trigger: dbTriggers) {
			Map host = reset(Nest.value(trigger,"hosts").asCArray());
	
			hostIds.put(host.get("hostid"), Nest.value(host,"hostid").$());
		}
	
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"name", "hostid", "status"});
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setPreserveKeys(true);
		if(viewMode == STYLE_LEFT){
			hoptions.setSelectScreens(API_OUTPUT_COUNT);
		}
		CArray<Map> hosts = API.Host(idBean, executor).get(hoptions);
	
		CArray<Map> triggers = array();
		CArray<String> hostNames = array();
	
		for(Map trigger: dbTriggers) {
			Map host = reset(Nest.value(trigger,"hosts").asCArray());
	
			Nest.value(host,"name").$(Nest.value(host,"name").asString());
			Nest.value(trigger,"description").$(CMacrosResolverHelper.resolveTriggerReference(idBean, Nest.value(trigger,"expression").asString(), Nest.value(trigger,"description").asString()));
			hostNames.put(host.get("hostid"), Nest.value(host,"name").$());
	
			// a little tricky check for attempt to overwrite active trigger (value=1) with
			// inactive or active trigger with lower priority.
			Object trigger_description = trigger.get("description"),
					host_name = host.get("name");
			if (!isset(triggers.getNested(trigger_description, host_name))
					|| ((Nest.value(triggers, trigger_description, host_name, "value").asInteger() == TRIGGER_VALUE_FALSE && Nest.value(trigger,"value").asInteger() == TRIGGER_VALUE_TRUE)
						|| ((Nest.value(triggers, trigger_description, host_name, "value").asInteger() == TRIGGER_VALUE_FALSE || Nest.value(trigger,"value").asInteger() == TRIGGER_VALUE_TRUE)
							&& Nest.value(trigger,"priority").asInteger() > Nest.value(triggers, trigger_description, host_name, "priority").asInteger()))) {
				triggers.put(trigger_description, host_name, map(
					"hostid", Nest.value(host,"hostid").$(),
					"triggerid", Nest.value(trigger,"triggerid").$(),
					"value", Nest.value(trigger,"value").$(),
					"lastchange", Nest.value(trigger,"lastchange").$(),
					"priority", Nest.value(trigger,"priority").$(),
					"flags", Nest.value(trigger,"flags").$(),
					"url", Nest.value(trigger,"url").$(),
					"hosts", array(host)
				));
			}
		}
	
		CTableInfo triggerTable = new CTableInfo(_("No triggers found."));
	
		if (empty(hostNames)) {
			return triggerTable;
		}
	
		triggerTable.makeVerticalRotation();
	
		order_result(hostNames);
	
		if (viewMode == STYLE_TOP) {
			// header
			CArray header = array(new CCol(_("Triggers"), "center"));
	
			for(String hostName: hostNames) {
				header.add( new CCol(hostName, "vertical_rotation") );
			}
	
			triggerTable.setHeader(header, "vertical_header");
	
			// data
			for(Entry<Object, Map> entry: triggers.entrySet()) {
				Object description = entry.getKey();
				Map triggerHosts = entry.getValue();
				CArray columns = array(nbsp(asString(description)));
	
				for(String hostName: hostNames) {
					columns.add( getTriggerOverviewCells(idBean, executor,
						isset(triggerHosts.get(hostName)) ? Nest.value(triggerHosts, hostName).asCArray() : null,
						pageFile,
						screenId
					));
				}
	
				triggerTable.addRow(columns);
			}
		}
		else {
			// header
			CArray header = array(new CCol(_("Host"), "center"));
	
			for(Entry<Object, Map> entry: triggers.entrySet()) {
				Object description = entry.getKey();
				//Map _triggerHosts = entry.getValue();
				header.add( new CCol(asString(description), "vertical_rotation") );
			}
	
			triggerTable.setHeader(header, "vertical_header");
	
			// data
			CArray<CArray<Map>> scripts = API.Script(idBean, executor).getScriptsByHosts(rda_objectValues(hosts, "hostid").valuesAsLong());
	
			for(Entry<Object, String> entry: hostNames.entrySet()) {
				Object hostId = entry.getKey();
				String hostName = entry.getValue();		
			
				CSpan name = new CSpan(hostName, "link_menu");
				name.setMenuPopup(getMenuPopupHost(Nest.value(hosts, hostId).asCArray(), Nest.value(scripts, hostId).asCArray()));
	
				CArray columns = array(name);
				for(Map triggerHosts: triggers) {
					columns.add(getTriggerOverviewCells(idBean, executor,
						isset(triggerHosts,hostName) ? Nest.value(triggerHosts, hostName).asCArray() : null,
						pageFile,
						screenId
					));
				}
	
				triggerTable.addRow(columns);
			}
		}
	
		return triggerTable;
	}
	
	
	@CodeConfirmed("blue.2.2.5")
	public static CCol getTriggerOverviewCells(IIdentityBean idBean, SQLExecutor executor, CArray trigger, String pageFile) {
		return getTriggerOverviewCells(idBean, executor, trigger, pageFile, null);
	}
	
	/**
	 * Creates and returns a trigger status cell for the trigger overview table.
	 *
	 * @see getTriggersOverview()
	 *
	 * @param array  trigger
	 * @param string pageFile		the page where the element is displayed
	 * @param string screenId
	 *
	 * @return CCol
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CCol getTriggerOverviewCells(IIdentityBean idBean, SQLExecutor executor, CArray trigger, String pageFile, String screenId) {
		Object ack = null, css = null, style = null;
		CArray desc = array(),
				//_menuPopup = array(),
				triggerItems = array(),
				acknowledge = array();
	
		// for how long triggers should blink on status change (set by user in administration->general)
		Map config = select_config(idBean, executor);
	
		if (!empty(trigger)) {
			style = "cursor: pointer; ";
	
			// problem trigger
			if (Nest.value(trigger,"value").asInteger() == TRIGGER_VALUE_TRUE) {
				css = getSeverityStyle(Nest.value(trigger,"priority").asInteger());
				ack = null;
	
				if (Nest.value(config,"event_ack_enable").asInteger() == 1) {
					Map event;
					if (!empty(event = get_last_event_by_triggerid(idBean, executor, Nest.value(trigger,"triggerid").asString()))) {
						if (!empty(screenId)) {
							acknowledge = map(
								"eventid", Nest.value(event,"eventid").$(),
								"screenid", screenId,
								"backurl", pageFile
							);
						} else {
							acknowledge = array(
								"eventid", Nest.value(event,"eventid").$(),
								"backurl", "overview.action"
							);
						}
	
						if (Nest.value(event,"acknowledged").asInteger() == 1) {
							ack = new CImg("images/general/tick.png", "ack");
						}
					}
				}
			}
			// ok trigger
			else {
				css = "normal";
			}
	
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbItems = DBfetchArray(DBselect(executor,
				"SELECT DISTINCT i.itemid,i.hostid,i.name,i.key_,i.value_type"+
				" FROM items i,functions f"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items", "i")+
				    " AND f.itemid=i.itemid"+
					" AND f.triggerid="+sqlParts.marshalParam(Nest.value(trigger,"triggerid").$()),
				sqlParts.getNamedParams()
			));
	
			dbItems = CMacrosResolverHelper.resolveItemNames(idBean, executor, dbItems);
			for(Map dbItem: dbItems) {
				triggerItems.add(map(
					"name", Nest.value(dbItem,"name_expanded").$(),
					"params", map(
						"action", in_array(Nest.value(dbItem,"value_type").$(), array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64))
							? "showgraph" : "showlatest",
						"itemid", Nest.value(dbItem,"itemid").$(),
						"period", 3600
					)
				));
			}
	
			// dependency: triggers on which depends this
			Object triggerId = empty(Nest.value(trigger,"triggerid").$()) ? 0 : Nest.value(trigger,"triggerid").$();
	
			// trigger dependency DOWN
			CTableInfo dependencyTable = new CTableInfo();
			dependencyTable.setAttribute("style", "width: 200px;");
			dependencyTable.addRow(bold(_("Depends on")+NAME_DELIMITER));
	
			boolean isDependencyFound = false;
			CArray<Map> dbDependencies = DBselect(executor, 
					"SELECT td.* FROM trigger_depends td"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "trigger_depends", "td")+
					" AND td.triggerid_down=#{triggerid}"+sqlParts.marshalParam(triggerId), 
					sqlParts.getNamedParams());
			for (Map _dbDependency: dbDependencies) {
				dependencyTable.addRow(SPACE+"-"+SPACE+CMacrosResolverHelper.resolveTriggerNameById(idBean, executor, Nest.value(_dbDependency,"triggerid_up").asLong()));
				isDependencyFound = true;
			}
	
			if (isDependencyFound) {
				CImg icon = new CImg("images/general/arrow_down2.png", "DEP_DOWN");
				icon.setAttribute("style", "vertical-align: middle; border: 0px;");
				icon.setHint(dependencyTable, "", "", false);
	
				desc.add( icon );
			}
	
			// trigger dependency UP
			dependencyTable = new CTableInfo();
			dependencyTable.setAttribute("style", "width: 200px;");
			dependencyTable.addRow(bold(_("Dependent")+NAME_DELIMITER));
	
			isDependencyFound = false;
			dbDependencies = DBselect(executor, 
					"SELECT td.* FROM trigger_depends td"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "trigger_depends", "td")+
					" AND td.triggerid_up="+sqlParts.marshalParam(triggerId), 
					sqlParts.getNamedParams());
			for (Map dbDependency: dbDependencies) {
				dependencyTable.addRow(SPACE+"-"+SPACE+CMacrosResolverHelper.resolveTriggerNameById(idBean, executor, Nest.value(dbDependency,"triggerid_down").asInteger()));
				isDependencyFound = true;
			}
	
			if (isDependencyFound) {
				CImg icon = new CImg("images/general/arrow_up2.png", "DEP_UP");
				icon.setAttribute("style", "vertical-align: middle; border: none;");
				icon.setHint(dependencyTable, "", "", false);
	
				desc.add( icon );
			}
		}
	
		CCol column = ((isArray(desc) && count(desc) > 0) || !empty(ack))
			? new CCol(array(desc, ack), css+" hosts")
			: new CCol(SPACE, css+" hosts");
	
		column.setAttribute("style", style);
	
		if (!empty(trigger) && Nest.value(config,"blink_period").asInteger() > 0 && time() - Nest.value(trigger,"lastchange").asInteger() < Nest.value(config,"blink_period").asInteger()) {
			column.addClass("blink");
			column.setAttribute("data-toggle-class", css);
		}
	
		if (!empty(trigger)) {
			column.setMenuPopup(getMenuPopupTrigger(trigger, triggerItems, acknowledge));
		}
	
		return column;
	}
	
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray calculate_availability(IIdentityBean idBean, SQLExecutor executor, String _triggerid, long period_start, long period_end) {
		CArray _ret = array();
		int start_value = TRIGGER_VALUE_FALSE;
		Long _min = 0L, max = 0L;
		String _sql;
		SqlBuilder sqlParts = null;
		if (period_start > 0 && period_start <= time()) {
			sqlParts = new SqlBuilder();
			_sql = "SELECT e.eventid,e.value"+
					" FROM events e"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
					    " AND e.objectid="+sqlParts.marshalParam(_triggerid)+
						" AND e.source="+EVENT_SOURCE_TRIGGERS+
						" AND e.object="+EVENT_OBJECT_TRIGGER+
						" AND e.clock<"+sqlParts.marshalParam(period_start)+
					" ORDER BY e.eventid DESC";
			Map _row;
			if (!empty(_row = DBfetch(DBselect(executor, _sql, 1, sqlParts.getNamedParams())))) {
				start_value = Nest.value(_row,"value").asInteger();
				_min = period_start;
			}
		}
	
		sqlParts = new SqlBuilder();
		_sql = "SELECT COUNT(e.eventid) AS cnt,MIN(e.clock) AS min_clock,MAX(e.clock) AS max_clock"+
				" FROM events e"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
				    " AND e.objectid="+sqlParts.marshalParam(_triggerid)+
					" AND e.source="+EVENT_SOURCE_TRIGGERS+
					" AND e.object="+EVENT_OBJECT_TRIGGER;
		if (period_start != 0) {
			_sql += " AND clock>="+sqlParts.marshalParam(period_start);
		}
		if (period_end != 0) {
			_sql += " AND clock<="+sqlParts.marshalParam(period_end);
		}
	
		CArray<Map> db_events = CArray.valueOf( DBfetch(DBselect(executor, _sql, sqlParts.getNamedParams())) );
		if (Nest.value(db_events,"cnt").asInteger() > 0) {
			if (empty(_min)) {
				_min = Nest.value(db_events,"min_clock").asLong();
			}
			max = Nest.value(db_events,"max_clock").asLong();
		} else {
			if (period_start == 0 && period_end == 0) {
				max = time();
				_min = max - SEC_PER_DAY;
			} else {
				Nest.value(_ret,"true_time").$(0);
				Nest.value(_ret,"false_time").$(0);
				Nest.value(_ret,"true").$((TRIGGER_VALUE_TRUE == start_value) ? 100 : 0);
				Nest.value(_ret,"false").$((TRIGGER_VALUE_FALSE == start_value) ? 100 : 0);
				return _ret;
			}
		}
	
		int state = start_value;
		long true_time = 0;
		long false_time = 0;
		long time = _min;
		if (period_start == 0 && period_end == 0) {
			max = time();
		}
		if (period_end == 0) {
			period_end = max;
		}
	
		int rows = 0;
		sqlParts = new SqlBuilder();
		db_events = DBselect(executor,
			"SELECT e.eventid,e.clock,e.value"+
			" FROM events e"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
			    " AND e.objectid="+sqlParts.marshalParam(_triggerid)+
				" AND e.source="+EVENT_SOURCE_TRIGGERS+
				" AND e.object="+EVENT_OBJECT_TRIGGER+
				" AND e.clock BETWEEN "+_min+" AND "+max+
			" ORDER BY e.eventid",
			sqlParts.getNamedParams()
		);
		for (Map row: db_events) {
			long clock = Nest.value(row,"clock").asLong();
			int value = Nest.value(row,"value").asInteger();
	
			long diff = clock - time;
			time = clock;
	
			if (state == 0) {
				false_time += diff;
				state = value;
			} else if (state == 1) {
				true_time += diff;
				state = value;
			}
			rows++;
		}
	
		if (rows == 0) {
			Map trigger = get_trigger_by_triggerid(idBean, executor, _triggerid);
			state = Nest.value(trigger,"value").asInteger();
		}
	
		if (state == TRIGGER_VALUE_FALSE) {
			false_time = false_time + period_end - time;
		} else if (state == TRIGGER_VALUE_TRUE) {
			true_time = true_time + period_end - time;
		}
		long total_time = true_time + false_time;
	
		if (total_time == 0) {
			Nest.value(_ret,"true_time").$(0);
			Nest.value(_ret,"false_time").$(0);
			Nest.value(_ret,"true").$(0f);
			Nest.value(_ret,"false").$(0f);
		} else {
			Nest.value(_ret,"true_time").$(true_time);
			Nest.value(_ret,"false_time").$(false_time);
			Nest.value(_ret,"true").$((float)(100 * true_time) / (float)total_time);
			Nest.value(_ret,"false").$((float)(100 * false_time) / (float)total_time);
		}
	
		return _ret;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Map> get_triggers_unacknowledged(IIdentityBean idBean, SQLExecutor executor,CArray db_element) {
		return get_triggers_unacknowledged(idBean, executor, db_element, false);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Map> get_triggers_unacknowledged(IIdentityBean idBean, SQLExecutor executor,CArray db_element, boolean count_problems) {
		return get_triggers_unacknowledged(idBean, executor, db_element, count_problems, false);
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Map> get_triggers_unacknowledged(IIdentityBean idBean, SQLExecutor executor, CArray db_element, boolean count_problems, boolean ack) {
		CArray elements = map(
			"hosts", array(),
			"hosts_groups", array(),
			"triggers", array()
		);
	
		get_map_elements(idBean, executor, db_element, elements);
		if (empty(Nest.value(elements,"hosts_groups").$()) && empty(Nest.value(elements,"hosts").$()) && empty(Nest.value(elements,"triggers").$())) {
			return null;
		}
	
		Map config = select_config(idBean, executor);
	
		CTriggerGet toptions = new CTriggerGet();
		toptions.setMonitored(true);
		toptions.setCountOutput(true);
		toptions.setFilter(array());
		toptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
	
		if (ack) {
			toptions.setWithAcknowledgedEvents(true);
		} else {
			toptions.setWithUnacknowledgedEvents(true);
		}
	
		if (count_problems) {
			toptions.setFilter("value", Nest.as(TRIGGER_VALUE_TRUE).asString());
		}
		if (!empty(Nest.value(elements,"hosts_groups").$())) {
			toptions.setGroupIds(array_unique(Nest.value(elements,"hosts_groups").asCArray()).valuesAsLong());
		}
		if (!empty(Nest.value(elements,"hosts").$())) {
			toptions.setHostIds(array_unique(Nest.value(elements,"hosts").asCArray()).valuesAsLong());
		}
		if (!empty(Nest.value(elements,"triggers").$())) {
			toptions.setTriggerIds(array_unique(Nest.value(elements,"triggers").asCArray()).valuesAsLong());
		}
		return API.Trigger(idBean, executor).get(toptions);
	}
	
	
	@CodeConfirmed("blue.2.2.5")
	public static CTableInfo make_trigger_details(IIdentityBean idBean, SQLExecutor executor, Map trigger) {
		Map hosts = reset(Nest.value(trigger,"hosts").asCArray());
		Long hostId = Nest.value(hosts,"hostid").asLong();
	
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"name", "hostid", "status"});
		hoptions.setHostIds(hostId);
		hoptions.setSelectScreens(API_OUTPUT_COUNT);
		hoptions.setPreserveKeys(true);
		hosts = API.Host(idBean, executor).get(hoptions);
		Map host = reset(hosts);
	
		CArray<CArray<Map>> scripts = API.Script(idBean, executor).getScriptsByHosts(hostId);
	
		CSpan hostName = new CSpan(Nest.value(host,"name").$());//删除link样式
		hostName.setMenuPopup(getMenuPopupHost(host, !empty(scripts) ? CArray.valueOf(reset(scripts)) : null));
	
		CTableInfo table = new CTableInfo();
		table.addRow(array(_("Host"), hostName));
		table.addRow(array(_("Trigger"), CMacrosResolverHelper.resolveTriggerName(idBean, executor, trigger)));
		table.addRow(array(_("Severity"), getSeverityCell(idBean, executor, Nest.value(trigger,"priority").asInteger())));
		table.addRow(array(_("Expression"), explode_exp(idBean, executor, Nest.value(trigger,"expression").asString(), true, true)));
		table.addRow(array(_("Event generation"), _("Normal")+((TRIGGER_MULT_EVENT_ENABLED == Nest.value(trigger,"type").asInteger())
			? SPACE+"+"+SPACE+_("Multiple PROBLEM events") : "")));
		table.addRow(array(_("Disabled"), ((TRIGGER_STATUS_ENABLED == Nest.value(trigger,"status").asInteger())
			? new CCol(_("No"), "off") : new CCol(_("Yes"), "on"))));
	
		return table;
	}

	
	/**
	 * Analyze an expression and returns expression html tree
	 *
	 * @param string _expression
	 *
	 * @return array
	 */
	@CodeConfirmed("benne.2.2.7")
	public static CArray analyzeExpression(IIdentityBean idBean, SQLExecutor executor, String expression) {
		if (empty(expression)) {
			return array("", null);
		}
	
		CTriggerExpression expressionData = new CTriggerExpression();
		if (!expressionData.parse(expression)) {
			error(expressionData.error);
			return null;
		}
	
		CArray expressionTree = array();
		expressionTree.add(getExpressionTree(expressionData, 0, strlen(expressionData.expression) - 1) );
	
		CArray next = array();
		TObj<Integer> letterNum = TObj.as(0);
		return buildExpressionHtmlTree(idBean, executor, expressionTree, next, letterNum);
	}
	
	@CodeConfirmed("benne.2.2.7")
	public static CArray buildExpressionHtmlTree(IIdentityBean idBean, SQLExecutor executor, CArray expressionTree, CArray next, TObj letterNum) {
		return buildExpressionHtmlTree(idBean, executor, expressionTree, next, letterNum, 0);
	}
	
	@CodeConfirmed("benne.2.2.7")
	public static CArray buildExpressionHtmlTree(IIdentityBean idBean, SQLExecutor executor, CArray expressionTree, CArray next, TObj letterNum, int level) {
		return buildExpressionHtmlTree(idBean, executor, expressionTree, next, letterNum, level, null);
	}
	
	/**
	 * Builds expression html tree
	 *
	 * @param array _expressionTree output of getExpressionTree() function
	 * @param array _next           parameter only for recursive call; should be empty array
	 * @param int _letterNum        parameter only for recursive call; should be 0
	 * @param int _level            parameter only for recursive call
	 * @param string _operand       parameter only for recursive call
	 *
	 * @return bool                 returns true if element is found, false - otherwise
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray buildExpressionHtmlTree(IIdentityBean idBean, SQLExecutor executor, CArray<Map> expressionTree, CArray next, TObj letterNum, int level, String operand) {
		CArray treeList = array();
		String outline = "";
	
		String[] keys = expressionTree.keys();
		String lastKey = (keys != null && keys.length > 0) ? keys[keys.length - 1] : null;
			
		for(Entry<Object, Map> entry: expressionTree.entrySet()) {
			String key = Nest.as(entry.getKey()).asString();
			Map element = entry.getValue();
			
			String type = Nest.value(element,"type").asString();
			
			if("operand".equals(type)) {
				next.put(level, (!key.equals(lastKey)));
				CArray expr = expressionLevelDraw(next, level);
				expr.add(SPACE );
				expr.add(italic("&".equals(Nest.value(element,"operand").asString()) ? _("AND") : _("OR")) );
				CArray levelDetails = map(
					"list", expr,
					"id", Nest.value(element,"id").$(),
					"expression", map(
						"value", Nest.value(element,"expression").$()
					)
				);

				CArray levelErrors = expressionHighLevelErrors(idBean, executor, Nest.value(element,"expression").asString());
				if (count(levelErrors) > 0) {
					Nest.value(levelDetails,"expression","levelErrors").$(levelErrors);
				}
				treeList.add(levelDetails );

				CArray buildExpressionHtmlTree = buildExpressionHtmlTree(idBean, executor, Nest.value(element,"elements").asCArray(), next, letterNum, level + 1, Nest.value(element,"operand").asString());
				String subOutline = Nest.value(buildExpressionHtmlTree,0).asString();
				CArray subTreeList = Nest.value(buildExpressionHtmlTree,1).asCArray();
				treeList = array_merge(treeList, subTreeList);

				outline += (level == 0) ? subOutline : "("+subOutline+")";
				if (operand != null && !empty(next.get(level))) {
					outline += " "+operand+" ";
				}
			}else if("expression".equals(type)) {
				next.put(level, (!key.equals(lastKey)));
				
				String letter = num2letter(letterNum.asInteger());
				letterNum.$(letterNum.asInteger()+1);
				outline += letter;
				if (operand != null && !empty(next.get(level))) {
					outline += " "+operand+" ";
				}

				CSpan url;
				if (defined("NO_LINK_IN_TESTING")) {
					url = new CSpan(Nest.value(element,"expression").$());
				} else {
					String _expressionId = "expr_"+Nest.value(element,"id").$();

					url = new CSpan(Nest.value(element,"expression").$(), "link");
					url.setAttribute("id", _expressionId);
					url.setAttribute("onclick", "javascript: copy_expression(\""+_expressionId+"\");");
				}
				CArray expr = expressionLevelDraw(next, level);
				expr.add(SPACE);
				expr.add(bold(letter));
				expr.add(SPACE);
				expr.add(url);

				CArray levelDetails = map(
					"list", expr,
					"id", Nest.value(element,"id").$(),
					"expression", map(
						"value", Nest.value(element,"expression").$()
					)
				);

				CArray levelErrors = expressionHighLevelErrors(idBean, executor, Nest.value(element,"expression").asString());
				if (count(levelErrors) > 0) {
					Nest.value(levelDetails,"expression","levelErrors").$(levelErrors);
				}
				treeList.add( levelDetails );
			}
		}
		return array(outline, treeList);
	}

	private static CArray errors, definedErrorPhrases;
	@CodeConfirmed("blue.2.2.5")
	public static CArray expressionHighLevelErrors(IIdentityBean idBean, SQLExecutor executor, String expression) {
		if (!isset(errors)) {
			definedErrorPhrases = map(
				EXPRESSION_HOST_UNKNOWN, _("Unknown host, no such host present in system"),
				EXPRESSION_HOST_ITEM_UNKNOWN, _("Unknown host item, no such item in selected host"),
				EXPRESSION_NOT_A_MACRO_ERROR, _("Given expression is not a macro"),
				EXPRESSION_FUNCTION_UNKNOWN, _("Incorrect function is used")
			);
			errors = array();
		}
	
		if (!isset(errors.get(expression))) {
			errors.put(expression, array());
			CTriggerExpression _expressionData = new CTriggerExpression();
			if (_expressionData.parse(expression)) {
				for(Map _exprPart: _expressionData.expressions) {
					Object _info = get_item_function_info(idBean, executor, Nest.value(_exprPart,"expression").asString());
	
					if (!isArray(_info) && isset(definedErrorPhrases.get(_info))) {
						if (!isset(errors.getNested(expression, _exprPart.get("expression")))) {
							errors.put(expression, _exprPart.get("expression"), definedErrorPhrases.get(_info));
						}
					}
				}
			}
		}
	
		CArray _ret = array();
		if (count(errors.get(expression)) == 0) {
			return _ret;
		}
	
		CTriggerExpression _expressionData = new CTriggerExpression();
		if (_expressionData.parse(expression)) {
			for(Map _exprPart: _expressionData.expressions) {
				if (isset(errors.getNested(expression, _exprPart.get("expression")))) {
					_ret.put(_exprPart.get("expression"), errors.getNested(expression, _exprPart.get("expression")));
				}
			}
		}
		return _ret;
	}
		
	
	/**
	 * Draw level for trigger expression builder tree
	 *
	 * @param array _next
	 * @param int _level
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray expressionLevelDraw(CArray next, int level) {
		CArray expr = array();
		for (int i = 1; i <= level; i++) {
			String _image;
			if (i == level) {
				_image = Nest.value(next, i).asBoolean() ? "top_right_bottom" : "top_right";
			} else {
				_image = Nest.value(next, i).asBoolean() ? "top_bottom" : "space";
			}
			expr.add( new CImg("images/general/tr_"+_image+".gif", "tr", 12, 12) );
		}
		return expr;
	}
	
	
	/**
	 * Returns number of elements in a trigger expression
	 * Element is expression between two operands.
	 *
	 * For example:
	 * expression \"{host.key.last(0)}=0 & ({host2:key.last(0)}=0 & {host3.key.last(0)}=0)\" has two elements:
	 * \"{host.key.last(0)}=0\" and \"({host2:key.last(0)}=0 & {host3.key.last(0)}=0)\"
	 *
	 * @param CTriggerExpression _expressionData
	 * @param int _start
	 * @param int _end
	 *
	 * @return integer
	 */
	@CodeConfirmed("blue.2.2.5")
	public static int getExpressionElementsNum(CTriggerExpression _expressionData, int _start, int _end) {
		int _expressionElementsNum = 1;
		for (int i = _start, _level = 0; i <= _end; i++) {
			switch (_expressionData.expression.charAt(i)) {
				case '(':
					_level++;
					break;
				case ')':
					_level--;
					break;
				case '|':
				case '&':
					if (_level == 0) {
						_expressionElementsNum++;
					}
					break;
				case '{':
					for(Map _exprPart: _expressionData.expressions) {
						if (Nest.value(_exprPart,"pos").asInteger() == i) {
							i += strlen(Nest.value(_exprPart,"expression").asString()) - 1;
							break;
						}
					}
					break;
			}
		}
	
		return _expressionElementsNum;
	}
		
	
	/**
	 * Makes tree of expression elements
	 *
	 * Expression:
	 *   \"{host1:system.cpu.util[,iowait].last(0)} > 50 & {host2:system.cpu.util[,iowait].last(0)} > 50\"
	 * Result:
	 *   array(
	 *     [0] => array(
	 *       "id" => "0_92",
	 *       "type" => "operand",
	 *       "operand" => "&",
	 *       "elements" => array(
	 *         [0] => array(
	 *           "id" => "0_44",
	 *           "type" => "expression",
	 *           "expression" => "{host1:system.cpu.util[,iowait].last(0)} > 50"
	 *         ),
	 *         [1] => array(
	 *           "id" => "48_92",
	 *           "type" => "expression",
	 *           "expression" => "{host2:system.cpu.util[,iowait].last(0)} > 50"
	 *         )
	 *       )
	 *     )
	 *   )
	 *
	 * @param CTriggerExpression _expressionData
	 * @param int start
	 * @param int end
	 *
	 * @return array
	 */
	@CodeConfirmed("benne.2.2.7")
	public static CArray getExpressionTree(CTriggerExpression expressionData, int start, int end) {
		CArray expressionTree = array();
	
		for (char operand : new char[] { '|', '&' }) {
			boolean operandFound = false;
			int lParentheses = -1;
			int rParentheses = -1;
			CArray expressions = array();
			int openSymbolNum = start;
	
			for (int i = start, level = 0; i <= end; i++) {
				char c = expressionData.expression.charAt(i);
				if (' ' == c) {
					if (openSymbolNum == i) {
						openSymbolNum++;
					}
				} else if ('(' == c) {
					if (level == 0) {
						lParentheses = i;
					}
					level++;
				} else if (')' == c) {
					level--;
					if (level == 0) {
						rParentheses = i;
					}
				} else if (operand == c) {
					if (level == 0) {
						int closeSymbolNum = i - 1;
						while (expressionData.expression.charAt(closeSymbolNum) == ' ') {
							closeSymbolNum--;
						}

						int expressionElementsNum = getExpressionElementsNum(expressionData, openSymbolNum, closeSymbolNum);
						if (expressionElementsNum == 1 && openSymbolNum == lParentheses && closeSymbolNum == rParentheses) {
							openSymbolNum++;
							closeSymbolNum--;
						}

						expressions.add(getExpressionTree(expressionData, openSymbolNum, closeSymbolNum) );
						openSymbolNum = i + 1;
						operandFound = true;
					}
				} else if ('{' == c) {
					for(Map _exprPart: expressionData.expressions) {
						if (Nest.value(_exprPart,"pos").asInteger() == i) {
							i += strlen(Nest.value(_exprPart,"expression").asString()) - 1;
							break;
						}
					}
				}
			}
	
			int closeSymbolNum = end;
			while (expressionData.expression.charAt(closeSymbolNum) == ' ') {
				closeSymbolNum--;
			}
	
			if (operandFound) {
				int expressionElementsNum = getExpressionElementsNum(expressionData, openSymbolNum, closeSymbolNum);
				if (expressionElementsNum == 1 && openSymbolNum == lParentheses && closeSymbolNum == rParentheses) {
					openSymbolNum++;
					closeSymbolNum--;
				}
	
				expressions.add(getExpressionTree(expressionData, openSymbolNum, closeSymbolNum));
	
				openSymbolNum = start;
				while (expressionData.expression.charAt(openSymbolNum) == ' ') {
					openSymbolNum++;
				}
	
				closeSymbolNum = end;
				while (expressionData.expression.charAt(closeSymbolNum) == ' ') {
					closeSymbolNum--;
				}
	
				expressionTree = map(
					"id", openSymbolNum+"_"+closeSymbolNum,
					"expression", substr(expressionData.expression, openSymbolNum, closeSymbolNum - openSymbolNum + 1),
					"type", "operand",
					"operand", operand,
					"elements", expressions
				);
				break;
			} else if (operand == '&') {
				if (openSymbolNum == lParentheses && closeSymbolNum == rParentheses) {
					openSymbolNum++;
					closeSymbolNum--;
	
					expressionTree = getExpressionTree(expressionData, openSymbolNum, closeSymbolNum);
				} else {
					expressionTree = map(
						"id", openSymbolNum+"_"+closeSymbolNum,
						"expression", substr(expressionData.expression, openSymbolNum, closeSymbolNum - openSymbolNum + 1),
						"type", "expression"
					);
				}
			}
		}
	
		return expressionTree;
	}
	
	/**
	 * Recreate an expression depending on action
	 *
	 * @param string _expression
	 * @param string _expressionId  element identifier like \"0_55\"
	 * @param string _action        one of &/|/r/R (AND/OR/replace/Remove)
	 * @param string _newExpression expression for AND, OR or replace actions
	 *
	 * @return bool                 returns new expression or false if expression is incorrect
	 */
	@CodeConfirmed("benne.2.2.7")
	public static String remakeExpression(String expression, String expressionId, String action, String newExpression) {
		if (empty(expression)) {
			return null;
		}
	
		CTriggerExpression expressionData = new CTriggerExpression();
		if (!"R".equals(action) && !expressionData.parse(newExpression)) {
			error(expressionData.error);
			return null;
		}
	
		if (!expressionData.parse(expression)) {
			error(expressionData.error);
			return null;
		}
	
		CArray expressionTree = array();
		expressionTree.add(getExpressionTree(expressionData, 0, strlen(expressionData.expression) - 1) );
	
		if (rebuildExpressionTree(expressionTree, expressionId, action, newExpression)) {
			expression = makeExpression(expressionTree);
		}
		return expression;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static boolean rebuildExpressionTree(CArray _expressionTree, String _expressionId, String _action, String _newExpression) {
		return rebuildExpressionTree(_expressionTree, _expressionId, _action, _newExpression, null);
	}
	/**
	 * Rebuild expression depending on action
	 *
	 * Example:
	 *   _expressionTree = array(
	 *     [0] => array(
	 *       "id" => "0_92",
	 *       "type" => "operand",
	 *       "operand" => "&",
	 *       "elements" => array(
	 *         [0] => array(
	 *           "id" => "0_44",
	 *           "type" => "expression",
	 *           "expression" => "{host1:system.cpu.util[,iowait].last(0)} > 50"
	 *         ),
	 *         [1] => array(
	 *           "id" => "48_92",
	 *           "type" => "expression",
	 *           "expression" => "{host2:system.cpu.util[,iowait].last(0)} > 50"
	 *         )
	 *       )
	 *     )
	 *   )
	 *   _action = "R"
	 *   _expressionId = "48_92"
	 *
	 * Result:
	 *   _expressionTree = array(
	 *     [0] => array(
	 *       "id" => "0_44",
	 *       "type" => "expression",
	 *       "expression" => "{host1:system.cpu.util[,iowait].last(0)} > 50"
	 *     )
	 *   )
	 *
	 * @param array _expressionTree
	 * @param string _expressionId  element identifier like \"0_55\"
	 * @param string _action        one of &/|/r/R (AND/OR/replace/Remove)
	 * @param string _newExpression expression for AND, OR or replace actions
	 * @param string _operand       parameter only for recursive call
	 *
	 * @return bool                 returns true if element is found, false - otherwise
	 */
	@CodeConfirmed("blue.2.2.5")
	public static boolean rebuildExpressionTree(CArray<Map> expressionTree, String expressionId, String action, String newExpression, String operand ) {
		
		for(Entry<Object, Map> entry: expressionTree.entrySet()) {
			Object key = entry.getKey();
			Map expression = entry.getValue();
		
			if (expressionId.equals(Nest.value(expression, "id").asString())) {
				switch (action.charAt(0)) {
					// AND and OR
					case '&':
					case '|':
						Object _type = expression.get("type");
						if("operand".equals(_type)) {
							if (action.equals(expression.get("operand"))) {
								((CArray)expression.get("elements")).add( map(
									"expression", newExpression,
									"type", "expression"
								));
							} else {
								CArray _element = map(
									"type", "operand",
									"operand", action,
									"elements", array(
										expression,
										map(
											"expression", newExpression,
											"type", "expression"
										)
									)
								);
								entry.setValue(_element);
							}
						}else if("expression".equals(_type)) {
							if (empty(operand) || !operand.equals(action)) {
								CArray _element = map(
									"type", "operand",
									"operand", action,
									"elements", array(
										expression,
										map(
											"expression", newExpression,
											"type", "expression"
										)
									)
								);
								entry.setValue(_element);
							}
							else {
								expressionTree.add( map(
									"expression", newExpression,
									"type", "expression"
								));
							}
						}
						break;
					// replace
					case 'r':
						expression.put("expression", newExpression);
						if ("operand".equals(expression.get("type"))) {
							expression.put("type", "expression");
							unset(expression, "operand");
							unset(expression, "elements");
						}
						break;
					// remove
					case 'R':
						unset(expressionTree, key);
						break;
				}
				return true;
			}
	
			if ("operand".equals(expression.get("type"))) {
				if (rebuildExpressionTree((CArray)expression.get("elements"), expressionId, action, newExpression, Nest.value(expression,"operand").asString())) {
					return true;
				}
			}
		}
	
		return false;
	}
		
	@CodeConfirmed("benne.2.2.7")
	public static String makeExpression(CArray expressionTree) {
		return makeExpression(expressionTree, 0);
	}
	
	@CodeConfirmed("benne.2.2.7")
	public static String makeExpression(CArray expressionTree, int _level) {
		return makeExpression(expressionTree, _level, null);
	}
	
	/**
	 * Makes expression by expression tree
	 *
	 * Example:
	 *   _expressionTree = array(
	 *     [0] => array(
	 *       "type" => "operand",
	 *       "operand" => "&",
	 *       "elements" => array(
	 *         [0] => array(
	 *           "type" => "expression",
	 *           "expression" => "{host1:system.cpu.util[,iowait].last(0)} > 50"
	 *         ),
	 *         [1] => array(
	 *           "type" => "expression",
	 *           "expression" => "{host2:system.cpu.util[,iowait].last(0)} > 50"
	 *         )
	 *       )
	 *     )
	 *   )
	 *
	 * Result:
	 *   \"{host1:system.cpu.util[,iowait].last(0)} > 50 & {host2:system.cpu.util[,iowait].last(0)} > 50\"
	 *
	 * @param array  _expressionTree
	 * @param int    _level				parameter only for recursive call
	 * @param string _operand			parameter only for recursive call
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String makeExpression(CArray<Map> expressionTree, int level, String operand) {
		String expression = "";
	
		String[] keys = expressionTree.keys();
		String lastKey = (keys != null && keys.length > 0) ? keys[keys.length - 1] : null;
	
		for(Entry<Object, Map> entry: expressionTree.entrySet()) {
			String key = Nest.as(entry.getKey()).asString();
			Map element = entry.getValue();
			
			String type = asString(element.get("type"));
			if("operand".equals(type)) {
				String _subExpression = makeExpression(Nest.value(element,"elements").asCArray(), level + 1, Nest.value(element,"operand").asString());
				
				expression += (level == 0) ? _subExpression : "("+_subExpression+")";
			} else if ("expression".equals(type)) {
				expression += Nest.value(element,"expression").$();
			}
			if (operand != null && !key.equals(lastKey)) {
				expression += " "+operand+" ";
			}
		}
	
		return expression;
	}
		
	public static Object get_item_function_info(IIdentityBean idBean, SQLExecutor executor, String expr) {
		CArray<String> value_type = map(
			ITEM_VALUE_TYPE_UINT64	, _("Numeric (integer 64bit)"),
			ITEM_VALUE_TYPE_FLOAT	, _("Numeric (float)"),
			ITEM_VALUE_TYPE_STR		, _("Character"),
			ITEM_VALUE_TYPE_LOG		, _("Log"),
			ITEM_VALUE_TYPE_TEXT	, _("Text")
		);
	
		CArray<Integer> type_of_value_type = map(
			ITEM_VALUE_TYPE_UINT64	, T_RDA_INT,
			ITEM_VALUE_TYPE_FLOAT	, T_RDA_DBL_BIG,
			ITEM_VALUE_TYPE_STR		, T_RDA_STR,
			ITEM_VALUE_TYPE_LOG		, T_RDA_STR,
			ITEM_VALUE_TYPE_TEXT	, T_RDA_STR
		);
	
		CArray<Map> function_info = map(
			"band" ,			map("value_type" , _("Numeric (integer 64bit)"),	"type" , T_RDA_INT, 			"validation" , NOT_EMPTY),
			"abschange" ,	map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"avg" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"change" ,		map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"count" ,			map("value_type" , _("Numeric (integer 64bit)"), 	"type" , T_RDA_INT, 			"validation" , NOT_EMPTY),
			"date" ,				map("value_type" , "YYYYMMDD",						"type" , T_RDA_INT,				"validation" , "{}>=20100101&&{}<=99991231"),
			"dayofmonth" ,	map("value_type" , "1-31",							"type" , T_RDA_INT,				"validation" , "{}>=1&&{}<=31"),
			"dayofweek" ,	map("value_type" , "1-7",							"type" , T_RDA_INT,				"validation" , IN("1,2,3,4,5,6,7")),
			"delta" ,			map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"diff" ,				map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"fuzzytime" ,		map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"iregexp" ,		map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"last" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"logeventid" ,	map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"logseverity" ,	map("value_type" , _("Numeric (integer 64bit)"), 	"type" , T_RDA_INT, 			"validation" , NOT_EMPTY),
			"logsource" ,		map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"max" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"min" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"nodata" ,			map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"now" ,				map("value_type" , _("Numeric (integer 64bit)"), 	"type" , T_RDA_INT, 			"validation" , NOT_EMPTY),
			"prev" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"regexp" ,			map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"str" ,				map("value_type" , _("0 or 1"),						"type" , T_RDA_INT,				"validation" , IN("0,1")),
			"strlen" ,			map("value_type" , _("Numeric (integer 64bit)"), 	"type" , T_RDA_INT, 			"validation" , NOT_EMPTY),
			"sum" ,				map("value_type" , value_type,						"type" , type_of_value_type,	"validation" , NOT_EMPTY),
			"time" ,				map("value_type" , "HHMMSS",						"type" , T_RDA_INT,				"validation" , "rda_strlen({})==6")
		);
	
		CTriggerExpression expressionData = new CTriggerExpression();
		Map result = null;
		if (expressionData.parse(expr)) {
			if (isset(expressionData.macros.get(0))) {
				result = map(
					"value_type", _("0 or 1"),
					"type", T_RDA_INT,
					"validation", IN("0,1")
				);
			} else if (isset(expressionData.usermacros.get(0)) || isset(expressionData.lldmacros.get(0))) {
				result = map(
					"value_type", value_type.get(ITEM_VALUE_TYPE_FLOAT),
					"type", T_RDA_STR,
					"validation", "preg_match(\"^"+RDA_PREG_NUMBER+"$\", {})"   //"preg_match(\"/^"+RDA_PREG_NUMBER+"$/u\", {})"
				);
			} else if (isset(expressionData.expressions.get(0))) {
				Map exprPart = reset(expressionData.expressions);
	
				if (!isset(function_info.get(exprPart.get("functionName")))) {
					return EXPRESSION_FUNCTION_UNKNOWN;
				}
	
				CHostGet hoptions = new CHostGet();
				hoptions.setFilter("host", Nest.value(exprPart,"host").asString());
				hoptions.setTemplatedHosts(true);
				CArray<Map> hostFound = API.Host(idBean, executor).get(hoptions);
	
				if (empty(hostFound)) {
					return EXPRESSION_HOST_UNKNOWN;
				}
	
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"value_type"});
				ioptions.setHostIds(rda_objectValues(hostFound, "hostid").valuesAsLong());
				ioptions.setFilter("key_", Nest.value(exprPart,"item").asString());
				ioptions.setWebItems(true);
				Map _itemFound = API.Item(idBean, executor).get(ioptions);
	
				if (empty(_itemFound)) {
					CItemPrototypeGet ipoptions = new CItemPrototypeGet();
					ipoptions.setOutput(new String[]{"value_type"});
					ipoptions.setHostIds(rda_objectValues(hostFound, "hostid").valuesAsLong());
					ipoptions.setFilter("key_", Nest.value(exprPart,"item").asString());
					_itemFound = API.ItemPrototype(idBean, executor).get(ipoptions);
	
					if (empty(_itemFound)) {
						return EXPRESSION_HOST_ITEM_UNKNOWN;
					}
				}
	
				_itemFound = reset(_itemFound);
				result = (CArray)function_info.get(exprPart.get("functionName"));
	
				if (isArray(Nest.value(result,"value_type").$())) {
					Nest.value(result,"value_type").$(Nest.value(result,"value_type", _itemFound.get("value_type")).$());
					Nest.value(result,"type").$(Nest.value(result, "type", _itemFound.get("value_type")).$());
	
					if (Nest.value(result,"type").asInteger() == T_RDA_INT) {
						Nest.value(result,"type").$(T_RDA_STR);
						Nest.value(result,"validation").$("preg_match(\"^"+RDA_PREG_NUMBER+"$\",{})");  //"preg_match(\"/^"+RDA_PREG_NUMBER+"$/u\",{})"
					}
				}
			} else {
				return EXPRESSION_NOT_A_MACRO_ERROR;
			}
		}
	
		return result;
	}

	/**
	 * Execute expression and return array with keys "result" as "TRUE" or "FALSE" and "error" as error text
	 * if there is one.
	 *
	 * @param string _expression
	 * @param array  _rplcts
	 *
	 * @return array
	 */
	@CodeTodo
	public static <T> T evalExpressionData(String _expression, CArray _rplcts) {
		Object _result = false;
	
		CArray _arr = array();
		String _evStr = str_replace(array_keys(_rplcts), array_values(_rplcts), _expression);
		preg_match_all("[0-9\\.]+["+RDA_BYTE_SUFFIXES+RDA_TIME_SUFFIXES+"]?", _evStr, _arr, PREG_OFFSET_CAPTURE);
	
		for (int i = count(_arr.get(0)) - 1; i >= 0; i--) {
			_evStr = substr_replace(_evStr, asString(convert(Nest.value(_arr, 0, i, 0).asString())), Nest.value(_arr, 0, i, 1).asInteger(), strlen(Nest.value(_arr, 0, i, 0).asString()));
		}
		
		if (preg_match(IPattern.compile("^[0-9.\\s=#()><+*\\/&E|\\-]+$", IPattern.DOTALL), _evStr) == 0) {
			return (T)map("result", "FALSE");
		}
	
		_evStr = preg_replace("(-?[0-9]*\\.?[0-9]+) *(\\=|\\#|\\!=|\\<|\\>) *(-?[0-9]*\\.?[0-9]+)", "($1 $2 $3)", _evStr); //"((float)$1 $2 (float)$3)"
	
		CArray _switch = map("=" , "==", "#" , "!=", "&" , "&&", "|" , "||");
		_evStr = str_replace(array_keys(_switch), array_values(_switch), _evStr);
	
		// execute expression
		_result = eval("return ("+trim(_evStr)+");"); //eval("_result = ("+trim(_evStr)+");");
	
		_result = (Boolean.TRUE.equals(_result) || !empty(_result) && "-".equals(_result)) ? "TRUE" : "FALSE";
		String _error = "";
	
		// remove eval() generated error message
		
		//TODO: $RDA_MESSAGES
//		if (!empty($RDA_MESSAGES)) {
//			CArray _messageList = array();
//	
//			for($RDA_MESSAGES as _rdaMessage) {
//				if (strpos(Nest.value(_rdaMessage,"message").$(), "eval()") !== false) {
//					_error = substr(Nest.value(_rdaMessage,"message").$(), 0, strpos(Nest.value(_rdaMessage,"message").$(), "["));
//					_result = "NULL";
//				}
//				else {
//					_messageList[] = _rdaMessage;
//				}
//			}
//	
//			$RDA_MESSAGES = _messageList;
//		}
	
		return (T)map(
			"result", _result,
			"error", _error
		);
	}
	
	public static void $CODE$SEPARATOR$END(){}
	
	/**
	 * Resolve {TRIGGER.ID} macro in trigger url.
	 *
	 * @param array  _trigger				trigger data with url and triggerid
	 * @param int    _trigger["triggerid"]
	 * @param string _trigger["url"]
	 *
	 * @return string
	 */
	public static String resolveTriggerUrl(Map trigger) {
		return str_replace("{TRIGGER.ID}", Nest.value(trigger,"triggerid").asString(), Nest.value(trigger,"url").asString());
	}
	
	public static String convert(String value) {
		value = trim(value);
		CArray arr = array();
		if (preg_match("(?<value>[\\-+]?[0-9]+[.]?[0-9]*)(?<mult>["+RDA_BYTE_SUFFIXES+RDA_TIME_SUFFIXES+"]?)", value, arr) == 0) {
			return value;
		}

		Double dvalue = Nest.value(arr, "value").asDouble();
		switch (Nest.value(arr, "mult").asChar()) {
			case 'T':
				dvalue *= 1024 * 1024 * 1024 * 1024;
				break;
			case 'G':
				dvalue *= 1024 * 1024 * 1024;
				break;
			case 'M':
				dvalue *= 1024 * 1024;
				break;
			case 'K':
				dvalue *= 1024;
				break;
			case 'm':
				dvalue *= 60;
				break;
			case 'h':
				dvalue *= 60 * 60;
				break;
			case 'd':
				dvalue *= 60 * 60 * 24;
				break;
			case 'w':
				dvalue *= 60 * 60 * 24 * 7;
				break;
		}
		return new BigDecimal(dvalue).toString();
	}
	
	/**
	 * Quoting _param if it contain special characters.
	 *
	 * @param string _param
	 *
	 * @return string
	 */
	public static String quoteFunctionParam(String param) {
		if ((param==null || param.length()==0) || (param.charAt(0)!='\\' && strpos(param, ",")==-1 && strpos(param, ")")==-1)) {
			return param;
		}	
		return "\""+str_replace("\"", "\\\"", param)+"\"";
	}
	
	/**
	 * Returns the text indicating the triggers status and state. If the _state parameter is not given, only the status of
	 * the trigger will be taken into account.
	 *
	 * @param int _status
	 * @param int _state
	 *
	 * @return string
	 */
	public static String triggerIndicator(int status) {
		return triggerIndicator(status, null);
	}
	
	/**
	 * Returns the text indicating the triggers status and state. If the _state parameter is not given, only the status of
	 * the trigger will be taken into account.
	 *
	 * @param int _status
	 * @param int _state
	 *
	 * @return string
	 */
	public static String triggerIndicator(int status, Integer state) {
		if (status == TRIGGER_STATUS_ENABLED) {
			return (state!=null && state == TRIGGER_STATE_UNKNOWN) ? _("Unknown") : _("Enabled");
		} else if (status == TRIGGER_STATUS_DISABLED) {
			return _("Disabled");
		}	
		return _("Unknown");
	}
	
	/**
	 * Returns the CSS class for the triggers status and state indicator. If the _state parameter is not given, only the
	 * status of the trigger will be taken into account.
	 *
	 * @param int _status
	 * @param int _state
	 *
	 * @return string
	 */
	public static String triggerIndicatorStyle(int status) {
		return triggerIndicatorStyle(status, null);
	}
	
	/**
	 * Returns the CSS class for the triggers status and state indicator. If the _state parameter is not given, only the
	 * status of the trigger will be taken into account.
	 *
	 * @param int _status
	 * @param int _state
	 *
	 * @return string
	 */
	public static String triggerIndicatorStyle(int status, Integer state) {
		if (status == TRIGGER_STATUS_ENABLED) {
			return (state!=null && state == TRIGGER_STATE_UNKNOWN) ? "unknown" : "enabled";
		} else if (status == TRIGGER_STATUS_DISABLED) {
			return "disabled";
		}
		return "unknown";
	}
	
	/**
	 * Orders trigger by both status and state. Triggers are sorted in the following order: enabled, disabled, unknown.
	 *
	 * Keep in sync with orderItemsByStatus().
	 *
	 * @param array  _triggers
	 * @param string _sortorder
	 */
	public static void orderTriggersByStatus(CArray<Map> triggers) {
		orderTriggersByStatus(triggers, RDA_SORT_UP);
	}
	
	/**
	 * Orders trigger by both status and state. Triggers are sorted in the following order: enabled, disabled, unknown.
	 *
	 * Keep in sync with orderItemsByStatus().
	 *
	 * @param array  _triggers
	 * @param string _sortorder
	 */
	public static void orderTriggersByStatus(CArray<Map> triggers, String sortorder) {
		CArray<Integer> sort = array();

		for (Entry<Object, Map> e : triggers.entrySet()) {
		    Object key = e.getKey();
		    Map trigger = e.getValue();
			Integer statusOrder = null;
			if (Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_ENABLED) {
				statusOrder = (Nest.value(trigger,"state").asInteger() == TRIGGER_STATE_UNKNOWN) ? 2 : 0;
			} else if (Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED) {
				statusOrder = 1;
			}
			Nest.value(sort,key).$(statusOrder);
		}

		if (RDA_SORT_UP.equals(sortorder)) {
			asort(sort);
		} else {
			arsort(sort);
		}

		CArray<Map> orgTriggers = Clone.deepcopy(triggers);
		triggers.clear();
		for (Object key : sort.keySet()) {
			Nest.value(triggers,key).$(Nest.value(orgTriggers,key).$());
		}
	}
}
