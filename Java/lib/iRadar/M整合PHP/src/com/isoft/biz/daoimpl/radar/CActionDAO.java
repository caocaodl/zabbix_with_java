package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.ActionsUtil.discovery_object2str;
import static com.isoft.iradar.inc.ActionsUtil.eventType;
import static com.isoft.iradar.inc.ActionsUtil.get_operators_by_conditiontype;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.rda_dbcast_2bigint;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_APPLICATION;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DCHECK;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DHOST_IP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DOBJECT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DRULE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_PORT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSTATUS;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DUPTIME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DVALUE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_METADATA;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_PROXY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TIME_PERIOD;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_SEVERITY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_VALUE;
import static com.isoft.iradar.inc.Defines.EVENT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.EVENT_NOT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_COMMAND;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_DISABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ENABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_REMOVE;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_GLOBAL_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_object_status2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_diff;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_push;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_ctype_digit;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.ValidateUtil.validate_ip_range;
import static com.isoft.iradar.inc.ValidateUtil.validate_port_list;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.utils.MixedUtil;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with actions.
 * @author benne
 */
@CodeConfirmed("benne.2.2.6")
public class CActionDAO extends CCoreLongKeyDAO<CActionGet> {

	public CActionDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "actions", "a", new String[]{"actionid", "name", "status"});
	}

	/**
	 * Get actions data
	 *
	 * @param array options
	 * @param array options["itemids"]
	 * @param array options["hostids"]
	 * @param array options["groupids"]
	 * @param array options["actionids"]
	 * @param array options["applicationids"]
	 * @param array options["status"]
	 * @param array options["editable"]
	 * @param array options["extendoutput"]
	 * @param array options["count"]
	 * @param array options["pattern"]
	 * @param array options["limit"]
	 * @param array options["order"]
	 *
	 * @return array|int item data as array or false if error
	 */
	@Override
	public <T> T get(CActionGet params) {
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("actions", "a.actionid");
		sqlParts.from.put("actions", "actions a");
				
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			// conditions are checked here by sql, operations after, by api queries
			int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;

			Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);

			// condition hostgroup
			sqlParts.where.put("NOT EXISTS ("+
					"SELECT NULL"+
					" FROM conditions cc"+
						" LEFT JOIN rights r"+
							" ON r.tenantid = cc.tenantid "+
						        " AND r.id="+rda_dbcast_2bigint("cc.value")+
								" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE a.tenantid = cc.tenantid "+
						" AND a.actionid=cc.actionid"+
						" AND cc.conditiontype="+CONDITION_TYPE_HOST_GROUP+
					" GROUP BY cc.value"+
					" HAVING MIN(r.permission) IS NULL"+
						" OR MIN(r.permission)="+PERM_DENY+
						" OR MAX(r.permission)<"+permission+
					")");

			// condition host or template
			sqlParts.where.put("NOT EXISTS ("+
					"SELECT NULL"+
					" FROM conditions cc,hosts_groups hgg"+
						" LEFT JOIN rights r"+
							" ON r.tenantid = hgg.tenantid "+
						        " AND r.id=hgg.groupid"+
								" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE a.tenantid=cc.tenantid"+
						" AND a.actionid=cc.actionid"+
						" AND "+rda_dbcast_2bigint("cc.value")+"=hgg.hostid"+
						" AND cc.conditiontype IN ("+CONDITION_TYPE_HOST+","+CONDITION_TYPE_TEMPLATE+")"+
					" GROUP BY cc.value"+
					" HAVING MIN(r.permission) IS NULL"+
						" OR MIN(r.permission)="+PERM_DENY+
						" OR MAX(r.permission)<"+permission+
					")");

			// condition trigger
			sqlParts.where.put("NOT EXISTS ("+
					"SELECT NULL"+
					" FROM conditions cc,functions f,items i,hosts_groups hgg"+
						" LEFT JOIN rights r"+
							" ON r.tenantid = hgg.tenantid "+
						        " AND r.id=hgg.groupid"+
								" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE a.tenantid=cc.tenantid"+
						" AND cc.tenantid=f.tenantid"+
						" AND f.tenantid=i.tenantid"+
						" AND i.tenantid=hgg.tenantid"+
						" AND a.actionid=cc.actionid"+
						" AND "+rda_dbcast_2bigint("cc.value")+"=f.triggerid"+
						" AND f.itemid=i.itemid"+
						" AND i.hostid=hgg.hostid"+
						" AND cc.conditiontype="+CONDITION_TYPE_TRIGGER+
					" GROUP BY cc.value"+
					" HAVING MIN(r.permission) IS NULL"+
						" OR MIN(r.permission)="+PERM_DENY+
						" OR MAX(r.permission)<"+permission+
					")");
		}

		// actionids
		if (!is_null(params.getActionIds())) {
			sqlParts.select.put("actionid","a.actionid");
			sqlParts.where.dbConditionLong("a.actionid", params.getActionIds());
		}

		// groupids
		if (!is_null(params.getGroupIds())) {			
			sqlParts.select.put("groupids", "cg.value");
			sqlParts.from.put("conditions_groups","conditions cg");
			sqlParts.where.dbConditionLong("cg.value", params.getGroupIds());
			/* To be confirmed */
			sqlParts.where.put("ctg","cg.conditiontype="+CONDITION_TYPE_HOST_GROUP);
			sqlParts.where.put("acg.tenantid","a.tenantid=cg.tenantid");
			sqlParts.where.put("acg","a.actionid=cg.actionid");
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostids","ch.value");
			sqlParts.from.put("conditions_hosts","conditions ch");
			sqlParts.where.dbConditionLong("ch.value", params.getHostIds());
			/* To be confirmed */
			sqlParts.where.put("cth","ch.conditiontype="+CONDITION_TYPE_HOST);
			sqlParts.where.put("ach.tenantid","a.tenantid=ch.tenantid");
			sqlParts.where.put("ach","a.actionid=ch.actionid");
		}
		
		// triggerids
		if (!is_null(params.getTriggerIds())) {
			sqlParts.select.put("triggerids","ct.value");
			sqlParts.from.put("conditions_triggers","conditions ct");
			sqlParts.where.dbConditionLong("ctt.value", params.getTriggerIds());
			/* To be confirmed */
			sqlParts.where.put("ctt","ct.conditiontype="+CONDITION_TYPE_TRIGGER);
			sqlParts.where.put("act.tenantid","a.tenantid=ct.tenantid");
			sqlParts.where.put("act","a.actionid=ct.actionid");
		}
		
		// mediatypeids
		if (!is_null(params.getMediaTypeIds())) {
			sqlParts.select.put("mediatypeid","om.mediatypeid");
			sqlParts.from.put("opmessage","opmessage om");
			sqlParts.from.put("operations_media","operations omed");
			sqlParts.where.dbConditionLong("om.mediatypeid", params.getMediaTypeIds());
			sqlParts.where.put("aomed.tenantid","a.tenantid=omed.tenantid");
			sqlParts.where.put("aomed","a.actionid=omed.actionid");
			sqlParts.where.put("oom.tenantid","omed.tenantid=om.tenantid");
			sqlParts.where.put("oom","omed.operationid=om.operationid");
		}
		
		// operation messages
		// usrgrpids
		if (!is_null(params.getUsrgrpIds())) {
			sqlParts.select.put("usrgrpid","omg.usrgrpid");
			sqlParts.from.put("opmessage_grp","opmessage_grp omg");
			sqlParts.from.put("operations_usergroups","operations oug");
			sqlParts.where.dbConditionLong("omg.usrgrpid", params.getUsrgrpIds());
			sqlParts.where.put("aoug.tenantid","a.tenantid=oug.tenantid");
			sqlParts.where.put("aoug","a.actionid=oug.actionid");
			sqlParts.where.put("oomg.tenantid","oug.tenantid=omg.tenantid");
			sqlParts.where.put("oomg","oug.operationid=omg.operationid");
		}

		// userids
		if (!is_null(params.getUserIds())) {
			sqlParts.select.put("userid","omu.userid");
			sqlParts.from.put("opmessage_usr","opmessage_usr omu");
			sqlParts.from.put("operations_users","operations ou");
			sqlParts.where.dbConditionLong("omu.userid", params.getUserIds());
			sqlParts.where.put("aou.tenantid","a.tenantid=ou.tenantid");
			sqlParts.where.put("aou","a.actionid=ou.actionid");
			sqlParts.where.put("oomu.tenantid","ou.tenantid=omu.tenantid");
			sqlParts.where.put("oomu","ou.operationid=omu.operationid");
		}

		// operation commands
		// scriptids
		if (!is_null(params.getScriptIds())) {
			sqlParts.select.put("scriptid","oc.scriptid");
			sqlParts.from.put("opcommand","opcommand oc");
			sqlParts.from.put("operations_scripts","operations os");
			sqlParts.where.dbConditionLong("oc.scriptid", params.getScriptIds());
			/* To be confirmed */
			sqlParts.where.put("oc.type="+RDA_SCRIPT_TYPE_GLOBAL_SCRIPT);
			sqlParts.where.put("aos.tenantid","a.tenantid=os.tenantid");
			sqlParts.where.put("aos","a.actionid=os.actionid");
			sqlParts.where.put("ooc.tenantid","os.tenantid=oc.tenantid");
			sqlParts.where.put("ooc","os.operationid=oc.operationid");
		}
		
		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("actions a", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("actions a", params, sqlParts);
		}

		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
				
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray actionids = new CArray();
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
					ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("actionid");
				
				actionids.put(id, id);
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// mediatype
				if (isset(row.get("mediatypeid"))) {
					if(!result.get(id).containsKey("mediatypeids")){
						result.get(id).put("mediatypeids", array());
					}
					((CArray)result.get(id).get("mediatypeids")).add(row.remove("mediatypeid"));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			// check hosts, templates
			CArray hosts = array();
			CArray hostids = array();
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,och.hostid");
			sqlParts.from.put("operations o,opcommand_hst och");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=och.tenantid");
			sqlParts.where.put("o.operationid=och.operationid");
			sqlParts.where.put("och.hostid<>0");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			
			CArray<Map> dbHosts = DBselect(getSqlExecutor(), sqlParts);
			for (Map host: dbHosts) {
				if (!isset(hosts.get(host.get("hostid")))) {
					hosts.put(host.get("hostid"), array());
				}
				hosts.put(host.get("hostid"), host.get("actionid"), Nest.value(host,"actionid").$());
				hostids.put(host.get("hostid"), Nest.value(host,"hostid").$());
			}
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,ot.templateid");
			sqlParts.from.put("operations o,optemplate ot");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=ot.tenantid");
			sqlParts.where.put("o.operationid=ot.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());

			CArray<Map> dbTemplates = DBselect(getSqlExecutor(), sqlParts);
			for (Map template: dbTemplates) {
				if (!isset(hosts.get(template.get("templateid")))) {
					hosts.put(template.get("templateid"), array());
				}
				hosts.put(template.get("templateid"), template.get("actionid"), Nest.value(template,"actionid").$());
				hostids.put(template.get("templateid"), Nest.value(template,"templateid").$());
			}

			SQLExecutor executor = this.getSqlExecutor();
			
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hostids.valuesAsLong());
			hoptions.setOutput(new String[]{"hostid"});
			hoptions.setEditable(params.getEditable());
			hoptions.setTemplatedHosts(true);
			hoptions.setPreserveKeys(true);
			CArray<Map> allowedHosts = API.Host(this.idBean, executor).get(hoptions);
			for(Object hostid: hostids) {
				if (isset(allowedHosts.get(hostid))) {
					continue;
				}
				for(Object actionid: Nest.value(hosts, hostid).asCArray()) {
					unset(result, actionid);
					unset(actionids, actionid);
				}
			}
			unset(allowedHosts);

			// check hostgroups
			CArray groups = array(), groupids = array();
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,ocg.groupid");
			sqlParts.from.put("operations o,opcommand_grp ocg");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=ocg.tenantid");
			sqlParts.where.put("o.operationid=ocg.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			
			CArray<Map> dbGroups = DBselect(getSqlExecutor(), sqlParts);
			for (Map group: dbGroups) {
				if (!isset(groups.get(group.get("groupid")))) {
					groups.put(group.get("groupid"), array());
				}
				groups.put(group.get("groupid"), group.get("actionid"), Nest.value(group,"actionid").$());
				groupids.put(group.get("groupid"), Nest.value(group,"groupid").$());
			}

			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,og.groupid");
			sqlParts.from.put("operations o,opgroup og");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=og.tenantid");
			sqlParts.where.put("o.operationid=og.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			
			dbGroups = DBselect(getSqlExecutor(), sqlParts);
			for (Map group: dbGroups) {
				if (!isset(groups.get(group.get("groupid")))) {
					groups.put(group.get("groupid"), array());
				}
				groups.put(group.get("groupid"), group.get("actionid"), Nest.value(group,"actionid").$());
				groupids.put(group.get("groupid"), Nest.value(group,"groupid").$());
			}

			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(groupids.valuesAsLong());
			hgoptions.setOutput(new String[]{"groupid"});
			hgoptions.setEditable(params.getEditable());
			hgoptions.setPreserveKeys(true);
			CArray<Map> allowedGroups = API.HostGroup(this.idBean, executor).get(hgoptions);
			for(Object groupid: groupids) {
				if (isset(allowedGroups.get(groupid))) {
					continue;
				}
				for(Object actionid: Nest.value(groups, groupid).asCArray()) {
					unset(result, actionid);
					unset(actionids, actionid);
				}
			}
			unset(allowedGroups);

			// check scripts
			CArray scripts = array(), scriptids = array();
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,oc.scriptid");
			sqlParts.from.put("operations o,opcommand oc");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=oc.tenantid");
			sqlParts.where.put("o.operationid=oc.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			sqlParts.where.put("oc.type="+RDA_SCRIPT_TYPE_GLOBAL_SCRIPT);
			
			CArray<Map> dbScripts = DBselect(getSqlExecutor(), sqlParts);
			for (Map script: dbScripts) {
				if (!isset(scripts.get(script.get("scriptid")))) {
					scripts.put(script.get("scriptid"), array());
				}
				scripts.put(script.get("scriptid"), script.get("actionid"), Nest.value(script,"actionid").$());
				scriptids.put(script.get("scriptid"), Nest.value(script,"scriptid").$());
			}

			CScriptGet soptions = new CScriptGet();
			soptions.setScriptIds(scriptids.valuesAsLong());
			soptions.setOutput(new String[]{"scriptid"});
			soptions.setPreserveKeys(true);
			CArray<Map> allowedScripts = API.Script(this.idBean, executor).get(soptions);
			for(Object scriptid: scriptids) {
				if (isset(allowedScripts.get(scriptid))) {
					continue;
				}
				for(Object actionid: Nest.value(scripts, scriptid).asCArray()) {
					unset(result, actionid);
					unset(actionids, actionid);
				}
			}
			unset(allowedScripts);

			// check users
			CArray users = array(), userids = array();
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,omu.userid");
			sqlParts.from.put("operations o,opmessage_usr omu");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=omu.tenantid");
			sqlParts.where.put("o.operationid=omu.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			
			CArray<Map> dbUsers = DBselect(getSqlExecutor(), sqlParts);
			for (Map user: dbUsers) {
				if (!isset(users.get(user.get("userid")))) {
					users.put(user.get("userid"), array());
				}
				users.put(user.get("userid"), user.get("actionid"), Nest.value(user,"actionid").$());
				userids.put(user.get("userid"), Nest.value(user,"userid").$());
			}

			CUserGet uoptions = new CUserGet();
			uoptions.setUserIds(userids.valuesAsString());
			uoptions.setOutput(new String[]{"userid"});
			uoptions.setPreserveKeys(true);
			CArray<Map> allowedUsers = API.User(this.idBean, executor).get(uoptions);
			for(Object _userid: userids) {
				if (isset(allowedUsers.get(_userid))) {
					continue;
				}
				for(Object actionid: Nest.value(users, _userid).asCArray()) {
					unset(result, actionid);
					unset(actionids, actionid);
				}
			}

			// check usergroups
			CArray usrgrps = array(), usrgrpids = array();
			
			sqlParts = new SqlBuilder();
			sqlParts.select.put("o.actionid,omg.usrgrpid");
			sqlParts.from.put("operations o,opmessage_grp omg");
			applyQueryTenantOptions("operations", "o", params, sqlParts);
			sqlParts.where.put("o.tenantid=omg.tenantid");
			sqlParts.where.put("o.operationid=omg.operationid");
			sqlParts.where.dbConditionInt("o.actionid", actionids.valuesAsLong());
			
			CArray<Map> dbUsergroups = DBselect(getSqlExecutor(), sqlParts);
			for (Map usrgrp: dbUsergroups) {
				if (!isset(usrgrps.get(usrgrp.get("usrgrpid")))) {
					usrgrps.put(usrgrp.get("usrgrpid"), array());
				}
				usrgrps.put(usrgrp.get("usrgrpid"), usrgrp.get("actionid"), Nest.value(usrgrp,"actionid").$());
				usrgrpids.put(usrgrp.get("usrgrpid"), Nest.value(usrgrp,"usrgrpid").$());
			}

			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUsrgrpIds(usrgrpids.valuesAsLong());
			ugoptions.setOutput(new String[]{"usrgrpid"});
			ugoptions.setPreserveKeys(true);
			CArray<Map> allowedUsergrps = API.UserGroup(this.idBean, executor).get(ugoptions);

			for(Object usrgrpid: usrgrpids) {
				if (isset(allowedUsergrps.get(usrgrpid))) {
					continue;
				}
				for(Object actionid: Nest.value(usrgrps, usrgrpid).asCArray()) {
					unset(result, actionid);
					unset(actionids, actionid);
				}
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
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("actionid", "name"));

		CActionGet options = new CActionGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"actionid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}

	/**
	 * Add actions
	 *
	 * @param _array actions multidimensional array with actions data
	 * @param array actions[0,...]['expression']
	 * @param array actions[0,...]['description']
	 * @param array actions[0,...]['type'] OPTIONAL
	 * @param array actions[0,...]['priority'] OPTIONAL
	 * @param array actions[0,...]['status'] OPTIONAL
	 * @param array actions[0,...]['comments'] OPTIONAL
	 * @param array actions[0,...]['url'] OPTIONAL
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> actions) {
		// check fields
		CArray actionDbFields = map(
			"name", null,
			"eventsource", null,
			"evaltype", null
		);
		CArray<String> duplicates = array();
		for(Map action : actions) {
			if (!check_db_fields(actionDbFields, action)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect parameter for action \"%1$s\".", Nest.value(action,"name").$()));
			}
			if (isset(action,"esc_period") && Nest.value(action,"esc_period").asInteger() < SEC_PER_MIN && Nest.value(action,"eventsource").asInteger() == EVENT_SOURCE_TRIGGERS) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" has incorrect value for \"esc_period\" (minimum %2$s seconds).", Nest.value(action,"name").$(), SEC_PER_MIN));
			}
			if (isset(duplicates, action.get("name"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" already exists.", Nest.value(action,"name").$()));
			} else {
				Nest.value(duplicates,action.get("name")).$(Nest.value(action,"name").$());
			}
		}
		
		CActionGet params = new CActionGet();
		params.setFilter("name", duplicates.valuesAsString());
		params.setOutput(API_OUTPUT_EXTEND);
		params.setEditable(true);
		params.setNopermissions(true);
		CArray<Map> dbActions = get(params);
		for(Map dbAction : dbActions) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" already exists.", Nest.value(dbAction,"name").$()));
		}

		CArray<Long> actionids = insert("actions", Clone.deepcopy(actions));

		CArray<Map> conditions = array(); 
		CArray<Map> operations = array();
		for (Entry<Object, Map> e : actions.entrySet()) {
            Object anum = e.getKey();
            Map action = e.getValue();
			// conditions are optional, but when set, its fields must be validated
			if (isset(action,"conditions")) {
				if (!isArray(Nest.value(action,"conditions").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect action conditions for action \"%1$s\".", Nest.value(action,"name").$()));
				}

				for(Map condition : (CArray<Map>)Nest.value(action,"conditions").asCArray()) {
					if (!isset(condition,"conditiontype")) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Missing \"%1$s\" field for action condition.", "conditiontype"));
					}

					if(!isset(condition,"operator")){
						Nest.value(condition,"operator").$(CONDITION_OPERATOR_EQUAL);
					}

					Nest.value(condition,"actionid").$(Nest.value(actionids,anum).$());
					conditions.add(condition);
				}
			}

			if (!isset(action,"operations") || empty(Nest.value(action,"operations").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect parameter for action \"%1$s\".", Nest.value(action,"name").$()));
			} else {
				for(Map operation : (CArray<Map>)Nest.value(action,"operations").asCArray()) {
					Nest.value(operation,"actionid").$(Nest.value(actionids,anum).$());
					operations.add(operation);
				}
			}
		}

		validateConditions(this.idBean, this.getSqlExecutor(), Clone.deepcopy(conditions));
		addConditions(Clone.deepcopy(conditions));

		validateOperations(Clone.deepcopy(operations));
		addOperations(Clone.deepcopy(operations));

		return map("actionids", actionids);
	}
	
	/**
	 * Update actions
	 *
	 * @param _array actions multidimensional array with actions data
	 * @param array actions[0,...]['actionid']
	 * @param array actions[0,...]['expression']
	 * @param array actions[0,...]['description']
	 * @param array actions[0,...]['type'] OPTIONAL
	 * @param array actions[0,...]['priority'] OPTIONAL
	 * @param array actions[0,...]['status'] OPTIONAL
	 * @param array actions[0,...]['comments'] OPTIONAL
	 * @param array actions[0,...]['url'] OPTIONAL
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> actions) {
		CArray actionids = rda_objectValues(actions, "actionid");
		CArray<Map> update = array();

		CActionGet params = new CActionGet();
		params.setActionIds(actionids.valuesAsLong());
		params.setEditable(true);
		params.setOutput(API_OUTPUT_EXTEND);
		params.setPreserveKeys(true);
		params.setSelectOperations(API_OUTPUT_EXTEND);
		params.setSelectConditions(API_OUTPUT_EXTEND);
		CArray<Map> updActions = get(params);

		for(Map action : actions) {
			if (isset(action,"actionid") && !isset(updActions,action.get("actionid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		// check fields
		CArray<String> duplicates = array();
		for(Map action : actions) {
			String actionName = isset(action,"name") ? Nest.value(action,"name").asString() : Nest.value(updActions,action.get("actionid"),"name").asString();

			if (!check_db_fields(map("actionid", null), action)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect parameters for action update method \"%1$s\".", actionName));
			}

			// check if user changed esc_period for trigger eventsource
			if (isset(action,"esc_period")
					&& Nest.value(action,"esc_period").asInteger() < SEC_PER_MIN
					&& Nest.value(updActions,action.get("actionid"),"eventsource").asInteger() == EVENT_SOURCE_TRIGGERS) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" has incorrect value for \"esc_period\" (minimum %2$s seconds).", actionName, SEC_PER_MIN));
			}

			checkNoParameters(action, new String[]{"eventsource"}, _("Cannot update \"%1$s\" for action \"%2$s\"."), actionName);

			if (!isset(action,"name")) {
				continue;
			}

			if (isset(duplicates,action.get("name"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" already exists.", Nest.value(action,"name").$()));
			} else {
				Nest.value(duplicates,action.get("name")).$(Nest.value(action,"name").$());
			}
		}

		CArray<Map> operationsCreate = array();
		CArray<Map> operationsUpdate = array();
		CArray operationidsDelete = array();
		
		CArray<Map> conditionsCreate = array();
		CArray<Map> conditionsUpdate = array();
		CArray conditionidsDelete = array();
		
		for(Map action : actions) {
			String actionName = null;
			if (isset(action,"name")) {
				actionName  = Nest.value(action,"name").asString();

				params = new CActionGet();
				params.setFilter("name", actionName);
				params.setOutput(new String[]{"actionid"});
				params.setEditable(true);
				params.setNopermissions(true);
				params.setPreserveKeys(true);
				CArray<Map> actionExists = get(params);
				Map actionExist = reset(actionExists);
				if ((!empty(actionExist))
						&& (bccomp(Nest.value(actionExist,"actionid").$(), Nest.value(action,"actionid").$()) != 0)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" already exists.", actionName));
				}
			} else {
				actionName = Nest.value(updActions,action.get("actionid"),"name").asString();
			}

			if (isset(action,"conditions")) {
				if (!isArray(Nest.value(action,"conditions").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,_s("Incorrect action conditions for action \"%1$s\".", actionName));
				}

				CArray conditionsDb = Nest.value(updActions,action.get("actionid"),"conditions").asCArray();
				if (conditionsDb == null) {
					conditionsDb = array();
				}
				conditionsDb = rda_toHash(conditionsDb, "conditionid");

				for(Map condition : (CArray<Map>)Nest.value(action,"conditions").asCArray()) {
					Nest.value(condition,"actionid").$(Nest.value(action,"actionid").$());

					if (!isset(condition,"conditionid")) {
						conditionsCreate.add(condition);
					} else if (isset(conditionsDb,condition.get("conditionid"))) {
						// value and operator validation depends on condition type
						String[] defaultFields = new String[]{"conditiontype", "operator", "value"};
						for(String field : defaultFields) {
							Nest.value(condition,field).$(isset(condition,field)
								? Nest.value(condition,field).$()
								: Nest.value(conditionsDb,condition.get("conditionid"),field).$());
						}

						conditionsUpdate.add(condition);
						unset(conditionsDb,condition.get("conditionid"));
					} else {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action conditionid."));
					}
				}

				conditionidsDelete = array_merge(conditionidsDelete, array_keys(conditionsDb));
			}

			if (isset(action,"operations") && empty(Nest.value(action,"operations").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Action \"%1$s\" no operations defined.", actionName));
			} else if (isset(action,"operations")) {
				validateOperations(Nest.value(action,"operations").asCArray());

				CArray<Map> operationsDb = Nest.value(updActions,action.get("actionid"),"operations").asCArray();
				operationsDb = rda_toHash(operationsDb, "operationid");
				for(Map operation : (CArray<Map>)Nest.value(action,"operations").asCArray()) {
					Nest.value(operation,"actionid").$(Nest.value(action,"actionid").$());

					if (!isset(operation,"operationid")) {
						operationsCreate.add(operation);
					} else if (isset(operationsDb,operation.get("operationid"))) {
						operationsUpdate.add(operation);
						unset(operationsDb,operation.get("operationid"));
					} else {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operationid."));
					}
				}
				operationidsDelete = array_merge(operationidsDelete, array_keys(operationsDb));
			}

			Object actionid = Nest.value(action,"actionid").$();
			unset(action,"actionid");
			unset(action,"conditions");
			unset(action,"operations");
			if (!empty(action)) {
				update.add(map(
					"values", action,
					"where", map("actionid", actionid)
				));
			}
		}

		update("actions", update);

		validateConditions(this.idBean, this.getSqlExecutor(), conditionsCreate);
		addConditions(conditionsCreate);

		validateConditions(this.idBean, this.getSqlExecutor(),conditionsUpdate, true);
		updateConditions(conditionsUpdate);

		if (!empty(conditionidsDelete)) {
			deleteConditions(conditionidsDelete.valuesAsLong());
		}

		addOperations(operationsCreate);
		updateOperations(operationsUpdate, updActions);
		if (!empty(operationidsDelete)) {
			deleteOperations(operationidsDelete);
		}

		return map("actionids", actionids);
	}
	
	protected void addConditions(CArray<Map> conditions) {
		for(Map condition: conditions) {
			CArray connectionDbFields = map(
				"actionid", null,
				"conditiontype", null
			);
			if (!check_db_fields(connectionDbFields.toMap(), condition)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameters for condition."));
			}
		}
		insert("conditions", conditions);
	}

	protected void updateConditions(CArray<Map> conditions) {
		CArray update = array();
		for(Map condition: conditions) {
			Object conditionid = condition.remove("conditionid");
			update.add(map(
				"values", condition,
				"where", map("conditionid", conditionid)
			));
		}
		update("conditions", update);
	}

	protected void deleteConditions(Long[] conditionids) {
		delete("conditions", (Map)map("conditionid", conditionids));
	}

	protected boolean addOperations(CArray<Map> operations) {
		for (Map operation: operations) {
			CArray operationDbFields = map(
				"actionid", null,
				"operationtype", null
			);
			if (!check_db_fields(operationDbFields.toMap(), operation)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameter for operations."));
			}
		}
	
		CArray<Long> operationids = insert("operations", Clone.deepcopy(operations));
	
		CArray<Map> opmessage = array();
		CArray<Map> opmessageUsr = array();
		CArray<Map> opmessageGrp = array();
		CArray<Map> opcommand = array();
		CArray<Map> opcommandHst = array();
		CArray<Map> opcommandGrp = array();
		CArray<Map> opgroup = array();
		CArray<Map> optemplate = array();
		
		CArray<Map> opconditionInserts = array();
		for(Entry<Object, Map> e: operations.entrySet()) {
			Object onum = e.getKey();
			Map operation = e.getValue();
			
			Long operationid = Nest.value(operationids,onum).asLong();
			
			switch (Nest.value(operation,"operationtype").asInteger()) {
				case OPERATION_TYPE_MESSAGE:
					Map n_opmessage = Nest.value(operation,"opmessage").asCArray();
					if (isset(n_opmessage) && !empty(n_opmessage)) {
						n_opmessage.put("operationid", operationid);
						opmessage.add(n_opmessage);
					}
					
					CArray<Map> n_opmessage_usr = (CArray<Map>)operation.get("opmessage_usr");
					if (isset(n_opmessage_usr)) {
						for(Map user: n_opmessage_usr) {
							opmessageUsr.add(map(
								"operationid", operationid,
								"userid", user.get("userid")
							));
						}
					}
					
					CArray<Map> n_opmessage_grp = (CArray<Map>)operation.get("opmessage_grp");
					if (isset(n_opmessage_grp)) {
						for(Map usrgrp: n_opmessage_grp) {
							opmessageGrp.add(map(
								"operationid", operationid,
								"usrgrpid", usrgrp.get("usrgrpid")
							));
						}
					}
					break;
				case OPERATION_TYPE_COMMAND:
					Map n_opcommand = Nest.value(operation,"opcommand").asCArray();
					if (isset(n_opcommand) && !empty(n_opcommand)) {
						n_opcommand.put("operationid", operationid);
						opcommand.add(n_opcommand);
					}
					
					CArray<Map> n_opcommand_hst = (CArray<Map>)operation.get("opcommand_hst");
					if (isset(n_opcommand_hst)) {
						for(Map hst: n_opcommand_hst) {
							opcommandHst.add(map(
								"operationid", operationid,
								"hostid", hst.get("hostid")
							));
						}
					}
					
					CArray<Map> n_opcommand_grp = (CArray<Map>)operation.get("opcommand_grp");
					if (isset(n_opcommand_grp)) {
						for(Map grp: n_opcommand_grp) {
							opcommandGrp.add(map(
								"operationid", operationid,
								"groupid", grp.get("groupid")
							));
						}
					}
					break;
				case OPERATION_TYPE_GROUP_ADD:
				case OPERATION_TYPE_GROUP_REMOVE:
					CArray<Map> n_opgroup = (CArray<Map>)Nest.value(operation,"opgroup").asCArray();
					for(Map grp: n_opgroup) {
						opgroup.add(map(
							"operationid",  operationid,
							"groupid",  grp.get("groupid")
						));
					}
					break;
				case OPERATION_TYPE_TEMPLATE_ADD:
				case OPERATION_TYPE_TEMPLATE_REMOVE:
					CArray<Map> n_optemplate = (CArray<Map>)Nest.value(operation,"optemplate").asCArray();
					for(Map tpl: n_optemplate) {
						optemplate.add(map(
							"operationid",  operationid,
							"templateid",  tpl.get("templateid")
						));
					}
					break;
				case OPERATION_TYPE_HOST_ADD:
				case OPERATION_TYPE_HOST_REMOVE:
				case OPERATION_TYPE_HOST_ENABLE:
				case OPERATION_TYPE_HOST_DISABLE:
			}
			
			CArray<Map> n_opconditions = (CArray<Map>)operation.get("opconditions");
			if (isset(n_opconditions)) {
				for(Map opcondition: n_opconditions) {
					opcondition.put("operationid", operationid);
					opconditionInserts.add(opcondition);
				}
			}
		}
		
		this.insert("opconditions", opconditionInserts);
		this.insert("opmessage", opmessage, false);
		this.insert("opcommand", opcommand, false);
		this.insert("opmessage_grp", opmessageGrp);
		this.insert("opmessage_usr", opmessageUsr);
		this.insert("opcommand_hst", opcommandHst);
		this.insert("opcommand_grp", opcommandGrp);
		this.insert("opgroup", opgroup);
		this.insert("optemplate", optemplate);
	
		return true;
	}

	protected void updateOperations(CArray<Map> operations, CArray actionsDb) {
		CArray<Map> operationsUpdate = array();
	
		// messages
		CArray opmessageCreate = array();
		CArray opmessageUpdate = array();
		CArray opmessageDeleteByOpId = array();
	
		CArray opmessageGrpCreate = array();
		CArray opmessageUsrCreate = array();
		CArray opmessageGrpDeleteByOpId = array();
		CArray opmessageUsrDeleteByOpId = array();
	
		// commands
		CArray opcommandCreate = array();
		CArray opcommandUpdate = array();
		CArray opcommandDeleteByOpId = array();
	
		CArray opcommandGrpCreate = array();
		CArray opcommandHstCreate = array();
	
		CArray opcommandGrpDeleteByOpId = array();
		CArray opcommandHstDeleteByOpId = array();
	
		// groups
		CArray opgroupCreate = array();
		CArray opgroupDeleteByOpId = array();
	
		// templates
		CArray optemplateCreate = array();
		CArray optemplateDeleteByOpId = array();
	
		CArray opconditionsCreate = array();
	
		for(Map operation: operations) {
			CArray operationsDb = rda_toHash(actionsDb.getNested(operation.get("actionid"), "operations"), "operationid");
			Map operationDb = (Map)operationsDb.get(operation.get("operationid"));
	
			boolean typeChanged = false;
			Integer operationtype = asInteger(operation.get("operationtype"));
			Integer operationtypeDB = asInteger(operationDb.get("operationtype"));
			if (isset(operationtype) && (operationtype != operationtypeDB)) {
				typeChanged = true;
	
				switch (operationtypeDB) {
					case OPERATION_TYPE_MESSAGE:
						opmessageDeleteByOpId.add(operationDb.get("operationid"));
						opmessageGrpDeleteByOpId.add(operationDb.get("operationid"));
						opmessageUsrDeleteByOpId.add(operationDb.get("operationid"));
						break;
					case OPERATION_TYPE_COMMAND:
						opcommandDeleteByOpId.add(operationDb.get("operationid"));
						opcommandHstDeleteByOpId.add(operationDb.get("operationid"));
						opcommandGrpDeleteByOpId.add(operationDb.get("operationid"));
						break;
					case OPERATION_TYPE_GROUP_ADD:
						if (operationtype == OPERATION_TYPE_GROUP_REMOVE) {
							break;
						}
					case OPERATION_TYPE_GROUP_REMOVE:
						if (operationtype == OPERATION_TYPE_GROUP_ADD) {
							break;
						}
						opgroupDeleteByOpId.add(operationDb.get("operationid"));
						break;
					case OPERATION_TYPE_TEMPLATE_ADD:
						if (operationtype == OPERATION_TYPE_TEMPLATE_REMOVE) {
							break;
						}
					case OPERATION_TYPE_TEMPLATE_REMOVE:
						if (operationtype == OPERATION_TYPE_TEMPLATE_ADD) {
							break;
						}
						optemplateDeleteByOpId.add(operationDb.get("operationid"));
						break;
				}
			}
	
			if (!isset(operationtype)) {
				operationtype = (Integer)operation.put("operationtype", operationtypeDB);
			}
	
			switch (operationtype) {
				case OPERATION_TYPE_MESSAGE:
					if (!isset(operation.get("opmessage_grp"))) {
						operation.put("opmessage_grp", array());
					} else {
						rda_array_push((CArray)operation.get("opmessage_grp"), (CArray)map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operation.get("opmessage_usr"))) {
						operation.put("opmessage_usr", array());
					} else {
						rda_array_push((CArray)operation.get("opmessage_usr"), map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operationDb.get("opmessage_usr"))) {
						operationDb.put("opmessage_usr", array());
					}
					if (!isset(operationDb.get("opmessage_grp"))) {
						operationDb.put("opmessage_grp", array());
					}
	
					if (typeChanged) {
						MixedUtil.rmapSet(operation, "opmessage", "operationid", operation.get("operationid"));
						opmessageCreate.add(operation.get("opmessage"));
	
						opmessageGrpCreate = array_merge(opmessageGrpCreate, (CArray)operation.get("opmessage_grp"));
						opmessageUsrCreate = array_merge(opmessageUsrCreate, (CArray)operation.get("opmessage_usr"));
					}
					else {
						opmessageUpdate.add( map(
							"values", operation.get("opmessage"),
							"where", map("operationid", operation.get("operationid"))
						));
	
						CArray<CArray<Map>> diff = rda_array_diff((CArray)operation.get("opmessage_grp"), (CArray)operationDb.get("opmessage_grp"), "usrgrpid");
						opmessageGrpCreate = array_merge(opmessageGrpCreate, diff.get("first"));
	
						for (Map omgrp: diff.get("second")) {
							this.delete("opmessage_grp", map(
								"usrgrpid", omgrp.get("usrgrpid"),
								"operationid", operation.get("operationid")
							).toStrKeyMap());
						}
	
						diff = rda_array_diff((CArray)operation.get("opmessage_usr"), (CArray)operationDb.get("opmessage_usr"), "userid");
						opmessageUsrCreate = array_merge(opmessageUsrCreate, diff.get("first"));
						for(Map omusr: diff.get("second")) {
							this.delete("opmessage_usr", map(
								"userid", omusr.get("userid"),
								"operationid", operation.get("operationid")
							).toStrKeyMap());
						}
					}
					break;
				case OPERATION_TYPE_COMMAND:
					if (!isset(operation.get("opcommand_grp"))) {
						operation.put("opcommand_grp", array());
					} else {
						rda_array_push((CArray)operation.get("opcommand_grp"), map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operation.get("opcommand_hst"))) {
						operation.put("opcommand_hst", array());
					} else {
						rda_array_push((CArray)operation.get("opcommand_hst"), map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operationDb.get("opcommand_grp"))) {
						operationDb.put("opcommand_grp", array());
					}
					if (!isset(operationDb.get("opcommand_hst"))) {
						operationDb.put("opcommand_hst", array());
					}
	
					if (typeChanged) {
						MixedUtil.rmapSet(operation,"opcommand", "operationid", operation.get("operationid"));
						opcommandCreate.add(operation.get("opcommand"));
	
						opcommandGrpCreate = array_merge((CArray)opcommandGrpCreate, (CArray)operation.get("opcommand_grp"));
						opcommandHstCreate = array_merge((CArray)opcommandHstCreate, (CArray)operation.get("opcommand_hst"));
					} else {
						// clear and reset fields to default values on type change
						if (Nest.value(operation,"opcommand", "type").asInteger() == RDA_SCRIPT_TYPE_GLOBAL_SCRIPT) {
							MixedUtil.rmapSet(operation,"opcommand", "command", "");
						} else {
							MixedUtil.rmapSet(operation,"opcommand", "scriptid", null);
						}
						if (Nest.value(operation,"opcommand", "type").asInteger() != RDA_SCRIPT_TYPE_CUSTOM_SCRIPT) {
							MixedUtil.rmapSet(operation,"opcommand", "execute_on", RDA_SCRIPT_EXECUTE_ON_AGENT);
						}
						if (Nest.value(operation,"opcommand", "type").asInteger()  != RDA_SCRIPT_TYPE_SSH
								&& Nest.value(operation,"opcommand", "type").asInteger()  != RDA_SCRIPT_TYPE_TELNET) {
							MixedUtil.rmapSet(operation,"opcommand", "port", "");
							MixedUtil.rmapSet(operation,"opcommand", "username", "");
							MixedUtil.rmapSet(operation,"opcommand", "password", "");
						}
						if (!isset(Nest.value(operation,"opcommand", "authtype").$())) {
							MixedUtil.rmapSet(operation,"opcommand", "authtype", ITEM_AUTHTYPE_PASSWORD);
						}
						if (Nest.value(operation,"opcommand", "authtype").asInteger() == ITEM_AUTHTYPE_PASSWORD) {
							MixedUtil.rmapSet(operation,"opcommand", "publickey", "");
							MixedUtil.rmapSet(operation,"opcommand", "privatekey", "");
						}
	
						opcommandUpdate.add( map(
							"values", operation.get("opcommand"),
							"where", map("operationid", operation.get("operationid"))
						));
	
						CArray<CArray<Map>> diff = rda_array_diff((CArray)operation.get("opcommand_grp"), (CArray)operationDb.get("opcommand_grp"), "groupid");
						opcommandGrpCreate = array_merge(opcommandGrpCreate, diff.get("first"));
						for (Map omgrp: diff.get("second")) {
							this.delete("opcommand_grp", map(
								"groupid", omgrp.get("groupid"),
								"operationid", operation.get("operationid")
							).toStrKeyMap());
						}
	
						diff = rda_array_diff((CArray)operation.get("opcommand_hst"), (CArray)operationDb.get("opcommand_hst"), "hostid");
						opcommandHstCreate = array_merge(opcommandHstCreate, diff.get("first"));
						CArray opCommandHostIds = rda_objectValues(diff.get("second"), "opcommand_hstid");
						if (isset(opCommandHostIds)) {
							this.delete("opcommand_hst", map(
								"opcommand_hstid", opCommandHostIds
							).toStrKeyMap());
						}
					}
					break;
				case OPERATION_TYPE_GROUP_ADD:
				case OPERATION_TYPE_GROUP_REMOVE:
					if (!isset(operation.get("opgroup"))) {
						operation.put("opgroup", array());
					} else {
						rda_array_push((CArray)operation.get("opgroup"), map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operationDb.get("opgroup"))) {
						operationDb.put("opgroup", array());
					}
					
					CArray<CArray<Map>> diff = rda_array_diff((CArray)operation.get("opgroup"), (CArray)operationDb.get("opgroup"), "groupid");
					opgroupCreate = array_merge(opgroupCreate, diff.get("first"));
					for (Map ogrp: diff.get("second")) {
						this.delete("opgroup", map(
							"groupid", ogrp.get("groupid"),
							"operationid", operation.get("operationid")
						).toStrKeyMap());
					}
					break;
				case OPERATION_TYPE_TEMPLATE_ADD:
				case OPERATION_TYPE_TEMPLATE_REMOVE:
					if (!isset(operation.get("optemplate"))) {
						operation.put("optemplate", array());
					} else {
						rda_array_push((CArray)operation.get("optemplate"), map("operationid", operation.get("operationid")));
					}
	
					if (!isset(operationDb.get("optemplate"))) {
						operationDb.put("optemplate", array());
					}
					
					diff = rda_array_diff((CArray)operation.get("optemplate"), (CArray)operationDb.get("optemplate"), "templateid");
					optemplateCreate = array_merge(optemplateCreate, diff.get("first"));
					for (Map otpl: diff.get("second")) {
						this.delete("optemplate", map(
							"templateid", otpl.get("templateid"),
							"operationid", operation.get("operationid")
						).toStrKeyMap());
					}
					break;
			}
	
			if (!isset(operation.get("opconditions"))) {
				operation.put("opconditions", array());
			} else {
				rda_array_push((CArray)operation.get("opconditions"), map("operationid", operation.get("operationid")));
			}
	
			validateOperationConditions(Nest.value(operation,"opconditions").asCArray());
	
			CArray<CArray<Map>> diff = rda_array_diff((CArray)operation.get("opconditions"), (CArray)operationDb.get("opconditions"), "opconditionid");
			opconditionsCreate = array_merge(opconditionsCreate, diff.get("first"));
	
			CArray opconditionsidDelete = rda_objectValues(diff.get("second"), "opconditionid");
			if (!empty(opconditionsidDelete)) {
				this.delete("opconditions", (CArray)map("opconditionid", opconditionsidDelete));
			}
	
			Object operationid = operation.remove("operationid");
			if (!empty(operation)) {
				operationsUpdate.add(map(
					"values", operation,
					"where", map("operationid", operationid)
				));
			}
		}
	
		this.update("operations", operationsUpdate);
	
		if (!empty(opmessageDeleteByOpId)) {
			this.delete("opmessage", (CArray)map("operationid", opmessageDeleteByOpId));
		}
		if (!empty(opcommandDeleteByOpId)) {
			this.delete("opcommand", (CArray)map("operationid", opcommandDeleteByOpId));
		}
		if (!empty(opmessageGrpDeleteByOpId)) {
			this.delete("opmessage_grp", (CArray)map("operationid", opmessageGrpDeleteByOpId));
		}
		if (!empty(opmessageUsrDeleteByOpId)) {
			this.delete("opmessage_usr", (CArray)map("operationid", opmessageUsrDeleteByOpId));
		}
		if (!empty(opcommandHstDeleteByOpId)) {
			this.delete("opcommand_hst", (CArray)map("operationid", opcommandHstDeleteByOpId));
		}
		if (!empty(opcommandGrpDeleteByOpId)) {
			this.delete("opcommand_grp", (CArray)map("operationid", opcommandGrpDeleteByOpId));
		}
		if (!empty(opcommandGrpDeleteByOpId)) {
			this.delete("opcommand_grp", (CArray)map("opcommand_grpid", opcommandGrpDeleteByOpId));
		}
		if (!empty(opcommandHstDeleteByOpId)) {
			this.delete("opcommand_hst", (CArray)map("opcommand_hstid", opcommandHstDeleteByOpId));
		}
		if (!empty(opgroupDeleteByOpId)) {
			this.delete("opgroup", (CArray)map("operationid", opgroupDeleteByOpId));
		}
		if (!empty(optemplateDeleteByOpId)) {
			this.delete("optemplate", (CArray)map("operationid", optemplateDeleteByOpId));
		}
	
		this.insert("opmessage", opmessageCreate, false);
		this.insert("opcommand", opcommandCreate, false);
		this.insert("opmessage_grp", opmessageGrpCreate);
		this.insert("opmessage_usr", opmessageUsrCreate);
		this.insert("opcommand_grp", opcommandGrpCreate);
		this.insert("opcommand_hst", opcommandHstCreate);
		this.insert("opgroup", opgroupCreate);
		this.insert("optemplate", optemplateCreate);
		this.update("opmessage", opmessageUpdate);
		this.update("opcommand", opcommandUpdate);
		this.insert("opconditions", opconditionsCreate);
	}

	protected void deleteOperations(CArray<Map> operationids) {
		this.delete("operations", (CArray)map("operationid", operationids));
	}

	@Override
	public CArray<Long[]> delete(Long... actionids) {
		if (empty(actionids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
		
		CActionGet params = new CActionGet();
		params.setActionIds(TArray.as(actionids).asLong());
		params.setEditable(true);
		params.setOutput(new String[]{"actionid"});
		params.setPreserveKeys(true);
		CArray<Map> delActions = get(params);
		for(Long actionid : actionids) {
			if (isset(delActions,actionid)) {
				continue;
			}
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		delete("actions", (Map)map("actionid", actionids));
		delete("alerts", (Map)map("actionid", actionids));

		return map("actionids", actionids);
	}

	public boolean validateOperations(CArray<Map> operations) {
		CArray hostIdsAll = array();
		CArray hostGroupIdsAll = array();
		CArray useridsAll = array();
		CArray userGroupidsAll = array();
		CArray userids = null;
		CArray usergroupids = null;
		CArray templateids = null;
		CArray<Map> scripts = null;
		for(Map operation : operations) {
			if ((isset(operation,"esc_step_from") || isset(operation,"esc_step_to")) && !(isset(operation,"esc_step_from") && isset(operation,"esc_step_to"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("esc_step_from and esc_step_to must be set together."));
			}

			if (isset(operation,"esc_step_from") && isset(operation,"esc_step_to")) {
				if (Nest.value(operation,"esc_step_from").asLong() < 1 || Nest.value(operation,"esc_step_to").asLong() < 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation escalation step values."));
				}

				if (Nest.value(operation,"esc_step_from").asLong() > Nest.value(operation,"esc_step_to").asLong() && Nest.value(operation,"esc_step_to").asLong() != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation escalation step values."));
				}
			}

			if (isset(operation,"esc_period")) {
				if (isset(operation,"esc_period") && Nest.value(operation,"esc_period").asLong() != 0 && Nest.value(operation,"esc_period").asLong() < SEC_PER_MIN) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation step duration."));
				}
			}

			switch (Nest.value(operation,"operationtype").asInteger()) {
				case OPERATION_TYPE_MESSAGE:
					userids = isset(operation,"opmessage_usr") ? rda_objectValues(Nest.value(operation,"opmessage_usr").$(), "userid") : array();
					usergroupids = isset(operation,"opmessage_grp") ? rda_objectValues(Nest.value(operation,"opmessage_grp").$(), "usrgrpid") : array();

					if (empty(userids) && empty(usergroupids)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No recipients for action operation message."));
					}

					useridsAll = array_merge(useridsAll, userids);
					userGroupidsAll = array_merge(userGroupidsAll, usergroupids);
					break;
				case OPERATION_TYPE_COMMAND:
					if (!isset(Nest.value(operation,"opcommand","type").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No command type specified for action operation."));
					}

					if ((!isset(Nest.value(operation,"opcommand","command").$()) || rda_empty(trim(Nest.value(operation,"opcommand","command").asString())))
							&& Nest.value(operation,"opcommand","type").asInteger() != RDA_SCRIPT_TYPE_GLOBAL_SCRIPT) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No command specified for action operation."));
					}

					switch (Nest.value(operation,"opcommand","type").asInteger()) {
						case RDA_SCRIPT_TYPE_IPMI:
							break;
						case RDA_SCRIPT_TYPE_CUSTOM_SCRIPT:
							if (!isset(Nest.value(operation,"opcommand","execute_on").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No execution target specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
							}
							break;
						case RDA_SCRIPT_TYPE_SSH:
							if (!isset(Nest.value(operation,"opcommand","authtype").$()) || rda_empty(Nest.value(operation,"opcommand","authtype").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No authentication type specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
							}

							if (!isset(Nest.value(operation,"opcommand","username").$()) || rda_empty(Nest.value(operation,"opcommand","username").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No authentication user name specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
							}

							if (Nest.value(operation,"opcommand","authtype").asInteger() == ITEM_AUTHTYPE_PUBLICKEY) {
								if (!isset(Nest.value(operation,"opcommand","publickey").$()) || rda_empty(Nest.value(operation,"opcommand","publickey").$())) {
									throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No public key file specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
								}
								if (!isset(Nest.value(operation,"opcommand","privatekey").$()) || rda_empty(Nest.value(operation,"opcommand","privatekey").$())) {
									throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No private key file specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
								}
							}
							break;
						case RDA_SCRIPT_TYPE_TELNET:
							if (!isset(Nest.value(operation,"opcommand","username").$()) || rda_empty(Nest.value(operation,"opcommand","username").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No authentication user name specified for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
							}
							break;
						case RDA_SCRIPT_TYPE_GLOBAL_SCRIPT:
							if (!isset(Nest.value(operation,"opcommand","scriptid").$()) || rda_empty(Nest.value(operation,"opcommand","scriptid").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No script specified for action operation command."));
							}
							CScriptGet soptions = new CScriptGet();
							soptions.setOutput(new String[]{"scriptid","name"});
							soptions.setScriptIds(Nest.value(operation,"opcommand","scriptid").asLong());
							soptions.setPreserveKeys(true);
							scripts = API.Script(this.idBean, this.getSqlExecutor()).get(soptions);
							if (!isset(scripts,Nest.value(operation,"opcommand","scriptid").$())) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Specified script does not exist or you do not have rights on it for action operation command."));
							}
							break;
						default:
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation command type."));
					}

					if (isset(Nest.value(operation,"opcommand","port").$()) && !rda_empty(Nest.value(operation,"opcommand","port").$())) {
						if (rda_ctype_digit(Nest.value(operation,"opcommand","port").$())) {
							if (Nest.value(operation,"opcommand","port").asInteger() > 65535 || Nest.value(operation,"opcommand","port").asInteger() < 1) {
								throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect action operation port \"%s\".", Nest.value(operation,"opcommand","port").$()));
							}
						} else if (preg_match("^"+RDA_PREG_EXPRESSION_USER_MACROS+"$", Nest.value(operation,"opcommand","port").asString())==0) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect action operation port \"%s\".", Nest.value(operation,"opcommand","port").$()));
						}
					}

					CArray groupids = array();
					if (isset(operation,"opcommand_grp")) {
						groupids = rda_objectValues(Nest.value(operation,"opcommand_grp").$(), "groupid");
					}

					CArray hostids = array();
					boolean withoutCurrent = true;
					if (isset(operation,"opcommand_hst")) {
						for(Map hstCommand : (CArray<Map>)Nest.value(operation,"opcommand_hst").asCArray()) {
							if (Nest.value(hstCommand,"hostid").asLong() == 0) {
								withoutCurrent = false;
							} else {
								Nest.value(hostids,hstCommand.get("hostid")).$(Nest.value(hstCommand,"hostid").$());
							}
						}
					}

					if (empty(groupids) && empty(hostids) && withoutCurrent) {
						if (Nest.value(operation,"opcommand","type").asInteger() == RDA_SCRIPT_TYPE_GLOBAL_SCRIPT) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("You did not specify targets for action operation global script \"%s\".", Nest.value(scripts,Nest.value(operation,"opcommand","scriptid").$(),"name").$()));
						} else {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("You did not specify targets for action operation command \"%s\".", Nest.value(operation,"opcommand","command").$()));
						}
					}

					hostIdsAll = array_merge(hostIdsAll, hostids);
					hostGroupIdsAll = array_merge(hostGroupIdsAll, groupids);
					break;
				case OPERATION_TYPE_GROUP_ADD:
				case OPERATION_TYPE_GROUP_REMOVE:
					groupids = isset(operation,"opgroup") ? rda_objectValues(Nest.value(operation,"opgroup").$(), "groupid") : array();
					if (empty(groupids)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Operation has no group to operate."));
					}
					hostGroupIdsAll = array_merge(hostGroupIdsAll, groupids);
					break;
				case OPERATION_TYPE_TEMPLATE_ADD:
				case OPERATION_TYPE_TEMPLATE_REMOVE:
					templateids = isset(operation,"optemplate") ? rda_objectValues(Nest.value(operation,"optemplate").$(), "templateid") : array();
					if (empty(templateids)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Operation has no template to operate."));
					}
					hostIdsAll = array_merge(hostIdsAll, templateids);
					break;
				case OPERATION_TYPE_HOST_ADD:
				case OPERATION_TYPE_HOST_REMOVE:
				case OPERATION_TYPE_HOST_ENABLE:
				case OPERATION_TYPE_HOST_DISABLE:
					break;
				default:
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation type."));
			}
		}

		if (!API.HostGroup(this.idBean, this.getSqlExecutor()).isWritable(hostGroupIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation host group. Host group does not exist or you have no access to this host group."));
		}
		if (!API.Host(this.idBean, this.getSqlExecutor()).isWritable(hostIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation host. Host does not exist or you have no access to this host."));
		}
		if (!API.User(this.idBean, this.getSqlExecutor()).isReadable(useridsAll.valuesAsString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation user. User does not exist or you have no access to this user."));
		}
		if (!API.UserGroup(this.idBean, this.getSqlExecutor()).isReadable(userGroupidsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation user group. User group does not exist or you have no access to this user group."));
		}

		return true;
	}
	
	public static boolean validateConditions(IIdentityBean idBean, SQLExecutor executor, CArray<Map> conditions) {
		return validateConditions(idBean, executor, conditions, false);
	}
	/**
	 * Validate conditions.
	 *
	 * @static
	 *
	 * @param array conditions
	 * @param int   conditions['conditiontype']
	 * @param array conditions['value']
	 *
	 * @return bool
	 */
	public static boolean validateConditions(IIdentityBean idBean, SQLExecutor executor, CArray<Map> conditions, boolean update) {
		CArray hostGroupIdsAll = array();
		CArray templateIdsAll = array();
		CArray triggerIdsAll = array();
		CArray hostIdsAll = array();
		CArray discoveryRuleIdsAll = array();
		CArray proxyIdsAll = array();
		CArray proxyidsAll = array();

		// build validators
		CTimePeriodValidator timePeriodValidator = CValidator.init(new CTimePeriodValidator(),map());
		CSetValidator discoveryCheckTypeValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(discovery_check_type2str())
		));
		CSetValidator discoveryObjectStatusValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(discovery_object_status2str())
		));
		CSetValidator triggerSeverityValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(getSeverityCaption(idBean, executor))
		));
		CSetValidator discoveryObjectValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(discovery_object2str())
		));
		CSetValidator triggerValueValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(trigger_value2str())
		));
		CSetValidator eventTypeValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(eventType())
		));

		for(Map condition : conditions) {
			// on create operator is mandatory and needs validation, but on update it must be validated only if it's set
			if (!update || (update && isset(condition,"operator"))) {
				CSetValidator operatorValidator = CValidator.init(new CSetValidator(),map(
					"values", get_operators_by_conditiontype(Nest.value(condition,"conditiontype").asInteger())
				));
				if (!operatorValidator.validate(idBean, Nest.value(condition,"operator").asString())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition operator."));
				}
			}

			if (!update || (update && isset(condition,"value"))) {
				// validate condition values depending on condition type
				switch (Nest.value(condition,"conditiontype").asInteger()) {
					case CONDITION_TYPE_HOST_GROUP:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(hostGroupIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_TEMPLATE:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(templateIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_TRIGGER:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(triggerIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_HOST:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(hostIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_DRULE:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(discoveryRuleIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_DCHECK:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(proxyIdsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_PROXY:
						if (empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						Nest.value(proxyidsAll,condition.get("value")).$(Nest.value(condition,"value").$());
						break;

					case CONDITION_TYPE_DOBJECT:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!discoveryObjectValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_("Incorrect action condition discovery object."));
						}
						break;

					case CONDITION_TYPE_TIME_PERIOD:
						if (!timePeriodValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, timePeriodValidator.getError());
						}
						break;

					case CONDITION_TYPE_DHOST_IP:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!validate_ip_range(Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_s("Incorrect action condition ip \"%1$s\".", Nest.value(condition,"value").$()));
						}
						break;

					case CONDITION_TYPE_DSERVICE_TYPE:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!discoveryCheckTypeValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition discovery check."));
						}
						break;

					case CONDITION_TYPE_DSERVICE_PORT:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!validate_port_list(Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_s("Incorrect action condition port \"%1$s\".", Nest.value(condition,"value").$()));
						}
						break;

					case CONDITION_TYPE_DSTATUS:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!discoveryObjectStatusValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_("Incorrect action condition discovery status."));
						}
						break;

					case CONDITION_TYPE_MAINTENANCE:
						if (!rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Maintenance action condition value must be empty."));
						}
						break;

					case CONDITION_TYPE_TRIGGER_SEVERITY:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!triggerSeverityValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_("Incorrect action condition trigger severity."));
						}
						break;

					case CONDITION_TYPE_TRIGGER_VALUE:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!triggerValueValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition trigger value."));
						}
						break;

					case CONDITION_TYPE_EVENT_TYPE:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						} else if (!eventTypeValidator.validate(idBean, Nest.value(condition,"value").asString())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition event type."));
						}
					break;

					case CONDITION_TYPE_TRIGGER_NAME:
					case CONDITION_TYPE_DUPTIME:
					case CONDITION_TYPE_DVALUE:
					case CONDITION_TYPE_APPLICATION:
					case CONDITION_TYPE_HOST_NAME:
					case CONDITION_TYPE_HOST_METADATA:
						if (rda_empty(Nest.value(condition,"value").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty action condition."));
						}
						break;

					default:
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition type."));
				}
			}
		}

		if (!API.HostGroup(idBean, executor).isWritable(hostGroupIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition host group. Host group does not exist or you have no access to it."));
		}
		if (!API.Host(idBean, executor).isWritable(hostIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition host. Host does not exist or you have no access to it."));
		}
		if (!API.Template(idBean, executor).isWritable(templateIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition template. Template does not exist or you have no access to it."));
		}
		if (!API.Trigger(idBean, executor).isWritable(triggerIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition trigger. Trigger does not exist or you have no access to it."));
		}
		if (!API.DRule(idBean, executor).isWritable(discoveryRuleIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition discovery rule. Discovery rule does not exist or you have no access to it."));
		}
		if (!API.DCheck(idBean, executor).isWritable(proxyIdsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition discovery check. Discovery check does not exist or you have no access to it."));
		}
		if (!API.Proxy(idBean, executor).isWritable(proxyidsAll.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action condition proxy. Proxy does not exist or you have no access to it."));
		}

		return true;
	}
	
	
	/**
		 * Validate operation conditions.
		 *
		 * @static
		 * @param conditions
		 * @return bool
		 */
		public static boolean validateOperationConditions(CArray<Map> conditions) {
			CArray ackStatuses = map(
				EVENT_ACKNOWLEDGED, 1,
				EVENT_NOT_ACKNOWLEDGED, 1
			);
	
			for(Map condition: conditions) {
				switch (Nest.value(condition,"conditiontype").asInteger()) {
					case CONDITION_TYPE_EVENT_ACKNOWLEDGED:
						if (!isset(ackStatuses,Nest.value(condition,"value").asInteger())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation condition acknowledge type."));
						}
						break;
	
					default:
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect action operation condition type."));
				}
			}
			return true;
		}

	@SuppressWarnings("unchecked")
	@Override
	protected void addRelatedObjects(CActionGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] actionIds = result.keysAsLong();
		// adding conditions
		if (!is_null(params.getSelectConditions()) && !API_OUTPUT_COUNT.equals(params.getSelectConditions())) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("conditions", new String[] { "actionid", "conditionid" }, params.getSelectConditions()));
			options.setFilter("actionid", actionIds);
			options.setPreserveKeys(true);
			
			CArray<Map> conditions = select("conditions", options);
			CRelationMap relationMap = createRelationMap(conditions, "actionid", "conditionid");
			unsetExtraFields(conditions, new String[] { "actionid", "conditionid" }, params.getSelectConditions());
			relationMap.mapMany(result, conditions, "conditions", params.getLimitSelects());
		}
		
		// adding operations
		if (!is_null(params.getSelectOperations())
				&& !API_OUTPUT_COUNT.equals(params.getSelectOperations())) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("operations", new String[] { "operationid", "actionid", "operationtype" }, params.getSelectOperations()));
			Map<String, Object[]> filter = new LinkedMap();
			filter.put("actionid", actionIds);
			options.setFilter(filter);
			options.setPreserveKeys(true);
			
			CArray<Map> operations = select("operations", options);
			CRelationMap relationMap = createRelationMap(operations, "actionid", "operationid");
			Long[] operationIds = relationMap.getRelatedLongIds();
			
			if (outputIsRequested("opconditions", params.getSelectOperations())) {
				for(Map operation: operations){
					operation.put("opconditions", array());
				}
				
				SqlBuilder sqlParts = new SqlBuilder();		
				sqlParts.select.put("op.*");
				sqlParts.from.put("opconditions op");
				applyQueryTenantOptions("opconditions", "op", options, sqlParts);
				sqlParts.where.dbConditionInt("op.operationid", operationIds);
				
				CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
				for(Map opcondition : datas){
					Nest.value(operations, opcondition.get("operationid"),"opconditions").asCArray().add(opcondition);
				}
			}
			
			CArray<Long> opmessage = new CArray<Long>();
			CArray<Long> opcommand = new CArray<Long>();
			CArray<Long> opgroup = new CArray<Long>();
			CArray<Long> optemplate = new CArray<Long>();
			for(Entry<Object, Map> e: operations.entrySet()){
				Long operationid = Nest.as(e.getKey()).asLong();
				Map operation = e.getValue();
				int operationtype = (Integer)operation.get("operationtype");
				switch (operationtype) {
				case OPERATION_TYPE_MESSAGE:
					opmessage.add(operationid);
					break;
				case OPERATION_TYPE_COMMAND:
					opcommand.add(operationid);
					break;
				case OPERATION_TYPE_GROUP_ADD:
				case OPERATION_TYPE_GROUP_REMOVE:
					opgroup.add(operationid);
					break;
				case OPERATION_TYPE_TEMPLATE_ADD:
				case OPERATION_TYPE_TEMPLATE_REMOVE:
					optemplate.add(operationid);
					break;
				case OPERATION_TYPE_HOST_ADD:
				case OPERATION_TYPE_HOST_REMOVE:
				case OPERATION_TYPE_HOST_ENABLE:
				case OPERATION_TYPE_HOST_DISABLE:
				}
			}
			
			// get OPERATION_TYPE_MESSAGE data
			if (!opmessage.isEmpty()) {
				if (outputIsRequested("opmessage", params.getSelectOperations())) {
					for (Long operationId : opmessage) {
						operations.get(operationId).put("opmessage", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("o.operationid");
					sqlParts.select.put("o.default_msg");
					sqlParts.select.put("o.subject");
					sqlParts.select.put("o.message");
					sqlParts.select.put("o.mediatypeid");
					sqlParts.from.put("opmessage o");
					applyQueryTenantOptions("opmessage", "o", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opmessage.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						Nest.value(operations, row.get("operationid"), "opmessage").$(row);
					}
				}
				
				if (outputIsRequested("opmessage_grp", params.getSelectOperations())) {
					for (Long operationId : opmessage) {
						operations.get(operationId).put("opmessage_grp", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("og.operationid");
					sqlParts.select.put("og.usrgrpid");
					sqlParts.from.put("opmessage_grp og");
					applyQueryTenantOptions("opmessage_grp", "og", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opmessage.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("opmessage_grp")).add(row);
					}
				}
				
				if (outputIsRequested("opmessage_usr", params.getSelectOperations())) {
					for (Long operationId : opmessage) {
						operations.get(operationId).put("opmessage_usr", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("ou.operationid");
					sqlParts.select.put("ou.userid");
					sqlParts.from.put("opmessage_usr ou");
					applyQueryTenantOptions("opmessage_usr", "ou", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opmessage.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("opmessage_usr")).add(row);
					}
				}
			}
			
			// get OPERATION_TYPE_COMMAND data
			if (!opcommand.isEmpty()) {
				if (outputIsRequested("opcommand", params.getSelectOperations())) {
					for (Long operationId : opcommand) {
						operations.get(operationId).put("opcommand", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("o.*");
					sqlParts.from.put("opcommand o");
					applyQueryTenantOptions("opcommand", "o", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opcommand.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						Nest.value(operations, row.get("operationid"), "opcommand").$(row);
					}
				}
				
				if (outputIsRequested("opcommand_hst", params.getSelectOperations())) {
					for (Long operationId : opcommand) {
						operations.get(operationId).put("opcommand_hst", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("oh.opcommand_hstid");
					sqlParts.select.put("oh.operationid");
					sqlParts.select.put("oh.hostid");
					sqlParts.from.put("opcommand_hst oh");
					applyQueryTenantOptions("opcommand_hst", "oh", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opcommand.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("opcommand_hst")).add(row);
					}
				}
				
				if (outputIsRequested("opcommand_grp", params.getSelectOperations())) {
					for (Long operationId : opcommand) {
						operations.get(operationId).put("opcommand_grp", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("og.opcommand_grpid");
					sqlParts.select.put("og.operationid");
					sqlParts.select.put("og.groupid");
					sqlParts.from.put("opcommand_grp og");
					applyQueryTenantOptions("opcommand_grp", "og", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opcommand.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("opcommand_grp")).add(row);
					}
				}
			}
			
			// get OPERATION_TYPE_GROUP_ADD, OPERATION_TYPE_GROUP_REMOVE data
			if (!opgroup.isEmpty()) {
				if (outputIsRequested("opgroup", params.getSelectOperations())) {
					for (Long operationId : opgroup) {
						operations.get(operationId).put("opgroup", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("o.operationid");
					sqlParts.select.put("o.groupid");
					sqlParts.from.put("opgroup o");
					applyQueryTenantOptions("opgroup", "o", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", opgroup.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("opgroup")).add(row);
					}
				}
			}
			
			// get OPERATION_TYPE_TEMPLATE_ADD, OPERATION_TYPE_TEMPLATE_REMOVE data
			if (!optemplate.isEmpty()) {
				if (outputIsRequested("optemplate", params.getSelectOperations())) {
					for (Long operationId : optemplate) {
						operations.get(operationId).put("optemplate", array());
					}
					
					SqlBuilder sqlParts = new SqlBuilder();		
					sqlParts.select.put("o.operationid");
					sqlParts.select.put("o.templateid");
					sqlParts.from.put("optemplate o");
					applyQueryTenantOptions("optemplate", "o", options, sqlParts);
					sqlParts.where.dbConditionInt("operationid", optemplate.valuesAsLong());
					
					String sql = createSelectQueryFromParts(sqlParts);
					Map paraMap = sqlParts.getNamedParams();
					List<Map> datas = (List<Map>)getSqlExecutor().executeNameParaQuery(sql, paraMap);
					for(Map row : datas){
						((CArray)operations.get((Long)row.get("operationid")).get("optemplate")).add(row);
					}
				}
			}
			
			unsetExtraFields(operations, new String[]{"operationid","actionid","operationtype"}, params.getSelectOperations());
			relationMap.mapMany(result, operations, "operations");
		}
	}
	
}
