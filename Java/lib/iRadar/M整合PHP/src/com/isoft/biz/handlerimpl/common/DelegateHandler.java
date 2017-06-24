package com.isoft.biz.handlerimpl.common;

import com.isoft.biz.Delegator;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.DelegateDTO;
import com.isoft.biz.handler.common.IDelegateHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class DelegateHandler extends BaseLogicHandler implements IDelegateHandler {
	
	public IResponseEvent doDelegate(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		DelegateDTO dto = (DelegateDTO) request.getDTO();
		Delegator delegator = (Delegator)dto.getObjParam();
		dto = new DelegateDTO();
		try {
			Object ret = delegator.doDelegate(identityBean, dao.getSqlExecutor());
			dto.setObjParam(ret);
		} catch (Exception e) {
			dto.setException(e);
		}
		response.setDTO(dto);
		return response;
	}
}
