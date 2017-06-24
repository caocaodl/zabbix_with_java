package com.isoft.zend.ext.standard;

import static com.isoft.zend.ext.standard.StrnatCmp.strnatcmp_ex;

public class ZStandardExport {
	
	private ZStandardExport() {
	}
	
	public static int natcasesort(String left, String right) {
		return strnatcmp_ex(left, right, true);
	}
	
	public static int natsort(String left, String right) {
		return strnatcmp_ex(left, right, false);
	}
	
}
