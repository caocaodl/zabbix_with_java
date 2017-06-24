package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_BOOLEAN;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_DECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_HEXADECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_OCTAL;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_EXTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SIMPLE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_JMX;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_SSH;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_KEY_TELNET;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_STANDARD_ROWS;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_STANDARD_WIDTH;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.rda_subarray_push;
import static com.isoft.iradar.inc.FuncsUtil.rda_substr;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ItemsUtil.interfaceType2str;
import static com.isoft.iradar.inc.ItemsUtil.itemValueTypeString;
import static com.isoft.iradar.inc.ItemsUtil.item_data_type2str;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonQMessage;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.COptGroup;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationItemEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget itemWidget = new CWidget();

		/*if (!empty(Nest.value(data,"hostid").$())) {
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				itemWidget.addItem(get_header_host_table(executor, !empty(Nest.value(data,"is_discovery_rule").$()) ? "discoveries" : "items",
					Nest.value(data,"hostid").asLong(), Nest.value(data,"parent_discoveryid").asLong()));
			} else {
				itemWidget.addItem(get_header_host_table(executor, !empty(Nest.value(data,"is_discovery_rule").$()) ? "discoveries" : "items",
					Nest.value(data,"hostid").asLong()));
			}
		}*/

		// create form
		CForm itemForm = new CForm();
		itemForm.setName("itemForm");
		itemForm.addVar("form", Nest.value(data,"form").$());
		itemForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			itemForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		if (!empty(Nest.value(data,"itemid").$())) {
			itemForm.addVar("itemid", Nest.value(data,"itemid").$());
		}

		// create form list
		CFormList itemFormList = new CFormList("itemFormList");
		if (!empty(Nest.value(data,"templates").$())) {
			if (Nest.value(data,"is_discovery_rule").asBoolean()) {
				itemFormList.addRow(_("Parent discovery rules"), Nest.value(data,"templates").$());
			} else {
				itemFormList.addRow(_("Parent items"), Nest.value(data,"templates").$());
			}
		}

		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean());
		nameTextBox.attr("autofocus", "autofocus");
		itemFormList.addRow(_("Name"), nameTextBox);

		// append type to form list
		if (!empty(Nest.value(data,"limited").$())) {
			itemForm.addVar("type", Nest.value(data,"type").$());
			itemFormList.addRow(_("Type"),
				new CTextBox("typename", item_type2str(Nest.value(data,"type").asInteger()), RDA_TEXTBOX_STANDARD_SIZE, true)
			);
		} else {
			CComboBox typeComboBox = new CComboBox("type", Nest.value(data,"type").$());
			typeComboBox.addItems(Nest.value(data,"types").asCArray());
			itemFormList.addRow(_("Type"), typeComboBox);
		}

		// append key to form list
		itemFormList.addRow(_("Key"), array(
			new CTextBox("key", Nest.value(data,"key").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean()),
			empty(Nest.value(data,"limited").$()) && !Nest.value(data,"is_discovery_rule").asBoolean()
				? new CButton("keyButton", _("Select"),
					"return PopUp(\"popup.action?srctbl=help_items&srcfld1=key"+
						"&dstfrm="+itemForm.getName()+"&dstfld1=key&itemtype=\"+jQuery(\"#type option:selected\").val());",
					"formlist")
				: null
		));

		// append interfaces to form list
		if (!empty(Nest.value(data,"interfaces").$())) {
			CComboBox interfacesComboBox = new CComboBox("interfaceid", Nest.value(data,"interfaceid").$());

			// set up interface groups
			CArray<COptGroup> interfaceGroups = array();
			for(Integer interfaceType : (CArray<Integer>)rda_objectValues(Nest.value(data,"interfaces").$(), "type")) {
				Nest.value(interfaceGroups,interfaceType).$(new COptGroup(interfaceType2str(interfaceType)));
			}

			// add interfaces to groups
			for(Map iface : (CArray<Map>)Nest.value(data,"interfaces").asCArray()) {
				CComboItem option = new CComboItem(
					Nest.value(iface,"interfaceid").$(),
					!empty(Nest.value(iface,"useip").$()) ? Nest.value(iface,"ip").$()+" : "+Nest.value(iface,"port").$() : Nest.value(iface,"dns").$()+" : "+Nest.value(iface,"port").$(),
					Nest.value(iface,"interfaceid").$().equals(Nest.value(data,"interfaceid").$()) ? true : false
				);
				option.setAttribute("data-interfacetype", Nest.value(iface,"type").$());
				interfaceGroups.get(iface.get("type")).addItem(option);
			}
			for(COptGroup interfaceGroup : interfaceGroups) {
				interfacesComboBox.addItem(interfaceGroup);
			}

			CSpan span = new CSpan(_("No interface found"), "red");
			span.setAttribute("id", "interface_not_defined");
			span.setAttribute("style", "display: none;");

			itemFormList.addRow(_("Host interface"), array(interfacesComboBox, span), false, "interface_row");
			itemForm.addVar("selectedInterfaceId", Nest.value(data,"interfaceid").$());
		}
		itemFormList.addRow(_("SNMP OID"),
			new CTextBox("snmp_oid", Nest.value(data,"snmp_oid").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean()),
			false, "row_snmp_oid"
		);
		itemFormList.addRow(_("Context name"),
			new CTextBox("snmpv3_contextname", Nest.value(data,"snmpv3_contextname").asString(), RDA_TEXTBOX_STANDARD_SIZE),
			false, "row_snmpv3_contextname"
		);
		itemFormList.addRow(_("SNMP community"),
			new CTextBox("snmp_community", Nest.value(data,"snmp_community").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64),
			false, "row_snmp_community"
		);
		itemFormList.addRow(_("Security name"),
			new CTextBox("snmpv3_securityname", Nest.value(data,"snmpv3_securityname").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64),
			false, "row_snmpv3_securityname"
		);

		// append snmpv3 security level to form list
		CComboBox securityLevelComboBox = new CComboBox("snmpv3_securitylevel", Nest.value(data,"snmpv3_securitylevel").$());
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV, "noAuthNoPriv");
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "authNoPriv");
		securityLevelComboBox.addItem(ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "authPriv");
		itemFormList.addRow(_("Security level"), securityLevelComboBox, false, "row_snmpv3_securitylevel");
		CArray authProtocolRadioButton = array(
			new CRadioButton("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_MD5, null, "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_MD5, Nest.value(data,"snmpv3_authprotocol").asInteger() == ITEM_AUTHPROTOCOL_MD5),
			new CLabel(_("MD5"), "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_MD5),
			new CRadioButton("snmpv3_authprotocol", ITEM_AUTHPROTOCOL_SHA, null, "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_SHA, Nest.value(data,"snmpv3_authprotocol").asInteger() == ITEM_AUTHPROTOCOL_SHA),
			new CLabel(_("SHA"), "snmpv3_authprotocol_"+ITEM_AUTHPROTOCOL_SHA)
		);
		itemFormList.addRow(_("Authentication protocol"),
			new CDiv(authProtocolRadioButton, "jqueryinputset"),
			false, "row_snmpv3_authprotocol"
		);
		itemFormList.addRow(_("Authentication passphrase"),
			new CTextBox("snmpv3_authpassphrase", Nest.value(data,"snmpv3_authpassphrase").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64),
			false, "row_snmpv3_authpassphrase"
		);
		CArray privProtocolRadioButton = array(
			new CRadioButton("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_DES, null, "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_DES, Nest.value(data,"snmpv3_privprotocol").asInteger() == ITEM_PRIVPROTOCOL_DES),
			new CLabel(_("DES"), "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_DES),
			new CRadioButton("snmpv3_privprotocol", ITEM_PRIVPROTOCOL_AES, null, "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_AES, Nest.value(data,"snmpv3_privprotocol").asInteger() == ITEM_PRIVPROTOCOL_AES),
			new CLabel(_("AES"), "snmpv3_privprotocol_"+ITEM_PRIVPROTOCOL_AES)
		);
		itemFormList.addRow(_("Privacy protocol"),
			new CDiv(privProtocolRadioButton, "jqueryinputset"),
			false, "row_snmpv3_privprotocol"
		);
		itemFormList.addRow(_("Privacy passphrase"),
			new CTextBox("snmpv3_privpassphrase", Nest.value(data,"snmpv3_privpassphrase").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64),
			false, "row_snmpv3_privpassphrase"
		);
		itemFormList.addRow(_("Port"),
			new CTextBox("port", Nest.value(data,"port").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64), false, "row_port"
		);
		itemFormList.addRow(_("IPMI sensor"),
			new CTextBox("ipmi_sensor", Nest.value(data,"ipmi_sensor").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean(), 128),
			false, "row_ipmi_sensor"
		);

		// append authentication method to form list
		CComboBox authTypeComboBox = new CComboBox("authtype", Nest.value(data,"authtype").$());
		authTypeComboBox.addItem(ITEM_AUTHTYPE_PASSWORD, _("Password"));
		authTypeComboBox.addItem(ITEM_AUTHTYPE_PUBLICKEY, _("Public key"));
		itemFormList.addRow(_("Authentication method"), authTypeComboBox, false, "row_authtype");
		itemFormList.addRow(_("User name"),
			new CTextBox("username", Nest.value(data,"username").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64), false, "row_username"
		);
		itemFormList.addRow(_("Public key file"),
			new CTextBox("publickey", Nest.value(data,"publickey").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64), false, "row_publickey"
		);
		itemFormList.addRow(_("Private key file"),
			new CTextBox("privatekey", Nest.value(data,"privatekey").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64), false,  "row_privatekey"
		);
		itemFormList.addRow(_("Password"),
			new CTextBox("password", Nest.value(data,"password").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64), false, "row_password"
		);
		itemFormList.addRow(_("Executed script"),
			new CTextArea("params_es", Nest.value(data,"params").asString(), (CArray)map("rows", RDA_TEXTAREA_STANDARD_ROWS, "width", RDA_TEXTAREA_STANDARD_WIDTH)),
			false, "label_executed_script"
		);
		itemFormList.addRow(_("SQL query"),
			new CTextArea("params_ap",
				Nest.value(data,"params").asString(),
				(CArray)map("rows", RDA_TEXTAREA_STANDARD_ROWS, "width", RDA_TEXTAREA_STANDARD_WIDTH)
			),
			false,
			"label_params"
		);
		itemFormList.addRow(_("Formula"),
			new CTextArea("params_f", Nest.value(data,"params").asString(), (CArray)map("rows", RDA_TEXTAREA_STANDARD_ROWS, "width", RDA_TEXTAREA_STANDARD_WIDTH)),
			false, "label_formula"
		);

		// append value type to form list
		if (!Nest.value(data,"is_discovery_rule").asBoolean()) {
			if (!empty(Nest.value(data,"limited").$())) {
				itemForm.addVar("value_type", Nest.value(data,"value_type").$());
				itemFormList.addRow(_("Type of information"),
					new CTextBox("value_type_name", itemValueTypeString(Nest.value(data,"value_type").asInteger()), RDA_TEXTBOX_STANDARD_SIZE, true)
				);
			} else {
				CComboBox valueTypeComboBox = new CComboBox("value_type", Nest.value(data,"value_type").$());
				valueTypeComboBox.addItem(ITEM_VALUE_TYPE_UINT64, _("Numeric (unsigned)"));
				valueTypeComboBox.addItem(ITEM_VALUE_TYPE_FLOAT, _("Numeric (float)"));
				valueTypeComboBox.addItem(ITEM_VALUE_TYPE_STR, _("Character"));
				valueTypeComboBox.addItem(ITEM_VALUE_TYPE_LOG, _("Log"));
				valueTypeComboBox.addItem(ITEM_VALUE_TYPE_TEXT, _("Text"));
				itemFormList.addRow(_("Type of information"), valueTypeComboBox);
			}

			// append data type to form list
			CTag dataType = null;
			if (!empty(Nest.value(data,"limited").$())) {
				itemForm.addVar("data_type", Nest.value(data,"data_type").$());
				dataType = new CTextBox("data_type_name", item_data_type2str(Nest.value(data,"data_type").asInteger()), RDA_TEXTBOX_SMALL_SIZE, true);
			} else {
				dataType = new CComboBox("data_type", Nest.value(data,"data_type").$());
				((CComboBox)dataType).addItems(item_data_type2str());
			}
			itemFormList.addRow(_("Data type"), dataType, false, "row_data_type");
			itemFormList.addRow(_("Units"),
				new CTextBox("units", Nest.value(data,"units").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean()), false, "row_units"
			);

			// append multiplier to form list
			CArray multiplier = array();
			CCheckBox multiplierCheckBox = null;
			CTextBox formulaTextBox = null;
			if (!empty(Nest.value(data,"limited").$())) {
				itemForm.addVar("multiplier", Nest.value(data,"multiplier").$());

				multiplierCheckBox = new CCheckBox("multiplier", Nest.value(data,"multiplier").asInteger() == 1 ? true:false);
				multiplierCheckBox.setAttribute("disabled", "disabled");
				multiplier.add(multiplierCheckBox);

				multiplier.add(SPACE);
				formulaTextBox = new CTextBox("formula", Nest.value(data,"formula").asString(), RDA_TEXTBOX_SMALL_SIZE, true);
				formulaTextBox.setAttribute("style", "text-align: right;");
				multiplier.add(formulaTextBox);
			} else {
				multiplierCheckBox = new CCheckBox("multiplier", Nest.value(data,"multiplier").asInteger() == 1 ? true: false,
					"var editbx = document.getElementById(\"formula\"); if (editbx) { editbx.disabled = !this.checked; }", 1
				);
				multiplier.add(multiplierCheckBox);
				multiplier.add(SPACE);
				formulaTextBox = new CTextBox("formula", Nest.value(data,"formula").asString(), RDA_TEXTBOX_SMALL_SIZE);
				formulaTextBox.setAttribute("style", "text-align: right;");
				multiplier.add(formulaTextBox);
			}
			itemFormList.addRow(_("Use custom multiplier"), multiplier, false, "row_multiplier");
		}
		itemFormList.addRow(_("Update interval (in sec)"), new CNumericBox("delay", Nest.value(data,"delay").asString(), 5), false, "row_delay");

		// append delay flex to form list
		CTable delayFlexTable = new CTable(_("No flexible intervals defined."), "formElementTable");
		delayFlexTable.setAttribute("style", "min-width: 310px;");
		delayFlexTable.setAttribute("id", "delayFlexTable");
		delayFlexTable.setHeader(array(_("Interval"), _("Period"), _("Action operations")));
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
				new CButton("remove", _("Remove"), "javascript: removeDelayFlex("+i+");", "link_menu icon remove")
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
		itemFormList.addRow(_("Flexible intervals"),
			new CDiv(delayFlexTable, "objectgroup inlineblock border_dotted ui-corner-all"), false, "row_flex_intervals"
		);

		// append new flexible interval to form list
		CSpan newFlexInt = new CSpan(array(
			_("Interval (in sec)"),
			SPACE,
			new CNumericBox("new_delay_flex[delay]", Nest.value(data,"new_delay_flex","delay").asString(), 5, false, false, false),
			SPACE,
			_("Period"),
			SPACE,
			new CTextBox("new_delay_flex[period]", Nest.value(data,"new_delay_flex","period").asString(), 20),
			SPACE,
			new CButton("add_delay_flex", _("Add"), null, "formlist")
		));
		newFlexInt.setAttribute("id", "row-new-delay-flex-fields");

		CSpan maxFlexMsg = new CSpan(_("Maximum number of flexible intervals added"), "red");
		maxFlexMsg.setAttribute("id", "row-new-delay-flex-max-reached");
		maxFlexMsg.setAttribute("style", "display: none;");

		itemFormList.addRow(_("New flexible interval"), array(newFlexInt, maxFlexMsg), false, "row_new_delay_flex", "new");

		if (Nest.value(data,"is_discovery_rule").asBoolean()) {
			itemFormList.addRow(_("Keep lost resources period (in days)"), new CTextBox("lifetime", Nest.value(data,"lifetime").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64));

			// append filter to formlist
			String filter_value = null;
			String filter_macro = null;
			if (!empty(Nest.value(data,"filter").$())) {
				// exploding filter to two parts: before first ":" and after
				int pos = rda_strpos(Nest.value(data,"filter").asString(), ":");
				filter_macro  = rda_substr(Nest.value(data,"filter").asString(), 0, pos);
				filter_value  = rda_substr(Nest.value(data,"filter").asString(), pos + 1);
			} else {
				filter_macro = "";
				filter_value = "";
			}
			itemFormList.addRow(
				_("Filter"),
				array(
					_("Macro"),
					SPACE,
					new CTextBox("filter_macro", filter_macro, 13),
					SPACE,
					_("Regexp"),
					SPACE,
					new CTextBox("filter_value", filter_value, 20)
				)
			);
			itemFormList.addRow(_("Allowed hosts"),
				new CTextBox("trapper_hosts", Nest.value(data,"trapper_hosts").asString(), RDA_TEXTBOX_STANDARD_SIZE),
				false, "row_trapper_hosts");
		} else {
			Map<String, Object> dataConfig = select_config(idBean, executor);
			CArray keepHistory = array();
			keepHistory.add(new CNumericBox("history", Nest.value(data,"history").asString(), 8));
			if (!empty(Nest.value(dataConfig,"hk_history_global").$()) && empty(Nest.value(data,"parent_discoveryid").$()) && !Nest.value(data,"is_template").asBoolean()) {
				keepHistory.add(SPACE);
				if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
					keepHistory.add(new CSpan(_x("Overridden by", "item_form")));
					keepHistory.add(SPACE);
					CLink link = new CLink(_x("global housekeeping settings", "item_form"), "adm.housekeeper.action");
					link.setAttribute("target", "_blank");
					keepHistory.add(link);
					keepHistory.add(SPACE);
					keepHistory.add(new CSpan("("+_n("%1$s day", "%1$s days", Nest.value(dataConfig,"hk_history").$())+")"));
				} else {
					keepHistory.add(new CSpan(_("Overriden by global housekeeping settings")+
						"("+_n("%1$s day", "%1$s days", Nest.value(dataConfig,"hk_history").$())+")"
					));
				}
			}
			itemFormList.addRow(_("History storage period (in days)"), keepHistory);

			CArray keepTrend = array();
			keepTrend.add(new CNumericBox("trends", Nest.value(data,"trends").asString(), 8));
			CLink link = null;
			if (!empty(Nest.value(dataConfig,"hk_trends_global").$()) && empty(Nest.value(data,"parent_discoveryid").$()) && !Nest.value(data,"is_template").asBoolean()) {
				keepTrend.add(SPACE);
				if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
					keepTrend.add(new CSpan(_x("Overridden by", "item_form")));
					keepTrend.add(SPACE);
					link = new CLink(_x("global housekeeping settings", "item_form"), "adm.housekeeper.action");
					link.setAttribute("target", "_blank");
					keepTrend.add(link);
					keepTrend.add(SPACE);
					keepTrend.add(new CSpan("("+_n("%1$s day", "%1$s days", Nest.value(dataConfig,"hk_trends").$())+")"));
				} else {
					keepTrend.add(new CSpan(_("Overriden by global housekeeping settings")+
						"("+_n("%1$s day", "%1$s days", Nest.value(dataConfig,"hk_trends").$())+")"
					));
				}
			}

			itemFormList.addRow(_("Trend storage period (in days)"), keepTrend, false, "row_trends");
			itemFormList.addRow(_("Log time format"),
				new CTextBox("logtimefmt", Nest.value(data,"logtimefmt").asString(), RDA_TEXTBOX_SMALL_SIZE, Nest.value(data,"limited").asBoolean(), 64),
				false, "row_logtimefmt"
			);

			// append delta to form list
			CArray deltaOptions = map(
				0, _("As is"),
				1, _("Delta (speed per second)"),
				2, _("Delta (simple change)")
			);
			CTag deltaComboBox = null;
			if (Nest.value(data,"limited").asBoolean()) {
				itemForm.addVar("delta", Nest.value(data,"delta").$());
				deltaComboBox  = new CTextBox("delta_name", Nest.value(deltaOptions,data.get("delta")).asString(), null, true);
			} else {
				deltaComboBox= new CComboBox("delta", Nest.value(data,"delta").$());
				((CComboBox)deltaComboBox).addItems(deltaOptions);
			}
			itemFormList.addRow(_("Store value"), deltaComboBox, false, "row_delta");

			// append valuemap to form list
			CTag valuemapComboBox = null;
			if (Nest.value(data,"limited").asBoolean()) {
				itemForm.addVar("valuemapid", Nest.value(data,"valuemapid").$());
				valuemapComboBox = new CTextBox("valuemap_name", !empty(Nest.value(data,"valuemaps").$()) ? Nest.value(data,"valuemaps").asString() : _("As is"), RDA_TEXTBOX_SMALL_SIZE, true);
			} else {
				valuemapComboBox = new CComboBox("valuemapid", Nest.value(data,"valuemapid").$());
				((CComboBox)valuemapComboBox).addItem(0, _("As is"));
				for(Map valuemap : (CArray<Map>)Nest.value(data,"valuemaps").asCArray()) {
					((CComboBox)valuemapComboBox).addItem(
						Nest.value(valuemap,"valuemapid").$(),
						CHtml.encode(Nest.value(valuemap,"name").asString())
					);
				}
			}
			link = new CLink(_("show value mappings"), "adm.valuemapping.action");
			link.setAttribute("target", "_blank");
			itemFormList.addRow(_("Show value"), array(valuemapComboBox, SPACE, link), false, "row_valuemap");
			itemFormList.addRow(_("Allowed hosts"),
				new CTextBox("trapper_hosts", Nest.value(data,"trapper_hosts").asString(), RDA_TEXTBOX_STANDARD_SIZE),
				false, "row_trapper_hosts");

			// append applications to form list
			itemFormList.addRow(_("New application"),
				new CTextBox("new_application", Nest.value(data,"new_application").asString(), RDA_TEXTBOX_STANDARD_SIZE), false, null, "new"
			);
			CListBox applicationComboBox = new CListBox("applications[]", Nest.value(data,"applications").$(), 6);
			applicationComboBox.addItem(0, "-"+_("None")+"-");
			for(Map application : (CArray<Map>)Nest.value(data,"db_applications").asCArray()) {
				applicationComboBox.addItem(Nest.value(application,"applicationid").$(), CHtml.encode(Nest.value(application,"name").asString()));
			}
			itemFormList.addRow(_("Application"), applicationComboBox);

			// append populate host to form list
			if (empty(Nest.value(data,"parent_discoveryid").$())) {
				boolean itemCloned = isset(_REQUEST,"clone");
				CComboBox hostInventoryFieldComboBox = new CComboBox("inventory_link");
				hostInventoryFieldComboBox.addItem(0, "-"+_("None")+"-", Nest.value(data,"inventory_link").asInteger() == 0 ? true : null);

				// a list of available host inventory fields
				for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"possibleHostInventories").asCArray()).entrySet()) {
					long fieldNo = Nest.as(e.getKey()).asLong();
				    Map fieldInfo = e.getValue();
					boolean enabled = false;
					if (isset(Nest.value(data,"alreadyPopulated",fieldNo).$())) {
						enabled  = isset(Nest.value(data,"item","inventory_link").$())
							? Nest.value(data,"item","inventory_link").asLong()==fieldNo
							: Nest.value(data,"inventory_link").asLong()==fieldNo && !itemCloned;
					} else {
						enabled = true;
					}
					hostInventoryFieldComboBox.addItem(
						fieldNo,
						Nest.value(fieldInfo,"title").asString(),
						Nest.value(data,"inventory_link").asLong()==fieldNo && enabled ? true : null,
						enabled ? true : false
					);
				}
				/*
				 * @author fengjinbing
				 * @describe  hidden field
				 * @time 2014-12-25
				 * */
				itemFormList.addRow(_("Populates host inventory field"), hostInventoryFieldComboBox, true, "row_inventory_link");
			}
		}

		// append description to form list
		CTextArea description = new CTextArea("description", Nest.value(data,"description").asString());
		description.addStyle("margin-top: 5px;");
		itemFormList.addRow(_("Description"), description);

		// status
		CCheckBox enabledCheckBox = new CCheckBox("status", empty(Nest.value(data,"status").$()), null, ITEM_STATUS_ACTIVE);
		itemFormList.addRow(_("Enabled"), enabledCheckBox);

		// append tabs to form
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", Nest.value(data,"caption").asString(), itemFormList);
		itemForm.addItem(itemTab);

		// append buttons to form
		CArray buttons = array();
		if (!empty(Nest.value(data,"itemid").$())) {
			array_push(buttons, new CSubmit("clone", _("Clone")));

			if (!Nest.value(data,"is_template").asBoolean() && !empty(Nest.value(data,"itemid").$()) && empty(Nest.value(data,"parent_discoveryid").$()) && !Nest.value(data,"is_discovery_rule").asBoolean()) {
				array_push(buttons,
					new CButtonQMessage("del_history", _("Clear history and trends"), _("History clearing can take a long time. Continue?"))
				);
			}
		}
		array_push(buttons, new CButtonCancel(url_param(idBean, "groupid")+url_param(idBean, "parent_discoveryid")+url_param(idBean, "hostid")));
		itemForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), buttons));
		itemWidget.addItem(itemForm);

		/*
		 * Visibility
		 */
		int type;
		Nest.value(data,"typeVisibility").$(array());
		i = 0;
		for(Map delayFlex : (CArray<Map>)Nest.value(data,"delay_flex").asCArray()) {
			if (!isset(delayFlex,"delay") && !isset(delayFlex,"period")) {
				continue;
			}
			for(Object otype : Nest.value(data,"types").asCArray().keySet()) {
				type = Nest.as(otype).asInteger();
				if (type == ITEM_TYPE_TRAPPER || type == ITEM_TYPE_IRADAR_ACTIVE || type == ITEM_TYPE_SNMPTRAP) {
					continue;
				}
				rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "delay_flex["+i+"][delay]");
				rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "delay_flex["+i+"][period]");
			}
			i++;
			if (i == 7) {
				break;
			}
		}
		if (!empty(Nest.value(data,"interfaces").$())) {
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IRADAR, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IRADAR, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_EXTERNAL, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_EXTERNAL, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IPMI, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IPMI, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "interfaceid");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPTRAP, "interface_row");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPTRAP, "interfaceid");
		}
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "row_username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "row_password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SIMPLE, "password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "row_snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "row_snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "row_snmp_oid");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "snmp_community");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "snmp_community");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "row_snmp_community");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "row_snmp_community");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "snmpv3_contextname");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "row_snmpv3_contextname");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "snmpv3_securityname");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "row_snmpv3_securityname");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "snmpv3_securitylevel");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "row_snmpv3_securitylevel");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV1, "row_port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV2C, "row_port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SNMPV3, "row_port");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IPMI, "ipmi_sensor");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_IPMI, "row_ipmi_sensor");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "authtype");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "row_authtype");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "row_username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "row_username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "row_username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "row_username");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "row_password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "row_password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "row_password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_JMX, "row_password");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "label_executed_script");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "label_executed_script");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "label_params");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_CALCULATED, "label_formula");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "params_script");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_SSH, "row_params");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "params_script");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TELNET, "row_params");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "params_dbmonitor");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_DB_MONITOR, "row_params");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_CALCULATED, "params_calculted");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_CALCULATED, "row_params");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TRAPPER, "trapper_hosts");
		rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), ITEM_TYPE_TRAPPER, "row_trapper_hosts");
		for(Object otype : Nest.value(data,"types").asCArray().keySet()) {
			type = Nest.as(otype).asInteger();
			switch (type) {
				case ITEM_TYPE_DB_MONITOR:
					rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, map("id", "key", "defaultValue", RDA_DEFAULT_KEY_DB_MONITOR));
					break;
				case ITEM_TYPE_SSH:
					rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, map("id", "key", "defaultValue", RDA_DEFAULT_KEY_SSH));
					break;
				case ITEM_TYPE_TELNET:
					rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, map("id", "key", "defaultValue", RDA_DEFAULT_KEY_TELNET));
					break;
				case ITEM_TYPE_JMX:
					rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, map("id", "key", "defaultValue", RDA_DEFAULT_KEY_JMX));
					break;
				default:
					rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, map("id", "key", "defaultValue", ""));
			}
		}
		for(Object otype : Nest.value(data,"types").asCArray().keySet()) {
			type = Nest.as(otype).asInteger();
			if (type == ITEM_TYPE_TRAPPER || type == ITEM_TYPE_IRADAR_ACTIVE || type == ITEM_TYPE_SNMPTRAP) {
				continue;
			}
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "row_flex_intervals");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "row_new_delay_flex");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "new_delay_flex[delay]");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "new_delay_flex[period]");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "add_delay_flex");
		}
		for(Object otype : Nest.value(data,"types").asCArray().keySet()) {
			type = Nest.as(otype).asInteger();
			if (type == ITEM_TYPE_TRAPPER || type == ITEM_TYPE_SNMPTRAP) {
				continue;
			}
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "delay");
			rda_subarray_push(Nest.value(data,"typeVisibility").asCArray(), type, "row_delay");
		}

		// disable dropdown items for calculated and aggregate items
		for(int itype : new int[]{ITEM_TYPE_CALCULATED, ITEM_TYPE_AGGREGATE}) {
			// set to disable character, log and text items in value type
			rda_subarray_push(Nest.value(data,"typeDisable").asCArray(), itype, array(ITEM_VALUE_TYPE_STR, ITEM_VALUE_TYPE_LOG, ITEM_VALUE_TYPE_TEXT), "value_type");

			// disable octal, hexadecimal and boolean items in data_type; Necessary for Numeric (unsigned) value type only
			rda_subarray_push(Nest.value(data,"typeDisable").asCArray(), itype, array(ITEM_DATA_TYPE_OCTAL, ITEM_DATA_TYPE_HEXADECIMAL, ITEM_DATA_TYPE_BOOLEAN), "data_type");
		}

		Nest.value(data,"valueTypeVisibility").$(array());
		if (!Nest.value(data,"is_discovery_rule").asBoolean()) {
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "data_type");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "row_data_type");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "units");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_units");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "multiplier");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_multiplier");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "delta");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_delta");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "trends");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_trends");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "trends");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "row_trends");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_LOG, "logtimefmt");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_LOG, "row_logtimefmt");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "valuemapid");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_STR, "valuemapid");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_STR, "row_valuemap");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_STR, "valuemap_name");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_valuemap");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "valuemap_name");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "valuemapid");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "row_valuemap");
			rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "valuemap_name");
			if (empty(Nest.value(data,"parent_discoveryid").$())) {
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_STR, "inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_STR, "row_inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_TEXT, "inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_TEXT, "row_inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_FLOAT, "row_inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "inventory_link");
				rda_subarray_push(Nest.value(data,"valueTypeVisibility").asCArray(), ITEM_VALUE_TYPE_UINT64, "row_inventory_link");
			}
		}

		Nest.value(data,"securityLevelVisibility").$(array());
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "snmpv3_authprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "row_snmpv3_authprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "snmpv3_authpassphrase");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, "row_snmpv3_authpassphrase");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "snmpv3_authprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "row_snmpv3_authprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "snmpv3_authpassphrase");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "row_snmpv3_authpassphrase");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "snmpv3_privprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "row_snmpv3_privprotocol");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "snmpv3_privpassphrase");
		rda_subarray_push(Nest.value(data,"securityLevelVisibility").asCArray(), ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV, "row_snmpv3_privpassphrase");

		Nest.value(data,"authTypeVisibility").$(array());
		rda_subarray_push(Nest.value(data,"authTypeVisibility").asCArray(), ITEM_AUTHTYPE_PUBLICKEY, "publickey");
		rda_subarray_push(Nest.value(data,"authTypeVisibility").asCArray(), ITEM_AUTHTYPE_PUBLICKEY, "row_publickey");
		rda_subarray_push(Nest.value(data,"authTypeVisibility").asCArray(), ITEM_AUTHTYPE_PUBLICKEY, "privatekey");
		rda_subarray_push(Nest.value(data,"authTypeVisibility").asCArray(), ITEM_AUTHTYPE_PUBLICKEY, "row_privatekey");

		Nest.value(data,"dataTypeVisibility").$(array());
		if (!Nest.value(data,"is_discovery_rule").asBoolean()) {
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "row_units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "row_units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "row_units");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "row_multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "row_multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "row_multiplier");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "delta");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_DECIMAL, "row_delta");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "delta");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_OCTAL, "row_delta");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "delta");
			rda_subarray_push(Nest.value(data,"dataTypeVisibility").asCArray(), ITEM_DATA_TYPE_HEXADECIMAL, "row_delta");
		}

		includeSubView("js/configuration.item.edit.js", data);
		return itemWidget;
	}

}
