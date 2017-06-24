package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.ILogDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ILogHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class LogAction extends IaasPageAction {


	public String doPage() throws Exception {
		
        RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(ILogHandler.class);
        request.setCallDAOIF(ILogDAO.class);
        request.setCallHandlerMethod(ILogHandler.METHOD_DOPLATFORM_LOG_PAGE);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        DataPage dataPage = new DataPage(true, getPage(),getRows());
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        paramDTO.setDataPage(dataPage);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        List dataList = dto.getListParam();
        setResultList(dataList);
        setDataPage(dataPage);
		return JSON;
	}

}
