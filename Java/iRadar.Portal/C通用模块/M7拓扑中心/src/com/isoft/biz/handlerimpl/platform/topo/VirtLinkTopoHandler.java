package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.Feature;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.TopoDataOperDAO;
import com.isoft.biz.daoimpl.platform.topo.VirtLinkTopoDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IVirtLinkTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Servers;
import com.isoft.iaas.openstack.nova.model.Server.Addresses.Address;
import com.isoft.iaas.openstack.quantum.model.Network;
import com.isoft.iaas.openstack.quantum.model.Port.Ip;
import com.isoft.iaas.openstack.quantum.model.Port;
import com.isoft.iaas.openstack.quantum.model.Router;
import com.isoft.iaas.openstack.quantum.model.Subnet;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.data.DataDriver;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class VirtLinkTopoHandler extends BaseLogicHandler implements IVirtLinkTopoHandler {
	
	private static String ALLTENANTS = "all";
	
	private static List<Server> vms;
	private static List<Subnet> subnets;
	private static List<Ip> ipList;
	private static List<Router> routers;
	private static List<Port> ports;
	private static List<Network> networks;
	private static List<Tenant> tenants;

	public IResponseEvent doVirtLinkTopoXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		String g = "0,0,30,50";
		List<Subnet> subnets = DataDriver.getAllSubnet();
		List<Ip> ipList = DataDriver.getAllIps();
		List<Router> routers = DataDriver.getAllRoute();
		List<Port> ports = DataDriver.getAllPort();
		List<Network> networks = DataDriver.getAllNetwork();
		CArray<String> routerNames = CArray.array();
		int length = routers.size();
		for(int i=0;i<length;i++){
			Router router = routers.get(i);
			String routerName = router.getName();
			String visibleName = "";
			if(routerNames.containsValue(routerName)){
				routerName = routerName.concat("_conflict_"+i);
				visibleName = router.getName();
			}
			routerNames.add(routerName);
			String routerImage = NodeVo.ICON_PATH+"router_0.gif";
			String routerTagName = "_"+router.getId().replace("-", "_");
			s.append("<"+routerTagName+" id='"+router.getId()+"' g='"+g+"' image='"+routerImage+"' name='"+routerName+"' visibleName='"+visibleName+"'>");
			for(Port port : ports){
			   if(router.getId().equals(port.getDeviceId())){
				    List<Ip> ips = port.getList();
					for(Ip ip : ips){
						for(Subnet subnet : subnets){
							if(subnet.getId().equals(ip.getSubnetId())){
								for(Network network : networks){
									List<String> subnetIds = network.getSubnets();
									if(subnetIds.contains(subnet.getId())){
										String to = subnet.getCidr() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId();
										s.append("<line to='"+to+"' strokeweight='1' name='' g='' color=''/>");
									}
								}
							}
						}
					}
			   }
			}
			s.append("</"+routerTagName+">");
		}
		
		for(Subnet subnet : subnets){
			//TODO
			String subnetImage = NodeVo.ICON_PATH+"switch.gif";
			String subnetTagName = "_"+subnet.getId().replace("-", "_");
			for(Network network : networks){
				List<String> subnetIds = network.getSubnets();
				if(subnetIds.contains(subnet.getId())){
					String name = subnet.getCidr() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId();
					s.append("<"+subnetTagName+" id='"+subnet.getId()+"' g='"+g+"' image='"+subnetImage+"' name='"+name+"'>");
				}
			}
			for(Ip ip : ipList){
				String subnetId = ip.getSubnetId();
				if(subnet.getId().equals(subnetId)){
					for(Network network : networks){
						List<String> subnetIds = network.getSubnets();
						if(subnetIds.contains(subnet.getId())){
							String name = ip.getAddress() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId();
							s.append("<line to='"+name+"' strokeweight='1' name='' g='' color=''/>");
						}
					}
				}
			}
			s.append("</"+subnetTagName+">");
		}
		String vmImage = TopoUtil.getCloudHostImage(new Object());
		for(Ip ip : ipList){
			for(Network network : networks){
				List<String> subnetIds = network.getSubnets();
				if(subnetIds.contains(ip.getSubnetId())){
					String name = ip.getAddress() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId();
					String tagName = "_"+ip.getAddress().replace(".", "_");
					s.append("<"+tagName+" id='"+ip.getAddress()+"_"+ip.getSubnetId()+"' g='"+g+"' image='"+vmImage+"' name='"+ name +"'>");
					s.append("</"+tagName+">");
				}
			}
			
		}
		s.append("</process>");
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(s.toString());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetVirtLinkTopoData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
//		if(Cphp.empty(vms)||Cphp.empty(subnets)||Cphp.empty(ipList)||Cphp.empty(routers)||Cphp.empty(ports)||Cphp.empty(networks)){
			IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
			vms = admClient.getComputeClient().servers().list(true).queryParam("all_tenants", "1").execute().getList();
			subnets = DataDriver.getAllSubnet(admClient);
			ipList = DataDriver.getAllIps(admClient);
			routers = DataDriver.getAllRoute(admClient);
			ports = DataDriver.getAllPort(admClient);
			networks = DataDriver.getAllNetwork(admClient);
//		}
		ParamDTO paramDto = (ParamDTO) request.getDTO();
		Map param = paramDto.getMapParam();
		ParamDTO dto = new ParamDTO();
		dto.setMapParam(getVirtLinkTopoData(Nest.as(param.get("isTenant")).asBoolean(),Nest.as(param.get("tenantId")).asString(),identityBean,vms,subnets,ipList,routers,ports,networks,dao));
		response.setDTO(dto);
		return response;
	}
	
	public Map getVirtLinkTopoData(boolean isTenant,String tenantId,IIdentityBean identityBean,List<Server> servers,
			List<Subnet> subnets,List<Ip> ipList,List<Router> routers,List<Port> ports,List<Network> networks,IDAO idao){
		Map<String,List> data = new HashMap();
		List<Map> nodes = new ArrayList();
		List<Map> edges = new ArrayList();
		
		if(isTenant){
			tenantId = identityBean.getTenantId();
		}
		
		for(Server server:servers){
			if(server.getTenantId().equals(tenantId)||ALLTENANTS.equals(tenantId)){
				Map node = new HashMap();
				node.put("name", server.getName());
				node.put("id", server.getId());
				node.put("hostType",HostConstants.CATEGORY_VM);
				nodes.add(node);
				
				Map<String, List<Address>> address = server.getAddresses().getAddresses();
				for (List<Address> addressesList : address.values()) {                    
		          for (Address add : addressesList) {                                 
		          	if("fixed".equals(add.getType())){
		          		for(Ip ip:ipList){
		          			if(ip.getAddress().equals(add.getAddr())){
		          				Map edge = new HashMap(4);
								edge.put("id", server.getId().concat("_").concat(ip.getSubnetId()));
								edge.put("name", "");
								edge.put("from", ip.getSubnetId());
								edge.put("to", server.getId());
								edges.add(edge);
		          			}
		          		}
		          	}                                                                 
		          }                                                                   
			   } 
			}
		}
		
		for(Router router:routers){
			if(router.getTenantId().equals(tenantId)||ALLTENANTS.equals(tenantId)){
				Map node = new HashMap(3);
				node.put("id", router.getId());
				node.put("name", router.getName());
				node.put("hostType", HostConstants.CATEGORY_ROUTER);
				nodes.add(node);
				
				for(Port port : ports){
					if(router.getId().equals(port.getDeviceId())){
					    List<Ip> ips = port.getList();
						for(Ip ip : ips){
							for(Subnet subnet : subnets){
								if(subnet.getId().equals(ip.getSubnetId())){
									for(Network network : networks){
										List<String> subnetIds = network.getSubnets();
										if(subnetIds.contains(subnet.getId())){
											if(Nest.as(network.getRouterExternal()).asBoolean()){
												Map edge = new HashMap(4);
												edge.put("id", subnet.getId().concat("_").concat(router.getId()));
												edge.put("name", subnet.getName().concat(" ").concat(router.getName()));
												edge.put("from", subnet.getId());
												edge.put("to", router.getId());
												edges.add(edge);
											}else{
												Map edge = new HashMap(4);
												edge.put("id", router.getId().concat("_").concat(subnet.getId()));
												edge.put("name", router.getName().concat(" ").concat(subnet.getName()));
												edge.put("from", router.getId());
												edge.put("to", subnet.getId());
												edges.add(edge);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			}
		
		for(Subnet subnet : subnets){
			if(subnet.getTenantId().equals(tenantId)||ALLTENANTS.equals(tenantId)){
				for(Network network : networks){                                                       
					List<String> subnetIds = network.getSubnets();                                     
					if(subnetIds.contains(subnet.getId())){
						if(Nest.as(network.getRouterExternal()).asBoolean()){
							Map node = new HashMap(3);                                                     
							node.put("id", subnet.getId());                                                
//							node.put("name", subnet.getName());     
							node.put("name", subnet.getCidr() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId());
							node.put("hostType", HostConstants.CATEGORY_CLOUD);                           
							nodes.add(node);   
						}else{
							Map node = new HashMap(3);                                                     
							node.put("id", subnet.getId());                                                
//							node.put("name", subnet.getName());                                            
							node.put("name", subnet.getCidr() +" "+network.getProviderNetworkType()+":"+network.getProviderSegmentationId());
							node.put("hostType", HostConstants.CATEGORY_SUBNET);                           
							nodes.add(node);
						}
					}                                                                                  
				} 
			}
		}
//		doTopoDataSetXY(nodes,((VirtLinkTopoDAO)idao).getSqlExecutor(),identityBean);
		data.put("nodes", nodes);
		data.put("edges", edges);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(idao.getSqlExecutor(), (List<Map>)data.get("nodes"));
		return data;
	}

	public IResponseEvent doGetVirtLinkVMData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		VirtLinkTopoDAO idao = (VirtLinkTopoDAO) dao;
		ParamDTO paramDto = (ParamDTO) request.getDTO();
		Map param = paramDto.getMapParam();
		ParamDTO dto = new ParamDTO();
		dto.setMapParam(idao.doHostIdByTopoId(identityBean,param));
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetTenantData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		VirtLinkTopoDAO idao = (VirtLinkTopoDAO) dao;
		ParamDTO paramDto = (ParamDTO) request.getDTO();
		Map param = paramDto.getMapParam();
		ParamDTO dto = new ParamDTO();
		if(Cphp.empty(tenants)){
			tenants = OpsUtils.getOpenStackClientForAdmin().getIdentityClient().tenants().list().execute().getList();
		}
		Map<String,List> data = new HashMap();
		List<Map> nodes = new ArrayList();
		List<Map> edges = new ArrayList();
		List<Map> nodeList = new ArrayList();
		String adminId = Feature.defaultTenantId;
		for(Tenant tenant:tenants){
			if(!adminId.equals(tenant.getId())){
				Map node = new HashMap();
				node.put("id", tenant.getId());
				node.put("name", tenant.getName());
				nodeList.add(node);
				Map edge = new HashMap(4);
				edge.put("id", tenant.getId());
				edge.put("name", Feature.sysDefaultUser.concat("__").concat(tenant.getName()));
				edge.put("from", "");
				edge.put("to", tenant.getId());
				edges.add(edge);
			}
		}
		for(Map edge:edges){
			String tenantid = Nest.as(edge.get("id")).asString();
			String tenantName = Nest.as(edge.get("name")).asString();
			edge.put("id", adminId.concat("__").concat(tenantid));
			edge.put("from", adminId);
		}
		Map adminNode = new HashMap();
		adminNode.put("id", adminId);
		adminNode.put("name", Feature.sysDefaultUser);
		nodes.add(adminNode);
		for(Map node:nodeList){
			nodes.add(node);
		}
		data.put("nodes", nodes);
		data.put("edges", edges);
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
	public void doTopoDataSetXY(List<Map> nodes,SQLExecutor sqlExecutor,IIdentityBean identityBean){
		Map paraMap = new LinkedMap();
		if(Feature.defaultTenantId.equals(identityBean.getTenantId())){
			paraMap.put("topoType", TopoDataOperAction.TOPO_VIRTADMIN);
		}else{
			paraMap.put("topoType", TopoDataOperAction.TOPO_VIRTTENANT);
			paraMap.put("tenant", identityBean.getTenantId());
		}
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		Map location = topoDataOperDAO.doTopoDataLocOperGet(paraMap);
		for(Map node:nodes){
			if(!Cphp.empty(Nest.value(location, node.get("id")).$())){
				node.put("X", Nest.value(location, node.get("id"),"X").asDouble());
				node.put("Y", Nest.value(location, node.get("id"),"Y").asDouble());
			}
		}
	}
	
}
