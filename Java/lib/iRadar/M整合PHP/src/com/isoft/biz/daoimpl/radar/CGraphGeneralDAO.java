package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SUM;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_FIXED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_ITEM_VALUE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CGraphGeneralGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CGraphItemGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CColorValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.object.CUpdateDiscoveredValidator;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with graphs.
 *
 * @package API
 */
public abstract class CGraphGeneralDAO<P extends CGraphGeneralGet> extends CCoreLongKeyDAO<P> {

	protected static String ERROR_TEMPLATE_HOST_MIX = "templateHostMix";
	protected static String ERROR_MISSING_GRAPH_NAME = "missingGraphName";
	protected static String ERROR_MISSING_GRAPH_ITEMS = "missingGraphItems";
	protected static String ERROR_MISSING_REQUIRED_VALUE = "missingRequiredValue";
	protected static String ERROR_TEMPLATED_ID = "templatedId";
	protected static String ERROR_GRAPH_SUM = "graphSum";
	
	public CGraphGeneralDAO(IIdentityBean idBean, SQLExecutor executor, String tableName,
			String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}


	/**
	 * Update graphs.
	 *
	 * @param array _graphs
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> graphs) {
		graphs = rda_toArray(graphs);
		CArray graphIds = rda_objectValues(graphs, "graphid");

		graphs = extendObjects(tableName(), graphs,
			new String[]{"name", "graphtype", "ymin_type", "ymin_itemid", "ymax_type", "ymax_itemid", "yaxismin", "yaxismax"}
		);

		P goptions = getParamInstance();
		goptions.setOutput(API_OUTPUT_EXTEND);
		goptions.setSelectGraphItems(API_OUTPUT_EXTEND);
		goptions.put("graphids", graphIds);
		goptions.setEditable(true);
		goptions.setPreserveKeys(true);
		
		CArray<Map> dbGraphs = get(goptions);

		CUpdateDiscoveredValidator _updateDiscoveredValidator = CValidator.init(new CUpdateDiscoveredValidator(),map(
			"messageAllowed", _("Cannot update a discovered graph.")
		));

		for (Map graph : graphs) {
			// check permissions
			if (!isset(dbGraphs.get(graph.get("graphid")))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}

			// cannot update discovered graphs
			checkPartialValidator(CArray.valueOf(graph), _updateDiscoveredValidator, Nest.value(dbGraphs, graph.get("graphid")).asCArray());

			// validate items on set or pass existing items from DB
			if (isset(graph,"gitems")) {
				for(Map item: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					if (isset(item,"gitemid") && empty(Nest.value(item,"gitemid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Missing \"gitemid\" field for item."));
					}

					if (isset(Nest.value(item,"gitemid").$()) && !empty(Nest.value(item,"gitemid").$())) {
						CArray _validGraphItemIds = array();

						for(Map _dbItem: (CArray<Map>)Nest.value(dbGraphs, graph.get("graphid"), "gitems").asCArray()) {
							_validGraphItemIds.put(_dbItem.get("gitemid"), Nest.value(_dbItem,"gitemid").$());
						}

						if (!in_array(Nest.value(item,"gitemid").$(), _validGraphItemIds)) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_("No permissions to referred object or it does not exist!")
							);
						}
					}
				}
			}
			else {
				Nest.value(graph,"gitems").$(Nest.value(dbGraphs, graph.get("graphid"), "gitems").$());
			}
		}
//		unset(_graph);

		validateUpdate(Clone.deepcopy(graphs), Clone.deepcopy(dbGraphs));

		for(Map _graph: graphs) {
			unset(_graph,"templateid");

			Nest.value(_graph,"gitems").$(isset(Nest.value(_graph,"gitems").$()) ? Nest.value(_graph,"gitems").$() : Nest.value(dbGraphs, _graph.get("graphid"), "gitems").$());

			// Y axis min clean unused fields
			if (isset(Nest.value(_graph,"ymin_type").$())) {
				if (Nest.value(_graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
					Nest.value(_graph,"yaxismin").$(null);
				}
				else {
					Nest.value(_graph,"ymin_itemid").$(null);
				}
			}

			// Y axis max clean unused fields
			if (isset(Nest.value(_graph,"ymax_type").$())) {
				if (Nest.value(_graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
					Nest.value(_graph,"yaxismax").$(null);
				}
				else {
					Nest.value(_graph,"ymax_itemid").$(null);
				}
			}

			updateReal(CArray.valueOf(Clone.deepcopy(_graph)), Nest.value(dbGraphs, _graph.get("graphid")).asCArray());
			inherit(_graph);
		}

		return map("graphids", graphIds);
	}


	/**
	 * Create graphs.
	 *
	 * @param array _graphs
	 *
	 * @return array
	 */
	public CArray create(CArray<Map> graphs) {
		graphs = rda_toArray(graphs);
		CArray graphids = array();

		// set default parameters
		for(Map graph: graphs) {
			if (!isset(graph.get("graphtype"))) {
				graph.put("graphtype", GRAPH_TYPE_NORMAL);
			}
			if (!isset(graph.get("ymin_type"))) {
				graph.put("ymin_type", GRAPH_YAXIS_TYPE_CALCULATED);
			}
			if (!isset(graph.get("ymax_type"))) {
				graph.put("ymax_type", GRAPH_YAXIS_TYPE_CALCULATED);
			}
		}
		
		validateCreate(Clone.deepcopy(graphs));
		
		for(Map graph: graphs) {
			graph.put("graphid", this.createReal(Clone.deepcopy(graph)));
			this.inherit(graph);
			graphids.add(graph.get("graphid"));
		}

		return map("graphids", graphids);
	}
	
