package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.RoleDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IRoleHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class RoleHandler extends BaseLogicHandler implements IRoleHandler {

	public IResponseEvent doRolePage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doRolePage(dataPage, param);
		populateUserEntry(dataList, "createdUser", idao);
		populateUserEntry(dataList, "modifiedUser", idao);
		populateUserEntry(dataList, "deletedUser", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doRoleAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		if(!idao.doRoleDuplicateCheck(param)){
			dto.setBoolParam(false);
			dto.setStrParam("角色名称已经存在！");
		}else{
			String[] ret = idao.doRoleAdd(param);
			dto.setBoolParam(ret[0] != null);
			dto.setStrParam(ret[1]);
		}
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doRoleEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doRoleEdit(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doRoleDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doRoleDel(param);
		dto.setBoolParam(ret[0] != null);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent getFuncs(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		String roleId = paramDTO.getStrParam();
		Map param = new LinkedMap();
		param.put("roleId", roleId);
		ParamDTO dto = new ParamDTO();
		List dataList = idao.getFuncs(param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent getAllFuncSet(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = new LinkedMap();
		String roleId = paramDTO.getStrParam();
		param.put("roleId", roleId);
		ParamDTO dto = new ParamDTO();
		List<Map> dataList = idao.getAllFuncSet(param);
		List<String> funcList = idao.getFuncs(param);
		
	    for(Map func:dataList){
			for(String funcId:funcList){
			  if(funcId.equals((String)func.get("id"))){
				  
				 func.put("checked", true);
			  }
		    }
		}
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doRoleGrantFuncs(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		RoleDAO idao = (RoleDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		idao.doDeleteFuncs(param);
		idao.doGrantFuncs(param);
		response.setDTO(dto);
		return response;
	}
}
