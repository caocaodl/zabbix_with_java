package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.TopoDataOperDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ITopoDataOperHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class TopoDataOperHandler extends BaseLogicHandler implements ITopoDataOperHandler {

	public IResponseEvent doTopoDataLocOperSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		boolean result = idao.doTopoDataLocOperSave(identityBean);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doCabTopoCabinetDataLocSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		boolean result = idao.doCabTopoCabinetDataLocSave();
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doCabTopoHostDataLocSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		boolean result = idao.doCabTopoHostDataLocSave();
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doPhyTopoTbnailSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		String result = idao.doPhyTopoTbnailSave();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doPhyTopoTbnailDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDto = (ParamDTO) request.getDTO();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		List<Map> result = idao.doPhyTopoTbnailDel(paramDto.getMapParam());
		ParamDTO dto = new ParamDTO();
		dto.setListParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doPhyTopoLocSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		boolean result = idao.doPhyTopoLocSave();
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doBizTopoLocSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		TopoDataOperDAO idao = (TopoDataOperDAO) dao;
		boolean result = idao.doBizTopoLocSave();
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
		response.setDTO(dto);
		return response;
	}
	
}
