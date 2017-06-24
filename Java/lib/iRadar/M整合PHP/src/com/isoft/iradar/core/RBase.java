package com.isoft.iradar.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.types.CArray.map;

import com.isoft.types.CArray;

public class RBase {

	/**
	 * An array of available themes.
	 * @return array
	 */
	public static CArray<String> getThemes() {
		return map(
			"classic", _("Classic"),
			"originalblue", _("Original blue"),
			"darkblue", _("Black & Blue"),
			"darkorange", _("Dark orange")
		);
	}

}
