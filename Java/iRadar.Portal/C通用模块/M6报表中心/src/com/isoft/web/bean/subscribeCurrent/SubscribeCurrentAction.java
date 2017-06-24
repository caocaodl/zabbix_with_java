package com.isoft.web.bean.subscribeCurrent;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import com.csvreader.CsvWriter;
import com.isoft.biz.dao.subscribeCurrent.ISubscribeCurrentDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.subscribeCurrent.ISubscribeCurrentHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
import com.isoft.web.listener.DataSourceEnum;

public class SubscribeCurrentAction extends IaasPageAction {
	
	public String doIndex()throws Exception{
		return "success";
	}
	//获取数据
	public String doReport() throws Exception {
		
		RequestEvent request = new RequestEvent();
		request.setCallDAOIF(ISubscribeCurrentDAO.class);
		request.setCallHandlerIF(ISubscribeCurrentHandler.class);
		request.setCallHandlerMethod(ISubscribeCurrentHandler.doSubscribeCurrentPage);
	    request.setModuleName(ModuleConstants.MODULE_SUBSCRIBECURRENT);
	    request.setDataSource(DataSourceEnum.IRADAR);
	    
		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		setResultList(dataList);
		return "resultList";
	}
	//导出趋势图数据报表
	public void doReportCSV() throws Exception {
		
		//获得要导出数据
		RequestEvent request = new RequestEvent();
		request.setCallDAOIF(ISubscribeCurrentDAO.class);
		request.setCallHandlerIF(ISubscribeCurrentHandler.class);
		request.setCallHandlerMethod(ISubscribeCurrentHandler.doSubscribeCurrentPage);
		request.setModuleName(ModuleConstants.MODULE_SUBSCRIBECURRENT);
		request.setDataSource(DataSourceEnum.IRADAR);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		//参数乱码转码
		param.put("startTime", new String(param.get("startTime").toString().getBytes("iso-8859-1"), "UTF-8"));
		param.put("endTime", new String(param.get("endTime").toString().getBytes("iso-8859-1"), "UTF-8"));
		
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent responsee = delegator(request);
		ParamDTO dto = (ParamDTO) responsee.getDTO();
		List dataList = dto.getListParam();

		//设置导出头信息
		HttpServletResponse response = getResponse();		    
	    response.setHeader("Content-disposition","attachment;filename=subscribeCurrent.csv");
	    response.setContentType("application/csv");
		
	    ServletOutputStream out = response.getOutputStream();
		//加上UTF-8文件的标识字符  否则乱码
		out.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});  
	    //设置表头
		CsvWriter wr =new CsvWriter(out, ',', Charset.forName("GBK"));  
		String[] contents = {"监控服务名称","分值"};                      
		wr.writeRecord(contents);  
		//设置数据行
		if(dataList.size()>0){
			String data[] = null;
			for(int i=0; i<dataList.size(); i++){
				Map map = (Map) dataList.get(i);
				data = new String[map.size()];
				data[0] = map.get("hostName").toString(); //监控服务名称
				data[1] = map.get("score").toString(); //分值
				wr.writeRecord(data);
			}
		}
		wr.close(); 
		response.flushBuffer();		 
	}
}
