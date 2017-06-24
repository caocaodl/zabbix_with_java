package com.isoft.types;

public class IEntry<K, V> {
	protected K key;
	protected V value;

	public IEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
}
