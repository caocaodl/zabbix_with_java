package com.isoft.biz.daoimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.framework.persistlayer.SQLExecutor;

public class BaseDAO extends ISoftBaseDAO{

    public BaseDAO(SQLExecutor sqlExecutor) {
        super(sqlExecutor);
    }
    
    @Override
	protected List<Class<?>> getExtendsDAOClass() {
        List<Class<?>> baseDAOs = super.getExtendsDAOClass();
        if(!baseDAOs.contains(BaseDAO.class)){
        	baseDAOs.add(BaseDAO.class);
        }
        return baseDAOs;
    }
    
    @SuppressWarnings("unchecked")
	private Map<String,Map> collections = new HashMap<String,Map>();
    @SuppressWarnings("unchecked")
	private Map getHitsMap(String key){
    	if(!collections.containsKey(key)){
    		collections.put(key, new HashMap(10));
    	}
    	return collections.get(key);
    }
    
    private static final String SQL_POPULATE_ = "SQL_POPULATE_";
	private void populateEntry(List<Map> dataList, String pkey, String tkey){
		populateEntry(dataList, pkey, tkey, null);
	}
	private void populateEntry(List<Map> dataList, String pkey, String tkey, String ekey){
    	SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_POPULATE_+tkey);
		Map hits = getHitsMap(tkey);
		Map param = new LinkedMap();
		List list = null;
		for(Map data:dataList){
			Object id = data.get(pkey);
			if(hits.containsKey(id)){
				if(hits.get(id) != null){
					data.put(pkey, hits.get(id));
				}
			} else {
				param.put("id", id);
				if (ekey != null && ekey.length() > 0) {
					param.put("ekey", ekey);
				}
				list = executor.executeNameParaQuery(sql, param, String.class);
				if(!list.isEmpty()){
					hits.put(id, (String)list.get(0));
					data.put(pkey, hits.get(id));
				} else {
					hits.put(id, null);
				}
			}
		}		
    }
    
	public void populateUserEntry(List<Map> dataList, String pkey){
    	this.populateEntry(dataList, pkey, "USER");
    }
    
	public void populateTenantEntry(List<Map> dataList, String pkey){
    	this.populateEntry(dataList, pkey, "TENANT");
    }
	
	public void populateDictEntry(List<Map> dataList, String pkey, String ekey){
    	this.populateEntry(dataList, pkey, "DICT", ekey);
    }
}
