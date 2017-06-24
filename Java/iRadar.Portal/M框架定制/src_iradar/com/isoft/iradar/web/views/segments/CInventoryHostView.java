package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CInventoryHostView extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget(null, "inventory-host");
		CForm hostInventoriesForm = new CForm();
		RadarContext.getContext().getRequest().setAttribute("data", data);
		includeSubView("js/configuration.inventory.js");
		/* Overview tab */
		CFormList overviewFormList = new CFormList();

		overviewFormList.addRow(_("Host name"), Nest.value(data,"host","name").$());

		if (!Nest.value(data,"host","host").$().equals(Nest.value(data,"host","name").$())) {
		//	overviewFormList.addRow(_("Visible name"), new CSpan(Nest.value(data,"host","name").$(), "text-field"));
		}

		CArray<CRow> agentInterfaceRows = array();
		CArray<CRow> snmpInterfaceRows = array();
		CArray<CRow> ipmiInterfaceRows = array();
		CArray<CRow> jmxInterfaceRows = array();

		for(Map iface : (CArray<Map>)Nest.value(data,"host","interfaces").asCArray()) {
			String spanClass = Nest.value(iface,"main").asBoolean() ? " default_interface" : null;

			switch (Nest.value(iface,"type").asInteger()) {
				case INTERFACE_TYPE_AGENT:
					agentInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass),
						new CDiv(_("Agent interfaces"), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_SNMP:
					snmpInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass),
						new CDiv(_("SNMP interfaces"), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_IPMI:
					ipmiInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;

				case INTERFACE_TYPE_JMX:
					jmxInterfaceRows.add(new CRow(array(
						new CDiv(Nest.value(iface,"ip").$(), "ip"+spanClass),
						new CDiv(Nest.value(iface,"dns").$(), "dns"+spanClass),
						new CDiv(Nest.value(iface,"useip").asInteger() == 1 ? _("IP") : _("DNS"), "useip"+spanClass),
						new CDiv(Nest.value(iface,"port").$(), "port"+spanClass)
					)));
					break;
			}
		}

		boolean interfaceTableHeaderSet = false;
		CArray interfaceRowArray = array();
		// Agent interface
		if (!empty(agentInterfaceRows)) {
			CTable agentInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup element-row-first interfaces");
			agentInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port"), _("Interface Type")));
			interfaceTableHeaderSet = true;

			for(CRow iface : agentInterfaceRows) {
				agentInterfacesTable.addRow(iface);
			}

			CRow row = new CRow();
//			CCol interfaceCol = new CCol("","interfaceClass");
			CCol inter = new CCol(new CDiv(agentInterfacesTable));
//			row.addItem(array(interfaceCol,inter));
			row.addItem(inter);
//			overviewFormList.addRow(_("Agent interfaces"),row,false,null,"interfaceClass");
//			overviewFormList.addRow(row);
			interfaceRowArray.add(row);
			
//			overviewFormList.addRow(
//				_("Agent interfaces"),
//				new CDiv(agentInterfacesTable)
//			);
		}


		// SNMP interface
		if (!empty(snmpInterfaceRows)) {
			CTable snmpInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				snmpInterfacesTable.addClass("element-row");
			} else {
				snmpInterfacesTable.addClass("element-row-first");
				snmpInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port"), _("Interface Type")));
				interfaceTableHeaderSet = true;
			}

			for(CRow iface : snmpInterfaceRows) {
				snmpInterfacesTable.addRow(iface);
			}


			
