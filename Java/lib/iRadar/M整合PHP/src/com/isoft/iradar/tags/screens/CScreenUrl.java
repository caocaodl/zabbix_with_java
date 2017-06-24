package com.isoft.iradar.tags.screens;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CIFrame;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenUrl extends CScreenBase {

	public CScreenUrl(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenUrl(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		return getOutput(new CIFrame(Nest.value(screenitem,"url").asString(), Nest.value(screenitem,"width").asString(), Nest.value(screenitem,"height").asString(), "auto"));
	}

}
