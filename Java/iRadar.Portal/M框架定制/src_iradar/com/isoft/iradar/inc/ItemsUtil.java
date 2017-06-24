package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.arsort;
import static com.isoft.iradar.Cphp.asort;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.localtime;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sscanf;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.AcknowUtil.get_last_event_by_triggerid;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ITEM;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_ANY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_BOOLEAN;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_DECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_HEXADECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_OCTAL;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_CRITICAL;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_ERROR;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_FAILURE_AUDIT;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_INFORMATION;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_SUCCESS_AUDIT;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_VERBOSE;
import static com.isoft.iradar.inc.Defines.ITEM_LOGTYPE_WARNING;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NORMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_EXTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SIMPLE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_HISTORY_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_JAN_2038;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_LEFT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.UNKNOWN_VALUE;
import static com.isoft.iradar.inc.FuncsUtil.convertFunctionValue;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHistory;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_substr;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_itemid;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.TranslateDefines.UNRESOLVED_MACRO_STRING;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.iradar.inc.ValuemapUtil.applyValueMap;
import static com.isoft.iradar.inc.ValuemapUtil.getMappedValue;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.isoft.biz.daoimpl.radar.CItemDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.g;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.model.sql.SqlBuilder.Segment;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;

@CodeConfirmed("benne.2.2.6")
public class ItemsUtil {
	
	private ItemsUtil() {
	}

	/**
	 * Convert windows events type constant in to the string representation
	 *
	 * @param int logtype
	 * @return string
	 */
	public static String get_item_logtype_description(int logtype) {
		switch (logtype) {
			case ITEM_LOGTYPE_INFORMATION:
				return _("Information");
			case ITEM_LOGTYPE_WARNING:
				return _("Warning");
			case ITEM_LOGTYPE_ERROR:
				return _("Error");
			case ITEM_LOGTYPE_FAILURE_AUDIT:
				return _("Failure Audit");
			case ITEM_LOGTYPE_SUCCESS_AUDIT:
				return _("Success Audit");
			case ITEM_LOGTYPE_CRITICAL:
				return _("Critical");
			case ITEM_LOGTYPE_VERBOSE:
				return _("Verbose");
			default:
				return _("Unknown");
		}
	}
	
	/**
	 * Convert windows events type constant in to the CSS style name
	 *
	 * @param int logtype
	 * @return string
	 */
	public static String get_item_logtype_style(int logtype) {
		switch (logtype) {
			case ITEM_LOGTYPE_INFORMATION:
			case ITEM_LOGTYPE_SUCCESS_AUDIT:
			case ITEM_LOGTYPE_VERBOSE:
				return "information";
			case ITEM_LOGTYPE_WARNING:
				return "warning";
			case ITEM_LOGTYPE_ERROR:
			case ITEM_LOGTYPE_FAILURE_AUDIT:
				return "high";
			case ITEM_LOGTYPE_CRITICAL:
				return "disaster";
			default:
				return "normal";
		}
	}
	
	/**
	 * Get item type string name by item type number, or array of all item types if null passed
	 *
	 * @param int|null type
	 * @return array|string
	 */
	public static CArray<String> item_type2str() {
		CArray<String> types = map(
			ITEM_TYPE_IRADAR, _("iRadar agent"),					//Agent主动
			ITEM_TYPE_IRADAR_ACTIVE, _("iRadar agent (active)"),	//Agent被动
//			ITEM_TYPE_SIMPLE, _("Simple check"),					//简单检查
			ITEM_TYPE_SNMPV1, _("SNMPv1 agent"),					//SNMPV1
			ITEM_TYPE_SNMPV2C, _("SNMPv2 agent"),					//SNMPV2C
			ITEM_TYPE_SNMPV3, _("SNMPv3 agent"),					//SNMPV3
//			ITEM_TYPE_SNMPTRAP, _("SNMP trap"),						//SNMPTrap
//			ITEM_TYPE_INTERNAL, _("iRadar internal"),				//内部类型
			ITEM_TYPE_TRAPPER, _("iRadar trapper"),					//Trapper(API)
//			ITEM_TYPE_AGGREGATE, _("iRadar aggregate"),				//整合
//			ITEM_TYPE_EXTERNAL, _("External check"),				//额外检查
//			ITEM_TYPE_DB_MONITOR, _("Database monitor"),			//数据库监控
//			ITEM_TYPE_IPMI, _("IPMI agent"),						//IPMI
//			ITEM_TYPE_SSH, _("SSH agent"),							//SSH
//			ITEM_TYPE_TELNET, _("TELNET agent"),					//TELNET
//			ITEM_TYPE_JMX, _("JMX agent"),							//JMX
			ITEM_TYPE_CALCULATED, _("Calculated"),					//计算
			ITEM_TYPE_HTTPTEST, _("Web monitoring")					//WEB监控
		);
		return types;
	}
	
