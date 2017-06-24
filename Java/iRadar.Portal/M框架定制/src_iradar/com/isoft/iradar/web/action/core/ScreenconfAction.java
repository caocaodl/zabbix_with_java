package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit_details;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ScreenconfAction extends RadarBaseAction {
	
	boolean _isExportData = false;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST,"go") && "export".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"screens")) {
			_isExportData = true;
			Nest.value(page,"type").$(detect_page_type(PAGE_TYPE_XML));
			Nest.value(page,"file").$("rda_export_screens.xml");
		} else {
			_isExportData = false;
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("title", _("Configuration of screens"));
			page("file", "screenconf.action");
			page("hist_arg", new String[] { "templateid" });
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"screens" ,			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"screenid" ,			array(T_RDA_INT, O_NO,	P_SYS,	DB_ID,			"isset({form})&&{form}==\"update\""),
			"templateid" ,		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"name" ,				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,		"isset({save})", _("Name")),
			"hsize" ,				array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 100), "isset({save})", _("Columns")),
			"vsize" ,				array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 100), "isset({save})", _("Rows")),
			// actions
			"go" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"clone" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"delete" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"cancel" ,				array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"form" ,				array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,	null,			null),
			// import
			"rules" ,				array(T_RDA_STR, O_OPT, null,	DB_ID,			null),
			"import" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		
		CProfile.update(getIdentityBean(), executor,"web.screenconf.config", get_request("config", 0), PROFILE_TYPE_INT);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		if (isset(_REQUEST,"screenid")) {
			CArray<Map> screens = null;
			if (isset(_REQUEST,"templateid")) {
				CTemplateScreenGet option = new CTemplateScreenGet();
				option.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
				option.setEditable(true);
				option.setOutput(API_OUTPUT_EXTEND);
				option.setSelectScreenItems(API_OUTPUT_EXTEND);
				screens = API.TemplateScreen(getIdentityBean(), executor).get(option);
			} else {
				CScreenGet option = new CScreenGet();
				option.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
				option.setEditable(true);
				option.setOutput(API_OUTPUT_EXTEND);
				option.setSelectScreenItems(API_OUTPUT_EXTEND);
				screens = API.Screen(getIdentityBean(), executor).get(option);
			}
			if (empty(screens)) {
				access_deny();
			}
		}
		//TODO
