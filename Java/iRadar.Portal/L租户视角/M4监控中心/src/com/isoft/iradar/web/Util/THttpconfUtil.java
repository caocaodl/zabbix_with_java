package com.isoft.iradar.web.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CUserDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CTable;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_RESPONSE_TIME;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_AVAilABLE_RATE;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_CONDITION_LT;
import static com.isoft.iradar.common.util.IMonConsts.T_HTTPCONF_CONDITION_GT;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;


public class THttpconfUtil {

	
	/**
	 * 修改时   拼装告警线
	 * @param httptestid   Web服务监控id
	 * @return
	 */
	public static CArray<Map> getAlertLines(SQLExecutor executor, Long httptestid){
		CArray<Map> alertLines=map();
		
		Map params=new HashMap();
		//该Web服务监控   拥有的响应时间告警线
		params.put("httptestid", httptestid);
		CArray<Map> triExps = DBselect(executor,
				" SELECT t.triggerid, t.expression, t.description, t.status"+
				" FROM `triggers` t "+
				" LEFT JOIN functions f ON f.triggerid=t.triggerid"+
				" LEFT JOIN httpstepitem si ON f.itemid=si.itemid"+
				" LEFT JOIN httpstep s ON si.httpstepid=s.httpstepid"+
				" WHERE s.httptestid=#{httptestid} AND si.type=1",
				params
			);
		
		CArray<Map> texps= rda_toHash(triExps, "triggerid");
		
		if(!triExps.isEmpty()){
			Map alertLine=null;
			for(Map item : texps){
				String expression = TvmUtil.getENDescription(item.get("description").toString());
				alertLine=new HashMap();
				
				alertLine.put("alarmLineid", item.get("triggerid"));
				alertLine.put("item_type", T_HTTPCONF_RESPONSE_TIME);
				alertLine.put("condition", getTimeConditon(expression));
				alertLine.put("timeout", getTimeTimeout(expression));
				alertLine.put("unit", "s");
				alertLine.put("retry", getTimeRetry(expression));
				alertLine.put("status", item.get("status").toString());
				
				alertLines.put(item.get("triggerid"), alertLine);
			}	
		}
		
		//该Web服务监控  拥有的当日利用率告警线		
		 triExps = DBselect(executor,
				" SELECT t.triggerid, t.expression, t.description, t.status"+
				" FROM `triggers` t "+
				" LEFT JOIN functions f ON f.triggerid=t.triggerid"+
				" LEFT JOIN httptestitem ti ON f.itemid=ti.itemid"+
				" WHERE ti.httptestid=#{httptestid} AND ti.type=3",
				params
			);		
		
		 texps= rda_toHash(triExps, "triggerid");
		 
		if(!triExps.isEmpty()){
			Map alertLine=null;
			for(Map item : texps){
				String expression = TvmUtil.getENDescription(item.get("description").toString());
				alertLine=new HashMap();
				
				alertLine.put("alarmLineid", item.get("triggerid"));
				alertLine.put("item_type", T_HTTPCONF_AVAilABLE_RATE);
				alertLine.put("condition", T_HTTPCONF_CONDITION_LT);
				alertLine.put("avbRate", getAvbTimeout(expression));
				alertLine.put("unit", "%");
				alertLine.put("retry", "_");
				alertLine.put("status", item.get("status").toString());
				
				alertLines.put(item.get("triggerid"), alertLine);
			}	
		}		 
		 
		return alertLines;
	}
	
	
	/**
	 * 响应时间    获得大于或小于
	 */
	public static int getTimeConditon(String expression){
		String con=expression.substring(expression.indexOf("\"")+1, expression.indexOf("\"")+3);
		if("gt".equals(con)){ //大于
			return T_HTTPCONF_CONDITION_GT;
		}else{
			return T_HTTPCONF_CONDITION_LT;
		}	
	}
	
	/**
	 * 响应时间    阀值
	 */
	public static String getTimeTimeout(String expression){
		String time = expression.substring(expression.indexOf("#")+3, expression.indexOf("\"")-1);
		return time;
	}
	
	/**
	 * 响应时间    重试次数
	 */
	public static String getTimeRetry(String expression){
		String retry = expression.substring(expression.indexOf("#")+1, expression.indexOf("#")+2);
		return retry;
	}
	
