package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.substr_replace;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.replace_template_dependencies;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class CTriggerGeneralDAO<P extends CParamGet> extends CCoreLongKeyDAO<P> {

	public CTriggerGeneralDAO(IIdentityBean idBean, SQLExecutor executor, String tableName,
			String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}
	
	@Override
	public abstract <T> T get(P params);

	abstract protected void createReal(CArray<Map> array);
	
	abstract protected void updateReal(CArray<Map> array);

	/**
	 * Updates the children of the trigger on the given hosts and propagates the inheritance to all child hosts.
	 * If the given trigger was assigned to a different template or a host, all of the child triggers, that became
	 * obsolete will be deleted.
	 *
	 * @param array _trigger    the trigger with an exploded expression
	 * @param array _hostids
	 *
	 * @return bool
	 */
	protected boolean inherit(Map trigger) {
		return inherit(trigger, null);
	}
	protected boolean inherit(Map trigger, Long[] hostIds) {
		CTemplateGet options = new CTemplateGet();
		options.setTriggerIds(Nest.value(trigger,"triggerid").asLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setNopermissions(true);
		CArray<Map> triggerTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(options);

		if (empty(triggerTemplates)) {
			// nothing to inherit, just exit
			return true;
		}

		if (!isset(trigger,"expression") || !isset(trigger,"description")) {
			P params = getParamInstance();
			params.put("triggerids", new String[]{Nest.value(trigger,"triggerid").asString()});
			params.setOutput(new String[]{"expression", "description"});
			params.setNopermissions(true);
			CArray<Map> dbTriggers = get(params);
			Map dbTrigger = reset(dbTriggers);

			if (!isset(trigger,"description")) {
				Nest.value(trigger,"description").$(Nest.value(dbTrigger,"description").$());
			}
			if (!isset(trigger,"expression")) {
				Nest.value(trigger,"expression").$(explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString()));
			}
		}

		// fetch all of the child hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setTemplateIds(rda_objectValues(triggerTemplates, "templateid").valuesAsLong());
		hoptions.setOutput(new String[]{"hostid", "host"});
		hoptions.setPreserveKeys(true);
		hoptions.setHostIds(hostIds);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> childHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

		for(Map childHost : childHosts) {
			// update the child trigger on the child host
			Map newTrigger = inheritOnHost(trigger, childHost, triggerTemplates);
			// propagate the trigger inheritance to all child hosts
			inherit(newTrigger);
		}

		return true;
	}
	
	/**
	 * Updates the child of the templated trigger on the given host. Trigger inheritance will not propagate to
	 * child hosts.
	 *
	 * @param array _trigger            a templated trigger
	 * @param array _chdHost            the target host
	 * @param array _triggerTemplates   the templates, to which the templated trigger belongs
	 *
	 * @return array|mixed  the updated child trigger
	 */
	protected Map inheritOnHost(Map trigger, Map chdHost, CArray<Map> triggerTemplates) {
		Map newTrigger = Clone.deepcopy(trigger);

		Nest.value(newTrigger,"templateid").$(Nest.value(trigger,"triggerid").$());
		unset(newTrigger,"triggerid");

		if (isset(trigger,"dependencies")) {
			CArray deps = rda_objectValues(Nest.value(trigger,"dependencies").asCArray(), "triggerid");
			Nest.value(newTrigger,"dependencies").$(replace_template_dependencies(this.idBean, getSqlExecutor(), deps, Nest.value(chdHost,"hostid").asString()));
		}
		CTriggerExpression expressionData = new CTriggerExpression();
		expressionData.parse(Nest.value(trigger,"expression").asString());

		Nest.value(newTrigger,"expression").$(Nest.value(trigger,"expression").$());
		// replace template separately in each expression, only in beginning (host part)
		int size = expressionData.expressions.size();
		do {
			Map<String, Object> exprPart = expressionData.expressions.get(--size);
			for(Map triggerTemplate : triggerTemplates) {
				if (Nest.value(triggerTemplate,"host").asString().equals(Nest.value(exprPart,"host").asString())) {
					Nest.value(exprPart,"host").$(Nest.value(chdHost,"host").$());
					break;
				}
			}

			Nest.value(newTrigger,"expression").$(substr_replace(Nest.value(newTrigger,"expression").asString(),
					"{"+Nest.value(exprPart,"host").asString()+":"+Nest.value(exprPart,"item").asString()+"."+Nest.value(exprPart,"function").asString()+"}",
					Nest.value(exprPart,"pos").asInteger(), strlen(Nest.value(exprPart,"expression").asString())
			));
		} while (size>0);

		// check if a child trigger already exists on the host
		P params = getParamInstance();
		params.setFilter("templateid", Nest.value(newTrigger,"templateid").asString());
		params.setOutput(new String[]{"triggerid"});
		params.put("hostids", new String[]{Nest.value(chdHost,"hostid").asString()});
		CArray<Map> childTriggers = get(params);

		// yes we have a child trigger, just update it
		Map childTrigger = null;
		if (!empty(childTrigger = reset(childTriggers))) {
			Nest.value(newTrigger,"triggerid").$(Nest.value(childTrigger,"triggerid").$());
		} else {// no child trigger found
			// look for a trigger with the same description and expression
			params = getParamInstance();
			params.setFilter("description", Nest.value(newTrigger,"description").asString());
			params.setFilter("flags");
			params.setOutput(new String[]{"triggerid", "expression"});
			params.setNopermissions(true);
			params.put("hostids", new String[]{Nest.value(chdHost,"hostid").asString()});
			childTriggers = get(params);

			for(Map schildTrigger : childTriggers) {
				Object tmpExp = explode_exp(idBean, getSqlExecutor(),Nest.value(schildTrigger,"expression").asString());
				if (strcmp(Nest.as(tmpExp).asString(), Nest.value(newTrigger,"expression").asString()) == 0) {
					// we have a trigger with the same description and expression as the parent
					// convert it to a template trigger
					Nest.value(newTrigger,"triggerid").$(Nest.value(schildTrigger,"triggerid").$());
					break;
				}
			}
		}

		checkIfExistsOnHost(newTrigger, Nest.value(chdHost,"hostid").asLong());

		if (isset(newTrigger,"triggerid")) {
			updateReal(array(newTrigger));
		} else {
			params = getParamInstance();
			params.put("triggerids", new String[]{Nest.value(trigger,"triggerid").asString()});
			params.setOutput(API_OUTPUT_EXTEND);
			params.setPreserveKeys(true);
			
			CArray<Map> oldTriggers = get(params);
			Map<Object,Object> oldTrigger = reset(oldTriggers);
			unset(oldTrigger,"triggerid");
			for (Entry<Object,Object> e : oldTrigger.entrySet()) {
                Object key = e.getKey();
				if (!isset(newTrigger,key)) {
					Nest.value(newTrigger, key).$(Nest.value(oldTrigger, key).$());
				}
			}
			createReal(array(newTrigger));
		}

		return newTrigger;
	}
	
	/**
	 * Checks that no trigger with the same description and expression as _trigger exist on the given host.
	 * Assumes the given trigger is valid.
	 *
	 * @throws APIException if at least one trigger exists
	 *
	 * @param array _trigger a trigger with an exploded expression
	 * @param null  _hostid
	 *
	 * @return void
	 */
	protected void checkIfExistsOnHost(Map trigger) {
		checkIfExistsOnHost(trigger, null);
	}
	
	protected void checkIfExistsOnHost(Map trigger, Long hostId) {
		// skip the check if the description and expression haven't been changed
		if (!isset(trigger,"description") && !isset(trigger,"expression")) {
			return;
		}

		// make sure we have all the required data
		if (!isset(trigger,"description") || !isset(trigger,"expression")) {
			boolean explodeExpression = !isset(trigger,"expression");
			trigger = extendObject(tableName(), trigger, new String[]{"description", "expression"});

			if (explodeExpression) {
				Nest.value(trigger,"expression").$(explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString()));
			}
		}

		Map<String, Object[]> filter = new HashMap();
		filter.put("description", new Object[]{Nest.value(trigger,"description").$()});

		if (!empty(hostId)) {
			filter.put("hostid", new Object[]{hostId});
		} else {
			CTriggerExpression expressionData = new CTriggerExpression(Nest.value(trigger,"expression").asCArray());
			expressionData.parse(Nest.value(trigger,"expression").asString());
			CArray<String> expressionHosts = expressionData.getHosts();
			filter.put("host", new String[]{reset(expressionHosts)});
		}

		P params = getParamInstance();
		params.setFilter(filter);
		params.setOutput(new String[]{"expression", "triggerid"});
		params.setNopermissions(true);
		CArray<Map> triggers = get(params);

		for(Map dbTrigger : triggers) {
			Object tmpExp = explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString());

			// check if the expressions are also equal and that this is a different trigger
			boolean differentTrigger = (!isset(trigger,"triggerid") || !idcmp(Nest.value(trigger,"triggerid").$(), Nest.value(dbTrigger,"triggerid").$()));

			if (strcmp(Nest.as(tmpExp).asString(), Nest.value(trigger,"expression").asString()) == 0 && differentTrigger) {
				CHostGet options = new CHostGet();
				options.setOutput(new String[]{"name"});
				options.setTemplatedHosts(true);
				options.setNopermissions(true);
				options.setLimit(1);
				if (isset(filter,"host")) {
					options.setFilter("host", Nest.array(filter,"host").asString());
				} else {
					options.setHostIds(hostId);
				}
				CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
				Map host = reset(hosts);

				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Trigger \"%1$s\" already exists on \"%2$s\".", Nest.value(trigger,"description").asString(), Nest.value(host,"name").asString()));
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(P params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] triggerids = result.keysAsLong();
		// adding groups
		if (!is_null(params.get("selectGroups")) && !API_OUTPUT_COUNT.equals(params.get("selectGroups"))) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("f.triggerid");
			sqlParts.select.put("hg.groupid");
			sqlParts.from.put("functions f");
			sqlParts.from.put("items i");
			sqlParts.from.put("hosts_groups hg");
			applyQueryTenantOptions("functions", "f", params, sqlParts);
			sqlParts.where.dbConditionInt("f.triggerid", triggerids);
			sqlParts.where.put("f.tenantid=i.tenantid");
			sqlParts.where.put("f.itemid=i.itemid");
			sqlParts.where.put("i.tenantid=hg.tenantid");
			sqlParts.where.put("i.hostid=hg.hostid");
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = new CRelationMap();
			for(Map relation:datas){
				relationMap.addRelation(relation.get("triggerid"), relation.get("groupid"));
			}
			
			CHostGroupGet tparams = new CHostGroupGet();
			tparams.setOutput(params.get("selectGroups"));
			tparams.setGroupIds(relationMap.getRelatedLongIds());
			tparams.setPreserveKeys(true);
			
			datas = API.HostGroup(this.idBean, getSqlExecutor()).get(tparams);
			relationMap.mapMany(result, datas, "groups", null);
		}
		
		// adding hosts
		if (!is_null(params.get("selectHosts")) && !API_OUTPUT_COUNT.equals(params.get("selectHosts"))) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("f.triggerid");
			sqlParts.select.put("i.hostid");
			sqlParts.from.put("functions f");
			sqlParts.from.put("items i");
			applyQueryTenantOptions("functions", "f", params, sqlParts);
			sqlParts.where.dbConditionInt("f.triggerid", triggerids);
			sqlParts.where.put("f.tenantid=i.tenantid");
			sqlParts.where.put("f.itemid=i.itemid");
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = new CRelationMap();
			for(Map relation:datas){
				relationMap.addRelation(relation.get("triggerid"), relation.get("hostid"));
			}
			
			CHostGet tparams = new CHostGet();
			tparams.setOutput(params.get("selectHosts"));
			tparams.setHostIds(relationMap.getRelatedLongIds());
			tparams.setTemplatedHosts(true);
			tparams.setPreserveKeys(true);
			tparams.setNopermissions(true);
			tparams.setSortfield("host");
			
			datas = API.Host(this.idBean, getSqlExecutor()).get(tparams);
			relationMap.mapMany(result, datas, "hosts", params.getLimitSelects());
		}
		
		// adding functions
		if (!is_null(params.get("selectFunctions")) && !API_OUTPUT_COUNT.equals(params.get("selectFunctions"))) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("functions", new String[]{"triggerid", "functionid"}, params.get("selectFunctions")));
			Map<String, Object[]> filter = new HashMap();
			filter.put("triggerid", triggerids);
			options.setFilter(filter);
			options.setPreserveKeys(true);
			CArray<Map> functions = select("functions", options );
			CRelationMap relationMap = createRelationMap(functions, "triggerid", "functionid");

			unsetExtraFields(functions, new String[]{"triggerid", "functionid"}, params.get("selectFunctions"));
			relationMap.mapMany(result, functions, "functions");
		}
	}

}
