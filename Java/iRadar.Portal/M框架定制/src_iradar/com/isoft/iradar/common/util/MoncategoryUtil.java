package com.isoft.iradar.common.util;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.reset;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class MoncategoryUtil {
	
	public final static Map<Long,String> Moncategory_Map = EasyMap.build(IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_CATE_SERVER,
			    IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_CATE_SERVER,//监控类别--服务器 
			    IMonConsts.MON_COMMON_NET,IMonConsts.MON_CATE_NET_DEV,//监控类别--网络设备 
			    IMonConsts.MON_NET_CISCO,IMonConsts.MON_CATE_NET_DEV,//监控类别--网络设备 
			    IMonConsts.MON_NET_HUAWEI_SWITCH,IMonConsts.MON_CATE_NET_DEV,//监控类别--网络设备 
			    IMonConsts.MON_NET_ZHONGXING_SWITCH,IMonConsts.MON_CATE_NET_DEV,//监控类别--网络设备 
//			    IMonConsts.MON_STORAGE,IMonConsts.MON_CATE_STORAGE,//监控类别--存储设备
			    IMonConsts.MON_CLOUD_CONTROLER,IMonConsts.MON_CATE_PLA_SER, //监控类别--平台服务
			    IMonConsts.MON_CLOUD_COMPUTER,IMonConsts.MON_CATE_PLA_SER, //监控类别--平台服务
			    IMonConsts.MON_CLOUD_CEPH,IMonConsts.MON_CATE_PLA_SER, //监控类别--平台服务
			    IMonConsts.MON_CLOUD_NETWORK,IMonConsts.MON_CATE_PLA_SER, //监控类别--平台服务
			    IMonConsts.MON_CLOUD_WEB,IMonConsts.MON_CATE_PLA_SER, //监控类别--平台服务
			    IMonConsts.MON_DB_MySQL,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_DB_Oracle,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_DB_DM,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_DB_DB2,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_DB_SQLSERVER,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_DB_MONGODB,IMonConsts.MON_CATE_DB,//监控类别--数据库 
			    IMonConsts.MON_MIDDLE_TOMCAT,IMonConsts.MON_CATE_MIDWARE,//监控类别--中间件 
			    IMonConsts.MON_MIDDLE_IIS,IMonConsts.MON_CATE_MIDWARE,//监控类别--中间件 
			    IMonConsts.MON_MIDDLE_WEBLOGIC,IMonConsts.MON_CATE_MIDWARE,//监控类别--中间件 
			    IMonConsts.MON_MIDDLE_WEBSPHERE,IMonConsts.MON_CATE_MIDWARE,//监控类别--中间件 
			    IMonConsts.MON_CLUSTER,IMonConsts.MON_CATE_OTHER);//监控类别--其他 

	public final static Map<String,CArray<Long>> moncategoryGroupMapping = EasyMap.build(IMonConsts.MON_CATE_SERVER,CArray.array(IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_SERVER_LINUX),
																						 IMonConsts.MON_CATE_NET_DEV,CArray.array(IMonConsts.MON_COMMON_NET,IMonConsts.MON_NET_CISCO,IMonConsts.MON_NET_HUAWEI_SWITCH,IMonConsts.MON_NET_ZHONGXING_SWITCH),
																						 IMonConsts.MON_CATE_PLA_SER,CArray.array(IMonConsts.MON_CLOUD_CONTROLER,IMonConsts.MON_CLOUD_COMPUTER,IMonConsts.MON_CLOUD_CEPH,IMonConsts.MON_CLOUD_NETWORK,IMonConsts.MON_CLOUD_WEB),
																						 IMonConsts.MON_CATE_DB,CArray.array(IMonConsts.MON_DB_MySQL,IMonConsts.MON_DB_Oracle,IMonConsts.MON_DB_DB2,IMonConsts.MON_DB_SQLSERVER,IMonConsts.MON_DB_MONGODB),
																						 IMonConsts.MON_CATE_MIDWARE,CArray.array(IMonConsts.MON_MIDDLE_TOMCAT,IMonConsts.MON_MIDDLE_IIS,IMonConsts.MON_MIDDLE_WEBLOGIC,IMonConsts.MON_MIDDLE_WEBSPHERE));
	
	
	public static String getMoncategoryName(Long groupid){	
		for(Entry<Long,String> e : Moncategory_Map.entrySet()){
			Long key = e.getKey();
			if(key == groupid){
				return e.getValue();
			}
		}
		return IMonConsts.MON_CATE_SERVER;
	}
	
	private final static String SQL_GET_PID = "SELECT pid FROM sys_func WHERE id=#{id}";
	public static String getPidByFuncId(SQLExecutor executor, String funcId){
		Map paramMap = new HashMap();
		paramMap.put("id", funcId);
		CArray<Map> pids = DBUtil.DBselect(executor, SQL_GET_PID, paramMap);
		String MoncategoryName=null;
		if(!empty(pids)){
			MoncategoryName= Nest.value(reset(pids),"pid").asString();
		}else if(empty(MoncategoryName))
			MoncategoryName = getMoncategoryName(Long.valueOf(funcId));
		return MoncategoryName;
	}
	
//	private final static String SQL_GET_GROUPID = "SELECT id FROM sys_func WHERE pid=#{pid}";
	private final static String SQL_GET_GROUPID = "SELECT id " +
												  "FROM sys_func WHERE pid=#{pid} " +
												  "AND id IN (SELECT groupid FROM groups WHERE tenantid<>'|' AND internal<>'1')";
	public static CArray<Long> getGroupIdsByPId(SQLExecutor executor, String funcId){
		Map paramMap = new HashMap();
		paramMap.put("pid", funcId);
		CArray<Map> groupIds = DBUtil.DBselect(executor, SQL_GET_GROUPID, paramMap);
		CArray<Long> groupIdsCA = FuncsUtil.rda_objectValues(groupIds, "id");
		CArray defaultMapping = Nest.value(moncategoryGroupMapping, funcId).asCArray();
		CArray allGroups = Cphp.array_merge(groupIdsCA,defaultMapping);
		CArray<Long> allGroupIds = CArray.array();
		for(Object group:allGroups){
			allGroupIds.add(Nest.as(group).asLong());
		}
		return allGroupIds;
	}
	
//	public static Long[] getGroupidByMoncategory(String funcId){
//		Long[] groupid=null;
//		if(IMonConsts.MON_CATE_SERVER.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_SERVER_WINDOWS,IMonConsts.MON_SERVER_LINUX};
//		}else if(IMonConsts.MON_CATE_NET_DEV.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_COMMON_NET,IMonConsts.MON_NET_CISCO,IMonConsts.MON_NET_HUAWEI_SWITCH,IMonConsts.MON_NET_ZHONGXING_SWITCH};
//		}else if(IMonConsts.MON_CATE_STORAGE.equals(funcId)){
////			groupid = new Long[]{IMonConsts.MON_STORAGE};
//		}else if(IMonConsts.MON_CATE_PLA_SER.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_CLOUD_CONTROLER,IMonConsts.MON_CLOUD_COMPUTER,IMonConsts.MON_CLOUD_CEPH,IMonConsts.MON_CLOUD_NETWORK,IMonConsts.MON_CLOUD_WEB};
//		}else if(IMonConsts.MON_CATE_DB.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_DB_MySQL,IMonConsts.MON_DB_Oracle,IMonConsts.MON_DB_DM,IMonConsts.MON_DB_DB2,IMonConsts.MON_DB_SQLSERVER,IMonConsts.MON_DB_MONGODB};
//		}else if(IMonConsts.MON_CATE_MIDWARE.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_MIDDLE_TOMCAT,IMonConsts.MON_MIDDLE_IIS,IMonConsts.MON_MIDDLE_WEBLOGIC,IMonConsts.MON_MIDDLE_WEBSPHERE};
//		}else if(IMonConsts.MON_CATE_OTHER.equals(funcId)){
//			groupid = new Long[]{IMonConsts.MON_CLUSTER};
//		}
//		return groupid;
//	}
	
	public static void main(String args[]){
		System.out.println(getMoncategoryName(101l));
	}

}
