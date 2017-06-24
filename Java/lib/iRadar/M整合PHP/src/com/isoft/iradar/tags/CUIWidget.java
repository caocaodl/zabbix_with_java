package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;

public class CUIWidget extends CDiv {

	private static final long serialVersionUID = 1L;
	
	public String domid;
	public Integer state;
	public String css_class;
	private CDiv _header;
	private CArray _body;
	private CDiv _footer;

	public CUIWidget(String id) {
		this(id, null);
	}

	public CUIWidget(String id, Object body) {
		this(id, body, null);
	}
	
	public CUIWidget(String id, Object body, Integer state) {
		super(null, "ui-widget ui-widget-content ui-helper-clearfix ui-corner-all widget");
		
		this.domid = id;
		this.state = state; // 0 - closed, 1 - opened
		this.css_class = "header";
		this._header = null;
		this._body = array(body);
		this._footer = null;

		setAttribute("id", id+"_widget");
	}

	public CObject addItem(Object item) {
		if (!is_null(item)) {
			_body.add( item );
		}
		return this;
	}

	public CDiv setHeader() {
		return setHeader(null);
	}
	
	public CDiv setHeader(String caption) {
		return setHeader(caption, SPACE);
	}
	
	public CDiv setHeader(String caption, Object icons) {
		icons = CArray.valueOf(icons);
		
		if (is_null(caption) && !is_null(icons)) {
			caption = SPACE;
		}
		this._header = new CDiv(null, "nowrap ui-corner-all ui-widget-header "+css_class);

		if (!is_null(this.state)) {
			CIcon icon = new CIcon(
				_("Show")+"/"+_("Hide"),
				asBoolean(this.state) ? "arrowup" : "arrowdown",
				"changeHatStateUI(this,'"+domid+"');"
			);
			icon.setAttribute("id", this.domid+"_icon");
			this._header.addItem(icon);
		}
		this._header.addItem(icons);
		this._header.addItem(caption);
		return this._header;
	}

	public CDiv setDoubleHeader(Object left, Object right) {
		CTable table = new CTable();
		table.addStyle("width: 100%;");
		CCol lCol = new CCol(left);
		lCol.addStyle("text-align: left; border: 0;");
		CCol rCol = new CCol(right);
		rCol.addStyle("text-align: right; border: 0;");
		table.addRow(array(lCol, rCol));

		this._header = new CDiv(null, "nowrap ui-corner-all ui-widget-header "+css_class);
		this._header.addItem(table);
		return this._header;
	}

	public CDiv setFooter(Object footer) {
		return setFooter(footer, false);
	}
	
	public CDiv setFooter(Object footer, boolean right) {
		this._footer = new CDiv(footer, "nowrap ui-corner-all ui-widget-header footer "+(right ? " right" : " left"));
		return this._footer;
	}

	public CUIWidget get() {
		cleanItems();
		super.addItem(_header);

		if (is_null(state)) {
			state = 1;
		}

		CDiv div = new CDiv(_body, "body");
		div.setAttribute("id", domid);

		if (state == 0) {
			div.setAttribute("style", "display: none;");
			if (!empty(_footer)) {
				_footer.setAttribute("style", "display: none;");
			}
		}

		super.addItem(div);
		super.addItem(_footer);
		return this;
	}

	public String toString() {
		return toString(true);
	}

	public String toString(boolean $destroy) {
		get();
		return super.toString($destroy);
	}
}
