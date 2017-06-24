package com.isoft.struts2.views.tags.ui.select;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.AndurilTagSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class SelectItemTag extends AndurilTagSupport {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req,
            HttpServletResponse res) {
        return new UISelectItem(stack,req,res);
    }

    @Override
    protected void setProperties(AndurilComponent component) {
        super.setProperties(component);
        
        UISelectItem aui = (UISelectItem)component;
        if (_itemLabel != null) {
            if (isValueReference(_itemLabel)) {
                this.setValueBinding(aui, "itemLabel", _itemLabel);
            } else {
                aui.setItemLabel(_itemLabel);
            }
        }
        if (_itemValue != null) {
            if (isValueReference(_itemValue)) {
                this.setValueBinding(aui, "itemValue", _itemValue);
            } else {
                aui.setItemValue(_itemValue);
            }
        }
    }

    @Override
    public void release() {
        this._itemLabel = null;
        this._itemValue = null;
    }    
    
    private String _itemLabel;
    private String _itemValue;
    
    public void setItemLabel(String itemLabel) {
        this._itemLabel = itemLabel;
    }

    public void setItemValue(String itemValue) {
        this._itemValue = itemValue;
    }
    
}
