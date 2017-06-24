package com.isoft.imon.topo.platform.context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.element.BcastDomain;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.platform.element.ElementStatus;
import com.isoft.imon.topo.web.view.TopoView;

/**
 * 
 * 拓扑池类
 * 
 * @author Administrator
 * 
 * @date 2014年8月4日
 */
public class TopoPool {
	private static final Logger LOG = LoggerFactory.getLogger(TopoPool.class);
	
	protected List<NetElement> elements;
	protected List<Host> hosts;

	public TopoPool() {
		this.hosts = new CopyOnWriteArrayList<Host>();
		this.elements = new CopyOnWriteArrayList<NetElement>();
	}

	/**
	 * 添加网元
	 * 
	 * @param element
	 * @return
	 */
	public synchronized boolean addElement(NetElement element) {
		if (this.elements.contains(element)) {
			return false;
		}
		if ((element instanceof Link)) {
			Link link = (Link) element;
			if(LOG.isDebugEnabled()) {
				LOG.debug("discovery link: " + link);
			}
			
			if (!checkLink(link)) {
				if(LOG.isInfoEnabled()) {
					LOG.info("重复链路:" + link.toString());
				}
				return false;
			}
			if (link.getStartId() > link.getEndId()) {
				int temp = link.getStartId();
				link.setStartId(link.getEndId());
				link.setEndId(temp);
			}
		}

		if (element.getId() == 0) {
			throw new IllegalArgumentException("ID未被正常设置");
		}

		if ((element instanceof Host)){
			this.hosts.add((Host) element);
		}
		this.elements.add(element);
		return true;
	}

	/**
	 * 检查是否有链路
	 * 
	 * @param link
	 * @return
	 */
	private boolean checkLink(Link link) {
		for (NetElement ne : this.elements) {
			if (!"Link".equals(ne.getCategory())) {
				continue;
			}
			Link _link = (Link) ne;
			if ((_link.getStartIfIndex().equals(link.getStartIfIndex())) && (_link.getEndIfIndex().equals(link.getEndIfIndex()))
					&& (_link.getStartId() == link.getStartId()) && (_link.getEndId() == link.getEndId()))
				return false;
			if ((_link.getStartIfIndex().equals(link.getStartIfIndex())) && (_link.getEndIfIndex().equals(link.getEndIfIndex()))
					&& (_link.getStartId() == link.getEndId()) && (_link.getEndId() == link.getStartId()))
				return false;
		}
		return true;
	}
	
	public synchronized boolean addBroadcastDomain(BcastDomain domain) {
		boolean merge = false;
		for (NetElement ne : this.elements) {
			if (!(ne instanceof BcastDomain)) {
				continue;
			}
			BcastDomain bd = (BcastDomain) ne;
			boolean cross = false;
			for (String netAddress : domain.getNetAddresses()) {
				if (bd.contain(netAddress)) {
					cross = true;
					break;
				}
			}
			if (!cross) {
				for (String netAddress : bd.getNetAddresses())
					if (domain.contain(netAddress)) {
						cross = true;
						break;
					}
			}
			if (cross) {
				for (String netAddress : domain.getNetAddresses())
					bd.addAddress(netAddress);
				merge = true;
				break;
			}
		}
		if (!merge)
			return addElement(domain);
		return false;
	}

	/**
	 * 移除网元
	 * 
	 * @param element
	 */
	public void removeElement(NetElement element) {
		ContextFactory.getFactory().removeContext(element.getId());

		element.setElementStatus(ElementStatus.Unmanaged);
		this.elements.remove(element);
		if ((element instanceof Host))
			this.hosts.remove(element);
		System.out.println(element.toString() + "\n退出轮询池");
	}

	/**
	 * 根据设备ID获取设备信息
	 * 
	 * @param id
	 * @return Host
	 */
	public Host getHostByID(int id) {
		for (Host host : this.hosts) {
			if (host.getId() == id)
				return host;
		}
		return null;
	}

	/**
	 * 根据ID获取网元
	 * 
	 * @param id
	 * @return
	 */
	public NetElement getElementByID(int id) {
		for (NetElement element : this.elements) {
			if (element.getId() == id)
				return element;
		}
		return null;
	}

	/**
	 * 根据IP地址获取网元
	 * 
	 * @param ip
	 * @return
	 */
	public NetElement getElementByIP(String ip) {
		if (ip != null)
			for (NetElement ne : this.elements) {
				if (ip.equals(ne.getIpAddress()))
					return ne;
				Host host = getHostByIP(ip);
				if (host != null)
					return host;
			}
		return null;
	}

	/**
	 * 根据IP地址获取主机
	 * 
	 * @param ip
	 * @return
	 */
	public Host getHostByIP(String ip) {
		if (ip != null)
			for (Host host : this.hosts) {
				if (host.getIpAddress().equals(ip))
					return host;
				if (host.getIfByIP(ip) != null) {
					return host;
				}
			}
		return null;
	}

	/**
	 * 根据MAC地址获取主机
	 * 
	 * @param mac
	 * @return
	 */
	public Host getHostByMac(String mac) {
		if (mac != null)
			for (Host host : this.hosts) {
				if (host.getIfByMac(mac) != null) {
					return host;
				}
			}
		return null;
	}

