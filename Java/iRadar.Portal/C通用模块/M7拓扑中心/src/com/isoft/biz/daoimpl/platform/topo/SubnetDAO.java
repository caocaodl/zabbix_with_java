package com.isoft.biz.daoimpl.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ISubnetDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.LinkVo;
import com.isoft.framework.persistlayer.SQLExecutor;

public class SubnetDAO extends BaseDAO implements ISubnetDAO{

	public SubnetDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_SUBNET_ADD = "SQL_SUBNET_ADD";
	public String doSubnetAdd(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_SUBNET_ADD);
		String id = getFlowcode(NameSpaceEnum.T_SUBNET);
		paraMap.put("id", id);
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			return id;
		} else {
			return null;
		}
	}
	
	private static final String SQL_SUBNET_TRUNCATE = "SQL_SUBNET_TRUNCATE";
	public int doSubnetTruncate(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_SUBNET_TRUNCATE);
		Map<String,Object> paraMap = new HashMap<String, Object>();
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
}
