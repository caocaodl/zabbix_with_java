package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.core.g.RDA_SERVER;
import static com.isoft.iradar.core.g.RDA_SERVER_PORT;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_AGGREGATE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_DB_MONITOR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_EXTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_INTERNAL;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_IRADAR_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SIMPLE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.QUEUE_DETAILS;
import static com.isoft.iradar.inc.Defines.QUEUE_DETAIL_ITEM_COUNT;
import static com.isoft.iradar.inc.Defines.QUEUE_OVERVIEW;
import static com.isoft.iradar.inc.Defines.QUEUE_OVERVIEW_BY_PROXY;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TIMEOUT;
import static com.isoft.iradar.inc.Defines.RDA_SOCKET_BYTES_LIMIT;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_cookie;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.TranslateDefines.QUEUE_DATE_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.server.IRadarServer;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class QueueAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Queue"));
		page("file", "queue.action");
		page("hist_arg", new String[] { "config" });
		
		define("RDA_PAGE_DO_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray queueModes = array(
			QUEUE_OVERVIEW,
			QUEUE_OVERVIEW_BY_PROXY,
			QUEUE_DETAILS
		);
		
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"config", array(T_RDA_INT, O_OPT, P_SYS, IN(queueModes), null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		int config = get_request("config", (Integer)CProfile.get(getIdentityBean(), executor, "web.queue.config", 0));
		CProfile.update(getIdentityBean(), executor, "web.queue.config", config, PROFILE_TYPE_INT);
		
		// fetch data
		IRadarServer iradarServer = null;
		try {
			iradarServer = new IRadarServer(RDA_SERVER, RDA_SERVER_PORT, RDA_SCRIPT_TIMEOUT, RDA_SOCKET_BYTES_LIMIT);
			Map<Integer,String> queueRequests = (Map)map(
				QUEUE_OVERVIEW, IRadarServer.QUEUE_OVERVIEW,
				QUEUE_OVERVIEW_BY_PROXY, IRadarServer.QUEUE_OVERVIEW_BY_PROXY,
				QUEUE_DETAILS, IRadarServer.QUEUE_DETAILS
			);
			Object oqueueData = iradarServer.getQueue(queueRequests.get(config), get_cookie("rda_sessionid"));
			
			// check for errors error
			if (!empty(iradarServer.getError())) {
				error(iradarServer.getError());
				show_error_message(_("Cannot display item queue."));
				return;
			}
			
			// create filter form
			CForm form = new CForm("get");
			CComboBox cmbMode = new CComboBox("config", config, "submit();");
			cmbMode.addItem(QUEUE_OVERVIEW, _("Overview"));
			cmbMode.addItem(QUEUE_OVERVIEW_BY_PROXY, _("Overview by proxy"));
			cmbMode.addItem(QUEUE_DETAILS, _("Details"));
			form.addItem(cmbMode);
	
			// display table
			CWidget queueWidget = new CWidget();
			queueWidget.addPageHeader(_("QUEUE OF ITEMS TO BE UPDATED"), form);
	
			CTableInfo table = new CTableInfo(_("The queue is empty."));
	
			// overview
			if (config == QUEUE_OVERVIEW) {
				CArray<Integer> itemTypes = array(
					ITEM_TYPE_IRADAR,
					ITEM_TYPE_IRADAR_ACTIVE,
					ITEM_TYPE_SIMPLE,
					ITEM_TYPE_SNMPV1,
					ITEM_TYPE_SNMPV2C,
					ITEM_TYPE_SNMPV3,
					ITEM_TYPE_INTERNAL,
					ITEM_TYPE_AGGREGATE,
					ITEM_TYPE_EXTERNAL,
					ITEM_TYPE_DB_MONITOR,
					ITEM_TYPE_IPMI,
					ITEM_TYPE_SSH,
					ITEM_TYPE_TELNET,
					ITEM_TYPE_JMX,
					ITEM_TYPE_CALCULATED
				);
	
				table.setHeader(array(
					_("Items"),
					_("5 seconds"),
					_("10 seconds"),
					_("30 seconds"),
					_("1 minute"),
					_("5 minutes"),
					_("More than 10 minutes")
				));
	
				CArray<Map> queueData = rda_toHash(oqueueData, "itemtype");
				Map itemTypeData;
				for(int type : itemTypes) {
					if (isset(queueData,type)) {
						itemTypeData = queueData.get(type);
					} else {
						itemTypeData = map(
							"delay5", 0,
							"delay10", 0,
							"delay30", 0,
							"delay60", 0,
							"delay300", 0,
							"delay600", 0
						);
					}
	
					table.addRow(array(
						item_type2str(type),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_NOT_CLASSIFIED, Nest.value(itemTypeData,"delay5").$(), !Nest.value(itemTypeData,"delay5").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_INFORMATION, Nest.value(itemTypeData,"delay10").$(), !Nest.value(itemTypeData,"delay10").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_WARNING, Nest.value(itemTypeData,"delay30").$(), !Nest.value(itemTypeData,"delay30").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_AVERAGE, Nest.value(itemTypeData,"delay60").$(), !Nest.value(itemTypeData,"delay60").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_HIGH, Nest.value(itemTypeData,"delay300").$(), !Nest.value(itemTypeData,"delay300").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_DISASTER, Nest.value(itemTypeData,"delay600").$(), !Nest.value(itemTypeData,"delay600").asBoolean())
					));
				}
			} else if (config == QUEUE_OVERVIEW_BY_PROXY) {// overview by proxy
				CProxyGet poptions = new CProxyGet();
				poptions.setOutput(new String[]{"hostid", "host"});
				poptions.setPreserveKeys(true);
				CArray<Map> proxies = API.Proxy(getIdentityBean(), executor).get(poptions);
				order_result(proxies, "host");
	
				Nest.value(proxies,0).$(map("host", _("Server")));
	
				table.setHeader(array(
					_("Proxy"),
					_("5 seconds"),
					_("10 seconds"),
					_("30 seconds"),
					_("1 minute"),
					_("5 minutes"),
					_("More than 10 minutes")
				));
	
				CArray<Map> queueData = rda_toHash(oqueueData, "proxyid");
				Map proxyData;
				for (Entry<Object, Map> e : proxies.entrySet()) {
				    Object proxyId = e.getKey();
				    Map proxy = e.getValue();
					if (isset(queueData,proxyId)) {
						proxyData = queueData.get(proxyId);
					} else {
						proxyData = map(
							"delay5", 0,
							"delay10", 0,
							"delay30", 0,
							"delay60", 0,
							"delay300", 0,
							"delay600", 0
						);
					}
	
					table.addRow(array(
						Nest.value(proxy,"host").$(),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_NOT_CLASSIFIED, Nest.value(proxyData,"delay5").$(), !Nest.value(proxyData,"delay5").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_INFORMATION, Nest.value(proxyData,"delay10").$(), !Nest.value(proxyData,"delay10").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_WARNING, Nest.value(proxyData,"delay30").$(), !Nest.value(proxyData,"delay30").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_AVERAGE, Nest.value(proxyData,"delay60").$(), !Nest.value(proxyData,"delay60").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_HIGH, Nest.value(proxyData,"delay300").$(), !Nest.value(proxyData,"delay300").asBoolean()),
						getSeverityCell(getIdentityBean(), executor,TRIGGER_SEVERITY_DISASTER, Nest.value(proxyData,"delay600").$(), !Nest.value(proxyData,"delay600").asBoolean())
					));
				}
			} else if (config == QUEUE_DETAILS) {// details
				CArray<Map> queueData = rda_toHash(oqueueData, "itemid");
	
				CItemGet ioptions = new CItemGet();
				ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_"});
				ioptions.setSelectHosts(new String[]{"name"});
				ioptions.setItemIds(array_keys(queueData).valuesAsLong());
				ioptions.setWebItems(true);
				ioptions.setPreserveKeys(true);
				CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);
	
				items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor,items);
	
				table.setHeader(array(
					_("Scheduled check"),
					_("Delayed by"),
					_("Host"),
					_("Name")
				));
	
				int i = 0;
				for(Map itemData : queueData) {
					if (!isset(items,itemData.get("itemid"))) {
						continue;
					}
	
					// display only the first 500 items
					i++;
					if (i > QUEUE_DETAIL_ITEM_COUNT) {
						break;
					}
	
					Map item = items.get(itemData.get("itemid"));
					Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
	
					table.addRow(array(
						rda_date2str(QUEUE_DATE_FORMAT, Nest.value(itemData,"nextcheck").asLong()),
						rda_date2age(Nest.value(itemData,"nextcheck").asLong()),
						Nest.value(host,"name").$(),
						Nest.value(item,"name_expanded").$()
					));
				}
			}
	
			queueWidget.addItem(table);
			queueWidget.show();
	
			// display the table footer
			if (config == QUEUE_OVERVIEW_BY_PROXY) {
				show_table_header(_("Total")+": "+table.getNumRows());
			} else if (config == QUEUE_DETAILS) {
				show_table_header(
					_("Total")+": "+table.getNumRows()+
					((count(oqueueData) > QUEUE_DETAIL_ITEM_COUNT) ? " ("+_("Truncated")+")" : "")
				);
			}
		} finally {
			if (iradarServer != null) {
				iradarServer.close();
			}
		}
	}

}
