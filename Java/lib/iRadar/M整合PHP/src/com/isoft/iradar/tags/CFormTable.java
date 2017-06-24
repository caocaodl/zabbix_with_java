package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_object;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.types.CArray.array;

import java.util.ArrayList;
import java.util.List;

import com.isoft.types.Mapper.TObj;

public class CFormTable extends CForm {
	
	private static final long serialVersionUID = 1L;
	
	private String align;
	private CHelp help;
	private String title;
	private String tableclass = "formtable";
	protected List top_items = new ArrayList();
	protected List center_items = new ArrayList();
	protected CCol bottom_items;
	
	public CFormTable(){
		this(null);
	}
	
	public CFormTable(String title){
		this(title, null);
	}
	
	public CFormTable(String title, String action){
		this(title, action, null);
	}
	
	public CFormTable(String title, String action, String method){
		this(title, action, method, null);
	}
	
	public CFormTable(String title, String action, String method, String enctype){
		this(title, action, method, enctype, null);
	}

	public CFormTable(String title, String action, String method, String enctype, String var) {
		super(is_null(method) ? "post" : method, action, enctype);
		method = is_null(method) ? "post" : method;

		setTitle(title);

		var = is_null(var) ? "form" : var;
		addVar(var, get_request(var, "1"));

		bottom_items = new CCol(SPACE, "form_row_last");
		bottom_items.setColSpan(2);
	}

	public void setName(String value) {
		attr("name", value);
		attr("id", rda_formatDomId(value));
	}

	public void setAlign(String value) {
		this.align = value;
	}
	
	public void setTitle() {
		setTitle(null);
	}

	public void setTitle(String value) {
		this.title = value;
	}
	
	public int setHelp() {
		return setHelp(null);
	}

	public int setHelp(Object value) {
		if (is_null(value)) {
			this.help = new CHelp();
		} else if (value instanceof CHelp) {
			this.help = (CHelp) value;
		} else if (is_string(value)) {
			this.help = new CHelp(TObj.as(value).asString());
			if (getName() == null) {
				setName(TObj.as(value).asString());
			}
		} else {
			return error("Incorrect value for setHelp \"" + value + "\".");
		}
		return 0;
	}
	
	public Object addRow(Object item1) {
		return this.addRow(item1, null);
	}

	public Object addRow(Object item1, Object item2) {
		return this.addRow(item1, item2, null);
	}
	
	public Object addRow(Object item1, Object item2, String classStyle) {
		return this.addRow(item1, item2, classStyle, null);
	}
	
	public Object addRow(Object item1, Object item2, String classStyle, String id) {
		if (is_object(item1) && item1 instanceof CRow) {
		} else if (is_object(item1) &&item1 instanceof CTable) {
			CCol _td = new CCol(item1, "form_row_c");
			_td.setColSpan(2);
			item1 = new CRow(_td);
		} else {
			if (is_string(item1)) {
				item1 = nbsp(TObj.as(item1).asString());
			}
			if (empty(item1)) {
				item1 = SPACE;
			}
			if (empty(item2)) {
				item2 = SPACE;
			}

			item1 = new CRow(
				array(
					new CCol(item1, "form_row_l"),
					new CCol(item2, "form_row_r")
				),
				classStyle
			);
		}

		if (!is_null(id) && item1 instanceof CTag) {
			((CTag)item1).attr("id", rda_formatDomId(id));
		}
		center_items.add(item1);

		return item1;
	}
	
	public void addSpanRow(Object value) {
		addSpanRow(value, null);
	}

	public void addSpanRow(Object value, String classStyle) {
		if (is_null(value)) {
			value = SPACE;
		}
		if (is_null(classStyle)) {
			classStyle = "form_row_c";
		}
		CCol col = new CCol(value, classStyle);
		col.setColSpan(2);
		center_items.add(new CRow(col));
	}

	public void addItemToBottomRow(Object value) {
		bottom_items.addItem(value);
	}

	public void setTableClass(Object classStyle) {
		if (is_string(classStyle)) {
			tableclass = (String)classStyle;
		}
	}

	public StringBuilder bodyToString() {
		StringBuilder res = super.bodyToString();
		CTable tbl = new CTable(null, tableclass);
		tbl.setCellSpacing(0);
		tbl.setCellPadding(1);
		tbl.setAlign(align);

		// add first row
		if (!is_null(title)) {
			CCol col = new CCol(null, "form_row_first");
			col.setColSpan(2);

			if (isset(help)) {
				col.addItem(help);
			}
			if (isset(title)) {
				col.addItem(title);
			}
			tbl.setHeader(col);
		}

		// add last row
		tbl.setFooter(bottom_items);

		// add center rows
		for (Object item : center_items) {
			tbl.addRow(item);
		}
		return res.append(tbl.toString());
	}
}
