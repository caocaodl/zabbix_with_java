package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.natsort;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.core.HostinventoriesAction;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CInventoryHostList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CArray groupIds = Nest.value(data, "inventoryGroupIds").asCArray();
		
		CWidget hostInventoryWidget = new CWidget();

		CForm rForm = new CForm("get");
		//rForm.addItem(array(_("Monitor category"), SPACE, ((CPageFilter) data.get("pageFilter")).getGroupsCB()));
		
		CArray<String>  ms=new CArray<String>();
		/*ms.put(IMonGroup.MON_SERVER_WINDOWS.id().toString(),IMonGroup.MON_SERVER_WINDOWS.desc());
		ms.put(IMonGroup.MON_SERVER_LINUX.id().toString(),IMonGroup.MON_SERVER_LINUX.desc());
		ms.put(IMonGroup.MON_NET_CISCO.id().toString(),IMonGroup.MON_NET_CISCO.desc());														
		ms.put(IMonGroup.MON_COMMON_NET.id().toString(),IMonGroup.MON_COMMON_NET.desc());														
		ms.put(IMonGroup.MON_STORAGE.id().toString(),IMonGroup.MON_STORAGE.desc());*/
		ms.put(IMonConsts.MON_CATE_SERVER,_("MON_CATE_SERVER"));
		ms.put(IMonConsts.MON_CATE_NET_DEV,_("MON_CATE_NET_DEV"));
		ms.put(IMonConsts.MON_CATE_STORAGE,_("MON_CATE_STORAGE"));
		
		String selmonitorcategoryid = get_request("selmonitorcategoryid");
		CComboBox msbox =((CPageFilter) data.get("pageFilter")).getCB("selmonitorcategoryid", selmonitorcategoryid, ms);
		rForm.addItem(array(SPACE+_("Monitor category")+SPACE, SPACE, msbox));
		hostInventoryWidget.addHeader(rForm);

		String filterFieldValue="";
		boolean hasresetflag = empty(Nest.value(data, "resetflag").$()) ;
		if(hasresetflag){
			filterFieldValue=Nest.value(data, "filterFieldValue").asString();
		}
		
		CTable filterTable = new CTable("", "filter");
		// getting inventory fields to make a drop down
		CArray<Map> inventoryFields = getHostInventories(true); // "true" means list should be ordered by title
		CComboBox inventoryFieldsComboBox = new CComboBox("filter_field", Nest.value(data, "filterField").$());
		for (Map inventoryField : inventoryFields) {
			String compare = Nest.value(inventoryField, "db_field").asString();
			if (compare.equals("contract_number")) {
				inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "别名");
			} else if (compare.equals("os_full")) {
				//if(hasresetflag){
					inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "设备编号");
				/*}else{
					inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "设备编号",true);
				}*/
			} else if (compare.equals("hardware")) {
				inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "所属部门");
			} else if (compare.equals("software")) {
				inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "所在机房");
			} else if (compare.equals("url_a")) {
				inventoryFieldsComboBox.addItem(Nest.value(inventoryField, "db_field").$(), "所在机柜");
			}
		}
				
		CInput temone =new CInput("hidden","selmonitorcategoryid",selmonitorcategoryid);//在检索中添加隐藏的templateid条件	
		CButton filter = new CButton("filter", _("GoFilter"), "javascript: create_var(\"rda_filter\", \"filter_set\", \"1\", true); chkbxRange.clearSelectedOnFilterChange();");
		filter.useJQueryStyle("main");
		CArray filtercarray=array(temone);
		
		//加入重置标志，用于将文本变为空
		CButton reset = new CButton("reset", _("Reset"), "javascript: var uri = new Curl(location.href); uri.setArgument(\"rda_filter\", 1); uri.setQuery(\"?resetflag=1&selmonitorcategoryid="+(empty(selmonitorcategoryid)?0:selmonitorcategoryid)+"\"); location.href = uri.getUrl();","darkgray");
		reset.useJQueryStyle();
		CComboBox exactComboBox = new CComboBox("filter_exact", Nest.value(data, "filterExact").$());
		exactComboBox.addItem("0", _("like"));
		//if(hasresetflag){
			exactComboBox.addItem("1", _("exactly"));
		/*}else{
			exactComboBox.addItem("1", _("exactly"),true);
		}*/
		filterTable.addRow(
			array(
				new CDiv(array(bold(_("Field")), SPACE, inventoryFieldsComboBox), "device_monitor"),
				new CDiv(array(exactComboBox, new CTextBox("filter_field_value", filterFieldValue, 20)), "device_monitor"), 
				new CDiv(array(filter, reset,filtercarray), "device_monitor")
			), 
			"host-inventories"
		);

		CForm filterForm = new CForm("get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addItem(filterTable);
		hostInventoryWidget.addFlicker(filterForm, Nest.as(CProfile.get(idBean, executor, "web.hostinventories.filter.state", 0)).asInteger());
		hostInventoryWidget.addHeaderRowNumber();

		CTableInfo table = new CTableInfo(_("No hosts found."));
		table.setHeader(array(make_sorting_header(_("Host"), "name"), _("Inventory Alias"), _("Monitoring Type"), _("IP Address"), _("Host Sequence"), _("Host Department"), _("Host MachineRoom"), _("Host Cabinet")));
		for (Map host : (CArray<Map>) Nest.value(data, "hosts").asCArray()) {
			String type = null; // 设备类型
			String os_full = null;
			String hardware = null;
			String software = null;
			String url_a = null;
			String contract_number = null; // 别名
			String hostInterface = null; // ip地址
			Long hostid = Nest.value(host, "hostid").asLong();
			
			Map systemType = new HashedMap();
			SystemDAO sys = new SystemDAO(executor);
			CHostGet hostGet = new CHostGet();
			hostGet.setOutput(API_OUTPUT_EXTEND);
			hostGet.setHostIds(hostid);
			hostGet.setSelectInventory(API_OUTPUT_EXTEND);
			Map hosts = API.Host(idBean, executor).get(hostGet);
			CArray data1 = Nest.value(hosts, "0").asCArray();
			CArray record = Nest.value(data1, "inventory").asCArray();
			if (!empty(record)) {
				os_full = Nest.value(record, "os_full").asString();
				hardware = Nest.value(record, "hardware").asString();
				software = Nest.value(record, "software").asString();
				url_a = Nest.value(record, "url_a").asString();
				contract_number = Nest.value(record, "contract_number").asString();
			}
			if (empty(os_full)) {
				os_full = "-";
			}
			if (empty(hardware)) {
				hardware = "-";
			} else {
				systemType.put("type", "dept");
				systemType.put("dkey", hardware);
				List<Map> all = sys.doSystem(systemType);
				if(empty(all)){
					hardware = "-";	
				}
				for (Map m : all) {
					hardware = (String) m.get("dlabel");
				}
			}
			if (empty(software)) {
				software = "-";
			} else {
				systemType.put("type", "motor_room");
				systemType.put("dkey", software);
				List<Map> all = sys.doSystem(systemType);
				if(empty(all)){
					software = "-";	
				}
				for (Map m : all) {
					software = (String) m.get("dlabel");
				}
			}
			if (empty(url_a)) {
				url_a = "-";
			} else {
				systemType.put("type", "cabinet");
				systemType.put("dkey", url_a);
				List<Map> all = sys.doSystem(systemType);
				if(empty(all)){
					url_a = "-";	
				}
				for (Map m : all) {
					url_a = (String) m.get("dlabel");
				}
			}
			if (empty(contract_number)) {
				contract_number = "-";
			}
			CHostIfaceGet inface = new CHostIfaceGet();
			inface.setOutput(API_OUTPUT_EXTEND);
			inface.setHostIds(hostid);
			Map infaceMap = API.HostInterface(idBean, executor).get(inface);
			CArray infaceMap1 = Nest.value(infaceMap, "0").asCArray();
			hostInterface = Nest.value(infaceMap1, "ip").asString();
			if (empty(hostInterface)) {
				hostInterface = "-";
			}
 
			CArray chostGroups = array();
			for (Map group : (CArray<Map>) Nest.value(host, "groups").asCArray()) {
//				if(array(HostinventoriesAction.groupids).containsValue(Nest.value(group, "groupid").asLong())) {
//					chostGroups.add(Nest.value(group, "name").$());
//				}
				if(groupIds.containsValue(Nest.value(group, "groupid").asLong())) {
					chostGroups.add(Nest.value(group, "name").$());
				}
			}
			natsort(chostGroups);
			type = implode(", ", chostGroups.valuesAsString());

			CArray row = array(
					new CLink(
						Nest.value(host, "name").$(), 
						"?hostid=" + Nest.value(host, "hostid").asString() + url_param(idBean, "groupid"), 
						(Nest.value(host, "status").asInteger() == HOST_STATUS_NOT_MONITORED) ? "not-monitored" : ""
					),
					rda_str2links(contract_number), // 别名
					rda_str2links(type), // 类型
					rda_str2links(hostInterface), // ip
					rda_str2links(os_full), // 设备编号
					rda_str2links(hardware),// 所属部门
					rda_str2links(software),// 所在机房
					rda_str2links(url_a) // 所在机柜
			);

			table.addRow(row);
		}

		CArray ctable = array(table, Nest.value(data, "paging").$());
		hostInventoryWidget.addItem(ctable);

		return hostInventoryWidget;
	}

}
