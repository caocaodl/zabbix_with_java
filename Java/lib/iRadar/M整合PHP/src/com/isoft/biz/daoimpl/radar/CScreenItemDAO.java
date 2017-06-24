package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_ACTIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_EVENTS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SERVER_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SYSTEM_STATUS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_URL;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_is_int;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CScreenItemGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CScreenItemDAO extends CCoreLongKeyDAO<CScreenItemGet> {
	
	/**
	 * Supported values for the resourcetype column.
	 *
	 * @var array
	 */
	protected static CArray<Integer> resourceTypes = array(
		SCREEN_RESOURCE_GRAPH,
		SCREEN_RESOURCE_SIMPLE_GRAPH,
		SCREEN_RESOURCE_MAP,
		SCREEN_RESOURCE_PLAIN_TEXT,
		SCREEN_RESOURCE_HOSTS_INFO,
		SCREEN_RESOURCE_TRIGGERS_INFO,
		SCREEN_RESOURCE_SERVER_INFO,
		//SCREEN_RESOURCE_CLOCK,
		SCREEN_RESOURCE_SCREEN,
		SCREEN_RESOURCE_TRIGGERS_OVERVIEW,
		SCREEN_RESOURCE_DATA_OVERVIEW,
		SCREEN_RESOURCE_URL,
		SCREEN_RESOURCE_ACTIONS,
		SCREEN_RESOURCE_EVENTS,
		SCREEN_RESOURCE_HOSTGROUP_TRIGGERS,
		SCREEN_RESOURCE_SYSTEM_STATUS,
		SCREEN_RESOURCE_HOST_TRIGGERS
	);

	public CScreenItemDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "screens_items", "si", new String[] {"screenitemid", "screenid"});
	}

	/**
	 * Get screem item data.
	 *
	 * @param array _options
	 * @param array _options["screenitemids"]	Search by screen item IDs
	 * @param array _options["screenids"]		Search by screen IDs
	 * @param array _options["filter"]			Result filter
	 * @param array _options["limit"]			The size of the result set
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CScreenItemGet params) {
		// build and execute query
		SqlBuilder sqlParts = createSelectQuery(tableName(), params);
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
				ret = row.get("rowscount");
			} else {// normal select query
				if (params.getPreserveKeys() != null) {
					Long id = (Long)row.get("screenitemid");
					if (!isset(result,id)) {
						result.put(id, new HashMap());
					}
					result.get(id).putAll(row);
				} else {
					result.add(row);
				}
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		return (T)result;
	}

	
	/**
	 * Create screen items.
	 *
	 * @param array _screenItems	An array of screen items
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> screenItems) {
		validateCreate(screenItems);
		CArray<Long> screenItemIds = insert(tableName(), screenItems);
		return map("screenitemids", screenItemIds.valuesAsLong());
	}

	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _screenItems
	 */
	protected void validateCreate(CArray<Map> screenItems) {
		CArray screenItemDBfields = map(
			"screenid", null,
			"resourcetype", null
		);

		for(Map screenItem : screenItems) {
			if (!check_db_fields(screenItemDBfields, screenItem)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
			unset(screenItem,"screenitemid");
		}

		CArray screenIds = array_keys(array_flip(rda_objectValues(screenItems, "screenid")));

		CScreenGet soptions = new CScreenGet();
		soptions.setScreenIds(screenIds.valuesAsLong());
		soptions.setOutput(new String[]{"screenid", "hsize", "vsize", "name"});
		soptions.setEditable(true);
		soptions.setPreserveKeys(true);
		CArray<Map> dbScreens = API.Screen(this.idBean, this.getSqlExecutor()).get(soptions);

		if (count(dbScreens) < count(screenIds)) {
			CTemplateScreenGet tsoptions = new CTemplateScreenGet();
			tsoptions.setScreenIds(screenIds.valuesAsLong());
			tsoptions.setOutput(new String[]{"screenid", "hsize", "vsize", "name"});
			tsoptions.setEditable(true);
			tsoptions.setPreserveKeys(true);
			CArray<Map> dbTemplateScreens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);

			if (!empty(dbTemplateScreens)) {
				dbScreens = rda_array_merge(dbScreens, dbTemplateScreens);
			}
		}

		CScreenItemGet options = new CScreenItemGet();
		options.setScreenIds(screenIds.valuesAsLong());
		options.setOutput(new String[]{"screenitemid", "screenid", "x", "y", "rowspan", "colspan"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbScreenItems = get(options);

		checkInput(screenItems, dbScreenItems);
		checkDuplicateResourceInCell(screenItems, dbScreenItems, dbScreens);

		for(Map screenItem : screenItems) {
			checkSpans(screenItem, dbScreens.get(screenItem.get("screenid")));
			checkSpansInBounds(screenItem, dbScreenItems, dbScreens.get(screenItem.get("screenid")));
			checkGridCoordinates(screenItem, dbScreens.get(screenItem.get("screenid")));
		}
	}

	/**
	 * Updates screen items.
	 *
	 * @param array _screenItems	An array of screen items
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> screenItems) {
		validateUpdate(screenItems);
		screenItems = rda_toHash(screenItems, "screenitemid");
		CArray<Map> update = array();
		CArray screenItemIds = array();
		for(Map screenItem : screenItems) {
			Object screenItemId = Nest.value(screenItem,"screenitemid").$();
			unset(screenItem,"screenitemid");
			update.add(map(
				"values", screenItem,
				"where", map("screenitemid", screenItemId)
			));
			screenItemIds.add(screenItemId);
		}
		update(tableName(), update);
		return map("screenitemids", screenItemIds.valuesAsLong());
	}
	
	/**
	 * Validates the input parameters for the update() method.
	 *
	 * @throws APIException
	 *
	 * @param array _screenItems
	 */
	protected void validateUpdate(CArray<Map> screenItems) {
		CArray screenItemDBfields = map(
			"screenitemid", null
		);

		for(Map screenItem : screenItems) {
			if (!check_db_fields(screenItemDBfields, screenItem)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		screenItems = rda_toHash(screenItems, "screenitemid");
		CArray screenItemIds = array_keys(screenItems);

		CScreenGet soptions = new CScreenGet();
		soptions.setScreenItemIds(screenItemIds.valuesAsLong());
		soptions.setOutput(new String[]{"screenid", "hsize", "vsize", "name"});
		soptions.setEditable(true);
		soptions.setPreserveKeys(true);
		CArray<Map> dbScreens = API.Screen(this.idBean, this.getSqlExecutor()).get(soptions);

		CTemplateScreenGet tsoptions = new CTemplateScreenGet();
		tsoptions.setScreenItemIds(screenItemIds.valuesAsLong());
		tsoptions.setOutput(new String[]{"screenid", "hsize", "vsize", "name"});
		tsoptions.setEditable(true);
		tsoptions.setPreserveKeys(true);
		CArray<Map> dbTemplateScreens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);

		if (!empty(dbTemplateScreens)) {
			dbScreens = rda_array_merge(dbScreens, dbTemplateScreens);
		}

		CScreenItemGet options = new CScreenItemGet();
		options.setScreenitemIds(screenItemIds.valuesAsLong());
		options.setOutput(new String[]{"screenitemid", "screenid", "x", "y", "rowspan", "colspan", "resourcetype", "resourceid"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbScreenItems = get(options);

		screenItems = extendObjects(tableName(), screenItems, new String[]{"screenid", "x", "y", "rowspan", "colspan"});

		checkInput(screenItems, dbScreenItems);
		checkDuplicateResourceInCell(screenItems, dbScreenItems, dbScreens);

		for(Map screenItem : screenItems) {
			checkSpans(screenItem, dbScreens.get(screenItem.get("screenid")));
			checkSpansInBounds(screenItem, dbScreenItems, dbScreens.get(screenItem.get("screenid")));
			checkGridCoordinates(screenItem, dbScreens.get(screenItem.get("screenid")));
		}
	}
	
	/**
	 * Update screen items using the given "x" and "y" parameters.
	 * If the given cell is free, a new screen item will be created.
	 *
	 * @param array _screenItems	An array of screen items with the given X and Y coordinates
	 *
	 * @return array
	 */
	public CArray<Long[]> updateByPosition(CArray<Map> screenItems) {
		CArray screenItemDBfields = map(
			"screenid", null,
			"x", null,
			"y", null
		);

		for(Map screenItem : screenItems) {
			if (!check_db_fields(screenItemDBfields, screenItem)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		CScreenItemGet options = new CScreenItemGet();
		options.setScreenIds(rda_objectValues(screenItems, "screenid").valuesAsLong());
		options.setOutput(new String[]{"screenitemid", "screenid", "x", "y"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbScreenItems = get(options);

		CArray<Map> create = array();
		CArray<Map> update = array();
		CArray<Long[]> affectedIds = array();

		for(Map screenItem : screenItems) {
			boolean gocontinue = false;
			for(Map dbScreenItem : dbScreenItems) {
				if (Nest.value(screenItem,"screenid").asLong() == Nest.value(dbScreenItem,"screenid").asLong()
						&& Nest.value(screenItem,"x").asInteger() == Nest.value(dbScreenItem,"x").asInteger() && Nest.value(screenItem,"y").asInteger() == Nest.value(dbScreenItem,"y").asInteger()) {
					Nest.value(screenItem,"screenitemid").$(Nest.value(dbScreenItem,"screenitemid").$());
					Nest.value(update,dbScreenItem.get("screenitemid")).$(screenItem);
					gocontinue = true;
					continue;
				}
			}
			if(gocontinue){
				continue;
			}
			create.add(screenItem);
		}

		if (!empty(update)) {
			affectedIds = API.ScreenItem(this.idBean, this.getSqlExecutor()).update(update);
		}

		if (!empty(create)) {
			affectedIds = API.ScreenItem(this.idBean, this.getSqlExecutor()).create(create);
		}

		return map("screenitemids", affectedIds.valuesAsLong());
	}

	/**
	 * Deletes screen items.
	 *
	 * @param array _screenItemIds
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... screenItemIds) {
		// check permissions
		CScreenItemGet options = new CScreenItemGet();
		options.setScreenitemIds(TArray.as(screenItemIds).asLong());
		options.setPreserveKeys(true);
		CArray<Map> dbScreenItems = get(options);
		for(Long screenItemId : screenItemIds) {
			if (!isset(dbScreenItems,screenItemId)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}
		// delete screen items
		delete(tableName(), (Map)map("screenitemid", screenItemIds));
		return map("screenitemids", screenItemIds);
	}

	/**
	 * Returns true if the given screen items exist and are available for reading.
	 *
	 * @param array _screenItemIds
	 *
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... screenItemIds) {
		if (!isArray(screenItemIds)) {
			return false;
		} else if (empty(screenItemIds)) {
			return true;
		}
		screenItemIds = array_unique(screenItemIds);
		CScreenItemGet options = new CScreenItemGet();
		options.setScreenitemIds(screenItemIds);
		options.setCountOutput(true);
		long count = get(options);
		return (count(screenItemIds) == count);
	}

	/**
	 * Returns true if the given screen items exist and are available for writing.
	 *
	 * @param array _screenItemIds	An array if screen item IDs
	 *
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... screenItemIds) {
		if (!isArray(screenItemIds)) {
			return false;
		} else if (empty(screenItemIds)) {
			return true;
		}
		screenItemIds = array_unique(screenItemIds);
		CScreenItemGet options = new CScreenItemGet();
		options.setScreenitemIds(screenItemIds);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(screenItemIds) == count);
	}
	
	/**
	 * Validates screen items.
	 *
	 * If the _dbScreenItems parameter is given, the screen items will be matched
	 * against the ones given in _dbScreenItems. If a screen item is not present in
	 * _dbScreenItems, a RDA_API_ERROR_PERMISSIONS exception will be thrown.
	 *
	 * @throws APIException if a validation error occurred.
	 *
	 * @param array _screenItems
	 * @param array _dbScreenItems
	 */
	protected void checkInput(CArray<Map> screenItems) {
		checkInput(screenItems, array());
	}
	protected void checkInput(CArray<Map> screenItems, CArray<Map> dbScreenItems) {
		CArray hostGroupsIds = array();
		CArray hostIds = array();
		CArray graphIds = array();
		CArray itemIds = array();
		CArray mapIds = array();
		CArray screenIds = array();
		
		screenItems = extendFromObjects(screenItems, dbScreenItems, array("resourcetype", "resourceid"));

		for(Map screenItem : screenItems) {
			// check permissions
			if (isset(screenItem,"screenitemid") && !isset(dbScreenItems,screenItem.get("screenitemid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}

			if (!isValidResourceType(Nest.value(screenItem,"resourcetype").asInteger())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect resource type provided for screen item."));
			}

			if (!isset(screenItem,"resourceid")) {
				Nest.value(screenItem,"resourceid").$(null);
			}

			// check resource id
			switch (Nest.value(screenItem,"resourcetype").asInteger()) {
				case SCREEN_RESOURCE_HOSTS_INFO:
				case SCREEN_RESOURCE_TRIGGERS_INFO:
				case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
				case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
				case SCREEN_RESOURCE_DATA_OVERVIEW:
					if (in_array(Nest.value(screenItem,"resourcetype").asInteger(), new Integer[]{SCREEN_RESOURCE_TRIGGERS_OVERVIEW, SCREEN_RESOURCE_DATA_OVERVIEW})) {
						if (empty(Nest.value(screenItem,"resourceid").$())) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No host group ID provided for screen element."));
						}
					}

					if (!empty(Nest.value(screenItem,"resourceid").$())) {
						Nest.value(hostGroupsIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					}
					break;

				case SCREEN_RESOURCE_HOST_TRIGGERS:
					if (!empty(Nest.value(screenItem,"resourceid").$())) {
						Nest.value(hostIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					}
					break;

				case SCREEN_RESOURCE_GRAPH:
					if (empty(Nest.value(screenItem,"resourceid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No graph ID provided for screen element."));
					}

					Nest.value(graphIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					break;

				case SCREEN_RESOURCE_SIMPLE_GRAPH:
				case SCREEN_RESOURCE_PLAIN_TEXT:
					if (empty(Nest.value(screenItem,"resourceid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No item ID provided for screen element."));
					}

					Nest.value(itemIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					break;

//				case SCREEN_RESOURCE_CLOCK:
//					if (isset(screenItem,"style") && Nest.value(screenItem,"style").asInteger() == TIME_TYPE_HOST) {
//						if (empty(Nest.value(screenItem,"resourceid").$())) {
//							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No item ID provided for screen element."));
//						}
//						
//						Nest.value(itemIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
//					}
//					break;

				case SCREEN_RESOURCE_MAP:
					if (empty(Nest.value(screenItem,"resourceid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No map ID provided for screen element."));
					}

					Nest.value(mapIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					break;

				case SCREEN_RESOURCE_SCREEN:
					if (empty(Nest.value(screenItem,"resourceid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No screen ID provided for screen element."));
					}

					Nest.value(screenIds,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
					break;
			}

			// check url
			if (Nest.value(screenItem,"resourcetype").asInteger() == SCREEN_RESOURCE_URL) {
				if (!isset(screenItem,"url") || rda_empty(Nest.value(screenItem,"url").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No URL provided for screen element."));
				}
			}

			// check \"Show lines\"
			if (isset(Nest.value(screenItem,"elements").$())) {
				switch (Nest.value(screenItem,"resourcetype").asInteger()) {
					case SCREEN_RESOURCE_ACTIONS:
					case SCREEN_RESOURCE_EVENTS:
					case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
					case SCREEN_RESOURCE_HOST_TRIGGERS:
					case SCREEN_RESOURCE_PLAIN_TEXT:
						if (Nest.value(screenItem,"elements").asInteger() < 1 || Nest.value(screenItem,"elements").asInteger() > 100) {
							throw CDB.exception(
								RDA_API_ERROR_PARAMETERS,
								_s(
									"Incorrect value \"%1$s\" for \"%2$s\" field: must be between %3$s and %4$s.",
									Nest.value(screenItem,"elements").$(),
									"elements",
									1,
									100
								)
							);
						}
						break;
				}
			}
		}

		// check host groups
		if (!empty(hostGroupsIds)) {
			CHostGroupGet options = new CHostGroupGet();
			options.setGroupIds(hostGroupsIds.valuesAsLong());
			options.setOutput(new String[]{"groupid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbHostGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(options);

			for(Object hostGroupsId : hostGroupsIds) {
				if (!isset(dbHostGroups, hostGroupsId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect host group ID \"%1$s\" provided for screen element.", hostGroupsId)
					);
				}
			}
		}

		// check hosts
		if (!empty(hostIds)) {
			CHostGet options = new CHostGet();
			options.setHostIds(hostIds.valuesAsLong());
			options.setOutput(new String[]{"hostid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbHosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);

			for(Object hostId : hostIds) {
				if (!isset(dbHosts,hostId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect host ID \"%1$s\" provided for screen element.", hostId)
					);
				}
			}
		}

		// check graphs
		if (!empty(graphIds)) {
			CGraphGet options = new CGraphGet();
			options.setGraphIds(graphIds.valuesAsLong());
			options.setOutput(new String[]{"graphid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbGraphs = API.Graph(this.idBean, this.getSqlExecutor()).get(options);

			for(Object graphId : graphIds) {
				if (!isset(dbGraphs,graphId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect graph ID \"%1$s\" provided for screen element.", graphId)
					);
				}
			}
		}

		// check items
		if (!empty(itemIds)) {
			CItemGet options = new CItemGet();
			options.setItemIds(itemIds.valuesAsLong());
			options.setOutput(new String[]{"itemid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			options.setWebItems(true);
			CArray<Map> dbItems = API.Item(this.idBean, this.getSqlExecutor()).get(options);

			for(Object itemId : itemIds) {
				if (!isset(dbItems,itemId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect item ID \"%1$s\" provided for screen element.", itemId)
					);
				}
			}
		}

		// check maps
		if (!empty(mapIds)) {
			CMapGet options = new CMapGet();
			options.setSysmapIds(mapIds.valuesAsLong());
			options.setOutput(new String[]{"sysmapid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbMaps = API.Map(this.idBean, this.getSqlExecutor()).get(options);

			for(Object mapId : mapIds) {
				if (!isset(dbMaps,mapId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect map ID \"%1$s\" provided for screen element.", mapId)
					);
				}
			}
		}

		// check screens
		if (!empty(screenIds)) {
			CScreenGet options = new CScreenGet();
			options.setScreenIds(screenIds.valuesAsLong());
			options.setOutput(new String[]{"screenid"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbScreens = API.Screen(this.idBean, this.getSqlExecutor()).get(options);

			if (count(dbScreens) < count(screenIds)) {
				CTemplateScreenGet tsoptions = new CTemplateScreenGet();
				tsoptions.setScreenIds(screenIds.valuesAsLong());
				tsoptions.setOutput(new String[]{"screenid"});
				tsoptions.setEditable(true);
				tsoptions.setPreserveKeys(true);
				CArray<Map> dbTemplateScreens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);

				if (!empty(dbTemplateScreens)) {
					dbScreens = rda_array_merge(dbScreens, dbTemplateScreens);
				}
			}

			for(Object screenId : screenIds) {
				if (!isset(dbScreens,screenId)) {
					throw CDB.exception(
						RDA_API_ERROR_PERMISSIONS,
						_s("Incorrect screen ID \"%1$s\" provided for screen element.", screenId)
					);
				}
			}
		}
	}
	
	/**
	 * Returns true if the given resource type is supported.
	 *
	 * @param int _resourceType
	 *
	 * @return bool
	 */
	protected boolean isValidResourceType(Integer resourceType) {
		return in_array(resourceType, resourceTypes);
	}
	
	/**
	 * Checks that the row and column spans are valid.
	 *
	 * @throws APIException if the any of the spans is not an integer or missing
	 *
	 * @param array _screenItem
	 * @param array _screen
	 */
	protected void checkSpans(Map screenItem, Map screen) {
		if (isset(screenItem,"rowspan")) {
			if (!rda_is_int(Nest.value(screenItem,"rowspan").$()) || Nest.value(screenItem,"rowspan").asInteger() < 0) {
				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s(
						"Screen \"%1$s\" row span in cell X - %2$s Y - %3$s is incorrect.",
						Nest.value(screen,"name").$(),
						Nest.value(screenItem,"x").$(),
						Nest.value(screenItem,"y").$()
					)
				);
			}
		}

		if (isset(screenItem,"colspan")) {
			if (!rda_is_int(Nest.value(screenItem,"colspan").$()) || Nest.value(screenItem,"colspan").asInteger() < 0) {
				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s(
						"Screen \"%1$s\" column span in cell X - %2$s Y - %3$s is incorrect.",
						Nest.value(screen,"name").$(),
						Nest.value(screenItem,"x").$(),
						Nest.value(screenItem,"y").$()
					)
				);
			}
		}
	}
	
	/**
	 * Checks that the row and column spans fit into the size of the screen.
	 *
	 * @throws APIException if the any of the spans is bigger then the free space on the screen
	 *
	 * @param array _screenItem
	 * @param array _dbScreenItems
	 * @param array _screen
	 */
	protected void checkSpansInBounds(Map screenItem, CArray<Map> dbScreenItems, Map screen) {
		if (!isset(screenItem,"x")) {
			Nest.value(screenItem,"x").$(isset(screenItem,"screenitemid")
				? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"x").asInteger()
				: 0);
		}
		if (!isset(screenItem,"y")) {
			Nest.value(screenItem,"y").$(isset(screenItem,"screenitemid")
				? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"y").asInteger()
				: 0);
		}

		if (isset(screenItem,"rowspan") && isset(screen,"vsize")
				&& Nest.value(screenItem,"rowspan").asInteger() > Nest.value(screen,"vsize").asInteger() - Nest.value(screenItem,"y").asInteger()) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s(
					"Screen \"%1$s\" row span in cell X - %2$s Y - %3$s is too big.",
					Nest.value(screen,"name").$(),
					Nest.value(screenItem,"x").$(),
					Nest.value(screenItem,"y").$()
				)
			);
		}

		if (isset(screenItem,"colspan") && isset(screen,"hsize")
				&& Nest.value(screenItem,"colspan").asInteger() > Nest.value(screen,"hsize").asInteger() - Nest.value(screenItem,"x").asInteger()) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s(
					"Screen \"%1$s\" column span in cell X - %2$s Y - %3$s is too big.",
					Nest.value(screen,"name").$(),
					Nest.value(screenItem,"x").$(),
					Nest.value(screenItem,"y").$()
				)
			);
		}
	}
	
	/**
	 * Check duplicates screen items in one cell.
	 *
	 * @throws APIException
	 *
	 * @param array _screenItems
	 * @param array _dbScreenItems
	 * @param array _dbScreens
	 */
	protected void checkDuplicateResourceInCell(CArray<Map> screenItems, CArray<Map> dbScreenItems, CArray<Map> dbScreens) {
		for(Map screenItem : screenItems) {
			if (!isset(screenItem,"x")) {
				Nest.value(screenItem,"x").$(isset(screenItem,"screenitemid")
					? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"x").asInteger()
					: 0);
			}
			if (!isset(screenItem,"y")) {
				Nest.value(screenItem,"y").$(isset(screenItem,"screenitemid")
					? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"y").asInteger()
					: 0);
			}
		}

		for (Entry<Object, Map> e : screenItems.entrySet()) {
		    Object key = e.getKey();
		    Map screenItem = e.getValue();
			// check between input and input
		    for (Entry<Object, Map> e2 : screenItems.entrySet()) {
			    Object key2 = e2.getKey();
			    Map screenItem2 = e2.getValue();
				if (key.equals(key2)) {
					continue;
				}

				if (Nest.value(screenItem,"x").asInteger() == Nest.value(screenItem2,"x").asInteger() &&
					Nest.value(screenItem,"y").asInteger() == Nest.value(screenItem2,"y").asInteger() &&
					Nest.value(screenItem,"screenid").asLong() == Nest.value(screenItem2,"screenid").asLong()
				) {
					Object screenId = isset(screenItem,"screenitemid")
						? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"screenid").$()
						: Nest.value(screenItem,"screenid").$();

					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_s(
							"Screen \"%1$s\" cell X - %2$s Y - %3$s is already taken.",
							Nest.value(dbScreens,screenId,"name").$(),
							Nest.value(screenItem,"x").$(),
							Nest.value(screenItem,"y").$()
						)
					);
				}
			}

			// check between input and db
			for(Map dbScreenItem : dbScreenItems) {
				if (isset(screenItem,"screenitemid")
						&& bccomp(Nest.value(screenItem,"screenitemid").$(), Nest.value(dbScreenItem,"screenitemid").$()) == 0) {
					continue;
				}

				if (Nest.value(screenItem,"x").asInteger() == Nest.value(dbScreenItem,"x").asInteger() && Nest.value(screenItem,"y").asInteger() == Nest.value(dbScreenItem,"y").asInteger()) {
					Object screenId = isset(screenItem,"screenitemid")
						? Nest.value(dbScreenItems,screenItem.get("screenitemid"),"screenid").$()
						: Nest.value(screenItem,"screenid").$();

					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_s(
							"Screen \"%1$s\" cell X - %2$s Y - %3$s is already taken.",
							Nest.value(dbScreens,screenId,"name").$(),
							Nest.value(screenItem,"x").$(),
							Nest.value(screenItem,"y").$()
						)
					);
				}
			}
		}
	}
	
	/**
	 * Checks that the row and column fit into the size of the screen.
	 *
	 * @throws APIException if the any of the coordinates is bigger then the free space on the screen
	 *
	 * @param array _screenItem
	 * @param array _screen
	 */
	protected void checkGridCoordinates(Map screenItem, Map screen) {
		if (isset(screenItem,"x") && Nest.value(screenItem,"x").asInteger() > Nest.value(screen,"hsize").asInteger() - 1) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s(
					"The X coordinate of screen element located at X - %1$s and Y - %2$s of screen \"%3$s\" is too big.",
					Nest.value(screenItem,"x").$(),
					Nest.value(screenItem,"y").$(),
					Nest.value(screen,"name").$()
				)
			);
		}

		if (isset(screenItem,"y") && Nest.value(screenItem,"y").asInteger() > Nest.value(screen,"vsize").asInteger() - 1) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s(
					"The Y coordinate of screen element located at X - %1$s and Y - %2$s of screen \"%3$s\" is too big.",
					Nest.value(screenItem,"x").$(),
					Nest.value(screenItem,"y").$(),
					Nest.value(screen,"name").$()
				)
			);
		}
	}

	@Override
	protected void applyQueryFilterOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryFilterOptions(tableName, tableAlias, params, sqlParts);
		
		// screens
		if (Nest.value(params,"screenids").$() != null) {
			addQuerySelect(fieldId("screenid"), sqlParts);
			sqlParts.where.dbConditionInt(fieldId("screenid"), Nest.array(params,"screenids").asLong());
		}
	}	
}
