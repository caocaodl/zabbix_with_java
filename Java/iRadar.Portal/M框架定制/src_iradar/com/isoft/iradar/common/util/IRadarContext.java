package com.isoft.iradar.common.util;

import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.utils.EasyMap;


public class IRadarContext {
	static ThreadLocal<IRadarContext> iRadarContext = new ThreadLocal<IRadarContext>();
	
	public final static IdentityBean IDBEAN_PLATFORM = new IdentityBean();
	static {
		IDBEAN_PLATFORM.init(map(
			"tenantId", "-",
			"osTenantId", "0",
			"tenantRole", Role.LESSOR.magic(),
			"userId",  "platform_idbean_userid",
			"userName", "platform_idbean_username",
			"admin", "Y",
			"osUser", null
		));
	}
	
	
	public final static String KEY_SQLEXECUTOR = "SQLExecutor";
	public final static String KEY_IDENTITYBEAN = "IdentityBean";
	public final static String KEY_IGNORETENANTFORSQL = "ignoreTenantForSql";
	
	private Map<String, Object> context;
	
	public static void init(SQLExecutor sqlE, IIdentityBean idBean) {
		IRadarContext ctx = new IRadarContext(EasyMap.build()).setSqlExecutor(sqlE).setIdentityBean(idBean);
		setContext(ctx);
	}
    public static void setContext(IRadarContext context) {
    	iRadarContext.set(context);
    }
    public static IRadarContext getContext() {
        return iRadarContext.get();
    }
    
    public static void clear() {
    	getContext().context.clear();
    	getContext().context = null;
    	setContext(null);
    }
    
    
    public static SQLExecutor getSqlExecutor() {
    	return (SQLExecutor)getContext().get(KEY_SQLEXECUTOR);
    }
    public static IIdentityBean getIdentityBean() {
    	return (IIdentityBean)getContext().get(KEY_IDENTITYBEAN);
    }
    
    public static boolean isIgnoreTenantForSql() {
    	Object r = getContext().get(KEY_IGNORETENANTFORSQL);
    	return r==null? false: (Boolean)r;
    }
    public static void setIgnoreTenantForSql(boolean ignore) {
    	getContext().put(KEY_IGNORETENANTFORSQL, ignore);
    }
    

    /**
     * Creates a new ActionContext initialized with another context.
     *
     * @param context a context map.
     */
    public IRadarContext(Map<String, Object> context) {
        this.context = context;
    }
    
    public Object get(String key) {
    	return context.get(key);
    }
    public IRadarContext put(String key, Object value) {
    	context.put(key, value);
    	return this;
    }
    
    public IRadarContext setSqlExecutor(SQLExecutor sqlE) {
    	this.context.put(KEY_SQLEXECUTOR, sqlE);
    	return this;
    }
    public IRadarContext setIdentityBean(IIdentityBean sqlE) {
    	this.context.put(KEY_IDENTITYBEAN, sqlE);
    	return this;
    }
}
