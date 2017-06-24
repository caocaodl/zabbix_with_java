package com.isoft.iradar.web.action.moncenter;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_API_CFN;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CEILOMETER;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CEILOMETER_COMPUTE;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CENTRAL;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CEPH_OSD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CINDER_API;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CINDER_SCHEDULER;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_COLLECTOR;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CONDUCTOR;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_CONSOLEAUTH;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_ENGINE;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_EVALUATOR;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_HEAT_API;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_HTTPD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_LIBVIRTD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_MEMCACHED;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_MESSAGEBUS;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_MYSQLD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_AGENT;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_DHCP;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_IPSEC;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_L3_AGENT;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_LBAAS;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_METADATA;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_OPENVSWITCH;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NET_VPN;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOTIFICATION;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOTIFIER;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOVA_API;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOVA_CERT;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOVA_COMPUTE;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_NOVNCPROXY;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_OPENSTACK_GLANCE;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_OPENSTACK_GLANCE_REGISTRY;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_OPENSTACK_KEYSTONE;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_OPENVSWITCH;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_OPENVSWITCH_AGENT;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_SCHEDULER;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_SERVER;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_SPICEHTML5PROXY;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_SPS;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_SPSAGENT;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_TGTD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_VOLUME;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD_WEB_HTTPD;
import static com.isoft.iradar.common.util.ItemsKey.CLOUD__CEPH_MON;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CObject;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.bean.Column;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 监控中心_简单列表页面
 */
public abstract class A_SimpleDataAction extends A_BasedDataAction {

	public int scrollPagCount = 20;
	public static Map vmStatusMap = null;
	
