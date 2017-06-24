package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
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
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NO_TRIM;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
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
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ItemsUtil.activate_item;
import static com.isoft.iradar.inc.ItemsUtil.disable_item;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldLabelByType;
import static com.isoft.iradar.inc.ItemsUtil.getParamFieldNameByType;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid_limited;
import static com.isoft.iradar.inc.ItemsUtil.orderItemsByStatus;
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
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostDiscoveryAction extends RadarBaseAction {
	
	private Map host;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of discovery rules"));
		page("file", "host_discovery.action");
		page("scripts", new String[] { "class.cviewswitcher.js" });
		page("hist_arg", new String[] { "hostid" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		String paramsFieldName = getParamFieldNameByType(get_request("type", 0));
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hostid",								array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"!isset({form})"),
			"itemid",								array(T_RDA_INT, O_NO,	P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"interfaceid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID, null, _("Interface")),
			"name",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Name")),
			"description",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"filter_macro",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"filter_value",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"key",									array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Key")),
			"delay",									array(T_RDA_INT, O_OPT, null, BETWEEN(0, SEC_PER_DAY), "isset({save})&&(isset({type})&&({type}!="+ITEM_TYPE_TRAPPER+"&&{type}!="+ITEM_TYPE_SNMPTRAP+"))",_("Update interval (in sec)")),
			"delay_flex",							array(T_RDA_STR, O_OPT, null,	"",			null),
			"add_delay_flex",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"new_delay_flex",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({add_delay_flex})&&(isset({type})&&({type}!=2))", _("New flexible interval")),
			"status",								array(T_RDA_INT, O_OPT, null,	IN(ITEM_STATUS_ACTIVE), null),
			"type",									array(T_RDA_INT, O_OPT, null, IN(array(-1, ITEM_TYPE_IRADAR, ITEM_TYPE_SNMPV1, ITEM_TYPE_TRAPPER, ITEM_TYPE_SIMPLE, ITEM_TYPE_SNMPV2C, ITEM_TYPE_INTERNAL, ITEM_TYPE_SNMPV3, ITEM_TYPE_IRADAR_ACTIVE, ITEM_TYPE_EXTERNAL, ITEM_TYPE_DB_MONITOR, ITEM_TYPE_IPMI, ITEM_TYPE_SSH, ITEM_TYPE_TELNET, ITEM_TYPE_JMX)), "isset({save})"),
			"authtype",							array(T_RDA_INT, O_OPT, null,	IN(ITEM_AUTHTYPE_PASSWORD+","+ITEM_AUTHTYPE_PUBLICKEY), "isset({save})&&isset({type})&&({type}=="+ITEM_TYPE_SSH+")"),
			"username",							array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET, "type"), _("User name")),
			"password",							array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SSH+","+ITEM_TYPE_TELNET+ "type")),
			"publickey",							array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&({type})=="+ITEM_TYPE_SSH+"&&({authtype})=="+ITEM_AUTHTYPE_PUBLICKEY),
			"privatekey",							array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&isset({type})&&({type})=="+ITEM_TYPE_SSH+"&&({authtype})=="+ITEM_AUTHTYPE_PUBLICKEY),
			paramsFieldName,				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})&&isset({type})&&"+ IN(ITEM_TYPE_SSH+","+ITEM_TYPE_DB_MONITOR+","+ITEM_TYPE_TELNET+","+ITEM_TYPE_CALCULATED, "type"), getParamFieldLabelByType(get_request("type", 0))),
			"snmp_community",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C,"type"), _("SNMP community")),
			"snmp_oid",							array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3,"type"), _("SNMP OID")),
			"port",									array(T_RDA_STR, O_OPT, null,	BETWEEN(0, 65535), "isset({save})&&isset({type})&&"+IN(ITEM_TYPE_SNMPV1+","+ITEM_TYPE_SNMPV2C+","+ITEM_TYPE_SNMPV3,"type"), _("Port")),
			"snmpv3_contextname", 		array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_securitylevel", 		array(T_RDA_INT, O_OPT, null,	IN("0,1,2"), "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_securityname", 		array(T_RDA_STR, O_OPT, null,	null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+"))"),
			"snmpv3_authprotocol", 		array(T_RDA_INT, O_OPT, null, IN(ITEM_AUTHPROTOCOL_MD5+","+ITEM_AUTHPROTOCOL_SHA), "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+"))"),
			"snmpv3_authpassphrase", 	array(T_RDA_STR, O_OPT, null, null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"||{snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV+"))"),
			"snmpv3_privprotocol", 		array(T_RDA_INT, O_OPT, null, IN(ITEM_PRIVPROTOCOL_DES+","+ITEM_PRIVPROTOCOL_AES), "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"))"),
			"snmpv3_privpassphrase", 	array(T_RDA_STR, O_OPT, null, null, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_SNMPV3+")&&({snmpv3_securitylevel}=="+ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV+"))"),
			"ipmi_sensor",						array(T_RDA_STR, O_OPT, NO_TRIM,	NOT_EMPTY, "isset({save})&&(isset({type})&&({type}=="+ITEM_TYPE_IPMI+"))", _("IPMI sensor")),
			"trapper_hosts",					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({type})&&({type}==2)"),
			"lifetime", 							array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			// actions
			"go",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"g_hostdruleid",					array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"save",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"update",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",						array(T_RDA_INT, O_OPT, null,	null,		null),
			// ajax
			"favobj",								array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",								array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
			"favstate",							array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&(\"filter\"=={favobj})")
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
		Nest.value(_REQUEST,"params").$(get_request(paramsFieldName, ""));
		unset(_REQUEST,paramsFieldName);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions*/
		if (!empty(get_request("itemid"))) {
			CDiscoveryRuleGet option = new CDiscoveryRuleGet();
			option.setItemIds(Nest.value(_REQUEST,"itemid").asLong());
			option.setOutput(API_OUTPUT_EXTEND);
			option.setSelectHosts(new String[]{"status", "flags"});
			option.setEditable(true);
			CArray<Map> items = API.DiscoveryRule(getIdentityBean(), executor).get(option);
			Map item = reset(items);
			if (empty(item)) {
				access_deny();
			}
			Nest.value(_REQUEST,"hostid").$(Nest.value(item,"hostid").$());
			host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
		} else {
			CHostGet option = new CHostGet();
			option.setHostIds( Nest.value(_REQUEST,"hostid").asLong());
			option.setOutput(new String[]{"status", "flags"});
			option.setTemplatedHosts(true);
			option.setEditable(true);
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(option);
			host = reset(hosts);
			if (empty(host)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/** Ajax*/
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor,"web.host_discovery.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Actions*/
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
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"itemid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.DiscoveryRule(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"itemid").asLong()));
				}
			});

			show_messages(result, _("Discovery rule deleted"), _("Cannot delete discovery rule"));
			unset(_REQUEST,"itemid");
			unset(_REQUEST,"form");
			clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"itemid")) {
			unset(_REQUEST,"itemid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			CArray<Map> delay_flex = get_request("delay_flex", array());

			String db_delay_flex = "";
			for(Map val : delay_flex) {
				db_delay_flex += Nest.value(val,"delay").asString()+"/"+Nest.value(val,"period").asString()+";";
			}
			db_delay_flex = trim(db_delay_flex, ';');

			String ifm = get_request("filter_macro");
			String ifv = get_request("filter_value");
			String filter = isset(ifm)&&isset(ifv) ? ifm+":"+ifv : "";

			final Map item = map(
				"interfaceid", get_request("interfaceid"),
				"name", get_request("name"),
				"description", get_request("description"),
				"key_", get_request("key"),
				"hostid", get_request("hostid"),
				"delay", get_request("delay"),
				"status", get_request("status", ITEM_STATUS_DISABLED),
				"type", get_request("type"),
				"snmp_community", get_request("snmp_community"),
				"snmp_oid", get_request("snmp_oid"),
				"trapper_hosts", get_request("trapper_hosts"),
				"port", get_request("port"),
				"snmpv3_contextname", get_request("snmpv3_contextname"),
				"snmpv3_securityname", get_request("snmpv3_securityname"),
				"snmpv3_securitylevel", get_request("snmpv3_securitylevel"),
				"snmpv3_authprotocol", get_request("snmpv3_authprotocol"),
				"snmpv3_authpassphrase", get_request("snmpv3_authpassphrase"),
				"snmpv3_privprotocol", get_request("snmpv3_privprotocol"),
				"snmpv3_privpassphrase", get_request("snmpv3_privpassphrase"),
				"delay_flex", db_delay_flex,
				"authtype", get_request("authtype"),
				"username", get_request("username"),
				"password", get_request("password"),
				"publickey", get_request("publickey"),
				"privatekey", get_request("privatekey"),
				"params", get_request("params"),
				"ipmi_sensor", get_request("ipmi_sensor"),
				"lifetime", get_request("lifetime"),
				"filter", filter
			);

			boolean result;
			if (hasRequest("itemid")) {
				Long itemId = get_request_asLong("itemid");
				
				DBstart(executor);
				
				Map dbItem = get_item_by_itemid_limited(executor, itemId);

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
						return !empty(API.DiscoveryRule(getIdentityBean(), executor).update(array(fitem)));
					}
				});
				
				result = DBend(executor, result);
				show_messages(result, _("Discovery rule updated"), _("Cannot update discovery rule"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.DiscoveryRule(getIdentityBean(), executor).create(array(item)));
					}
				});
				show_messages(result, _("Discovery rule created"), _("Cannot add discovery rule"));
			}

			if (result) {
				unset(_REQUEST,"itemid");
				unset(_REQUEST,"form");
				clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_hostdruleid")) {
			final Long[] groupHostDiscoveryRuleId = get_requests("g_hostdruleid").valuesAsLong();
			final boolean enable = ("activate".equals(get_request("go")));

			DBstart(executor);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return enable ? activate_item(getIdentityBean(), executor,groupHostDiscoveryRuleId) : disable_item(getIdentityBean(), executor,groupHostDiscoveryRuleId);
				}
			});
			result = DBend(executor, result);
			
			int updated = count(groupHostDiscoveryRuleId);

			String messageSuccess = enable
				? _n("Discovery rule enabled", "Discovery rules enabled", updated)
				: _n("Discovery rule disabled", "Discovery rules disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable discovery rules", "Cannot enable discovery rules", updated)
				: _n("Cannot disable discovery rules", "Cannot disable discovery rules", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_hostdruleid")) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.DiscoveryRule(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"g_hostdruleid").asLong()));
				}
			});
			show_messages(goResult, _("Discovery rules deleted"), _("Cannot delete discovery rules"));
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			Map data = getItemFormData(this.getIdentityBean(), executor, map("is_discovery_rule", true));
			Nest.value(data,"page_header").$(_("CONFIGURATION OF DISCOVERY RULES"));

			// render view
			CView itemView = new CView("configuration.item.edit", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		} else {
			Map data = map(
				"hostid", get_request("hostid", 0),
				"host", host,
				"showErrorColumn", (Nest.value(host,"status").asInteger() != HOST_STATUS_TEMPLATE)
			);
			
			Map<String, Object> config = select_config(getIdentityBean(), executor);

			String sortfield = getPageSortField(getIdentityBean(), executor, "name");

			// discoveries
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setHostIds(Nest.value(data,"hostid").asLong());
			droptions.setOutput(API_OUTPUT_EXTEND);
			droptions.setEditable(true);
			droptions.setSelectItems(API_OUTPUT_COUNT);
			droptions.setSelectGraphs(API_OUTPUT_COUNT);
			droptions.setSelectTriggers(API_OUTPUT_COUNT);
			droptions.setSelectHostPrototypes(API_OUTPUT_COUNT);
			droptions.setSortfield(sortfield);
			droptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> discoveries = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
			Nest.value(data,"discoveries").$(discoveries);

			discoveries = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, discoveries);
			Nest.value(data,"discoveries").$(discoveries);

			if ("status".equals(sortfield)) {
				orderItemsByStatus(discoveries, getPageSortOrder(getIdentityBean(), executor));
			} else {
				order_result(discoveries, sortfield, getPageSortOrder(getIdentityBean(), executor));
			}

			// paging
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, discoveries, array("itemid"), map("hostid", get_request("hostid"))));

			// render view
			CView discoveryView = new CView("configuration.host.discovery.list", data);
			discoveryView.render(getIdentityBean(), executor);
			discoveryView.show();
		}
	}
}
