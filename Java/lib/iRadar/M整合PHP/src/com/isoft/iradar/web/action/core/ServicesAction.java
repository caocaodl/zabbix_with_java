package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_IT_SERVICE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SERVICE_ALGORITHM_MAX;
import static com.isoft.iradar.inc.Defines.SERVICE_SLA;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_ONETIME_DOWNTIME;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_UPTIME;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.dowHrMinToSec;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.ServicesUtil.checkServiceTime;
import static com.isoft.iradar.inc.ServicesUtil.createServiceConfigurationTree;
import static com.isoft.iradar.inc.ServicesUtil.get_service_childs;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.validateDateInterval;
import static com.isoft.iradar.inc.ValidateUtil.validateDateTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CServiceGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tree.CServiceTree;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ServicesAction extends RadarBaseAction {
	
	private Map service;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of IT services"));
		page("file", "services.action");
		page("scripts", new String[] { "class.calendar.js" });
		page("hist_arg", new String[] {});

		if (isset(_REQUEST, "pservices") || isset(_REQUEST, "cservices")) {
			define("RDA_PAGE_NO_MENU", 1);
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"serviceid" ,										array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"name" , 											array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save_service})", _("Name")),
			"algorithm" ,										array(T_RDA_INT, O_OPT, null,	IN("0,1,2"),"isset({save_service})"),
			"showsla" ,										array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"goodsla" , 										array(T_RDA_DBL, O_OPT, null,	BETWEEN(0, 100), null, _("Calculate SLA, acceptable SLA (in %)")),
			"sortorder" , 									array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 999), null, _("Sort order (0->999)")),
			"times" ,											array(T_RDA_STR, O_OPT, null,	null,		null),
			"triggerid" ,										array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"trigger" ,											array(T_RDA_STR, O_OPT, null,	null,		null),
			"new_service_time" ,							array(T_RDA_STR, O_OPT, null,	null,		null),
			"new_service_time_from_day" ,			array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_from_month" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_from_year" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_from_hour" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_from_minute" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_to_day" ,				array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_to_month" ,			array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_to_year" ,			array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_to_hour" ,			array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_service_time_to_minute" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"children" ,										array(T_RDA_STR, O_OPT, P_SYS,	NOT_EMPTY,		null),
			"parentid" ,										array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"parentname" ,									array(T_RDA_STR, O_OPT, null,	null,		null),
			// actions
			"save_service" ,									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_service_time" ,							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,											array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			// others
			"form" ,											array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,								array(T_RDA_INT, O_OPT, null,	null,		null),
			"pservices" ,										array(T_RDA_INT, O_OPT, null,	null,		null),
			"cservices" ,										array(T_RDA_INT, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		if (!empty(Nest.value(_REQUEST,"serviceid").$())) {
			CServiceGet options = new CServiceGet();
			options.setOutput(API_OUTPUT_EXTEND);
			options.setServiceIds(Nest.value(_REQUEST,"serviceid").asLong());

			if (isset(_REQUEST,"delete") || isset(_REQUEST,"pservices") || isset(_REQUEST,"cservices")) {
				options.setOutput(new String[]{"serviceid", "name"});
			} else {
				options.setSelectParent(new String[]{"serviceid", "name"});
				options.setSelectDependencies(API_OUTPUT_EXTEND);
				options.setSelectTimes(API_OUTPUT_EXTEND);
			}
			
			CArray<Map> services = API.Service(getIdentityBean(), executor).get(options);
			this.service = reset(services);
			if (empty(this.service)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Actions */
		// delete
		if (isset(_REQUEST,"delete") && isset(_REQUEST,"serviceid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Service(getIdentityBean(), executor).delete(Nest.value(service,"serviceid").asLong()));
				}
			});
			show_messages(result, _("Service deleted"), _("Cannot delete service"));
			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_IT_SERVICE, "Name ["+Nest.value(service,"name").asString()+"] id ["+Nest.value(service,"serviceid").asString()+"]");
				unset(_REQUEST,"form");
			}
		}

		if (isset(_REQUEST,"form")) {
			Nest.value(_REQUEST,"showsla").$(get_request("showsla", 0));
			// save
			if (isset(_REQUEST,"save_service")) {
				DBstart(executor);
				
				CArray<Long[]> result = null;
				
				CArray<Map> children = get_request("children", array());
				CArray<Map> dependencies = array();
				for(Map child : children) {
					dependencies.add(map(
						"dependsOnServiceid", Nest.value(child,"serviceid").$(),
						"soft", (isset(child,"soft")) ? Nest.value(child,"soft").asInteger() : 0
					));
				}

				final Map serviceRequest = map(
					"name", get_request("name"),
					"triggerid", get_request("triggerid"),
					"algorithm", get_request("algorithm"),
					"showsla", get_request("showsla", 0),
					"goodsla", get_request("goodsla"),
					"sortorder", get_request("sortorder"),
					"times", get_request("times", array()),
					"parentid", get_request("parentid"),
					"dependencies", dependencies
				);

				int audit_action;
				if (isset(service,"serviceid")) {
					Nest.value(serviceRequest,"serviceid").$(Nest.value(service,"serviceid").$());
					result = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Service(getIdentityBean(), executor).update(array(serviceRequest));
						}
					}, null);
					show_messages(!empty(result), _("Service updated"), _("Cannot update service"));
					audit_action = AUDIT_ACTION_UPDATE;
				} else {
					result = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Service(getIdentityBean(), executor).create(array(serviceRequest));
						}
					}, null);
					show_messages(!empty(result), _("Service created"), _("Cannot add service"));
					audit_action = AUDIT_ACTION_ADD;
				}

				if (!empty(result)) {
					Long serviceid = (isset(service,"serviceid")) ? Nest.value(service,"serviceid").asLong() : reset(Nest.array(result,"serviceids").asLong());
					add_audit(getIdentityBean(), executor, audit_action, AUDIT_RESOURCE_IT_SERVICE, "Name ["+Nest.value(_REQUEST,"name").asString()+"] id ["+serviceid+"]");
					unset(_REQUEST,"form");
				}
				
				DBend(executor, !empty(result));
			} else if (isset(_REQUEST,"add_service_time") && isset(_REQUEST,"new_service_time")) {// validate and get service times
				CArray new_service_time = array();
				Nest.value(_REQUEST,"times").$(get_request("times", array()));
				Nest.value(new_service_time,"type").$(Nest.value(_REQUEST,"new_service_time","type").$());
				boolean result = true;
				if (Nest.value(_REQUEST,"new_service_time","type").asInteger() == SERVICE_TIME_TYPE_ONETIME_DOWNTIME) {
					if (!validateDateTime(Nest.value(_REQUEST,"new_service_time_from_year").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_month").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_day").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_hour").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_minute").asInteger())) {
						result = false;
						error(_s("Invalid date \"%s\".", _("From")));
					}
					if (!validateDateInterval(Nest.value(_REQUEST,"new_service_time_from_year").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_month").asInteger(),
							Nest.value(_REQUEST,"new_service_time_from_day").asInteger())) {
						result = false;
						error(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("From")));
					}
					if (!validateDateTime(Nest.value(_REQUEST,"new_service_time_to_year").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_month").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_day").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_hour").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_minute").asInteger())) {
						result = false;
						error(_s("Invalid date \"%s\".", _("Till")));
					}
					if (!validateDateInterval(Nest.value(_REQUEST,"new_service_time_to_year").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_month").asInteger(),
							Nest.value(_REQUEST,"new_service_time_to_day").asInteger())) {
						result = false;
						error(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Till")));
					}
					if (result) {
						Nest.value(new_service_time,"ts_from").$(mktime(Nest.value(_REQUEST,"new_service_time_from_hour").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_from_minute").asRealInteger(),
								0,
								Nest.value(_REQUEST,"new_service_time_from_month").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_from_day").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_from_year").asRealInteger()));

						Nest.value(new_service_time,"ts_to").$( mktime(Nest.value(_REQUEST,"new_service_time_to_hour").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_to_minute").asRealInteger(),
								0,
								Nest.value(_REQUEST,"new_service_time_to_month").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_to_day").asRealInteger(),
								Nest.value(_REQUEST,"new_service_time_to_year").asRealInteger()));

						Nest.value(new_service_time,"note").$(Nest.value(_REQUEST,"new_service_time","note").$());
					}
				} else {
					Nest.value(new_service_time,"ts_from").$(dowHrMinToSec(Nest.value(_REQUEST,"new_service_time","from_week").asRealLong(), Nest.value(_REQUEST,"new_service_time","from_hour").asRealLong(), Nest.value(_REQUEST,"new_service_time","from_minute").asRealLong()));
					Nest.value(new_service_time,"ts_to").$(dowHrMinToSec(Nest.value(_REQUEST,"new_service_time","to_week").asRealLong(), Nest.value(_REQUEST,"new_service_time","to_hour").asRealLong(), Nest.value(_REQUEST,"new_service_time","to_minute").asRealLong()));
					Nest.value(new_service_time,"note").$(Nest.value(_REQUEST,"new_service_time","note").$());
				}

				if (result) {
					try {
						checkServiceTime(new_service_time);

						// if this time is not already there, adding it for inserting
						if (!str_in_array(Nest.value(_REQUEST,"times").$(), new_service_time)) {
							array_push(Nest.value(_REQUEST,"times").asCArray(), new_service_time);

							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_week");
							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_week");
							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_hour");
							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_hour");
							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_minute");
							unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_minute");
						}
					} catch (APIException e) {
						error(e.getMessage());
					}
				}

				show_messages();
			} else {
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_week");
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_week");
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_hour");
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_hour");
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"from_minute");
				unset(Nest.value(_REQUEST,"new_service_time").asCArray(),"to_minute");
			}
		}

		/* Display parent services list */
		if (isset(_REQUEST,"pservices")) {
			
			CServiceGet option = new CServiceGet();
			option.setOutput(new String[]{"serviceid", "name", "algorithm"});
			option.setSelectTrigger(new String[]{"triggerid", "description", "expression"});
			option.setPreserveKeys(true);
			option.setSortfield(new String[]{"sortorder", "name"});
			CArray<Map> parentServices = API.Service(getIdentityBean(), executor).get(option);

			Map data = null;
			if (isset(service)) {
				// unset unavailable parents
				CArray<Long> childServicesIds = get_service_childs(getIdentityBean(), executor, Nest.value(service,"serviceid").asLong());
				childServicesIds.add(Nest.value(service,"serviceid").asLong());
				for (Long childServiceId : childServicesIds) {
					unset(parentServices,childServiceId);
				}
				data = map("service" , service);
			} else {
				data = array();
			}

			// expand trigger descriptions
			CArray triggers = rda_objectValues(parentServices, "trigger");
			triggers = CMacrosResolverHelper.resolveTriggerNames(this.getIdentityBean(), executor, triggers);
			for (Entry<Object, Map> e : parentServices.entrySet()) {
	            Object key = e.getKey();
	            Map parentService = e.getValue();
				Nest.value(parentServices,key,"trigger").$(!empty(Nest.value(parentService,"trigger").$())
						? Nest.value(triggers,Nest.value(parentService,"trigger","triggerid").$(),"description").$()
						: "-");
			}

			Nest.value(data,"db_pservices").$(parentServices);

			// render view
			CView servicesView = new CView("configuration.services.parent.list", data);
			servicesView.render(getIdentityBean(), executor);
			servicesView.show();
		}
		/* Display child services list */
		else if (isset(_REQUEST,"cservices")) {
			CServiceGet option = new CServiceGet();
			option.setOutput(new String[]{"serviceid", "name", "algorithm"});
			option.setSelectTrigger(new String[]{"triggerid", "description", "expression"});
			option.setPreserveKeys(true);
			option.setSortfield("sortorder", "name");
			CArray<Map> childServices = API.Service(getIdentityBean(), executor).get(option);

			Map data = null;
			if (isset(service)) {
				CArray<Long> childServicesIds = get_service_childs(getIdentityBean(), executor, Nest.value(service,"serviceid").asLong());
				childServicesIds.add(Nest.value(service,"serviceid").asLong());
				
				childServicesIds.add(Nest.value(service,"serviceid").asLong());
				for (Long childServiceId : childServicesIds) {
					unset(childServices,childServiceId);
				}
				data = array("service" , service);
			} else {
				data = array();
			}

			// expand trigger descriptions
			CArray triggers = rda_objectValues(childServices, "trigger");
			triggers = CMacrosResolverHelper.resolveTriggerNames(this.getIdentityBean(), executor,triggers);
			for (Entry<Object, Map> e : childServices.entrySet()) {
	            Object key = e.getKey();
	            Map childService = e.getValue();
				Nest.value(childServices,key,"trigger").$(!empty(Nest.value(childService,"trigger").$())
						? Nest.value(triggers,Nest.value(childService,"trigger","triggerid").$(),"description").$()
						: "-");
			}

			Nest.value(data,"db_cservices").$(childServices);

			// render view
			CView servicesView = new CView("configuration.services.child.list", data);
			servicesView.render(getIdentityBean(), executor);
			servicesView.show();
		}
		/* Display */
		else if (isset(_REQUEST,"form")) {
			Map data = array();
			Nest.value(data,"form").$(get_request("form"));
			Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));
			Nest.value(data,"service").$(!empty(service) ? service : null);

			Nest.value(data,"times").$(get_request("times", array()));
			Nest.value(data,"new_service_time").$(get_request("new_service_time", map("type" , SERVICE_TIME_TYPE_UPTIME)));

			// populate the form from the object from the database
			if (isset(Nest.value(data,"service","serviceid").$()) && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"name").$(Nest.value(data,"service","name").$());
				Nest.value(data,"algorithm").$(Nest.value(data,"service","algorithm").$());
				Nest.value(data,"showsla").$(Nest.value(data,"service","showsla").$());
				Nest.value(data,"goodsla").$(Nest.value(data,"service","goodsla").$());
				Nest.value(data,"sortorder").$(Nest.value(data,"service","sortorder").$());
				Nest.value(data,"triggerid").$(isset(Nest.value(data,"service","triggerid").$()) ? Nest.value(data,"service","triggerid").$() : 0);
				Nest.value(data,"times").$(Nest.value(service,"times").$());

				// parent
				CArray parent = Nest.value(service,"parent").asCArray();
				if (!empty(parent)) {
					Nest.value(data,"parentid").$(Nest.value(parent,"serviceid").$());
					Nest.value(data,"parentname").$(Nest.value(parent,"name").$());
				} else {
					Nest.value(data,"parentid").$(0);
					Nest.value(data,"parentname").$("root");
				}

				// get children
				Nest.value(data,"children").$(array());
				if (!empty(Nest.value(service,"dependencies").$())) {
					CServiceGet option = new CServiceGet();
					option.setServiceIds(rda_objectValues(Nest.value(service,"dependencies").$(), "servicedownid").valuesAsLong());
					option.setSelectTrigger(new String[]{"triggerid", "description", "expression"});
					option.setOutput(new String[]{"name", "triggerid"});
					option.setPreserveKeys(true);
					CArray<Map> childServices = API.Service(getIdentityBean(), executor).get(option);

					// expand trigger descriptions
					CArray triggers = rda_objectValues(childServices, "trigger");
					triggers.add(CMacrosResolverHelper.resolveTriggerNames(this.getIdentityBean(), executor,triggers));
					CArray<Map> dependencys = Nest.value(service,"dependencies").asCArray();
					for (Map dependency : dependencys) {
						CArray childService = Nest.value(childServices,dependency.get("servicedownid")).asCArray();
						Nest.value(data,"children").asCArray().add(map(
								"name" , Nest.value(childService,"name").$(),
								"triggerid" , Nest.value(childService,"triggerid").$(),
								"trigger" , !empty(Nest.value(childService,"triggerid").$())
										? Nest.value(triggers,Nest.value(childService,"trigger","triggerid").$(),"description").$()
										: "-",
								"serviceid" , Nest.value(dependency,"servicedownid").$(),
								"soft" , Nest.value(dependency,"soft").$()
							));
					}
				}
			}
			// populate the form from a submitted request
			else {
				Nest.value(data,"name").$(get_request("name", ""));
				Nest.value(data,"algorithm").$(get_request("algorithm", SERVICE_ALGORITHM_MAX));
				Nest.value(data,"showsla").$(get_request("showsla", 0));
				Nest.value(data,"goodsla").$(get_request("goodsla", SERVICE_SLA));
				Nest.value(data,"sortorder").$(get_request("sortorder", 0));
				Nest.value(data,"triggerid").$(get_request("triggerid", 0));
				Nest.value(data,"parentid").$(get_request("parentid", 0));
				Nest.value(data,"parentname").$(get_request("parentname", ""));
				Nest.value(data,"children").$(get_request("children", array()));
			}

			// get trigger
			if (Nest.value(data,"triggerid").asInteger() > 0) {
				CTriggerGet option = new CTriggerGet();
				option.setTriggerIds(Nest.array(data,"triggerid").asLong());
				option.setOutput(new String[]{"description"});
				option.setSelectHosts(new String[]{"name"});
				option.setExpandDescription(true);
				CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(option);
				Map trigger = reset(triggers);
				Map host = reset(Nest.value(trigger,"hosts").asCArray());
				Nest.value(data,"trigger").$(Nest.value(host,"name").asString()+NAME_DELIMITER+Nest.value(trigger,"description").$());
			}else {
				Nest.value(data,"trigger").$("");
			}

			// render view
			CView servicesView = new CView("configuration.services.edit", data);
			servicesView.render(getIdentityBean(), executor);
			servicesView.show();
		} else {
			// services
			CServiceGet option = new CServiceGet();
			option.setOutput(new String[]{"name", "serviceid", "algorithm"});
			option.setSelectParent(new String[]{"serviceid"});
			option.setSelectDependencies(new String[]{"servicedownid", "soft", "linkid"});
			option.setSelectTrigger(new String[]{"description", "triggerid", "expression"});
			option.setPreserveKeys(true);
			option.setSortfield("sortorder");
			option.setSortorder(RDA_SORT_UP);
			CArray<Map> services = API.Service(getIdentityBean(), executor).get(option);

			// triggers
			CArray<Map> triggers = rda_objectValues(services, "trigger");
			triggers = CMacrosResolverHelper.resolveTriggerNames(this.getIdentityBean(), executor,triggers);

			for(Map service:triggers) {
				if (!empty(Nest.value(service,"trigger").$())) {
					Nest.value(service,"trigger").$(Nest.value(triggers,Nest.value(service,"trigger","triggerid").$()).$());
				}
			}

			CArray treeData = array();
			createServiceConfigurationTree(services, treeData);
			CServiceTree tree = null;
			try {
				tree = new CServiceTree("service_conf_tree", treeData, map(
					"caption" , _("Service"),
					"algorithm" , _("Status calculation"),
					"description" , _("Trigger")
				));
			} catch (InstantiationException e) {
			}
			if (empty(tree)) {
				error(_("Cannot format tree."));
			}

			Map data = map("tree" , tree);

			// render view
			CView servicesView = new CView("configuration.services.list", data);
			servicesView.render(getIdentityBean(), executor);
			servicesView.show();
		}
	}

}
