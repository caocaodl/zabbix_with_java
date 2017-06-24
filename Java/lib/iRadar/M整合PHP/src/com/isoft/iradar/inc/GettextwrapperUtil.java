package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.vsprintf;

/**
 * TODO
 * @author benne
 *
 */
public class GettextwrapperUtil {
	
	private GettextwrapperUtil() {
	}
	
	/**
	 * Returns a formatted string.
	 *
	 * @param string _format		receives already stranlated string with format
	 * @param array  _arguments		arguments to replace according to given format
	 *
	 * @return string
	 */
	public static String _params(String format, Object... arguments) {
		return vsprintf(format, arguments);
	}
}
