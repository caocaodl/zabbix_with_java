package com.isoft.web.bean.iface;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;

import com.isoft.biz.dao.portserves.IMonitorDataInterfaceDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.portserves.IMonitorDataInterfaceHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.RegexUtil;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

@Path("/monitorData") 
public class MonitorDataInterfaceAction extends IaasPageAction{

	public static SQLExecutor sqlExecutor;
	private IdentityBean idBean = new IdentityBean();
	
	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{tenantid}/{hostname}")
	public JSONArray doList(
			@HeaderParam("startTime") String startTime,
			@HeaderParam("endTime") String endTime,
			@HeaderParam("itemNumber") String itemNumber,
			@HeaderParam("templatename") String templatename,
			@PathParam("hostname") String hostname,
			@PathParam("tenantid") String tenantid) throws Exception {
		
		boolean flag=true;
		long slTime =0l;
		long elTime =0l;
		int num = 0;
		String message = _("No datas");
		try {
			if(itemNumber == null){
				num = Integer.parseInt(IMonConsts.INTERFACE_LIMITNUM);
			}else{
				if(RegexUtil.isNumber(itemNumber)){
					num = Integer.parseInt(itemNumber);
				}else{
					flag = false;
					message = _("ItemNumber parameter must be an integer");
				}
			}
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			if(flag){
				if(startTime==null){//开始时间,如果为空，则开始时间为当前时间前30天
					Date sTime=new Date();
					slTime=(sTime.getTime()/1000-2592000);
				}else{
					if(RegexUtil.isDateFormate(startTime)){
						Date sTime = sdf.parse(startTime);
						slTime=sTime.getTime()/1000;
					}else{
						flag = false;
						message = _("Starttime format error,must be 'MM - DD YYYY - hh: MM: ss' format");
					}
				}
			}
			
			
			if(flag){
				if(endTime==null){//结束时间，,如果为空，则默认为当前
					Date eTime=new Date();
					elTime=eTime.getTime()/1000;
				}else{
					if(RegexUtil.isDateFormate(endTime)){
						Date eTime = sdf.parse(endTime);
						elTime=eTime.getTime()/1000;
					}else{
						flag = false;
						message = _("Endtime format error,must be 'MM - DD YYYY - hh: MM: ss' format");
					}
				}
			}
			
			if(tenantid==null){
				flag=false;
		    }
			
			if(hostname==null){
				flag=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag=false;
		}
		
		List<Map> results=null;
		if(flag){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IMonitorDataInterfaceHandler.class);
			request.setCallDAOIF(IMonitorDataInterfaceDAO.class);
			request.setCallHandlerMethod(IMonitorDataInterfaceHandler.doList);
		    request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
		    request.setDataSource(DataSourceEnum.IRADAR);
		    Map param = new HashMap();
		    param.put("startTime", slTime);
		    param.put("endTime", elTime);
		    param.put("itemNumber", num);
		    param.put("tenantid", tenantid);
		    param.put("templatename", templatename);
		    param.put("hostname", hostname);
		    
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			results = dto.getListParam();
			if(empty(results)){
				results.add(EasyMap.build("message",_("Execute Success")+":" + message));
			}
		}else{
			results = new ArrayList<Map>();
			results.add(EasyMap.build("message", _("Execute Failure")+":" + message));
		}
		
		JSONArray jsonResults = JSONArray.fromObject(results);
		
		return jsonResults;        	
	    }
	
		
}
