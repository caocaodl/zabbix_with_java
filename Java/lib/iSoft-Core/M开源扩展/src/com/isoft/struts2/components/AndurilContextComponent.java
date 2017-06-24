package com.isoft.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

public abstract class AndurilContextComponent extends AndurilComponent {
	
    protected String var;
        
    public AndurilContextComponent(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
    protected void putInContext(Object value) {
        if (var != null && var.length() > 0) {
            stack.getContext().put(var, value);
        }
    }
    
    public void setVar(String var) {
        if (var != null) {
            this.var = findString(var);
        }
    }
    
    protected String getVar() {
        return this.var;
    }
}
