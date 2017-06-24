package com.isoft.biz.daoimpl.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import com.isoft.biz.dao.platform.topo.IHostExpDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.method.Role;
import com.isoft.biz.vo.platform.topo.HostExp;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostExpDAO extends BaseDAO implements IHostExpDAO{

	public HostExpDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_HOST_LIST = "SQL_HOST_LIST";
	@SuppressWarnings("unchecked")
	public List<Host> doHostList(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_LIST);
		return executor.executeNameParaQuery(sql, paraMap, HostExp.class);
	}
	
	
	private static final String SQL_HOST_ADD = "SQL_HOST_ADD";
	private static final String SQL_HOST_GET = "SQL_HOST_GET";
	private static final String SQL_HOST_UPDATE = "SQL_HOST_UPDATE";
	public int doHostExpAdd(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_GET);
		Map sqlVO = getSqlVO(SQL_HOST_GET);
		List<Map> hosts = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		if(!Cphp.empty(hosts)&&hosts.size()>0)
			sql = getSql(SQL_HOST_UPDATE);
		else
			sql = getSql(SQL_HOST_ADD);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	public boolean doHostExpGet(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_GET);
		Map sqlVO = getSqlVO(SQL_HOST_GET);
		List<Map> hosts = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		if(hosts!=null&&hosts.size()>0)
			return Nest.value(hosts.get(0),"error").asBoolean();
		else
			return false;
	}
	
	private static final String SQL_HOST_NOT_EXIST_DEL = "SQL_HOST_NOT_EXIST_DEL";
	private static final String SQL_HOST_NOT_EXIST_LINK_DEL = "SQL_HOST_NOT_EXIST_LINK_DEL";
	private static final String SQL_HOST_NOT_EXIST_EXTEND_DEL = "SQL_HOST_NOT_EXIST_EXTEND_DEL";
	private static final String SQL_HOST_NOT_EXIST_LOCATION_DEL = "SQL_HOST_NOT_EXIST_LOCATION_DEL";
	public void delHostExptNotExist(List<Integer> hostIds){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_NOT_EXIST_DEL);
		Map paraMap = new LinkedMap();
		paraMap.put("hostIds", hostIds);
		executor.executeInsertDeleteUpdate(sql, paraMap);
		sql = getSql(SQL_HOST_NOT_EXIST_LINK_DEL);
		executor.executeInsertDeleteUpdate(sql, paraMap);
		sql = getSql(SQL_HOST_NOT_EXIST_EXTEND_DEL);
		executor.executeInsertDeleteUpdate(sql, paraMap);
		sql = getSql(SQL_HOST_NOT_EXIST_LOCATION_DEL);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_HOST_ERROR_SET = "SQL_HOST_ERROR_SET";
	public void doHostExpErrorSet(List<Integer> hostIds){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_ERROR_SET);
		Map paraMap = new LinkedMap();
		paraMap.put("hostIds", hostIds);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_HOST_TRUNCATE = "SQL_HOST_TRUNCATE";
	
	public int doHostExpTruncate(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_TRUNCATE);
		Map<String,Object> paraMap = new HashMap<String,Object>();
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_HOST_LOAD = "SQL_HOST_LOAD";
	@SuppressWarnings("unchecked")
	public Host doHostExpLoadById(String hostId){
		SQLExecutor executor = getSqlExecutor();
		Map<String,Object> paraMap = new HashMap<String,Object>();
		paraMap.put("hostId", hostId);
		String sql = getSql(SQL_HOST_LOAD);
		List<Host> list = executor.executeNameParaQuery(sql, paraMap, HostExp.class);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	private static final String SQL_HOST_CATEGORY_LIST = "SQL_HOST_CATEGORY_LIST";
	@SuppressWarnings("unchecked")
	public CArray<String> doCategoryList(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_HOST_CATEGORY_LIST);
		List<String> dataList = executor.executeNameParaQuery(sql, paraMap, String.class);
		if(!Cphp.empty(dataList)){
			return Nest.as(dataList).asCArray();
		}
		return CArray.array();
	}
	/**
	 * 根据hostid 查出组id
	 * @param paraMap
	 * @return
	 */
	private static final String SQL_HOST_GROUP = "SQL_HOST_GROUP";
	@SuppressWarnings("unchecked")
	public List doHostgroup(String hostId){
		SQLExecutor executor = getSqlExecutor();
		Map<String,Object> paraMap = new HashMap<String,Object>();
		paraMap.put("hostId", hostId);
		Map sqlVo;
		sqlVo = getSqlVO(SQL_HOST_GROUP);
		String sql = getSql(SQL_HOST_GROUP);
		List list = executor.executeNameParaQuery(sql, paraMap,sqlVo);
		if(list.size()>0){
			return list;
		}
		return null;
	}
	/**
	 * 实现资产设备列表
	 * @param paraMap
	 * @return
	 */
	private static String SQL_ASSETS_HOST_LIST = "SQL_ASSETS_HOST_LIST";
	@SuppressWarnings("rawtypes")
	public List<Map> doAssetsHostList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ASSETS_HOST_LIST);
		Map tempMap = getSqlVO(SQL_ASSETS_HOST_LIST);
		return executor.executeNameParaQuery(sql, paraMap,tempMap);
	}
	
	/**
	 * 实现资产设备类型列表
	 * @param paraMap
	 * @return
	 */
	private static String SQL_ASSETS_CATEGORY_LIST = "SQL_ASSETS_CATEGORY_LIST";
	private static String SQL_ASSETS_CATEGORY_LIST_TEM = "SQL_ASSETS_CATEGORY_LIST_TEN";
	@SuppressWarnings("rawtypes")
	public List<Map> doAssetsCategoryList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql;
		Map sqlVo;
		List groupList;
		CArray serverCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_SERVER);
		CArray netCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_NET_DEV);
		CArray storageCA = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_STORAGE);
		if(Role.isLessor(RadarContext.getIdentityBean().getTenantRole())){
			sql = getSql(SQL_ASSETS_CATEGORY_LIST);
			sqlVo = getSqlVO(SQL_ASSETS_CATEGORY_LIST);
			/**
			 * 系统屏蔽默认的存储设备类型
			 */
//			groupList = EasyList.build(IMonConsts.MON_STORAGE, IMonConsts.MON_NET_CISCO,IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_SERVER_LINUX);
			groupList = Cphp.array_merge(serverCA,netCA,storageCA).toList();
		}else{
			sql = getSql(SQL_ASSETS_CATEGORY_LIST_TEM);
			sqlVo = getSqlVO(SQL_ASSETS_CATEGORY_LIST_TEM);
			groupList = EasyList.build(IMonConsts.DISCOVERED_HOSTS, IMonConsts.MON_VM);
		}
		paraMap.put("groupList", groupList);
		
		return executor.executeNameParaQuery(sql, paraMap,sqlVo);	
	}

	
	/**
	 * 获取资产设别类型，通过hostId
	 * @param paraMap
	 * @return
	 */
	private static String SQL_ASSETS_CATEGORY_BY_HOSTID = "SQL_ASSETS_CATEGORY_BY_HOSTID";
	@SuppressWarnings("unchecked")
	public String doAssetsCategoryByHostId(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ASSETS_CATEGORY_BY_HOSTID);
		List<String> resultList = executor.executeNameParaQuery(sql, paraMap,String.class);
		if(resultList.size()!=0){
			return resultList.get(0);
		}
		return "unkown";
	}

}
