package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.bindec;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_EXPIRED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_value2array;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.MaintenancesUtil.get_maintenance_by_maintenanceid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.validateDateInterval;
import static com.isoft.iradar.inc.ValidateUtil.validateDateTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MaintenanceAction extends RadarBaseAction {
	
	private CArray<Map> dbMaintenances = null;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of maintenance periods"));
		page("file", "maintenance.action");
		page("hist_arg", new String[] { "groupid" });
		page("scripts", new String[] { "class.calendar.js" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts" ,												array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groups" ,												array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostids" ,												array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groupids" ,											array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groupid" ,											array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// maintenance
			"maintenanceid" ,									array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"maintenanceids" ,								array(T_RDA_INT, O_OPT, P_SYS,	DB_ID, 		null),
			"mname" ,											array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"maintenance_type" ,							array(T_RDA_INT, O_OPT, null,	null,		"isset({save})"),
			"description" ,										array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"active_since" ,										array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till" ,											array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_since_day" ,								array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_since_month" ,							array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_since_year" ,								array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_since_hour" ,								array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_since_minute" ,							array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_till_day" ,									array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_till_month" ,								array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_till_year" ,									array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_till_hour" ,									array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"active_till_minute" ,								array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_day" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_month" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_year" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_hour" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_minute" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod" ,								array(T_RDA_STR, O_OPT, null,	null,		"isset({add_timeperiod})"),
			"timeperiods" ,										array(T_RDA_STR, O_OPT, null,	null,		null),
			"del_timeperiodid" ,								array(null,      O_OPT, P_ACT,	NOT_EMPTY,		null),
			"edit_timeperiodid" ,								array(null,      O_OPT, P_ACT,	NOT_EMPTY,		null),
			"twb_groupid" ,									array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// actions
			"go" ,													array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_timeperiod" ,								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_timeperiod" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,													array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,												array(T_RDA_STR, O_OPT, P_SYS,		 null,	null),
			// form
			"form" ,												array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,									array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (isset(_REQUEST,"maintenanceid")) {
			CMaintenanceGet option = new CMaintenanceGet();
			option.setOutput(API_OUTPUT_EXTEND);
			option.setSelectTimeperiods(API_OUTPUT_EXTEND);
			option.setEditable(true);
			option.setMaintenanceIds(get_request_asLong("maintenanceid"));
			dbMaintenances = API.Maintenance(getIdentityBean(), executor).get(option);
			if (empty(dbMaintenances)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go") && (!isset(_REQUEST,"maintenanceids") || !isArray(Nest.value(_REQUEST,"maintenanceids").$()))) {
			access_deny();
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"maintenanceid")) {
			unset(_REQUEST,"maintenanceid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"cancel_new_timeperiod")) {
			unset(_REQUEST,"new_timeperiod");
		} else if (isset(_REQUEST,"save")) {
			String msg1,msg2;
			if (isset(_REQUEST,"maintenanceid")) {
				msg1 = _("Maintenance updated");
				msg2 = _("Cannot update maintenance");
			} else {
				msg1 = _("Maintenance added");
				msg2 = _("Cannot add maintenance");
			}

			boolean result = true;

			if (!validateDateTime(Nest.value(_REQUEST,"active_since_year").asInteger(),
					Nest.value(_REQUEST,"active_since_month").asInteger(),
					Nest.value(_REQUEST,"active_since_day").asInteger(),
					Nest.value(_REQUEST,"active_since_hour").asInteger(),
					Nest.value(_REQUEST,"active_since_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("Active since")));
				result = false;
			}
			if (!validateDateInterval(Nest.value(_REQUEST,"active_since_year").asInteger(),
					Nest.value(_REQUEST,"active_since_month").asInteger(),
					Nest.value(_REQUEST,"active_since_day").asInteger())) {
				info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active since")));
				result = false;
			}
			if (!validateDateTime(Nest.value(_REQUEST,"active_till_year").asInteger(),
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("Active till")));
				result = false;
			}
			if (!validateDateInterval(Nest.value(_REQUEST,"active_till_year").asInteger(), Nest.value(_REQUEST,"active_till_month").asInteger(), Nest.value(_REQUEST,"active_till_day").asInteger())) {
				info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active till")));
				result = false;
			}

			if (result) {
				Long activeSince = null;
				if (isset(_REQUEST,"active_since")) {
					activeSince = mktime(
						Nest.value(_REQUEST,"active_since_hour").asInteger(),
						Nest.value(_REQUEST,"active_since_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_since_month").asInteger(),
						Nest.value(_REQUEST,"active_since_day").asInteger(),
						Nest.value(_REQUEST,"active_since_year").asInteger()
					);
				}
				Long activeTill = null;
				if (isset(_REQUEST,"active_till")) {
					activeTill = mktime(
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_year").asInteger()
					);
				}

				final Map maintenance = map(
					"name", Nest.value(_REQUEST,"mname").$(),
					"maintenance_type", Nest.value(_REQUEST,"maintenance_type").$(),
					"description", Nest.value(_REQUEST,"description").$(),
					"active_since", activeSince,
					"active_till", activeTill,
					"timeperiods", get_request("timeperiods", array()),
					"hostids", get_request("hostids", array()),
					"groupids", get_request("groupids", array())
				);

				if (isset(_REQUEST,"maintenanceid")) {
					Nest.value(maintenance,"maintenanceid").$(Nest.value(_REQUEST,"maintenanceid").$());
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Maintenance(getIdentityBean(), executor).update(array(maintenance)));
						}
					});
				} else {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Maintenance(getIdentityBean(), executor).create(array(maintenance)));
						}
					});
				}
			}

			if (result) {
				add_audit(getIdentityBean(), executor, !isset(_REQUEST,"maintenanceid") ? AUDIT_ACTION_ADD : AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_MAINTENANCE, _("Name")+NAME_DELIMITER+Nest.value(_REQUEST,"mname").asString());
				unset(_REQUEST,"form");
			}

			show_messages(result, msg1, msg2);
			clearCookies(result);
		} else if (isset(_REQUEST,"delete") || "delete".equals(Nest.value(_REQUEST,"go").$())) {
			final CArray<String> maintenanceids;
			if (isset(_REQUEST,"maintenanceids")) {
				maintenanceids = Nest.value(_REQUEST,"maintenanceids").asCArray();
			} else {
				maintenanceids = get_request("maintenanceid", array());
			}

			rda_value2array(maintenanceids);
			
			DBstart(executor);

			CArray<Map> maintenances = array();
			for(String maintenanceid : maintenanceids) {
				Nest.value(maintenances,maintenanceid).$(get_maintenance_by_maintenanceid(getIdentityBean(), executor, maintenanceid));
			}

			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Maintenance(getIdentityBean(), executor).delete(maintenanceids.valuesAsLong()));
				}
			});
			if (goResult) {
				for (Entry<Object, Map> e : maintenances.entrySet()) {
				    Object maintenanceid = e.getKey();
				    Map maintenance = e.getValue();
					add_audit(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_MAINTENANCE, "Id ["+maintenanceid+"] "+_("Name")+" ["+Nest.value(maintenance,"name").asString()+"]");
				}
				unset(_REQUEST,"form");
				unset(_REQUEST,"maintenanceid");
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("Maintenance deleted"), _("Cannot delete maintenance"));
			clearCookies(goResult);
		} else if (isset(_REQUEST,"add_timeperiod") && isset(_REQUEST,"new_timeperiod")) {
			Map new_timeperiod = Nest.value(_REQUEST,"new_timeperiod").asCArray();
			if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME) {
				Nest.value(new_timeperiod,"start_date").$(mktime(Nest.value(_REQUEST,"new_timeperiod_start_date_hour").asInteger(),
					Nest.value(_REQUEST,"new_timeperiod_start_date_minute").asInteger(),
					0,
					Nest.value(_REQUEST,"new_timeperiod_start_date_month").asInteger(),
					Nest.value(_REQUEST,"new_timeperiod_start_date_day").asInteger(),
					Nest.value(_REQUEST,"new_timeperiod_start_date_year").asInteger()));
			}

			// start time
			Nest.value(new_timeperiod,"start_time").$((Nest.value(new_timeperiod,"hour").asInteger() * SEC_PER_HOUR) + (Nest.value(new_timeperiod,"minute").asInteger() * SEC_PER_MIN));

			// period
			Nest.value(new_timeperiod,"period").$( (Nest.value(new_timeperiod,"period_days").asInteger() * SEC_PER_DAY) + (Nest.value(new_timeperiod,"period_hours").asInteger() * SEC_PER_HOUR) + (Nest.value(new_timeperiod,"period_minutes").asInteger() * SEC_PER_MIN));

			// days of week
			if (!isset(new_timeperiod,"dayofweek")) {
				String dayofweek =  (!isset(new_timeperiod,"dayofweek_su")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_sa")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_fr")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_th")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_we")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_tu")) ? "0" : "1";
				dayofweek += (!isset(new_timeperiod,"dayofweek_mo")) ? "0" : "1";
				Nest.value(new_timeperiod,"dayofweek").$(bindec(dayofweek));
			}

			// months
			if (!isset(new_timeperiod,"month")) {
				String month =  (!isset(new_timeperiod,"month_dec")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_nov")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_oct")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_sep")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_aug")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_jul")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_jun")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_may")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_apr")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_mar")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_feb")) ? "0" : "1";
				month += (!isset(new_timeperiod,"month_jan")) ? "0" : "1";
				Nest.value(new_timeperiod,"month").$(bindec(month));
			}

			if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
				if (Nest.value(new_timeperiod,"month_date_type").asInteger() > 0) {
					Nest.value(new_timeperiod,"day").$(0);
				} else {
					Nest.value(new_timeperiod,"every").$(0);
					Nest.value(new_timeperiod,"dayofweek").$(0);
				}
			}

			Nest.value(_REQUEST,"timeperiods").$(get_request("timeperiods", array()));

			boolean result = false;
			if (Nest.value(new_timeperiod,"period").asInteger() < 300) {
				info(_("Incorrect maintenance period (minimum 5 minutes)"));
			} else if (Nest.value(new_timeperiod,"hour").asInteger() > 23 || Nest.value(new_timeperiod,"minute").asInteger() > 59) {
				info(_("Incorrect maintenance period"));
			} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME) {
				if (!validateDateTime(Nest.value(_REQUEST,"new_timeperiod_start_date_year").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_month").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_day").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_hour").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_minute").asInteger())) {
					error(_("Invalid maintenance period"));
				} else if (!validateDateInterval(Nest.value(_REQUEST,"new_timeperiod_start_date_year").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_month").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_day").asInteger())) {
					error(_("Incorrect maintenance - date must be between 2010.01.01 and 2038.01.01"));
				} else {
					result = true;
				}
			} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY && Nest.value(new_timeperiod,"every").asInteger() < 1) {
				info(_("Incorrect maintenance day period"));
			} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
				if (Nest.value(new_timeperiod,"every").asInteger() < 1) {
					info(_("Incorrect maintenance week period"));
				} else if (Nest.value(new_timeperiod,"dayofweek").asInteger() < 1) {
					info(_("Incorrect maintenance days of week"));
				} else {
					result = true;
				}
			} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
				if (Nest.value(new_timeperiod,"month").asInteger() < 1) {
					info(_("Incorrect maintenance month period"));
				} else if (Nest.value(new_timeperiod,"day").asInteger() == 0 && Nest.value(new_timeperiod,"dayofweek").asInteger() < 1) {
					info(_("Incorrect maintenance days of week"));
				} else if ((Nest.value(new_timeperiod,"day").asInteger() < 1 || Nest.value(new_timeperiod,"day").asInteger() > 31) && Nest.value(new_timeperiod,"dayofweek").asInteger() == 0) {
					info(_("Incorrect maintenance date"));
				} else {
					result = true;
				}
			} else {
				result = true;
			}
			show_messages();

			if (result) {
				if (!isset(new_timeperiod,"id")) {
					if (!str_in_array(new_timeperiod, Nest.value(_REQUEST,"timeperiods").asCArray())) {
						array_push(Nest.value(_REQUEST,"timeperiods").asCArray(), new_timeperiod);
					}
				} else {
					Object id = Nest.value(new_timeperiod,"id").$();
					unset(new_timeperiod,"id");
					Nest.value(_REQUEST,"timeperiods",id).$(new_timeperiod);
				}
				unset(_REQUEST,"new_timeperiod");
			}
		} else if (isset(_REQUEST,"del_timeperiodid")) {
			Nest.value(_REQUEST,"timeperiods").$(get_request("timeperiods", array()));
			CArray delTimeperiodIds = array_keys(Nest.value(_REQUEST,"del_timeperiodid").asCArray());
			Object delTimeperiodId = reset(delTimeperiodIds);
			unset(Nest.value(_REQUEST,"timeperiods").asCArray(),delTimeperiodId);
		} else if (isset(_REQUEST,"edit_timeperiodid")) {
			Nest.value(_REQUEST,"edit_timeperiodid").$(array_keys(Nest.value(_REQUEST,"edit_timeperiodid").asCArray()));
			Object edit_timeperiodid = array_pop(Nest.value(_REQUEST,"edit_timeperiodid").asCArray());
			Nest.value(_REQUEST,"edit_timeperiodid").$(edit_timeperiodid);
			Nest.value(_REQUEST,"timeperiods").$(get_request("timeperiods", array()));

			if (isset(Nest.value(_REQUEST,"timeperiods",edit_timeperiodid).$())) {
				Nest.value(_REQUEST,"new_timeperiod").$(Nest.value(_REQUEST,"timeperiods",edit_timeperiodid).$());
				Nest.value(_REQUEST,"new_timeperiod","id").$(edit_timeperiodid);
				Nest.value(_REQUEST,"new_timeperiod","start_date").$(Nest.value(_REQUEST,"timeperiods",edit_timeperiodid,"start_date").$());
			}
		}

		CArray options = map(
			"groups" , map("editable" , true),
			"groupid" , get_request("groupid", null)
		);
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor,options);
		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").asString());

		/* Display */
		CArray data = map(
			"form" , get_request("form")
		);

		if (!empty(Nest.value(data, "form").$())) {
			doRenderFormView(executor, data);
		} else {
			doRenderListView(executor, pageFilter, data);
		}
	}
	
	private void doRenderFormView(SQLExecutor executor,CArray data){
		Nest.value(data,"maintenanceid").$(get_request("maintenanceid"));
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		if (isset(data,"maintenanceid") && !isset(_REQUEST,"form_refresh")) {
			Map dbMaintenance = reset(dbMaintenances);
			Nest.value(data,"mname").$(Nest.value(dbMaintenance,"name").$());
			Nest.value(data,"maintenance_type").$(Nest.value(dbMaintenance,"maintenance_type").$());
			Nest.value(data,"active_since").$(Nest.value(dbMaintenance,"active_since").$());
			Nest.value(data,"active_till").$(Nest.value(dbMaintenance,"active_till").$());
			Nest.value(data,"description").$(Nest.value(dbMaintenance,"description").$());

			// time periods
			Nest.value(data,"timeperiods").$(Nest.value(dbMaintenance,"timeperiods").$());
			CArrayHelper.sort(Nest.value(data,"timeperiods").asCArray(), array("timeperiod_type", "start_date"));

			// get hosts
			CHostGet option = new CHostGet();
			option.setMaintenanceIds(Nest.array(data,"maintenanceid").asLong());
			option.put("real_hosts", true);
			option.setOutput(new String[]{"hostid"});
			option.setEditable(true);
			Nest.value(data,"hostids").$(API.Host(getIdentityBean(), executor).get(option));
			Nest.value(data,"hostids").$(rda_objectValues(Nest.value(data,"hostids").$(), "hostid"));

			// get groupids
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setMaintenanceIds(Nest.array(data,"maintenanceid").asLong());
			hgoptions.setRealHosts(true);
			hgoptions.setOutput(new String[]{"groupid"});
			hgoptions.setEditable(true);
			Nest.value(data,"groupids").$(API.HostGroup(getIdentityBean(), executor).get(hgoptions));
			Nest.value(data,"groupids").$(rda_objectValues(Nest.value(data,"groupids").$(), "groupid"));
		} else {
			Nest.value(data,"mname").$(get_request("mname", ""));
			Nest.value(data,"maintenance_type").$(get_request("maintenance_type", 0));
			if (isset(_REQUEST,"active_since")) {
				Nest.value(data,"active_since").$(mktime(Nest.value(_REQUEST,"active_since_hour").asInteger(),
						Nest.value(_REQUEST,"active_since_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_since_month").asInteger(),
						Nest.value(_REQUEST,"active_since_day").asInteger(),
						Nest.value(_REQUEST,"active_since_year").asInteger()));
			} else {
				Nest.value(data,"active_since").$(strtotime("today"));
			}
			if (isset(_REQUEST,"active_till")) {
				Nest.value(data,"active_till").$(mktime(Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_year").asInteger()));
			} else {
				Nest.value(data,"active_till").$(strtotime("tomorrow"));
			}
			Nest.value(data,"description").$(get_request("description", ""));
			Nest.value(data,"timeperiods").$(get_request("timeperiods", array()));
			Nest.value(data,"hostids").$(get_request("hostids", array()));
			Nest.value(data,"groupids").$(get_request("groupids", array()));
		}

		// get groups
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setEditable(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setRealHosts(true);
		hgoptions.setPreserveKeys(true);
		CArray<Map> all_groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		Nest.value(data,"all_groups").$(all_groups);
		order_result(all_groups, "name");

		Nest.value(data,"twb_groupid").$(get_request("twb_groupid", 0));
		if (!isset(Nest.value(data,"all_groups").asCArray(),data.get("twb_groupid"))) {
			Map twb_groupid = reset(Nest.value(data,"all_groups").asCArray());
			Nest.value(data,"twb_groupid").$(Nest.value(twb_groupid,"groupid").$());
		}

		// get hosts from selected twb group
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "name"});
		hoptions.put("real_hosts", true);
		hoptions.setEditable(true);
		hoptions.setGroupIds(Nest.array(data,"twb_groupid").asLong());
		Nest.value(data,"hosts").$(API.Host(getIdentityBean(), executor).get(hoptions));

		// selected hosts
		hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid", "name"});
		hoptions.put("real_hosts", true);
		hoptions.setEditable(true);
		hoptions.setHostIds(Nest.array(data,"hostids").asLong());
		CArray<Map> hostsSelected = API.Host(getIdentityBean(), executor).get(hoptions);
		
		Nest.value(data,"hosts").$(array_merge(Nest.value(data,"hosts").asCArray(), hostsSelected));
		Nest.value(data,"hosts").$(rda_toHash(Nest.value(data,"hosts").asCArray(), "hostid"));
		order_result(Nest.value(data,"hosts").asCArray(), "name");

		// render view
		CView maintenanceView = new CView("configuration.maintenance.edit", data);
		maintenanceView.render(getIdentityBean(), executor);
		maintenanceView.show();
	}

	private void doRenderListView(SQLExecutor executor, CPageFilter pageFilter, CArray data) {
		String sortfield = getPageSortField(getIdentityBean(), executor,"name");
		String sortorder = getPageSortOrder(getIdentityBean(), executor);
		Map<String, Object> config = select_config(getIdentityBean(), executor);
		
		// get only maintenance IDs for paging
		CMaintenanceGet options = new CMaintenanceGet();
		options.setOutput(new String[]{"maintenanceid"});
		options.setEditable(true);
		options.setSortfield(sortfield);
		options.setSortorder(sortorder);
		options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);

		if (pageFilter.$("groupsSelected").asBoolean()){
			if (pageFilter.$("groupid").asInteger() > 0) {
				options.setGroupIds(pageFilter.$("groupid").asLong());
			} else {
				options.setGroupIds(pageFilter.$("groups").asCArray().keysAsLong());
			}
		} else {
			options.setGroupIds(new Long[0]);
		}

		CArray<Map> maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
		Nest.value(data,"maintenances").$(maintenances);
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, maintenances, array("maintenanceid")));

		// get list of maintenances
		options = new CMaintenanceGet();
		options.setMaintenanceIds(rda_objectValues(maintenances, "maintenanceid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
		Nest.value(data,"maintenances").$(maintenances);

		for (Entry<Object, Map> e : maintenances.entrySet()) {
            //Object number = e.getKey();
            Map maintenance = e.getValue();
			if (Nest.value(maintenance,"active_till").asLong() < time()) {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_EXPIRED);
			} else if (Nest.value(maintenance,"active_since").asLong() > time()) {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_APPROACH);
			} else {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_ACTIVE);
			}
		}
		order_result(maintenances, sortfield, sortorder);

		Nest.value(data,"pageFilter").$(pageFilter);

		// render view
		CView maintenanceView = new CView("configuration.maintenance.list", data);
		maintenanceView.render(getIdentityBean(), executor);
		maintenanceView.show();
	}
	
}
