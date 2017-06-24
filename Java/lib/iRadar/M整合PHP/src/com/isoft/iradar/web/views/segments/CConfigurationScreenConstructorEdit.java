package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HALIGN_CENTER;
import static com.isoft.iradar.inc.Defines.HALIGN_DEFAULT;
import static com.isoft.iradar.inc.Defines.HALIGN_LEFT;
import static com.isoft.iradar.inc.Defines.HALIGN_RIGHT;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_ACTIONS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_EVENTS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SERVER_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SYSTEM_STATUS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_URL;
import static com.isoft.iradar.inc.Defines.SCREEN_SIMPLE_ITEM;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_DATE_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_HOST_NAME_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RECIPIENT_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RECIPIENT_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RETRIES_LEFT_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RETRIES_LEFT_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_SEVERITY_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_STATUS_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_STATUS_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TIME_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TIME_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TYPE_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TYPE_DESC;
import static com.isoft.iradar.inc.Defines.STYLE_HORIZONTAL;
import static com.isoft.iradar.inc.Defines.STYLE_LEFT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.STYLE_VERTICAL;
import static com.isoft.iradar.inc.Defines.VALIGN_BOTTOM;
import static com.isoft.iradar.inc.Defines.VALIGN_DEFAULT;
import static com.isoft.iradar.inc.Defines.VALIGN_MIDDLE;
import static com.isoft.iradar.inc.Defines.VALIGN_TOP;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.ScreensUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationScreenConstructorEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		String action = "screenedit.action?form=update&screenid="+get_request("screenid");
		if (isset(Nest.value(_REQUEST,"screenitemid").$())) {
			action += "&screenitemid="+get_request("screenitemid");
		}

		// create screen form
		CForm screenForm = new CForm("post", action);
		screenForm.setName("screen_item_form");

		// create screen form list
		CFormList screenFormList = new CFormList("screenFormList");
		screenFormList.addVar("screenid", Nest.value(_REQUEST,"screenid").asString());

		CArray<Map> screenItems = null;
		if (isset(Nest.value(_REQUEST,"screenitemid").$())) {
			screenFormList.addVar("screenitemid", Nest.value(_REQUEST,"screenitemid").asString());
			screenItems = rda_toHash(Nest.value(data,"screen","screenitems").$(), "screenitemid");
		} else {
			screenFormList.addVar("x", Nest.value(_REQUEST,"x").asString());
			screenFormList.addVar("y", Nest.value(_REQUEST,"y").asString());
		}

		Integer resourceType = null;
		Long resourceId = null;
		Object width = null;
		Object height = null;
		Object colspan = null;
		Object rowspan = null;
		Object elements = null;
		Object valign = null;
		Object halign = null;
		Integer style = null;
		Object url = null;
		Object dynamic = null;
		Object sortTriggers = null;
		Object application = null;
		if (isset(Nest.value(_REQUEST,"screenitemid").$()) && !isset(Nest.value(_REQUEST,"form_refresh").$())) {
			Map screenItem		= screenItems.get(_REQUEST.get("screenitemid"));
			resourceType	= Nest.value(screenItem,"resourcetype").asInteger();
			resourceId		= Nest.value(screenItem,"resourceid").asLong();
			width			= Nest.value(screenItem,"width").$();
			height			= Nest.value(screenItem,"height").$();
			colspan		= Nest.value(screenItem,"colspan").$();
			rowspan		= Nest.value(screenItem,"rowspan").$();
			elements		= Nest.value(screenItem,"elements").$();
			valign			= Nest.value(screenItem,"valign").$();
			halign			= Nest.value(screenItem,"halign").$();
			style			= Nest.value(screenItem,"style").asInteger();
			url			= Nest.value(screenItem,"url").$();
			dynamic		= Nest.value(screenItem,"dynamic").$();
			sortTriggers	= Nest.value(screenItem,"sort_triggers").$();
			application	= Nest.value(screenItem,"application").$();
		} else {
			resourceType= get_request("resourcetype", 0);
			resourceId	= get_request("resourceid", 0L);
			width			= get_request("width", 500);
			height			= get_request("height", 100);
			colspan			= get_request("colspan", 1);
			rowspan		= get_request("rowspan", 1);
			elements		= get_request("elements", 25);
			valign			= get_request("valign", VALIGN_DEFAULT);
			halign			= get_request("halign", HALIGN_DEFAULT);
			style				= get_request("style", 0);
			url				= get_request("url", "");
			dynamic		= get_request("dynamic", SCREEN_SIMPLE_ITEM);
			sortTriggers	= get_request("sort_triggers", SCREEN_SORT_TRIGGERS_DATE_DESC);
			application	= get_request("application", "");
		}

		// append resource types to form list
		CComboBox resourceTypeComboBox = new CComboBox("resourcetype", resourceType, "submit()");
		CArray<String> screenResources = ScreensUtil.screen_resources();
		if (!empty(Nest.value(data,"screen","templateid").$())) {
			unset(screenResources,SCREEN_RESOURCE_DATA_OVERVIEW);
			unset(screenResources,SCREEN_RESOURCE_ACTIONS);
			unset(screenResources,SCREEN_RESOURCE_EVENTS);
			unset(screenResources,SCREEN_RESOURCE_HOSTS_INFO);
			unset(screenResources,SCREEN_RESOURCE_MAP);
			unset(screenResources,SCREEN_RESOURCE_SCREEN);
			unset(screenResources,SCREEN_RESOURCE_SERVER_INFO);
			unset(screenResources,SCREEN_RESOURCE_HOSTGROUP_TRIGGERS);
			unset(screenResources,SCREEN_RESOURCE_HOST_TRIGGERS);
			unset(screenResources,SCREEN_RESOURCE_SYSTEM_STATUS);
			unset(screenResources,SCREEN_RESOURCE_TRIGGERS_INFO);
			unset(screenResources,SCREEN_RESOURCE_TRIGGERS_OVERVIEW);
		}
		resourceTypeComboBox.addItems(screenResources);
		screenFormList.addRow(_("Resource"), resourceTypeComboBox);

		String caption = null;
		Long id = null;
		CButton selectButton = null;
		/* Screen item: Graph */		
		if (resourceType == SCREEN_RESOURCE_GRAPH) {
			caption = "";
			id = 0L;

			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(resourceId);
			goptions.setSelectHosts(new String[]{"hostid", "name", "status"});
			goptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> graphs = API.Graph(idBean, executor).get(goptions);
			if (!empty(graphs)) {
				id = resourceId;
				Map graph = reset(graphs);

				order_result(Nest.value(graph,"hosts").asCArray(), "name");
				Nest.value(graph,"host").$(reset(Nest.value(graph,"hosts").asCArray()));

				caption = Nest.value(graph,"host","name").$()+NAME_DELIMITER+Nest.value(graph,"name").$();
			}

			if (!empty(Nest.value(data,"screen","templateid").$())) {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=graphs&srcfld1=graphid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&templated_hosts=1&only_hostid="+Nest.value(data,"screen","templateid").asString()+
						"&writeonly=1\", 800, 450);",
					"formlist"
				);
			} else {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=graphs&srcfld1=graphid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&real_hosts=1&with_graphs=1&writeonly=1\", 800, 450);",
					"formlist"
				);
			}

			screenFormList.addVar("resourceid", Nest.as(id).asString());
			screenFormList.addRow(_("Graph name"), array(
				new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
				selectButton
			));
		}

		/*
		 * Screen item: Simple graph
		 */
		else if (resourceType == SCREEN_RESOURCE_SIMPLE_GRAPH) {
			caption = "";
			id = 0L;

			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(resourceId);
			ioptions.setSelectHosts(new String[]{"name"});
			ioptions.setOutput(new String[]{"itemid", "hostid", "key_", "name"});
			CArray<Map> items = API.Item(idBean, executor).get(ioptions);

			if (!empty(items)) {
				items = CMacrosResolverHelper.resolveItemNames(idBean, executor, items);

				id = resourceId;
				Map item = reset(items);
				Nest.value(item,"host").$( reset(Nest.value(item,"hosts").asCArray()));

				caption = Nest.value(item,"host","name").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$();
			}

			if (!empty(Nest.value(data,"screen","templateid").$())) {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=items&srcfld1=itemid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&templated_hosts=1&only_hostid="+Nest.value(data,"screen","templateid").$()+
						"&templated=1&writeonly=1&numeric=1\", 800, 450);", "formlist"
				);
			} else {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=items&srcfld1=itemid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&real_hosts=1&with_simple_graph_items=1&writeonly=1&templated=0&numeric=1\", 800, 450);",
					"formlist"
				);
			}

			screenFormList.addVar("resourceid", Nest.as(id).asString());
			screenFormList.addRow(_("Parameter"), array(
				new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
				selectButton
			));
		}

		/*
		 * Screen item: Map
		 */
		else if (resourceType == SCREEN_RESOURCE_MAP) {
			caption = "";
			id = 0L;

			CMapGet moptions = new CMapGet();
			moptions.setSysmapIds(resourceId);
			moptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> maps = API.Map(idBean, executor).get(moptions);
			if (!empty(maps)) {
				id = resourceId;
				Map map = reset(maps);
				caption = Nest.value(map,"name").asString();
			}

			screenFormList.addVar("resourceid", Nest.as(id).asString());
			screenFormList.addRow(_("Parameter"), array(
				new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
				new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=sysmaps&srcfld1=sysmapid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&writeonly=1\", 400, 450);",
					"formlist"
				)
			));
		}

		/*
		 * Screen item: Plain text
		 */
		else if (resourceType == SCREEN_RESOURCE_PLAIN_TEXT) {
			caption = "";
			id = 0L;

			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(resourceId);
			ioptions.setSelectHosts(new String[]{"name"});
			ioptions.setOutput(new String[]{"itemid", "hostid", "key_", "name"});
			CArray<Map> items = API.Item(idBean, executor).get(ioptions);

			if (!empty(items)) {
				items = CMacrosResolverHelper.resolveItemNames(idBean, executor, items);

				id = resourceId;
				Map item = reset(items);
				Nest.value(item,"host").$(reset(Nest.value(item,"hosts").asCArray()));
				caption = Nest.value(item,"host","name").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$();
			}

			if (!empty(Nest.value(data,"screen","templateid").$())) {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=items&srcfld1=itemid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&templated_hosts=1&only_hostid="+Nest.value(data,"screen","templateid").$()+
						"&writeonly=1\", 800, 450);",
					"formlist"
				);
			} else {
				selectButton = new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=items&srcfld1=itemid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&real_hosts=1&writeonly=1&templated=0\", 800, 450);",
					"formlist"
				);
			}

			screenFormList.addVar("resourceid", Nest.as(id).asString());
			screenFormList.addRow(_("Parameter"), array(
				new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
				selectButton
			));
			screenFormList.addRow(_("Show lines"), new CNumericBox("elements", Nest.as(elements).asString(), 3));
			screenFormList.addRow(_("Show text as HTML"), new CCheckBox("style", Nest.as(style).asBoolean(), null, 1));
		}

		/*
		 * Screen item: Status of triggers
		 */
		else if (in_array(resourceType, new Integer[]{SCREEN_RESOURCE_HOSTGROUP_TRIGGERS, SCREEN_RESOURCE_HOST_TRIGGERS})) {
			Object cdata = array();

			if (resourceType == SCREEN_RESOURCE_HOSTGROUP_TRIGGERS) {
				if (resourceId > 0L) {
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(resourceId);
					hgoptions.setOutput(new String[]{"groupid", "name"});
					hgoptions.setEditable(true);
					cdata = API.HostGroup(idBean, executor).get(hgoptions);

					if (!empty(cdata)) {
						cdata = reset((CArray)cdata);
						Nest.value((Map)cdata,"prefix").$("");
					}
				}

				screenFormList.addRow(_("Group"), new CMultiSelect(map(
					"name", "resourceid",
					"objectName", "hostGroup",
					"objectOptions", map("editable", true),
					"data", !empty(cdata) ? array(map("id", Nest.value((Map)cdata,"groupid").$(), "name", Nest.value((Map)cdata,"name").$(), "prefix", Nest.value((Map)cdata,"prefix").$())) : null,
					"defaultValue", 0,
					"selectedLimit", 1,
					"popup", map(
						"parameters", "srctbl=host_groups&dstfrm="+screenForm.getName()+"&dstfld1=resourceid"+
							"&srcfld1=groupid&writeonly=1",
						"width", 450,
						"height", 450
					)
				)));
			} else {
				if (resourceId > 0L) {
					CHostGet hoptions = new CHostGet();
					hoptions.setHostIds(resourceId);
					hoptions.setOutput(new String[]{"hostid", "name"});
					hoptions.setEditable(true);
					cdata = API.Host(idBean, executor).get(hoptions);

					if (!empty(cdata)) {
						cdata = reset((CArray)cdata);
						Nest.value((Map)cdata,"prefix").$("");
					}
				}

				screenFormList.addRow(_("Host"), new CMultiSelect(map(
					"name", "resourceid",
					"objectName", "hosts",
					"objectOptions", map("editable", true),
					"data", !empty(cdata) ? array(map("id", Nest.value((Map)cdata,"hostid").$(), "name", Nest.value((Map)cdata,"name").$(), "prefix", Nest.value((Map)cdata,"prefix").$())) : null,
					"defaultValue", 0,
					"selectedLimit", 1,
					"popup", map(
						"parameters", "srctbl=hosts&dstfrm="+screenForm.getName()+"&dstfld1=resourceid"+
							"&srcfld1=hostid&writeonly=1",
						"width", 450,
						"height", 450
					)
				)));
			}

			screenFormList.addRow(_("Show lines"), new CNumericBox("elements", Nest.as(elements).asString(), 3));
			screenFormList.addRow(
				_("Sort triggers by"),
				new CComboBox("sort_triggers", sortTriggers, null, (CArray)map(
					SCREEN_SORT_TRIGGERS_DATE_DESC, _("Last change (descending)"),
					SCREEN_SORT_TRIGGERS_SEVERITY_DESC, _("Severity (descending)"),
					SCREEN_SORT_TRIGGERS_HOST_NAME_ASC, _("Host (ascending)")
				))
			);
		}

		/*
		 * Screen item: History of actions
		 */
		else if (resourceType == SCREEN_RESOURCE_ACTIONS) {
			screenFormList.addRow(_("Show lines"), new CNumericBox("elements", Nest.as(elements).asString(), 3));
			screenFormList.addRow(
				_("Sort triggers by"),
				new CComboBox("sort_triggers", sortTriggers, null, (CArray)map(
					SCREEN_SORT_TRIGGERS_TIME_DESC, _("Time (descending)"),
					SCREEN_SORT_TRIGGERS_TIME_ASC, _("Time (ascending)"),
					SCREEN_SORT_TRIGGERS_TYPE_DESC, _("Type (descending)"),
					SCREEN_SORT_TRIGGERS_TYPE_ASC, _("Type (ascending)"),
					SCREEN_SORT_TRIGGERS_STATUS_DESC, _("Status (descending)"),
					SCREEN_SORT_TRIGGERS_STATUS_ASC, _("Status (ascending)"),
					SCREEN_SORT_TRIGGERS_RETRIES_LEFT_DESC, _("Retries left (descending)"),
					SCREEN_SORT_TRIGGERS_RETRIES_LEFT_ASC, _("Retries left (ascending)"),
					SCREEN_SORT_TRIGGERS_RECIPIENT_DESC, _("Recipient (descending)"),
					SCREEN_SORT_TRIGGERS_RECIPIENT_ASC, _("Recipient (ascending)")
				))
			);
			screenFormList.addVar("resourceid", "0");
		}

		/*
		 * Screen item: History of events
		 */
		else if (resourceType == SCREEN_RESOURCE_EVENTS) {
			screenFormList.addRow(_("Show lines"), new CNumericBox("elements", Nest.as(elements).asString(), 3));
			screenFormList.addVar("resourceid", "0");
		}

		/*
		 * Screen item: Overviews
		 */
		else if (in_array(resourceType, new Integer[]{SCREEN_RESOURCE_TRIGGERS_OVERVIEW, SCREEN_RESOURCE_DATA_OVERVIEW})) {
			Map cdata = array();

			if (resourceId > 0L) {
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setGroupIds(resourceId);
				hgoptions.setOutput(new String[]{"groupid", "name"});
				hgoptions.setEditable(true);
				cdata = API.HostGroup(idBean, executor).get(hgoptions);

				if (!empty(cdata)) {
					cdata = reset(cdata);
					Nest.value(cdata,"prefix").$("");
				}
			}

			screenFormList.addRow(_("Group"), new CMultiSelect(map(
				"name", "resourceid",
				"objectName", "hostGroup",
				"objectOptions", map("editable", true),
				"data", !empty(cdata) ? array(map("id", Nest.value(cdata,"groupid").$(), "name", Nest.value(cdata,"name").$(), "prefix", Nest.value(cdata,"prefix").$())) : null,
				"selectedLimit", 1,
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+screenForm.getName()+"&dstfld1=resourceid"+
						"&srcfld1=groupid&writeonly=1",
					"width", 450,
					"height", 450
				)
			)));
			screenFormList.addRow(_("Application"), new CTextBox("application", Nest.as(application).asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 255));
		}

		/* Screen item: Screens */
		else if (resourceType == SCREEN_RESOURCE_SCREEN) {
			caption = "";
			id = 0L;

			if (resourceId > 0L) {
				Map params = new HashMap();
				params.put("resourceId", resourceId);
				CArray<Map> db_screens = DBselect(executor,
					"SELECT DISTINCT s.screenid,s.name"+
					" FROM screens s"+
					" WHERE s.screenid=#{resourceId}",
					params
				);
				for (Map row : db_screens) {
					CScreenGet soptions = new CScreenGet();
					soptions.setScreenIds(Nest.value(row,"screenid").asLong());
					soptions.setOutput(new String[]{"screenid"});
					CArray<Map> screen = API.Screen(idBean, executor).get(soptions);
					if (empty(screen)) {
						continue;
					}
					if (ScreensUtil.check_screen_recursion(idBean, executor, Nest.value(_REQUEST,"screenid").asLong(), Nest.value(row,"screenid").asLong())) {
						continue;
					}

					caption = Nest.value(row,"name").asString();
					id = resourceId;
				}
			}

			screenFormList.addVar("resourceid", Nest.as(id).asString());
			screenFormList.addRow(_("Parameter"), array(
				new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
				new CButton("select", _("Select"),
					"javascript: return PopUp(\"popup.action?srctbl=screens2&srcfld1=screenid&srcfld2=name"+
						"&dstfrm="+screenForm.getName()+"&dstfld1=resourceid&dstfld2=caption"+
						"&writeonly=1&screenid="+Nest.value(_REQUEST,"screenid").asString()+"\", 800, 450);",
					"formlist"
				)
			));
		}

		/*
		 * Screen item: Hosts info
		 */
		else if (resourceType == SCREEN_RESOURCE_HOSTS_INFO || resourceType == SCREEN_RESOURCE_TRIGGERS_INFO) {
			Map cdata = array();

			if (resourceId > 0L) {
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setGroupIds(resourceId);
				hgoptions.setOutput(new String[]{"groupid", "name"});
				hgoptions.setEditable(true);
				CArray<Map> cdatas = API.HostGroup(idBean, executor).get(hgoptions);

				if (!empty(cdatas)) {
					cdata = reset(cdatas);
					Nest.value(cdata,"prefix").$("");
				}
			}

			screenFormList.addRow(_("Group"), new CMultiSelect(map(
				"name", "resourceid",
				"objectName", "hostGroup",
				"objectOptions", map("editable", true),
				"data", !empty(cdata) ? array(map("id", Nest.value(cdata,"groupid").$(), "name", Nest.value(cdata,"name").$(), "prefix", Nest.value(cdata,"prefix").$())) : null,
				"defaultValue", 0,
				"selectedLimit", 1,
				"popup", map(
					"parameters", "srctbl=host_groups&dstfrm="+screenForm.getName()+"&dstfld1=resourceid"+
						"&srcfld1=groupid&writeonly=1",
					"width", 450,
					"height", 450
				)
			)));
		}
