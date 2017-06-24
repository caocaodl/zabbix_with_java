package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_object;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.ItemsUtil.get_realrule_by_itemid_and_hostid;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.get_hosts_by_triggerid;
import static com.isoft.iradar.inc.TriggersUtil.triggerExpression;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicator;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicatorStyle;
import static com.isoft.types.CArray.array;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
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
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.action.core.TriggersAction;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTriggersList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		
		CWidget triggersWidget = new CWidget();
		
		CForm triggersForm = new CForm();
		triggersForm.setName("triggersForm");
		triggersForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		
		// create new trigger button
		triggersForm.addVar("hostid", Nest.value(data,"hostid").$());
		
		CToolBar tb = new CToolBar(triggersForm);
		
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			tb.addSubmit("form", _("Create trigger prototype"),"","orange setup");
			triggersForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
			triggersWidget.addPageHeader(_("CONFIGURATION OF TRIGGER PROTOTYPES"), triggersForm);
		} else {
			CForm createTriggerForm = new CForm("get");
			createTriggerForm.setAttribute("style", "display: inline;");
			createTriggerForm.cleanItems();
			createTriggerForm.addVar("actionType", TriggersAction.CREATE);
			createTriggerForm.addVar("hostid", Nest.value(data,"hostid").$());
			CSubmit allButton = new CSubmit("form",  _("Create trigger"), "", "orange create");
			createTriggerForm.addItem(allButton);
			tb.addItem(createTriggerForm);
//			if (empty(Nest.value(data,"hostid").$())) {
//				tb.addSubmit("form", _("Create trigger"),"","orange create","readonly");
//				show_messages(false,"",_("select host first"));
//			} else {
//				tb.addSubmit("form", _("Create trigger"),"","orange create");
//			}
		}
		
		CArray<CComboItem> goComboBox = array();
		
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Enable selected trigger prototypes?") : _("Enable selected triggers?")
		);
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Disable selected trigger prototypes?") : _("Disable selected triggers?")
		);
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("massupdate", _("Mass update"));
		goOption.setAttribute("class", "orange massupdate");
		goComboBox.add(goOption);
		
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			goOption = new CComboItem("copy_to", _("Copy selected to ..."));
			goOption.setAttribute("class", "orange copy");
			goComboBox.add(goOption);
		}

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete selected trigger prototypes?") : _("Delete selected triggers?")
		);
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"g_triggerid\";");
		
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			if (!empty(Nest.value(data,"hostid").$())) {
				rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"hostid").$()+"\";");
				rda_add_post_js("cookie.prefix = \""+Nest.value(data,"hostid").$()+"\";");
			}
		} else {
			rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"parent_discoveryid").$()+"\";");
			rda_add_post_js("cookie.prefix = \""+Nest.value(data,"parent_discoveryid").$()+"\";");
		}
		
		// create widget header
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			triggersWidget.addHeader(array(_("Trigger prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));
		} else {
			//查询
			CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
			CForm filterForm = new CForm("get");
			filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
			filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));
			triggersWidget.addHeader(filterForm);
			
			//创建按钮
			CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
			triggersWidget.addItem(headerActions);
		}
		/*if (!empty(Nest.value(data,"hostid").$())) {//注释掉用不到的表头
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				triggersWidget.addItem(get_header_host_table(executor,"triggers", Nest.value(data,"hostid").asLong(), Nest.value(data,"parent_discoveryid").asLong()));
			} else {
				triggersWidget.addItem(get_header_host_table(executor,"triggers", Nest.value(data,"hostid").asLong()));
			}
		}*/
		
		// create table
		Curl clink = new Curl();
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			clink.setArgument("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		clink.setArgument("hostid", Nest.value(data,"hostid").$());
		String link = clink.getUrl();
		CTableInfo triggersTable = new CTableInfo(_("No triggers found."));
		String triggerClass = triggersTable.getAttribute("class").toString();
		if(empty(Nest.value(data,"hostid").$())){
			triggerClass += " detailall";
		}else{
			triggerClass += " normaldisplay";	
		}
		triggersTable.setAttribute("class", triggerClass);
		triggersTable.setHeader(array(
			new CCheckBox("all_triggers", false, "checkAll(\""+triggersForm.getName()+"\", \"all_triggers\", \"g_triggerid\");"),
			make_sorting_header(_("Priority"), "priority", link,idBean,executor),
			empty(Nest.value(data,"hostid").$()) ? _("Host") : null,
			make_sorting_header(_("Name"), "description", link,idBean,executor),
			_("Expression"),
			make_sorting_header(_("Status"), "status", link,idBean,executor),
			Nest.value(data,"showErrorColumn").asBoolean() ? _("Error") : null
		));
		for (Map  trigger : (CArray<Map>)Nest.value(data,"triggers").asCArray()) {
			String triggerid = Nest.value(trigger,"triggerid").asString();
			Nest.value(trigger,"discoveryRuleid").$(Nest.value(data,"parent_discoveryid").$());
			CArray description = array();

			Nest.value(trigger,"hosts").$(rda_toHash(Nest.value(trigger,"hosts").$(), "hostid"));
			Nest.value(trigger,"items").$(rda_toHash(Nest.value(trigger,"items").$(), "itemid"));
			Nest.value(trigger,"functions").$(rda_toHash(Nest.value(trigger,"functions").$(), "functionid"));

			if (Nest.value(trigger,"templateid").asLong() > 0) {
				if (!isset(Nest.value(data,"realHosts",triggerid).$())) {
					description.add(new CSpan(empty(Nest.value(data,"parent_discoveryid").$()) ? _("Host") : _("Template"), "unknown"));
					description.add(NAME_DELIMITER);
				} else {
					CArray<Map> real_dhosts = Nest.value(data,"realHosts",triggerid).asCArray();
					Map real_host = reset(real_dhosts);

					if (!empty(Nest.value(data,"parent_discoveryid").$())) {
						String tpl_disc_ruleid = get_realrule_by_itemid_and_hostid(executor, Nest.value(data,"parent_discoveryid").asString(), Nest.value(real_host,"hostid").asString());
//						description.add(new CLink(
//							CHtml.encode(Nest.value(real_host,"name").asString()),
//							"trigger_prototypes.action?hostid="+Nest.value(real_host,"hostid").asString()+"&parent_discoveryid="+tpl_disc_ruleid,
//							"unknown"
//						));
						description.add(new CSpan(CHtml.encode(Nest.value(real_host,"name").asString())));
					} else {
						description.add(new CLink(
							CHtml.encode(Nest.value(real_host,"name").asString()),
							"triggers.action?hostid="+Nest.value(real_host,"hostid").$(),
							"unknown"
						));
					}
					description.add(NAME_DELIMITER);
				}
			}

			if (empty(Nest.value(data,"parent_discoveryid").$())) {
				if (!empty(Nest.value(trigger,"discoveryRule").$())) {
//					description.add(new CLink(
//						CHtml.encode(Nest.value(trigger,"discoveryRule","name").asString()),
//						"trigger_prototypes.action?"+
//							"hostid="+Nest.value(data,"hostid").$()+"&parent_discoveryid="+Nest.value(trigger,"discoveryRule","itemid").$(),
//						"parent-discovery"
//					));
					description.add(new CSpan(CHtml.encode(Nest.value(trigger,"discoveryRule","name").asString())));
					description.add(NAME_DELIMITER+Nest.value(trigger,"description").$());
				} else {
					description.add(new CLink(
						CHtml.encode(Nest.value(trigger,"description").asString()),
						"triggers.action?form=update&hostid="+Nest.value(data,"hostid").$()+"&triggerid="+triggerid
					));
				}

				CArray<Map> dependencies = Nest.value(trigger,"dependencies").asCArray();
				if (count(dependencies) > 0) {
					description.add(array(BR(), bold(_("Depends on")+NAME_DELIMITER)));
					for(Map dep_trigger : dependencies) {
						description.add(BR());

						CArray<Map> db_hosts = get_hosts_by_triggerid(idBean, executor, Nest.value(dep_trigger,"triggerid").asLong());
						for (Map host : db_hosts) {
							description.add(CHtml.encode(Nest.value(host,"name").asString()));
							description.add(", ");
						}
						array_pop(description);
						description.add(NAME_DELIMITER);
						description.add(CHtml.encode(Nest.value(dep_trigger,"description").asString()));
					}
				}
			} else {
//				description.add(new CLink(
//					CHtml.encode(Nest.value(trigger,"description").asString()),
//					"trigger_prototypes.action?"+
//						"form=update"+
//						"&hostid="+Nest.value(data,"hostid").$()+
//						"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").$()+
//						"&triggerid="+triggerid
//				));
				description.add(new CSpan(CHtml.encode(Nest.value(trigger,"description").asString())));
			}

			Object error = null;
			if (Nest.value(data,"showErrorColumn").asBoolean()) {
				error  = "";
				if (Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_ENABLED) {
					if (!rda_empty(Nest.value(trigger,"error").$())) {
						error = new CDiv(SPACE, "status_icon iconerror");
						((CDiv)error).setHint(Nest.value(trigger,"error").$(), "", "on");
					} else {
						error = new CDiv(SPACE, "status_icon iconok");
					}
				}
			}
			//阀值规则状态修改ajax提交时所需要的值
			Object status = "";
			Object _go = "";
			Object _hostid = "";
			Object _g_triggerid = "";
			Object _sid = "";
			String _random = getronDowm(); 	//获取随机数
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				status = new CLink(
					triggerIndicator(Nest.value(trigger,"status").asInteger()),
					"trigger_prototypes.action?"+
						"go="+(Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED ? "activate" : "disable")+
						"&hostid="+Nest.value(data,"hostid").$()+
						"&g_triggerid="+triggerid+
						"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").$(),
					triggerIndicatorStyle(Nest.value(trigger,"status").asInteger())
				);
				_go = (Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED ? "activate" : "disable");
				_hostid = Nest.value(data,"hostid").$();
				_g_triggerid = triggerid;
			} else {
				status = new CLink(
					triggerIndicator(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger()),
					"triggers.action?"+
						"go="+(Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED ? "activate" : "disable")+
						"&hostid="+Nest.value(data,"hostid").$()+
						"&g_triggerid="+triggerid,
					triggerIndicatorStyle(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger())
				);
				
				_go = (Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED ? "activate" : "disable");
				_hostid = Nest.value(data,"hostid").$();
				_g_triggerid = triggerid;
			}
			
			//阀值规则状态链接中 添加自定义属性 以用作ajax提交时取值
			CLink statusLink = (CLink)status;
			String[] aks = statusLink.getUrl().split("=");
			_sid = aks[aks.length-1];
			statusLink.setAttribute("go", _go);
			statusLink.setAttribute("hostid", _hostid);
			statusLink.setAttribute("g_triggerid", _g_triggerid);
			statusLink.setAttribute("sid", _sid);
			statusLink.setAttribute("onclick", "changeStatus(this)");
			statusLink.setAttribute("href", "javascript:void(0)");
			statusLink.setAttribute("random", _random);

			CDiv errordiv = new CDiv();
			if(!Cphp.empty(error)){				
				errordiv = (CDiv)error;
			}
			errordiv.setAttribute("id", _random);
			
			CCol cstatus = new CCol(new CDiv(statusLink, "switch"));
			CArray hosts = null;
			if (empty(Nest.value(data,"hostid").$())) {
				hosts = array();
				for(Map host : (CArray<Map>)Nest.value(trigger,"hosts").asCArray()) {
					if (!empty(hosts)) {
						hosts.add(", ");
					}
					hosts.add(Nest.value(host,"name").$());
				}
			}

			CCheckBox checkBox = new CCheckBox("g_triggerid["+triggerid+"]", false, null, triggerid);
			checkBox.setEnabled(empty(Nest.value(trigger,"discoveryRule").$()));

			CCol expressionColumn = new CCol(triggerExpression(trigger, true));
			expressionColumn.setAttribute("style", "white-space: normal;");

			triggersTable.addRow(array(
				checkBox,
//				getSeverityCell(idBean, executor, Nest.value(trigger,"priority").asInteger()),
				BlocksUtil.getTriggerLevel(Nest.value(trigger,"priority").asInteger(),idBean, executor),
				hosts,
				description,
				expressionColumn,
				cstatus,
				Nest.value(data,"showErrorColumn").asBoolean() ? errordiv : null
			));
		}

		// append table to form
		triggersForm.addItem(array(triggersTable, Nest.value(data,"paging").$()));

		// append form to widget
		triggersWidget.addItem(triggersForm);

		return triggersWidget;
	}
	
	/**
	 * 获取随机数 
	 * @return
	 */
	private String getronDowm(){
	     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");  
		 Date date = new Date();  
	     Random random = new Random();  
	     int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数  
	     return "ID"+rannum + date.getTime();// ID标识+5为随机数+当前系统时间毫秒数
	}
	
	/**
	 * 将FuncUtil中的方法抽取，作为本地方法，如果后续各模块使用频繁，可将其整合到FuncUtil中
	 */
	public static CCol make_sorting_header(Object obj, String tabfield,IIdentityBean idBean, SQLExecutor executor) {
		return make_sorting_header(obj, tabfield, "",idBean,executor);
	}
	
	public static CCol make_sorting_header(Object obj, String tabfield, String url,IIdentityBean idBean, SQLExecutor executor) {
		RadarContext ctx = RadarContext.getContext();
		Map<String, Object> page = RadarContext.page();
		String sort = ctx.getRequest().getParameter("sort");
		String order = ctx.getRequest().getParameter("sortorder");
		String sortorder = "";
		if(empty(order))
			sortorder = Defines.RDA_SORT_UP.equals(CProfile.get(idBean, executor, "web."+RadarContext.page().get("file")+".sortorder"))?Defines.RDA_SORT_DOWN:Defines.RDA_SORT_UP;
		else
			sortorder = tabfield.equals(sort) && RDA_SORT_UP.equals(order)?RDA_SORT_DOWN : RDA_SORT_UP;

		Curl link = new Curl(url);
		if (empty(url)) {
			link.formatGetArguments();
		}
		link.setArgument("sort", tabfield);
		link.setArgument("sortorder", sortorder);

		url = link.getUrl();
		
		String script;
		int type = (Integer)page.get("type");
		if (type!= PAGE_TYPE_HTML && defined("RDA_PAGE_MAIN_HAT")) {
			script = "javascript: return updater.onetime_update('hat_latest', '"+url+"');";
		}
		else {
			script = "javascript: redirect(\""+url+"\");";
		}

		obj = FuncsUtil.toArray(obj);
		CSpan cont = new CSpan();
		
		int len = Array.getLength(obj);
		for (int i = 0; i < len; i++) {
			Object el = Array.get(obj, i);
			if (is_object(el) || SPACE.equals(el)) {
				cont.addItem(el);
			} else {
				cont.addItem(new CSpan(el, "underline"));
			}
		}
		cont.addItem(SPACE);
		CSpan img = null;
		if (isset(sort) && sort.equals(tabfield)) {
			if (RDA_SORT_UP.equals(sortorder)) {
				img = new CSpan(SPACE, "icon_sortdown");
			} else {
				img = new CSpan(SPACE, "icon_sortup");
			}
		}
		CCol col = new CCol(new CSpan[]{cont, img}, "nowrap hover_grey");
		col.setAttribute("onclick", script);

		return col;
	}
}
