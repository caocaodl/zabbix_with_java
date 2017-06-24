package com.isoft.framework.common;

import com.isoft.biz.exception.BusinessException;
import com.isoft.framework.common.interfaces.IDTO;
import com.isoft.framework.common.interfaces.IResponseEvent;

public class ResponseEvent extends BaseEvent implements IResponseEvent {
    private static final long serialVersionUID = 1L;
    private IDTO responseDTO; // 请求响应的DTO
    private BusinessException businessException; // 业务异常

    public IDTO getDTO() {
        return responseDTO;
    }

    public void setDTO(IDTO responseDTO) {
        this.responseDTO = responseDTO;
    }

    public BusinessException getBusinessException() {
        return businessException;
    }

    public void setBusinessException(BusinessException e) {
        this.businessException = e;
    }

    public boolean hasException() {
        return businessException != null;
    }
}
