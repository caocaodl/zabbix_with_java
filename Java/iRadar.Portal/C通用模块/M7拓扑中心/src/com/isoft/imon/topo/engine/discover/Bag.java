package com.isoft.imon.topo.engine.discover;

import java.io.Serializable;
import java.util.List;

/**
 * 数据包
 * 
 * @author ldd 2014-2-18
 */
public interface Bag extends Serializable {

	/**
	 * 是否是复合数据包
	 * 
	 * @return boolean
	 */
	boolean isComposite();

	/**
	 * 获取数据包名称
	 * 
	 * @return String
	 */
	String getBagName();

	/**
	 * 设置正常值
	 * 
	 * @param paramInt
	 *            void
	 */
	void setHealth(int paramInt);

	/**
	 * 获取正常值
	 * 
	 * @return int
	 */
	int getHealth();
}
