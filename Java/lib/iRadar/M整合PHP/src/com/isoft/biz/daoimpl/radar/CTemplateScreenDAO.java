package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.FuncsUtil.createParentToChildRelation;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
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
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTemplateScreenDAO extends CScreenDAO {

	public CTemplateScreenDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "screens", "s", new String[] {"screenid", "name"});
	}

	@Override
	public <T> T get(CScreenGet params) {
		return this.get((CTemplateScreenGet)params);
	}
	
	/**
	 * Get screen data.
	 *
	 * @param array  _options
	 * @param bool   _options["with_items"]		only with items
	 * @param bool   _options["editable"]		only with read-write permission. Ignored for SuperAdmins
	 * @param int    _options["count"]			count Hosts, returned column name is rowscount
	 * @param string _options["pattern"]		search hosts by pattern in host names
	 * @param int    _options["limit"]			limit selection
	 * @param string _options["order"]			deprecated parameter (for now)
	 *
	 * @return array
	 */
	public <T> T get(CTemplateScreenGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("screens", "s.screenid,s.templateid");
		sqlParts.from.put("screens", "screens s");
		sqlParts.where.put("template", "s.templateid IS NOT NULL");
		
		if (!is_null(params.getEditable(true)) || (is_null(params.getHostIds()) && is_null(params.getTemplateIds()))) {
			params.setNoInheritance(true);
		}
		
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
		
		// templateids
		if (!is_null(params.getTemplateIds())) {
			if (isset(params.getHostIds()) && !is_null(params.getHostIds())) {
				params.setHostIds(array_merge(params.getHostIds(), params.getTemplateIds()));
			} else {
				params.setHostIds(params.getTemplateIds());
			}
		}
		
		// hostids
		CArray templatesChain = array();
		if (!is_null(params.getHostIds())) {
			// collecting template chain
			CArray<Long> linkedTemplateids = array(params.getHostIds());
			CArray<Long> childTemplateids = array(params.getHostIds());

			while (is_null(params.getNoInheritance()) && !empty(childTemplateids)) {
				SqlBuilder subsqlParts = new SqlBuilder();
				String sql = "SELECT ht.*"+
					" FROM hosts_templates ht"+
					" WHERE "+subsqlParts.where.dbConditionInt("hostid", childTemplateids.valuesAsLong());
				CArray<Map> dbTemplates = DBselect(getSqlExecutor(),sql,subsqlParts.getNamedParams());

				childTemplateids = array();
				for (Map link : dbTemplates) {
					Nest.value(childTemplateids,link.get("templateid")).$(Nest.value(link,"templateid").asLong());
					Nest.value(linkedTemplateids,link.get("templateid")).$(Nest.value(link,"templateid").asLong());
					createParentToChildRelation(templatesChain, link, "templateid", "hostid");
				}
			}
			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("templateid", "s.templateid");
			}
			sqlParts.where.dbConditionInt("templateid", "s.templateid", linkedTemplateids.valuesAsLong());
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
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				if (isset(row.get("screenitemid")) && is_null(params.getSelectScreenItems())) {
					if (!isset(result.get(id),"screenitems")) {
						result.get(id).put("screenitems", new CArray());
					}
					((CArray)result.get(id).get("screenitems")).add(map("screenitemid", row.remove("screenitemid")));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput()) && is_null(params.getGroupCount())) {
			return (T)ret;
		}
		
		CArray screenIds = array_keys(result);
		CArray graphids = array();
		CArray itemids = array();

		// adding screenitems
		if (params.getSelectScreenItems() != null && !API_OUTPUT_COUNT.equals(params.getSelectScreenItems())) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("screens_items",new String[]{"screenid", "screenitemid", "resourcetype", "resourceid"}, params.getSelectScreenItems()));
			options.setFilter("screenid", screenIds.valuesAsString());
			options.setPreserveKeys(true);
			CArray<Map> screenItems = select("screens_items", options);
			CRelationMap relationMap = createRelationMap(screenItems, "screenid", "screenitemid");

			for(Map screenItem : screenItems) {
				switch (Nest.value(screenItem,"resourcetype").asInteger()) {
					case SCREEN_RESOURCE_GRAPH:
						Nest.value(graphids,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
						break;
					case SCREEN_RESOURCE_SIMPLE_GRAPH:
					case SCREEN_RESOURCE_PLAIN_TEXT:
						Nest.value(itemids,screenItem.get("resourceid")).$(Nest.value(screenItem,"resourceid").$());
						break;
				}
			}

			unsetExtraFields(screenItems, new String[]{"screenid", "screenitemid", "resourceid", "resourcetype"}, params.getSelectScreenItems());
			relationMap.mapMany(result, screenItems, "screenitems");
		}

		// creating linkage of template -> real objects
		CArray<Map> tplGraphs = null;
		CArray realGraphs = null;
		CArray<Map> tplItems = null;
		CArray realItems = null;
		if (!is_null(params.getSelectScreenItems()) && !is_null(params.getHostIds())) {
			// prepare graphs
			if (!empty(graphids)) {
				CGraphGet goptions = new CGraphGet();
				goptions.setOutput(new String[]{"graphid", "name"});
				goptions.setGraphIds(graphids.valuesAsLong());
				goptions.setNopermissions(true);
				goptions.setPreserveKeys(true);
				tplGraphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);

				goptions = new CGraphGet();
				goptions.setOutput(new String[]{"graphid", "name"});
				goptions.setSelectHosts(new String[]{"hostid"});
				goptions.setHostIds(params.getHostIds());
				goptions.setFilter("name", rda_objectValues(tplGraphs, "name").valuesAsString());
				goptions.setNopermissions(true);
				goptions.setPreserveKeys(true);
				CArray<Map> dbGraphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);
				
				realGraphs = array();
				for(Map graph : dbGraphs) {
					Map host = reset(Nest.value(graph,"hosts").asCArray());
					unset(graph,"hosts");
					if (!isset(realGraphs,host.get("hostid"))) {
						Nest.value(realGraphs,host.get("hostid")).$(array());
					}
					Nest.value(realGraphs,host.get("hostid"),graph.get("name")).$(graph);
				}
			}

			// prepare items
			if (!empty(itemids)) {
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"itemid", "key_", "hostid"});
				ioptions.setItemIds(itemids.valuesAsLong());
				ioptions.setNopermissions(true);
				ioptions.setPreserveKeys(true);
				tplItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

				ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"itemid", "key_", "hostid"});
				ioptions.setHostIds(params.getHostIds());
				ioptions.setFilter("key_", rda_objectValues(tplItems, "key_").valuesAsString());
				ioptions.setNopermissions(true);
				ioptions.setPreserveKeys(true);
				CArray<Map> dbItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

				realItems = array();
				for(Map item : dbItems) {
					unset(item,"hosts");
					if (!isset(realItems,item.get("hostid"))) {
						Nest.value(realItems,item.get("hostid")).$(array());
					}
					Nest.value(realItems,item.get("hostid"),item.get("key_")).$(item);
				}
			}
		}

		// hashing
		CArray hostids = rda_toHash(params.getHostIds());
		if (is_null(params.getCountOutput())
				|| (!is_null(params.getCountOutput()) && !is_null(params.getGroupCount()))) {
			// creating copies of templated screens (inheritance)
			// screenNum is needed due to we can't refer to screenid/hostid/templateid as they will repeat
			int screenNum = 0;
			CArray vrtResult = array();

			for(Map screen : result) {
				if (is_null(params.getHostIds()) || isset(hostids,screen.get("templateid"))) {
					screenNum++;
					Nest.value(vrtResult,screenNum).$(screen);
					Nest.value(vrtResult,screenNum,"hostid").$(Nest.value(screen,"templateid").$());
				}
				if (!isset(templatesChain,screen.get("templateid"))) {
					continue;
				}

				for(Object hostid : Nest.value(templatesChain,screen.get("templateid")).asCArray()) {
					if (!isset(hostids,hostid)) {
						continue;
					}

					screenNum++;
					Nest.value(vrtResult,screenNum).$(screen);
					Nest.value(vrtResult,screenNum,"hostid").$(hostid);

					if (!isset(Nest.value(vrtResult,screenNum,"screenitems").$())) {
						continue;
					}

					for(Map screenitem : (CArray<Map>)Nest.value(vrtResult,screenNum,"screenitems").asCArray()) {
						switch (Nest.value(screenitem,"resourcetype").asInteger()) {
							case SCREEN_RESOURCE_GRAPH:
								String graphName = Nest.value(tplGraphs,screenitem.get("resourceid"),"name").asString();
								Nest.value(screenitem,"real_resourceid").$(Nest.value(realGraphs,hostid,graphName,"graphid").$());
								break;
							case SCREEN_RESOURCE_SIMPLE_GRAPH:
							case SCREEN_RESOURCE_PLAIN_TEXT:
								String itemKey = Nest.value(tplItems,screenitem.get("resourceid"),"key_").asString();
								Nest.value(screenitem,"real_resourceid").$(Nest.value(realItems,hostid,itemKey,"itemid").$());
								break;
						}
					}
				}
			}
			result = array_values(vrtResult);
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			unsetExtraFields(result, new String[]{"templateid"}, params.getOutput());
		}
	
		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		} else if (!is_null(params.getNoInheritance())) {
			result = rda_toHash(result, "screenid");
		}
		return (T)result;
	}

	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("screenid", "name"),"templateid");
		CTemplateScreenGet options = new CTemplateScreenGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"screenid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> screens = get(options);
		return !empty(screens);
	}

	public boolean copy(CArray data) {
		CArray screenIds = rda_toArray(Nest.value(data,"screenIds").$());
		Nest.value(data,"screenIds").$(screenIds);
		CArray templateIds = rda_toArray(Nest.value(data,"templateIds").$());
		Nest.value(data,"templateIds").$(templateIds);

		validateCopy(data);

		CTemplateScreenGet options = new CTemplateScreenGet();
		options.setScreenIds(screenIds.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectScreenItems(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		CArray<Map> screens = get(options);

		for(Object templateId : templateIds) {
			CArray resourceGraphIds = array();
			CArray resourceItemIds = array();
			for(Map screen : screens) {
				Nest.value(screen,"templateid").$(templateId);
				for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
					if (!empty(Nest.value(screenItem,"resourceid").$())) {
						switch (Nest.value(screenItem,"resourcetype").asInteger()) {
							case SCREEN_RESOURCE_GRAPH:
								resourceGraphIds.add(Nest.value(screenItem,"resourceid").$());
								break;
							default:
								resourceItemIds.add(Nest.value(screenItem,"resourceid").$());
						}
					}
				}
			}

			// get same items on destination template
			CArray resourceItemsMap = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbItems = DBselect(getSqlExecutor(),
				"SELECT src.itemid AS srcid,dest.itemid as destid"+
				" FROM items dest,items src"+
				" WHERE dest.tenantid=src.tenantid"+
					" AND dest.key_=src.key_"+
					" AND dest.hostid="+ sqlParts.marshalParam(templateId)+
					" AND "+sqlParts.dual.dbConditionInt("src.itemid", resourceItemIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for (Map dbItem : dbItems) {
				Nest.value(resourceItemsMap,dbItem.get("srcid")).$(Nest.value(dbItem,"destid").$());
			}

			// get same graphs on destination template
			CArray resourceGraphsMap = array();
			sqlParts = new SqlBuilder();
			dbItems = DBselect(getSqlExecutor(),
				"SELECT src.graphid AS srcid,dest.graphid as destid"+
				" FROM graphs dest,graphs src,graphs_items destgi,items desti"+
				" WHERE dest.tenantid=src.tenantid"+
					" AND destgi.tenantid=dest.tenantid"+
					" AND destgi.tenantid=desti.tenantid"+
					" AND dest.name=src.name"+
					" AND destgi.graphid=dest.graphid"+
					" AND destgi.itemid=desti.itemid"+
					" AND desti.hostid="+sqlParts.marshalParam(templateId)+
					" AND "+sqlParts.dual.dbConditionInt("src.graphid", resourceGraphIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for (Map dbItem : dbItems) {
				Nest.value(resourceGraphsMap,dbItem.get("srcid")).$(Nest.value(dbItem,"destid").$());
			}

			CArray<Long> newScreenIds = insert("screens", Clone.deepcopy(screens));

			CArray insertScreenItems = array();
			Map params = new HashMap();
			for (Entry<Object, Map> e : screens.entrySet()) {
			    Object snum = e.getKey();
			    Map screen = e.getValue();
				for(Map screenItem : (CArray<Map>)Nest.value(screen,"screenitems").asCArray()) {
					Nest.value(screenItem,"screenid").$(newScreenIds.get(snum));
					Object rid = Nest.value(screenItem,"resourceid").$();
					params.put("rid", rid);
					params.put("templateId", templateId);
					switch (Nest.value(screenItem,"resourcetype").asInteger()) {
						case SCREEN_RESOURCE_GRAPH:
							if (!empty(rid) && !isset(resourceGraphsMap,rid)) {
								Map graph = DBfetch(DBselect(getSqlExecutor(),"SELECT g.name FROM graphs g WHERE g.graphid=#{rid}",params));
								Map template = DBfetch(DBselect(getSqlExecutor(),"SELECT h.name FROM hosts h WHERE h.hostid=#{templateId}",params));
								throw CDB.exception(
									RDA_API_ERROR_PARAMETERS,
									_s("Graph \"%1$s\" does not exist on template \"%2$s\".", Nest.value(graph,"name").$(), Nest.value(template,"name").$())
								);
							}
							Nest.value(screenItem,"resourceid").$(resourceGraphsMap.get(rid));
							break;
						default:
							if (!empty(rid) && !isset(resourceItemsMap,rid)) {
								Map item = DBfetch(DBselect(getSqlExecutor(),"SELECT i.name FROM items i WHERE i.itemid=#{rid}",params));
								Map template = DBfetch(DBselect(getSqlExecutor(),"SELECT h.name FROM hosts h WHERE h.hostid=#{templateId}",params));
								throw CDB.exception(
									RDA_API_ERROR_PARAMETERS,
									_s("Item \"%1$s\" does not exist on template \"%2$s\".", Nest.value(item,"name").$(), Nest.value(template,"name").$())
								);
							}
							Nest.value(screenItem,"resourceid").$(resourceItemsMap.get(rid));
					}
					insertScreenItems.add(screenItem);
				}
			}
			insert("screens_items", insertScreenItems);
		}
		return true;
	}
	
	/**
	 * Validates the input parameters for the create() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array _screens
	 */
	@Override
	protected void validateCreate(CArray<Map> screens) {
		CArray screenDbFields = map("name", null, "templateid", null);

		for(Map screen : screens) {
			if (!check_db_fields(screenDbFields, screen)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect input parameters."));
			}
		}

		CArray templateIds = rda_objectValues(screens, "templateid");

		CTemplateScreenGet options = new CTemplateScreenGet();
		options.setFilter("name", rda_objectValues(screens, "name").valuesAsString());
		options.setFilter("templateid", templateIds.valuesAsString());
		options.setOutput(new String[]{"name", "templateid"});
		options.setNopermissions(true);
		CArray<Map> dbScreens = get(options);

		CTemplateGet toptions = new CTemplateGet();
		toptions.setTemplateIds(templateIds.valuesAsLong());
		toptions.setOutput(new String[]{"name", "templateid"});
		toptions.setEditable(true);
		toptions.setPreserveKeys(true);
		CArray<Map> dbTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);

		for(Object templateId : templateIds) {
			if (!isset(dbTemplates,templateId)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		for(Map screen : screens) {
			for(Map dbScreen : dbScreens) {
				if (Nest.value(dbScreen,"name").$().equals(Nest.value(screen,"name").$()) && bccomp(Nest.value(dbScreen,"templateid").$(), Nest.value(screen,"templateid").$()) == 0) {
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_s("Template screen \"%1$s\" already exists.", Nest.value(screen,"name").$())
					);
				}
			}
		}
	}

	/**
	 * Update template screens.
	 *
	 * @param array _screens
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> screens) {
		// check hostids before doing anything
		checkObjectIds(screens, "screenid",
			_("No \"%1$s\" given for template screen."),
			_("Empty screen ID for template screen."),
			_("Incorrect template screen ID.")
		);

		CTemplateScreenGet options = new CTemplateScreenGet();
		options.setOutput(new String[]{"screenid", "hsize", "vsize", "templateid"});
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
	
	@Override
	protected void validateUpdate(CArray<Map> screens, CArray<Map> dbScreens) {
		for(Map screen : screens) {
			if (!isset(dbScreens,screen.get("screenid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		screens = extendObjects(tableName(), screens, new String[]{"name"});

		for(Map screen : screens) {
			// \"templateid\" is not allowed
			if (array_key_exists("templateid", screen)) {
				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s("Cannot update \"templateid\" for template screen \"%1$s\".", Nest.value(screen,"name").$())
				);
			}

			if (isset(screen,"name")) {
				CTemplateScreenGet options = new CTemplateScreenGet();
				options.setFilter("name", Nest.value(screen,"name").asString());
				options.setFilter("templateid", Nest.value(dbScreens,screen.get("screenid"),"templateid").asString());
				options.setNopermissions(true);
				options.setOutput(new String[]{"screenid"});
				CArray<Map> dbScreensExist = get(options);
				Map dbScreenExist = reset(dbScreensExist);

				if (!empty(dbScreenExist) && bccomp(Nest.value(dbScreenExist,"screenid").$(), Nest.value(screen,"screenid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Template screen \"%1$s\" already exists.", Nest.value(screen,"name").$()));
				}
			}
		}
	}
	
	/**
	 * Validate input for copy method.
	 *
	 * @param array _data["screenIds"]
	 * @param array _data["templateIds"]
	 */
	protected void validateCopy(CArray data) {
		Long[] screenIds = Nest.array(data,"screenIds").asLong();
		Long[] templateIds = Nest.array(data,"templateIds").asLong();

		CTemplateScreenGet options = new CTemplateScreenGet();
		options.setScreenIds(screenIds);
		options.setOutput(new String[]{"screenid", "name", "templateid"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbScreens = get(options);

		for(Long screenId : screenIds) {
			if (!isset(dbScreens,screenId)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		// check permissions on templates
		if (!API.Template(this.idBean, this.getSqlExecutor()).isWritable(templateIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		// check if screen with same name exists
		options = new CTemplateScreenGet();
		options.setFilter("name", rda_objectValues(dbScreens, "name").valuesAsString());
		options.setTemplateIds(templateIds);
		options.setOutput(new String[]{"name", "templateid"});
		options.setPreserveKeys(true);
		CArray<Map> dbExistingScreens = get(options);

		Map params = new HashMap();
		for(Map dbExistingScreen : dbExistingScreens) {
			params.put("templateid", Nest.value(dbExistingScreen,"templateid").$());
			Map dbTemplate = DBfetch(DBselect(getSqlExecutor(), "SELECT h.name FROM hosts h WHERE h.hostid=#{templateid}",params));
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s("Screen \"%1$s\" already exists on template \"%2$2\".", Nest.value(dbExistingScreen,"name").$(), Nest.value(dbTemplate,"name").$())
			);
		}
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (params.getCountOutput() == null) {
			// request the templateid field for inheritance to work
			addQuerySelect(fieldId("templateid"), sqlParts);
		}
	}	
}