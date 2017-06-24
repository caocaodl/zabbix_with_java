package com.isoft.iradar.virtualresource;

import static com.isoft.types.CArray.array;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;
import com.isoft.web.CDelegator;

public class VirtualResourceDao {
	private static final Logger LOG = LoggerFactory.getLogger(VirtualResourceDao.class);
	
	private <T> T delegate(Delegator<T> d) {
		return delegate(Feature.defaultTenantId, d);
	}
	
	private <T> T delegate(String tenantId, Delegator<T> d) {
		try {
			if(RadarContext.getContext() == null) {
	    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
	    		RadarContext.setContext(ctx);
	    		
	    		Map userdata = EasyMap.build(
					"userid", 0L, 
					"type", Defines.USER_TYPE_SUPER_ADMIN
				);
	    		CWebUser.set(userdata);
	    	}
			
			Map uinfo = new HashMap();
			uinfo.put("tenantId", tenantId);
			uinfo.put("osTenantId", "0");
			uinfo.put("tenantRole", Role.LESSOR.magic());
			uinfo.put("userId", Feature.defaultUser);
			uinfo.put("userName", Feature.defaultUser);
			uinfo.put("admin", "Y");
			uinfo.put("osUser", null);
			
			IdentityBean idBean = new IdentityBean();
			idBean.init(uinfo);
	    	return CDelegator.doDelegate(idBean, d);
		} finally {
			RadarContext.releaseContext();
		}
	}
	
