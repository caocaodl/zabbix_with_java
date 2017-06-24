package com.isoft.biz.vo.platform.topo;

import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.isoft.biz.Delegator;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.util.SysConfigHelper;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.web.action.core.EventsAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class NodeVo {
	private String nodeId;
	private String topoId;
	private String hostId;
	private String category;
	private String tbnailId;
	private String tbnailName;
	private String tagName;
	private String name;
	private String g;
	private String tenantId;
	private String userId;
	private String modifiedAt;
	private String modifiedUser;
	private String createdAt;
	private String createdUser;
	private String icon;
	private boolean checked;
	
	private List<LineVo> lines;

	public NodeVo() {
		lines = new ArrayList<LineVo>();
	}

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

	public String getTbnailId() {
		return tbnailId;
	}

	public void setTbnailId(String tbnailId) {
		this.tbnailId = tbnailId;
	}

	public String getTbnailName() {
		return tbnailName;
	}

	public void setTbnailName(String tbnailName) {
		this.tbnailName = tbnailName;
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

	public List<LineVo> getLines() {
		if(lines == null){
			lines = new ArrayList<LineVo>();
		}
		return lines;
	}

	public void setLines(List<LineVo> lines) {
		this.lines = lines;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	
	public static String ICON_PATH = SysConfigHelper.CONTEXT_PATH + "assets/topo/images/ims/icon/";
	@SuppressWarnings("unchecked")
	private static List<String> VENDORS = EasyList.build("cisco", "h3c", "huawei", "linux", "windows");
	public String getImage(Host host) {
//		String category = host.getCategory().toLowerCase();
//		String enterprise = host.getEnterprise().toLowerCase();
		String symbol = host.getSymbol();
		if (host.getSymbol().startsWith("Windows")) {
			symbol = "Windows";
		}
		symbol = symbol.toLowerCase();
		
//		int iconNum = getIconNum(host);
		
		String img;
//		if (VENDORS.contains(enterprise)) {
//			img = category+"_"+enterprise+ "_" + iconNum + ".gif";
//		} else if (VENDORS.contains(symbol)) {
//			img = category+"_"+symbol + "_"+ iconNum + ".gif";
//		} else {
//			img = category + "_" + iconNum + ".gif";
//		}
		if(VENDORS.get(4).contains(symbol))
			img = "server_windows_1.gif";
		else if(VENDORS.subList(0, 3).contains(symbol))	
			img = "switch_0.gif";
		else
			img = "server_linux_0.gif";
		return ICON_PATH + img;
	}
	
	@SuppressWarnings("unchecked")
	private int getIconNum(final Host host) {
		IIdentityBean idBean = new IdentityBean();
    	return CDelegator.doDelegate(idBean, new Delegator() {
			@Override public Integer doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				CEventGet eget = new CEventGet();
				eget.setSource(EVENT_SOURCE_TRIGGERS);
				eget.setObject(EVENT_OBJECT_TRIGGER);
				eget.setOutput(Defines.API_OUTPUT_REFER);
				eget.setSelectTriggers("priority");
				eget.setHostIds((long)host.getId());
				eget.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));  //1代表活动告警或者活动故障
				
				CArray<Map> events = API.Event(idBean, executor).get(eget);
				
				int maxPriority = -1;
				for(Map event: events) {
					CArray<Map> triggers = Nest.value(event, "triggers").asCArray();
					for(Map trigger: triggers) {
						maxPriority = Math.max(maxPriority, Nest.value(trigger, "priority").asInteger());
					}
				}
				
				if(maxPriority == -1) {
					return TopoUtil.HOST_NORMAL_STATE;
				}else if(Arrays.binarySearch(Nest.as(EventsAction.PRIORITIES_WARNING).asCArray().valuesAsInteger(), maxPriority)>-1) {
					return TopoUtil.HOST_WARN_STATE;
				}else {
					return TopoUtil.HOST_FAULT_STATE;
				}
			}
		});
	}

	public String getX() {
		if (this.getG() == null || "".equals(this.getG())) {
			return "0";
		}
		String[] xywh = this.getG().split(",");
		return xywh[0];
	}

	public String getY() {
		if (this.getG() == null || "".equals(this.getG())) {
			return "0";
		}
		String[] xywh = this.getG().split(",");
		return xywh[1];
	}
	
	
	
}
