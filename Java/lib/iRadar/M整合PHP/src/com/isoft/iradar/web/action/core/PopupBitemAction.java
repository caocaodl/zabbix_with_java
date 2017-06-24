package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_LEFT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_RIGHT;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.T_RDA_CLR;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.insert_js_function;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CColor;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVisibilityBox;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupBitemAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Graph item"));
		Nest.value(page, "file").$(_("popup_bitem.action"));
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"dstfrm",			array(T_RDA_STR, O_MAND, P_SYS,	NOT_EMPTY,			null),
			"config",			array(T_RDA_INT, O_OPT,	 P_SYS,	IN("0,1,2,3"),		null),
			"gid",				array(T_RDA_INT, O_OPT,	 P_SYS,	BETWEEN(0,65535),	null),
			"list_name",		array(T_RDA_STR, O_OPT,	 P_SYS,	NOT_EMPTY,			"isset({save})&&isset({gid})"),
			"caption",			array(T_RDA_STR, O_OPT,	 null,	null,				null),
			"itemid",			array(T_RDA_INT, O_OPT,	 P_SYS, DB_ID+"({}!=0)", "isset({save})", _("Parameter")),
			"color",				array(T_RDA_CLR, O_OPT,	 null,	null, "isset({save})", _("Colour")),
			"calc_fnc",		array(T_RDA_INT, O_OPT,	 null,	IN("0,1,2,4,7,9"),	"isset({save})"),
			"axisside",			array(T_RDA_INT, O_OPT,	 null,	IN(GRAPH_YAXIS_SIDE_LEFT+","+GRAPH_YAXIS_SIDE_RIGHT), null),
			// actions
			"add",				array(T_RDA_STR, O_OPT,	 P_SYS|P_ACT,	null,	null),
			"save",				array(T_RDA_STR, O_OPT,	 P_SYS|P_ACT,	null,	null),
			// other
			"form",				array(T_RDA_STR, O_OPT,	 P_SYS,	null,	null),
			"form_refresh",	array(T_RDA_STR, O_OPT,	 null,	null,	null),
			"host",				array(T_RDA_STR, O_OPT,	 null,	null,	null),
			"name",			array(T_RDA_STR, O_OPT,	 null,	null,	null)
		);
		check_fields(getIdentityBean(), fields);
		
		Nest.value(_REQUEST,"caption").$(get_request("caption", ""));
		Nest.value(_REQUEST,"axisside").$(get_request("axisside",	GRAPH_YAXIS_SIDE_LEFT));
		
		if (rda_empty(Nest.value(_REQUEST,"caption").$()) && isset(_REQUEST,"itemid") && Nest.value(_REQUEST,"itemid").asLong() > 0) {
			CArray<Map> items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, array(get_item_by_itemid(executor,Nest.value(_REQUEST,"itemid").asString())));
			Map item = reset(items);

			Nest.value(_REQUEST,"caption").$(Nest.value(item,"name_expanded").$());
		}
		
		insert_js_function("add_bitem");
		insert_js_function("update_bitem");
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		if (isset(_REQUEST,"save") && !isset(_REQUEST,"gid")) {
			insert_js("add_bitem("+
					rda_jsvalue(Nest.value(_REQUEST,"dstfrm").asString())+","+
					rda_jsvalue(Nest.value(_REQUEST,"caption").asString())+",'"+
					Nest.value(_REQUEST,"itemid").asString()+"','"+
					Nest.value(_REQUEST,"color").asString()+"',"+
					Nest.value(_REQUEST,"calc_fnc").asString()+","+
					Nest.value(_REQUEST,"axisside").asString()+");\n"
			);
		}

		if (isset(_REQUEST,"save") && isset(_REQUEST,"gid")) {
			insert_js("update_bitem("+
					rda_jsvalue(Nest.value(_REQUEST,"dstfrm").asString())+","+
					rda_jsvalue(Nest.value(_REQUEST,"list_name").asString())+",'"+
					Nest.value(_REQUEST,"gid").asString()+"',"+
					rda_jsvalue(Nest.value(_REQUEST,"caption").asString())+",'"+
					Nest.value(_REQUEST,"itemid").asString()+"','"+
					Nest.value(_REQUEST,"color").asString()+"',"+
					Nest.value(_REQUEST,"calc_fnc").asString()+","+
					Nest.value(_REQUEST,"axisside").asString()+");\n"
				);
		} else {
			echo(SBR);

			CFormTable frmGItem = new CFormTable(_("New item for the graph"));
			frmGItem.setName("graph_item");
			frmGItem.setHelp("web.graph.item.action");

			frmGItem.addVar("dstfrm", Nest.value(_REQUEST,"dstfrm").$());

			Integer config = get_request("config", 1);
			Object gid = get_request("gid", null);
			Object list_name = get_request("list_name", null);
			String caption = get_request("caption", "");
			Integer itemid = get_request("itemid", 0);
			String color = get_request("color", "009900");
			Integer calc_fnc = get_request("calc_fnc", 2);
			Integer axisside = get_request("axisside", GRAPH_YAXIS_SIDE_LEFT);

			frmGItem.addVar("gid", gid);
			frmGItem.addVar("config", config);
			frmGItem.addVar("list_name", list_name);
			frmGItem.addVar("itemid", itemid);

			frmGItem.addRow(array(CVisibilityBox.instance("caption_visible", !rda_empty(caption), "caption", _("Default")),
				_("Caption")), new CTextBox("caption", caption, 50)
			);

			Map host = get_request("host", map());
			String itemName = get_request("name");
			if (!empty(host) && !empty(itemName)) {
				caption = Nest.value(host,"name").asString()+NAME_DELIMITER+itemName;
			}

			CTextBox txtCondVal = new CTextBox("name", caption, 50, true);

			CSubmit btnSelect = new CSubmit("btn1", _("Select"),
				"return PopUp(\"popup.action?"+
					"dstfrm="+frmGItem.getName()+
					"&dstfld1=itemid"+
					"&dstfld2=name"+
					"&srctbl=items"+
					"&srcfld1=itemid"+
					"&srcfld2=name"+
					"&monitored_hosts=1"+
					"&numeric=1\");",
				"T"
			);

			frmGItem.addRow(_("Parameter"), array(txtCondVal, btnSelect));

			CComboBox cmbFnc = new CComboBox("calc_fnc", calc_fnc);
			cmbFnc.addItem(CALC_FNC_MIN, _("min"));
			cmbFnc.addItem(CALC_FNC_AVG, _("avg"));
			cmbFnc.addItem(CALC_FNC_MAX, _("max"));
			cmbFnc.addItem(0, _("Count"));

			frmGItem.addRow(_("Function"), cmbFnc);

			if (config == 1) {
				CComboBox cmbAxis = new CComboBox("axisside", axisside);
				cmbAxis.addItem(GRAPH_YAXIS_SIDE_LEFT, _("Left"));
				cmbAxis.addItem(GRAPH_YAXIS_SIDE_RIGHT, _("Right"));

				frmGItem.addRow(_("Axis side"), cmbAxis);
			}

			if (config == 1) {
				frmGItem.addRow(_("Colour"), new CColor("color", color));
			} else {
				frmGItem.addVar("color", color);
			}

			frmGItem.addItemToBottomRow(new CSubmit("save", isset(gid) ? _("Save") : _("Add")));

			frmGItem.addItemToBottomRow(new CButtonCancel(null, "close_window();"));
			frmGItem.show();
		}
	}

}
