package com.isoft.web.bean.iface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;

import com.isoft.biz.dao.portserves.IItemDataInterfaceDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IItemDataInterfaceHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

@Path("/itemData") 
public class ItemDataInterfaceAction extends IaasPageAction{

	private IdentityBean idBean = new IdentityBean();
	
	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public JSONArray doList(
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime,
			@QueryParam("itemNumber") String itemNumber,
			@QueryParam("itemid") String itemid,
			@QueryParam("tenantid") String tenantid) throws Exception {
		
		boolean flag=true;
		long slTime =0l;
		long elTime =0l;
		int num = 0;
		try {
			if(itemNumber == null){
				num = Integer.parseInt(IMonConsts.INTERFACE_LIMITNUM);
			}else{
				num = Integer.parseInt(itemNumber);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			if(startTime==null){//开始时间,如果为空，则开始时间为当前时间前30天
				Date sTime=new Date();
				slTime=(sTime.getTime()/1000-2592000);
			}else{
				Date sTime = sdf.parse(startTime);
				slTime=sTime.getTime()/1000;
			}
			if(endTime==null){//结束时间，,如果为空，则默认为当前
				Date eTime=new Date();
				elTime=eTime.getTime()/1000;
			}else{
				Date eTime = sdf.parse(endTime);
				elTime=eTime.getTime()/1000;
			}
			if(tenantid==null){
				flag=false;
		    }
			if(itemid==null){
				flag=false;
			}
		} catch (Exception e) {
			flag=false;
		}
		
		List results=null;
		if(flag){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IItemDataInterfaceHandler.class);
			request.setCallDAOIF(IItemDataInterfaceDAO.class);
			request.setCallHandlerMethod(IItemDataInterfaceHandler.doList);
		    request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    Map param = new HashMap();
		    param.put("startTime", slTime);
		    param.put("endTime", elTime);
		    param.put("itemNumber", num);
		    param.put("tenantid", tenantid);
		    param.put("itemid", itemid);
		    
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			results = dto.getListParam();			
		}else{
			results = new ArrayList();
		}
		JSONArray jsonResults = JSONArray.fromObject(results);
		return jsonResults;        	
	    }
}
