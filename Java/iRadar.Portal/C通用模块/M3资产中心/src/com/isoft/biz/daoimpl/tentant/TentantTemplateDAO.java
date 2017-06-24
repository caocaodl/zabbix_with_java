package com.isoft.biz.daoimpl.tentant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;

public class TentantTemplateDAO extends BaseDAO {
	public TentantTemplateDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	private static final String GET_ONE_VICE_ITEM = "GET_ONE_VICE_ITEM";
	private static final String GET_I_UPDATA_ITEM = "GET_I_UPDATA_ITEM";
	private static final String SQL_PROCESS_REMARK_NUM = "SQL_PROCESS_REMARK_NUM";
	private static final String SQL_GET_PROCESS = "SQL_GET_PROCESS";
	private static final String SQL_GET_BEFORE_ITEM = "SQL_GET_BEFORE_ITEM";
	private static final String SQL_GET_RELASE_ITEMID = "SQL_GET_RELASE_ITEMID";
	private static final String GET_IS_VICE_ITEMID = "GET_IS_VICE_ITEMID";
	private static final String SQL_PROCESS_REMARK = "SQL_PROCESS_REMARK";
	private static final String GET_MAIN_HOST_MAXID = "GET_MAIN_HOST_MAXID";
	private static final String GET_MAIN_ITEM_MAXID = "GET_MAIN_ITEM_MAXID";
	/******************************查询数据****************************************/
	/**获取附表中将要更新的item
	 * @param itemid
	 * @return
	 */
	public List getOneViceItem(Long itemid){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(GET_ONE_VICE_ITEM);
		Map sqlVO = getSqlVO(GET_ONE_VICE_ITEM);
		Map paramap=new HashMap();
		paramap.put("itemid", itemid);
		return executor.executeNameParaQuery(sql, paramap, sqlVO);
	}
	
	/**从备份监控指标表中查找要要更新到监控指标表的item
	 * @param paraMap
	 * @return
	 */
	public List getUpdateItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(GET_I_UPDATA_ITEM);
		Map sqlVO = getSqlVO(GET_I_UPDATA_ITEM);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	/**获取发布之前的监控指标
	 * @param paraMap
	 * @return
	 */
	public List getBeforeItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_BEFORE_ITEM);
		Map sqlVO = getSqlVO(SQL_GET_BEFORE_ITEM);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	/**获取指标备份表是否关联监控指标表
	 * @param paraMap
	 * @return
	 */
	public List isHasRelaseItemid(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_RELASE_ITEMID);
		List numlist=executor.executeNameParaQuery(sql, paraMap,String.class);
		return numlist;
	}
	
	/**判断监控指标副表是否有跟主表一样的数据
	 * @param itemid
	 * @return
	 */
	public List isHasViceItemid(Long itemid){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(GET_IS_VICE_ITEMID);
		Map paramap=new HashMap();
		paramap.put("itemid", itemid);
		List numlist=executor.executeNameParaQuery(sql, paramap,String.class);
		return numlist;
	}
	
	
	
	
	/**在租户模型副表中，查找是否有相同id的监控模型
	 * @return
	 */
	public Long getMainItemMaxid(){
		SQLExecutor executor = getSqlExecutor();
		Map paraMap=new HashMap();
		paraMap.put("flag", 0);
		String sql = getSql(GET_MAIN_ITEM_MAXID);
		List<Map> maxidlist=executor.executeNameParaQuery(sql, paraMap);
		Long maxid=0L;
		for(Map maxidmap : maxidlist){
			maxid=(Long)maxidmap.get("itemid");
		}
		return maxid;
	}
	
	
	/******************************添加数据****************************************/
	private static final String SQL_ADD_TENANT_ITEM = "SQL_ADD_TENANT_ITEM";
	private static final String UPDATE_VICE_TENANT_ITEM = "UPDATE_VICE_TENANT_ITEM";
	private static final String ADD_VICE_HOST = "ADD_VICE_HOST";
	private static final String ADD_MAIN_HOST = "ADD_MAIN_HOST";
	private static final String ADD_MAIN_ITEM = "ADD_MAIN_ITEM";
	
	/**添加监控指标备份数据
	 * @param paraMap
	 * @return
	 */
	public int AddItem(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD_TENANT_ITEM);
		int addnum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return addnum;
	}
	
	/** 在租户模型副表中，创建新的监控模型
	 * @param paraMap
	 * @return
	 */
	public int addViceHost(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(ADD_VICE_HOST);
		int addnum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return addnum;
	}
	
	/** 在监控指标主表中，添加新的监控指标
	 * @param paraMap
	 * @return
	 */
	public int addMainItem(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(ADD_MAIN_ITEM);
		int addnum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return addnum;
	}
	
	/** 在租户模型主表中，创建新的监控模型
	 * @param paraMap
	 * @return
	 */
	public int addMainHost(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(ADD_MAIN_HOST);
		int addnum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return addnum;
	}
	
	
	/******************************更新数据数据****************************************/
	private static final String SQL_UPDATE_STATUS = "SQL_UPDATE_STATUS";
	private static final String SQL_UPDATE_ITEM_STATUS = "SQL_UPDATE_ITEM_STATUS";
	private static final String UPDATE_VICE_ITEM_STATUS = "UPDATE_VICE_ITEM_STATUS";
	private static final String UPDATE_TENANT_ITEM = "UPDATE_TENANT_ITEM";
	private static final String SQL_UPDATE_TENANT_ITEM = "SQL_UPDATE_TENANT_ITEM";
	private static final String UPDATE_MAIN_TEMPLATE = "UPDATE_MAIN_TEMPLATE";
	/**更新审批状态
	 * @param paraMap
	 * @return
	 */
	public int UpdateStatus(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_STATUS);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**更新监控指标使用状态
	 * @param paraMap
	 * @return
	 */
	public int updateItemStatus(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_ITEM_STATUS);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**更新监控指标副表使用状态
	 * @param paraMap
	 * @return
	 */
	public int updateViceItemStatus(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_VICE_ITEM_STATUS);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**更新监控指标主表对应的item数据
	 * @param paraMap
	 * @return
	 */
	public int updateMainItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_TENANT_ITEM);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**更新监控指标备份数据
	 * @param paraMap
	 * @return
	 */
	public int UpdateItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_TENANT_ITEM);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**更新监控指标副表发布前的状态
	 * @param paraMap
	 * @return
	 */
	public int updateViceBeforeItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_VICE_TENANT_ITEM);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	/**在租户模型副表中，更新主表数据
	 * @param paraMap
	 * @return
	 */
	public int updateMainTemplate(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_MAIN_TEMPLATE);
		int updatenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return updatenum;
	}
	
	
	
	/******************************删除数据部分****************************************/
	private static final String SQL_DELETE_BEFORE_ITEM = "SQL_DELETE_BEFORE_ITEM";
	private static final String DELETE_VICE_ITEM = "DELETE_VICE_ITEM";
	private static final String DELETE_VICE_HOST = "DELETE_VICE_HOST";
	
	
	/**重置后删除原来的item
	 * @param paraMap
	 * @return
	 */
	public int deleteBeforeItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DELETE_BEFORE_ITEM);
		int deletenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return deletenum;
	}
	
	
	/**删除监控指标附表中对应的item
	 * @param paraMap
	 * @return
	 */
	public int deleteViceItem(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(DELETE_VICE_ITEM);
		int deletenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return deletenum;
	}
	
	

}
