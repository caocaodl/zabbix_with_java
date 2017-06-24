package com.isoft.iradar.core.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.isoft.types.CArray;

public abstract class EasyMap {
	
	public final static Map newMap() {
		return null;
	}
	public final static Map newMap(int i) {
		return new CArray(i);
	}
	
	public final static boolean isNullOrEmpty(Map m) {
		return m==null || m.isEmpty();
	}
	
	public static Object get(Object m, Object key) {
		return ((Map)m).get(key);
	}
	public static String getString(Map m, Object key) {
		if(m == null) return null;
		Object o = m.get(key);
		if(o == null) return null;
		if(o instanceof String[]) return ((String[])o)[0];
		return String.valueOf(o);
	}
	public static String[] getStringArray(Map m, Object key) {
		if(m == null) return null;
		Object o = m.get(key);
		if(o == null) return null;
		if(o instanceof String[]) return ((String[])o);
		return new String[] {String.valueOf(o)};
	}
	public static Integer getInteger(Map m, Object key) {
		if(m == null) return null;
		Object v = m.get(key);
		if(v == null) return null;
		if(v instanceof Integer) return (Integer)v;
		return Integer.valueOf(String.valueOf(v));
	}
	public static Float getFloat(Map m, Object key) {
		if(m == null) return null;
		Object v = m.get(key);
		if(v == null) return null;
		if(v instanceof Float) return (Float)v;
		return Float.valueOf(String.valueOf(v));
	}
	public static BigDecimal getBigDecimal(Map m, Object key) {
		if(m == null) return null;
		Object v = m.get(key);
		if(v == null) return null;
		if(v instanceof BigDecimal) return (BigDecimal)v;
		if(v instanceof Double) return NumberUtil.valueOf((Double)v);
		if(v instanceof Long) return NumberUtil.valueOf((Long)v);
		return new BigDecimal(String.valueOf(v));
	}
	public static Boolean getBoolean(Map m, Object key) {
		if(m == null) return null;
		Object v = m.get(key);
		if(v == null) return false;
		if(v instanceof Boolean) return (Boolean)v;
		if(v instanceof String) return "Y".equals(v) || Boolean.valueOf(String.valueOf(v));
		return false;
	}
	public static Date getDate(Map m, Object key) {
		if(m == null) return null;
		Object v = m.get(key);
		if(v == null) return null;
		return (Date)v;
	}
	
	
	public static Map remove(Map m, Object... keys) {
		if(m!=null && keys!=null) {
			for(Object key: keys) 
				m.remove(key);
		}
		return m;
	}
	
	public static Map removeExcept(Map m, Object... keys) {
		if(m!=null && keys!=null) {
			Map tmp = newMap(keys.length);
			for(Object key: keys) {
				tmp.put(key, m.get(key));
			}
			m.clear();
			m.putAll(tmp);
			tmp = null;
		}
		return m;
	}
	
	/**
	 * 输入偶数个参数后，单数为KEY，偶数为VALUE，创建HashMap
	 * 
	 * @param args
	 * @return
	 */
	public static Map build(Object... args){
		if(args == null) return null;
		Map map = newMap((args.length+1)/2);
		return puts(map, args);
	}
	
	public static <T extends Map> T puts(T map, Object... args){
		for(int i=0, ilen=args.length; i<ilen; i+=2) {
			Object key = args[i];
			Object value = i<ilen? args[i+1]: null;
			map.put(key, value);
		}
		return map;
	}
	
	public static Map buildUnmodify(Object... args){
		Map m = build(args);
		return Collections.unmodifiableMap(m);
	}
	
	public static Map linkedBuild(Object... args){
		if(args == null) return null;
		
		Map<String, Object> map = new LinkedHashMap<String, Object>((args.length+1)/2);
		
		for(int i=0, ilen=args.length; i<ilen; i+=2) {
			String key = (String)args[i];
			Object value = i<ilen? args[i+1]: null;
			map.put(key, value);
		}
		
		return map;
	}
	
	/**
	 * 给当前map设值
	 * 
	 * @param map
	 * @param args
	 */
	public static Map fill(Map map, Object... args) {
		if(map == null) map = newMap();
		if(args == null) return map;

		Map n = build(args);
		
		return merge(n, map);
	}
	
	/**
	 * 吸取当前MAP的值，与fill方法的区别在于：如果为map空，会创建一个新的
	 * 
	 * @param map
	 * @param args
	 * @return
	 */
	public static Map suck(Map src, Object... keys) {
		return merges(src, newMap(), keys);
	}
	
