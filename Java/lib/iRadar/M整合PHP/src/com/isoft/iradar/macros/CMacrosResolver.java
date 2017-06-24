package com.isoft.iradar.macros;

import static com.isoft.iradar.Cphp.PREG_OFFSET_CAPTURE;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match_all;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.substr_replace;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asCArray;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.INTERFACE_PRIMARY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_HOST_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_PREG_ITEM_KEY_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_TIME_SUFFIXES;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.ItemsUtil.formatHistoryValue;
import static com.isoft.iradar.inc.ItemsUtil.getItemFunctionalValue;
import static com.isoft.iradar.inc.TranslateDefines.UNRESOLVED_MACRO_STRING;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("blue.2.2.5")
public class CMacrosResolver extends CMacrosResolverGeneral{
	
	/**
	 * Supported macros resolving scenarios.
	 *
	 * @var array
	 */
	@CodeConfirmed("blue.2.2.5")
	
	public CMacrosResolver() {
		super.configs = map(
			"scriptConfirmation", map(
				"types", array("host", "interfaceWithoutPort", "user"),
				"method", "resolveTexts"
			),
			"httpTestName", map(
				"types", array("host", "interfaceWithoutPort", "user"),
				"method", "resolveTexts"
			),
			"hostInterfaceIpDns", map(
				"types", array("host", "agentInterface", "user"),
				"method", "resolveTexts"
			),
			"hostInterfaceIpDnsAgentPrimary", map(
				"types", array("host", "user"),
				"method", "resolveTexts"
			),
			"hostInterfacePort", map(
				"types", array("user"),
				"method", "resolveTexts"
			),
			"triggerName", map(
				"types", array("host", "interface", "user", "item", "reference"),
				"source", "description",
				"method", "resolveTrigger"
			),
			"triggerDescription", map(
				"types", array("host", "interface", "user", "item"),
				"source", "comments",
				"method", "resolveTrigger"
			),
			"triggerExpressionUser", map(
				"types", array("user"),
				"source", "expression",
				"method", "resolveTrigger"
			),
			"eventDescription", map(
				"types", array("host", "interface", "user", "item", "reference"),
				"source", "description",
				"method", "resolveTrigger"
			),
			"graphName", map(
				"types", array("graphFunctionalItem"),
				"source", "name",
				"method", "resolveGraph"
			)
		);
	}
	
