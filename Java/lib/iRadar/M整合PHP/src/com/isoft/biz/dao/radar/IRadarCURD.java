package com.isoft.biz.dao.radar;

import java.util.Map;

import com.isoft.iradar.model.params.CParamWrapper;
import com.isoft.types.CArray;

public interface IRadarCURD<P extends CParamWrapper, K> {

	<T> T get(P params);
	
	Object getObjects(Map<String, Object[]> filter);
	
	/**
	 * Check if user has read permissions
	 */
	boolean isReadable(K... ids);
	
	/**
	 * Check if user has write permissions
	 */
	boolean isWritable(K... ids);
	
	boolean exists(CArray object);
	
	CArray<K[]> create(CArray<Map> rows);
	
	CArray<K[]> update(CArray<Map> rows);
	
	CArray<K[]> delete(K... ids);

}
