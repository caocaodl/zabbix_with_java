package com.isoft.imon.topo.netmgt.discoveryd.dao;

import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_OFF;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.INTERFACE_PRIMARY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NORMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV1;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV2C;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPV3;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv1;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv2c;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv3;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snmp4j.mp.SnmpConstants;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.biz.Delegator;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.ImsIpInterface;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class NodeDao {
    
    public List<Host> getNetElments() {
    	try{
	    	if(RadarContext.getContext() == null) {
	    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
	    		RadarContext.setContext(ctx);
	    		
	    		Map userdata = EasyMap.build(
					"userid", 0L, 
					"type", Defines.USER_TYPE_SUPER_ADMIN
				);
	    		CWebUser.set(userdata);
	    	}
	    	//FIXME
	    	IIdentityBean idBean = new IdentityBean();
	    	return CDelegator.doDelegate(idBean, new Delegator() {
				@Override
				public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
					return getNetElments(idBean, executor);
				}
			});
    	} finally {
			RadarContext.releaseContext();
		}
    }
    
    private List<Host> getNetElments(IIdentityBean idBean, SQLExecutor executor) {
    	List<Host> netElements = new ArrayList<Host>(); 
    	
		CHostGet hGet = new CHostGet();
		hGet.setSelectInterfaces(new String[] {"interfaceid", "ip", "port", "main", "type"});
		hGet.setOutput(new String[] {"hostid", "name","tenantid"});
		hGet.setFilter("snmp_available", String.valueOf(HOST_AVAILABLE_TRUE));//SNMP是可用的
		hGet.setFilter("status", String.valueOf(HOST_STATUS_MONITORED));//当前节点是被监控的
		hGet.setFilter("maintenance_status", String.valueOf(HOST_MAINTENANCE_STATUS_OFF));//没有在维护状态的
		CArray<Map> hs = API.Host(idBean, executor).get(hGet);
		
		for(Map host: hs) {
			List<Map> snmpIfs = new ArrayList<Map>();
			Map snmpCrendence = null;
			
			CArray<Map> interfaces = (CArray) host.get("interfaces");
			for(Map ipIf: interfaces) {
				if((Integer)ipIf.get("type") == INTERFACE_TYPE_SNMP) { //只获取SNMP的接口
					Object id = ipIf.get("interfaceid");
					int main = (Integer)ipIf.get("main");
					snmpIfs.add(ipIf);
					
					if(main == INTERFACE_PRIMARY) {
						//主要接口，通过其监控项获取SNMP凭证
						if(snmpCrendence == null) {
							snmpCrendence = getSnmpCredenceOfInterface(idBean, executor, id);
						}
					}

					//通过SNMP发现规则，获取凭证
					if(snmpCrendence == null) {
						snmpCrendence = getSnmpCredenceByDiscoveryInfo(idBean, executor, ipIf);
					}
					
					//设置端口号，有可能系统使用默认端口而置空
					if(snmpCrendence != null) {
						if(Cphp.empty(snmpCrendence.get("port"))) {
							Object port = ipIf.get("port");
							port = !Cphp.empty(port)? port: 161;
							snmpCrendence.put("port", port);
						}
					}
				}
			}
			
			if(snmpCrendence != null) {
		    	netElements.add(convertHost(idBean, executor, host, snmpCrendence, snmpIfs));
			}
		}
		
		return netElements;
    }
    
    private Host convertHost(IIdentityBean idBean, SQLExecutor executor, Map m, Map credenceMap, List<Map> snmpIfs) {
    	int id = EasyMap.getInteger(m, "hostid");
    	String alias = EasyMap.getString(m, "name"); 
    	String tenantId = EasyMap.getString(m, "tenantid"); 
    	SnmpCredence snmpCrendence = convetrSnmpCredence(idBean, executor, id, credenceMap);
    	
    	Host h = new Host();
    	h.setId(id);
    	h.setAlias(alias);
    	h.setTenantId(tenantId);
    	h.putCredence(snmpCrendence);
    	
    	for(Map snmpIf: snmpIfs) {
    		ImsIpInterface ipIf = convertyImsIpInterface(snmpIf);
    		ipIf.setNode(h);
    		h.getIpInterfaces().add(ipIf);
    	}
    	
    	return h;
    }
    
    private SnmpCredence convetrSnmpCredence(IIdentityBean idBean, SQLExecutor executor, int hostId, Map credenceMap) {
    	SnmpCredence sc = new SnmpCredence();
    	sc.setVersion(EasyMap.getInteger(credenceMap, "type"));
    	sc.setPort(EasyMap.getInteger(credenceMap, "port"));
    	
    	String snmp_community = EasyMap.getString(credenceMap, "snmp_community");
    	String snmpv3_authpassphrase = EasyMap.getString(credenceMap, "snmpv3_authpassphrase");
    	String snmpv3_authprotocol = EasyMap.getString(credenceMap, "snmpv3_authprotocol");
    	String snmpv3_contextname = EasyMap.getString(credenceMap, "snmpv3_contextname");
    	String snmpv3_privpassphrase = EasyMap.getString(credenceMap, "snmpv3_privpassphrase");
    	String snmpv3_privprotocol = EasyMap.getString(credenceMap, "snmpv3_privprotocol");
    	String snmpv3_securitylevel = EasyMap.getString(credenceMap, "snmpv3_securitylevel");
    	String snmpv3_securityname = EasyMap.getString(credenceMap, "snmpv3_securityname");
    	
    	snmp_community = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmp_community);
    	snmpv3_authpassphrase = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_authpassphrase);
    	snmpv3_authprotocol = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_authprotocol);
    	snmpv3_contextname = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_contextname);
    	snmpv3_privpassphrase = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_privpassphrase);
    	snmpv3_privprotocol = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_privprotocol);
    	snmpv3_securitylevel = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_securitylevel);
    	snmpv3_securityname = CMacrosResolverHelper.resolveHttpTestName(idBean, executor, hostId, snmpv3_securityname);
    	
    	sc.setCommunity(snmp_community);
    	sc.setAuthpassphrase(snmpv3_authpassphrase);
    	sc.setAuthprotocol(EasyObject.asInteger(snmpv3_authprotocol));
    	sc.setContextName(snmpv3_contextname);
    	sc.setPrivpassphrase(snmpv3_privpassphrase);
    	sc.setPrivprotocol(EasyObject.asInteger(snmpv3_privprotocol));
    	sc.setSecuritylevel(EasyObject.asInteger(snmpv3_securitylevel));
    	sc.setSecurityName(snmpv3_securityname);
    	return sc;
    }
    
    private ImsIpInterface convertyImsIpInterface(Map snmpIf) {
    	String id = EasyMap.getString(snmpIf, "interfaceid");
    	String ip = EasyMap.getString(snmpIf, "ip");
    	int isSnmpPrimary = EasyMap.getInteger(snmpIf, "main");
    	
    	ImsIpInterface ipIf = new ImsIpInterface();
    	ipIf.setId(id);
    	try {
			ipIf.setIpAddress(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	ipIf.setIsSnmpPrimary(isSnmpPrimary);
    	return ipIf;
    }
    
    /**
     * 如果明确节点接口，则可以通过item监控项来获取SNMP凭证的配置信息
     * 
     * @param id
     */
    private Map getSnmpCredenceOfInterface(IIdentityBean idBean, SQLExecutor executor, Object id) {
    	CItemGet itemGet = new CItemGet();
		itemGet.setLimit(1);
		itemGet.setInterfaceIds(Nest.as(id).asLong());
		itemGet.setFilter("type", String.valueOf(ITEM_TYPE_SNMPV1), String.valueOf(ITEM_TYPE_SNMPV2C), String.valueOf(ITEM_TYPE_SNMPV3));//使用SNMP采集的监控项
		itemGet.setFilter("status", String.valueOf(ITEM_STATUS_ACTIVE)); //状态可用
		itemGet.setFilter("state", String.valueOf(ITEM_STATE_NORMAL));//支持所设置的操作（SNMP没有出现异常）
		itemGet.setFilter("flag", String.valueOf(RDA_FLAG_DISCOVERY_NORMAL), String.valueOf(RDA_FLAG_DISCOVERY_CREATED));//不是原型或发现型
		itemGet.setOutput(new String[] {"type", "port", "snmp_community", "snmpv3_authpassphrase", "snmpv3_authprotocol", "snmpv3_contextname", "snmpv3_privpassphrase", "snmpv3_privprotocol", "snmpv3_securitylevel", "snmpv3_securityname"});
		CArray<Map> result = API.Item(idBean, executor).get(itemGet);
		
		Map m = result.get(0);
		if(m != null) {
			m.put("type", TYPE_2_VERSION_ITEM.get(EasyMap.getInteger(m, "type")));
		}
		return m;
    }
    private final static Map<Integer, Integer> TYPE_2_VERSION_ITEM = EasyMap.build(
    		ITEM_TYPE_SNMPV1, SnmpConstants.version1,
    		ITEM_TYPE_SNMPV2C, SnmpConstants.version2c,
    		ITEM_TYPE_SNMPV3, SnmpConstants.version3
    	);
    
    /**
     * 通过发现规则获取SNMP凭证的配置信息
     * 
     * @param executor
     * @param iface
     * @return
     */
    private Map getSnmpCredenceByDiscoveryInfo(IIdentityBean idBean, SQLExecutor executor, Map iface) {
    	CDServiceGet dsGet = new CDServiceGet();
    	dsGet.setFilter("ip", EasyMap.getString(iface, "ip"));
    	dsGet.setFilter("port", EasyMap.getString(iface, "port"));
    	dsGet.setFilter("type", String.valueOf(SVC_SNMPv1), String.valueOf(SVC_SNMPv2c), String.valueOf(SVC_SNMPv3));
    	dsGet.setSelectDChecks(new String[] {"type", "snmp_community", "ports", "snmpv3_authpassphrase", "snmpv3_authprotocol", "snmpv3_contextname", "snmpv3_privpassphrase", "snmpv3_privprotocol", "snmpv3_securitylevel", "snmpv3_securityname"});
    	CArray<Map> result = API.DService(idBean, executor).get(dsGet);
    	
    	Map r = result.get(0);
    	if(r != null) {
    		r = (Map)r.get("dchecks");
    		if(r != null) {
    			r = (Map)r.get(0);
    			if(r != null) {
    				r.put("port", r.get("ports"));
    			}
    		}
    	}
    	
    	if(r != null) {
    		r.put("type", TYPE_2_VERSION_SVC.get(EasyMap.getInteger(r, "type")));
    	}
    	return r;
    }
    private final static Map<Integer, Integer> TYPE_2_VERSION_SVC = EasyMap.build(
    		SVC_SNMPv1, SnmpConstants.version1,
    		SVC_SNMPv2c, SnmpConstants.version2c,
    		SVC_SNMPv3, SnmpConstants.version3
    	);
}
