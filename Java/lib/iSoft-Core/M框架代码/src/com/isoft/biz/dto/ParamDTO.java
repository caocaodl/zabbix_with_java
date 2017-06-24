package com.isoft.biz.dto;

import java.util.List;
import java.util.Map;

import com.isoft.framework.common.DataPage;
import com.isoft.biz.dto.BaseDTO;

public class ParamDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	public DataPage getDataPage() {
		return dataPage;
	}

	public void setDataPage(DataPage dataPage) {
		this.dataPage = dataPage;
	}

	public Object[] getArrayParam() {
		return arrayParam;
	}

	public void setArrayParam(Object[] arrayParam) {
		this.arrayParam = arrayParam;
	}

	@SuppressWarnings("unchecked")
	public List getListParam() {
		return listParam;
	}

	@SuppressWarnings("unchecked")
	public void setListParam(List listParam) {
		this.listParam = listParam;
	}

	@SuppressWarnings("unchecked")
	public Map getMapParam() {
		return mapParam;
	}

	@SuppressWarnings("unchecked")
	public void setMapParam(Map mapParam) {
		this.mapParam = mapParam;
	}

	public String getStrParam() {
		return strParam;
	}

	public void setStrParam(String strParam) {
		this.strParam = strParam;
	}

	public Integer getIntParam() {
		return intParam;
	}

	public void setIntParam(Integer intParam) {
		this.intParam = intParam;
	}

	public Boolean getBoolParam() {
		return boolParam;
	}

	public void setBoolParam(Boolean boolParam) {
		this.boolParam = boolParam;
	}

	public Object getObjParam() {
		return objParam;
	}

	public void setObjParam(Object objParam) {
		this.objParam = objParam;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private DataPage dataPage;
	private Object[] arrayParam;
	@SuppressWarnings("unchecked")
	private List listParam;
	@SuppressWarnings("unchecked")
	private Map mapParam;
	private String strParam;
	private Integer intParam;
	private Boolean boolParam;
	private Object objParam;
	private String id;

}
