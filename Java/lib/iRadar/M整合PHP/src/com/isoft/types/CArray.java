package com.isoft.types;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.isoft.iradar.core.utils.EasyList;
import com.isoft.types.Mapper.TArray;

public class CArray<V> extends LinkedHashMap<Object, V> implements Iterable<V>, CMap<Object, V>{
	
	private static final long serialVersionUID = 1L;
	
	static {
		ConvertUtils.register(new Converter() {
			@Override public Object convert(Class type, Object value) {
				return valueOf(value);
			}
		}, CArray.class);
	}

	public CArray() {
		super();
	}

	public CArray(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public CArray(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CArray(int initialCapacity) {
		super(initialCapacity);
	}

	public CArray(Map<Object, V> m) {
		super(m);
	}
	
	public CArray(IEntry<Object, V>... es) {
		if (es != null) {
			for (IEntry<Object, V> e : es) {
				put(e.key, e.value);
			}
		}
	}

	private long ordinal = 0L;
	private Object tkey = null;	

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(legal(key));
	}
	
    /**
     * Appends the specified element to the end of this map.
     *
     * @param e element to be appended to this map
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(V e) {
        this.put(this.ordinal++, e);
        return true;
    }
    
	@Override
	public V get(Object key) {
		return super.get(legal(key));
	}
    
	@Override
	public V remove(Object key) {
		return super.remove(this.legal(key));
	}

	/**
	 * 非线程安全
	 */
	@Override
	public V put(Object key, V value) {
		this.tkey = this.legal(key);
		if (this.tkey instanceof Number && ((Number) this.tkey).longValue() >= this.ordinal) {
			this.ordinal = ((Number) this.tkey).longValue() + 1;
		}
		return super.put(this.tkey, value);
	}
	
	public Object getNested(Object... keys) {
		if (keys == null || keys.length == 0) {
			throw new IllegalArgumentException("至少传入一个参数");
		}
		Object o = this;
		for (Object k : keys) {
			o = ((Map) o).get(k);
			if(o == null) {
				return null;
			}
		}
		return o;
	}
	
	/**
	 * 当参数大于2个时，最后一个是要赋的值，前面的是Map的key
	 * 
	 * @param ksv
	 * @return
	 */
	public Object put(Object... ksv) {
		if (ksv == null || ksv.length == 0) {
			throw new IllegalArgumentException("至少传入一个参数");
		}
		int len = ksv.length;
		if (len == 0) {
			return null;
		} else if (len == 1) {
			return this.add((V) ksv[0]);
		} else if (len == 2) {
			return this.put(ksv[0], (V) ksv[1]);
		} else {
			Object k = ksv[len - 2];
			Object v = ksv[len - 1];
			Map m = this;
			for (int i = 0, imax = len - 2; i < imax; i++) {
				Object mk = ksv[i];
				Map nm = (Map) m.get(mk);
				if (nm == null) {
					nm = new CArray();
					m.put(mk, nm);
				}
				m = nm;
			}
			return m.put(k, v);
		}
	}
	
	public <X> CArray<X> nestedAdd(Object key, X v){
		CArray<X> c = (CArray)this.get(key);
		if(c == null) {
			c = array();
			this.put(key, c);
		}
		c.add(v);
		return c;
	}
	
	public CArray<V> reverse(){
		return reverse(false);
	}
	
	public CArray<V> reverse(boolean preserve_keys){
		Entry<Object, V>[] es = this.entrySet().toArray(new Entry[0]);
		this.clear();
		for (int i = es.length - 1; i >= 0; i--) {
			if (!preserve_keys && es[i].getKey() instanceof Number) {//不保留key，并且key是数字，则通过add重新生成顺序索引
				this.add(es[i].getValue());
			} else {
				this.put(es[i].getKey(), es[i].getValue());
			}
		}
		return this;
	}
	
	public CArray<V> ksort(){
		return ksort(true);
	}
	public CArray<V> ksort(boolean keepKey){
		return sort(new Comparator<Entry<Object,V>>() {
			@Override public int compare(Entry<Object, V> o1, Entry<Object, V> o2) {
				return String.valueOf(o1.getKey()).compareTo(String.valueOf(o2.getKey()));
			}
		}, keepKey);
	}
	
	public CArray<V> sort(){
		return sort(false);
	}
	public CArray<V> sort(boolean keepKey){
		return sort(new Comparator<Entry<Object,V>>() {
			@Override public int compare(Entry<Object, V> o1, Entry<Object, V> o2) {
				return String.valueOf(o1.getValue()).compareTo(String.valueOf(o2.getValue()));
			}
		}, keepKey);
	}
	public CArray<V> sort(Comparator<Entry<Object,V>> cmp){
		return sort(cmp, false);
	}
	public CArray<V> sort(Comparator<Entry<Object,V>> cmp, boolean keepKey){
		Entry<Object, V>[] es = this.entrySet().toArray(new Entry[0]);
		Arrays.sort(es, cmp);
		this.clear();
		
		if(keepKey) {
			for(Entry<Object,V> e: es) {
				this.put(e.getKey(), e.getValue());
			}
		}else {
			for(Entry<Object,V> e: es) {
				this.add(e.getValue());
			}
		}
		return this;
	}
	
	public CArray<V> unique(){
		this.sort();
		Entry<Object, V>[] es = this.entrySet().toArray(new Entry[0]);
		this.clear();
		for(Entry<Object,V> e: es) {
			if(!this.containsValue(e.getValue())){
				this.put(e.getKey(), e.getValue());
			}
		}
		return this;
	}
	
	@Override
	public void clear() {
		super.clear();
		this.ordinal = 0L;
		this.tkey = null;
	}
	
	public CArray<V> unset(){
		this.clear();
		return this;
	}
	public CArray<V> unset(Object k){
		this.remove(k);
		return this;
	} 

	@Override
	public Iterator<V> iterator() {
		return super.values().iterator();
	}
	
	public String[] keys() {
		Object[] elementData = this.keySet().toArray();
		String[] ts = new String[elementData.length];
		for (int i = 0, ilen = ts.length; i < ilen; i++) {
			ts[i] = String.valueOf(elementData[i]);
		}
		return ts;
	}
	
	public Long[] keysAsLong() {
		return TArray.as(this.keySet().toArray()).asLong();
	}
	
	public Byte[] valuesAsByte() {
		return (TArray.as(this.toArray())).asByte();
	}
	
	public Double[] valuesAsDouble() {
		return (TArray.as(this.toArray())).asDouble();
	}
	
	public Float[] valuesAsFloat() {
		return (TArray.as(this.toArray())).asFloat();
	}
	
	public Integer[] valuesAsInteger() {
		return (TArray.as(this.toArray())).asInteger();
	}
	
	public Long[] valuesAsLong() {
		return (TArray.as(this.toArray())).asLong();
	}
	
	public Short[] valuesAsShort() {
		return (TArray.as(this.toArray())).asShort();
	}
	
	public String[] valuesAsString() {
		return (TArray.as(this.toArray())).asString();
	}
	
	private final Object legal(Object key) {
		if (key == null || "".equals(key)) {
			return "";
		}
		if (key instanceof Number) {
			return ((Number) key).longValue();
		} else if (key instanceof String) {
			String str = (String) key;
			final char[] chars = str.toCharArray();
			int sz = chars.length;
			final int start = (chars[0] == '-') ? 1 : 0;
			if (sz > start + 1 && chars[start] == '0') { // leading 0
				return key;
			}
			int i = start;
			while (i < sz) {
				if (chars[i] < '0' || chars[i] > '9') {
					return key;
				}
				i++;
			}
			if (i > start && i == sz) {
				Object v = str;
				try {
					v = Long.valueOf(str);
				}catch (Exception e) {}
				return v;
			} else {
				return key;
			}			
		} else {
			return key;
		}
	}
	
	/**
	 * array_unshift() 
	 * 将传入的单元插入到 array 数组的开头。
	 * 注意单元是作为整体被插入的，因此传入单元将保持同样的顺序。
	 * 所有的数值键名将修改为从零开始重新计数，所有的文字键名保持不变。
	 * 
	 * @param ts
	 * @return
	 */
	public CArray<V> unshift(V...ts) {
		Entry<Object, V>[] es = this.entrySet().toArray(new Entry[0]);
		this.clear();
		
		for(V t: ts) {
			this.add(t);
		}
		
		this.reput(es, false);
		return this;
	}
	
	/**
	 * array_shift() 
	 * 将 array 的第一个单元移出并作为结果返回，将 array 的长度减一并将所有其它单元向前移动一位。
	 * 所有的数字键名将改为从零开始计数，文字键名将不变
	 * 
	 * @return
	 */
	public V shift() {
		V t = this.remove(this.keySet().iterator().next());
		Entry<Object, V>[] es = this.entrySet().toArray(new Entry[0]);
		
		this.clear();
		this.reput(es, false);
		return t;
	}
	
	/**
	 * 自动根据key的类型
	 * 
	 * @param key
	 * @param value
	 */
	protected void reput(Entry<Object, V>[] es, boolean keepKey) {
		if(keepKey) {
			for(Entry<Object, V> e: es) {
				this.put(e.getKey(), e.getValue());
			}
		}else {
			for(Entry<Object, V> e: es) {
				Object key = e.getKey();
				V value = e.getValue();
				if(key instanceof String) {
					this.put(key, value);
				}else {
					this.add(value);
				}
			}
		}
	}
	
	
	/**
	 * 多态，返回本身
	 * 
	 * @param t
	 * @return
	 */
	public static <T> CArray<T> valueOf(CArray<T> t){
		return t;
	}
	
	/**
	 * 数组，以数组的形式添加（自动计算索引KEY）
	 * 
	 * @param m
	 * @return
	 */
	public static <T> CArray<T> valueOf(T[] m){
		return valueOf(Arrays.asList(m));
	}
	
	/**
	 * 集合，以数组的形式添加（自动计算索引KEY）
	 * 
	 * @param ls
	 * @return
	 */
	public static <T> CArray<T> valueOf(List<T> ls){
		CArray<T> p = new CArray<T>();
		for(T t: ls)  p.add(t);
		return p;
	}
	
	/**
	 * 集合，以数组的形式添加（自动计算索引KEY）
	 * 
	 * @param ls
	 * @return
	 */
	public static <T> CArray<T> valueOf(Collection<T> ls){
		CArray<T> p = new CArray<T>();
		for(T t: ls)  p.add(t);
		return p;
	}
	/**
	 * Map，以Map的形式添加（使用Map的KEY）
	 * 
	 * @param m
	 * @return
	 */
	public static <T> CArray<T> valueOf(Map<?, T> m){
		if (m instanceof CArray) {
			return (CArray) m;
		}
		CArray<T> p = new CArray<T>();
		for (Entry<?, T> entry : m.entrySet()) {
			p.put(entry.getKey(), entry.getValue());
		}
		return p;
	}
	
	/**
	 * 其他类型的对象直接生成一个新数组
	 * 
	 * @param t
	 * @return
	 */
	public static CArray<?> valueOf(Object t){
		if(t == null) return null;
		
		if(t instanceof CArray) {
			return valueOf((CArray)t);
		}else if(t instanceof Collection) {
			return valueOf((Collection)t);
		}else if(t instanceof Map) {
			return valueOf((Map)t);
		}else if(t.getClass().isArray()) {
			return valueOf(EasyList.asList(t));
		}
//		throw new RuntimeException("Unkown Class: " + t.getClass());
		return array(t);
	}
	
	/**
	 * 实例化一个新{@com.isoft.types.CArray}对象
	 * 
	 * @return
	 */
	public static CArray array() {
		return new CArray();
	}

	/**
	 * 相同类型的对象，以数组的形式添加（自动计算索引KEY）
	 * 
	 * @param entries
	 * @return
	 */
	public static <T> CArray<T> array(T... entries) {
		return valueOf(Arrays.asList(entries));
	}
	
	/**
	 * 以数组的形式添加Map的形式添加（使用Map的key）
	 * 
	 * @param args
	 * @return
	 */
	public static <T> CArray<T> map(Object... args) {
		CArray<T> map = new CArray<T>();
		for (int i = 0, ilen = args.length; i < ilen; i += 2) {
			Object key = args[i];
			Object value = (i + 1) < ilen ? args[i + 1] : null;
			map.put(key, (T) value);
		}
		return map;
	}
	
	public CArray<V> push(V o){
		this.add(o);
		return this;
	}
	
	/**
	 * 获取对象中是字符串类型的Key集合
	 * 
	 * @return
	 */
	public Set<String> strKeySet(){
		Set<String> r = new HashSet<String>();
		for(Object key: keySet()) {
			if(key instanceof String) {
				r.add((String)key);
			}
		}
		return r;
	}
	
	/**
	 * 强制转化成key是字符型的Map
	 * 
	 * @return 
	 */
	public Map<String, V> toStrKeyMap(){
		Map<String, V> r = new IMap<String, V>();
		for(Object k: this.keySet()) {
			r.put(String.valueOf(k), this.get(k));
		}
		return r;
	}

	/**
	 * 
	 * @return 自身的只读Map
	 */
	public Map toMap() {
		return Collections.unmodifiableMap(this);
	}
	
	/**
	 * 获取对象值的List形式
	 * 
	 * @return 值对应的List集合
	 */
	public List<V> toList() {
		return new IList<V>(this.values());
	}

	/**
	 * @return 值对应的数组
	 */
	public V[] toArray() {
		return (V[]) this.values().toArray();
	}
	
	/**
	 * 不推荐使用，可能会导致ClassCastException
	 * @return
	 */
	@Deprecated
	public <K> K[] toArray(K[] a) {
		V[] elementData = this.toArray();
		int size = this.size();
		if (a.length < size) {
			// Make a new array of a's runtime type, but my contents:
            return (K[]) Arrays.copyOf(elementData, size, a.getClass());
		}
		System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size) {
        	a[size] = null;
        }
        return a;
	}
	
