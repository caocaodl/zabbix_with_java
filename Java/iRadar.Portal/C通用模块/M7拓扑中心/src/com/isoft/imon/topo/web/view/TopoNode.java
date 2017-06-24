package com.isoft.imon.topo.web.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyList;

public class TopoNode {
	private Host host;
	private List<Link> links;
	private float x;
	private float y;
	public NodeState state = NodeState.GOOD;
	private String nameInIradar;
	
	public TopoNode(Host host) {
		this.host = host;
		this.links = new ArrayList<Link>();
		this.x = 0f;
		this.y = 0f;
	}
	
	public static String ICON_PATH = com.isoft.imon.topo.util.SysConfigHelper.CONTEXT_PATH + "assets/topo/images/ims/icon/";
	@SuppressWarnings("unchecked")
	private static List<String> VENDORS = EasyList.build("cisco", "h3c", "huawei", "linux", "windows");
	public String getIcon() {
		NetElement ne = host;
		
		String category = host.getCategory().toLowerCase();
		String enterprise = host.getEnterprise().toLowerCase();
		String symbol = host.getSymbol();
		if (host.getSymbol().startsWith("Windows")) {
			symbol = "Windows";
		}
		symbol = symbol.toLowerCase();
		
//		int iconNum = getIconNum(ne);
		
		String img="";
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
	private int getIconNum(NetElement ne) {
		return 0;
	}
	
	public String toJSON() {
		return "{" +
            "id : -1," +	
			"hostId:" + getId() + "," +
			"tbnailId:" + TopoUtil.INIT_NODE_THUMBNAIL_ID + "," +
			"tagName:'" + getTagName() + "'," +
			"category:'" + getCategory() + "'," +
			"name:'" + getName() + "'," +
			(Cphp.empty(getNameInIradar())?"":"visibleName:'" + getNameInIradar() + "'," )+
			(Cphp.empty(getNameInIradar())?"":"nameInIradar:'" + getNameInIradar() + "'," )+
			"searchName:'" + getSearchName() + "'," +
			"image:'"+ getIcon() + "'," +
			"width:"+getWidth()+"," +
			"height:"+getHeight()+"," +
		"}";
	}
	public String getSearchName(){
		return host.getAlias();
	}
	public int getId(){
		return host.getId();
	}
	public String getG(){
		return this.getX()+","+this.getY()+","+this.getWidth()+","+this.getHeight();
	}
	public String getCategory() {
		return host.getCategory();
	}
	public String getTypeName() {
		return host.getCategory()+"_"+host.getEnterprise();
	}
	public String getTagName() {
		return getTypeName()+"_"+host.getId();
	}
	public String getName() {
		return host.getAlias();
	}
	public String getNameInIradar() {
		return nameInIradar;
	}
	public void setNameInIradar(String nameInIradar) {
		this.nameInIradar = nameInIradar;
	}
	
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		String tagName = getTagName();
		sb.append("<"+tagName+" g='0,0,77,52' id='"+getId()+"' name='"+getName()+"'>");
		for(Link link: getLinks()) {
			int id = link.getStartId()==host.getId()? link.getEndId(): link.getStartId();
			TopoNode node = TopoView.getInstance().getNode(id);
			sb.append("<line to='"+node.getName()+"' strokeweight='1' name='' g='' color=''/>");
		}
		sb.append("</"+tagName+">");
		return sb.toString();
	}
	
	
	public void toUpdate() {
		links.clear();
		state = NodeState.BAD;
	}
	public void doUpdate() {
		state = NodeState.GOOD;
	}
	
	public void addLink(Link link) {
		links.add(link);
	}
	
	public Host getHost() {
		return host;
	}

	public List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		return host.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TopoNode) {
			return this.host.equals(((TopoNode)obj).host);
		}
		return false;
	}
	
	public int getWidth() {
		return TopoUtil.TOPO_NODE_WIDTH;
	}

	public int getHeight() {
		return TopoUtil.TOPO_NODE_HEIGHT;
	}
	
	public static enum NodeState{
		GOOD, BAD;
	}
}


