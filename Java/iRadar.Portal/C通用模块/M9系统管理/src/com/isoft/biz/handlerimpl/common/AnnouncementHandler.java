package com.isoft.biz.handlerimpl.common;


import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IAnnouncementHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;


public class AnnouncementHandler extends BaseLogicHandler implements  IAnnouncementHandler{
        
//	public IResponseEvent doList(IIdentityBean identityBean,
//			IRequestEvent request, IDAO dao) {
//		IResponseEvent response = new ResponseEvent();
//		AnnouncementDAO idao = (AnnouncementDAO) dao;
//		ParamDTO dto = new ParamDTO();
//		List ret = idao.doList();
//		dto.setListParam(ret);
//		response.setDTO(dto);
//		return response;
//	}
//	public IResponseEvent doListAll(IIdentityBean identityBean,
//			IRequestEvent request, IDAO dao) {
//		IResponseEvent response = new ResponseEvent();
//		AnnouncementDAO idao = (AnnouncementDAO) dao;
//		ParamDTO dto = new ParamDTO();
//		List ret = idao.doList();
//		dto.setListParam(ret);
//		response.setDTO(dto);
//		return response;
//	}
	public IResponseEvent doListOne(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		List ret = idao.doListOne(param);
		dto.setListParam(ret);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doUpdata(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = idao.doUpdata(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doCreate(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean success = idao.doCreate(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doDelete(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		List param = paramDTO.getListParam();
		ParamDTO dto = new ParamDTO();
		boolean success =idao.doDelete(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doStart(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		boolean success =idao.doStart(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doEnd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		boolean success =idao.doEnd(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent doCease(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		AnnouncementDAO idao = (AnnouncementDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		List param = paramDTO.getListParam();
		ParamDTO dto = new ParamDTO();
		boolean success =idao.doCease(param);
		dto.setBoolParam(success);
		response.setDTO(dto);
		return response;
	}
	
}
