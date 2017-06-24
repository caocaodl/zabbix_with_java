package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
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
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_EXTERNAL;
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
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FormsUtil.getItemFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.get_requests;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ItemsUtil.activate_item;
import static com.isoft.iradar.inc.ItemsUtil.disable_item;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldLabelByType;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldNameByType;
import static com.isoft.iradar.inc.ItemsUtil.get_applications_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid_limited;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DiscPrototypesAction extends RadarBaseAction {
	
	private Map discovery_rule;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of item prototypes"));
		page("file", "disc_prototypes.action");
		page("scripts", new String[] {"effects.js", "class.cviewswitcher.js"});
		page("hist_arg", new String[] {"parent_discoveryid"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		String paramsFieldName = getParamFieldNameByType(get_request("type", 0));

		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"parent_discoveryid",			array(T_RDA_INT, O_MAND, P_SYS,	DB_ID,		null),
			"itemid",								array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"hostid",								array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"interfaceid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null, _("Interface")),
			"name",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"description",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"key",									array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Key")),
			"delay",									array(T_RDA_INT, O_OPT, null,	BETWEEN(0, SEC_PER_DAY),	"isset({save})&&(isset({type})&&({type}!="+ITEM_TYPE_TRAPPER+"&&{type}!="+ITEM_TYPE_SNMPTRAP+"))", _("Update interval (in sec)")),
			"new_delay_flex",					array(T_RDA_STR, O_OPT, null, NOT_EMPTY, "isset({add_delay_flex})&&(isset({type})&&({type}!=2))", _("New flexible interval")),
			"delay_flex",							array(T_RDA_STR, O_OPT, null,	"",			null),
			"status",								array(T_RDA_INT, O_OPT, null,	IN(ITEM_STATUS_ACTIVE), null),
			"type",									array(T_RDA_INT, O_OPT, null, IN(array(-1, ITEM_TYPE_IRADAR, ITEM_TYPE_SNMPV1, ITEM_TYPE_TRAPPER, ITEM_TYPE_SIMPLE, ITEM_TYPE_SNMPV2C,ITEM_TYPE_INTERNAL, ITEM_TYPE_SNMPV3, ITEM_TYPE_IRADAR_ACTIVE, ITEM_TYPE_AGGREGATE, ITEM_TYPE_EXTERNAL,ITEM_TYPE_DB_MONITOR, ITEM_TYPE_IPMI, ITEM_TYPE_SSH, ITEM_TYPE_TELNET, ITEM_TYPE_JMX, ITEM_TYPE_CALCULATED,ITEM_TYPE_SNMPTRAP)),"isset({save})"),
			"value_type",						array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4"), "isset({save})"),
			"data_type",							array(T_RDA_INT, O_OPT, null,	IN(ITEM_DATA_TYPE_DECIMAL+","+ITEM_DATA_TYPE_OCTAL+","+ITEM_DATA_TYPE_HEXADECIMAL+","+ITEM_DATA_TYPE_BOOLEAN), "isset({save})&&(isset({value_type})&&({value_type}=="+ITEM_VALUE_TYPE_UINT64+"))"),
			"valuemapid",						array(T_RDA_INT, O_OPT, null,	DB_ID,		"isset({save})&&isset({value_type})&&"+IN(ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64, "value_type")),
			"authtype",							array(T_RDA_INT, O_OPT, null,	IN(ITEM_AUTHTYPE_PASSWORD+","+ITEM_AUTHTYPE_PUBLICKEY),"isset({save})&&isset({type})&&({type}=="+ITEM_TYPE_SSH+")"),
			"username",							array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET, "type"), _("User name")),
			"password",							array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET, "type")),
			"publickey",							array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({type})&&({type})=="+ITEM_TYPE_SSH+"&&({authtype})=="+ITEM_AUTHTYPE_PUBLICKEY),
			"privatekey",							array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({type})&&({type})=="+ITEM_TYPE_SSH+"&&({authtype})=="+ITEM_AUTHTYPE_PUBLICKEY),
			paramsFieldName,				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_DB_MONITOR+","+ITEM_TYPE_TELNET+","+ITEM_TYPE_CALCULATED,"type"), getParamFieldLabelByType(get_request("type", 0))),
			"snmp_community",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C,"type"), _("SNMP community")),
			"snmp_oid",							array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3,"type"), _("SNMP OID")),
			"port",									array(T_RDA_STR, O_OPT, null,	BETWEEN(0, 65535),"isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3,"type"),_("Port")),
			"snmpv3_securitylevel",		array(T_RDA_INT, O_OPT, null,	IN("0,1,2"),"isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_contextname",		array(T_RDA_STR, O_OPT, null,	null,"isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_securityname",		array(T_RDA_STR, O_OPT, null,	null,"isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_authprotocol",		array(T_RDA_INT, O_OPT, null,	IN(ITEM_AUTHPROTOCOL_MD5+","+ITEM_AUTHPROTOCOL_SHA),"isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+"))"),
			"snmpv3_authpassphrase",	array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+"))"),
			"snmpv3_privprotocol",		array(T_RDA_INT, O_OPT, null,	IN(ITEM_PRIVPROTOCOL_DES+","+ITEM_PRIVPROTOCOL_AES), "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"))"),
			"snmpv3_privpassphrase",	array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"))"),
			"ipmi_sensor",						array(T_RDA_STR, O_OPT, NO_TRIM,	NOT_EMPTY, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_IPMI+"))", _("IPMI sensor")),
			"trapper_hosts",					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({type})&&({type}==2)"),
			"units",									array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({value_type})&&"+IN("0,3","value_type")+"(isset({data_type})&&({data_type}!="+ITEM_DATA_TYPE_BOOLEAN+"))"),
			"multiplier",							array(T_RDA_INT, O_OPT, null,	null,		null),
			"delta",									array(T_RDA_INT, O_OPT, null,	IN("0,1,2"), "isset({save})&&isset({value_type})&&"+IN("0,3","value_type")+"(isset({data_type})&&({data_type}!="+ITEM_DATA_TYPE_BOOLEAN+"))"),
			"formula",								array(T_RDA_DBL, O_OPT, null,	NOT_ZERO, "isset({save})&&isset({multiplier})&&({multiplier}==1)&&"+IN("0,3","value_type"), _("Custom multiplier")),
			"logtimefmt",						array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&(isset({value_type})&&({value_type}==2))"),
			"group_itemid",					array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"new_application",				array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"applications",						array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"history",								array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("History storage period")),
			"trends",								array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})&&isset({value_type})&&"+IN(ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64, "value_type"), _("Trend storage period")),
			"add_delay_flex",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			// actions
			"go",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",						array(T_RDA_INT, O_OPT, null,	null,		null),
			// filter
			"filter_set",							array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// ajax
			"favobj",								array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",								array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
			"favstate",							array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&(\"filter\"=={favobj})"),
			"item_filter", 						array(T_RDA_STR, O_OPT, P_SYS,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
		Nest.value(_REQUEST,"params").$(get_request(paramsFieldName, ""));
		unset(_REQUEST,paramsFieldName);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// permissions
		if (!empty(get_request("parent_discoveryid", null))) {
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setItemIds(Nest.value(_REQUEST,"parent_discoveryid").asLong());
			droptions.setOutput(API_OUTPUT_EXTEND);
			droptions.setEditable(true);
			CArray<Map> discovery_rules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
			discovery_rule = reset(discovery_rules);
			if (empty(discovery_rule)) {
				access_deny();
			}
			Nest.value(_REQUEST,"hostid").$(Nest.value(discovery_rule,"hostid").$());

			if (isset(_REQUEST,"itemid")) {
				CItemPrototypeGet ipoptions = new CItemPrototypeGet();
				ipoptions.setItemIds(Nest.value(_REQUEST,"itemid").asLong());
				ipoptions.setOutput(new String[]{"itemid"});
				ipoptions.setEditable(true);
				ipoptions.setPreserveKeys(true);
				CArray<Map> itemPrototype = API.ItemPrototype(getIdentityBean(), executor).get(ipoptions);
				if (empty(itemPrototype)) {
					access_deny();
				}
			}
		} else {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.host_discovery.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}
		if (PAGE_TYPE_JS == Nest.value(page,"type").asInteger() || PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger()) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
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
		} else if (hasRequest("delete") && hasRequest("itemid")) {
			DBstart(executor);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.ItemPrototype(getIdentityBean(), executor).delete(Nest.as(get_request("itemid")).asLong()));
				}
			});
			result = DBend(executor, result);
			
			show_messages(result, _("Item prototype deleted"), _("Cannot delete item prototype"));
			unset(_REQUEST,"itemid");
			unset(_REQUEST,"form");
			clearCookies(result, get_request("parent_discoveryid"));
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"itemid")) {
			unset(_REQUEST,"itemid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (hasRequest("save")) {
			CArray<Map> delay_flex = get_request("delay_flex", array());
			String db_delay_flex = "";
			for(Map value : delay_flex) {
				db_delay_flex += Nest.value(value,"delay").$()+"/"+Nest.value(value,"period").$()+";";
			}
			db_delay_flex = trim(db_delay_flex, ';');

			DBstart(executor);
			CArray<String> applications = get_request("applications", array());
			String application = reset(applications);
			if (empty(application)) {
				array_shift(applications);
			}

			if (!rda_empty(Nest.value(_REQUEST,"new_application").$())) {
				CArray<Long[]> new_appids = API.Application(getIdentityBean(), executor).create(array((Map)map(
					"name" , Nest.value(_REQUEST,"new_application").$(),
					"hostid", Nest.value(_REQUEST,"hostid").$()
				)));
				if (!empty(new_appids)) {
					Long new_appid = reset(new_appids.get("applicationids"));
					Nest.value(applications,new_appid).$(new_appid);
				}
			}

			final Map item = map(
				"name", 								get_request("name"),
				"description", 						get_request("description"),
				"key_", 									get_request("key"),
				"hostid", 								get_request("hostid"),
				"interfaceid", 						get_request("interfaceid"),
				"delay", 								get_request("delay"),
				"status", 								get_request("status", ITEM_STATUS_DISABLED),
				"type", 									get_request("type"),
				"snmp_community", 			get_request("snmp_community"),
				"snmp_oid", 							get_request("snmp_oid"),
				"value_type", 						get_request("value_type"),
				"trapper_hosts", 					get_request("trapper_hosts"),
				"port", 									get_request("port"),
				"history", 								get_request("history"),
				"trends", 								get_request("trends"),
				"units", 								get_request("units"),
				"multiplier", 							get_request("multiplier", 0),
				"delta", 								get_request("delta"),
				"snmpv3_contextname" , 		get_request("snmpv3_contextname"),
				"snmpv3_securityname" , 	get_request("snmpv3_securityname"),
				"snmpv3_securitylevel" , 		get_request("snmpv3_securitylevel"),
				"snmpv3_authprotocol" ,		get_request("snmpv3_authprotocol"),
				"snmpv3_authpassphrase" , 	get_request("snmpv3_authpassphrase"),
				"snmpv3_privprotocol" , 		get_request("snmpv3_privprotocol"),
				"snmpv3_privpassphrase" , 	get_request("snmpv3_privpassphrase"),
				"formula", 							get_request("formula"),
				"logtimefmt", 						get_request("logtimefmt"),
				"valuemapid", 						get_request("valuemapid"),
				"authtype", 							get_request("authtype"),
				"username", 							get_request("username"),
				"password", 							get_request("password"),
				"publickey", 							get_request("publickey"),
				"privatekey", 						get_request("privatekey"),
				"params", 							get_request("params"),
				"ipmi_sensor", 						get_request("ipmi_sensor"),
				"data_type", 							get_request("data_type"),
				"ruleid", 								get_request("parent_discoveryid"),
				"delay_flex", 							db_delay_flex,
				"applications", 						applications
			);

			boolean result;
			if (hasRequest("itemid")) {
				Long itemId = get_request_asLong("itemid");

				Map dbItem = get_item_by_itemid_limited(executor, itemId);
				Nest.value(dbItem,"applications").$(get_applications_by_itemid(executor, itemId));

				// unset snmpv3 fields
				if (Nest.value(item,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV) {
					Nest.value(item,"snmpv3_authprotocol").$(ITEM_AUTHPROTOCOL_MD5);
					Nest.value(item,"snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
				} else if (Nest.value(item,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV) {
					Nest.value(item,"snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
				}

				final Map fitem = CArrayHelper.unsetEqualValues(item, dbItem);
				Nest.value(item,"itemid").$(itemId);

				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.ItemPrototype(getIdentityBean(), executor).update(array(fitem)));
					}
				});
				show_messages(result, _("Item prototype updated"), _("Cannot update item prototype"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.ItemPrototype(getIdentityBean(), executor).create(array(item)));
					}
				});
				show_messages(result, _("Item prototype added"), _("Cannot add item prototype"));
			}

			result = DBend(executor, result);
			if (result) {
				unset(_REQUEST,"itemid");
				unset(_REQUEST,"form");
				clearCookies(result, get_request("parent_discoveryid"));
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_itemid")) {
			final Long[] groupItemId = get_requests("group_itemid").valuesAsLong();
			final boolean enable = ("activate".equals(get_request("go")));
			
			DBstart(executor);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return enable ? activate_item(getIdentityBean(), executor, groupItemId) : disable_item(getIdentityBean(), executor, groupItemId);
				}
			});
			result = DBend(executor, result);
			
			int updated = count(groupItemId);

			String messageSuccess = enable
				? _n("Item prototype enabled", "Item prototypes enabled", updated)
				: _n("Item prototype disabled", "Item prototypes disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable item prototype", "Cannot enable item prototypes", updated)
				: _n("Cannot disable item prototype", "Cannot disable item prototypes", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("parent_discoveryid"));
		} else if ("delete".equals(get_request("go")) && hasRequest("group_itemid")) {
			DBstart(executor);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.ItemPrototype(getIdentityBean(), executor).delete(get_request("group_itemid",array()).valuesAsLong()));
				}
			});
			result = DBend(executor, result);
			
			show_messages(result, _("Item prototypes deleted"), _("Cannot delete item prototypes"));
			clearCookies(result, get_request("parent_discoveryid"));
		}

		/* Display */
		Map data;
		if (isset(_REQUEST,"form")) {
			data = getItemFormData(this.getIdentityBean(), executor);
			Nest.value(data,"page_header").$(_("CONFIGURATION OF ITEM PROTOTYPES"));
			Nest.value(data,"is_item_prototype").$(true);

			// render view
			CView itemView = new CView("configuration.item.edit", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		} else {
			data = map(
				"form", get_request("form", null),
				"parent_discoveryid", get_request("parent_discoveryid", null),
				"hostid", get_request("hostid", null),
				"discovery_rule", discovery_rule
			);

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			String sortfield = getPageSortField(getIdentityBean(), executor, "name");

			CItemPrototypeGet ipoptions = new CItemPrototypeGet();
			ipoptions.setDiscoveryIds(Nest.value(data,"parent_discoveryid").asLong());
			ipoptions.setOutput(API_OUTPUT_EXTEND);
			ipoptions.setEditable(true);
			ipoptions.setSelectApplications(API_OUTPUT_EXTEND);
			ipoptions.setSortfield(sortfield);
			ipoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> items = API.ItemPrototype(getIdentityBean(), executor).get(ipoptions);
			items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);
			Nest.value(data,"items").$(items);

			order_result(items, sortfield, getPageSortOrder(getIdentityBean(), executor));

			Nest.value(data,"paging").$(getPagingLine(
				getIdentityBean(), 
				executor,
				items,
				array("itemid"),
				map(
					"hostid", get_request("hostid"),
					"parent_discoveryid", get_request("parent_discoveryid")
				)
			));

			// render view
			CView itemView = new CView("configuration.item.prototype.list", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		}
	}

}
