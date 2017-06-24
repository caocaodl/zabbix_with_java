package com.isoft.iradar.managers;

import static com.isoft.biz.daoimpl.radar.CDB.getSchema;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.FuncsUtil.toArray;
import static com.isoft.iradar.utils.MixedUtil.rmapRemove;
import static com.isoft.iradar.utils.MixedUtil.rmapSet;
import static com.isoft.types.CArray.array;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.model.CWebUser;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.IEntry;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CProfile {
	
	private Map<String, Object> userDetails;
	private Map<String, Map<Long,Object>> profiles;
	private Map<String, Map<Long,Map>> update = new HashMap();
	private Map<String, Map<Long,Map>> insert = new HashMap();
	private Integer stringProfileMaxLength;

	private CProfile() {
	}

	public static CProfile $() {
		HttpServletRequest request = RadarContext.request();
		CProfile profile = (CProfile) request.getAttribute(CProfile.class.getName());
		if (profile == null) {
			profile = new CProfile();
			request.setAttribute(CProfile.class.getName(), profile);
		}
		return profile;
	}
	
	public static void init(IIdentityBean idBean, SQLExecutor executor) {
		$().userDetails = CWebUser.data();
		$().profiles = array();
		Map<String, Object> profilesTableSchema = getSchema("profiles");
		$().stringProfileMaxLength = Nest.value(profilesTableSchema, "fields", "value_str", "length").asInteger();
		String sql = "SELECT p.* FROM profiles p WHERE p.tenantid=#{tenantid} AND p.userid=#{userId}"
				+ " ORDER BY p.userid,p.profileid";
		Map params = new HashMap();
		params.put("tenantid", idBean.getTenantId());
		params.put("userId", $().userDetails.get("userid"));
		List<Map> dbProfiles = executor.executeNameParaQuery(sql, params);
		for(Map profile : dbProfiles){
			String value_type = getFieldByType((Integer)profile.get("type"));
			rmapSet($().profiles, (String) profile.get("idx"),
					(Long) profile.get("idx2"), profile.get(value_type));
		}
	}
	
	public static Boolean flush(IIdentityBean idBean, SQLExecutor executor) {
		if (is_null($().profiles) || $().profiles.isEmpty()) {
			return true;
		}
		if (empty(Nest.value($().userDetails,"userid").asLong())) {
			return null;
		}
		if (!$().insert.isEmpty() || !$().update.isEmpty()) {
			DBstart(executor);
			for (Entry<String, Map<Long, Map>> e : $().insert.entrySet()) {
				String idx = e.getKey();
				Map<Long, Map> profile = e.getValue();
				for(Entry<Long, Map> e2:profile.entrySet()){
					Long idx2 = e2.getKey();
					 Map data = e2.getValue();
					 try{
					 insertDB(idBean, executor, idx, data.get("value"), (Integer)data.get("type"), idx2);
					 }catch(Exception ea){
						 ea.printStackTrace();
					 }
				}
			}
			
			for (Entry<String, Map<Long, Map>> e : $().update.entrySet()) {
				String idx = e.getKey();
				Map<Long, Map> profile = e.getValue();
				for(Entry<Long, Map> e2:profile.entrySet()){
					Long idx2 = e2.getKey();
					Map data = e2.getValue();
					updateDB(idBean, executor, idx, data.get("value"), (Integer)data.get("type"), idx2);
				}
			}			
			DBend(executor);
		}
		return false;
	}
	
	public static void clear() {
		$().insert.clear();
		$().update.clear();
	}
	
	public static Object get(IIdentityBean idBean, SQLExecutor executor, String idx) {
		return get(idBean, executor, idx, null);
	}
	
	public static Object get(IIdentityBean idBean, SQLExecutor executor, String idx, Object default_value) {
		return get(idBean, executor, idx, default_value, 0L);
	}
	
	public static Object get(IIdentityBean idBean, SQLExecutor executor, String idx, Object default_value, Long idx2) {
		// no user data available, just return the default value
		if (CWebUser.data().isEmpty()) {
			return default_value;
		}

		if (is_null($().profiles)) {
			init(idBean, executor);
		}

		Object ret = Nest.value($().profiles, idx, idx2).$();
		if (isset(ret)) {
			return ret;
		} else {
			return default_value;
		}
	}
	
	private final static String SQL_PROFILE_DELETE = "DELETE FROM profiles WHERE tenantid=#{tenantid} and idx=#{idx} and idx2=#{idx2}";
	public static void delete(IIdentityBean idBean, SQLExecutor executor, String idx, Object idx2) {
		if (!isArray(idx2)) {
			idx2 = toArray(idx2);
		}
		
		Map params = new HashMap();
		params.put("tenantid", idBean.getTenantId());
		params.put("idx", idx);
		int len = Array.getLength(idx2);
		for (int i = 0; i < len; i++) {
			params.put("idx2", Array.get(idx2, i));
			executor.executeInsertDeleteUpdate(SQL_PROFILE_DELETE, params);
		}

		// remove from cache
		if (!is_null($().profiles)) {
			for (int i = 0; i < len; i++) {
				rmapRemove($().profiles, idx, Array.get(idx2, i));
			}
		}
	}
	
	/**
	 * Update favorite values in DB profiles table.
	 * @param idx max length is 96
	 * @param value mixed max length 255 for string
	 * @param type
	 * @param idx2
	 */
	public static boolean update(IIdentityBean idBean, SQLExecutor executor, String idx, Object value, Integer type) {
		return update(idBean, executor, idx, value, type, 0L);
	}
	
	/**
	 * Update favorite values in DB profiles table.
	 * @param idx max length is 96
	 * @param value mixed max length 255 for string
	 * @param type
	 * @param idx2
	 */
	public static boolean update(IIdentityBean idBean, SQLExecutor executor, String idx, Object value, Integer type, Long idx2) {
		if (is_null($().profiles)) {
			init(idBean, executor);
		}
		if (!checkValueType(value, type)) {
			return false;
		}
		
		Map<String, Object> profile = new IMap<String, Object>(
				new IEntry<String, Object>("idx", idx),
				new IEntry<String, Object>("value", value),
				new IEntry<String, Object>("type", type),
				new IEntry<String, Object>("idx2", idx2)
		);
		Object current = get(idBean, executor, idx, null, idx2);
		if (is_null(current)) {
			rmapSet($().insert, idx, idx2, profile);
		} else {
			if (!current.equals(value)) {
				rmapSet($().update, idx, idx2, profile);
			}
		}
		rmapSet($().profiles, idx, idx2, value);
		return true;
	}
	
	private static void insertDB(IIdentityBean idBean, SQLExecutor executor, String idx, Object value, Integer type, Long idx2) {
		if(value == null){
			return;
		}
		String value_type = getFieldByType(type);
		String sql = "INSERT INTO profiles (tenantid,profileid,userid,idx,"+value_type+",type,idx2) VALUES (#{tenantid},#{profileid},#{userid},#{idx},#{"+value_type+"},#{type},#{idx2})";
		Map params = new HashMap();
		params.put("tenantid", idBean.getTenantId());
		params.put("profileid", get_dbid(idBean, executor, "profiles", "profileid"));
		params.put("userid", $().userDetails.get("userid"));
		params.put("idx", idx);
		params.put(value_type, value);
		params.put("type", type);
		params.put("idx2", idx2);
		executor.executeInsertDeleteUpdate(sql, params);
	}
	
	private static void updateDB(IIdentityBean idBean, SQLExecutor executor, String idx, Object value, Integer type, Long idx2) {
		if(value == null){
			return;
		}
		
		String value_type = getFieldByType(type);
		
		Map params = new HashMap();
		params.put("idx", idx);
		params.put(value_type, value);
		params.put("tenantid", idBean.getTenantId());
		params.put("userid", $().userDetails.get("userid"));

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE profiles SET ");
		sql.append(value_type+"=#{"+value_type+"}").append(',');
		sql.append(" type=").append(type);
		sql.append(" WHERE tenantid=#{tenantid}");
		sql.append(" AND userid=#{userid}");
		sql.append(" AND idx=#{idx}");
		
		if (idx2.longValue() > 0L) {
			sql.append(" AND idx2=").append(idx2);
		}
		
		executor.executeInsertDeleteUpdate(sql.toString(), params);
	}
	
	public static String getFieldByType(Integer type) {
		String field;
		switch (type) {
		case PROFILE_TYPE_INT:
			field = "value_int";
			break;
		case PROFILE_TYPE_STR:
			field = "value_str";
			break;
		case PROFILE_TYPE_ID:
		default:
			field = "value_id";
		}
		return field;
	}
	
	private static boolean checkValueType(Object value, Integer type) {
		switch (type) {
		case PROFILE_TYPE_ID:
			return value instanceof Long || is_numeric(value);
		case PROFILE_TYPE_INT:
			return value instanceof Integer || is_numeric(value);
		case PROFILE_TYPE_STR:
			return (Nest.as(value).asString(true)).length() <= $().stringProfileMaxLength;
		default:
			return true;
		}
	}
}
