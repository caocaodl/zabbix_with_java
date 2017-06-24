package com.isoft.iradar.biz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.util.StringUtil;

public class TemplatesDAO extends BaseDAO{
	
	public TemplatesDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	/**
	 * 取状态列表
	 */
	private static final String SQL_GET_STATUS = "SQL_GET_STATUS";
	public List doGetStatusList(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_STATUS);
		return executor.executeNameParaQuery(sql, param);
		
//		List ls = new ArrayList();
//		
//		List<String> ids = (List)param.get("hostIdList");
//		
//		for(String id: ids){
//			ls.add(EasyMap.build(
//					"hostid", EasyObject.$BigInteger(id),
//					"serviceStatus", Integer.valueOf(id) % 2
//				));
//		}
//		return ls;
	}
				
	/**
	 * 更新状态（0：未启用 1：启用     当启用时先判断有无记录，如果没有则插入启用状态的记录， 有则进行修改状态。 修改状态为未启用则直接修改）
	 */
	
	public List doUpdateStatus(Map param) {
		String status = (String) param.get("statusType");
		if(!StringUtil.isEmpty(status) && "cancel_service".equals(status)){
			List<String> list = (List) param.get("hostIdList");
			if(list.size()>0){
				param.put("status", "0");
				doStatus(param);
			}
		}else if(!StringUtil.isEmpty(status) && "start_service".equals(status)){
			List<String> list = (List) param.get("hostIdList");
			if(list.size()>0){
				Map map = new HashMap();
				for(String str : list){
					map.put("id", str);
					List byIdList = doGetById(map);
					if(byIdList.size()>0){ //有记录直接更新为启用状态
						List hostIdList = new ArrayList();
						hostIdList.add(str);
						map.put("hostIdList", hostIdList);
						map.put("status", "1");
						doStatus(map);
					}else{//没有记录 直接插入启用状态的记录
						map.put("status", "1");
						doAddHostService(map);
					}					
				}
			}
		}
		 return doGetStatusList(param);
	}

	/**
	 * 根据id取记录
	 */
	private static final String SQL_GET_BYID = "SQL_GET_BYID";
	public List doGetById(Map param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_BYID);
		return executor.executeNameParaQuery(sql, param);
	}
	
	/**
	 * 插入新记录
	 */
	private static final String SQL_ADD_HOSTSERVICE = "SQL_ADD_HOSTSERVICE";
	public void doAddHostService(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD_HOSTSERVICE);		
		executor.executeInsertDeleteUpdate(sql, param);		
	}
	
	/**
	 * 更新数据库数据
	 */
	private static final String SQL_UPDATE_STATUS = "SQL_UPDATE_STATUS";
	public void doStatus(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_STATUS);
		executor.executeInsertDeleteUpdate(sql, param);
	}
	
	
}
