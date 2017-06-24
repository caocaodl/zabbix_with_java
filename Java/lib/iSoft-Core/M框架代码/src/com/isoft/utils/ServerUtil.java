package com.isoft.utils;

import javax.servlet.http.HttpServletRequest;

import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;
import com.isoft.framework.common.IdentityBean;

public class ServerUtil {
    
    public static boolean checkSessionTimeOut(HttpServletRequest httpRequest){
        return checkSessionTimeOut(httpRequest,null);
    }
    
    public static boolean checkSessionTimeOut(HttpServletRequest httpRequest,Role roleMask){
    	return false;
//        String requestSessionId = httpRequest.getRequestedSessionId();
//        HttpSession session = httpRequest.getSession(true);
//        String currSessionId = session.getId();
//        if (requestSessionId != null && !currSessionId.equals(requestSessionId)) {
//            return true;
//        }
//        IdentityBean idBean = (IdentityBean)session.getAttribute(Constant.ATTR_ID_BEAN);
//        if(idBean == null){
//            return true;
//        }
//        String userId = idBean.getUserId();
//        if(userId==null || userId.length()==0){
//            return true;
//        }
//        if(roleMask==null){
//            return false;
//        }else{
//            return !Role.isPassable(idBean.getRole(), roleMask);
//        }
    }
    
    public static boolean checkAuthorization(IdentityBean identityBean,FuncIdEnum[] funcId) {
    	return true;
//        if (funcId == null || funcId.length == 0) {
//            return false;
//        }
//        if (identityBean.isAdmin()) {
//            return true;
//        }
//        if (funcId.length == 1 && FuncIdEnum.DEFAULT_FUNID.equals(funcId[0])) {
//            return true;
//        }
//
//        for(FuncIdEnum f:funcId){
//            if(Boolean.TRUE.equals(identityBean.getUserInfo().get(f.magic()))){
//                return true;
//            }
//        }
//        return false;
    }
    
}
