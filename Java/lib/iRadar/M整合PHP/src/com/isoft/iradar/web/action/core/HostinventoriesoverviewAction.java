package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostinventoriesoverviewAction extends RadarBaseAction {
	
	private CPageFilter pageFilter;
	private String groupFieldTitle;

	@Override
	protected void doInitPage() {
		page("title", _("Host inventory overview"));
		page("file", "hostinventoriesoverview.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",		array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,	null),
			"groupby",	array(T_RDA_STR, O_OPT,	P_SYS,	DB_ID,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor, "host_count", RDA_SORT_DOWN);
		if ((PAGE_TYPE_JS == Nest.value(page,"type").asInteger()) || (PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger())) {
			return true;
		}
		return false;
	}	

	@Override
	protected void doPageFilter(SQLExecutor executor) {
		CArray options = map(
			"groups", map(
				"real_hosts", 1
			),
			"groupid", get_request("groupid", null)
		);
		this.pageFilter = new CPageFilter(getIdentityBean(), executor, options);
		Nest.value(_REQUEST,"groupid").$(this.pageFilter.$("groupid").$());
		Nest.value(_REQUEST,"groupby").$(get_request("groupby", ""));
		this.groupFieldTitle = "";
	}

	@Override
	public void doAction(SQLExecutor executor) {
		CWidget hostinvent_wdgt = new CWidget();
		hostinvent_wdgt.addPageHeader(_("HOST INVENTORY OVERVIEW"));
		
		// getting inventory fields to make a drop down
		CArray<Map> inventoryFields = getHostInventories(true); // "true" means list should be ordered by title
		CComboBox inventoryFieldsComboBox = new CComboBox("groupby", Nest.value(_REQUEST,"groupby").$(), "submit()");
		inventoryFieldsComboBox.addItem("", _("not selected"));
		for (Map inventoryField : inventoryFields) {
			inventoryFieldsComboBox.addItem(
				Nest.value(inventoryField,"db_field").$(),
				Nest.value(inventoryField,"title").asString(),
				Nest.value(_REQUEST,"groupby").$().equals(Nest.value(inventoryField,"db_field").$()) ? true : null // selected?
			);
			if(Nest.value(_REQUEST,"groupby").$().equals(Nest.value(inventoryField,"db_field").$())){
				this.groupFieldTitle = Nest.value(inventoryField,"title").asString();
			}
		}
		
		CForm r_form = new CForm("get");
		r_form.addItem(array(_("Group"), SPACE, this.pageFilter.getGroupsCB(), SPACE));
		r_form.addItem(array(_("Grouping by"), SPACE, inventoryFieldsComboBox));
		hostinvent_wdgt.addHeader(_("Hosts"), r_form);
		hostinvent_wdgt.addItem(BR());

		CTableInfo table = new CTableInfo(_("No hosts found."));
		table.setHeader(
			array(
				make_sorting_header("".equals(groupFieldTitle) ? _("Field") : groupFieldTitle, "inventory_field"),
				make_sorting_header(_("Host count"), "host_count")
			)
		);
		
		// to show a report, we will need a host group and a field to aggregate
		if(this.pageFilter.$("groupsSelected").asBoolean() && !"".equals(this.groupFieldTitle)){
			CHostGet options = new CHostGet();
			options.setOutput(new String[]{"hostid", "name"});
			options.setSelectInventory(new String[]{Nest.value(_REQUEST,"groupby").asString()});
			options.setWithInventory(true);
			
			if(this.pageFilter.$("groupid").asInteger() > 0){
				options.setGroupIds(this.pageFilter.$("groupid").asLong());
			}
			
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
			
			// aggregating data by chosen field value
			CArray<Map> report = array();
			for(Map host : hosts){
				String groupby = Nest.value(host,"inventory",_REQUEST.get("groupby")).asString();
				if(!"".equals(groupby)){
					String lowerValue = rda_strtolower(groupby);
					if(!isset(report,lowerValue)){
						Nest.value(report,lowerValue).$(map(
							"inventory_field", groupby,
							"host_count", 1
						));
					} else{
						Nest.value(report,lowerValue,"host_count").$(Nest.value(report,lowerValue,"host_count").asInteger()+1);
					}
				}
			}
			
			order_result(report, getPageSortField(getIdentityBean(), executor, "host_count"), getPageSortOrder(getIdentityBean(), executor));
			
			for(Map rep : report){
				CArray row = array(
					new CSpan(Nest.value(rep,"inventory_field").$(), "pre"),
					new CLink(Nest.value(rep,"host_count").$(),"hostinventories.action?filter_field="+Nest.value(_REQUEST,"groupby").asString()+"&filter_field_value="+urlencode(Nest.value(rep,"inventory_field").asString()+"&filter_set=1&filter_exact=1"+url_param(getIdentityBean(), "groupid")))
				);
				table.addRow(row);
			}
		}
		hostinvent_wdgt.addItem(table);
		hostinvent_wdgt.show();
	}

}
