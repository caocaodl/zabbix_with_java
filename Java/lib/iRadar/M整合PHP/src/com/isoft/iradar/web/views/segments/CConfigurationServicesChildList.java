package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.ServicesUtil.serviceAlgorythm;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationServicesChildList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.services.child.list.js");

		CWidget servicesChildWidget = new CWidget();
		servicesChildWidget.addPageHeader(_("IT service dependencies"));

		// create form
		CForm servicesChildForm = new CForm();
		servicesChildForm.setName("servicesForm");
		if (!empty(Nest.value(data,"service").$())) {
			servicesChildForm.addVar("serviceid", Nest.value(data,"service","serviceid").$());
		}

		// create table
		CTableInfo servicesChildTable = new CTableInfo(_("No IT services found."));
		servicesChildTable.setHeader(array(
			new CCheckBox("all_services", false, "javascript: checkAll(\""+servicesChildForm.getName()+"\", \"all_services\", \"services\");"),
			_("Service"),
			_("Status calculation"),
			_("Trigger")
		));

		Object prefix = null;
		for(Map service : (CArray<Map>)Nest.value(data,"db_cservices").asCArray()) {
			CLink description = new CLink(Nest.value(service,"name").$(), "#", "service-name");
			description.setAttributes((Map)map(
				"id" , "service-name-"+Nest.value(service,"serviceid").$(),
				"data-name" , Nest.value(service,"name").$(),
				"data-serviceid" , Nest.value(service,"serviceid").$(),
				"data-trigger" , Nest.value(service,"trigger").$()
			));

			CCheckBox cb = new CCheckBox("services["+Nest.value(service,"serviceid").$()+"]", false, null, Nest.value(service,"serviceid").asInteger());
			cb.addClass("service-select");

			servicesChildTable.addRow(array(
				cb,
				array(prefix, description),
				serviceAlgorythm(Nest.value(service,"algorithm").asInteger()),
				Nest.value(service,"trigger").$())
			);
		}
		servicesChildTable.setFooter(new CCol(new CButton("select", _("Select")), "right"));

		// append table to form
		servicesChildForm.addItem(servicesChildTable);

		// append form to widget
		servicesChildWidget.addItem(servicesChildForm);
		return servicesChildWidget;
	}

}
