package com.isoft.biz.web.platform.topo;

import java.util.Map;

import com.isoft.biz.dao.platform.topo.ITopoDataOperDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ITopoDataOperHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.types.CArray;
import com.isoft.web.common.IaasPageAction;

/**
 * 拓扑节点坐标保存类
 * 前端传递的参数：
 * 			   1.topoType:拓扑类型
 * 			   2.hostId  :节点hostId
 * 			   3.X		 :节点X坐标
 * 			   4.Y       :节点Y坐标
 * 			   5.nodeType:节点类型
 */
public class TopoDataOperAction extends IaasPageAction{

	/**
	 *拓扑类型
	 *1.物理链路拓扑  			TopoPhy
	 *2.虚拟链路拓扑(运营商)	TopoVirtAdmin
	 *3.虚拟链路拓扑(租户)		TopoVirtTenant
	 *4.云主机从属拓扑			TopoCloudVm
	 *5.机房拓扑				TopoCab
	 *6.客户业务拓扑			TopoBiz
	 *
	 *节点类型
	 *	物理链路:
	 *		1.host节点		host
	 *		2.缩略图节点		group
	 *	机房拓扑:
	 * 		1.host节点		host
	 *  	2.机柜节点			cabinet
	 *	业务拓扑:
	 *		1.host节点		host
	 *		2.业务节点	    	bizNode
	 *		3.业务区域节点	    bizArea
	 *		4.云主机区域节点	    bizVmArea
	 *		5.应用区域节点	    bizAppArea
	 */
	public static String TOPO_PHY 		 = "TopoPhy";
	public static String TOPO_VIRTADMIN  = "TopoVirtAdmin";
	public static String TOPO_VIRTTENANT = "TopoVirtTenant";
	public static String TOPO_CLOUDVM 	 = "TopoCloudVm";
	public static String TOPO_CAB 		 = "TopoCab";
	public static String TOPO_BIZ 		 = "TopoBiz";
	
	public static String NODE_HOST 		 	= "NODEHOST";
	public static String NODE_GROUP 	 	= "NODEGROUP";
	public static String NODE_CABINET 	 	= "NODECABINET";
	public static String NODE_BIZNODE 	 	= "NODEBIZNODE";
	public static String NODE_BIZAREA 	 	= "NODEBIZAREA";
	public static String NODE_BIZVMAREA  	= "NODEBIZVMAREA";
	public static String NODE_BIZAPPAREA 	= "NODEBIZAPPAREA";
	public static String NODE_BIZSERVERAREA = "NODEBIZSERVERAREA";
	public static String NODE_BIZNETDEVAREA = "NODEBIZNETDEVAREA";
	public static CArray<String> bizAreaCA = CArray.array(NODE_BIZAREA,NODE_BIZVMAREA,NODE_BIZAPPAREA,NODE_BIZSERVERAREA,NODE_BIZNETDEVAREA);
	
	public String doSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doTopoDataLocOperSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	/**
	 * 保存机房拓扑中机柜的X Y坐标
	 */
	public String doCabTopoCabinetSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doCabTopoCabinetDataLocSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	/**
	 * 保存机房拓扑中设备的X Y坐标
	 */
	public String doCabTopoHostSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doCabTopoHostDataLocSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	/**
	 * 保存物理链路拓扑的缩略图数据
	 * @return
	 * @throws Exception
	 */
	public String doPhyTopoTbnailSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doPhyTopoTbnailSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("tbnailId", dto.getStrParam());
		return "resultMap";
	}
	
	/**
	 * 删除物理链路拓扑的缩略图数据
	 * @return
	 * @throws Exception
	 */
	public String doPhyTopoTbnailDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doPhyTopoTbnailDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		Map param = getVo();
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("nodes", dto.getListParam());
		return "resultMap";
	}
	
	/**
	 * 保存物理链路拓扑的坐标数据
	 * @return
	 * @throws Exception
	 */
	public String doPhyTopoSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doPhyTopoLocSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	/**
	 * 保存业务拓扑的坐标数据
	 * @return
	 * @throws Exception
	 */
	public String doBizTopoLocSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ITopoDataOperHandler.class);
		request.setCallDAOIF(ITopoDataOperDAO.class);
		request.setCallHandlerMethod(ITopoDataOperHandler.doBizTopoLocSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
}

