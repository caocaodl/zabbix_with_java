package com.isoft.iradar.macros;

import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.natsort;
import static com.isoft.iradar.Cphp.preg_match_all;
import static com.isoft.iradar.Cphp.preg_replace;
import static com.isoft.iradar.core.utils.EasyObject.asCArray;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.RDA_HISTORY_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_PREG_NUMBER;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ItemsUtil.formatHistoryValue;
import static com.isoft.iradar.inc.ItemsUtil.item_get_history;
import static com.isoft.iradar.inc.TranslateDefines.UNRESOLVED_MACRO_STRING;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CUserMacroGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("blue.2.2.5")
public class CMacrosResolverGeneral {
	
	@CodeConfirmed("blue.2.2.5")
	public static final String PATTERN_HOST = "\\{(HOSTNAME|HOST\\.HOST|HOST\\.NAME)\\}";
	public static final String PATTERN_HOST_INTERNAL = "HOST\\.HOST|HOSTNAME";
	public static final String PATTERN_MACRO_PARAM = "[1-9]?";
	public static final String PATTERN_HOST_FUNCTION = "\\{(HOSTNAME|HOST\\.HOST|HOST\\.NAME)([1-9]?)\\}";
	public static final String PATTERN_INTERFACE = "\\{(IPADDRESS|HOST\\.IP|HOST\\.DNS|HOST\\.CONN)\\}";
	public static final String PATTERN_INTERFACE_FUNCTION = "\\{(IPADDRESS|HOST\\.IP|HOST\\.DNS|HOST\\.CONN|HOST\\.PORT)([1-9]?)\\}";
	public static final String PATTERN_INTERFACE_FUNCTION_WITHOUT_PORT = "\\{(IPADDRESS|HOST\\.IP|HOST\\.DNS|HOST\\.CONN)([1-9]?)\\}";
	public static final String PATTERN_ITEM_FUNCTION = "\\{(ITEM\\.LASTVALUE|ITEM\\.VALUE)([1-9]?)\\}";
	public static final String PATTERN_ITEM_NUMBER = "\\$[1-9]";
	public static final String PATTERN_ITEM_MACROS = "\\{(HOSTNAME|HOST\\.HOST|HOST\\.NAME|IPADDRESS|HOST\\.IP|HOST\\.DNS|HOST\\.CONN)\\}";

	protected CArray configs;

	/**
	 * Interface priorities.
	 *
	 * @var array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<Integer> interfacePriorities = map(
		INTERFACE_TYPE_AGENT, 4,
		INTERFACE_TYPE_SNMP, 3,
		INTERFACE_TYPE_JMX, 2,
		INTERFACE_TYPE_IPMI, 1
	);
	
	/**
	 * Work config name.
	 *
	 * @var string
	 */
	@CodeConfirmed("blue.2.2.5")
	protected String config = "";
	
