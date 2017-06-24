package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationScreenList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();
		
		// create form
		CForm screenForm = new CForm();
		screenForm.setName("screenForm");
		screenForm.addVar("templateid", Nest.value(data,"templateid").$());
		
		CToolBar tb = new CToolBar(screenForm);
		
		// create new screen button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create screen"),"","orange create"));
		if (!empty(Nest.value(data,"templateid").$())) {
			createForm.addVar("templateid", Nest.value(data,"templateid").$());
		}
		tb.addForm(createForm);
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = null;
//		if (empty(Nest.value(data,"templateid").$())) {
//			goOption = new CComboItem("export", _("Export selected"));
//			goOption.setAttribute("class", "orange export");
//			goComboBox.add(goOption);
//		}
		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected screens?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);

		tb.addComboBox(goComboBox);
		rda_add_post_js("chkbxRange.pageGoName = \"screens\";");
		
		// header
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		screenWidget.addItem(headerActions);
		
		// create table
		CTableInfo screenTable = new CTableInfo(_("No screens found."));
		String screenClass = screenTable.getAttribute("class").toString();
		screenClass += " table_monitor";
		screenTable.setAttribute("class", screenClass);
		
		screenTable.setHeader(array(
			new CCheckBox("all_screens", false, "checkAll(\""+screenForm.getName()+"\", \"all_screens\", \"screens\");"),
			make_sorting_header(_("Name"), "name"),
			_("Dimension (cols x rows)"),
			_("Screen")
		));

		for(Map screen : (CArray<Map>)Nest.value(data,"screens").asCArray()) {
			CLink displayLink = new CLink(_("Displaying"), "screens.action?elementid="+Nest.value(screen,"screenid").asString());
			displayLink.setAttribute("target", "_blank");
			displayLink.setAttribute("style", "margin-left: 1.5em");
			
			screenTable.addRow(array(
				new CCheckBox("screens["+Nest.value(screen,"screenid").asString()+"]", false, null, Nest.value(screen,"screenid").asString()),
				new CLink(Nest.value(screen,"name").$(), "screenedit.action?screenid="+Nest.value(screen,"screenid").asString()+url_param(idBean, "templateid")),
				Nest.value(screen,"hsize").$()+" x "+Nest.value(screen,"vsize").$(),
				CArray.array(
					new CLink(_("Edit"), "?form=update&screenid="+Nest.value(screen,"screenid").asString()+url_param(idBean, "templateid")),
					displayLink
				)
			));
		}
		// append table to form
		screenForm.addItem(array(screenTable, Nest.value(data,"paging").$()));

		// append form to widget
		screenWidget.addItem(screenForm);
		return screenWidget;
	}
}
