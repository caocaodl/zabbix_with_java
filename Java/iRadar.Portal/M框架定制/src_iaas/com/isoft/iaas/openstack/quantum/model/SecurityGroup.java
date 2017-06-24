package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("security_group")
public class SecurityGroup implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String id;

	@JsonProperty
	private String name;

	@JsonProperty
	private String description;

	@JsonProperty("tenant_id")
	private String tenantId;

	@JsonProperty("security_group_rules")
	private List<Rule> rules;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getTenantId() {
		return tenantId;
	}

	public List<Rule> getRules() {
		return rules;
	}
	
	
	@JsonRootName("security_group_rule")
	public static final class Rule implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@JsonProperty
		private String id;

		@JsonProperty
		private String direction;
		
		@JsonProperty
		private String ethertype;

		@JsonProperty("port_range_max")
		private String parentGroupId;

		@JsonProperty("port_range_min")
		private Integer fromPort;

		@JsonProperty
		private String protocol;

		@JsonProperty("remote_group_id")
		private String remoteGroupId;
		
		@JsonProperty("remote_ip_prefix")
		private String remoteIpPrefix;
		
		@JsonProperty("security_group_id")
		private String securityGroupId;

		@JsonProperty("tenant_id")
		private String tenantId;

		
		public String getId() {
			return id;
		}

		public String getDirection() {
			return direction;
		}

		public String getEthertype() {
			return ethertype;
		}

		public String getParentGroupId() {
			return parentGroupId;
		}

		public Integer getFromPort() {
			return fromPort;
		}

		public String getProtocol() {
			return protocol;
		}

		public String getRemoteGroupId() {
			return remoteGroupId;
		}

		public String getRemoteIpPrefix() {
			return remoteIpPrefix;
		}

		public String getSecurityGroupId() {
			return securityGroupId;
		}

		public String getTenantId() {
			return tenantId;
		}
	}
}
