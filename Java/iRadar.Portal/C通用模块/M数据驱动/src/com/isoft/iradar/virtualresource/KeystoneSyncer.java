package com.isoft.iradar.virtualresource;

import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.daemon.AbstractServiceDaemon;
import com.isoft.framework.scheduler.LegacyScheduler;
import com.isoft.framework.scheduler.ReadyRunnable;
import com.isoft.framework.scheduler.Scheduler;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.imon.topo.core.logging.Logging;
import com.isoft.lang.Clone;

public class KeystoneSyncer extends AbstractServiceDaemon {
	private static final Logger LOG = LoggerFactory.getLogger(KeystoneSyncer.class);
	
	private final static int MAX_SCHEDULER_THREADS = 1;
	private final static long SYNC_INTERVAL_INIT = 1000 * 1;
	private final static long SYNC_INTERVAL = 1000 * 30 ;//单线程30秒可接受
	private final static String LOG4J_CATEGORY = "iRadar.IaaS.Keystone.Syncer";
	
	private volatile Scheduler m_scheduler;
	private volatile KeystoneSyncDao dao;
	
	public static List<Tenant> tenantsData = new ArrayList();
	
	public KeystoneSyncer() {
		super(LOG4J_CATEGORY);
	}
	
	private void scheduleDateCollect() {
		IaaSClient osClient = OpsUtils.getOpenStackClientForAdmin();
		Keystone keystone = osClient.getIdentityClient();
		List<Tenant> tenants = keystone.tenants().list().execute().getList();
		List<User> users = keystone.users().list().execute().getList();
		
		tenantsData = Clone.deepcopy(tenants);
		
		Map<String, Map> osTenants = new HashMap();
		for (Tenant tenant : tenants) {
			osTenants.put(tenant.getId(), map(
					"tenantid",tenant.getId(),
					"name",tenant.getName(),
					"parent",0,
					"status",tenant.getEnabled()?0:1,
					"proxy_hostid",null,
					"loadfactor",0,
					"enabled",tenant.getEnabled()?1:0
			));
		}
		
		Map<String, Map> osUsers = new HashMap();
		for (User user : users) {
			user = keystone.users().show(user.getId()).execute();
			osUsers.put(user.getId(), map(
					"tenantid",user.getTenantId(),
					"userid","admin".equalsIgnoreCase(user.getName())?1:user.getId(),
					"name",user.getName(),
					"alias",user.getName(),
					"email",user.getEmail(),
					"enabled",user.getEnabled()?1:0
			));
		}
		
		List<Map> updates = new ArrayList();
		List<Map> creates = new ArrayList();
		
		List<Map> dbTenants = dao.listTenants();
		for (Map dbtenant : dbTenants) {
			Map ostenant = osTenants.remove(dbtenant.get("tenantid"));
			if (ostenant == null) {
				if((Integer)dbtenant.get("enabled")==1){
					dbtenant.put("enabled",0);
					updates.add(dbtenant);
				}
			} else if((dbtenant.get("name")==null && ostenant.get("name")!=null)
					|| (!StringUtils.equals((String)ostenant.get("name"), (String)dbtenant.get("name")))
					|| (Integer)ostenant.get("enabled") != (Integer)dbtenant.get("enabled")){
				dbtenant.put("name",ostenant.get("name"));
				dbtenant.put("enabled",ostenant.get("enabled"));
				updates.add(dbtenant);
			}
		}
		
		for (Map dbtenant : osTenants.values()) {
			creates.add(dbtenant);
		}
		
		dao.syncTenants(creates, updates);
		
		updates.clear();
		creates.clear();
		
		List<Map> dbUsers = dao.listUsers();
		for (Map dbuser : dbUsers) {
			Map osuser = osUsers.remove(dbuser.get("userid"));
			if (osuser == null) {
				if((Integer)dbuser.get("enabled")==1){
					dbuser.put("enabled",0);
					updates.add(dbuser);
				}
			} else if((dbuser.get("name")==null && osuser.get("name")!=null)
					|| (!StringUtils.equals((String)osuser.get("name"), (String)dbuser.get("name")))
					|| (dbuser.get("alias")==null && osuser.get("alias")!=null)
					|| (!StringUtils.equals((String)osuser.get("alias"), (String)dbuser.get("alias")))
					|| (Integer)osuser.get("enabled") != (Integer)dbuser.get("enabled")){
				dbuser.put("name",osuser.get("name"));
				dbuser.put("alias",osuser.get("alias"));
				dbuser.put("enabled",osuser.get("enabled"));
				updates.add(dbuser);
			}
		}
		
		for (Map dbuser : osUsers.values()) {
			creates.add(dbuser);
		}
		
		dao.syncUsers(creates, updates);
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
            		scheduleDateCollect();
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
		LOG.debug("init: Initializing IaaSKeystoneSyncer daemon");
		dao = new KeystoneSyncDao();
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
            LOG.debug("start: Starting IaaSKeystoneSyncer scheduler");
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
                    LOG.debug("init: Creating IaaSKeystoneSyncer scheduler");
                    setScheduler(new LegacyScheduler("IaaSKeystoneSyncer", MAX_SCHEDULER_THREADS));
                } catch (final RuntimeException e) {
                    LOG.error("init: Failed to create IaaSKeystoneSyncer scheduler", e);
                    throw e;
                }
            }
        });
	}
}
