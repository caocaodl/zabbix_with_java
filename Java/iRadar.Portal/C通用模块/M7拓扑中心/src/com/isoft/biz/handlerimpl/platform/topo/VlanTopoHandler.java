package com.isoft.biz.handlerimpl.platform.topo;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IVlanTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.iaas.openstack.nova.model.Network;
import com.isoft.imon.topo.web.view.TopoNode;
import com.isoft.iradar.core.utils.StringUtil;

public class VlanTopoHandler extends BaseLogicHandler implements IVlanTopoHandler {


	public IResponseEvent doVlanTopoMenuJson(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		
		List<String> vlans = doVlanMenuJson();
		List<String> fixedIps = doFixedIPMenuJson();
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{ name:'VLAN',title:'VLAN',childNodes:[");
		sb.append(StringUtil.join(vlans.toArray(new String[vlans.size()]), ","));
		sb.append("]},");
		sb.append("{ name:'IP',title:'IP',childNodes:[");
		sb.append(StringUtil.join(fixedIps.toArray(new String[fixedIps.size()]),
				","));
		sb.append("]}");
		sb.append("]");
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(sb.toString());
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doVlanTopoXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		
		StringBuffer s = new StringBuffer("");
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
//		List<Network> nets = getRMINetworks();
//	    
//		for(Network net : nets){
//			String tagName ="net_"+net.getId();
//			String g = "0,0,30,50";
//			s.append("<"+tagName+" id='"+net.getId()+"' g='"+g+"' name='"+net.getCidr()+"'>");
//			if(net.getIpList()!=null){
//				for(FixedIp fip : net.getIpList()){
//					String to = fip.getAddress();
//					s.append("<line to='"+to+"' strokeweight='1' name='' g='' color=''/>");	
//				}	
//			}
//			s.append("</"+tagName+">");
//			if(net.getIpList()!=null){
//				for(FixedIp fip : net.getIpList()){
//					String ipTagName = "fip_"+fip.getId();
//					s.append("<"+ipTagName+" id='"+fip.getId()+"' g='"+g+"' name='"+fip.getAddress()+"'>");
//					s.append("</"+ipTagName+">");
//				}
//			}
//		}
		s.append("</process>");
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(s.toString());
		response.setDTO(dto);
		return response;
	}
	
	private List<String> doVlanMenuJson(){
		List<Network> nets = getRMINetworks();
		List<String> vlanMenuJson = new ArrayList<String>();
		for (Network net : nets) {
			int id = 1;//net.getId();
			String title = net.getCidr();
			String name = "net_"+net.getId();
			String image = TopoNode.ICON_PATH+"router_0.gif";
			String width = "30";
			String height = "50";
			
			String nodeJson = "{" + "id:'" + id + "'," + "name:'"
					+ name+ "'," + "title:'"
					+ title + "'," + "image:'"
					+ image + "',width:" + width + ","
					+ "height:" + height
					+"}";
			vlanMenuJson.add(nodeJson);
		}
		return vlanMenuJson;
	}
	
	private List<String> doFixedIPMenuJson(){
		List<Network> nets = getRMINetworks();
		List<String> fipMenuJson = new ArrayList<String>();
//		for (Network net : nets) {
//			if(net.getIpList()==null){
//				continue;
//			}
//			for(FixedIp fip : net.getIpList()){
//				int id = fip.getId();
//				String title = fip.getAddress();
//				String name = "fip_"+fip.getId();
//				String image = TopoNode.ICON_PATH+"server_0.gif";
//				String width = "30";
//				String height = "50";
//				String nodeJson = "{" + "id:'" + id + "'," + "name:'"
//						+ name+ "'," + "title:'"
//						+ title + "'," + "image:'"
//						+ image + "',width:" + width + ","
//						+ "height:" + height
//						+"}";
//				fipMenuJson.add(nodeJson);	
//			}
//		}
		return fipMenuJson;
	}
	
	private List<Network> getRMINetworks(){
		List<Network> nodes = new ArrayList<Network>();
		try{
			URL url = new URL(	"http://192.168.30.86:9090/services/InterfaceService?wsdl");
			QName qname = new QName("http://ws.i-soft.com.cn/","InterfaceService");
			Service service = Service.create(url, qname);
//			InterfaceService resCollect = service.getPort(InterfaceService.class);
//			nodes = resCollect.getNetworkNodes();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return nodes;
	}
}
