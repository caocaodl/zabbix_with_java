package com.isoft.biz.handlerimpl;

import java.util.List;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.handler.LogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public abstract class BaseLogicHandler implements LogicHandler {
	
	@SuppressWarnings("unchecked")
	protected void populateUserEntry(List dataList, String pkey, BaseDAO idao){
		if(dataList == null || dataList.isEmpty()){
			return;
		}		
		idao.populateUserEntry(dataList, pkey);
	}
	
	@SuppressWarnings("unchecked")
	protected void populateTenantEntry(List dataList, String pkey, BaseDAO idao){
		if(dataList == null || dataList.isEmpty()){
			return;
		}		
		idao.populateTenantEntry(dataList, pkey);
	}

    public IResponseEvent processException(IResponseEvent response,
            BusinessException e) {
        if (response == null) {
            response = new ResponseEvent();
        }
        if (e != null) {
            response.setBusinessException(e);
            e.printStackTrace(System.err);
        }
        return response;
    }

}
