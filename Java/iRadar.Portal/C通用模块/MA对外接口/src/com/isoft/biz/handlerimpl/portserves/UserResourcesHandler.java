package com.isoft.biz.handlerimpl.portserves;

import java.util.List;
import java.util.Map;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.portserves.UserResourcesDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IProfServesHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class UserResourcesHandler extends BaseLogicHandler implements IProfServesHandler {

	public IResponseEvent doResourceUser(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		
		UserResourcesDAO idao = (UserResourcesDAO) dao;
		List userlist = idao.doResourceUser();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(userlist);
		response.setDTO(dto);
		return response;
	}

}
