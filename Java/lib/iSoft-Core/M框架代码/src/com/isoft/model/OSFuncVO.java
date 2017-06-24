package com.isoft.model;

public class OSFuncVO {
	private String funcName;
	private String funcId;
	private String btName;
	private String btId;
	private String btDeps;
	private String btExtra;
	private String funcUrl;
	private Integer seqNo;
	private Integer role;
	
	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getFuncId() {
		return funcId;
	}

	public void setFuncId(String funcId) {
		this.funcId = funcId;
	}

	public String getBtName() {
		return btName;
	}

	public void setBtName(String btName) {
		this.btName = btName;
	}

	public String getBtId() {
		return btId;
	}

	public void setBtId(String btId) {
		this.btId = btId;
	}

	public String getBtDeps() {
		return btDeps;
	}

	public void setBtDeps(String btDeps) {
		this.btDeps = btDeps;
	}

	public String getBtExtra() {
		return btExtra;
	}

	public void setBtExtra(String btExtra) {
		this.btExtra = btExtra;
	}

	public String getFuncUrl() {
		return funcUrl;
	}

	public void setFuncUrl(String funcUrl) {
		this.funcUrl = funcUrl;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}
}
