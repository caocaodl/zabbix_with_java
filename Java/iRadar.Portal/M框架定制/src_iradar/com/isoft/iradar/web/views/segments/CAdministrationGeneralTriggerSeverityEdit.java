package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CColor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralTriggerSeverityEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.triggerSeverity.js");

		CFormList severityTab = new CFormList("scriptsTab");

		CDiv headerDiv = new CDiv(_("Custom severity"), "inlineblock");
		headerDiv.addStyle("width: 16.3em; margin-left: 3px; zoom:1; *display: inline;");
		severityTab.addRow(_("level"), array(headerDiv, _("Colour")));

//		CTextBox severityNameTB0 = new CTextBox("severity_name_0", _(Nest.value(data,"config","severity_name_0").asString()));
//		severityNameTB0.addStyle("width: 15em;");
//		severityNameTB0.setAttribute("maxlength", 32);
//		CColor severityColorTB0 = new CColor("severity_color_0", Nest.value(data,"config","severity_color_0").asString());
//		//severityTab.addRow(_("Not classified"), array(severityNameTB0, SPACE, severityColorTB0));
//		severityTab.addRow("0（低）", array(severityNameTB0, SPACE, severityColorTB0));

		CTextBox severityNameTB1 = new CTextBox("severity_name_1", _(Nest.value(data,"config","severity_name_1").asString()));
		severityNameTB1.addStyle("width: 15em;");
		severityNameTB1.setAttribute("maxlength", 32);
		CColor severityColorTB1 = new CColor("severity_color_1", Nest.value(data,"config","severity_color_1").asString());
		//severityTab.addRow(_("Information"), array(severityNameTB1, SPACE, severityColorTB1));
		severityTab.addRow("1（低）", array(severityNameTB1, SPACE, severityColorTB1));

		CTextBox severityNameTB2 = new CTextBox("severity_name_2", _(Nest.value(data,"config","severity_name_2").asString()));
		severityNameTB2.addStyle("width: 15em;");
		severityNameTB2.setAttribute("maxlength", 32);
		CColor severityColorTB2 = new CColor("severity_color_2", Nest.value(data,"config","severity_color_2").asString());
		//severityTab.addRow(_("Warning"), array(severityNameTB2, SPACE, severityColorTB2));
		severityTab.addRow(2, array(severityNameTB2, SPACE, severityColorTB2));

		CTextBox severityNameTB3 = new CTextBox("severity_name_3", _(Nest.value(data,"config","severity_name_3").asString()));
		severityNameTB3.addStyle("width: 15em;");
		severityNameTB3.setAttribute("maxlength", 32);
		CColor severityColorTB3 = new CColor("severity_color_3", Nest.value(data,"config","severity_color_3").asString());
		//severityTab.addRow(_("Average"), array(severityNameTB3, SPACE, severityColorTB3));
		severityTab.addRow(3, array(severityNameTB3, SPACE, severityColorTB3));

//		CTextBox severityNameTB4 = new CTextBox("severity_name_4", _(Nest.value(data,"config","severity_name_4").asString()));
//		severityNameTB4.addStyle("width: 15em;");
//		severityNameTB4.setAttribute("maxlength", 32);
//		CColor severityColorTB4 = new CColor("severity_color_4", Nest.value(data,"config","severity_color_4").asString());
//		//severityTab.addRow(_("High"), array(severityNameTB4, SPACE, severityColorTB4));
//		severityTab.addRow(4, array(severityNameTB4, SPACE, severityColorTB4));

		CTextBox severityNameTB5 = new CTextBox("severity_name_5", _(Nest.value(data,"config","severity_name_5").asString()));
		severityNameTB5.addStyle("width: 15em;");
		severityNameTB5.setAttribute("maxlength", 32);
		CColor severityColorTB5 = new CColor("severity_color_5", Nest.value(data,"config","severity_color_5").asString());
		//severityTab.addRow(_("Disaster"), array(severityNameTB5, SPACE, severityColorTB5));
		severityTab.addRow("4（高）", array(severityNameTB5, SPACE, severityColorTB5));

//		severityTab.addRow(SPACE);
//		severityTab.addInfo(_("Custom severity names affect all locales and require manual translation!"));

		CTabView severityView = new CTabView();
		severityView.addTab("severities", _("Trigger severities"), severityTab);

		CForm severityForm = new CForm();
		severityForm.setName("triggerSeverity");
		severityForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		severityForm.addItem(severityView);
		severityForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButton("resetDefaults", _("Reset defaults"))));

		return severityForm;
	}

}
