package com.isoft.web.bean.iface;

import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.isoft.biz.dao.portserves.IUserResourcesDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IUserResourcesHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

@Path("/user") 
 
public class ResourceUser extends IaasPageAction{
	
	private IdentityBean idBean = new IdentityBean();

	protected IdentityBean getIdentityBean() {
		return this.idBean;
	}
	
    @Path("/userResources")  
    @GET  
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public String doResourceUser(@QueryParam("userid") String userid){
    	
    	RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IUserResourcesHandler.class);
		request.setCallDAOIF(IUserResourcesDAO.class);
		request.setCallHandlerMethod(IUserResourcesHandler.doResourceUser);
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
		return results.toString();
    } 
}
