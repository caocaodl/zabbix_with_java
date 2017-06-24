package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;

/**
 * Vlan表实体
 * @author Administrator
 * @date 2014年8月6日 
 */
@AnalysableAnnotation(label = "Vlan")
public final class VlanTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091806016L;
	//vlan
	private String vlan;
	//别名
	private String alias;
	//网络地址
	private String netAddress;
	//网络掩码
	private String netMask;
	//类型
	private int type;
	//状态
	private int state;
	private String memberIfs;
	private String[] ifes;

	public String getMemberIfs() {
		return this.memberIfs;
	}

	public void setMemberIfs(String memberIfs) {
		this.memberIfs = memberIfs;
		if (memberIfs != null)
			this.ifes = memberIfs.split(",");
	}

	public String getVlan() {
		return this.vlan;
	}

	public void setVlan(String vlan) {
		this.vlan = vlan;
	}

	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNetAddress() {
		return this.netAddress;
	}

	public void setNetAddress(String netAddress) {
		this.netAddress = netAddress;
	}

	public String getNetMask() {
		return this.netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public String[] getIfes() {
		return this.ifes;
	}

	public String getEntity() {
		return this.vlan;
	}

	public void persist(int elementId, String logTime, List<String> sqls) {
	}
}
