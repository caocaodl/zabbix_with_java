package com.isoft.event.args;

import java.util.Date;

import com.isoft.framework.events.EventArgs;

public class EArgsHost implements EventArgs {
	
	public static enum Opertaion{
		ADD,
		DELETE,
		;
	}
	
	private Long[] hostIds;
	private Long userId;
	private Date timeStamp;
	private Opertaion opertaion;
	
	public EArgsHost(Opertaion op) {
		this.opertaion = op;
		this.timeStamp = new Date();
	}

	public Long[] getHostId() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Opertaion getOpertaion() {
		return opertaion;
	}

	public void setOpertaion(Opertaion opertaion) {
		this.opertaion = opertaion;
	}
	
}
