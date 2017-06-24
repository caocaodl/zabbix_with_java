package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
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

public class CConfigurationHttpconfList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget httpWidget = new CWidget();
		
		CForm httpForm = new CForm();
		httpForm.setName("scenarios");
		httpForm.addVar("hostid", Nest.value(data,"hostid").$());
		
		CToolBar tb = new CToolBar(httpForm);
		
		// create form
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

//		if (empty(Nest.value(data,"hostid").$())) {
//			CSubmit createButton = new CSubmit("form", _("Create scenario"),"","orange create");
//			show_messages(false,"",_("select host first"));
//			createButton.setEnabled(false);
//			createForm.addItem(createButton);
//		} else {
			createForm.addItem(new CSubmit("form", _("Create scenario"),"","orange create"));
//		}
		tb.addForm(createForm);
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected WEB scenarios?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm",_("Disable selected WEB scenarios?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("clean_history", _("Clear history for selected"));
		goOption.setAttribute("confirm", _("Delete history of selected WEB scenarios?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected WEB scenarios?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"group_httptestid\";");
		if(!empty(data.get("hostid"))) {
			rda_add_post_js("chkbxRange.prefix = \""+data.get("hostid")+"\";");
			rda_add_post_js("cookie.prefix = \""+data.get("hostid")+"\";");
		}
		
		// header
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));
		
		httpWidget.addHeader(filterForm);
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		httpWidget.addItem(headerActions);
		
		/*if (!empty(Nest.value(data,"hostid").$())) {//注释掉用不到的表头
			httpWidget.addItem(get_header_host_table(executor,"web", Nest.value(data,"hostid").asLong()));
		}*/

		CTableInfo httpTable = new CTableInfo(_("No web scenarios found."));
		if(empty(Nest.value(data,"hostid").$())){
			httpTable.setAttribute("class", httpTable.getAttribute("class")+" detailall");
		}else{
			httpTable.setAttribute("class", httpTable.getAttribute("class")+" normaldisplay");
		}
		httpTable.setHeader(array(
			new CCheckBox("all_httptests", false, "checkAll(\""+httpForm.getName()+"\", \"all_httptests\", \"group_httptestid\");"),
			(Nest.value(data,"hostid").asLong() == 0) ? make_sorting_header(_("Host"), "hostname") : null,
			make_sorting_header(_("Name"), "name"),
			_("Number of steps"),
			_("Update interval"),
			make_sorting_header(_("Status Of Valid"), "status"))
		);
		
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"httpTests").asCArray()).entrySet()) {
		    Object httpTestId = e.getKey();
		    Map httpTest = e.getValue();
		    CArray name = array();
			if (isset(Nest.value(data,"parentTemplates",httpTestId).$())) {
				Map template = Nest.value(data,"parentTemplates",httpTestId).asCArray();
				name.add(new CLink(Nest.value(template,"name").$(), "?groupid=0&hostid="+Nest.value(template,"id").$(), "unknown"));
				name.add(NAME_DELIMITER);
			}
			name.add(new CLink(Nest.value(httpTest,"name").$(), "?form=update&httptestid="+httpTest.get("httptestid")+"&hostid="+Nest.value(httpTest,"hostid").$()));
			Object status = new CLink(
					HttpTestUtil.httptest_status2str(Nest.value(httpTest,"status").asInteger()),
					"?group_httptestid[]="+httpTest.get("httptestid")+
						"&hostid="+httpTest.get("hostid")+"&statusflag=1"+
						"&go="+(Nest.value(httpTest,"status").asBoolean() ? "activate" : "disable"),
						CommonUtils.status2style(Nest.value(httpTest,"status").asInteger())
				);
			
			//自定义属性 用于页面Ajax异步切换状态
			Object _go =  Nest.value(httpTest,"status").asBoolean() ? "activate" : "disable";
			Object _hostid = httpTest.get("hostid");
			Object _group_httptestid = httpTest.get("httptestid");
			
			CLink statusLink = (CLink)status;
			String[] aks = statusLink.getUrl().split("=");
			Object _sid = aks[aks.length-1];
			
			statusLink.setAttribute("go", _go);
			statusLink.setAttribute("sid", _sid);
			statusLink.setAttribute("statusflag", "1");
			statusLink.setAttribute("group_httptestid[]", _group_httptestid);
			statusLink.setAttribute("hostid", _hostid);
			statusLink.setAttribute("onclick", "changeHttpConfStatus(this)");
			statusLink.setAttribute("href", "javascript:void(0)");
			
			CCol cstatus = new CCol(new CDiv(statusLink, "switch"));
			httpTable.addRow(array(
				new CCheckBox("group_httptestid["+Nest.value(httpTest,"httptestid").$()+"]", false, null, Nest.value(httpTest,"httptestid").asInteger()),
				(Nest.value(data,"hostid").asInteger() > 0) ? null : Nest.value(httpTest,"hostname").$(),
				name,
				Nest.value(httpTest,"stepscnt").$(),
				Nest.value(httpTest,"delay").$(),
				cstatus
			));
		}

		// append table to form
		httpForm.addItem(array(httpTable, Nest.value(data,"paging").$()));

		// append form to widget
		httpWidget.addItem(httpForm);

		return httpWidget;
	}

}
