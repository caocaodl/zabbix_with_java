package com.isoft.iradar.trapitem;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.trapitem.config.TrapConfig;
import com.isoft.iradar.trapitem.config.TrapHost;
import com.isoft.iradar.trapitem.config.TrapItem;
import com.isoft.iradar.trapitem.config.TrapTemplate;
import com.isoft.iradar.web.daoimpl.CInspectionReportHistoryDAO;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

/**
 * 云服务器监控指标的Trap采集
 * 
 * 
 * @author BluE
 *
 */
public class TrapItemCollectd extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(TrapItemCollectd.class);
	
	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long DISCOVERY_INTERVAL = 1000 * 30 ;//三十秒采集一次
	private final static long NEXT_CHECK_SPEED = 600 ;//超过十分钟的，第一次加速到10分钟一次
	private final static String LOG4J_CATEGORY = "iRadar.trapItem.collectd";

	private final CArray<Long> nextChecks = CArray.array();
	
	String GET_PROXY_HOST_BY_VM = " SELECT ho.host "+
								  " FROM hosts ho "+
								  " WHERE ho.hostid IN (SELECT h.proxy_hostid "+
													    " FROM hosts h "+
													    " WHERE h.tenantid = #{tenantid} "+
													    " AND h.host = #{host} )";
	
	private volatile Scheduler m_scheduler;
	
	private TrapConfig config;
	private ScriptEngine scriptEngine;
	private Hashtable<String, Integer> serverAddress;
	
	
	public TrapItemCollectd() {
		super(LOG4J_CATEGORY);
	}
	
	//采集类型
	private final static int TYPE_VM = 0;
	private final static int TYPE_ADMIN = 1;
	private final static int TYPE_TENANT = 2;
	
	/**
	 * 指标采集函数：分为 vm（云主机指标）、 admin（云服务指标）、tenant（租户相关指标）三部分
	 * 
	 * 算法流程：每30S筛选出过了休眠期的指标，取数据并发送给对应服务器；
	 * 
	 * 其中对于超过600S的item首次需要加速
	 * 
	 */
	private void scheduleDataCollect() {
		//采集器
		eval("_collects=(function(){" +
				"var r={}; " +
				"var cs=__cfg.getCollectors(); " +
				"var keys=cs.keySet().toArray(); " +
				"for(var i=0,ilen=keys.length;i<ilen;i++){" +
					"var key=keys[i];" +
					"var c=cs.get(key); " +
					"r[c.getKey()] = (function(js){" +
						"return function(id){" +
							"return eval(js)" +
						"}" +
					"})(c.getScript()+'');" +
				"} " +
				"return r;" +
			"})()");
		
		//Admin客户端
		eval("$=__f.admin()");
		eval("__t_cache={}");
		
		eval("$g=(function(){"+this.config.getInit()+"})()");
		
		//采集云主机相关数据
		collectVM();
		
		Tenants tenants = null;
		
		//采集云服务相关监控数据
		for(TrapHost host: this.config.getHosts()) {
			String hostName = host.getName();
			BlockingQueue<iRadarItem> queue = new LinkedBlockingQueue<iRadarItem>();
			
			//判断有没有需要加速的新指标
			for(String templateName: host.getTemplates()) {
				TrapTemplate template = this.config.getTemplates().get(templateName);
				if(template == null) {
					LOG.warn("Unkonw template[{}] in host[{}]", hostName, templateName);
					continue;
				}
				
				//admin监控项
				for(TrapItem item: template.getAdminItems()) {
					speedNewItem(TYPE_ADMIN, hostName, item);
				}
				
				//tenant监控项
				for(TrapItem item: template.getTenantItems()) {
					speedNewItem(TYPE_TENANT, hostName, item);
				}
			}
			
			//对过了休眠期的指标进行采集
			long now = System.currentTimeMillis();
			
			CArray<Long> adminChecks = Nest.value(nextChecks, TYPE_ADMIN, hostName).asCArray();
			if(!Cphp.empty(adminChecks)) {
				for(Object itemObj: adminChecks.keySet()) {
					long nextCheck = adminChecks.get(itemObj);
					if(now >= nextCheck) {
						TrapItem item = (TrapItem)itemObj;
						//接口采集
						collectIaaSAdmin(host, queue, item);
						//更新休眠时间
						updateNextCheck(TYPE_ADMIN, hostName, item);
					}
				}
			}
			
			CArray<Long> tenantChecks = Nest.value(nextChecks, TYPE_TENANT, hostName).asCArray();
			if(!Cphp.empty(tenantChecks)) {
				for(Object itemObj: tenantChecks.keySet()) {
					long nextCheck = tenantChecks.get(itemObj);
					if(now >= nextCheck) {
						TrapItem item = (TrapItem)itemObj;
						
						if(tenants == null) {//只有当需要更新时才重新获取租户信息
							tenants = OpsUtils.getOpenStackClientForAdmin().getIdentityClient().tenants().list().execute();
						}
						
						//接口采集
						collectIaaSTenant(tenants, host, queue, item);
						//更新休眠时间
						updateNextCheck(TYPE_TENANT, hostName, item);
					}
				}
			}
			
			sendTrapData(queue, host.getName(), (Feature.debug&&LOG.isTraceEnabled())? "__tid__": Feature.defaultTenantId);
		}
	}
	
	private void speedNewItem(int type, String host, TrapItem item) {
		if(Nest.value(nextChecks, type, host, item).$() == null) {
			long interval = item.getInterval();
			long nextCheck = System.currentTimeMillis() + Math.min(interval, NEXT_CHECK_SPEED)*1000;
			Nest.value(nextChecks, type, host, item).$(nextCheck);
		}
	}
	
	private void updateNextCheck(int type, String host, TrapItem item) {
		long interval = item.getInterval();
		long nextCheck = System.currentTimeMillis() + interval*1000;
		Nest.value(nextChecks, type, host, item).$(nextCheck);
	}
	
	private void logScriptException(ScriptException e, TrapItem item, StringBuffer script) {
		if(LOG.isWarnEnabled()) {
			LOG.warn("item["+item.getTemplate().getName()+"-" + item.getKey()+"] eval exception: "+ e.getMessage() +"; [Script] "+script.toString());
		}else if(LOG.isErrorEnabled()) {
			LOG.error("item["+item.getTemplate().getName()+"-" + item.getKey()+"] eval exception: "+script.toString(), e);
		}
	}

	/**
	 * @param tenants
	 * @param host
	 * @param queue
	 * @param template
	 */
	public void collectIaaSTenant(Tenants tenants, TrapHost host, BlockingQueue<iRadarItem> queue, TrapItem item) {
		StringBuffer sb = new StringBuffer();
		sb.append("(function() {");
		
		for(String collectName: item.collectors()) {
			sb.append(collectName).append("=_collects['").append(collectName).append("']();");
		}
		
		sb.append(item.getResult());
		
		sb.append("})()");
		
		
		for(Tenant tenant: tenants.getList()) {
			if(!tenant.getEnabled()) {
				continue;
			}
			
			String tenantId = tenant.getId();
			
			Object value = null;
			try {
				scriptEngine.eval("var $tid='"+tenantId+"'; var $t=_tc($tid);");
				value = scriptEngine.eval(sb.toString());
			} catch (ScriptException e) {
				logScriptException(e, item, sb);
			}
			
			if(value != null) {
				String key = item.getKey();
				int index = key.indexOf("$tid");
				key = key.substring(0, index) + tenantId + key.substring(index+4);
				queue.add(new iRadarItem(key, String.valueOf(value), host.getName()));
			}
		}
	}

	/**
	 * @param host
	 * @param queue
	 * @param template
	 */
	public void collectIaaSAdmin(TrapHost host, BlockingQueue<iRadarItem> queue, TrapItem item) {
		StringBuffer sb = new StringBuffer();
		sb.append("(function() {");
		
		for(String collectName: item.collectors()) {
			sb.append(collectName).append("=_collects['").append(collectName).append("']();");
		}
		
		sb.append(item.getResult());
		
		sb.append("})()");
		
		Object value = null;
		try {
			value = scriptEngine.eval(sb.toString());
		} catch (ScriptException e) {
			logScriptException(e, item, sb);
		}
		
		if(value != null) {
			queue.add(new iRadarItem(item.getKey(), String.valueOf(value), host.getName()));
		}
	}

	/**
	 * 云主机采集函数
	 * 
	 * 注意：云主机因为是批量采集的，所以这里的加速，只做到对首次服务启动的加速，对于后续增加的云主机，没有进行加速
	 */
	public void collectVM() {
		String VMS_HOSTNAME = "VMS";
		
		final CArray vmProxyHost = CArray.array();
		String proxyHostAddress = "";
		final CInspectionReportHistoryDAO dao = new CInspectionReportHistoryDAO();
		Hashtable<String, Integer> proxyHostMap = null;
		for(String templateName: this.config.getVmTemplates()) {
			Map<String, Map<Object, BlockingQueue<iRadarItem>>> queueTenant = EasyMap.build();
			
			TrapTemplate template = this.config.getTemplates().get(templateName);
			if(template == null) {
				LOG.warn("Unkonw vm_templates in host[{}]", templateName);
				continue;
			}
			
			//采集admin监控项
			for(TrapItem item: template.getAdminItems()) {
				//首次加速
				speedNewItem(TYPE_VM, VMS_HOSTNAME, item);
				
				//检查时间
				long nextCheck = Nest.value(nextChecks, TYPE_VM, VMS_HOSTNAME, item).asLong();
				if(System.currentTimeMillis() < nextCheck) {
					continue;
				}
				
				//更新休眠时间
				updateNextCheck(TYPE_VM, VMS_HOSTNAME, item);
				
				//执行采集
				StringBuffer sb = new StringBuffer();
				sb.append("(function() {");
				
				for(String collectName: item.collectors()) {
					sb.append(collectName).append("=_collects['").append(collectName).append("']();");
				}
				
				sb.append(item.getResult());
				
				sb.append("})()");
				
				Object value = null;
				try {
					value = scriptEngine.eval(sb.toString());
				} catch (ScriptException e) {
					logScriptException(e, item, sb);
				}
				
				if(value != null) {
					List<Map> values = (List)value;
					for(Map v: values) {
						String key = item.getKey();
						String vStr = Nest.value(v, "value").asString();
						String host = Nest.value(v, "host").asString();
						String tenantId = Nest.value(v, "tenantid").asString();
						
						Map<Object, BlockingQueue<iRadarItem>> queueHost = queueTenant.get(tenantId);
						if(queueHost == null) {
							queueHost = EasyMap.build();
							queueTenant.put(tenantId, queueHost);
						}
						
						BlockingQueue<iRadarItem> queue = queueHost.get(host);
						if(queue == null) {
							queue = new LinkedBlockingQueue<iRadarItem>();
							queueHost.put(host, queue);
						}
						
						queue.add(new iRadarItem(key, vStr, host));
					}
				}
			}

			for(Entry<String, Map<Object, BlockingQueue<iRadarItem>>> entryTenant: queueTenant.entrySet()) {
				final String tenantId = entryTenant.getKey();
				for(Entry<Object, BlockingQueue<iRadarItem>> entry: entryTenant.getValue().entrySet()) {
					final String host = String.valueOf(entry.getKey());

					if(Feature.debug && LOG.isTraceEnabled()) {
						//No op
					}else {
						if(!Cphp.empty(vmProxyHost) && !Cphp.empty(Nest.value(vmProxyHost, tenantId, host).$())){
							proxyHostAddress = Nest.value(vmProxyHost, tenantId, host).asString();
						}else{
							proxyHostAddress = this.delegate(new Delegator() {
								@Override
								public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
									CArray param = CArray.array();
									param.put("tenantid", tenantId);
									param.put("host", host);
									CArray<Map> hosts = dao.doGetProxyHostByVm(param, GET_PROXY_HOST_BY_VM);
									if(!Cphp.empty(hosts)){
										String proxyHostAddr = Nest.value(hosts, 0,"host").asString();
										Nest.value(vmProxyHost, tenantId, host).$(proxyHostAddr);
										return proxyHostAddr;
									}
									return null;
								}
							});
						}
					}
					
					if(!Cphp.empty(proxyHostAddress)){
						proxyHostMap = new Hashtable<String, Integer>(1);
						proxyHostMap.put(proxyHostAddress, Feature.iradarPort);
					}else{
						proxyHostMap = this.serverAddress;
					}
					
					sendTrapData(entry.getValue(), host, tenantId, proxyHostMap);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		TrapItemCollectd o = new TrapItemCollectd();
		o.initCollectEngine();
		while(true) {
			o.scheduleDataCollect();
			Thread.sleep(DISCOVERY_INTERVAL);
		}
	}
	
	//发送数据
	private void sendTrapData(BlockingQueue<iRadarItem> queue, String host, String tenantId) {
		sendTrapData(queue,host,tenantId,null);
	}
	
	private void sendTrapData(BlockingQueue<iRadarItem> queue, String host, String tenantId, Hashtable<String, Integer> proxyHost) {
		if(!queue.isEmpty()){
			if(Feature.debug && LOG.isTraceEnabled()) {
				try {
					while(!queue.isEmpty()) {
						iRadarItem item = queue.poll();
						LOG.debug(item.getHostName() + " | " + item.getKey() + " = " + item.getValue());
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}else {
				Sender sender = null;
				if(!Cphp.empty(proxyHost)){
					sender = new Sender(queue, proxyHost, host, tenantId);
				}else{
					sender = new Sender(queue, serverAddress, host, tenantId);
				}
				new Thread(sender).start();
			}
		}
	}
	
	public static class OpsClientFacotry{
		public IaaSClient admin() {
			return OpsUtils.getOpenStackClientForAdmin();
		}
		
		public IaaSClient tenant(String tenantId) {
			return OpsUtils.getOpenStackClient(tenantId);
		}
		
		public void info(Object o) {
			System.out.println("JS Console:" + o);
		}
	}
	
	private void initCollectEngine() {
		//collect配置文件
		this.config = new TrapConfig();
		this.config.reload();
		
		//脚本引擎
		ScriptEngineManager sem = new ScriptEngineManager();
		this.scriptEngine = sem.getEngineByName("javascript");
		
		Bindings b = new SimpleBindings();
		this.scriptEngine.setBindings(b, ScriptContext.GLOBAL_SCOPE);
		
		//采集相关配置
		b.put("__cfg", this.config);
		
		//将静态方法转化为JS可用的对象
		b.put("__f", new OpsClientFacotry());
		
		try {
			this.scriptEngine.eval("___=this");
			//cache算法
			this.scriptEngine.eval("__f_cache = function(cache, facotry, id){var v=cache[id]; if(!v){v=facotry(id); cache[id]=v;} return v;}");
			//tenant对应客户端
			this.scriptEngine.eval("_tcs={}; _tc = function(id){return __f_cache(_tcs, function(id){return __f.tenant(id);}, id)}");
		} catch (ScriptException e) {
			LOG.error("init scriptEngine eval exception", e);
		}
	}

	@Override
	protected void onInit() {
		LOG.debug("init: Initializing discovery daemon");
		
		//iradar服务器地址
		this.serverAddress = new Hashtable<String, Integer>(1);
		this.serverAddress.put(Feature.iradarServer, Feature.iradarPort);
		
		this.initCollectEngine();
		
		this.runOnce(DISCOVERY_INTERVAL);
	}

	private Object eval(String s) {
		try {
			return scriptEngine.eval(s);
		} catch (ScriptException e) {
			LOG.error("eval exception", e);
		}
		return null;
	}
	
	private ReadyRunnable scheduler(final long interval) {
        return new ReadyRunnable() {
            @Override public boolean isReady() { return true; }
            @Override public void run() {
            	try {
            		scheduleDataCollect();
            	} catch (Throwable e) {
            		LOG.error("start: Failed to schedule existing interfaces", e);
            	} finally {
            		runOnce(interval, this);
            	}
            }
        };
    }
	
	private void runOnce(long interval) {
		//加入处理线程
		getScheduler().schedule(interval, scheduler(DISCOVERY_INTERVAL));
	}
	
	private void runOnce(long interval,ReadyRunnable runnable) {
		//加入处理线程
		getScheduler().schedule(interval, runnable);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onStart() {
		// start the scheduler
        try {
            LOG.debug("start: Starting IaaSTrapper scheduler");
            getScheduler().start();
        } catch (RuntimeException e) {
            LOG.error("start: Failed to start scheduler", e);
            throw e;
        }
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onStop() {
		getScheduler().stop();
		setScheduler(null);
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onPause() {
		getScheduler().pause();
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onResume() {
		getScheduler().resume();
	}

	
	/**
     * <p>setScheduler</p>
     *
     * @param scheduler a {@link com.isoft.framework.scheduler.Scheduler} object.
     */
    protected void setScheduler(Scheduler scheduler) {
        m_scheduler = scheduler;
    }
    
    private Scheduler getScheduler() {
        if (m_scheduler == null) {
            createScheduler();
        }
        return m_scheduler;
    }
    
	private void createScheduler() {
		Logging.withPrefix(LOG4J_CATEGORY, new Runnable() {
            @Override
            public void run() {
                // Create a scheduler
                try {
                    LOG.debug("init: Creating IaaSTrapper scheduler");
                    setScheduler(new LegacyScheduler("IaaSTrapper", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create IaaSTrapper scheduler", e);
                    throw e;
                }
            }
        });
	}
	
	private <T> T delegate(Delegator<T> d) {
		return delegate(Feature.defaultTenantId, d);
	}
	
	private <T> T delegate(String tenantId, Delegator<T> d) {
		try {
			if(RadarContext.getContext() == null) {
	    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
	    		RadarContext.setContext(ctx);
	    		
	    		Map userdata = EasyMap.build(
					"userid", 0L, 
					"type", Defines.USER_TYPE_SUPER_ADMIN
				);
	    		CWebUser.set(userdata);
	    	}
			
			Map uinfo = new HashMap();
			uinfo.put("tenantId", tenantId);
			uinfo.put("osTenantId", "0");
			uinfo.put("tenantRole", Role.LESSOR.magic());
			uinfo.put("userId", Feature.defaultUser);
			uinfo.put("userName", Feature.defaultUser);
			uinfo.put("admin", "Y");
			uinfo.put("osUser", null);
			
			IdentityBean idBean = new IdentityBean();
			idBean.init(uinfo);
	    	return CDelegator.doDelegate(idBean, d);
		} finally {
			RadarContext.releaseContext();
		}
	}
}