//     关闭clock元素
//		/*
//		 * Screen item: Clock
//		 */
//		else if (resourceType == SCREEN_RESOURCE_CLOCK  && false) {
//			caption = get_request("caption", "");
//
//			if (rda_empty(caption) && TIME_TYPE_HOST == Nest.as(style).asInteger() && resourceId > 0L) {
//				CItemGet ioptions = new CItemGet();
//				ioptions.setItemIds(resourceId);
//				ioptions.setSelectHosts(new String[]{"name"});
//				ioptions.setOutput(new String[]{"itemid", "hostid", "key_", "name"});
//				CArray<Map> items = API.Item(idBean, executor).get(ioptions);
//
//				if (!empty(items)) {
//					items = CMacrosResolverHelper.resolveItemNames(idBean, executor, items);
//
//					Map item = reset(items);
//					Map host = reset(Nest.value(item,"hosts").asCArray());
//					caption = Nest.value(host,"name").asString()+NAME_DELIMITER+Nest.value(item,"name_expanded").$();
//				}
//			}
//
//			screenFormList.addVar("resourceid", Nest.as(resourceId).asString());
//
//			CComboBox styleComboBox = new CComboBox("style", style, "javascript: submit();");
//			styleComboBox.addItem(TIME_TYPE_LOCAL, _("Local time"));
//			styleComboBox.addItem(TIME_TYPE_SERVER, _("Server time"));
//			styleComboBox.addItem(TIME_TYPE_HOST, _("Host time"));
//			screenFormList.addRow(_("Time type"), styleComboBox);
//
//			if (TIME_TYPE_HOST == Nest.as(style).asInteger()) {
//				if (!empty(Nest.value(data,"screen","templateid").$())) {
//					selectButton = new CButton("select", _("Select"),
//						"javascript: return PopUp('popup.action?writeonly=1&dstfrm="+screenForm.getName()+
//							"&dstfld1=resourceid&dstfld2=caption&srctbl=items&srcfld1=itemid&srcfld2=name&templated_hosts=1"+
//							"&only_hostid="+Nest.value(data,"screen","templateid").$()+"', 800, 450);", "formlist"
//					);
//				} else {
//					selectButton = new CButton("select", _("Select"),
//							"javascript: return PopUp('popup.action?writeonly=1&dstfrm="+screenForm.getName()+"&dstfld1=resourceid"+
//							"&dstfld2=caption&srctbl=items&srcfld1=itemid&srcfld2=name&real_hosts=1', 800, 450);", "formlist"
//					);
//				}
//				screenFormList.addRow(_("Parameter"), array(
//					new CTextBox("caption", caption, RDA_TEXTBOX_STANDARD_SIZE, true),
//					selectButton
//				));
//			} else {
//				screenFormList.addVar("caption", caption);
//			}
//		} 
		
		else {
			screenFormList.addVar("resourceid", "0");
		}

		/*
		 * Append common fields
		 */
		if (in_array(resourceType, new Integer[]{SCREEN_RESOURCE_HOSTS_INFO, SCREEN_RESOURCE_TRIGGERS_INFO})) {
			CArray styleRadioButton = array(
				new CRadioButton("style", STYLE_HORIZONTAL, null, "style_"+STYLE_HORIZONTAL, Nest.as(style).asInteger() == STYLE_HORIZONTAL),
				new CLabel(_("Horizontal"), "style_"+STYLE_HORIZONTAL),
				new CRadioButton("style", STYLE_VERTICAL, null, "style_"+STYLE_VERTICAL, Nest.as(style).asInteger() == STYLE_VERTICAL),
				new CLabel(_("Vertical"), "style_"+STYLE_VERTICAL)
			);
			screenFormList.addRow(_("Style"), new CDiv(styleRadioButton, "jqueryinputset"));
		}
		else if (in_array(resourceType, new Integer[]{SCREEN_RESOURCE_TRIGGERS_OVERVIEW,SCREEN_RESOURCE_DATA_OVERVIEW})) {
			CArray styleRadioButton = array(
				new CRadioButton("style", STYLE_LEFT, null, "style_"+STYLE_LEFT, Nest.as(style).asInteger() == STYLE_LEFT),
				new CLabel(_("Left"), "style_"+STYLE_LEFT),
				new CRadioButton("style", STYLE_TOP, null, "style_"+STYLE_TOP, Nest.as(style).asInteger() == STYLE_TOP),
				new CLabel(_("Top"), "style_"+STYLE_TOP)
			);
			screenFormList.addRow(_("Hosts location"), new CDiv(styleRadioButton, "jqueryinputset"));
		} else {
			screenFormList.addVar("style", "0");
		}

		if (in_array(resourceType, new Integer[]{SCREEN_RESOURCE_URL})) {
			screenFormList.addRow(_("Url"), new CTextBox("url", Nest.as(url).asString(), RDA_TEXTBOX_STANDARD_SIZE));
		} else {
			screenFormList.addVar("url", "");
		}

		if (in_array(resourceType, new Integer[]{
				SCREEN_RESOURCE_GRAPH, 
				SCREEN_RESOURCE_SIMPLE_GRAPH, 
				//SCREEN_RESOURCE_CLOCK, 
				SCREEN_RESOURCE_URL})) {
			screenFormList.addRow(_("Width"), new CNumericBox("width", Nest.as(width).asString(), 5));
			screenFormList.addRow(_("Height"), new CNumericBox("height", Nest.as(height).asString(), 5));
		} else {
			screenFormList.addVar("width", "500");
			screenFormList.addVar("height", "100");
		}

		if (in_array(resourceType, new Integer[]{
				SCREEN_RESOURCE_GRAPH, 
				SCREEN_RESOURCE_SIMPLE_GRAPH, 
				SCREEN_RESOURCE_MAP, 
				//SCREEN_RESOURCE_CLOCK, 
				SCREEN_RESOURCE_URL})) {
			CArray hightAlignRadioButton = array(
				new CRadioButton("halign", HALIGN_LEFT, null, "halign_"+HALIGN_LEFT, Nest.as(halign).asInteger() == HALIGN_LEFT),
				new CLabel(_("Left"), "halign_"+HALIGN_LEFT),
				new CRadioButton("halign", HALIGN_CENTER, null, "halign_"+HALIGN_CENTER, Nest.as(halign).asInteger() == HALIGN_CENTER),
				new CLabel(_("Center"), "halign_"+HALIGN_CENTER),
				new CRadioButton("halign", HALIGN_RIGHT, null, "halign_"+HALIGN_RIGHT, Nest.as(halign).asInteger() == HALIGN_RIGHT),
				new CLabel(_("Right"), "halign_"+HALIGN_RIGHT)
			);
			screenFormList.addRow(_("Horizontal align"), new CDiv(hightAlignRadioButton, "jqueryinputset"));
		} else {
			screenFormList.addVar("halign", "0");
		}

		CArray verticalAlignRadioButton = array(
			new CRadioButton("valign", VALIGN_TOP, null, "valign_"+VALIGN_TOP, Nest.as(valign).asInteger() == VALIGN_TOP),
			new CLabel(_("Top"), "valign_"+VALIGN_TOP),
			new CRadioButton("valign", VALIGN_MIDDLE, null, "valign_"+VALIGN_MIDDLE, Nest.as(valign).asInteger() == VALIGN_MIDDLE),
			new CLabel(_("Middle"), "valign_"+VALIGN_MIDDLE),
			new CRadioButton("valign", VALIGN_BOTTOM, null, "valign_"+VALIGN_BOTTOM, Nest.as(valign).asInteger() == VALIGN_BOTTOM),
			new CLabel(_("Bottom"), "valign_"+VALIGN_BOTTOM)
		);
		screenFormList.addRow(_("Vertical align"), new CDiv(verticalAlignRadioButton, "jqueryinputset"));
		screenFormList.addRow(_("Column span"), new CNumericBox("colspan", Nest.as(colspan).asString(), 3));
		screenFormList.addRow(_("Row span"), new CNumericBox("rowspan", Nest.as(rowspan).asString(), 3));

		// dynamic addon
		if (Nest.value(data,"screen","templateid").asLong() == 0 && in_array(resourceType, new Integer[]{SCREEN_RESOURCE_GRAPH, SCREEN_RESOURCE_SIMPLE_GRAPH, SCREEN_RESOURCE_PLAIN_TEXT})) {
			screenFormList.addRow(_("Dynamic item"), new CCheckBox("dynamic", Nest.as(dynamic).asBoolean(), null, 1));
		}

		// append tabs to form
		CTabView screenTab = new CTabView();
		screenTab.setAttribute("style", "text-align: left;");
		screenTab.addTab("screenTab", _("Screen cell configuration"), screenFormList);
		screenForm.addItem(screenTab);

		// append buttons to form
		CArray buttons = array();
		if (isset(Nest.value(_REQUEST,"screenitemid").$())) {
			array_push(buttons, new CButtonDelete(null, url_param(idBean, "form")+url_param(idBean, "screenid")+url_param(idBean, "screenitemid")));
		}
		array_push(buttons, new CButtonCancel(url_param(idBean, "screenid")));

		screenForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), buttons));

		return screenForm;
	}

}
