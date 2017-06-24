package com.isoft.biz.db.orm;

import java.math.BigInteger;

public class RadarColumnFilter extends ColumnFilter {

	@Override
	public Object doFilterBigInteger(String label, BigInteger v) {
		if (label != null && label.endsWith("id") && v == null) {
			return 0L;
		}
		if(v == null){
			return null;
		}
		return v.longValue();
	}

}
