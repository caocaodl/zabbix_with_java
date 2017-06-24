package com.isoft.iradar.data.cache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.isoft.cache.CacheFactory;

public class CacheHelper {
	protected final static String CACHE_KEY_HOST_ITEM_DATA = "CACHE_HOST_ITEM_DATA"; 
	
	public static void cacheHostItemData(Long hostid, String itemKey, Object value) {
		Cache cahce = CacheFactory.getCache(CACHE_KEY_HOST_ITEM_DATA);
		
		CacheKeyHostItem key = new CacheKeyHostItem(hostid, itemKey);
		
		Element element = new Element(key, value);
		cahce.put(element);
	}
	
	public static Object getHostItemData(Long hostid, String itemKey){
        Cache cache = CacheFactory.getCache(CACHE_KEY_HOST_ITEM_DATA);
        
        CacheKeyHostItem key = new CacheKeyHostItem(hostid, itemKey);
        Element element = cache.get(key);
        if(element != null) {
        	return element.getValue();
        }
        return null;
    }
}

class CacheKeyHostItem implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long hostid;
	private String item;
	
	public CacheKeyHostItem(Long hostid, String item) {
		this.hostid = hostid;
		this.item = item;
	}

	@Override
	public int hashCode() {
		return this.hostid.hashCode()*10000 + this.item.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CacheKeyHostItem) {
			CacheKeyHostItem that = (CacheKeyHostItem)obj;
			return that.hostid.equals(this.hostid) && that.item.equals(this.item);
		}
		return false;
	}
}
