package com.isoft.iradar.tags.screens;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CHostsInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenHostsInfo extends CScreenBase {

	public CScreenHostsInfo(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenHostsInfo(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		return getOutput(new CHostsInfo(this.idBean, this.executor,Nest.value(this.screenitem,"resourceid").asLong(), Nest.value(this.screenitem,"style").asInteger()));
	}

}