	/**
	 * Get reference macros for trigger.
	 * If macro reference non existing value it expands to empty string.
	 *
	 * @param string _expression
	 * @param string _text
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<String> getTriggerReference(String expression, String text) {
		CArray<String> result = new CArray();
		// search for reference macros $1, $2, $3, ...
		CArray<CArray> refNumbers = new CArray();
		preg_match_all("\\$([1-9])", text, refNumbers );

		if (empty(refNumbers)) {
			return result;
		}

		// replace functionids with string "function" to make values search easier
		expression = preg_replace("\\{[0-9]+\\}", "function", expression);

		// search for numeric values in expression
		CArray<CArray> values = new CArray();
		preg_match_all(RDA_PREG_NUMBER, expression, values);

		for (int refNum : refNumbers.get(1).valuesAsInteger()) {
			String refVal = ((CArray<String>)values.get(0)).get(refNum - 1);
			result.put("$" + refNum, isset(refVal) ? refVal : "");
		}
		return result;
	}

	/**
	 * Find macros in text by pattern.
	 *
	 * @param string _pattern
	 * @param array  _texts
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<String> findMacros(String pattern, CArray<String> texts) {
		CArray<String> result = new CArray();

		for (String text : texts) {
			CArray<CArray> _matches = new CArray();
			preg_match_all(pattern, text, _matches);
			result = array_merge(result, _matches.get(0));
		}

		return array_unique(result);
	}
	
	/**
	 * Find macros with function position.
	 *
	 * @param string _pattern
	 * @param string _text
	 *
	 * @return array	where key is found macro and value is array with related function position
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<CArray> findFunctionMacros(String _pattern, String _text) {
		CArray _result = array();

		CArray<CArray> _matches = array();
		preg_match_all(_pattern, _text, _matches);

		if(!_matches.isEmpty()){
			CArray<String> m1 = _matches.get(1);
			for(Entry<Object, String> entry: m1.entrySet()) {
				Object _num = entry.getKey();
				String _macro = entry.getValue();
			
				Object _fNum = empty(_matches.getNested(2,_num)) ? 0 : _matches.getNested(2,_num);
				_result.put(_macro, _fNum, _fNum);
			}
		}

		return _result;
	}
	
	
	/**
	 * Find function ids in trigger expression.
	 *
	 * @param string _expression
	 *
	 * @return array	where key is function id position in expression and value is function id
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray findFunctions(String _expression) {
		CArray<CArray> _matches = array();
		preg_match_all("\\{([0-9]+)\\}", _expression, _matches);

		CArray _functions = array();
		for(Object entryO: _matches.get(1).entrySet()) {
			Entry<Object, Object> entry = (Entry)entryO;
			Integer i = asInteger(entry.getKey());
			Object _functionid = entry.getValue();
			_functions.put(i + 1, _functionid);
		}

		// macro without number is same as 1. but we need to distinguish them, so it's treated as 0
		if (isset(_functions.get(1))) {
			_functions.put(0, _functions.get(1));
		}

		return _functions;
	}

	/**
	 * Add function macro name with corresponding value to replace to _macroValues array.
	 *
	 * @param array  _macroValues
	 * @param array  _fNums
	 * @param int    _triggerId
	 * @param string _macro
	 * @param string _replace
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray getFunctionMacroValues(CArray _macroValues, CArray _fNums, Object _triggerId, String _macro, String _replace) {
		for(Object _fNum: _fNums) {
			_macroValues.put(_triggerId, getFunctionMacroName(_macro, asInteger(_fNum)), _replace);
		}

		return _macroValues;
	}

	/**
	 * Get {ITEM.LASTVALUE} macro.
	 *
	 * @param mixed _lastValue
	 * @param array _item
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	protected String getItemLastValueMacro(IIdentityBean idBean, SQLExecutor executor, String _lastValue, CArray _item) {
		return (_lastValue == null) ? UNRESOLVED_MACRO_STRING : formatHistoryValue(idBean, executor, _lastValue, _item);
	}

	/**
	 * Get function macro name.
	 *
	 * @param string _macro
	 * @param int    _fNum
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	protected String getFunctionMacroName(String _macro, int _fNum) {
		return "{"+((_fNum == 0) ? _macro : _macro+_fNum)+"}";
	}

	/**
	 * Get {ITEM.VALUE} macro.
	 * For triggers macro is resolved in same way as {ITEM.LASTVALUE} macro. Separate methods are created for event description,
	 * where {ITEM.VALUE} macro resolves in different way.
	 *
	 * @param mixed _lastValue
	 * @param array _item
	 * @param array _trigger
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	protected String getItemValueMacro(IIdentityBean idBean, SQLExecutor executor, String _lastValue, CArray _item, CArray _trigger) {
		if (config == "eventDescription") {
			String _value = item_get_history(idBean, executor, _item, Nest.value(_trigger,"clock").asInteger(), Nest.value(_trigger,"ns").asInteger());

			return (_value == null) ? UNRESOLVED_MACRO_STRING : formatHistoryValue(idBean, executor, _value, _item);
		}
		else {
			return getItemLastValueMacro(idBean, executor, _lastValue, _item);
		}
	}

	/**
	 * Get interface macros.
	 *
	 * @param array	_macros
	 * @param array	_macroValues
	 * @param bool	_port
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray getIpMacros(IIdentityBean idBean, SQLExecutor executor, CArray _macros, CArray _macroValues, boolean _port) {
		if (!empty(_macros)) {
			String _selectPort;
			if (_port) {
				_selectPort = ",n.port";
			}
			else {
				_selectPort = null;
			}
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("f.triggerid,f.functionid,n.ip,n.dns,n.type,n.useip"+_selectPort);
			sqlParts.from.put("functions f" +
					" JOIN items i ON f.tenantid=i.tenantid AND f.itemid=i.itemid" +
					" JOIN interface n ON i.tenantid=n.tenantid AND i.hostid=n.hostid"
				);
			sqlParts.where.dbConditionTenants(idBean, "functions", "f");
			sqlParts.where.dbConditionInt("f.functionid", array_keys(_macros).valuesAsLong());
			sqlParts.where.put("n.main=1");
			
			CArray<Map> _dbInterfaces = DBselect(executor, sqlParts);

			// macro should be resolved to interface with highest priority (_priorities)
			CArray<Map> ifaces = array();
			for(Map _dbInterface: _dbInterfaces) {
				Object _functionid = _dbInterface.get("functionid");
				if (isset(ifaces.get(_functionid))
						&& interfacePriorities.get(ifaces.getNested(_functionid, "type")) > interfacePriorities.get(_dbInterface.get("type"))) {
					continue;
				}

				ifaces.put(_functionid, _dbInterface);
			}

			for(Map iface: ifaces) {
				for(Object entryO: Nest.value(_macros, iface.get("functionid")).asCArray().entrySet()) {
					Entry<String, CArray> entry = (Entry)entryO;
					String _macro = entry.getKey();
					CArray _fNums = entry.getValue();
					
					String _replace = "";
					if("IPADDRESS".equals(_macro) || "HOST.IP".equals(_fNums)) {
						_replace = Nest.value(iface,"ip").asString();
					}else if("HOST.DNS".equals(_macro)) {
						_replace = Nest.value(iface,"dns").asString();
					}else if("HOST.CONN".equals(_macro)) {
						_replace = Nest.value(iface,"useip").asBoolean() ? Nest.value(iface,"ip").asString() : Nest.value(iface,"dns").asString();
					}else if("HOST.PORT".equals(_macro)) {
						_replace = Nest.value(iface,"port").asString();
					}
					
					_macroValues = getFunctionMacroValues(_macroValues, _fNums, Nest.value(iface,"triggerid").$(), _macro, _replace);
				}
			}
		}

		return _macroValues;
	}

	/**
	 * Get item macros.
	 *
	 * @param array _macros
	 * @param array _triggers
	 * @param array _macroValues
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray getItemMacros(IIdentityBean idBean, SQLExecutor executor, CArray _macros, CArray _triggers, CArray _macroValues) {
		if (!empty(_macros)) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("f.triggerid,f.functionid,i.itemid,i.value_type,i.units,i.valuemapid");
			sqlParts.from.put("functions f" +
					" JOIN items i ON f.tenantid=i.tenantid AND f.itemid=i.itemid" +
					" JOIN hosts h ON i.tenantid=h.tenantid AND i.hostid=h.hostid"
				);
			sqlParts.where.dbConditionTenants(idBean, "functions", "f");
			sqlParts.where.dbConditionInt("f.functionid", array_keys(_macros).valuesAsLong());
			
			CArray<Map> _functions = DBselect(executor, sqlParts);

			CArray<CArray<Map>> _history = Manager.History(idBean, executor).getLast(_functions, 1, RDA_HISTORY_PERIOD);

			// false passed to DBfetch to get data without null converted to 0, which is done by default
			for(Map _func: _functions) {
				for(Object entryO: Nest.value(_macros, _func.get("functionid")).asCArray().entrySet()) {
					Entry<String, CArray> entry = (Entry)entryO;
					String _macro = entry.getKey();
					CArray _fNums = entry.getValue();
					String _lastValue = isset(_history.get(_func.get("itemid"))) ? _history.get(_func.get("itemid")).get(0).get("value").toString() : null;

					String _replace = "";
					if("ITEM.LASTVALUE".equals(_macro)) {
						_replace = getItemLastValueMacro(idBean, executor, _lastValue, asCArray(_func));
					}else if("ITEM.VALUE".equals(_macro)) {
						_replace = getItemValueMacro(idBean, executor, _lastValue, asCArray(_func), asCArray(_triggers.get(_func.get("triggerid"))));
					}

					_macroValues = getFunctionMacroValues(_macroValues, _fNums, Nest.value(_func,"triggerid").$(), _macro, _replace);
				}
			}
		}

		return _macroValues;
	}

	/**
	 * Get host macros.
	 *
	 * @param array _macros
	 * @param array _macroValues
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray getHostMacros(IIdentityBean idBean, SQLExecutor executor, CArray _macros, CArray _macroValues) {
		if (!empty(_macros)) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("f.triggerid,f.functionid,h.host,h.name");
			sqlParts.from.put("functions f" +
					" JOIN items i ON f.itemid=i.itemid AND f.itemid=i.itemid" +
					" JOIN hosts h ON i.hostid=h.hostid AND i.hostid=h.hostid"
				);
			sqlParts.where.dbConditionTenants(idBean, "functions", "f");
			sqlParts.where.dbConditionInt("f.functionid", array_keys(_macros).valuesAsLong());
			
			CArray<Map> _dbFuncs = DBselect(executor, sqlParts);
			
			for(Map _func: _dbFuncs) {
				for(Object entryO: Nest.value(_macros, _func.get("functionid")).asCArray().entrySet()) {
					Entry<String, CArray> entry = (Entry)entryO;
					String _macro = entry.getKey();
					CArray _fNums = entry.getValue();
				
					String _replace = "";
					if("HOSTNAME".equals(_macro) || "HOST.HOST".equals(_fNums)) {
						_replace = Nest.value(_func,"host").asString();
					}else if("HOST.NAME".equals(_macro)) {
						_replace = Nest.value(_func,"name").asString();
					}

					_macroValues = getFunctionMacroValues(_macroValues, _fNums, Nest.value(_func,"triggerid").$(), _macro, _replace);
				}
			}
		}

		return _macroValues;
	}
	
	/**
	 * Is type available.
	 *
	 * @param string _type
	 *
	 * @return bool
	 */
	@CodeConfirmed("blue.2.2.5")
	protected boolean isTypeAvailable(String _type) {
		return in_array(_type, Nest.value(configs, config, "types").asCArray());
	}

