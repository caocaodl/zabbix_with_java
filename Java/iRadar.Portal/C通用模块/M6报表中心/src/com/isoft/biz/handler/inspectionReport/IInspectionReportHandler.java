package com.isoft.biz.handler.inspectionReport;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IInspectionReportHandler extends LogicHandler {

	BLHMethod doPage = new BLHMethod("doPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doAdd = new BLHMethod("doAdd",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doUpdate = new BLHMethod("doUpdate",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doUpdateStatus = new BLHMethod("doUpdateStatus",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doCheckName = new BLHMethod("doCheckName",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
	BLHMethod doHostApplication = new BLHMethod("doHostApplication",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
	BLHMethod doInspectionHistoryPage = new BLHMethod("doInspectionHistoryPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
	BLHMethod doInspectionHistoryInfo = new BLHMethod("doInspectionHistoryInfo",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
	BLHMethod doInspectionTimeRuleList = new BLHMethod("doInspectionTimeRuleList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
}