	/**
	 * Get item type string name by item type number, or array of all item types if null passed
	 *
	 * @param int|null type
	 * @return array|string
	 */
	public static String item_type2str(int type) {
		CArray<String> types = item_type2str();
		if (isset(types, type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Returns human readable an item value type
	 *
	 * @param int valueType
	 *
	 * @return string
	 */
	public static String itemValueTypeString(int valueType) {
		switch (valueType) {
			case ITEM_VALUE_TYPE_UINT64:
				return _("Numeric (unsigned)");
			case ITEM_VALUE_TYPE_FLOAT:
				return _("Numeric (float)");
			case ITEM_VALUE_TYPE_STR:
				return _("Character");
			case ITEM_VALUE_TYPE_LOG:
				return _("Log");
			case ITEM_VALUE_TYPE_TEXT:
				return _("Text");
		}
		return _("Unknown");
	}
	
	public static CArray<String > item_data_type2str() {
		CArray<String >types = map(
			ITEM_DATA_TYPE_BOOLEAN, _("Boolean"),
			ITEM_DATA_TYPE_OCTAL, _("Octal"),
			ITEM_DATA_TYPE_DECIMAL, _("Decimal"),
			ITEM_DATA_TYPE_HEXADECIMAL, _("Hexadecimal")
		);
		return types;
	}
	
	public static String item_data_type2str(int type) {
		CArray<String >types = map(
			ITEM_DATA_TYPE_BOOLEAN, _("Boolean"),
			ITEM_DATA_TYPE_OCTAL, _("Octal"),
			ITEM_DATA_TYPE_DECIMAL, _("Decimal"),
			ITEM_DATA_TYPE_HEXADECIMAL, _("Hexadecimal")
		);
		if (isset(types,type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	public static CArray<String>  item_status2str() {
		CArray<String> types = map(
			ITEM_STATUS_ACTIVE, _("Enabled"),
			ITEM_STATUS_DISABLED, _("Disabled")
		);
		return types;
	}
	
	public static String  item_status2str(int type) {
		CArray<String> types = map(
			ITEM_STATUS_ACTIVE, _("Enabled"),
			ITEM_STATUS_DISABLED, _("Disabled")
		);
		if (isset(types,type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Returns the names of supported item states.
	 *
	 * If the state parameter is passed, returns the name of the specific state, otherwise - returns an array of all
	 * supported states.
	 *
	 * @param string state
	 *
	 * @return array|string
	 */
	public static CArray<String> itemState() {
		CArray<String> states = map(
			ITEM_STATE_NORMAL, _("Normal"),
			ITEM_STATE_NOTSUPPORTED, _("Not supported")
		);
		return states;
	}
	
	/**
	 * Returns the names of supported item states.
	 *
	 * If the state parameter is passed, returns the name of the specific state, otherwise - returns an array of all
	 * supported states.
	 *
	 * @param string state
	 *
	 * @return array|string
	 */
	public static String itemState(int state) {
		CArray<String> states = map(
			ITEM_STATE_NORMAL, _("Normal"),
			ITEM_STATE_NOTSUPPORTED, _("Not supported")
		);
		if (isset(states,state)) {
			return states.get(state);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Returns the text indicating the items status and state. If the state parameter is not given, only the status of
	 * the item will be taken into account.
	 *
	 * @param int status
	 * @param int state
	 *
	 * @return string
	 */
	public static String itemIndicator(int status) {
		return itemIndicator(status, null);
	}
	
	/**
	 * Returns the text indicating the items status and state. If the state parameter is not given, only the status of
	 * the item will be taken into account.
	 *
	 * @param int status
	 * @param int state
	 *
	 * @return string
	 */
	public static String itemIndicator(int status, Integer state) {
		if (status == ITEM_STATUS_ACTIVE) {
			return (state !=null && state == ITEM_STATE_NOTSUPPORTED) ? _("Not supported") : _("Enabled");
		} else if (status == ITEM_STATUS_DISABLED) {
			return _("Disabled");
		}
		return _("Unknown");
	}
	
	/**
	 * Returns the CSS class for the items status and state indicator. If the state parameter is not given, only the status of
	 * the item will be taken into account.
	 *
	 * @param int status
	 * @param int state
	 *
	 * @return string
	 */
	public static String itemIndicatorStyle(int status) {
		return itemIndicatorStyle(status, null);
	}
	
	/**
	 * Returns the CSS class for the items status and state indicator. If the state parameter is not given, only the status of
	 * the item will be taken into account.
	 *
	 * @param int status
	 * @param int state
	 *
	 * @return string
	 */
	public static String itemIndicatorStyle(int status, Integer state) {
		if (status == ITEM_STATUS_ACTIVE) {
			return (state!=null && state == ITEM_STATE_NOTSUPPORTED) ? "unknown" : "enabled";
		} else if (status == ITEM_STATUS_DISABLED) {
			return "disabled";
		}
		return "unknown";
	}
	
	/**
	 * Orders items by both status and state. Items are sorted in the following order: enabled, disabled, not supported.
	 *
	 * Keep in sync with orderTriggersByStatus().
	 *
	 * @param array  items
	 * @param string sortorder
	 */
	public static void orderItemsByStatus(CArray<Map> items) {
		orderItemsByStatus(items, RDA_SORT_UP);
	}
	
	/**
	 * Orders items by both status and state. Items are sorted in the following order: enabled, disabled, not supported.
	 *
	 * Keep in sync with orderTriggersByStatus().
	 *
	 * @param array  items
	 * @param string sortorder
	 */
	public static void orderItemsByStatus(CArray<Map> items, String sortorder) {
		CArray<Integer> sort = array();

		for (Entry<Object, Map> e : items.entrySet()) {
		    Object key = e.getKey();
		    Map item = e.getValue();
			Integer statusOrder = null;
			if (Nest.value(item,"status").asInteger() == ITEM_STATUS_ACTIVE) {
				statusOrder = (Nest.value(item,"state").asInteger() == ITEM_STATE_NOTSUPPORTED) ? 2 : 0;
			} else if (Nest.value(item,"status").asInteger() == ITEM_STATUS_DISABLED) {
				statusOrder = 1;
			}
			Nest.value(sort,key).$(statusOrder);
		}

		if (RDA_SORT_UP.equals(sortorder)) {
			asort(sort);
		} else {
			arsort(sort);
		}

		CArray cloneItems = Clone.deepcopy(items);
		items.clear();
		for (Object key : sort.keySet()) {
			Nest.value(items,key).$(Nest.value(cloneItems,key).$());
		}
	}
	
	/**
	 * Returns the name of the given interface type. Items \"status\" and \"state\" properties must be defined.
	 *
	 * @param int type
	 *
	 * @return null
	 */
	public static String interfaceType2str(int type) {
		CArray<String> interfaceGroupLabels = map(
			INTERFACE_TYPE_AGENT, _("Agent"),
			INTERFACE_TYPE_SNMP, _("SNMP"),
			INTERFACE_TYPE_JMX, _("JMX"),
			INTERFACE_TYPE_IPMI, _("IPMI")
		);
		return isset(interfaceGroupLabels,type) ? interfaceGroupLabels.get(type) : null;
	}
	
	public static CArray<Integer> itemTypeInterface() {
		CArray<Integer> types = map(
			ITEM_TYPE_SNMPV1, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPV2C, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPV3, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPTRAP, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_IPMI, INTERFACE_TYPE_IPMI,
			ITEM_TYPE_IRADAR, INTERFACE_TYPE_AGENT,
			ITEM_TYPE_SIMPLE, INTERFACE_TYPE_ANY,
			ITEM_TYPE_EXTERNAL, INTERFACE_TYPE_ANY,
			ITEM_TYPE_SSH, INTERFACE_TYPE_ANY,
			ITEM_TYPE_TELNET, INTERFACE_TYPE_ANY,
			ITEM_TYPE_JMX, INTERFACE_TYPE_JMX
		);
		return types;
	}
	
	public static Integer itemTypeInterface(int type) {
		CArray<Integer> types = map(
			ITEM_TYPE_SNMPV1, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPV2C, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPV3, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_SNMPTRAP, INTERFACE_TYPE_SNMP,
			ITEM_TYPE_IPMI, INTERFACE_TYPE_IPMI,
			ITEM_TYPE_IRADAR, INTERFACE_TYPE_AGENT,
			ITEM_TYPE_SIMPLE, INTERFACE_TYPE_ANY,
			ITEM_TYPE_EXTERNAL, INTERFACE_TYPE_ANY,
			ITEM_TYPE_SSH, INTERFACE_TYPE_ANY,
			ITEM_TYPE_TELNET, INTERFACE_TYPE_ANY,
			ITEM_TYPE_JMX, INTERFACE_TYPE_JMX
		);
		if (isset(types, type)) {
			return types.get(type);
		} else {
			return null;
		}
	}
	
	public static boolean update_item_status(IIdentityBean idBean, SQLExecutor executor, Long[] itemids, int status) {
		boolean result = true;
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_items = DBselect(executor, 
				"SELECT i.* FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.itemid", itemids),
				sqlParts.getNamedParams()
		);
		Map params = new HashMap();
		params.put("status", status);
		for (Map item : db_items) {
			int old_status = Nest.value(item,"status").asInteger();
			if (status != old_status) {
				params.put("itemid", Nest.value(item,"itemid").$());
				result &= DBexecute(executor,
					"UPDATE items SET status=#{status} WHERE itemid=#{itemid}",
					params
				);
				if (result) {
					Map host = get_host_by_hostid(idBean, executor, Nest.value(item,"hostid").asLong());
					Map item_new = get_item_by_itemid(executor, Nest.value(item,"itemid").asString());
					add_audit_ext(idBean, executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ITEM, Nest.value(item,"itemid").asLong(), Nest.value(host,"host").$()+NAME_DELIMITER+Nest.value(item,"name").$(), "items", item, item_new);
				}
			}
		}
		return result;
	}

	public static boolean copyItemsToHosts(IIdentityBean idBean, SQLExecutor executor,Long[] srcItemIds, Long[] dstHostIds) {
		CItemGet options = new CItemGet();
		options.setItemIds(srcItemIds);
		options.setOutput(new String[]{
				"type", "snmp_community", "snmp_oid", "name", "key_", "delay", "history", "trends", "status", "value_type",
				"trapper_hosts", "units", "multiplier", "delta", "snmpv3_contextname", "snmpv3_securityname",
				"snmpv3_securitylevel", "snmpv3_authprotocol", "snmpv3_authpassphrase", "snmpv3_privprotocol",
				"snmpv3_privpassphrase", "formula", "logtimefmt", "valuemapid", "delay_flex", "params", "ipmi_sensor",
				"data_type", "authtype", "username", "password", "publickey", "privatekey", "flags", "filter", "port",
				"description", "inventory_link"});
		options.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
		options.setSelectApplications(API_OUTPUT_REFER);
		CArray<Map> srcItems = API.Item(idBean, executor).get(options);

		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "host", "status"});
		hoptions.setSelectInterfaces(new String[]{"interfaceid", "type", "main"});
		hoptions.setHostIds(dstHostIds);
		hoptions.setPreserveKeys(true);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> dstHosts = API.Host(idBean, executor).get(hoptions);

		for(Map dstHost : dstHosts) {
			CArray<Integer> interfaceids = array();
			for(Map iface : (CArray<Map>)Nest.value(dstHost,"interfaces").asCArray()) {
				if (Nest.value(iface,"main").asInteger() == 1) {
					Nest.value(interfaceids,iface.get("type")).$(Nest.value(iface,"interfaceid").$());
				}
			}
			for(Map srcItem : srcItems) {
				if (Nest.value(dstHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
					Integer type = itemTypeInterface(Nest.value(srcItem,"type").asInteger());
					if (type!=null && type == INTERFACE_TYPE_ANY) {
						for(int itype : new int[]{INTERFACE_TYPE_AGENT, INTERFACE_TYPE_SNMP, INTERFACE_TYPE_JMX, INTERFACE_TYPE_IPMI}) {
							if (isset(interfaceids,itype)) {
								Nest.value(srcItem,"interfaceid").$(interfaceids.get(itype));
								break;
							}
						}
					} else if (type !=null) {
						if (!isset(interfaceids,type)) {
							error(_s("Cannot find host interface on \"%1$s\" for item key \"%2$s\".", Nest.value(dstHost,"host").$(), Nest.value(srcItem,"key_").$()));
							return false;
						}
						Nest.value(srcItem,"interfaceid").$(interfaceids.get(type));
					}
				}
				unset(srcItem,"itemid");
				Nest.value(srcItem,"hostid").$(Nest.value(dstHost,"hostid").$());
				Nest.value(srcItem,"applications").$(get_same_applications_for_host(executor, rda_objectValues(Nest.value(srcItem,"applications").$(), "applicationid").valuesAsLong(), Nest.value(dstHost,"hostid").asLong()));
			}
			if (empty(API.Item(idBean, executor).create(srcItems))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean copyItems(IIdentityBean idBean, SQLExecutor executor,Long srcHostId, Long dstHostId) {
		CItemGet options = new CItemGet();
		options.setHostIds(srcHostId);
		options.setOutput(new String[]{
				"type", "snmp_community", "snmp_oid", "name", "key_", "delay", "history", "trends", "status", "value_type",
				"trapper_hosts", "units", "multiplier", "delta", "snmpv3_contextname", "snmpv3_securityname",
				"snmpv3_securitylevel", "snmpv3_authprotocol", "snmpv3_authpassphrase", "snmpv3_privprotocol",
				"snmpv3_privpassphrase", "formula", "logtimefmt", "valuemapid", "delay_flex", "params", "ipmi_sensor",
				"data_type", "authtype", "username", "password", "publickey", "privatekey", "flags", "filter", "port",
				"description", "inventory_link"});
		options.setInherited(false);
		options.setFilter("flags", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL));
		options.setSelectApplications(API_OUTPUT_REFER);
		CArray<Map> srcItems = API.Item(idBean, executor).get(options);
		
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "host", "status"});
		hoptions.setSelectInterfaces(new String[]{"interfaceid", "type", "main"});
		hoptions.setHostIds(dstHostId);
		hoptions.setPreserveKeys(true);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> dstHosts = API.Host(idBean, executor).get(hoptions);
		Map dstHost = reset(dstHosts);

		for(Map srcItem : srcItems) {
			if (Nest.value(dstHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
				// find a matching interface
				Map iface = CItemDAO.findInterfaceForItem(srcItem, Nest.value(dstHost,"interfaces").asCArray());
				if (!empty(iface)) {
					Nest.value(srcItem,"interfaceid").$(Nest.value(iface,"interfaceid").$());
				}
				// no matching interface found, throw an error
				else if (iface != null) {
					error(_s("Cannot find host interface on \"%1$s\" for item key \"%2$s\".", Nest.value(dstHost,"host").$(), Nest.value(srcItem,"key_").$()));
				}
			}
			unset(srcItem,"itemid");
			unset(srcItem,"templateid");
			Nest.value(srcItem,"hostid").$(dstHostId);
			Nest.value(srcItem,"applications").$(get_same_applications_for_host(executor,rda_objectValues(Nest.value(srcItem,"applications").$(), "applicationid").valuesAsLong(), dstHostId));
		}

		return !empty(API.Item(idBean, executor).create(srcItems));
	}
	
	public static boolean copyApplications(IIdentityBean idBean, SQLExecutor executor, Long srcHostId, Long dstHostId) {
		CAppGet aoptions = new CAppGet();
		aoptions.setHostIds(srcHostId);
		aoptions.setOutput(API_OUTPUT_EXTEND);
		aoptions.setInherited(false);
		CArray<Map> apps_to_clone = API.Application(idBean, executor).get(aoptions);
		if (empty(apps_to_clone)) {
			return true;
		}

		for(Map app : apps_to_clone) {
			Nest.value(app,"hostid").$(dstHostId);
			unset(app,"applicationid");
			unset(app,"templateid");
		}
		return !empty(API.Application(idBean, executor).create(apps_to_clone));
	}
	
	public static boolean activate_item(IIdentityBean idBean, SQLExecutor executor, Long... itemids) {
		// first update status for child items
		CArray child_items = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_items = DBselect(executor, 
				"SELECT i.itemid,i.hostid FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.templateid", itemids),
				sqlParts.getNamedParams());
		for(Map item : db_items) {
			Nest.value(child_items,item.get("itemid")).$(Nest.value(item,"itemid").$());
		}
		if (!empty(child_items)) {
			activate_item(idBean, executor, child_items.valuesAsLong()); // Recursion !!!
		}
		return update_item_status(idBean, executor, itemids, ITEM_STATUS_ACTIVE);
	}
	
	public static boolean disable_item(IIdentityBean idBean, SQLExecutor executor, Long... itemids) {
		// first update status for child items
		CArray chd_items = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map>db_tmp_items = DBselect(executor, 
				"SELECT i.itemid,i.hostid FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.templateid", itemids),
				sqlParts.getNamedParams());
		for(Map db_tmp_item : db_tmp_items) {
			Nest.value(chd_items,db_tmp_item.get("itemid")).$(Nest.value(db_tmp_item,"itemid").$());
		}
		if (!empty(chd_items)) {
			disable_item(idBean, executor, chd_items.valuesAsLong()); // Recursion !!!
		}
		return update_item_status(idBean, executor, itemids, ITEM_STATUS_DISABLED);
	}
	
	public static CArray<Map> get_items_by_hostid(SQLExecutor executor, Long[] hostids) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
				"SELECT i.* FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.hostid", hostids),
				sqlParts.getNamedParams());
	}
	
	public static Map get_item_by_key(SQLExecutor executor, String key) {
		return get_item_by_key(executor, key, "");
	}
	
	public static Map get_item_by_key(SQLExecutor executor, String key, String host) {
		Map params = new HashMap();
		String sql_from = "";
		String sql_where = "";
		if (!empty(host)) {
			sql_from = ",hosts h ";
			sql_where = " AND h.host=#{host} AND i.hostid=h.hostid ";
			params.put("host", host);
		}
		String sql = "SELECT DISTINCT i.*"+
				" FROM items i "+sql_from+
				" WHERE i.key_#{key}="+
					sql_where;
		params.put("key", key);
		return DBfetch(DBselect(executor, sql, params));
	}
	
	public static Map get_item_by_itemid(SQLExecutor executor, String itemid) {
		Map params = new HashMap();
		params.put("itemid", itemid);
		Map db_item = DBfetch(DBselect(executor, "SELECT i.* FROM items i WHERE i.itemid=#{itemid}",params));
		if (!empty(db_item)) {
			return db_item;
		}
		error(_s("No item with itemid=\"%1$s\".", itemid));
		return null;
	}
	
	public static Map get_item_by_itemid_limited(SQLExecutor executor, Long itemid) {
		Map params = new HashMap();
		params.put("itemid", itemid);
		Map row = DBfetch(DBselect(executor,
			"SELECT i.itemid,i.interfaceid,i.name,i.key_,i.hostid,i.delay,i.history,i.status,i.type,i.lifetime,"+
				"i.snmp_community,i.snmp_oid,i.value_type,i.data_type,i.trapper_hosts,i.port,i.units,i.multiplier,"+
				"i.delta,i.snmpv3_contextname,i.snmpv3_securityname,i.snmpv3_securitylevel,i.snmpv3_authprotocol,"+
				"i.snmpv3_authpassphrase,i.snmpv3_privprotocol,i.snmpv3_privpassphrase,i.formula,i.trends,i.logtimefmt,"+
				"i.valuemapid,i.delay_flex,i.params,i.ipmi_sensor,i.templateid,i.authtype,i.username,i.password,"+
				"i.publickey,i.privatekey,i.flags,i.filter,i.description,i.inventory_link"+
			" FROM items i"+
			" WHERE i.itemid=#{itemid}",params));
		if (!empty(row)) {
			return row;
		}
		error(_s("No item with itemid \"%1$s\".", itemid));
		return null;
	}
	
	/**
	 * Description:
	 * Replace items for specified host
	 *
	 * Comments:
	 * error= true : rise Error if item doesn't exist (error generated), false: special processing (NO error generated)
	 */
	public static Long get_same_item_for_host(SQLExecutor executor, Long itemid, Long dest_hostids) {
		if (isset(itemid)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> db_items = DBselect(executor,
				"SELECT src.*"+
				" FROM items src,items dest"+
				" WHERE dest.itemid="+sqlParts.marshalParam(itemid)+
					" AND src.key_=dest.key_"+
					" AND "+sqlParts.dual.dbConditionInt("src.hostid", new Long[]{dest_hostids}),
				sqlParts.getNamedParams()
			);
			Long same_item = null;
			for (Map db_item : db_items) {
				same_item = Nest.value(db_item,"itemid").asLong();
			}
			return same_item;
		}
		return null;
	}
	
	/**
	 * Description:
	 * Replace items for specified host
	 *
	 * Comments:
	 * error= true : rise Error if item doesn't exist (error generated), false: special processing (NO error generated)
	 */
	public static CArray<String> get_same_item_for_host(SQLExecutor executor, Long itemid, Long[] dest_hostids) {
		if (isset(itemid)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> db_items = DBselect(executor,
				"SELECT src.*"+
				" FROM items src,items dest"+
				" WHERE dest.itemid="+sqlParts.marshalParam(itemid)+
					" AND src.key_=dest.key_"+
					" AND "+sqlParts.dual.dbConditionInt("src.hostid", dest_hostids),
				sqlParts.getNamedParams()
			);
			CArray<String> same_items = array();
			for (Map db_item : db_items) {
				Nest.value(same_items,db_item.get("itemid")).$(Nest.value(db_item,"itemid").asString());
			}
			return same_items;
		}
		return null;
	}
	
	/**
	 * Description:
	 * Replace items for specified host
	 *
	 * Comments:
	 * error= true : rise Error if item doesn't exist (error generated), false: special processing (NO error generated)
	 */
	public static Map get_same_item_for_host(SQLExecutor executor, Map item, Long dest_hostids) {
		String itemid = Nest.as(item).asString();
		if (isset(itemid)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> db_items = DBselect(executor,
				"SELECT src.*"+
				" FROM items src,items dest"+
				" WHERE dest.itemid="+sqlParts.marshalParam(itemid)+
					" AND src.key_=dest.key_"+
					" AND "+sqlParts.dual.dbConditionInt("src.hostid", new Long[]{dest_hostids}),
				sqlParts.getNamedParams()
			);
			Map same_item = null;
			for (Map db_item : db_items) {
				same_item = db_item;
			}
			return same_item;
		}
		return null;
	}
	
	/**
	 * Description:
	 * Replace items for specified host
	 *
	 * Comments:
	 * error= true : rise Error if item doesn't exist (error generated), false: special processing (NO error generated)
	 */
	public static CArray<Map> get_same_item_for_host(SQLExecutor executor, Map item, Long[] dest_hostids) {
		String itemid = Nest.as(item).asString();
		if (isset(itemid)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> db_items = DBselect(executor,
				"SELECT src.*"+
				" FROM items src,items dest"+
				" WHERE dest.itemid="+sqlParts.marshalParam(itemid)+
					" AND src.key_=dest.key_"+
					" AND "+sqlParts.dual.dbConditionInt("src.hostid", dest_hostids),
				sqlParts.getNamedParams()
			);
			CArray<Map> same_items = array();
			for (Map db_item : db_items) {
				Nest.value(same_items,db_item.get("itemid")).$(db_item);
			}
			return same_items;
		}
		return null;
	}
	
	public static Map get_realhost_by_itemid(IIdentityBean idBean, SQLExecutor executor, String itemid) {
		Map item = get_item_by_itemid(executor, itemid);
		if (Nest.value(item,"templateid").asLong() != 0) {
			return get_realhost_by_itemid(idBean, executor,Nest.value(item,"templateid").asString()); // attention recursion!
		}
		return get_host_by_itemid(idBean, executor,itemid);
	}
	
	public static void fillItemsWithChildTemplates(SQLExecutor executor, CArray<Map> items) {
		boolean processSecondLevel = false;
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbItems = DBselect(executor,
				"SELECT i.itemid,i.templateid FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.itemid", rda_objectValues(items, "templateid").valuesAsLong()),
				sqlParts.getNamedParams());
		for (Map dbItem : dbItems) {
			for (Entry<Object, Map> e : items.entrySet()) {
			    Object itemid = e.getKey();
			    Map item = e.getValue();
				if (Nest.value(item,"templateid").asLong() == Nest.value(dbItem,"itemid").asLong() && !empty(Nest.value(dbItem,"templateid").$())) {
					Nest.value(items,itemid,"templateid").$(Nest.value(dbItem,"templateid").$());
					processSecondLevel = true;
				}
			}
		}
		if (processSecondLevel) {
			fillItemsWithChildTemplates(executor, items); // attention recursion!
		}
	}
	
	public static String get_realrule_by_itemid_and_hostid(SQLExecutor executor, String itemid, String hostid) {
		Map item = get_item_by_itemid(executor,itemid);
		if (bccomp(hostid,Nest.value(item,"hostid").$()) == 0) {
			return Nest.value(item,"itemid").asString();
		}
		if (Nest.value(item,"templateid").asLong() != 0) {
			return get_realrule_by_itemid_and_hostid(executor,Nest.value(item,"templateid").asString(), hostid);
		}
		return Nest.value(item,"itemid").asString();
	}
	
	/**
	 * Retrieve overview table object for items.
	 *
	 * @param array  hostIds
	 * @param string application name of application to filter
	 * @param int    viewMode
	 *
	 * @return CTableInfo
	 */
	public static CTableInfo getItemsDataOverview(IIdentityBean idBean, SQLExecutor executor, Long[] hostIds, String application, int viewMode) {
		String sqlFrom = "";
		String sqlWhere = "";

		SqlBuilder sqlParts = new SqlBuilder();
		if (application!=null && application.length()>0) {
			sqlFrom = "applications a,items_applications ia,";
			sqlWhere = " AND i.itemid=ia.itemid AND a.applicationid=ia.applicationid AND a.name="+sqlParts.marshalParam(application);
		}

		CArray<Map> dbItems = DBselect(executor,
			"SELECT DISTINCT h.hostid,h.name AS hostname,i.itemid,i.key_,i.value_type,i.units,"+
				"i.name,t.priority,i.valuemapid,t.value AS tr_value,t.triggerid"+
			" FROM hosts h,"+sqlFrom+"items i"+
				" LEFT JOIN functions f ON f.itemid=i.itemid"+
				" LEFT JOIN triggers t ON t.triggerid=f.triggerid AND t.status="+TRIGGER_STATUS_ENABLED+
			" WHERE "+sqlParts.dual.dbConditionInt("h.hostid", hostIds)+
				" AND h.status="+HOST_STATUS_MONITORED+
				" AND h.hostid=i.hostid"+
				" AND i.status="+ITEM_STATUS_ACTIVE+
				" AND "+sqlParts.dual.dbConditionInt("i.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED})+
					sqlWhere,
			sqlParts.getNamedParams()
		);

		dbItems = CMacrosResolverHelper.resolveItemNames(idBean, executor,dbItems);

		CArrayHelper.sort(dbItems, array(
			map("field", "name_expanded", "order", RDA_SORT_UP),
			map("field", "itemid", "order", RDA_SORT_UP)
		));

		// fetch latest values
		CArray<CArray<Map>> history = Manager.History(idBean, executor).getLast(rda_toHash(dbItems, "itemid"), 1, RDA_HISTORY_PERIOD);

		// fetch data for the host JS menu
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"name", "hostid", "status"});
		hoptions.setMonitoredHosts(true);
		hoptions.setHostIds(hostIds);
		hoptions.setWithMonitoredItems(true);
		hoptions.setPreserveKeys(true);
		if(viewMode == STYLE_LEFT){
		hoptions.setSelectScreens(API_OUTPUT_COUNT);
		}
		CArray<Map> hosts = API.Host(idBean, executor).get(hoptions);

		CArray<CArray<Map>> items = array();
		CArray<String> hostNames = array();
		for(Map dbItem : dbItems) {
			String name = Nest.value(dbItem,"name_expanded").asString();

			Nest.value(dbItem,"hostname").$(Nest.value(dbItem,"hostname").$());
			Nest.value(hostNames,dbItem.get("hostid")).$(Nest.value(dbItem,"hostname").$());

			// a little tricky check for attempt to overwrite active trigger (value=1) with
			// inactive or active trigger with lower priority.
			if (!isset(Nest.value(items,name,dbItem.get("hostname")).$())
					|| ((Nest.value(items,name,dbItem.get("hostname"),"tr_value").asInteger() == TRIGGER_VALUE_FALSE && Nest.value(dbItem,"tr_value").asInteger() == TRIGGER_VALUE_TRUE)
						|| ((Nest.value(items,name,dbItem.get("hostname"),"tr_value").asInteger() == TRIGGER_VALUE_FALSE || Nest.value(dbItem,"tr_value").asInteger() == TRIGGER_VALUE_TRUE)
							&& Nest.value(dbItem,"priority").asInteger() > Nest.value(items,name,dbItem.get("hostname"),"severity").asInteger()))) {
				Nest.value(items,name,dbItem.get("hostname")).$(map(
					"itemid", Nest.value(dbItem,"itemid").$(),
					"value_type", Nest.value(dbItem,"value_type").$(),
					"value", isset(history,dbItem.get("itemid")) ? Nest.value(history,dbItem.get("itemid"),0,"value").$() : null,
					"units", Nest.value(dbItem,"units").$(),
					"name", name,
					"valuemapid", Nest.value(dbItem,"valuemapid").$(),
					"severity", Nest.value(dbItem,"priority").$(),
					"tr_value", Nest.value(dbItem,"tr_value").$(),
					"triggerid", Nest.value(dbItem,"triggerid").$()
				));
			}
		}

		CTableInfo table = new CTableInfo(_("No items found."));
		if (empty(hostNames)) {
			return table;
		}
		table.makeVerticalRotation();

		order_result(hostNames);

		if (viewMode == STYLE_TOP) {
			CArray header = array(new CCol(_("Items"), "center"));
			for(String hostName : hostNames) {
				header.add(new CCol(hostName, "vertical_rotation"));
			}
			table.setHeader(header, "vertical_header");

			for (Entry<Object, CArray<Map>> e : items.entrySet()) {
			    String descr = Nest.as(e.getKey()).asString();
			    CArray<Map> ithosts = e.getValue();
			    CArray tableRow = array(nbsp(descr));
				for(String hostName : hostNames) {
					tableRow = getItemDataOverviewCells(idBean, executor, tableRow, ithosts, hostName);
				}
				table.addRow(tableRow);
			}
		} else {
			CArray<CArray<Map>> scripts = API.Script(idBean, executor).getScriptsByHosts(rda_objectValues(hosts, "hostid").valuesAsLong());

			CArray header = array(new CCol(_("Hosts"), "center"));
			for (Entry<Object, CArray<Map>> e : items.entrySet()) {
			    String descr = Nest.as(e.getKey()).asString();
			    //CArray<Map> ithosts = e.getValue();
				header.add(new CCol(descr, "vertical_rotation"));
			}
			table.setHeader(header, "vertical_header");

			for (Entry<Object, String> e : hostNames.entrySet()) {
			    Object hostId = e.getKey();
			    String hostName = e.getValue();
				Map host = hosts.get(hostId);

				CSpan name = new CSpan(Nest.value(host,"name").$(), "link_menu");
				name.setMenuPopup(getMenuPopupHost(host, scripts.get(hostId)));

				CArray tableRow = array(new CCol(name));
				for(CArray<Map> ithosts : items) {
					tableRow = getItemDataOverviewCells(idBean, executor, tableRow, ithosts, hostName);
				}
				table.addRow(tableRow);
			}
		}

		return table;
	}
	
	public static CArray getItemDataOverviewCells(IIdentityBean idBean, SQLExecutor executor,CArray tableRow, CArray<Map> ithosts, String hostName) {
		String css = "";
		Object value = "-";
		Map ack = null;

		Map item = null;
		if (isset(ithosts,hostName)) {
			item = ithosts.get(hostName);

			if (Nest.value(item,"tr_value").asInteger() == TRIGGER_VALUE_TRUE) {
				css = getSeverityStyle(Nest.value(item,"severity").asInteger());
				ack = get_last_event_by_triggerid(idBean, executor, Nest.value(item,"triggerid").asString());
				ack = (Nest.value(ack,"acknowledged").asInteger() == 1)
					? array(SPACE, new CImg("images/general/tick.png", "ack"))
					: null;
			}

			value = (Nest.value(item,"value").$() != null) ? formatHistoryValue(idBean, executor,Nest.value(item,"value").asString(), item) : UNKNOWN_VALUE;
		}

		if ( !"-".equals(value)) {
			value = new CSpan(value, "link");
		}

		CCol column = new CCol(array(value, ack), css);

		if (isset(ithosts,hostName)) {
			column.setMenuPopup(getMenuPopupHistory(item));
		}

		tableRow.add(column);

		return tableRow;
	}
	
	/**
	 * Get same application IDs on destination host and return array with keys as source application IDs
	 * and values as destination application IDs.
	 *
	 * Comments: !!! Don't forget sync code with C !!!
	 *
	 * @param array  applicationIds
	 * @param string hostId
	 *
	 * @return array
	 */
	public static CArray<Long> get_same_applications_for_host(SQLExecutor executor, Long[] applicationIds, Long hostId) {
		CArray<Long> applications = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbApplications = DBselect(executor,
			"SELECT a1.applicationid AS dstappid,a2.applicationid AS srcappid"+
			" FROM applications a1,applications a2"+
			" WHERE a1.name=a2.name"+
				" AND a1.hostid="+sqlParts.marshalParam(hostId)+
				" AND "+sqlParts.dual.dbConditionInt("a2.applicationid", applicationIds),
			sqlParts.getNamedParams()
		);

		for (Map dbApplication : dbApplications) {
			Nest.value(applications,dbApplication.get("srcappid")).$(Nest.value(dbApplication,"dstappid").$());
		}

		return applications;
	}
	
	public static CArray<Object> get_applications_by_itemid(SQLExecutor executor, Long... itemids) {
		return get_applications_by_itemid(executor, itemids, "applicationid");
	}
	
	public static CArray<Object> get_applications_by_itemid(SQLExecutor executor, Long[] itemids, String field) {
		CArray<Object> result = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_applications = DBselect(executor,
			"SELECT DISTINCT app."+field+" AS result"+
			" FROM applications app,items_applications ia"+
			" WHERE app.applicationid=ia.applicationid"+
				" AND "+sqlParts.dual.dbConditionInt("ia.itemid", itemids),
			sqlParts.getNamedParams()
		);
		for (Map db_application : db_applications) {
			array_push(result, Nest.value(db_application,"result").$());
		}
		return result;
	}
	
	/**
	 * Clear items history and trends.
	 *
	 * @param itemIds
	 *
	 * @return bool
	 */
	public static boolean delete_history_by_itemid(SQLExecutor executor, Long... itemIds) {
		boolean result = delete_trends_by_itemid(executor, itemIds);
		if (!result) {
			return result;
		}

		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionInt("itemid", itemIds);
		Map params = sqlParts.getNamedParams();
		String segmentSql = sqlParts.createSegmentSql(Segment.where);

		String sql =  "DELETE FROM history_text WHERE " + segmentSql;
		executor.executeInsertDeleteUpdate(sql, params);
		
		sql =  "DELETE FROM history_log WHERE " + segmentSql;
		executor.executeInsertDeleteUpdate(sql, params);
		
		sql =  "DELETE FROM history_uint WHERE " + segmentSql;
		executor.executeInsertDeleteUpdate(sql, params);
		
		sql =  "DELETE FROM history_str WHERE " + segmentSql;
		executor.executeInsertDeleteUpdate(sql, params);
		
		sql =  "DELETE FROM history WHERE " + segmentSql;
		executor.executeInsertDeleteUpdate(sql, params);

		return true;
	}
	
	/**
	 * Clear trends history for provided item ids.
	 *
	 * @param mixed itemIds IDs of items for which history should be cleared
	 *
	 * @return bool
	 */
	public static boolean delete_trends_by_itemid(SQLExecutor executor, Long[] itemIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionInt("itemid", itemIds);
		Map params = sqlParts.getNamedParams();
		String segmentSql = sqlParts.createSegmentSql(Segment.where);
		boolean r1 = false;
		boolean r2 = false;
		String sql = "SELECT COUNT(*) num FROM trends WHERE " + segmentSql;
		List<Map> result = executor.executeNameParaQuery(sql,params);
		if(!empty(result) && result.size()>0 && Nest.value(result.get(0),"num").asInteger()>0){
			sql =  "DELETE FROM trends WHERE " + segmentSql;
			r1 = executor.executeInsertDeleteUpdate(sql, params)>0;
		}else{
			r1 = true;
		}
		sql = "SELECT COUNT(*) num FROM trends_uint WHERE " + segmentSql;
		result = executor.executeNameParaQuery(sql,params);
		if(!empty(result) && result.size()>0 && Nest.value(result.get(0),"num").asInteger()>0){
			sql =  "DELETE FROM trends_uint WHERE " + segmentSql;
			r2 = executor.executeInsertDeleteUpdate(sql, params)>0;
		}else{
			r2 = true;
		}
		return r1 && r2;
	}
	
	/**
	 * Format history value.
	 * First format the value according to the configuration of the item. Then apply the value mapping to the formatted (!)
	 * value.
	 *
	 * @param mixed     value
	 * @param array     item
	 * @param int       Nest.value(item,"value_type").$()     type of the value: ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64, ...
	 * @param string    Nest.value(item,"units").$()          units of item
	 * @param int       Nest.value(item,"valuemapid").$()     id of mapping set of values
	 * @param bool      trim
	 *
	 * @return string
	 */
	public static String formatHistoryValue(IIdentityBean idBean, SQLExecutor executor,String value, Map item) {
		return formatHistoryValue(idBean, executor,value, item, true);
	}
	
	/**
	 * Format history value.
	 * First format the value according to the configuration of the item. Then apply the value mapping to the formatted (!)
	 * value.
	 *
	 * @param mixed     value
	 * @param array     item
	 * @param int       Nest.value(item,"value_type").$()     type of the value: ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64, ...
	 * @param string    Nest.value(item,"units").$()          units of item
	 * @param int       Nest.value(item,"valuemapid").$()     id of mapping set of values
	 * @param bool      trim
	 *
	 * @return string
	 */
	public static String formatHistoryValue(IIdentityBean idBean, SQLExecutor executor,String value, Map item, boolean trim) {
		Object mapping = false;

		// format value
		if (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT || Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_UINT64) {
			value = convert_units(map(
					"value", value,
					"units", Nest.value(item,"units").$()
			));
		} else if (Nest.value(item,"value_type").asInteger() != ITEM_VALUE_TYPE_STR
			&& Nest.value(item,"value_type").asInteger() != ITEM_VALUE_TYPE_TEXT
			&& Nest.value(item,"value_type").asInteger() != ITEM_VALUE_TYPE_LOG) {
			value = _("Unknown value type");
		}

		// apply value mapping
		switch (Nest.value(item,"value_type").asInteger()) {
			case ITEM_VALUE_TYPE_STR:
				mapping = getMappedValue(idBean, executor, value, Nest.value(item,"valuemapid").asLong());
			// break; is not missing here
			case ITEM_VALUE_TYPE_TEXT:
			case ITEM_VALUE_TYPE_LOG:
				if (trim && rda_strlen(value) > 20) {
					value = rda_substr(value, 0, 20)+"...";
				}

				if (!empty(mapping)) {
					value = mapping+" ("+value+")";
				}
				break;
			default:
				value = applyValueMap(idBean, executor, value, Nest.value(item,"valuemapid").asLong());
		}

		return value;
	}
	
	public static String getMappedValue(IIdentityBean idBean, SQLExecutor executor, String value, long valueMapId) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbMappings = DBselect(executor,
			"SELECT m.newvalue"+
			" FROM mappings m"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "mappings", "m")+
			    " AND m.valuemapid="+sqlParts.marshalParam(valueMapId)+
				" AND m.value="+sqlParts.marshalParam(value),
			sqlParts.getNamedParams()
		);
		Map mapping = DBfetch(dbMappings);
		if (!empty(mapping)) {
			String newvalue = Nest.value(mapping,"newvalue").asString();
			return newvalue;
		}
		return null;
	}

	public static String applyValueMap(IIdentityBean idBean, SQLExecutor executor, String value, long valueMapId) {
		String mapping = getMappedValue(idBean, executor, value, valueMapId);
		return (empty(mapping)) ? value : mapping+" ("+value+")";
	}
	
	/**
	 * Retrieves from DB historical data for items and applies functional calculations.
	 * If fails for some reason, returns UNRESOLVED_MACRO_STRING.
	 *
	 * @param array		item
	 * @param string	item["value_type"]	type of item, allowed: ITEM_VALUE_TYPE_FLOAT and ITEM_VALUE_TYPE_UINT64
	 * @param string	item["itemid"]		ID of item
	 * @param string	item["units"]		units of item
	 * @param string	function			function to apply to time period from param, allowed: min, max and avg
	 * @param string	parameter			formatted parameter for function, example: \"2w\" meaning 2 weeks
	 *
	 * @return string item functional value from history
	 */
	public static String getItemFunctionalValue(SQLExecutor executor,Map item, String function, String parameter) {
		// check whether function is allowed
		if (!in_array(function, array("min", "max", "avg")) || parameter == null || parameter.length()==0) {
			return UNRESOLVED_MACRO_STRING;
		}

		parameter = Nest.as(convertFunctionValue(parameter)).asString();

		if (bccomp(parameter, 0) == 0) {
			return UNRESOLVED_MACRO_STRING;
		}

		// allowed item types for min, max and avg function
		CArray historyTables = map(ITEM_VALUE_TYPE_FLOAT, "history", ITEM_VALUE_TYPE_UINT64, "history_uint");

		if (!isset(historyTables,item.get("value_type"))) {
			return UNRESOLVED_MACRO_STRING;
		} else {
			// search for item function data in DB corresponding history table
			Map params = new HashMap();
			params.put("itemid", Nest.value(item,"itemid").$());
			CArray<Map> result = DBselect(executor,
				"SELECT "+function+"(value) AS value"+
				" FROM "+Nest.value(historyTables,item.get("value_type")).$()+
				" WHERE clock>"+(time() - Nest.as(parameter).asInteger())+
				" AND itemid=#{itemid}"+
				" HAVING COUNT(*)>0", // necessary because DBselect() return 0 if empty data set, for graph templates
				params
			);
			Map row = null;
			if (!empty(row = DBfetch(result))) {
				return convert_units(map("value", Nest.value(row,"value").$(), "units", Nest.value(item,"units").$()));
			} else {// no data in history
				return UNRESOLVED_MACRO_STRING;
			}
		}
	}
	
	/**
	 * Returns the history value of the item at the given time. If no value exists at the given time, the function
	 * will return the previous value.
	 *
	 * The db_item parameter must have the value_type and itemid properties set.
	 *
	 * @param array db_item
	 * @param int clock
	 * @param int ns
	 *
	 * @return string
	 */
	public static String item_get_history(IIdentityBean idBean, SQLExecutor executor, Map db_item, int clock, int ns) {
		String value = null;
		String table = CHistoryManager.getTableName(Nest.value(db_item,"value_type").asInteger());
		String sql = "SELECT value"+
							" FROM " + table +
							" WHERE itemid=#{itemid}" +
								" AND clock=#{clock}" +
								" AND ns=#{ns}";
		Map params = new HashMap();
		params.put("itemid", Nest.value(db_item,"itemid").asLong());
		params.put("clock", clock);
		params.put("ns", ns);
		Map row = DBfetch(DBselect(executor, sql, 1, params));
		if (row != null) {
			value = Nest.value(row,"value").asString();
		}
		if (value != null) {
			return value;
		}

		int max_clock = 0;		
		sql = "SELECT DISTINCT clock"+
				   " FROM " + table +
				  " WHERE itemid=#{itemid}" +
					" AND clock=#{clock}" +
					" AND ns<#{ns}";
		row = DBfetch(DBselect(executor, sql, 1, params));
		if (row!=null) {
			max_clock = Nest.value(row,"clock").asInteger();
		}
		if (max_clock == 0) {
			sql = "SELECT MAX(clock) AS clock" +
					   " FROM " + table +
					  " WHERE itemid=#{itemid}" +
						  " AND clock<#{clock}";
			row = DBfetch(DBselect(executor, sql, 1, params));
			if (row!=null) {
				max_clock = Nest.value(row,"clock").asInteger();
			}
		}
		if (max_clock == 0) {
			return value;
		}

		if (clock == max_clock) {
			sql = "SELECT value" +
					   " FROM " + table +
					 " WHERE itemid=#{itemid}" +
					     " AND clock=#{clock}" + 
						 " AND ns<#{ns}";
		} else {
			sql = "SELECT value" +
					   " FROM "+ table +
					  " WHERE itemid=#{itemid}" +
						 " AND clock=#{clock}" +
				" ORDER BY itemid,clock desc,ns desc";
		}
		row = DBfetch(DBselect(executor, sql, 1, params));
		if (row != null) {
			value = Nest.value(row,"value").asString();
		}
		return value;
	}
	
	/**
	 * Check if current time is within the given period.
	 *
	 * @param string period	time period format: \"wd[-wd2],hh:mm-hh:mm\"
	 * @param int now			current timestamp
	 *
	 * @return bool		true - within period, false - out of period
	 */
	@CodeConfirmed("blue.2.2.5")
	public static boolean checkTimePeriod(String period, int now) {
		CArray<String> ss = sscanf(period, "%d-%d,%d:%d-%d:%d");
		String d1 = ss.get(0), 
				d2 = ss.get(1), 
				h1 = ss.get(2), 
				m1 = ss.get(3), 
				h2 = ss.get(4), 
				m2 = ss.get(5);
		
		if (ss.size() != 6) {
			ss = sscanf(period, "%d,%d:%d-%d:%d");
			d1 = ss.get(0); 
			h1 = ss.get(1); 
			m1 = ss.get(2); 
			h2 = ss.get(3); 
			m2 = ss.get(4);
			if (ss.size() != 5) {
				// delay period format is wrong - skip
				return false;
			}
			d2 = d1;
		}

		CArray<Integer> tm = localtime(now, true);
		int day = (Nest.value(tm,"tm_wday").asInteger() == 0) ? 7 : Nest.value(tm,"tm_wday").asInteger();
		int sec = SEC_PER_HOUR * Nest.value(tm,"tm_hour").asInteger() + SEC_PER_MIN * Nest.value(tm,"tm_min").asInteger() + Nest.value(tm,"tm_sec").asInteger();

		int sec1 = SEC_PER_HOUR * asInteger(h1) + SEC_PER_MIN * asInteger(m1);
		int sec2 = SEC_PER_HOUR * asInteger(h2) + SEC_PER_MIN * asInteger(m2);

		return asInteger(d1) <= day && day <= asInteger(d2) && sec1 <= sec && sec < sec2;
	}
	
	private static Pattern PATTERN_FLEX_INTERVAL = Pattern.compile("\\d/"); //"%d/%29s"
	@CodeConfirmed("blue.2.2.5")
	public static int getItemDelay(int delay, String flexIntervals) {
		if (!empty(delay) || rda_empty(flexIntervals)) {
			return delay;
		}
		
		int minDelay = SEC_PER_YEAR;
		String[] _flexIntervals = explode(";", flexIntervals);
		for(String flexInterval: _flexIntervals) {
//			if (sscanf(flexInterval, \"%d/%29s\", flexDelay, flexPeriod) != 2) {
//				continue;
//			}
//			minDelay = min(minDelay, flexDelay);
			
			Matcher matcher = PATTERN_FLEX_INTERVAL.matcher(flexInterval);
			if(matcher.find()) {
				String flexDelay = matcher.group(1);
//				String flexPeriod = matcher.group(2);
				minDelay = min(EasyObject.asDouble(minDelay), EasyObject.asDouble(flexDelay)).intValue();
			}
		}
		return minDelay;
	}
	
	/**
	 * Return delay value that is currently applicable
	 *
	 * @param int delay                 default delay
	 * @param array arrOfFlexIntervals  array of intervals in format: \"d/wd[-wd2],hh:mm-hh:mm\"
	 * @param int now                   current timestamp
	 *
	 * @return int                       delay for a current timestamp
	 */
	@CodeConfirmed("blue.2.2.5")
	public static int getCurrentDelay(int delay, CArray<String> arrOfFlexIntervals, int now) {
		if (empty(arrOfFlexIntervals)) {
			return delay;
		}

		int currentDelay = -1;

		for(String flexInterval: arrOfFlexIntervals) {
			CArray<String> ss  = sscanf(flexInterval, "%d/%29s");
			String flexDelay = ss.get(0),
					flexPeriod = ss.get(1);
			if (ss.size() != 2) {
				continue;
			}
			if ((currentDelay == -1 || asInteger(flexDelay) < currentDelay) && checkTimePeriod(flexPeriod, now)) {
				currentDelay = asInteger(flexDelay);
			}
		}

		if (currentDelay == -1) {
			return delay;
		}

		return currentDelay;
	}
	
	/**
	 * Return time of next flexible interval
	 *
	 * @param array arrOfFlexIntervals  array of intervals in format: \"d/wd[-wd2],hh:mm-hh:mm\"
	 * @param int now                   current timestamp
	 * @param int nextInterval          timestamp of a next interval
	 *
	 * @return bool                      false if no flexible intervals defined
	 */
	@CodeConfirmed("blue.2.2.5")
	public static boolean getNextDelayInterval(CArray<String> arrOfFlexIntervals, int now, TObj nextInterval) {
		if (empty(arrOfFlexIntervals)) {
			return false;
		}

		int next = 0;
		CArray<Integer> tm = localtime(now, true);
		int day = (Nest.value(tm,"tm_wday").asInteger() == 0) ? 7 : Nest.value(tm,"tm_wday").asInteger();
		int sec = SEC_PER_HOUR * Nest.value(tm,"tm_hour").asInteger() + SEC_PER_MIN * Nest.value(tm,"tm_min").asInteger() + Nest.value(tm,"tm_sec").asInteger();

		for(String flexInterval: arrOfFlexIntervals) {
			CArray<String> ss = sscanf(flexInterval, "%d/%d-%d,%d:%d-%d:%d");
			@SuppressWarnings("unused")
			String  delay = ss.get(0), 
					d1 = ss.get(1), 
					d2 = ss.get(2), 
					h1 = ss.get(3), 
					m1 = ss.get(4), 
					h2 = ss.get(5), 
					m2 = ss.get(6);
			
			if (ss.size() != 7) {
				ss = sscanf(flexInterval, "%d/%d,%d:%d-%d:%d");
				delay = ss.get(0);
				d1 = ss.get(1); 
				h1 = ss.get(2); 
				m1 = ss.get(3); 
				h2 = ss.get(4); 
				m2 = ss.get(5);
				if (ss.size() != 6) {
					continue;
				}
				d2 = d1;
			}

			int sec1 = SEC_PER_HOUR * asInteger(h1) + SEC_PER_MIN * asInteger(m1);
			int sec2 = SEC_PER_HOUR * asInteger(h2) + SEC_PER_MIN * asInteger(m2);

			// current period
			if (asInteger(d1) <= day && day <= asInteger(d2) && sec1 <= sec && sec < sec2) {
				if (next == 0 || next > now - sec + sec2) {
					// the next second after the current interval's upper bound
					next = now - sec + sec2;
				}
			}
			// will be active today
			else if (asInteger(d1) <= day && asInteger(d2) >= day && sec < sec1) {
				if (next == 0 || next > now - sec + sec1) {
					next = now - sec + sec1;
				}
			}
			else {
				int nextDay = (day + 1 <= 7) ? day + 1 : 1;

				// will be active tomorrow
				if (asInteger(d1) <= nextDay && nextDay <= asInteger(d2)) {
					if (next == 0 || next > now - sec + SEC_PER_DAY + sec1) {
						next = now - sec + SEC_PER_DAY + sec1;
					}
				}
				// later in the future
				else {
					int dayDiff = -1;

					if (day < asInteger(d1)) {
						dayDiff = asInteger(d1) - day;
					}
					if (day >= asInteger(d2)) {
						dayDiff = (asInteger(d1) + 7) - day;
					}
					if (asInteger(d1) <= day && day < asInteger(d2)) {
						// should never happen, could not deduce day difference
						dayDiff = -1;
					}
					if (dayDiff != -1 && (next == 0 || next > now - sec + SEC_PER_DAY * dayDiff + sec1)) {
						next = now - sec + SEC_PER_DAY * dayDiff + sec1;
					}
				}
			}
		}
		if (next != 0) {
			nextInterval.$(next);
		}
		return next != 0;
	}
	
	/**
	 * Calculate nextcheck timestamp for an item
	 *
	 * the parameter flexIntervals accepts data in a format:
	 *
	 *           +------------[;]<----------+
	 *           |                          |
	 *         ->+-[d/wd[-wd2],hh:mm-hh:mm]-+
	 *
	 *         d       - delay (0-n)
	 *         wd, wd2 - day of week (1-7)
	 *         hh      - hours (0-24)
	 *         mm      - minutes (0-59)
	 *
	 * @param string seed               seed value applied to delay to spread item checks over the delay period
	 * @param int itemType
	 * @param int delay                 default delay, can be overriden
	 * @param string flexIntervals      flexible intervals
	 * @param int now                   current timestamp
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static int calculateItemNextcheck(String seed, int itemType, int delay, String flexIntervals, int now) {
		int nextcheck = 0;
		// special processing of active items to see better view in queue
		if (itemType == ITEM_TYPE_IRADAR_ACTIVE) {
			if (delay != 0) {
				nextcheck = now + delay;
			}
			else {
				nextcheck = RDA_JAN_2038;
			}
		}
		else {
			// try to find the nearest "nextcheck" value with condition "now" < "nextcheck" < "now" + SEC_PER_YEAR
			// if it is not possible to check the item within a year, fail

			String[] arrOfFlexIntervals = explode(";", flexIntervals);
			int t = now;
			int tmax = now + SEC_PER_YEAR;
			int try_ = 0;

			while (t < tmax) {
				// calculate "nextcheck" value for the current interval
				int currentDelay = getCurrentDelay(delay, CArray.valueOf(arrOfFlexIntervals), t);

				if (currentDelay != 0) {
					nextcheck = currentDelay * floor(t / currentDelay) + (asInteger(seed) % currentDelay);

					if (try_ == 0) {
						while (nextcheck <= t) {
							nextcheck += currentDelay;
						}
					}
					else {
						while (nextcheck < t) {
							nextcheck += currentDelay;
						}
					}
				}
				else {
					nextcheck = RDA_JAN_2038;
				}

				// "nextcheck" < end of the current interval ?
				// the end of the current interval is the beginning of the next interval - 1
				TObj nextInterval = TObj.as(0);
				if (getNextDelayInterval(CArray.valueOf(arrOfFlexIntervals), t, nextInterval) && nextcheck >= nextInterval.asInteger()) {
					// "nextcheck" is beyond the current interval
					t = nextInterval.asInteger();
					try_++;
				}
				else {
					break;
				}
			}
		}

		return nextcheck;
	}
	
	/**
	 * Check if given character is a valid key id char
	 * this function is a copy of is_key_char() from /src/libs/rdacommon/misc.c
	 * don't forget to take look in there before changing anything
	 *
	 * @author Konstantin Buravcov
	 * @param string char
	 * @return bool
	 */
	@CodeConfirmed("blue.2.2.5")
	public static boolean isKeyIdChar(char ch) {
		return (
				(ch >= 'a' && ch <= 'z')
				|| ch == '.' || ch == '_' || ch == '-'
				|| (ch >= 'A' && ch <= 'Z')
				|| (ch >= '0' && ch <= '9')
			);
	}
	
	/*
	 * Description:
	 *	Function returns true if http items exists in the items array.
	 *	The array should contain a field "type"
	 */
	@CodeConfirmed("blue.2.2.5")
	public static boolean httpItemExists(CArray<Map> items) {
		for (Map item : items) {
			if (Nest.value(item, "type").asInteger() == ITEM_TYPE_HTTPTEST) {
				return true;
			}
		}
		return false;
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static String getParamFieldNameByType(int itemType) {
		switch (itemType) {
		case ITEM_TYPE_SSH:
		case ITEM_TYPE_TELNET:
		case ITEM_TYPE_JMX:
			return "params_es";
		case ITEM_TYPE_DB_MONITOR:
			return "params_ap";
		case ITEM_TYPE_CALCULATED:
			return "params_f";
		default:
			return "params";
		}
	}
	
	@CodeConfirmed("blue.2.2.5")
	public static String getParamFieldLabelByType(int itemType) {
		switch (itemType) {
		case ITEM_TYPE_SSH:
		case ITEM_TYPE_TELNET:
		case ITEM_TYPE_JMX:
			return _("Executed script");
		case ITEM_TYPE_DB_MONITOR:
			return _("SQL query");
		case ITEM_TYPE_CALCULATED:
			return _("Formula");
		default:
			return "params";
		}
	}
	
	/**
	 * Quoting param if it contain special characters.
	 *
	 * @param string param
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String quoteItemKeyParam(String param) {
		if ((param ==null || param.length()==0) || (param.charAt(0)!= '"' && param.indexOf(',')==-1 && param.indexOf(']')==-1)) {
			return param;
		}
		return '"'+param.replaceAll("\"", "\\\\\"")+'"';
	}
	
}
