package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilUITag;
import com.opensymphony.xwork2.util.ValueStack;

public class $ColumnTag extends AndurilUITag {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		return new $Column(stack, req, res);
	}

	@Override
	protected void setProperties(AndurilComponent component) {
		super.setProperties(component);
		ColumnItem item = new ColumnItem();
		item.setTitle(title);
		item.setField(field);
		item.setWidth(getPropertyIntegerValue(width));
		item.setRowspan(getPropertyIntegerValue(rowspan));
		item.setColspan(getPropertyIntegerValue(colspan));
		item.setAlign(align);
		item.setHalign(halign);
		item.setSortable(getPropertyBooleanValue(sortable, null));
		item.setOrder(order);
		item.setResizable(getPropertyBooleanValue(resizable, null));
		item.setFixed(getPropertyBooleanValue(fixed, null));
		item.setHidden(getPropertyBooleanValue(hidden, null));
		item.setCheckbox(getPropertyBooleanValue(checkbox, null));
		item.setFormatter(formatter);
		item.setDitemtype(ditemtype);
		item.setStyler(styler);
		item.setSorter(sorter);
		item.setEditor(editor);
		(($Column) component).setModel(item);
	}

	@Override
	public void release() {
		super.release();
		this.title = null;
		this.field = null;
		this.width = null;
		this.rowspan = null;
		this.colspan = null;
		this.align = null;
		this.halign = null;
		this.sortable = null;
		this.order = null;
		this.resizable = null;
		this.fixed = null;
		this.hidden = null;
		this.checkbox = null;
		this.formatter = null;
		this.ditemtype = null;
		this.styler = null;
		this.sorter = null;
		this.editor = null;
	}

	private String title;
	private String field;
	private String width;
	private String rowspan;
	private String colspan;
	private String align;
	private String halign;
	private String sortable;
	private String order;
	private String resizable;
	private String fixed;
	private String hidden;
	private String checkbox;
	private String formatter;
	private String ditemtype;
	private String styler;
	private String sorter;
	private String editor;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setRowspan(String rowspan) {
		this.rowspan = rowspan;
	}

	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setHalign(String halign) {
		this.halign = halign;
	}

	public void setSortable(String sortable) {
		this.sortable = sortable;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setResizable(String resizable) {
		this.resizable = resizable;
	}

	public void setFixed(String fixed) {
		this.fixed = fixed;
	}

	public void setHidden(String hidden) {
		this.hidden = hidden;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public void setDitemtype(String ditemtype) {
		this.ditemtype = ditemtype;
	}

	public void setStyler(String styler) {
		this.styler = styler;
	}

	public void setSorter(String sorter) {
		this.sorter = sorter;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

}
