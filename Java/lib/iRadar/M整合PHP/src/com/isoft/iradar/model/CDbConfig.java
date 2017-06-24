package com.isoft.iradar.model;

import com.isoft.types.CArray;

@SuppressWarnings("serial")
public class CDbConfig extends CArray {
	private Integer alert_history;
	private Integer event_history;
	private Integer refresh_unsupported;
	private String work_period;
	private Long alert_usrgrpid;
	private Integer event_ack_enable;
	private Integer event_expire;
	private Integer event_show_max;
	private String default_theme;
	private Integer authentication_type;
	private String ldap_host;
	private Integer ldap_port;
	private String ldap_base_dn;
	private String ldap_bind_dn;
	private String ldap_bind_password;
	private String ldap_search_attribute;
	private Integer dropdown_first_entry;
	private Integer dropdown_first_remember;
	private Long discovery_groupid;
	private Integer max_in_table;
	private Integer search_limit;
	private String severity_color_0;
	private String severity_color_1;
	private String severity_color_2;
	private String severity_color_3;
	private String severity_color_4;
	private String severity_color_5;
	private String severity_name_0;
	private String severity_name_1;
	private String severity_name_2;
	private String severity_name_3;
	private String severity_name_4;
	private String severity_name_5;
	private Integer ok_period;
	private Integer blink_period;
	private String problem_unack_color;
	private String problem_ack_color;
	private String ok_unack_color;
	private String ok_ack_color;
	private Integer problem_unack_style;
	private Integer problem_ack_style;
	private Integer ok_unack_style;
	private Integer ok_ack_style;
	private Integer snmptrap_logging;
	private Integer server_check_interval;

	public Integer getAlert_history() {
		return alert_history;
	}

	public void setAlert_history(Integer alertHistory) {
		alert_history = alertHistory;
	}

	public Integer getEvent_history() {
		return event_history;
	}

	public void setEvent_history(Integer eventHistory) {
		event_history = eventHistory;
	}

	public Integer getRefresh_unsupported() {
		return refresh_unsupported;
	}

	public void setRefresh_unsupported(Integer refreshUnsupported) {
		refresh_unsupported = refreshUnsupported;
	}

	public String getWork_period() {
		return work_period;
	}

	public void setWork_period(String workPeriod) {
		work_period = workPeriod;
	}

	public Long getAlert_usrgrpid() {
		return alert_usrgrpid;
	}

	public void setAlert_usrgrpid(Long alertUsrgrpid) {
		alert_usrgrpid = alertUsrgrpid;
	}

	public Integer getEvent_ack_enable() {
		return event_ack_enable;
	}

	public void setEvent_ack_enable(Integer eventAckEnable) {
		event_ack_enable = eventAckEnable;
	}

	public Integer getEvent_expire() {
		return event_expire;
	}

	public void setEvent_expire(Integer eventExpire) {
		event_expire = eventExpire;
	}

	public Integer getEvent_show_max() {
		return event_show_max;
	}

	public void setEvent_show_max(Integer eventShowMax) {
		event_show_max = eventShowMax;
	}

	public String getDefault_theme() {
		return default_theme;
	}

	public void setDefault_theme(String defaultTheme) {
		default_theme = defaultTheme;
	}

	public Integer getAuthentication_type() {
		return authentication_type;
	}

	public void setAuthentication_type(Integer authenticationType) {
		authentication_type = authenticationType;
	}

	public String getLdap_host() {
		return ldap_host;
	}

	public void setLdap_host(String ldapHost) {
		ldap_host = ldapHost;
	}

	public Integer getLdap_port() {
		return ldap_port;
	}

	public void setLdap_port(Integer ldapPort) {
		ldap_port = ldapPort;
	}

	public String getLdap_base_dn() {
		return ldap_base_dn;
	}

	public void setLdap_base_dn(String ldapBaseDn) {
		ldap_base_dn = ldapBaseDn;
	}

	public String getLdap_bind_dn() {
		return ldap_bind_dn;
	}

	public void setLdap_bind_dn(String ldapBindDn) {
		ldap_bind_dn = ldapBindDn;
	}

	public String getLdap_bind_password() {
		return ldap_bind_password;
	}

	public void setLdap_bind_password(String ldapBindPassword) {
		ldap_bind_password = ldapBindPassword;
	}

	public String getLdap_search_attribute() {
		return ldap_search_attribute;
	}

	public void setLdap_search_attribute(String ldapSearchAttribute) {
		ldap_search_attribute = ldapSearchAttribute;
	}

	public Integer getDropdown_first_entry() {
		return dropdown_first_entry;
	}

	public void setDropdown_first_entry(Integer dropdownFirstEntry) {
		dropdown_first_entry = dropdownFirstEntry;
	}

	public Integer getDropdown_first_remember() {
		return dropdown_first_remember;
	}

	public void setDropdown_first_remember(Integer dropdownFirstRemember) {
		dropdown_first_remember = dropdownFirstRemember;
	}

