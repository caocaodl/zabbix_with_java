package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.RadarIDGenerator.getIDGenerator;
import static com.isoft.iradar.inc.Defines.RDA_DB_DB2;
import static com.isoft.iradar.inc.Defines.RDA_DB_MYSQL;
import static com.isoft.iradar.inc.Defines.RDA_DB_ORACLE;
import static com.isoft.iradar.inc.Defines.RDA_DB_POSTGRESQL;
import static com.isoft.iradar.inc.Defines.RDA_DB_SQLITE3;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.biz.daoimpl.radar.CCoreLongKeyDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class DBUtil {
	/**
	 * Checks whether all _db_fields keys exists as _args keys.
	 *
	 * If _db_fields element value is given and corresponding _args is not then it is assigned to _args element.
	 *
	 * @param _dbFields
	 * @param _args
	 *
	 * @return bool
	 */
	public static boolean check_db_fields(Map<Object, Object> dbFields, Map args) {
		for (Entry<Object, Object> entry : dbFields.entrySet()) {
			Object field = entry.getKey();
			Object def = entry.getValue();
			if (!isset(args.get(field))) {
				if (is_null(def)) {
					return false;
				} else {
					args.put(field, def);
				}
			}
		}
		return true;
	}
	
	public static boolean DBstart(SQLExecutor executor){
		return executor.DBstart();
	}
	
	/**
	 * Closes transaction.
	 * @return bool True - successful commit, False - otherwise
	 */
	public static boolean DBend(SQLExecutor executor){
		return executor.DBend(true);
	}
	
	/**
	 * Closes transaction.
	 * @return bool True - successful commit, False - otherwise
	 */
	public static boolean DBend(SQLExecutor executor, boolean doCommit){
		return executor.DBend(doCommit);
	}
	
	public static Long get_dbid(IIdentityBean idBean, SQLExecutor executor, String table, String field) {
		return get_dbid(idBean, executor, table, field, 1);
	}
	
	public static Long get_dbid(IIdentityBean idBean, SQLExecutor executor, String table, String field, int count) {
		return getIDGenerator(table, field).next(Feature.idsUseConnection? null: executor, count);
	}	
	
	public static Long create_id_by_nodeid(IIdentityBean idBean, SQLExecutor executor, Long id) {
		if (id == 0L) {
			return 0L;
		}
		return id;
	}
	
	/**
	 * Returns true if both IDs are equal.
	 *
	 * @param _id1
	 * @param _id2
	 *
	 * @return bool
	 */
	public static boolean idcmp(Object _id1, Object _id2) {
		return String.valueOf(_id1).equals(String.valueOf(_id2));
	}
	
	public static Map DBfetch(Iterable<Map> rows) {
		if(rows != null) {
			Iterator<Map> iterator = rows.iterator();
			if(iterator.hasNext()) {
				return iterator.next();
			}
		}
		return null;
		
	}
	
	public static CArray<Map> DBfetchArray(Iterable<Map> rows) {
		return (CArray<Map>)CArray.valueOf(rows);
	}
	
	public static CArray<Map> DBfetchArray(CArray<Map> rows) {
		return rows;
	}
	/**
	 * Transform DB cursor to array.
	 *
	 * @return array
	 */
	public static CArray<Map> DBfetchArrayAssoc(Iterable<Map> rows, Object _field) {
		CArray _result = array();
		Iterator<Map> iterator = rows.iterator();
		while(iterator.hasNext()) {
			Map _row = iterator.next();
			_result.put(_row.get(_field), _row);
		}
		return _result;
	}
	
	/**
	 * Fetch only values from one column to array.
	 *
	 * @param resource _cursor
	 * @param string   _column
	 * @param bool     _asHash
	 *
	 * @return array
	 */
	public static CArray DBfetchColumn(CArray<Map> cursor, String column) {
		return DBfetchColumn(cursor, column, false);
	}
	
	public static CArray DBfetchColumn(CArray<Map> cursor, String column, boolean asHash) {
		CArray result = new CArray();
		for (Map _dbResult : cursor) {
			if (asHash) {
				Nest.value(result,_dbResult.get(column)).$(_dbResult.get(column));
			} else {
				result.add(_dbResult.get(column));
			}
		}
		return result;
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, SqlBuilder sqlParts){
		String sql = CCoreLongKeyDAO.createSelectQueryFromParts(sqlParts);
		Map paraMap = sqlParts.getNamedParams();
		return DBselect(executor, sql, paraMap);
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query) {
		return DBselect(executor, query, null, 0, new HashMap());
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query, Map paraMap) {
		return DBselect(executor, query, null, 0, paraMap);
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query, Integer limit) {
		return DBselect(executor, query, limit, 0, new HashMap());
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query, Integer limit, Map paraMap) {
		return DBselect(executor, query, limit, 0, paraMap);
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query, Integer limit, Integer offset) {
		return DBselect(executor, query, limit, offset, new HashMap());
	}
	
	public static CArray<Map> DBselect(SQLExecutor executor, String query, Integer limit, Integer offset, Map paraMap) {
		if (offset == null) {
			offset = 0;
		}
		// add the LIMIT clause
		if ((query = DBaddLimit(query, limit, offset)) == null || query.length()==0) {
			return null;
		}
		List<Map> datas = executor.executeNameParaQuery(query, paraMap);
		return CArray.valueOf(datas);
	}
	
	public static boolean DBexecute(SQLExecutor executor, String sql) {
		return DBexecute(executor, sql, 0);
	}
	
	public static boolean DBexecute(SQLExecutor executor, String sql, Map params) {
		return DBexecute(executor, sql, params, 0);
	}
	
	public static boolean DBexecute(SQLExecutor executor, String sql, int _skip_error_messages) {
		int i = executor.executeInsertDeleteUpdate(sql, new HashMap(0));
		return i != 0;
	}
	
	public static boolean DBexecute(SQLExecutor executor, String sql, Map params, int _skip_error_messages) {
		int i = executor.executeInsertDeleteUpdate(sql, params);
		return i != 0;
	}
	
	public static String DBaddLimit(String query) {
		return DBaddLimit(query, 0);
	}
	
	public static String DBaddLimit(String query, int limit) {
		return DBaddLimit(query, limit, 0);
	}

	public static String DBaddLimit(String query, Integer limit, Integer offset) {
		if (isset(limit) && (limit < 0 || offset < 0)) {
			return query;
		}
		if (isset(limit)) {
			query += " LIMIT " + limit + " OFFSET " + offset;
		}
		return query;
	}
	
	/**
	 * Escape string for safe usage in SQL queries.
	 * Works for ibmdb2, mysql, oracle, postgresql, sqlite.
	 *
	 * @param array|string _var
	 *
	 * @return array|bool|string
	 */
	private final static CArray DB = array();
	static {
		DB.put("TYPE", RDA_DB_MYSQL);
//		DB.put("DB", 1);
	}

	public static String getDBType() {
		return Nest.value(DB, "type").asString();
	}
	
	/**
	 * Creates db dependent string with sql expression that casts passed value to bigint.
	 * Works for ibmdb2, mysql, oracle, postgresql, sqlite.
	 *
	 * @param int _field
	 *
	 * @return bool|string
	 */
	public static String rda_dbcast_2bigint(String _field) {
		if (!isset(DB.get("TYPE"))) {
			return null;
		}
		
		Object type = DB.get("TYPE");
		if(RDA_DB_DB2.equals(type) || RDA_DB_POSTGRESQL.equals(type) || RDA_DB_SQLITE3.equals(type)) {
			return "CAST("+_field+" AS BIGINT)";
		}
		if(RDA_DB_MYSQL.equals(type)) {
			return "CAST("+_field+" AS UNSIGNED)";
		}
		if(RDA_DB_ORACLE.equals(type)) {
			return "CAST("+_field+" AS NUMBER(20))";
		}

		return null;
	}
	
	/**
	 * @param x
	 * @param y Object 将被转化为字符串
	 * @return
	 */
	public static String rda_sql_mod(String x, Object y) {
		Object type = DB.get("TYPE");
		if(RDA_DB_SQLITE3.equals(type)) {
			return " (("+x+") % ("+y+"))";
		}else {
			return " MOD("+x+","+y+")";
		}
	}

}
