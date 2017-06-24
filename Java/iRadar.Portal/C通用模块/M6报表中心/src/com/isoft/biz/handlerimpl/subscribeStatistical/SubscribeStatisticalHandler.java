package com.isoft.biz.handlerimpl.subscribeStatistical;

import java.util.List;
import java.util.Map;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.biz.daoimpl.subscribeStatistical.SubscribeStatisticalDAO;
import com.isoft.biz.handler.subscribeStatistical.ISubscribeStatisticalHandler;

public class SubscribeStatisticalHandler extends BaseLogicHandler implements ISubscribeStatisticalHandler {

	public IResponseEvent doSubscribeStatisticalPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		SubscribeStatisticalDAO idao = (SubscribeStatisticalDAO) dao;
		List dataList = idao.doSubscribeStatisticalPage();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
}
