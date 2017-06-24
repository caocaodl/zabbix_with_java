package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NOT_ZERO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicator;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.isoft.biz.daoimpl.tentant.THostDAO;
import com.isoft.biz.daoimpl.tentant.TentantTemplateDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
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

public class TentantItemsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("title", _("Configuration of templates"));
		page("file", "tentantitems.action");
	}

	@Override
	protected boolean doAjax(SQLExecutor arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor arg0) {
		CArray fields = map(
				"hostid",									array(T_RDA_INT, O_OPT, P_SYS,	DB_ID+NOT_ZERO, null),
				"itemid",									array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
				"name",									array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, null, _("Name")),
				"description",							array(T_RDA_STR, O_OPT, null,	null,		null),
				"key",										array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Key")),
				"status",									array(T_RDA_INT, O_OPT, null,	null, null),
				"username",								array(T_RDA_STR, O_OPT, null,	null),
				"password",								array(T_RDA_STR, O_OPT, null,	null,null),
				"units",										array(T_RDA_STR, O_OPT, null,	null,		null),
				"visible",									array(T_RDA_STR, O_OPT, null,		null,		null),
				"applications",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
				"new_application",					array(T_RDA_STR, O_OPT, null,	null,		null),
				// actions
				"go",											array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"save",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"update",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
				"cancel",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"form",										array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"form_refresh",							array(T_RDA_INT, O_OPT, null,	null,		null),
				// ajax
				"favobj",									array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
				"favref",									array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
				"favstate",								array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}")
			);
			check_fields(getIdentityBean(), fields);

	}

	@Override
	protected void doPermissions(SQLExecutor arg0) {
	}
	
	@Override
	protected void doAction(final SQLExecutor executor) {
		CWidget hostsWidget = new CWidget();//定义当前页
		TentantTemplateDAO idao=new TentantTemplateDAO(executor);//通过sql接口操作数据库的dao层
		THostDAO hostdao= new THostDAO(this.getIdBean(), executor);
		boolean result = false;
		if(isset(_REQUEST,"go")){//更新监控指标状态
			try {	
				DBstart(executor);
				//更新监控指标状态
				Long hostid=Nest.value(_REQUEST, "hostid").asLong();
				Long itemid=Nest.value(_REQUEST, "group_itemid").asLong();
				int updatesttus;
				if("disable".equals(Nest.value(_REQUEST, "go").asString())){
					updatesttus=1;
				}else{
					updatesttus=0;
				}		
				Map item = map("status",updatesttus,
						 "itemid", itemid);
				if(idao.updateViceItemStatus(item)==0){
					idao.updateItemStatus(item);
				}
				//状态变更后，审批状态变为未提交
				hostdao.updateProcessStatus(hostid, 0);
				
				DBend(executor, true);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
			}
		}else if(isset(_REQUEST,"save")){//保存方法
			String msgOk= null, msgFail = null;
			try {	
				DBstart(executor);
				//更新监控指标，如果是第一次则创建，如果第N次，则将监控指标更新
				Map item = map(
						"name", get_request("name"),
						"hostid", get_request("hostid"),
						"key_", get_request("key"),
						"status", get_request("status", ITEM_STATUS_DISABLED),
						"units", get_request("units")
					);
			    Long itemid=Nest.as(get_request("itemid")).asLong();
			    Map paraMap = map("itemid", get_request("itemid"));
			    List paralist=idao.isHasRelaseItemid(paraMap);
			    if(paralist.size()!=0){//不为零，说明该版本已经存在，直接更新即可
			    	Nest.value(item,"itemid").$(itemid);
			    	idao.UpdateItem(item);//在本次发布流程中，实时更新只更新副表中数据
			    }else{//为零，说明该记录之前还没有被更改过
			    	String GET_ITEM = " select  it.itemid,it.hostid,it.name, it.key_, it.units,it.status  "
		                    +" from i_tenant_main_items it where it.itemid=#{itemid} ";
					Map params = new HashMap();
					params.put("itemid",itemid);
					CArray<Map> items = DBselect(executor, GET_ITEM,params);
				    
				    for(Map it: items){//将上一版本的发布监控指标保存到副表中去，以relaseitemid关联，即该字段不为零
				    	Map paramap=new HashMap();
				    	paramap.put("itemid", Nest.value(it,"itemid").asInteger()*10+1);
						paramap.put("name", Nest.value(it,"name").asString());
						paramap.put("hostid", Nest.value(it,"hostid").asString());
						paramap.put("key_", Nest.value(it,"key_").asString());
						paramap.put("units", Nest.value(it,"units").asString());
						paramap.put("status", Nest.value(it,"status").asString());
						paramap.put("relaseitemid", Nest.value(it,"itemid").asString());
						idao.AddItem(paramap);//保存原来的监控指标
				    }
				    Map itmap = map(
				    		"itemid" ,itemid,
							"name", get_request("name"),
							"hostid", get_request("hostid"),
							"key_", get_request("key"),
							"status", get_request("status", ITEM_STATUS_DISABLED),
							"units", get_request("units"),
							"relaseitemid",0
						);
			    	idao.AddItem(itmap);//在本次发布流程中，创建需要实时更新记录
			    }
			    
			    //改变审批状态
			    Long templateId = Nest.as(get_request("hostid", 0L)).asLong();
			    hostdao.updateProcessStatus(templateId, 0);
				
				DBend(executor, true);
				unset(_REQUEST,"form");
				msgOk="指标保存成功";
				msgFail="指标保存失败";
				show_messages(true, msgOk, msgFail);
			}catch (Exception e) {
				DBend(executor, false);
				e.printStackTrace();
				show_messages(false, msgOk, msgFail);
			}
		}
		
		if(isset(_REQUEST,"form")){//跳转到编辑页面
			String GET_ITEM = " select  it.itemid,it.hostid,it.name, it.key_, it.units,it.status  "
                    +" from i_tenant_main_items it where it.itemid=#{itemid} ";
			Map params = new HashMap();
			params.put("itemid",Nest.value(_REQUEST,"itemid").asLong());
			CArray<Map> datas = DBselect(executor, GET_ITEM,params);
			Map data=map();
			for(Map item:datas) {
				data=item;
			}
			// 渲染视图
			CView itemView = new CView("configuration.tentantitem.edit", data);
			hostsWidget.addItem(itemView.render(getIdentityBean(), executor));
		}else{
			CForm form = new CForm();//定义显示数据表单
			form.setName("hosts");
			
			CToolBar tb = new CToolBar(form);
			tb.addSubmit("form", "创建监控指标","","orange create");
			
			CArray<CComboItem> goComboBox = array();
			CComboItem goOption = new CComboItem("export", _("Export selected"));
			goOption.setAttribute("class", "orange export");
			goComboBox.add(goOption);
			
			goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm", _("Delete selected items?"));
			goOption.setAttribute("class", "orange delete");
			goComboBox.add(goOption);
			
			tb.addComboBox(goComboBox);
			
			rda_add_post_js("chkbxRange.pageGoName = \"group_itemid\";");
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			hostsWidget.addItem(headerActions);
			
			CTableInfo table = new CTableInfo("没有发现监控指标");//定义指标列表
			table.setHeader(getHeader(form));//设置表头
			
			String GET_ITEMS = " select  it.itemid,it.hostid,it.name, it.key_, it.units,it.status,it.description "
                                  +" from i_tenant_main_items it where it.hostid=#{hostid} ";
			Map params = new HashMap();
			params.put("hostid",Nest.value(_REQUEST,"hostid").asLong());
	        CArray<Map> items = DBselect(executor, GET_ITEMS,params);
			
			order_result(items, "itemid", "asc");
			CTable paging = getPagingLine(getIdentityBean(), executor, items, array("itemid"));
			
			for(Map item:items){
				CArray names=array();;
				CArray keys=array();;
				CArray units=array();;
				CArray descriptions = array();
				       descriptions.add(Nest.value(item,"description").asString());;
				int statusint=0;
				boolean stattusboolen=false;
				List isItemid=idao.isHasViceItemid(Nest.value(item,"itemid").asLong());
				if(isItemid.size() !=0){//不为零，主表和副表都相同的itemid记录，就显示副表中记录
					List<Map> itemlist=idao.getOneViceItem(Nest.value(item,"itemid").asLong());
					for(Map it : itemlist){
						 names.add(new CLink(CHtml.encode(Nest.value(it,"name").asString()),
					    		   "?form=update&hostid="+Nest.value(it,"hostid").$()+"&itemid="+Nest.value(it,"itemid").$()));
						 keys.add(Nest.value(it,"key_").asString());
						 units.add(Nest.value(it,"units").asString());
						 statusint=Nest.value(it,"status").asInteger();
						 stattusboolen = Nest.value(it,"status").asBoolean();
					}
				}else{//反之则显示主表中记录
					 names.add(new CLink(CHtml.encode(Nest.value(item,"name").asString()),
				    		   "?form=update&hostid="+Nest.value(item,"hostid").$()+"&itemid="+Nest.value(item,"itemid").$()));
					 keys.add(Nest.value(item,"key_").asString());
					 units.add(Nest.value(item,"units").asString());
					 statusint=Nest.value(item,"status").asInteger();
					 stattusboolen = Nest.value(item,"status").asBoolean();
				}
				
				CCol status = new CCol(new CDiv(new CLink(
						itemIndicator(statusint, Nest.value(item,"state").asInteger()),
						"?group_itemid="+Nest.value(item,"itemid").$()+"&hostid="+Nest.value(item,"hostid").$()+"&go="+(stattusboolen ? "activate" : "disable"),
						itemIndicatorStyle(statusint, Nest.value(item,"state").asInteger())
				), "switch"));
				
				CCheckBox checkBox = new CCheckBox("group_itemid["+Nest.value(item,"itemid").$()+"]", false, null, Nest.value(item,"itemid").asInteger());
				checkBox.setEnabled(empty(Nest.value(item,"discoveryRule").$()));
				table.addRow(array(
						checkBox,
						//appnames,//监控维度
						names,//指标名称
						keys,//键值
						units,//单位
						descriptions,//描述
						status//状态
						));
			}
			form.addItem(array(table, paging));
			hostsWidget.addItem(form);
		}
		hostsWidget.show();
	}
	
	/**设备列表表头
	 * @param form
	 * @return
	 */
	protected CArray getHeader(CForm form){
		return array(
				new CCheckBox("all_hosts", false, "checkAll(\""+form.getName()+"\", \"all_hosts\", \"group_itemid\");"),
				//"监控维度",
				"指标名称",
				"键值",
				"单位",
				"描述",
				"状态"
			);
	}

}
