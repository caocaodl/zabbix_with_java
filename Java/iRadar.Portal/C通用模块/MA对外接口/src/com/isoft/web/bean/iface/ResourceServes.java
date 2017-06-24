package com.isoft.web.bean.iface;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.isoft.biz.dao.portserves.IProfServesDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IProfServesHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;


@Path("/servers") 
 
public class ResourceServes extends IaasPageAction{
	
	private IdentityBean idBean = new IdentityBean();

	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
	@Path("/subscribe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public List getUser() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IProfServesHandler.class);
		request.setCallDAOIF(IProfServesDAO.class);
		request.setCallHandlerMethod(IProfServesHandler.doProtServer);
	    request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
	    request.setDataSource(DataSourceEnum.IRADAR);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List results = dto.getListParam();
		//setResultList(results);
		return results;     
    	//return new CarModel("0", "BMW");
   }

	
	    @Path("/subscribe")  
	    @POST  
	    @Consumes("application/x-www-form-urlencoded")   
	    @Produces("text/plain")  
	    public String doInterfaceServer(@FormParam("userid") String userid){
	    	String value=null;
	    	RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IProfServesHandler.class);
			request.setCallDAOIF(IProfServesDAO.class);
			request.setCallHandlerMethod(IProfServesHandler.doFind);
		    request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    Map param=getVo();
		    param.put("userid", userid);
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
		    List results = dto.getListParam();
			if(results.size()>0){
		    	RequestEvent request1 = new RequestEvent();
				request1.setCallHandlerIF(IProfServesHandler.class);
				request1.setCallDAOIF(IProfServesDAO.class);
				request1.setCallHandlerMethod(IProfServesHandler.doInterfaceServer);
			    request1.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
			    request1.setDataSource(DataSourceEnum.IRADAR);
			    request1.setDTO(paramDTO);
				IResponseEvent response1 = delegator(request1);
				ParamDTO dto1 = (ParamDTO) response1.getDTO();
				boolean success = dto1.getBoolParam();
			    value="恭喜訂閱成功";
		        }else{
				
			    value="用戶不存在";
				
			    }

			    return value.toString();
	    } 
	   
	    @DELETE
	    @Path("userid/{userid}")
	    @Consumes("application/x-www-form-urlencoded")   
	    @Produces("text/plain")
	    public String doInterfaceServerDelect(@PathParam("userid") String userid){  
	    	String value=null;
	    	RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IProfServesHandler.class);
			request.setCallDAOIF(IProfServesDAO.class);
			request.setCallHandlerMethod(IProfServesHandler.doFind);
		    request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    Map param=getVo();
		    param.put("userid", userid);
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
		    List results = dto.getListParam();
			if(results.size()>0){
		    	RequestEvent request1 = new RequestEvent();
				request1.setCallHandlerIF(IProfServesHandler.class);
				request1.setCallDAOIF(IProfServesDAO.class);
				request1.setCallHandlerMethod(IProfServesHandler.doInterfaceServerDelect);
			    request1.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
			    request1.setDataSource(DataSourceEnum.IRADAR);
			    request1.setDTO(paramDTO);
				IResponseEvent response1 = delegator(request1);
				ParamDTO dto1 = (ParamDTO) response1.getDTO();
				boolean success = dto1.getBoolParam();
			    value="恭喜取消訂閱成功";
		        }else{
				
			    value="用戶不存在";
				
			    }

			    return value.toString();
	    }  
	    
	    
}
