package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.array_valuesN;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.RegexpUtil;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralRegularexpressionsEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/adm.regexprs.edit.js");

		rda_add_post_js("iradarRegExp.addExpressions("+CJs.encodeJson(array_valuesN(Nest.value(data,"expressions").asCArray()))+")");

		CForm form = new CForm();
		form.attr("id", "iradarRegExpForm");
		form.addVar("form", 1);
		form.addVar("regexpid", Nest.value(data,"regexpid").$());

		/* Expressions tab */
		CFormList exprTab = new CFormList("exprTab");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 128);
		nameTextBox.attr("autofocus", "autofocus");
		exprTab.addRow(_("Name"), nameTextBox);

		CTable exprTable = new CTable(null, "formElementTable formWideTable");
		exprTable.attr("id", "exprTable");
		exprTable.setHeader(array(
			_("Expression"),
			new CCol(_("Expression type"), "nowrap"),
			new CCol(_("Case sensitive"), "nowrap"),
			SPACE
		));
		exprTable.setFooter(new CButton("add", _("Add"), null, "link_menu exprAdd"));
		exprTab.addRow(_("Expressions"), new CDiv(exprTable, "inlineblock border_dotted objectgroup"));

		CTable exprForm = new CTable(null, "formElementTable");
		exprForm.addRow(array(_("Expression"), new CTextBox("expressionNew", null, RDA_TEXTBOX_STANDARD_SIZE)));
		exprForm.addRow(array(_("Expression type"), new CComboBox("typeNew", null, null, RegexpUtil.expression_type2str())));
		exprForm.addRow(array(_("Delimiter"), new CComboBox("delimiterNew", null, null, RegexpUtil.expressionDelimiters())), null, "delimiterNewRow");
		exprForm.addRow(array(_("Case sensitive"), new CCheckBox("case_sensitiveNew")));
		CArray exprFormFooter = array(
			new CButton("saveExpression", _("Add"), null, "link_menu"),
			SPACE,
			new CButton("cancelExpression", _("Cancel"), null, "link_menu")
		);
		exprTab.addRow(null, new CDiv(array(exprForm, exprFormFooter), "objectgroup inlineblock border_dotted"), true, "exprForm");

		/* Test tab */
		CFormList testTab = new CFormList("testTab");
		testTab.addRow(_("Test string"), new CTextArea("test_string", Nest.value(data,"test_string").asString()));
		CDiv preloaderDiv = new CDiv(null, "preloader", "testPreloader");
		preloaderDiv.addStyle("display: none");
		testTab.addRow(SPACE, array(new CButton("testExpression", _("Test expressions")), preloaderDiv));

		CTableInfo tabExp = new CTableInfo(null);
		tabExp.attr("id", "testResultTable");
		tabExp.setHeader(array(_("Expression"), _("Expression type"), _("Result")));
		testTab.addRow(_("Result"), tabExp);

		CTabView regExpView = new CTabView();
		if (empty(Nest.value(data,"form_refresh").$())) {
			regExpView.setSelected("0");
		}
		regExpView.addTab("expr", _("Expressions"), exprTab);
		regExpView.addTab("test", _("Test"), testTab);
		form.addItem(regExpView);

		// footer
		CArray secondaryActions = array(new CButtonCancel());
		if (isset(Nest.value(data,"regexpid").$())) {
			array_unshift(secondaryActions,
				new CSubmit("clone", _("Clone")),
				new CButtonDelete(_("Delete regular expression?"), url_param(idBean, "form")+url_param(idBean, "regexpid")+url_param(idBean, "delete", false, "go"))
			);
		}
		form.addItem(makeFormFooter(new CSubmit("save", _("Save")), secondaryActions));

		return form;
	}

}
