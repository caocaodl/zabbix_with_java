package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_search;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.GraphsUtil.copyGraphToHost;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HttpTestUtil.copyHttpTests;
import static com.isoft.iradar.inc.ItemsUtil.copyApplications;
import static com.isoft.iradar.inc.ItemsUtil.copyItems;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.TriggersUtil.copyTriggersToHosts;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TemplatesAction extends RadarBaseAction {
	
	private boolean exportData;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST,"go") && "export".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"templates")) {
			exportData = true;
			Nest.value(page,"type").$(detect_page_type(PAGE_TYPE_XML));
			Nest.value(page,"file").$("rda_export_templates.xml");
		} else {
			exportData = false;
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("title", _("Configuration of templates"));
			page("file", "templates.action");
			page("hist_arg", new String[] { "groupid" });
			page("scripts", new String[] { "multiselect.js" });
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR						TYPE		OPTIONAL FLAGS			VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts",					array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"groups",					array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"clear_templates",		array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"templates",				array(T_RDA_INT, O_OPT, null,		DB_ID,	null),
			"add_templates",		array(T_RDA_INT, O_OPT, null,		DB_ID,	null),
			"add_template" ,		array(T_RDA_STR, O_OPT, null,		null,	null),
			"templateid",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	"isset({form})&&{form}==\"update\""),
			"template_name",		array(T_RDA_STR, O_OPT, null,		NOT_EMPTY, "isset({save})", _("Template name")),
			"visiblename",			array(T_RDA_STR, O_OPT, null,		null,	"isset({save})"),
			"groupid",					array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"twb_groupid",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"newgroup",				array(T_RDA_STR, O_OPT, null,		null,	null),
			"macros_rem",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"macros",					array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"macro_new",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	"isset({macro_add})"),
			"value_new",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	"isset({macro_add})"),
			"macro_add",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			// actions
			"go",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"unlink",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"unlink_and_clear",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"clone",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"full_clone",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete_and_clear",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"cancel",					array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form",						array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form_refresh",			array(T_RDA_STR, O_OPT, null,		null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("templateid")) && !API.Template(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"templateid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		CArray templateIds = get_request("templates", array());

		if (exportData) {
			//TODO
//			_export = new CConfigurationExport(array("templates" => _templateIds));
//			_export.setBuilder(new CConfigurationExportBuilder());
//			_export.setWriter(CExportWriterFactory::getWriter(CExportWriterFactory::XML));
//			_exportData = _export.export();
//
//			if (hasErrorMesssages()) {
//				show_messages();
//			}
//			else {
//				print(_exportData);
//			}
//			exit();
		}
		
		/* Actions */
		if (isset(_REQUEST,"add_template") && isset(_REQUEST,"add_templates")) {
			Nest.value(_REQUEST,"templates").$(array_merge(templateIds, Nest.value(_REQUEST,"add_templates").asCArray()));
		}
		if (hasRequest("unlink") || hasRequest("unlink_and_clear")) {
			Nest.value(_REQUEST,"clear_templates").$(get_request("clear_templates", array()));

			CArray unlinkTemplates = array();

			if (hasRequest("unlink") && isArray(get_request("unlink",new Object()))) {
				unlinkTemplates = array_keys(get_request("unlink", array()));
			} else if (hasRequest("unlink_and_clear") && isArray(get_request("unlink_and_clear",new Object()))) {
				unlinkTemplates = array_keys(get_request("unlink_and_clear", array()));
				Nest.value(_REQUEST,"clear_templates").$(unlinkTemplates);
			}

			for(Object id : unlinkTemplates) {
				unset(templateIds, array_search(id,templateIds));
			}
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"templateid")) {
			Nest.value(_REQUEST,"form").$("clone");
			unset(_REQUEST,"templateid");
			unset(_REQUEST,"hosts");
		} else if (isset(_REQUEST,"full_clone") && isset(_REQUEST,"templateid")) {
			Nest.value(_REQUEST,"form").$("full_clone");
			Nest.value(_REQUEST,"hosts").$(array());
		} else if (isset(_REQUEST,"save")) {
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				
				CArray<Map> macros = get_request("macros", array());
				CArray<Map> groups = get_request("groups", array());
				CArray templates = get_request("templates", array());
				CArray templatesClear = get_request("clear_templates", array());
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();
				final String newGroup = get_request("newgroup", "");
				String templateName = get_request("template_name", "");
				String visibleName = get_request("visiblename", "");
				Long cloneTemplateId = null;

				if ("full_clone".equals(Nest.value(_REQUEST,"form").asString())) {
					cloneTemplateId = templateId;
					templateId = null;
				}

				if (!empty(templateId)) {
					msgOk = _("Template updated");
					msgFail = _("Cannot update template");
				} else {
					msgOk = _("Template added");
					msgFail = _("Cannot add template");
				}

				for (Entry<Object, Map> e : (Clone.deepcopy(macros)).entrySet()) {
				    Object key = e.getKey();
				    Map macro = e.getValue();
					if (rda_empty(Nest.value(macro,"macro").$()) && rda_empty(Nest.value(macro,"value").$())) {
						unset(macros,key);
					}
				}

				for (Entry<Object, Map> e : macros.entrySet()) {
				    Object key = e.getKey();
				    Map macro = e.getValue();
					// transform macros to uppercase {_aaa} => {$AAA}
					Nest.value(macros,key,"macro").$(StringUtils.upperCase(Nest.value(macro,"macro").asString()));
				}

				// create new group
				groups = rda_toObject(groups, "groupid");

				if (!rda_empty(newGroup)) {
					CArray result = Call(new Wrapper<CArray>() {
						@Override
						protected CArray doCall() throws Throwable {
							return API.HostGroup(getIdentityBean(), executor).create(array((Map)map("name", newGroup)));
						}
					}, null);

					if (empty(result)) {
						throw new Exception();
					}

					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(Nest.array(result,"groupids").asLong());
					hgoptions.setOutput(API_OUTPUT_EXTEND);
					CArray<Map> dbnewGroup = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

					if (!empty(dbnewGroup)) {
						groups = array_merge(groups, dbnewGroup);
					} else {
						throw new Exception();
					}
				}

				CArray linkedTemplates = Clone.deepcopy(templates);
				templates = array();
				for(Object linkedTemplateId : linkedTemplates) {
					templates.add(map("templateid", linkedTemplateId));
				}

				templatesClear = rda_toObject(templatesClear, "templateid");

				// skip discovered hosts
				CHostGet hoptions = new CHostGet();
				hoptions.setHostIds(get_request("hosts", array()).valuesAsLong());
				hoptions.setOutput(new String[]{"hostid"});
				hoptions.setTemplatedHosts(true);
				hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
				CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);

				final Map template = map(
					"host", templateName,
					"name", visibleName,
					"groups", groups.isEmpty()?null:groups,
					"templates", templates,
					"hosts", hosts,
					"macros", macros
				);

				// create/update template
				boolean created;
				if (!empty(templateId)) {
					created = false;
					Nest.value(template,"templateid").$(templateId);
					Nest.value(template,"templates_clear").$(templatesClear);
					
					CArray<Long[]> updates = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Template(getIdentityBean(), executor).update(array(template));
						}
					}, null);

					if (empty(updates)) {
						throw new Exception();
					}
				} else {
					created = true;
					CArray<Long[]> result = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Template(getIdentityBean(), executor).create(array(template));
						}
					}, null);

					if (!empty(result)) {
						templateId = reset(result.get("templateids"));
					} else {
						throw new Exception();
					}
				}

				// full clone
				if (!rda_empty(templateId) && !empty(templateId) && !empty(cloneTemplateId) && "full_clone".equals(Nest.value(_REQUEST,"form").asString())) {
					if (!copyApplications(this.getIdentityBean(), executor, cloneTemplateId, templateId)) {
						throw new Exception();
					}

					if (!copyItems(this.getIdentityBean(), executor, cloneTemplateId, templateId)) {
						throw new Exception();
					}

					// copy web scenarios
					if (!copyHttpTests(this.getIdentityBean(), executor, cloneTemplateId, templateId)) {
						throw new Exception();
					}

					// clone triggers
					CTriggerGet toptions = new CTriggerGet();
					toptions.setHostIds(cloneTemplateId);
					toptions.setOutput(new String[]{"triggerid"});
					toptions.setInherited(false);
					CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
					if (!empty(triggers)) {
						if (!copyTriggersToHosts(this.getIdentityBean(), executor, rda_objectValues(triggers, "triggerid").valuesAsLong(), new Long[]{Nest.as(templateId).asLong()}, cloneTemplateId)) {
							throw new Exception();
						}
					}

					// host graphs
					CGraphGet goptions = new CGraphGet();
					goptions.setHostIds(cloneTemplateId);
					goptions.setInherited(false);
					goptions.setOutput(API_OUTPUT_REFER);
					CArray<Map> dbGraphs = API.Graph(getIdentityBean(), executor).get(goptions);

					boolean result = true;
					for(Map dbGraph : dbGraphs) {
						result &= !empty(copyGraphToHost(this.getIdentityBean(), executor, Nest.value(dbGraph,"graphid").asLong(), Nest.as(templateId).asLong()));
					}

					if (!result) {
						throw new Exception();
					}

					// clone discovery rules
					CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
					droptions.setHostIds(cloneTemplateId);
					droptions.setInherited(false);
					CArray<Map> discoveryRules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
					if (!empty(discoveryRules)) {
						boolean copyDiscoveryRules = API.DiscoveryRule(getIdentityBean(), executor).copy(map(
							"discoveryids", rda_objectValues(discoveryRules, "itemid"),
							"hostids", array(templateId)
						));

						if (!copyDiscoveryRules) {
							throw new Exception();
						}
					}

					// clone screens
					CTemplateScreenGet tsoptions = new CTemplateScreenGet();
					tsoptions.setTemplateIds(cloneTemplateId);
					tsoptions.setOutput(new String[]{"screenid"});
					tsoptions.setPreserveKeys(true);
					tsoptions.put("inherited", false);
					CArray<Map> screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
					if (!empty(screens)) {
						boolean screensCopied = API.TemplateScreen(getIdentityBean(), executor).copy(map(
							"screenIds", rda_objectValues(screens, "screenid"),
							"templateIds", templateId
						));

						if (!screensCopied) {
							throw new Exception();
						}
					}
				}

				DBend(executor, true);
				show_messages(true, msgOk, msgFail);
				clearCookies(true);

				if (created) {
					add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_TEMPLATE, Nest.as(templateId).asLong(), templateName, "hosts", null, null);
				}
				unset(_REQUEST,"form");
				unset(_REQUEST,"templateid");

			} catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"templateid")) {
			DBstart(executor);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Template(getIdentityBean(), executor).massUpdate(map(
							"templates", rda_toObject(Nest.value(_REQUEST,"templateid").asCArray(), "templateid"),
							"hosts", array()
						)));
				}
			});
			if (result) {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Template(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"templateid").asLong()));
					}
				});
			}

			result = DBend(executor, result);
			
			show_messages(result, _("Template deleted"), _("Cannot delete template"));
			clearCookies(result);

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"templateid");
			}
			unset(_REQUEST,"delete");
		} else if (isset(_REQUEST,"delete_and_clear") && isset(_REQUEST,"templateid")) {
			DBstart(executor);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Template(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"templateid").asLong()));
				}
			});
			
			result = DBend(executor, result);

			show_messages(result, _("Template deleted"), _("Cannot delete template"));
			clearCookies(result);

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"templateid");
			}
			unset(_REQUEST,"delete");
		} else if (str_in_array(Nest.value(_REQUEST,"go").asString(), array("delete", "delete_and_clear")) && isset(_REQUEST,"templates")) {
			final CArray templates = get_request("templates", array());
			
			DBstart(executor);
			
			boolean goResult = true;

			if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
				goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Template(getIdentityBean(), executor).massUpdate(map(
								"templates", rda_toObject(templates, "templateid"),
								"hosts", array()
							)));
					}
				});
			}

			if (goResult) {
				goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Template(getIdentityBean(), executor).delete(templates.valuesAsLong()));
					}
				});
			}
			
			goResult = DBend(executor, goResult);

			show_messages(goResult, _("Template deleted"), _("Cannot delete template"));
			clearCookies(goResult);
		}

		/* Display */
		CWidget templateWidget = new CWidget();

		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"config", map(
				"individual", 1
			),
			"groups", map(
				"templated_hosts", true,
				"editable", true
			),
			"groupid", get_request("groupid", null)
		));
		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());

		if (isset(_REQUEST,"form")) {
			templateWidget.addPageHeader(_("CONFIGURATION OF TEMPLATES"));
			
			Long templateId = get_request("templateid", 0L);
			if (!empty(templateId)) {
				templateWidget.addItem(get_header_host_table(getIdentityBean(), executor, "", Nest.as(templateId).asLong(true)));
			}

			CArray data = array();

			if (!empty(templateId)) {
				CTemplateGet toptions = new CTemplateGet();
				toptions.setTemplateIds(templateId);
				toptions.setSelectGroups(API_OUTPUT_EXTEND);
				toptions.setSelectParentTemplates(new String[]{"templateid", "name"});
				toptions.setSelectMacros(API_OUTPUT_EXTEND);
				toptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> dbTemplates = API.Template(getIdentityBean(), executor).get(toptions);
				Nest.value(data,"dbTemplate").$(reset(dbTemplates));

				Nest.value(data,"original_templates").$(array());
				for(Map parentTemplate : (CArray<Map>)Nest.value(data,"dbTemplate","parentTemplates").asCArray()) {
					Nest.value(data,"original_templates",parentTemplate.get("templateid")).$(Nest.value(parentTemplate,"templateid").$());
				}
			} else {
				Nest.value(data,"original_templates").$(array());
			}

			templateIds = get_request("templates", hasRequest("form_refresh") ? array() : Nest.value(data,"original_templates").asCArray());

			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(templateIds.valuesAsLong());
			toptions.setOutput(new String[]{"templateid", "name"});
			CArray<Map> linkedTemplates = API.Template(getIdentityBean(), executor).get(toptions);
			Nest.value(data,"linkedTemplates").$(linkedTemplates);

			CArrayHelper.sort(linkedTemplates, array("name"));

			CView templateForm = new CView("configuration.template.edit", data);
			templateWidget.addItem(templateForm.render(getIdentityBean(), executor));
		} else {
			CForm frmForm = new CForm();
			frmForm.cleanItems();
			frmForm.addItem(new CDiv(array(
				new CSubmit("form", _("Create template")),
				new CButton("form", _("Import"), "redirect(\"conf.import.action?rules_preset=template\")")
			)));
			frmForm.addItem(new CVar("groupid", Nest.value(_REQUEST,"groupid").$(), "filter_groupid_id"));

			templateWidget.addPageHeader(_("CONFIGURATION OF TEMPLATES"), frmForm);

			CForm frmGroup = new CForm("get");
			frmGroup.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));

			templateWidget.addHeader(_("Templates"), frmGroup);
			templateWidget.addHeaderRowNumber();

			CForm form = new CForm();
			form.setName("templates");

			CTableInfo table = new CTableInfo(_("No templates found."));
			table.setHeader(array(
				new CCheckBox("all_templates", false, "checkAll(\""+form.getName()+"\", \"all_templates\", \"templates\");"),
				make_sorting_header(_("Templates"), "name"),
				_("Applications"),
				_("Items"),
				_("Triggers"),
				_("Graphs"),
				_("Screens"),
				_("Discovery"),
				_("Web"),
				_("Linked templates"),
				_("Linked to")
			));

			Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
			
			// get templates
			CArray<Map> templates = array();

			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			String sortorder = getPageSortOrder(getIdentityBean(), executor);

			if (pageFilter.$("groupsSelected").asBoolean()) {
				CTemplateGet toptions = new CTemplateGet();
				if(pageFilter.$("groupid").asInteger() > 0){
					toptions.setGroupIds(pageFilter.$("groupid").asLong());
				}
				toptions.setEditable(true);
				toptions.setSortfield(sortfield);
				toptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				templates = API.Template(getIdentityBean(), executor).get(toptions);
			}

			// sorting && paging
			order_result(templates, sortfield, sortorder);
			CTable paging = getPagingLine(getIdentityBean(), executor, templates, array("templateid"));

			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(rda_objectValues(templates, "templateid").valuesAsLong());
			toptions.setEditable(true);
			toptions.setOutput(new String[]{"name", "proxy_hostid"});
			toptions.setSelectHosts(new String[]{"hostid", "name", "status"});
			toptions.setSelectTemplates(new String[]{"hostid", "name", "status"});
			toptions.setSelectParentTemplates(new String[]{"hostid", "name", "status"});
			toptions.setSelectItems(API_OUTPUT_COUNT);
			toptions.setSelectTriggers(API_OUTPUT_COUNT);
			toptions.setSelectGraphs(API_OUTPUT_COUNT);
			toptions.setSelectApplications(API_OUTPUT_COUNT);
			toptions.setSelectDiscoveries(API_OUTPUT_COUNT);
			toptions.setSelectScreens(API_OUTPUT_COUNT);
			toptions.setSelectHttpTests(API_OUTPUT_COUNT);
			toptions.setNopermissions(true);
			templates = API.Template(getIdentityBean(), executor).get(toptions);

			order_result(templates, sortfield, sortorder);

			for(Map template : templates) {
				CArray templatesOutput = array();

				if (!empty(Nest.value(template,"proxy_hostid").$())) {
					Map proxy = get_host_by_hostid(getIdentityBean(), executor, Nest.value(template,"proxy_hostid").asLong());
					templatesOutput.add(Nest.value(proxy,"host").asString()+NAME_DELIMITER);
				}

				templatesOutput.add(new CLink(Nest.value(template,"name").$(), "templates.action?form=update&templateid="+Nest.value(template,"templateid").$()+url_param(getIdentityBean(), "groupid")));

				CArray applications = array(new CLink(_("Applications"), "applications.action?groupid="+Nest.value(_REQUEST,"groupid").asString()+"&hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"applications").$()+")");
				CArray items = array(new CLink(_("Items"), "items.action?filter_set=1&groupid="+Nest.value(_REQUEST,"groupid").asString()+"&hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"items").$()+")");
				CArray triggers = array(new CLink(_("Triggers"), "triggers.action?groupid="+Nest.value(_REQUEST,"groupid").asString()+"&hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"triggers").$()+")");
				CArray graphs = array(new CLink(_("Graphs"), "graphs.action?groupid="+Nest.value(_REQUEST,"groupid").asString()+"&hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"graphs").$()+")");
				CArray screens = array(new CLink(_("Screens"), "screenconf.action?templateid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"screens").$()+")");
				CArray discoveries = array(new CLink(_("Discovery"), "host_discovery.action?hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"discoveries").$()+")");
				CArray httpTests = array(new CLink(_("Web"), "httpconf.action?groupid="+Nest.value(_REQUEST,"groupid").asString()+"&hostid="+Nest.value(template,"templateid").$()),
					" ("+Nest.value(template,"httpTests").$()+")");

				order_result(Nest.value(template,"parentTemplates").asCArray(), "name");

				int i = 0;
				CArray linkedTemplatesOutput = array();

				for(Map linkedTemplate : (CArray<Map>)Nest.value(template,"parentTemplates").asCArray()) {
					i++;

					if (i > Nest.value(config,"max_in_table").asInteger()) {
						linkedTemplatesOutput.add("...");
						linkedTemplatesOutput.add("//empty element for array_pop");
						break;
					}

					String url = "templates.action?form=update&templateid="+Nest.value(linkedTemplate,"templateid").asString()+url_param(getIdentityBean(), "groupid");

					linkedTemplatesOutput.add(new CLink(Nest.value(linkedTemplate,"name").$(), url, "unknown"));
					linkedTemplatesOutput.add(", ");
				}
				array_pop(linkedTemplatesOutput);

				i = 0;
				CArray linkedToOutput = array();
				CArray<Map> linkedToObjects = array(); 

				for(Map h : (CArray<Map>)Nest.value(template,"hosts").asCArray()) {
					Nest.value(h,"objectid").$(Nest.value(h,"hostid").$());
					linkedToObjects.add(h);
				}

				for(Map h : (CArray<Map>)Nest.value(template,"templates").asCArray()) {
					Nest.value(h,"objectid").$(Nest.value(h,"templateid").$());
					linkedToObjects.add(h);
				}

				order_result(linkedToObjects, "name");

				for(Map linkedToHost : linkedToObjects) {
					if (++i > Nest.value(config,"max_in_table").asInteger()) {
						linkedToOutput.add("...");
						linkedToOutput.add("//empty element for array_pop");
						break;
					}

					String style,url;
					switch (Nest.value(linkedToHost,"status").asInteger()) {
						case HOST_STATUS_NOT_MONITORED:
							style = "on";
							url = "hosts.action?form=update&hostid="+Nest.value(linkedToHost,"objectid").asString()+"&groupid="+Nest.value(_REQUEST,"groupid").$();
							break;

						case HOST_STATUS_TEMPLATE:
							style = "unknown";
							url = "templates.action?form=update&templateid="+Nest.value(linkedToHost,"objectid").$();
							break;

						default:
							style = null;
							url = "hosts.action?form=update&hostid="+Nest.value(linkedToHost,"objectid").asString()+"&groupid="+Nest.value(_REQUEST,"groupid").$();
					}

					linkedToOutput.add(new CLink(Nest.value(linkedToHost,"name").$(), url, style));
					linkedToOutput.add(", ");
				}
				array_pop(linkedToOutput);

				table.addRow(array(
					new CCheckBox("templates["+Nest.value(template,"templateid").$()+"]", false, null, Nest.value(template,"templateid").asInteger()),
					templatesOutput,
					applications,
					items,
					triggers,
					graphs,
					screens,
					discoveries,
					httpTests,
					!empty(linkedTemplatesOutput) ? new CCol(linkedTemplatesOutput, "wraptext") : "-",
					!empty(linkedToOutput) ? new CCol(linkedToOutput, "wraptext") : "-"
				));
			}

			CComboBox goBox = new CComboBox("go");
			goBox.addItem("export", _("Export selected"));
			CComboItem goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", _("Delete selected templates?"));
			goBox.addItem(goOption);
			goOption = new CComboItem("delete_and_clear", _("Delete selected with linked elements"));
			goOption.setAttribute("confirm", _("Delete and clear selected templates? (Warning: all linked hosts will be cleared!)"));
			goBox.addItem(goOption);
			CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
			goButton.setAttribute("id", "goButton");

			rda_add_post_js("chkbxRange.pageGoName = \"templates\";");

			CTable footer = get_table_header(array(goBox, goButton));

			form.addItem(array(paging, table, paging, footer));
			templateWidget.addItem(form);
		}

		templateWidget.show();
	}

}
