package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.inc.Defines.IRADAR_API_VERSION;

import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.6")
public class CAPIInfo {

	/**
	 * Get API version.
	 * @return string
	 */
	public String version() {
		return IRADAR_API_VERSION;
	}

}
