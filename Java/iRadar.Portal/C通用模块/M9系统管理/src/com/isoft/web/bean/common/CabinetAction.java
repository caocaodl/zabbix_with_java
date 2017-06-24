package com.isoft.web.bean.common;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
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

import com.isoft.biz.daoimpl.common.SystemDAO;
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

public class CabinetAction extends RadarBaseAction {
	
	private Long osId;
	public static String actionType;
	public static Map titleMap = map("Cabinet","机柜",
									 "DEPT"   ,"部门结构",
									 "firm"   ,"厂商",
									 "mRoom"  ,"机房"); 
	
	@Override
	protected void doInitPage() {
		String title = "";
		if(!empty(Nest.value(_REQUEST,"actionType").asString())){
			actionType=Nest.value(_REQUEST,"actionType").asString();
			title = Nest.value(titleMap, actionType).asString();
		}
		page("title", title);
		page("file", "cbn.action");
		page("hist_arg", new String[] {});
		page("css", new String[] {"lessor/devicecenter/cbn.css"});
		
		this.getResponse().setHeader("Pragma","No-cache"); 
		this.getResponse().setHeader("Cache-Control","no-cache"); 
		this.getResponse().setHeader("Cache-Control", "no-store");
		this.getResponse().setDateHeader("Expires", 0); 
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"id" ,					array(T_RDA_INT, O_OPT, P_SYS,	null,	null),
			"dlabel" ,				array(T_RDA_STR, O_OPT, null,	"(/^[0-9a-zA-Z_\\-\\u4e00-\\u9fa5]+$/.test({}))",	"isset({save})", "名称"),
			"operationsystem",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"actionType",		    array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"seq_no",		    array(T_RDA_INT, O_OPT, P_SYS,	null,	null),
			
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
	
	@SuppressWarnings("null")
	@Override
	public void doAction(final SQLExecutor executor) {
		SystemWordbook Syswb  =new SystemWordbook();
		if(!empty(Nest.value(_REQUEST,"actionType").asString())){
			actionType=Nest.value(_REQUEST,"actionType").asString();
		}
		if (isset(_REQUEST,"save")) {
			doSaveAction(executor,actionType);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			 Long[] osIds = Nest.array(_REQUEST,"operationsystem").asLong();
			doDeleteAction(executor,osIds);
		} else if (isset(_REQUEST,"delete") && !empty(osId)) {
			Long[] osIds=new Long[1];
			osIds[0]=osId;
			doDeleteAction(executor,osIds);
			unset(_REQUEST,"form");
		} 
		
		/* Display  */
		CForm form = new CForm();
		form.cleanItems();
		String actionStr = "cbn.action?actionType="+actionType;
		CComboBox cmbConf = new CComboBox("configDropDown", actionStr, "redirect(this.options[this.selectedIndex].value);");
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

		
		
		
		/* Display  */
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
			osForm.addVar("actionType", actionType);
			CFormList operationsystemFormList = new CFormList("operationsystemFormList");
			if(actionType.equals("Cabinet")){
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("type", "MOTOR_ROOM");
				List<Map> resultData =  Syswb.doAll(paramMap);
				Map<String, String> paramMap1 = new HashMap<String, String>();
				paramMap1.put("id",Nest.value(data,"id").asString());
				List<Map> result=  Syswb.doAll(paramMap1);
				// create form list
				CComboBox vendor = new CComboBox("seq_no",result.get(0).get("seq_no").toString());
				int seq_no=0;
				for (Map m : resultData) {
					try{
						seq_no=Integer.parseInt(m.get("dkey").toString()) ;
					}catch (Exception e) {
						// TODO: handle exception
					}
					vendor.addItem(seq_no,(String) m.get("dlabel"));	
				}
				operationsystemFormList.addRow(_("Attribution"), vendor);	
			}
			
		
			CTextBox nameTextBox = new CTextBox("dlabel", Nest.value(data,"dlabel").asString(), RDA_TEXTBOX_STANDARD_SIZE,false,30);
			operationsystemFormList.addRow(_("Name"), nameTextBox);
			
			
			// append tabs to form
			CTabView osTab = new CTabView();
			osTab.addTab("operationsystemTab", "机柜", operationsystemFormList);
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
			Map<String, String> paramMap = new HashMap<String, String>();
			List<Map> resultData=null;
			String name=null;
			CToolBar tb = new CToolBar(osForm);
			if(actionType.equals("Cabinet")){
				 tb.addSubmit("form", "创建机柜", "", "orange create");
				 paramMap.put("type", "CABINET");
				 resultData =  Syswb.doAll(paramMap);
				 name=_("cationName");
			}
			if(actionType.equals("DEPT")){
				tb.addSubmit("form", "创建部门", "", "orange create");
				paramMap.put("type", "DEPT");
				resultData =  Syswb.doAll(paramMap);
				name=_("deptName");
			}
			if(actionType.equals("firm")){
				tb.addSubmit("form", "创建厂商", "", "orange create");
				paramMap.put("type", "FIRM");
				resultData =  Syswb.doAll(paramMap);
				name=_("firmName");
			}
			if(actionType.equals("mRoom")){
				tb.addSubmit("form", "创建机房", "", "orange create");
				paramMap.put("type", "MOTOR_ROOM");
				resultData =  Syswb.doAll(paramMap);
				name=_("roomName");
			}
			osForm.addVar("actionType", actionType);//删除时，提示框title
			CArray<CComboItem> goComboBox = array();
			CComboItem goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", "删除所选的选项?");
			goOption.setAttribute("class", "orange delete");
			goComboBox.add(goOption);
			tb.addComboBox(goComboBox);
			
			rda_add_post_js("chkbxRange.pageGoName = \"operationsystem\";");
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			osWidget.addItem(headerActions);
			
			//从数据库中获取类型为"SYSTEM"的数据
			
			
			
			CTableInfo osTable = new CTableInfo();
			CArray arraydata = new CArray();
			if(actionType.equals("Cabinet")){
				osTable.setHeader(array(
						new CCheckBox("all_operationsystem", false, "checkAll(\""+osForm.getName()+"\", \"all_operationsystem\", \"operationsystem\");"),
						name,
						"所属机房"
						));
				
				Map resultSingle = null;
				for (int i = 0; i < resultData.size(); i++) {
					resultSingle =  resultData.get(i);
					CArray<Map> parm=new CArray<Map>();
					parm.put("type", "MOTOR_ROOM");
					parm.put("dkey",resultSingle.get("seq_no"));
					List<Map> list= new SystemDAO(executor).doSystem(parm);
					String room = "";
					if(list != null && list.size() != 0){						
						room=list.get(0).get("dlabel").toString();
					}
					osTable.addRow(array(
						new CCheckBox("operationsystem["+resultSingle.get("id")+"]", false, null, Nest.value(resultSingle,"id").asInteger()),
//						dkey,
						new CLink(resultSingle.get("dlabel"), "?form=edit&id="+resultSingle.get("id")+"&dlabel="+resultSingle.get("dlabel")),
						room
					));
					arraydata.put(resultSingle);
				}
			}else{
				osTable.setHeader(array(
						new CCheckBox("all_operationsystem", false, "checkAll(\""+osForm.getName()+"\", \"all_operationsystem\", \"operationsystem\");"),
						name
						));	
				Map resultSingle = null;
				for (int i = 0; i < resultData.size(); i++) {
					resultSingle =  resultData.get(i);
					CArray<Map> parm=new CArray<Map>();
					osTable.addRow(array(
						new CCheckBox("operationsystem["+resultSingle.get("id")+"]", false, null, Nest.value(resultSingle,"id").asInteger()),
//						dkey,
						new CLink(resultSingle.get("dlabel"), "?form=edit&id="+resultSingle.get("id")+"&dlabel="+resultSingle.get("dlabel")+"&actionType="+actionType)			
					));
					arraydata.put(resultSingle);
				}
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
	private void doSaveAction(final SQLExecutor executor ,String actionType) {
		String checkFiledMessage = "";
		Map operationsystem=new HashMap();
		if(actionType.equals("Cabinet")){
			  operationsystem = map("id", Nest.value(_REQUEST,"id").$(),
									"dlabel", Nest.value(_REQUEST,"dlabel").$(),
									"seq_no", Nest.value(_REQUEST,"seq_no").asInteger(),
									"type","CABINET");
			  if(empty(Nest.value(operationsystem,"seq_no").$()))
				  checkFiledMessage = _("The room is not empty");
		}
		if(actionType.equals("DEPT")){
			  operationsystem = map("id", Nest.value(_REQUEST,"id").$(),
									"dlabel", Nest.value(_REQUEST,"dlabel").$(),
									"type","DEPT");
		}
		if(actionType.equals("firm")){
			  operationsystem = map("id", Nest.value(_REQUEST,"id").$(),
									"dlabel", Nest.value(_REQUEST,"dlabel").$(),
									"type","FIRM");
		}
		if(actionType.equals("mRoom")){
			  operationsystem = map("id", Nest.value(_REQUEST,"id").$(),
									"dlabel", Nest.value(_REQUEST,"dlabel").$(),
									"type","MOTOR_ROOM");
		}
		//执行数据库操作——开始
		DBstart(executor);
		boolean result = false;
		String msgOk=null,msgFail=null;
		Object[] obj = null;
		//保存数据
		if (isset(_REQUEST,"id")) {
			if(actionType.equals("Cabinet")){
				obj = new SystemDAO(executor).doUpdate(operationsystem);
			}else{
				obj = new SystemDAO(executor).doUpdatetwo(operationsystem);
			}
			
			msgOk = _("updataSuccess");
			msgFail =  _("updataAire");
		} else {
			msgOk =  _("addSuccess");
			msgFail =  _("addAire");	
			if(actionType.equals("Cabinet")){
				if(empty(checkFiledMessage))
					obj = new SystemDAO(executor).doAddCatinet(operationsystem);
			}
			if(actionType.equals("mRoom")){
				obj = new SystemDAO(executor).doAddRoom(operationsystem);
			}
			if(actionType.equals("DEPT")||actionType.equals("firm")){
				obj = new SystemDAO(executor).doAdd(operationsystem);
			}
		}
//		result = !empty(obj[0]);
		if(empty(checkFiledMessage)){
			result = obj[0]!=null;
			if(obj[1].toString().equals("名称重复")){
				msgFail="名称重复";
			}
		}
		//执行数据库操作——结束
		result = DBend(executor, result);
		
		//显示操作结果提示
		if(!empty(checkFiledMessage))
			msgFail = checkFiledMessage;
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
	private void doDeleteAction(final SQLExecutor executor,Long [] osIds ) {
        String  success= _("delectSuccess");
        String  faile= _("delectAire");
        Boolean panduan=false;
		//准备数据
        if(actionType.equals("mRoom")){
        	List<Long> oslist = new ArrayList<Long>();
    		//final Long[] osIds = Nest.array(_REQUEST,"operationsystem").asLong();
    		for (int i = 0; i < osIds.length; i++) {
    			Long osid = osIds[i];
    			//查出机房信息
    			CArray<Map> cha=new CArray<Map>();
    			cha.put("id", osid);
    			List<Map> list= new SystemDAO(executor).doSystem(cha);
    			//根据机房查询是否有机柜
    			CArray<Map> chaTwo=new CArray<Map>();
    			chaTwo.put("type", "CABINET");
    			chaTwo.put("seq_no", list.get(0).get("dkey").toString());
    			List<Map> list1= new SystemDAO(executor).doSystem(chaTwo);
    			if(list1.size()>0){
    				panduan=true;
    				faile=list.get(0).get("dlabel").toString()+"含有机柜不能删除";
    				
    			}else{
    				oslist.add(osid);
    			}
    		}
    		if(panduan){
    			show_messages(true,faile);		
    		}else{	
        		delete(executor,oslist,success,faile);
    		}
        } else if(actionType.equals("Cabinet")){

        	List<Long> oslist = new ArrayList<Long>();
    	//	final Long[] osIds = Nest.array(_REQUEST,"operationsystem").asLong();
    		for (int i = 0; i < osIds.length; i++) {
    			Long osid = osIds[i];
    			//查出资产信息
    			CArray<Map> cha=new CArray<Map>();
    			cha.put("id", osid);
    			List<Map> list= new SystemDAO(executor).doSystem(cha);
    			CArray<Map> cha1=new CArray<Map>();
    			cha1.put("url_a", list.get(0).get("dkey").toString());
    			List<Map> list2= new SystemDAO(executor).existSystemhtintery(cha1);
    			if(list2.size()>0){
    				panduan=true;
    				faile=list.get(0).get("dlabel").toString()+"含有设备不能删除";
    				
    			}else{
    				oslist.add(osid);
    			}
    		}
    		if(panduan){
    			show_messages(true,faile);		
    		}else{	
        		delete(executor,oslist,success,faile);
    		}
        
        } else{
        	List<Long> oslist = new ArrayList<Long>();
    	//	final Long[] osIds = Nest.array(_REQUEST,"operationsystem").asLong();
    		for (int i = 0; i < osIds.length; i++) {
    			Long osid = osIds[i];
    			oslist.add(osid);
    		}
    		delete(executor,oslist,success,faile);
        }
	
	}
	private void delete(final SQLExecutor executor,List oslist,String success,String faile) {
		Map<String, List> paramMap = new HashMap<String, List>();
		paramMap.put("oslist", oslist);
		
		//执行数据库操作——开始
		DBstart(executor);
		
		//删除数据
		boolean goResult = new SystemWordbook().doDelete(paramMap);
		
		//执行数据库操作——结束
		goResult = DBend(executor, goResult);	
		
		//显示结果提示
		show_messages(goResult, success,faile);	
		
		//清空
		clearCookies(goResult);
		
	}
}
