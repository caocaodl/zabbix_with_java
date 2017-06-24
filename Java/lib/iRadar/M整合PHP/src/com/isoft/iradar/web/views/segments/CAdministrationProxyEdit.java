package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
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
		proxyWidget.addPageHeader(_("CONFIGURATION OF PROXIES"));

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
		proxyFormList.addRow(_("Proxy name"), nameTextBox);

		// append status to form list
		CComboBox statusBox = new CComboBox("status", Nest.value(data,"status").$(), "submit()");
		statusBox.addItem(HOST_STATUS_PROXY_ACTIVE, _("Active"));
		statusBox.addItem(HOST_STATUS_PROXY_PASSIVE, _("Passive"));
		proxyFormList.addRow(_("Proxy mode"), statusBox);

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

		// append hosts to form list
		CTweenBox hostsTweenBox = new CTweenBox(proxyForm, "hosts", Nest.value(data,"hosts").asCArray());
		for(Map host : (CArray<Map>)Nest.value(data,"dbHosts").asCArray()) {
			// show only normal hosts, and discovered hosts monitored by the current proxy
			// for new proxies display only normal hosts
			if ((!empty(Nest.value(data,"proxyid").$()) && idcmp(Nest.value(data,"proxyid").$(), Nest.value(host,"proxy_hostid").$())) || Nest.value(host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
				hostsTweenBox.addItem(
					Nest.value(host,"hostid").$(),
					Nest.value(host,"name").asString(),
					null,
					empty(Nest.value(host,"proxy_hostid").$()) || (!empty(Nest.value(data,"proxyid").$()) && bccomp(Nest.value(host,"proxy_hostid").$(), Nest.value(data,"proxyid").$()) == 0 && Nest.value(host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL)
				);
			}
		}
		proxyFormList.addRow(_("Hosts"), hostsTweenBox.get(_("Proxy hosts"), _("Other hosts")));

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
					new CButtonDelete(_("Delete proxy?"), url_param(idBean, "form")+url_param(idBean, "proxyid")),
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
