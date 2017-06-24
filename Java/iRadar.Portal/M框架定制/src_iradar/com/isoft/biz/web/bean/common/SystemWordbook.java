package com.isoft.biz.web.bean.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.ISystemDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ISystemHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class SystemWordbook extends IaasPageAction {
	
	public SystemWordbook(){
		
	}
	
	/**
	 * 获取全部操作系统类型
	 * @param paraMap
	 * @return
	 */
	public List doAll(Map paraMap){
		
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ISystemHandler.class);
		request.setCallDAOIF(ISystemDAO.class);
		request.setCallHandlerMethod(ISystemHandler.doSystem);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    
	    ParamDTO paramDTO = new ParamDTO();
	    paramDTO.setMapParam(paraMap);
        request.setDTO(paramDTO);
        
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List results = dto.getListParam();
		return results;
	}
	
	/**
	 * 添加操作系统类型
	 * @param paraMap
	 * @return
	 */
	public Object[] doAdd(Map paraMap){
		
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ISystemHandler.class);
		request.setCallDAOIF(ISystemDAO.class);
		request.setCallHandlerMethod(ISystemHandler.doAdd);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    
	    ParamDTO paramDTO = new ParamDTO();
	    paramDTO.setMapParam(paraMap);
        request.setDTO(paramDTO);
        
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		Object[] obj = dto.getArrayParam();
		return obj;
	}
	
	/**
	 * 修改操作系统类型
	 * @param paraMap
	 * @return
	 */
	public Object[] doUpdate(Map paraMap){
		
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ISystemHandler.class);
		request.setCallDAOIF(ISystemDAO.class);
		request.setCallHandlerMethod(ISystemHandler.doUpdate);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    
	    ParamDTO paramDTO = new ParamDTO();
	    paramDTO.setMapParam(paraMap);
        request.setDTO(paramDTO);
        
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		Object[] obj = dto.getArrayParam();
		return obj;
	}
	
	/**
	 * 删除操作系统类型
	 * @param paraMap
	 * @return
	 */
	public boolean doDelete(Map paraMap){
		
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ISystemHandler.class);
		request.setCallDAOIF(ISystemDAO.class);
		request.setCallHandlerMethod(ISystemHandler.doDelete);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    
	    ParamDTO paramDTO = new ParamDTO();
	    paramDTO.setMapParam(paraMap);
        request.setDTO(paramDTO);
        
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		return dto.getBoolParam();
	}
	
	public boolean doSysRelationGet(Map paraMap){
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ISystemHandler.class);
		request.setCallDAOIF(ISystemDAO.class);
		request.setCallHandlerMethod(ISystemHandler.doSysRelationGet);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    
	    ParamDTO paramDTO = new ParamDTO();
	    paramDTO.setMapParam(paraMap);
        request.setDTO(paramDTO);
        
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		return dto.getBoolParam();
	}
}
