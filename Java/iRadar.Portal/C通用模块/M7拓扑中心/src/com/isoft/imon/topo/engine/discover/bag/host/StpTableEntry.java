package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.engine.discover.bag.SimpleBag;

/**Stp表实体
 * @author Administrator
 * @date 2014年8月6日 
 */
public final class StpTableEntry extends SimpleBag {
	private static final long serialVersionUID = 201307091806011L;

	private String port;

	private int state;

	private int enable;

	private String designatedBridge;

	private String designatedPort;

	public String getDesignatedBridge() {
		return this.designatedBridge;
	}

	public void setDesignatedBridge(String designatedBridge) {
		this.designatedBridge = designatedBridge;
	}

	public String getDesignatedPort() {
		return this.designatedPort;
	}

	public void setDesignatedPort(String designatedPort) {
		this.designatedPort = designatedPort;
	}

	public int getEnable() {
		return this.enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getEntity() {
		return this.designatedPort + "-" + this.designatedBridge;
	}

	public void persist(int elementId, String logTime, List<String> sqls) {
	}

	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof StpTableEntry))) {
			return false;
		}
		StpTableEntry that = (StpTableEntry) obj;

		return (this.designatedBridge.equals(that.getDesignatedBridge()))
				&& (this.designatedPort.equals(that.getDesignatedPort()));
	}

	public int hashCode() {
		int result = 1;
		result = result * 31 + this.designatedBridge.hashCode();
		result = result * 31 + this.designatedPort.hashCode();
		return result;
	}
}
