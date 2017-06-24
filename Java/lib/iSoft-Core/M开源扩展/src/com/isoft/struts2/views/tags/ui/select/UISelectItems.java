package com.isoft.struts2.views.tags.ui.select;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.SelectItem;
import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.components.SelectHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class UISelectItems extends AndurilComponent {
    
    public UISelectItems(ValueStack stack, HttpServletRequest request,
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
    
    @SuppressWarnings("unchecked")
    @Override
    protected void popComponentStack() {
        super.popComponentStack();
        Object component = this.getComponentStack().peek();
        if(component instanceof SelectHolder){
            List<SelectItem> siList = (List<SelectItem>)this.getValue();
            ((SelectHolder)component).pushSelectItems(siList);
        }
    }
}
