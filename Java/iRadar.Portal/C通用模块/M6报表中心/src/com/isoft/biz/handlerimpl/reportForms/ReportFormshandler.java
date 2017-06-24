package com.isoft.biz.handlerimpl.reportForms;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;


import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.reportForms.ReportFormsDAO;

import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IRoleHandler;
import com.isoft.biz.handler.reportForms.IReportFormshandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class ReportFormshandler extends BaseLogicHandler implements IReportFormshandler {
	//获取所有设备数据
	public IResponseEvent doStatement(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doForm();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取所有os类型数据
	public IResponseEvent doStatementOs(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doFormOs();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取设备组
	public IResponseEvent doEquipment(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doGroup();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取所有的OS类型
	public IResponseEvent doOs(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doOs();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取单个设备组的数量
	public IResponseEvent doEquipmentSingle(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String groupid = (String) param.get("groupid");		
		List dataList = idao.doSingleGroup(groupid);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取单个OS的数量
	public IResponseEvent doStatementSingleOs(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String hostid = (String) param.get("hostid");		
		List dataList = idao.doSingleGroupOs(hostid);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取其他设备的数量
	public IResponseEvent doEquipmentSingleElse(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String groupid = (String) param.get("groupid");		
		List dataList = idao.doEquipmentSingleElse(groupid);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	//获取其他OS类型的数量
	public IResponseEvent doOsSingleElse(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportFormsDAO idao = (ReportFormsDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String hostid = (String) param.get("hostid");		
		List dataList = idao.doOsSingleElse(hostid);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
}