	/**
	 * 当日利用率  阀值
	 */
	public static String getAvbTimeout(String expression){
		String time = expression.substring(expression.indexOf("<")+1);
		Double avb =Double.parseDouble(time)*100;
		time=avb.toString();
		return time.substring(0, time.indexOf(".")); 
	}
	

	/**
	 *系统中的通知方式 
	 */
	public static CArray<Map> getSysMediaTypes(SQLExecutor executor){
		CArray<Map> mediatypes = DBselect(executor,
				" SELECT mt.mediatypeid,mt.description,mt.type " +
				" FROM media_type mt WHERE mt.tenantid='-' AND mt.status=0 ORDER BY mt.mediatypeid ASC");
		
		return mediatypes;
	}
	
	/**  对通知类型升级，变成所有监察页面通用
	 * @param executor
	 * @param httptestid
	 * @param isWeb
	 * @return
	 */
	public static CArray<Map> getUserMediatypes(SQLExecutor executor, Object httptestid,boolean isWeb){
		CArray<Map> userMediatypes = array();
		//云平台用户
		CArray<Map> users = DataDriver.getUsersOfCurTenant();
		
		//系统中的通知方式
		CArray<Map> mediatypes = getSysMediaTypes(executor);
					
		if(!empty(users)){
			for(Map user:users){
				if((Boolean) user.get("enable")){ //只取可用用户
					CArray<Map> usermtsMap=map();
					usermtsMap.put("userid", user.get("id"));
					usermtsMap.put("name", user.get("name"));	
					
					if(!empty(mediatypes)){ //封装用户的通知方式
						CArray<Map> mts=array();
						for(Map mediatype:mediatypes){
							Map mtMap = new HashMap();
							mtMap.put("mediatypeid", mediatype.get("mediatypeid"));
							//mtMap.put("description", mediatype.get("description"));
							if(httptestid !=null){ //为修改操作  设置是否要选中
								CArray usrmtids = oldUserMediatypes(executor, user.get("id").toString(), Long.valueOf(httptestid.toString()),isWeb);
								if(usrmtids.containsValue(mediatype.get("mediatypeid"))){
									mtMap.put("selected", true);
								}
							}
							mts.add(mtMap);
						}	
						usermtsMap.put("mediatypes", mts);
					}
					userMediatypes.add(usermtsMap);
				}
			}		
		}	
		return userMediatypes;
	}
	
	/**
	 * 从云平台取租户下的可用用户  并且封装用户的发送方式              修改时还要封装选择
	 */
	public static CArray<Map> getUserMediatypes(SQLExecutor executor, Object httptestid)
	{
		return getUserMediatypes(executor,httptestid,true);
	}

	public static CArray<Map> getUserExistMediatypes(IIdentityBean idBean,SQLExecutor executor, CArray<Long> triggerIds){
		CArray<Map> userMediatypes = array();
		//云平台用户
		CArray<Map> users = DataDriver.getUsersOfCurTenant();
		
		//系统中的通知方式
		CArray<Map> mediatypes = getSysMediaTypes(executor);
					
		if(!empty(users)){
			for(Map user:users){
				if((Boolean) user.get("enable")){ //只取可用用户
					CArray<Map> usermtsMap=map();
					usermtsMap.put("userid", user.get("id"));
					usermtsMap.put("name", user.get("name"));	
					
					if(!empty(mediatypes)){ //封装用户的通知方式
						CArray<Map> mts=array();
						for(Map mediatype:mediatypes){
							Map mtMap = new HashMap();
							mtMap.put("mediatypeid", mediatype.get("mediatypeid"));
							if(!empty(triggerIds)){ //为修改操作  设置是否要选中
								CArray<String> usrmtids = getExistUserMediatypes(idBean,executor, user.get("id").toString(), triggerIds);
								String meidatypeId = Nest.value(mediatype,"mediatypeid").asString();
								if(usrmtids.containsValue(meidatypeId)){
									mtMap.put("selected", true);
								}
							}
							mts.add(mtMap);
						}	
						usermtsMap.put("mediatypes", mts);
					}
					userMediatypes.add(usermtsMap);
				}
			}		
		}	
		return userMediatypes;
	}

