package com.isoft.imon.topo.platform.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isoft.imon.topo.engine.discover.Bag;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.Poller;
import com.isoft.imon.topo.util.CommonUtil;

/**
 * 轮询采集上下文类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
@SuppressWarnings("rawtypes")
public class PollingCollectContext implements Context {
	private static Logger logger = Logger.getLogger(PollingCollectContext.class);
	private NetElement element;
	private List<Poller> pollers;
	private final Map<Class<? extends ProtocolAdapter>, ProtocolAdapter> adapters;
	private final List<Bag> results;
	private boolean occupied;
	private String lastPollingTime;
	private String nextPollingTime;
	// private List<String> sqls = new ArrayList<String>();

	@SuppressWarnings("unchecked")
	public PollingCollectContext(NetElement element, String credence) {
		this.element = element;
		this.pollers = ContextResourcesPool.getPool().getPollers(element, credence);
		this.adapters = new HashMap();
		this.results = new ArrayList<Bag>();
		setContext();
	}

	@SuppressWarnings("unchecked")
	public PollingCollectContext(NetElement element, List<Poller> pollers) {
		this.element = element;
		this.pollers = pollers;
		this.adapters = new HashMap();
		this.results = new ArrayList<Bag>();
		setContext();
	}

	/**
	 * 设置上下文
	 */
	private void setContext() {
		if (!CommonUtil.isEmpty(this.pollers)) {
			for (Poller<?, ?> poller : this.pollers) {
				poller.setPollingCollectContext(this);
			}
		}
	}

	/**
	 * 获取适配器
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ProtocolAdapter<?> getAdapter(Class<? extends ProtocolAdapter> clazz) {
		if (!this.adapters.containsKey(clazz)) {
			try {
				ProtocolAdapter<NetElement> adapter = (ProtocolAdapter) clazz.newInstance();
				adapter.createConnector(this.element);
				this.adapters.put(clazz, adapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (ProtocolAdapter<?>) this.adapters.get(clazz);
	}

	/**
	 * 释放适配器
	 */
	private void freeAdapters() {
		for (ProtocolAdapter<?> adapter : this.adapters.values())
			adapter.freeConnector();
		this.adapters.clear();
	}

	/**
	 * 执行轮询操作
	 */
	@SuppressWarnings("unchecked")
	public void doPolling() {
		if ((this.pollers == null) || (this.occupied)) {
			return;
		}

		this.occupied = true;
		this.results.clear();

		long start = System.currentTimeMillis();
		try {
			for (Poller poller : this.pollers) {
//				if (!ContextFactory.getFactory().isPollingStatus()) {
//					break;
//				}
				if (!poller.isRunnable()) {
					continue;
				}
				try {
					long startX = System.currentTimeMillis();
					Bag bag = poller.polling(this.element);
					if (bag != null) {
						this.results.add(bag);
					}

					if (logger.isDebugEnabled()) {
						logger.debug(poller + " %-% " + elementName() + " %-% " + bag + " USE " + (System.currentTimeMillis() - startX));
					}
				} catch (Exception e) {
					System.out.println("**********bug start***************");
					e.printStackTrace();
					logger.error(this.element.getAlias() + poller.getClass() + " polling failed", e);
				}
			}

//			if (ContextFactory.getFactory().isPollingStatus()) {
//				long startX = System.currentTimeMillis();
//				this.element.getAnalyseContext().push(this.results);
//				if (logger.isDebugEnabled()) {
//					logger.debug("Analyse@-" + " %-% " + elementName() + " %-% " + "-" + " USE " + (System.currentTimeMillis() - startX));
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(this.element.getAlias() + " polling failed", e);
		} finally {
			freeAdapters();
			this.occupied = false;

			if (logger.isDebugEnabled()) {
				logger.debug(elementName() + " use " + (System.currentTimeMillis() - start));
			}
		}
	}

	/**
	 * 获取网元名称
	 * 
	 * @return
	 */
	private String elementName() {
		return (this.element.getId() + "#" + this.element.getAlias());
	}

	/**
	 * 获取网元
	 * 
	 * @return
	 */
	public NetElement getElement() {
		return this.element;
	}

	/**
	 * 获取轮询器集合
	 * 
	 * @return
	 */
	public List<Poller> getPollers() {
		return this.pollers;
	}

	/**
	 * 根据类获取轮询器
	 * 
	 * @param clazz
	 * @return
	 */
	public Poller getPoller(String clazz) {
		for (Poller poller : this.pollers) {
			if (poller.getClass().getName().equals(clazz))
				return poller;
		}
		return null;
	}

	/**
	 * 获取轮询采集名称
	 */
	public String getName() {
		return "polling-collect-" + this.element.getId();
	}

	/**
	 * 根据网元ID获取轮询采集名称
	 * 
	 * @param elementId
	 * @return
	 */
	public static String getName(int elementId) {
		return "polling-collect-" + elementId;
	}

	/**
	 * 获取最近轮询时间
	 * 
	 * @return
	 */
	public String getLastPollingTime() {
		return this.lastPollingTime;
	}

	/**
	 * 设置最近轮询时间
	 * 
	 * @param lastPollingTime
	 */
	public void setLastPollingTime(String lastPollingTime) {
		this.lastPollingTime = lastPollingTime;
	}

	/**
	 * 获取下次轮询时间
	 * 
	 * @return
	 */
	public String getNextPollingTime() {
		return this.nextPollingTime;
	}

	/**
	 * 设置下次轮询时间
	 * 
	 * @param nextPollingTime
	 */
	public void setNextPollingTime(String nextPollingTime) {
		this.nextPollingTime = nextPollingTime;
	}
}
