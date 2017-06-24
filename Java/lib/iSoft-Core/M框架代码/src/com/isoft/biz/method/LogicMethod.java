package com.isoft.biz.method;

import java.io.Serializable;

import com.isoft.dictionary.FuncIdEnum;

public abstract class LogicMethod implements Serializable{
    private static final long serialVersionUID = 1L;
    
    public String method;
    public FuncIdEnum[] funcId;
    public Role role;
    
    public LogicMethod(String method, FuncIdEnum[] funcId, Role role) {
        this.method = method;
        this.funcId = funcId;
        this.role = role;
    }
}
