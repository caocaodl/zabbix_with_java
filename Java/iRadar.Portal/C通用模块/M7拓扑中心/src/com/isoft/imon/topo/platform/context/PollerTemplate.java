package com.isoft.imon.topo.platform.context;

import java.util.List;
import java.util.Map;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 轮询器模板类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public final class PollerTemplate {
	public static final String FDB_POLLER = "Fdb";
	public static final String VLAN_POLLER = "Vlan";
	public static final String CONFIG_POLLER = "Config";
	private String clazz;
	private String credence;
	private int intervalValue;
	private char intervalUnit;
	private boolean enabled;
	private int suit;
	private Map<String, List<String>> include;
	private Map<String, List<String>> exclude;

	/**
	 * 获取轮询器类
	 * 
	 * @return
	 */
	public String getClazz() {
		return this.clazz;
	}

	/**
	 * 设置轮询器类
	 * 
	 * @param clazz
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	/**
	 * 获取间隔值
	 * 
	 * @return
	 */
	public int getIntervalValue() {
		return this.intervalValue;
	}

	/**
	 * 设置间隔值
	 * 
	 * @param interval
	 */
	public void setInterval(String interval) {
		String[] intervals = interval.split(":");
		this.intervalValue = Integer.parseInt(intervals[0]);
		this.intervalUnit = intervals[1].charAt(0);
	}

	/**
	 * 获取间隔单位
	 * 
	 * @return
	 */
	public char getIntervalUnit() {
		return this.intervalUnit;
	}

	/**
	 * 判断是否可用
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * 设置是否可用
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getSuit() {
		return this.suit;
	}

	public void setSuit(int suit) {
		this.suit = suit;
	}

	/**
	 * 设置包含的
	 * 
	 * @param include
	 */
	public void setInclude(Map<String, List<String>> include) {
		this.include = include;
	}

	/**
	 * 设置不包含的
	 * 
	 * @param exclude
	 */
	public void setExclude(Map<String, List<String>> exclude) {
		this.exclude = exclude;
	}

	/**
	 * 获取凭证
	 * 
	 * @return
	 */
	public String getCredence() {
		return this.credence;
	}

	/**
	 * 设置凭证
	 * 
	 * @param credence
	 */
	public void setCredence(String credence) {
		this.credence = credence;
	}

	/**
	 * 判断是否为本次的轮询器
	 * 
	 * @param ne
	 * @param credence
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean isMyPoller(NetElement ne, String credence) {
		if (credence != null) {
			if (this.suit == 4)
				return false;
//			if ((ContextFactory.getFactory().isDiscoveryStatus()) && (this.suit == 1))
//				return false;
//			if (((ContextFactory.getFactory().isPollingStatus()) || (ContextFactory.getFactory().isPostDiscoveryStatus())) && (this.suit == 2))
//				return false;
			if ((this.credence != null) && (!this.credence.equals(credence)))
				return false;
		}
		for (String attr : this.include.keySet()) {
			List attrVals = (List) this.include.get(attr);
			String neAttr = null;
			try {
				neAttr = CommonUtil.invokeMethod(ne, "get" + attr).toString();
			} catch (Exception e) {
				System.out.println(ne.getAlias() + "没有方法get" + attr);
			}
			if ((neAttr != null) && (!attrVals.contains(neAttr)))
				return false;
		}
		if (this.exclude != null) {
			for (String attr : this.exclude.keySet()) {
				List attrVals = (List) this.exclude.get(attr);
				String neAttr = null;
				try {
					neAttr = CommonUtil.invokeMethod(ne, "get" + attr).toString();
				} catch (Exception e) {
					System.out.println(ne.getAlias() + "没有方法get" + attr);
				}
				if ((neAttr != null) && (attrVals.contains(neAttr)))
					return false;
			}
		}
		return true;
	}

	/**
	 * 获取HashCode
	 */
	public int hashCode() {
		return this.clazz.hashCode();
	}

	/**
	 * 判断是否相等
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof PollerTemplate)))
			return false;
		PollerTemplate that = (PollerTemplate) obj;

		return this.clazz.equals(that.getClazz());
	}
}
