package com.isoft.utils;

import java.util.List;

import javax.sql.DataSource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.isoft.biz.util.BizError;
import com.isoft.cache.CacheFactory;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.web.listener.DataSourceEnum;

public class DataSourceUtil {
    public static final String CACHE_DATASOURCE = "CACHE_DATASOURCE";

    public static DataSource getDataSource(String dsId){
        Cache datasourceCahce = CacheFactory.getCache(CACHE_DATASOURCE);
        Element element = datasourceCahce.get(dsId);
        if(element == null){
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_DATASOURCE_ERROR, dsId);
        }
        return (DataSource)element.getObjectValue();
    }

    public static void setDataSource(String key,DataSource ds){
        Cache datasourceCahce = CacheFactory.getCache(CACHE_DATASOURCE);
        Element element = new Element(key,ds);
        datasourceCahce.put(element);
    }
    
    public static List<?> getAllDataSources(){
        Cache datasourceCahce = CacheFactory.getCache(CACHE_DATASOURCE);
        return datasourceCahce.getKeys();
    }
    
	public static DataSource getDefaultDataSource() {
		return getDataSource(DataSourceEnum.values()[0].getDsName());
	}
}
