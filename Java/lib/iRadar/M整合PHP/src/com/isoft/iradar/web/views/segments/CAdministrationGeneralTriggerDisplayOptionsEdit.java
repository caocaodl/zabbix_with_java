package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CColor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralTriggerDisplayOptionsEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.triggerDisplayOptions.js");

		CFormList triggerDOFormList = new CFormList("scriptsTab");

		CDiv headerDiv = new CDiv(_("Colour"), "inlineblock trigger_displaying_form_col");
		headerDiv.addStyle("margin-left: 2px;");
		triggerDOFormList.addRow(SPACE, array(headerDiv, _("Blinking")));

		// Unacknowledged problem events
		triggerDOFormList.addRow(
			_("Unacknowledged PROBLEM events"),
			array(
				new CDiv(
					new CColor("problem_unack_color", Nest.value(data,"problem_unack_color").asString()),
					"inlineblock trigger_displaying_form_col"
				),
				new CCheckBox(
					"problem_unack_style",
					Nest.value(data,"problem_unack_style").asInteger() == 1,
					null,
					1
				)
			)
		);

		// Acknowledged problem events
		triggerDOFormList.addRow(
			_("Acknowledged PROBLEM events"),
			array(
				new CDiv(
					new CColor("problem_ack_color", Nest.value(data,"problem_ack_color").asString()),
					"inlineblock trigger_displaying_form_col"
				),
				new CCheckBox(
					"problem_ack_style",
					Nest.value(data,"problem_ack_style").asInteger() == 1,
					null,
					1
				)
			)
		);

		// Unacknowledged recovery events
		triggerDOFormList.addRow(
			_("Unacknowledged OK events"),
			array(
				new CDiv(
					new CColor("ok_unack_color", Nest.value(data,"ok_unack_color").asString()),
					"inlineblock trigger_displaying_form_col"
				),
				new CCheckBox(
					"ok_unack_style",
					Nest.value(data,"ok_unack_style").asInteger() == 1,
					null,
					1
				)
			)
		);

		// Acknowledged recovery events
		triggerDOFormList.addRow(
			_("Acknowledged OK events"),
			array(
				new CDiv(
					new CColor("ok_ack_color", Nest.value(data,"ok_ack_color").asString()),
					"inlineblock trigger_displaying_form_col"
				),
				new CCheckBox(
					"ok_ack_style",
					Nest.value(data,"ok_ack_style").asInteger() == 1,
					null,
					1
				)
			)
		);

		// some air between the sections
		triggerDOFormList.addRow(BR());

		// Display OK triggers
		CTextBox okPeriodTextBox = new CTextBox("ok_period", Nest.value(data,"ok_period").asString());
		okPeriodTextBox.addStyle("width: 4em;");
		okPeriodTextBox.setAttribute("maxlength", "6");
		triggerDOFormList.addRow(_("Display OK triggers for"), array(okPeriodTextBox, SPACE, _("seconds")));

		// Triggers blink on status change
		okPeriodTextBox = new CTextBox("blink_period", Nest.value(data,"blink_period").asString());
		okPeriodTextBox.addStyle("width: 4em;");
		okPeriodTextBox.setAttribute("maxlength", "6");
		triggerDOFormList.addRow(_("On status change triggers blink for"), array(okPeriodTextBox, SPACE, _("seconds")));

		CTabView severityView = new CTabView();
		severityView.addTab("triggerdo", _("Trigger displaying options"), triggerDOFormList);

		CForm severityForm = new CForm();
		severityForm.setName("triggerDisplayOptions");
		severityForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		severityForm.addItem(severityView);
		severityForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButton("resetDefaults", _("Reset defaults"))));

		return severityForm;
	}

}