//		/** Export*/
//		if (_isExportData) {
//			//sjh CConfigurationExport  CConfigurationExportBuilder  CExportWriterFactory不存在  TODO
//			CArray _screens = get_request("screens", array());
//			_export = new CConfigurationExport(array("screens" => _screens));
//			_export.setBuilder(new CConfigurationExportBuilder());
//			_export.setWriter(CExportWriterFactory::getWriter(CExportWriterFactory::XML));
//			_exportData = _export.export();
//			if (hasErrorMesssages()) {
//				show_messages();
//			}
//			else {
//				print(_exportData);
//			}
//			exit();
//		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"screenid")) {
			unset(_REQUEST,"screenid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			CArray<Long[]> screenids = null;
			if (isset(_REQUEST,"screenid")) {
				final Map screen = map(
					"screenid", Nest.value(_REQUEST,"screenid").$(),
					"name", Nest.value(_REQUEST,"name").$(),
					"hsize", Nest.value(_REQUEST,"hsize").$(),
					"vsize", Nest.value(_REQUEST,"vsize").$()
				);

				Map screenOld = null;
				if (isset(_REQUEST,"templateid")) {
					CTemplateScreenGet tsoptions = new CTemplateScreenGet();
					tsoptions.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
					tsoptions.setOutput(API_OUTPUT_EXTEND);
					tsoptions.setEditable(true);
					CArray<Map> screenOlds = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
					screenOld = reset(screenOlds);
					screenids = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.TemplateScreen(getIdentityBean(), executor).update(array(screen));
						}
					}, null);
				} else {
					CScreenGet soptions = new CScreenGet();
					soptions.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
					soptions.setOutput(API_OUTPUT_EXTEND);
					soptions.setEditable(true);
					CArray<Map> screenOlds = API.Screen(getIdentityBean(), executor).get(soptions);
					screenOld = reset(screenOlds);
					screenids = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Screen(getIdentityBean(), executor).update(array(screen));
						}
					}, null);
				}

				if (!empty(screenids)) {
					add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "screens", screenOld, screen);
				}
				show_messages(!empty(screenids), _("Screen updated"), _("Cannot update screen"));
			} else {
				final Map screen = map(
					"name", Nest.value(_REQUEST,"name").$(),
					"hsize", Nest.value(_REQUEST,"hsize").$(),
					"vsize", Nest.value(_REQUEST,"vsize").$()
				);
				
				if (isset(_REQUEST,"templateid")) {
					Nest.value(screen,"templateid").$(get_request("templateid"));
					screenids = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.TemplateScreen(getIdentityBean(), executor).create(array(screen));
						}
					}, null);
				} else {
					screenids = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Screen(getIdentityBean(), executor).create(array(screen));
						}
					}, null);
				}

				if (!empty(screenids)) {
					Long screenid = reset(screenids)[0];
					add_audit_details(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_SCREEN, screenid, Nest.value(screen,"name").asString());
				}
				show_messages(!empty(screenids), _("Screen added"), _("Cannot add screen"));
			}

			if (!empty(screenids)) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"screenid");
				clearCookies(!empty(screenids));
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"screenid") || "delete".equals(Nest.value(_REQUEST,"go").asString())) {
			final CArray screenids = get_request("screens", array());
			if (isset(_REQUEST,"screenid")) {
				screenids.add(Nest.value(_REQUEST,"screenid").$());
			}

			CScreenGet soptions = new CScreenGet();
			soptions.setScreenIds(screenids.valuesAsLong());
			soptions.setOutput(API_OUTPUT_EXTEND);
			soptions.setEditable(true);
			CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);

			DBstart(executor);
			boolean goResult;
			if (!empty(screens)) {
				goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Screen(getIdentityBean(), executor).delete(screenids.valuesAsLong()));
					}
				});

				if (goResult) {
					for(Map screen : screens) {
						add_audit_details(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString());
					}
				}
			} else {
				goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.TemplateScreen(getIdentityBean(), executor).delete(screenids.valuesAsLong()));
					}
				});

				if (goResult) {
					CTemplateScreenGet tsoptions = new CTemplateScreenGet();
					tsoptions.setScreenIds(screenids.valuesAsLong());
					tsoptions.setOutput(API_OUTPUT_EXTEND);
					tsoptions.setEditable(true);
					CArray<Map> templatedScreens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);

					for(Map screen : templatedScreens) {
						add_audit_details(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString());
					}
				}
			}

			goResult = DBend(executor, goResult);
			
			if (goResult) {
				unset(_REQUEST,"screenid");
				unset(_REQUEST,"form");
			}

			show_messages(goResult, _("Screen deleted"), _("Cannot delete screen"));
			clearCookies(goResult);
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"form", get_request("form", null),
				"screenid", get_request("screenid", null),
				"templateid", get_request("templateid", null)
			);

			// screen
			if (!empty(Nest.value(data,"screenid").$())) {
				CArray<Map> screens = null;
				if (!empty(Nest.value(data,"templateid").$())) {
					CTemplateScreenGet tsoptions = new CTemplateScreenGet();
					tsoptions.setScreenIds(Nest.value(data,"screenid").asLong());
					tsoptions.setEditable(true);
					tsoptions.setOutput(API_OUTPUT_EXTEND);
					screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
				} else {
					CScreenGet soptions = new CScreenGet();
					soptions.setScreenIds(Nest.value(data,"screenid").asLong());
					soptions.setEditable(true);
					soptions.setOutput(API_OUTPUT_EXTEND);
					screens = API.Screen(getIdentityBean(), executor).get(soptions);
				}
				Nest.value(data,"screen").$(reset(screens));
			}

			if (!empty(Nest.value(data,"screenid").$()) && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"name").$(Nest.value(data,"screen","name").$());
				Nest.value(data,"hsize").$(Nest.value(data,"screen","hsize").$());
				Nest.value(data,"vsize").$(Nest.value(data,"screen","vsize").$());
				if (!empty(Nest.value(data,"screen","templateid").$())) {
					Nest.value(data,"templateid").$(Nest.value(data,"screen","templateid").$());
				}
			} else {
				Nest.value(data,"name").$(get_request("name", ""));
				Nest.value(data,"hsize").$(get_request("hsize", 1));
				Nest.value(data,"vsize").$(get_request("vsize", 1));
			}

			// render view
			CView screenView = new CView("configuration.screen.edit", data);
			screenView.render(getIdentityBean(), executor);
			screenView.show();
		} else {
			CArray data = map(
				"templateid", get_request("templateid", null)
			);

			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
			CArray<Map> screens = null;
			if (!empty(Nest.value(data,"templateid").$())) {
				CTemplateScreenGet tsoptions = new CTemplateScreenGet();
				tsoptions.setEditable(true);
				tsoptions.setOutput(API_OUTPUT_EXTEND);
				tsoptions.setTemplateIds(Nest.value(data,"templateid").asLong());
				tsoptions.setSortfield(sortfield);
				tsoptions.setLimit(Nest.value(config,"search_limit").asInteger());
				screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
				Nest.value(data,"screens").$(screens);
			} else {
				CScreenGet soptions = new CScreenGet();
				soptions.setEditable(true);
				soptions.setOutput(API_OUTPUT_EXTEND);
				soptions.put("templateIds", new String[]{Nest.value(data,"templateid").asString()});
				soptions.setSortfield(sortfield);
				soptions.setLimit(Nest.value(config,"search_limit").asInteger());
				screens = API.Screen(getIdentityBean(), executor).get(soptions);
				Nest.value(data,"screens").$(screens);
			}
			order_result(screens, sortfield, getPageSortOrder(getIdentityBean(), executor));

			// paging
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,
				screens,
				array("screenid"),
				map("templateid", get_request("templateid"))
			));

			// render view
			CView screenView = new CView("configuration.screen.list", data);
			screenView.render(getIdentityBean(), executor);
			screenView.show();
		}
	}
}
