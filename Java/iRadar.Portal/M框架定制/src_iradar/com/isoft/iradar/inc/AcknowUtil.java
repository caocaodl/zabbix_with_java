package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class AcknowUtil {

	private AcknowUtil() {
	}

	public static Map get_last_event_by_triggerid(IIdentityBean idBean, SQLExecutor executor, String triggerId) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT e.*"+
				" FROM events e"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
				    " AND e.objectid="+sqlParts.marshalParam(triggerId)+
					" AND e.source="+EVENT_SOURCE_TRIGGERS+
					" AND e.object="+EVENT_OBJECT_TRIGGER+
				" ORDER BY e.objectid DESC,e.object DESC,e.eventid DESC";
		Map params = sqlParts.getNamedParams();
		CArray<Map> dbEvents = DBselect(executor, sql, 1, params);
		return !empty(dbEvents) ? dbEvents.get(0) : null;
	}
	
	/**
	 * Get acknowledgement table.
	 *
	 * @param array event
	 * @param array event["acknowledges"]
	 * @param array event["acknowledges"]["clock"]
	 * @param array event["acknowledges"]["alias"]
	 * @param array event["acknowledges"]["message"]
	 *
	 * @return CTableInfo
	 */
	public static CTableInfo makeAckTab(Map event) {
		CTableInfo acknowledgeTable = new CTableInfo(_("No acknowledges found."));
		acknowledgeTable.setHeader(array(_("Time"), _("User name"), _("Comments")));

		Object oacknowledges = Nest.value(event,"acknowledges").$();
		if (!empty(oacknowledges) && isArray(oacknowledges)) {
			CArray<Map> acknowledges = Nest.as(oacknowledges).asCArray();
			for (Map acknowledge : acknowledges) {
				acknowledgeTable.addRow(array(
					rda_date2str(_("d M Y H:i:s"), Nest.value(acknowledge,"clock").asLong()),
					getUserFullname((Map)acknowledge),
					new CCol(rda_nl2br(CommonUtils.encode(Nest.value(acknowledge,"message").asString())), "wraptext")
				));
			}
		}
		return acknowledgeTable;
	}
	
	/**
	 * Gets user full name in format \"alias (name surname)\". If both name and surname exist, returns translated string.
	 *
	 * @param array _userData
	 *
	 * @return string
	 */
	public static String getLocalUserFullname(Map userData) {
		return Nest.value(userData, "alias").asString();
	}
}
