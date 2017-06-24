package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.biz.daoimpl.tentant.THostDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTentanttemplateEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		THostDAO hostdao= new THostDAO(idBean, executor);
		
		CForm itemForm = new CForm();
		itemForm.setName("itemForm");
		Long templateid=Nest.value(data,"dbTemplate","templateid").asLong();
		if (!empty(templateid)) {
			itemForm.addVar("templateid", templateid);//传值租户模型id
		}
		 CArray<Map> vicehost = hostdao.getViceHostNumTwo(templateid, 0);
         String namestr=null;
         //模型表跟设备表是同一张表，只是状态不同，模型状态为3
         if(vicehost.size()!=0){
         	for(Map host:vicehost){//如果该模型名称没有更新或者已发布，则显示主表数据，如果正在审批中，则显示副表数据
         		namestr=Nest.value(host,"name").asString();
         	}
         }else{
         	namestr=Nest.value(data,"dbTemplate","name").asString();
         }
		
		int processStatus=Nest.value(data,"dbTemplate","process_status").asInteger();
		itemForm.addVar("processStatus", processStatus);//数据库中审批状态值
		CFormList itemFormList = new CFormList("itemFormList");
		
		CTableInfo table =new CTableInfo();//显示审批详情记录
		table.setHeader(getHeader());
		
		CTextBox template_nameTB = new CTextBox("template_name", namestr, RDA_TEXTBOX_STANDARD_SIZE,false);
		template_nameTB.setAttribute("maxlength", 64);
		template_nameTB.attr("autofocus", "autofocus");
		itemFormList.addRow("名称", template_nameTB);
		
		if (!empty(templateid)) {
			//审批状态
			CTextBox statusTextBox = new CTextBox("process_status", getStateStr(processStatus), RDA_TEXTBOX_STANDARD_SIZE, true);
			statusTextBox.attr("autofocus", "autofocus");
			itemFormList.addRow("审批状态", statusTextBox);
			
			//审批详情
			CArray<Map> processcarray=Nest.value(data,"processList").asCArray();
			int i=0;
			for(Map process : processcarray){
				i++;//在这里表示第几步操作步骤
				String username=Nest.value(process,"username").asString();
				String result=Nest.value(process,"result").asString();
				String description=Nest.value(process,"description").asString();
				String time=Nest.value(process,"operating_time").asString();
				table.addRow(array(i,i,username,result,description,time));
			}
			itemFormList.addRow("审批详情", table);
			
			// 审批备注
			CTextArea note = new CTextArea("note", Nest.value(data,"remarkstr").asString());
			note.addStyle("margin-top: 5px;");
			itemFormList.addRow("审批备注", note);
		
		}
		
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", Nest.value(data,"caption").asString(), itemFormList);
		itemForm.addItem(itemTab);
		//添加基本按钮
		CArray buttons = array();
		if (!empty(templateid)) {
			buttons.add(new CSubmit("submit", "提交"));
			buttons.add(new CSubmit("reset", "恢复到上一版本"));
		}
		array_push(buttons, new CButtonCancel(url_param(idBean, "groupid")));
		itemForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), buttons));
		
		//添加审批相关按钮
		CArray approvalbuttons = array();//审批按钮
		if (!empty(templateid)) {
			approvalbuttons.add(new CSubmit("reject", "驳回"));
			array_push(approvalbuttons, new CButtonCancel(url_param(idBean, "groupid")));
			itemForm.addItem(makeFormFooter(new CSubmit("agree", "同意"),approvalbuttons));
		}
		
		return itemForm;
	}
	
	private String getStateStr(int state){
		String statestr="";
		if(state==2){
			statestr="已发布";
		}else if(state==1){
			statestr="审批中";
		}else {
			statestr="未提交";
		}
		return statestr;
	}

	private CArray getHeader(){
		return array(
					"序号",
					"操作步骤",
					"操作人员",
					"审批结果",
					"审批备注",
					"操作时间"
			);
	}
}
