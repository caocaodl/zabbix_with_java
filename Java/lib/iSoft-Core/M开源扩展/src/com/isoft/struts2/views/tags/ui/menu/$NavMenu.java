package com.isoft.struts2.views.tags.ui.menu;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.common.IdentityBean;
import com.isoft.model.FuncItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.isoft.utils.CacheUtil;
import com.opensymphony.xwork2.util.ValueStack;

public class $NavMenu extends AndurilUIComponent {

	private String nav;
	private String homeTitle;
	private String homeUrl;
	
	public String getNav() {
		return nav;
	}

	public void setNav(String nav) {
		this.nav = nav;
	}

	public String getHomeTitle() {
		return homeTitle;
	}

	public void setHomeTitle(String homeTitle) {
		this.homeTitle = homeTitle;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public $NavMenu(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		// String baseUrl = getContextPath();
		// String selectedFuncId = this.request.getParameter("funcId");

		// FuncItem selectedFunc = null;
		// String funcId = request.getParameter("funcId");
		// if(funcId != null && funcId.length()>0){
		// List<FuncItem> funcList = CacheUtil.getNavFuncList();
		// for(FuncItem func:funcList){
		// if(func.getId().equals(funcId)){
		// selectedFunc = func;
		// break;
		// }
		// }
		// } else {
		// selectedFunc = encodeNavigatorFuncLayer(writer, mask);
		// }
		writer.write("<ul class='header_nav' id='JS_mainNav' data-initial='{");
		writer.write(String.format(JSON_PROP_COMMA, "nav", this.getNav()));
		writer.write("\"home\":{");
		writer.write(String.format(JSON_PROP_COMMA, "title", this.getHomeTitle()));
		writer.write(String.format(JSON_PROP, "url", this.getHomeUrl()));
		writer.write("}");
		writer.write("}'>");
		encodeNavMenu(writer);
		writer.write("</ul>");
		return false;
	}

	private void encodeNavMenu(Writer writer) throws IOException {
		IdentityBean idBean = getIdentityBean();
		int mask = 0;
		if (idBean != null && idBean.getTenantRole() != null) {
			mask = idBean.getTenantRole().magic();
		}
		boolean isAdmin = idBean.isAdmin();

		StringBuilder segment = new StringBuilder();
		Map<String, Boolean> permModuleIds = idBean.getPermModuleIds();
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		int navMenuCnt = 0;
		for (FuncItem item : funcList) {
			if ((Integer.valueOf(item.getRole()) & mask) == 0) {
				continue;
			}
			if(!item.isEntrance()){
				continue;
			}
			if (!isAdmin) {
				if (permModuleIds == null
						|| !permModuleIds.containsKey(item.getId())) {
					continue;
				}
			}
			if (navMenuCnt++ > 0) {
				segment.append("<li class='split'></li>");
			}
			segment.append("<li><a ");
			segment.append(String.format(HTML_PROP_SPACE, "class", item.getIconClass()+" "+item.getId()));
			segment.append("href='#|' data-menu='{");
			segment.append(String.format(JSON_PROP_COMMA, "type", item.getRenderType()));
			segment.append(String.format(JSON_PROP_COMMA, "cls", item.getRenderStyle()));
			
			if ("nomenu".equals(item.getRenderType())) {
				segment.append(String.format(JSON_PROP_COMMA, "url", item.getFuncUrl()));
			} else if ("tree".equals(item.getRenderType())) {
				if (item.getRenderUrl() != null && item.getRenderUrl().length() > 0) {
					segment.append(String.format(JSON_PROP_COMMA, "initial", "1"));
					segment.append(String.format(JSON_PROP_COMMA, "url", item.getRenderUrl()));
				} else {
					segment.append(String.format(JSON_PROP_COMMA, "initial", "1"));
					List<FuncItem> subFuncs = item.getSubFuncList();
					if (!subFuncs.isEmpty()) {
						segment.append("\"data\":[");
						int subMenuCnt = 0;
						for (FuncItem subItem : subFuncs) {
							if (encodeSubMenu(segment, subItem, mask, isAdmin,
									permModuleIds, subMenuCnt)) {
								subMenuCnt++;
							}
						}
						segment.append("],");
					}
				}
			}
			segment.append(String.format(JSON_PROP, "copyright", "iSoft"));
			segment.append("}'");

			segment.append(">");
			segment.append(item.getFuncName());
			segment.append("</a></li>");
		}
		writer.write(segment.toString());
	}

	private boolean encodeSubMenu(StringBuilder segment, FuncItem item,
			int mask, boolean isAdmin, Map<String, Boolean> permModuleIds,
			int menuCnt) {
		if ((Integer.valueOf(item.getRole()) & mask) == 0) {
			return false;
		}
		if(!item.isEntrance()){
			return false;
		}
		if (!isAdmin) {
			if (permModuleIds == null
					|| !permModuleIds.containsKey(item.getId())) {
				return false;
			}
		}
		if (menuCnt > 0) {
			segment.append(",");
		}
		segment.append("{");
		segment.append(String.format(JSON_PROP_COMMA, "id", item.getId()));
		segment.append(String.format(JSON_PROP_COMMA, "text", item.getFuncName()));
		segment.append(String.format(JSON_PROP_COMMA, "iconCls", item.getIconClass()));
		if (!StringUtils.isEmpty(item.getFuncUrl())) {
			segment.append(String.format(JSON_PROP_COMMA, "href", item.getFuncUrl()));
		}
		if (!StringUtils.isEmpty(item.getRenderUrl())) {
			segment.append(String.format(JSON_PROP_COMMA, "url", item.getRenderUrl()));
			segment.append(String.format(JSON_PROP_COMMA, "state", "closed"));
		} else {
			List<FuncItem> subFuncs = item.getSubFuncList();
			if (!subFuncs.isEmpty()) {
				segment.append("\"children\":[");
				int subMenuCnt = 0;
				for (FuncItem subItem : subFuncs) {
					if (encodeSubMenu(segment, subItem, mask, isAdmin,
							permModuleIds, subMenuCnt)) {
						subMenuCnt++;
					}
				}
				segment.append("],");
			}
		}
		segment.append(String.format(JSON_PROP, "copyright", "iSoft"));
		segment.append("}");
		return true;
	}

}
