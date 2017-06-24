package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_reverse;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.bindec;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.range;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.IM_ESTABLISHED;
import static com.isoft.iradar.inc.Defines.IM_TREE;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_DECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NORMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NOTSUPPORTED;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.PERM_RES_DATA_ARRAY;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_INTERVAL;
import static com.isoft.iradar.inc.Defines.RDA_ITEM_DELAY_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_FILTER_SIZE;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_real;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_num2bitstr;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_str_revert;
import static com.isoft.iradar.inc.FuncsUtil.rda_subarray_push;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HostsUtil.isTemplate;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.ItemsUtil.get_applications_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.itemState;
import static com.isoft.iradar.inc.ItemsUtil.itemValueTypeString;
import static com.isoft.iradar.inc.ItemsUtil.item_data_type2str;
import static com.isoft.iradar.inc.ItemsUtil.item_status2str;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.PermUtil.getGroupAuthenticationType;
import static com.isoft.iradar.inc.PermUtil.getUserAuthenticationType;
import static com.isoft.iradar.inc.PermUtil.get_accessible_groups_by_rights;
import static com.isoft.iradar.inc.PermUtil.get_accessible_hosts_by_rights;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.SoundsUtil.getMessageSettings;
import static com.isoft.iradar.inc.TriggersUtil.analyzeExpression;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.remakeExpression;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.utils.CJs;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class FormsUtil {

	private FormsUtil() {
	}
	
	public static Map getUserFormData(IIdentityBean idBean, SQLExecutor executor, String userid) {
		return getUserFormData(idBean, executor, userid, false);
	}
	
	public static Map getUserFormData(IIdentityBean idBean, SQLExecutor executor, String userid, boolean isProfile) {
		Map<String, Object> config = select_config(idBean, executor);
		CArray data = map("is_profile", isProfile);

		Map user = null;
		if (isset(userid)) {
			CUserGet options = new CUserGet();
			options.setUserIds(userid);
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> users = API.User(idBean, executor).get(options);
			user = reset(users);
		}

		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		if (isset(userid) && (!isset(_REQUEST,"form_refresh") || isset(_REQUEST,"register"))) {
			Nest.value(data,"alias").$(Nest.value(user,"alias").$());
			Nest.value(data,"name").$(Nest.value(user,"name").$());
			Nest.value(data,"surname").$(Nest.value(user,"surname").$());
			Nest.value(data,"password1").$(null);
			Nest.value(data,"password2").$(null);
			Nest.value(data,"url").$(Nest.value(user,"url").$());
			Nest.value(data,"autologin").$(Nest.value(user,"autologin").$());
			Nest.value(data,"autologout").$(Nest.value(user,"autologout").$());
			Nest.value(data,"lang").$(Nest.value(user,"lang").$());
			Nest.value(data,"theme").$(Nest.value(user,"theme").$());
			Nest.value(data,"refresh").$(Nest.value(user,"refresh").$());
			Nest.value(data,"rows_per_page").$(Nest.value(user,"rows_per_page").$());
			Nest.value(data,"user_type").$(Nest.value(user,"type").$());
			Nest.value(data,"messages").$(getMessageSettings(idBean, executor));

			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUserIds(userid);
			ugoptions.setOutput(new String[]{"usrgrpid"});
			CArray<Map> userGroups = API.UserGroup(idBean, executor).get(ugoptions);
			Nest.value(data,"user_groups").$(rda_toHash(rda_objectValues(userGroups, "usrgrpid")));

			Nest.value(data,"user_medias").$(array());
			
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbMedia = DBselect(executor,
					"SELECT m.mediaid,m.mediatypeid,m.period,m.sendto,m.severity,m.active"+
					" FROM media m"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "media", "m")+
					    " AND m.userid="+sqlParts.marshalParam(userid),
					sqlParts.getNamedParams()
			);
			for(Map dbMedium : dbMedia) {
				Nest.value(data,"user_medias").asCArray().add(dbMedium);
			}

			if (Nest.value(data,"autologout").asInteger() > 0) {
				Nest.value(_REQUEST,"autologout").$(Nest.value(data,"autologout").$());
			}
		} else {
			Nest.value(data,"alias").$(get_request("alias", ""));
			Nest.value(data,"name").$(get_request("name", ""));
			Nest.value(data,"surname").$(get_request("surname", ""));
			Nest.value(data,"password1").$(get_request("password1", ""));
			Nest.value(data,"password2").$(get_request("password2", ""));
			Nest.value(data,"url").$(get_request("url", ""));
			Nest.value(data,"autologin").$(get_request("autologin", 0));
			Nest.value(data,"autologout").$(get_request("autologout", 900));
			Nest.value(data,"lang").$(get_request("lang", "en_gb"));
			Nest.value(data,"theme").$(get_request("theme", THEME_DEFAULT));
			Nest.value(data,"refresh").$(get_request("refresh", 30));
			Nest.value(data,"rows_per_page").$(get_request("rows_per_page", 50));
			Nest.value(data,"user_type").$(get_request("user_type", USER_TYPE_IRADAR_USER));
			Nest.value(data,"user_groups").$(get_request("user_groups", array()));
			Nest.value(data,"change_password").$(get_request("change_password", null));
			Nest.value(data,"user_medias").$(get_request("user_medias", array()));

			// set messages
			Nest.value(data,"messages").$(get_request("messages", array()));
			if (!isset(Nest.value(data,"messages","enabled").$())) {
				Nest.value(data,"messages","enabled").$(0);
			}
			if (!isset(Nest.value(data,"messages","sounds.recovery").$())) {
				Nest.value(data,"messages","sounds.recovery").$("alarm_ok.wav");
			}
			if (!isset(Nest.value(data,"messages","triggers.recovery").$())) {
				Nest.value(data,"messages","triggers.recovery").$(0);
			}
			if (!isset(Nest.value(data,"messages","triggers.severities").$())) {
				Nest.value(data,"messages","triggers.severities").$(array());
			}
			Nest.value(data,"messages").$(array_merge(getMessageSettings(idBean, executor), Nest.value(data,"messages").asCArray()));
		}

		// authentication type
		if (!empty(Nest.value(data,"user_groups").$())) {
			Nest.value(data,"auth_type").$(getGroupAuthenticationType(idBean, executor,Nest.array(data,"user_groups").asLong(), GROUP_GUI_ACCESS_INTERNAL));
		} else {
			Nest.value(data,"auth_type").$((userid == null)
				? Nest.value(config,"authentication_type").$()
				: getUserAuthenticationType(idBean, executor, userid, GROUP_GUI_ACCESS_INTERNAL));
		}

		// set autologout
		if (Nest.value(data,"autologin").asBoolean() || !isset(data,"autologout")) {
			Nest.value(data,"autologout").$(0);
		}

		// set media types
		if (!empty(Nest.value(data,"user_medias").$())) {
			CArray mediaTypeDescriptions = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbMediaTypes = DBselect(executor,
				"SELECT mt.mediatypeid,mt.description FROM media_type mt"+
					" WHERE mt.tenantid='-'"+
					    " AND "+sqlParts.dual.dbConditionLong("mt.mediatypeid", rda_objectValues(Nest.value(data,"user_medias").$(), "mediatypeid").valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map dbMediaType : dbMediaTypes) {
				Nest.value(mediaTypeDescriptions,dbMediaType.get("mediatypeid")).$(Nest.value(dbMediaType,"description").$());
			}

			for(Map media : (CArray<Map>)Nest.value(data,"user_medias").asCArray()) {
				Nest.value(media,"description").$(Nest.value(mediaTypeDescriptions,media.get("mediatypeid")).$());
			}

			CArrayHelper.sort(Nest.value(data,"user_medias").asCArray(), array("description", "sendto"));
		}

		// set user rights
		if (!Nest.value(data,"is_profile").asBoolean()) {
			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUsrgrpIds(Nest.array(data,"user_groups").asLong());
			ugoptions.setOutput(new String[]{"usrgrpid", "name"});
			CArray<Map> groups = API.UserGroup(idBean, executor).get(ugoptions);
			Nest.value(data,"groups").$(groups);
			order_result(groups, "name");

			Long[] group_ids = Nest.array(data,"user_groups").asLong();
			if (count(group_ids) == 0) {
				group_ids = new Long[] { -1L };
			}
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> db_rights = DBselect(executor,
					"SELECT r.* FROM rights r "+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "rights", "r")+
					" AND "+sqlParts.dual.dbConditionInt("r.groupid", group_ids),
					sqlParts.getNamedParams());

			// deny beat all, read-write beat read
			CArray<Integer> tmp_permitions = array();
			for(Map db_right : db_rights) {
				if (isset(tmp_permitions,db_right.get("id")) && Nest.value(tmp_permitions,db_right.get("id")).asInteger() != PERM_DENY) {
					Nest.value(tmp_permitions,db_right.get("id")).$((Nest.value(db_right,"permission").asInteger() == PERM_DENY)
						? PERM_DENY
						: max(Nest.value(tmp_permitions,db_right.get("id")).asInteger(), Nest.value(db_right,"permission").asInteger()));
				} else {
					Nest.value(tmp_permitions,db_right.get("id")).$(Nest.value(db_right,"permission").asInteger());
				}
			}

			Nest.value(data,"user_rights").$(array());
			for (Entry<Object, Integer> e : tmp_permitions.entrySet()) {
			    Object id = e.getKey();
			    Integer permition = e.getValue();
				array_push(Nest.value(data,"user_rights").asCArray(), map("id", id, "permission", permition));
			}
		}
		return data;
	}
	
	public static CFormList getPermissionsFormList(IIdentityBean idBean, SQLExecutor executor) {
		return getPermissionsFormList(idBean, executor, array());
	}
	
	public static CFormList getPermissionsFormList(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights) {
		return getPermissionsFormList(idBean, executor, rights, USER_TYPE_IRADAR_USER);
	}
	
	public static CFormList getPermissionsFormList(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, int user_type) {
		return getPermissionsFormList(idBean, executor, rights, user_type, null);
	}

	public static CFormList getPermissionsFormList(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, Integer user_type, CFormList rightsFormList) {
		if(user_type == null){
			user_type = USER_TYPE_IRADAR_USER;
		}
		CArray<Map> lists = array();
		// group
		Nest.value(lists,"group","label").$(_("Host groups"));
		Nest.value(lists,"group","read_write").$(new CListBox("groups_write", null, 15));
		Nest.value(lists,"group","read_only").$(new CListBox("groups_read", null, 15));
		Nest.value(lists,"group","deny").$(new CListBox("groups_deny", null, 15));

		CArray<Map> groups = get_accessible_groups_by_rights(idBean, executor, rights, user_type, PERM_DENY, PERM_RES_DATA_ARRAY);
		String list_name = null;
		for(Map group : groups ) {
			switch(Nest.value(group,"permission").asInteger()) {
				case PERM_READ:
					list_name = "read_only";
					break;
				case PERM_READ_WRITE:
					list_name = "read_write";
					break;
				default:
					list_name = "deny";
			}
			((CListBox)Nest.value(lists,"group",list_name).$()).addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}

		// host
		Nest.value(lists,"host","label").$(_("Hosts"));
		Nest.value(lists,"host","read_write").$(new CListBox("hosts_write", null, 15));
		Nest.value(lists,"host","read_only").$(new CListBox("hosts_read", null, 15));
		Nest.value(lists,"host","deny").$(new CListBox("hosts_deny", null, 15));

		CArray<Map> hosts = get_accessible_hosts_by_rights(idBean, executor, rights, user_type, PERM_DENY, PERM_RES_DATA_ARRAY);
		for(Map host : hosts) {
			switch(Nest.value(host,"permission").asInteger()) {
				case PERM_READ:
					list_name = "read_only";
					break;
				case PERM_READ_WRITE:
					list_name = "read_write";
					break;
				default:
					list_name = "deny";
			}
			if (HOST_STATUS_PROXY_ACTIVE == Nest.value(host,"status").asInteger() || HOST_STATUS_PROXY_PASSIVE == Nest.value(host,"status").asInteger()) {
				Nest.value(host,"host_name").$(Nest.value(host,"host").$());
			}
			((CListBox)Nest.value(lists,"host",list_name).$()).addItem(Nest.value(host,"hostid").$(), Nest.value(host,"host_name").asString());
		}

		// display
		if (empty(rightsFormList)) {
			rightsFormList = new CFormList("rightsFormList");
		}
		boolean isHeaderDisplayed = false;
		for(Map<Object,Object> list : lists) {
			String sLabel = "";
			CRow row = new CRow();
			for (Entry<Object, Object> e : list.entrySet()) {
			    Object classStyle = e.getKey();
			    Object item = e.getValue();
				if (is_string(item)) {
					sLabel = Nest.as(item).asString();
				} else {
					row.addItem(new CCol(item, classStyle));
				}
			}

			CTable table = new CTable(_("No accessible resources"), "right_table calculated");
			if (!isHeaderDisplayed) {
				table.setHeader(array(_("Read-write"), _("Read only"), _("Deny")), "header");
				isHeaderDisplayed = true;
			}
			table.addRow(row);
			rightsFormList.addRow(sLabel, table);
		}
		return rightsFormList;
	}
	
	public static CArray prepareSubfilterOutput(CArray<Map> data, CArray<String> subfilter, String subfilterName) {
		order_result(data, "name");

		CArray output = array();
		CSpan span = null;
		CSpan nspan  = null;
		for (Entry<Object, Map> e : data.entrySet()) {
		    String id = Nest.as(e.getKey()).asString();
		    Map element = e.getValue();
			Nest.value(element,"name").$(nbsp(CHtml.encode(Nest.value(element,"name").asString())));

			// is activated
			if (str_in_array(id, subfilter)) {
				span = new CSpan(Nest.value(element,"name").$()+SPACE+"("+Nest.value(element,"count").$()+")", "subfilter_enabled");
				span.onClick(CHtml.encode(
					"javascript: create_var(\"rda_filter\", \"subfilter_set\", \"1\", false);"+
					"create_var(\"rda_filter\", "+CJs.encodeJson(subfilterName+"["+id+"]")+", null, true);"
				));
				output.add(span);
			} else {// isn't activated
				// subfilter has 0 items
				if (Nest.value(element,"count").asInteger() == 0) {
					span = new CSpan(Nest.value(element,"name").$()+SPACE+"("+Nest.value(element,"count").$()+")", "subfilter_inactive");
					output.add(span);
				} else {
					// this level has no active subfilters
					nspan = !empty(subfilter)
						? new CSpan(SPACE+"(+"+Nest.value(element,"count").$()+")", "subfilter_active")
						: new CSpan(SPACE+"("+Nest.value(element,"count").$()+")", "subfilter_active");

					span = new CSpan(Nest.value(element,"name").$(), "subfilter_disabled");
					span.onClick(CHtml.encode(
						"javascript: create_var(\"rda_filter\", \"subfilter_set\", \"1\", false);"+
						"create_var(\"rda_filter\", "+
							CJs.encodeJson(subfilterName+"["+id+"]")+", "+
							CJs.encodeJson(id)+", "+
							"true"+
						");"
					));

					output.add(span);
					output.add(nspan);
				}
			}
			output.add(", ");
		}

		array_pop(output);
		return output;
	}
	
	public static CForm getItemFilterForm(IIdentityBean idBean, SQLExecutor executor, CArray<Map> items) {
		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		Object filter_groupId = Nest.value(_REQUEST,"filter_groupid").$();
		Object filter_hostId	= Nest.value(_REQUEST,"filter_hostid").$();
		Object filter_application = Nest.value(_REQUEST,"filter_application").$();
		Object filter_name = Nest.value(_REQUEST,"filter_name").$();
		Object filter_type = Nest.value(_REQUEST,"filter_type").$();
		Object filter_key = Nest.value(_REQUEST,"filter_key").$();
		Object filter_snmp_community = Nest.value(_REQUEST,"filter_snmp_community").$();
		Object filter_snmpv3_securityname = Nest.value(_REQUEST,"filter_snmpv3_securityname").$();
		Object filter_snmp_oid = Nest.value(_REQUEST,"filter_snmp_oid").$();
		Object filter_port = Nest.value(_REQUEST,"filter_port").$();
		Object filter_value_type = Nest.value(_REQUEST,"filter_value_type").$();
		Object filter_data_type = Nest.value(_REQUEST,"filter_data_type").$();
		Object filter_delay = Nest.value(_REQUEST,"filter_delay").$();
		Object filter_history = Nest.value(_REQUEST,"filter_history").$();
		Object filter_trends = Nest.value(_REQUEST,"filter_trends").$();
		Object filter_status = Nest.value(_REQUEST,"filter_status").$();
		Object filter_state = Nest.value(_REQUEST,"filter_state").$();
		Object filter_templated_items = Nest.value(_REQUEST,"filter_templated_items").$();
		Object filter_with_triggers = Nest.value(_REQUEST,"filter_with_triggers").$();
		Object subfilter_hosts = Nest.value(_REQUEST,"subfilter_hosts").$();
		Object subfilter_apps = Nest.value(_REQUEST,"subfilter_apps").$();
		Object subfilter_types = Nest.value(_REQUEST,"subfilter_types").$();
		Object subfilter_value_types = Nest.value(_REQUEST,"subfilter_value_types").$();
		Object subfilter_status = Nest.value(_REQUEST,"subfilter_status").$();
		Object subfilter_state = Nest.value(_REQUEST,"subfilter_state").$();
		Object subfilter_templated_items = Nest.value(_REQUEST,"subfilter_templated_items").$();
		Object subfilter_with_triggers	= Nest.value(_REQUEST,"subfilter_with_triggers").$();
		Object subfilter_history = Nest.value(_REQUEST,"subfilter_history").$();
		Object subfilter_trends = Nest.value(_REQUEST,"subfilter_trends").$();
		Object subfilter_interval = Nest.value(_REQUEST,"subfilter_interval").$();

		CForm form = new CForm("get");
		form.setAttribute("name", "rda_filter");
		form.setAttribute("id", "rda_filter");
		form.addVar("subfilter_hosts", subfilter_hosts);
		form.addVar("subfilter_apps", subfilter_apps);
		form.addVar("subfilter_types", subfilter_types);
		form.addVar("subfilter_value_types", subfilter_value_types);
		form.addVar("subfilter_status", subfilter_status);
		form.addVar("subfilter_state", subfilter_state);
		form.addVar("subfilter_templated_items", subfilter_templated_items);
		form.addVar("subfilter_with_triggers", subfilter_with_triggers);
		form.addVar("subfilter_history", subfilter_history);
		form.addVar("subfilter_trends", subfilter_trends);
		form.addVar("subfilter_interval", subfilter_interval);

		// type select
		CArray fTypeVisibility = array();
		CComboBox cmbType = new CComboBox("filter_type", filter_type);
		cmbType.setAttribute("id", "filter_type");
		cmbType.addItem(-1, _("all"));
		for(String vItem : new String[]{"filter_delay_label", "filter_delay"}) {
			rda_subarray_push(fTypeVisibility, -1, vItem);
		}

		CArray<String> itemTypes = item_type2str();
		unset(itemTypes,ITEM_TYPE_HTTPTEST); // httptest items are only for internal iradar logic

		cmbType.addItems(itemTypes);

		for (Entry<Object, String> e : itemTypes.entrySet()) {
		    int typeNum = Nest.as(e.getKey()).asInteger();
		    //String typeLabel = e.getValue();
			if (typeNum != ITEM_TYPE_TRAPPER) {
				rda_subarray_push(fTypeVisibility, typeNum, "filter_delay_label");
				rda_subarray_push(fTypeVisibility, typeNum, "filter_delay");
			}

			switch (typeNum) {
				case ITEM_TYPE_SNMPV1:
				case ITEM_TYPE_SNMPV2C:
					String[] snmp_types = new String[]{
						"filter_snmp_community_label", "filter_snmp_community",
						"filter_snmp_oid_label", "filter_snmp_oid",
						"filter_port_label", "filter_port"
					};
					for(String vItem : snmp_types) {
						rda_subarray_push(fTypeVisibility, typeNum, vItem);
					}
					break;
				case ITEM_TYPE_SNMPV3:
					for(String vItem : new String[]{"filter_snmpv3_securityname_label", "filter_snmpv3_securityname", "filter_snmp_oid_label",
						"filter_snmp_oid", "filter_port_label", "filter_port"}) {
						rda_subarray_push(fTypeVisibility, typeNum, vItem);
					}
					break;
			}
		}

		rda_add_post_js("var filterTypeSwitcher = new CViewSwitcher(\"filter_type\", \"change\", "+rda_jsvalue(fTypeVisibility, true)+");");

		// type of information select
		CArray fVTypeVisibility = array();

		CComboBox cmbValType = new CComboBox("filter_value_type", filter_value_type);
		cmbValType.addItem(-1, _("all"));
		cmbValType.addItem(ITEM_VALUE_TYPE_UINT64, _("Numeric (unsigned)"));
		cmbValType.addItem(ITEM_VALUE_TYPE_FLOAT, _("Numeric (float)"));
		cmbValType.addItem(ITEM_VALUE_TYPE_STR, _("Character"));
		cmbValType.addItem(ITEM_VALUE_TYPE_LOG, _("Log"));
		cmbValType.addItem(ITEM_VALUE_TYPE_TEXT, _("Text"));

		for(String vItem : new String[]{"filter_data_type_label","filter_data_type"}) {
			rda_subarray_push(fVTypeVisibility, ITEM_VALUE_TYPE_UINT64, vItem);
		}

		rda_add_post_js("var filterValueTypeSwitcher = new CViewSwitcher(\"filter_value_type\", \"change\", "+rda_jsvalue(fVTypeVisibility, true)+");");

		// status select
		CComboBox cmbStatus = new CComboBox("filter_status", filter_status);
		cmbStatus.addItem(-1, _("all"));
		for(int status : new int[]{ITEM_STATUS_ACTIVE, ITEM_STATUS_DISABLED}) {
			cmbStatus.addItem(status, item_status2str(status));
		}

		// state select
		CComboBox cmbState = new CComboBox("filter_state", filter_state);
		cmbState.addItem(-1, _("all"));
		for(int state : new int[]{ITEM_STATE_NORMAL, ITEM_STATE_NOTSUPPORTED}) {
			cmbState.addItem(state, itemState(state));
		}

		// update interval
		CSpan updateIntervalLabel = new CSpan(array(bold(_("Update interval")), SPACE+_("(in sec)")+NAME_DELIMITER));
		updateIntervalLabel.setAttribute("id", "filter_delay_label");

		CNumericBox updateIntervalInput = new CNumericBox("filter_delay", Nest.as(filter_delay).asString(), 5, false, true);
		updateIntervalInput.setEnabled("no");

		// data type
		CSpan dataTypeLabel = new CSpan(bold(_("Data type")+NAME_DELIMITER));
		dataTypeLabel.setAttribute("id", "filter_data_type_label");

		CComboBox dataTypeInput = new CComboBox("filter_data_type", filter_data_type);
		dataTypeInput.addItem(-1, _("all"));
		dataTypeInput.addItems(item_data_type2str());
		dataTypeInput.setEnabled(false);

		// filter table
		CTable table = new CTable("", "filter");
		table.setCellPadding(0);
		table.setCellSpacing(0);

		// SNMP community
		CSpan snmpCommunityLabel = new CSpan(array(bold(_("SNMP community")), SPACE+_("like")+NAME_DELIMITER));
		snmpCommunityLabel.setAttribute("id", "filter_snmp_community_label");

		CTextBox snmpCommunityField = new CTextBox("filter_snmp_community", Nest.as(filter_snmp_community).asString(), RDA_TEXTBOX_FILTER_SIZE);
		snmpCommunityField.setEnabled("no");

		// SNMPv3 security name
		CSpan snmpSecurityLabel = new CSpan(array(bold(_("Security name")), SPACE+_("like")+NAME_DELIMITER));
		snmpSecurityLabel.setAttribute("id", "filter_snmpv3_securityname_label");

		CTextBox snmpSecurityField = new CTextBox("filter_snmpv3_securityname", Nest.as(filter_snmpv3_securityname).asString(), RDA_TEXTBOX_FILTER_SIZE);
		snmpSecurityField.setEnabled("no");

		// SNMP OID
		CSpan snmpOidLabel = new CSpan(array(bold(_("SNMP OID")), SPACE+_("like")+NAME_DELIMITER));
		snmpOidLabel.setAttribute("id", "filter_snmp_oid_label");

		CTextBox snmpOidField = new CTextBox("filter_snmp_oid", Nest.as(filter_snmp_oid).asString(), RDA_TEXTBOX_FILTER_SIZE);
		snmpOidField.setEnabled("no");

		// port
		CSpan portLabel = new CSpan(array(bold(_("Port")), SPACE+_("like")+NAME_DELIMITER));
		portLabel.setAttribute("id", "filter_port_label");

		CNumericBox portField = new CNumericBox("filter_port", Nest.as(filter_port).asString(), 5, false, true);
		portField.setEnabled("no");

		// row 1
		CArray groupFilter = null;
		if (!empty(filter_groupId)) {
			CHostGroupGet hgoptins = new CHostGroupGet();
			hgoptins.setGroupIds(Nest.as(filter_groupId).asLong());
			hgoptins.setOutput(new String[]{"name"});
			CArray<Map> getHostInfos = API.HostGroup(idBean, executor).get(hgoptins);
			Map getHostInfo = reset(getHostInfos);
			if (!empty(getHostInfo)) {
				groupFilter = array();
				groupFilter.add(map(
					"id", Nest.value(getHostInfo,"groupid").$(),
					"name", Nest.value(getHostInfo,"name").$(),
					"prefix", ""
				));
			}
		}

		table.addRow(array(
			new CCol(bold(_("Host group")+NAME_DELIMITER), "label col1"),
			new CCol(array(
				new CMultiSelect(map(
					"name", "filter_groupid",
					"selectedLimit", 1,
					"objectName", "hostGroup",
					"objectOptions", map(
						"editable", true
					),
					"data", groupFilter,
					"popup", map(
						"parameters", "srctbl=host_groups&dstfrm="+form.getName()+"&dstfld1=filter_groupid&srcfld1=groupid&writeonly=1",
						"width", 450,
						"height", 450,
						"buttonClass", "input filter-multiselect-select-button"
					)
				))
			), "col1"),
			new CCol(bold(_("Type")+NAME_DELIMITER), "label col2"),
			new CCol(cmbType, "col2"),
			new CCol(bold(_("Type of information")+NAME_DELIMITER), "label col3"),
			new CCol(cmbValType, "col3"),
			new CCol(bold(_("State")+NAME_DELIMITER), "label"),
			new CCol(cmbState, "col4")
		), "item-list-row");
		// row 2
		CArray hostFilterData = null;
		if (!empty(filter_hostId)) {
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(Nest.as(filter_hostId).asCArray().valuesAsLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setOutput(new String[]{"name"});
			CArray<Map> getHostInfos = API.Host(idBean, executor).get(hoptions);
			Map getHostInfo = reset(getHostInfos);
			if (!empty(getHostInfo)) {
				hostFilterData = array();
				hostFilterData.add(map(
					"id", Nest.value(getHostInfo,"hostid").$(),
					"name", Nest.value(getHostInfo,"name").$(),
					"prefix", ""
				));
			}
		}

		table.addRow(array(
			new CCol(bold(_("Host")+NAME_DELIMITER), "label"),
			new CCol(array(
				new CMultiSelect(map(
					"name", "filter_hostid",
					"selectedLimit", 1,
					"objectName", "hosts",
					"objectOptions", map(
						"editable", true,
						"templated_hosts", true
					),
					"data", hostFilterData,
					"popup", map(
						"parameters", "srctbl=host_templates&dstfrm="+form.getName()+"&dstfld1=filter_hostid&srcfld1=hostid&writeonly=1",
						"width", 450,
						"height", 450,
						"buttonClass", "input filter-multiselect-select-button"
					)
				))
			), "col1"),
			new CCol(updateIntervalLabel, "label"),
			new CCol(updateIntervalInput),
			new CCol(dataTypeLabel, "label"),
			new CCol(dataTypeInput),
			new CCol(bold(_("Status")+NAME_DELIMITER), "label col4"),
			new CCol(cmbStatus, "col4")
		), "item-list-row");
		// row 3
		table.addRow(array(
			new CCol(bold(_("Application")+NAME_DELIMITER), "label"),
			new CCol(array(
				new CTextBox("filter_application", Nest.as(filter_application).asString(), RDA_TEXTBOX_FILTER_SIZE),
				new CButton("btn_app", _("Select"),
					"return PopUp(\"popup.action?srctbl=applications&srcfld1=applicationid"+
						"&dstfrm="+form.getName()+"&dstfld1=filter_application"+
						"&with_applications=1"+
						"\" + (jQuery('input[name=\"filter_hostid\"]').length > 0 ? \"&hostid=\"+jQuery('input[name=\"filter_hostid\"]').val() : '')"+
						", 550, 450, \"application\");",
					"filter-select-button"
				)
			), "col1"),
			new CCol(array(snmpCommunityLabel, snmpSecurityLabel), "label"),
			new CCol(array(snmpCommunityField, snmpSecurityField)),
			new CCol(array(bold(_("History")), SPACE+_("(in days)")+NAME_DELIMITER), "label"),
			new CCol(new CNumericBox("filter_history", Nest.as(filter_history).asString(), 8, false, true)),
			new CCol(bold(_("Triggers")+NAME_DELIMITER), "label"),
			new CCol(new CComboBox("filter_with_triggers", filter_with_triggers, null, (CArray)map(
				-1, _("all"),
				1 , _("With triggers"),
				0 , _("Without triggers")
			)))
		), "item-list-row");
		// row 4
		table.addRow(array(
			new CCol(array(bold(_("Name")), SPACE+_("like")+NAME_DELIMITER), "label"),
			new CCol(new CTextBox("filter_name", Nest.as(filter_name).asString(), RDA_TEXTBOX_FILTER_SIZE), "col1"),
			new CCol(snmpOidLabel, "label"),
			new CCol(snmpOidField),
			new CCol(array(bold(_("Trends")), SPACE+_("(in days)")+NAME_DELIMITER), "label"),
			new CCol(new CNumericBox("filter_trends", Nest.as(filter_trends).asString(), 8, false, true)),
			new CCol(bold(_("Template")+NAME_DELIMITER), "label"),
			new CCol(new CComboBox("filter_templated_items", filter_templated_items, null, (CArray)map(
				-1, _("all"),
				1 , _("Templated items"),
				0 , _("Not Templated items")
			)))
		), "item-list-row");
		// row 5
		table.addRow(array(
			new CCol(array(bold(_("Key")), SPACE+_("like")+NAME_DELIMITER), "label"),
			new CCol(new CTextBox("filter_key", Nest.as(filter_key).asString(), RDA_TEXTBOX_FILTER_SIZE), "col1"),
			new CCol(portLabel, "label"),
			new CCol(portField),
			new CCol(null, "label"),
			new CCol(),
			new CCol(null, "label"),
			new CCol()
		), "item-list-row");

		CButton filter = new CButton("filter", _("GoFilter"),
			"javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true); chkbxRange.clearSelectedOnFilterChange();"
		);
		filter.useJQueryStyle("main");

		CButton reset = new CButton("reset", _("Reset"), "javascript: clearAllForm(\"rda_filter\");");
		reset.useJQueryStyle();

		CDiv div_buttons = new CDiv(array(filter, SPACE, reset));
		div_buttons.setAttribute("style", "padding: 4px 0px;");

		CCol footer = new CCol(div_buttons, "controls", 8);

		table.addRow(footer);
		form.addItem(table);

		// subfilters
		CTable table_subfilter = new CTable(null, "filter sub-filter");

		// array contains subfilters and number of items in each
		CArray<CArray>item_params = map(
			"hosts", array(),
			"applications", array(),
			"types", array(),
			"value_types", array(),
			"status", array(),
			"state", array(),
			"templated_items", array(),
			"with_triggers", array(),
			"history", array(),
			"trends", array(),
			"interval", array()
		);

		// generate array with values for subfilters of selected items
		for(Map item : items) {
			// hosts
			if (rda_empty(filter_hostId)) {
				Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());

				if (!isset(Nest.value(item_params,"hosts",host.get("hostid")).$())) {
					Nest.value(item_params,"hosts",host.get("hostid")).$(map("name", Nest.value(host,"name").$(), "count", 0));
				}
				boolean show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    String name = Nest.as(e.getKey()).asString();
				    Boolean value = e.getValue();
					if ("subfilter_hosts".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
					Nest.value(item_params,"hosts",host.get("hostid"),"count").$(Nest.value(item_params,"hosts",host.get("hostid"),"count").asInteger()+1);
				}
			}

			// applications
			if (!empty(Nest.value(item,"applications").$())) {
				for(Map application : (CArray<Map>)Nest.value(item,"applications").asCArray()) {
					if (!isset(Nest.value(item_params,"applications",application.get("name")).$())) {
						Nest.value(item_params,"applications",application.get("name")).$(map("name", Nest.value(application,"name").$(), "count", 0));
					}
				}
			}
			boolean show_item = true;
			for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
			    Object name = e.getKey();
			    Boolean value = e.getValue();
				if ("subfilter_apps".equals(name)) {
					continue;
				}
				show_item &= value;
			}
			boolean sel_app = false;
			if (show_item) {
				// if any of item applications are selected
				for(Map app : (CArray<Map>)Nest.value(item,"applications").asCArray()) {
					if (str_in_array(Nest.value(app,"name").$(), Nest.as(subfilter_apps).asCArray())) {
						sel_app = true;
						break;
					}
				}
				for(Map app : (CArray<Map>)Nest.value(item,"applications").asCArray()) {
					if (str_in_array(Nest.value(app,"name").$(), Nest.as(subfilter_apps).asCArray()) || !sel_app) {
						Nest.value(item_params,"applications",app.get("name"),"count").$(Nest.value(item_params,"applications",app.get("name"),"count").asInteger()+1);
					}
				}
			}

			// types
			if (Nest.as(filter_type).asInteger() == -1) {
				if (!isset(Nest.value(item_params,"types",item.get("type")).$())) {
					Nest.value(item_params,"types",item.get("type")).$(map("name", item_type2str(Nest.value(item,"type").asInteger()), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_types".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"types",item.get("type"),"count").$(Nest.value(item_params,"types",item.get("type"),"count").asInteger()+1);
				}
			}

			// value types
			if (Nest.as(filter_value_type).asInteger() == -1) {
				if (!isset(Nest.value(item_params,"value_types",item.get("value_type")).$())) {
					Nest.value(item_params,"value_types",item.get("value_type")).$(map(
						"name", itemValueTypeString(Nest.value(item,"value_type").asInteger()),
						"count", 0
					));
				}

				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_value_types".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"value_types",item.get("value_type"),"count").$(Nest.value(item_params,"value_types",item.get("value_type"),"count").asInteger()+1);
				}
			}

			// status
			if (Nest.as(filter_status).asInteger() == -1) {
				if (!isset(Nest.value(item_params,"status",item.get("status")).$())) {
					Nest.value(item_params,"status",item.get("status")).$(map(
						"name", item_status2str(Nest.value(item,"status").asInteger()),
						"count", 0
					));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_status".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"status",item.get("status"),"count").$(Nest.value(item_params,"status",item.get("status"),"count").asInteger()+1);
				}
			}

			// state
			if (Nest.as(filter_state).asInteger() == -1) {
				if (!isset(Nest.value(item_params,"state",item.get("state")).$())) {
					Nest.value(item_params,"state",item.get("state")).$(map(
						"name", itemState(Nest.value(item,"state").asInteger()),
						"count", 0
					));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_state".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"state",item.get("state"),"count").$(Nest.value(item_params,"state",item.get("state"),"count").asInteger()+1);
				}
			}

			// template
			if (Nest.as(filter_templated_items).asInteger() == -1) {
				if (Nest.value(item,"templateid").asLong() == 0 && !isset(Nest.value(item_params,"templated_items",0).$())) {
					Nest.value(item_params,"templated_items",0).$(map("name", _("Not Templated items"), "count", 0));
				} else if (Nest.value(item,"templateid").asLong() > 0 && !isset(Nest.value(item_params,"templated_items", 1).$())) {
					Nest.value(item_params,"templated_items",1).$(map("name", _("Templated items"), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_templated_items".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					if (Nest.value(item,"templateid").asLong() == 0) {
						Nest.value(item_params,"templated_items",0,"count").$(Nest.value(item_params,"templated_items",0,"count").asInteger()+1);
					} else {
						Nest.value(item_params,"templated_items",1,"count").$(Nest.value(item_params,"templated_items",1,"count").asInteger()+1);
					}
				}
			}

			// with triggers
			if (Nest.as(filter_with_triggers).asInteger() == -1) {
				if (count(Nest.value(item,"triggers").$()) == 0 && !isset(Nest.value(item_params,"with_triggers",0).$())) {
					Nest.value(item_params,"with_triggers",0).$(map("name", _("Without triggers"), "count", 0));
				} else if (count(Nest.value(item,"triggers").$()) > 0 && !isset(Nest.value(item_params,"with_triggers",1).$())) {
					Nest.value(item_params,"with_triggers",1).$(map("name", _("Without triggers"), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_with_triggers".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					if (count(Nest.value(item,"triggers").$()) == 0) {
						Nest.value(item_params,"with_triggers",0,"count").$(Nest.value(item_params,"with_triggers",0,"count").asInteger()+1);
					} else {
						Nest.value(item_params,"with_triggers",1,"count").$(Nest.value(item_params,"with_triggers",1,"count").asInteger()+1);
					}
				}
			}

			// trends
			if (rda_empty(filter_trends)) {
				if (!isset(Nest.value(item_params,"trends",item.get("trends")).$())) {
					Nest.value(item_params,"trends",item.get("trends")).$(map("name", Nest.value(item,"trends").$(), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_trends".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"trends",item.get("trends"),"count").$(Nest.value(item_params,"trends",item.get("trends"),"count").asInteger()+1);
				}
			}

			// history
			if (rda_empty(filter_history)) {
				if (!isset(Nest.value(item_params,"history",item.get("history")).$())) {
					Nest.value(item_params,"history",item.get("history")).$(map("name", Nest.value(item,"history").$(), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_history".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"history",item.get("history"),"count").$(Nest.value(item_params,"history",item.get("history"),"count").asInteger()+1);
				}
			}

			// interval
			if (rda_empty(filter_delay) && Nest.as(filter_type).asInteger() != ITEM_TYPE_TRAPPER) {
				if (!isset(Nest.value(item_params,"interval",item.get("delay")).$())) {
					Nest.value(item_params,"interval",item.get("delay")).$(map("name", Nest.value(item,"delay").$(), "count", 0));
				}
				show_item = true;
				for (Entry<Object, Boolean> e : ((CArray<Boolean>)Nest.value(item,"subfilters").asCArray()).entrySet()) {
				    Object name = e.getKey();
				    Boolean value = e.getValue();
					if ("subfilter_interval".equals(name)) {
						continue;
					}
					show_item &= value;
				}
				if (show_item) {
					Nest.value(item_params,"interval",item.get("delay"),"count").$(Nest.value(item_params,"interval",item.get("delay"),"count").asInteger()+1);
				}
			}
		}

		// output
		if (rda_empty(filter_hostId) && count(Nest.value(item_params,"hosts").$()) > 1) {
			CArray hosts_output = prepareSubfilterOutput(Nest.value(item_params,"hosts").asCArray(), Nest.as(subfilter_hosts).asCArray(), "subfilter_hosts");
			table_subfilter.addRow(array(_("Hosts"), hosts_output));
		}

		if (!empty(Nest.value(item_params,"applications").$()) && count(Nest.value(item_params,"applications").$()) > 1) {
			CArray application_output = prepareSubfilterOutput(Nest.value(item_params,"applications").asCArray(), Nest.as(subfilter_apps).asCArray(), "subfilter_apps");
			table_subfilter.addRow(array(_("Applications"), application_output));
		}

		if (Nest.as(filter_type).asInteger() == -1 && count(Nest.value(item_params,"types").$()) > 1) {
			CArray type_output = prepareSubfilterOutput(Nest.value(item_params,"types").asCArray(), Nest.as(subfilter_types).asCArray(), "subfilter_types");
			table_subfilter.addRow(array(_("Types"), type_output));
		}

		if (Nest.as(filter_value_type).asInteger() == -1 && count(Nest.value(item_params,"value_types").$()) > 1) {
			CArray value_types_output = prepareSubfilterOutput(Nest.value(item_params,"value_types").asCArray(), Nest.as(subfilter_value_types).asCArray(), "subfilter_value_types");
			table_subfilter.addRow(array(_("Type of information"), value_types_output));
		}

		if (Nest.as(filter_status).asInteger() == -1 && count(Nest.value(item_params,"status").$()) > 1) {
			CArray status_output = prepareSubfilterOutput(Nest.value(item_params,"status").asCArray(), Nest.as(subfilter_status).asCArray(), "subfilter_status");
			table_subfilter.addRow(array(_("Status"), status_output));
		}

		if (Nest.as(filter_state).asInteger() == -1 && count(Nest.value(item_params,"state").$()) > 1) {
			CArray state_output = prepareSubfilterOutput(Nest.value(item_params,"state").asCArray(), Nest.as(subfilter_state).asCArray(), "subfilter_state");
			table_subfilter.addRow(array(_("State"), state_output));
		}

		if (Nest.as(filter_templated_items).asInteger() == -1 && count(Nest.value(item_params,"templated_items").$()) > 1) {
			CArray templated_items_output = prepareSubfilterOutput(Nest.value(item_params,"templated_items").asCArray(), Nest.as(subfilter_templated_items).asCArray(), "subfilter_templated_items");
			table_subfilter.addRow(array(_("Template"), templated_items_output));
		}

		if (Nest.as(filter_with_triggers).asInteger() == -1 && count(Nest.value(item_params,"with_triggers").$()) > 1) {
			CArray with_triggers_output = prepareSubfilterOutput(Nest.value(item_params,"with_triggers").asCArray(), Nest.as(subfilter_with_triggers).asCArray(), "subfilter_with_triggers");
			table_subfilter.addRow(array(_("With triggers"), with_triggers_output));
		}

		if (rda_empty(filter_history) && count(Nest.value(item_params,"history").$()) > 1) {
			CArray history_output = prepareSubfilterOutput(Nest.value(item_params,"history").asCArray(), Nest.as(subfilter_history).asCArray(), "subfilter_history");
			table_subfilter.addRow(array(_("History"), history_output));
		}

		if (rda_empty(filter_trends) && (count(Nest.value(item_params,"trends").$()) > 1)) {
			CArray trends_output = prepareSubfilterOutput(Nest.value(item_params,"trends").asCArray(), Nest.as(subfilter_trends).asCArray(), "subfilter_trends");
			table_subfilter.addRow(array(_("Trends"), trends_output));
		}

		if (rda_empty(filter_delay) && Nest.as(filter_type).asInteger() != ITEM_TYPE_TRAPPER && count(Nest.value(item_params,"interval").$()) > 1) {
			CArray interval_output = prepareSubfilterOutput(Nest.value(item_params,"interval").asCArray(), Nest.as(subfilter_interval).asCArray(), "subfilter_interval");
			table_subfilter.addRow(array(_("Interval"), interval_output));
		}

		form.addItem(new CDiv(_("Subfilter [affects only filtered data!]"), "thin_header"));
		form.addItem(table_subfilter);

		return form;
	}
	
	/**
	 * Get data for item edit page.
	 *
	 * @param bool options["is_discovery_rule"]
	 *
	 * @return array
	 */
	public static Map getItemFormData(IIdentityBean idBean, SQLExecutor executor) {
		return getItemFormData(idBean, executor, array());
	}
	
	/**
	 * Get data for item edit page.
	 *
	 * @param bool options["is_discovery_rule"]
	 *
	 * @return array
	 */
	public static Map getItemFormData(IIdentityBean idBean, SQLExecutor executor,CArray options) {
		String ifm = get_request("filter_macro");
		String ifv = get_request("filter_value");
		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		Map data = map(
			"form", get_request("form"),
			"form_refresh", get_request("form_refresh"),
			"is_discovery_rule", !empty(Nest.value(options,"is_discovery_rule").$()),
			"parent_discoveryid", get_request("parent_discoveryid", !empty(Nest.value(options,"is_discovery_rule").$()) ? get_request("itemid", null) : null),
			"itemid", get_request("itemid", null),
			"limited", false,
			"interfaceid", get_request("interfaceid", 0),
			"name", get_request("name", ""),
			"description", get_request("description", ""),
			"key", get_request("key", ""),
			"hostname", get_request("hostname", null),
			"delay", get_request("delay", RDA_ITEM_DELAY_DEFAULT),
			"history", get_request("history", 90),
			"status", get_request("status", isset(Nest.value(_REQUEST,"form_refresh").$()) ? 1 : 0),
			"type", get_request("type", 0),
			"snmp_community", get_request("snmp_community", "public"),
			"snmp_oid", get_request("snmp_oid", "interfaces.ifTable.ifEntry.ifInOctets.1"),
			"port", get_request("port", ""),
			"value_type", get_request("value_type", ITEM_VALUE_TYPE_UINT64),
			"data_type", get_request("data_type", ITEM_DATA_TYPE_DECIMAL),
			"trapper_hosts", get_request("trapper_hosts", ""),
			"units", get_request("units", ""),
			"valuemapid", get_request("valuemapid", 0),
			"params", get_request("params", ""),
			"multiplier", get_request("multiplier", 0),
			"delta", get_request("delta", 0),
			"trends", get_request("trends", DAY_IN_YEAR),
			"new_application", get_request("new_application", ""),
			"applications", get_request("applications", array()),
			"delay_flex", get_request("delay_flex", array()),
			"new_delay_flex", get_request("new_delay_flex", map("delay", 50, "period", RDA_DEFAULT_INTERVAL)),
			"snmpv3_contextname", get_request("snmpv3_contextname", ""),
			"snmpv3_securityname", get_request("snmpv3_securityname", ""),
			"snmpv3_securitylevel", get_request("snmpv3_securitylevel", 0),
			"snmpv3_authprotocol", get_request("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_MD5),
			"snmpv3_authpassphrase", get_request("snmpv3_authpassphrase", ""),
			"snmpv3_privprotocol", get_request("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_DES),
			"snmpv3_privpassphrase", get_request("snmpv3_privpassphrase", ""),
			"ipmi_sensor", get_request("ipmi_sensor", ""),
			"authtype", get_request("authtype", 0),
			"username", get_request("username", ""),
			"password", get_request("password", ""),
			"publickey", get_request("publickey", ""),
			"privatekey", get_request("privatekey", ""),
			"formula", get_request("formula", 1f),
			"logtimefmt", get_request("logtimefmt", ""),
			"inventory_link", get_request("inventory_link", 0),
			"add_groupid", get_request("add_groupid", get_request("groupid", 0)),
			"valuemaps", null,
			"possibleHostInventories", null,
			"alreadyPopulated", null,
			"lifetime", get_request("lifetime", 30),
			"filter", isset(ifm)&&isset(ifv) ? ifm+":"+ifv : "",
			"initial_item_type", null,
			"templates", array()
		);

		// hostid
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setItemIds(Nest.value(data,"parent_discoveryid").asLong());
			droptions.setOutput(API_OUTPUT_EXTEND);
			droptions.setEditable(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(idBean, executor).get(droptions);
			Map discoveryRule = reset(discoveryRules);
			Nest.value(data,"hostid").$(Nest.value(discoveryRule,"hostid").$());
		} else {
			Nest.value(data,"hostid").$(get_request("hostid", 0));
		}

		// types, http items only for internal processes
		CArray<String> types = item_type2str();
		Nest.value(data,"types").$(types);
		unset(types,ITEM_TYPE_HTTPTEST);
		if (!empty(Nest.value(options,"is_discovery_rule").$())) {
			unset(types,ITEM_TYPE_AGGREGATE);
			unset(types,ITEM_TYPE_CALCULATED);
			unset(types,ITEM_TYPE_SNMPTRAP);
		}

		// item
		if (!empty(Nest.value(data,"itemid").$())) {
			if (Nest.value(data,"is_discovery_rule").asBoolean()) {
				CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
				droptions.setItemIds(Nest.value(data,"itemid").asLong());
				droptions.setOutput(API_OUTPUT_EXTEND);
				droptions.setHostIds(Nest.value(data,"hostid").asLong());
				droptions.setEditable(true);
				Nest.value(data,"item").$(API.DiscoveryRule(idBean, executor).get(droptions));
			} else {
				CItemGet ioptions = new CItemGet();
				ioptions.setItemIds(Nest.value(data,"itemid").asLong());
				ioptions.setFilter("flags");
				ioptions.setOutput(new String[]{
						"itemid", "type", "snmp_community", "snmp_oid", "hostid", "name", "key_", "delay", "history",
						"trends", "status", "value_type", "trapper_hosts", "units", "multiplier", "delta",
						"snmpv3_securityname", "snmpv3_securitylevel", "snmpv3_authpassphrase", "snmpv3_privpassphrase",
						"formula", "logtimefmt", "templateid", "valuemapid", "delay_flex", "params", "ipmi_sensor",
						"data_type", "authtype", "username", "password", "publickey", "privatekey", "filter",
						"interfaceid", "port", "description", "inventory_link", "lifetime", "snmpv3_authprotocol",
						"snmpv3_privprotocol", "snmpv3_contextname"});
				Nest.value(data,"item").$(API.Item(idBean, executor).get(ioptions));
			}
			Nest.value(data,"item").$(reset(Nest.value(data,"item").asCArray()));
			Nest.value(data,"hostid").$(!empty(Nest.value(data,"hostid").$()) ? Nest.value(data,"hostid").$() : Nest.value(data,"item","hostid").$());
			Nest.value(data,"limited").$(Nest.value(data,"item","templateid").asLong() != 0);

			// get templates
			Long itemid = Nest.value(data,"itemid").asLong();
			do {
				CArray<Map> items = null;
				if (Nest.value(data,"is_discovery_rule").asBoolean()) {
					CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
					droptions.setItemIds(itemid);
					droptions.setOutput(new String[]{"itemid", "templateid"});
					droptions.setSelectHosts(new String[]{"name"});
					items = API.DiscoveryRule(idBean, executor).get(droptions);
				} else {
					CItemGet ioptions = new CItemGet();
					ioptions.setItemIds(itemid);
					ioptions.setOutput(new String[]{"itemid", "templateid"});
					ioptions.setSelectHosts(new String[]{"name"});
					ioptions.setSelectDiscoveryRule(new String[]{"itemid"});
					ioptions.setFilter("flags");
					items = API.Item(idBean, executor).get(ioptions);
				}
				Map item = reset(items);

				if (!empty(item)) {
					Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
					if (!empty(Nest.value(item,"hosts").$())) {
						Nest.value(host,"name").$(CHtml.encode(Nest.value(host,"name").asString()));
						CArray templates = Nest.value(data,"templates").asCArray();
						if (bccomp(Nest.value(data,"itemid").$(), itemid) == 0) {
						}
						// discovery rule
						else if (Nest.value(data,"is_discovery_rule").asBoolean()) {
							templates.add(new CLink(Nest.value(host,"name").$(), "host_discovery.action?form=update&itemid="+Nest.value(item,"itemid").$(), "highlight underline weight_normal"));
							templates.add(SPACE+RARR+SPACE);
						}
						// item prototype
						else if (Nest.value(item,"discoveryRule").asBoolean()) {
							templates.add(new CLink(Nest.value(host,"name").$(), "disc_prototypes.action?form=update&itemid="+Nest.value(item,"itemid").$()+"&parent_discoveryid="+Nest.value(item,"discoveryRule","itemid").$(), "highlight underline weight_normal"));
							templates.add(SPACE+RARR+SPACE);
						}
						// plain item
						else {
							templates.add(new CLink(Nest.value(host,"name").$(), "items.action?form=update&itemid="+Nest.value(item,"itemid").$(), "highlight underline weight_normal"));
							templates.add(SPACE+RARR+SPACE);
						}
					}
					itemid = Nest.value(item,"templateid").asLong();
				} else {
					break;
				}
			} while (Nest.as(itemid).asLong() != 0);

			Nest.value(data,"templates").$(array_reverse(Nest.value(data,"templates").asCArray()));
			array_shift(Nest.value(data,"templates").asCArray());
		}

		// caption
		if (!empty(Nest.value(data,"is_discovery_rule").$())) {
			Nest.value(data,"caption").$(_("Discovery rule"));
		} else {
			Nest.value(data,"caption").$(!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Item prototype") : _("Item"));
		}

		// hostname
		if (empty(Nest.value(data,"is_discovery_rule").$()) && empty(Nest.value(data,"hostname").$())) {
			if (!empty(Nest.value(data,"hostid").$())) {
				CHostGet hoptions = new CHostGet();
				hoptions.setHostIds(Nest.value(data,"hostid").asLong());
				hoptions.setOutput(new String[]{"name"});
				hoptions.setTemplatedHosts(true);
				CArray<Map> hostInfos = API.Host(idBean, executor).get(hoptions);
				Map hostInfo = reset(hostInfos);
				Nest.value(data,"hostname").$(Nest.value(hostInfo,"name").$());
			} else {
				Nest.value(data,"hostname").$( _("not selected"));
			}
		}

		// fill data from item
		if ((!empty(Nest.value(data,"itemid").$()) && !isset(_REQUEST,"form_refresh")) || (Nest.value(data,"limited").asBoolean() && !isset(_REQUEST,"form_refresh"))) {
			Nest.value(data,"name").$(Nest.value(data,"item","name").$());
			Nest.value(data,"description").$(Nest.value(data,"item","description").$());
			Nest.value(data,"key").$(Nest.value(data,"item","key_").$());
			Nest.value(data,"interfaceid").$(Nest.value(data,"item","interfaceid").$());
			Nest.value(data,"type").$(Nest.value(data,"item","type").$());
			Nest.value(data,"snmp_community").$(Nest.value(data,"item","snmp_community").$());
			Nest.value(data,"snmp_oid").$(Nest.value(data,"item","snmp_oid").$());
			Nest.value(data,"port").$(Nest.value(data,"item","port").$());
			Nest.value(data,"value_type").$(Nest.value(data,"item","value_type").$());
			Nest.value(data,"data_type").$(Nest.value(data,"item","data_type").$());
			Nest.value(data,"trapper_hosts").$(Nest.value(data,"item","trapper_hosts").$());
			Nest.value(data,"units").$(Nest.value(data,"item","units").$());
			Nest.value(data,"valuemapid").$(Nest.value(data,"item","valuemapid").$());
			Nest.value(data,"multiplier").$(Nest.value(data,"item","multiplier").$());
			Nest.value(data,"hostid").$(Nest.value(data,"item","hostid").$());
			Nest.value(data,"params").$(Nest.value(data,"item","params").$());
			Nest.value(data,"snmpv3_contextname").$(Nest.value(data,"item","snmpv3_contextname").$());
			Nest.value(data,"snmpv3_securityname").$(Nest.value(data,"item","snmpv3_securityname").$());
			Nest.value(data,"snmpv3_securitylevel").$(Nest.value(data,"item","snmpv3_securitylevel").$());
			Nest.value(data,"snmpv3_authprotocol").$(Nest.value(data,"item","snmpv3_authprotocol").$());
			Nest.value(data,"snmpv3_authpassphrase").$(Nest.value(data,"item","snmpv3_authpassphrase").$());
			Nest.value(data,"snmpv3_privprotocol").$(Nest.value(data,"item","snmpv3_privprotocol").$());
			Nest.value(data,"snmpv3_privpassphrase").$(Nest.value(data,"item","snmpv3_privpassphrase").$());
			Nest.value(data,"ipmi_sensor").$(Nest.value(data,"item","ipmi_sensor").$());
			Nest.value(data,"authtype").$(Nest.value(data,"item","authtype").$());
			Nest.value(data,"username").$(Nest.value(data,"item","username").$());
			Nest.value(data,"password").$(Nest.value(data,"item","password").$());
			Nest.value(data,"publickey").$(Nest.value(data,"item","publickey").$());
			Nest.value(data,"privatekey").$(Nest.value(data,"item","privatekey").$());
			Nest.value(data,"formula").$(Nest.value(data,"item","formula").$());
			Nest.value(data,"logtimefmt").$(Nest.value(data,"item","logtimefmt").$());
			Nest.value(data,"inventory_link").$(Nest.value(data,"item","inventory_link").$());
			Nest.value(data,"new_application").$(get_request("new_application", ""));
			Nest.value(data,"lifetime").$(Nest.value(data,"item","lifetime").$());
			Nest.value(data,"filter").$(Nest.value(data,"item","filter").$());

			if (!Nest.value(data,"limited").asBoolean() || !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"delay").$(Nest.value(data,"item","delay").$());
				if ((Nest.value(data,"type").asInteger() == ITEM_TYPE_TRAPPER || Nest.value(data,"type").asInteger() == ITEM_TYPE_SNMPTRAP) && Nest.value(data,"delay").asLong() == 0) {
					Nest.value(data,"delay").$(RDA_ITEM_DELAY_DEFAULT);
				}
				Nest.value(data,"history").$(Nest.value(data,"item","history").$());
				Nest.value(data,"status").$(Nest.value(data,"item","status").$());
				Nest.value(data,"delta").$(Nest.value(data,"item","delta").$());
				Nest.value(data,"trends").$(Nest.value(data,"item","trends").$());

				String db_delay_flex = Nest.value(data,"item","delay_flex").asString();
				if (isset(db_delay_flex)) {
					String[] arr_of_dellays = explode(";", db_delay_flex);
					for(String one_db_delay : arr_of_dellays) {
						String[] arr_of_delay = explode("/", one_db_delay);
						if (arr_of_delay.length<2 || !isset(arr_of_delay[0]) || !isset(arr_of_delay[1])) {
							continue;
						}
						array_push(Nest.value(data,"delay_flex").asCArray(), map("delay", arr_of_delay[0], "period", arr_of_delay[1]));
					}
				}
				Nest.value(data,"applications").$(array_unique(rda_array_merge(Nest.value(data,"applications").asCArray(), get_applications_by_itemid(executor, Nest.value(data,"itemid").asLong()))));
			}
		}

		// applications
		if (count(Nest.value(data,"applications").$()) == 0) {
			array_push(Nest.value(data,"applications").asCArray(), 0);
		}
		SqlBuilder sqlParts = new SqlBuilder();
		Nest.value(data,"db_applications").$(DBselect(executor,
			"SELECT DISTINCT a.applicationid,a.name"+
			" FROM applications a"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "applications", "a")+
			    " AND a.hostid="+sqlParts.marshalParam(Nest.value(data,"hostid").$()),
			sqlParts.getNamedParams()
		));
		order_result(Nest.value(data,"db_applications").asCArray(), "name");

		// interfaces
		CHostIfaceGet hioptions = new CHostIfaceGet();
		hioptions.setHostIds(Nest.value(data,"hostid").asLong());
		hioptions.setOutput(API_OUTPUT_EXTEND);
		Nest.value(data,"interfaces").$(API.HostInterface(idBean, executor).get(hioptions));

		// valuemapid
		if (Nest.value(data,"limited").asBoolean()) {
			if (!empty(Nest.value(data,"valuemapid").$())) {
				sqlParts = new SqlBuilder();
				Map map_data = DBfetch(DBselect(executor,
						"SELECT v.name FROM valuemaps v "+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "valuemaps", "v")+
						    " AND v.valuemapid="+sqlParts.marshalParam(Nest.value(data,"valuemapid").$()),
						 sqlParts.getNamedParams()));
				if (!empty(map_data)) {
					Nest.value(data,"valuemaps").$(Nest.value(map_data,"name").$());
				}
			}
		} else {
			sqlParts = new SqlBuilder();
			Nest.value(data,"valuemaps").$(DBselect(executor,
					"SELECT v.*"+
					" FROM valuemaps v"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "valuemaps", "v"),
					sqlParts.getNamedParams()
			));
			order_result(Nest.value(data,"valuemaps").asCArray(), "name");
		}

		// possible host inventories
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			Nest.value(data,"possibleHostInventories").$(getHostInventories());

			// get already populated fields by other items
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"inventory_link"});
			ioptions.setFilter("hostid", Nest.value(data,"hostid").asString());
			ioptions.setNopermissions(true);
			Nest.value(data,"alreadyPopulated").$(API.Item(idBean, executor).get(ioptions));
			Nest.value(data,"alreadyPopulated").$(rda_toHash(Nest.value(data,"alreadyPopulated").$(), "inventory_link"));
		}

		// template
		Nest.value(data,"is_template").$(isTemplate(idBean, executor, Nest.value(data,"hostid").asString()));

		// unset snmpv3 fields
		if (Nest.value(data,"type").asInteger() != ITEM_TYPE_SNMPV3) {
			Nest.value(data,"snmpv3_contextname").$("");
			Nest.value(data,"snmpv3_securityname").$("");
			Nest.value(data,"snmpv3_securitylevel").$(ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV);
			Nest.value(data,"snmpv3_authprotocol").$(ITEM_AUTHPROTOCOL_MD5);
			Nest.value(data,"snmpv3_authpassphrase").$("");
			Nest.value(data,"snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
			Nest.value(data,"snmpv3_privpassphrase").$("");
		}

		// unset ssh auth fields
		if (Nest.value(data,"type").asInteger() != ITEM_TYPE_SSH) {
			Nest.value(data,"authtype").$(ITEM_AUTHTYPE_PASSWORD);
			Nest.value(data,"publickey").$("");
			Nest.value(data,"privatekey").$("");
		}

		return data;
	}
	
	public static Map getCopyElementsFormData(IIdentityBean idBean, SQLExecutor executor, String elementsField) {
		return getCopyElementsFormData(idBean, executor, elementsField, null);
	}
	
	public static Map getCopyElementsFormData(IIdentityBean idBean, SQLExecutor executor, String elementsField, String title) {
		CArray data = map(
			"title", title,
			"elements_field", elementsField,
			"elements", get_request(elementsField, array()),
			"copy_type", get_request("copy_type", 0),
			"filter_groupid", get_request("filter_groupid", 0),
			"copy_targetid", get_request("copy_targetid", array()),
			"hostid", get_request("hostid", 0),
			"groups", array(),
			"hosts", array()
		);

		// validate elements
		if (empty(Nest.value(data,"elements").$()) || !isArray(Nest.value(data,"elements").$())) {
			error(_("Incorrect list of items."));
			return null;
		}

		// get groups
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid", "name"});
		CArray<Map> groups = API.HostGroup(idBean, executor).get(hgoptions);
		Nest.value(data,"groups").$(groups);
		order_result(groups, "name");

		// get hosts
		if (Nest.value(data,"copy_type").asInteger() == 0) {
			for(Map group : groups) {
				if (empty(Nest.value(data,"filter_groupid").$())) {
					Nest.value(data,"filter_groupid").$(Nest.value(group,"groupid").$());
				}
			}

			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(new String[]{"groupid", "name"});
			hoptions.setGroupIds(Nest.value(data,"filter_groupid").asLong());
			hoptions.setTemplatedHosts(true);
			CArray<Map> hosts = API.Host(idBean, executor).get(hoptions);
			Nest.value(data,"hosts").$(hosts);
			order_result(hosts, "name");
		}

		return data;
	}
	
	public static CArray getTriggerMassupdateFormData(IIdentityBean idBean, SQLExecutor executor) {
		CArray data = map(
			"visible", get_request("visible", array()),
			"priority", get_request("priority", ""),
			"dependencies", get_request("dependencies", array()),
			"massupdate", get_request("massupdate", 1),
			"parent_discoveryid", get_request("parent_discoveryid"),
			"go", get_request("go", "massupdate"),
			"g_triggerid", get_request("g_triggerid", array()),
			"priority", get_request("priority", 0),
			"config", select_config(idBean, executor),
			"hostid", get_request("hostid", 0)
		);

		// get dependencies
		CTriggerGet toptions = new CTriggerGet();
		toptions.setTriggerIds(Nest.array(data,"dependencies").asLong());
		toptions.setOutput(new String[]{"triggerid", "description"});
		toptions.setPreserveKeys(true);
		toptions.setSelectHosts(new String[]{"name"});
		CArray<Map> dependencies = API.Trigger(idBean, executor).get(toptions);
		Nest.value(data,"dependencies").$(dependencies);
		for(Map dependency:dependencies) {
			if (!empty(Nest.value(dependency,"hosts",0,"name").$())) {
				Nest.value(dependency,"host").$(Nest.value(dependency,"hosts",0,"name").$());
			}
			unset(dependency,"hosts");
		}
		order_result(dependencies, "description");

		return data;
	}
	
	public static Map getTriggerFormData(IIdentityBean idBean, SQLExecutor executor) {
		CArray data = map(
			"form", get_request("form"),
			"form_refresh", get_request("form_refresh"),
			"parent_discoveryid", get_request("parent_discoveryid"),
			"dependencies", get_request("dependencies", array()),
			"db_dependencies", array(),
			"triggerid", get_request("triggerid"),
			"expression", get_request_real("expression", ""),
			"expr_temp", get_request("expr_temp", ""),
			"description", get_request("description", ""),
			"type", get_request("type", 0),
			"priority", get_request("priority", 0),
			"status", get_request("status", 0),
			"comments", get_request("comments", ""),
			"url", get_request("url", ""),
			"input_method", get_request("input_method", IM_ESTABLISHED),
			"limited", null,
			"templates", array(),
			"hostid", get_request("hostid", 0)
		);

		if (!empty(Nest.value(data,"triggerid").$())) {
			// get trigger
			CArray<Map> triggers = null;
			if(!empty(Nest.value(data,"parent_discoveryid").$())){
				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setOutput(API_OUTPUT_EXTEND);
				tpoptions.setSelectHosts(new String[] { "hostid" });
				tpoptions.setTriggerIds(Nest.value(data, "triggerid").asLong());
				triggers = API.TriggerPrototype(idBean, executor).get(tpoptions);
			} else {
				CTriggerGet toptions = new CTriggerGet();
				toptions.setOutput(API_OUTPUT_EXTEND);
				toptions.setSelectHosts(new String[]{"hostid"});
				toptions.setTriggerIds(Nest.value(data,"triggerid").asLong());
				triggers = API.Trigger(idBean, executor).get(toptions);
			}
			Nest.value(data,"trigger").$(reset(triggers));

			// get templates
			String tmp_triggerid = Nest.value(data,"triggerid").asString();
			SqlBuilder sqlParts = null;
			do {
				sqlParts = new SqlBuilder();
				Map db_triggers = DBfetch(DBselect(executor,
					"SELECT t.triggerid,t.templateid,id.parent_itemid,h.name,h.hostid"+
					" FROM triggers t"+
						" LEFT JOIN functions f ON t.tenantid=f.tenantid AND t.triggerid=f.triggerid"+
						" LEFT JOIN items i ON f.tenantid=i.tenantid AND f.itemid=i.itemid"+
						" LEFT JOIN hosts h ON i.tenantid=h.tenantid AND i.hostid=h.hostid"+
						" LEFT JOIN item_discovery id ON i.tenantid=id.tenantid AND i.itemid=id.itemid"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
					    " AND t.triggerid="+sqlParts.marshalParam(tmp_triggerid),
					sqlParts.getNamedParams()
				));
				if (bccomp(Nest.value(data,"triggerid").$(), tmp_triggerid) != 0) {
					// parent trigger prototype link
					String link = null;
					if (!empty(Nest.value(data,"parent_discoveryid").$())) {
						link = "trigger_prototypes.action?form=update&triggerid="+Nest.value(db_triggers,"triggerid").$()+"&parent_discoveryid="+Nest.value(db_triggers,"parent_itemid").$()+"&hostid="+Nest.value(db_triggers,"hostid").$();
					} else {// parent trigger link
						link = "triggers.action?form=update&triggerid="+Nest.value(db_triggers,"triggerid").$()+"&hostid="+Nest.value(db_triggers,"hostid").$();
					}

					Nest.value(data,"templates").asCArray().add(new CLink(
						CHtml.encode(Nest.value(db_triggers,"name").asString()),
						link,
						"highlight underline weight_normal"
					));
					Nest.value(data,"templates").asCArray().add(SPACE+RARR+SPACE);
				}
				tmp_triggerid = Nest.value(db_triggers,"templateid").asString();
			} while (Nest.as(tmp_triggerid).asLong() != 0);
			Nest.value(data,"templates").$(array_reverse(Nest.value(data,"templates").asCArray()));
			array_shift(Nest.value(data,"templates").asCArray());

			Nest.value(data,"limited").$(!empty(Nest.value(data,"trigger","templateid").$()) ? "yes" : null);

			// select first host from triggers if gived not match
			CArray<Map> hosts = Nest.value(data,"trigger","hosts").asCArray();
			if (count(hosts) > 0 && !in_array(map("hostid", Nest.value(data,"hostid").$()), hosts)) {
				Map host = reset(hosts);
				Nest.value(data,"hostid").$(Nest.value(host,"hostid").$());
			}
		}

		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		if ((!empty(Nest.value(data,"triggerid").$()) && !isset(_REQUEST,"form_refresh")) || !empty(Nest.value(data,"limited").$())) {
			Nest.value(data,"expression").$(explode_exp(idBean, executor,Nest.value(data,"trigger","expression").asString()));

			if (empty(Nest.value(data,"limited").$()) || !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"description").$(Nest.value(data,"trigger","description").$());
				Nest.value(data,"type").$(Nest.value(data,"trigger","type").$());
				Nest.value(data,"priority").$(Nest.value(data,"trigger","priority").$());
				Nest.value(data,"status").$(Nest.value(data,"trigger","status").$());
				Nest.value(data,"comments").$(Nest.value(data,"trigger","comments").$());
				Nest.value(data,"url").$(Nest.value(data,"trigger","url").$());

				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> db_triggers = DBselect(executor,
					"SELECT t.triggerid,t.description"+
					" FROM triggers t,trigger_depends d"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
						 " AND t.tenantid=d.tenantid"+
					     " AND t.triggerid=d.triggerid_up"+
						 " AND d.triggerid_down="+sqlParts.marshalParam(Nest.value(data,"triggerid").$()),
					sqlParts.getNamedParams()
				);
				for(Map trigger : db_triggers) {
					if (uint_in_array(Nest.value(trigger,"triggerid").asLong(), (CArray<Long>)Nest.value(data,"dependencies").asCArray())) {
						continue;
					}
					array_push(Nest.value(data,"dependencies").asCArray(), Nest.value(trigger,"triggerid").$());
				}
			}
		}

		if (Nest.value(data,"input_method").asInteger() == IM_TREE) {
			CArray analyze = analyzeExpression(idBean, executor,Nest.value(data,"expression").asString());
			if (!empty(analyze)) {
				Nest.value(data,"outline").$(Nest.value(analyze,0).$());
				Nest.value(data,"eHTMLTree").$(Nest.value(analyze,1).$());
				if (isset(_REQUEST,"expr_action") && Nest.value(data,"eHTMLTree").$() != null) {
					String new_expr = remakeExpression(Nest.value(data,"expression").asString(), Nest.value(_REQUEST,"expr_target_single").asString(),
							Nest.value(_REQUEST,"expr_action").asString(), Nest.value(data,"expr_temp").asString());
					if (new_expr != null) {
						Nest.value(data,"expression").$(new_expr);
						analyze = analyzeExpression(idBean, executor, Nest.value(data,"expression").asString());
						if (!empty(analyze)) {
							Nest.value(data,"outline").$(Nest.value(analyze,0).$());
							Nest.value(data,"eHTMLTree").$(Nest.value(analyze,1).$());
						} else {
							show_messages(false, "", _("Expression Syntax Error."));
						}
						Nest.value(data,"expr_temp").$("");
					} else {
						show_messages(false, "", _("Expression Syntax Error."));
					}
				}
				Nest.value(data,"expression_field_name").$("expr_temp");
				Nest.value(data,"expression_field_value").$(Nest.value(data,"expr_temp").$());
				Nest.value(data,"expression_field_readonly").$("yes");
				Nest.value(data,"expression_field_params").$("this.form.elements[\""+Nest.value(data,"expression_field_name").$()+"\"].value");
				Nest.value(data,"expression_macro_button").$(new CButton("insert_macro", _("Insert macro"), "return call_ins_macro_menu(event);", "formlist"));
				if (Nest.value(data,"limited").asBoolean()) {
					((CButton)Nest.value(data,"expression_macro_button").$()).setAttribute("disabled", "disabled");
				}
			} else {
				show_messages(false, "", _("Expression Syntax Error."));
				Nest.value(data,"input_method").$(IM_ESTABLISHED);
			}
		}
		if (Nest.value(data,"input_method").asInteger() != IM_TREE) {
			Nest.value(data,"expression_field_name").$("expression");
			Nest.value(data,"expression_field_value").$(Nest.value(data,"expression").$());
			Nest.value(data,"expression_field_readonly").$(Nest.value(data,"limited").$());
		}

		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(Nest.array(data,"dependencies").asLong());
			toptions.setOutput(new String[]{"triggerid", "description"});
			toptions.setPreserveKeys(true);
			toptions.setSelectHosts(new String[]{"name"});
			CArray<Map> db_dependencies = API.Trigger(idBean, executor).get(toptions);
			Nest.value(data,"db_dependencies").$(db_dependencies);
			for(Map dependency:db_dependencies) {
				Nest.value(dependency,"host").$(Nest.value(dependency,"hosts", 0, "name").$());
				unset(dependency,"hosts");
			}
			order_result(db_dependencies, "description");
		}
		return data;
	}

	public static CTable get_timeperiod_form() {
		CTable tblPeriod = new CTable(null, "formElementTable");

		// init new_timeperiod variable
		Map new_timeperiod = get_request("new_timeperiod", array());
		boolean isnew = !empty(new_timeperiod);

		if (isnew) {
			if (isset(new_timeperiod,"id")) {
				tblPeriod.addItem(new CVar("new_timeperiod[id]", Nest.value(new_timeperiod,"id").$()));
			}
			if (isset(new_timeperiod,"timeperiodid")) {
				tblPeriod.addItem(new CVar("new_timeperiod[timeperiodid]", Nest.value(new_timeperiod,"timeperiodid").$()));
			}
		}
		if (!isnew) {
			new_timeperiod = array();
			Nest.value(new_timeperiod,"timeperiod_type").$(TIMEPERIOD_TYPE_ONETIME);
		}
		if (!isset(new_timeperiod,"every")) {
			Nest.value(new_timeperiod,"every").$(1);
		}
		if (!isset(new_timeperiod,"day")) {
			Nest.value(new_timeperiod,"day").$(1);
		}
		if (!isset(new_timeperiod,"hour")) {
			Nest.value(new_timeperiod,"hour").$(12);
		}
		if (!isset(new_timeperiod,"minute")) {
			Nest.value(new_timeperiod,"minute").$(0);
		}
		if (!isset(new_timeperiod,"start_date")) {
			Nest.value(new_timeperiod,"start_date").$(0);
		}
		if (!isset(new_timeperiod,"period_days")) {
			Nest.value(new_timeperiod,"period_days").$(0);
		}
		if (!isset(new_timeperiod,"period_hours")) {
			Nest.value(new_timeperiod,"period_hours").$(1);
		}
		if (!isset(new_timeperiod,"period_minutes")) {
			Nest.value(new_timeperiod,"period_minutes").$(0);
		}
		if (!isset(new_timeperiod,"month_date_type")) {
			Nest.value(new_timeperiod,"month_date_type").$(!Nest.value(new_timeperiod,"day").asBoolean());
		}

		// start time
		if (isset(new_timeperiod,"start_time")) {
			Nest.value(new_timeperiod,"hour").$(floor(Nest.value(new_timeperiod,"start_time").asLong() / SEC_PER_HOUR));
			Nest.value(new_timeperiod,"minute").$(floor((Nest.value(new_timeperiod,"start_time").asLong() - (Nest.value(new_timeperiod,"hour").asLong() * SEC_PER_HOUR)) / SEC_PER_MIN));
		}

		// period
		if (isset(new_timeperiod,"period")) {
			Nest.value(new_timeperiod,"period_days").$(floor(Nest.value(new_timeperiod,"period").asLong() / SEC_PER_DAY));
			Nest.value(new_timeperiod,"period_hours").$(floor((Nest.value(new_timeperiod,"period").asLong() - (Nest.value(new_timeperiod,"period_days").asLong() * SEC_PER_DAY)) / SEC_PER_HOUR));
			Nest.value(new_timeperiod,"period_minutes").$(floor((Nest.value(new_timeperiod,"period").asLong() - Nest.value(new_timeperiod,"period_days").asLong() * SEC_PER_DAY - Nest.value(new_timeperiod,"period_hours").asLong() * SEC_PER_HOUR) / SEC_PER_MIN));
		}

		// daysofweek
		StringBuilder dayofweeksb = new StringBuilder();
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_mo") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_tu") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_we") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_th") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_fr") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_sa") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_su") ? "0" : "1");
		String dayofweek = null;
		if (isset(new_timeperiod,"dayofweek")) {
			dayofweek = rda_num2bitstr(Nest.value(new_timeperiod,"dayofweek").asLong(), true);
		} else {
			dayofweek = dayofweeksb.toString();
		}

		Nest.value(new_timeperiod,"dayofweek_mo").$(dayofweek.charAt(0));
		Nest.value(new_timeperiod,"dayofweek_tu").$(dayofweek.charAt(1));
		Nest.value(new_timeperiod,"dayofweek_we").$(dayofweek.charAt(2));
		Nest.value(new_timeperiod,"dayofweek_th").$(dayofweek.charAt(3));
		Nest.value(new_timeperiod,"dayofweek_fr").$(dayofweek.charAt(4));
		Nest.value(new_timeperiod,"dayofweek_sa").$(dayofweek.charAt(5));
		Nest.value(new_timeperiod,"dayofweek_su").$(dayofweek.charAt(6));

		// months
		StringBuilder monthsb = new StringBuilder();
		monthsb.append(!isset(new_timeperiod,"month_jan") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_feb") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_mar") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_apr") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_may") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_jun") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_jul") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_aug") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_sep") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_oct") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_nov") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_dec") ? "0" : "1");
		String month = null;
		if (isset(new_timeperiod,"month")) {
			month = rda_num2bitstr(Nest.value(new_timeperiod,"month").asLong(), true);
		} else {
			month = monthsb.toString();
		}

		Nest.value(new_timeperiod,"month_jan").$(month.charAt(0));
		Nest.value(new_timeperiod,"month_feb").$(month.charAt(1));
		Nest.value(new_timeperiod,"month_mar").$(month.charAt(2));
		Nest.value(new_timeperiod,"month_apr").$(month.charAt(3));
		Nest.value(new_timeperiod,"month_may").$(month.charAt(4));
		Nest.value(new_timeperiod,"month_jun").$(month.charAt(5));
		Nest.value(new_timeperiod,"month_jul").$(month.charAt(6));
		Nest.value(new_timeperiod,"month_aug").$(month.charAt(7));
		Nest.value(new_timeperiod,"month_sep").$(month.charAt(8));
		Nest.value(new_timeperiod,"month_oct").$(month.charAt(9));
		Nest.value(new_timeperiod,"month_nov").$(month.charAt(10));
		Nest.value(new_timeperiod,"month_dec").$(month.charAt(11));

		String bit_dayofweek = rda_str_revert(dayofweek);
		String bit_month = rda_str_revert(month);

		CComboBox cmbType = new CComboBox("new_timeperiod[timeperiod_type]", Nest.value(new_timeperiod,"timeperiod_type").$(), "submit()");
		cmbType.addItem(TIMEPERIOD_TYPE_ONETIME, _("One time only"));
		cmbType.addItem(TIMEPERIOD_TYPE_DAILY, _("Daily"));
		cmbType.addItem(TIMEPERIOD_TYPE_WEEKLY, _("Weekly"));
		cmbType.addItem(TIMEPERIOD_TYPE_MONTHLY, _("Monthly"));

		tblPeriod.addRow(array(_("Period type"), cmbType));

		if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY) {
			tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month)));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			tblPeriod.addRow(array(_("Every day(s)"), new CNumericBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString(), 3)));
		} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month)));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			tblPeriod.addRow(array(_("Every week(s)"), new CNumericBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString(), 2)));

			CTable tabDays = new CTable();
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_mo]", Nest.as(dayofweek.charAt(0)).asBoolean(), null, 1), _("Monday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_tu]", Nest.as(dayofweek.charAt(1)).asBoolean(), null, 1), _("Tuesday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_we]", Nest.as(dayofweek.charAt(2)).asBoolean(), null, 1), _("Wednesday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_th]", Nest.as(dayofweek.charAt(3)).asBoolean(), null, 1), _("Thursday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_fr]", Nest.as(dayofweek.charAt(4)).asBoolean(), null, 1), _("Friday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_sa]", Nest.as(dayofweek.charAt(5)).asBoolean(), null, 1), _("Saturday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_su]", Nest.as(dayofweek.charAt(6)).asBoolean(), null, 1), _("Sunday")));
			tblPeriod.addRow(array(_("Day of week"), tabDays));
		} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));

			CTable tabMonths = new CTable();
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_jan]", Nest.as(month.charAt(0)).asBoolean(), null, 1), _("January"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_jul]", Nest.as(month.charAt(6)).asBoolean(), null, 1), _("July")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_feb]", Nest.as(month.charAt(1)).asBoolean(), null, 1), _("February"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_aug]", Nest.as(month.charAt(7)).asBoolean(), null, 1), _("August")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_mar]", Nest.as(month.charAt(2)).asBoolean(), null, 1), _("March"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_sep]", Nest.as(month.charAt(8)).asBoolean(), null, 1), _("September")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_apr]", Nest.as(month.charAt(3)).asBoolean(), null, 1), _("April"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_oct]", Nest.as(month.charAt(9)).asBoolean(), null, 1), _("October")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_may]", Nest.as(month.charAt(4)).asBoolean(), null, 1), _("May"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_nov]", Nest.as(month.charAt(10)).asBoolean(), null, 1), _("November")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_jun]", Nest.as(month.charAt(5)).asBoolean(), null, 1), _("June"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_dec]", Nest.as(month.charAt(11)).asBoolean(), null, 1), _("December")
			));
			tblPeriod.addRow(array(_("Month"), tabMonths));

			tblPeriod.addRow(array(_("Date"), array(
				new CRadioButton("new_timeperiod[month_date_type]", "0", null, null, !Nest.value(new_timeperiod,"month_date_type").asBoolean(), "submit()"),
				_("Day"),
				SPACE,
				new CRadioButton("new_timeperiod[month_date_type]", "1", null, null, Nest.value(new_timeperiod,"month_date_type").asBoolean(), "submit()"),
				_("Day of week")))
			);

			if (Nest.value(new_timeperiod,"month_date_type").asInteger() > 0) {
				tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));

				CComboBox cmbCount = new CComboBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString());
				cmbCount.addItem(1, _("First"));
				cmbCount.addItem(2, _("Second"));
				cmbCount.addItem(3, _("Third"));
				cmbCount.addItem(4, _("Fourth"));
				cmbCount.addItem(5, _("Last"));

				CCol td = new CCol(cmbCount);
				td.setColSpan(2);

				CTable tabDays = new CTable();
				tabDays.addRow(td);
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_mo]", Nest.as(dayofweek.charAt(0)).asBoolean(), null, 1), _("Monday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_tu]", Nest.as(dayofweek.charAt(1)).asBoolean(), null, 1), _("Tuesday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_we]", Nest.as(dayofweek.charAt(2)).asBoolean(), null, 1), _("Wednesday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_th]", Nest.as(dayofweek.charAt(3)).asBoolean(), null, 1), _("Thursday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_fr]", Nest.as(dayofweek.charAt(4)).asBoolean(), null, 1), _("Friday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_sa]", Nest.as(dayofweek.charAt(5)).asBoolean(), null, 1), _("Saturday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_su]", Nest.as(dayofweek.charAt(6)).asBoolean(), null, 1), _("Sunday")));
				tblPeriod.addRow(array(_("Day of week"), tabDays));
			} else {
				tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));
				tblPeriod.addRow(array(_("Day of month"), new CNumericBox("new_timeperiod[day]", Nest.value(new_timeperiod,"day").asString(), 2)));
			}
		} else {
			tblPeriod.addItem(new CVar("new_timeperiod[every]", Nest.value(new_timeperiod,"every").$(), "new_timeperiod_every_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month), "new_timeperiod_month_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$(), "new_timeperiod_day_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[hour]", Nest.value(new_timeperiod,"hour").$(), "new_timeperiod_hour_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[minute]", Nest.value(new_timeperiod,"minute").$(), "new_timeperiod_minute_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));

			CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
			Object date = null;
			if (isset(Nest.value(_REQUEST,"add_timeperiod").$())) {
				date = map(
					"y" , get_request("new_timeperiod_start_date_year"),
					"m" , get_request("new_timeperiod_start_date_month"),
					"d" , get_request("new_timeperiod_start_date_day"),
					"h" , get_request("new_timeperiod_start_date_hour"),
					"i" , get_request("new_timeperiod_start_date_minute")
				);
			} else {
				date = rdaDateToTime(!empty(Nest.value(new_timeperiod,"start_date").$())
					? Nest.value(new_timeperiod,"start_date").asString() : date(TIMESTAMP_FORMAT_ZERO_TIME, time()));
			}

			tblPeriod.addRow(array(_("Date"), createDateSelector("new_timeperiod_start_date", Nest.as(date).asLong())));
		}

		if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() != TIMEPERIOD_TYPE_ONETIME) {
			tblPeriod.addRow(array(_("At (hour:minute)"), array(
				new CNumericBox("new_timeperiod[hour]", Nest.value(new_timeperiod,"hour").asString(), 2),
				":",
				new CNumericBox("new_timeperiod[minute]", Nest.value(new_timeperiod,"minute").asString(), 2)))
			);
		}

		CComboBox perHours = new CComboBox("new_timeperiod[period_hours]", Nest.value(new_timeperiod,"period_hours").asString(), null, range(0, 23));
		CComboBox perMinutes = new CComboBox("new_timeperiod[period_minutes]", Nest.value(new_timeperiod,"period_minutes").asString(), null, range(0, 59));
		tblPeriod.addRow(array(
			_("Maintenance period length"),
			array(
				new CNumericBox("new_timeperiod[period_days]", Nest.value(new_timeperiod,"period_days").asString(), 3),
				_("Days")+SPACE+SPACE,
				perHours,
				_("Hours")+SPACE+SPACE,
				perMinutes,
				_("Minutes")
		)));

		return tblPeriod;
	}
	
}
