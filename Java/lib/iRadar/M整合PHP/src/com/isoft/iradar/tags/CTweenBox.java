package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_bool;
import static com.isoft.iradar.Cphp.is_int;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;

public class CTweenBox {
	
	private CForm form;
	private String name;
	private String varname;
	private Object value;
	private String id_l;
	private String id_r;
	private CListBox lbox;
	private CListBox rbox;
	
	private boolean disableRemovebtn;
	
	public CTweenBox(CForm form, String name) {
		this(form, name, null);
	}
	
	public CTweenBox(CForm form, String name, Object value) {
		this(form, name, value, 10);
	}
	
	public CTweenBox(CForm form, String name, Object value, int size) {
		rda_add_post_js("if (IE7) $$(\"select option[disabled]\").each(function(e) { e.setStyle({color: \"gray\"}); });");

		this.form = form;
		this.name = name+"_tweenbox";
		this.varname = name;
		this.value = rda_toHash(value);
		this.id_l = this.varname+"_left";
		this.id_r = this.varname+"_right";
		this.lbox = new CListBox(this.id_l, null, size);
		this.rbox = new CListBox(this.id_r, null, size);
		this.lbox.setAttribute("style", "width: 280px;");
		this.rbox.setAttribute("style", "width: 280px;");
	}

	public void setName(String name) {
		if (is_string(name)) {
			this.name = name;
		}
	}

	public String getName() {
		return this.name;
	}
	
	public boolean isDisableRemovebtn() {
		return disableRemovebtn;
	}

	public void setDisableRemovebtn(boolean disableRemovebtn) {
		this.disableRemovebtn = disableRemovebtn;
	}

	public void addItem(Object value, String caption) {
		addItem(value, caption, null);
	}
	
	public void addItem(Object value, String caption, Object selected) {
		addItem(value, caption, selected, true);
	}
	
	public void addItem(Object value, String caption, Object selected, Boolean enabled) {
		if (is_null(selected)) {
			if (isArray(this.value)) {
				if (isset(((CArray)this.value).get(value))) {
					selected = 1;
				}
			} else if (value.equals(this.value)) {
				selected = 1;
			}
		}
		if ((is_bool(selected) && (Boolean)selected)
				|| (is_int(selected) && (Integer)selected != 0)
				|| (is_string(selected) && ("yes".equals(selected) || "selected".equals(selected) || "on".equals(selected) ))) {
			this.lbox.addItem(value, caption, null, enabled);
			this.form.addVar(this.varname+"["+value+"]", value);
		}
		else {
			this.rbox.addItem(value, caption, null, enabled);
		}
	}

	public CTable get() {
		return get(null);
	}
	
	public CTable get(String _caption_l) {
		return get(_caption_l, null);
	}
	
	public CTable get(String caption_l, Object  caption_r) {
		if (empty(caption_l)) {
			caption_l = _("In");
		}
		if (empty(caption_r)) {
			caption_r = _("Other");
		}

		CTable grp_tab = new CTable(null, "tweenBoxTable");
		grp_tab.attr("name", this.name);
		grp_tab.attr("id", rda_formatDomId(this.name));
		grp_tab.setCellSpacing(0);
		grp_tab.setCellPadding(0);

		if (!is_null(caption_l) || !is_null(caption_r)) {
			grp_tab.addRow(array(caption_l, SPACE, caption_r));
		}

		CArray btns = array();
		CButton add_btn = new CButton("add", "  &laquo;  ", null, "formlist");
		add_btn.setAttribute("onclick", "moveListBoxSelectedItem(\""+this.form.getName()+"\", \""+this.varname+"\", \""+this.id_r+"\", \""+this.id_l+"\", \"add\");");
		btns.add(add_btn);
		CButton rmv_btn= null;
		if(!disableRemovebtn){
			rmv_btn = new CButton("remove", "  &raquo;  ", null, "formlist");
			rmv_btn.setAttribute("onclick", "moveListBoxSelectedItem(\""+this.form.getName()+"\", \""+this.varname+"\", \""+this.id_l+"\", \""+this.id_r+"\", \"rmv\");");
			btns.add(BR());
			btns.add(rmv_btn);
		}

		grp_tab.addRow(array(this.lbox, new CCol(btns), this.rbox));
		return grp_tab;
	}

	public void show() {
		get(null);
	}
	
	public void show(String caption_l) {
		get(caption_l, null);
	}
	
	public void show(String caption_l, String  caption_r) {
		if (empty(caption_l)) {
			caption_l = _("In");
		}
		if (empty(caption_r)) {
			caption_r = _("Other");
		}
		CTable tab = this.get(caption_l, caption_r);
		tab.show();
	}

	public String toString() {
		return this.get().toString();
	}
}
