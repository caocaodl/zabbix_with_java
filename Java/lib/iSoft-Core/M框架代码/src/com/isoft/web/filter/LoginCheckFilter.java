package com.isoft.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.model.PermItem;
import com.isoft.server.RunParams;
import com.isoft.utils.CacheUtil;

public class LoginCheckFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		boolean pass = false;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		IdentityBean idBean = (IdentityBean) httpRequest.getSession()
				.getAttribute(Constant.ATTR_ID_BEAN);
		if (idBean != null) {
			String userId = idBean.getUserId();
			if (userId != null && userId.length() > 0) {
				pass = true;
			}
		}

		String ctxPath = httpRequest.getContextPath();
		String viewId = httpRequest.getRequestURI();
		viewId = viewId.substring(ctxPath.length());
		if(viewId.indexOf(';')>0){
			viewId = viewId.substring(0,viewId.indexOf(';'));
		}
		
		if(!pass){
			if ("/index.action".equals(viewId) || "/login.action".equals(viewId)
					|| "/logout.action".equals(viewId) || "/platform/ProfForgotPwd.action".equals(viewId)
					|| "/platform/ProfPwdReset.action".equals(viewId)) {
				pass = true;
			}
		}
		pass = true;
		if (pass) {
			boolean hasPerm = true;
//			if(RunParams.CHECK_PERM){
//				List<PermItem> permList = CacheUtil.getPermByViewId(viewId);
//				if (permList != null) {
//					hasPerm = false;
//					for(PermItem perm : permList){
//						if( (perm.getRole() & idBean.getTenantRole().magic()) >0){
//							if(idBean.isAdmin() || idBean.getPerms().containsKey(perm.getId())){
//								hasPerm = true;
//								break;
//							}
//						}						
//					}
//				}
//			}
			if (hasPerm) {
				filter.doFilter(request, response);
			} else {
				if (httpRequest.getHeader("x-requested-with") != null
						&& httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
					((HttpServletResponse) response).setHeader("sessionStatus","denied");
				} else {
					httpRequest.getRequestDispatcher("/denied.html").forward(request, response);
				}
			}
		} else {
			if (httpRequest.getHeader("x-requested-with") != null
					&& httpRequest.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")) {
				((HttpServletResponse) response).setHeader("sessionStatus","timeout");
			} else {
				httpRequest.getRequestDispatcher("/timeout.html").forward(request, response);
			}			
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String portalUri = config.getInitParameter("portalUri");
		if (portalUri != null && portalUri.length() > 0) {
			RunParams.PORTAL_URI = portalUri;
		}
		String portalVersion = config.getInitParameter("portalVersion");
		if (portalVersion != null && portalVersion.length() > 0) {
			RunParams.RELEASE_VERSION = portalVersion;
		}
	}

}
