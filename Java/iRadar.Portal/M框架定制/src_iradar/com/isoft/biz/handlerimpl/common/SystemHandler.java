package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ISystemHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class SystemHandler extends BaseLogicHandler implements ISystemHandler {
	
	/**
	 * 查询
	 * @param identityBean
	 * @param request
	 * @param dao
	 * @return
	 */
	public IResponseEvent doSystem(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		SystemDAO idao = (SystemDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		List dataList = idao.doSystem(param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	/**
	 * 增加
	 * @param identityBean
	 * @param request
	 * @param dao
	 * @return
	 */
	public IResponseEvent doAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		SystemDAO idao = (SystemDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		Object[] ret = idao.doAdd(param);
		dto.setArrayParam(ret);
		response.setDTO(dto);
		return response;
	}
	
	/**
	 * 修改
	 * @param identityBean
	 * @param request
	 * @param dao
	 * @return
	 */
	public IResponseEvent doUpdate(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		SystemDAO idao = (SystemDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		Object[] ret = idao.doUpdate(param);
		dto.setArrayParam(ret);
		response.setDTO(dto);
		return response;
	}
	
	/**
	 * 删除
	 * @param identityBean
	 * @param request
	 * @param dao
	 * @return
	 */
	public IResponseEvent doDelete(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		SystemDAO idao = (SystemDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean result = idao.doDelete(param);
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doSysRelationGet(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		SystemDAO idao = (SystemDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean result = idao.doSysRelationGet(param);
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
}
