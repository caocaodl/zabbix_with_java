package com.isoft.framework.persistlayer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.model.schema.DBField;
import com.isoft.model.schema.DBSchema;
import com.isoft.utils.DebugUtil;
import com.isoft.utils.VelocityUtil;

public abstract class A_SQLExecutor extends DBCompatible {

    protected Connection conn;
    protected IIdentityBean idBean;

    public A_SQLExecutor(Connection connection, IIdentityBean idBean) {
        this.conn = connection;
        this.idBean = idBean;
    }

    @SuppressWarnings("unchecked")
    protected abstract void setupParams(Map paraMap);

    /**
     * Executes the queries other than that is of type select.
     *
     * @param paraNamedSql
     * @param paraMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public int executeInsertDeleteUpdate(String paraNamedSql, Map paraMap) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(paraNamedSql, paraMap);
        String sql = translateParamSQL(paramSql, paraMap, paraList);
        PreparedStatement ps = null;
        int count = 0;

        int i = 1;
        try {
            ps = conn.prepareStatement(sql);
            Object value = null;
            for (Iterator it = paraList.iterator(); it.hasNext();) {
                value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null || value.toString().length()==0) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug(StringUtils.join(new String[] { "Executing the sql:",sql }));
                DebugUtil.debug(BizError.paraToStr(paraList));
            }

            count = ps.executeUpdate();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug(StringUtils.join(new Object[] {"The number of rows afffected :", new Integer(count) }));
            }

        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            try {
                if (ps != null){
                    ps.close();
                }
            } catch (SQLException e) {
                throw BizError.createDAOException(
                        ErrorCodeEnum.DAO_SQL_STATEMENT_CLOSED_FAIL, e);
            }
        }
        paraMap.put("success",count==1?true:false);
        return count;
    }

    /**
     * execute store process
     *
     * @param nameParaSql
     * @param paraMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object executeProcess(String nameParaSql, Map paraMap) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String sql = translateParamSQL(paramSql, paraMap, paraList);
        CallableStatement stm = null;
        try {
            int i = 2;
            Object value = null;
            stm = conn.prepareCall(sql);
            stm.registerOutParameter(1, Types.VARCHAR);
            for (Iterator it = paraList.iterator(); it.hasNext();) {
                value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    stm.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    stm.setNull(i, Types.NULL);
                } else {
                    stm.setObject(i, value);
                }
                i++;
            }
            stm.execute();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing process SQL:\n" + sql);
            }
            return stm.getObject(1);
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            try {
                if (stm != null){
                    stm.close();
                }
            } catch (SQLException e) {
                throw BizError.createDAOException(
                        ErrorCodeEnum.DAO_SQL_STATEMENT_CLOSED_FAIL, e);
            }
        }
    }
    
    private static Map<String,DBSchema> tableSchema = new HashMap<String,DBSchema>();
    private static Object tableSchemaLock = new Object();
    public DBSchema executeSchemaQuery(String tableName){
		synchronized (tableSchemaLock) {
			if (tableSchema.containsKey(tableName)) {
				return tableSchema.get(tableName);
			} else {
				ResultSet rs1 = null;
				ResultSet rs2 = null;
				try {
					DBSchema dbTable = new DBSchema(tableName);
					DatabaseMetaData dbMeta = conn.getMetaData();
					rs1 = dbMeta.getColumns(null, null, tableName, null);
					while (rs1.next()) {
						DBField field = new DBField();
						field.setColumnName(rs1.getString("COLUMN_NAME"));
						field.setDataType(rs1.getInt("DATA_TYPE"));
						field.setTypeName(rs1.getString("TYPE_NAME"));
						field.setColumnSize(rs1.getInt("COLUMN_SIZE"));
						field.setAutoIncrement("YES".equals(rs1
								.getString("IS_AUTOINCREMENT")));
						field.setNullable(rs1.getInt("NULLABLE") > 0);
						dbTable.addField(field);
					}

					rs2 = dbMeta.getPrimaryKeys(null, null, tableName);
					while (rs2.next()) {
						dbTable.addPrimaryKey(rs2.getString("COLUMN_NAME"));
					}
					tableSchema.put(tableName, dbTable);
					return dbTable;
				} catch (SQLException e) {
					throw BizError.convertSQLException(e, "SchemaQuery",null);
				} finally {
					try {
						if (rs1 != null) {
							rs1.close();
						}
					} catch (SQLException ex) {
						if (DebugUtil.isErrorEnabled()) {
							DebugUtil.error(ex);
						}
					}
					try {
						if (rs2 != null) {
							rs2.close();
						}
					} catch (SQLException ex) {
						if (DebugUtil.isErrorEnabled()) {
							DebugUtil.error(ex);
						}
					}
				}
			}
		}
	}
    
	/**
	 * Returns the name of the field that's used as a private key. If the tableName is not given,
	 * the PK field of the given table will be returned.
	 *
	 * @param string tableName;
	 *
	 * @return string
	 */
    public String executeSchemaPkQuery(String tableName){
    	DBSchema schema = executeSchemaQuery(tableName);
    	List<String> keys = schema.getKeys();
		if (keys.size() > 1) {
			throw new BusinessException("Composite private keys are not supported in this API version.");
		}
		if (!keys.isEmpty()) {
			return keys.get(0);
		} else {
			return null;
		}
    }

