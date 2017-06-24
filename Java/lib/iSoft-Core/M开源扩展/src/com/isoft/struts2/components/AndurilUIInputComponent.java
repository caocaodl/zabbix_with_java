package com.isoft.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

public abstract class AndurilUIInputComponent extends AndurilUIComponent {

    public AndurilUIInputComponent(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
}
