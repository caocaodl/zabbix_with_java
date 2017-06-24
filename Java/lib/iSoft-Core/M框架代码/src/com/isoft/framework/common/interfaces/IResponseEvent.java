package com.isoft.framework.common.interfaces;

import com.isoft.biz.exception.BusinessException;

public interface IResponseEvent extends IEvent {
    IDTO getDTO();

    void setDTO(IDTO responseDTO);

    BusinessException getBusinessException();

    void setBusinessException(BusinessException e);

    boolean hasException();
}
