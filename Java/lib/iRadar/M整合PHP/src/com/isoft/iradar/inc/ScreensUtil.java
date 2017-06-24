package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.SCREEN_DYNAMIC_ITEM;
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
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ScreensUtil {

	private ScreensUtil() {
	}
	
	public static CArray<String> screen_resources() {
		CArray<String> resources = map(
			//SCREEN_RESOURCE_CLOCK, _("Clock"),
			SCREEN_RESOURCE_URL, _("Url"),
			//SCREEN_RESOURCE_DATA_OVERVIEW, _("Data overview"),
			//SCREEN_RESOURCE_TRIGGERS_OVERVIEW, _("Triggers overview"),
			
			//SCREEN_RESOURCE_HOSTS_INFO, _("Hosts info"),
			//SCREEN_RESOURCE_MAP, _("Map"),
			
			SCREEN_RESOURCE_PLAIN_TEXT, _("Plain text"),
			SCREEN_RESOURCE_SIMPLE_GRAPH, _("Simple graph"),
			SCREEN_RESOURCE_GRAPH, _("Graph"),
			//SCREEN_RESOURCE_ACTIONS, _("History of actions"),
			//SCREEN_RESOURCE_EVENTS, _("History of events"),
			
			SCREEN_RESOURCE_SCREEN, _("Screen"),
			//SCREEN_RESOURCE_SERVER_INFO, _("Server info"),
			
			SCREEN_RESOURCE_HOST_TRIGGERS, _("Host issues"),
			SCREEN_RESOURCE_HOSTGROUP_TRIGGERS, _("Host group issues")
			//SCREEN_RESOURCE_SYSTEM_STATUS, _("System status"),
			//SCREEN_RESOURCE_TRIGGERS_INFO, _("Triggers info"),
		);
		//natsort(resources);
		return resources;
	}
	
	public static String screen_resources(int resource) {
		CArray<String> resources = map(
			//SCREEN_RESOURCE_CLOCK, _("Clock"),
			SCREEN_RESOURCE_DATA_OVERVIEW, _("Data overview"),
			SCREEN_RESOURCE_GRAPH, _("Graph"),
			SCREEN_RESOURCE_ACTIONS, _("History of actions"),
			SCREEN_RESOURCE_EVENTS, _("History of events"),
			SCREEN_RESOURCE_HOSTS_INFO, _("Hosts info"),
			SCREEN_RESOURCE_MAP, _("Map"),
			SCREEN_RESOURCE_PLAIN_TEXT, _("Plain text"),
			SCREEN_RESOURCE_SCREEN, _("Screen"),
			SCREEN_RESOURCE_SERVER_INFO, _("Server info"),
			SCREEN_RESOURCE_SIMPLE_GRAPH, _("Simple graph"),
			SCREEN_RESOURCE_HOSTGROUP_TRIGGERS, _("Host group issues"),
			SCREEN_RESOURCE_HOST_TRIGGERS, _("Host issues"),
			SCREEN_RESOURCE_SYSTEM_STATUS, _("System status"),
			SCREEN_RESOURCE_TRIGGERS_INFO, _("Triggers info"),
			SCREEN_RESOURCE_TRIGGERS_OVERVIEW, _("Triggers overview"),
			SCREEN_RESOURCE_URL, _("Url")
		);

		if (isset(resources, resource)) {
			return resources.get(resource);
		} else {
			return _("Unknown");
		}
	}

	public static Map get_screen_by_screenid(IIdentityBean idBean, SQLExecutor executor, long screenid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor,"SELECT s.* FROM screens s"+
								" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "screens", "s")+
								    " AND s.screenid="+sqlParts.marshalParam(screenid),
								 sqlParts.getNamedParams()));
	}

	public static boolean check_screen_recursion(IIdentityBean idBean, SQLExecutor executor, long mother_screenid, long child_screenid) {
		if (bccomp(mother_screenid , child_screenid) == 0) {
			return true;
		}
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_scr_items = DBselect(executor,
			"SELECT si.resourceid"+
			" FROM screens_items si"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "screens_items", "si")+
			" AND si.screenid="+sqlParts.marshalParam(child_screenid)+
			" AND si.resourcetype="+SCREEN_RESOURCE_SCREEN,
			sqlParts.getNamedParams()
		);
		for (Map scr_item : db_scr_items) {
			if (check_screen_recursion(idBean, executor, mother_screenid, Nest.value(scr_item,"resourceid").asLong())) {
				return true;
			}
		}
		return false;
	}

	public static Map get_slideshow(IIdentityBean idBean, SQLExecutor executor, long slideshowid, int step) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map db_slides = DBfetch(DBselect(executor,
			"SELECT MIN(s.step) AS min_step,MAX(s.step) AS max_step"+
			" FROM slides s"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slides", "s")+
			" AND s.slideshowid=#{slideshowid}"+sqlParts.marshalParam(slideshowid),
			sqlParts.getNamedParams()
		));
		if (empty(db_slides) || is_null(Nest.value(db_slides,"min_step").$())) {
			return null;
		}

		step = step % (Nest.value(db_slides,"max_step").asInteger() + 1);
		Integer curr_step = null;
		if (!isset(step) || step < Nest.value(db_slides,"min_step").asInteger() || step > Nest.value(db_slides,"max_step").asInteger()) {
			curr_step = Nest.value(db_slides,"min_step").asInteger();
		} else {
			curr_step = step;
		}

		sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor,
			"SELECT sl.*"+
			" FROM slides sl,slideshows ss"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slides", "sl")+
			    " AND ss.slideshowid="+sqlParts.marshalParam(slideshowid)+
			    " AND sl.tenantid=ss.tenantid"+
				" AND sl.slideshowid=ss.slideshowid"+
				" AND sl.step="+sqlParts.marshalParam(curr_step),
			sqlParts.getNamedParams()
		));
	}

	public static boolean slideshow_accessible(IIdentityBean idBean, SQLExecutor executor, long slideshowid, int perm) {
		boolean result = false;
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT s.slideshowid"+
				" FROM slideshows s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slideshows", "s")+
				    " AND s.slideshowid="+sqlParts.marshalParam(slideshowid);
		if (!empty(DBselect(executor,sql,sqlParts.getNamedParams()))) {
			result = true;

			CArray<Long> screenids = array();
			sqlParts = new SqlBuilder();
			CArray<Map> db_screens = DBselect(executor,
				"SELECT DISTINCT s.screenid"+
				" FROM slides s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slides", "s")+
				" AND s.slideshowid="+sqlParts.marshalParam(slideshowid),
				sqlParts.getNamedParams()
			);
			for (Map slide_data : db_screens) {
				Nest.value(screenids,slide_data.get("screenid")).$(Nest.value(slide_data,"screenid").$());
			}

			CScreenGet options = new CScreenGet();
			options.setScreenIds(screenids.valuesAsLong());
			if (perm == PERM_READ_WRITE) {
				options.setEditable(true);
			}
			CArray<Map> screens = API.Screen(idBean, executor).get(options);
			screens = rda_toHash(screens, "screenid");

			for(Long screenid : screenids) {
				if (!isset(screens,screenid)) {
					return false;
				}
			}
		}

		return result;
	}

	public static Map get_slideshow_by_slideshowid(IIdentityBean idBean, SQLExecutor executor, long slideshowid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor, "SELECT s.* FROM slideshows s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slideshows", "s")+
					" AND s.slideshowid="+sqlParts.marshalParam(slideshowid),
				sqlParts.getNamedParams()));
	}

	public static Long add_slideshow(IIdentityBean idBean, SQLExecutor executor,String name, long delay, CArray<Map> slides) {
		// validate slides
		if (empty(slides)) {
			error(_("Slide show must contain slides."));
			return null;
		}

		// validate screens
		CArray screenids = rda_objectValues(slides, "screenid");
		CScreenGet options = new CScreenGet();
		options.setScreenIds(screenids.valuesAsLong());
		options.setOutput(new String[]{"screenid"});
		CArray<Map> screens = API.Screen(idBean, executor).get(options);
		screens = rda_toHash(screens, "screenid");
		for(Object screenid : screenids) {
			if (!isset(screens, screenid)) {
				error(_("Incorrect screen provided for slide show."));
				return null;
			}
		}

		// validate slide name
		SqlBuilder sqlParts = new SqlBuilder();
		Map db_slideshow = DBfetch(DBselect(executor,
			"SELECT s.slideshowid FROM slideshows s"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slideshows", "s")+
			    " AND s.name="+sqlParts.marshalParam(name),
			sqlParts.getNamedParams()
		));
		if (!empty(db_slideshow)) {
			error(_s("Slide show \"%s\" already exists.", name));
			return null;
		}

		Long slideshowid = get_dbid(idBean, executor, "slideshows", "slideshowid");
		Map params = new HashMap();
		params.put("tenantid", idBean.getTenantId());
		params.put("slideshowid", slideshowid);
		params.put("name", name);
		params.put("delay", delay);
		boolean result = DBexecute(executor,
			"INSERT INTO slideshows (tenantid,slideshowid,name,delay) VALUES (#{tenantid},#{slideshowid},#{name},#{delay})",
			params
		);

		// create slides
		int i = 0;
		for(Map slide : slides) {
			Long slideid = get_dbid(idBean, executor, "slides", "slideid");

			// set default delay
			if (empty(Nest.value(slide,"delay").$())) {
				Nest.value(slide,"delay").$(0);
			}

			params = new HashMap();
			params.put("tenantid", idBean.getTenantId());
			params.put("slideid", slideid);
			params.put("slideshowid", slideshowid);
			params.put("screenid", Nest.value(slide,"screenid").$());
			params.put("step", i++);
			params.put("delay", Nest.value(slide,"delay").$());
			result = DBexecute(executor,
				"INSERT INTO slides (tenantid,slideid,slideshowid,screenid,step,delay) VALUES (#{tenantid},#{slideid},#{slideshowid},#{screenid},#{step},#{delay})",
				params
			);
			if (!result) {
				return null;
			}
		}
		return slideshowid;
	}

	public static boolean update_slideshow(IIdentityBean idBean, SQLExecutor executor, long slideshowid, String name, long delay, CArray<Map> slides) {
		// validate slides
		if (empty(slides)) {
			error(_("Slide show must contain slides."));
			return false;
		}

		// validate screens
		CArray screenids = rda_objectValues(slides, "screenid");
		CScreenGet options = new CScreenGet();
		options.setScreenIds(screenids.valuesAsLong());
		options.setOutput(new String[]{"screenid"});
		CArray<Map> screens = API.Screen(idBean, executor).get(options);
		screens = rda_toHash(screens, "screenid");
		for(Object screenid : screenids) {
			if (!isset(screens,screenid)) {
				error(_("Incorrect screen provided for slide show."));
				return false;
			}
		}

		// validate slide name
		SqlBuilder sqlParts = new SqlBuilder();
		Map dbSlideshow = DBfetch(DBselect(executor,
			"SELECT s.slideshowid"+
			" FROM slideshows s"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slideshows", "s")+
			    " AND s.name="+sqlParts.marshalParam(name)+
				" AND s.slideshowid<>"+sqlParts.marshalParam(slideshowid),
				sqlParts.getNamedParams()
		));
		if (!empty(dbSlideshow)) {
			error(_s("Slide show \"%1$s\" already exists.", name));
			return false;
		}

		sqlParts = new SqlBuilder();
		dbSlideshow = DBfetch(DBselect(executor,
			"SELECT s.* FROM slideshows s"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slideshows", "s")+
			" AND s.slideshowid="+sqlParts.marshalParam(slideshowid),
			sqlParts.getNamedParams()
		));
		boolean changed = false;
		CArray<Object> slideshow = map("name", name, "delay", delay);

		for (Entry<Object, Object> e : slideshow.entrySet()) {
		    Object key = e.getKey();
		    Object val = e.getValue();
			if (!Nest.as(val).asString().equals(Nest.value(dbSlideshow,key).asString())) {
				changed = true;
				break;
			}
		}

		Boolean result = null;
		if (changed) {
			Map params = new HashMap();
			params.put("tenantid", idBean.getTenantId());
			params.put("name", name);
			params.put("delay", delay);
			params.put("slideshowid", slideshowid);
			result = DBexecute(executor,
				"UPDATE slideshows"+
				" SET name=#{name}, delay=#{delay}"+
				" WHERE tenantid=#{tenantid}"+
				" AND slideshowid=#{slideshowid}",
				params
			);
			if (!result) {
				return false;
			}
		}

		// get slides
		sqlParts = new SqlBuilder();
		CArray<Map> db_slides = DBfetchArrayAssoc(DBselect(executor,
				"SELECT s.* FROM slides s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slides", "s")+
				" AND s.slideshowid=#{slideshowid}"+sqlParts.marshalParam(slideshowid),
				sqlParts.getNamedParams()), 
			"slideid");

		CArray slidesToDel = rda_objectValues(db_slides, "slideid");
		slidesToDel = rda_toHash(slidesToDel);
		int step = 0;
		for(Map slide : slides) {
			Nest.value(slide,"delay").$(!empty(Nest.value(slide,"delay").$()) ? Nest.value(slide,"delay").$() : 0);
			if (isset(db_slides,slide.get("slideid"))) {
				// update slide
				if (Nest.value(db_slides,slide.get("slideid"),"delay").asInteger() != Nest.value(slide,"delay").asInteger() || Nest.value(db_slides,slide.get("slideid"),"step").asInteger() != step) {
					Map params = new HashMap();
					params.put("tenantid", idBean.getTenantId());
					params.put("step", step);
					params.put("delay", Nest.value(slide,"delay").$());
					params.put("slideid", Nest.value(slide,"slideid").$());
					result = DBexecute(executor, "UPDATE slides SET step=#{step}, delay=#{delay} WHERE tenantid=#{tenantid} AND slideid=#{slideid}",params);
				} else {// do nothing with slide
					result = true;
				}
				unset(slidesToDel,slide.get("slideid"));
			} else {// insert slide
				Long slideid = get_dbid(idBean, executor,"slides", "slideid");
				Map params = new HashMap();
				params.put("tenantid", idBean.getTenantId());
				params.put("slideid", slideid);
				params.put("slideshowid", slideshowid);
				params.put("screenid", Nest.value(slide,"screenid").$());
				params.put("step", step);
				params.put("delay", Nest.value(slide,"delay").$());
				result = DBexecute(executor,
					"INSERT INTO slides (tenantid,slideid,slideshowid,screenid,step,delay) VALUES (#{tenantid},#{slideid},#{slideshowid},#{screenid},#{step},#{delay})",
					params
				);
			}
			step ++;
			if (!result) {
				return false;
			}
		}

		// delete unnecessary slides
		if (!empty(slidesToDel)) {
			DBexecute(executor, "DELETE FROM slides WHERE tenantid="+idBean.getTenantId()+" AND slideid IN("+implode(",", slidesToDel)+")");
		}
		return true;
	}

	public static boolean delete_slideshow(IIdentityBean idBean, SQLExecutor executor, long slideshowid) {
		Map params = new HashMap();
		params.put("tenantid", idBean.getTenantId());
		params.put("slideshowid", slideshowid);
		boolean result = DBexecute(executor,"DELETE FROM slideshows where tenantid=#{tenantid} AND slideshowid=#{slideshowid}",params);
		//FIXME comments by benne
		//result &= DBexecute(executor,"DELETE FROM slides where slideshowid=#{slideshowid}",params);
		//result &= DBexecute(executor,"DELETE FROM profiles WHERE idx='web.favorite.screenids' AND source='slideshowid' AND value_id=#{slideshowid}",params);
		DBexecute(executor,"DELETE FROM slides where tenantid=#{tenantid} AND slideshowid=#{slideshowid}",params);
		DBexecute(executor,"DELETE FROM profiles WHERE tenantid=#{tenantid} AND idx='web.favorite.screenids' AND source='slideshowid' AND value_id=#{slideshowid}",params);
		return result;
	}

	// check whether there are dynamic items in the screen, if so return TRUE, else FALSE
	public static boolean check_dynamic_items(IIdentityBean idBean, SQLExecutor executor,long elid) {
		return check_dynamic_items(idBean, executor, elid, 0);
	}
	
	public static boolean check_dynamic_items(IIdentityBean idBean, SQLExecutor executor,long elid, int config) {
		String sql = null;
		SqlBuilder sqlParts = new SqlBuilder();
		if (config == 0) {
			sql = "SELECT si.screenitemid"+
					" FROM screens_items si"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "screens_items", "si")+
					    " AND si.screenid="+sqlParts.marshalParam(elid)+
						" AND si.dynamic="+SCREEN_DYNAMIC_ITEM;
		} else {
			sql = "SELECT si.screenitemid"+
					" FROM slides s,screens_items si"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "slides", "s")+
					    " AND s.slideshowid="+sqlParts.marshalParam(elid)+
						" AND si.tenantid=s.tenantid"+
						" AND si.screenid=s.screenid"+
						" AND si.dynamic="+SCREEN_DYNAMIC_ITEM;
		}
		if (!DBselect(executor, sql, 1, sqlParts.getNamedParams()).isEmpty()) {
			return true;
		}
		return false;
	}

	public static String getResourceNameByType(int resourceType) {
		switch (resourceType) {
			case SCREEN_RESOURCE_DATA_OVERVIEW:
			case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
				return _("Group");
		}
		return null;
	}
}
