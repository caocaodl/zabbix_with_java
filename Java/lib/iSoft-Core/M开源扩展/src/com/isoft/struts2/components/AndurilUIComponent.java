package com.isoft.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.struts2.convert.Converter;
import com.isoft.struts2.interceptor.ValueBindingInterceptor;
import com.opensymphony.xwork2.util.ValueStack;

public abstract class AndurilUIComponent extends AndurilComponent {
	
	public final static String JSON_PROP_COMMA = "\"%s\":\"%s\",";
	public final static String JSON_PROP = "\"%s\":\"%s\"";
	public final static String HTML_PROP_SPACE = "%s=\"%s\" ";
	
    private String id;
    private Converter converter;
        
    public AndurilUIComponent(ValueStack stack, HttpServletRequest request,
            HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public Converter findConverter(Object value) {
        if (this.converter != null) {
            return this.converter;
        }
        if (value != null) {
            this.converter = ValueBindingInterceptor.getConvertor(value.getClass());
        }
        if (this.converter == null) {
            this.converter = ValueBindingInterceptor.getConvertor(String.class);
        }
        return this.converter;
    }

    public void setConverter(String converter) {
        this.converter = ValueBindingInterceptor.getConvertor(converter);
    }
    
    protected String encodeValueAttr(Object value) {
        return findConverter(value).getAsString(value);
    }

    protected String encodeNameAttr(ValueBinding vb, Object value) {
//        return ValueBinding
//                .encodeExpressionString(findConverter(value), vb
//                        .getExpressionString());
    	return vb.getExpressionString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
	public Converter getConverter() {
		return converter;
	}

	public void setConverter(Converter converter) {
		this.converter = converter;
	} 
}
