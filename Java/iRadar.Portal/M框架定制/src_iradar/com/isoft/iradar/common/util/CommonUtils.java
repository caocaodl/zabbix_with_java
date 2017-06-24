package com.isoft.iradar.common.util;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.common.util.ItemsKey.CPU_RATE_VM_LINUX;
import static com.isoft.iradar.common.util.ItemsKey.CPU_RATE_VM_WINDOWS;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_DISABLED;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.web.bean.common.SystemWordbook;
import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.LatestValueHelper.NormalValue;
import com.isoft.iradar.common.util.LatestValueHelper.PrototypeValues;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.web.action.moncenter.A_SimpleDataAction;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.bean.capacity.CapacityUtil;


/**
 * 项目中通用函数
 * @author BT
 *
 */
public class CommonUtils {
	public final static String PROTOTYPE_KEY_MODEL = "{#";
	
	public final static CArray KEYS_RATE = array(
		array("cpuUsedRate", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.CPU_USER_RATE,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.CPU_USER_RATE,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.CPU_USER_RATE_WINDOWS
		)),
		
		array("memoryRatedRate", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.MEMORY_USELV_KEY,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.MEMORY_USELV_KEY,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.MEMORY_USELV_KEY_WINDOWS
		)),
		array("memoryUsedTotal", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.MEMORY_USED_LINUX,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.MEMORY_USED_LINUX,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.MEMORY_USED_WINDOWS
		)),
		array("memoryTotal", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.TOTAL_MEMORY,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.TOTAL_MEMORY,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.TOTAL_MEMORY_WINDOWS
		)),
		
		array("diskUsedRate", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.DISK_USELV_KEY,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.DISK_USELV_KEY,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.DISK_USELV_KEY_WINDOWS
		)),
		array("diskUsedTotal", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.USED_DISK_SPACE_ON,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.USED_DISK_SPACE_ON,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.USED_DISK_SPACE_ON_WINDOWS
		)),
		array("diskTotal", map(
			IMonConsts.MON_CLOUD_COMPUTER, ItemsKey.TOTAL_DISK_SPACE_ON,
			IMonConsts.MON_SERVER_LINUX, ItemsKey.TOTAL_DISK_SPACE_ON,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.TOTAL_DISK_SPACE_ON_WINDOWS
		), "prototype")
	); 
	
	public final static CArray KEYS_NET = array(
		array("up", map(
			IMonConsts.MON_SERVER_LINUX, ItemsKey.NET_UP_SPEED,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.NET_UP_SPEED_WINDOWS,
			IMonConsts.MON_NET_CISCO, ItemsKey.NET_UP_SPEED_NET_CISCO
		)),
		array("upMax", map(
			IMonConsts.MON_SERVER_LINUX, ItemsKey.NET_UP_SPEED_MAX,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.NET_UP_SPEED_MAX_WINDOWS,
			IMonConsts.MON_NET_CISCO, ItemsKey.NET_BANDWIDTH_NET_CISCO
		)),
		array("down", map(
			IMonConsts.MON_SERVER_LINUX, ItemsKey.NET_DOWN_SPEED,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.NET_DOWN_SPEED_WINDOWS,
			IMonConsts.MON_NET_CISCO, ItemsKey.NET_DOWN_SPEED_NET_CISCO
		)),
		array("downMax", map(
			IMonConsts.MON_SERVER_LINUX, ItemsKey.NET_DOWN_SPEED_MAX,
			IMonConsts.MON_SERVER_WINDOWS, ItemsKey.NET_DOWN_SPEED_MAX_WINDOWS,
			IMonConsts.MON_NET_CISCO, ItemsKey.NET_BANDWIDTH_NET_CISCO
		))
	); 
	
	public static CArray<PrototypeValues> keysOfPrototypeValues(CArray<Map> hosts, CArray keys){
		CArray<CArray> vars = array();
		
		for(Object cfgO: keys.values()) {
			CArray cfg = (CArray)cfgO;
			String var = Nest.value(cfg, 0).asString();
			CArray varHosts = array();
			for (Map host : hosts) {
				long groupId = Nest.value(host,"groupid").asLong();
				ItemsKey key = (ItemsKey)Nest.value(cfg, 1).asCArray().get(groupId);
				if(key == null) continue;
				
				Nest.value(cfg, 2).$(key.getValue().contains(PROTOTYPE_KEY_MODEL)? "prototype": "normal");
				
				Map hostC = Clone.deepcopy(host);
				hostC.put("key", key.getValue());
				
				varHosts.add(hostC);
			}
			
			Nest.value(vars, var, "hosts").$(varHosts);
			Nest.value(vars, var, "cfg").$(cfg);
		}
		
		CArray<PrototypeValues> varValues = array();
		for(CArray var: vars) {
			CArray<Map> varHosts = Nest.value(var, "hosts").asCArray();
			CArray cfg = Nest.value(var, "cfg").asCArray();
			String varName = Nest.value(cfg, "0").asString();
			
				boolean isNormal = "normal".equals(cfg.get(2));
				LatestValueHelper lvh = isNormal? LatestValueHelper.buildByNormalKey(varHosts): LatestValueHelper.buildByPrototypeKey(varHosts);
				PrototypeValues pvs = lvh.values();
			varValues.put(varName, pvs);
		}
		return varValues;
	}
	
	/**
	 * 获取单个设备的CPU使用率
	 * @param executor
	 * @param idBean
	 * @param hostid
	 * @param key cpu使用率键值
	 * @param isFormat 是否是格式化后的数据
	 * @param isHaveUnit 格式化后的数据 是否保留单位标识
	 * @return
	 */
	public static String returnCPURate(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		NormalValue nv = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), key).value();
		return isFormat? nv.out().format(): String.valueOf(nv.value().$());
	}
	
	public static String returnCPURate(SQLExecutor executor,IIdentityBean idBean,String hostid,CArray<ItemsKey> keyCArray,boolean isFormat,boolean isHaveUnit){
		String value = LatestValueHelper.NA;
		for(ItemsKey key:keyCArray){
			value = returnCPURate(executor,idBean,hostid,key.getValue(),isFormat,isHaveUnit);
			if(!LatestValueHelper.NA.equals(value))
				return value;
		}
		return value;
	}
	
	public static String returnHostHealthNum(String key,String... hostids){
	
		return returnHostHealthNum(key,false,hostids);
	}
	/**
	 * 获取设备的CPU、内存、磁盘健康度
	 * @param hostid
	 * @param key
	 * @return
	 */
	public static String returnHostHealthNum(String key,boolean keyFlag,String... hostids){
		BigDecimal bigcpuRate = new BigDecimal("0");
		BigDecimal bigcpuHealth = new BigDecimal("0");
		int abandon = 1;
		for(String hostid:hostids){
			if(!keyFlag){
				bigcpuRate = new BigDecimal(clearFlag(LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), key).value().out().print()));
			}else{				
				bigcpuRate = new BigDecimal(clearFlag(LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid),key).values().avg().out().print()));
			}
			if(bigcpuRate.intValue() > 50){
				bigcpuHealth = bigcpuHealth.add(new BigDecimal(100 - (bigcpuRate.intValue()-50)));
			}else if(bigcpuRate.doubleValue() == 0.0d){
				abandon ++;
			}else{
				bigcpuHealth = bigcpuHealth.add(new BigDecimal("100"));
			}
		}
		int avalibleHostNum = CArray.array(hostids).size()+1-abandon;
		avalibleHostNum = avalibleHostNum == 0?1:avalibleHostNum;
		bigcpuHealth = bigcpuHealth.divide(new BigDecimal(avalibleHostNum),2);
		return bigcpuHealth.toString();
	}
	
	private static String clearFlag(String str){
		return str.equals("--")?"0":str;
	}
	
	/**
	 * 判断设备是否可达
	 * @param hostid
	 * @return
	 */
	public static boolean checkHostIsLink(IIdentityBean idBean, SQLExecutor executor,String hostid){
		//获取设备信息
		CArray<Map> hosts = array();
		CHostGet options = new CHostGet();
		options.setHostIds(Long.valueOf(hostid));
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectParentTemplates(new String[]{"hostid", "name"});
		options.setSelectTriggers(API_OUTPUT_COUNT);
		hosts = API.Host(idBean, executor).get(options);//得到当前页设备表

		//是否可用
		int available = Nest.value(hosts.get(0), new Object[] { "available" }).asInteger();
		int ipmi = Nest.value(hosts.get(0), new Object[] { "ipmi_available" }).asInteger();
		int snmp = Nest.value(hosts.get(0), new Object[] { "snmp_available" }).asInteger();
		int jmx = Nest.value(hosts.get(0), new Object[] { "jmx_available" }).asInteger();
		
		boolean availableStr =false;
		 if(available== HOST_AVAILABLE_TRUE || ipmi==HOST_AVAILABLE_TRUE 
				|| snmp== HOST_AVAILABLE_TRUE || jmx==HOST_AVAILABLE_TRUE){
			availableStr = true;//可用
		}
		return availableStr;
	}
	
	/**
	 * 获取设备可达性分数
	 * @param hostids
	 * @return
	 */
	public static Integer returnHostLinkHealthNum(IIdentityBean idBean, SQLExecutor executor,String... hostids){
		BigDecimal hostLinkHealth = new BigDecimal(CArray.array(hostids).size());
		boolean is_link = false;
		int notLinkNum = 0;
		for(String hostid:hostids){
			is_link = checkHostIsLink(idBean,executor,hostid);
			if(!is_link){
				notLinkNum ++;
			}
		}
		BigDecimal bigLinkNum = new BigDecimal(hostLinkHealth.intValue() - notLinkNum);
		if(hostLinkHealth.intValue() != 0){			
			hostLinkHealth = bigLinkNum.divide(hostLinkHealth,2).multiply(new BigDecimal("100"));
		}
		return hostLinkHealth.intValue();
	}
	
	/**
	 * 单个设备健康度
	 * @param idBean	
	 * @param executor
	 * @param _filter
	 * @return
	 */
	public static Map host_health(IIdentityBean idBean, SQLExecutor executor,String hostid,String groupId) {
		Map healthMap = new HashMap();
		Map keyMap = new HashMap();
		keyMap.put("0", _("Availability"));
		keyMap.put("1", "cpu");
		keyMap.put("2", "内存");
		keyMap.put("3", "磁盘");
		Map valueMap = new HashMap();
		BigDecimal northNum = new BigDecimal(0);
		BigDecimal westNum = new BigDecimal(0);
		BigDecimal southNum = new BigDecimal(0);
		BigDecimal eastNum = new BigDecimal(0); 
		BigDecimal total = new BigDecimal(100); 
		boolean islink = checkHostIsLink(idBean, executor, hostid);	//可用性
		if(islink){
			northNum = total;
		}
		if(groupId.equals(IMonConsts.MON_SERVER_WINDOWS.toString())){			
			westNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CPU_USER_RATE_WINDOWS.getValue()).value().out().print())));
			southNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MEMORY_USELV_KEY_WINDOWS.getValue()).value().out().print())));
			eastNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.DISK_USELV_KEY_WINDOWS.getValue()).values().avg().out().print())));
			
		}else if(groupId.equals(IMonConsts.MON_SERVER_LINUX.toString())){
			westNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CPU_USER_RATE.getValue()).value().out().print())));
			southNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.MEMORY_USELV_KEY.getValue()).values().avg().out().print())));
			eastNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.DISK_USELV_KEY.getValue()).values().avg().out().print())));
			
		}else if(groupId.equals(IMonConsts.MON_VM.toString())){
			int isAvailable = A_SimpleDataAction.getVmStatus(DataDriver.getAllVmStatus(executor,hostid),Nest.as(hostid).asLong());
			northNum = isAvailable!=0&&isAvailable!=2?total:new BigDecimal(0);
			westNum = total.subtract(new BigDecimal(_clear(CommonUtils.returnCPURate(executor, idBean, hostid,array(ItemsKey.CPU_RATE_VM_LINUX,ItemsKey.CPU_RATE_VM_WINDOWS), false,false))));
			southNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MEMORY_RATE_VM.getValue()).value().out().print())));
			eastNum = total.subtract(new BigDecimal(_clear(LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.DISK_RATE_VM.getValue()).values().avg().out().print())));
		}
		
		valueMap.put("0", northNum.intValue());
		valueMap.put("1", westNum.intValue());
		valueMap.put("2", southNum.intValue());
		valueMap.put("3", eastNum.intValue());
		
		healthMap.put("key", keyMap);
		healthMap.put("value", valueMap);
		return healthMap;
	}
	
	private static String _clear(String value){
		if(value == null || value.equals("--") || value.equals("") || value.equals("null")){
			value = "0";
		}
		return value;
	}
	
	/**
	 * 获取单个设备CPU负载
	 * @param executor
	 * @param idBean
	 * @param hostid	设备ID
	 * @param isFormat 	是否是格式化后的数据
	 * @param isHaveUnit格式化后的数据 是否保留单位标识
	 * @param key cpu负载键值
	 * @return
	 */
	public static String returnCPULoad(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), key).values().avg().round(2).out().print();
	}
	
	/**
	 * 获取内存使用率(单个设备)
	 * @param executor
	 * @param idBean
	 * @param hostid	设备ID
	 * @param key		内存使用率键值
	 * @param isFormat	是否格式化数据
	 * @param isHaveUnit是否保留格式化后的单位
	 * @return
	 */
	public static String returnMemoryRate(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取已使用内存量(单个设备)
	 * @param executor
	 * @param idBean	租户ID
	 * @param hostid	设备ID
	 * @param memory_used_key 已用内存键值
	 * @return
	 */
	public static String returnUsedMemory(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(key, hostid).sum().round(2).out().format();
	}
	
	/**
	 * 获取单个设备的内存总量
	 * @param executor
	 * @param idBean	租户ID
	 * @param hostid	设备ID
	 * @param total_memory_key 内存总量键值
	 * @return
	 */
	public static String returnTotalMemory(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(key, hostid).sum().round(2).out().format();
	}	
	
	/**
	 * 获取某个设备的磁盘使用率(有专门磁盘使用率键值时 使用此方法快捷)
	 * 获取全部磁盘分区的利用率 再取平均值
	 * @param disk_uselv_key 磁盘利用率键值
	 * @return
	 */
	public static String returnDiskRate(SQLExecutor executor,IIdentityBean idBean,String hostid,String disk_uselv_key){
		return getPrototypeTargetLastValue(disk_uselv_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取某个设备的磁盘使用率
	 * @param executor
	 * @param idBean
	 * @param hostid	设备ID
	 * @param key		磁盘使用率键值
	 * @param isFormat	是否格式化数据
	 * @param isHaveUnit是否保留格式化数据后的单位
	 * @return
	 */
	public static String returnDiskRate(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(key, hostid).avg().round(2).out().format();
	}
	
	
	/**
	 * 获取某个设备的磁盘使用量
	 * 求出各个磁盘分区使用量之和
	 * @param used_disk_space_on_key 磁盘已使用量键值
	 * @param isFormat	是否需要对数据格式化
	 * @param isHaveUnit是否保留格式化数据后的单位符号 
	 * @return
	 */
	public static String returnDiskUsed(SQLExecutor executor,IIdentityBean idBean,String hostid,String used_disk_space_on_key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(used_disk_space_on_key, hostid).sum().round(2).out().format();
	}
	
	/**
	 * 获取某个设备的磁盘总量
	 * 求出各个磁盘分区总量之和
	 * @param total_disk_space_on_key 磁盘总量键值
	 * @param isFormat 是否格式化数据
	 * @param isHaveUnit 是否保留格式化数据的单位
	 * @return
	 */
	public static String returnDiskTotal(SQLExecutor executor,IIdentityBean idBean,String hostid,String key,boolean isFormat,boolean isHaveUnit){
		return getPrototypeTargetLastValue(key, hostid).sum().round(2).out().format();
	}
	
	/**
	 * 获取网络上行速率
	 * @param net_up_speed_key 网络上行速率键值
	 * @param isFormat 是否格式化数据
	 * @return
	 */
	public static String returnNetRateForUp(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_up_speed_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_up_speed_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取网络上行流量
	 * @param net_up_flow_key 网络上行流量带宽键值
	 * @param isFormat 是否格式化数据
	 * @return
	 */
	public static String returnNetFlowForUp(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_up_flow_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_up_flow_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取网络上行速率最大值(单个设备 各个分区最大值均值)
	 * @param net_up_speed_max_key 网络上行速率最大值键值
	 * @param isFormat 是否格式化数据
	 * @return
	 */
	public static String returnNetRateForUpMax(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_up_speed_max_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_up_speed_max_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取网络下行带宽
	 * @param net_down_flow_key 网络下行流量带宽键值
	 * @param isFormat 是否对数据格式化
	 * @return
	 */
	public static String returnNetFlowForDown(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_down_flow_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_down_flow_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取网络下行速率
	 * @param net_down_flow_key 网络下行速率
	 * @param isFormat 是否对数据格式化
	 * @return
	 */
	public static String returnNetRateForDown(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_down_speed_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_down_speed_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取网络下行速率最大值(单个设备 各个分区最大值均值)
	 * @param net_down_speed_max_key 网络下行速率最大值键值
	 * @param isFormat 是否格式化数据
	 * @return
	 */
	public static String returnNetRateForDownMax(SQLExecutor executor,IIdentityBean idBean,String hostid,String net_down_speed_max_key,boolean isFormat){
		return getPrototypeTargetLastValue(net_down_speed_max_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 磁盘读取速率
	 * @param disk_read_speed_key 磁盘读取速率键值
	 * @return
	 */
	public static String returnDiskReadSpeed(SQLExecutor executor,IIdentityBean idBean,String hostid,String disk_read_speed_key){
		return getPrototypeTargetLastValue(disk_read_speed_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 磁盘写入速率
	 * @param disk_write_speed_key 磁盘写入速率键值
	 * @return
	 */
	public static String returnDiskWriteSpeed(SQLExecutor executor,IIdentityBean idBean,String hostid,String disk_write_speed_key){
		return getPrototypeTargetLastValue(disk_write_speed_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 磁盘读取请求带宽
	 * @param disk_read_request_bw_key 磁盘读取速率键值
	 * @return
	 */
	public static String returnDiskReadRequestBw(SQLExecutor executor,IIdentityBean idBean,String hostid,String disk_read_request_bw_key){
		return getPrototypeTargetLastValue(disk_read_request_bw_key, hostid).avg().round(2).out().format();
	}
	
	/**
	 * 获取某个设备的健康度
	 * @param executor
	 * @param idBean
	 * @param hostid
	 * @param groupId
	 * @return
	 */
	public static int returnHealthDegree(SQLExecutor executor,IIdentityBean idBean,String hostid){
		BigDecimal healthNum = new BigDecimal(100);		//初始化100分
		//验证某个设备的可用性
		boolean islink = checkHostIsLink(idBean, executor, hostid);	//设备可用性
		if(islink){
			//活动告警级别
			int cutpoint = getEventPriority(executor,idBean,hostid)*10;
			healthNum = healthNum.subtract(new BigDecimal(cutpoint));
		}else{
			healthNum = healthNum.subtract(new BigDecimal(100));
		}
		return healthNum.intValue();
	}
	
	/**
	 * 获取某个设备活动告警的最高级别
	 * @param executor
	 * @param idBean
	 * @param hostid
	 * @return
	 */
	public static int getEventPriority(SQLExecutor executor,IIdentityBean idBean,String hostid){
		int result = 0;
		Map<String, String> params = new HashMap<String,String>();
		params.put("hostid", hostid);
		params.put("tenantid", idBean.getTenantId());
		String SQL = "SELECT max(t.priority) priority " + 
				"  FROM triggers t " + 
				" WHERE     t.value = 1 " + 
				"       AND t.status = 0 " + 
				"       AND t.tenantid = #{tenantid} " + 
				"       AND EXISTS " + 
				"              (SELECT 1 " + 
				"                 FROM events e " + 
				"                WHERE     e.objectid = t.triggerid " + 
				"                      AND e.object = 0 " + 
				"                      AND e.source = 0) " + 
				"       AND EXISTS " + 
				"              (SELECT 1 " + 
				"                 FROM functions f INNER JOIN items i ON i.itemid = f.itemid " + 
				"                WHERE f.triggerid = t.triggerid AND i.hostid = #{hostid})";
		
		CArray<Map> eventsCountMap = DBselect(executor, SQL, params);
		if(eventsCountMap != null && eventsCountMap.size() != 0){
			result = Nest.value(eventsCountMap.get(0), "priority").asInteger();
		}
		return result;
	}
	
	/**
	 * 获取某个设备的网络利用率
	 * 网络利用率=(网络上行利用率+网络下行利用率)/N
	 * @param executor
	 * @param idBean
	 * @param hostid
	 * @return
	 */
	public static String returnNetRate(SQLExecutor executor,IIdentityBean idBean,String hostid,String netUpSpeedKey,String netUpSpeedMaxKey,String netDownSpeedKey,String netDownSpeedMaxKey){
		BigDecimal resultBig = new BigDecimal("0");
		try {
			String netUpSpeed = CommonUtils.returnNetRateForUp(executor, idBean, hostid, netDownSpeedMaxKey, false);	//网络上行速率
			String netUpMaxSpeed = CommonUtils.returnNetRateForUpMax(executor, idBean, hostid, netUpSpeed, false);		//网络上行最大速率
			String netDownSpeed = CommonUtils.returnNetRateForDown(executor, idBean, hostid, netUpMaxSpeed, false);		//网络下行速率
			String netDownMaxSpeed = CommonUtils.returnNetRateForDownMax(executor, idBean, hostid, netDownSpeed, false);//网络下行最大速率
			BigDecimal netUpRate = new BigDecimal("0");		//网络上行利用率
			BigDecimal netDownRate = new BigDecimal("0");	//网络下行利用率
			
			//网络上行利用率
			if(!netUpSpeed.equals("--") && !netUpMaxSpeed.equals("--")){
				BigDecimal netUpSpeedBig = new BigDecimal(netUpSpeed);
				BigDecimal netUpMaxBig = new BigDecimal(netUpMaxSpeed);
				netUpRate = netUpSpeedBig.divide(netUpMaxBig, 2);
			}
			//网络下行利用率
			if(!netDownSpeed.equals("--") && !netDownMaxSpeed.equals("--")){
				BigDecimal netDownSpeedBig = new BigDecimal(netDownSpeed);
				BigDecimal netDowNMaxBig = new BigDecimal(netDownMaxSpeed);
				netDownRate = netDownSpeedBig.divide(netDowNMaxBig, 2);
			}
			//网络利用率
			resultBig = (netUpRate.add(netDownRate)).divide(new BigDecimal("2"), 2);
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return resultBig.toString();
	}
	
	
	
	/**
	 * 数据单位转化
	 * @param oldUnit	原始单位
	 * @param newUnit	结果单位
	 * @param value		数据
	 * @return
	 */
	public static String changeUnit(String oldUnit,String newUnit,BigDecimal value){
		BigDecimal result = new BigDecimal("0");
		BigDecimal multValue = new BigDecimal("1024");		//内存转化值
		//单位转化
		if(!oldUnit.equals(newUnit)){
			if(oldUnit.equals("GB")){
				if(newUnit.equals("MB")){
					result = value.multiply(multValue);		//GB转化为MB
				}
			}else if(oldUnit.equals("MB")){
				if(newUnit.equals("GB")){
					result = value.divide(multValue, 2);		//MB转化为GB
				}
			}
		}else{
			result = value;
		}
		result = result.setScale(3,BigDecimal.ROUND_HALF_UP);//保留小数点后两位
		return result.toString();
	}
	
	
	/**
	 * 获取服务器分组下所有的设备ID集合
	 * @return
	 */
	public static CArray<Map> queryMonServerHostIDs(SQLExecutor executor,IIdentityBean idBean){
		return queryMonServerHostIDAndName();
	} 
	
	
	public static CArray<Map> queryMonServerHostIDAndName(){
		return queryMonServerHostIDAndName(IMonConsts.MON_SERVER_WINDOWS, IMonConsts.MON_SERVER_LINUX);
	}
	
	/**
	 * 获取服务器分组下所有的设备ID、Name集合
	 * @return
	 */
	public static CArray<Map> queryMonServerHostIDAndName(Long... groupIds){
		CArray<Map> r = CArray.map();
		for(Long groupid: groupIds) {
			CHostGet options = new CHostGet();
			options.setOutput(new String[] {"hostid","name"});
			options.setGroupIds(groupid);
//			options.setWithMonitoredItems(true);
			CArray<Map> hosts = API.Host(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
			
			for(Map host: hosts) {
				host.put("groupid", groupid);
				r.put(Nest.value(host, "hostid").$(), host);
			}
		}
		return r;
	}
	
	/**  获取设备类型下的设备和groupid
	 * @param groupIds
	 * @return
	 */
	public static List<Map> queryHostIDAndName(Long... groupIds){
		List<Map> hostdata=new ArrayList<Map>();
		for(Long groupid: groupIds) {
			CHostGet options = new CHostGet();
			options.setOutput(new String[] {"hostid","name"});
			options.setGroupIds(groupid);
			CArray<Map> hosts = API.Host(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
			
			for (Map host: hosts) {//磁盘使用率隐藏
			    Map hostdt =new HashMap();
			    hostdt.put("name", Nest.value(host, "name").asString());
			    hostdt.put("hostid", Nest.value(host, "hostid").asString());
			    hostdt.put("groupid", groupid);
			    hostdata.add(hostdt);
		   }
		}
		return hostdata;
	}
	
	
	/**  获取设备类型下的设备和groupid
	 * @param groupIds
	 * @return
	 */
	public static List<Map> queryHostIDAndNameTwo(Long... groupIds){
		List<Map> hostdata=new ArrayList<Map>();
		for(Long groupid: groupIds) {
			CHostGet options = new CHostGet();
			options.setOutput(new String[] {"hostid","name"});
			options.setGroupIds(groupid);
			CArray<Map> hosts = API.Host(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
			
			for (Map host: hosts) {
			    Map hostdt =new HashMap();
			    hostdt.put("name", Nest.value(host, "name").asString());
			    hostdt.put("hostid", Nest.value(host, "hostid").asString()+"-"+groupid);
			    hostdata.add(hostdt);
		   }
		}
		return hostdata;
	}
	
	/**   报表中获取设备不含有重复设备的设备数据
	 * @param keys
	 * @param groupIds
	 * @return
	 */
	public static List<Map> queryHostIDAndNameTwo(String[] keys,Long... groupIds){
		List<Map> hostdata=new ArrayList<Map>();
		Set<Map> sethostdata=new HashSet<Map>();
		List<Map> removedata=new ArrayList<Map>();
		List<Map> adddata=new ArrayList<Map>();
		List<Map> listhostdata=new ArrayList<Map>();
		try {
			for(Long groupid: groupIds) {
				CHostGet options = new CHostGet();
				options.setOutput(new String[] {"hostid","name"});
				options.setGroupIds(groupid);
				CArray<Map> hosts = API.Host(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
				
				for (Map host: hosts) {
					String name = Nest.value(host, "name").asString();
					String hostid = Nest.value(host, "hostid").asString();
				    Map hostdt =new HashMap();
				    hostdt.put("name", name);
				    hostdt.put("repeathostid", hostid);
				    if(sethostdata.contains(hostdt)){
				    	listhostdata.add(hostdt);
				    }else{
				    	sethostdata.add(hostdt);
				    	Map hostd =new HashMap();//重新定义变量，避免sethostdata和hostdata所需变量一样，影响去除判断
				    	hostd.put("name", name);
				    	hostd.put("repeathostid", hostid);
				    	hostd.put("hostid", Nest.value(host, "hostid").asString()+"-"+groupid);
				    	hostdata.add(hostd);
				    }
			   }
			}
			
			for(Map host:listhostdata){//去除重复元素
				for(int i=0 ;i<keys.length;i++){
					Long repeathostid = Nest.value(host, "repeathostid").asLong();
					Double cpuLoad = LatestValueHelper.buildByNormalKey(repeathostid, keys[i]).value().round(2).value().asDouble();
				    if(cpuLoad>0){
			            	for(Map hostmap:hostdata){
			            		if(repeathostid == Nest.value(hostmap, "repeathostid").asLong()){
			            			removedata.add(hostmap);
			            			host.put("hostid", Nest.value(host, "repeathostid").asString()+"-"+groupIds[i]);
			            			adddata.add(host);
			            			break;
			            		}
			            }
				    }
				}
			}
		} catch (Exception e) {
              e.printStackTrace();
		}
		if(!empty(removedata)){
			hostdata.removeAll(removedata);
			hostdata.addAll(adddata);
		}
		
		return hostdata;
	}
	
    /**  获取web服务的名称
     * @return
     */
    public static CArray<Map> queryWebidAndName(){
    	CHttpTestGet options = new CHttpTestGet();
		options.setOutput(new String[]{"httptestid","name","hostid"});
		options.setSelectSteps(new String[]{"name"});
		options.setTemplated(false);
		options.setLimit(1001 + 1);
		options.setEditable(true);
		
		CArray<Map> httpTests = API.HttpTest(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
		return httpTests;
    }
	
	
	public static CFormList assembleDetailDataForTable(Map leftData,Map rightData){
		return assembleDetailDataForTable(leftData,rightData,false);
	}
	/**
	 * 组装设备详情页面设备属性
	 * @param leftData		左侧键值
	 * @param rightData		右侧键值
	 * @return
	 */
	public static CFormList assembleDetailDataForTable(Map leftData,Map rightData,boolean flag){
		//初始化表格
		CFormList overviewFormList = new CFormList();
		CFormList leftForm = new CFormList();
		CFormList rightForm = new CFormList();
		if(flag){			
			leftForm.setAttribute("id", "leftFormDiv");
			rightForm.setAttribute("id", "rightFormDiv");
		}
		String textFieldClass = "text-field-notServer";
		
		//左侧
		for(Object key:leftData.keySet()){
			CSpan span = new CSpan(rda_str2links(Nest.value(leftData, key).asString()), textFieldClass);
			span.setTitle(rda_str2links(Nest.value(leftData, key).asString()));
			leftForm.addRow(
					key,
					span
				);
		}	
		//右侧
		for(Object key:rightData.keySet()){
			CSpan span = new CSpan(rda_str2links(Nest.value(rightData, key).asString()), textFieldClass);
			span.setTitle(rda_str2links(Nest.value(rightData, key).asString()));
			rightForm.addRow(
					key,
					span
				);
		}
		overviewFormList.addRow(leftForm,rightForm);
		if(flag){
			overviewFormList.addClass("overviewClass");
		}
		return overviewFormList;
	}
	
	/**
	 * 根据资产的键值获取其代表的标签名称
	 * @param dkey 键值
	 * @param type 资产类型
	 * @return
	 */
	public static String returnSystemDLabel(Object dkey,String type){
		SystemWordbook sys =new SystemWordbook();
		Map systemType =new HashedMap();
		systemType.put("type", type);
		systemType.put("dkey", dkey);
		List<Map> all=sys.doAll(systemType);
		String dlebal = "";
		for(Map map:all){
			if(map.get("dkey").equals(dkey)){
				dlebal = map.get("dlabel").toString();
				break;
			}
		}
		return dlebal;
	}
	
	/**
	 * 还原对防xss时做的转义
	 * 
	 * @param content
	 * @return
	 */
	public static String unXss(String content) {
		if (content != null && content.length() > 0) {
			content = content.replaceAll("&", "&amp;");
			content = content.replaceAll("<", "&lt;");
			content = content.replaceAll(">", "&gt;");
			content = content.replaceAll("'", "&#039;");
			content = content.replaceAll("\"", "&quot;");
	
			content = content.replaceAll("&amp;", "&");
			content = content.replaceAll("&lt;", "<");
			content = content.replaceAll("&gt;", ">");
			content = content.replaceAll("&#039;", "'");
			content = content.replaceAll("&quot;", "\"");
			return content;
		} else {
			return content;
		}
	}
	
	/**
	 * 防xss，对输入内容进行转义
	 * 
	 * @param content
	 * @return
	 */
	public static String xss(String content) {
		if (content != null && content.length() > 0) {
			content = StringUtils.trim(content);
			content = content.replaceAll("&amp;","&");
			content = content.replaceAll("&lt;","<");
			content = content.replaceAll("&gt;",">");
			content = content.replaceAll("&#039;","'");
			content = content.replaceAll("&quot;","\"");
			
			content = content.replaceAll("&", "&amp;");
			content = content.replaceAll("<", "&lt;");
			content = content.replaceAll(">", "&gt;");
			content = content.replaceAll("'", "&#039;");
			content = content.replaceAll("\"", "&quot;");
			return content;
		} else {
			return content;
		}
	}
	
	/**
	 * 查询TOPN使用率数据
	 * @param executor
	 * @param idBean
	 * @param type		要查询的指标类型
	 * @param showNum	要查询的行数
	 * @param isSort	是否倒序
	 * @return
	 */
	public static List getTOPNRate(SQLExecutor executor,IIdentityBean idBean,String type,int showNum,boolean isSort){
		List result = new ArrayList();			
		if("CPU".equals(type)){
			result = CommonUtils.getTOPNRateByCPU(executor, idBean, showNum,isSort);
		}else if("内存".equals(type)){
			result = CommonUtils.getTOPNRateByMemory(executor, idBean, showNum,isSort);
		}else if("网络".equals(type)){
			result = CommonUtils.getTOPNRateByNet(executor, idBean, showNum,isSort);
		}else if("磁盘".equals(type)){
			result = CommonUtils.getTOPNRateByDisk(executor, idBean, showNum,isSort);
		}else if("performance".equals(type)) {
			TopNHelper helper = new TopNHelper(showNum, "value", isSort);
			CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName();
			for(Map host: hosts){
				String hostid= Nest.value(host, "hostid").asString();
				String groupid= Nest.value(host, "groupid").asString();
				String hostname = Nest.value(host, "name").asString();
				
				int healthnum=CommonUtils.returnHealthDegree(executor, idBean, hostid);
				helper.put(EasyMap.build("hostid", hostid, "name", hostname, "value", healthnum));
			}
			result = helper.getResult();
		}
		return result;
	}
	
	
	/**
	 * 获取CPU使用率前N项 包括正序和倒序
	 * 
	 * @param executor
	 * @param idBean
	 * @param showNum
	 * @param order 真desc，否asc
	 * @return
	 */
	public static List getTOPNRateByCPU(SQLExecutor executor, IIdentityBean idBean, int showNum, boolean order){
		return getTOPNRateByCPU(executor,idBean,0L,0,null,showNum,order);
	}
	
	/**获取某个设备组CPU使用率最近一段时间内平均值  包括正序和倒序 
	 * @param executor
	 * @param idBean   用户信息
	 * @param groupid  设备组
	 * @param period   时间范围
	 * @param itemkey  键值
	 * @param showNum  展示数目
	 * @param order    正序倒叙
	 * @return
	 */
	public static List getTOPNRateByCPU(SQLExecutor executor, IIdentityBean idBean,Long groupid,Integer period,String itemkey,int showNum, boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts =array();
		if(groupid == 0){//没有设备组,默认为服务器组
			hosts = CommonUtils.queryMonServerHostIDAndName();
		}else{
			hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		}
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			CArray<ItemsKey> keys = array();
			
			if(groupid == 0){
				Long groupId = Nest.value(en.getValue(),"groupid").asLong();
				if(IMonConsts.MON_SERVER_LINUX == groupId){
					keys.add(ItemsKey.CPU_USER_RATE);
				}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){
					keys.add(ItemsKey.CPU_USER_RATE_WINDOWS);
				}
			}else if(groupid == IMonGroup.MON_VM.id()){
				keys.add(ItemsKey.CPU_RATE_VM_LINUX);
				keys.add(ItemsKey.CPU_RATE_VM_WINDOWS);
			}
			
			if(!empty(keys)){
				if(period == 0){
					Double avgalue = LatestValueHelper.buildByNormalKey(hostid, reset(keys).getValue()).value().round(2).value().asDouble();
					helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", avgalue));
			   }else {
				   int i=0;
				   for(ItemsKey key : keys){
					   Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, key.getValue()));
					   if(!empty(item)){
						   Double avgalue = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
						   Double maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
						   if(avgalue !=0){
							   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", avgalue,"maxvalue",maxvalue));
							   break; 
						   }
						   i++;
						   if(i==keys.size()){
							   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", avgalue,"maxvalue",maxvalue)); 
						   }
					   }
				   }
			   }
			}
			
		}
		
		return helper.getResult();
	}
	
	/**
	 * 获取内存使用率前N项 包括正序和倒序
	 * @param executor
	 * @param idBean
	 * @param showNum	需要返回的数据量
	 * @return
	 */
	public static List getTOPNRateByMemory(SQLExecutor executor,IIdentityBean idBean,int showNum,boolean order){
		return getTOPNRateByMemory(executor,idBean,0L,0,null,showNum,order);
	}
	
	
	public static List getTopNRate(SQLExecutor executor,IIdentityBean idBean,Integer period,int showNum,String markstr,boolean order){
		String unitkeystr=null,uintgroupidstr=null,keystr = null, groupidstr=null;
		if("cpu".equals(markstr)){
			unitkeystr="101";
			uintgroupidstr="cpuUsage";
			keystr="102";
			groupidstr="wmi.get[root\\cimv2,Select LoadPercentage from Win32_processor]";
		}
		CArray<Map> datas = CapacityUtil.getTOPN(executor, idBean.getTenantId(), period, showNum, unitkeystr, uintgroupidstr, keystr, groupidstr,order);
		List<Map>  dt=new ArrayList<Map>();
		for(Map data:datas){
			double value=Nest.value(data, "avgvalue").asDouble();
			if(value > 0){
				CHostGet options=new CHostGet();
				options.setItemIds(Nest.value(data, "itemid").asLong());
				options.setOutput(new String[]{"hostid","name"});
				Map hosts = reset((CArray<Map>)API.Host(idBean, executor).get(options));
				dt.add(EasyMap.build("hostid", Nest.value(data, "hostid").asLong(), 
						              "name", Nest.value(data, "name").asString(), 
						              "value", Cphp.round(value,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			}
		}
		return dt;
	}
	
	/**获取某个设备组内存使用率最近一段时间内平均值  包括正序和倒序 
	 * @param executor
	 * @param idBean   用户信息
	 * @param groupid  设备组
	 * @param period   时间范围
	 * @param itemkey  键值
	 * @param showNum  展示数目
	 * @param order    正序倒叙
	 * @return
	 */
	public static List getTOPNRateByMemory(SQLExecutor executor, IIdentityBean idBean,Long groupid,Integer period,String itemkey,int showNum, boolean order){
        TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts =array();
		if(groupid == 0){//没有设备组,默认为服务器组
			hosts = CommonUtils.queryMonServerHostIDAndName();
		}else{
			hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		}
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			ItemsKey key = null;
			boolean isLinux = false;
			if(groupid == 0 ){
				Long groupId = Nest.value(en.getValue(),"groupid").asLong();
				if(IMonConsts.MON_SERVER_LINUX == groupId){
					key = ItemsKey.MEMORY_USELV_KEY;
					isLinux = true;
				}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){
					key = ItemsKey.MEMORY_USELV_KEY_WINDOWS;
				}
			}else if(groupid == IMonGroup.MON_VM.id()){
				key = ItemsKey.MEMORY_RATE_VM;
			}
			//MEMORY_RATE_VM
			if(key != null){
				if(period == 0){
					Double cpuLoad = 0.0;
					if(isLinux)
						cpuLoad = LatestValueHelper.buildByPrototypeKey(hostid, key.getValue()).values().avg().round(2).value().asDouble();
					else
						cpuLoad = LatestValueHelper.buildByNormalKey(hostid, key.getValue()).value().round(2).value().asDouble();
					helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(cpuLoad,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			   }else {
					CArray<Map> item = DataDriver.getItemsBykey(IRadarContext.getSqlExecutor(), hostid, key.getValue());
					Double avgvalue = 0d, maxvalue = 0d;
					 if(!empty(item)){
						 if(item.size()>1){
							 avgvalue = Cphp.round(ReportUtil.getSomeTimeAvgNamePrototypeKey(executor,item,idBean.getTenantId(),period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
						 }else{
							 avgvalue = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item.get(0), idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
						 }
					 }
					 if(!empty(item)){
						 if(item.size()>1){
							 maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxNamePrototypeKey(executor,item,idBean.getTenantId(),period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
						 }else{
							 maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxName(executor, item.get(0), idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
						 }
					 }
					helper.put(EasyMap.build("hostid", hostid, "name", hostName, 
							"value", avgvalue,
							"maxvalue",maxvalue));
			   }
			}
			
		}
		
		return helper.getResult();
	}
	
	/**获取某设备组磁盘使用率最近一段时间内的平均值list
	 * @param executor
	 * @param idBean
	 * @param groupid
	 * @param period
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List getTOPNRateByDisk(SQLExecutor executor, IIdentityBean idBean,Long groupid,Integer period,String itemkey,
			                               int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		CArray<Map> hosts =array();
		if(groupid == 0){//没有设备组,默认为服务器组
			hosts = CommonUtils.queryMonServerHostIDAndName();
		}else{
			hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		}
		for(Entry<Object, Map> en: hosts.entrySet()){
				Long hostid = (Long)en.getKey();
				String hostName = Nest.value(en.getValue(), "name").asString();
				
				
				String key = null;;
				if(groupid == 0){
					Long groupId = Nest.value(en.getValue(),"groupid").asLong();
					if(IMonConsts.MON_SERVER_LINUX == groupId){
						key = ItemsKey.DISK_USELV_KEY.getValue();
					}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){
						key = ItemsKey.DISK_USELV_KEY_WINDOWS.getValue();
					}
				}else if(groupid == IMonGroup.MON_VM.id()){
					key = ItemsKey.DISK_RATE_VM.getValue();
				}
				
				if(key!=null){
					CArray<Map> items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, key);
					Double cpuLoad = 0d;   
					if(!empty(items))
						cpuLoad = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
					helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(cpuLoad,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
				}
			}
			
		return helper.getResult();
	}
	
	
	/**获取某个设备组IO读写速率使用率最近一段时间内平均值  包括正序和倒序 
	 * @param executor
	 * @param idBean   用户信息
	 * @param groupid  设备组
	 * @param period   时间范围
	 * @param itemkey  键值
	 * @param showNum  展示数目
	 * @param order    正序倒叙
	 * @return
	 */
	public static List getTOPNRateByCommonTwo(SQLExecutor executor, IIdentityBean idBean,String statisticalType,Integer period,String itemkey,int showNum, boolean order){
        TopNHelper helper = new TopNHelper(showNum, "value", order);
        Long[] groupids = null;//
		String[] keys = null;
		if("server".equals(statisticalType)){//服务器
			groupids = new Long[]{IMonGroup.MON_SERVER_LINUX.id(),IMonGroup.MON_SERVER_WINDOWS.id()};
		}else if("vmhost".equals(statisticalType)){//云主机
			groupids = new Long[]{IMonGroup.MON_VM.id()};
		}else if("switches".equals(statisticalType)){//交换机
			groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id()};
		}
		
		if("server_ioread".equals(itemkey)){//服务器 IO读取速率
			keys = new String[]{ItemsKey.DISK_READ_SPEED.getValue(),ItemsKey.DISK_READ_REQUEST_BW_WINDOWS.getValue()};
		}else if("server_iowrite".equals(itemkey)){//服务器 IO写速率 
			keys = new String[]{ItemsKey.DISK_WRITE_SPEED.getValue(),ItemsKey.DISK_WRITE_REQUEST_BW_WINDOWS.getValue()};
		}else if("vm_ioread".equals(itemkey)){//云主机 IO读取速率
			keys = new String[]{ItemsKey.DISK_READ_SPEED_VM_LINUX.getValue(),ItemsKey.DISK_READ_SPEED_VM_WINDOWS.getValue()};
		}else if("vm_iowrite".equals(itemkey)){//云主机 IO写速率 
			keys = new String[]{ItemsKey.DISK_WRITE_SPEED_VM_LINUX.getValue(),ItemsKey.DISK_WRITE_SPEED_VM_WINDOWS.getValue()};
		}else if("netuppacket".equals(itemkey)){//交换机 上行丢包率
			keys = new String[]{ItemsKey.CISCO_NET_IFINERRORS.getValue(),ItemsKey.COMMON_NET_IFINERRORS.getValue()};
		}else if("netdownpacket".equals(itemkey)){//交换机 下行丢包率
			keys = new String[]{ItemsKey.CISCO_NET_IFOUTERRORS.getValue(),ItemsKey.COMMON_NET_IFOUTERRORS.getValue()};
		}else if("netuprate".equals(itemkey)){//交换机 上行速率
			keys = new String[]{ItemsKey.NET_UP_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFINOCTETS.getValue()};
		}else if("netupdown".equals(itemkey)){//交换机 下行速率
			keys = new String[]{ItemsKey.NET_DOWN_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFOUTOCTETS.getValue()};
		}
        
		if("vmhost".equals(statisticalType)){
			for(int i=0;i < groupids.length;i++){
				getTopNRateByKeys(executor,idBean,groupids[i],period,keys,helper);
			}
		}else{
			for(int i=0;i < groupids.length;i++){
				getTopNRate(executor,idBean,groupids[i],period,keys[i],helper);
			}
		}
		return helper.getResult();
	}
	
	
	public static void getTopNRate(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,TopNHelper helper){
		CArray<Map> hosts = null;
		hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double value = 0d;
			if(itemkey != null) {
				 Map item = reset(DataDriver.getItemId(executor, hostid, itemkey));
				 if(Defines.RDA_FLAG_DISCOVERY_PROTOTYPE==Nest.value(item, "flags").asInteger()){
					CArray<Map> items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, itemkey);
					if(!empty(items))
						value = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
				 }else{
					if(!empty(item))
						value = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
				 }
				 helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(value,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			}
		}
	}
	
	public static void getTopNRateByKeys(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String[] itemkeys,TopNHelper helper){
		CArray<Map> hosts = null;
		hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double value = 0d;
			for(String key : itemkeys){
				   Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, key));
				   if(!empty(item)){
					   Double cpuLoad = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
						helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(cpuLoad, Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT))); 
				   }
			   }
		}
	}
	
	
	/**
	 * 获取网络使用率前N项 包括正序和倒序
	 * @param executor
	 * @param idBean
	 * @param showNum	需要返回的数据量
	 * @return
	 */
	public static List getTOPNRateByNet(SQLExecutor executor,IIdentityBean idBean,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName();
		for(Entry<Object, Map> host: hosts.entrySet()){
			Long hostid = (Long)host.getKey();
			String hostName = Nest.value(host.getValue(), "name").asString();
			CArray<PrototypeValues> netValues = CommonUtils.keysOfPrototypeValues(array(host.getValue()), CommonUtils.KEYS_NET);
			
			Double up = netValues.get("up").sum().convertUnit(NormalValue.POW_M).round(2).value().asDouble(true);
			Double upMax = netValues.get("upMax").sum().convertUnit(NormalValue.POW_M).round(2).value().asDouble(true);
			Double down = netValues.get("down").sum().convertUnit(NormalValue.POW_M).round(2).value().asDouble(true);
			Double downMax = netValues.get("downMax").sum().convertUnit(NormalValue.POW_M).round(2).value().asDouble(true);
			
			Double netUsedRate;
			Double used = 0d, total = 0d;
			if(up!=null && down!=null) {
				used = up + down;
			}
			if(upMax!=null && upMax!=null) {
				total = upMax + downMax;
			}
			netUsedRate = total==0d? 0: Cphp.round(100*used/total, 2);
			
			helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", netUsedRate));
		}
		
		return helper.getResult();
	}
	
	/**
	 * 获取磁盘使用率前N项 包括正序和倒序
	 * @param executor
	 * @param idBean
	 * @param showNum	需要返回的数据量
	 * @return
	 */
	public static List getTOPNRateByDisk(SQLExecutor executor,IIdentityBean idBean,int showNum, boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName();
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Long groupId = Nest.value(en.getValue(),"groupid").asLong();
			
			ItemsKey key = null;
			if(IMonConsts.MON_SERVER_LINUX == groupId){
				key = ItemsKey.DISK_USELV_KEY;
			}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){
				key = ItemsKey.DISK_USELV_KEY_WINDOWS;
			}
			
			if(key != null) {
				Double cpuLoad = LatestValueHelper.buildByPrototypeKey(hostid, key.getValue()).values().avg().round(2).value().asDouble();
				helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", cpuLoad));
			}
		}
		
		return helper.getResult();
	}
	
	
	
	
	/**
	 * 获取某个设备的某一类监控指标最新值(例：磁盘各个分区的利用率)
	 * @param executor  sql
	 * @param idBean	租户ID
	 * @param key 		键值
	 * @param hostid	设备ID
	 * @param isFormat	是否需要对数据格式化
	 * @param isHaveUnit是否保留格式化数据后的单位符号 
	 * @return
	 */
	public static PrototypeValues getPrototypeTargetLastValue(String key,String hostid){
		return LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), key).values();
	}
	
	/**
	 * 获取某个设备的某个监控指标最新值
	 * @param executor  sql
	 * @param idBean	租户ID
	 * @param key 		键值
	 * @param hostid	设备ID
	 * @param isFormat	是否需要对数据格式化
	 * @param isHaveUnit是否保留格式化数据后的单位符号 
	 * @return
	 */
	public static String getTargetLastValue(SQLExecutor executor,IIdentityBean idBean,String key,String hostid,boolean isFormat,boolean isHaveUnit){
		NormalValue nv = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), key).value();
		return isFormat? nv.out().format(): nv.out().print();
	}

	public static String getTargetLastValue(SQLExecutor executor,IIdentityBean idBean,CArray<ItemsKey> keyCArray,String hostid,boolean isFormat,boolean isHaveUnit){
		String value = LatestValueHelper.NA;
		for(ItemsKey key:keyCArray){
			value = getTargetLastValue(executor,idBean,key.getValue(),hostid,isFormat,isHaveUnit);
			if(!LatestValueHelper.NA.equals(value))
				return value;
		}
		return value;
	}
	
	public static String getTargetKey(SQLExecutor executor,IIdentityBean idBean,CArray<ItemsKey> keyCArray,String hostid,boolean isFormat,boolean isHaveUnit){
		String value = LatestValueHelper.NA;
		String targetKey = "";
		for(ItemsKey key:keyCArray){
			targetKey = key.getValue();
			value = getTargetLastValue(executor,idBean,key.getValue(),hostid,isFormat,isHaveUnit);
			if(!LatestValueHelper.NA.equals(value))
				return targetKey;
		}
		return targetKey;
	}
	
	public static String getActionUrlToMultiLine(String hostId,CArray<ItemsKey> keys,int period,int width){
		String urlParaSep = "&";
		String urlParaHead = "keys[";
		String urlParaTail = "]=";
		String sTime = Cphp.date(Defines.TIMESTAMP_FORMAT);
		String action = "chartmultiline.action?hostid="+hostId+urlParaSep+"period="+period+urlParaSep+"stime="+sTime+urlParaSep+"updateProfile=0&width="+width;
		int length = keys.size();
		for(int i=0;i<length;i++){
			action += urlParaSep+urlParaHead+i+urlParaTail+keys.get(i).name();
		}
		return action;
	}
	
	public static String status2style(int status) {
		CArray<String> statuses = map(
			0, "enabled",
			1, "disabled"
		);

		if (isset(statuses,status)) {
			return statuses.get(status);
		} else {
			return "unknown";
		}
	}
	
	public static String encode(String value){
		if (value != null && value.length() > 0) {
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
		}
		return value;
	}
	
	/**
	 * 测试函数可用性
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
}
