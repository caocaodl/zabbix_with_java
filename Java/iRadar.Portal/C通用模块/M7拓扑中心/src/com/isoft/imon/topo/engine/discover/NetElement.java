package com.isoft.imon.topo.engine.discover;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.imon.topo.core.utils.InetAddressUtils;
import com.isoft.imon.topo.platform.context.ContextFactory;
import com.isoft.imon.topo.platform.context.PollingCollectContext;
import com.isoft.imon.topo.platform.element.ElementStatus;
import com.isoft.iradar.inc.Defines;

/**
 * 网元设备
 * 
 * @author ldd 2014-2-19
 */
public abstract class NetElement {
	private static final Logger LOG = LoggerFactory.getLogger(NetElement.class);
	
	// ID
	protected int id;
	// 别名
	protected String alias;
	// 类型
	protected String category;
	// 标志符
	protected String symbol;
	// 凭证Map
	protected Map<String, Credence> credences;
	// 网元设备状态
	protected ElementStatus status;
	
	// 轮询采集环境
	protected PollingCollectContext pcc;
	
	private Map<String, Bag> bags;
	
    private Set<ImsIpInterface> m_ipInterfaces = new LinkedHashSet<ImsIpInterface>();

    
    
	/**
	 * 构造方法，初始化参数
	 */
	public NetElement() {
		this.symbol = "unknown";
		this.status = ElementStatus.Unknown;
		this.bags = new HashMap<String, Bag>();
	}
	
	
	/**
	 * 通过凭证创建注册轮询环境
	 * 
	 * @param credence
	 *            void
	 */
	public void createContext(String credence) {
		this.pcc = new PollingCollectContext(this, credence);
		try {
			ContextFactory.getFactory().register(this.pcc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PollingCollectContext getCollectContext() {
		return this.pcc;
	}
	
	public String getIpAddress() {
		return getPrimaryInterface().getIpAddress().getHostAddress();
	}
	
	public String getNetmask() {
		return getPrimaryInterface().getNetmask();
	}
	public void setNetmask(String netmask) {
		getPrimaryInterface().setNetmask(netmask);
	}
	
	

	/**
	 * 通过数据包的类型获取Bag的信息
	 * 
	 * @param bagClazz
	 * @return Bag
	 */
	public Bag getBag(Class<? extends Bag> bagClazz) {
		return bags.get(bagClazz.getName());
	}

	/**
	 * 
	 * @param bag
	 *            void
	 */
	public void putBag(Bag bag) {
		if(bag != null) {
			bags.put(bag.getBagName(), bag);
		}
	}

	/**
	 * 
	 * @param bagName
	 *            void
	 */
	public void removeBag(String bagName) {
		bags.remove(bagName);
	}

	/**
	 * 获取ID
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 获取设备类别
	 * 
	 * @return
	 */
	public String getCategory() {
		return this.category;
	}

	/**
	 * 设置设备类别
	 * 
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * 获取标识符
	 * 
	 * @return
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * 设置标识符
	 * 
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * 获取别名
	 * 
	 * @return
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * 设置别名
	 * 
	 * @param alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取网元状态
	 * 
	 * @return
	 */
	public ElementStatus getElementStatus() {
		return this.status;
	}

	/**
	 * 设置网元状态
	 * 
	 * @param status
	 *            void
	 */
	public void setElementStatus(ElementStatus status) {
		this.status = status;
		if (this.status == ElementStatus.Unmanaged) { // || (this.status == ElementStatus.Maintainance)
//			this.severity = AlertSeverity.Normal;
		}
	}

	public int hashCode() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if ((obj == null) || (!(obj instanceof NetElement))) {
			return false;
		}
		NetElement that = (NetElement) obj;
		return this.id == that.getId();
	}

	/**
	 * 
	 * @param credence
	 *            void
	 */
	public void putCredence(Credence credence) {
		if (this.credences == null) {
			this.credences = new HashMap<String, Credence>();
		}
		this.credences.put(credence.getType(), credence);
	}

	/**
	 * 
	 * @param type
	 * @return Credence
	 */
	public Credence getCredence(String type) {
		if (this.credences == null) {
			return null;
		}
		return this.credences.get(type);
	}

	/**
	 * 
	 * @return List<Credence>
	 */
	public List<Credence> getCredences() {
		if (this.credences == null) {
			return null;
		}
		return new ArrayList<Credence>(this.credences.values());
	}
	

	
	
	
	
	public Set<ImsIpInterface> getIpInterfaces() {
        return m_ipInterfaces;
    }
	
    /**
     * <p>getIpInterfaceByIpAddress</p>
     * 
     * @param ipAddress a {@link java.lang.String} object.
     
     */
    public ImsIpInterface getIpInterfaceByIpAddress(String ipAddress) {
        return getIpInterfaceByIpAddress(InetAddressUtils.getInetAddress(ipAddress));
    }

    /**
     * <p>getIpInterfaceByIpAddress</p>
     *
     * @param ipAddress a {@link java.lang.String} object.
     
     */
    public ImsIpInterface getIpInterfaceByIpAddress(InetAddress ipAddress) {
        for (ImsIpInterface iface : getIpInterfaces()) {
            if (ipAddress.equals(iface.getIpAddress())) {
                return iface;
            }
        }
        return null;
    }
    
    public ImsIpInterface getPrimaryInterface() {
        List<ImsIpInterface> primaryInterfaces = new ArrayList<ImsIpInterface>();
        for(ImsIpInterface iface : getIpInterfaces()) {
            if (iface.getIsSnmpPrimary() == Defines.INTERFACE_PRIMARY) {
                primaryInterfaces.add(iface);
            }
        }
        if (primaryInterfaces.size() < 1) {
            return null;
        } else {
            if (primaryInterfaces.size() > 1) {
                // Sort the list by the last capabilities scan time so that we return the most recent value
                Collections.sort(primaryInterfaces, new Comparator<ImsIpInterface>() {
                    @Override
                    public int compare(ImsIpInterface o1, ImsIpInterface o2) {
                        if (o1 == null) {
                            if (o2 == null) {
                                return 0;
                            } else {
                                return -1; // Put nulls at the end of the list
                            }
                        } else {
                            if (o2 == null) {
                                return 1; // Put nulls at the end of the list
                            } else {
                                if (o1.getIpLastCapsdPoll() == null) {
                                    if (o2.getIpLastCapsdPoll() == null) {
                                        return 0;
                                    } else {
                                        return 1; // Descending order
                                    }
                                } else {
                                    if (o2.getIpLastCapsdPoll() == null) {
                                        return -1; // Descending order
                                    } else {
                                        // Reverse the comparison so that we get a descending order
                                        return o2.getIpLastCapsdPoll().compareTo(o1.getIpLastCapsdPoll());
                                    }
                                }
                            }
                        }
                    }
                });
                ImsIpInterface retval = primaryInterfaces.iterator().next();
                LOG.warn("Multiple primary SNMP interfaces for node {}, returning most recently scanned interface: {}", id, retval.getInterfaceId());
                return retval;
            } else {
                return primaryInterfaces.iterator().next();
            }
        }
    }

	@Override
	public String toString() {
		return "[" +
			"id=" + this.id + ", "+
			"alias=" + this.alias + ", "+
			"category=" + this.category + ", "+
			"credences=" + this.credences + ", "+
			"m_ipInterfaces=" + this.m_ipInterfaces +
		"]";
	}
    
}
