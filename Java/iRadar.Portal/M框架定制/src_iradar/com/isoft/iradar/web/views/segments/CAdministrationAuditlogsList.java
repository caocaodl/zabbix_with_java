package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.inc.AuditUtil.audit_resource2str;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGOUT;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTenantGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationAuditlogsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget auditWidget = new CWidget();
		
		Map _config = select_config(idBean, executor);
		CForm frmGroup = new CForm("get");
		frmGroup.setAttribute("name", "tenantUserForm");
		CPageFilter pageFilter = new CPageFilter(idBean, executor, map(
				"tenants", map(),
				"tenantid", get_request("tenantid", null)
		));
		Nest.value(_REQUEST,"tenantid").$(pageFilter.$("tenantid").$());
		CComboBox tenantComboBox= pageFilter.getTenantsCB();
		CComboBox userComboBox = null;
		CArray<String> arraystr=new CArray<String>();
		CArray<Map> users = null;
		String userSql = " select DISTINCT u.alias,u.userid,u.name from auditlog l" +
						 " left join users u on l.userid = u.userid " +
						 " left join tenants t on u.tenantid = t.tenantid ";
		if(!Cphp.empty(Nest.value(_REQUEST,"tenantid").asString())){
			Map params = new HashMap();
			params.put("tenantid", Nest.value(_REQUEST,"tenantid").asString());
			users = DBselect(executor,userSql+
											 " where 1 = 1 " +
											 " and u.tenantid = #{tenantid} " +
											 " order by u.alias",params);
		}else{
			users = DBselect(executor,userSql+" order by u.alias");
		}
		for(Map user : users)
			arraystr.put(Nest.value(user, "userid").asString(), Nest.value(user, "name").asString());
		String userid = Nest.as(get_request("userid", 1==Nest.value(_config, "dropdown_first_remember").asInteger()?CProfile.get(idBean, executor,"web.auditlogs.filter.alias", ""):0)).asString();
		userComboBox = pageFilter.getCB("userid", userid.toString(), arraystr);
		
		tenantComboBox.addStyle("width: 100px");
		userComboBox.addStyle("width: 100px");
		frmGroup.addItem(array(_("Tenant")+SPACE, tenantComboBox,SPACE,SPACE));
		frmGroup.addItem(array(_("User")+SPACE, userComboBox));
		auditWidget.addHeader(frmGroup);
		// create filter
		CForm filterForm = new CForm("get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		CTable filterTable = new CTable("", "filter");
		

		CComboBox actionComboBox = new CComboBox("action", Nest.value(data,"action").$());
		actionComboBox.addItem(-1, _("All"));
		actionComboBox.addItem(AUDIT_ACTION_LOGIN, _("Login"));
		actionComboBox.addItem(AUDIT_ACTION_LOGOUT, _("Logout"));
		actionComboBox.addItem(AUDIT_ACTION_ADD, _("Add"));
		actionComboBox.addItem(AUDIT_ACTION_UPDATE, _("Update"));
		actionComboBox.addItem(AUDIT_ACTION_DELETE, _("Delete"));
		actionComboBox.addItem(AUDIT_ACTION_ENABLE, _("Enable"));
		actionComboBox.addItem(AUDIT_ACTION_DISABLE, _("Disable"));
		
		CArray<String> auditResources = audit_resource2str();		
		CComboBox resourceComboBox = new CComboBox("resourcetype", Nest.value(data,"resourcetype").$());
		resourceComboBox.addItems(auditResources);
		
		CButton filterButton = new CButton("filter", _("GoFilter"), "javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true);");
		filterButton.useJQueryStyle("main");
		CButton resetButton = new CButton("filter_rst", _("Reset"), "javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1); uri.setArgument(\"tenantid\", \""+Nest.value(_REQUEST,"tenantid").asString()+"\"); uri.setArgument(\"userid\",  \""+Nest.value(_REQUEST,"userid").asString()+"\"); location.href = uri.getUrl();","darkgray");
		resetButton.useJQueryStyle();
		
		filterTable.addRow(array(
			new CDiv(array(bold(_("Operations")), SPACE, actionComboBox),"device_monitor"),
			new CDiv(array(bold(_("Resource")), SPACE, resourceComboBox),"device_monitor"),
			new CDiv(array(filterButton,resetButton),"device_monitor")
		));
		filterForm.addItem(filterTable);

		auditWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor,"web.auditlogs.filter.state", 1)).asInteger());
		auditWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor,"web.auditlogs.filter.state", 1)).asInteger());

		// create form
		CForm auditForm = new CForm("get");
		auditForm.setName("auditForm");

		// create table
		CTableInfo auditTable = new CTableInfo(_("No audit entries found."));
		auditTable.setAttribute("id", "scrollPagBody");
		auditTable.setHeader(array(
			_("Time"),
			_("Audit Tenant"),
			_("User"),
			_("IP"),
			_("Resource"),
			_("Resource Name"),
			_("Audit Action"),
//			_("ID"),
			_("Details")
		));
		for(Map action : (CArray<Map>)Nest.value(data,"actions").asCArray()) {
			Object details = array();
			String resourcetype=Nest.value(action,"resourcetype").asString();
			if (isArray(Nest.value(action,"details").$())) {
				for(Map detail : (CArray<Map>)Nest.value(action,"details").asCArray()) {
					((CArray)details).add(array(Nest.value(detail,"table_name").$()+"."+Nest.value(detail,"field_name").$()+NAME_DELIMITER+Nest.value(detail,"oldvalue").$()+" => "+Nest.value(detail,"newvalue").$(), BR()));
				}
			} else {
				details = Nest.value(action,"details").$();
			}

			CCol resourcename = new CCol(Nest.value(action,"resourcename").$());
			resourcename.setAttribute("style", "white-space: normal;");
			auditTable.addRow(array(
				rda_date2str(_("d M Y H:i:s"), Nest.value(action,"clock").asLong()),
				Nest.value(action,"name").$(),
				Nest.value(action,"alias").$(),
				Feature.LOCALHOSTIPV6CA.containsValue(Nest.value(action,"ip").asString())?Feature.LOCALHOSTIPV4:Nest.value(action,"ip").asString(),
				resourcetype,
				resourcename,
				Nest.value(action,"action").$(),
//				Nest.value(action,"resourceid").asLong()==0L?"":Nest.value(action,"resourceid").$(),
				new CCol(details, "wraptext")
			));			
		}

		// append table to form
		auditForm.addItem(array(auditTable, Nest.value(data,"paging").$()));

		// append navigation bar js
		CArray objData = map(
			"id", "timeline_1",
			"domid", "events",
			"loadSBox", 0,
			"loadImage", 0,
			"loadScroll", 1,
			"dynamic", 0,
			"mainObject", 1,
			"periodFixed", CProfile.get(idBean, executor, "web.auditlogs.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		rda_add_post_js("timeControl.addObject(\"events\", "+rda_jsvalue(Nest.value(data,"timeline").$())+", "+rda_jsvalue(objData)+");");
		rda_add_post_js("timeControl.processObjects();");

		// append form to widget
		auditWidget.addItem(auditForm);

		return auditWidget;
	}

}
