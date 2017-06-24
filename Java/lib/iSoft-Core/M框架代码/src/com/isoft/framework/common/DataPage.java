package com.isoft.framework.common;

import java.io.Serializable;
import java.util.List;

import com.isoft.web.common.A_PaginationAction;

public class DataPage implements Serializable {
	private static final long serialVersionUID = 1L;

	private int start = 0;
	private int limit = 15;
	private String order;
	private String sort;
	private int page = 0;
	private int totalCount =0;
	private boolean success = true;
	@SuppressWarnings("unchecked")
	private List list;
	
	public DataPage(String start, String limit) {
		try {
			this.start = Integer.valueOf(start);
		} catch (NumberFormatException e) {
			this.start = 0;
		}
		try {
			this.limit = Integer.valueOf(limit);
		} catch (NumberFormatException e) {
			this.limit = 10;
		}		
	}
	
	public DataPage(int start, int limit) {
		this.start = start;
		this.limit = limit;
	}
	
	public DataPage(boolean isJqFrid, int page, int limit) {
		this.page = page;
		this.limit = limit;
		this.start = (page - 1) * limit;
	}
	
	public DataPage(boolean isJqFrid, A_PaginationAction pagerAction) {
		this.page = pagerAction.getPage();
		this.limit = pagerAction.getRows();
		this.start = (page - 1) * limit;
		this.order = pagerAction.getOrder();
		this.sort = pagerAction.getSort();
	}
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public int getTotalPage(){
		return ((this.totalCount-this.totalCount%this.limit)/this.limit) + (this.totalCount%this.limit>0?1:0);
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		return list;
	}

	@SuppressWarnings("unchecked")
	public void setList(List list) {
		this.list = list;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
