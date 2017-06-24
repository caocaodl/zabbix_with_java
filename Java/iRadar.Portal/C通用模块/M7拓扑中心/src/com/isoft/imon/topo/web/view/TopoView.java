package com.isoft.imon.topo.web.view;

import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.biz.Delegator;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.LinkDAO;
import com.isoft.biz.daoimpl.platform.topo.SubnetDAO;
import com.isoft.biz.vo.platform.topo.LinkVo;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.engine.discover.element.Link;
import com.isoft.imon.topo.engine.discover.element.Subnet;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

//FIXME
public class TopoView {
	private static TopoView INSTANCE ;
	private Map<Integer, TopoNode> nodes;

	public static String SUBNETOFHOSTSEPARATOR = "$";	
	@SuppressWarnings("unchecked")
	private TopoView() {
		this.nodes = EasyMap.build();
	}
	
	public static TopoView getInstance(){
		if(INSTANCE==null){
			INSTANCE = new TopoView();
		}
		return INSTANCE;
	}
	
	public TopoNode getNode(int hostId) {
		return this.nodes.get(hostId);
	}
	
	public Map<Integer,TopoNode> getNodes(){
		return this.nodes;
	}

	public void refreshElements(IIdentityBean idBean, List<NetElement> elements) {
		for (TopoNode node : nodes.values()) {
			node.toUpdate();
		}
		List<Integer> hostIds = new ArrayList();
		// 清空link和hostExp信息
		truncateLinkAndHostExp(idBean);
		for (NetElement element : elements) {
			if (element instanceof Host) {
				putHost((Host) element);
				insertHosExpt(idBean, (Host) element);
				hostIds.add(((Host) element).getId());
			} else if (element instanceof Link) {
				putLink((Link) element);
				insertLink(idBean, (Link) element);
			}
//			else if (element instanceof Subnet) {
//				if(!HostConstants.SUBNET_LOOPBACK.equals(element.getAlias()))
//					insertSubnet(idBean, (Subnet) element);
//			}
		}
		delHostExptNotExist();
		setHostExptError(idBean,hostIds);
	}

	private void putHost(Host host) {
		int id = host.getId();
		TopoNode node = getNode(id);
		if (node == null) {
			node = new TopoNode(host);
			this.nodes.put(host.getId(), node);
		}
		node.doUpdate();
	}

	private void putLink(Link link) {
		int id = link.getStartId();
		TopoNode node = getNode(id);
		if(node!=null){
			node.addLink(link);
		}
	}

