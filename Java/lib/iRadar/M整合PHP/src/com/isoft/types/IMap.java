package com.isoft.types;

import java.util.LinkedHashMap;

public class IMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	public IMap() {
	}

	public IMap(IEntry<K, V>... es) {
		if (es != null) {
			for (IEntry<K, V> e : es) {
				put(e.key, e.value);
			}
		}
	}

	public IMap E(K key, V value) {
		put(key, value);
		return this;
	}
}
