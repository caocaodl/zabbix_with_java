package com.isoft.imon.topo.engine.discover.bag;

import java.util.List;

import com.isoft.imon.topo.engine.discover.Bag;

/**
 * 简单数据包
 * 
 * @author ldd 2014-2-18
 */
public abstract class SimpleBag implements Bag {
	private static final long serialVersionUID = 201201251938L;
	// 健康值
	protected int health;

	/*
	 * 是否是复合数据，return false (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#isComposite()
	 */
	public final boolean isComposite() {
		return false;
	}

	/*
	 * 数据持久层 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Bag#persist(java.util.List)
	 */
	public final void persist(List<String> sqls) {
		throw new UnsupportedOperationException("SimpleBag不支持这个方法");
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

	/**
	 * 获取实体对象
	 * 
	 * @return String
	 */
	public abstract String getEntity();
}
