package com.isoft.iradar.model.params;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CParamWrapper extends CArray {

	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean containsKey(Object key) {
		if (key != null && key instanceof String) {
			Field f = getWrapperField(this.getClass(), (String)key);
			if (f != null) {
				return true;
			}
		}
		return super.containsKey(key);
	}

	@Override
	public Object get(Object key) {
		if (key != null && key instanceof String) {
			Field f = getWrapperField(this.getClass(), (String)key);
			if (f != null) {
				try {
					return f.get(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.get(key);
	}

	@Override
	public Object put(Object key, Object value) {
		if (key != null && key instanceof String) {
			Field f = getWrapperField(this.getClass(), (String)key);
			if (f != null) {
				try {
					autoSetField(f, value);
					return value;
				} catch (Exception e) {
					throw new IllegalArgumentException(key+ " => "+value, e);
				}
			}
		}
		return super.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		if (key != null && key instanceof String) {
			Field f = getWrapperField(this.getClass(), (String)key);
			if (f != null) {
				try {
					Object o = f.get(this);
					f.set(this, null);
					return o;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return super.remove(key);
	}

	/**
	 * 对容易出问题的设置类型进行特殊转换，主要 包括:CArray \ String[]
	 * 
	 * TODO: 最好能将CParamGet类的值类型进行统一
	 * 
	 * @param f
	 * @param value
	 * @throws Exception
	 */
	private void autoSetField(Field f, Object value) throws Exception {
		if(value != null) {
			Class type = f.getType();
			if(!type.isInstance(value)) {
				if (type.isAssignableFrom(CArray.class)) {
					value = CArray.valueOf(value);
				} else if (type.equals(Long[].class)) {
					value = TArray.as(value).asLong();
				} else if (type.equals(long[].class)) {
					value = TArray.as(value).asLong();
				} else if (type.equals(Integer[].class)) {
					value = TArray.as(value).asInteger();
				} else if (type.equals(int[].class)) {
					value = TArray.as(value).asInteger();
				} else if (type.equals(String[].class)) {
					value = TArray.as(value).asString();
				} else if (type.equals(Boolean.class)) {
					value = Nest.as(value).asBoolean();
				}
			}
		}
		f.set(this, value);
	}
	
	private final static CArray<CArray<Field>> CACHE_FIELDS = new CArray();
	private Field getWrapperField(Class clazz, String name){
		CArray<Field> cacheFields = makeCache(clazz);
		
		String qname = queryParamkey(name);
		if (cacheFields.containsKey(qname)) {
			return cacheFields.get(qname);
		} else if (!CParamWrapper.class.equals(clazz)) {
			return getWrapperField(clazz.getSuperclass(), name);
		}
		return null;
	}
	
	private static CArray<Field> makeCache(Class clazz) {
		CArray<Field> cacheFields = CACHE_FIELDS.get(clazz);
		if(cacheFields == null) {
			cacheFields = new CArray();
			CACHE_FIELDS.put(clazz, cacheFields);
			
			Field[] fields = clazz.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field f : fields) {
					f.setAccessible(true);
					String fieldName = f.getName();
					cacheFields.put(queryParamkey(fieldName), f);
				}
			}
		}
		return cacheFields;
	}
	
	private static String queryParamkey(String key) {
		return key.toLowerCase().replaceAll("_", "");
	}

	@Override
	public int size() {
		return makeCache(this.getClass()).size();
	}

	@Override
	public Set entrySet() {
		CArray<Field> fs = array();
		Class clz = this.getClass();
		while(!(clz.equals(CArray.class))) {
			fs.putAll(makeCache(clz));
			clz = clz.getSuperclass();
		}
		
		Set set = new HashSet(fs.size());
		
		for(Field f: fs) {
			boolean isNotStaticFinal = (f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0;
			if(f.isAccessible() && isNotStaticFinal) {
				set.add(new FieldEntry(this, f.getName()));
			}
		}
		
		return set;
	}
	
}

class FieldEntry implements Map.Entry<String, Object>{
	private CParamWrapper param;
	private String name;
	
	public FieldEntry(CParamWrapper param, String name) {
		this.param = param;
		this.name = name;
	}
	
	@Override
	public String getKey() {
		return name;
	}

	@Override
	public Object getValue() {
		return param.get(getKey());
	}

	@Override
	public Object setValue(Object value) {
		throw new UnsupportedOperationException();
	}
}
