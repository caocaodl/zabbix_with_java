package com.isoft.imon.topo.platform.element;


/**
 * 类别类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public class Category {
	private String enName;
	private String cnName;
	private Class<?> clazz;

	public Category() {
	}

	public Category(String enName, String cnName, Class<?> clazz) {
		this.enName = enName;
		this.cnName = cnName;
		this.clazz = clazz;
	}

	public String getEnName() {
		return this.enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return this.cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	/**
	 * 获取类
	 * 
	 * @return
	 */
	public Class<?> getClazz() {
		return this.clazz;
	}

	/**
	 * 设置类
	 * 
	 * @param clazz
	 */
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 * 获取Hashcode
	 */
	public int hashCode() {
		return this.enName.hashCode();
	}

	/**
	 * 重写equals方法
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof Category)))
			return false;
		Category that = (Category) obj;

		return this.enName.equals(that.getEnName());
	}
}
