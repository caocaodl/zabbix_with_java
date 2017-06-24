package com.isoft.biz.handlerimpl.home;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.home.LoginDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.home.ILoginHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.model.PermItem;
import com.isoft.utils.EncryptionUtil;

public class LoginHandler extends BaseLogicHandler implements ILoginHandler {

	public IResponseEvent doLogin(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		LoginDAO idao = (LoginDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		int status = -1;
		List userList = idao.getUser(param);
		if (!userList.isEmpty()) {
			Map user = (Map) userList.get(0);
			String ustatus = (String)user.get("status");
			if ("Y".equals(ustatus)) {
				param.put("tid", user.get("tenantId"));
				List tenantList = idao.getTenant(param);
				if(tenantList.size() > 0){
					Map tenant = (Map) tenantList.get(0);
					String tstatus = (String)tenant.get("status");
					if ("Y".equals(tstatus)) {
						int roleMagic = (Integer) tenant.get("role");
						user.put("tenantRole", roleMagic);
						user.put("osTenantId", tenant.get("osTenantId"));
						String password = (String) param.get("password");
						if (EncryptionUtil.encrypt(password).equals(
								user.get("userPswd"))) {
							idao.updateLastLoginAt(user);
							param.put("tRole", roleMagic);
							param.put("tUserId", user.get("userId"));
							List<PermItem> permSet = idao.getUserPerms(param);
							user.put("permSet", permSet);
							status = 1;
							dto.setMapParam(user);
						} else {
							status = -1;
						}
					} else if("F".equals(tstatus) || "R".equals(tstatus)){
						status = -5;
					} else {
						status = -3;
					}
				} else if("F".equals(ustatus)){
					status = -4;
				}else {
					status = -2;
				}
			}else{
				status = -1;
			}
		}
		dto.setIntParam(status);
		response.setDTO(dto);
		return response;
	}

	
	public IResponseEvent doLoginForSso(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		LoginDAO idao = (LoginDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		int status = -1;
		List userList = idao.getUser(param);
		if (!userList.isEmpty()) {
			Map user = (Map) userList.get(0);
			if ("Y".equals(user.get("status"))) {
				param.put("tid", user.get("tenantId"));
				Map tenant = (Map) idao.getTenant(param).get(0);
				if ("Y".equals(tenant.get("status"))) {
					int roleMagic = (Integer) tenant.get("role");
					user.put("tenantRole", roleMagic);
					user.put("osTenantId", tenant.get("osTenantId"));
					String password = (String) param.get("password");
					if (password.equals(user.get("userPswd"))) {
						idao.updateLastLoginAt(user);
						status = 1;
						dto.setMapParam(user);
					} else {
						status = -1;
					}
				} else {
					status = -3;
				}
			} else {
				status = -2;
			}
		}
		dto.setIntParam(status);
		response.setDTO(dto);
		return response;
	}
}
