package com.isoft.iradar;

import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.utils.DataSourceUtil.getDataSource;
import static com.isoft.web.listener.DataSourceEnum.IRADAR;
import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.isoft.biz.dao.IDGenerator;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.daoimpl.IDGeneratorException;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.framework.tools.DBTools;
import com.isoft.types.Mapper.Nest;

public class RadarIDGenerator implements IDGenerator<Long> {

	private static final long serialVersionUID = 1L;

    private static final int DEFAULT_INCREMENT_BY = 1;
    
    private static Map<String,Integer> incrementSteps = new HashMap();
    
	static {
		incrementSteps.put(getGeneratorKey("auditlog", "auditid"), 1000);
		incrementSteps.put(getGeneratorKey("auditlog_details", "auditdetailid"), 1000);
		incrementSteps.put(getGeneratorKey("housekeeper", "housekeeperid"), 1000);
		incrementSteps.put(getGeneratorKey("profiles", "profileid"), 100);
	}
    
    private static Map<String, RadarIDGenerator> cache = new HashMap();
    
    public static RadarIDGenerator getIDGenerator(String table, String field){
    	String key = getGeneratorKey(table, field);
    	RadarIDGenerator idg = cache.get(key);
		if (idg == null) {
			synchronized (cache) {
				if(!cache.containsKey(key)){
					cache.put(key, new RadarIDGenerator(table, field));
				}
				idg = cache.get(key);
			}
		}
    	return idg;
    }
    
    private RadarIDGenerator(String table, String field) {
		this.table = table;
		this.field = field;
	}
    
    private static String getGeneratorKey(String table, String field){
		return format("%s-%s", table, field);
    }    
    
    private String table;
    private String field;
    
    /**
     * How many values are available without to perform a new db access ?
     */
    private int availableIds = 0;

    /**
     * The last value returned
     */
    private long currentValue = 0;
    
    /**
     * Does the underlying database support <CODE>SELECT FOR UPDATE</CODE> ?
     */
    private boolean supportsSelectForUpdate = false;

    /**
     * The incrementBy properties
     */
	public int incrementBy;

    /**
     * Is this instance already initialized ? This parameter is used in order to
     * initialized the instance the first time. We could initialize the instance in
     * the constructor, but in this case, the constructor should throw a
     * DBIDGeneratorException that complicates the use becase a static instance
     * can not be created easly
     */
    private boolean isInitialized = false;

	/**
     * Create a new DBIDGenerator using the given DataSource. The increment value
     * is set to <code>DEFAULT_CACHE_SIZE</code>
     * @param nameSpace the name space (counter name)
     * @param ds the datasource to use
     */
    @Override
    @Deprecated
    public void init(DataSource ds, NameSpaceEnum nameSpace) {
        setIncrementBy(DEFAULT_INCREMENT_BY);
    }
    
    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    public synchronized Long next(int count) throws IDGeneratorException {
    	return next(null, count);
    }
    
    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    public synchronized Long next(SQLExecutor executor, int count) throws IDGeneratorException {
    	count--;
		Long id = next(executor);
		while ((count--) > 0) {
			next(executor);
		}
		return id;
    }

    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    @Override
    public synchronized Long next() throws IDGeneratorException {
        return next(null);
    }
    
    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    public synchronized Long next(SQLExecutor executor) throws IDGeneratorException {
        if (availableIds > 0) {
            availableIds--;
            return ++currentValue;
        } else {
            currentValue = executor==null? read(): read(executor);
        }

        availableIds = incrementBy - 1;
        return currentValue;
    }

    private long read() throws IDGeneratorException {
    	if (!isInitialized) {
            init();
        }

		Connection conn = null;
		ResultSet rs = null;
		boolean isAutoCommit = false;
        long value = 0;

        try {
            conn = getDataSource(IRADAR.getDsName()).getConnection();
            
            DatabaseMetaData dmd = conn.getMetaData();
            this.supportsSelectForUpdate = dmd.supportsSelectForUpdate();
            
            if (supportsSelectForUpdate) {
                isAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
            }

            IIdentityBean idBean = new IdentityBean();
			SQLExecutor executor = new SQLExecutor(conn, idBean);
            value = get_dbid(executor, table, field, incrementBy);

            if (supportsSelectForUpdate) {
                conn.commit();
            }

        } catch (SQLException e) {

            try {
                if (conn != null && supportsSelectForUpdate) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
            }

            throw new IDGeneratorException("Error reading the counter: " + this.table+"."+this.field, e);
        } finally {
            if (conn != null) {
                try {
                    if (supportsSelectForUpdate) {
                        conn.setAutoCommit(isAutoCommit);
                    }
                } catch (SQLException e) {
                }
            }
            DBTools.close(conn, null, rs);
        }

        return value;
    }
    