	private final static String SQL_LIST = "SELECT h.hostid, h.name, h.hostid_os, h.tenantid FROM hosts h LEFT JOIN hosts_groups hg ON h.hostid=hg.hostid WHERE h.hostid_os IS NOT NULL AND hg.groupid IN (#foreach($var in $list)$var.id #if($velocityCount<$list.size()),#end #end)";
    /**
     * 获取所有已发现的虚拟资源
     * 
     * @return
     */
    public CArray<Map> list() {
    	return delegate(new Delegator<CArray<Map>>() {
			@Override public CArray<Map> doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				CArray<Map> datas = DBUtil.DBselect(sqlE, SQL_LIST, EasyMap.build(
					"list", ResourceType.ALL
				));
				
				CArray<Map> hostHash = FuncsUtil.rda_toHash(datas, "hostid");
				
				CHostIfaceGet params = new CHostIfaceGet();
				params.setHostIds(hostHash.keysAsLong());
				params.setOutput(new String[] {"hostid", "ip"});
				CArray<Map> interfaces = API.HostInterface(idBean, sqlE).get(params);
				
				for(Map iface: interfaces) {
					Object ip = Nest.value(iface, "ip").$();
					Object hostid = Nest.value(iface, "hostid").$();
					Map host = (Map)Nest.value(hostHash, hostid).$();
					Nest.value(host, "interfaces").$s(true).put(ip, ip);
				}
				
				return datas;
			}
		});
    }
    
    /**
     * 更新虚拟资源名称
     * 
     * @param datas
     */
    private final static String SQL_UPDATE = "UPDATE hosts SET name=#{name} WHERE hostid=#{hostid}";
    public void update(String tenantId, final Collection<Map> datas) {
    	delegate(tenantId, new Delegator() {
			@Override public Boolean doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
				for(Map data: datas) {
					DBUtil.DBstart(sqlE);
					
					boolean success = true;
					try {
						Long hostid = Nest.value(data, "hostid").asLong();
						if(Cphp.isset(data,"ips")){
							List<String> ips = (List<String>)Nest.value(data, "ips").$();
							CArray interfaces = CArray.array();
							int main = 1;
							for(String ip: ips) {
								interfaces.add(CArray.map(
									"main", main, 
									"type", Defines.INTERFACE_TYPE_AGENT, 
									"useip", 1, 
									"ip", ip, 
									"dns", "", 
									"port", DEFAULT_AGENT_PORT
								));
								if(main == 1) {
									main = 0;
								}
							}
							API.HostInterface(idBean, sqlE).replaceHostInterfaces(EasyMap.build(
								"hostid", hostid,
								"interfaces", interfaces
							));
						}
						
						if(!Cphp.empty(Nest.value(data, "name").$())){
							Map host = CArray.map("hostid",Nest.value(data, "hostid").asLong(),
									  "name",Nest.value(data, "name").asString());
							DBUtil.DBexecute(sqlE, SQL_UPDATE, host);
						}
						
					} catch (Exception e) {
						if(LOG.isErrorEnabled()) {
							LOG.error("update fail", e);
						}
						success = false;
					}
					DBUtil.DBend(sqlE, success);
				}
				return null;
			}
		});
    }
    
    private final static int DEFAULT_AGENT_PORT = 10050;
    private final static String SQL_UPDATE_HOST = "UPDATE hosts SET hostid_os = #{hostid_os} WHERE hostid = #{hostid}";
    private final static String SQL_GROUP_TEMPLATES = "SELECT templateid FROM i_group_templates WHERE groupid = #{id}";
    /**
     * 添加新发现的虚拟资源
     * 
     * @param datas
     */
    public void create(String tenantId, final Collection<VirtualResource> vrs) {
    	final CArray<CArray> templateCache = CArray.array(); 
		for(final VirtualResource vr: vrs) {
	    	delegate(tenantId, new Delegator() {
				@Override public Boolean doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
					CArray<Map> hosts = CArray.array();
					String groupid = vr.getType().getId();
					
					CArray templates = templateCache.get(groupid);
					if(templates == null) {
						templates = DBUtil.DBselect(sqlE, SQL_GROUP_TEMPLATES, EasyMap.build("id", groupid));
						templateCache.put(groupid, templates);
					}
					
					CArray interfaces = array();
					int main  = 1;
					for(String ip: vr.getIps()) {
						interfaces.push(CArray.map(
							"main", main, 
							"type", Defines.INTERFACE_TYPE_AGENT, 
							"useip", 1, 
							"ip", ip, 
							"dns", "", 
							"port", DEFAULT_AGENT_PORT
						));
						if(main == 1) {
							main = 0;
						}
					}
					
					CArray groups = array(CArray.map(
						"groupid", groupid
					));
					
					hosts.add(CArray.map(
						"host", vr.getId(),
						"name", vr.getName(),
						"hostid_os", vr.getId(),
						"tenantid", vr.getTenantId(),
						"status", Defines.HOST_STATUS_MONITORED,
						"proxy_hostid", 0,
						"ipmi_authtype", null,
						"ipmi_privilege", null,
						"ipmi_username", null,
						"ipmi_password", null,
						"groups", groups,
						"templates", templates,
						"interfaces", interfaces,
						"macros", null,
						"inventory", null,
						"inventory_mode", Defines.HOST_INVENTORY_DISABLED
					));
					
					
					DBUtil.DBstart(sqlE);
					boolean success = true;
					try {
						CArray<Long[]> result = API.Host(idBean, sqlE).create(hosts);
						Long[] ids = result.get("hostids");
						for(int i=0,ilen=ids.length; i<ilen; i++) {
							Long hostid = ids[i];
							Map data = hosts.get(i);
							data.put("hostid", hostid);
							DBUtil.DBexecute(sqlE, SQL_UPDATE_HOST, data);
						}
					} catch (Exception e) {
						if(LOG.isErrorEnabled()) {
							LOG.error("create fail", e);
						}
						success = false;
					}
					DBUtil.DBend(sqlE, success);
					return null;
				}
			});
		}
    }
    
    /**
     * {51872b8e-5c1c-4bf5-86ac-9d86ee3a7afc={
     * hostid_os=51872b8e-5c1c-4bf5-86ac-9d86ee3a7afc, 
     * hostid=10156, 
     * name=testvm, 
     * tenantid=5e4d0a6d39a44b9c906a3173b448aa4a
     * }}
     * 
     * @param datas
     */
    public void delete(String tenantId, final CArray<Map> datas) {
    	CArray hosts = FuncsUtil.rda_objectValues(datas, "hostid");
    	for(final Long hostid: hosts.valuesAsLong()) {
    		delegate(tenantId, new Delegator() {
    			@Override public Boolean doDelegate(IIdentityBean idBean, SQLExecutor sqlE) throws Exception {
    				DBUtil.DBstart(sqlE);
    				boolean success = true;
    				try {
    					//服务器应该等会挂载到云主机上
    					CHostGet params = new CHostGet();
    					params.setProxyIds(hostid);
    					params.setOutput("hostid");
    					CArray<Map> proxyHost = API.Host(idBean, sqlE).get(params);
    					if(!proxyHost.isEmpty()) {
    						CArray proxyHosts = FuncsUtil.rda_objectValues(proxyHost, "hostid");
    						API.Host(idBean, sqlE).delete(proxyHosts.valuesAsLong());
    					}
    					
    					API.Host(idBean, sqlE).delete(hostid);
    				} catch (Exception e) {
    					if(LOG.isErrorEnabled()) {
    						LOG.error("delete fail", e);
    					}
    					success = false;
    				}
    				DBUtil.DBend(sqlE, success);
    				return null;
    			}
    		});
    	}
    }
    
}
