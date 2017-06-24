package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_HIDE;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_INVERT_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_SHOW;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_BLUE;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_GREEN;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_RED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_FILTER_SIZE;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HISTORY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringHistory extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/monitoring.history.js");

		CWidget historyWidget = new CWidget();

		CArray header = map("left", count(Nest.value(data,"items").$())+SPACE+_("ITEMS"), "right", array());
		CArray<String> headerPlaintext = array();

		if (count(Nest.value(data,"items").$()) == 1) {
			Nest.value(header,"left").$(array(new CLink(Nest.value(data,"item","hostname").$(), "latest.action?hostid="+Nest.value(data,"item","hostid").$()), NAME_DELIMITER, Nest.value(data,"item","name_expanded").$()));
			headerPlaintext.add(Nest.value(data,"item","hostname").$()+NAME_DELIMITER+Nest.value(data,"item","name_expanded").$());

			if ("showgraph".equals(Nest.value(data,"action").$())) {
				Nest.value(header,"right").asCArray().add(get_icon(idBean, executor, "favourite", map(
					"fav", "web.favorite.graphids",
					"elid", Nest.value(data,"item","itemid").$(),
					"elname", "itemid"
				)));
			}
		}

		Nest.value(header,"right").asCArray().add(SPACE);
		Nest.value(header,"right").asCArray().add(get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$())));

		// append action form to header
		CForm actionForm = new CForm("get");
		actionForm.addVar("itemid", Nest.value(_REQUEST,"itemid").$());

		if (isset(_REQUEST,"filter_task")) {
			actionForm.addVar("filter_task", Nest.value(_REQUEST,"filter_task").$());
		}
		if (isset(_REQUEST,"filter")) {
			actionForm.addVar("filter", Nest.value(_REQUEST,"filter").$());
		}
		if (isset(_REQUEST,"mark_color")) {
			actionForm.addVar("mark_color", Nest.value(_REQUEST,"mark_color").$());
		}

		CComboBox actionComboBox = new CComboBox("action", Nest.value(data,"action").$(), "submit()");
		if (isset(Nest.value(data,"iv_numeric",Nest.value(data,"item","value_type").$()).$())) {
			actionComboBox.addItem("showgraph", _("Graph"));
		}
		actionComboBox.addItem("showvalues", _("Values"));
		actionComboBox.addItem("showlatest", _("500 latest values"));
		actionForm.addItem(actionComboBox);

		if ( !"showgraph".equals(Nest.value(data,"action").$())) {
			actionForm.addItem(array(SPACE, new CSubmit("plaintext", _("As plain text"))));
		}

		array_unshift(Nest.value(header,"right").asCArray(), actionForm, SPACE);

		// create filter
		CFormTable filterForm = null;
		if ("showvalues".equals(Nest.value(data,"action").$()) || "showlatest".equals(Nest.value(data,"action").$())) {
			if (isset(Nest.value(data,"iv_string",Nest.value(data,"item","value_type").$()).$())) {
				filterForm  = new CFormTable(null, null, "get");
				filterForm.setAttribute("name", "rda_filter");
				filterForm.setAttribute("id", "rda_filter");
				filterForm.addVar("action", Nest.value(data,"action").$());
				filterForm.addVar("itemid", rda_toHash(Nest.value(_REQUEST,"itemid").$()));

				CListBox itemListbox = new CListBox("cmbitemlist[]");
				CArray<Map> itemsData = array();
				for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"items").asCArray()).entrySet()) {
				    Object itemid = e.getKey();
				    Map item = e.getValue();
					if (!isset(Nest.value(data,"iv_string",item.get("value_type")).$())) {
						unset(Nest.value(data,"items").asCArray(),itemid);
						continue;
					}

					Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
					Nest.value(itemsData,itemid,"id").$(itemid);
					Nest.value(itemsData,itemid,"name").$(Nest.value(host,"name").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$());
				}

				order_result(itemsData, "name");
				for(Map item : itemsData) {
					itemListbox.addItem(Nest.value(item,"id").$(), Nest.value(item,"name").asString());
				}

				CButton addItemButton = new CButton("add_log", _("Add"), "return PopUp('popup.action?multiselect=1&real_hosts=1"+
						"&reference=itemid&srctbl=items&value_types[]="+Nest.value(data,"item","value_type").asString()+"&srcfld1=itemid');");
				Object deleteItemButton = null;

				if (count(Nest.value(data,"items").$()) > 1) {
					deleteItemButton = new CSubmit("remove_log", _("Remove selected"), "javascript: removeSelectedItems(\"cmbitemlist_\", \"itemid\")");
				}

				filterForm.addRow(_("Items list"), array(itemListbox, BR(), addItemButton, deleteItemButton));
				filterForm.addRow(_("Select rows with value like"), new CTextBox("filter", get_request("filter", ""), RDA_TEXTBOX_FILTER_SIZE));

				Integer filterTask = get_request("filter_task", 0);

				CComboBox taskComboBox = new CComboBox("filter_task", filterTask, "submit()");
				taskComboBox.addItem(FILTER_TASK_SHOW, _("Show selected"));
				taskComboBox.addItem(FILTER_TASK_HIDE, _("Hide selected"));
				taskComboBox.addItem(FILTER_TASK_MARK, _("Mark selected"));
				taskComboBox.addItem(FILTER_TASK_INVERT_MARK, _("Mark others"));
				CArray tasks = array(taskComboBox);

				if (str_in_array(filterTask, array(FILTER_TASK_MARK, FILTER_TASK_INVERT_MARK))) {
					CComboBox colorComboBox = new CComboBox("mark_color", get_request("mark_color", 0));
					colorComboBox.addItem(MARK_COLOR_RED, _("as Red"));
					colorComboBox.addItem(MARK_COLOR_GREEN, _("as Green"));
					colorComboBox.addItem(MARK_COLOR_BLUE, _("as Blue"));

					tasks.add(SPACE);
					tasks.add(colorComboBox);
				}

				filterForm.addRow(_("Selected"), tasks);
				filterForm.addItemToBottomRow(new CSubmit("select", _("GoFilter")));
			}
		}

		// create history screen
		CScreenBase screen = CScreenBuilder.getScreen(idBean, executor, map(
			"resourcetype", SCREEN_RESOURCE_HISTORY,
			"action", Nest.value(data,"action").$(),
			"items", Nest.value(data,"items").$(),
			"item", Nest.value(data,"item").$(),
			"itemids", Nest.value(data,"itemids").$(),
			"profileIdx", "web.item.graph",
			"profileIdx2", reset(Nest.value(data,"itemids").asCArray()),
			"period", Nest.value(data,"period").$(),
			"stime", Nest.value(data,"stime").$(),
			"filter", get_request("filter"),
			"filter_task", get_request("filter_task"),
			"mark_color", get_request("mark_color"),
			"plaintext", Nest.value(data,"plaintext").$()
		));

		// append plaintext to widget
		if (!empty(Nest.value(data,"plaintext").$())) {
			CSpan plaintextSpan = new CSpan(null, "textblackwhite");

			for(String text : headerPlaintext) {
				plaintextSpan.addItem(array(new CJSScript(text), BR()));
			}

			CArray cscreen = (CArray)screen.get();

			CTag pre = new CTag("pre", "true");
			for(Object text : cscreen) {
				pre.addItem(new CJSScript(text));
			}
			plaintextSpan.addItem(pre);
			historyWidget.addItem(plaintextSpan);
		} else {// append graph to widget
			CTable right = new CTable();
			right.addRow(Nest.value(header,"right").$());

			historyWidget.addPageHeader(Nest.value(header,"left").$(), right);
			historyWidget.addItem(SPACE);

			if (isset(Nest.value(data,"iv_string",Nest.value(data,"item","value_type").$()).$())) {
				historyWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor, "web.history.filter.state", 1)).asInteger());
			}

			CTable historyTable = new CTable(null, "maxwidth");
			historyTable.addRow(screen.get());

			historyWidget.addItem(historyTable);

			if ("showvalues".equals(Nest.value(data,"action").$()) || "showgraph".equals(Nest.value(data,"action").$())) {
				historyWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor, "web.history.filter.state", 1)).asInteger());

				CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
					"timeline", screen.timeline,
					"profileIdx", screen.profileIdx
				));
			}
		}

		return historyWidget;
	}

}
