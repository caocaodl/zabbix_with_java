package com.isoft.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import com.isoft.biz.util.BizError;
import com.isoft.cache.CacheFactory;
import com.isoft.consts.Constant;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.model.SelectItem;

public class DictUtil {

    @SuppressWarnings("unchecked")
    public static void setDictsByType(String dictType,Map dictCache){
        Cache funCahce = CacheFactory.getCache(Constant.CACHE_DICT_KEY);
        Element element = new Element(dictType,dictCache);
        funCahce.put(element);
    }

    @SuppressWarnings("unchecked")
    public static Map getDictsByType(String dictType){
        Cache dicCahce = null;
        try {
            dicCahce = CacheFactory.getCache(Constant.CACHE_DICT_KEY);
            Element element = dicCahce.get(dictType);
            if(element == null)
                return null;
            return (Map)element.getValue();
        } catch (Exception e) {
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, e,dictType);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void setOrderDictsByType(String dictType,List dictOrder){
        Cache funCahce = CacheFactory.getCache(Constant.CACHE_DICT_KEY);
        Element element = new Element(dictType+"_ORDER",dictOrder);
        funCahce.put(element);
    }
    
    @SuppressWarnings("unchecked")
    public static List getOrderDictsByType(String dictType){
        Cache dicCahce = CacheFactory.getCache(Constant.CACHE_DICT_KEY);
        try {
            Element element = dicCahce.get(dictType+"_ORDER");
            if(element == null)
                return null;
            return (List)element.getValue();
        }catch (Exception e) {
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, e,dictType);
        }
    }

    @SuppressWarnings("unchecked")
    public static String getDictItemValueByKey(String dictType,String key,Locale locale){
        Map dicts = getDictsByType(dictType);
        if(dicts == null){
            throw BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_EHCACHE_ERROR, dictType);
        }
        return (String)((Map)dicts.get(key)).get(locale.toString());
    }

    /**
     * get a list of some a type dictionary
     * @param dicType
     * @return DicCache
     */
    @SuppressWarnings("unchecked")
    public static List getDicSltItemByType(String dicType){
        return getOrderDictsByType(dicType);
    }

    /**
     * get a list of some a type dictionary
     * @param dicType
     * @return DicCache
     */
    @SuppressWarnings("unchecked")
    public static List getDicSltItemByType(String dicType,String excludeKey){
        List rValue = new ArrayList();
        Map temp = getDictsByType(dicType);
        if(temp == null)return null;
        for (Iterator iter = temp.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if(key.equals(excludeKey)) continue;
            String value = (String)temp.get(key);
            SelectItem item = new SelectItem();
            item.setLabel(value);
            item.setDescription(value);
            item.setValue(key);
            rValue.add(item);
        }
        return rValue;
    }

    public static void removeAllDicItem(){
        Cache dicCahce = CacheFactory.getCache(Constant.CACHE_DICT_KEY);
        dicCahce.removeAll();
    }
    
    public static List<String> ExtendDict = new LinkedList<String>();  
    static {
    	ExtendDict.add("TENANT");
    	ExtendDict.add("OS_TENANT");
    	ExtendDict.add("OS_COMPUTE_NODES");
    	ExtendDict.add("OS_VM");
    	ExtendDict.add("OS_VM_TENANT");
    	ExtendDict.add("OS_SECGROUP");
    	ExtendDict.add("OS_FLAVOR");
    	ExtendDict.add("OS_IMAGE");
    	ExtendDict.add("OS_ALL_IMAGE");
    	ExtendDict.add("OS_EXCLUSIVE_HOST");    	
    }
    public static boolean isExtendDict(String key) {
        return ExtendDict.contains(key);
    }

}
