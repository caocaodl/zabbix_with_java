package com.isoft.imon.topo.platform.plugin;

import com.isoft.imon.topo.platform.context.ContextResourcesPool;

/**
 * 公共插件抽象类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public abstract class CommonPlugin {
	protected final ContextResourcesPool pool;

	public CommonPlugin() {
		this.pool = ContextResourcesPool.getPool();
	}
}
