package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
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

public class CConfigurationSysmapList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget sysmapWidget = new CWidget();

		// create header buttons
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create map")));
		createForm.addItem(new CButton("form", _("Import"), "redirect(\"conf.import.action?rules_preset=map\")"));

		sysmapWidget.addPageHeader(_("CONFIGURATION OF NETWORK MAPS"), createForm);

		// create form
		CForm sysmapForm = new CForm();
		sysmapForm.setName("frm_maps");

		sysmapWidget.addHeader(_("Maps"));
		sysmapWidget.addHeaderRowNumber();

		// create table
		CTableInfo sysmapTable = new CTableInfo(_("No maps found."));
		sysmapTable.setHeader(array(
			new CCheckBox("all_maps", false, "checkAll(\""+sysmapForm.getName()+"\", \"all_maps\", \"maps\");"),
			make_sorting_header(_("Name"), "name"),
			make_sorting_header(_("Width"), "width"),
			make_sorting_header(_("Height"), "height"),
			_("Edit")
		));

		for(Map map : (CArray<Map>)Nest.value(data,"maps").asCArray()) {
			sysmapTable.addRow(array(
				new CCheckBox("maps["+Nest.value(map,"sysmapid").$()+"]", false, null, Nest.value(map,"sysmapid").asInteger()),
				new CLink(Nest.value(map,"name").$(), "sysmap.action?sysmapid="+Nest.value(map,"sysmapid").$()),
				Nest.value(map,"width").$(),
				Nest.value(map,"height").$(),
				new CLink(_("Edit"), "sysmaps.action?form=update&sysmapid="+Nest.value(map,"sysmapid").$()+"#form")
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		goComboBox.addItem("export", _("Export selected"));
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected maps?"));
		goComboBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"maps\";");

		// append table to form
		sysmapForm.addItem(array(Nest.value(data,"paging").$(), sysmapTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		sysmapWidget.addItem(sysmapForm);

		return sysmapWidget;
	}

}
