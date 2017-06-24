package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.getSameGraphItemsForHost;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphItemGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with graph prototypes.
 *
 * @package API
 */
public class CGraphPrototypeDAO extends CGraphGeneralDAO<CGraphPrototypeGet> {
	
	public CGraphPrototypeDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "graphs", "g", new String[]{"graphid", "name", "graphtype"});
		
		errorMessages = array_merge(errorMessages, map(
			ERROR_TEMPLATE_HOST_MIX , _("Graph prototype \"%1$s\" with templated items cannot contain items from other hosts."),
			ERROR_MISSING_GRAPH_NAME , _("Missing \"name\" field for graph prototype."),
			ERROR_MISSING_GRAPH_ITEMS , _("Missing items for graph prototype \"%1$s\"."),
			ERROR_MISSING_REQUIRED_VALUE , _("No \"%1$s\" given for graph prototype."),
			ERROR_TEMPLATED_ID , _("Cannot update \"templateid\" for graph prototype \"%1$s\"."),
			ERROR_GRAPH_SUM , _("Cannot add more than one item with type \"Graph sum\" on graph prototype \"%1$s\".")
		));
	}
	

	@Override
	public <T> T get(CGraphPrototypeGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("graphs", "g.graphid");
		sqlParts.from.put("graphs", "graphs g");
		sqlParts.where.put("g.flags="+RDA_FLAG_DISCOVERY_PROTOTYPE);
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid",params.getGroupIds());
			sqlParts.where.put("hg.tenantid=i.tenantid");
			sqlParts.where.put("hg.hostid=i.hostid");
			sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
			sqlParts.where.put("gig","gi.graphid=g.graphid");
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
			sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hg","hg.groupid");
			}
		}

		// templateids
		if (!is_null(params.getTemplateIds())) {
			if (!is_null(params.getHostIds())) {
				params.setHostIds(array_merge(params.getHostIds(), params.getTemplateIds()));
			} else {
				params.setHostIds(params.getTemplateIds());
			}
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostid","i.hostid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("i.hostid",params.getHostIds());
			sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
			sqlParts.where.put("gig","gi.graphid=g.graphid");
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("i","i.hostid");
			}
		}

		// graphids
		if (!is_null(params.getGraphIds())) {
			sqlParts.where.dbConditionInt("g.graphid",params.getGraphIds());
		}

		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","gi.itemid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
			sqlParts.where.put("gig","gi.graphid=g.graphid");
			sqlParts.where.dbConditionInt("gi.itemid",params.getItemIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("gi","gi.itemid");
			}
		}
		
		// discoveryids
		if (!is_null(params.getDiscoveryIds())) {
			sqlParts.select.put("itemid","id.parent_itemid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("item_discovery","item_discovery id");
			sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
			sqlParts.where.put("gig","gi.graphid=g.graphid");
			sqlParts.where.put("giid.tenantid","gi.tenantid=id.tenantid");
			sqlParts.where.put("giid","gi.itemid=id.itemid");
			sqlParts.where.dbConditionInt("id.parent_itemid",params.getDiscoveryIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("id","id.parent_itemid");
			}
		}

		// templated
		if (!is_null(params.getTemplated())) {
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
			sqlParts.where.put("ggi.tenantid","g.tenantid=gi.tenantid");
			sqlParts.where.put("ggi","g.graphid=gi.graphid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (params.getTemplated()) {
				sqlParts.where.put("h.status=" + HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>" + HOST_STATUS_TEMPLATE);
			}
		}

		// inherited
		if (!is_null(params.getInherited())) {
			if (params.getInherited()) {
				sqlParts.where.put("g.templateid IS NOT NULL");
			} else {
				sqlParts.where.put("g.templateid IS NULL");
			}
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("graphs g", params, sqlParts);
		}

		// filter
		if (is_null(params.getFilter())) {
			params.setFilter(new HashMap(0));
		}
		
		if (params.getFilter() != null) {
			dbFilter("graphs g", params, sqlParts);
			if (isset(params.getFilter().get("host"))) {
				sqlParts.from.put("graphs_items","graphs_items gi");
				sqlParts.from.put("items","items i");
				sqlParts.from.put("hosts","hosts h");
				sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
				sqlParts.where.put("gig","gi.graphid=g.graphid");
				sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
				sqlParts.where.put("igi","i.itemid=gi.itemid");
				sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
				sqlParts.where.put("hi","h.hostid=i.hostid");
				sqlParts.where.dbConditionString("host","h.host",TArray.as(params.getFilter().get("host")).asString());
			}

			if (isset(params.getFilter().get("hostid"))) {
				sqlParts.from.put("graphs_items","graphs_items gi");
				sqlParts.from.put("items","items i");
				sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
				sqlParts.where.put("gig","gi.graphid=g.graphid");
				sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
				sqlParts.where.put("igi","i.itemid=gi.itemid");
				sqlParts.where.dbConditionInt("hostid","i.hostid",TArray.as(params.getFilter().get("hostid")).asLong());
			}
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
				Long id = (Long)row.get("graphid");
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(result.get(id).get("hosts"))) {
						result.get(id).put("hosts", new CArray());
					}
					((CArray)result.get(id).get("hosts")).add(map("hostid", row.remove("hostid")));
				}
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(result.get(id).get("items"))) {
						result.get(id).put("items", new CArray());
					}
					((CArray)result.get(id).get("items")).add(map("itemid", row.remove("itemid")));
				}
				result.get(id).putAll(row);
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
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	@Override
	protected void inherit(Map graph, Long[] hostids) {
		CTemplateGet toptions = new CTemplateGet();
		toptions.setItemIds(rda_objectValues(Nest.value(graph,"gitems").$(), "itemid").valuesAsLong());
		toptions.setOutput(new String[]{"templateid"});
		toptions.setNopermissions(true);
		CArray<Map> graphTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);

		if (empty(graphTemplates)) {
			return;
		}

		Map graphTemplate = reset(graphTemplates);

		CHostGet hoptions = new CHostGet();
		hoptions.setTemplateIds(Nest.value(graphTemplate,"templateid").asLong());
		hoptions.setOutput(new String[]{"hostid", "host"});
		hoptions.setPreserveKeys(true);
		hoptions.setHostIds(hostids);
		hoptions.setNopermissions(true);
		hoptions.setTemplatedHosts(true);
		CArray<Map> chdHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

		CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
		gpoptions.setGraphIds(Nest.value(graph,"graphid").asLong());
		gpoptions.setNopermissions(true);
		gpoptions.setFilter("flags");
		gpoptions.setSelectGraphItems(API_OUTPUT_EXTEND);
		gpoptions.setOutput(API_OUTPUT_EXTEND);
		graph = get(gpoptions);
		graph = reset(graph);

		for(Map chdHost: chdHosts) {
			Map tmpGraph = graph;
			Nest.value(tmpGraph,"templateid").$(Nest.value(graph,"graphid").$());

			Object gitems = getSameGraphItemsForHost(this.idBean, getSqlExecutor(), Nest.value(tmpGraph,"gitems").asCArray(), Nest.value(chdHost,"hostid").asLong());
			if (empty(Nest.value(tmpGraph,"gitems").$(gitems).$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Graph \"%1$s\" cannot inherit. No required items on \"%2$s\".", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$()));
			}

			if (Nest.value(tmpGraph,"ymax_itemid").asInteger() > 0) {
				Map ymaxItemid = getSameGraphItemsForHost(this.idBean, getSqlExecutor(),array((Map)map("itemid", Nest.value(tmpGraph,"ymax_itemid").$())), Nest.value(chdHost,"hostid").asLong());
				if (empty(ymaxItemid)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Graph \"%1$s\" cannot inherit. No required items on \"%2$s\" (Ymax value item).", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$()));
				}
				ymaxItemid = reset(ymaxItemid);
				Nest.value(tmpGraph,"ymax_itemid").$(Nest.value(ymaxItemid,"itemid").$());
			}
			if (Nest.value(tmpGraph,"ymin_itemid").asInteger() > 0) {
				Map yminItemid = getSameGraphItemsForHost(this.idBean, getSqlExecutor(), array((Map)map("itemid", Nest.value(tmpGraph,"ymin_itemid").$())), Nest.value(chdHost,"hostid").asLong());
				if (empty(yminItemid)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Graph \"%1$s\" cannot inherit. No required items on \"%2$s\" (Ymin value item).",
							Nest.value(tmpGraph,"name").$(),
							chdHost.get("host")
					));
				}
				yminItemid = reset(yminItemid);
				Nest.value(tmpGraph,"ymin_itemid").$(Nest.value(yminItemid,"itemid").$());
			}

			// check if templated graph exists
			gpoptions = new CGraphPrototypeGet();
			gpoptions.setFilter("templateid", Nest.value(tmpGraph,"graphid").asString());
			gpoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString(), Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			gpoptions.setOutput(API_OUTPUT_EXTEND);
			gpoptions.setSelectGraphItems(API_OUTPUT_EXTEND);
			gpoptions.setPreserveKeys(true);
			gpoptions.setHostIds(Nest.value(chdHost, "hostid").asLong());
			CArray<Map> chdGraphs = get(gpoptions);

			Map chdGraph;
			if (!empty(chdGraph = reset(chdGraphs))) {
				if (!rda_strtolower(Nest.value(tmpGraph,"name").asString()).equals(rda_strtolower(Nest.value(chdGraph,"name").asString()))
						&& exists(map("name" , Nest.value(tmpGraph,"name").$(), "hostids" , Nest.value(chdHost,"hostid").$()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Graph \"%1$s\" already exists on \"%2$s\".", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$())
					);
				} else if (!Nest.value(chdGraph,"flags").$().equals(Nest.value(tmpGraph,"flags").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Graph with same name but other type exist."));
				}

				Nest.value(tmpGraph,"graphid").$(Nest.value(chdGraph,"graphid").$());
				updateReal(CArray.valueOf(tmpGraph), CArray.valueOf(chdGraph));
			}
			// check if graph with same name and items exists
			else {
				gpoptions = new CGraphPrototypeGet();
				gpoptions.setFilter("name", Nest.value(tmpGraph,"name").asString());
				gpoptions.setFilter("flags");
				gpoptions.setOutput(API_OUTPUT_EXTEND);
				gpoptions.setSelectGraphItems(API_OUTPUT_EXTEND);
				gpoptions.setPreserveKeys(true);
				gpoptions.setNopermissions(true);
				gpoptions.setHostIds(Nest.value(chdHost, "hostid").asLong());
				chdGraph = get(gpoptions);
				if (!empty(chdGraph = reset(chdGraph))) {
					if (Nest.value(chdGraph,"templateid").asInteger() != 0) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Graph \"%1$s\" already exists on \"%2$s\" (inherited from another template).", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$()));
					} else if (!Nest.value(chdGraph,"flags").$().equals(Nest.value(tmpGraph,"flags").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Graph with same name but other type exist."));
					}

					CGraphItemGet gioptions = new CGraphItemGet();
					gioptions.setGraphIds(Nest.value(chdGraph,"graphid").asLong());
					gioptions.setOutput(API_OUTPUT_EXTEND);
					gioptions.setPreserveKeys(true);
					gioptions.setExpandData(true);
					gioptions.setNopermissions(true);
					CArray<Map> chdGraphItems = API.GraphItem(this.idBean, this.getSqlExecutor()).get(gioptions);

					if (count(chdGraphItems) == count(Nest.value(tmpGraph,"gitems").$())) {
						label2: for(Map gitem: (CArray<Map>)Nest.value(tmpGraph,"gitems").asCArray()) {
							for(Map chdItem: chdGraphItems) {
								if (Nest.value(gitem,"key_").$().equals(Nest.value(chdItem,"key_").$()) && bccomp(Nest.value(chdHost,"hostid").$(), Nest.value(chdItem,"hostid").$()) == 0) {
									continue label2;
								}
							}

							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Graph \"%1$s\" already exists on \"%2$s\" (items are not identical).", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$()));
						}

						Nest.value(tmpGraph,"graphid").$(Nest.value(chdGraph,"graphid").$());
						updateReal(CArray.valueOf(tmpGraph), CArray.valueOf(chdGraph));
					} else {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Graph \"%1$s\" already exists on \"%2$s\" (items are not identical).", Nest.value(tmpGraph,"name").$(), Nest.value(chdHost,"host").$()));
					}
				} else {
					Object graphid = createReal(tmpGraph);
					Nest.value(tmpGraph,"graphid").$(graphid);
				}
			}
			inherit(tmpGraph);
		}
	}
	
	/**
	 * Inherit template graphs from template to host.
	 *
	 * @param array data
	 *
	 * @return bool
	 */
	public void syncTemplates(CArray data) {
		Nest.value(data,"templateids").$(rda_toArray(Nest.value(data,"templateids").$()));
		Nest.value(data,"hostids").$(rda_toArray(Nest.value(data,"hostids").$()));

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbLinks = DBselect(getSqlExecutor(),
				"SELECT ht.hostid,ht.templateid"+
				" FROM hosts_templates ht"+
				" WHERE "+sqlParts.dual.dbConditionInt("ht.hostid", Nest.array(data,"hostids").asLong())+
					" AND "+sqlParts.dual.dbConditionInt("ht.templateid", Nest.array(data,"templateids").asLong()),
				sqlParts.getNamedParams()
			);
		
		CArray linkage = array();
		for(Map link : dbLinks) {
			if (!isset(linkage, link.get("templateid"))) {
				Nest.value(linkage, link.get("templateid")).$(array());
			}
			Nest.value(linkage, link.get("templateid"), link.get("hostid")).$(1);
		}

		CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
		gpoptions.setHostIds(Nest.array(data,"templateids").asLong());
		gpoptions.setPreserveKeys(true);
		gpoptions.setOutput(API_OUTPUT_EXTEND);
		gpoptions.setSelectGraphItems(API_OUTPUT_EXTEND);
		gpoptions.setFilter("flags");
		CArray<Map> graphs = get(gpoptions);

		for (Map graph : graphs) {
			for(Object hostid: Nest.value(data,"hostids").asCArray()) {
				if (isset(linkage.getNested(Nest.value(graph,"hosts", 0, "hostid").$(), hostid))) {
					inherit(CArray.valueOf(graph), CArray.valueOf(hostid).valuesAsLong());
				}
			}
		}
	}	
	
	public CArray delete(CArray graphids) {
		return delete(graphids, false);
	}
	
	/**
	 * Delete GraphPrototype.
	 *
	 * @param int|string|array _graphids
	 * @param bool             _nopermissions
	 *
	 * @return array
	 */
	public CArray delete(CArray graphids, boolean nopermissions) {
		if (empty(graphids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		graphids = rda_toArray(graphids);
		CArray delGraphPrototypeIds = Clone.deepcopy(graphids);

		CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
		gpoptions.setGraphIds(graphids.valuesAsLong());
		gpoptions.setEditable(true);
		gpoptions.setOutput(API_OUTPUT_EXTEND);
		gpoptions.setPreserveKeys(true);
		CArray<Map> delGraphs = get(gpoptions);

		if (!nopermissions) {
			for(Object graphid: graphids) {
				if (!isset(delGraphs.get(graphid))) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
				}
				if (Nest.value(delGraphs, graphid, "templateid").asInteger() != 0) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Cannot delete templated graphs."));
				}
			}
		}

		CArray parentGraphids = Clone.deepcopy(graphids);
		SqlBuilder sqlParts = null;
		do {
			sqlParts = new SqlBuilder();
			CArray<Map> dbGraphs = DBselect(getSqlExecutor(),
				"SELECT g.graphid FROM graphs g WHERE "+sqlParts.dual.dbConditionInt("g.templateid", parentGraphids.valuesAsLong()),
				sqlParts.getNamedParams());
			parentGraphids = array();
			for(Map dbGraph : dbGraphs) {
				parentGraphids.add(Nest.value(dbGraph,"graphid").$() );
				graphids.add(Nest.value(dbGraph,"graphid").$() );
			}
		} while (!empty(parentGraphids));

		graphids = array_unique(graphids);
		CArray createdGraphs = array();

		
		sqlParts = new SqlBuilder();
		CArray<Map> dbGraphs = DBselect(getSqlExecutor(),
				"SELECT gd.graphid FROM graph_discovery gd WHERE "+sqlParts.dual.dbConditionInt("gd.parent_graphid", graphids.valuesAsLong()),
				sqlParts.getNamedParams());
		for(Map graph : dbGraphs) {
			createdGraphs.put(graph.get("graphid"), Nest.value(graph,"graphid").$());
		}
		if (!empty(createdGraphs)) {
			Object result = API.Graph(this.idBean, this.getSqlExecutor()).delete(createdGraphs, true);
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete graphs created by low level discovery."));
			}
		}

		delete("graphs", (Map)map("graphid", graphids));

		for(Map graph: delGraphs) {
			info(_s("Graph prototype \"%s\" deleted.", Nest.value(graph,"name").$()));
		}

		return map("graphids" , delGraphPrototypeIds);
	}
	
	@Override
	protected Long createReal(Map graph) {
		// mark the graph as a graph prototype
		Nest.value(graph,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);

		return super.createReal(graph);
	}

	@Override
	protected void addRelatedObjects(CGraphPrototypeGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] graphids = result.keysAsLong();
		// adding Items
		if (!is_null(params.getSelectItems())
				&& !API_OUTPUT_COUNT.equals(params.getSelectItems())) {
			CRelationMap relationMap = createRelationMap(result, "graphid", "itemid", "graphs_items");
			CItemGet iparams = new CItemGet();
			iparams.setOutput(params.getSelectItems());
			iparams.setItemIds(relationMap.getRelatedLongIds());
			iparams.setWebItems(true);
			iparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.Item(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, datas, "items");
		}
		
		// adding discoveryRule
		if (!is_null(params.getSelectDiscoveryRule())){
			SqlBuilder sqlParts = new SqlBuilder();		
			sqlParts.select.put("id.parent_itemid");
			sqlParts.select.put("gi.graphid");
			sqlParts.from.put("item_discovery id");
			sqlParts.from.put("graphs_items gi");
			applyQueryTenantOptions("graphs_items", "gi", params, sqlParts);
			sqlParts.where.dbConditionInt("gi.graphid", graphids);
			sqlParts.where.put("gi.tenantid=id.tenantid");
			sqlParts.where.put("gi.itemid=id.itemid");
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "graphid", "parent_itemid");
			
			CDiscoveryRuleGet dparams = new CDiscoveryRuleGet();
			dparams.setOutput(params.getSelectDiscoveryRule());
			dparams.setItemIds(relationMap.getRelatedLongIds());
			dparams.setPreserveKeys(true);
			
			datas = API.DiscoveryRule(this.idBean, getSqlExecutor()).get(dparams);
			relationMap.mapOne(result, datas, "discoveryRule");
		}
	}

	/**
	 * Validate graph prototype specific data on Create method.
	 * Get allowed item ID's, check permissions, check if items have at least one prototype, do all general validation,
	 * and check for numeric item types.
	 *
	 * @param array _graphs
	 */
	@Override
	protected void validateCreate(CArray<Map> graphs) {
		CArray itemIds = validateItemsCreate(graphs);

		CItemGet ioptions = new CItemGet();
		ioptions.setItemIds(itemIds.valuesAsLong());
		ioptions.setWebItems(true);
		ioptions.setEditable(true);
		ioptions.setOutput(new String[]{"name", "value_type", "flags"});
		ioptions.setSelectItemDiscovery(new String[]{"parent_itemid"});
		ioptions.setPreserveKeys(true);
		ioptions.setFilter("flags", 
									Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString(),
									Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString(),
									Nest.as(RDA_FLAG_DISCOVERY_CREATED).asString());
		CArray<Map> allowedItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

		for(Object itemid: itemIds) {
			if (!isset(allowedItems.get(itemid))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}
		}

		checkDiscoveryRuleCount(graphs, allowedItems);

		super.validateCreate(graphs);

		CArray allowedValueTypes = array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64);

		for(Map graph: graphs) {
			for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
				if (!in_array(Nest.value(allowedItems, gitem.get("itemid"), "value_type").$(), allowedValueTypes)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
						"Cannot add a non-numeric item \"%1$s\" to graph prototype \"%2$s\".",
						Nest.value(allowedItems, gitem.get("itemid"), "name").$(),
						graph.get("name")
					));
				}
			}
		}
	}

	/**
	 * Validate graph prototype specific data on Update method.
	 * Get allowed item ID's, check permissions, check if items have at least one prototype, do all general validation,
	 * and check for numeric item types.
	 *
	 * @param array _graphs
	 * @param array _dbGraphs
	 */
	protected void validateUpdate(CArray<Map> graphs, CArray<Map> dbGraphs) {
		// check for \"itemid\" when updating graph prototype with only \"gitemid\" passed
		for(Map graph: graphs) {
			if (isset(graph,"gitems")) {
				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					if (isset(gitem,"gitemid") && !isset(gitem,"itemid")) {
						CArray dbGitems = rda_toHash(Nest.value(dbGraphs, graph.get("graphid"), "gitems").asCArray(), "gitemid");
						Nest.value(gitem,"itemid").$(Nest.value(dbGitems, gitem.get("gitemid"), "itemid").$());
					}
				}
			}
		}

		CArray itemIds = validateItemsUpdate(graphs);

		CItemGet ioptions = new CItemGet();
		ioptions.setItemIds(itemIds.valuesAsLong());
		ioptions.setWebItems(true);
		ioptions.setEditable(true);
		ioptions.setOutput(new String[]{"name", "value_type", "flags"});
		ioptions.setSelectItemDiscovery(new String[]{"parent_itemid"});
		ioptions.setPreserveKeys(true);
		ioptions.setFilter("flags", 
									Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString(),
									Nest.as(RDA_FLAG_DISCOVERY_PROTOTYPE).asString(),
									Nest.as(RDA_FLAG_DISCOVERY_CREATED).asString());
		CArray<Map> allowedItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

		for(Object itemId: itemIds) {
			if (!isset(allowedItems,itemId)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}
		}

		checkDiscoveryRuleCount(graphs, allowedItems);

		super.validateUpdate(graphs, dbGraphs);

		CArray allowedValueTypes = array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64);

		for(Map item: allowedItems) {
			if (!in_array(Nest.value(item,"value_type").$(), allowedValueTypes)) {
				Object graphName = null; //TODO: Nest.value(_graph,"name").$()
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Cannot add a non-numeric item \"%1$s\" to graph prototype \"%2$s\".", Nest.value(item,"name").$(), graphName)
				);
			}
		}
	}

	/**
	 * Check if graph prototype has at least one item prototype and belongs to one discovery rule.
	 *
	 * @throws APIException if graph prototype has no item prototype or items belong to multiple discovery rules.
	 *
	 * @param array  _graphs				array of graphs
	 * @param array  _graphs["gitems"]		array of graphs items
	 * @param string _graphs["name"]		graph name
	 * @param array  _items					array of existing graph items and ones that user has permission to access
	 */
	protected void checkDiscoveryRuleCount(CArray<Map> graphs, CArray<Map> items) {
		for(Map graph: graphs) {
			// for update method we will skip this step, if no items are set
			if (isset(graph,"gitems")) {
				CArray itemDiscoveryIds = array();

				for(Map gitem: (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
					if (Nest.value(items, gitem.get("itemid"), "flags").asInteger() == RDA_FLAG_DISCOVERY_PROTOTYPE) {
						itemDiscoveryIds.put(Nest.value(items, gitem.get("itemid"), "itemDiscovery", "parent_itemid").$(), true); 
					}
				}

				if (count(itemDiscoveryIds) > 1) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
						"Graph prototype \"%1$s\" contains item prototypes from multiple discovery rules.",
						graph.get("name")
					));
				} else if (empty(itemDiscoveryIds)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
						"Graph prototype \"%1$s\" must have at least one item prototype.",
						graph.get("name")
					));
				}
			}
		}
	}
}
