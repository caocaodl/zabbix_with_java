package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.isoft.biz.dao.common.ITenantDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ITenantHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class TenantAction extends IaasPageAction {

	public String doPage() throws Exception {
		
        RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(ITenantHandler.class);
        request.setCallDAOIF(ITenantDAO.class);
        request.setCallHandlerMethod(ITenantHandler.doTenantPage);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        DataPage dataPage = new DataPage(true, getPage(),getRows());
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        paramDTO.setDataPage(dataPage);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        List dataList = dto.getListParam();
        setResultList(dataList);
        setDataPage(dataPage);
		return JSON;
	}
	
	public String doOperAdd() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(ITenantHandler.class);
        request.setCallDAOIF(ITenantDAO.class);
        request.setCallHandlerMethod(ITenantHandler.doTenantAdd);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}

	public String doOperDel() {
		boolean success = false;
		String tenantId = getParameter("id");
		if(!StringUtils.isEmpty(tenantId)){
			RequestEvent request = new RequestEvent();
	        request.setCallHandlerIF(ITenantHandler.class);
	        request.setCallDAOIF(ITenantDAO.class);
	        request.setCallHandlerMethod(ITenantHandler.doTenantDel);
	        request.setModuleName(ModuleConstants.MODULE_COMMON);
	        
	        ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
	
	        IResponseEvent response = delegator(request);
	        ParamDTO dto = (ParamDTO)response.getDTO();
	        success = dto.getBoolParam();
		}
        getResultMap().put(SUCCESS, success);
        return "resultMap";
	}

	public String doOperEdit() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(ITenantHandler.class);
        request.setCallDAOIF(ITenantDAO.class);
        request.setCallHandlerMethod(ITenantHandler.doTenantEdit);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}

	public String doOperActive() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(ITenantHandler.class);
        request.setCallDAOIF(ITenantDAO.class);
        request.setCallHandlerMethod(ITenantHandler.doTenantActive);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
        getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}
	
	public String doOperForbid() {
		boolean success = false;
		String tenantId = getParameter("id");
		if(!StringUtils.isEmpty(tenantId)){
			RequestEvent request = new RequestEvent();
	        request.setCallHandlerIF(ITenantHandler.class);
	        request.setCallDAOIF(ITenantDAO.class);
	        request.setCallHandlerMethod(ITenantHandler.doTenantForbid);
	        request.setModuleName(ModuleConstants.MODULE_COMMON);
	        
	        ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
	
	        IResponseEvent response = delegator(request);
	        ParamDTO dto = (ParamDTO)response.getDTO();
	        success = dto.getBoolParam();
		}
        getResultMap().put(SUCCESS, success);
        return "resultMap";
	}
	
	public String doOperResume() {
		boolean success = false;
		String tenantId = getParameter("id");
		if(!StringUtils.isEmpty(tenantId)){
			RequestEvent request = new RequestEvent();
	        request.setCallHandlerIF(ITenantHandler.class);
	        request.setCallDAOIF(ITenantDAO.class);
	        request.setCallHandlerMethod(ITenantHandler.doTenantResume);
	        request.setModuleName(ModuleConstants.MODULE_COMMON);
	        
	        ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
	
	        IResponseEvent response = delegator(request);
	        ParamDTO dto = (ParamDTO)response.getDTO();
	        success = dto.getBoolParam();
		}
        getResultMap().put(SUCCESS, success);
        return "resultMap";
	}
	
	public String doOperRelease() {
		boolean success = false;
		String tenantId = getParameter("id");
		String osTenantId = getParameter("osTenantId");
		if(!StringUtils.isEmpty(tenantId)&&!StringUtils.isEmpty(osTenantId)){
			RequestEvent request = new RequestEvent();
	        request.setCallHandlerIF(ITenantHandler.class);
	        request.setCallDAOIF(ITenantDAO.class);
	        request.setCallHandlerMethod(ITenantHandler.doTenantRelease);
	        request.setModuleName(ModuleConstants.MODULE_COMMON);
	        
	        ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
	
	        IResponseEvent response = delegator(request);
	        ParamDTO dto = (ParamDTO)response.getDTO();
	        success = dto.getBoolParam();
		}
        getResultMap().put(SUCCESS, success);
        return "resultMap";
	}
		
}
