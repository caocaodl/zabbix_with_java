package com.isoft.biz.handlerimpl.inspectionReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.inspectionReport.InspectionReportDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.inspectionReport.IInspectionReportHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.util.StringUtil;

public class InspectionReportHandler extends BaseLogicHandler implements
		IInspectionReportHandler {

	public IResponseEvent doPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doPage(dataPage, param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doAdd(param);
		//添加巡检设备关联
		boolean flag = !StringUtil.isEmpty(ret[0]);
		if(flag){
			if(param.containsKey("hostIds")){
				Map mapParam = new HashMap();
				mapParam.put("inspectionId", ret[0]);
				String[] hids=((String)param.get("hostIds")).split(",");
				for(String hostId:hids){
					if(hostId.indexOf("@")!=-1){//设置应用集
						mapParam.put("hostId", hostId.substring(0,hostId.indexOf("@")));
						mapParam.put("applicationId", hostId.substring(hostId.indexOf("@")+1));
						String[] str=idao.doAddInspectionHost(mapParam);
						if(StringUtil.isEmpty(str[0]))
							flag=false;						
					}
				}
			}
		}
		dto.setBoolParam(flag);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUpdate(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUpdate(param);
		//对监控项进行修改
		Map mapParam = new HashMap();
		mapParam.put("inspectionId", param.get("id"));
		idao.doDeleteInspectionHost(mapParam); //删除监控项记录
		
		//添加巡检设备关联
		boolean flag = !StringUtil.isEmpty(ret[0]);
		if(flag){
			if(param.containsKey("hostIds")){
				mapParam.put("inspectionId", ret[0]);
				String[] hids=((String)param.get("hostIds")).split(",");
				for(String hostId:hids){
					if(hostId.indexOf("@")!=-1){//设置应用集
						mapParam.put("hostId", hostId.substring(0,hostId.indexOf("@")));
						mapParam.put("applicationId", hostId.substring(hostId.indexOf("@")+1));
						String[] str=idao.doAddInspectionHost(mapParam);
						if(StringUtil.isEmpty(str[0]))
							flag=false;						
					}
				}
			}
		}
		
		dto.setBoolParam(flag);
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doUpdateStatus(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		String[] ret = idao.doUpdateStatus(param);
		dto.setBoolParam(!StringUtil.isEmpty(ret[0]));
		dto.setStrParam(ret[1]);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doCheckName(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		boolean bool = idao.doCheckName(param);
		dto.setBoolParam(bool);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doHostApplication(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		/*List<Map> dataList = idao.doMonitorHost(param);
		List<Map> inspectionHostList = idao.doInspectionHostApplication(param);
		if(dataList.size()>0 && inspectionHostList.size()>0){ //修改时 设置设备选中状态
			for(Map ihl : inspectionHostList){
				for(Map dl : dataList){
					if(ihl.get("hostId").equals(dl.get("hostid"))){
						dl.put("checked", true);
					}
				}
			}
		}*/
		List<Map> hostList = idao.doMonitorHost(param);
		List<Map> applicationList = idao.doMonitorApplication(param);
		List<Map> inspectionHostApplicationList = idao.doInspectionHostApplication(param);
		List<Map> dataList = new ArrayList();
		Map data = null;
		if(hostList.size()>0){
			for(Map hostMap : hostList){//封装设备
				data = new HashMap();
				data.put("id", hostMap.get("hostId"));
				data.put("name", hostMap.get("hostName"));
				data.put("pid", "0");
				if(inspectionHostApplicationList.size()>0 && !"".equals(param.get("inspectionId"))){//设置设备选中(不为空是区分新增跟修改)
					for(Map haMap : inspectionHostApplicationList){
						if(hostMap.get("hostId").equals(haMap.get("hostId"))){
							data.put("checked", true);
							data.put("open", true);
							break;
						}
					}
				}
				dataList.add(data);
				
				if(applicationList.size()>0){
					for(Map applicationMap : applicationList){//封装设备下的应用集
						data = new HashMap();
						data.put("id", hostMap.get("hostId")+"@"+applicationMap.get("applicationId"));
						data.put("name", applicationMap.get("applicationIdName"));
						data.put("pid", hostMap.get("hostId"));
						if(inspectionHostApplicationList.size()>0 && !"".equals(param.get("inspectionId"))){//设置设备下的应用集选中 (不为空是区分新增跟修改)
							for(Map hostApplicationMap : inspectionHostApplicationList){
								if(hostApplicationMap.get("hostId").equals(hostMap.get("hostId"))
										&&hostApplicationMap.get("applicationId").equals(applicationMap.get("applicationId"))){
									data.put("checked", true);
								}
							}
						}
						dataList.add(data);
					}
				}
			}
		}
				
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doInspectionHistoryPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doInspectionHistoryPage(dataPage, param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doInspectionHistoryInfo(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doInspectionHistoryInfo(dataPage, param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doInspectionTimeRuleList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		InspectionReportDAO idao = (InspectionReportDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		List dataList = idao.doInspectionTimeRuleList(param);
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}
}
