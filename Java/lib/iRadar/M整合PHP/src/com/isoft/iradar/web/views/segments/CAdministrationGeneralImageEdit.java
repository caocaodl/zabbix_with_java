package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_BACKGROUND;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_ICON;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFile;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralImageEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CForm imageForm = new CForm("post", null, "multipart/form-data");
		imageForm.setName("imageForm");
		imageForm.addVar("form", Nest.value(data,"form").$());
		imageForm.addVar("imageid", Nest.value(data,"imageid").$());

		CComboBox imageComboBox = new CComboBox("imagetype", Nest.value(data,"imagetype").$());
		imageComboBox.addItem(IMAGE_TYPE_ICON, _("Icon"));
		imageComboBox.addItem(IMAGE_TYPE_BACKGROUND, _("Background"));

		// append form list
		CFormList imageFormList = new CFormList("imageFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"imagename").asString(), 64, false, 64);
		nameTextBox.attr("autofocus", "autofocus");
		imageFormList.addRow(_("Name"), nameTextBox);
		imageFormList.addRow(_("Type"), imageComboBox);
		imageFormList.addRow(_("Upload"), new CFile("image"));

		if (!empty(Nest.value(data,"imageid").$())) {
			if (Nest.value(data,"imagetype").asInteger() == IMAGE_TYPE_BACKGROUND) {
				imageFormList.addRow(_("Image"), new CLink(new CImg("imgstore.action?width=200&height=200&iconid="+Nest.value(data,"imageid").$(), "no image"), "image.action?imageid="+Nest.value(data,"imageid").$()));
			} else {
				imageFormList.addRow(_("Image"), new CImg("imgstore.action?iconid="+Nest.value(data,"imageid").$(), "no image", null));
			}
		}

		// append tab
		CTabView imageTab = new CTabView();
		imageTab.addTab("imageTab", _("Image"), imageFormList);
		imageForm.addItem(imageTab);

		// append buttons
		if (empty(Nest.value(data,"imageid").$())) {
			imageForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel()));
		} else {
			imageForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CButtonDelete(_("Delete selected image?"), url_param(idBean, "form")+url_param(idBean, "imageid")),
					new CButtonCancel()
				)
			));
		}

		return imageForm;
	}

}
