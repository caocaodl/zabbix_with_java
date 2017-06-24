package com.isoft.struts2.views.tags.ge;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class $InvokeTag extends AndurilTagSupport{

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new $Invoke(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        $Invoke tagBean = ($Invoke)component;
        tagBean.setMethod(this.getValueBindingExpr(_method));
    }
    
    @Override
    public void release() {
        super.release();
        this._method = null;
    }
    
    private String _method;

    public void setMethod(String method) {
        this._method = method;
    }
}
