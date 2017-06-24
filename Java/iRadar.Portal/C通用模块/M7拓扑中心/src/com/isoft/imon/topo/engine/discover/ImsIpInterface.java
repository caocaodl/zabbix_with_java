package com.isoft.imon.topo.engine.discover;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

import com.isoft.imon.topo.core.utils.InetAddressUtils;
import com.isoft.iradar.inc.Defines;

/**
 * <p>ImsIpInterface class.</p>
 */
public class ImsIpInterface implements Serializable {
	private static final long serialVersionUID = 5875339681538434428L;

	private String m_id;
	private InetAddress m_ipAddress;
	private NetElement m_node;
	private int m_isSnmpPrimary = Defines.INTERFACE_PRIMARY;
	private Date m_ipLastCapsdPoll;
	private String netmask;
	
	@Override
	public String toString() {
		return m_ipAddress.toString();
	}

	public ImsIpInterface() { }
	
	public ImsIpInterface(String ip, NetElement node) {
		this(InetAddressUtils.addr(ip), node);
	}
	
	public ImsIpInterface(InetAddress ipAddress, NetElement node) {
		this.m_ipAddress = ipAddress;
		this.m_node = node;
	}
	
	
	
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getId() {
        return m_id;
    }
	
    public void setId(String id) {
        m_id = id;
    }
	
	public InetAddress getIpAddress() {
        return m_ipAddress;
    }

    public void setIpAddress(InetAddress ipaddr) {
        m_ipAddress = ipaddr;
    }
	
	public NetElement getNode() {
		return m_node;
	}
	
	public void setNode(NetElement node) {
        m_node = node;
    }
	
	public int getIsSnmpPrimary() {
        return m_isSnmpPrimary;
    }
	
	public void setIsSnmpPrimary(int issnmpprimary) {
        m_isSnmpPrimary = issnmpprimary;
    }
	
	public Date getIpLastCapsdPoll() {
        return m_ipLastCapsdPoll;
    }
	
	public String getInterfaceId() {
        return getId() == null? null : getId().toString();
    }

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
