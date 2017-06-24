package com.isoft.biz.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.isoft.biz.dao.DB;
import com.isoft.biz.dao.IDGenerator;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.framework.tools.DBTools;

public class DBIDGenerator implements IDGenerator {

	private static final long serialVersionUID = 1L;

	/**
     * The default number of cached id
     */
    private static final int DEFAULT_INCREMENT_BY = 100;
    
    /*
DROP TABLE IF EXISTS `sys_id`;

CREATE TABLE `sys_id` (
    `idspace`      VARCHAR(30) BINARY NOT NULL,
    `counter`      BIGINT      NOT NULL,
    `increment_by` INT         DEFAULT 100,
    `note` varchar(50) DEFAULT NULL,
    CONSTRAINT `pk_id` PRIMARY KEY (`idspace`)
)ENGINE = InnoDB CHARACTER SET utf8; 
     */
    
    private static final String TABLE_NAME = DB.getFlowcodeTabName();

    /**
     * Base query to get a value
     */
    private static final String SQL_GET_ID = "select counter, increment_by from "+TABLE_NAME+" where idspace=?";

    /**
     * The query to update a value
     */
    private static final String SQL_UPDATE_ID = "update "+TABLE_NAME+" set counter=? where idspace=?";

    /**
     * The query to create a new counter
     */
    private static final String SQL_INSERT_ID = "insert into "+TABLE_NAME+" (idspace, counter, increment_by, note) values (?,?,?,?)";

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
     * The query to read a value
     */
    private String selectQuery   = null;

    /**
     * The DataSource used to access to the db
     */
    private DataSource dataSource;

    /**
     * The incrementBy properties
     */
    public int incrementBy = DEFAULT_INCREMENT_BY;

    /**
     * The name space
     */
    private NameSpaceEnum nameSpace = null;

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
    public DBIDGenerator(DataSource ds, NameSpaceEnum nameSpace) {

        if (nameSpace == null) {
            throw new IllegalArgumentException("The nameSpace must be not null");
        }

        this.nameSpace = nameSpace;

        setIncrementBy(DEFAULT_INCREMENT_BY);

        if (ds == null) {
            throw new IllegalArgumentException("The datasource must be not null");
        }
        dataSource = ds;

    }

    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    public synchronized String next() throws DBIDGeneratorException {
        if (availableIds > 0) {
            availableIds--;
            return String.valueOf(++currentValue);
        } else {
            currentValue = read();
        }

        availableIds = incrementBy - 1;

        return String.valueOf(currentValue);
    }

    /**
     * Read a value from the data store. If no idSpace with the same name is
     * found, a new one is created.
     *
     * @return the read value
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private long read() throws DBIDGeneratorException {

        if (!isInitialized) {
            init();
        }

        Connection        conn         = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;
        boolean           isAutoCommit = false;

        long value = 0;

        try {

            conn = dataSource.getConnection();

            if (supportsSelectForUpdate) {
                //
                // Checking the autocommit because using the "select for update"
                // the autocommit must be disable.
                // After the queries the original value is set.
                //
                isAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);

                stmt = conn.prepareStatement(selectQuery);
            } else {
                stmt = conn.prepareStatement(selectQuery);
            }

            stmt.setString(1, nameSpace.name());
            rs = stmt.executeQuery();

            if (!rs.next()) {
                //
                // IDSpace not found. A new one will be created
                //
                DBTools.close(null, stmt, rs);

                stmt = conn.prepareStatement(SQL_INSERT_ID);
                stmt.setString(1, nameSpace.name());
                stmt.setLong(2, 0);
                stmt.setInt(3, incrementBy);
                stmt.setString(4, nameSpace.note());
                stmt.executeUpdate();

                DBTools.close(null, stmt, null);

            } else {
                //
                // IDSpace found
                //
                value = rs.getLong(1);
                setIncrementBy(rs.getInt(2));

                DBTools.close(null, stmt, rs);
            }

            stmt = conn.prepareStatement(SQL_UPDATE_ID);
            stmt.setLong  (1, value + incrementBy);
            stmt.setString(2, nameSpace.name());
            stmt.executeUpdate();

            if (supportsSelectForUpdate) {
                conn.commit();
            }

        } catch (SQLException e) {

            try {
                if (conn != null && supportsSelectForUpdate) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                //
                // Nothing to do
                //
            }

            throw new DBIDGeneratorException("Error reading the counter: " +
                                             nameSpace, e);
        } finally {
            if (conn != null) {
                try {
                    if (supportsSelectForUpdate) {
                        //
                        // Setting the original value for the autocommit
                        //
                        conn.setAutoCommit(isAutoCommit);
                    }
                } catch (SQLException e) {
                    //
                    // Nothing to do
                    //
                }
            }
            DBTools.close(conn, stmt, rs);
        }

        return value;
    }

    /**
     * Initializes the datasource and the queries
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private void init() throws DBIDGeneratorException {
        if (!isInitialized) {
            initQueries();
        }
        isInitialized = true;
    }


    /**
     * Checking the underlying database, initializes the queries to use
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private void initQueries() throws DBIDGeneratorException {
        Connection conn = null;
        try {

            conn = dataSource.getConnection();

            java.sql.DatabaseMetaData dmd = conn.getMetaData();

            supportsSelectForUpdate = dmd.supportsSelectForUpdate();


            if (supportsSelectForUpdate) {
                selectQuery = SQL_GET_ID + " for update";
            } else {
                selectQuery = SQL_GET_ID;
            }


        } catch (SQLException e) {
            throw new DBIDGeneratorException(
                    "Error checking if the database supports 'select for update'",
                    e);
        } finally {
            DBTools.close(conn, null, null);
        }

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
}