	/**
	 * Resolve macros.
	 *
	 * Macros examples:
	 * reference: $1, $2, $3, ...
	 * user: {$MACRO1}, {$MACRO2}, ...
	 * host: {HOSTNAME}, {HOST.HOST}, {HOST.NAME}
	 * ip: {IPADDRESS}, {HOST.IP}, {HOST.DNS}, {HOST.CONN}
	 * item: {ITEM.LASTVALUE}, {ITEM.VALUE}
	 *
	 * @param array  _options
	 * @param string _options["config"]
	 * @param array  _options["data"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public CArray resolve(IIdentityBean idBean, SQLExecutor executor, CArray options) {
		if (empty(Nest.value(options,"data").$())) {
			return array();
		}

		this.config = Nest.value(options,"config").asString();

		// call method
		String method = Nest.value(configs, config, "method").asString();

		try {
			Object[] args = {idBean, executor, Nest.value(options,"data").asCArray()};
			Class[] types = {IIdentityBean.class, SQLExecutor.class, CArray.class};
			Method call = getClass().getDeclaredMethod(method, types);
			Object result = call.invoke(this, args);
			return asCArray(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Batch resolving macros in text using host id.
	 *
	 * @param array _data	(as _hostId => array(texts))
	 *
	 * @return array		(as _hostId => array(texts))
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<CArray> resolveTexts(IIdentityBean idBean, SQLExecutor executor, CArray<CArray> _data) {
		CArray _hostIds = array_keys(_data);

		CArray _macros = array();

		boolean _hostMacrosAvailable, _agentInterfaceAvailable, ifaceWithoutPortMacrosAvailable;
		_hostMacrosAvailable = _agentInterfaceAvailable = ifaceWithoutPortMacrosAvailable = false;

		if (isTypeAvailable("host")) {
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				CArray _hostMacros;
				if (!empty(_hostMacros = findMacros(PATTERN_HOST, _texts))) {
					for(Object _hostMacro: _hostMacros) {
						Nest.value(_macros, _hostId, _hostMacro).$(UNRESOLVED_MACRO_STRING);
					}
					_hostMacrosAvailable = true;
				}
			}
		}

		if (isTypeAvailable("agentInterface")) {
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				CArray ifaceMacros;
				if (!empty(ifaceMacros = findMacros(PATTERN_INTERFACE, _texts))) {
					for(Object ifaceMacro: ifaceMacros) {
						Nest.value(_macros, _hostId, ifaceMacro).$(UNRESOLVED_MACRO_STRING);
					}
					_agentInterfaceAvailable = true;
				}
			}
		}

		if (isTypeAvailable("interfaceWithoutPort")) {
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				CArray ifaceMacros;
				if (!empty(ifaceMacros = findMacros(PATTERN_INTERFACE, _texts))) {
					for(Object ifaceMacro: ifaceMacros) {
						Nest.value(_macros, _hostId, ifaceMacro).$(UNRESOLVED_MACRO_STRING);
					}
					ifaceWithoutPortMacrosAvailable = true;
				}
			}
		}
		
		// host macros
		if (_hostMacrosAvailable) {
			SqlBuilder sqlParts = new SqlBuilder();		
			sqlParts.select.put("h.hostid,h.name,h.host");
			sqlParts.from.put("hosts h");
			sqlParts.where.dbConditionTenants(idBean, "hosts", "h");
			sqlParts.where.dbConditionInt("h.hostid", _hostIds.valuesAsLong());
			
			CArray<Map> _dbHosts = DBselect(executor, sqlParts);
			
			for(Map _dbHost: _dbHosts) {
				Object _hostId = Nest.value(_dbHost,"hostid").$();

				CArray<String> _hostMacros;
				if (!empty(_hostMacros = findMacros(PATTERN_HOST, _data.get(_hostId)))) {
					for(String _hostMacro: _hostMacros) {
						if("{HOSTNAME}".equals(_hostMacro) || "{HOST.HOST}".equals(_hostMacro)) {
							Nest.value(_macros, _hostId, _hostMacro).$(_dbHost.get("host"));
						}else if("{HOST.NAME}".equals(_hostMacro)) {
							Nest.value(_macros, _hostId, _hostMacro).$(_dbHost.get("name"));
						}
					}
				}
			}
		}
		
		// interface macros, macro should be resolved to main agent interface
		if (_agentInterfaceAvailable) {
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				
				CArray ifaceMacros;
				if (!empty(ifaceMacros = findMacros(PATTERN_INTERFACE, _texts))) {
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("i.hostid,i.ip,i.dns,i.useip");
					sqlParts.from.put("interface i");
					sqlParts.where.dbConditionTenants(idBean, "interface", "i");
					sqlParts.where.put("i.main="+INTERFACE_PRIMARY);
					sqlParts.where.put("i.type="+INTERFACE_TYPE_AGENT);
					sqlParts.where.put("i.hostid="+sqlParts.marshalParam(_hostId));
					
					Map _dbInterface = DBfetch(DBselect(executor, sqlParts));
					
					CArray _dbInterfaceTexts = array(Nest.value(_dbInterface,"ip").$(), Nest.value(_dbInterface,"dns").$());

					if (!empty(findMacros(PATTERN_HOST, _dbInterfaceTexts))
							|| !empty(findMacros(RDA_PREG_EXPRESSION_USER_MACROS, _dbInterfaceTexts))) {
						String _saveCurrentConfig = config;

						CArray _dbInterfaceMacros = resolve(idBean, executor, map(
							"config", "hostInterfaceIpDnsAgentPrimary",
							"data", map(_hostId, _dbInterfaceTexts)
						));

						_dbInterfaceMacros = reset(_dbInterfaceMacros);
						Nest.value(_dbInterface,"ip").$(_dbInterfaceMacros.get(0));
						Nest.value(_dbInterface,"dns").$(_dbInterfaceMacros.get(1));

						config = _saveCurrentConfig;
					}

					for(Object ifaceMacro: ifaceMacros) {
						if("{IPADDRESS}".equals(ifaceMacro) || "{HOST.IP}".equals(ifaceMacro)) {
							Nest.value(_macros, _hostId, ifaceMacro).$(_dbInterface.get("ip"));
						}else if("{HOST.DNS}".equals(ifaceMacro)) {
							Nest.value(_macros, _hostId, ifaceMacro).$(_dbInterface.get("dns"));
						}else if("{HOST.CONN}".equals(ifaceMacro)) {
							Nest.value(_macros, _hostId, ifaceMacro).$(
									Nest.value(_dbInterface,"useip").asBoolean() 
									? Nest.value(_dbInterface,"ip").$() 
									: Nest.value(_dbInterface,"dns").$()
								);
						}
					}
				}
			}
		}
		
		// interface macros, macro should be resolved to interface with highest priority
		if (ifaceWithoutPortMacrosAvailable) {
			CArray<Map> ifaces = array();

			
			SqlBuilder sqlParts = new SqlBuilder();		
			sqlParts.select.put("i.hostid,i.ip,i.dns,i.useip,i.type");
			sqlParts.from.put("interface i");
			sqlParts.where.dbConditionTenants(idBean, "interface", "i");
			sqlParts.where.put("i.main="+INTERFACE_PRIMARY);
			sqlParts.where.dbConditionInt("i.hostid", _hostIds.valuesAsLong());
			sqlParts.where.dbConditionInt("i.type", interfacePriorities.valuesAsLong());
			
			CArray<Map> _dbInterfaces = DBselect(executor, sqlParts);

			for(Map _dbInterface: _dbInterfaces) {
				Object _hostId = Nest.value(_dbInterface,"hostid").$();

				if (isset(ifaces.get(_hostId))) {
					Object _dbPriority = interfacePriorities.get(_dbInterface.get("type"));
					Object _existPriority = interfacePriorities.get(ifaces.getNested(_hostId, "type"));

					if (asInteger(_dbPriority) > asInteger(_existPriority)) {
						ifaces.put(_hostId, _dbInterface);
					}
				}
				else {
					ifaces.put(_hostId, _dbInterface);
				}
			}

			if (!empty(ifaces)) {
				for(Entry<Object, Map> entry: ifaces.entrySet()) {
					Object _hostId = entry.getKey();
					Map iface = entry.getValue();
				
					CArray ifaceMacros;
					if (!empty(ifaceMacros = findMacros(PATTERN_INTERFACE, _data.get(_hostId)))) {
						for(Object ifaceMacro: ifaceMacros) {
							if("{IPADDRESS}".equals(ifaceMacro) || "{HOST.IP}".equals(ifaceMacro)) {
								Nest.value(_macros, _hostId, ifaceMacro).$(iface.get("ip"));
							}else if("{HOST.DNS}".equals(ifaceMacro)) {
								Nest.value(_macros, _hostId, ifaceMacro).$(iface.get("dns"));
							}else if("{HOST.CONN}".equals(ifaceMacro)) {
								Nest.value(_macros, _hostId, ifaceMacro).$(
										Nest.value(iface,"useip").asBoolean()
										? iface.get("ip")
										: iface.get("dns")
									);
							}

							// Resolving macros to AGENT main interface. If interface is AGENT macros stay unresolved.
							if (Nest.value(iface,"type").asInteger() != INTERFACE_TYPE_AGENT) {
								String _mhi = Nest.value(_macros, _hostId, ifaceMacro).asString();
								
								if (!empty(findMacros(PATTERN_HOST, array(_mhi)))
										|| !empty(findMacros(RDA_PREG_EXPRESSION_USER_MACROS, array(_mhi)))) {
									// attention recursion!
									CArray _macrosInMacros = resolveTexts(idBean, executor, (CArray)map(_hostId, array(_mhi)));
									Nest.value(_macros, _hostId, ifaceMacro).$(_macrosInMacros.getNested(_hostId, 0));
								}else if (!empty(findMacros(PATTERN_INTERFACE, array(_mhi)))) {
									Nest.value(_macros, _hostId, ifaceMacro).$(UNRESOLVED_MACRO_STRING);
								}
							}
						}
					}
				}
			}
		}
		
		// get user macros
		if (isTypeAvailable("user")) {
			CArray _userMacrosData = array();
			
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				
				CArray _userMacros = findMacros(RDA_PREG_EXPRESSION_USER_MACROS, _texts);

				for(Object _userMacro: _userMacros) {
					if (!isset(_userMacrosData.get(_hostId))) {
						_userMacrosData.put(_hostId, map(
							"hostids", array(_hostId),
							"macros", array()
						));
					}

					_userMacrosData.put(_hostId, "macros", _userMacro , null);
				}
			}

			CArray<Map> _userMacros = getUserMacros(idBean, executor, _userMacrosData);

			for(Entry<Object, Map> entry: _userMacros.entrySet()) {
				Object _hostId = entry.getKey();
				Map _userMacro = entry.getValue();
				
				_macros.put(_hostId, 
						isset(_macros.get(_hostId))
						? array_merge(asCArray(_macros.get(_hostId)), Nest.value(_userMacro,"macros").asCArray())
						: Nest.value(_userMacro,"macros").$()
					);
			}
		}
		
		// replace macros to value
		if (!empty(_macros)) {
			for (Entry<Object, CArray> entry : _data.entrySet()) {
				Object _hostId = entry.getKey();
				CArray<String> _texts = entry.getValue();
				
				if (isset(_macros.get(_hostId))) {
					for (Entry<Object, String> entry_texts : _texts.entrySet()) {
						Object _tnum = entry_texts.getKey();
						String _text = entry_texts.getValue();
						
						CArray<CArray> _matches = array();
						preg_match_all(PATTERN_HOST+"|"+PATTERN_INTERFACE+"|"+RDA_PREG_EXPRESSION_USER_MACROS, _text, _matches, PREG_OFFSET_CAPTURE);

						for (int i = count(_matches.get(0)) - 1; i >= 0; i--) {
							CArray _matche = asCArray( _matches.getNested(0 ,i) );

							Object _macrosValue = isset(_macros.getNested(_hostId, _matche.get(0))) ? _macros.getNested(_hostId, _matche.get(0)) : _matche.get(0);
							_text = substr_replace(_text, asString(_macrosValue), asInteger(_matche.get(1)), strlen(asString(_matche.get(0))));
						}

						_data.put(_hostId, _tnum, _text);
					}
				}
			}
		}

		return _data;
	}
	
	
	/**
	 * Resolve macros in trigger.
	 *
	 * @param string _triggers[_triggerId]["expression"]
	 * @param string _triggers[_triggerId]["description"]			depend from config
	 * @param string _triggers[_triggerId]["comments"]				depend from config
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray resolveTrigger(IIdentityBean idBean, SQLExecutor executor, CArray<Map> triggers) {
		CArray macros = map(
			"host", array(),
			"interfaceWithoutPort", array(),
			"interface", array(),
			"item", array()
		);
		
		CArray macroValues = array();
		CArray<Map> userMacrosData = array();

		// get source field
		String source = getSource();

		// get available functions
		boolean hostMacrosAvailable = isTypeAvailable("host");
		boolean ifaceWithoutPortMacrosAvailable = isTypeAvailable("interfaceWithoutPort");
		boolean ifaceMacrosAvailable = isTypeAvailable("interface");
		boolean itemMacrosAvailable = isTypeAvailable("item");
		boolean userMacrosAvailable = isTypeAvailable("user");
		boolean referenceMacrosAvailable = isTypeAvailable("reference");

		// find macros
		for(Entry<Object, Map> entry: triggers.entrySet()) {
			Object triggerId = entry.getKey();
			Map trigger = entry.getValue();
			if (userMacrosAvailable) {
				CArray<String> userMacros = findMacros(RDA_PREG_EXPRESSION_USER_MACROS, array(Nest.value(trigger, source).asString()));

				if (!empty(userMacros)) {
					if (!isset(userMacrosData,triggerId)) {
						userMacrosData.put(triggerId, map("macros", array(), "hostids", array()));
					}

					for(String userMacro: userMacros) {
						userMacrosData.put(triggerId, "macros", userMacro, null);
					}
				}
			}

			CArray functions = findFunctions(Nest.value(trigger,"expression").asString());

			if (hostMacrosAvailable) {
				for(Entry<Object, CArray> entry_findFunctionMacros: findFunctionMacros(PATTERN_HOST_FUNCTION, Nest.value(trigger, source).asString()).entrySet()) {
					Object macro = entry_findFunctionMacros.getKey();
					CArray fNums = entry_findFunctionMacros.getValue();
				
					for(Object fNum: fNums) {
						Nest.value(macroValues, triggerId, getFunctionMacroName(asString(macro), asInteger(fNum)), UNRESOLVED_MACRO_STRING);

						if (isset(functions,fNum)) {
							Nest.value(macros, "host", functions.get(fNum), macro).$s(true).add(fNum);
						}
					}
				}
			}

			if (ifaceWithoutPortMacrosAvailable) {
				for(Entry<Object, CArray> findFunctionMacro: findFunctionMacros(PATTERN_INTERFACE_FUNCTION_WITHOUT_PORT, Nest.value(trigger, source).asString()).entrySet()) {
					Object macro = findFunctionMacro.getKey();
					CArray fNums = findFunctionMacro.getValue();
					for(Object fNum: fNums) {
						Nest.value(macroValues, triggerId, getFunctionMacroName(asString(macro), asInteger(fNum)), UNRESOLVED_MACRO_STRING);

						if (isset(functions,fNum)) {
							Nest.value(macros, "interfaceWithoutPort", functions.get(fNum), macro).$s(true).add(fNum);
						}
					}
				}
			}

			if (ifaceMacrosAvailable) {
				for(Entry<Object, CArray> findFunctionMacro: findFunctionMacros(PATTERN_INTERFACE_FUNCTION, Nest.value(trigger, source).asString()).entrySet()) {
					Object macro = findFunctionMacro.getKey();
					CArray fNums = findFunctionMacro.getValue();
					for(Object fNum: fNums) {
						Nest.value(macroValues, triggerId, getFunctionMacroName(asString(macro), asInteger(fNum)), UNRESOLVED_MACRO_STRING);

						if (isset(functions,fNum)) {
							Nest.value(macros, "interface", functions.get(fNum), macro).$s(true).add(fNum);
						}
					}
				}
			}

			if (itemMacrosAvailable) {
				for(Entry<Object, CArray> findFunctionMacro: findFunctionMacros(PATTERN_ITEM_FUNCTION, Nest.value(trigger, source).asString()).entrySet()) {
					Object macro = findFunctionMacro.getKey();
					CArray fNums = findFunctionMacro.getValue();
					for(Object fNum: fNums) {
						Nest.value(macroValues, triggerId, getFunctionMacroName(asString(macro), asInteger(fNum)), UNRESOLVED_MACRO_STRING);

						if (isset(functions,fNum)) {
							Nest.value(macros, "item", functions.get(fNum), macro).$s(true).add(fNum);
						}
					}
				}
			}

			if (referenceMacrosAvailable) {
				for(Entry<Object, String> findFunctionMacro: getTriggerReference(Nest.value(trigger,"expression").asString(), Nest.value(trigger, source).asString()).entrySet()) {
					Object macro = findFunctionMacro.getKey();
					String value = findFunctionMacro.getValue();
					macroValues.put(triggerId, macro, value);
				}
			}
		}

		// get macro value
		String patternInterfaceFunction = null;
		if (hostMacrosAvailable) {
			macroValues = getHostMacros(idBean, executor, Nest.value(macros,"host").asCArray(), macroValues);
		}
		if (ifaceWithoutPortMacrosAvailable) {
			macroValues = getIpMacros(idBean, executor, Nest.value(macros,"interfaceWithoutPort").asCArray(), macroValues, false);
		}
		if (ifaceMacrosAvailable) {
			macroValues = getIpMacros(idBean, executor, Nest.value(macros,"interface").asCArray(), macroValues, true);
			patternInterfaceFunction = PATTERN_INTERFACE_FUNCTION;
		} else {
			patternInterfaceFunction = PATTERN_INTERFACE_FUNCTION_WITHOUT_PORT;
		}
		if (itemMacrosAvailable) {
			macroValues = getItemMacros(idBean, executor, Nest.value(macros,"item").asCArray(), triggers, macroValues);
		}
		if (!empty(userMacrosData)) {
			// get hosts for triggers
			CTriggerGet toptions = new CTriggerGet();
			toptions.setOutput(new String[]{"triggerid"});
			toptions.setSelectHosts(new String[]{"hostid"});
			toptions.setTriggerIds(array_keys(userMacrosData).valuesAsLong());
			toptions.setPreserveKeys(true);
			CArray<Map> dbTriggers = API.Trigger(idBean, executor).get(toptions);
			for(Entry<Object, Map> entry: userMacrosData.entrySet()) {
				Object triggerId = entry.getKey();
				//Map _userMacro = entry.getValue();
				if (isset(dbTriggers,triggerId)) {
					userMacrosData.put(triggerId, "hostids", rda_objectValues(dbTriggers.getNested(triggerId,"hosts"), "hostid"));
				}
			}

			// get user macros values
			CArray<Map> userMacros = getUserMacros(idBean, executor, userMacrosData);
			for(Entry<Object, Map> entry: userMacros.entrySet()) {
				Object triggerId = entry.getKey();
				Map userMacro = entry.getValue();
				macroValues.put(triggerId, 
						isset(macroValues,triggerId)
						? array_merge(asCArray(macroValues.get(triggerId)), Nest.value(userMacro,"macros").asCArray())
						: Nest.value(userMacro,"macros").$()
					);
			}
		}

		// replace macros to value
		for(Entry<Object, Map> entry: triggers.entrySet()) {
			Object triggerId = entry.getKey();
			Map trigger = entry.getValue();
			
			CArray<CArray> matches = array();
			preg_match_all(PATTERN_HOST_FUNCTION+
								"|"+patternInterfaceFunction+
								"|"+PATTERN_ITEM_FUNCTION+
								"|"+RDA_PREG_EXPRESSION_USER_MACROS+
								"|\\$([1-9])", Nest.value(trigger, source).asString(), matches, PREG_OFFSET_CAPTURE);
			if(!matches.isEmpty()){
				for (int i = count(matches.get(0)) - 1; i >= 0; i--) {
					CArray match = asCArray( matches.getNested(0, i) );
	
					Object macrosValue = isset(macroValues.getNested(triggerId, match.get(0))) ? macroValues.getNested(triggerId, match.get(0)) : match.get(0);
					trigger.put(source, substr_replace(Nest.value(trigger, source).asString(), asString(macrosValue), asInteger( match.get(1) ), strlen(asString(match.get(0)))));
				}
			}

			triggers.put(triggerId, source, trigger.get(source) );
		}

		return triggers;
	}
	

	/**
	 * Expand reference macros for trigger.
	 * If macro reference non existing value it expands to empty string.
	 *
	 * @param string _expression
	 * @param string _text
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public String resolveTriggerReference(String _expression, String _text) {
		for(Entry<Object, String> entry: getTriggerReference(_expression, _text).entrySet()) {
			Object _key = entry.getKey();
			String _value = entry.getValue();
		
			_text = str_replace(_key, _value, _text);
		}

		return _text;
	}

	/**
	 * Resolve functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 *
	 * @param array  _data							list or hashmap of graphs
	 * @param type   _data[]["name"]				string in which macros should be resolved
	 * @param array  _data[]["items"]				list of graph items
	 * @param int    _data[]["items"][n]["hostid"]	graph n-th item corresponding host ID
	 * @param string _data[]["items"][n]["host"]	graph n-th item corresponding host name
	 *
	 * @return string	inputted data with resolved source field
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<CArray> resolveGraph(IIdentityBean idBean, SQLExecutor executor, CArray<CArray> _data) {
		if (isTypeAvailable("graphFunctionalItem")) {
			String _source = getSource();

			CArray _strList = array();
			CArray _itemsList = array();

			for(CArray _graph: _data) {
				_strList.add(_graph.get(_source));
				_itemsList.add(_graph.get("items"));
			}

			CArray<String> _resolvedStrList = resolveGraphsFunctionalItemMacros(idBean, executor, _strList, _itemsList);
			
			Iterator<String> iterator = _resolvedStrList.iterator();
			String _resolvedStr = iterator.next();

			for(CArray _graph: _data) {
				_graph.put(_source, _resolvedStr);
				_resolvedStr = iterator.hasNext()? iterator.next(): null;
			}
//			unset(_graph);
		}

		return _data;
	}

	/**
	 * Resolve functional macros, like {hostname:key.function(param)}.
	 * If macro can not be resolved it is replaced with UNRESOLVED_MACRO_STRING string i.e. \"*UNKNOWN*\".
	 *
	 * Supports function \"last\", \"min\", \"max\" and \"avg\".
	 * Supports seconds as parameters, except \"last\" function.
	 * Second parameter like {hostname:key.last(0,86400) and offsets like {hostname:key.last(#1)} are not supported.
	 * Supports postfixes s,m,h,d and w for parameter.
	 *
	 * @param array  _strList				list of string in which macros should be resolved
	 * @param array  _itemsList				list of	lists of graph items
	 * @param int    _items[n][m]["hostid"]	n-th graph m-th item corresponding host Id
	 * @param string _items[n][m]["host"]	n-th graph m-th item corresponding host name
	 *
	 * @return array	list of strings with macros replaced with corresponding values
	 */
	private CArray resolveGraphsFunctionalItemMacros(IIdentityBean idBean, SQLExecutor executor, CArray<String> _strList, CArray<CArray> _itemsList) {
		// retrieve all string macros and all host-key pairs
		CArray<CArray<String>> _hostKeyPairs = array();
		CArray<CArray> _matchesList = array();
		
		Iterator<CArray> iterator = _itemsList.iterator();
		CArray<Map> _items = iterator.next();

		for(String _str: _strList) {
			// extract all macros into _matches - keys: macros, hosts, keys, functions and parameters are used
			// searches for macros, for example, \"{somehost:somekey[\"param[123]\"].min(10m)}\"
			CArray _matches = array();
			preg_match_all(
				"(?<macros>\\{"+
				"(?<hosts>("+RDA_PREG_HOST_FORMAT+"|(\\{("+PATTERN_HOST_INTERNAL+")"+PATTERN_MACRO_PARAM+"\\}))):"+
				"(?<keys>"+RDA_PREG_ITEM_KEY_FORMAT+")\\+"+
				"(?<functions>(last|max|min|avg))\\("+
				"(?<parameters>([0-9]+["+RDA_TIME_SUFFIXES+"]?)?)"+
				"\\)\\}{1})", _str, _matches, PREG_OFFSET_CAPTURE);

			if (!empty(Nest.value(_matches,"hosts").$())) {
				for(Entry<Object, CArray> entry: ((CArray<CArray>)_matches.get("hosts")).entrySet()) {
					Object i = entry.getKey();
					CArray _host = entry.getValue();
					Nest.value(_matches, "hosts", i, 0, resolveGraphPositionalMacros(idBean, asString(_host.get(0)), _items));

					if (!UNRESOLVED_MACRO_STRING.equals(_matches.getNested("hosts", i, 0))) {
						if (!isset(_hostKeyPairs.get(_matches.getNested("hosts", i, 0)))) {
							_hostKeyPairs.put(_matches.getNested("hosts", i, 0), array());
						}
						_hostKeyPairs.put(_matches.getNested("hosts", i, 0), _matches.getNested("keys", i, 0), 1);
					}
				}

				_matchesList.add( _matches );
				_items = iterator.hasNext()? iterator.next(): null;;
			}
		}

		// stop, if no macros found
		if (empty(_matchesList)) {
			return _strList;
		}

		// build item retrieval query from host-key pairs
		SqlBuilder sqlParts = new SqlBuilder();
		String _query = "SELECT h.host,i.key_,i.itemid,i.value_type,i.units,i.valuemapid"+
					" FROM items i, hosts h"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
					" AND i.tenantid=h.tenantid"+
					" AND i.hostid=h.hostid AND (";
		for(Entry<Object, CArray<String>> entry: _hostKeyPairs.entrySet()) {
			Object _host = entry.getKey();
			CArray<String> _keys = entry.getValue();
			_query += "(h.host="+sqlParts.marshalParam(_host)+" AND i.key_ IN(";
			for(Entry<Object, String> entry__keys: _keys.entrySet()) {
				Object _key = entry__keys.getKey();
				//String _val = entry__keys.getValue();
				_query += sqlParts.marshalParam(_key)+",";
			}
			_query = substr(_query, 0, -1)+")) OR ";
		}
		_query = substr(_query, 0, -4)+")";

		// get necessary items for all graph strings
		_items = DBfetchArrayAssoc(DBselect(executor, _query, sqlParts.getNamedParams()), "itemid");

		CItemGet ioptions = new CItemGet();
		ioptions.setItemIds(array_keys(_items).valuesAsLong());
		ioptions.setWebItems(true);
		ioptions.setOutput(new String[]{"itemid", "value_type", "lastvalue", "lastclock"});
		ioptions.setPreserveKeys(true);
		CArray<Map> _allowedItems = API.Item(idBean, executor).get(ioptions);

		// map item data only for allowed items
		for(Map _item: _items) {
			if (isset(_allowedItems.get(_item.get("itemid")))) {
				Nest.value(_item,"lastvalue").$(Nest.value(_allowedItems,_item.get("itemid"),"lastvalue").$());
				Nest.value(_item,"lastclock").$(Nest.value(_allowedItems,_item.get("itemid"),"lastclock").$());
				_hostKeyPairs.put(_item.get("host"), _item.get("key_"), _item);
			}
		}

		// replace macros with their corresponding values in graph strings
		Iterator<CArray> iterator_matchesList = _matchesList.iterator();
		CArray _matches = iterator_matchesList.next();

			
		for(Entry<Object, String> entry: _strList.entrySet()) {
			String _str = entry.getValue();
			// iterate array backwards!
			int i = count(Nest.value(_matches,"macros").$());

			while (!empty(i--)) {
				Object _host = _matches.getNested("hosts", i, 0);
				Object _key = _matches.getNested("keys", i, 0);
				Object _function = _matches.getNested("functions", i, 0);
				Object _parameter = _matches.getNested("parameters", i, 0);

				String _value;
				
				// host is real and item exists and has permissions
				if (!UNRESOLVED_MACRO_STRING.equals(_host)  && isArray(_hostKeyPairs.getNested(_host,_key))) {
					CArray _item = Nest.value(_hostKeyPairs, _host, _key).asCArray();

					// macro function is \"last\"
					if ("last".equals(_function)) {
						_value = (Nest.value(_item,"lastclock").asLong() > 0L)
								? formatHistoryValue(idBean, executor, Nest.value(_item,"lastvalue").asString(), _item)
								: UNRESOLVED_MACRO_STRING;
					}
					// macro function is \"max\", \"min\" or \"avg\"
					else {
						_value = getItemFunctionalValue(executor, _item, asString(_function), asString(_parameter));
					}
				}
				// there is no item with given key in given host, or there is no permissions to that item
				else {
					_value = UNRESOLVED_MACRO_STRING;
				}

				_str = substr_replace(_str, _value, Nest.value(_matches, "macros", i, 1).asInteger(), strlen(Nest.value(_matches, "macros", i, 0).asString()));
				entry.setValue(_str);
			}

			_matches = iterator_matchesList.next();
		}
//		unset(_str);

		return _strList;
	}

