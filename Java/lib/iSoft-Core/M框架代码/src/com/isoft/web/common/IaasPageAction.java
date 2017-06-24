package com.isoft.web.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class IaasPageAction extends A_PaginationAction {
	
	@SuppressWarnings("unchecked")
	public IaasPageAction() {
		Object key = null;
		HttpServletRequest reuqest = getRequest();
		if (reuqest != null) {
			Map params = reuqest.getParameterMap();
			Iterator ito = params.keySet().iterator();
			while (ito.hasNext()) {
				key = ito.next();
				this.vo.put(key, params.get(key));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Map vo = new HashMap(){

		private static final long serialVersionUID = 1L;
		
		@Override
		public Object put(Object key, Object value) {
			if (value != null) {
				if (value.getClass().isArray()) {
					Object[] values = (Object[]) value;
					if (values.length > 0) {
						return super.put(key, ((Object[]) value)[0]);
					} else {
						return super.put(key, null);
					}
				}
			}
			return super.put(key, value);
		}
		
	};
	@SuppressWarnings("unchecked")
	private List resultList = new ArrayList(0);

	@SuppressWarnings("unchecked")
	public Map getVo() {
		return vo;
	}

	@SuppressWarnings("unchecked")
	public void setVo(Map vo) {
		this.vo = vo;
	}

	@SuppressWarnings("unchecked")
	public List getResultList() {
		return resultList;
	}

	@SuppressWarnings("unchecked")
	public void setResultList(List dataList) {
		this.resultList = dataList;
	}
	
	public String doIndex() throws Exception {
		return SUCCESS;
	}
	
	private HashMap<String, Object> resultMap = new HashMap<String, Object>();

	public HashMap<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(HashMap<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	
}