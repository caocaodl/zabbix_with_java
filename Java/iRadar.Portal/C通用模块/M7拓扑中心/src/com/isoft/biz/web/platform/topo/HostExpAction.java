package com.isoft.biz.web.platform.topo;

import java.util.Map;

import com.isoft.biz.dao.platform.topo.IHostExpDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IHostExpHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class HostExpAction extends IaasPageAction {
	@SuppressWarnings({ "rawtypes" })
	public String doCategoryList() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IHostExpHandler.class);
		request.setCallDAOIF(IHostExpDAO.class);
		request.setCallHandlerMethod(IHostExpHandler.doCategoryList);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultList(dto.getListParam());
		return "resultList";
	}
	
	/**
	 * 资产设备类别列表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes" })
	public String doAssetsCategoryList() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IHostExpHandler.class);
		request.setCallDAOIF(IHostExpDAO.class);
		request.setCallHandlerMethod(IHostExpHandler.doAssetsCategoryList);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultList(dto.getListParam());
		return "resultList";
	}
}
