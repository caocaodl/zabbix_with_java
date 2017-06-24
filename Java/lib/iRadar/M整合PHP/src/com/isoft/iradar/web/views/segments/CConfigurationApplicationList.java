package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CArrayHelper;
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

public class CConfigurationApplicationList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget applicationWidget = new CWidget();

		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

		// append host summary to widget header
		if (empty(Nest.value(data,"hostid").$())) {
			CSubmit createButton = new CSubmit("form", _("Create application (select host first)"));
			createButton.setEnabled(false);
			createForm.addItem(createButton);
		} else {
			createForm.addItem(new CSubmit("form", _("Create application")));
			applicationWidget.addItem(get_header_host_table(idBean, executor, "applications", Nest.value(data,"hostid").asLong(true)));
		}

		applicationWidget.addPageHeader(_("CONFIGURATION OF APPLICATIONS"), createForm);

		// create widget header
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));

		applicationWidget.addHeader(_("Applications"), filterForm);
		applicationWidget.addHeaderRowNumber();

		// create form
		CForm applicationForm = new CForm();
		applicationForm.setName("applicationForm");
		applicationForm.addVar("groupid", Nest.value(data,"groupid").$());
		applicationForm.addVar("hostid", Nest.value(data,"hostid").$());

		// create table
		CTableInfo applicationTable = new CTableInfo(_("No applications found."));
		applicationTable.setHeader(array(
			new CCheckBox("all_applications", false, "checkAll(\""+applicationForm.getName()+"\", \"all_applications\", \"applications\");"),
			(Nest.value(data,"hostid").asInteger() > 0) ? null : _("Host"),
			make_sorting_header(_("Application"), "name"),
			_("Show")
		));

		for(Map application : (CArray<Map>)Nest.value(data,"applications").asCArray()) {
			// inherited app, display the template list
			Object name = null;
			if (!empty(Nest.value(application,"templateids").$()) && !empty(Nest.value(application,"sourceTemplates").$())) {
				name = array();
				CArrayHelper.sort(Nest.value(application,"sourceTemplates").asCArray(), array("name"));

				for(Map template : (CArray<Map>)Nest.value(application,"sourceTemplates").asCArray()) {
					((CArray)name).add(new CLink(Nest.value(template,"name").$(), "applications.action?hostid="+Nest.value(template,"hostid").$(), "unknown"));
					((CArray)name).add(", ");
				}
				array_pop(((CArray)name));
				((CArray)name).add(NAME_DELIMITER);
				((CArray)name).add(Nest.value(application,"name").$());
			} else {
				name = new CLink(
					Nest.value(application,"name").$(),
					"applications.action?"+
						"form=update"+
						"&applicationid="+application.get("applicationid")+
						"&hostid="+application.get("hostid")+
						"&groupid="+data.get("groupid")
				);
			}

			applicationTable.addRow(array(
				new CCheckBox("applications["+application.get("applicationid")+"]", false, null, Nest.value(application,"applicationid").asInteger()),
				(Nest.value(data,"hostid").asInteger() > 0) ? null : Nest.value(application,"host").$(),
				name,
				array(
					new CLink(
						_("Items"),
						"items.action?"+
							"hostid="+application.get("hostid")+
							"&filter_set=1"+
							"&filter_application="+urlencode(Nest.value(application,"name").asString())
					),
					SPACE+"("+count(Nest.value(application,"items").asCArray())+")"
				)
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected applications?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected applications?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected applications?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");

		rda_add_post_js("chkbxRange.pageGoName = \"applications\";");
		rda_add_post_js("chkbxRange.prefix = \""+data.get("hostid")+"\";");
		rda_add_post_js("cookie.prefix = \""+data.get("hostid")+"\";");

		// append table to form
		applicationForm.addItem(array(Nest.value(data,"paging").$(), applicationTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		applicationWidget.addItem(applicationForm);

		return applicationWidget;
	}

}
