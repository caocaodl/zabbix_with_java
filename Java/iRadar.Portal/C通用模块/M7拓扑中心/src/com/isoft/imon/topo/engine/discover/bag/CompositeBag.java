package com.isoft.imon.topo.engine.discover.bag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 复合数据包
 * 
 * @author ldd 2014-2-18
 * @param <T>
 */
public class CompositeBag<T extends SimpleBag> implements Bag {

	private static final long serialVersionUID = 201201251936L;
	// 实体Map对象
	protected final Map<String, T> entities;
	// 网元设备ID
	protected int elementId;
	// 日志时间
	protected String logTime;
	// 名称
	protected String name;
	// 健康值
	protected int health;

	/**
	 * 构造方法
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CompositeBag() {
		this.entities = new TreeMap();
		this.logTime = DateUtil.getCurrentDateTime();
	}

	/**
	 * 构造方法
	 * 
	 * @param elementId
	 * @param name
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CompositeBag(int elementId, String name) {
		this.elementId = elementId;
		this.name = name;
		this.entities = new TreeMap();
		this.logTime = DateUtil.getCurrentDateTime();
	}

	/**
	 * 添加一个实体对象
	 * 
	 * @param sb
	 *            void
	 */
	public final void add(T sb) {
		if ((sb == null) || (sb.getEntity() == null)) {
			throw new IllegalArgumentException("entity或enttiy.key不能为空.");
		}
		// 如果Map不包含这个对象，则进行添加
		if (!this.entities.containsKey(sb.getEntity())) {
			this.entities.put(sb.getEntity(), sb);
		}
	}

	/**
	 * 是否是一个复合数据包
	 */
	public final boolean isComposite() {
		return true;
	}

	/**
	 * 获取日志的时间
	 * 
	 * @return String
	 */
	public String getLogTime() {
		return this.logTime;
	}

	/**
	 * 设置日志时间
	 * 
	 * @param logTime
	 *            void
	 */
	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	/**
	 * 获取实体对象的列表
	 * 
	 * @return List<T>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final List<T> getEntities() {
		return new ArrayList(this.entities.values());
	}

	/**
	 * 获取实体对象的长度
	 * 
	 * @return int
	 */
	public final int size() {
		return this.entities.size();
	}

	/**
	 * 判断实体对象Map是否为空
	 * 
	 * @return boolean
	 */
	public final boolean isEmpty() {
		return this.entities.isEmpty();
	}

	/**
	 * 根据实体对象Map的key获取对象的信息
	 * 
	 * @param entity
	 * @return T
	 */
	public final T get(String entity) {
		return (T) this.entities.get(entity);
	}

	/**
	 * 判断实体对象是否包含在实体对象Map中
	 * 
	 * @param entity
	 * @return boolean
	 */
	public boolean contains(T entity) {
		return this.entities.containsKey(entity.getEntity());
	}

	/**
	 * 判断当前实体是否包含在实体Map对象中
	 * 
	 * @param entity
	 * @return boolean
	 */
	public boolean contains(String entity) {
		if (entity != null) {
			return this.entities.containsKey(entity);
		}
		return false;
	}

	/**
	 * 获取实体对象
	 * 
	 * @param attribute
	 * @param value
	 * @return T
	 */
	public T getEntry(String attribute, Object value) {
		if ((attribute != null) && (value != null)) {
			for (T entry : this.entities.values()) {
				Object _value = CommonUtil.invokeMethod(entry, "get"
						+ attribute);
				if (value.equals(_value)) {
					return entry;
				}
			}
		}
		return null;
	}

	/**
	 * 获取实体对象集合
	 * 
	 * @param attribute
	 * @param value
	 * @return List<T>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> getEntries(String attribute, Object value) {
		if ((attribute == null) || (value == null))
			return null;
		List entries = new ArrayList();
		for (SimpleBag entry : this.entities.values()) {
			Object _value = CommonUtil.invokeMethod(entry, "get" + attribute);
			if (value.equals(_value)) {
				entries.add(entry);
			}
		}
		if (entries.isEmpty()) {
			return null;
		}
		return entries;
	}

	/**
	 * 获取包名称
	 */
	public final String getBagName() {
		return this.name;
	}

	/**
	 * 设置包名称
	 */
	public void setBagName(String bagName) {
		this.name = bagName;
	}

	/**
	 * 获取网元ID
	 */
	public int getElementId() {
		return this.elementId;
	}

	/**
	 * 设置网元ID
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	/**
	 * 获取正常值
	 */
	public int getHealth() {
		return this.health;
	}

	/**
	 * 设置正常值
	 */
	public void setHealth(int health) {
		this.health = health;
	}
}
