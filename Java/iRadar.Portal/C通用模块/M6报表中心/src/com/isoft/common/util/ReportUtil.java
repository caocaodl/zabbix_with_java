package com.isoft.common.util;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.bindec;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_num2bitstr;
import static com.isoft.iradar.inc.FuncsUtil.rda_str_revert;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.TopNHelper;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.action.core.MaintenanceAction;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.bean.reportForms.EventsReportForms;

public class ReportUtil {

	/**  获取云平台的某段时间内虚拟内核数目和和内存
	 * @param executor
	 * @param idBean
	 * @param groupid
	 * @param period
	 * @param key
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List getTOPNRateBySystem(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		CHostGet cHostGet = new CHostGet();
		cHostGet.setOutput(new String[]{"hostid","name"});
		cHostGet.setGroupIds(IMonConsts.MON_CLOUD_CONTROLER.longValue());
		cHostGet.setPreserveKeys(true);		
		CArray<Map> _hosts = API.Host(idBean, executor).get(cHostGet);	
		Map host=Cphp.reset(_hosts);
		long hostid=Nest.value(host, "hostid").asLong();
		String hostName = Nest.value(host, "name").asString();
		
		String totalkey = null;
		String userkey =  null;
		if("core".equals(itemkey)){//虚拟内核数
			totalkey = ItemsKey.VM_CORE_TOTAL.getValue();
			userkey =  ItemsKey.VM_CORE_USED.getValue();
			hostName = "云平台虚拟内核数";
		}else{
			totalkey = ItemsKey.VM_MEMORY_TOTAL.getValue();
			userkey =  ItemsKey.VM_MEMORY_USED.getValue();
			hostName = "云平台内存总量";
		}
		Double coreTotalNum = 0d,value=0d,coreUsedNum = 0d;
		 if(totalkey != null) {
			 Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid,totalkey ));
			   if(!empty(item)){
				   coreTotalNum = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
			   }
		}
	    if(userkey != null) {
			 Map item = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid,userkey ));
			   if(!empty(item)){
				   coreUsedNum = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
			   }
		}
	    if(coreTotalNum<=0){
	    	coreTotalNum=1d;
	    }
		value = coreUsedNum/coreTotalNum;
	    helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", value));
		return helper.getResult();
	}
	

	/** 一个统计类型多个分组方式
	 * @param executor
	 * @param idBean
	 * @param statisticalType  统计类型
	 * @param keytype 监控指标类型
	 * @param period
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List GroupAndItemkey(SQLExecutor executor,IIdentityBean idBean,String statisticalType,String keytype,int period,int showNum,boolean order){
		return GroupAndItemkey(executor,idBean,statisticalType,keytype,period,showNum,order,false);
	}
	
	public static List GroupAndItemkey(SQLExecutor executor,IIdentityBean idBean,String statisticalType,String keytype,int period,int showNum,boolean order,boolean iStenant){
		String[] keys = null;
		if("connectednum".equals(keytype)){//数据库 当前连接数 
			keys = new String[]{ItemsKey.THREADS_CONNECTED_MYSQL.getValue(),
					            ItemsKey.SESSION_SYSTEM_ORACLE.getValue(),
					            ItemsKey.SESSIONS_PAGEWRITES.getValue(),
					            ItemsKey.DB_DB2_APPLCOUNT.getValue(),
					            ItemsKey.DB_MONGODB_CONNECTIONS_CURRENT.getValue()};
		}else if("sessionsnum".equals(keytype)){//中间件 tomcat 性能指标
			keys = new String[]{ItemsKey.ACTIVESESSIONS_TOMCAT.getValue(),
					            ItemsKey.MID_WEBSPHERE_JVMTHREADCOUNT.getValue(),
					            ItemsKey.MID_WEBLOGIC_JMSSERVERSCURRENTCOUNT.getValue(),
					            ItemsKey.MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS.getValue()};
		}else if("errors".equals(keytype)){//中间件 tomcat 每秒错误次数
			keys = new String[]{ItemsKey.ERRORCOUNT_TOMCAT.getValue()};
		}else if("uptime".equals(keytype)){//中间件 tomcat 运行状态
			keys = new String[]{ItemsKey.UPTIME_TOMCAT.getValue()};
		}
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		if(iStenant){
			Long grouptype = null;
			if("database".equals(statisticalType)){//数据库
				grouptype = 1L;
			}else if("tomcatmiddle".equals(statisticalType)){//中间件
				grouptype = 2L;
			}
			for(int i=0;i < 1;i++){
				getTopNRate(executor,idBean,grouptype,period,keys[i],helper);
			}
		}else{
			Long[] groupids = null;
			
			if("database".equals(statisticalType)){//数据库
				groupids = new Long[]{IMonGroup.MON_DB_MYSQL.id(),
						              IMonGroup.MON_DB_ORACLE.id(),
						              IMonGroup.MON_DB_SQLSERVER.id(),
						              IMonGroup.MON_DB_DB2.id(),
						              IMonGroup.MON_DB_MONGODB.id()};
			}else if("tomcatmiddle".equals(statisticalType)){//中间件
				groupids = new Long[]{IMonGroup.MON_MIDDLE_TOMCAT.id(),
						              IMonGroup.MON_MIDDLE_WEBSPHERE.id(),
						              IMonGroup.MON_MIDDLE_WEBLOGIC.id(), 
						              IMonGroup.MON_MIDDLE_IIS.id()};
			}
			for(int i=0;i < groupids.length;i++){
				getTopNRate(executor,idBean,groupids[i],period,keys[i],helper);
			}
			
		}
		return helper.getResult();
		
	}
	
	public static void getTopNRate(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,TopNHelper helper,boolean iStenant){
		CArray<Map> hosts = null;
		if(iStenant){
			hosts = getHostsByGroupT(executor,idBean,groupid);
		}else{
			hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		}
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double value = 0d;
			if(itemkey != null) {
				 Map item = reset(DataDriver.getItemId(executor, hostid, itemkey));
				   if(!empty(item)){
					   value = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
				   }
				helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", value));
			}
		}
	}
	/** 求多分组某段时间内的均值
	 * @param executor
	 * @param idBean
	 * @param groupid
	 * @param period
	 * @param itemkey
	 * @param helper
	 */
	public static void getTopNRate(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,TopNHelper helper){
		//获取该设备分组下的所有设备
		getTopNRate(executor,idBean,groupid,period,itemkey,helper,false);
	}
	
	
	public static CArray<Map> getHostsByGroupT(SQLExecutor executor,IIdentityBean idBean,Long grouptype){
		CArray<Map> hosts=array();
		CHostGet option = new CHostGet();
		option.setOutput(new String[]{"hostid", "name"});
		option.setGroupIds(IMonConsts.DISCOVERED_HOSTS);
		if( 1 == grouptype){
			option.setFilter("ipmi_username", IMonConsts.A_TYPE_MYSQL);
		}else if( 2 == grouptype ){
			option.setFilter("ipmi_username", IMonConsts.A_TYPE_TOMCAT);
		}
		option.setEditable(true);
		hosts = API.Host(idBean, executor).get(option);
		return hosts;
	}
	
