package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.IM_ESTABLISHED;
import static com.isoft.iradar.inc.Defines.IM_FORCED;
import static com.isoft.iradar.inc.Defines.IM_TREE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_STANDARD_ROWS;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_STANDARD_WIDTH;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_ENABLED;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSeverity;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTriggersEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.triggers.edit.js");

		CWidget triggersWidget = new CWidget();

		// append host summary to widget header
		if (!empty(Nest.value(data,"hostid").$())) {
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				triggersWidget.addItem(get_header_host_table(idBean, executor, "triggers", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));
			} else {
				triggersWidget.addItem(get_header_host_table(idBean, executor, "triggers", Nest.value(data,"hostid").asLong(true)));
			}
		}

		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			triggersWidget.addPageHeader(_("CONFIGURATION OF TRIGGER PROTOTYPES"));
		} else {
			triggersWidget.addPageHeader(_("CONFIGURATION OF TRIGGERS"));
		}

		// create form
		CForm triggersForm = new CForm();
		triggersForm.setName("triggersForm");
		triggersForm.addVar("form", Nest.value(data,"form").$());
		triggersForm.addVar("hostid", Nest.value(data,"hostid").$());
		triggersForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		triggersForm.addVar("input_method", Nest.value(data,"input_method").$());
		triggersForm.addVar("toggle_input_method", "");
		triggersForm.addVar("remove_expression", "");
		if (!empty(Nest.value(data,"triggerid").$())) {
			triggersForm.addVar("triggerid", Nest.value(data,"triggerid").$());
		}

		// create form list
		CFormList triggersFormList = new CFormList("triggersFormList");
		if (!empty(Nest.value(data,"templates").$())) {
			triggersFormList.addRow(_("Parent triggers"), Nest.value(data,"templates").$());
		}
		CTextBox nameTextBox = new CTextBox("description", Nest.value(data,"description").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean());
		nameTextBox.attr("autofocus", "autofocus");
		triggersFormList.addRow(_("Name"), nameTextBox);

		// append expression to form list
		CTextArea expressionTextBox = new CTextArea(
			Nest.value(data,"expression_field_name").asString(),
			Nest.value(data,"expression_field_value").asString(),
			(Map)map(
				"rows", RDA_TEXTAREA_STANDARD_ROWS,
				"width", RDA_TEXTAREA_STANDARD_WIDTH,
				"readonly", Nest.value(data,"expression_field_readonly").$()
			)
		);
		if ("yes".equals(Nest.value(data,"expression_field_readonly").$())) {
			triggersForm.addVar("expression", Nest.value(data,"expression").$());
		}

		CButton addExpressionButton = new CButton(
			"insert",
			(Nest.value(data,"input_method").asInteger() == IM_TREE) ? _("Edit") : _("Add"),
			"return PopUp(\"popup_trexpr.action?dstfrm="+triggersForm.getName()+
				"&dstfld1="+Nest.value(data,"expression_field_name").$()+"&srctbl=expression"+url_param(idBean, "parent_discoveryid")+
				"&srcfld1=expression&expression=\" + encodeURIComponent(jQuery(\"[name='"+Nest.value(data,"expression_field_name").$()+"']\").val()), 800, 265);",
			"formlist"
		);
		if (Nest.value(data,"limited").asBoolean()) {
			addExpressionButton.setAttribute("disabled", "disabled");
		}
		CArray expressionRow = array(expressionTextBox, addExpressionButton);
		if (!empty(Nest.value(data,"expression_macro_button").$())) {
			array_push(expressionRow, Nest.value(data,"expression_macro_button").$());
		}
		CSpan inputMethodToggle = null;
		if (Nest.value(data,"input_method").asInteger() == IM_TREE) {
			array_push(expressionRow, BR());
			if (empty(Nest.value(data,"outline").$())) {
				// add button
				addExpressionButton = new CSubmit("add_expression", _("Add"), null, "formlist");
				if (Nest.value(data,"limited").asBoolean()) {
					addExpressionButton.setAttribute("disabled", "disabled");
				}
				array_push(expressionRow, addExpressionButton);
			} else {
				// add button
				addExpressionButton = new CSubmit("and_expression", _("AND"), null, "formlist");
				if (Nest.value(data,"limited").$() == "yes") {
					addExpressionButton.setAttribute("disabled", "disabled");
				}
				array_push(expressionRow, addExpressionButton);

				// or button
				CSubmit orExpressionButton = new CSubmit("or_expression", _("OR"), null, "formlist");
				if (Nest.value(data,"limited").asBoolean()) {
					orExpressionButton.setAttribute("disabled", "disabled");
				}
				array_push(expressionRow, orExpressionButton);

				// replace button
				CSubmit replaceExpressionButton = new CSubmit("replace_expression", _("Replace"), null, "formlist");
				if (Nest.value(data,"limited").asBoolean()) {
					replaceExpressionButton.setAttribute("disabled", "disabled");
				}
				array_push(expressionRow, replaceExpressionButton);
			}
		} else if (Nest.value(data,"input_method").asInteger() != IM_FORCED) {
			inputMethodToggle  = new CSpan(_("Expression constructor"), "link");
			inputMethodToggle.setAttribute("onclick", "javascript: "+
				"document.getElementById(\"toggle_input_method\").value=1;"+
				"document.getElementById(\"input_method\").value="+((Nest.value(data,"input_method").asInteger() == IM_TREE) ? IM_ESTABLISHED : IM_TREE)+";"+
				"document.forms[\""+triggersForm.getName()+"\"].submit();"
			);
			expressionRow.add(array(BR(), inputMethodToggle));
		}
		triggersFormList.addRow(_("Expression"), expressionRow);

		// append expression table to form list
		CRow row = null;
		if (Nest.value(data,"input_method").asInteger() == IM_TREE) {
			CTable expressionTable = new CTable(null, "formElementTable");
			expressionTable.setAttribute("style", "min-width: 500px;");
			expressionTable.setAttribute("id", "exp_list");
			expressionTable.setOddRowClass("even_row");
			expressionTable.setEvenRowClass("even_row");
			expressionTable.setHeader(array(
				(Nest.value(data,"limited").asBoolean()) ? null : _("Target"),
				_("Expression"),
				empty(Nest.value(data,"parent_discoveryid").$()) ? _("Error") : null,
				(Nest.value(data,"limited").$() == "yes") ? null : _("Action")
			));

			boolean allowedTesting = true;
			if (!empty(Nest.value(data,"eHTMLTree").$())) {
				for(Entry<Object,Map> t : ((CArray<Map>)Nest.value(data,"eHTMLTree").asCArray()).entrySet()) {
					Object i = t.getKey();
					Map tree = t.getValue();
					CSpan deleteUrl = null;
					CCheckBox triggerCheckbox = null;
					if (Nest.value(data,"limited").$() != "yes") {
						deleteUrl  = new CSpan(_("Delete"), "link");
						deleteUrl.setAttribute("onclick", "javascript:"+
							" if (Confirm(\""+_("Delete expression?")+"\")) {"+
								" delete_expression(\""+Nest.value(tree,"id").$() +"\");"+
								" document.forms[\""+triggersForm.getName()+"\"].submit();"+
							" }"
						);
						triggerCheckbox  = new CCheckBox("expr_target_single", (Nest.as(i).asInteger() == 0) ? true : false, "check_target(this);", Nest.value(tree,"id").asString());
					} else {
						triggerCheckbox = null;
					}

					CCol errorColumn = null;
					if (empty(Nest.value(data,"parent_discoveryid").$())) {
						CImg errorImg = null;
						if (!isset(Nest.value(tree,"expression","levelErrors").$())) {
							errorImg = new CImg("images/general/ok_icon.png", "expression_no_errors");
							errorImg.setHint(_("No errors found."));
						} else {
							allowedTesting = false;
							errorImg = new CImg("images/general/error2.png", "expression_errors");
							CArray errorTexts = array();
							if (isArray(Nest.value(tree,"expression","levelErrors").$())) {
								for (Entry<Object, String> e : ((CArray<String>)Nest.value(tree,"expression","levelErrors").asCArray()).entrySet()) {
								    Object expVal = e.getKey();
								    String errTxt = e.getValue();
									if (count(errorTexts) > 0) {
										array_push(errorTexts, BR());
									}
									array_push(errorTexts, expVal, ":", errTxt);
								}
							}
							errorImg.setHint(errorTexts, "", "left");
						}
						errorColumn = new CCol(errorImg, "center");
					} else {
						errorColumn = null;
					}

					// templated trigger
					if (Nest.value(data,"limited").asBoolean()) {
						// make all links inside inactive
						for (Object link : Nest.value(tree,"list").asCArray()) {
							if(link instanceof CSpan && "link".equals(((CSpan)link).getAttribute("class"))){
								((CSpan)link).removeAttribute("class");
								((CSpan)link).setAttribute("onclick", "");
							}
						}
					}

					row  = new CRow(array(triggerCheckbox, Nest.value(tree,"list").$(), errorColumn, isset(deleteUrl) ? deleteUrl : null));
					expressionTable.addRow(row);
				}
			} else {
				allowedTesting = false;
				Nest.value(data,"outline").$("");
			}

			CButton testButton = new CButton("test_expression", _("Test"),
				"openWinCentered(\"tr_testexpr.action?expression=\" + encodeURIComponent(this.form.elements[\"expression\"].value),"+
				"\"ExpressionTest\", 850, 400, \"titlebar=no, resizable=yes, scrollbars=yes\"); return false;",
				"link_menu"
			);
			if (!allowedTesting) {
				testButton.setAttribute("disabled", "disabled");
			}
			if (empty(Nest.value(data,"outline").$())) {
				testButton.setAttribute("disabled", "disabled");
			}

			CSpan wrapOutline = new CSpan(array(Nest.value(data,"outline").$()));
			triggersFormList.addRow(SPACE, array(
				wrapOutline,
				BR(),
				BR(),
				new CDiv(array(expressionTable, testButton), "objectgroup inlineblock border_dotted ui-corner-all")
			));

			inputMethodToggle = new CSpan(_("Close expression constructor"), "link");
			inputMethodToggle.setAttribute("onclick", "javascript: "+
				"document.getElementById(\"toggle_input_method\").value=1;"+
				"document.getElementById(\"input_method\").value="+IM_ESTABLISHED+";"+
				"document.forms[\""+triggersForm.getName()+"\"].submit();"
			);
			triggersFormList.addRow(SPACE, array(inputMethodToggle, BR()));
		}

		triggersFormList.addRow(_("Multiple PROBLEM events generation"), new CCheckBox("type", ((Nest.value(data,"type").asInteger() == TRIGGER_MULT_EVENT_ENABLED) ? true : false), null, 1));
		triggersFormList.addRow(_("Description"), new CTextArea("comments", Nest.value(data,"comments").asString()));
		triggersFormList.addRow(_("URL"), new CTextBox("url", Nest.value(data,"url").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		triggersFormList.addRow(_("Severity"), new CSeverity(idBean, executor, map("name", "priority", "value", Nest.value(data,"priority").$())));

		// append status to form list
		Boolean status = null;
		if (empty(Nest.value(data,"triggerid").$()) && empty(Nest.value(data,"form_refresh").$())) {
			status = true;
		} else {
			status = (Nest.value(data,"status").asInteger() == 0) ? true : false;
		}
		triggersFormList.addRow(_("Enabled"), new CCheckBox("status", status, null, 1));

		// append tabs to form
		CTabView triggersTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			triggersTab.setSelected("0");
		}
		triggersTab.addTab(
			"triggersTab",
			empty(Nest.value(data,"parent_discoveryid").$()) ? _("Trigger") : _("Trigger prototype"), triggersFormList
		);

		/*
		 * Dependencies tab
		 */
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			CFormList dependenciesFormList = new CFormList("dependenciesFormList");
			CTable dependenciesTable = new CTable(_("No dependencies defined."), "formElementTable");
			dependenciesTable.setAttribute("style", "min-width: 500px;");
			dependenciesTable.setAttribute("id", "dependenciesTable");
			dependenciesTable.setHeader(array(_("Name"), _("Action")));

			for(Map dependency : (CArray<Map>)Nest.value(data,"db_dependencies").asCArray()) {
				triggersForm.addVar("dependencies[]", Nest.value(dependency,"triggerid").$(), "dependencies_"+Nest.value(dependency,"triggerid").$());

				row = new CRow(array(
					Nest.value(dependency,"host").$()+NAME_DELIMITER+Nest.value(dependency,"description").$(),
					new CButton("remove", _("Remove"), "javascript: removeDependency(\""+Nest.value(dependency,"triggerid").$()+"\");", "link_menu")
				));
				row.setAttribute("id", "dependency_"+Nest.value(dependency,"triggerid").$());
				dependenciesTable.addRow(row);
			}
			dependenciesFormList.addRow(
				_("Dependencies"),
				new CDiv(
					array(
						dependenciesTable,
						new CButton("bnt1", _("Add"),
							"return PopUp(\"popup.action?"+
								"srctbl=triggers"+
								"&srcfld1=triggerid"+
								"&reference=deptrigger"+
								"&multiselect=1"+
								"&with_triggers=1\", 1000, 700);",
							"link_menu"
						)
					),
					"objectgroup inlineblock border_dotted ui-corner-all"
				)
			);
			triggersTab.addTab("dependenciesTab", _("Dependencies"), dependenciesFormList);
		}

		// append tabs to form
		triggersForm.addItem(triggersTab);

		// append buttons to form
		CArray buttons = array();
		if (!empty(Nest.value(data,"triggerid").$())) {
			buttons.add(new CSubmit("clone", _("Clone")));

			CButtonDelete deleteButton = new CButtonDelete(
				!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete trigger prototype?") : _("Delete trigger?"),
				url_params(idBean, array("form", "groupid", "hostid", "triggerid", "parent_discoveryid"))
			);
			if (Nest.value(data,"limited").asBoolean()) {
				deleteButton.setAttribute("disabled", "disabled");
			}
			buttons.add(deleteButton);
		}
		buttons.add(new CButtonCancel(url_params(idBean, array("groupid", "hostid", "parent_discoveryid"))));
		triggersForm.addItem(makeFormFooter(
			new CSubmit("save", _("Save")),
			array(buttons)
		));

		triggersWidget.addItem(triggersForm);

		return triggersWidget;
	}

}
