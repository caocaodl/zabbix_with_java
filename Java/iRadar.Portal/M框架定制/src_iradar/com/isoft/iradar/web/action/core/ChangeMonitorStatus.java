package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.HostsUtil.updateHostStatus;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 设备中心-设备监控列表状态切换实现ajax方式
 * @author HP Pro2000MT
 *
 */
public class ChangeMonitorStatus extends RadarBaseAction{

	private final static String SQL_UPDATE_HOST_STATUS = "UPDATE hosts SET status = #{status} WHERE hostid = #{hostid} and tenantid = #{tenantid}";
	
	@Override
	protected void doInitPage() {
//		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入Ajax异步修改设备状态JS
		page("file", "changeMonitorStatus.action");
		page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
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
		boolean result=true;
		if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED;
			int oldstatus = enable ? TRIGGER_STATUS_DISABLED : TRIGGER_STATUS_ENABLED ;
			CArray hosts = get_request("hosts", array());

			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hosts.valuesAsLong());
			hoptions.setEditable(true);
			hoptions.setTemplatedHosts(true);
			hoptions.setOutput(new String[] { "hostid" });
			CArray<Map> actHosts = API.Host(getIdentityBean(), executor).get(hoptions);
			actHosts = rda_objectValues(actHosts, "hostid");

			if (!empty(actHosts)) {
				DBstart(executor);
				if(actHosts.size() == 1){
					result = updateOneHostStatus(getIdentityBean(), executor, actHosts.valuesAsLong(), status);
				}else{
					result = updateHostStatus(getIdentityBean(), executor, actHosts.valuesAsLong(), status);
				}
				
				DBend(executor, result);
			}
			echo(String.valueOf(oldstatus).trim()+","+String.valueOf(result).trim());
			return true;
		}
		
		return false;
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
	}

	@Override
	public void doAction(SQLExecutor executor) {
	}
	
	private boolean updateOneHostStatus(IIdentityBean idBean, SQLExecutor executor, Long[] hostids, int status){
		Map datas= map();
		datas.put("tenantid",idBean.getTenantId());
		datas.put("hostid", hostids[0]);
		datas.put("status", status);
		boolean result = DBUtil.DBexecute(executor, SQL_UPDATE_HOST_STATUS, datas);
		return result;
	}
}
