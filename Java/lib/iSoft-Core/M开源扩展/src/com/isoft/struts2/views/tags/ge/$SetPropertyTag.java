package com.isoft.struts2.views.tags.ge;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class $SetPropertyTag extends AndurilTagSupport{

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new $SetProperty(stack, req, res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        $SetProperty tagBean = ($SetProperty)component;
        tagBean.setPropertyName(this._propertyName);
    }
    
    @Override
    public void release() {
        super.release();
        this._propertyName = null;
    }
    
    private String _propertyName;

    public void setPropertyName(String propertyName) {
        this._propertyName = propertyName;
    }
}