	/**
	 * 修改时     用户原发送方式数据
	 */
	public static CArray oldUserMediatypes(SQLExecutor executor, String userid, Long objid,boolean isWeb){	
		//修改时   用户的发送方式
		Map params = new HashMap();
		params.put("userid", userid);
		String conditionsSql;
		String paramsql;
		if(isWeb){
			conditionsSql = " LEFT JOIN httptest h ON a.name=h.name";
			paramsql = " AND httptestid=#{httptestid}";
			params.put("httptestid", objid);
		}else{
			conditionsSql = " LEFT JOIN triggers t ON a.name=t.triggerid";
			paramsql=" AND t.triggerid =#{triggerid}";
			params.put("triggerid", objid);
		}
		
		CArray<Map> mediatypes = DBselect(executor,
				" SELECT o.mediatypeid"+
				" FROM opmessage o"+
				" LEFT JOIN opmessage_usr ou ON o.operationid=ou.operationid"+
				" LEFT JOIN operations op ON ou.operationid = op.operationid"+
				" LEFT JOIN actions a ON op.actionid=a.actionid"+
				conditionsSql+
				" WHERE ou.userid=#{userid}"+
				paramsql,
				params
			);	
		
		CArray usermediatypeids =  rda_objectValues(mediatypes, "mediatypeid");
		
		return usermediatypeids;	
	}
	
	public static CArray getExistUserMediatypes(IIdentityBean idBean,SQLExecutor executor,String userId,CArray<Long> triggerIds){
		CActionGet options = new CActionGet();
		options.setSelectOperations(Defines.API_OUTPUT_EXTEND);
		Map filter = map("name",triggerIds);
		options.setFilter(filter);
		CArray<Map> actions = API.Action(idBean, executor).get(options);
		CArray<String> mediatypeids = array();
		for(Map action:actions){
			CArray<Map> operations = Nest.value(action, "operations").asCArray();
			for(Map operation:operations){
				CArray<Map> opmessageUsrs = Nest.value(operation, "opmessage_usr").asCArray();
				for(Map opmessageUsr:opmessageUsrs){
					if(userId.equals(Nest.value(opmessageUsr,"userid").asString())){
						mediatypeids.add(Nest.value(operation, "opmessage","mediatypeid").asString());
					}
				}
			}
		}
		return mediatypeids;
	}
	
	/**
	 * 通知方式表头
	 */
	public static CArray getalarmHeader(CArray<Map> sysMediatypes){
		CArray carry =array();
		carry.add("");
		carry.add("");
			
		if(!empty(sysMediatypes)){
			for(Map mediatype:sysMediatypes){
				carry.put(new CDiv(mediatype.get("description"),"type_"+mediatype.get("type")));			
			}		
		}
		return carry;
	}
	
	/**
	 * 保存操作时  先判断用户是否在监控数据库中，不存在则插入
	 * 
	 * @param tenantId  租户id
	 * @param userid   用户id
	 * @param name  用户名称
	 */
	public static void doCheckUser(SQLExecutor executor, String tenantId, String userid, String name){
		Map params = new HashMap();
		params.put("userid", userid);
		params.put("tenantId", tenantId);
		
		CArray<Map> useridMap = DBselect(executor,
				" SELECT userid"+
				" FROM users"+
				" WHERE userid=#{userid}",
				params
			);
		
		if(empty(useridMap)){//不存在执行插入操作
			CUserDAO.addUser(executor, name, tenantId, userid, name, true);
		}		
	}
	
