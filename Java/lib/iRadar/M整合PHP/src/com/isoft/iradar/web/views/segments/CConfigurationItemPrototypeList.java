package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.ItemsUtil.get_realhost_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_realrule_by_itemid_and_hostid;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicator;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationItemPrototypeList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget itemsWidget = new CWidget();

		// create new item button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		createForm.addItem(new CSubmit("form", _("Create item prototype")));
		itemsWidget.addPageHeader(_("CONFIGURATION OF ITEM PROTOTYPES"), createForm);

		// header
		itemsWidget.addHeader(array(_("Item prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));
		itemsWidget.addHeaderRowNumber();
		itemsWidget.addItem(get_header_host_table(idBean, executor, "items", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));

		// create form
		CForm itemForm = new CForm();
		itemForm.setName("items");
		itemForm.addVar("hostid", Nest.value(data,"hostid").$());
		itemForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());

		// create table
		CTableInfo itemTable = new CTableInfo(_("No item prototypes found."));

		Curl csortLink = new Curl();
		csortLink.setArgument("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		String sortLink = csortLink.getUrl();

		itemTable.setHeader(array(
			new CCheckBox("all_items", false, "checkAll(\""+itemForm.getName()+"\", \"all_items\", \"group_itemid\");"),
			make_sorting_header(_("Name"),"name", sortLink),
			make_sorting_header(_("Key"), "key_", sortLink),
			make_sorting_header(_("Interval"), "delay", sortLink),
			make_sorting_header(_("History"), "history", sortLink),
			make_sorting_header(_("Trends"), "trends", sortLink),
			make_sorting_header(_("Type"), "type", sortLink),
			_("Applications"),
			make_sorting_header(_("Status"), "status", sortLink)
		));

		for(Map item : (CArray<Map>)Nest.value(data,"items").asCArray()) {
			CArray description = array();
			if (!empty(Nest.value(item,"templateid").$())) {
				Map template_host = get_realhost_by_itemid(idBean, executor, Nest.value(item,"templateid").asString());
				String templateDiscoveryRuleId = get_realrule_by_itemid_and_hostid(executor, Nest.value(data,"parent_discoveryid").asString(), Nest.value(template_host,"hostid").asString());

				description.add(new CLink(Nest.value(template_host,"name").$(), "?parent_discoveryid="+templateDiscoveryRuleId, "unknown"));
				description.add(NAME_DELIMITER);
			}
			description.add(new CLink(
				Nest.value(item,"name_expanded").$(),
				"?form=update&itemid="+item.get("itemid")+"&parent_discoveryid="+data.get("parent_discoveryid")
			));

			CLink status = new CLink(
				itemIndicator(Nest.value(item,"status").asInteger()),
				"?group_itemid="+item.get("itemid")+
					"&parent_discoveryid="+data.get("parent_discoveryid")+
					"&go="+(Nest.value(item,"status").asBoolean() ? "activate" : "disable"),
				itemIndicatorStyle(Nest.value(item,"status").asInteger())
			);

			String applications = null;
			if (!empty(Nest.value(item,"applications").$())) {
				order_result(Nest.value(item,"applications").asCArray(), "name");

				CArray<String> capplications = rda_objectValues(Nest.value(item,"applications").$(), "name");
				applications  = implode(", ", capplications);
				if (empty(applications)) {
					applications = "-";
				}
			} else {
				applications = "-";
			}

			itemTable.addRow(array(
				new CCheckBox("group_itemid["+item.get("itemid")+"]", false, null, Nest.value(item,"itemid").asString()),
				description,
				Nest.value(item,"key_").$(),
				Nest.value(item,"delay").$(),
				Nest.value(item,"history").$(),
				in_array(Nest.value(item,"value_type").$(), array(ITEM_VALUE_TYPE_STR, ITEM_VALUE_TYPE_LOG, ITEM_VALUE_TYPE_TEXT))
					? "" : Nest.value(item,"trends").$(),
				item_type2str(Nest.value(item,"type").asInteger()),
				new CCol(applications, "wraptext"),
				status
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected item prototypes?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected item prototypes?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected item prototypes?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");

		rda_add_post_js("chkbxRange.pageGoName = \"group_itemid\";");
		rda_add_post_js("chkbxRange.prefix = \""+data.get("parent_discoveryid")+"\";");
		rda_add_post_js("cookie.prefix = \""+data.get("parent_discoveryid")+"\";");

		// append table to form
		itemForm.addItem(array(Nest.value(data,"paging").$(), itemTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		itemsWidget.addItem(itemForm);

		return itemsWidget;
	}

}
