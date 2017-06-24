package com.isoft.iradar.core.utils;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class EasyServlet {
	
	public static String getRealPath(HttpServletRequest request, String path) {
		return request.getSession().getServletContext().getRealPath(path);
	}
	
	public static String getRequestPayload(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = req.getReader();
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sb.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
