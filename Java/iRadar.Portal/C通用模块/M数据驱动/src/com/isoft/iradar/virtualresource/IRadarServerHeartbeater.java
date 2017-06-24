package com.isoft.iradar.virtualresource;

import static com.isoft.iradar.Cphp.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.Feature;
import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.iradar.core.g;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.server.IRadarServer;

public class IRadarServerHeartbeater extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(IRadarServerHeartbeater.class);
	
	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long SYNC_INTERVAL_INIT = 1000 * 1;
	private final static long SYNC_INTERVAL = 1000 * Defines.SERVER_CHECK_INTERVAL ;//单线程30秒可接受
	private final static String LOG4J_CATEGORY = "iRadar.Server.Heartbeater";
	
	private volatile Scheduler m_scheduler;
		
	public IRadarServerHeartbeater() {
		super(LOG4J_CATEGORY);
	}
	
	private void scheduleHeartbeat() {
		IRadarServer iradarServer = new IRadarServer(Feature.iradarServer, Feature.iradarPort, Defines.RDA_SOCKET_TIMEOUT, 0);
		g.RDA_SERVER_RUNNING = iradarServer.isRunning();
		g.RDA_SERVER_RUNNING_TS = time();
		iradarServer.close();
	}

//	private ReadyRunnable scheduler() {
//        return new ReadyRunnable() {
//            @Override public boolean isReady() { return true; }
//            @Override public void run() {
//                Logging.withPrefix(LOG4J_CATEGORY, new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                        	scheduleHeartbeat();
//                        } catch (Throwable e) {
//                            LOG.error("start: Failed to schedule existing interfaces", e);
//						} finally {
//							runOnce(SYNC_INTERVAL);
//						}
//                    }
//                });
//            }
//        };
//    }
	
	private ReadyRunnable scheduler() {
        return new ReadyRunnable() {
            @Override public boolean isReady() { return true; }
            @Override public void run() {
            	try {
            		scheduleHeartbeat();
            	} catch (Throwable e) {
            		LOG.error("start: Failed to schedule existing interfaces", e);
            	} finally {
            		runOnce(SYNC_INTERVAL,this);
            	}
            }
        };
    }

	@Override
	protected void onInit() {
		LOG.debug("init: Initializing IradarServer Heartbeater daemon");
		this.runOnce(SYNC_INTERVAL_INIT);
	}
	
	private void runOnce(long interval) {
		//加入处理线程
		getScheduler().schedule(interval, scheduler());
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
            LOG.debug("start: Starting IradarServer Heartbeater scheduler");
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
                    LOG.debug("init: Creating IradarServer Heartbeater scheduler");
                    setScheduler(new LegacyScheduler("IradarServer Heartbeater", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create IradarServer Heartbeater scheduler", e);
                    throw e;
                }
            }
        });
	}
}
