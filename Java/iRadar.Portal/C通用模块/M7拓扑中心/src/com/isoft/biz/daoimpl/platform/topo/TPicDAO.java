package com.isoft.biz.daoimpl.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ITPicDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.TPic;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.types.Mapper.Nest;

public class TPicDAO extends BaseDAO implements ITPicDAO {

	public TPicDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_T_PIC_PAGE = "SQL_T_PIC_PAGE";
    @SuppressWarnings("rawtypes")
	public List<TPic> doTPicPage(DataPage dataPage,Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_T_PIC_PAGE);
        if(Cphp.isset(paraMap, "name")){
        	String name = TopoUtil.doSpecialCharacters(Nest.as(paraMap.get("name")).asString());
    		Nest.value(paraMap, "name").$(name);
        }
        if(dataPage==null){
        	return (List<TPic>)executor.executeNameParaQuery(sql, paraMap, TPic.class);
        }
        return (List<TPic>)executor.executeNameParaQuery(dataPage,sql, paraMap, TPic.class);
    }
    
    private static final String SQL_T_PIC_LIST = "SQL_T_PIC_LIST";
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List<TPic> doTPicList(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_T_PIC_LIST);
        return executor.executeNameParaQuery(sql, paraMap, TPic.class);	  
    }
    
    private static final String SQL_T_PIC_LOAD_BY_ID = "SQL_T_PIC_LOAD_BY_ID";
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public TPic doTPicLoadByID(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_T_PIC_LOAD_BY_ID);
        List<TPic> tPics = executor.executeNameParaQuery(sql, paraMap, TPic.class);
        if(tPics.size()!=0){
        	return tPics.get(0);
        }
        return null;
    }
    
    private static final String SQL_T_PIC_ADD = "SQL_T_PIC_ADD";
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String[] doTPicAdd(Map paraMap) {
    	String[] ret = new String[2];
        SQLExecutor executor = getSqlExecutor();
        if(doTPicDuplicateCheck(paraMap)==0){
        	 String sql = getSql(SQL_T_PIC_ADD);
             String id = getFlowcode(NameSpaceEnum.T_PIC);
             paraMap.put("id", id);
             if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
             	ret[0] = id;
     			ret[1] = "成功";
             }
        }else{
        	ret[0] = null;
        	ret[1] = "名称已经存在！";
        }
		return ret;
    }
    
    private static String SQL_T_PIC_DUPLICATE_CHECK = "SQL_T_PIC_DUPLICATE_CHECK";
	public int doTPicDuplicateCheck(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_PIC_DUPLICATE_CHECK);
		return executor.executeNameParaQuery(sql, paraMap, String.class).size();
	}
	
	
	
    private static final String SQL_T_PIC_DEL = "SQL_T_PIC_DEL";
    @SuppressWarnings({"rawtypes" })
	public String doTPicDel(Map paraMap) {
    	
    	List<Long> tpIdList = (List<Long>) paraMap.get("tpIdList");
    	for (int i = 0; i < tpIdList.size(); i++) {
    		Long tpId = tpIdList.get(i);
    		//判断要删除的拓扑图片是否为系统原始图片,如果是return异常
    		if(tpId > 100 && tpId < 107){
    			return "error";
    		}
    		Map<String, Object> paramMap = new HashMap<String, Object>();
    		paramMap.put("picid", tpId);
    		//判断要删除的拓扑图片是否被其它模块引用，如果被引用，则顺便删除数据库中的记录
    		if(doTPicReference(paramMap)!=0){
        		doTopoPicDel(paramMap);
        	}
		}
    	
		SQLExecutor executor = getSqlExecutor();
	    String sql = getSql(SQL_T_PIC_DEL);
	    if(executor.executeInsertDeleteUpdate(sql, paraMap) != 0) {
	    	return "true";
	    }
	    return "false";
    }
    
    /**
	 * 判断拓扑图片是否被拓扑模块引用
	 */
	private static String SQL_T_PIC_REFERENCE = "SQL_T_PIC_REFERENCE";
	public int doTPicReference(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_PIC_REFERENCE);
		return executor.executeNameParaQuery(sql, paraMap, String.class).size();
	}
	/**
	 * 删除拓扑图片被引用的记录
	 */
    private static final String SQL_TOPO_PIC_DEL = "SQL_TOPO_PIC_DEL";
    @SuppressWarnings({"rawtypes" })
	public boolean doTopoPicDel(Map paraMap) {
    	SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_PIC_DEL);
		return executor.executeInsertDeleteUpdate(sql, paraMap) != 0;
    }
}
