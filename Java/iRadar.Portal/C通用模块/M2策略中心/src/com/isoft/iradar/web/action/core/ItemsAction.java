package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_intersect;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ITEM;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_BOOLEAN;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_DECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_HEXADECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_OCTAL;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NORMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_EXTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_INTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SIMPLE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NOT_ZERO;
import static com.isoft.iradar.inc.Defines.NO_TRIM;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.P_UNSET_EMPTY;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_ITEM_DELAY_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL_STR;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FormsUtil.getItemFilterForm;
import static com.isoft.iradar.inc.FormsUtil.getItemFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_requests;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ItemsUtil.activate_item;
import static com.isoft.iradar.inc.ItemsUtil.copyItemsToHosts;
import static com.isoft.iradar.inc.ItemsUtil.delete_history_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.disable_item;
import static com.isoft.iradar.inc.ItemsUtil.fillItemsWithChildTemplates;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldLabelByType;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldNameByType;
import static com.isoft.iradar.inc.ItemsUtil.get_applications_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid_limited;
import static com.isoft.iradar.inc.ItemsUtil.itemTypeInterface;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.ItemsUtil.orderItemsByStatus;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.getParentHostsByTriggers;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public abstract class ItemsAction extends RadarBaseAction {
	
	private CArray<Map> hosts;
	private boolean isTemplate = false;
	private String profileFlag = "";
	
	protected abstract String getAction();
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of items"));
		page("file", getAction());
		page("scripts", new String[] { "class.cviewswitcher.js", "multiselect.js" });
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/devicecenter/items.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		String paramsFieldName = getParamFieldNameByType(get_request("type", 0));
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",									array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",									array(T_RDA_INT, O_OPT, P_SYS,	DB_ID+NOT_ZERO, "isset({form})&&!isset({itemid})"),
			"interfaceid",							array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null, _("Interface")),
			"copy_type",								array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	"isset({copy})"),
			"copy_mode",							array(T_RDA_INT, O_OPT, P_SYS,	IN("0"),	null),
			"itemid",									array(T_RDA_INT, O_NO,	P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name",									array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Name")),
			"description",							array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"key",										array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Key")),
			"delay",										array(T_RDA_INT, O_OPT, null,	BETWEEN(0, SEC_PER_DAY), "isset({save})&&isset({type})&&{type}!="+ITEM_TYPE_TRAPPER+"&&{type}!="+ITEM_TYPE_SNMPTRAP, _("Update interval (in sec)")),
			"new_delay_flex",						array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({add_delay_flex})&&isset({type})&&{type}!=2", _("New flexible interval")),
			"delay_flex",								array(T_RDA_STR, O_OPT, null,	"",			null),
			"history",									array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("History storage period") ),
			"status",									array(T_RDA_INT, O_OPT, null,	IN(array(ITEM_STATUS_DISABLED, ITEM_STATUS_ACTIVE)), null),
			"type",										array(T_RDA_INT, O_OPT, null, IN(array(-1, ITEM_TYPE_IRADAR, ITEM_TYPE_SNMPV1, ITEM_TYPE_TRAPPER, ITEM_TYPE_SIMPLE, ITEM_TYPE_SNMPV2C,ITEM_TYPE_INTERNAL, ITEM_TYPE_SNMPV3, ITEM_TYPE_IRADAR_ACTIVE, ITEM_TYPE_AGGREGATE, ITEM_TYPE_EXTERNAL,ITEM_TYPE_DB_MONITOR, ITEM_TYPE_IPMI, ITEM_TYPE_SSH, ITEM_TYPE_TELNET, ITEM_TYPE_JMX, ITEM_TYPE_CALCULATED, ITEM_TYPE_SNMPTRAP)), "isset({save})"),
			"trends",									array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})&&isset({value_type})&&"+IN(ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64, "value_type"), _("Trend storage period")),
			"value_type",							array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4"), "isset({save})"),
			"data_type",								array(T_RDA_INT, O_OPT, null, IN(ITEM_DATA_TYPE_DECIMAL+","+ITEM_DATA_TYPE_OCTAL+","+ITEM_DATA_TYPE_HEXADECIMAL+","+ITEM_DATA_TYPE_BOOLEAN),"isset({save})&&isset({value_type})&&{value_type}=="+ITEM_VALUE_TYPE_UINT64),
			"valuemapid",							array(T_RDA_INT, O_OPT, null,	DB_ID,		"isset({save})&&isset({value_type})&&"+IN(ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64, "value_type")),
			"authtype",								array(T_RDA_INT, O_OPT, null,	IN(ITEM_AUTHTYPE_PASSWORD+","+ITEM_AUTHTYPE_PUBLICKEY),"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SSH),
			"username",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET, "type"), _("User name")),
			"password",								array(T_RDA_STR, O_OPT, null,	null,"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET, "type")),
			"publickey",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SSH+"&&{authtype}=="+ITEM_AUTHTYPE_PUBLICKEY),
			"privatekey",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,"isset({save})&&isset({type})&&({type})=="+ITEM_TYPE_SSH+"&&({authtype})=="+ITEM_AUTHTYPE_PUBLICKEY),
			paramsFieldName,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_DB_MONITOR+","+ITEM_TYPE_TELNET+","+ITEM_TYPE_CALCULATED, "type"),getParamFieldLabelByType(get_request("type", 0))),
			//"inventory_link",						array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})&&{value_type}!="+ITEM_VALUE_TYPE_LOG),
			"snmp_community",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C, "type"), _("SNMP community")),
			"snmp_oid",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3, "type"), _("SNMP OID")),
			"port",										array(T_RDA_STR, O_OPT, null,	BETWEEN(0, 65535), "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3, "type"), _("Port")),
			"snmpv3_securitylevel",			array(T_RDA_INT, O_OPT, null,	IN("0,1,2"),"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3),
			"snmpv3_contextname",			array(T_RDA_STR, O_OPT, null,	null,"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3),
			"snmpv3_securityname",			array(T_RDA_STR, O_OPT, null,	null,"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3),
			"snmpv3_authprotocol",			array(T_RDA_INT, O_OPT, null,	IN(ITEM_AUTHPROTOCOL_MD5+","+ITEM_AUTHPROTOCOL_SHA),"isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3+"&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+")"),
			"snmpv3_authpassphrase",		array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3+"&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+")"),
			"snmpv3_privprotocol",			array(T_RDA_INT, O_OPT, null,	IN(ITEM_PRIVPROTOCOL_DES+","+ITEM_PRIVPROTOCOL_AES), "isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3+"&&{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV),
			"snmpv3_privpassphrase",		array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_SNMPV3+"&&{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV),
			"ipmi_sensor",							array(T_RDA_STR, O_OPT, NO_TRIM, NOT_EMPTY, "isset({save})&&isset({type})&&{type}=="+ITEM_TYPE_IPMI, _("IPMI sensor")),
			"trapper_hosts",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({type})&&{type}==2"),
			"units",										array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({value_type})&&"+IN("0,3", "value_type")+"isset({data_type})&&{data_type}!="+ITEM_DATA_TYPE_BOOLEAN),
			"multiplier",								array(T_RDA_INT, O_OPT, null,	null,		null),
			"delta",										array(T_RDA_INT, O_OPT, null,	IN("0,1,2"), "isset({save})&&isset({value_type})&&"+IN("0,3", "value_type")+"isset({data_type})&&{data_type}!="+ITEM_DATA_TYPE_BOOLEAN),
			"formula",									array(T_RDA_DBL_STR, O_OPT, null,		"({value_type}==0&&{}!=0)||({value_type}==3&&{}>0)", "isset({save})&&isset({multiplier})&&{multiplier}==1", _("Custom multiplier")),
			"logtimefmt",							array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({value_type})&&{value_type}==2"),
			"group_itemid",						array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"copy_targetid",						array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"copy_groupid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({copy})&&isset({copy_type})&&{copy_type}==0"),
			"new_application",					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"visible",									array(T_RDA_STR, O_OPT, null,		null,		null),
			"applications",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"new_applications",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"del_history",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_delay_flex",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			// actions
			"go",											array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"update",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"copy",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",										array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"massupdate",							array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",							array(T_RDA_INT, O_OPT, null,	null,		null),
			"flag_from_template",					array(T_RDA_STR, O_OPT, null,	null,		null),
			// filter
			"filter_set",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"filter_groupid",						array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"filter_hostid",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"filter_application",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_name",							array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_type",								array(T_RDA_INT, O_OPT, null, IN(array(-1, ITEM_TYPE_IRADAR, ITEM_TYPE_SNMPV1, ITEM_TYPE_TRAPPER, ITEM_TYPE_SIMPLE, ITEM_TYPE_SNMPV2C,ITEM_TYPE_INTERNAL, ITEM_TYPE_SNMPV3, ITEM_TYPE_IRADAR_ACTIVE, ITEM_TYPE_AGGREGATE, ITEM_TYPE_EXTERNAL, ITEM_TYPE_DB_MONITOR,ITEM_TYPE_IPMI, ITEM_TYPE_SSH, ITEM_TYPE_TELNET, ITEM_TYPE_JMX, ITEM_TYPE_CALCULATED, ITEM_TYPE_SNMPTRAP)), null),
			"filter_key",								array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_snmp_community",			array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_snmpv3_securityname", array(T_RDA_STR, O_OPT, null, null,		null),
			"filter_snmp_oid",						array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_port",								array(T_RDA_INT, O_OPT, P_UNSET_EMPTY, BETWEEN(0, 65535), null, _("Port")),
			"filter_value_type",					array(T_RDA_INT, O_OPT, null,	IN("-1,0,1,2,3,4"), null),
			"filter_data_type",						array(T_RDA_INT, O_OPT, null,	BETWEEN(-1, ITEM_DATA_TYPE_BOOLEAN), null),
			"filter_delay",							array(T_RDA_INT, O_OPT, P_UNSET_EMPTY, BETWEEN(0, SEC_PER_DAY), null, _("Update interval")),
			"filter_history",							array(T_RDA_INT, O_OPT, P_UNSET_EMPTY, BETWEEN(0, 65535), null,	_("History")),
			"filter_trends",							array(T_RDA_INT, O_OPT, P_UNSET_EMPTY, BETWEEN(0, 65535), null, _("Trends")),
			"filter_status",							array(T_RDA_INT, O_OPT, null,	IN(array(-1, ITEM_STATUS_ACTIVE, ITEM_STATUS_DISABLED)), null),
			"filter_state",								array(T_RDA_INT, O_OPT, null,	IN(array(-1, ITEM_STATE_NORMAL, ITEM_STATE_NOTSUPPORTED)), null),
			"filter_templated_items", 			array(T_RDA_INT, O_OPT, null,	IN("-1,0,1"), null),
			"filter_with_triggers",				array(T_RDA_INT, O_OPT, null,	IN("-1,0,1"), null),
			"filter_ipmi_sensor",					array(T_RDA_STR, O_OPT, null,	null,		null),
			// subfilters
			"subfilter_set",							array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"subfilter_apps",						array(T_RDA_STR, O_OPT, null,	null,		null),
			"subfilter_types",						array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_value_types",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_status",						array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_state",						array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_templated_items",		array(T_RDA_INT, O_OPT, null, null,		null),
			"subfilter_with_triggers", 			array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_hosts",						array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_interval",					array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_history",					array(T_RDA_INT, O_OPT, null,	null,		null),
			"subfilter_trends",						array(T_RDA_INT, O_OPT, null,	null,		null),
			// ajax
			"favobj",									array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",									array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
			"favstate",								array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}")
		);
		check_fields(getIdentityBean(), fields);
		
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
		Nest.value(_REQUEST,"params").$(get_request(paramsFieldName, ""));
		unset(_REQUEST,paramsFieldName);
		
		//增加是否来自监控模型的判断
		
		Boolean flag_from_template = Nest.value(_REQUEST, "flag_from_template").asBoolean(true);
		
		if(flag_from_template == null) {
			Long hostid = Nest.value(_REQUEST,"filter_hostid").asLong(true);
			if(hostid == null) {
				hostid = Nest.value(_REQUEST, "hostid").asLong(true);
			}
			if(hostid != null) {
				CHostGet isTemplateParams = new CHostGet();
				isTemplateParams.setHostIds(hostid);
				isTemplateParams.setTemplatedHosts(true);
				isTemplateParams.setOutput(new String[]{"status"});
				CArray<Map> hostsCA = API.Host(getIdBean(), executor).get(isTemplateParams);
				this.isTemplate = Nest.as(Defines.HOST_STATUS_TEMPLATE).asString().equals(Nest.value(hostsCA, 0, "status").asString())?true:false;
			}
		}else {
			this.isTemplate = flag_from_template;
		}
		this.profileFlag = isTemplate? "template.": "";
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions*/
		if (get_request("itemid", false)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(Nest.value(_REQUEST,"itemid").asLong());
			ioptions.setFilter("flags" ,Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setSelectHosts(new String[]{"status"});
			ioptions.setEditable(true);
			ioptions.setPreserveKeys(true);
			CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);
			if (empty(items)) {
				access_deny();
			}
			Map item = reset(items);
			hosts = Nest.value(item,"hosts").asCArray();
		} else if (get_request("hostid", 0) > 0) {
			CHostGet option = new CHostGet();
			option.setHostIds(Nest.value(_REQUEST,"hostid").asLong());
			option.setOutput(new String[]{"status"});
			option.setTemplatedHosts(true);
			option.setEditable(true);
			hosts = API.Host(getIdentityBean(), executor).get(option);
			if (empty(hosts)) {
				access_deny();
			}
		}		
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor,"web.items.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}

		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		CArray<String> subfiltersList = array("subfilter_apps", "subfilter_types", "subfilter_value_types", "subfilter_status",
			"subfilter_state", "subfilter_templated_items", "subfilter_with_triggers", "subfilter_hosts", "subfilter_interval",
			"subfilter_history", "subfilter_trends"
		);
		
		Map host = null;
		if (!empty(hosts)) {
			host = reset(hosts);
			Nest.value(_REQUEST,"filter_hostid").$(Nest.value(host,"hostid").$());
		}
		
		// filter
		if (isset(_REQUEST,"filter_set")) {
			Nest.value(_REQUEST,"filter_groupid").$(get_request("filter_groupid", 0));
			Nest.value(_REQUEST,"filter_hostid").$(get_request("filter_hostid", 0));
			Nest.value(_REQUEST,"filter_application").$(get_request("filter_application"));
			Nest.value(_REQUEST,"filter_name").$(get_request("filter_name"));
			Nest.value(_REQUEST,"filter_type").$(get_request("filter_type", -1));
			Nest.value(_REQUEST,"filter_key").$(get_request("filter_key"));
			Nest.value(_REQUEST,"filter_snmp_community").$(get_request("filter_snmp_community"));
			Nest.value(_REQUEST,"filter_snmpv3_securityname").$(get_request("filter_snmpv3_securityname"));
			Nest.value(_REQUEST,"filter_snmp_oid").$(get_request("filter_snmp_oid"));
			Nest.value(_REQUEST,"filter_port").$(get_request("filter_port"));
			Nest.value(_REQUEST,"filter_value_type").$(get_request("filter_value_type", -1));
			Nest.value(_REQUEST,"filter_data_type").$(get_request("filter_data_type", -1));
			Nest.value(_REQUEST,"filter_delay").$(get_request("filter_delay"));
			Nest.value(_REQUEST,"filter_history").$(get_request("filter_history"));
			Nest.value(_REQUEST,"filter_trends").$(get_request("filter_trends"));
			Nest.value(_REQUEST,"filter_status").$(get_request("filter_status", -1));
			Nest.value(_REQUEST,"filter_state").$(get_request("filter_state", -1));
			Nest.value(_REQUEST,"filter_templated_items").$(get_request("filter_templated_items", -1));
			Nest.value(_REQUEST,"filter_with_triggers").$(get_request("filter_with_triggers", -1));
			Nest.value(_REQUEST,"filter_ipmi_sensor").$(get_request("filter_ipmi_sensor"));

			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_groupid", 				Nest.value(_REQUEST,"filter_groupid").asLong(), 					PROFILE_TYPE_ID);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_hostid", 				Nest.value(_REQUEST,"filter_hostid").asLong(), 						PROFILE_TYPE_ID);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_application", 			Nest.value(_REQUEST,"filter_application").asString(true),			PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_name", 				Nest.value(_REQUEST,"filter_name").asString(true), 					PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_type", 				Nest.value(_REQUEST,"filter_type").asInteger(), 					PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_key", 					Nest.value(_REQUEST,"filter_key").asString(true), 					PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmp_community", 		Nest.value(_REQUEST,"filter_snmp_community").asString(true), 		PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmpv3_securityname", 	Nest.value(_REQUEST,"filter_snmpv3_securityname").asString(true), 	PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmp_oid", 			Nest.value(_REQUEST,"filter_snmp_oid").asString(true), 				PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_port", 				Nest.value(_REQUEST,"filter_port").asString(true), 					PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_value_type", 			Nest.value(_REQUEST,"filter_value_type").asInteger(), 				PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_data_type", 			Nest.value(_REQUEST,"filter_data_type").asInteger(), 				PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_delay", 				Nest.value(_REQUEST,"filter_delay").asString(true), 				PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_history",				Nest.value(_REQUEST,"filter_history").asString(true), 				PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_trends", 				Nest.value(_REQUEST,"filter_trends").asString(true), 				PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_status", 				Nest.value(_REQUEST,"filter_status").asInteger(), 					PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_state", 				Nest.value(_REQUEST,"filter_state").asInteger(), 					PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_templated_items", 		Nest.value(_REQUEST,"filter_templated_items").asInteger(), 			PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_with_triggers", 		Nest.value(_REQUEST,"filter_with_triggers").asInteger(), 			PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+"filter_ipmi_sensor", 			Nest.value(_REQUEST,"filter_ipmi_sensor").asString(true), 			PROFILE_TYPE_STR);

			// subfilters
			for (String name : subfiltersList) {
				Nest.value(_REQUEST,name).$(array());
				CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+name, "", PROFILE_TYPE_STR);
			}
		} else {
			Nest.value(_REQUEST,"filter_groupid"			).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_groupid"));
			Nest.value(_REQUEST,"filter_hostid"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_hostid"));
			Nest.value(_REQUEST,"filter_application"		).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_application"));
			Nest.value(_REQUEST,"filter_name"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_name"));
			Nest.value(_REQUEST,"filter_type"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_type", -1));
			Nest.value(_REQUEST,"filter_key"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_key"));
			Nest.value(_REQUEST,"filter_snmp_community"		).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmp_community"));
			Nest.value(_REQUEST,"filter_snmpv3_securityname").$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmpv3_securityname"));
			Nest.value(_REQUEST,"filter_snmp_oid"			).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_snmp_oid"));
			Nest.value(_REQUEST,"filter_port"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_port"));
			Nest.value(_REQUEST,"filter_value_type"			).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_value_type", -1));
			Nest.value(_REQUEST,"filter_data_type"			).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_data_type", -1));
			Nest.value(_REQUEST,"filter_delay"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_delay"));
			Nest.value(_REQUEST,"filter_history"			).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_history"));
			Nest.value(_REQUEST,"filter_trends"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_trends"));
			Nest.value(_REQUEST,"filter_status"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_status"));
			Nest.value(_REQUEST,"filter_state"				).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_state"));
			Nest.value(_REQUEST,"filter_templated_items"	).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_templated_items", -1));
			Nest.value(_REQUEST,"filter_with_triggers"		).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_with_triggers", -1));
			Nest.value(_REQUEST,"filter_ipmi_sensor"		).$(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+"filter_ipmi_sensor"));

			// subfilters
			for (String name : subfiltersList) {
				if (isset(_REQUEST,"subfilter_set")) {
					Nest.value(_REQUEST,name).$(get_request(name, array()));
					CProfile.update(getIdentityBean(), executor,"web.items."+profileFlag+name, implode(";", Nest.array(_REQUEST,name).asString()), PROFILE_TYPE_STR);
				} else {
					Nest.value(_REQUEST,name).$(array());
					String subfiltersVal = Nest.as(CProfile.get(getIdentityBean(), executor,"web.items."+profileFlag+name)).asString();
					if (!rda_empty(subfiltersVal)) {
						Nest.value(_REQUEST,name).$(explode(";", subfiltersVal));
						Nest.value(_REQUEST,name).$(array_combine(Nest.value(_REQUEST,name).asCArray(), Nest.value(_REQUEST,name).asCArray()));
					}
				}
			}
		}
	
		/* Actions*/
		boolean result = false;
		if (isset(_REQUEST,"add_delay_flex") && isset(_REQUEST,"new_delay_flex")) {
			CTimePeriodValidator timePeriodValidator = CValidator.init(new CTimePeriodValidator(),map("allowMultiple", false));
			Nest.value(_REQUEST,"delay_flex").$(get_request("delay_flex", array()));

			if (timePeriodValidator.validate(getIdentityBean(), Nest.value(_REQUEST,"new_delay_flex","period").asString())) {
				array_push(Nest.value(_REQUEST,"delay_flex").asCArray(), Nest.value(_REQUEST,"new_delay_flex").$());
				unset(_REQUEST,"new_delay_flex");
			} else {
				error(timePeriodValidator.getError());
				show_messages(false, null, _("Invalid time period"));
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"itemid")) {//zhixing
			result = false;
			Map item = get_item_by_itemid(executor, Nest.value(_REQUEST,"itemid").asString());
			if (!empty(item)) {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Item(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"itemid").asLong()));
					}
				});
			}
			show_messages(result, _("Item deleted"), _("Cannot delete item"));
			unset(_REQUEST,"itemid");
			unset(_REQUEST,"form");
			clearCookies(result, get_request("hostid"));
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"itemid")) {
			unset(_REQUEST,"itemid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			CArray<Map> delay_flex = get_request("delay_flex", array());
			String db_delay_flex = "";
			for(Map value : delay_flex) {
				db_delay_flex += Nest.value(value,"delay").$()+"/"+Nest.value(value,"period").$()+";";
			}
			db_delay_flex = trim(db_delay_flex, ';');
			CArray applications = get_request("applications", array());
			Object application = reset(applications);
			if (empty(application)) {
				array_shift(applications);
			}

			DBstart(executor);
			result = true;

			if (!rda_empty(Nest.value(_REQUEST,"new_application").$())) {
				CArray<Long[]> new_appids = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Application(getIdentityBean(), executor).create(array((Map)map(
								"name", Nest.value(_REQUEST,"new_application").$(),
								"hostid", get_request("hostid")
							)));
					}
				}, null);
				if (!empty(new_appids)) {
					Long new_appid = reset(new_appids.get("applicationids"));
					Nest.value(applications,new_appid).$(new_appid);
				} else {
					result = false;
				}
			}

			if (result) {
				Map item = map(
					"name", get_request("name"),
					"description", get_request("description"),
					"key_", get_request("key"),
					"hostid", get_request("hostid"),
					"interfaceid", get_request("interfaceid", 0),
					"delay", get_request("delay"),
					"history", get_request("history"),
					"status", get_request("status", ITEM_STATUS_DISABLED),
					"type", get_request("type"),
					"snmp_community", get_request("snmp_community"),
					"snmp_oid", get_request("snmp_oid"),
					"value_type", get_request("value_type"),
					"trapper_hosts", get_request("trapper_hosts"),
					"port", get_request("port"),
					"units", get_request("units"),
					"multiplier", get_request("multiplier", 0),
					"delta", get_request("delta"),
					"snmpv3_contextname", get_request("snmpv3_contextname"),
					"snmpv3_securityname", get_request("snmpv3_securityname"),
					"snmpv3_securitylevel", get_request("snmpv3_securitylevel"),
					"snmpv3_authprotocol", get_request("snmpv3_authprotocol"),
					"snmpv3_authpassphrase", get_request("snmpv3_authpassphrase"),
					"snmpv3_privprotocol", get_request("snmpv3_privprotocol"),
					"snmpv3_privpassphrase", get_request("snmpv3_privpassphrase"),
					"formula", get_request("formula"),
					"trends", get_request("trends"),
					"logtimefmt", get_request("logtimefmt"),
					"valuemapid", get_request("valuemapid"),
					"delay_flex", db_delay_flex,
					"authtype", get_request("authtype"),
					"username", get_request("username"),
					"password", get_request("password"),
					"publickey", get_request("publickey"),
					"privatekey", get_request("privatekey"),
					"params", get_request("params"),
					"ipmi_sensor", get_request("ipmi_sensor"),
					"data_type", get_request("data_type"),
					"applications", get_request("applications",null),
					"inventory_link", get_request("inventory_link")
				);

				if (hasRequest("itemid")) {
					Long itemId = Nest.as(get_request("itemid")).asLong();

					Map dbItem = get_item_by_itemid_limited(executor,itemId);
					Nest.value(dbItem,"applications").$(get_applications_by_itemid(executor,itemId));

					// unset snmpv3 fields
					if (Nest.value(item,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV) {
						Nest.value(item,"snmpv3_authprotocol").$(ITEM_AUTHPROTOCOL_MD5);
						Nest.value(item,"snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
					} else if (Nest.value(item,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV) {
						Nest.value(item,"snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
					}

					item = CArrayHelper.unsetEqualValues(item, dbItem);
					Nest.value(item,"itemid").$(itemId);

					final Map fitem = Clone.deepcopy(item);
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Item(getIdentityBean(), executor).update(array(fitem)));
						}
					});
				} else {
					final Map fitem = Clone.deepcopy(item);
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Item(getIdentityBean(), executor).create(array(fitem)));
						}
					});
				}
			}
			
			result = DBend(executor, result);

			if (isset(_REQUEST,"itemid")) {
				show_messages(result, _("Item updated"), _("Cannot update item"));
			} else {
				show_messages(result, _("Item added"), _("Cannot add item"));
			}

			if (result) {
				unset(_REQUEST,"itemid");
				unset(_REQUEST,"form");
				clearCookies(result, get_request("hostid"));
			}
		}
		// cleaning history for one item
		else if (isset(_REQUEST,"del_history") && isset(_REQUEST,"itemid")) {
			result = false;
			
			DBstart(executor);
			
			Map item = get_item_by_itemid(executor,Nest.value(_REQUEST,"itemid").asString());
			if (!empty(item)) {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return delete_history_by_itemid(executor,Nest.value(_REQUEST,"itemid").asLong());
					}
				});
			}

			if (result) {
				host = get_host_by_hostid(getIdentityBean(), executor,Nest.value(_REQUEST,"hostid").asLong());
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ITEM, _("Item")+" ["+Nest.value(item,"key_").$()+"] ["+Nest.value(_REQUEST,"itemid").asString()+"] "+
					_("Host")+" ["+Nest.value(host,"name").$()+"] "+_("History cleared"));
			}

			result = DBend(executor, result);
			
			show_messages(result, _("History cleared"), _("Cannot clear history"));
			clearCookies(result, get_request("hostid"));
		}
		// mass update
		else if (isset(_REQUEST,"update") && isset(_REQUEST,"massupdate") && isset(_REQUEST,"group_itemid")) {
			String db_delay_flex = null;
			Map visible = get_request("visible", array());
			if (isset(visible,"delay_flex")) {
				CArray<Map> delay_flex = get_request("delay_flex",array());
				if (!empty(delay_flex)) {
					db_delay_flex = "";
					for(Map val : delay_flex) {
						db_delay_flex += Nest.value(val,"delay").asString()+"/"+Nest.value(val,"period").asString()+";";
					}
					db_delay_flex = trim(db_delay_flex, ';');
				} else {
					db_delay_flex = "";
				}
			} else {
				db_delay_flex = null;
			}

			if (!is_null(get_request("formula", null))) {
				Nest.value(_REQUEST,"multiplier").$(1);
			}
			if ("0".equals(get_request("formula", ""))) {
				Nest.value(_REQUEST,"multiplier").$(0);
			}

			CArray applications = get_request("applications", null);
			if (isset(applications,0) && "0".equals(Nest.value(applications,0).asString())) {
				applications = array();
			}

			try {
				DBstart(executor);
				
				// add new or existing applications
				CArray existApplication = null;
				if (isset(visible,"new_applications") && !empty(Nest.value(_REQUEST,"new_applications").$())) {
					final CArray<Map> newApplications = array();
					for(Object newApplication : Nest.value(_REQUEST,"new_applications").asCArray()) {
						if (is_array(newApplication) && isset((Map)newApplication,"new")) {
							newApplications.add(map(
								"name", Nest.value((Map)newApplication,"new").$(),
								"hostid", get_request("hostid")
							));
						} else {
							if(existApplication==null){
								existApplication = array();
							};
							existApplication.add(newApplication);
						}
					}

					if (isset(newApplications)) {
						CArray<Long[]> createdApplication = Call(new Wrapper<CArray<Long[]>>() {
							@Override
							protected CArray<Long[]> doCall() throws Throwable {
								return API.Application(getIdentityBean(), executor).create(newApplications);
							}
						}, null);
						if (empty(createdApplication)) {
							throw new Exception();
						}
						if (isset(existApplication)) {
							existApplication = array_merge(existApplication, CArray.valueOf(createdApplication.get("applicationids")));
						} else {
							existApplication = CArray.valueOf(createdApplication.get("applicationids"));
						}
					}
				}

				if (isset(visible,"applications")) {
					if (isset(_REQUEST,"applications")) {
						if (isset(existApplication)) {
							applications = array_unique(array_merge(Nest.value(_REQUEST,"applications").asCArray(), existApplication));
						} else {
							applications = Nest.value(_REQUEST,"applications").asCArray();
						}
					} else {
						if (isset(existApplication)){
							applications = Clone.deepcopy(existApplication);
						} else {
							applications = array();
						}
					}
				}

				final Map item = map(
					"interfaceid", get_request("interfaceid"),
					"description", get_request("description"),
					"delay", get_request("delay"),
					"history", get_request("history"),
					"status", get_request("status"),
					"type", get_request("type"),
					"snmp_community", get_request("snmp_community"),
					"snmp_oid", get_request("snmp_oid"),
					"value_type", get_request("value_type"),
					"trapper_hosts", get_request("trapper_hosts"),
					"port", get_request("port"),
					"units", get_request("units"),
					"multiplier", get_request("multiplier"),
					"delta", get_request("delta"),
					"snmpv3_contextname", get_request("snmpv3_contextname"),
					"snmpv3_securityname", get_request("snmpv3_securityname"),
					"snmpv3_securitylevel", get_request("snmpv3_securitylevel"),
					"snmpv3_authprotocol", get_request("snmpv3_authprotocol"),
					"snmpv3_authpassphrase", get_request("snmpv3_authpassphrase"),
					"snmpv3_privprotocol", get_request("snmpv3_privprotocol"),
					"snmpv3_privpassphrase", get_request("snmpv3_privpassphrase"),
					"formula", get_request("formula"),
					"trends", get_request("trends"),
					"logtimefmt", get_request("logtimefmt"),
					"valuemapid", get_request("valuemapid"),
					"delay_flex", db_delay_flex,
					"authtype", get_request("authtype"),
					"username", get_request("username"),
					"password", get_request("password"),
					"publickey", get_request("publickey"),
					"privatekey", get_request("privatekey"),
					"ipmi_sensor", get_request("ipmi_sensor"),
					"applications", applications,
					"data_type", get_request("data_type")
				);

				// add applications
				if (!empty(existApplication) && (!isset(visible,"applications") || !isset(_REQUEST,"applications"))) {
					CArray<Map> linkApplications = array();
					for(Object linkApp : existApplication) {
						linkApplications.add(map("applicationid", linkApp));
					}
					CArray<Map> linkItems = array();
					for(Object linkItem : get_request("group_itemid",array())) {
						linkItems.add(map("itemid", linkItem));
					}
					
					final CArray<CArray<Map>> linkApp = map(
						"applications", linkApplications,
						"items", linkItems
					);
					API.Application(getIdentityBean(), executor).massAdd(linkApp);
					Call(new Wrapper() {
						@Override
						protected Object doCall() throws Throwable {
							return API.Application(getIdentityBean(), executor).massAdd(linkApp);
						}
					});
				}

				List<String> ufields = new ArrayList();
				for (Entry<String, Object> e : ((Map<String,Object>)item).entrySet()) {
				    String key = e.getKey();
				    Object field = e.getValue();
					if (field == null) {
						ufields.add(key);
					}
				}
				
				for (String key : ufields) {
					unset(item,key);
				}

				for(Object id : Nest.value(_REQUEST,"group_itemid").asCArray()) {
					Nest.value(item,"itemid").$(id);

					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Item(getIdentityBean(), executor).update(array(Clone.deepcopy(item))));
						}
					});
					
					if (!result) {
						break;
					}
				}
			} catch (Exception e) {
				result = false;
			}
			
			result = DBend(executor, result);

			show_messages(result, _("Items updated"), _("Cannot update items"));

			if (result) {
				unset(_REQUEST,"group_itemid");
				unset(_REQUEST,"massupdate");
				unset(_REQUEST,"update");
				unset(_REQUEST,"form");
				clearCookies(result, get_request("hostid"));
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_itemid")) {
			final Long[] groupItemId = get_requests("group_itemid").valuesAsLong();
			final boolean enable = ("activate".equals(get_request("go")));
			
			DBstart(executor);
			result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return enable ? activate_item(getIdentityBean(), executor,groupItemId) : disable_item(getIdentityBean(), executor,groupItemId);
				}
			});
			result = DBend(executor, result);
			
			int updated = count(groupItemId);

			String messageSuccess = enable
				? _n("启用成功", "启用成功", updated)
				: _n("停用成功", "停用成功", updated);
			String messageFailed = enable
				? _n("启用失败", "启用失败", updated)
				: _n("停用失败", "停用失败", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		} else if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"copy") && isset(_REQUEST,"group_itemid")) {
			if (isset(_REQUEST,"copy_targetid") && Nest.array(_REQUEST,"copy_targetid").asLong().length > 0 && isset(_REQUEST,"copy_type")) {
				final CArray hosts_ids = array();
				if (Nest.value(_REQUEST,"copy_type").asLong() == 0) {// host
					hosts_ids.putAll(Nest.value(_REQUEST,"copy_targetid").asCArray());
				} else {// groups
					CArray group_ids = CArray.valueOf(Nest.value(_REQUEST,"copy_targetid").$());

					SqlBuilder sqlParts = new SqlBuilder();
					CArray<Map> db_hosts = DBselect(executor,
						"SELECT DISTINCT h.hostid"+
						" FROM hosts h,hosts_groups hg"+
						" WHERE h.hostid=hg.hostid"+
							" AND "+sqlParts.dual.dbConditionInt("hg.groupid", group_ids.valuesAsLong()),
						sqlParts.getNamedParams()
					);
					for(Map db_host : db_hosts) {
						hosts_ids.add(Nest.value(db_host,"hostid").$());
					}
				}

				DBstart(executor);
				
				boolean goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return copyItemsToHosts(getIdentityBean(), executor, Nest.array(_REQUEST,"group_itemid").asLong(), hosts_ids.valuesAsLong());
					}
				});
				goResult = DBend(executor, goResult);

				show_messages(goResult, _("Items copied"), _("Cannot copy items"));
				clearCookies(goResult, get_request("hostid"));

				Nest.value(_REQUEST,"go").$("none2");
			} else {
				show_error_message(_("No target selected."));
			}
		}
		// clean history for selected items
		else if ("clean_history".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_itemid")) {
			DBstart(executor);
			
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return delete_history_by_itemid(executor, Nest.value(_REQUEST,"group_itemid").asCArray().valuesAsLong());
				}
			});

			Map item = null;
			for(String id : Nest.array(_REQUEST,"group_itemid").asString()) {
				if (empty(item = get_item_by_itemid(executor,id))) {
					continue;
				}
				host = get_host_by_hostid(getIdentityBean(), executor, Nest.value(item,"hostid").asLong());
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ITEM,
					_("Item")+" ["+Nest.value(item,"key_").$()+"] ["+id+"] "+_("Host")+" ["+Nest.value(host,"host").$()+"] "+_("History cleared")
				);
			}
			
			goResult = DBend(executor, goResult);

			show_messages(goResult, _("History cleared"));
			clearCookies(goResult, get_request("hostid"));
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_itemid")) {
			DBstart(executor);
			
			final Long[] group_itemid = Nest.array(_REQUEST,"group_itemid").asLong();

			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"key_", "itemid"});
			ioptions.setSelectHosts(new String[]{"name"});
			ioptions.setItemIds(group_itemid);
			ioptions.setPreserveKeys(true);
			CArray<Map> itemsToDelete = API.Item(getIdentityBean(), executor).get(ioptions);

			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Item(getIdentityBean(), executor).delete(TArray.as(group_itemid).asLong()));
				}
			});

			if (goResult) {
				for(Map item : itemsToDelete) {
					host = reset(Nest.value(item,"hosts").asCArray());
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_ITEM, _("Item")+" ["+Nest.value(item,"key_").$()+"] ["+Nest.value(item,"itemid").$()+"] "+
						_("Host")+" ["+Nest.value(host,"name").$()+"]");
				}
			}

			show_messages(DBend(executor, goResult), _("Items deleted"), _("Cannot delete items"));
			clearCookies(goResult, get_request("hostid"));
		}

		/* Display */
		if (isset(_REQUEST,"form") && str_in_array(Nest.value(_REQUEST,"form").asString(), array(_("Create item"), "update", "clone"))) {
			Map data = getItemFormData(getIdentityBean(), executor);
			Nest.value(data,"page_header").$(_("CONFIGURATION OF ITEMS"));

			// render view
			CView itemView = new CView("configuration.item.edit", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		} else if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) || isset(_REQUEST,"massupdate") && isset(_REQUEST,"group_itemid")) {
			Map data = map(
				"form", get_request("form"),
				"hostid", get_request("hostid"),
				"itemids", get_request("group_itemid", array()),
				"description", get_request("description", ""),
				"delay", get_request("delay", RDA_ITEM_DELAY_DEFAULT),
				"delay_flex", get_request("delay_flex", array()),
				"history", get_request("history", 90),
				"status", get_request("status", 0),
				"type", get_request("type", 0),
				"interfaceid", get_request("interfaceid", 0),
				"snmp_community", get_request("snmp_community", "public"),
				"port", get_request("port", ""),
				"value_type", get_request("value_type", ITEM_VALUE_TYPE_UINT64),
				"data_type", get_request("data_type", ITEM_DATA_TYPE_DECIMAL),
				"trapper_hosts", get_request("trapper_hosts", ""),
				"units", get_request("units", ""),
				"authtype", get_request("authtype", ""),
				"username", get_request("username", ""),
				"password", get_request("password", ""),
				"publickey", get_request("publickey", ""),
				"privatekey", get_request("privatekey", ""),
				"valuemapid", get_request("valuemapid", 0),
				"delta", get_request("delta", 0),
				"trends", get_request("trends", DAY_IN_YEAR),
				"applications", get_request("applications", array()),
				"snmpv3_contextname", get_request("snmpv3_contextname", ""),
				"snmpv3_securityname", get_request("snmpv3_securityname", ""),
				"snmpv3_securitylevel", get_request("snmpv3_securitylevel", 0),
				"snmpv3_authprotocol", get_request("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_MD5),
				"snmpv3_authpassphrase", get_request("snmpv3_authpassphrase", ""),
				"snmpv3_privprotocol", get_request("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_DES),
				"snmpv3_privpassphrase", get_request("snmpv3_privpassphrase", ""),
				"formula", get_request("formula", "1"),
				"logtimefmt", get_request("logtimefmt", ""),
				"initial_item_type", null,
				"multiple_interface_types", false,
				"visible", get_request("visible", array())
			);

			Nest.value(data,"displayApplications").$(true);
			Nest.value(data,"displayInterfaces").$(true);
			CArray<Map> itemids=Nest.value(data,"itemids").asCArray();
			// hosts
			CHostGet hoptions = new CHostGet();
			hoptions.setItemIds(itemids.valuesAsLong());
			hoptions.setSelectInterfaces(API_OUTPUT_EXTEND);
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"hosts").$(hosts);
			int hostCount = count(hosts);

			if (hostCount > 1) {
				Nest.value(data,"displayApplications").$(false);
				Nest.value(data,"displayInterfaces").$(false);
			} else {
				// get template count to display applications multiselect only for single template
				CTemplateGet toptions = new CTemplateGet();
				toptions.setOutput(new String[]{"templateid"});
				toptions.setItemIds(itemids.valuesAsLong());
				CArray<Map> templates = API.Template(getIdentityBean(), executor).get(toptions);
				int templateCount = count(templates);

				if (templateCount != 0) {
					Nest.value(data,"displayInterfaces").$(false);

					if (templateCount == 1 && empty(Nest.value(data,"hostid").$())) {
						// if selected from filter without "hostid"
						Map template = reset(templates);
						Nest.value(data,"hostid").$(Nest.value(template,"templateid").$());
					}

					// if items belong to single template and some belong to single host, don't display application multiselect
					// and don't display application multiselect for multiple templates
					if (hostCount == 1 && templateCount == 1 || templateCount > 1) {
						Nest.value(data,"displayApplications").$(false);
					}
				}

				if (hostCount == 1 && Nest.value(data,"displayInterfaces").asBoolean()) {
					Nest.value(data,"hosts").$(reset(hosts));

					// if selected from filter without "hostid"
					if (empty(Nest.value(data,"hostid").$())) {
						Nest.value(data,"hostid").$(Nest.value(hosts,"hostid").$());
					}
					
					// set the initial chosen interface to one of the interfaces the items use
					CItemGet ioptions = new CItemGet();
					ioptions.setItemIds(itemids.valuesAsLong());
					ioptions.setOutput(new String[]{"itemid", "type"});
					CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);
					CArray<Integer> usedInterfacesTypes = array();
					for(Map item : items) {
						Nest.value(usedInterfacesTypes,item.get("type")).$(itemTypeInterface(Nest.value(item,"type").asInteger()));
					}
					Integer initialItemType = min(array_keys(usedInterfacesTypes).valuesAsInteger());
					Nest.value(data,"type").$((get_request("type") != null) ? (Nest.value(data,"type").$()) : initialItemType);
					Nest.value(data,"initial_item_type").$(initialItemType);
					Nest.value(data,"multiple_interface_types").$((count(array_unique(usedInterfacesTypes)) > 1));
				}
			}

			// item types
			Nest.value(data,"itemTypes").$(item_type2str());
			unset(Nest.value(data,"itemTypes").asCArray(),ITEM_TYPE_HTTPTEST);

			// valuemap
			CArray<Map> valuemaps = DBselect(executor,
					"SELECT v.valuemapid,v.name"+
					" FROM valuemaps v"
			);
			Nest.value(data,"valuemaps").$(valuemaps);
			order_result(valuemaps, "name");

			// render view
			CView itemView = new CView("configuration.item.massupdate", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		} else if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_itemid")) {
			Map data = map(
				"group_itemid", get_request("group_itemid", array()),
				"hostid", get_request("hostid", 0),
				"copy_type", get_request("copy_type", 0),
				"copy_groupid", get_request("copy_groupid", 0),
				"copy_targetid", get_request("copy_targetid", array())
			);

			if (!is_array(Nest.value(data,"group_itemid").$()) || (is_array(Nest.value(data,"group_itemid").$()) && count(Nest.value(data,"group_itemid").$()) < 1)) {
				error(_("Incorrect list of items."));
			} else {
				// group
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map>groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
				Nest.value(data,"groups").$(groups);
				order_result(groups, "name");

				// hosts
				if (Nest.value(data,"copy_type").asInteger() == 0) {
					if (empty(Nest.value(data,"copy_groupid").$())) {
						for(Map group : groups) {
							Nest.value(data,"copy_groupid").$(Nest.value(group,"groupid").$());
						}
					}

					CHostGet hoptions = new CHostGet();
					hoptions.setOutput(API_OUTPUT_EXTEND);
					hoptions.setGroupIds(Nest.array(data,"copy_groupid").asLong());
					hoptions.setTemplatedHosts(true);
					CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
					Nest.value(data,"hosts").$(hosts);
					order_result(hosts, "name");
				}
			}

			// render view
			CView itemView = new CView("configuration.item.copy", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		}
		// list of items
		else {
			Nest.value(_REQUEST,"hostid").$(empty(Nest.value(_REQUEST,"filter_hostid").$()) ? null : Nest.value(_REQUEST,"filter_hostid").$());

			Map data = map(
				"form", get_request("form"),
				"hostid", get_request("hostid"),
				"sortfield", getPageSortField(getIdentityBean(), executor,"name")
			);

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			
			// items
			CItemGet options = new CItemGet();
			options.setHostIds(Nest.value(data,"hostid").asLong());
			options.setSearch(array());
			options.setOutput(new String[]{
					"itemid", "type", "hostid", "name", "key_", "delay", "history", "trends", "status", "value_type", "error",
					"templateid", "flags", "state"
			});
			options.setEditable(true);
			options.setSelectHosts(API_OUTPUT_EXTEND);
			options.setSelectTriggers(API_OUTPUT_REFER);
			options.setSelectApplications(API_OUTPUT_EXTEND);
			options.setSelectDiscoveryRule(API_OUTPUT_EXTEND);
			options.setSelectItemDiscovery(new String[]{"ts_delete"});
			options.setSortfield(Nest.value(data,"sortfield").asString());
			options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			
			boolean changedFilter =false;
			if (isset(_REQUEST,"filter_groupid") && !empty(Nest.value(_REQUEST,"filter_groupid").$())) {
				options.setGroupIds(Nest.value(_REQUEST,"filter_groupid").asLong());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_hostid") && !empty(Nest.value(_REQUEST,"filter_hostid").$())) {
				Nest.value(data,"filter_hostid").$(Nest.value(_REQUEST,"filter_hostid").$());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_application") && !rda_empty(Nest.value(_REQUEST,"filter_application").$())) {
				options.setApplication(Nest.value(_REQUEST,"filter_application").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_name") && !rda_empty(Nest.value(_REQUEST,"filter_name").$())) {
				options.setSearch("name", Nest.value(_REQUEST,"filter_name").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_type") && !rda_empty(Nest.value(_REQUEST,"filter_type").$()) && Nest.value(_REQUEST,"filter_type").asInteger() != -1) {
				options.setFilter("type", Nest.value(_REQUEST,"filter_type").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_key") && !rda_empty(Nest.value(_REQUEST,"filter_key").$())) {
				options.setSearch("key_", Nest.value(_REQUEST,"filter_key").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_snmp_community") && !rda_empty(Nest.value(_REQUEST,"filter_snmp_community").$())) {
				options.setFilter("snmp_community",Nest.value(_REQUEST,"filter_snmp_community").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_snmpv3_securityname") && !rda_empty(Nest.value(_REQUEST,"filter_snmpv3_securityname").$())) {
				options.setFilter("snmpv3_securityname",Nest.value(_REQUEST,"filter_snmpv3_securityname").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_snmp_oid") && !rda_empty(Nest.value(_REQUEST,"filter_snmp_oid").$())) {
				options.setFilter("snmp_oid",Nest.value(_REQUEST,"filter_snmp_oid").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_port") && !rda_empty(Nest.value(_REQUEST,"filter_port").$())) {
				options.setFilter("port",Nest.value(_REQUEST,"filter_port").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_value_type") && !rda_empty(Nest.value(_REQUEST,"filter_value_type").$())
					&& Nest.value(_REQUEST,"filter_value_type").asInteger() != -1) {
				options.setFilter("value_type",Nest.value(_REQUEST,"filter_value_type").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_data_type") && !rda_empty(Nest.value(_REQUEST,"filter_data_type").$())
					&& Nest.value(_REQUEST,"filter_data_type").asInteger() != -1) {
				options.setFilter("data_type",Nest.value(_REQUEST,"filter_data_type").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_delay") && !rda_empty(Nest.value(_REQUEST,"filter_delay").$())) {
				options.setFilter("delay",Nest.value(_REQUEST,"filter_delay").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_history") && !rda_empty(Nest.value(_REQUEST,"filter_history").$())) {
				options.setFilter("history",Nest.value(_REQUEST,"filter_history").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_trends") && !rda_empty(Nest.value(_REQUEST,"filter_trends").$())) {
				options.setFilter("trends",Nest.value(_REQUEST,"filter_trends").asString());
				if (!(isset(_REQUEST,"filter_value_type") && !rda_empty(Nest.value(_REQUEST,"filter_value_type").$())
						&& Nest.value(_REQUEST,"filter_value_type").asInteger() != -1)) {
					options.setFilter("value_type",Defines.ITEM_VALUE_TYPE_FLOAT,Defines.ITEM_VALUE_TYPE_UINT64);
				}
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_status") && !rda_empty(Nest.value(_REQUEST,"filter_status").$()) && Nest.value(_REQUEST,"filter_status").asInteger() != -1) {
				options.setFilter("status",Nest.value(_REQUEST,"filter_status").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_state") && !rda_empty(Nest.value(_REQUEST,"filter_state").$()) && Nest.value(_REQUEST,"filter_state").asInteger() != -1) {
				options.setFilter("status",Nest.as(ITEM_STATUS_ACTIVE).asString());
				options.setFilter("state",Nest.value(_REQUEST,"filter_state").asString());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_templated_items") && !rda_empty(Nest.value(_REQUEST,"filter_templated_items").$())
					&& Nest.value(_REQUEST,"filter_templated_items").asInteger() != -1) {
				options.setInherited(Nest.value(_REQUEST,"filter_templated_items").asBoolean());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_with_triggers") && !rda_empty(Nest.value(_REQUEST,"filter_with_triggers").$())
					&& Nest.value(_REQUEST,"filter_with_triggers").asInteger() != -1) {
				options.setWithTriggers(Nest.value(_REQUEST,"filter_with_triggers").asBoolean());
				changedFilter = true;
			}
			if (isset(_REQUEST,"filter_ipmi_sensor") && !rda_empty(Nest.value(_REQUEST,"filter_ipmi_sensor").$())) {
				options.setFilter("ipmi_sensor",Nest.value(_REQUEST,"filter_ipmi_sensor").asString());
				changedFilter = true;
			}

			CArray<Map> items = null;
			if (empty(Nest.value(options,"hostids").$()) && !changedFilter) {
				items = array();
			} else {
				items = API.Item(getIdentityBean(), executor).get(options);
			}
			Nest.value(data,"items").$(items);

			// set values for subfilters, if any of subfilters = false then item shouldnt be shown
			if (!empty(items)) {
				// fill template host
				fillItemsWithChildTemplates(executor,items);

				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> dbHostItems = DBselect(executor,
					"SELECT i.itemid,h.name,h.hostid"+
					" FROM hosts h,items i"+
					" WHERE i.hostid=h.hostid"+
						" AND "+sqlParts.dual.dbConditionInt("i.itemid", rda_objectValues(items, "templateid").valuesAsLong()),
					sqlParts.getNamedParams()
				);
				for(Map dbHostItem : dbHostItems) {
					for(Map item : items) {
						if (Nest.value(item,"templateid").asLong() == Nest.value(dbHostItem,"itemid").asLong()) {
							Nest.value(item,"template_host").$(dbHostItem);
						}
					}
				}

				// resolve name macros
				items = resolveItemNames(getIdentityBean(), executor,items);
				Nest.value(data,"items").$(items);

				for(Map item : items) {
					Nest.value(item,"hostids").$(rda_objectValues(Nest.value(item,"hosts").$(), "hostid"));

					if (empty(Nest.value(data,"filter_hostid").$())) {
						host = reset(Nest.value(item,"hosts").asCArray());
						Nest.value(item,"host").$(Nest.value(host,"name").$());
					}

					Nest.value(item,"subfilters").$(map(
						"subfilter_hosts", empty(Nest.value(_REQUEST,"subfilter_hosts").$())
							|| !empty(array_intersect(Nest.value(_REQUEST,"subfilter_hosts").asCArray(), Nest.value(item,"hostids").asCArray())),
						"subfilter_types", empty(Nest.value(_REQUEST,"subfilter_types").$())
							|| uint_in_array(Nest.value(item,"type").$(), Nest.value(_REQUEST,"subfilter_types").asCArray()),
						"subfilter_value_types", empty(Nest.value(_REQUEST,"subfilter_value_types").$())
							|| uint_in_array(Nest.value(item,"value_type").$(), Nest.value(_REQUEST,"subfilter_value_types").asCArray()),
						"subfilter_status", empty(Nest.value(_REQUEST,"subfilter_status").$())
							|| uint_in_array(Nest.value(item,"status").$(), Nest.value(_REQUEST,"subfilter_status").asCArray()),
						"subfilter_state", empty(Nest.value(_REQUEST,"subfilter_state").$())
							|| uint_in_array(Nest.value(item,"state").$(), Nest.value(_REQUEST,"subfilter_state").asCArray()),
						"subfilter_templated_items", empty(Nest.value(_REQUEST,"subfilter_templated_items").$())
							|| (Nest.value(item,"templateid").asLong() == 0 && uint_in_array(0, Nest.value(_REQUEST,"subfilter_templated_items").asCArray())
							|| (Nest.value(item,"templateid").asLong() > 0 && uint_in_array(1, Nest.value(_REQUEST,"subfilter_templated_items").asCArray()))),
						"subfilter_with_triggers", empty(Nest.value(_REQUEST,"subfilter_with_triggers").$())
							|| (count(Nest.value(item,"triggers").$()) == 0 && uint_in_array(0, Nest.value(_REQUEST,"subfilter_with_triggers").asCArray()))
							|| (count(Nest.value(item,"triggers").$()) > 0 && uint_in_array(1, Nest.value(_REQUEST,"subfilter_with_triggers").asCArray())),
						"subfilter_history", empty(Nest.value(_REQUEST,"subfilter_history").$())
							|| uint_in_array(Nest.value(item,"history").$(), Nest.value(_REQUEST,"subfilter_history").asCArray()),
						"subfilter_trends", empty(Nest.value(_REQUEST,"subfilter_trends").$())
							|| uint_in_array(Nest.value(item,"trends").$(), Nest.value(_REQUEST,"subfilter_trends").asCArray()),
						"subfilter_interval", empty(Nest.value(_REQUEST,"subfilter_interval").$())
							|| uint_in_array(Nest.value(item,"delay").$(), Nest.value(_REQUEST,"subfilter_interval").asCArray()),
						"subfilter_apps", empty(Nest.value(_REQUEST,"subfilter_apps").$())
					));

					CArray<Map> apps = (CArray<Map>)Nest.value(item,"applications").asCArray();
					if (!empty(Nest.value(_REQUEST,"subfilter_apps").$())) {
						for(Map application : apps) {
							if (str_in_array(Nest.value(application,"name").$(), Nest.value(_REQUEST,"subfilter_apps").asCArray())) {
								Nest.value(item,"subfilters","subfilter_apps").$(true);
								break;
							}
						}
					}

					if (!empty(apps)) {
						order_result(apps, "name");

						CArray applications = array();
						for(Map application : apps) {
							applications.add(Nest.value(application,"name").$());
						}
						Nest.value(item,"applications_list").$(implode(", ", applications));
					} else {
						Nest.value(item,"applications_list").$("");
					}
				}

				// disable subfilters if list is empty
				boolean atLeastOne = false;
				for(Map item : items) {
					atLeastOne = true;
					for(Object value : Nest.value(item,"subfilters").asCArray()) {
						if (empty(value)) {
							atLeastOne = false;
							break;
						}
					}
					if (atLeastOne) {
						break;
					}
				}
				if (!atLeastOne) {
					for(String name : subfiltersList) {
						Nest.value(_REQUEST,name).$(array());
						CProfile.update(getIdentityBean(), executor, "web.items."+profileFlag+name, "", PROFILE_TYPE_STR);
						for(Map item : items) {
							Nest.value(item,"subfilters",name).$(true);
						}
					}
				}
			}

			Nest.value(data,"flicker").$(getItemFilterForm(getIdentityBean(), executor, items, isTemplate));

			// remove subfiltered items
			if (!empty(items)) {
				for (Entry<Object, Map> e : Clone.deepcopy(items).entrySet()) {
				    Object number = e.getKey();
				    Map item = e.getValue();
					for(Object value : Nest.value(item,"subfilters").asCArray()) {
						if (empty(value)) {
							unset(items,number);
							break;
						}
					}
				}
			}

			if ("status".equals(Nest.value(data,"sortfield").asString())) {
				orderItemsByStatus(items, getPageSortOrder(getIdentityBean(), executor));
			} else {
				order_result(items, Nest.value(data,"sortfield").asString(), getPageSortOrder(getIdentityBean(), executor));
			}

			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, items, array("itemid")));

			CArray itemTriggerIds = array();
			for(Map item : items) {
				itemTriggerIds = array_merge(itemTriggerIds, rda_objectValues(Nest.value(item,"triggers").$(), "triggerid"));
			}
			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(itemTriggerIds.valuesAsLong());
			toptions.setOutput(API_OUTPUT_EXTEND);
			toptions.setSelectHosts(new String[]{"hostid", "name", "host"});
			toptions.setSelectFunctions(API_OUTPUT_EXTEND);
			toptions.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
			toptions.setPreserveKeys(true);
			CArray<Map> itemTriggers = API.Trigger(getIdentityBean(), executor).get(toptions);
			Nest.value(data,"itemTriggers").$(itemTriggers);
			Nest.value(data,"triggerRealHosts").$(getParentHostsByTriggers(getIdentityBean(), executor,itemTriggers));

			// determine, show or not column of errors
			if (isset(hosts)) {
				host = reset(hosts);
				Nest.value(data,"showErrorColumn").$((Nest.value(host,"status").asInteger() != HOST_STATUS_TEMPLATE));
			} else {
				Nest.value(data,"showErrorColumn").$(true);
			}
			
			// render view
			CView itemView = new CView("configuration.item.list", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		}
	}
}
