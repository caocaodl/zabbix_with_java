package com.isoft.framework.common.interfaces;

import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IRequestEvent extends IEvent {
    IDTO getDTO();
    
    void setDTO(IDTO requestDTO);

    /**
     * 获得所要执行的BLH中的方法
     * @return
     */
    String getCallHandlerMethod();

    /**
     * 获得所要调用的BLH
     * @return
     */
    String getCallHandlerIF();

    /**
     * 获得DAO接口
     * @return
     */
    String getCallDAOIF();

    /**
     * 获得所属模块名
     * @return
     */
    String getModuleName();

    /**
     * 获得功能号
     * @return
     */
    FuncIdEnum[] getFuncId();
    
    /**
     * 获得用户角色
     * @return
     */
    Role getRole();

    /**
     * 设置DAO接口
     * @return
     */
    @SuppressWarnings("unchecked")
    void setCallDAOIF(Class callDAOIF);


    /**
     * 设置所要调用的BLH
     * @return
     */
    @SuppressWarnings("unchecked")
    void setCallHandlerIF(Class handlerIF);

    /**
     * 设置所属模块名
     * @return
     */
    void setModuleName(String moduleName);
    
}
