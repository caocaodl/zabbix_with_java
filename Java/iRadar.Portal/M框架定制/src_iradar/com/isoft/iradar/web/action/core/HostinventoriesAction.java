package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.daoimpl.common.SystemDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostinventoriesAction extends RadarBaseAction {
	
//	public static Long[] groupids ={IMonGroup.MON_SERVER_WINDOWS.id(),IMonGroup.MON_SERVER_LINUX.id(),
//			IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id(),IMonGroup.MON_STORAGE.id()};
	
	@Override
	protected void doInitPage() {
		page("title", _("Host inventory"));
		page("file", "hostinventories.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("css", new String[] { "lessor/devicecenter/hostinventories.css" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		@SuppressWarnings("unchecked")
		CArray fields = map(
			"groupid",				array(T_RDA_INT, O_OPT,	P_SYS,			DB_ID,		null),
			"selmonitorcategoryid",	array(T_RDA_STR, O_OPT,	P_SYS,			null,		null),
			"hostid",				array(T_RDA_INT, O_OPT,	P_SYS,			DB_ID,		"isset({save})", 	null),
			"contract_number",		array(T_RDA_STR, O_OPT, null,	    	"{}.length<65",  "isset({save})"  , _("Host Other Name")),//加入字符过长判断
			"software",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"os_full",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"hardware",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"url_a",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"type_full",			array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"vendor",				array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"host_networks",		array(T_RDA_STR, O_OPT, P_SYS,			null,		null, 		null),
			"inventory_mode",		array(T_RDA_STR, O_OPT, P_SYS,			NOT_EMPTY,	null, 		null),
			"cancel" ,				array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"form",					array(T_RDA_STR, O_OPT, P_SYS,			null,		null),
			"form_refresh",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT,			null,		null),
			"masssave",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"resetflag",			array(T_RDA_STR, O_OPT, null,			null,		null),
			// filter
			"filter_set",			array(T_RDA_STR, O_OPT,	P_SYS,			null,		null),
			"filter_field",			array(T_RDA_STR, O_OPT, null,			null,		null),
			"filter_field_value",	array(T_RDA_STR, O_OPT, null,			null,		null),
			"filter_exact",        	array(T_RDA_INT, O_OPT, null,			null,		null),
			//ajax
			"favobj",				array(T_RDA_STR, O_OPT, P_ACT,			null,		null),
			"favref",				array(T_RDA_STR, O_OPT, P_ACT,  		NOT_EMPTY,	"isset({favobj})"),
			"favstate",				array(T_RDA_INT, O_OPT, P_ACT,  		NOT_EMPTY,	"isset({favobj})&&(\"filter\"=={favobj})")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		if (hasRequest("favobj")) {
			if("filter".equals(Nest.value(_REQUEST,"favobj").asString())){
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter.state", Nest.as(get_request("favstate")).asString(), PROFILE_TYPE_INT);
			}
		}
		
		CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter.mon_cate", Nest.as(get_request("selmonitorcategoryid")).asString(), PROFILE_TYPE_STR);
		
		if ((PAGE_TYPE_JS == Nest.value(page,"type").asInteger()) || (PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger())) {
			return false;
		}
		
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		Long hostid = get_request("hostid", 0L);
		CArray data = array();
		DBstart(executor);
		
		if (isset(_REQUEST,"save")){
			String msgOk = _("Hosts updated success");
			String msgFail = _("Cannot update host");	
			Map inventory= map(                                                //获取资产属性
				"contract_number", get_request("contract_number"),
				"software", get_request("software"),
				"os_full", get_request("os_full"),
				"hardware", get_request("hardware"),
				"url_a", get_request("url_a"),
				"vendor", get_request("vendor"),
				"host_networks", get_request("host_networks"),
				"type_full", get_request("type_full")
			);
			
			boolean conflictFlag = false;
			boolean result = false;
			CHostGet hostOption = new CHostGet();
			hostOption.setOutput(new String[]{"hostid"});
			hostOption.setSelectInventory(new String[]{"os_full"});
			hostOption.setWithInventory(true);
			hostOption.setEditable(false);
			CArray<Map> hostWithInventorys = API.Host(getIdentityBean(), executor).get(hostOption);
			for(Map hostInventory:hostWithInventorys){
				String osFull = Nest.value(hostInventory, "inventory","os_full").asString();
				if(!Cphp.empty(get_request("os_full"))){
					if(Nest.as(get_request("os_full")).asString().equals(osFull)&&Nest.value(hostInventory, "hostid").asLong()!=hostid){
						conflictFlag = true;
						break;
					}
				}
			}
			if(conflictFlag){
				msgFail = _("the os_full is conflicted");
				result = false;
			}else{
				final Map  attribute=new HashedMap();
				String host=get_request("hostid");
				Long hostidLong=Long.valueOf(host).longValue();
				attribute.put("inventory", inventory);
				attribute.put("hostid",  hostidLong);
				attribute.put("inventory_mode",  get_request("inventory_mode"));
				DBstart(executor);
				result = API.Call(new Wrapper<Boolean>() {
					@Override protected Boolean doCall() throws Throwable {
						return !empty(API.Host(getIdentityBean(), executor).update( array(attribute)));
					}
				});
				hostid =0L; 
			}
			show_messages(result, msgOk, msgFail);
			DBend(executor, result);
		}
		
		/* Display */
		if (hostid > 0) {
			// host scripts
			Nest.value(data,"hostScripts").$(API.Script(getIdentityBean(), executor).getScriptsByHosts(hostid));
			data.put("hostid",hostid);
			//查询资产记录
			CHostGet params1 = new CHostGet();
			params1.setOutput(new String[]{"inventory_mode"});
			params1.setSelectInventory(API_OUTPUT_EXTEND);
			params1.setHostIds(hostid);
			CArray  inventory_mode= API.Host(getIdentityBean(), executor).get(params1);
			int  inventory_mode1=Nest.value(inventory_mode,"0","inventory","inventory_mode").asInteger();
			data.put("inventory_mode",inventory_mode1);
			// inventory info
			Nest.value(data,"tableTitles").$(getHostInventories());
			Nest.value(data,"tableTitles").$(rda_toHash(Nest.value(data,"tableTitles").$(), "db_field"));
			CArray inventoryFields = array_keys(Nest.value(data,"tableTitles").asCArray());
			
			// overview tab
			CHostGet params = new CHostGet();
			params.setHostIds(hostid);
			params.setOutput(new String[]{"hostid", "host", "name", "maintenance_status"});
			params.setSelectInterfaces(API_OUTPUT_EXTEND);
			params.setSelectInventory(inventoryFields.valuesAsString());
			params.setPreserveKeys(true);
			Nest.value(data,"host").$(reset((CArray<Map>)API.Host(getIdentityBean(), executor).get(params)));
			unset(data,"host","inventory","hostid");
			
			// resolve macros
			Nest.value(data,"host","interfaces").$(CMacrosResolverHelper.resolveHostInterfaces(getIdentityBean(), executor, Nest.value(data,"host","interfaces").asCArray().entryValueFromMap2CArray()));
		
			// get permissions
			int userType = CWebUser.getType();
			if (userType == USER_TYPE_SUPER_ADMIN) {
				Nest.value(data,"rwHost").$(true);
			} else if (userType == USER_TYPE_IRADAR_ADMIN) {
				params = new CHostGet();
				params.setHostIds(hostid);
				params.setEditable(true);
				CArray<Map> rwHost = API.Host(getIdentityBean(), executor).get(params);
				Nest.value(data,"rwHost").$( (!empty(rwHost)) ? true : false);
			} else {
				Nest.value(data,"rwHost").$(false);
			}

			// view generation
			CView hostinventoriesView = new CView("inventory.host.view", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		} else {
			CArray<Long> serverGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_SERVER);
			CArray<Long> netGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_NET_DEV);
			CArray<Long> storageGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_STORAGE);
			Long[] allGroupIds = Cphp.array_merge(serverGroupIds, netGroupIds,storageGroupIds).valuesAsLong();
			
			Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
			boolean dropdown_first_remember = 1==Nest.value(config, "dropdown_first_remember").asInteger();
			Nest.value(data, "dropdown_first_remember").$(dropdown_first_remember);
			
			if(!isset(_REQUEST,"selmonitorcategoryid")){
				Nest.value(_REQUEST, "selmonitorcategoryid").$(dropdown_first_remember?doGetMonCateHistoryValue(executor,"web.hostinventories.filter.mon_cate"):0);
			}
			
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor));
			Nest.value(data, "resetflag").$(Nest.value(_REQUEST, "resetflag").$());

			CArray params = map(
				"groups", map(
					"real_hosts", 1
				),
				"groupid", get_request("groupid", null)
			);
			CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, params);
			Nest.value(data,"pageFilter").$(pageFilter);
			
			// host inventory filter
			if (hasRequest("filter_set")) {
				Nest.value(data,"filterField").$(get_request("filter_field"));
				Nest.value(data,"filterFieldValue").$(get_request("filter_field_value"));
				Nest.value(data,"filterExact").$(get_request("filter_exact"));
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_field", Nest.value(data,"filterField").$(), PROFILE_TYPE_STR);
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_field_value", Nest.value(data,"filterFieldValue").$(), PROFILE_TYPE_STR);
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_exact", Nest.value(data,"filterExact").$(), PROFILE_TYPE_INT);
			} else {
				Nest.value(data,"filterField").$(dropdown_first_remember?CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_field"):"url_a");
				Nest.value(data,"filterFieldValue").$(CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_field_value"));
				Nest.value(data,"filterExact").$(dropdown_first_remember?CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_exact"):0);
			}
			
			Nest.value(data,"hosts").$(array());
			
			if (!empty(pageFilter.$("groupsSelected").$())) {
			//if (!empty(Nest.value(_REQUEST, "selmonitorcategoryid").$())) {
				//isset(_REQUEST,"save")
				// which inventory fields we will need for displaying
				CArray requiredInventoryFields = array(
					"contract_number",
					"os_full",
					"hardware",
					"software",
					"url_a"
					
				);
				
				// checking if correct inventory field is specified for filter
				CArray<Map> possibleInventoryFields = getHostInventories();
				possibleInventoryFields = rda_toHash(possibleInventoryFields, "db_field");
				if (!empty(Nest.value(data,"filterField").$())
						&& !empty(Nest.value(data,"filterFieldValue").$())
						&& !isset(possibleInventoryFields.get(data.get("filterField")))) {
					error(_s("Impossible to filter by inventory field \"%s\", which does not exist.", Nest.value(data,"filterField").$()));
				} else {
					// if we are filtering by field, this field is also required
					if (!empty(Nest.value(data,"filterField").$()) && !empty(Nest.value(data,"filterFieldValue").$())) {
						requiredInventoryFields.add(Nest.value(data,"filterField").$());
					}
					
					CHostGet options = new CHostGet();
					options.setOutput(new String[]{"hostid", "name", "status"});
					options.setSelectInventory(API_OUTPUT_EXTEND);
					//options.setWithInventory(true);
					options.setSelectGroups(API_OUTPUT_EXTEND);
					options.setEditable(true);
					options.setSortfield("status");
					options.setSortorder("ASC");
					options.setLimit((Nest.value(data,"config","search_limit").asInteger() + 1));
					//if (pageFilter.$("groupid").asInteger() > 0) {
					//	options.setGroupIds(pageFilter.$("groupid").asLong());
					if (!empty(Nest.value(_REQUEST, "selmonitorcategoryid").asString())) {
//							Long[] groupids= MoncategoryUtil.getGroupidByMoncategory(Nest.value(_REQUEST, "selmonitorcategoryid").asString());
						Long[] groupids= MoncategoryUtil.getGroupIdsByPId(executor,Nest.value(_REQUEST, "selmonitorcategoryid").asString()).valuesAsLong();
						options.setGroupIds(groupids);
					}else{//资产列表显示为物理设备
						options.setGroupIds(allGroupIds);
					}
					
					CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
					Nest.value(data,"hosts").$(hosts);
					SystemDAO sys = new SystemDAO(executor);
					Map systemType = new HashedMap();
					// copy some inventory fields to the uppers array level for sorting
					// and filter out hosts if we are using filter
					CArray<Object> waitClearIds = new CArray();
					
		            for (Entry<Object, Map> e : hosts.entrySet()) {
		                Object num = e.getKey();
		                Map host1 = e.getValue();

		                Nest.value(data,"hosts",num,"contract_number").$(Nest.value(host1,"inventory","contract_number").$());
		                Nest.value(data,"hosts",num,"os_full").$(Nest.value(host1,"inventory","os_full").$());
						Nest.value(data,"hosts",num,"hardware").$(Nest.value(host1,"inventory","hardware").$());
		                Nest.value(data,"hosts",num,"software").$(Nest.value(host1,"inventory","software").$());
		                Nest.value(data,"hosts",num,"url_a").$(Nest.value(host1,"inventory","url_a").$());
		               // Nest.value(data,"hosts",num,"pr_macaddress_a").$(Nest.value(host1,"inventory","macaddress_a").$());
						// if we are filtering by inventory field
						if(!empty(Nest.value(data,"filterField").$()) && !empty(Nest.value(data,"filterFieldValue").$())) {
							// must we filter exactly or using a substring (both are case insensitive)
							String filterField = Nest.value(hosts, num, "inventory",data.get("filterField")).asString();
							
							if(empty(filterField)){
								waitClearIds.add(num);
							}else{
								String compare=data.get("filterField").toString();
								if (empty(compare)) {
								} else {
									if (compare.equals("hardware")) {
										systemType.put("type", "dept");
										systemType.put("dkey", filterField);
										List<Map> all = sys.doSystem(systemType);
										for (Map m : all) {
											filterField = (String) m.get("dlabel");
										}
									}
									if (compare.equals("software")) {
										systemType.put("type", "motor_room");
										systemType.put("dkey", filterField);
										List<Map> all = sys.doSystem(systemType);
										for (Map m : all) {
											filterField = (String) m.get("dlabel");
										}
									}
									if (compare.equals("url_a")) {
										systemType.put("type", "cabinet");
										systemType.put("dkey", filterField);
										List<Map> all = sys.doSystem(systemType);
										for (Map m : all) {
											filterField = (String) m.get("dlabel");
										}
									}
								}
								String filterFieldValue = Nest.value(data,"filterFieldValue").asString();
								Object filterExact = Nest.value(data,"filterExact").$();
								boolean match = (!empty(filterExact))
									? rda_strtolower(filterField).equals(rda_strtolower(filterFieldValue))
									: rda_strpos(rda_strtolower(filterField),rda_strtolower(filterFieldValue)) >-1;
								if (!match) {
									waitClearIds.add(num);
								}
							}
						}
					}
		            
					for (Object num : waitClearIds) {
						hosts.remove(num);
					}
					
					order_result(hosts, getPageSortField(getIdentityBean(), executor,"name"), getPageSortOrder(getIdentityBean(), executor));
				}
			}
			
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, Nest.value(data,"hosts").asCArray()));
            
			Nest.value(data, "inventoryGroupIds").$(Cphp.array_merge(serverGroupIds, netGroupIds,storageGroupIds));
			CView hostinventoriesView = new CView("inventory.host.list", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		}
	}

	public String doGetMonCateHistoryValue(SQLExecutor executor,String idx){
		String sql = "SELECT p.* FROM profiles p WHERE p.tenantid=#{tenantid} AND p.userid=#{userId} AND p.idx=#{idx}"
				+ " ORDER BY p.userid,p.profileid";
		Map params = new HashMap();
		params.put("tenantid", getIdBean().getTenantId());
		params.put("userId", getIdBean().getUserId());
		params.put("idx", idx);
		List<Map> dbProfiles = executor.executeNameParaQuery(sql, params);
		return (dbProfiles!=null&&dbProfiles.size()>0)?Nest.value(dbProfiles.get(0),CProfile.getFieldByType((Integer)dbProfiles.get(0).get("type"))).asString():"";
	}
	
}
