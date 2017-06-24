package com.isoft.biz.daoimpl.platform.topo;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.IBackGPicDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class BackGPicDAO extends BaseDAO implements IBackGPicDAO {

	public BackGPicDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

    private static final String SQL_BACKG_PIC_LIST = "SQL_BACKG_PIC_LIST";
    @SuppressWarnings("rawtypes")
	public List<Map> doBackGPicList(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_BACKG_PIC_LIST);
        Map sqlVO = getSqlVO(SQL_BACKG_PIC_LIST);
        
        return executor.executeNameParaQuery(sql, paraMap, sqlVO);        
    }
    
	private static final String SQL_BACKG_PIC_ADD = "SQL_BACKG_PIC_ADD";
	private static final String SQL_BACKG_PIC_UPDATE = "SQL_BACKG_PIC_UPDATE";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean doBackGPicChange(Map paraMap) {
		boolean flag = false;
		SQLExecutor executor = getSqlExecutor();
		if (doBackGPicDuplicateCheck(paraMap) == 0) {
			String sql = getSql(SQL_BACKG_PIC_ADD);
			String id = getFlowcode(NameSpaceEnum.T_TOPO_PIC);
			paraMap.put("id", id);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				flag = true;
			}
		} else {
			String sql = getSql(SQL_BACKG_PIC_UPDATE);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				flag = true;
			}
		}
		return flag;
	}
    
    private static String SQL_BACKG_PIC_DUPLICATE_CHECK = "SQL_BACKG_PIC_DUPLICATE_CHECK";
   	public int doBackGPicDuplicateCheck(Map<String,Object> paraMap){
   		SQLExecutor executor = getSqlExecutor();
   		String sql = getSql(SQL_BACKG_PIC_DUPLICATE_CHECK);
   		return executor.executeNameParaQuery(sql, paraMap, String.class).size();
   	} 
   	
    private static String SQL_BACKG_PIC_LIST_BY_PICID = "SQL_BACKG_PIC_LIST_BY_PICID";
   	@SuppressWarnings("unchecked")
	public List<String> doBackGPicByPicId(Map<String,Object> paraMap){
   		SQLExecutor executor = getSqlExecutor();
   		String sql = getSql(SQL_BACKG_PIC_LIST_BY_PICID);
   		return executor.executeNameParaQuery(sql, paraMap, String.class);
   	} 
   	
    private static String SQL_BACKG_PIC_DEL_BY_PICID = "SQL_BACKG_PIC_DEL_BY_PICID";
   	public int doBackGPicDelByPicId(Map<String,Object> paraMap){
   		SQLExecutor executor = getSqlExecutor();
   		String sql = getSql(SQL_BACKG_PIC_DEL_BY_PICID);
   		return executor.executeInsertDeleteUpdate(sql, paraMap);
   	}
   	
   	private static String SQL_BACKG_PIC_DEL_BY_TOPOID = "SQL_BACKG_PIC_DEL_BY_TOPOID";
   	public int doBackGPicDelByTopoId(Map<String,Object> paraMap){
   		SQLExecutor executor = getSqlExecutor();
   		String sql = getSql(SQL_BACKG_PIC_DEL_BY_TOPOID);
   		return executor.executeInsertDeleteUpdate(sql, paraMap);
   	}
   	
    private static String SQL_BACKG_PIC_DETAIL_BY_TOPOID = "SQL_BACKG_PIC_DETAIL_BY_TOPOID";
   	@SuppressWarnings({ "rawtypes" })
	public Map doBackGPicDetailByTopoId(Map<String,Object> paraMap){
   		SQLExecutor executor = getSqlExecutor();
   		String sql = getSql(SQL_BACKG_PIC_DETAIL_BY_TOPOID);
   		Map sqlVo = getSqlVO(SQL_BACKG_PIC_DETAIL_BY_TOPOID);
   		List<Map> resultList =  executor.executeNameParaQuery(sql, paraMap, sqlVo);
   		if(resultList.size()!=0){
   			return resultList.get(0);
   		}else{
   			return null;
   		}
   		
   	}
   	
}
