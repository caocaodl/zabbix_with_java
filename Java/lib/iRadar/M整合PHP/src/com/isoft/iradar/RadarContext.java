package com.isoft.iradar;

import static com.isoft.iradar.core.utils.StringUtil.isEmptyStr;
import static com.isoft.iradar.inc.FuncsUtil.get_cookie;
import static com.isoft.iradar.model.CRequestParameters.parse;
import static com.isoft.types.CArray.array;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.opensymphony.xwork2.ActionContext;

public class RadarContext {

	private static final String MARKER_SESSIONID = "ID__";
	
	private static ThreadLocal<RadarContext> ctxHolder = new ThreadLocal<RadarContext>();

	public static void setContext(RadarContext ctx) {
		ctxHolder.set(ctx);
	}

	public static RadarContext getContext() {
		return ctxHolder.get();
	}

	public static void releaseContext() {
		ctxHolder.set(null);
	}
	
	public static String getContextPath() {
		String ctx = request().getContextPath();
		if (ctx.length() == 1) {
			ctx = "";
		}
		return ctx;
	}
	
	public static HttpServletRequest request() {
		return getContext().getRequest();
	}
	
	public static HttpServletResponse response() {
		return getContext().getResponse();
	}
	
	public static HttpSession session() {
		return getContext().getSession();
	}
	
	public static String sessionId() {
		HttpSession session = getContext().getSession();
		String sid = (String) session.getAttribute(MARKER_SESSIONID);
		if(isEmptyStr(sid)) {
			sid = get_cookie("rda_sessionid");
			if (sid == null) {
				sid = "";
			}
			session.setAttribute(MARKER_SESSIONID, sid);
		}
		return sid;
	}
	
	public static void sessionId(String sid) {
		HttpSession session = getContext().getSession();
		session.setAttribute(MARKER_SESSIONID, sid);
	}
	
	public static CMap<Object, Object> _GET() {
		if(getContext().requestGetParameters == null){
			getContext().requestGetParameters = parse(getContext().getRequest());
		}
		return getContext().requestGetParameters;
	}
	
	public static CMap<Object, Object> _REQUEST() {
		return getContext().requestParameters;
	}
	
	public static <T> T _REQUEST(String key) {
		return (T)getContext().requestParameters.get(key);
	}
	
	public static CMap<String, Object> _COOKIES() {
		return getContext().cookies;
	}
	
	public static Map<String, Object> page() {
		return getContext().getPage();
	}
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private CMap<Object, Object> requestParameters = null;
	private CMap<Object, Object> requestGetParameters = null;
	private CMap<String, Object> cookies;
	
	public static IIdentityBean getIdentityBean() {
		RadarContext ctx = getContext();
    	IdentityBean idBean = null;
        if(ctx.getRequest() == null){
        	idBean = (IdentityBean)ctx.getSession().getAttribute(Constant.ATTR_ID_BEAN);
            if (idBean == null) {
                idBean = new IdentityBean();
                ctx.getSession().setAttribute(Constant.ATTR_ID_BEAN, idBean);
            }
		} else {
            idBean = (IdentityBean)ctx.getSession().getAttribute(Constant.ATTR_ID_BEAN);
            if (idBean == null) {
                idBean = new IdentityBean();
                ctx.getSession().setAttribute(Constant.ATTR_ID_BEAN, idBean);
            }
        }
        return idBean;
	}

	public RadarContext() {
		this.requestParameters = new CArray<Object>();
	}

	public RadarContext(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.requestParameters = parse(request);
		this.cookies = wrapCookies(request);
	}
	
	private CMap<String, Object> wrapCookies(HttpServletRequest request){
		CArray map = array();
		Cookie[] cookies = request.getCookies();
		if(cookies!=null && cookies.length>0){
			for (Cookie c : cookies) {
				map.put(c.getName(), c.getValue());
			}
		}
		return map;
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
		return getRequest().getSession(true);
	}
	
	private Map<String, Object> page = new HashMap<String, Object>();

	public Map<String, Object> getPage() {
		return this.page;
	}
	
	public Object getPage(String key) {
		return this.page.get(key);
	}

	public void setPage(String attrName, Object attrValue) {
		this.page.put(attrName, attrValue);
	}
	
	private PrintWriter writer;
	public void write(String s){
		if (writer == null) {
			try {
				writer = this.getResponse().getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (writer != null) {
			writer.write(s);
			writer.write('\n');
		}
	}

	private Map<String,Object> defines = new HashMap<String,Object>();
	public Object define(String constantVar) {
		return defines.get(constantVar);
	}
	
	public void define(String constantVar, Object value) {
		defines.put(constantVar, value);
	}

	public boolean defined(String constantVar) {
		return defines.containsKey(constantVar);		
	}
	
	public void undefine(String constantVar) {
		defines.remove(constantVar);		
	}
	
	public void include(String fileName) {
		try {
			request.getRequestDispatcher(fileName).include(request, response);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}		
	}
}
