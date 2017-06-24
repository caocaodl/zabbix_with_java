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
import static com.isoft.iradar.inc.ValidateUtil.maxTill;
import static com.isoft.iradar.inc.ValidateUtil.minSince;
import static com.isoft.iradar.inc.ValidateUtil.validateDateInterval;
import static com.isoft.iradar.inc.ValidateUtil.validateDateTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
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
		page("css", new String[] { "lessor/devicecenter/maintenance.css" });
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
			if (!validateDateTime(Nest.value(_REQUEST,"active_till_year").asInteger(),
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("Active till")));
				result = false;
			}

			if (result) {
				Long activeSince = null;//生效时间
				if (isset(_REQUEST,"active_since")) {
					activeSince = mktime(
						Nest.value(_REQUEST,"active_since_hour").asInteger(),
						Nest.value(_REQUEST,"active_since_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_since_month").asInteger(),
						Nest.value(_REQUEST,"active_since_day").asInteger(),
						Nest.value(_REQUEST,"active_since_year").asInteger()
					);

					if (activeSince < minSince) {
						info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active since")));
						result = false;
					}
				}
				Long activeTill = null;//结束时间
				if (isset(_REQUEST,"active_till")) {
					activeTill = mktime(
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_year").asInteger()
					);
					if (activeTill > maxTill) {
						info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active till")));
						result = false;
					}
				}
				
				if(isset(_REQUEST,"active_till") && isset(_REQUEST,"active_since")){
					if(activeSince >= activeTill){
						info(_s("timeMessage"));
						result = false;
					}
				}
				
				for(Map timeperiod: (CArray<Map>)get_request("timeperiods", array())){
					if(Nest.value(timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME){
					   	  if(Nest.value(timeperiod, "start_date").asLong() < activeSince){
					          info( _("Low Start_date"));
					   		  result = false;
					   		  break;
					   	  }else if(Nest.value(timeperiod, "start_date").asLong() > activeTill){
					   		  info(_("Beyond Time_until"));
					   		  result = false;
					   		  break;
					   	  }
					}
					
				}

				if (result) {
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

		/*CArray options = map(//删除设备类型选项，因为没有设备类型的维护查询不到
			"groups" , map("editable" , true),
			"groupid" , get_request("groupid", null)
		);
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor,options);
		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").asString());*/

		/* Display */
		CArray data = map(
			"form" , get_request("form")
		);

		if (!empty(Nest.value(data, "form").$())) {
			doRenderFormView(executor, data);
		} else {
			doRenderListView(executor, data);
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
//			hgoptions.setRealHosts(true);
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
//		hgoptions.setRealHosts(true);
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

	private void doRenderListView(SQLExecutor executor, CArray data) {
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
		CArray<Map> maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
		
		/*if (pageFilter.$("groupsSelected").asBoolean()){
			if (pageFilter.$("groupid").asInteger() > 0) {
				options.setGroupIds(pageFilter.$("groupid").asLong());
			} else {
				options.setGroupIds(pageFilter.$("groups").asCArray().keysAsLong());
			}
		} else {
			options.setGroupIds(new Long[0]);
		}
	
		if (pageFilter.$("groupid").asInteger() > 0) {
			CArray<Map> maintenanc=new CArray<Map>();
			maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
			for (Entry<Object, Map> e : maintenances.entrySet()) {
				Map group = e.getValue();
				CArray<Map> groups=Nest.value(group, "groups").asCArray();
				for (Entry<Object, Map> f : groups.entrySet()) {
					Map groupid =f.getValue();
					Long guoupId=Nest.value(groupid, "groupid").asLong();
					if(pageFilter.$("groupid").asLong()==guoupId){
						maintenanc.push(group);						
					}
				}	
			}
			maintenances=maintenanc;
		}else{
			 maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
		}*/
		Nest.value(data,"maintenances").$(maintenances);
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, maintenances, array("maintenanceid")));

		// get list of maintenances
		options = new CMaintenanceGet();
		options.setMaintenanceIds(rda_objectValues(maintenances, "maintenanceid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectGroups(new String[]{"groupid"});
		options.setSelectHosts(new String[]{"hostid"});
		maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
		Nest.value(data,"maintenances").$(maintenances);

		for (Map maintenance : maintenances) {
			if (Nest.value(maintenance,"active_till").asLong() < time()) {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_EXPIRED);
			} else if (Nest.value(maintenance,"active_since").asLong() > time()) {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_APPROACH);
			} else {
				Nest.value(maintenance,"status").$(MAINTENANCE_STATUS_ACTIVE);
			}

			//下次运维时间
			try {
				getNextTime(executor, maintenance);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			
			//设置维护数目
			getNum(executor, maintenance);
			
		}
		order_result(maintenances, sortfield, sortorder);

		//Nest.value(data,"pageFilter").$(pageFilter);

		// render view
		CView maintenanceView = new CView("configuration.maintenance.list", data);
		maintenanceView.render(getIdentityBean(), executor);
		maintenanceView.show();
	}
	
	/**
	 * 设置数目
	 */
	public Map getNum(SQLExecutor executor, Map maintenance){	

		List<String> hostidList = new ArrayList<String>();
		List<String> tempList = new ArrayList<String>();
		
		//维护中设备
		Long[] hostIds = rda_objectValues(Nest.value(maintenance,"hosts").asCArray(), "hostid").valuesAsLong();
		for(Long hostId: hostIds){
			hostidList.add(hostId.toString());
		}
		//维护中设备组
		Long[] groupIds = rda_objectValues(Nest.value(maintenance, "groups").asCArray(), "groupid").valuesAsLong();
		CHostGroupGet options = new CHostGroupGet();
		options.setGroupIds(groupIds);
		options.setSelectHosts(new String[]{"hostid"});
		CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(options);
		
		for (Map hostGroup : hostGroups) {
			CArray<Map> hosts = Nest.value(hostGroup, "hosts").asCArray();
			for(Map host: hosts){
				hostidList.add(Nest.value(host,"hostid").$().toString());
			}
		}
		//去重
		for(String hostId: hostidList){
			if(!tempList.contains(hostId)){
				tempList.add(hostId);
			}
		}
		
		Nest.value(maintenance, "hostNum").$(tempList.size());
		
		return maintenance;
	}
	
	/**
	 * 下次运维时间
	 * @throws ParseException 
	 */
	public Map getNextTime(SQLExecutor executor, Map maintenance) throws ParseException{
		int status = Nest.value(maintenance,"status").asInteger();
		long active_since = Nest.value(maintenance,"active_since").asLong();
		long active_till_1 = Nest.value(maintenance,"active_till").asLong()+1;//加1 处理运行时间恰为结束时间
		//假设最小时间为结束时间
		long minTime = active_till_1;
		
		if(status == MAINTENANCE_STATUS_EXPIRED){ //过期
			
		}else{ //未开始与运行中		
			CMaintenanceGet options = new CMaintenanceGet();
			options.setMaintenanceIds(Nest.value(maintenance, "maintenanceid").asLong());
			options.setSelectTimeperiods(API_OUTPUT_EXTEND);
			CArray<CArray> maintenances = API.Maintenance(getIdentityBean(), executor).get(options);
			
			CArray<Map> timePeriods = Nest.value(reset(maintenances), "timeperiods").asCArray();
			for(Map timePeriod: timePeriods){
				int periodType = Nest.value(timePeriod, "timeperiod_type").asInteger();
				long start_time = Nest.value(timePeriod, "start_time").asLong();
				long start_date = Nest.value(timePeriod, "start_date").asLong();
				int every = Nest.value(timePeriod, "every").asInteger();
				//int dayofweek = Nest.value(timePeriod, "dayofweek").asInteger();
				int day = Nest.value(timePeriod, "day").asInteger();
				
				//起始时间年月日所对应的秒数
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(active_since*1000L);
				long ymdSeconds=new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime())).getTime()/1000L;
							
				if(periodType == TIMEPERIOD_TYPE_ONETIME){ //一次性     
					if(status == MAINTENANCE_STATUS_APPROACH){//未开始的
						if(start_date>active_since && start_date<active_till_1){ 
							if(start_date < minTime){
								minTime = start_date;
							}
						}						
					}else{//运行中的
						if(start_date>time() && start_date<active_till_1){ 
							if(start_date < minTime){
								minTime = start_date;
							}						
						}						
					}
					
				}else if(periodType == TIMEPERIOD_TYPE_DAILY){ //每日						
					long seconds = ymdSeconds + start_time;//起始当天的运行时间秒数
					minTime = dayMinTime(status, seconds, every, active_since, active_till_1, start_time, minTime);
					
				}else if(periodType == TIMEPERIOD_TYPE_WEEKLY){ //每周
					//拆分周运行规则
					String binaryWeek = Integer.toBinaryString(Nest.value(timePeriod, "dayofweek").asInteger());
					//存开始运行时间所在周的秒数
					long[] timeSenconds = weekSenconds(binaryWeek, ymdSeconds, start_time);
					//时间从小到大排序   最小时间为第一个匹配
					Arrays.sort(timeSenconds);
					
					minTime = weekMinTime(status, timeSenconds, every, active_since, active_till_1, start_time, minTime);
					
				}else if(periodType == TIMEPERIOD_TYPE_MONTHLY){
					//拆分月时间规则
					String binaryMonth = Integer.toBinaryString(Nest.value(timePeriod, "month").asInteger());
					String binaryWeek = Integer.toBinaryString(Nest.value(timePeriod, "dayofweek").asInteger());
					
					minTime = monthMinTime(status, binaryMonth, ymdSeconds, every, binaryWeek, day, start_time,
							active_since, active_till_1, minTime);
					

				}				
			}			
		}
		//在运行中    但规则已过期
		if(minTime != active_till_1){
			Nest.value(maintenance, "nextMaintenanceTime").$(minTime);
		}
		return maintenance;
	}
	
	/**
	 * 开始运行时间所在周的运行时间秒数
	 * @param dayofweek
	 * @param ymdSeconds
	 * @param start_time
	 * @return
	 */
	public long[] weekSenconds(String dayofweek, long ymdSeconds, long start_time){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ymdSeconds*1000L);
		
		//周运行个数
		int w,weekCount = 0;
		for(int i=dayofweek.length(); i>0; i--){
			w = Integer.parseInt(dayofweek.substring(i-1, i));
			if(w==1) weekCount++;
		}
		
		//周运行个数时间秒数
		long[] time = new long[weekCount];
		int s,j=0;
		int week=1;//周几
		for(int i=dayofweek.length(); i>0; i--){
			week++;
			s=Integer.parseInt(dayofweek.substring(i-1, i));
			if(s==1){
				if(week==8){//周日
					c.set(Calendar.DAY_OF_WEEK, 1);
					
				}else{
					c.set(Calendar.DAY_OF_WEEK, week);
				}
				
				time[j]= c.getTime().getTime()/1000L + start_time;
				j++;
			}
		}
		
		return time;
	}
	
	/**
	 * 
	 * @param binaryMonth  二进制运行月份
	 * @param active_since 开始时间秒数
	 * @param every  每月的第几周
	 * @param binaryWeek  二进制周几
	 * @param day 每月几号
	 * @param start_time  运行小时+分钟对应的秒数
	 * @return 
	 */
	public long[] monthSeconds(String binaryMonth, long ymdSeconds,int every,String binaryWeek, int day,long start_time){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ymdSeconds*1000L);

		//月运行个数
		int m,mCount=0;
		for(int i=binaryMonth.length(); i>0; i--){
			m = Integer.parseInt(binaryMonth.substring(i-1, i));
			if(m==1) mCount++;
		}
		
		//月中周几运行	
		int w,z=0,wCount=0,num=1;
		int[] week = new int[7];
		if(day==0){ //为一周中的第几天			
			for(int i=binaryWeek.length(); i>0; i--){
				num++;
				w = Integer.parseInt(binaryWeek.substring(i-1, i));
				if(w==1) {
					wCount++;
					week[z]=num;
					if(week[z]==8){//周日
						week[z]=1;
					}
					z++;
				}
			}
		}
		
		//数组长度
		long[] time=null;
		if(day==0){ //周
			time = new long[mCount*wCount];
		}else{
			time = new long[mCount];
		}
		
		//月运行个数时间秒数
		int s,j=0;
		int count=0;//月份
		for(int i=binaryMonth.length(); i>0; i--){			
			s=Integer.parseInt(binaryMonth.substring(i-1, i));
			if(s==1){
				if(day==0){ //月份的第几周运行
					c.set(c.get(Calendar.YEAR), count, 1); //定义月份
					c.set(Calendar.DAY_OF_WEEK_IN_MONTH, every); //定义第几周
					
					//本月的最大时间毫秒数
					long monthMaxSeconds = monthMaxSeconds(ymdSeconds, count);
					
					for(int x=0; x<wCount; x++){
						c.set(Calendar.DAY_OF_WEEK, week[x]);
						
						if(c.getTime().getTime()>=monthMaxSeconds){ //一周中的几天 超出本月范围   取上一周
							c.set(c.get(Calendar.YEAR), count, 1); //定义月份
							c.set(Calendar.DAY_OF_WEEK_IN_MONTH, every-1);
						}
						
						time[j]= c.getTime().getTime()/1000L + start_time;
						j++;
					}	
										
				}else{ //月份的几日运行
					c.set(c.get(Calendar.YEAR), count, day);
					time[j]= c.getTime().getTime()/1000L + start_time;
					j++;
				}
			}
			count++;
		}
		
		return time;
	}
	
	/**
	 * 日规则下最小时间秒数
	 * @param status  标识 未开始或运行中
	 * @param seconds  当天运行时间
	 * @param every  隔几天运行一次
	 * @param active_since  起始时间
	 * @param active_till_1 结束时间
	 * @param start_time  运行当天的 小时和分钟对应的秒数
	 * @param minTime 原最小时间
	 * @return
	 */
	public long dayMinTime(int status, long seconds, int every, long active_since, long active_till_1, long start_time, long minTime){		
		long time=0l;
		int i=0;//循环次数累积
		boolean flag=true;
		while(flag){			
			if(status == MAINTENANCE_STATUS_APPROACH){//未开始
				if(seconds+24*60*60*every*i >= active_since){
					time = seconds+24*60*60*every*i;
					flag=false;
				} 
				i++;
			}else{ //运行中
				if(seconds+24*60*60*every*i>=time()){
					time = seconds+24*60*60*every*i;
					flag=false;
				} 
				i++;				
			}

		}
		
		//设置最先运行时间
		if(time<active_till_1){ //第一次运行时间在作用区间内
			if(time<minTime){//设置第一次运行
				minTime = time;
			} 		
		}
		return minTime;
	}
	
	/**
	 * 周规则下最小时间
	 * @param status  标识 未开始或运行中
	 * @param timeSenconds  当天运行时间
	 * @param every  隔几天运行一次
	 * @param active_since  起始时间
	 * @param active_till_1 结束时间
	 * @param start_time  运行当天的 小时和分钟对应的秒数
	 * @param minTime 原最小时间
	 * @return
	 */
	public long weekMinTime(int status, long[] timeSenconds, int every, long active_since, long active_till_1, long start_time, long minTime){
		long time=0l;
		int i=0;
		boolean flag=true;
		while(flag){
			for(long seconds : timeSenconds){
				if(status == MAINTENANCE_STATUS_APPROACH){//未开始
					if(seconds+7*24*60*60*every*i >= active_since){
						time = seconds+7*24*60*60*every*i;
						flag=false;
						break;
					}				
				}else{//运行中
					if(seconds+7*24*60*60*every*i >= time()){
						time = seconds+7*24*60*60*every*i;
						flag=false;
						break;
					}						
				}
			}
			i++;
		}
		
		//设置最先运行时间
		if(time<active_till_1){ //第一次运行时间在作用区间内
			if(time<minTime){//设置第一次运行
				minTime = time;
			} 		
		}
		
		return minTime;
	}
	
	/**
	 * 月规则最小时间
	 * @return
	 * @throws ParseException 
	 */
	public long monthMinTime(int status, String binaryMonth, long ymdSeconds, int every, String binaryWeek, int day, long start_time,
			long active_since, long active_till_1, long minTime) throws ParseException{
		long time=0l;
		boolean flag=true;
		//设置最先运行时间   (当前年都比起始时间小则取第二年的进行比较)
		while(flag){
			Calendar c = Calendar.getInstance();

			//年内月运行时间
			long[] timeSenconds = monthSeconds(binaryMonth, ymdSeconds, every, binaryWeek, day, start_time);
			Arrays.sort(timeSenconds);
			long maintenanceEvery = 0L;
			boolean firstMaintenance = false;
			long maintenanceNow = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime())).getTime()/1000L;
			for(long timeSecond:timeSenconds){
				if(maintenanceNow<timeSecond){
					maintenanceEvery=timeSecond;
					break;
				}
			}
			if(maintenanceEvery==0L){
				//月规则下运行时间秒数
				Calendar cNew = Calendar.getInstance();
				cNew.setTimeInMillis(ymdSeconds*1000L);
				cNew.add(Calendar.YEAR, 1);
				ymdSeconds = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(cNew.getTime())).getTime()/1000L;
				timeSenconds = monthSeconds(binaryMonth, ymdSeconds, every, binaryWeek, day, start_time);
				Arrays.sort(timeSenconds);
			}
			
			if(timeSenconds.length == 0 ){
				break;
			}
				
			
			for(long seconds : timeSenconds){
				if(status == MAINTENANCE_STATUS_APPROACH){//未开始
					if(seconds >= active_since){
						time = seconds;
						flag=false;
						break;
					}					
				}else{//运行中
					if(maintenanceEvery!=0L){
						time = maintenanceEvery;
						flag=false;
						break;
					}
					if(seconds >= time()){
						time = seconds;
						flag=false;
						break;
					}						
				}

			}
		}
		
		//设置最先运行时间
		if(time<active_till_1){ //第一次运行时间在作用区间内
			if(time<minTime){//设置第一次运行
				minTime = time;
			} 		
		}
		return minTime;
	}
	
	/**
	 * 
	 * @param ymdSeconds  开始时间的年月日秒数
	 * @param count 月份
	 * @return
	 */
	public long monthMaxSeconds(long ymdSeconds, int count){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ymdSeconds*1000L);
		
		c.set(c.get(Calendar.YEAR), count+1, 1);
		return c.getTime().getTime();
	}
}
