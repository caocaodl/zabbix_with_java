package com.isoft.web.bean.capacity;

import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.types.CArray;

public class CapacityUtil {

	public static CArray<Map> getTOPNRateByCPU(SQLExecutor sqlExecutor,String tenantid,Integer period){
		
		 StringBuffer buf=new StringBuffer();
         buf.append(" select * from (  ");
		 buf.append("  select it.hostid,ho.host as name,avg(s.value) as avgvalue from history s,items it,hosts ho where  ");
		 buf.append("  it.hostid=ho.hostid  ");
		 buf.append("  and it.itemid =s.itemid  ");
		 buf.append("  and it.itemid in (     ");
		 buf.append("   select i.itemid from items i where i.key_ =#{key}  "); 
		 buf.append("  and i.hostid in(  ");
		 buf.append("     select h.hostid from hosts h,hosts_groups hg ");
		 buf.append("        where hg.hostid=h.hostid ");
		 buf.append("         and hg.groupid in(102) ");
		 buf.append("     )  ");
		 buf.append("  )  ");
		 buf.append("  and  s.tenantid = #{tenantid} ");
		 buf.append("  and s.clock > #{period} ");
		 buf.append("  group by it.hostid,ho.host ");
		 buf.append("  union ");
		 buf.append("  select it.hostid,ho.host as name,avg(s.value) as avgvalue from history_uint s,items it,hosts ho where  ");
		 buf.append("  it.hostid=ho.hostid  ");
		 buf.append("  and it.itemid =s.itemid  ");
		 buf.append("  and it.itemid in (  ");  
		 buf.append("  select itemid from items i where i.key_ in('cpuUsage') ");
		 buf.append("  and i.hostid in( ");
		 buf.append("     select h.hostid from hosts h,hosts_groups hg ");
		 buf.append("        where hg.hostid=h.hostid ");
		 buf.append("         and hg.groupid in(101) ");
		 buf.append("     )  ");
		 buf.append("  )  ");
		 buf.append("  and  s.tenantid = #{tenantid} ");
		 buf.append("  and s.clock > #{period} ");
		 buf.append("  group by it.hostid,ho.host   ");
		 buf.append("  ) t ");
		 buf.append("  order by t.avgvalue desc ");

		 Map sqlParam = EasyMap.build(
				    "key","wmi.get[root\\cimv2,Select LoadPercentage from Win32_processor]",
					"tenantid",tenantid,
					"period", (Cphp.time()- period)
				);
		 
		 CArray<Map> results = DBselect(sqlExecutor, buf.toString(), sqlParam);
		 return results;
	 	
	}
	
	public static CArray<Map> getTOPNRateByMemory(SQLExecutor sqlExecutor,String tenantid,Integer period){
		StringBuffer buf=new StringBuffer();
		
		 buf.append("  select it.hostid,ho.host as name,avg(s.value) as avgvalue from history s,items it,hosts ho where  ");
		 buf.append("  it.hostid=ho.hostid  ");
		 buf.append("  and it.itemid =s.itemid  ");
		 buf.append("  and it.itemid in (    ");
		 buf.append("    select itemid from items i where i.key_ in(#{key1},#{key2})  ");
		 buf.append("  and i.hostid in( ");
		 buf.append("   select h.hostid from hosts h,hosts_groups hg ");
		 buf.append("      where hg.hostid=h.hostid   ");
		 buf.append("       and hg.groupid in(101,102) ");
		 buf.append("   )                              ");
		 buf.append(")                                 ");
		 buf.append(" and  s.tenantid = #{tenantid}                       ");
		 buf.append(" and s.clock > #{period}                             ");
		 buf.append(" group by it.hostid,ho.host                          ");
		 buf.append(" order by avgvalue desc                              ");
		
		Map sqlParam = EasyMap.build(
				"key1","memUsage[{#SNMPVALUE}]",
				"key2","vm.memory.size[pused]",
				"tenantid",tenantid,
				"period", (Cphp.time()- period)
			);
	    CArray<Map> results = DBselect(sqlExecutor, buf.toString(), sqlParam);
	    return results;
	}
	
	
	public static CArray<Map> getTOPNRateByDisk(SQLExecutor sqlExecutor,String tenantid,Integer period){
		StringBuffer buf=new StringBuffer();
		 buf.append("  select it.hostid,ho.host as name,avg(s.value) as avgvalue from history s,items it,hosts ho where  ");
		 buf.append("  it.hostid=ho.hostid  ");
		 buf.append("  and it.itemid =s.itemid  ");
		 buf.append("  and it.itemid in (    ");
		 buf.append("    select itemid from items i where i.key_ in('fsUsage[{#SNMPINDEX}]','vfs.fs.size[{#FSNAME},pused]' )  ");
		 buf.append("  and i.hostid in( ");
		 buf.append("   select h.hostid from hosts h,hosts_groups hg ");
		 buf.append("      where hg.hostid=h.hostid   ");
		 buf.append("       and hg.groupid in(101,102) ");
		 buf.append("   )                              ");
		 buf.append(")                                 ");
		 buf.append(" and  s.tenantid = #{tenantid}                       ");
		 buf.append(" and s.clock > #{period}                             ");
		 buf.append(" group by it.hostid,ho.host                          ");
		 buf.append(" order by avgvalue desc                              ");
		
		Map sqlParam = EasyMap.build(
				"tenantid",tenantid,
				"period", (Cphp.time()- period)
			);
	    CArray<Map> results = DBselect(sqlExecutor, buf.toString(), sqlParam);
	    return results;
	}
	