	/**
	 * Resolve positional macros, like {HOST.HOST2}.
	 * If macro can not be resolved it is replaced with UNRESOLVED_MACRO_STRING string i.e. \"*UNKNOWN*\"
	 * Supports HOST.HOST<1..9> macros.
	 *
	 * @param string	_str				string in which macros should be resolved
	 * @param array		_items				list of graph items
	 * @param int 		_items[n]["hostid"] graph n-th item corresponding host Id
	 * @param string	_items[n]["host"]   graph n-th item corresponding host name
	 *
	 * @return string	string with macros replaces with corresponding values
	 */
	@CodeConfirmed("blue.2.2.5")
	private String resolveGraphPositionalMacros(IIdentityBean idBean, String _str, CArray<Map> _items) {
		// extract all macros into _matches
		CArray<CArray> _matches = array();
		preg_match_all("{(("+PATTERN_HOST_INTERNAL+")("+PATTERN_MACRO_PARAM+"))\\}", _str, _matches);

		// match found groups if ever regexp should change
		Nest.value(_matches,"macroType").$(_matches.get(2));
		Nest.value(_matches,"position").$(_matches.get(3));

		// build structure of macros: _macroList["HOST.HOST"][2] = "host name";
		CArray<CArray<String>> _macroList = array();

		// _matches[3] contains positions, e.g., "",1,2,2,3,...
		for(Entry<Object, Object> entry: ((CArray<Object>)_matches.get("position")).entrySet()) {
			Object i = entry.getKey();
			Object _position = entry.getValue();
		
			// take care of macro without positional index
			int _posInItemList = ("".equals(_position)) ? 0 : asInteger(_position) - 1;

			// init array
			if (!isset(_macroList.get(_matches.getNested("macroType", i)))) {
				_macroList.put(_matches.getNested("macroType", i), array());
			}

			// skip computing for duplicate macros
			if (isset(_macroList.getNested(_matches.getNested("macroType", i), _position))) {
				continue;
			}

			// positional index larger than item count, resolve to UNKNOWN
			if (!isset(_items.get(_posInItemList))) {
				_macroList.put(_matches.getNested("macroType", i), _position, UNRESOLVED_MACRO_STRING);
				continue;
			}

			// retrieve macro replacement data
			Object o = _matches.getNested("macroType", i);
			if("HOSTNAME".equals(o) || "HOST.HOST".equals(o)) {
				_macroList.put(_matches.getNested("macroType", i), _position, _items.getNested(_posInItemList, "host"));
			}
		}

		// replace macros with values in _str
		for(Entry<Object, CArray<String>> entry: _macroList.entrySet()) {
			Object _macroType = entry.getKey();
			CArray<String> _positions = entry.getValue();
			for(Entry<Object, String> entry_positions: _positions.entrySet()) {
				Object _position = entry_positions.getKey();
				String _replacement = entry_positions.getValue();
				_str = str_replace("{"+_macroType+_position+"}", _replacement, _str);
			}
		}

		return _str;
	}

