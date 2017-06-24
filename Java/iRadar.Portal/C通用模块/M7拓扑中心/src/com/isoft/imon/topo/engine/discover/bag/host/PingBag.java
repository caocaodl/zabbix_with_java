package com.isoft.imon.topo.engine.discover.bag.host;

/**
 * ping包
 * 
 * @author Administrator
 * @date 2014年8月6日
 */
public final class PingBag {
	// 包大小
	private int packetSize;
	// 发送包
	private int sentPackets;
	// 接收包
	private int receivePackets;
	// 最小值
	private int minRtt;
	// 平均值
	private int avgRtt;
	// 最大值
	private int maxRtt;

	public PingBag() {
		this.avgRtt = -1;
	}

	public int getMinRtt() {
		return this.minRtt;
	}

	public void setMinRtt(int minRtt) {
		this.minRtt = minRtt;
	}

	public int getMaxRtt() {
		return this.maxRtt;
	}

	public void setMaxRtt(int maxRtt) {
		this.maxRtt = maxRtt;
	}

	public int getPacketSize() {
		return this.packetSize;
	}

	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	public int getSentPackets() {
		return this.sentPackets;
	}

	public void setSentPackets(int sentPackets) {
		this.sentPackets = sentPackets;
	}

	public int getReceivePackets() {
		return this.receivePackets;
	}

	public void setReceivePackets(int receivePackets) {
		this.receivePackets = receivePackets;
	}

	public int getAvgRtt() {
		return this.avgRtt;
	}

	public void setAvgRtt(int avgRtt) {
		this.avgRtt = avgRtt;
	}

	/**
	 * 转换字符串方法
	 * 
	 * @param scrAddress
	 * @param dstAddress
	 * @return
	 */
	public String toString(String scrAddress, String dstAddress) {
		StringBuffer sb = new StringBuffer(50);
		sb.append("From ").append(scrAddress).append(" Pinging ");
		sb.append(dstAddress).append(",Reply from ").append(dstAddress);
		sb.append(":").append("<br>");
		if (this.sentPackets == 0) {
			sb.append("Request timed out");
		} else {
			sb.append("Packets:").append("<br>");
			sb.append("Sent = ").append(this.sentPackets).append(",");
			sb.append("Received = ").append(this.receivePackets).append(",");
			sb.append("Lost = ").append(this.sentPackets - this.receivePackets)
					.append("(");
			sb.append(
					(this.sentPackets - this.receivePackets) * 100
							/ this.sentPackets).append("% loss),<br>");
			sb.append("Approximate round trip times in milli-seconds:<br>");
			sb.append("Minimum = ").append(this.minRtt).append("ms,");
			sb.append("Maximum = ").append(this.maxRtt).append("ms,");
			sb.append("Average = ").append(this.avgRtt).append("ms");
		}
		return sb.toString();
	}
}
