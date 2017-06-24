package com.isoft.common.util;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.types.CArray.array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.isoft.iradar.inc.Defines.SPACE;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.TopNHelper;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class DataUtil {

	
	public static List getHostItemValueData(SQLExecutor executor, IIdentityBean idBean,String statisticalType,int period,boolean CSV_EXPORT){
		List<Map> data=new ArrayList<Map>(); 
		Long[] groupids = null;//
		 String[][] keys = null;
		 Boolean isNeedMaxs[] = null;
		 String[] itemsname = null;
		 if("server".equals(statisticalType)){//服务器
			groupids = new Long[]{IMonGroup.MON_SERVER_LINUX.id(),
					              IMonGroup.MON_SERVER_WINDOWS.id()};
		 }else if("vmhost".equals(statisticalType)){//云主机
			groupids = new Long[]{IMonGroup.MON_VM.id()};
		 }else if("switches".equals(statisticalType)){
			 groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),
					               IMonGroup.MON_COMMON_NET.id(),
					               IMonGroup.MON_NET_HUAWEI_SWITCH.id()};
		 }
		 
		 if("server".equals(statisticalType)){
			 keys = new String[][]{{ItemsKey.CPU_USER_RATE.getValue(),//linux类型 服务器cpu使用率
				                   ItemsKey.MEMORY_USELV_KEY.getValue(),//服务器 内存使用率
				                   ItemsKey.DISK_USELV_KEY.getValue(),//服务器 硬盘使用率
				                   ItemsKey.DISK_READ_SPEED.getValue(),//服务器 读速率
				                   ItemsKey.DISK_WRITE_SPEED.getValue(),//服务器 写速率
				                   ItemsKey.NET_UP_SPEED.getValue(),//服务器 网络接口上行IO
				                   ItemsKey.NET_DOWN_SPEED.getValue()//服务器 网络接口下行IO
				                   },
					               {ItemsKey.CPU_USER_RATE_WINDOWS.getValue(),//Windowns类型 服务器cpu使用率
				                	ItemsKey.MEMORY_USELV_KEY_WINDOWS.getValue(),//服务器 内存使用率
				                	ItemsKey.DISK_USELV_KEY_WINDOWS.getValue(),//服务器 硬盘使用率
				                	ItemsKey.DISK_READ_REQUEST_BW_WINDOWS.getValue(),//服务器 读速率
				                	ItemsKey.DISK_WRITE_REQUEST_BW_WINDOWS.getValue(),//服务器 写速率
				                	ItemsKey.NET_UP_FLOW_WINDOWS.getValue(),//服务器 网络上行速率
				                	ItemsKey.NET_DOWN_FLOW_WINDOWS.getValue()}};//服务器 网络下行速率
			 itemsname = new String[]{"cpuavg","memoryavg","disk","ioread","iowrite","upipos","downipos"};
			 isNeedMaxs= new Boolean[]{true,true,false,false,false,false,false};
		 }else if("vmhost".equals(statisticalType)){
			 keys = new String[][]{{ItemsKey.CPU_RATE_VM_LINUX.getValue(),//linux类型 云主机cpu使用率
				                    ItemsKey.MEMORY_RATE_VM.getValue(),//云主机 内存使用率
				                    ItemsKey.DISK_RATE_VM.getValue(),//云主机 硬盘使用率
				                    ItemsKey.DISK_READ_SPEED_VM_LINUX.getValue(),//云主机 IO读速率
				                    ItemsKey.DISK_WRITE_SPEED_VM_LINUX.getValue(),//云主机 IO写速率
				                    ItemsKey.NET_UP_FLOW_VM.getValue(),//云主机 网络上行IO
				                    ItemsKey.NET_DOWN_FLOW_VM.getValue()//云主机 网络下行IO
				                    },
		                            {ItemsKey.CPU_RATE_VM_WINDOWS.getValue(),//Windowns类型 云主机cpu使用率
				                     ItemsKey.MEMORY_RATE_VM.getValue(),//云主机 内存使用率
					                 ItemsKey.DISK_RATE_VM.getValue(),//云主机 硬盘使用率
				                     ItemsKey.DISK_READ_SPEED_VM_WINDOWS.getValue(),//云主机 IO读速率
		            	             ItemsKey.DISK_WRITE_SPEED_VM_WINDOWS.getValue(),//云主机 IO写速率
					                 ItemsKey.NET_UP_FLOW_VM.getValue(),//云主机 网络上行IO
					                 ItemsKey.NET_DOWN_FLOW_VM.getValue()//云主机 网络下行IO
		            	             }};
             itemsname = new String[]{"cpuavg","memoryavg","disk","ioread","iowrite","upipos","downipos"};
		 }else if("switches".equals(statisticalType)){
			 keys = new String[][]{{ItemsKey.MEMORY_RATE_VM.getValue(),//思科交换机 上行丢包率
                 ItemsKey.CISCO_NET_IFOUTERRORS.getValue(),//思科交换机 下行丢包率
                 ItemsKey.NET_UP_SPEED_NET_CISCO.getValue(),//思科交换机 上行速率
                 ItemsKey.NET_DOWN_SPEED_NET_CISCO.getValue()//思科交换机 上行速率
                 },
                 {ItemsKey.COMMON_NET_IFINERRORS.getValue(),//交换机 上行丢包率
                  ItemsKey.COMMON_NET_IFOUTERRORS.getValue(),//交换机 下行丢包率
	              ItemsKey.COMMON_NET_IFINOCTETS.getValue(),//交换机 上行速率
                  ItemsKey.COMMON_NET_IFOUTOCTETS.getValue()//交换机 上行速率
 	             },
 	            {ItemsKey.COMMON_NET_IFINERRORS.getValue(),//华为交换机 上行丢包率
	              ItemsKey.COMMON_NET_IFOUTERRORS.getValue(),//华为交换机 下行丢包率
	              ItemsKey.COMMON_NET_IFINOCTETS.getValue(),//华为交换机 上行速率
	              ItemsKey.COMMON_NET_IFOUTOCTETS.getValue()//华为交换机 上行速率
 	 	         }};
             itemsname = new String[]{"netuppacket","netdownpacket","netuprate","netupdown"};
             isNeedMaxs= new Boolean[]{false,false,false,false};
		}
		 if("vmhost".equals(statisticalType)){
			 for(int i=0;i<1;i++){
				 getTopNRateByKeys(executor,idBean,groupids[0],period,itemsname,keys,data,CSV_EXPORT);
			 } 
		 }else{
			 for(int i=0;i<groupids.length;i++){
				 getTopNRateByKeys(executor,idBean,groupids[i],period,itemsname,keys[i],data,isNeedMaxs,CSV_EXPORT);
			 }
		 }
		
		 return data;
	}
	//一个类型一个指标方法
	public static void getTopNRateByKeys(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String[] itemnames,String[] itemkeys,List<Map> data,Boolean[] isNeedMaxs,boolean CSV_EXPORT){
		List<Map> hosts = null;
		hosts = queryHostIDAndName(groupid);
		String[] maxarray={"maxcpu","maxmemery"};
		for(Map host:hosts){
			Map hostmap=new LinkedHashMap();
			Long hostid =Nest.value(host, "hostid").asLong();
			hostmap.put("name", Nest.value(host, "name").asString());
			hostmap.put("hostip", Nest.value(host, "hostip").asString());
			for(int i=0;i<itemkeys.length;i++){
				 CArray<Map> item = DataDriver.getItemsBykey(IRadarContext.getSqlExecutor(), hostid, itemkeys[i]);
				 Double avgvalue = 0d;
				 String units="";
				 if(!empty(item)){
					 units = Nest.value(item.get(0), "units").asString();
					 if(item.size()>1){
						 avgvalue = Cphp.round(ReportUtil.getSomeTimeAvgNamePrototypeKey(executor,item,idBean.getTenantId(),period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
					 }else{
						 avgvalue = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item.get(0), idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
					 }
				 }
				 if(avgvalue == 0d){
					 hostmap.put(itemnames[i], empty(item)? "--": ("0"+ units));
				 }else{
					 hostmap.put(itemnames[i],avgvalue.toString()+ (!CSV_EXPORT?SPACE:"") + units);
				 }
				 if(isNeedMaxs[i]){
					 Double maxvalue = 0d;
					 if(!empty(item)){
						 if(item.size()>1){
							 maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxNamePrototypeKey(executor,item,idBean.getTenantId(),period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
						 }else{
							 maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxName(executor, item.get(0), idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT); 
						 }
					 }
					 if(maxvalue == 0d){
						 hostmap.put(maxarray[i], empty(item)? "--": ("0"+ units));
					 }else{
						 hostmap.put(maxarray[i], maxvalue.toString() + (!CSV_EXPORT?SPACE:"") + units); 
					 }
				 }
			}
			
			data.add(hostmap);
		}
	
	}
	
	//一个类型多个指标方法
	public static void getTopNRateByKeys(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String[] itemnames,String[][] itemkeyss,List<Map> data,boolean CSV_EXPORT){
		List<Map> hosts = null;
		hosts = queryHostIDAndName(groupid);
		String[] maxarray={"maxcpu","maxmemery"};
		String[] itemkeys= new String[]{};
		for(Map host:hosts){
			Map hostmap=new LinkedHashMap();
			Long hostid =Nest.value(host, "hostid").asLong();
			hostmap.put("name", Nest.value(host, "name").asString());
			hostmap.put("hostip", Nest.value(host, "hostip").asString());
			String osType_vm = CommonUtils.getTargetLastValue(executor,idBean, array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid.toString(), false, false);
			if(osType_vm != null && osType_vm.contains("Windows")){
				itemkeys=itemkeyss[0];
			}else{
				itemkeys=itemkeyss[1];
			}
			for(int i=0;i<itemkeys.length;i++){
				 Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, itemkeys[i]));
				 Double cpuLoad = 0d;
				 String units = "";
				 if(!empty(item)){
					 units = Nest.value(item, "units").asString();
					 cpuLoad = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
				 }
				 if(cpuLoad == 0d){
					 hostmap.put(itemnames[i], empty(item)? "--": ("0"+ units));
				 }else{
					 hostmap.put(itemnames[i],cpuLoad.toString()+ (!CSV_EXPORT?SPACE:"") + units);
				 }
				
				 if(i<2){
					 Double maxvalue = 0d;
					 if(!empty(item)){
						 maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
					 }
					 if(maxvalue == 0d){
						 hostmap.put(maxarray[i], empty(item)? "--": ("0"+ units));
					 }else{
						 hostmap.put(maxarray[i], maxvalue.toString()+ (!CSV_EXPORT?SPACE:"") + units); 
					 }
					 
				 }
			}
			
			data.add(hostmap);
		}
	
	}
	
	public static List<Map> queryHostIDAndName(Long... groupIds){
		List<Map> hostdata=new ArrayList<Map>();
		for(Long groupid: groupIds) {
			CHostGet options = new CHostGet();
			options.setOutput(new String[] {"hostid","name"});
			options.setSelectInterfaces(new String[]{"ip"});
			options.setGroupIds(groupid);
			CArray<Map> hosts = API.Host(IRadarContext.getIdentityBean(), IRadarContext.getSqlExecutor()).get(options);
			
			for (Map host: hosts) {//磁盘使用率隐藏
			    Map hostdt =new HashMap();
			    hostdt.put("name", Nest.value(host, "name").asString());
			    hostdt.put("hostid", Nest.value(host, "hostid").asString());
			    hostdt.put("hostip", Nest.value(host, "interfaces","0","ip").asString());
			    hostdt.put("groupid", groupid);
			    hostdata.add(hostdt);
		   }
		}
		return hostdata;
	}
	
	public static  List getTOPNRate(SQLExecutor executor, IIdentityBean idBean,String statisticalType,
			Integer period,String itemkey,int showNum, boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
        Long[] groupids = null;//
		String[] keys = null;
		 if("switches".equals(statisticalType)){//交换机
			groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id(),IMonGroup.MON_NET_HUAWEI_SWITCH.id()};
		}
		//上行为发送=out，下行为接受=in
		if("netuppacket".equals(itemkey)){//交换机 上行丢包率 
			keys = new String[]{ItemsKey.CISCO_NET_IFOUTERRORS.getValue(),ItemsKey.COMMON_NET_IFOUTERRORS.getValue(),ItemsKey.COMMON_NET_IFOUTERRORS.getValue()};
		}else if("netdownpacket".equals(itemkey)){//交换机 下行丢包率
			keys = new String[]{ItemsKey.CISCO_NET_IFINERRORS.getValue(),ItemsKey.COMMON_NET_IFINERRORS.getValue(),ItemsKey.COMMON_NET_IFINERRORS.getValue()};
		}else if("netuprate".equals(itemkey)){//交换机 上行速率
			keys = new String[]{ItemsKey.NET_UP_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFOUTOCTETS.getValue(),ItemsKey.COMMON_NET_IFOUTOCTETS.getValue()};
		}else if("netupdown".equals(itemkey)){//交换机 下行速率
			keys = new String[]{ItemsKey.NET_DOWN_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFINOCTETS.getValue(),ItemsKey.COMMON_NET_IFINOCTETS.getValue()};
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
	
	public static void getTopNRateByKeys(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String[] keys,TopNHelper helper){
		CArray<Map> hosts = null;
		hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double value = 0d;
			int i=0;
			for(String key : keys){
				   Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, key));
				   if(!empty(item)){
					   Double avgvalue = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
					   Double maxvalue = Cphp.round(ReportUtil.getSomeTimeMaxName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
					   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(avgvalue, Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT))); 
					   if(avgvalue !=0){
						   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", avgvalue,"maxvalue",maxvalue));
						   break; 
					   }
					   i++;
					   if(i == keys.length){
						   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", avgvalue,"maxvalue",maxvalue)); 
					   }
				   }
			   }
		}
	}
	
	
	public static void getTopNRate(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,TopNHelper helper){
		CArray<Map> hosts = null;
		hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double avgvalue = 0d,maxvalue=0d;
			if(itemkey != null) {
				 Map item = reset(DataDriver.getItemId(executor, hostid, itemkey));
				 if(Defines.RDA_FLAG_DISCOVERY_PROTOTYPE==Nest.value(item, "flags").asInteger()){
					CArray<Map> items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, itemkey);
					if(!empty(items)){
						avgvalue = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
						maxvalue = ReportUtil.getSomeTimeMaxNamePrototypeKey(executor, items, idBean.getTenantId(), period);
					}
						
				 }else{
					if(!empty(item)){
						avgvalue = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
						maxvalue = ReportUtil.getSomeTimeMaxName(executor, item, idBean.getTenantId(), period);
					}
						
				 }
				 helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(avgvalue,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT),
						                                                       "maxvalue", Cphp.round(maxvalue,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			}
		}
	}
	
	public static void main(String agrs[]){
		
	}

}
