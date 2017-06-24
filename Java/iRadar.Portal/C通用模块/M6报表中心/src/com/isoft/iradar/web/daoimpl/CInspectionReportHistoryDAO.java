package com.isoft.iradar.web.daoimpl;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_EXPIRED;
import static com.isoft.iradar.inc.Defines.UNKNOWN_VALUE;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.ItemsUtil.formatHistoryValue;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.daoimpl.common.UserDAO;
import com.isoft.biz.method.Role;
import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.virtualresource.VirtualResourceDao;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class CInspectionReportHistoryDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(VirtualResourceDao.class);
	
	private <T> T delegate(Delegator<T> d) {
		return delegate(Feature.defaultTenantId, d);
	}
	
	private <T> T delegate(String tenantId, Delegator<T> d) {
		try {
			if(RadarContext.getContext() == null) {
	    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
	    		RadarContext.setContext(ctx);
	    		
	    		Map userdata = EasyMap.build(
					"userid", 0L, 
					"type", Defines.USER_TYPE_SUPER_ADMIN
				);
	    		CWebUser.set(userdata);
	    	}
			
			Map uinfo = new HashMap();
			uinfo.put("tenantId", tenantId);
			uinfo.put("osTenantId", "0");
			uinfo.put("tenantRole", Role.LESSOR.magic());
			uinfo.put("userId", Feature.defaultUser);
			uinfo.put("userName", Feature.defaultUser);
			uinfo.put("admin", "Y");
			uinfo.put("osUser", null);
			
			IdentityBean idBean = new IdentityBean();
			idBean.init(uinfo);
	    	return CDelegator.doDelegate(idBean, d);
		} finally {
			RadarContext.releaseContext();
		}
	}
	
	
	/**
	 * 获取当前时间节点前  未执行的巡检报告
	 * 
	 */
	public CArray<Map>  noExcInspectionReportList(){
    	return delegate(new Delegator<CArray<Map>>() {
			public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				CArray<Map> newreports = new  CArray<Map>();
				Map params = new HashMap();
				params.put("time", time());//strtotime("now");
				CArray<Map> reports=DBUtil.DBselect(sqlE, 
						//" SELECT reportid FROM i_inspection_reports WHERE time<=#{time} AND `status`=0 AND executed=0 "
						" SELECT reportid ,batchnum,tenantid,name,username,groupid,time,active_till, status,timeperiod_type,every, month,dayofweek, day, start_time, period, start_date,batch_time "
						+ "FROM i_inspection_reports WHERE time<=#{time} and active_till>=#{time}  and status = 0  "
						, params);
				for(Map report:reports){
					long nexttime = getNextTime(report);
				    if((nexttime-59)<=time() && (nexttime+59)>=time()){
				    	long batchtime = Nest.value(report, "batch_time").asLong();
				    	 if(batchtime == 0){
				    		 newreports.add(report);
				    	 }else if((time()-batchtime)>=120){
				    		 newreports.add(report);
				    	 }
				    }
				}
				 return newreports;
			}
		});	
	}
	
	/**
	 * 测试用
	 * 
	 */
	/*public CArray<Map>  TestReportList(final String reportid){
    	return delegate(new Delegator<CArray<Map>>() {
			public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				Map params = new HashMap();
				params.put("reportid", reportid);
				CArray<Map> reports=DBUtil.DBselect(sqlE, 
						" SELECT reportid ,batchnum,tenantid,name,username,groupid,time,active_till, status,timeperiod_type,every, month,dayofweek, day, start_time, period, start_date "
						+ "FROM i_inspection_reports WHERE  reportid = #{reportid}  "
						, params);
				 return reports;
			}
		});	
	}*/
	
	private long getNextTime(Map inspectionReport){
		ReportUtil util = new ReportUtil(); 
		if (Nest.value(inspectionReport,"active_till").asLong() < time()) {
			Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_EXPIRED);
		} else if (Nest.value(inspectionReport,"time").asLong() > time()) {
			Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_APPROACH);
		} else {
			Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_ACTIVE);
		}
		long next_time=0l;
		try {
			next_time = util.get_nexttime_form(inspectionReport);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  next_time;
	}
	
	/**
	 * 查询巡检报告历史记录信息
	 */
	public CArray<Map> reportHistoryItems(final String reportid){
    	return delegate(new Delegator<CArray<Map>>() {
			public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map params=new HashMap();
				params.put("reportid", reportid);
				//巡检报告下的监控项信息
				CArray<Map> items=DBselect(executor, 
						" SELECT ri.tenantid, ri.hostid, ri.hostname, ri.itemid, ri.itemname, ri.delay,i.value_type, "+
						" i.units, i.valuemapid "+
						" FROM i_inspection_report_items ri "+
						" LEFT JOIN items i ON ri.itemid=i.itemid "+
						" WHERE reportid=#{reportid} "
						, params);
				
				
				CArray<Map> itemsdata=array();
				if(!empty(items)){	
					CArray<Map> rightItems=array();
					CArray<Map> errorItems=array();
					int valueType=-1;
					String sqlh="";
					for(Map item:items){
					//	sqlh = " SELECT h.clock FROM ";
						String tenantid = Nest.value(item, "tenantid").asString();
						long itemid = Nest.value(item, "itemid").asLong();
						CArray data=map(
								"tenantid",      tenantid,
								"reportid",      reportid,
								"hostid",        Nest.value(item, "hostid").$(),
								"hostname",      Nest.value(item, "hostname").$(),
								"itemid",        Nest.value(item, "itemid").$(),
								"itemname",      Nest.value(item, "itemname").$()
							);
						Long delay = Nest.value(item, "delay").asLong();
						if(itemid!=0){
							sqlh = " SELECT * FROM ";
							//封装数据
							if(delay <= 0){
								delay = 30L;
							}
							//history =Manager.History(idBean, executor).getLast(item, 1, Nest.value(item, "delay").asInteger());
							valueType=Nest.value(item, "value_type").asInteger();
							String table = CHistoryManager.getTableName(valueType);
							sqlh += table+ " h WHERE itemid= #{itemid}  AND h.tenantid = #{tenantid} ";
							sqlh += " AND h.clock>" + (Cphp.time()-delay);
							sqlh += " ORDER BY clock DESC LIMIT 0,1 ";
							
							params=new HashMap();
							params.put("itemid", Nest.value(item, "itemid").$());
							params.put("tenantid", tenantid);
							
							Map his=DBfetch(DBselect(executor, sqlh, params));
							//获取最新history的value值
							String lastValue = null;
							if (his!=null) {
								lastValue  = formatHistoryValue(idBean, executor, Nest.value(his,"value").asString(), item, false);
							} else {
								lastValue = UNKNOWN_VALUE;
							}
							Nest.value(data, "value").$(lastValue);
							
							if(!empty(his)){
								//历史数据前后15秒内的事件
								//params.put("starttime", Nest.value(his, "clock").asInteger()-15);
								//params.put("endtime", Nest.value(his, "clock").asInteger()+15);
								Map paramst=new HashMap();
								paramst.put("itemid", Nest.value(item, "itemid").$());
								paramst.put("starttime", Cphp.time()+delay);
								paramst.put("endtime", Cphp.time()-delay);
								
								Map event = DBfetch(DBselect(executor, 
										" SELECT e.value "+
										" FROM `events` e "+
										" LEFT JOIN `triggers` t ON e.objectid=t.triggerid "+
										" LEFT JOIN functions f ON f.triggerid=t.triggerid "+
										" WHERE e.clock BETWEEN #{starttime} AND #{endtime} "+
										" AND f.itemid=#{itemid} "+
										" ORDER BY e.clock DESC "+
										" LIMIT 0,1 ", 
									paramst));
								if(!empty(event)){
									if(Nest.value(event, "value").asInteger()==0){ //ok
										Nest.value(data, "isproblem").$(IMonConsts.INSPECTION_ITEM_HISTORY_NORMAL);
										rightItems.add(data);
									}else{//problem
										Nest.value(data, "isproblem").$(IMonConsts.INSPECTION_ITEM_HISTORY_ABNORMAL);
										errorItems.add(data);
									}
								}else{//空事件认为是正常
									Nest.value(data, "isproblem").$(IMonConsts.INSPECTION_ITEM_HISTORY_NORMAL);
									rightItems.add(data);
								}
							}else{//空历史认为是未统计
								Nest.value(data, "isproblem").$(IMonConsts.INSPECTION_ITEM_HISTORY_OTHER);
								rightItems.add(data);							
							}
						
						}else{
							Nest.value(data, "value").$(UNKNOWN_VALUE);
							Nest.value(data, "isproblem").$(IMonConsts.INSPECTION_ITEM_HISTORY_OTHER);
							rightItems.add(data);
						}
					 }
					itemsdata=rda_array_merge(rightItems, errorItems);
				}
				
				return itemsdata;
			}
		});			
	}
	
	
	/**
	 * 添加巡检历史记录
	 */
    public Boolean addInspectionRepHis(final Long reportid, final int batchnum,final CArray<Map> hiss) {
    	return delegate(new Delegator<Boolean>() {
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Long newrhid = DBUtil.get_dbid(null, executor, "i_inspection_report_historys", "report_historyid", hiss.size());
				boolean flag=true;
				int result=1;
				if(!empty(hiss)){
					/*executor.executeInsertDeleteUpdate(
							" delete from i_inspection_report_historys where reportid=#{reportid} ", 
							map(
								"reportid", reportid
							)
						);*/
					
					for(Map his:hiss){
						result=executor.executeInsertDeleteUpdate(
								"INSERT INTO `i_inspection_report_historys` (tenantid, report_historyid, reportid, batchnum,hostid, hostname, itemid, itemname, value, isproblem) VALUES (" +
									"#{tenantid}, #{reporthistoryid}, #{reportid}, #{batchnum}, #{hostid}, #{hostname},#{itemid}, #{itemname}, #{value}, #{isproblem}"+
								")", 
								map(
									"tenantid",         Nest.value(his, "tenantid").$(),
									"reporthistoryid",  newrhid++,
									"reportid",         Nest.value(his, "reportid").$(),
									"batchnum",         batchnum,
									"hostid",           Nest.value(his, "hostid").$(),
									"hostname",         Nest.value(his, "hostname").$(),
									"itemid",           Nest.value(his, "itemid").$(),
									"itemname",         Nest.value(his, "itemname").$(),
									"value",            Nest.value(his, "value").$(),
									"isproblem",        Nest.value(his, "isproblem").$()
								)
							);
							
							if(result == 0) {
								flag=false;
							}
					}
				}	
				return flag;
			}
		});
    }
	
    /**    发送巡检报告方法
     * @param report  巡检主表
     * @param hiss    巡检明细
     * @return
     */
    public Boolean sendInspectionReport(final Map report, final CArray<Map> hiss){
    	return delegate(new Delegator<Boolean>() {
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Long alertsid = DBUtil.get_dbid(idBean, executor, "alerts", "alertid", 1);
				int result=1;
				boolean flag=true;
				CHostGroupGet options =new CHostGroupGet();
				options.setGroupIds(Nest.value(report, "groupid").asLong());
				options.setOutput(new String[]{"name"});
				Map groups = reset((CArray<Map>)API.HostGroup(idBean, executor).get(options));
				
				StringBuffer buf=new StringBuffer(); 
				buf.append("<div><table cellpadding='3' cellspacing='1'>");
				buf.append("<tr><td colspan='4'>巡检报告</td></tr>");
				buf.append("<tr><td>巡检人</td><td>"+Nest.value(report, "username").asString()+"</td><td>巡检时间</td><td>"+ rda_date2str(_("d M Y H:i:s"),Cphp.time())+"</td></tr>");
				buf.append("<tr><td>巡检任务</td><td>"+Nest.value(report, "name").asString()+"</td><td>巡检类型</td><td>"+Nest.value(groups, "name").asString()+"</td></tr>");
				
				//获取巡检结果
				int abnormalNum = 0;//异常
				int otherNum  = 0;//未统计
				StringBuffer databuf=new StringBuffer(); 
				databuf.append("<table><tr><td>巡检设备名</td><td>监控指标</td><td>监控值</td><td>巡检结果</td></tr>");
				for(Map his : hiss){
					 int ispro = Nest.value(his, "isproblem").asInteger();
					 databuf.append("<tr><td>"+Nest.value(his, "hostname").$()+"</td><td>"+Nest.value(his, "itemname").$()+"</td><td>"+ Nest.value(his, "value").$()+"</td><td>"+ReportUtil.getNormalZH(Nest.value(his, "isproblem").asInteger())+"</td></tr>");
					 if(1 == ispro){//异常
						 abnormalNum++;
					 }else if(2 == ispro){//未统计
						 otherNum++;
					 }
				}
				databuf.append("</table>");
				buf.append("<tr><td>巡检结果</td>"+
		                "<br/>指标：正常"+(hiss.size()-otherNum-abnormalNum)+"个,异常"+abnormalNum+"个,未统计"+otherNum+"个,共"+hiss.size()+"个</td></tr></table>");
				buf.append(databuf);
				buf.append("</div>");
				String mail="";//默认邮箱
				String userId = idBean.getUserId();
				/*IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
				Users s = $.getIdentityClient().users().list().execute();
				for(User u: s) {
					if(u.getId() ==  idBean.getUserId()){
						mail = u.getEmail();
					}
				}*/
				if(userId == null || userId.equals("")){
					userId="1";//默认为管理员
				}else if(userId.equals("admin")){
					userId="1";//管理员id转换为1
				}
				CArray<Map> userIds = new CArray<Map>();
				userIds.put("userid", userId);
				userIds.put("tenantid", Nest.value(report, "tenantid").$());
				UserDAO userDao = new UserDAO(executor);
				List<Map> list = userDao.doGetMediaByUserId(userIds);
				for (int i = 0; i < list.size(); i++) {
					mail = Nest.value(list.get(i), "sendto").asString();
					result = executor.executeInsertDeleteUpdate(
							"INSERT INTO `alerts` (tenantid, alertid, actionid, eventid, userid, clock, mediatypeid, sendto"
							+ " ,subject, message,status,retries,error,esc_step,alerttype) VALUES (" +
								"#{tenantid}, #{alertid}, #{actionid}, #{eventid}, #{userid},UNIX_TIMESTAMP(), #{mediatypeid}, #{sendto}"
								+ ",#{subject}, #{message}, #{status}, #{retries}, #{error}, #{esc_step},#{alerttype} "+
							")", 
							map(
								"tenantid",         Nest.value(report, "tenantid").$(),
								"alertid",          alertsid++,
								"actionid",         1,
								"eventid",          1,
								"userid",           "1",
								//"clock",            Nest.value(his, "itemid").$(),
								"mediatypeid",      1,
								"sendto",           mail,//
								"subject",          rda_date2str(_("d M Y H:i:s"),Cphp.time())+" "+Nest.value(report, "name").$()+"  "+"巡检报告",
								"message",          buf.toString(),
								"status",           0,
								"retries",          0,
								"error",            "",
								"esc_step",         1,
								"alerttype",        0
							)
						);
					if(result == 0) {
						flag=false;
						break;
					}
				}
				return flag;
			}
		});	
    }
    /**
     * 修改巡检报表为已执行
     */
	
	/*public Boolean  updateExecuted(final CArray<Long> reportids){
    	return delegate(new Delegator<Boolean>() {
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				return DBUtil.DBexecute(sqlE, 
						" UPDATE i_inspection_reports SET executed=1 ", 
						map(
								"list", reportids.toList()
							));
			}
		});	
	}*/ 
    
    /** 修改下次巡检时间
     * @param reportids
     * @return
     */
  /*  public Boolean  updateNexttime(final CArray<Map> inspectionReport){
    	return delegate(new Delegator<Boolean>() {
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				ReportUtil util = new ReportUtil(); 
				if (Nest.value(inspectionReport,"active_till").asLong() < time()) {
					Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_EXPIRED);
				} else if (Nest.value(inspectionReport,"time").asLong() > time()) {
					Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_APPROACH);
				} else {
					Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_ACTIVE);
				}
				long next_time=0l;
				try {
					next_time = util.get_nexttime_form(inspectionReport);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return DBUtil.DBexecute(sqlE, 
						" UPDATE i_inspection_reports SET next_time=#{nexttime} where reportid=#{reportid} and tenantid=#{tenantid} ", 
						map(
								"nexttime",next_time,
								"tenantid",idBean.getTenantId(),
								"reportid",Nest.value(inspectionReport, "reportid").asLong()
							));
			}
		});	
	}*/
	
	public CArray<Map> doGetProxyHostByVm(final Map param,final String sql){
		return delegate(new Delegator<CArray<Map>>() {
			public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				return DBUtil.DBselect(sqlE,sql, param);
			}
		});	
	} 
	
	public Boolean  updateReprotBatchNum(final Map report,final int batchnum){
    	return delegate(new Delegator<Boolean>() {
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				int result=1;
				boolean flag=true; 
				 result = executor.executeInsertDeleteUpdate(
						   " update i_inspection_reports set batchnum=#{batchnum} ,batch_time=#{batch_time} where reportid=#{reportid} and tenantid=#{tenantid} "
						, 
						map(
						    "batchnum",         batchnum,
						    "batch_time",       Cphp.time(),
							"reportid",         Nest.value(report, "reportid").$(),
							"tenantid",         Nest.value(report, "tenantid").$()
						)
					);
				 Long reportbatchid = DBUtil.get_dbid(idBean, executor, "i_inspection_report_batch", "reportbatchid", 1);   
				   result = executor.executeInsertDeleteUpdate(
							"INSERT INTO `i_inspection_report_batch` (tenantid, reportbatchid, reportid, batchnum, batch_time) VALUES (" +
								"#{tenantid}, #{reportbatchid}, #{reportid}, #{batchnum}, #{batch_time})", 
							map(
								"tenantid",         Nest.value(report, "tenantid").$(),
								"reportbatchid",    reportbatchid,
								"reportid",         Nest.value(report, "reportid").$(),
								"batchnum",         batchnum,
								"batch_time",       Cphp.time()
							)
						);
				   if(result == 0) {
						flag=false;
					}
				 return false;
				
			  }
			});
	}
	
	
}
