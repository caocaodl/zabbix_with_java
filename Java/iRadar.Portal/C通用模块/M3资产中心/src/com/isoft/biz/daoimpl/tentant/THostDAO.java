package com.isoft.biz.daoimpl.tentant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.types.CArray;

import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.get_dbid;

public class THostDAO extends CDB {

	public THostDAO(IIdentityBean idBean, SQLExecutor sqlExecutor) {
		super(idBean, sqlExecutor);
	}
	
	
	/**获取审批详情
	 * @param dataPage
	 * @param paraMap
	 * @return
	 */
	private static final String GET_PROCESS = "GET_PROCESS";
	public List GetProcess(DataPage dataPage,Long hostid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(GET_PROCESS);
		Map sqlVO = getSqlVO(GET_PROCESS);
		Map paramap=new HashMap();
		paramap.put("hostid", hostid);
		return executor.executeNameParaQuery(dataPage, sql, paramap, sqlVO);
	}

	/**在租户模型副表中，查找是否有相同id的监控模型
	 * @return
	 */
	public Long getMainHostMaxid(){
		return get_dbid(idBean, this.getSqlExecutor(), "i_tenant_template", "hostid", 1);
	}
	
	/**获取备注名称
	 * @param paraMap
	 * @return
	 */
	private static final String SQL_PROCESS_REMARK = "SQL_PROCESS_REMARK";
	public List getRemark(int hostid){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PROCESS_REMARK);
		Map paramap=new HashMap();
		paramap.put("hostid", hostid);
		List remarklist=executor.executeNameParaQuery(sql, paramap, String.class);
		return remarklist;
	}
	
	private static final String SQL_PROCESS_REMARK_NUM = "SQL_PROCESS_REMARK_NUM";
	/**获取发布前的监控指标
	 * @param paraMap
	 * @return
	 */
	public List getRemarkNum(Long hostid){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PROCESS_REMARK_NUM);
		Map paramap=new HashMap();
		paramap.put("hostid", hostid);
		List numlist=executor.executeNameParaQuery(sql, paramap,String.class);
		return numlist;
	}
	
	public CArray<Map> getViceHostNumTwo(Long hostid,int flag){
		SQLExecutor executor = getSqlExecutor();
		String GET_VICE_HOST = " select  hostid as hostid,name as name "
		        +" from i_tenant_hosts t where t.hostid=#{hostid} and t.flag=#{flag} ";
		Map params = new HashMap();
		params.put("hostid",hostid);
		params.put("flag",flag);
		CArray<Map> hostcarray = DBselect(executor, GET_VICE_HOST,params);
	    return hostcarray;
	}
	
	/******************************添加数据部分****************************************/
	private static final String ADD_PROCESS_REMARK = "ADD_PROCESS_REMARK";
	private static final String ADD_VICE_HOST = "ADD_VICE_HOST";
	/**创建审批流程
	 * @param hostid  模型id
	 * @param user  用户信息
	 * @param approveresult 处理结果
	 * @param note  审批备注
	 */
	public int createRemark(Long hostid,Map user,String approveresult,String note){
		Map remarkmap=new HashMap();
		remarkmap.put("hostid", hostid);
		remarkmap.put("userid", user.get("userid"));
		remarkmap.put("username", user.get("alias"));
		remarkmap.put("approve_result", approveresult);
		remarkmap.put("description", note);
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(ADD_PROCESS_REMARK);
		int createRemarknum=executor.executeInsertDeleteUpdate(sql, remarkmap);
		return createRemarknum;
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
	
	
	
	/******************************更新数据数据****************************************/
	private static final String UPDATE_PROCESS_STATUS = "UPDATE_PROCESS_STATUS";
	private static final String UPDATE_VICE_HOST = "UPDATE_VICE_HOST";
	private static final String UPDATE_MAIN_TEMPLATE = "UPDATE_MAIN_TEMPLATE";
	
	/**更新审批状态
	 * @param hostid 模型di
	 * @param status 审批状态
	 */
	public int updateProcessStatus(Long hostid,int status){
		Map paramap=new HashMap();
		paramap.put("hostid", hostid);
		paramap.put("process_status", status);
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_PROCESS_STATUS);
		int updateStatusnum=executor.executeInsertDeleteUpdate(sql, paramap);
		return updateStatusnum;
	}
	
	/**更新监控指标副表发布前的状态
	 * @param paraMap
	 * @return
	 */
	public int updateViceHost(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(UPDATE_VICE_HOST);
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
	
	
	/******************************删除数据数据****************************************/
	private static final String DELETE_VICE_HOST = "DELETE_VICE_HOST";
	/**监控维度重置后，删除监控模型副表的host
	 * @param hostid
	 * @return
	 */
	public int deleteViceHost(Long hostid){
		SQLExecutor executor = getSqlExecutor();
		Map paraMap=new HashMap();
		paraMap.put("hostid", hostid);
		String sql = getSql(DELETE_VICE_HOST);
		int deletenum=executor.executeInsertDeleteUpdate(sql, paraMap);
		return deletenum;
	}
	
	
	
	
	

}
