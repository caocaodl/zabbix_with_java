package com.isoft.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


import com.isoft.common.db.Deamon;
import com.isoft.common.db.Query;

public class QueryCache {

	private static Map<String,Query> QUERYCACHE = new HashMap();
	
	private static void addQUERYCACHE(String cmd,Query query) throws Exception{
		QUERYCACHE.put(cmd, query);
	}
	
	public static Query getQuery(String dbType,String cmd) throws Exception{
		if(QUERYCACHE.get(cmd)!=null)
			return QUERYCACHE.get(cmd);
		else{
			Query query = createQuery(getProperties(dbType),cmd);
			if(query!=null)
				addQUERYCACHE(cmd,query);
			return query;
		}
	}
	
	private static Properties getProperties(String dbType){
		Properties props = new Properties();
		try {
			InputStream propsConf = QueryCache.class.getResourceAsStream(String.format(Constants.PROPS_PATH, dbType)); 
			props.load(propsConf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return props;
	}
	
	private static Query createQuery(Properties props,String cmd) throws Exception {
		try {
			String query = "";
			try {
				query = new String(props.getProperty(cmd + "."+ Constants.QUERY_POSTFIX));
			} catch (Exception ex) {
				Deamon.debug("Error while getting " + cmd + "."+ Constants.QUERY_POSTFIX + " " + ex.getMessage());
			}

			String noDataFound = "";
			try {
				noDataFound = new String(props.getProperty(cmd + "."+ Constants.QUERY_NO_DATA_FOUND));
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.QUERY_NO_DATA_FOUND+ " null or not present " + ex.getMessage());
			}
			String whenNotAlive = "";
			try {
				whenNotAlive = new String(props.getProperty(cmd + "."+ Constants.QUERY_WHEN_NOT_ALIVE));
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.QUERY_WHEN_NOT_ALIVE+ " null or not present " + ex.getMessage());
			}
			String raceCondQuery = "";
			try {
				raceCondQuery = new String(props.getProperty(cmd + "."+ Constants.RACE_CONDITION_QUERY));
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.RACE_CONDITION_QUERY+ " null or not present " + ex.getMessage());
			}
			String raceCondValue = "";
			try {
				raceCondValue = new String(props.getProperty(cmd + "."+ Constants.RACE_CONDITION_VALUE));
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.RACE_CONDITION_VALUE+ " null or not present " + ex.getMessage());
			}
			/**
			 * set Period if not defined period =2 min.
			 */
			int period = -1;
			try {
				period = new Integer(props.getProperty(cmd + "."+ Constants.QUERY_PERIOD));
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.QUERY_PERIOD+ " null or not present " + ex.getMessage());
				try {
					period = new Integer(props.getProperty(Constants.QUERY_DEFAULT_PERIOD));
				}catch (Exception ex1) {
					Deamon.debug("Note: " + Constants.QUERY_DEFAULT_PERIOD+ " null or not present using default values 2 min.");
					period = 2;
				}
			}
			Boolean active = true;
			try {
				String active_str = props.getProperty(cmd + "."+ Constants.QUERY_ACTIVE);
				if (active_str != null) {
					if (active_str.equalsIgnoreCase("false"))
						active = false;
				}
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd+ "." + Constants.QUERY_ACTIVE+ " null or not present " + ex.getMessage());
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_ACTIVE+ " null or not present using default values TRUE");
			}

			Boolean trim = true;
			try {
				String trim_str = props.getProperty(cmd + "."+ Constants.QUERY_TRIM);
				if (trim_str != null) {
					if (trim_str.equalsIgnoreCase("false"))
						trim = false;
				}
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_TRIM + " null or not present "+ ex.getMessage());
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_TRIM+ " null or not present using default values TRUE");
			}

			Boolean space = false;
			try {
				String space_str = props.getProperty(cmd + "."+ Constants.QUERY_SPACE);
				if (space_str != null) {
					if (space_str.equalsIgnoreCase("true"))
						space = true;
				}
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_SPACE + " null or not present "+ ex.getMessage());
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_SPACE+ " null or not present using default values TRUE");
			}

			List<Integer> excludeColumns = new ArrayList<Integer>();
			try {
				String excludeColumnsList = new String(props.getProperty(cmd + "."+ Constants.QUERY_EXCLUDE_COLUMNS));
				StringTokenizer st = new StringTokenizer(excludeColumnsList,Constants.DELIMITER);
				while (st.hasMoreTokens()) {
					String token = st.nextToken().toString();
					Integer tmpInteger = new Integer(token);
					excludeColumns.add(tmpInteger);
				}
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd + "."+ Constants.QUERY_EXCLUDE_COLUMNS + " error "+ ex.getMessage());
			}

			List<Integer> raceExcludeColumns = new ArrayList<Integer>();
			try {
				String excludeColumnsList = new String(props.getProperty(cmd + "."+ Constants.RACE_CONDITION_EXCLUDE_COLUMNS));
				StringTokenizer st = new StringTokenizer(excludeColumnsList,Constants.DELIMITER);
				while (st.hasMoreTokens()) {
					String token = st.nextToken().toString();
					Integer tmpInteger = new Integer(token);
					excludeColumns.add(tmpInteger);
				}
			} catch (Exception ex) {
				Deamon.debug("Note: " + cmd + "."+ Constants.RACE_CONDITION_EXCLUDE_COLUMNS + " error "+ ex.getMessage());
			}
			
			if (query == null || query.length() == 0)
				return null;
			Query q = new Query(query, cmd, noDataFound,whenNotAlive , raceCondQuery,raceCondValue, 
					period, active, trim, space, excludeColumns,raceExcludeColumns);
			return q;
		} catch (Exception ex) {
			Deamon.debug("Error on Configurator on getQueryProperties("+ props.toString() + ") " + ex.getMessage());
			return null;
		}
	}
	
}
