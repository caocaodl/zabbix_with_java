package com.isoft.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class MapVO {

	public Map vo2Map() {
		Map mvo = new HashMap();
		Field[] fields = this.getClass().getDeclaredFields();
		if (fields != null) {
			for (Field f : fields) {
				try {
					Object v = PropertyUtils.getProperty(this, f.getName());
					mvo.put(f.getName(), v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mvo;
	}

}
