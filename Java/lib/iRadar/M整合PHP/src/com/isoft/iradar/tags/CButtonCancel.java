package com.isoft.iradar.tags;

import com.isoft.iradar.Cphp;

public class CButtonCancel extends CButton {
	
	private static final long serialVersionUID = 1L;
	
	public CButtonCancel() {
		this(null);
	}
	
	public CButtonCancel(String vars) {
		this(vars, null);
	}
	
	public CButtonCancel(String vars, String action) {
		this(vars, action, null);
	}

	public CButtonCancel(String vars, String action, String styleclass) {
		super("cancel", Cphp._("Cancel"), action, styleclass);
		if(Cphp.is_null(action)){
			this.setVars(vars);
		}
	}

	public void setVars(String value) {
		String url = "?cancel=1";
		if (!Cphp.empty(value)) {
			url += value;
		}
		Curl uri = new Curl(url);
		url = uri.getUrl();
		this.setAttribute("onclick", "javascript: return redirect('" + url + "');");
	}
}
