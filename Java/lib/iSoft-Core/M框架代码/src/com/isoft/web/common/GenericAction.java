package com.isoft.web.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;

public abstract class GenericAction extends ToolkitAction implements Action {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public final void initServletAware(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		this.request = httpRequest;
		this.response = httpResponse;
		this.session = httpRequest.getSession();
	}

	public final void resetIdentityBean() {
		this.session.setAttribute(Constant.ATTR_ID_BEAN, new IdentityBean());
	}

	protected void setAttribute(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	protected Object getAttribute(String key) {
		return getRequest().getAttribute(key);
	}

	public String getParameter(String pName) {
		return getRequest().getParameter(pName);
	}

	protected String[] getParameterValues(String pName) {
		return getRequest().getParameterValues(pName);
	}
	
	protected String request(String arg){
		return this.getRequest().getParameter(arg);
	}
	
	protected String[] requests(String arg){
		return this.getRequest().getParameterValues(arg);
	}

	public void setSession(HttpSession httpSession) {
		this.session = httpSession;
	}

	public HttpServletRequest getRequest() {
		if (this.request == null) {
			ActionContext ac = ActionContext.getContext();
			if (ac != null) {
				request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
			}
		}
		return this.request;
	}

	public HttpServletResponse getResponse() {
		if (this.response == null) {
			ActionContext ac = ActionContext.getContext();
			if (ac != null) {
				response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
			}
		}
		return this.response;
	}

	public HttpSession getSession() {
		if (session != null) {
			return session;
		}
		if (getRequest() == null) {
			return null;
		}
		return getRequest().getSession(true);
	}

	public String getCtxPath() {
		return getRequest().getContextPath();
	}

	public String getViewId() {
		return getRequest().getRequestURI();
	}

	private String ts;

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
}
