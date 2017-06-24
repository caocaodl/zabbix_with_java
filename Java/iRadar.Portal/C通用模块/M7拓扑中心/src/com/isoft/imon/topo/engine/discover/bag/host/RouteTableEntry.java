package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.bag.SimpleBag;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;

/**
 * 路由表数据实体
 * @author ldd
 * 2014-2-22
 */
/**
 * @author ldd
 * 2014-2-22
 */
@AnalysableAnnotation(label = "路由表")
public final class RouteTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091806010L;
	
	/**
	 * IP路由类型
	 * @author ldd
	 * 2014-2-22
	 */
	public static final class ipRouteType {
		public static final int other = 1; // none of the following
		public static final int invalid = 2; // an invalidated route
		public static final int direct = 3; // route to directly, 
											// connected (sub-)network
		public static final int indirect = 4; // route to a non-local,
												// host/network/sub-network
	}
	
	/**
	 * IP路由协议
	 * @author ldd
	 * 2014-2-22
	 */
	public static final class ipRouteProto {
		public static final int	other = 1;	//none of the following
		public static final int	local = 2; //non-protocol information,e.g., manually configured entries
		public static final int netmgmt =3; //set via a network management protocol
		public static final int icmp = 4; //obtained via ICMP e.g., Redirect
		public static final int egp = 5;
		public static final int ggp = 6;
		public static final int hello = 7;
		public static final int rip = 8;
		public static final int isis = 9;
		public static final int esis = 10;
		public static final int ciscoIgrp = 11;
		public static final int bbnSpfIgp = 12;
		public static final int ospf = 13;
		public static final int bgp = 14;			
	}
    //接口索引
	private String ifIndex;
    //目的对象
	private String dest;
    //路径长度
	private int metric;
    //下一个跳跃
	private String nextHop;
    //类型
	public int type;
    //协议
	public int proto;
    //子网掩码
	private String mask;

	/**
	 *获取接口索引
	 * @return
	 * String
	 */
	public String getIfIndex() {
		return this.ifIndex;
	}

	/**
	 *设置接口索引
	 * @param ifIndex
	 * void
	 */
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}

	/**
	 *获取dest
	 * @return
	 * String
	 */
	public String getDest() {
		return this.dest;
	}

	/**
	 *设置dest
	 * @param dest
	 * void
	 */
	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 *获取路径长度
	 * @return
	 * int
	 */
	public int getMetric() {
		return this.metric;
	}

	/**
	 *设置路径长度
	 * @param metric
	 * void
	 */
	public void setMetric(int metric) {
		this.metric = metric;
	}

	/**
	 *获取下一跳
	 * @return
	 * String
	 */
	public String getNextHop() {
		return this.nextHop;
	}

	/**
	 *设置下一跳
	 * @param nextHop
	 * void
	 */
	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	/**
	 *获取类型
	 * @return
	 * int
	 */
	public int getType() {
		return this.type;
	}

	/**
	 *设置类型
	 * @param type
	 * void
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 *获取协议
	 * @return
	 * int
	 */
	public int getProto() {
		return this.proto;
	}

	/**
	 *设置协议
	 * @param proto
	 * void
	 */
	public void setProto(int proto) {
		this.proto = proto;
	}

	/**
	 *获取子网掩码
	 * @return
	 * String
	 */
	public String getMask() {
		return this.mask;
	}

	/**
	 *设置子网掩码
	 * @param mask
	 * void
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/* (non-Javadoc)
	 * 获取dest
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.dest;
	}

	/**
	 *获取协议名称
	 * @return
	 * String
	 */
	public String getProtoName() {
		return DictionaryFactory.getFactory().getEntryValue("ipRouteProto",
				this.proto);
	}

	/**
	 *获取类型名称
	 * @return
	 * String
	 */
	public String getTypeName() {
		return DictionaryFactory.getFactory().getEntryValue("ipRouteType",
				this.type);
	}

	/* (non-Javadoc)
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int, java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