	public static List<Map> getHostsByGroupTlist(SQLExecutor executor,IIdentityBean idBean,Long grouptype){
		List<Map> hostdata=new ArrayList<Map>();
		CArray<Map> hosts=array();
		CHostGet option = new CHostGet();
		option.setOutput(new String[]{"hostid", "name"});
		option.setGroupIds(IMonConsts.DISCOVERED_HOSTS);
		if( 1 == grouptype){
			option.setFilter("ipmi_username", IMonConsts.A_TYPE_MYSQL);
		}else if( 2 == grouptype ){
			option.setFilter("ipmi_username", IMonConsts.A_TYPE_TOMCAT);
		}
		option.setEditable(true);
		hosts = API.Host(idBean, executor).get(option);
		for (Map host: hosts) {//磁盘使用率隐藏
		    Map hostdt =new HashMap();
		    hostdt.put("name", Nest.value(host, "name").asString());
		    hostdt.put("hostid", Nest.value(host, "hostid").asString());
		    hostdata.add(hostdt);
	   }
		return hostdata;
	}
	
	public static List getTopNRateByWeb(SQLExecutor executor,IIdentityBean idBean,int period,String keytype,int showNum,boolean order){
       TopNHelper helper = new TopNHelper(showNum, "value", order);
		//获取所有网站监察
		CArray<Map> https = CommonUtils.queryWebidAndName();
		for(Entry<Object, Map> en: https.entrySet()){
			Long httptestid = (Long)en.getKey();
			Long hostid = Nest.value(en.getValue(), "hostid").asLong();
			String httpName = Nest.value(en.getValue(), "name").asString();
			String stepName = Nest.value(en.getValue(), "steps","0","name").asString();
			String key=null;
			Double value = 0d;
			if("responsetime".equals(keytype)){//响应时间
				key = "web.test.time["+httpName+","+stepName+",resp]";
			}else if("avbRate".equals(keytype)){//当日可用率
				key = "web.test.fail["+httpName+"]";
			}
			if(key != null) {
				 Map item = reset(DataDriver.getItemId(executor, hostid, key));
				   if(!empty(item)){
					   value = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
				   }
				helper.put(EasyMap.build("hostid", httptestid, "name", httpName, "value", value));
			}
		}
		
		return helper.getResult();
	}
	
	/**  单个设备类型并且只有一个指标的通用方式求一段时间内均值
	 * @param executor
	 * @param idBean
	 * @param groupid
	 * @param period
	 * @param itemkey
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List getTopNRate(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取该设备分组下的所有设备
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName(groupid);
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Double value = 0d;
			if(itemkey != null) {
				 Map item = reset(DataDriver.getItemId(executor, hostid, itemkey));
				   if(!empty(item)){
					   value = Cphp.round(ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);;
				   }
				helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", value));
			}
		}
		
		return helper.getResult();
	}
	/**  获取某个设备组下某段时间内网络IPOS均值
	 * @param executor
	 * @param idBean
	 * @param groupid
	 * @param period
	 * @param key
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List getTOPNRateByUporDownIPOS(SQLExecutor executor,IIdentityBean idBean,Long groupid,int period,String itemkey,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName();
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			Long groupId = Nest.value(en.getValue(),"groupid").asLong();
			String key = null;
			Double value = 0d;
			if(IMonConsts.MON_SERVER_LINUX == groupId){
				if("up".equals(itemkey)){
					key = ItemsKey.NET_INGERFACE_UPIPOS_LINUX.getValue();
				}else{
					key = ItemsKey.NET_INGERFACE_DOWNIPOS_LINUX.getValue();
				}
				
			}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){
				if("up".equals(itemkey)){
					key = ItemsKey.NET_INGERFACE_UPIPOS_WINDOWS.getValue();
				}else{
					key = ItemsKey.NET_INGERFACE_DOWNIPOS_WINDOWS.getValue();
				}
			}
			if(key != null) {
				CArray<Map> items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, key);
				if(!empty(items))
					value = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
				helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(value,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			}
		}
		
		return helper.getResult();
	}
	
	
	/**获取某个设备组下某段时间内发送利用率、接受利用率
	 * @param executor
	 * @param groupid
	 * @param idBean
	 * @param showNum
	 * @param order
	 * @return
	 */
	public static List getTOPNRateByUporDownNet(SQLExecutor executor,IIdentityBean idBean,String statisticalType,int period,String keytype,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		Long[] groupids = null;//
		String[] keys = null;
		String[] totalkeys = null;
		if("switches".equals(statisticalType)){//交换机
			groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id()};
		}
		
		if("up".equals(keytype)){//数据库 当前连接数 
			keys = new String[]{ItemsKey.NET_UP_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFINOCTETS.getValue()};
			totalkeys = new String[]{ItemsKey.NET_BANDWIDTH_NET_CISCO.getValue(),ItemsKey.COMMON_NET_NET_BANDWIDTH.getValue()};
		}else if("down".equals(keytype)){
			keys = new String[]{ItemsKey.NET_DOWN_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFOUTOCTETS.getValue()};
			totalkeys = new String[]{ItemsKey.NET_BANDWIDTH_NET_CISCO.getValue(),ItemsKey.COMMON_NET_NET_BANDWIDTH.getValue()};
		}
		
