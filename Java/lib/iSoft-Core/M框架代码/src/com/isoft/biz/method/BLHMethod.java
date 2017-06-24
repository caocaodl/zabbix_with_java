package com.isoft.biz.method;

import com.isoft.dictionary.FuncIdEnum;

public class BLHMethod extends LogicMethod{
    private static final long serialVersionUID = 1L;

    public BLHMethod(String method, FuncIdEnum[] funcId, Role role) {
        super(method, funcId, role);
    }
    
    public BLHMethod(String method, FuncIdEnum funcId, Role role) {
        super(method, new FuncIdEnum[] { funcId }, role);
    }
}
