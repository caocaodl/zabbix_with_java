package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.get_status;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.TranslateDefines.SERVER_INFO_DATE_FORMAT;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CServerInfo extends CTable {

	private static final long serialVersionUID = 1L;
	
	private IIdentityBean idBean;
	private SQLExecutor executor;

	public CServerInfo(IIdentityBean idBean, SQLExecutor executor) {
		super(null, "server_info");
		this.idBean = idBean;
		this.executor = executor;
	}

	@Override
	public StringBuilder bodyToString() {
		cleanItems();
		Map status = get_status(idBean, executor);
		CSpan server = (Nest.value(status,"iradar_server").$() == _("Yes"))
			? new CSpan(_("running"), "off")
			: new CSpan(_("not running"), "on");
		Object serverLink = (CWebUser.getType() == USER_TYPE_SUPER_ADMIN)
			? new CLink(_("iRadar server"), "report1.action")
			: _("iRadar server");

		addRow(new CCol(_("iRadar server info"), "nowrap ui-corner-all ui-widget-header"));
		addRow(_("Updated date")+NAME_DELIMITER+rda_date2str(SERVER_INFO_DATE_FORMAT, time()));
		addRow(_("Users (online)")+NAME_DELIMITER+Nest.value(status,"users_count").$()+"("+Nest.value(status,"users_online").$()+")");
		addRow(new CCol(array(_("Logged in as")+SPACE, new CLink(CWebUser.get("alias"), "profile.action"))));
		addRow(new CCol(array(serverLink, SPACE+_("is")+SPACE, server)), "status");
		addRow(new CCol(array(
			_("Hosts (m/n/t)")+NAME_DELIMITER+Nest.value(status,"hosts_count").$()+"(",
			new CSpan(Nest.value(status,"hosts_count_monitored").$(), "off"),
			"/",
			new CSpan(Nest.value(status,"hosts_count_not_monitored").$(), "on"),
			"/",
			new CSpan(Nest.value(status,"hosts_count_template").$(), "unknown"),
			")"
		)));
		addRow(new CCol(array(
			_("Items (m/d/n)")+NAME_DELIMITER+Nest.value(status,"items_count").$()+"(",
			new CSpan(Nest.value(status,"items_count_monitored").$(), "off"),
			"/",
			new CSpan(Nest.value(status,"items_count_disabled").$(), "on"),
			"/",
			new CSpan(Nest.value(status,"items_count_not_supported").$(), "unknown"),
			")"
		)));
		addRow(new CCol(array(
			_("Triggers (e/d)[p/o]")+NAME_DELIMITER+Nest.value(status,"triggers_count").$()+
			"("+Nest.value(status,"triggers_count_enabled").$()+"/"+Nest.value(status,"triggers_count_disabled").$()+")[",
			new CSpan(Nest.value(status,"triggers_count_on").$(), "on"),
			"/",
			new CSpan(Nest.value(status,"triggers_count_off").$(), "off"),
			"]"
		)));
		return super.bodyToString();
	}	

}
