package com.isoft.struts2.components;

import com.isoft.struts2.convert.Converter;
import com.opensymphony.xwork2.util.ValueStack;

public class ValueBinding {
    private String _expressionString = null;

    public String getExpressionString() {
        return _expressionString;
    }

    public ValueBinding(String expression) {
        _expressionString = expression;
    }

    public Object getValue(ValueStack stack) {
        return stack.findValue(_expressionString);
    }

    public void setValue(ValueStack stack, Object value){
        stack.setValue(_expressionString, value);
    }
    
    public static boolean isAccepted(String paramName){
        return paramName!=null && '#'==paramName.charAt(0) && ']' == paramName.charAt(paramName.length()-1);
    }
    
    public static String encodeExpressionString(Converter cv, String paramName) {
        return "#(" + cv.getId() + ")[" + paramName + "]";
    }
    
    public static String[] decodeExpressionString(String paramName){
        String[] vb = new String[2];
        int ts = paramName.indexOf('(');
        int te = paramName.lastIndexOf(')');
        int ps = paramName.indexOf('[');
        int pe = paramName.lastIndexOf(']');
        if(ts==1 && te>(ts+1)){
            vb[0] = paramName.substring(ts+1,te);
        }
        if(ps==(te+1) && pe>(ps+1)){
            vb[1] = paramName.substring(ps+1,pe);
        }
        return vb;
    }
}