	/**
	 * Get source field.
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	protected String getSource() {
		return Nest.value(configs, config, "source").asString();
	}

	/**
	 * Get macros with values.
	 *
	 * @param array _data			Macros to resolve (array(hostids => array(hostid), macros => array(macro => null)))
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray getUserMacros(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _data) {
		/*
		 * User macros
		 */
		CArray _hostIds = array();
		for(Map _element: _data) {
			for(Object _hostId: Nest.value(_element,"hostids").asCArray()) {
				_hostIds.put(_hostId, _hostId);
			}
		}

		if (empty(_hostIds)) {
			return _data;
		}

		// hostid => array(templateid)
		CArray _hostTemplates = array();

		// hostid => array(macro => value)
		CArray _hostMacros = array();

		CHostGet hoptions = null;
		do {
			hoptions = new CHostGet();
			hoptions.setHostIds(_hostIds.valuesAsLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setOutput(new String[]{"hostid"});
			hoptions.setSelectParentTemplates(new String[]{"templateid"});
			hoptions.setSelectMacros(new String[]{"macro", "value"});
			CArray<Map> _dbHosts = API.Host(idBean, executor).get(hoptions);

			_hostIds = array();

			if (!empty(_dbHosts)) {
				for(Map _dbHost: _dbHosts) {
					Object _hostid = _dbHost.get("hostid");
					_hostTemplates.put(_hostid, rda_objectValues(Nest.value(_dbHost,"parentTemplates").$(), "templateid"));

					for(Map _dbMacro: (CArray<Map>)Nest.value(_dbHost,"macros").asCArray()) {
						if (!isset(_hostMacros.get(_hostid))) {
							_hostMacros.put(_hostid, array());
						}

						_hostMacros.put(_hostid, _dbMacro.get("macro"), Nest.value(_dbMacro,"value").$());
					}
				}

				for(Map _dbHost: _dbHosts) {
					// only unprocessed templates will be populated
					for(Object _templateId: Nest.value(_hostTemplates, _dbHost.get("hostid")).asCArray()) {
						if (!isset(_hostTemplates.get(_templateId))) {
							_hostIds.put(_templateId, _templateId);
						}
					}
				}
			}
		} while (!empty(_hostIds));

		boolean _allMacrosResolved = true;

		for(Map _element: _data) {
			_hostIds = array();

			for(Object _hostId: Nest.value(_element,"hostids").asCArray()) {
				_hostIds.put(_hostId, _hostId);
			}

			natsort(_hostIds);

			
			for(Entry<Object, Object> entry: ((CArray<Object>)Nest.value(_element,"macros").asCArray()).entrySet()) {
				Object _macro = entry.getKey();
				Object _value = entry.getValue();
			
				_value = getHostUserMacros(_hostIds, asString(_macro), _hostTemplates, _hostMacros);
				entry.setValue(_value);

				if (_value == null) {
					_allMacrosResolved = false;
				}
			}
//			unset(_value);
		}
//		unset(_element);

		if (_allMacrosResolved) {
			// there are no more hosts with unresolved macros
			return _data;
		}

		/* Global macros */
		CUserMacroGet umoptions = new CUserMacroGet();
		umoptions.setOutput(new String[]{"macro", "value"});
		umoptions.setGlobalMacro(true);
		CArray<Map> _dbGlobalMacros = API.UserMacro(idBean, executor).get(umoptions);
		if (!empty(_dbGlobalMacros)) {
			_dbGlobalMacros = rda_toHash(_dbGlobalMacros, "macro");

			_allMacrosResolved = true;

			for(Map _element: _data) {
				for(Entry<Object, Object> entry: ((CArray<Object>)Nest.value(_element,"macros").asCArray()).entrySet()) {
					Object _macro = entry.getKey();
					Object _value = entry.getValue();
					
					if (_value == null) {
						if (isset(_dbGlobalMacros.get(_macro))) {
							entry.setValue(Nest.value(_dbGlobalMacros, _macro, "value").$());
						}
						else {
							_allMacrosResolved = false;
						}
					}
				}
//				unset(_value);
			}
//			unset(_element);

			if (_allMacrosResolved) {
				// there are no more hosts with unresolved macros
				return _data;
			}
		}

		/*
		 * Unresolved macros stay as is
		 */
		for(Map _element: _data) {
			for(Entry<Object, Object> entry: ((CArray<Object>)Nest.value(_element,"macros").asCArray()).entrySet()) {
				Object _macro = entry.getKey();
				Object _value = entry.getValue();
				if (_value == null) {
					entry.setValue(_macro);
				}
			}
//			unset(_value);
		}
//		unset(_element);

		return _data;
	}

