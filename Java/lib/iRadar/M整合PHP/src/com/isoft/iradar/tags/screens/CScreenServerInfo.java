package com.isoft.iradar.tags.screens;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CServerInfo;
import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.7")
public class CScreenServerInfo extends CScreenBase {

	public CScreenServerInfo(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenServerInfo(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		return getOutput(new CServerInfo(this.idBean, this.executor));
	}

}
