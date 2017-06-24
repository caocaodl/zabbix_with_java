package com.isoft.iradar.web.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.web.action.core.LatestAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class AbstractLatestAction extends LatestAction {

	private static Map<String, String> GROUP_MAP = EasyMap.build(
			"mon_cloud", 	"云服务",
			"mon_cluster", 	"集群",
			"mon_db_mysql", "MySQL数据库",
			"mon_db_oracle", "Oracle数据库",
			"mon_db_dm",    "DM数据库",
			"mon_db_sqlServer",    "SqlServer数据库",
			"mon_db_db2",    "DB2数据库",
			"mon_db_mongodb",    "MongoDB数据库",
			"mon_desktopc", "桌面云",
			"windows_mon_server", 	"Windows服务器",
			"linux_mon_server", 	"Linux服务器",
			"mon_middle_tomcat","中间件",
			"mon_net_cisco", 		"网络设备",
			"mon_storage", 	"存储设备",
			"windows_mon_vm", 		"Windows云主机",
			"linux_mon_vm", 		"Linux云主机",
			"mon_common_net_host", 	"通用网络设备",
			"mon_web", 		"Web服务" //web服务 比较特殊，使用的是template的Web 场景
		);
	
	protected abstract String getKey();
	
	private String getGroupName() {
		return GROUP_MAP.get(getKey());
	}
	
	@Override
	protected String getAction(){
		return getKey()+".action";
	}
	
	protected Object getHeader(){
		return getGroupName()+"监察";
	}
	
	@Override
	protected void setRequest(){
		HttpServletRequest request = getRequest();
		request.setAttribute("curlAction", getAction());
	}
	
	@Override
	protected boolean showGroupFilter(){
		return false;
	}
	
	@Override
	protected Object getGroupId(SQLExecutor executor) {
		return getGroupIdByCHostGroupDAO(executor);
	}
	
	private Object getGroupIdByCHostGroupDAO(SQLExecutor executor){
		CHostGroupGet params = new CHostGroupGet();
		params.setLimit(1);
		params.setOutput("groupid");
		params.setSearch("name", getGroupName());
		CArray<Map> result = API.HostGroup(getIdentityBean(), executor).get(params);
		if(!Cphp.empty(result)){
			return Nest.value(result, 0, "groupid").$();
		}
		return null;
	}
	
}