	/**
	 * Creates a new graph and returns it's ID.
	 *
	 * @param _graph
	 *
	 * @return mixed
	 */
	protected Long createReal(Map _graph) {
		CArray<Long> _graphids = this.insert("graphs", array(Clone.deepcopy(_graph)));
		Long _graphid = reset(_graphids);

		for(Map _gitem: (CArray<Map>)Nest.value(_graph,"gitems").asCArray()) {
			Nest.value(_gitem,"graphid").$(_graphid);
		}
//		unset(_gitem);

		insert("graphs_items", Nest.value(_graph,"gitems").asCArray());

		return _graphid;
	}
	
	/**
	 * Updates the graph if _graph differs from _dbGraph.
	 *
	 * @param array _graph
	 * @param array _dbGraph
	 *
	 * @return string
	 */
	protected String updateReal(CArray _graph, CArray _dbGraph) {
		CArray _dbGitems = rda_toHash(Nest.value(_dbGraph,"gitems").$(), "gitemid");
		CArray _dbGitemIds = rda_toHash(rda_objectValues(_dbGitems, "gitemid"));

		// update the graph if it's modified
		if (recordModified("graphs", _dbGraph, _graph)) {
			updateByPk(tableName(), Nest.value(_graph,"graphid").$(), _graph);
		}

		// delete remaining items only if new items or items that require update are set
		if (!empty(Nest.value(_graph,"gitems").$())) {
			CArray _insertGitems = array();
			CArray _deleteGitemIds = Clone.deepcopy(_dbGitemIds);

			for(Map _gitem: (CArray<Map>)Nest.value(_graph,"gitems").asCArray()) {
				// updating an existing item
				if (!empty(Nest.value(_gitem,"gitemid").$()) && isset(Nest.value(_dbGitemIds, _gitem.get("gitemid")).$())) {
					if (recordModified("graphs_items", Nest.value(_dbGitems, _gitem.get("gitemid")).asCArray(), _gitem)) {
						updateByPk("graphs_items", Nest.value(_gitem,"gitemid").$(), _gitem);
					}

					// remove this graph item from the collection so it won't get deleted
					unset(_deleteGitemIds, _gitem.get("gitemid"));
				}
				// adding a new item
				else {
					Nest.value(_gitem,"graphid").$(Nest.value(_graph,"graphid").$());
					_insertGitems.add( _gitem );
				}
			}

			if (!empty(_deleteGitemIds)) {
				delete("graphs_items", (Map)map("gitemid", _deleteGitemIds));
			}

			if (!empty(_insertGitems)) {
				insert("graphs_items", _insertGitems);
			}
		}

		return Nest.value(_graph,"graphid").asString();
	}
	
