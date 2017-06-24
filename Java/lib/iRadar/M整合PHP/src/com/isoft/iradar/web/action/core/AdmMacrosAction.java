package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_MACRO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_macros;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_message;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CUserMacroGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmMacrosAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of macros"));
		page("file", "adm.macros.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"macros_rem",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"macros",				array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"macro_new",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	"isset({macro_add})"),
			"value_new",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	"isset({macro_add})"),
			"macro_add" ,		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT,	null,	null,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		boolean result = true;
		if (isset(_REQUEST,"save")) {
			try {
				DBstart(executor);
				
				CUserMacroGet umoptions = new CUserMacroGet();
				umoptions.setGlobalMacro(true);
				umoptions.setOutput(API_OUTPUT_EXTEND);
				umoptions.setPreserveKeys(true);
				CArray<Map> globalMacros = API.UserMacro(getIdentityBean(), executor).get(umoptions);

				CArray<Map> newMacros = Clone.deepcopy(get_request("macros", array()));

				// remove empty new macro lines
				for (Entry<Object, Map> e : newMacros.entrySet()) {
				    Object number = e.getKey();
				    Map newMacro = e.getValue();
					if (!isset(newMacro,"globalmacroid") && rda_empty(Nest.value(newMacro,"macro").$()) && rda_empty(Nest.value(newMacro,"value").$())) {
						unset(newMacros,number);
					}
				}

				//CArray duplicatedMacros = array();
				for (Entry<Object, Map> e : newMacros.entrySet()) {
				    Object number = e.getKey();
				    Map newMacro = e.getValue();
					// transform macros to uppercase {_aaa} => {$AAA}
					Nest.value(newMacros,number,"macro").$(StringUtils.upperCase(Nest.value(newMacro,"macro").asString()));
				}

				// update
				CArray<Map> macrosToUpdate = array();
				for (Entry<Object, Map> e : Clone.deepcopy(newMacros).entrySet()) {
				    Object number = e.getKey();
				    Map newMacro = e.getValue();
					if (isset(Nest.value(newMacro,"globalmacroid").$()) && isset(globalMacros, newMacro.get("globalmacroid"))) {

						Map dbGlobalMacro = globalMacros.get(newMacro.get("globalmacroid"));

						// remove item from new macros array
						unset(newMacros,number);
						unset(globalMacros, newMacro.get("globalmacroid"));

						// if the macro is unchanged - skip it
						if (Cphp.equals(dbGlobalMacro, newMacro)) {
							continue;
						}

						Nest.value(macrosToUpdate,newMacro.get("globalmacroid")).$(newMacro);
					}
				}
				if (!empty(macrosToUpdate)) {
					if (empty(API.UserMacro(getIdentityBean(), executor).updateGlobal(macrosToUpdate))) {
						throw new Exception(_("Cannot update macro."));
					}
					for(Map macro : macrosToUpdate) {
						add_audit_ext(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_MACRO, Nest.value(macro,"globalmacroid").asLong(), Nest.value(macro,"macro").asString()+SPACE+RARR+SPACE+Nest.value(macro,"value").$(), null, null, null);
					}
				}

				// delete the remaining global macros
				if (!empty(globalMacros)) {
					CArray ids = rda_objectValues(globalMacros, "globalmacroid");
					if (empty(API.UserMacro(getIdentityBean(), executor).deleteGlobal(ids.valuesAsLong()))) {
						throw new Exception(_("Cannot remove macro."));
					}
					for(Map macro : globalMacros) {
						add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_MACRO, Nest.value(macro,"globalmacroid").asLong(), Nest.value(macro,"macro").asString()+SPACE+RARR+SPACE+Nest.value(macro,"value").$(), null, null, null);
					}
				}

				// create
				if (!empty(newMacros)) {
					// mark marcos as new
					for (Entry<Object, Map> e : newMacros.entrySet()) {
					    Object number = e.getKey();
					    //Map macro = e.getValue();
						Nest.value(_REQUEST,"macros",number,"type").$("new");
					}

					CArray newMacrosIds = API.UserMacro(getIdentityBean(), executor).createGlobal(array_values(newMacros));
					if (empty(newMacrosIds)) {
						throw new Exception(_("Cannot add macro."));
					}
					
					umoptions = new CUserMacroGet();
					umoptions.setGlobalMacroIds(Nest.array(newMacrosIds,"globalmacroids").asLong());
					umoptions.setGlobalMacro(true);
					umoptions.setOutput(API_OUTPUT_EXTEND);
					CArray<Map> newMacrosCreated = API.UserMacro(getIdentityBean(), executor).get(umoptions);
					for(Map macro : newMacrosCreated) {
						add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_MACRO, Nest.value(macro,"globalmacroid").asLong(), Nest.value(macro,"macro").asString()+SPACE+RARR+SPACE+Nest.value(macro,"value").$(), null, null, null);
					}
				}

				// reload macros after updating to properly display them in the form
				umoptions = new CUserMacroGet();
				umoptions.setGlobalMacro(true);
				umoptions.setOutput(API_OUTPUT_EXTEND);
				umoptions.setPreserveKeys(true);
				Nest.value(_REQUEST,"macros").$(API.UserMacro(getIdentityBean(), executor).get(umoptions));

				result = true;
				DBend(executor, result);
				show_message(_("Macros updated"));
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				DBend(executor, false);
				error(e.getMessage());
				show_error_message(_("Cannot update macros"));
			}
		}

		/* Display */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.macros.action", "redirect(this.options[this.selectedIndex].value);");
		cmbConf.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
//FIXME
//			"adm.images.action", _("Images"),
//			"adm.iconmapping.action", _("Icon mapping"),
			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"adm.other.action", _("Other")
		));
		form.addItem(cmbConf);

		CWidget cnf_wdgt = new CWidget();
		cnf_wdgt.addPageHeader(_("CONFIGURATION OF MACROS"), form);

		Map data = array();
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));
		Nest.value(data,"macros").$(array());

		if (!empty(Nest.value(data,"form_refresh").$())) {
			Nest.value(data,"macros").$(get_request("macros", array()));
		} else {
			CUserMacroGet umoptions = new CUserMacroGet();
			umoptions.setGlobalMacro(true);
			umoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> macros = API.UserMacro(getIdentityBean(), executor).get(umoptions);
			Nest.value(data,"macros").$(macros);
		}
		if (empty(Nest.value(data,"macros").$())) {
			Nest.value(data,"macros").$(map(
				0, map(
					"macro", "",
					"value", ""
				)
			));
		}
		if (result) {
			Nest.value(data,"macros").$(order_macros(Nest.value(data,"macros").asCArray(), "macro"));
		}
		CView macrosForm = new CView("administration.general.macros.edit", data);
		cnf_wdgt.addItem(macrosForm.render(getIdentityBean(), executor));
		cnf_wdgt.show();
	}
}
