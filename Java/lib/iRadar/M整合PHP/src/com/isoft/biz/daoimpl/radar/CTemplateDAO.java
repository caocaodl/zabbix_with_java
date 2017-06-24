package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.AuditUtil.do_audit_off;
import static com.isoft.iradar.inc.AuditUtil.do_audit_on;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_PREG_HOST_FORMAT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.host.CHostNormalValidator;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

@CodeConfirmed("benne.2.2.6")
public class CTemplateDAO extends CHostGeneralDAO<CTemplateGet> {

	public CTemplateDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, new String[]{"hostid", "host", "name"});
	}

	@Override
	protected String pkOption(String tableName) {
		if (tableName!=null && !tableName.equals(this.tableName())) {
			return super.pkOption(tableName);
		} else {
			return "templateids";
		}
	}

	@Override
	public <T> T get(CTemplateGet params) {
		int userType = CWebUser.getType();
		String userid = Nest.as(CWebUser.get("userid")).asString();
		
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("templates", "h.hostid");
		sqlParts.from.put("hosts", "hosts h");
		sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			int permission = !empty(params.getEditable()) ? PERM_READ_WRITE : PERM_READ;

			List<Long> userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid);
			
			sqlParts.where.put(
					"EXISTS ("+
					"SELECT NULL"+
					" FROM hosts_groups hgg"+
						" JOIN rights r"+
							" ON r.tenantid=hgg.tenantid"+ 
								" AND r.id=hgg.groupid"+
								" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups.toArray(new Long[0]))+
					" WHERE h.tenantid=hgg.tenantid"+
					" AND h.hostid=hgg.hostid"+
					" GROUP BY hgg.hostid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
						" AND MAX(r.permission)>="+permission+
					")"
			);
		}
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid", params.getGroupIds());
			sqlParts.where.put("hgh.tenantid","hg.tenantid=h.tenantid");
			sqlParts.where.put("hgh","hg.hostid=h.hostid");
	
			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hg","hg.groupid");
			}
		}
		
		// templateids
		if (!is_null(params.getTemplateIds())) {
			sqlParts.where.dbConditionInt("templateid","h.hostid", params.getTemplateIds());
		}
		
		// parentTemplateids
		if (!is_null(params.getParentTemplateIds())) {
			sqlParts.select.put("parentTemplateid","ht.templateid as parentTemplateid");
			sqlParts.from.put("hosts_templates","hosts_templates ht");
			sqlParts.where.dbConditionInt("ht.templateid", params.getParentTemplateIds());
			sqlParts.where.put("hht.tenantid","h.tenantid=ht.tenantid");
			sqlParts.where.put("hht","h.hostid=ht.hostid");
	
			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("templateid","ht.templateid");
			}
		}
				
		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("linked_hostid","ht.hostid as linked_hostid");
			sqlParts.from.put("hosts_templates","hosts_templates ht");
			sqlParts.where.dbConditionInt("ht.hostid",params.getHostIds());
			sqlParts.where.put("hht.tenantid","h.tenantid=ht.tenantid");
			sqlParts.where.put("hht","h.hostid=ht.templateid");
	
			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("ht","ht.hostid");
			}
		}
	
		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","i.itemid");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("i.itemid",params.getItemIds());
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
		}
	
		// triggerids
		if (!is_null(params.getTriggerIds())) {
			sqlParts.select.put("triggerid","f.triggerid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("f.triggerid",params.getTriggerIds());
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
		}
	
		// graphids
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid","gi.graphid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("gi.graphid",params.getGraphIds());
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
		}
		
		// with_items
		if (!is_null(params.getWithItems())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE h.tenantid=i.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}
		
		// with_triggers
		if (!is_null(params.getWithTriggers())) {
			sqlParts.where.put("EXISTS("+
				"SELECT NULL"+
				" FROM items i,functions f,triggers t"+
				" WHERE i.tenantid=h.tenantid"+
				" AND i.tenantid=f.tenantid"+
				" AND f.tenantid=t.tenantid"+
				" AND i.hostid=h.hostid"+
				" AND i.itemid=f.itemid"+
				" AND f.triggerid=t.triggerid"+
				" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
				")");
		}
	
		// with_graphs
		if (!is_null(params.getWithGraphs())) {
			sqlParts.where.put("EXISTS("+
				"SELECT NULL"+
				" FROM items i,graphs_items gi,graphs g"+
				" WHERE i.tenantid=h.tenantid"+
				" AND i.tenantid=gi.tenantid"+
				" AND gi.tenantid=g.tenantid"+
				" AND i.hostid=h.hostid"+
				" AND i.itemid=gi.itemid"+
				" AND gi.graphid=g.graphid"+
				" AND g.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
				")");
		}
		
		// with_httptests, with_monitored_httptests
		if (!empty(params.getWithHttpTests())) {
			sqlParts.where.put("EXISTS (SELECT ht.httptestid FROM httptest ht WHERE ht.tenantid=h.tenantid AND ht.hostid=h.hostid)");
		}
	
		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("hosts h", params, sqlParts);
		}
		
		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("hosts h", params, sqlParts);
		}		
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
	
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
				if (params.getGroupCount() != null) {
					result.add(row);
				} else {
					ret = row.get("rowscount");
				}
			} else {
				Long id = (Long)row.remove("hostid");
				row.put("templateid", id);
				
				Map resultRow = result.get(id);
				if (!isset(resultRow)) {
					resultRow = new HashMap();
					result.put(id, resultRow);
				}
	
				// groupids
				if (isset(row.get("groupid")) && is_null(params.getSelectGroups())) {
					if (!isset(resultRow.get("groups"))) {
						resultRow.put("groups", new CArray());
					}
					((CArray)resultRow.get("groups")).add(array("groupid", row.remove("groupid")));
				}
				// hostids
				if (isset(row.get("linked_hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(resultRow.get("hosts"))) {
						resultRow.put("hosts", new CArray());
					}
					((CArray)resultRow.get("hosts")).add(array("hostid", row.remove("linked_hostid")));
				}
				// parentTemplateids
				if (isset(row.get("parentTemplateid")) && is_null(params.getSelectParentTemplates())) {
					if (!isset(resultRow.get("parentTemplates"))) {
						resultRow.put("parentTemplates", new CArray());
					}
					((CArray)resultRow.get("parentTemplates")).add(array("templateid", row.remove("parentTemplateid")));
				}
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(resultRow.get("items"))) {
						resultRow.put("items", new CArray());
					}
					((CArray)resultRow.get("items")).add(array("itemid", row.remove("itemid")));
				}
				// triggerids
				if (isset(row.get("triggerid")) && is_null(params.getSelectTriggers())) {
					if (!isset(resultRow.get("triggers"))) {
						resultRow.put("triggers", new CArray());
					}
					((CArray)resultRow.get("triggers")).add(array("triggerid", row.remove("triggerid")));
				}
				// graphids
				if (isset(row.get("graphid")) && is_null(params.getSelectGraphs())) {
					if (!isset(resultRow.get("graphs"))) {
						resultRow.put("graphs", new CArray());
					}
					((CArray)resultRow.get("graphs")).add(array("graphid", row.remove("graphid")));
				}
				
				resultRow.putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}
	
		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	/**
	 * Get Template ID by Template name
	 *
	 * @param array template_data
	 * @param array template_data['host']
	 * @param array template_data['templateid']
	 * @return string templateid
	 */
	@Override
	public CArray<Map> getObjects(Map<String, Object[]> filter) {
		CTemplateGet params = this.getParamInstance();
		params.setFilter(filter);
		params.setOutput(API_OUTPUT_EXTEND);
		return get(params);
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("templateid", "host", "name"));
		
		CTemplateGet options = new CTemplateGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"templateid"});
		options.setNopermissions(true);
		options.setLimit(1);
		
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	/**
	 * Add Template
	 *
	 * @param array templates multidimensional array with templates data
	 * @param string templates["host"]
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> templates) {
		CArray<Long> templateids = array();
		CArray groupids = array();

		// CHECK IF HOSTS HAVE AT LEAST 1 GROUP {{{
		for (Map template : templates) {
			if (empty(Nest.value(template,"groups").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No groups for template \"%1$s\".", Nest.value(template,"host").$()));
			}
			Nest.value(template,"groups").$(rda_toArray(Nest.value(template,"groups").$()));

			CArray<Map> groups = Nest.value(template,"groups").asCArray();
			for (Map group : groups) {
				Nest.value(groupids,group.get("groupid")).$(Nest.value(group,"groupid").$());
			}
		}
		// }}} CHECK IF HOSTS HAVE AT LEAST 1 GROUP


		// PERMISSIONS {{{
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setGroupIds(groupids.valuesAsLong());
		hgoptions.setEditable(true);
		hgoptions.setPreserveKeys(true);
		CArray<Map> updGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);
		for(Object groupid : groupids) {
			if (!isset(updGroups,groupid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}
		// }}} PERMISSIONS

		for (Map template : templates) {
			// If visible name is not given or empty it should be set to host name
			if ((!isset(template,"name") || rda_empty(trim(Nest.value(template,"name").asString()))) && isset(template,"host")) {
				Nest.value(template,"name").$(Nest.value(template,"host").$());
			}

			CArray templateDbFields = map(
				"host", null
			);

			if (!check_db_fields(templateDbFields, template)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Field \"host\" is mandatory"));
			}

			if (preg_match("^"+RDA_PREG_HOST_FORMAT+"$", Nest.value(template,"host").asString())==0) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Incorrect characters used for Template name \"%1$s\"",
					Nest.value(template,"host").asString()
				));
			}

			if (isset(template,"host")) {
				if (exists(map("host", Nest.value(template,"host").$()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Template \"%1$s\" already exists.", Nest.value(template,"host").$()));
				}

				if (API.Host(this.idBean, this.getSqlExecutor()).exists(map("host", Nest.value(template,"host").$()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host \"%1$s\" already exists.", Nest.value(template,"host").$()));
				}
			}

			if (isset(template,"name")) {
				if (exists(map("name", Nest.value(template,"name").$()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
						"Template with the same visible name \"%1$s\" already exists.",
						Nest.value(template,"name").asString()
					));
				}

				if (API.Host(this.idBean, this.getSqlExecutor()).exists(map("name", Nest.value(template,"name").$()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
						"Host with the same visible name \"%1$s\" already exists.",
						Nest.value(template,"name").asString()
					));
				}
			}

			CArray<Long> ctemplateids = insert("hosts", array((Map)map("host", Nest.value(template,"host").$(),"name", Nest.value(template,"name").$(), "status", HOST_STATUS_TEMPLATE)));
			Long templateid = reset(ctemplateids);
			templateids.add(templateid);

			Long hostgroupid = null;
			boolean result;
			Map params = new HashMap();
			for(Map group : (CArray<Map>)Nest.value(template,"groups").asCArray()) {
				hostgroupid = get_dbid(this.idBean, getSqlExecutor(),"hosts_groups", "hostgroupid");
				params.put("tenantid", this.idBean.getTenantId());
				params.put("hostgroupid", hostgroupid);
				params.put("hostid", templateid);
				params.put("groupid", Nest.value(group,"groupid").$());
				result = DBexecute(getSqlExecutor(), "INSERT INTO hosts_groups (tenantid,hostgroupid,hostid,groupid) VALUES (#{tenantid},#{hostgroupid},#{hostid},#{groupid})", params);
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
				}
			}

			Nest.value(template,"templateid").$(templateid);
			CArray options = map();
			Nest.value(options,"templates").$(template);
			if (isset(template,"templates") && !is_null(Nest.value(template,"templates").$()))
				Nest.value(options,"templates_link").$(Nest.value(template,"templates").$());
			if (isset(template,"macros") && !is_null(Nest.value(template,"macros").$()))
				Nest.value(options,"macros").$(Nest.value(template,"macros").$());
			if (isset(template,"hosts") && !is_null(Nest.value(template,"hosts").$()))
				Nest.value(options,"hosts").$(Nest.value(template,"hosts").$());

			result = !empty(massAdd(options));
			if (!result) throw CDB.exception(RDA_API_ERROR_PARAMETERS,"");
		}

		return map("templateids", templateids.valuesAsLong());
	}

	/**
	 * Update Template
	 *
	 * @param array templates multidimensional array with templates data
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> templates) {
		CArray templateids = rda_objectValues(templates, "templateid");
		
		CTemplateGet toptions = new CTemplateGet();
		toptions.setTemplateIds(templateids.valuesAsLong());
		toptions.setEditable(true);
		toptions.setOutput(API_OUTPUT_EXTEND);
		toptions.setPreserveKeys(true);

		CArray<Map> updTemplates = get(toptions);

		for(Map template : templates) {
			if (!isset(updTemplates,template.get("templateid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}

		CArray<Map> macros = map();
		for(Map template : templates) {
			// if visible name is not given or empty it should be set to host name
			if ((!isset(template,"name") || rda_empty(trim(Nest.value(template,"name").asString()))) && isset(template,"host")) {
				Nest.value(template,"name").$(Nest.value(template,"host").$());
			}
			Map tplTmp = Clone.deepcopy(template);

			Nest.value(template,"templates_link").$(isset(template,"templates") ? Nest.value(template,"templates").$() : null);

			if (isset(template,"macros")) {
				Nest.value(macros,template.get("templateid")).$(Nest.value(template,"macros").$());
				unset(template,"macros");
			}

			unset(template,"templates");
			unset(template,"templateid");
			unset(tplTmp,"templates");

			Nest.value(template,"templates").$(array(tplTmp));
			boolean result = !empty(massUpdate(CArray.valueOf(template)));
			if (!result) throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Failed to update template"));
		}

		if (!empty(macros)) {
			API.UserMacro(this.idBean, this.getSqlExecutor()).replaceMacros(macros);
		}

		return map("templateids", templateids.valuesAsLong());
	}

	/**
	 * Delete Template
	 *
	 * @param array templateids
	 * @param array templateids["templateids"]
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> delete(Long... templateids) {
		if (empty(templateids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
		
		do_audit_off();
		try {

			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(TArray.as(templateids).asLong());
			toptions.setEditable(true);
			toptions.setOutput(API_OUTPUT_EXTEND);
			toptions.setPreserveKeys(true);
			CArray<Map> delTemplates = get(toptions);
			for(Long templateid : templateids) {
				if (!isset(delTemplates,templateid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
				}
			}
	
			API.Template(this.idBean, this.getSqlExecutor()).unlink(templateids, null, true);
	
			// delete the discovery rules first
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setHostIds(TArray.as(templateids).asLong());
			droptions.setNopermissions(true);
			droptions.setPreserveKeys(true);
			CArray<Map> delRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
			if (!empty(delRules)) {
				API.DiscoveryRule(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delRules).valuesAsLong());
			}
	
			// delete the items
			CItemGet ioptions = new CItemGet();
			ioptions.setTemplateIds(TArray.as(templateids).asLong());
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setNopermissions(true);
			ioptions.setPreserveKeys(true);
			CArray<Map> delItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
			if (!empty(delItems)) {
				API.Item(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delItems).valuesAsLong());
			}
	
			// delete screen items
			SqlBuilder sqlParts = new SqlBuilder();
			DBexecute(getSqlExecutor(),
					"DELETE FROM screens_items WHERE "+sqlParts.dual.dbConditionInt("resourceid", templateids)+" AND resourcetype="+SCREEN_RESOURCE_HOST_TRIGGERS,
					sqlParts.getNamedParams()
			);
	
			// delete host from maps
			if (!empty(templateids)) {
				delete("sysmaps_elements", (Map)map("elementtype", SYSMAP_ELEMENT_TYPE_HOST, "elementid", templateids));
			}
	
			// disable actions
			// actions from conditions
			CArray actionids = array();
			sqlParts = new SqlBuilder();
			String sql = "SELECT DISTINCT actionid"+
				" FROM conditions"+
				" WHERE conditiontype="+CONDITION_TYPE_TEMPLATE+
				" AND "+sqlParts.dual.dbConditionString("value", TArray.as(templateids).asString());
			CArray<Map> dbActions = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbAction : dbActions) {
				Nest.value(actionids,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
			}
	
			// actions from operations
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT o.actionid"+
				" FROM operations o,optemplate ot"+
				" WHERE o.tenantid=ot.tenantid"+
				" AND o.operationid=ot.operationid"+
				" AND "+sqlParts.dual.dbConditionInt("ot.templateid", templateids);
			dbActions = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbAction : dbActions) {
				Nest.value(actionids,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
			}
	
			if (!empty(actionids)) {
				update("actions", array((Map)map(
					"values", map("status", ACTION_STATUS_DISABLED),
					"where", map("actionid", actionids)
				)));
			}
	
			// delete action conditions
			delete("conditions", (CArray)map(
				"conditiontype", CONDITION_TYPE_TEMPLATE,
				"value", templateids
			));
	
			// delete action operation commands
			CArray operationids = array();
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT ot.operationid"+
				" FROM optemplate ot"+
				" WHERE "+sqlParts.dual.dbConditionInt("ot.templateid", templateids);
			CArray<Map> dbOperations = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbOperation : dbOperations) {
				Nest.value(operationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
			}
	
			delete("optemplate", (CArray)map(
				"templateid",templateids
			));
	
			// delete empty operations
			CArray delOperationids = array();
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT o.operationid"+
				" FROM operations o"+
				" WHERE "+sqlParts.dual.dbConditionInt("o.operationid", operationids.valuesAsLong())+
				" AND NOT EXISTS(SELECT NULL FROM optemplate ot WHERE ot.tenantid=o.tenantid"+
				" AND ot.operationid=o.operationid)";
			dbOperations = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbOperation : dbOperations) {
				Nest.value(delOperationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
			}
	
			delete("operations", (CArray)map(
				"operationid", delOperationids.valuesAsLong()
			));
	
			// http tests
			CHttpTestGet htoptions = new CHttpTestGet();
			htoptions.setTemplateIds(TArray.as(templateids).asLong());
			htoptions.setOutput(new String[]{"httptestid"});
			htoptions.setNopermissions(true);
			htoptions.setPreserveKeys(true);
			CArray<Map> delHttpTests = API.HttpTest(this.idBean, this.getSqlExecutor()).get(htoptions);
			if (!empty(delHttpTests)) {
				API.HttpTest(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delHttpTests).valuesAsLong());
			}
	
			// Applications
			CAppGet aoptions = new CAppGet();
			aoptions.setTemplateIds(TArray.as(templateids).asLong());
			aoptions.setOutput(new String[]{"applicationid"});
			aoptions.setNopermissions(true);
			aoptions.setPreserveKeys(true);
			CArray<Map> delApplications = API.Application(this.idBean, this.getSqlExecutor()).get(aoptions);
			if (!empty(delApplications)) {
				API.Application(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delApplications).valuesAsLong());
			}
	
			delete("hosts", (CArray)map("hostid", templateids));
	
			do_audit_on();
			
			// TODO: remove info from API
			for(Map template : delTemplates) {
				info(_s("Deleted: Template \"%1$s\".", Nest.value(template,"name").$()));
				add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_DELETE, AUDIT_RESOURCE_HOST, Nest.value(template,"templateid").asLong(), Nest.value(template,"host").asString(), "hosts", null, null);
			}
	
			return map("templateids", templateids);
		} finally {
			do_audit_on();
		}
	}

	/**
	 * Additionally allows to link templates to hosts and other templates.
	 *
	 * Checks write permissions for templates.
	 *
	 * Additional supported data parameters are:
	 * - hosts  - an array of hosts or templates to link the given templates to
	 *
	 * @param array data
	 *
	 * @return array
	 */
	@Override
	public CArray massAdd(CArray data) {
		CArray<Map> templates = isset(data,"templates") ? rda_toArray(Nest.value(data,"templates").asCArray()) : array();
		CArray templateids = rda_objectValues(templates, "templateid");

		// check permissions
		if (!isWritable(templateids.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		// link hosts to the given templates
		if (isset(data,"hosts") && !empty(Nest.value(data,"hosts").$())) {
			CArray hostIds = rda_objectValues(Nest.value(data,"hosts").$(), "hostid");

			// check if any of the hosts are discovered
			checkValidator(hostIds.valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()), map(
				"message", _("Cannot update templates on discovered host \"%1$s\".")
			)));

			link(templateids, hostIds);
		}

		Nest.value(data,"hosts").$(array());
		return super.massAdd(data);
	}

	/**
	 * Mass update hosts
	 *
	 * @param _array hosts multidimensional array with Hosts data
	 * @param array Nest.value(hosts,"hosts").$() Array of Host objects to update
	 * @return boolean
	 */
	public CArray massUpdate(CArray data) {
		CArray<Map> templates = rda_toArray(Nest.value(data,"templates").$());
		CArray templateids = rda_objectValues(templates, "templateid");

		CTemplateGet toptions = new CTemplateGet();
		toptions.setTemplateIds(templateids.valuesAsLong());
		toptions.setEditable(true);
		toptions.setOutput(API_OUTPUT_EXTEND);
		toptions.setPreserveKeys(true);
		CArray<Map> updTemplates = get(toptions);
		for(Map template : templates) {
			if (!isset(updTemplates,template.get("templateid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}

		// CHECK IF TEMPLATES HAVE AT LEAST 1 GROUP {{{
		if (isset(data,"groups") && empty(Nest.value(data,"groups").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No groups for template"));
		}
		// }}} CHECK IF TEMPLATES HAVE AT LEAST 1 GROUP


		// UPDATE TEMPLATES PROPERTIES {{{
		if (isset(data,"name")) {
			if (count(templates) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot mass update visible template name"));
			}

			Map curTemplate = reset(templates);
			
			toptions = new CTemplateGet();
			toptions.setFilter("name", Nest.value(curTemplate,"name").asString());
			toptions.setOutput(new String[]{"templateid"});
			toptions.setEditable(true);
			toptions.setNopermissions(true);

			CArray<Map> templateExists = get(toptions);
			Map templateExist = reset(templateExists);

			if (!empty(templateExist) && (bccomp(Nest.value(templateExist,"templateid").$(), Nest.value(curTemplate,"templateid").$()) != 0)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Template with the same visible name \"%1$s\" already exists.",
					Nest.value(curTemplate,"name").asString()
				));
			}

			// can't set the same name as existing host
			if (API.Host(this.idBean, this.getSqlExecutor()).exists(map("name", Nest.value(curTemplate,"name").asString()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Host with the same visible name \"%1$s\" already exists.",
					Nest.value(curTemplate,"name").asString()
				));
			}
		}

		if (isset(data,"host")) {
			if (count(templates) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot mass update template name"));
			}

			Map curTemplate = reset(templates);
			
			toptions = new CTemplateGet();
			toptions.setFilter("host", Nest.value(curTemplate,"host").asString());
			toptions.setOutput(new String[]{"templateid"});
			toptions.setEditable(true);
			toptions.setNopermissions(true);

			CArray<Map> templateExists = get(toptions);
			Map templateExist = reset(templateExists);

			if (!empty(templateExist) && (bccomp(Nest.value(templateExist,"templateid").$(), Nest.value(curTemplate,"templateid").$()) != 0)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Template with the same name \"%1$s\" already exists.",
					Nest.value(curTemplate,"host").asString()
				));
			}

			// can't set the same name as existing host
			if (API.Host(this.idBean, this.getSqlExecutor()).exists(map("host", Nest.value(curTemplate,"host").asString()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Host with the same name \"%1$s\" already exists.",
					Nest.value(curTemplate,"host").asString()
				));
			}
		}

		if (isset(data,"host") && preg_match("^"+RDA_PREG_HOST_FORMAT+"$", Nest.value(data,"host").asString())==0) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
				"Incorrect characters used for template name \"%1$s\".",
				Nest.value(data,"host").asString()
			));
		}

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<String> sqlSet = array();
		if (isset(data,"host")) {
			sqlSet.add("host="+sqlParts.marshalParam(Nest.value(data,"host").asString()));
		}

		if (isset(data,"name")) {
			// if visible name is empty replace it with host name
			if (rda_empty(trim(Nest.value(data,"name").asString())) && isset(data,"host")) {
				sqlSet.add("name="+sqlParts.marshalParam(Nest.value(data,"host").asString()));
			}
			// we cannot have empty visible name
			else if (rda_empty(trim(Nest.value(data,"name").asString())) && !isset(data,"host")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot have empty visible template name"));
			} else {
				sqlSet.add("name="+sqlParts.marshalParam(Nest.value(data,"name").asString()));
			}
		}

		boolean result;
		if (!empty(sqlSet)) {
			String sql = "UPDATE hosts SET "+implode(", ", sqlSet)+" WHERE "+sqlParts.dual.dbConditionInt("hostid", templateids.valuesAsLong());
			result = DBexecute(getSqlExecutor(),sql,sqlParts.getNamedParams());
		}
		// }}} UPDATE TEMPLATES PROPERTIES


		// UPDATE HOSTGROUPS LINKAGE {{{
		if (isset(data,"groups") && !is_null(Nest.value(data,"groups").$())) {
			Nest.value(data,"groups").$(rda_toArray(Nest.value(data,"groups").$()));
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setHostIds(templateids.valuesAsLong());
			CArray<Map> templateGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);
			CArray templateGroupids = rda_objectValues(templateGroups, "groupid");
			CArray newGroupids = rda_objectValues(Nest.value(data,"groups").$(), "groupid");

			CArray groupsToAdd = array_diff(newGroupids, templateGroupids);

			if (!empty(groupsToAdd)) {
				result = !empty(massAdd(map(
					"templates", templates,
					"groups", rda_toObject(groupsToAdd, "groupid")
				)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't add group"));
				}
			}

			CArray groupidsToDel = array_diff(templateGroupids, newGroupids);
			if (!empty(groupidsToDel)) {
				result = !empty(massRemove(map(
					"templateids", templateids,
					"groupids", groupidsToDel
				)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't remove group"));
				}
			}
		}
		// }}} UPDATE HOSTGROUPS LINKAGE

		Nest.value(data,"templates_clear").$(isset(data,"templates_clear") ? rda_toArray(Nest.value(data,"templates_clear").$()) : array());
		CArray templateidsClear = rda_objectValues(Nest.value(data,"templates_clear").$(), "templateid");

		if (!empty(Nest.value(data,"templates_clear").$())) {
			result = !empty(massRemove(map(
				"templateids", templateids,
				"templateids_clear", templateidsClear
			)));
		}

		// UPDATE TEMPLATE LINKAGE {{{
		// firstly need to unlink all things, to correctly check circulars

		CArray newHostids = null,templateHostids=null;
		if (isset(data,"hosts") && !is_null(Nest.value(data,"hosts").$())) {
			CHostGet hoptions = new CHostGet();
			hoptions.setTemplateIds(templateids.valuesAsLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			CArray<Map> templateHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
			templateHostids = rda_objectValues(templateHosts, "hostid");
			newHostids = rda_objectValues(Nest.value(data,"hosts").$(), "hostid");

			CArray hostsToDel = array_diff(templateHostids, newHostids);
			CArray hostidsToDel = array_diff(hostsToDel, templateidsClear);

			if (!empty(hostidsToDel)) {
				result = !empty(massRemove(map(
					"hostids", hostidsToDel,
					"templateids", templateids
				)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't unlink template"));
				}
			}
		}

		CArray templateTemplateids=null,newTemplateids=null;
		if (isset(data,"templates_link") && !is_null(Nest.value(data,"templates_link").$())) {
			toptions = new CTemplateGet();
			toptions.setHostIds(templateids.valuesAsLong());
			CArray<Map> templateTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);
			templateTemplateids = rda_objectValues(templateTemplates, "templateid");
			newTemplateids = rda_objectValues(Nest.value(data,"templates_link").$(), "templateid");

			CArray templatesToDel = array_diff(templateTemplateids, newTemplateids);
			CArray templateidsToDel = array_diff(templatesToDel, templateidsClear);
			if (!empty(templateidsToDel)) {
				result = !empty(massRemove(map(
					"templateids", templateids,
					"templateids_link", templateidsToDel
				)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't unlink template"));
				}
			}
		}

		if (isset(data,"hosts") && !is_null(Nest.value(data,"hosts").$())) {

			CArray hostsToAdd = array_diff(newHostids, templateHostids);
			if (!empty(hostsToAdd)) {
				result = !empty(massAdd(map("templates", templates, "hosts", hostsToAdd)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't link template"));
				}
			}
		}

		if (isset(data,"templates_link") && !is_null(Nest.value(data,"templates_link").$())) {
			CArray templatesToAdd = array_diff(newTemplateids, templateTemplateids);
			if (!empty(templatesToAdd)) {
				result = !empty(massAdd(map("templates", templates, "templates_link", templatesToAdd)));
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Can't link template"));
				}
			}
		}
		// }}} UPDATE TEMPLATE LINKAGE

		// macros
		if (isset(data,"macros")) {
			delete("hostmacro", (CArray)map("hostid", templateids.valuesAsLong()));

			massAdd(map(
				"hosts", templates,
				"macros", Nest.value(data,"macros").$()
			));
		}

		return map("templateids", templateids.valuesAsLong());
	}

	/**
	 * Additionally allows to unlink templates from hosts and other templates.
	 *
	 * Checks write permissions for templates.
	 *
	 * Additional supported data parameters are:
	 * - hostids  - an array of host or template IDs to unlink the given templates from
	 *
	 * @param array data
	 *
	 * @return array
	 */
	@Override
	public CArray massRemove(CArray data) {
		CArray templateids = rda_toArray(Nest.value(data,"templateids").$());

		// check permissions
		if (!isWritable(templateids.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		if (isset(data,"hostids")) {
			// check if any of the hosts are discovered
			checkValidator(Nest.array(data,"hostids").asLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()), map(
				"message", _("Cannot update templates on discovered host \"%1$s\".")
			)));

			API.Template(this.idBean, this.getSqlExecutor()).unlink(templateids.valuesAsLong(), rda_toArray(Nest.value(data,"hostids").$()).valuesAsLong());
		}

		Nest.value(data,"hostids").$(array());
		return super.massRemove(data);
	}

	@Override
	public boolean isReadable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CTemplateGet options = new CTemplateGet();
		options.setTemplateIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CTemplateGet options = new CTemplateGet();
		options.setTemplateIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CTemplateGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] templateids = result.keysAsLong();

		// Adding Templates
		if (!is_null(params.getSelectTemplates())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectTemplates())){
				CRelationMap relationMap = createRelationMap(result, "templateid", "hostid", "hosts_templates");
				CTemplateGet options = new CTemplateGet();
				options.setOutput(params.getSelectTemplates());
				options.setTemplateIds(relationMap.getRelatedLongIds());
				options.setPreserveKeys(true);
				CArray<Map> templates = API.Template(this.idBean, this.getSqlExecutor()).get(options);
				if(!is_null(params.getLimitSelects())) {
					order_result(templates, "host");
				}
				relationMap.mapMany(result, templates, "templates", params.getLimitSelects());
			} else {
				CTemplateGet options = new CTemplateGet();
				options.setParentTemplateIds(templateids);
				options.setCountOutput(true);
				options.setGroupCount(true);
				
				CArray<Map> templates = API.Template(this.idBean, this.getSqlExecutor()).get(options);
				for(Entry<Object, Map> e: result.entrySet()){
					Object templateid = e.getKey();
					Map template = e.getValue();
					if(templates.containsKey(templateid)){
						template.put("templates", ((Map)templates.get(templateid)).get("rowscount"));
					} else {
						template.put("templates", 0);
					}
				}
			}
		}
		
		// Adding Hosts
		if (!is_null(params.getSelectHosts())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectHosts())){
				CRelationMap relationMap = createRelationMap(result, "templateid", "hostid", "hosts_templates");
				CHostGet options = new CHostGet();
				options.setOutput(params.getSelectHosts());
				options.setHostIds(relationMap.getRelatedLongIds());
				options.setPreserveKeys(true);
				CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
				if(!is_null(params.getLimitSelects())) {
					order_result(hosts, "host");
				}
				relationMap.mapMany(result, hosts, "hosts", params.getLimitSelects());
			} else {
				CHostGet options = new CHostGet();
				options.setTemplateIds(templateids);
				options.setCountOutput(true);
				options.setGroupCount(true);
				CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
				hosts = rda_toHash(hosts, "templateid");
				
				for(Entry<Object, Map> e: result.entrySet()){
					Object templateid = e.getKey();
					Map template = e.getValue();
					if(hosts.containsKey(templateid)){
						template.put("hosts", ((Map)hosts.get(templateid)).get("rowscount"));
					} else {
						template.put("hosts", 0);
					}
				}
			}
		}
		
		// Adding screens
		if (!is_null(params.getSelectScreens())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectScreens())){
				CTemplateScreenGet options = new CTemplateScreenGet();
				options.setOutput(outputExtend("screens", new String[]{"templateid"}, params.getSelectScreens()));
				options.setTemplateIds(templateids);
				options.setNopermissions(true);
				CArray<Map> screens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(options);
				if(!is_null(params.getLimitSelects())) {
					order_result(screens, "name");
				}
				
				// preservekeys is not supported by templatescreen.get, so we're building a map using array keys
				CRelationMap relationMap = new CRelationMap();
				for(Entry<Object, Map> e: screens.entrySet()){
					Object key = e.getKey();
					Map screen = e.getValue();
					relationMap.addRelation(Nest.value(screen,"templateid").$(), key);
				}
				
				unsetExtraFields(screens, new String[]{"templateid"}, params.getSelectScreens());
				relationMap.mapMany(result, screens, "screens", params.getLimitSelects());
			} else {
				CTemplateScreenGet options = new CTemplateScreenGet();
				options.setTemplateIds(templateids);
				options.setNopermissions(true);
				options.setCountOutput(true);
				options.setGroupCount(true);
				CArray<Map> screens = API.TemplateScreen(this.idBean, getSqlExecutor()).get(options);
				screens = rda_toHash(screens, "templateid");
				
				for(Entry<Object, Map> e: result.entrySet()){
					Object templateid = e.getKey();
					Map template = e.getValue();
					if(screens.containsKey(templateid)){
						template.put("screens", ((Map)screens.get(templateid)).get("rowscount"));
					} else {
						template.put("screens", 0);
					}
				}
			}
		}		
	}
	
}
