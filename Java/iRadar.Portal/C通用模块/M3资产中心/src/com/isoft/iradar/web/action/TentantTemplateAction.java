package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.tentant.THostDAO;
import com.isoft.biz.daoimpl.tentant.TentantTemplateDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TentantTemplateAction extends RadarBaseAction {

	
	@Override
	protected void doInitPage() {
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("title", _("Configuration of templates"));
		page("file", "tentantTemplate.action");
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray fields=map(
				"processStatus",									array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
				"itemid",								     	array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
				"groupid",					                    array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
				"templateid",			                        array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
				"template_name",	     	                    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Template name")),
				"note",	     	                                array(T_RDA_STR, O_OPT, null,	null, null, null),
				//按钮操作部分
				"go",											array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"save",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"agree",											array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"reject",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"submit",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"reset",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"cancel",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"form",										array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"filter_set",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null)
				);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		
	}
	
	@Override
	protected void doAction(SQLExecutor executor) {
		CWidget hostsWidget = new CWidget();//定义租户模型界面
		final TentantTemplateDAO idao=new TentantTemplateDAO(executor);
		final THostDAO hostdao= new THostDAO(this.getIdBean(), executor);
		Map usermap=CWebUser.data();//获取当前用户信息
		if(isset(_REQUEST,"agree")){//同意操作
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();//获取监控模型id
				/********发布租户模型***********/
				CArray<Map> vicehostlist=hostdao.getViceHostNumTwo(templateId, 0);//获取副表中模型数据
				for(Map host : vicehostlist){
					String GET_TEMPLATE = "select tem.hostid as templateid,tem.host as name ,tem.process_status "
							+ " from i_tenant_template tem  where tem.status=3 and tem.hostid=#{hostid}";
					Map params = new HashMap();
					params.put("hostid",templateId);
					CArray<Map> templates= DBselect(executor, GET_TEMPLATE, params);
					
					for(Map tem: templates){
						Map template=map("name",Nest.value(tem, "name").asString(),
								"flag",1,//1表示删除
								"hostid",templateId
								);
						hostdao.updateViceHost(template);//将主表数据更新到副表
					}
					Map templatemap=map("host",Nest.value(host, "name").asString(),
							"templateid",templateId
							);
					Map param = new HashMap();
					param.put("host",Nest.value(host, "name").asString());
					param.put("hostid",templateId);
					int updatenum=hostdao.updateMainTemplate(param);//将副表数据更新到主表
				}
				
				/********发布监控指标部分***********/
				Map param=new HashMap();
				param.put("hostid", templateId);
				List<Map> beforeitemlist=idao.getBeforeItem(param);//获取副表中保存发布前的数据
				for(Map items: beforeitemlist){
					Long itemid=Nest.value(items, "itemid").asLong();
					String GET_ITEM = " select  it.itemid,it.hostid,it.name, it.key_, it.units,it.status  "
		                    +" from i_tenant_main_items it where it.itemid=#{itemid} ";
					Map params = new HashMap();
					params.put("itemid",itemid);
					CArray<Map> itemscarray = DBselect(executor, GET_ITEM,params);
				    
				    for(Map it: itemscarray){
				    	Map paramap=new HashMap();
						paramap.put("name", Nest.value(it,"name").asString());
						paramap.put("hostid", Nest.value(it,"hostid").asString());
						paramap.put("key_", Nest.value(it,"key_").asString());
						paramap.put("units", Nest.value(it,"units").asString());
						paramap.put("status", Nest.value(it,"status").asString());
						paramap.put("itemid", Nest.value(it,"itemid").asString());
						//将主表原来的item更新到副表，方便以后重置，在解决连续发布情况下，数据是上上版本的问题
						int numb=idao.updateViceBeforeItem(paramap);
				    }
				}
				
				//将副监控表对应item更新到主表中，同时删除副表原来的item
				Map para=new HashMap();
				para.put("hostid", templateId);
				para.put("relaseitemid", 0);
				para.put("flags", 0);
				List<Map> itemlist=idao.getUpdateItem(para);
				for(Map items: itemlist){
					int itemid=Nest.value(items, "itemid").asInteger();
					items.remove("itemid");//删除原来的itemid，然后添加itemid，保证参数跟查询sql参数未知的一致
					items.put("itemid", itemid);
					
					int num=idao.updateMainItem(items);//更新监控主表
					Map pa=new HashMap();
					pa.put("itemid", itemid);
					int deltenum=idao.deleteViceItem(pa);//删除原来的更新记录
				}
				//将监控维度副表数据删除，此处删除为将状态改为1
				
				//保存同意审批流程
				String note = get_request("note", "");//获取审批备注
				hostdao.createRemark(templateId,usermap,"同意发布",note);
				
				//变更审批状态，0表示未提交，1表示审批中，2表示已发布
				hostdao.updateProcessStatus(templateId,2);
				
				DBend(executor, true);
				msgOk = "审批成功";
				msgFail = "审批失败";
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}else if(isset(_REQUEST,"reject")){//驳回操作
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();
				//保存驳回审批流程
				String note = get_request("note", "");
				hostdao.createRemark(templateId,usermap,"驳回重改",note);
				//变更审批状态0表示未提交，1表示审批中，2表示已发布
				hostdao.updateProcessStatus(templateId,0);
				
				DBend(executor, true);
				msgOk = "驳回成功";
				msgFail = "驳回失败";
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}else if(isset(_REQUEST,"submit")){//提交操作
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();
				//变更审批状态，0表示未提交，1表示审批中，2表示已发布
				hostdao.updateProcessStatus(templateId,1);
				
				DBend(executor, true);
				msgOk = "提交成功";
				msgFail = "提交失败";
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}else if(isset(_REQUEST,"save")){//保存提交
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();
				String templateName = get_request("template_name", "");
				if(!empty(templateId)){//如果不为空，则更新，反之，则是保存
					int processStatus=Nest.as(get_request("processStatus", 0)).asInteger();
					String GET_VICE_HOST = " select  hostid as hostid,name as name "
					        +" from i_tenant_hosts t where t.hostid=#{hostid} ";
					Map params = new HashMap();
					params.put("hostid",templateId);
					CArray<Map> hostcarray = DBselect(executor, GET_VICE_HOST,params);
					if(hostcarray.size()==0){
						Map hosts =map("hostid",templateId,
								 "name",templateName,
								 "flag",0);
						int addHostNum=hostdao.addViceHost(hosts);//将修改的租户模型数据添加到副表
					}else{
						Map hosts =map("name",templateName,
							 "flag",1,
							 "hostid",templateId);
					   int updateHostNum=hostdao.updateViceHost(hosts);//将修改的租户模型数据更新到副表
					}
					
					//保存同意提交审批流程
					String note = get_request("note", "");
					hostdao.createRemark(templateId,usermap,"同意提交",note);
					//变更审批状态，0表示未提交，1表示审批中，2表示已发布
					hostdao.updateProcessStatus(templateId,0);
					
					DBend(executor, true);
					msgOk="租户模型更新成功";
					msgFail="租户模型更新失败";
					show_messages(true, msgOk, msgFail);
					clearCookies(true);
				}else{
					Long hostid=hostdao.getMainHostMaxid();
					Map host =map("tenantid",0,
							"hostid",hostid,
							"host",templateName,
							"status",3,
							"name",templateName);
					int addMainHostNum=idao.addMainHost(host);
					msgOk="租户模型保存成功";
					msgFail="租户模型保存失败";
					show_messages(true, msgOk, msgFail);
				}
				unset(_REQUEST,"form");
				unset(_REQUEST,"templateid");
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}else if(isset(_REQUEST,"reset")){//重置操作
			String msgOk= null, msgFail = null;
			try {
				DBstart(executor);
				Long templateId = Nest.as(get_request("templateid", 0L)).asLong();
				
				/*******************重置租户模型**********************/
				
				CArray<Map> hostcarray = hostdao.getViceHostNumTwo(templateId, 1);
				for(Map host : hostcarray){
					Map param = new HashMap();
					param.put("host",Nest.value(host, "name").asString());
					param.put("hostid",templateId);
					int updatenum=hostdao.updateMainTemplate(param);
					int deleteHostNum=hostdao.deleteViceHost(templateId);
				}
				/*******************重置监控指标**********************/
				Map paramap=new HashMap();
				paramap.put("hostid", templateId);
				List<Map> beforeitems=idao.getBeforeItem(paramap);
				
				for(Map item: beforeitems){
					idao.updateMainItem(item);
					
					Map para=new HashMap();
					para.put("relaseitemid", Nest.value(item, "itemid").asString());
					idao.deleteBeforeItem(para);
				}
				
				//保存审批流程，同时审批状态
				String note = get_request("note", "");
				hostdao.createRemark(templateId,usermap,"重置到前一版本",note);//保存重置审批流程，可以写为公共方法
				//变更审批状态，0表示未提交，1表示审批中，2表示已发布，重置后仍为发布状态
				hostdao.updateProcessStatus(templateId,2);
				
				DBend(executor, true);
				msgOk = "重置成功";
				msgFail = "重置失败";
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}
		
		if(isset(_REQUEST,"form")){//模型编辑
			Long templateId = get_request("templateid", 0L);
			CArray data = array();
			if (!empty(templateId)) {
				String GET_TEMPLATE = "select tem.hostid as templateid,tem.host as name ,tem.process_status "
						+ " from i_tenant_template tem  where tem.status=3 and tem.hostid=#{hostid}";
				Map params = new HashMap();
				params.put("hostid",templateId);
				CArray<Map> dbTemplates= DBselect(executor, GET_TEMPLATE, params);
				Nest.value(data,"dbTemplate").$(reset(dbTemplates));
			} else {
				Nest.value(data,"original_templates").$(array());
			}
			
			//获取审批备注
			String remarkstr=null;
			if(templateId!=0){
				List remarkList=hostdao.getRemark(Integer.valueOf(templateId.toString()));//获取审批备注
				if(remarkList.size()!=0){
					remarkstr=remarkList.get(0).toString();
				}
			}
			//获取审批详情，即本次发布操作流程
			if(templateId!=0){
				List limitnum=hostdao.getRemarkNum(templateId);//获取本发布流程发布的审批记录数
				DataPage datapage=new DataPage(true,  1, limitnum.size());
				List processList=hostdao.GetProcess(datapage,templateId);
				Nest.value(data,"processList").$(processList);
			}
			
			// 渲染视图，跳转到编辑页面
			CView itemView = new CView("configuration.tentanttemplate.edit", data);
			hostsWidget.addItem(itemView.render(getIdentityBean(), executor));
		}else{//列表展示
			CForm form = new CForm();//定义显示数据表单
			form.setName("tentant");
			
			CToolBar tb = new CToolBar(form);
			tb.addSubmit("form", "创建租户模型","","orange create");
			
			CTableInfo table = new CTableInfo("没有发现租户模型");//定义显示租户模型列表
			table.setHeader(getHeader(form));//设置表头
			String GET_TEMPLATES = "select tem.hostid,tem.host,tem.process_status,count(it.itemid) as items "
							+ " from i_tenant_template tem left join i_tenant_main_items it "
					        + " on tem.hostid=it.hostid "
							+ " where tem.status=3 "
							+ " group by tem.hostid,tem.host,tem.process_status ";
			CArray<Map> templates = DBselect(executor, GET_TEMPLATES);
			//页面分页
			order_result(templates, "hostid", "asc");//当前页排序
			CTable paging = getPagingLine(getIdentityBean(), executor, templates, array("hostid"));//分页
			
			for(Map template : templates) {
				CArray templatesOutput = array();
				CArray<Map> hostcarray = hostdao.getViceHostNumTwo(Nest.value(template,"hostid").asLong(), 0);
                String namestr=null;
                //模型表跟设备表是同一张表，只是状态不同，模型状态为3
                if(hostcarray.size()!=0){
                	for(Map host:hostcarray){//如果该模型名称没有更新或者已发布，则显示主表数据，如果正在审批中，则显示副表数据
                		namestr=Nest.value(host,"name").asString();
                	}
                }else{
                	namestr=Nest.value(template,"host").asString();
                }
				templatesOutput.add(new CLink(namestr, "tentantTemplate.action?form=update&templateid="+Nest.value(template,"hostid").$()));
				
				int hostid=Nest.value(template,"hostid").asInteger();
				String remarkstr=null;
				//int newappnum=0;
				if(hostid!=0){
					List remarkList=hostdao.getRemark(hostid);//获取审批备注列表
					if(remarkList.size()!=0){
						remarkstr=remarkList.get(0).toString();
					}
					/*List applist=idao.getNewappNum(hostid);//获取在新表里的监控维度数目
					newappnum = applist.size();*/
				}
				//监控指标
				CArray items = array(new CLink(_("Items"), "tentantitems.action?filter_set=1&hostid="+Nest.value(template,"hostid").asString()),
						" ("+Nest.value(template,"items").$()+")");
				//审批备注
                CCol remarks = new CCol(remarkstr);
				//审批状态
				CCol statuss = new CCol(getStateStr(Nest.value(template,"process_status").asInteger()));
				
				table.addRow(array(
						new CCheckBox("templates["+Nest.value(template,"templateid").$()+"]", false, null, Nest.value(template,"templateid").asInteger()),
						templatesOutput,//模型名称
						items,//监控指标
						remarks,//审批备注
						statuss//审批状态
					));
			}
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			hostsWidget.addItem(headerActions);
			
			form.addItem(array(table, paging));
			hostsWidget.addItem(form);
		}
		hostsWidget.show();

	}
	
	/**设备租户模型表头
	 * @param form
	 * @return
	 */
	protected CArray getHeader(CForm form){
		return array(
				new CCheckBox("all_hosts", false, "checkAll(\""+form.getName()+"\", \"all_hosts\", \"hosts\");"),
				"模型名称",
				"监控指标",
				"审批备注",
				"审批状态"
			);
	}
	
	/**更新审批状态
	 * @param state
	 * @return
	 */
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
	
	
	
	


}
