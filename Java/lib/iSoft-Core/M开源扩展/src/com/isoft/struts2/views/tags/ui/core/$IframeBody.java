package com.isoft.struts2.views.tags.ui.core;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.framework.common.IdentityBean;
import com.isoft.model.FuncItem;
import com.isoft.model.PermItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.utils.CacheUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class $IframeBody extends AndurilUIComponent {

	private String onload;
	private String onunload;
	private String oncontextmenu;
	private String style;
	private String styleClass;
	private boolean narrow;
	private boolean autoCrumb = true;

	public String getOnload() {
		return onload;
	}

	public void setOnload(String onload) {
		this.onload = onload;
	}

	public String getOnunload() {
		return onunload;
	}

	public void setOnunload(String onunload) {
		this.onunload = onunload;
	}

	public String getOncontextmenu() {
		return oncontextmenu;
	}

	public void setOncontextmenu(String oncontextmenu) {
		this.oncontextmenu = oncontextmenu;
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

	public boolean isNarrow() {
		return narrow;
	}

	public void setNarrow(boolean narrow) {
		this.narrow = narrow;
	}

	public boolean isAutoCrumb() {
		return autoCrumb;
	}

	public void setAutoCrumb(boolean autoCrumb) {
		this.autoCrumb = autoCrumb;
	}

	public $IframeBody(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.startElement("body");
		if (this.getId() != null && this.getId().length() > 0) {
			writer.writeAttribute(HTML.ID_ATTR, this.getId());
		}
		if (this.styleClass != null && this.styleClass.length() > 0) {
			writer.writeAttribute(HTML.CLASS_ATTR, this.styleClass);
		}
		if (this.onload != null && this.onload.length() > 0) {
			writer.writeAttribute("onload", this.onload);
		}
		if (this.onunload != null && this.onunload.length() > 0) {
			writer.writeAttribute("onunload", this.onunload);
		}
		if (this.oncontextmenu != null && this.oncontextmenu.length() > 0) {
			writer.writeAttribute("oncontextmenu", this.oncontextmenu);
		}
		writer.write("<div class=\"conLayer\">");
		
		if(this.autoCrumb) {
			encodeBreadCrumb(writer);
		}
		
		writer.write("<div class='section'>");
		
//		for (String css : TabPanel.cssstyle) {
//			linkCss(writer, css);
//		}
//		for (String js : TabPanel.javascript) {
//			linkJavaScript(writer, js);
//		}
//		writer.write("<div id='iaasConsole' class='easyui-tabs' style=''>");
//		String title = getBreadCrumbTitle();
//		writer.write("<div title='" + title + "' style='padding:10px;'>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
//		writer.write("</div>");
//		writer.write("</div>");
		writer.write("</div>");
		writer.write("</div>\n");
		
//		writer.write("<script type='text/javascript'>");
//		writer.write("function addConsoleTab(title, url){");
//		writer.write("if ($('#iaasConsole').tabs('exists', title)){");
//		writer.write("$('#iaasConsole').tabs('select', title);");
//		writer.write("} else {");
//		writer.write("var content = '<iframe scrolling=\"auto\" frameborder=\"0\"  src=\"'+url+'\" style=\"width:100%;height:100%;\"></iframe>';");
//		writer.write("$('#iaasConsole').tabs('add',{");
//		writer.write("title:title,");
//		writer.write("content:content,");
//		writer.write("closable:true");
//		writer.write("});");
//		writer.write("}");
//		writer.write("}");		
//		writer.write("</script>");
		
		if(this.narrow){
			writer.write("<style type='text/css'>\n");
			writer.write(".conLayer .section {\n");
			writer.write(" margin-top:0px;\n");
			writer.write(" margin-left:0px;\n");
			writer.write(" margin-right:0px;\n");
			writer.write(" margin-bottom:0px;\n");
			writer.write("}\n");
			writer.write("</style>\n");
		}
		writer.endElement("body");
		return false;
	}
	
	private void encodeBreadCrumb(HtmlResponseWriter writer) throws IOException {
		String viewId = (String) request.getAttribute("struts.request_uri");
		String ctxPath = getContextPath();
		viewId = viewId.substring(ctxPath.length());
		List<PermItem> permList = CacheUtil.getPermByViewId(viewId);
		IdentityBean idBean = getIdentityBean();
		int mask = 0;
		if (idBean != null && idBean.getTenantRole() != null) {
			mask = idBean.getTenantRole().magic();
		}
		String curFuncId = null;
		if(permList!=null && !permList.isEmpty()){
			for(PermItem item:permList){
				if(item.getRole() == mask){
					curFuncId = item.getFuncId();
					break;
				}
			}
		}
		if(curFuncId == null){
			curFuncId = viewId;
		}
		FuncItem funcLeaf = CacheUtil.getNavFuncByFuncId(curFuncId);
		if (funcLeaf != null) {
			writer.write("<div class=\"breadcrumb\">");
			writer.write("<p>当前位置：<a>我的控制台</a><span>");
			StringBuilder breadCrumb = new StringBuilder("&gt;</span><a>"+funcLeaf.getFuncName()+"</a>");
			
			FuncItem pFunc = funcLeaf;
			while((pFunc = pFunc.getParentFunc())!=null){
				breadCrumb.insert(0, "&gt;</span><a>"+pFunc.getFuncName()+"</a>");
			}
			writer.write(breadCrumb.toString());
			writer.write("</p>");
			writer.write("</div>");
		}
	}
	
	@SuppressWarnings("unused")
	private String getBreadCrumbTitle() throws IOException {
		String viewId = (String) request.getAttribute("struts.request_uri");
		String ctxPath = getContextPath();
		viewId = viewId.substring(ctxPath.length());

		//FuncItem funcLeaf = CacheUtil.getNavFuncByViewId(viewId);
		FuncItem funcLeaf = null;
		if (funcLeaf != null) {
			StringBuilder breadCrumb = new StringBuilder("&nbsp;&gt;&nbsp;"
					+ funcLeaf.getFuncName());

			FuncItem pFunc = funcLeaf;
			while ((pFunc = pFunc.getParentFunc()) != null) {
				if(pFunc.isEntrance()){
					breadCrumb.insert(0, "&nbsp;&gt;&nbsp;" + pFunc.getFuncName());
				}
			}
			breadCrumb.insert(0, "当前位置&nbsp;:&nbsp;我的控制台");
			return breadCrumb.toString();
		}
		return "";
	}
	
}
