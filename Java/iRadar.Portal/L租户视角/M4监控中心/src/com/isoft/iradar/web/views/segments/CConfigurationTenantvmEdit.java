package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.web.Util.THttpconfUtil.getalarmHeader;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
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


public class CConfigurationTenantvmEdit extends CViewSegment {		
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget tvmwidget=new CWidget();
		
		CForm tvmForm = new CForm();
		tvmForm.setName("tvmForm");
		tvmForm.addVar("form", "form");
		tvmForm.addVar("hostid", Nest.value(data, "hostid").asLong());
		tvmForm.addVar("detatalarraytest", "");
		tvmForm.addVar("addcustom", Nest.value(data, "addcustom").asBoolean());
		
		LinkedHashMap<String,String> keymap=Nest.value(data, "keymap").asCArray();//应用类型
		LinkedHashMap<String,String> descriptionmap=Nest.value(data, "descriptionmap").asCArray();//描述map
		LinkedHashMap<String,String> appidmap=Nest.value(data, "appidmap").asCArray();//appid map
		
		CFormList tvmFormList = new CFormList("tvmFormList");
		String vmname=Nest.value(data, "vmname").asString();
		String vmip=Nest.value(data, "vmip").asString();
		CTextBox nameTextBox = new CTextBox("vmname", vmname, RDA_TEXTBOX_STANDARD_SIZE,true);
		nameTextBox.setTitle(vmname);
		tvmFormList.addRow(_("virtualMachineName"), nameTextBox);
		
		CTextBox ipBox = new CTextBox("vmip", vmip, RDA_TEXTBOX_STANDARD_SIZE,true);
		tvmFormList.addRow(_("virtualMachineIp"), ipBox);
		
		//查看监控指标
		CTable itemtable=new CTable(_("NO found Data"),"width:800px");
		itemtable.setAttribute("id", "itemtableid");
		
		List<Map<String,Object>> itemdatas=new ArrayList<Map<String,Object>>();
		int i=0,j=appidmap.size();
		for (Entry<String, String> entry: appidmap.entrySet()) {//磁盘使用率隐藏
			i++;
			Map<String,Object>	datamap=new HashMap<String,Object>();	
		    String key = entry.getKey();
		    String value = entry.getValue();
		    if(!"磁盘使用率".equals(key)){
		    	datamap.put("appname", key);
			    List<Map<String,String>> items=getItemName(value,executor);
			    datamap.put("itemsmap",items);
			    itemdatas.add(datamap);
		    }
		}
		
		for (Entry<String, String> entry: descriptionmap.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    itemtable.addRow(new CDiv(key, "ind"));
			itemtable.addRow(new CDiv(value,"instructions"),"instruct");
		}
		tvmFormList.addRow(_("Items"),new CButton("chooseItem",_("Items"),null));
		tvmFormList.addRow("",itemtable,false,"itemdiv","pack");
		
		//监控配置信息
		tvmFormList.addRow(_("Item information"),"");
		CTSeverity severityDiv = new CTSeverity(idBean, executor, map(//监控频率
				"id", "testids",
				"name", "monitoringName",
				"value", Nest.value(data, "monitoringName").$()
			));
		tvmFormList.addRow(new CDiv(_("Item Frequency"),"sm_title"),severityDiv);
		CButton cb=new CButton("addCustom", _("Add custom Trigger Line"));
		cb.setAttribute("onclick", "addCustomTwo("+JSONArray.fromObject(itemdatas).toString()+")");
		tvmFormList.addRow(new CDiv( _("Add custom Trigger Line"),"sm_title"),cb);
		tvmFormList.addRow("",new CDiv(null,"sm_title","itemid"));
		//监控描述部分
		CFormList tvmform=new CFormList("tvmt");
		tvmform.addStyle("display:none");
		//监控指标描述
		CComboBox typeBox = new CComboBox("type", Nest.value(data,"itembtn").$());
		typeBox.addItem(keymap.get("system.cpu.util[,idle]"),keymap.get("system.cpu.util[,idle]"));
		CComboBox conditionOperatorsComboBox = new CComboBox("operator", Nest.value(data,"operator").asString());//操作符号
		conditionOperatorsComboBox.addItem(_("GT"),_("GT"));
		conditionOperatorsComboBox.addItem(_("LT"),_("LT"));
		CTextBox TextBox = new CTextBox("numerical",Nest.value(data,"numerical").asString(), 20,false);//阀值
		TextBox.setAttribute("maxlength", IMonConsts.T_THRESHOLD_VALUE_LENGTJ);//告警规则加入长度限制
		CSpan cspan= new CSpan("", "","untis");
		
