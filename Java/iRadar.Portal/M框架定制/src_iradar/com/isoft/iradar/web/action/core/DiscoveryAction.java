package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ksort;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_port2str;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CDHostGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CUserMacroGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class DiscoveryAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Status of discovery"));
		page("file", "discovery.action");
		page("hist_arg", new String[] { "druleid" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"druleid",	array(T_RDA_INT, O_OPT, P_SYS, DB_ID,	null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"),	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "ip", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {		
		Long druleid = null;
		// check discovery for existing if defined druleid
		if (!empty(druleid = get_request_asLong("druleid"))) {
			CDRuleGet options = new CDRuleGet();
			options.setDruleIds(druleid);
			options.setCountOutput(true);
			long dbDRule = API.DRule(getIdentityBean(), executor).get(options);
			if (empty(dbDRule)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Display */
		CArray data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$(),
			"druleid", get_request("druleid", 0),
			"sort", get_request("sort"),
			"sortorder", get_request("sortorder"),
			"services", array(),
			"drules", array()
		);
		
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"drules", map("filter", map("status", DRULE_STATUS_ACTIVE)),
			"druleid", get_request("druleid", null)
		));
		Nest.value(data,"pageFilter").$(pageFilter);
		
		if (pageFilter.$("drulesSelected").asBoolean()) {
		
			// discovery rules
			CDRuleGet droptions = new CDRuleGet();
			droptions.setFilter("status", Nest.as(DRULE_STATUS_ACTIVE).asString());
			droptions.setSelectDHosts(API_OUTPUT_EXTEND);
			droptions.setOutput(API_OUTPUT_EXTEND);
		
			if (pageFilter.$("druleid").asInteger() > 0) {
				droptions.setDruleIds(pageFilter.$("druleid").asLong());// set selected discovery rule id
			}
		
			CArray<Map> drules = API.DRule(getIdentityBean(), executor).get(droptions);
			Nest.value(data,"drules").$(drules);
			if (!empty(drules)) {
				order_result(drules, "name");
			}
		
			// discovery services
			CDServiceGet dsoptions = new CDServiceGet();
			dsoptions.setSelectHosts(new String[]{"hostid", "name", "status"});
			dsoptions.setOutput(API_OUTPUT_EXTEND);
			dsoptions.setSortfield(getPageSortField(getIdentityBean(), executor, "ip"));
			dsoptions.setSortorder(getPageSortOrder(getIdentityBean(), executor));
			dsoptions.setLimitSelects(1);
			if (!empty(Nest.value(data,"druleid").$())) {
				dsoptions.setDruleIds(Nest.value(data,"druleid").asLong());
			} else {
				dsoptions.setDruleIds(rda_objectValues(drules, "druleid").valuesAsLong());
			}
			CArray<Map> dservices = API.DService(getIdentityBean(), executor).get(dsoptions);
		
			// user macros
			CUserMacroGet umoptions = new CUserMacroGet();
			umoptions.setOutput(API_OUTPUT_EXTEND);
			umoptions.setGlobalMacro(true);
			CArray<Map> macros = API.UserMacro(getIdentityBean(), executor).get(umoptions);
			Nest.value(data,"macros").$(rda_toHash(macros, "macro"));
		
			// services
			CArray services = array();
			Nest.value(data,"services").$(services);
			for(Map dservice: dservices) {
				Object key_ = Nest.value(dservice,"key_").$();
				if (!rda_empty(key_)) {
					if (isset(Nest.value(data,"macros", key_).$())) {
						key_ = Nest.value(data,"macros", key_, "value").$();
					}
					key_ = ": "+key_;
				}
				String serviceName = discovery_check_type2str(Nest.value(dservice,"type").asInteger())+discovery_port2str(Nest.value(dservice,"type").asInteger(), Nest.value(dservice,"port").asInteger())+key_;
				data.put("services", serviceName, 1);
			}
			ksort(services);
		
			// discovery services to hash
			Nest.value(data,"dservices").$(rda_toHash(dservices, "dserviceid"));
		
			// discovery hosts
			CDHostGet dhoptions = new CDHostGet();
			dhoptions.setDruleIds(rda_objectValues(drules, "druleid").valuesAsLong());
			dhoptions.setSelectDServices(API_OUTPUT_REFER);
			dhoptions.setOutput(API_OUTPUT_REFER);
			CArray<Map> _dhosts = API.DHost(getIdentityBean(), executor).get(dhoptions);
			Nest.value(data,"dhosts").$(rda_toHash(_dhosts, "dhostid"));
		}
		
		// render view
		CView discoveryView = new CView("monitoring.discovery", data);
		discoveryView.render(getIdentityBean(), executor);
		discoveryView.show();
	}

}
