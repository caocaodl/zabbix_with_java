package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.toArray;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.isoft.iradar.RadarContext;
import com.isoft.types.CArray;

public class CWidget {
	public Boolean state;
	public Integer flicker_state;
	private String css_class;
	private List<Map<String, Object>> pageHeaders = new ArrayList();
	private List<Map<String, Object>> headers = new ArrayList();
	private List flicker = new ArrayList();
	protected List body = new ArrayList();
	protected String rootClass;
	protected String bodyId;

	public static boolean flag = false;
	public static boolean firstFlag = false;

	public static String oldStr = "";
	public static String newStr = "";

	public CWidget() {
		this(null);
	}

	public CWidget(String bodyId) {
		this(bodyId, null);
	}

	public CWidget(String bodyId, String rootClass) {
		if (is_null(bodyId)) {
			bodyId = "widget_" + System.currentTimeMillis();
		}
		this.bodyId = bodyId;
		this.flicker_state = Integer.valueOf(1);
		this.css_class = (is_null(this.state) ? "header_wide" : "header");
		setRootClass(rootClass);
	}

	public void setClass(String styleclass) {
		this.css_class = styleclass;
	}

	public void addPageHeader() {
		addPageHeader(SPACE);
	}

	public void addPageHeader(Object left) {
		addPageHeader(left, SPACE);
	}

	public void addPageHeader(Object left, Object right) {
		right = toArray(right);
		this.pageHeaders.add(CArray.map(new Object[] { "left", left, "right", right }).toMap());
	}

	public void addHeader() {
		addHeader(SPACE);
	}

	public void addHeader(Object left) {
		addHeader(left, SPACE);
	}

	public void addHeader(Object left, Object right) {
		right = toArray(right);
		Map header = new HashMap();
		header.put("left", left);
		header.put("right", right);
		this.headers.add(header);
	}

	public void addHeaderRowNumber() {
		addHeaderRowNumber(SPACE);
	}

	public void addHeaderRowNumber(Object right) {
		CDiv numRows = new CDiv();
		numRows.setAttribute("name", "numrows");
		addHeader(numRows, right);
	}

	public void addFlicker() {
		addFlicker(null);
	}

	public void addFlicker(Object items) {
		addFlicker(items, Integer.valueOf(0));
	}

	public void addFlicker(Object items, Integer state) {
		if (!is_null(items)) {
			this.flicker.add(items);
		}
		this.flicker_state = state;
	}

	public void addItem(Object items) {
		if (!is_null(items))
			this.body.add(items);
	}

	public CDiv get() {
		List widget = new ArrayList();
		List area = new ArrayList();

		if (!empty(this.pageHeaders)) {
			area.add(createPageHeader());
		}
		if (!empty(this.headers) && empty(this.flicker)) {
			area.add(createHeader());
		}
		// if (is_null(this.state)) {
		// this.state = Boolean.valueOf(true);
		// }
		if (!empty(this.flicker)) {
			String flicker_domid = "flicker_" + this.bodyId;
			CTable flicker_tab = new CTable(null, "flicker_table");
			flicker_tab.setAttribute("width", "100%");
			flicker_tab.setCellPadding(0);
			flicker_tab.setCellSpacing(0);

			CDiv div = new CDiv(this.flicker, null, flicker_domid);
			div.setAttribute("class", "flickerarea");
			if ((this.flicker_state == null) || (this.flicker_state.intValue() == 0)) {
				div.setAttribute("style", "display: none;");
			}

			CDiv icon_l = new CDiv(SPACE+SPACE, (this.flicker_state != null) && (this.flicker_state.intValue() > 0) ? "dbl_arrow_up"
					: "dbl_arrow_down", "flicker_icon_l");
			icon_l.setAttribute("title", _("Maximize") + "/" + _("Minimize"));

			CDiv icon_r = new CDiv(SPACE+SPACE, (this.flicker_state != null) && (this.flicker_state.intValue() > 0) ? "dbl_arrow_up"
					: "dbl_arrow_down", "flicker_icon_r");
			icon_r.setAttribute("title", _("Maximize") + "/" + _("Minimize"));

			CDiv icons_row = new CDiv(null, "textwhite flicker_c_div");
			icons_row.addItem(new Object[] { icon_l, new CSpan(SPACE + _("Filter") + SPACE), icon_r });
			icons_row.addAction("onclick", "javascript: changeFlickerState('" + flicker_domid + "');");

			CTable thin_tab = createFlicker(icons_row);
			thin_tab.attr("id", "filter_icon");

			flicker_tab.addRow(thin_tab, "textcolorstyles pointer");
			flicker_tab.addRow(div);

			area.add(flicker_tab);
		}

		if (!area.isEmpty()) {
			widget.add(area);
		}

		if (is_null(this.state)) {
			this.state = Boolean.valueOf(true);
		}

		CDiv div = new CDiv(this.body, "w");
		div.setAttribute("id", this.bodyId);
		if (!this.state.booleanValue()) {
			div.setAttribute("style", "display: none;");
		}
		widget.add(div);
		CDiv cwidget = new CDiv(widget, getRootClass());
		return cwidget;
	}

