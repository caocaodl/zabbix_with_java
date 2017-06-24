package com.isoft.web.bean.common;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.web.bean.common.SystemWordbook;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmOperationsystemAction extends RadarBaseAction {
	
	private Long osId;

	@Override
	protected void doInitPage() {
		page("title", "操作系统");
		page("file", "adm.operationsystem.action");
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/devicecenter/cbn.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"id" ,					array(T_RDA_INT, O_OPT, P_SYS,	null,	null),
			"dlabel" ,				array(T_RDA_STR, O_OPT, null,	"(/^[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]+$/.test({}))",	"isset({save})", "名称"),
			"operationsystem",		array(T_RDA_INT, O_OPT, P_SYS,	null,	null),
			// actions
			"go" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"clone" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form" ,			array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		
		//获取在编辑页面删除数据时 通过url_param传过来的id参数
		osId = get_request_asLong("id");
		
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}
	
	@Override
	public void doAction(final SQLExecutor executor) {
		
		if (isset(_REQUEST,"save")) {
			doSaveAction(executor);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			doDeleteAction(executor);
		} else if (isset(_REQUEST,"delete") && !empty(osId)) {
			String msgOk = "操作系统类型已删除";
			String msgFailure = "无法删除操作系统类型";
			//准备数据
			List<Long> oslist = new ArrayList<Long>();
			oslist.add(osId);
			Map<String, List> paramMap = new HashMap<String, List>();
			paramMap.put("oslist", oslist);
			
			//执行数据库操作——开始
			DBstart(executor);
			boolean goResult = false;
			SystemWordbook sysWB = new SystemWordbook();
			Map param = map("osId",oslist);
			if(sysWB.doSysRelationGet(param)){
				goResult = false;	
				msgFailure = _("the relation of os is exist");
			}else{
				//删除数据
				goResult = sysWB.doDelete(paramMap);
				
				//执行数据库操作——结束
				goResult = DBend(executor, goResult);	
			}
			
			if (goResult) {
				unset(_REQUEST,"form");
			}
			//显示结果提示
			show_messages(goResult, msgOk, msgFailure);	
			//清空
			clearCookies(goResult);
		} 
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		CComboBox cmbConf = new CComboBox("configDropDown", "adm.operationsystem.action?actionType=otsys", "redirect(this.options[this.selectedIndex].value);");
		cmbConf.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"cbn.action?actionType=DEPT", _("DEPT"),
			"cbn.action?actionType=mRoom", _("mRoom"),
			"cbn.action?actionType=Cabinet", _("Cabinet"),
			"cbn.action?actionType=firm", _("FIRM"),
			"adm.operationsystem.action?actionType=otsys", _("otsys")
			
		));
		form.addItem(cmbConf);
		
		
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"id", get_request("id"),
				"dlabel", get_request("dlabel"),
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0)
			);
			CWidget osWidget = new CWidget();
			
			// create form
			CForm osForm = new CForm();
			osForm.setName("operationsystemForm");
			osForm.addVar("form", Nest.value(data,"form").$());
			if (!empty(Nest.value(data,"id").$())) {
				osForm.addVar("id", Nest.value(data,"id").$());
			}
			
			// create form list
			CFormList operationsystemFormList = new CFormList("operationsystemFormList");
			CTextBox nameTextBox = new CTextBox("dlabel", Nest.value(data,"dlabel").asString(), RDA_TEXTBOX_STANDARD_SIZE,false,30);
			operationsystemFormList.addRow(_("Name"), nameTextBox);
			
			// append tabs to form
			CTabView osTab = new CTabView();
			osTab.addTab("operationsystemTab", "操作系统类型", operationsystemFormList);
			osForm.addItem(osTab);

			// append buttons to form
			if (!empty(Nest.value(data,"id").$())) {
				osForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					array(
						new CButtonCancel(url_param(getIdentityBean(), "config"))
					)
				));
			} else {
				osForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					new CButtonCancel(url_param(getIdentityBean(), "config"))
				));
			}
			// append form to widget
			osWidget.addPageHeader("",form);
			osWidget.addItem(osForm);
			osWidget.show();
		} else {
			CWidget osWidget = new CWidget();
			CForm osForm = new CForm();
			osForm.setName("operationsystem");
			
			CToolBar tb = new CToolBar(osForm);
			tb.addSubmit("form", "创建操作系统类型", "", "orange create");
			
			CArray<CComboItem> goComboBox = array();
			CComboItem goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", "删除所选的操作系统类型?");
			goOption.setAttribute("class", "orange delete");
			goComboBox.add(goOption);
			tb.addComboBox(goComboBox);
			
			rda_add_post_js("chkbxRange.pageGoName = \"operationsystem\";");
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			osWidget.addItem(headerActions);
			
			//从数据库中获取类型为"SYSTEM"的数据
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("type", "SYSTEM");
			List<Map> resultData =  new SystemWordbook().doAll(paramMap);
			
			CTableInfo osTable = new CTableInfo();
			osTable.setHeader(array(
					new CCheckBox("all_operationsystem", false, "checkAll(\""+osForm.getName()+"\", \"all_operationsystem\", \"operationsystem\");"),
//					"键值",
					"操作系统类型"
					));
			CArray arraydata = new CArray();
			Map resultSingle = null;
			for (int i = 0; i < resultData.size(); i++) {
				resultSingle =  resultData.get(i);
				Object dkey = resultSingle.get("dkey");
				osTable.addRow(array(
					new CCheckBox("operationsystem["+resultSingle.get("id")+"]", false, null, Nest.value(resultSingle,"id").asInteger()),
//					dkey,
					new CLink(resultSingle.get("dlabel"), "?form=edit&id="+resultSingle.get("id")+"&dlabel="+resultSingle.get("dlabel"))
				));
				arraydata.put(resultSingle);
			}
			CTable paging = getPagingLine(getIdentityBean(), executor,arraydata);
			osForm.addItem(array(osTable,paging));
			osWidget.addPageHeader("",form);
			osWidget.addItem(osForm);
			osWidget.show();
		}
	}
	/**
	 * 保存数据
	 * @param executor
	 */
	private void doSaveAction(final SQLExecutor executor) {
		
		//准备数据
		final Map operationsystem = map(
				"id", Nest.value(_REQUEST,"id").$(),
				"dlabel", Nest.value(_REQUEST,"dlabel").$(),
				"type","SYSTEM"
			);
		
		boolean issamename=true;
		boolean result=true;
		String msgOk,msgFail;
		
		if (isset(_REQUEST,"id")) {
			msgOk = "操作系统类型已更新";
			msgFail = "无法更新操作系统类型";
		} else {
			msgOk = "操作系统类型已添加";
			msgFail = "无法添加操作系统类型";
		}
		String sql = " SELECT  dlabel  " +
					 " FROM sys_dict " +
					 " WHERE TYPE='SYSTEM' " +
					 " AND dlabel =#{dlabel} ";
		Map param = map();
		if(isset(_REQUEST,"id")){
			sql += " AND id <> #{id}"; 
			param.put("id",Nest.value(_REQUEST,"id").asString());
		}
		param.put("dlabel",Nest.value(_REQUEST,"dlabel").asString());
		Map nameExists = DBfetch(DBselect(executor,sql, 1, param));
		result=empty(nameExists);
		if (!result) {
			info(_s("same_system"));
		}
		if(result){
			//执行数据库操作——开始
			DBstart(executor);
			Object[] obj = null;
			//保存数据
			if (isset(_REQUEST,"id")) {
				obj = new SystemWordbook().doUpdate(operationsystem);
			} else {
				obj = new SystemWordbook().doAdd(operationsystem);
			}
			result = !empty(obj[0]);
			
			//执行数据库操作——结束
			result = DBend(executor, result);
		}
		//显示操作结果提示
		show_messages(result, msgOk, msgFail);
		
		//清空
		if(result){
			unset(_REQUEST,"form");
		}
		clearCookies(result);
	}
	/**
	 * 删除数据
	 * @param executor
	 */
	private void doDeleteAction(final SQLExecutor executor) {
		String msgOk = "操作系统类型已删除";
		String msgFailure = "无法删除操作系统类型";
		//准备数据
		List<Long> oslist = new ArrayList<Long>();
		final Long[] osIds = Nest.array(_REQUEST,"operationsystem").asLong();
		for (int i = 0; i < osIds.length; i++) {
			Long osid = osIds[i];
			oslist.add(osid);
		}
		Map<String, List> paramMap = new HashMap<String, List>();
		paramMap.put("oslist", oslist);
		
		//执行数据库操作——开始
		DBstart(executor);
		
		boolean goResult = false;
		SystemWordbook sysWB = new SystemWordbook();
		Map param = map("osId",oslist);
		if(sysWB.doSysRelationGet(param)){
			goResult = false;	
			msgFailure = _("the relation of os is exist");
		}else{
			//删除数据
			goResult = sysWB.doDelete(paramMap);
			
			//执行数据库操作——结束
			goResult = DBend(executor, goResult);	
		}
		//显示结果提示
		show_messages(goResult, msgOk, msgFailure);	
		
		//清空
		clearCookies(goResult);
	}
}
