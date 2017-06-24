package com.isoft.struts2.result;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.isoft.framework.common.DataPage;
import com.isoft.json.JsonUtil;
import com.isoft.json.ResultBean;
import com.opensymphony.xwork2.ActionInvocation;

public class JQGridResult extends StrutsResultSupport {

	private static final long serialVersionUID = 1L;

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

	@SuppressWarnings("unchecked")
	private String getJsonString(ActionInvocation invocation) {
		Object property = getSerializeObject(invocation);
		if(property instanceof DataPage){
			DataPage dataPage = (DataPage)property;
			Map info = new HashMap(5);
			//info.put("page", String.valueOf(dataPage.getPage()));
			//info.put("total",String.valueOf(dataPage.getTotalPage()));
			//info.put("records", String.valueOf(dataPage.getTotalCount()));
			info.put("total", dataPage.getTotalCount());
			info.put("rows", dataPage.getList());
			return JsonUtil.encodeObject2Json(info);
		} else {
			return JsonUtil.encodeObject2Json(property);
		}
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
