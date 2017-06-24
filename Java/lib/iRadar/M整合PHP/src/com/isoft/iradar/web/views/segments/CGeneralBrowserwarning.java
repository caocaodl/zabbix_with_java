package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.inc.ViewsUtil.includeSubView;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;

public class CGeneralBrowserwarning extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("general.browserwarning");
		return null;
	}

}
