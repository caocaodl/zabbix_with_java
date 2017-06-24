package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFile;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * TODO
 * @author benne
 *
 */
public class CConfImport extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTable rulesTable = new CTable(null, "formElementTable");
		rulesTable.setHeader(array(SPACE, _("Update existing"), _("Add missing")), "bold");

//		CArray titles = map(
//			"groups", _("Groups"),
//			"hosts", _("Hosts"),
//			"templates", _("Templates"),
//			"templateScreens", _("Template screens"),
//			"templateLinkage", _("Template linkage"),
//			"items", _("Items"),
//			"discoveryRules", _("Discovery rules"),
//			"triggers", _("Triggers"),
//			"graphs", _("Graphs"),
//			"screens", _("Screens"),
//			"maps", _("Maps"),
//			"images", _("Images")
//		);
		CArray<Map> rules = Nest.value(data,"rules").asCArray();
		for (Entry<Object, Map> e : rules.entrySet()) {
		    Object key = e.getKey();
		    Map title = e.getValue();
			Object cbExist = SPACE;
			Object cbMissed = SPACE;

			if (isset(Nest.value(rules,key,"updateExisting").$())) {
				cbExist = new CCheckBox("rules["+key+"][updateExisting]", Nest.value(rules,key,"updateExisting").asBoolean(), null, 1);

				if ("images".equals(key)) {
					if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
						continue;
					}
					((CCheckBox)cbExist).setAttribute("onclick", "if (this.checked) return confirm(\""+_("Images for all maps will be updated!")+"\")");
				}
			}

			if (isset(Nest.value(rules,key,"createMissing").$())) {
				cbMissed = new CCheckBox("rules["+key+"][createMissing]", Nest.value(rules,key,"createMissing").asBoolean(), null, 1);
			}

			rulesTable.addRow(array(title, new CCol(cbExist, "center"), new CCol(cbMissed, "center")));
		}

		// form list
		CFormList importFormList = new CFormList("proxyFormList");
		importFormList.addRow(_("Import file"), new CFile("import_file"));
		importFormList.addRow(_("Rules"), new CDiv(rulesTable, "border_dotted objectgroup inlineblock"));

		// tab
		CTabView importTab = new CTabView();
		importTab.addTab("importTab", _("Import"), importFormList);

		// form
		CForm importForm = new CForm("post", null, "multipart/form-data");
		importForm.addItem(importTab);
		importForm.addItem(makeFormFooter(new CSubmit("import", _("Import")), new CButtonCancel()));

		// widget
		CWidget importWidget = new CWidget();
		importWidget.addItem(importForm);

		return importWidget;
	}

}
