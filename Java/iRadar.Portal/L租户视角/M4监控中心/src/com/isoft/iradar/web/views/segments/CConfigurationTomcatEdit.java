package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.web.Util.THttpconfUtil.getalarmHeader;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import com.isoft.biz.daoimpl.common.MediaDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTomcatEdit extends CViewSegment {
	
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget tvmwidget=new CWidget();
		
		CForm tvmForm = new CForm();
		tvmForm.setName("tvmForm");
		tvmForm.addVar("form", "form");
		//tvmForm.addVar("hostid", Nest.value(data, "hostid").asLong());
		tvmForm.addVar("applicationid", Nest.value(data, "applicationid").asLong());
		tvmForm.addVar("detatalarraytest", "");
		tvmForm.addVar("chooseitem", Nest.value(data, "chooseitem").asBoolean());
		tvmForm.addVar("addcustom", Nest.value(data, "addcustom").asBoolean());
		
		Map<String,String> keymap=Nest.value(data, "keymap").asCArray();//应用类型
		
		CFormList tvmFormList = new CFormList("tvmFormList");
		String name=Nest.value(data, "name").asString();
		String tomcatport=Nest.value(data, "tomcatport").asString();
		String jmxport=Nest.value(data, "jmxport").asString();
		String javahome=Nest.value(data, "javahome").asString();
		
		CTextBox nameTextBox = new CTextBox("name", name, RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.setTitle(name);
		CTextBox tomcatportTextBox = new CTextBox("tomcatport", tomcatport, RDA_TEXTBOX_STANDARD_SIZE);
		CTextBox jmxportTextBox = new CTextBox("jmxport", jmxport, RDA_TEXTBOX_STANDARD_SIZE);
		CTextBox javahomeTextBox = new CTextBox("javahome", javahome, RDA_TEXTBOX_STANDARD_SIZE);
		
		tvmFormList.addRow(_("Name"), nameTextBox);
		tvmFormList.addRow("tomcat端口号", tomcatportTextBox);
		tvmFormList.addRow("JMX端口号", jmxportTextBox);
		tvmFormList.addRow("java路径", javahomeTextBox);
		tvmFormList.addRow("", "以Windows系统为例：C:/Program Files/Java/jdk1.7.0_67 ");
		
		//所属云主机
		Map<String,String> virtmap=Nest.value(data, "virtmap").asCArray();
		String pro_hostid=Nest.value(data, "pro_hostid").asString();
		CComboBox boBox = new CComboBox("belongVirt", "");
		if(empty(pro_hostid)){
			for(Map.Entry<String, String> entry:virtmap.entrySet()){ 
				boBox.addItem(entry.getKey(), entry.getValue());
			}   
		}else{
			boBox.addItem(pro_hostid, virtmap.get(pro_hostid),true);
		}
		tvmFormList.addRow(_("Belongs to VM"), boBox);

		//监控选择项
		tvmFormList.addRow(_("Set Item"),"",false,"monit_id","monit_class");
		CTSeverity severityDiv = new CTSeverity(idBean, executor, map(
				"id", "monitoringid",
				"name", "monitoringName",
				"value", Nest.value(data, "monitoringName").$()
			));
		tvmFormList.addRow(new CDiv(_("Item Frequency"),"sm_title"),severityDiv);
		CButton cb=new CButton("showcalCustom", _("Add custom Trigger Line"));
		cb.setAttribute("onclick", "addshowcalCustom("+JSONArray.fromObject(keymap).toString()+")");
		tvmFormList.addRow(new CDiv(_("Add custom Trigger Line"),"sm_title"),cb);
		
		//监控指标表达式
		CFormList tvmformOne=new CFormList("tvmtServes");
		tvmformOne.addStyle("display:none;");
		CComboBox typeBox = new CComboBox("type", "");

		for(Map.Entry<String, String> entry:keymap.entrySet()){ 
			typeBox.addItem(entry.getValue(), entry.getValue());
		} 
		
		CComboBox conditionOperatorsComboBox = new CComboBox("operator", Nest.value(data,"operator").asString());//操作符号
		conditionOperatorsComboBox.addItem(_("GT"),_("GT"));
		conditionOperatorsComboBox.addItem(_("LT"),_("LT"));
		CTextBox TextBox = new CTextBox("numerical",Nest.value(data,"numerical").asString(), 20,false);//阀值
		TextBox.setAttribute("maxlength", IMonConsts.T_THRESHOLD_VALUE_LENGTJ);//告警规则加入长度限制
		CSpan cspan= new CSpan("", "","untis");
		
		CTable conditionTable = new CTable(_("No conditions defined."),"pack");
		conditionTable.attr("id", "conditionTable");
		conditionTable.attr("style", "min-width: 350px;");
		conditionTable.addRow(array(typeBox,conditionOperatorsComboBox,TextBox,cspan));
		tvmformOne.addRow(new CDiv(_("Item Decription"),"sm_title"),conditionTable,false,null,"pack");
		
		//触发次数以及是否立即启用
		CArray<String> triggercap=getTriggerCaption();
		CTSeverity triggernumDiv = new CTSeverity(idBean, executor, map(//触发次数
				"id", "triggerbtnid",
				"name", "gaonum",
				"value", Nest.value(data, "gaonum").$()
			),triggercap,false);
		tvmformOne.addRow(new CDiv(_("Alarm trigger several times"),"sm_title"),triggernumDiv,false,null,"pack");
		
		CArray<String> cap=getCaption();
		CTSeverity capDiv = new CTSeverity(idBean, executor, map(//是否立即启用
				"id", "capbtnid",
				"name", "isenable",
				"value", Nest.value(data, "isenable").$()
			),cap,false);
		tvmformOne.addRow(new CDiv(_("if available."),"sm_title"),capDiv,false,null,"pack");
		
		CArray operationButton=array(new CButton("add", _("Sure")),new CButton("cancel",_("Cancel")));
		tvmformOne.addRow(new CDiv(operationButton,"savebtn"),null,false,null,"pack" );
		
		//已添加自定义告警
		CTable ctable=new CTable(_("NO found Data"));
		ctable.setHeader(array(_("Item"),_("Condition"),_("threshold"),_("trigger Number"),_("Status"),_("Operations")));
		ctable.addStyle("width:800px");
		ctable.attr("id", "cdata");
		CArray<Map> triggers=Nest.value(data, "triggers").asCArray();
		if(!empty(triggers)){
			for (Map  trigger : triggers) {
				 //CCol numcol = new CCol(String.valueOf(num));
				String triggerid=Nest.value(trigger,"triggerid").asString();
				CArray<Map> items=Nest.value(trigger,"items").asCArray();
				String indnamestr=null;
				for(Map item : items){
					indnamestr=keymap.get(Nest.value(item,"key_").asString());
				}
				CCol indname= new CCol(indnamestr);
				indname.setAttribute("value", indnamestr);
			   
				CArray<Map> functions=Nest.value(trigger,"functions").asCArray();
				String functionstr=null;
				String conditionstr=null;
				String thresholdstr=null;
				String numberstr=null;
				for(Map function : functions){
					functionstr=Nest.value(function,"parameter").asString();
					 String[] exparr=functionstr.split(",");
					 conditionstr=TvmUtil.getLt(exparr[2]); 
					 thresholdstr=exparr[1];
					 numberstr=exparr[0].substring(1);	 
				}
				 CCol indconditions= new CCol(conditionstr);
				 indconditions.setAttribute("value", conditionstr);
				 CCol indthreshold= new CCol(thresholdstr);
				 indthreshold.setAttribute("value", thresholdstr);
				 CCol indnumberstr= new CCol(numberstr+_("of times"));
				 indnumberstr.setAttribute("value", numberstr+_("of times"));
				 String indenblestr=TvmUtil.getStatus(Nest.value(trigger, "status").asInteger());
				 CCol indenble= new CCol(indenblestr);
				 indenble.setAttribute("value", indenblestr);
				ctable.addRow(array(indname,indconditions,indthreshold,indnumberstr,indenble,
						array(new CButton("update1", _("Edit"), "edit("+triggerid+")","link_menu icon edit"),new CButton("delete1", _("Delete"),"del("+triggerid+")","link_menu icon remove"))),"pack",triggerid);
			}
		}
		
		
		//第二个formlist，可以标题跟表格换行
		CFormList cform=new CFormList("tvmformlisttwo",IMonConsts.STYLE_CLASS_MULTLINE);
		CTable alarmtable =new CTable(_("No alarm way"));
		alarmtable.addClass("alarm_id");
		//通知方式头
		alarmtable.addRow(getalarmHeader(Nest.value(data, "sysMediatypes").$s()));
				
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
				
				alarmtable.addRow(usrmtsLine);
			}
		}
		
		CArray others = array();
		others.add(new CButtonCancel());
       
		cform.addRow(_("Added custom Trigger Line"), ctable);
		cform.addRow(_("Event notification"),alarmtable);
		cform.addRow("",new CDiv(array(new CSubmit("tsave",  _("Create Project")), others),"savebtn"));
		
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", Nest.value(data,"caption").asString(), array(tvmFormList,tvmformOne,cform));//将两个formlist合并
		tvmForm.addItem(itemTab);
		tvmwidget.addItem(tvmForm);
		
		return tvmwidget;
	}

	public  CArray<String> getTriggerCaption()
	   {
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(1), "1"+_("of times"), 
			Integer.valueOf(2), "2"+_("of times"), 
			Integer.valueOf(3), "3"+_("of times")});
		 return itemcap;
	   }
	 
	public  CArray<String> getCaption()
	   {
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(0), _("Enable"), 
			Integer.valueOf(1), _("DISABLED")});
		  return itemcap;
	   }
}
