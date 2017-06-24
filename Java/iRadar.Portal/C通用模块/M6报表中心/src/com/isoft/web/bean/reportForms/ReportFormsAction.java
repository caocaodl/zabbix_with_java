package com.isoft.web.bean.reportForms;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.reportForms.IReportFormsDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.reportForms.IReportFormshandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.types.CArray;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

public class ReportFormsAction extends IaasPageAction {

	
      //获得设备饼图数据	
	public String getStatement(){
	
		    RequestEvent request = new RequestEvent();
		    request.setCallHandlerIF(IReportFormshandler.class);
		    request.setCallDAOIF(IReportFormsDAO.class);
		    request.setCallHandlerMethod(IReportFormshandler.doStatement);
	        request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	        request.setDataSource(DataSourceEnum.IRADAR);
	    
		    IResponseEvent response = delegator(request);
		    ParamDTO dto = (ParamDTO) response.getDTO();
		    List results = dto.getListParam();
		    setResultList(results);
		    return "resultList";
	}
	//获取OS类型的饼图数据
	public String getStatementOs(){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IReportFormshandler.class);
			request.setCallDAOIF(IReportFormsDAO.class);
			request.setCallHandlerMethod(IReportFormshandler.doStatementOs);
		    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List results = dto.getListParam();
			setResultList(results);
			return "resultList";
		}
	//获取单个设备的饼图数据
	public String getStatementSingle(){
		   //获得当前设备所占的数量
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IReportFormshandler.class);
			request.setCallDAOIF(IReportFormsDAO.class);
			request.setCallHandlerMethod(IReportFormshandler.doEquipmentSingle);
		    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List results = dto.getListParam();
			//货期除当前设备的其他设备所占的数量
			RequestEvent request1= new RequestEvent();
			request1.setCallHandlerIF(IReportFormshandler.class);
			request1.setCallDAOIF(IReportFormsDAO.class);
			request1.setCallHandlerMethod(IReportFormshandler.doEquipmentSingleElse);
		    request1.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request1.setDataSource(DataSourceEnum.IRADAR);
		   
	        request1.setDTO(paramDTO);
			IResponseEvent response1 = delegator(request1);
			ParamDTO dto1 = (ParamDTO) response1.getDTO();
			List results1 = dto1.getListParam();
			results.add(results1.get(0));
			setResultList(results);
			return "resultList";
		}
	     //获取单个OS类型的饼图数据
	public String getStatementSingleOs(){
	        //获取当前OS类型所占的数量 
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IReportFormshandler.class);
			request.setCallDAOIF(IReportFormsDAO.class);
			request.setCallHandlerMethod(IReportFormshandler.doStatementSingleOs);
		    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    ParamDTO paramDTO = new ParamDTO();
	        Map param = getVo();
	        paramDTO.setMapParam(param);
	        request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List results = dto.getListParam();
			//获取其他OS类型所占的数量
			RequestEvent request1= new RequestEvent();
			request1.setCallHandlerIF(IReportFormshandler.class);
			request1.setCallDAOIF(IReportFormsDAO.class);
			request1.setCallHandlerMethod(IReportFormshandler.doOsSingleElse);
		    request1.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request1.setDataSource(DataSourceEnum.IRADAR);
		   
	        request1.setDTO(paramDTO);
			IResponseEvent response1 = delegator(request1);
			ParamDTO dto1 = (ParamDTO) response1.getDTO();
			List results1 = dto1.getListParam();
			results.add(results1.get(0));
			setResultList(results);
			return "resultList";
		}
	//得到所有的设备组
	public String getEquipment(){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IReportFormshandler.class);
			request.setCallDAOIF(IReportFormsDAO.class);
			request.setCallHandlerMethod(IReportFormshandler.doEquipment);
		    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List results = dto.getListParam();
			
			CArray status = new CArray();
			status.put("groupid", 0);
			status.put("name", "所有");
			results.add(status);
			setResultList(results);
			return "resultList";
		}
	//得到所有的OS类型
	public String getCompleteOs(){
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IReportFormshandler.class);
		request.setCallDAOIF(IReportFormsDAO.class);
		request.setCallHandlerMethod(IReportFormshandler.doOs);
	    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List results = dto.getListParam();
		
		CArray status = new CArray();
		status.put("hostid", 0);
		status.put("name", "所有");
		results.add(status);
		setResultList(results);
		return "resultList";
	}
	
//	public String list(){
//	   return "json";
//	}
	
}