    /**
     * It returns a list of VO which is of type voClass.
     *
     * @param conn
     *            JDBC conncection
     * @param nameParaSql
     *            sql statement with named parameter enclosed by ${}, the
     *            retrived column name or alias name must have
     *            <p>
     *            corresponding property in the voClass,otherwise,DAO Exception
     *            will be thrown.
     * @param paraMap
     *            named parameter name/value map
     * @param voClass
     * @param orderFields
     *            it can be null.
     * @return
     */
    @SuppressWarnings("unchecked")
    public List executeNameParaQuery(String nameParaSql, Map paraMap,
            Class voClass) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        return executeQuery(newSQL, paraList, voClass);
    }
    
	public List executeNameParaQuery(String nameParaSql, Map paraMap) {
		return executeNameParaQuery(nameParaSql, paraMap, new DbMarshaller());
	}
    
    @SuppressWarnings("unchecked")
    public List executeNameParaQuery(String nameParaSql, Map paraMap,
    		IDbMarshaller dbMarshaller) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        return executeQuery(newSQL, paraList, dbMarshaller);
    }

    /**
     * 报表的查询方法
     *
     * @param nameParaSql
     * @param paraMap
     * @param orderFields
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map> executeNameParaQuery(String nameParaSql, Map paraMap,
            Map propMap) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        return executeQueryForReport(newSQL, paraList, propMap);
    }

    /**
     * 带翻页的报表的查询方法
     *
     * @param dataPage
     * @param nameParaSql
     * @param paraMap
     * @param propMap
     * @param orderFields
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map> executeNameParaQuery(DataPage dataPage, String nameParaSql,
            Map paraMap, Map propMap) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        if(mysql){
        	return executeQueryForReportInMysql(dataPage, newSQL, paraList, propMap);
        } else if(oracle){
        	return executeQueryForReportInOracle(dataPage, newSQL, paraList, propMap);
        } else {
        	throw BizError.createDAOException(ErrorCodeEnum.CODING_METHOD_IMPLEMENT_ERROR);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<?> executeNameParaQuery(DataPage dataPage, String nameParaSql,
            Map paraMap, Class voClass) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        if(mysql){
        	return executeQueryForReportInMysql(dataPage, newSQL, paraList, voClass);
        } else if(oracle){
        	return executeQueryForReportInOracle(dataPage, newSQL, paraList, voClass);
        } else {
        	throw BizError.createDAOException(ErrorCodeEnum.CODING_METHOD_IMPLEMENT_ERROR);
        }
    }
    
	public List<?> executeNameParaQuery(DataPage dataPage, String nameParaSql,
			Map paraMap) {
		return executeNameParaQuery(dataPage, nameParaSql, paraMap,
				new DbMarshaller());
	}
    
    public List<?> executeNameParaQuery(DataPage dataPage, String nameParaSql,
            Map paraMap, IDbMarshaller dbMarshaller) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String newSQL = translateParamSQL(paramSql, paraMap, paraList);
        if(mysql){
        	return executeQueryForReportInMysql(dataPage, newSQL, paraList, dbMarshaller);
        } else if(oracle){
        	return executeQueryForReportInOracle(dataPage, newSQL, paraList, dbMarshaller);
        } else {
        	throw BizError.createDAOException(ErrorCodeEnum.CODING_METHOD_IMPLEMENT_ERROR);
        }
    }

    /*
     * private String buildWhereClauseParaMap(ClauseParaEntry[] clauseParas,Map
     * paraMap){ if(clauseParas==null) return ""; StringBuffer buf=new
     * StringBuffer(); for(int i=0;i<clauseParas.length;i++){
     * if(clauseParas[i].getClause()!=null){ buf.append(" ");
     * buf.append(clauseParas[i].getClause()); }
     * paraMap.put(clauseParas[i].getParaName(),clauseParas[i].getParaValue()); }
     * return buf.toString(); }
     */

    /**
     * 获得所有数据条数
     *
     * @param nameParaSql
     * @param paraMap
     * @param orderFields
     * @return
     */
    @SuppressWarnings("unchecked")
    public int executeNameParaQueryCount(String nameParaSql, Map paraMap) {
        setupParams(paraMap);
        List paraList = new ArrayList();
        String paramSql = VelocityUtil.merge(nameParaSql, paraMap);
        String sql = getCountSql(paramSql);
        String newSQL = translateParamSQL(sql, paraMap, paraList);
        List list = executeQuery(newSQL, paraList, Integer.class);

        int count = 0;
        if (list != null && !list.isEmpty()) {
            count = ((Integer) list.get(0)).intValue();
        }
        return count;
    }

    /**
     * 带翻页的报表查询。
     *
     * @param dataPage
     * @param sql
     * @param paraList
     * @param propMap
     * @return
     */
    private List<Map> executeQueryForReportInOracle(DataPage dataPage, String sql,
            List paraList, Class voClass) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // set the FetchSize
            ps.setFetchSize(300);
            int i = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            /***************************执行查询的开始********************************************/
            long start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }
            /***************************执行查询的结束********************************************/

            /***************************统计总行数的开始********************************************/
            start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countsql = "SELECT COUNT(1) FROM (" + sql + ") SCFS";
            ps1 = conn.prepareStatement(countsql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(j + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int counts = rs1.getInt(1);
            /***************************统计总行数的结束********************************************/

            /***************************游标滚动操作的开始********************************************/
            start = System.currentTimeMillis();
            // rs.last();
            dataPage.setTotalCount(counts);
            // rs.last();
            // dataPage.setRowCount(rs.getRow());
            // rs.beforeFirst();
            // int startPosition = currPage * pageSize + 1;
            int startPosition = dataPage.getStart() + 1;
            // int numOfEntries = (currPage+1)*pageSize >=
            // lastRow?pageSize:(lastRow-currPage * pageSize);
            int numOfEntries = dataPage.getLimit();
            if (startPosition > 1) {
                rs.absolute(startPosition - 1);
            }
            int num = numOfEntries;
            int n = 0;
            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getVOMetaMap(rsetMeta,voClass);
            start = System.currentTimeMillis();
            while (rs.next() && (num == -1 || n++ < num)) {
                list.add(populateMap(rs, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    private List<Map> executeQueryForReportInOracle(DataPage dataPage, String sql,
            List paraList, IDbMarshaller dbMarshaller) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // set the FetchSize
            ps.setFetchSize(300);
            int i = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            /***************************执行查询的开始********************************************/
            long start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }
            /***************************执行查询的结束********************************************/

            /***************************统计总行数的开始********************************************/
            start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countsql = "SELECT COUNT(1) FROM (" + sql + ") SCFS";
            ps1 = conn.prepareStatement(countsql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(j + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int counts = rs1.getInt(1);
            /***************************统计总行数的结束********************************************/

            /***************************游标滚动操作的开始********************************************/
            start = System.currentTimeMillis();
            // rs.last();
            dataPage.setTotalCount(counts);
            // rs.last();
            // dataPage.setRowCount(rs.getRow());
            // rs.beforeFirst();
            // int startPosition = currPage * pageSize + 1;
            int startPosition = dataPage.getStart() + 1;
            // int numOfEntries = (currPage+1)*pageSize >=
            // lastRow?pageSize:(lastRow-currPage * pageSize);
            int numOfEntries = dataPage.getLimit();
            if (startPosition > 1) {
                rs.absolute(startPosition - 1);
            }
            int num = numOfEntries;
            int n = 0;
            start = System.currentTimeMillis();
            while (rs.next() && (num == -1 || n++ < num)) {
                list.add(dbMarshaller.marshal(rs));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    /**
     * 带翻页的报表查询。
     *
     * @param dataPage
     * @param sql
     * @param paraList
     * @param propMap
     * @return
     */
    private List<Map> executeQueryForReportInOracle(DataPage dataPage, String sql,
            List paraList, Map<String, Class<?>> voMetaMap) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // set the FetchSize
            ps.setFetchSize(300);
            int i = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            /***************************执行查询的开始********************************************/
            long start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }
            /***************************执行查询的结束********************************************/

            /***************************统计总行数的开始********************************************/
            start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countsql = "SELECT COUNT(1) FROM (" + sql + ") SCFS";
            ps1 = conn.prepareStatement(countsql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(j + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int counts = rs1.getInt(1);
            /***************************统计总行数的结束********************************************/

            /***************************游标滚动操作的开始********************************************/
            start = System.currentTimeMillis();
            // rs.last();
            dataPage.setTotalCount(counts);
            // rs.last();
            // dataPage.setRowCount(rs.getRow());
            // rs.beforeFirst();
            // int startPosition = currPage * pageSize + 1;
            int startPosition = dataPage.getStart() + 1;
            // int numOfEntries = (currPage+1)*pageSize >=
            // lastRow?pageSize:(lastRow-currPage * pageSize);
            int numOfEntries = dataPage.getLimit();
            if (startPosition > 1) {
                rs.absolute(startPosition - 1);
            }
            int num = numOfEntries;
            int n = 0;
            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getRowMetaMap(rsetMeta,voMetaMap);
            start = System.currentTimeMillis();
            while (rs.next() && (num == -1 || n++ < num)) {
                list.add(populateMap(rs, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private List<Map> executeQueryForReportInMysql(DataPage dataPage, String sql,
            List paraList, Map<String, Class<?>> voMetaMap) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            /***************************统计总行数的开始********************************************/
            long start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") _SCFS";
            ps1 = conn.prepareStatement(countSql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(j + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + countSql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int totalCount = rs1.getInt(1);
            dataPage.setTotalCount(totalCount);
            /***************************统计总行数的结束********************************************/

            /***************************执行查询的开始********************************************/
            String sort = dataPage.getSort();
            if(sort!=null && sort.length()>0 && voMetaMap.containsKey(sort)){
            	if("asc".equals(dataPage.getOrder()) || "desc".equals(dataPage.getOrder())){
					sort = " order by " + sort + " " + dataPage.getOrder();
            	}
            } else {
            	sort = "";
            }
            sql = sql + sort + " limit ?,?";
            ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            int i = 1;
            paraList.add(dataPage.getStart());
            paraList.add(dataPage.getLimit());
            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(i + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            
            start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            start = System.currentTimeMillis();
            
            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getRowMetaMap(rsetMeta,voMetaMap);
            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(populateMap(rs, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private List<Map> executeQueryForReportInMysql(DataPage dataPage, String sql,
            List paraList, IDbMarshaller dbMarshaller) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            /***************************统计总行数的开始********************************************/
            long start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") _SCFS";
            ps1 = conn.prepareStatement(countSql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(j + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + countSql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int totalCount = rs1.getInt(1);
            dataPage.setTotalCount(totalCount);
            /***************************统计总行数的结束********************************************/

            /***************************执行查询的开始********************************************/
            String pageSql = sql +" limit ?,?";
            ps = conn.prepareStatement(pageSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            int i = 1;
            paraList.add(dataPage.getStart());
            paraList.add(dataPage.getLimit());
            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(i + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            
            start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + pageSql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            start = System.currentTimeMillis();
            
            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(dbMarshaller.marshal(rs));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private List<Class> executeQueryForReportInMysql(DataPage dataPage, String sql,
            List paraList, Class voClass) {
        List list = new ArrayList();
        dataPage.setList(list);
        PreparedStatement ps = null, ps1 = null;
        ResultSet rs = null, rs1 = null;
        try {
            /***************************统计总行数的开始********************************************/
            long start = System.currentTimeMillis();
            // ResultSetMetaData rsMeta= rs.getMetaData();
            // long rl=System.currentTimeMillis();
            // int rCounter=0;

            // count record sql
            // count record sql;
            String countSql = "SELECT COUNT(1) FROM (" + sql + ") _SCFS";
            ps1 = conn.prepareStatement(countSql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            int j = 1;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(j + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps1.setTimestamp(j, sqlDate);
                } else if (value == null) {
                    ps1.setNull(j, Types.NULL);
                } else {
                    ps1.setObject(j, value);
                }
                j++;
            }
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + countSql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs1 = ps1.executeQuery();
            rs1.next();
            int totalCount = rs1.getInt(1);
            dataPage.setTotalCount(totalCount);
            /***************************统计总行数的结束********************************************/

            /***************************执行查询的开始********************************************/
            String pageSql = sql +" limit ?,?";
            ps = conn.prepareStatement(pageSql, ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            int i = 1;
            paraList.add(dataPage.getStart());
            paraList.add(dataPage.getLimit());
            for (Iterator it = paraList.iterator(); it.hasNext();) {
                Object value = it.next();
//                if(DebugUtil.isDebugEnabled()){
//                    DebugUtil.debug(i + "." + value + "\n");
//                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            
            start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + pageSql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            start = System.currentTimeMillis();
            
            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getVOMetaMap(rsetMeta, voClass);
            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(populateVO(rs, voClass, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (rs1 != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps1 != null) {
                    ps1.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }

    /**
     * 不带翻页的报表查询。
     *
     * @param sql
     * @param paraList
     * @param propMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Map> executeQueryForReport(String sql, List paraList,
            Map<String, Class<?>> voMetaMap) {
        List<Map> list = new ArrayList<Map>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            // set the FetchSize
            ps.setFetchSize(300);
            int i = 1;
            Object value = null;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }
            /***************************执行查询的开始********************************************/
            long start = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            rs = ps.executeQuery();
            /***************************执行查询的结束********************************************/

            /***************************游标滚动操作的开始********************************************/
            // rs.setFetchSize(1000);
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getRowMetaMap(rsetMeta,voMetaMap);
            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(populateMap(rs, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            // 针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }

    /**
     * 不带翻页的查询方法
     *
     * @param sql
     * @param paraList
     * @param voClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private List executeQuery(String sql, List paraList, Class voClass) {
        List list = new ArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setFetchSize(300);
            int i = 1;
            Object value = null;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }

            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            long start = System.currentTimeMillis();
            rs = ps.executeQuery();
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            ResultSetMetaData rsetMeta = rs.getMetaData();
            Map<String, Class<?>> rowMetaMap = getVOMetaMap(rsetMeta,voClass);
            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(populateVO(rs, voClass, rowMetaMap));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private List executeQuery(String sql, List paraList, IDbMarshaller dbMarshaller) {
        List list = new ArrayList();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setFetchSize(300);
            int i = 1;
            Object value = null;

            for (Iterator it = paraList.iterator(); it.hasNext();) {
                value = it.next();
                if(DebugUtil.isDebugEnabled()){
                    DebugUtil.debug(i + "." + value + "\n");
                }
                if (value instanceof Date) {
                    Date utilDate = (Date) value;
                    Timestamp sqlDate = new Timestamp(utilDate.getTime());
                    ps.setTimestamp(i, sqlDate);
                } else if (value == null) {
                    ps.setNull(i, Types.NULL);
                } else {
                    ps.setObject(i, value);
                }
                i++;
            }

            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("Executing query SQL:\n" + sql);
                DebugUtil.debug(BizError.paraToStr(paraList));
            }
            long start = System.currentTimeMillis();
            rs = ps.executeQuery();
            long end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to execute the query."+end+" "+start);
            }

            start = System.currentTimeMillis();
            while (rs.next()) {
                list.add(dbMarshaller.marshal(rs));
            }
            end = System.currentTimeMillis();
            if(DebugUtil.isDebugEnabled()){
                DebugUtil.debug("It took " + (end - start) + " ms to fetch the data from database.");
                DebugUtil.debug(list.size() + " records found.");
            }
        } catch (SQLException e) {
            throw BizError.convertSQLException(e, sql, paraList);
        } finally {
            //针对查询的操作的关闭异常，忽略。
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                if (DebugUtil.isErrorEnabled()) {
                    DebugUtil.error(ex);
                }
            }
        }
        return list;
    }

    /**
     * 数据行记录转化为Map对象
     */
    private Map<String, Object> populateMap(ResultSet rset,
            Map<String, Class<?>> rowMetaMap) throws SQLException {
        Map<String, Object> voObject = new HashMap<String, Object>();
        String propName = null;
        Class<?> propClass = null;
        Set<Entry<String, Class<?>>> propMetaSet = rowMetaMap.entrySet();
        for (Entry<String, Class<?>> propMeta : propMetaSet) {
            propName = propMeta.getKey();
            propClass = propMeta.getValue();
            if (propClass.equals(String.class)) {
                voObject.put(propName, rset.getString(propName));
            } else if (propClass.equals(Integer.class)) {
                voObject.put(propName, rset.getInt(propName));
            } else if (propClass.equals(BigDecimal.class)) {
                voObject.put(propName, rset.getBigDecimal(propName));
            } else if (propClass.equals(Date.class)) {
                voObject.put(propName, rset.getTimestamp(propName));
            } else if (propClass.equals(Double.class)) {
                voObject.put(propName, new Double(rset.getDouble(propName)));
            } else if (propClass.equals(Float.class)) {
                voObject.put(propName, rset.getFloat(propName));
            } else if (propClass.equals(Long.class)) {
                voObject.put(propName, rset.getLong(propName));
            } else if (propClass.equals(Boolean.class)) {
                voObject.put(propName, rset.getBoolean(propName));
            } else {
                voObject.put(propName, rset.getString(propName));
            }
        }
        return voObject;
    }

    /**
     * 数据行记录转化为Object对象
     */
    private Object populateVO(ResultSet rs, Class<?> voClass,
            Map<String, Class<?>> rowMetaMap) throws SQLException {
        Object voObject = null;
        if (voClass.equals(Integer.class)) {
            voObject = rs.getInt(1);
        } else if (voClass.equals(Long.class)) {
            voObject = rs.getLong(1);
        } else if (voClass.equals(BigInteger.class)) {
            voObject = BigInteger.valueOf(rs.getLong(1));
        } else if (voClass.equals(BigDecimal.class)) {
            voObject = rs.getBigDecimal(1);
        } else if (voClass.equals(String.class)) {
            voObject = rs.getString(1);
        } else if (voClass.equals(Date.class)) {
            voObject = rs.getDate(1);
        } else if (voClass.equals(Timestamp.class)) {
            voObject = rs.getTimestamp(1);
        } else if (voClass.equals(Short.class)) {
            voObject = rs.getShort(1);
        } else if (voClass.equals(Byte.class)) {
            voObject = rs.getByte(1);
        } else if (voClass.equals(Double.class)) {
            voObject = rs.getDouble(1);
        } else if (voClass.equals(Float.class)) {
            voObject = rs.getFloat(1);
        } else if (voClass.equals(Boolean.class)) {
            voObject = rs.getBoolean(1);
        } else {
            try {
                String propName = null;
                String clazName = null;
                Class<?> propClass = null;
                voObject = voClass.newInstance();
                Set<Entry<String, Class<?>>> propMetaSet = rowMetaMap.entrySet();
                for (Entry<String, Class<?>> propMeta : propMetaSet) {
                    propName = propMeta.getKey();
                    propClass = propMeta.getValue();
                    clazName = propClass.getName();
                    if (propClass.equals(Integer.class) || "int".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getInt(propName));
                    } else if (propClass.equals(BigDecimal.class)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getBigDecimal(propName));
                    } else if (propClass.equals(Date.class) || "date".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getTimestamp(propName));
                    } else if (propClass.equals(String.class)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getString(propName));
                    } else if (propClass.equals(Double.class) || "double".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getDouble(propName));
                    } else if (propClass.equals(Float.class) || "float".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getFloat(propName));
                    } else if (propClass.equals(Long.class) || "long".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getLong(propName));
                    } else if (propClass.equals(Boolean.class) || "boolean".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, rs.getBoolean(propName));
                    }  else if (propClass.equals(BigInteger.class) || "bigint".equals(clazName)) {
                        PropertyUtils.setSimpleProperty(voObject, propName, BigInteger.valueOf(rs.getLong(propName)));
                    } else {
                        if (rs.getString(propName) != null){
                            PropertyUtils.setSimpleProperty(voObject, propName, rs.getString(propName));
                        }
                    }
                }
            } catch (InvocationTargetException e) {
                throw BizError.createCodingException(
                        ErrorCodeEnum.CODING_SQL_VO_POPULATED_FAIL, e, voClass
                                .getName());
            } catch (NoSuchMethodException e) {
                throw BizError.createCodingException(
                        ErrorCodeEnum.CODING_SQL_VO_POPULATED_FAIL, e, voClass
                                .getName());
            } catch (IllegalAccessException e) {
                throw BizError.createCodingException(
                        ErrorCodeEnum.CODING_SQL_VO_POPULATED_FAIL, e, voClass
                                .getName());
            } catch (InstantiationException e) {
                throw BizError.createCodingException(
                        ErrorCodeEnum.CODING_SQL_VO_POPULATED_FAIL, e, voClass
                                .getName());
            }
        }
        return voObject;
    }

    /**
     * 解析sql模板（目前是public的接口，上线改为private）
     *
     * @param nameSQL
     * @param paraMap
     * @param paraList
     * @return
     */
    @SuppressWarnings("unchecked")
    public String translateParamSQL(String nameSQL, Map paraMap,
            List paraList) {
        String realSql = null;

        if (nameSQL == null) {
            throw BizError
                    .createCodingException(ErrorCodeEnum.CODING_SQL_NULL_STATEMENT);
        }

        if ((paraMap == null || paraMap.isEmpty())
                && (nameSQL.indexOf("#{") > -1 || nameSQL.indexOf("@{") > -1)) {
            throw BizError
                    .createCodingException(ErrorCodeEnum.CODING_SQL_MISSED_PARAM_BIND);
        }

        if (nameSQL.indexOf("@{") < 0) {
            realSql = translateNamedSQL(nameSQL, paraMap, paraList);
            return realSql;
        }

        if (paraList == null) {
            paraList = new ArrayList();
        }
        StringBuffer buf = new StringBuffer(nameSQL.length() + 30);
        int len = nameSQL.length();
        int i = 0;
        int startIndex = 0;
        int endIndex = 0;
        while (i < len) {
            if ((i + 1) < len && nameSQL.charAt(i) == '@' && nameSQL.charAt(i + 1) == '{') {
                endIndex = i;
                buf.append(nameSQL.substring(startIndex, endIndex));
                StringBuffer paraBuf = new StringBuffer(20);
                i = i + 2;
                while (i < len && nameSQL.charAt(i) != '}') {
                    if (nameSQL.charAt(i) != ' ')
                        paraBuf.append(nameSQL.charAt(i));
                    i++;
                }
                startIndex = i + 1;
                String paraName = paraBuf.toString();
                // if the variable is not closed
                if (i >= len || nameSQL.charAt(i) != '}') {
                    throw BizError.createCodingException(
                            ErrorCodeEnum.CODING_SQL_VAR_NOT_CLOSED, paraName);
                }

                buf.append(getParaValue(paraMap, paraName));
            }
            i++;
            if (i >= len) {
                buf.append(nameSQL.substring(startIndex, nameSQL.length()));
                break;
            }
        }
        realSql = translateParamSQL(buf.toString(), paraMap, paraList);
        return realSql;
    }

    @SuppressWarnings("unchecked")
    private String translateNamedSQL(String nameSQL, Map paraMap, List paraList) {

        if (nameSQL.indexOf("#{") < 0) {
            return nameSQL;
        }

        if (paraList == null) {
            paraList = new ArrayList();
        }

        StringBuilder buf = new StringBuilder(nameSQL.length() + 30);
        int len = nameSQL.length();
        int i = 0;
        Object objv = null;
        while (i < len) {
            if ((i + 1) < len && nameSQL.charAt(i) == '#' && nameSQL.charAt(i + 1) == '{') {
                StringBuffer paraBuf = new StringBuffer(20);
                i = i + 2;
                while (i < len && nameSQL.charAt(i) != '}') {
                    if (nameSQL.charAt(i) != ' '){
                        paraBuf.append(nameSQL.charAt(i));
                    }
                    i++;
                }
                String paraName = paraBuf.toString();

                // if the variable is not closed
                if (i >= len || nameSQL.charAt(i) != '}') {
                    throw BizError.createCodingException(
                            ErrorCodeEnum.CODING_SQL_VAR_NOT_CLOSED, paraName);
                }
                
                objv = getParaValue(paraMap, paraName);
                if(objv != null && objv instanceof List){
                	List datas = (List)objv;
					for (int index = 0; index < datas.size(); index++) {
						paraList.add(datas.get(index));
						if(index > 0){
							buf.append(',');
						}
	                	buf.append('?');
					}
                } else if(objv != null && objv.getClass().isArray()){
                	Object[] datas = (Object[])objv;
					for (int index = 0; index < datas.length; index++) {
						paraList.add(datas[index]);
						if(index > 0){
							buf.append(',');
						}
	                	buf.append('?');
					}
                } else {
                	paraList.add(objv);
                	buf.append('?');
                }
                i++;
            }
            if (i >= len)
                break;
            buf.append(nameSQL.charAt(i));
            i++;
        }
        return buf.toString();
    }

    private String getCountSql(String namedSQL) {
        return StringUtils.join(new String[] { "SELECT COUNT(1) FROM (",namedSQL, ") SCFS" });
    }
    
    private Map<String, Class<?>> getVOMetaMap(ResultSetMetaData rsMeta,
    		Class<?> voClass) throws SQLException {
    	PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(voClass);
        int colCount = rsMeta.getColumnCount();
        Map<String, Object> rowMetaMap = new HashMap<String, Object>(colCount);
        Map<String, Class<?>> voMetaMap = new HashMap<String, Class<?>>(pds.length);
        String propName = null;
        
        for (int i = 0; i < colCount; i++) {
            propName = rsMeta.getColumnLabel(i + 1);
            rowMetaMap.put(propName.toUpperCase(), true);
        }        
        
        for (PropertyDescriptor pd:pds) {
            propName = pd.getName();
            if (propName.equalsIgnoreCase("class")){
                continue;
            }
            if(rowMetaMap.containsKey(propName.toUpperCase())){
            	voMetaMap.put(propName, pd.getPropertyType());
            }
        }
        return voMetaMap;
    }
    
    private Map<String, Class<?>> getRowMetaMap(ResultSetMetaData rsMeta,
            Map<String, Class<?>> voMetaMap) throws SQLException {
    	int colCount = rsMeta.getColumnCount();
        Map<String, Class<?>> rowMetaMap = new HashMap<String, Class<?>>(colCount);
        Map<String, String> tmpMetaMap = new HashMap<String,String>();
        Set<String> propSet = voMetaMap.keySet();
        for(String prop:propSet){
        	tmpMetaMap.put(prop.toUpperCase(), prop);
        }
        String propName = null;
        String voPropName = null;
        Class<?> propClass = null;
        for (int i = 0; i < colCount; i++) {
            propName = rsMeta.getColumnLabel(i + 1);
            voPropName = tmpMetaMap.get(propName.toUpperCase());
            propClass = voMetaMap.get(voPropName);
            if (propClass!=null) {
                rowMetaMap.put(voPropName, propClass);
            }
        }
        return rowMetaMap;
    }

    @SuppressWarnings("unchecked")
    private Object getParaValue(Map map, String name) {
        int dotIndex = name.indexOf('.');

        if (dotIndex < 0){
            return map.get(name);
        }

        String objName = name.substring(0, dotIndex);
        String propName = name.substring(dotIndex + 1);

        Object rootObject = map.get(objName);
        if (rootObject == null){
            return null;
        }

        try {
            return PropertyUtils.getProperty(rootObject, propName);
        } catch (Exception e) {
            throw BizError.createCodingException(
                    ErrorCodeEnum.CODING_SQL_MISSED_PARAM_VALUE, e, propName,
                    objName);
        }
    }
    
	public void packInParams(Map params, String tname, Object[] ids) {
		StringBuilder sql = new StringBuilder();
		if (ids != null && ids.length > 0){
			for (int i = 0; i < ids.length; i++) {
				sql.append(i == 0 ? "" : ",").append("#{" + tname + "_" + i + "}");
				params.put(tname + "_" + i, ids[i]);
			}
		} else {
			sql.append("NULL");
		}
		params.put(tname, sql.toString());
	}
}
