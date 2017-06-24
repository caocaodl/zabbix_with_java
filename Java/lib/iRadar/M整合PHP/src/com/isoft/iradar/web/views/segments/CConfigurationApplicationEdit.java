package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CConfigurationApplicationEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget applicationWidget = new CWidget();
		applicationWidget.addPageHeader(_("CONFIGURATION OF APPLICATIONS"));

		// append host summary to widget header
		applicationWidget.addItem(get_header_host_table(idBean, executor, "applications", Nest.value(data,"hostid").asLong(true)));

		// create form
		CForm applicationForm = new CForm();
		applicationForm.setName("applicationForm");
		applicationForm.addVar("form", Nest.value(data,"form").$());
		applicationForm.addVar("groupid", Nest.value(data,"groupid").$());
		applicationForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (!empty(Nest.value(data,"applicationid").$())) {
			applicationForm.addVar("applicationid", Nest.value(data,"applicationid").$());
		}

		// create form list
		CFormList applicationFormList = new CFormList("applicationFormList");
		CTextBox nameTextBox = new CTextBox("appname", Nest.value(data,"appname").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		applicationFormList.addRow(_("Name"), nameTextBox);

		// append tabs to form
		CTabView applicationTab = new CTabView();
		applicationTab.addTab("applicationTab", _("Application"), applicationFormList);
		applicationForm.addItem(applicationTab);

		// append buttons to form
		if (!empty(Nest.value(data,"applicationid").$())) {
			applicationForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					new CButtonDelete(_("Delete application?"), url_params(idBean, array("config", "hostid", "groupid", "form", "applicationid"))),
					new CButtonCancel(url_param(idBean, "config")+url_param(idBean, "hostid")+url_param(idBean, "groupid"))
				)
			));
		} else {
			applicationForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel(url_param(idBean, "config")+url_param(idBean, "hostid")+url_param(idBean, "groupid"))
			));
		}

		// append form to widget
		applicationWidget.addItem(applicationForm);

		return applicationWidget;
	}

}
