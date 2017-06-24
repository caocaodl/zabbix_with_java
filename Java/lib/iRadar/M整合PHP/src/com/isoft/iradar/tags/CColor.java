package com.isoft.iradar.tags;

import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.JsUtil.insert_show_color_picker_javascript;
import static com.isoft.types.CArray.array;

public class CColor extends CObject {
	
	private static final long serialVersionUID = 1L;

	public CColor(String name, String value) {
		CTextBox txt = new CTextBox(name, value);
		txt.addStyle("width: 6em;");
		txt.attr("maxlength", 6);
		txt.attr("id", rda_formatDomId(name));
		txt.addAction("onchange", "set_color_by_name(\""+name+"\", this.value)");
		txt.addStyle("margin-top: 0px; margin-bottom: 0px;");

		CColorCell lbl = new CColorCell("lbl_"+name, value, "javascript: show_color_picker(\""+name+"\")");

		addItem(array(txt, lbl));

		insert_show_color_picker_javascript();
	}

}
