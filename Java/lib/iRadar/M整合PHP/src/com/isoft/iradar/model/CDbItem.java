package com.isoft.iradar.model;

import com.isoft.types.CArray;

@SuppressWarnings("serial")
public class CDbItem extends CArray{

	private Long itemid;
	private Integer type;
	private String snmp_community;
	private String snmp_oid;
	private Long hostid;
	private String name;
	private String key_;
	private Integer delay;
	private Integer history;
	private Integer trends;
	private String lastvalue;
	private Integer lastclock;
	private String prevvalue;
	private Integer status;
	private Integer value_type;
	private String trapper_hosts;
	private String units;
	private Integer multiplier;
	private Integer delta;
	private String prevorgvalue;
	private String snmpv3_securityname;
	private Integer snmpv3_securitylevel;
	private String snmpv3_authpassphrase;
	private String snmpv3_privpassphrase;
	private String formula;
	private String error;
	private Long lastlogsize;
	private String logtimefmt;
	private Long templateid;
	private Long valuemapid;
	private String delay_flex;
	private String params;
	private String ipmi_sensor;
	private Integer data_type;
	private Integer authtype;
	private String username;
	private String password;
	private String publickey;
	private String privatekey;
	private Integer mtime;
	private Integer lastns;
	private Integer flags;
	private String filter;
	private Long interfaceid;
	private String port;
	private String description;
	private Integer inventory_link;
	private String lifetime;

	public Long getItemid() {
		return itemid;
	}

	public void setItemid(Long itemid) {
		this.itemid = itemid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSnmp_community() {
		return snmp_community;
	}

	public void setSnmp_community(String snmpCommunity) {
		snmp_community = snmpCommunity;
	}

	public String getSnmp_oid() {
		return snmp_oid;
	}

	public void setSnmp_oid(String snmpOid) {
		snmp_oid = snmpOid;
	}

	public Long getHostid() {
		return hostid;
	}

	public void setHostid(Long hostid) {
		this.hostid = hostid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey_() {
		return key_;
	}

	public void setKey_(String key) {
		key_ = key;
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	public Integer getHistory() {
		return history;
	}

	public void setHistory(Integer history) {
		this.history = history;
	}

	public Integer getTrends() {
		return trends;
	}

	public void setTrends(Integer trends) {
		this.trends = trends;
	}

	public String getLastvalue() {
		return lastvalue;
	}

	public void setLastvalue(String lastvalue) {
		this.lastvalue = lastvalue;
	}

	public Integer getLastclock() {
		return lastclock;
	}

	public void setLastclock(Integer lastclock) {
		this.lastclock = lastclock;
	}

	public String getPrevvalue() {
		return prevvalue;
	}

	public void setPrevvalue(String prevvalue) {
		this.prevvalue = prevvalue;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getValue_type() {
		return value_type;
	}

	public void setValue_type(Integer valueType) {
		value_type = valueType;
	}

	public String getTrapper_hosts() {
		return trapper_hosts;
	}

	public void setTrapper_hosts(String trapperHosts) {
		trapper_hosts = trapperHosts;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public Integer getDelta() {
		return delta;
	}

	public void setDelta(Integer delta) {
		this.delta = delta;
	}

	public String getPrevorgvalue() {
		return prevorgvalue;
	}

	public void setPrevorgvalue(String prevorgvalue) {
		this.prevorgvalue = prevorgvalue;
	}

	public String getSnmpv3_securityname() {
		return snmpv3_securityname;
	}

	public void setSnmpv3_securityname(String snmpv3Securityname) {
		snmpv3_securityname = snmpv3Securityname;
	}

	public Integer getSnmpv3_securitylevel() {
		return snmpv3_securitylevel;
	}

	public void setSnmpv3_securitylevel(Integer snmpv3Securitylevel) {
		snmpv3_securitylevel = snmpv3Securitylevel;
	}

	public String getSnmpv3_authpassphrase() {
		return snmpv3_authpassphrase;
	}

	public void setSnmpv3_authpassphrase(String snmpv3Authpassphrase) {
		snmpv3_authpassphrase = snmpv3Authpassphrase;
	}

	public String getSnmpv3_privpassphrase() {
		return snmpv3_privpassphrase;
	}

	public void setSnmpv3_privpassphrase(String snmpv3Privpassphrase) {
		snmpv3_privpassphrase = snmpv3Privpassphrase;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Long getLastlogsize() {
		return lastlogsize;
	}

	public void setLastlogsize(Long lastlogsize) {
		this.lastlogsize = lastlogsize;
	}

	public String getLogtimefmt() {
		return logtimefmt;
	}

	public void setLogtimefmt(String logtimefmt) {
		this.logtimefmt = logtimefmt;
	}

	public Long getTemplateid() {
		return templateid;
	}

	public void setTemplateid(Long templateid) {
		this.templateid = templateid;
	}

	public Long getValuemapid() {
		return valuemapid;
	}

	public void setValuemapid(Long valuemapid) {
		this.valuemapid = valuemapid;
	}

	public String getDelay_flex() {
		return delay_flex;
	}

	public void setDelay_flex(String delayFlex) {
		delay_flex = delayFlex;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getIpmi_sensor() {
		return ipmi_sensor;
	}

	public void setIpmi_sensor(String ipmiSensor) {
		ipmi_sensor = ipmiSensor;
	}

	public Integer getData_type() {
		return data_type;
	}

	public void setData_type(Integer dataType) {
		data_type = dataType;
	}

	public Integer getAuthtype() {
		return authtype;
	}

	public void setAuthtype(Integer authtype) {
		this.authtype = authtype;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPublickey() {
		return publickey;
	}

	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}

	public String getPrivatekey() {
		return privatekey;
	}

	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}

	public Integer getMtime() {
		return mtime;
	}

	public void setMtime(Integer mtime) {
		this.mtime = mtime;
	}

	public Integer getLastns() {
		return lastns;
	}

	public void setLastns(Integer lastns) {
		this.lastns = lastns;
	}

	public Integer getFlags() {
		return flags;
	}

	public void setFlags(Integer flags) {
		this.flags = flags;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Long getInterfaceid() {
		return interfaceid;
	}

	public void setInterfaceid(Long interfaceid) {
		this.interfaceid = interfaceid;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInventory_link() {
		return inventory_link;
	}

	public void setInventory_link(Integer inventoryLink) {
		inventory_link = inventoryLink;
	}

	public String getLifetime() {
		return lifetime;
	}

	public void setLifetime(String lifetime) {
		this.lifetime = lifetime;
	}

}
