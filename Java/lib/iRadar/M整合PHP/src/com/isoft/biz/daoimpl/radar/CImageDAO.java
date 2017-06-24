package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_MAX_IMAGE_SIZE;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_USE_ICONMAP_OFF;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CImageDAO extends CCoreLongKeyDAO<CImageGet> {

	public CImageDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "images", "i", new String[]{"imageid", "name"});
	}
	
	/**
	 * Get images data
	 *
	 * @param array _options
	 * @param array _options["itemids"]
	 * @param array _options["hostids"]
	 * @param array _options["groupids"]
	 * @param array _options["triggerids"]
	 * @param array _options["imageids"]
	 * @param boolean _options["status"]
	 * @param boolean _options["editable"]
	 * @param boolean _options["count"]
	 * @param string _options["pattern"]
	 * @param int _options["limit"]
	 * @param string _options["order"]
	 * @return array|boolean image data as array or false if error
	 */
	@Override
	public <T> T get(CImageGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("images", "i.imageid");
		sqlParts.from.put("images", "images i");
		
		// imageids
		if (!is_null(params.getImageIds())) {
			sqlParts.where.dbConditionInt("imageid", "i.imageid",params.getImageIds());
		}

		// sysmapids
		if (!is_null(params.getSysmapIds())) {
			sqlParts.select.put("sm","sm.sysmapid");
			sqlParts.from.put("sysmaps","sysmaps sm");
			sqlParts.from.put("sysmaps_elements","sysmaps_elements se");
			sqlParts.where.dbConditionInt("sm", "sm.sysmapid",params.getSysmapIds());
			sqlParts.where.put("smse.tenantid","sm.tenantid=se.tenantid");
			sqlParts.where.put("smse","sm.sysmapid=se.sysmapid");
			/* To be confirmed */
			sqlParts.where.put("se.tenantid","("+
					"se.tenantid=i.tenantid"+
					" OR sm.tenantid=i.tenantid)");
			sqlParts.where.put("se","("+
					"se.iconid_off=i.imageid"+
					" OR se.iconid_on=i.imageid"+
					" OR se.iconid_disabled=i.imageid"+
					" OR se.iconid_maintenance=i.imageid"+
					" OR sm.backgroundid=i.imageid)");
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("images i", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("images i", params, sqlParts);
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
		CArray imageids = array();
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
				Long id = (Long)row.get("imageid");
				imageids.put(id, id);
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				// sysmapds
				if (isset(row.get("sysmapid"))) {
					if (!isset(result.get(id).get("sysmaps"))) {
						result.get(id).put("sysmaps", new CArray());
					}
					((CArray)result.get(id).get("sysmaps")).add(map("sysmapid", row.get("sysmapid")));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		// adding objects
		if (!is_null(params.getSelectImage())) {
			sqlParts = new SqlBuilder();
			CArray<Map> dbImgs = DBselect(getSqlExecutor(),
					"SELECT i.imageid,i.image FROM images i WHERE "+sqlParts.dual.dbConditionInt("i.imageid", imageids.valuesAsLong()),
					sqlParts.getNamedParams());
			for (Map img : dbImgs) {
				// PostgreSQL and SQLite images are stored escaped in the DB
				//Nest.value(_img,"image").$() = rda_unescape_image(Nest.value(_img,"image").$());
				//_result[_img["imageid"]]["image"] = base64_encode(Nest.value(_img,"image").$());
				Nest.value(result,img.get("imageid"),"image").$(Nest.value(img,"image").$());
			}
		}
	
		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	/**
	 * Get images.
	 *
	 * @param array _image
	 * @param array _image["name"]
	 * @param array _image["hostid"]
	 *
	 * @return array|boolean
	 */
	public CArray<Map> getObjects(CArray imageData) {
		CImageGet options = new CImageGet();
		options.setFilter(imageData);
		options.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> result = get(options);
		return result;
	}

	/**
	 * Check image existence.
	 *
	 * @param array _images
	 * @param array _images["name"]
	 *
	 * @return boolean
	 */
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("imageid", "name"), "imagetype");
		CImageGet options = new CImageGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"imageid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}

	/**
	 * Add images.
	 *
	 * @param array _images ["name" => string, "image" => string, "imagetype" => int]
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> images) {
		CArray imageids = array();

		if (Nest.value(userData(),"type").asInteger() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		for(Map image : images) {
			CArray imageDbFields = map(
				"name", null,
				"image", null,
				"imagetype", 1
			);

			if (!check_db_fields(imageDbFields, image)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Wrong fields for image \"%1$s\".", Nest.value(image,"name").$()));
			}
			if (exists(map("name", Nest.value(image,"name").$()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Image \"%1$s\" already exists.", Nest.value(image,"name").$()));
			}

//			// decode BASE64
//			Nest.value(_image,"image").$() = base64_decode(Nest.value(_image,"image").$());

			// validate image
			checkImage(Nest.value(image,"image").asString());

			Long imageid = get_dbid(this.idBean, getSqlExecutor(),"images", "imageid");
			CArray values = map(
				"imageid", imageid,
				"name", Nest.value(image,"name").$(),
				"imagetype", Nest.value(image,"imagetype").$()
			);

			Nest.value(values,"image").$(Nest.value(image,"image").$());
			String sql = "INSERT INTO images ("+implode(", ", array_keys(values))+") VALUES (#{"+implode("}, #{", array_keys(values))+"})";
			if (!DBexecute(getSqlExecutor(),sql, values)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
			}

			imageids.add(imageid);
		}

		return map("imageids", imageids.valuesAsLong());
	}

	/**
	 * Update images.
	 *
	 * @param array _images
	 *
	 * @return array (updated images)
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> images) {
		if (Nest.value(userData(),"type").asInteger() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		for(Map image : images) {
			if (!isset(image,"imageid")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for image."));
			}

			CImageGet options = new CImageGet();
			options.setFilter("name", Nest.value(image,"name").asString());
			options.setOutput(new String[]{"imageid"});
			options.setNopermissions(true);
			CArray<Map> imagesExists = get(options);
			Map imageExists = reset(imagesExists);

			if (!empty(imageExists) && (bccomp(Nest.value(imageExists,"imageid").$(), Nest.value(image,"imageid").$()) != 0)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Image \"%1$s\" already exists.", Nest.value(image,"name").$()));
			}

			CArray<Object> values = array();
			if (isset(image,"name")) {
				Nest.value(values,"name").$(Nest.value(image,"name").$());
			}
			if (isset(image,"imagetype")) {
				Nest.value(values,"imagetype").$(Nest.value(image,"imagetype").$());
			}
			if (isset(image,"image")) {
//				// decode BASE64
//				Nest.value(_image,"image").$() = base64_decode(Nest.value(_image,"image").$());

				// validate image
				checkImage(Nest.value(image,"image").asString());
				Nest.value(values,"image").$(Nest.value(image,"image").$());
			}

			CArray<String> sqlUpd = array();
			for (Entry<Object, Object> e : values.entrySet()) {
			    Object field = e.getKey();
				sqlUpd.add(field+"=#{"+field+"}");
			}
			sqlUpd.put("imageid",Nest.value(image,"imageid").asString());
			String sql = "UPDATE images SET "+implode(", ", sqlUpd)+" WHERE imageid=#{imageid}";
			boolean result = DBexecute(getSqlExecutor(),sql,sqlUpd);

			if (!result) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Could not save image!"));
			}
		}

		return map("imageids", rda_objectValues(images, "imageid").valuesAsLong());
	}

	/**
	 * Delete images.
	 *
	 * @param array _imageids
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... imageids) {
		if (empty(imageids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty parameters"));
		}

		if (Nest.value(userData(),"type").asInteger() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		// check if icon is used in icon maps
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbIconmaps = DBselect(getSqlExecutor(),
			"SELECT DISTINCT im.name"+
			" FROM icon_map im,icon_mapping imp"+
			" WHERE im.tenantid=imp.tenantid"+
				" AND im.iconmapid=imp.iconmapid"+
				" AND ("+sqlParts.dual.dbConditionInt("im.default_iconid", imageids)+
					" OR "+sqlParts.dual.dbConditionInt("imp.iconid", imageids)+")",
			sqlParts.getNamedParams()
		);

		CArray<String> usedInIconmaps = array();
		for (Map iconmap : dbIconmaps) {
			usedInIconmaps.add(Nest.value(iconmap,"name").asString());
		}

		if (!empty(usedInIconmaps)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_n("The image is used in icon map %1$s.", "The image is used in icon maps %1$s.",
					"\""+implode("\", \"", usedInIconmaps)+"\"", count(usedInIconmaps))
			);
		}

		// check if icon is used in maps
		sqlParts = new SqlBuilder();
		CArray<Map> dbSysmaps = DBselect(getSqlExecutor(),
			"SELECT DISTINCT sm.sysmapid,sm.name"+
			" FROM sysmaps_elements se,sysmaps sm"+
			" WHERE sm.tenantid=se.tenantid"+
				" AND sm.sysmapid=se.sysmapid"+
				" AND (sm.iconmapid IS NULL"+
					" OR se.use_iconmap="+SYSMAP_ELEMENT_USE_ICONMAP_OFF+")"+
				" AND ("+sqlParts.dual.dbConditionInt("se.iconid_off", imageids)+
					" OR "+sqlParts.dual.dbConditionInt("se.iconid_on", imageids)+
					" OR "+sqlParts.dual.dbConditionInt("se.iconid_disabled", imageids)+
					" OR "+sqlParts.dual.dbConditionInt("se.iconid_maintenance", imageids)+")"+
				" OR "+sqlParts.dual.dbConditionInt("sm.backgroundid", imageids),
				sqlParts.getNamedParams()
		);

		CArray<String> usedInMaps = array();
		for (Map sysmap : dbSysmaps) {
			usedInMaps.add(Nest.value(sysmap,"name").asString());
		}

		if (!empty(usedInMaps)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_n("The image is used in map %1$s.", "The image is used in maps %1$s.",
				"\""+implode("\", \"", usedInMaps)+"\"", count(usedInMaps))
			);
		}

		update("sysmaps_elements", array((Map)map("values", map("iconid_off", 0), "where", map("iconid_off", imageids))));
		update("sysmaps_elements", array((Map)map("values", map("iconid_on", 0), "where", map("iconid_on", imageids))));
		update("sysmaps_elements", array((Map)map("values", map("iconid_disabled", 0), "where", map("iconid_disabled", imageids))));
		update("sysmaps_elements", array((Map)map("values", map("iconid_maintenance", 0), "where", map("iconid_maintenance", imageids))));

		delete("images", (Map)map("imageid", imageids));

		return map("imageids", imageids);
	}
	
	/**
	 * Validate image.
	 *
	 * @param string _image string representing image, for example, result of base64_decode()
	 *
	 * @throws APIException if image size is 1MB or greater.
	 * @throws APIException if file format is unsupported, GD can not create image from given string
	 */
	protected void checkImage(String image) {
		// check size
		if (strlen(image) > RDA_MAX_IMAGE_SIZE) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Image size must be less than 1MB."));
		}

		// check file format
		if (imageCreateFromString(image) == false) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("File format is unsupported."));
		}
	}

	//TODO
	private boolean imageCreateFromString(String image) {
		return true;
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		if (params.getCountOutput() == null) {
			if (API_OUTPUT_EXTEND.equals(params.getOutput())) {
				params.setOutput(new String[]{"imageid", "imagetype", "name"});
			} else if (isArray(params.getOutput()) && in_array("image", (String[])params.getOutput())) {
				String[] fields =  (String[])params.getOutput();
				CArray<String> output = CArray.valueOf(fields);
				for(int idx =0; idx<fields.length;idx++){
					String field = fields[idx];
					if ("image".equals(field)) {
						unset(output,idx);
					}
				}
			}
			super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		}
	}
	
}
