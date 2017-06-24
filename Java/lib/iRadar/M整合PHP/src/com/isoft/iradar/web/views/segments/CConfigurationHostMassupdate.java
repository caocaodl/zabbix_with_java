package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_AUTOMATIC;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_MANUAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.HostsUtil.ipmiAuthTypes;
import static com.isoft.iradar.inc.HostsUtil.ipmiPrivileges;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVisibilityBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostMassupdate extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.host.edit.js");

		// create form
		CForm hostForm = new CForm();
		hostForm.setName("hostForm");
		hostForm.addVar("go", "massupdate");
		for(String hostid : (CArray<String>)Nest.value(data,"hosts").asCArray()) {
			hostForm.addVar("hosts["+hostid+"]", hostid);
		}

		// create form list
		CFormList hostFormList = new CFormList("hostFormList");

		// replace host groups
		CArray<Map> hostGroupsToReplace = null;
		if (isset(_REQUEST,"groups")) {
			hostGroupsToReplace = array();
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(Nest.array(_REQUEST,"groups").asLong());
			hgoptions.setOutput(new String[]{"groupid", "name"});
			hgoptions.setEditable(true);
			CArray<Map> getHostGroups = API.HostGroup(idBean, executor).get(hgoptions);
			for(Map getHostGroup : getHostGroups) {
				hostGroupsToReplace.add(map(
					"id", Nest.value(getHostGroup,"groupid").$(),
					"name", Nest.value(getHostGroup,"name").$()
				));
			}
		}

		CDiv replaceGroups = new CDiv(new CMultiSelect(map(
			"name", "groups[]",
			"objectName", "hostGroup",
			"objectOptions", map("editable", true),
			"data", hostGroupsToReplace,
			"popup", map(
				"parameters", "srctbl=host_groups&dstfrm="+hostForm.getName()+"&dstfld1=groups_&srcfld1=groupid"+
					"&writeonly=1&multiselect=1",
				"width", 450,
				"height", 450
			)
		)), null, "replaceGroups");

		hostFormList.addRow(
			array(
				_("Replace host groups"),
				SPACE,
				CVisibilityBox.instance("visible[groups]", isset(Nest.value(data,"visible","groups").$()), "replaceGroups", _("Original"))
			),
			replaceGroups
		);

		// add new or existing host groups
		CArray<Map> hostGroupsToAdd = null;
		if (isset(_REQUEST,"new_groups")) {
			hostGroupsToAdd = array();
			CArray hostGroupIds = array();
			for(Object newHostGroup : Nest.value(_REQUEST,"new_groups").asCArray()) {
				if (newHostGroup instanceof Map && isset((Map)newHostGroup,"new")) {
					hostGroupsToAdd.add(map(
						"id", Nest.value((Map)newHostGroup,"new").$(),
						"name", Nest.value((Map)newHostGroup,"new").asString()+" ("+_x("new", "new element in multiselect")+")",
						"isNew", true
					));
				} else {
					hostGroupIds.add(newHostGroup);
				}
			}

			if (!empty(hostGroupIds)) {
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setGroupIds(hostGroupIds.valuesAsLong());
				hgoptions.setOutput(new String[]{"groupid", "name"});
				CArray<Map> getHostGroups = API.HostGroup(idBean, executor).get(hgoptions);
				for(Map getHostGroup : getHostGroups) {
					hostGroupsToAdd.add(map(
						"id", Nest.value(getHostGroup,"groupid").$(),
						"name", Nest.value(getHostGroup,"name").$()
					));
				}
			}
		}
		Object newGroups = null;
		if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
			newGroups = new CDiv(new CMultiSelect(map(
				"name", "new_groups[]",
				"objectName", "hostGroup",
				"objectOptions", map("editable", true),
				"data", hostGroupsToAdd,
				"addNew", true,
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+hostForm.getName()+"&dstfld1=new_groups_&srcfld1=groupid"+
						"&writeonly=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			)), null, "newGroups");

			hostFormList.addRow(
				array(
					_("Add new or existing host groups"),
					SPACE,
					CVisibilityBox.instance("visible[new_groups]", isset(Nest.value(data,"visible","new_groups").$()), "newGroups", _("Original"))
				),
				newGroups
			);
		} else {
			newGroups = new CMultiSelect(map(
				"name", "new_groups[]",
				"objectName", "hostGroup",
				"objectOptions", map("editable", true),
				"data", hostGroupsToAdd,
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+hostForm.getName()+"&dstfld1=new_groups_&srcfld1=groupid"+
						"&writeonly=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			));

			hostFormList.addRow(
				array(
					_("New host group"),
					SPACE,
					CVisibilityBox.instance("visible[new_groups]", isset(Nest.value(data,"visible","new_groups").$()), "new_groups_", _("Original"))
				),
				newGroups
			);
		}

		// append proxy to form list
		CComboBox proxyComboBox = new CComboBox("proxy_hostid", Nest.value(data,"proxy_hostid").$());
		proxyComboBox.addItem(0, _("(no proxy)"));
		for(Map proxie : (CArray<Map>)Nest.value(data,"proxies").asCArray()) {
			proxyComboBox.addItem(Nest.value(proxie,"hostid").$(), Nest.value(proxie,"host").asString());
		}
		hostFormList.addRow(
			array(
				_("Monitored by proxy"),
				SPACE,
				CVisibilityBox.instance("visible[proxy_hostid]", isset(Nest.value(data,"visible","proxy_hostid").$()), "proxy_hostid", _("Original"))
			),
			proxyComboBox
		);

		// append status to form list
		CComboBox statusComboBox = new CComboBox("status", Nest.value(data,"status").$());
		statusComboBox.addItem(HOST_STATUS_MONITORED, _("Monitored"));
		statusComboBox.addItem(HOST_STATUS_NOT_MONITORED, _("Not monitored"));
		hostFormList.addRow(
			array(
				_("Status"),
				SPACE,
				CVisibilityBox.instance("visible[status]", isset(Nest.value(data,"visible","status").$()), "status", _("Original"))
			),
			statusComboBox
		);

		CFormList templatesFormList = new CFormList("templatesFormList");

		CDiv templatesDiv = new CDiv(
			array(
				new CMultiSelect(map(
					"name", "templates[]",
					"objectName", "templates",
					"data", Nest.value(data,"linkedTemplates").$(),
					"popup", map(
						"parameters", "srctbl=templates&srcfld1=hostid&srcfld2=host&dstfrm="+hostForm.getName()+
							"&dstfld1=templates_&templated_hosts=1&multiselect=1",
						"width", 450,
						"height", 450
					)
				)),
				new CDiv(array(
					new CCheckBox("mass_replace_tpls", Nest.value(data,"mass_replace_tpls").asBoolean()),
					SPACE,
					_("Replace"),
					BR(),
					new CCheckBox("mass_clear_tpls", Nest.value(data,"mass_clear_tpls").asBoolean()),
					SPACE,
					_("Clear when unlinking")
				), "floatleft")
			),
			"objectgroup inlineblock border_dotted ui-corner-all"
		);
		templatesDiv.setAttribute("id", "templateDiv");

		templatesFormList.addRow(
			array(
				_("Link templates"),
				SPACE,
				CVisibilityBox.instance("visible[templates]", isset(Nest.value(data,"visible","templates").$()), "templateDiv", _("Original"))
			),
			templatesDiv
		);

		CFormList ipmiFormList = new CFormList("ipmiFormList");
		// append ipmi to form list
		CComboBox ipmiAuthtypeComboBox = new CComboBox("ipmi_authtype", Nest.value(data,"ipmi_authtype").$());
		ipmiAuthtypeComboBox.addItems(ipmiAuthTypes());
		ipmiFormList.addRow(
			array(
				_("IPMI authentication algorithm"),
				SPACE,
				CVisibilityBox.instance("visible[ipmi_authtype]", isset(Nest.value(data,"visible","ipmi_authtype").$()), "ipmi_authtype", _("Original"))
			),
			ipmiAuthtypeComboBox
		);

		CComboBox ipmiPrivilegeComboBox = new CComboBox("ipmi_privilege", Nest.value(data,"ipmi_privilege").$());
		ipmiPrivilegeComboBox.addItems(ipmiPrivileges());
		ipmiFormList.addRow(
			array(
				_("IPMI privilege level"),
				SPACE,
				CVisibilityBox.instance("visible[ipmi_privilege]", isset(Nest.value(data,"visible","ipmi_privilege").$()), "ipmi_privilege", _("Original"))
			),
			ipmiPrivilegeComboBox
		);

		ipmiFormList.addRow(
			array(
				_("IPMI username"),
				SPACE,
				CVisibilityBox.instance("visible[ipmi_username]", isset(Nest.value(data,"visible","ipmi_username").$()), "ipmi_username", _("Original"))
			),
			new CTextBox("ipmi_username", Nest.value(data,"ipmi_username").asString(), RDA_TEXTBOX_SMALL_SIZE)
		);

		ipmiFormList.addRow(
			array(
				_("IPMI password"),
				SPACE,
				CVisibilityBox.instance("visible[ipmi_password]", isset(Nest.value(data,"visible","ipmi_password").$()), "ipmi_password", _("Original"))
			),
			new CTextBox("ipmi_password", Nest.value(data,"ipmi_password").asString(), RDA_TEXTBOX_SMALL_SIZE)
		);

		CFormList inventoryFormList = new CFormList("inventoryFormList");
		// append inventories to form list
		CComboBox inventoryModesComboBox = new CComboBox("inventory_mode", Nest.value(data,"inventory_mode").$(), "submit()");
		inventoryModesComboBox.addItem(HOST_INVENTORY_DISABLED, _("Disabled"));
		inventoryModesComboBox.addItem(HOST_INVENTORY_MANUAL, _("Manual"));
		inventoryModesComboBox.addItem(HOST_INVENTORY_AUTOMATIC, _("Automatic"));
		inventoryFormList.addRow(
			array(
				_("Inventory mode"),
				SPACE,
				CVisibilityBox.instance("visible[inventory_mode]", isset(Nest.value(data,"visible","inventory_mode").$()), "inventory_mode", _("Original"))
			),
			inventoryModesComboBox
		);

		Map<String, Object> hostInventoryTable = CDB.getSchema("host_inventory");
		if (Nest.value(data,"inventory_mode").asInteger() != HOST_INVENTORY_DISABLED) {
			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"inventories").asCArray()).entrySet()) {
			    Object field = e.getKey();
			    Map fieldInfo = e.getValue();
				if (!isset(Nest.value(data,"host_inventory",field).$())) {
					Nest.value(data,"host_inventory",field).$("");
				}

				Object fieldInput=null;
				if (CDB.FIELD_TYPE_TEXT.equals(Nest.value(hostInventoryTable,"fields",field,"type").asString())) {
					fieldInput = new CTextArea("host_inventory["+field+"]", Nest.value(data,"host_inventory",field).asString());
					((CTextArea)fieldInput).addStyle("width: 64em;");
				} else {
					Integer fieldLength = Nest.value(hostInventoryTable,"fields",field,"length").asInteger();
					fieldInput = new CTextBox("host_inventory["+field+"]", Nest.value(data,"host_inventory",field).asString());
					((CTextBox)fieldInput).setAttribute("maxlength", fieldLength);
					((CTextBox)fieldInput).addStyle("width: "+(fieldLength > 64 ? 64 : fieldLength)+"em;");
				}

				inventoryFormList.addRow(
					array(
						Nest.value(fieldInfo,"title").$(),
						SPACE,
						CVisibilityBox.instance(
							"visible["+field+"]",
							isset(Nest.value(data,"visible",field).$()),
							"host_inventory["+field+"]",
							_("Original")
						)
					),
					fieldInput
				);
			}
		}

		// append tabs to form
		CTabView hostTab = new CTabView();
		// reset the tab when opening the form for the first time
		if (!hasRequest("masssave")) {
			hostTab.setSelected("0");
		}
		hostTab.addTab("hostTab", _("Host"), hostFormList);
		hostTab.addTab("templatesTab", _("Templates"), templatesFormList);
		hostTab.addTab("ipmiTab", _("IPMI"), ipmiFormList);
		hostTab.addTab("inventoryTab", _("Inventory"), inventoryFormList);
		hostForm.addItem(hostTab);

		// append buttons to form
		hostForm.addItem(makeFormFooter(new CSubmit("masssave", _("Update")), new CButtonCancel(url_param(idBean, "groupid"))));

		return hostForm;
	}

}
