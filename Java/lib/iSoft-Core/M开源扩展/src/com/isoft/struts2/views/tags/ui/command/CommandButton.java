package com.isoft.struts2.views.tags.ui.command;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class CommandButton extends AndurilUIComponent {
	
	private String style;
	private String styleClass;
	private String onclick;
	private String action;
	
	public CommandButton(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
		super(stack, request, response);
	}
	
	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		return false;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		String _id = this.getId();
		String hookClickName = "hookClick_" + _id;
		this.makeHookClickScript(writer, hookClickName);
		
		writer.startElement(HTML.INPUT_ELEM);
		writer.writeAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_BUTTON);		
		writer.writeAttribute(HTML.ID_ATTR, _id);
		writer.writeAttribute(HTML.NAME_ATTR, _id);
		
		Object _value = this.getValue();
		if(_value != null) {
			writer.writeAttribute(HTML.VALUE_ATTR, _value);
		}
		
		String _style = this.getStyle();
		if(_style != null) {
			writer.writeAttribute(HTML.STYLE_ATTR, _style);
		}		
		String _styleClass = this.getStyleClass();
		if(_styleClass != null) {
			writer.writeAttribute(HTML.CLASS_ATTR, _styleClass);
		}
		
		writer.writeAttribute(HTML.ONCLICK_ATTR, this.getOnclickContent(hookClickName));
		
		writer.endElement(HTML.INPUT_ELEM);		
		return false;
	}
	
	private void makeHookClickScript(HtmlResponseWriter writer, String hookClickName) throws IOException {		
		String _onclick = this.getOnclick();
		if(_onclick == null) {
			_onclick = "return true;";
		}
				
		writer.startElement(HTML.SCRIPT_ELEM);	
		writer.writeAttribute(HTML.SCRIPT_TYPE_ATTR, HTML.SCRIPT_TYPE_TEXT_JAVASCRIPT);
		
		writer.writeText("window."+hookClickName+"=function(){try{"+_onclick+"}catch(e){alert(e.message);return false;}};");		
		
		writer.endElement(HTML.SCRIPT_ELEM);
	}
	
	private static final String METHOD_PREFIX = "method:";
//	private static final String ACTION_PREFIX = "action:";
	private String getOnclickContent(String hookClickName) {
		String methodName = this.getAction();
		methodName = "processTo" + methodName;
		
		String actionNname = METHOD_PREFIX + methodName;  
		return "Thread.start(this,[{callback:submitAction, params:'"+actionNname+"'},{callback:"+hookClickName+"}]);";
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
