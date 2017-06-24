package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.GraphsUtil.get_realhosts_by_graphid;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationGraphList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget graphWidget = new CWidget();

		// create new graph button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			createForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
			createForm.addItem(new CSubmit("form", _("Create graph prototype")));

			graphWidget.addPageHeader(_("CONFIGURATION OF GRAPH PROTOTYPES"), createForm);
			graphWidget.addHeader(array(_("Graph prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));

			if (!empty(Nest.value(data,"hostid").$())) {
				graphWidget.addItem(get_header_host_table(idBean, executor, "graphs", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));
			}
		} else {
			createForm.addVar("hostid", Nest.value(data,"hostid").$());

			if (!empty(Nest.value(data,"hostid").$())) {
				createForm.addItem(new CSubmit("form", _("Create graph")));
			} else {
				CSubmit createGraphButton = new CSubmit("form", _("Create graph (select host first)"));
				createGraphButton.setEnabled(false);
				createForm.addItem(createGraphButton);
			}

			graphWidget.addPageHeader(_("CONFIGURATION OF GRAPHS"), createForm);

			CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
			CForm filterForm = new CForm("get");
			filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
			filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));

			graphWidget.addHeader(_("Graphs"), filterForm);

			if (!empty(Nest.value(data,"hostid").$())) {
				graphWidget.addItem(get_header_host_table(idBean, executor, "graphs", Nest.value(data,"hostid").asLong(true)));
			}
		}
		graphWidget.addHeaderRowNumber();

		// create form
		CForm graphForm = new CForm();
		graphForm.setName("graphForm");
		graphForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			graphForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}

		// create table
		CTableInfo graphTable = new CTableInfo(!empty(Nest.value(data,"parent_discoveryid").$()) ? _("No graph prototypes found.") : _("No graphs found."));
		graphTable.setHeader(array(
			new CCheckBox("all_graphs", false, "checkAll(\""+graphForm.getName()+"\", \"all_graphs\", \"group_graphid\");"),
			!empty(Nest.value(data,"hostid").$()) ? null : _("Hosts"),
			make_sorting_header(_("Name"), "name"),
			_("Width"),
			_("Height"),
			make_sorting_header(_("Graph type"), "graphtype")
		));

		for(Map graph : (CArray<Map>)Nest.value(data,"graphs").asCArray()) {
			Object graphid = Nest.value(graph,"graphid").$();

			String shostList = null;
			if (empty(Nest.value(data,"hostid").$())) {
				CArray<String> hostList = array();
				for(Map host : (CArray<Map>)Nest.value(graph,"hosts").asCArray()) {
					Nest.value(hostList,host.get("name")).$(Nest.value(host,"name").$());
				}

				for(Map template : (CArray<Map>)Nest.value(graph,"templates").asCArray()) {
					Nest.value(hostList,template.get("name")).$(Nest.value(template,"name").$());
				}
				shostList = implode(", ", hostList);
			}

			boolean isCheckboxEnabled = true;
			CArray name = array();
			if (!empty(Nest.value(graph,"templateid").$())) {
				CArray<Map> realDbHosts = get_realhosts_by_graphid(idBean, executor, Nest.value(graph,"templateid").asLong());
				Map realHosts = DBfetch(realDbHosts);
				name.add(new CLink(Nest.value(realHosts,"name").$(), "graphs.action?hostid="+Nest.value(realHosts,"hostid").$(), "unknown"));
				name.add(NAME_DELIMITER);
				name.add(new CLink(
					Nest.value(graph,"name").$(),
					"graphs.action?"+
						"form=update"+
						"&graphid="+graphid+url_param(idBean, "parent_discoveryid")+
						"&hostid="+Nest.value(data,"hostid").asString()
				));

				if (!empty(Nest.value(graph,"discoveryRule").$())) {
					isCheckboxEnabled = false;
				}
			} else if (!empty(Nest.value(graph,"discoveryRule").$()) && empty(Nest.value(data,"parent_discoveryid").$())) {
				name.add(new CLink(
					Nest.value(graph,"discoveryRule","name").$(),
					"host_discovery.action?form=update&itemid="+Nest.value(graph,"discoveryRule","itemid").$(),
					"parent-discovery"
				));
				name.add(NAME_DELIMITER);
				name.add(new CSpan(Nest.value(graph,"name").$()));

				isCheckboxEnabled = false;
			} else {
				name.add(new CLink(
					Nest.value(graph,"name").$(),
					"graphs.action?"+
						"form=update"+
						"&graphid="+graphid+url_param(idBean, "parent_discoveryid")+
						"&hostid="+Nest.value(data,"hostid").asString()
				));
			}

			CCheckBox checkBox = new CCheckBox("group_graphid["+graphid+"]", false, null, Nest.as(graphid).asInteger());
			checkBox.setEnabled(isCheckboxEnabled);

			graphTable.addRow(array(
				checkBox,
				shostList,
				name,
				Nest.value(graph,"width").$(),
				Nest.value(graph,"height").$(),
				Nest.value(graph,"graphtype").$()
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			goComboBox.addItem("copy_to", _("Copy selected to ..."));
		}

		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete selected graph prototypes?") : _("Delete selected graphs?")
		);
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.attr("id", "goButton");

		rda_add_post_js("chkbxRange.pageGoName = \"group_graphid\";");
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"parent_discoveryid").asString()+"\";");
			rda_add_post_js("cookie.prefix = \""+Nest.value(data,"parent_discoveryid").asString()+"\";");
		} else {
			rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"hostid").asString()+"\";");
			rda_add_post_js("cookie.prefix = \""+Nest.value(data,"hostid").asString()+"\";");
		}

		// append table to form
		graphForm.addItem(array(Nest.value(data,"paging").$(), graphTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		graphWidget.addItem(graphForm);

		return graphWidget;
	}

}
