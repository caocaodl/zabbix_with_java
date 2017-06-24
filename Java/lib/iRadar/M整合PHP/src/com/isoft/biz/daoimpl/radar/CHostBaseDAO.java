package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_arrayFindDuplicates;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.TriggersUtil.get_triggers_by_hostid;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

@CodeConfirmed("benne.2.2.6")
public abstract class CHostBaseDAO<P extends CParamGet> extends CCoreLongKeyDAO<P> {

	public CHostBaseDAO(IIdentityBean idBean, SQLExecutor executor, String[] sortColumns) {
		super(idBean, executor, "hosts", "h", sortColumns);
	}

	public CHostBaseDAO(IIdentityBean idBean, SQLExecutor executor, String tableName, String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}
	
	/**
	 * Links the templates to the given hosts.
	 *
	 * @param array templateIds
	 * @param array targetIds		an array of host IDs to link the templates to
	 * @return 
	 *
	 * @return array 	an array of added hosts_templates rows, with "hostid" and "templateid" set for each row
	 */
	protected CArray<Map> link(CArray<Long> templateIds, CArray<Long> targetIds) {
		if (empty(templateIds)) {
			return null;
		}

		// permission check
		if (!API.Template(this.idBean, this.getSqlExecutor()).isReadable(templateIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		// check if someone passed duplicate templates in the same query
		CArray<Integer> templateIdDuplicates = rda_arrayFindDuplicates(templateIds);
		if (!rda_empty(templateIdDuplicates)) {
			CArray<String> duplicatesFound = array();
			for (Entry<Object, Integer> e : templateIdDuplicates.entrySet()) {
                Object value = e.getKey();
                Integer count = e.getValue();
				duplicatesFound.add(_s("template ID \"%1$s\" is passed %2$s times", value, count));
			}
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s("Cannot pass duplicate template IDs for the linkage: %s.", implode(", ", duplicatesFound))
			);
		}

		SqlBuilder sqlParts = null;
		// check if any templates linked to targets have more than one unique item key/application
		for(Object targetid : targetIds) {
			CTemplateGet options = new CTemplateGet();
			options.setNopermissions(true);
			options.setOutput(new String[]{"templateid"});
			options.setHostIds(Nest.as(targetid).asLong());
			CArray<Map> linkedTpls = API.Template(this.idBean, this.getSqlExecutor()).get(options);

			CArray<String> templateIdsAll = array_merge(templateIds, rda_objectValues(linkedTpls, "templateid"));

			sqlParts = new SqlBuilder();
			CArray<Map> dbItems = DBselect(getSqlExecutor(),
				"SELECT i.key_"+
					" FROM items i"+
					" WHERE "+sqlParts.dual.dbConditionInt("i.hostid", templateIdsAll.valuesAsLong())+
					" GROUP BY i.key_"+
					" HAVING COUNT(i.itemid)>1",
				sqlParts.getNamedParams()
			);
			Map dbItem = dbItems.get(0);
			if (!empty(dbItem)) {
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"hostid"});
				ioptions.setFilter("key_", Nest.value(dbItem,"key_").asString());
				ioptions.setFilter("flags"); //添加未指定值的flags，CItemDAO将去掉对flags的过滤
				ioptions.setTemplateIds(templateIdsAll.valuesAsLong());
				CArray<Map> dbItemHosts = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
				Long[] hostids = FuncsUtil.rda_toHash(dbItemHosts, "hostid").keysAsLong();

				options = new CTemplateGet();
				options.setOutput(new String[]{"name"});
				options.setTemplateIds(hostids);
				CArray<Map> templates = API.Template(this.idBean, this.getSqlExecutor()).get(options);

				Set names = FuncsUtil.rda_toHash(templates, "name").keySet();

				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Template \"%1$s\" with item key \"%2$s\" already linked to host.",
						names, Nest.value(dbItem,"key_").$()));
			}
		}

		// get DB templates which exists in all targets
		sqlParts = new SqlBuilder();
		CArray<Map> res = DBselect(getSqlExecutor(),
				"SELECT * FROM hosts_templates WHERE "+sqlParts.dual.dbConditionInt("hostid", targetIds.valuesAsLong()),
				sqlParts.getNamedParams());
		CArray<Map> mas = array();
		for (Map row : res) {
			if (!isset(mas,row.get("templateid"))) {
				Nest.value(mas,row.get("templateid")).$(array());
			}
			Nest.value(mas,row.get("templateid"),row.get("hostid")).$(1);
		}
		int targetIdCount = count(targetIds);
		CArray<Long> commonDBTemplateIds = array();
		for (Entry<Object, Map> e : mas .entrySet()) {
            Object templateId = e.getKey();
            Map targetList = e.getValue();
			if (count(targetList) == targetIdCount) {
				commonDBTemplateIds.add(Nest.as(templateId).asLong());
			}
		}

		// check if there are any template with triggers which depends on triggers in templates which will be not linked
		CArray commonTemplateIds = array_unique(array_merge(commonDBTemplateIds, templateIds));
		for(Object templateid : templateIds) {
			CArray<Long> triggerids = array();
			CArray<Map> dbTriggers = get_triggers_by_hostid(this.idBean, getSqlExecutor(), Nest.as(templateid).asLong());
			for (Map trigger : dbTriggers) {
				Nest.value(triggerids,trigger.get("triggerid")).$(Nest.value(trigger,"triggerid").$());
			}

			sqlParts = new SqlBuilder();
			String sql = "SELECT DISTINCT h.host"+
				" FROM trigger_depends td,functions f,items i,hosts h"+
				" WHERE ("+
				sqlParts.where.dbConditionInt("td.triggerid_down", triggerids.valuesAsLong())+
				" AND f.tenantid=td.tenantid"+
				" AND f.triggerid=td.triggerid_up"+
				" )"+
				" AND i.tenantid=f.tenantid"+
				" AND h.tenantid=i.tenantid"+
				" AND i.itemid=f.itemid"+
				" AND h.hostid=i.hostid"+
				" AND "+sqlParts.dual.dbConditionLong("h.hostid", commonTemplateIds.valuesAsLong(), true)+
				" AND h.status="+HOST_STATUS_TEMPLATE;
			CArray<Map> dbDepHosts = DBselect(getSqlExecutor(), sql,sqlParts.getNamedParams());
			Map dbDepHost = dbDepHosts.get(0);
			if (!empty(dbDepHost)) {
				CTemplateGet options = new CTemplateGet();
				options.setTemplateIds(Nest.as(templateid).asLong());
				options.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> tmpTpls = API.Template(this.idBean, this.getSqlExecutor()).get(options);
				Map tmpTpl = reset(tmpTpls);

				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Trigger in template \"%1$s\" has dependency with trigger in template \"%2$s\".", Nest.value(tmpTpl,"host").$(), Nest.value(dbDepHost,"host").$()));
			}
		}

		sqlParts = new SqlBuilder();
		res = DBselect(getSqlExecutor(),
			"SELECT ht.hostid,ht.templateid"+
				" FROM hosts_templates ht"+
				" WHERE "+sqlParts.dual.dbConditionInt("ht.hostid", targetIds.valuesAsLong())+
				" AND "+sqlParts.dual.dbConditionInt("ht.templateid", templateIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		CArray linked = array();
		for (Map row : res) {
			if (!isset(linked,row.get("hostid"))) {
				Nest.value(linked, row.get("hostid")).$(array());
			}
			Nest.value(linked, row.get("hostid"), row.get("templateid")).$(1);
		}

		// add template linkages, if problems rollback later
		CArray<Map> hostsLinkageInserts = array();
		for(Object targetid : targetIds) {
			for(Object templateid : templateIds) {
				if (isset(linked,targetid) && isset(Nest.value(linked,targetid,templateid).$())) {
					continue;
				}
				hostsLinkageInserts.add(map("hostid", targetid, "templateid", templateid));
			}
		}
		insert("hosts_templates", hostsLinkageInserts);

		// check if all trigger templates are linked to host.
		// we try to find template that is not linked to hosts (targetids)
		// and exists trigger which reference that template and template from (templateids)
		sqlParts = new SqlBuilder();
		String sql = "SELECT DISTINCT h.host"+
			" FROM functions f,items i,triggers t,hosts h"+
			" WHERE f.tenantid=i.tenantid"+
			" AND f.tenantid=t.tenantid"+
			" AND i.tenantid=h.tenantid"+
			" AND f.itemid=i.itemid"+
			" AND f.triggerid=t.triggerid"+
			" AND i.hostid=h.hostid"+
			" AND h.status="+HOST_STATUS_TEMPLATE+
			" AND NOT EXISTS (SELECT 1 FROM hosts_templates ht WHERE TRUE "+ //因租户的虚拟机会使用运营商的模型，所以删除
			" AND ht.templateid=i.hostid AND "+sqlParts.dual.dbConditionInt("ht.hostid", targetIds.valuesAsLong())+")"+
			" AND EXISTS (SELECT 1 FROM functions ff,items ii WHERE ff.tenantid=ii.tenantid "+
			" AND ff.tenantid=t.tenantid AND ff.itemid=ii.itemid AND ff.triggerid=t.triggerid AND "+sqlParts.dual.dbConditionInt("ii.hostid", templateIds.valuesAsLong())+ ")";
		CArray<Map> dbNotLinkedTpls = DBselect(getSqlExecutor(),sql, 1, sqlParts.getNamedParams());
		Map dbNotLinkedTpl = reset(dbNotLinkedTpls);
		if (!empty(dbNotLinkedTpl)) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s("Trigger has items from template \"%1$s\" that is not linked to host.", Nest.value(dbNotLinkedTpl,"host").$())
			);
		}

		// check template linkage circularity
		res = DBselect(getSqlExecutor(),
			"SELECT ht.hostid,ht.templateid"+
				" FROM hosts_templates ht,hosts h"+
				" WHERE ht.tenantid=h.tenantid "+
				" AND ht.hostid=h.hostid "+
				" AND h.status IN("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+","+HOST_STATUS_TEMPLATE+")"
		);

		// build linkage graph and prepare list for rootList generation
		CArray<CArray<Long>> graph = array();
		CArray<Long> hasParentList = array();
		CArray<Long> hasChildList = array();
		CArray<Long> all = array();
		for (Map row : res) {
			if (!isset(graph,row.get("hostid"))) {
				Nest.value(graph, row.get("hostid")).$(array());
			}
			Nest.value(graph, row.get("hostid")).asCArray().add(Nest.value(row,"templateid").$());
			Nest.value(hasParentList, row.get("templateid")).$(Nest.value(row,"templateid").$());
			Nest.value(hasChildList, row.get("hostid")).$(Nest.value(row,"hostid").$());
			Nest.value(all, row.get("templateid")).$(Nest.value(row,"templateid").$());
			Nest.value(all, row.get("hostid")).$(Nest.value(row,"hostid").$());
		}

		// get list of templates without parents
		CArray<Long> rootList = array();
		for(Long parentId : hasChildList) {
			if (!isset(hasParentList,parentId)) {
				rootList.add(parentId);
			}
		}

		// search cycles and double linkages in rooted parts of graph
		CArray<Integer> visited = array();
		for(Long root : rootList) {
			CArray<Integer> path = array();
			// raise exception on cycle or double linkage
			checkCircularAndDoubleLinkage(graph, root, path, visited);
		}

		// there is still possible cycles without root
		if (count(visited) < count(all)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Circular template linkage is not allowed."));
		}

		return hostsLinkageInserts;
	}
	
	protected void unlink(Long[] templateids) {
		unlink(templateids, null);
	}
	
	protected void unlink(Long[] templateids, Long[] targetids) {
		CArray cond = map("templateid", templateids);
		if (!is_null(targetids)) {
			Nest.value(cond,"hostid").$(targetids);
		}
		delete("hosts_templates", cond);

		CHostGet options = new CHostGet();
		Object hosts = null;
		if (!is_null(targetids)) {
			options.setHostIds(TArray.as(targetids).asLong());
			options.setOutput(new String[]{"hostid", "host"});
			options.setNopermissions(true);
			hosts  = API.Host(this.idBean, this.getSqlExecutor()).get(options);
		} else {
			options.setTemplateIds(TArray.as(templateids).asLong());
			options.setOutput(new String[]{"hostid", "host"});
			options.setNopermissions(true);
			hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
		}

		if (!empty(hosts)) {
			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(TArray.as(templateids).asLong());
			toptions.setOutput(new String[]{"hostid", "host"});
			toptions.setNopermissions(true);
			Object templates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);

			hosts = implode(", ", rda_objectValues(hosts, "host"));
			templates = implode(", ", rda_objectValues(templates, "host"));

			info(_s("Templates \"%1$s\" unlinked from hosts \"%2$s\".", templates, hosts));
		}
	}
	
	/**
	 * Searches for cycles and double linkages in graph.
	 *
	 * @throw APIException rises exception if cycle or double linkage is found
	 *
	 * @param array graph - array with keys as parent ids and values as arrays with child ids
	 * @param int current - cursor for recursive DFS traversal, starting point for algorithm
	 * @param array path - should be passed empty array for DFS
	 *
	 * @return boolean
	 */
	protected boolean checkCircularAndDoubleLinkage(CArray<CArray<Long>> graph, Long current, CArray<Integer> path, CArray<Integer> visited) {
		if (isset(path,current)) {
			if (Nest.value(path,current).asInteger() == 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Circular template linkage is not allowed."));
			} else {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Template cannot be linked to another template more than once even through other templates."));
			}
		}
		Nest.value(path,current).$(1);
		Nest.value(visited,current).$(1);

		if (isset(graph,current)) {
			for(Long next : graph.get(current)) {
				checkCircularAndDoubleLinkage(graph, next, path, visited);
			}
		}

		Nest.value(path,current).$(2);

		return false;
	}

}
