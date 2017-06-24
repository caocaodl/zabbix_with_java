package com.isoft.imon.topo.platform.element;

/**
 * 网元状态类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public enum ElementStatus {
	Available(0, "可用", "status0.gif", "00EC00"), 
	Unknown(1, "未知", "status1.gif", "0000E3"), 
	Unmanaged(2, "不管理", "status2.gif",	"E0E0E0"),
	// Maintainance(3, "维护", "status3.gif", "FFDC35"),
	Down(4, "不可用", "status4.gif", "FF0000");

	private int code;
	private String name;
	private String image;
	private String color;

	private ElementStatus(int code, String name, String image, String color) {
		this.code = code;
		this.name = name;
		this.image = image;
		this.color = color;
	}

	/**
	 * 获取状态名称
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置状态名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取图片
	 * 
	 * @return
	 */
	public String getImage() {
		return this.image;
	}

	/**
	 * 设置图片
	 * 
	 * @param image
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 获取颜色
	 * 
	 * @return
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * 设置颜色
	 * 
	 * @param color
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * 获取状态编码
	 * 
	 * @return
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * 设置状态编码
	 * 
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 通过编码获取网元状态
	 * 
	 * @param code
	 * @return
	 */
	public static ElementStatus getElementStatus(int code) {
		for (ElementStatus es : values()) {
			if (es.getCode() == code)
				return es;
		}
		throw new IllegalArgumentException("ElementStatus doesn't exist,Code="
				+ code);
	}

	/**
	 * 通过名称获取网元状态
	 * 
	 * @param name
	 * @return
	 */
	public static ElementStatus getElementStatusByName(String name) {
		for (ElementStatus es : values()) {
			if (es.getName().equals(name))
				return es;
		}
		throw new IllegalArgumentException("ElementStatus doesn't exist,Name="
				+ name);
	}
}
