package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.HttpTestUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationThttpconfList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget httpWidget = new CWidget();
		
		
		//CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		//CForm filterForm = new CForm("get");
		//filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		//filterForm.addItem(array(SPACE+_("VM Manage")+SPACE, pageFilter.getHostsCB()));
		
		//httpWidget.addHeader(filterForm);
		
		CForm httpForm = new CForm();
		httpForm.setName("scenarios");
		httpForm.addVar("hostid", Nest.value(data,"hostid").$());		
		
		CToolBar tb = new CToolBar(httpForm);
		
		// create form
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

		/*if (empty(Nest.value(data,"hostid").$())) {
			CSubmit createButton = new CSubmit("form", _("Create Web Monitoring (select host first)"),"","orange create");
			createButton.setEnabled(false);
			createForm.addItem(createButton);
		} else {*/
			createForm.addItem(new CSubmit("form", _("Create Web Monitoring"),"","orange create"));
		//}
		tb.addForm(createForm);
		
		CArray<CComboItem> goComboBox = array();

		/*CComboItem goOption = new CComboItem("clean_history", _("Clear history for selected"));
		goOption.setAttribute("confirm", _("Delete history of selected WEB scenarios?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);*/

		CComboItem goOption = goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected WEB supervise?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"group_httptestid\";");
		
	
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		httpWidget.addItem(headerActions);
		

		CTableInfo httpTable = new CTableInfo(_("No web scenarios found."));
		httpTable.setHeader(array(
			new CCheckBox("all_httptests", false, "checkAll(\""+httpForm.getName()+"\", \"all_httptests\", \"group_httptestid\");"),
			Nest.value(data,"displayNodes").asBoolean() ? _("Node") : null,
			make_sorting_header(_("Name"), "name"),
			"URL",
			_("Item Frequency"),
			_("Operations")
		));
		
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"httpTests").asCArray()).entrySet()) {
		    Object httpTestId = e.getKey();
		    Map httpTest = e.getValue();
		    String name = Nest.value(httpTest,"name").asString();
			CArray edit = array(new CLink(_("monitorConfig"), "?form=update&httptestid="+httpTest.get("httptestid")+"&hostid="+Nest.value(httpTest,"hostid").$()));
  		    CArray buttons= array(edit);
			httpTable.addRow(array(
				new CCheckBox("group_httptestid["+Nest.value(httpTest,"httptestid").$()+"]", false, null, Nest.value(httpTest,"httptestid").asInteger()),
				name,
				Nest.value(httpTest, "url").$(),
				Nest.value(httpTest,"delay").$(),
				buttons
			));
		}

		// append table to form
		httpForm.addItem(array(httpTable, Nest.value(data,"paging").$()));

		// append form to widget
		httpWidget.addItem(httpForm);

		return httpWidget;
	}

}
