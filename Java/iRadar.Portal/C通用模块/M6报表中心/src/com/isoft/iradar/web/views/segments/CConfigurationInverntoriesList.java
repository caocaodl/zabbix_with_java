package com.isoft.iradar.web.views.segments;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.web.bean.common.SystemWordbook;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationInverntoriesList extends CViewSegment {
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CInput dataexport = new CSubmit("data_export", _("Export to CSV"), "", "orange export");
		CDiv hostDiv = new CDiv();
		hostDiv.setAttribute("class", "caption");
		
		CDiv toolbar = new CDiv();
		toolbar.setAttribute("class", "toolbar");
		toolbar.addItem(dataexport);
		
		CForm btnForm = new CForm("get", "intvoisport.action");	
		
		CWidget mainWidget = new CWidget();
		CForm createForm = new CForm("get");
        CJSScript _fusionScript = new CJSScript(get_js("inite();"));
		mainWidget.addItem(createForm);
		
		//添加action名称隐藏域
		CInput acName = new CInput("hidden", "actionName",Nest.value(data, "actionName").asString());
		CForm div = new CForm("get","intvoisport.action");
		div.addClass("intvoisportClass");
		div.addItem(array(toolbar,hostDiv));
		
		//厂商列表
		CTableInfo table1 = inverntForReportFirm(idBean,executor,true);
		CDiv titleDiv1 = new CDiv("资产物理类型统计表");
		titleDiv1.setAttribute("class", "caption");
		CDiv div1 = new CDiv();
		div1.setAttribute("class", "manufacturCon");
		div1.addItem(array(titleDiv1,table1));
		
		CDiv newdiv = new CDiv();
		newdiv.setAttribute("id", "todayMonItemChart");
		
		btnForm.addItem(acName);
		mainWidget.addHeader(btnForm);
		mainWidget.addItem(div1);
		mainWidget.addItem(div);
		mainWidget.addItem(array(newdiv,_fusionScript));
		return mainWidget;
	}

	/**
	 * 生成厂商页面数据以及支持报表导出
	 * @param idBean
	 * @param executor
	 * @param isFirm 是否是厂商页面
	 * @return
	 */
	public CTableInfo inverntForReportFirm(IIdentityBean idBean, SQLExecutor executor,boolean isFirm){
		Map result = new HashMap();
		CTableInfo widget = new CTableInfo(null);
		//获取厂商数据
		SystemWordbook Syswb  =new SystemWordbook();
		Map<String, String> paramMap = new HashMap<String, String>();;
		paramMap.put("type", "FIRM");
		List<Map> manufacturers = Syswb.doAll(paramMap);
	
			CTableInfo manufacturerTable = new CTableInfo(_("No firm"));
			manufacturerTable.setHeader(array(_("Belong company"),_("SERVER"),_("Net Hosts"),_("Stroge Hosts")));
			//生成表格
			CArray dataArray = null;
			Integer hostTotalNum = 0;
			Integer netTotalNum = 0;
			Integer storgeTotalNum = 0;
			for(Map manufacturer:manufacturers){//遍历所有的厂商
				dataArray = countForHostGroups(idBean,executor,manufacturer.get("dlabel"),manufacturer.get("dkey"),isFirm);
				hostTotalNum += Integer.parseInt(dataArray.get(1).toString());
				netTotalNum += Integer.parseInt(dataArray.get(2).toString());
				storgeTotalNum += Integer.parseInt(dataArray.get(3).toString());
				manufacturerTable.addRow(dataArray);
			}
			manufacturerTable.addRow(array("合计",hostTotalNum+"",netTotalNum+"",storgeTotalNum+""));
		return manufacturerTable;
	}
	
	/**
	 * 返回设备类型是某个厂商的设备个数
	 * @param idBean
	 * @param executor
	 * @param name 厂商
	 * @param isFirm 是否是厂商统计页面
	 * @return
	 */
	public CArray countForHostGroups(IIdentityBean idBean, SQLExecutor executor,Object manufacturerName,Object dkey,boolean isFirm){
		CArray<Long> serverGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_SERVER);
		CArray<Long> netGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_NET_DEV);
		CArray<Long> storageGroupIds = MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_STORAGE);
		
		//获取所有的资产数据
		CHostGet options = new CHostGet();
		options.setOutput(new String[]{"hostid", "name"});
		options.setSelectInventory(new String[]{"vendor"});	
		options.setSelectGroups(new String[]{"groupid"});
		/**
		 * 系统屏蔽默认的存储设备类型
		 */
//		Long[] groupIds = {IMonConsts.MON_SERVER_LINUX.longValue(),
//				           IMonConsts.MON_SERVER_WINDOWS.longValue(),
//				           IMonConsts.MON_STORAGE.longValue(),
//				           IMonConsts.MON_COMMON_NET.longValue(),
//				           IMonConsts.MON_NET_CISCO.longValue()};
		Long[] groupIds = Cphp.array_merge(serverGroupIds, netGroupIds,storageGroupIds).valuesAsLong();
		options.setGroupIds(groupIds);
		CArray<Map> hosts = API.Host(idBean, executor).get(options);
		
		//数据变量
		Integer hostNum = 0;
		Integer netNum = 0;
		Integer storageNum = 0;
		Object key = "";
		String _groups = "";
		//根据厂商和设备类型统计数据
		for(Map host:hosts){//遍历所有的资产设备如果设备属于某一个设备类型并且属于当前的厂商
			key = Nest.value(host, "inventory","vendor").$();
			_groups = Nest.value(host, "groups").asCArray().toString();
			if(dkey.equals(key)){	
//				if(_groups.contains(IMonConsts.MON_SERVER_LINUX.toString()) || _groups.contains(IMonConsts.MON_SERVER_WINDOWS.toString())){
//					hostNum++;
//				}
//				if(_groups.contains(IMonConsts.MON_STORAGE.toString())){
//					storageNum++;
//				}
//				if(_groups.contains(IMonConsts.MON_NET_CISCO.toString()) || _groups.contains(IMonConsts.MON_COMMON_NET.toString())){
//					netNum++;
//				}
				
				for(Long groupId:serverGroupIds){
					if(_groups.contains(Nest.as(groupId).asString()))
						hostNum++;
				}
				
				for(Long groupId:netGroupIds){
					if(_groups.contains(Nest.as(groupId).asString()))
						netNum++;
				}
				
				for(Long groupId:storageGroupIds){
					if(_groups.contains(Nest.as(groupId).asString()))
						storageNum++;
				}
				
			}
		}
		if(!isFirm){
			return array(manufacturerName,hostNum+"",netNum+"",storageNum+"");
		}
		return array(manufacturerName,hostNum,netNum,storageNum);
	}
	
	
}
