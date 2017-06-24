package com.isoft.imon.topo.platform.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isoft.imon.topo.platform.plugin.Plugin;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 上下文工厂类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public final class ContextFactory {
	private static Logger logger = Logger.getLogger(ContextFactory.class);
	private static ContextFactory factory = new ContextFactory();
	// 轮询组标识码
	public static final int SUIT_POLLING = 1;
	// 发现模式组标识码
	public static final int SUIT_DISCOVERY = 2;
	// 发现和轮询模式组标识码
	public static final int SUIT_DISCOVERY_AND_POLLING = 3;
	// 轮询采集环境
	private final List<PollingCollectContext> runnables;
	private final Map<String, Context> contexts;
	// 插件集合
	private final List<Plugin> plugins;

	/**
	 * 获取上下文工厂
	 * 
	 * @return
	 */
	public static ContextFactory getFactory() {
		return factory;
	}

	private ContextFactory() {
		this.contexts = Collections.synchronizedMap(new HashMap<String, Context>());
		this.runnables = new ArrayList<PollingCollectContext>();
		this.plugins = new ArrayList<Plugin>();
	}

	/**
	 * 注册
	 * 
	 * @param context
	 */
	public void register(Context context) {
		if (this.contexts.containsKey(context.getName())) {
//			throw new IllegalArgumentException("已经存在[" + context.getName() + "]的context");
			return;
		}
		this.contexts.put(context.getName(), context);
		if ((context instanceof PollingCollectContext))
			this.runnables.add((PollingCollectContext) context);
	}

	/**
	 * 获取上下文
	 * 
	 * @param name
	 * @return
	 */
	public Context getContext(String name) {
		if (!this.contexts.containsKey(name)) {
			if (logger.isDebugEnabled()) {
				logger.debug("不存在[" + name + "]的context");
			}
			return null;
		}
		return (Context) this.contexts.get(name);
	}

	/**
	 * 移除上下文
	 * 
	 * @param elementId
	 */
	public void removeContext(int elementId) {
		this.contexts.remove(PollingCollectContext.getName(elementId));
	}

	/**
	 * 运行
	 */
	public void doRunning() {
//		if (this.executor != null)
//			return;
//
//		this.executor = Executors.newScheduledThreadPool(this.runnables.size());
//		int elementNum = PollingPool.getPool().getElements().size();
//		if (elementNum % PER_THREAD_NODES == 0)
//			THREAD_TOTAL = elementNum / PER_THREAD_NODES;
//		else {
//			THREAD_TOTAL = elementNum / PER_THREAD_NODES + 1;
//		}
//		for (int i = 0; i < THREAD_TOTAL; i++) {
//			PollingCaller caller = new PollingCaller(i);
//			long delay = i * PER_THREAD_NODES * 50;
//			this.executor.scheduleAtFixedRate(caller, delay, 60 * 1000L, TimeUnit.MILLISECONDS);
//		}
//		Logger.getLogger(getClass()).info(DateUtil.getCurrentDateTime() + "轮询开始,有" + elementNum + "个网元," + THREAD_TOTAL + "个轮询线程.");
	}

	/**
	 * 注册插件
	 * 
	 * @param plugin
	 */
	public void registerPlugin(Plugin plugin) {
		for (Plugin pp : this.plugins) {
			if (pp.getClass() == plugin.getClass())
				throw new IllegalArgumentException("插件已经存在.");
		}
		this.plugins.add(plugin);
	}

	/**
	 * 获取插件列表
	 * 
	 * @return
	 */
	public List<Plugin> getPlugins() {
		return Collections.unmodifiableList(this.plugins);
	}

	/**
	 * 根据名称判断是否包含该插件
	 * 
	 * @param pluginName
	 * @return
	 */
	public boolean isContainsPlugin(String pluginName) {
		for (Plugin plugin : this.plugins) {
			if (plugin.getClass().getSimpleName().equals(pluginName))
				return true;
		}
		return false;
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		unload();
		logger.info(DateUtil.getCurrentDateTime() + "结束轮询...");
	}

	/**
	 * 卸载
	 */
	public void unload() {
		PollingPool.getPool().unload();
		synchronized (this.runnables) {
			this.runnables.clear();
		}
		synchronized (this.contexts) {
			this.contexts.clear();
		}
	}

	public boolean isPollingStatus() {
		return false;
	}

	public boolean isDiscoveryStatus() {
		return true;
	}
}
