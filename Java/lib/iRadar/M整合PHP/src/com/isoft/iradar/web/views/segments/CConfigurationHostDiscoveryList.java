package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.ItemsUtil.get_realhost_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicator;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostDiscoveryList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget discoveryWidget = new CWidget();

		// create new discovery rule button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());
		createForm.addItem(new CSubmit("form", _("Create discovery rule")));
		discoveryWidget.addPageHeader(_("CONFIGURATION OF DISCOVERY RULES"), createForm);

		// header
		discoveryWidget.addHeader(_("Discovery rules"));
		discoveryWidget.addHeaderRowNumber();
		discoveryWidget.addItem(get_header_host_table(idBean, executor, "discoveries", Nest.value(data,"hostid").asLong(true)));

		// create form
		CForm discoveryForm = new CForm();
		discoveryForm.setName("discovery");
		discoveryForm.addVar("hostid", Nest.value(data,"hostid").$());

		// create table
		CTableInfo discoveryTable = new CTableInfo(_("No discovery rules found."));

		Curl csortLink = new Curl();
		csortLink.setArgument("hostid", Nest.value(data,"hostid").$());
		String sortLink = csortLink.getUrl();

		discoveryTable.setHeader(array(
			new CCheckBox("all_items", false, "checkAll(\""+discoveryForm.getName()+"\", \"all_items\", \"g_hostdruleid\");"),
			make_sorting_header(_("Name"), "name", sortLink),
			_("Items"),
			_("Triggers"),
			_("Graphs"),
			(Nest.value(data,"host","flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) ? _("Hosts") : null,
			make_sorting_header(_("Key"), "key_", sortLink),
			make_sorting_header(_("Interval"), "delay", sortLink),
			make_sorting_header(_("Type"), "type", sortLink),
			make_sorting_header(_("Status"), "status", sortLink),
			Nest.value(data,"showErrorColumn").asBoolean() ? _("Error") : null
		));

		for(Map discovery : (CArray<Map>)Nest.value(data,"discoveries").asCArray()) {
			CArray description = array();

			if (!empty(Nest.value(discovery,"templateid").$())) {
				Map template_host = get_realhost_by_itemid(idBean, executor, Nest.value(discovery,"templateid").asString());
				description.add(new CLink(Nest.value(template_host,"name").$(), "?hostid="+Nest.value(template_host,"hostid").$(), "unknown"));
				description.add(NAME_DELIMITER);
			}

			description.add(new CLink(Nest.value(discovery,"name_expanded").$(), "?form=update&itemid="+Nest.value(discovery,"itemid").$()));

			CLink status = new CLink(
				itemIndicator(Nest.value(discovery,"status").asInteger(), Nest.value(discovery,"state").asInteger()),
				"?hostid="+Nest.value(_REQUEST,"hostid").asString()+"&g_hostdruleid="+discovery.get("itemid")+"&go="+(Nest.value(discovery,"status").asBoolean() ? "activate" : "disable"),
				itemIndicatorStyle(Nest.value(discovery,"status").asInteger(), Nest.value(discovery,"state").asInteger())
			);

			Object error = null;
			if (Nest.value(data,"showErrorColumn").asBoolean()) {
				error = "";
				if (Nest.value(discovery,"status").asInteger() == ITEM_STATUS_ACTIVE) {
					if (rda_empty(Nest.value(discovery,"error").$())) {
						error = new CDiv(SPACE, "status_icon iconok");
					} else {
						error = new CDiv(SPACE, "status_icon iconerror");
						((CDiv)error).setHint(Nest.value(discovery,"error").$(), "", "on");
					}
				}
			}

			// host prototype link
			CArray hostPrototypeLink = null;
			if (Nest.value(data,"host","flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
				hostPrototypeLink = array(
					new CLink(_("Host prototypes"), "host_prototypes.action?parent_discoveryid="+Nest.value(discovery,"itemid").$()),
					" ("+discovery.get("hostPrototypes")+")"
				);
			}

			discoveryTable.addRow(array(
				new CCheckBox("g_hostdruleid["+discovery.get("itemid")+"]", false, null, Nest.value(discovery,"itemid").asString()),
				description,
				array(
					new CLink(
						_("Item prototypes"),
						"disc_prototypes.action?hostid="+get_request("hostid")+"&parent_discoveryid="+discovery.get("itemid")
					),
					" ("+discovery.get("items")+")"
				),
				array(
					new CLink(
						_("Trigger prototypes"),
						"trigger_prototypes.action?hostid="+get_request("hostid")+"&parent_discoveryid="+discovery.get("itemid")
					),
					" ("+discovery.get("triggers")+")"
				),
				array(
					new CLink(
						_("Graph prototypes"),
						"graphs.action?hostid="+get_request("hostid")+"&parent_discoveryid="+discovery.get("itemid")
					),
					" ("+discovery.get("graphs")+")"
				),
				hostPrototypeLink,
				Nest.value(discovery,"key_").$(),
				Nest.value(discovery,"delay").$(),
				item_type2str(Nest.value(discovery,"type").asInteger()),
				status,
				Nest.value(data,"showErrorColumn").asBoolean() ? error : null
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected discovery rules?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected discovery rules?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected discovery rules?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");

		rda_add_post_js("chkbxRange.pageGoName = \"g_hostdruleid\";");
		rda_add_post_js("chkbxRange.prefix = \""+data.get("hostid")+"\";");
		rda_add_post_js("cookie.prefix = \""+data.get("hostid")+"\";");

		// append table to form
		discoveryForm.addItem(array(Nest.value(data,"paging").$(), discoveryTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		discoveryWidget.addItem(discoveryForm);

		return discoveryWidget;
	}

}
