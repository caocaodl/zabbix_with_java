package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_BOTH;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_UNACK;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_BACKGROUND;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_CUSTOM;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_IP;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_LABEL;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_STATUS;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CIconMapGet;
import com.isoft.iradar.model.params.CImageGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
//TODO
public class SysmapsAction extends RadarBaseAction {
	
	boolean _isExportData = false;
	CArray<Map> _sysmap;
	
	@Override
	protected void doInitPage() {

		if (isset(Nest.value(_REQUEST,"go").$()) && Nest.value(_REQUEST,"go").$() == "export" && isset(Nest.value(_REQUEST,"maps").$())) {
			Nest.value(page,"file").$("rda_export_maps.xml");
			Nest.value(page,"type").$(detect_page_type(PAGE_TYPE_XML));

			_isExportData = true;
		}else {

			page("title", _("Configuration of network maps"));
			page("file", "sysmaps.action");
			page("hist_arg", new String[] {});
			page("type", detect_page_type(PAGE_TYPE_HTML));
			
			_isExportData = false;
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"maps" ,					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"sysmapid" ,				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"name" ,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Name")),
			"width" ,					array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("Width")),
			"height" ,					array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("Height")),
			"backgroundid" ,			array(T_RDA_INT, O_OPT, null,	DB_ID,			"isset({save})"),
			"iconmapid" ,				array(T_RDA_INT, O_OPT, null,	DB_ID,			"isset({save})"),
			"expandproblem" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 1),	null),
			"markelements" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 1),	null),
			"show_unack" ,				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 2),	null),
			"highlight" ,				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 1),	null),
			"label_format" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 1),	null),
			"label_type_host" ,		array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_type_hostgroup" ,	array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_type_trigger" ,		array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_type_map" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_type_image" ,		array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_string_host" ,		array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"label_string_hostgroup" ,	array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"label_string_trigger" ,	array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"label_string_map" ,		array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"label_string_image" ,		array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"label_type" ,				array(T_RDA_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL,MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
			"label_location" ,			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 3),	"isset({save})"),
			"urls" ,					array(T_RDA_STR, O_OPT, null,	null,			null),
			"severity_min" ,			array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4,5"), null),
			// actions
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"delete" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"cancel" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"go" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			// form
			"form" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"form_refresh" ,			array(T_RDA_INT, O_OPT, null,	null,			null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		
		/** Permissions */
		if (isset(_REQUEST,"sysmapid")) {
			CMapGet option = new CMapGet();
			option.setSysmapIds(Nest.array(_REQUEST,"sysmapid").asLong());
			option.setEditable(true);
			option.setOutput(API_OUTPUT_EXTEND);
			option.setSelectUrls(API_OUTPUT_EXTEND);
			_sysmap = API.Map(getIdentityBean(), executor).get(option);
			if (empty(_sysmap)) {
				access_deny();
			}
			else {
				_sysmap = array(reset(_sysmap));
			}
		}else {
			_sysmap = array();
		}

		if (_isExportData) {
			//sjh  CConfigurationExport不存在    TODO
/*			_export = new CConfigurationExport(map("maps" , get_request("maps", array())));
			_export.setBuilder(new CConfigurationExportBuilder());
			_export.setWriter(CExportWriterFactory::getWriter(CExportWriterFactory::XML));
			_exportData = _export.export();

			if (hasErrorMesssages()) {
				show_messages();
			}
			else {
				echo _exportData;
			}*/
		}

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));

	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/*
		 * Actions
		 */
		//sjh  TODO
