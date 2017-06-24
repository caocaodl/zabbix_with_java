package com.isoft.biz.web.bean.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts2.ServletActionContext;

import com.isoft.biz.dao.common.ISystemDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ISystemHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class CabinetAtion extends IaasPageAction{

	public void  docabinet() throws IOException{
	RequestEvent request = new RequestEvent();
	request.setCallHandlerIF(ISystemHandler.class);
	request.setCallDAOIF(ISystemDAO.class);
	request.setCallHandlerMethod(ISystemHandler.doSystem);
    request.setModuleName(ModuleConstants.MODULE_COMMON);
    Map param = getVo();
    ParamDTO paramDTO = new ParamDTO();
    paramDTO.setMapParam(param);
    request.setDTO(paramDTO);
	IResponseEvent response = delegator(request);
	ParamDTO dto = (ParamDTO) response.getDTO();
	List results = dto.getListParam();
	JSONArray jsonResults = JSONArray.fromObject(results);
	String jsonResult = jsonResults.toString();
	HttpServletResponse response1 = ServletActionContext.getResponse();
	response1.setContentType("text/html;charset=utf-8");
	PrintWriter out = response1.getWriter();
	out.println(jsonResult);
	out.flush();
	out.close();

}
}