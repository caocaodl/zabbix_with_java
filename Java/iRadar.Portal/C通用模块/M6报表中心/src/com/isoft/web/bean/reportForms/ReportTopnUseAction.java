package com.isoft.web.bean.reportForms;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.csvreader.CsvWriter;
import com.isoft.biz.dao.reportForms.IReportTopnUseDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.reportForms.IReportTopnUsehandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

public class ReportTopnUseAction  extends IaasPageAction{
	public String doList() throws Exception{
		Map param = getVo();
	
		String host=(String) param.get("host");
		if("".equals(host)){
			
			param.put("host", "1");
			param.put("limt", "10");
			
		}
		setVo(param);
		return "page";
	}


//生成趋势线图
	public String getChars(){
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IReportTopnUsehandler.class);
		request.setCallDAOIF(IReportTopnUseDAO.class);
		request.setCallHandlerMethod(IReportTopnUsehandler.getChars);
	    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List results = dto.getListParam();
		setResultList(results);
		return "resultList";
	}
	//获取使用率列表
	public String numbuerLimit(){
	   //获取当前OS类型所占的数量 
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IReportTopnUsehandler.class);
		request.setCallDAOIF(IReportTopnUseDAO.class);
		request.setCallHandlerMethod(IReportTopnUsehandler.numbuerLimit);
	    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);
        IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List results = dto.getListParam();
		setResultList(results);
		return "resultList";
				
	}
	public void doCSV() throws Exception {
		//获得要导出数据
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IReportTopnUsehandler.class);
		request.setCallDAOIF(IReportTopnUseDAO.class);
		request.setCallHandlerMethod(IReportTopnUsehandler.getChars);
	    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
		//参数乱码转码
		param.put("host", new String(param.get("host").toString().getBytes("iso-8859-1"), "UTF-8"));
		param.put("timeUp", new String(param.get("timeUp").toString().getBytes("iso-8859-1"), "UTF-8"));
		param.put("timeDown", new String(param.get("timeDown").toString().getBytes("iso-8859-1"), "UTF-8"));
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent responsee = delegator(request);
		ParamDTO dto = (ParamDTO) responsee.getDTO();
		List dataList = dto.getListParam();

		//设置导出头信息		
		HttpServletResponse response = getResponse();		    
	    response.setHeader("Content-disposition","attachment;filename=inventoriesList.csv");
	    response.setContentType("application/csv");
		String host=(String) param.get("host");
	    ServletOutputStream out = response.getOutputStream();
		//加上UTF-8文件的标识字符  否则乱码
		out.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});  
	    //设置表头
		CsvWriter wr =new CsvWriter(out, ',', Charset.forName("GBK"));  
		String[] contents = {""+host+"","使用率"};                      
		wr.writeRecord(contents);  
		//设置数据行
		if(dataList.size()>0){
			String data[] = null;
			for(int i=0; i<dataList.size(); i++){
				Map map = (Map) dataList.get(i);
				data = new String[map.size()];
				data[0] = map.get("value").toString(); //厂商
				data[1] = map.get("label").toString(); //型号
				wr.writeRecord(data);
			}
		}
		
		wr.close(); 
		response.flushBuffer();		 
	}
	//导出当前列表
	public void doUseCSV() throws Exception {
		//获得要导出数据
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IReportTopnUsehandler.class);
		request.setCallDAOIF(IReportTopnUseDAO.class);
		request.setCallHandlerMethod(IReportTopnUsehandler.numbuerLimit);
	    request.setModuleName(ModuleConstants.MODULE_REPORT_FORMS);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
		//参数乱码转码
		param.put("host", new String(param.get("host").toString().getBytes("iso-8859-1"), "UTF-8"));
		param.put("limt", new String(param.get("limt").toString().getBytes("iso-8859-1"), "UTF-8"));
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent responsee = delegator(request);
		ParamDTO dto = (ParamDTO) responsee.getDTO();
		List dataList = dto.getListParam();

		//设置导出头信息		
		HttpServletResponse response = getResponse();		    
	    response.setHeader("Content-disposition","attachment;filename=data.csv");
	    response.setContentType("application/csv");
		String host=(String) param.get("host");
		String hostName=null;
		if(host.equals("1")){
			hostName="CPU";
		}else if(host.equals("2")){
			hostName="内存";
		}else if(host.equals("3")){
			hostName="磁盘";
		}else if(host.equals("4")){
			hostName="带宽";
		}
	    ServletOutputStream out = response.getOutputStream();
		//加上UTF-8文件的标识字符  否则乱码
		out.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});  
	    //设置表头
		CsvWriter wr =new CsvWriter(out, ',', Charset.forName("GBK"));  
		String[] contents = {""+hostName+"","使用率"};                      
		wr.writeRecord(contents);  
		//设置数据行
		if(dataList.size()>0){
			String data[] = null;
			for(int i=0; i<dataList.size(); i++){
				Map map = (Map) dataList.get(i);
				data = new String[map.size()];
				data[0] = map.get("Hostid").toString(); //厂商
				data[1] = map.get("Uselv").toString(); //型号
				wr.writeRecord(data);
			}
		}
		
		wr.close(); 
		response.flushBuffer();		 
	}
}
