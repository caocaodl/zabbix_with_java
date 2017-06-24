package com.isoft.biz.handlerimpl.subscribeCurrent;

import java.util.List;
import java.util.Map;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.biz.daoimpl.subscribeCurrent.SubscribeCurrentDAO;
import com.isoft.biz.handler.subscribeCurrent.ISubscribeCurrentHandler;

public class SubscribeCurrentHandler extends BaseLogicHandler implements ISubscribeCurrentHandler {

	public IResponseEvent doSubscribeCurrentPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String startTime =(String) param.get("startTime");
		String endTime =(String) param.get("endTime");
		SubscribeCurrentDAO idao = (SubscribeCurrentDAO) dao;
		List dataList = idao.doSubscribeCurrentPage(startTime,endTime);
		ParamDTO dto = new ParamDTO();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
}
