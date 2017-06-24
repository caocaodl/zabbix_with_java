package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.util.LinkedList;
import java.util.List;

public class DataGridItem {
	protected List<HeaderItem> headerList = new LinkedList<HeaderItem>();
	protected List<HeaderItem> frozenHeaderList = new LinkedList<HeaderItem>();
	protected List<ButtonItem> buttonList = new LinkedList<ButtonItem>();

	protected void addHeaderItem(HeaderItem item) {
		if (item.getFrozen() != null && item.getFrozen()) {
			frozenHeaderList.add(item);
		} else {
			headerList.add(item);
		}
	}	

	protected void addButtonItem(ButtonItem item) {
		buttonList.add(item);
	}
	
	protected DataGridItem subGrid;
	public DataGridItem getSubGrid() {
		return subGrid;
	}
	public void setSubGrid(DataGridItem subGrid) {
		this.subGrid = subGrid;
	}

	protected String id;
	protected String title;
	protected Boolean collapsible;
	protected Boolean fitColumns;
	protected String resizeHandle;
	protected Boolean autoRowHeight;
	protected String toolbar;
	protected Boolean striped;
	protected String method;
	protected Boolean nowrap;
	protected String idField;
	protected String url;
	protected String loadMsg;
	protected Boolean pagination;
	protected Boolean rownumbers;
	protected Boolean singleSelect;
	protected Boolean checkOnSelect;
	protected Boolean selectOnCheck;
	protected String pagePosition;
	protected Integer pageNumber;
	protected Integer pageSize;
	protected String pageList;
	// protected array pageList;
	protected String sortName;
	protected String sortOrder;
	protected Boolean multiSort;
	protected Boolean remoteSort;
	protected Boolean showHeader;
	protected Boolean showFooter;
	protected Integer scrollbarSize;
	protected String rowStyler;
	protected String loader;
	protected String loadFilter;
	protected String editors;
	protected String view;
	protected GroupViewItem groupView;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getCollapsible() {
		return collapsible;
	}

	public void setCollapsible(Boolean collapsible) {
		this.collapsible = collapsible;
	}

	public List<HeaderItem> getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List<HeaderItem> headerList) {
		this.headerList = headerList;
	}

	public List<HeaderItem> getFrozenHeaderList() {
		return frozenHeaderList;
	}

	public void setFrozenHeaderList(List<HeaderItem> frozenHeaderList) {
		this.frozenHeaderList = frozenHeaderList;
	}

	public Boolean getFitColumns() {
		return fitColumns;
	}

	public void setFitColumns(Boolean fitColumns) {
		this.fitColumns = fitColumns;
	}

	public String getResizeHandle() {
		return resizeHandle;
	}

	public void setResizeHandle(String resizeHandle) {
		this.resizeHandle = resizeHandle;
	}

	public Boolean getAutoRowHeight() {
		return autoRowHeight;
	}

	public void setAutoRowHeight(Boolean autoRowHeight) {
		this.autoRowHeight = autoRowHeight;
	}

	public String getToolbar() {
		return toolbar;
	}

	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}

	public Boolean getStriped() {
		return striped;
	}

	public void setStriped(Boolean striped) {
		this.striped = striped;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Boolean getNowrap() {
		return nowrap;
	}

	public void setNowrap(Boolean nowrap) {
		this.nowrap = nowrap;
	}

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLoadMsg() {
		return loadMsg;
	}

	public void setLoadMsg(String loadMsg) {
		this.loadMsg = loadMsg;
	}

	public Boolean getPagination() {
		return pagination;
	}

	public void setPagination(Boolean pagination) {
		this.pagination = pagination;
	}

	public Boolean getRownumbers() {
		return rownumbers;
	}

	public void setRownumbers(Boolean rownumbers) {
		this.rownumbers = rownumbers;
	}

	public Boolean getSingleSelect() {
		return singleSelect;
	}

	public void setSingleSelect(Boolean singleSelect) {
		this.singleSelect = singleSelect;
	}

	public Boolean getCheckOnSelect() {
		return checkOnSelect;
	}

	public void setCheckOnSelect(Boolean checkOnSelect) {
		this.checkOnSelect = checkOnSelect;
	}

	public Boolean getSelectOnCheck() {
		return selectOnCheck;
	}

	public void setSelectOnCheck(Boolean selectOnCheck) {
		this.selectOnCheck = selectOnCheck;
	}

	public String getPagePosition() {
		return pagePosition;
	}

	public void setPagePosition(String pagePosition) {
		this.pagePosition = pagePosition;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getPageList() {
		return pageList;
	}

	public void setPageList(String pageList) {
		this.pageList = pageList;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getMultiSort() {
		return multiSort;
	}

	public void setMultiSort(Boolean multiSort) {
		this.multiSort = multiSort;
	}

	public Boolean getRemoteSort() {
		return remoteSort;
	}

	public void setRemoteSort(Boolean remoteSort) {
		this.remoteSort = remoteSort;
	}

	public Boolean getShowHeader() {
		return showHeader;
	}

	public void setShowHeader(Boolean showHeader) {
		this.showHeader = showHeader;
	}

	public Boolean getShowFooter() {
		return showFooter;
	}

	public void setShowFooter(Boolean showFooter) {
		this.showFooter = showFooter;
	}

	public Integer getScrollbarSize() {
		return scrollbarSize;
	}

	public void setScrollbarSize(Integer scrollbarSize) {
		this.scrollbarSize = scrollbarSize;
	}

	public String getRowStyler() {
		return rowStyler;
	}

	public void setRowStyler(String rowStyler) {
		this.rowStyler = rowStyler;
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public String getLoadFilter() {
		return loadFilter;
	}

	public void setLoadFilter(String loadFilter) {
		this.loadFilter = loadFilter;
	}

	public String getEditors() {
		return editors;
	}

	public void setEditors(String editors) {
		this.editors = editors;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	public GroupViewItem getGroupView() {
		return groupView;
	}

	public void setGroupView(GroupViewItem groupView) {
		this.groupView = groupView;
	}
}