    /**
     * Read a value from the data store. If no idSpace with the same name is
     * found, a new one is created.
     */
    private long read(SQLExecutor executor) throws IDGeneratorException {
        if (!isInitialized) {
            init();
        }

        long value = 0;
        try {
            value = get_dbid(executor, table, field, incrementBy);
        } catch (Exception e) {
            throw new IDGeneratorException("Error reading the counter: " + this.table+"."+this.field, e);
        }
        return value;
    }

    /**
     * Initializes the datasource and the queries
     * @throws com.IDGeneratorException.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private void init() throws IDGeneratorException {
		if (this.incrementBy < 1) {
			String key = getGeneratorKey(table, field);
			if(incrementSteps.containsKey(key)){
				this.incrementBy = incrementSteps.get(key);
			}
			if (this.incrementBy < 1) {
				this.incrementBy = DEFAULT_INCREMENT_BY;
			}
		}
		isInitialized = true;
    }

    /**
     * Sets the incrementBy property checking if it is bigger than 0
     * @param incr the incrementBy value
     */
    private void setIncrementBy(int incr) {
        if (incr < 1) {
            incrementBy = 1;
        } else {
            incrementBy = incr;
        }
    }
    
    private final static String SQL_GET_NEXTID = "SELECT i.nextid FROM ids i WHERE i.table_name=#{tableName} AND i.field_name=#{fieldName} for update";
	private final static String SQL_INSERT_IDS = "INSERT INTO ids (table_name,field_name,nextid) VALUES (#{tableName},#{fieldName},#{id})";
	private final static String SQL_DELETE_IDS = "DELETE FROM ids WHERE table_name=#{tableName} AND field_name=#{fieldName}";
	private final static String SQL_UPDATE_IDS = "UPDATE ids SET nextid=nextid+#{step} WHERE table_name=#{tableName} AND field_name=#{fieldName}";
	private Long get_dbid(SQLExecutor executor, String table, String field, int step) {
		Long nextId = null;
		String sql = null;
		boolean found = false;
		List<Map> datas = null;
		Map params = new HashMap();
		do {
			long min = 0L;
			long max = 9999999999999999L;
			params.put("tableName", table);
			params.put("fieldName", field);
			params.put("step", step);
			params.put("min", min);
			params.put("max", max);
			datas = executor.executeNameParaQuery(SQL_GET_NEXTID, params);
			if (datas.isEmpty()) {
				sql = "SELECT IFNULL(MAX("+field+"),0)+1 AS id FROM "+table+" WHERE "+field+">=#{min} AND "+field+"<=#{max}";
				datas = executor.executeNameParaQuery(sql, params);
				if(datas.isEmpty() || Nest.value(datas.get(0),"id").asLong()==0L){
					params.put("id", min);
					executor.executeInsertDeleteUpdate(SQL_INSERT_IDS, params);
				} else {
					params.put("id", datas.get(0).get("id"));
					executor.executeInsertDeleteUpdate(SQL_INSERT_IDS, params);
				}
			} else {
				Long tnextId = (Long)datas.get(0).get("nextid");
				if (tnextId.longValue() < min || tnextId.longValue() > max) {
					executor.executeInsertDeleteUpdate(SQL_DELETE_IDS, params);
				}
				
				sql = "SELECT IFNULL(MAX("+field+"),0)+1 AS id FROM "+table+" WHERE "+field+">=#{min} AND "+field+"<=#{max}";
				datas = executor.executeNameParaQuery(sql, params);
				if (tnextId.longValue() < Nest.value(datas.get(0),"id").asLong()) {
					executor.executeInsertDeleteUpdate(SQL_DELETE_IDS, params);
				}
				
				executor.executeInsertDeleteUpdate(SQL_UPDATE_IDS, params);
				datas = executor.executeNameParaQuery(SQL_GET_NEXTID, params);
				if(datas.isEmpty() || is_null(datas.get(0).get("nextid"))){
					// should never be here
					continue;
				} else {
					nextId = (Long)datas.get(0).get("nextid");
					if (tnextId.longValue() + step == nextId.longValue()) {
						found = true;
						nextId -= step;
					}
				}
			}
		} while (!found);
		return nextId;
	}
}
