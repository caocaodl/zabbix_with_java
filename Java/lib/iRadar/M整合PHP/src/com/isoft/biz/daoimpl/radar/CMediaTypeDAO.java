package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CMediaTypeDAO extends CCoreLongKeyDAO<CMediaTypeGet> {

	public CMediaTypeDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "media_type", "mt", new String[]{"mediatypeid"});
	}
	
	@Override
	public <T> T get(CMediaTypeGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("media_type", "mt.mediatypeid");
		sqlParts.from.put("media_type", "media_type mt");
		
		// mediatypeids
		if (!is_null(params.getMediaTypeIds())) {
			sqlParts.where.dbConditionInt("mt.mediatypeid", params.getMediaTypeIds());
		}

		// mediaids
		if (!is_null(params.getMediaIds())) {
			sqlParts.select.put("mediaid","m.mediaid");
			sqlParts.from.put("media","media m");
			sqlParts.where.dbConditionInt("m.mediaid", params.getMediaIds());
			sqlParts.where.put("mmt.tenantid","m.tenantid=mt.tenantid");
			sqlParts.where.put("mmt","m.mediatypeid=mt.mediatypeid");
		}

		// userids
		if (!is_null(params.getUserIds())) {
			sqlParts.select.put("userid","m.userid");
			sqlParts.from.put("media","media m");
			sqlParts.where.dbConditionInt("m.userid",params.getUserIds());
			sqlParts.where.put("mmt.tenantid","m.tenantid=mt.tenantid");
			sqlParts.where.put("mmt","m.mediatypeid=mt.mediatypeid");
		}

		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("media_type mt", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("media_type mt", params, sqlParts);
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
				Long id = (Long)row.get("mediatypeid");
			
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// usrids
				if (isset(row.get("userid")) && is_null(params.getSelectUsers())) {
					if(!result.get(id).containsKey("users")){
						result.get(id).put("users", new ArrayList(0));
					}
					((CArray)result.get(id).get("users")).add(row.remove("userid"));
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
	
	/**
	 * Add Media types
	 *
	 * @param array _mediatypes
	 * @param string _mediatypes["type"]
	 * @param string _mediatypes["description"]
	 * @param string _mediatypes["smtp_server"]
	 * @param string _mediatypes["smtp_helo"]
	 * @param string _mediatypes["smtp_email"]
	 * @param string _mediatypes["exec_path"]
	 * @param string _mediatypes["gsm_modem"]
	 * @param string _mediatypes["username"]
	 * @param string _mediatypes["passwd"]
	 * @param int _mediatypes["status"]
	 * @return array|boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> mediatypes) {
		if (USER_TYPE_SUPER_ADMIN != Nest.value(userData(),"type").asInteger()) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can create media types."));
		}

		for(Map mediatype : mediatypes) {
			CArray mediatypeDbFields = map(
				"type", null,
				"description", null
			);
			if (!check_db_fields(mediatypeDbFields, mediatype)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for media type."));
			}

			if (in_array(Nest.value(mediatype,"type").asInteger(), array(MEDIA_TYPE_JABBER, MEDIA_TYPE_EZ_TEXTING))
					&& (!isset(mediatype,"passwd") || empty(Nest.value(mediatype,"passwd").$()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Password required for media type."));
			}

			CMediaTypeGet options = new CMediaTypeGet();
			options.setFilter("description", Nest.value(mediatype,"description").asString());
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> mediatypeExist = get(options);
			if (!empty(mediatypeExist)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Media type \"%s\" already exists.", Nest.value(mediatypeExist,0,"description").asString()));
			}
		}

		CArray<Long> mediatypeids = insert("media_type", mediatypes);

		return map("mediatypeids", mediatypeids.valuesAsLong());
	}
	
	/**
	 * Update Media types
	 *
	 * @param array _mediatypes
	 * @param string _mediatypes["type"]
	 * @param string _mediatypes["description"]
	 * @param string _mediatypes["smtp_server"]
	 * @param string _mediatypes["smtp_helo"]
	 * @param string _mediatypes["smtp_email"]
	 * @param string _mediatypes["exec_path"]
	 * @param string _mediatypes["gsm_modem"]
	 * @param string _mediatypes["username"]
	 * @param string _mediatypes["passwd"]
	 * @param int _mediatypes["status"]
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> mediatypes) {
		if (USER_TYPE_SUPER_ADMIN != Nest.value(userData(),"type").asInteger()) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can edit media types."));
		}

		CArray<Map> update = array();
		for(Map mediatype : mediatypes) {
			CArray mediatypeDbFields = map(
				"mediatypeid", null
			);
			if (!check_db_fields(mediatypeDbFields, mediatype)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for media type."));
			}

			if (isset(mediatype,"description")) {
				CMediaTypeGet options = new CMediaTypeGet();
				options.setFilter("description", Nest.value(mediatype,"description").asString());
				options.setPreserveKeys(true);
				options.setOutput(new String[]{"mediatypeid"});
				CArray<Map> existMediatypes = get(options);
				Map existMediatype = reset(existMediatypes);

				if (!empty(existMediatype) && bccomp(Nest.value(existMediatype,"mediatypeid").$(), Nest.value(mediatype,"mediatypeid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Media type \"%s\" already exists.", Nest.value(mediatype,"description").$()));
				}
			}

			if (array_key_exists("passwd", mediatype) && empty(Nest.value(mediatype,"passwd").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Password required for media type."));
			}

			if (array_key_exists("type", mediatype) && !in_array(Nest.value(mediatype,"type").asInteger(), array(MEDIA_TYPE_JABBER, MEDIA_TYPE_EZ_TEXTING))) {
				Nest.value(mediatype,"passwd").$("");
			}

			Long mediatypeid = Nest.value(mediatype,"mediatypeid").asLong();
			unset(mediatype,"mediatypeid");

			if (!empty(mediatype)) {
				update.add(map(
					"values", mediatype,
					"where", map("mediatypeid", mediatypeid)
				));
			}
		}

		update("media_type", update);
		Long[] mediatypeids = rda_objectValues(mediatypes, "mediatypeid").valuesAsLong();

		return map("mediatypeids", mediatypeids);
	}
	
	/**
	 * Delete Media types.
	 *
	 * @param array _mediatypeids
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> delete(Long... mediatypeids) {
		if (Nest.value(userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete media types."));
		}

		CActionGet options = new CActionGet();
		options.setMediaTypeIds(TArray.as(mediatypeids).asLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		CArray<Map> actions = API.Action(this.idBean, this.getSqlExecutor()).get(options);
		if (!empty(actions)) {
			Map _action = reset(actions);
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Media types used by action \"%s\".", Nest.value(_action,"name").asString()));
		}

		delete("media_type", (Map)map("mediatypeid", mediatypeids));

		return map("mediatypeids", mediatypeids);
	}

	@Override
	protected void addRelatedObjects(CMediaTypeGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		if(!is_null(params.getSelectUsers()) && !API_OUTPUT_COUNT.equals(params.getSelectUsers())){
			CRelationMap relationMap = createRelationMap(result, "mediatypeid", "userid", "media");
			CUserGet uparams = new CUserGet();
			uparams.setOutput(params.getSelectUsers());
			uparams.setUserIds(relationMap.getRelatedStringIds());
			uparams.setPreserveKeys(true);
			CArray<Map> datas = API.User(this.idBean, getSqlExecutor()).get(uparams);
			relationMap.mapMany(result, datas, "users", params.getLimitSelects());
		}
	}	

}
