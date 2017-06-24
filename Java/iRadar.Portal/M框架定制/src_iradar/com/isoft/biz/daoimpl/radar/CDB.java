package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.getDBType;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.RDA_DB_DB2;
import static com.isoft.iradar.inc.FuncsUtil.rda_ctype_digit;
import static com.isoft.iradar.inc.FuncsUtil.rda_is_int;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.toArray;
import static com.isoft.iradar.inc.SchemaUtil.SCHEMAS;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static org.apache.commons.lang.StringUtils.join;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CDB extends BaseDAO {
		
	public static final int DBEXECUTE_ERROR = 1;
	public static final int RESERVEIDS_ERROR = 2;
	public static final int SCHEMA_ERROR = 3;
	public static final int INPUT_ERROR = 4;
	
	public final static int TABLE_TYPE_CONFIG = 1;
	public final static int TABLE_TYPE_HISTORY = 2;
	
	public final static String FIELD_TYPE_INT = "int";
	public final static String FIELD_TYPE_CHAR = "char";
	public final static String FIELD_TYPE_ID = "id";
	public final static String FIELD_TYPE_FLOAT = "float";
	public final static String FIELD_TYPE_UINT = "uint";
	public final static String FIELD_TYPE_BLOB = "blob";
	public final static String FIELD_TYPE_TEXT = "text";
	
	protected IIdentityBean idBean;
	
	public CDB(IIdentityBean idBean, SQLExecutor executor) {
		super(executor);
		this.idBean = idBean;
	}
	
	@Override
	public String getSql(String arg0) {
		return super.getSql(arg0);
	}

	public static APIException exception() {
		return new APIException();
	}
	
	public static APIException exception(int code, String msg) {
		return new APIException(code, msg);
	}
	
	protected long reserveIds(String table, int count) {
		return reserveIds(this.idBean, getSqlExecutor(), table, count);
	}
	
	protected static long reserveIds(IIdentityBean idBean, SQLExecutor executor, String table, int count) {
		Map<String, Object> tableSchema = getSchema(table);
		String fieldName = (String)tableSchema.get("key");
		return get_dbid(idBean, executor, table, fieldName, count);
	}
	
	/**
	 * Returns an array that describes the schema of the database table. If no tableName
	 * is given, the schema of the current table will be returned.
	 */
	public static Map<String, Object> getSchema(String tableName) {
		return SCHEMAS.get(tableName);
	}

	public static String getPk(String tableName){
		return (String)getSchema(tableName).get("key");
	}
	
	/**
	 * Returns true if the table has the given field. If no _tableName is given,
	 * the current table will be used.
	 */
	public static boolean hasField(String tableName, String fieldName) {
		Map<String, Object> schema = getSchema(tableName);
		return isset((Map)schema.get("fields"), fieldName);
	}
	
	private static void addMissingFields(IIdentityBean idBean, Map<String, Object> tableSchema, CArray<Map> values) {
		Map<String, Map> fields = (Map) tableSchema.get("fields");
		for (Entry<String, Map> e : fields.entrySet()) {
			String name = e.getKey();
			Map<String, Object> field = (Map) e.getValue();
			if (FIELD_TYPE_TEXT.equals(field.get("type")) && !(Boolean) field.get("null")) {
				for (Map value : values) {
					if (!isset(value, name)) {
						value.put(name, "");
					}
				}
			} else if("tenantid".equals(name)){
				for (Map value : values) {
					value.put(name, idBean.getTenantId());
				}
			}
		}
	}
	
	public static Map<String, Object> getDefaults(String table) {
		Map<String, Object> tableSchema = getSchema(table);
		Map<String, Map<String, Object>> fields = (Map) tableSchema.get("fields");
		Map<String, Object> defaults = new HashMap();
		for (Entry<String, Map<String, Object>> e : fields.entrySet()) {
			String name = e.getKey();
			Map<String, Object> field = e.getValue();
			if (isset(field, "default")) {
				defaults.put(name, field.get("default"));
			}
		}
		return defaults;
	}
	
	public static CArray<Map> dbfetchArrayAssoc(CArray<Map> data, String field) {
		CArray<Map> result = new CArray<Map>();
		for (Map row: data) {
			Object key = row.get(field);
			result.put(key, row);
		}
		return result;
	}
	
	private static void checkValueTypes(String table, Map<String, Object> values) {
		Map<String, Object> tableSchema = getSchema(table);
		Map<String, Map<String,Object>> fields = (Map)tableSchema.get("fields");
		
		Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
			String field = e.getKey();
			Object value = e.getValue();
			if (!isset(fields, field)) {
				iterator.remove();
				continue;
			}
			
			if ("tenantid".equals(field)) {
				iterator.remove();
				continue;
			}
			
			if (isset(fields.get(field), "ref_table")) {
				if ((Boolean) fields.get(field).get("null")) {
					if (0 == Nest.as(value).asLong()) {
						values.put(field, (value= null));
					}
				}
			}
			
			if (is_null(value)) {
				if ((Boolean) fields.get(field).get("null")) {
					values.put(field, (value=null));
				} else if (isset(fields.get(field), "default")) {
					values.put(field, (value=fields.get(field).get("default")));
				} else {
					throw new RuntimeException(_s("Field \"%1$s\" cannot be set to NULL.", field));
				}
			} else {
				String type = Nest.value(fields, field, "type").asString();
				String fvalue = Nest.value(values,field).asString();
				if (FIELD_TYPE_CHAR.equals(type)) {
					int length = rda_strlen(fvalue);
					if (length > Nest.value(fields, field, "length").asInteger()) {
						throw CDB.exception(SCHEMA_ERROR, _s("Value \"%1$s\" is too long for field \"%2$s\" - %3$d characters. Allowed length is %4$d characters.",
								fvalue, field, length, Nest.value(fields, field, "length").asInteger()));
					}
				} else if(FIELD_TYPE_ID.equals(type) || FIELD_TYPE_UINT.equals(type)){
					if (!rda_ctype_digit(fvalue)) {
						throw CDB.exception(DBEXECUTE_ERROR, _s("Incorrect value \"%1$s\" for unsigned int field \"%2$s\".", fvalue, field));
					}
				} else if(FIELD_TYPE_INT.equals(type)){
					if (!rda_is_int(fvalue)) {
						throw CDB.exception(DBEXECUTE_ERROR, _s("Incorrect value \"%1$s\" for int field \"%2$s\".", fvalue, field));
					}
				} else if(FIELD_TYPE_FLOAT.equals(type)){
					if (!is_numeric(fvalue)) {
						throw CDB.exception(DBEXECUTE_ERROR, _s("Incorrect value \"%1$s\" for float field \"%2$s\".", fvalue, field));
					}
				} else if(FIELD_TYPE_TEXT.equals(type)){
					int length = rda_strlen(fvalue);
					if(RDA_DB_DB2.equals(getDBType())){
						if (length > 2048) {
							throw CDB.exception(SCHEMA_ERROR, _s("Value \"%1$s\" is too long for field \"%2$s\" - %3$d characters. Allowed length is 2048 characters.",
									fvalue, field, length));
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the records that match the given criteria.
	 *
	 * @static
	 *
	 * @param string _tableName
	 * @param array _criteria   An associative array of field-value pairs, where value can be either a single value
	 *                          or an array (IN)
	 *
	 * @return array
	 */
	public CArray find(String _tableName, CArray<?> _criteria) {
		return find(this.getSqlExecutor(),_tableName, _criteria);
	}
	
	public CArray find(String _tableName) {
		return find(_tableName, array());
	}
	
	/**
	 * Returns the records that match the given criteria.
	 *
	 * @static
	 *
	 * @param string _tableName
	 * @param array _criteria   An associative array of field-value pairs, where value can be either a single value
	 *                          or an array (IN)
	 *
	 * @return array
	 */
	public static CArray find(SQLExecutor executor,String _tableName, CArray<?> _criteria) {
		// build the WHERE part
		SqlBuilder _sqlWhere = new SqlBuilder();
		
		for(Entry<Object, ?> entry: _criteria.entrySet()) {
			String _field = asString(entry.getKey());
			Object _value = entry.getValue();
			
			// check if the table has this field
			if (!hasField(_tableName, _field)) {
				throw CDB.exception(DBEXECUTE_ERROR, _s("Table \"%1$s\" doesn't have a field named \"%2$s\".", _tableName, _field));
			}
			_sqlWhere.where.dbConditionString(_field, rda_toArray(_value).valuesAsString());
		}

		// build query
		_sqlWhere.select.put("*");
		_sqlWhere.from.put(_tableName);
		return DBfetchArray(DBselect(executor, _sqlWhere));
	}
	
	public static CArray find(SQLExecutor executor,String _tableName) {
		return find(executor, _tableName, array());
	}
	
	/**
	 * Insert data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	public CArray<Long> insert(String table, CArray<Map> values) {
		return insert(this.idBean, getSqlExecutor(), table, values, true);
	}

	/**
	 * Insert data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	public static CArray<Long> insert(IIdentityBean idBean, SQLExecutor executor, String table, CArray<Map> values) {
		return insert(idBean, executor, table, values, true);
	}
	
	/**
	 * Insert data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	protected CArray<Long> insert(String table, CArray<Map> values, boolean getids) {
		return insert(this.idBean, getSqlExecutor(), table, values, getids);
	}

	/**
	 * Insert data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	public static CArray<Long> insert(IIdentityBean idBean, SQLExecutor executor, String table, CArray<Map> values, boolean getids) {
		if (empty(values)) {
			return null;
		}
		
		values = Clone.deepcopy(values); //下面的操作会删除不属于当前的表的字段，从而影响后面的保存操作，故做一次clone
		
		CArray<Long> resultIds = array();
		long id = 0;
		if (getids) {
			id = reserveIds(idBean, executor, table, count(values));
		}
	
		Map tableSchema = getSchema(table);
		addMissingFields(idBean, tableSchema, values);
		for (Entry<Object, Map> entry: values.entrySet()) {
			Object key = entry.getKey();
			Map<String, Object> row = entry.getValue();
			
			if (getids) {
				resultIds.put(key, id);
				row.put((String)tableSchema.get("key"), id);
				id++;
			}
			checkValueTypes(table, row);
			StringBuilder sql = new StringBuilder();
			StringBuilder sqlv = new StringBuilder();
			Map params = new HashMap();
			int i=1;
			sql.append("INSERT INTO ").append(table).append("(");
			sqlv.append("VALUES").append("(");
			boolean first = true;
			for (Entry<String, Object> e : row.entrySet()) {
				if (first) {
					first = false;
				} else {
					sql.append(',');
					sqlv.append(',');
				}
				sql.append(e.getKey());
				sqlv.append("#{").append("v"+i).append("}");
				params.put("v"+i, e.getValue());
				i++;
			}
			
			//增加创建时的租户字段的赋值
			String prefix = first?"":",";
			sql.append(prefix + "tenantid");
			sqlv.append(prefix+ "#{").append("v"+i).append("}");
			params.put("v"+i, idBean.getTenantId());
			
			sql.append(")");
			sqlv.append(")");
			sql.append(sqlv);
			executor.executeInsertDeleteUpdate(sql.toString(), params);
		}
		return resultIds;
	}
	
	/**
	 * Insert batch data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	public static CArray<Long> insertBatch(IIdentityBean idBean, SQLExecutor executor, String table, CArray<Map> values) {
		return insertBatch(idBean, executor, table, values, true);
	}
	
	/**
	 * Insert batch data into DB.
	 *
	 * @param string _table
	 * @param array  _values pair of fieldname => fieldvalue
	 * @param bool   _getids
	 *
	 * @return array    an array of ids with the keys preserved
	 */
	public static CArray<Long> insertBatch(IIdentityBean idBean, SQLExecutor executor, String table, CArray<Map> values, boolean getids) {
		if (empty(values)) {
			return null;
		}

		CArray<Long> resultIds = array();

		Map<String, Object> tableSchema = getSchema(table);
		addMissingFields(idBean, tableSchema, values);

		long id = 0;
		if (getids) {
			id = reserveIds(idBean, executor, table, count(values));
		}

		String tenantidKey = "tenantid";
		
		CArray<Map> newValues = array();
		for (Entry<Object, Map> entry: values.entrySet()) {
			Object key = entry.getKey();
			Map<String, Object> row = entry.getValue();
			if (getids) {
				Nest.value(resultIds,key).$(id);
				row.put((String)tableSchema.get("key"), id);
				Nest.value(values,key,tableSchema.get("key")).$(id);
				id++;
			}
			
			boolean hasTenant = row.containsKey(tenantidKey);
			checkValueTypes(table, row);
			//增加checkValueTypes方法中会被删掉的租户ID
			if(hasTenant) {
				row.put(tenantidKey, idBean.getTenantId());
			}
			newValues.add(row);
		}

		CArray<String> fields = array_keys(CArray.valueOf(reset(newValues)));

		SqlBuilder sqlParts = new SqlBuilder();		
		String sql = createInsertQuery(sqlParts, table, fields, newValues);
		if (!DBexecute(executor, sql, sqlParts.getNamedParams())) {
			throw CDB.exception(DBEXECUTE_ERROR, _s("SQL statement execution has failed \"%1$s\".", sql));
		}

		return resultIds;
	}
	
	/**
	 * Create INSERT SQL query for MySQL, PostgreSQL and IBM DB2.
	 * Creation example:
	 *	INSERT INTO applications (name,hostid,templateid,applicationid)
	 *	VALUES ('CPU','10113','13','868'),('Filesystems','10113','5','869'),('General','10113','21','870');
	 *
	 * @param string _table
	 * @param array _fields
	 * @param array _values
	 *
	 * @return string
	 */
	private static String createInsertQuery(SqlBuilder sqlParts, String table, CArray<String> fields, CArray<Map> values) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(table);
		sql.append(" (");
		sql.append(implode(",", fields));
		sql.append(") VALUES ");
		boolean firstRow = true;
		boolean firstField;
		for (Map row : values) {
			if(firstRow){
				firstRow = false;
			} else {
				sql.append(',');
			}
			sql.append('(');
			firstField = true;
			for (String field : fields) {
				if(firstField){
					firstField = false;
				} else {
					sql.append(',');
				}
				sql.append(sqlParts.marshalParam(Nest.value(row, field).$()));
			}
			sql.append(')');
		}
		return sql.toString();
	}
	
	public boolean update(String table, CArray<Map> data) {
		return update(this.idBean, getSqlExecutor(), table, data);
	}

	public static boolean update(IIdentityBean idBean, SQLExecutor executor, String table, CArray<Map> data) {
		if (empty(data)) {
			return true;
		}
		
		data = Clone.deepcopy(data); //下面的操作会删除不属于当前的表的字段，从而影响后面的保存操作，故做一次clone
		
		Map<String, Object> tableSchema = getSchema(table);
		Map<String, Map<String,Object>> fields = (Map)tableSchema.get("fields");
		for (Map row : data) {
			Map<String, Object> values =(Map)row.get("values");
			checkValueTypes(table, values);
			if(empty(values)){
				throw new RuntimeException(_s("Cannot perform update statement on table \"%1$s\" without values.",table));
			}
			if (!isset(row, "where") || empty(row.get("where"))) {
				throw new RuntimeException(_s("Cannot perform update statement on table \"%1$s\" without where condition.",table));
			}
			
			StringBuilder sql = new StringBuilder();
			Map params = new HashMap();
			int i=1;
			boolean first = true;
			sql.append("UPDATE ").append(table).append(" SET ");
			for (Entry<String, Object> e : values.entrySet()) {
				if (first) {
					first = false;
				} else {
					sql.append(',');
				}
				sql.append(e.getKey()).append("=#{").append("v"+i).append("}");
				params.put("v"+i, e.getValue());
				i++;
			}
			
			Map<String,Object> where = (Map)row.get("where");
			SqlBuilder sqlParts = new SqlBuilder();
			if(!IRadarContext.isIgnoreTenantForSql()) {
				sqlParts.where.put(table+".tenantid", "tenantid="+sqlParts.marshalParam(idBean.getTenantId()));
			}
			for(Entry<String, Object> e:where.entrySet()){
				String field = e.getKey();
				Object vs = e.getValue();
				if(!isset(fields, field) || is_null(vs)){
					throw new RuntimeException(_s("Incorrect field \"%1$s\" name or value in where statement for table \"%2$s\".",field, table));
				}
				vs = toArray(vs);
				int size = Array.getLength(vs);
				String[] ss = new String[size]; 
				for (int n = 0; n < size; n++) {
					ss[n] = Array.get(vs, n).toString();
				}
				Arrays.sort(ss);
				sqlParts.where.dbConditionString(field, ss);
			}
			String sqlWhere = " WHERE " + join(sqlParts.where.arrayUnique()," AND ");
			sql.append(sqlWhere);
			params.putAll(sqlParts.getNamedParams());
			executor.executeInsertDeleteUpdate(sql.toString(), params);
		}
		return true;
	}

	public boolean updateByPk(String tableName, Object pk, Object values) {
		return update(tableName, array((Map)map(
				"where", map(getPk(tableName),pk),
				"values", values
			)));
	}

	protected CArray<Map> save(String _tableName, CArray<Map> _data) {
		String _pk = getPk(_tableName);
		CArray _newRecords = array();
		for (Entry<Object, Map> entry: _data.entrySet()) {
			Object _key = entry.getKey();
			Map _record = entry.getValue();
			// if the pk is set - update the record
			if (isset(_record, _pk)) {
				this.updateByPk(_tableName, _record.get(_pk), _record);
			}
			// if no pk is set, create the record later
			else {
				_newRecords.put(_key, _record);
			}
		}
		
		// insert the new records
		if (!empty(_newRecords)) {
			CArray<Long> _newIds = this.insert(_tableName, _newRecords, true);
			for(Entry<Object, Long> entry: _newIds.entrySet()) {
				Object _key = entry.getKey();
				Long _id = entry.getValue();
				_data.put(_key, _pk, _id);
			}
		}
		return _data;
	}

	/**
	 * Replaces the records given in _oldRecords with the ones in _newRecords.
	 *
	 * If a record with the same primary key as a new one already exists in the old records, the record is updated
	 * only if they are different. For new records the newly generated PK is added to the result. Old records that are
	 * not present in the new records are deleted.
	 *
	 * All of the records must have the primary key defined.
	 *
	 * @static
	 *
	 * @param _tableName
	 * @param array _oldRecords
	 * @param array _newRecords
	 *
	 * @return array    the new records, that have been passed with the primary keys set for newly inserted records
	 */
	public CArray<Map> replace(String _tableName, CArray<Map> _oldRecords, CArray<Map> _newRecords) {
		_oldRecords = Clone.deepcopy(_oldRecords);
		_newRecords = Clone.deepcopy(_newRecords);
		String _pk = getPk(_tableName);
		_oldRecords = rda_toHash(_oldRecords, _pk);
	
		CArray<Map> _modifiedRecords = array();
		for (Map _record: _newRecords) {
			// if it's a new or modified record - save it later
			Object pv = _record.get(_pk);
			if (!isset(pv) || recordModified(_tableName, _oldRecords.get(pv), _record)) {
				_modifiedRecords.add(_record);
			}
	
			// remove the existing records from the collection, the remaining ones will be deleted
			if(isset(pv)) {
				unset(_oldRecords,pv);
			}
		}
	
		// save modified records
		if (!empty(_modifiedRecords)) {
			this.save(_tableName, _modifiedRecords);
		}
	
		// delete remaining records
		if (!empty(_oldRecords)) {
			this.delete(_tableName, (Map)map(
				_pk, array_keys(_oldRecords).valuesAsLong()
			));
		}
	
		return _modifiedRecords;
	}

	protected boolean recordModified(String tableName,
			Map<String, Object> oldRecord, Map<String, Object> newRecord) {
		for (Entry<String, Object> e : oldRecord.entrySet()) {
			String field = e.getKey();
			Object value = e.getValue();
			if (hasField(tableName, field) && isset(newRecord, field)
					&& !Cphp.equals(newRecord.get(field), value)) {
				return true;
			}
		}
		return false;
	}

	public boolean delete(String table, Map<String, Object> wheres) {
		return delete(this.idBean, getSqlExecutor(), table, wheres, false);
	}
	
	public static boolean delete(IIdentityBean idBean, SQLExecutor executor, String table, Map<String, Object> wheres) {
		return delete(idBean, executor, table, wheres, false);
	}
	
	protected boolean delete(String table, Map<String, Object> wheres, boolean use_or) {
		return delete(this.idBean, getSqlExecutor(), table, wheres, use_or);
	}

	public static boolean delete(IIdentityBean idBean, SQLExecutor executor, String table, Map<String, Object> wheres, boolean use_or) {
		if (empty(wheres)) {
			throw new RuntimeException(_s("Cannot perform delete statement on table \"%1$s\" without where condition.", table));
		}
		Map<String, Object> tableSchema = getSchema(table);
		Map<String, Map<String,Object>> fields = (Map)tableSchema.get("fields");
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.put(table+".tenantid", "tenantid="+sqlParts.marshalParam(idBean.getTenantId()));
		for (Entry<String, Object> e : wheres.entrySet()) {
			String field = e.getKey();
			Object values = e.getValue();
			if (!isset(fields, field) || values ==null) {
				throw new RuntimeException(_s("Incorrect field \"%1$s\" name or value in where statement for table \"%2$s\".", field, table));
			}
			if (values.getClass().isArray()) {
				if (values instanceof Long[]) {
					Arrays.sort((Long[]) values);
					sqlParts.where.dbConditionLong(field, (Long[]) values);
				} else if (values instanceof long[]) {
					Arrays.sort((long[]) values);
					sqlParts.where.dbConditionLong(field, TArray.as(values).asLong());
				} else if (values instanceof int[]) {
					Arrays.sort((int[]) values);
					sqlParts.where.dbConditionInt(field, TArray.as(values).asLong());
				} else if (values instanceof Integer[]) {
					Arrays.sort((Integer[]) values);
					sqlParts.where.dbConditionInt(field, TArray.as(values).asLong());
				} else if (values instanceof String[]) {
					Arrays.sort((String[]) values);
					sqlParts.where.dbConditionString(field, (String[]) values);
				} else {
					String[] svalues = TArray.as(values).asString();
					Arrays.sort(svalues);
					sqlParts.where.dbConditionString(field, svalues);
				}
			} else {
				if (values instanceof Integer) {
					sqlParts.where.dbConditionInt(field, new Long[]{Nest.as(values).asLong()});
				} else if (values instanceof Long) {
					sqlParts.where.dbConditionLong(field, new Long[]{(Long) values});
				} else if (values instanceof String) {
					sqlParts.where.dbConditionString(field, new String[]{(String) values});
				} else if (values instanceof CArray) {
					Long[] lvalues = ((CArray) values).valuesAsLong();
					Arrays.sort(lvalues);
					sqlParts.where.dbConditionInt(field, lvalues);
				} else {
					sqlParts.where.dbConditionString(field, new String[]{String.valueOf(values)});
				}
			}			
		}
		String sqlWhere = " where " + join(sqlParts.where.arrayUnique(), use_or ? " or ":" and ");
		StringBuilder sql = new StringBuilder();
		sql.append("delete from ").append(table).append(sqlWhere);
		executor.executeInsertDeleteUpdate(sql.toString(), sqlParts.getNamedParams());
		return true;
	}

	protected boolean isNumericFieldType(String type) {
		if (FIELD_TYPE_ID.equals(type) 
				|| FIELD_TYPE_INT.equals(type)
				|| FIELD_TYPE_UINT.equals(type)) {
			return true;
		}
		return false;
	}
}
