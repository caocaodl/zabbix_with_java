package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.BackGPicDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IBackGPicHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.imon.topo.util.TopoUtil;


public class BackGPicHandler extends BaseLogicHandler implements IBackGPicHandler {

	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doBackGPicList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getOsTenantId());
		param.put("userId", identityBean.getUserId());
		BackGPicDAO idao = (BackGPicDAO)dao;
		List<Map> dataList = idao.doBackGPicList(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({"rawtypes"})
	public IResponseEvent doBackGPicChange(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		BackGPicDAO idao = (BackGPicDAO)dao;
	    boolean flag = idao.doBackGPicChange(param);

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		response.setDTO(dto);
		return response;
	}
	
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doBackGPicInit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getOsTenantId());
		param.put("userId", identityBean.getUserId());
		BackGPicDAO idao = (BackGPicDAO)dao;
		Map resultMap = idao.doBackGPicDetailByTopoId(param);
		if(resultMap == null){
			resultMap = new HashMap();
			resultMap.put("id",TopoUtil.DEFAULT_BACKGROUPPIC_ID );
			resultMap.put("src", "");
			resultMap.put("width", 2500);
			resultMap.put("height", 2000);
		}
		
		ParamDTO dto = new ParamDTO();
		dto.setMapParam(resultMap);
		response.setDTO(dto);
		return response;
	}
}
