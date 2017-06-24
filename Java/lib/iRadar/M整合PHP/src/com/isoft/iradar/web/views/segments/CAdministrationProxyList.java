package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationProxyList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget proxyWidget = new CWidget();

		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create proxy")));
		proxyWidget.addPageHeader(_("CONFIGURATION OF PROXIES"), createForm);
		proxyWidget.addHeader(_("Proxies"));
		proxyWidget.addHeaderRowNumber();

		// create form
		CForm proxyForm = new CForm("get");
		proxyForm.setName("proxyForm");

		// create table
		CTableInfo proxyTable = new CTableInfo(_("No proxies found."));
		proxyTable.setHeader(array(
			new CCheckBox("all_hosts", false, "checkAll(\""+proxyForm.getName()+"\", \"all_hosts\", \"hosts\");"),
			make_sorting_header(_("Name"), "host"),
			_("Mode"),
			_("Last seen (age)"),
			_("Host count"),
			_("Item count"),
			_("Required performance (vps)"),
			_("Hosts")
		));

		for(Map proxy : (CArray<Map>)Nest.value(data,"proxies").asCArray()) {
			CArray hosts = array();

			if (!empty(Nest.value(proxy,"hosts").$())) {
				int i = 1;

				for(Map host : (CArray<Map>)Nest.value(proxy,"hosts").asCArray()) {
					if (i > Nest.value(data,"config","max_in_table").asInteger()) {
						break;
					}
					i++;

					String style = null;
					if (Nest.value(host,"status").asInteger() == HOST_STATUS_MONITORED) {
						style  = "off";
					} else if (Nest.value(host,"status").asInteger() == HOST_STATUS_TEMPLATE) {
						style = "unknown";
					} else {
						style = "on";
					}

					hosts.add(new CLink(Nest.value(host,"name").$(), "hosts.action?form=update&hostid="+Nest.value(host,"hostid").$(), style));
					hosts.add(", ");
				}

				array_pop(hosts);
			}

			String lastAccess = "-";
			if (isset(Nest.value(proxy,"lastaccess").$())) {
				lastAccess = (Nest.value(proxy,"lastaccess").asInteger() == 0) ? "-" : rda_date2age(Nest.value(proxy,"lastaccess").asLong());
			}

			proxyTable.addRow(array(
				new CCheckBox("hosts["+proxy.get("proxyid")+"]", false, null, Nest.value(proxy,"proxyid").asInteger()),
				isset(Nest.value(proxy,"host").$()) ? new CLink(Nest.value(proxy,"host").$(), "proxies.action?form=update&proxyid="+Nest.value(proxy,"proxyid").$()) : "",
				(isset(Nest.value(proxy,"status").$()) && Nest.value(proxy,"status").asInteger() == HOST_STATUS_PROXY_ACTIVE) ? _("Active") : _("Passive"),
				lastAccess,
				isset(Nest.value(proxy,"host").$()) ? count(Nest.value(proxy,"hosts").$()) : "",
				isset(Nest.value(proxy,"item_count").$()) ? Nest.value(proxy,"item_count").$() : 0,
				isset(Nest.value(proxy,"perf").$()) ? Nest.value(proxy,"perf").$() : "-",
				new CCol((empty(hosts) ? "-" : hosts), "wraptext")
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable hosts monitored by selected proxies?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable hosts monitored by selected proxies?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected proxies?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");

		// append table to form
		proxyForm.addItem(array(Nest.value(data,"paging").$(), proxyTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		proxyWidget.addItem(proxyForm);

		return proxyWidget;
	}

}