//			overviewFormList.addRow(
////					_("SNMP interfaces"),
//					new CDiv(snmpInterfacesTable)
//				);
			interfaceRowArray.add(new CDiv(snmpInterfacesTable));
		}

		// JMX interface
		if (!empty(jmxInterfaceRows)) {
			CTable jmxInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				jmxInterfacesTable.addClass("element-row");
			} else {
				jmxInterfacesTable.addClass("element-row-first");
				jmxInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
			}

			for(CRow iface : jmxInterfaceRows) {
				jmxInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("JMX interfaces"),
				new CDiv(jmxInterfacesTable)
			);
		}

		// IPMI interface
		if (!empty(ipmiInterfaceRows)) {
			CTable ipmiInterfacesTable = new CTable(null, "formElementTable border_dotted objectgroup interfaces");
			if (interfaceTableHeaderSet) {
				ipmiInterfacesTable.addClass("element-row");
			} else {
				ipmiInterfacesTable.addClass("element-row-first");
				ipmiInterfacesTable.setHeader(array(_("IP address"), _("DNS name"), _("Connect to"), _("Port")));
				interfaceTableHeaderSet = true;
			}

			for(CRow iface : ipmiInterfaceRows) {
				ipmiInterfacesTable.addRow(iface);
			}

			overviewFormList.addRow(
				_("IPMI interfaces"),
				new CDiv(ipmiInterfacesTable)
			);
		}
		overviewFormList.addRow(_("Host interfaces"),interfaceRowArray);
		Map systemType = new HashedMap();
		SystemDAO sys = new SystemDAO(executor);
		systemType.put("type", "system");
		List<Map> all = sys.doSystem(systemType);
		systemType.put("type", "dept");
		List<Map> dept = sys.doSystem(systemType);
		systemType.put("type", "firm");
		List<Map> firm = sys.doSystem(systemType);
		systemType.put("type", "motor_room");
		List<Map> motor_room = sys.doSystem(systemType);
		List<Map> cabine = new ArrayList<Map>();
		CArray inventory = Nest.value(data, "host", "inventory").asCArray();
		CComboBox type_full = new CComboBox("type_full", Nest.value(inventory, "type_full").asString());
		type_full.addStyle("width:550px;");
		type_full.addStyle("max-width:550px;");
		
		IsContainsSameKey(Nest.value(inventory, "type_full").asString(),all,type_full);//如果为空或已删除，设置为请选择提示
		for (Map m : all) {
			type_full.addItem(m.get("dkey"), (String) m.get("dlabel"));
		}
		CComboBox hardware = new CComboBox("hardware", Nest.value(inventory, "hardware").asString());
		hardware.addStyle("width:550px;");
		hardware.addStyle("max-width:550px;");
		
		IsContainsSameKey(Nest.value(inventory, "hardware").asString(),dept,hardware);//如果为空或已删除，设置为请选择提示
		for (Map m : dept) {
			hardware.addItem(m.get("dkey"), (String) m.get("dlabel"));
		}

		String roomNumber = null;
		CComboBox software = new CComboBox("software", Nest.value(inventory, "software").asString());
		software.addStyle("width:550px;");
		software.addStyle("max-width:550px;");
		software.setAttribute("onchange", "javascript:selected(this.options[this.selectedIndex].value);");
		
		IsContainsSameKey(Nest.value(inventory, "software").asString(),motor_room,software);//如果为空或已删除，设置为请选择提示
		for (Map m : motor_room) {
			software.addItem(m.get("dkey"), (String) m.get("dlabel"));
		}
