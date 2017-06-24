package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tree.CServiceTree;
import com.isoft.iradar.web.views.CViewSegment;

public class CConfigurationServicesList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.services.list.js");

		CWidget serviceWidget = new CWidget(null, "service-list");
		serviceWidget.addPageHeader(_("CONFIGURATION OF IT SERVICES"), SPACE);
		serviceWidget.addHeader(_("IT services"));

		// create form
		CForm serviceForm = new CForm();
		serviceForm.setName("serviceForm");

		serviceWidget.addItem(BR());
		serviceWidget.addItem(((CServiceTree)data.get("tree")).getHTML(idBean, executor));
		return serviceWidget;
	}

}
