package com.isoft.imon.topo.engine.discover.bag.host;

import com.isoft.imon.topo.engine.discover.bag.SingleBag;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;
import com.isoft.imon.topo.platform.policy.thresholdOver.ThresholdOverAnnotation;

@AnalysableAnnotation(label = "链路性能")
public final class LinkSpeedBag extends SingleBag {
	private static final long serialVersionUID = 201307091522101L;

	/**
	 * 下行流量
	 */
	private long inSpeed;

	/**
	 * 上行流量
	 */
	private long outSpeed;

	@AnalysableAnnotation(label = "下行带宽利用率", unit = "%")
	@ThresholdOverAnnotation(violateUpper = 1, threshold = 80.0D, severity = 2)
	private float inPercentage;

	@AnalysableAnnotation(label = "上行行带宽利用率", unit = "%")
	@ThresholdOverAnnotation(violateUpper = 1, threshold = 80.0D, severity = 2)
	private float outPercentage;

	private long frameVolume;

	private long broadcastVolume;

	private long discardRate;

	private long errorRate;

	public long getInSpeed() {
		return this.inSpeed;
	}

	public void setInSpeed(long inSpeed) {
		this.inSpeed = inSpeed;
	}

	public long getOutSpeed() {
		return this.outSpeed;
	}

	public void setOutSpeed(long outSpeed) {
		this.outSpeed = outSpeed;
	}

	public float getInPercentage() {
		return this.inPercentage;
	}

	public void setInPercentage(float inPercentage) {
		this.inPercentage = inPercentage;
	}

	public float getOutPercentage() {
		return this.outPercentage;
	}

	public void setOutPercentage(float outPercentage) {
		this.outPercentage = outPercentage;
	}

	public long getFrameVolume() {
		return this.frameVolume;
	}

	public void setFrameVolume(long frameVolume) {
		this.frameVolume = frameVolume;
	}

	public long getBroadcastVolume() {
		return this.broadcastVolume;
	}

	public void setBroadcastVolume(long broadcastVolume) {
		this.broadcastVolume = broadcastVolume;
	}

	public long getDiscardRate() {
		return this.discardRate;
	}

	public void setDiscardRate(long discardRate) {
		this.discardRate = discardRate;
	}

	public long getErrorRate() {
		return this.errorRate;
	}

	public void setErrorRate(long errorRate) {
		this.errorRate = errorRate;
	}
}
