package com.isoft.framework.common;

import com.isoft.biz.method.LogicMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;
import com.isoft.framework.common.interfaces.IDTO;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.web.listener.DataSourceEnum;

public class RequestEvent extends BaseEvent implements IRequestEvent {
	
    private static final long serialVersionUID = 1L;
    
    private boolean checkLogin = true; // 是否时登录
    private String callDAOIF; // 所要调用的DAO
    private String callHandlerIF; // 所要调用的BLH
    private String callHandlerMethod; // 所要调用的BLH中的方法
    private String moduleName; // 所属包名
    private FuncIdEnum[] funcId; // 功能编号
    private Role role; // 用户角色
    private IDTO requestDTO;
    
    private DataSourceEnum dataSource = DataSourceEnum.values()[0];

    public IDTO getDTO() {
        return requestDTO;
    }

    public void setDTO(IDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

    @SuppressWarnings("unchecked")
    public void setCallHandlerIF(Class handlerIF) {
        this.callHandlerIF = handlerIF.getName();
    }

    public void setCallHandlerIF(String callHandlerIF) {
        this.callHandlerIF = callHandlerIF;
    }

    public String getCallHandlerIF() {
        return callHandlerIF;
    }

    public String getCallHandlerMethod() {
        return callHandlerMethod;
    }

    public void setCallHandlerMethod(LogicMethod method) {
        this.callHandlerMethod = method.method;
        this.funcId = method.funcId;
        this.role = method.role;
    }

    public String getCallDAOIF() {
        return callDAOIF;
    }

    @SuppressWarnings("unchecked")
    public void setCallDAOIF(Class callDAOIF) {
        this.callDAOIF = callDAOIF.getName();
    }

    public void setCallDAOIF(String callDAOIF) {
        this.callDAOIF = callDAOIF;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public FuncIdEnum[] getFuncId() {
        return this.funcId;
    }

    public Role getRole() {
        return this.role;
    }

    public boolean isCheckLogin() {
        return checkLogin;
    }

    public void setCheckLogin(boolean checkLogin) {
        this.checkLogin = checkLogin;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public DataSourceEnum getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSourceEnum dataSource) {
		this.dataSource = dataSource;
	}

	@Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer("\n");
        strBuf.append("ModulePackageName=").append(moduleName).append("\n");
        strBuf.append("FuncId=");
        for (int i = 0; i < funcId.length; i++) {
            if (i > 0) {
                strBuf.append("|");
            }
            strBuf.append(funcId[i]);
        }
        strBuf.append("\n").append("Role=").append(role).append("\n")
        	.append("CallHandlerIF=").append(callHandlerIF).append("\n")
        	.append("CallHandlerMethod=").append(callHandlerMethod).append("\n")
            .append("CallDAOIF=").append(callDAOIF).append("\n")
            .append("CheckLogin=").append(checkLogin).append("\n")
            .append("DataSource=").append(dataSource).append("\n");
        return strBuf.toString();
    }
}