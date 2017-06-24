package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.bindec;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.maxTill;
import static com.isoft.iradar.inc.ValidateUtil.validateDateInterval;
import static com.isoft.iradar.inc.ValidateUtil.validateDateTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.web.daoimpl.CInspectionReportDAO;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class InspectionReportAction extends RadarBaseAction {
	
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;

	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Inspection_export")+time+".csv");
		}else{
			page("title", _("inspection report"));
			page("file", "inspectionReport.action");
			page("hist_arg", new String[] {});
			page("scripts", new String[] { "class.calendar.js" });
			page("css", new String[] { "lessor/reportcenter/report_performance.css","lessor/reportcenter/inspectionreport.css" });
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		
		CArray fields = map(
			"inspectionReportid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"batchnum",       	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"name",			    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("inspection report name")),  
			"time_day" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"time_month" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"time_year" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"time_hour" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"time_minute" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till_day" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till_month" ,array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till_year" ,array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till_hour" ,array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till_minute",array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"new_timeperiod_start_date_day" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_month" ,	    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_year" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_hour" ,		array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod_start_date_minute" ,	array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null),
			"new_timeperiod" ,	array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"timeperiods" ,		array(T_RDA_STR, O_OPT, null,	null,		null),
			//"status",			array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
			"executed",			array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	"isset({save})"),

			"inspectionReports",array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null), //列表多选巡检报告
			"groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null), //选中的设备类型
			//"hosts",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("inspection report hosts")), //选中的设备
			"hosts",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			//"items",            array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("inspection report items")), //选中的监控指标
			"csv_export",	    array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// actions
			"go",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"contenttwo",		array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"reportcontent",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			
			// other
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",	    array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));		
	}

	@Override
	protected void doPermissions(SQLExecutor arg0) {

	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}
	
	@Override
	protected void doAction(final SQLExecutor executor) {
		/* Actions */
		boolean result = true;	
		
		if (isset(_REQUEST,"save")) {
			DBstart(executor);

			//时间输入是否正确
			if (!validateDateTime(Nest.value(_REQUEST,"time_year").asInteger(),
					Nest.value(_REQUEST,"time_month").asInteger(),
					Nest.value(_REQUEST,"time_day").asInteger(),
					Nest.value(_REQUEST,"time_hour").asInteger(),
					Nest.value(_REQUEST,"time_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("inspection report time")));
				result = false;
			}
			if (!validateDateInterval(Nest.value(_REQUEST,"time_year").asInteger(),
					Nest.value(_REQUEST,"time_month").asInteger(),
					Nest.value(_REQUEST,"time_day").asInteger())) {
				info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("inspection report time")));
				result = false;
			}
			
			//巡检结束时间
			if (!validateDateTime(Nest.value(_REQUEST,"active_till_year").asInteger(),
					Nest.value(_REQUEST,"active_till_month").asInteger(),
					Nest.value(_REQUEST,"active_till_day").asInteger(),
					Nest.value(_REQUEST,"active_till_hour").asInteger(),
					Nest.value(_REQUEST,"active_till_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("inspection report time")));
				result = false;
			}
			if (!validateDateInterval(Nest.value(_REQUEST,"active_till_year").asInteger(),
					Nest.value(_REQUEST,"active_till_month").asInteger(),
					Nest.value(_REQUEST,"active_till_day").asInteger())) {
				info(_s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("inspection report time")));
				result = false;
			}
			
			Long timeNow = Cphp.time();
			//巡检开始时间
			Long time = mktime(Nest.value(_REQUEST,"time_hour").asInteger(), 
								   Nest.value(_REQUEST,"time_minute").asInteger(), 
								   0, 
								   Nest.value(_REQUEST,"time_month").asInteger(), 
								   Nest.value(_REQUEST,"time_day").asInteger(), 
								   Nest.value(_REQUEST,"time_year").asInteger()
							   );
			//巡检结束时间
			Long active_till_time = mktime(Nest.value(_REQUEST,"active_till_hour").asInteger(), 
					   Nest.value(_REQUEST,"active_till_minute").asInteger(), 
					   0, 
					   Nest.value(_REQUEST,"active_till_month").asInteger(), 
					   Nest.value(_REQUEST,"active_till_day").asInteger(), 
					   Nest.value(_REQUEST,"active_till_year").asInteger()
				   );
			int res=timeNow.compareTo(time);
			if(res>0){
				 info(_s("巡检时间不能小于当前时间"));
				 result = false;
			}
			if (time > maxTill) {
				info(_s("巡检时间必须在2010.01.01 到 2038.01.01之间"));
				result = false;
			}
			if(time  >= active_till_time){
				info(_s("巡检开始时间必须小于巡检结束时间"));
				result = false;
			}
			//一次性时 巡检执行具体时间
			long  perform_time =  0l;
			Map new_timeperiod = Nest.value(_REQUEST,"new_timeperiod").asCArray();
			if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME) {
				perform_time = mktime(Nest.value(_REQUEST,"new_timeperiod_start_date_hour").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"new_timeperiod_start_date_month").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_day").asInteger(),
						Nest.value(_REQUEST,"new_timeperiod_start_date_year").asInteger());
				if(perform_time < time || perform_time >active_till_time){
					info(_s("执行日期必须在巡检开始时间与巡检结束时间之间"));
					result = false;
				}
				
			}
			
			if(empty(get_request("hosts", array()))){//验证巡检设备是否为空
				info(_s("巡检设备不能为空"));
				result = false;
			} 
			
			 
			 
			if(result){
				//result = false;
				//封装数据
				final Map inspectionReport = map(
						"tenantid", getIdentityBean().getTenantId(),
						"name",     Nest.value(_REQUEST, "name").$(),
						"username", getIdentityBean().getUserName(),
						"time",     time,
						"active_till",     active_till_time,
						"groupid",  Nest.value(_REQUEST, "groupid").$(),					
						"hosts",    get_request("hosts", array()),
						//"items",    get_request("items", array()),
						"status",   0,
						"executed", get_request("executed", 0)
						
					);
				/******************************************* */
				Nest.value(inspectionReport,"timeperiod_type").$(Nest.value(new_timeperiod,"timeperiod_type").asInteger());
				Nest.value(inspectionReport,"start_date").$(perform_time);
		
				// start time
				Nest.value(inspectionReport,"start_time").$((Nest.value(new_timeperiod,"hour").asInteger() * SEC_PER_HOUR) + (Nest.value(new_timeperiod,"minute").asInteger() * SEC_PER_MIN));
		
				// period
				//Nest.value(inspectionReport,"period").$( (Nest.value(new_timeperiod,"period_days").asInteger() * SEC_PER_DAY) + (Nest.value(new_timeperiod,"period_hours").asInteger() * SEC_PER_HOUR) + (Nest.value(new_timeperiod,"period_minutes").asInteger() * SEC_PER_MIN));
				Nest.value(inspectionReport,"period").$(3600);
				// days of week
				if (!isset(new_timeperiod,"dayofweek")) {
					String dayofweek =  (!isset(new_timeperiod,"dayofweek_su")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_sa")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_fr")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_th")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_we")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_tu")) ? "0" : "1";
					dayofweek += (!isset(new_timeperiod,"dayofweek_mo")) ? "0" : "1";
					Nest.value(inspectionReport,"dayofweek").$(bindec(dayofweek));
				}else{
					Nest.value(inspectionReport,"dayofweek").$(0);
				}
		
				// months
				if (!isset(new_timeperiod,"month")) {
					String month =  (!isset(new_timeperiod,"month_dec")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_nov")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_oct")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_sep")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_aug")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_jul")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_jun")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_may")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_apr")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_mar")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_feb")) ? "0" : "1";
					month += (!isset(new_timeperiod,"month_jan")) ? "0" : "1";
					Nest.value(inspectionReport,"month").$(bindec(month));
				}else{
					Nest.value(inspectionReport,"month").$(0);
				}
				
				/******************************************* */
				//巡检周期参数检查
				if (Nest.value(new_timeperiod,"hour").asInteger() > 23 || Nest.value(new_timeperiod,"minute").asInteger() > 59) {//小时 分钟设置
					info(_("Incorrect inspection period"));
					result = false;
				}  else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY && Nest.value(new_timeperiod,"every").asInteger() < 1) {
					info(_("Incorrect inspection day period"));
					result = false;
				} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
					if (Nest.value(new_timeperiod,"every").asInteger() < 1) {
						info(_("Incorrect inspection week period"));
						result = false;
					} else if (Nest.value(inspectionReport,"dayofweek").asInteger() < 1) {//没有选择周几
						info(_("Incorrect inspection days of week"));
						result = false;
					} 
				} else if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
					if (Nest.value(inspectionReport,"month").asInteger() < 1) {
						info(_("Incorrect inspection month period"));
						result = false;
					} else if (Nest.value(new_timeperiod,"day").asInteger() == 0 && Nest.value(inspectionReport,"dayofweek").asInteger() < 1) {//没有选择周几
						info(_("Incorrect inspection days of week"));
						result = false;
					} else if ((Nest.value(new_timeperiod,"day").asInteger() < 1 || Nest.value(new_timeperiod,"day").asInteger() > 31) && Nest.value(inspectionReport,"dayofweek").asInteger() == 0) {//月中某日输入错误日期
						info(_("Incorrect inspection date"));
						result = false;
					}
				} 
				
				if (Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
					if (Nest.value(new_timeperiod,"month_date_type").asInteger() > 0) {
						Nest.value(inspectionReport,"day").$(0);
						Nest.value(inspectionReport,"every").$(Nest.value(new_timeperiod,"every").asInteger());
					} else {
						Nest.value(inspectionReport,"every").$(0);
						Nest.value(inspectionReport,"dayofweek").$(0);
						Nest.value(inspectionReport,"day").$(Nest.value(new_timeperiod,"day").asInteger());
					}
				}
				if(Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME) {
					Nest.value(inspectionReport,"every").$(0);
					Nest.value(inspectionReport,"day").$(Nest.value(new_timeperiod,"day").asInteger());
					Nest.value(inspectionReport,"dayofweek").$(0);
				}
				if(Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY) {
					Nest.value(inspectionReport,"every").$(Nest.value(new_timeperiod,"every").asInteger());
					Nest.value(inspectionReport,"day").$(Nest.value(new_timeperiod,"day").asInteger());
					Nest.value(inspectionReport,"dayofweek").$(0);
				}
				if(Nest.value(new_timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
					Nest.value(inspectionReport,"every").$(Nest.value(new_timeperiod,"every").asInteger());
					Nest.value(inspectionReport,"day").$(Nest.value(new_timeperiod,"day").asInteger());
				}
				
				if(result){
					if(isset(_REQUEST, "inspectionReportid")){//修改
						Nest.value(inspectionReport, "reportid").$(Nest.value(_REQUEST, "inspectionReportid").$());
						
						result = Call(new Wrapper<Boolean>() {
							@Override
							protected Boolean doCall() throws Throwable {
								return new CInspectionReportDAO(executor).update(getIdentityBean(),array(inspectionReport));
							}
						});						
					}else{//新增
						result = Call(new Wrapper<Boolean>() {
							@Override
							protected Boolean doCall() throws Throwable {
								return new CInspectionReportDAO(executor).create(getIdentityBean(),array(inspectionReport));
							}
						});
						
					}
					
					result = DBend(executor, result);
				}

					
			}
			
			if(isset(_REQUEST, "inspectionReportid")){
				show_messages(result, _("inspection report updated"), _("Cannot update inspection report"));	
			}else{
				show_messages(result, _("inspection report added"), _("Cannot add inspection report"));				
			}


			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result);
			}
			unset(_REQUEST,"save");
			
		}else if(isset(_REQUEST,"delete")){//单删除
			DBstart(executor);
			
			Long dinspectionReportid = Nest.value(_REQUEST,"inspectionReportid").asLong();	
			new CInspectionReportDAO(executor).deleteInspectionReports(executor, array(dinspectionReportid));
			
			result = DBend(executor, true);
			
			show_messages(result, _("inspection report deleted"), _("Cannot deleted inspection report"));

			unset(_REQUEST,"form");
			clearCookies(result);
			unset(_REQUEST,"delete");	
			
		}else if(isset(_REQUEST, "go") && "delete".equals(Nest.value(_REQUEST,"go").asString())){//批量删除
			DBstart(executor);
			
			Long[] dinspectionReportids = get_request("inspectionReports", array()).valuesAsLong();
			new CInspectionReportDAO(executor).deleteInspectionReports(executor, CArray.valueOf(dinspectionReportids));
			
			result = DBend(executor, true);
			
			show_messages(result, _("inspection report deleted"), _("Cannot deleted inspection report"));

			clearCookies(result);
		}else if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean enable =("activate".equals(get_request("go")));
			int status = enable ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED;
			boolean results = true;   
			DBstart(executor);
				
			Long[] dinspectionReportids = get_request("inspectionReports", array()).valuesAsLong();
			int updatenum = new CInspectionReportDAO(executor).updateInspectionReportsStatus(getIdentityBean(), executor,CArray.valueOf(dinspectionReportids),status);
			if(updatenum==0){
				results=false;
			}
			DBend(executor, true);
			String messageSuccess = enable
				? _n("巡检已启用", "巡检已启用", updatenum)
				: _n("巡检已停用", "巡检已停用", updatenum);
			String messageFailed = enable
				? _n("巡检启用失败", "巡检启用失败", updatenum)
				: _n("巡检停用失败", "巡检停用失败", updatenum);
			show_messages(results, messageSuccess, messageFailed);
			clearCookies(result);
      }
		
		/* Display */
		if (isset(_REQUEST,"form")) {
			if("historyRecords".equals(Nest.value(_REQUEST, "form").$())){//查看历史记录
				String tenantid = getIdentityBean().getTenantId();
				Map data = map();
				Map params=new HashMap();
				params.put("reportid", Nest.value(_REQUEST, "inspectionReportid").$());
				params.put("tenantid", tenantid);
				/*CArray<Map> reporthistorys=DBselect(executor, 
						" SELECT h.host, rh.item_enum_name, rh.isproblem "+
						" FROM i_inspection_report_historys rh "+
						" LEFT JOIN `hosts` h ON rh.hostid=h.hostid "+
						" WHERE reportid=#{reportid} ORDER BY isproblem DESC "
						, params);*/
				Map report =DBfetch(DBselect(executor, 
						" SELECT r.name, r.username,r.active_till, r.groupid "+
						" FROM i_inspection_reports r "+
						" WHERE reportid=#{reportid} AND tenantid = #{tenantid} ORDER BY reportid  "
						, params));
				Nest.value(data, "name").$(Nest.value(report, "name").asString());
				Nest.value(data, "username").$(Nest.value(report, "username").asString());
				Nest.value(data, "active_till").$(Nest.value(report, "active_till").asLong());
				
				params.put("batchnum", Nest.value(_REQUEST, "batchnum").$());
				Map batchmap = DBfetch((DBselect(executor,
						" SELECT b.batchnum, b.batch_time " +
						" FROM i_inspection_report_batch b " +
						" WHERE reportid=#{reportid} AND batchnum=#{batchnum} AND tenantid=#{tenantid}"
					, params)));
				Nest.value(data, "time").$(Nest.value(batchmap, "batch_time").asLong());
				
				CArray<Map> reporthistorys=DBselect(executor, 
						" SELECT rh.hostname, rh.itemname , rh.value, rh.isproblem "+
						" FROM i_inspection_report_historys rh "+
						" WHERE reportid=#{reportid}  AND batchnum=#{batchnum} AND tenantid = #{tenantid} ORDER BY  report_historyid DESC "
						, params);//改用此方法，保留巡检之前删除设备
				Nest.value(data, "reporthistorys").$(reporthistorys);
				Nest.value(data, "tenantid").$(tenantid);
				Nest.value(data, "inspectionReportid").$(Nest.value(_REQUEST, "inspectionReportid").asString());
				Nest.value(data, "batchnum").$(Nest.value(_REQUEST, "batchnum").asString());
				
				CHostGroupGet options = new CHostGroupGet();
				options.setEditable(true);
				options.setFilter("internal", 0);
				options.setOnlyHostGroup(true);
				options.setRealHosts(true);//类型下无设备，则不显示
				options.setOutput(new String[]{"groupid","name"});
				CArray<Map> all_groups = API.HostGroup(getIdentityBean(), executor).get(options);
				
				for(Map group:all_groups){
					if(Nest.value(group, "groupid").asLong()==Nest.value(report, "groupid").asLong()){
						Nest.value(data, "groupname").$(Nest.value(group, "name").asString());
						break;
					}
				}

				CArray<Map> statisticals = DBselect(executor, 
						" SELECT hostid,COUNT(itemid) itemnum,SUM(CASE WHEN isproblem = 0 THEN 1 ELSE 0 END) normalnum, "+
						" SUM(CASE WHEN isproblem = 2 THEN 1 ELSE 0 END) othernum  "+
						" FROM i_inspection_report_historys h "+
						" WHERE reportid=#{reportid}  AND batchnum=#{batchnum} AND tenantid = #{tenantid} "+
						" GROUP BY hostid ORDER BY  hostid DESC "
						, params);
			
				Nest.value(data, "statisticals").$(statisticals);//巡检报告内容，设备及监控指标数量
				
				//求巡检结果
				int hosttotalNum = statisticals.size();
				int itemtotalNum = reporthistorys.size();
				int j = 0;
				int i = 0;
				int itemothertotalNum=0;
				int itemnormaltotalNum=0;
				for(Map statistical : statisticals){
					int itemNum = Nest.value(statistical, "itemnum").asInteger();
					int normalNum = Nest.value(statistical, "normalnum").asInteger();
					int otherNum = Nest.value(statistical, "othernum").asInteger();
					if(itemNum == normalNum){
						j++;
					}
					if(itemNum == otherNum){
						i++;
					}
					itemothertotalNum += otherNum;
					itemnormaltotalNum += normalNum;
				}
				String reportcontent = "设备：正常"+j+"个,异常"+(hosttotalNum-i-j)+"个,未统计"+i+"个,共"+hosttotalNum+"个 ;\r\n";
				String contenttwo = "指标：正常"+itemnormaltotalNum+"个,异常"+(itemtotalNum-itemnormaltotalNum-itemothertotalNum)+"个,未统计"+itemothertotalNum+"个,共"+itemtotalNum+"个";
				Nest.value(data, "reportcontent").$(reportcontent);
				Nest.value(data, "contenttwo").$(contenttwo);
				
				if (CSV_EXPORT) {
					csvRows.add(array("巡检报告"));
					csvRows.add(array("巡检人",Nest.value(report, "username").asString(),"巡检时间",rda_date2str(_("d M Y H:i:s"), Nest.value(batchmap,"batch_time").asLong())));
					csvRows.add(array("巡检任务",Nest.value(report, "name").asString(),"巡检类型",Nest.value(data, "groupname").asString()));
					csvRows.add(array("巡检结果",reportcontent+contenttwo));
				}
				
				if (CSV_EXPORT) {
					csvRows.add(array(
							"巡检设备名",
							"监控指标",
							_("MonitoringValue"),
							"巡检结果"));
					for(Map item : reporthistorys) {
						csvRows.add(array(
								Nest.value(item, "hostname").$(),
								Nest.value(item, "itemname").$(),
								"\r"+Nest.value(item, "value").$(),
								ReportUtil.getNormalZH(Nest.value(item, "isproblem").asInteger())
							));
					}
				}
				
				if (CSV_EXPORT) {
					print(rda_toCSV(csvRows));
					return;
				}
				
				CView hostgroupView = new CView("configuration.inspectionReportHistorys.list", data);
				hostgroupView.render(getIdentityBean(), executor);
				hostgroupView.show();
								
			}else{//修改 新增 公用操作
				CArray data = map(
						"form",     get_request("form"),
						"inspectionReportid", get_request("inspectionReportid", null),
						"batchnum", get_request("batchnum", null),
						"name",     get_request("name", ""),
						"time", 	get_request("time", strtotime("tomorrow")),
						"active_till", 	get_request("active_till", strtotime("tomorrow")),
						"groupid",  get_request("groupid", -1),
						"status",   get_request("status", 0),
						"executed", get_request("executed", 0)
					);

				//所有设备类型
				CHostGroupGet options = new CHostGroupGet();
				options.setEditable(true);
				options.setFilter("internal", 0);
				options.setOnlyHostGroup(true);
				options.setRealHosts(true);//类型下无设备，则不显示
				options.setOutput(new String[]{"groupid","name"});
				CArray<Map> all_groups = API.HostGroup(getIdentityBean(), executor).get(options);
				order_result(all_groups, "name");	
				
				Nest.value(data, "db_groups").$(all_groups);
						
				if(!isset(_REQUEST, "form_refresh")){
					if(isset(_REQUEST, "inspectionReportid")){ //修改
						Nest.value(data, "inspectionReportid").$(Nest.value(_REQUEST, "inspectionReportid").$());
						Nest.value(data, "batchnum").$(Nest.value(_REQUEST, "batchnum").$());
						String tenantid = getIdentityBean().getTenantId(); 
						
						Map paraMap = new HashMap();
						paraMap.put("reportid", Nest.value(data, "inspectionReportid").$());
						paraMap.put("batchnum", Nest.value(data, "batchnum").$());
						paraMap.put("tenantid", tenantid);
						
						Map dbdata = DBfetch(DBselect(executor, 
								" SELECT  b.name, b.time, b.active_till,b.groupid, b.status, b.executed "
								+ ",b.timeperiod_type, b.every, b.month,b.dayofweek, b.day, b.start_time, b.period, b.start_date "
								+ " FROM i_inspection_reports b WHERE b.reportid=#{reportid} and b.tenantid=#{tenantid} "
								, paraMap));
						
						Nest.value(data, "name").$(Nest.value(dbdata, "name").$());
						Nest.value(data, "time").$(Nest.value(dbdata, "time").$());
						Nest.value(data, "active_till").$(Nest.value(dbdata, "active_till").$());
						Nest.value(data, "groupid").$(Nest.value(dbdata, "groupid").$());
						
						Map  new_timeperiod=map(
								"timeperiod_type",Nest.value(dbdata, "timeperiod_type").asInteger(),
								"every",Nest.value(dbdata, "every").asInteger(),
								"month",Nest.value(dbdata, "month").asInteger(),
								"dayofweek",Nest.value(dbdata, "dayofweek").asInteger(),
								"day",Nest.value(dbdata, "day").asInteger(),
								"start_time",Nest.value(dbdata, "start_time").asInteger(),
								"period",Nest.value(dbdata, "period").asInteger(),
								"start_date",Nest.value(dbdata, "start_date").asInteger()
								);
						//Nest.value(data, "new_timeperiod").$(new_timeperiod);
						
						Nest.value(_REQUEST,"new_timeperiod").$(new_timeperiod);
						Nest.value(_REQUEST,"new_timeperiod","id").$(Nest.value(data, "inspectionReportid").$());
						Nest.value(_REQUEST,"new_timeperiod","start_date").$(Nest.value(dbdata, "start_date").asInteger());
						
						Nest.value(data, "status").$(Nest.value(dbdata, "status").$());
						Nest.value(data, "executed").$(Nest.value(dbdata, "executed").$());
						
					}else{//新增
						//默认选中设备类型
						Map defaultGroup = reset(Nest.value(data, "db_groups").$s());
						Nest.value(data, "groupid").$(Nest.value(defaultGroup,"groupid").asLong());
						Nest.value(data, "hosts").$(get_request("hosts",array()));
					}
				}else{//切换设备类型
                    Long time = mktime(Nest.value(_REQUEST,"time_hour").asInteger(), //添加设置的巡检时间
								   Nest.value(_REQUEST,"time_minute").asInteger(), 
								   0, 
								   Nest.value(_REQUEST,"time_month").asInteger(), 
								   Nest.value(_REQUEST,"time_day").asInteger(), 
								   Nest.value(_REQUEST,"time_year").asInteger()
							   );
                    Long active_till = mktime(Nest.value(_REQUEST,"active_till_hour").asInteger(), //添加设置的巡检时间
							   Nest.value(_REQUEST,"active_till_minute").asInteger(), 
							   0, 
							   Nest.value(_REQUEST,"active_till_month").asInteger(), 
							   Nest.value(_REQUEST,"active_till_day").asInteger(), 
							   Nest.value(_REQUEST,"active_till_year").asInteger()
						   );
					Nest.value(data, "time").$(time);
					Nest.value(data, "active_till").$(active_till);
					Nest.value(data, "hosts").$(get_request("hosts",array()));
					
				}
				
				// render view
				CView hostgroupView = new CView("configuration.inspectionReports.edit", data);	
				hostgroupView.render(getIdentityBean(), executor);
				hostgroupView.show();
			}			
		}else{ //列表
			CArray data = map();
			
			Map params = new HashMap();
			params.put("tenantid", getIdentityBean().getTenantId());
			
			CArray<Map> tempInspectionReports = DBselect(executor,
					" SELECT b.reportid,b.batchnum, b.name, b.time,b.active_till, b.status " +
				    " ,b.timeperiod_type, b.every, b.month,b.dayofweek, b.day, b.start_time, b.period, b.start_date ,b.create_time "+
					" FROM i_inspection_reports b " +
					" WHERE tenantid=#{tenantid} "+
					" ORDER BY reportid DESC " +
					" LIMIT 0, 1001 "
				, params);
			
			//CArray<Map> tempInspectionReports=(CArray<Map>) inspectionReports.clone();
			
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, tempInspectionReports, array("reportid")));
			Nest.value(data,"inspectionReports").$(tempInspectionReports);
			
			// render view
			CView hostgroupView = new CView("configuration.inspectionReports.list", data);
			hostgroupView.render(getIdentityBean(), executor);
			hostgroupView.show();
		}
	}
	
	public static void main(String agrs[]){
		System.out.println(2>=2);
	}
	/**
	 * 此方法用于测试
	 */
	/*private void scheduleCollect() {
		CInspectionReportHistoryDAO dao =new CInspectionReportHistoryDAO();
		boolean result=true;
		CArray<Map> reports = dao.noExcInspectionReportList();
		if(!empty(reports)){
			for(Map report:reports){
				CArray<Map> rhitems=dao.reportHistoryItems(report.get("reportid").toString());
				if(!empty(rhitems)){
					int batchnum = Nest.value(report, "batchnum").asInteger()+1;
					result=dao.addInspectionRepHis(Nest.value(report, "reportid").asLong(),batchnum, rhitems);
					if(result){
						dao.updateNexttime(array(report));
					}
					if(result){
						dao.updateReprotBatchNum(report, batchnum);
						dao.sendInspectionReport(report,rhitems);
					}
				}
			}
		}
	
	}*/
}
