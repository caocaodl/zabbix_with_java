package com.isoft.biz.handlerimpl.inventoriesReport;


import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.biz.daoimpl.inventoriesReport.InventoriesReportDAO;
import com.isoft.biz.handler.inventoriesReport.IInventoriesReportHandler;

public class InventoriesReportHandler extends BaseLogicHandler implements IInventoriesReportHandler {

	public IResponseEvent doInventoriesReportPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InventoriesReportDAO idao = (InventoriesReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doInventoriesReportPage(dataPage, param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent  doInventoriesCSV(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InventoriesReportDAO idao = (InventoriesReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doInventoriesCSV(param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
}
