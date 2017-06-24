package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_VALUE_MAP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValuemapUtil.addValueMap;
import static com.isoft.iradar.inc.ValuemapUtil.deleteValueMap;
import static com.isoft.iradar.inc.ValuemapUtil.updateValueMap;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmValuemappingAction extends RadarBaseAction {
	
	private Map dbValueMap;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of value mapping"));
		page("file", "adm.valuemapping.action");
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/devicecenter/admvaluemapping.css"});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"valuemapid",	array(T_RDA_INT, O_NO,	P_SYS,			DB_ID,		"(isset({form})&&{form}==\"update\")||isset({delete})"),
			"mapname",		array(T_RDA_STR, O_OPT,	null,			NOT_EMPTY,	"isset({save})","名称"),
			"mappings",		array(T_RDA_STR, O_OPT,	null,			null,		null),
			"save",				array(T_RDA_STR, O_OPT,	P_SYS|P_ACT,	null,		null),
			"delete",			array(T_RDA_STR, O_OPT,	P_SYS|P_ACT,	null,		null),
			"form",				array(T_RDA_STR, O_OPT,	P_SYS,			null,		null),
			"form_refresh",	array(T_RDA_INT, O_OPT,	null,			null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST,"valuemapid")) {
			Map params = new HashMap();
			params.put("valuemapid", get_request("valuemapid"));
			dbValueMap = DBfetch(DBselect(executor, "SELECT v.name FROM valuemaps v WHERE v.valuemapid=#{valuemapid}", params));
			if (empty(dbValueMap)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		String msgOk = "";
		String msgFail = "";
		int audit_action;
		try {
			if (isset(_REQUEST,"save")) {
				DBstart(executor);
				
				Map valueMap = map("name", get_request("mapname"));
				CArray<Map> mappings = get_request("mappings", array());

				if (isset(_REQUEST,"valuemapid")) {
					msgOk = _("Value map updated");
					msgFail = _("Cannot update value map");
					audit_action = AUDIT_ACTION_UPDATE;

					Nest.value(valueMap,"valuemapid").$(get_request("valuemapid"));
					updateValueMap(getIdentityBean(), executor, valueMap, mappings);
				} else {
					msgOk = _("Value map added");
					msgFail = _("Cannot add value map");
					audit_action = AUDIT_ACTION_ADD;

					addValueMap(getIdentityBean(), executor, valueMap, mappings);
				}

				add_audit(getIdentityBean(), executor, audit_action, AUDIT_RESOURCE_VALUE_MAP, _s("Value map \"%1$s\".", Nest.value(valueMap,"name").$()));
				show_messages(true, msgOk);
				unset(_REQUEST,"form");
				
				DBend(executor, true);
			} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"valuemapid")) {
				DBstart(executor);
				
				msgOk = _("Value map deleted");
				msgFail = _("Cannot delete value map");

				Map params = new HashMap();
				params.put("valuemapid", Nest.value(_REQUEST,"valuemapid").asString());
				String sql = "SELECT v.name,v.valuemapid"+
						" FROM valuemaps v"+
						" WHERE v.valuemapid=#{valuemapid}";
				Map valueMapToDelete = DBfetch(DBselect(executor, sql, params));
				if (!empty(valueMapToDelete)) {
					deleteValueMap(getIdentityBean(), executor, Nest.value(_REQUEST,"valuemapid").asLong());
				} else {
					throw new Exception(_s("Value map with valuemapid \"%1$s\" does not exist.", Nest.value(_REQUEST,"valuemapid").$()));
				}

				add_audit(
					getIdentityBean(), 
					executor,
					AUDIT_ACTION_DELETE,
					AUDIT_RESOURCE_VALUE_MAP,
					_s("Value map \"%1$s\" \"%2$s\".", Nest.value(valueMapToDelete,"name").$(), Nest.value(valueMapToDelete,"valuemapid").$())
				);
				show_messages(true, msgOk);
				unset(_REQUEST,"form");
				
				DBend(executor, true);
			}
		} catch (Exception e) {
			DBend(executor, false);
			error(e.getMessage());
			show_messages(false, null, msgFail);
		}

		/* Display */
		CComboBox generalComboBox = new CComboBox("configDropDown", "adm.valuemapping.action", "redirect(this.options[this.selectedIndex].value);");
		generalComboBox.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