//		CComboBox url_a = new CComboBox("url_a", Nest.value(inventory, "url_a").asString());// 机柜下拉框
		CComboBox url_a = new CComboBox("url_a");
		url_a.addStyle("width:550px;");
		url_a.addStyle("max-width:550px;");
		url_a.attr("id", "cabinet");
		String cabinet = Nest.value(inventory, "url_a").asString();
		IsContainsSameKey(Nest.value(inventory, "url_a").asString(),cabine,url_a);//如果为空或已删除，设置为请选择提示
		if (empty(cabinet)) {
			if (motor_room.size() > 0) {
				/*roomNumber = motor_room.get(0).get("dkey").toString();
				systemType.put("type", "cabinet");
				systemType.put("seq_no", roomNumber);
				cabine = sys.doSystem(systemType);
				
				
				for (Map m : cabine) {
					url_a.addItem(m.get("dkey"), (String) m.get("dlabel"));
				}*/
				if(!empty(Nest.value(inventory, "software").asString())){
					roomNumber = Nest.value(inventory, "software").asString();
					systemType.put("type", "cabinet");
					systemType.put("seq_no", roomNumber);
					cabine = sys.doSystem(systemType);
					for (Map m : cabine) {
						url_a.addItem(m.get("dkey"), (String) m.get("dlabel"));
					}
				}
			}
		} else {
			roomNumber = Nest.value(inventory, "software").asString();
			systemType.put("type", "cabinet");
			systemType.put("seq_no", roomNumber);
			cabine = sys.doSystem(systemType);
			
//			IsContainsSameKey(Nest.value(inventory, "url_a").asString(),cabine,url_a);//如果为空或已删除，设置为请选择提示
			for (Map m : cabine) {
				if(cabinet.equals(Nest.value(m, "dkey").asString()))
					url_a.addItem(m.get("dkey"), (String) m.get("dlabel"),true);
				else
					url_a.addItem(m.get("dkey"), (String) m.get("dlabel"));
			}
		}
		CComboBox vendor = new CComboBox("vendor", Nest.value(inventory, "vendor").asString());
		vendor.addStyle("width:550px;");
		vendor.addStyle("max-width:550px;");
		
		IsContainsSameKey(Nest.value(inventory, "vendor").asString(),firm,vendor);//如果为空或已删除，设置为请选择提示
		for (Map m : firm) {
			vendor.addItem(m.get("dkey"), (String) m.get("dlabel"));
		}
		CTextBox ID = new CTextBox("hostid", Nest.value(data, "hostid").asString());
		ID.setType("hidden");
		overviewFormList.addRow("", ID);
		CTextBox inventory_mode = new CTextBox("inventory_mode", Nest.value(data, "inventory_mode").asString());
		inventory_mode.setType("hidden");
		overviewFormList.addRow("", inventory_mode);
		CTextBox visiblenam = new CTextBox("contract_number", Nest.value(inventory, "contract_number").asString());
		visiblenam.addStyle("width:550px;");
		visiblenam.setAttribute("maxlength", 60);
		overviewFormList.addRow((_("Host Other Name")), visiblenam);
		CTextBox os_full = new CTextBox("os_full", Nest.value(inventory, "os_full").asString());
		os_full.addStyle("width:550px;");
		os_full.setAttribute("maxlength", 50);
		overviewFormList.addRow((_("ID")), os_full);
		String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
		String url= "'"+_("DEPT")+"', '"+common_action_with_context+"cbn.action?actionType=DEPT"+"'";
		overviewFormList.addRow((_("Belong Paent")), array(hardware, new CLink(_("DEPT"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"",null,Boolean.TRUE)));
		url= "'"+_("mRoom")+"', '"+common_action_with_context+"cbn.action?actionType=mRoom"+"'";
		overviewFormList.addRow((_("Belong Room")), array(software, new CLink(_("mRoom"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"",null,Boolean.TRUE)));
		url= "'"+_("Cabinet")+"', '"+common_action_with_context+"cbn.action?actionType=Cabinet"+"'";
		overviewFormList.addRow((_("Belong Cabin")), array(url_a, new CLink(_("Cabinet"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"",null,Boolean.TRUE)));
		url= "'"+_("otsys")+"', '"+common_action_with_context+"adm.operationsystem.action?actionType=otsys"+"'";
		overviewFormList.addRow((_("Belong Windows")), array(type_full, new CLink(_("otsys"), IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"",null,Boolean.TRUE)));
		url= "'"+_("FIRM")+"', '"+common_action_with_context+"cbn.action?actionType=firm"+"'";
		overviewFormList.addRow((_("Belong company")), array(vendor, new CLink(_("FIRM"),IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"",null,Boolean.TRUE)));
		CTextArea host_networks = new CTextArea("host_networks", Nest.value(inventory, "host_networks").asString());
		host_networks.addStyle("width:550px;");
		host_networks.setMaxlength(255);
		overviewFormList.addRow((_("Note")), host_networks);
		CTabView hostInventoriesTab = new CTabView(map("remember", true));
		hostInventoriesTab.setSelected("0");

		hostInventoriesTab.addTab("overviewTab", _("Overview"), overviewFormList);

		/*
		 * Details tab
		 */
		CFormList detailsFormList = new CFormList();

		boolean inventoryValues = false;
		if (!empty(Nest.value(data,"host","inventory").$())) {
			for (Entry<Object,String> e : ((CArray<String>)Nest.value(data,"host","inventory").asCArray()).entrySet()) {
			    Object key = e.getKey();
			    String value = e.getValue();
				if (!rda_empty(value)) {
					detailsFormList.addRow(
						Nest.value(data,"tableTitles",key,"title").$(),
						new CSpan(rda_str2links(value),
						"text-field"));
					inventoryValues = true;
				}
			}
		}

		if (!inventoryValues) {
			hostInventoriesTab.setDisabled(array(1));
		}

	//	hostInventoriesTab.addTab("detailsTab", _("Details"), detailsFormList);

		// append tabs and form
		hostInventoriesForm.addItem(hostInventoriesTab);
//		hostInventoriesForm.addItem(makeFormFooter(
//			null,
//			new CButtonCancel(url_param("groupid"))
//		));
		/* footer */
		CArray others = array();
		// do not display the clone and delete buttons for clone forms and new host forms
		if (!empty(get_request("hostid")) && !in_array(get_request("form"), array("clone", "full_clone"))) {
//			others.add(new CSubmit("clone", _("Clone")));
//			others.add(new CSubmit("full_clone", _("Full clone")));
//			others.add(new CButtonDelete(_("Delete selected host?"), url_param("form")+url_param("hostid")+url_param("groupid")));
		}
		others.add(new CButtonCancel(url_param(idBean, "groupid")));

		hostInventoriesForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), others));
		hostInventoryWidget.addItem(hostInventoriesForm);

		return hostInventoryWidget;
	}
	
	/** 用于判断已经删除的部门、系统等是否还存在
	 * @param key
	 * @param keymap
	  * @param typebox
	 * @return
	 */
	private void IsContainsSameKey(String key,List<Map> keymaps,CComboBox typebox){
		/**
		 * 无论是否已经选择，都添加	请选择 选项
		 */
//		if(empty(key)){
			typebox.addItem("", _("not selected"),false,true,"sel_font_type");
//		}else{
//			boolean ishas=false;
//			for(Map keymap:keymaps){
//				if(key.equals(keymap.get("dkey"))){
//					ishas= true;
//					break;
//				}
//			}
//			if(!ishas){
//				typebox.addItem("", _("not selected"),false,true,"sel_font_type");
//			}
//		}
		
	}
}
