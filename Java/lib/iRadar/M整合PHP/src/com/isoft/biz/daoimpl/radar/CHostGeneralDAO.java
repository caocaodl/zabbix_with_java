package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_RULE;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CAppManager;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CUserMacroGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with hosts.
 * @author benne
 */
@CodeConfirmed("benne.2.2.6")
public abstract class CHostGeneralDAO<P extends CParamGet> extends CHostBaseDAO<P> {

	public CHostGeneralDAO(IIdentityBean idBean, SQLExecutor executor, String[] sortColumns) {
		super(idBean, executor, sortColumns);
	}
	
	/**
	 * Allows to:
	 * - add hosts to groups;
	 * - link templates to hosts;
	 * - add new macros to hosts.
	 *
	 * Supported data parameters are:
	 * - hosts          - an array of hosts to be updated
	 * - templates      - an array of templates to be updated
	 * - groups         - an array of host groups to add the host to
	 * - templates_link - an array of templates to link to the hosts
	 * - macros         - an array of macros to create on the host
	 *
	 * @param array data
	 *
	 * @return array
	 */
	public CArray massAdd(CArray data) {
		CArray hostIds = rda_objectValues(Nest.value(data,"hosts").$(), "hostid");
		CArray templateIds = rda_objectValues(Nest.value(data,"templates").$(), "templateid");

		CArray allHostIds = array_merge(hostIds, templateIds);

		// add groups
		if (!empty(Nest.value(data,"groups").$())) {
			API.HostGroup(this.idBean, this.getSqlExecutor()).massAdd(map(
				"hosts",  data.get("hosts"),
				"templates",  data.get("templates"),
				"groups",  data.get("groups")
			));
		}

		// link templates
		if (!empty(Nest.value(data,"templates_link").$())) {
			link(rda_objectValues(rda_toArray(Nest.value(data,"templates_link").$()), "templateid"), allHostIds);
		}

		// create macros
		if (!empty(Nest.value(data,"macros").$())) {
			Nest.value(data,"macros").$(rda_toArray(Nest.value(data,"macros").$())) ;

			CArray hostMacrosToAdd = array();
			for (Map hostMacro: (CArray<Map>)Nest.value(data,"macros").asCArray()) {
				for(Object hostid: allHostIds) {
					hostMacro.put("hostid", hostid);
					hostMacrosToAdd.add(hostMacro);
				}
			}

			API.UserMacro(this.idBean, this.getSqlExecutor()).create(hostMacrosToAdd);
		}

		CArray ids = map("hostids",  hostIds, "templateids",  templateIds);
		return map(this.pkOption(), ids.get(this.pkOption()));
	}
	
	/**
	 * Allows to:
	 * - remove hosts from groups;
	 * - unlink and clear templates from hosts;
	 * - remove macros from hosts.
	 *
	 * Supported data parameters are:
	 * - hostids            - an array of host IDs to be updated
	 * - templateids        - an array of template IDs to be updated
	 * - groupids           - an array of host group IDs the hosts should be removed from
	 * - templateids_link   - an array of template IDs to unlink from the hosts
	 * - templateids_clear  - an array of template IDs to unlink and clear from the hosts
	 * - macros             - an array of macros to delete from the hosts
	 *
	 * @param array data
	 *
	 * @return array
	 */
	public CArray massRemove(CArray data) {
		CArray allHostIds = array_merge(Nest.value(data,"hostids").asCArray(), Nest.value(data,"templateids").asCArray());

		if (isset(data,"groupids")) {
			API.HostGroup(this.idBean, this.getSqlExecutor()).massRemove(map(
				"hostids", Nest.value(data,"hostids").$(),
				"templateids", Nest.value(data,"templateids").$(),
				"groupids", rda_toArray(Nest.value(data,"groupids").$())
			));
		}

		if (!empty(data.get("templateids_link"))) {
			unlink(rda_toArray(data.get("templateids_link")).valuesAsLong(), allHostIds.valuesAsLong());
		}

		if (isset(data, "templateids_clear")) {
			unlink(rda_toArray(data.get("templateids_clear")).valuesAsLong(), allHostIds.valuesAsLong(), true);
		}

		if (isset(data, "macros")) {
			CUserMacroDAO userMacroDAO = API.UserMacro(this.idBean, getSqlExecutor());
			CUserMacroGet umoptions = new CUserMacroGet();
			umoptions.setHostIds(allHostIds.valuesAsLong());
			umoptions.setFilter("macro", Nest.array(data,"macros").asString());
			CArray hostMacros = userMacroDAO.get(umoptions);
			CArray hostMacroIds = rda_objectValues(hostMacros, "hostmacroid");
			userMacroDAO.delete(hostMacroIds);
		}

		return map(this.pkOption(), data.get(this.pkOption()));
	}

