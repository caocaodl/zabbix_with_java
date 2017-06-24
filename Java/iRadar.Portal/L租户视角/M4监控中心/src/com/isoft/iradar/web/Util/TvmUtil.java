package com.isoft.iradar.web.Util;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.common.util.IMonConsts.ONITORING_FREQUENCY_15;
import static com.isoft.iradar.common.util.IMonConsts.ONITORING_FREQUENCY_30;
import static com.isoft.iradar.common.util.IMonConsts.ONITORING_FREQUENCY_45;
import static com.isoft.iradar.common.util.IMonConsts.ONITORING_FREQUENCY_60;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.cinder.v2.model.BackStorInfo;
import com.isoft.iaas.openstack.cinder.v2.model.Volume;
import com.isoft.iaas.openstack.cinder.v2.model.Volumes;
import com.isoft.iaas.openstack.nova.model.Flavor;
import com.isoft.iaas.openstack.nova.model.Flavors;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Servers;
import com.isoft.iaas.openstack.quantum.model.Port;
import com.isoft.iaas.openstack.quantum.model.Ports;
import com.isoft.iaas.openstack.quantum.model.Quota;
import com.isoft.iaas.openstack.quantum.model.SecurityGroup;
import com.isoft.iaas.openstack.quantum.model.SecurityGroups;
import com.isoft.iradar.Cgd;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.common.util.LatestValueHelper.NormalValue;
import com.isoft.iradar.common.util.TopNHelper;
import com.isoft.iradar.core.g;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.HtmlUtil;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.iradar.model.CMessage;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TvmUtil {
	private static final Logger LOG = LoggerFactory.getLogger(TvmUtil.class);
    private final static String SQL_UPDATE_ITEM =
         "INSERT INTO items ( "
        + 	" tenantid ,itemid ,type,snmp_community ,snmp_oid"//1
        + 	",hostid,name,key_,delay,history"//2
        +   ",trends,status,value_type,trapper_hosts,units"//3
    	+	",multiplier,delta,snmpv3_securityname,snmpv3_securitylevel,snmpv3_authpassphrase "//4
    	+	",snmpv3_privpassphrase,formula,error,lastlogsize,logtimefmt "//5
    	+	",templateid ,valuemapid ,delay_flex ,params,ipmi_sensor"//6
    	+   ",data_type,authtype,username ,password ,publickey "//7
    	+   ",privatekey ,mtime,flags,filter,interfaceid"//8
    	+   ",port ,description,inventory_link ,lifetime,snmpv3_authprotocol"//9
    	+   ",snmpv3_privprotocol ,state,snmpv3_contextname"//10
        +   " ) VALUES ("
        +   " #{tenantid},#{itemid}, #{type}, '', ''"//1
        +   ",#{hostid}, #{itemname}, #{key}, #{delay}, 30"//2
		+   ",180, #{status}, #{value_type}, '', #{units} "//3
		+   ",#{multiplier}, #{delta}, '', 2, '' "//4
		+   ",'', #{formula}, '', 0, '' "//5
		+   ",NULL, NULL, '', '', '' "//6
		+   ",#{data_type}, 0, '', '', ''"//7
		+   ",'', 0, 0, '', #{interfaceid} "//8
		+   ",'', #{description}, 0, '30', 0 "//9
		+   ",0, "+Defines.ITEM_STATUS_ACTIVE+", '')";//10
    
    /**创建服务应用监控指标
     * @param sqlE
     * @param datas
     */
    public void createItem(SQLExecutor sqlE,final Collection<Map> datas) {
		for(Map data: datas) {
			DBUtil.DBstart(sqlE);
			boolean success = true;
			try {
				CArray defConfig = CArray.map(
					"value_type", 	Defines.ITEM_VALUE_TYPE_UINT64 //数字类型
					,"data_type", 	Defines.ITEM_DATA_TYPE_DECIMAL //信息类型
					,"multiplier", 	0//是否  使用自定义倍数
					//,"delta", 	    0
					,"formula", 	1//自定义倍数的值
					,"interfaceid", null
					,"units", 		""
				);
				data = Cphp.array_merge(defConfig, data);
				success=DBUtil.DBexecute(sqlE, SQL_UPDATE_ITEM, data);
			} catch (Exception e) {
				if(LOG.isErrorEnabled()) {
					LOG.error("create item fail", e);
				}
				success = false;
			}
			DBUtil.DBend(sqlE, success);
		}
    }
    
    
    /** 获取云主机数据
     * @param idbean
     * @param executor
     * @return
     */
    public static CArray<Map> getVMhost(IIdentityBean idBean,SQLExecutor executor){
    	CArray<Map> hosts=array();
		CHostGet option = new CHostGet();
		option.setGroupIds(IMonConsts.MON_VM);
		option.setOutput(new String[]{"hostid", "name"});
		option.setEditable(true);
		option.setOutput(new String[] { "name", "hostid", "status","host" });
		option.setSelectInterfaces(new String[] { "ip" });
		option.setPreserveKeys(true);		//以主键作为Map中的key
		hosts = API.Host(idBean, executor).get(option);
    	return hosts;
    }
    
    /** 获取表达式英文部分
     * @param description
     * @return
     */
    public static String getENDescription(String description){
    	if(description!=null){
    		if(description.contains("=")){
    			String[] des=description.split("=");
                if(des.length==2){
                	return des[0];
                }else{
                	return description;
                }
    		}else{
    			return description;
    		}
    	}
    	return null;
    }
    
    /** 获取表达式中文部分
     * @param description
     * @return
     */
    public static String getZHDescription(String description){
    	if(description!=null){
    		if(description.contains("=")){
    			String[] des=description.split("=");
                if(des.length==2){
                	return des[1];
                }else{
                	return description;
                }
    		}else{
    			return description;
    		}
    		
    	}
    	return null;
    }
    
    /**获取云主机关联ip
     * @param host
     * @return
     */
    public static String getVmHostIp(Map host){
    	String ip=null;
    	CArray<Map> ipmaps=Nest.value(host, "interfaces").asCArray();
		for(Map ipmap:ipmaps){
			ip=Nest.value(ipmap, "ip").asString();
		}
		return ip;
    }
    
    /** 获取云主机对应items
     * @param idbean
     * @param executor
     * @param host
     * @return
     */
    public static CArray<Map> getVMItem(IIdentityBean idBean,SQLExecutor executor,Map host){
    	CItemGet itemGet = new CItemGet();
		itemGet.setHostIds(rda_objectValues(host, "hostid").valuesAsLong());
		itemGet.setOutput(new String[] {"itemid", "name", "key_"});
		itemGet.setPreserveKeys(true);
		CArray<Map> items = API.Item(idBean,executor).get(itemGet);
    	return items;
    }
    
    /**获取history最新数据
     * @param executor
     * @param itemid
     * @return
     */
    public static String  getLastHistory(SQLExecutor executor,Long itemid){
		 String value=null;
		 String GET_Histoy_value = "select value from history where itemid=#{itemid} order by clock desc limit 0,1";
			Map params = new HashMap();
			params.put("itemid",itemid);
			CArray<Map> historys= DBselect(executor, GET_Histoy_value, params);
			for(Map history:historys){
				value=Nest.value(history, "value").asString();
			}
		return value;
	}
    
    /**  获取监控最后时间
     * @param idBean
     * @param executor
     * @param item
     * @return
     */
    public static String getLastHistoryTime(IIdentityBean idBean,SQLExecutor executor,Map item){
		int valueType=Nest.value(item, "value_type").asInteger();
		String table = CHistoryManager.getTableName(valueType);
		Map paraMap = new HashMap();
		paraMap.put("itemid", Nest.value(item, "itemid").asLong());
		paraMap.put("tenantid", idBean.getTenantId());
		Map dbdata = DBfetch(DBselect(executor, 
					" SELECT  h.clock  FROM "+table+" h "
					+ " WHERE h.itemid=#{itemid} and h.tenantid=#{tenantid}  order by h.clock desc LIMIT 0,1 "
					, paraMap));
		String clock=Nest.value(dbdata, "clock").asString();
		return clock;
	}
    
    /**将状态值代码转变为状态值对应的中文名，为公共方法，不知如何写入到工具类中，目前先写在这里
	 * @param status
	 * @return
	 */
	public static String status2str(int status)
    {
	     CArray Status = CArray.map(new Object[] { 
	       Integer.valueOf(0), Cphp._("Enabled"), 
	       Integer.valueOf(1), Cphp._("Disabled") });
	     if (Status.containsKey(Integer.valueOf(status))) {
	         return (String)Status.get(Integer.valueOf(status));
	        }
	        return Cphp._("Unknown");
    }
    
    /**状态值显示启用停用状态
	 * @param status
	 * @return
	 */
	public static String status2style(int status)
     {
       switch (status) {
       case 0:
          return "enabled";
       case 1:
         return "disabled";
        }
       return "unknown";
     }
	
	public static String getNumStr(String numstr){
	 Map<String,String> NumberMap=new HashMap<String,String>();
		NumberMap.put("3"+_("of times"), "3");
		NumberMap.put("2"+_("of times"), "2");
		NumberMap.put("1"+_("of times"), "1");
		
		return NumberMap.get(numstr);
	}
	
	 /** 获取开关状态数字值
	 * @param statusstr
	 * @return
	 */
	public static String getStatusStr(String statusstr){
		 Map<String,String> StatusMap=new HashMap<String,String>();
		 StatusMap.put(_("Enable"), "0");
		 StatusMap.put(_("DISABLED"), "1");
		 return StatusMap.get(statusstr);
	}
	
	 /** 获取开关状态数字值
	 * @param statusstr
	 * @return
	 */
/*	public static String getStatusNumToStr(String statusstr){
		 Map<String,String> StatusMap=new HashMap<String,String>();
		 StatusMap.put("0", "开启");
		 StatusMap.put("1", "关闭");
		 return StatusMap.get(statusstr);
	}*/
	 public static String getStatus(int status){
		 if(status==0){
			 return _("Enable");
		 }else{
			 return _("DISABLED");
		 }
	 }
	 
	/**操作符号转换，获取符号
	 * @param symbolstr
	 * @return
	 */
	public static String getSymbol(String symbolstr){
		Map<String,String> SymbolMap=new HashMap<String,String>();
		SymbolMap.put(_("GT"), "gt");
		SymbolMap.put(_("LT"), "lt");//eq
		SymbolMap.put(_("EQ"), "eq");
		SymbolMap.put(_("NE"), "ne");
		 return SymbolMap.get(symbolstr);
	}
	
	public static String getLt(String ltstr){
		Map<String,String> SymbolMap=new HashMap<String,String>();
		SymbolMap.put("gt" , _("GT"));
		SymbolMap.put("lt" , _("LT"));
		SymbolMap.put("\"gt\"" , _("GT"));
		SymbolMap.put("\"lt\"" , _("LT"));
		SymbolMap.put("\"eq\"", _("EQ"));
		SymbolMap.put("\"ne\"", _("NE"));
		 return SymbolMap.get(ltstr);
	 }
    
    /**获取云主机下所有设备id
     * @param executor
     * @param idBean
     * @return
     */
    private static CArray<Map> queryMonServerHostIDs(SQLExecutor executor,IIdentityBean idBean,Long groupIds){
		CHostGet options = new CHostGet();
		options.setOutput(new String[] {"hostid","name"});
		options.setGroupIds(groupIds);
		options.setWithMonitoredItems(true);
		options.setPreserveKeys(true);
		CArray<Map> hosts = API.Host(idBean, executor).get(options);
		return hosts;
	} 
    
	/**
	 * 云主机CPU利用率TOP5
	 * @param idBean
	 * @param executor
	 * @param flag 
	 * @return
	 */
	public static Map queryCloudCPURateTop5(IIdentityBean idBean, SQLExecutor executor,String flag) {
		TopNHelper helper = new TopNHelper(5, "value");
		//获取云主机分组下的所有设备ID
		CArray<Map> hosts = queryMonServerHostIDs(executor,idBean,IMonConsts.MON_VM.longValue());
		String rate = "";
		for(Entry<Object, Map> en: hosts.entrySet()){
			String hostid = en.getKey().toString();
			Object hostName = Nest.value(en.getValue(), "name").$();
			if(flag.equals("cpu")){				
				rate = CommonUtils.returnCPURate(executor, idBean, hostid,array(ItemsKey.CPU_RATE_VM_LINUX,ItemsKey.CPU_RATE_VM_WINDOWS), false,false);
			}else if(flag.equals("memory")){
				rate = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MEMORY_RATE_VM.getValue()).value().round(2).out().print();
			}
			if(!rate.equals("--") && !"null".equals(rate)){				
				helper.put(EasyMap.build("name", hostName, "value", rate));
			}
		}
		Map<String,Double> cpuRateMap = EasyMap.build();
		for(Map m: helper.getResult()) {
			cpuRateMap.put(m.get("name").toString(), Double.valueOf(m.get("value").toString()));
		}
		//对CPU信息重新排序
		if(flag.equals("cpu")){
			List<Map.Entry<String,Double>> infos = new ArrayList<Map.Entry<String,Double>>(cpuRateMap.entrySet());
			Collections.sort(infos,new Comparator<Map.Entry<String, Double>>() {
				public int compare(Entry<String, Double> o1,
						Entry<String, Double> o2) {
					return o2.getValue()-o1.getValue() > 0 ? -1:1;
				}
			});	
			cpuRateMap.clear();
			for(int i = infos.size()-1; i >= 0;i--){
				cpuRateMap.put(infos.get(i).getKey(), infos.get(i).getValue());
			}
		}
		
		return cpuRateMap;
	}
    
	
	/**
	 * 资源使用趋势
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_Trigger_use_trend(IIdentityBean idBean, SQLExecutor executor){
		Map dataMap = new HashMap();
		CArray<Map> cloudHost = statisticsTriggerNum(idBean,executor,"cloudHost");
		Map memoryMap =  statisticsTriggerNum(idBean,executor,"service");
		Map websiteMap =  statisticsTriggerNum(idBean,executor,"website");
		
		dataMap.put("cpu", cloudHost);
		dataMap.put("memory",memoryMap);
		dataMap.put("website",websiteMap);
		return dataMap;
	}
	
	
	/**
	 * 统计租户不同类型某段时间内告警数量
	 * @param executor
	 * @param period 周期
	 * @isHistroy 0新增 1恢复
	 * @return
	 */
	public static CArray<Map> statisticsTriggerNum (IIdentityBean idBean,SQLExecutor executor,String type){
		Map params = new HashMap();
		CArray<Map> reuslt = array();
		Calendar currentDate = Calendar.getInstance();  
		currentDate.add(Calendar.DATE, -7);
		Long startTime = currentDate.getTimeInMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String EVENT_SQL = "SELECT " + 
				"  COUNT(eid) v," + 
				"  time_ t " + 
				"FROM" + 
				"  (SELECT " + 
				"    e.objectid eid," + 
				"    FLOOR((e.clock + 28800) / #{timeBucket}) time_ " + 
				"  FROM" + 
				"    events e " + 
				"  WHERE e.source = 0 " + 
				"    AND e.object = 0 " + 
				"    AND e.clock > #{startTime} " + 
				"    AND e.tenantid = #{tenantid} " + 
				"    AND e.objectid IN(" + 
				"       SELECT tr.triggerid" + 
				"       	FROM triggers tr" + 
				"       	INNER JOIN functions f ON f.triggerid = tr.triggerid" + 
				"       	INNER JOIN items i ON i.itemid = f.itemid" + 
				"          	INNER JOIN hosts h ON h.hostid = i.hostid" + 
				"     		WHERE 1=1" +
				"		#if($showhost) " +
				"			AND h.hostid_os IS NOT NULL" +
				"			AND i.key_ NOT LIKE 'web.test.%' " +
				"            AND i.itemid not in( " +
                "                 SELECT DISTINCT(i.itemid)  FROM applications a,items_applications i " +
                "            WHERE i.applicationid = a.applicationid  " +
                "            AND  a.type in('2','3') " +
                "             AND   a.tenantid = #{tenantid} ) " +
				"		#else" +
				"			AND h.hostid_os IS NULL" +
				"		#end" +
				"	 )" +
				"  GROUP BY e.objectid, time_) t " + 
				"GROUP BY time_ ";
		Long timeBucket  = 24*60*60l;			//时间段
		params.put("timeBucket", timeBucket);
		params.put("startTime", startTime);		//统计开始时间
		params.put("tenantid", idBean.getTenantId());
		if(type.equals("cloudHost")){
			params.put("showhost", true);
		}else if(type.equals("service")){
			EVENT_SQL = "SELECT " + 
					"  COUNT(eid) v," + 
					"  time_ t " + 
					"FROM" + 
					"  (SELECT " + 
					"    e.objectid eid," + 
					"    FLOOR((e.clock + 28800) / #{timeBucket}) time_ " + 
					"  FROM" + 
					"    events e " + 
					"  WHERE e.source = 0 " + 
					"    AND e.object = 0 " + 
					"    AND e.clock > #{startTime} " + 
					"    AND e.tenantid = #{tenantid} " + 
					"    AND e.objectid IN(" + 
					"       SELECT tr.triggerid" + 
					"       	FROM triggers tr" + 
					"       	INNER JOIN functions f ON f.triggerid = tr.triggerid  " + 
					"       	INNER JOIN items_applications i ON i.itemid = f.itemid " + 
					"          	INNER JOIN applications a ON i.applicationid = a.applicationid" + 
					"     		WHERE a.type in('2','3')" +
					"		    and a.tenantid = #{tenantid} " +
					"	 )" +
					"  GROUP BY e.objectid, time_) t " + 
					"GROUP BY time_ ";
			//params.put("showhost", false);
		}else if(type.equals("website")){
			EVENT_SQL = "SELECT " + 
					"  COUNT(eid) v," + 
					"  time_ t " + 
					"FROM(" + 
					"  SELECT " + 
					"    e.objectid eid," + 
					"    FLOOR((e.clock + 28800) / #{timeBucket}) time_ " + 
					"  FROM" + 
					"   events e " + 
					"	WHERE e.source = 0 " + 
					"	AND e.object = 0 " +
					"   AND e.clock > #{startTime} " + 
					"	AND e.tenantid = #{tenantid}" + 
					"	AND e.objectid IN (" + 
					"	SELECT " + 
					"	  tr.triggerid objectid" + 
					"	FROM" + 
					"	  triggers tr " + 
					"	  INNER JOIN functions f " + 
					"	    ON tr.triggerid = f.triggerid" + 
					"	  INNER JOIN items i " + 
					"	    ON i.itemid = f.itemid " + 
					"	    AND i.itemid IN " + 
					"	    (SELECT " + 
					"	      m.itemid " + 
					"	    FROM" + 
					"	      items m " + 
					"	    WHERE m.tenantid = #{tenantid}  " + 
					"	      and m.key_ LIKE 'web.test.%' " + 
					"	     )" + 
					"    )" + 
					"  GROUP BY e.objectid, time_" + 
					"  ) t " + 
					"GROUP BY time_ " + 
					"ORDER BY time_ ASC ";
		}
		
		CArray<Map> eventsCountMap = DBselect(executor, EVENT_SQL, params);
		Long _value = 0l;
		Long _time = 0l;
		long prev = (startTime+28800)/timeBucket-1;
		
		Calendar _currentDate = Calendar.getInstance();
		_currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		_currentDate.set(Calendar.MINUTE, 0);  
		_currentDate.set(Calendar.SECOND, 0);
		_currentDate.add(Calendar.DAY_OF_MONTH, 1);
		Long endTime = (_currentDate.getTimeInMillis()/1000+28800)/timeBucket;
		endTime--;
		
		Date date = new Date();
		eventsCountMap.push(CArray.map(//将最后一个时间加到最后，进行补齐
			"t", endTime,
			"v", 0
		));
		for(Map hostdata: eventsCountMap){
			_time = Nest.value(hostdata, "t").asLong();
			_value = Nest.value(hostdata, "v").asLong();
			
			long distance = _time-prev; //补齐
			for(long i=1; i<=distance; i++){
				long vTime, vValue;
				if(i==distance){
					vTime = timeBucket * _time;
					vValue = _value;
				}else{
					vTime = timeBucket * (prev+i);
					vValue = 0;
				}
				date.setTime((vTime-28800)*1000);
				reuslt.put(sdf.format(date), vValue);
			}
			prev = _time;
		}
		return reuslt;
	}
	
	/**
	 * 资源分配情况
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_resource_distribution(IIdentityBean idBean, SQLExecutor executor){
		Map dataMap = new HashMap<String,Integer>();
		String tid = idBean.getTenantId();
		
		IIdentityBean originIdBean = idBean;
		idBean = IRadarContext.IDBEAN_PLATFORM;
		IRadarContext.getContext().setIdentityBean(idBean);
		Long ctrlNodeHostId = BlocksUtil.getHostIdByMonCloud(idBean, executor);
		
		IaaSClient $t = OpsUtils.getOpenStackClient(tid);
		BackStorInfo backStor = $t.getVolumeClient().volumes().backStor().execute();
		
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		Volumes volumes = $.getVolumeClient().volumes().list(true).queryParam("tenant_id", tid).execute();
		
		Ports ports = $.getNetworkClient().ports().list().execute();
		Quota quota = $.getNetworkClient().quotas().show(tid).execute();
		SecurityGroups securityGroups = $.getNetworkClient().securityGroups().list().execute();
		
		Servers servers = $.getComputeClient().servers().list(true).queryParam("all_tenants", 1).queryParam("tenant_id", tid).execute();
		Flavors flavors = $.getComputeClient().flavors().list(true).queryParam("all_tenants", 1).execute();
		CArray<Flavor> flavorHash = CArray.map();
		for(Flavor flavor: flavors.getList()) {
			flavorHash.put(flavor.getId(), flavor);
		}
		
		
		String cloudHostUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vm.used["+tid+"]").value().out().format();
		String cloudHostTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vm.quota["+tid+"]").value().out().format();
		//云主机
		dataMap.put("cloudHostUsed", cloudHostUsed);
		dataMap.put("cloudHostTotal", cloudHostTotal);
		
		String virtual_kernelUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vmcore.used["+tid+"]").value().out().format();
		String virtual_kernelTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vmcore.quota["+tid+"]").value().out().format();
		//虚拟内核
		dataMap.put("virtual_kernelUsed", virtual_kernelUsed);
		dataMap.put("virtual_kernelTotal", virtual_kernelTotal);
		
		String memoryInfoUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vmmem.used["+tid+"]").value().convertUnit(NormalValue.POW_M).round(0).value().asInteger()+"";
		String memoryInfoTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.vmmem.quota["+tid+"]").value().convertUnit(NormalValue.POW_M).round(0).value().asInteger()+"";
		//内存
		dataMap.put("memoryInfoUsed", memoryInfoUsed);
		dataMap.put("memoryInfoTotal", memoryInfoTotal);
		
		
		int storage_spaceUsed = 0;
		for(Server server: servers.getList()) {
			Flavor f = server.getFlavor();
			f = flavorHash.get(f.getId());
			storage_spaceUsed += f.getDisk();
		}
		
		int storage_spaceTotal = 0;
		for(Volume volume: volumes.getList()) {
			storage_spaceTotal+= volume.getSize();
		}
		
		String subnetUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.subnet.used["+tid+"]").value().out().format();
		String subnetTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.subnet.quota["+tid+"]").value().out().format();
		//子网
		dataMap.put("subnetUsed", subnetUsed);
		dataMap.put("subnetTotal", subnetTotal);
		
		int portUsed = 0;
		for(Port port: ports.getList()) {
			if(tid.equals(port.getTenantId())) {
				portUsed++;
			}
		}
		int portTotal = quota.getPort();
		//端口
		dataMap.put("portUsed", portUsed);
		dataMap.put("portTotal", portTotal);
		
		String routerUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.router.used["+tid+"]").value().out().format();
		String routerTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.router.quota["+tid+"]").value().out().format();
		//路由器
		dataMap.put("routerUsed", routerUsed);
		dataMap.put("routerTotal", routerTotal);
		
		String float_ipUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.floatip.used["+tid+"]").value().out().format();
		String float_ipTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.floatip.quota["+tid+"]").value().out().format();
		//浮动IP
		dataMap.put("float_ipUsed", float_ipUsed);
		dataMap.put("float_ipTotal", float_ipTotal);
		
		String security_group_ruleUsed = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.securityrules.used["+tid+"]").value().out().format();
		String security_group_ruleTotal = LatestValueHelper.buildByNormalKey(ctrlNodeHostId, "iaas.securityrules.quota["+tid+"]").value().out().format();
		//安全组规则
		dataMap.put("security_group_ruleUsed", security_group_ruleUsed);
		dataMap.put("security_group_ruleTotal", security_group_ruleTotal);
		
		int security_groupUsed = 0;
		for(SecurityGroup securityGroup: securityGroups.getList()) {
			if(tid.equals(securityGroup.getTenantId())) {
				security_groupUsed++;
			}
		}
		int security_groupTotal = quota.getSecurityGroup();
		//安全组
		dataMap.put("security_groupUsed", security_groupUsed);
		dataMap.put("security_groupTotal", security_groupTotal);
		
		IRadarContext.getContext().setIdentityBean(originIdBean);
		return dataMap;
	}
	
	/**
	 * 监控状态-求云主机下设备的正常数量和异常数量
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_cloudSerStat(IIdentityBean idBean, SQLExecutor executor){
		Map map = new HashMap<String,Map>();
		Map normalMap = new HashMap();
		Map errorMap = new HashMap();
		Map<String,Map> result = getHealthNumForTenant(idBean,executor,"cloudHost");
		//云主机
		normalMap.put("1", result.get("normalNum"));
		errorMap.put("1", result.get("errorNum"));

		//服务
		result = getHealthNumForTenant(idBean,executor,"server");
		normalMap.put("2", result.get("normalNum"));
		errorMap.put("2", result.get("errorNum"));
		
		//网站
		result = getHealthNumForTenant(idBean,executor,"net");
		normalMap.put("3", result.get("normalNum"));
		errorMap.put("3", result.get("errorNum"));
		
		
		map.put("normal", normalMap);
		map.put("error", errorMap);
		return map;
	}
    
	/**
	 * 根据活动事件是否有值来判断是否正常
	 * @param controlerMap
	 * @param flag
	 * @return
	 */
	private static Map<String,Map> getHealthNumForTenant(IIdentityBean idBean, SQLExecutor executor,String flag){
		Map result = new HashMap<String, Integer>();
		Long[] hostids = {};
		String[] httpnames ={};
		int totalNum = 0;
		int errorNum = 0;
		CArray<Map> hosts = array();
		if(flag.equals("cloudHost")){
			//获取云主机分组下的所有设备ID
			hosts = queryMonServerHostIDs(executor,idBean,IMonConsts.MON_VM.longValue());
			hostids = hosts.keysAsLong();
			totalNum = hostids.length;
		}else if(flag.equals("server")){
			hostids = getHostidForApp(idBean,executor);
			totalNum = hostids.length;
			if(hostids[0]==0){
				totalNum = hostids.length-1;
			}
		}else if(flag.equals("net")){
			httpnames = getHostidForNet(idBean,executor);//添加租户id
			totalNum = httpnames.length;
		}
		
		
		//根据类型分组下设备是否有活动告警来判断正常和异常个数
		try {
			if(flag.equals("server")){
				//errorNum = getHostidForErrorApp(idBean,executor,CArray.valueOf(hostids));
				for(Long hostid:hostids){
					String sql = " "+
						"select count(0) as eventnum from events e                                        "+
						"where e.object = 0                                                               "+
						" and e.source = 0                                                                "+
						" and e.value = 1                                                                 "+
						"and e.tenantid=#{tenantid}                                                       "+
						"and exists( select 1 from triggers t                                             "+
						" inner join functions f on f.triggerid = t.triggerid                             "+
						" inner join items_applications ia  on ia.itemid = f.itemid                       "+
						" where ia.applicationid =  #{applicationid}                                      "+
						"  and t.tenantid = #{tenantid}                                                    "+
						"  and e.objectid = t.triggerid                                                   "+
						")                                                                                ";
				 Map appCountMap = reset((CArray<Map>)DBselect(executor, sql, map("tenantid",idBean.getTenantId(),"applicationid",hostid)));
				 int num =0;
				     num=Nest.value(appCountMap, "eventnum").asInteger();
					 if(num>0){
						 errorNum ++; 
					 }
				}
			}else if(flag.equals("net")){
				for(String httpname:httpnames){
					String sql = " select count(0) as eventnum  FROM events e                  "+                                   
							"	WHERE e.source = 0                                             "+
							"	AND  e.object = 0                                              "+
							"   and e.value = 1        "+
							"   and  e.tenantid = #{tenantid}      "+
							"	AND  e.objectid IN (                                           "+
							"	SELECT                                                         "+
							"	  tr.triggerid as objectid                                     "+
							"	FROM                                                           "+
							"	  triggers tr                                                  "+
							"	  INNER JOIN functions f ON tr.triggerid = f.triggerid         "+
							"	  INNER JOIN items i ON i.itemid = f.itemid                    "+
							"	  AND i.itemid IN                                              "+
							"	    (SELECT  m.itemid  FROM items m                            "+
							"	    WHERE m.tenantid = #{tenantid}      "+
							"	      and m.key_ LIKE 'web.test.time["+httpname+"%'            "+
							"	     )                                                         "+
							 "           )                                                     ";
					 Map appCountMap = reset((CArray<Map>)DBselect(executor, sql, map("tenantid",idBean.getTenantId())));
					 int num = 0;
					    num = Nest.value(appCountMap, "eventnum").asInteger();
					 if(num>0){
						 errorNum ++; 
					 }
				}
			}else{
				for(Long hostid:hostids){
					CEventGet eventOptions = new CEventGet();
					eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
					eventOptions.setObject(EVENT_OBJECT_TRIGGER);
					eventOptions.setOutput(API_OUTPUT_EXTEND);
					eventOptions.setSelectAcknowledges(API_OUTPUT_COUNT);
					eventOptions.setHostIds(hostid);
					eventOptions.setSortfield("clock", "eventid");
					eventOptions.setSortorder(RDA_SORT_DOWN);
					eventOptions.setNopermissions(true);
					CArray<Map> allEventsSlice = API.Event(idBean, executor).get(eventOptions);
					for(Map map :allEventsSlice){
						if(map.get("value").equals("1")){
							errorNum++;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorNum = 0;
		}
		result.put("normalNum", totalNum - errorNum);
		result.put("errorNum", errorNum);
		return result;
	} 
	
	
	private static String[] getHostidForNet(IIdentityBean idBean,SQLExecutor executor){
		Map param = new HashMap();
		param.put("tenantid", idBean.getTenantId());
		String sql = "SELECT DISTINCT(name) FROM httptest h WHERE h.tenantid = #{tenantid}";
		CArray<Map> eventsCountMap = DBselect(executor, sql,param);
		CArray hostids = array();
		for(Map map :eventsCountMap){
			hostids.add(map.get("name"));
		}
		return hostids.valuesAsString();
		
	}
	
	private static Long[] getHostidForApp(IIdentityBean idBean,SQLExecutor executor){
		Map param = new HashMap();
		param.put("tenantid", idBean.getTenantId());
		String sql = "SELECT DISTINCT(a.applicationid) as hostid FROM applications a WHERE a.type in('2','3') and a.tenantid = #{tenantid}";
		CArray<Map> appCountMap = DBselect(executor, sql, param);
		CArray hostids = array();
		for(Map map :appCountMap){
			hostids.add(map.get("hostid"));
		}
		if(empty(hostids)){//为空时，加入0，否则sql报错
			hostids.add("0");
		}
		return hostids.valuesAsLong();
		
	}
	
	private static int getHostidForErrorApp(IIdentityBean idBean,SQLExecutor executor,CArray<Long> appids){
		return getHostidForErrorApp(idBean, executor,appids,0);
		
	}

	public static int getHostidForErrorApp(IIdentityBean idBean,SQLExecutor executor,CArray<Long> appids,int period){
		int num=0;
		if(!empty(appids)){
			Map param = new HashMap();
			param.put("tenantid", idBean.getTenantId());
			param.put("list", appids.toList());
			if(period >0){
				param.put("starttime", Cphp.time()-period);         
			}
			
			try {
				String sql = " select count(DISTINCT(a.applicationid)) as hostnum from items_applications a "+
							"right join functions f                       "+
							" on a.itemid =  f.itemid                    "+
							"right join triggers t                        "+
							"  on f.triggerid = t.triggerid              "+
							"where                                        "+
							" a.applicationid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)"+
							"and  a.tenantid =#{tenantid}                        "+
							"and  t.triggerid in(                        "+
							" select DISTINCT(e.objectid) from events e   "+
							//"and  t.triggerid in(                        "+
							//" select DISTINCT(e.objectid) from events e   "+
							"  where   e.object = 0                       "+
							"       and e.value=0                        ";
							if(period >0){
								sql+="       and e.clock>=#{starttime}            ";
							}
							sql+=" ) ";
				 Map appCountMap = reset((CArray<Map>)DBselect(executor, sql, param));
				 num =Nest.value(appCountMap, "hostnum").asInteger();
			} catch (Exception e) {
				num = 0;
			}
		}
		return num;
		
	}
	
    /**
	 * 获取服务器分组下所有设备的cpu使用率
	 * @param executor
	 * @param idBean
	 * @param isFormat	是否格式化数据
	 * @param isHaveUnit是否保留格式化数据后的单位
	 * @return
	 */
	public static String returnCPURate(SQLExecutor executor,IIdentityBean idBean,boolean isFormat,boolean isHaveUnit){
		String result = "0";
		String hostid = null;
		String cpuRateStr = null;
		BigDecimal cpuRate = new BigDecimal("0");	//CPU利用率
		BigDecimal cpuAdd = new BigDecimal("0");	//循环增加量
		String groupId = null;						//设备分组ID
		try {
			//获取服务器分组下 所有的设备ID
			CArray<Map> hosts = queryMonServerHostIDs(executor, idBean,IMonConsts.MON_VM.longValue());
			//可用的设备数量
			BigDecimal hostNum = new BigDecimal("0");
			//计算CPU利用率
			if(!empty(hosts)){
				for (Map host : hosts) {
					hostid = Nest.value(host, "hostid").$().toString();
					groupId = Nest.value(host,"groups","0", "groupid").asString();
					cpuRateStr = CommonUtils.returnCPURate(executor, idBean, hostid,array(ItemsKey.CPU_RATE_VM_LINUX,ItemsKey.CPU_RATE_VM_WINDOWS), isFormat,isHaveUnit);
					if(cpuRateStr != null && !cpuRateStr.equals("--") && !cpuRateStr.equals("null")){					
						cpuAdd = new BigDecimal(cpuRateStr);
						cpuRate = cpuRate.add(cpuAdd);
						hostNum = hostNum.add(new BigDecimal("1"));
					}
				}
				//计算CPU利用率平均值
				if(hostNum.intValue() != 0){					
					result = Nest.as(cpuRate.divide(hostNum)).asString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    
	/**
	 * 获取所有设备内存使用率(首页概览-内存)
	 * @param executor
	 * @param idBean
	 * @return
	 */
	public static String returnUsedMemory(SQLExecutor executor,IIdentityBean idBean,boolean isFormat,boolean isHaveUnit){
		String result = "0";
		String hostid = null;
		String memoryRateStr = null;
		BigDecimal memoryRate = new BigDecimal("0");	//内存利用率
		BigDecimal memoryAdd = new BigDecimal("0");		//循环增加量
		String groupId = null;							//设备分组
		try {
			//获取服务器分组下 所有的设备ID
			CArray<Map> hosts = queryMonServerHostIDs(executor, idBean,IMonConsts.MON_VM.longValue());
			//可用的设备数量
			BigDecimal hostNum = new BigDecimal("0");
			//计算内存利用率
			if(!empty(hosts)){
				for (Map host : hosts) {
					hostid = Nest.value(host, "hostid").$().toString();
					groupId = Nest.value(host,"groups","0", "groupid").asString();
					memoryRateStr = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.MEMORY_RATE_VM.getValue(),hostid, isFormat,isFormat);
					if(memoryRateStr != null && !memoryRateStr.equals("--")){					
						memoryAdd = new BigDecimal(memoryRateStr);
						memoryRate = memoryRate.add(memoryAdd);
						hostNum = hostNum.add(new BigDecimal("1"));
					}
				}
				//计算内存利用率平均值
				if(hostNum.intValue() != 0){					
					result = Nest.as(memoryRate.divide(hostNum)).asString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
 
	/**
	 * 获取所有设备磁盘利用率
	 * @param executor
	 * @param idBean
	 * @param isFormat	是否格式化数据
	 * @param isHaveUnit是否保留格式化后的数据的单位
	 * @return
	 */
	public static String returnUsedDiskRate(SQLExecutor executor,IIdentityBean idBean,boolean isFormat,boolean isHaveUnit){
		int availabeNum = 0;			//可用数量
		String result = "0";			//磁盘利用率
		String hostid = null;			//设备ID
		String addValue = "--";			//各个设备利用率
		String[] addValues = null;		//结果集
		BigDecimal diskUsedRate = new BigDecimal("0");
		String groupId = null;			//设备分组
		try {
			//获取服务器分组下 所有的设备ID
			CArray<Map> hosts = queryMonServerHostIDs(executor, idBean,IMonConsts.MON_VM.longValue());
			if(!empty(hosts)){
				for (Map host : hosts) {
					hostid = Nest.value(host, "hostid").$().toString();
					groupId = Nest.value(host,"groups","0", "groupid").asString();
					addValue = CommonUtils.returnDiskRate(executor, idBean, hostid,ItemsKey.DISK_RATE_VM.getValue(),isFormat,isHaveUnit);
					if(addValue != null && !addValue.equals("--")){
						addValues = addValue.split(" ");
						diskUsedRate = diskUsedRate.add(new BigDecimal(addValues[0]));
						availabeNum++;
					}
				}
				if(availabeNum != 0){				
					diskUsedRate = diskUsedRate.divide(new BigDecimal(availabeNum));
					result = diskUsedRate.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}	
	
	public static boolean IscontainsDelay(int delay){
		boolean iscon=false;
		try {
			Map testmap = new HashMap();
			testmap.put(ONITORING_FREQUENCY_15, ONITORING_FREQUENCY_15);
			testmap.put(ONITORING_FREQUENCY_30, ONITORING_FREQUENCY_30);
			testmap.put(ONITORING_FREQUENCY_45, ONITORING_FREQUENCY_45);
			testmap.put(ONITORING_FREQUENCY_60, ONITORING_FREQUENCY_60);
			iscon = testmap.containsKey(delay);
		} catch (Exception e) {
			iscon = false;
		}
		return iscon;
	}
	 
	/**  获取租户所属用户方法
	 * @param idBean
	 * @param executor
	 * @param userId
	 * @param hostid
	 * @return
	 */
	public static String getUserById(IIdentityBean idBean,SQLExecutor executor, String userId,String hostid){
		if(LatestValueHelper.NA.equals(userId) ){
			return "--";
		}else{
			String belonguser=null;;
			CUserGet options = new CUserGet();
			options.setUserIds(userId);
			options.setEditable(false);
			options.setOutput(new String[]{"name"});
			CArray<Map> users = API.User(idBean, executor).get(options);
			if(!empty(users)){
				belonguser = Nest.value(users, 0,"name").asString();
			}
			return belonguser;
		 }
	}
	
	/** 给简单应用FTP等状态加样式
	 * @param obj
	 * @return
	 */
	public static Object getSimpleAppStatus(Object obj){
		CSpan ctn = new CSpan();
		CSpan text = new CSpan();
			text.setAttribute("style", "margin-left: 0.5em;");
			ctn.addItem(text);
		if("1".equals(String.valueOf(obj))){
			ctn.addItem(new CImg("images/gradients/normal.png"));
			text.addItem(_("Available"));
			return ctn;
		}else if("0".equals(String.valueOf(obj))) {
			ctn.addItem(new CImg("images/gradients/anormal.png"));
			text.addItem(_("Not available"));
			return ctn;
		}else {
			return new CDiv(new CSpan(Cphp._("Unknown"), "unknown"),"switch wz");
		}
		
	}
	
	public static Object getSimpleAppNtpWinStatus(Object obj){
		if("0".equals(String.valueOf(obj))){
			return getSimpleAppStatus("1");
		}else if("6".equals(String.valueOf(obj))) {
			return getSimpleAppStatus("0");
		}else {
			return getSimpleAppStatus(obj);
		}
		
	}
	
	/**  拼接租户告警描述
	 * @param vmname
	 * @param alarmtype
	 * @param gaonum
	 * @param operator
	 * @param numerical
	 * @return
	 */
	public static String getTriggerDescription(String vmname,String alarmtype,String gaonum,String operator,String numerical){
		return getTriggerDescription(vmname, alarmtype, gaonum, operator, numerical, true);
	}
	
	public static String getTriggerDescription(String vmname,String alarmtype,String gaonum,String operator,String numerical,boolean needEquals){
		String description = (needEquals?"=":"")+vmname+":"+ alarmtype+"　连续　"+gaonum+_("of times")+"　"+operator+"　"+numerical;
		return description;
	}
	
	/**  通过hostid获取triggers，再获取租户告警第一个ID值
	 * @param hostid
	 * @return
	 */
	public static Long getTrggersToTriggerid(IIdentityBean idBean,SQLExecutor executor,Long hostid){
		return getTrggersToTriggerid(idBean,executor,hostid,false);
	}
	/**  通过hostid获取triggers，再获取租户告警第一个ID值
	 * @param hostid
	 * @return
	 */
	public static Long getTrggersToTriggerid(IIdentityBean idBean,SQLExecutor executor,Long id,boolean isApp){
		CTriggerGet toptions = new CTriggerGet();
		if(isApp){
			toptions.setApplicationIds(id);
		}else{
			toptions.setHostIds(id);
		}
	    toptions.setOutput(new String[]{"triggerid"});
		toptions.setSelectItems(new String[]{"itemid"});
		toptions.setSelectFunctions(new String[]{"parameter"});
		CArray<Map> triggers = API.Trigger(idBean, executor).get(toptions);
		return getTriggerid(triggers);
	}
	
	/**从已有的triggers中获取租户告警第一个ID值
	 * @param triggers
	 * @return
	 */
	public static Long getTriggerid(CArray<Map> triggers){
		Long triggerid = 0L;
		for(Map trigger : triggers){
			triggerid=Nest.value(trigger, "triggerid").asLong();
		}
		return triggerid;
	}
	
	
	 /** 重写提示信息方法，只获取报错信息，多余信息不显示
	 * @param bool
	 * @param okmsg
	 * @param errmsg
	 */
	public static void show_messages(boolean bool, String okmsg, String errmsg) {
		    Map page = RadarContext.page();
		    @SuppressWarnings("unchecked")
			List<CMessage> oldrda_messages = (List)g.RDA_MESSAGES.$();
		    List<CMessage> rda_messages = new ArrayList<CMessage>();
		    if(oldrda_messages.size()>1){
		    	 rda_messages.add(oldrda_messages.get(oldrda_messages.size()-1));
		    }else{
		    	rda_messages=oldrda_messages;
		    }

		    if (!Cphp.defined("PAGE_HEADER_LOADED")) {
		      return;
		    }
		    if (Cphp.defined("RDA_API_REQUEST")) {
		      return;
		    }
		    if (!Cphp.isset(page.get("type"))) {
		      page.put("type", Integer.valueOf(0));
		    }
		    
		    CArray message = CArray.array();
		    int width = 0;
		    int height = 0;
		    
		    String _msg = null;
		    if ((!bool) && (!Cphp.is_null(errmsg))) {
		      _msg = Cphp._("ERROR") + ": " + errmsg;
		    } else if ((bool) && (!Cphp.is_null(okmsg))) {
		      _msg = okmsg;
		    }
		    
		    CTable msg_tab = null;
		    CDiv msg_details; if (Cphp.isset(_msg)) {
		      switch (((Integer)page.get("type")).intValue()) {
		      case 1: 
		        Cphp.array_push(message, new Object[] { CArray.map(new Object[] {
		          "text", _msg, 
		          "color", !bool ? CArray.map(new Object[] { "R", Integer.valueOf(255), "G", Integer.valueOf(0), "B", Integer.valueOf(0) }) : CArray.map(new Object[] { "R", Integer.valueOf(34), "G", Integer.valueOf(51), "B", Integer.valueOf(68) }), 
		          "font", Integer.valueOf(2) }) });
		        
		        width = ((Integer)Cphp.max(new Integer[] { Integer.valueOf(width), Integer.valueOf(Cgd.imagefontwidth(2) * rda_strlen(_msg) + 1) })).intValue();
		        height += Cgd.imagefontheight(2) + 1;
		        break;
		      case 2: 
		        Cphp.echo(Cphp.htmlspecialchars(_msg) + "\n");
		        break;
		      case 0: 
		      default: 
		        msg_tab = new CTable(_msg, bool ? "msgok" : "msgerr");
		        msg_tab.setCellPadding(0);
		        msg_tab.setCellSpacing(0);
		        
		        CArray row = CArray.array();
		        
		        CCol msg_col = new CCol(HtmlUtil.bold(_msg), "msg_main msg");
		        msg_col.setAttribute("id", "page_msg");
		        row.add(msg_col);
		        
		        if ((Cphp.isset(rda_messages)) && (!Cphp.empty(rda_messages))) {
		          msg_details = new CDiv(Cphp._("Details"), "blacklink");
		          msg_details.setAttribute("onclick", "javascript: showHide(\"msg_messages\", IE ? \"block\" : \"table\");");
		          msg_details.setAttribute("title", Cphp._("Maximize") + "/" + Cphp._("Minimize"));
		          Cphp.array_unshift(row, new Object[] { new CCol(msg_details, "clr") });
		        }
		        msg_tab.addRow(row);
		        msg_tab.show();
		      }
		      
		    }
		    
		    if ((Cphp.isset(rda_messages)) && (!Cphp.empty(rda_messages))) { CMessage msg;
		      if (((Integer)page.get("type")).intValue() == 1) {
		        int _msg_font = 2;
		        for (msg_details = (CDiv) rda_messages.iterator(); ((Iterator<CMessage>) msg_details).hasNext();) { msg = (CMessage)((Iterator<CMessage>) msg_details).next();
		          if ("error".equals(msg.getType())) {
		            Cphp.array_push(message, new Object[] { CArray.map(new Object[] {
		              "text", msg.getMessage(), 
		              "color", CArray.map(new Object[] { "R", Integer.valueOf(255), "G", Integer.valueOf(55), "B", Integer.valueOf(55) }), 
		              "font", Integer.valueOf(_msg_font) }) });
		          }
		          else {
		            Cphp.array_push(message, new Object[] { CArray.map(new Object[] {
		              "text", msg.getMessage(), 
		              "color", CArray.map(new Object[] { "R", Integer.valueOf(155), "G", Integer.valueOf(155), "B", Integer.valueOf(55) }), 
		              "font", Integer.valueOf(_msg_font) }) });
		          }
		          
		          width = ((Integer)Cphp.max(new Integer[] { Integer.valueOf(width), Integer.valueOf(Cgd.imagefontwidth(_msg_font) * rda_strlen(msg.getMessage()) + 1) })).intValue();
		          height += Cgd.imagefontheight(_msg_font) + 1;
		        }
		      }
		      else if (((Integer)page.get("type")).intValue() == 2) {
		        for (CMessage msg1 : rda_messages) {
		          Cphp.echo("[" + msg1.getType() + "] " + msg1.getMessage() + "\n");
		        }
		      }
		      else {
		        CList lst_error = new CList(null, "messages");
		        for (CMessage msg1 : rda_messages) {
		          lst_error.addItem(msg1.getMessage(), msg1.getType());
		          bool = (bool) && (!"error".equals(rda_strtolower(msg1.getType())));
		        }
		        int msg_show = 6;
		        int msg_count = Cphp.count(rda_messages);
		        if (msg_count > msg_show) {
		          msg_count = msg_show * 16;
		          lst_error.setAttribute("style", "height: " + msg_count + "px;");
		        }
		        CTable tab = new CTable(null, bool ? "msgok" : "msgerr");
		        tab.setCellPadding(0);
		        tab.setCellSpacing(0);
		        tab.setAttribute("id", "msg_messages");
		        tab.setAttribute("style", "width: 100%;");
		        if ((Cphp.isset(msg_tab)) && (bool)) {
		          tab.setAttribute("style", "display: none;");
		        }
		        tab.addRow(new CCol(lst_error, "msg"));
		        tab.show();
		      }
		      rda_messages.clear();
		    }
		    
		    if ((((Integer)page.get("type")).intValue() == 1) && (Cphp.count(message) > 0)) {
		      width += 2;
		      height += 2;
		    }
		  }
	
	   public static void show_messages() {
	    show_messages(true, null, null);
	  }
	  
	  public static void show_message(String msg) {
	    show_messages(true, msg, "");
	  }
	  
	  public static void show_messages(boolean bool) {
	    show_messages(bool, null, null);
	  }
	  
	  public static void show_messages(boolean bool, String okmsg) {
	    show_messages(bool, okmsg, null);
	  }
	 

	  public static int rda_strlen(String _str)
	   {
	     if (Cphp.defined("RDA_MBSTRINGS_ENABLED")) {
	       return Cphp.mb_strlen(_str);
	     }
	     
	     return Cphp.strlen(_str);
	   }
	   
	   public static int rda_strlen(char c)
	   {
	     return String.valueOf(c).getBytes().length;
	   }
	   
	   public static String rda_strtolower(String _str) {
		   if (Cphp.defined("RDA_MBSTRINGS_ENABLED")) {
		     return Cphp.mb_strtolower(_str);
		   }
		   return Cphp.strtolower(_str);
	   }
	   
	/** 检查此端口号是否已经使用
	 * @param executor
	 * @param idbean
	 * @param applicationid
	 * @param testkey
	 * @return
	 */
	public static boolean checkSameport(SQLExecutor executor,IIdentityBean idBean,Long hostid,Long applicationid,String testkey,int type){
		boolean result=false;
		String appsql = "select applicationid from applications where hostid=#{hostid} and applicationid!=#{applicationid} and type=#{type} and tenantid=#{tenantid}";
    	CArray applicationids = DBselect(executor, appsql, 
    			                     map("hostid",hostid,"applicationid",applicationid,"type",type,"tenantid",idBean.getTenantId()));
    	CItemGet itemGet = new CItemGet();
		itemGet.setApplicationIds(rda_objectValues(applicationids, "applicationid").valuesAsLong());
		itemGet.setOutput(new String[] {"key_"});
		itemGet.setEditable(true);
		CArray<Map> itemkeys = API.Item(idBean, executor).get(itemGet);
		for(Map itemkey:itemkeys){
			if(testkey.equals(Nest.value(itemkey, "key_").asString())){
				result=true;
				break;
			}
		}
		return result;
	}
	
	
	
	/**  给设备名称去除默认前缀
	 * @param appname 设备名
	 * @param defprofix  默认前缀
	 */
	public static String removeDefaultProfix(String appname,String defprofix){
		if(appname != null){
			if(appname.contains(defprofix)){
				appname = appname.substring(defprofix.length(), appname.length());
				return appname;
	   		}
		}
		return appname;
	}
}
