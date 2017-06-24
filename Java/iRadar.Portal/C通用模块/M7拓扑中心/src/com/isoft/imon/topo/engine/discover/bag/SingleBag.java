package com.isoft.imon.topo.engine.discover.bag;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 单一数据包
 * 
 * @author ldd 2014-2-18
 */
public abstract class SingleBag implements Bag {

	private static final long serialVersionUID = 1L;
	// 设备ID
	protected int elementId;
	// 日志时间
	protected String logTime;
	// 健康值
	protected int health;

	/**
	 * 构造方法
	 */
	public SingleBag() {
		this.logTime = DateUtil.getCurrentDateTime();
	}

	/*
	 * 获取设备ID
	 */
	public int getElementId() {
		return this.elementId;
	}

	/**
	 * 设置设备ID
	 * 
	 * @param elementId
	 *            void
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
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
	 * 获取日志时间
	 * 
	 * @return String
	 */
	public String getLogTime() {
		return this.logTime;
	}

	/*
	 * 是否是复合数据包 return false (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#isComposite()
	 */
	public final boolean isComposite() {
		return false;
	}

	/*
	 * 获取数据包的名称 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#getBagName()
	 */
	public final String getBagName() {
		return getClass().getName();
	}

	/*
	 * 获取健康值 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#getHealth()
	 */
	public int getHealth() {
		return this.health;
	}

	/*
	 * 设置健康值 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#setHealth(int)
	 */
	public void setHealth(int health) {
		this.health = health;
	}
}
