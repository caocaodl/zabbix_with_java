package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupUsrgrpAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("User groups"));
		Nest.value(page, "file").$(_("popup_usrgrp.action"));
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields=map(
			"dstfrm",			array(T_RDA_STR, O_MAND,	P_SYS,	NOT_EMPTY,	null),
			"new_groups",	array(T_RDA_STR, O_OPT,		P_SYS,	NOT_EMPTY,	null),
			"select",			array(T_RDA_STR, O_OPT,		P_SYS|P_ACT,	null,	null)
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
		// destination form
		String dstfrm = get_request("dstfrm",	"0");
		CArray new_groups = get_request("new_groups", array());
		
		show_table_header(_("User groups"));
		
		echo("<script language=\"JavaScript\" type=\"text/javascript\">");
		echo("<!--");
		echo("function add_var_to_opener_obj(obj,name,value){");
		echo("        new_variable = window.opener.document.createElement('input');");
		echo("        new_variable.type = 'hidden';");
		echo("        new_variable.name = name;");
		echo("        new_variable.value = value;");
		echo("        obj.appendChild(new_variable);");
		echo("}");
		echo("-->");
		echo("</script>");
		
		if(isset(_REQUEST,"select") && count(new_groups) > 0){
			echo("<script language=\"JavaScript\" type=\"text/javascript\">");
			echo("form = window.opener.document.forms['"+dstfrm+"'];");
			echo("<!--");
			for(Object id : new_groups){
				echo("add_var_to_opener_obj(form,\"new_groups["+id+"]\",\""+id+"\");");
			}
			echo("if(form){");
			echo("	form.submit();");
			echo("	close_window();");
			echo("}");
			echo("-->");
			echo("</script>");
		}
		
		CForm form = new CForm();
		form.addVar("dstfrm", dstfrm);

		form.setName("groups");

		CTableInfo table = new CTableInfo(_("No user groups found."));
		table.setHeader(array(
				new CCheckBox("all_groups",false,"checkAll('"+form.getName()+"','all_groups','new_groups');"),
			_("Name")
			));

		CArray<Map> userGroups = DBselect(executor,"SELECT ug.usrgrpid,ug.name FROM usrgrp ug");
		order_result(userGroups, "name");

		for(Map userGroup : userGroups) {
			table.addRow(array(
				new CCheckBox("new_groups["+Nest.value(userGroup,"usrgrpid").asString()+"]",
					isset(new_groups,userGroup.get("usrgrpid")), null, Nest.value(userGroup,"usrgrpid").asInteger()),
					Nest.value(userGroup,"name").asString()
			));
		}

		table.setFooter(new CCol(new CSubmit("select", _("Select"))));

		form.addItem(table);
		form.show();
	}

}
