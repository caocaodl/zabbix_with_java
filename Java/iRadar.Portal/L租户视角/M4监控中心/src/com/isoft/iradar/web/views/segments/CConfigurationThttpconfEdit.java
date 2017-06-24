package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.web.Util.THttpconfUtil.getAlarmLineListTable;
import static com.isoft.iradar.web.Util.THttpconfUtil.getalarmHeader;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationThttpconfEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {

		CWidget httpWidget = new CWidget();
		
		// create form
		CForm httpForm = new CForm();
		httpForm.setName("httpForm");
		httpForm.setAttribute("id", "httpForm");
		httpForm.addVar("form", Nest.value(data,"form").$());
		httpForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (isset(data,"httptestid")) {
			httpForm.addVar("httptestid", Nest.value(data,"httptestid").$());
			httpForm.addVar("actionid", Nest.value(data,"actionid").$());
		}
		
		/* 填写监控信息     tab */
		CFormList monInfoFormList = new CFormList("monInfoFormList");
		
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		/*monInfoFormList.addRow(_("Name"), array(nameTextBox, "<br/>给监控项目起一个名字，比如：web监控。"));*///将名称提示注释掉
		monInfoFormList.addRow(_("Name"), nameTextBox);
		
		CTextBox url = new CTextBox("url", Nest.value(data, "url").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		monInfoFormList.addRow("URL", array(url, "<br/>填写被监控的网址，可以是网站首页或其它页面，比如：http://www.domain.com/index.html"));
		
		//所属云主机
		Map<String,String> virtmap = Nest.value(data, "virtmap").asCArray();
		String hostid=Nest.value(data, "hostid").asString();
		CComboBox boBox = new CComboBox("hostid", hostid);
		if(empty(hostid)){
			for(Map.Entry<String, String> entry:virtmap.entrySet()){ 
				boBox.addItem(entry.getKey(), entry.getValue());
			}   
		}else{
			boBox.addItem(hostid, virtmap.get(hostid),true);
		}
		monInfoFormList.addRow(_("Belongs to VM"), boBox);
		
		/* 设置项目配置信息 tab */		
		CTSeverity delay = new CTSeverity(idBean, executor, map(
				"id", "delayid",
				"name", "delay",
				"value", Nest.value(data, "delay").asInteger()
			), setDelayList(), false);
		monInfoFormList.addRow(_("Item Frequency"), delay);
		
		//添加自定义告警线 
		CTable tblAlarmLine = new CTable(null, "formElementTable");
		tblAlarmLine.setAttribute("id", "alarmLineTable");
		
		CDiv alarmLineOpt = new CDiv(tblAlarmLine, "", "alarmLineOpt");	
				
		monInfoFormList.addRow(_("Add custom Trigger Line"), new CDiv(array(new CButton("addAlarmLine", _("Add custom Trigger Line")), alarmLineOpt), "objectgroup inlineblock border_dotted"));
		
		
		//显示  自定义告警线列表
		//CDiv alarmLineDiv = new CDiv(getAlarmLineListTable((CArray<Map>)Nest.value(data,"alarmLines").asCArray()), "objectgroup inlineblock border_dotted");
		CFormList alarmLineAndType = new CFormList("alarmLineAndType", IMonConsts.STYLE_CLASS_MULTLINE);
		alarmLineAndType.addRow(new CDiv(_("Added custom Trigger Line"),"twosm_title"), getAlarmLineListTable((CArray<Map>)Nest.value(data,"alarmLines").asCArray()));

		
		CTable alarmTypetable =new CTable(_("No alarm way"));
		alarmTypetable.addClass("alarm_id");
		//alarmtable.addStyle("width:400px");
		
		//通知方式头
		alarmTypetable.addRow(getalarmHeader(Nest.value(data, "sysMediatypes").$s()));
		
		//用户通知方式列表
		CArray<Map> userMediatypes = Nest.value(data, "userMediatypes").$s();
		if(!empty(userMediatypes)){
			for(Map usrmts : userMediatypes){
				CArray usrmtsLine=array();
				if(idBean.getUserId().equals(usrmts.get("userid"))){
					usrmtsLine.add(new CDiv(_("Current account"),"current_user"));
					usrmtsLine.add(new CDiv(usrmts.get("name"), "currentId"));
				}else{
					usrmtsLine.add("");
					usrmtsLine.add(new CDiv(usrmts.get("name"), "otherId"));
				}
				
				CArray<Map> mediatypes = Nest.value(usrmts, "mediatypes").$s();
				if(!empty(mediatypes)){
					for(Map mediatype : mediatypes){
						usrmtsLine.add(new CCheckBox("userMediatypes["+usrmts.get("userid")+"][mediatypeids]["+mediatype.get("mediatypeid")+"]", isset(mediatype, "selected")?true:false, "", mediatype.get("mediatypeid").toString()));
					}
				}
				
				usrmtsLine.add(new CInput("hidden", "userMediatypes["+usrmts.get("userid")+"][userid]", usrmts.get("userid").toString()));
				usrmtsLine.add(new CInput("hidden", "userMediatypes["+usrmts.get("userid")+"][name]", usrmts.get("name").toString()));
				
				alarmTypetable.addRow(usrmtsLine);
			}
		}
		
		alarmLineAndType.addRow(new CDiv(_("Event notification"),"twosm_title"), alarmTypetable);
		
		// append buttons to form
		if (!empty(Nest.value(data,"httptestid").$())) {
			alarmLineAndType.addRow("", new CDiv(array(
					new CSubmit("save", _("Create Project")),
					/*new CButtonDelete(_("Delete scenario?"), 
							url_param(idBean, map(
								"form", Nest.value(data, "form").asString(),
								"httptestid", Nest.value(data, "httptestid").asString(),
								"hostid", Nest.value(data, "hostid").asString()
							), false)
						),*/
					new CButtonCancel(url_param(idBean, "hostid"))
				), "savebtn"));
			
		} else {
			alarmLineAndType.addRow("", new CDiv(array(
					new CSubmit("save", _("Create Project")), 
					new CButtonCancel()
					), "savebtn"));
		}
			
		CTabView httpTab = new CTabView();
				
		httpTab.addTab("monInfoTab", "填写监控信息", array(monInfoFormList, alarmLineAndType));
		
		httpForm.addItem(httpTab);
		

		httpWidget.addItem(httpForm);

		return httpWidget;
	}
	
	/**
	 * 设置秒数
	 */
	public static  CArray<String> setDelayList(){
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(60), "60秒", 
			Integer.valueOf(90), "90秒", 
			Integer.valueOf(120),"120秒",
			Integer.valueOf(240),"240秒"
		 });
		 return itemcap;
	   }
	
}