	/**
	 * Check if object exist.
	 *
	 * @param array _object
	 *
	 * @return bool
	 */
	@Override
	public boolean exists(CArray _object) {
		P goptions = getParamInstance();
		goptions.setFilter("flags");
		goptions.setOutput(new String[]{"graphid"});
		goptions.setNopermissions(true);
		goptions.setLimit(1);
		if (isset(Nest.value(_object,"name").$())) {
			goptions.setFilter("name", Nest.value(_object,"name").asString());
		}
		if (isset(Nest.value(_object,"host").$())) {
			goptions.setFilter("host", Nest.value(_object,"host").asString());
		}
		if (isset(Nest.value(_object,"hostids").$())) {
			goptions.put("hostids",Nest.array(_object,"hostids").asString());
		}

		CArray<Map> _objs = get(goptions);
		return !empty(_objs);
	}
	
	/**
	 * Get graphid by graph name.
	 *
	 * params: hostids, name
	 *
	 * @param array _graphData
	 *
	 * @return string|boolean
	 */
	@Override
	public CArray<Map> getObjects(Map<String, Object[]> _graphData) {
		P _options = getParamInstance();
		_options.setFilter(_graphData);
		_options.setOutput(API_OUTPUT_EXTEND);
		return this.get(_options);
	}
	
