package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.ItemsUtil.item_status2str;
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

public class CConfigurationHostPrototypeList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget itemsWidget = new CWidget();

		Map discoveryRule = Nest.value(data,"discovery_rule").asCArray();

		// create new item button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		createForm.addItem(new CSubmit("form", _("Create host prototype")));
		itemsWidget.addPageHeader(_("CONFIGURATION OF HOST PROTOTYPES"), createForm);

		// header
		itemsWidget.addHeader(array(_("Host prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));
		itemsWidget.addHeaderRowNumber();
		itemsWidget.addItem(get_header_host_table(idBean, executor, "hosts", Nest.value(discoveryRule,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));

		// create form
		CForm itemForm = new CForm();
		itemForm.setName("hosts");
		itemForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());

		// create table
		CTableInfo hostTable = new CTableInfo(_("No host prototypes found."));

		Curl csortLink = new Curl();
		csortLink.setArgument("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		String sortLink = csortLink.getUrl();

		hostTable.setHeader(array(
			new CCheckBox("all_hosts", false, "checkAll(\""+itemForm.getName()+"\", \"all_hosts\", \"group_hostid\");"),
			make_sorting_header(_("Name"),"name", sortLink),
			_("Templates"),
			make_sorting_header(_("Status"),"status", sortLink)
		));

		for(Map hostPrototype : (CArray<Map>)Nest.value(data,"hostPrototypes").asCArray()) {
			// name
			CArray name = array();
			if (!empty(Nest.value(hostPrototype,"templateid").$())) {
				Map sourceTemplate = Nest.value(hostPrototype,"sourceTemplate").asCArray();
				name.add(new CLink(Nest.value(sourceTemplate,"name").$(), "?parent_discoveryid="+Nest.value(hostPrototype,"sourceDiscoveryRuleId").$(), "unknown"));
				name.add(NAME_DELIMITER);
			}
			name.add(new CLink(Nest.value(hostPrototype,"name").$(), "?form=update&parent_discoveryid="+discoveryRule.get("itemid")+"&hostid="+Nest.value(hostPrototype,"hostid").$()));

			// template list
			Object hostTemplates = null;
			if (empty(Nest.value(hostPrototype,"templates").$())) {
				hostTemplates  = "-";
			} else {
				hostTemplates = array();
				order_result(Nest.value(hostPrototype,"templates").asCArray(), "name");

				for(Map template : (CArray<Map>)Nest.value(hostPrototype,"templates").asCArray()) {

					CArray caption = array();
					caption.add(new CLink(Nest.value(template,"name").$(), "templates.action?form=update&templateid="+Nest.value(template,"templateid").$(), "unknown"));

					CArray<Map> linkedTemplates = Nest.value(data,"linkedTemplates",template.get("templateid"),"parentTemplates").asCArray();
					if (!empty(linkedTemplates)) {
						order_result(linkedTemplates, "name");

						caption.add(" (");
						for(Map tpl : linkedTemplates) {
							caption.add(new CLink(Nest.value(tpl,"name").$(),"templates.action?form=update&templateid="+Nest.value(tpl,"templateid").$(), "unknown"));
							caption.add(", ");
						}
						array_pop(caption);

						caption.add(")");
					}

					((CArray)hostTemplates).add(caption);
					((CArray)hostTemplates).add(", ");
				}

				if (!empty(hostTemplates)) {
					array_pop(((CArray)hostTemplates));
				}
			}

			// status
			CLink status = new CLink(item_status2str(Nest.value(hostPrototype,"status").asInteger()),
				"?group_hostid="+hostPrototype.get("hostid")+"&parent_discoveryid="+discoveryRule.get("itemid")+
				"&go="+(Nest.value(hostPrototype,"status").asBoolean() ? "activate" : "disable"), itemIndicatorStyle(Nest.value(hostPrototype,"status").asInteger())
			);

			hostTable.addRow(array(
				new CCheckBox("group_hostid["+hostPrototype.get("hostid")+"]", false, null, Nest.value(hostPrototype,"hostid").asInteger()),
				name,
				new CCol(hostTemplates, "wraptext"),
				status
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected host prototypes?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected host prototypes?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected host prototypes?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"group_hostid\";");
		rda_add_post_js("chkbxRange.prefix = \""+discoveryRule.get("itemid")+"\";");
		rda_add_post_js("cookie.prefix = \""+discoveryRule.get("itemid")+"\";");

		// append table to form
		itemForm.addItem(array(Nest.value(data,"paging").$(), hostTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		itemsWidget.addItem(itemForm);
		return itemsWidget;
	}

}
