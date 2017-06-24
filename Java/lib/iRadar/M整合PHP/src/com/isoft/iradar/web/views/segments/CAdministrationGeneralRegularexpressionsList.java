package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.RegexpUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralRegularexpressionsList extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		((CWidget)data.get("cnf_wdgt")).addHeader(_("Regular expressions"));

		CForm regExpForm = new CForm();
		regExpForm.setName("regularExpressionsForm");
		regExpForm.addItem(BR());

		CTableInfo regExpTable = new CTableInfo(_("No regular expressions found."));
		regExpTable.setHeader(array(
			new CCheckBox("all_regexps", false, "checkAll(\"regularExpressionsForm\", \"all_regexps\", \"regexpids\");"),
			_("Name"),
			_("Expressions")
		));

		CArray expressions = array();
		CArray values = array();
		for(Map exp : (CArray<Map>)Nest.value(data,"db_exps").asCArray()) {
			if (!isset(expressions,exp.get("regexpid"))) {
				Nest.value(values,exp.get("regexpid")).$(1);
			} else {
				Nest.value(values,exp.get("regexpid")).$(Nest.value(values,exp.get("regexpid")).asInteger()+1);
			}

			if (!isset(expressions,exp.get("regexpid"))) {
				Nest.value(expressions,exp.get("regexpid")).$(new CTable());
			}

			((CTable)expressions.get(exp.get("regexpid"))).addRow(array(
					Nest.value(values,exp.get("regexpid")).$(),
				" &raquo; ",
				Nest.value(exp,"expression").$(),
				" ["+RegexpUtil.expression_type2str(Nest.value(exp,"expression_type").asInteger())+"]"
			));
		}
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"regexps").asCArray()).entrySet()) {
		    Object regexpid = e.getKey();
		    Map regexp = e.getValue();
			regExpTable.addRow(array(
				new CCheckBox("regexpids["+Nest.value(regexp,"regexpid").$()+"]", false, null, Nest.value(regexp,"regexpid").asInteger()),
				new CLink(Nest.value(regexp,"name").$(), "adm.regexps.action?form=update&regexpid="+Nest.value(regexp,"regexpid").$()),
				isset(expressions,regexpid) ? expressions.get(regexpid) : "-"
			));
		}

		CComboBox goBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected regular expressions?"));
		goBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"regexpids\";");

		regExpTable.setFooter(new CCol(array(goBox, goButton)));

		regExpForm.addItem(regExpTable);

		return regExpForm;
	}

}