	protected void checkAxisItems(CArray _graph) {
		checkAxisItems(_graph, null);
	}
	/**
	 * Check values for Y axis items and values.
	 *
	 * @param array _graph
	 * @param bool  _tpl
	 */
	protected void checkAxisItems(CArray _graph, Long _tpl) {
		CArray _axisItems = array();
		if (isset(Nest.value(_graph,"ymin_type").$()) && Nest.value(_graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
			Nest.value(_axisItems, _graph.get("ymin_itemid")).$(Nest.value(_graph,"ymin_itemid").$());
		}
		if (isset(Nest.value(_graph,"ymax_type").$()) && Nest.value(_graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
			Nest.value(_axisItems, _graph.get("ymax_itemid")).$(Nest.value(_graph,"ymax_itemid").$());
		}

		if (!empty(_axisItems)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(_axisItems.valuesAsLong());
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setCountOutput(true);
			ioptions.setWebItems(true);
			ioptions.setFilter("flags");
			ioptions.setFilter("value_type", 
									   Nest.as(ITEM_VALUE_TYPE_FLOAT).asString(),
									   Nest.as(ITEM_VALUE_TYPE_UINT64).asString());
			if (!empty(_tpl)) {
				ioptions.setHostIds(_tpl);
			} else {
				ioptions.setTemplated(false);
			}

			Long _cntExist = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

			if (_cntExist != count(_axisItems)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect item for axis value."));
			}
		}

		// more than one sum type item for pie graph
		if (Nest.value(_graph,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(_graph,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED) {
			int _sumItems = 0;
			for(Map _gitem: (CArray<Map>)Nest.value(_graph,"gitems").asCArray()) {
				if (Nest.value(_gitem,"type").asInteger() == GRAPH_ITEM_SUM) {
					_sumItems++;
				}
			}
			if (_sumItems > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s(getErrorMsg(ERROR_GRAPH_SUM), Nest.value(_graph,"name").$())
				);
			}
		}

		// Y axis MIN value < Y axis MAX value
		if ((Nest.value(_graph,"graphtype").asInteger() == GRAPH_TYPE_NORMAL || Nest.value(_graph,"graphtype").asInteger() == GRAPH_TYPE_STACKED)
				&& Nest.value(_graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_FIXED
				&& Nest.value(_graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_FIXED
				&& Nest.value(_graph,"yaxismin").asInteger() >= Nest.value(_graph,"yaxismax").asInteger()) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Y axis MAX value must be greater than Y axis MIN value."));
		}
	}

	@Override
	protected void addRelatedObjects(P params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] graphids = result.keysAsLong();
		
		// adding GraphItems
		if (!is_null(params.getSelectGraphItems()) && !API_OUTPUT_COUNT.equals(params.getSelectGraphItems())) {
			CGraphItemGet tparams = new CGraphItemGet();
			tparams.setOutput(outputExtend("graphs_items", new String[] {"graphid", "gitemid"}, params.getSelectGraphItems()));
			tparams.setGraphIds(graphids);
			tparams.setPreserveKeys(true);
			
			CGraphItemDAO idao = API.GraphItem(this.idBean, getSqlExecutor());
			CArray<Map> gitems = idao.select(idao.tableName, tparams);
			
			CRelationMap relationMap = this.createRelationMap(gitems, "graphid", "gitemid");
			this.unsetExtraFields(gitems, new String[] {"graphid", "gitemid"}, params.getSelectGraphItems());
			relationMap.mapMany(result, gitems, "gitems");
		}
		
		// adding HostGroups
		if (!is_null(params.getSelectGroups()) && !API_OUTPUT_COUNT.equals(params.getSelectGroups())) {
			CRelationMap relationMap = new CRelationMap();
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(getSqlExecutor(),
				"SELECT gi.graphid,hg.groupid"+
				" FROM graphs_items gi,items i,hosts_groups hg"+
				" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "graphs_items", "gi", params)+
				    " AND "+sqlParts.dual.dbConditionInt("gi.graphid", graphids)+
					" AND gi.tenantid=i.tenantid"+
					" AND i.tenantid=hg.tenantid"+
					" AND gi.itemid=i.itemid"+
					" AND i.hostid=hg.hostid",
				sqlParts.getNamedParams()
			);
			
			for (Map relation : dbRules) {
				relationMap.addRelation(Nest.value(relation,"graphid").$(), Nest.value(relation,"groupid").$());
			}
			
			CHostGroupGet iparams = new CHostGroupGet();
			iparams.setOutput(params.getSelectGroups());
			iparams.setGroupIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> groups = API.HostGroup(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, groups, "groups");
		}
		
		// adding Hosts
		if (!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())) {
			CRelationMap relationMap = new CRelationMap();
			
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(getSqlExecutor(),
				"SELECT gi.graphid,i.hostid"+
				" FROM graphs_items gi,items i"+
				" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "graphs_items", "gi", params)+
				    " AND "+sqlParts.dual.dbConditionInt("gi.graphid", graphids)+
					" AND gi.tenantid=i.tenantid"+
					" AND gi.itemid=i.itemid",
				sqlParts.getNamedParams()
			);
			
			for (Map relation : dbRules) {
				relationMap.addRelation(Nest.value(relation,"graphid").$(), Nest.value(relation,"hostid").$());
			}
						
			CHostGet iparams = new CHostGet();
			iparams.setOutput(params.getSelectHosts());
			iparams.setHostIds(relationMap.getRelatedLongIds());
			iparams.setTemplatedHosts(true);
			iparams.setPreserveKeys(true);
			
			CArray<Map> hosts = API.Host(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, hosts, "hosts");
		}
		
		// adding Templates
		if (!is_null(params.getSelectTemplates()) && !API_OUTPUT_COUNT.equals(params.getSelectTemplates())) {
			CRelationMap relationMap = new CRelationMap();
			
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(getSqlExecutor(),
				"SELECT gi.graphid,i.hostid"+
				" FROM graphs_items gi,items i"+
				" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "graphs_items", "gi", params)+
				    " AND "+sqlParts.dual.dbConditionInt("gi.graphid", graphids)+
					" AND gi.tenantid=i.tenantid"+
					" AND gi.itemid=i.itemid",
				sqlParts.getNamedParams()
			);
			
			for (Map relation : dbRules) {
				relationMap.addRelation(Nest.value(relation,"graphid").$(), Nest.value(relation,"hostid").$());
			}
			
			CTemplateGet iparams = new CTemplateGet();
			iparams.setOutput(params.getSelectTemplates());
			iparams.setTemplateIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> templates = API.Template(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, templates, "templates");
		}
	}
	
	/**
	 * Validate graph name and graph items including Y axis item ID's and graph item fields on Create method
	 * and return valid item ID's on success or trow an error on failure.
	 *
	 * @param array _graphs
	 *
	 * @return array
	 */
	protected CArray validateItemsCreate(CArray<Map> graphs) {
		CArray itemIds = array();

		for(Map graph: graphs) {
			// validate graph name
			CArray fields = map("name", null);
			if (!check_db_fields(fields, graph)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _(getErrorMsg(ERROR_MISSING_GRAPH_NAME)));
			}

			// graph items are mandatory
			if (!isset(graph,"gitems") || !isArray(Nest.value(graph,"gitems").$()) || empty(Nest.value(graph,"gitems").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s(getErrorMsg(ERROR_MISSING_GRAPH_ITEMS), Nest.value(graph,"name").$())
				);
			}

			// validate item fields
			if (isset(graph,"gitems")) {
				fields = map("itemid", null);
				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					// \"itemid\" is required
					if (!check_db_fields(fields, gitem)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Missing \"itemid\" field for item."));
					}

					// assigning with key preserves unique itemids
					Nest.value(itemIds,  gitem.get("itemid")).$(Nest.value(gitem,"itemid").$());
				}
			}

