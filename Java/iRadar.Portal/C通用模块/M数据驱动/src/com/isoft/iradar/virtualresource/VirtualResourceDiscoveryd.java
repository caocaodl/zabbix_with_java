package com.isoft.iradar.virtualresource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class VirtualResourceDiscoveryd extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(VirtualResourceDiscoveryd.class);
	
	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long DISCOVERY_INTERVAL_INIT = 1000 * 1;
	private final static long DISCOVERY_INTERVAL = 1000 * 30 ;//单线程30秒可接受
	private final static String LOG4J_CATEGORY = "iRadar.virtualResource.discoveryd";
	
	private volatile Scheduler m_scheduler;
	private volatile VirtualResourceDao dao;
	
	public VirtualResourceDiscoveryd() {
		super(LOG4J_CATEGORY);
	}
	
	private void scheduleDateCollect() {
		CArray<Map> existVrs = FuncsUtil.rda_toHash(dao.list(), "hostid_os");
		
		List<VirtualResource> creates = EasyList.build();
		List<Map> updates = EasyList.build();
		
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		for(ResourceType type: ResourceType.ALL) {
			Collection<VirtualResource> vrs = type.getCollector().collect(admClient, type);
			for(VirtualResource vr: vrs) {
				Map existVr = existVrs.remove(vr.getId());
				if(existVr == null) {
					creates.add(vr);
				}else {
					List<String> newIps = vr.getIps();
					Map<String, String> oldIps = Nest.value(existVr, "interfaces").asCArray();
					
					Map update = EasyMap.build(
							"tenantid", Nest.value(existVr, "tenantid").$(),
							"hostid", Nest.value(existVr, "hostid").$());
					
					boolean needUpdate = false;
					if(oldIps==null || newIps.size()!=oldIps.size()){
						needUpdate = true;
						update.put("ips", newIps);
					}else{
						for(String newIp: newIps) {
							if(!oldIps.containsKey(newIp)) {
								needUpdate = true;
								update.put("ips", newIps);
								break;
							}
						}
					}
					
					if(!Cphp.empty(vr.getName())&&!vr.getName().equals(Nest.value(existVr, "name").asString())){
						needUpdate = true;
						update.put("name", vr.getName());
					}
					
					if(needUpdate)
						updates.add(update);
				}
			}
		}
		
		if(!creates.isEmpty()) {
			Map<String, List<VirtualResource>> map = EasyList.groupClazzBy(creates, "tenantId");
			
			for(Entry<String, List<VirtualResource>> entry: map.entrySet()) {
				String tenantId = entry.getKey();
				List<VirtualResource> vsr = entry.getValue();
				dao.create(tenantId, vsr);
			}
		}
		
		if(!updates.isEmpty()) {
			Map<String, List<Map>> map = EasyList.groupBy(updates, "tenantid");
			for(Entry<String, List<Map>> entry: map.entrySet()) {
				String tenantId = entry.getKey();
				List<Map> vsr = entry.getValue();
				dao.update(tenantId, vsr);
			}
		}
		
		if(!existVrs.isEmpty()) {
			Map<String, List<Map>> map = EasyList.groupBy(existVrs.values(), "tenantid");
			for(Entry<String, List<Map>> entry: map.entrySet()) {
				String tenantId = entry.getKey();
				List<Map> vsr = entry.getValue();
				dao.delete(tenantId, CArray.valueOf(vsr));
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
//						} finally {
//							runOnce(DISCOVERY_INTERVAL);
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
		
		dao = new VirtualResourceDao();
		
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
            LOG.debug("start: Starting IaaSDiscoverer scheduler");
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
                    LOG.debug("init: Creating IaaSDiscoverer scheduler");
                    setScheduler(new LegacyScheduler("IaaSDiscoverer", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create IaaSDiscoverer scheduler", e);
                    throw e;
                }
            }
        });
	}
}
