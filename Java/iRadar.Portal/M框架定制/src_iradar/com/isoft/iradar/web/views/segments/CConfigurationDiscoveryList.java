package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_status2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_status2style;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationDiscoveryList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget discoveryWidget = new CWidget();

		// create form
		CForm discoveryForm = new CForm();
		discoveryForm.setName("druleForm");
		
		// create new discovery rule button
		CToolBar tb = new CToolBar(discoveryForm);
		tb.addSubmit("form", _("Create discovery rule"), "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected discovery rules?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected discovery rules?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected discovery rules?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"g_druleid\";");
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		discoveryWidget.addItem(headerActions);

		// create table
		CTableInfo discoveryTable = new CTableInfo(_("No discovery rules found."));
		String discoverClass = discoveryTable.getAttribute("class").toString();
		discoverClass += " discover";
		discoveryTable.setAttribute("class", discoverClass);
		discoveryTable.setHeader(array(
			new CCheckBox("all_drules", false, "checkAll(\""+discoveryForm.getName()+"\", \"all_drules\", \"g_druleid\");"),
			make_sorting_header(_("Name"), "name"),
			_("IP range"),
			_("Delay"),
			_("Checks"),
			_("Status")
		));
		
		CArray<Map> dservices = (CArray<Map>)Nest.value(data,"dservices").asCArray();
		Map dservicesHostidMap = EasyList.asIndexMap(dservices.toList(), "dhostid");
	
		for(Map drule :(CArray<Map>)Nest.value(data,"drules").asCArray()) {
			//当前发现规则，所以发现的设备数量
			Object dhostCtn;
			CArray<Map> dhosts = Nest.value(drule, "dhosts").asCArray();
			if(Cphp.empty(dhosts) || Cphp.empty(dservices)) {
				CSpan countCtn = new CSpan("0");
				countCtn.attr("style", "font-size: 0.9em;");
				dhostCtn = countCtn;
			}else {
				int upHostCount=0, downHostCount=0;
				for(Map dhost: dhosts) {
					Object hostid = Nest.value(dhost, "dhostid").$();
					if(!dservicesHostidMap.containsKey(hostid)){
						continue;
					}
					
					if(Defines.DHOST_STATUS_ACTIVE == Nest.value(dhost, "status").asInteger()) {
						upHostCount++;
					}else {
						downHostCount++;
					}
				}
				CSpan countCtn = new CSpan();
				countCtn.attr("style", "font-size: 0.9em;");
				countCtn.addItem(new CSpan(upHostCount, "green"));
				countCtn.addItem("/");
				countCtn.addItem(new CSpan(downHostCount, "red"));
				String url = "'"+_("the results of discovery")+"', '"+RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX+"discovery.action?druleid="+Nest.value(drule,"druleid").$()+"'";
				dhostCtn = new CLink(countCtn, IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE);
			}
			
			array_push(Nest.value(drule,"description").asCArray(), 
				new CLink(Nest.value(drule,"name").$(), "?form=update&druleid="+Nest.value(drule,"druleid").$(), discovery_status2style(Nest.value(drule,"status").asInteger()))
			);
			
			if(Defines.DRULE_STATUS_ACTIVE == Nest.value(drule,"status").asInteger()) {
				array_push(Nest.value(drule,"description").asCArray(), " (", dhostCtn, ")");
			}

			//自定义属性 保存修改发现发现策略所需要参数 以用做页面Ajax请求
			CLink statusLink = new CLink(
				discovery_status2str(Nest.value(drule,"status").asInteger()),
				"?g_druleid"+SQUAREBRACKETS+"="+Nest.value(drule,"druleid").asString()+(Nest.value(drule,"status").asInteger() == DRULE_STATUS_ACTIVE ? "&go=disable" : "&go=activate"),
				discovery_status2style(Nest.value(drule,"status").asInteger())
			);
			
			Object _go = Nest.value(drule,"status").asInteger() == DRULE_STATUS_ACTIVE ? "disable" : "activate";
			Object _g_druleid = Nest.value(drule,"druleid").asString();
			String[] aks = statusLink.getUrl().split("=");
			Object _sid = aks[aks.length-1];
			
			statusLink.setAttribute("go", _go);
			statusLink.setAttribute("g_druleid", _g_druleid);
			statusLink.setAttribute("sid", _sid);
			statusLink.setAttribute("onclick", "changeDiscoveryStatus(this)");
			statusLink.setAttribute("href", "javascript:void(0)");
			
			CCol status = new CCol(new CDiv(statusLink, "switch"));
			
			discoveryTable.addRow(array(
				new CCheckBox("g_druleid["+Nest.value(drule,"druleid").$()+"]", false, null, Nest.value(drule,"druleid").asInteger()),
				Nest.value(drule,"description").$(),
				Nest.value(drule,"iprange").$(),
				Nest.value(drule,"delay").$(),
				!empty(Nest.value(drule,"checks").$()) ? implode(", ", Nest.array(drule,"checks").asString()) : "",
				status
			));
		}

		// append table to form
		discoveryForm.addItem(array(discoveryTable, Nest.value(data,"paging").$()));

		// append form to widget
		discoveryWidget.addItem(discoveryForm);
		return discoveryWidget;
	}

}
