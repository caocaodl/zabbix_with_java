package com.isoft.imon.topo.engine.discover.bag.host.cisco;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**
 * 云主机共同体
 * 
 * @author Administrator
 * 
 * @date 2014年8月6日
 */
public final class VlanCommunityEntry extends SimpleBag  {

	private static final long serialVersionUID = 1L;
	private String community;

	/**
	 * 获取云主机共同体
	 * 
	 * @return
	 */
	public String getCommunity() {
		return this.community;
	}

	/**
	 * 设置云主机共同体
	 * 
	 * @param community
	 */
	public void setCommunity(String community) {
		this.community = community;
	}

	/*
	 * (non-Javadoc) 获取实体
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.community;
	}

	public void persist(int elementId, String logTime, List<String> sqls) {
	}

	/*
	 * (non-Javadoc) 判断是否等于
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof VlanCommunityEntry))) {
			return false;
		}
		VlanCommunityEntry that = (VlanCommunityEntry) obj;
		return that.getCommunity().equals(this.community);
	}

	/*
	 * (non-Javadoc) 哈希编码
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return 31 + this.community.hashCode();
	}
}
