package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_BASIC;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_NTLM;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.HttpTestUtil;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CEditableComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
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

public class CConfigurationHttpconfEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.httpconf.edit.js",data);

		CWidget httpWidget = new CWidget();
		httpWidget.addPageHeader(_("CONFIGURATION OF WEB MONITORING"));

		// append host summary to widget header
		if (!empty(Nest.value(data,"hostid").$())) {
			httpWidget.addItem(get_header_host_table(idBean, executor, "web", Nest.value(data,"hostid").asLong(true)));
		}

		// create form
		CForm httpForm = new CForm();
		httpForm.setName("httpForm");
		httpForm.addVar("form", Nest.value(data,"form").$());
		httpForm.addVar("hostid", Nest.value(data,"hostid").$());
		httpForm.addVar("steps", Nest.value(data,"steps").$());
		httpForm.addVar("templated", Nest.value(data,"templated").$());

		if (!empty(Nest.value(data,"httptestid").$())) {
			httpForm.addVar("httptestid", Nest.value(data,"httptestid").$());
		}

		/* Scenario tab */
		CFormList httpFormList = new CFormList("httpFormList");

		// Parent http tests
		if (!empty(Nest.value(data,"templates").$())) {
			httpFormList.addRow(_("Parent web scenarios"), Nest.value(data,"templates").$());
		}

		// Name
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"templated").asBoolean(), 64);
		if (!Nest.value(data,"templated").asBoolean()) {
			nameTextBox.attr("autofocus", "autofocus");
		}
		httpFormList.addRow(_("Name"), nameTextBox);

		// Application
		if (!empty(Nest.value(data,"application_list").$())) {
			CArray applications = rda_array_merge(array(""), Nest.value(data,"application_list").asCArray());
			httpFormList.addRow(_("Application"),
				new CComboBox("applicationid", Nest.value(data,"applicationid").$(), null, applications)
			);
		} else {
			httpFormList.addRow(_("Application"), new CSpan(_("No applications found.")));
		}

		// New application
		httpFormList.addRow(_("New application"),
			new CTextBox("new_application", Nest.value(data,"new_application").asString(), RDA_TEXTBOX_STANDARD_SIZE), false, null, "new"
		);

		// Authentication
		CComboBox authenticationComboBox = new CComboBox("authentication", Nest.value(data,"authentication").$(), "submit();");
		authenticationComboBox.addItems(HttpTestUtil.httptest_authentications());
		httpFormList.addRow(_("Authentication"), authenticationComboBox);
		if (in_array(Nest.value(data,"authentication").asInteger(), array(HTTPTEST_AUTH_BASIC, HTTPTEST_AUTH_NTLM))) {
			httpFormList.addRow(_("User"), new CTextBox("http_user", Nest.value(data,"http_user").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64));
			httpFormList.addRow(_("Password"), new CTextBox("http_password", Nest.value(data,"http_password").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 64));
		}

		// update interval
		httpFormList.addRow(_("Update interval (in sec)"), new CNumericBox("delay", Nest.value(data,"delay").asString(), 5));

		// number of retries
		httpFormList.addRow(_("Retries"), new CNumericBox("retries", Nest.value(data,"retries").asString(), 2));

		// append http agents to form list - http://www.useragentstring.com
		CEditableComboBox agentComboBox = new CEditableComboBox("agent", Nest.value(data,"agent").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		agentComboBox.addItemsInGroup(_("Internet Explorer"), (CArray)map(
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)" , "Internet Explorer 10.0",
			"Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)" , "Internet Explorer 9.0",
			"Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)" , "Internet Explorer 8.0",
			"Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)" , "Internet Explorer 7.0",
			"Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1)" , "Internet Explorer 6.0"
		));
		agentComboBox.addItemsInGroup(_("Mozilla Firefox"), (CArray)map(
			"Mozilla/5.0 (X11; Linux i686; rv:8.0) Gecko/20100101 Firefox/8.0" , "Mozilla Firefox 8.0",
			"Mozilla/5.0 (X11; Linux i686; rv:7.0) Gecko/20100101 Firefox/7.0" , "Mozilla Firefox 7.0",
			"Mozilla/5.0 (X11; Linux i686; rv:6.0) Gecko/20100101 Firefox/6.0" , "Mozilla Firefox 6.0",
			"Mozilla/5.0 (X11; U; Linux i586; de; rv:5.0) Gecko/20100101 Firefox/5.0" , "Mozilla Firefox 5.0",
			"Mozilla/5.0 (X11; U; Linux x86_64; pl-PL; rv:2.0) Gecko/20110307 Firefox/4.0" , "Mozilla Firefox 4.0",
			"Mozilla/6.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:2.0.0.0) Gecko/20061028 Firefox/3.0" , "Mozilla Firefox 3.0",
			"Mozilla/5.0 (X11;U;Linux i686;en-US;rv:1.8.1) Gecko/2006101022 Firefox/2.0" , "Mozilla Firefox 2.0"
		));
		agentComboBox.addItemsInGroup(_("Opera"), (CArray)map(
			"Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00" , "Opera 12.00",
			"Opera/9.80 (X11; Linux x86_64; U; pl) Presto/2.7.62 Version/11.00" , "Opera 11.00",
			"Opera/9.80 (X11; Linux x86_64; U; en) Presto/2.2.15 Version/10.00" , "Opera 10.00",
			"Opera/9.00 (X11; Linux i686; U; pl)" , "Opera 9.00"
		));
		agentComboBox.addItemsInGroup(_("Safari"), (CArray)map(
			"Mozilla/5.0 (X11; U; Linux x86_64; en-us) AppleWebKit/531.2+ (KHTML, like Gecko) Version/5.0 Safari/531.2+" , "Safari 5.0",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; ru-RU) AppleWebKit/528.16 (KHTML, like Gecko) Version/4.0 Safari/528.16" , "Safari 4.0",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; sv-SE) AppleWebKit/523.13 (KHTML, like Gecko) Version/3.0 Safari/523.13" , "Safari 3.0",
			"Mozilla/5.0 (iPhone; U; CPU iPhone OS 2_1 like Mac OS X; fr-fr) AppleWebKit/525.18.1 (KHTML, like Gecko) Mobile/5F136" , "Safari on iPhone"
		));
		agentComboBox.addItemsInGroup(_("Google Chrome"), (CArray)map(
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.8 (KHTML, like Gecko) Chrome/17.0.940.0 Safari/535.8" , "Google Chrome 17",
			"Mozilla/5.0 (Windows NT 6.0; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.36 Safari/535.7" , "Google Chrome 16",
			"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.872.0 Safari/535.2" , "Google Chrome 15",
			"Mozilla/5.0 (X11; Linux i686) AppleWebKit/535.1 (KHTML, like Gecko) Ubuntu/11.04 Chromium/14.0.825.0 Chrome/14.0.825.0 Safari/535.1" , "Google Chrome 14",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_3) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.32 Safari/535.1" , "Google Chrome 13",
			"Mozilla/5.0 (Windows NT 6.1; en-US) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.750.0 Safari/534.30" , "Google Chrome 12"
		));
		agentComboBox.addItemsInGroup(_("Others"), (CArray)map(
			"Mozilla/5.0 (X11; Linux 3.1.0-rc9+; en_US) KHTML/4.7.2 (like Gecko) Konqueror/4.7" , "Konqueror 4.7",
			"Mozilla/5.0 (compatible; Konqueror/4.6; Linux) KHTML/4.6.0 (like Gecko)" , "Konqueror 4.6",
			"Lynx/2.8.7rel.1 libwww-FM/2.14 SSL-MM/1.4.1 OpenSSL/0.9.8r" , "Lynx 2.8.7rel.1",
			"Lynx/2.8.4rel.1 libwww-FM/2.14" , "Lynx 2.8.4rel.1",
			"Links (2.3pre1; Linux 2.6.35.10 i686; 225x51)" , "Links 2.3pre1",
			"Links (2.2; Linux 2.6.37.6-0.7-desktop i686; 225x51)" , "Links 2.2",
			"Googlebot/2.1 (+http://www.google.com/bot.html)" , "Googlebot"
		));
		httpFormList.addRow(_("Agent"), agentComboBox);

		// append HTTP proxy to form list
		CTextBox httpProxyTextBox = new CTextBox("http_proxy", Nest.value(data,"http_proxy").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 255);
		httpProxyTextBox.setAttribute("placeholder", "http://[username[:password]@]proxy.example.com[:port]");
		httpFormList.addRow(_("HTTP proxy"), httpProxyTextBox);

		// append status to form list
		httpFormList.addRow(_("Variables"), new CTextArea("variables", Nest.value(data,"variables").asString()));
		httpFormList.addRow(_("Enabled"), new CCheckBox("status", !Nest.value(data,"status").asBoolean()));

		/* Step tab */
		CFormList httpStepFormList = new CFormList("httpFormList");
		CTable stepsTable = new CTable(null, "formElementTable");
		stepsTable.setAttributes((CArray)map(
			"style" , "min-width: 500px;",
			"id" , "httpStepTable"
		));
		stepsTable.setHeader(array(
			new CCol(SPACE, null, null, "15"),
			new CCol(SPACE, null, null, "15"),
			new CCol(_("Name"), null, null, "150"),
			new CCol(_("Timeout"), null, null, "50"),
			new CCol(_("URL"), null, null, "200"),
			new CCol(_("Required"), null, null, "50"),
			new CCol(_("Status codes"), "nowrap", null, "90"),
			new CCol("", null, null, "50")
		));

		int i = 1;
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"steps").asCArray()).entrySet()) {
		    Object stepid = e.getKey();
		    Map step = e.getValue();
			if (!isset(step,"name")) {
				Nest.value(step,"name").$("");
			}
			if (!isset(step,"timeout")) {
				Nest.value(step,"timeout").$(15);
			}
			if (!isset(step,"url")) {
				Nest.value(step,"url").$("");
			}
			if (!isset(step,"posts")) {
				Nest.value(step,"posts").$("");
			}
			if (!isset(step,"required")) {
				Nest.value(step,"required").$("");
			}

			CSpan numSpan = new CSpan((i++)+":");
			numSpan.addClass("rowNum");
			numSpan.setAttribute("id", "current_step_"+stepid);

			CSpan name = new CSpan(Nest.value(step,"name").$(), "link");
			name.setAttributes((Map)map(
				"id" , "name_"+stepid,
				"name_step" , stepid
			));

			Object url = null;
			if (rda_strlen(Nest.value(step,"url").asString()) > 70) {
				url = new CSpan(substr(Nest.value(step,"url").asString(), 0, 35)+SPACE+"..."+SPACE+substr(Nest.value(step,"url").asString(), rda_strlen(Nest.value(step,"url").asString()) - 25, 25));
				((CSpan)url).setHint(Nest.value(step,"url").$());
			} else {
				url = Nest.value(step,"url").$();
			}

			Object removeButton = null;
			Object dragHandler = null;
			if (Nest.value(data,"templated").asBoolean()) {
				removeButton = SPACE;
				dragHandler = SPACE;
			} else {
				removeButton = new CButton("remove_"+stepid, _("Remove"), "javascript: removeStep(this);", "link_menu");
				((CButton)removeButton).setAttribute("remove_step", stepid);
				dragHandler = new CSpan(null, "ui-icon ui-icon-arrowthick-2-n-s move");
			}

			CRow row = new CRow(array(
				dragHandler,
				numSpan,
				name,
				Nest.value(step,"timeout").$()+SPACE+_("sec"),
				url,
				htmlspecialchars(Nest.value(step,"required").asString()),
				Nest.value(step,"status_codes").$(),
				removeButton
			), "sortable", "steps_"+stepid);

			stepsTable.addRow(row);
		}

		if (!Nest.value(data,"templated").asBoolean()) {
			stepsTable.addRow(new CCol(new CButton("add_step", _("Add"), null, "link_menu"), null, 8));
		}

		httpStepFormList.addRow(_("Steps"), new CDiv(stepsTable, "objectgroup inlineblock border_dotted ui-corner-all"));

		// append tabs to form
		CTabView httpTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			httpTab.setSelected("0");
		}
		httpTab.addTab("scenarioTab", _("Scenario"), httpFormList);
		httpTab.addTab("stepTab", _("Steps"), httpStepFormList);
		httpForm.addItem(httpTab);

		// append buttons to form
		if (!empty(Nest.value(data,"httptestid").$())) {
			httpForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					Nest.value(data,"templated").asBoolean() ? null : new CButtonDelete(_("Delete scenario?"), url_param(idBean, "form")+url_param(idBean, "httptestid")+url_param(idBean, "hostid")),
					new CButtonCancel(url_param(idBean, "hostid"))
				)
			));
		} else {
			httpForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		}
		httpWidget.addItem(httpForm);

		return httpWidget;
	}

}
