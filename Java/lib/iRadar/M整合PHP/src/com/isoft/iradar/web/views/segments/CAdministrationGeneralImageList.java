package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_BACKGROUND;
import static com.isoft.iradar.inc.Defines.IMAGE_TYPE_ICON;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralImageList extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		// header
		CComboBox imageComboBox = new CComboBox("imagetype", Nest.value(data,"imagetype").$(), "submit();");
		imageComboBox.addItem(IMAGE_TYPE_ICON, _("Icon"));
		imageComboBox.addItem(IMAGE_TYPE_BACKGROUND, _("Background"));
		CForm imageComboBoxForm = new CForm();
		imageComboBoxForm.addItem(_("Type")+SPACE);
		imageComboBoxForm.addItem(imageComboBox);
		((CWidget)data.get("widget")).addHeader(_("Images"), imageComboBoxForm);

		// form
		CForm imageForm = new CForm();
		imageForm.setName("imageForm");
		imageForm.addItem(BR());

		CTable imageTable = new CTable(_("No images found."), "header_wide padding_standard");

		int count = 0;
		CRow imageRow = new CRow();
		for(Map image : (CArray<Map>)Nest.value(data,"images").asCArray()) {
			CTag img = (Nest.value(image,"imagetype").asInteger() == IMAGE_TYPE_BACKGROUND)
				? new CLink(new CImg("imgstore.action?width=200&height=200&iconid="+Nest.value(image,"imageid").$(), "no image"), "image.action?imageid="+Nest.value(image,"imageid").$())
				: new CImg("imgstore.action?iconid="+Nest.value(image,"imageid").$(), "no image");

			CLink name = new CLink(Nest.value(image,"name").$(), "adm.images.action?form=update&imageid="+Nest.value(image,"imageid").$());

			CCol imgColumn = new CCol();
			imgColumn.setAttribute("align", "center");
			//TODO benne said:how about center? 
			//imgColumn.addItem(array(img, BR(), name), "center");
			imgColumn.addItem(array(img, BR(), name));
			imageRow.addItem(imgColumn);

			count++;
			if ((count % 4) == 0) {
				imageTable.addRow(imageRow);
				imageRow = new CRow();
			}
		}

		if (count > 0) {
			while ((count % 4) != 0) {
				imageRow.addItem(SPACE);
				count++;
			}
			imageTable.addRow(imageRow);
		}

		imageForm.addItem(imageTable);

		return imageForm;
	}

}
