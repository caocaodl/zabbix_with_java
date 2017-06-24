package com.isoft.iradar.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.iradar.Cphp;

public class CButtonPopup extends CButton {
	
	private static final long serialVersionUID = 1L;

	protected Map<String, String> options = new HashMap<String, String>();
	protected int width = 450;
	protected int height = 450;

	public CButtonPopup(Map<String, String> options) {
		super("button_popup", Cphp._("Select"), null, "formlist");
		this.options = options;
	}
	
	public void setCaption(String caption) {
		this.attr("value", caption);
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString(Boolean destroy) {
		this.addAction("onclick", this.getPopupAction());
		return super.toString(destroy);
	}
	
	protected String getPopupAction() {
		List<String> params = new ArrayList<String>();
		for(Entry<String, String> e:this.options.entrySet()){
			params.add(e.getKey()+"="+e.getValue());
		}

		return "return PopUp(\"popup.action?"+Cphp.implode("&", params.toArray(new String[0]))+"\", "+this.width+", "+this.height+");";
	}
}
