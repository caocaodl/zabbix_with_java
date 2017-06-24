package com.isoft.types;

import static com.isoft.iradar.Cphp.array_add;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.types.CArray.array;
import static org.apache.commons.beanutils.ConvertUtils.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import com.isoft.iradar.Cphp;



public class Mapper extends MapUtils {

	private Mapper() {
	}

	public static Map marshal(Object obj) {
		Object ov = null;
		Map params = new HashMap();
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			try {
				f.setAccessible(true);
				ov = f.get(obj);
				if (ov != null) {
					params.put(f.getName(), ov);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return params;
	}

	public static void unmarshal(Map v, Object obj) {
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			try {
				f.setAccessible(true);
				f.set(obj, v.get(f.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static final class Nest {
		
		public static TNew args(Object... objs){
			return new TNew(objs);
		}
		
		public static TObj as(Object obj){
			return new TObj(obj);
		}
		
		public static TObject value(Object obj, Object... keys) {
			if(obj instanceof Map) {
				return value((Map)obj, keys);
			}else if(obj instanceof String){
				if(keys.length == 1) {
					String s = (String)obj;
					
					Object k = keys[0];
					if(k instanceof Number) {
						k = ((Number)k).longValue();
					}else {
						k = 0;
					}
					
					CArray container = CArray.valueOf(s.toCharArray());
					return value(container, k);
				}else {
					throw new UnsupportedOperationException("Nest String arguments length must be 1!");
				}
			}else {
				return TNull.INSTANCE;
			}
		}
		
		public static TObject value(Map obj, Object... keys) {
			if (keys == null || keys.length == 0) {
				throw new IllegalArgumentException("至少传入一个参数");
			}
			Map container = obj;
			int i =0;
			for (; i < keys.length - 1; i++) {
				Object key = keys[i];
				Map parent = container;
				if (parent != null) {
					container = (Map) parent.get(key);
					if (container == null) {
						container = new CArray();
						parent.put(key, container);
					}
				}
			}
			return new TObject(container, keys[i]);
		}

		public static TArray array(Map obj, Object... keys) {
			if (keys == null || keys.length == 0) {
				throw new IllegalArgumentException("至少传入一个参数");
			}
			if (obj == null) {
				return null;
			}
			Object o = obj;
			for (Object k : keys) {
				o = ((Map) o).get(k);
			}
			return new TArray(o);
		}
		
		public static TMerge merge(Map... srcs) {
			return new TMerge(srcs);
		}
	}
	
	public static class TObj<T> {
		protected T obj;
		
		public static <T> TObj<T> as(T obj){
			return new TObj(obj);
		}

		private TObj(T obj) {
			this.obj = obj;
		}

		public T $() {
			return this.obj;
		}

		public TObj<T> $(T obj) {
			this.obj = obj;
			return this;
		}
		
		public CArray $s() {
			return $s(false);
		}
		public CArray $s(boolean autoFill) {
			if(this.obj == null) {
				if(autoFill) {
					$((T)array());
				}else {
					return array(); 
				}
			}
			return (CArray)this.obj;
		}
		
		public void plus(Object obj) {
			Object me = $();
			if(me instanceof Number && obj instanceof Number) {
				Number nme = (Number)me;
				Number nobj = (Number)obj;
				if (me instanceof Integer) {
					me = nme.intValue() + nobj.intValue();
				} else if (me instanceof Long) {
					me = nme.longValue() + nobj.longValue();
				} else if (me instanceof Float) {
					me = nme.floatValue() + nobj.floatValue();
				} else if (me instanceof Double) {
					me = nme.doubleValue() + nobj.doubleValue();
				} else if (me instanceof Short) {
					me = nme.shortValue() + nobj.shortValue();
				} else if (me instanceof Byte) {
					me = nme.byteValue() + nobj.byteValue();
				}
				
				$((T)me);
			}else if(me instanceof String){
				me = (String)me + obj;
				$((T)me);
			}else if(me instanceof CArray && obj instanceof Map) {
				array_add((CArray)me, (Map)obj);
			}else if(me == null) {
				$((T)obj);
			}else {
				throw new IllegalArgumentException(me.getClass().toString() +" => " + obj.getClass().toString());
			}
		}
		
		public void push(Object obj) {
			$s(true).add(obj);
		}
		
		public CArray asCArray() {
			return (CArray) convert(this.obj, CArray.class);
		}
		
		public CArray asCArray(boolean nullAsEmpty) {
			if(nullAsEmpty && this.obj == null) return new CArray();
			return (CArray) convert(this.obj, CArray.class);
		}

		public String asString() {
			return (String) convert(this.obj, String.class);
		}
		
		public String asString(boolean nullAsEmpty) {
			if(nullAsEmpty && this.obj == null) return "";
			return (String) convert(this.obj, String.class);
		}
		
		public char asChar() {
			return (Character) convert(this.obj, Character.class);
		}

		public byte asByte() {
			return asByte(false);
		}

		public Byte asByte(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Byte) convert(this.obj, Byte.class);
			}
		}

		public double asDouble() {
			return asDouble(false);
		}

		public Double asDouble(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Double) convert(this.obj, Double.class);
			}
		}

		public float asFloat() {
			return asFloat(false);
		}

		public Float asFloat(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Float) convert(this.obj, Float.class);
			}
		}

		public int asInteger() {
			return asInteger(false);
		}

		public Integer asInteger(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Integer) convert(this.obj, Integer.class);
			}
		}
		
		public Integer asRealInteger() {
			if(Cphp.is_numeric(this.obj)) {
				return (Integer) convert(this.obj, Integer.class);
			}
			return null;
		}

		public long asLong() {
			return asLong(false);
		}

		public Long asLong(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Long) convert(this.obj, Long.class);
			}
		}
		
		public Long asRealLong() {
			if(Cphp.is_numeric(this.obj)) {
				return (Long) convert(this.obj, Long.class);
			}
			return null;
		}

		public short asShort() {
			return asShort(false);
		}

		public Short asShort(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return (Short) convert(this.obj, Short.class);
			}
		}
		
		public boolean asBoolean() {
			if (this.obj instanceof Number) {
				return !empty(this.obj);
			}
			return (Boolean) convert(this.obj, Boolean.class);
		}

		public Boolean asBoolean(boolean canBeNull) {
			if (canBeNull && this.obj == null) {
				return null;
			} else {
				return this.asBoolean();
			}
		}
	}
	
	public static final class TNull extends TObject {
		public final static TNull INSTANCE = new TNull();
		
		private TNull() {
			super(null, null);
		}

		public TNull $(Object obj) {
			return this;
		}

		@Override
		public CArray $s(boolean autoFill) {
			return array();
		}

		@Override
		public void plus(Object obj) {
		}

		@Override
		public void push(Object obj) {
		}
	}

	public static class TObject extends TObj<Object> {
		private Map container;
		private Object key;

		public static TObject as(Object obj){
			return new TObject(null, obj);
		}
		
		private TObject(Map container, Object key) {
			super(container==null? null: container.get(key));
			this.container = container;
			this.key = key;
		}

		public TObject $(Object obj) {
			if(this.container != null) {
				this.container.put(this.key, obj);
			}
			this.obj = obj;
			return this;
		}
	}

	public static final class TArray {
		public final static Class StringClass = (new String[] {}).getClass();
		public final static Class ByteClass = (new Byte[] {}).getClass();
		public final static Class DoubleClass = (new Double[] {}).getClass();
		public final static Class FloatClass = (new Float[] {}).getClass();
		public final static Class IntegerClass = (new Integer[] {}).getClass();
		public final static Class LongClass = (new Long[] {}).getClass();
		public final static Class ShortClass = (new Short[] {}).getClass();
		private Object obj;
		
		public static TArray as(Object obj){
			return new TArray(obj);
		}

		protected TArray(Object obj) {
			this.obj = obj;
		}

		public Object get(int index) {
			if (this.obj == null) {
				return null;
			}
			if (this.obj instanceof Map) {
				return ((Map) this.obj).get(index);
			}
			if (this.obj instanceof List) {
				return ((List) this.obj).get(index);
			}
			if (this.obj.getClass().isArray()) {
				int len = Array.getLength(this.obj);
				if (index < len) {
					return Array.get(this.obj, index);
				}
			}
			if (this.obj instanceof Set) {
				return ((Set) this.obj).toArray()[index];
			}
			return null;
		}

		public String[] asString() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsString();
			}
			if(this.obj instanceof String){
				return new String[]{(String)this.obj};
			}
			return (String[]) convert(this.obj, StringClass);
		}

		public Byte[] asByte() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsByte();
			}
			return (Byte[]) convert(this.obj, ByteClass);
		}

		public Double[] asDouble() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsDouble();
			}
			return (Double[]) convert(this.obj, DoubleClass);
		}

		public Float[] asFloat() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsFloat();
			}
			return (Float[]) convert(this.obj, FloatClass);
		}

		public Integer[] asInteger() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsInteger();
			}
			return (Integer[]) convert(this.obj, IntegerClass);
		}

		public Long[] asLong() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsLong();
			}
			return (Long[]) convert(this.obj, LongClass);
		}

		public Short[] asShort() {
			if(this.obj instanceof CArray){
				return ((CArray)this.obj).valuesAsShort();
			}
			return (Short[]) convert(this.obj, ShortClass);
		}
	}
	
	public static final class TNew {
		private Object[] objs;
		
		private TNew(Object... objs){
			this.objs = objs;
		}
		
		public List asList(List list){
			if (this.objs != null && this.objs.length > 0) {
				for (Object obj : this.objs) {
					list.add(obj);
				}
			}
			return list;
		}
		
		public Map asMap(Map map){
			if (this.objs != null && this.objs.length > 0) {
				for (int i = 0; i < this.objs.length; i += 2) {
					map.put(this.objs[i], ((i + 1) < this.objs.length) ? this.objs[i + 1] : null);
				}
			}
			return map;
		}
	}
	
	public static final class TMerge {
		
		public static TMerge from(Map... srcs){
			return new TMerge(srcs);
		}
		
		private Map set = new HashMap();

		protected TMerge(Map... srcs) {
			if (srcs != null && srcs.length > 0) {
				for (Map obj : srcs) {
					this.set.putAll(obj);
				}
			}
		}
		
		public void into(Map dest){
			if (dest != null) {
				dest.clear();
				dest.putAll(set);
			}
		}
	}

}