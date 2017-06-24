package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
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

public class CConfigurationApplicationList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		Map<String, Object> page = RadarContext.page();
		int module = Nest.value(page, "module").asInteger();
		
		//监控纬度 table列表样式
		String tableCls=" applications";
		CWidget applicationWidget = new CWidget();

		CForm createForm = new CForm("get");
		createForm.setName("applicationFormCreate");
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

		CToolBar tb = new CToolBar(createForm);
		/**
		 * 判断是否选中设备
		 */
		// append host summary to widget header
		if (empty(Nest.value(data,"hostid").$())) {
			CSubmit createButton = new CSubmit("form", _("Create application (select host first)"),"","orange create");
			createButton.setEnabled(false);
			createForm.addItem(createButton);
		} else {
			createForm.addItem(new CSubmit("form", _("Create application"),"","orange create"));
		}
		
		tb.addForm(createForm);
		
		// create delete buttons
		CArray goComboBox = array();
		
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected applications?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);

		rda_add_post_js("chkbxRange.pageGoName = \"applications\";");
		rda_add_post_js("chkbxRange.prefix = \""+data.get("hostid")+"\";");
		rda_add_post_js("cookie.prefix = \""+data.get("hostid")+"\";");

		
		
		// create widget header
		/**
		 * 查询条件
		 */
		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		applicationWidget.addItem(headerActions);

		// create form
		CForm applicationForm = new CForm();
		applicationForm.setName("applicationForm");
		applicationForm.addVar("groupid", Nest.value(data,"groupid").$());
		applicationForm.addVar("hostid", Nest.value(data,"hostid").$());

		// create table
		CTableInfo applicationTable = new CTableInfo(_("No applications found."));
		// add css
		String oldClass=applicationTable.getAttribute("class").toString();
		applicationTable.setAttribute("class", oldClass+tableCls);
		applicationTable.setHeader(array(
			new CCheckBox("all_applications", false, "checkAll(\""+applicationForm.getName()+"\", \"all_applications\", \"applications\");"),
			(Nest.value(data,"hostid").asInteger() > 0) ? null : _("Host"),
			make_sorting_header(_("Application"), "name"),
			_("correlation indicator")
		));

		for(Map application : (CArray<Map>)Nest.value(data,"applications").asCArray()) {
			// inherited app, display the template list
			Object name = null;
			if (!empty(Nest.value(application,"templateids").$()) && !empty(Nest.value(application,"sourceTemplates").$())) {
				name = array();
				CArrayHelper.sort(Nest.value(application,"sourceTemplates").asCArray(), array("name"));

				for(Map template : (CArray<Map>)Nest.value(application,"sourceTemplates").asCArray()) {
					((CArray)name).add(new CLink(Nest.value(template,"name").$(), "policy_apps.action?hostid="+Nest.value(template,"hostid").$(), "unknown"));
					((CArray)name).add(", ");
				}
				array_pop(((CArray)name));
				((CArray)name).add(NAME_DELIMITER);
				((CArray)name).add(Nest.value(application,"name").$());
			} else {
				name = new CLink(
					Nest.value(application,"name").$(),
					"monitor_apps.action?"+
						"form=update"+
						"&applicationid="+application.get("applicationid")+
						"&hostid="+application.get("hostid")+
						"&groupid="+data.get("groupid")
				);
			}
			
			String url = "'"+_("Items")+"', '"+RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX+(module==IMonModule.policy.ordinal()?"policy":"monitor")+"_items.action?"+
					"hostid="+application.get("hostid")+
					"&filter_set=1"+
					"&filter_application="+urlencode(Nest.value(application,"name").asString())+"'";
			applicationTable.addRow(array(
				new CCheckBox("applications["+application.get("applicationid")+"]", false, null, Nest.value(application,"applicationid").asInteger()),
				(Nest.value(data,"hostid").asInteger() > 0) ? null : Nest.value(application,"host").$(),
				name,
				array(
					new CLink(_("Items"),IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE),
					SPACE+"("+count(Nest.value(application,"items").asCArray())+")"
				)
			));
		}

		// append form to widget
		applicationWidget.addItem(applicationForm);

		// append table to form
		applicationForm.addItem(array(applicationTable, Nest.value(data,"paging").$()));
				
		
		return applicationWidget;
	}

}