	/**
	 * 写入host的扩展信息到数据库中
	 * @param host
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void insertHosExpt(IIdentityBean idBean, final Host host) {
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("hostId", host.getId());
				paramMap.put("enterprise", host.getEnterprise());
				paramMap.put("category", host.getCategory());
				paramMap.put("model", host.getModel());
				paramMap.put("symbol",host.getSymbol());
				paramMap.put("sysOid", host.getSysOid());
				paramMap.put("sysName", host.getSysName());
				paramMap.put("sysDescr", host.getSysDescr());
				paramMap.put("bridgeMac", host.getBridgeMac());
				paramMap.put("serialNum", host.getSerialNum());
				paramMap.put("tenantid", host.getTenantId());
//				if(!Cphp.empty(host.getSubnets())){
//					List<String> subnetIds = new ArrayList();
//					for(Subnet subnet:host.getSubnets()){
//						if(!HostConstants.SUBNET_LOOPBACK.equals(subnet.getAlias()))
//							subnetIds.add(Nest.as(subnet.getId()).asString());
//					}
//					paramMap.put("subnet", Cphp.implode(SUBNETOFHOSTSEPARATOR, CArray.valueOf(subnetIds)));
//				}
				HostExpDAO hostExpDao = new HostExpDAO(executor);
				hostExpDao.doHostExpAdd(paramMap);
				return null;
			}
		});
	}
	
	private void delHostExptNotExist() {
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
	    	IIdentityBean idBean = new IdentityBean();
	    	CDelegator.doDelegate(idBean, new Delegator() {
				public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
					CHostGet hGet = new CHostGet();
					hGet.setOutput(new String[] {"hostid"});
					CArray<Map> hs = API.Host(idBean, executor).get(hGet);
					List<Integer> hostIds = rda_objectValues(hs, "hostid").toList();
					if(hostIds!=null&&hostIds.size()>0){
						HostExpDAO hostExpDao = new HostExpDAO(executor);
						hostExpDao.delHostExptNotExist(hostIds);
					}
					return null;
				}
	    	});
		} finally {
			RadarContext.releaseContext();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setHostExptError(IIdentityBean idBean, final List<Integer> hostIds) {
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				if(hostIds!=null&&hostIds.size()>0){
					HostExpDAO hostExpDao = new HostExpDAO(executor);
					hostExpDao.doHostExpErrorSet(hostIds);
				}
				return null;
			}
		});
	}

	/**
	 * 将发现的link写入到数据库中
	 * 
	 * @param link
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void insertLink(IIdentityBean idBean, final Link link) {
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("startId", link.getStartId());
				paramMap.put("startIfIndex", link.getStartIfIndex());
				paramMap.put("startIfDescr", link.getStartIfDescr());
				paramMap.put("startIp", link.getStartIp());
				paramMap.put("startMac", link.getStartMac());
				paramMap.put("endId", link.getEndId());
				paramMap.put("endIfIndex", link.getEndIfIndex());
				paramMap.put("endIfDescr", link.getEndIfDescr());
				paramMap.put("endIp", link.getEndIp());
				paramMap.put("endMac", link.getEndMac());
				paramMap.put("bandWidth", link.getBandWidth());
				paramMap.put("type", link.getType());
				paramMap.put("tag", link.getTag());
				paramMap.put("trafficIf", link.getTrafficIf());
				paramMap.put("trafficDirect", link.getTrafficDirect());
				paramMap.put("backup", link.getBackup());
				LinkDAO linkDao = new LinkDAO(executor);
				linkDao.doLinkAdd(paramMap);
				return null;
			}
		});
	}

	/**
	 * 写入subnet的信息到数据库中
	 * @param host
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void insertSubnet(IIdentityBean idBean, final Subnet subnet) {
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("subnetId", subnet.getId());
				paramMap.put("alias", subnet.getAlias());
				paramMap.put("subnetmask", subnet.getNetMask());
				paramMap.put("vlanId", subnet.getVlanId());
				paramMap.put("gateway", subnet.getGateway());
				paramMap.put("ipAddress", subnet.getIpAddress());
				paramMap.put("netAddress", subnet.getNetAddress());
				paramMap.put("startIp", subnet.getStartIp());
				paramMap.put("startLongIp", subnet.getStartLongIp());
				paramMap.put("endIp", subnet.getEndIp());
				paramMap.put("endLongIp", subnet.getEndLongIp());
				SubnetDAO subnetDao = new SubnetDAO(executor);
				subnetDao.doSubnetAdd(paramMap);
				return null;
			}
		});
	}
	
	/**
	 * 将数据中的Host和Link初始化到缓存中
	 * @param hosts
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadDBNodes() {
		//FIXME
		IIdentityBean idBean = new IdentityBean();
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				HostExpDAO hostDao = new HostExpDAO(executor);
				List<Host> hosts = hostDao.doHostList(new HashMap());
				for (Host host : hosts) {
					putHost(host);
				}

				LinkDAO linkDao = new LinkDAO(executor);
				List<LinkVo> links = linkDao.doLinkList(new HashMap());
				for (LinkVo link : links) {
					putLink(link);
				}
				
				return null;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void truncateLinkAndHostExp(IIdentityBean idBean) {
		// 清空数据表host_exp,link
		CDelegator.doDelegate(idBean, new Delegator() {
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
//				HostExpDAO hostDao = new HostExpDAO(executor);
//				hostDao.doHostExpTruncate();
				LinkDAO linkDao = new LinkDAO(executor);
				linkDao.doLinkTruncate();
				SubnetDAO subnetDao = new SubnetDAO(executor);
				subnetDao.doSubnetTruncate();
				return null;
			}
		});
	}

}
