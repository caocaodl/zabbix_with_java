package com.isoft.iradar.web.daoimpl;

import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CInspectionReportDAO extends BaseDAO {

	public CInspectionReportDAO(SQLExecutor executor) {
		super(executor);
	}

	/**
	 * 校验是否重名
	 */
	public boolean exists(Object name, String id){
		String sql="SELECT reportid FROM i_inspection_reports WHERE name=#{name}";
		Map paraMap = new HashMap();
		paraMap.put("name", name);
		if(id!=null){
			paraMap.put("reportid", id);
			sql+=" AND reportid<>#{reportid}";
		}
		
		Map result = DBfetch(DBselect(this.getSqlExecutor(), sql, paraMap));
		
		return empty(result);
	}
	
	public boolean create(IIdentityBean idBean,CArray<Map> inspectionReports) {
		//名称校验
		for(Map inspectionReport : inspectionReports){
			if(!this.exists(Nest.value(inspectionReport, "name").$(), null)){
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("inspection report \"%1$s\" already exists.", Nest.value(inspectionReport,"name").$()));
			}			
		}
		//获得第一个要保存巡检报告id
		Long id = DBUtil.get_dbid(null, this.getSqlExecutor(), "i_inspection_reports", "reportid", inspectionReports.size());
		
		//保存巡检报告
		boolean flag=true;
		CArray<Map> items=array();
		CArray<Map> hosts=array();
		int result=1;
		for(Map inspectionReport : inspectionReports) {
			result = this.getSqlExecutor().executeInsertDeleteUpdate(
				"INSERT INTO `i_inspection_reports` (tenantid, reportid,batchnum, name, username, time,active_till, groupid, status, executed"+
				",timeperiod_type,every,month,dayofweek,day,start_time,period,start_date,create_time,batch_time) VALUES (#{tenantid}, #{reportid}, #{batchnum}, #{name}, #{username}, #{time}, #{active_till},#{groupid}, #{status}, #{executed}"+
				",#{timeperiod_type}, #{every}, #{month}, #{dayofweek}, #{day}, #{start_time},#{period}, #{start_date},  #{create_time},  #{batch_time})", 
				map(
					"tenantid", Nest.value(inspectionReport, "tenantid").$(),
					"reportid", id,
					"batchnum", 0,
					"name",     Nest.value(inspectionReport, "name").$(),
					"username", Nest.value(inspectionReport, "username").$(),
					"time",     Nest.value(inspectionReport, "time").$(),
					"active_till",     Nest.value(inspectionReport, "active_till").$(),
					"groupid",  Nest.value(inspectionReport, "groupid").$(),
					"status",   Nest.value(inspectionReport, "status").$(),
					"executed", Nest.value(inspectionReport, "executed").$(),
					"timeperiod_type", Nest.value(inspectionReport, "timeperiod_type").$(),
					"every",    Nest.value(inspectionReport, "every").$(),
					"month",    Nest.value(inspectionReport, "month").$(),
					"dayofweek", Nest.value(inspectionReport, "dayofweek").$(),
					"day",      Nest.value(inspectionReport, "day").$(),
					"start_time", Nest.value(inspectionReport, "start_time").$(),
					"period",   Nest.value(inspectionReport, "period").$(),
					"start_date", Nest.value(inspectionReport, "start_date").$(),
					"create_time",time(),
					"batch_time",0
				)
			);
			if(result == 0) {
				flag=false;
				break;
			}else{
				//保存 关联的设备类型
				hosts=Nest.value(inspectionReport, "hosts").$s();
				flag = saveInspectionReportItems(idBean,Nest.value(inspectionReport, "tenantid").$(), id, hosts);				
			}
			id++;
		}

		return flag;
	}
	
	/**
	 * 修改巡检报告
	 * @param baogaos
	 * @return
	 */
	public boolean update(IIdentityBean idBean,CArray<Map> inspectionReports) {
		//名称校验
		for(Map inspectionReport : inspectionReports){
			if(!this.exists(Nest.value(inspectionReport, "name").$(), Nest.value(inspectionReport, "reportid").asString())){
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("inspection report \"%1$s\" already exists.", Nest.value(inspectionReport,"name").$()));
			}			
		}
		
		boolean flag=true;
		CArray<Map> items=array();
		CArray<Map> hosts=array();
		int result=1;
		
		//删除关联的设备类型
		CArray<Long> dinspectionReportids=array();
		for(Map inspectionReport: inspectionReports){
			dinspectionReportids.add(Nest.value(inspectionReport, "reportid").asLong());
		}
		deleteInspectionReportItems(this.getSqlExecutor(), dinspectionReportids);
		
		//修改巡检报告
		for(Map inspectionReport : inspectionReports) {			         
			result = this.getSqlExecutor().executeInsertDeleteUpdate(
				" UPDATE i_inspection_reports bg " +
				" SET bg.tenantid=#{tenantid}, bg.name=#{name}, bg.username=#{username}, bg.time=#{time},bg.active_till=#{active_till}, bg.groupid=#{groupid}, bg.status=#{status}, "
				+ "bg.executed=#{executed},timeperiod_type=#{timeperiod_type},every=#{every},month=#{month},dayofweek=#{dayofweek},day=#{day},start_time=#{start_time},period=#{period},start_date=#{start_date} " +
				" WHERE bg.reportid=#{reportid}", 
				map(
					"tenantid", Nest.value(inspectionReport, "tenantid").$(),
					"name",     Nest.value(inspectionReport, "name").$(),
					"username", Nest.value(inspectionReport, "username").$(),
					"active_till",     Nest.value(inspectionReport, "active_till").$(),
					"time",     Nest.value(inspectionReport, "time").$(),
					"groupid",  Nest.value(inspectionReport, "groupid").$(),
					"status",   Nest.value(inspectionReport, "status").$(),
					"executed", Nest.value(inspectionReport, "executed").$(),
					"reportid", Nest.value(inspectionReport, "reportid").$(),
					"timeperiod_type", Nest.value(inspectionReport, "timeperiod_type").$(),
					"every",    Nest.value(inspectionReport, "every").$(),
					"month",    Nest.value(inspectionReport, "month").$(),
					"dayofweek", Nest.value(inspectionReport, "dayofweek").$(),
					"day",      Nest.value(inspectionReport, "day").$(),
					"start_time", Nest.value(inspectionReport, "start_time").$(),
					"period",   Nest.value(inspectionReport, "period").$(),
					"start_date", Nest.value(inspectionReport, "start_date").$()
				)
			);
			if(result == 0) {
				flag=false;
				break;
			}else{
				//保存 关联的设备类型
				hosts=Nest.value(inspectionReport, "hosts").$s();
					
				flag = saveInspectionReportItems(idBean,Nest.value(inspectionReport, "tenantid").$(), Nest.value(inspectionReport, "reportid").asLong(), hosts);					
			}
		}

		return true;
	}	
	
	/**
	 * 删除巡检报告
	 * @param executor
	 * @param baogaoids
	 * @return
	 */
	public boolean deleteInspectionReports(SQLExecutor executor, CArray<Long> inspectionReportids) {
		int result=1;
		boolean flag = true;
		result = executor.executeInsertDeleteUpdate(//删除巡检主表数据
			"delete from i_inspection_reports where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
			map(
				"list", inspectionReportids.toList()
			)
		);
		result = executor.executeInsertDeleteUpdate(//删除巡检items表数据
				"delete from i_inspection_report_items where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
				map(
					"list", inspectionReportids.toList()
				)
			);
		result = executor.executeInsertDeleteUpdate(//删除巡检批次数据
				"delete from i_inspection_report_batch where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
				map(
					"list", inspectionReportids.toList()
				)
			);
		result = executor.executeInsertDeleteUpdate(//删除巡检历史数据
				"delete from i_inspection_report_historys where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
				map(
					"list", inspectionReportids.toList()
				)
			);
		if(result == 0){
			flag=false;
		}
		return flag;
	}
	
	/**
	 * 变更巡检状态
	 * @param executor
	 * @param baogaoids
	 * @return
	 */
	public int updateInspectionReportsStatus(IIdentityBean idBean,SQLExecutor executor, CArray<Long> inspectionReportids,int status) {
		int updatenum = executor.executeInsertDeleteUpdate(
			"update i_inspection_reports set status=#{status}  where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)"
			+ " and tenantid=#{tenantid}", 
			map(
				"status",status,
				"list", inspectionReportids.toList(),
				"tenantid",idBean.getTenantId()
			)
		);
		return updatenum;
	}
	
	/**
	 * 保存巡检报告关联监控项
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean saveInspectionReportItems(IIdentityBean idBean,Object tenantid, Long reportid, CArray<Map> hosts) {
		//Long newrepitemid = DBUtil.get_dbid(null, this.getSqlExecutor(), "i_inspection_report_items", "reportitemid", hosts.size() *items.size());

		boolean flag=true;
		int result=1;
		for(Long hostid : hosts.valuesAsLong()){
			CHostGet options = new CHostGet();
			options.setHostIds(hostid);
			options.setOutput(new String[]{"host"});
			options.setEditable(true);
			CArray<Map> hostmap=API.Host(idBean, this.getSqlExecutor()).get(options);
			String hostname=null;
			for(Map host:hostmap){
				hostname = Nest.value(host, "host").asString();
			}
			
			CItemGet citem = new CItemGet();
			citem.setHostIds(hostid);
			citem.setOutput(new String[]{"itemid","delay","name","key_"});
			citem.setEditable(true);
			CArray<Map> itemmap = API.Item(idBean, this.getSqlExecutor()).get(citem);
			itemmap = CMacrosResolverHelper.resolveItemNames(idBean, this.getSqlExecutor(),itemmap);
			Long newrepitemid = DBUtil.get_dbid(null, this.getSqlExecutor(), "i_inspection_report_items", "reportitemid", itemmap.size());

			for(Map item : itemmap){
				result=this.getSqlExecutor().executeInsertDeleteUpdate(
						"INSERT INTO `i_inspection_report_items` (tenantid, reportitemid, reportid, hostid, hostname,itemid,itemname, delay) VALUES (" +
							"#{tenantid}, #{reportitemid}, #{reportid}, #{hostid}, #{hostname}, #{itemid}, #{itemname}, #{delay} "+
						")", 
						map(
							"tenantid",      tenantid,
							"reportitemid",  newrepitemid++,
							"reportid",      reportid,
							"hostid",        hostid,
							"hostname",      hostname,
							"itemid",        Nest.value(item, "itemid").$(),
							"itemname",      Nest.value(item, "name_expanded").$(),
							"delay",         Nest.value(item, "delay").asLong() )
					);
					
					if(result == 0) {
						flag=false;
					}					
			
			}
			
 	/*		for(String enumName:items.valuesAsString()){	
				Map hitemid=reset(DataDriver.getItemId(this.getSqlExecutor(), hostid, ItemsKey.valueOf(enumName).getValue()));
				if(!empty(hitemid)){
					result=this.getSqlExecutor().executeInsertDeleteUpdate(
							"INSERT INTO `i_inspection_report_items` (tenantid, reportitemid, reportid, hostid, hostname,itemid, item_enum_name, delay) VALUES (" +
								"#{tenantid}, #{reportitemid}, #{reportid}, #{hostid}, #{hostname}, #{itemid}, #{itemEnumName}, #{delay} "+
							")", 
							map(
								"tenantid",      tenantid,
								"reportitemid",  newrepitemid++,
								"reportid",      reportid,
								"hostid",        hostid,
								"hostname",     hostname,
								"itemid",        Nest.value(hitemid, "itemid").$(),
								"itemEnumName",   enumName,
								"delay",           Nest.value(hitemid, "delay").asLong()
							)
						);
						
						if(result == 0) {
							flag=false;
						}					
				}else{//item为零处理,item保存为零，delay保存也为零
					result=this.getSqlExecutor().executeInsertDeleteUpdate(
							"INSERT INTO `i_inspection_report_items` (tenantid, reportitemid, reportid, hostid, hostname,itemid, item_enum_name, delay) VALUES (" +
								"#{tenantid}, #{reportitemid}, #{reportid}, #{hostid}, #{hostname}, #{itemid}, #{itemEnumName}, #{delay} "+
							")", 
							map(
								"tenantid",      tenantid,
								"reportitemid",  newrepitemid++,
								"reportid",      reportid,
								"hostid",        hostid,
								"hostname",     hostname,
								"itemid",        0,
								"itemEnumName",   enumName,
								"delay",           0L
							)
						);
						
						if(result == 0) {
							flag=false;
						}
				}	
				if(!flag) break;
			}
			if(!flag) break;*/
		}
		return flag;	
	}
		
	/**
	 * 删除巡检报告下关联的设备类型
	 * @param executor
	 * @param inspectionReportids
	 * @return
	 */
	public boolean deleteInspectionReportItems(SQLExecutor executor, CArray<Long> inspectionReportids) {
		executor.executeInsertDeleteUpdate(
			"delete from i_inspection_report_items where reportid IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
			map(
				"list", inspectionReportids.toList()
			)
		);
		return true;
	}
}
