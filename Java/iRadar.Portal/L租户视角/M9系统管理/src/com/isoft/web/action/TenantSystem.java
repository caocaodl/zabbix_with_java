package com.isoft.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.isoft.biz.daoimpl.common.MediaDAO;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TenantSystem extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title","通知设置");
		page("file", "tenantsystem.action");
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("js", new String[] {"imon/common.media.js"});
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
//		VAR						TYPE		OPTIONAL FLAGS			VALIDATION	EXCEPTION
		CArray fields = map(
			// actions
			"go",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"tsave",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form",						    array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form_refresh",			        array(T_RDA_STR, O_OPT, null,		null,	null),
			"Email",			            array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"Mobile",			            array(T_RDA_STR, O_OPT, P_SYS,		null,	null)
		);
		check_fields(getIdentityBean(), fields);
	}
	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void doAction(final SQLExecutor executor) {
		CWidget tvmwidget = new CWidget();
		MediaDAO mdao = new MediaDAO(executor);
		CForm sysformOne = new CForm();
 		IdentityBean idBean= getIdentityBean();
		final String usertid = idBean.getUserId();
		String username=idBean.getUserName();
		sysformOne.addItem("");
		tvmwidget.addItem(sysformOne);
		List<Map> mediaTypes = mdao.doGetMediaTypes();
		if (isset(_REQUEST, "tsave")) {
			DBstart(executor);
			final CArray<Map> id = new CArray<Map>();
			id.put("userid", usertid);
			id.put("tenantid", idBean.getTenantId());
			mdao.doDeleteMediaByUserId(id);
			final Map users = new CArray<Map>();
			users.put("userid", usertid);
			users.put("tenantid", idBean.getTenantId());
			for (Map userOne : mediaTypes) {
				String name = "";
				if("电子邮件".equals(Nest.value(userOne,"name").asString())){
					name = "Email";
				}
				if("手机短信".equals(Nest.value(userOne,"name").asString())){
					name = "Mobile";
				}
				
				if(!Cphp.empty(Nest.value(_REQUEST, name).asString())){
					users.put("mediatypeid", Nest.value(userOne, "id").asString());
					users.put("sendto", Nest.value(_REQUEST, name).asString());
					mdao.doAddMediaByUserId(users);
				}
			}
			show_messages(true, "设置成功");
		}
		CForm sysform = new CForm();
		CTable alarmtable = new CTable("没有告警方式");
		alarmtable.setAttribute("align", "center");
		CRow rowName = new CRow();
		rowName.addItem(array(new CSpan("用户名称:", ""),new CSpan(username, "")));
		alarmtable.addRow(rowName);
		CArray<Map> ids = new CArray<Map>();
		ids.put("userid",  usertid);
		ids.put("tenantid", idBean.getTenantId());
		List<Map> medias = mdao.doGetMediaByUserId(ids);
		
		for (Map userOne : mediaTypes) {
			String value="";//通知配置编辑框的值
			CRow row = new CRow();
			for (Map user : medias) {
				if(Nest.value(userOne, "id").asString().equals(Nest.value(user, "id").asString())){
					value=Nest.value(user,"name").asString();
				}
			}
			String name = "";
			if("电子邮件".equals(Nest.value(userOne,"name").asString()))
				name = "Email";
			if("手机短信".equals(Nest.value(userOne,"name").asString()))
				name = "Mobile";
			
			CTextBox userName = new CTextBox(name, value);
			userName.setAttribute("maxlength", "30");
			
			row.addItem(array(new CSpan(Nest.value(userOne,"name").$()+":", ""),userName));
			alarmtable.addRow(row);
		}
		CRow row = new CRow();
		row.addItem(makeFormFooter(new CSubmit("tsave", _("Save")), ""));
		alarmtable.addRow(row);
		sysform.addItem(array(alarmtable));
		CFormList detailsFormList = new CFormList();
		detailsFormList.addItem(array(alarmtable));
		tvmwidget.addItem(new CDiv(sysform,"tenantsystem"));
		tvmwidget.show();
	}
	
	public static void main(String[] args){
		String email="12389qq.com";
		System.out.print(checkEmail(email));
	}
	
	public static boolean checkEmail(String email){
		boolean flag = false;
		try{
		String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
	     flag =  matcher.matches();
		  }catch(Exception e){
		  flag = false;
		  }
		
		  return flag;
		}
}
