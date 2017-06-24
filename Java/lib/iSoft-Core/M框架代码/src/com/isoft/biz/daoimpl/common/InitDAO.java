package com.isoft.biz.daoimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.IInitDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.model.OSFuncVO;

public class InitDAO extends BaseDAO implements IInitDAO {

	public InitDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private final static String GET_OS_FUNCT = "GET_OS_FUNCT";

	public void doInitSysFunc() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(GET_OS_FUNCT);
		Map paraMap = new HashMap();
		List<OSFuncVO> osFuncs = executor.executeNameParaQuery(sql, paraMap,
				OSFuncVO.class);
		for (OSFuncVO osFunc : osFuncs) {
			String funcName = osFunc.getFuncName();
			int role = osFunc.getRole();
			int seqNo = osFunc.getSeqNo();
			String funcId = osFunc.getFuncId();
			String[] modules = funcName.split("\\.");
			String fid = createModuleTree(executor, modules, role, funcId, seqNo);

			String btId = osFunc.getBtId();
			String btName = osFunc.getBtName();
			String btDeps = osFunc.getBtDeps();
			String btExtra = osFunc.getBtExtra();
			String btUri = osFunc.getFuncUrl();
			
			String bid = createButtonLeaf(executor, fid, btId, btName, btDeps, btExtra, seqNo);
			createButtonUri(executor, fid, bid, btUri);
		}
	}

	private final static String GET_URI_ID_BY_NAME = "GET_URI_ID_BY_NAME";
	private final static String INSERT_SYS_FUNC_BT_URI = "INSERT_SYS_FUNC_BT_URI";
	private void createButtonUri(SQLExecutor executor, String fid, String bid,
			String btUri) {
		String sql = getSql(GET_URI_ID_BY_NAME);
		Map paraMap = new HashMap();
		paraMap.put("fid", fid);
		paraMap.put("bid", bid);
		paraMap.put("uri", btUri);
		List<String> uriids = executor.executeNameParaQuery(sql, paraMap, String.class);
		if(uriids.isEmpty()){
			sql = getSql(INSERT_SYS_FUNC_BT_URI);
			executor.executeInsertDeleteUpdate(sql, paraMap);
		}
	}

	private final static String GET_BT_ID_BY_NAME = "GET_BT_ID_BY_NAME";
	private final static String INSERT_SYS_FUNC_BT = "INSERT_SYS_FUNC_BT";
	private String createButtonLeaf(SQLExecutor executor, String fid,
			String btId, String btName, String btDeps, String btExtra, int seqNo) {
		String btid = null;
		String sql = getSql(GET_BT_ID_BY_NAME);
		Map paraMap = new HashMap();
		paraMap.put("id", seqNo);
		paraMap.put("fid", fid);
		paraMap.put("btName", btId);
		paraMap.put("btAlias", btName);
		paraMap.put("btDeps", btDeps);
		paraMap.put("btExtra", btExtra);
		paraMap.put("seqNo", seqNo);
		List<String> btids = executor.executeNameParaQuery(sql, paraMap, String.class);
		if(!btids.isEmpty()){
			btid = btids.get(0);
		} else {
			String insSql = getSql(INSERT_SYS_FUNC_BT);	
			executor.executeInsertDeleteUpdate(insSql, paraMap);
			btids = executor.executeNameParaQuery(sql, paraMap, String.class);
			btid = btids.get(0);
		}
		return btid;
	}

	private final static String INSERT_SYS_FUNC = "INSERT_SYS_FUNC";
	private String createModuleTree(SQLExecutor executor, String[] modules, int role, String funcId,int seqNo) {
		Map paraMap = new HashMap();
		String pid = "-1";
		String moduleId = null;
		String sql = getSql(INSERT_SYS_FUNC);
		for (int i = 0; i < modules.length; i++) {
			String moduleName = modules[i];
			Object[] cal = calModuleId(executor, moduleName, role, pid);
			boolean existed = (Boolean)cal[0];
			moduleId = (String)cal[1];
			if(!"-1".equals(pid) && !existed){
				moduleId = pid + moduleId;
			}
			if (!existed) {
				paraMap.put("moduleName", moduleName);
				paraMap.put("moduleId", moduleId);
				paraMap.put("funcId", (i == modules.length - 1) ? funcId : "");
				paraMap.put("role", role);
				paraMap.put("pid", pid);
				paraMap.put("seqNo", seqNo);
				executor.executeInsertDeleteUpdate(sql, paraMap);
			}
			pid = moduleId;
		}
		return moduleId;
	}

	private final static String GET_MODULE_ID_BY_NAME = "GET_MODULE_ID_BY_NAME";
	private final static String GET_MAX_MODULE_ID_BY_PID = "GET_MAX_MODULE_ID_BY_PID";
	private Object[] calModuleId(SQLExecutor executor, String moduleName, int role, String pid) {
		String moduleId = null;
		boolean existed = false;
		Map paraMap = new HashMap();
		paraMap.put("moduleName", moduleName);
		paraMap.put("role", role);
		paraMap.put("pid", pid);
		String sql = getSql(GET_MODULE_ID_BY_NAME);
		String maxMidSql = getSql(GET_MAX_MODULE_ID_BY_PID);
		List<String> moduleIdList = executor.executeNameParaQuery(sql, paraMap, String.class);
		if (!moduleIdList.isEmpty()) {
			moduleId = moduleIdList.get(0);
			existed = true;
		} else {
			String maxMid = "000";
			paraMap.put("pid", pid);
			List<String> maxMidList = executor.executeNameParaQuery(maxMidSql, paraMap, String.class);
			if (!maxMidList.isEmpty()) {
				maxMid = maxMidList.get(0);
				if (maxMid == null || maxMid.length() == 0) {
					maxMid = "000";
				}
			}
			int maxmid = Integer.valueOf(maxMid.substring(0,maxMid.length()-1));
			int mid = maxmid+1;
			moduleId = "000"+mid+String.valueOf(role);
			moduleId = moduleId.substring(moduleId.length()-4);
		}
		return new Object[] { existed, moduleId };
	}
}
