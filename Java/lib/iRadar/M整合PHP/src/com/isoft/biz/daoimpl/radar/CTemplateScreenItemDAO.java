package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
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
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.params.CTemplateScreenItemGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTemplateScreenItemDAO extends CCoreLongKeyDAO<CTemplateScreenItemGet> {

	/**
	 * Supported values for the resourcetype column.
	 *
	 * @var array
	 */
	protected static int[] resourceTypes = new int[]{
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
	};
	
	public CTemplateScreenItemDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "screens_items", "si", new String[] {"screenitemid", "screenid"});
	}

	/**
	 * Get screem item data.
	 *
	 * @param array _options
	 * @param array _options['hostid']			Use hostid to get real resource id
	 * @param array _options['screenitemids']	Search by screen item IDs
	 * @param array _options['screenids']		Search by screen IDs
	 * @param array _options['filter']			Result filter
	 * @param array _options['limit']			The size of the result set
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CTemplateScreenItemGet params) {
		// build and execute query
		SqlBuilder sqlParts = this.createSelectQuery(this.tableName(), params);
		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
				ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("screenitemid");
				if (params.getPreserveKeys() != null) {
					result.put(id, row);
				} else {
					result.add(row);
				}
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		// fill result with real resourceid
		if (!empty(params.getHostIds()) && !empty(result)) {
			if (empty(params.getScreenItemIds())) {
				params.setScreenItemIds(rda_objectValues(result, "screenitemid").valuesAsLong());
			}

			CTemplateScreenGet tsoptions = new CTemplateScreenGet();
			tsoptions.setScreenItemIds(params.getScreenItemIds());
			tsoptions.setHostIds(params.getHostIds());
			tsoptions.setSelectScreenItems(API_OUTPUT_EXTEND);
			CArray<Map> dbTemplateScreens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);

			if (!empty(dbTemplateScreens)) {
				for (Map screenItem: result) {
					for(Map dbTemplateScreen: dbTemplateScreens) {
						for(Map dbScreenItem: (CArray<Map>)Nest.value(dbTemplateScreen,"screenitems").asCArray()) {
							if (Nest.value(screenItem,"screenitemid").$().equals(Nest.value(dbScreenItem,"screenitemid").$())
									&& isset(Nest.value(dbScreenItem,"real_resourceid").$()) && Nest.value(dbScreenItem,"real_resourceid").asBoolean()) {
								Nest.value(screenItem,"real_resourceid").$(Nest.value(dbScreenItem,"real_resourceid").$());
							}
						}
					}
				}
			}
		}

		return (T)result;
	}

	@Override
	protected void applyQueryFilterOptions(String tableName, String tableAlias, CParamGet options, SqlBuilder sqlParts) {
		super.applyQueryFilterOptions(tableName, tableAlias, options, sqlParts);
		// screen ids
		if (Nest.value(options,"screenids").$() != null) {
			addQuerySelect(fieldId("screenid"), sqlParts);
			sqlParts.where.dbConditionInt(fieldId("screenid"), Nest.array(options,"screenids").asLong());
		}
	}
	
}
