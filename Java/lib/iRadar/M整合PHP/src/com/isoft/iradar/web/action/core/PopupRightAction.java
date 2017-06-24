package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.PermUtil.permission2str;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupRightAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Resource"));
		Nest.value(page, "file").$(_("popup_right.action"));
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR					TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"dstfrm",		array(T_RDA_STR, O_MAND,P_SYS, NOT_EMPTY,	null),
			"permission",array(T_RDA_INT, O_MAND,P_SYS, IN(PERM_DENY+","+PERM_READ+","+PERM_READ_WRITE), null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		String dstfrm = get_request("dstfrm",	"0");
		Integer permission = get_request("permission", PERM_DENY);

		/* Display  */
		CForm titleFrom = new CForm();
		titleFrom.addVar("dstfrm", dstfrm);
		titleFrom.addVar("permission", permission);

		show_table_header(permission2str(permission), titleFrom);

		// host groups
		CForm hostGroupForm = new CForm();
		hostGroupForm.setAttribute("id", "groups");

		CTableInfo hostGroupTable = new CTableInfo(_("No host groups found."));
		hostGroupTable.setHeader(new CCol(array(
			new CCheckBox("all_groups", false, "checkAll(this.checked)"),
			_("Name")
		)));

		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid", "name"});
		
		CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		order_result(hostGroups, "name");

		for(Map hostGroup : hostGroups) {
			CCheckBox hostGroupCheckBox = new CCheckBox();
			hostGroupCheckBox.setAttribute("data-id", Nest.value(hostGroup,"groupid").$());
			hostGroupCheckBox.setAttribute("data-name", Nest.value(hostGroup,"name").$());
			hostGroupCheckBox.setAttribute("data-permission", permission);

			hostGroupTable.addRow(new CCol(array(hostGroupCheckBox, Nest.value(hostGroup,"name").$())));
		}

		hostGroupTable.setFooter(new CCol(new CButton("select", _("Select"), "addGroups(\""+dstfrm+"\")"), "right"));

		hostGroupForm.addItem(hostGroupTable);
		hostGroupForm.show();
		
		echo(getJsTemplate("javascript_for_popup_right"));
	}

}
