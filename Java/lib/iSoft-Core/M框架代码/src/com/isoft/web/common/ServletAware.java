package com.isoft.web.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface ServletAware {
    public HttpSession getSession();
    public HttpServletRequest getRequest();
    public HttpServletResponse getResponse();    
    public String getParameter(String pName);
}
