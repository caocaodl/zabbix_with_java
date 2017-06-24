package com.isoft.struts2.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Component;

import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.server.RunParams;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.struts2.views.tags.ui.Tag;
import com.opensymphony.xwork2.util.ValueStack;

public class AndurilComponent extends Component implements ValueHolder {

	protected HttpServletRequest request;
	protected HttpServletResponse response;

	private boolean rendered = true;

	private Map<String, ValueBinding> _valueBindingMap = null;

	private Object _value = null;

	public AndurilComponent(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack);
		this.request = request;
		this.response = response;
	}

	public ValueBinding getValueBinding(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (_valueBindingMap == null) {
			return null;
		} else {
			return _valueBindingMap.get(name);
		}
	}

	public void setValueBinding(String name, ValueBinding binding) {
		if (name == null) {
			throw new NullPointerException("name");
		}
		if (_valueBindingMap == null) {
			_valueBindingMap = new HashMap<String, ValueBinding>();
		}
		_valueBindingMap.put(name, binding);
	}

	/**
	 * @see Component#start
	 */
	@Override
	public boolean start(Writer writer) {
		if (!this.rendered) {
			return false;
		}
		HtmlResponseWriter responseWriter = new HtmlResponseWriter(writer);
		try {
			return encodeBegin(responseWriter);
		} catch (IOException e) {
			throw new StrutsException(e);
		}
	}

	/**
	 * @see Component#end
	 */
	@Override
	public boolean end(Writer writer, String body) {
		try {
			if (!this.rendered) {
				return false;
			}
			HtmlResponseWriter responseWriter = new HtmlResponseWriter(writer);
			if (body.length() > 0 && !this.usesBody()) {
				responseWriter.write(body);
			}
			if (this.usesBody()) {
				encodeBody(responseWriter, body);
			}
			return encodeEnd(responseWriter);
		} catch (IOException e) {
			throw new StrutsException("IOError while writing the body: "
					+ e.getMessage(), e);
		} finally {
				popComponentStack();
		}
	}

	@Override
	public boolean usesBody() {
		return (!this.rendered) && super.usesBody();
	}

	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return true;
	}

	protected void encodeBody(HtmlResponseWriter writer, String bodyContent)
			throws IOException {
	}

	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public void setValue(Object value) {
		_value = value;
	}

	public Object getValue() {
		if (_value != null) {
			return _value;
		}
		ValueBinding vb = getValueBinding("value");
		return vb != null ? (Object) vb.getValue(this.getStack()) : null;
	}

	protected HttpSession getSession() {
		return request.getSession();
	}
	
	protected IdentityBean getIdentityBean(){
		return (IdentityBean)getSession().getAttribute(Constant.ATTR_ID_BEAN);
	}

	public String getContextPath() {
		String ctx = this.request.getContextPath();
		if (ctx.length() == 1) {
			ctx = "";
		}
		return ctx;
	}

	private final static String ATTR_CONTAINER = "_container_";
	
	@SuppressWarnings("unchecked")
	protected void linkCss(Writer writer, Class clazz, String spec) throws IOException {
		List<String> res = Tag.getResource(clazz.getSimpleName() + "." + spec + ".css");
		if (res != null && !res.isEmpty()) {
			for (String css : res) {
				linkCss(writer, css);
			}
		}
	}
	
	protected void linkCss(Writer writer, Class clazz) throws IOException {
		List<String> res = Tag.getResource(clazz.getSimpleName()+".css");
		if (res != null && !res.isEmpty()) {
			for (String css : res) {
				linkCss(writer, css);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void linkCss(Writer writer, String src)
			throws IOException {
		if (StringUtils.isEmpty(src)) {
			return;
		}
		Map<String, Integer> container = (Map<String, Integer>) request
				.getAttribute(ATTR_CONTAINER);
		if (container == null) {
			container = new HashMap<String, Integer>();
			request.setAttribute(ATTR_CONTAINER, container);
		}

		if (!container.containsKey(src)) {
			container.put(src, 0);
			writer.write("<link href='");
			writer.write(getContextPath());
			writer.write(src);
			writer.write("?ts="+RunParams.RELEASE_VERSION);
			writer.write("' rel='stylesheet' type='text/css' charset='utf-8'/>\n");
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void linkJavaScript(Writer writer, Class clazz, String spec) throws IOException {
		List<String> res = Tag.getResource(clazz.getSimpleName() + "." + spec + ".js");
		if (res != null && !res.isEmpty()) {
			for (String js : res) {
				linkJavaScript(writer, js);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void linkJavaScript(Writer writer, Class clazz) throws IOException {
		List<String> res = Tag.getResource(clazz.getSimpleName()+".js");
		if (res != null && !res.isEmpty()) {
			for (String js : res) {
				linkJavaScript(writer, js);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void linkJavaScript(Writer writer, String src)
			throws IOException {
		if (StringUtils.isEmpty(src)) {
			return;
		}
		Map<String, Integer> container = (Map<String, Integer>) request
				.getAttribute(ATTR_CONTAINER);
		if (container == null) {
			container = new HashMap<String, Integer>();
			request.setAttribute(ATTR_CONTAINER, container);
		}

		if (!container.containsKey(src)) {
			container.put(src, 0);
			writer.write("<script src='");
			writer.write(getContextPath());
			writer.write(src);
			writer.write("?ts="+RunParams.RELEASE_VERSION);
			writer.write("' type='text/javascript' charset='utf-8'></script>\n");
		}
	}
}
