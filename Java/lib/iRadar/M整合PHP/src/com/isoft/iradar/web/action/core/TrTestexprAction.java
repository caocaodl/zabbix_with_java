package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.SORT_NUMERIC;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ksort;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.inc.Defines.EXPRESSION_FUNCTION_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_HOST_ITEM_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_HOST_UNKNOWN;
import static com.isoft.iradar.inc.Defines.EXPRESSION_NOT_A_MACRO_ERROR;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.MD5Util.MD5;
import static com.isoft.iradar.inc.TriggersUtil.analyzeExpression;
import static com.isoft.iradar.inc.TriggersUtil.evalExpressionData;
import static com.isoft.iradar.inc.TriggersUtil.get_item_function_info;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TrTestexprAction extends RadarBaseAction {
	
	private String COMBO_PATTERN;
	private int COMBO_PATTERN_LENGTH;

	@Override
	protected void doInitPage() {
		page("title", _("Test"));
		page("file", "tr_testexpr.action");

		define("RDA_PAGE_NO_MENU", 1);
		COMBO_PATTERN = "str_in_array({},array(";
		COMBO_PATTERN_LENGTH = rda_strlen(COMBO_PATTERN);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		Map<String,String> definedErrorPhrases = (Map)map(
			EXPRESSION_HOST_UNKNOWN, _("Unknown host, no such host present in system"),
			EXPRESSION_HOST_ITEM_UNKNOWN, _("Unknown host item, no such item in selected host"),
			EXPRESSION_NOT_A_MACRO_ERROR, _("Given expression is not a macro"),
			EXPRESSION_FUNCTION_UNKNOWN, _("Incorrect function is used")
		);
		
		// expression analyze
		String expression = FuncsUtil.get_request_real("expression", "");

		define("NO_LINK_IN_TESTING", true);
		CArray analyzeExpressions = analyzeExpression(this.getIdentityBean(), executor, expression);
		String outline = Nest.value(analyzeExpressions,0).asString();
		CArray<Map> eHTMLTree = Nest.value(analyzeExpressions,1).asCArray();

		// test data (create table, create check fields)
		CTable dataTable = new CTable(null, "tableinfo");
		dataTable.setAttribute("id", "data_list");
		dataTable.setHeader(array(_("Expression Variable Elements"), _("Result type"), _("Value")));

		CArray fields = array();
		CArray rplcts = array();
		boolean allowedTesting = true;

		CTriggerExpression expressionData = new CTriggerExpression();
		CArray macrosData = null;
		if (expressionData.parse(expression)) {
			macrosData = array();

			CArray<Map> expressions = array_merge(expressionData.expressions, expressionData.macros, expressionData.usermacros, expressionData.lldmacros);

			for(Map exprPart : expressions) {
				if (isset(macrosData,exprPart.get("expression"))) {
					continue;
				}

				String fname = "test_data_"+MD5(Nest.value(exprPart,"expression").asString());
				Nest.value(macrosData,exprPart.get("expression")).$(get_request(fname, ""));

				Object info = get_item_function_info(this.getIdentityBean(), executor, Nest.value(exprPart,"expression").asString());

				CTag control;
				if (!isArray(info) && isset(definedErrorPhrases,info)) {
					allowedTesting = false;
					control = new CTextBox(fname, Nest.value(macrosData,exprPart.get("expression")).asString(), 30);
					control.setAttribute("disabled", "disabled");
				} else {
					String validation = Nest.value((Map)info,"validation").asString();

					if (substr(validation, 0, COMBO_PATTERN_LENGTH).equals(COMBO_PATTERN)) {
						String[] vals = explode(",", substr(validation, COMBO_PATTERN_LENGTH, rda_strlen(validation) - COMBO_PATTERN_LENGTH - 4));
						control = new CComboBox(fname, Nest.value(macrosData,exprPart.get("expression")).asString());
						for(String v : vals) {
							((CComboBox)control).addItem(v, v);
						}
					} else {
						control = new CTextBox(fname, Nest.value(macrosData,exprPart.get("expression")).asString(), 30);
					}

					Nest.value(fields,fname).$(array(Nest.value((Map)info,"type").$(), O_OPT, null, validation, "isset({test_expression})", Nest.value(exprPart,"expression").$()));
				}

				Object resultType = (isArray(info) || !isset(definedErrorPhrases,info))
					? Nest.value((Map)info,"value_type").$()
					: new CCol(definedErrorPhrases.get(info), "disaster");

				dataTable.addRow(new CRow(array(Nest.value(exprPart,"expression").$(), resultType, control)));
			}
		}

		// checks
		Nest.value(fields,"test_expression").$(array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null));
		boolean test;
		if (!check_fields(getIdentityBean(), fields)) {
			test = false;
		}

		// actions
		if (isset(_REQUEST,"test_expression")) {
			show_messages();
			test = true;
		} else {
			test = false;
		}

		// form
		CFormTable testForm = new CFormTable(_("Test"), "tr_testexpr.action");
		testForm.setHelp("web.testexpr.service.action");
		testForm.setTableClass("formlongtable formtable");
		testForm.addVar("form_refresh", get_request("form_refresh", 1));
		testForm.addVar("expression", expression);
		testForm.addRow(_("Test data"), dataTable);

		CTable resultTable = new CTable(null, "tableinfo");
		resultTable.setAttribute("id", "result_list");
		resultTable.setOddRowClass("even_row");
		resultTable.setEvenRowClass("even_row");
		resultTable.setHeader(array(_("Expression"), _("Result")));

		ksort(rplcts, SORT_NUMERIC);

		Object result;
		for(Map e : eHTMLTree) {
			result = map("result", "-", "error", "");

			if (allowedTesting && test && isset(e,"expression")) {
				result = evalExpressionData(Nest.value(e,"expression","value").asString(), macrosData);
			}

			String style = "text-align: center;";
			if (!"-".equals(Nest.value(result,"result").asString())) {
				style = ("TRUE".equals(Nest.value(result,"result").asString()))
					? "background-color: #ccf; color: #00f;"
					: "background-color: #fcc; color: #f00;";
			}

			CCol col = new CCol(array(Nest.value(result,"result").asString(), SPACE, Nest.value(result,"error").asString()));
			col.setAttribute("style", style);

			resultTable.addRow(new CRow(array(Nest.value(e,"list").$(), col)));
		}

		result = map("result", "-", "error", "");

		if (allowedTesting && test) {
			result = evalExpressionData(expression, macrosData);
		}

		String style = "text-align: center;";
		if (!"-".equals(Nest.value(result,"result").asString())) {
			style = ("TRUE".equals(Nest.value(result,"result").asString()))
				? "background-color: #ccf; color: #00f;"
				: "background-color: #fcc; color: #f00;";
		}

		CCol col = new CCol(array(Nest.value(result,"result").asString(), SPACE, Nest.value(result,"error").asString()));
		col.setAttribute("style", style);

		resultTable.setFooter(array(outline, col), resultTable.headerClass);

		testForm.addRow(_("Result"), resultTable);

		// action buttons
		CSubmit testButton = new CSubmit("test_expression", _("Test"));
		if (!allowedTesting) {
			testButton.setAttribute("disabled", "disabled");
		}

		testForm.addItemToBottomRow(testButton);
		testForm.addItemToBottomRow(SPACE);
		testForm.addItemToBottomRow(new CButton("close", _("Close"), "javascript: self.close();"));
		testForm.show();
	}
}