	/**
	 * 吸取的同时可以改变新的key
	 * 
	 * @param src
	 * @param keys
	 * @return
	 */
	public static Map suckTo(Map src, Object... keys) {
		Map m = newMap();
		
		for(int i=0, ilen=keys.length; i<ilen; i+=2) {
			Object fromKey = keys[i];
			Object toKey = keys[i+1];
			merge(src, m, fromKey, toKey);
		}
		return m;
	}
	
	/**
	 * 复制仅包含对应KEY的MAP
	 * 
	 * @param map
	 * @param args
	 * @return
	 */
	public static Map clone(Map map, Object... keys) {
		return fill(merge(map, newMap()), keys);
	}
	
	/**
	 * 复制去除对应KEY的MAP
	 * 
	 * @param map
	 * @param keys
	 * @return
	 */
	public static Map cloneE(Map map, Object... keys) {
		Map m = merge(map, newMap());
		for(Object key: keys) m.remove(key);
		return m;
	}
	
	
	/**
	 * 判断m对应的key的值是否为null
	 * 
	 * @param m
	 * @param key
	 * @return
	 */
	public static boolean isNullValue(Map m, Object key) {
		return m==null || m.get(key)==null;
	}
	
	/**
	 * 合并source到dest
	 * 
	 * @param source
	 * @param dest
	 * @param key
	 */
	public static Map merge(Map source, Map dest) {
		if(source != null) dest.putAll(source);
		return dest;
	}
	
	/**
	 * 合并source的keys里的key值到dest的key
	 * 
	 * @param source
	 * @param dest
	 * @param key
	 */
	public static Map merges(Map source, Map dest, Object... keys) {
		for(Object key: keys) {
			merge(source, dest, key, key);
		}
		return dest;
	}
	
	/**
	 * 可以设定src key和dest key的merge
	 * 
	 * @param source
	 * @param dest
	 * @param sdkeys
	 * @return
	 */
	public static Map mergesSD(Map source, Map dest, Object... sdkeys) {
		for(int i=0, ilen=sdkeys.length; i<ilen; i+=2) {
			Object skey = sdkeys[i];
			Object dkey = sdkeys[i+1];
			merge(source, dest, skey, dkey);
		}
		return dest;
	}
	
	/**
	 * 合并source的key值到dest的key
	 * 
	 * @param source
	 * @param dest
	 * @param key
	 */
	public static Map merge(Map source, Map dest, Object key) {
		return merge(source, dest, key, key);
	}
	
	/**
	 * 合并source的key值到dest的destKey
	 * 
	 * @param source
	 * @param dest
	 * @param key
	 */
	public static Map merge(Map source, Map dest, Object key, Object destKey) {
		dest.put(destKey, source.get(key));
		return dest;
	}
	
	/**
	 * 将dest中对应值为null的key，赋值成source里对应的值
	 * 
	 * @param source
	 * @param dest
	 * @param keys
	 */
	public static Map mergeNullValue(Map source, Map dest, Object... keys) {
		if(keys==null || keys.length==0) {
			keys = source.keySet().toArray();
		}
		
		for(Object key: keys) {
			if(isNullValue(dest, key)) {
				merge(source, dest, key);
			}
		}
		return dest;
	}
	
	/**
	 * Map内部值copy
	 * 
	 * @param m
	 * @param sourceKey
	 * @param destKey
	 */
	public static Map copyValue(Map m, Object sourceKey, Object destKey) {
		m.put(destKey, m.get(sourceKey));
		return m;
	}
	
	/**
	 * Map内部值move
	 * 
	 * @param m
	 * @param sourceKey
	 * @param destKey
	 */
	public static Map moveValue(Map m, Object sourceKey, Object destKey) {
		m.put(destKey, m.remove(sourceKey));
		return m;
	}

	/**
	 * Map内部值copy到空值
	 * 
	 * @param m
	 * @param sourceKey
	 * @param destKey
	 */
	public static Map copyToNullValue(Map m, Object sourceKey, Object destKey) {
		if(isNullValue(m, destKey)) {
			copyValue(m, sourceKey, destKey);
		}
		return m;
	}
	
	/**
	 * Map设置value到对应NULL的Key
	 * 
	 * @param m
	 * @param sourceKey
	 * @param destKey
	 */
	public static Map setToNullValue(Map m, Object key, Object value) {
		if(isNullValue(m, key)) {
			m.put(key, value);
		}
		return m;
	}
	
