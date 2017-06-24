package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("quota")
public class Quota implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private Integer subnet;

	@JsonProperty
	private Integer router;

	@JsonProperty
	private Integer port;

	@JsonProperty
	private Integer network;

	@JsonProperty("floatingip")
	private Integer floatingIp;

	@JsonProperty("ikepolicy")
	private Integer ikePolicy;

	@JsonProperty("ipsec_site_connection")
	private Integer ipsecSiteConnection;

	@JsonProperty("ipsecpolicy")
	private Integer ipsecPolicy;

	@JsonProperty("security_group_rule")
	private Integer securityGroupRule;

	@JsonProperty("vpnservice")
	private Integer vpnService;

	@JsonProperty("security_group")
	private Integer securityGroup;

	@JsonProperty("tenant_id")
	private String tenantId;

	public Integer getSubnet() {
		return subnet;
	}

	public Integer getRouter() {
		return router;
	}

	public Integer getPort() {
		return port;
	}

	public Integer getNetwork() {
		return network;
	}

	public Integer getFloatingIp() {
		return floatingIp;
	}

	public Integer getIkePolicy() {
		return ikePolicy;
	}

	public Integer getIpsecSiteConnection() {
		return ipsecSiteConnection;
	}

	public Integer getIpsecPolicy() {
		return ipsecPolicy;
	}

	public Integer getSecurityGroupRule() {
		return securityGroupRule;
	}

	public Integer getVpnService() {
		return vpnService;
	}

	public Integer getSecurityGroup() {
		return securityGroup;
	}

	public String getTenantId() {
		return tenantId;
	}

}
