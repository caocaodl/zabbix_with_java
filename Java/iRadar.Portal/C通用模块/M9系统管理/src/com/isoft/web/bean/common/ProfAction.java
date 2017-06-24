package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.common.IProfDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IProfHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class ProfAction extends IaasPageAction {

	@Override
	public String doIndex() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfHandler.class);
		request.setCallDAOIF(IProfDAO.class);
		request.setCallHandlerMethod(IProfHandler.doProfView);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		if (!dataList.isEmpty()) {
			this.setVo((Map) dataList.get(0));
		}
		return SUCCESS;
	}

	public String doOperEdit() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfHandler.class);
		request.setCallDAOIF(IProfDAO.class);
		request.setCallHandlerMethod(IProfHandler.doProfEdit);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		getResultMap().put(SUCCESS, success);
		return "resultMap";
	}
	
	public String doPswd() {
		return "pswd";
	}
	
	public String doOperPswd() {
		Map param = getVo();
		if(StringUtils.isEmpty((String)param.get("curPswd"))
		   ||StringUtils.isEmpty((String)param.get("newPswd"))){
			getResultMap().put(SUCCESS, false);
		} else {
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IProfHandler.class);
			request.setCallDAOIF(IProfDAO.class);
			request.setCallHandlerMethod(IProfHandler.doProfPswd);
			request.setModuleName(ModuleConstants.MODULE_COMMON);

			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			boolean success = dto.getBoolParam();
			getResultMap().put(SUCCESS, success);
		}
		return "resultMap";
	}

	public String doTenant() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfHandler.class);
		request.setCallDAOIF(IProfDAO.class);
		request.setCallHandlerMethod(IProfHandler.doTenantView);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		if (!dataList.isEmpty()) {
			this.setVo((Map) dataList.get(0));
		}
		return "tenant";
	}

	public String doTenantEdit() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfHandler.class);
		request.setCallDAOIF(IProfDAO.class);
		request.setCallHandlerMethod(IProfHandler.doTenantEdit);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		getResultMap().put(SUCCESS, success);
		return "resultMap";
	}

	public String doForgotPwd() {
		return "forgotPwd";
	}

	public String doPwdReset() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfHandler.class);
		request.setCallDAOIF(IProfDAO.class);
		request.setCallHandlerMethod(IProfHandler.doPwdReset);
		request.setModuleName(ModuleConstants.MODULE_COMMON);
		request.setCheckLogin(false);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		getResultMap().put(SUCCESS, false);
		if(param.get("tenantId") == null || StringUtils.isEmpty((String)param.get("tenantId"))){
			getResultMap().put("error", "企业账号不能为空");
		}else if(param.get("userName") == null || StringUtils.isEmpty((String)param.get("userName"))){
			getResultMap().put("error", "用户名称不能为空");
		}else if(param.get("email") == null || StringUtils.isEmpty((String)param.get("email"))){
			getResultMap().put("error", "邮箱地址不能为空");
		}else{
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
	
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			getResultMap().put(SUCCESS, dto.getBoolParam());
			getResultMap().put("error", dto.getStrParam());
		
		}
		return "resultMap";
	}
}
