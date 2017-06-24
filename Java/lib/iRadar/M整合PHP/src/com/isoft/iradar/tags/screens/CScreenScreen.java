package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenScreen extends CScreenBase {

	public CScreenScreen(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenScreen(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		CScreenGet soptions = new CScreenGet();
		soptions.setScreenIds(Nest.value(screenitem,"resourceid").asLong());
		soptions.setOutput(API_OUTPUT_EXTEND);
		soptions.setSelectScreenItems(API_OUTPUT_EXTEND);
		CArray<Map> screens = API.Screen(this.idBean, executor).get(soptions);
		Map screen = reset(screens);

		CScreenBuilder screenBuilder = new CScreenBuilder(this.idBean, this.executor, map(
			"isFlickerfree", this.isFlickerfree,
			"mode", (this.mode == SCREEN_MODE_EDIT || this.mode == SCREEN_MODE_SLIDESHOW) ? SCREEN_MODE_SLIDESHOW : SCREEN_MODE_PREVIEW,
			"timestamp", this.timestamp,
			"screen", screen,
			"period", Nest.value(timeline,"period").$(),
			"stime", Nest.value(timeline,"stimeNow").$(),
			"profileIdx", this.profileIdx,
			"updateProfile", false
		));

		return getOutput(screenBuilder.show(idBean), true);
	}

}
