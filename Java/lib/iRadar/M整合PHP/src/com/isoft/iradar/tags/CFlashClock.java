package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.urlencode;

import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.6")
public class CFlashClock extends CFlash {

	private static final long serialVersionUID = 1L;

	private String src;
	private String timeType;
	private String timeError;
	private String timeZone;
	private Long timeOffset;

	public CFlashClock() {
		this(200);
	}
	
	public CFlashClock(Object width) {
		this(width, 200);
	}
	
	public CFlashClock(Object width, Object height) {
		this(width, height, null);
	}
	
	public CFlashClock(Object width, Object height, String url) {
		super(url=(
						!is_null(url)?
						"images/flash/rdaclock.swf?analog=1&smooth=1&url="+urlencode(url)
					  : "images/flash/rdaclock.swf?analog=1&smooth=1"),
				  (!is_numeric(width) || (Integer)width < 24)?200:(Integer)width,
				  (!is_numeric(height) || (Integer)height < 24)?200:(Integer)height
		);
		this.src = url;
		this.timeError = null;
		this.timeType = null;
		this.timeZone = null;
		this.timeOffset = null;
	}

	public void setTimeType(String value) {
		this.timeType = value;
	}

	public void setTimeZone(String value) {
		this.timeZone = value;
	}

	public void setTimeOffset(Long value) {
		this.timeOffset = value;
	}

	public void setTimeError(String value) {
		this.timeError = value;
	}

	@Override
	public StringBuilder bodyToString() {
		String src = this.src;
		if (!empty(this.timeError)) {
			src += "&timeerror="+this.timeError;
		}
		if (!empty(this.timeType)) {
			src += "&timetype="+urlencode(this.timeType);
		}
		if (!is_null(this.timeZone)) {
			src += "&timezone="+urlencode(this.timeZone);
		}
		if (!is_null(this.timeOffset)) {
			src += "&timeoffset="+this.timeOffset;
		}
		this.setSrc(src);
		return super.bodyToString();
	}
}
