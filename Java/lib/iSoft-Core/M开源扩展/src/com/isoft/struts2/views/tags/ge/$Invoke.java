package com.isoft.struts2.views.tags.ge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsException;

import com.isoft.struts2.components.AndurilComponent;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

public class $Invoke extends AndurilComponent {

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    
    public $Invoke(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
        return false;
    }

    @Override
    protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
        Object action = ActionContext.getContext().getActionInvocation().getAction();
        Method method;
        try {
            method = action.getClass().getMethod(this.method, EMPTY_CLASS_ARRAY);
        } catch (Exception e) {
            throw new StrutsException(e);
        }
        try {
            method.invoke(action, new Object[0]);
        } catch (IllegalArgumentException e) {
            throw new StrutsException(e);
        } catch (IllegalAccessException e) {
            throw new StrutsException(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause == null) {
                cause = e.getCause();
            }

            if (cause == null) {
                throw new StrutsException(e);
            }

            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new StrutsException(cause);
        }
        return false;
    }
    
    private String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }   
}
