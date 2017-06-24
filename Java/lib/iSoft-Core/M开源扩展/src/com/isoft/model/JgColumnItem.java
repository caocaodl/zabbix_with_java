package com.isoft.model;

import java.io.Serializable;

public class JgColumnItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String name;
	private String width;
	private String align;
	private boolean frozen;
	private String formatter;
	private String jsformatter;
	private String jsunformatter;
	private String formatoptions;
	private boolean sortable;
	
	private boolean editable;
	private String editType;
	private String editOptions;
	private String editRules;
	
	private boolean hidden;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}

	public String getEditOptions() {
		return editOptions;
	}

	public void setEditOptions(String editOptions) {
		this.editOptions = editOptions;
	}

	public String getEditRules() {
		return editRules;
	}

	public void setEditRules(String editRules) {
		this.editRules = editRules;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public JgColumnItem(String title, String name, String width, String align,
			boolean frozen, String formatter,boolean sortable) {
		this.title = title;
		this.name = name;
		this.width = width;
		this.align = align;
		this.frozen = frozen;
		this.formatter = formatter;
		this.sortable = sortable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public String getJsformatter() {
		return jsformatter;
	}

	public void setJsformatter(String jsformatter) {
		this.jsformatter = jsformatter;
	}

	public String getJsunformatter() {
		return jsunformatter;
	}

	public void setJsunformatter(String jsunformatter) {
		this.jsunformatter = jsunformatter;
	}

	public String getFormatoptions() {
		return formatoptions;
	}

	public void setFormatoptions(String formatoptions) {
		this.formatoptions = formatoptions;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
}
