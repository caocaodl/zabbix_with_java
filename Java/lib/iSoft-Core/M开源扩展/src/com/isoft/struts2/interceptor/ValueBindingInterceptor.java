package com.isoft.struts2.interceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.convert.Converter;
import com.isoft.struts2.convert.StringConverter;
import com.isoft.struts2.convert.UploadedFileConverter;
import com.isoft.struts2.util.ContentParseUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.NoParameters;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

@SuppressWarnings("unchecked")
public class ValueBindingInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 1L;
    
    private static Map<Object,Converter> convertors = Collections.EMPTY_MAP;
    private static Object CONVERTOR_LOAD_LOCK = new Object();
    private ValueStackFactory valueStackFactory;
    
    private OgnlUtil ognlUtil;
    
    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }    

    public void setConvertors(String convertors) throws ClassNotFoundException {
        synchronized(ValueBindingInterceptor.CONVERTOR_LOAD_LOCK){
            if(ValueBindingInterceptor.convertors.size()>0){
                return;
            }
            ValueBindingInterceptor.convertors = new HashMap<Object,Converter>();
            Set<String> centrys = ContentParseUtil.semicolonDelimitedStringToSet(convertors);
            for (String entry : centrys) {
                List<String> cparts = ContentParseUtil.colonDelimitedStringToSet(entry);
                Map<String,String> params = new HashMap<String,String>();
                try{
                    List<String> cvMapping = ContentParseUtil.parenthesesDelimitedStringToSet(cparts.get(3));
                    Class<?> cvClazz = Class.forName("com.isoft.struts2.convert."+cvMapping.get(0));
                    Converter convertor = (Converter)cvClazz.newInstance();
                    if (cvMapping.size() > 0) {
                        for (int i = 1; i < cvMapping.size(); i++) {
                            List<String> mp =ContentParseUtil.equalDelimitedStringToSet(cvMapping.get(i));
                            params.put(mp.get(0), mp.get(1));
                        }
                    }
                    convertor.setId(cparts.get(0));
                    convertor.setObjClass(Class.forName(cparts.get(1)));
                    ognlUtil.setProperties(params, convertor);
                    ValueBindingInterceptor.convertors.put(convertor.getId(), convertor);
                    if("Y".equalsIgnoreCase(cparts.get(2))){
                       ValueBindingInterceptor.convertors.put(convertor.getObjClass(), convertor);
                    }
                }catch(Exception cnfe){
                    throw new StrutsException(cnfe);
                }
            }
        }
    }
    
    public static Converter getConvertor(String clasz) {
    	try{
	    	Class<?> cvClazz = Class.forName("com.isoft.struts2.convert."+clasz+"Converter");
	        Converter convertor = (Converter)cvClazz.newInstance();
	        //return ValueBindingInterceptor.convertors.get(cid);
	        return convertor;
    	}catch(Exception e){
    		return new StringConverter();
    	}
    }
    
    public static Converter getConvertor(Class clasz) {
    	try{
	    	Class<?> cvClazz = Class.forName("com.isoft.struts2.convert."+clasz.getSimpleName()+"Converter");
	        Converter convertor = (Converter)cvClazz.newInstance();
	        //return ValueBindingInterceptor.convertors.get(cid);
	        return convertor;
    	}catch(Exception e){
    		return new StringConverter();
    	}
    }
    
    public static void main(String[] args){
    	Class<String> c = String.class;
    	System.out.println(c.getSimpleName());
    	//getConvertor(String.class);
    }
    
    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            final Map<String, Object> parameters = retrieveParameters(ac);

            if (parameters != null) {
                //Map<String, Object> contextMap = ac.getContextMap();
                ValueStack stack = ac.getValueStack();
                setParameters(action, stack, parameters);
            }
        }
        return invocation.invoke();
    }

    /**
     * Gets the parameter map to apply from wherever appropriate
     *
     * @param ac The action context
     * @return The parameter map to apply
     */
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        return ac.getParameters();
    }


    protected void setParameters(Object action, ValueStack stack, final Map<String, Object> parameters) {
        ValueStack newStack = valueStackFactory.createValueStack(stack);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String name = entry.getKey();
            boolean acceptableName = acceptableName(name);
            if (acceptableName) {
                String[] vb = ValueBinding.decodeExpressionString(name);
                Converter converter = ValueBindingInterceptor.getConvertor(vb[0]);
                if(converter!=null){
                    if (converter instanceof UploadedFileConverter) {
                        HttpServletRequest request = (HttpServletRequest) stack.getContext().get(ServletActionContext.HTTP_REQUEST);
                        if(request instanceof MultiPartRequestWrapper){
                            try {
                                Object[] value = (Object[])entry.getValue();
                                newStack.setValue(vb[1], value);
                            } catch (RuntimeException e) {
                                throw new StrutsException(e);
                            }
                        }
                    } else {
                        try {
                            String[] value = (String[])entry.getValue();
                            newStack.setValue(vb[1], converter.getAsObject(value));
                        } catch (RuntimeException e) {
                            throw new StrutsException(e);
                        }
                    }
                }
            }
        }
    }
    
    protected boolean acceptableName(String name) {
        if (isAccepted(name) && !isExcluded(name)) {
            return true;
        }
        return false;
    }

    protected boolean isAccepted(String paramName) {
        return ValueBinding.isAccepted(paramName);
    }

    protected boolean isExcluded(String paramName) {
        return false;
    }
}
