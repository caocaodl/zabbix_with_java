package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NOT_ZERO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.P_UNSET_EMPTY;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ReportsUtil.validateBarReportItems;
import static com.isoft.iradar.inc.ReportsUtil.validateBarReportPeriods;
import static com.isoft.iradar.inc.ReportsUtil.valueComparisonFormForMultiplePeriods;
import static com.isoft.iradar.inc.ReportsUtil.valueDistributionFormForMultipleItems;
import static com.isoft.iradar.inc.ReportsUtil.valueDistributionFormForMultiplePeriods;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Report6Action extends RadarBaseAction {
	
	private CArray<Map> items;
	private boolean isValid;
	
	@Override
	protected void doInitPage() {
		page("title", _("Bar reports"));
		page("file", "report6.action");
		page("hist_arg", new String[] { "period" });
		page("scripts", new String[]{"class.calendar.js"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"config",	            	array(T_RDA_INT, O_OPT,	P_SYS,			IN("0,1,2,3"),	null),
			"groupid",					array(T_RDA_INT, O_OPT,	P_SYS,			DB_ID,			null),
			"hostids" ,					array(T_RDA_INT, O_OPT,	null,			DB_ID, "isset({config})&&({config}==3)&&isset({report_show})&&!isset({groupids})"),
			"groupids" ,				array(T_RDA_INT, O_OPT,	null,			DB_ID, "isset({config})&&({config}==3)&&isset({report_show})&&!isset({hostids})"),
			"itemid" ,					array(T_RDA_INT, O_OPT, null,			DB_ID+NOT_ZERO, "isset({config})&&({config}==3)&&isset({report_show})"),
			"items" ,					array(T_RDA_STR, O_OPT,	null,			NOT_EMPTY, "isset({report_show})&&!isset({delete_period})&&(isset({config})&&({config}!=3)||!isset({config}))", _("Items")),
			"new_graph_item" ,	array(T_RDA_STR, O_OPT,	null,			null,			null),
			"group_gid" ,			array(T_RDA_STR, O_OPT,	null,			null,			null),
			"title" ,						array(T_RDA_STR, O_OPT,	null,			null,			null),
			"xlabel" ,					array(T_RDA_STR, O_OPT,	null,			null,			null),
			"ylabel" ,					array(T_RDA_STR, O_OPT,	null,			null,			null),
			"showlegend" ,			array(T_RDA_STR, O_OPT,	null,			null,			null),
			"sorttype" ,				array(T_RDA_INT, O_OPT,	null,			null,			null),
			"scaletype" ,				array(T_RDA_INT, O_OPT,	null,			null,			null),
			"avgperiod" ,			array(T_RDA_INT, O_OPT,	null,			null,			null),
			"periods" ,			    array(T_RDA_STR, O_OPT,	null,			null, "isset({report_show})&&!isset({delete_item})&&(isset({config})&&({config}==2))",_("Period")),
			"new_period" ,			array(T_RDA_STR, O_OPT,	null,			null,			null),
			"group_pid" ,			array(T_RDA_STR, O_OPT,	null,			null,			null),
			"palette" ,					array(T_RDA_INT, O_OPT,	null,			null,			null),
			"palettetype" ,			array(T_RDA_INT, O_OPT,	null,			null,			null),
			// actions
			"delete_item" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,			null),
			"delete_period" ,		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,			null),
			// filter
			"report_show" ,			array(T_RDA_STR, O_OPT,	P_SYS,			null,			null),
			"report_timesince" ,	array(T_RDA_INT, O_OPT,	P_UNSET_EMPTY,	null,			null),
			"report_timetill" ,		array(T_RDA_INT, O_OPT,	P_UNSET_EMPTY,	null,			null),
			// ajax
			"favobj" ,					array(T_RDA_STR, O_OPT, P_ACT,			null,			null),
			"favref" ,					array(T_RDA_STR, O_OPT, P_ACT,			NOT_EMPTY,		"isset({favobj})"),
			"favstate" ,				array(T_RDA_INT, O_OPT, P_ACT,			NOT_EMPTY, "isset({favobj})&&(\"filter\"=={favobj})")
		);
		isValid = check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (isset(_REQUEST,"new_graph_item")) {
			Nest.value(_REQUEST,"items").$(get_request("items", array()));
			Map newItem = get_request("new_graph_item", array());

			Boolean itemExists = null;
			for(Map item : (CArray<Map>)Nest.value(_REQUEST,"items").asCArray()){
				if ((bccomp(Nest.value(newItem,"itemid").$(), Nest.value(item,"itemid").$()) == 0)
						&& StringUtils.equals(Nest.value(newItem,"calc_fnc").asString(), Nest.value(item,"calc_fnc").asString())
						&& StringUtils.equals(Nest.value(newItem,"caption").asString(), Nest.value(item,"caption").asString())) {
					itemExists = true;
					break;
				}
			}

			if (!isset(itemExists)) {
				array_push(Nest.value(_REQUEST,"items").asCArray(), CArray.valueOf(newItem));
			}
		}

		// validate permissions		
		if (Nest.as(get_request("config")).asInteger() == 3) {
			if (!empty(get_request("groupid")) && empty(API.HostGroup(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"groupid").asLong()))) {
				access_deny();
			}
			if (!empty(get_request("groupids")) && empty(API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupids").asLong()))) {
				access_deny();
			}
			if (!empty(get_request("hostids")) && empty(API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostids").asLong()))) {
				access_deny();
			}
		
			if (!empty(get_request("itemid"))) {
				CItemGet ioptions = new CItemGet();
				ioptions.setItemIds(Nest.value(_REQUEST,"itemid").asLong());
				ioptions.setWebItems(true);
				ioptions.setOutput(new String[]{"itemid"});
				items = API.Item(getIdentityBean(), executor).get(ioptions);
				if (empty(items)) {
					access_deny();
				}
			}
		} else {
			if (!empty(get_request("items",array())) && count(Nest.value(_REQUEST,"items").asCArray()) > 0) {
				Long[] itemIds = rda_objectValues(Nest.value(_REQUEST,"items").asCArray(), "itemid").valuesAsLong();
				CItemGet ioptions = new CItemGet();
				ioptions.setItemIds(itemIds);
				ioptions.setWebItems(true);
				ioptions.setCountOutput(true);
				long itemsCount = API.Item(getIdentityBean(), executor).get(ioptions);

				if (count(itemIds) != itemsCount) {
					access_deny();
				}
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if ((PAGE_TYPE_JS == Nest.value(page,"type").asInteger()) || (PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger())) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor,"web.report6.filter.state",Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}

		if (isset(_REQUEST,"delete_item") && isset(_REQUEST,"group_gid")) {
			CArray items = Nest.value(_REQUEST,"items").asCArray();
			for(Object gid : items.keySet().toArray()){
				if (!isset(Nest.value(_REQUEST,"group_gid",gid).$())) {
					continue;
				}
				unset(items, gid);
			}
			unset(_REQUEST,"delete_item");
			unset(_REQUEST,"group_gid");
		} else if (isset(_REQUEST,"new_period")) {
			Nest.value(_REQUEST,"periods").$(get_request("periods", array()));
			CArray newPeriod = get_request("new_period", array());
			Boolean periodExists = null;
			CArray<Map> periods = Nest.value(_REQUEST,"periods").asCArray(); 
			for(Map period : periods){
				Nest.value(period,"report_timesince").$(rdaDateToTime(Nest.value(period,"report_timesince").asString()));
				Nest.value(period,"report_timetill").$(rdaDateToTime(Nest.value(period,"report_timetill").asString()));

				if (Nest.value(newPeriod,"report_timesince").asLong() == Nest.value(period,"report_timesince").asLong()
						&& Nest.value(newPeriod,"report_timetill").asLong() == Nest.value(period,"report_timetill").asLong()) {
					periodExists = true;
					break;
				}
			}

			if (!isset(periodExists)) {
				array_push(Nest.value(_REQUEST,"periods").asCArray(), newPeriod);
			}
		}else if (isset(_REQUEST,"delete_period") && isset(_REQUEST,"group_pid")) {
			CArray periods = Nest.value(_REQUEST,"periods").asCArray();
			for(Object  pid : periods.keySet().toArray()){
				if (!isset(Nest.value(_REQUEST,"group_pid",pid).$())) {
					continue;
				}
				unset(periods,pid);
			}
			unset(_REQUEST,"delete_period");
			unset(_REQUEST,"group_pid");
		}

		// item validation
		int config = get_request("config", 1);
		Nest.value(_REQUEST,"config").$(config);

		// items array validation
		CArray<Map> validItems = null;
		CArray<Map> validPeriods = null;
		if (config != 3) {
			CArray<Map> items = get_request("items", array());
			
			validItems = validateBarReportItems(this.getIdentityBean(), executor,items);

			if (config == 2) {
				validPeriods = validateBarReportPeriods(this.getIdentityBean(), get_request("periods",array()));
			}
		}
		
		Nest.value(_REQUEST,"report_timesince").$(rdaDateToTime(get_request("report_timesince",
				date(TIMESTAMP_FORMAT_ZERO_TIME, time() - SEC_PER_DAY))));
		Nest.value(_REQUEST,"report_timetill").$(rdaDateToTime(get_request("report_timetill",
				date(TIMESTAMP_FORMAT_ZERO_TIME, time()))));

		CWidget rep6_wdgt = new CWidget();

		CForm r_form = new CForm();
		CComboBox cnfCmb = new CComboBox("config", config, "submit();");
		cnfCmb.addItem(1, _("Distribution of values for multiple periods"));
		cnfCmb.addItem(2, _("Distribution of values for multiple items"));
		cnfCmb.addItem(3, _("Compare values for multiple periods"));

		r_form.addItem(array(_("Reports")+SPACE, cnfCmb));

		rep6_wdgt.addPageHeader(_("Bar reports"));
		rep6_wdgt.addHeader(_("Report"), r_form);
		rep6_wdgt.addItem(BR());

		CTable rep_tab = new CTable();
		rep_tab.setCellPadding(3);
		rep_tab.setCellSpacing(3);

		rep_tab.setAttribute("border", 0);

		CFormTable rep_form = null;
		switch (config) {
			default:
			case 1:
				rep_form = valueDistributionFormForMultiplePeriods(this.getIdentityBean(), executor,validItems,null);
				break;
			case 2:
				rep_form = valueDistributionFormForMultipleItems(this.getIdentityBean(), executor,validItems, validPeriods,null);
				break;
			case 3:
				rep_form = valueComparisonFormForMultiplePeriods(this.getIdentityBean(), executor);
				break;
		}

		rep6_wdgt.addFlicker(rep_form, Nest.as(CProfile.get(getIdentityBean(), executor,"web.report6.filter.state", 1)).asInteger());

		if (isset(_REQUEST,"report_show")) {
			items = (config == 3)
				? array(map("itemid",get_request("itemid")))
				: get_request("items",array());
				
			if (isValid  && ((config != 3) ? !empty(validItems) : true) && ((config == 2) ? !empty(validPeriods) : true)) {
				String src = "chart_bar.action?"+
					"config="+config+
					url_param(getIdentityBean(), "title")+
					url_param(getIdentityBean(), "xlabel")+
					url_param(getIdentityBean(), "ylabel")+
					url_param(getIdentityBean(), "scaletype")+
					url_param(getIdentityBean(), "avgperiod")+
					url_param(getIdentityBean(), "showlegend")+
					url_param(getIdentityBean(), "sorttype")+
					url_param(getIdentityBean(), "report_timesince")+
					url_param(getIdentityBean(), "report_timetill")+
					url_param(getIdentityBean(), "periods")+
					url_param(getIdentityBean(), items, false, "items")+
					url_param(getIdentityBean(), "hostids")+
					url_param(getIdentityBean(), "groupids")+
					url_param(getIdentityBean(), "palette")+
					url_param(getIdentityBean(), "palettetype");

				rep_tab.addRow(new CImg(src, "report"));
			}
		}

		CTable outer_table = new CTable();

		outer_table.setAttribute("border", 0);
		outer_table.setAttribute("width", "100%");

		outer_table.setCellPadding(1);
		outer_table.setCellSpacing(1);

		CRow tmp_row = new CRow(rep_tab);
		tmp_row.setAttribute("align", "center");

		outer_table.addRow(tmp_row);

		rep6_wdgt.addItem(outer_table);
		rep6_wdgt.show();
	}

}
