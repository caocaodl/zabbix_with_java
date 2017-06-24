package com.isoft.imon.topo.netmgt.discoveryd;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.events.Event;
import com.isoft.framework.events.EventCenter;
import com.isoft.framework.events.EventListener;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.HostPlugin;
import com.isoft.imon.topo.host.discovery.DiscoveryEngine;
import com.isoft.imon.topo.netmgt.discoveryd.dao.NodeDao;
import com.isoft.imon.topo.platform.context.ContextFactory;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.context.TopoPool;
import com.isoft.imon.topo.platform.plugin.Plugin;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.imon.topo.util.NmsException;
import com.isoft.imon.topo.web.view.TopoView;

public class Discoveryd extends AbstractServiceDaemon implements EventListener {
	private static final Logger LOG = LoggerFactory.getLogger(Discoveryd.class);
	
	private static int MAX_SCHEDULER_THREADS = 1;
	private final static int SNIFFER_WORKER_SIZE = MAX_SCHEDULER_THREADS;//共有多个嗅探线程（消费者数量）
	
	private static long DISCOVERY_INTERVAL_INIT = 1000 * 10;
	private static long DISCOVERY_INTERVAL = 1000 * 60 * 30;
	
	private NodeDao nodeDao;
	
	/**
	* Log4j category
	*/
	private final static String LOG4J_CATEGORY = "iRadar.discoveryd";
	
	/**
	* Reference to the discovery scheduler
	*/
	private volatile Scheduler m_scheduler;
	
	/**
	* Constructor.
	*/
	public Discoveryd() {
		super(LOG4J_CATEGORY);
	}
	
	private final static Event EVENT_SNIFFER_START = new Event("sniffer_start");
	private final static Event EVENT_SNIFFER_WORK_COMPLETE = new Event("sniffer_work_complete");
	public final static Event EVENT_DLINK_COMPLETE = new Event("dlink_complete");
	private AtomicInteger SNIFFER_WORKER_REST = new AtomicInteger(0);
	
	/**
	 * 运行嗅探线程
	 */
	private void scheduleSniffer() {
		final TopoPool pool = PollingPool.getPool();
		pool.unload();//清空缓存，保证每次发现都是全新的
		
		List<Host> hosts = nodeDao.getNetElments();
		final Queue<Host> queue = new ConcurrentLinkedQueue<Host>(hosts);//创建一个可在多线程环境下使用的队列；用队列的好处是用一个，可以去掉一个
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("need sniff hosts: " + hosts);
		}
		
		EventCenter.notice(EVENT_SNIFFER_START);	//开始嗅探
		for(int i=0; i<SNIFFER_WORKER_SIZE; i++) {
			getScheduler().schedule(0, new ReadyRunnable() {
	            @Override public boolean isReady() { return true; }
	            @Override public void run() {
	            	Host host = queue.poll();//获取一个节点，并从队列中去除
	            	if(host != null) {
	            		if(LOG.isDebugEnabled()) {
	            			LOG.debug("sniff host: " + host);
	            		}
	            		
	            		try {
							host.getCredence("SNMP").getSniffer().doSniff(host);
						} catch (NmsException e) {
							e.printStackTrace();
						}
	            		getScheduler().schedule(0, this);
	            	}else {
	            		EventCenter.notice(EVENT_SNIFFER_WORK_COMPLETE);
	            	}
	            }
	        });
		}
    }
	
	/**
	 * 运行链路发现线程
	 */
	private void scheduleDisvoeryLink() {
		DiscoveryEngine engine = DiscoveryEngine.getEngine();
		engine.init();
		engine.discoveryLink(getScheduler());
	}

    @Override
	public void onEvent(Event e) {
		if(EVENT_SNIFFER_START.equals(e)) {
			SNIFFER_WORKER_REST.set(0);
		}else if(EVENT_SNIFFER_WORK_COMPLETE.equals(e)) {
			if(SNIFFER_WORKER_REST.addAndGet(1) == SNIFFER_WORKER_SIZE) {
				scheduleDisvoeryLink();
			}
		}else if(EVENT_DLINK_COMPLETE.equals(e)) {
			if(LOG.isInfoEnabled()) {
				LOG.info("discovery link complete!");
			}
			
			//刷新存储的拓扑数据
			PollingPool.getPool().setup();
			
			if(LOG.isInfoEnabled()) {
				LOG.info("save the link of discovery complete!");
			}
			
			//触发下一次发现
			getScheduler().schedule(DISCOVERY_INTERVAL, scheduler());
		}
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
                    LOG.debug("init: Creating discoveryd scheduler");
                    setScheduler(new LegacyScheduler("discoveryd", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create discoveryd scheduler", e);
                    throw e;
                }
            }
        });
	}

	private ReadyRunnable scheduler() {
        // Schedule existing interfaces for data discovery
        ReadyRunnable interfaceScheduler = new ReadyRunnable() {
            @Override public boolean isReady() { return true; }
            @Override public void run() {
                Logging.withPrefix(LOG4J_CATEGORY, new Runnable() {
                    @Override
                    public void run() {
                        try {
                        	scheduleSniffer();
                        } catch (Exception e) {
                            LOG.error("start: Failed to schedule existing interfaces", e);
                        }
                    }
                });
            }
        };
        return interfaceScheduler;
    }

	/**
	* <p>onInit</p>
	*/
	@Override
	protected void onInit() {
		LOG.debug("init: Initializing discovery daemon");
		
		//注册监听事件
		EventCenter.registe(this, 
				EVENT_SNIFFER_START.getUei(), 
				EVENT_SNIFFER_WORK_COMPLETE.getUei(),
				EVENT_DLINK_COMPLETE.getUei()
			);
		
		//注册插件
		Plugin plugin = (Plugin) CommonUtil.getInstance(HostPlugin.class.getName());
		ContextFactory.getFactory().registerPlugin(plugin);
		plugin.register();
		
		//实例化DAO
		nodeDao = new NodeDao();
		//将数据库中host和link初始化到缓存中
        TopoView.getInstance().loadDBNodes();
        
		//加入处理线程
		getScheduler().schedule(DISCOVERY_INTERVAL_INIT, scheduler());
		
	}
	

	/** {@inheritDoc} */
	@Override
	protected void onStart() {
		// start the scheduler
        try {
            LOG.debug("start: Starting discoveryd scheduler");
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
}