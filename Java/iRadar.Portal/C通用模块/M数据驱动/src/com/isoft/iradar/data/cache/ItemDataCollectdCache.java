package com.isoft.iradar.data.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.web.action.moncenter.I_LatestDataAction;
import com.isoft.iradar.web.bean.Column;
import com.isoft.iradar.web.bean.Key;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class ItemDataCollectdCache extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(ItemDataCollectdCache.class);
	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long COLLECT_INTERVAL_INIT = 1000 * 30*0;
	private final static long COLLECT_INTERVAL = 1000 * 60 * 2;
	private final static String LOG4J_CATEGORY = "iRadar.item.data.collectd";

	private final static Queue<Map> hostidQueue = new LinkedBlockingQueue<Map>();
	
	private volatile Scheduler m_scheduler;
	
	public ItemDataCollectdCache() {
		super(LOG4J_CATEGORY);
	}

	@Override
	protected void onInit() {
		LOG.debug("init: Initializing collect daemon");
		this.runOnce(COLLECT_INTERVAL_INIT);
	}
	
	private ReadyRunnable monitorCentercheduler() {
        return new ReadyRunnable() {
            @Override public boolean isReady() { return true; }
            @Override public void run() {
            	try {
            		monitorCenterDataCollect();
            	} catch (Throwable e) {
            		LOG.error("start: Failed to schedule existing interfaces", e);
            	} finally {
            		runOnce(COLLECT_INTERVAL,this);
            	}
            }
        };
    }
	
	private ReadyRunnable schedulerParent() {
        return new ReadyRunnable() {
            @Override public boolean isReady() { return true; }
            @Override public void run() {
            	try {
            		getScheduler().schedule(COLLECT_INTERVAL_INIT, monitorCentercheduler());
//            		getScheduler().schedule(COLLECT_INTERVAL_INIT, dashBoradcheduler());
            	} catch (Throwable e) {
            		LOG.error("start: Failed to schedule existing interfaces", e);
            		runOnce(COLLECT_INTERVAL,this);
            	}
            }
        };
    }
	
	private void runOnce(long interval) {
		//加入处理线程
		getScheduler().schedule(interval, schedulerParent());
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
            LOG.debug("start: Starting IaaSItemDataCollect scheduler");
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
                    LOG.debug("init: Creating Item Data Collect scheduler");
                    setScheduler(new LegacyScheduler("IaaSItemDataCollect", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create Item Data Collect cheduler", e);
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
	
    public CArray<Map> hostsList(final long groupId) {
    	return delegate(new Delegator<CArray<Map>>() {
			@Override public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				CHostGet options = new CHostGet();
				options.setOutput(new String[] {"hostid"});
				options.setGroupIds(groupId);
				CArray<Map> hosts = API.Host(idBean, sqlE).get(options);
				return hosts;
			}
		});
    }
    
    public Object fillHostItem() {
    	return delegate(new Delegator<CArray<Map>>() {
    		@Override public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
    			IRadarContext.init(sqlE, idBean);
    			try {
    				Map<String,Map> configs = I_LatestDataAction.CONFIGS;
        			for(Entry<String,Map> e: configs.entrySet()){
        				Map groupColumns = e.getValue();
        				long groupId = ((IMonGroup)(groupColumns.get("group"))).id();
        				CArray<Column> columns = (CArray) groupColumns.get("columns");
        				CArray<Map> hosts = hostsList(groupId);
        				for (Map hostMap : hosts) {
        					Long hostId = Nest.value(hostMap, "hostid").asLong();
        					for (Column column : columns) {
        						for(Key key: column.keys()) {
        							if(Cphp.isset(key.itemKeyCArray())) {
        								for(ItemsKey itemKey: key.itemKeyCArray()) {
        									addQueue(hostId, itemKey);
        								}
        							}else {
        								ItemsKey itemKey = key.itemKey();
        								addQueue(hostId, itemKey);
        							}
        						}
        					}
        				}
        			}
    			}finally {
    				IRadarContext.clear();
    			}
    			return null;
			}
    	});
    }
    
    private void addQueue(Long hostid, ItemsKey itemKey) {
    	hostidQueue.offer(CArray.array(hostid, itemKey.getValue()));
    }
    
    private void monitorCenterDataCollect() {
		LOG.debug("===Cache start=====");
		long start = System.currentTimeMillis();
		
		if(hostidQueue.isEmpty()) {
			this.fillHostItem();
		}
		
		while(true) {
			CArray map = (CArray)hostidQueue.poll();
			if(map == null) 
				break;
			Long hostid = Nest.value(map, 0).asLong();
			String key = Nest.value(map, 1).asString();
			cacheItemData(hostid, key);
		}
		
		LOG.debug("===Cache end=====use:"+(System.currentTimeMillis()-start));
	}
    
    private void cacheItemData(final Long hostid,final String key) {
    	delegate(new Delegator<CArray<Map>>() {
    		@Override public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
    			IRadarContext.init(sqlE, idBean);
    			try {
    		    	CArray<Map> items = DataDriver.getItemsBykey(IRadarContext.getSqlExecutor(), hostid, key);
    				CArray<CArray<Map>> values = LatestValueHelper.fetchValue(items, false);
    				for(Map item: items) {
    					int delay = Nest.value(item, "delay").asInteger();
    					if(delay!=0 && delay < 600) 
    						break; //对有采集周期（非trapper）且小10分钟的（如数据库状态），需要保持最新状态，不进行缓存
    					
    					Object itemid = Nest.value(item, "itemid").$();
    					CArray<Map> historys = values.get(itemid);
    					if(!Cphp.empty(historys)) {
    						String key_ = Nest.value(item, "key_").asString();
    						Object value = Nest.value(historys, 0, "value").$();
    						CacheHelper.cacheHostItemData(hostid, key_, CArray.map(itemid, CArray.array(CArray.map("value", value))));
    					}
    				}
    			}catch(Exception e) {
    				LOG.error("~~~~~Fetch Cache Data Error: ", e);
    			}finally {
    				IRadarContext.clear();
    			}
    			return null;
    		}
    	});
    }
}