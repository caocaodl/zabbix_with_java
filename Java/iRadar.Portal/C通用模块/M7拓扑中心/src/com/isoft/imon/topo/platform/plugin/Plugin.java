package com.isoft.imon.topo.platform.plugin;

/**
 * 插件接口
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public interface Plugin {
	public void register();

	public void start();

	public void shutdown();

	public String getName();
}
