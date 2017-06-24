package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_APPLICATION;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NOT_ZERO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.getApplicationSourceParentIds;
import static com.isoft.iradar.inc.HostsUtil.get_application_by_applicationid;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ItemsUtil.activate_item;
import static com.isoft.iradar.inc.ItemsUtil.disable_item;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ApplicationsAction extends RadarBaseAction {
	
	private CArray<Map> dbApplication;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of applications"));
		page("file", "applications.action");
		page("hist_arg", new String[] {"groupid", "hostid"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"applications" ,		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"hostid" ,				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID+NOT_ZERO, "isset({form})&&!isset({applicationid})"),
			"groupid" ,			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"applicationid" ,	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			"isset({form})&&{form}==\"update\""),
			"appname" ,			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,		"isset({save})", _("Name")),
			// actions
			"go" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"clone" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form" ,				array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (isset(_REQUEST,"applicationid")) {
			CAppGet options = new CAppGet();
			options.setApplicationIds(Nest.value(_REQUEST,"applicationid").asLong());
			options.setOutput(new String[]{"name", "hostid"});
			dbApplication = API.Application(getIdentityBean(), executor).get(options);
			if (empty(dbApplication)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"applications") || !isArray(Nest.value(_REQUEST,"applications").$())) {
				access_deny();
			} else {
				CAppGet cappGet = new CAppGet();
				cappGet.setApplicationIds(Nest.array(_REQUEST,"applications").asLong());
				cappGet.setCountOutput(true);
				long dbApplications = API.Application(getIdentityBean(), executor).get(cappGet);
				if (dbApplications != count(Nest.value(_REQUEST,"applications").$())) {
					access_deny();
				}
			}
		}
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
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
		/* Actions */
		if (isset(_REQUEST,"save")) {
			DBstart(executor);
			
			final Map application = map(
				"name", Nest.value(_REQUEST,"appname").$(),
				"hostid", Nest.value(_REQUEST,"hostid").$()
			);

			CArray<Long[]> dbApplications = null;
			int action;
			String msgOk,msgFail;
			if (isset(_REQUEST,"applicationid")) {
				Nest.value(application,"applicationid").$(Nest.value(_REQUEST,"applicationid").$());
				dbApplications = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Application(getIdentityBean(), executor).update(array(application));
					}
				}, null);
				action = AUDIT_ACTION_UPDATE;
				msgOk = _("Application updated");
				msgFail = _("Cannot update application");
			} else {
				dbApplications = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Application(getIdentityBean(), executor).create(array(application));
					}
				}, null);
				action = AUDIT_ACTION_ADD;
				msgOk = _("Application added");
				msgFail = _("Cannot add application");
			}
			boolean result = !empty(dbApplications);
			result = DBend(executor, result);

			show_messages(result, msgOk, msgFail);

			if (result) {
				Long applicationId = dbApplications.get("applicationids")[0];

				add_audit(getIdentityBean(), executor, action, AUDIT_RESOURCE_APPLICATION,
					_("Application")+" ["+Nest.value(_REQUEST,"appname").asString()+"] ["+applicationId+"]"
				);
				unset(_REQUEST,"form");
				clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"applicationid")) {
			unset(_REQUEST,"applicationid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"delete")) {
			if (isset(_REQUEST,"applicationid")) {
				boolean result = false;
				Map app = get_application_by_applicationid(getIdentityBean(), executor, Nest.value(_REQUEST,"applicationid").asString());
				Map host = null;
				if (!empty(app)) {
					host = get_host_by_hostid(getIdentityBean(), executor, Nest.value(app,"hostid").asLong());
					
					DBstart(executor);
					
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Application(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"applicationid").asLong()));
						}
					});
					result = DBend(executor, result);
				}
				show_messages(result, _("Application deleted"), _("Cannot delete application"));
				if (result) {
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_APPLICATION, "Application ["+Nest.value(app,"name").asString()+"] from host ["+Nest.value(host,"host").asString()+"]");
				}

				unset(_REQUEST,"form");
				unset(_REQUEST,"applicationid");
				clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
			}
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			boolean goResult = true;
			CArray applications = get_request("applications", array());
			
			DBstart(executor);

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbApplications = DBselect(executor,
				"SELECT a.applicationid,a.name,a.hostid"+
				" FROM applications a"+
				" WHERE "+sqlParts.dual.dbConditionInt("a.applicationid", applications.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(final Map dbApplication : dbApplications) {
				if (!isset(applications,dbApplication.get("applicationid"))) {
					continue;
				}

				goResult &= Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Application(getIdentityBean(), executor).delete(Nest.value(dbApplication,"applicationid").asLong()));
					}
				});

				if (goResult) {
					Map host = get_host_by_hostid(getIdentityBean(), executor, Nest.value(dbApplication,"hostid").asLong());

					add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_APPLICATION,
						"Application ["+Nest.value(dbApplication,"name").asString()+"] from host ["+Nest.value(host,"host").asString()+"]");
				}
			}
			
			goResult = DBend(executor, goResult);

			show_messages(goResult, _("Application deleted"), _("Cannot delete application"));
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		} else if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean result = true;
			String hostId = get_request("hostid");
			final boolean enable = ("activate".equals(get_request("go")));
			int updated = 0;
			
			DBstart(executor);
			
			Map params = new HashMap();
			params.put("hostid", hostId);
			for(String appid : (CArray<String>)get_request("applications",array())) {
				params.put("applicationid", appid);
				CArray<Map> dbItems = DBselect(executor,
					"SELECT ia.itemid,i.hostid,i.key_"+
					" FROM items_applications ia"+
						" LEFT JOIN items i ON ia.itemid=i.itemid"+
					" WHERE ia.applicationid=#{applicationid}"+
						" AND i.hostid=#{hostid}"+
						" AND i.type<>"+ITEM_TYPE_HTTPTEST,
					params
				);
				for(final Map item : dbItems) {
					result &= Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return enable ? activate_item(getIdentityBean(), executor,Nest.value(item,"itemid").asLong()) : disable_item(getIdentityBean(), executor,Nest.value(item,"itemid").asLong());
						}
					});
					updated++;
				}
			}
			
			result = DBend(executor, result);

			String messageSuccess = enable
				? _n("Item enabled", "Items enabled", updated)
				: _n("Item disabled", "Items disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable item", "Cannot enable items", updated)
				: _n("Cannot disable item", "Cannot disable items", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, hostId);
		}

		/* Display  */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"applicationid", get_request("applicationid"),
				"groupid", get_request("groupid", 0),
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0)
			);

			if (isset(data,"applicationid") && !isset(_REQUEST,"form_refresh")) {
				Map dbApp = reset(dbApplication);
				Nest.value(data,"appname").$(Nest.value(dbApp,"name").$());
				Nest.value(data,"hostid").$(Nest.value(dbApp,"hostid").$()) ;
			} else {
				Nest.value(data,"appname").$(get_request("appname", ""));
				Nest.value(data,"hostid").$(get_request("hostid")) ;
			}
			
			// render view
			CView applicationView = new CView("configuration.application.edit", data);
			applicationView.render(getIdentityBean(), executor);
			applicationView.show();
		} else {
			CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups", map("editable", true, "with_hosts_and_templates", true),
				"hosts", map("editable", true, "templated_hosts", true),
				"hostid", get_request("hostid"),
				"groupid", get_request("groupid")
			));
			CArray data = map(
				"pageFilter", pageFilter
			);
			Nest.value(data,"groupid").$(pageFilter.$("groupid").$());
			Nest.value(data,"hostid").$(pageFilter.$("hostid").$());

			if (!empty(pageFilter.$("hostsSelected"))) {
				Map<String, Object> config = select_config(getIdentityBean(), executor);
				// get application ids
				String sortfield = getPageSortField(getIdentityBean(), executor, "name");
				String sortorder = getPageSortOrder(getIdentityBean(), executor);

				CAppGet options = new CAppGet();
				if((pageFilter.$("hostid").asInteger() > 0)){
					options.setHostIds(pageFilter.$("hostid").asLong());
				}
				if(pageFilter.$("groupid").asInteger() > 0){
					options.setGroupIds( pageFilter.$("groupid").asLong());
				}
				options.setOutput(new String[]{"applicationid"});
				options.setEditable(true);
				options.setSortfield(sortfield);
				options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				CArray<Map> applications = API.Application(getIdentityBean(), executor).get(options);

				// get applications
				options = new CAppGet();
				options.setApplicationIds(rda_objectValues(applications, "applicationid").valuesAsLong());
				options.setOutput(API_OUTPUT_EXTEND);
				options.setSelectItems(new String[]{"itemid"});
				options.setExpandData(true);
				applications = API.Application(getIdentityBean(), executor).get(options);
				Nest.value(data,"applications").$(applications);

				order_result(applications, sortfield, sortorder);

				// fetch template application source parents
				CArray<CArray<Long>> applicationSourceParentIds = getApplicationSourceParentIds(getIdentityBean(), executor, rda_objectValues(applications, "applicationid").valuesAsLong());
				CArray parentAppIds = array();

				for (CArray<Long> applicationParentIds : applicationSourceParentIds) {
					for(Long parentId : applicationParentIds) {
						Nest.value(parentAppIds,parentId).$(parentId);
					}
				}

				if (!empty(parentAppIds)) {
					SqlBuilder sqlParts = new SqlBuilder();
					String sql = "SELECT a.applicationid,h.hostid,h.name"+
							" FROM applications a,hosts h"+
							" WHERE a.hostid=h.hostid"+
								" AND "+ sqlParts.where.dbConditionInt("a.applicationid", parentAppIds.valuesAsLong());
					 CArray<Map> parentTemplates = DBfetchArrayAssoc(DBselect(executor, sql, sqlParts.getNamedParams()),"applicationid");

					for (Map application : applications) {
						if (!empty(Nest.value(application,"templateids").$()) && isset(applicationSourceParentIds,application.get("applicationid"))) {
							CArray subParentAppIds = Nest.value(applicationSourceParentIds,application.get("applicationid")).asCArray();
							for (Object parentAppId : subParentAppIds) {
								if(empty(Nest.value(application,"sourceTemplates").$())){
									Nest.value(application,"sourceTemplates").$(array());
								}
								Nest.value(application,"sourceTemplates").asCArray().add(parentTemplates.get(parentAppId));
							}
						}
					}
				}
			} else {
				Nest.value(data,"applications").$(array());
			}

			// get paging
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, Nest.value(data,"applications").asCArray(), array("applicationid")));
			
			// render view
			CView applicationView = new CView("configuration.application.list", data);
			applicationView.render(getIdentityBean(), executor);
			applicationView.show();
		}
	}
}
