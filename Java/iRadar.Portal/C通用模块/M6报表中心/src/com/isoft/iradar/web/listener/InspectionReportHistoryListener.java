package com.isoft.iradar.web.listener;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.types.CArray.array;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.iradar.web.daoimpl.CInspectionReportHistoryDAO;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class InspectionReportHistoryListener extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(InspectionReportHistoryListener.class);

	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long DISCOVERY_INTERVAL_INIT = 1000 * 1;
	private final static long DISCOVERY_INTERVAL = 1000 * 60;
	private final static String LOG4J_CATEGORY = "iRadar.inspectionReport.history";
	
	private volatile Scheduler m_scheduler;
	private volatile CInspectionReportHistoryDAO dao;
	
	protected InspectionReportHistoryListener() {
		super(LOG4J_CATEGORY);
	}

	private void scheduleDateCollect() {
		boolean result=true;
		CArray<Map> reports = dao.noExcInspectionReportList();
		if(!empty(reports)){
			for(Map report:reports){
				CArray<Map> rhitems=dao.reportHistoryItems(report.get("reportid").toString());
				if(!empty(rhitems)){
					int batchnum = Nest.value(report, "batchnum").asInteger()+1;
					result=dao.addInspectionRepHis(Nest.value(report, "reportid").asLong(),batchnum, rhitems);
					/*if(result){
						dao.updateNexttime(array(report));
					}*/
					if(result){
						dao.updateReprotBatchNum(report, batchnum);
						dao.sendInspectionReport(report,rhitems);
					}
				}
			}
		}
	}
	
//	private ReadyRunnable scheduler() {
//        return new ReadyRunnable() {
//            @Override public boolean isReady() { return true; }
//            @Override public void run() {
//                Logging.withPrefix(LOG4J_CATEGORY, new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                        	scheduleDateCollect();
//                        } catch (Throwable e) {
//                            LOG.error("start: Failed to schedule existing interfaces", e);
//                        } finally {
//                        	runOnce(DISCOVERY_INTERVAL);
//                        }
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
            		scheduleDateCollect();
            	} catch (Throwable e) {
            		LOG.error("start: Failed to schedule existing interfaces", e);
            	} finally {
            		runOnce(DISCOVERY_INTERVAL,this);
            	}
            }
        };
    }
	
	@Override
	protected void onInit() {
		LOG.debug("init: Initializing discovery daemon");
		
		dao = new CInspectionReportHistoryDAO();
		
		this.runOnce(DISCOVERY_INTERVAL_INIT);
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
            LOG.debug("start: Starting Inspecter scheduler");
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
                    LOG.debug("init: Creating Inspecter scheduler");
                    setScheduler(new LegacyScheduler("Inspecter", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create Inspecter scheduler", e);
                    throw e;
                }
            }
        });
	}

}
