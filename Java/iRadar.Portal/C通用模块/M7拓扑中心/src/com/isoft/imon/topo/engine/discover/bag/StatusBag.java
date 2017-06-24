package com.isoft.imon.topo.engine.discover.bag;

import java.util.List;

import com.isoft.imon.topo.platform.element.ElementStatus;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;
import com.isoft.imon.topo.platform.policy.thresholdOver.ThresholdOverAnnotation;

/**
 * 网元状态包
 * 
 * @author Administrator
 * 
 * @date 2014年8月6日
 */
@AnalysableAnnotation(label = "网元状态")
public final class StatusBag extends SingleBag {
	private static final long serialVersionUID = 20130709151810L;

	@AnalysableAnnotation(label = "网元状态")
	private ElementStatus elementStatus;

	@ThresholdOverAnnotation(threshold = 500.0D, severity = 2)
	@AnalysableAnnotation(label = "响应时间", unit = "ms")
	private int responseTime;
	private String alert;

	/**
	 * 获取响应时间
	 * 
	 * @return
	 */
	public Integer getResponseTime() {
		if (this.responseTime == -1) {
			return null;
		}
		return this.responseTime;
	}

	/**
	 * 设置响应时间
	 * 
	 * @param responseTime
	 */
	public void setResponseTime(int responseTime) {
		this.responseTime = responseTime;
	}

	/**
	 * 获取网元状态
	 * 
	 * @return
	 */
	public ElementStatus getElementStatus() {
		return this.elementStatus;
	}

	/**
	 * 设置网元状态
	 * 
	 * @param elementStatus
	 */
	public void setElementStatus(ElementStatus elementStatus) {
		this.elementStatus = elementStatus;
	}

	/**
	 * 获取告警
	 * 
	 * @return
	 */
	public String getAlert() {
		return this.alert;
	}

	/**
	 * 设置告警
	 * 
	 * @param alert
	 */
	public void setAlert(String alert) {
		this.alert = alert;
	}
}
