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
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
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
import java.util.regex.Pattern;

import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ProxiesAction extends RadarBaseAction {
	
	private CArray<Map> dbProxy;
	private String regex = "^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5]))$";
	private Pattern pattern = Pattern.compile(regex);
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of proxies"));
		page("file", "proxies.action");
		page("hist_arg", new String[] { "" });
		page("css", new String[] { "lessor/toolmanage/proxies.css" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Nest.value(_REQUEST, "status").$(Defines.HOST_STATUS_PROXY_ACTIVE);
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"proxyid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"host",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Proxy ip")),
			"proxyName",		array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Proxy name")),
			"status",			array(T_RDA_INT, O_OPT, null,	BETWEEN(HOST_STATUS_PROXY_ACTIVE,HOST_STATUS_PROXY_PASSIVE), "isset({save})"),
			"interface",		array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&{status}=="+HOST_STATUS_PROXY_PASSIVE),
			"hosts",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"twb_groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// actions
			"go",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",		array(T_RDA_STR, O_OPT, null,	null,		null)
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
				"name", get_request("proxyName"),
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
				msgOk = _("Proxy updated");
				msgFail = _("Cannot update proxy");
			} else {
				msgOk = _("Proxy added");
				msgFail = _("Cannot add proxy");
			}
			if(pattern.matcher(Nest.value(proxy, "host").asString()).matches()){
				if (isset(_REQUEST,"proxyid")) {
					Nest.value(proxy,"proxyid").$(Nest.value(_REQUEST,"proxyid").$());
					proxyIds = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Proxy(getIdentityBean(), executor).update(array(proxy));
						}
					}, null);

					action = AUDIT_ACTION_UPDATE;
				} else {
					proxyIds = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Proxy(getIdentityBean(), executor).create(array(proxy));
						}
					}, null);

					action = AUDIT_ACTION_ADD;
				}

				boolean result = !empty(proxyIds);
				result = DBend(executor, result);

				show_messages(result, msgOk, msgFail);
				clearCookies(result);

				if (result) {
					add_audit(getIdentityBean(), executor, action, AUDIT_RESOURCE_PROXY, "添加监控代理"+"["+Nest.value(_REQUEST,"host").asString()+"]");
					unset(_REQUEST,"form");
				}
			}else{
				info(_("the ip is not correct"));
				show_messages(false, msgOk, msgFail);
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
			final int hstatus = enable ? HOST_STATUS_MONITORED : HOST_STATUS_NOT_MONITORED;
			CArray<String> hosts = get_request("hosts", array());

			DBstart(executor);
			
			int updated = 0;
			for(String hostId : hosts) {
				Map params = new HashMap();
				params.put("hostId", hostId);
				CArray<Map> dbHosts = DBselect(executor,
					"SELECT h.tenantid,h.hostid,h.status"+
					" FROM hosts h"+
					" WHERE h.proxy_hostid=#{hostId}",
					params
				);
				for(final Map dbHost : dbHosts) {
					int oldStatus = Nest.value(dbHost,"status").asInteger();
					updated++;

					if (oldStatus == hstatus) {
						continue;
					}
					
					final String tenantid = Nest.value(dbHost,"tenantid").asString();
					@SuppressWarnings("serial")
					final IdentityBean tidBean = new IdentityBean(){
						@Override
						public String getTenantId() {
							return tenantid;
						}

						@Override
						public Role getTenantRole() {
							return Role.TENANT;
						}
					};
					result &= Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return updateHostStatus(tidBean, executor, Nest.array(dbHost,"hostid").asLong(), hstatus);
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
				? _n("Cloud host enabled proxy", "Cloud hosts enabled proxy", updated)
				: _n("Cloud host disabled proxy", "Cloud hosts disabled proxy", updated);
			String messageFailed = enable
				? _n("Cannot enable cloud host proxy", "Cannot enable cloud hosts proxy", updated)
				: _n("Cannot disable cloud host proxy", "Cannot disable cloud hosts proxy", updated);

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
				"alias", get_request("proxyName", ""),
				"status", get_request("status", HOST_STATUS_PROXY_ACTIVE),
				"hosts", get_request("hosts", array()),
				"interface", get_request("interface", array()),
				"proxy", array(),
				"twb_groupid", get_request("twb_groupid", 0)
			);

			// proxy
			if (!empty(Nest.value(data,"proxyid").$())) {
				Map proxy = reset(dbProxy);

				if (!isset(_REQUEST,"form_refresh")) {
					Nest.value(data,"name").$(Nest.value(proxy,"host").$());
					Nest.value(data,"alias").$(Nest.value(proxy,"name").$());
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
			Map params = new HashMap();
			if (!empty(Nest.value(data,"proxyid").$())) {
				params.put("proxyid", Nest.value(data,"proxyid").$());
			}
			
			CHostGet hoptions = new CHostGet();
			if(!empty(Nest.value(data,"twb_groupid").$()))
				hoptions.setGroupIds(Nest.value(data,"twb_groupid").asLong());
			hoptions.setSortfield("name");
			hoptions.setEditable(false);
			hoptions.setOutput(API_OUTPUT_EXTEND);
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			CArray<Map> db_hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"db_hosts").$(db_hosts);
			
			hoptions = new CHostGet();
			hoptions.setSortfield("name");
			hoptions.setHostIds(Nest.value(data, "hosts").asCArray().valuesAsLong());
			hoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			CArray<Map> existHosts = array();
			for(Map host:hosts)
				existHosts.put(Nest.value(host, "hostid").asLong(), host);
			Nest.value(data,"exist_hosts").$(existHosts);
			
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setSortfield("name");
			hgoptions.setEditable(true);
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> db_groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"db_groups").$(db_groups);

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
			String sortorder = getPageSortOrder(getIdentityBean(), executor);

			CProxyGet poptions = new CProxyGet();
			poptions.setEditable(true);
			poptions.setSelectHosts(new String[]{"hostid", "host", "name", "status","ipmi_password"});
			poptions.setOutput(API_OUTPUT_EXTEND);
			poptions.setSortfield(sortfield);
			poptions.setSortorder(sortorder);
			poptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			poptions.setPreserveKeys(true);
			CArray<Map> proxies = API.Proxy(getIdentityBean(), executor).get(poptions);
			Nest.value(data,"proxies").$(proxies);

			CArray proxyIds = array_keys(proxies);

			// sorting & paging
			order_result(proxies, sortfield, getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, Clone.deepcopy(proxies), array("proxyid")));

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

	/**
	 * 创建新用户实例
	 * @param oldIBean
	 * @return
	 */
	public IdentityBean buildNewIdBean(IIdentityBean oldIBean){
		IdentityBean idbean = new IdentityBean();
		Map uinfo = new HashMap();
		uinfo.put("tenantId", "-");
		uinfo.put("osTenantId", "0");
		uinfo.put("tenantRole", oldIBean.getTenantRole().magic());
		uinfo.put("userId",  oldIBean.getUserId());
		uinfo.put("userName", oldIBean.getUserName());
		uinfo.put("admin", "Y");
		uinfo.put("osUser", ((Map)this.getSession().getAttribute(CWebUser.class.getName())).get("osUser"));
		idbean.init(uinfo);
		
		return idbean;
	}
	
}
