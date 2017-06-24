package com.isoft.biz.daoimpl.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ILinkDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.LinkVo;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;

public class LinkDAO extends BaseDAO implements ILinkDAO{

	public LinkDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static String SQL_LINK_LIST = "SQL_LINK_LIST";
	@SuppressWarnings("unchecked")
	public List<LinkVo> doLinkList(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LINK_LIST);
		return (List<LinkVo>) executor.executeNameParaQuery(sql, paraMap, LinkVo.class);
	}
	
	
	private static final String SQL_LINK_ADD = "SQL_LINK_ADD";
	private static final String SQL_LINK_GET = "SQL_LINK_GET";
	private static final String SQL_LINK_UPDATE = "SQL_LINK_UPDATE";
	public String doLinkAdd(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LINK_GET);
		List<String> links = executor.executeNameParaQuery(sql, paraMap, String.class);
		if (!Cphp.empty(links)&&links.size()>0)
			sql = getSql(SQL_LINK_UPDATE);
		else {
			String linkId = getFlowcode(NameSpaceEnum.T_LINK);
			sql = getSql(SQL_LINK_ADD);
			paraMap.put("linkId", linkId);
		}
		executor.executeInsertDeleteUpdate(sql, paraMap);
		return null;
	}
	
	private static final String SQL_LINK_TRUNCATE = "SQL_LINK_TRUNCATE";
	public int doLinkTruncate(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LINK_TRUNCATE);
		Map<String,Object> paraMap = new HashMap<String, Object>();
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
}
