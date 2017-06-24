package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_PROXY;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.updateHostStatus;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ProxiesAction extends RadarBaseAction {
	
	private CArray<Map> dbProxy;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of proxies"));
		page("file", "proxies.action");
		page("hist_arg", new String[] { "" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"proxyid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"host",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Proxy name")),
			"status",			array(T_RDA_INT, O_OPT, null,	BETWEEN(HOST_STATUS_PROXY_ACTIVE,HOST_STATUS_PROXY_PASSIVE), "isset({save})"),
			"interface",		array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&{status}=="+HOST_STATUS_PROXY_PASSIVE),
			"hosts",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// actions
			"go",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",	array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "host", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST,"proxyid")) {
			CProxyGet poptions = new CProxyGet();
			poptions.setProxyIds(get_request_asLong("proxyid"));
			poptions.setSelectHosts(new String[]{"hostid", "host"});
			poptions.setSelectInterface(API_OUTPUT_EXTEND);
			poptions.setOutput(API_OUTPUT_EXTEND);
			dbProxy = API.Proxy(getIdentityBean(), executor).get(poptions);
			if (empty(dbProxy)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"hosts") || !isArray(Nest.value(_REQUEST,"hosts").$())) {
				access_deny();
			} else {
				CProxyGet poptions = new CProxyGet();
				poptions.setProxyIds(Nest.array(_REQUEST,"hosts").asLong());
				poptions.setSelectHosts(new String[]{"hostid", "host"});
				poptions.setSelectInterface(API_OUTPUT_EXTEND);
				poptions.setCountOutput(true);
				long dbProxyChk = API.Proxy(getIdentityBean(), executor).get(poptions);
				if (dbProxyChk != count(Nest.value(_REQUEST,"hosts").$())) {
					access_deny();
				}
			}
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
			final Map proxy = map(
				"host", get_request("host"),
				"status", get_request("status"),
				"interface", get_request("interface")
			);

			// skip discovered hosts
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(get_request("hosts", array()).valuesAsLong());
			hoptions.setOutput(new String[]{"hostid"});
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			Nest.value(proxy,"hosts").$(API.Host(getIdentityBean(), executor).get(hoptions));
			
			DBstart(executor);

			CArray<Long[]> proxyIds;
			int action;
			String msgOk,msgFail;
			if (isset(_REQUEST,"proxyid")) {
				Nest.value(proxy,"proxyid").$(Nest.value(_REQUEST,"proxyid").$());
				proxyIds = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Proxy(getIdentityBean(), executor).update(array(proxy));
					}
				}, null);

				action = AUDIT_ACTION_UPDATE;
				msgOk = _("Proxy updated");
				msgFail = _("Cannot update proxy");
			} else {
				proxyIds = Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Proxy(getIdentityBean(), executor).create(array(proxy));
					}
				}, null);

				action = AUDIT_ACTION_ADD;
				msgOk = _("Proxy added");
				msgFail = _("Cannot add proxy");
			}

			boolean result = !empty(proxyIds);
			result = DBend(executor, result);

			show_messages(result, msgOk, msgFail);
			clearCookies(result);

			if (result) {
				add_audit(getIdentityBean(), executor, action, AUDIT_RESOURCE_PROXY, "["+Nest.value(_REQUEST,"host").asString()+"] ["+reset(Nest.value(proxyIds,"proxyids").asCArray())+"]");
				unset(_REQUEST,"form");
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"delete")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Proxy(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"proxyid").asLong()));
				}
			});

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"proxyid");
			}

			show_messages(result, _("Proxy deleted"), _("Cannot delete proxy"));
			clearCookies(result);

			unset(_REQUEST,"delete");
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"proxyid")) {
			unset(_REQUEST,"proxyid");
			unset(_REQUEST,"hosts");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("hosts")) {
			boolean result = true;
			boolean enable = ("activate".equals(get_request("go")));
			final int status = enable ? HOST_STATUS_MONITORED : HOST_STATUS_NOT_MONITORED;
			CArray<String> hosts = get_request("hosts", array());

			DBstart(executor);
			
			int updated = 0;
			for(String hostId : hosts) {
				Map params = new HashMap();
				params.put("hostId", hostId);
				CArray<Map> dbHosts = DBselect(executor,
					"SELECT h.hostid,h.status"+
					" FROM hosts h"+
					" WHERE h.proxy_hostid=#{hostId}",
					params
				);
				for(final Map dbHost : dbHosts) {
					int oldStatus = Nest.value(dbHost,"status").asInteger();
					updated++;

					if (oldStatus == status) {
						continue;
					}

					result &= Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return updateHostStatus(getIdentityBean(), executor, Nest.array(dbHost,"hostid").asLong(), status);
						}
					});
					
					if (!result) {
						continue;
					}
				}
			}

			result = (result && !empty(hosts));
			result = DBend(executor, result);

			String messageSuccess = enable
				? _n("Host enabled", "Hosts enabled", updated)
				: _n("Host disabled", "Hosts disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable host", "Cannot enable hosts", updated)
				: _n("Cannot disable host", "Cannot disable hosts", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"hosts")) {
			DBstart(executor);
			
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Proxy(getIdentityBean(), executor).delete(get_request("hosts",array()).valuesAsLong()));
				}
			});
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("Proxy deleted"), _("Cannot delete proxy"));
			clearCookies(goResult);
		}

		/* Display */
		CArray data;
		if (isset(_REQUEST,"form")) {
			data = map(
				"form", get_request("form", "1"),
				"form_refresh", get_request("form_refresh", 0) + 1,
				"proxyid", get_request("proxyid", 0),
				"name", get_request("host", ""),
				"status", get_request("status", HOST_STATUS_PROXY_ACTIVE),
				"hosts", get_request("hosts", array()),
				"interface", get_request("interface", array()),
				"proxy", array()
			);

			// proxy
			if (!empty(Nest.value(data,"proxyid").$())) {
				Map proxy = reset(dbProxy);

				if (!isset(_REQUEST,"form_refresh")) {
					Nest.value(data,"name").$(Nest.value(proxy,"host").$());
					Nest.value(data,"status").$(Nest.value(proxy,"status").$());
					Nest.value(data,"interface").$(Nest.value(proxy,"interface").$());
					Nest.value(data,"hosts").$(rda_objectValues(Nest.value(proxy,"hosts").$(), "hostid"));
				}
			}

			// interface
			if (Nest.value(data,"status").asInteger() == HOST_STATUS_PROXY_PASSIVE && empty(Nest.value(data,"interface").$())) {
				Nest.value(data,"interface").$(map(
					"dns", "localhost",
					"ip", "127.0.0.1",
					"useip", 1,
					"port", "10051"
				));
			}

			// fetch available hosts, skip host prototypes
			CArray<Map> dbHosts = DBselect(executor,
				"SELECT h.hostid,h.proxy_hostid,h.name,h.flags"+
				" FROM hosts h"+
				" WHERE h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")"+
					" AND h.flags<>"+RDA_FLAG_DISCOVERY_PROTOTYPE
			);
			Nest.value(data,"dbHosts").$(dbHosts);
			order_result(dbHosts, "name");

			// render view
			CView proxyView = new CView("administration.proxy.edit", data);
			proxyView.render(getIdentityBean(), executor);
			proxyView.show();
		} else {
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			data = map(
				"config", config
			);

			String sortfield = getPageSortField(getIdentityBean(), executor,"host");

			CProxyGet poptions = new CProxyGet();
			poptions.setEditable(true);
			poptions.setSelectHosts(new String[]{"hostid", "host", "name", "status"});
			poptions.setOutput(API_OUTPUT_EXTEND);
			poptions.setSortfield(sortfield);
			poptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> proxies = API.Proxy(getIdentityBean(), executor).get(poptions);
			Nest.value(data,"proxies").$(rda_toHash(proxies, "proxyid"));

			CArray proxyIds = array_keys(proxies);

			// sorting & paging
			order_result(proxies, sortfield, getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, proxies, array("proxyid")));

			// calculate performance
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbPerformance = DBselect(executor,
				"SELECT h.proxy_hostid,SUM(CAST(1.0/i.delay AS DECIMAL(20,10))) AS qps"+
				" FROM items i,hosts h"+
				" WHERE i.status="+ITEM_STATUS_ACTIVE+
					" AND i.hostid=h.hostid"+
					" AND h.status="+HOST_STATUS_MONITORED+
					" AND i.delay<>0"+
					" AND i.flags<>"+RDA_FLAG_DISCOVERY_PROTOTYPE+
					" AND "+sqlParts.dual.dbConditionInt("h.proxy_hostid", proxyIds.valuesAsLong())+
				" GROUP BY h.proxy_hostid",
				sqlParts.getNamedParams()
			);
			for(Map performance : dbPerformance) {
				if (isset(proxies,performance.get("proxy_hostid"))) {
					Nest.value(proxies,performance.get("proxy_hostid"),"perf").$(round(Nest.value(performance,"qps").asFloat(), 2));
				}
			}

			// get items
			CItemGet ioptions = new CItemGet();
			ioptions.setProxyIds(proxyIds.valuesAsLong());
			ioptions.setGroupCount(true);
			ioptions.setCountOutput(true);
			ioptions.setWebItems(true);
			ioptions.setMonitored(true);
			CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);
			for(Map item : items) {
				if (isset(proxies,item.get("proxy_hostid"))) {
					if (!isset(Nest.value(proxies,item.get("proxy_hostid"),"item_count").$())) {
						Nest.value(proxies,item.get("proxy_hostid"),"item_count").$(0);
					}
					Nest.value(proxies,item.get("proxy_hostid"),"item_count").$(
							Nest.value(proxies,item.get("proxy_hostid"),"item_count").asInteger()
							+
							Nest.value(item,"rowscount").asInteger()
					);
				}
			}

			// render view
			CView proxyView = new CView("administration.proxy.list", data);
			proxyView.render(getIdentityBean(), executor);
			proxyView.show();
		}
	}

}
