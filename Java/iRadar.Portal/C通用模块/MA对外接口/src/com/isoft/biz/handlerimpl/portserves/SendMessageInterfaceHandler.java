package com.isoft.biz.handlerimpl.portserves;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.ISendMessageInterfaceHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class SendMessageInterfaceHandler extends BaseLogicHandler implements ISendMessageInterfaceHandler{
	
	public IResponseEvent doSendMessageTel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		  return response;
	}
}
