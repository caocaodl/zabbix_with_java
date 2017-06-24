package com.isoft.struts2.views.tags.ui.ztree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.views.tags.ge.$IteratorTag;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeNodeIteratorTag extends $IteratorTag {

	private static final long serialVersionUID = 1L;
	
	@Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new $ZTreeNodeIterator(stack);
    }

}
