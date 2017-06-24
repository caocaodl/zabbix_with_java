package com.isoft.iradar.inc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.web.views.CViewPageFooter;
import com.isoft.iradar.web.views.CViewPageHeader;

public class ViewsUtil {
	
	private ViewsUtil() {
	}
	
	public static HttpServletRequest getRequest() {
		RadarContext ctx = RadarContext.getContext();
		return ctx.getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		RadarContext ctx = RadarContext.getContext();
		return ctx.getResponse();
	}
	
	public static void includePageHeader(IIdentityBean idBean, SQLExecutor executor) {
		if (getRequest().getAttribute("require_once_page_header") == null) {
			getRequest().setAttribute("require_once_page_header", true);
			CViewPageHeader.renderAndShow(idBean, executor);			
		}
	}

	public static void includePageFooter(IIdentityBean idBean, SQLExecutor executor) {
		if (getRequest().getAttribute("require_once_page_footer") == null) {
			getRequest().setAttribute("require_once_page_footer", true);
			CViewPageFooter.renderAndShow(idBean, executor);			
		}		
	}
	
	public static void includeSubView(String view) {
		includeSubView(view, null);
	}
	
	public static void includeSubView(String view, Map data) {
		RadarContext ctx = RadarContext.getContext();
		HttpServletRequest request = ctx.getRequest();
		HttpServletResponse response = ctx.getResponse();
		if (data != null) {
			request.setAttribute(view, data);
		}
		try {
			request.getRequestDispatcher("/WEB-INF/iradar/views/" + view + ".jsp").include(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map getSubViewData(String view){
		RadarContext ctx = RadarContext.getContext();
		HttpServletRequest request = ctx.getRequest();
		return getSubViewData(request, view);
	}
	
	public static Map getSubViewData(HttpServletRequest request, String view){
		return (Map)request.getAttribute(view);
	}

	public static void redirect(String view) {
		RadarContext ctx = RadarContext.getContext();
		HttpServletResponse response = ctx.getResponse();
		try {
			response.sendRedirect(view);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void forward(String view) {
		RadarContext ctx = RadarContext.getContext();
		HttpServletRequest request = ctx.getRequest();
		HttpServletResponse response = ctx.getResponse();
		try {
			request.getRequestDispatcher("/WEB-INF/iradar/views/" + view + ".jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
