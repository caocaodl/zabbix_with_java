package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CUserMediaGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;

/**
 * Class containing methods for operations with users media.
 *
 * @package API
 */
public class CUserMediaDAO extends CCoreLongKeyDAO<CUserMediaGet> {

	public CUserMediaDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "media", "m", new String[]{"mediaid", "userid", "mediatypeid"});
	}

	@Override
	public <T> T get(CUserMediaGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("media", "m.mediaid");
		sqlParts.from.put("media", "media m");
		
		// mediaids
		if (!is_null(params.getMediaIds())) {
			sqlParts.where.dbConditionInt("m.mediaid", params.getMediaIds());
		}

		// userids
		if (!is_null(params.getUserIds())) {
			sqlParts.select.put("userid","u.userid");
			sqlParts.from.put("users","users u");
			sqlParts.where.dbConditionInt("u.userid",params.getUserIds());
			sqlParts.where.put("mu.tenantid","m.tenantid=u.tenantid");
			sqlParts.where.put("mu","m.userid=u.userid");
			
			if(!is_null(params.getCountOutput())){
				sqlParts.group.put("userid","m.userid");
			}
		}

		// usrgrpids
		if (!is_null(params.getUsrgrpIds())) {
			sqlParts.select.put("usrgrpid","ug.usrgrpid");
			sqlParts.from.put("users_groups","users_groups ug");
			sqlParts.where.dbConditionInt("ug.usrgrpid",params.getUsrgrpIds());
			sqlParts.where.put("mug.tenantid","m.tenantid=ug.tenantid");
			sqlParts.where.put("mug","m.userid=ug.userid");
			
			if(!is_null(params.getCountOutput())){
				sqlParts.group.put("usrgrpid","ug.usrgrpid");
			}
		}
		
		// mediatypeids
		if (!is_null(params.getMediatypeIds())) {
			sqlParts.select.put("mediatypeid","m.mediatypeid");
			sqlParts.where.dbConditionInt("m.mediatypeid",params.getMediatypeIds());
			
			if(!is_null(params.getCountOutput())){
				sqlParts.group.put("mediatypeid","m.mediatypeid");
			}
		}

		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("media m", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("media m", params, sqlParts);
		}
		
		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
					ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("mediaid");
			
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}

}
