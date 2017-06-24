package com.isoft.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * Struts2 result for json
 */
public class JsonResult extends StrutsResultSupport {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -1479138901556722285L;

	/**
	 * json化属性名称
	 */
	private String jsonProperty;

	/**
	 * 总记录数属性名
	 */
	private String totleCountProperty;

	/**
	 * 是否是表格数据
	 */
	private boolean grid = false;

	public void setJsonProperty(String jsonProperty) {
		this.jsonProperty = jsonProperty;
	}

	public void setGrid(boolean grid) {
		this.grid = grid;
	}

	public void setTotleCountProperty(String totleCountProperty) {
		this.totleCountProperty = totleCountProperty;
	}

	/**
	 * 执行result主方法
	 */
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(getJsonString(invocation));
			writer.flush();
		} catch (IOException e) {

		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private String getJsonString(ActionInvocation invocation) {
		// 获取需要序列化的对象
		Object property = getSerializeObject(invocation);
		String jsonString = null;

		if (property instanceof JSONObject) {
			jsonString = property.toString();
		} else if (property instanceof Jsonable) {
			jsonString = ((Jsonable) property).toJsonString();
		} else if (property instanceof Collection<?> && grid) {
			jsonString = getPagingJsonString((Collection<?>) property,
					invocation);
		} else if (property instanceof Number || property instanceof String
				|| property instanceof Boolean) {
			JSONObject jobj = new JSONObject();
			jobj.put(jsonProperty, property.toString());
			jsonString = jobj.toString();
		} else {
			jsonString = JsonUtil.encodeObject2Json(property);
		}

		return jsonString;
	}

	/**
	 * 获取需要序列化的对象
	 * 
	 * @author Administrator
	 * @param invocation
	 * @return
	 */
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

	/**
	 * 获取分页json信息
	 * 
	 * @author Administrator
	 * @return
	 */
	private String getPagingJsonString(Collection<?> list,
			ActionInvocation invocation) {
		String methodName = StringUtils.isEmpty(totleCountProperty) ? "getTotleCount"
				: "get" + StringUtils.capitalize(totleCountProperty);
		try {
			int totleCount = (Integer) MethodUtils.invokeMethod(invocation
					.getAction(), "getTotleCount", null);
			JSONObject object = new JSONObject();

			// 生成分页的json数据对象
			object.put("success", true);
			object.put("totalCount", totleCount);
			object.put("list", JsonUtil.encodeCollection2Array(list));

			return object.toString();
		} catch (Exception e) {
			return JsonUtil.encodeObject2Json(ResultBean
					.getFailureResult("Can not invoke method: " + methodName));
		}
	}
}
