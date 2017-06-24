package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralMacrosEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CForm macrosForm = new CForm();
		macrosForm.setName("macrosForm");

		// tab
		CTabView macrosTab = new CTabView();

		CView macrosView = new CView("common.macros", map(
			"macros" , Nest.value(data,"macros").$()
		));
		macrosTab.addTab("macros", _("Macros"), macrosView.render(idBean, executor));

		CSubmit saveButton = new CSubmit("save", _("Save"));
		saveButton.attr("data-removed-count", 0);
		saveButton.addClass("main");

		macrosForm.addItem(macrosTab);
		macrosForm.addItem(makeFormFooter(null, array(saveButton)));

		return macrosForm;
	}

}
