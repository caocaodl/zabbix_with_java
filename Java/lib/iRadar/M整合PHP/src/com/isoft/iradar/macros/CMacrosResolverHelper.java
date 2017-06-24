package com.isoft.iradar.macros;

import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.INTERFACE_PRIMARY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("blue.2.2.5")
public class CMacrosResolverHelper {
	protected final static Logger LOG = LoggerFactory.getLogger(CMacrosResolverHelper.class);
	
	/**
	 * @var CMacrosResolver
	 */
	@CodeConfirmed("blue.2.2.5")
	private static CMacrosResolver macrosResolver = new CMacrosResolver();
	
	/**
	 * Resolve macros.
	 *
	 * @static
	 *
	 * @param array _options
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolve(IIdentityBean idBean, SQLExecutor executor, CArray _options) {
		init();
		return macrosResolver.resolve(idBean, executor, _options);
	}
	
	/**
	 * Resolve macros in http test name.
	 *
	 * @static
	 *
	 * @param int    _hostId
	 * @param string _name
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveHttpTestName(IIdentityBean idBean, SQLExecutor executor, int _hostId, String _name) {
		init();

		CArray _macros = macrosResolver.resolve(idBean, executor, map(
			"config", "httpTestName",
			"data", map(_hostId, array(_name))
		));

		return Nest.value(_macros, _hostId, 0).asString();
	}
	
	/**
	 * Resolve macros in host interfaces.
	 *
	 * @static
	 *
	 * @param array  ifaces
	 * @param string ifaces[n]["hostid"]
	 * @param string ifaces[n]["type"]
	 * @param string ifaces[n]["main"]
	 * @param string ifaces[n]["ip"]
	 * @param string ifaces[n]["dns"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveHostInterfaces(IIdentityBean idBean, SQLExecutor executor, CArray<CArray> ifaces) {
		init();

		// agent primary ip and dns
		CArray<CArray> data = array();
		for(CArray iface: ifaces) {
			if (Nest.value(iface,"type").asInteger() == INTERFACE_TYPE_AGENT && Nest.value(iface,"main").asInteger() == INTERFACE_PRIMARY) {
				Object hostid = iface.get("hostid");
				if(!isset(data,hostid)){
					Nest.value(data, hostid).$(new CArray());
				}
				data.get(hostid).add(Nest.value(iface,"ip").$() );
				data.get(hostid).add(Nest.value(iface,"dns").$() );
			}
		}

		CArray<Map> resolvedData = macrosResolver.resolve(idBean, executor, map(
			"config", "hostInterfaceIpDnsAgentPrimary",
			"data", data
		));

		for(Entry<Object, Map> entry: resolvedData.entrySet()) {
			Object hostId = entry.getKey();
			Map texts = entry.getValue();
			
			int n = 0;

			for(CArray iface: ifaces) {
				if (Nest.value(iface,"type").asInteger() == INTERFACE_TYPE_AGENT && Nest.value(iface,"main").asInteger() == INTERFACE_PRIMARY
						&& hostId.equals(Nest.value(iface,"hostid").$())) {
					Nest.value(iface,"ip").$(texts.get(n));
					n++;
					Nest.value(iface,"dns").$(texts.get(n));
					n++;
				}
			}
//			unset(iface);
		}

		// others ip and dns
		data = array();
		for(CArray iface: ifaces) {
			if (!(Nest.value(iface,"type").asInteger() == INTERFACE_TYPE_AGENT && Nest.value(iface,"main").asInteger() == INTERFACE_PRIMARY)) {
				Object hostid = iface.get("hostid");
				if(!isset(data,hostid)){
					Nest.value(data, hostid).$(new CArray());
				}
				data.get(hostid).add( Nest.value(iface,"ip").$() );
				data.get(hostid).add( Nest.value(iface,"dns").$() );
			}
		}

		resolvedData = macrosResolver.resolve(idBean, executor, map(
			"config", "hostInterfaceIpDns",
			"data", data
		));

		for(Entry<Object, Map> entry: resolvedData.entrySet()) {
			Object hostId = entry.getKey();
			Map texts = entry.getValue();
			
			int n = 0;

			for(CArray iface: ifaces) {
				if (!(Nest.value(iface,"type").asInteger() == INTERFACE_TYPE_AGENT && Nest.value(iface,"main").asInteger() == INTERFACE_PRIMARY)
						&& hostId.equals(Nest.value(iface,"hostid").$())) {
					Nest.value(iface,"ip").$(texts.get(n));
					n++;
					Nest.value(iface,"dns").$(texts.get(n));
					n++;
				}
			}
//			unset(iface);
		}

		// port
		data = array();
		for(CArray iface: ifaces) {
			Object hostid = iface.get("hostid");
			if(!isset(data,hostid)){
				Nest.value(data, hostid).$(new CArray());
			}
			data.get(hostid).add( Nest.value(iface,"port").$() );
		}

		resolvedData = macrosResolver.resolve(idBean, executor, map(
			"config", "hostInterfacePort",
			"data", data
		));

		
		for(Entry<Object, Map> entry: resolvedData.entrySet()) {
			Object hostId = entry.getKey();
			Map texts = entry.getValue();
			int n = 0;

			for(CArray iface: ifaces) {
				if (Cphp.equals(Nest.value(iface,"hostid").$(), hostId)) {
					Nest.value(iface,"port").$(texts.get(n));
					n++;
				}
			}
//			unset(iface);
		}

		return ifaces;
	}
	
	/**
	 * Resolve macros in trigger name.
	 *
	 * @static
	 *
	 * @param array _trigger
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveTriggerName(IIdentityBean idBean, SQLExecutor executor, Map _trigger) {
		CArray<Map> _macros = resolveTriggerNames(idBean, executor, array(_trigger));
		Map _macro = reset(_macros);

		return Nest.value(_macro,"description").asString();
	}
	
	
	/**
	 * Resolve macros in trigger names.
	 *
	 * @static
	 *
	 * @param array _triggers
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveTriggerNames(IIdentityBean idBean, SQLExecutor executor, CArray _triggers) {
		init();

		return macrosResolver.resolve(idBean, executor, map(
			"config", "triggerName",
			"data", rda_toHash(_triggers, "triggerid")
		));
	}

	/**
	 * Resolve macros in trigger description.
	 *
	 * @static
	 *
	 * @param array _trigger
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveTriggerDescription(IIdentityBean idBean, SQLExecutor executor, CArray _trigger) {
		CArray _macros = resolveTriggerDescriptions(idBean, executor, array(_trigger));
		_macros = reset(_macros);

		return Nest.value(_macros,"comments").asString();
	}

	/**
	 * Resolve macros in trigger descriptions.
	 *
	 * @static
	 *
	 * @param array _triggers
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveTriggerDescriptions(IIdentityBean idBean, SQLExecutor executor, CArray _triggers) {
		init();
		return macrosResolver.resolve(idBean, executor, map(
			"config", "triggerDescription",
			"data", rda_toHash(_triggers, "triggerid")
		));
	}

	/**
	 * Get trigger by id and resolve macros in trigger name.
	 *
	 * @static
	 *
	 * @param int triggerId
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveTriggerNameById(IIdentityBean idBean, SQLExecutor executor, long triggerId) {
		CArray<Map> macros = resolveTriggerNameByIds(idBean, executor, array(triggerId));
		Map macro = reset(macros);
		return Nest.value(macro,"description").asString();
	}

	/**
	 * Get triggers by ids and resolve macros in trigger names.
	 *
	 * @static
	 *
	 * @param array triggerIds
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray<Map> resolveTriggerNameByIds(IIdentityBean idBean, SQLExecutor executor, CArray triggerIds) {
		init();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> triggers = DBselect(executor,
			"SELECT DISTINCT t.description,t.expression,t.triggerid"+
			" FROM triggers t"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
			" AND "+sqlParts.dual.dbConditionLong("t.triggerid", triggerIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);

		return macrosResolver.resolve(idBean, executor, map(
			"config", "triggerName",
			"data", rda_toHash(triggers, "triggerid")
		));
	}

	/**
	 * Resolve macros in trigger reference.
	 *
	 * @static
	 *
	 * @param string _expression
	 * @param string _text
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveTriggerReference(IIdentityBean idBean, String _expression, String _text) {
		init();

		return macrosResolver.resolveTriggerReference(_expression, _text);
	}

	/**
	 * Resolve user macros in trigger expression.
	 *
	 * @static
	 *
	 * @param array _trigger
	 * @param array _trigger["triggerid"]
	 * @param array _trigger["expression"]
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveTriggerExpressionUserMacro(IIdentityBean idBean, SQLExecutor executor, CArray _trigger) {
		if (rda_empty(Nest.value(_trigger,"expression").$())) {
			return Nest.value(_trigger,"expression").asString();
		}

		init();

		CArray _triggers = macrosResolver.resolve(idBean, executor, map(
			"config", "triggerExpressionUser",
			"data", rda_toHash(array(_trigger), "triggerid")
		));
		_trigger = reset(_triggers);

		return Nest.value(_trigger,"expression").asString();
	}

	/**
	 * Resolve macros in event description.
	 *
	 * @static
	 *
	 * @param array _event
	 *
	 * @return string
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveEventDescription(IIdentityBean idBean, SQLExecutor executor, Map _event) {
		init();
		CArray _macros = macrosResolver.resolve(idBean, executor, map(
			"config", "eventDescription",
			"data", map(Nest.value(_event,"triggerid").$(), _event)
		));
		_macros = reset(_macros);
		return Nest.value(_macros,"description").asString();
	}
	
	/**
	 * Resolve positional macros and functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 *
	 * @static
	 *
	 * @param type   _name					string in which macros should be resolved
	 * @param array  _items					list of graph items
	 * @param int    _items[n]['hostid']	graph n-th item corresponding host Id
	 * @param string _items[n]['host']		graph n-th item corresponding host name
	 *
	 * @return string	string with macros replaced with corresponding values
	 */
	@CodeConfirmed("blue.2.2.5")
	public static String resolveGraphName(IIdentityBean idBean, SQLExecutor executor, String _name, CArray _items) {
		CArray<Map> _graph = (CArray<Map>)macrosResolver.resolve(idBean, executor, map(
				"config", "graphName",
				"data", array(map("name", _name, "items", _items))
			)); 
		
		Map graph = reset(_graph);
		return asString(graph.get("name"));
	}

