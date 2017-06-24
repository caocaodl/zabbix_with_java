package com.isoft.iradar.web.action.moncenter;

public class MongoLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return MONGO_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return MONGO_LATEST_DATA_ACTION;
	}
	
}
