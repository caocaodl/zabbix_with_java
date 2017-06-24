package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.getDeletableHostGroups;
import static com.isoft.iradar.inc.HostsUtil.get_hostgroup_by_groupid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.common.HostTypeDAO;
import com.isoft.biz.daoimpl.radar.CUserDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.segments.CConfigurationHostgroupsEdit;
import com.isoft.model.FuncItem;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.utils.CacheUtil;
import com.isoft.web.bean.common.HostTypeAction;

public class HostgroupsAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of host groups"));
		page("file", "hostgroups.action");
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/devicecenter/hostgroups.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groups",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostids",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groupids",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// group
			"groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name",				array(T_RDA_STR, O_OPT, null,	"(/^[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]+$/.test({}))",	"isset({save})", "类型名称"),
			"twb_groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"moncategory",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// actions
			"go",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// other
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",	array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		boolean result = false;
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"groupid")) {
			/*unset(_REQUEST,"groupid");*/
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			CArray hostIds = get_request("hosts", array());
			CArray templateIds = get_request("templates", array());

			CArray<Map> hostsByGroup = array();
			if(!empty(Nest.value(_REQUEST,"groupid").$())){
				CHostGet hoptions = new CHostGet();
				hoptions.setEditable(true);
//				hoptions.setHostIds(hostIds.valuesAsLong());
				hoptions.setGroupIds(Nest.value(_REQUEST, "groupid").asLong());
				hoptions.setOutput(new String[]{"hostid"});
				hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
				hostsByGroup = API.Host(getIdentityBean(), executor).get(hoptions);
			}
			final CArray<Map> hosts = FuncsUtil.rda_array_merge(hostsByGroup);
			
			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(hostIds.valuesAsLong());
			toptions.setOutput(new String[]{"templateid"});
			final CArray<Map>  templates = API.Template(getIdentityBean(), executor).get(toptions);

			String msgOk,msgFail;
			if (!empty(Nest.value(_REQUEST,"groupid").$())) {
				DBstart(executor);

				try {
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
					hgoptions.setOutput(API_OUTPUT_EXTEND);
					CArray<Map> oldGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
					Map oldGroup = reset(oldGroups);

					result = true;
					// don't try to update the name for a discovered host group
					if (Nest.value(oldGroup,"flags").asInteger() != RDA_FLAG_DISCOVERY_CREATED) {
						result = API.Call(new Wrapper<Boolean>() {
							@Override protected Boolean doCall() throws Throwable {//加入call方法，捕获异常的方法，给界面提示
								return !empty(API.HostGroup(getIdentityBean(), executor).update(array((Map)map(
										"groupid", Nest.value(_REQUEST,"groupid").$(),
										"name", Nest.value(_REQUEST,"name").$()
									))));
							}
						});
					}
					
					CArray<Map> groups = null;
					if (result) {
						hgoptions = new CHostGroupGet();
						hgoptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
						hgoptions.setOutput(API_OUTPUT_EXTEND);
						final CArray groups_tmp = groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
						result = Call(new Wrapper<Boolean>() {
							@Override protected Boolean doCall() throws Throwable {
								return !empty(API.HostGroup(getIdentityBean(), executor).massUpdate(map(
									"hosts", hosts,
									"templates", templates,
									"groups", groups_tmp
								)));
							}
						});
					}

					
					
					if (result) {
						Map group = reset(groups);

						add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_HOST_GROUP, Nest.value(group,"groupid").asLong(), Nest.value(group,"name").asString(),
							"groups", (Map)map("name", Nest.value(oldGroup,"name").$()), (Map)map("name", Nest.value(group,"name").$()));
						//修改菜单项
						updateFunc(group);
						
						//修改设备类型对应的的默认监控模型
						updateGroupTemplates(executor, Nest.value(_REQUEST,"groupid").asLong(), templateIds);
					}
				} catch (Exception e) {
					result = false;
				}
				result = DBend(executor, result);
				
				msgOk = _("Group updated");
				msgFail = _("Cannot update group");
			} else {
				DBstart(executor);
				
				try {
					CArray hostgroup = Call(new Wrapper<CArray>() {
						@Override
						protected CArray doCall() throws Throwable {
							return API.HostGroup(getIdentityBean(), executor).create(array((Map)map("name" , Nest.value(_REQUEST,"name").$())));
						}
					}, null);
					result = !empty(hostgroup);
					
					if (result) {
						CHostGroupGet hgoptions = new CHostGroupGet();
						hgoptions.setGroupIds(Nest.array(hostgroup,"groupids").asLong());
						hgoptions.setOutput(API_OUTPUT_EXTEND);
						final CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

						result = !empty(hostgroup);

						if (result) {
							Map group = reset(groups);
							add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_HOST_GROUP, Nest.value(group,"groupid").asLong(), Nest.value(group,"name").asString(), null, null, null);
							
							hostgroup = addFuncWithRights(executor, getIdentityBean(), hosts, templates, groups, group);
							
							//保存设备类型对应的的默认监控模型
							if(!templateIds.isEmpty()){
								addGroupTemplates(executor, Nest.value(hostgroup,"groupids").asLong(), templateIds);
							}	
						}
					}
				} catch (Exception e) {
					result = false;
				}
				
				result = DBend(executor, result);

				msgOk = _("Group added");
				msgFail = _("Cannot add group");
			}

			show_messages(result, msgOk, msgFail);

			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result);
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"groupid")) {
			//系统内置的类型不能删除
			//修改页面单删除时  设置是否可删除该设备类型
			getDeleteStatus(executor);
			if(isset(_REQUEST,"deleteStatus")  && "N".equals(Nest.value(_REQUEST, "deleteStatus").asString())){
				result = false;
			}else{
				if(doGroupMaintenanceCheck(executor,Nest.value(_REQUEST,"groupid").asLong())){
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							Long groupid = Nest.value(_REQUEST,"groupid").asLong();
							CUserDAO.removeDefaultRights(executor, array(groupid));
							return !empty(API.HostGroup(getIdentityBean(), executor).delete(groupid));
						}
					});
				}else{
					result = false;
					FuncsUtil.info(_("the maintenance of groups is not null"));
				}
			}
				
			show_messages(result, _("Group deleted"), _("Cannot delete group"));
			
			if(result){
				List idList = new ArrayList();
				idList.add(Nest.value(_REQUEST,"groupid").asString());
				//删除菜单项
				deleteFunc(idList);		
				//删除类型对应模型
				deleteGroupTemplates(executor ,idList);
			}

			
			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result);
			}
			unset(_REQUEST,"delete");
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			//系统内置的类型不能删除
			//列表页面批量删除时 设置可删除的组id
			boolean goResult;
			getGroupids(executor);
			if(isset(_REQUEST, "groups") && Nest.value(_REQUEST, "groups").asCArray().isEmpty()){
				goResult=false;
			}else{
				if(doGroupMaintenanceCheck(executor,Nest.value(_REQUEST, "groups").asCArray().valuesAsLong())){
					goResult = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							Long[] groupids = get_request("groups", array()).valuesAsLong();
							CUserDAO.removeDefaultRights(executor, CArray.valueOf(groupids));
							return !empty(API.HostGroup(getIdentityBean(), executor).delete(groupids));
						}
					});	
				}else{
					goResult = false;
					FuncsUtil.info(_("the maintenance of groups is not null"));
				}
			}

			show_messages(goResult, _("Group deleted"), _("Cannot delete group"));
			
			
			if(goResult){
				List idList = new ArrayList();
				Long[] ids = get_request("groups", array()).valuesAsLong();
				for(Long l:ids){
					idList.add(l.toString());
				}
				//删除菜单项
				deleteFunc(idList);	
				
				//删除类型对应模型
				deleteGroupTemplates(executor, idList);
			}
			
			clearCookies(goResult);
		} else if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean enable = ("activate".equals(get_request("go")));
			final int status = enable ? HOST_STATUS_MONITORED : HOST_STATUS_NOT_MONITORED;
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;

			CArray groups = get_request("groups", array());

			if (!empty(groups)) {
				DBstart(executor);
				
				CHostGet hoptions = new CHostGet();
				hoptions.setGroupIds(groups.valuesAsLong());
				hoptions.setEditable(true);
				hoptions.setOutput(API_OUTPUT_EXTEND);
				final CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);

				if (!empty(hosts)) {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Host(getIdentityBean(), executor).massUpdate(map(
									"hosts", hosts,
									"status", status
								)));
						}
					});

					if (result) {
						for(Map host : hosts) {
							add_audit_ext(
								getIdentityBean(), 
								executor,
								auditAction,
								AUDIT_RESOURCE_HOST,
								Nest.value(host,"hostid").asLong(),
								Nest.value(host,"host").asString(),
								"hosts",
								(Map)map("status", Nest.value(host,"status").$()),
								(Map)map("status", status)
							);
						}
					}
				} else {
					result = true;
				}
				
				result = DBend(executor, result);

				int updated = count(hosts);

				String messageSuccess = enable
					? _n("Host enabled", "Hosts enabled", updated)
					: _n("Host disabled", "Hosts disabled", updated);
				String messageFailed = enable
					? _n("Cannot enable host", "Cannot enable hosts", updated)
					: _n("Cannot disable host", "Cannot disable hosts", updated);

				show_messages(result, messageSuccess, messageFailed);
				clearCookies(result);
			}
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"form", get_request("form"),
				"groupid", get_request("groupid", 0),
				"hosts", get_request("hosts", array()),
				"templates", get_request("templates", array()),
				"name", get_request("name", ""),
				"twb_groupid", get_request("twb_groupid", -1),
				"moncategory", get_request("moncategory", IMonConsts.MON_CATE_SERVER)
			);

			if (Nest.value(data,"groupid").asInteger() > 0 && empty(Nest.value(data,"name").$())) {//10945,点击其他类型，避免已选择默认类型为空
				//获得已选中监控模型
				Map gts = new HashMap();
				gts.put("groupid", Nest.value(data,"groupid").asLong());
				List<Map> groupTemplatesList = new HostTypeDAO(executor).doGroupId(gts);
				gts = new HashMap<Object, Map>();
				if(groupTemplatesList.size()>0){
					for(Map m: groupTemplatesList){
						gts.put(m.get("hostid"), m);
					}
				}
				CArray<Map> r_templates = new CArray<Map>(gts); 
				Nest.value(data,"templates").$(rda_toHash(rda_objectValues(r_templates, "hostid"), "hostid"));
			}
			
			if (Nest.value(data,"groupid").asInteger() > 0) {
				Nest.value(data,"group").$(get_hostgroup_by_groupid(getIdentityBean(), executor, Nest.value(data,"groupid").asString()));
				// if first time select all hosts for group from db
				if (!isset(_REQUEST,"form_refresh")) {
					Nest.value(data,"name").$(Nest.value(data,"group","name").$());
					CHostGet hoptions = new CHostGet();
					hoptions.setEditable(!IMonGroup.MON_VM.id().equals(Nest.value(data,"groupid").asLong()));
					hoptions.setGroupIds(Nest.value(data,"groupid").asLong());
					hoptions.setTemplatedHosts(true);
					hoptions.setOutput(new String[]{"hostid"});
					CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
					Nest.value(data,"hosts").$(hosts);
					Nest.value(data,"hosts").$(rda_toHash(rda_objectValues(hosts, "hostid"), "hostid"));
				}
				Nest.value(data,"moncategory").$(CConfigurationHostgroupsEdit.getIsSysFunc(data)?Nest.value(MoncategoryUtil.Moncategory_Map, Nest.value(data,"groupid").asLong()).$():getPidByFuncId(executor,Nest.value(data,"groupid").asString()));
			}

			// get all possible groups
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setNotProxyHosts(true);
			hgoptions.setSortfield("name");
			hgoptions.setEditable(true);
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> db_groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"db_groups").$(db_groups);
			//获得监控模型的groupid
			for(Map map: db_groups){
				if("Templates".equals(map.get("name"))){
					Nest.value(data, "templates_groupid").$(Nest.value(map,"groupid").$());
					break;
				}
			}
			//获得所有监控模型
			CHostGet options = new CHostGet();
			options.setGroupIds(Nest.value(data,"templates_groupid").asLong());		//默认监控模型获取所有的监控模型 并不只是Templates分组下（）
			options.setTemplatedHosts(true);
			options.setSortfield("name");
			options.setEditable(true);
			options.setOutput(API_OUTPUT_EXTEND);
			options.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			CArray<Map> templates = API.Host(getIdentityBean(), executor).get(options);
			Nest.value(data,"db_templates").$(templates);

		
			
			if ("clone".equals(Nest.value(_REQUEST,"form").asString())) {
				unset(data,"groupid");
			}			
			
		/*	// get all possible hosts
			CHostGet hoptions = new CHostGet();
			if(!empty(Nest.value(data,"twb_groupid").$()) && Nest.value(data,"twb_groupid").asInteger() != -1){
				hoptions.setGroupIds(Nest.value(data,"twb_groupid").asLong());
			}
			//hoptions.setTemplatedHosts(true); //只查询设备不查询监控模型
			hoptions.setSortfield("name");
			hoptions.setEditable(true);
			hoptions.setOutput(API_OUTPUT_EXTEND);
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			CArray<Map> db_hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"db_hosts").$(db_hosts);

			// get selected hosts
			hoptions = new CHostGet();
			hoptions.setHostIds(Nest.array(data,"hosts").asLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setSortfield("name");
			hoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> r_hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"r_hosts").$(rda_toHash(r_hosts, "hostid"));*/

			// deletable groups
			if (!empty(Nest.value(data,"groupid").$())) {
				Nest.value(data,"deletableHostGroups").$(getDeletableHostGroups(getIdentityBean(), executor, Nest.array(data,"groupid").asLong()));
			}

			// render view
			CView hostgroupView = new CView("configuration.hostgroups.edit", data);
			hostgroupView.render(getIdentityBean(), executor);
			hostgroupView.show();
		} else {
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			CArray data = map(
				"config", config
			);

			String sortfield = getPageSortField(getIdentityBean(), executor,"name");
			String sortorder = getPageSortOrder(getIdentityBean(), executor);

			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setEditable(true);
			hgoptions.setOutput(new String[]{"groupid"});
			hgoptions.setSortfield(sortfield);
			hgoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, groups, array("groupid")));

			// get hosts and templates count
			//FIXME: 此处的设备数量与设备管理中的不同，主要原因是下面这个方法没有过滤租户的云主机
			hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(rda_objectValues(groups, "groupid").valuesAsLong());
			hgoptions.setSelectHosts(API_OUTPUT_COUNT);
			hgoptions.setNopermissions(true);
			CArray<Map> groupCounts = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"groupCounts").$(rda_toHash(groupCounts, "groupid"));

			// get host groups
			hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(rda_objectValues(groups, "groupid").valuesAsLong());
			hgoptions.setSelectGroupDiscovery(new String[]{"ts_delete"});
			hgoptions.setSelectDiscoveryRule(new String[]{"itemid", "name"});
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			hgoptions.setLimitSelects(Nest.value(config,"max_in_table").asInteger() + 1);
			groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"groups").$(groups);
			order_result(groups, sortfield, sortorder);

			// render view
			CView hostgroupView = new CView("configuration.hostgroups.list", data);
			hostgroupView.render(getIdentityBean(), executor);
			hostgroupView.show();
		}
	}

	public static CArray addFuncWithRights(final SQLExecutor executor, final IIdentityBean idBean,
			final CArray<Map> hosts, final CArray<Map> templates,
			final CArray<Map> groups, Map group) {
		CArray hostgroup;
		hostgroup = Call(new Wrapper<CArray>() {
			@Override
			protected CArray doCall() throws Throwable {
				CArray<Long[]> datas = API.HostGroup(idBean, executor).massAdd(map(
						"hosts", hosts,
						"templates", templates,
						"groups", groups
					));
				
				CArray<Map> rightTenants = DBUtil.DBselect(executor, "select distinct tenantid from rights");
				
				CArray<Map> rights = array();
				for(Map rightTenant: rightTenants) {
					String tenantId = Nest.value(rightTenant, "tenantid").asString();
					for(Long groupId: datas.get("groupids")) {
						rights.add(map(
							"tenantid", tenantId,
							"groupid", groupId
						));
					}
				}
				CUserDAO.addDefaultRights(executor, rights);
				
				return datas;
			}
		}, null);
		
		//增加菜单项
		addFunc(group);
		return hostgroup;
	}
	
	/**
	 * 列表页面批量删除时  设置可删除的组id
	 */
	public void getGroupids(final SQLExecutor executor){	
		if(isset(_REQUEST, "groups")){
			CArray groups = (CArray)Nest.value(_REQUEST, "groups").$();
			for(IMonGroup group: IMonGroup.values()) {
				groups.remove(group.id());
			}
		}
	}
	
	/**
	 * 修改页面单删除时  查看是否可删除类型
	 */
	public void getDeleteStatus(final SQLExecutor executor){
		Long groupid = Nest.value(_REQUEST, "groupid").asLong();
		if(IMonGroup.showableGroups().containsKey(groupid)){
			_REQUEST.put("deleteStatus", "N"); //N：不能删除该设备类型						
		}
	}
	
	/**
	 * 增加菜单项
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addFunc(Map group){
		Map paramMap = new HashMap();
		paramMap.put("id", Nest.value(group,"groupid").asString());
		paramMap.put("pid", get_request("moncategory", IMonConsts.MON_CATE_SERVER)); //监控中心--设备类别的funcId
		paramMap.put("funcName", Nest.value(group,"name").asString());
		paramMap.put("funcId", "others_"+Nest.value(group,"groupid").asString());
		paramMap.put("funcUrl", "/platform/iradar/mon_others.action?groupid="+Nest.value(group,"groupid").asString());
		paramMap.put("entrance", "Y");
		paramMap.put("leaf", "Y");
		paramMap.put("renderType", "");
		paramMap.put("renderStyle", "");
		paramMap.put("renderUrl", "");
		paramMap.put("icon", "others_"+Nest.value(group,"groupid").asString());
		paramMap.put("status", "1");
		paramMap.put("role", "2");
		paramMap.put("note", "");
		
		Object[] obj = new HostTypeAction().doAdd(paramMap);
		
		//刷新缓存
		FuncItem item = null;
		if(obj[0]!=null){//保存成功
			item = new FuncItem(); 
			List list = (List) obj[1];
			Map map = (Map) list.get(0);
			
			item.setId(map.get("id").toString());
			item.setPid(map.get("pid").toString());
			item.setFuncName(map.get("funcName").toString());
			item.setFuncUrl(map.get("funcUrl").toString());
			item.setEntrance("Y".equals(map.get("entrance"))?true:false);
			item.setLeaf("Y".equals(map.get("leaf"))?true:false);
			item.setRenderType(map.get("renderType").toString());
			item.setRenderStyle(map.get("renderStyle").toString());
			item.setRenderUrl(map.get("renderUrl").toString());
			item.setRole(map.get("role").toString());
			
		    FuncItem parentFunc = CacheUtil.getNavFuncByFuncId(item.getPid());
		    parentFunc.getSubFuncList().add(item);
		}	
	}
	
	/**
	 * 修改菜单项
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateFunc(Map group){
		Map paramMap = new HashMap();
		paramMap.put("id", Nest.value(group,"groupid").asString());
		paramMap.put("funcName", Nest.value(group,"name").asString());
		paramMap.put("pid", get_request("moncategory", IMonConsts.MON_CATE_SERVER));
		boolean bool =  new HostTypeAction().doUpdate(paramMap);
		if(bool){//修改成功
		    List<FuncItem> funcList = CacheUtil.getNavFuncByFuncId("0003").getSubFuncList();
		    for(FuncItem fi:funcList){
		    	List<FuncItem> subFuncs = fi.getSubFuncList();
		    	for(FuncItem item:subFuncs){
		    		if(paramMap.get("id").equals(item.getId())){
		    			item.setFuncName(paramMap.get("funcName").toString());
		    			FuncItem oldParent = CacheUtil.getNavFuncByFuncId(item.getPid());
		    			oldParent.getSubFuncList().remove(item);
		    			item.setPid(Nest.value(paramMap, "pid").asString());
		    			FuncItem newParent = CacheUtil.getNavFuncByFuncId(item.getPid());
		    			newParent.getSubFuncList().add(item);
		    			break;
		    		}
		    	}
		    }	
		}
	}
	
	/**
	 * 删除菜单项
	 */
	public void deleteFunc(List idList){
		Map paramMap = new HashMap();
		paramMap.put("idList", idList);
		new HostTypeAction().doDelete(paramMap);
		
		//移除菜单项
		String id=null;
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		List<FuncItem> removeList = new ArrayList<FuncItem>();
		for(int i=0; i<idList.size(); i++){
			id = idList.get(i).toString();
			for(FuncItem fi:funcList){
		    	List<FuncItem> subFuncs = fi.getSubFuncList();
		    	for(FuncItem item:subFuncs){
		    		List<FuncItem> subFuncsList = item.getSubFuncList();
		    		for(FuncItem subItem:subFuncsList){
		    			if(id.equals(subItem.getId()))
			    			removeList.add(subItem);
		    		}
		    		if(removeList.size()>0){
		    			subFuncsList.removeAll(removeList);
			    		removeList.clear();
			    	}
		    	}
			}
		}
	}
	
	/**
	 * 保存类型对应的模型
	 * @param executor
	 * @param groupid
	 * @param templateIds
	 */
	public void addGroupTemplates(SQLExecutor executor, Long groupid, CArray templateIds){
		List gtList = new ArrayList();
		Map entity = null;
		for(Object obj :templateIds){
			entity = new HashMap();
			entity.put("groupid", groupid);
			entity.put("templateid", obj);
			gtList.add(entity);
		}
		
		new HostTypeDAO(executor).doAddGroupTemplates(gtList);
	}
	
	/**
	 * 修改类型对应的模型
	 */
	public void updateGroupTemplates(SQLExecutor executor, Long groupid, CArray templateIds){
		List gtList = new ArrayList();
		Map entity = null;
		for(Object obj :templateIds){
			entity = new HashMap();
			entity.put("groupid", groupid);
			entity.put("templateid", obj);
			gtList.add(entity);
		}		
		
		new HostTypeDAO(executor).doUpdateGroupTemplates(groupid, gtList);		
	}
	
	/**
	 * 删除类型对应的模型 
	 * @param groupidList
	 */
	public void deleteGroupTemplates(SQLExecutor executor, List groupidList){
		Map paramMap = new HashMap();
		paramMap.put("groupidList", groupidList);
		
		new HostTypeDAO(executor).doDeleteGroupTemplates(paramMap);	
	}
	
	private final static String SQL_GET_PID = "SELECT pid FROM sys_func WHERE id=#{id}";
	public String getPidByFuncId(SQLExecutor executor, String funcId){
		Map paramMap = new HashMap();
		paramMap.put("id", funcId);
		CArray<Map> pids = DBUtil.DBselect(executor, SQL_GET_PID, paramMap);
		if(!empty(pids)){
			return Nest.value(reset(pids),"pid").asString();
		}else
			return IMonConsts.MON_CATE_SERVER;
	}
	
	String sql = "SELECT maintenanceid FROM maintenances_groups WHERE groupid IN (#{groupIds})";
	public boolean doGroupMaintenanceCheck(SQLExecutor executor,Long... groupIds){
		Map paraMap = map("groupIds",groupIds);
		List<Map> datas = (List<Map>)executor.executeNameParaQuery(sql, paraMap);
		return datas!=null&&datas.size()>0?false:true;
	}
	
}
