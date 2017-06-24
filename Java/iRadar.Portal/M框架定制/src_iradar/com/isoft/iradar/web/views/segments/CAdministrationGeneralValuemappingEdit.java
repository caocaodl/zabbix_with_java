package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralValuemappingEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.valuemapping.edit.js");

		CForm valueMappingForm = new CForm();
		valueMappingForm.setName("valueMappingForm");
		valueMappingForm.addVar("form", Nest.value(data,"form").$());
		valueMappingForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		valueMappingForm.addVar("valuemapid", Nest.value(data,"valuemapid").$());

		// create form list
		CFormList valueMappingFormList = new CFormList("valueMappingFormList",IMonConsts.STYLE_CLASS_MULTLINE);

		// name
		CTextBox nameTextBox = new CTextBox("mapname", Nest.value(data,"mapname").asString(), 40, false, 64);
		nameTextBox.attr("autofocus", "autofocus");
		valueMappingFormList.addRow(_("Name"), nameTextBox);

		// mappings
		CTable mappingsTable = new CTable(SPACE, "formElementTable");
		mappingsTable.setAttribute("id", "mappingsTable");
		mappingsTable.addRow(array(_("Value"), SPACE, _("Mapped to"), SPACE));
		mappingsTable.addRow(new CRow(new CCol(new CButton("addMapping", _("Add"), "", "link_menu add"),null,4),"cmd_row"));

		valueMappingFormList.addRow(_("Mappings"), new CDiv(mappingsTable, "border_dotted inlineblock objectgroup"));

		// add mappings to form by js
		if (empty(Nest.value(data,"mappings").$())) {
			rda_add_post_js("mappingsManager.addNew();");
		} else {
			rda_add_post_js("mappingsManager.addExisting("+rda_jsvalue(Nest.value(data,"mappings").$())+");");
		}

		// append tab
		CTabView valueMappingTab = new CTabView();
		valueMappingTab.addTab("valuemapping", _("Value mapping"), valueMappingFormList);
		valueMappingForm.addItem(valueMappingTab);
		Map page = RadarContext.getContext().getPage();
		String link = (String)page.get("file") + "?delete=1" + url_param(idBean, "valuemapid");
		Curl url = new Curl(link);
		String  action = "redirect('" + url.getUrl() + "')";
		 
		// append buttons
		if (!empty(Nest.value(data,"valuemapid").$())) {
			CButton del=new CButton("delete","删除");
			del.onClick("showModalWindow('提示','删除所选的键值映射?',[{"
				+ "text:'确定',click:function(){ return "+action+" }"
				+ "},{text:'取消',click:function(){jQuery(this).dialog('destroy')}}])");
			
			valueMappingForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					//new CButtonDelete(Nest.value(data,"confirmMessage").asString(), url_param(idBean, "valuemapid")),
					del,
					new CButtonCancel()
				)
			));
		} else {
			valueMappingForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel()));
		}

		return valueMappingForm;
	}
}
