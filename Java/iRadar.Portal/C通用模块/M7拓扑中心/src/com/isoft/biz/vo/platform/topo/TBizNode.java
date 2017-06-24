package com.isoft.biz.vo.platform.topo;

import java.util.ArrayList;
import java.util.List;

import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.util.SysConfigHelper;
import com.isoft.iradar.core.utils.EasyList;

public class TBizNode {
	private String nodeId;
	private String topoId;
	private String hostId;
	private String priority;
	private String tagName;
	private String name;
	private String g;
	private String remark;
	private String strokeweight;
	private String fill;
	private String stroke;
	private String tenantId;
	private String userId;
	private String modifiedAt;
	private String modifiedUser;
	private String createdAt;
	private String createdUser;
	private String type;
	private String ownerHost;
	
	private List<TBizLine> lines;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getTopoId() {
		return topoId;
	}

	public void setTopoId(String topoId) {
		this.topoId = topoId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(String modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getModifiedUser() {
		return modifiedUser;
	}

	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	
	public static String ICON_PATH = SysConfigHelper.CONTEXT_PATH + "assets/topo/images/ims/icon/";
	@SuppressWarnings("unchecked")
	private static List<String> VENDORS = EasyList.build("cisco", "h3c", "huawei", "linux", "windows");
	public String getImage(Host host) {
		String category = host.getCategory().toLowerCase();
		String enterprise = host.getEnterprise().toLowerCase();
		String symbol = host.getSymbol();
		if (host.getSymbol().startsWith("Windows")) {
			symbol = "Windows";
		}
		symbol = symbol.toLowerCase();
		
		int iconNum = getIconNum(host);
		
		String img;
		if (VENDORS.contains(enterprise)) {
			img = category+"_"+enterprise+ "_" + iconNum + ".gif";
		} else if (VENDORS.contains(symbol)) {
			img = category+"_"+symbol + "_"+ iconNum + ".gif";
		} else {
			img = category + "_" + iconNum + ".gif";
		}
		return ICON_PATH + img;
	}
	
	private int getIconNum(Host host) {
		return 0;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<TBizLine> getLines() {
		if(lines == null){
			lines = new ArrayList<TBizLine>();
		}
		return lines;
	}

	public void setLines(List<TBizLine> lines) {
		this.lines = lines;
	}

	public String getStrokeweight() {
		return strokeweight;
	}

	public void setStrokeweight(String strokeweight) {
		this.strokeweight = strokeweight;
	}

	public String getFill() {
		return fill;
	}

	public void setFill(String fill) {
		this.fill = fill;
	}

	public String getStroke() {
		return stroke;
	}

	public void setStroke(String stroke) {
		this.stroke = stroke;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwnerHost() {
		return ownerHost;
	}

	public void setOwnerHost(String ownerHost) {
		this.ownerHost = ownerHost;
	}
}
