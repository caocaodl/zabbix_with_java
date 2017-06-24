package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.ALERT_MAX_RETRIES;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_NOT_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_STATUS_SENT;
import static com.isoft.iradar.inc.Defines.ALERT_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RECIPIENT_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RECIPIENT_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RETRIES_LEFT_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RETRIES_LEFT_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_STATUS_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_STATUS_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TIME_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TIME_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TYPE_ASC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_TYPE_DESC;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.iradar.inc.TranslateDefines.HISTORY_OF_ACTIONS_DATE_FORMAT;
import static com.isoft.types.CArray.array;

import java.util.List;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenActions extends CScreenBase {

	public CScreenActions(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenActions(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		String sortfield = "clock";
		String sortorder = RDA_SORT_DOWN;
		String sorttitle = _("Time");

		switch (Nest.value(screenitem,"sort_triggers").asInteger()) {
			case SCREEN_SORT_TRIGGERS_TIME_ASC:
				sortfield = "clock";
				sortorder = RDA_SORT_UP;
				sorttitle = _("Time");
				break;
			case SCREEN_SORT_TRIGGERS_TIME_DESC:
				sortfield = "clock";
				sortorder = RDA_SORT_DOWN;
				sorttitle = _("Time");
				break;
			case SCREEN_SORT_TRIGGERS_TYPE_ASC:
				sortfield = "description";
				sortorder = RDA_SORT_UP;
				sorttitle = _("Type");
				break;
			case SCREEN_SORT_TRIGGERS_TYPE_DESC:
				sortfield = "description";
				sortorder = RDA_SORT_DOWN;
				sorttitle = _("Type");
				break;
			case SCREEN_SORT_TRIGGERS_STATUS_ASC:
				sortfield = "status";
				sortorder = RDA_SORT_UP;
				sorttitle = _("Status");
				break;
			case SCREEN_SORT_TRIGGERS_STATUS_DESC:
				sortfield = "status";
				sortorder = RDA_SORT_DOWN;
				sorttitle = _("Status");
				break;
			case SCREEN_SORT_TRIGGERS_RETRIES_LEFT_ASC:
				sortfield = "retries";
				sortorder = RDA_SORT_UP;
				sorttitle = _("Retries left");
				break;
			case SCREEN_SORT_TRIGGERS_RETRIES_LEFT_DESC:
				sortfield = "retries";
				sortorder = RDA_SORT_DOWN;
				sorttitle = _("Retries left");
				break;
			case SCREEN_SORT_TRIGGERS_RECIPIENT_ASC:
				sortfield = "sendto";
				sortorder = RDA_SORT_UP;
				sorttitle = _("Recipient(s)");
				break;
			case SCREEN_SORT_TRIGGERS_RECIPIENT_DESC:
				sortfield = "sendto";
				sortorder = RDA_SORT_DOWN;
				sorttitle = _("Recipient(s)");
				break;
		}

		String sql = "SELECT a.alertid,a.clock,mt.description,a.sendto,a.subject,a.message,a.status,a.retries,a.error"+
				" FROM events e,alerts a"+
					" LEFT JOIN media_type mt ON mt.tenantid='-' AND mt.mediatypeid=a.mediatypeid"+
				" WHERE e.eventid=a.eventid"+
					" AND alerttype="+ALERT_TYPE_MESSAGE;

		SqlBuilder sqlParts = new SqlBuilder();
		if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
			String userid = Nest.as(CWebUser.get("userid")).asString();
			List<Long> userGroups = getUserGroupsByUserId(this.idBean, executor, userid);
			sql += " AND EXISTS ("+
					"SELECT NULL"+
					" FROM functions f,items i,hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.id=hgg.groupid"+
							" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups.toArray(new Long[0]))+
					" WHERE e.objectid=f.triggerid"+
						" AND f.itemid=i.itemid"+
						" AND i.hostid=hgg.hostid"+
					" GROUP BY f.triggerid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
					")";
		}

		sql += " ORDER BY "+sortfield+" "+sortorder;
		CArray<Map> alerts = DBselect(executor, sql, Nest.value(screenitem,"elements").asInteger(),sqlParts.getNamedParams());

		// indicator of sort field
		CSpan sortfieldSpan = new CSpan(array(sorttitle, SPACE));
		CSpan sortorderSpan = new CSpan(SPACE, (RDA_SORT_DOWN.equals(sortorder)) ? "icon_sortdown default_cursor" : "icon_sortup default_cursor");

		// create alert table
		CTableInfo actionTable = new CTableInfo(_("No actions found."));
		actionTable.setHeader(array(
			("clock".equals(sortfield)) ? array(sortfieldSpan, sortorderSpan) : _("Time"),
			("description".equals(sortfield)) ? array(sortfieldSpan, sortorderSpan) : _("Type"),
			("status".equals(sortfield)) ? array(sortfieldSpan, sortorderSpan) : _("Status"),
			("retries".equals(sortfield)) ? array(sortfieldSpan, sortorderSpan) : _("Retries left"),
			("sendto".equals(sortfield)) ? array(sortfieldSpan, sortorderSpan) : _("Recipient(s)"),
			_("Message"),
			_("Error")
		));

		for(Map alert : alerts) {
			CSpan status = null;
			CSpan retries = null;
			if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_SENT) {
				status = new CSpan(_("sent"), "green");
				retries = new CSpan(SPACE, "green");
			} else if (Nest.value(alert,"status").asInteger() == ALERT_STATUS_NOT_SENT) {
				status = new CSpan(_("In progress"), "orange");
				retries = new CSpan(ALERT_MAX_RETRIES - Nest.value(alert,"retries").asInteger(), "orange");
			} else {
				status = new CSpan(_("not sent"), "red");
				retries = new CSpan(0, "red");
			}

			CArray message = array(
				bold(_("Subject")+NAME_DELIMITER),
				Nest.value(alert,"subject").$(),
				BR(),
				BR(),
				bold(_("Message")+NAME_DELIMITER),
				BR(),
				Nest.value(alert,"message").$()
			);

			CSpan error = empty(Nest.value(alert,"error").$()) ? new CSpan(SPACE, "off") : new CSpan(Nest.value(alert,"error").$(), "on");

			actionTable.addRow(array(
				new CCol(rda_date2str(HISTORY_OF_ACTIONS_DATE_FORMAT, Nest.value(alert,"clock").asLong()), "top"),
				new CCol(!empty(Nest.value(alert,"description").$()) ? Nest.value(alert,"description").$() : "-", "top"),
				new CCol(status, "top"),
				new CCol(retries, "top"),
				new CCol(Nest.value(alert,"sendto").$(), "top"),
				new CCol(message, "top pre"),
				new CCol(error, "wraptext top")
			));
		}

		return getOutput(actionTable);
	}

}
