package com.isoft.biz.handlerimpl.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.LogDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ILogHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.model.PermItem;
import com.isoft.utils.CacheUtil;

public class LogHandler extends BaseLogicHandler implements ILogHandler {

	public IResponseEvent doLogPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		LogDAO idao = (LogDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List<Map> dataList = idao.doLogPage(dataPage, param);
		if(dataList.size() > 0){
			for(int i=0; i<dataList.size(); i++){
				List<PermItem> permList =  CacheUtil.getPermByViewId((String) dataList.get(i).get("requestUri"));
				String funcId = (String)dataList.get(i).get("funcId");
				if(permList != null){
					for(PermItem bt : permList){
						if(bt.getFuncId().equals(funcId)){
							//dataList.get(i).put("funcName", bt.getFuncName());
							dataList.get(i).put("funcMenu", bt.getAlias());
							break;
						}
					}
				}
			}
		}
		populateUserEntry(dataList, "userId", idao);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doLogAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		LogDAO idao = (LogDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String id = idao.doLogAdd(param);
		dto.setStrParam(id);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doLogRequest(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		LogDAO idao = (LogDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		idao.doLogRequest(param);
		response.setDTO(dto);
		return response;
	}
}
