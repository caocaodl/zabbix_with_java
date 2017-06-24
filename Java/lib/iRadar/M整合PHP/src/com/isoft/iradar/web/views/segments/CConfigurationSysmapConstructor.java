package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.SYSMAP_EXPAND_MACROS_ON;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_ALIGN_ON;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_SHOW_ON;
import static com.isoft.iradar.inc.JsUtil.insert_show_color_picker_javascript;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationSysmapConstructor extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.sysmaps.js");

		CWidget sysmapWidget = new CWidget();
		sysmapWidget.addPageHeader(_("CONFIGURATION OF NETWORK MAPS"));

		// create menu
		CIcon addIcon = new CIcon(_("Add element"), "iconplus");
		addIcon.setAttribute("id", "selementAdd");
		CIcon removeIcon = new CIcon(_("Remove element"), "iconminus");
		removeIcon.setAttribute("id", "selementRemove");

		CIcon addLinkIcon = new CIcon(_("Add link"), "iconplus");
		addLinkIcon.setAttribute("id", "linkAdd");
		CIcon removeLinkIcon = new CIcon(_("Remove link"), "iconminus");
		removeLinkIcon.setAttribute("id", "linkRemove");

		CSpan expandMacros = new CSpan((Nest.value(data,"sysmap","expand_macros").asInteger() == SYSMAP_EXPAND_MACROS_ON) ? _("On") : _("Off"), "whitelink");
		expandMacros.setAttribute("id", "expand_macros");

		CSpan gridShow = new CSpan((Nest.value(data,"sysmap","grid_show").asInteger() == SYSMAP_GRID_SHOW_ON) ? _("Shown") : _("Hidden"), "whitelink");
		gridShow.setAttribute("id", "gridshow");

		CSpan gridAutoAlign = new CSpan((Nest.value(data,"sysmap","grid_align").asInteger() == SYSMAP_GRID_ALIGN_ON) ? _("On") : _("Off"), "whitelink");
		gridAutoAlign.setAttribute("id", "gridautoalign");

		CComboBox gridSize = new CComboBox("gridsize", Nest.value(data,"sysmap","grid_size").$());
		gridSize.addItems((CArray)map(
			20, "20x20",
			40, "40x40",
			50, "50x50",
			75, "75x75",
			100, "100x100"
		));

		CSubmit gridAlignAll = new CSubmit("gridalignall", _("Align icons"));
		gridAlignAll.setAttribute("id", "gridalignall");

		CDiv gridForm = new CDiv(array(gridSize, gridAlignAll));
		gridForm.setAttribute("id", "gridalignblock");

		CSubmit saveButton = new CSubmit("save", _("Save"));
		saveButton.setAttribute("id", "sysmap_save");

		CTable menuTable = new CTable(null, "textwhite");
		menuTable.addRow(array(
			_s("Map \"%s\"", Nest.value(data,"sysmap","name").$()),
			SPACE+SPACE,
			_("Icon"), SPACE, addIcon, SPACE, removeIcon,
			SPACE+SPACE,
			_("Link"), SPACE, addLinkIcon, SPACE, removeLinkIcon,
			SPACE+SPACE,
			_("Expand macros")+" [ ", expandMacros, " ]",
			SPACE+SPACE,
			_("Grid")+SPACE+"[", gridShow, "|", gridAutoAlign, "]",
			SPACE,
			gridForm,
			SPACE+"|"+SPACE,
			saveButton
		));

		sysmapWidget.addPageHeader(menuTable);

		// create map
		CImg backgroundImage = new CImg("images/general/tree/zero.gif", "Sysmap");
		//TODO what? by benne
		//backgroundImage.setAttribute("id", "sysmap_img", Nest.value(data,"sysmap","width").$(), Nest.value(data,"sysmap","height").$());
		backgroundImage.setAttribute("id", "sysmap_img");

		CTable backgroundImageTable = new CTable();
		backgroundImageTable.addRow(backgroundImage);
		sysmapWidget.addItem(backgroundImageTable);

		CDiv container = new CDiv();
		container.setAttribute("id", "sysmap_cnt");
		sysmapWidget.addItem(container);

		// create elements
		rda_add_post_js("IRADAR.apps.map.run(\"sysmap_cnt\", "+CJs.encodeJson((CArray)map(
			"sysmap", Nest.value(data,"sysmap").$(),
			"iconList", Nest.value(data,"iconList").$(),
			"defaultAutoIconId", Nest.value(data,"defaultAutoIconId").$(),
			"defaultIconId", Nest.value(data,"defaultIconId").$(),
			"defaultIconName", Nest.value(data,"defaultIconName").$()
		), true)+");");

		insert_show_color_picker_javascript();

		return sysmapWidget;
	}

}
