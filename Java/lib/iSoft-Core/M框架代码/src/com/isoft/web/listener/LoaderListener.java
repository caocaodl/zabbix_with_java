package com.isoft.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;

import net.sf.ehcache.Cache;

import com.isoft.biz.util.BizError;
import com.isoft.cache.CacheFactory;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.server.RunParams;

public abstract class LoaderListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent event) {
		ServletContext ctx = event.getServletContext();
		RunParams.TITLE = StringUtils.defaultString(ctx.getInitParameter("release.title"),	RunParams.TITLE);
		RunParams.RELEASE_VERSION = StringUtils.defaultString(ctx.getInitParameter("release.version"),	RunParams.RELEASE_VERSION);
		RunParams.DEBUG = Boolean.valueOf(ctx.getInitParameter("release.debug"));
		RunParams.DEBUG = true;
		try {
			doLoad(ctx);
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		}
	}
    
    public void contextDestroyed(ServletContextEvent event) {
        doUnload(event.getServletContext());
    }
    
    public void doLoad(ServletContext servletContext){
        try {
            initEhcache(servletContext);
            initExtendsPreload(servletContext);
        } catch (Exception e) {
        	e.printStackTrace(System.err);
            throw BizError.createBizLogicException(ErrorCodeEnum.BIZLOGIC_UNKNOWN, e);
        }
    }
    
    public void doUnload(ServletContext servletContext){
        destroyedExtendsPreload(servletContext);
    }

    protected void initEhcache(ServletContext servletContext) {
        String separator = System.getProperty("file.separator");
        String rootPath = servletContext.getRealPath("");
        String filePath = rootPath + separator + "WEB-INF" + separator
                + CacheFactory.CACHE_CONFIG;
        CacheFactory.createCacheManager(filePath);
    }
    
    protected void destroyedEhcache(ServletContext servletContext){
        String[] cacheKeys = new String[] { "CACHE_SYS_PARAM","CACHE_DATASOURCE",
                "CACHE_DICT", "CACHE_FUNC" };
        for (String ck : cacheKeys) {
            Cache cache = CacheFactory.getCache(ck);
            if (cache != null) {
                cache.removeAll(true);
            }
        }
        CacheFactory.shutdown();
    }
//    
//    protected void initEventListenerCfg(ServletContext servletContext) {
//        if (!ElistenerCfg.loaded) {
//            String rootPath = servletContext.getRealPath("");
//            String elistenerCfgPath = rootPath + File.separator + "WEB-INF"
//                    + File.separator + "config" + File.separator
//                    + "elisteners.xml";
//            ConfResolvor config = new ConfResolvor();
//            config.resolve(new ElistenerCfg(), elistenerCfgPath);
//            ElistenerCfg.loaded = true;
//        }
//    }
//    
//    protected void destroyedEventListenerCfg(ServletContext servletContext) {
//        ElistenerCfg.shutdown();
//    }

    protected abstract void initExtendsPreload(ServletContext servletContext);
    protected abstract void destroyedExtendsPreload(ServletContext servletContext);
}
