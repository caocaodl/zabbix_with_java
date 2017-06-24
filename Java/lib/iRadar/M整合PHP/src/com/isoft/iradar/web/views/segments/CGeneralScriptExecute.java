package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._s;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CGeneralScriptExecute extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget scriptWidget = new CWidget();
		CForm scriptForm = new CForm();
		CTabView scriptTab = new CTabView();
		scriptTab.addTab("scriptTab", _s(
			"Result of \"%s\"", Nest.value(data,"info","name").asString()),
			new CSpan(Nest.value(data,"message").asString(), "pre fixedfont")
		);
		scriptForm.addItem(scriptTab);
		scriptWidget.addItem(scriptForm);
		return scriptWidget;
	}

}
