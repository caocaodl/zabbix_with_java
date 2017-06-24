package com.isoft.biz.handlerimpl.reportForms;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.reportForms.ReportFormsDAO;
import com.isoft.biz.daoimpl.reportForms.ReportTopnUseDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.reportForms.IReportFormshandler;
import com.isoft.biz.handler.reportForms.IReportTopnUsehandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class ReportTopnUsehandler  extends BaseLogicHandler implements IReportTopnUsehandler {
	
	public IResponseEvent getChars(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportTopnUseDAO idao = (ReportTopnUseDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String host = (String) param.get("host");
		String timeUp = (String) param.get("timeUp");
		String timeDown = (String) param.get("timeDown");
		List dataList = idao.getChars(host, timeUp, timeDown);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	public IResponseEvent numbuerLimit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		ReportTopnUseDAO idao = (ReportTopnUseDAO) dao;
		ParamDTO dto = new ParamDTO();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String host = (String) param.get("host");
		String limt = (String) param.get("limt");
		List dataList = idao.numbuerLimit(host, limt);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	

}
