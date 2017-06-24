package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.CloudTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.TopoDataOperDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ICloudTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Hypervisor;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Server;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.data.DataDriver;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class CloudTopoHandler extends BaseLogicHandler implements ICloudTopoHandler {

	public IResponseEvent doColudTopoXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		CloudTopoDAO idao = (CloudTopoDAO) dao;
		CArray vmHostNameCA = idao.doGetVMHostName(identityBean);
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		List<Hypervisors> nodes = DataDriver.getAllHypervisors();
		for(Hypervisors node : nodes){
			Hypervisor hypervisor =  node.getHypervisors().get(0);
			String hostName = hypervisor.getHypervisorHostname();
			List<Server> servers = hypervisor.getServers();
			
			String g = "0,0,30,50";
			String hostImage = NodeVo.ICON_PATH+"linux_35.gif";
			String vmImage = TopoUtil.getCloudHostImage(new Object());
			s.append("<"+hostName+" id='"+hypervisor.getId()+"' g='"+g+"' image='"+hostImage+"' name='"+hostName+"'>");
			if(servers.size()==0){
				s.append("</"+hostName+">");
				continue;
			}
			for(Server server : servers){
				if(vmHostNameCA.containsKey(server.getUuid())){
					String to = server.getName();
					s.append("<line to='"+to+"' strokeweight='1' name='' g='' color=''/>");	
				}
			}
			s.append("</"+hostName+">");
			
			for(Server server : servers){
				if(vmHostNameCA.containsKey(server.getUuid())){
					String instanceName = server.getName().replaceAll("-", "_");
					s.append("<"+instanceName+" id='"+server.getUuid()+"' g='"+g+"' image='"+vmImage+"' name='"+server.getName()+"' visibleName='"+Nest.value(vmHostNameCA, server.getUuid()).asString()+"'>");
					s.append("</"+instanceName+">");
				}
			}
		}
		s.append("</process>");
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(s.toString());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetCloudTopoData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		Map<String,List> data = new HashMap();
		List<Map> nodes = new ArrayList();
		List<Map> edges = new ArrayList();
		CloudTopoDAO idao = (CloudTopoDAO) dao;
		CArray vmHostNameCA = idao.doGetVMHostName(identityBean);
		List<Hypervisors> hypervisorsList = DataDriver.getAllHypervisors();
		
		Map cloudNode = new HashMap();
		cloudNode.put("name", HostConstants.CATEGORY_CLOUD);
		cloudNode.put("id", "-1");
		cloudNode.put("hostType",HostConstants.CATEGORY_CLOUD);
		nodes.add(cloudNode);
		
		for(Hypervisors hypervisors:hypervisorsList){
			List<Hypervisor> hypervisorList = hypervisors.getHypervisors();
			for(Hypervisor hypervisor:hypervisorList){
				Map hypervisorNode = new HashMap();
				hypervisorNode.put("name", hypervisor.getHypervisorHostname());
				hypervisorNode.put("id", hypervisor.getId());
				hypervisorNode.put("hostType",HostConstants.CATEGORY_SERVER);
				nodes.add(hypervisorNode);
				
				Map hyEdge = new HashMap(4);
				hyEdge.put("id", HostConstants.CATEGORY_CLOUD.concat("_").concat(hypervisor.getId()));
				hyEdge.put("name", HostConstants.CATEGORY_CLOUD.concat("_").concat(hypervisor.getHypervisorHostname()));
				hyEdge.put("from", "-1");
				hyEdge.put("to", hypervisor.getId());
				edges.add(hyEdge);
				
				List<Server> servers = hypervisor.getServers();
				for(Server server:servers){
					
					String serverName = server.getName();
					
					if(vmHostNameCA.containsKey(server.getUuid())){
						serverName = Nest.value(vmHostNameCA, server.getUuid()).asString();
					}
					
					Map serverNode = new HashMap();
					serverNode.put("name", serverName);
					serverNode.put("id", server.getUuid());
					serverNode.put("hostType",HostConstants.CATEGORY_VM);
					nodes.add(serverNode);
					
					Map serverEdge = new HashMap(4);
					serverEdge.put("id", hypervisor.getId().concat("_").concat(server.getUuid()));
					serverEdge.put("name", hypervisor.getHypervisorHostname().concat("_").concat(serverName));
					serverEdge.put("from", hypervisor.getId());
					serverEdge.put("to", server.getUuid());
					edges.add(serverEdge);
				}
			}
		}
		ParamDTO dto = new ParamDTO();
		data.put("nodes", nodes);
		data.put("edges", edges);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(idao.getSqlExecutor(), (List<Map>)data.get("nodes"));
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
}