	public Long getDiscovery_groupid() {
		return discovery_groupid;
	}

	public void setDiscovery_groupid(Long discoveryGroupid) {
		discovery_groupid = discoveryGroupid;
	}

	public Integer getMax_in_table() {
		return max_in_table;
	}

	public void setMax_in_table(Integer maxInTable) {
		max_in_table = maxInTable;
	}

	public Integer getSearch_limit() {
		return search_limit;
	}

	public void setSearch_limit(Integer searchLimit) {
		search_limit = searchLimit;
	}

	public String getSeverity_color_0() {
		return severity_color_0;
	}

	public void setSeverity_color_0(String severityColor_0) {
		severity_color_0 = severityColor_0;
	}

	public String getSeverity_color_1() {
		return severity_color_1;
	}

	public void setSeverity_color_1(String severityColor_1) {
		severity_color_1 = severityColor_1;
	}

	public String getSeverity_color_2() {
		return severity_color_2;
	}

	public void setSeverity_color_2(String severityColor_2) {
		severity_color_2 = severityColor_2;
	}

	public String getSeverity_color_3() {
		return severity_color_3;
	}

	public void setSeverity_color_3(String severityColor_3) {
		severity_color_3 = severityColor_3;
	}

	public String getSeverity_color_4() {
		return severity_color_4;
	}

	public void setSeverity_color_4(String severityColor_4) {
		severity_color_4 = severityColor_4;
	}

	public String getSeverity_color_5() {
		return severity_color_5;
	}

	public void setSeverity_color_5(String severityColor_5) {
		severity_color_5 = severityColor_5;
	}

	public String getSeverity_name_0() {
		return severity_name_0;
	}

	public void setSeverity_name_0(String severityName_0) {
		severity_name_0 = severityName_0;
	}

	public String getSeverity_name_1() {
		return severity_name_1;
	}

	public void setSeverity_name_1(String severityName_1) {
		severity_name_1 = severityName_1;
	}

	public String getSeverity_name_2() {
		return severity_name_2;
	}

	public void setSeverity_name_2(String severityName_2) {
		severity_name_2 = severityName_2;
	}

	public String getSeverity_name_3() {
		return severity_name_3;
	}

	public void setSeverity_name_3(String severityName_3) {
		severity_name_3 = severityName_3;
	}

	public String getSeverity_name_4() {
		return severity_name_4;
	}

	public void setSeverity_name_4(String severityName_4) {
		severity_name_4 = severityName_4;
	}

	public String getSeverity_name_5() {
		return severity_name_5;
	}

	public void setSeverity_name_5(String severityName_5) {
		severity_name_5 = severityName_5;
	}

	public Integer getOk_period() {
		return ok_period;
	}

	public void setOk_period(Integer okPeriod) {
		ok_period = okPeriod;
	}

	public Integer getBlink_period() {
		return blink_period;
	}

	public void setBlink_period(Integer blinkPeriod) {
		blink_period = blinkPeriod;
	}

	public String getProblem_unack_color() {
		return problem_unack_color;
	}

	public void setProblem_unack_color(String problemUnackColor) {
		problem_unack_color = problemUnackColor;
	}

	public String getProblem_ack_color() {
		return problem_ack_color;
	}

	public void setProblem_ack_color(String problemAckColor) {
		problem_ack_color = problemAckColor;
	}

	public String getOk_unack_color() {
		return ok_unack_color;
	}

	public void setOk_unack_color(String okUnackColor) {
		ok_unack_color = okUnackColor;
	}

	public String getOk_ack_color() {
		return ok_ack_color;
	}

	public void setOk_ack_color(String okAckColor) {
		ok_ack_color = okAckColor;
	}

	public Integer getProblem_unack_style() {
		return problem_unack_style;
	}

	public void setProblem_unack_style(Integer problemUnackStyle) {
		problem_unack_style = problemUnackStyle;
	}

	public Integer getProblem_ack_style() {
		return problem_ack_style;
	}

	public void setProblem_ack_style(Integer problemAckStyle) {
		problem_ack_style = problemAckStyle;
	}

	public Integer getOk_unack_style() {
		return ok_unack_style;
	}

	public void setOk_unack_style(Integer okUnackStyle) {
		ok_unack_style = okUnackStyle;
	}

	public Integer getOk_ack_style() {
		return ok_ack_style;
	}

	public void setOk_ack_style(Integer okAckStyle) {
		ok_ack_style = okAckStyle;
	}

	public Integer getSnmptrap_logging() {
		return snmptrap_logging;
	}

	public void setSnmptrap_logging(Integer snmptrapLogging) {
		snmptrap_logging = snmptrapLogging;
	}

	public Integer getServer_check_interval() {
		return server_check_interval;
	}

	public void setServer_check_interval(Integer serverCheckInterval) {
		server_check_interval = serverCheckInterval;
	}

}
