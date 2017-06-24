package com.isoft.iradar.tags;

import java.io.IOException;
import java.io.PrintWriter;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;

public class CTable extends CTag {
	
	private static final long serialVersionUID = 1L;

	public String headerClass;
	public String footerClass;
	protected String oddRowClass;
	protected String evenRowClass;
	protected String header;
	protected String footer;
	protected int colnum;
	protected int rownum;
	protected String message;

	public CTable() {
		this(null, null);
	}

	public CTable(String message) {
		this(message, null);
	}

	public CTable(String message, String styleclass) {
		super("table", "yes");
		this.attr("class", styleclass);
		this.rownum = 0;
		this.oddRowClass = null;
		this.evenRowClass = null;
		this.header = "";
		this.headerClass = null;
		this.footer = "";
		this.footerClass = null;
		this.colnum = 1;
		this.message = message;
	}

	public void setOddRowClass(String value) {
		this.oddRowClass = value;
	}

	public void setEvenRowClass(String value) {
		this.evenRowClass = value;
	}

	public void setAlign(String value) {
		this.setAttribute("align", value);
	}

	public void setCellPadding(int value) {
		this.setAttribute("cellpadding", value);
	}

	public void setCellSpacing(int value) {
		this.setAttribute("cellspacing", value);
	}

	public CRow prepareRow(Object item) {
		return this.prepareRow(item, null, null);
	}

	public CRow prepareRow(Object item, String styleClass) {
		return this.prepareRow(item, styleClass, null);
	}

	public CRow prepareRow(Object item, String styleClass, String id) {
		Object ret = item;
		if (Cphp.is_null(ret)) {
			return null;
		}
		if (ret instanceof CCol[]) {
			CCol[] col = (CCol[]) ret;
			if (!Cphp.empty(this.header) && !Cphp.isset(col[0].attributes, "colspan")) {
				col[0].attributes.put("colspan", this.colnum);
			}
			ret = new CRow(col, styleClass, id);
		}
		if (ret instanceof CCol) {
			CCol col = (CCol) ret;
			if (!Cphp.empty(this.header) && !Cphp.isset(col.attributes, "colspan")) {
				col.attributes.put("colspan", this.colnum);
			}
			ret = new CRow(col, styleClass, id);
		}
		if (ret instanceof CRow) {
			CRow row = (CRow) ret;
			row.attr("class", styleClass);
		} else {
			ret = new CRow(ret, styleClass, id);
		}

		CRow crow = (CRow) ret;
		if (!Cphp.isset(crow.attributes, "class")
				|| Cphp.isArray(crow.getAttribute("class"))) {
			String sclass = (this.rownum % 2 > 0) ? this.oddRowClass
					: this.evenRowClass;
			crow.attr("class", sclass);
			crow.attr("origClass", sclass);
		}
		return crow;
	}

	public void setHeader() {
		this.setHeader(null);
	}

	public void setHeader(Object value) {
		this.setHeader(value, "header");
	}

	public void setHeader(Object value, String styleclass) {
		if (value != null) {
			if (Cphp.is_null(styleclass)) {
				styleclass = this.headerClass;
			}
			CRow crow = null;
			if (value instanceof CRow) {
				crow = (CRow) value;
				if (!Cphp.is_null(styleclass)) {
					crow.setAttribute("class", styleclass);
				}
			} else {
				crow = new CRow(value, styleclass);
			}
			this.colnum = crow.itemsCount();
			this.header = crow.toString();
		}
	}

	public void setFooter() {
		this.setFooter(null);
	}

	public void setFooter(Object value) {
		this.setFooter(value, "footer");
	}

	public void setFooter(Object value, String styleclass) {
		if (Cphp.is_null(styleclass)) {
			styleclass = this.footerClass;
		}
		this.footer = String.valueOf(this.prepareRow(value, styleclass));
	}

	public CObject addRow(Object item) {
		return this.addRow(item, null, null);
	}

	public CObject addRow(Object item, String styleclass) {
		return this.addRow(item, styleclass, null);
	}

	public CObject addRow(Object item, String styleclass, String id) {
		CObject ret = addItem(prepareRow(item, styleclass, id));
		this.rownum++;
		return ret;
	}

	public void showRow(Object item, String styleclass, String id) {
		RadarContext ctx = RadarContext.getContext();
		try {
			PrintWriter writer = ctx.getResponse().getWriter();
			writer.write(prepareRow(item, styleclass, id).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rownum++;
	}

	public int getNumRows() {
		return this.rownum;
	}

	public StringBuilder startToString() {
		StringBuilder ret = super.startToString();
		ret.append(this.header);
		return ret;
	}

	public StringBuilder endToString() {
		StringBuilder ret = new StringBuilder();
		if (this.rownum == 0 && Cphp.isset(this.message)) {
			CRow crow = prepareRow(new CCol(this.message, "message"));
			ret.append(crow.toString());
		}
		ret.append(this.footer);
		ret.append(super.endToString());
		return ret;
	}
}
