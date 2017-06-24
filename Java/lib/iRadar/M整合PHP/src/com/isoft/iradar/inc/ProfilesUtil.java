package com.isoft.iradar.inc;

import static com.isoft.biz.daoimpl.radar.CDB.getSchema;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.ctype_digit;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_COUNT;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.Var;
import com.isoft.iradar.core.g;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.validators.CColorValidator;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ProfilesUtil {
	
	private ProfilesUtil() {
	}
	
	public static Map<String,Object> select_config(IIdentityBean idBean, SQLExecutor executor) {
		return select_config(idBean, executor, true);
	}
	
	public static Map<String,Object> select_config(IIdentityBean idBean, SQLExecutor executor, Boolean cache) {
		Map<String, Object> page = RadarContext.getContext().getPage();
		Var<Map<String, Object>> config = g.config;
		
		if (cache && isset(config.$())) {
			return config.$();
		}

		Map params = new HashMap();
		//params.put("tenantId", idBean.getTenantId());
		params.put("tenantId", "-");
		Map db_config = DBfetch(DBselect(executor,
				"SELECT c.*"+
				" FROM config c"+
				" WHERE c.tenantid='-'",
				params
		));
		if(db_config == null){
			db_config = new HashMap();
		}
		if (!empty(db_config)) {
			config.$(db_config);
			return db_config;
		}
		else if (isset(page,"title") && !_("Installation").equals(Nest.value(page,"title").asString())) {
			error(_("Unable to select configuration."));
		}
		return db_config;
	}
	
	public static Boolean update_config(IIdentityBean idBean, SQLExecutor executor, CArray<Object> configs) {
		CArray update = array();

		if (isset(configs,"work_period")) {
			CTimePeriodValidator _timePeriodValidator = CValidator.init(new CTimePeriodValidator(),map());
			if (!_timePeriodValidator.validate(idBean, Nest.value(configs,"work_period").asString())) {
				error(_("Incorrect working time."));
				return false;
			}
		}
		if (isset(configs,"alert_usrgrpid")) {
			if (Nest.value(configs,"alert_usrgrpid").asLong() != 0 && empty(DBfetch(DBselect(executor,"SELECT u.usrgrpid FROM usrgrp u WHERE u.usrgrpid="+Nest.value(configs,"alert_usrgrpid").asLong())))) {
				error(_("Incorrect user group."));
				return false;
			}
		}

		if (isset(configs,"discovery_groupid")) {
			CHostGroupGet options = new CHostGroupGet();
			options.setGroupIds(Nest.value(configs,"discovery_groupid").asLong());
			options.setOutput(new String[]{"groupid"});
			options.setPreserveKeys(true);
			CArray<Map> groupid = API.HostGroup(idBean, executor).get(options);
			if (empty(groupid)) {
				error(_("Incorrect host group."));
				return false;
			}
		}

		// checking color values to be correct hexadecimal numbers
		CArray<String> colors = array(
			"severity_color_0",
			"severity_color_1",
			"severity_color_2",
			"severity_color_3",
			"severity_color_4",
			"severity_color_5",
			"problem_unack_color",
			"problem_ack_color",
			"ok_unack_color",
			"ok_ack_color"
		);
		CColorValidator colorvalidator = CValidator.init(new CColorValidator(),map());
		for(String color : colors) {
			if (isset(configs,color) && !is_null(configs.get(color))) {
				if (!colorvalidator.validate(idBean, Nest.value(configs,color).asString())) {
					error(colorvalidator.getError());
					return false;
				}
			}
		}

		if (isset(configs,"ok_period") && !is_null(Nest.value(configs,"ok_period").$()) && !ctype_digit(Nest.value(configs,"ok_period").$())) {
			error(_("\"Display OK triggers\" needs to be \"0\" or a positive integer."));
			return false;
		}

		if (isset(configs,"blink_period") && !is_null(Nest.value(configs,"blink_period").$()) && !ctype_digit(Nest.value(configs,"blink_period").$())) {
			error(_("\"Triggers blink on status change\" needs to be \"0\" or a positive integer."));
			return false;
		}

		Map<String, Object> currentConfig = select_config(idBean, executor);

		// check duplicate severity names and if name is empty.
		CArray names = array();
		for (int i = 0; i < TRIGGER_SEVERITY_COUNT; i++) {
			String varName = "severity_name_"+i;
			if (!isset(configs,varName) || is_null(Nest.value(configs,varName).asString())) {
				Nest.value(configs,varName).$(Nest.value(currentConfig,varName).$());
			}

			if (StringUtils.isEmpty(Nest.value(configs,varName).asString())) {
				error(_("Severity name cannot be empty."));
				return false;
			}

			if (isset(names,configs.get(varName))) {
				error(_s("Duplicate severity name \"%s\".", Nest.value(configs,varName).asString()));
				return false;
			} else {
				Nest.value(names,configs.get(varName)).$(1);
			}
		}

		CArray<String> fields = array();
		for (Entry<Object, Object> e : configs.entrySet()) {
		    String key = Nest.as(e.getKey()).asString();
		    Object value = e.getValue();
			if (!is_null(value)) {
				if ("alert_usrgrpid".equals(key)) {
					update.put(key, ("0".equals(value)) ? null : value);
				} else{
					update.put(key, value);
				}
				fields.add(key+"=#{"+key+"}");
			}
		}

		if (count(update) == 0) {
			error(_("Nothing to do."));
			return null;
		}

		//update.put("tenantid", idBean.getTenantId());
		update.put("tenantid", "-");
		return DBexecute(executor,
				"UPDATE config"+ " SET "+implode(",", fields)+
				" WHERE tenantid='-'",
				update
		);
	}
	
	public static CArray get_user_history(IIdentityBean idBean, SQLExecutor executor) {
		CArray result = array();
		CSpan delimiter = new CSpan("&raquo;", "delimiter");

		SqlBuilder sqlParts = new SqlBuilder();
		Map history = DBfetch(DBselect(executor,
			"SELECT uh.title1,uh.url1,uh.title2,uh.url2,uh.title3,uh.url3,uh.title4,uh.url4,uh.title5,uh.url5"+
			" FROM user_history uh"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "user_history", "uh")+
			    " AND uh.userid="+sqlParts.marshalParam(CWebUser.get("userid")),
			sqlParts.getNamedParams()
		));

		if (!empty(history) && !rda_empty(Nest.value(history,"url4").$())) {
			Nest.value(CWebUser.data(),"last_page").$(map("title", Nest.value(history,"title4").$(), "url", Nest.value(history,"url4").asString().replace(".php", ".action")));
		} else {
			Nest.value(CWebUser.data(),"last_page").$(map("title", _("Dashboard"), "url", "dashboard.action"));
		}

		for (int i = 1; i < 6; i++) {
			if (!rda_empty(Nest.value(history,"title"+i).$())) {
				CLink url = new CLink(Nest.value(history,"title"+i).$(), Nest.value(history,"url"+i).asString().replace(".php", ".action"), "history");
				array_push(result, array(SPACE, url, SPACE));
				array_push(result, delimiter);
			}
		}
		array_pop(result);
		return result;
	}

	public static Boolean add_user_history(IIdentityBean idBean, SQLExecutor executor, Map page) {
		Object userid = CWebUser.get("userid");
		String title = Nest.value(page,"title").asString();

		String url = null; 
		if (isset(page,"hist_arg") && isArray(Nest.value(page,"hist_arg").$())) {
			CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
			StringBuilder sb = new StringBuilder();
			for(String arg : Nest.array(page,"hist_arg").asString()) {
				if (isset(_REQUEST,arg)) {
					sb.append(url_param(idBean, arg, true));
				}
			}
			if (sb.length()>0) {
				sb.setCharAt(0, '?');
			}
			url = Nest.value(page,"file").asString()+sb.toString();
		} else {
			url = Nest.value(page,"file").asString();
		}

		// if url length is greater than db field size, skip history update
		Map<String, Object> historyTableSchema = getSchema("user_history");
		if (rda_strlen(url) > Nest.value(historyTableSchema,"fields","url5","length").asInteger()) {
			return false;
		}

		SqlBuilder sqlParts = new SqlBuilder();
		Map history5 = DBfetch(DBselect(executor,
			"SELECT uh.title5,uh.url5"+
			" FROM user_history uh"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "user_history", "uh")+
			" AND uh.userid="+sqlParts.marshalParam(userid),
			sqlParts.getNamedParams()
		));

		
		String sql = null;
		Map params = new HashMap();
		params.put("userid", userid);
		params.put("tenantid", idBean.getTenantId());
		if (!empty(history5) && (title.equals(Nest.value(history5,"title5").asString()))) {
			if (!url.equals(Nest.value(history5,"url5").asString())) {
				// title same, url isnt, change only url
				params.put("url", url);
				params.put("userid", userid);
				sql = "UPDATE user_history SET url5=#{url} WHERE tenantid=#{tenantid} AND userid=#{userid}";
			} else {
				// no need to change anything;
				return null;
			}
		} else {
			// new page with new title is added
			if (history5 == null) {
				Long userhistoryid = get_dbid(idBean, executor, "user_history", "userhistoryid");
				params.put("userhistoryid", userhistoryid);
				params.put("userid", userid);
				params.put("title", title);
				params.put("url", url);
				sql = "INSERT INTO user_history (tenantid,userhistoryid, userid, title5, url5) VALUES(#{tenantid},#{userhistoryid},#{userid},#{title},#{url})";
			} else {
				params.put("title", title);
				params.put("url", url);
				params.put("userid", userid);
				sql = "UPDATE user_history SET title1=title2,url1=url2,title2=title3,url2=url3,title3=title4,url3=url4,title4=title5,url4=url5,title5=#{title},url5=#{url} WHERE tenantid=#{tenantid} AND userid=#{userid}";
			}
		}
		return DBexecute(executor,sql,params);
	}
}