		for(int i=0;i < groupids.length;i++){
			//获取通用设备思科分组下的所有设备ID
			CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName(groupids[i]);
			for(Entry<Object, Map> host: hosts.entrySet()){
			   Long hostid = (Long)host.getKey();
			   String hostName = Nest.value(host.getValue(), "name").asString();
			   Double used=0d,total=0d,netUsedRate=0d;
//			   Map useitem = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, keys[i]));
//			   if(!empty(useitem)){
//				    used = ReportUtil.getSomeTimeAvgName(executor, useitem, idBean.getTenantId(), period);
//			   }

			   CArray<Map> items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, keys[i]);
			   if(!empty(items))
				   used = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
			   
//			   Map totalitem = reset(DataDriver.getItemId(IRadarContext.getSqlExecutor(), hostid, totalkeys[i]));
//			   if(!empty(useitem)){
//				   total = ReportUtil.getSomeTimeAvgName(executor, totalitem, idBean.getTenantId(), period);
//			   }
			   items = DataDriver.getItemIds(IRadarContext.getSqlExecutor(), hostid, totalkeys[i]);
			   if(!empty(items))
				   total = ReportUtil.getSomeTimeAvgNamePrototypeKey(executor, items, idBean.getTenantId(), period);
			   netUsedRate = total==0d? 0: Cphp.round(100*used/total, 2);
			   helper.put(EasyMap.build("hostid", hostid, "name", hostName, "value", Cphp.round(netUsedRate,2)));
			}
		}
		return helper.getResult();
		
		
	}
	
	/**  求数据类型历史表中item value某段时间内平均值
	 * @param sqlExecutor
	 * @param itemid
	 * @param tenantid
	 * @param period 最近一段时间内
	 * @return
	 */
	public static Double getSomeTimeAvgName(SQLExecutor sqlExecutor,Map item,String tenantid,Integer period){
		int valueType=Nest.value(item, "value_type").asInteger();
		String table = CHistoryManager.getTableName(valueType);
        StringBuffer buf=new StringBuffer();
        buf.append(" select avg(h.value) avgvalue FROM "+table);
        buf.append(" h where h.itemid = #{itemid}  and h.tenantid = #{tenantid} and h.clock> #{period} ");
        if(Feature.defaultTenantId.equals(tenantid))
        	tenantid = Nest.value(item, "tenantid").asString();
		Map sqlParam = EasyMap.build(
				"itemid", Nest.value(item, "itemid").asInteger(),
				"tenantid",tenantid,
				"period", (Cphp.time()- period)
			);
		 Map results = DBfetch(DBselect(sqlExecutor, buf.toString(), sqlParam));
		 if(empty(results)){
			 return 0d;
		 }else{
			 return Nest.value(results, "avgvalue").asDouble();
		 }
	}
	
	
	/**  求数据类型历史表中item value某段时间内最大值
	 * @param sqlExecutor
	 * @param itemid
	 * @param tenantid
	 * @param period 最近一段时间内
	 * @return
	 */
	public static Double getSomeTimeMaxName(SQLExecutor sqlExecutor,Map item,String tenantid,Integer period){
		int valueType=Nest.value(item, "value_type").asInteger();
		String table = CHistoryManager.getTableName(valueType);
        StringBuffer buf=new StringBuffer();
        buf.append(" select max(h.value) as maxva FROM "+table);
        buf.append(" h where h.itemid = #{itemid}  and h.tenantid = #{tenantid} and h.clock> #{period} ");
        if(Feature.defaultTenantId.equals(tenantid))
        	tenantid = Nest.value(item, "tenantid").asString();
		Map sqlParam = EasyMap.build(
				"itemid", Nest.value(item, "itemid").asInteger(),
				"tenantid",tenantid,
				"period", (Cphp.time()- period)
			);
		 Map results = DBfetch(DBselect(sqlExecutor, buf.toString(), sqlParam));
		 if(empty(results)){
			 return 0d;
		 }else{
			 return Nest.value(results, "maxva").asDouble();
		 }
	}
	
	public static Double getSomeTimeAvgNamePrototypeKey(SQLExecutor sqlExecutor,CArray<Map> items,String tenantid,Integer period){
		CArray<Double> values = array();
		for(Map item:items)
			values.add(getSomeTimeAvgName(sqlExecutor,item,tenantid,period));
		return getNum(values);
	}
	
	public static Double getSomeTimeMaxNamePrototypeKey(SQLExecutor sqlExecutor,CArray<Map> items,String tenantid,Integer period){
		CArray<Double> values = array();
		for(Map item:items)
			values.add(getSomeTimeMaxName(sqlExecutor,item,tenantid,period));
		return getNum(values);
	}
	
	public static Double getNum(CArray<Double> values){
		if(Cphp.empty(values))
			return 0d;
		else{
			Double sum = 0d;
			int count = 0;
			for(Double value: values) {
				if(value == null) {
					continue;
				}else {
					sum += value;
					count++;
				}
			}
			
			if(count == 0)
				return 0d;
			else
				return sum/count;
		}
	}
	
	/** 通过使用量和总容量来获取单个设备的使用率
	 * @param sqlExecutor
	 * @param tenantid 租户id
	 * @param table 表名称
	 * @param fsusekey 已使用空间
	 * @param fstotalbyte 总空间大小
	 * @param hostid  设备di或设备组
	 * @param period  时间范围
	 * @param timetype 默认类型
	 * @return
	 */
	private static Double getHostDiskUsage(SQLExecutor sqlExecutor,String tenantid,String table,String fsusekey,String fstotalbyte,
			                   String hostid,Integer period,String timetype){
		StringBuffer buf=new StringBuffer();
		buf.append(" SELECT SUM(t1.sum_)/SUM(t2.sum_) as avgnum FROM ");
		buf.append("(SELECT  ");
		buf.append(" FLOOR(h.clock / (300 * #{timetype})) time_,"); 
		buf.append(" SUM(h.value) sum_  ");
		buf.append(" FROM "+ table +" h ");
		buf.append(" WHERE h.itemid IN (SELECT itemid  FROM items WHERE  hostid IN(("+hostid+")) AND key_ LIKE '"+fsusekey+"')");
		buf.append(" AND h.tenantid > #{tenantid} ");
		buf.append(" AND h.clock > #{period} ");
		buf.append(" GROUP BY  time_ ");
		buf.append(" ) t1,( ");
		buf.append(" SELECT  ");
		buf.append(" FLOOR(h.clock / (300 #{timetype})) time_,"); 
		buf.append(" SUM(h.value) sum_  "); 
		buf.append(" FROM "+ table +" h ");
		buf.append(" WHERE h.itemid IN (SELECT itemid  FROM items WHERE  hostid in("+hostid+") AND key_ LIKE '"+fstotalbyte+"')");
		buf.append(" AND h.tenantid > #{tenantid} ");
		buf.append(" AND h.clock > #{period} ");
		buf.append(" GROUP BY  time_ ");
		buf.append(" ) t2");
		buf.append(" WHERE t1.time_=t2.time_");
		if(!"1".equals(timetype)){
			buf.append(" GROUP BY t1.time_");
		}
		Map sqlParam = EasyMap.build(
				"tenantid",tenantid,
				"timetype", timetype,
				"period", (Cphp.time()- period)
			);
		 Map results = DBfetch(DBselect(sqlExecutor, buf.toString(), sqlParam));
		 if(empty(results)){
			 return 0d;
		 }else{
			 return Nest.value(results, "avgnum").asDouble();
		 }
	}
	
	public  static String create_Favorite_Graphs_STR(IIdentityBean idBean, SQLExecutor executor,String hostid,String key,int time) {
		return create_Favorite_Graphs_STR(idBean, executor, hostid, Arrays.asList(key), time,"");
	}
	
	public  static String create_Favorite_Graphs_STR(IIdentityBean idBean, SQLExecutor executor,String hostid,String key,int time,String name) {
		return create_Favorite_Graphs_STR(idBean, executor, hostid, Arrays.asList(key), time,name);
	}
	/**   获取自生中趋势图的的src
	 * @param idBean
	 * @param executor
	 * @param hostid  设备id
	 * @param key   主键
	 * @param time  时间范围
	 * @return
	 */
	public  static String create_Favorite_Graphs_STR(IIdentityBean idBean, SQLExecutor executor,String hostid, Collection<String> keys,int time) {
		return create_Favorite_Graphs_STR(idBean,executor,hostid,keys,time,"");
	}
	
	
	/**  获取自生中趋势图的的src
	 * @param idBean
	 * @param executor
	 * @param hostid
	 * @param keys
	 * @param time
	 * @param name
	 * @return
	 */
	public  static String create_Favorite_Graphs_STR(IIdentityBean idBean, SQLExecutor executor,String hostid, Collection<String> keys,int time,String name) {
		long hostids = Long.parseLong(hostid);
		String itemid = "";
		CHostGet ch = new CHostGet();
		ch.setOutput(new String[] { "hostid" });
		ch.setSelectItems(new String[] { "itemid", "key_" });
		ch.setHostIds(hostids);
		CArray<Map> item = API.Host(idBean, executor).get(ch);
		CArray<Map> items = Nest.value(item, "0", "items").asCArray();
		for (Entry<Object, Map> e : items.entrySet()) {
			String key = String.valueOf(e.getValue().get("key_")); 
			if (keys.contains(key)) {
				itemid = e.getValue().get("itemid").toString();
			}
		}
		String timeG = Cphp.date(Defines.TIMESTAMP_FORMAT);
		if(itemid == null || itemid.equals("")){
				for(String key:keys){
					if(key!=null){
						Map itemmap = reset(DataDriver.getItemsBykey(executor,Long.parseLong(hostid),key));
						itemid = Nest.value(itemmap, "itemid").asString();
					}
				}
		}
	    if(!"".equals(itemid)){
	    	String src = "chart.action?itemid=" + itemid + "&period=" + time + "&stime=" + timeG + "&name=" + name + "&updateProfile=0&width=750";
			
			return src;
	    }else{
	    	return null;
	    }
		
	}
	
	/** 生成web服务对应响应时间和http状态的趋势图  
	 */
	public  static String create_Http_Favorite_Graphs_STR(int httpItemType,Long httpTestId,int time) {
		String timeG = Cphp.date(Defines.TIMESTAMP_FORMAT);
		return "chart3.action?http_item_type=" + httpItemType + "&httptestid=" + httpTestId + "&graphtype=1&period=" + time + "&stime=" + timeG + "&width=750&updateProfile=0";
	}
	
	/**   获取原型中趋势图的的src
	 * @param idBean
	 * @param executor
	 * @param hostid 设备id
	 * @param key  主键
	 * @param time  时间范围
	 * @return
	 */
	public static String create_Favorite_Graphs_Prototype_STR(IIdentityBean idBean, SQLExecutor executor,String hostid, ItemsKey key,int time) {
		//String timeG = Cphp.date(Defines.TIMESTAMP_FORMAT);
		//String src = "chartprototype.action?hostid=" + hostid + "&key="+key.name()+"&period=" + time + "&stime=" + timeG + "&name=testname1&updateProfile=0&width=650";
		return create_Favorite_Graphs_Prototype_STR(idBean,executor,hostid,key,time,"");
	}
	
	public static String create_Favorite_Graphs_Prototype_STR(IIdentityBean idBean, SQLExecutor executor,String hostid, ItemsKey key,int time,String name) {
		String timeG = Cphp.date(Defines.TIMESTAMP_FORMAT);
		String src = "chartprototype.action?hostid=" + hostid + "&key="+key.name()+"&period=" + time + "&stime=" + timeG + "&name="+name+"&updateProfile=0&width=750";
		return src;
	}
	
	private final static CArray<String> PERIOD_DATE_MAP = CArray.map("86400", "Day", "604800", "Week", "2592000", "Month");
	public static List getEventNum(IIdentityBean idBean, SQLExecutor executor,String statisticalType,int period){
		long starttime=0l;
		List list=new ArrayList();
		Map eventmap = new HashMap();
		String longstr=null;
		int[] eventprioritys = {3,4,5};
		
		if(period == 3600){
			starttime = (Cphp.time()- period);
		}else{
			String periodstr = PERIOD_DATE_MAP.get(period);
			starttime = EventsReportForms.getCalendar(periodstr);
		}
		Map sqlParam = EasyMap.build(
				"tenantid",idBean.getTenantId(),
				"starttime", starttime
			);
		if("database".equals(statisticalType)){
			longstr = IMonGroup.MON_DB_MYSQL.id()+","+
		              IMonGroup.MON_DB_ORACLE.id()+","+
					  IMonGroup.MON_DB_SQLSERVER.id()+","+
		              IMonGroup.MON_DB_DB2.id()+","+
					  IMonGroup.MON_DB_MONGODB.id();
		}else if("tomcatmiddle".equals(statisticalType)){
			longstr = IMonGroup.MON_MIDDLE_TOMCAT.id()+","+
		              IMonGroup.MON_MIDDLE_WEBSPHERE.id()+","+
		              IMonGroup.MON_MIDDLE_WEBLOGIC.id()+","+ 
		              IMonGroup.MON_MIDDLE_IIS.id();
		}
		String groupsql="",websql="";
		if(longstr != null){
			groupsql = " AND hg.groupid IN ("+longstr+")";
		}
		if(Nest.as(Defines.HTTPSTEP_ITEM_TYPE_RSPCODE).asString().equals(statisticalType))
			//websql = " AND i.key_ LIKE 'web.test.rspcode[%' ";
			websql = " AND i.key_ LIKE 'web.test.%' ";
		else if(Nest.as(Defines.HTTPSTEP_ITEM_TYPE_LASTERROR).asString().equals(statisticalType))
			websql =  " AND i.key_ LIKE 'web.test.error[%' ";
		
		String sql = ""+
				" SELECT                                                        "+
				"    count(0) as eventsnum                                      "+
				" FROM  events e                                                "+
				" WHERE e.source = 0                                            "+
				"    AND e.object = 0                                           "+
				"    AND e.clock >= #{starttime}                                "+
				"    AND e.tenantid = #{tenantid}                               "+           
				"    AND EXISTS (select 1 from                                  "+       
				"     triggers t                                                "+
				"     inner join functions f   on f.triggerid = t.triggerid     "+            
				"     inner join items i       on i.itemid = f.itemid           "+ websql +
				"     right join hosts_groups hg  ON hg.hostid = i.hostid       "+
				"           where   t.triggerid = e.objectid                    "+
				"           and t.priority =  #{priority}                       "+ groupsql                                                        +
				"     )                                                         ";
		Map<Integer, String> priorityMap = EventsReportForms.returnPriority(executor,idBean);
		
		for(Entry<Integer, String> entry: priorityMap.entrySet()){
			int level = entry.getKey();
			String levelName = entry.getValue();
			sqlParam.put("priority", level);
			Map event = reset(DBselect(executor, sql,sqlParam));
			int eventnum =Nest.value(event, "eventsnum").asInteger();
			if(eventnum > 0){
				list.add(EasyMap.build("priority", levelName, "eventsnum", eventnum));
			}
		}
		return list;
	}
	
	
	public static List getEventNumByWeb(IIdentityBean idBean, SQLExecutor executor,int period){
		List<Map> eventlist=new ArrayList<Map>();
		Map event=new HashMap();
		
		CHttpTestGet options = new CHttpTestGet();
		options.setOutput(new String[] { "httptestid","name" });
		options.setEditable(true);
		CArray<Map> httpTests = API.HttpTest(idBean, executor).get(options);
		int total=httpTests.size();
		int errorNum=0;
		for(Map http:httpTests){
			String httpname =Nest.value(http, "name").asString();
			String key1="web.test.time["+httpname+","+httpname+",resp]";
			String key2="web.test.fail["+httpname+"]";
			String sql ="SELECT count(0) as num,t.triggerid from items i                        "+
			" right join functions f                      "+
			"  on i.itemid =  f.itemid                    "+
			" right join triggers t                       "+
			"   on f.triggerid = t.triggerid              "+
			"where                                        "+
			"  i.tenantid=#{tenantid}                     "+
			" and i.key_ in(#{key1},#{key2})              ";
			CArray<Map> appMaps = DBselect(executor, sql, map("key1",key1,"key2",key2,"tenantid",idBean.getTenantId()));
			
			String sql2 =" select DISTINCT(e.objectid) from events e   "+
			"  where   e.object = 0                       "+
			"        and e.value = 0                      "+
			"        and e.objectid =  #{triggerid}       "+
			"        and e.clock >= #{starttime}          ";
			for(Map appMap:appMaps){
				Long triggerid = Nest.value(appMap, "triggerid").asLong();
				CArray<Map> triggerMap = DBselect(executor, sql2, map("triggerid",triggerid,"starttime",Cphp.time()-period));
				if(triggerMap.size()>0){
					errorNum++;	
					break;
				}
			}
		}
		int normal = total-errorNum;
		if(normal != 0){
			event.put("priority", "正常");
			event.put("eventsnum", total-errorNum);
			eventlist.add(event);
		}
		if(errorNum != 0){
			Map event1=new HashMap();
			event1.put("priority", "异常");
			event1.put("eventsnum", errorNum);
			eventlist.add(event1);
		}
		return eventlist;
	}
	
	private static String getfaultZHName(String priority){
		if("4".equals(priority)){
			return "很严重";
		}else if("5".equals(priority)){
			return "灾难";
		}else{
			return "较严重";
		}
	} 
	
	private final static CArray<String> PERIOD_MAP = CArray.map("0", "Day", "1", "Week", "2", "Month");
	public static List getTrend(IIdentityBean idBean, SQLExecutor executor,int _,String hostid, String period){
		period = PERIOD_MAP.get(period);
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		Long timeBucket  = 0l;	//时间段(比如说每周1天为1段 timeBucket=60*60*24)
		if("Day".equals(period)){
			timeBucket = 2*60*60l;
			sdf = new SimpleDateFormat("d日H时");
		}else if("Week".equals(period)){
			timeBucket = 24*60*60l;
			sdf = new SimpleDateFormat("MM-dd");
		}else if("Month".equals(period)){
			timeBucket = 7*24*60*60l;
			sdf = new SimpleDateFormat("M月第W周");
		}else if("Quarter".equals(period)){
			timeBucket = 24*60*60l;
			sdf = new SimpleDateFormat("yyyy-MM");
		}
		
		long startTime = EventsReportForms.getCalendar(period);
		
		Map sqlParam = EasyMap.build(
				"tenantid",idBean.getTenantId(),
				"starttime", startTime,
				"timeBucket", timeBucket,
				"hostid",hostid
			);
	    String sql=	"	SELECT   "+
				"  floor((e.clock+28800)/#{timeBucket})   as moment, count(0) as eventsnum  "+
				" FROM                                            "+
				" events e                                        "+
				" WHERE e.source = 0 and e.object = 0             "+
				"   AND e.clock >= #{starttime}                   "+
				"   AND e.tenantid = #{tenantid}                  "+
				" AND e.objectid in(                              "+
				"    select                                       "+
				"    t.triggerid                                  "+
				"    from                                         "+
				"    triggers t                                   "+
				"    inner join functions f                       "+
				"    on f.triggerid = t.triggerid                 "+
				"    inner join items i                           "+
				"    on i.itemid = f.itemid                       "+
				"   where i.hostid = #{hostid}                    "+
				"   and  t.priority IN(3,4,5)                     "+
				"    ) group by moment  ";
		CArray<Map> events = DBselect(executor, sql, sqlParam);
		
		List reuslt = EasyList.build();
		
		Long _value = 0l;
		Long _time = 0l;
		long prev = (startTime + 28800) / timeBucket - 1;

		Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.add(Calendar.DAY_OF_MONTH, 1);
		Long endTime = (currentDate.getTimeInMillis() / 1000 + 28800) / timeBucket;
		if ("Week".equals(period)) {
			endTime--;
		}

		Date date = new Date();
		events.push(CArray.map(// 将最后一个时间加到最后，进行补齐
				"moment", endTime, "eventsnum", 0));
		for (Map hostdata : events) {
			_time = Nest.value(hostdata, "moment").asLong();
			_value = Nest.value(hostdata, "eventsnum").asLong();

			long distance = _time - prev; // 补齐
			for (long i = 1; i <= distance; i++) {
				long vTime, vValue;
				if (i == distance) {
					vTime = timeBucket * _time;
					vValue = _value;
				} else {
					vTime = timeBucket * (prev + i);
					vValue = 0;
				}
				date.setTime((vTime - 28800) * 1000);
//				reuslt.put(sdf.format(date), vValue);
				reuslt.add(CArray.map("moment", sdf.format(date), "eventsnum", vValue));
			}
			prev = _time;
		}

		return reuslt;
	}
	
	public static List TopNByApp(SQLExecutor executor,IIdentityBean idBean,String apptype,String keytype,int period,int showNum,boolean order){
		TopNHelper helper = new TopNHelper(showNum, "value", order);
		List<Map> applist = gethostByApps(executor,idBean,apptype,false);
		for(Map app : applist){
			CItemGet itemGet = new CItemGet();
			itemGet.setApplicationIds(Nest.value(app, "hostid").asLong());
			itemGet.setOutput(new String[] {"itemid","value_type","units","valuemapid","name","key_"});
			itemGet.setEditable(true);
			CArray<Map> itemkeys = API.Item(idBean, executor).get(itemGet);
			for(Map item:itemkeys){
				String key=Nest.value(item, "key_").asString();
				 if(keytype.equals(key.substring(0, key.indexOf("[")))){
					 Double value = ReportUtil.getSomeTimeAvgName(executor, item, idBean.getTenantId(), period);
				     helper.put(EasyMap.build("hostid", Nest.value(app, "hostid").asLong(), "name", Nest.value(app, "name").asString(), "value", value));
					 break;
				  }
			}
		}
		return helper.getResult();
	}
	
	public static List<Map> gethostByApps(SQLExecutor executor,IIdentityBean idBean,String apptype,boolean isHost){
		List<Map> hostdata=new ArrayList<Map>();
		String sql = " select  a.hostid,a.applicationid as applicationid,a.name from applications a where a.type =#{type} and a.tenantid =#{tenantid}";
		CArray<Map> appMap = DBselect(executor, sql, map("type",apptype,"tenantid",idBean.getTenantId()));
		for (Map app: appMap) {
		    Map appdt =new HashMap();
		    appdt.put("name", Nest.value(app, "name").asString());
		    if(isHost){
		    	 appdt.put("hostid", Nest.value(app, "applicationid").asString()+","+Nest.value(app, "hostid").asString());
		    }else{
		    	 appdt.put("hostid", Nest.value(app, "applicationid").asString());
		    }
		   
		    hostdata.add(appdt);
	   }
		return hostdata;
	}
	
	public static List<Map> gethostByApps(SQLExecutor executor,IIdentityBean idBean,String apptype){
		return gethostByApps(executor,idBean,apptype,true);
	}
	
	
	public static List getEventNumByApp(IIdentityBean idBean, SQLExecutor executor,String apptype,int period){
		List<Map> eventlist=new ArrayList<Map>();
		Map event=new HashMap();
		List<Map> applist = gethostByApps(executor,idBean,apptype,false);
		if(!empty(applist)){
			Long[] appl=new Long[applist.size()];
			int i=0;
			for(Map app:applist){
				appl[i]=Nest.value(app,"hostid").asLong();
				i++;
			}
			int total=applist.size();
			int errorNum = TvmUtil.getHostidForErrorApp(idBean,executor,CArray.valueOf(appl),period);
			int normal = total-errorNum;
			if(normal != 0){
				event.put("priority", "正常");
				event.put("eventsnum", normal);
				eventlist.add(event);
			}
			if(errorNum != 0){
				Map event1=new HashMap();
				event1.put("priority", "异常");
				event1.put("eventsnum", errorNum);
				eventlist.add(event1);
			}
		}
		return eventlist;
	}

	
	public static void get_timeperiod_form(CFormList tblPeriod) {

		//CTable tblPeriod = new CTable(null, "formElementTable");

		// init new_timeperiod variable
		Map new_timeperiod = get_request("new_timeperiod", array());
		boolean isnew = !empty(new_timeperiod);

		if (isnew) {
			if (isset(new_timeperiod,"id")) {
				tblPeriod.addItem(new CVar("new_timeperiod[id]", Nest.value(new_timeperiod,"id").$()));
			}
			if (isset(new_timeperiod,"timeperiodid")) {
				tblPeriod.addItem(new CVar("new_timeperiod[timeperiodid]", Nest.value(new_timeperiod,"timeperiodid").$()));
			}
		}
		if (!isnew) {
			new_timeperiod = array();
			Nest.value(new_timeperiod,"timeperiod_type").$(TIMEPERIOD_TYPE_DAILY);
		}
		if (!isset(new_timeperiod,"every")) {
			Nest.value(new_timeperiod,"every").$(1);
		}
		if (!isset(new_timeperiod,"day")) {
			Nest.value(new_timeperiod,"day").$(1);
		}
		if (!isset(new_timeperiod,"hour")) {
			Nest.value(new_timeperiod,"hour").$(12);
		}
		if (!isset(new_timeperiod,"minute")) {
			Nest.value(new_timeperiod,"minute").$(0);
		}
		if (!isset(new_timeperiod,"start_date")) {
			Nest.value(new_timeperiod,"start_date").$(0);
		}
		if (!isset(new_timeperiod,"period_days")) {
			Nest.value(new_timeperiod,"period_days").$(0);
		}
		if (!isset(new_timeperiod,"period_hours")) {
			Nest.value(new_timeperiod,"period_hours").$(1);
		}
		if (!isset(new_timeperiod,"period_minutes")) {
			Nest.value(new_timeperiod,"period_minutes").$(0);
		}
		if (!isset(new_timeperiod,"month_date_type")) {
			Nest.value(new_timeperiod,"month_date_type").$(empty(Nest.value(new_timeperiod,"day").$()));
		}

		// start time
		if (isset(new_timeperiod,"start_time")) {
			Nest.value(new_timeperiod,"hour").$(floor(Nest.value(new_timeperiod,"start_time").asLong() / SEC_PER_HOUR));
			Nest.value(new_timeperiod,"minute").$(floor((Nest.value(new_timeperiod,"start_time").asLong() - (Nest.value(new_timeperiod,"hour").asLong() * SEC_PER_HOUR)) / SEC_PER_MIN));
		}

		// period
		if (isset(new_timeperiod,"period")) {
			Nest.value(new_timeperiod,"period_days").$(floor(Nest.value(new_timeperiod,"period").asLong() / SEC_PER_DAY));
			Nest.value(new_timeperiod,"period_hours").$(floor((Nest.value(new_timeperiod,"period").asLong() - (Nest.value(new_timeperiod,"period_days").asLong() * SEC_PER_DAY)) / SEC_PER_HOUR));
			Nest.value(new_timeperiod,"period_minutes").$(floor((Nest.value(new_timeperiod,"period").asLong() - Nest.value(new_timeperiod,"period_days").asLong() * SEC_PER_DAY - Nest.value(new_timeperiod,"period_hours").asLong() * SEC_PER_HOUR) / SEC_PER_MIN));
		}

		// daysofweek
		StringBuilder dayofweeksb = new StringBuilder();
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_mo") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_tu") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_we") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_th") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_fr") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_sa") ? "0" : "1");
		dayofweeksb.append(!isset(new_timeperiod,"dayofweek_su") ? "0" : "1");
		String dayofweek = null;
		if (isset(new_timeperiod,"dayofweek")) {
			dayofweek = rda_num2bitstr(Nest.value(new_timeperiod,"dayofweek").asLong(), true);
		} else {
			dayofweek = dayofweeksb.toString();
		}

		Nest.value(new_timeperiod,"dayofweek_mo").$(dayofweek.charAt(0));
		Nest.value(new_timeperiod,"dayofweek_tu").$(dayofweek.charAt(1));
		Nest.value(new_timeperiod,"dayofweek_we").$(dayofweek.charAt(2));
		Nest.value(new_timeperiod,"dayofweek_th").$(dayofweek.charAt(3));
		Nest.value(new_timeperiod,"dayofweek_fr").$(dayofweek.charAt(4));
		Nest.value(new_timeperiod,"dayofweek_sa").$(dayofweek.charAt(5));
		Nest.value(new_timeperiod,"dayofweek_su").$(dayofweek.charAt(6));

		// months
		StringBuilder monthsb = new StringBuilder();
		monthsb.append(!isset(new_timeperiod,"month_jan") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_feb") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_mar") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_apr") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_may") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_jun") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_jul") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_aug") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_sep") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_oct") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_nov") ? "0" : "1");
		monthsb.append(!isset(new_timeperiod,"month_dec") ? "0" : "1");
		String month = null;
		if (isset(new_timeperiod,"month")) {
			month = rda_num2bitstr(Nest.value(new_timeperiod,"month").asLong(), true);
		} else {
			month = monthsb.toString();
		}

		Nest.value(new_timeperiod,"month_jan").$(month.charAt(0));
		Nest.value(new_timeperiod,"month_feb").$(month.charAt(1));
		Nest.value(new_timeperiod,"month_mar").$(month.charAt(2));
		Nest.value(new_timeperiod,"month_apr").$(month.charAt(3));
		Nest.value(new_timeperiod,"month_may").$(month.charAt(4));
		Nest.value(new_timeperiod,"month_jun").$(month.charAt(5));
		Nest.value(new_timeperiod,"month_jul").$(month.charAt(6));
		Nest.value(new_timeperiod,"month_aug").$(month.charAt(7));
		Nest.value(new_timeperiod,"month_sep").$(month.charAt(8));
		Nest.value(new_timeperiod,"month_oct").$(month.charAt(9));
		Nest.value(new_timeperiod,"month_nov").$(month.charAt(10));
		Nest.value(new_timeperiod,"month_dec").$(month.charAt(11));

		String bit_dayofweek = rda_str_revert(dayofweek);
		String bit_month = rda_str_revert(month);

		CComboBox cmbType = new CComboBox("new_timeperiod[timeperiod_type]", Nest.value(new_timeperiod,"timeperiod_type").$(), "submit()");
		//cmbType.addItem(TIMEPERIOD_TYPE_ONETIME, _("One time only"));
		cmbType.addItem(TIMEPERIOD_TYPE_DAILY, _("Daily"));
		cmbType.addItem(TIMEPERIOD_TYPE_WEEKLY, _("Weekly"));
		cmbType.addItem(TIMEPERIOD_TYPE_MONTHLY, _("Monthly"));

		tblPeriod.addRow(_("Period type"), cmbType);

		if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY) {
			tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month)));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			//tblPeriod.addRow(_("Every day(s)"), new CNumericBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString(), 3));
			tblPeriod.addItem(new CVar("new_timeperiod[every]", Nest.value(new_timeperiod,"every").$()));
		} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month)));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			//tblPeriod.addRow(_("Every week(s)"), new CNumericBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString(), 2));
			tblPeriod.addItem(new CVar("new_timeperiod[every]", Nest.value(new_timeperiod,"every").$()));
			
			CTable tabDays = new CTable();
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_mo]", Nest.as(dayofweek.charAt(0)).asBoolean(), null, 1), _("Monday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_tu]", Nest.as(dayofweek.charAt(1)).asBoolean(), null, 1), _("Tuesday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_we]", Nest.as(dayofweek.charAt(2)).asBoolean(), null, 1), _("Wednesday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_th]", Nest.as(dayofweek.charAt(3)).asBoolean(), null, 1), _("Thursday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_fr]", Nest.as(dayofweek.charAt(4)).asBoolean(), null, 1), _("Friday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_sa]", Nest.as(dayofweek.charAt(5)).asBoolean(), null, 1), _("Saturday")));
			tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_su]", Nest.as(dayofweek.charAt(6)).asBoolean(), null, 1), _("Sunday")));
			tblPeriod.addRow(_("Day of week"), tabDays);
		} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));

			CTable tabMonths = new CTable();
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_jan]", true, null, 1), _("January"),//Nest.as(month.charAt(0)).asBoolean()
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_jul]", true, null, 1), _("July")//Nest.as(month.charAt(6)).asBoolean()
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_feb]", true, null, 1), _("February"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_aug]", true, null, 1), _("August")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_mar]", true, null, 1), _("March"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_sep]", true, null, 1), _("September")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_apr]", true, null, 1), _("April"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_oct]", true, null, 1), _("October")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_may]", true, null, 1), _("May"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_nov]", true, null, 1), _("November")
			));
			tabMonths.addRow(array(
				new CCheckBox("new_timeperiod[month_jun]", true, null, 1), _("June"),
				SPACE, SPACE,
				new CCheckBox("new_timeperiod[month_dec]", true, null, 1), _("December")
			));
			tblPeriod.addRow(_("Month"), tabMonths,true);
			//tblPeriod.addItem(new CVar("new_timeperiod[every]", Nest.value(new_timeperiod,"every").$()));

			 Nest.value(new_timeperiod, "month_date_type").$(Nest.value(new_timeperiod, "month_date_type").asInteger());//始终为默认值 
			
			tblPeriod.addRow(_("Date"), array(
				new CRadioButton("new_timeperiod[month_date_type]", "0", null, null, !Nest.value(new_timeperiod,"month_date_type").asBoolean(), "submit()"),
				_("Day"),
				SPACE,
				new CRadioButton("new_timeperiod[month_date_type]", "1", null, null, Nest.value(new_timeperiod,"month_date_type").asBoolean(), "submit()"),
				_("Day of week"))
			,true);//隐藏日期类型行，（1）避免每月具体时间引起的bug （2）与维护计划保持一致

			if (Nest.value(new_timeperiod,"month_date_type").asInteger() > 0) {
				tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$()));

				CComboBox cmbCount = new CComboBox("new_timeperiod[every]", Nest.value(new_timeperiod,"every").asString());
				cmbCount.addItem(1, _("First"));
				cmbCount.addItem(2, _("Second"));
				cmbCount.addItem(3, _("Third"));
				cmbCount.addItem(4, _("Fourth"));
				cmbCount.addItem(5, _("Last"));

				CCol td = new CCol(cmbCount);
				td.setColSpan(2);

				CTable tabDays = new CTable();
				tabDays.addRow(td);
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_mo]", Nest.as(dayofweek.charAt(0)).asBoolean(), null, 1), _("Monday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_tu]", Nest.as(dayofweek.charAt(1)).asBoolean(), null, 1), _("Tuesday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_we]", Nest.as(dayofweek.charAt(2)).asBoolean(), null, 1), _("Wednesday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_th]", Nest.as(dayofweek.charAt(3)).asBoolean(), null, 1), _("Thursday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_fr]", Nest.as(dayofweek.charAt(4)).asBoolean(), null, 1), _("Friday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_sa]", Nest.as(dayofweek.charAt(5)).asBoolean(), null, 1), _("Saturday")));
				tabDays.addRow(array(new CCheckBox("new_timeperiod[dayofweek_su]", Nest.as(dayofweek.charAt(6)).asBoolean(), null, 1), _("Sunday")));
				tblPeriod.addRow(_("Day of week"), tabDays);
			} else {
				tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));
				tblPeriod.addRow(_("Day of month"), new CNumericBox("new_timeperiod[day]", Nest.value(new_timeperiod,"day").asString(), 2));
			}
		} else {
			tblPeriod.addItem(new CVar("new_timeperiod[every]", Nest.value(new_timeperiod,"every").$(), "new_timeperiod_every_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[month]", bindec(bit_month), "new_timeperiod_month_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[day]", Nest.value(new_timeperiod,"day").$(), "new_timeperiod_day_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[hour]", Nest.value(new_timeperiod,"hour").$(), "new_timeperiod_hour_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[minute]", Nest.value(new_timeperiod,"minute").$(), "new_timeperiod_minute_tmp"));
			tblPeriod.addItem(new CVar("new_timeperiod[start_date]", Nest.value(new_timeperiod,"start_date").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[month_date_type]", Nest.value(new_timeperiod,"month_date_type").$()));
			tblPeriod.addItem(new CVar("new_timeperiod[dayofweek]", bindec(bit_dayofweek)));

			CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
			Object date = null;
			if (isset(Nest.value(_REQUEST,"add_timeperiod").$())) {
				date = map(
					"y" , get_request("new_timeperiod_start_date_year"),
					"m" , get_request("new_timeperiod_start_date_month"),
					"d" , get_request("new_timeperiod_start_date_day"),
					"h" , get_request("new_timeperiod_start_date_hour"),
					"i" , get_request("new_timeperiod_start_date_minute")
				);
				if(empty(Nest.as(date).asLong())){//输入超过常规日期的日期，默认设置为当前时间
					date =rdaDateToTime(date(TIMESTAMP_FORMAT_ZERO_TIME, time()));
				}
			} else {
				date = rdaDateToTime(!empty(Nest.value(new_timeperiod,"start_date").$())
					? Nest.value(new_timeperiod,"start_date").asString() : date(TIMESTAMP_FORMAT_ZERO_TIME, time()));
			}

			tblPeriod.addRow(_("Date"), createDateSelector("new_timeperiod_start_date", Nest.as(date).asLong()));
		}

		if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() != TIMEPERIOD_TYPE_ONETIME) {
			tblPeriod.addRow(_("At (hour:minute)"), array(
				new CNumericBox("new_timeperiod[hour]", Nest.value(new_timeperiod,"hour").asString(), 2),
				":",
				new CNumericBox("new_timeperiod[minute]", Nest.value(new_timeperiod,"minute").asString(), 2))
			);
		}
	
	}
	
	public  long  get_nexttime_form(Map inspectionReport) throws ParseException{
		int status = Nest.value(inspectionReport,"performstatus").asInteger();
		long active_since = Nest.value(inspectionReport,"time").asLong();
		long active_till_1 = Nest.value(inspectionReport,"active_till").asLong();//加1 处理运行时间恰为结束时间
		//假设最小时间为结束时间
		long minTime = active_till_1;
		
		//未开始与运行中		
		int periodType = Nest.value(inspectionReport, "timeperiod_type").asInteger();
		long start_time = Nest.value(inspectionReport, "start_time").asLong();
		long start_date = Nest.value(inspectionReport, "start_date").asLong();
		int every = Nest.value(inspectionReport, "every").asInteger();
		int day = Nest.value(inspectionReport, "day").asInteger();
		
		//起始时间年月日所对应的秒数
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(active_since*1000L);
		long ymdSeconds=new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime())).getTime()/1000L;
		MaintenanceAction manc=new MaintenanceAction();
		
		if(periodType == TIMEPERIOD_TYPE_ONETIME){ //一次性     
			if(status == MAINTENANCE_STATUS_APPROACH){//未开始的
				if(start_date>active_since && start_date<active_till_1){ 
					if(start_date < minTime){
						minTime = start_date;
					}
				}						
			}else{//运行中的
				if(start_date>time() && start_date<active_till_1){ 
					if(start_date < minTime){
						minTime = start_date;
					}						
				}						
			}
			
		}else if(periodType == TIMEPERIOD_TYPE_DAILY){ //每日						
			long seconds = ymdSeconds + start_time;//起始当天的运行时间秒数
			minTime = manc.dayMinTime(status, seconds, every, active_since, active_till_1, start_time, minTime);
			
		}else if(periodType == TIMEPERIOD_TYPE_WEEKLY){ //每周
			//拆分周运行规则
			String binaryWeek = Integer.toBinaryString(Nest.value(inspectionReport, "dayofweek").asInteger());
			//存开始运行时间所在周的秒数
			long[] timeSenconds = manc.weekSenconds(binaryWeek, ymdSeconds, start_time);
			//时间从小到大排序   最小时间为第一个匹配
			Arrays.sort(timeSenconds);
			
			minTime = manc.weekMinTime(status, timeSenconds, every, active_since, active_till_1, start_time, minTime);
			
		}else if(periodType == TIMEPERIOD_TYPE_MONTHLY){
			//拆分月时间规则
			String binaryMonth = Integer.toBinaryString(Nest.value(inspectionReport, "month").asInteger());
			String binaryWeek = Integer.toBinaryString(Nest.value(inspectionReport, "dayofweek").asInteger());
			
			minTime = manc.monthMinTime(status, binaryMonth, ymdSeconds, every, binaryWeek, day, start_time,
					active_since, active_till_1, minTime);
		}				
	
		return minTime;
	}

	
	public static String getNormalZH(int isproblem){
		if(IMonConsts.INSPECTION_ITEM_HISTORY_ABNORMAL == isproblem){
			return _("Abnormal");
		}else if(IMonConsts.INSPECTION_ITEM_HISTORY_OTHER == isproblem){
			return _("NoStatistical");
		}else{
			return _("Normal");
		}
	}
	
	/**   报表中周期时间，单位s
	 * @param period
	 * @return
	 */
	public static int getReportTime(String period){
		int time = 3600;
		if("1".equals(period)){
			time=86400;
		}else if("2".equals(period)){
			time= 604800;
		}else if("3".equals(period)){
			time= 2592000;
		}
		return time;
	}
	
	/** 去除字符串中的{}
	 * @return
	 */
	public static String getCurlybraces(String str){
		 String newstr = null;
		 if(str != null && str.contains("{")){
			 String strs = str.replace("{", "");
			        newstr = strs.replace("}", "");
			   return newstr;
		 }else{
			 return str;
		 }
	}
	/*public static void main(String agrs[]){
		 Map data=new HashMap();
		 data.put(1l,"name1" );
		 
		 Long[] groupids = null;
	     String[] keys = null;
		 groupids = new Long[]{IMonGroup.MON_DB_MYSQL.id(),102l};
		 keys = new String[]{ItemsKey.CONNECTIONS_ABORTED_MYSQL.getValue(),"ccc"};
		for(int i=0;i < groupids.length;i++){
			getTop(groupids[i],keys[i],data);
		}
		System.out.println(data.size());
		System.out.println(10/3);
	}
	
	private static void getTop(Long groupid,String keys,Map data){
		data.put(groupid, keys);
	}*/
	

}
