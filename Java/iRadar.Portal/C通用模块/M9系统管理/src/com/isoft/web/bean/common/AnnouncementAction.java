package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.IHostTypeDAO;
import com.isoft.biz.dao.common.IAnnouncementDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IHostTypeHandler;
import com.isoft.biz.handler.common.IAnnouncementHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

public class AnnouncementAction extends IaasPageAction{
	
	private IdentityBean idBean = new IdentityBean();

	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
//	public List doList(){
//	    RequestEvent request = new RequestEvent();
//	    request.setCallHandlerIF(IAnnouncementHandler.class);
//	    request.setCallDAOIF(IAnnouncementDAO.class);
//	    request.setCallHandlerMethod(IAnnouncementHandler.doList);
//        request.setModuleName(ModuleConstants.MODULE_COMMON);
//        request.setDataSource(DataSourceEnum.IRADAR);
//	    IResponseEvent response = delegator(request);
//	    ParamDTO dto = (ParamDTO) response.getDTO();
//	    List results = dto.getListParam();
//	    return results;
//	}
//	public List doListAll(){
//	    RequestEvent request = new RequestEvent();
//	    request.setCallHandlerIF(IAnnouncementHandler.class);
//	    request.setCallDAOIF(IAnnouncementDAO.class);
//	    request.setCallHandlerMethod(IAnnouncementHandler.doListAll);
//        request.setModuleName(ModuleConstants.MODULE_COMMON);
//        request.setDataSource(DataSourceEnum.IRADAR);
//	    IResponseEvent response = delegator(request);
//	    ParamDTO dto = (ParamDTO) response.getDTO();
//	    List results = dto.getListParam();
//	    return results;
//	}
	public List doListOne( Map param){
	    RequestEvent request = new RequestEvent();
	    request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doListOne);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        request.setDataSource(DataSourceEnum.IRADAR);
        ParamDTO paramDTO = new ParamDTO();
    	paramDTO.setMapParam(param);
	  	request.setDTO(paramDTO);
	    IResponseEvent response = delegator(request);
	    ParamDTO dto = (ParamDTO) response.getDTO();
	    List results = dto.getListParam();
	    return results;
	}
	public boolean  doCreate(Map param){
		RequestEvent request = new RequestEvent();
	    request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doCreate);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
	  	paramDTO.setMapParam(param);
	  	request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}
	public boolean  doEnd(Map param){
		RequestEvent request = new RequestEvent();
	    request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doEnd);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
	  	paramDTO.setMapParam(param);
	  	request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}
	public boolean  doStart(Map param){
		RequestEvent request = new RequestEvent();
	    request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doStart);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
	  	paramDTO.setMapParam(param);
	  	request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}
	public boolean  doEdit(Map param){
	    RequestEvent request = new RequestEvent();
	    request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doUpdata);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
	  	paramDTO.setMapParam(param);
	  	request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}
	public boolean doDelete(List<String> list) {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doDelete);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setListParam(list);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}
	public boolean doCease(List<String> list) {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IAnnouncementHandler.class);
	    request.setCallDAOIF(IAnnouncementDAO.class);
	    request.setCallHandlerMethod(IAnnouncementHandler.doCease);
	    request.setModuleName(ModuleConstants.MODULE_COMMON);
	    request.setDataSource(DataSourceEnum.IRADAR);
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setListParam(list);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		boolean success = dto.getBoolParam();
		return success;
	}

}
