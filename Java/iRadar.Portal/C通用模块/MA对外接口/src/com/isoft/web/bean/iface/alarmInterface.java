package com.isoft.web.bean.iface;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.isoft.biz.dao.portserves.IalarmInterfaceDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IalarmInterfaceHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

@Path("/alarm")  
public class alarmInterface extends IaasPageAction{
	private IdentityBean idBean = new IdentityBean();

	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
	    @Path("/push")  
	    @POST  
	    @Consumes("application/x-www-form-urlencoded")   
	    @Produces("text/plain")  
	    public String doalarmInterface(
	    		@FormParam("time") String time,  //告警时间
	    		@FormParam("ponderance") String ponderance, //严重性
	    		@FormParam("ip") String ip ,  //设备
	    		@FormParam("describe") String describe   //告警描述
	    		){
	    	String value="";
	    	RequestEvent request = new RequestEvent();
	    	request.setCallDAOIF(IalarmInterfaceDAO.class);
			request.setCallHandlerIF(IalarmInterfaceHandler.class);
			request.setCallHandlerMethod(IalarmInterfaceHandler.doalarmInterface);
			request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
			//request.setDataSource(DataSourceEnum.IRADAR);
			ParamDTO paramDTO = new ParamDTO();
			Map param = getVo();
			param.put("time", time);
			param.put("ponderance",ponderance);
			param.put("ip", ip);
			param.put("describe", describe);
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			boolean success = dto.getBoolParam();
	        if(success){
	        	value="推送成功";
	        }else{
	        	value="推送失败";
	        	
	        }  
			return value.toString();
			
			
	    }
	   
	
}
