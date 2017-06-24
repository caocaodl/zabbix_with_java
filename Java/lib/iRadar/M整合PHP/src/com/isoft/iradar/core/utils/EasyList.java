package com.isoft.iradar.core.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.beanutils.BeanUtils;

public abstract class EasyList {
	public final static List EMPTY_LIST = Collections.EMPTY_LIST;
	
	public static boolean isNullOrEmpty(Collection ls) {
		return ls==null || ls.isEmpty();
	}
	
	public static Map safeGet(List ls, int index) {
		if(ls==null || (index+1)>ls.size()) {
			return null;
		}else {
			return (Map)ls.get(index);
		}
	}
	
	public static <T> T safeGetT(List<T> ls) {
		return safeGetT(ls, 0);
	}
	public static <T> T safeGetT(List<T> ls, int index) {
		if(ls==null || (index+1)>ls.size()) {
			return null;
		}else {
			return ls.get(index);
		}
	}
	
	public static <T> List build(T... objects) {
		if(objects==null || objects.length==0) {
			return new ArrayList();
		}else {
			return new ArrayList(Arrays.asList(objects)); //Arrays.asList方法直接返回的List对象有部分方法有问题，所以需要再包一层
		}
	}
	public static <T> List buildUnmodify(T... groupKeys) {
		List ls = build(groupKeys);
		return Collections.unmodifiableList(ls);
	}
	
	public static List merge(List ls, Object... os) {
		ls.addAll(build(os));
		return ls;
	}
	
	public static List fill(List ls, Object... os) {
		if(ls == null) {
			ls = build(os);
		}else {
			ls.addAll(build(os));
		}
		return ls;
	}
	
	public static LinkedHashMap asIndexMap(List<Map> ls, Object...indexKeys){
		if(ls==null || ls.size()==0) return new LinkedHashMap(0);
		
		LinkedHashMap lhm = new LinkedHashMap(ls.size());
		if(indexKeys.length > 1) {
			for(Map m: ls) {
				Map nowKeyMap = lhm;
				for(int i=0,ilen=indexKeys.length; i<ilen; i++) {
					Object key = m.get(indexKeys[i]);
					if(i >= ilen-1) {
						nowKeyMap.put(key, m);
					}else {
						if(!nowKeyMap.containsKey(key)) {
							nowKeyMap.put(key, new LinkedHashMap());
						}
						nowKeyMap = (LinkedHashMap)nowKeyMap.get(key);
					}
				}
			}
		}else if(indexKeys.length == 1) {
			for(Map m: ls) {
				lhm.put(m.get(indexKeys[0]), m);
			}
		}
		return lhm;
	}
	
