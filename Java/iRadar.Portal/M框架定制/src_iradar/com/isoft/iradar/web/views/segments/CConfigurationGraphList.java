package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.GraphsUtil.get_realhosts_by_graphid;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import java.util.Map;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationGraphList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		Map<String, Object> page = RadarContext.page();
		String action = Nest.value(page, "file").asString();
		int module = Nest.value(page, "module").asInteger();
		CWidget graphWidget = new CWidget();
		
		// create form
		CForm graphForm = new CForm();
		graphForm.setName("graphForm");
		graphForm.addVar("hostid", Nest.value(data,"hostid").$());
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			graphForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		
		CToolBar tb = new CToolBar(graphForm);
		
		// create new graph button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			createForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
			createForm.addItem(new CSubmit("form", _("Create graph prototype"),"","orange create"));

			graphWidget.addPageHeader(_("CONFIGURATION OF GRAPH PROTOTYPES"), createForm);
			graphWidget.addHeader(array(_("Graph prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));

		} else {
			createForm.addVar("hostid", Nest.value(data,"hostid").$());
			if (!empty(Nest.value(data,"hostid").$())) {
				createForm.addItem(new CSubmit("form", _("Create graph"),"","orange create"));
			} else {
				CSubmit createGraphButton = new CSubmit("form", _("Create graph (select host first)"),"","orange create");
				createGraphButton.setEnabled(false);
				createForm.addItem(createGraphButton);
			}
			
			tb.addForm(createForm);
			
			CArray<CComboItem> goComboBox = array();
			CComboItem goOption = null;
			if (empty(Nest.value(data,"parent_discoveryid").$())) {
				goOption = new CComboItem("copy_to", _("Copy selected to ..."));
				goOption.setAttribute("class", "orange copy");
				goComboBox.add(goOption);
			}

			goOption = new CComboItem("delete", _("Delete selected"));
			goOption.setAttribute("confirm",!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete selected graph prototypes?") : _("Delete selected graphs?"));
			goOption.setAttribute("class", "orange delete");
			goComboBox.add(goOption);
			
			tb.addComboBox(goComboBox);
			
			rda_add_post_js("chkbxRange.pageGoName = \"group_graphid\";");
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"parent_discoveryid").asString()+"\";");
				rda_add_post_js("cookie.prefix = \""+Nest.value(data,"parent_discoveryid").asString()+"\";");
			} else {
				rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"hostid").asString()+"\";");
				rda_add_post_js("cookie.prefix = \""+Nest.value(data,"hostid").asString()+"\";");
			}
			
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			graphWidget.addItem(headerActions);

			CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
			CForm filterForm = new CForm("post");
			if (IMonModule.policy.ordinal() == module) {
				filterForm.addVar("groupid", Nest.value(data,"groupid").$());
			} else {
				filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
			}
			filterForm.addItem(array(SPACE+(IMonModule.policy.ordinal() == module?_("Template"):_("Host"))+SPACE, pageFilter.getHostsCB()));

			graphWidget.addHeader(filterForm);
		}

		// create table
		CTableInfo graphTable = new CTableInfo(!empty(Nest.value(data,"parent_discoveryid").$()) ? _("No graph prototypes found.") : _("No graphs found."));
		if(empty(Nest.value(data,"hostid").$())){
			graphTable.setAttribute("class", graphTable.getAttribute("class")+" detailall");
		}else{
			graphTable.setAttribute("class", graphTable.getAttribute("class")+" normaldisplay");
		}
		graphTable.setHeader(array(
			new CCheckBox("all_graphs", false, "checkAll(\""+graphForm.getName()+"\", \"all_graphs\", \"group_graphid\");"),
			!empty(Nest.value(data,"hostid").$()) ? null : (IMonModule.policy.ordinal() == module?_("Template"):_("Host")),
			make_sorting_header(_("Name"), "name"),
			_("Width"),
			_("Height"),
			make_sorting_header(_("Graph type"), "graphtype")
		));
		Boolean isZero=Nest.value(data,"isZero").asBoolean();
		boolean isZ=true;
		for(Map graph : (CArray<Map>)Nest.value(data,"graphs").asCArray()) {
			Object graphid = Nest.value(graph,"graphid").$();

			String shostList = null;
			if (empty(Nest.value(data,"hostid").$())) {
				CArray<String> hostList = array();
				for(Map host : (CArray<Map>)Nest.value(graph,"hosts").asCArray()) {
					Nest.value(hostList,host.get("name")).$(Nest.value(host,"name").$());
				}

				for(Map template : (CArray<Map>)Nest.value(graph,"templates").asCArray()) {
					Nest.value(hostList,template.get("name")).$(Nest.value(template,"name").$());
				}
				shostList = implode(", ", hostList);
			}
			
			if(empty(shostList)&&isZero){
				isZ=false;
			}else{
				isZ=true;
			}
            if(isZ){//bug10763,将设备管理为空过滤
            	boolean isCheckboxEnabled = true;
    			CArray name = array();
    			if (!empty(Nest.value(graph,"templateid").$())) {
    				CArray<Map> realDbHosts = get_realhosts_by_graphid(idBean, executor, Nest.value(graph,"templateid").asLong());
    				Map realHosts = DBfetch(realDbHosts);
    				name.add(new CLink(Nest.value(realHosts,"name").$(), action+"?hostid="+Nest.value(realHosts,"hostid").$(), "unknown"));
    				name.add(NAME_DELIMITER);
    				name.add(new CLink(
    					Nest.value(graph,"name").$(),
    					action+"?"+
    						"form=update"+
    						"&graphid="+graphid+url_param(idBean, "parent_discoveryid")+
    						"&hostid="+Nest.value(data,"hostid").asString()
    				));

    				if (!empty(Nest.value(graph,"discoveryRule").$())) {
    					isCheckboxEnabled = false;
    				}
    			} else if (!empty(Nest.value(graph,"discoveryRule").$()) && empty(Nest.value(data,"parent_discoveryid").$())) {
//    				name.add(new CLink(
//    					Nest.value(graph,"discoveryRule","name").$(),
//    					"host_discovery.action?form=update&itemid="+Nest.value(graph,"discoveryRule","itemid").$(),
//    					"parent-discovery"
//    				));
    				name.add(Nest.value(graph,"discoveryRule","name").$());
    				name.add(NAME_DELIMITER);
    				name.add(new CSpan(Nest.value(graph,"name").$()));

    				isCheckboxEnabled = false;
    			} else {
    				name.add(new CLink(
    					Nest.value(graph,"name").$(),
    					action+"?"+
    						"form=update"+
    						"&graphid="+graphid+url_param(idBean, "parent_discoveryid")+
    						"&hostid="+Nest.value(data,"hostid").asString()
    				));
    			}

    			CCheckBox checkBox = new CCheckBox("group_graphid["+graphid+"]", false, null, Nest.as(graphid).asInteger());
    			checkBox.setEnabled(isCheckboxEnabled);

    			graphTable.addRow(array(
    				checkBox,
    				!empty(Nest.value(data,"hostid").$()) ? null :shostList,
    				name,
    				Nest.value(graph,"width").$(),
    				Nest.value(graph,"height").$(),
    				Nest.value(graph,"graphtype").$()
    			));
            }
			
		}
		// append table to form
		graphForm.addItem(array(graphTable, Nest.value(data,"paging").$()));

		// append form to widget
		graphWidget.addItem(graphForm);

		return graphWidget;
	}
}