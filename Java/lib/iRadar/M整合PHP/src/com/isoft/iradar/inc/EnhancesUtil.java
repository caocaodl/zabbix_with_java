package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;

import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFieldset;
import com.isoft.iradar.tags.CLegend;

public class EnhancesUtil {

	private EnhancesUtil() {
	}
	
	public static CDiv get_table_header_actions(Object funcs){
		CDiv funcer = new CDiv();
		funcer.setAttribute("class", "headeractions");
		funcer.addItem(get_table_header(funcs));
		return funcer;
	}

	public static CDiv get_table_header_actions(Object left, Object right){
		CDiv funcer = new CDiv();
		funcer.setAttribute("class", "headeractions");
		funcer.addItem(get_table_header(left,right));
		return funcer;
	}
	
	public static CDiv get_table_footer_actions(Object funcs){
		CDiv funcer = new CDiv();
		funcer.setAttribute("class", "footeractions");
		funcer.addItem(get_table_header(funcs));
		return funcer;
	}
	
	public static CFieldset get_search_condition_area(Object queryfields){
		CFieldset area = new CFieldset();
		area.setAttribute("class", "collapsible");
		CLegend legend = new CLegend();
		legend.addItem(_("search conditions"));
		area.addItem(legend);
		area.addItem(queryfields);
		return area;
	}
	
}
