package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_FIXED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_ITEM_VALUE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_itemid;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.insert_show_color_picker_javascript;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.GraphsUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationGraphEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget graphWidget = new CWidget();

		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			graphWidget.addPageHeader(_("CONFIGURATION OF GRAPH PROTOTYPES"));
			graphWidget.addItem(get_header_host_table(idBean, executor, "graphs", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));
		} else {
			graphWidget.addPageHeader(_("CONFIGURATION OF GRAPHS"));
			graphWidget.addItem(get_header_host_table(idBean, executor, "graphs", Nest.value(data,"hostid").asLong(true)));
		}

		// create form
		CForm graphForm = new CForm();
		graphForm.setName("graphForm");
		graphForm.addVar("form", Nest.value(data,"form").$());
		graphForm.addVar("form_refresh", Nest.value(data,"form_refresh").$());
		graphForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			graphForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		if (!empty(Nest.value(data,"graphid").$())) {
			graphForm.addVar("graphid", Nest.value(data,"graphid").$());
		}
		graphForm.addVar("ymin_itemid", Nest.value(data,"ymin_itemid").$());
		graphForm.addVar("ymax_itemid", Nest.value(data,"ymax_itemid").$());

		// create form list
		CFormList graphFormList = new CFormList("graphFormList");
		if (!empty(Nest.value(data,"templates").$())) {
			graphFormList.addRow(_("Parent graphs"), Nest.value(data,"templates").$());
		}
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		graphFormList.addRow(_("Name"), nameTextBox);
		graphFormList.addRow(_("Width"), new CNumericBox("width", Nest.value(data,"width").asString(), 5));
		graphFormList.addRow(_("Height"), new CNumericBox("height", Nest.value(data,"height").asString(), 5));

		CComboBox graphTypeComboBox = new CComboBox("graphtype", Nest.value(data,"graphtype").$(), "submit()");
		graphTypeComboBox.addItems((CArray)GraphsUtil.graphType());
		graphFormList.addRow(_("Graph type"), graphTypeComboBox);

		// append legend to form list
		graphFormList.addRow(_("Show legend"), new CCheckBox("show_legend", Nest.value(data,"show_legend").asBoolean(), null, 1));

		// append graph types to form list
		if (Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_NORMAL || Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_STACKED) {
			graphFormList.addRow(_("Show working time"), new CCheckBox("show_work_period", Nest.value(data,"show_work_period").asBoolean(), null, 1));
			graphFormList.addRow(_("Show triggers"), new CCheckBox("show_triggers", Nest.value(data,"show_triggers").asBoolean(), null, 1));

			if (Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_NORMAL) {
				// percent left
				CTextBox percentLeftTextBox = new CTextBox("percent_left", Nest.value(data,"percent_left").asString(), 6, false, 7);
				CCheckBox percentLeftCheckbox = new CCheckBox("visible[percent_left]", true, "javascript: showHideVisible(\"percent_left\");", 1);

				if (Nest.value(data,"percent_left").asInteger() == 0) {
					percentLeftTextBox.attr("style", "visibility: hidden;");
					percentLeftCheckbox.setChecked(false);
				}
				graphFormList.addRow(_("Percentile line (left)"), array(percentLeftCheckbox, SPACE, percentLeftTextBox));

				// percent right
				CTextBox percentRightTextBox = new CTextBox("percent_right", Nest.value(data,"percent_right").asString(), 6, false, 7);
				CCheckBox percentRightCheckbox = new CCheckBox("visible[percent_right]", true, "javascript: showHideVisible(\"percent_right\");", 1);

				if (Nest.value(data,"percent_right").asInteger() == 0) {
					percentRightTextBox.attr("style", "visibility: hidden;");
					percentRightCheckbox.setChecked(false);
				}
				graphFormList.addRow(_("Percentile line (right)"), array(percentRightCheckbox, SPACE, percentRightTextBox));
			}

			CArray yaxisMinData = array();

			CComboBox yTypeComboBox = new CComboBox("ymin_type", Nest.value(data,"ymin_type").$());
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_CALCULATED, _("Calculated"));
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_FIXED, _("Fixed"));
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_ITEM_VALUE, _("Item"));

			yaxisMinData.add(yTypeComboBox);

			if (Nest.value(data,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_FIXED) {
				yaxisMinData.add(new CTextBox("yaxismin", Nest.value(data,"yaxismin").asString(), 7));
			} else if (Nest.value(data,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				graphForm.addVar("yaxismin", Nest.value(data,"yaxismin").$());

				String ymin_name = "";
				if (!empty(Nest.value(data,"ymin_itemid").$())) {
					Map min_host = get_host_by_itemid(idBean, executor, Nest.value(data,"ymin_itemid").asString());

					CArray<Map> minItems = CMacrosResolverHelper.resolveItemNames(idBean, executor, array(get_item_by_itemid(executor, Nest.value(data,"ymin_itemid").asString())));
					Map minItem = reset(minItems);

					ymin_name = Nest.value(min_host,"name").asString()+NAME_DELIMITER+Nest.value(minItem,"name_expanded").$();
				}

				yaxisMinData.add(new CTextBox("ymin_name", ymin_name, 36, true));
				yaxisMinData.add(new CButton("yaxis_min", _("Select"), "javascript: "+
					"return PopUp(\"popup.action?dstfrm="+graphForm.getName()+
						"&dstfld1=ymin_itemid"+
						"&dstfld2=ymin_name"+
						"&srctbl=items"+
						"&srcfld1=itemid"+
						"&srcfld2=name"+
						"&numeric=1"+
						"&writeonly=1\" + getOnlyHostParam(), 0, 0, \"rda_popup_item\");",
					"formlist"
				));

				// select prototype button
				if (!empty(Nest.value(data,"parent_discoveryid").$())) {
					yaxisMinData.add( new CButton("yaxis_min_prototype", _("Select prototype"), "javascript: "+
						"return PopUp(\"popup.action?dstfrm="+graphForm.getName()+
							"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").asString()+
							"&dstfld1=ymin_itemid"+
							"&dstfld2=ymin_name"+
							"&srctbl=prototypes"+
							"&srcfld1=itemid"+
							"&srcfld2=name"+
							"&numeric=1\", 0, 0, \"rda_popup_item\");",
						"formlist"
					));
				}
			} else {
				graphForm.addVar("yaxismin", Nest.value(data,"yaxismin").$());
			}

			graphFormList.addRow(_("Y axis MIN value"), yaxisMinData);

			CArray yaxisMaxData = array();
			yTypeComboBox = new CComboBox("ymax_type", Nest.value(data,"ymax_type").$());
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_CALCULATED, _("Calculated"));
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_FIXED, _("Fixed"));
			yTypeComboBox.addItem(GRAPH_YAXIS_TYPE_ITEM_VALUE, _("Item"));

			yaxisMaxData.add(yTypeComboBox);

			if (Nest.value(data,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_FIXED) {
				yaxisMaxData.add(new CTextBox("yaxismax", Nest.value(data,"yaxismax").asString(), 7));
			} else if (Nest.value(data,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				graphForm.addVar("yaxismax", Nest.value(data,"yaxismax").$());

				String ymax_name = "";
				if (!empty(Nest.value(data,"ymax_itemid").$())) {
					Map max_host = get_host_by_itemid(idBean, executor, Nest.value(data,"ymax_itemid").asString());

					CArray<Map> maxItems = CMacrosResolverHelper.resolveItemNames(idBean, executor, array(get_item_by_itemid(executor, Nest.value(data,"ymax_itemid").asString())));
					Map maxItem = reset(maxItems);

					ymax_name = Nest.value(max_host,"name").asString()+NAME_DELIMITER+Nest.value(maxItem,"name_expanded").$();
				}

				yaxisMaxData.add(new CTextBox("ymax_name", ymax_name, 36, true));
				yaxisMaxData.add(new CButton("yaxis_max", _("Select"), "javascript: "+
					"return PopUp(\"popup.action?dstfrm="+graphForm.getName()+
						"&dstfld1=ymax_itemid"+
						"&dstfld2=ymax_name"+
						"&srctbl=items"+
						"&srcfld1=itemid"+
						"&srcfld2=name"+
						"&numeric=1"+
						"&writeonly=1\" + getOnlyHostParam(), 0, 0, \"rda_popup_item\");",
					"formlist"
				));

				// select prototype button
				if (!empty(Nest.value(data,"parent_discoveryid").$())) {
					yaxisMaxData.add(new CButton("yaxis_max_prototype", _("Select prototype"), "javascript: "+
						"return PopUp(\"popup.action?dstfrm="+graphForm.getName()+
							"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").asString()+
							"&dstfld1=ymax_itemid"+
							"&dstfld2=ymax_name"+
							"&srctbl=prototypes"+
							"&srcfld1=itemid"+
							"&srcfld2=name"+
							"&numeric=1\", 0, 0, \"rda_popup_item\");",
						"formlist"
					));
				}
			} else {
				graphForm.addVar("yaxismax", Nest.value(data,"yaxismax").$());
			}

			graphFormList.addRow(_("Y axis MAX value"), yaxisMaxData);
		} else {
			graphFormList.addRow(_("3D view"), new CCheckBox("show_3d", Nest.value(data,"show_3d").asBoolean(), null, 1));
		}

		// append items to form list
		CTable itemsTable = new CTable(null, "formElementTable");
		itemsTable.attr("style", "min-width: 700px;");
		itemsTable.attr("id", "itemsTable");
		itemsTable.setHeader(array(
			new CCol(SPACE, null, null, 15),
			new CCol(SPACE, null, null, 15),
			new CCol(_("Name"), null, null, (Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_NORMAL) ? 280 : 360),
			(Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED)
				? new CCol(_("Type"), null, null, 80) : null,
			new CCol(_("Function"), null, null, 80),
			(Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_NORMAL) ? new CCol(_("Draw style"), "nowrap", null, 80) : null,
			(Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_NORMAL || Nest.value(data,"graphtype").asInteger() == GRAPH_TYPE_STACKED)
				? new CCol(_("Y axis side"), "nowrap", null, 80) : null,
			new CCol(_("Colour"), null, null, 100),
			new CCol(_("Action"), null, null, 50)
		));

		CButton addButton = new CButton("add_item", _("Add"),
			"return PopUp(\"popup.action?writeonly=1&multiselect=1&dstfrm="+graphForm.getName()+
				(Nest.value(data,"normal_only").asBoolean() ? "&normal_only=1" : "")+
				"&srctbl=items&srcfld1=itemid&srcfld2=name&numeric=1\" + getOnlyHostParam(), 800, 600);",
			"link_menu"
		);

		CButton addPrototypeButton = null;
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			addPrototypeButton = new CButton("add_protoitem", _("Add prototype"),
				"return PopUp(\"popup.action?writeonly=1&multiselect=1&dstfrm="+graphForm.getName()+
					url_param(idBean, Nest.value(data,"graphtype").$(), false, "graphtype")+
					url_param(idBean, "parent_discoveryid")+
					(Nest.value(data,"normal_only").asBoolean() ? "&normal_only=1" : "")+
					"&srctbl=prototypes&srcfld1=itemid&srcfld2=name&numeric=1\", 800, 600);",
				"link_menu"
			);
		}
		itemsTable.addRow(new CRow(
			new CCol(array(addButton, SPACE, SPACE, SPACE, addPrototypeButton), null, 8),
			null,
			"itemButtonsRow"
		));

		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"items").asCArray()).entrySet()) {
		    Object n = e.getKey();
		    Map item = e.getValue();
			String name = Nest.value(item,"host").asString()+NAME_DELIMITER+Nest.value(item,"name_expanded").$();

			if (rda_empty(Nest.value(item,"drawtype").$())) {
				Nest.value(item,"drawtype").$(0);
			}

			if (rda_empty(Nest.value(item,"yaxisside").$())) {
				Nest.value(item,"yaxisside").$(0);
			}

			insert_js("loadItem("+n+", "+CJs.encodeJson(Nest.value(item,"gitemid").$())+", "+Nest.value(data,"graphid").$()+", "+Nest.value(item,"itemid").$()+", "+
				CJs.encodeJson(name)+", "+Nest.value(item,"type").$()+", "+Nest.value(item,"calc_fnc").$()+", "+Nest.value(item,"drawtype").$()+", "+
				Nest.value(item,"yaxisside").$()+", \""+Nest.value(item,"color").$()+"\", "+Nest.value(item,"flags").$()+");",
				true
			);
		}

		graphFormList.addRow(_("Items"), new CDiv(itemsTable, "objectgroup inlineblock border_dotted ui-corner-all"));

		// append tabs to form
		CTabView graphTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			graphTab.setSelected("0");
		}
		graphTab.addTab(
			"graphTab",
			empty(Nest.value(data,"parent_discoveryid").$()) ? _("Graph") : _("Graph prototype"), graphFormList
		);

		/*
		 * Preview tab
		 */
		CImg chartImage = new CImg("chart3.action?period=3600");
		chartImage.preload();

		CTable graphPreviewTable = new CTable(null, "center maxwidth");
		graphPreviewTable.addRow(new CDiv(chartImage, null, "previewChar"));
		graphTab.addTab("previewTab", _("Preview"), graphPreviewTable);
		graphForm.addItem(graphTab);

		// append buttons to form
		CSubmit saveButton = new CSubmit("save", _("Save"));
		CButtonCancel cancelButton = new CButtonCancel(url_param(idBean, "parent_discoveryid"));
		if (!empty(Nest.value(data,"graphid").$())) {
			CButtonDelete deleteButton = new CButtonDelete(
				!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete graph prototype?") : _("Delete graph?"),
				url_params(idBean, array("graphid", "parent_discoveryid", "hostid"))
			);
			CSubmit cloneButton = new CSubmit("clone", _("Clone"));

			if (!empty(Nest.value(data,"templateid").$())) {
				saveButton.setEnabled(false);
				deleteButton.setEnabled(false);
			}

			graphForm.addItem(makeFormFooter(saveButton, array(cloneButton, deleteButton, cancelButton)));
		} else {
			graphForm.addItem(makeFormFooter(saveButton, cancelButton));
		}

		// insert js (depended from some variables inside the file)
		insert_show_color_picker_javascript();
		includeSubView("js/configuration.graph.edit.js");

		// append form to widget
		graphWidget.addItem(graphForm);

		return graphWidget;
	}

}
