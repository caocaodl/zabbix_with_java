package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_intersect_key;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strInArray;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.GettextwrapperUtil._params;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import jline.internal.Log;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.radar.IRadarCURD;
import com.isoft.biz.dao.radar.IRadarDAO;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CDbConfig;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CIdValidator;
import com.isoft.iradar.validators.CPartialValidatorInterface;
import com.isoft.iradar.validators.CValidator;
import com.isoft.model.schema.DBField;
import com.isoft.model.schema.DBSchema;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;
import com.isoft.types.Mapper.TObj;
import com.isoft.utils.DebugUtil;

public class CCoreDAO<P extends CParamGet, K> extends CDB implements IRadarDAO,	IRadarCURD<P, K> {
	
	protected String tableName;
	protected String tableAlias = "t";
	protected String pk;
	protected String[] sortColumns;
	
	/**
	 * An array containing all of the error strings.
	 * @var array
	 */
	protected CArray<String> errorMessages = array();

	public CCoreDAO(IIdentityBean idBean, SQLExecutor executor, String tableName, String tableAlias, String[] sortColumns) {
		super(idBean, executor);
		this.tableName = tableName;
		this.tableAlias = tableAlias;
		this.pk = executor.executeSchemaPkQuery(this.tableName());
		this.sortColumns = sortColumns;
	}
	
