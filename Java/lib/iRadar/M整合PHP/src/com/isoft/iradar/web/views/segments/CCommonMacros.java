package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CCommonMacros extends CViewSegment {

	@Override
	public CFormList doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		boolean readonly = (isset(Nest.value(data,"readonly").$()) && Nest.value(data,"readonly").asBoolean());
		CArray<Map> macros = array_values(Nest.value(data,"macros").asCArray());

		if (readonly && empty(macros)) {
			CFormList macrosFormList = new CFormList("macrosFormList");
			macrosFormList.addRow(_("No macros found."));
			return macrosFormList;
		}

		if (!readonly) {
			includeSubView("js/common.macros.js", data);
		}

		CTable macrosTable = new CTable(SPACE, "formElementTable");
		macrosTable.setAttribute("id", "tbl_macros");
		macrosTable.addRow(array(_("Macro"), SPACE, _("Value"), SPACE));

		// fields
		for (Entry<Object, Map> e : macros.entrySet()) {
		    Object i = e.getKey();
		    Map macro = e.getValue();
			CTextBox text1 = new CTextBox("macros["+i+"][macro]", Nest.value(macro,"macro").asString(), 30, readonly, 64);
			text1.setAttribute("placeholder", "{$MACRO}");
			text1.setAttribute("style", "text-transform:uppercase;");
			CTextBox text2 = new CTextBox("macros["+i+"][value]", Nest.value(macro,"value").asString(), 40, readonly, 255);
			text2.setAttribute("placeholder", _("value"));
			CSpan span = new CSpan(RARR);
			span.addStyle("vertical-align:top;");

			CArray deleteButtonCell = null;
			if (!readonly) {
				deleteButtonCell = array(new CButton("macros_"+i+"_remove", _("Remove"), null, "link_menu macroRemove"));
				if (isset(Nest.value(macro,"globalmacroid").$())) {
					deleteButtonCell.add(new CVar("macros["+i+"][globalmacroid]", Nest.value(macro,"globalmacroid").$(), "macros_"+i+"_id"));
				}
				if (isset(Nest.value(macro,"hostmacroid").$())) {
					deleteButtonCell.add(new CVar("macros["+i+"][hostmacroid]", Nest.value(macro,"hostmacroid").$(), "macros_"+i+"_id"));
				}
			}

			CArray row = array(text1, span, text2, deleteButtonCell);
			macrosTable.addRow(row, "form_row");
		}

		// buttons
		if (!readonly) {
			CButton addButton = new CButton("macro_add", _("Add"), null, "link_menu");
			CCol buttonColumn = new CCol(addButton);
			buttonColumn.setAttribute("colspan", 5);

			CRow buttonRow = new CRow();
			buttonRow.setAttribute("id", "row_new_macro");
			buttonRow.addItem(buttonColumn);

			macrosTable.addRow(buttonRow);
		}

		// form list
		CFormList macrosFormList = new CFormList("macrosFormList");
		macrosFormList.addRow(macrosTable);
		return macrosFormList;
	}

}
