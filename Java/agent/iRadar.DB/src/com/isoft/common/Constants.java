/*
 * Copyright (C) 2010 Andrea Dalle Vacche.
 * 
 * This file is part of DBforBIX.
 *
 * DBforBIX is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * DBforBIX is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * DBforBIX. If not, see <http://www.gnu.org/licenses/>.
 */

package com.isoft.common;

public class Constants {
	public static final String VERSION = "Version 0.6.1";
	public static final String BANNER = Constants.PROJECT_NAME + " " + VERSION;
	public static final String PROJECT_NAME = "DBforBIX";
	public static final String ITEM_PREFIX = PROJECT_NAME+".";
//	public static final String PROPS_PATH = "/resource/%1$squery.props";
	public static final String PROPS_PATH = "/%1$squery.props";
	public static final String DATABASES_LIST = "DatabaseList";
	public static final String DATABASES_TYPE = "DatabaseType";
	public static final String DELIMITER = ",";
	public static final String QUERY_LIST = "QueryList";
	public static final String QUERY_LIST_FILE = "QueryListFile";
	public static final String EXTRA_QUERY_LIST_FILE = "ExtraQueryListFile";
	public static final String QUERY_POSTFIX = "Query";
	public static final String QUERY_NO_DATA_FOUND = "NoDataFound";
	public static final String QUERY_WHEN_NOT_ALIVE = "WhenNotAlive";
	public static final String CONN_URL = "Url";
	public static final String CONN_USERNAME = "User";
	public static final String CONN_PASSWORD = "Password";
	public static final String CONN_DEFAULT_USERNAME = "DefaultUser";
	public static final String CONN_DEFAULT_PASSWORD = "DefaultPassword";
	public static final String CONN_MAX_ACTIVE = "MaxActive";
	public static final String CONN_MAX_IDLE = "MaxIdle";
	public static final String CONN_MAX_WAIT = "MaxWait";
	public static final String TENANTID_PROPERTY = "Tenantid";
	public static final String DEFAULT_TENANTID = "b77cff72790044bcb876ff96f7bab7c2";
	public static final String ORACLE = "Oracle";
	public static final String ORACLE_VALIDATION_QUERY = "SELECT SYSDATE FROM DUAL";
	public static final String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	public static final String ORACLE_WHOAMI_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'SESSION_USER') FROM DUAL";
	public static final String ORACLE_DBNAME_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'DB_NAME') FROM DUAL";
	
	public static final String OSCAR = "Oscar";
	public static final String OSCAR_DRIVER = "com.oscar.Driver";
	public static final String OSCAR_VALIDATION_QUERY = "SELECT SYSDATE FROM DUAL";
	public static final String OSCAR_WHOAMI_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'SESSION_USER') FROM DUAL";
	public static final String OSCAR_DBNAME_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'DB_NAME') FROM DUAL";
	
	public static final String KINGBASEES = "KingbaseES";
	public static final String KINGBASEES_DRIVER = "com.kingbase.Driver";//未完成
	public static final String KINGBASEES_VALIDATION_QUERY = "SELECT SYSDATE FROM DUAL";
	public static final String KINGBASEES_WHOAMI_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'SESSION_USER') FROM DUAL";
	public static final String KINGBASEES_DBNAME_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'DB_NAME') FROM DUAL";
	
	public static final String MYSQL = "MySQL";
	public static final String MYSQL_VALIDATION_QUERY = "SELECT 1 FROM DUAL";
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String MYSQL_WHOAMI_QUERY = "SELECT USER()";
	public static final String MYSQL_DBNAME_QUERY = "SELECT DATABASE()";
	
	public static final String POSTGRESQL     			 = "PostgreSQL";
	public static final String POSTGRESQL_VALIDATION_QUERY = "SELECT 1";
	public static final String POSTGRESQL_WHOAMI_QUERY	 = "SELECT CURRENT_USER";
	public static final String POSTGRESQL_DBNAME_QUERY	 = "SELECT CURRENT_DATABASE()";
	public static final String POSTGRESQL_DRIVER     	     = "org.postgresql.Driver";
	
	public static final String DB2     			= "DB2";
	public static final String DB2_VALIDATION_QUERY = "SELECT 1 FROM SYSIBM.DUAL";
	public static final String DB2_WHOAMI_QUERY	= "SELECT CURRENT USER FROM SYSIBM.SYSDUMMY1";
	public static final String DB2_DBNAME_QUERY	= "SELECT CURRENT SERVER FROM SYSIBM.SYSDUMMY1";
	public static final String DB2_DRIVER     	= "com.ibm.db2.jcc.DB2Driver";
	
	
	public static final String MSSQL     			= "MSSQL";
	public static final String MSSQL_VALIDATION_QUERY = "SELECT GETDATE()";
	public static final String MSSQL_WHOAMI_QUERY	= "SELECT CURRENT_USER";
	public static final String MSSQL_DBNAME_QUERY	= "SELECT DB_NAME()";
	public static final String MSSQL_DRIVER     	= "com.microsoft.sqlserver.jdbc.SQLServerDriver";

	public static final String SQLANY     			= "SQLANY";
	public static final String SQLANY_VALIDATION_QUERY = "SELECT 1";
	public static final String SQLANY_WHOAMI_QUERY	= "SELECT CURRENT_USER";
	public static final String SQLANY_DBNAME_QUERY	= "SELECT db_name()";
	public static final String SQLANY_DRIVER     	= "com.sybase.jdbc4.jdbc.SybDriver";
	
	public static final String SYBASE     			= "SYBASE";
	public static final String SYBASE_VALIDATION_QUERY = "SELECT 1";
	public static final String SYBASE_WHOAMI_QUERY	= "SELECT suser_name()";
	public static final String SYBASE_DBNAME_QUERY	= "SELECT db_name()";
	public static final String SYBASE_DRIVER     	= "net.sourceforge.jtds.jdbc.Driver";

	public static final String ALLBASE     			= "ALLBASE";
	public static final String ALLBASE_VALIDATION_QUERY = "SELECT 1 FROM MGR@GUSSNM.DUMMY";
	public static final String ALLBASE_WHOAMI_QUERY	= "SELECT 'User' FROM MGR@GUSSNM.DUMMY";
	public static final String ALLBASE_DBNAME_QUERY	= "SELECT 'DBName' FROM MGR@GUSSNM.DUMMY";
	public static final String ALLBASE_DRIVER     	= "com.mbf.jdbc.MBFDriver";

	public static final String DM = "DM";
	public static final String DM_VALIDATION_QUERY = "SELECT SYSDATE FROM DUAL";
	public static final String DM_DRIVER = "dm.jdbc.driver.DmDriver";
	public static final String DM_WHOAMI_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'SESSION_USER') FROM DUAL";
	public static final String DM_DBNAME_QUERY = "SELECT SYS_CONTEXT ('USERENV', 'DB_NAME') FROM DUAL";
	
	public static final String RACE_CONDITION_QUERY = "RaceConditionQuery";
	public static final String RACE_CONDITION_EXCLUDE_COLUMNS = "RaceConditionQueryExcludeColumnsList";
	public static final String RACE_CONDITION_VALUE = "RaceConditionValue";
	public static final String QUERY_PERIOD = "Period";
	public static final String QUERY_DEFAULT_PERIOD = "DefaultQueryPeriod";
	public static final String QUERY_ACTIVE = "Active";
	public static final String IRADAR_SERVER_LIST = "iRadarServerList";
	public static final String IRADAR_SERVER_PORT = "Port";
	public static final String IRADAR_SERVER_HOST = "Address";
	public static final String DBFORBIX_PIDFILE = "DBforBIX.PidFile";
	public static final String DBFORBIX_DAEMON_SLEEP = "DBforBIX.Sleep";
	public static final String DBFORBIX_DAEMON_THREAD = "DBforBIX.MaxThreadNumber";
	public static final int IRADAR_SERVER_DEFAULT_PORT = 10051;
	public static final String QUERY_TRIM = "Trim";
	public static final String QUERY_SPACE = "AddSpaces";
	public static final String QUERY_EXCLUDE_COLUMNS = "ExcludeColumnsList";
	
	

}