	/**不同设备类型存在不同history table表中
	 * @param sqlExecutor  数据源
	 * @param tenantid 租户id
	 * @param period 时间段
	 * @param shownum 展示数目
	 * @param unitkeystr 无字符键值字符串
	 * @param uintgroupidstr  无字符设备组字符串
	 * @param keystr  普通键值字符串
	 * @param groupidstr  普通设备组字符串
	 * @param positive  普通设备组字符串
	 * @return
	 */
	public static CArray<Map> getTOPN(SQLExecutor sqlExecutor,String tenantid,Integer period,Integer shownum,
			  String unitkeystr,String uintgroupidstr,String keystr,String groupidstr,boolean positive){
		StringBuffer buf=new StringBuffer();
		String orderby ="";
		if(positive){
			orderby = "desc";
		}
		buf.append("select t.va,t.itemid from (                               ");
		buf.append("  select avg(s.value) as va,s.itemid from history_uint s  "); 
		buf.append("	where s.itemid in (                                   ");
		buf.append("		select i.itemid from items i where i.key_ in("+unitkeystr+")  ");
		buf.append("		and i.hostid in(                                  ");
		buf.append("		   select h.hostid from hosts h,hosts_groups hg   ");
		buf.append("		      where hg.hostid=h.hostid                    ");
		buf.append("		       and hg.groupid in("+uintgroupidstr+")      ");
		buf.append("		   )                                              ");
		buf.append("	)                                                     ");
		buf.append("	and  s.tenantid = #{tenantid}                         ");
		buf.append("	and s.clock > #{period}                               ");
		buf.append("	group by s.itemid                                     ");
		buf.append("union                                                     ");
		buf.append("	select avg(s.value) as va,s.itemid from history s     ");
		buf.append("	where s.itemid in (                                   ");
		buf.append("		select i.itemid from items i where i.key_ in("+keystr+")  ");
		buf.append("		and i.hostid in(                                  ");
		buf.append("		   select h.hostid from hosts h,hosts_groups hg   ");
		buf.append("		      where hg.hostid=h.hostid                    ");
		buf.append("		       and hg.groupid in("+groupidstr+")          ");
		buf.append("		   )                                              ");
		buf.append("	)                                                     ");
		buf.append("	and  s.tenantid = #{tenantid}                         ");
		buf.append("	and s.clock >#{period}                                ");
		buf.append("	group by s.itemid  ) t                                ");
		buf.append(" order by t.va   "+orderby+ "                             ");
		buf.append(" linmit   #{shownum}                                      ");

		Map sqlParam = EasyMap.build(
				"tenantid",tenantid,
				"period", (Cphp.time()- period),
				"shownum", shownum
			);
	    CArray<Map> results = DBselect(sqlExecutor, buf.toString(), sqlParam);
		return results;
	}
	
	/**不同设备类型存在相同history 表中
	 * @param sqlExecutor  数据源
	 * @param tenantid 租户id
	 * @param period 时间段
	 * @param shownum 展示数目
	 * @param keystr  普通键值字符串
	 * @param groupidstr  普通设备组字符串
	 * @param table  所属history表
	 * @return
	 */
	public static CArray<Map> getTOPN(SQLExecutor sqlExecutor,String tenantid,Integer period,Integer shownum,
			  String keystr,String groupidstr,String table,boolean positive){
		StringBuffer buf=new StringBuffer();
		String orderby ="";
		if(positive){
			orderby = "desc";
		}
		buf.append("	select avg(s.value) as va,s.itemid from "+table+" s   ");
		buf.append("	where s.itemid in (                                   ");
		buf.append("		select i.itemid from items i where i.key_ in("+keystr+")  ");
		buf.append("		and i.hostid in(                                  ");
		buf.append("		   select h.hostid from hosts h,hosts_groups hg   ");
		buf.append("		      where hg.hostid=h.hostid                    ");
		buf.append("		       and hg.groupid in("+groupidstr+")          ");
		buf.append("		   )                                              ");
		buf.append("	)                                                     ");
		buf.append("	and  s.tenantid = #{tenantid}                         ");
		buf.append("	and s.clock >#{period}                                ");
		buf.append("	group by s.itemid                                     ");
		buf.append("    order by t.va  "+ positive +"                         ");
		buf.append("    linmit   #{shownum}                                   ");
		Map sqlParam = EasyMap.build(
				"tenantid",tenantid,
				"period", (Cphp.time()- period),
				"shownum", shownum
			);
	    CArray<Map> results = DBselect(sqlExecutor, buf.toString(), sqlParam);
		return results;
	}
	
	
}
