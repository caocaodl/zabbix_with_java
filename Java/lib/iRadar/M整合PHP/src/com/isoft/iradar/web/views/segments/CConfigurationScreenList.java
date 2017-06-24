package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationScreenList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();

		// create new screen button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create screen")));
		if (!empty(Nest.value(data,"templateid").$())) {
			createForm.addVar("templateid", Nest.value(data,"templateid").$());
			screenWidget.addItem(get_header_host_table(idBean, executor, "screens", Nest.value(data,"templateid").asLong(true)));
		} else {
			createForm.addItem(new CButton("form", _("Import"), "redirect(\"conf.import.action?rules_preset=screen\")"));
		}
		screenWidget.addPageHeader(_("CONFIGURATION OF SCREENS"), createForm);

		// header
		screenWidget.addHeader(_("Screens"));
		screenWidget.addHeaderRowNumber();

		// create form
		CForm screenForm = new CForm();
		screenForm.setName("screenForm");

		screenForm.addVar("templateid", Nest.value(data,"templateid").$());

		// create table
		CTableInfo screenTable = new CTableInfo(_("No screens found."));
		screenTable.setHeader(array(
			new CCheckBox("all_screens", false, "checkAll(\""+screenForm.getName()+"\", \"all_screens\", \"screens\");"),
			make_sorting_header(_("Name"), "name"),
			_("Dimension (cols x rows)"),
			_("Screen")
		));

		for(Map screen : (CArray<Map>)Nest.value(data,"screens").asCArray()) {
			screenTable.addRow(array(
				new CCheckBox("screens["+Nest.value(screen,"screenid").asString()+"]", false, null, Nest.value(screen,"screenid").asString()),
				new CLink(Nest.value(screen,"name").$(), "screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+url_param(idBean, "templateid")),
				Nest.value(screen,"hsize").$()+" x "+Nest.value(screen,"vsize").$(),
				new CLink(_("Edit"), "?form=update&screenid="+Nest.value(screen,"screenid").asString()+url_param(idBean, "templateid"))
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		if (empty(Nest.value(data,"templateid").$())) {
			goComboBox.addItem("export", _("Export selected"));
		}
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected screens?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"screens\";");

		// append table to form
		screenForm.addItem(array(Nest.value(data,"paging").$(), screenTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		screenWidget.addItem(screenForm);
		return screenWidget;
	}

}
