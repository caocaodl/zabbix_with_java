package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CRadioButtonList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationProxyEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget proxyWidget = new CWidget();

		// create form
		CForm proxyForm = new CForm();
		proxyForm.setName("proxyForm");
		proxyForm.addVar("form", Nest.value(data,"form").$());
		proxyForm.addVar("form_refresh", Nest.value(data,"form_refresh").$());
		if (!empty(Nest.value(data,"proxyid").$())) {
			proxyForm.addVar("proxyid", Nest.value(data,"proxyid").$());
		}

		// create form list
		CFormList proxyFormList = new CFormList("proxyFormList");
		CTextBox nameTextBox = new CTextBox("host", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64);
		nameTextBox.attr("autofocus", "autofocus");
		proxyFormList.addRow(_("Proxy ip"), nameTextBox);
		CTextBox descTextBox = new CTextBox("proxyName", Nest.value(data,"alias").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64);
		nameTextBox.attr("autofocus", "autofocus");
		proxyFormList.addRow(_("Proxy name"), descTextBox);

		if (Nest.value(data,"status").asInteger() == HOST_STATUS_PROXY_PASSIVE) {
			if (isset(Nest.value(data,"interface","interfaceid").$())) {
				proxyForm.addVar("interface[interfaceid]", Nest.value(data,"interface","interfaceid").$());
				proxyForm.addVar("interface[hostid]", Nest.value(data,"interface","hostid").$());
			}

			CTable interfaceTable = new CTable(null, "formElementTable");
			interfaceTable.addRow(array(
				_("IP address"),
				_("DNS name"),
				_("Connect to"),
				_("Port")
			));

			CRadioButtonList connectByComboBox = new CRadioButtonList("interface[useip]", Nest.value(data,"interface","useip").asString());
			connectByComboBox.addValue(_("IP"), "1");
			connectByComboBox.addValue(_("DNS"), "0");
			connectByComboBox.useJQueryStyle();

			interfaceTable.addRow(array(
				new CTextBox("interface[ip]", Nest.value(data,"interface","ip").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64),
				new CTextBox("interface[dns]", Nest.value(data,"interface","dns").asString(), RDA_TEXTBOX_SMALL_SIZE, false, 64),
				connectByComboBox,
				new CTextBox("interface[port]", Nest.value(data,"interface","port").asString(), 18, false, 64)
			));
			proxyFormList.addRow(_("Interface"), new CDiv(interfaceTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		}

		// append groups to form list
		CComboBox groupsComboBox = new CComboBox("twb_groupid", Nest.value(data,"twb_groupid").$(), "submit()");
		groupsComboBox.addItem("0", _("All"));
		for(Map group : (CArray<Map>)Nest.value(data,"db_groups").asCArray()) {
			long groupId = Nest.value(group,"groupid").asLong();
			if(!IMonGroup.DISCOVERED_HOSTS.id().equals(groupId)&&!IMonGroup.TEMPLATES.id().equals(groupId))					
				groupsComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}
		// append hosts to form list
		CTweenBox hostsTweenBox = new CTweenBox(proxyForm, "hosts", Nest.value(data,"hosts").asCArray(),Defines.RDA_TEXTBOX_FILTER_SIZE);
		for(Map host : (CArray<Map>)Nest.value(data,"db_hosts").asCArray()) {
			if (empty(Nest.value(data,"exist_hosts",host.get("hostid")).$()))
				hostsTweenBox.addItem(Nest.value(host,"hostid").$(),Nest.value(host,"name").asString(),false);
		}
		for(Map host : (CArray<Map>)Nest.value(data,"exist_hosts").asCArray()) {
			hostsTweenBox.addItem(
				Nest.value(host,"hostid").$(),
				Nest.value(host,"name").asString(),
				null,
				true
			);
		}
		proxyFormList.addRow(_("Hosts"), hostsTweenBox.get(_("Proxy hosts"), array(_("Other hosts | Groups")+SPACE, groupsComboBox)));

		// append tabs to form
		CTabView proxyTab = new CTabView();
		proxyTab.addTab("proxyTab", _("Proxy"), proxyFormList);
		proxyForm.addItem(proxyTab);

		// append buttons to form
		if (!empty(Nest.value(data,"proxyid").$())) {
			proxyForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					new CButtonCancel()
				)
			));
		} else {
			proxyForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		}

		// append form to widget
		proxyWidget.addItem(proxyForm);

		return proxyWidget;
	}

}
