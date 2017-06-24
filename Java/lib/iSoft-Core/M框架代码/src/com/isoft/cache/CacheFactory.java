package com.isoft.cache;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;

public final class CacheFactory {
    private static CacheManager manager = null;

    public static final String CACHE_CONFIG = "ehcache.xml";
    
    private CacheFactory() {

    }

    /**
     * 创建缓存工厂
     *
     */
    public static void createCacheManager() {
        try {
            URL url = CacheFactory.class.getResource(CACHE_CONFIG);
            manager = CacheManager.create(url);
        } catch (CacheException e) {
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, e);
        }
    }

    /**
     * 创建缓存工厂
     *
     * @param request
     */
    public static void createCacheManager(String conf) {
        if (manager == null) {
            try {
                manager = CacheManager.create(conf);
            } catch (CacheException e) {
                throw BizError.createFrameworkException(
                        ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, conf);
            }
        }
    }

    /**
     * 获得缓存对象
     *
     * @param cacheKey
     * @return
     */
    public static Cache getCache(String cacheKey){
        if(manager == null){
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, cacheKey);
        }
        if (manager.getCache(cacheKey) == null) {
            try {
                manager.addCache(cacheKey);
            } catch (Exception e) {
                throw BizError.createFrameworkException(
                        ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, e, cacheKey);
            }
            return manager.getCache(cacheKey);
        } else {
            return manager.getCache(cacheKey);
        }
    }

    /**
     *
     *关闭缓存
     */
    public static void shutdown(){
        if(manager != null)
            manager = null;
    }
}
