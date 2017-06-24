package com.isoft.biz.handlerimpl.portserves;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.portserves.EventDataInterfaceDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IEventDataInterfaceHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class EventDataInterfaceHandler extends BaseLogicHandler implements
		IEventDataInterfaceHandler {
	
	public IResponseEvent doList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		EventDataInterfaceDAO idao = (EventDataInterfaceDAO) dao;
		List dataList = idao.doList(param);
		ParamDTO dto = new ParamDTO();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
}