	/**
	 * Resolve item name macros to \"name_expanded\" field.
	 *
	 * @param array  _items
	 * @param string _items[n]["itemid"]
	 * @param string _items[n]["hostid"]
	 * @param string _items[n]["name"]
	 * @param string _items[n]["key_"]				item key (optional)
	 *												but is (mandatory) if macros exist and \"key_expanded\" is not present
	 * @param string _items[n]["key_expanded"]		expanded item key (optional)
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public CArray<Map> resolveItemNames(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _items) {
		// define resolving fields
		for(Map _item: _items) {
			Nest.value(_item,"name_expanded").$(Nest.value(_item,"name").$());
		}
//		unset(_item);

		CArray<Map> _macros = array();
		CArray<Map> _itemsWithReferenceMacros = array();
		CArray<Map> _itemsWithUnResolvedKeys = array();

		// reference macros - $1..$9
		for(Entry<Object, Map> entry: _items.entrySet()) {
			Object _key = entry.getKey();
			Map _item = entry.getValue();
			CArray<String> _matchedMacros = findMacros(PATTERN_ITEM_NUMBER, array(Nest.value(_item,"name_expanded").asString()));

			if (!empty(_matchedMacros)) {
				_macros.put(_key, map("macros", array()));

				for(String _macro: _matchedMacros) {
					_macros.put(_key, "macros", _macro, null);
				}

				_itemsWithReferenceMacros.put(_key, _item);
			}
		}

		if (!empty(_itemsWithReferenceMacros)) {
			// resolve macros in item key
			for(Entry<Object, Map> entry: _itemsWithReferenceMacros.entrySet()) {
				Object _key = entry.getKey();
				Map _item = entry.getValue();
				if (!isset(Nest.value(_item,"key_expanded").$())) {
					_itemsWithUnResolvedKeys.put(_key, _item);
				}
			}

			if (!empty(_itemsWithUnResolvedKeys)) {
				_itemsWithUnResolvedKeys = resolveItemKeys(idBean, executor, _itemsWithUnResolvedKeys);

				for(Entry<Object, Map> entry: _itemsWithUnResolvedKeys.entrySet()) {
					Object _key = entry.getKey();
					Map _item = entry.getValue();
					_itemsWithReferenceMacros.put(_key, _item);
				}
			}

			// reference macros - $1..$9
			for(Entry<Object, Map> entry: _itemsWithReferenceMacros.entrySet()) {
				Object _key = entry.getKey();
				Map _item = entry.getValue();
				CItemKey _itemKey = new CItemKey(Nest.value(_item,"key_expanded").asString());

				if (_itemKey.isValid()) {
					for(Entry<Object, String> entry_itemKeyParams: _itemKey.getParameters().entrySet()) {
						Integer n = asInteger( entry_itemKeyParams.getKey() );
						String _keyParameter = entry_itemKeyParams.getValue();
						String _paramNum = "$"+(++n);

						if (array_key_exists(_paramNum, Nest.value(_macros, _key, "macros").asCArray())) {
							_macros.put(_key, "macros", _paramNum, _keyParameter);
						}
					}
				}
			}
		}

		// user macros
		CArray _userMacros = array();

		for(Map _item: _items) {
			CArray<String> _matchedMacros = findMacros(RDA_PREG_EXPRESSION_USER_MACROS, array(Nest.value(_item,"name_expanded").asString()));

			if (!empty(_matchedMacros)) {
				for(String _macro: _matchedMacros) {
					Object _hostid = _item.get("hostid");
					if (!isset(_userMacros.get(_hostid))) {
						_userMacros.put(_hostid, map(
							"hostids", array(_hostid),
							"macros", array()
						));
					}

					_userMacros.put(_hostid, "macros", _macro, null);
				}
			}
		}

		if (!empty(_userMacros)) {
			_userMacros = getUserMacros(idBean, executor, _userMacros);

			for(Entry<Object, Map> entry: _items.entrySet()) {
				Object _key = entry.getKey();
				Map _item = entry.getValue();
			
				Object _hostid = _item.get("hostid");
				if (isset(_userMacros.get(_hostid))) {
					_macros.put(_key, "macros", 
							isset(_macros.get(_key))
							? rda_array_merge(Nest.value(_macros, _key,"macros").asCArray(), Nest.value(_userMacros, _hostid,"macros").asCArray())
							: _userMacros.getNested(_hostid, "macros")
						);
				}
			}
		}

		// replace macros to value
		if (!empty(_macros)) {
			for(Entry<Object, Map> entry: _macros.entrySet()) {
				Object _key = entry.getKey();
				Map _macroData = entry.getValue();
				_items.put(_key, "name_expanded", str_replace(
					array_keys(Nest.value(_macroData,"macros").asCArray()),
					array_values(Nest.value(_macroData,"macros").asCArray()),
					Nest.value(_items, _key, "name_expanded").asString()
				));
			}
		}

		return _items;
	}

	/**
	 * Resolve item key macros to \"key_expanded\" field.
	 *
	 * @param array  _items
	 * @param string _items[n]["itemid"]
	 * @param string _items[n]["hostid"]
	 * @param string _items[n]["key_"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public CArray resolveItemKeys(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _items) {
		// define resolving field
		for(Map _item: _items) {
			Nest.value(_item,"key_expanded").$(Nest.value(_item,"key_").$());
		}
//		unset(_item);

		CArray<CArray> _macros = array();
		CArray _itemIds = array();

		// host, ip macros
		for(Entry<Object, Map> entry: _items.entrySet()) {
			Object _key = entry.getKey();
			Map _item = entry.getValue();
			CArray<String> _matchedMacros = findMacros(PATTERN_ITEM_MACROS, array(Nest.value(_item,"key_expanded").asString()));

			if (!empty(_matchedMacros)) {
				Object _itemid = _item.get("itemid");
				_itemIds.put(_itemid, _itemid);

				_macros.put(_key, map(
					"itemid", _itemid,
					"macros", array()
				));

				for(String _macro: _matchedMacros) {
					_macros.put(_key, "macros", _macro, null);
				}
			}
		}

		if (!empty(_macros)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(_itemIds.valuesAsLong());
			ioptions.setSelectInterfaces(new String[]{"ip", "dns", "useip"});
			ioptions.setSelectHosts(new String[]{"host", "name"});
			ioptions.setWebItems(true);
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setFilter("flags");
			ioptions.setPreserveKeys(true);
			CArray<Map> _dbItems = API.Item(idBean, executor).get(ioptions);

			for(Entry<Object, CArray> entry: _macros.entrySet()) {
				Object _key = entry.getKey();
				CArray _macroData = entry.getValue();
				
				Object _itemid = _macroData.get("itemid");
				if (isset(_dbItems.get(_itemid))) {
					Map _host = reset(Nest.value(_dbItems, _itemid, "hosts").asCArray());
					Map iface = reset(Nest.value(_dbItems, _itemid, "interfaces").asCArray());

					// if item without interface or template item, resolve interface related macros to *UNKNOWN*
					if (empty(iface)) {
						iface = map(
							"ip", UNRESOLVED_MACRO_STRING,
							"dns", UNRESOLVED_MACRO_STRING,
							"useip", false
						);
					}

					
					
					for(String _macro: Nest.value(_macroData,"macros").asCArray().keys()) {
						if("{HOST.NAME}".equals(_macro)) {
							_macros.put(_key, "macros", _macro, Nest.value(_host,"name").$());
						}else if("{HOST.HOST}".equals(_macro) 
								|| "{HOSTNAME}".equals(_macro)) {// deprecated
							_macros.put(_key, "macros", _macro, Nest.value(_host,"host").$());
						}else if("{HOST.IP}".equals(_macro) 
								|| "{IPADDRESS}".equals(_macro)) {// deprecated
							_macros.put(_key, "macros", _macro, Nest.value(_host,"ip").$());
						}else if("{HOST.DNS}".equals(_macro)) {
							_macros.put(_key, "macros", _macro, Nest.value(_host,"dns").$());
						}else if("{HOST.CONN}".equals(_macro)) {
							_macros.put(_key, "macros", _macro, 
									Nest.value(iface,"useip").asBoolean() 
									? Nest.value(iface,"ip").$() 
									: Nest.value(iface,"dns").$()
								);
						}
					}
				}

				unset(_macros, _key, "itemid");
			}
		}

		// user macros
		CArray _userMacros = array();

		for(Map _item: _items) {
			CArray<String> _matchedMacros = findMacros(RDA_PREG_EXPRESSION_USER_MACROS, array(Nest.value(_item,"key_expanded").asString()));

			if (!empty(_matchedMacros)) {
				for(String _macro: _matchedMacros) {
					Object _hostid = _item.get("hostid");
					if (!isset(_userMacros.get(_hostid))) {
						_userMacros.put(_hostid, map(
							"hostids", array(_hostid),
							"macros", array()
						));
					}

					_userMacros.put(_hostid, "macros", _macro, null);
				}
			}
		}

		if (!empty(_userMacros)) {
			_userMacros = getUserMacros(idBean, executor, _userMacros);

			for(Entry<Object, Map> entry: _items.entrySet()) {
				Object _key = entry.getKey();
				Map _item = entry.getValue();
				
				Object _hostid = _item.get("hostid");
				if (isset(_userMacros.get(_hostid))) {
					_macros.put(_key, "macros", 
							isset(_macros.get(_key))
							? rda_array_merge(Nest.value(_macros, _key, "macros").asCArray(), Nest.value(_userMacros, _hostid, "macros").asCArray())
							: _userMacros.getNested(_hostid, "macros")
						);
				}
			}
		}

		// replace macros to value
		if (!empty(_macros)) {
			for(Entry<Object, CArray> entry: _macros.entrySet()) {
				Object _key = entry.getKey();
				CArray _macroData = entry.getValue();
			
				_items.put(_key, "key_expanded", str_replace(
					array_keys(Nest.value(_macroData,"macros").asCArray()),
					array_values(Nest.value(_macroData,"macros").asCArray()),
					Nest.value(_items, _key, "key_expanded").asString()
				));
			}
		}

		return _items;
	}

	/**
	 * Resolve function parameter macros to \"parameter_expanded\" field.
	 *
	 * @param array  _data
	 * @param string _data[n]["hostid"]
	 * @param string _data[n]["parameter"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public CArray resolveFunctionParameters(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _data) {
		// define resolving field
		for(Map _function: _data) {
			Nest.value(_function,"parameter_expanded").$(Nest.value(_function,"parameter").$());
		}
//		unset(_function);

		CArray<CArray> _macros = array();

		// user macros
		CArray<Map> _userMacros = array();

		for(Map _function: _data) {
			CArray<String> _matchedMacros = findMacros(RDA_PREG_EXPRESSION_USER_MACROS, array(Nest.value(_function,"parameter_expanded").asString()));

			if (!empty(_matchedMacros)) {
				for(String _macro: _matchedMacros) {
					Object _hostid = _function.get("hostid");
					if (!isset(_userMacros.get(_hostid))) {
						_userMacros.put(_hostid, map(
							"hostids", array(_hostid),
							"macros", array()
						));
					}

					_userMacros.put(_hostid, "macros", _macro, null);
				}
			}
		}

		if (!empty(_userMacros)) {
			_userMacros = getUserMacros(idBean, executor, _userMacros);

			for(Entry<Object, Map> entry: _data.entrySet()) {
				Object _key = entry.getKey();
				Map _function = entry.getValue();
				Object _hostid = _function.get("hostid");
				if (isset(_userMacros.get(_hostid))) {
					_macros.put(_key, "macros", 
							isset(_macros.get(_key))
							? rda_array_merge(Nest.value(_macros, _key, "macros").asCArray(), Nest.value(_userMacros, _hostid, "macros").asCArray())
							: _userMacros.getNested(_hostid, "macros")
						);
				}
			}
		}

		// replace macros to value
		if (!empty(_macros)) {
			for(Entry<Object, CArray> entry: _macros.entrySet()) {
				Object _key = entry.getKey();
				CArray _macroData = entry.getValue();
				
				_data.put(_key, "parameter_expanded", str_replace(
					array_keys(Nest.value(_macroData,"macros").asCArray()),
					array_values(Nest.value(_macroData,"macros").asCArray()),
					Nest.value(_data, _key, "parameter_expanded").asString()
				));
			}
		}

		return _data;
	}
}
