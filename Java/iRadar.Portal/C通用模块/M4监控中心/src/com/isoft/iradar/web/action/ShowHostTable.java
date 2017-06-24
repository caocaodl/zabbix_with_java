package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.types.CArray.array;

import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.types.CArray;

/**
 * 展示监控指标列表(监控中心简单列表页面)
 * @author BT
 *
 */
public class ShowHostTable{
	/**
	 * 组装设备信息页面
	 * @return
	 */
	
	/**
	 * 获取表头(不同的设备类型 对应不同的监控指标)
	 * @param groupid
	 * @return
	 */
	public CArray getGroupHeader(String groupid){
		CArray header = new CArray();
		
		if(groupid.equals(IMonConsts.MON_SERVER_WINDOWS.toString()) ||groupid.equals(IMonConsts.MON_SERVER_LINUX.toString())){	//服务器
			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
			
		}else if(groupid.equals(IMonConsts.MON_VM.toString())){		//云主机
			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
			
		}else if(groupid.equals(IMonConsts.MON_MIDDLE_TOMCAT.toString())){	//中间件tomcat
			header = array(_("hostName"),"Swap分区内存剩余","最大线程数","当前线程数","每秒出错数");
			
		}else if(groupid.equals(IMonConsts.MON_DB_MySQL.toString())){//MySQL数据库
			header = array(_("hostName"),_("IP"),"缓存空闲内存","当前连接数","总连接数","QPS","当前版本");
			
		}else if(groupid.equals(IMonConsts.MON_DB_Oracle.toString())){//Oracle数据库
			header = array(_("hostName"),_("IP"),"磁盘上读取数据块的数量","数据文件读取","数据文件写入","缓冲池命中率","当前版本");
			
		}else if(groupid.equals(IMonConsts.MON_DB_DM.toString())){//DM数据库
			header = array(_("hostName"),_("IP"),"活动事务","进程状态","会话连接时间超时个数","会话对对象锁定时间超时个数","当前版本");
			
		}else if(groupid.equals(IMonConsts.MON_DB_DB2.toString())){//DB2数据库
			header = array(_("hostName"),_("IP"),"-","-","-","-","-");
			
		}else if(groupid.equals(IMonConsts.MON_DB_SQLSERVER.toString())){//SqlServer数据库
			header = array(_("hostName"),_("IP"),"-","-","-","-","-");
			
		}else if(groupid.equals(IMonConsts.MON_DB_MONGODB.toString())){//MongoDB数据库
			header = array(_("hostName"),_("IP"),"-","-","-","-","-");
			
		}else if(groupid.equals(IMonConsts.MON_NET_CISCO.toString())){	//网络设备
			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
			
		}
		/**
		 * 系统屏蔽默认的存储设备类型
		 */
//		else if(groupid.equals(IMonConsts.MON_STORAGE.toString())){//存储设备
//			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
//		
//		}
		else if(groupid.equals(IMonConsts.MON_CLOUD_CONTROLER.toString())){	//云服务
			header = array(_("hostName"),_("IP"),"实例个数","内存总量","租户个数","告警个数","宿主机个数","镜像总个数");
			
		}else if(groupid.equals(IMonConsts.MON_CLUSTER.toString())){//集群
			header = array(_("hostName"),_("IP"), "数据中心名称","数据中心状态", "存储域名称","存储域类型","主机节点物理内存","云主机池名称");
			
		}else if(groupid.equals(IMonConsts.MON_DESKTOPC.toString())){//桌面云
			header = array(_("hostName"),_("IP"), "CPU数量","USB策略", "最小内存","定义的内存");
			
		}else if(groupid.equals(IMonConsts.MON_CLOUD_CEPH.toString())){//Ceph
			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
			
		}else{	//其他默认值
			header = array(_("hostName"),_("IP"), _("CPU_RATE"),_("CPU_LOAD"), _("memoryRate"),_("disk_Rate"),_("disk_read_and_write"),_("net_Rate"));
		}
		return header;
	}
	
	/**
	 * 获取某一设备类型监控指标的取值
	 * @param groupid
	 * @return
	 */
}