	/**
	 * Resolve positional macros and functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 * ! if same graph will be passed more than once only name for first entry will be resolved.
	 *
	 * @static
	 *
	 * @param array  _data					list or hashmap of graphs
	 * @param int    _data[n]["graphid"]	id of graph
	 * @param string _data[n]["name"]		name of graph
	 *
	 * @return array	inputted data with resolved names
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveGraphNameByIds(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _data) {
		init();

		CArray _graphIds = array();
		CArray<Map> _graphMap = array();
		for(Map _graph: _data) {
			// skip graphs without macros
			if (strpos(Nest.value(_graph,"name").asString(), "{") > -1) {
				Object _graphid = _graph.get("graphid");
				_graphMap.put(_graphid, map(
					"graphid", _graphid,
					"name", Nest.value(_graph,"name").$(),
					"items", array()
				));
				_graphIds.put(_graphid, _graphid);
			}
		}
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("i.hostid,gi.graphid,h.host");
		sqlParts.from.put("graphs_items gi,items i,hosts h");
		sqlParts.where.dbConditionTenants(idBean, "hosts", "h");
		sqlParts.where.put("gi.tenantid=i.tenantid");
		sqlParts.where.put("gi.itemid=i.itemid");
		sqlParts.where.put("i.tenantid=h.tenantid");
		sqlParts.where.put("i.hostid=h.hostid");
		sqlParts.where.dbConditionInt("gi.graphid", _graphIds.valuesAsLong());
		sqlParts.order.put("gi.sortorder");

		CArray<Map> _items = DBfetchArray(DBselect(executor, sqlParts));

		for(Map _item: _items) {
			((CArray)_graphMap.getNested(_item.get("graphid"), "items")).add( map("hostid", Nest.value(_item,"hostid").$(), "host", Nest.value(_item,"host").$()) );
		}

		_graphMap = macrosResolver.resolve(idBean, executor, map(
			"config", "graphName",
			"data", _graphMap
		));

		Iterator<Map> iterator = _graphMap.iterator();
		Map _resolvedGraph = iterator.hasNext()?iterator.next():null;
		for(Map _graph: _data) {
			if (Cphp.equals(Nest.value(_graph,"graphid").$(), Nest.value(_resolvedGraph,"graphid").$())) {
				Nest.value(_graph,"name").$(Nest.value(_resolvedGraph,"name").$());
				_resolvedGraph = iterator.hasNext()?iterator.next():null;
			}
		}
		return _data;
	}

	/**
	 * Resolve item name macros to \"name_expanded\" field.
	 *
	 * @static
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
	public static CArray resolveItemNames(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _items) {
		init();
		return macrosResolver.resolveItemNames(idBean, executor, _items);
	}

	/**
	 * Resolve item key macros to \"key_expanded\" field.
	 *
	 * @static
	 *
	 * @param array  _items
	 * @param string _items[n]["itemid"]
	 * @param string _items[n]["hostid"]
	 * @param string _items[n]["key_"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveItemKeys(IIdentityBean idBean, SQLExecutor executor, CArray _items) {
		init();
		return macrosResolver.resolveItemKeys(idBean, executor, _items);
	}

	/**
	 * Resolve function parameter macros to \"parameter_expanded\" field.
	 *
	 * @static
	 *
	 * @param array  _data
	 * @param string _data[n]["hostid"]
	 * @param string _data[n]["parameter"]
	 *
	 * @return array
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CArray resolveFunctionParameters(IIdentityBean idBean, SQLExecutor executor, CArray _data) {
		init();

		return macrosResolver.resolveFunctionParameters(idBean, executor, _data);
	}

	/**
	 * Create CMacrosResolver object and store in static variable.
	 *
	 * @static
	 */
	@CodeConfirmed("blue.2.2.5")
	private static void init() {
		if (macrosResolver == null) {
			macrosResolver = new CMacrosResolver();
		}
	}
	
}
