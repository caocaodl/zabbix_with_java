package com.isoft.iradar.web.filter;

import static com.isoft.Feature.enableGuestUser;
import static com.isoft.iradar.model.CWebUser.checkAuthentication;
import static com.isoft.iradar.model.CWebUser.isLoggedIn;
import static com.isoft.iradar.model.CWebUser.setDefault;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.iradar.RadarContext;

public class RadarContextFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse =(HttpServletResponse)response;
		RadarContext ctx = new RadarContext(httpRequest, httpResponse);
		RadarContext.setContext(ctx);
		
		httpResponse.setHeader("Cache-Control","no-cache");
		httpResponse.setHeader("Pragma","no-cache");
		httpResponse.setDateHeader ("Expires", 0);
		
		try {
			String uri = httpRequest.getRequestURI();
			String ctxPath = httpRequest.getContextPath().length() == 1 ? "" : httpRequest.getContextPath();
			if (!uri.endsWith("jsLoader.action")
					&& uri.startsWith(ctxPath + "/platform")
					&& !authenticateUser(httpRequest, httpResponse)) {
				httpResponse.sendRedirect(ctxPath + "/index.action");
				return;
			}
			chain.doFilter(request, response);
		} finally {
			RadarContext.releaseContext();
		}
		
	}

	private boolean authenticateUser(HttpServletRequest request,
			HttpServletResponse response) {
		if (!isLoggedIn() && !checkAuthentication(RadarContext.sessionId())) {
			if (enableGuestUser) {
				setDefault();
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}
	
}