	private CCoreDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
		this.pk = this.pk(this.tableName());
	}
	
	protected boolean enableXMLSql() {
		return false;
	}
	
	@Override
	final protected void loadXMLSql(List<Class<?>> baseDAOs, Properties sqlProperties) {
		if(enableXMLSql()){
			super.loadXMLSql(baseDAOs, sqlProperties);
		}
	}
	
	@Override
	protected List<Class<?>> getExtendsDAOClass() {
        List<Class<?>> baseDAOs = super.getExtendsDAOClass();
        if(!baseDAOs.contains(CCoreDAO.class)){
        	baseDAOs.add(CCoreDAO.class);
        }
        return baseDAOs;
	}
	
	protected Map<String, Object> userData(){
		return CWebUser.data();
	}
	
	public String tableName() {
		return this.tableName;
	}
	
	public String tableAlias() {
		return this.tableAlias;
	}
	
	/**
	 * Returns the table name with the table alias. If the tableName and tableAlias
	 * parameters are not given, the name and the alias of the current table will be used.
	 *
	 * @param string tableName
	 * @param string tableAlias
	 *
	 * @return string
	 */
	protected String tableId(String tableName, String tableAlias) {
		tableName = !StringUtils.isEmpty(tableName) ? tableName : this.tableName();
		tableAlias = !StringUtils.isEmpty(tableAlias) ? tableAlias : this.tableAlias();
		return tableName+" "+tableAlias;
	}

	protected String fieldId(String fieldName) {
		return fieldId(fieldName, null);
	}

	/**
	 * Prepends the table alias to the given field name. If no _tableAlias is given,
	 * the alias of the current table will be used.
	 *
	 * @param string fieldName
	 * @param string tableAlias
	 *
	 * @return string
	 */
	protected String fieldId(String fieldName, String tableAlias) {
		tableAlias = !StringUtils.isEmpty(tableAlias) ? tableAlias : this.tableAlias();
		return tableAlias+"."+fieldName;
	}

	/**
	 * Returns the name of the field that's used as a private key. If the tableName is not given,
	 * the PK field of the given table will be returned.
	 *
	 * @param string tableName;
	 *
	 * @return string
	 */
	protected String pk() {
		return this.pk(null);
	}
	protected String pk(String tableName) {
		if (tableName != null && tableName.length() > 0) {
			return getSqlExecutor().executeSchemaPkQuery(tableName);
		}
		return this.pk;
	}
	
	/**
	 * Returns the name of the option that refers the PK column. If the _tableName parameter
	 * is not given, the Pk option of the current table will be returned.
	 *
	 * @param string tableName
	 *
	 * @return string
	 */
	protected String pkOption(String tableName) {
		return this.pk(tableName)+"s";
	}
	protected String pkOption() {
		return pkOption(this.tableName);
	}
	
	/**
	 * Returns an array that describes the schema of the database table. If no tableName
	 * is given, the schema of the current table will be returned.
	 */
	protected DBSchema getTableSchema(String tableName) {
		tableName = tableName!=null ? tableName : this.tableName();
		return getSqlExecutor().executeSchemaQuery(tableName);
	}
	
	/**
	 * Returns a translated error message.
	 *
	 * @param id
	 *
	 * @return string
	 */
	protected String getErrorMsg(Object id) {
		return this.errorMessages.get(id);
	}
	
	/**
	 * Adds the given fields to the "output" option if it's not already present.
	 */
	protected Object outputExtend(String tableName, String[] fields, Object output) {
		for (String field : fields) {
			if (API_OUTPUT_REFER.equals(output)) {
				output = new String[] { pk(tableName), field };
			}
			if (isArray(output)) {
				boolean isCArray = output instanceof CArray;
				if(isCArray) {
					if(!strInArray(field, ((CArray)output).valuesAsString())) {
						((CArray)output).add(field);
					}
				}else {
					String[] toutput = (String[])output;
					if(!strInArray(field, toutput)) {
						output = new String[toutput.length + 1];
						System.arraycopy(toutput, 0, output, 0, toutput.length);
						((String[])output)[toutput.length] = field;
					}
				}
			}
		}
		return output;
	}

	/**
	 * Returns true if the given field is requested in the output parameter.
	 */
	protected boolean outputIsRequested(String field, Object output) {
		if(output instanceof String){
			String soutput = (String)output;
			if(API_OUTPUT_EXTEND.equals(soutput)){
				return true;
			} else if(API_OUTPUT_REFER.equals(soutput)){
				return true;
			} else if(API_OUTPUT_COUNT.equals(soutput)){
				return false;
			}
		} else if(isArray(output)){
			return strInArray(field, CArray.valueOf(output).valuesAsString());
		} 
		return false;
	}

	/**
	 * Unsets fields _field from the given objects if they are not requested in output.
	 * @param objects
	 * @param fields
	 * @param output
	 */
	@SuppressWarnings("unchecked")
	protected <T> void unsetExtraFields(CArray<Map> objects, String[] fields, Object output) {
		List<String> extraFields = new ArrayList<String>(fields.length);
		for (String field : fields) {
			if(!outputIsRequested(field, output)){
				extraFields.add(field);
			}
		}
		if(!extraFields.isEmpty()){
			for (Map object : objects) {
				for(String field:extraFields){
					object.remove(field);
				}
			}
		}
	}
	
	
	protected CRelationMap createRelationMap(List<Map> objects, String key, String baseField, String foreignField) {
		return createRelationMap(objects, key, baseField, foreignField, null);
	}
	protected CRelationMap createRelationMap(List<Map> objects, String key, String baseField, String foreignField, String table) {
		CArray<Map> arrays = new CArray<Map>();
		for(Map m: objects) {
			arrays.put(m.get(key), m);
		}
		return createRelationMap(arrays, baseField, foreignField, table);
	}
	
	
	protected <T> CRelationMap createRelationMap(CArray<Map> objects, String baseField, String foreignField) {
		return createRelationMap(objects, baseField, foreignField, null);
	}
	protected <T> CRelationMap createRelationMap(CArray<Map> objects, String baseField, String foreignField, String table) {
		CRelationMap relationMap = new CRelationMap();
		
		// create the map from a database table
		if (table != null && table.length() > 0) {
			CParamGet params = new CParamGet();
			params.setOutput(new String[]{baseField, foreignField});
			Map<String, Object[]> filter = new LinkedMap();
			filter.put(baseField, objects.keys());
			params.setFilter(filter);
			
			SqlBuilder sqlParts = createSelectQuery(table, params);
			String sql = createSelectQueryFromParts(sqlParts);
			Map paraMap = sqlParts.getNamedParams();
			SQLExecutor executor = getSqlExecutor();
			List<Map> datas = (List<Map>)executor.executeNameParaQuery(sql, paraMap);
			for(Map relation : datas){
				relationMap.addRelation(relation.get(baseField), relation.get(foreignField));
			}
		} else {
			for (Map object : objects) {
				relationMap.addRelation(object.get(baseField), object.get(foreignField));
			}
		}
		return relationMap;
	}
	

	/**
	 * Constructs an SQL SELECT query for a specific table from the given API options, executes it and returns
	 * the result.
	 */
	protected CArray<Map> select(String tableName, CParamGet params) {
		Integer limit = isset(params.getLimit()) ? params.getLimit() : null;
		CCoreDAO idao = new CCoreDAO(this.idBean, this.getSqlExecutor());
		SqlBuilder sqlParts = idao.createSelectQuery(tableName, params);
		CArray<Map> datas = DBselect(this.getSqlExecutor(),
				createSelectQueryFromParts(sqlParts), limit,
				sqlParts.getNamedParams());		
		
		if(params.isPreserveKeys()) {
			CArray<Map> result = new CArray<Map>();
			String pk = pk(tableName);
			for(Map row : datas){
				result.put(row.get(pk), row);
			}
			return result;
		} else {
			return CArray.valueOf(datas);
		}
	}

	/**
	 * Creates an SQL SELECT query from the given options.
	 */
	protected SqlBuilder createSelectQuery(String tableName, CParamGet params) {
		return createSelectQueryParts(tableName, tableAlias(), params);
	}

	/**
	 * Builds an SQL parts array from the given params.
	 * @param tableName
	 * @param tableAlias
	 * @param options
	 * @return
	 */
	protected SqlBuilder createSelectQueryParts(String tableName, String tableAlias, CParamGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		
		sqlParts.select.put(fieldId(pk(tableName), tableAlias));
		sqlParts.from.put(tableId(tableName, tableAlias));
		
		// add filter options
		applyQueryFilterOptions(tableName, tableAlias, params, sqlParts);
		// add output options
		applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		// add tenant options
		applyQueryTenantOptions(tableName, tableAlias, params, sqlParts);
		// add sort options
		applyQuerySortOptions(tableName, tableAlias, params, sqlParts);
		
		return sqlParts;
	}

	/**
	 * Creates a SELECT SQL query from the given SQL parts array.
	 * 
	 * @param sqlParts
	 * @return
	 */
	public static String createSelectQueryFromParts(SqlBuilder sqlParts) {
		return sqlParts.createSelectQueryFromParts();
	}

	/**
	 * Modifies the SQL parts to implement all of the output related options.
	 *
	 * @param string tableName
	 * @param string tableAlias
	 * @param P  params
	 * @param SqlBuilder  sqlParts
	 *
	 * @return
	 */
	protected void applyQueryOutputOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		String pkFieldId = this.fieldId(this.pk(tableName), tableAlias);
		// count
		if (!is_null(params.getCountOutput())) {
			sqlParts.select.clear();
			sqlParts.select.put("COUNT(DISTINCT "+pkFieldId+") AS rowscount");
			// select columns used by group count
			if (!is_null(params.getGroupCount())) {
				Set<Entry<String, String>> set = sqlParts.group.namedMap.entrySet();
				for(Entry<String, String> entry : set){
					sqlParts.select.put(entry.getKey(),entry.getValue());
				}
			}
		}
		// custom output
		else if(isArray(params.getOutput())){
			// the pk field must always be included for the API to work properly
			sqlParts.select.clear();
			sqlParts.select.put(pkFieldId);
			
			String[] fields = CArray.valueOf(params.getOutput()).valuesAsString();
			if (fields.length > 0) {
				DBSchema schema = getSqlExecutor().executeSchemaQuery(tableName);
				for (String field : fields) {
					if(schema.containsField(field)){
						sqlParts.select.put(this.fieldId(field, tableAlias));
					}
				}
			}
			sqlParts.select.arrayUnique();
		}
		// extended output
		else if(API_OUTPUT_EXTEND.equals(params.getOutput())){
			// TODO: API_OUTPUT_EXTEND must return ONLY the fields from the base table
			this.addQuerySelect(this.fieldId("*", tableAlias), sqlParts);
		}
	}
	
	/**
	 * 租户粘性
	 * @return
	 */
	protected boolean isStickTenantResource(){
		return false;
	}
	
	/**
	 * Modifies the SQL parts to implement all of the sorting related options.
	 *
	 * @param string tableName
	 * @param string tableAlias
	 * @param P params
	 * @param SqlBuilder  sqlParts
	 *
	 * @return
	 */
	protected void applyQueryTenantOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		if (Role.isTenant(this.idBean.getTenantRole())|| isStickTenantResource() || params.getEditable()) {
			String fieldKey = this.fieldId("tenantid", tableName);
			String fieldId = this.fieldId("tenantid", tableAlias);
			String[] tenantids = params.getFilter() != null ? Nest.array(params.getFilter(), "tenantid").asString() : null;
			if(tenantids==null || tenantids.length==0){
				tenantids = new String[] { idBean.getTenantId() }; 
			}
			sqlParts.where.dbConditionStringAhead(fieldKey, fieldId, tenantids);
		}
	}

	/**
	 * Modifies the SQL parts to implement all of the filter related options.
	 *
	 * @param string tableName
	 * @param string tableAlias
	 * @param P  params
	 * @param SqlBuilder  sqlParts
	 *
	 * @return
	 */
	protected void applyQueryFilterOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		String pkOption = this.pkOption(tableName);
		String tableId = this.tableId(tableName, tableAlias);
		
		// pks
		if (isset(params, pkOption)) {
			sqlParts.where.dbConditionInt(this.fieldId(this.pk(tableName), tableAlias), Nest.array(params,pkOption).asLong());
		}
	
		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter(tableId, params, sqlParts);
		}
		
		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch(tableId, params, sqlParts);
		}
	}

	/**
	 * Modifies the SQL parts to implement all of the sorting related options.
	 *
	 * @param string tableName
	 * @param string tableAlias
	 * @param P params
	 * @param SqlBuilder  sqlParts
	 *
	 * @return
	 */
	protected void applyQuerySortOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		if (this.sortColumns != null && this.sortColumns.length > 0
				&& params.getSortfield() != null
				&& params.getSortfield().length > 0) {
			String[] sortfields = params.getSortfield();
			String[] sortorders = params.getSortorder();
			if (sortfields !=null && sortfields.length>0) {
				for(int i=0;i<sortfields.length;i++){
					String sortfield = sortfields[i];					
					if(strInArray(sortfield,sortColumns)){
						String sortorder = (sortorders!=null && i<sortorders.length)?sortorders[i]:"";
						if(RDA_SORT_DOWN.equals(sortorder)){
							sortorder = " "+RDA_SORT_DOWN;
						} else {
							sortorder = "";
						}
						this.applyQuerySortField(sortfield, sortorder, tableAlias, sqlParts);
						
						if(sqlParts.from.size()>1){
							if(!sqlParts.select.namedMap.containsValue(tableAlias+"."+sortfield)){
								sqlParts.select.put(sortfield,tableAlias+"."+sortfield);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a specific property from the 'sortfield' parameter to the sqlParts array.
	 *
	 * @param string sortfield
	 * @param string sortorder
	 * @param string alias
	 * @param array  sqlParts
	 *
	 * @return
	 */
	protected void applyQuerySortField(String sortfield, String sortorder, String alias, SqlBuilder sqlParts) {
		sqlParts.order.put(alias+"."+sortfield,alias+"."+sortfield+sortorder);
	}

	/**
	 * Adds the given field to the SELECT part of the sqlParts array if it's not already present.
	 * If _sqlParts['select'] not present it is created and field appended.
	 *
	 * @param string fieldId
	 * @param array  sqlParts
	 *
	 * @return
	 */
	protected void addQuerySelect(String fieldId, SqlBuilder sqlParts) {
		if(sqlParts.select.isEmpty()){
			sqlParts.select.put(fieldId);
			return;
		}
		
		String[] segments = fieldId.split("\\.");
		String tableAlias = segments[0];
		String field = segments.length>1? segments[1]: null;
		
		if (!sqlParts.select.containsValue(fieldId)
				&& !sqlParts.select.containsValue(this.fieldId("*", tableAlias))) {
			// if we want to select all of the columns, other columns from this table can be removed
			if("*".equals(field)){
				List<String> needClearKeys = new ArrayList<String>();
				
				Set<Entry<String, String>> set = sqlParts.select.namedMap.entrySet();
				for(Entry<String, String> e : set){
					String key = e.getKey();
					String selectFieldId = e.getValue();
					String selectTableAlias = selectFieldId.split("\\.")[0];
					if(tableAlias.equals(selectTableAlias)){
						needClearKeys.add(key);
					}
				}
				
				for(String key: needClearKeys) {
					sqlParts.select.clear(key);
				}
			}
			sqlParts.select.put(fieldId);
		}
	}
	
	/**
	 * Adds the given field to the ORDER BY part of the _sqlParts array.
	 *
	 * @param string _fieldId
	 * @param array  _sqlParts
	 * @param string _sortorder		sort direction, RDA_SORT_UP or RDA_SORT_DOWN
	 *
	 * @return array
	 */
	protected void addQueryOrder(String fieldId, SqlBuilder sqlParts) {
		addQueryOrder(fieldId, sqlParts, null);
	}
	protected void addQueryOrder(String fieldId, SqlBuilder sqlParts, String sortorder) {
		// some databases require the sortable column to be present in the SELECT part of the query
		addQuerySelect(fieldId, sqlParts);
		sqlParts.order.put(fieldId, fieldId+(sortorder!=null ? ' '+sortorder : ""));		
	}

	/**
	 * Adds the related objects requested by "select*" options to the resulting object set.
	 */
	protected void addRelatedObjects(P params, CArray<Map> result) {
	}
	
	/**
	 * Deletes the object with the given IDs with respect to relative objects.
	 *
	 * The method must be extended to handle relative objects.
	 *
	 * @param array _ids
	 */
	protected void deleteByIds(CArray<Long> ids) {
		delete(this.tableName(), (Map)map(
			this.pk(), ids.valuesAsLong()
		));
	}
	
	/**
	 * Fetches the fields given in _fields from the database and extends the objects with the loaded data.
	 *
	 * @param string _tableName
	 * @param array  _objects
	 * @param array  _fields
	 *
	 * @return array
	 */
	protected CArray<Map> extendObjects(String tableName, CArray<Map> objects, String[] fields) {
		CParamGet params = new CParamGet();
		params.setOutput(fields);
		params.put(pkOption(tableName),rda_objectValues(objects, pk(tableName)).valuesAsString());
		params.setPreserveKeys(true);
		CArray<Map> dbObjects = select(tableName, params);
		for(Map object : objects) {
			Object pk = Nest.value(object,pk(tableName)).$();
			if (isset(dbObjects,pk)) {
				check_db_fields(Nest.value(dbObjects,pk).asCArray(), object);
			}
		}
		return objects;
	}
	
	/**
	 * An extendObjects() wrapper for singular objects.
	 *
	 * @see extendObjects()
	 *
	 * @param string _tableName
	 * @param array  _object
	 * @param array  _fields
	 *
	 * @return mixed
	 */
	protected Map extendObject(String tableName, Map object, String[] fields) {
		CArray<Map> objects = extendObjects(tableName, array(object), fields);
		return reset(objects);
	}
	
	/**
	 * For each object in _objects the method copies fields listed in _fields that are not present in the target
	 * object from from the source object.
	 *
	 * Matching objects in both arrays must have the same keys.
	 *
	 * @param array  _objects
	 * @param array  _sourceObjects
	 *
	 * @return array
	 */
	protected CArray<Map> extendFromObjects(CArray<Map> objects, CArray<Map> sourceObjects, CArray<String> fields) {
		CArray sfields = array_flip(fields);

		for (Entry<Object, Map> e : objects.entrySet()) {
            Object key = e.getKey();
            Map object = e.getValue();
			if (isset(sourceObjects,key)) {
				TObj.as(object).plus(array_intersect_key(CArray.valueOf(sourceObjects.get(key)), sfields));
			}
		}
		return objects;
	}
	
	/**
	 * Checks that each object has a valid ID.
	 *
	 * @param array _objects
	 * @param idField			name of the field that contains the id
	 * @param messageRequired	error message if no ID is given
	 * @param messageEmpty		error message if the ID is empty
	 * @param messageInvalid	error message if the ID is invalid
	 */
	protected void checkObjectIds(CArray<Map> objects, String idField, String messageRequired, String messageEmpty, String messageInvalid) {
		CIdValidator _idValidator = CValidator.init(new CIdValidator(),map(
			"messageEmpty", messageEmpty,
			"messageRegex", messageInvalid
		));
		for (Map _object:objects) {
			if (!isset(_object,idField)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _params(messageRequired, idField));
			}

			checkValidator(Nest.value(_object,idField).asString(), _idValidator);
		}
	}
	
	@Override
	public <T> T get(P params) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isReadable(K... ids) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isWritable(K... ids) {
		throw new NotImplementedException();
	}
	
	@Override
	public boolean exists(CArray object) {
		throw new NotImplementedException();
	}

	@Override
	public CArray<K[]> create(CArray<Map> rows) {
		throw new NotImplementedException();
	}

	@Override
	public CArray<K[]> update(CArray<Map> rows) {
		throw new NotImplementedException();
	}

	@Override
	public CArray<K[]> delete(K... ids) {
		throw new NotImplementedException();
	}

	@Override
	public Object getObjects(Map<String, Object[]> filter) {
		throw new NotImplementedException();
	}

	final protected P getParamInstance() {
		String daoClassName = getClass().getSimpleName();
		String paramClassName = String.format(CParamGet.clazzNameFormat, daoClassName.substring(0,daoClassName.length()-3));
		try {
			return (P)Class.forName(paramClassName).newInstance();
		} catch (Exception e) {
			throw new NotImplementedException(e);
		}
	}
		
	protected boolean dbSearch(String table, CParamGet options, SqlBuilder sqlParts) {
		String[] segments = table.split(" ");
		table = segments[0];
		String tableShort = segments[1];
		SQLExecutor executor = getSqlExecutor();
		DBSchema schema = executor.executeSchemaQuery(table);
		
		String exclude = is_null(options.getExcludeSearch()) ? "" : " NOT ";
		
		List<String> search = new ArrayList<String>();
		String field = null;
		String pattern = null;
		for(Map.Entry<String,String> entry:options.getSearch().entrySet()){
			if(!schema.containsField(entry.getKey()) || is_null(entry.getValue())){
				continue;
			}
			
			if(isNumericFieldType(schema.getField(entry.getKey()))){
				continue;
			}
			
			field = tableShort + "." + entry.getKey();
			pattern = entry.getValue();
			
			StringBuilder subSearch = new StringBuilder();
			subSearch.append(" ").append(field);
			subSearch.append(" ").append(exclude);
			subSearch.append(" ").append("REGEXP");
			if (is_null(options.getSearchWildcardsEnabled())) {
				subSearch.append(" ");
				subSearch.append("TOREGEXP(");
				if (is_null(options.getStartSearch())) {
					subSearch.append(sqlParts.marshalParam(pattern.toUpperCase()));
				} else {
					subSearch.append("concat('^',").append(sqlParts.marshalParam(pattern.toUpperCase())).append(')');
				}
				subSearch.append(")");
			} else {
				subSearch.append(" ");
				subSearch.append("WCREGEXP(");
				if (is_null(options.getStartSearch())) {
					subSearch.append("concat(").append(sqlParts.marshalParam(pattern.toUpperCase())).append(",'$')");
				} else {
					subSearch.append("concat('^',").append(sqlParts.marshalParam(pattern.toUpperCase())).append(",'$')");
				}
				subSearch.append(")");
			}
			
			search.add(subSearch.toString());
		}
		
		if (!search.isEmpty()) {
			if (isset(sqlParts.where.get("search"))) {
				search.add(sqlParts.where.get("search"));
			}

			String glue = (is_null(options.getSearchByAny()) || !options.getSearchByAny()) ? " AND " : " OR ";
			sqlParts.where.put("search","("+StringUtils.join(search,glue)+")");
			return true;
		}
		return false;
	}

	protected boolean dbFilter(String table, CParamGet options, SqlBuilder sqlParts) {
		String[] segments = table.split(" ");
		table = segments[0];
		String tableShort = segments[1];
		SQLExecutor executor = getSqlExecutor();
		DBSchema schema = executor.executeSchemaQuery(table);
		List<String> filter = new ArrayList<String>();
		String fieldName = null;
		for(Map.Entry<String,?> entry:options.getFilter().entrySet()){
			String field = entry.getKey();
			Object value = entry.getValue();
			if(!schema.containsField(field) || FIELD_TYPE_TEXT.equals(schema.getField(field).getTypeName())  || is_null(value)){
				continue;
			}
			
			fieldName = fieldId(field, tableShort);
			filter.add(isNumericFieldType(schema.getField(field)) 
							? sqlParts.dual.dbConditionInt(fieldName, TArray.as(value).asLong())
							: sqlParts.dual.dbConditionString(fieldName, TArray.as(value).asString()));
		}
		
		if(!filter.isEmpty()){
			if (isset(sqlParts.where.get("filter"))) {
				filter.add(sqlParts.where.get("filter"));
			}
			if (is_null(options.getSearchByAny()) || !options.getSearchByAny() || filter.size() == 1) {
				sqlParts.where.put("filter",StringUtils.join(filter," AND "));
			} else {
				sqlParts.where.put("filter","("+StringUtils.join(filter," OR ")+")");
			}
			return true;
		}
		return false;
	}
	
	protected Map arrayMap(){
		Map map = new HashMap();
		return map;
	}
	
	@Deprecated
	protected boolean isNumericFieldType(DBField field){
		if(field != null){
			return field.getTypeName().contains("INT");
		} else {
			return false;
		}
	}
	
	private static CDbConfig dbConfig = null;
	private static Object dbConfigLock = new Object();
	public CDbConfig getDbConfig(){
		if (dbConfig == null) {
			synchronized (dbConfigLock) {
				if (dbConfig == null) {
					String sql = "select * from config where tenantid = '-' order by configid desc limit 1";
					Map paraMap = new HashMap(0);
					SQLExecutor executor = getSqlExecutor();
					List<CDbConfig> datas = executor.executeNameParaQuery(sql, paraMap,CDbConfig.class);
					if(!datas.isEmpty()){
						dbConfig = datas.get(0);
					}
				}
			}
		}
		return dbConfig;
	}
	
	/**
	 * Adds a deprecated property to an array of resulting objects if it's requested in output. The value for the
	 * deprecated property will be taken from the new one.
	 * @param objects
	 * @param deprecatedProperty
	 * @param newProperty
	 * @param output
	 */
	protected void handleDeprecatedOutput(CArray<Map> objects, String deprecatedProperty, String newProperty, Object output) {
		if (outputIsRequested(deprecatedProperty, output)) {
			for (Map object : objects) {
				object.put(deprecatedProperty, object.get(newProperty));
			}
		}
	}
	
	/**
	 * Fetch data from DB.
	 * If post SQL filtering is necessary, several queries will be executed. SQL limit is calculated so that minimum
	 * amount of queries would be executed and minimum amount of unnecessary data retrieved.
	 * @return 
	 */
	protected CArray<Map> customFetch(String query, Map params, P options) {
		if (requiresPostSqlFiltering(options)) {
			int offset = 0;
			Integer limit = !empty(options.getLimit()) ? 2 * options.getLimit() : null;
			Integer  minLimit = limit;
			boolean hasMore = false;
			CArray<Map> elements = null;
			CArray<Map> allElements = new CArray();
			do {
				elements = DBselect(getSqlExecutor(), query, limit, offset, params);
				hasMore = (limit!=null && elements.size() == limit);
				elements = applyPostSqlFiltering(elements, options);
				
				if (!empty(options.getLimit()) && (allElements.size() + elements.size()) >= options.getLimit()) {
					allElements.putAll(array_slice(elements, 0, options.getLimit() - count(allElements), true));
					break;
				}
				
				allElements.putAll(elements);
				if (limit != null && limit > 0) {
					offset += limit;
					minLimit *= 2;
					int elemCount = (!elements.isEmpty()) ? elements.size() : 1;
					limit = Math.max(minLimit, Math.round(limit / elemCount * (options.getLimit() - allElements.size()) * 2));
				}
			} while (hasMore);
			
			return allElements;
		} else {
			return DBselect(getSqlExecutor(), query, options.getLimit(), params);
		}
	}
	
	/**
	 * Checks if post SQL filtering necessary.
	 * @param params
	 * @return
	 */
	protected boolean requiresPostSqlFiltering(P params){
		return false;
	}
	
	/**
	 * Removes elements which could not be removed within SQL query.
	 * @param elements
	 * @param params
	 * @return
	 */
	protected CArray applyPostSqlFiltering(CArray elements,P params){
		return elements;
	}
	
	/**
	 * Triggers a deprecated notice. Should be called when a deprecated parameter or method is used.
	 * The notice will not be displayed in the result returned by an API method.
	 *
	 * @param string _error		error text
	 */
	protected void deprecated(String error) {
		if(DebugUtil.isDebugEnabled()) {
			DebugUtil.debug("E_USER_NOTICE:"+error);
		}
	}
	
	/**
	 * Converts a deprecated parameter to a new one in the _params array. If both parameter are used,
	 * the new parameter will override the deprecated one.
	 * If a deprecated parameter is used, a notice will be triggered in the frontend.
	 *
	 * @param array  params
	 * @param string deprecatedParam
	 * @param string newParam
	 *
	 * @return array
	 */
	protected <T extends Map> T convertDeprecatedParam(T params, String deprecatedParam, String newParam) {
		if (isset(params,deprecatedParam)) {
			deprecated("Parameter \""+deprecatedParam+"\" is deprecated.");

			// if the new parameter is not used, use the deprecated one instead
			if (!isset(params,newParam)) {
				params.put(newParam, params.get(deprecatedParam));
			}

			// unset the deprecated parameter
			unset(params,deprecatedParam);
		}
		return params;
	}
	
	/**
	 * Check if a set of parameters contains a deprecated parameter or a a parameter with a deprecated value.
	 * If _value is not set, the method will trigger a deprecated notice if _params contains the _paramName key.
	 * If _value is set, the method will trigger a notice if the value of the parameter is equal to the deprecated value
	 * or the parameter is an array and contains a deprecated value.
	 *
	 * @param array  _params
	 * @param string _paramName
	 * @param string _value
	 *
	 * @return void
	 */
	protected void checkDeprecatedParam(CArray params, String paramName) {
		checkDeprecatedParam(params, paramName, null);
	}
	protected void checkDeprecatedParam(CArray params, String paramName, String value) {
		if (isset(params, paramName)) {
			if (value == null) {
				deprecated("Parameter \""+paramName+"\" is deprecated.");
			} else if (isArray(params.get(paramName)) && in_array(value, Nest.as(params.get(paramName)).asCArray()) || value.equals(params.get(paramName))) {
				deprecated("Value \""+value+"\" for parameter \""+paramName+"\" is deprecated.");
			}
		}
	}
	
	/**
	 * Runs the given validator and throws an exception if it fails.
	 *
	 * @param value
	 * @param CValidator _validator
	 */
	protected <T> void checkValidator(T value, CValidator<T> validator) {
		if (!validator.validate(this.idBean, value)) {
			throw CDB.exception(RDA_API_ERROR_INTERNAL, validator.getError());
		}
	}
	
	/**
	 * Runs the given partial validator and throws an exception if it fails.
	 *
	 * @param array _array
	 * @param CPartialValidatorInterface _validator
	 * @parma array _fullArray
	 */
	protected void checkPartialValidator(Map array, CPartialValidatorInterface validator, Map fullArray) {
		if (!validator.validatePartial(this.idBean, array, fullArray)) {
			throw CDB.exception(RDA_API_ERROR_INTERNAL, validator.getError());
		}
	}
	
	protected void checkPartialValidator(Map array, CPartialValidatorInterface validator) {
		this.checkPartialValidator(array, validator, array());
	}
	
	/**
	 * Checks if an objects contains any of the given parameters.
	 *
	 * Example:
	 * checkNoParameters(_item, array('templateid', 'state'), _('Cannot set "%1$s" for item "%2$s".'), _item['name']);
	 * If any of the parameters 'templateid' or 'state' are present in the object, it will be placed in "%1$s"
	 * and _item['name'] will be placed in "%2$s".
	 *
	 * @throws APIException			if any of the parameters are present in the object
	 *
	 * @param array  _object
	 * @param array  _params		array of parameters to check
	 * @param string _error
	 * @param string _objectName
	 */
	protected void checkNoParameters(Map object, String[] params, String error, String objectName) {
		for(String param : params) {
			if (array_key_exists(param, object)) {
				error = _params(error, param, objectName);
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, error);
			}
		}
	}

	/**
	 * Checks if the object has any fields, that are not defined in the schema or in _additionalFields.
	 *
	 * @param string tableName
	 * @param array  object
	 * @param string error
	 * @param array  extraFields	an array of field names, that are not present in the schema, but may be
	 *								used in requests
	 *
	 * @throws APIException
	 */
	protected void checkUnsupportedFields(String tableName, Map object, String error, CArray extraFields) {
		extraFields = array_flip(extraFields);
		for(Object field: object.keySet()) {
			if (!hasField(tableName, Nest.as(field).asString()) && !isset(extraFields,field)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, error);
			}
		}
	}
	
	protected void checkUnsupportedFields(String tableName, Map object, String error) {
		checkUnsupportedFields(tableName, object, error, array());
	}
}
