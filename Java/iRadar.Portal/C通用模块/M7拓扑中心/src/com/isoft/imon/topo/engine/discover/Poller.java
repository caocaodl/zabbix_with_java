package com.isoft.imon.topo.engine.discover;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.platform.context.PollingCollectContext;
import com.isoft.imon.topo.platform.context.ProtocolAdapter;
import com.isoft.imon.topo.platform.element.ElementStatus;

/**
 * 轮询器的抽象的泛型类
 * 
 * @author ldd 2014-2-17
 * @param <NE>
 * @param <C>
 */
public abstract class Poller<NE extends NetElement, C> implements Cloneable {

	public static final int UNACCESSIBLE = -1;
	// 轮询ID
	protected int id;
	// 网元设备ID
	protected int elementId;
	// 轮询间隔，单位为毫秒
	protected long interval;
	// 是否启用
	protected boolean enabled;
	// 轮询间隔值
	protected int intervalValue;
	// 轮询的时间单位
	protected char intervalUnit;
	// 上一次的轮询时间
	protected long lastTime;
	// 下一次的轮询时间
	protected long nextTime;
	// 轮询采集环境
	protected PollingCollectContext pcc;
	// 设置标志时间，用来存储轮询关闭时上次轮询的时间
	protected long flagTime;

	public Poller() {
		this.nextTime = 0L;
	}

	/**
	 * 判断当前时间是否大于下次的轮询时间 如果大于，则将最新的轮询时间改为当前时间，返回ture 如果小于，则返回false
	 * 
	 * @return
	 */
	public synchronized boolean isRunnable() {
		long now = System.currentTimeMillis();
		if ((this.enabled) && ((now + 1000) >= this.nextTime)) { // 轮询开启
			this.lastTime = now;
			this.nextTime = (this.lastTime + this.interval);
			return true;
		}
		if (!this.enabled) { // 轮询关闭
			this.lastTime = this.flagTime;
			this.nextTime = 0;
			return true;
		}
		return false;
	}

	/**
	 * 轮询一个网元信息 使用锁的功能，就是在并发的情况下，也只能运行一个网元的轮询，其他的阻塞、等待。
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Bag polling(NE element) {
		if (this.pcc.getElement().getElementStatus() != ElementStatus.Unmanaged) {
			ProtocolAdapter adapter = this.pcc.getAdapter(getAdapterClazz());
			if (adapter.isUsable()) {
				return collect(element, (C) adapter.getConnector());
			}
		}
		return null;
	}

	/**
	 * 设置轮询采集环境
	 * 
	 * @param pcc
	 */
	public void setPollingCollectContext(PollingCollectContext pcc) {
		this.pcc = pcc;
	}

	/**
	 * 获取一个适配器的类
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Class<? extends ProtocolAdapter> getAdapterClazz();

	/**
	 * 对网元的数据进行采集
	 * 
	 * @param paramNE
	 * @param paramC
	 * @return
	 */
	public abstract Bag collect(NE paramNE, C paramC);

	/**
	 * 获取数据包的名称
	 * 
	 * @return
	 */
	protected String getBagName() {
		Class<?> bagClass = getBagClazz();
		if (bagClass == null) {
			throw new NullPointerException();
		} else {
			return bagClass.getName();
		}
	}

	/**
	 * 获取数据包的对象
	 * 
	 * @return
	 */
	public Class<?> getBagClazz() {
		Method method = null;
		try {
			for (Method _method : getClass().getMethods()) {
				// 方法的名称不等于collect 或者 方法的返回类型不等于Bag 直接进行下次循环
				if ((!_method.getName().equals("collect"))
						|| (_method.getReturnType().getSimpleName()
								.equals("Bag"))) {
					continue;
				}
				method = _method;
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(getClass() + " collect() isn't override");
		}

		if (method == null) {
			return null;
		}
		if (method.getReturnType().getSuperclass() == CompositeBag.class) {
			Type[] params = ((ParameterizedType) method.getReturnType()
					.getGenericSuperclass()).getActualTypeArguments();
			return (Class<?>) params[0];
		}
		if (method.getReturnType() == CompositeBag.class) {
			Type[] params = ((ParameterizedType) method.getGenericReturnType())
					.getActualTypeArguments();
			return (Class<?>) params[0];
		}
		return method.getReturnType();
	}

	/**
	 * 是否启用
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * 设置是否启用
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 获取轮询间隔
	 * 
	 * @return
	 */
	public long getInterval() {
		return this.interval;
	}

	/**
	 * 获取最近轮询时间
	 * 
	 * @return
	 */
	public long getLastTime() {
		// 不管理状态和故障状态下的设备在服务器重启的时候，其上次轮询时间为0，故设置当前时间
		if (this.lastTime == 0L) {
			this.lastTime = System.currentTimeMillis();
		}
		return this.lastTime;
	}

	/**
	 * 获取下次轮询时间
	 * 
	 * @return
	 */
	public long getNextTime() {

		resetInterval();

		return this.nextTime;
	}

	/**
	 * 获取轮询器ID
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * 设置轮询器ID
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 获取网元设备ID
	 * 
	 * @return
	 */
	public int getElementId() {
		return this.elementId;
	}

	/**
	 * 设置网元ID
	 * 
	 * @param elementId
	 */
	public void setElementId(int elementId) {
		this.elementId = elementId;
	}

	/**
	 * 获取轮询间隔值
	 * 
	 * @return
	 */
	public int getIntervalValue() {
		return this.intervalValue;
	}

	/**
	 * 设置轮询间隔值
	 * 
	 * @param intervalValue
	 */
	public void setIntervalValue(int intervalValue) {
		this.intervalValue = intervalValue;
		resetInterval();
	}

	/**
	 * 获取轮询间隔时间单位
	 * 
	 * @return
	 */
	public char getIntervalUnit() {
		return this.intervalUnit;
	}

	/**
	 * 设置轮询间隔时间单位
	 * 
	 * @param intervalUnit
	 */
	public void setIntervalUnit(char intervalUnit) {
		this.intervalUnit = intervalUnit;
		resetInterval();
	}

	/**
	 * 设置间隔时间
	 */
	protected void resetInterval() {
		if (this.intervalUnit == 'H')
			this.interval = (this.intervalValue * 3600 * 1000);
		else if (this.intervalUnit == 'M')
			this.interval = (this.intervalValue * 60 * 1000);
		else if (this.intervalUnit == 'S')
			this.interval = (this.intervalValue * 1000);
		else
			this.interval = (this.intervalValue);
		// 如果轮询设为不启用，则设置下次轮询时间为0
		if (!this.enabled) {
			this.flagTime = this.lastTime;
			this.nextTime = 0;
			return;
		}
		this.nextTime = (this.lastTime + this.interval);
	}
}
