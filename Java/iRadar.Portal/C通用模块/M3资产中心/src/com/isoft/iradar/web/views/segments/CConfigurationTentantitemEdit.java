package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.tentant.TentantTemplateDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationTentantitemEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CForm itemForm = new CForm();
		itemForm.setName("itemForm");
		TentantTemplateDAO idao=new TentantTemplateDAO(executor);
		List isItemid=new ArrayList();
		if (!empty(Nest.value(data,"itemid").$())) {
			itemForm.addVar("itemid", Nest.value(data,"itemid").$());
			isItemid=idao.isHasViceItemid(Nest.value(data,"itemid").asLong());
		}
		if (!empty(Nest.value(data,"hostid").$())) {
			itemForm.addVar("hostid", Nest.value(data,"hostid").$());
		}
		String name=null;
		String key=null;
		String units=null;
		String statusstr=null;
		if(isItemid.size()!=0){
			List<Map> itemlist=idao.getOneViceItem(Nest.value(data,"itemid").asLong());
			for(Map it : itemlist){
				name=Nest.value(it,"name").asString();
				key=Nest.value(it,"key_").asString();
				units=Nest.value(it,"units").asString();
				statusstr=Nest.value(it,"status").asString();
			}
		}else{
			name=Nest.value(data,"name").asString();
			key=Nest.value(data,"key_").asString();
			units=Nest.value(data,"units").asString();
			statusstr=Nest.value(data,"status").asString();
		}
		
		CFormList itemFormList = new CFormList("itemFormList");
        //名称
		CTextBox nameTextBox = new CTextBox("name", name, RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean());
		nameTextBox.attr("autofocus", "autofocus");
		itemFormList.addRow("指标名称", nameTextBox);
		// 键值
		itemFormList.addRow(_("Key"), array(
			new CTextBox("key", key, RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean()),
			empty(Nest.value(data,"limited").$()) && !Nest.value(data,"is_discovery_rule").asBoolean()
				? new CButton("keyButton", _("Select"),
					"return PopUp(\"popup.action?srctbl=help_items&srcfld1=key"+
						"&dstfrm="+itemForm.getName()+"&dstfld1=key&itemtype=\"+jQuery(\"#type option:selected\").val());",
					"formlist")
				: null
		));
		//单位
		itemFormList.addRow("单位",
				new CTextBox("units", units,RDA_TEXTBOX_STANDARD_SIZE, Nest.value(data,"limited").asBoolean()), false, "row_units"
			);
		//状态
		CCheckBox enabledCheckBox = new CCheckBox("status", empty(statusstr), null, ITEM_STATUS_ACTIVE);
		itemFormList.addRow(_("Enabled"), enabledCheckBox);
		
		CTabView itemTab = new CTabView();
		itemTab.addTab("itemTab", Nest.value(data,"caption").asString(), itemFormList);
		itemForm.addItem(itemTab);
		
		//添加操作按钮
		CArray buttons = array();
		array_push(buttons, new CButtonCancel(url_param(idBean, "groupid")+url_param(idBean, "parent_discoveryid")+url_param(idBean, "hostid")));
		itemForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), buttons));
				
		return itemForm;
	}

}
