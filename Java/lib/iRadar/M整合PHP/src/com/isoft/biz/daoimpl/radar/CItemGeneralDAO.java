package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ltrim;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_ITEM_VALUE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_ANY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_JMX;
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
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_JMX;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_SSH;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_TELNET;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.RDA_JAN_2038;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.GettextwrapperUtil._params;
import static com.isoft.iradar.inc.ItemsUtil.calculateItemNextcheck;
import static com.isoft.iradar.inc.ItemsUtil.get_same_applications_for_host;
import static com.isoft.iradar.inc.ItemsUtil.itemTypeInterface;
import static com.isoft.iradar.inc.ValidateUtil.validatePortNumberOrMacro;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.object.CUpdateDiscoveredValidator;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class CItemGeneralDAO<P extends CParamGet> extends CCoreLongKeyDAO<P> {
	
	protected static String ERROR_EXISTS_TEMPLATE = "existsTemplate";
	protected static String ERROR_EXISTS = "exists";
	protected static String ERROR_NO_INTERFACE = "noInterface";
	protected static String ERROR_INVALID_KEY = "invalidKey";
	
	protected Map<String,Map> fieldRules;

	public CItemGeneralDAO(IIdentityBean idBean, SQLExecutor executor, String tableName, String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
		
		// template - if templated item, value is taken from template item, cannot be changed on host
		// system - values should not be updated
		// host - value should be null for template items
		this.fieldRules = (Map)map(
			"type", 									map("template" , 1),
			"snmp_community", 			map(),
			"snmp_oid", 							map("template" , 1),
			"hostid", 								map(),
			"name", 								map("template" , 1),
			"description", 						map(),
			"key_", 									map("template" , 1),
			"delay", 								map(),
			"history", 								map(),
			"trends", 								map(),
			"status", 								map(),
			"value_type", 						map("template" , 1),
			"trapper_hosts", 					map(),
			"units", 								map("template" , 1),
			"multiplier", 							map("template" , 1),
			"delta", 								map(),
			"snmpv3_contextname", 		map(),
			"snmpv3_securityname", 		map(),
			"snmpv3_securitylevel", 		map(),
			"snmpv3_authprotocol", 		map(),
			"snmpv3_authpassphrase", 	map(),
			"snmpv3_privprotocol", 		map(),
			"snmpv3_privpassphrase", 	map(),
			"formula", 							map("template" , 1),
			"error", 								map("system" , 1),
			"lastlogsize", 						map("system" , 1),
			"logtimefmt", 						map(),
			"templateid", 						map("system" , 1),
			"valuemapid", 						map("template" , 1),
			"delay_flex", 							map(),
			"params", 							map(),
			"ipmi_sensor", 						map("template" , 1),
			"data_type", 							map("template" , 1),
			"authtype", 							map(),
			"username", 							map(),
			"password", 							map(),
			"publickey", 							map(),
			"privatekey", 						map(),
			"mtime", 								map("system" , 1),
			"flags", 								map(),
			"filter", 									map(),
			"interfaceid", 						map("host" , 1),
			"port", 									map(),
			"inventory_link", 					map(),
			"lifetime", 							map()
		);
		
		this.errorMessages = array_merge(errorMessages, map(
			ERROR_NO_INTERFACE, _("Cannot find host interface on \"%1$s\" for item key \"%2$s\".")
		));
	}

	/**
	 * @abstract
	 *
	 * @param array _options
	 *
	 * @return array
	 */
	@Override
	public abstract <T> T get(P params);

	/**
	 * Check items data.
	 *
	 * Any system field passed to the function will be unset.
	 *
	 * @throw APIException
	 *
	 * @param array _items passed by reference
	 * @param bool  _update
	 *
	 * @return void
	 */
	protected void checkInput(CArray<Map> items) {
		checkInput(items, false);
	}
	protected void checkInput(CArray<Map> items, boolean update) {
		CArray itemDbFields = null;
		CArray<Map> dbItems = null;
		CArray<Map>dbHosts = null;
		if (update) {
			itemDbFields = map("itemid", null);

			CArray<String> dbItemsFields = array("itemid", "templateid");
			for (Entry<String, Map> e : fieldRules.entrySet()) {
				String field = e.getKey();
			    Map rule = e.getValue();
				if (!isset(rule,"system")) {
					dbItemsFields.add(field);
				}
			}

			P options = getParamInstance();
			options.setOutput(dbItemsFields.valuesAsString());
			options.put("itemids", rda_objectValues(items, "itemid").valuesAsString());
			options.setEditable(true);
			options.setPreserveKeys(true);
			dbItems = get(options);

			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(new String[]{"hostid", "status", "name"});
			hoptions.setHostIds(rda_objectValues(dbItems, "hostid").valuesAsLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setEditable(true);
			hoptions.setSelectApplications(API_OUTPUT_REFER);
			hoptions.setPreserveKeys(true);
			dbHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		} else {
			itemDbFields = map(
				"name", null,
				"key_" , null,
				"hostid", null,
				"type", null,
				"value_type", null,
				"delay", "0",
				"delay_flex", ""
			);

			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(new String[]{"hostid", "status", "name"});
			hoptions.setHostIds(rda_objectValues(items, "hostid").valuesAsLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setEditable(true);
			hoptions.setSelectApplications(API_OUTPUT_REFER);
			hoptions.setPreserveKeys(true);
			dbHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		}

		// interfaces
		CHostIfaceGet hioptions = new CHostIfaceGet();
		hioptions.setOutput(new String[]{"interfaceid", "hostid", "type"});
		hioptions.setHostIds(rda_objectValues(dbHosts, "hostid").valuesAsLong());
		hioptions.setNopermissions(true);
		hioptions.setPreserveKeys(true);
		CArray<Map> interfaces = API.HostInterface(this.idBean, this.getSqlExecutor()).get(hioptions);

		if (update) {
			CUpdateDiscoveredValidator updateDiscoveredValidator = CValidator.init(new CUpdateDiscoveredValidator(),map(
				"allowed", array("itemid", "status"),
				"messageAllowedField", _("Cannot update \"%1$s\" for a discovered item.")
			));
			for(Map item : items) {
				// check permissions
				if (!isset(dbItems,item.get("itemid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_("No permissions to referred object or it does not exist!"));
				}

				// discovered fields, except status, cannot be updated
				checkPartialValidator(item, updateDiscoveredValidator, dbItems.get(item.get("itemid")));
			}

			items = extendObjects(tableName(), items, new String[]{"name"});
		}

		for (Entry<Object, Map> e : items.entrySet()) {
		    Object inum = e.getKey();
		    Map item = e.getValue();
			item = clearValues(item);

			Map fullItem = Clone.deepcopy(items.get(inum));

			if (!check_db_fields(itemDbFields, item)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to function."));
			}

			if (update) {
				check_db_fields(dbItems.get(item.get("itemid")), fullItem);

				checkNoParameters(
					item,
					new String[]{"templateid", "state"},
					_("Cannot update \"%1$s\" for item \"%2$s\"."),
					Nest.value(item,"name").asString()
				);

				// apply rules
				for (Entry<String, Map> re : fieldRules.entrySet()) {
					String field = re.getKey();
				    Map rules = re.getValue();
					if ((0 != Nest.value(fullItem,"templateid").asInteger() && isset(rules,"template")) || isset(rules,"system")) {
						unset(item,field);
					}
				}

				if (!isset(item,"key_")) {
					Nest.value(item,"key_").$(Nest.value(fullItem,"key_").$());
				}
				if (!isset(item,"hostid")) {
					Nest.value(item,"hostid").$(Nest.value(fullItem,"hostid").$());
				}

				// if a templated item is being assigned to an interface with a different type, ignore it
				Integer itemInterfaceType = itemTypeInterface(Nest.value(dbItems,item.get("itemid"),"type").asInteger());
				if (!empty(Nest.value(fullItem,"templateid").$()) && isset(item,"interfaceid") && isset(interfaces,item.get("interfaceid"))
						&& itemInterfaceType != INTERFACE_TYPE_ANY && Nest.value(interfaces,item.get("interfaceid"),"type").asInteger() != itemInterfaceType) {
					unset(item,"interfaceid");
				}
			} else {
				if (!isset(dbHosts,item.get("hostid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				check_db_fields(itemDbFields, fullItem);

				checkNoParameters(
					item,
					new String[]{"templateid", "state"},
					_("Cannot set \"%1$s\" for item \"%2$s\"."),
					Nest.value(item,"name").asString()
				);
			}

			Map host = dbHosts.get(fullItem.get("hostid"));

			if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_IRADAR_ACTIVE) {
				Nest.value(item,"delay_flex").$("");
			}
			if (Nest.value(fullItem,"value_type").asInteger() == ITEM_VALUE_TYPE_STR) {
				Nest.value(item,"delta").$(0);
			}
			if (Nest.value(fullItem,"value_type").asInteger() != ITEM_VALUE_TYPE_UINT64) {
				Nest.value(item,"data_type").$(0);
			}

			// check if the item requires an interface
			Integer itemInterfaceType = itemTypeInterface(Nest.value(fullItem,"type").asInteger());
			if (itemInterfaceType != null && Nest.value(host,"status").asInteger() != HOST_STATUS_TEMPLATE) {
				if (empty(Nest.value(fullItem,"interfaceid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No interface found."));
				} else if (!isset(interfaces,fullItem.get("interfaceid")) || bccomp(Nest.value(interfaces,fullItem.get("interfaceid"),"hostid").$(), Nest.value(fullItem,"hostid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Item uses host interface from non-parent host."));
				} else if (itemInterfaceType != INTERFACE_TYPE_ANY && Nest.value(interfaces,fullItem.get("interfaceid"),"type").asInteger() != itemInterfaceType) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Item uses incorrect interface type."));
				}
			} else {// no interface required, just set it to null
				Nest.value(item,"interfaceid").$(0);
			}

			// item key
			if ((Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_DB_MONITOR && strcmp(Nest.value(fullItem,"key_").asString(), RDA_DEFAULT_KEY_DB_MONITOR) == 0)
					|| (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_SSH && strcmp(Nest.value(fullItem,"key_").asString(), RDA_DEFAULT_KEY_SSH) == 0)
					|| (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_TELNET && strcmp(Nest.value(fullItem,"key_").asString(), RDA_DEFAULT_KEY_TELNET) == 0)
					|| (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_JMX && strcmp(Nest.value(fullItem,"key_").asString(), RDA_DEFAULT_KEY_JMX) == 0)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Check the key, please. Default example was passed."));
			}

			// key
			CItemKey itemKey = new CItemKey(Nest.value(fullItem,"key_").asString());
			if (!itemKey.isValid()) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_params(getErrorMsg(ERROR_INVALID_KEY), 
						Nest.value(fullItem,"key_").$(),
						Nest.value(fullItem,"name").$(),
						Nest.value(host,"name").$(),
						itemKey.getError()
					)
				);
			}

			// parameters
			if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_AGGREGATE) {
				CArray<String> _params = itemKey.getParameters();

				if (!str_in_array(itemKey.getKeyId(), array("grpmax", "grpmin", "grpsum", "grpavg"))
						|| count(_params) != 4
						|| !str_in_array(_params.get(2), array("last", "min", "max", "avg", "sum", "count"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Key \"%1$s\" does not match <grpmax|grpmin|grpsum|grpavg>[\"Host group(s)\", \"Item key\","+
							" \"<last|min|max|avg|sum|count>\", \"parameter\"].", itemKey.getKeyId()));
				}
			}

			// type of information
			if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_AGGREGATE && Nest.value(fullItem,"value_type").asInteger() != ITEM_VALUE_TYPE_FLOAT
					&& Nest.value(fullItem,"value_type").asInteger() != ITEM_VALUE_TYPE_UINT64) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Type of information must be \"Numeric (float)\" for aggregate items."));
			}

			// log
			if (Nest.value(fullItem,"value_type").asInteger() != ITEM_VALUE_TYPE_LOG && str_in_array(itemKey.getKeyId(), array("log", "logrt", "eventlog"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Type of information must be \"Log\" for log key."));
			}

			// update interval
			if (Nest.value(fullItem,"type").asInteger() != ITEM_TYPE_TRAPPER && Nest.value(fullItem,"type").asInteger() != ITEM_TYPE_SNMPTRAP) {
				int res = calculateItemNextcheck("0", Nest.value(fullItem,"type").asInteger(), Nest.value(fullItem,"delay").asInteger(), Nest.value(fullItem,"delay_flex").asString(), ((Long)time()).intValue());
				if (res == RDA_JAN_2038) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_("Item will not be refreshed. Please enter a correct update interval."));
				}
			}

			// ssh, telnet
			if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_SSH || Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_TELNET) {
				if (rda_empty(Nest.value(fullItem,"username").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No authentication user name specified."));
				}

				if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_SSH && Nest.value(fullItem,"authtype").asInteger() == ITEM_AUTHTYPE_PUBLICKEY) {
					if (rda_empty(Nest.value(fullItem,"publickey").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No public key file specified."));
					}
					if (rda_empty(Nest.value(fullItem,"privatekey").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No private key file specified."));
					}
				}
			}

			// snmp trap
			if (Nest.value(fullItem,"type").asInteger() == ITEM_TYPE_SNMPTRAP
					&& strcmp(Nest.value(fullItem,"key_").asString(), "snmptrap.fallback") != 0
					&& strcmp(itemKey.getKeyId(), "snmptrap") != 0) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("SNMP trap key is invalid."));
			}

			// snmp oid
			if ((in_array(Nest.value(fullItem,"type").asInteger(), array(ITEM_TYPE_SNMPV1, ITEM_TYPE_SNMPV2C, ITEM_TYPE_SNMPV3)))
					&& rda_empty(Nest.value(fullItem,"snmp_oid").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No SNMP OID specified."));
			}

			// snmp community
			if (in_array(Nest.value(fullItem,"type").asInteger(), array(ITEM_TYPE_SNMPV1, ITEM_TYPE_SNMPV2C))
					&& rda_empty(Nest.value(fullItem,"snmp_community").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No SNMP community specified."));
			}

			// snmp port
			if (isset(fullItem,"port") && !rda_empty(Nest.value(fullItem,"port").$()) && !validatePortNumberOrMacro(Nest.value(fullItem,"port").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Item \"%1$s:%2$s\" has invalid port: \"%3$s\".", Nest.value(fullItem,"name").$(), Nest.value(fullItem,"key_").$(), Nest.value(fullItem,"port").$()));
			}

			if (isset(fullItem,"snmpv3_securitylevel") && Nest.value(fullItem,"snmpv3_securitylevel").asInteger() != ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV) {
				// snmpv3 authprotocol
				if (str_in_array(Nest.value(fullItem,"snmpv3_securitylevel").asInteger(), array(ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV))) {
					if (isset(fullItem,"snmpv3_authprotocol") && (rda_empty(Nest.value(fullItem,"snmpv3_authprotocol").$())
							|| !str_in_array(Nest.value(fullItem,"snmpv3_authprotocol").asInteger(),
								array(ITEM_AUTHPROTOCOL_MD5, ITEM_AUTHPROTOCOL_SHA)))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect authentication protocol for item \"%1$s\".", Nest.value(fullItem,"name").$()));
					}
				}

				// snmpv3 privprotocol
				if (Nest.value(fullItem,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV) {
					if (isset(fullItem,"snmpv3_privprotocol") && (rda_empty(Nest.value(fullItem,"snmpv3_privprotocol").$())
							|| !str_in_array(Nest.value(fullItem,"snmpv3_privprotocol").asInteger(),
								array(ITEM_PRIVPROTOCOL_DES, ITEM_PRIVPROTOCOL_AES)))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect privacy protocol for item \"%1$s\".", Nest.value(fullItem,"name").$()));
					}
				}
			}

			// check that the given applications belong to the item's host
			if (isset(item,"applications") && !empty(Nest.value(item,"applications").$())) {
				CArray dbApplicationIds = rda_objectValues(Nest.value(host,"applications").$(), "applicationid");
				for(Object appId : Nest.value(item,"applications").asCArray()) {
					if (!in_array(appId, dbApplicationIds)) {
						String error = _s("Application with ID \"%1$s\" is not available on \"%2$s\".", appId, Nest.value(host,"name").$());
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, error);
					}
				}
			}
			checkDelayFlex(fullItem);
			checkSpecificFields(fullItem);
		}
		checkExistingItems(items);
	}
	
	protected boolean checkSpecificFields(Map item) {
		return true;
	}
	
	protected Map clearValues(Map item) {
		if (isset(item,"port") &&  !"".equals(Nest.value(item,"port").asString())) {
			Nest.value(item,"port").$(ltrim(Nest.value(item,"port").asString(), "0"));
			if ("".equals(Nest.value(item,"port").asString())) {
				Nest.value(item,"port").$(0);
			}
		}
		if (isset(item,"lifetime") &&  !"".equals(Nest.value(item,"lifetime").asString())) {
			Nest.value(item,"lifetime").$(ltrim(Nest.value(item,"lifetime").asString(), "0"));
			if ("".equals(Nest.value(item,"lifetime").asString())) {
				Nest.value(item,"lifetime").$(0);
			}
		}
		return item;
	}
	
	protected void errorInheritFlags(int flag, String key, String host) {
		switch (flag) {
			case RDA_FLAG_DISCOVERY_NORMAL:
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Item with key \"%1$s\" already exists on \"%2$s\" as an item.", key, host));
			case RDA_FLAG_DISCOVERY_RULE:
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Item with key \"%1$s\" already exists on \"%2$s\" as a discovery rule.", key, host));
			case RDA_FLAG_DISCOVERY_PROTOTYPE:
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Item with key \"%1$s\" already exists on \"%2$s\" as an item prototype.", key, host));
			case RDA_FLAG_DISCOVERY_CREATED:
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Item with key \"%1$s\" already exists on \"%2$s\" as an item created from item prototype.", key, host));
			default:
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Item with key \"%1$s\" already exists on \"%2$s\" as unknown item element.", key, host));
		}
	}
	
	/**
	 * Returns the interface that best matches the given item.
	 *
	 * @param array _itemType   An item
	 * @param array ifaces An array of interfaces to choose from
	 *
	 * @return array|boolean    The best matching interface;
	 *							an empty array of no matching interface was found;
	 *							false, if the item does not need an interface
	 */
	public static Map findInterfaceForItem(Map item, CArray<Map> interfaces) {
		CArray<Map> typeInterface = array();
		for(Map iface : interfaces) {
			if (Nest.value(iface,"main").asInteger() == 1) {
				Nest.value(typeInterface,iface.get("type")).$(iface);
			}
		}
		// find item interface type
		Integer type = itemTypeInterface(Nest.value(item,"type").asInteger());
		Map matchingInterface = new HashMap();
		// the item can use any interface
		if (type!=null && type==INTERFACE_TYPE_ANY) {
			CArray<Integer> ifaceTypes = array(
				INTERFACE_TYPE_AGENT,
				INTERFACE_TYPE_SNMP,
				INTERFACE_TYPE_JMX,
				INTERFACE_TYPE_IPMI
			);
			for(int _itype : ifaceTypes) {
				if (isset(typeInterface,_itype)) {
					matchingInterface = typeInterface.get(_itype);
					break;
				}
			}
		} else if (type != null) {// the item uses a specific type of interface
			if(isset(typeInterface,type)){
				matchingInterface = typeInterface.get(type);
			}
		} else {// the item does not need an interface
			matchingInterface = null;
		}
		return matchingInterface;
	}
	
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		P options = getParamInstance();
		options.put("itemids", ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		P options = getParamInstance();
		options.put("itemids", ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}
	
	/**
	 * Checks whether the given items are referenced by any graphs and tries to
	 * unset these references, if they are no longer used.
	 *
	 * @throws APIException if at least one of the item can't be deleted
	 *
	 * @param array _itemids   An array of item IDs
	 */
	protected void checkGraphReference(Long... itemids) {
		checkUseInGraphAxis(itemids, true);
		checkUseInGraphAxis(itemids);
	}
	
	/**
	 * Checks if any of the given items are used as min/max Y values in a graph.
	 *
	 * if there are graphs, that have an y*_itemid column set, but the
	 * y*_type column is not set to GRAPH_YAXIS_TYPE_ITEM_VALUE, the y*_itemid
	 * column will be set to NULL.
	 *
	 * If the _checkMax parameter is set to true, the items will be checked against
	 * max Y values, otherwise, they will be checked against min Y values.
	 *
	 * @throws APIException if any of the given items are used as min/max Y values in a graph.
	 *
	 * @param array _itemids   An array of items IDs
	 * @param type _checkMax
	 */
	protected void checkUseInGraphAxis(Long... itemids) {
		checkUseInGraphAxis(itemids,false);
	}
	protected void checkUseInGraphAxis(Long[] itemids,boolean checkMax) {
		CArray filter = null;
		String itemIdColumn = null;
		String typeColumn = null;
		if (checkMax) {
			filter = map("ymax_itemid", itemids);
			itemIdColumn = "ymax_itemid";
			typeColumn = "ymax_type";
		} else {
			filter = map("ymin_itemid", itemids);
			itemIdColumn = "ymin_itemid";
			typeColumn = "ymin_type";
		}

		// make it work for both graphs and graph prototypes
		Nest.value(filter,"flags").$(new String[]{
			Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString(),
			Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString(),
			Nest.as(RDA_FLAG_DISCOVERY_CREATED).asString()
		});

		// check if the items are used in Y axis min/max values in any graphs
		CGraphGet goptions = new CGraphGet();
		goptions.setOutput(new String[]{itemIdColumn, typeColumn, "graphtype"});
		goptions.setFilter(filter);
		CArray<Map> graphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);

		CArray<Map> updateGraphs = array();
		for(Map graph : graphs) {
			// check if Y type is actually set to GRAPH_YAXIS_TYPE_ITEM_VALUE
			if (Nest.value(graph,typeColumn).asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				if (checkMax) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, "Could not delete these items because some of them are used as MAX values for graphs.");
				} else {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, "Could not delete these items because some of them are used as MIN values for graphs.");
				}
			} else {
				Nest.value(graph,itemIdColumn).$(null);
				updateGraphs.add(graph);
			}
		}

		// if there are graphs, that have an y*_itemid column set, but the
		// y*_type column is not set to GRAPH_YAXIS_TYPE_ITEM_VALUE, set y*_itemid to NULL.
		// Otherwise we won't be able to delete them.
		if (!empty(updateGraphs)) {
			API.Graph(this.idBean, this.getSqlExecutor()).update(updateGraphs);
		}
	}
	
	/**
	 * Updates the children of the item on the given hosts and propagates the inheritance to the child hosts.
	 *
	 * @abstract
	 *
	 * @param array _items          an array of items to inherit
	 * @param array|null _hostids   an array of hosts to inherit to; if set to null, the children will be updated on all
	 *                              child hosts
	 *
	 * @return bool
	 */
	abstract protected boolean inherit(CArray<Map> items, Long... hostids);

	/**
	 * Prepares and returns an array of child items, inherited from items _itemsToInherit on the given hosts.
	 *
	 * @param array      _itemsToInherit
	 * @param array|null _hostIds
	 *
	 * @return array an array of unsaved child items
	 */
	protected CArray<Map> prepareInheritedItems(CArray<Map> itemsToInherit, Long... hostIds) {
		// fetch all child hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "host", "status"});
		hoptions.setSelectParentTemplates(new String[]{"templateid"});
		hoptions.setSelectInterfaces(API_OUTPUT_EXTEND);
		hoptions.setTemplateIds(rda_objectValues(itemsToInherit, "hostid").valuesAsLong());
		if(hostIds.length != 0) {
			hoptions.setHostIds(hostIds);
		}
		hoptions.setPreserveKeys(true);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> chdHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		if (empty(chdHosts)) {
			return array();
		}

		CArray<Map> newItems = array();
		for (Entry<Object, Map> e : chdHosts.entrySet()) {
		    Object hostId = e.getKey();
		    Map host = e.getValue();
			CArray templateids = rda_toHash(Nest.value(host,"parentTemplates").$(), "templateid");

			// skip items not from parent templates of current host
			CArray<Map> parentItems = array();
			for (Entry<Object, Map> ei : itemsToInherit.entrySet()) {
			    Object inum = ei.getKey();
			    Map parentItem = ei.getValue();
				if (isset(templateids,parentItem.get("hostid"))) {
					Nest.value(parentItems,inum).$(parentItem);
				}
			}

			// check existing items to decide insert or update
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"itemid", "type", "key_", "flags", "templateid"});
			ioptions.setHostIds(Nest.as(hostId).asLong());
			ioptions.setPreserveKeys(true);
			ioptions.setNopermissions(true);
			ioptions.setFilter("flags");
			CArray<Map> exItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

			CArray<Map> exItemsKeys = rda_toHash(exItems, "key_");
			CArray<Map> exItemsTpl = rda_toHash(exItems, "templateid");

			for(Map parentItem : parentItems) {
				Map exItem = null;

				// check if an item of a different type with the same key exists
				if (isset(exItemsKeys,parentItem.get("key_"))) {
					exItem = exItemsKeys.get(parentItem.get("key_"));
					if (Nest.value(exItem,"flags").asInteger() != Nest.value(parentItem,"flags").asInteger()) {
						errorInheritFlags(Nest.value(exItem,"flags").asInteger(), Nest.value(exItem,"key_").asString(), Nest.value(host,"host").asString());
					}
				}

				// update by templateid
				if (isset(exItemsTpl,parentItem.get("itemid"))) {
					exItem = exItemsTpl.get(parentItem.get("itemid"));

					if (isset(exItemsKeys, parentItem.get("key_"))
						&& !idcmp(Nest.value(exItemsKeys,parentItem.get("key_"),"templateid").$(), Nest.value(parentItem,"itemid").$())) {
						throw CDB.exception(
							RDA_API_ERROR_PARAMETERS,
							_params(getErrorMsg(ERROR_EXISTS), Nest.value(parentItem,"key_").$(), Nest.value(host,"host").$())
						);
					}
				}

				// update by key
				if (isset(exItemsKeys,parentItem.get("key_"))) {
					exItem = exItemsKeys.get(parentItem.get("key_"));

					if (Nest.value(exItem,"templateid").asLong() > 0 && !idcmp(Nest.value(exItem,"templateid").$(), Nest.value(parentItem,"itemid").$())) {
						throw CDB.exception(
							RDA_API_ERROR_PARAMETERS,
							_params(getErrorMsg(ERROR_EXISTS_TEMPLATE), 
								Nest.value(parentItem,"key_").$(),
								Nest.value(host,"host").$()
							)
						);
					}
				}

				if (Nest.value(host,"status").asInteger() == HOST_STATUS_TEMPLATE || !isset(parentItem,"type")) {
					unset(parentItem,"interfaceid");
				}
				else if ((isset(parentItem,"type") && isset(exItem) && Nest.value(parentItem,"type").asInteger() != Nest.value(exItem,"type").asInteger()) || !isset(exItem)) {
					Map iface = findInterfaceForItem(parentItem, Nest.value(host,"interfaces").asCArray());
					if (!empty(iface)) {
						Nest.value(parentItem,"interfaceid").$(Nest.value(iface,"interfaceid").$());
					} else if (iface != null) {
						throw CDB.exception(
							RDA_API_ERROR_PARAMETERS,
							_params(getErrorMsg(ERROR_NO_INTERFACE), 
								Nest.value(host,"host").$(),
								Nest.value(parentItem,"key_").$()
							)
						);
					}
				} else {
					unset(parentItem,"interfaceid");
				}

				// copying item
				Map newItem = Clone.deepcopy(parentItem);
				Nest.value(newItem,"hostid").$(Nest.value(host,"hostid").$());
				Nest.value(newItem,"templateid").$(Nest.value(parentItem,"itemid").$());

				// setting item application
				if (isset(parentItem,"applications")) {
					Nest.value(newItem,"applications").$(get_same_applications_for_host(getSqlExecutor(),Nest.array(parentItem,"applications").asLong(), Nest.value(host,"hostid").asLong()));
				}

				if (!empty(exItem)) {
					Nest.value(newItem,"itemid").$(Nest.value(exItem,"itemid").$());
				} else {
					unset(newItem,"itemid");
				}
				newItems.add(newItem);
			}
		}

		return newItems;
	}
	
	/**
	 * Check if any item from list already exists.
	 * If items have item ids it will check for existing item with different itemid.
	 *
	 * @throw APIException
	 *
	 * @param array _items
	 */
	protected void checkExistingItems(CArray<Map> items) {
		items = Clone.deepcopy(items);
		
		CArray<CArray<String>> itemKeysByHostId = array();
		CArray itemIds = array();
		for(Map item : items) {
			if (!isset(itemKeysByHostId,item.get("hostid"))) {
				Nest.value(itemKeysByHostId,item.get("hostid")).$(array());
			}
			Nest.value(itemKeysByHostId,item.get("hostid")).asCArray().add(Nest.value(item,"key_").$());

			if (isset(item,"itemid")) {
				itemIds.add(Nest.value(item,"itemid").$());
			}
		}

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<String> sqlWhere = array();
		for (Entry<Object, CArray<String>> e : itemKeysByHostId.entrySet()) {
		    Object hostId = e.getKey();
		    CArray<String> keys = e.getValue();
			sqlWhere.add("(i.hostid="+hostId+" AND "+sqlParts.dual.dbConditionString("i.key_", keys.valuesAsString())+")");
		}

		if (!empty(sqlWhere)) {
			String sql = "SELECT i.key_,h.host"+
					" FROM items i,hosts h"+
					" WHERE i.tenantid=h.tenantid"+
					" AND i.hostid=h.hostid AND ("+implode(" OR ", sqlWhere)+")";

			// if we update existing items we need to exclude them from result.
			if (!empty(itemIds)) {
				sql += " AND "+sqlParts.dual.dbConditionLong("i.itemid", itemIds.valuesAsLong(), true);
			}
			CArray<Map> dbItems = DBselect(getSqlExecutor(),sql, 1,sqlParts.getNamedParams());
			for (Map dbItem : dbItems) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Item with key \"%1$s\" already exists on \"%2$s\".", Nest.value(dbItem,"key_").$(), Nest.value(dbItem,"host").$()));
			}
		}
	}
	
	/**
	 * Validate flexible intervals.
	 * Flexible intervals is string with format:
	 *   "delay/day1-day2,time1-time2;interval2;interval3;..." (day2 is optional)
	 * Examples:
	 *   600/5-7,00:00-09:00;600/1-2,00:00-09:00
	 *   600/5,0:0-9:0;600/1-2,0:0-9:0
	 *
	 * @param array _item
	 *
	 * @return bool
	 */
	protected void checkDelayFlex(Map item) {
		if (array_key_exists("delay_flex", item)) {
			String delayFlex = Nest.value(item,"delay_flex").asString();

			if ("".equals(delayFlex)) {
				return;
			}

			CTimePeriodValidator validator = CValidator.init(new CTimePeriodValidator(),map());
			String[] intervals = explode(";", rtrim(delayFlex, ";"));
			for(String interval : intervals) {
				CArray<String> matches = array();
				if (preg_match("^\\d+/(.+)$", interval, matches )==0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect flexible interval \"%1$s\".", interval));
				}

				if (!validator.validate(this.idBean, matches.get(1))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, validator.getError());
				}
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(P params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		// adding hosts
		if (Nest.value(params,"selectHosts").$() != null && !API_OUTPUT_COUNT.equals(Nest.value(params,"selectHosts").$())) {
			CRelationMap relationMap = createRelationMap(result, "itemid", "hostid");
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(relationMap.getRelatedLongIds());
			hoptions.setTemplatedHosts(true);
			hoptions.setOutput(Nest.value(params,"selectHosts").$());
			hoptions.setNopermissions(true);
			hoptions.setPreserveKeys(true);
			CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
			relationMap.mapMany(result, hosts, "hosts");
		}
	}	
	
}
