package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.CiscoDetailServiceImpl;
import com.isoft.iradar.web.action.CloudControlerDetailServiceImpl;
import com.isoft.iradar.web.action.CloudSimpleDetailServiceImpl;
import com.isoft.iradar.web.action.CommonNetworkServiceImpl;
import com.isoft.iradar.web.action.DB2DBDetailServiceImpl;
import com.isoft.iradar.web.action.DMDBDetailServiceImpl;
import com.isoft.iradar.web.action.HuaweiDetailServiceImpl;
import com.isoft.iradar.web.action.IHostDetailService;
import com.isoft.iradar.web.action.LinuxHostDetailServiceImpl;
import com.isoft.iradar.web.action.MiddelIISServiceImpl;
import com.isoft.iradar.web.action.MiddelWebLogicServiceImpl;
import com.isoft.iradar.web.action.MiddelWebSphereServiceImpl;
import com.isoft.iradar.web.action.MongoDBDetailServiceImpl;
import com.isoft.iradar.web.action.MySqlDBDetailServiceImpl;
import com.isoft.iradar.web.action.OracleDBDetailServiceImpl;
import com.isoft.iradar.web.action.OthersHostDetailServiceImpl;
import com.isoft.iradar.web.action.SqlServerDetailServiceImpl;
import com.isoft.iradar.web.action.TomcatMiddelServiceImpl;
import com.isoft.iradar.web.action.VMHostDetailServiceImpl;
import com.isoft.iradar.web.action.WindowsServiceImpl;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

/**
 * 设备详情视图页面
 * @author HP Pro2000MT
 *
 */
public class CHostDetailView extends CViewSegment{
	/**
	 * 设备详情页面布局
	 */
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget(null, "inventory-host");
		String groupId = Nest.value(data, "groupid").asString();
		String hostid = Nest.value(data, "hostid").asString();
		//设备详情数据
		CFormList overviewFormList = new CFormList();
		
		CDiv headRight = new CDiv(null, "head_right");
		String formListClass = "equipment_details_notServer";
		//获取设备的健康度
		Map healthMap = CommonUtils.host_health(idBean, executor, hostid, groupId); 
		int healthNum = CommonUtils.returnHealthDegree(executor, idBean, Nest.value(data, "hostid").asString());
		data.put("healthNum", healthNum);
		
		//根据设备类型 获取不同的关键指标表格
		CTableInfo target_key = null;
		IHostDetailService service = null;
		CDiv div = null;	//健康度区域显示的插件内容
		if(groupId.equals(IMonConsts.MON_SERVER_LINUX.toString())){			//服务器linux
			service = new LinuxHostDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "special nn equipment_details",healthMap);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "special equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_SERVER_WINDOWS.toString())){		//服务器windows
			service = new WindowsServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "special nn equipment_details",healthMap);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "special equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_NET_CISCO.toString())){			//网络设备CISCO
			service = new CiscoDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_COMMON_NET.toString())){			//通用网络设备
			service = new CommonNetworkServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "common equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_NET_HUAWEI_SWITCH.toString())){			//华为交换机
			service = new HuaweiDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_VM.toString())){					//云主机
			service = new VMHostDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "special nn equipment_details",healthMap);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "special equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_MIDDLE_TOMCAT.toString())){		//中间件Tomcat
			service = new TomcatMiddelServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_MIDDLE_IIS.toString())){			//中间件IIS
			service = new MiddelIISServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_MIDDLE_WEBLOGIC.toString())){	//中间件WebLogic
			service = new MiddelWebLogicServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_MIDDLE_WEBSPHERE.toString())){	//中间件WebSphere
			service = new MiddelWebSphereServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_DB_MySQL.toString())){			//MySQL数据库
			service = new MySqlDBDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_DB_Oracle.toString())){			//Oracle数据库
			service = new OracleDBDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_DB_DM.toString())){				//DM数据库
			service = new DMDBDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_DB_DB2.toString())){				//DB2数据库
			service = new DB2DBDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_DB_SQLSERVER.toString())){		//SqlServer数据库
			service = new SqlServerDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
			
		}else if(groupId.equals(IMonConsts.MON_DB_MONGODB.toString())){			//MongoDB数据库
			service = new MongoDBDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLOUD_CONTROLER.toString())){	//云服务控制节点
			service = new CloudControlerDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLOUD_COMPUTER.toString())){		//云服务计算节点
			service = new CloudSimpleDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLOUD_CEPH.toString())){			//云服务存储节点
			service = new CloudSimpleDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLOUD_NETWORK.toString())){		//云服务网络节点
			service = new CloudSimpleDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLOUD_WEB.toString())){			//云服务门户节点
			service = new CloudSimpleDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_CLUSTER.toString())){			//集群(健康度暂时不做处理)
//			service = new ClusterDetailServiceImpl();
//			target_key = service.getTargetKey(idBean, executor, data);
//			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data, "", String.valueOf(0));
//			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
//			headRight.addItem(new CDiv(_("healthNum"), "health"));
//			formListClass = "equipment_details";

		}else if(groupId.equals(IMonConsts.MON_DESKTOPC.toString())){			//桌面云
//			service = new ClusterDetailServiceImpl();
//			target_key = service.getTargetKey(idBean, executor, data);
//			String cpuUsege = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.HOSTMEMORYUSAGE_DESKTOPC.getValue()).values().avg().toString();
//			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data, "", String.valueOf(cpuUsege));
//			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
//			headRight.addItem(new CDiv(_("healthNum"), "health"));
//			formListClass = "equipment_details";

		}else{
			service = new OthersHostDetailServiceImpl();
			target_key = service.getTargetKey(idBean, executor, data);
			div  = service.getHealthFunsionCharts("trend_chart", "equipment_details",data);
			overviewFormList = service.getOverviewForm(idBean, executor, data, hostid);
			headRight.addItem(new CDiv(_("healthNum"), "health"));
			formListClass = "equipment_details";
		}
		headRight.addItem(div);
		
		CDiv headLeft = new CDiv(null, "head_left");
		headLeft.addItem(new CDiv(_("Host detail"), "details"));
		headLeft.addItem(new CDiv(overviewFormList,formListClass));
		
		CDiv headCtn = new CDiv(null, "page_header");
		headCtn.addItem(headLeft);
		headCtn.addItem(headRight);
		
		//实现div分割区域
		CDiv splitDiv = new CDiv();
		splitDiv.addStyle("height:25px;");
		
		CDiv ctn = new CDiv();							//创建要显示的table
		ctn.addItem(headCtn);							//添加设备详情(包含健康度)
		ctn.addItem(splitDiv);							//添加分割区域
		ctn.addItem(new CDiv(_("Target Key"),"norm"));
		ctn.addItem(target_key);						//添加关键指标
		ctn.addItem(splitDiv);							//添加分割区域
		
		//最近告警列表数据
		CTable trigger_table = (CTable)Nest.value(data, "triggerForm").$();
		String triggerClass = trigger_table.getAttribute("class").toString();
		triggerClass += " tableinfo_alarm"; 
		trigger_table.setAttribute("class", triggerClass);
		
		if(!groupId.equals(IMonConsts.MON_VM.toString())){			
			ctn.addItem(new CDiv(_("recentlyEvents"), "alarm"));
			ctn.addItem(trigger_table);						//添加告警列表
		}
		hostInventoryWidget.addItem(ctn);
		return hostInventoryWidget;
	}	
}
