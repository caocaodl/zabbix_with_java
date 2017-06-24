package com.isoft.web.common;

import com.isoft.biz.method.Role;
import com.isoft.consts.Constant;
import com.isoft.framework.common.IdentityBean;

public class BasePageAction extends GenericAction {

	protected final static String JSON = "json";
	
    
    @Override
    protected IdentityBean getIdentityBean() {
    	IdentityBean idBean = null;
        if(getRequest() == null){
        	idBean = (IdentityBean)getSession().getAttribute(Constant.ATTR_ID_BEAN);
            if (idBean == null) {
                idBean = new IdentityBean();
                getSession().setAttribute(Constant.ATTR_ID_BEAN, idBean);
            }
        }else{
            idBean = (IdentityBean)getSession().getAttribute(Constant.ATTR_ID_BEAN);
            if (idBean == null) {
                idBean = new IdentityBean();
                getSession().setAttribute(Constant.ATTR_ID_BEAN, idBean);
            }
        }
        return idBean;
    }
    
    protected void clearIdentityBean() {
        getSession().removeAttribute(Constant.ATTR_ID_BEAN);
    }
    
    public boolean isLoginIn(){
        IdentityBean idBean = getIdentityBean();
        if(idBean == null){
            return false;
        }
        String userId = idBean.getUserId();
        return userId!=null && userId.length()>0;
    }
    
    public int getRole(){
    	return getIdentityBean().getTenantRole().magic();
    }

	@Override
	protected Object hookGetSelectItemsKey(Object key) {
		if("ORDER_STATUS".equals(key)){
			if(Role.isTenant(getRole())){
				return "TENANT_ORDER_STATUS";
			}
			if(Role.isLessor(getRole())){
				return "LESSOR_ORDER_STATUS";
			}
		}
		return key;
	}
    
}