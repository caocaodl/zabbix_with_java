package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_EXPIRED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.List;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationMaintenanceList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();
		
		// create form
		CForm maintenanceForm = new CForm();
		maintenanceForm.setName("maintenanceForm");
		
		CToolBar tb = new CToolBar(maintenanceForm);
		tb.addSubmit("form",_("Create maintenance period"), "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected maintenance periods?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"maintenanceids\";");
		
		// header
		/*CForm filterForm = new CForm("get");
		
		CComboBox group= ((CPageFilter) data.get("pageFilter")).getGroupsCB();
		List list =group.items;
		int k=list.size();
		for(int i=0;i<k;i++){
			if(list.get(i).toString().contains("Discovered hosts")||list.get(i).toString().contains("Templates")){
				list.remove(i);
				k--;
				i--;
			}
			
		}*/
		/*filterForm.addItem(array(_("Group")+SPACE, group));
		maintenanceWidget.addHeader(filterForm);*/
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		maintenanceWidget.addItem(headerActions);

		// create table
		CTableInfo maintenanceTable = new CTableInfo(_("No maintenance periods found."));
		maintenanceTable.setHeader(array(
			new CCheckBox("all_maintenances", false, "checkAll(\""+maintenanceForm.getName()+"\", \"all_maintenances\", \"maintenanceids\");"),
			make_sorting_header("维护计划名称", "name"),
			make_sorting_header(_("Type"), "maintenance_type"),
			"下次维护时间",
			_("Status"),
			//_("Description"),
			"维护数目"
		));

		for(Map maintenance :(CArray<Map>)Nest.value(data,"maintenances").asCArray()) {
			Integer maintenanceid = Nest.value(maintenance,"maintenanceid").asInteger();

			CSpan maintenanceStatus = null;
			switch (Nest.value(maintenance,"status").asInteger()) {
				case MAINTENANCE_STATUS_EXPIRED:
					maintenanceStatus  = new CSpan(_x("Expired", "maintenance status"), "red");
					break;
				case MAINTENANCE_STATUS_APPROACH:
					maintenanceStatus = new CSpan(_x("Approaching", "maintenance status"), "blue");
					break;
				case MAINTENANCE_STATUS_ACTIVE:
					maintenanceStatus = new CSpan(_x("Active", "maintenance status"), "green");
					break;
			}

			String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
			String url= "'"+_("hosts configuration")+"', '"+common_action_with_context+"hostMonitor.action?selMaintenanceId="+maintenanceid+"'";
			maintenanceTable.addRow(array(
				new CCheckBox("maintenanceids["+maintenanceid+"]", false, null, maintenanceid),
				new CLink(Nest.value(maintenance,"name").$(), "maintenance.action?form=update&maintenanceid="+maintenanceid),
				!empty(Nest.value(maintenance,"maintenance_type").$()) ? _("No data collection") : _("With data collection"),
				rda_date2str(_("d M Y H:i:s"), Nest.value(maintenance, "nextMaintenanceTime").asLong()),
				maintenanceStatus,
				//Nest.value(maintenance,"description").$(),
				new CLink("设备("+Nest.value(maintenance, "hostNum").$()+")",
						IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE)
			));
		}
		// append table to form
		maintenanceForm.addItem(array(maintenanceTable, Nest.value(data,"paging").$()));

		// append form to widget
		maintenanceWidget.addItem(maintenanceForm);
		return maintenanceWidget;
	}

}
