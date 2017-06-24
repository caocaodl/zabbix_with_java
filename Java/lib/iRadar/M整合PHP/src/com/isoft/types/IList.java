package com.isoft.types;

import java.util.ArrayList;
import java.util.Collection;

public class IList<V> extends ArrayList<V> {

	private static final long serialVersionUID = 1L;
	
	public IList() {
		super();
	}
	
	public IList(V... es) {
		if (es != null) {
			for (V e : es) {
				add(e);
			}
		}
	}
	public IList(Collection<? extends V> c) {
		super(c);
	}


	public IList E(V value) {
		add(value);
		return this;
	}
}
