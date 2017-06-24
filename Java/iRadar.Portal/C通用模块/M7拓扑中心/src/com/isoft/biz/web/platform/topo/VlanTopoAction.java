package com.isoft.biz.web.platform.topo;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import com.isoft.biz.dao.platform.topo.INodeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IVlanTopoHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class VlanTopoAction extends IaasPageAction{
	
	private Object result;
	private static final String RESULT = "result";
	
	public String doMenuJson() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVlanTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IVlanTopoHandler.doVlanTopoMenuJson);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		String json = dto.getStrParam();
		try {
			result = new ByteArrayInputStream(json.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return RESULT;
	}
	
	public String doXml() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVlanTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IVlanTopoHandler.doVlanTopoXml);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		String json = dto.getStrParam();
		try {
			result = new ByteArrayInputStream(json.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return RESULT;
	}
	
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}