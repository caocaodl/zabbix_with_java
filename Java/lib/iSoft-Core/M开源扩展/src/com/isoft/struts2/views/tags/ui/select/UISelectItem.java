package com.isoft.struts2.views.tags.ui.select;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.SelectItem;
import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.SelectHolder;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class UISelectItem extends AndurilComponent {
    
    public UISelectItem(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
        return false;
    }
    
    @Override
    protected void popComponentStack() {
        super.popComponentStack();
        Object component = this.getComponentStack().peek();
        if(component instanceof SelectHolder){
            SelectItem si = new SelectItem(getItemValue(), getItemLabel());
            ((SelectHolder)component).pushSelectItem(si);
        }
    }



    private String _itemLabel = null;
    private Object _itemValue = null;
    
    public void setItemLabel(String itemLabel) {
        _itemLabel = itemLabel;
    }

    public String getItemLabel() {
        if (_itemLabel != null)
            return _itemLabel;
        ValueBinding vb = getValueBinding("itemLabel");
        return vb != null ? (String) vb.getValue(this.getStack()) : null;
    }

    public void setItemValue(Object itemValue) {
        _itemValue = itemValue;
    }

    public Object getItemValue() {
        if (_itemValue != null)
            return _itemValue;
        ValueBinding vb = getValueBinding("itemValue");
        return vb != null ? (Object) vb.getValue(this.getStack()) : null;
    }
}
