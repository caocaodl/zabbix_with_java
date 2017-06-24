package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.$_REQUEST;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_LEFT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_RIGHT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_HOURLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_YEARLY;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.ValidateUtil.validateUnixTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CColorCell;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.validators.CColorValidator;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ReportsUtil {

	private ReportsUtil(){
		
	}
	
	public static CFormTable valueDistributionFormForMultiplePeriods(IIdentityBean idBean, SQLExecutor executor){
		return valueDistributionFormForMultiplePeriods(idBean, executor, array(),null);
	}
	
	/**
	 * Create bar report form for \"Distribution of values for multiple periods\".
	 * @param executor
	 * @param _group_gid  default null
	 * @param _items
	 * @return object _reportForm
	 */
	public static CFormTable valueDistributionFormForMultiplePeriods(IIdentityBean idBean, SQLExecutor executor,CArray<Map> _items,CArray _group_gid){
		
		int _config = asInteger(get_request("config", 1));
		int _scaletype = asInteger(get_request("scaletype", TIMEPERIOD_TYPE_WEEKLY));

		String _title = get_request("title", _("Report 1"));
		String _xlabel = get_request("xlabel", "");
		String _ylabel = get_request("ylabel", "");
		boolean _showlegend = Nest.as(get_request("showlegend", 0)).asBoolean();

		long _report_timesince = Nest.value(Cphp.$_REQUEST(),"report_timesince").asLong();
		long _report_timetill =  Nest.value(Cphp.$_REQUEST(),"report_timetill").asLong();

		CFormTable _reportForm = new CFormTable(null, null, "get");
		_reportForm.setAttribute("name", "rda_report");
		_reportForm.setAttribute("id", "rda_report");

		if (isset(Nest.value(Cphp.$_REQUEST(),"report_show").$()) && Cphp.is_array(_items) && !empty(_items)) {
			_reportForm.addVar("report_show", "show");
		}

		_reportForm.addVar("config", _config);

		_reportForm.addVar("report_timesince", Cphp.date(TIMESTAMP_FORMAT_ZERO_TIME, _report_timesince));
		_reportForm.addVar("report_timetill", Cphp.date(TIMESTAMP_FORMAT_ZERO_TIME, _report_timetill));

		_reportForm.addRow(_("Title"), new CTextBox("title", _title, 40));
		_reportForm.addRow(_("X label"), new CTextBox("xlabel", _xlabel, 40));
		_reportForm.addRow(_("Y label"), new CTextBox("ylabel", _ylabel, 40));
		_reportForm.addRow(_("Legend"), new CCheckBox("showlegend", _showlegend, null, 1));

		CComboBox _scale = new CComboBox("scaletype", _scaletype);
		_scale.addItem(TIMEPERIOD_TYPE_HOURLY, _("Hourly"));
		_scale.addItem(TIMEPERIOD_TYPE_DAILY, 	_("Daily"));
		_scale.addItem(TIMEPERIOD_TYPE_WEEKLY,	_("Weekly"));
		_scale.addItem(TIMEPERIOD_TYPE_MONTHLY, _("Monthly"));
		_scale.addItem(TIMEPERIOD_TYPE_YEARLY,	_("Yearly"));
		_reportForm.addRow(_("Scale"), _scale);

		CTable _reporttimetab = new CTable(null, "calendar");

		CArray _timeSinceRow = HtmlUtil.createDateSelector("report_timesince", _report_timesince, "report_timetill");
		Cphp.array_unshift(_timeSinceRow, _("From"));
		_reporttimetab.addRow(_timeSinceRow);

		CArray _timeTillRow = HtmlUtil.createDateSelector("report_timetill", _report_timetill, "report_timesince");
		Cphp.array_unshift(_timeTillRow, _("Till"));
		_reporttimetab.addRow(_timeTillRow);

		_reportForm.addRow(_("Period"), _reporttimetab);
		
		CTableInfo _items_table ;
		CSubmit _delete_button;
		if (!empty(_items)) {
			_items = CMacrosResolverHelper.resolveItemNames(idBean, executor,_items);

			_items_table = new CTableInfo();
			Map _item ;
			for(Entry<Object, Map> e : _items.entrySet()){
				Object _id = e.getKey();
				_item = e.getValue();
				
				CColorCell _color = new CColorCell(null, Nest.value(_item,"color").asString());

				CSpan _caption = new CSpan(Nest.value(_item,"caption").$(), "link");
				_caption.onClick("return PopUp(\"popup_bitem.action?"+
					"config=1"+
					"&list_name=items"+
					"&dstfrm="+_reportForm.getName()+
					HtmlUtil.url_param(idBean, _item, false)+
					HtmlUtil.url_param(idBean, _id, false, "gid")+
					"\", 550, 400, \"graph_item_form\");"
				);

				String _description = Nest.value(_item,"host","name").$()+Defines.NAME_DELIMITER+Nest.value(_item,"name_expanded").$();

				_items_table.addRow(array(
					new CCheckBox("group_gid["+_id+"]", isset(Nest.value(_group_gid,_id))),
					_caption,
					_description,
					GraphsUtil.graph_item_calc_fnc2str(Nest.value(_item,"calc_fnc").asInteger()),
					(Nest.value(_item,"axisside").asInteger() == Defines.GRAPH_YAXIS_SIDE_LEFT) ? _("Left") : _("Right"),
					_color
				));

				// once used, unset unnecessary fields so they don't pass to URL
				Cphp.unset(Nest.value(_item,"value_type").asCArray(), Nest.value(_item,"host").$(), Nest.value(_item,"name").$(), Nest.value(_item,"name_expanded").$());
			}
			//Cphp.unset(_item);

			_reportForm.addVar("items", _items);

		    _delete_button = new CSubmit("delete_item", _("Delete selected"));
		} else {
			_items_table =   null;
			_delete_button = null;
		}

		_reportForm.addRow(_("Items"), array(
			_items_table,
			new CButton("add_item", _("Add"),
				"return PopUp(\"popup_bitem.action?config=1&dstfrm="+_reportForm.getName()+
				"\", 800, 400, \"graph_item_form\");"),
			_delete_button
		));
		
		//unset(_items_table, _delete_button);

		_reportForm.addItemToBottomRow(new CSubmit("report_show", _("Show")));

		CButton _reset = new CButton("reset", _("Reset"));
		_reset.setType("reset");
		_reportForm.addItemToBottomRow(_reset);

		return _reportForm;
	}
	
	public static CFormTable valueDistributionFormForMultipleItems(IIdentityBean idBean, SQLExecutor executor){
		return valueDistributionFormForMultipleItems(idBean, executor,array(),array(),null);
	}
	
	/**
	 * Create bar report form for \"Distribution of values for multiple items\".
     *
	 * @param executor
	 * @param _items
	 * @param _periods
	 * @param _group_gid default null
	 * @return CFormTable
	 */
	public static CFormTable valueDistributionFormForMultipleItems(IIdentityBean idBean, SQLExecutor executor,CArray<Map> _items, CArray<Map> _periods,CArray _group_gid){
		int _config = get_request("config", 1);

		String _title = get_request("title", _("Report 2"));
		String _xlabel = get_request("xlabel", "");
		String _ylabel = get_request("ylabel", "");

		int _sorttype = get_request("sorttype", 0);
		boolean _showlegend = Nest.as(get_request("showlegend", 0)).asBoolean();

		CFormTable _reportForm = new CFormTable(null, null, "get");
		_reportForm.setAttribute("name", "rda_report");
		_reportForm.setAttribute("id", "rda_report");

		if (isset(Nest.value(Cphp.$_REQUEST(),"report_show").asCArray()) && Cphp.is_array(_items) && !empty(_items) && Cphp.is_array(_periods) && !empty(_periods)) {
			_reportForm.addVar("report_show", "show");
		}

		_reportForm.addVar("config", _config);

		_reportForm.addRow(_("Title"), new CTextBox("title", _title, 40));
		_reportForm.addRow(_("X label"), new CTextBox("xlabel", _xlabel, 40));
		_reportForm.addRow(_("Y label"), new CTextBox("ylabel", _ylabel, 40));

		_reportForm.addRow(_("Legend"), new CCheckBox("showlegend", _showlegend, null, 1));

		if (Cphp.count(_periods) < 2) {
			CComboBox _sortCmb = new CComboBox("sorttype", _sorttype);
				_sortCmb.addItem(0, _("Name"));
				_sortCmb.addItem(1, _("Value"));

			_reportForm.addRow(_("Sort by"), _sortCmb);
		}else {
			_reportForm.addVar("sortorder", 0);
		}
		CSubmit _delete_button ;
		CTableInfo _periods_table;
		if (Cphp.is_array(_periods) && !empty(_periods)) {
		    _periods_table = new CTableInfo();
			for(Entry<Object, Map> entry : _periods.entrySet()){
				Object _pid = entry.getKey();
				Map _period = entry.getValue();
				CColorCell _color = new CColorCell(null, Nest.value(_period,"color").asString(),null);

				String _edit_link = "popup_period.action?"+
					"period_id="+_pid+
					"&config=2"+
					"&dstfrm="+_reportForm.getName()+
					"&caption="+Nest.value(_period,"caption")+
					"&report_timesince="+Nest.value(_period,"report_timesince")+
					"&report_timetill="+Nest.value(_period,"report_timetill")+
					"&color="+Nest.value(_period,"color").asString();

				CSpan _caption = new CSpan(Nest.value(_period,"caption").$(), "link");
				_caption.addAction("onclick", "\"return PopUp(\""+_edit_link+"\",840,340,period_form);");

				_periods_table.addRow(array(
					new CCheckBox("group_pid["+_pid+"]"),
					_caption,
					FuncsUtil.rda_date2str(TranslateDefines.REPORTS_BAR_REPORT_DATE_FORMAT, Nest.value(_period,"report_timesince").asLong()),
					FuncsUtil.rda_date2str(TranslateDefines.REPORTS_BAR_REPORT_DATE_FORMAT, Nest.value(_period,"report_timetill").asLong()),
					_color
				));
			}

			_reportForm.addVar("periods", _periods);

		    _delete_button = new CSubmit("delete_period", _("Delete selected"));

		}
		else {
			_periods_table = null;
			_delete_button = null;
		}

		_reportForm.addRow(_("Period"), array(
			_periods_table,
			new CButton("add_period", _("Add"),
				"return PopUp('popup_period.action?config=2&dstfrm="+_reportForm.getName()+"', 840, 340, 'period_form');"),
			_delete_button
		));
		//unset(_periods_table, _delete_button);
		CTableInfo _items_table ;
		if (!empty(_items)) {
			_items = CMacrosResolverHelper.resolveItemNames(idBean, executor,_items);

			_items_table = new CTableInfo();
			
			Map _item;
			for(Entry<Object, Map> e : _items.entrySet()){
				Object _id = e.getKey();
				_item  = e.getValue();
				CSpan _caption = new CSpan(Nest.value(_item,"caption").$(), "link");
				_caption.onClick("return PopUp(\"popup_bitem.action?"+
					"config=2"+
					"&list_name=items"+
					"&dstfrm="+_reportForm.getName()+
					HtmlUtil.url_param(idBean, _item, false)+
					HtmlUtil.url_param(idBean, _id, false, "gid")+
					"\", 550, 400, \"graph_item_form\");"
				);

				String _description = Nest.value(_item,"host","name").$()+Defines.NAME_DELIMITER+Nest.value(_item,"name_expanded").asString();

				_items_table.addRow(array(
					new CCheckBox("group_gid["+_id+"]", isset(Nest.value(_group_gid,_id))),
					_caption,
					_description,
					GraphsUtil.graph_item_calc_fnc2str(Nest.value(_item,"calc_fnc").asInteger())
				));

				// once used, unset unnecessary fields so they don't pass to URL. \"color\" goes in \"periods\" parameter.
				Cphp.unset(Nest.value(_item,"value_type").asCArray(), Nest.value(_item,"host").$(), Nest.value(_item,"name").$(), Nest.value(_item,"name_expanded").$(), Nest.value(_item,"color").$());
			}
			//unset(_item);

			_reportForm.addVar("items", _items);

			_delete_button = new CSubmit("delete_item", _("Delete selected"));
		}
		else {
			_items_table = null;
			_delete_button = null;
		}

		_reportForm.addRow(_("Items"), array(
			_items_table,
			new CButton("add_item",_("Add"),
				"return PopUp('popup_bitem.action?config=2&dstfrm="+_reportForm.getName()+"', 550, 400, 'graph_item_form');"),
			_delete_button
		));
		//unset(_items_table, _delete_button);

		_reportForm.addItemToBottomRow(new CSubmit("report_show", _("Show")));

		CButton _reset = new CButton("reset", _("Reset"));
		_reset.setType("reset");
		_reportForm.addItemToBottomRow(_reset);

		return _reportForm;
	}

	/**
	 * Create report bar for for \"Compare values for multiple periods\"
	 * @param executor
	 * @return object _reportForm
	 */
	public static CFormTable valueComparisonFormForMultiplePeriods(IIdentityBean idBean, SQLExecutor executor) {
		int _config = get_request("config", 1);

		String _title = get_request("title", _("Report 3"));
		String _xlabel = get_request("xlabel", "");
		String _ylabel = get_request("ylabel", "");

		int _scaletype = get_request("scaletype", TIMEPERIOD_TYPE_WEEKLY);
		int _avgperiod = get_request("avgperiod", TIMEPERIOD_TYPE_DAILY);

		long _report_timesince = Long.valueOf(get_request("report_timesince", date(TIMESTAMP_FORMAT_ZERO_TIME, (time() - SEC_PER_DAY))));
		long _report_timetill = Long.valueOf(get_request("report_timetill", date(TIMESTAMP_FORMAT_ZERO_TIME)));

		int _itemId = get_request("itemid", 0);

		CArray<String> _hostids = get_request("hostids", array());
		_hostids = rda_toHash(_hostids);
		boolean _showlegend = Nest.as(get_request("showlegend", 0)).asBoolean();

		int _palette = get_request("palette", 0);
		int _palettetype = get_request("palettetype", 0);

		CFormTable _reportForm = new CFormTable(null,null,"get");
		_reportForm.setAttribute("name","rda_report");
		_reportForm.setAttribute("id","rda_report");

		if (isset(Nest.value($_REQUEST(),"report_show").$()) && !empty(_itemId)) {
			_reportForm.addVar("report_show","show");
		}

		_reportForm.addVar("config", _config);
		_reportForm.addVar("report_timesince", date(TIMESTAMP_FORMAT, Long.valueOf(_report_timesince)));
		_reportForm.addVar("report_timetill", date(TIMESTAMP_FORMAT, Long.valueOf(_report_timetill)));

		_reportForm.addRow(_("Title"), new CTextBox("title", _title, 40));
		_reportForm.addRow(_("X label"), new CTextBox("xlabel", _xlabel, 40));
		_reportForm.addRow(_("Y label"), new CTextBox("ylabel", _ylabel, 40));

		_reportForm.addRow(_("Legend"), new CCheckBox("showlegend", _showlegend, null, 1));
		_reportForm.addVar("sortorder", 0);

		CArray<Map> _groupids = get_request("groupids", array());
		CTweenBox _group_tb = new CTweenBox(_reportForm, "groupids", _groupids, 10);

		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setRealHosts(true);
		hgoptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> _db_groups = API.HostGroup(idBean, executor).get(hgoptions);
		order_result(_db_groups, "name");
		for(Entry<Object,Map> e : _db_groups.entrySet()){// as _gnum => _group) {
			//Object _gnum = e.getKey();
			Map _group   = e.getValue();
			Nest.value(_groupids,Nest.value(_group,"groupid")).$(Nest.value(_group,"groupid").$());
			_group_tb.addItem(Nest.value(_group,"groupid").$(),Nest.value(_group,"name").asString());
		}

		_reportForm.addRow(_("Groups"), _group_tb.get(_("Selected groups"), _("Other groups")));

		Long _groupid = get_request("groupid", 0L);
		CComboBox _cmbGroups = new CComboBox("groupid", _groupid, "submit()");
		_cmbGroups.addItem(0, _("All"));
		for(Entry<Object,Map> e : _db_groups.entrySet()){
			//Object _gnum = e.getKey();
			Map _group   = e.getValue();
			_cmbGroups.addItem(Nest.value(_group,"groupid").$(), Nest.value(_group,"name").asString());
		}

		CCol _td_groups = new CCol(array(_("Group"), SPACE, _cmbGroups));
		_td_groups.setAttribute("style", "text-align: right;");

		CTweenBox _host_tb = new CTweenBox(_reportForm, "hostids", _hostids, 10);

		CHostGet hoptions = new CHostGet();
		hoptions.put("real_hosts" , true);
		hoptions.setOutput(new String[]{"hostid", "name"});
		if (_groupid > 0L) {
			hoptions.setGroupIds(_groupid);
		}
		CArray<Map> _db_hosts = API.Host(idBean, executor).get(hoptions);
		_db_hosts = rda_toHash(_db_hosts, "hostid");
		order_result(_db_hosts, "name");

		for(Entry<Object,Map> entry:_db_hosts.entrySet()){
			//Object _hnum = entry.getKey();
			Map    _host = entry.getValue();
			_host_tb.addItem(Nest.value(_host,"hostid").$(),Nest.value(_host,"name").asString());
		}

		hoptions = new CHostGet();
		hoptions.put("real_hosts" , true);
		hoptions.setOutput(new String[]{"hostid", "name"});
		hoptions.setHostIds(_hostids.valuesAsLong());
		CArray<Map> _db_hosts2 = API.Host(idBean, executor).get(hoptions);
		order_result(_db_hosts2, "name");
		for(Entry<Object,Map> entry :_db_hosts2.entrySet()){ 
			//Object _hnum = entry.getKey();
			Map    _host = entry.getValue();
			if (!isset(Nest.value(_db_hosts,Nest.value(_host,"hostid").$()).$())) {
				_host_tb.addItem(Nest.value(_host,"hostid").$(), Nest.value(_host,"name").asString());
			}
		}

		_reportForm.addRow(_("Hosts"),
			_host_tb.get(_("Selected hosts"),
			array(_("Other hosts | Group")+SPACE, _cmbGroups)
		));

		CTable _reporttimetab = new CTable(null,"calendar");

		CArray _timeSinceRow = createDateSelector("report_timesince", _report_timesince, "report_timetill");
		array_unshift(_timeSinceRow, _("From"));
		_reporttimetab.addRow(_timeSinceRow);

		CArray _timeTillRow = createDateSelector("report_timetill", _report_timetill, "report_timesince");
		array_unshift(_timeTillRow, _("Till"));
		_reporttimetab.addRow(_timeTillRow);

		_reportForm.addRow(_("Period"), _reporttimetab);

		CComboBox _scale = new CComboBox("scaletype", _scaletype);
		_scale.addItem(TIMEPERIOD_TYPE_HOURLY, _("Hourly"));
		_scale.addItem(TIMEPERIOD_TYPE_DAILY, _("Daily"));
		_scale.addItem(TIMEPERIOD_TYPE_WEEKLY, _("Weekly"));
		_scale.addItem(TIMEPERIOD_TYPE_MONTHLY, _("Monthly"));
		_scale.addItem(TIMEPERIOD_TYPE_YEARLY, _("Yearly"));
		_reportForm.addRow(_("Scale"), _scale);

		CComboBox _avgcmb = new CComboBox("avgperiod", _avgperiod);
		_avgcmb.addItem(TIMEPERIOD_TYPE_HOURLY, _("Hourly"));
		_avgcmb.addItem(TIMEPERIOD_TYPE_DAILY, _("Daily"));
		_avgcmb.addItem(TIMEPERIOD_TYPE_WEEKLY, _("Weekly"));
		_avgcmb.addItem(TIMEPERIOD_TYPE_MONTHLY, _("Monthly"));
		_avgcmb.addItem(TIMEPERIOD_TYPE_YEARLY, _("Yearly"));
		_reportForm.addRow(_("Average by"), _avgcmb);

		String _itemName = "";
		if (Boolean.valueOf(String.valueOf(_itemId))) {
			CArray _items = CMacrosResolverHelper.resolveItemNames(idBean, executor,array(ItemsUtil.get_item_by_itemid(executor,String.valueOf(_itemId))));
			CArray _item = reset(_items);

			_itemName = (String) Nest.value(_item,"name_expanded").$();
		}

		CVar _itemidVar = new CVar("itemid", _itemId, "itemid");
		_reportForm.addItem(_itemidVar);

		CTextBox _txtCondVal = new CTextBox("item_name", _itemName, 50, true);
		_txtCondVal.setAttribute("id", "item_name");

		CButton _btnSelect = new CButton("btn1", _("Select"),
			"return PopUp(\"popup.action?dstfrm="+_reportForm.getName()+
				"&dstfld1=itemid"+
				"&dstfld2=item_name"+
				"&srctbl=items"+
				"&srcfld1=itemid"+
				"&srcfld2=name"+
				"&monitored_hosts=1\");",
			"T"
		);

		_reportForm.addRow(_("Item"), array(_txtCondVal, _btnSelect));

		CComboBox _paletteCmb = new CComboBox("palette", _palette);
		_paletteCmb.addItem(0, _s("Palette #%1$s", 1));
		_paletteCmb.addItem(1, _s("Palette #%1$s", 2));
		_paletteCmb.addItem(2, _s("Palette #%1$s", 3));
		_paletteCmb.addItem(3, _s("Palette #%1$s", 4));

		CComboBox _paletteTypeCmb = new CComboBox("palettetype", _palettetype);
		_paletteTypeCmb.addItem(0, _("Middle"));
		_paletteTypeCmb.addItem(1, _("Darken"));
		_paletteTypeCmb.addItem(2, _("Brighten"));

		_reportForm.addRow(_("Palette"), array(_paletteCmb, _paletteTypeCmb));
		_reportForm.addItemToBottomRow(new CSubmit("report_show", _("Show")));

		CButton _reset = new CButton("reset", _("Reset"));
		_reset.setType("reset");
		_reportForm.addItemToBottomRow(_reset);

		return _reportForm;
	}
	
	public static CArray<Map> validateBarReportItems(IIdentityBean idBean, SQLExecutor executor) {
		return validateBarReportItems(idBean, executor, array());
	}

	/**
	 * Validate items array for bar reports - IDs, color, permissions, etc.
	 * Color validation for graph items is only for \"Distribution of values for multiple periods\" section (_config == 1).
	 * Automatically set caption like item name if none is set. If no axis side is set, set LEFT side as default.
	 * 
	 * @param executor
	 * @param _items
	 * @return
	 */
	public static CArray<Map> validateBarReportItems(IIdentityBean idBean, SQLExecutor executor,CArray<Map> _items ) {
		int _config = get_request("config", 1);

		if (!isset(_items) || empty(_items)) {
			return null;
		}

		CArray<String> _fields = array("itemid", "calc_fnc");
		if (_config == 1) {
			array_push(_fields, "color");
		}
		CArray _itemIds = new CArray();
        for(Map _item : _items){
			for(String _field : _fields) {
				if (!isset(Nest.value(_item,_field))) {
					show_error_message(_s("Missing \"%1$s\" field for item.", _field));
					return null;
				}
			}

			Nest.value(_itemIds,Nest.value(_item,"itemid").$()).$(Nest.value(_item,"itemid").$());
		}

        CItemGet ioptions = new CItemGet();
        ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_", "value_type"});
        ioptions.setSelectHosts(new String[]{"name"});
        ioptions.setWebItems(true);
        ioptions.setItemIds(_itemIds.valuesAsLong());
        ioptions.setPreserveKeys(true);
		CArray<Map> _validItems = API.Item(idBean, executor).get(ioptions);

		_items = rda_toHash(_items, "itemid");

		CArray _allowedValueTypes = array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64);
		
		for(Map _item : _validItems) {
			//TODO in_CArray
			//if (!in_array(Nest.value(_item,"value_type").$(), _allowedValueTypes)) {
			if (empty(in_array(Nest.value(_item,"value_type").$(), _allowedValueTypes))) {
				show_error_message(_s("Cannot add a non-numeric item \"%1$s\".", Nest.value(_item,"name").$()));
				return null;
			}

			// add host name and set caption like item name for valid items
			Nest.value(_item,"host").$(reset(Nest.value(_item,"hosts").asCArray()));
			unset(Nest.value(_item,"hosts").asCArray());

			// if no caption set, set default caption like item name
			if (!isset(Nest.value(_items,Nest.value(_item,"itemid").$(),"caption").$()) || rda_empty(Nest.value(_items,Nest.value(_item,"itemid").$(),"caption").$())) {
				Nest.value(_item,"caption").$(Nest.value(_item,"name").$());
			}
			else {
				Nest.value(_validItems,Nest.value(_item,"itemid").$(),"caption").$(Nest.value(_items,Nest.value(_item,"itemid").$(),"caption").$());
			}

			if (!isset(Nest.value(_items,Nest.value(_item,"itemid").$(),"axisside").$()) || rda_empty(Nest.value(_items,Nest.value(_item,"itemid").$(),"axisside").$())) {
				Nest.value(_items,Nest.value(_item,"itemid").$(),"axisside").$(GRAPH_YAXIS_SIDE_LEFT);
			}
		}
		//unset(_item);

		// check axis value. 0 = count
		CSetValidator _calcFncValidator =  CValidator.init(new CSetValidator(),map(
			"values", array(0, CALC_FNC_MIN, CALC_FNC_AVG, CALC_FNC_MAX)
		));
		CValidator _axisValidator = CValidator.init(new CSetValidator(),map(
			"values" , array(GRAPH_YAXIS_SIDE_LEFT, GRAPH_YAXIS_SIDE_RIGHT)
		));
		CColorValidator _colorValidator = null ;
		if (_config == 1) {
			 _colorValidator = CValidator.init(new CColorValidator(),map());
		}
		for(Map _item : _items) {
			if (!_calcFncValidator.validate(idBean, Nest.value(_item,"calc_fnc").asString())) {
				show_error_message(_s("Incorrect value for field \"%1$s\".", "calc_fnc"));
				return null;
			}
			if (!_axisValidator.validate(idBean, Nest.value(_item,"axisside").$())) {
				show_error_message(_s("Incorrect value for field \"%1$s\".", "axisside"));
				return null;
			}
			if (_config == 1) {
				if (!_colorValidator.validate(idBean, Nest.value(_item,"color").asString())) {
					show_error_message(_colorValidator.getError());
					return null;
				}
				Nest.value(_validItems,Nest.value(_item,"itemid").$(),"color").$(Nest.value(_item,"color").$());
			}
			Nest.value(_validItems,Nest.value(_item,"itemid").$(),"calc_fnc").$(Nest.value(_item,"calc_fnc").$());
			Nest.value(_validItems,Nest.value(_item,"itemid").$(),"axisside").$(Nest.value(_item,"axisside").$());
		}

		return _validItems;
	}
	
	public static CArray validateBarReportPeriods(IIdentityBean idBean){
	  return validateBarReportPeriods(idBean, array());
	}

	public static CArray validateBarReportPeriods(IIdentityBean idBean, CArray<Map> _periods) {
		if (!isset(_periods) || empty(_periods)) {
			return null;
		}
		CArray<String> _fields = array("report_timesince", "report_timetill", "color");
		CColorValidator _colorValidator = CValidator.init(new CColorValidator(),map());

		for(Map _period : _periods) {
			for(String _field : _fields) {
				if (!isset(Nest.value(_period,_field).$()) || empty(Nest.value(_period,_field).$())) {
					show_error_message(_s("Missing \"%1$s\" field for period.", _field));
					return null;
				}
			}

			if (!_colorValidator.validate(idBean, Nest.value(_period,"color").asString())) {
				show_error_message(_colorValidator.getError());
				return null;
			}
			if (!validateUnixTime(Nest.value(_period,"report_timesince").asInteger())) {
				show_error_message(_s("Invalid period for field \"%1$s\".", "report_timesince"));
				return null;
			}
			if (!validateUnixTime(Nest.value(_period,"report_timetill").asInteger())) {
				show_error_message(_s("Invalid period for field \"%1$s\".", "report_timetill"));
				return null;
			}
			if (!isset(Nest.value(_period,"caption").$()) || rda_empty(Nest.value(_period,"caption").$())) {
				Nest.value(_period,"caption").$(rda_date2str(TranslateDefines.REPORTS_BAR_REPORT_DATE_FORMAT, Nest.value(_period,"report_timesince").asLong())+
						" - "+
					rda_date2str(TranslateDefines.REPORTS_BAR_REPORT_DATE_FORMAT, Nest.value(_period,"report_timetill").asLong()));
			}

		}
		//unset(_period);
		
		return _periods;
	}
	
}
