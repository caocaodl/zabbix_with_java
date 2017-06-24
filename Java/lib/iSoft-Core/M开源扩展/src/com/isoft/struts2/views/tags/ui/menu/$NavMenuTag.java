package com.isoft.struts2.views.tags.ui.menu;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $NavMenuTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $NavMenu(stack, req, res);
	}
	
	
	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		
		$NavMenu aui = ($NavMenu) component;
		aui.setNav(nav);
		aui.setHomeTitle(homeTitle);
		aui.setHomeUrl(homeUrl);
	}


	@Override
	public void release() {
		super.release();
		this.nav = null;
		this.homeTitle = null;
		this.homeUrl = null;
	}
	
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
}
