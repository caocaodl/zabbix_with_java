package com.isoft.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.LinkedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.biz.dao.common.ILogDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ILogHandler;
import com.isoft.consts.ModuleConstant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.model.FuncItem;
import com.isoft.model.PermItem;
import com.isoft.utils.CacheUtil;
import com.isoft.web.common.IaasPageAction;

public class DevModeFilter implements Filter {
	private static final Logger LOG = LoggerFactory.getLogger(DevModeFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		LogBean logBean = new LogBean();
		logBean.initServletAware((HttpServletRequest)request, (HttpServletResponse)response);
		try {
			if(logBean.isLoginIn()){
				logBean.keepIdentityBean();
			}
			chain.doFilter(request, response);
		} finally {
			Map param = new LinkedMap();
			StringBuilder sBuilder = new StringBuilder("");
			String viewUri = (String) request.getAttribute("struts.view_uri");
			String requestUri = ((HttpServletRequest) request).getRequestURI();
			String ctxPath =((HttpServletRequest) request).getContextPath();
			requestUri = requestUri.substring(ctxPath.length());
			//logBean.logRequest(requestUri);

			List<PermItem> permList =  CacheUtil.getPermByViewId(requestUri);
			
			
			if(LOG.isDebugEnabled()) {
				LOG.debug("#######struts.request_uri= " + requestUri);
				LOG.debug("#######struts.view_uri   = " + viewUri);
			}
			
			param.put("requestUri", requestUri);
			param.put("funcName", "");
			param.put("FuncId", "");
			List<FuncItem> funcList = CacheUtil.getNavFuncList();
			if(permList != null){
				outer:for(PermItem bt : permList){
					for (FuncItem func : funcList) {
						if(func.getId().equals((String) bt.getFuncId())){
							param.put("funcName", func.getFuncName());
							param.put("FuncId", func.getId());
							break outer;
						}
						for (FuncItem funcItem : func.getSubFuncList()) {
							if(funcItem.getId().equals((String) bt.getFuncId())){
								param.put("funcName", funcItem.getFuncName());
								param.put("FuncId", funcItem.getId());
								break outer;
							}
							for(FuncItem item : funcItem.getSubFuncList()){
								if(item.getId().equals((String) bt.getFuncId())){
									param.put("funcName", item.getFuncName());
									param.put("FuncId", item.getId());
									break outer;
								}
							}
						}
					}
				}
			}
			
			Enumeration pnames = request.getParameterNames();
			while (pnames.hasMoreElements()) {
				String pname = (String) pnames.nextElement();
				if("password".equals(pname) || "curPswd".equals(pname) || "newPswd".equals(pname)){
					continue;
				}
				String pvalue = request.getParameter(pname);
				if (pname.equals("FuncId")) {
					param.put("FuncId", pvalue);
				} else if (pname.equals("oper")) {
					param.put("funcMenu", pvalue);
				} else {
					sBuilder.append(pname + ":" + pvalue + ",");
				}
			}
			param.put("description", sBuilder.toString());
			if (!param.containsKey("funcMenu")) {
				String[] paraStr = requestUri.split("/");
				if (paraStr.length > 0) {
					String funcMenu = paraStr[paraStr.length - 1].split("[.]")[0];
					param.put("funcMenu", funcMenu);
				}
			}
			//logBean.logMonitor(param);
		}
	}

	protected class LogBean extends IaasPageAction {
		
		private IdentityBean idBean = null;
		
		public void keepIdentityBean(){
			this.idBean = super.getIdentityBean();
		}
		
		@Override
		protected IdentityBean getIdentityBean() {
			if(this.idBean != null){
				return this.idBean;
			}
			return super.getIdentityBean();
		}

		public void logMonitor(Map param) {
			if(isLoginIn() || this.idBean != null){
				RequestEvent request = new RequestEvent();
				request.setCallHandlerIF(ILogHandler.class);
				request.setCallDAOIF(ILogDAO.class);
				request.setCallHandlerMethod(ILogHandler.METHOD_DOPLATFORM_LOG_ADD);
				request.setModuleName(ModuleConstant.MODULE_COMMON);
				request.setCheckLogin(false);
	
				ParamDTO paramDTO = new ParamDTO();
				paramDTO.setMapParam(param);
				request.setDTO(paramDTO);
				delegator(request);
			}
		}
		
		public void logRequest(String uri) {
			if(uri.indexOf(';')>0){
				uri = uri.substring(0,uri.indexOf(';'));
			}
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(ILogHandler.class);
			request.setCallDAOIF(ILogDAO.class);
			request.setCallHandlerMethod(ILogHandler.doLogRequest);
			request.setModuleName(ModuleConstant.MODULE_COMMON);
			request.setCheckLogin(false);
			
			Map param = new HashMap(5);
			param.put("uri", uri);
			if(isLoginIn()){
				param.put("roleMagic", getIdentityBean().getTenantRole().magic());
			} else {
				param.put("roleMagic", 0);
			}

			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			delegator(request);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
