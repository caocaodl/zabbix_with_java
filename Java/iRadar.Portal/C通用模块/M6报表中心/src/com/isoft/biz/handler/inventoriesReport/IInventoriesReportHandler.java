package com.isoft.biz.handler.inventoriesReport;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IInventoriesReportHandler extends LogicHandler {

	BLHMethod doInventoriesReportPage = new BLHMethod("doInventoriesReportPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doInventoriesCSV = new BLHMethod("doInventoriesCSV",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
