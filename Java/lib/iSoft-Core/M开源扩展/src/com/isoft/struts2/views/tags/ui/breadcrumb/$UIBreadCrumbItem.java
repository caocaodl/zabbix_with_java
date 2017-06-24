package com.isoft.struts2.views.tags.ui.breadcrumb;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.BreadCrumbItem;
import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.BreadCrumbHolder;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $UIBreadCrumbItem extends AndurilComponent {
    
    public $UIBreadCrumbItem(ValueStack stack, HttpServletRequest request,
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
        if(component instanceof BreadCrumbHolder){
        	BreadCrumbItem bci = new BreadCrumbItem(getItemLabel());
            ((BreadCrumbHolder)component).pushBreadCrumbItem(bci);
        }
    }



    private String _itemLabel = null;
        
    public void setItemLabel(String itemLabel) {
        _itemLabel = itemLabel;
    }

    public String getItemLabel() {
        if (_itemLabel != null)
            return _itemLabel;
        ValueBinding vb = getValueBinding("itemLabel");
        return vb != null ? (String) vb.getValue(this.getStack()) : null;
    }
}
