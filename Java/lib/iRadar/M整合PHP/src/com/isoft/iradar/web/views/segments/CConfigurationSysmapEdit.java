package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_BOTH;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_UNACK;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_CUSTOM;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.MapsUtil;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSeverity;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationSysmapEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.sysmap.edit.js");

		CWidget sysmapWidget = new CWidget();
		sysmapWidget.addPageHeader(_("CONFIGURATION OF NETWORK MAPS"));

		// create sysmap form
		CForm sysmapForm = new CForm();
		sysmapForm.setName("map.edit.action");
		sysmapForm.addVar("form", get_request("form", "1"));
		sysmapForm.addVar("form_refresh", get_request("form_refresh", 0) + 1);
		if (isset(Nest.value(data,"sysmap","sysmapid").$())) {
			sysmapForm.addVar("sysmapid", Nest.value(data,"sysmap","sysmapid").$());
		}

		// create sysmap form list
		CFormList sysmapList = new CFormList("sysmaplist");

		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"sysmap","name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		sysmapList.addRow(_("Name"), nameTextBox);
		sysmapList.addRow(_("Width"), new CNumericBox("width", Nest.value(data,"sysmap","width").asString(), 5));
		sysmapList.addRow(_("Height"), new CNumericBox("height", Nest.value(data,"sysmap","height").asString(), 5));

		// append background image to form list
		CComboBox imageComboBox = new CComboBox("backgroundid", Nest.value(data,"sysmap","backgroundid").$());
		imageComboBox.addItem(0, _("No image"));
		for(Map image : (CArray<Map>)Nest.value(data,"images").asCArray()) {
			imageComboBox.addItem(Nest.value(image,"imageid").$(), Nest.value(image,"name").asString());
		}
		sysmapList.addRow(_("Background image"), imageComboBox);

		// append iconmapping to form list
		CComboBox iconMappingComboBox = new CComboBox("iconmapid", Nest.value(data,"sysmap","iconmapid").$());
		iconMappingComboBox.addItem(0, _("<manual>"));
		for(Map iconMap : (CArray<Map>)Nest.value(data,"iconMaps").asCArray()) {
			iconMappingComboBox.addItem(Nest.value(iconMap,"iconmapid").$(), Nest.value(iconMap,"name").asString());
		}
		CLink iconMappingsLink = new CLink(_("show icon mappings"), "adm.iconmapping.action");
		iconMappingsLink.setAttribute("target", "_blank");
		sysmapList.addRow(_("Automatic icon mapping"), array(iconMappingComboBox, SPACE, iconMappingsLink));

		// append multiple checkboxes to form list
		sysmapList.addRow(_("Icon highlight"), new CCheckBox("highlight", Nest.value(data,"sysmap","highlight").asBoolean(), null, 1));
		sysmapList.addRow(_("Mark elements on trigger status change"), new CCheckBox("markelements", Nest.value(data,"sysmap","markelements").asBoolean(), null, 1));
		sysmapList.addRow(_("Expand single problem"), new CCheckBox("expandproblem", Nest.value(data,"sysmap","expandproblem").asBoolean(), null, 1));
		sysmapList.addRow(_("Advanced labels"), new CCheckBox("label_format", Nest.value(data,"sysmap","label_format").asBoolean(), null, 1));

		// append hostgroup to form list
		CComboBox labelTypeHostgroupComboBox = new CComboBox("label_type_hostgroup", Nest.value(data,"sysmap","label_type_hostgroup").$(), null, Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea customLabelHostgroupTextArea = new CTextArea("label_string_hostgroup", Nest.value(data,"sysmap","label_string_hostgroup").asString());
		if (Nest.value(data,"sysmap","label_type_hostgroup").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			customLabelHostgroupTextArea.addClass("hidden");
		}
		sysmapList.addRow(_("Host group label type"), array(labelTypeHostgroupComboBox, BR(), customLabelHostgroupTextArea));

		// append host to form list
		CComboBox labelTypeHostComboBox = new CComboBox("label_type_host", Nest.value(data,"sysmap","label_type_host").$(), null, Nest.value(data,"labelTypes").asCArray());
		CTextArea customLabelHostTextArea = new CTextArea("label_string_host", Nest.value(data,"sysmap","label_string_host").asString());
		if (Nest.value(data,"sysmap","label_type_host").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			customLabelHostTextArea.addClass("hidden");
		}
		sysmapList.addRow(_("Host label type"), array(labelTypeHostComboBox, BR(), customLabelHostTextArea));

		// append trigger to form list
		CComboBox labelTypeTriggerComboBox = new CComboBox("label_type_trigger", Nest.value(data,"sysmap","label_type_trigger").$(), null, Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea customLabelTriggerTextArea = new CTextArea("label_string_trigger", Nest.value(data,"sysmap","label_string_trigger").asString());
		if (Nest.value(data,"sysmap","label_type_trigger").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			customLabelTriggerTextArea.addClass("hidden");
		}
		sysmapList.addRow(_("Trigger label type"), array(labelTypeTriggerComboBox, BR(), customLabelTriggerTextArea));

		// append map to form list
		CComboBox labelTypeMapComboBox = new CComboBox("label_type_map", Nest.value(data,"sysmap","label_type_map").$(), null, Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea customLabelMapTextArea = new CTextArea("label_string_map", Nest.value(data,"sysmap","label_string_map").asString());
		if (Nest.value(data,"sysmap","label_type_map").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			customLabelMapTextArea.addClass("hidden");
		}
		sysmapList.addRow(_("Map label type"), array(labelTypeMapComboBox, BR(), customLabelMapTextArea));

		// append image to form list
		CComboBox labelTypeImageComboBox = new CComboBox("label_type_image", Nest.value(data,"sysmap","label_type_image").$(), null, Nest.value(data,"labelTypesImage").asCArray());
		CTextArea customLabelImageTextArea = new CTextArea("label_string_image", Nest.value(data,"sysmap","label_string_image").asString());
		if (Nest.value(data,"sysmap","label_type_image").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			customLabelImageTextArea.addClass("hidden");
		}
		sysmapList.addRow(_("Image label type"), array(labelTypeImageComboBox, BR(), customLabelImageTextArea));

		// append icon label to form list
		unset(Nest.value(data,"labelTypes").asCArray(),MAP_LABEL_TYPE_CUSTOM);
		sysmapList.addRow(_("Icon label type"), new CComboBox("label_type", Nest.value(data,"sysmap","label_type").$(), null, Nest.value(data,"labelTypes").asCArray()));

		// append icon label location to form list
		CComboBox locationComboBox = new CComboBox("label_location", Nest.value(data,"sysmap","label_location").$());
		locationComboBox.addItems((CArray)map(0, _("Bottom"), 1, _("Left"), 2, _("Right"), 3, _("Top")));
		sysmapList.addRow(_("Icon label location"), locationComboBox);

		// append show unack to form list
		CComboBox showUnackComboBox = new CComboBox("show_unack", Nest.value(data,"sysmap","show_unack").$());
		showUnackComboBox.addItems((CArray)map(
			EXTACK_OPTION_ALL, _("All"),
			EXTACK_OPTION_BOTH, _("Separated"),
			EXTACK_OPTION_UNACK, _("Unacknowledged only")
		));
		showUnackComboBox.setEnabled(Nest.value(data,"config","event_ack_enable").asBoolean());
		if (!Nest.value(data,"config","event_ack_enable").asBoolean()) {
			showUnackComboBox.setAttribute("title", _("Acknowledging disabled"));
		}
		sysmapList.addRow(_("Problem display"), showUnackComboBox);

		// append severity min to form list
		sysmapList.addRow(_("Minimum trigger severity"), new CSeverity(idBean, executor, map("name", "severity_min", "value", Nest.value(data,"sysmap","severity_min").$())));

		// create url table
		CTable urlTable = new CTable(_("No URLs defined."), "formElementTable");
		urlTable.setAttribute("style", "min-width: 500px;");
		urlTable.setHeader(array(_("Name"), _("URL"), _("Element"), SPACE));
		if (empty(Nest.value(data,"sysmap","urls").$())) {
			Nest.value(data,"sysmap","urls").asCArray().add(map("name", "", "url", "", "elementtype", 0));
		}
		int i = 0;
		for(Map url : (CArray<Map>)Nest.value(data,"sysmap","urls").asCArray()) {
			CTextBox urlLabel = new CTextBox("urls["+i+"][name]", Nest.value(url,"name").asString(), 32);
			CTextBox urlLink = new CTextBox("urls["+i+"][url]", Nest.value(url,"url").asString(), 32);
			CComboBox urlEtype = new CComboBox("urls["+i+"][elementtype]", Nest.value(url,"elementtype").$());
			urlEtype.addItems(MapsUtil.sysmap_element_types());
			CSpan removeButton = new CSpan(_("Remove"), "link_menu");
			removeButton.addAction("onclick", "$(\"urlEntry_"+i+"\").remove();");

			CRow urlRow = new CRow(array(urlLabel, urlLink, urlEtype, removeButton));
			urlRow.setAttribute("id", "urlEntry_"+i);

			urlTable.addRow(urlRow);
			i++;
		}

		// append empty template row to url table
		CTextBox templateUrlLabel = new CTextBox("urls[#{id}][name]", "", 32);
		templateUrlLabel.setAttribute("disabled", "disabled");
		CTextBox templateUrlLink = new CTextBox("urls[#{id}][url]", "", 32);
		templateUrlLink.setAttribute("disabled", "disabled");
		CComboBox templateUrlEtype = new CComboBox("urls[#{id}][elementtype]");
		templateUrlEtype.setAttribute("disabled", "disabled");
		templateUrlEtype.addItems(MapsUtil.sysmap_element_types());
		CSpan templateRemoveButton = new CSpan(_("Remove"), "link_menu");
		templateRemoveButton.addAction("onclick", "$(\"entry_#{id}\").remove();");
		CRow templateUrlRow = new CRow(array(templateUrlLabel, templateUrlLink, templateUrlEtype, templateRemoveButton));
		templateUrlRow.addStyle("display: none");
		templateUrlRow.setAttribute("id", "urlEntryTpl");
		urlTable.addRow(templateUrlRow);

		// append \"add\" button to url table
		CSpan addButton = new CSpan(_("Add"), "link_menu");
		addButton.addAction("onclick", "cloneRow(\"urlEntryTpl\", "+i+")");
		CCol addButtonColumn = new CCol(addButton);
		addButtonColumn.setColSpan(4);
		urlTable.addRow(addButtonColumn);

		// append url table to form list
		sysmapList.addRow(_("URLs"), new CDiv(urlTable, "objectgroup inlineblock border_dotted ui-corner-all"));

		// append sysmap to form
		CTabView sysmapTab = new CTabView();
		sysmapTab.addTab("sysmapTab", _("Map"), sysmapList);
		sysmapForm.addItem(sysmapTab);

		// append buttons to form
		CArray others = array();
		if (isset(_REQUEST,"sysmapid") && Nest.value(_REQUEST,"sysmapid").asInteger() > 0) {
			others.add(new CButton("clone", _("Clone")));
			others.add(new CButtonDelete(_("Delete network map?"), url_param(idBean, "form")+url_param(idBean, "sysmapid")));
		}
		others.add(new CButtonCancel());

		sysmapForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), others));

		// append form to widget
		sysmapWidget.addItem(sysmapForm);

		return sysmapWidget;
	}

}
