package com.isoft.framework.common.interfaces;

import com.isoft.biz.exception.BusinessException;

public interface IDelegate {
	IResponseEvent delegate(IIdentityBean identityBean,
			IRequestEvent requestEvent);

	IResponseEvent processException(IResponseEvent response, BusinessException e);
}
