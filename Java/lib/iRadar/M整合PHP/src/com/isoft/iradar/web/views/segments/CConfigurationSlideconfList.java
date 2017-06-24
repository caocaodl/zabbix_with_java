package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
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
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationSlideconfList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget slideWidget = new CWidget();

		// create new hostgroup button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create slide show")));
		slideWidget.addPageHeader(_("CONFIGURATION OF SLIDE SHOWS"), createForm);
		slideWidget.addHeader(_("Slide shows"));
		slideWidget.addHeaderRowNumber();

		// create form
		CForm slideForm = new CForm();
		slideForm.setName("slideForm");

		// create table
		CTableInfo slidesTable = new CTableInfo(_("No slide shows found."));
		slidesTable.setHeader(array(
			new CCheckBox("all_shows", false, "checkAll(\""+slideForm.getName()+"\", \"all_shows\", \"shows\");"),
			make_sorting_header(_("Name"), "name"),
			make_sorting_header(_("Delay"), "delay"),
			make_sorting_header(_("Count of slides"), "cnt")
		));

		for(Map slide :(CArray<Map>)Nest.value(data,"slides").asCArray()) {
			slidesTable.addRow(array(
				new CCheckBox("shows["+Nest.value(slide,"slideshowid").$()+"]", false, null, Nest.value(slide,"slideshowid").asInteger()),
				new CLink(Nest.value(slide,"name").$(), "?form=update&slideshowid="+Nest.value(slide,"slideshowid").$(), "action"),
				Nest.value(slide,"delay").$(),
				Nest.value(slide,"cnt").$()
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected slide shows?"));
		goComboBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"shows\";");

		// append table to form
		slideForm.addItem(array(Nest.value(data,"paging").$(), slidesTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		slideWidget.addItem(slideForm);

		return slideWidget;
	}

}
