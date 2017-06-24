package com.isoft.web.bean.common;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.maxTill;
import static com.isoft.iradar.inc.ValidateUtil.minSince;
import static com.isoft.iradar.inc.ValidateUtil.validateDateTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AnnounceAction extends RadarBaseAction {
	
	public static final int ANNOUNCEMENT_TYPE = 3;	//日志资源中公告类型
	
	@Override
	protected void doInitPage() {
		page("title", _("Manage announcement"));
		page("file", "announce.action");
		page("hist_arg", new String[] { "groupid" });
		page("scripts", new String[] { "class.calendar.js"});
		page("css", new String[] { "lessor/systemmanage/noticemanage.css" });
		page("js", new String[] {"imon/notice.js"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			// maintenance
			"announcementid" ,									    array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"announcementids" ,								        array(T_RDA_INT, O_OPT, P_SYS,	DB_ID, 		null),
			"content" ,											    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", "内容"),
			"title" ,							                    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})","标题"),
			"active_since" ,										array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_till" ,											array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	"isset({save})"),
			"active_since_day" ,								    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY+"{}*1>0",	null,"生效时间日期数"),
			"active_since_month" ,							        array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY+"{}*1>0",	null,"生效时间月份数"),
			"active_since_year" ,								    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY+"{}*1>0",	null,"生效时间年份数"),
			"active_since_hour" ,								    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null,"生效时间小时数"),
			"active_since_minute" ,							        array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null,"生效时间分钟数"),
			"active_till_day" ,									    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY+"{}*1>0",	null,"停止时间日期数"),
			"active_till_month" ,								    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY+"{}*1>0",	null,"停止时间月份数"),
			"active_till_year" ,									array(T_RDA_STR, O_OPT, null,   NOT_EMPTY+"{}*1>0",	null,"停止时间年分数"),
			"active_till_hour" ,									array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null,"停止时间小时数"),
			"active_till_minute" ,								    array(T_RDA_STR, O_OPT, null, 	NOT_EMPTY,	null,"停止时间分钟数"),
			// actions
			"go" ,													array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cease" ,												array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,												array(T_RDA_STR, O_OPT, P_SYS,		 null,	null),
			// form
			"form" ,												array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"show" ,												array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,									    array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Actions */
		final AnnouncementDAO an=new AnnouncementDAO(executor);
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"announcementid")) {
			unset(_REQUEST,"announcementid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			String msg1,msg2;
			if (isset(_REQUEST,"announcementid")) {
				msg1 = _("公告更新成功");
				msg2 = _("公告更新失败");
			} else {
				msg1 = _("公告创建成功");
				msg2 = _("公告创建失败");
			}

			boolean result = true;
			if (!validateDateTime(Nest.value(_REQUEST,"active_since_year").asInteger(),
					Nest.value(_REQUEST,"active_since_month").asInteger(),
					Nest.value(_REQUEST,"active_since_day").asInteger(),
					Nest.value(_REQUEST,"active_since_hour").asInteger(),
					Nest.value(_REQUEST,"active_since_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("Active since")));
				result = false;
			}
			if (!validateDateTime(Nest.value(_REQUEST,"active_till_year").asInteger(),
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger())) {
				info(_s("Invalid date \"%s\".", _("Active till")));
				result = false;
			}
			if (result) {
				Long activeSince = null;
				if (isset(_REQUEST,"active_since")) {
					activeSince = mktime(
						Nest.value(_REQUEST,"active_since_hour").asInteger(),
						Nest.value(_REQUEST,"active_since_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_since_month").asInteger(),
						Nest.value(_REQUEST,"active_since_day").asInteger(),
						Nest.value(_REQUEST,"active_since_year").asInteger()
					);
					if (activeSince < minSince) {
						show_messages(true,"生效时间必须在2010.01.01 00:00 到 2038.01.01 00:00之间");
						result = false;
					}
				}
				Long activeTill = null;
				if (isset(_REQUEST,"active_till")) {
					activeTill = mktime(
						Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_year").asInteger()
					);
					if (activeTill > maxTill) {
						show_messages(true,"停止时间必须在2010.01.01 00:00 到 2038.01.01 00:00之间");
						result = false;
					}
				}
				if (result) {
				    Date date2 =new Date();
					SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
					String nowdate = format.format(date2);
					Date now_date1 = null; //系统时间
					int stu = 0;//为开始
					try {
						now_date1 = format.parse(nowdate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					 Long timestamp=activeSince*1000;
					 Long timeend=activeTill*1000;
					 Long nowCompare=now_date1.getTime();
					int resultstart=nowCompare.compareTo(timestamp);
					int resultEnd=nowCompare.compareTo(timeend);
					if(resultstart>=0&&resultEnd<=0){
						stu=1;//生效
					}if(resultEnd>=0){
						stu=2;//已过期
					}
				final Map maintenance = map(
					"announcementid",get_request("announcementid"),
					"content",get_request("content"),
					"title",get_request("title"),
					"active_since", activeSince,
					"active_till", activeTill
					
				);
				Nest.value(_REQUEST, "active_since").$(activeSince);
				Nest.value(_REQUEST, "activeTill").$(activeTill);
				maintenance.put("status", stu);
				IdentityBean infor=getIdBean();
				String userName =infor.getUserName();
			    Role a=	infor.getTenantRole();
				if(a.toString().equals("1")){
					msg2= "您没有权限进行此操作";
					result = false;
				}
				String tenid=infor.getTenantId();
				maintenance.put("userName", userName);
				maintenance.put("tenantid", tenid);
				int compareTime = timestamp.compareTo(timeend);
				if(compareTime>0){
					info(_("timeMessage"));
					result = false;
				}else{
					if(an.doConflictNameCheck(maintenance) && !isset(_REQUEST,"announcementid")){
						result = false;
						msg2 = _("the titile is conflicted");
					}else{
						if(result){
							if (isset(_REQUEST,"announcementid")) {
								result = Call(new Wrapper<Boolean>() {
									@Override
									protected Boolean doCall() throws Throwable {
										
										return an.doUpdata(maintenance);
									}
								});
							} else {
								result = Call(new Wrapper<Boolean>() {
									@Override
									protected Boolean doCall() throws Throwable {
										return an.doCreate(maintenance);
									}
								});
							}
						}
					}
				}

				if (result) {
					if(!isset(_REQUEST,"announcementid")){
						add_audit(getIdentityBean(),executor,AUDIT_ACTION_ADD, ANNOUNCEMENT_TYPE, "添加公告："+Nest.value(maintenance,"title").asString());
					}else{
						add_audit(getIdentityBean(),executor,AUDIT_ACTION_UPDATE, ANNOUNCEMENT_TYPE,"更新公告：" + Nest.value(maintenance,"title").asString());
					}
					unset(_REQUEST,"form");
				}

				show_messages(result, msg1, msg2);
				clearCookies(result);
			  }
			}
		} else if (isset(_REQUEST,"delete") || "delete".equals(Nest.value(_REQUEST,"go").$())) {
			final CArray<String> announcementids;
			if (isset(_REQUEST,"announcementids")) {
				announcementids = Nest.value(_REQUEST,"announcementids").asCArray();
			} else {
				announcementids = get_request("announcementids", array());
			}
			DBstart(executor);
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return an.doDelete(announcementids.toList());
				}
			});
			if (goResult) {
				add_audit(getIdentityBean(),executor, AUDIT_ACTION_DELETE, ANNOUNCEMENT_TYPE, "删除公告 "+announcementids.values());
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("公告删除成功"), _("公告删除失败"));
			clearCookies(goResult);
		}else if (isset(_REQUEST,"cease") || "cease".equals(Nest.value(_REQUEST,"go").$())) {
			final CArray<String> announcementids;
			if (isset(_REQUEST,"announcementids")) {
				announcementids = Nest.value(_REQUEST,"announcementids").asCArray();
			} else {
				announcementids = get_request("announcementids", array());
			}
			DBstart(executor);
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return an.doCease(announcementids.toList());
				}
			});
			if (goResult) {
				add_audit(getIdentityBean(),executor, AUDIT_ACTION_DISABLE, ANNOUNCEMENT_TYPE, "停用公告  ID"+announcementids.values());
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("公告停用成功"), _("公告停用失败"));
			clearCookies(goResult);
		}else if (isset(_REQUEST,"enable") || "enable".equals(Nest.value(_REQUEST,"go").$())) {
			final CArray<String> announcementids;
			if (isset(_REQUEST,"announcementids")) {
				announcementids = Nest.value(_REQUEST,"announcementids").asCArray();
			} else {
				announcementids = get_request("announcementids", array());
			}
			DBstart(executor);
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return an.doEnable(announcementids.toList());
				}
			});
			if (goResult) {
				add_audit(getIdentityBean(),executor, AUDIT_ACTION_ENABLE, ANNOUNCEMENT_TYPE, "启用公告  ID"+announcementids.values());
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("公告启用成功"), _("公告启用失败"));
			clearCookies(goResult);
		}  
	
		/* Display */
		CArray data = map(
			"form" , get_request("form"),
			"show" , get_request("show")
		);
		if (!empty(Nest.value(data, "form").$())) {
			doRenderFormView(executor, data);
		} else if(!empty(Nest.value(data, "show").$())){ 
			doRenderShowView(executor, data);
		}else{
			doRenderListView(executor,  data);
		}
	}
	
	private void doRenderFormView(SQLExecutor executor,CArray data){
		Nest.value(data,"announcementid").$(get_request("announcementid"));
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));
		Nest.value(data,"active_since").$(get_request("active_since"));
		Nest.value(data,"active_till").$(get_request("active_till"));
		if (isset(data,"announcementid") && !isset(_REQUEST,"form_refresh")) {	
		}else{
			if (isset(_REQUEST,"active_since")) {
				Nest.value(data,"active_since").$(mktime(Nest.value(_REQUEST,"active_since_hour").asInteger(),
						Nest.value(_REQUEST,"active_since_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_since_month").asInteger(),
						Nest.value(_REQUEST,"active_since_day").asInteger(),
						Nest.value(_REQUEST,"active_since_year").asInteger()));
			} else {
				Nest.value(data,"active_since").$(strtotime("today"));
			}
			if (isset(_REQUEST,"active_till")) {
				Nest.value(data,"active_till").$(mktime(Nest.value(_REQUEST,"active_till_hour").asInteger(),
						Nest.value(_REQUEST,"active_till_minute").asInteger(),
						0,
						Nest.value(_REQUEST,"active_till_month").asInteger(),
						Nest.value(_REQUEST,"active_till_day").asInteger(),
						Nest.value(_REQUEST,"active_till_year").asInteger()));
			} else {
				Nest.value(data,"active_till").$(strtotime("tomorrow"));
			}
			
		}
		// render view
		CView maintenanceView = new CView("configuration.Announce.edit", data);
		maintenanceView.render(getIdentityBean(), executor);
		maintenanceView.show();
	}

	private void doRenderListView(SQLExecutor executor, CArray data) {
		AnnouncementDAO an =new AnnouncementDAO(executor);
		CArray<Map> maintenances =new CArray<Map>();
		List<Map> lis=an.doconfig();
		CArray<Map> parm =new CArray<Map>();
		parm.put("search_limit",Long.parseLong(lis.get(0).get("search_limit").toString()));
		List list=an.doList(parm);
		int i=0;
		for (Object object : list) {
			maintenances.put(i, object);
			i++;
		}
		Nest.value(data,"maintenances").$(maintenances);
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, maintenances, array("announcementid")));
		Nest.value(data,"maintenances").$(maintenances);
		// render view
		CView maintenanceView = new CView("configuration.Announce.list", data);
		maintenanceView.render(getIdentityBean(), executor);
		maintenanceView.show();
	}
	
	/**  租户公告展示页
	 * @param executor
	 * @param data
	 */
	private void doRenderShowView(SQLExecutor executor, CArray data) {
		AnnouncementDAO an =new AnnouncementDAO(executor);
		List<Map> list = an.doeffectiveList(map("search_limit",100));
		
		CWidget hostsWidget = new CWidget();
		CForm aform=new CForm("aform");
		CTableInfo table =new CTableInfo();
		table.setHeader(array("标题","内容","日期","查看"));
		for (Map announc : list) {
			String title = Nest.value(announc, "title").asString();
			String id= Nest.value(announc, "announcementid").asString();
			String comment= Nest.value(announc, "content").asString();
			if(comment.length()>50){
				comment = comment.subSequence(0, 50)+"...";
			}
			String activeSince = Nest.value(announc,"active_since").asString();
			Long timestamp = Long.parseLong(activeSince)*1000;
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp));  
			CSpan span=new CSpan("查看","tenantitle");
			span.setAttribute("onclick", "return PopUp(\"alertfist.action?tenanid="+id+"&title="+title+"\", 550, 300)");
			table.addRow(array(title,comment,date,span));
		}
		aform.addItem(table);
		hostsWidget.addItem(aform);
		hostsWidget.show();
	}
}
