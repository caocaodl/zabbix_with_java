package com.isoft.imon.topo.core.utils;

import java.math.BigDecimal;

public abstract class NumberUtil {
	
	public final static BigDecimal valueOf(float f){
		return new BigDecimal(String.valueOf(f));
	}
	
	public final static BigDecimal valueOf(double f){
		return new BigDecimal(String.valueOf(f));
	}
	
	public final static BigDecimal valueOf(long f){
		return new BigDecimal(String.valueOf(f));
	}
}