//FIXME			
//			"adm.images.action", _("Images"),
//			"adm.iconmapping.action", _("Icon mapping"),
//			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"cbn.action?actionType=DEPT", _("DEPT"),
			"cbn.action?actionType=mRoom", _("mRoom"),
			"cbn.action?actionType=Cabinet", _("Cabinet"),
			"cbn.action?actionType=firm", _("FIRM"),
			"adm.operationsystem.action?actionType=otsys", _("otsys")
	//		"adm.operationsystem.action","操作系统类型"
			//"users.action",_("notice")
		));

		CForm valueMapForm = new CForm();
		valueMapForm.cleanItems();
		valueMapForm.addItem(generalComboBox);
		
		CWidget valueMapWidget = new CWidget();
		valueMapWidget.addPageHeader(SPACE,valueMapForm);
		
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		if (!isset(_REQUEST,"form")) {
			createForm.addItem(new CSubmit("form", _("Create value map"),"","orange create"));
		}
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(createForm));
		valueMapWidget.addItem(headerActions);
		
		CView valueMapView;
		if (isset(_REQUEST,"form")) {
			Map data = map(
				"form", get_request("form", "1"),
				"form_refresh", get_request("form_refresh", 0),
				"valuemapid", get_request("valuemapid"),
				"mappings", array(),
				"mapname", "",
				"confirmMessage", null,
				"add_value", get_request("add_value"),
				"add_newvalue", get_request("add_newvalue")
			);

			if (isset(data,"valuemapid")) {
				Nest.value(data,"mapname").$(Nest.value(dbValueMap,"name").$());

				Map params = new HashMap();
				if (empty(Nest.value(data,"form_refresh").$())) {
					params.put("valuemapid", Nest.value(data,"valuemapid").$());
					Nest.value(data,"mappings").$(DBselect(executor,
						"SELECT m.mappingid,m.value,m.newvalue FROM mappings m WHERE m.valuemapid=#{valuemapid}",
						params
					));
				} else {
					Nest.value(data,"mapname").$(get_request("mapname", ""));
					Nest.value(data,"mappings").$(get_request("mappings", array()));
				}

				params.put("valuemapid", Nest.value(data,"valuemapid").$());
				Map valueMapCount = DBfetch(DBselect(
					executor,
					"SELECT COUNT(i.itemid) AS cnt FROM items i WHERE i.valuemapid=#{valuemapid}",
					params
				));

				Nest.value(data,"confirmMessage").$(Nest.value(valueMapCount,"cnt").asInteger()>0
					? _n("Delete selected value mapping? It is used for %d item!",
							"Delete selected value mapping? It is used for %d items!", Nest.value(valueMapCount,"cnt").$())
					: _("Delete selected value mapping?")
				);
			}

			if (empty(Nest.value(data,"valuemapid").$()) && !empty(Nest.value(data,"form_refresh").$())) {
				Nest.value(data,"mapname").$(get_request("mapname", ""));
				Nest.value(data,"mappings").$(get_request("mappings", array()));
			}

			order_result(Nest.value(data,"mappings").asCArray(), "value");

			valueMapView = new CView("administration.general.valuemapping.edit", data);
		} else {
			Map data = map(
				"valuemaps", array()
			);

//			valueMapWidget.addItem(BR());

			CArray<Map> dbValueMaps = DBselect(
				executor,
				"SELECT v.valuemapid,v.name"+
				" FROM valuemaps v"
			);
			for(Map dbValueMap : dbValueMaps) {
				Nest.value(data,"valuemaps",dbValueMap.get("valuemapid")).$(dbValueMap);
				Nest.value(data,"valuemaps",dbValueMap.get("valuemapid"),"maps").$(array());
			}
			order_result(Nest.value(data,"valuemaps").asCArray(), "name");

			CArray<Map> dbMaps = DBselect(
				executor,
				"SELECT m.valuemapid,m.value,m.newvalue"+
				" FROM mappings m"
			);
			for(Map dbMap : dbMaps) {
				Nest.value(data,"valuemaps",dbMap.get("valuemapid"),"maps").asCArray().add(map(
					"value", Nest.value(dbMap,"value").$(),
					"newvalue", Nest.value(dbMap,"newvalue").$()
				));
			}

			valueMapView = new CView("administration.general.valuemapping.list", data);
		}

		valueMapWidget.addItem(valueMapView.render(getIdentityBean(), executor));
		valueMapWidget.show();
	}
}
