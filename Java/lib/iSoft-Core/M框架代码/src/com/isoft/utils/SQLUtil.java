package com.isoft.utils;

import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.isoft.cache.CacheFactory;

public class SQLUtil {
	public static boolean devMode = false;
    public static final String CACHE_SQL_LEY = "CACHE_SQL";
    /**
     * 获得缓存的sql模板
     * @param packagePath
     * @param key
     * @return
     */
    public static String getSqlTemplate(String packagePath,String key){
    	if(devMode){
    		return null;
    	}
        Cache sqlCahce = CacheFactory.getCache(CACHE_SQL_LEY);
        Element element = sqlCahce.get(packagePath+key);
        if(element == null)
            return null;
        else
            return (String)element.getValue();
    }
    @SuppressWarnings("unchecked")
    public static Map getSqlVOTemplate(String packagePath,String key){
        Cache sqlCahce = CacheFactory.getCache(CACHE_SQL_LEY);
        Element element = sqlCahce.get(packagePath+key+"_VO");
        if(element == null)
            return null;
        else
            return (Map)element.getValue();
    }

    /**
     * 更新sql的缓存
     * @param packagePath
     * @param key
     * @param sql
     */
    public static void setSqlTemplate(String packagePath,String key,String sql){
    	if(devMode){
    		return;
    	}
        Cache sqlCahce = CacheFactory.getCache(CACHE_SQL_LEY);
        Element element = new Element(packagePath+key,sql);
        sqlCahce.put(element);
    }

    @SuppressWarnings("unchecked")
    public static void setSqlVOTemplate(String packagePath,String key,Map vo){
        Cache sqlCahce = CacheFactory.getCache(CACHE_SQL_LEY);
        Element element = new Element(packagePath+key+"_VO",vo);
        sqlCahce.put(element);
    }

    /**
     * 清楚所有sql模板
     *
     */
    public static void removeAllSqlTemplate(){
        Cache sqlCahce = CacheFactory.getCache(CACHE_SQL_LEY);
        sqlCahce.removeAll();
    }
}
