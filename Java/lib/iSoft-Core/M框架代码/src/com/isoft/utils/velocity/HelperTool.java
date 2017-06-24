package com.isoft.utils.velocity;

public class HelperTool {
    public String op(Object op){
        if("00".equals(op)){
            return "=";
        }else if("01".equals(op)){
            return "!=";
        }else{
            return "=";
        }
    }
}
