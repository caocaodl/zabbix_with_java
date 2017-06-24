package com.isoft.biz.handlerimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.common.PreLoaderDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class PreLoaderHandler extends BaseLogicHandler {
    
    public IResponseEvent loadSysConfig(IIdentityBean identityBean,
            IRequestEvent request, IDAO dao) {
        IResponseEvent response = new ResponseEvent();
        PreLoaderDAO idao = (PreLoaderDAO) dao;
        ParamDTO dto = new ParamDTO();
        
        Map dtoMap = new HashMap(5);
        
        List sysDict = idao.loadSysDicts();
        dtoMap.put("sysDict", sysDict);
        
        List sysFunc = idao.loadSysFuncs();
        dtoMap.put("sysFunc", sysFunc);
        
        List sysPerm = idao.loadSysPerms();
        dtoMap.put("sysPerm", sysPerm);
                
        dto.setMapParam(dtoMap);
        response.setDTO(dto);
        return response;
    }
    
}
