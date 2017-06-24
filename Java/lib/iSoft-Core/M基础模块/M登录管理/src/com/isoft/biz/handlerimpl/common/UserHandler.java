package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.UserDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IUserHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class UserHandler extends BaseLogicHandler implements IUserHandler {

	public IResponseEvent doUserPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doUserPage(dataPage, param);
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		populateUserEntry(dataList, "deletedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doUserView(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doUserView(param);
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		populateUserEntry(dataList, "deletedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doUserAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserAdd(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doUserEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserEdit(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doUserDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserDel(param);
		if(ret[0] != null){
			param.put("userId", param.get("id"));
			idao.doDeleteRoles(param);
		}
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUserActive(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserActive(param);
		if(ret[0] != null){
			param.put("userId", param.get("id"));
			idao.doDeleteRoles(param);
		}
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUserForbid(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserForbid(param);
		if(ret[0] != null){
			param.put("userId", param.get("id"));
			idao.doDeleteRoles(param);
		}
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUserResume(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUserResume(param);
		if(ret[0] != null){
			param.put("userId", param.get("id"));
			idao.doDeleteRoles(param);
		}
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent getAllRoleSet(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = new LinkedMap();
		String userId = paramDTO.getStrParam();
		param.put("uid", userId);
		ParamDTO dto = new ParamDTO();
		List<Map> dataList = idao.getAllRoleSet(param);
		List<String> funcList = idao.getRoles(param);
		for(String funcId:funcList){
			for(Map func:dataList){
				if(funcId.startsWith((String)func.get("id"))){
					func.put("checked", true);
				}
			}
		}
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUserGrantRoles(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("uid", param.get("userId"));
		ParamDTO dto = new ParamDTO();
		idao.doDeleteRoles(param);
		idao.doGrantRoles(param);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUserViewByName(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		UserDAO idao = (UserDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doUserViewForUserName(param);
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		populateUserEntry(dataList, "deletedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

}
