package com.isoft.biz.daoimpl.portserves;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.common.IProfDAO;
import com.isoft.biz.dao.portserves.IProfServesDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.server.RunParams;
import com.isoft.utils.EncryptionUtil;
import com.isoft.utils.U4aUtil;

public class ProfServesDAO extends BaseDAO implements IProfServesDAO {

	public ProfServesDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_TENANT_VIEW = "SQL_TENANT_VIEW";
	private static final String SQL_UPDATA = "SQL_UPDATA";
	private static final String SQL_UPDATA_VIEW = "SQL_UPDATA_VIEW";
	private static final String SQL_SELECT = "SQL_SELECT";
	
// 查看訂閱
	public List doProtServer() {
		SQLExecutor executor = getSqlExecutor();
		Map paraMap = new LinkedMap();
		String sql = getSql(SQL_TENANT_VIEW);
		Map sqlVO = getSqlVO(SQL_TENANT_VIEW);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
 //訂閱
	public boolean doInterfaceServer(String userid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATA);
		Map paraMap = new LinkedMap();
		paraMap.put("userid", userid);
		return executor.executeInsertDeleteUpdate(sql, paraMap) > 0;
	}
//取消訂閱	
	public boolean doInterfaceServerDelect(String userid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATA_VIEW);
		Map paraMap = new LinkedMap();
		paraMap.put("userid", userid);
		return executor.executeInsertDeleteUpdate(sql, paraMap) > 0;
	}
	
//查找用戶	
	public List doFind (String userid) {
		SQLExecutor executor = getSqlExecutor();
		Map paraMap = new LinkedMap();
		paraMap.put("userid", userid);
		String sql = getSql(SQL_SELECT);
		Map sqlVO = getSqlVO(SQL_SELECT);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
   
}
