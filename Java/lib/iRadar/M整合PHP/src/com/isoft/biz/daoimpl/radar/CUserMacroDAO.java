package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_SHORTEN;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CUserMacroGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with user macro.
 *
 * @package API
 */
public class CUserMacroDAO extends CCoreLongKeyDAO<CUserMacroGet> {
	
	public CUserMacroDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "hostmacro", "hm", new String[]{"macro"});
	}
	
	/**
	 * Get UserMacros data.
	 *
	 * @param array _options
	 * @param array Nest.value(_options,"groupids").$() usermacrosgroup ids
	 * @param array Nest.value(_options,"hostids").$() host ids
	 * @param array Nest.value(_options,"hostmacroids").$() host macros ids
	 * @param array Nest.value(_options,"globalmacroids").$() global macros ids
	 * @param array Nest.value(_options,"templateids").$() template ids
	 * @param boolean Nest.value(_options,"globalmacro").$() only global macros
	 * @param boolean Nest.value(_options,"selectGroups").$() select groups
	 * @param boolean Nest.value(_options,"selectHosts").$() select hosts
	 * @param boolean Nest.value(_options,"selectTemplates").$() select templates
	 *
	 * @return array|boolean UserMacros data as array or false if error
	 */
	@Override
	public <T> T get(CUserMacroGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("macros", "hm.hostmacroid");
		sqlParts.from.put("hostmacro hm");
		
		SqlBuilder sqlPartsGlobal = new SqlBuilder();
		sqlPartsGlobal.select.put("macros", "gm.globalmacroid");
		sqlPartsGlobal.from.put("globalmacro gm");
		
		// global macro
		if (!is_null(params.getGlobalMacro())) {
			params.setGroupIds((Long[])null);
			params.setHostMacroIds((Long[])null);
			params.setTriggerIds((Long[])null);
			params.setHostIds((Long[])null);
			params.setItemIds((Long[])null);
			params.setSelectGroups(null);
			params.setSelectTemplates(null);
			params.setSelectHosts(null);
		}

		// globalmacroids
		if (!is_null(params.getGlobalMacroIds())) {
			sqlPartsGlobal.where.dbConditionInt("gm.globalmacroid",params.getGlobalMacroIds());
		}

		// hostmacroids
		if (!is_null(params.getHostMacroIds())) {
			sqlParts.where.dbConditionInt("hm.hostmacroid",params.getHostMacroIds());
		}

		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid",params.getGroupIds());
			sqlParts.where.put("hgh.tenantid","hg.tenantid=hm.tenantid");
			sqlParts.where.put("hgh","hg.hostid=hm.hostid");
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.where.dbConditionInt("hm.hostid",params.getHostIds());
		}

		// templateids
		if (!is_null(params.getTemplateIds())) {
			sqlParts.select.put("templateid","ht.templateid");
			sqlParts.from.put("macros_templates","hosts_templates ht");
			sqlParts.where.dbConditionInt("ht.templateid",params.getTemplateIds());
			sqlParts.where.put("hht.tenantid","hm.tenantid=ht.tenantid");
			sqlParts.where.put("hht","hm.hostid=ht.hostid");
		}

		// search
		if (isArray(params.getSearch())) {
			dbSearch("hostmacro hm", params, sqlParts);
			dbSearch("globalmacro gm", params, sqlPartsGlobal);
		}

		// filter
		if (isArray(params.getFilter())) {
			if (isset(params.getFilter().get("macro"))) {
				sqlParts.where.dbConditionString("hm.macro", TArray.as(params.getFilter().get("macro")).asString());
				sqlPartsGlobal.where.dbConditionString("gm.macro", TArray.as(params.getFilter().get("macro")).asString());
			}
		}

		applyQuerySortOptions("hostmacro","hm", params, sqlParts);
		applyQuerySortOptions("globalmacro", "gm", params, sqlPartsGlobal);
		
		// limit
		if (params.getLimit()!=null) { // rda_ctype_digit(_options['limit']) && 
			sqlParts.limit = params.getLimit();
			sqlPartsGlobal.limit = params.getLimit();
		}

		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		// init GLOBALS
		if (!is_null(params.getGlobalMacro())) {
			applyQueryOutputOptions("globalmacro", "gm", params, sqlPartsGlobal);
			applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlPartsGlobal); 
			
			for(Map row : datas){
				if (params.getCountOutput()!=null) {
						ret = row.get("rowscount");
				} else {
					Long id = (Long)row.get("globalmacroid");
					if (API_OUTPUT_SHORTEN.equals(params.getOutput())) {
						result.put(id, map("globalmacroid",id));
					} else {
						if (!isset(result.get(id))) {
							result.put(id, new HashMap());
						}
						result.get(id).putAll(row);
					}
				}
			}
		}
		// init HOSTS
		else {
			applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
			applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
			
			for(Map row : datas){
				if (params.getCountOutput()!=null) {
						ret = row.get("rowscount");
				} else {	
					Long id = (Long)row.get("hostmacroid");
					if (!isset(result.get(id))) {
						result.put(id, new HashMap());
					}
					// groupids
					if (isset(row.get("groupid"))) {
						if (!isset(result.get(id).get("groups"))) {
							result.get(id).put("groups", new CArray());
						}
						((CArray)result.get(id).get("groups")).add(map("groupid", row.get("groupid")));
					}
					// templateids
					if (isset(row.get("templateid"))) {
						if (!isset(result.get(id).get("templates"))) {
							result.get(id).put("templates", new CArray());
						}
						((CArray)result.get(id).get("templates")).add(map("templateid", row.get("templateid")));
					}
					// hostids
					if (isset(row.get("hostid"))) {
						if (!isset(result.get(id).get("hosts"))) {
							result.get(id).put("hosts", new CArray());
						}
						((CArray)result.get(id).get("hosts")).add(map("hostid", row.get("hostid")));
					}
					result.get(id).putAll(row);
				}
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!Cphp.empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"hostid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (Cphp.is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Validates the input parameters for the createGlobal() method.
	 *
	 * @param array _globalMacros
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateCreateGlobal(CArray<Map> globalMacros) {
		checkGlobalMacrosPermissions(_("Only Super Admins can create global macros."));

		for(Map globalMacro: globalMacros) {
			CArray cglobalMacro = CArray.valueOf(globalMacro);
			checkMacro(cglobalMacro);
			checkValue(cglobalMacro);
			checkUnsupportedFields("globalmacro", cglobalMacro,
				_s("Wrong fields for macro \"%1$s\".", Nest.value(cglobalMacro,"macro").$()));
		}

		checkDuplicateMacros(globalMacros);
		checkIfGlobalMacrosDontRepeat(globalMacros);
	}
	
	/**
	 * Add global macros.
	 *
	 * @param array globalMacros
	 *
	 * @return array
	 */
	public CArray createGlobal(CArray globalMacros) {
		globalMacros = rda_toArray(globalMacros);

		validateCreateGlobal(globalMacros);

		CArray<Long> globalmacroids = insert("globalmacro", globalMacros);

		return map("globalmacroids", globalmacroids);
	}
	
	/**
	 * Validates the input parameters for the updateGlobal() method.
	 *
	 * @param array _globalMacros
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateUpdateGlobal(CArray<Map> globalMacros) {
		checkGlobalMacrosPermissions(_("Only Super Admins can update global macros."));

		for(Map globalMacro: globalMacros) {
			if (!isset(globalMacro,"globalmacroid") || rda_empty(Nest.value(globalMacro,"globalmacroid").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		globalMacros = extendObjects("globalmacro", globalMacros, new String[]{"macro"});

		for(Map globalMacro: globalMacros) {
			CArray mglobalMacro = CArray.valueOf(globalMacro);
			checkMacro(mglobalMacro);
			checkValue(mglobalMacro);
			checkUnsupportedFields("globalmacro", mglobalMacro,
				_s("Wrong fields for macro \"%1$s\".", Nest.value(mglobalMacro,"macro").$()));
		}

		checkDuplicateMacros(globalMacros);
		checkIfGlobalMacrosExist(rda_objectValues(globalMacros, "globalmacroid").valuesAsLong());
		checkIfGlobalMacrosDontRepeat(globalMacros);
	}
	
	/**
	 * Updates global macros.
	 *
	 * @param array _globalMacros
	 *
	 * @return array
	 */
	public CArray updateGlobal(CArray<Map> globalMacros) {
		globalMacros = rda_toArray(globalMacros);

		validateUpdateGlobal(globalMacros);

		// update macros
		CArray data = array();
		for(Map gmacro: globalMacros) {
			Object globalMacroId = Nest.value(gmacro,"globalmacroid").$();
			unset(gmacro, "globalmacroid");

			data.add( map(
				"values", gmacro,
				"where", map("globalmacroid", globalMacroId)
			));
		}
		update("globalmacro", data);

		return map("globalmacroids", rda_objectValues(globalMacros, "globalmacroid"));
	}
	
	/**
	 * Validates the input parameters for the deleteGlobal() method.
	 *
	 * @param array _globalMacroIds
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateDeleteGlobal(Long[] globalMacroIds) {
		if (empty(globalMacroIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		checkGlobalMacrosPermissions(_("Only Super Admins can delete global macros."));
		checkIfGlobalMacrosExist(globalMacroIds);
	}
	
	/**
	 * Delete global macros.
	 *
	 * @param mixed _globalMacroIds
	 *
	 * @return array
	 */
	public CArray<Long[]> deleteGlobal(Long[] globalMacroIds) {
		validateDeleteGlobal(globalMacroIds);

		// delete macros
		delete("globalmacro", (Map)map("globalmacroid", globalMacroIds));

		return map("globalmacroids", globalMacroIds);
	}
	
	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @param array _hostMacros
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateCreate(CArray<Map> hostMacros) {
		// check the data required for authorization first
		for(Map hostMacro: hostMacros) {
			checkHostId(CArray.valueOf(hostMacro));
		}

		checkHostPermissions(array_unique(rda_objectValues(hostMacros, "hostid")));

		for(Map hostMacro: hostMacros) {
			CArray chostMacro = CArray.valueOf(hostMacro);
			checkMacro(chostMacro);
			checkValue(chostMacro);
			checkUnsupportedFields("hostmacro", chostMacro,
				_s("Wrong fields for macro \"%1$s\".", Nest.value(chostMacro,"macro").$()));
		}

		checkDuplicateMacros(hostMacros);
		checkIfHostMacrosDontRepeat(hostMacros);
	}
	
	/**
	 * Add new host macros.
	 *
	 * @param array _hostMacros an array of host macros
	 *
	 * @return array
	 */
	public CArray create(CArray hostMacros) {
		hostMacros = rda_toArray(hostMacros);

		this.validateCreate(hostMacros);

		CArray hostmacroids = this.insert("hostmacro", hostMacros);

		return map("hostmacroids", hostmacroids);
	}
	
	/**
	 * Validates the input parameters for the update() method.
	 *
	 * @param array _hostMacros
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateUpdate(CArray<Map> hostMacros) {
		for(Map hostMacro: hostMacros) {
			if (!isset(hostMacro,"hostmacroid") || rda_empty(Nest.value(hostMacro,"hostmacroid").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		// make sure we have all the data we need
		hostMacros = extendObjects(tableName(), hostMacros, new String[]{"macro", "hostid"});
		CUserMacroGet umoptions = new CUserMacroGet();
		umoptions.setHostMacroIds(rda_objectValues(hostMacros, "hostmacroid").valuesAsLong());
		umoptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> dbHostMacros = get(umoptions);

		// check the data required for authorization first
		for(Map hostMacro: hostMacros) {
			checkHostId(CArray.valueOf(hostMacro));
		}

		// check permissions for all affected hosts
		CArray affectedHostIds = array_merge(rda_objectValues(dbHostMacros, "hostid"), rda_objectValues(hostMacros, "hostid"));
		affectedHostIds = array_unique(affectedHostIds);
		checkHostPermissions(affectedHostIds);

		for(Map hostMacro: hostMacros) {
			CArray chostMacro = CArray.valueOf(hostMacro);
			checkMacro(chostMacro);
			checkHostId(chostMacro);
			checkValue(chostMacro);
			checkUnsupportedFields("hostmacro", chostMacro,
				_s("Wrong fields for macro \"%1$s\".", Nest.value(chostMacro,"macro").$()));
		}

		checkDuplicateMacros(hostMacros);

		// check if the macros exist
		checkIfHostMacrosExistIn(rda_objectValues(hostMacros, "hostmacroid"), dbHostMacros);

		checkIfHostMacrosDontRepeat(hostMacros);
	}
	
	/**
	 * Update host macros
	 *
	 * @param array _hostMacros an array of host macros
	 *
	 * @return boolean
	 */
	public CArray update(CArray<Map> hostMacros) {
		hostMacros = rda_toArray(hostMacros);

		validateUpdate(hostMacros);

		CArray data = array();
		for(Map macro: hostMacros) {
			Object _hostMacroId = Nest.value(macro,"hostmacroid").$();
			unset(macro,"hostmacroid");

			data.add( map(
				"values" , macro,
				"where" , map("hostmacroid" , _hostMacroId)
			));
		}

		update("hostmacro", data);

		return map("hostmacroids" , rda_objectValues(hostMacros, "hostmacroid"));
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @param array _hostMacroIds
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateDelete(CArray hostMacroIds) {
		if (empty(hostMacroIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CUserMacroGet umoptions = new CUserMacroGet();
		umoptions.setOutput(new String[]{"hostid", "hostmacroid"});
		umoptions.setHostMacroIds(hostMacroIds.valuesAsLong());
		CArray<Map> _dbHostMacros = select("hostmacro", umoptions);

		// check permissions for all affected hosts
		checkHostPermissions(array_unique(rda_objectValues(_dbHostMacros, "hostid")));

		// check if the macros exist
		checkIfHostMacrosExistIn(hostMacroIds, _dbHostMacros);
	}
	
	/**
	 * Remove Macros from Hosts
	 *
	 * @param mixed hostMacroIds
	 *
	 * @return boolean
	 */
	public CArray delete(CArray hostMacroIds) {
		hostMacroIds = rda_toArray(hostMacroIds);

		this.validateDelete(hostMacroIds);

		this.delete("hostmacro", (Map)map("hostmacroid", hostMacroIds));

		return map("hostmacroids", hostMacroIds);
	}
	
	/**
	 * Replace macros on hosts/templates.
	 * _macros input array has hostid as key and array of that host macros as value.
	 *
	 * @param array macros
	 *
	 * @return void
	 */
	public void replaceMacros(CArray<Map> macros) {
		CArray hostIds = array_keys(macros);
		if (!API.Host(this.idBean, this.getSqlExecutor()).isWritable(hostIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		CHostGet hoptions = new CHostGet();
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setSelectMacros(API_OUTPUT_EXTEND);
		hoptions.setTemplatedHosts(true);
		hoptions.setOutput(API_OUTPUT_REFER);
		hoptions.setPreserveKeys(true);
		CArray<Map> dbMacros = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

		CArray macroIdsToDelete = array();
		CArray macrosToUpdate = array();
		CArray macrosToAdd = array();

		for(Entry<Object, Map> entry: macros.entrySet()) {
			Object hostid = entry.getKey();
			Map hostMacros = entry.getValue();
			CArray<Map> dbHostMacros = rda_toHash(dbMacros.getNested(hostid, "macros"), "hostmacroid");

			// look for db macros which hostmacroids are not in list of new macros
			// if there are any, they should be deleted
			CArray hostMacroIds = rda_toHash(hostMacros, "hostmacroid");
			for(Map dbHostMacro: dbHostMacros) {

				if (!isset(hostMacroIds.get(dbHostMacro.get("hostmacroid")))) {
					macroIdsToDelete.add( Nest.value(dbHostMacro,"hostmacroid").$() );
				}
			}

			// if macro has hostmacroid it should be updated otherwise created as new
			for(Map hostMacro: (CArray<Map>)CArray.valueOf(hostMacros)) {
				if (isset(Nest.value(hostMacro,"hostmacroid").$()) && isset(dbHostMacros.get(hostMacro.get("hostmacroid")))) {
					macrosToUpdate.add(hostMacro);
				} else {
					Nest.value(hostMacro,"hostid").$(hostid);
					macrosToAdd.add( hostMacro );
				}
			}
		}

		if (!empty(macroIdsToDelete)) {
			delete(macroIdsToDelete);
		}
		if (!empty(macrosToAdd)) {
			create(macrosToAdd);
		}
		if (!empty(macrosToUpdate)) {
			update(macrosToUpdate);
		}
	}
	
	/**
	 * Validates the \"macro\" field.
	 *
	 * @param array _macro
	 *
	 * @throws APIException if the field is empty, too long or doesn't match the RDA_PREG_EXPRESSION_USER_MACROS
	 * regex.
	 */
	protected void checkMacro(CArray _macro) {
		if (!isset(Nest.value(_macro,"macro").$()) || rda_empty(Nest.value(_macro,"macro").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty macro."));
		}
		if (rda_strlen(Nest.value(_macro,"macro").asString()) > 64) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro name \"%1$s\" is too long, it should not exceed 64 chars.", Nest.value(_macro,"macro").$()));
		}
		if (preg_match("^"+RDA_PREG_EXPRESSION_USER_MACROS+"$", Nest.value(_macro,"macro").asString()) == 0) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Wrong macro \"%1$s\".", Nest.value(_macro,"macro").$()));
		}
	}
	
	/**
	 * Validate the \"value\" field.
	 *
	 * @param array _macro
	 *
	 * @throws APIException if the field is too long.
	 */
	protected void checkValue(CArray _macro) {
		if (isset(Nest.value(_macro,"value").$()) && rda_strlen(Nest.value(_macro,"value").asString()) > 255) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro \"%1$s\" value is too long, it should not exceed 255 chars.", Nest.value(_macro,"macro").$()));
		}
	}
	
	/**
	 * Validates the \"hostid\" field.
	 *
	 * @param array _macro
	 *
	 * @throws APIException if the field is empty.
	 */
	protected void checkHostId(CArray _macro) {
		if (!isset(Nest.value(_macro,"hostid").$()) || rda_empty(Nest.value(_macro,"hostid").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No host given for macro \"%1$s\".", Nest.value(_macro,"macro").$()));
		}
		if (!is_numeric(Nest.value(_macro,"hostid").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Invalid hostid for macro \"%1$s\".", Nest.value(_macro,"macro").$()));
		}
	}
	
	/**
	 * Checks if the current user has access to the given hosts and templates. Assumes the \"hostid\" field is valid.
	 *
	 * @throws APIException if the user doesn't have write permissions for the given hosts
	 *
	 * @param array _hostIds    an array of host or template IDs
	 */
	protected void checkHostPermissions(CArray _hostIds) {
		if (!API.Host(this.idBean, this.getSqlExecutor()).isWritable(_hostIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Checks if the given macros contain duplicates. Assumes the \"macro\" field is valid.
	 *
	 * @throws APIException if the given macros contain duplicates
	 *
	 * @param array _macros
	 *
	 * @return void
	 */
	protected void checkDuplicateMacros(CArray<Map> macros) {
		CArray existingMacros = array();
		for(Map macro: macros) {
			// global macros don't have hostid
			Object hostid = isset(Nest.value(macro,"hostid").$()) ? Nest.value(macro,"hostid").$() : 1;

			if (isset(existingMacros.getNested(hostid, macro.get("macro")))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro \"%1$s\" is not unique.", Nest.value(macro,"macro").$()));
			}

			existingMacros.put(hostid, macro.get("macro"), 1);
		}
	}

	/**
	 * Checks if any of the given host macros already exist on the corresponding hosts. If the macros are updated and
	 * the \"hostmacroid\" field is set, the method will only fail, if a macro with a different hostmacroid exists.
	 * Assumes the \"macro\", \"hostid\" and \"hostmacroid\" fields are valid.
	 *
	 * @param array _hostMacros
	 *
	 * @throws APIException if any of the given macros already exist
	 */
	protected void checkIfHostMacrosDontRepeat(CArray<Map> hostMacros) {
		CUserMacroGet umoptions = new CUserMacroGet();
		umoptions.setOutput(new String[]{"hostmacroid", "hostid", "macro"});
		umoptions.setFilter("macro" , rda_objectValues(hostMacros, "macro").valuesAsString());
		umoptions.setFilter("hostid" , array_unique(rda_objectValues(hostMacros, "hostid")).valuesAsString());
		CArray<Map> dbHostMacros = select(tableName(), umoptions);

		for(Map hostMacro: hostMacros) {
			for(Map dbHostMacro: dbHostMacros) {
				boolean differentMacros = ((isset(Nest.value(hostMacro,"hostmacroid").$())
					&& bccomp(Nest.value(hostMacro,"hostmacroid").$(), Nest.value(dbHostMacro,"hostmacroid").$()) != 0)
					|| !isset(Nest.value(hostMacro,"hostmacroid").$()));

				if (Nest.value(hostMacro,"macro").asString().equals(Nest.value(dbHostMacro,"macro").asString()) && bccomp(Nest.value(hostMacro,"hostid").$(), Nest.value(dbHostMacro,"hostid").$()) == 0
						&& differentMacros) {
					umoptions = new CUserMacroGet();
					umoptions.setOutput(new String[]{"name"});
					umoptions.setHostIds(Nest.value(hostMacro, "hostid").asLong());
					Map _hosts = select("hosts", umoptions);
					Map _host = reset(_hosts);
					String _error = _s("Macro \"%1$s\" already exists on \"%2$s\".", Nest.value(hostMacro,"macro").$(), Nest.value(_host,"name").$());
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _error);
				}
			}
		}
	}

	/**
	 * Checks if all of the host macros with hostmacrosids given in _hostMacrosIds are present in _hostMacros.
	 * Assumes the \"hostmacroid\" field is valid.
	 *
	 * @param array _hostMacrosIds
	 * @param array _hostMacros
	 *
	 * @throws APIException if any of the host macros is not present in _hostMacros
	 */
	protected void checkIfHostMacrosExistIn(CArray<Object> hostMacrosIds, CArray hostMacros) {
		hostMacros = rda_toHash(hostMacros, "hostmacroid");
		for(Object hostMacroId: hostMacrosIds) {
			if (!isset(hostMacros.get(hostMacroId))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro with hostmacroid \"%1$s\" does not exist.", hostMacroId));
			}
		}
	}

	/**
	 * Checks if any of the given host global macros already exist. If the macros are updated and
	 * the \"globalmacroid\" field is set, the method will only fail, if a macro with a different globalmacroid exists.
	 * Assumes the \"macro\", \"hostmacroid\" fields are valid.
	 *
	 * @param array _globalMacros
	 *
	 * @throws APIException if any of the given macros already exist
	 */
	protected void checkIfGlobalMacrosDontRepeat(CArray globalMacros) {
		CArray nameMacro = rda_toHash(globalMacros, "macro");
		CArray macroNames = rda_objectValues(globalMacros, "macro");
		if (!empty(macroNames)) {
			CUserMacroGet umoptions = new CUserMacroGet();
			umoptions.setFilter("macro" , macroNames.valuesAsString());
			umoptions.setOutput(new String[]{"globalmacroid", "macro"});
			CArray<Map> dbMacros = select("globalmacro", umoptions);
			for(Map dbMacro: dbMacros) {
				Map macro = Nest.value(nameMacro, dbMacro.get("macro")).asCArray();
				if (!isset(Nest.value(macro,"globalmacroid").$()) || bccomp(Nest.value(macro,"globalmacroid").$(), Nest.value(dbMacro,"globalmacroid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro \"%1$s\" already exists.", Nest.value(dbMacro,"macro").$()));
				}
			}
		}
	}

	/**
	 * Checks if the user has the permissions to edit global macros.
	 *
	 * @param string _error a message that will be used as the error text
	 *
	 * @throws APIException if the user doesn't have the required permissions
	 */
	protected void checkGlobalMacrosPermissions(String error) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, error);
		}
	}

	/**
	 * Checks if all of the global macros with globalmacroids given in _globalMacroIds are present in _globalMacros.
	 * Assumes the \"globalmacroids\" field is valid.
	 *
	 * @param array _globalMacroIds
	 *
	 * @throws APIException if any of the global macros is not present in _globalMacros
	 */
	protected void checkIfGlobalMacrosExist(Long[] globalMacroIds) {
		CUserMacroGet umoptions = new CUserMacroGet();
		umoptions.setOutput(new String[]{"globalmacroid"});
		umoptions.setGlobalMacroIds(TArray.as(globalMacroIds).asLong());
		CArray globalMacros = select("globalmacro", umoptions);
		globalMacros = rda_toHash(globalMacros, "globalmacroid");
		for(Object globalMacroId: globalMacroIds) {
			if (!isset(globalMacros.get(globalMacroId))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Macro with globalmacroid \"%1$s\" does not exist.", globalMacroId));
			}
		}
	}
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if(!API_OUTPUT_COUNT.equals(params.getOutput()) && is_null(params.get("globalMacro"))){
			if (!is_null(params.get("selectGroups"))
					|| !is_null(params.get("selectHosts"))
					|| !is_null(params.get("selectTemplates"))) {
				addQuerySelect(fieldId("hostid"), sqlParts);
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(CUserMacroGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		if(is_null(params.getGlobalMacro())){
			Long[] hostMacroIds = result.keysAsLong();
			
			// adding groups
			if(!is_null(params.getSelectGroups()) && !API_OUTPUT_COUNT.equals(params.getSelectGroups())){
				SqlBuilder sqlParts = new SqlBuilder();		
				sqlParts.select.put("hm.hostmacroid");
				sqlParts.select.put("hg.groupid");
				sqlParts.from.put("hostmacro hm");
				sqlParts.from.put("hosts_groups hg");
				applyQueryTenantOptions("hostmacro", "hg", params, sqlParts);
				sqlParts.where.dbConditionInt("hm.hostmacroid", hostMacroIds);
				sqlParts.where.put("hm.tenantid=hg.tenantid");
				sqlParts.where.put("hm.hostid=hg.hostid");
				
				CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
				CRelationMap relationMap = this.createRelationMap(datas, "hostmacroid", "groupid");
				
				CHostGroupGet gparams = new CHostGroupGet();
				gparams.setOutput(params.getSelectGroups());
				gparams.setGroupIds(relationMap.getRelatedLongIds());
				gparams.setPreserveKeys(true);
				
				datas = API.HostGroup(this.idBean, getSqlExecutor()).get(gparams); 
				relationMap.mapMany(result, datas, "groups", params.getLimitSelects());
			}
			
			// adding templates
			if(!is_null(params.getSelectTemplates()) && !API_OUTPUT_COUNT.equals(params.getSelectTemplates())){
				CRelationMap relationMap = createRelationMap(result, "hostmacroid", "hostid");
				CTemplateGet tplParams = new CTemplateGet();
				tplParams.setOutput(params.getSelectTemplates());
				tplParams.setTemplateIds(relationMap.getRelatedLongIds());
				tplParams.setPreserveKeys(true);
				
				CArray<Map> datas = API.Template(this.idBean, getSqlExecutor()).get(tplParams);
				relationMap.mapMany(result, datas, "templates", params.getLimitSelects());
			}
			
			// adding hosts
			if(!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())){
				CRelationMap relationMap = createRelationMap(result, "hostmacroid", "hostid");
				CHostGet hostParams = new CHostGet();
				hostParams.setOutput(params.getSelectHosts());
				hostParams.setHostIds(relationMap.getRelatedLongIds());
				hostParams.setPreserveKeys(true);
				
				CArray<Map> datas = API.Host(this.idBean, getSqlExecutor()).get(hostParams);
				relationMap.mapMany(result, datas, "hosts", params.getLimitSelects());
			}
		}
	}
}
