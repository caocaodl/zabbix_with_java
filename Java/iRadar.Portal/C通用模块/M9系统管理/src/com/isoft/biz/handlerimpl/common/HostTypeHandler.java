package com.isoft.biz.handlerimpl.common;

import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.HostTypeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IHostTypeHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.util.StringUtil;

public class HostTypeHandler extends BaseLogicHandler implements
		IHostTypeHandler {

	public IResponseEvent doAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		HostTypeDAO idao = (HostTypeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		Object[] ret = idao.doAdd(param);
		dto.setArrayParam(ret);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUpdate(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		HostTypeDAO idao = (HostTypeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUpdate(param);
		dto.setBoolParam(!StringUtil.isEmpty(ret[0]));
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doDelete(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		HostTypeDAO idao = (HostTypeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		idao.doDelete(param);
		response.setDTO(dto);
		return response;
	}
}
