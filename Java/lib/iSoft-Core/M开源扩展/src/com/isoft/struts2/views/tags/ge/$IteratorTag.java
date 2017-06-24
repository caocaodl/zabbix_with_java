package com.isoft.struts2.views.tags.ge;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.IteratorComponent;

import com.isoft.struts2.util.UIComponentTagUtils;
import com.opensymphony.xwork2.util.ValueStack;

public class $IteratorTag extends org.apache.struts2.views.jsp.IteratorTag {

    private static final long serialVersionUID = 1;

    private String _statusAttr;
    private String _value;
//    private String _begin;
//    private String _end;
//    private String _step;
    private String _rendered;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new IteratorComponent(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();

        IteratorComponent tag = (IteratorComponent) getComponent();
        tag.setStatus(_statusAttr);
        tag.setValue(_value);
//        tag.setBegin(_begin);
//        tag.setEnd(_end);
//        tag.setStep(_step);
        if (_value != null) {
            if (UIComponentTagUtils.isValueReference(_value)) {
                tag.setValue(UIComponentTagUtils.getValueBindingExpr(_value));
            } else {
                tag.setValue(_value);
            }
        }
    }

    public void setStatus(String status) {
        this._statusAttr = status;
    }

    public void setValue(String value) {
        this._value = value;
    }

//    public void setBegin(String begin) {
//        this._begin = begin;
//    }
//
//    public void setEnd(String end) {
//        this._end = end;
//    }
//
//    public void setStep(String step) {
//        this._step = step;
//    }
    
    public void setRendered(String _rendered) {
        this._rendered = _rendered;
    }
    
    @Override
    public void release() {
        super.release();
        this._statusAttr = null;
        this._value = null;
//        this._begin = null;
//        this._end = null;
//        this._step = null;
        this._rendered = null;
    }
    
    @Override
    public int doStartTag() throws JspException {
        boolean rendered = true;
        if (_rendered != null) {
            if (UIComponentTagUtils.isValueReference(_rendered)) {
                rendered = (Boolean) findValue(UIComponentTagUtils.getValueBindingExpr(_rendered), Boolean.class);
            } else {
                rendered = Boolean.valueOf(_rendered);
            }
        }
        if(rendered){
            return super.doStartTag();
        }else {
            return SKIP_BODY;
        }
    }

    @Override
    public int doEndTag() throws JspException {
        component = null;
        return EVAL_PAGE;
    }

    @Override
    public int doAfterBody() throws JspException {
        boolean again = component.end(pageContext.getOut(), getBody());

        if (again) {
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (Exception e) {
                    throw new JspException(e.getMessage());
                }
            }
            return SKIP_BODY;
        }
    }

}
