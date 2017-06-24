package com.isoft.web.common;

import com.isoft.biz.Delegator;
import com.isoft.biz.dao.common.IDelegateDAO;
import com.isoft.biz.dto.DelegateDTO;
import com.isoft.biz.handler.common.IDelegateHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.listener.DataSourceEnum;

public class DelegateAction extends BasePageAction {
	
	private IIdentityBean idBean;
	
	public DelegateAction(IIdentityBean idBean) {
		this.idBean = idBean;
	}

	@Override
	protected IdentityBean getIdentityBean() {
		return (IdentityBean)this.idBean;
	}
	
	public <T> T doDelegate(Delegator<T> delegator) throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IDelegateHandler.class);
		request.setCallDAOIF(IDelegateDAO.class);
		request.setCallHandlerMethod(IDelegateHandler.doDelegate);
		request.setModuleName(ModuleConstants.MODULE_COMMON);
		request.setDataSource(DataSourceEnum.IRADAR);

		DelegateDTO dto = new DelegateDTO();
		dto.setObjParam(delegator);
		request.setDTO(dto);
		IResponseEvent response = delegator(request);
        dto = (DelegateDTO)response.getDTO();
		if (dto.hasException()) {
			throw dto.getException();
		}
        return (T)dto.getObjParam();
	}
}
