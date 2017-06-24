package com.isoft.model;

import java.io.Serializable;

public class JgButtonItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String separator;

	private String btOper;
	private String btCaption;
	private String btIcon;
	private String btTitle;
	private String onClick;

	private String dlCaption;
	private String dlMsg;
	private String dlIframe;
	private String dlReloadAfterSubmit;
	private String dlBeforeShowForm;
	private String dlBeforeInitData;
	private String dlAfterShowForm;
	private String dlBeforeSubmit;
	private String dlOnclickSubmit;
	private String dlAfterSubmit;
	
	private String options;

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getBtOper() {
		return btOper;
	}

	public void setBtOper(String btOper) {
		this.btOper = btOper;
	}

	public String getBtCaption() {
		return btCaption;
	}

	public void setBtCaption(String btCaption) {
		this.btCaption = btCaption;
	}

	public String getBtIcon() {
		return btIcon;
	}

	public void setBtIcon(String btIcon) {
		this.btIcon = btIcon;
	}

	public String getBtTitle() {
		return btTitle;
	}

	public void setBtTitle(String btTitle) {
		this.btTitle = btTitle;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getDlCaption() {
		return dlCaption;
	}

	public String getDlMsg() {
		return dlMsg;
	}

	public void setDlMsg(String dlMsg) {
		this.dlMsg = dlMsg;
	}

	public String getDlIframe() {
		return dlIframe;
	}

	public void setDlIframe(String dlIframe) {
		this.dlIframe = dlIframe;
	}

	public void setDlCaption(String dlCaption) {
		this.dlCaption = dlCaption;
	}

	public String getDlReloadAfterSubmit() {
		return dlReloadAfterSubmit;
	}

	public void setDlReloadAfterSubmit(String dlReloadAfterSubmit) {
		this.dlReloadAfterSubmit = dlReloadAfterSubmit;
	}

	public String getDlBeforeShowForm() {
		return dlBeforeShowForm;
	}

	public void setDlBeforeShowForm(String dlBeforeShowForm) {
		this.dlBeforeShowForm = dlBeforeShowForm;
	}

	public String getDlBeforeInitData() {
		return dlBeforeInitData;
	}

	public void setDlBeforeInitData(String dlBeforeInitData) {
		this.dlBeforeInitData = dlBeforeInitData;
	}

	public String getDlAfterShowForm() {
		return dlAfterShowForm;
	}

	public void setDlAfterShowForm(String dlAfterShowForm) {
		this.dlAfterShowForm = dlAfterShowForm;
	}

	public String getDlBeforeSubmit() {
		return dlBeforeSubmit;
	}

	public void setDlBeforeSubmit(String dlBeforeSubmit) {
		this.dlBeforeSubmit = dlBeforeSubmit;
	}

	public String getDlOnclickSubmit() {
		return dlOnclickSubmit;
	}

	public void setDlOnclickSubmit(String dlOnclickSubmit) {
		this.dlOnclickSubmit = dlOnclickSubmit;
	}

	public String getDlAfterSubmit() {
		return dlAfterSubmit;
	}

	public void setDlAfterSubmit(String dlAfterSubmit) {
		this.dlAfterSubmit = dlAfterSubmit;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

}
