package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.g.RDA_SERVER;
import static com.isoft.iradar.core.g.RDA_SERVER_PORT;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TIMEOUT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_SOCKET_BYTES_LIMIT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.splitPath;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static org.apache.commons.collections.ListUtils.isEqualList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.model.sql.SqlDecorator;
import com.isoft.iradar.server.IRadarServer;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with scripts.
 * @author benne
  */
public class CScriptDAO extends CCoreLongKeyDAO<CScriptGet> {

	public CScriptDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "scripts", "s", new String[] { "scriptid", "name" });
	}

	@Override
	public <T> T get(CScriptGet params) {
		final SQLExecutor executor = getSqlExecutor();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("scripts", "s.scriptid");
		sqlParts.from.put("scripts s");
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.where.dbConditionInt("s.groupid", TArray.as(params.getGroupIds()).asLong(), new SqlDecorator(){
				@Override
				public String decorate(String sql) {
					return "(s.groupid IS NULL OR "+sql+")";
				}
			});
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			// return scripts that are assigned to the hosts' groups or to no group
			CHostGroupGet grpGet = new CHostGroupGet();
			grpGet.setOutput(new String[] { "groupid" });
			grpGet.setHostIds(params.getHostIds());
			CArray<Map> hostGroups = API.HostGroup(this.idBean, executor).get(grpGet);
			Long[] hostGroupIds = rda_objectValues(hostGroups, "groupid").valuesAsLong();
			
			sqlParts.where.dbConditionInt("s.groupid", hostGroupIds, new SqlDecorator(){
				@Override
				public String decorate(String sql) {
					return "("+sql+" OR  s.groupid IS NULL)";
				}
			});
		}

		// usrgrpids
		if (!is_null(params.getUsrgrpIds())) {
			sqlParts.where.dbConditionInt("s.usrgrpid", TArray.as(params.getUsrgrpIds()).asLong(), new SqlDecorator(){
				@Override
				public String decorate(String sql) {
					return "(s.usrgrpid IS NULL OR "+sql+")";
				}
			});
		}

		// scriptids
		if (!is_null(params.getScriptIds())) {
			sqlParts.where.dbConditionInt("s.scriptid", params.getScriptIds());
		}

		// search
		if (params.getSearch() != null && !params.getSearch().isEmpty()) {
			dbSearch("scripts s", params, sqlParts);
		}
		
		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("scripts s", params, sqlParts);
		}

		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(),sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for (Map row : datas) {
			if (params.getCountOutput() != null) {
				ret = row.get("rowscount");
			} else {
				Long id = (Long) row.get("scriptid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				result.get(id).putAll(row);
			}
		}		
		
		if (!is_null(params.getCountOutput())) {
			return (T) ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[] { "groupid", "host_access" }, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T) result;
	}
	
	/**
	 * Add scripts.
	 *
	 * @param array _scripts
	 * @param array _scripts["name"]
	 * @param array _scripts["hostid"]
	 * @return 
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> scripts) {
		validateCreate(scripts);
		scripts = trimMenuPath(scripts);
		validateMenuPath(scripts, "create");
		scripts = unsetExecutionType(scripts);
		CArray<Long> scriptIds = insert("scripts", scripts);
		return map("scriptids", scriptIds.valuesAsLong());
	}
	
	/**
	 * Update scripts.
	 *
	 * @param array _scripts
	 * @param array _scripts["name"]
	 * @param array _scripts["hostid"]
	 * @return 
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> scripts) {
		validateUpdate(scripts);
		scripts = trimMenuPath(scripts);
		validateMenuPath(scripts,"update");
		scripts = unsetExecutionType(scripts);
		
		CArray update = array();
		for(Map script : scripts) {
			String scriptId = Nest.value(script,"scriptid").asString();
			unset(script,"scriptid");
			update.add(map(
				"values", script,
				"where", map("scriptid", scriptId)
			));
		}
		update("scripts", update);
		return map("scriptids", rda_objectValues(scripts, "scriptid").valuesAsLong());
	}
	
	/**
	 * Delete scripts.
	 *
	 * @param array _scriptIds
	 * @return 
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... scriptIds) {
		validateDelete(scriptIds);
		delete("scripts", (Map) map("scriptid", scriptIds));
		return map("scriptid", scriptIds);
	}
	
	/**
	 * Execute script.
	 *
	 * @param string _data["scriptid"]
	 * @param string _data["hostid"]
	 * @return 
	 *
	 * @return array
	 */
	public CArray execute(CArray data) {
		Long scriptId = Nest.value(data,"scriptid").asLong();
		Long hostId = Nest.value(data,"hostid").asLong();

		CScriptGet options = new CScriptGet();
		options.setHostIds(hostId);
		options.setScriptIds(scriptId);
		options.setOutput(new String[]{"scriptid"});
		options.setPreserveKeys(true);
		CArray<Map> scripts = get(options);

		if (!isset(scripts,scriptId)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		// execute script
		IRadarServer iradarServer = null;
		try{
			iradarServer = new IRadarServer(RDA_SERVER, RDA_SERVER_PORT, RDA_SCRIPT_TIMEOUT, RDA_SOCKET_BYTES_LIMIT);
			Object result = iradarServer.executeScript(scriptId, hostId);
			if (!(result instanceof Boolean)) {
				// return the result in a backwards-compatible format
				return map(
					"response", "success",
					"value", result
				);
			} else {
				throw CDB.exception(RDA_API_ERROR_INTERNAL, iradarServer.getError());
			}
		} finally {
			if (iradarServer != null) {
				iradarServer.close();
			}
		}
	}

	/**
	 * Returns all the scripts that are available on each given host.
	 * @param hostIds
	 * @return array (an array of scripts in the form of array(_hostId => array(_script1, _script2, ...), ...) )
	 */
	public CArray<CArray<Map>> getScriptsByHosts(Long... hostIds) {
		CArray<CArray<Map>> scriptsByHost = array();
		
		if (empty(hostIds)) {
			return scriptsByHost;
		}
		
		for (Long hostId : hostIds) {
			Nest.value(scriptsByHost, hostId).$(array());
		}
		
		CScriptGet options = new CScriptGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectHosts(new String[]{"hostid"});
		options.setHostIds(hostIds);
		options.setSortfield("name");
		options.setPreserveKeys(true);
		CArray<Map> scripts = get(options);
		
		if (scripts != null && !scripts.isEmpty()) {
			// resolve macros
			CArray macrosData = array();
			for (Entry<Object, Map> e : scripts.entrySet()) {
				Object scriptId = e.getKey();
				Map script = e.getValue();
				if (!empty(Nest.value(script,"confirmation").$())) {
					CArray<Map> hosts = Nest.value(script, "hosts").asCArray();
					if (hosts != null && !hosts.isEmpty()) {
						for (Map host : hosts) {
							if (isset(scriptsByHost.get(host.get("hostid")))) {
								if(!macrosData.containsKey(host.get("hostid"))){
									Nest.value(macrosData, host.get("hostid")).$(array());
								}
								Nest.value(macrosData, host.get("hostid"), scriptId).$(Nest.value(script,"confirmation").$());
							}
						}
					}
				}
			}
			
			if (!macrosData.isEmpty()) {
				macrosData = CMacrosResolverHelper.resolve(this.idBean, getSqlExecutor(),map(
						"config", "scriptConfirmation",
						"data", macrosData
				));
			}
			
			for (Entry<Object, Map> e : scripts.entrySet()) {
				Object scriptId = e.getKey();
				Map script = e.getValue();
				
				CArray<Map> hosts = (CArray)script.remove("hosts");
				if (hosts != null && !hosts.isEmpty()) {
					for (Map host : hosts) {
						Object hostId = Nest.value(host,"hostid").$();
						if (isset(scriptsByHost,hostId)) {
							int size = count(scriptsByHost.get(hostId));
							Nest.value(scriptsByHost, hostId, size).$(script);
							// set confirmation text with resolved macros
							if (isset(Nest.value(macrosData, hostId, scriptId).$()) && Nest.value(script,"confirmation").asBoolean()) {
								Nest.value(scriptsByHost, hostId, size, "confirmation").$(Nest.value(macrosData, hostId, scriptId).$());
							}
						}
					}
				}
			}
		}
		return scriptsByHost;
	}
	
	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _scripts
	 */
	protected void validateCreate(CArray<Map> scripts) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		CArray dbFields = map("command" ,null, "name",null);
		CArray<String> names = array();

		for(Map script : scripts) {
			if (!check_db_fields(dbFields, script)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for script."));
			}
			String name = Nest.value(script,"name").asString();
			if (rda_empty(name)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty name for script."));
			}
			if (isset(names,name)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate script name \"%1$s\".", name));
			}
			Nest.value(names,name).$(name);
		}

		CScriptGet options = new CScriptGet();
		options.setOutput(new String[]{"name"});
		options.setFilter("name", names.valuesAsString());
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> dbScripts = get(options);
		Map dbScript = reset(dbScripts);
		if (!empty(dbScript)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script \"%1$s\" already exists.", Nest.value(dbScript,"name").asString()));
		}
	}
	
	/**
	 * Validates the input parameters for the update() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _scripts
	 */
	protected void validateUpdate(CArray<Map> scripts) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		for(Map script : scripts) {
			if (empty(Nest.value(script,"scriptid").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		scripts = rda_toHash(scripts, "scriptid");
		CArray<Object> scriptIds = array_keys(scripts);

		CArray<String> names = array();

		CScriptGet options = new CScriptGet();
		options.setScriptIds(scriptIds.valuesAsLong());
		options.setOutput(new String[]{"scriptid"});
		options.setPreserveKeys(true);
		CArray<Map> dbScripts = get(options);

		for(Map script : scripts) {
			if (!isset(dbScripts,script.get("scriptid"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script with scriptid \"%1$s\" does not exist.", Nest.value(script,"scriptid").asString()));
			}

			if (isset(script,"name")) {
				if (rda_empty(Nest.value(script,"name").asString())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty name for script."));
				}
				if (isset(names,script.get("name"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate script name \"%1$s\".", Nest.value(script,"name").asString()));
				}
				Nest.value(names,Nest.value(script,"name").$()).$(Nest.value(script,"name").$());
			}
		}

		if (!empty(names)) {
			options = new CScriptGet();
			options.setOutput(new String[]{"scriptid", "name"});
			options.setFilter("name", names.valuesAsString());
			options.setNopermissions(true);
			options.setPreserveKeys(true);
			dbScripts = get(options);

			for(Map dbScript : dbScripts) {
				Object dbScriptId = Nest.value(dbScript,"scriptid").$();
				if (!isset(scripts,dbScript.get("scriptid"))
						|| bccomp(Nest.value(scripts,dbScriptId,"scriptid").$(), dbScriptId) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script \"%1$s\" already exists.", Nest.value(dbScript,"name").$()));
				}
			}
		}
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _scriptIds
	 */
	protected void validateDelete(Long... scriptIds) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}
	
		if (empty(scriptIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete scripts. Empty input parameter \"scriptids\"."));
		}
		
		CScriptGet options = new CScriptGet();
		options.setScriptIds(TArray.as(scriptIds).asLong());
		options.setEditable(true);
		options.setOutput(new String[]{"name"});
		options.setPreserveKeys(true);
		CArray<Map> dbScripts = get(options);
	
		for(Long scriptId : scriptIds) {
			if (isset(dbScripts,scriptId)) {
				continue;
			}
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _s("Cannot delete scripts. Script with scriptid \"%1$s\" does not exist.", scriptId));
		}
	
		CActionGet params = new CActionGet();
		params.setScriptIds(TArray.as(scriptIds).asLong());
		params.setNopermissions(true);
		params.setPreserveKeys(true);
		params.setOutput(new String[]{"actionid", "name"});
		params.setSelectOperations(new String[]{"opcommand"});
		CArray<Map> actions = API.Action(this.idBean, this.getSqlExecutor()).get(params);
	
		for (Map action : actions) {
			CArray<Map> operations = Nest.value(action,"operations").asCArray();
			for (Map operation : operations) {
				if (isset(Nest.value(operation,"opcommand","scriptid").$())
						&& !empty(Nest.value(operation,"opcommand","scriptid").$())
						&& inArray(Nest.value(operation,"opcommand","scriptid").asLong(), scriptIds)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Cannot delete scripts. Script \"%1$s\" is used in action operation \"%2$s\".",
								Nest.value(dbScripts,Nest.value(operation,"opcommand","scriptid").$(),"name").$(), Nest.value(action,"name").$()
						)
					);
				}
			}
		}
	}
	
	/**
	 * Validates script name menu path.
	 * @throws APIException if the input is invalid
	 * @param array  _scripts
	 * @param string _method
	 */
	protected void validateMenuPath(CArray<Map> scripts, String method) {
		CScriptGet options = new CScriptGet();
		options.setOutput(new String[]{"scriptid", "name"});
		options.setNopermissions(true);
		CArray<Map> dbScripts = get(options);

		for(Map script : scripts) {
			if (!isset(script,"name")) {
				continue;
			}

			List<String> path = splitPath(Nest.value(script,"name").asString());
			List<String> folders = new ArrayList();
			folders.addAll(path);
			String name = array_pop(folders);

			// menu1/menu2/{empty}
			if (rda_empty(name)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Empty name for script \"%1$s\".", Nest.value(script,"name").asString()));
			}

			// menu1/{empty}/name
			for(String folder : folders) {
				if (rda_empty(folder)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect menu path for script \"%1$s\".", Nest.value(script,"name").asString()));
				}
			}

			// validate path
			for(Map dbScript : dbScripts) {
				if ("update".equals(method) && Nest.value(script,"scriptid").asLong()==Nest.value(dbScript,"scriptid").asLong()) {
					continue;
				}

				List<String> dbScriptPath = splitPath(Nest.value(dbScript,"name").asString());
				List<String> dbScriptFolders = new ArrayList();
				dbScriptFolders.addAll(dbScriptPath);
				array_pop(dbScriptFolders);

				// script NAME cannot be a FOLDER for other scripts
				List<String> dbScriptFolderItems = new ArrayList();
				for(String dbScriptFolder : dbScriptFolders) {
					dbScriptFolderItems.add(dbScriptFolder);
					if (isEqualList(path, dbScriptFolderItems)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script name \"%1$s\" already used in menu path for script \"%2$s\".", Nest.value(script,"name").$(), Nest.value(dbScript,"name").$()));
					}
				}

				// script FOLDER cannot be a NAME for other scripts
				List<String>folderItems = new ArrayList();
				for(String folder : folders) {
					folderItems.add(folder);
					if (isEqualList(dbScriptPath, folderItems)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script menu path \"%1$s\" already used in script name \"%2$s\".", Nest.value(script,"name").$(), Nest.value(dbScript,"name").$()));
					}
				}

				// check duplicate script names in same menu path
				if (isEqualList(path, dbScriptPath)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Script \"%1$s\" already exists.", Nest.value(script,"name").$()));
				}
			}
		}
	}
	
	/**
	 * Trim script name menu path.
	 * @param array _scripts
	 * @return 
	 * @return
	 */
	protected CArray<Map> trimMenuPath(CArray<Map> scripts) {
		for(Map script : scripts) {
			if (!isset(script,"name")) {
				continue;
			}
			List<String> path = splitPath(Nest.value(script,"name").asString(), false);
			Nest.value(script,"name").$("");
			for(String item : path) {
				Nest.value(script,"name").$(Nest.value(script,"name").asString()+trim(item)+"/");
			}
			Nest.value(script,"name").$(substr(Nest.value(script,"name").asString(), 0, strlen(Nest.value(script,"name").asString()) - 1));
		}
		return scripts;
	}
	
	/**
	 * Unset script execution type if type is IPMI.
	 * @param array _scripts
	 * @return array
	 */
	protected CArray<Map> unsetExecutionType(CArray<Map> scripts) {
		for (Entry<Object, Map> e : scripts.entrySet()) {
            Object key = e.getKey();
            Map script = e.getValue();
			if (isset(script,"type") && Nest.value(script,"type").asInteger() == RDA_SCRIPT_TYPE_IPMI) {
				unset(scripts,key,"execute_on");
			}
		}
		return scripts;
	}
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if(!API_OUTPUT_COUNT.equals(params.getOutput())){
			if(!is_null(params.get("selectGroups")) || !is_null(params.get("selectHosts"))){
				addQuerySelect(fieldId("groupid"), sqlParts);
				addQuerySelect(fieldId("host_access"), sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CScriptGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		// adding groups
		if(!is_null(params.getSelectGroups()) && !API_OUTPUT_COUNT.equals(params.getSelectGroups())){
			for (Entry<Object, Map> e : result.entrySet()) {
				Object scriptId = e.getKey();
				Map script = e.getValue();
				
				CHostGroupGet grpGet = new CHostGroupGet();
				grpGet.setOutput(params.getSelectGroups());
				if (!empty(script.get("groupid"))) {
					grpGet.setGroupIds(Nest.value(script, "groupid").asLong());
				}
				if(Nest.value(script, "host_access").asInteger() == PERM_READ_WRITE){
					grpGet.setEditable(true);					
				}
				CArray<Map> groups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(grpGet);
				Nest.value(result, scriptId, "groups").$(groups);
			}
		}
		
		// adding hosts
		if(!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())){
			CArray processedGroups = array();
			for (Entry<Object, Map> e : result.entrySet()) {
				Object scriptId = e.getKey();
				Map script = e.getValue();
				
				String key = script.get("groupid")+"_"+script.get("host_access");
				
				if (isset(processedGroups, key)) {
					Nest.value(result, scriptId, "hosts").$(Nest.value(result,processedGroups.get(key), "hosts").$());
				} else {
					CHostGet hgoptions = new CHostGet();
					hgoptions.setOutput(params.getSelectHosts());
					if(!empty(script.get("groupid"))){
						hgoptions.setGroupIds(Nest.value(script, "groupid").asLong());
					}
					if(!empty(script.get("groupid"))){
						hgoptions.setGroupIds(Nest.value(script, "groupid").asLong());
					}
					if(!empty(params.getHostIds())){
						hgoptions.setHostIds(params.getHostIds());
					}
					if(Nest.value(script,"host_access").asInteger() == PERM_READ_WRITE){
						hgoptions.setEditable(true);
					}
					CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hgoptions);
					Nest.value(result, scriptId, "hosts").$(hosts);
					processedGroups.put(key, scriptId);
				}
			}
		}
	}

}
