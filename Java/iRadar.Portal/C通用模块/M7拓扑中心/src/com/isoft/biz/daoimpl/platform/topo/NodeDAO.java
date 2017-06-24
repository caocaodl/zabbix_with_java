package com.isoft.biz.daoimpl.platform.topo;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.INodeDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.TopoUtil;

public class NodeDAO extends BaseDAO implements INodeDAO{

	public NodeDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static String SQL_T_NODE_LIST = "SQL_T_NODE_LIST";
	@SuppressWarnings("unchecked")
	public List<NodeVo> doTNodeList(DataPage dataPage,Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_LIST);
		if(dataPage == null){
			return (List<NodeVo>) executor.executeNameParaQuery(sql, paraMap, NodeVo.class);	
		}else{
			return (List<NodeVo>) executor.executeNameParaQuery(dataPage,sql, paraMap, NodeVo.class);
		}
		
	}
	
	private static String SQL_T_NODE_LIST_EXCLUDE_THUMBNAIL = "SQL_T_NODE_LIST_EXCLUDE_THUMBNAIL";
	@SuppressWarnings("unchecked")
	public List<NodeVo> doTNodeListExcluedThumbnail(DataPage dataPage,Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_LIST_EXCLUDE_THUMBNAIL);
		if(dataPage == null){
			return (List<NodeVo>) executor.executeNameParaQuery(sql, paraMap, NodeVo.class);	
		}else{
			return (List<NodeVo>) executor.executeNameParaQuery(dataPage,sql, paraMap, NodeVo.class);
		}
		
	}
	
	private static final String SQL_T_NODE_ADD = "SQL_T_NODE_ADD";	
	public String doTNodeAdd(Map<String,Object> paraMap){
		if(doTNodeDuplicateCheck(paraMap)==0){
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_T_NODE_ADD);
			String nodeId = getFlowcode(NameSpaceEnum.T_NODE);
			paraMap.put("nodeId", nodeId);
			paraMap.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				return nodeId;
			} 
		}
		return null;
	}
	
	
	private static String SQL_TOPO_NODE_DUPLICATE_CHECK = "SQL_TOPO_NODE_DUPLICATE_CHECK";
	public int doTNodeDuplicateCheck(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_NODE_DUPLICATE_CHECK);
		return executor.executeNameParaQuery(sql, paraMap, String.class).size();
	}
	
	private static final String SQL_DEL_EXCLUDE_THUMBNAIL_OF_NODE = "SQL_DEL_EXCLUDE_THUMBNAIL_OF_NODE";
	@SuppressWarnings("rawtypes")
	public int doDelExcludeNodeOfThumbnail(Map param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DEL_EXCLUDE_THUMBNAIL_OF_NODE);
		return executor.executeInsertDeleteUpdate(sql, param);
	}
	
    private static final String SQL_T_NODE_MODIFY_G = "SQL_T_NODE_MODIFY_G";
	public boolean doTNodeModifyG(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_MODIFY_G);
		return executor.executeInsertDeleteUpdate(sql, paraMap)!=0?true:false;
	}
	
    private static final String SQL_T_NODE_MODIFY_TBNAILID = "SQL_T_NODE_MODIFY_TBNAILID";
	public int doTNodeModifyTbnailId(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_MODIFY_TBNAILID);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_THUMBNAIL_CHECK_OPER = "SQL_THUMBNAIL_CHECK_OPER";
	public int doThumbnailCheckOper(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_THUMBNAIL_CHECK_OPER);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_THUMBNAIL_CHECK_ALL_OPER = "SQL_THUMBNAIL_CHECK_ALL_OPER";
	public int doThumbnailAllCheckOper(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_THUMBNAIL_CHECK_ALL_OPER);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_THUMBNAIL_UPDATE_TBNAILID_G = "SQL_THUMBNAIL_UPDATE_TBNAILID_G";
	public int doThumbnailUpdateTbnailIdG(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_THUMBNAIL_UPDATE_TBNAILID_G);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	String SQL_T_NODE_LOAD_BY_ID = "SQL_T_NODE_LOAD_BY_ID";
	@SuppressWarnings("unchecked")
	public NodeVo doTNodeLoadById(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_LOAD_BY_ID);
		List<NodeVo> lists = (List<NodeVo>) executor.executeNameParaQuery(sql, paraMap, NodeVo.class);
		if(lists.size()>0){
			return lists.get(0);
		}else{
			return null;
		}
	}
	
	private static final String SQL_T_NODE_DEL_BY_ID = "SQL_T_NODE_DEL_BY_ID";
	private static final String SQL_T_NODE_UPDATE_THUMBNAIL_BY_ID = "SQL_T_NODE_UPDATE_THUMBNAIL_BY_ID";
	public boolean doTNodeDel(Map<String,Object> paraMap){
		boolean flag = false;
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_UPDATE_THUMBNAIL_BY_ID);
		executor.executeInsertDeleteUpdate(sql, paraMap);
		sql = getSql(SQL_T_NODE_DEL_BY_ID);
		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
			flag = true;
		}
		return flag;
	}
	
	private static final String SQL_T_NET_TOPO_DEL_BY_ID = "SQL_T_NET_TOPO_DEL_BY_ID";
	public boolean doNetTopoDel(Map<String,Object> paraMap){
		boolean flag = false;
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NET_TOPO_DEL_BY_ID);
		if(executor.executeInsertDeleteUpdate(sql, paraMap)!=0){
			flag = true;
		}
		return flag;
	}
	
	private static final String SQL_T_NODE_LOAD_WITH_THUMBNAIL = "SQL_T_NODE_LOAD_WITH_THUMBNAIL";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<NodeVo> doLoadNodeIdsWithThmbnail(Map param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_LOAD_WITH_THUMBNAIL);
		return executor.executeNameParaQuery(sql, param, NodeVo.class);
	}
	
	private static final String SQL_T_NODE_THUMBNAIL_TREE = "SQL_T_NODE_THUMBNAIL_TREE";
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<NodeVo> doTNodeThumbnailTree(Map param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_THUMBNAIL_TREE);
		return executor.executeNameParaQuery(sql, param, NodeVo.class);
	}
	
	private static String SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE = "SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE";
	@SuppressWarnings("unchecked")
	public List<NodeVo> doTNodeThumbnailUnchecked(DataPage dataPage,Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE);
		if(dataPage != null){
			return (List<NodeVo>) executor.executeNameParaQuery(dataPage,sql, paraMap, NodeVo.class);
		}else{
			return (List<NodeVo>) executor.executeNameParaQuery(sql, paraMap, NodeVo.class);
		}
		
	}
	
	private static String SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID = "SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID";
	@SuppressWarnings("unchecked")
	public List<Integer> doHostIdByTopoId(DataPage dataPage,Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_HOST_ID_LIST_BY_TOPO_ID);
		if(dataPage == null){
			return (List<Integer>) executor.executeNameParaQuery(sql, paraMap, Integer.class);	
		}else{
			return (List<Integer>) executor.executeNameParaQuery(dataPage,sql, paraMap, Integer.class);
		}
	}
	
	private static String SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE_TYPE_LIST = "SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE_TYPE_LIST";
	@SuppressWarnings("unchecked")
	public List<String> doNodeTypeList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_THUMBNAIL_UNCHECKED_NODE_TYPE_LIST);
		return (List<String>) executor.executeNameParaQuery(sql, paraMap, String.class);
	}
	
	private static String SQL_T_NODE_GET_HOSTID_IN_IRADAR = "SQL_T_NODE_GET_HOSTID_IN_IRADAR";
	@SuppressWarnings("unchecked")
	public List<Map> doNodeGetHostIdInIradar(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_NODE_GET_HOSTID_IN_IRADAR);
		Map sqlVO = getSqlVO(SQL_T_NODE_GET_HOSTID_IN_IRADAR);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
}
