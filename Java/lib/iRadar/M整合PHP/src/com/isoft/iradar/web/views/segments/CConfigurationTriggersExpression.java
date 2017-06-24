package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.inc.Defines.PARAM_TYPE_COUNTS;
import static com.isoft.iradar.inc.Defines.PARAM_TYPE_TIME;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTriggersExpression extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.triggers.expression.js", data);

		CWidget expressionWidget = new CWidget(null, "trigger-expression");

		// create form
		CForm expressionForm = new CForm();
		expressionForm.setName("expression");
		expressionForm.addVar("dstfrm", Nest.value(data,"dstfrm").$());
		expressionForm.addVar("dstfld1", Nest.value(data,"dstfld1").$());
		expressionForm.addVar("itemid", Nest.value(data,"itemid").$());

		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			expressionForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}

		// create form list
		CFormList expressionFormList = new CFormList("expressionFormList");

		// append item to form list
		CArray item = array(
			new CTextBox("description", Nest.value(data,"description").asString(), RDA_TEXTBOX_STANDARD_SIZE, true),
			new CButton("select", _("Select"), "return PopUp(\"popup.action?writeonly=1&dstfrm="+expressionForm.getName()+
				"&dstfld1=itemid&dstfld2=description&submitParent=1"+(!empty(Nest.value(data,"parent_discoveryid").$()) ? "&normal_only=1" : "")+
				"&srctbl=items&srcfld1=itemid&srcfld2=name\", 0, 0, \"rda_popup_item\");",
				"formlist"
			)
		);
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			item.add(new CButton("select", _("Select prototype"), "return PopUp(\"popup.action?dstfrm="+expressionForm.getName()+
				"&dstfld1=itemid&dstfld2=description&submitParent=1"+url_param(idBean, "parent_discoveryid", true)+
				"&srctbl=prototypes&srcfld1=itemid&srcfld2=name\", 0, 0, \"rda_popup_item\");",
				"formlist"
			));
		}

		expressionFormList.addRow(_("Item"), item);

		CComboBox functionComboBox = new CComboBox("expr_type", Nest.value(data,"expr_type").$(), "submit()");
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"functions").asCArray()).entrySet()) {
		    Object id = e.getKey();
		    Map f = e.getValue();
			functionComboBox.addItem(id, Nest.value(f,"description").asString());
		}
		expressionFormList.addRow(_("Function"), functionComboBox);

		if (isset(Nest.value(data,"functions",data.get("selectedFunction"),"params").$())) {
			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"functions",data.get("selectedFunction"),"params").asCArray()).entrySet()) {
			    int paramId = Nest.as(e.getKey()).asInteger();
			    Map paramFunction = e.getValue();
				Object paramValue = isset(Nest.value(data,"param").asCArray(),paramId) ?Nest.value(data,"param").asCArray().get(paramId) : null;

				if (Nest.value(paramFunction,"T").asInteger() == T_RDA_INT) {
					Object paramTypeElement = null;

					if (paramId == 0
						|| (paramId == 1
							&& ("regexp".equals(substr(Nest.value(data,"expr_type").asString(), 0, 6))
								|| "iregexp".equals(substr(Nest.value(data,"expr_type").asString(), 0, 7))
								|| ("str".equals(substr(Nest.value(data,"expr_type").asString(), 0, 3)) && !"strlen".equals(substr(Nest.value(data,"expr_type").asString(), 0, 6)))))) {
						if (isset(Nest.value(paramFunction,"M").$())) {
							paramTypeElement = new CComboBox("paramtype", Nest.value(data,"paramtype").$());

							for (Entry<Object, String> ep : ((CArray<String>)Nest.value(paramFunction,"M").asCArray()).entrySet()) {
							    Object mid = ep.getKey();
							    String caption = ep.getValue();
								((CComboBox)paramTypeElement).addItem(mid, caption);
							}
						} else {
							expressionForm.addVar("paramtype", PARAM_TYPE_TIME);
							paramTypeElement = SPACE+_("Time");
						}
					}

					Object paramField = null;
					if (paramId == 1
							&& (!"str".equals(substr(Nest.value(data,"expr_type").asString(), 0, 3)) || "strlen".equals(substr(Nest.value(data,"expr_type").asString(), 0, 6)))
							&& !"regexp".equals(substr(Nest.value(data,"expr_type").asString(), 0, 6))
							&& !"iregexp".equals(substr(Nest.value(data,"expr_type").asString(), 0, 7))) {
						paramTypeElement = SPACE+_("Time");
						paramField  = new CTextBox("param["+paramId+"]", Nest.as(paramValue).asString(), 10);
					} else {
						paramField = (Nest.value(data,"paramtype").asInteger() == PARAM_TYPE_COUNTS)
							? new CNumericBox("param["+paramId+"]", Nest.as(Nest.as(paramValue).asInteger()).asString(), 10)
							: new CTextBox("param["+paramId+"]", Nest.as(Nest.as(paramValue).asInteger()).asString(), 10);
					}

					expressionFormList.addRow(Nest.value(paramFunction,"C").$()+" ", array(paramField, paramTypeElement));
				} else {
					expressionFormList.addRow(Nest.value(paramFunction,"C").$(), new CTextBox("param["+paramId+"]", Nest.as(paramValue).asString(), 30));
					expressionForm.addVar("paramtype", PARAM_TYPE_TIME);
				}
			}
		} else {
			expressionForm.addVar("paramtype", PARAM_TYPE_TIME);
			expressionForm.addVar("param", 0);
		}

		expressionFormList.addRow("N", new CTextBox("value", Nest.value(data,"value").asString(), 10));

		// append tabs to form
		CTabView expressionTab = new CTabView();
		expressionTab.addTab("expressionTab", _("Trigger expression condition"), expressionFormList);
		expressionForm.addItem(expressionTab);

		// append buttons to form
		expressionForm.addItem(makeFormFooter(
			new CSubmit("insert", _("Insert")),
			new CButtonCancel(url_params(idBean, array("parent_discoveryid", "dstfrm", "dstfld1"))
		)));

		expressionWidget.addItem(expressionForm);

		return expressionWidget;
	}

}
