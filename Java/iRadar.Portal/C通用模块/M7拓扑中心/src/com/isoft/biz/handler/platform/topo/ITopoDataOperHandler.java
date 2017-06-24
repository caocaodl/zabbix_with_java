package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ITopoDataOperHandler extends LogicHandler {
	
	BLHMethod doTopoDataLocOperSave = new BLHMethod("doTopoDataLocOperSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doCabTopoCabinetDataLocSave = new BLHMethod("doCabTopoCabinetDataLocSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doCabTopoHostDataLocSave = new BLHMethod("doCabTopoHostDataLocSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doPhyTopoTbnailSave = new BLHMethod("doPhyTopoTbnailSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doPhyTopoTbnailDel = new BLHMethod("doPhyTopoTbnailDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doPhyTopoLocSave = new BLHMethod("doPhyTopoLocSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doBizTopoLocSave = new BLHMethod("doBizTopoLocSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
