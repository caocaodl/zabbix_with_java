package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.core.EventsAction;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationProxyList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget proxyWidget = new CWidget();

		// create form
		CForm proxyForm = new CForm("get");
		proxyForm.setName("proxyForm");
		
		CToolBar tb = new CToolBar(proxyForm);
		
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addItem(new CSubmit("form", _("Create proxy"), "", "orange create"));
		
		tb.addForm(createForm);
		
		CArray<CComboItem> goComboBox = array();
		// create go buttons
		CComboItem goOption =  new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected proxies?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);

		rda_add_post_js("chkbxRange.pageGoName = \"hosts\";");
		
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		proxyWidget.addItem(headerActions);	

		// create table
		CTableInfo proxyTable = new CTableInfo(_("No proxies found."));
		proxyTable.setHeader(array(
			new CCheckBox("all_hosts", false, "checkAll(\""+proxyForm.getName()+"\", \"all_hosts\", \"hosts\");"),
			make_sorting_header(_("Proxy ip"), "host"),
			make_sorting_header(_("Name"), "name"),
			_("Last seen (age)"),
			_("Host count"),
			_("Item count"),
			//_("Required performance (vps)"),
			_("Proxy hosts")
		));

		for(Map proxy : (CArray<Map>)Nest.value(data,"proxies").asCArray()) {
			CArray hosts = array();
			
			String pstyle = null;
			if (Nest.value(proxy,"status").asInteger() == HOST_STATUS_PROXY_ACTIVE) {
				pstyle  = "on";
			} else if (Nest.value(proxy,"status").asInteger() == HOST_STATUS_PROXY_PASSIVE) {
				pstyle = "off";
			} else {
				pstyle = "";
			}
			
			
			
			if (!empty(Nest.value(proxy,"hosts").$())) {
				
				CHostGet hget = new CHostGet();
				hget.setOutput(new String[]{"name", "hostid", "status"});
				hget.setSelectGroups(new String[]{"groupid","name"});
				hget.setHostIds(FuncsUtil.rda_objectValues(Nest.value(proxy, "hosts").asCArray(),"hostid").valuesAsLong());
				CArray<Map> hostsCA = API.Host(idBean, executor).get(hget);
				hostsCA = FuncsUtil.rda_toHash(hostsCA, "hostid");
				
//				int i = 1;
				for(Map host : (CArray<Map>)Nest.value(proxy,"hosts").asCArray()) {
//					if (i > Nest.value(data,"config","max_in_table").asInteger()) {
//						break;
//					}
//					i++;
					
					Map hostDetail = hostsCA.get(host.get("hostid"));
					
					String style = null;
					if (Nest.value(host,"status").asInteger() == HOST_STATUS_MONITORED) {
						style  = "on";
					} else if (Nest.value(host,"status").asInteger() == HOST_STATUS_TEMPLATE) {
						style = "unknown";
					} else {
						style = "off";
					}
					String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
					
					
					if(empty(Nest.value(host, "ipmi_password").$())){
//						String url = "'"+_("Cloud host")+"', '"+common_action_with_context+ "mon_vm.action?ddreset=1&hostid="+Nest.value(host,"hostid").$()+"'";
//						hosts.add(new CLink(Nest.value(host,"name").$(),IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),style,null,Boolean.TRUE));
//						hosts.add(", ");
						hosts.add(EventsAction.getHostDetailUrl(hostDetail));
						hosts.add(", ");
					}
				}

				array_pop(hosts);
			}

			String lastAccess = "-";
			if (isset(Nest.value(proxy,"lastaccess").$())) {
				lastAccess = (Nest.value(proxy,"lastaccess").asInteger() == 0) ? "-" : rda_date2age(Nest.value(proxy,"lastaccess").asLong());
			}

			proxyTable.addRow(array(
				new CCheckBox("hosts["+proxy.get("proxyid")+"]", false, null, Nest.value(proxy,"proxyid").asInteger()),
				isset(Nest.value(proxy,"host").$()) ? new CLink(Nest.value(proxy,"host").$(), "proxies.action?form=update&proxyid="+Nest.value(proxy,"proxyid").$(),  pstyle) : "",
				empty(Nest.value(proxy,"name").$())?"":Nest.value(proxy,"name").$(),
				lastAccess,
				isset(Nest.value(proxy,"host").$()) ? count(Nest.value(proxy,"hosts").$()) : "",
				isset(Nest.value(proxy,"item_count").$()) ? Nest.value(proxy,"item_count").$() : 0,
				//isset(Nest.value(proxy,"perf").$()) ? Nest.value(proxy,"perf").$() : "-",
				new CCol((empty(hosts) ? "-" : hosts), "wraptext")
			));
		}



		// append table to form
		proxyForm.addItem(array(proxyTable, Nest.value(data,"paging").$()));

		// append form to widget
		proxyWidget.addItem(proxyForm);

		return proxyWidget;
	}

}
