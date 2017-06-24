package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.ProfDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IProfHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.utils.EncryptionUtil;

public class ProfHandler extends BaseLogicHandler implements IProfHandler {

	@SuppressWarnings("unchecked")
	public IResponseEvent doProfView(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doProfView();
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings("unchecked")
	public IResponseEvent doProfEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = idao.doProfEdit(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public IResponseEvent doProfPswd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = false;
		String curPswd = EncryptionUtil.encrypt((String)param.get("curPswd"));
		String pswd = idao.getProfPswd();
		if(curPswd.equals(pswd)){
			param.put("pswd", EncryptionUtil.encrypt((String)param.get("newPswd")));
			success = idao.doProfChangePswd(param);
		}
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public IResponseEvent doTenantView(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doTenantView();
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings("unchecked")
	public IResponseEvent doTenantEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = idao.doTenantEdit(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings("unchecked")
	public IResponseEvent doPwdReset(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfDAO idao = (ProfDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] result = idao.doPwdReset(param);
		dto.setBoolParam(result[0] != null);
		dto.setStrParam(result[1]);
		response.setDTO(dto);
		return response;
	}

}
