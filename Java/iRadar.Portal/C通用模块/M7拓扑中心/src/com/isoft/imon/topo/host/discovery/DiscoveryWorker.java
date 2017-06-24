package com.isoft.imon.topo.host.discovery;

import com.isoft.framework.scheduler.ReadyRunnable;

/**创建一个DiscoveryWorker的进程
 * @author soft
 *
 */
public abstract class DiscoveryWorker implements ReadyRunnable {
	protected DiscoveryEngine engine = DiscoveryEngine.getEngine();

	/**构造方法
	 * @param name
	 */
	public DiscoveryWorker(String name) {
		this.engine.workStart();
	}

	public final void run() {
		try {
			doWork();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.engine.workComplete();
		}
	}

	@Override
	public boolean isReady() {
		return true;
	}

	public abstract void doWork();
}
