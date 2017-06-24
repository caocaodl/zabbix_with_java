package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._page;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.inc.BlocksUtil.make_system_status;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenSystemStatus extends CScreenBase {

	public CScreenSystemStatus(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenSystemStatus(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		Map page = _page();

		// rewrite page file
		Nest.value(page,"file").$(pageFile);

		CUIWidget item = new CUIWidget("hat_syssum", make_system_status(idBean, executor, map(
			"groupids", null,
			"hostids", null,
			"maintenance", null,
			"severity", null,
			"limit", null,
			"extAck", 0,
			"screenid", screenid
		)));
		item.setHeader(_("Status of iRadar"), SPACE);
		item.setFooter(_s("Updated: %s", rda_date2str(_("H:i:s"))));

		return getOutput(item);
	}

}