			// add Y axis item IDs for persmission validation
			if (isset(graph,"ymin_type") && Nest.value(graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				if (!isset(graph,"ymin_itemid") || rda_empty(Nest.value(graph,"ymin_itemid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s(getErrorMsg(ERROR_MISSING_REQUIRED_VALUE), "ymin_itemid")
					);
				} else {
					Nest.value(itemIds,  graph.get("ymin_itemid")).$(Nest.value(graph,"ymin_itemid").$());
				}
			}
			if (isset(graph,"ymax_type") && Nest.value(graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				if (!isset(Nest.value(graph,"ymax_itemid").$()) || rda_empty(Nest.value(graph,"ymax_itemid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s(getErrorMsg(ERROR_MISSING_REQUIRED_VALUE), "ymax_itemid")
					);
				} else {
					Nest.value(itemIds,  graph.get("ymax_itemid")).$(Nest.value(graph,"ymax_itemid").$());
				}
			}
		}

		return itemIds;
	}
	
	/**
	 * Validate graph gerenal data on Create method.
	 * Check if new items are from same templated host, validate Y axis items and values and hosts and templates.
	 *
	 * @param array _graphs
	 */
	protected void validateCreate(CArray<Map> graphs) {
		CColorValidator colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map graph: graphs) {
			// check for \"templateid\", because it is not allowed
			if (array_key_exists("templateid", graph)) {
				String error = _s(getErrorMsg(ERROR_TEMPLATED_ID), Nest.value(graph,"name").$());
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, error);
			}

			Long templatedGraph = null;
			if (isset(graph,"gitems")) {
				// check if new items are from same templated host
				CHostGet hoptions = new CHostGet();
				hoptions.setItemIds(rda_objectValues(Nest.value(graph,"gitems").$(), "itemid").valuesAsLong());
				hoptions.setOutput(new String[]{"hostid", "status"});
				hoptions.setEditable(true);
				hoptions.setTemplatedHosts(true);
				CArray<Map> graphHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

				// check - items from one template. at least one item belongs to template
				for(Map host: graphHosts) {
					if (HOST_STATUS_TEMPLATE == Nest.value(host,"status").asInteger()) {
						templatedGraph = Nest.value(host,"hostid").asLong();
						break;
					}
				}

				if (!empty(templatedGraph) && count(graphHosts) > 1) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s(getErrorMsg(ERROR_TEMPLATE_HOST_MIX), Nest.value(graph,"name").$())
					);
				}

				// check color
				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					if (!isset(gitem,"color")) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s(getErrorMsg(ERROR_MISSING_REQUIRED_VALUE), "color")
						);
					}

					if (!colorValidator.validate(this.idBean, Nest.value(gitem,"color").asString())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, colorValidator.getError());
					}
				}
			}

			// check graph type and ymin/ymax items
			checkAxisItems(CArray.valueOf(graph), templatedGraph);
		}

		validateHostsAndTemplates(graphs);
	}
	


	/**
	 * Validate graph items including valid Y axis item ID's on Update method
	 * and return valid item ID's on success or trow an error on failure.
	 *
	 * @param array _graphs
	 *
	 * @return array
	 */
	protected CArray validateItemsUpdate(CArray<Map> graphs) {
		CArray itemIds = array();
		CArray dbFields = map("itemid", null);
		
		for(Map graph: graphs) {
			// graph items are optional
			if (isset(graph,"gitems") && (!isArray(Nest.value(graph,"gitems").$()) || empty(Nest.value(graph,"gitems").$()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s(getErrorMsg(ERROR_MISSING_GRAPH_ITEMS), Nest.value(graph,"name").$())
				);
			}

			// validate item fields
			if (isset(graph,"gitems")) {
				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					// \"itemid\" is required only if no \"gitemid\" is set
					if (!isset(gitem,"gitemid") && !check_db_fields(dbFields, gitem)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Missing \"itemid\" field for item."));
					}

					// assigning with key preserves unique itemids
					Nest.value(itemIds,  gitem.get("itemid")).$(Nest.value(gitem,"itemid").$());
				}
			}

			// add Y min axis item IDs for persmission validation
			if (isset(graph,"ymin_type") && Nest.value(graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				if (!isset(graph,"ymin_itemid") || rda_empty(Nest.value(graph,"ymin_itemid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s(getErrorMsg(ERROR_MISSING_REQUIRED_VALUE), "ymin_itemid")
					);
				} else {
					Nest.value(itemIds,  graph.get("ymin_itemid")).$(Nest.value(graph,"ymin_itemid").$());
				}
			}

			// add Y max axis item IDs for persmission validation
			if (isset(graph,"ymax_type") && Nest.value(graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				if (!isset(graph,"ymax_itemid") || rda_empty(Nest.value(graph,"ymax_itemid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s(getErrorMsg(ERROR_MISSING_REQUIRED_VALUE), "ymax_itemid")
					);
				} else {
					Nest.value(itemIds,  graph.get("ymax_itemid")).$(Nest.value(graph,"ymax_itemid").$());
				}
			}
		}

		return itemIds;
	}
	
	/**
	 * Validate graph general data on Update method.
	 * When updating graph check to what host graph belongs to and trow an error if new items added from other hosts.
	 * Includes Y axis validation and if graph already exists somewhere in DB.
	 *
	 * @param array _graphs
	 * @param array _dbGraphs
	 */
	protected void validateUpdate(CArray<Map> graphs, CArray<Map> dbGraphs) {
		CColorValidator colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map graph: graphs) {
			// check for \"templateid\", because it is not allowed
			if (array_key_exists("templateid", graph)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s(getErrorMsg(ERROR_TEMPLATED_ID), Nest.value(graph,"name").$())
				);
			}

			Long templatedGraph = null;

			if (isset(graph,"gitems")) {
				// first item determines to which host graph belongs to
				Map _gitem = array_shift(Nest.value(dbGraphs, graph.get("graphid"), "gitems").asCArray());

				CHostGet hoptions = new CHostGet();
				hoptions.setItemIds(Nest.value(_gitem,"itemid").asLong());
				hoptions.setOutput(new String[]{"hostid", "status"});
				hoptions.setEditable(true);
				hoptions.setTemplatedHosts(true);
				CArray<Map> graphHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

				Map host = array_shift(graphHosts);

				// if the current graph is templated and new items to be added
				if (HOST_STATUS_TEMPLATE == Nest.value(host,"status").asInteger()) {
					templatedGraph = Nest.value(host,"hostid").asLong();

					CArray itemIds = array();

					for(Map gitem : (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
						if (!isset(Nest.value(gitem,"gitemid").$()) && isset(Nest.value(gitem,"itemid").$())) {
							itemIds.add( Nest.value(gitem,"itemid").$() );
						}
					}

					if (!empty(itemIds)) {
						hoptions = new CHostGet();
						hoptions.setItemIds(itemIds.valuesAsLong());
						hoptions.setOutput(new String[]{"hostid"});
						hoptions.setEditable(true);
						hoptions.setTemplatedHosts(true);
						CArray<Map> itemHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

						// only one host is allowed and it has to be the current. other templated hosts are allowed
						itemHosts = array_unique(rda_objectValues(itemHosts, "hostid"));

						if (count(itemHosts) > 1 || !in_array(Nest.value(host,"hostid").$(), itemHosts)) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_s(getErrorMsg(ERROR_TEMPLATE_HOST_MIX), Nest.value(graph,"name").$())
							);
						}
					}
				}

				// items fields
				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					// check color
					if (isset(Nest.value(gitem,"color").$()) && !colorValidator.validate(this.idBean, Nest.value(gitem,"color").asString())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, colorValidator.getError());
					}
				}
			}

			// check ymin, ymax items
			checkAxisItems(CArray.valueOf(graph), templatedGraph);
		}

		validateHostsAndTemplates(graphs);
	}
	
	/**
	 * Check if graph already exists somewhere in DB.
	 *
	 * @param array _graphs
	 */
	protected void validateHostsAndTemplates(CArray<Map> _graphs) {
		CArray _graphNames = array();

		for(Map _graph: _graphs) {
			// check if the host has any graphs in DB with the same name within host
			CHostGet hoptions = new CHostGet();
			hoptions.setItemIds(rda_objectValues(Nest.value(_graph,"gitems").$(), "itemid").valuesAsLong());
			hoptions.setOutput(new String[]{"hostid"});
			hoptions.setNopermissions(true);
			hoptions.setPreserveKeys(true);
			hoptions.setTemplatedHosts(true);
			CArray<Map> _hostsAndTemplates = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

			CArray _hostAndTemplateIds = array_keys(_hostsAndTemplates);

			CGraphGet goptions = new CGraphGet();
			goptions.setHostIds(_hostAndTemplateIds.valuesAsLong());
			goptions.setOutput(new String[]{"graphid"});
			goptions.setFilter("name" , Nest.value(_graph,"name").asString());
			goptions.setFilter("flags");
			goptions.setNopermissions(true);
			CArray<Map> _dbGraphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);

			if (!empty(_dbGraphs)) {
				boolean _duplicateGraphsFound = false;

				if (isset(Nest.value(_graph,"graphid").$())) {
					for(Map _dbGraph: _dbGraphs) {
						if (bccomp(Nest.value(_dbGraph,"graphid").$(), Nest.value(_graph,"graphid").$()) != 0) {
							_duplicateGraphsFound = true;
							break;
						}
					}
				}
				else {
					_duplicateGraphsFound = true;
				}

				if (_duplicateGraphsFound) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Graph with name \"%1$s\" already exists in graphs or graph prototypes.", Nest.value(_graph,"name").$())
					);
				}
			}

			// checks that there are no two graphs with the same name within host
			for(Object _id: _hostAndTemplateIds) {
				if (!isset(Nest.value(_graphNames, _graph.get("name")).$())) {
					Nest.value(_graphNames, _graph.get("name")).$(array());
				}

				if (isset(Nest.value(_graphNames, _graph.get("name"), _id).$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("More than one graph with name \"%1$s\" within host.", Nest.value(_graph,"name").$())
					);
				}
				else {
					Nest.value(_graphNames, _graph.get("name"), _id).$(true);
				}
			}
		}
	}
	
	
	/**
	 * 子类需要实现的抽象方法
	 * 
	 * @param _graph
	 */
	protected void inherit(Map graph) {
		inherit(graph, null);
	}

	protected void inherit(Map graph, Long[] hostids) {
	}

}
