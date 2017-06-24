package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_ICON;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CIconMapGet;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmIconmappingAction extends RadarBaseAction {
	
	private CArray<Map> iconMap;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of icon mapping"));
		page("file", "adm.iconmapping.action");
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
				"iconmapid",		array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,	"(isset({form})&&{form}==\"update\")||isset({delete})"),
				"iconmap",		array(T_RDA_STR, O_OPT, null,			null,	"isset({save})"),
				"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
				"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
				"clone",			array(T_RDA_STR, O_OPT, null,			null,	null),
				"form",				array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
				"form_refresh",	array(T_RDA_INT, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions  */
		if (isset(_REQUEST,"iconmapid")) {
			CIconMapGet imoptions = new CIconMapGet();
			imoptions.setIconmapIds(get_request_asLong("iconmapid"));
			imoptions.setOutput(API_OUTPUT_EXTEND);
			imoptions.setEditable(true);
			imoptions.setPreserveKeys(true);
			imoptions.setSelectMappings(API_OUTPUT_EXTEND);
			this.iconMap = API.IconMap(getIdentityBean(), executor).get(imoptions);
			if (empty(this.iconMap)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
			Nest.value(_REQUEST,"iconmap","mappings").$(isset(Nest.value(_REQUEST,"iconmap","mappings").$())
				? Nest.value(_REQUEST,"iconmap","mappings").asCArray()
				: array());

			int i = 0;
			for(Map mapping : (CArray<Map>)Nest.value(_REQUEST,"iconmap","mappings").asCArray()) {
				Nest.value(mapping,"sortorder").$(i++);
			}

			CArray<Long[]> result = null;
			String msgOk = null;
			String msgErr = null;
			if (isset(_REQUEST,"iconmapid")) {
				Nest.value(_REQUEST,"iconmap","iconmapid").$(Nest.value(_REQUEST,"iconmapid").$());
				result = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.IconMap(getIdentityBean(), executor).update(Nest.value(_REQUEST,"iconmap").asCArray());
					}
				}, null);
				msgOk = _("Icon map updated");
				msgErr = _("Cannot update icon map");
			} else {
				result = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.IconMap(getIdentityBean(), executor).create(Nest.value(_REQUEST,"iconmap").asCArray());
					}
				}, null);
				msgOk = _("Icon map created");
				msgErr = _("Cannot create icon map");
			}

			show_messages(!empty(result), msgOk, msgErr);

			if (!empty(result)) {
				unset(_REQUEST,"form");
			}
		} else if (isset(_REQUEST,"delete")) {
			CArray<Long[]> result = Call(new Wrapper<CArray<Long[]>>() {
				@Override
				protected CArray<Long[]> doCall() throws Throwable {
					return API.IconMap(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"iconmapid").asLong());
				}
			}, null);
			if (!empty(result)) {
				unset(_REQUEST,"form");
			}
			show_messages(!empty(result), _("Icon map deleted"), _("Cannot delete icon map"));
		} else if (isset(_REQUEST,"clone")) {
			unset(_REQUEST,"iconmapid");
			Nest.value(_REQUEST,"form").$("clone");
		}

		/* Display */
		CComboBox generalComboBox = new CComboBox("configDropDown", "adm.iconmapping.action", "redirect(this.options[this.selectedIndex].value);");
		generalComboBox.addItems((CArray)map(
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
		CForm iconMapForm = new CForm();
		iconMapForm.cleanItems();
		iconMapForm.addItem(generalComboBox);

		if (!isset(_REQUEST,"form")) {
			iconMapForm.addItem(new CSubmit("form", _("Create icon map")));
		}

		CWidget iconMapWidget = new CWidget();
		iconMapWidget.addPageHeader(_("CONFIGURATION OF ICON MAPPING"), iconMapForm);

		CArray data = map(
			"form_refresh", get_request("form_refresh", 0),
			"iconmapid", get_request("iconmapid"),
			"iconList", array(),
			"inventoryList", array()
		);

		CImageGet ioptions = new CImageGet();
		ioptions.setOutput(new String[]{"imageid", "name"});
		ioptions.setFilter("imagetype", String.valueOf(IMAGE_TYPE_ICON));
		ioptions.setPreserveKeys(true);
		CArray<Map> iconList = API.Image(getIdentityBean(), executor).get(ioptions);
		order_result(iconList, "name");

		for(Map icon : iconList) {
			Nest.value(data,"iconList",icon.get("imageid")).$(Nest.value(icon,"name").$());
		}

		CArray<Map> inventoryFields = getHostInventories();
		for(Map field : inventoryFields) {
			Nest.value(data,"inventoryList",field.get("nr")).$(Nest.value(field,"title").$());
		}

		CView iconMapView = null;
		if (isset(_REQUEST,"form")) {
			if (Nest.value(data,"form_refresh").asBoolean() || ("clone".equals(Nest.value(_REQUEST,"form").asString()))) {
				Nest.value(data,"iconmap").$(get_request("iconmap"));
			} else if (isset(_REQUEST,"iconmapid")) {
				Nest.value(data,"iconmap").$(reset(iconMap));
			} else {
				Map firstIcon = reset(iconList);
				Nest.value(data,"iconmap").$(map(
					"name", "",
					"default_iconid", Nest.value(firstIcon,"imageid").$(),
					"mappings", array()
				));
			}

			iconMapView = new CView("administration.general.iconmap.edit", data);
		} else {
			iconMapWidget.addHeader(_("Icon mapping"));

			CIconMapGet imoptions = new CIconMapGet();
			imoptions.setOutput(API_OUTPUT_EXTEND);
			imoptions.setEditable(true);
			imoptions.setPreserveKeys(true);
			imoptions.setSelectMappings(API_OUTPUT_EXTEND);
			CArray<Map> iconmaps = API.IconMap(getIdentityBean(), executor).get(imoptions);
			Nest.value(data,"iconmaps").$(iconmaps);
			order_result(iconmaps, "name");

			iconMapView = new CView("administration.general.iconmap.list", data);
		}

		iconMapWidget.addItem(iconMapView.render(getIdentityBean(), executor));
		iconMapWidget.show();
	}
}
