package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CScreenItemGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CScreenDAO extends CCoreLongKeyDAO<CScreenGet> {

	public CScreenDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "screens", "s", new String[]{"screenid", "name"});
	}
	
	public CScreenDAO(IIdentityBean idBean, SQLExecutor executor, String tableName, String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}

	/**
	 * Get screen data.
	 *
	 * @param array  _options
	 * @param bool   _options['editable']		only with read-write permission. Ignored for SuperAdmins
	 * @param int    _options['count']			count Hosts, returned column name is rowscount
	 * @param string _options['pattern']		search hosts by pattern in host names
	 * @param int    _options['limit']			limit selection
	 * @param string _options['order']			deprecated parameter (for now)
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CScreenGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("screens", "s.screenid");
		sqlParts.from.put("screens", "screens s");
		sqlParts.where.put("template", "s.templateid IS NULL");
		
		// screenids
		if (!is_null(params.getScreenIds())) {
			sqlParts.where.dbConditionInt("s.screenid",params.getScreenIds());
		}

		// screenitemids
		if (!is_null(params.getScreenItemIds())) {
			if (!API_OUTPUT_EXTEND.equals(params.getOutput())) {
				sqlParts.select.put("screenitemid","si.screenitemid");
			}			
			sqlParts.from.put("screens_items","screens_items si");
			sqlParts.where.put("ssi.tenantid","si.tenantid=s.tenantid");
			sqlParts.where.put("ssi","si.screenid=s.screenid");
			sqlParts.where.dbConditionInt("si.screenitemid",params.getScreenItemIds());
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("screens s", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("screens s", params, sqlParts);
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}

		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Long> screenIds = array();
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
				Long id = (Long)row.get("screenid");
				screenIds.put(id, id);
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				if (isset(row.get("screenitemid")) && is_null(params.getSelectScreenItems())) {
					if (!isset(result.get(id),"screenitems")) {
						result.get(id).put("screenitems", new CArray());
					}
					((CArray)result.get(id).get("screenitems")).add(map("screenitemid", row.get("screenitemid")));
				}
				result.get(id).putAll(row);
			}
		}
		
		// editable + PERMISSION CHECK
		int userType = Nest.value(userData(),"type").asInteger();
		if (userType == USER_TYPE_SUPER_ADMIN || params.getNopermissions()) {
		} else if(!empty(result)){
			CArray groupsToCheck = array();
			CArray hostsToCheck = array();
			CArray graphsToCheck = array();
			CArray itemsToCheck = array();
			CArray mapsToCheck = array();
			CArray screensToCheck = array();
			CArray<Map> screensItems = array();

			sqlParts = new SqlBuilder();
			CArray<Map> dbScreenItems = DBselect(getSqlExecutor(),
					"SELECT si.* FROM screens_items si WHERE "+sqlParts.dual.dbConditionInt("si.screenid", screenIds.valuesAsLong()),
					sqlParts.getNamedParams());

			for (Map screenItem : dbScreenItems) {
				Nest.value(screensItems,screenItem.get("screenitemid")).$(screenItem);

				if (!empty(Nest.value(screenItem,"resourceid").$())) {
					switch (Nest.value(screenItem,"resourcetype").asInteger()) {
						case SCREEN_RESOURCE_HOSTS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
						case SCREEN_RESOURCE_DATA_OVERVIEW:
						case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
							groupsToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
						case SCREEN_RESOURCE_HOST_TRIGGERS:
							hostsToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
						case SCREEN_RESOURCE_GRAPH:
							graphsToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
						case SCREEN_RESOURCE_SIMPLE_GRAPH:
						case SCREEN_RESOURCE_PLAIN_TEXT:
							itemsToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
						case SCREEN_RESOURCE_MAP:
							mapsToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
						case SCREEN_RESOURCE_SCREEN:
							screensToCheck.add(Nest.value(screenItem,"resourceid").$());
							break;
					}
				}
			}

			groupsToCheck = array_unique(groupsToCheck);
			hostsToCheck = array_unique(hostsToCheck);
			graphsToCheck = array_unique(graphsToCheck);
			itemsToCheck = array_unique(itemsToCheck);
			mapsToCheck = array_unique(mapsToCheck);
			screensToCheck = array_unique(screensToCheck);

			// group
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(groupsToCheck.valuesAsLong());
			hgoptions.setEditable(params.getEditable());
			CArray<Map> allowedGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);
			allowedGroups = rda_objectValues(allowedGroups, "groupid");

			// host
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hostsToCheck.valuesAsLong());
			hoptions.setEditable(params.getEditable());
			CArray<Map> allowedHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
			allowedHosts = rda_objectValues(allowedHosts, "hostid");

			// graph
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(graphsToCheck.valuesAsLong());
			goptions.setEditable(params.getEditable());
			CArray<Map> allowedGraphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);
			allowedGraphs = rda_objectValues(allowedGraphs, "graphid");

			// item
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setItemIds(itemsToCheck.valuesAsLong());
			ioptions.setWebItems(true);
			ioptions.setEditable(params.getEditable());
			CArray<Map> allowedItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
			allowedItems = rda_objectValues(allowedItems, "itemid");

			// map
			CMapGet moptions = new CMapGet();
			moptions.setSysmapIds(mapsToCheck.valuesAsLong());
			moptions.setEditable(params.getEditable());
			CArray<Map> allowedMaps = API.Map(this.idBean, this.getSqlExecutor()).get(moptions);
			allowedMaps = rda_objectValues(allowedMaps, "sysmapid");

			// screen
			CScreenGet soptions = new CScreenGet();
			soptions.setScreenIds(screensToCheck.valuesAsLong());
			soptions.setEditable(params.getEditable());
			CArray<Map> allowedScreens = API.Screen(this.idBean, this.getSqlExecutor()).get(soptions);
			allowedScreens = rda_objectValues(allowedScreens, "screenid");

			CArray<Long> restrGroups = array_diff(groupsToCheck, allowedGroups);
			CArray<Long> restrHosts = array_diff(hostsToCheck, allowedHosts);
			CArray<Long> restrGraphs = array_diff(graphsToCheck, allowedGraphs);
			CArray<Long> restrItems = array_diff(itemsToCheck, allowedItems);
			CArray<Long> restrMaps = array_diff(mapsToCheck, allowedMaps);
			CArray<Long> restrScreens = array_diff(screensToCheck, allowedScreens);

			// group
			for(Long resourceId : restrGroups) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0
							&& uint_in_array(Nest.value(screenItem,"resourcetype").asInteger(), array(
								SCREEN_RESOURCE_HOSTS_INFO, SCREEN_RESOURCE_TRIGGERS_INFO, SCREEN_RESOURCE_TRIGGERS_OVERVIEW,
								SCREEN_RESOURCE_DATA_OVERVIEW, SCREEN_RESOURCE_HOSTGROUP_TRIGGERS))) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
				}
			}

			// host
			for(Long resourceId : restrHosts) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0
							&& uint_in_array(Nest.value(screenItem,"resourcetype").asInteger(), array(SCREEN_RESOURCE_HOST_TRIGGERS))) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
				}
			}

			// graph
			for(Long resourceId : restrGraphs) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0 && Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_GRAPH) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
				}
			}

			// item
			for(Long resourceId : restrItems) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0
							&& uint_in_array(Nest.value(screenItem,"resourcetype").asInteger(), array(SCREEN_RESOURCE_SIMPLE_GRAPH, SCREEN_RESOURCE_PLAIN_TEXT))) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
				}
			}

			// map
			for(Long resourceId : restrMaps) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0
							&& Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_MAP) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
				}
			}

			// screen
			for(Long resourceId : restrScreens) {
				for (Entry<Object, Map> e : Clone.deepcopy(screensItems).entrySet()) {
				    Object screenItemId = e.getKey();
				    Map screenItem = e.getValue();
					if (bccomp(Nest.value(screenItem,"resourceid").$(), resourceId) == 0
							&& Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_SCREEN) {
						unset(result,screenItem.get("screenid"));
						unset(screensItems,screenItemId);
					}
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
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("screenid", "name"));
		CScreenGet options = new CScreenGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"screenid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> screens = get(options);
		return !empty(screens);
	}

	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _screens
	 */
	protected void validateCreate(CArray<Map> screens) {
		CArray screenDbFields = map("name", null);

		for(Map screen : screens) {
			if (!check_db_fields(screenDbFields, screen)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect input parameters."));
			}

			// \"templateid\", is not allowed
			if (array_key_exists("templateid", screen)) {
				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s("Cannot set \"templateid\" for screen \"%1$s\".", Nest.value(screen,"name").$())
				);
			}

			unset(screen,"screenid");
		}

		CParamGet options = new CParamGet();
		options.setFilter("name", rda_objectValues(screens, "name").valuesAsString());
		options.setOutput(new String[]{"name"});
		CArray<Map> dbScreens = select("screens", options);

		for(Map dbScreen : dbScreens) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Screen \"%1$s\" already exists.", Nest.value(dbScreen,"name").$()));
		}
	}

	/**
	 * Create screen.
	 *
	 * @param array _screens
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> screens) {
		validateCreate(screens);

		CArray<Long> screenIds = insert("screens", screens);

		// create screen items
		CArray<Map> screenItems = array();
		for (Entry<Object, Map> e : screens.entrySet()) {
		    Object key = e.getKey();
		    Map screen = e.getValue();
			if (isset(screen,"screenitems")) {
				for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
					Nest.value(screenItem,"screenid").$(screenIds.get(key));
					screenItems.add(screenItem);
				}
			}
		}

		if (!empty(screenItems)) {
			API.ScreenItem(this.idBean, this.getSqlExecutor()).create(screenItems);
		}

		return map("screenids", screenIds.valuesAsLong());
	}
	
	/**
	 * Validates the input parameters for the update() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _screens
	 * @param array _dbScreens	array of existing screens with screen IDs as keys
	 */
	protected void validateUpdate(CArray<Map> screens, CArray<Map> dbScreens) {
		for(Map screen : screens) {
			if (!isset(dbScreens, screen.get("screenid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS,
					_("No permissions to referred object or it does not exist!")
				);
			}
		}

		screens = extendObjects(tableName(), screens, new String[]{"name"});

		for(Map screen : screens) {
			// \"templateid\" is not allowed
			if (array_key_exists("templateid", screen)) {
				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s("Cannot update \"templateid\" for screen \"%1$s\".", Nest.value(screen,"name").$())
				);
			}

			if (isset(screen,"name")) {
				CParamGet options = new CParamGet();
				options.setFilter("name", Nest.value(screen,"name").asString());
				options.setOutput(new String[]{"screenid"});
				CArray<Map> dbScreensExist = select("screens", options);
				Map dbScreenExist = reset(dbScreensExist);

				if (!empty(dbScreenExist) && bccomp(Nest.value(dbScreenExist,"screenid").$(), Nest.value(screen,"screenid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Screen \"%1$s\" already exists.", Nest.value(screen,"name").$()));
				}
			}
		}
	}

	/**
	 * Update screen.
	 *
	 * @param array _screens
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> screens) {
		// check screen IDs before doing anything
		checkObjectIds(screens, "screenid",
			_("No \"%1$s\" given for screen."),
			_("Empty screen ID for screen."),
			_("Incorrect screen ID.")
		);

		CScreenGet options = new CScreenGet();
		options.setOutput(new String[]{"screenid", "hsize", "vsize"});
		options.setSelectScreenItems(new String[]{"screenitemid", "x", "y", "colspan", "rowspan"});
		options.setScreenIds(rda_objectValues(screens, "screenid").valuesAsLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbScreens = get(options);

		validateUpdate(screens, dbScreens);
		updateReal(screens);
		truncateScreenItems(screens, dbScreens);

		return map("screenids", rda_objectValues(screens, "screenid").valuesAsLong());
	}
	
	/**
	 * Saves screens and screen items.
	 *
	 * @param array _screens
	 */
	protected void updateReal(CArray<Map> screens) {
		screens = Clone.deepcopy(screens);
		
		CArray<Map> update = array();

		for(Map screen : screens) {
			Long screenId = Nest.value(screen,"screenid").asLong();
			unset(screen,"screenid");
			unset(screen,"screenitems");

			if (!empty(screen)) {
				update.add(map(
					"values", screen,
					"where", map("screenid", screenId)
				));
			}
		}

		update("screens", update);

		// replace screen items
		for(Map screen : screens) {
			if (isset(screen,"screenitems")) {
				replaceItems(Nest.value(screen,"screenid").asLong(), Nest.value(screen,"screenitems").asCArray());
			}
		}
	}
	
	/**
	 * Delete or reduce the size of screens items when reducing the size of the screens.
	 *
	 * Each array in the _screens array must have the following values:
	 * - screenid
	 * - hsize
	 * - vsize
	 *
	 * Each array in the _dbScreens array must have the following values:
	 * - screenid
	 * - hsize
	 * - vsize
	 * - screenitems
	 *
	 * @param array _screens
	 * @param array _dbScreens	array of existing screens with screen IDs as keys
	 */
	protected void truncateScreenItems(CArray<Map> screens, CArray<Map> dbScreens) {
		CArray deleteScreenItemIds = array();
		CArray<Map> updateScreenItems = array();
		for(Map screen : screens) {
			Map dbScreen = dbScreens.get(screen.get("screenid"));
			CArray<Map> dbScreenItems = Nest.value(dbScreen,"screenitems").asCArray();

			if (isset(screen,"hsize")) {
				for(Map dbScreenItem : dbScreenItems) {
					// delete screen items that are located on the deleted columns
					if (Nest.value(dbScreenItem,"x").asInteger() > Nest.value(screen,"hsize").asInteger() - 1) {
						Nest.value(deleteScreenItemIds,dbScreenItem.get("screenitemid")).$(Nest.value(dbScreenItem,"screenitemid").$());
					}
					// reduce the colspan of screenitems that are displayed on the deleted columns
					else if ((Nest.value(dbScreenItem,"x").asInteger() + Nest.value(dbScreenItem,"colspan").asInteger()) > Nest.value(screen,"hsize").asInteger()) {
						int colspan = Nest.value(screen,"hsize").asInteger() - Nest.value(dbScreenItem,"x").asInteger();
						Long screenItemId = Nest.value(dbScreenItem,"screenitemid").asLong();
						Nest.value(updateScreenItems,screenItemId,"screenitemid").$(Nest.value(dbScreenItem,"screenitemid").$());
						Nest.value(updateScreenItems,screenItemId,"colspan").$(colspan);
					}
				}
			}

			if (isset(screen,"vsize")) {
				for(Map dbScreenItem : dbScreenItems) {
					// delete screen items that are located on the deleted rows
					if (Nest.value(dbScreenItem,"y").asInteger() > Nest.value(screen,"vsize").asInteger() - 1) {
						Nest.value(deleteScreenItemIds,dbScreenItem.get("screenitemid")).$(Nest.value(dbScreenItem,"screenitemid").$());
					}
					// reduce the rowspan of screenitems that are displayed on the deleted rows
					else if ((Nest.value(dbScreenItem,"y").asInteger() + Nest.value(dbScreenItem,"rowspan").asInteger()) > Nest.value(screen,"vsize").asInteger()) {
						int rowspan = Nest.value(screen,"vsize").asInteger() - Nest.value(dbScreenItem,"y").asInteger();

						Long screenItemId = Nest.value(dbScreenItem,"screenitemid").asLong();
						Nest.value(updateScreenItems,screenItemId,"screenitemid").$(Nest.value(dbScreenItem,"screenitemid").$());
						Nest.value(updateScreenItems,screenItemId,"rowspan").$(rowspan);
					}
				}
			}
		}

		if (!empty(deleteScreenItemIds)) {
			delete("screens_items", (Map)map("screenitemid", deleteScreenItemIds.valuesAsLong()));
		}

		for(Map screenItem : updateScreenItems) {
			updateByPk("screens_items", Nest.value(screenItem,"screenitemid").$(), screenItem);
		}
	}
	
	/**
	 * Validate input for delete method.
	 *
	 * @param array _screenIds
	 */
	protected void validateDelete(Long... screenIds) {
		CScreenGet options = getParamInstance();
		options.setScreenIds(screenIds);
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map>dbScreens = get(options);
		for(Long screenId : screenIds) {
			if (!isset(dbScreens, screenId)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}
	}

	/**
	 * Delete screen.
	 *
	 * @param array _screenIds
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... screenIds) {
		validateDelete(screenIds);
		delete("screens_items", (Map)map("screenid", screenIds));
		delete("screens_items", (Map)map(
				"resourceid", screenIds, 
				"resourcetype", SCREEN_RESOURCE_SCREEN
		));
		delete("slides", (Map)map("screenid", screenIds));
		delete("screens", (Map)map("screenid", screenIds));
		delete("profiles", (Map)map(
			"idx", "web.favorite.screenids",
			"source", "screenid",
			"value_id", screenIds
		));

		return map("screenids", screenIds);
	}
	
	/**
	 * Replaces all of the screen items of the given screen.
	 *
	 * @param int   _screenId		The ID of the target screen
	 * @param array _screenItems	An array of screen items
	 */
	protected void replaceItems(Long screenId, CArray<Map> screenItems) {
		for(Map screenItem : screenItems) {
			Nest.value(screenItem,"screenid").$(screenId);
		}

		CArray<Map> createScreenItems = array();
		CArray<Map> updateScreenItems = array();
		CArray deleteScreenItemsIds = array();

		CScreenItemGet options = new CScreenItemGet();
		options.setScreenIds(screenId);
		options.setPreserveKeys(true);
		CArray<Map> dbScreenItems = API.ScreenItem(this.idBean, this.getSqlExecutor()).get(options);

		for(Map screenItem : screenItems) {
			if (isset(screenItem,"screenitemid") && isset(dbScreenItems,screenItem.get("screenitemid"))) {
				Nest.value(updateScreenItems,screenItem.get("screenitemid")).$(screenItem);
			} else {
				createScreenItems.add(screenItem);
			}
		}

		for(Map dbScreenItem : dbScreenItems) {
			if (!isset(updateScreenItems,dbScreenItem.get("screenitemid"))) {
				Nest.value(deleteScreenItemsIds,dbScreenItem.get("screenitemid")).$(Nest.value(dbScreenItem,"screenitemid").$());
			}
		}

		if (!empty(deleteScreenItemsIds)) {
			API.ScreenItem(this.idBean, this.getSqlExecutor()).delete(deleteScreenItemsIds.valuesAsLong());
		}
		if (!empty(updateScreenItems)) {
			API.ScreenItem(this.idBean, this.getSqlExecutor()).update(updateScreenItems);
		}
		if (!empty(createScreenItems)) {
			API.ScreenItem(this.idBean, this.getSqlExecutor()).create(createScreenItems);
		}
	}

	@Override
	protected void addRelatedObjects(CScreenGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		CArray screenIds = array_keys(result);
		// adding ScreenItems
		if (params.getSelectScreenItems() != null && !API_OUTPUT_COUNT.equals(params.getSelectScreenItems())) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("screens_items", new String[]{"screenid", "screenitemid"}, params.getSelectScreenItems()));
			options.setFilter("screenid", screenIds.valuesAsString());
			options.setPreserveKeys(true);
			CArray<Map> screenItems = select("screens_items", options);
			CRelationMap relationMap = createRelationMap(screenItems, "screenid", "screenitemid");
			unsetExtraFields(screenItems, new String[]{"screenid", "screenitemid"}, params.getSelectScreenItems());
			relationMap.mapMany(result, screenItems, "screenitems");
		}
	}
}
