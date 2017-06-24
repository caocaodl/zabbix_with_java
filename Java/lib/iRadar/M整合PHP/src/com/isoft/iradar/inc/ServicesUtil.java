package com.isoft.iradar.inc;

import static com.isoft.biz.daoimpl.radar.CDB.insert;
import static com.isoft.biz.daoimpl.radar.CDB.update;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.SERVICE_ALGORITHM_MAX;
import static com.isoft.iradar.inc.Defines.SERVICE_ALGORITHM_MIN;
import static com.isoft.iradar.inc.Defines.SERVICE_ALGORITHM_NONE;
import static com.isoft.iradar.inc.Defines.SERVICE_STATUS_OK;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_DOWNTIME;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_ONETIME_DOWNTIME;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_UPTIME;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.FuncsUtil.rda_is_int;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ValidateUtil.validateUnixTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tree.CTree;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ServicesUtil {

	private ServicesUtil() {
	}
	
	public static CArray<String> serviceAlgorythm() {
		CArray<String> algorythms = map(
			SERVICE_ALGORITHM_MAX, _("Problem, if at least one child has a problem"),
			SERVICE_ALGORITHM_MIN, _("Problem, if all children have problems"),
			SERVICE_ALGORITHM_NONE, _("Do not calculate")
		);
		return algorythms;
	}

	public static String serviceAlgorythm(int algorythm) {
		CArray<String> algorythms = map(
			SERVICE_ALGORITHM_MAX, _("Problem, if at least one child has a problem"),
			SERVICE_ALGORITHM_MIN, _("Problem, if all children have problems"),
			SERVICE_ALGORITHM_NONE, _("Do not calculate")
		);
		return algorythms.get(algorythm);
	}

	public static CArray<Long> get_service_childs(IIdentityBean idBean, SQLExecutor executor, Long serviceid) {
		return get_service_childs(idBean, executor, serviceid, 0);
	}
	
	public static CArray<Long> get_service_childs(IIdentityBean idBean, SQLExecutor executor, Long serviceid, int soft) {
		CArray<Long> childs = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> result = DBselect(executor,
			"SELECT sl.servicedownid"+
			" FROM services_links sl"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "services_links", "sl")+
			" AND sl.serviceupid=#{serviceid}"+sqlParts.marshalParam(serviceid)+
				(soft>0 ? "" : " AND sl.soft=0"),
			sqlParts.getNamedParams()
		);
		for (Map row : result) {
			childs.add(Nest.value(row,"servicedownid").asLong());
			childs = array_merge(childs, get_service_childs(idBean, executor, Nest.value(row,"servicedownid").asLong()));
		}
		return childs;
	}

	/**
	 * Creates nodes that can be used to display the service configuration tree using the CTree class.
	 *
	 * @see CTree
	 *
	 * @param array services
	 * @param array parentService
	 * @param array service
	 * @param array dependency
	 * @param array tree
	 */
	public static void createServiceConfigurationTree(CArray<Map> services, CArray tree) {
		createServiceConfigurationTree(services, tree, array());
	}

	public static void createServiceConfigurationTree(CArray<Map> services, CArray<Map> tree, Map parentService) {
		createServiceConfigurationTree(services, tree, parentService, array());
	}

	public static void createServiceConfigurationTree(CArray<Map> services, CArray<Map> tree, Map parentService, Map service) {
		createServiceConfigurationTree(services, tree, parentService, service, array());
	}

	public static void createServiceConfigurationTree(CArray<Map> services, CArray<Map> tree, Map parentService, Map service, Map dependency) {
		CLink caption = null;
		Map serviceNode = null;
		if (empty(service)) {
			caption = new CLink(_("root"), "#", "service-conf-menu");
			caption.setAttribute("data-menu", map(
				"serviceid", 0,
				"name", _("root"),
				"hasDependencies", true
			));
	
			serviceNode = map(
				"id", 0,
				"parentid", 0,
				"caption", caption,
				"trigger", array(),
				"algorithm", SPACE,
				"description", SPACE
			);
	
			service = Clone.deepcopy(serviceNode);
			Nest.value(service,"serviceid").$(0);
			Nest.value(service,"dependencies").$(array());
			Nest.value(service,"trigger").$(array());
	
			// add all top level services as children of \"root\"
			for (Map topService : services) {
				if (empty(Nest.value(topService,"parent").$())) {
					Nest.value(service,"dependencies").asCArray().add(map(
						"servicedownid", Nest.value(topService,"serviceid").$(),
						"soft", 0,
						"linkid", 0
					));
				}
			}
	
			tree.clear();
			tree.add(serviceNode);
		} else {
			// caption
			caption = new CLink(Nest.value(service,"name").$(), "#", "service-conf-menu");
	
			// service is deletable only if it has no hard dependency
			boolean deletable = true;
			CArray<Map> dependencies =Nest.value(service,"dependencies").asCArray();
			for (Map dep : dependencies) {
				if (Nest.value(dep,"soft").asInteger() == 0) {
					deletable = false;
					break;
				}
			}
	
			caption.setAttribute("data-menu", map(
				"serviceid", Nest.value(service,"serviceid").$(),
				"name", Nest.value(service,"name").$(),
				"deletable", deletable
			));
	
			serviceNode = map(
				"id", Nest.value(service,"serviceid").$(),
				"caption", caption,
				"description", (!empty(Nest.value(service,"trigger").$())) ? Nest.value(service,"trigger","description").$() : "-",
				"parentid", !empty(parentService) ? Nest.value(parentService,"serviceid").$() : 0,
				"algorithm", serviceAlgorythm(Nest.value(service,"algorithm").asInteger())
			);
		}
	
		if (empty(dependency) || Nest.value(dependency,"soft").asInteger()==0) {
			Nest.value(tree,serviceNode.get("id")).$(serviceNode);
			CArray<Map> dependencies = Nest.value(service,"dependencies").asCArray();
			for(Map sdependency : dependencies) {
				Map childService = services.get(sdependency.get("servicedownid"));
				createServiceConfigurationTree(services, tree, service, childService, sdependency);
			}
		} else {
			Nest.value(serviceNode,"caption").$(new CSpan(Nest.value(serviceNode,"caption").$(), "service-caption-soft"));
			Nest.value(tree,serviceNode.get("id")+"."+dependency.get("linkid")).$(serviceNode);
		}
	}

	/**
	 * Creates nodes that can be used to display the SLA report tree using the CTree class.
	 *
	 * @see CTree
	 *
	 * @param array services       an array of services to display in the tree
	 * @param array slaData        sla report data, see CService::getSla()
	 * @param period
	 * @param array parentService
	 * @param array service
	 * @param array dependency
	 * @param array tree
	 */
	public static void createServiceMonitoringTree(IIdentityBean idBean, CArray<Map> services, CArray<Map> slaData, String period, CArray tree) {
		createServiceMonitoringTree(idBean, services, slaData, period, tree, map());
	}

	public static void createServiceMonitoringTree(IIdentityBean idBean, CArray<Map> services, CArray<Map> slaData, String period, CArray tree, Map parentService) {
		createServiceMonitoringTree(idBean, services, slaData, period, tree, parentService, map());
	}

	public static void createServiceMonitoringTree(IIdentityBean idBean, CArray<Map> services, CArray<Map> slaData, String period, CArray tree, Map parentService, Map service) {
		createServiceMonitoringTree(idBean, services, slaData, period, tree, parentService, service, map());
	}

	public static void createServiceMonitoringTree(IIdentityBean idBean, CArray<Map> services, CArray<Map> slaData, String period, CArray tree, Map parentService, Map service, Map dependency) {
		// if no parent service is given, start from the root
		CArray serviceNode = null;
		if (empty(service)) {
			serviceNode = map(
				"id", 0,
				"parentid", 0,
				"caption", _("root"),
				"status", SPACE,
				"sla", SPACE,
				"sla2", SPACE,
				"trigger", array(),
				"reason", SPACE,
				"graph", SPACE
			);
	
			service = serviceNode;
			Nest.value(service,"serviceid").$(0);
			Nest.value(service,"dependencies").$(array());
			Nest.value(service,"trigger").$(array());
	
			// add all top level services as children of \"root\"
			for(Map topService : services) {
				if (empty(Nest.value(topService,"parent").$())) {
					Nest.value(service,"dependencies").asCArray().add(map(
						"servicedownid", Nest.value(topService,"serviceid").$(),
						"soft", 0,
						"linkid", 0
					));
				}
			}
	
			tree.clear();
			tree.add(serviceNode);
		} else {// create a not from the given service
			Map serviceSla = slaData.get(service.get("serviceid"));
			Map slaValues = reset(Nest.value(serviceSla,"sla").asCArray());
			
			// caption
			// remember the selected time period when following the bar link
			CArray periods = map(
				"today", "daily",
				"week", "weekly",
				"month", "monthly",
				"year", "yearly",
				24, "daily",
				24 * 7, "weekly",
				24 * 30, "monthly",
				24 * DAY_IN_YEAR, "yearly"
			);
			
			CArray caption = array(new CLink(
				array(Nest.value(service,"name").$()),
				"report3.action?serviceid="+Nest.value(service,"serviceid").$()+"&year="+date("Y")+"&period="+Nest.value(periods,period).$()
			));
			Map trigger = (Map)Nest.value(service,"trigger").$();
			if (!empty(trigger)) {
				CLink url = new CLink(Nest.value(trigger,"description").$(),
					"events.action?source="+EVENT_SOURCE_TRIGGERS+"&triggerid="+Nest.value(trigger,"triggerid").asString()
				);
				caption.add(" - ");
				caption.add(url);
			}
			
			// reason
			Object problemList = "-";
			if (!empty(Nest.value(serviceSla,"problems").$())) {
				problemList = new CList(null, "service-problems");
				CArray<Map> problemTriggers = Nest.value(serviceSla,"problems").asCArray();
				for(Map problemTrigger : problemTriggers) {
					((CList)problemList).addItem(new CLink(Nest.value(problemTrigger,"description").$(),
						"events.action?source="+EVENT_SOURCE_TRIGGERS+"&triggerid="+Nest.value(problemTrigger,"triggerid").asString()
					));
				}
			}
			
			// sla
			Object sla = "-";
			Object sla2 = "-";
			if (Nest.value(service,"showsla").asBoolean() && Nest.value(slaValues,"sla").$() != null) {
				double slaGood = Nest.value(slaValues,"sla").asDouble();
				double slaBad = 100d - slaGood;
	
				double p = min(slaBad, 20d);
	
				int width = 160;
				int widthRed = Nest.as(width * p / 20).asInteger();
				int widthGreen = width - widthRed;
	
				CDiv chart1 = null;
				if (widthGreen > 0) {
					chart1 = new CDiv(null, "sla-bar-part sla-green");
					chart1.setAttribute("style", "width: "+widthGreen+"px;");
				}
				
				CDiv chart2 = null;
				if (widthRed > 0) {
					chart2 = new CDiv(null, "sla-bar-part sla-red");
					chart2.setAttribute("style", "width: "+widthRed+"px;");
				}
				Object bar = new CLink(array(
					chart1,
					chart2,
					new CDiv("80%", "sla-bar-legend sla-bar-legend-start"),
					new CDiv("100%", "sla-bar-legend sla-bar-legend-end")
				), "srv_status.action?serviceid="+Nest.value(service,"serviceid").asString()+"&showgraph=1"+url_param(idBean, "path"));
				bar = new CDiv(bar, "sla-bar");
				((CDiv)bar).setAttribute("title", _s("Only the last 20%% of the indicator is displayed."));
	
				CArray slaBar = array(
					bar,
					new CSpan(sprintf("%.4f", slaBad), "sla-value "+((Nest.value(service,"goodsla").asDouble() > slaGood) ? "red" : "green"))
				);
	
				sla = new CDiv(slaBar, "invisible");
				sla2 = array(
					new CSpan(sprintf("%.4f", slaGood), "sla-value "+((Nest.value(service,"goodsla").asDouble() > slaGood) ? "red" : "green")),
					"/",
					new CSpan(sprintf("%.4f", Nest.value(service,"goodsla").$()), "sla-value")
				);
			}
			
			serviceNode = map(
					"id", Nest.value(service,"serviceid").$(),
					"caption", caption,
					"description", (!empty(Nest.value(service,"trigger").$())) ? Nest.value(service,"trigger","description").$() : _("None"),
					"reason", problemList,
					"sla", sla,
					"sla2", sla2,
					"parentid", (!empty(parentService)) ? Nest.value(parentService,"serviceid").$() : 0,
					"status", (Nest.value(serviceSla,"status").$() != null) ? Nest.value(serviceSla,"status").$() : "-"
				);
		}
		
		// hard dependencies and dependencies for the \"root\" node
		if (empty(dependency) || Nest.value(dependency,"soft").asInteger() == 0) {
			Nest.value(tree,serviceNode.get("id")).$(serviceNode);
	
			CArray<Map> dependencies =Nest.value(service,"dependencies").asCArray();
			for(Map depend : dependencies) {
				Map childService = services.get(depend.get("servicedownid"));
				createServiceMonitoringTree(idBean, services, slaData, period, tree, service, childService, depend);
			}
		} else {// soft dependencies
			Nest.value(serviceNode,"caption").$(new CSpan(Nest.value(serviceNode,"caption").$(), "service-caption-soft"));
			Nest.value(tree,serviceNode.get("id")+"."+dependency.get("linkid")).$(serviceNode);
		}
	}
	
	/**
	 * Calculates the current IT service status based on it's child services.
	 *
	 * The new statuses are written to the services array in the \"newStatus\" property.
	 *
	 * @param string rootServiceId     id of the service to start calculation from
	 * @param array servicesLinks      array with service IDs as keys and arrays of child service IDs as values
	 * @param array services           array of services with IDs as keys
	 * @param array triggers           array of triggers with trigger IDs as keys
	 */
	public static void calculateItServiceStatus(Long rootServiceId, CArray<CArray<Long>> servicesLinks, CArray<Map> services, CArray<Map> triggers) {
		Map service = services.get(rootServiceId);

		// don't calculate a thread if it is already calculated
		// it can be with soft links
		if (isset(service,"newStatus")) {
			return;
		}

		int newStatus = SERVICE_STATUS_OK;

		// leaf service with a trigger
		if (Nest.value(service,"triggerid").asLong() != 0) {
			if (Nest.value(service,"algorithm").asInteger() != SERVICE_ALGORITHM_NONE) {
				Map trigger = triggers.get(service.get("triggerid"));
				newStatus = calculateItServiceStatusByTrigger(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"value").asInteger(), Nest.value(trigger,"priority").asInteger());
			}
		} else if (isset(servicesLinks,rootServiceId)) {
			// calculate status depending on children status
			CArray<Integer> statuses = array();

			for(Long srootServiceId : servicesLinks.get(rootServiceId)) {
				calculateItServiceStatus(srootServiceId, servicesLinks, services, triggers);
				statuses.add(Nest.value(services,rootServiceId,"newStatus").asInteger());
			}

			if (!empty(statuses) && Nest.value(service,"algorithm").asInteger() != SERVICE_ALGORITHM_NONE) {
				int maxSeverity = max(statuses);
				// always return the maximum status of child services
				if (Nest.value(service,"algorithm").asInteger() == SERVICE_ALGORITHM_MAX && maxSeverity != SERVICE_STATUS_OK) {
					newStatus = maxSeverity;
				} else if (min(statuses) != SERVICE_STATUS_OK) {
					newStatus = maxSeverity;
				}
			}
		}

		Nest.value(service,"newStatus").$(newStatus);
	}
	
	/**
	 * Updates the status of all IT services
	 */
	public static void updateItServices(IIdentityBean idBean, SQLExecutor executor) {
		CArray<CArray<Long>> servicesLinks = array();
		CArray<Map> services = array();
		CArray<Long> rootServiceIds = array();
		CArray<Map> triggers = array();

		// auxiliary arrays
		CArray<Boolean> triggerIds = array();
		CArray<Boolean> servicesLinksDown = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> result = DBselect(executor, "SELECT sl.serviceupid,sl.servicedownid FROM services_links sl"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "services_links", "sl"),
				sqlParts.getNamedParams());

		for (Map row : result) {
			if(!isset(servicesLinks,row.get("serviceupid"))){
				Nest.value(servicesLinks,row.get("serviceupid")).$(array());
			}
			Nest.value(servicesLinks,row.get("serviceupid")).asCArray().add(Nest.value(row,"servicedownid").asLong());
			Nest.value(servicesLinksDown,row.get("servicedownid")).$(true);
		}

		sqlParts = new SqlBuilder();
		result = DBselect(executor, "SELECT s.serviceid,s.algorithm,s.triggerid,s.status FROM services s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "services", "s")+
				" ORDER BY s.serviceid",
			sqlParts.getNamedParams());

		for (Map row : result) {
			Nest.value(services,row.get("serviceid")).$(map(
				"serviceid", Nest.value(row,"serviceid").asLong(),
				"algorithm", Nest.value(row,"algorithm").asInteger(),
				"triggerid", Nest.value(row,"triggerid").asLong(),
				"status", Nest.value(row,"status").asInteger()
			));

			if (!isset(servicesLinksDown,row.get("serviceid"))) {
				rootServiceIds.add(Nest.value(row,"serviceid").asLong());
			}

			if (Nest.value(row,"triggerid").asLong() != 0) {
				Nest.value(triggerIds,row.get("triggerid")).$(true);
			}
		}

		if (!empty(triggerIds)) {
			sqlParts = new SqlBuilder();
			result = DBselect(executor,
				"SELECT t.triggerid,t.priority,t.status,t.value"+
				" FROM triggers t"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
				    " AND "+sqlParts.dual.dbConditionInt("t.triggerid", array_keys(triggerIds).valuesAsLong()),
				sqlParts.getNamedParams()
			);

			for(Map row : result) {
				Nest.value(triggers,row.get("triggerid")).$(map(
					"priority", Nest.value(row,"priority").$(),
					"status", Nest.value(row,"status").$(),
					"value", Nest.value(row,"value").$()
				));
			}
		}

		// clearing auxiliary variables
		triggerIds = null;
		servicesLinksDown = null;

		// calculating data
		for(Long rootServiceId : rootServiceIds) {
			calculateItServiceStatus(rootServiceId, servicesLinks, services, triggers);
		}

		// updating changed data
		CArray<Map> updates = array();
		CArray<Map> inserts = array();
		long clock = time();

		for(Map service : services) {
			if (Nest.value(service,"newStatus").asInteger() != Nest.value(service,"status").asInteger()) {
				updates.add(map(
					"values", map("status", Nest.value(service,"newStatus").$()),
					"where",  map("serviceid", Nest.value(service,"serviceid").$())
				));
				inserts.add(map(
					"serviceid", Nest.value(service,"serviceid").$(),
					"clock", clock,
					"value", Nest.value(service,"newStatus").$()
				));
			}
		}

		if (!empty(updates)) {
			update(idBean, executor,"services", updates);
			insert(idBean, executor,"service_alarms", inserts);
		}
	}

	
	/**
	 * Checks the status of the trigger and returns the corresponding service status.
	 *
	 * @param int triggerStatus
	 * @param int triggerValue
	 * @param int triggerPriority
	 *
	 * @return int
	 */
	public static int calculateItServiceStatusByTrigger(int triggerStatus, int triggerValue, int triggerPriority) {
		if (triggerStatus == TRIGGER_STATUS_DISABLED || triggerValue == TRIGGER_VALUE_FALSE) {
			return SERVICE_STATUS_OK;
		}
		return triggerPriority;
	}

	/**
	 * Validate the new service time. Validation is implemented as a separate function to be available directly from the
	 * frontend.
	 *
	 * @throws APIException if the given service time is invalid
	 *
	 * @param array serviceTime
	 *
	 * @return void
	 */
	public static void checkServiceTime(Map serviceTime) {
		// type validation
		CArray serviceTypes = array(
			SERVICE_TIME_TYPE_DOWNTIME,
			SERVICE_TIME_TYPE_ONETIME_DOWNTIME,
			SERVICE_TIME_TYPE_UPTIME
		);
		if (!isset(Nest.value(serviceTime,"type").$()) || !in_array(Nest.value(serviceTime,"type").$(), serviceTypes)) {
			throw new APIException(RDA_API_ERROR_PARAMETERS, _("Incorrect service time type."));
		}

		// one-time downtime validation
		if (Nest.value(serviceTime,"type").asInteger() == SERVICE_TIME_TYPE_ONETIME_DOWNTIME) {
			if (!isset(Nest.value(serviceTime,"ts_from").$()) || !validateUnixTime(Nest.value(serviceTime,"ts_from").asInteger())) {
				throw new APIException(RDA_API_ERROR_PARAMETERS, _("Incorrect service start time."));
			}
			if (!isset(Nest.value(serviceTime,"ts_to").$()) || !validateUnixTime(Nest.value(serviceTime,"ts_to").asInteger())) {
				throw new APIException(RDA_API_ERROR_PARAMETERS, _("Incorrect service end time."));
			}
		}
		// recurring downtime validation
		else {
			if (!isset(Nest.value(serviceTime,"ts_from").$()) || !rda_is_int(Nest.value(serviceTime,"ts_from").$()) || Nest.value(serviceTime,"ts_from").asInteger() < 0 || Nest.value(serviceTime,"ts_from").asInteger() > SEC_PER_WEEK) {
				throw new APIException(RDA_API_ERROR_PARAMETERS, _("Incorrect service start time."));
			}
			if (!isset(Nest.value(serviceTime,"ts_to").$()) || !rda_is_int(Nest.value(serviceTime,"ts_to").$()) || Nest.value(serviceTime,"ts_to").asInteger() < 0 || Nest.value(serviceTime,"ts_to").asInteger() > SEC_PER_WEEK) {
				throw new APIException(RDA_API_ERROR_PARAMETERS, _("Incorrect service end time."));
			}
		}

		if (Nest.value(serviceTime,"ts_from").asInteger() >= Nest.value(serviceTime,"ts_to").asInteger()) {
			throw new APIException(RDA_API_ERROR_PARAMETERS, _("Service start time must be less than end time."));
		}
	}
}