	/**
	 * 设置添加自定义告警线   表单内容
	 * @return
	 */
	public static CTable get_alarmLine_form(SQLExecutor executor, IIdentityBean idBean, CArray new_alarmLine){
		CTable tblAlarmLine = new CTable(null, "formElementTable");
		tblAlarmLine.setAttribute("id", "formElementTable");
		
		if(isset(new_alarmLine, "lineid")){
			tblAlarmLine.addRow(new CInput("hidden", "new_alarmLine[lineid]", Nest.value(new_alarmLine, "lineid").asString()));			
		}
			
		CComboBox itemType = new CComboBox("new_alarmLine[item_type]", 
				Nest.value(new_alarmLine,"item_type").$(), 
				"switchItemType('"+Nest.value(new_alarmLine,"item_type").$()+"','"+Nest.value(new_alarmLine, "lineid").$()+"')"
			);
		itemType.addItem(T_HTTPCONF_RESPONSE_TIME, "当前响应时间");
		itemType.addItem(T_HTTPCONF_AVAilABLE_RATE, "当日可用率");
	
		if(Nest.value(new_alarmLine,"item_type").asInteger() == T_HTTPCONF_RESPONSE_TIME){ //当前响应时间
			CComboBox conditionBox = new CComboBox("new_alarmLine[condition]", Nest.value(new_alarmLine,"condition").$());
			conditionBox.addItem(T_HTTPCONF_CONDITION_GT, _("GT"));
			conditionBox.addItem(T_HTTPCONF_CONDITION_LT, _("LT"));
			

			CNumericBox timeoutBox = new CNumericBox("new_alarmLine[timeout]", Nest.value(new_alarmLine,"timeout").asString(), 5);
			timeoutBox.setAttribute("maxlength", IMonConsts.T_THRESHOLD_VALUE_LENGTJ);
			//连续触发几次机告警
			CTSeverity triggernumDiv = new CTSeverity(idBean, executor, map(//触发次数
					"id", "new_alarmLine_retry",
					"name", "new_alarmLine_retry",
					"value", Nest.value(new_alarmLine,"retry").$()
				), getTriggerNumber(), false);
			
			//是否启用
			CTSeverity statusDiv = new CTSeverity(idBean, executor, map(//是否立即启用
					"id", "new_alarmLine_status",
					"name", "new_alarmLine_status",
					"value", Nest.value(new_alarmLine, "status").$()
				), getStatus(), false);	
			
			CDiv itemdecriptiondiv = new CDiv(array(itemType, conditionBox, timeoutBox), "describe_div_id");
			tblAlarmLine.addRow(array(_("Item Decription"), itemdecriptiondiv, "s"));
			tblAlarmLine.addRow(array(_("Alarm trigger several times"), triggernumDiv), "pack");	
			
			tblAlarmLine.addRow(array(_("if available."), statusDiv), "pack");
		}
		if(Nest.value(new_alarmLine,"item_type").asInteger() == T_HTTPCONF_AVAilABLE_RATE){ //当日可用率
			CComboBox conditionBox = new CComboBox("new_alarmLine[condition]", Nest.value(new_alarmLine,"condition").$());
			conditionBox.addItem(T_HTTPCONF_CONDITION_LT, _("LT"));
					
			CNumericBox avbTextBox = new CNumericBox("new_alarmLine[avbRate]", Nest.value(new_alarmLine,"avbRate").asString(), 3);
			avbTextBox.setAttribute("maxlength", IMonConsts.T_THRESHOLD_VALUE_LENGTJ);
			
			CDiv itemdecriptiondiv = new CDiv(array(itemType, conditionBox, avbTextBox), "describe_div_id");
			tblAlarmLine.addRow(array(_("Item Decription"), itemdecriptiondiv,"%"));	
			
			//是否启用
			CTSeverity statusDiv = new CTSeverity(idBean, executor, map(//是否立即启用
					"id", "new_alarmLine_status",
					"name", "new_alarmLine_status",
					"value", Nest.value(new_alarmLine, "status").$()
				), getStatus(), false);	
			//tblAlarmLine.addRow(array("是否立即开启：", new CCheckBox("new_alarmLine[status]", Nest.value(new_alarmLine, "status").asInteger()==0?true:false, null, 0)));
			tblAlarmLine.addRow(array(_("if available."), statusDiv), "pack");
		}
				
		// 保存、取消   操作
		//tblAlarmLine.addRow(new CDiv(array( new CButton("add_alarmLine", "添加", "addOpt()"), new CButton("cancel_alarmLine", "取消", "cancelOpt()")), "op_button"));
		tblAlarmLine.addRow(array("", "", "", "",new CDiv(array( 
				new CButton("save", _("Sure"), "addOpt()"), 
				new CButton("cancel", _("Cancel"), "cancelOpt()")
			), "savebtn")));
		
		return tblAlarmLine;		
	}
	
