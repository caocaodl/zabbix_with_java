package com.isoft.biz.web.platform.topo;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
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

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.daoimpl.platform.topo.TopoDAO;
import com.isoft.biz.vo.platform.topo.Topo;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
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
/**
 * 拓扑中心
 * @author guzhaohui
 *
 */
public class TopoAction extends RadarBaseAction {
	
	private Long topoId;
	
	@Override
	protected void doInitPage() {
		page("title", getPageTitle());
		page("file", getPageFile());
		page("hist_arg", new String[] {});
		page("js", new String[] {"imon/browseTopo.js"});
		page("css", new String[] {"lessor/topocenter/topo.css"});
	}
	
	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"id" ,				array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"toponame" ,		array(T_RDA_STR, O_OPT, null,	NOT_EMPTY+"{}.length<10",	"isset({save})","拓扑名称"),
			"topotype",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"is_public",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"userid",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"modified_at",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"modified_user",	array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"created_at",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"created_user",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"txtTopoName",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			// actions
			"go" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"reset" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"clone" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"filter" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form" ,			array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}
	
	@Override
	protected void doPermissions(SQLExecutor executor) {
		//获取在编辑页面删除数据时 通过url_param传过来的id参数
		topoId = get_request_asLong("id");
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
		} else if ("public".equals(Nest.value(_REQUEST,"go").asString())) {
			doPublic(executor);
		} else if ("hide".equals(Nest.value(_REQUEST,"go").asString())) {
			doHide(executor);
		}else if (isset(_REQUEST,"reset")) {
			 Nest.value(_REQUEST,"txtTopoName").$("");
		}else if (isset(_REQUEST,"filter")) {
			doListView(executor, Nest.value(_REQUEST,"txtTopoName").$());
			return;
		} else if (isset(_REQUEST,"delete") && !empty(topoId)) {
			//准备数据
			List<Long> topoIdList = new ArrayList<Long>();
			topoIdList.add(topoId);
			Map<String, List> paramMap = new HashMap<String, List>();
			paramMap.put("topoIdList", topoIdList);
			
			//执行数据库操作——开始
			DBstart(executor);
			
			//删除数据
			boolean goResult = new TopoDAO(executor).doTopoDel(paramMap);
			
			//执行数据库操作——结束
			goResult = DBend(executor, goResult);	        
			
			if (goResult) {
				unset(_REQUEST,"form");
			}
			//显示结果提示
			show_messages(goResult, getPageTitle()+"已删除", "无法删除"+getPageTitle());	
			//清空
			clearCookies(goResult);
		} 
		/* 编辑数据界面  */
		if (isset(_REQUEST,"form")) {
			Map data = map(
				"id", get_request("id"),
				"toponame", get_request("toponame"),
				"topotype",getTopoType(),
				"is_public",get_request("is_public"),
				
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0)
			);
			if(isset(_REQUEST,"id")){
				Map param = new LinkedMap();
				param.put("id", get_request("id"));
				data = new TopoDAO(executor).doTopoDataSelect(param);
				Nest.value(data, "form").$(get_request("form"));
				Nest.value(data, "form_refresh").$(get_request("form_refresh", 0));
			}
			CWidget netTopoWidget = new CWidget();
			
			// create form
			CForm TopoForm = new CForm();
			TopoForm.setName("topoForm");
			TopoForm.addVar("form", Nest.value(data,"form").$());
			if (!empty(Nest.value(data,"id").$())) {
				TopoForm.addVar("id", Nest.value(data,"id").$());
			}
			
			// create form list
			CFormList topoFormList = new CFormList("topoFormList");
			CTextBox nameTextBox = new CTextBox("toponame", Nest.value(data,"toponame").asString(), RDA_TEXTBOX_STANDARD_SIZE);
			nameTextBox.setAttribute("maxlength", "9");
			nameTextBox.setAttribute("placeholder", "输入的文本不能多于9个字符");
			topoFormList.addRow("拓扑名称", nameTextBox);
			
			CComboBox isPublicBox = new CComboBox("is_public", Nest.value(data,"is_public").asString());
			isPublicBox.addItems((CArray)map(
				"Y", "公开",
				"N", "不公开"
			));
			topoFormList.addRow("是否公开", isPublicBox);
			
			// append tabs to form
			CTabView topoTab = new CTabView();
			topoTab.addTab("topoFormTab", getPageTitle(), topoFormList);
			TopoForm.addItem(topoTab);

