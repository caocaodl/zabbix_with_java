package com.isoft.struts2.views.tags.ui.core;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.common.IdentityBean;
import com.isoft.model.FuncItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HTML;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.utils.CacheUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class IaaSBody extends AndurilUIComponent {

	private String onload;
	private String onunload;
	private String oncontextmenu;
	private String style;
	private boolean hasNoticeLayer;
	private boolean hasLeftMenuLayer;

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

	public boolean isHasNoticeLayer() {
		return hasNoticeLayer;
	}

	public void setHasNoticeLayer(boolean hasNoticeLayer) {
		this.hasNoticeLayer = hasNoticeLayer;
	}

	public boolean isHasLeftMenuLayer() {
		return hasLeftMenuLayer;
	}

	public void setHasLeftMenuLayer(boolean hasLeftMenuLayer) {
		this.hasLeftMenuLayer = hasLeftMenuLayer;
	}

	public IaaSBody(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.startElement("body");
		if (this.getId() != null && this.getId().length() > 0) {
			writer.writeAttribute(HTML.ID_ATTR, this.getId());
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
		
		IdentityBean idBean = getIdentityBean();
		int mask = 0;
		if (idBean != null && idBean.getTenantRole() != null) {
			mask = idBean.getTenantRole().magic();
		}
		
		FuncItem selectedFunc = null;
		String funcId = request.getParameter("funcId");
		if(funcId != null && funcId.length()>0){
			List<FuncItem> funcList = CacheUtil.getNavFuncList();
			for(FuncItem func:funcList){
				if(func.getId().equals(funcId)){
					selectedFunc = func;
					break;
				}
			}
		} else {
			selectedFunc = encodeNavigatorFuncLayer(writer, mask);
		}
		
		this.hasLeftMenuLayer = this.hasLeftMenuLayer && !selectedFunc.getSubFuncList().isEmpty();
		if(this.hasLeftMenuLayer){
			encodeLeftMenuLayer(selectedFunc, writer, mask);
		} else {
			selectedFuncItem = selectedFunc;
		}
		writer.write("");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
//		encodeConsoleLayer(writer);
		if(this.hasLeftMenuLayer){
			String baseUrl = getContextPath();
			writer.write("<script type=\"text/javascript\" src=\""+baseUrl+"/assets/javascripts/content.js\"></script>\n");
		}
		writer.endElement("body");
		return false;
	}
	
	private void encodeNoticeLayer(Writer writer) throws IOException {
		String userName = null;
		IdentityBean idBean = getIdentityBean();
		if(idBean!=null){
			userName = idBean.getUserName();
		} else {
			userName = "";
		}
		
		Date currDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyy年MM月dd日 EEE",Locale.CHINA); //EEE 表示星期 
		String cnDate = df.format(currDate);
		df = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA); //EEE 表示星期 
		String enDate = df.format(currDate);
		
		writer.write("<div class=\"header\">\n");
		writer.write("<div class=\"welcome\">欢迎 "+userName+" !</div>\n");
		writer.write("<div class=\"logout\"><a href=\"javascript:void(0);\" onclick=\"window.parent.location='"+getContextPath()+"/logout.action'\">退出</a></div>\n");
		writer.write("<time datetime=\""+enDate+"\">"+cnDate+"</time>\n");
		writer.write("</div>\n");
	}
	
	private FuncItem encodeNavigatorFuncLayer(Writer writer, int mask) throws IOException {
		this.linkCss(writer, FuncBody.class);
		writer.write("<div id=\"topLayer\">\n");
		writer.write("<div class=\"isoft_nav container\">\n");
		writer.write("<a href=\"#\" class=\"logo\"></a>\n");
		
		if(this.hasNoticeLayer){
			encodeNoticeLayer(writer);
		}
/*		
		writer.write("<ul class='nav_list'>\n");
		
		String baseUrl = getContextPath();
		String funcUrl = baseUrl + "/platform/workspace.action?funcId=";
		String selectedFuncId = this.request.getParameter("funcId");
		boolean selected = false;
		FuncItem selectedFunc = null;
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		
		for (FuncItem item : funcList) {
			
			if((Integer.valueOf(item.getRole()) & mask) ==0){
				continue;
			}
			
//			if(StringUtils.isEmpty(selectedFuncId)){
//				selectedFuncId = item.getId();
//			}
			
			selected = item.getId().equals(selectedFuncId);
			
			writer.write("<li>\n");
			
			if(selected){
				writer.write("<a id='nav_3' href=\""+(funcUrl+item.getId())+"\" target=\"bodySegment\" onclick=\"goNav(this)\" class=\"current\">\n");
				selectedFunc = item;
			} else {
				writer.write("<a id='nav_3' href=\""+(funcUrl+item.getId())+"\" target=\"bodySegment\" onclick=\"goNav(this)\">\n");	
			}
			
			writer.write(item.getFuncName());
			writer.write("<span class=\"cl\"></span>\n");
			writer.write("<span class=\"cr\"></span>\n");
			writer.write("</a>\n");
			writer.write("</li>\n");
		}
		
		writer.write("<i class='nav_bg_l'></i>\n");
		writer.write("<i class='nav_bg_r'></i>\n");
		writer.write("</ul>\n");
*/
		writer.write("</div>\n");
		writer.write("</div>\n");
		
		//return selectedFunc;
		return null;
	}
	
	@SuppressWarnings("unused")
	private FuncItem encodeNavigatorFuncLayer1(HtmlResponseWriter writer, int mask) throws IOException {
		writer.write("<div id=\"topLayer\">\n");
		writer.write("<div class=\"topMenu container\">\n");
		writer.write("<a href=\"#\" class=\"logo\"></a>\n");
		writer.write("<ul>\n");
		
		String baseUrl = getContextPath();
		String funcUrl = baseUrl + "/platform/workspace.action?funcId=";
		String selectedFuncId = this.request.getParameter("funcId");
		boolean selected = false;
		FuncItem selectedFunc = null;
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		
		for (FuncItem item : funcList) {
			
			if((Integer.valueOf(item.getRole()) & mask) ==0){
				continue;
			}
			
			if(StringUtils.isEmpty(selectedFuncId)){
				selectedFuncId = item.getId();
			}
			
			selected = item.getId().equals(selectedFuncId);
			
			writer.write("<li>\n");
			if(selected){
				writer.write("<a class=\"link over\">\n");
				selectedFunc = item;
			} else {
				writer.write("<a href=\""+(funcUrl+item.getId())+"\" class=\"link\">\n");
			}
			
			writer.write(item.getFuncName());
			writer.write("<span class=\"cl\"></span>\n");
			writer.write("<span class=\"cr\"></span>\n");
			writer.write("</a>\n");
			writer.write("</li>\n");
		}
		
		writer.write("</ul>\n");
		writer.write("</div>\n");
		writer.write("</div>\n");
		
		return selectedFunc;
	}
	
	private FuncItem selectedFuncItem = null;

	public void encodeLeftMenuLayer(FuncItem funcItem, Writer writer, int mask) throws IOException {
		if(funcItem == null){
			return;
		}
		
		writer.write("<div id=\"menuLayer\">\n");
		writer.write("<div class=\"leftMenu container\">\n");
		writer.write("<div class=\"con container\">\n");		
		
		encodeFuncMenu(writer, funcItem, false, mask);
		
		writer.write("</div>\n");
		
//		writer.write("<div class=\"top\">\n");
//		writer.write("<div class=\"title container\">我的控制台</div>\n");
//		writer.write("<div class=\"titleMask container\"></div>\n");
//		writer.write("</div>\n");
		
		writer.write("<div class=\"mask\"></div>\n");
		writer.write("</div>\n");
		
		writer.write("<div id=\"frame-bg\"></div>\n");
		writer.write("</div>\n");
		
	}

	public boolean encodeFuncMenu(Writer writer, FuncItem funcItem , boolean selected, int mask)
			throws IOException {
		String baseUrl = getContextPath();
		boolean renderModuleFunc = (funcItem.isEntrance() && !funcItem.isLeaf() && !funcItem.isMultiLevelSubs());
		if(renderModuleFunc){
			String menuIcon = "/assets/images/arrow.gif";
			writer.write("<a class=\"but container menuFolder\">"+funcItem.getFuncName()+"<img src=\""+(baseUrl+menuIcon)+"\" /></a>\n");
			writer.write("<p class=\"container menuFolding\">\n");
		}
		
		for(FuncItem item:funcItem.getSubFuncList()){
			if((Integer.valueOf(item.getRole()) & mask) ==0){
				continue;
			}
			if(item.isLeaf()){
				if(!selected){
					selectedFuncItem = item;
				}
				writer.write("<a href=\""+(baseUrl+item.getFuncUrl())+"\" class=\"chilBut container"+(!selected?" chilOver":"")+"\" target=\"rightConsole\">"+item.getFuncName()+"</a>\n");
				selected = true;
			} else {
				selected = encodeFuncMenu(writer, item, selected, mask);
			}

		}
		if(renderModuleFunc){
			writer.write("</p>\n");
		}
		return selected;
	}
	
	@SuppressWarnings("unused")
	private void encodeConsoleLayer(HtmlResponseWriter writer) throws IOException {
		String baseUrl = getContextPath();
		writer.write("<div id=\"contentLayerFrameWrapper\"");
		if(!this.hasLeftMenuLayer){
			writer.write(" style=\"margin-left:0px;\"");
		}
		writer.write(">\n");
		if (selectedFuncItem != null) {
			writer.write("<iframe id=\"contentLayerFrame\" src=\""+(baseUrl+selectedFuncItem.getFuncUrl())+"\" frameborder=0 name=\"contentFrame\" onLoad=\"iFrameHeight()\" height='600px'>\n");
			writer.write("</iframe>\n");
		}
		writer.write("</div>");
	}

}