	@Override
	protected void doInitPage() {
		page("title", _("Latest data"));
		page("file", getSimpleAction());
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("js", new String[] {"imon/scrollpagination.js"});
		page("css", new String[] { "lessor/supervisecenter/supervise.css" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray fields = map("groupid",array(T_RDA_INT, O_OPT, P_SYS, DB_ID, null),
							"hostid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID, null), 
							"tenantid",		array(T_RDA_STR, O_OPT, null, null, null), 
							"tenant",		array(T_RDA_STR, O_OPT, null, null, null), 
							"filter_set",	array(T_RDA_STR, O_OPT, P_SYS, null, null), 
							"filter_rst",	array(T_RDA_STR, O_OPT, P_SYS, null, null), 
							"filter_field",	array(T_RDA_STR, O_OPT, null, null, null),
							"filter_field_value",array(T_RDA_STR, O_OPT, null, null, null), 
							"filter_exact",		 array(T_RDA_INT, O_OPT, null, "IN(0,1)", null), 
							"name",				 array(T_RDA_STR, O_OPT, null, NOT_EMPTY, null, "名称"),
							"output",		array(T_RDA_STR, O_OPT, null, null, null), 
							"start",		array(T_RDA_STR, O_OPT, null, null, null));
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if("ajax".equals(Nest.value(_REQUEST, "output").asString())){
			CTableInfo table = getTableInfo(executor);
			CArray<String> items =  CArray.valueOf(table.items);
			String contenant = CObject.unpack_object(items).toString();
			AjaxResponse ajaxResponse = new AjaxResponse();
			Map response = map("contenant",contenant,
							   "start",empty(contenant)?Nest.value(_REQUEST, "start").asInteger():Nest.value(_REQUEST, "start").asInteger()+scrollPagCount);
			ajaxResponse.success(response);
			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		Long groupid = getHostGroupId();
		CPageFilter pageFilter = getPageFilter(executor);
		boolean isCloudHost = IMonConsts.MON_VM.equals(groupid);
		CWidget overviewWidget = new CWidget();
		CForm headerForm = new CForm("post", getSimpleAction());
		if (isCloudHost) {
			headerForm.addItem(array(SPACE + _("Tenant") + SPACE, pageFilter.getTenantsCB()));
		}
		headerForm.addItem(array(SPACE + _("Host") + SPACE, pageFilter.getHostsCB()));

		// 类型 显示详情页面和简单页面
		CForm btnForm = new CForm("post", getLatestAction());
		btnForm.addVar("pageType", 1);
		btnForm.addVar("groupid", pageFilter.$("groupid").asString());
		btnForm.addVar("hostid", pageFilter.$("hostid").asString());
		if (isCloudHost) {
			btnForm.addVar("tenantid", pageFilter.$("tenantid").asString());
		}
		
		CButton button = new CButton("pageButton", SPACE, "submit()", "details_pic simpleness_pic");
		button.setName("pageButton");
		btnForm.addItem(button);// 切换按钮
		
		overviewWidget.addHeader(headerForm,btnForm);

		CTableInfo table = getTableInfo(executor);
		
		overviewWidget.addItem(array(table));
		overviewWidget.show();
		JsUtil.insert_js("jQuery(function() {scrollPagTool(\'scrollPagBody\',\'"+this.getSimpleAction()+"\',\'"+0+"\',\'0\',\'"+get_request("tenantid", "0")+"\');});", false);
	}

	public CTableInfo getTableInfo(SQLExecutor executor){
		CArray configs = (CArray) CONFIGS.get(getSimpleAction());
		CArray<Column> columns = (CArray) configs.get("columns");

		Long groupid = getHostGroupId();

		boolean isCloudNode = IMonConsts.MON_CLOUD_CONTROLER.equals(groupid)
				|| IMonConsts.MON_CLOUD_COMPUTER.equals(groupid)
				|| IMonConsts.MON_CLOUD_CEPH.equals(groupid)
				|| IMonConsts.MON_CLOUD_NETWORK.equals(groupid)
				|| IMonConsts.MON_CLOUD_WEB.equals(groupid);

		boolean isCloudHost = IMonConsts.MON_VM.equals(groupid);
		if(isCloudHost){
			vmStatusMap = DataDriver.getAllVmStatus(executor);
		}

		// table
		CTableInfo table = getTableWithHeader(groupid,columns,isCloudNode,isCloudHost);
		if(empty(Nest.value(_REQUEST, "output").asString()))
			return table;
		
		String tenantid = get_request("tenantid", "0");

		CPageFilter pageFilter = getPageFilter(executor);
		Nest.value(_REQUEST, "groupid").$(pageFilter.$("groupid").asString());
		Nest.value(_REQUEST, "hostid").$(pageFilter.$("hostid").asString());

		// get hosts
		Long[] selectedHostIds = null;
		if (!empty(Nest.value(_REQUEST, "hostid").$())) {
			selectedHostIds = Nest.array(_REQUEST, "hostid").asLong();
		} else if (pageFilter.$("hostsSelected").asBoolean()) {
			selectedHostIds = pageFilter.$("hosts").asCArray().keysAsLong();
		} else {
			selectedHostIds = new Long[0];
		}

		// 获取设备
		CHostGet options = new CHostGet();
		options.setOutput(new String[] { "name", "hostid", "status", "host", "available", "ipmi_available", "snmp_available", "jmx_available" });
		options.setGroupIds(groupid);
		if (selectedHostIds != null && selectedHostIds.length > 0) {
			options.setHostIds(selectedHostIds);
		}
		options.setSelectInterfaces(new String[] { "ip" });
		options.setPreserveKeys(true);
		options.setSortfield(get_request("name", "name"));
		options.setSortorder(get_request("sortorder", RDA_SORT_UP));
		if (tenantid != null && tenantid.length() > 1) {
			options.setFilter("tenantid", tenantid);
		}

		CArray<Map> hosts = API.Host(this.getIdBean(), executor).get(options,true,Nest.value(_REQUEST, "start").asString()!=null&&0!=Nest.value(_REQUEST, "start").asInteger()?Nest.value(_REQUEST, "start").asInteger():0,EasyObject.asInteger(scrollPagCount));

		CImg normal_img = new CImg("images/gradients/normal.png");
		CImg anormal_img = new CImg("images/gradients/anormal.png");
		CImg unknown_img = new CImg("styles/themes/originalblue/images/iradar/unknown.png");

		int available = 0;
		int ipmi = 0;
		int snmp = 0;
		int jmx = 0;
		int isAvailable = 1;
		String vmStates = null;
		for (Map hostMap : hosts) {
			Long c_hostid = Nest.value(hostMap, "hostid").asLong();
			Object hostname = hostMap.get("name");
			String IP = Nest.value(hostMap, "interfaces", "0", "ip").asString();

			// 简单列表页面服务器名称添加到连接设备详情页面的连接
			CLink hostLink = new CLink(hostname, "host_detail.action?hostid=" + c_hostid + "&groupid=" + groupid);
			CArray cells = null;
			if (groupid.toString().equals(IMonConsts.MON_CLUSTER.toString())
					|| groupid.toString().equals(IMonConsts.MON_DESKTOPC.toString())) {
				cells = array(hostname, IP);
			} else {
				cells = array(hostLink, IP);
			}

			if (!isCloudNode) {
				for (Column column : columns) {
					Object text = column.cell(c_hostid);

					CArray<String> attrs = column.attrs();
					if (column.attrs() == null) {
						cells.add(text);
					} else {
						CCol c = new CCol(text);
						for (Entry<Object, String> entry : attrs.entrySet()) {
							c.attr(String.valueOf(entry.getKey()),
									entry.getValue());
						}
						cells.add(c);
					}
				}

				if (isCloudHost) { // 虚拟机
//					vmStates = LatestValueHelper.buildByNormalKey(c_hostid, ItemsKey.STATUS_VM.getValue()).value().out().format(); // 云主机状态
//					vmStates = Nest.value(vmStatusMap, c_hostid).asString();
//					if(empty(vmStates)){
//						vmStates = LatestValueHelper.NA;
//					}
//					if ("ACTIVE".equals(vmStates) || " ".equals(vmStates)) {
//						isAvailable = 1; // 运行
//					}else if("SUPENDED".equals(vmStates)){
//						isAvailable = 5; // 挂起
//					}else if("PAUSED".equals(vmStates)){
//						isAvailable = 3; // 暂停
//					}else if("SHUTOFF".equals(vmStates)){
//						isAvailable = 4; // 关机
//					}else if ("--".equals(vmStates)) {
//						isAvailable = 0;
//						vmStates = _("Unknown");
//					} else {
//						isAvailable = 2; // 不可用
//					}
					isAvailable = getVmStatus(vmStatusMap,c_hostid);
				} else {
					available = Nest.value(hostMap, new Object[] { "available" }).asInteger();
					ipmi = Nest.value(hostMap, new Object[] { "ipmi_available" }).asInteger();
					snmp = Nest.value(hostMap, new Object[] { "snmp_available" }).asInteger();
					jmx = Nest.value(hostMap, new Object[] { "jmx_available" }) .asInteger();
					if (available == HOST_AVAILABLE_UNKNOWN
							&& ipmi == HOST_AVAILABLE_UNKNOWN
							&& snmp == HOST_AVAILABLE_UNKNOWN
							&& jmx == HOST_AVAILABLE_UNKNOWN) {// 未知的
						isAvailable = 0;
					} else if (available == HOST_AVAILABLE_TRUE
							|| ipmi == HOST_AVAILABLE_TRUE
							|| snmp == HOST_AVAILABLE_TRUE
							|| jmx == HOST_AVAILABLE_TRUE) { // 可用的
						isAvailable = 1;
					} else {// 不可用
						isAvailable = 2;
					}
				}
				if(isCloudHost){
					switch(isAvailable){
					case 0:
						cells.add(array(unknown_img, _("Unknown")));
						break;
					case 2:
						cells.add(array(anormal_img, _("Not available")));
						break;
					default:
						cells.add(array(normal_img, _("Available")));
						break;
					}
					switch (isAvailable) {
					case 0:
						cells.add(array(unknown_img, _("Unknown")));
						break;
					case 1:
						cells.add(array(normal_img, _("ACTIVE")));
						break;
					case 2:
						cells.add(array(unknown_img, _("Unknown")));
						break;
					case 5:
						cells.add(array(anormal_img, _("SUPENDED")));
						break;
					case 3:
						cells.add(array(anormal_img, _("PAUSED")));
						break;
					case 4:
						cells.add(array(anormal_img, _("SHUTOFF")));
						break;
					default:
						break;
					}
				}else{					
					switch (isAvailable) {
					case 0:
						cells.add(array(unknown_img, _("Unknown")));
						break;
					case 1:
						cells.add(array(normal_img, _("Available")));
						break;
					case 2:
						cells.add(array(anormal_img, _("Not available")));
						break;
					default:
						break;
					}
				}
				table.addRow(cells);
			} else {
				CLink hostNameLink = new CLink(hostname, "host_detail.action?hostid=" + c_hostid + "&groupid=" + groupid);
				table = getCloudTypedTableInfo(table, groupid, hostNameLink, IP, c_hostid.toString());
			}
		}
		return table;
	}
	
	public CPageFilter getPageFilter(SQLExecutor executor){
		Long groupid = getHostGroupId();
		boolean isCloudHost = IMonConsts.MON_VM.equals(groupid);

		CArray pfparams = map(
			"hosts", map("templated_hosts", true, "editable", false), 
			"hostid", get_request("hostid", null), 
			"groups", map(), 
			"groupid", groupid
		);

		if (isCloudHost) {
			pfparams.put("tenants", map("withVms", true));
			pfparams.put("tenantid", get_request("tenantid", "0"));
		}
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, pfparams);
		return pageFilter;
	}
	
	/**
	 * 生成云服务表格数据
	 * @param table
	 * @param groupid
	 * @param hostname
	 * @param ip
	 * @return
	 */
	private CTableInfo getCloudTypedTableInfo(CTableInfo table, Long groupid,
			CLink hostname, String ip, String hostid) {
		CArray<String> server = array(); // 服务
		CArray<String> status = array(); // 状态
		if (IMonConsts.MON_CLOUD_CONTROLER.equals(groupid)) {
			server = array(_("openstack-keystone"),
					_("memcached"),
					_("mysqld"), 
					_("openstack-glance-api"),
					_("openstack-glance-registry"), 
					_("openstack-nova-api"),
					_("openstack-nova-cert"),
					_("openstack-nova-consoleauth"),
					_("openstack-nova-scheduler"),
					_("openstack-nova-conductor"),
					_("neutron-server"),
					_("openstack-cinder-api"),
					_("openstack-cinder-scheduler"),
					_("openstack-heat-api"),
					_("openstack-heat-api-cfn"),
					_("openstack-heat-engine"),
					_("openstack-ceilometer-api"),
					_("openstack-ceilometer-notification"),
					_("openstack-ceilometer-central"),
					_("openstack-ceilometer-collector"),
					_("openstack-ceilometer-alarm-evaluator"),
					_("openstack-ceilometer-alarm-notifier"),
					_("httpd"),
					_("sps-api"), 
					_("openstack-nova-spicehtml5proxy"),
					_("openstack-nova-novncproxy"));

			status = array(CLOUD_OPENSTACK_KEYSTONE.getValue(),
					CLOUD_MEMCACHED.getValue(), 
					CLOUD_MYSQLD.getValue(),
					CLOUD_OPENSTACK_GLANCE.getValue(),
					CLOUD_OPENSTACK_GLANCE_REGISTRY.getValue(),
					CLOUD_NOVA_API.getValue(), 
					CLOUD_NOVA_CERT.getValue(),
					CLOUD_CONSOLEAUTH.getValue(), 
					CLOUD_SCHEDULER.getValue(),
					CLOUD_CONDUCTOR.getValue(),
					CLOUD_SERVER.getValue(),
					CLOUD_CINDER_API.getValue(),
					CLOUD_CINDER_SCHEDULER.getValue(),
					CLOUD_HEAT_API.getValue(), 
					CLOUD_API_CFN.getValue(),
					CLOUD_ENGINE.getValue(), 
					CLOUD_CEILOMETER.getValue(),
					CLOUD_NOTIFICATION.getValue(), 
					CLOUD_CENTRAL.getValue(),
					CLOUD_COLLECTOR.getValue(), 
					CLOUD_EVALUATOR.getValue(),
					CLOUD_NOTIFIER.getValue(), 
					CLOUD_HTTPD.getValue(),
					CLOUD_SPS.getValue(), 
					CLOUD_SPICEHTML5PROXY.getValue(),
					CLOUD_NOVNCPROXY.getValue());

		} else if (IMonConsts.MON_CLOUD_COMPUTER.equals(groupid)) {
			server = array(_("libvirtd"), 
					_("messagebus"),
					_("openstack-nova-compute"),
					_("neutron-openvswitch-agent"),
					_("openvswitch"),
					_("openstack-cinder-volume"), 
					_("tgtd"),
					_("openstack-ceilometer-compute"),
					_("SPSAGENT")
					);

			status = array(CLOUD_LIBVIRTD.getValue(),
					CLOUD_MESSAGEBUS.getValue(), 
					CLOUD_NOVA_COMPUTE.getValue(),
					CLOUD_OPENVSWITCH_AGENT.getValue(),
					CLOUD_OPENVSWITCH.getValue(), 
					CLOUD_VOLUME.getValue(),
					CLOUD_TGTD.getValue(), 
					CLOUD_CEILOMETER_COMPUTE.getValue(),
					CLOUD_SPSAGENT.getValue()
					);

		} else if (IMonConsts.MON_CLOUD_CEPH.equals(groupid)) {
			server = array(_("ceph-mon"), _("ceph-osd"));
			status = array(CLOUD__CEPH_MON.getValue(), CLOUD_CEPH_OSD.getValue());
		} else if (IMonConsts.MON_CLOUD_NETWORK.equals(groupid)) {
			server = array(_("openvswitch"), 
					_("neutron-openvswitch-agent"),
					_("neutron-l3-agent"), 
					_("neutron-dhcp-agent"),
					_("neutron-metadata-agent"),
					_("neutron-vpn-agent"),
					_("neutron-lbaas-agent"), 
					_("ipsec"));
			status = array(CLOUD_NET_OPENVSWITCH.getValue(),
					CLOUD_NET_AGENT.getValue(), 
					CLOUD_NET_L3_AGENT.getValue(),
					CLOUD_NET_DHCP.getValue(), 
					CLOUD_NET_METADATA.getValue(),
					CLOUD_NET_VPN.getValue(), 
					CLOUD_NET_LBAAS.getValue(),
					CLOUD_NET_IPSEC.getValue());

		} else if (IMonConsts.MON_CLOUD_WEB.equals(groupid)) {
			server = array(_("iaas.horizon.httpd"));
			status = array(CLOUD_WEB_HTTPD.getValue());
		}

		CArray cells = null;
		String state = null;
		CImg normal_img = new CImg("images/gradients/normal.png");
		CImg anormal_img = new CImg("images/gradients/anormal.png");
		CImg unknown_img = new CImg("styles/themes/originalblue/images/iradar/unknown.png");
		CArray statesArray = null;
		for (int i = 0; i < server.size(); i++) {
			state = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), status.get(i)).value().out().format();
			if (state.contains("激活") && !state.contains("未激活")) {
				statesArray = array(normal_img, "激活");
			} else if ("--".equals(state)) {
				statesArray = array(unknown_img, _("Unknown"));
			} else {
				statesArray = array(anormal_img, "未激活");
			}
			cells = array(hostname, ip, server.get(i), statesArray);
			table.addRow(cells);
			cells.clear();
		}
		return table;
	}

