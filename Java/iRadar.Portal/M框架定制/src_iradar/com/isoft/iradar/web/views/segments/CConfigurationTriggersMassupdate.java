package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSeverity;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CVisibilityBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTriggersMassupdate extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.triggers.edit.js");

		CWidget triggersWidget = new CWidget();

		// append host summary to widget header
		/*if (!empty(Nest.value(data,"hostid").$())) {
			if (!empty(Nest.value(data,"parent_discoveryid").$())) {
				triggersWidget.addItem(
					get_header_host_table(executor, "triggers", Nest.value(data,"hostid").asLong(true), Nest.value(data,"parent_discoveryid").asLong(true))
				);
			} else {
				triggersWidget.addItem(get_header_host_table(executor, "triggers", Nest.value(data,"hostid").asLong(true)));
			}
		}*/

		// create form
		CForm triggersForm = new CForm();
		triggersForm.setName("triggersForm");
		triggersForm.addVar("massupdate", Nest.value(data,"massupdate").$());
		triggersForm.addVar("hostid", Nest.value(data,"hostid").$());
		triggersForm.addVar("go", Nest.value(data,"go").$());
		if (!empty(Nest.value(data,"parent_discoveryid").$())) {
			triggersForm.addVar("parent_discoveryid", Nest.value(data,"parent_discoveryid").$());
		}
		for(Object triggerid : Nest.value(data,"g_triggerid").asCArray()) {
			triggersForm.addVar("g_triggerid["+triggerid+"]", triggerid);
		}

		// create form list
		CFormList triggersFormList = new CFormList("triggersFormList");

		// append severity to form list
		CSeverity severityDiv = new CSeverity(idBean, executor, map(
			"id", "priority_div",
			"name", "priority",
			"value", Nest.value(data,"priority").$()
		));

		triggersFormList.addRow(
			array(
				_("Severity"),
				SPACE,
				CVisibilityBox.instance(
					"visible[priority]",
					isset(Nest.value(data,"visible","priority").$()),
					"priority_div",
					_("Original")
				)
			),
			severityDiv
		);

		// append dependencies to form list
		if (empty(Nest.value(data,"parent_discoveryid").$())) {
			CTable dependenciesTable = new CTable(_("No dependencies defined."), "formElementTable");
			dependenciesTable.setAttribute("style", "min-width: 500px;");
			dependenciesTable.setAttribute("id", "dependenciesTable");
			dependenciesTable.setHeader(array(
				_("Name"),
				_("Operations")
			));

			for(Map dependency :(CArray<Map>)Nest.value(data,"dependencies").asCArray()) {
				triggersForm.addVar("dependencies[]", Nest.value(dependency,"triggerid").$(), "dependencies_"+Nest.value(dependency,"triggerid").$());

				CRow row = new CRow(array(
					dependency.get("host")+NAME_DELIMITER+Nest.value(dependency,"description").$(),
					new CButton(
						"remove",
						_("Remove"),
						"javascript: removeDependency(\""+dependency.get("triggerid")+"\");",
						"link_menu"
					)
				));
				row.setAttribute("id", "dependency_"+Nest.value(dependency,"triggerid").$());
				dependenciesTable.addRow(row);
			}

			CDiv dependenciesDiv = new CDiv(
				array(
					dependenciesTable,
					/**
					 * 此处的dstfld1=new_dependency[]造成批量更新页面中无法添加trigger的依赖关系
					 * 经过与zabbix原生界面对比,这是由于jQuery造成的,zabbix的web界面使用jQuery版本是1.7的,本项目使用的是1.10,
					 * 1.10版本的选择器禁止Id中包含：[],故无法添加,将此处的dstfld1=new_dependency[]修改为""
					 */
/*					new CButton("btn1", _("Add"),
						"return PopUp(\"popup.action?"+
							"dstfrm=massupdate"+
							"&dstact=add_dependency"+
							"&reference=deptrigger"+
							"&dstfld1=new_dependency[]"+
							"&srctbl=triggers"+
							"&objname=triggers"+
							"&srcfld1=triggerid"+
							"&multiselect=1"+
							"&with_triggers=1\", 1000, 700);",
						"link_menu"
					)*/
					new CButton("btn1", _("Add"),
						"return PopUp(\"popup.action?"+
							"dstfrm=massupdate"+
							"&dstact=add_dependency"+
							"&reference=deptrigger"+
							"&dstfld1="+
							"&srctbl=triggers"+
							"&objname=triggers"+
							"&srcfld1=triggerid"+
							"&multiselect=1"+
							"&with_triggers=1\", 1000, 700);",
						"link_menu"
					)
				),
				"objectgroup inlineblock border_dotted ui-corner-all"
			);
			dependenciesDiv.setAttribute("id", "dependencies_div");

			triggersFormList.addRow(
				array(
					_("Replace depenencies"),
					SPACE,
					CVisibilityBox.instance(
						"visible[dependencies]",
						isset(Nest.value(data,"visible","dependencies").$()),
						"dependencies_div",
						_("Original")
					)
				),
				dependenciesDiv
			);
		}

		// append tabs to form
		CTabView triggersTab = new CTabView();
		triggersTab.addTab("triggersTab", _("Triggers massupdate"), triggersFormList);
		triggersForm.addItem(triggersTab);

		// append buttons to form
		triggersForm.addItem(makeFormFooter(
			new CSubmit("mass_save", _("Save")),
			new CButtonCancel(url_params(idBean, array("groupid", "hostid", "parent_discoveryid")))
		));

		triggersWidget.addItem(triggersForm);

		return triggersWidget;
	}

}
