package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_INTERVAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ItemsUtil.interfaceType2str;
import static com.isoft.iradar.inc.ItemsUtil.item_data_type2str;
import static com.isoft.iradar.inc.ItemsUtil.item_status2str;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.COptGroup;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVisibilityBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationItemMassupdate extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget itemWidget = new CWidget();

		if (!empty(Nest.value(data,"hostid").$())) {
			itemWidget.addItem(get_header_host_table(idBean, executor, "items", Nest.value(data,"hostid").asLong(true)));
		}

		itemWidget.addPageHeader(_("CONFIGURATION OF ITEMS"));

		// create form
		CForm itemForm = new CForm();
		itemForm.setName("itemForm");
		itemForm.addVar("massupdate", 1);
		itemForm.addVar("group_itemid", Nest.value(data,"itemids").$());
		itemForm.addVar("hostid", Nest.value(data,"hostid").$());

		// create form list
		CFormList itemFormList = new CFormList("itemFormList");

		// append type to form list
		CComboBox typeComboBox = new CComboBox("type", Nest.value(data,"type").$());
		typeComboBox.addItems(Nest.value(data,"itemTypes").asCArray());
		itemFormList.addRow(
			array(
				_("Type"),
				SPACE,
				CVisibilityBox.instance("visible[type]", isset(Nest.value(data,"visible","type").$()), "type", _("Original"))
			),
			typeComboBox
		);

		// append hosts to form list
		if (!empty(Nest.value(data,"displayInterfaces").$())) {
			CComboBox interfacesComboBox = new CComboBox("interfaceid", Nest.value(data,"interfaceid").$());
			interfacesComboBox.addItem(new CComboItem(0, "", false, false));

			// set up interface groups
			CArray<COptGroup> interfaceGroups = array();
			for(Integer interfaceType : (CArray<Integer>)rda_objectValues(Nest.value(data,"hosts","interfaces").$(), "type")) {
				Nest.value(interfaceGroups,interfaceType).$(new COptGroup(interfaceType2str(interfaceType)));
			}

			// add interfaces to groups
			for(Map iface : (CArray<Map>)Nest.value(data,"hosts","interfaces").asCArray()) {
				CComboItem option = new CComboItem(
					Nest.value(iface,"interfaceid").$(),
					Nest.value(iface,"useip").asBoolean() ? Nest.value(iface,"ip").$()+" : "+Nest.value(iface,"port").$() : Nest.value(iface,"dns").$()+" : "+Nest.value(iface,"port").$(),
					Nest.value(iface,"interfaceid").asInteger() == Nest.value(data,"interfaceid").asInteger() ? true : false
				);
				option.setAttribute("data-interfacetype", Nest.value(iface,"type").$());
				interfaceGroups.get(Nest.value(iface,"type").$()).addItem(option);
			}
			for(COptGroup interfaceGroup : interfaceGroups) {
				interfacesComboBox.addItem(interfaceGroup);
			}

			CSpan span = new CSpan(_("No interface found"), "red");
			span.setAttribute("id", "interface_not_defined");
			span.setAttribute("style", "display: none;");

			CVisibilityBox interfaceVisBox = CVisibilityBox.instance("visible[interface]", isset(Nest.value(data,"visible","interface").$()), "interfaceDiv", _("Original"));
			interfaceVisBox.setAttribute("data-multiple-interface-types", Nest.value(data,"multiple_interface_types").$());
			itemFormList.addRow(
				array(_("Host interface"), SPACE, interfaceVisBox),
				new CDiv(array(interfacesComboBox, span), null, "interfaceDiv"),
				false,
				"interface_row"
			);
			itemForm.addVar("selectedInterfaceId", Nest.value(data,"interfaceid").$());
		}

		// append snmp community to form list
		itemFormList.addRow(
			array(
				_("SNMP community"),
				SPACE,
				CVisibilityBox.instance("visible[community]", isset(Nest.value(data,"visible","community").$()), "snmp_community", _("Original"))
			),
			new CTextBox("snmp_community", Nest.value(data,"snmp_community").asString(), RDA_TEXTBOX_SMALL_SIZE)
		);

		// append snmpv3 contextname to form list
		itemFormList.addRow(
			array(
				_("Context name"),
				SPACE,
				CVisibilityBox.instance("visible[contextname]", isset(Nest.value(data,"visible","contextname").$()), "snmpv3_contextname", _("Original"))
			),
			new CTextBox("snmpv3_contextname", Nest.value(data,"snmpv3_contextname").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append snmpv3 securityname to form list
		itemFormList.addRow(
			array(
				_("Security name"),
				SPACE,
				CVisibilityBox.instance("visible[securityname]", isset(Nest.value(data,"visible","securityname").$()), "snmpv3_securityname", _("Original"))
			),
			new CTextBox("snmpv3_securityname", Nest.value(data,"snmpv3_securityname").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append snmpv3 securitylevel to form list
		CComboBox securityLevelComboBox = new CComboBox("snmpv3_securitylevel", Nest.value(data,"snmpv3_securitylevel").$());
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV, "noAuthNoPriv");
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "authNoPriv");
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "authPriv");
		itemFormList.addRow(
			array(
				_("Security level"),
				SPACE,
				CVisibilityBox.instance("visible[securitylevel]", isset(Nest.value(data,"visible","securitylevel").$()), "snmpv3_securitylevel", _("Original"))
			),
			securityLevelComboBox
		);

		// append snmpv3 authprotocol to form list
		CDiv authProtocol = new CDiv(
			array(
				new CRadioButton("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_MD5, null, "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_MD5, Nest.value(data,"snmpv3_authprotocol").asInteger() == ITEM_AUTHPROTOCOL_MD5),
				new CLabel(_("MD5"), "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_MD5),
				new CRadioButton("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_SHA, null, "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_SHA, Nest.value(data,"snmpv3_authprotocol").asInteger() == ITEM_AUTHPROTOCOL_SHA),
				new CLabel(_("SHA"), "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_SHA)
			),
			"jqueryinputset",
			"authprotocol_div"
		);
		itemFormList.addRow(
			array(
				_("Authentication protocol"),
				SPACE,
				CVisibilityBox.instance("visible[authprotocol]", isset(Nest.value(data,"visible","authprotocol").$()), "authprotocol_div", _("Original"))
			),
			authProtocol
		);

		// append snmpv3 authpassphrase to form list
		itemFormList.addRow(
			array(
				_("Authentication passphrase"),
				SPACE,
				CVisibilityBox.instance("visible[authpassphrase]", isset(Nest.value(data,"visible","authpassphrase").$()), "snmpv3_authpassphrase", _("Original"))
			),
			new CTextBox("snmpv3_authpassphrase", Nest.value(data,"snmpv3_authpassphrase").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append snmpv3 privprotocol to form list
		CDiv privProtocol = new CDiv(
			array(
				new CRadioButton("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_DES, null, "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_DES, Nest.value(data,"snmpv3_privprotocol").asInteger() == ITEM_PRIVPROTOCOL_DES),
				new CLabel(_("DES"), "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_DES),
				new CRadioButton("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_AES, null, "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_AES, Nest.value(data,"snmpv3_privprotocol").asInteger() == ITEM_PRIVPROTOCOL_AES),
				new CLabel(_("AES"), "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_AES)
			),
			"jqueryinputset",
			"privprotocol_div"
		);
		itemFormList.addRow(
			array(
				_("Privacy protocol"),
				SPACE,
				CVisibilityBox.instance("visible[privprotocol]", isset(Nest.value(data,"visible","privprotocol").$()), "privprotocol_div", _("Original"))
			),
			privProtocol
		);

		// append snmpv3 privpassphrase to form list
		itemFormList.addRow(
			array(
				_("Privacy passphrase"),
				SPACE,
				CVisibilityBox.instance("visible[privpassphras]", isset(Nest.value(data,"visible","privpassphras").$()), "snmpv3_privpassphrase", _("Original"))
			),
			new CTextBox("snmpv3_privpassphrase", Nest.value(data,"snmpv3_privpassphrase").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append port to form list
		itemFormList.addRow(
			array(
				_("Port"),
				SPACE,
				CVisibilityBox.instance("visible[port]", isset(Nest.value(data,"visible","port").$()), "port", _("Original"))
			),
			new CTextBox("port", Nest.value(data,"port").asString(), RDA_TEXTBOX_SMALL_SIZE)
		);

		// append value type to form list
		CComboBox valueTypeComboBox = new CComboBox("value_type", Nest.value(data,"value_type").$());
		valueTypeComboBox.addItem(ITEM_VALUE_TYPE_UINT64, _("Numeric (unsigned)"));
		valueTypeComboBox.addItem(ITEM_VALUE_TYPE_FLOAT, _("Numeric (float)"));
		valueTypeComboBox.addItem(ITEM_VALUE_TYPE_STR, _("Character"));
		valueTypeComboBox.addItem(ITEM_VALUE_TYPE_LOG, _("Log"));
		valueTypeComboBox.addItem(ITEM_VALUE_TYPE_TEXT, _("Text"));
		itemFormList.addRow(
			array(
				_("Type of information"),
				SPACE,
				CVisibilityBox.instance("visible[value_type]", isset(Nest.value(data,"visible","value_type").$()), "value_type", _("Original"))
			),
			valueTypeComboBox
		);

		// append data type to form list
		CComboBox dataTypeComboBox = new CComboBox("data_type", Nest.value(data,"data_type").$());
		dataTypeComboBox.addItems(item_data_type2str());
		itemFormList.addRow(
			array(
				_("Data type"),
				SPACE,
				CVisibilityBox.instance("visible[data_type]", isset(Nest.value(data,"visible","data_type").$()), "data_type", _("Original"))
			),
			dataTypeComboBox
		);

		// append units to form list
		itemFormList.addRow(
			array(
				_("Units"),
				SPACE,
				CVisibilityBox.instance("visible[units]", isset(Nest.value(data,"visible","units").$()), "units", _("Original"))
			),
			new CTextBox("units", Nest.value(data,"units").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append authtype to form list
		CComboBox authTypeComboBox = new CComboBox("authtype", Nest.value(data,"authtype").$());
		authTypeComboBox.addItem(ITEM_AUTHTYPE_PASSWORD, _("Password"));
		authTypeComboBox.addItem(ITEM_AUTHTYPE_PUBLICKEY, _("Public key"));
		itemFormList.addRow(
			array(
				_("Authentication method"),
				SPACE,
				CVisibilityBox.instance("visible[authtype]", isset(Nest.value(data,"visible","authtype").$()), "authtype", _("Original"))
			),
			authTypeComboBox
		);

		// append username to form list
		itemFormList.addRow(
			array(
				_("User name"),
				SPACE,
				CVisibilityBox.instance("visible[username]", isset(Nest.value(data,"visible","username").$()), "username", _("Original"))
			),
			new CTextBox("username", Nest.value(data,"username").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append publickey to form list
		itemFormList.addRow(
			array(
				_("Public key file"),
				SPACE,
				CVisibilityBox.instance("visible[publickey]", isset(Nest.value(data,"visible","publickey").$()), "publickey", _("Original"))
			),
			new CTextBox("publickey", Nest.value(data,"publickey").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append privatekey to form list
		itemFormList.addRow(
			array(
				_("Private key file"),
				SPACE,
				CVisibilityBox.instance("visible[privatekey]", isset(Nest.value(data,"visible","privatekey").$()), "privatekey", _("Original"))
			),
			new CTextBox("privatekey", Nest.value(data,"privatekey").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append password
		itemFormList.addRow(
			array(
				_("Password"),
				SPACE,
				CVisibilityBox.instance("visible[password]", isset(Nest.value(data,"visible","password").$()), "password", _("Original"))
			),
			new CTextBox("password", Nest.value(data,"password").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append formula to form list
		itemFormList.addRow(
			array(
				_("Custom multiplier")+" (0 - "+_("Disabled")+")",
				SPACE,
				CVisibilityBox.instance("visible[formula]", isset(Nest.value(data,"visible","formula").$()), "formula", _("Original"))
			),
			new CTextBox("formula", Nest.value(data,"formula").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append delay to form list
		itemFormList.addRow(
			array(
				_("Update interval (in sec)"),
				SPACE,
				CVisibilityBox.instance("visible[delay]", isset(Nest.value(data,"visible","delay").$()), "delay", _("Original"))
			),
			new CNumericBox("delay", Nest.value(data,"delay").asString(), 5)
		);

		// append delay flex to form list
		CTable delayFlexTable = new CTable(_("No flexible intervals defined."), "formElementTable");
		delayFlexTable.setAttribute("style", "min-width: 310px;");
		delayFlexTable.setAttribute("id", "delayFlexTable");
		delayFlexTable.setHeader(array(_("Interval"), _("Period"), _("Action")));
		int i = 0;
		Nest.value(data,"maxReached").$(false);
		for(Map delayFlex : (CArray<Map>)Nest.value(data,"delay_flex").asCArray()) {
			if (!isset(delayFlex,"delay") && !isset(delayFlex,"period")) {
				continue;
			}
			itemForm.addVar("delay_flex["+i+"][delay]", Nest.value(delayFlex,"delay").$());
			itemForm.addVar("delay_flex["+i+"][period]", Nest.value(delayFlex,"period").$());

			CRow row = new CRow(array(
				Nest.value(delayFlex,"delay").$(),
				Nest.value(delayFlex,"period").$(),
				new CButton("remove", _("Remove"), "javascript: removeDelayFlex("+i+");", "link_menu")
			));
			row.setAttribute("id", "delayFlex_"+i);
			delayFlexTable.addRow(row);

			// limit count of intervals, 7 intervals by 30 symbols = 210 characters, db storage field is 256
			i++;
			if (i == 7) {
				Nest.value(data,"maxReached").$(true);
				break;
			}
		}
		itemFormList.addRow(
			array(
				_("Flexible intervals"),
				SPACE,
				CVisibilityBox.instance("visible[delay_flex]", isset(Nest.value(data,"visible","delay_flex").$()), array("delayFlexDiv", "row-new-delay-flex-fields"), _("Original"))
			),
			new CDiv(delayFlexTable, "objectgroup inlineblock border_dotted ui-corner-all", "delayFlexDiv")
		);

		// append new delay to form list
		CDiv newFlexInt = new CDiv(
			array(
				_("Interval (in sec)"),
				SPACE,
				new CNumericBox("new_delay_flex[delay]", "50", 5),
				SPACE,
				_("Period"),
				SPACE,
				new CTextBox("new_delay_flex[period]", RDA_DEFAULT_INTERVAL, 20),
				SPACE,
				new CSubmit("add_delay_flex", _("Add"), null, "formlist")
			),
			null,
			"row-new-delay-flex-fields"
		);

		CSpan maxFlexMsg = new CSpan(_("Maximum number of flexible intervals added"), "red");
		maxFlexMsg.setAttribute("id", "row-new-delay-flex-max-reached");
		maxFlexMsg.setAttribute("style", "display: none;");

		itemFormList.addRow(_("New flexible interval"), array(newFlexInt, maxFlexMsg), false, "row_new_delay_flex", "new");

		// append history to form list
		itemFormList.addRow(
			array(
				_("History storage period (in days)"),
				SPACE,
				CVisibilityBox.instance("visible[history]", isset(Nest.value(data,"visible","history").$()), "history", _("Original"))
			),
			new CNumericBox("history", Nest.value(data,"history").asString(), 8)
		);

		// append trends to form list
		itemFormList.addRow(
			array(
				_("Trend storage period (in days)"),
				SPACE,
				CVisibilityBox.instance("visible[trends]", isset(Nest.value(data,"visible","trends").$()), "trends", _("Original"))
			),
			new CNumericBox("trends", Nest.value(data,"trends").asString(), 8)
		);

		// append status to form list
		CComboBox statusComboBox = new CComboBox("status", Nest.value(data,"status").$());
		for(int status : new int[]{ITEM_STATUS_ACTIVE, ITEM_STATUS_DISABLED}) {
			statusComboBox.addItem(status, item_status2str(status));
		}
		itemFormList.addRow(
			array(
				_("Status"),
				SPACE,
				CVisibilityBox.instance("visible[status]", isset(Nest.value(data,"visible","status").$()), "status", _("Original"))
			),
			statusComboBox
		);

		// append logtime to form list
		itemFormList.addRow(
			array(
				_("Log time format"),
				SPACE,
				CVisibilityBox.instance("visible[logtimefmt]", isset(Nest.value(data,"visible","logtimefmt").$()), "logtimefmt", _("Original"))
			),
			new CTextBox("logtimefmt", Nest.value(data,"logtimefmt").asString(), RDA_TEXTBOX_SMALL_SIZE)
		);

		// append delta to form list
		CComboBox deltaComboBox = new CComboBox("delta", Nest.value(data,"delta").$());
		deltaComboBox.addItem(0, _("As is"));
		deltaComboBox.addItem(1, _("Delta (speed per second)"));
		deltaComboBox.addItem(2, _("Delta (simple change)"));
		itemFormList.addRow(
			array(
				_("Store value"),
				SPACE,
				CVisibilityBox.instance("visible[delta]", isset(Nest.value(data,"visible","delta").$()), "delta", _("Original"))
			),
			deltaComboBox
		);

		// append valuemap to form list
		CComboBox valueMapsComboBox = new CComboBox("valuemapid", Nest.value(data,"valuemapid").$());
		valueMapsComboBox.addItem(0, _("As is"));
		for(Map valuemap : (CArray<Map>)Nest.value(data,"valuemaps").asCArray()) {
			valueMapsComboBox.addItem(Nest.value(valuemap,"valuemapid").$(), Nest.value(valuemap,"name").asString());
		}
		CLink valueMapLink = new CLink(_("show value mappings"), "adm.valuemapping.action");
		valueMapLink.setAttribute("target", "_blank");

		itemFormList.addRow(
			array(
				_("Show value"),
				SPACE,
				CVisibilityBox.instance("visible[valuemapid]", isset(Nest.value(data,"visible","valuemapid").$()), "valuemap", _("Original"))
			),
			new CDiv(array(valueMapsComboBox, SPACE, valueMapLink), null, "valuemap")
		);

		// append trapper hosts to form list
		itemFormList.addRow(
			array(
				_("Allowed hosts"),
				SPACE,
				CVisibilityBox.instance("visible[trapper_hosts]", isset(Nest.value(data,"visible","trapper_hosts").$()), "trapper_hosts", _("Original"))
			),
			new CTextBox("trapper_hosts", Nest.value(data,"trapper_hosts").asString(), RDA_TEXTBOX_STANDARD_SIZE)
		);

		// append applications to form list
		if (!empty(Nest.value(data,"displayApplications").$())) {
			// replace applications
			CArray<Map> appToReplace = null;
			if (hasRequest("applications")) {
				appToReplace = array();
				CAppGet aoptions = new CAppGet();
				aoptions.setApplicationIds(get_request_asLong("applications"));
				aoptions.setOutput(new String[]{"applicationid", "name"});
				CArray<Map> getApps = API.Application(idBean, executor).get(aoptions);
				for(Map getApp : getApps) {
					appToReplace.add(map(
						"id", Nest.value(getApp,"applicationid").$(),
						"name", Nest.value(getApp,"name").$()
					));
				}
			}

			CDiv replaceApp = new CDiv(new CMultiSelect(map(
				"name" , "applications[]",
				"objectName" , "applications",
				"objectOptions" , map("hostid" , Nest.value(data,"hostid").$()),
				"data" , appToReplace,
				"popup" , map(
					"parameters" , "srctbl=applications&dstfrm="+itemForm.getName()+"&dstfld1=applications_"+
						"&srcfld1=applicationid&multiselect=1&noempty=1&hostid="+Nest.value(data,"hostid").$(),
					"width" , 450,
					"height" , 450
				)
			)), null, "replaceApp");

			itemFormList.addRow(
				array(_("Replace applications"), SPACE, CVisibilityBox.instance("visible[applications]",
					isset(Nest.value(data,"visible","applications").$()), "replaceApp", _("Original")
				)),
				replaceApp
			);

			// add new or existing applications
			CArray appToAdd = null;
			if (hasRequest("new_applications")) {
				appToAdd = array();
				CArray appToAddId = array();
				for(Object newApplication : Nest.as(get_request("new_applications")).asCArray()) {
					if (newApplication instanceof Map && isset((Map)newApplication,"new")) {
						appToAdd.add(map(
							"id", Nest.value((Map)newApplication,"new").$(),
							"name", Nest.value((Map)newApplication,"new").$()+" ("+_x("new", "new element in multiselect")+")",
							"isNew", true
						));
					} else {
						appToAddId.add(newApplication);
					}
				}

				if (isset(appToAddId)) {
					CAppGet aoptions = new CAppGet();
					aoptions.setApplicationIds(appToAddId.valuesAsLong());
					aoptions.setOutput(new String[]{"applicationid", "name"});
					CArray<Map> getApps = API.Application(idBean, executor).get(aoptions);
					for(Map getApp : getApps) {
						appToAdd.add(map(
							"id", Nest.value(getApp,"applicationid").$(),
							"name", Nest.value(getApp,"name").$()
						));
					}
				}
			}

			CDiv newApp = new CDiv(new CMultiSelect(map(
				"name" , "new_applications[]",
				"objectName" , "applications",
				"objectOptions" , map("hostid" , Nest.value(data,"hostid").$()),
				"data" , appToAdd,
				"addNew" , true,
				"popup" , map(
					"parameters" , "srctbl=applications&dstfrm="+itemForm.getName()+"&dstfld1=new_applications_"+
						"&srcfld1=applicationid&multiselect=1&noempty=1&hostid="+Nest.value(data,"hostid").$(),
					"width" , 450,
					"height" , 450
				)
			)), null, "newApp");

			itemFormList.addRow(
				array(_("Add new or existing applications"), SPACE, CVisibilityBox.instance("visible[new_applications]",
					isset(Nest.value(data,"visible","new_applications").$()), "newApp", _("Original")
				)),
				newApp
			);
		}

		// append description to form list
		CTextArea descriptionTextArea = new CTextArea("description", Nest.value(data,"description").asString());
		descriptionTextArea.addStyle("margin-top: 5px;");
		itemFormList.addRow(
			array(
				_("Description"),
				SPACE,
				CVisibilityBox.instance("visible[description]", isset(Nest.value(data,"visible","description").$()), "description", _("Original"))
			),
			descriptionTextArea
		);
		// append tabs to form
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", _("Mass update"), itemFormList);
		itemForm.addItem(itemTab);

		// append buttons to form
		itemForm.addItem(makeFormFooter(new CSubmit("update", _("Update")), new CButtonCancel(url_param(idBean, "groupid")+url_param(idBean, "hostid")+url_param(idBean, "config"))));
		itemWidget.addItem(itemForm);

		includeSubView("js/configuration.item.edit.js");

		return itemWidget;
	}

}
