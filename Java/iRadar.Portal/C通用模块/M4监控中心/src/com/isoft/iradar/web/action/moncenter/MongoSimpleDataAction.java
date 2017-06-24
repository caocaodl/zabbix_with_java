package com.isoft.iradar.web.action.moncenter;

public class MongoSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return MONGO_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return MONGO_LATEST_DATA_ACTION;
	}

}