	private CTableInfo getTableWithHeader(Long groupid,CArray<Column> columns,boolean isCloudNode,boolean isCloudHost){
		CTableInfo table = new CTableInfo(_("No values found."));
		table.attr("id", "scrollPagBody");
		if (IMonConsts.MON_VM.equals(groupid)) {
			table.setAttribute("class", table.getAttribute("class").toString() + " cloudhost");
		} 
		/**
		 * 系统屏蔽默认的存储设备类型
		 */
//		else if (IMonConsts.MON_STORAGE.equals(groupid)) {
//			table.setAttribute("class", table.getAttribute("class").toString() + " storagedevice");
//		}
		table = getTableClassName(table, groupid.toString());

		// 设置表格header
		CArray header = new CArray();
		header.add(make_sorting_header(_("hostName"), "name"));
		header.add(_("IP"));
		if (!isCloudNode) {
			for (Column column : columns) {
				header.add(column.header());
			}
			header.add(_("Runing State"));
			if(isCloudHost)			
				header.add(_("the state of running"));
		} else {
			header.add(_("hostServer"));
			header.add(_("hostStates"));
		}
		table.setHeader(header);
		return table;
	}
	
	/**
	 * 根据不同设备类型 返回唯一的样式
	 * @param table
	 * @param groupid
	 * @return
	 */
	private CTableInfo getTableClassName(CTableInfo table, String groupid) {
		String tbClass = table.getAttribute("class").toString();
		if (IMonConsts.MON_DB_Oracle.toString().equals(groupid)) {
			tbClass += " supervise_oracle";
		} else if (IMonConsts.MON_NET_CISCO.toString().equals(groupid)) {
			tbClass += " supervise_cisco";
		} else if (IMonConsts.MON_DB_SQLSERVER.toString().equals(groupid)) {
			tbClass += " supervise_sqlserver";
		} else if (IMonConsts.MON_MIDDLE_TOMCAT.toString().equals(groupid)) {
			tbClass += " supervise_tomcat";
		} else if (IMonConsts.MON_CLOUD_CONTROLER.toString().equals(groupid)) {
			tbClass += " supervise_control";
		} else if (IMonConsts.MON_CLOUD_CEPH.toString().equals(groupid)) {
			tbClass += " supervise_storage";
		} else if (IMonConsts.MON_CLOUD_COMPUTER.toString().equals(groupid)) {
			tbClass += " supervise_calculate";
		} else if (IMonConsts.MON_CLOUD_NETWORK.toString().equals(groupid)) {
			tbClass += " supervise_net";
		} else if (IMonConsts.MON_CLOUD_WEB.toString().equals(groupid)) {
			tbClass += " supervise_door";
		} else if (IMonConsts.MON_CLUSTER.toString().equals(groupid)) {
			tbClass += " supervise_group";
		} else if (IMonConsts.MON_DESKTOPC.toString().equals(groupid)) {
			tbClass += " supervise_desktop";
		} else if (IMonConsts.MON_DB_MySQL.toString().equals(groupid)) {
			tbClass += " supervise_mysql";
		} else if (IMonConsts.MON_COMMON_NET.toString().equals(groupid)) {
			tbClass += " supervise_general";
		}

		table.setAttribute("class", tbClass);
		return table;
	}
	
	public static int getVmStatus(Map vmStatusMap,Long c_hostid){
		String vmStates = Nest.value(vmStatusMap, c_hostid).asString();
		if(empty(vmStates)){
			vmStates = LatestValueHelper.buildByNormalKey(c_hostid, ItemsKey.STATUS_VM.getValue()).value().out().format(); // 云主机状态
			vmStates = Nest.value(vmStatusMap, c_hostid).asString();
		}
		if ("ACTIVE".equals(vmStates) || " ".equals(vmStates)) {
			return 1; // 运行
		}else if("SUPENDED".equals(vmStates)){
			return 5; // 挂起
		}else if("PAUSED".equals(vmStates)){
			return 3; // 暂停
		}else if("SHUTOFF".equals(vmStates)){
			return 4; // 关机
		}else if ("--".equals(vmStates)) {
			return 0;
//			vmStates = _("Unknown");
		} else {
			return 2; // 不可用
		}
	}
}