	/**
	 * Get user macro from the requested hosts.
	 *
	 * @param array  _hostIds		The sorted list of hosts where macros will be looked for (hostid => hostid)
	 * @param string _macro			Macro to resolve
	 * @param array  _hostTemplates	The list of linked templates (hostid => array(templateid))
	 * @param array  _hostMacros	The list of macros on hosts (hostid => array(macro => value))
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected Object getHostUserMacros(CArray _hostIds, String _macro, CArray _hostTemplates, CArray _hostMacros) {
		for(Object _hostId: _hostIds) {
			if (isset(_hostMacros.get(_hostId)) && isset(_hostMacros.getNested(_hostId, _macro))) {
				return Nest.value(_hostMacros, _hostId, _macro).$();
			}
		}

		if (empty(_hostTemplates)) {
			return null;
		}

		CArray _templateIds = array();

		for(Object _hostId: _hostIds) {
			if (isset(_hostTemplates.get(_hostId))) {
				for(Object _templateId: Nest.value(_hostTemplates, _hostId).asCArray()) {
					_templateIds.put(_templateId, _templateId);
				}
			}
		}

		if (!empty(_templateIds)) {
			natsort(_templateIds);
			return getHostUserMacros(_templateIds, _macro, _hostTemplates, _hostMacros);
		}

		return null;
	}
}
