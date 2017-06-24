package com.isoft.biz.handler.reportForms;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IReportFormshandler extends LogicHandler {
	BLHMethod doStatement = new BLHMethod("doStatement",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doEquipment = new BLHMethod("doEquipment",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doEquipmentSingle = new BLHMethod("doEquipmentSingle",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doEquipmentSingleElse = new BLHMethod("doEquipmentSingleElse",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doOs = new BLHMethod("doOs",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doStatementOs = new BLHMethod("doStatementOs",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doStatementSingleOs = new BLHMethod("doStatementSingleOs",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doOsSingleElse = new BLHMethod("doOsSingleElse",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