/*		if (isset(Nest.value(_REQUEST,"save").$())) {
			_map = array(
				"name" => Nest.value($_REQUEST,"name").$(),
				"width" => Nest.value($_REQUEST,"width").$(),
				"height" => Nest.value($_REQUEST,"height").$(),
				"backgroundid" => Nest.value($_REQUEST,"backgroundid").$(),
				"iconmapid" => Nest.value($_REQUEST,"iconmapid").$(),
				"highlight" => get_request("highlight", 0),
				"markelements" => get_request("markelements", 0),
				"expandproblem" => get_request("expandproblem", 0),
				"label_format" => get_request("label_format", 0),
				"label_type_host" => get_request("label_type_host", 2),
				"label_type_hostgroup" => get_request("label_type_hostgroup", 2),
				"label_type_trigger" => get_request("label_type_trigger", 2),
				"label_type_map" => get_request("label_type_map", 2),
				"label_type_image" => get_request("label_type_image", 2),
				"label_string_host" => get_request("label_string_host", ""),
				"label_string_hostgroup" => get_request("label_string_hostgroup", ""),
				"label_string_trigger" => get_request("label_string_trigger", ""),
				"label_string_map" => get_request("label_string_map", ""),
				"label_string_image" => get_request("label_string_image", ""),
				"label_type" => Nest.value($_REQUEST,"label_type").$(),
				"label_location" => Nest.value($_REQUEST,"label_location").$(),
				"show_unack" => get_request("show_unack", 0),
				"severity_min" => get_request("severity_min", TRIGGER_SEVERITY_NOT_CLASSIFIED),
				"urls" => get_request("urls", array())
			);

			for(Nest.value(_map,"urls").$() as _unum => _url) {
				if (rda_empty(Nest.value(_url,"name").$()) && rda_empty(Nest.value(_url,"url").$())) {
					unset(_map["urls"][_unum]);
				}
			}

			if (isset(Nest.value($_REQUEST,"sysmapid").$())) {
				// TODO check permission by new value.
				Nest.value(_map,"sysmapid").$() = Nest.value($_REQUEST,"sysmapid").$();
				_result = API::Map().update(_map);

				_auditAction = AUDIT_ACTION_UPDATE;
				show_messages(_result, _("Network map updated"), _("Cannot update network map"));
			}
			else {
				_result = API::Map().create(_map);

				_auditAction = AUDIT_ACTION_ADD;
				show_messages(_result, _("Network map added"), _("Cannot add network map"));
			}

			if (_result) {
				add_audit(_auditAction, AUDIT_RESOURCE_MAP, "Name [".$_REQUEST["name"]."]");
				unset(Nest.value($_REQUEST,"form").$());
				clearCookies(_result);
			}
		}
		elseif ((isset(Nest.value($_REQUEST,"delete").$()) && isset(Nest.value($_REQUEST,"sysmapid").$())) || Nest.value($_REQUEST,"go").$() == "delete") {
			_sysmapIds = get_request("maps", array());

			if (isset(Nest.value($_REQUEST,"sysmapid").$())) {
				_sysmapIds[] = Nest.value($_REQUEST,"sysmapid").$();
			}

			DBstart();

			_maps = API::Map().get(array(
				"sysmapids" => _sysmapIds,
				"output" => array("sysmapid", "name"),
				"editable" => true
			));

			_result = API::Map().delete(_sysmapIds);

			if (_result) {
				unset(Nest.value($_REQUEST,"form").$());

				for(_maps as _map) {
					add_audit_ext(AUDIT_ACTION_DELETE, AUDIT_RESOURCE_MAP, Nest.value(_map,"sysmapid").$(), Nest.value(_map,"name").$(), null, null, null);
				}
			}

			_result = DBend(_result);

			show_messages(_result, _("Network map deleted"), _("Cannot delete network map"));
			clearCookies(_result);
		}
*/
		/*
		 * Display
		 */
		if (isset(Nest.value(_REQUEST,"form").$())) {
			doRenderFormView(executor);
		}else {
			doRenderListView(executor);
		}
	}
	
	public void doRenderFormView(SQLExecutor executor){
		CArray _data;
		if (!isset(Nest.value(_REQUEST,"sysmapid").$()) || isset(Nest.value(_REQUEST,"form_refresh").$())) {
			_data = map(
				"sysmap" , map(
					"sysmapid" , get_request("sysmapid"),
					"name" , get_request("name", ""),
					"width" , get_request("width", 800),
					"height" , get_request("height", 600),
					"backgroundid" , get_request("backgroundid", 0),
					"iconmapid" , get_request("iconmapid", 0),
					"label_format" , get_request("label_format", 0),
					"label_type_host" , get_request("label_type_host", 2),
					"label_type_hostgroup" , get_request("label_type_hostgroup", 2),
					"label_type_trigger" , get_request("label_type_trigger", 2),
					"label_type_map" , get_request("label_type_map", 2),
					"label_type_image" , get_request("label_type_image", 2),
					"label_string_host" , get_request("label_string_host", ""),
					"label_string_hostgroup" , get_request("label_string_hostgroup", ""),
					"label_string_trigger" , get_request("label_string_trigger", ""),
					"label_string_map" , get_request("label_string_map", ""),
					"label_string_image" , get_request("label_string_image", ""),
					"label_type" , get_request("label_type", 0),
					"label_location" , get_request("label_location", 0),
					"highlight" , get_request("highlight", 0),
					"markelements" , get_request("markelements", 0),
					"expandproblem" , get_request("expandproblem", 0),
					"show_unack" , get_request("show_unack", 0),
					"severity_min" , get_request("severity_min", TRIGGER_SEVERITY_NOT_CLASSIFIED),
					"urls" , get_request("urls", array())
				)
			);
		}else {
			_data = map("sysmap" , _sysmap);
		}

		// config
		Nest.value(_data,"config").$(select_config(getIdentityBean(), executor));

		// advanced labels
		//sjh  sysmapElementLabel方法不存在    TODO
//		Nest.value(_data,"labelTypes").$(sysmapElementLabel());
		Nest.value(_data,"labelTypesLimited").$(Nest.value(_data,"labelTypes").$());
		unset(_data,"labelTypesLimited",MAP_LABEL_TYPE_IP);
		Nest.value(_data,"labelTypesImage").$(Nest.value(_data,"labelTypesLimited").$());
		unset(_data,"labelTypesImage",MAP_LABEL_TYPE_STATUS);

		// images
		CImageGet option = new CImageGet();
		option.setOutput(new String[]{"imageid", "name"});
		option.setFilter("imagetype", String.valueOf(IMAGE_TYPE_BACKGROUND));
		Nest.value(_data,"images").$(API.Image(getIdentityBean(), executor).get(option));
		order_result(Nest.value(_data,"images").asCArray(), "name");

		CArray<Map> images = Nest.value(_data,"images").asCArray();
		for (Entry<Object, Map> e : images.entrySet()) {
            Object _num = e.getKey();
            Map _image = e.getValue();
			Nest.value(_data,"images",_num,"name").$(Nest.value(_image,"name").$());
		}

		// icon maps
		CIconMapGet option1 = new CIconMapGet();
		option1.setOutput(new String[]{"iconmapid", "name"});
		option1.setPreserveKeys(true);
		Nest.value(_data,"iconMaps").$(API.IconMap(getIdentityBean(), executor).get(option1));
		order_result(Nest.value(_data,"iconMaps").asCArray(), "name");

		// render view
		doRenderFormDisplay(_data);
	}
	
	public void doRenderFormDisplay(CArray data){

		includeSubView("js/configuration.sysmap.edit.js");
		
		CWidget _sysmapWidget = new CWidget();
		_sysmapWidget.addPageHeader(_("CONFIGURATION OF NETWORK MAPS"));

		// create sysmap form
		CForm _sysmapForm = new CForm();
		_sysmapForm.setName("map.edit.action");
		_sysmapForm.addVar("form", get_request("form", "1"));
		_sysmapForm.addVar("form_refresh", get_request("form_refresh", 0) + 1);
		if (isset(Nest.value(data,"sysmap","sysmapid").$())) {
			_sysmapForm.addVar("sysmapid", Nest.value(data,"sysmap","sysmapid").$());
		}

		// create sysmap form list
		CFormList _sysmapList = new CFormList("sysmaplist");

		CTextBox _nameTextBox = new CTextBox("name", Nest.value(data,"sysmap","name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		_nameTextBox.attr("autofocus", "autofocus");
		_sysmapList.addRow(_("Name"), _nameTextBox);
		_sysmapList.addRow(_("Width"), new CNumericBox("width", Nest.value(data,"sysmap","width").asString(), 5));
		_sysmapList.addRow(_("Height"), new CNumericBox("height", Nest.value(data,"sysmap","height").asString(), 5));

		// append background image to form list
		CComboBox _imageComboBox = new CComboBox("backgroundid", Nest.value(data,"sysmap","backgroundid").$());
		_imageComboBox.addItem(0, _("No image"));
		CArray<Map> images = Nest.value(data,"images").asCArray();
		for(Map _image:images) {
			_imageComboBox.addItem(Nest.value(_image,"imageid").$(), Nest.value(_image,"name").asString());
		}
		_sysmapList.addRow(_("Background image"), _imageComboBox);

		// append iconmapping to form list
		CComboBox _iconMappingComboBox = new CComboBox("iconmapid", Nest.value(data,"sysmap","iconmapid").$());
		_iconMappingComboBox.addItem(0, _("<manual>"));
		CArray<Map> iconMaps = Nest.value(data,"iconMaps").asCArray();
		for(Map _iconMap : iconMaps) {
			_iconMappingComboBox.addItem(Nest.value(_iconMap,"iconmapid").$(), Nest.value(_iconMap,"name").asString());
		}
		CLink _iconMappingsLink = new CLink(_("show icon mappings"), "adm.iconmapping.action");
		_iconMappingsLink.setAttribute("target", "_blank");
		_sysmapList.addRow(_("Automatic icon mapping"), array(_iconMappingComboBox, SPACE, _iconMappingsLink));

		// append multiple checkboxes to form list
		_sysmapList.addRow(_("Icon highlight"), new CCheckBox("highlight", Nest.value(data,"sysmap","highlight").asBoolean(), "", "1"));
		_sysmapList.addRow(_("Mark elements on trigger status change"), new CCheckBox("markelements", Nest.value(data,"sysmap","markelements").asBoolean(), "", "1"));
		_sysmapList.addRow(_("Expand single problem"), new CCheckBox("expandproblem", Nest.value(data,"sysmap","expandproblem").asBoolean(), "", "1"));
		_sysmapList.addRow(_("Advanced labels"), new CCheckBox("label_format", Nest.value(data,"sysmap","label_format").asBoolean(), "", "1"));

		// append hostgroup to form list
		CComboBox _labelTypeHostgroupComboBox = new CComboBox("label_type_hostgroup", Nest.value(data,"sysmap","label_type_hostgroup").$(), "", Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea _customLabelHostgroupTextArea = new CTextArea("label_string_hostgroup", Nest.value(data,"sysmap","label_string_hostgroup").asString());
		if (Nest.value(data,"sysmap","label_type_hostgroup").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			_customLabelHostgroupTextArea.addClass("hidden");
		}
		_sysmapList.addRow(_("Host group label type"), array(_labelTypeHostgroupComboBox, BR(), _customLabelHostgroupTextArea));

		// append host to form list
		CComboBox _labelTypeHostComboBox = new CComboBox("label_type_host", Nest.value(data,"sysmap","label_type_host").$(), "", Nest.value(data,"labelTypes").asCArray());
		CTextArea _customLabelHostTextArea = new CTextArea("label_string_host", Nest.value(data,"sysmap","label_string_host").asString());
		if (Nest.value(data,"sysmap","label_type_host").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			_customLabelHostTextArea.addClass("hidden");
		}
		_sysmapList.addRow(_("Host label type"), array(_labelTypeHostComboBox, BR(), _customLabelHostTextArea));

		// append trigger to form list
		CComboBox _labelTypeTriggerComboBox = new CComboBox("label_type_trigger", Nest.value(data,"sysmap","label_type_trigger").$(), "", Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea _customLabelTriggerTextArea = new CTextArea("label_string_trigger", Nest.value(data,"sysmap","label_string_trigger").asString());
		if (Nest.value(data,"sysmap","label_type_trigger").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			_customLabelTriggerTextArea.addClass("hidden");
		}
		_sysmapList.addRow(_("Trigger label type"), array(_labelTypeTriggerComboBox, BR(), _customLabelTriggerTextArea));

		// append map to form list
		CComboBox _labelTypeMapComboBox = new CComboBox("label_type_map", Nest.value(data,"sysmap","label_type_map").$(), "", Nest.value(data,"labelTypesLimited").asCArray());
		CTextArea _customLabelMapTextArea = new CTextArea("label_string_map", Nest.value(data,"sysmap","label_string_map").asString());
		if (Nest.value(data,"sysmap","label_type_map").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			_customLabelMapTextArea.addClass("hidden");
		}
		_sysmapList.addRow(_("Map label type"), array(_labelTypeMapComboBox, BR(), _customLabelMapTextArea));

		// append image to form list
		CComboBox _labelTypeImageComboBox = new CComboBox("label_type_image", Nest.value(data,"sysmap","label_type_image").$(), "", Nest.value(data,"labelTypesImage").asCArray());
		CTextArea _customLabelImageTextArea = new CTextArea("label_string_image", Nest.value(data,"sysmap","label_string_image").asString());
		if (Nest.value(data,"sysmap","label_type_image").asInteger() != MAP_LABEL_TYPE_CUSTOM) {
			_customLabelImageTextArea.addClass("hidden");
		}
		_sysmapList.addRow(_("Image label type"), array(_labelTypeImageComboBox, BR(), _customLabelImageTextArea));

		// append icon label to form list
		unset(data,"labelTypes",MAP_LABEL_TYPE_CUSTOM);
		_sysmapList.addRow(_("Icon label type"), new CComboBox("label_type", Nest.value(data,"sysmap","label_type").$(), "", Nest.value(data,"labelTypes").asCArray()));

		// append icon label location to form list
		CComboBox _locationComboBox = new CComboBox("label_location", Nest.value(data,"sysmap","label_location").$());

		CArray<String> item = map(0 , _("Bottom"), 1 , _("Left"), 2 , _("Right"), 3 , _("Top"));
		_locationComboBox.addItems(item);
		_sysmapList.addRow(_("Icon label location"), _locationComboBox);

		// append show unack to form list
		CComboBox _showUnackComboBox = new CComboBox("show_unack", Nest.value(data,"sysmap","show_unack").$());
		CArray<String> items = map(
				EXTACK_OPTION_ALL , _("All"),
				EXTACK_OPTION_BOTH , _("Separated"),
				EXTACK_OPTION_UNACK , _("Unacknowledged only")
			);
		_showUnackComboBox.addItems(items);
		_showUnackComboBox.setEnabled(Nest.value(data,"config","event_ack_enable").asBoolean());
		if (empty(Nest.value(data,"config","event_ack_enable").$())) {
			_showUnackComboBox.setAttribute("title", _("Acknowledging disabled"));
		}
		_sysmapList.addRow(_("Problem display"), _showUnackComboBox);

		// append severity min to form list
		//sjh CSeverity不存在   TODO
//		_sysmapList.addRow(_("Minimum trigger severity"), new CSeverity(array("name" => "severity_min", "value" => Nest.value(data,"sysmap","severity_min").$())));

		// create url table
		CTable _urlTable = new CTable(_("No URLs defined."), "formElementTable");
		_urlTable.setAttribute("style", "min-width: 500px;");
		_urlTable.setHeader(array(_("Name"), _("URL"), _("Element"), SPACE));
		if (empty(Nest.value(data,"sysmap","urls").$())) {
			Nest.value(data,"sysmap","urls").$(map("name" , "", "url" , "", "elementtype" , 0));
		}
		int i = 0;
		CArray<Map> urls = Nest.value(data,"sysmap","urls").asCArray();
		for(Map _url:urls){
			CTextBox _urlLabel = new CTextBox("urls["+i+"][name]", Nest.value(_url,"name").asString(), 32);
			CTextBox _urlLink = new CTextBox("urls["+i+"][url]", Nest.value(_url,"url").asString(), 32);
			//sjh CCombobox不存在   TODO
//			CCombobox _urlEtype = new CCombobox("urls["+$i+"][elementtype]", Nest.value(_url,"elementtype").$());
			//sjh sysmap_element_types方法不存在   TODO
//			_urlEtype.addItems(sysmap_element_types());
			CSpan _removeButton = new CSpan(_("Remove"), "link_menu");
			_removeButton.addAction("onclick", "$(\"urlEntry_"+i+"\").remove();");

			CRow _urlRow = new CRow(array(_urlLabel, _urlLink, null, _removeButton));
//			CRow _urlRow = new CRow(array(_urlLabel, _urlLink, _urlEtype, _removeButton));
			_urlRow.setAttribute("id", "urlEntry_"+i);

			_urlTable.addRow(_urlRow);
			i++;
		}

		// append empty template row to url table
		CTextBox _templateUrlLabel = new CTextBox("urls[#{id}][name]", "", 32);
		_templateUrlLabel.setAttribute("disabled", "disabled");
		CTextBox _templateUrlLink = new CTextBox("urls[#{id}][url]", "", 32);
		_templateUrlLink.setAttribute("disabled", "disabled");
		//sjh CCombobox不存在   TODO
//		CCombobox _templateUrlEtype = new CCombobox("urls[#{id}][elementtype]");
//		_templateUrlEtype.setAttribute("disabled", "disabled");
//		_templateUrlEtype.addItems(sysmap_element_types());
		CSpan _templateRemoveButton = new CSpan(_("Remove"), "link_menu");
		_templateRemoveButton.addAction("onclick", "$(\"entry_#{id}\").remove();");
		CRow _templateUrlRow = new CRow(array(_templateUrlLabel, _templateUrlLink, null, _templateRemoveButton));
//		CRow _templateUrlRow = new CRow(array(_templateUrlLabel, _templateUrlLink, _templateUrlEtype, _templateRemoveButton));
		_templateUrlRow.addStyle("display: none");
		_templateUrlRow.setAttribute("id", "urlEntryTpl");
		_urlTable.addRow(_templateUrlRow);

		// append \"add\" button to url table
		CSpan _addButton = new CSpan(_("Add"), "link_menu");
		_addButton.addAction("onclick", "cloneRow(\"urlEntryTpl\", "+i+")");
		CCol _addButtonColumn = new CCol(_addButton);
		_addButtonColumn.setColSpan(4);
		_urlTable.addRow(_addButtonColumn);

		// append url table to form list
		_sysmapList.addRow(_("URLs"), new CDiv(_urlTable, "objectgroup inlineblock border_dotted ui-corner-all"));

		// append sysmap to form
		CTabView _sysmapTab = new CTabView();
		_sysmapTab.addTab("sysmapTab", _("Map"), _sysmapList);
		_sysmapForm.addItem(_sysmapTab);

		// append buttons to form
		CArray _others = array();
		if (isset(_REQUEST,"sysmapid") && Nest.value(_REQUEST,"sysmapid").asInteger() > 0) {
			_others.add(new CButton("clone", _("Clone")));
			_others.add(new CButtonDelete(_("Delete network map?"), url_param(getIdentityBean(), "form")+url_param(getIdentityBean(), "sysmapid")));
		}
		_others.add(new CButtonCancel());

		_sysmapForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), _others));

		// append form to widget
		_sysmapWidget.addItem(_sysmapForm);
		_sysmapWidget.show();
	}
	
	public void doRenderListView(SQLExecutor executor){
		CArray _data = array();
		Map<String, Object> _config = select_config(getIdentityBean(), executor);
		
		// get maps
		String _sortField = getPageSortField(getIdentityBean(), executor,"name");
		String _sortOrder = getPageSortOrder(getIdentityBean(), executor);

		CMapGet option = new CMapGet();
		option.setEditable(true);
		option.setOutput(new String[]{"sysmapid", "name", "width", "height"});
		option.setSortfield(_sortField);
		option.setSortorder(_sortOrder);
		option.setLimit(Nest.value(_config,"search_limit").asInteger() + 1);
		Nest.value(_data,"maps").$(API.Map(getIdentityBean(), executor).get(option));
		order_result(Nest.value(_data,"maps").asCArray(), _sortField, _sortOrder);

		// paging
		Nest.value(_data,"paging").$(getPagingLine(getIdentityBean(), executor,Nest.value(_data,"maps").asCArray(), array("sysmapid")));

		// render view
/*			_mapView = new CView("configuration.sysmap.list", _data);
		_mapView.render();
		_mapView.show();*/
		doRenderListDisplay(_data);
	}
	
	public void doRenderListDisplay(CArray data){
		CWidget _sysmapWidget = new CWidget();

		// create header buttons
		CForm _createForm = new CForm("get");
		_createForm.cleanItems();
		_createForm.addItem(new CSubmit("form", _("Create map")));
		_createForm.addItem(new CButton("form", _("Import"), "redirect(\"conf.import.action?rules_preset=map\")"));

		_sysmapWidget.addPageHeader(_("CONFIGURATION OF NETWORK MAPS"), _createForm);

		// create form
		CForm _sysmapForm = new CForm();
		_sysmapForm.setName("frm_maps");

		_sysmapWidget.addHeader(_("Maps"));
		_sysmapWidget.addHeaderRowNumber();

		// create table
		CTableInfo _sysmapTable = new CTableInfo(_("No maps found."));
		_sysmapTable.setHeader(array(
			new CCheckBox("all_maps", false, "checkAll(\""+_sysmapForm.getName()+"\", \"all_maps\", \"maps\");"),
			make_sorting_header(_("Name"), "name"),
			make_sorting_header(_("Width"), "width"),
			make_sorting_header(_("Height"), "height"),
			_("Edit")
		));

//		for(Nest.value(data,"maps").$() as _map) {
		CArray<Map> maps = Nest.value(data,"maps").asCArray();
		for(Map _map:maps) {
			_sysmapTable.addRow(array(
				new CCheckBox("maps["+_map.get("sysmapid")+"]", false, null, Nest.value(_map,"sysmapid").asString()),
				new CLink(Nest.value(_map,"name").$(), "sysmap.action?sysmapid="+Nest.value(_map,"sysmapid").$()),
				Nest.value(_map,"width").$(),
				Nest.value(_map,"height").$(),
				new CLink(_("Edit"), "sysmaps.action?form=update&sysmapid="+_map.get("sysmapid")+"#form")
			));
		}

		// create go button
		CComboBox _goComboBox = new CComboBox("go");
		_goComboBox.addItem("export", _("Export selected"));
		CComboItem _goOption = new CComboItem("delete", _("Delete selected"));
		_goOption.setAttribute("confirm", _("Delete selected maps?"));
		_goComboBox.addItem(_goOption);
		CSubmit _goButton = new CSubmit("goButton", _("Go")+" (0)");
		_goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"maps\";");

		// append table to form
		_sysmapForm.addItem(array(Nest.value(data,"paging").$(), _sysmapTable, Nest.value(data,"paging").$(), get_table_header(array(_goComboBox, _goButton))));

		// append form to widget
		_sysmapWidget.addItem(_sysmapForm);
		_sysmapWidget.show();
	}

}
