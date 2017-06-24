package com.isoft.imon.topo.engine.discover;

/**
 * 凭证抽象类
 * 
 * @author ldd 2014-2-18
 */
public abstract class Credence implements Cloneable {
	// 凭证类型
	protected String type;

	/**
	 * 获取凭证类型
	 * 
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * 设置凭证类型
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取嗅探器
	 * 
	 * @return Sniffer
	 */
	public abstract Sniffer getSniffer();

	/**
	 * 编列凭证的信息，并返回字符串
	 * 
	 * @return String
	 */
	public abstract String marshal();

	/**
	 * 通过配置参数，编列凭证的基本信息
	 * 
	 * @param config
	 *            void
	 */
	public abstract void unmarshal(String config);

	/**
	 * 复制
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Cloning not allowed.");
		}
		return this;
	}

	/**
	 * 转化为字符串
	 */
	@Override
	public String toString() {
		return marshal();
	}
}
