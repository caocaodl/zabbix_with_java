package com.isoft.web.common;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.common.DataPage;

public abstract class A_PaginationAction extends BasePageAction {

	private String start;
	private String limit;
	private int page;
	private int rows;
	private String order;
	private String sort;
	
	private String oper;
	
	private DataPage dataPage;	

	public String doOper() throws Exception {
		if(StringUtils.isNotEmpty(this.oper)){
			try{
			String op = StringUtils.capitalize(this.oper);
			Method method = getClass().getMethod("doOper"+op);
			return (String)method.invoke(this);
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
		return super.execute();
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}
	
	public void set(String start) {
		this.start = start;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
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

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public DataPage getDataPage() {
		return dataPage;
	}

	protected void setDataPage(DataPage dataPage) {
		this.dataPage = dataPage;
	}

}
