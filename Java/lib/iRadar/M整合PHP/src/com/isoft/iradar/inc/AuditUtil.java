package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.natsort;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ACTION;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_APPLICATION;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_GRAPH_ELEMENT;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_IMAGE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_IRADAR_CONFIG;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ITEM;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_IT_SERVICE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MACRO;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MEDIA_TYPE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_PROXY;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_REGEXP;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCENARIO;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCRIPT;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER_GROUP;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_VALUE_MAP;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_substr;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.model.CWebUser;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class AuditUtil {
	
	interface AuditOff {
	}
	
	private AuditUtil() {
	}

	public static CArray<String> audit_resource2str() {
		CArray<String> resources = map(
			AUDIT_RESOURCE_USER, _("User"),
			//AUDIT_RESOURCE_IRADAR_CONFIG, _("Configuration of iRadar"),
			AUDIT_RESOURCE_MEDIA_TYPE, _("Media type"),
			AUDIT_RESOURCE_HOST, _("Host"),
			AUDIT_RESOURCE_ACTION, _("Action"),
			AUDIT_RESOURCE_GRAPH, _("Graph"),
			AUDIT_RESOURCE_GRAPH_ELEMENT, _("Graph element"),
			AUDIT_RESOURCE_USER_GROUP, _("User group"),
			AUDIT_RESOURCE_APPLICATION, _("Application"),
			AUDIT_RESOURCE_TRIGGER, _("Trigger"),
			AUDIT_RESOURCE_TRIGGER_PROTOTYPE, _("Trigger prototype"),
			AUDIT_RESOURCE_HOST_GROUP, _("Host group"),
			AUDIT_RESOURCE_ITEM, _("Item"),
			AUDIT_RESOURCE_IMAGE, _("Image"),
			AUDIT_RESOURCE_VALUE_MAP, _("Value map"),
			AUDIT_RESOURCE_IT_SERVICE, _("IT service"),
			AUDIT_RESOURCE_MAP, _("Map"),
			AUDIT_RESOURCE_SCREEN, _("Screen"),
			AUDIT_RESOURCE_SCENARIO, _("Scenario"),
			AUDIT_RESOURCE_DISCOVERY_RULE, _("Discovery rule"),
			AUDIT_RESOURCE_SLIDESHOW, _("Slide show"),
			AUDIT_RESOURCE_PROXY, _("Proxy"),
			AUDIT_RESOURCE_REGEXP, _("Regular expression"),
			AUDIT_RESOURCE_MAINTENANCE, _("Maintenance"),
			AUDIT_RESOURCE_SCRIPT, _("Script"),
			AUDIT_RESOURCE_MACRO, _("Macro"),
			AUDIT_RESOURCE_TEMPLATE, _("Template")
		);
		
		natsort(resources);
		return resources;
	}
	
	public static String audit_resource2str(int resource_type) {
		CArray<String> resources = map(
			AUDIT_RESOURCE_USER, _("User"),
			AUDIT_RESOURCE_IRADAR_CONFIG, _("Configuration of iRadar"),
			AUDIT_RESOURCE_MEDIA_TYPE, _("Media type"),
			AUDIT_RESOURCE_HOST, _("Host"),
			AUDIT_RESOURCE_ACTION, _("Action"),
			AUDIT_RESOURCE_GRAPH, _("Graph"),
			AUDIT_RESOURCE_GRAPH_ELEMENT, _("Graph element"),
			AUDIT_RESOURCE_USER_GROUP, _("User group"),
			AUDIT_RESOURCE_APPLICATION, _("Application"),
			AUDIT_RESOURCE_TRIGGER, _("Trigger"),
			AUDIT_RESOURCE_TRIGGER_PROTOTYPE, _("Trigger prototype"),
			AUDIT_RESOURCE_HOST_GROUP, _("Host group"),
			AUDIT_RESOURCE_ITEM, _("Item"),
			AUDIT_RESOURCE_IMAGE, _("Image"),
			AUDIT_RESOURCE_VALUE_MAP, _("Value map"),
			AUDIT_RESOURCE_IT_SERVICE, _("IT service"),
			AUDIT_RESOURCE_MAP, _("Map"),
			AUDIT_RESOURCE_SCREEN, _("Screen"),
			AUDIT_RESOURCE_SCENARIO, _("Scenario"),
			AUDIT_RESOURCE_DISCOVERY_RULE, _("Discovery rule"),
			AUDIT_RESOURCE_SLIDESHOW, _("Slide show"),
			AUDIT_RESOURCE_PROXY, _("Proxy"),
			AUDIT_RESOURCE_REGEXP, _("Regular expression"),
			AUDIT_RESOURCE_MAINTENANCE, _("Maintenance"),
			AUDIT_RESOURCE_SCRIPT, _("Script"),
			AUDIT_RESOURCE_MACRO, _("Macro"),
			AUDIT_RESOURCE_TEMPLATE, _("Template")
		);
		if (isset(resources, resource_type)) {
			return resources.get(resource_type);
		} else {
			return _("Unknown resource");
		}
	}
	
	public static void do_audit_off() {
		RadarContext.getContext().define(AuditOff.class.getName(), true);
	}
	
	public static void do_audit_on() {
		RadarContext.getContext().undefine(AuditOff.class.getName());
	}
	
	public static boolean add_audit(IIdentityBean idBean, SQLExecutor executor, int action, int resourcetype, String details) {
		if (RadarContext.getContext().defined(AuditOff.class.getName())) {
			return true;
		}
		
		if (empty(CWebUser.get("userid"))) {
			return true;
		}

		if (rda_strlen(details) > 128) {
			details = rda_substr(details, 0, 125)+"...";
		}

		HttpServletRequest request = RadarContext.request();
		String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR"))  ? request.getHeader("HTTP_X_FORWARDED_FOR")  : request.getRemoteAddr();
				
		Map values = map(
			"userid", CWebUser.get("userid"),
			"clock", time(),
			"ip", substr(ip, 0, 39),
			"action",action,
			"resourcetype", resourcetype,
			"details", details
		);

		try {
			CDB.insert(idBean, executor, "auditlog", array(values));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean add_audit_ext(IIdentityBean idBean, SQLExecutor executor, int action, int resourcetype, Object resourceid, String resourcename, String table_name, Map<String,?> values_old, Map<String,?> values_new) {
		if (RadarContext.getContext().defined(AuditOff.class.getName())) {
			return true;
		}
		
		List<String> values_diff = new ArrayList();
		if (AUDIT_ACTION_UPDATE == action && !empty(values_new)) {
			for (Entry<String, ?> e : values_new.entrySet()) {
				String id = e.getKey();
				Object value_new = e.getValue();
				// log only the values that have changed, skip arrays
				if (isset(values_old, id) 
						&& !isArray(values_old.get(id))
						&& !isArray(value_new)
						&& values_old.get(id).equals(value_new)) {
					values_diff.add(id);
				}
			}
			if (values_diff.isEmpty()) {
				return true;
			}
		}
		
		Map<String, Object> data = CWebUser.data();
		String userId = Nest.value(data, "userid").asString();
		
		if (resourcename.length() > 255) {
			resourcename = resourcename.substring(0,252)+"...";
		}

		HttpServletRequest request = RadarContext.request();
		String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR")) 
				? request.getHeader("HTTP_X_FORWARDED_FOR") 
				: request.getRemoteAddr();
				
		Map<String, Object> values = new HashMap();
		values.put("userid", userId);
		values.put("clock", time());
		values.put("ip", substr(ip, 0, 39));
		values.put("action", action);
		values.put("resourcetype", resourcetype);
		values.put("resourceid", resourceid);
		values.put("resourcename", resourcename);
		
		try {
			Long[] auditIds = CDB.insert(idBean, executor, "auditlog", (CArray)array(values)).valuesAsLong();
			Long auditId = auditIds[0];
			if (action == AUDIT_ACTION_UPDATE) {
				values = new HashMap();
				for (String id : values_diff) {
					values.put("auditid",auditId);
					values.put("table_name", table_name);
					values.put("field_name", id);
					values.put("oldvalue", values_old.get(id));
					values.put("newvalue", values_new.get(id));
				}
				CDB.insert(idBean, executor, "auditlog_details", (CArray)array(values));
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean add_audit_details(IIdentityBean idBean, SQLExecutor executor, int action, int resourcetype, Long resourceid, String resourcename) {
		return add_audit_details(idBean, executor, action, resourcetype, resourceid, resourcename, null);
	}
	
	public static boolean add_audit_details(IIdentityBean idBean, SQLExecutor executor, int action, int resourcetype, Long resourceid, String resourcename, String details) {
		if (RadarContext.getContext().defined(AuditOff.class.getName())) {
			return true;
		}
		
		Map<String, Object> data = CWebUser.data();
		Object userId = data.get("userid");
		
		if (resourcename.length() > 255) {
			resourcename = resourcename.substring(0,252)+"...";
		}

		HttpServletRequest request = RadarContext.request();
		String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR")) 
				? request.getHeader("HTTP_X_FORWARDED_FOR") 
				: request.getRemoteAddr();
				
		Map<String, Object> values = new HashMap();
		values.put("userid", userId);
		values.put("clock", time());
		values.put("ip", substr(ip, 0, 39));
		values.put("action", action);
		values.put("resourcetype", resourcetype);
		values.put("resourceid", resourceid);
		values.put("resourcename", resourcename);
		values.put("details", details);

		try {
			CDB.insert(idBean, executor, "auditlog", (CArray)array(values));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
