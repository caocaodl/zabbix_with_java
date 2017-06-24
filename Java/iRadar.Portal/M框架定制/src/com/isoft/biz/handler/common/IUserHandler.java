package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IUserHandler extends LogicHandler {
	
    BLHMethod doUserPage = new BLHMethod("doUserPage",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserView = new BLHMethod("doUserView",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserAdd = new BLHMethod("doUserAdd",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserEdit = new BLHMethod("doUserEdit",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserDel = new BLHMethod("doUserDel",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserActive = new BLHMethod("doUserActive",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserForbid = new BLHMethod("doUserForbid",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doUserResume = new BLHMethod("doUserResume",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
	BLHMethod getAllRoleSet = new BLHMethod(
			"getAllRoleSet", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod getRoles = new BLHMethod(
			"getRoles", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doUserGrantRoles = new BLHMethod(
			"doUserGrantRoles", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
    BLHMethod doUserViewByName = new BLHMethod("doUserViewByName",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doUserDisOrg = new BLHMethod(
			"doUserDisOrg", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

}
