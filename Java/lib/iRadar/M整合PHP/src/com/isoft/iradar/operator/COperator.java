package com.isoft.iradar.operator;

import java.util.Map;

public final class COperator {

	public final static class CMapOperator {

		public final static <T extends Map> T add(T a, T b) {
			try {
				T sum = null;
				if (a != null) {
					sum = (T) a.getClass().newInstance();
				} else if (b != null) {
					sum = (T) b.getClass().newInstance();
				} else {
					return null;
				}
				realAdd(a, b, sum);
				return sum;
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		private final static void realAdd(Map a, Map b, Map sum) {
			if (a != null && !a.isEmpty()) {
				sum.putAll(a);
			}
			if (b != null && !b.isEmpty()) {
				if (sum.isEmpty()) {
					sum.putAll(b);
				} else {
					for (Object key : b.keySet()) {
						if (!sum.containsKey(key)) {
							sum.put(key, b.get(key));
						}
					}
				}
			}
		}
	}

}
