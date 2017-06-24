package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $DataSubGridTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $DataSubGrid(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		$DataSubGrid grid = ($DataSubGrid)component;
		DataGridItem item = grid.getModel();
		item.setId(grid.getId());
		item.setTitle(this.title);
		item.setCollapsible(getPropertyBooleanValue(this.collapsible,true));
		item.setFitColumns(getPropertyBooleanValue(this.fitColumns,true));
		item.setResizeHandle(this.resizeHandle);
		item.setAutoRowHeight(getPropertyBooleanValue(this.autoRowHeight,false));
		item.setToolbar(this.toolbar);
		item.setStriped(getPropertyBooleanValue(this.striped,true));
		item.setMethod(this.method);
		item.setNowrap(getPropertyBooleanValue(this.nowrap,true));
		item.setIdField(this.idField);
		item.setUrl(this.url);
		item.setLoadMsg(this.loadMsg);
		item.setPagination(getPropertyBooleanValue(this.pagination,true));
		item.setRownumbers(getPropertyBooleanValue(this.rownumbers,true));
		item.setSingleSelect(getPropertyBooleanValue(this.singleSelect,true));
		item.setCheckOnSelect(getPropertyBooleanValue(this.checkOnSelect,false));
		item.setSelectOnCheck(getPropertyBooleanValue(this.selectOnCheck,true));
		item.setPagePosition(this.pagePosition);
		item.setPageNumber(getPropertyIntegerValue(this.pageNumber));
		item.setPageSize(getPropertyIntegerValue(this.pageSize,10));
		item.setPageList(pageList);
		item.setSortName(this.sortName);
		item.setSortOrder(this.sortOrder);
		item.setMultiSort(getPropertyBooleanValue(this.multiSort,false));
		item.setRemoteSort(getPropertyBooleanValue(this.remoteSort,true));
		item.setShowHeader(getPropertyBooleanValue(this.showHeader,true));
		item.setShowFooter(getPropertyBooleanValue(this.showFooter,true));
		item.setScrollbarSize(getPropertyIntegerValue(this.scrollbarSize));
		item.setRowStyler(this.rowStyler);
		item.setLoader(this.loader);
		item.setLoadFilter(this.loadFilter);
		item.setEditors(this.editors);
		item.setView(this.view);
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
		this.collapsible = null;
		this.fitColumns = null;
		this.resizeHandle = null;
		this.autoRowHeight = null;
		this.toolbar = null;
		this.striped = null;
		this.method = null;
		this.nowrap = null;
		this.idField = null;
		this.url = null;
		this.loadMsg = null;
		this.pagination = null;
		this.rownumbers = null;
		this.singleSelect = null;
		this.checkOnSelect = null;
		this.selectOnCheck = null;
		this.pagePosition = null;
		this.pageNumber = null;
		this.pageSize = null;
		this.pageList = null;
		this.sortName = null;
		this.sortOrder = null;
		this.multiSort = null;
		this.remoteSort = null;
		this.showHeader = null;
		this.showFooter = null;
		this.scrollbarSize = null;
		this.rowStyler = null;
		this.loader = null;
		this.loadFilter = null;
		this.editors = null;
		this.view = null;
	}

	private String title;
	private String collapsible;
	private String fitColumns;
	private String resizeHandle;
	private String autoRowHeight;
	private String toolbar;
	private String striped;
	private String method;
	private String nowrap;
	private String idField;
	private String url;
	private String loadMsg;
	private String pagination;
	private String rownumbers;
	private String singleSelect;
	private String checkOnSelect;
	private String selectOnCheck;
	private String pagePosition;
	private String pageNumber;
	private String pageSize;
	private String pageList;
	private String sortName;
	private String sortOrder;
	private String multiSort;
	private String remoteSort;
	private String showHeader;
	private String showFooter;
	private String scrollbarSize;
	private String rowStyler;
	private String loader;
	private String loadFilter;
	private String editors;
	private String view;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCollapsible(String collapsible) {
		this.collapsible = collapsible;
	}

	public void setFitColumns(String fitColumns) {
		this.fitColumns = fitColumns;
	}

	public void setResizeHandle(String resizeHandle) {
		this.resizeHandle = resizeHandle;
	}

	public void setAutoRowHeight(String autoRowHeight) {
		this.autoRowHeight = autoRowHeight;
	}

	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}

	public void setStriped(String striped) {
		this.striped = striped;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setNowrap(String nowrap) {
		this.nowrap = nowrap;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setLoadMsg(String loadMsg) {
		this.loadMsg = loadMsg;
	}

	public void setPagination(String pagination) {
		this.pagination = pagination;
	}

	public void setRownumbers(String rownumbers) {
		this.rownumbers = rownumbers;
	}

	public void setSingleSelect(String singleSelect) {
		this.singleSelect = singleSelect;
	}

	public void setCheckOnSelect(String checkOnSelect) {
		this.checkOnSelect = checkOnSelect;
	}

	public void setSelectOnCheck(String selectOnCheck) {
		this.selectOnCheck = selectOnCheck;
	}

	public void setPagePosition(String pagePosition) {
		this.pagePosition = pagePosition;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageList(String pageList) {
		this.pageList = pageList;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setMultiSort(String multiSort) {
		this.multiSort = multiSort;
	}

	public void setRemoteSort(String remoteSort) {
		this.remoteSort = remoteSort;
	}

	public void setShowHeader(String showHeader) {
		this.showHeader = showHeader;
	}

	public void setShowFooter(String showFooter) {
		this.showFooter = showFooter;
	}

	public void setScrollbarSize(String scrollbarSize) {
		this.scrollbarSize = scrollbarSize;
	}

	public void setRowStyler(String rowStyler) {
		this.rowStyler = rowStyler;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public void setLoadFilter(String loadFilter) {
		this.loadFilter = loadFilter;
	}

	public void setEditors(String editors) {
		this.editors = editors;
	}

	public void setView(String view) {
		this.view = view;
	}

}
