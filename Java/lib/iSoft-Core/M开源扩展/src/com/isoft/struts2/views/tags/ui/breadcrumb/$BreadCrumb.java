package com.isoft.struts2.views.tags.ui.breadcrumb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.BreadCrumbItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.BreadCrumbHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $BreadCrumb extends AndurilUIComponent implements BreadCrumbHolder {

    private List<BreadCrumbItem> _breadCrumbItemList;
    
	public $BreadCrumb(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
		this._breadCrumbItemList = new ArrayList<BreadCrumbItem>(1);
	}
	
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return true;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {

		writer.write("<div class=\"breadcrumb\">\n");
		writer.write("<p>当前位置：<a>我的控制台</a><span>");
		for (BreadCrumbItem item : _breadCrumbItemList) {
			writer.write("&gt;</span><a>"+item.getLabel()+"</a>");
		}
		writer.write("</p>\n");
		writer.write("</div>\n");
		
        return false;
    }
    
    
    public void pushBreadCrumbItem(BreadCrumbItem breadCrumbItem) {
        this._breadCrumbItemList.add(breadCrumbItem);
    }
    
}
