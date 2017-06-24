package com.isoft.struts2.views.tags.ui.linkButton;

public class ParamItems implements java.io.Serializable{

	private static final long serialVersionUID = 1L;

	ParamItems(String name, Object value){
		this.name = name;
		this.value = value;
	}
	private String name;
	private Object value;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

}