	public static <T> Map<Object, T> asIndexMapT(List<T> ls, String...indexKeys){
		if(ls==null || ls.size()==0) return new LinkedHashMap(0);
		LinkedHashMap lhm = new LinkedHashMap(ls.size());
		try {
			if(indexKeys.length > 1) {
				for(T m: ls) {
					Map nowKeyMap = lhm;
					for(int i=0,ilen=indexKeys.length; i<ilen; i++) {
						Object key = BeanUtils.getProperty(m, indexKeys[i]);
						if(i >= ilen-1) {
							nowKeyMap.put(key, m);
						}else {
							if(!nowKeyMap.containsKey(key)) {
								nowKeyMap.put(key, new LinkedHashMap());
							}
							nowKeyMap = (LinkedHashMap)nowKeyMap.get(key);
						}
					}
				}
			}else if(indexKeys.length == 1) {
				for(T m: ls) {
					lhm.put(BeanUtils.getProperty(m, indexKeys[0]), m);
				}
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return lhm;
	}
	
	
	public static <T> LinkedHashMap<String, T> asIndexClazzMap(Collection<T> ls, String key){
		if(ls==null || ls.size()==0) return new LinkedHashMap(0);
		
		LinkedHashMap lhm = new LinkedHashMap(ls.size());
		
		for(T m: ls) {
			try {
				lhm.put(BeanUtils.getProperty(m, key), m);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return lhm;
	}
	
	public static Map groupBy(Collection<Map> ls, Object... groupKeys) {
		Map<Object, Object> result = new HashMap(0);
		if(ls==null || ls.size()==0) return result;
		
		if(groupKeys.length > 1) {
			Object groupKey = groupKeys[0];
			
			Map<Object, List> gb = groupBy(ls, groupKey);
			for(Entry<Object, List> entry: gb.entrySet()) {
				result.put(entry.getKey(), groupBy(entry.getValue(), Arrays.copyOfRange(groupKeys, 1, groupKeys.length)));
			}
		}else if(groupKeys.length == 1) {
			for(Map data: ls) {
				Object groupV = data.get(groupKeys[0]);
				if(!result.containsKey(groupV)) {
					result.put(groupV, new ArrayList<Map>());
				}
				((List)result.get(groupV)).add(data);
			}
		}
		return result;
	}
	
	public static <T> Map<String, List<T>> groupClazzBy(Collection<T> ls, String groupKey) {
		Map<String, List<T>> result = new HashMap(0);
		if(ls==null || ls.size()==0) return result;
		
		for(T data: ls) {
			String groupV;
			try {
				groupV = BeanUtils.getProperty(data, groupKey);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if(!result.containsKey(groupV)) {
				result.put(groupV, new ArrayList<T>());
			}
			((List)result.get(groupV)).add(data);
		}
		
		return result;
	}
	
	
	public static List toMapList(Object[] ls, Object key) {
		return toMapList(EasyList.build(ls), key);
	}
	public static List toMapList(List ls, Object key) {
		if(ls == null) return null;
		List nls = new ArrayList(ls.size());
		for(Object o: ls) {
			nls.add(EasyMap.build(key, o));
		}
		return nls;
	}
	
	public static List toObjList(List<Map> ls, Object key) {
		if(ls == null) return null;
		List nls = new ArrayList(ls.size());
		for(Map m: ls) {
			nls.add(m.get(key));
		}
		return nls;
	}
	
	public static void moveKey(List<Map> ls, Object sourceKey, Object destKey) {
		if(ls == null) return;
		for(Map m: ls) {
			EasyMap.moveValue(m, sourceKey, destKey);
		}
	}
	
	public static void remove(List<Map> ls, Object key, Object value) {
		if(ls == null) return;
		
		Stack<Integer> indexStack = new Stack<Integer>();

		int count = 0;
		for(Map m: ls) {
			Object v = m.get(key);
			if(v == value) {
				indexStack.push(count);
			}else if(v!=null && v.equals(value)) {
				indexStack.push(count);
			}
			count++;
		}
		
		while(!indexStack.empty()) {
			ls.remove(indexStack.pop());
		}
	}
	
	/**
	 * 对2个LIST时行筛选，去掉完全相同的，并将之返回
	 * 
	 * @param ls1
	 * @param ls2
	 */
	public static <T> List<T> sieve(List<T> ls1, List<T> ls2){
		List<T> sames = new ArrayList<T>();
		
		List<T> iteratorLs = ls1.size()<ls2.size()? ls1: ls2;
		List<T> checkedLs = ls1==iteratorLs? ls2: ls1;
		
		for(int i=iteratorLs.size()-1,ilen=0; i>=ilen; i--) {
			T info = iteratorLs.get(i);
			if(checkedLs.contains(info)) {
				sames.add(info);
				checkedLs.remove(info);
				iteratorLs.remove(i);
			}
		}
		
		return sames;
	}
	
	/**
	 * 通过主键进行筛选，去掉主键值是相同的，并将之返回
	 * 
	 * @param ls1
	 * @param ls2
	 * @param keys
	 * @return
	 */
	public static List<Map> sieveByKey(List<Map> ls1, List<Map> ls2, Object...keys){
		List<Map> sames = new ArrayList<Map>();
		
		List<Map> iteratorLs = ls1;
		List<Map> checkedLs = ls2;
		Map checkedMap = EasyList.asIndexMap(checkedLs, keys);
		
		for(int i=iteratorLs.size()-1,ilen=0; i>=ilen; i--) {
			Map info = iteratorLs.get(i);
			Map data = EasyMap.fetchIndexMapData(checkedMap, info, keys);
			if(data!=null) {
				checkedLs.remove(data);
				iteratorLs.remove(i);
				sames.add(EasyMap.fill(data, "otherData", info));
			}
		}
		
		return sames;
	}
	
	/**
	 * 通过键值分组进行筛选，去掉值是相同的，并将之返回
	 * 
	 * @param ls1
	 * @param ls2
	 * @param keys
	 * @return
	 */
	public static List<Map> sieveByGroup(List<Map> ls1, List<Map> ls2, Object...keys){
		List<Map> sames = new ArrayList<Map>();
		
		List<Map> iteratorLs = ls1;
		List<Map> checkedLs = ls2;
		Map checkedMap = EasyList.groupBy(checkedLs, keys);
		
		for(int i=iteratorLs.size()-1,ilen=0; i>=ilen; i--) {
			Map info = iteratorLs.get(i);
			List<Map> gs = EasyMap.fetchGroupMapData(checkedMap, info, keys);
			if(gs!=null && !gs.isEmpty()) {
				Map data = gs.remove(0);
				checkedLs.remove(data);
				iteratorLs.remove(i);
				sames.add(EasyMap.fill(data, "otherData", info));
			}
		}
		
		return sames;
	}
	
	public static List<Map> mergeData(List<Map> delLs, List<Map> addLs, Object... keys){
		List<Map> merges = new ArrayList<Map>();
		for(int i=addLs.size()-1,ilen=0; i>=ilen; i--) {
			if(delLs.isEmpty()) break;
			Map delData = delLs.remove(0);
			Map addData = addLs.remove(i);
			merges.add(EasyMap.mergesSD(addData, delData, keys));
		}
		return merges;
	}
	
	public static List<Map> fillData(List<Map> ls, Object...kvs){
		for(Map m: ls) EasyMap.fill(m, kvs);
		return ls;
	}
	
	public static List fetchData(List<Map> ls, Object key){
		List r = new ArrayList(ls.size());
		for(Map m: ls) r.add(m.get(key));
		return r;
	}
	
	public static List<Map> bean2map(List ls){
		if(ls == null) return null;
		List nls = new ArrayList(ls.size());
		for(Object o: ls){
			nls.add(EasyMap.bean2map(o));
		}
		return nls;
	}
	
	public static List asList(Object t) {
		if(t == null) {
			return null;
		} else if(t instanceof Collection) {
			List ls = EasyList.build();
			ls.add((Collection)t);
			return ls;
		} else if(t.getClass().isArray()) {
			List ls = EasyList.build();
			for(int i=0,imax=Array.getLength(t); i<imax; i++) {
				ls.add(Array.get(t, i));
			}
			return ls;
		}
		return build(t);
	}
}