	public void show() {
		RadarContext ctx = RadarContext.getContext();
		String str = toString();
		if (flag) {
			if (firstFlag) {
				str = str.replaceFirst(oldStr, newStr);
			} else {
				str = str.replace(oldStr, newStr);
			}
		}
		ctx.write(str);
		flag = false;
		firstFlag = false;
		oldStr = "";
		newStr = "";
	}

	public String toString() {
		Object tab = get();
		return CObject.unpack_object(tab).toString();
	}

	private CDiv createPageHeader() {
		List pageHeader = new ArrayList();

		for (Map ph : this.pageHeaders) {
			pageHeader.add(get_table_header(ph.get("left"), ph.get("right")));
		}
		return new CDiv(pageHeader.toArray(new CTable[0]));
	}

	private CDiv createHeader() {
		Map header = !this.headers.isEmpty() ? (Map) this.headers.get(0) : new HashMap();

		List columnRights = new ArrayList();

		if (!is_null(header.get("right"))) {
			CArray rights = CArray.valueOf(header.get("right"));
			for (Iterator localIterator1 = rights.iterator(); localIterator1.hasNext();) {
				Object right = localIterator1.next();
				columnRights.add(new CDiv(right, "floatright"));
			}
		}

		if (!is_null(this.state)) {
			CIcon icon = new CIcon(_("Show") + "/" + _("Hide"), this.state.booleanValue() ? "arrowup" : "arrowdown",
					"change_hat_state(this, '" + this.bodyId + "');");
			icon.setAttribute("id", this.bodyId + "_icon");
			columnRights.add(icon);
		}

		Object[] crights = columnRights.toArray();
		ArrayUtils.reverse(crights);

		CTable table = new CTable(null, this.css_class + " maxwidth");
		table.setCellSpacing(0);
		table.setCellPadding(1);
		table.addRow(createHeaderRow(header.get("left"), crights), "first");

		if (!"header_wide".equals(this.css_class)) {
			table.addClass("ui-widget-header ui-corner-all");
		}

		boolean first = true;
		for (Map h : this.headers) {
			if (first) {
				first = false;
			} else {
				table.addRow(createHeaderRow(h.get("left"), h.get("right")), "next");
			}
		}
		return new CDiv(table, "body_header");
	}

	private CCol[] createHeaderRow(Object col1, Object col2) {
		CCol td_r = new CCol(col2, "header_r right");
		return new CCol[] { new CCol(col1, "header_l left"), td_r };
	}

	private CTable createFlicker(Object col1) {
		return createFlicker(col1, null);
	}

	private CTable createFlicker(Object col1, Object col2) {
		CTable table = new CTable(null, "textwhite maxwidth middle flicker");
		table.setCellSpacing(0);
		table.setCellPadding(1);

		CCol headerCol = null;
		if (!empty(this.headers)) {
			headerCol = new CCol(createHeader());
		}

		if (!is_null(col2)) {
			CCol td_r = new CCol(col2, "flicker_r");
			td_r.setAttribute("align", "right");
			td_r.setAttribute("width", "100px");
			table.addRow(new CCol[] { headerCol, new CCol(col1, "flicker_l"), td_r });
		} else {
			CCol td_c = new CCol(col1, "flicker_c");
			td_c.setAttribute("align", "right");
			td_c.setAttribute("width", "100px");
			table.addRow(new CCol[] { headerCol, td_c });
		}
		return table;
	}

	public void setRootClass(String rootClass) {
		this.rootClass = rootClass;
	}

	public String getRootClass() {
		return this.rootClass;
	}

	public static void setReplaceFlag(boolean boo, boolean firstBoo) {
		if (boo) {
			flag = boo;
			if (firstBoo) {
				firstFlag = firstBoo;
			}
		}
	}

	public static void setReplaceString(String oldstr, String repStr) {
		oldStr = oldstr;
		newStr = repStr;
	}

}