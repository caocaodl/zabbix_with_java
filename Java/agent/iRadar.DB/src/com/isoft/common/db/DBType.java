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

package com.isoft.common.db;

import com.isoft.common.Constants;
import com.isoft.common.db.impl.DB2;
import com.isoft.common.db.impl.KingbaseES;
import com.isoft.common.db.impl.MSSql;
import com.isoft.common.db.impl.MySQL;
import com.isoft.common.db.impl.Oracle;
import com.isoft.common.db.impl.Oscar;

public class DBType {
	public enum dbtype {
		kingbaseES(54321,"jdbc:kingbase://%1$s:%2$s/SAMPLES?ClientEncoding=utf-8",KingbaseES.class,Constants.KINGBASEES_DRIVER),
		oscar (2003,"jdbc:oscar://%1$s:%2$s/OSRDB",Oscar.class,Constants.OSCAR_DRIVER),
		dm    (0, "", null, null),
		db2   (50000, "jdbc:db2://%1$s:%2$s/SAMPLE", DB2.class, Constants.DB2_DRIVER), 
		pgsql (0, "", null, null), 
		mssql (1433, "jdbc:sqlserver://%1$s:%2$s;databaseName=master", MSSql.class, Constants.MSSQL_DRIVER), 
		mysql (3306, "jdbc:mysql://%1$s:%2$s/mysql", MySQL.class, Constants.MYSQL_DRIVER), 
		oracle(1521, "jdbc:oracle:thin:@%1$s:%2$s:ORCL", Oracle.class, Constants.ORACLE_DRIVER),
		sqlany(0, "", null, null),
		sybase(0, "", null, null), 
		allbase(0, "", null, null), 
		undefined(0, "", null, null)
		;
		
		private String url;
		private int defaultPort;
		private Class cmdClz;
		private String driverClass;
		private dbtype(int defaultPort, String url, Class cmdClz, String driverClass) {
			this.defaultPort = defaultPort;
			this.url = url;
			this.cmdClz = cmdClz;
			this.driverClass = driverClass;
		}
		
		public int defaultPort() {
			return this.defaultPort;
		}
		public String url(String ip, int port, String database) {
			return String.format(this.url, ip, port, database);
		}
		public Class cmdClz() {
			return this.cmdClz;
		}
		public String driverClass() {
			return this.driverClass;
		}
	}

	private dbtype type;

	public DBType(String data) {
		if (data.equalsIgnoreCase("pgsql"))
			this.type = dbtype.pgsql;
		if (data.equalsIgnoreCase("oracle"))
			this.type = dbtype.oracle;
		if (data.equalsIgnoreCase("mssql"))
			this.type = dbtype.mssql;
		if (data.equalsIgnoreCase("mysql"))
			this.type = dbtype.mysql;
		if (data.equalsIgnoreCase("db2"))
			this.type = dbtype.db2;
		if (data.equalsIgnoreCase("sqlany"))
			this.type = dbtype.sqlany;
		if (data.equalsIgnoreCase("allbase"))
			this.type = dbtype.allbase;
		if (data.equalsIgnoreCase("sybase"))
			this.type = dbtype.sybase;
		if (data.equalsIgnoreCase("dm"))
			this.type = dbtype.dm;	
		if (data.equalsIgnoreCase("oscar"))
			this.type = dbtype.oscar;
	}

	public String getDBTypeString() {
		switch (this.type) {
		case oscar:
			return Constants.OSCAR;
		case pgsql:
			return Constants.POSTGRESQL;
		case oracle:
			return Constants.ORACLE;
		case mssql:
			return Constants.MSSQL;
		case mysql:
			return Constants.MYSQL;
		case db2:
			return Constants.DB2;
		case sqlany:
			return Constants.SQLANY;
		case sybase:
			return Constants.SYBASE;
		case dm:
			return Constants.DM;			
		case allbase:
			return Constants.ALLBASE;
		default:
			break;
		}
		return null;
	}

	public String getJDBCDriverClass() {
		switch (this.type) {
		case oscar:
			return Constants.OSCAR_DRIVER;
		case pgsql:
			return Constants.POSTGRESQL_DRIVER;
		case oracle:
			return Constants.ORACLE_DRIVER;
		case mssql:
			return Constants.MSSQL_DRIVER;
		case mysql:
			return Constants.MYSQL_DRIVER;
		case db2:
			return Constants.DB2_DRIVER;
		case sqlany:
			return Constants.SQLANY_DRIVER;
		case sybase:
			return Constants.SYBASE_DRIVER;
		case dm:
			return Constants.DM_DRIVER;			
		case allbase:
			return Constants.ALLBASE_DRIVER;
		default:
			break;
		}
		return null;
	}

	public String getValidationQuery() {
		switch (this.type) {
		case oscar:
			return Constants.OSCAR_VALIDATION_QUERY;
		case pgsql:
			return Constants.POSTGRESQL_VALIDATION_QUERY;
		case oracle:
			return Constants.ORACLE_VALIDATION_QUERY;
		case mssql:
			return Constants.MSSQL_VALIDATION_QUERY;
		case mysql:
			return Constants.MYSQL_VALIDATION_QUERY;
		case db2:
			return Constants.DB2_VALIDATION_QUERY;
		case sqlany:
			return Constants.SQLANY_VALIDATION_QUERY;
		case sybase:
			return Constants.SYBASE_VALIDATION_QUERY;
		case dm:
			return Constants.DM_VALIDATION_QUERY;			
		case allbase:
			return Constants.ALLBASE_VALIDATION_QUERY;
		default:
			break;
		}
		return null;
	}

	public String getWhoAmIQuery() {
		switch (this.type) {
		case oscar:
			return Constants.OSCAR_WHOAMI_QUERY;
		case pgsql:
			return Constants.POSTGRESQL_WHOAMI_QUERY;
		case oracle:
			return Constants.ORACLE_WHOAMI_QUERY;
		case mssql:
			return Constants.MSSQL_WHOAMI_QUERY;
		case mysql:
			return Constants.MYSQL_WHOAMI_QUERY;
		case db2:
			return Constants.DB2_WHOAMI_QUERY;
		case sqlany:
			return Constants.SQLANY_WHOAMI_QUERY;
		case sybase:
			return Constants.SYBASE_WHOAMI_QUERY;
		case dm:
			return Constants.DM_WHOAMI_QUERY;			
		case allbase:
			return Constants.ALLBASE_WHOAMI_QUERY;
		default:
			break;
		}
		return null;
	}

	public String getDbNameQuery() {
		switch (this.type) {
		case oscar:
			return Constants.OSCAR_DBNAME_QUERY;
		case pgsql:
			return Constants.POSTGRESQL_DBNAME_QUERY;
		case oracle:
			return Constants.ORACLE_DBNAME_QUERY;
		case mssql:
			return Constants.MSSQL_DBNAME_QUERY;
		case mysql:
			return Constants.MYSQL_DBNAME_QUERY;
		case db2:
			return Constants.DB2_DBNAME_QUERY;
		case sqlany:
			return Constants.SQLANY_DBNAME_QUERY;
		case sybase:
			return Constants.SYBASE_DBNAME_QUERY;
		case dm:
			return Constants.DM_DBNAME_QUERY;			
		case allbase:
			return Constants.ALLBASE_DBNAME_QUERY;
		default:
			break;
		}
		return null;
	}

}
