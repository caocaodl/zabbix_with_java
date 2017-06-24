package com.isoft.biz.handlerimpl.portserves;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.portserves.ProfServesDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IProfServesHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class ProfServesHandler extends BaseLogicHandler implements IProfServesHandler {

	public IResponseEvent doProtServer(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfServesDAO idao = (ProfServesDAO) dao;
		ParamDTO dto = new ParamDTO();
		
		List dataList = idao.doProtServer();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doInterfaceServer(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ProfServesDAO idao = (ProfServesDAO) dao;
		String userid = (String) param.get("userid");
		boolean success = idao.doInterfaceServer(userid);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doInterfaceServerDelect(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ProfServesDAO idao = (ProfServesDAO) dao;
		String userid = (String) param.get("userid");
		boolean success = idao.doInterfaceServerDelect(userid);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doFind(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ProfServesDAO idao = (ProfServesDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		Map param = paramDTO.getMapParam();
		String userid = (String) param.get("userid");		
		List dataList = idao.doFind(userid);
	
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	

}
