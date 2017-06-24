package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationHostgroupsEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		boolean isVM = IMonGroup.MON_VM.id().equals(Nest.value(data,"groupid").asLong());
		CWidget hostGroupWidget = new CWidget();
		// create form
		CForm hostGroupForm = new CForm();
		hostGroupForm.setName("hostgroupForm");
		hostGroupForm.addVar("form", Nest.value(data,"form").$());
		if (isset(Nest.value(data,"groupid").$())) {
			hostGroupForm.addVar("groupid", Nest.value(data,"groupid").$());
		}

		// create hostgroup form list
		CFormList hostGroupFormList = new CFormList("hostgroupFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE,
			(!empty(Nest.value(data,"groupid").$()) && (Nest.value(data,"group","flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED||getIsSysFunc(data))),
			11
		);
		nameTextBox.attr("autofocus", "autofocus");
		hostGroupFormList.addRow(_("Group name"), nameTextBox);
		if(!isVM){
			CComboBox monCategory = new CComboBox("moncategory", Nest.value(data,"moncategory").$());
			for(Entry<String,String> e:IMonConsts.MON_CATE.entrySet()){
				String key = e.getKey();
				String value = e.getValue();
				monCategory.addItem(key, value);
			}
			if(getIsSysFunc(data))
				monCategory.attr("disabled", "disabled");
			hostGroupFormList.addRow(_("Monitor category"), monCategory);
		}
		// append groups and hosts to form list
		CComboBox groupsComboBox = new CComboBox("twb_groupid", Nest.value(data,"twb_groupid").$(), "submit()");
		
		if(!isVM){
			groupsComboBox.addItem("0", _("All"));
			for(Map group : (CArray<Map>)Nest.value(data,"db_groups").asCArray()) {
				String name = Nest.value(group,"name").asString();
				if("Templates".equals(name) || "Discovered hosts".equals(name)){//模型、 Discovered hosts  不显示选择							
				}else{
					groupsComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());	
				}
			}
		}
		
		//监控模型
		CTweenBox templatesComboBox = new CTweenBox(hostGroupForm, "templates", Nest.value(data,"templates").$(), 10);
		for(Map template : (CArray<Map>)Nest.value(data,"db_templates").asCArray()) {
			templatesComboBox.addItem(Nest.value(template,"hostid").$(), Nest.value(template,"name").asString());
		}		
		
		hostGroupFormList.addRow("默认监控模型", templatesComboBox.get("监控模型在", "监控模型"));
		
		//设备
		/*CTweenBox hostsComboBox = new CTweenBox(hostGroupForm, "hosts", Nest.value(data,"hosts").$(), 10);
		if(!IMonGroup.MON_VM.id().equals(Nest.value(data,"groupid").asLong())){
			for(Map host : (CArray<Map>)Nest.value(data,"db_hosts").asCArray()) {
				if (!isset(Nest.value(data,"hosts",host.get("hostid")).$())) {
					hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString());
				}
			}
		}
		for(Map host : (CArray<Map>)Nest.value(data,"r_hosts").asCArray()) {
			if (isset(Nest.value(data,"r_hosts",host.get("hostid")).$()) && Nest.value(host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
				hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString());
			} else {
				hostsComboBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString(), true, false);
			}
		}
		hostGroupFormList.addRow(_("Hosts"), hostsComboBox.get(_("Hosts in"), array(_("Other hosts | Group")+SPACE, groupsComboBox)));
	*/	if(IMonGroup.MON_VM.id().equals(Nest.value(data,"groupid").asLong())){
			String disableAddBtn = "jQuery('#hosts_tweenbox #add').attr(\"disabled\",\"disabled\"); ";
			String disableRmvBtn = "jQuery('#hosts_tweenbox #remove').attr(\"disabled\",\"disabled\");";
			JsUtil.insert_js(disableAddBtn+disableRmvBtn, true);
		}
		// append tabs to form
		CTabView hostGroupTab = new CTabView();
		hostGroupTab.addTab("hostgroupTab", _("Host group"), hostGroupFormList);
		hostGroupForm.addItem(hostGroupTab);

		// append buttons to form
		if (empty(Nest.value(data,"groupid").$())) {
			hostGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		} else {

			hostGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					new CButtonCancel())
			));
		}

		hostGroupWidget.addItem(hostGroupForm);

		return hostGroupWidget;
	}
	
	/**
	 * 设置 系统默认的菜单名称不能修改
	 * @param executor
	 */
	public static boolean getIsSysFunc(Map data){
		Long groupid = Nest.value(data, "groupid").asLong();
		return IMonGroup.showableGroups().containsKey(groupid);
	}

}
