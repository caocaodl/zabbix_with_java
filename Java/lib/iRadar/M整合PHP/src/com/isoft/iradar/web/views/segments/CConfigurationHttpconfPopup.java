package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHttpconfPopup extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.httpconf.popup.js");

		CWidget httpPopupWidget = new CWidget();

		boolean result = false;
		if (isset(_REQUEST,"save")) {
			result  = true;
			if ((!isset(_REQUEST,"stepid") || (isset(_REQUEST,"stepid") && !Nest.value(_REQUEST,"name").asString().equals(Nest.value(_REQUEST,"old_name").asString())))
					&& isset(_REQUEST,"steps_names")) {
				for(String name : (CArray<String>)Nest.value(_REQUEST,"steps_names").asCArray()) {
					if (name.equals(Nest.value(_REQUEST,"name").$())) {
						show_error_message(_s("Step with name \"%1$s\" already exists.", name));
						result = false;
					}
				}
			}
		}

		if (isset(_REQUEST,"save") && result) {
			if (!isset(_REQUEST,"stepid")) {
				insert_js("add_httpstep("+
							rda_jsvalue(Nest.value(_REQUEST,"dstfrm").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"name").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"timeout").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"url").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"posts").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"variables").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"required").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"status_codes").$())+");\n"
				);
			} else {
				insert_js("update_httpstep("+
							rda_jsvalue(Nest.value(_REQUEST,"dstfrm").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"list_name").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"stepid").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"name").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"timeout").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"url").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"posts").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"variables").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"required").$())+","+
							rda_jsvalue(Nest.value(_REQUEST,"status_codes").$())+");\n"
				);
			}
		} else {
			CForm httpPopupForm = new CForm();
			httpPopupForm.addVar("dstfrm", get_request("dstfrm", null));
			httpPopupForm.addVar("stepid", get_request("stepid", null));
			httpPopupForm.addVar("list_name", get_request("list_name", null));
			httpPopupForm.addVar("templated", get_request("templated", null));
			httpPopupForm.addVar("old_name", get_request("old_name", null));
			httpPopupForm.addVar("steps_names", get_request("steps_names", null));

			CFormList httpPopupFormList = new CFormList("httpPopupFormList");
			httpPopupFormList.addRow(_("Name"), new CTextBox("name", get_request("name", ""), RDA_TEXTBOX_STANDARD_SIZE, Nest.as(get_request("templated", null)).asBoolean(), 64));
			httpPopupFormList.addRow(_("URL"), new CTextBox("url", get_request("url", ""), RDA_TEXTBOX_STANDARD_SIZE));
			httpPopupFormList.addRow(_("Post"), new CTextArea("posts", get_request("posts", "")));
			httpPopupFormList.addRow(_("Variables"), new CTextArea("variables", get_request("variables", "")));
			httpPopupFormList.addRow(_("Timeout"), new CNumericBox("timeout", Nest.as(get_request("timeout", 15)).asString(), 5));
			httpPopupFormList.addRow(_("Required string"), new CTextBox("required", get_request("required", ""), RDA_TEXTBOX_STANDARD_SIZE));
			httpPopupFormList.addRow(_("Required status codes"), new CTextBox("status_codes", get_request("status_codes", ""), RDA_TEXTBOX_STANDARD_SIZE));

			// append tabs to form
			CTabView httpPopupTab = new CTabView();
			httpPopupTab.addTab("scenarioStepTab", _("Step of scenario"), httpPopupFormList);
			httpPopupForm.addItem(httpPopupTab);

			// append buttons to form
			Object stepid = get_request("stepid", null);
			httpPopupForm.addItem(makeFormFooter(
				new CSubmit("save", isset(stepid) ? _("Update") : _("Add")),
				new CButtonCancel(null, "close_window();")
			));

			httpPopupWidget.addItem(httpPopupForm);
		}

		return httpPopupWidget;
	}

}
