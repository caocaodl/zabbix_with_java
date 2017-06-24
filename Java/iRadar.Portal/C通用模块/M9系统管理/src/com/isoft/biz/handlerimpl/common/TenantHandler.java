package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dao.common.ITenantDAO;
import com.isoft.biz.daoimpl.common.TenantDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ITenantHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class TenantHandler extends BaseLogicHandler implements ITenantHandler {

	public IResponseEvent doTenantPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doTenantPage(dataPage, param);
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		populateUserEntry(dataList, "deletedUser", idao);
		populateUserEntry(dataList, "statusUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTenantAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doTenantAdd(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTenantEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doTenantEdit(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTenantDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = idao.doTenantDel(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTenantActive(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String tenantId = (String) param.get("id");		
		String[] ret = idao.doTenantActive(tenantId, true);
		dto.setBoolParam(!StringUtils.isEmpty(ret[0]));
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doAddOsTenantIdInExistTenant(
			IIdentityBean identityBean, IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean result = idao.doSaveOsTenantId(identityBean, param);
		// idao.doOsTenantId(identityBean, param);
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTenantForbid(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String tenantId = (String) param.get("id");
		boolean success = idao.doTenantForbid(tenantId);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTenantResume(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String tenantId = (String) param.get("id");
		boolean success = idao.doTenantResume(tenantId);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings("unchecked")
	public IResponseEvent doTenantRelease(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		boolean flag = false;
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String tenantId = (String) param.get("id");
		boolean success = idao.doTenantRelease(tenantId);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doOSTenantIdViewByTenantId(
			IIdentityBean identityBean, IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		TenantDAO idao = (TenantDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doTenantViewByTenantId(param);
		// populateUserEntry(dataList, "createdUser", idao);
		// populateUserEntry(dataList, "modifiedUser", idao);
		// populateUserEntry(dataList, "deletedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

}
