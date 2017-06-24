package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_SNMPTRAP;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATE_UNKNOWN;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicator;
import static com.isoft.iradar.inc.ItemsUtil.itemIndicatorStyle;
import static com.isoft.iradar.inc.ItemsUtil.item_type2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.triggerExpression;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicator;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicatorStyle;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationItemList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
//		Map<String, Object> page = RadarContext.page();
//		String action = Nest.value(page, "file").asString();
//		int module = Nest.value(page, "module").asInteger();
		
		includeSubView("js/configuration.item.list.js");
		
		CWidget itemsWidget = new CWidget(null, "item-list");
		
		// create new item button
		CForm itemForm = new CForm();
		itemForm.setName("items");
		CToolBar tb = new CToolBar(itemForm);
		
		// create form
		CForm createForm = new CForm("get");
		createForm.cleanItems();

		if (empty(Nest.value(data,"hostid").$())) {
			CSubmit createButton = new CSubmit("form", _("Create item"),"","orange create");
			show_messages(false,"",_("select host first"));
			createButton.setEnabled(false);
			createForm.addItem(createButton);
		} else {
			createForm.addVar("hostid", Nest.value(data,"hostid").$());
			createForm.addItem(new CSubmit("form", _("Create item"),"","orange create"));
		}
		tb.addForm(createForm);
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected items?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected items?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

	/*	goOption = new CComboItem("massupdate", _("Mass update"));
		goOption.setAttribute("class", "orange massupdate");
		goComboBox.add(goOption);

		goOption = new CComboItem("copy_to", _("Copy selected to ..."));
		goOption.setAttribute("class", "orange copy");
		goComboBox.add(goOption);

		goOption = new CComboItem("clean_history", _("Clear history for selected"));
		goOption.setAttribute("confirm", _("Delete history of selected items?"));
		goOption.setAttribute("class", "orange clearAll");
		goComboBox.add(goOption);*/

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected items?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);

		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"group_itemid\";");
		rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"hostid").$()+"\";");
		rda_add_post_js("cookie.prefix = \""+Nest.value(data,"hostid").$()+"\";");

		
		// header
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		itemsWidget.addItem(headerActions);

		/*if (!empty(Nest.value(data,"hostid").$())) {//注释掉用不到的表头
			itemsWidget.addItem(get_header_host_table(executor, "items", Nest.value(data,"hostid").asLong(true)));
		}*/
		itemsWidget.addFlicker(Nest.value(data,"flicker").$(), Nest.as(CProfile.get(idBean, executor, "web.items.filter.state", 0)).asInteger());

		if (!empty(Nest.value(data,"hostid").$())) {
			itemForm.addVar("hostid", Nest.value(data,"hostid").$());
		}

		// create table
		CTableInfo itemTable = new CTableInfo(_("No items found."));
		itemTable.setHeader(array(
			new CCheckBox("all_items", false, "checkAll(\""+itemForm.getName()+"\", \"all_items\", \"group_itemid\");"),
			_("Wizard"),
			empty(Nest.value(data,"filter_hostid").$()) ? _("Host") : null,
			make_sorting_header(_("Name"), "name"),
			_("Triggers relevance"),
			make_sorting_header(_("Key"), "key_"),
			make_sorting_header(_("Interval"), "delay"),
			make_sorting_header(_("History"), "history"),
			make_sorting_header(_("Trends"), "trends"),
			make_sorting_header(_("Type"), "type"),
			_("Applications"),
			make_sorting_header(_("Status"), "status"),
			!empty(Nest.value(data,"showErrorColumn").$()) ? _("Error") : null
		));

		for(Map item : (CArray<Map>)Nest.value(data,"items").asCArray()) {
			// description
			CArray description = array();
			if (!empty(Nest.value(item,"template_host").$())) {
				description.add(new CLink(
					CHtml.encode(Nest.value(item,"template_host","name").asString()),
					"?hostid="+Nest.value(item,"template_host","hostid").asString()+"&filter_set=1",
					"unknown"
				));
				description.add(NAME_DELIMITER);
			}

			if (!empty(Nest.value(item,"discoveryRule").$())) {
				description.add(Nest.value(item,"discoveryRule","name").asString()
						/*new CLink(//模型原型不需要关联，隐藏当前页面
					CHtml.encode(Nest.value(item,"discoveryRule","name").asString()),
					"disc_prototypes.action?parent_discoveryid="+Nest.value(item,"discoveryRule","itemid").$(),
					"parent-discovery"
				)*/);
				description.add(NAME_DELIMITER+Nest.value(item,"name_expanded").$());
			} else {
				description.add(new CLink(
					CHtml.encode(Nest.value(item,"name_expanded").asString()),
					"?form=update&hostid="+Nest.value(item,"hostid").$()+"&itemid="+Nest.value(item,"itemid").$()
				));
			}

			// status
			CCol status = new CCol(new CDiv(new CLink(
					itemIndicator(Nest.value(item,"status").asInteger(), Nest.value(item,"state").asInteger()),
					"?group_itemid="+Nest.value(item,"itemid").$()+"&hostid="+Nest.value(item,"hostid").$()+"&go="+(Nest.value(item,"status").asBoolean() ? "activate" : "disable"),
					itemIndicatorStyle(Nest.value(item,"status").asInteger(), Nest.value(item,"state").asInteger())
			), "switch"));
			
			CArray statusIcons = null;
			if (!empty(Nest.value(data,"showErrorColumn").$())) {
				 statusIcons  = array();
				if (Nest.value(item,"status").asInteger() == ITEM_STATUS_ACTIVE) {
					CDiv error = null;
					if (rda_empty(Nest.value(item,"error").$())) {
						error  = new CDiv(SPACE, "status_icon iconok");
					} else {
						error = new CDiv(SPACE, "status_icon iconerror");
						error.setHint(Nest.value(item,"error").$(), "", "on");
					}
					statusIcons.add(error);
				}

				// discovered item lifetime indicator
				if (Nest.value(item,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED && !empty(Nest.value(item,"itemDiscovery","ts_delete").$())) {
					CDiv deleteError = new CDiv(SPACE, "status_icon iconwarning");
					deleteError.setHint(
						_s("The item is not discovered anymore and will be deleted in %1$s (on %2$s at %3$s).",
							rda_date2age(Nest.value(item,"itemDiscovery","ts_delete").asLong()), rda_date2str(_("d M Y"), Nest.value(item,"itemDiscovery","ts_delete").asLong()),
							rda_date2str(_("H:i:s"), Nest.value(item,"itemDiscovery","ts_delete").asLong())
					));
					statusIcons.add(deleteError);
				}
			}

			Object triggerHintTable = new CTableInfo();
			((CTableInfo)triggerHintTable).setHeader(array(
				_("Severity"),
				_("Name"),
				_("Expression"),
				_("Status")
			));

			// triggers info
			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(item,"triggers").asCArray()).entrySet()) {
			    Object num = e.getKey();
			    Map trigger = e.getValue();
				trigger = Nest.value(data,"itemTriggers",trigger.get("triggerid")).asCArray();
				CArray triggerDescription = array();
				String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
				String url="";
				String name ="";
				if (Nest.value(trigger,"templateid").asInteger() > 0) {
					if (!isset(Nest.value(data,"triggerRealHosts",trigger.get("triggerid")).$())) {
						triggerDescription.add(new CSpan("HOST", "unknown"));
						triggerDescription.add(":");
					} else {
						Map realHost = reset((CArray<Map>)Nest.value(data,"triggerRealHosts",trigger.get("triggerid")).asCArray());
					/*	triggerDescription.add(new CLink(
							CHtml.encode(Nest.value(realHost,"name").asString()),
							"triggers.action?hostid="+Nest.value(realHost,"hostid").$(),
							"unknown"
						));*/
						name = Nest.value(realHost,"name").asString();
						url = "'"+_("Trigger")+"', '"+common_action_with_context+"triggers.action?hostid="+Nest.value(realHost,"hostid").$()+"'";
						triggerDescription.add(new CLink(
								name,
								IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"unknown",null,Boolean.TRUE
							));
						triggerDescription.add(":");
					}
				}

				Nest.value(trigger,"hosts").$(rda_toHash(Nest.value(trigger,"hosts").$(), "hostid"));

				if (Nest.value(trigger,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED) {
					triggerDescription.add(new CSpan(CHtml.encode(Nest.value(trigger,"description").asString())));
				} else {
					String hostid = "";
					CArray hosts = Nest.value(trigger, "hosts").asCArray();
					if(hosts!=null && !hosts.isEmpty()){
						hostid = Nest.value((Map)hosts.iterator().next(),"hostid").asString(true);
					}
					
					name =  Nest.value(trigger,"description").asString();
					url = "'"+_("Trigger")+"', '"+common_action_with_context+"triggers.action?form=update&hostid="+hostid+"&triggerid="+Nest.value(trigger,"triggerid").$()+"'";
					triggerDescription.add(new CLink(
						name,
						IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),null,null,Boolean.TRUE
					));
				}

				if (Nest.value(trigger,"state").asInteger() == TRIGGER_STATE_UNKNOWN) {
					Nest.value(trigger,"error").$("");
				}

				Nest.value(trigger,"items").$(rda_toHash(Nest.value(trigger,"items").$(), "itemid"));
				Nest.value(trigger,"functions").$(rda_toHash(Nest.value(trigger,"functions").$(), "functionid"));
				
				((CTableInfo)triggerHintTable).addRow(array(
					BlocksUtil.getTriggerLevel(Nest.value(trigger,"priority").asInteger(),idBean, executor),
					triggerDescription,
					triggerExpression(trigger, true),
					new CSpan(
						triggerIndicator(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger()),
						triggerIndicatorStyle(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger())
					)
				));

				Nest.value(item,"triggers", num).$(trigger);
			}

			Object triggerInfo = null;
			if (!empty(Nest.value(item,"triggers").$())) {
				CSpan ctriggerInfo = new CSpan(_("Triggers relevance"), "link_menu");
				ctriggerInfo.setHint(triggerHintTable);
				triggerInfo  = array(ctriggerInfo);
				((CArray)triggerInfo).add(" ("+count(Nest.value(item,"triggers").$())+")");
				triggerHintTable = array();
			} else {
				triggerInfo = SPACE;
			}

			// if item type is "Log" we must show log menu
			Object menuIcon = null;
			if (in_array(Nest.value(item,"value_type").asInteger(), new Integer[]{ITEM_VALUE_TYPE_LOG, ITEM_VALUE_TYPE_STR, ITEM_VALUE_TYPE_TEXT})) {
				boolean triggersFlag = false;
				String triggers = "Array(\""+_("Edit trigger")+"\", null, null, {\"outer\" : \"pum_o_submenu\", \"inner\" : [\"pum_i_submenu\"]}"+"\n";

				for(Map trigger : (CArray<Map>)Nest.value(item,"triggers").asCArray()) {
					boolean gocontinue = false;
					for(Map function : (CArray<Map>)Nest.value(trigger,"functions").asCArray()) {
						if (!str_in_array(Nest.value(function,"function").$(), array("regexp", "iregexp"))) {
							gocontinue = true;
							continue;
						}
					}
					if(gocontinue){
						continue;
					}

					triggers += ", [\""+Nest.value(trigger,"description").$()+"\","+
						rda_jsvalue("javascript: openWinCentered('tr_logform.action?sform=1&itemid="+Nest.value(item,"itemid").$()+
							"&triggerid="+Nest.value(trigger,"triggerid").$()+
							"','TriggerLog', 760, 540,"+
								"'titlebar=no, resizable=yes, scrollbars=yes');")+"]";
					triggersFlag = true;
				}

				if (triggersFlag) {
					triggers = rtrim(triggers, ",")+")";
				} else {
					triggers = "Array()";
				}

				menuIcon  = new CIcon(
					_("Menu"),
					"iconmenu_b",
					"call_triggerlog_menu("+
						"event, "+
						CJs.encodeJson(Nest.value(item,"itemid").$())+", "+
						CJs.encodeJson(CHtml.encode(Nest.value(item,"name").asString()))+", "+
						triggers+
					");"
				);
			} else {
				menuIcon = SPACE;
			}
			
			CCheckBox checkBox = new CCheckBox("group_itemid["+Nest.value(item,"itemid").$()+"]", false, null, Nest.value(item,"itemid").asInteger());
			checkBox.setEnabled(empty(Nest.value(item,"discoveryRule").$()));

			itemTable.addRow(array(
				checkBox,
				menuIcon,
				empty(Nest.value(data,"filter_hostid").$()) ? Nest.value(item,"host").$() : null,
				description,
				triggerInfo,
				CHtml.encode(Nest.value(item,"key_").asString()),
				Nest.value(item,"type").asInteger() == ITEM_TYPE_TRAPPER || Nest.value(item,"type").asInteger() == ITEM_TYPE_SNMPTRAP ? "" : Nest.value(item,"delay").$(),
				Nest.value(item,"history").$(),
				in_array(Nest.value(item,"value_type").asInteger(), new Integer[]{ITEM_VALUE_TYPE_STR, ITEM_VALUE_TYPE_LOG, ITEM_VALUE_TYPE_TEXT}) ? "" : Nest.value(item,"trends").$(),
				item_type2str(Nest.value(item,"type").asInteger()),
				new CCol(CHtml.encode(Nest.value(item,"applications_list").asCArray()), "wraptext"),
				status,
				!empty(Nest.value(data,"showErrorColumn").$()) ? statusIcons : null
			));
		}
		// append table to form
		itemForm.addItem(array(itemTable, Nest.value(data,"paging").$()));

		// append form to widget
		itemsWidget.addItem(itemForm);

		return itemsWidget;
	}
}