	@Override
	protected CArray<Map> link(CArray<Long> templateIds, CArray<Long> targetIds) {
		if (!API.Host(this.idBean, getSqlExecutor()).isWritable(targetIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	
		CArray<Map> hostsLinkageInserts = super.link(templateIds, targetIds);
		
		CAppManager appManager = Manager.Application(this.idBean, getSqlExecutor());
		for(Map hostTplIds: hostsLinkageInserts){
			appManager.link(Nest.value(hostTplIds,"templateid").asString(), array(hostTplIds.get("hostid")));
	
			API.DiscoveryRule(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.ItemPrototype(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.HostPrototype(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.Item(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			Manager.HttpTest(idBean, getSqlExecutor()).link(Nest.value(hostTplIds,"templateid").asLong(), Nest.value(hostTplIds,"hostid").asLong());
		}
	
		// we do linkage in two separate loops because for triggers you need all items already created on host
		for(Map hostTplIds: hostsLinkageInserts){
			API.Trigger(this.idBean, getSqlExecutor()).syncTemplates((CArray)map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.TriggerPrototype(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.GraphPrototype(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
	
			API.Graph(this.idBean, getSqlExecutor()).syncTemplates(map(
				"hostids", hostTplIds.get("hostid"),
				"templateids", hostTplIds.get("templateid")
			));
		}
	
		for(Map hostTplIds: hostsLinkageInserts){
			API.Trigger(this.idBean, getSqlExecutor()).syncTemplateDependencies(map(
				"templateids", hostTplIds.get("templateid"),
				"hostids", hostTplIds.get("hostid")
			));
		}
	
		return hostsLinkageInserts;
	}

	/**
	 * Unlinks the templates from the given hosts. If tragetids is set to null, the templates will be unlinked from
	 * all hosts.
	 *
	 * @param array      templateids
	 * @param null|array targetids		the IDs of the hosts to unlink the templates from
	 * @param bool       clear			delete all of the inherited objects from the hosts
	 */
	@Override
	protected void unlink(Long[] templateids) {
		unlink(templateids, null);
	}
	
	/**
	 * Unlinks the templates from the given hosts. If tragetids is set to null, the templates will be unlinked from
	 * all hosts.
	 *
	 * @param array      templateids
	 * @param null|array targetids		the IDs of the hosts to unlink the templates from
	 * @param bool       clear			delete all of the inherited objects from the hosts
	 */
	@Override
	protected void unlink(Long[] templateids, Long[] targetids) {
		unlink(templateids, targetids, false);
	}
	
	/**
	 * Unlinks the templates from the given hosts. If tragetids is set to null, the templates will be unlinked from
	 * all hosts.
	 *
	 * @param array      templateids
	 * @param null|array targetids		the IDs of the hosts to unlink the templates from
	 * @param bool       clear			delete all of the inherited objects from the hosts
	 */
	protected void unlink(Long[] templateids, Long[] targetids, boolean clear) {
		int[] flags = (clear)
				? new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_RULE}
				: new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_RULE, RDA_FLAG_DISCOVERY_PROTOTYPE};

		SqlBuilder sqlParts = new SqlBuilder();
		// check that all triggers on templates that we unlink, don't have items from another templates
		String sql = "SELECT DISTINCT t.description"+
			" FROM triggers t,functions f,items i"+
			" WHERE t.tenantid=f.tenantid"+
			" AND f.tenantid=i.tenantid"+
			" AND t.triggerid=f.triggerid"+
			" AND f.itemid=i.itemid"+
			" AND "+sqlParts.dual.dbConditionInt("i.hostid", templateids)+
			" AND EXISTS ("+
			"SELECT ff.triggerid"+
			" FROM functions ff,items ii"+
			" WHERE ff.tenantid=ii.tenantid"+
			" AND ff.tenantid=t.tenantid"+
			" AND ff.itemid=ii.itemid"+
			" AND ff.triggerid=t.triggerid"+
			" AND "+sqlParts.dual.dbConditionInt("ii.hostid", templateids, true)+
			")"+
			" AND t.flags="+RDA_FLAG_DISCOVERY_NORMAL;
		Map dbTrigger = DBfetch(DBselect(getSqlExecutor(),sql, 1, sqlParts.getNamedParams()));
		if (!empty(dbTrigger)) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_s("Cannot unlink trigger \"%s\", it has items from template that is left linked to host.", Nest.value(dbTrigger,"description").$())
			);
		}

		sqlParts = new SqlBuilder();
		String sqlFrom = " triggers t,hosts h";
		String sqlWhere = " EXISTS ("+
			"SELECT ff.triggerid"+
			" FROM functions ff,items ii"+
			" WHERE ff.tenantid=t.tenantid"+
			" AND ii.tenantid=ff.tenantid"+
			" AND ff.triggerid=t.templateid"+
			" AND ii.itemid=ff.itemid"+
			" AND "+sqlParts.dual.dbConditionInt("ii.hostid", templateids)+")"+
			" AND "+sqlParts.dual.dbConditionInt("t.flags", flags);


		if (!is_null(targetids)) {
			sqlFrom = " triggers t,functions f,items i,hosts h";
			sqlWhere += " AND "+sqlParts.dual.dbConditionInt("i.hostid", targetids)+
				" AND f.tenantid=i.tenantid"+
				" AND t.tenantid=f.tenantid"+
				" AND h.tenantid=i.tenantid"+
				" AND f.itemid=i.itemid"+
				" AND t.triggerid=f.triggerid"+
				" AND h.hostid=i.hostid";
		}
		sql = "SELECT DISTINCT t.triggerid,t.description,t.flags,t.expression,h.name as host"+
			" FROM "+sqlFrom+
			" WHERE "+sqlWhere;
		CArray<Map> dbTriggers = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		CArray<CArray<Map>> triggers = (CArray)map(
			RDA_FLAG_DISCOVERY_NORMAL, array(),
			RDA_FLAG_DISCOVERY_PROTOTYPE, array()
		);
		CArray triggerids = array();
		for(Map trigger : dbTriggers) {
			Nest.value(triggers,trigger.get("flags"),trigger.get("triggerid")).$(map(
				"description", Nest.value(trigger,"description").$(),
				"expression", explode_exp(idBean, getSqlExecutor(), Nest.value(trigger,"expression").asString()),
				"triggerid", Nest.value(trigger,"triggerid").$(),
				"host", Nest.value(trigger,"host").$()
			));
			if (!in_array(Nest.value(trigger,"triggerid").$(), triggerids)) {
				array_push(triggerids, Nest.value(trigger,"triggerid").$());
			}
		}

		if (!empty(triggers.get(RDA_FLAG_DISCOVERY_NORMAL))) {
			if (clear) {
				CArray<Long[]> result = API.Trigger(this.idBean, this.getSqlExecutor()).delete(true, array_keys(triggers.get(RDA_FLAG_DISCOVERY_NORMAL)).valuesAsLong());
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear triggers"));
			} else {
				update("triggers", array((Map)map(
					"values", map("templateid", 0),
					"where", map("triggerid", array_keys(triggers.get(RDA_FLAG_DISCOVERY_NORMAL)).valuesAsLong())
				)));

				for(Map trigger : triggers.get(RDA_FLAG_DISCOVERY_NORMAL)) {
					info(_s("Unlinked: Trigger \"%1$s\" on \"%2$s\".", Nest.value(trigger,"description").$(), Nest.value(trigger,"host").$()));
				}
			}
		}

		if (!empty(triggers.get(RDA_FLAG_DISCOVERY_PROTOTYPE))) {
			if (clear) {
				CArray<Long[]> result = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).delete(true,array_keys(triggers.get(RDA_FLAG_DISCOVERY_PROTOTYPE)).valuesAsLong());
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear triggers"));
			} else {
				update("triggers", array((Map)map(
					"values", map("templateid", 0),
					"where", map("triggerid", array_keys(triggers.get(RDA_FLAG_DISCOVERY_PROTOTYPE)).valuesAsLong())
				)));

				for(Map trigger : triggers.get(RDA_FLAG_DISCOVERY_PROTOTYPE)) {
					info(_s("Unlinked: Trigger prototype \"%1$s\" on \"%2$s\".", Nest.value(trigger,"description").$(), Nest.value(trigger,"host").$()));
				}
			}
		}

		/* ITEMS, DISCOVERY RULES {{{ */
		sqlParts = new SqlBuilder();
		sqlFrom = " items i1,items i2,hosts h";
		sqlWhere = " i2.tenantid=i1.tenantid"+
			" AND i2.itemid=i1.templateid"+
			" AND "+sqlParts.dual.dbConditionInt("i2.hostid", templateids)+
			" AND "+sqlParts.dual.dbConditionInt("i1.flags", flags)+
			" AND h.tenantid=i1.tenantid"+
			" AND h.hostid=i1.hostid";

		if (!is_null(targetids)) {
			sqlWhere += " AND "+sqlParts.dual.dbConditionInt("i1.hostid", targetids);
		}
		sql = "SELECT DISTINCT i1.itemid,i1.flags,i1.name,i1.hostid,h.name as host"+
			" FROM "+sqlFrom+
			" WHERE "+sqlWhere;
		CArray<Map> dbItems = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		CArray<CArray<Map>>items = map(
			RDA_FLAG_DISCOVERY_NORMAL, array(),
			RDA_FLAG_DISCOVERY_RULE, array(),
			RDA_FLAG_DISCOVERY_PROTOTYPE, array()
		);
		for(Map item : dbItems) {
			Nest.value(items,item.get("flags"),item.get("itemid")).$(map(
				"name", Nest.value(item,"name").$(),
				"host", Nest.value(item,"host").$()
			));
		}

		if (!empty(items.get(RDA_FLAG_DISCOVERY_RULE))) {
			if (clear) {
				CArray<Long[]> result = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).delete(true,array_keys(items.get(RDA_FLAG_DISCOVERY_RULE)).valuesAsLong());
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear discovery rules"));
			} else {
				update("items", array((Map)map(
					"values", map("templateid", 0),
					"where", map("itemid", array_keys(items.get(RDA_FLAG_DISCOVERY_RULE)).valuesAsLong())
				)));

				for(Map discoveryRule : items.get(RDA_FLAG_DISCOVERY_RULE)) {
					info(_s("Unlinked: Discovery rule \"%1$s\" on \"%2$s\".", Nest.value(discoveryRule,"name").$(), Nest.value(discoveryRule,"host").$()));
				}
			}
		}

		if (!empty(items.get(RDA_FLAG_DISCOVERY_NORMAL))) {
			if (clear) {
				CArray<Long[]> result = API.Item(this.idBean, this.getSqlExecutor()).delete(true, array_keys(items.get(RDA_FLAG_DISCOVERY_NORMAL)).valuesAsLong());
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear items"));
			} else {
				update("items", array((Map)map(
					"values", map("templateid", 0),
					"where", map("itemid", array_keys(items.get(RDA_FLAG_DISCOVERY_NORMAL)).valuesAsLong())
				)));

				for(Map item : items.get(RDA_FLAG_DISCOVERY_NORMAL)) {
					info(_s("Unlinked: Item \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(item,"host").$()));
				}
			}
		}

		if (!empty(items.get(RDA_FLAG_DISCOVERY_PROTOTYPE))) {
			if (clear) {
				CArray<Long[]> result = API.ItemPrototype(this.idBean, this.getSqlExecutor()).delete(true, array_keys(items.get(RDA_FLAG_DISCOVERY_PROTOTYPE)).valuesAsLong());
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear item prototypes"));
			} else {
				update("items", array((Map)map(
					"values", map("templateid", 0),
					"where", map("itemid", array_keys(items.get(RDA_FLAG_DISCOVERY_PROTOTYPE)).valuesAsLong())
				)));

				for(Map item : items.get(RDA_FLAG_DISCOVERY_PROTOTYPE)) {
					info(_s("Unlinked: Item prototype \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(item,"host").$()));
				}
			}
		}
		/* }}} ITEMS, DISCOVERY RULES */

		// host prototypes
		// we need only to unlink host prototypes. in case of unlink and clear they will be deleted together with LLD rules.
		if (!clear && isset(items,RDA_FLAG_DISCOVERY_RULE)) {
			Long[] discoveryRuleIds = array_keys(items.get(RDA_FLAG_DISCOVERY_RULE)).valuesAsLong();
			sqlParts = new SqlBuilder();
			CArray<Map> hostPrototypes = DBfetchArrayAssoc(DBselect(
				getSqlExecutor(),
				"SELECT DISTINCT h.hostid,h.host,h3.host AS parent_host"+
				" FROM hosts h"+
					" INNER JOIN host_discovery hd ON h.tenantid=hd.tenantid AND h.hostid=hd.hostid"+
					" INNER JOIN hosts h2 ON h.tenantid=h2.tenantid AND h.templateid=h2.hostid"+
					" INNER JOIN host_discovery hd2 ON h.tenantid=hd.tenantid AND h.hostid=hd.hostid"+
					" INNER JOIN items i ON hd.tenantid=i.tenantid AND hd.parent_itemid=i.itemid"+
					" INNER JOIN hosts h3 ON i.tenantid=h3.tenantid AND i.hostid=h3.hostid"+
				" WHERE "+sqlParts.dual.dbConditionInt("hd.parent_itemid", discoveryRuleIds),
				sqlParts.getNamedParams()
			), "hostid");
			if (!empty(hostPrototypes)) {
				update("hosts", array((Map)map(
					"values", map("templateid", 0),
					"where", map("hostid", array_keys(hostPrototypes).valuesAsLong())
				)));
				update("group_prototype", array((Map)map(
					"values", map("templateid", 0),
					"where", map("hostid", array_keys(hostPrototypes).valuesAsLong())
				)));
				for(Map hostPrototype : hostPrototypes) {
					info(_s("Unlinked: Host prototype \"%1$s\" on \"%2$s\".", Nest.value(hostPrototype,"host").$(), Nest.value(hostPrototype,"parent_host").$()));
				}
			}
		}


		/* GRAPHS {{{ */
		sqlParts = new SqlBuilder();
		sqlFrom = " graphs g,hosts h";
		sqlWhere = " EXISTS ("+
			"SELECT ggi.graphid"+
			" FROM graphs_items ggi,items ii"+
			" WHERE ggi.tenantid=g.tenantid"+ 
			" AND ii.tenantid=ggi.tenantid"+
			" AND ggi.graphid=g.templateid"+
			" AND ii.itemid=ggi.itemid"+
			" AND "+sqlParts.dual.dbConditionInt("ii.hostid", templateids)+")"+
			" AND "+sqlParts.dual.dbConditionInt("g.flags", flags);


		if (!is_null(targetids)) {
			sqlFrom = " graphs g,graphs_items gi,items i,hosts h";
			sqlWhere += " AND "+sqlParts.dual.dbConditionInt("i.hostid", targetids)+
				" AND gi.tenantid=i.tenantid"+
				" AND g.tenantid=gi.tenantid"+
				" AND h.tenantid=i.tenantid"+
				" AND gi.itemid=i.itemid"+
				" AND g.graphid=gi.graphid"+
				" AND h.hostid=i.hostid";
		}
		sql = "SELECT DISTINCT g.graphid,g.name,g.flags,h.name as host"+
			" FROM "+sqlFrom+
			" WHERE "+sqlWhere;
		CArray<Map> dbGraphs = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		CArray<CArray<Map>>graphs = map(
			RDA_FLAG_DISCOVERY_NORMAL, array(),
			RDA_FLAG_DISCOVERY_PROTOTYPE, array()
		);
		for(Map graph : dbGraphs) {
			Nest.value(graphs,graph.get("flags"),graph.get("graphid")).$(map(
				"name", Nest.value(graph,"name").$(),
				"graphid", Nest.value(graph,"graphid").$(),
				"host", Nest.value(graph,"host").$()
			));
		}

		if (!empty(graphs.get(RDA_FLAG_DISCOVERY_PROTOTYPE))) {
			if (clear) {
				CArray result = API.GraphPrototype(this.idBean, this.getSqlExecutor()).delete(array_keys(graphs.get(RDA_FLAG_DISCOVERY_PROTOTYPE)), true);
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear graph prototypes"));
			} else {
				update("graphs", array((Map)map(
					"values", map("templateid", 0),
					"where", map("graphid", array_keys(graphs.get(RDA_FLAG_DISCOVERY_PROTOTYPE)).valuesAsLong())
				)));

				for(Map graph : graphs.get(RDA_FLAG_DISCOVERY_PROTOTYPE)) {
					info(_s("Unlinked: Graph prototype \"%1$s\" on \"%2$s\".", Nest.value(graph,"name").$(), Nest.value(graph,"host").$()));
				}
			}
		}

		if (!empty(graphs.get(RDA_FLAG_DISCOVERY_NORMAL))) {
			if (clear) {
				CArray result = API.Graph(this.idBean, this.getSqlExecutor()).delete(array_keys(graphs.get(RDA_FLAG_DISCOVERY_NORMAL)), true);
				if (empty(result)) throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear graphs."));
			} else {
				update("graphs", array((Map)map(
					"values", map("templateid", 0),
					"where", map("graphid", array_keys(graphs.get(RDA_FLAG_DISCOVERY_NORMAL)).valuesAsLong())
				)));

				for(Map graph : graphs.get(RDA_FLAG_DISCOVERY_NORMAL)) {
					info(_s("Unlinked: Graph \"%1$s\" on \"%2$s\".", Nest.value(graph,"name").$(), Nest.value(graph,"host").$()));
				}
			}
		}
		/* }}} GRAPHS */

		// http tests
		sqlParts = new SqlBuilder();
		sqlWhere = "";
		if (!is_null(targetids)) {
			sqlWhere = " AND "+sqlParts.dual.dbConditionInt("ht1.hostid", targetids);
		}
		sql = "SELECT DISTINCT ht1.httptestid,ht1.name,h.name as host"+
				" FROM httptest ht1"+
				" INNER JOIN httptest ht2 ON ht2.tenantid=ht1.tenantid AND ht2.httptestid=ht1.templateid"+
				" INNER JOIN hosts h ON h.tenantid=ht1.tenantid AND h.hostid=ht1.hostid"+
				" WHERE "+sqlParts.dual.dbConditionInt("ht2.hostid", templateids)+
				sqlWhere;
		CArray<Map> dbHttpTests = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		CArray<Map> httpTests = array();
		for(Map httpTest : dbHttpTests) {
			Nest.value(httpTests,httpTest.get("httptestid")).$(map(
				"name", Nest.value(httpTest,"name").$(),
				"host", Nest.value(httpTest,"host").$()
			));
		}

		if (!empty(httpTests)) {
			if (clear) {
				CArray<Long[]> result = API.HttpTest(this.idBean, this.getSqlExecutor()).delete(true,array_keys(httpTests).valuesAsLong());
				if (empty(result)) {
					throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear Web scenarios."));
				}
			} else {
				update("httptest", array((Map)map(
					"values", map("templateid", 0),
					"where", map("httptestid", array_keys(httpTests).valuesAsLong())
				)));
				for(Map httpTest : httpTests) {
					info(_s("Unlinked: Web scenario \"%1$s\" on \"%2$s\".", Nest.value(httpTest,"name").$(), Nest.value(httpTest,"host").$()));
				}
			}
		}

		/* APPLICATIONS {{{ */
		sqlParts = new SqlBuilder();
		sql = "SELECT at.application_templateid,at.applicationid,h.name,h.host,h.hostid"+
			" FROM applications a1,application_template at,applications a2,hosts h"+
			" WHERE a1.tenantid=at.tenantid"+ 
				" AND at.tenantid=a2.tenantid"+
				" AND a1.tenantid=h.tenantid"+
				" AND a1.applicationid=at.applicationid"+
				" AND at.templateid=a2.applicationid"+
				" AND "+sqlParts.dual.dbConditionInt("a2.hostid", templateids)+
				" AND a1.hostid=h.hostid";
		if (!empty(targetids)) {
			sql += " AND "+sqlParts.dual.dbConditionInt("a1.hostid", targetids);
		}
		CArray<Map> query = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		CArray<Map> applicationTemplates = array();
		for(Map applicationTemplate : query) {
			applicationTemplates.add(map(
				"applicationid", Nest.value(applicationTemplate,"applicationid").$(),
				"application_templateid", Nest.value(applicationTemplate,"application_templateid").$(),
				"name", Nest.value(applicationTemplate,"name").$(),
				"hostid", Nest.value(applicationTemplate,"hostid").$(),
				"host", Nest.value(applicationTemplate,"host").$()
			));
		}

		if (!empty(applicationTemplates)) {
			// unlink applications from templates
			delete("application_template", (Map)map(
				"application_templateid", rda_objectValues(applicationTemplates, "application_templateid").valuesAsLong()
			));
			/* To be confirmed */
			if (clear) {
				// delete inherited applications that are no longer linked to any templates
				sqlParts = new SqlBuilder();
				CArray<Map> applications = DBselect(
					getSqlExecutor(),
					"SELECT a.applicationid"+
					" FROM applications a"+
						" LEFT JOIN application_template at ON a.tenantid=at.tenantid AND a.applicationid=at.applicationid"+
					" WHERE "+sqlParts.dual.dbConditionInt("a.applicationid", rda_objectValues(applicationTemplates, "applicationid").valuesAsLong())+
						" AND at.applicationid IS NULL",
					sqlParts.getNamedParams()
				);
				CArray<Long[]> result = API.Application(this.idBean, this.getSqlExecutor()).delete(true,rda_objectValues(applications, "applicationid").valuesAsLong());
				if (empty(result)) {
					throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Cannot unlink and clear applications."));
				}
			} else {
				for(Map application : applicationTemplates) {
					info(_s("Unlinked: Application \"%1$s\" on \"%2$s\".", Nest.value(application,"name").$(), Nest.value(application,"host").$()));
				}
			}
		}
		/* }}} APPLICATIONS */

		super.unlink(templateids, targetids);
	}
	
	@Override
	protected void addRelatedObjects(P params, CArray<Map> result) {
		super.addRelatedObjects(params, result);

		CArray hostids = array_keys(result);

		// adding groups
		if (Nest.value(params,"selectGroups").$() != null) {
			CRelationMap relationMap = createRelationMap(result, "hostid", "groupid", "hosts_groups");
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setOutput(Nest.value(params,"selectGroups").$());
			hgoptions.setGroupIds(relationMap.getRelatedLongIds());
			hgoptions.setPreserveKeys(true);
			CArray<Map> groups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);
			relationMap.mapMany(result, groups, "groups");
		}

		// adding templates
		if (Nest.value(params,"selectParentTemplates").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectParentTemplates").$())) {
				CRelationMap relationMap = createRelationMap(result, "hostid", "templateid", "hosts_templates");
				CTemplateGet toptions = new CTemplateGet();
				toptions.setOutput(Nest.value(params,"selectParentTemplates").$());
				toptions.setTemplateIds(relationMap.getRelatedLongIds());
				toptions.setPreserveKeys(true);
				CArray<Map> templates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);
				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(templates, "host");
				}
				relationMap.mapMany(result, templates, "parentTemplates", Nest.value(params,"limitSelects").asInteger());
			} else {
				CTemplateGet toptions = new CTemplateGet();
				toptions.setHostIds(hostids.valuesAsLong());
				toptions.setCountOutput(true);
				toptions.setGroupCount(true);
				CArray<Map> templates = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);
				templates = rda_toHash(templates, "hostid");
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "parentTemplates", isset(templates.get(hostid)) ? templates.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding items
		if (Nest.value(params,"selectItems").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectItems").$())) {
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(outputExtend("items", new String[]{"hostid", "itemid"}, Nest.value(params,"selectItems").$()));
				ioptions.setHostIds(hostids.valuesAsLong());
				ioptions.setNopermissions(true);
				ioptions.setPreserveKeys(true);
				CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(items, "name");
				}

				CRelationMap relationMap = createRelationMap(items, "hostid", "itemid");

				unsetExtraFields(items, new String[]{"hostid", "itemid"}, Nest.value(params,"selectItems").$());
				relationMap.mapMany(result, items, "items", Nest.value(params,"limitSelects").asInteger());
			} else {
				CItemGet ioptions = new CItemGet();
				ioptions.setHostIds(hostids.valuesAsLong());
				ioptions.setNopermissions(true);
				ioptions.setCountOutput(true);
				ioptions.setGroupCount(true);
				CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
				items = rda_toHash(items, "hostid");
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "items", isset(items.get(hostid)) ? items.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding discoveries
		if (Nest.value(params,"selectDiscoveries").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectDiscoveries").$())) {
				CDiscoveryRuleGet drtoptions = new CDiscoveryRuleGet();
				drtoptions.setOutput(outputExtend("items", new String[]{"hostid", "itemid"}, Nest.value(params,"selectDiscoveries").$()));
				drtoptions.setHostIds(hostids.valuesAsLong());
				drtoptions.setNopermissions(true);
				drtoptions.setPreserveKeys(true);
				CArray<Map> items = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(drtoptions);

				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(items, "name");
				}

				CRelationMap relationMap = createRelationMap(items, "hostid", "itemid");

				unsetExtraFields(items, new String[]{"hostid", "itemid"}, Nest.value(params,"selectDiscoveries").$());
				relationMap.mapMany(result, items, "discoveries", Nest.value(params,"limitSelects").asInteger());
			} else {
				CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
				droptions.setHostIds(hostids.valuesAsLong());
				droptions.setNopermissions(true);
				droptions.setCountOutput(true);
				droptions.setGroupCount(true);
				CArray<Map> items = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
				items = rda_toHash(items, "hostid");
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "discoveries", isset(items.get(hostid)) ? items.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding triggers
		if (Nest.value(params,"selectTriggers").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectTriggers").$())) {
				// discovered items
				SqlBuilder sqlParts = new SqlBuilder();
				sqlParts.select.put("i.hostid,f.triggerid");
				sqlParts.from.put("items i,functions f");
				applyQueryTenantOptions("items", "i", params, sqlParts);
				sqlParts.where.dbConditionInt("i.hostid", hostids.valuesAsLong());
				sqlParts.where.put("i.tenantid=f.tenantid");
				sqlParts.where.put("i.itemid=f.itemid");
				
				CArray<Map> res = DBselect(getSqlExecutor(), sqlParts);
				CRelationMap relationMap = new CRelationMap();
				for (Map relation: res) {
					relationMap.addRelation(Nest.value(relation,"hostid").$(), Nest.value(relation,"triggerid").$());
				}

				CTriggerGet ttoptions = new CTriggerGet();
				ttoptions.setOutput(Nest.value(params,"selectTriggers").$());
				ttoptions.setTriggerIds(relationMap.getRelatedLongIds());
				ttoptions.setPreserveKeys(true);
				CArray<Map> triggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(ttoptions);
				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(triggers, "description");
				}
				relationMap.mapMany(result, triggers, "triggers", Nest.value(params,"limitSelects").asInteger());
			} else {
				CTriggerGet toptions = new CTriggerGet();
				toptions.setHostIds(hostids.valuesAsLong());
				toptions.setCountOutput(true);
				toptions.setGroupCount(true);
				CArray<Map> triggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);
				triggers = rda_toHash(triggers, "hostid");

				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "triggers", isset(triggers.get(hostid)) ? triggers.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding graphs
		if (Nest.value(params,"selectGraphs").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectGraphs").$())) {
				// discovered items
				SqlBuilder sqlParts = new SqlBuilder();
				sqlParts.select.put("i.hostid,gi.graphid");
				sqlParts.from.put("items i,graphs_items gi");
				applyQueryTenantOptions("items", "i", params, sqlParts);
				sqlParts.where.dbConditionInt("i.hostid", hostids.valuesAsLong());
				sqlParts.where.put("i.tenantid=gi.tenantid");
				sqlParts.where.put("i.itemid=gi.itemid");
				
				CArray<Map> res = DBselect(getSqlExecutor(), sqlParts);
				CRelationMap relationMap = new CRelationMap();
				for (Map relation: (res)) {
					relationMap.addRelation(Nest.value(relation,"hostid").$(), Nest.value(relation,"graphid").$());
				}

				CGraphGet htoptions = new CGraphGet();
				htoptions.setOutput(Nest.value(params,"selectGraphs").$());
				htoptions.setGraphIds(relationMap.getRelatedLongIds());
				htoptions.setPreserveKeys(true);
				CArray<Map> graphs = API.Graph(this.idBean, this.getSqlExecutor()).get(htoptions);
				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(graphs, "name");
				}
				relationMap.mapMany(result, graphs, "graphs", Nest.value(params,"limitSelects").asInteger());
			} else {
				CGraphGet goptions = new CGraphGet();
				goptions.setHostIds(hostids.valuesAsLong());
				goptions.setCountOutput(true);
				goptions.setGroupCount(true);
				CArray<Map> graphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);
				graphs = rda_toHash(graphs, "hostid");
				
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "graphs", isset(graphs.get(hostid)) ? graphs.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding http tests
		if (Nest.value(params,"selectHttpTests").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectHttpTests").$())) {
				CHttpTestGet htoptions = new CHttpTestGet();
				htoptions.setOutput(outputExtend("httptest", new String[]{"hostid", "httptestid"}, Nest.value(params,"selectHttpTests").$()));
				htoptions.setHostIds(hostids.valuesAsLong());
				htoptions.setNopermissions(true);
				htoptions.setPreserveKeys(true);
				CArray<Map> httpTests = API.HttpTest(this.idBean, this.getSqlExecutor()).get(htoptions);

				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(httpTests, "name");
				}

				CRelationMap relationMap = createRelationMap(httpTests, "hostid", "httptestid");

				unsetExtraFields(httpTests, new String[]{"hostid", "httptestid"}, Nest.value(params,"selectHttpTests").$());
				relationMap.mapMany(result, httpTests, "httpTests", Nest.value(params,"limitSelects").asInteger());
			} else {
				CHttpTestGet htoptions = new CHttpTestGet();
				htoptions.setHostIds(hostids.valuesAsLong());
				htoptions.setNopermissions(true);
				htoptions.setCountOutput(true);
				htoptions.setGroupCount(true);
				CArray<Map> httpTests = API.HttpTest(this.idBean, this.getSqlExecutor()).get(htoptions);
				httpTests = rda_toHash(httpTests, "hostid");
				
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "httpTests", isset(httpTests.get(hostid)) ? httpTests.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding applications
		if (Nest.value(params,"selectApplications").$() != null) {
			if (!API_OUTPUT_COUNT.equals(Nest.value(params,"selectApplications").$())) {
				CAppGet aoptions = new CAppGet();
				aoptions.setOutput(outputExtend("applications", new String[]{"hostid", "applicationid"}, Nest.value(params,"selectApplications").$()));
				aoptions.setHostIds(hostids.valuesAsLong());
				aoptions.setNopermissions(true);
				aoptions.setPreserveKeys(true);
				CArray<Map> applications = API.Application(this.idBean, this.getSqlExecutor()).get(aoptions);

				if (!is_null(Nest.value(params,"limitSelects").$())) {
					order_result(applications, "name");
				}

				CRelationMap relationMap = createRelationMap(applications, "hostid", "applicationid");

				unsetExtraFields(applications, new String[]{"hostid", "applicationid"}, params.get("selectApplications"));
				relationMap.mapMany(result, applications, "applications", Nest.value(params,"limitSelects").asInteger());
			} else {
				CAppGet aoptions = new CAppGet();
				aoptions.setOutput(Nest.value(params,"selectApplications").$());
				aoptions.setHostIds(hostids.valuesAsLong());
				aoptions.setNopermissions(true);
				aoptions.setCountOutput(true);
				aoptions.setGroupCount(true);
				CArray<Map> applications = API.Application(this.idBean, this.getSqlExecutor()).get(aoptions);

				applications = rda_toHash(applications, "hostid");
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					//Map host = entry.getValue();
					result.put(hostid, "applications", isset(applications.get(hostid)) ? applications.getNested(hostid, "rowscount") : 0);
				}
			}
		}

		// adding macros
		if (Nest.value(params,"selectMacros").$() != null && !API_OUTPUT_COUNT.equals(Nest.value(params,"selectMacros").$())) {
			CUserMacroGet umoptions = new CUserMacroGet();
			umoptions.setOutput(outputExtend("hostmacro", new String[]{"hostid", "hostmacroid"}, Nest.value(params,"selectMacros").$()));
			umoptions.setHostIds(hostids.valuesAsLong());
			umoptions.setPreserveKeys(true);
			CArray<Map> macros = API.UserMacro(this.idBean, this.getSqlExecutor()).get(umoptions);

			CRelationMap relationMap = createRelationMap(macros, "hostid", "hostmacroid");

			unsetExtraFields(macros, new String[]{"hostid", "hostmacroid"}, Nest.value(params,"selectMacros").$());
			relationMap.mapMany(result, macros, "macros", Nest.value(params,"limitSelects").asInteger());
		}
	}
	
}
