package com.isoft.biz.vo.platform.topo;

import com.isoft.imon.topo.util.TopoUtil;

public class TBizLine {
	private String lineId;
	private String nodeId;
	private String topoId;
	private String toNode;
	private String g;
	private String tagName;
	private String strokeWeight;
	private String name;
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getG() {
		if(g==null){
			return "";
		}
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getToNode() {
		return toNode;
	}

	public void setToNode(String toNode) {
		this.toNode = toNode;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getStrokeWeight() {
		if(strokeWeight==null){
			return TopoUtil.LINE_STROKEWEIGHT_FINE;
		}
		return strokeWeight;
	}

	public void setStrokeWeight(String strokeWeight) {
		this.strokeWeight = strokeWeight;
	}

	public String getTopoId() {
		return topoId;
	}

	public void setTopoId(String topoId) {
		this.topoId = topoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
