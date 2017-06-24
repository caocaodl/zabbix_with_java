package com.isoft.biz.handlerimpl.platform.topo;

import static com.isoft.iradar.Cphp._;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IHostExpHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.web.view.TopoNode;
import com.isoft.imon.topo.web.view.TopoView;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.types.CArray;


public class HostExpHandler extends BaseLogicHandler implements IHostExpHandler {

	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doCategoryList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		List<Map> resultList = new ArrayList<Map>();
		IResponseEvent response = new ResponseEvent();
		HostExpDAO idao = (HostExpDAO)dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		CArray<String> categorys = idao.doCategoryList(param);
		if(Cphp.empty(categorys)){
			Map<Integer, TopoNode> nodes = TopoView.getInstance().getNodes();
			for (TopoNode topoNode : nodes.values()) {
				categorys.add(topoNode.getCategory());
			}
			Cphp.array_unique(categorys);
		}
		for(String category : categorys){
			Map map = new HashMap();
			String text = HostConstants.categoryMap.get(category);
			if(text==null||"".equals(text)){
				map.put("name", category);
				map.put("text", category);
			}else{
				map.put("name", category);
				map.put("text", text);
			}
			resultList.add(map);
		}
		
		ParamDTO dto = new ParamDTO();
        dto.setListParam(resultList);
		response.setDTO(dto);	
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doAssetsCategoryList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		HostExpDAO idao = (HostExpDAO)dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
	
		ParamDTO dto = new ParamDTO();
		List<Map> grouplist=idao.doAssetsCategoryList(param);
		for (Map object : grouplist) {
			if(object.get("name").equals(IMonConsts.DISCOVERED_HOSTS.toString())){
				object.put("text",_("Service application"));
			}
		}
        dto.setListParam(grouplist);
		response.setDTO(dto);	
		return response;
	}
	
}
