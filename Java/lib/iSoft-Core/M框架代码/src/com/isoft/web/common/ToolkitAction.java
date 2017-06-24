package com.isoft.web.common;

import java.util.Enumeration;

import com.opensymphony.xwork2.Action;

public abstract class ToolkitAction extends HackerMapAware implements Action {

	protected boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof String) {
			return ((String) obj).length() == 0;
		}
		return false;
	}

	protected Object setDefault(Object obj, Object defval){
		if(isEmpty(obj)){
			return defval;
		} else {
			return obj;
		}
	}
	
	protected int countRequest(String str){
		if(!isEmpty(str)){
			Enumeration<String> names = getRequest().getParameterNames();
			int count = 0;
			while(names.hasMoreElements()){
				if(names.nextElement().startsWith(str)){
					count++;
				}
			}
			return count;
		} else {
			return this.getRequest().getParameterMap().size();
		}
	}
}
