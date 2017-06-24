package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ServicesUtil.serviceAlgorythm;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationServicesParentList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.services.edit.js");

		CWidget servicesParentWidget = new CWidget();
		servicesParentWidget.addPageHeader(_("IT service parent"));

		// create form
		CForm servicesParentForm = new CForm();
		servicesParentForm.setName("servicesForm");
		if (!empty(Nest.value(data,"service").$())) {
			servicesParentForm.addVar("serviceid", Nest.value(data,"service","serviceid").$());
		}

		// create table
		CTableInfo servicesParentTable = new CTableInfo();
		servicesParentTable.setHeader(array(_("Service"), _("Status calculation"), _("Trigger")));

		Object prefix = null;

		// root
		Object description = new CLink(_("root"), "#", null, "javascript:"+
			"jQuery('#parent_name', window.opener.document).val("+rda_jsvalue(_("root"))+");"+
			"jQuery('#parentname', window.opener.document).val("+rda_jsvalue(_("root"))+");"+
			"jQuery('#parentid', window.opener.document).val("+rda_jsvalue(0)+");"+
			"self.close();"+
			"return false;"
		);
		servicesParentTable.addRow(array(array(prefix, description), _("Note"), "-"));

		// others
		for(Map db_service : (CArray<Map>)Nest.value(data,"db_pservices").$()) {
			description = new CSpan(Nest.value(db_service,"name").$(), "link");
			((CSpan)description).setAttribute("onclick", "javascript:"+
				"jQuery('#parent_name', window.opener.document).val("+rda_jsvalue(Nest.value(db_service,"name").$())+");"+
				"jQuery('#parentname', window.opener.document).val("+rda_jsvalue(Nest.value(db_service,"name").$())+");"+
				"jQuery('#parentid', window.opener.document).val("+rda_jsvalue(Nest.value(db_service,"serviceid").$())+");"+
				"self.close();"+
				"return false;"
			);
			servicesParentTable.addRow(array(array(prefix, description), serviceAlgorythm(Nest.value(db_service,"algorithm").asInteger()), Nest.value(db_service,"trigger").$()));
		}
		CCol column = new CCol(new CButton("cancel", _("Cancel"), "javascript: self.close();"));
		column.setAttribute("style", "text-align:right;");
		servicesParentTable.setFooter(column);

		// append table to form
		servicesParentForm.addItem(servicesParentTable);

		// append form to widget
		servicesParentWidget.addItem(servicesParentForm);
		return servicesParentWidget;
	}

}
