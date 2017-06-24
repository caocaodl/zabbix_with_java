package com.isoft.struts2.views.tags.ui.linkButton;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ParamHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class Param extends AndurilUIComponent {
	
	private String name;
	
	public Param(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
        return false;
	}
    
    @Override
    protected void popComponentStack() {
        super.popComponentStack();
        Object component = this.getComponentStack().peek();
        if(component instanceof ParamHolder){
        	ParamItems pi = new ParamItems(getName(),getValue());
            ((ParamHolder)component).pushParam(pi);
        }
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