			// append buttons to form
			if (!empty(Nest.value(data,"id").$())) {
				TopoForm.addItem(
					makeFormFooter(
						new CSubmit("save", _("Save")),
						array(
							new CButtonDelete("删除该"+getPageTitle()+"？", url_param(getIdentityBean(), "form")+url_param(getIdentityBean(), "id")+url_param(getIdentityBean(), "config")),
							new CButtonCancel(url_param(getIdentityBean(), "config"))
						)
				));
			} else {
				TopoForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					new CButtonCancel(url_param(getIdentityBean(), "config"))
				));
			}
			// append form to widget
			netTopoWidget.addItem(TopoForm);
			netTopoWidget.show();
		} else {
			doListView(executor,null);
		}
	}
	/**
	 * 获取列表展示数据
	 * @param executor
	 * @param topoName 拓扑名称，用于检索条件
	 */
	private void doListView(final SQLExecutor executor,Object topoName) {
		CWidget topoWidget = new CWidget();
		
		CForm topoForm = new CForm();
		topoForm.setName("topoForm");
		
		CToolBar tb = new CToolBar(topoForm);
		tb.addSubmit("form", "添加拓扑", "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		
		CComboItem goOption = new CComboItem("public", "公开拓扑");
		goOption.setAttribute("confirm", "确认公开所选的拓扑?");
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("hide", "隐蔽拓扑");
		goOption.setAttribute("confirm", "确认隐蔽所选的拓扑?");
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("delete", "删除拓扑");
		goOption.setAttribute("confirm", "确认删除所选的拓扑?");
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"topo\";");
		
		CForm frmFilter = new CForm("get");
		frmFilter.setAttribute("name", "frmfilter");
		CTextBox txtTopoName = new CTextBox("txtTopoName",Nest.value(_REQUEST,"txtTopoName").asString());
		CSubmit filter = new CSubmit("filter", _("GoFilter"));
		CSubmit reset = new CSubmit("reset",_("Reset"));
		frmFilter.addItem(array("拓扑名称：",txtTopoName,filter,reset));
		topoWidget.addHeader(frmFilter);
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		topoWidget.addItem(headerActions);
		Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
		int aa=Nest.value(config,"search_limit").asInteger();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("topoType", getTopoType());
		paramMap.put("limitee", aa);
		if(!"".equals(topoName)){
			paramMap.put("topoName",topoName);
		}
		List<Topo> resultData =  new TopoDAO(executor).doTopoList(paramMap);
		
		CTableInfo topoTable = new CTableInfo("没有发现拓扑");
		topoTable.setHeader(array(
				new CCheckBox("all_topo", false, "checkAll(\""+topoForm.getName()+"\", \"all_topo\", \"topo\");"),
				"拓扑名称",
				"是否公开",
				"修改时间",
				"修改人",
				"创建时间",
				"创建人",
				"操作"
				));
		CArray arraydata = new CArray();
		Topo resultSingle = null;
		
		CLink link = new CLink("浏览拓扑");
		for (int i = 0; i < resultData.size(); i++) {
			resultSingle =  resultData.get(i);
			link.setAttribute("onclick", "setUrl('"+getTopoType()+"',"+resultSingle.getId()+",'"+resultSingle.getTopoName()+"')");
			topoTable.addRow(
					array(
							new CCheckBox("topo["+resultSingle.getId()+"]", false, null),
							new CLink(resultSingle.getTopoName(), "?form=edit&id="+resultSingle.getId()+"&toponame="+resultSingle.getTopoName()+"&topotype="+getTopoType()+"&is_public="+resultSingle.getIsPublic()),
							"Y".equals(resultSingle.getIsPublic()) ? "公开" : "不公开",
							resultSingle.getModifiedAt(),
							resultSingle.getModifiedUser(),
							resultSingle.getCreatedAt(),
							resultSingle.getCreatedUser() != null ? resultSingle.getCreatedUser() : "",
							link
						)
					);
			arraydata.put(resultSingle);
		}
		CTable paging = getPagingLine(getIdentityBean(),executor,arraydata);
		topoForm.addItem(array(topoTable,paging));
		topoWidget.addItem(topoForm);
		topoWidget.show();
	}
	/**
	 * 保存数据
	 * @param executor
	 */
	private void doSaveAction(final SQLExecutor executor) {
		
		//准备数据
		final Map topoMap = map(
				"id",Nest.value(_REQUEST,"id").$(),
				"topoName", Nest.value(_REQUEST,"toponame").$(),
				"topoType", getTopoType(),
				"isPublic", Nest.value(_REQUEST,"is_public").$(),
				"username", getIdentityBean().getUserName(),
				"userId", getIdentityBean().getUserId(),
				"tenantId", getIdentityBean().getTenantId()
			);
		
		//执行数据库操作——开始
		DBstart(executor);
		boolean result;
		String msgFail = getPageTitle()+_("save Failure");
		String msgOk = getPageTitle()+_("save Success");
		Object[] obj = null;
		//保存数据
		if (isset(_REQUEST,"id")) {
			obj = new TopoDAO(executor).doTopoUpdate(topoMap);
		} else {
			obj = new TopoDAO(executor).doTopoAdd(topoMap);
		}
		//同名判断
		if(obj != null && obj.length>1){
			if("duplicateName".equals(obj[1])){
				msgFail += ","+_("name duplicate");
			}
		}
		result = !empty(obj[0]);
		
		//执行数据库操作——结束
		result = DBend(executor, result);
		
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
		
		//准备数据
		List<Long> topoIdList = new ArrayList<Long>();
		final Long[] topoIds = Nest.array(_REQUEST,"topo").asLong();
		for (int i = 0; i < topoIds.length; i++) {
			Long topoId = topoIds[i];
			topoIdList.add(topoId);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("topoIdList", topoIdList);
		paramMap.put("userId", getIdentityBean().getUserId());
		paramMap.put("tenantId", getIdentityBean().getTenantId());
		
		//执行数据库操作——开始
		DBstart(executor);
		
		//删除数据
		boolean goResult = new TopoDAO(executor).doTopoDel(paramMap);
		//删除与Topo相关的节点和链路
		if(goResult){
			for(long topoId:topoIdList){
				CFavorite.remove(getIdentityBean(), executor, "web.favorite.sysmapids",topoId);
			}
			delNodeByTopoId(executor,topoIdList);
		}
		//执行数据库操作——结束
		goResult = DBend(executor, goResult);	
		
		//显示结果提示
		show_messages(goResult, getPageTitle()+"已删除", "无法删除"+getPageTitle());	
		
		//清空
		clearCookies(goResult);
	}
	
	/**
	 * 公开数据
	 * @param executor
	 */
	private void doPublic(final SQLExecutor executor) {
		
		//准备数据
		List<Long> topoIdList = new ArrayList<Long>();
		final Long[] topoIds = Nest.array(_REQUEST,"topo").asLong();
		for (int i = 0; i < topoIds.length; i++) {
			Long topoId = topoIds[i];
			topoIdList.add(topoId);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();	
		paramMap.put("topoIdList", topoIdList);
		paramMap.put("userName", getIdentityBean().getUserName());
		
		boolean is_public = this.isPublic(executor, topoIdList,true);
		if(is_public){
			show_messages(true,"拓扑已公开，不能重复公开!");	
		}else{			
			//执行数据库操作——开始
			DBstart(executor);
			//公开数据
			boolean goResult = new TopoDAO(executor).doTopoPublic(paramMap);
			//执行数据库操作——结束
			goResult = DBend(executor, goResult);	
			//显示结果提示
			show_messages(goResult, getPageTitle()+"已公开", "无法公开"+getPageTitle());	
			//清空
			clearCookies(goResult);
		}
	}
	
	/**
	 * 隐藏数据
	 * @param executor
	 */
	private void doHide(final SQLExecutor executor) {
		
		//准备数据
		List<Long> topoIdList = new ArrayList<Long>();
		final Long[] topoIds = Nest.array(_REQUEST,"topo").asLong();
		for (int i = 0; i < topoIds.length; i++) {
			Long topoId = topoIds[i];
			topoIdList.add(topoId);
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("topoIdList", topoIdList);
		paramMap.put("userName", getIdentityBean().getUserName());
		
		boolean is_public = this.isPublic(executor, topoIdList,false);
		if(is_public){
			show_messages(true,"拓扑已隐蔽，不能重复隐蔽!");	
		}else{
			//执行数据库操作——开始
			DBstart(executor);
			//隐藏数据
			boolean goResult = new TopoDAO(executor).doTopoHide(paramMap);
			//执行数据库操作——结束
			goResult = DBend(executor, goResult);	
			//显示结果提示
			show_messages(goResult, getPageTitle()+"已隐蔽", "无法隐蔽"+getPageTitle());	
			//清空
			clearCookies(goResult);
		}
	}
	
	/**
	 * 判断拓扑是否已公开
	 * @param executor
	 * @param idBean
	 * @param list
	 * @return
	 */
	private boolean isPublic(SQLExecutor executor,List<Long> list,boolean isPublic){
		boolean result = false;
		String rst = null;
		if(list.size() == 1 && isPublic){	//如果只针对某一拓扑图进行公开操作,则判断其状态是否已经公开
			rst = new TopoDAO(executor).doQueryIsPublic(list.get(0).toString());
			if(rst != null && rst.equals("Y")){
				result = true;
			}
		}if(list.size() == 1 && !isPublic){	//如果只针对某一拓扑图进行隐藏操作,则判断其状态是否已经隐藏
			rst = new TopoDAO(executor).doQueryIsPublic(list.get(0).toString());
			if(rst != null && rst.equals("N")){
				result = true;
			}
		}else if(list.size() > 1 && isPublic){ //如果针对多个拓扑 只要有一个拓扑是隐藏的 就能成功执行公开
			for(Long topoid:list){
				rst = new TopoDAO(executor).doQueryIsPublic(topoid.toString());
				if(rst != null && rst.equals("N")){
					result = false;
					break;
				}
				result = true;
			}
		}else if(list.size() > 1 && !isPublic){ //如果针对多个拓扑 只要有一个拓扑是公开的 就能成功执行隐藏
			for(Long topoid:list){
				rst = new TopoDAO(executor).doQueryIsPublic(topoid.toString());
				if(rst != null && rst.equals("Y")){
					result = false;
					break;
				}
				result = true;
			}
		}
		
		return result;
	}
	
	protected String getTopoType() {
		return null;
	}
	
	protected String getPageFile() {
		return null;
	}
	
	protected String getPageTitle() {
		return null;
	}
	
	protected void delNodeByTopoId(final SQLExecutor executor,List<Long> topoIds){
		
	}
}