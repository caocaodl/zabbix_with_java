package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralWorkingtimeEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CFormList workingTimeTab = new CFormList("scriptsTab");
		CTextBox wtTextBox = new CTextBox("work_period", Nest.value(data,"config","work_period").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		wtTextBox.attr("autofocus", "autofocus");
		workingTimeTab.addRow(_("Working time"), wtTextBox);

		CTabView workingTimeView = new CTabView();
		workingTimeView.addTab("workingTime", _("Working time"), workingTimeTab);

		CForm workingTimeForm = new CForm();
		workingTimeForm.setName("workingTimeForm");

		workingTimeForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		workingTimeForm.addItem(workingTimeView);
		workingTimeForm.addItem(makeFormFooter(new CSubmit("save", _("Save"))));

		return workingTimeForm;
	}

}
