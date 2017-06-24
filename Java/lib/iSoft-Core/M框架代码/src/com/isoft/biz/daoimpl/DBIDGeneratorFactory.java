package com.isoft.biz.daoimpl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.isoft.biz.dao.IDGenerator;
import com.isoft.biz.dao.NameSpaceEnum;

public class DBIDGeneratorFactory {

	private static Map<String, DBIDGenerator> instancesCache = new HashMap<String, DBIDGenerator>();
	private static DataSource dataSource = null;

	public static synchronized void initDataSource(DataSource dataSource) {
		if(DBIDGeneratorFactory.dataSource==null){
			DBIDGeneratorFactory.dataSource = dataSource;
		}
	}

	public static synchronized IDGenerator getDBIDGenerator(NameSpaceEnum nameSpace) {
		DBIDGenerator instance = instancesCache.get(nameSpace.name());
		if (instance == null) {
			instance = new DBIDGenerator(dataSource, nameSpace);
			instancesCache.put(nameSpace.name(), instance);
		}
		return instance;
	}
}
