package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.get_request;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;

public class CGeneralSearch extends CViewSegment {

	@Override
	public CDiv doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CForm searchForm = new CForm("get","search.action");

		CTextBox searchBox = new CTextBox("search", get_request("search"));
		searchBox.setAttribute("autocomplete", "off");
		searchBox.addClass("search");
		searchForm.addItem(searchBox);

		CSubmit searchBtn = new CSubmit("searchbttn", _("Search"), null, "input button ui-button ui-widget ui-state-default ui-corner-all");
		searchForm.addItem(searchBtn);

		return new CDiv(searchForm, "rda_search", "rda_search");
	}

}