	public static <T> T safeGet(Map<?, T> m, Object key) {
		if(m==null || m.isEmpty()) return null;
		return m.get(key);
	}
	
	public static <T> Map sortKey(Map m, Comparator<? super T> compare) {
		if(m==null || m.size()<2) return m;
		
		T[] keys = (T[])m.keySet().toArray();
		Arrays.sort(keys, compare);
		
		Map indexMap = new LinkedHashMap(m.size());
		for(T key: keys) {
			indexMap.put(key, m.get(key));
		}
		return indexMap;
	}
	
	public static boolean constainKeys(Map m, Object... keys) {
		if(m==null || m.size()==0 || keys==null || keys.length==0) return false;
		for(Object key: keys) {
			if(m.containsKey(key)) return true;
		}
		return false;
	}
	
	/**
	 * 因为在SQL更新操作时，可能未加相关字段不存在就不更新的限制，所以就不能在这个方法里判断，是否双方都存在相应KEY
	 * 
	 * @param m1
	 * @param m2
	 * @param keys
	 * @return
	 */
	public static boolean diffValInKeys(Map m1, Map m2, List<String> keys) {
		return diffValInKeys(m1, m2, keys, false);
	}
	/**
	 * @param m1
	 * @param m2
	 * @param keys
	 * @param mustContainsKey 是否,都包含KEY才进行比较
	 * @return
	 */
	public static boolean diffValInKeys(Map m1, Map m2, List<String> keys, boolean mustContainsKey) {
		if(m1==null || m1.size()==0 || m2==null || m2.size()==0 || keys==null || keys.size()==0) return false;
		for(String key: keys) {
			if(mustContainsKey) {
				if(!(m1.containsKey(key) && m2.containsKey(key))) {
					continue; //不是都包含，就路过
				}
			}
			
			Object v1 = m1.get(key);
			Object v2 = m2.get(key);
			if(v1!=null && v2!=null) {
				if(!v1.equals(v2)) { //不相同，返回不一样
					return true;
				}
			}else {//在执行SQL时，无法分别：空字符串 和 NULL 
				boolean v1IsNull = v1==null || ((v1 instanceof String)&&StringUtil.isEmptyStr((String)v1));
				boolean v2IsNull = v2==null || ((v2 instanceof String)&&StringUtil.isEmptyStr((String)v2));
				if(!(v1IsNull && v2IsNull)) { //不都是NULL，就是其中有一个是NULL，另一个不是, 返回不一样
					return true;  
				}
			}
		}
		return false;
	}
	
	public final static boolean equals(Object o1, Object o2) {
		return o1==null? o2==null: o1.equals(o2);
	}
	
	/**
	 * 获取通过EasyList的asIndexMap方法生成的索引map的值
	 * 
	 * @param indexMap
	 * @param m
	 * @param keys
	 * @return
	 */
	public static Map fetchIndexMapData(Map indexMap, Map m, Object... keys) {
		for(Object key: keys) {
			Object v = m.get(key);
			indexMap = (Map)indexMap.get(v);
			if(indexMap == null) break;
		}
		return indexMap;
	}
	
	/**
	 * 获取通过EasyList的asIndexMap方法生成的索引map的值
	 * 
	 * @param indexMap
	 * @param m
	 * @param keys
	 * @return
	 */
	public static List<Map> fetchGroupMapData(Map groupMap, Map m, Object... keys) {
		for(int i=0,ilen=keys.length; i<ilen; i++) {
			Object key = keys[i];
			Object v = m.get(key);
			
			Object data = groupMap.get(v);
			if(data == null) break;
			if(i == ilen-1) {
				return (List<Map>)data;
			}else {
				groupMap = (Map)data;
			}
		}
		return null;
	}
	
	public static Map bean2map(Object o){
		Map m = newMap();
		if(o == null) return m;
		
		try {
			m = PropertyUtils.describe(o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return m;
	}
	
	public static <T> T map2bean(Map m, T o){
		try{
			BeanUtils.populate(o, m);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return o;
	}
	
	public static String toUrlParamStr(Map<String, ?> params) {
		if(isNullOrEmpty(params)) return "_";
		String[] ss = new String[params.size()];
		int i = 0;
		for(Entry<String, ?> entry: params.entrySet()) {
			ss[i] = entry.getKey() + "=" + String.valueOf(entry.getValue());
			i++;
		}
		return StringUtil.join(ss, "&");
	}
}
