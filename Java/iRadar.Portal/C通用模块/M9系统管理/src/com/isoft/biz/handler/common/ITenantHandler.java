package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ITenantHandler extends LogicHandler {
	
    BLHMethod doTenantPage = new BLHMethod("doTenantPage",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantAdd = new BLHMethod("doTenantAdd",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantEdit = new BLHMethod("doTenantEdit",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantDel = new BLHMethod("doTenantDel",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantActive = new BLHMethod("doTenantActive",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantForbid = new BLHMethod("doTenantForbid",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantResume = new BLHMethod("doTenantResume",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doTenantRelease = new BLHMethod("doTenantRelease",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doOSTenantIdViewByTenantId = new BLHMethod("doOSTenantIdViewByTenantId",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    
}
