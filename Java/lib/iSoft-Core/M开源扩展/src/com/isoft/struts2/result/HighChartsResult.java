package com.isoft.struts2.result;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.isoft.json.ResultBean;
import com.opensymphony.xwork2.ActionInvocation;

public class HighChartsResult extends StrutsResultSupport {

	private static final long serialVersionUID = -1479138901556722285L;

	private String jsonProperty;

	public void setJsonProperty(String jsonProperty) {
		this.jsonProperty = jsonProperty;
	}

	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(getJsonString(invocation));
			writer.flush();
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private String getJsonString(ActionInvocation invocation) {
		Object property = getSerializeObject(invocation);
		//return JsonUtil.encodeObject2Json(property);
		Map<BigInteger,Map<Long,Map<String,Map>>> data = (Map)property;
		Set<BigInteger> set = data.keySet();
		
		StringBuilder s = new StringBuilder();
		s.append("[");
		boolean first = true;
		for(BigInteger itemid:set){
			if(first){
				first = false;
			} else {
				s.append(",");
			}
			s.append("[");
			Map<String,Map>  items =data.get(itemid).get(0);
			Map<Long,Integer> dataClock = items.get("clock");
			Map<Long,Double> dataMax = items.get("avg");
			for(long i =0;i<dataClock.size();i++){
				if(i>0){
					s.append(",");
				}
				s.append("[");
				s.append(dataClock.get(i)*1000L+8*3600*1000L);
				s.append(",");
				s.append(dataMax.get(i)*100);
				//s.append(i);
				s.append("]");
			}
			s.append("]");
		}
		s.append("]");
		//return property.toString();
		return s.toString();
	}

	private Object getSerializeObject(ActionInvocation invocation) {
		Object property = null;

		if (StringUtils.isEmpty(jsonProperty)) {
			property = ResultBean.getSuccessResult();
		} else {
			Object action = invocation.getAction();
			String methodName = "get" + StringUtils.capitalize(jsonProperty);
			try {
				property = MethodUtils.invokeMethod(action, methodName, null);
			} catch (Exception e) {
				final String errorMessage = "can not find property "
						+ jsonProperty + " of action "
						+ action.getClass().getName();
				property = ResultBean.getFailureResult(errorMessage);
			}
			if (property == null) {
				final String errorMessage = "the property " + jsonProperty
						+ "is null";
				property = ResultBean.getFailureResult(errorMessage);
			}
		}

		return property;
	}

}
