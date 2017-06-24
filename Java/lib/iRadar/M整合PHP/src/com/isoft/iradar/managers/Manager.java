package com.isoft.iradar.managers;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.lang.CodeConfirmed;

/**
 * A class for creating a storing instances of DB objects managers.
 */
@CodeConfirmed("blue.2.2.5")
public class Manager extends CFactoryRegistry {

	public Manager() {
		super();
	}

	@CodeConfirmed("blue.2.2.5")
	public static Manager getInstance() {
		Class<? extends Manager> clazz = Manager.class;
		return (Manager)CFactoryRegistry.getInstance(clazz);
	}

	/**
	 * @return CApplicationManager
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CAppManager Application(IIdentityBean idBean, SQLExecutor executor) {
		return getInstance().getObject(CAppManager.class, idBean, executor);
	}

	/**
	 * @return CHistoryManager
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CHistoryManager History(IIdentityBean idBean, SQLExecutor executor) {
		return getInstance().getObject(CHistoryManager.class, idBean, executor);
	}

	/**
	 * @return CHttpTestManager
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CHttpTestManager HttpTest(IIdentityBean idBean, SQLExecutor executor) {
		return getInstance().getObject(CHttpTestManager.class, idBean, executor);
	}
}
