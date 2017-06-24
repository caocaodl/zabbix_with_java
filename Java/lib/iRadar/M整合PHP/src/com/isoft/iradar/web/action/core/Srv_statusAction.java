package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.key;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ServicesUtil.createServiceMonitoringTree;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CServiceGet;
import com.isoft.iradar.model.params.CSlaGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tree.CServiceTree;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class Srv_statusAction extends RadarBaseAction {
	
	private CArray<String> periods;
	
	@Override
	protected void doInitPage() {
		page("title", _("IT services"));
		page("file", "srv_status.action");
		page("hist_arg", new String[] {});

		define("RDA_PAGE_DO_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		this.periods = map(
			"today", _("Today"),
			"week", _("This week"),
			"month", _("This month"),
			"year", _("This year"),
			24, _("Last 24 hours"),
			24 * 7, _("Last 7 days"),
			24 * 30, _("Last 30 days"),
			24 * DAY_IN_YEAR, _("Last 365 days")
		);
		
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"serviceid",	 array(T_RDA_INT, O_OPT, P_SYS|P_NZERO, DB_ID,	null),
			"showgraph",array(T_RDA_INT, O_OPT, P_SYS,	IN("1"),		"isset({serviceid})"),
			"period",		 array(T_RDA_STR, O_OPT, P_SYS,	IN("\""+implode("\",\"", array_keys(this.periods))+"\""),	null),
			"fullscreen",  array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		if (isset(_REQUEST,"serviceid") && isset(_REQUEST,"showgraph")) {
			CServiceGet params = new CServiceGet();
			params.setServiceIds(Nest.value(_REQUEST,"serviceid").asLong());
			params.setPreserveKeys(true);
			CArray<Map> service = API.Service(getIdentityBean(), executor).get(params);

			if (!empty(service)) {
				CTable table = new CTable(null, "chart");
				table.addRow(new CImg("chart5.action?serviceid="+key(service)+url_param(getIdentityBean(), "path")));
				table.show();
			} else {
				access_deny();
			}
		} else {
			String period = get_request("period", String.valueOf(7 * 24));
			long period_end = time();
			long period_start = 0L;
			if("today".equals(period)){
				period_start = mktime(0, 0, 0, Nest.as(date("n")).asInteger(), Nest.as(date("j")).asInteger(), Nest.as(date("Y")).asInteger());
			} else if("week".equals(period)){
				period_start = strtotime("last sunday");
			} else if("month".equals(period)){
				period_start = mktime(0, 0, 0, Nest.as(date("n")).asInteger(), 1, Nest.as(date("Y")).asInteger());
			} else if("year".equals(period)){
				period_start = mktime(0, 0, 0, 1, 1, Nest.as(date("Y")).asInteger());
			} else if (String.valueOf(24).equals(period)
					|| String.valueOf(24 * 7).equals(period)
					|| String.valueOf(24 * 30).equals(period)
					|| String.valueOf(24 * DAY_IN_YEAR).equals(period)) {
				period_start = period_end - (Long.valueOf(period) * 3600);
			}
			
			CServiceGet params = new CServiceGet();
			params.setOutput(new String[]{"name", "serviceid", "showsla", "goodsla", "algorithm"});
			params.setSelectParent(new String[]{"serviceid"});
			params.setSelectDependencies(new String[]{"servicedownid", "soft", "linkid"});
			params.setSelectTrigger(new String[]{"description", "triggerid", "expression"});
			params.setPreserveKeys(true);
			params.setSortfield("sortorder");
			params.setSortorder(RDA_SORT_UP);
			CArray<Map> services = API.Service(getIdentityBean(), executor).get(params);
			
			// expand trigger descriptions
			CArray triggers = rda_objectValues(services, "trigger");
			triggers = CMacrosResolverHelper.resolveTriggerNames(this.getIdentityBean(), executor, triggers);
			
			for (Map service : services) {
				if (!empty(Nest.value(service,"trigger").$())) {
					Nest.value(service,"trigger").$(triggers.get(Nest.value(service,"trigger", "triggerid").$()));
				}
			}
			
			// fetch sla
			CSlaGet slaGet = new CSlaGet();
			slaGet.setInterval(map(
				"from", period_start,
				"to", period_end
			));
			CArray<Map> slaData = API.Service(getIdentityBean(), executor).getSla(slaGet);
		
			// expand problem trigger descriptions
			for (Map serviceSla : slaData) {
				CArray<Map> problemTriggers = Nest.value(serviceSla,"problems").asCArray();
				for (Map problemTrigger : problemTriggers) {
					Nest.value(problemTrigger,"description").$(Nest.value(triggers,problemTrigger.get("triggerid"),"description").$());
				}
			}
			
			CArray treeData = array();
			createServiceMonitoringTree(getIdentityBean(), services, slaData, period, treeData);
			CServiceTree tree = null;
			try {
				tree = new CServiceTree("service_status_tree",
					treeData,
					map(
						"caption", _("Service"),
						"status", _("Status"),
						"reason", _("Reason"),
						"sla", _("Problem time"),
						"sla2", nbsp(_("SLA")+" / "+_("Acceptable SLA"))
					)
				);
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			
			if (tree != null) {
				// creates form for choosing a preset interval
				CForm r_form = new CForm();
				r_form.setAttribute("class", "nowrap");
				r_form.setMethod("get");
				r_form.setAttribute("name", "period_choice");
				r_form.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").$());

				CComboBox period_combo = new CComboBox("period", period, "javascript: submit();");
	            for (Entry<Object, String> e : this.periods.entrySet()) {
	                Object key = e.getKey();
	                String val = e.getValue();
	                period_combo.addItem(key, val);
	            }
				r_form.addItem(array(_("Period")+SPACE, period_combo));

				CWidget srv_wdgt = new CWidget("hat_services", "service-list service-mon");
				srv_wdgt.addPageHeader(_("IT SERVICES"), get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen" , Nest.value(_REQUEST,"fullscreen").$())));
				srv_wdgt.addHeader(_("IT services"), r_form);
				srv_wdgt.addItem(BR());
				srv_wdgt.addItem(tree.getHTML(getIdentityBean(), executor));
				srv_wdgt.show();
			} else {
				error(_("Cannot format Tree. Check logic structure in service links."));
			}
		}
	}

}
