package com.isoft.struts2.views.tags.ui.linkButton;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.el.HTMLEncoder;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ParamHolder;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class Link extends AndurilUIComponent implements ParamHolder{
	private List<ParamItems> _paramList;
	private String onclick;
	private String text;
	
    public Link(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
        this._paramList = new ArrayList<ParamItems>();
    }
    
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return true;
    }
    
    @Override
	public boolean usesBody() {
		return true;
	}

	@Override
	protected void encodeBody(HtmlResponseWriter writer, String bodyContent)
			throws IOException {
		setText(bodyContent);
	}

	@Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
    	writer.startElement(HTML.ANCHOR_ELEM);
		List<ParamItems> paramList = getParamList();
		String href = String.valueOf(getValue());
		if (!paramList.isEmpty() && paramList.size() > 0) {
			StringBuffer hrefBuf = new StringBuffer(href);
			String charEncoding = response.getCharacterEncoding();
			boolean firstParameter = hrefBuf.indexOf("?") == -1;
			for (ParamItems pa : paramList) {
				hrefBuf.append(firstParameter ? '?' : '&');
				hrefBuf.append(URLEncoder.encode(pa.getName(), charEncoding));
				hrefBuf.append('=');
				if (pa.getValue() != null) {
					hrefBuf.append(URLEncoder.encode(pa.getValue().toString(), charEncoding));
				}
				firstParameter = false;
			}
			href = hrefBuf.toString();
		}
		writer.writeAttribute(HTML.HREF_ATTR, href);
		writer.write(HTMLEncoder.encode(encodeValueAttr(getText()), true, true));
		writer.endElement(HTML.ANCHOR_ELEM);
		return false;
	}

	public List<ParamItems> getParamList() {
		return _paramList;
	}

	public void setParamList(List<ParamItems> list) {
		_paramList = list;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public void pushParam(ParamItems param) {
		_paramList.add(param);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
    
}