	/**
	 * 不推荐使用，可能会导致ClassCastException
	 * @return
	 */
	public <S> S[] toArray(Class<S> a) {
		Object[] elementData = this.toArray();
		S[] ts = (S[])Array.newInstance(a, elementData.length);
		for(int i=0,ilen=ts.length; i<ilen; i++) {
			ts[i] = (S)ConvertUtils.convert(elementData[i], a);
		}
		return ts;
	}
	
	@Deprecated
	public Integer[] toIntArray() {
		return toArray(Integer.class);
	}
	
	@Deprecated
	public String[] toStrArray() {
		return toArray(String.class);
	}
	
	public <S> void copy(CArray<S> v) {
		this.clear();
		for(Entry<Object, S> e: v.entrySet()) {
			this.put(e.getKey(), e.getValue());
		}
	}
	
	public CArray<V> entryValueFromMap2CArray(){
		CArray<V> swap = new CArray<V>();
		for(Entry<Object, V> e: this.entrySet()) {
			Object v = e.getValue();
			if(v instanceof HashMap){
				v = CArray.valueOf((Map)v);
			}
			swap.put(e.getKey(), v);
		}
		copy(swap);
		return this;
	}
	
	/**
	 * 这个方法与getNested的不同在于，此方法仅适应于把CArray当List用的情况
	 * keys里的每一个值对就一级values，也就是说 使用此方法需要保证CArray结构是 list \ map \ list \ map ....这样循环的情况
	 * 
	 * @param keys 每一级map要取的KEY
	 * @return 一个list，包含所有取到的值
	 */
	public List listNested(Object... keys) {
		List ls = EasyList.build();
		for(Object o: this.values()) {
			if(!(o instanceof Map)) continue;
			
			CArray m = CArray.valueOf(o);
			if(keys.length == 1) {
				for(Object mo: m.values()) {
					if(!(mo instanceof Map)) continue;
					
					Object key = keys[0];
					Object value = ((Map)mo).get(key);
					ls.add(value);
				}
			}else {
				Object[] nextKeys = new Object[keys.length - 1];
				System.arraycopy(keys, 1, nextKeys, 0, nextKeys.length);
				ls.addAll(m.listNested(nextKeys));
			}
		}
		return ls;
	}
	
}