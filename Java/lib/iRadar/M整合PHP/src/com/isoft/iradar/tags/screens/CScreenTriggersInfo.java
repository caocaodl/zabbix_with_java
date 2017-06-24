package com.isoft.iradar.tags.screens;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CTriggersInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenTriggersInfo extends CScreenBase {

	public CScreenTriggersInfo(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenTriggersInfo(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		return getOutput(new CTriggersInfo(this.idBean, this.executor,Nest.value(this.screenitem,"resourceid").asLong(), null, Nest.value(this.screenitem,"style").asInteger()));
	}

}