	/**
	 * 根据序列号获取主机
	 * 
	 * @param num
	 * @return
	 */
	public Host getHostBySerialNum(String num) {
		if (num != null) {
			for (Host host : this.hosts)
				if (num.equals(host.getSerialNum())) {
					System.out.println(num + "已经存在:::::");
					return host;
				}
		}
		return null;
	}

	/**
	 * 根据NET地址获取广播域
	 * 
	 * @param netAddress
	 * @return
	 */
	public BcastDomain getBroadcastDomain(String netAddress) {
		for (NetElement element : this.elements) {
			if ((element instanceof BcastDomain)) {
				BcastDomain bd = (BcastDomain) element;
				if (bd.contain(netAddress))
					return bd;
			}
		}
		return null;
	}

	/**
	 * 根据Net地址获取子网
	 * 
	 * @param netAddress
	 * @return
	 */
	public Subnet getSubnetByIP(String netAddress) {
		for (NetElement ne : this.elements) {
			if (ne.getCategory().equals("Subnet")) {
				Subnet subnet = (Subnet) ne;
				if (subnet.getNetAddress().equals(netAddress))
					return subnet;
			}
		}
		return null;
	}

	/**
	 * 通过桥MAC获取主机
	 * 
	 * @param mac
	 * @return
	 */
	public Host getHostByBridgeMac(String mac) {
		if (mac != null) {
			for (Host host : this.hosts)
				if (mac.equals(host.getBridgeMac()))
					return host;
		}
		return null;
	}

	/**
	 * 通过ID获取链路
	 * 
	 * @param id
	 * @return
	 */
	public Link getLinkByID(int id) {
		for (NetElement ne : this.elements) {
			if ((ne.getCategory().equals("Link")) && (ne.getId() == id))
				return (Link) ne;
		}
		return null;
	}

	/**
	 * 通过节点ID获取链路
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<Link> getLinksByNodeID(int nodeId) {
		List<Link> links = null;
		for (NetElement ne : this.elements) {
			if (ne.getCategory().equals("Link")) {
				Link link = (Link) ne;
				if ((link.getStartId() == nodeId) || (link.getEndId() == nodeId)) {
					if (links == null)
						links = new ArrayList<Link>();
					links.add(link);
				}
			}
		}
		return links;
	}

	/**
	 * 获取链路
	 * 
	 * @param sid
	 * @param eid
	 * @return
	 */
	public Link getLink(int sid, int eid) {
		for (NetElement ne : this.elements) {
			if (ne.getCategory().equals("Link")) {
				Link link = (Link) ne;
				if ((link.getStartId() == sid) && (link.getEndId() == eid))
					return link;
				if ((link.getStartId() == eid) && (link.getEndId() == sid))
					return link;
			}
		}
		return null;
	}

	/**
	 * 通过IP地址获取子网
	 * 
	 * @param ipAddress
	 * @return
	 */
	public Subnet getSubnetByInsideIP(String ipAddress) {
		for (NetElement ne : this.elements) {
			if (ne.getCategory().equals("Subnet")) {
				Subnet subnet = (Subnet) ne;
				if (subnet.ipInScope(ipAddress))
					return subnet;
			}
		}
		return null;
	}

	/**
	 * 通过索引获取链路
	 * 
	 * @param hostId
	 * @param ifIndex
	 * @return
	 */
	public Link getLinkByIndex(int hostId, String ifIndex) {
		for (NetElement ne : this.elements) {
			if (!ne.getCategory().equals("Link")) {
				continue;
			}
			Link link = (Link) ne;
			if ((link.getStartId() == hostId) && (ifIndex.equals(link.getStartIfIndex())))
				return link;
			if ((link.getEndId() == hostId) && (ifIndex.equals(link.getEndIfIndex())))
				return link;
		}
		return null;
	}

	/**
	 * 获取主机集合列表
	 * 
	 * @return
	 */
	public List<Host> getHosts() {
		return this.hosts;
	}

	/**
	 * 获取网元集合列表
	 * 
	 * @return
	 */
	public List<NetElement> getElements() {
		return this.elements;
	}

	/**
	 * 获取网元数目
	 * 
	 * @return
	 */
	public int getElementNum() {
		int num = 0;
		for (NetElement ne : this.elements) {
			boolean b = ((ne instanceof Link)) || ((ne instanceof Subnet));
			if (b)
				continue;
			num++;
		}
		return num;
	}

	/**
	 * 通过类型获取网元集合列表
	 * 
	 * @param category
	 * @return
	 */
	public List<NetElement> getElementsByCategory(String category) {
		List<NetElement> list = new ArrayList<NetElement>();
		for (NetElement ne : this.elements) {
			if (category.equals(ne.getCategory()))
				list.add(ne);
		}
		return list;
	}

	/**
	 * 清除
	 */
	public void unload() {
		this.elements.clear();
		this.hosts.clear();
	}
	
	public void setup() {
		//FIXME
		IIdentityBean idBean = new IdentityBean();
		TopoView.getInstance().refreshElements(idBean, elements);
	}
}
