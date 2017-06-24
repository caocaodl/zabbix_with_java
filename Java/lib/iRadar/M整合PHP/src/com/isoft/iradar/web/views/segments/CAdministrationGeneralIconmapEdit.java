package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.key;
import static com.isoft.iradar.inc.Defines.RDA_ICON_PREVIEW_HEIGHT;
import static com.isoft.iradar.inc.Defines.RDA_ICON_PREVIEW_WIDTH;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralIconmapEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.iconmap.js");

		CFormList iconMapTab = new CFormList("scriptsTab");

		CTextBox name = new CTextBox("iconmap[name]", Nest.value(data,"iconmap","name").asString());
		name.setAttribute("maxlength", 64);
		name.attr("autofocus", "autofocus");
		iconMapTab.addRow(_("Name"), name);

		CTable iconMapTable = new CTable();
		iconMapTable.setAttribute("id", "iconMapTable");

		CForm iconMapForm = new CForm();
		iconMapForm.addVar("form", 1);
		if (isset(Nest.value(data,"iconmapid").$())) {
			iconMapForm.addVar("iconmapid", Nest.value(data,"iconmap","iconmapid").$());
		}

		// header
		iconMapTable.addRow(array(SPACE, SPACE, _("Inventory field"), _("Expression"), _("Icon"), SPACE, SPACE), "header");

		order_result(Nest.value(data,"iconmap","mappings").asCArray(), "sortorder");
		int i = 0;
		for(Map mapping : (CArray<Map>)Nest.value(data,"iconmap","mappings").asCArray()) {
			CSpan numSpan  = new CSpan((i + 1)+":");
			numSpan.addClass("rowNum");

			CComboBox profileLinksComboBox = new CComboBox("iconmap[mappings]["+i+"][inventory_link]", Nest.value(mapping,"inventory_link").$());
			profileLinksComboBox.addItems(Nest.value(data,"inventoryList").asCArray());

			CTextBox cexpressionTextBox  = new CTextBox("iconmap[mappings]["+i+"][expression]", Nest.value(mapping,"expression").asString());
			cexpressionTextBox.setAttribute("maxlength", 64);
			CArray expressionTextBox = array(cexpressionTextBox);
			if (isset(Nest.value(mapping,"iconmappingid").$())) {
				expressionTextBox.add(new CVar("iconmap[mappings]["+i+"][iconmappingid]", Nest.value(mapping,"iconmappingid").$()));
			}

			CComboBox iconsComboBox = new CComboBox("iconmap[mappings]["+i+"][iconid]", Nest.value(mapping,"iconid").$());
			iconsComboBox.addClass("mappingIcon");
			iconsComboBox.addItems(Nest.value(data,"iconList").asCArray());

			CImg iconPreviewImage = new CImg("imgstore.action?iconid="+mapping.get("iconid")+"&width="+RDA_ICON_PREVIEW_WIDTH+
				"&height="+RDA_ICON_PREVIEW_HEIGHT, _("Preview"), null, null, "pointer preview");
			iconPreviewImage.setAttribute("data-image-full", "imgstore.action?iconid="+Nest.value(mapping,"iconid").$());

			CRow row = new CRow(array(
				new CSpan(null, "ui-icon ui-icon-arrowthick-2-n-s move"),
				numSpan,
				profileLinksComboBox,
				expressionTextBox,
				iconsComboBox,
				iconPreviewImage,
				new CButton("remove", _("Remove"), "", "link_menu removeMapping")
			), "sortable");
			row.setAttribute("id", "iconmapidRow_"+i);
			iconMapTable.addRow(row);

			i++;
		}

		// hidden row for js
		Object firstIconId = key(Nest.value(data,"iconList").asCArray());
		CSpan numSpan = new CSpan("0:");
		numSpan.addClass("rowNum");

		CComboBox profileLinksComboBox = new CComboBox("iconmap[mappings][#{iconmappingid}][inventory_link]");
		profileLinksComboBox.addItems(Nest.value(data,"inventoryList").asCArray());
		profileLinksComboBox.setAttribute("disabled", "disabled");

		CTextBox expressionTextBox = new CTextBox("iconmap[mappings][#{iconmappingid}][expression]");
		expressionTextBox.setAttribute("maxlength", 64);
		expressionTextBox.setAttribute("disabled", "disabled");

		CComboBox iconsComboBox = new CComboBox("iconmap[mappings][#{iconmappingid}][iconid]", firstIconId);
		iconsComboBox.addClass("mappingIcon");
		iconsComboBox.addItems(Nest.value(data,"iconList").asCArray());
		iconsComboBox.setAttribute("disabled", "disabled");

		CImg iconPreviewImage = new CImg("imgstore.action?iconid="+firstIconId+"&width="+RDA_ICON_PREVIEW_WIDTH+
			"&height="+RDA_ICON_PREVIEW_HEIGHT, _("Preview"), null, null, "pointer preview");
		iconPreviewImage.setAttribute("data-image-full", "imgstore.action?iconid="+firstIconId);

		// row template
		CRow hiddenRowTemplate = new CRow(array(
			new CSpan(null, "ui-icon ui-icon-arrowthick-2-n-s move"),
			numSpan,
			profileLinksComboBox,
			expressionTextBox,
			iconsComboBox,
			iconPreviewImage,
			new CButton("remove", _("Remove"), "", "link_menu removeMapping")
		), "hidden");
		hiddenRowTemplate.setAttribute("id", "rowTpl");
		iconMapTable.addRow(hiddenRowTemplate);

		// add row button
		iconMapTable.addRow(new CCol(new CButton("addMapping", _("Add"), "", "link_menu"), null, 7));

		// <default icon row>
		numSpan = new CSpan((i++)+":");
		numSpan.addClass("rowNum");

		iconsComboBox = new CComboBox("iconmap[default_iconid]", Nest.value(data,"iconmap","default_iconid").$());
		iconsComboBox.addClass("mappingIcon");
		iconsComboBox.addItems(Nest.value(data,"iconList").asCArray());

		iconPreviewImage = new CImg("imgstore.action?iconid="+Nest.value(data,"iconmap","default_iconid").asString()+
			"&width="+RDA_ICON_PREVIEW_WIDTH+"&height="+RDA_ICON_PREVIEW_HEIGHT, _("Preview"), null, null, "pointer preview");
		iconPreviewImage.setAttribute("data-image-full", "imgstore.action?iconid="+Nest.value(data,"iconmap","default_iconid").$());

		iconMapTable.addRow(array(new CCol(_("Default"), null, 4), iconsComboBox, iconPreviewImage));
		// </default icon row>

		iconMapTab.addRow(_("Mappings"), new CDiv(iconMapTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		CTabView iconMapView = new CTabView();
		iconMapView.addTab("iconmap", _("Icon map"), iconMapTab);
		iconMapForm.addItem(iconMapView);

		// footer
		CArray<CButtonCancel> secondaryActions = array(new CButtonCancel());
		if (isset(Nest.value(data,"iconmapid").$())) {
			array_unshift(secondaryActions,
				new CSubmit("clone", _("Clone")),
				new CButtonDelete(_("Delete icon map?"), url_param(idBean, "form")+url_param(idBean, "iconmapid"))
			);
		}
		iconMapForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), secondaryActions));

		return iconMapForm;
	}

}
