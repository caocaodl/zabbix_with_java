package com.isoft.imon.topo.admin.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;

/**
 * 数据字典工厂类
 * 
 * @author Administrator
 * 
 */
public final class DictionaryFactory {
	private static DictionaryFactory factory = new DictionaryFactory();
	private final Map<String, List<DictionaryEntry>> entries;

	public static DictionaryFactory getFactory() {
		return factory;
	}

	private DictionaryFactory() {
		this.entries = new HashMap<String, List<DictionaryEntry>>();
		loadConfigSettings();
	}

	/**
	 * 注册数据字典实体
	 * 
	 * @param childName
	 * @param des
	 */
	public void registerEntries(String childName, List<DictionaryEntry> des) {
		if ((childName != null) && (des != null))
			this.entries.put(childName, des);
	}

	public void registerEntries(Element root) {
		if ((root == null) || (root.getChildren() == null)) {
			return;
		}
		List<Element> childs = root.getChildren();
		for (Element child : childs) {
			String name = child.getAttributeValue("name");
			List<Element> _items = child.getChildren();
			List<DictionaryEntry> items = new ArrayList<DictionaryEntry>(_items.size());
			for (Element _item : _items) {
				DictionaryEntry item = new DictionaryEntry();
				item.setKey(_item.getAttributeValue("key"));
				item.setValue(_item.getAttributeValue("value"));
				items.add(item);
			}
			this.entries.put(name, items);
		}
	}

	/**
	 * 获取数据字典实体
	 * 
	 * @param childName
	 * @return
	 */
	public List<DictionaryEntry> getEntries(String childName) {
		List<DictionaryEntry> list = this.entries.get(childName);
		if (list == null) {
			throw new NullPointerException();
		}
		// return
		// Collections.unmodifiableList((List<DictionaryEntry>)this.entries.get(childName));
		return Collections.unmodifiableList(list);
	}

	/**
	 * 获取实体值，参数key为String类型
	 * 
	 * @param childName
	 * @param key
	 * @return
	 */
	public String getEntryValue(String childName, String key) {
		List<DictionaryEntry> items = (List<DictionaryEntry>) this.entries.get(childName);
		if (items != null) {
			for (DictionaryEntry item : items) {
				if (item.getKey().equals(key))
					return item.getValue();
			}
		}
		return null;
	}

	/**
	 * 获取实体值，参数key为int类型
	 * 
	 * @param childName
	 * @param key
	 * @return
	 */
	public String getEntryValue(String childName, int key) {
		return getEntryValue(childName, String.valueOf(key));
	}

	/**
	 * 获取实体集合
	 * 
	 * @param childName
	 * @return
	 */
	public Map<String, String> getEntryMap(String childName) {
		if (!this.entries.containsKey(childName)) {
			return null;
		}
		List<DictionaryEntry> items = (List<DictionaryEntry>) this.entries.get(childName);
		Map<String, String> map = new HashMap<String, String>();
		for (DictionaryEntry item : items)
			map.put(item.getKey(), item.getValue());
		return map;
	}

	/**
	 * 加载配置设置
	 */
	private void loadConfigSettings() {
	}
}
