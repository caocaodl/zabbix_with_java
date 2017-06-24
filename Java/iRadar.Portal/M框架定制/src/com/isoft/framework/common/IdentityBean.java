package com.isoft.framework.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.method.Role;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iaas.openstack.keystone.model.Access.User;
import com.isoft.model.PermItem;

public class IdentityBean implements IIdentityBean {

	private static final long serialVersionUID = 1L;

	private String tenantId;//租户ID
	private String userId;// user id
	private String userAcc;// user account
	private String userName;// user name
	private User userDetail;// user detail
	private Map<String,Boolean> perms = new HashMap<String,Boolean>(0);
	private Map<String,Boolean> permFuncLabels = new HashMap<String,Boolean>(0);
	private Map<String,Boolean> permModuleIds = new HashMap<String,Boolean>(0);
	
	private Role tenantRole = Role.NONE;
	
	private boolean admin = false;
	
	public void init(Map user){
		this.tenantId = (String)user.get("tenantId");
		this.tenantRole = new Role((Integer)user.get("tenantRole"));
		this.userId = (String)user.get("userId");
		this.userName = (String)user.get("userName");
		this.admin = "Y".equals(user.get("admin"));
		this.userDetail = (User)user.get("osUser");
		List<PermItem> permSet = (List<PermItem>)user.remove("permSet");
		if (permSet != null && !permSet.isEmpty()) {
			String funcId = null;
			String moduleId = null;
			int step = 0;
			for (PermItem item : permSet) {
				perms.put(item.getId(), true);
				permFuncLabels.put(item.getLabel(), true);
				funcId = item.getFuncId();
				step = 0;
				while((step+=4)<funcId.length()){
					moduleId = funcId.substring(0,step);
					permModuleIds.put(moduleId, true);
				}
			}
		}
	}
	
	public User getUserDetail() {
		return userDetail;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getOsTenantId() {
		return tenantId;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserAcc() {
		return userAcc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Role getTenantRole() {
		return tenantRole;
	}

	public boolean isAdmin() {
		return admin;
	}

	public Map<String,Boolean> getPerms() {
		return perms;
	}

	public Map<String,Boolean> getPermFuncLabels() {
		return permFuncLabels;
	}
	
	public Map<String,Boolean> getPermModuleIds() {
		return permModuleIds;
	}

}
