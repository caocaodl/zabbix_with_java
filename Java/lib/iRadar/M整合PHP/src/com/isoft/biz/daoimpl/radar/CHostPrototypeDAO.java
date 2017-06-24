package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_PREG_INTERNAL_NAMES;
import static com.isoft.iradar.inc.Defines.RDA_PREG_MACRO_NAME_LLD;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostPrototypeGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CSupportGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CCollectionValidator;
import com.isoft.iradar.validators.CIdValidator;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.host.CHostNormalValidator;
import com.isoft.iradar.validators.hostgroup.CHostGroupNormalValidator;
import com.isoft.iradar.validators.schema.CPartialSchemaValidator;
import com.isoft.iradar.validators.schema.CSchemaValidator;
import com.isoft.iradar.validators.string.CLldMacroStringValidator;
import com.isoft.iradar.validators.string.CStringValidator;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with host prototypes.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CHostPrototypeDAO extends CHostBaseDAO<CHostPrototypeGet> {

	public CHostPrototypeDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, new String[] {"hostid", "host", "name", "status"});
	}

	/**
	 * Get host prototypes.
	 *
	 * @param array options
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CHostPrototypeGet params) {
		params.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString());
		
		SqlBuilder sqlParts = this.createSelectQuery(this.tableName, params);
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
				Long id = (Long)row.get(this.pk);
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"triggerid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array hostPrototypes
	 *
	 * @return void
	 */
	protected void validateCreate(CArray<Map> hostPrototypes) {
		// host prototype validator
		CSchemaValidator hostPrototypeValidator = CValidator.init(new CSchemaValidator(),getHostPrototypeSchema());
		hostPrototypeValidator.setValidator("ruleid", CValidator.init(new CIdValidator(),map(
			"messageEmpty", _("No discovery rule ID given for host prototype \"%1$s\"."),
			"messageRegex", _("Incorrect discovery rule ID for host prototype \"%1$s\".")
		)));

		// group validators
		CSchemaValidator groupLinkValidator = CValidator.init(new CSchemaValidator(),getGroupLinkSchema());
		CSchemaValidator groupPrototypeValidator = CValidator.init(new CSchemaValidator(),getGroupPrototypeSchema());

		CArray groupPrototypeGroupIds = array();
		for(Map hostPrototype : hostPrototypes) {
			// host prototype
			hostPrototypeValidator.setObjectName(isset(hostPrototype,"host") ? Nest.value(hostPrototype,"host").asString() : "");
			checkValidator(CArray.valueOf(hostPrototype), hostPrototypeValidator);

			// groups
			for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupLinks").asCArray()) {
				checkValidator(CArray.valueOf(groupPrototype), groupLinkValidator);
				Nest.value(groupPrototypeGroupIds,groupPrototype.get("groupid")).$(Nest.value(groupPrototype,"groupid").$());
			}

			// group prototypes
			if (isset(hostPrototype,"groupPrototypes")) {
				for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
					groupPrototypeValidator.setObjectName(isset(groupPrototype,"name") ? Nest.value(groupPrototype,"name").asString() : "");
					checkValidator(CArray.valueOf(groupPrototype), groupPrototypeValidator);
				}
			}
		}

		checkDiscoveryRulePermissions(rda_objectValues(hostPrototypes, "ruleid"));
		checkHostGroupsPermissions(groupPrototypeGroupIds);

		// check if the host is discovered
		CParamGet options = new CParamGet();
		options.setOutput(new String[]{"hostid"});
		options.put("itemids", rda_objectValues(hostPrototypes, "ruleid").valuesAsString());
		CArray<Map> discoveryRules = select("items", options);
		checkValidator(rda_objectValues(discoveryRules, "hostid").valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
			"message", _("Cannot create a host prototype on a discovered host \"%1$s\".")
		)));

		// check if group prototypes use discovered host groups
		checkValidator(array_unique(groupPrototypeGroupIds).valuesAsLong(), CValidator.init(new CHostGroupNormalValidator(getSqlExecutor()),map(
			"message", _("Group prototype cannot be based on a discovered host group \"%1$s\".")
		)));

		checkDuplicates(hostPrototypes);
	}

	/**
	 * Returns the parameters for creating a host prototype validator.
	 *
	 * @return array
	 */
	protected Map getHostPrototypeSchema() {
		return map(
			"validators", map(
				"host", CValidator.init(new CLldMacroStringValidator(),map(
					"maxLength", 64,
					"regex", "^("+RDA_PREG_INTERNAL_NAMES+"|\\{#"+RDA_PREG_MACRO_NAME_LLD+"\\})+$",
					"messageEmpty", _("Empty host."),
					"messageMaxLength", _("Host name \"%1$s\" is too long, it must not be longer than %2$d characters."),
					"messageRegex", _("Incorrect characters used for host \"%1$s\"."),
					"messageMacro", _("Host name for host prototype \"%1$s\" must contain macros.")
				)),
				"name", CValidator.init(new CStringValidator(),map(
					// if an empty name is given, it should be replaced with the host name, but we'll validate it
					// just in case
					"messageEmpty", _("Empty name for host prototype \"%1$s\".")
				)),
				"status", CValidator.init(new CSetValidator(),map(
					"values", array(Nest.as(HOST_STATUS_MONITORED).asString(), Nest.as(HOST_STATUS_NOT_MONITORED).asString()),
					"messageInvalid", _("Incorrect status for host prototype \"%1$s\".")
				)),
				"groupLinks", CValidator.init(new CCollectionValidator(),map(
					"uniqueField", "groupid",
					"messageEmpty", _("Host prototype \"%1$s\" must have at least one host group."),
					"messageInvalid", _("Incorrect host groups for host prototype \"%1$s\"."),
					"messageDuplicate", _("Duplicate host group ID \"%2$s\" for host prototype \"%1$s\".")
				)),
				"groupPrototypes", CValidator.init(new CCollectionValidator(),map(
					"empty", true,
					"uniqueField", "name",
					"messageInvalid", _("Incorrect group prototypes for host prototype \"%1$s\"."),
					"messageDuplicate", _("Duplicate group prototype name \"%2$s\" for host prototype \"%1$s\".")
				)),
				"inventory", CValidator.init(new CSchemaValidator(),map(
					"validators", map(
						"inventory_mode", null
					),
					"messageUnsupported", _("Unsupported parameter \"%2$s\" for host prototype %1$s host inventory.")
				)),
				"templates", null
			),
			"required", array("host", "ruleid", "groupLinks"),
			"messageRequired", _("No \"%2$s\" given for host prototype \"%1$s\"."),
			"messageUnsupported", _("Unsupported parameter \"%2$s\" for host prototype \"%1$s\".")
		);
	}

	/**
	 * Returns the parameters for creating a group prototype validator.
	 *
	 * @return array
	 */
	protected Map getGroupPrototypeSchema() {
		return map(
			"validators", map(
				"name", CValidator.init(new CLldMacroStringValidator(),map(
					"messageEmpty", _("Empty name for group prototype."),
					"messageMacro", _("Name for group prototype \"%1$s\" must contain macros.")
				))
			),
			"required", array("name"),
			"messageUnsupported", _("Unsupported parameter \"%1$s\" for group prototype.")
		);
	}

	/**
	 * Returns the parameters for creating a group link validator.
	 *
	 * @return array
	 */
	protected Map getGroupLinkSchema() {
		return map(
			"validators", map(
				"groupid", CValidator.init(new CIdValidator(),map(
					"messageEmpty", _("No host group ID for group prototype."),
					"messageRegex", _("Incorrect host group ID for group prototype.")
				))
			),
			"required", array("groupid"),
			"messageUnsupported", _("Unsupported parameter \"%1$s\" for group prototype.")
		);
	}
	
	/**
	 * Creates the given host prototypes.
	 *
	 * @param array hostPrototypes
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> hostPrototypes) {
		for(Map hostPrototype : hostPrototypes) {
			// if the visible name is not set, use the technical name instead
			if (!isset(hostPrototype,"name") || rda_empty(trim(Nest.value(hostPrototype,"name").asString()))) {
				Nest.value(hostPrototype,"name").$(Nest.value(hostPrototype,"host").$());
			}

			if (isset(hostPrototype,"templates")) {
				Nest.value(hostPrototype,"templates").$(rda_toArray(Nest.value(hostPrototype,"templates").$()));
			}
		}
		validateCreate(hostPrototypes);

		// merge groups into group prototypes
		for(Map hostPrototype : hostPrototypes) {
			for(Map group : (CArray<Map>)Nest.value(hostPrototype,"groupLinks").asCArray()) {
				if(!isset(hostPrototype,"groupPrototypes")){
					Nest.value(hostPrototype,"groupPrototypes").$(array());
				}
				Nest.value(hostPrototype,"groupPrototypes").asCArray().add(group);
			}
			unset(hostPrototype,"groupLinks");
		}

		hostPrototypes = createReal(hostPrototypes);
		inherit(hostPrototypes);

		return map("hostids", rda_objectValues(hostPrototypes, "hostid").valuesAsLong());
	}
	
	/**
	 * Creates the host prototypes and inherits them to linked hosts and templates.
	 *
	 * @param array hostPrototypes
	 *
	 * @return array	an array of host prototypes with host IDs
	 */
	protected CArray<Map> createReal(CArray<Map> hostPrototypes) {
		for(Map hostPrototype : hostPrototypes) {
			Nest.value(hostPrototype,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
		}

		// save the host prototypes
		CArray<Long> hostPrototypeIds = insert(tableName(), hostPrototypes);

		CArray<Map> groupPrototypes = array();
		CArray<Map> hostPrototypeDiscoveryRules = array();
		CArray<Map> hostPrototypeInventory = array();
		for (Entry<Object, Map> e : hostPrototypes.entrySet()) {
		    Object key = e.getKey();
		    Map hostPrototype = e.getValue();
		    Object hostid = Nest.value(hostPrototypeIds,key).$();
			Nest.value(hostPrototypes,key,"hostid").$(hostid);
			Nest.value(hostPrototype,"hostid").$(hostid);

			// save group prototypes
			for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
				Nest.value(groupPrototype,"hostid").$(Nest.value(hostPrototype,"hostid").$());
				groupPrototypes.add(groupPrototype);
			}

			// discovery rules
			hostPrototypeDiscoveryRules.add(map(
				"hostid", Nest.value(hostPrototype,"hostid").$(),
				"parent_itemid", Nest.value(hostPrototype,"ruleid").$()
			));

			// inventory
			if (isset(hostPrototype,"inventory") && Nest.value(hostPrototype,"inventory").asBoolean()) {
				hostPrototypeInventory.add(map(
					"hostid", Nest.value(hostPrototype,"hostid").$(),
					"inventory_mode", Nest.value(hostPrototype,"inventory","inventory_mode").$()
				));
			}
		}

		// save group prototypes
		groupPrototypes = save("group_prototype", groupPrototypes);
		int i = 0;
		for(Map hostPrototype : hostPrototypes) {
			for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
				Nest.value(groupPrototype,"group_prototypeid").$(Nest.value(groupPrototypes,i,"group_prototypeid").$());
				i++;
			}
		}

		// link host prototypes to discovery rules
		insert("host_discovery", hostPrototypeDiscoveryRules, false);

		// save inventory
		insert("host_inventory", hostPrototypeInventory, false);

		// link templates
		for(Map hostPrototype : hostPrototypes) {
			if (isset(hostPrototype,"templates") && !empty(Nest.value(hostPrototype,"templates").$())) {
				link(rda_objectValues(Nest.value(hostPrototype,"templates").$(), "templateid"), (CArray)array(Nest.value(hostPrototype,"hostid").$()));
			}
		}
		return hostPrototypes;
	}
	
	/**
	 * Validates the input parameters for the update() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array hostPrototypes
	 * @param array dbHostPrototypes	array of existing host prototypes with hostids as keys
	 *
	 * @return void
	 */
	protected void validateUpdate(CArray<Map> hostPrototypes, CArray<Map> dbHostPrototypes) {
		// TODO: permissions should be checked using the dbHostPrototypes array
		checkHostPrototypePermissions(rda_objectValues(hostPrototypes, "hostid").valuesAsLong());

		hostPrototypes = extendFromObjects(rda_toHash(hostPrototypes, "hostid"), dbHostPrototypes, array(
			"host", "name"
		));

		// host prototype validator
		CPartialSchemaValidator hostPrototypeValidator = CValidator.init(new CPartialSchemaValidator(),getHostPrototypeSchema());
		hostPrototypeValidator.setValidator("hostid", null);

		// group validator
		CPartialSchemaValidator groupLinkValidator = CValidator.init(new CPartialSchemaValidator(),getGroupLinkSchema());
		groupLinkValidator.setValidator("group_prototypeid", CValidator.init(new CIdValidator(),map(
			"messageEmpty", _("Group prototype ID cannot be empty."),
			"messageRegex", _("Incorrect group prototype ID.")
		)));

		// group prototype validator
		CPartialSchemaValidator groupPrototypeValidator = CValidator.init(new CPartialSchemaValidator(),getGroupPrototypeSchema());
		groupPrototypeValidator.setValidator("group_prototypeid", CValidator.init(new CIdValidator(),map(
			"messageEmpty", _("Group prototype ID cannot be empty."),
			"messageRegex", _("Incorrect group prototype ID.")
		)));

		CArray groupPrototypeGroupIds = array();
		for(Map hostPrototype : hostPrototypes) {
			// host prototype
			hostPrototypeValidator.setObjectName(Nest.value(hostPrototype,"host").asString());
			checkPartialValidator(hostPrototype, hostPrototypeValidator);

			// groups
			if (isset(hostPrototype,"groupLinks")) {
				for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupLinks").asCArray()) {
					checkPartialValidator(groupPrototype, groupLinkValidator);

					Nest.value(groupPrototypeGroupIds,groupPrototype.get("groupid")).$(Nest.value(groupPrototype,"groupid").$());
				}
			}

			// group prototypes
			if (isset(hostPrototype,"groupPrototypes")) {
				for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
					groupPrototypeValidator.setObjectName(isset(groupPrototype,"name") ? Nest.value(groupPrototype,"name").asString() : "");
					checkPartialValidator(groupPrototype, groupPrototypeValidator);
				}
			}
		}

		checkHostGroupsPermissions(groupPrototypeGroupIds);

		// check if group prototypes use discovered host groups
		checkValidator(array_unique(groupPrototypeGroupIds).valuesAsLong(), CValidator.init(new CHostGroupNormalValidator(getSqlExecutor()),map(
			"message", _("Group prototype cannot be based on a discovered host group \"%1$s\".")
		)));

		// check for duplicates
		for(Map hostPrototype : hostPrototypes) {
			Nest.value(hostPrototype,"ruleid").$(Nest.value(dbHostPrototypes,hostPrototype.get("hostid"),"discoveryRule","itemid").$());
		}
		checkDuplicates(hostPrototypes);
	}

	/**
	 * Updates the given host prototypes.
	 *
	 * @param array hostPrototypes
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> hostPrototypes) {
		// check hostids before doing anything
		checkObjectIds(hostPrototypes, "hostid",
			_("No \"%1$s\" given for host prototype."),
			_("Empty host ID for host prototype."),
			_("Incorrect host prototype ID.")
		);

		// fetch updated objects from the DB
		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setOutput(new String[]{"host", "name"});
		hpoptions.setSelectGroupLinks(API_OUTPUT_EXTEND);
		hpoptions.setSelectGroupPrototypes(API_OUTPUT_EXTEND);
		hpoptions.setSelectDiscoveryRule(new String[]{"itemid"});
		hpoptions.setHostIds(rda_objectValues(hostPrototypes, "hostid").valuesAsLong());
		hpoptions.setEditable(true);
		hpoptions.setPreserveKeys(true);
		CArray<Map> dbHostPrototypes = get(hpoptions);

		for(Map hostPrototype : hostPrototypes) {
			if (isset(hostPrototype,"templates")) {
				Nest.value(hostPrototype,"templates").$(rda_toArray(Nest.value(hostPrototype,"templates").$()));
			}

			// if the visible name is not set, use the technical name instead
			if (isset(hostPrototype,"name") && rda_empty(trim(Nest.value(hostPrototype,"name").asString()))) {
				Nest.value(hostPrototype,"name").$(isset(hostPrototype,"host")
					? Nest.value(hostPrototype,"host").asString()
					: Nest.value(dbHostPrototypes,hostPrototype.get("hostid"),"host").asString());
			}
		}
		
		validateUpdate(hostPrototypes, dbHostPrototypes);

		// fetch missing data from the DB
		hostPrototypes = extendFromObjects(rda_toHash(hostPrototypes, "hostid"), dbHostPrototypes, array(
			"host", "groupLinks", "groupPrototypes"
		));
		for(Map hostPrototype : hostPrototypes) {
			Nest.value(hostPrototype,"ruleid").$(Nest.value(dbHostPrototypes,hostPrototype.get("hostid"),"discoveryRule","itemid").$());
		}

		// merge group links into group prototypes
		for(Map hostPrototype : hostPrototypes) {
			if (isset(hostPrototype,"groupLinks")) {
				for(Map group : (CArray<Map>)Nest.value(hostPrototype,"groupLinks").asCArray()) {
					if(!isset(hostPrototype,"groupPrototypes")){
						Nest.value(hostPrototype,"groupPrototypes").$(array());
					}
					Nest.value(hostPrototype,"groupPrototypes").asCArray().add(group);
				}
				unset(hostPrototype,"groupLinks");
			}
		}

		hostPrototypes = updateReal(hostPrototypes);
		inherit(hostPrototypes);

		return map("hostids", rda_objectValues(hostPrototypes, "hostid").valuesAsLong());
	}
	
	/**
	 * Updates the host prototypes and propagates the changes to linked hosts and templates.
	 *
	 * @param array hostPrototypes
	 *
	 * @return array
	 */
	protected CArray<Map> updateReal(CArray<Map> hostPrototypes) {
		// save the host prototypes
		for(Map hostPrototype : hostPrototypes) {
			updateByPk(tableName(), Nest.value(hostPrototype,"hostid").$(), hostPrototype);
		}

		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setOutput(new String[]{"hostid"});
		hpoptions.setSelectGroupLinks(API_OUTPUT_EXTEND);
		hpoptions.setSelectGroupPrototypes(API_OUTPUT_EXTEND);
		hpoptions.setSelectTemplates(new String[]{"templateid"});
		hpoptions.setSelectInventory(API_OUTPUT_EXTEND);
		hpoptions.setHostIds(rda_objectValues(hostPrototypes, "hostid").valuesAsLong());
		hpoptions.setPreserveKeys(true);
		CArray<Map> exHostPrototypes = get(hpoptions);

		// update related objects
		CArray<Map> inventoryCreate = array();
		CArray inventoryDeleteIds = array();
		for (Entry<Object, Map> e : hostPrototypes.entrySet()) {
		    Object key = e.getKey();
		    Map hostPrototype = e.getValue();
			Map exHostPrototype = exHostPrototypes.get(hostPrototype.get("hostid"));

			// group prototypes
			if (isset(hostPrototype,"groupPrototypes")) {
				for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
					Nest.value(groupPrototype,"hostid").$(Nest.value(hostPrototype,"hostid").$());
				}

				// save group prototypes
				CArray exGroupPrototypes = rda_toHash(
					array_merge(Nest.value(exHostPrototype,"groupLinks").asCArray(), Nest.value(exHostPrototype,"groupPrototypes").asCArray()),
					"group_prototypeid"
				);
				CArray<Map> modifiedGroupPrototypes = array();
				for(Map groupPrototype : (CArray<Map>)Nest.value(hostPrototype,"groupPrototypes").asCArray()) {
					if (isset(groupPrototype,"group_prototypeid")) {
						unset(exGroupPrototypes,groupPrototype.get("group_prototypeid"));
					}
					modifiedGroupPrototypes.add(groupPrototype);
				}
				if (!empty(exGroupPrototypes)) {
					deleteGroupPrototypes(array_keys(exGroupPrototypes));
				}
				Nest.value(hostPrototypes,key,"groupPrototypes").$(save("group_prototype", modifiedGroupPrototypes));
			}

			// templates
			if (isset(hostPrototype,"templates")) {
				CArray existingTemplateIds = rda_objectValues(Nest.value(exHostPrototype,"templates").$(), "templateid");
				CArray newTemplateIds = rda_objectValues(Nest.value(hostPrototype,"templates").$(), "templateid");
				unlink(array_diff(existingTemplateIds, newTemplateIds).valuesAsLong(), array(Nest.value(hostPrototype,"hostid").$()).valuesAsLong());
				link(array_diff(newTemplateIds, existingTemplateIds), (CArray)array(Nest.value(hostPrototype,"hostid").$()));
			}

			// inventory
			if (isset(Nest.value(hostPrototype,"inventory").$()) ) {
				CArray inventory = rda_array_mintersect(array("inventory_mode"), Nest.value(hostPrototype,"inventory").asCArray());
				Nest.value(inventory,"hostid").$(Nest.value(hostPrototype,"hostid").$());

				if (!empty(hostPrototype.get("inventory"))
					&& (!isset(Nest.value(hostPrototype,"inventory","inventory_mode").$()) || Nest.value(hostPrototype,"inventory","inventory_mode").asInteger() != HOST_INVENTORY_DISABLED)) {

					if (!empty(Nest.value(exHostPrototype,"inventory").$())) {
						update("host_inventory", array((Map)map(
							"values", inventory,
							"where", map("hostid", Nest.value(inventory,"hostid").$())
						)));
					} else {
						inventoryCreate.add(inventory);
					}

				} else {
					inventoryDeleteIds.add(Nest.value(hostPrototype,"hostid").$());
				}
			}
		}

		// save inventory
		insert("host_inventory", inventoryCreate, false);
		delete("host_inventory", (CArray)map("hostid", inventoryDeleteIds.valuesAsLong()));

		return hostPrototypes;
	}
	
	/**
	 * Updates the children of the host prototypes on the given hosts and propagates the inheritance to the child hosts.
	 *
	 * @param array hostPrototypes		array of host prototypes to inherit
	 * @param array hostids   			array of hosts to inherit to; if set to null, the children will be updated on all
	 *                              	child hosts
	 *
	 * @return bool
	 */
	protected boolean inherit(CArray<Map> hostPrototypes) {
		return inherit(hostPrototypes, null);
	}
	
	protected boolean inherit(CArray<Map> hostPrototypes, CArray hostids) {
		if (empty(hostPrototypes)) {
			return true;
		}

		// prepare the child host prototypes
		CArray<Map> newHostPrototypes = prepareInheritedObjects(hostPrototypes, hostids);
		if (empty(newHostPrototypes)) {
			return true;
		}

		CArray<Map> insertHostPrototypes = array();
		CArray<Map> updateHostPrototypes = array();
		for(Map newHostPrototype : newHostPrototypes) {
			if (isset(newHostPrototype,"hostid")) {
				updateHostPrototypes.add(newHostPrototype);
			} else {
				insertHostPrototypes.add(newHostPrototype);
			}
		}

		// save the new host prototypes
		if (!rda_empty(insertHostPrototypes)) {
			insertHostPrototypes = createReal(insertHostPrototypes);
		}

		if (!rda_empty(updateHostPrototypes)) {
			updateHostPrototypes = updateReal(updateHostPrototypes);
		}

		// propagate the inheritance to the children
		return inherit(array_merge(updateHostPrototypes, insertHostPrototypes));
	}

	/**
	 * Prepares and returns an array of child host prototypes, inherited from host prototypes hostPrototypes
	 * on the given hosts.
	 *
	 * Each host prototype must have the \"ruleid\" parameter set.
	 *
	 * @param array     hostPrototypes
	 * @param array		hostIds
	 *
	 * @return array 	an array of unsaved child host prototypes
	 */
	protected CArray<Map> prepareInheritedObjects(CArray<Map> hostPrototypes) {
		return prepareInheritedObjects(hostPrototypes, null);
	}
	
	protected CArray<Map> prepareInheritedObjects(CArray<Map> hostPrototypes, CArray hostIds) {
		// fetch the related discovery rules with their hosts
		CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
		droptions.setOutput(new String[]{"itemid", "hostid"});
		droptions.setSelectHosts(new String[]{"hostid"});
		droptions.setItemIds(rda_objectValues(hostPrototypes, "ruleid").valuesAsLong());
		droptions.setTemplated(true);
		droptions.setNopermissions(true);
		droptions.setPreserveKeys(true);
		CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);

		// fetch all child hosts to inherit to
		// do not inherit host prototypes on discovered hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "host", "status"});
		hoptions.setSelectParentTemplates(new String[]{"templateid"});
		hoptions.setTemplateIds(rda_objectValues(discoveryRules, "hostid").valuesAsLong());
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
		CArray<Map> chdHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		if (empty(chdHosts)) {
			return array();
		}

		// fetch the child discovery rules
		droptions = new CDiscoveryRuleGet();
		droptions.setOutput(new String[]{"itemid", "templateid", "hostid"});
		droptions.setPreserveKeys(true);
		droptions.setFilter("templateid", array_keys(discoveryRules).valuesAsString());
		CArray<Map> childDiscoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);

		// fetch child host prototypes and group them by discovery rule
		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setOutput(new String[]{"hostid", "host", "templateid"});
		hpoptions.setSelectGroupLinks(API_OUTPUT_EXTEND);
		hpoptions.setSelectGroupPrototypes(API_OUTPUT_EXTEND);
		hpoptions.setSelectDiscoveryRule(new String[]{"itemid"});
		hpoptions.setDiscoveryIds(rda_objectValues(childDiscoveryRules, "itemid").valuesAsLong());
		CArray<Map> childHostPrototypes = API.HostPrototype(this.idBean, this.getSqlExecutor()).get(hpoptions);
		for(Map childDiscoveryRule : childDiscoveryRules) {
			Nest.value(childDiscoveryRule,"hostPrototypes").$(array());
		}
		for(Map childHostPrototype : childHostPrototypes) {
			Object discoveryRuleId = Nest.value(childHostPrototype,"discoveryRule","itemid").$();
			unset(childHostPrototype,"discoveryRule");
			Nest.value(childDiscoveryRules,discoveryRuleId,"hostPrototypes").asCArray().add(childHostPrototype);
		}

		// match each discovery that the parent host prototypes belong to to the child discovery rule for each host
		CArray<CArray<Map>> discoveryRuleChildren = array();
		for(Map childRule : childDiscoveryRules) {
			if(!isset(discoveryRuleChildren,childRule.get("templateid"))){
				Nest.value(discoveryRuleChildren,childRule.get("templateid")).$(array());
			}
			Nest.value(discoveryRuleChildren,childRule.get("templateid"),childRule.get("hostid")).$(Nest.value(childRule,"itemid").$());
		}

		CArray<Map> newHostPrototypes = array();
		for(Map host : chdHosts) {
			Object hostId = Nest.value(host,"hostid").$();

			// skip items not from parent templates of current host
			CArray templateIds = rda_toHash(Nest.value(host,"parentTemplates").$(), "templateid");
			CArray<Map> parentHostPrototypes = array();
			for (Entry<Object, Map> e : hostPrototypes.entrySet()) {
			    Object inum = e.getKey();
			    Map parentHostPrototype = e.getValue();
				Object parentTemplateId = Nest.value(discoveryRules,parentHostPrototype.get("ruleid"),"hostid").$();

				if (isset(templateIds,parentTemplateId)) {
					Nest.value(parentHostPrototypes,inum).$(parentHostPrototype);
				}
			}

			for(Map parentHostPrototype : parentHostPrototypes) {
				Object childDiscoveryRuleId = Nest.value(discoveryRuleChildren,parentHostPrototype.get("ruleid"),hostId).$();

				// check if the child discovery rule already has host prototypes
				CArray<Map> exHostPrototypes = Nest.value(childDiscoveryRules,childDiscoveryRuleId,"hostPrototypes").asCArray();
				Map exHostPrototype = null;
				if (!empty(exHostPrototypes)) {
					CArray<Map> exHostPrototypesHosts = rda_toHash(exHostPrototypes, "host");
					CArray<Map> exHostPrototypesTemplateIds = rda_toHash(exHostPrototypes, "templateid");

					// look for an already created inherited host prototype
					// if one exists - update it
					if (isset(exHostPrototypesTemplateIds,parentHostPrototype.get("hostid"))) {
						exHostPrototype = exHostPrototypesTemplateIds.get(parentHostPrototype.get("hostid"));

						// check if there's a host prototype on the target host with the same host name but from a different template
						// or no template
						if (isset(exHostPrototypesHosts,parentHostPrototype.get("host"))
							&& !idcmp(Nest.value(exHostPrototypesHosts,parentHostPrototype.get("host"),"templateid").$(), Nest.value(parentHostPrototype,"hostid").$())) {
							Map params = new HashMap();
							params.put("itemid", Nest.value(exHostPrototype,"discoveryRule","itemid").$());
							Map discoveryRule = DBfetch(DBselect(getSqlExecutor(),"SELECT i.name FROM items i WHERE i.itemid=#{itemid}",params));
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host prototype \"%1$s\" already exists on \"%2$s\".", Nest.value(parentHostPrototype,"host").$(), Nest.value(discoveryRule,"name").$()));
						}
					}

					// look for a host prototype with the same host name
					// if one exists - convert it to an inherited host prototype
					if (isset(exHostPrototypesHosts,parentHostPrototype.get("host"))) {
						exHostPrototype = exHostPrototypesHosts.get(parentHostPrototype.get("host"));

						// check that this host prototype is not inherited from a different template
						if (Nest.value(exHostPrototype,"templateid").asLong() > 0 && !idcmp(Nest.value(exHostPrototype,"templateid").$(), Nest.value(parentHostPrototype,"hostid").$())) {
							Map params = new HashMap();
							params.put("itemid", Nest.value(exHostPrototype,"discoveryRule","itemid").$());
							Map discoveryRule = DBfetch(DBselect(getSqlExecutor(),"SELECT i.name FROM items i WHERE i.itemid=#{itemid}",params));
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host prototype \"%1$s\" already exists on \"%2$s\", inherited from another template.", Nest.value(parentHostPrototype,"host").$(), Nest.value(discoveryRule,"name").$()));
						}
					}
				}

				// copy host prototype
				Map newHostPrototype = Clone.deepcopy(parentHostPrototype);
				Nest.value(newHostPrototype,"ruleid").$(Nest.value(discoveryRuleChildren,parentHostPrototype.get("ruleid"),hostId).$());
				Nest.value(newHostPrototype,"templateid").$(Nest.value(parentHostPrototype,"hostid").$());

				// update an existing inherited host prototype
				if (!empty(exHostPrototype)) {
					// look for existing group prototypes to update
					CArray exGroupPrototypesByTemplateId = rda_toHash(Nest.value(exHostPrototype,"groupPrototypes").$(), "templateid");
					CArray exGroupPrototypesByName = rda_toHash(Nest.value(exHostPrototype,"groupPrototypes").$(), "name");
					CArray exGroupPrototypesByGroupId = rda_toHash(Nest.value(exHostPrototype,"groupLinks").$(), "groupid");

					// look for a group prototype that can be updated
					for(Map groupPrototype : (CArray<Map>)Nest.value(newHostPrototype,"groupPrototypes").asCArray()) {
						// updated an inherited item prototype by templateid
						if (isset(exGroupPrototypesByTemplateId,groupPrototype.get("group_prototypeid"))) {
							Nest.value(groupPrototype,"group_prototypeid").$(Nest.value(exGroupPrototypesByTemplateId,groupPrototype.get("group_prototypeid"),"group_prototypeid").$());
						}
						// updated an inherited item prototype by name
						else if (isset(groupPrototype,"name") && !rda_empty(Nest.value(groupPrototype,"name").$())
								&& isset(exGroupPrototypesByName,groupPrototype.get("name"))) {

							Nest.value(groupPrototype,"templateid").$(Nest.value(groupPrototype,"group_prototypeid").$());
							Nest.value(groupPrototype,"group_prototypeid").$(Nest.value(exGroupPrototypesByName,groupPrototype.get("name"),"group_prototypeid").$());
						}
						// updated an inherited item prototype by group ID
						else if (isset(groupPrototype,"groupid") && !empty(Nest.value(groupPrototype,"groupid").$())
								&& isset(exGroupPrototypesByGroupId,groupPrototype.get("groupid"))) {

							Nest.value(groupPrototype,"templateid").$(Nest.value(groupPrototype,"group_prototypeid").$());
							Nest.value(groupPrototype,"group_prototypeid").$(Nest.value(exGroupPrototypesByGroupId,groupPrototype.get("groupid"),"group_prototypeid").$());
						}
						// create a new child group prototype
						else {
							Nest.value(groupPrototype,"templateid").$(Nest.value(groupPrototype,"group_prototypeid").$());
							unset(groupPrototype,"group_prototypeid");
						}

						unset(groupPrototype,"hostid");
					}

					Nest.value(newHostPrototype,"hostid").$(Nest.value(exHostPrototype,"hostid").$());
				}
				// create a new inherited host prototype
				else {
					for(Map groupPrototype : (CArray<Map>)Nest.value(newHostPrototype,"groupPrototypes").asCArray()) {
						Nest.value(groupPrototype,"templateid").$(Nest.value(groupPrototype,"group_prototypeid").$());
						unset(groupPrototype,"group_prototypeid");
						unset(groupPrototype,"hostid");
					}
					unset(newHostPrototype,"hostid");
				}
				newHostPrototypes.add(newHostPrototype);
			}
		}

		return newHostPrototypes;
	}
	
	/**
	 * Inherits all host prototypes from the templates given in \"templateids\" to hosts or templates given in \"hostids\".
	 *
	 * @param array data
	 *
	 * @return bool
	 */
	public boolean syncTemplates(Map data) {
		Nest.value(data,"templateids").$(rda_toArray(Nest.value(data,"templateids").$()));
		Nest.value(data,"hostids").$(rda_toArray(Nest.value(data,"hostids").$()));

		CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
		droptions.setOutput(new String[]{"itemid"});
		droptions.setHostIds(Nest.array(data,"templateids").asLong());
		CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setDiscoveryIds(rda_objectValues(discoveryRules, "itemid").valuesAsLong());
		hpoptions.setPreserveKeys(true);
		hpoptions.setOutput(API_OUTPUT_EXTEND);
		hpoptions.setSelectGroupLinks(API_OUTPUT_EXTEND);
		hpoptions.setSelectGroupPrototypes(API_OUTPUT_EXTEND);
		hpoptions.setSelectTemplates(new String[]{"templateid"});
		hpoptions.setSelectDiscoveryRule(new String[]{"itemid"});
		CArray<Map> hostPrototypes = get(hpoptions);

		for(Map hostPrototype : hostPrototypes) {
			// merge group links into group prototypes
			for(Object group : Nest.value(hostPrototype,"groupLinks").asCArray()) {
				if(!isset(hostPrototype,"groupPrototypes")){
					Nest.value(hostPrototype,"groupPrototypes").$(array());
				}
				Nest.value(hostPrototype,"groupPrototypes").asCArray().add(group);
			}
			unset(hostPrototype,"groupLinks");

			// the ID of the discovery rule must be passed in the \"ruleid\" parameter
			Nest.value(hostPrototype,"ruleid").$(Nest.value(hostPrototype,"discoveryRule","itemid").$());
			unset(hostPrototype,"discoveryRule");
		}

		inherit(hostPrototypes, Nest.value(data,"hostids").asCArray());
		return true;
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array hostPrototypeIds
	 * @param bool 	nopermissions
	 *
	 * @return void
	 */
	protected void validateDelete(boolean nopermissions, Long... hostPrototypeIds) {
		if (empty(hostPrototypeIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		if (!nopermissions) {
			checkHostPrototypePermissions(hostPrototypeIds);
			checkNotInherited(hostPrototypeIds);
		}
	}
	
	@Override
	public CArray<Long[]> delete(Long... hostPrototypeIds) {
		return delete(false, CArray.valueOf(hostPrototypeIds));
	}
	
	/**
	 * Delete host prototypes.
	 *
	 * @param string|array 	hostPrototypeIds
	 * @param bool 			nopermissions		if set to true, permission and template checks will be skipped
	 *
	 * @return array
	 */
	public CArray<Long[]> delete(boolean nopermissions, CArray hostPrototypeIds) {
		validateDelete(nopermissions, hostPrototypeIds.valuesAsLong());

		// include child IDs
		CArray parentHostPrototypeIds = Clone.deepcopy(hostPrototypeIds);
		CArray childHostPrototypeIds = array();
		SqlBuilder sqlParts = null;
		CArray<Map> query = null;
		do {
			sqlParts = new SqlBuilder();
			query = DBselect(getSqlExecutor(),
					"SELECT h.hostid FROM hosts h WHERE "+sqlParts.dual.dbConditionInt("h.templateid", parentHostPrototypeIds.valuesAsLong()),
					sqlParts.getNamedParams()
			);
			parentHostPrototypeIds = array();
			for(Map hostPrototype : query) {
				parentHostPrototypeIds.add(Nest.value(hostPrototype,"hostid").$());
				childHostPrototypeIds.add(Nest.value(hostPrototype,"hostid").$());
			}
		} while (!empty(parentHostPrototypeIds));

		hostPrototypeIds = array_merge(hostPrototypeIds, childHostPrototypeIds);

		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setHostIds(hostPrototypeIds.valuesAsLong());
		hpoptions.setOutput(new String[]{"host"});
		hpoptions.setSelectGroupPrototypes(new String[]{"group_prototypeid"});
		hpoptions.setSelectParentHost(new String[]{"host"});
		hpoptions.setNopermissions(true);
		CArray<Map> deleteHostPrototypes = get(hpoptions);

		// delete discovered hosts
		sqlParts = new SqlBuilder();
		CArray<Map> discoveredHosts = DBselect(getSqlExecutor(),
			"SELECT hostid FROM host_discovery WHERE "+sqlParts.dual.dbConditionInt("parent_hostid", hostPrototypeIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		if (!empty(discoveredHosts)) {
			API.Host(this.idBean, this.getSqlExecutor()).delete(true, rda_objectValues(discoveredHosts, "hostid").valuesAsLong());
		}

		// delete group prototypes and discovered groups
		CArray groupPrototypeIds = array();
		for(Map groupPrototype:deleteHostPrototypes) {
			for(Map grpPrototype : (CArray<Map>)Nest.value(groupPrototype,"groupPrototypes").asCArray()) {
				groupPrototypeIds.add(Nest.value(grpPrototype,"group_prototypeid").$());
			}
		}
		deleteGroupPrototypes(groupPrototypeIds);

		// delete host prototypes
		delete(tableName(), (CArray)map("hostid", hostPrototypeIds.valuesAsLong()));

		return map("hostids", hostPrototypeIds.valuesAsLong());
	}

	/**
	 * Returns true if all of the given objects are available for reading.
	 *
	 * @param ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CHostPrototypeGet options = new CHostPrototypeGet();
		options.setHostIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	/**
	 * Returns true if all of the given objects are available for writing.
	 *
	 * @param ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CHostPrototypeGet options = new CHostPrototypeGet();
		options.setHostIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}
	
	@Override
	protected CArray<Map> link(CArray<Long> templateIds, CArray<Long> targetIds) {
		if (!isWritable(targetIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
		return super.link(templateIds, targetIds);
	}

	/**
	 * Checks if the current user has access to the given LLD rules.
	 *
	 * @throws APIException if the user doesn't have write permissions for the given LLD rules
	 *
	 * @param array discoveryRuleIds
	 */
	protected void checkDiscoveryRulePermissions(CArray discoveryRuleIds) {
		if (!API.DiscoveryRule(this.idBean, this.getSqlExecutor()).isWritable(discoveryRuleIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}

	/**
	 * Checks if the current user has access to the given host groups.
	 *
	 * @throws APIException if the user doesn't have write permissions for the given host groups
	 *
	 * @param array hostGroupIds
	 */
	protected void checkHostGroupsPermissions(CArray hostGroupIds) {
		if (!API.HostGroup(this.idBean, this.getSqlExecutor()).isWritable(hostGroupIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}

	/**
	 * Checks if the current user has access to the given host prototypes.
	 *
	 * @throws APIException if the user doesn't have write permissions for the host prototypes.
	 *
	 * @param array hostPrototypeIds
	 */
	protected void checkHostPrototypePermissions(Long... hostPrototypeIds) {
		if (!isWritable(hostPrototypeIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}

	/**
	 * Checks if the given host prototypes are not inherited from a template.
	 *
	 * @throws APIException 	if at least one host prototype is inherited
	 *
	 * @param array hostPrototypeIds
	 */
	protected void checkNotInherited(Long... hostPrototypeIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect(getSqlExecutor(),
				"SELECT hostid FROM hosts h WHERE h.templateid>0 AND "+sqlParts.dual.dbConditionInt("h.hostid", hostPrototypeIds), 
				1,
				sqlParts.getNamedParams()
		);
		
		Map hostPrototype = DBfetch(query);

		if (!empty(hostPrototype)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Cannot delete templated host prototype."));
		}
	}

	/**
	 * Checks if host prototypes with the same technical or visible names already exist on the same LLD rule.
	 *
	 * Each host prototype must have the host, name and ruleid properties defined.
	 *
	 * @throws APIException 	if a host prototype with the same name or technical name exists
	 * 							either in the given array or in the database.
	 *
	 * @param array hostPrototypes
	 */
	protected void checkDuplicates(CArray<Map> hostPrototypes) {
		// check host name duplicates
		CCollectionValidator collectionValidator = CValidator.init(new CCollectionValidator(),map(
			"empty", true,
			"uniqueField", "host",
			"uniqueField2", "ruleid",
			"messageDuplicate", _("Host prototype with host name \"%1$s\" already exists.")
		));
		checkValidator(hostPrototypes, collectionValidator);
		checkExistingHostPrototypes(hostPrototypes, "host",
			_("Host prototype with host name \"%1$s\" already exists in discovery rule \"%2$s\".")
		);

		// check visible name duplicates
		collectionValidator.uniqueField = "name";
		collectionValidator.messageDuplicate = _("Host prototype with visible name \"%1$s\" already exists.");
		checkValidator(hostPrototypes, collectionValidator);
		checkExistingHostPrototypes(hostPrototypes, "name",
			_("Host prototype with visible name \"%1$s\" already exists in discovery rule \"%2$s\".")
		);
	}

	/**
	 * Check if a host with the same value in field already exists on an LLD rule.
	 * If host prototypes have host IDs it will check for existing prototypes with different host IDs.
	 *
	 * @throw APIException
	 *
	 * @param array hostPrototypes
	 * @param string field				name of the field to check uniqueness by
	 * @param string error				error message in case duplicates are found
	 */
	protected void checkExistingHostPrototypes(CArray<Map> hostPrototypes, String field, String error) {
		CArray<CArray> valuesByDiscoveryRuleId = array();
		CArray hostIds = array();
		for(Map hostPrototype : hostPrototypes) {
			if(!isset(valuesByDiscoveryRuleId,hostPrototype.get("ruleid"))){
				Nest.value(valuesByDiscoveryRuleId,hostPrototype.get("ruleid")).$(array());
			}
			Nest.value(valuesByDiscoveryRuleId,hostPrototype.get("ruleid")).asCArray().add(hostPrototype.get(field));
			if (isset(hostPrototype,"hostid")) {
				hostIds.add(Nest.value(hostPrototype,"hostid").$());
			}
		}

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<String> sqlWhere = array();
		for (Entry<Object, CArray> e : valuesByDiscoveryRuleId.entrySet()) {
		    Object discoveryRuleId = e.getKey();
		    CArray values = e.getValue();
			sqlWhere.add("(hd.parent_itemid="+discoveryRuleId+" AND "+sqlParts.dual.dbConditionString("h."+field, values.valuesAsString())+")");
		}

		if (!empty(sqlWhere)) {
			String sql = "SELECT i.name as discovery_name,h."+field+
				" FROM hosts h,host_discovery hd,items i"+
				" WHERE h.tenantid=hd.tenantid "+
				" AND hd.tenantid=i.tenantid "+
				" AND h.hostid=hd.hostid AND hd.parent_itemid=i.itemid AND ("+implode(" OR ", sqlWhere)+")";

			// if we update existing items we need to exclude them from result.
			if (!empty(hostIds)) {
				sql += " AND "+sqlParts.dual.dbConditionInt("h.hostid", hostIds.valuesAsLong(), true);
			}
			CArray<Map> query = DBselect(getSqlExecutor(), sql, 1, sqlParts.getNamedParams());
			Map row = DBfetch(query);
			if(!empty(row)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(error, Nest.value(row,field).asString(), Nest.value(row,"discovery_name").$()));
			}
		}
	}
	
	@Override
	protected void applyQueryFilterOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryFilterOptions(tableName, tableAlias, params, sqlParts);
		
		// do not return host prototypes from discovered hosts
		sqlParts.from.put("host_discovery hd");
		sqlParts.from.put("items i");
		sqlParts.from.put("hosts ph");
		sqlParts.where.put(this.fieldId("hostid")+"=hd.hostid");
		sqlParts.where.put("hd.tenantid=i.tenantid");
		sqlParts.where.put("hd.parent_itemid=i.itemid");
		sqlParts.where.put("i.tenantid=ph.tenantid");
		sqlParts.where.put("i.hostid=ph.hostid");
		sqlParts.where.put("ph.flags="+Defines.RDA_FLAG_DISCOVERY_NORMAL);
		
		if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;

			sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM "+
					"host_discovery hd,items i,hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.tenantid=hgg.tenantid"+ 
						" AND r.id=hgg.groupid"+
						" AND "+sqlParts.dual.dbConditionInt("r.groupid", getUserGroupsByUserId(this.idBean, getSqlExecutor(),Nest.value(CWebUser.data(),"userid").asString()).toArray(new Long[0]))+
				" WHERE h.tenantid=hd.tenantid"+ 
					" AND hd.tenantid=i.tenantid"+
					" AND i.tenantid=hgg.tenantid"+
					" AND h.hostid=hd.hostid"+
					" AND hd.parent_itemid=i.itemid"+
					" AND i.hostid=hgg.hostid"+
				" GROUP BY hgg.hostid"+
				" HAVING MIN(r.permission)>"+PERM_DENY+
				" AND MAX(r.permission)>="+permission+
				")");
		}
		
		// discoveryids
		if(!is_null(Nest.value(params,"discoveryids").$())) {
			sqlParts.where.dbConditionInt("hd.parent_itemid", Nest.array(params,"discoveryids").asLong());
			
			if(!is_null(params.getGroupCount())) {
				sqlParts.group.put("hd", "hd.parent_itemid");
			}
		}

		// inherited
		if(!is_null(Nest.value(params,"inherited").$())) {
			sqlParts.where.put(Nest.value(params,"inherited").asBoolean()? "h.templateid IS NOT NULL" : "h.templateid IS NULL");
		}		
	}

	@Override
	protected void addRelatedObjects(CHostPrototypeGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] hostPrototypeIds = result.keysAsLong();
		
		// adding discovery rule
		if (!is_null(params.getSelectDiscoveryRule()) && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())) {
			CRelationMap relationMap = this.createRelationMap(result, "hostid", "parent_itemid", "host_discovery");
			
			CDiscoveryRuleGet aparams = new CDiscoveryRuleGet();
			aparams.setOutput(params.getSelectDiscoveryRule());
			aparams.setItemIds(relationMap.getRelatedLongIds());
			aparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.DiscoveryRule(this.idBean, getSqlExecutor()).get(aparams);
			relationMap.mapOne(result, datas, "discoveryRule");
		}
		
		// adding group links
		if (!is_null(params.getSelectGroupLinks()) && !API_OUTPUT_COUNT.equals(params.getSelectGroupLinks())) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("hg.group_prototypeid");
			sqlParts.select.put("hg.hostid");
			sqlParts.from.put("group_prototype hg");
			applyQueryTenantOptions("group_prototype", "hg", params, sqlParts);
			sqlParts.where.dbConditionInt("hg.hostid", hostPrototypeIds);
			sqlParts.where.put("hg.groupid!=0");
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "group_prototypeid", "hostid", "group_prototypeid");
			
			CSupportGet iparams = new CSupportGet();
			iparams.setOutput(params.getSelectGroupLinks());
			iparams.setGroupPrototypeIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> groupPrototypes = this.select("group_prototype", iparams);
			for(Map row: groupPrototypes) {
				row.remove("name");
			}
			relationMap.mapMany(result, datas, "groupLinks");
		}

		// adding group prototypes
		if (!is_null(params.getSelectGroupPrototypes()) && !API_OUTPUT_COUNT.equals(params.getSelectGroupPrototypes())) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("hg.group_prototypeid");
			sqlParts.select.put("hg.hostid");
			sqlParts.from.put("group_prototype hg");
			applyQueryTenantOptions("group_prototype", "hg", params, sqlParts);
			sqlParts.where.dbConditionInt("hg.hostid", hostPrototypeIds);
			sqlParts.where.put("hg.name NOT LIKE "+sqlParts.marshalParam(""));
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "group_prototypeid", "hostid", "group_prototypeid");
			
			CSupportGet iparams = new CSupportGet();
			iparams.setOutput(params.getSelectGroupPrototypes());
			iparams.setGroupPrototypeIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> groupPrototypes = this.select("group_prototype", iparams);
			for(Map row: groupPrototypes) {
				row.remove("groupid");
			}
			relationMap.mapMany(result, datas, "groupPrototypes");
		}

		// adding host
		if (!is_null(params.getSelectParentHost()) && !API_OUTPUT_COUNT.equals(params.getSelectParentHost())) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("hd.hostid");
			sqlParts.select.put("i.hostid AS parent_hostid");
			sqlParts.from.put("host_discovery hd");
			sqlParts.from.put("items i");
			applyQueryTenantOptions("host_discovery", "hd", params, sqlParts);
			sqlParts.where.dbConditionInt("hd.hostid", hostPrototypeIds);
			sqlParts.where.put("hd.tenantid=i.tenantid");
			sqlParts.where.put("hd.parent_itemid=i.itemid");
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "hostid", "parent_hostid");
			
			CHostGet iparams = new CHostGet();
			iparams.setOutput(params.getSelectParentHost());
			iparams.setHostIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> hosts = API.Host(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapOne(result, hosts, "parentHost");
		}

		// adding templates
		if (!is_null(params.getSelectTemplates())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectTemplates())) {
				CRelationMap relationMap = this.createRelationMap(result, "hostid", "templateid", "hosts_templates");
				
				CTemplateGet aparams = new CTemplateGet();
				aparams.setOutput(params.getSelectTemplates());
				aparams.setTemplateIds(relationMap.getRelatedLongIds());
				aparams.setPreserveKeys(true);
				
				CArray<Map> datas = API.Template(this.idBean, getSqlExecutor()).get(aparams);
				relationMap.mapMany(result, datas, "templates");
			} else {
				CTemplateGet aparams = new CTemplateGet();
				aparams.setHostIds(hostPrototypeIds);
				aparams.setCountOutput(true);
				aparams.setGroupCount(true);
				
				CArray<Map> mdatas = API.Template(this.idBean, getSqlExecutor()).get(aparams);
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					Map v = entry.getValue();
					Map row = mdatas.get(hostid);
					v.put("templates", isset(row)? row.get("rowscount"): 0);
				}
			}
		}

		// adding inventory
		if (!is_null(params.getSelectInventory())) {
			CRelationMap relationMap = this.createRelationMap(result, "hostid", "hostid");
			
			// only allow to retrieve the hostid and inventory_mode fields
			List<String> output = new ArrayList<String>();
			if(this.outputIsRequested("hostid", params.getSelectInventory())) {
				output.add("hostid");
			}
			if(this.outputIsRequested("inventory_mode", params.getSelectInventory())) {
				output.add("inventory_mode");
			}
			
			CParamGet aparams = new CParamGet();
			aparams.setOutput(output.toArray(new String[0]));
			aparams.setFilter("hostid", hostPrototypeIds);
			
			CArray<Map> inventory = this.select("host_inventory", aparams);
			relationMap.mapOne(result, inventory, "inventory");
		}
	}
	
	/**
	 * Deletes the given group prototype and all discovered groups.
	 * Deletes also group prototype children.
	 *
	 * @param array groupPrototypeIds
	 */
	protected void deleteGroupPrototypes(CArray groupPrototypeIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		// delete child group prototypes
		CArray<Map> groupPrototypeChildren = DBselect(getSqlExecutor(),
			"SELECT gp.group_prototypeid FROM group_prototype gp WHERE "+sqlParts.dual.dbConditionInt("templateid", groupPrototypeIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		if (!empty(groupPrototypeChildren)) {
			deleteGroupPrototypes(rda_objectValues(groupPrototypeChildren, "group_prototypeid"));
		}

		// delete discovered groups
		sqlParts = new SqlBuilder();
		CArray<Map> hostGroups = DBselect(getSqlExecutor(),
			"SELECT groupid FROM group_discovery WHERE "+sqlParts.dual.dbConditionInt("parent_group_prototypeid", groupPrototypeIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		if (!empty(hostGroups)) {
			API.HostGroup(this.idBean, this.getSqlExecutor()).delete(true,rda_objectValues(hostGroups, "groupid").valuesAsLong());
		}

		// delete group prototypes
		delete("group_prototype", (CArray)map("group_prototypeid", groupPrototypeIds.valuesAsLong()));
	}
}
