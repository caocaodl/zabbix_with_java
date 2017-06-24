package com.isoft.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class EncodingFilter implements Filter {

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filter) throws IOException, ServletException {
		if(((HttpServletRequest)request).getMethod().equals("POST")){
			request.setCharacterEncoding("GBK");
		}
		filter.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}
