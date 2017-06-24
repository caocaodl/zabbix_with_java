package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CConfigurationScreenEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();
		screenWidget.addPageHeader(_("CONFIGURATION OF SCREENS"));
		if (!empty(Nest.value(data,"templateid").$())) {
			screenWidget.addItem(get_header_host_table(idBean, executor, "screens", Nest.value(data,"templateid").asLong(true)));
		}

		// create form
		CForm screenForm = new CForm();
		screenForm.setName("screenForm");
		screenForm.addVar("form", Nest.value(data,"form").$());
		if (!empty(Nest.value(data,"screenid").$())) {
			screenForm.addVar("screenid", Nest.value(data,"screenid").$());
		}
		screenForm.addVar("templateid", Nest.value(data,"templateid").$());

		// create screen form list
		CFormList screenFormList = new CFormList("screenFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		screenFormList.addRow(_("Name"), nameTextBox);
		screenFormList.addRow(_("Columns"), new CNumericBox("hsize", Nest.value(data,"hsize").asString(), 3));
		screenFormList.addRow(_("Rows"), new CNumericBox("vsize", Nest.value(data,"vsize").asString(), 3));

		// append tabs to form
		CTabView screenTab = new CTabView();
		screenTab.addTab("screenTab", _("Screen"), screenFormList);
		screenForm.addItem(screenTab);

		// append buttons to form
		screenForm.addItem(makeFormFooter(
			new CSubmit("save", _("Save")),
			array(
				!empty(Nest.value(data,"screenid").$()) ? new CSubmit("clone", _("Clone")) : null,
				!empty(Nest.value(data,"screenid").$()) ? new CButtonDelete(_("Delete screen?"), url_param(idBean, "form")+url_param(idBean, "screenid")+url_param(idBean, "templateid")) : null,
				new CButtonCancel(url_param(idBean, "templateid"))
			)
		));

		screenWidget.addItem(screenForm);
		return screenWidget;
	}

}
