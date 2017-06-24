package com.isoft.iradar.tags;

import static com.isoft.types.CArray.array;

import java.util.Map.Entry;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.utils.CJs;
import com.isoft.types.CArray;

public class CTabView extends CDiv {
	
	private static final long serialVersionUID = 1L;	

	protected String id = "tabs";
	protected CArray<CTag> tabs = array();
	protected CArray<String> headers = array();
	protected String selectedTab = null;

	/**
	 * Disabled tabs IDs, tab option
	 *
	 * @var array
	 */
	protected CArray disabledTabs = array();

	public CTabView() {
		this(array());
	}
	public CTabView(CArray _data) {
		super();
		if (Cphp.isset(_data.get("id"))) {
			this.id = (String)_data.get("id");
		}
		if (Cphp.isset(_data.get("selected"))) {
			this.setSelected((String)_data.get("selected"));
		}
		if (Cphp.isset(_data.get("disabled"))) {
			this.setDisabled((CArray)_data.get("disabled"));
		}
		this.attr("id", FuncsUtil.rda_formatDomId(this.id));
		this.attr("class", "tabs");
	}

	public void setSelected(String _selected) {
		this.selectedTab = _selected;
	}

	/**
	 * Disable tabs
	 *
	 * @param array		_disabled	disabled tabs IDs (first tab - 0, second - 1...)
	 *
	 * @return void
	 */
	public void setDisabled(CArray _disabled) {
		this.disabledTabs = _disabled;
	}

	public void addTab(String _id, String _header, Object _body) {
		this.headers.put(_id, _header);
		
		CDiv div = new CDiv(_body);
		this.tabs.put(_id, div);
		div.attr("id", FuncsUtil.rda_formatDomId(_id));
	}
	
	public String toString() {
		return toString(true);
	}
	public String toString(boolean _destroy) {
		if (Cphp.count(this.tabs) == 1) {
			this.setAttribute("class", "min-width ui-tabs ui-widget ui-widget-content ui-corner-all widget");

			Object header = Cphp.reset(this.headers);
			CDiv _header = new CDiv(header);
			_header.addClass("ui-corner-all ui-widget-header header");
			_header.setAttribute("id", "tab_"+Cphp.key(this.headers));
			this.addItem(_header);

			CTag _tab = Cphp.reset(this.tabs);
			_tab.addClass("ui-tabs ui-tabs-panel ui-widget ui-widget-content ui-corner-all widget");
			this.addItem(_tab);
		}
		else {
			CList _headersList = new CList();

			for(Entry<Object, String> e: this.headers.entrySet()) {
				String _id = String.valueOf(e.getKey());
				String _header = e.getValue();
				
				CLink _tabLink = new CLink(_header, "#"+_id, null, null, false);
				_tabLink.setAttribute("id", "tab_"+_id);
				_headersList.addItem(_tabLink);
			}

			this.addItem(_headersList);
			this.addItem(this.tabs);

			String _activeTab, _createEvent;
			if (this.selectedTab == null) {
				_activeTab = FuncsUtil.get_cookie("tab", "0");
				_createEvent = "";
			} else {
				_activeTab = this.selectedTab;
				_createEvent = "create: function() { jQuery.cookie(\"tab\", "+this.selectedTab+"); },";
			}

			String _disabledTabs = (this.disabledTabs == null) ? "" : "disabled: "+CJs.encodeJson(this.disabledTabs.toArray())+",";

			JsUtil.rda_add_post_js(
					"jQuery(\"#"+this.id+"\").tabs({"
					+_createEvent
					+_disabledTabs
					+"active: "+_activeTab+"," + 
					"activate: function(event, ui) {" + 
					"jQuery.cookie(\"tab\", ui.newTab.index().toString());" + 
					"}" + 
					"})" + 
					".css(\"visibility\", \"visible\");");
		}

		return super.toString(_destroy);
	}
	
}
