package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_HORIZONTAL;
import static com.isoft.iradar.inc.Defines.STYLE_VERTICAL;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.HostsUtil.get_hostgroup_by_groupid;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CTriggersInfo extends CTable {

	private static final long serialVersionUID = 1L;
	
	public  Integer  style;
	public  boolean  show_header;
	private Long  groupid;
	private Long  hostid;
	private SQLExecutor executor;
	private IIdentityBean idBean;

	public CTriggersInfo(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, null);
	}
	
	public CTriggersInfo(IIdentityBean idBean, SQLExecutor executor, Long groupid) {
		this(idBean, executor, groupid, null);
	}
	
	public CTriggersInfo(IIdentityBean idBean, SQLExecutor executor, Long groupid, Long hostid) {
		this(idBean, executor, groupid, hostid, STYLE_HORIZONTAL);
	}
	
	public CTriggersInfo(IIdentityBean idBean, SQLExecutor executor, Long groupid, Long hostid, Integer style) {
		super(null, "triggers_info");
		this.idBean = idBean;
		this.executor = executor;
		this.style = null;
		setOrientation(style);
		this.show_header = true;
		this.groupid = is_null(groupid) ? 0 : groupid;
		this.hostid = is_null(hostid) ? 0 : hostid;		
	}

	public void setOrientation(Integer value) {
		if (value != STYLE_HORIZONTAL && value != STYLE_VERTICAL) {
			error("Incorrect value for SetOrientation ["+value+"]");
			return;
		}
		this.style = value;
	}

	public void hideHeader() {
		this.show_header = false;
	}

	@Override
	public StringBuilder bodyToString() {
		cleanItems();

		int ok = 0, uncl = 0, info = 0, warn = 0, avg = 0, high = 0, dis = 0;

		CTriggerGet toptions = new CTriggerGet();
		toptions.setMonitored(true);
		toptions.setSkipDependent(true);
		toptions.setOutput(new String[]{"triggerid"});

		if (this.hostid > 0L) {
			toptions.setHostIds(this.hostid);
		} else if (this.groupid > 0L) {
			toptions.setGroupIds(this.groupid);
		}
		CArray triggers = API.Trigger(this.idBean, this.executor).get(toptions);
		triggers = rda_objectValues(triggers, "triggerid");

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_priority = DBselect(executor,
			"SELECT t.priority,t.value,count(DISTINCT t.triggerid) AS cnt"+
			" FROM triggers t"+
			" WHERE "+sqlParts.dual.dbConditionInt("t.triggerid", triggers.valuesAsLong())+
			" GROUP BY t.priority,t.value",
			sqlParts.getNamedParams()
		);
		for (Map row: db_priority) {
			switch (Nest.value(row,"value").asInteger()) {
				case TRIGGER_VALUE_TRUE:
					switch (Nest.value(row,"priority").asInteger()) {
						case TRIGGER_SEVERITY_NOT_CLASSIFIED:
							uncl += Nest.value(row,"cnt").asInteger();
							break;
						case TRIGGER_SEVERITY_INFORMATION:
							info += Nest.value(row,"cnt").asInteger();
							break;
						case TRIGGER_SEVERITY_WARNING:
							warn += Nest.value(row,"cnt").asInteger();
							break;
						case TRIGGER_SEVERITY_AVERAGE:
							avg += Nest.value(row,"cnt").asInteger();
							break;
						case TRIGGER_SEVERITY_HIGH:
							high += Nest.value(row,"cnt").asInteger();
							break;
						case TRIGGER_SEVERITY_DISASTER:
							dis += Nest.value(row,"cnt").asInteger();
							break;
					}
					break;
				case TRIGGER_VALUE_FALSE:
					ok += Nest.value(row,"cnt").asInteger();
					break;
			}
		}

		if (show_header) {
			String header_str = _("Triggers info")+SPACE;

			if (groupid != 0) {
				Map group = get_hostgroup_by_groupid(this.idBean, executor, asString(groupid));
				header_str += _("Group")+SPACE+"&quot;"+group.get("name")+"&quot;";
			} else {
				header_str += _("All groups");
			}

			CCol header = new CCol(header_str, "header");
			if (style == STYLE_HORIZONTAL) {
				header.setColSpan(8);
			}
			addRow(header);
		}

		CCol trok = getSeverityCell(idBean, executor, null, ok+SPACE+_("Ok"), true);
		CCol unclCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_NOT_CLASSIFIED, uncl+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_NOT_CLASSIFIED), empty(uncl));
		CCol infoCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_INFORMATION, info+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_INFORMATION), empty(info));
		CCol warnCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_WARNING, warn+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_WARNING), empty(warn));
		CCol avgCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_AVERAGE, avg+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_AVERAGE), empty(avg));
		CCol highCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_HIGH, high+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_HIGH), empty(high));
		CCol disCol = getSeverityCell(idBean, executor, TRIGGER_SEVERITY_DISASTER, dis+SPACE+getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_DISASTER), empty(dis));

		if (STYLE_HORIZONTAL == style) {
			addRow(array(trok, unclCol, infoCol, warnCol, avgCol, highCol, disCol));
		} else {
			addRow(trok);
			addRow(unclCol);
			addRow(infoCol);
			addRow(warnCol);
			addRow(avgCol);
			addRow(highCol);
			addRow(disCol);
		}

		return super.bodyToString();
	}
}