	/**
	 * 自定义告警线 列表table
	 *
	 */
	public static CTable getAlarmLineListTable(CArray<Map> alarmLines){
		CTable alarmLineTable = new CTable(_("NO found Data"), "formElementTable");
		alarmLineTable.setAttribute("id", "alarmLineList");
		alarmLineTable.addStyle("width:800px");
		alarmLineTable.setHeader(array(_("Item"),_("Condition"),_("threshold"),_("trigger Number"),_("Status"),_("Operations")));
				
		for (Entry<Object, Map> e : alarmLines.entrySet()) {
		    Object id = e.getKey();
		    Map alarmLine = e.getValue();

		    alarmLineTable.addRow(array(
				new CCol(getItemTypeTxt(alarmLine)),
				new CCol(getConditionTxt(alarmLine)),
				new CCol(getValveValue(alarmLine)),
				new CCol(getRetryNum(alarmLine)),
				Nest.value(alarmLine, "status").asInteger()==0?"开启":"关闭",
				new CCol(array(
					new CButton("edit_alarmLine", _("Edit"), "editOpt("+id+")", "link_menu icon edit"),
					SPACE+SPACE,
					new CButton("del_alarmLine", _("Remove"), "delOpt("+id+")", "link_menu icon remove")
				), "nowrap")
				));	
		    
		    //隐藏表单数据
		    alarmLineTable.addRow(array(
		    		new CInput("hidden", "alarmLines["+id+"][alarmLineid]", Nest.value(alarmLine,"alarmLineid").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][item_type]", Nest.value(alarmLine,"item_type").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][condition]", Nest.value(alarmLine,"condition").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][timeout]", Nest.value(alarmLine,"timeout").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][retry]", Nest.value(alarmLine,"retry").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][status]", Nest.value(alarmLine,"status").asString()),
		    		new CInput("hidden", "alarmLines["+id+"][avbRate]", Nest.value(alarmLine,"avbRate").asString())
		    		));	    
		}
		
		return alarmLineTable;
	}
	
	/**
	 * 设置监控项显示文本
	 */
	public static String getItemTypeTxt(Map alarmLine){
		String itemTypeText="";
		if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_RESPONSE_TIME){
			itemTypeText="当前响应时间";
		}else if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_AVAilABLE_RATE){
			itemTypeText="当日可用率";
		}
		return itemTypeText;
	}
	
	/**
	 * 设置条件显示文本
	 */
	public static String getConditionTxt(Map alarmLine){
		String itemTypeText="";
		if(Nest.value(alarmLine, "condition").asInteger()==T_HTTPCONF_CONDITION_LT){
			itemTypeText=_("LT");
		}else if(Nest.value(alarmLine, "condition").asInteger()==T_HTTPCONF_CONDITION_GT){
			itemTypeText=_("GT");
		}
		return itemTypeText;
	}
	
	/**
	 * 设置阀值文本
	 */
	public static String getValveValue(Map alarmLine){
		String str="";
		if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_RESPONSE_TIME){
			str+=Nest.value(alarmLine, "timeout").$()+"s";
		}else if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_AVAilABLE_RATE){
			str=Nest.value(alarmLine, "avbRate").$()+""+"%";
		}
		return str;
	}
	
	/**
	 * 设置重试次数
	 */
	public static String getRetryNum(Map alarmLine){
		String str="";
		if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_RESPONSE_TIME){
			str=Nest.value(alarmLine, "retry").asString();
		}else if(Nest.value(alarmLine, "item_type").asInteger()==T_HTTPCONF_AVAilABLE_RATE){
			str="_";
		}
		return str;
	}
	
	/**
	 * 列表   设置状态
	 */
	public static boolean getStatus(Map alarmLine){
		if(isset(alarmLine, "alarmLineid")){//修改
			return "0".equals(alarmLine.get("status")); //为0指 开启
		}else{//新增
			return false; 
		}
	}

	 /**
	  * 设置状态
	  * @return
	  */
	 public static  CArray<String> getStatus(){
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(0), _("Enable"), 
			Integer.valueOf(1), "关闭"});
		  return itemcap;
	   }
	
	/**
	 * 设置触发次数
	 * @return
	 */
	 public static  CArray<String> getTriggerNumber(){
		 CArray itemcap = CArray.map(new Object[] { 
			Integer.valueOf(1),  "1"+_("of times"), 
			Integer.valueOf(2),  "2"+_("of times"), 
			Integer.valueOf(3), "3"+_("of times")});
		 return itemcap;
	   }
	 

}
