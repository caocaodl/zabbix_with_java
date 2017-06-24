package com.isoft.iradar.web.listener;

import java.util.List;

import javax.servlet.ServletContext;

import com.isoft.event.EventFacade;
import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.imon.topo.netmgt.discoveryd.Discoveryd;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.data.cache.ItemDataCollectdCache;
import com.isoft.iradar.trapitem.TrapItemCollectd;
import com.isoft.iradar.virtualresource.IRadarServerHeartbeater;
import com.isoft.iradar.virtualresource.KeystoneSyncer;
import com.isoft.iradar.virtualresource.VirtualResourceDiscoveryd;

public class IMonListener extends IRadarListener {
	
	private final static List<AbstractServiceDaemon> DAEMONS = EasyList.build(
		new IRadarServerHeartbeater(), //定时检查iradarserver运行状态
		new KeystoneSyncer(), //keystone 租户及用户信息同步
		new Discoveryd(),					//拓扑链路发现
		new VirtualResourceDiscoveryd(),	//虚拟资源发现
		new TrapItemCollectd(),				//云服务器监控指标的Trap采集
		new InspectionReportHistoryListener(),  //巡检报告历史记录
		new ItemDataCollectdCache()//数据缓存
	);
	
	@Override
	protected void initExtendsPreload(ServletContext ctx, PreloaderBean loadBean) {
		super.initExtendsPreload(ctx, loadBean);
		
		try {
			//初始化事件监听
			EventFacade.init();
			com.isoft.imon.topo.util.SysConfigHelper.init(ctx);
			
			for(AbstractServiceDaemon daemon: DAEMONS) {
				daemon.init();
				daemon.start();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
