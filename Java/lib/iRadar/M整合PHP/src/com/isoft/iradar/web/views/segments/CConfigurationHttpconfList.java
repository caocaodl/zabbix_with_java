package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.HttpTestUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHttpconfList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget httpWidget = new CWidget();

		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

		if (empty(Nest.value(data,"hostid").$())) {
			CSubmit createButton = new CSubmit("form", _("Create scenario (select host first)"));
			createButton.setEnabled(false);
			createForm.addItem(createButton);
		} else {
			createForm.addItem(new CSubmit("form", _("Create scenario")));
			httpWidget.addItem(get_header_host_table(idBean, executor, "web", Nest.value(data,"hostid").asLong(true)));
		}

		httpWidget.addPageHeader(_("CONFIGURATION OF WEB MONITORING"), createForm);

		// header
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));

		httpWidget.addHeader(_("Scenarios"), filterForm);
		httpWidget.addHeaderRowNumber(array(
			"[ ",
			new CLink(Nest.value(data,"showDisabled").asBoolean() ? _("Hide disabled scenarios") : _("Show disabled scenarios"),
			"?showdisabled="+(Nest.value(data,"showDisabled").asBoolean() ? 0 : 1), null), " ]"
		));

		// create form
		CForm httpForm = new CForm();
		httpForm.setName("scenarios");
		httpForm.addVar("hostid", Nest.value(data,"hostid").$());

		CTableInfo httpTable = new CTableInfo(_("No web scenarios found."));
		httpTable.setHeader(array(
			new CCheckBox("all_httptests", false, "checkAll(\""+httpForm.getName()+"\", \"all_httptests\", \"group_httptestid\");"),
			(Nest.value(data,"hostid").asLong() == 0) ? make_sorting_header(_("Host"), "hostname") : null,
			make_sorting_header(_("Name"), "name"),
			_("Number of steps"),
			_("Update interval"),
			make_sorting_header(_("Status"), "status"))
		);

		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"httpTests").asCArray()).entrySet()) {
		    Object httpTestId = e.getKey();
		    Map httpTest = e.getValue();
		    CArray name = array();
			if (isset(Nest.value(data,"parentTemplates",httpTestId).$())) {
				Map template = Nest.value(data,"parentTemplates",httpTestId).asCArray();
				name.add(new CLink(Nest.value(template,"name").$(), "?groupid=0&hostid="+Nest.value(template,"id").$(), "unknown"));
				name.add(NAME_DELIMITER);
			}
			name.add(new CLink(Nest.value(httpTest,"name").$(), "?form=update&httptestid="+httpTest.get("httptestid")+"&hostid="+Nest.value(httpTest,"hostid").$()));

			httpTable.addRow(array(
				new CCheckBox("group_httptestid["+Nest.value(httpTest,"httptestid").$()+"]", false, null, Nest.value(httpTest,"httptestid").asInteger()),
				(Nest.value(data,"hostid").asInteger() > 0) ? null : Nest.value(httpTest,"hostname").$(),
				name,
				Nest.value(httpTest,"stepscnt").$(),
				Nest.value(httpTest,"delay").$(),
				new CLink(
					HttpTestUtil.httptest_status2str(Nest.value(httpTest,"status").asInteger()),
					"?group_httptestid[]="+httpTest.get("httptestid")+
						"&hostid="+httpTest.get("hostid")+
						"&go="+(Nest.value(httpTest,"status").asBoolean() ? "activate" : "disable"),
						HttpTestUtil.httptest_status2style(Nest.value(httpTest,"status").asInteger())
				)
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected WEB scenarios?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm",_("Disable selected WEB scenarios?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("clean_history", _("Clear history for selected"));
		goOption.setAttribute("confirm", _("Delete history of selected WEB scenarios?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected WEB scenarios?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"group_httptestid\";");
		rda_add_post_js("chkbxRange.prefix = \""+data.get("hostid")+"\";");
		rda_add_post_js("cookie.prefix = \""+data.get("hostid")+"\";");

		// append table to form
		httpForm.addItem(array(Nest.value(data,"paging").$(), httpTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		httpWidget.addItem(httpForm);

		return httpWidget;
	}

}
