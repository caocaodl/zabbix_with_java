package com.isoft.web.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.ISelectItemDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.ISelectItemHandler;
import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.utils.DictUtil;
import com.isoft.web.common.lang.DateTimeMap;
import com.isoft.web.common.lang.MaxLengthMap;
import com.isoft.web.common.lang.NotEmptyMap;
import com.isoft.web.common.lang.RequestAttributeMap;
import com.isoft.web.common.lang.RequestParaMap;
import com.isoft.web.common.lang.RequestParasMap;
import com.isoft.web.common.lang.SessionAttributeMap;

public abstract class HackerMapAware extends DelegateAware implements
        ServletAware {

    @SuppressWarnings("unchecked")
    private Map notEmpty;

    @SuppressWarnings( { "unchecked" })
    public Map getNotEmpty() {
        if (notEmpty == null) {
            notEmpty = new NotEmptyMap(getRequest());
        }
        return notEmpty;
    }

    @SuppressWarnings("unchecked")
    private Map param;

    @SuppressWarnings( { "unchecked" })
    public Map getParam() {
        if (param == null) {
            param = new RequestParaMap(getRequest());
        }
        return param;
    }

    @SuppressWarnings("unchecked")
    private Map params;

    @SuppressWarnings( { "unchecked" })
    public Map getParams() {
        if (params == null) {
            params = new RequestParasMap(getRequest());
        }
        return params;
    }

    @SuppressWarnings("unchecked")
    private Map dateTime;

    @SuppressWarnings("unchecked")
    public Map getDateTime() {
        if (dateTime == null) {
            dateTime = new DateTimeMap();
        }
        return dateTime;
    }

    @SuppressWarnings("unchecked")
    private Map sessionAttr;

    public Map getSessionAttr() {
        if (sessionAttr == null) {
            sessionAttr = new SessionAttributeMap(getSession());
        }
        return sessionAttr;
    }

    private Map requestAttr;

    public Map getRequestAttr() {
        if (requestAttr == null) {
            requestAttr = new RequestAttributeMap(getRequest());
        }
        return requestAttr;
    }
    
    private static List<Class> definesClass = new ArrayList(0);
    private DefinesMap defines = null;

    public Map getDefines() {
    	if(this.defines == null){
    		synchronized(lock){
    			if(this.defines == null){
    				this.defines = new DefinesMap(0);
					for (Class define : definesClass) {
						Field[] fields = define.getDeclaredFields();
						for(Field field:fields){
							int mod = field.getModifiers();
							if (Modifier.isPublic(mod)
									&& Modifier.isStatic(mod)) {
								try {
									this.defines.set(field.getName(), field.get(null));
								} catch (Exception e) {
								}
							}
						}
					}
    			}
    		}    		
    	}
    	Class[] extendsClass = getDefinesClass();
		if (extendsClass != null && extendsClass.length>0) {
			for (Class clazz : extendsClass) {
				if (!definesClass.contains(clazz)) {
					synchronized(lock){
						if (!definesClass.contains(clazz)) {
							Field[] fields = clazz.getDeclaredFields();
							for(Field field:fields){
								int mod = field.getModifiers();
								if (Modifier.isPublic(mod)
										&& Modifier.isStatic(mod)) {
									try {
										this.defines.set(field.getName(), field.get(null));
									} catch (Exception e) {
									}
								}
							}
							definesClass.add(clazz);
						}
					}
				}
			}
		}
        return this.defines;
    }
    
    protected Class[] getDefinesClass() {
		return null;
	}

	class DefinesMap extends HashMap {
        private static final long serialVersionUID = 1L;
        
        public DefinesMap(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }
        
        public Object set(Object key, Object value) {
            return super.put(key, value);
        }
    }

    private Map permItem = new PermMap(0);

    public Map getPermItem() {
        return permItem;
    }

    class PermMap extends HashMap {
        private static final long serialVersionUID = 1L;
        
        public PermMap(int initialCapacity) {
            super(initialCapacity);
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public boolean containsKey(Object key) {
            return true;
        }

        @Override
        public Object get(Object perm) {
            IdentityBean idBean = getIdentityBean();
            if (idBean.isAdmin()) {
                return true;
            }
            String funcLabel = (String) perm;
            funcLabel = hookGetPermItem(funcLabel);
            if (funcLabel == null) {
                return Boolean.FALSE;
            }
//            if (FuncIdEnum.DEFAULT_FUNID.magic().equals(funcLabel)) {
//                return Boolean.TRUE;
//            }
            return idBean.getPermFuncLabels().containsKey(funcLabel);
        }
    }

    private Map selectItem = new SelectItemMap();

    public Map getSelectItem() {
        return selectItem;
    }

    class SelectItemMap extends HashMap {
        private static final long serialVersionUID = 1L;

        public SelectItemMap() {
        }

        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public Object get(Object key) {
            key = hookGetSelectItemsKey(key);
            Object cacheObj = super.get(key);
            if (cacheObj != null) {
                return cacheObj;
            }

            List hookItems = hookGetSelectItems(key);
            if (hookItems == null) {
                hookItems = (List) DictUtil.getDicSltItemByType((String) key);
                if (hookItems == null) {
                    hookItems = new ArrayList();
                }
            }
            hookItems = hookPostSelectItems(key, hookItems);
            super.put(key, hookItems);
            return hookItems;
        }

        @Override
        public boolean containsKey(Object key) {
            return true;
        }
    }

    private Map maxLength;

    public Map getMaxLength() {
        if (maxLength == null) {
            maxLength = new MaxLengthMap(0);
        }
        return maxLength;
    }

    protected Object hookGetSelectItemsKey(Object key) {
        return key;
    }
    
    protected void hookGetSelectItemsRequestEvent(RequestEvent request,ParamDTO dto) {

    }

    protected List hookGetSelectItems(Object key) {
        boolean isExtDict = DictUtil.isExtendDict((String) key);
        if (isExtDict) {
            RequestEvent request = new RequestEvent();
            request.setCallHandlerIF(ISelectItemHandler.class);
            request.setCallDAOIF(ISelectItemDAO.class);
            request.setCallHandlerMethod(ISelectItemHandler.METHOD_GETSELECTITEMS);
            request.setModuleName(Constant.MODULE_COMMON);
            ParamDTO dto = new ParamDTO();
            dto.setStrParam((String) key);
            Map mapParam = hookGetSelectItemsParams((String) key);
			dto.setMapParam(mapParam);
            request.setDTO(dto);
            
            hookGetSelectItemsRequestEvent(request,dto);

            IResponseEvent response = delegator(request);
            if (response.hasException()) {
                return new ArrayList(0);
            }

            dto = (ParamDTO) response.getDTO();
            return dto.getListParam();
        } else {
        	return null;
        }
    }

    protected IdentityBean hookGetSelectItemsIdentityBean() {
        return getIdentityBean();
    }
    
    @SuppressWarnings("unchecked")
	protected Map hookGetSelectItemsParams(String key){
    	return new HashMap(0);
    }

    protected boolean hasChooseOption(Object key) {
        return true;
    }

    protected List hookPostSelectItems(Object key, List hookItems) {
        return hookItems;
    }

    protected String hookGetPermItem(String key) {
        return key;
    }
}
