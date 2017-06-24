package com.isoft.utils;

import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.isoft.cache.CacheFactory;
import com.isoft.consts.Constant;
import com.isoft.model.FuncItem;
import com.isoft.model.PermItem;

public class CacheUtil {
	
	private final static String KEY_NAVFUNC = "NavFunc";
	private final static String KEY_NAVFUNC_LEAF = "NavFuncLeaf";
	private final static String KEY_URI_PERM = "UriPerm";
    
    public static void cacheNavFuncList(List<FuncItem> navFuncList){
        Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        Element element = new Element(KEY_NAVFUNC,navFuncList);
        funcCahce.put(element);
    }
    
    public static List<FuncItem> getNavFuncList(){
        Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        return (List<FuncItem>)(funcCahce.get(KEY_NAVFUNC).getValue());
    }

	public static void cacheNavFuncLeaf(Map<String, FuncItem> funcLeafMap) {
		Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        Element element = new Element(KEY_NAVFUNC_LEAF,funcLeafMap);
        funcCahce.put(element);
	}
	
	public static Map<String, FuncItem> getNavFuncLeafMap(){
        Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        return (Map<String, FuncItem>)(funcCahce.get(KEY_NAVFUNC_LEAF).getValue());
    }
	
	public static FuncItem getNavFuncByFuncId(String funcId){
		return getNavFuncLeafMap().get(funcId);
	}
	
	public static void cacheUriPerm(Map permMap) {
		Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        Element element = new Element(KEY_URI_PERM,permMap);
        funcCahce.put(element);
	}
	
	public static Map getUriPermMap(){
        Cache funcCahce = CacheFactory.getCache(Constant.CACHE_FUNC_KEY);
        return (Map<String, PermItem>)(funcCahce.get(KEY_URI_PERM).getValue());
    }
	
	public static List<PermItem> getPermByViewId(String viewId){
		return (List<PermItem>)getUriPermMap().get(viewId);
	}
}
