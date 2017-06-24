package com.isoft.biz.daoimpl.platform.topo;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.isoft.biz.dao.platform.topo.IVirtLinkTopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class VirtLinkTopoDAO extends BaseDAO implements IVirtLinkTopoDAO {

	public VirtLinkTopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	/**
	 * 前端传参：
	 * 		   1：云主机UUID(hostid_os)
	 */
	private static String SQL_GET_MONITOR_VALUE = "SQL_GET_MONITOR_VALUE";
	public Map doHostIdByTopoId(IIdentityBean identityBean,Map paraMap) {
		String sql = getSql(SQL_GET_MONITOR_VALUE);
		Map sqlVO = getSqlVO(SQL_GET_MONITOR_VALUE);
		Map mData = new HashMap();
		SQLExecutor executor = getSqlExecutor();
		CHostGet option = new CHostGet();
		option.setEditable(false);
		option.setFilter(paraMap);
		option.setOutput(new String[]{"hostid"});
		CArray<Map> hosts = API.Host(identityBean, executor).get(option);
		String cpuUsage = "0";
		String memoryUsage = "0";
		if((!Cphp.empty(hosts))&&(!Cphp.empty(hosts.get(0)))){
			long hostid = Nest.value(hosts, 0,"hostid").asLong();
			CArray<Map> cpuLinuxItem = DataDriver.getItemId(executor, hostid, ItemsKey.CPU_RATE_VM_LINUX.getValue());
			CArray<Map> cpuWindowsItem = DataDriver.getItemId(executor, hostid, ItemsKey.CPU_RATE_VM_WINDOWS.getValue());
			CArray<Map> memoryItem = DataDriver.getItemId(executor, hostid, ItemsKey.MEMORY_RATE_VM.getValue());
			String cpuLinux = "";
			String cpuWindows = "";
			String memory = "";
			if(!Cphp.empty(cpuLinuxItem)){
				paraMap.put("itemId", Nest.value(cpuLinuxItem, 0,"itemid").asString());
				List<Map> cpuLinuxValue = executor.executeNameParaQuery(sql, paraMap,sqlVO);
				if(!Cphp.empty(cpuLinuxValue))
					cpuLinux = Nest.as(cpuLinuxValue.get(0).get("value")).asString();
			}
			if(!Cphp.empty(cpuWindowsItem)){
				paraMap.put("itemId", Nest.value(cpuWindowsItem, 0,"itemid").asString());
				List<Map> cpuWindowsValue = executor.executeNameParaQuery(sql, paraMap,sqlVO);
				if(!Cphp.empty(cpuWindows))
					cpuWindows = Nest.as(cpuWindowsValue.get(0).get("value")).asString();
			}
			if(!Cphp.empty(memoryItem)){
				paraMap.put("itemId", Nest.value(memoryItem, 0,"itemid").asString());
				List<Map> memoryValue = executor.executeNameParaQuery(sql, paraMap,sqlVO);
				if(!Cphp.empty(memoryValue))
					memory = Nest.as(memoryValue.get(0).get("value")).asString();
			}
			cpuUsage = Cphp.empty(cpuLinux)?cpuWindows:cpuLinux;
			memoryUsage = memory;
		}
		mData.put("cpu", cpuUsage);
		mData.put("memory", memoryUsage);
		return mData;
	}
	
}
