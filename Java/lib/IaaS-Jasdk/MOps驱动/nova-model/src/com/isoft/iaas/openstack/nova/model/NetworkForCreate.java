package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("network")
public class NetworkForCreate implements Serializable {

	private static final long serialVersionUID = 1L;

	private String bridge;

	@JsonProperty("bridge_interface")
	private String bridgeInterface;

	private String cidr;

	private int vlan;

	private String gateway;

	@JsonProperty("num_networks")
	private int numNetworks;

	@JsonProperty("network_size")
	private int networkSize;

	@JsonProperty("vlan_start")
	private int vlanStart;

	private String label;

	@JsonProperty("multi_host")
	private Boolean multiHost;

	@JsonProperty("project_id")
	private String projectId;

	private String dns1;

	private String dns2;

	public String getBridge() {
		return bridge;
	}

	public void setBridge(String bridge) {
		this.bridge = bridge;
	}

	public String getBridgeInterface() {
		return bridgeInterface;
	}

	public void setBridgeInterface(String bridgeInterface) {
		this.bridgeInterface = bridgeInterface;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(String cidr) {
		this.cidr = cidr;
	}

	public int getVlan() {
		return vlan;
	}

	public void setVlan(int vlan) {
		this.vlan = vlan;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public int getNumNetworks() {
		return numNetworks;
	}

	public void setNumNetworks(int numNetworks) {
		this.numNetworks = numNetworks;
	}

	public int getNetworkSize() {
		return networkSize;
	}

	public void setNetworkSize(int networkSize) {
		this.networkSize = networkSize;
	}

	public int getVlanStart() {
		return vlanStart;
	}

	public void setVlanStart(int vlanStart) {
		this.vlanStart = vlanStart;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getMultiHost() {
		return multiHost;
	}

	public void setMultiHost(Boolean multiHost) {
		this.multiHost = multiHost;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getDns1() {
		return dns1;
	}

	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}

	public String getDns2() {
		return dns2;
	}

	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}

}
