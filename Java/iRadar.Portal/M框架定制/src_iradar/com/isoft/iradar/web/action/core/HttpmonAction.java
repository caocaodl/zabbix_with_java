package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HttpTestUtil.get_httpstep_by_no;
import static com.isoft.iradar.inc.HttpTestUtil.resolveHttpTestMacros;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class HttpmonAction extends RadarBaseAction {
	
	private CPageFilter pageFilter;
	
	@Override
	protected void doInitPage() {
		page("title", _("Status of Web monitoring"));
		page("file", getAction());
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("css", new String[] { "lessor/supervisecenter/webservice.css" });
		define("RDA_PAGE_DO_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR				TYPE		OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"fullscreen",	array(T_RDA_INT,	O_OPT,		P_SYS,	IN("0,1"),	null),
			"groupid",		array(T_RDA_INT,	O_OPT,		P_SYS,	DB_ID,		null),
			"hostid",		array(T_RDA_INT,	O_OPT,		P_SYS,	DB_ID,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_DOWN);
		
		CArray options = map(
			"groups", map(
				"real_hosts", true,
				"with_httptests", true
			),
			"hosts", map(
				"with_monitored_items", true,
				"with_httptests", true
			),
			"hostid", get_request("hostid", null),
			"groupid", get_request("groupid", null)
		);
		this.pageFilter = new CPageFilter(getIdentityBean(), executor, options);
		Nest.value(_REQUEST,"groupid").$(this.pageFilter.$("groupid").asString());
		Nest.value(_REQUEST,"hostid").$(this.pageFilter.$("hostid").asString());
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		CForm r_form = new CForm("get");
		r_form.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").$());
		r_form.addItem(array(_("Group")+SPACE,this.pageFilter.getGroupsCB()));
		r_form.addItem(array(SPACE+_("Host")+SPACE,this.pageFilter.getHostsCB()));
		
		CWidget httpmon_wdgt = new CWidget();
//		httpmon_wdgt.addPageHeader(
//			_("STATUS OF WEB MONITORING"),
//			get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen", Nest.value(_REQUEST,"fullscreen").$()))
//		);
		httpmon_wdgt.addHeader(null, r_form);
		httpmon_wdgt.addHeaderRowNumber();
		
		// TABLE
		CTableInfo table = new CTableInfo(_("No web scenarios found."));
		if(this.pageFilter.$("hostid").asInteger() > 0){
			table.setAttribute("class", table.getAttribute("class")+" normaldisplay");
		}
		table.setHeader(array(
			Nest.value(_REQUEST,"hostid").asInteger() == 0 ? make_sorting_header(_("Host"), "hostname") : null,
			make_sorting_header(_("Name"), "name"),
			_("Number of steps"),
			_("Last check"),
			_("Status")
		));
		
		Map config = select_config(getIdentityBean(), executor);
		CTable paging = null;
		
		if (!empty(this.pageFilter.$("hostsSelected").$())) {
			CHttpTestGet options = new CHttpTestGet();
			options.setOutput(new String[]{"httptestid"});
			options.setTemplated(false);
			options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			options.setEditable(true);
			if (this.pageFilter.$("hostid").asInteger() > 0) {
				options.setHostIds(this.pageFilter.$("hostid").asLong());
			} else if (this.pageFilter.$("groupid").asInteger() > 0) {
				options.setGroupIds(this.pageFilter.$("groupid").asLong());
			}
			
			options.setEditable(true);
			
			CArray<Map> httpTests = API.HttpTest(getIdentityBean(), executor).get(options);
			paging = getPagingLine(getIdentityBean(), executor, httpTests);
			
			options = new CHttpTestGet();
			options.setHttptestIds(rda_objectValues(httpTests, "httptestid").valuesAsLong());
			options.setPreserveKeys(true);
			options.setOutput(API_OUTPUT_EXTEND);
			options.setSelectHosts(new String[]{"name", "status"});
			options.setSelectSteps(API_OUTPUT_COUNT);
			options.setEditable(true);
			
			httpTests = API.HttpTest(getIdentityBean(), executor).get(options);
			
			for (Map httpTest : httpTests) {
				Nest.value(httpTest,"host").$(reset(Nest.value(httpTest,"hosts").asCArray()));
				Nest.value(httpTest,"hostname").$(Nest.value(httpTest,"host","name").$());
				unset(httpTest,"hosts");
			}
			
			resolveHttpTestMacros(getIdentityBean(), executor, httpTests, true, false);
			
			order_result(httpTests, getPageSortField(getIdentityBean(), executor, "name"), getPageSortOrder(getIdentityBean(), executor));
			
			// fetch the latest results of the web scenario
			CArray lastHttpTestData = Manager.HttpTest(getIdentityBean(), executor).getLastData(array_keys(httpTests).valuesAsLong());
			
			for (Map httpTest : httpTests) {
				Object httptestid = Nest.value(httpTest, "httptestid").$();
				String lastcheck;
				CArray status = new CArray();
				if (isset(lastHttpTestData.get(httptestid))
						&& Nest.value(lastHttpTestData,httptestid,"lastfailedstep").$() != null) {
					Map lastData = (Map)Nest.value(lastHttpTestData,httptestid).$();

					lastcheck = rda_date2str(_("d M Y H:i:s"), Nest.value(lastData,"lastcheck").asLong());

					if (Nest.value(lastData,"lastfailedstep").asInteger() != 0) {
						Map stepData = get_httpstep_by_no(executor, Nest.value(httpTest,"httptestid").asLong(), Nest.value(lastData,"lastfailedstep").asInteger());

						String error = (Nest.value(lastData,"error").$() == null) ? _("Unknown error") : Nest.value(lastData,"error").asString();

						if (stepData!=null) {
							Nest.value(status ,"msg").$(_s(
								"Step \"%1$s\" [%2$s of %3$s] failed: %4$s",
								Nest.value(stepData,"name").$(),
								Nest.value(lastData,"lastfailedstep").$(),
								Nest.value(httpTest,"steps").$(),
								error
							));
						} else {
							Nest.value(status,"msg").$(_s("Unknown step failed: %1$s", error));
						}

						Nest.value(status,"style").$("disabled");
					} else {
						Nest.value(status,"msg").$(_("OK"));
						Nest.value(status,"style").$("enabled");
					}
				} else {// no history data exists
					lastcheck = _("Never");
					Nest.value(status,"msg").$(_("Unknown"));
					Nest.value(status,"style").$("unknown");
				}
				
				CSpan cpsan = new CSpan(Nest.value(httpTest,"hostname").$(),
					(Nest.value(httpTest,"host","status").asInteger() == HOST_STATUS_NOT_MONITORED) ? "not-monitored" : ""
				);
				table.addRow(new CRow(array(
					(Nest.value(_REQUEST,"hostid").asInteger() > 0) ? null : cpsan,
					new CLink(Nest.value(httpTest,"name").$(), "httpdetails.action?httptestid="+Nest.value(httpTest,"httptestid").$()),
					Nest.value(httpTest,"steps").$(),
					lastcheck,
					new CSpan(Nest.value(status,"msg").$(), Nest.value(status,"style").asString())
				)));
			}
		} else {
			CArray tmp = array();
			paging = getPagingLine(getIdentityBean(), executor, tmp);
		}
		
		httpmon_wdgt.addItem(array(table, paging));
		httpmon_wdgt.show();
	}
	
	protected String getAction(){
		return "httpmon.action";
	}
	
	protected void doPageFilterCB(CPageFilter pageFilter, CForm form) {
		if(showGroupFilter()){
			form.addItem(array(_("Group")+SPACE, this.pageFilter.getGroupsCB()));
		}
		form.addItem(array(SPACE+_("Host")+SPACE, this.pageFilter.getHostsCB()));
		form.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").$());
	}
	
	protected boolean showGroupFilter(){
		return true;
	}
	
	protected Object getHeader(){
		return _("Web scenarios");
	}
}
