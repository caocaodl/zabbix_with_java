package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.get_header_host_table;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.ItemsUtil.get_realrule_by_itemid_and_hostid;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.get_hosts_by_triggerid;
import static com.isoft.iradar.inc.TriggersUtil.triggerExpression;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicator;
import static com.isoft.iradar.inc.TriggersUtil.triggerIndicatorStyle;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTriggersList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget triggersWidget = new CWidget();

		// append host summary to widget header
		if (!empty(Nest.value(data,"hostid").$())) {
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				triggersWidget.addItem(get_header_host_table(idBean, executor, "triggers", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true)));
			} else {
				triggersWidget.addItem(get_header_host_table(idBean, executor, "triggers", Nest.value(data,"hostid").asLong(true)));
			}
		}

		// create new application button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("hostid", Nest.value(data,"hostid").$());

		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			createForm.addItem(new CSubmit("form", _("Create trigger prototype")));
			createForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
			triggersWidget.addPageHeader(_("CONFIGURATION OF TRIGGER PROTOTYPES"), createForm);
		} else {
			if (empty(Nest.value(data,"hostid").$())) {
				CSubmit createButton = new CSubmit("form", _("Create trigger (select host first)"));
				createButton.setEnabled(false);
				createForm.addItem(createButton);
			} else {
				createForm.addItem(new CSubmit("form", _("Create trigger")));
			}

			triggersWidget.addPageHeader(_("CONFIGURATION OF TRIGGERS"), createForm);
		}

		// create widget header
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			triggersWidget.addHeader(array(_("Trigger prototypes of")+SPACE, new CSpan(Nest.value(data,"discovery_rule","name").$(), "parent-discovery")));
			triggersWidget.addHeaderRowNumber(array(
				"[ ",
				new CLink(
					Nest.value(data,"showdisabled").asBoolean() ? _("Hide disabled trigger prototypes") : _("Show disabled trigger prototypes"),
					"trigger_prototypes.action?"+
						"showdisabled="+(Nest.value(data,"showdisabled").asBoolean() ? 0 : 1)+
						"&hostid="+Nest.value(data,"hostid").$()+
						"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").$()
				),
				" ]"
			));
		} else {
			CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
			CForm filterForm = new CForm("get");
			filterForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
			filterForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));

			triggersWidget.addHeader(_("Triggers"), filterForm);
			triggersWidget.addHeaderRowNumber(array(
				"[ ",
				new CLink(
					Nest.value(data,"showdisabled").asBoolean() ? _("Hide disabled triggers") : _("Show disabled triggers"),
					"triggers.action?"+
						"hostid="+Nest.value(data,"hostid").$()+
						"&showdisabled="+(Nest.value(data,"showdisabled").asBoolean() ? 0 : 1)
				),
				" ]"
			));
		}

		// create form
		CForm triggersForm = new CForm();
		triggersForm.setName("triggersForm");
		triggersForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		triggersForm.addVar("hostid", Nest.value(data,"hostid").$());

		// create table
		Curl clink = new Curl();
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			clink.setArgument("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		clink.setArgument("hostid", Nest.value(data,"hostid").$());
		String link = clink.getUrl();

		CTableInfo triggersTable = new CTableInfo(_("No triggers found."));
		triggersTable.setHeader(array(
			new CCheckBox("all_triggers", false, "checkAll(\""+triggersForm.getName()+"\", \"all_triggers\", \"g_triggerid\");"),
			make_sorting_header(_("Severity"), "priority", link),
			empty(Nest.value(data,"hostid").$()) ? _("Host") : null,
			make_sorting_header(_("Name"), "description", link),
			_("Expression"),
			make_sorting_header(_("Status"), "status", link),
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
						description.add(new CLink(
							CHtml.encode(Nest.value(real_host,"name").asString()),
							"trigger_prototypes.action?hostid="+Nest.value(real_host,"hostid").asString()+"&parent_discoveryid="+tpl_disc_ruleid,
							"unknown"
						));
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
					description.add(new CLink(
						CHtml.encode(Nest.value(trigger,"discoveryRule","name").asString()),
						"trigger_prototypes.action?"+
							"hostid="+Nest.value(data,"hostid").$()+"&parent_discoveryid="+Nest.value(trigger,"discoveryRule","itemid").$(),
						"parent-discovery"
					));
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
				description.add(new CLink(
					CHtml.encode(Nest.value(trigger,"description").asString()),
					"trigger_prototypes.action?"+
						"form=update"+
						"&hostid="+Nest.value(data,"hostid").$()+
						"&parent_discoveryid="+Nest.value(data,"parent_discoveryid").$()+
						"&triggerid="+triggerid
				));
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

			Object status = "";
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
			} else {
				status = new CLink(
					triggerIndicator(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger()),
					"triggers.action?"+
						"go="+(Nest.value(trigger,"status").asInteger() == TRIGGER_STATUS_DISABLED ? "activate" : "disable")+
						"&hostid="+Nest.value(data,"hostid").$()+
						"&g_triggerid="+triggerid,
					triggerIndicatorStyle(Nest.value(trigger,"status").asInteger(), Nest.value(trigger,"state").asInteger())
				);
			}

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
				getSeverityCell(idBean, executor, Nest.value(trigger,"priority").asInteger()),
				hosts,
				description,
				expressionColumn,
				status,
				Nest.value(data,"showErrorColumn").asBoolean() ? error : null
			));
		}

		// create go button
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Enable selected trigger prototypes?") : _("Enable selected triggers?")
		);
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Disable selected trigger prototypes?") : _("Disable selected triggers?")
		);
		goComboBox.addItem(goOption);

		goOption = new CComboItem("massupdate", _("Mass update"));
		goComboBox.addItem(goOption);
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			goOption = new CComboItem("copy_to", _("Copy selected to ..."));
			goComboBox.addItem(goOption);
		}

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute(
			"confirm",
			!empty(Nest.value(data,"parent_discoveryid").$()) ? _("Delete selected trigger prototypes?") : _("Delete selected triggers?")
		);
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");

		rda_add_post_js("chkbxRange.pageGoName = \"g_triggerid\";");
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"hostid").$()+"\";");
			rda_add_post_js("cookie.prefix = \""+Nest.value(data,"hostid").$()+"\";");
		} else {
			rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"parent_discoveryid").$()+"\";");
			rda_add_post_js("cookie.prefix = \""+Nest.value(data,"parent_discoveryid").$()+"\";");
		}

		// append table to form
		triggersForm.addItem(array(Nest.value(data,"paging").$(), triggersTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		triggersWidget.addItem(triggersForm);

		return triggersWidget;
	}

}