		CComboBox numericalComboBox = new CComboBox("numericalcom", Nest.value(data,"operator").asString());//云主机状态下拉选择框
		numericalComboBox.addStyle("display:none");
		CTable conditionTable = new CTable(_("No conditions defined."), "");
		conditionTable.attr("id", "conditionTable");
		conditionTable.attr("style", "min-width: 350px;");
		conditionTable.addRow(array(typeBox,conditionOperatorsComboBox,TextBox,cspan,numericalComboBox));
		tvmform.addRow(new CDiv(_("Item Decription"),"sm_title"),conditionTable,false,null,"pack");
		//触发次数以及立即启用
		CArray<String> triggercap=getTriggerCaption();
		CTSeverity triggernumDiv = new CTSeverity(idBean, executor, map(//触发次数
				"id", "triggerbtnid",
				"name", "gaonum",
				"value", Nest.value(data, "gaonum").$()
			),triggercap,false);
		tvmform.addRow(new CDiv(_("Alarm trigger several times"),"sm_title"),triggernumDiv,false,null,"pack");
		
		CArray<String> cap=getCaption();
		CTSeverity capDiv = new CTSeverity(idBean, executor, map(//是否立即启用
				"id", "capbtnid",
				"name", "isenable",
				"value", Nest.value(data, "isenable").$()
			),cap,false);
		tvmform.addRow(new CDiv(_("if available."),"sm_title"),capDiv,false,null,"pack");
	
		CArray operationButton=array(new CButton("add", _("Sure")),new CButton("cancel", _("Cancel")));
		tvmform.addRow(new CDiv(operationButton,"savebtn"),null,false,null,"pack" );
		
		
		CTable ctable=new CTable(_("NO found Data"));
		ctable.setHeader(array(_("Item"),_("Condition"),_("threshold"),_("trigger Number"),_("Status"),_("Operations")));
		ctable.addStyle("width:800px");
		ctable.attr("id", "cdata");
		
		CArray<Map> triggers=Nest.value(data, "triggers").asCArray();
		if(!empty(triggers)){
			for (Map  trigger : triggers) {
				String triggerid=Nest.value(trigger,"triggerid").asString();
				CArray<Map> items=Nest.value(trigger,"items").asCArray();
				String indnamestr=null;
				for(Map item : items){
					indnamestr=keymap.get(Nest.value(item,"key_").asString());
					if(!empty(indnamestr)){
						break;
					}
				}
				if(empty(indnamestr))
					break;
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
		
		CFormList cform=new CFormList("tvmformlisttwo",IMonConsts.STYLE_CLASS_MULTLINE); 
		CTable alarmtable =new CTable(_("No alarm way"));
		alarmtable.addClass("alarm_id");
		
		//通知方式头
		alarmtable.addRow(getalarmHeader(Nest.value(data, "sysMediatypes").$s()));
		
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
				
				alarmtable.addRow(usrmtsLine);
			}
		}
		
		CArray others = array();
		others.add(new CButtonCancel());
		
		cform.addRow(_("Added custom Trigger Line"), ctable);
		cform.addRow(_("Event notification"),alarmtable);

		cform.addRow("",new CDiv(array(new CSubmit("tsave", _("Create Project")), others),"savebtn"));

		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", Nest.value(data,"caption").asString(), array(tvmFormList,tvmform,cform));//将两个formlist合并
		tvmForm.addItem(itemTab);
		tvmwidget.addItem(tvmForm);
		
		return tvmwidget;
	}


	 public  CArray<String> getTriggerCaption()
	   {
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(1), "1"+_("of times"), 
			Integer.valueOf(2), "2"+_("of times"), 
			Integer.valueOf(3),"3"+_("of times")});
		 return itemcap;
	   }
	 
	 public  CArray<String> getCaption()
	   {
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(0), _("Enable"), 
			Integer.valueOf(1), _("DISABLED")});
		  return itemcap;
	   }

	 public static void main(String[] args){

	 }
	 
	 /**  获取应用级下的监控指标名称与单位
	 * @param applicationid
	 * @param executor
	 * @return
	 */
	public List<Map<String,String>> getItemName(String applicationid,SQLExecutor executor){
		 Map params=new HashMap();
		 List<Map<String,String>> items=new ArrayList<Map<String,String>>();
		 
		 params.put("applicationid", applicationid);
		CArray<Map> itemmap = DBselect(executor,
				" SELECT name , units"+
				" FROM i_t_item a"+
				" WHERE a.applicationid=#{applicationid}",
				params
			);
		
		 for(Map item:itemmap){
			 Map<String, String> maps=new HashMap<String, String>();
			 maps.put("name", item.get("name").toString());
			 maps.put("units",item.get("units").toString());
			 items.add(maps);
		 }
		return items;
	 }
}
