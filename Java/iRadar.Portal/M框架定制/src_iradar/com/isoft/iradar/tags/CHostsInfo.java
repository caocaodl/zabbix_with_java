package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_FALSE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.STYLE_HORIZONTAL;
import static com.isoft.iradar.inc.Defines.STYLE_VERTICAL;
import static com.isoft.iradar.inc.HostsUtil.get_hostgroup_by_groupid;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CHostsInfo extends CTable {
	
	private static final long serialVersionUID = 1L;
	
	private Long groupid;
	private Integer style;
	
	private SQLExecutor executor;
	private IIdentityBean idBean;
	
	public CHostsInfo(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, 0L);
	}
	
	public CHostsInfo(IIdentityBean idBean, SQLExecutor executor, Long groupid) {
		this(idBean, executor, groupid, STYLE_HORIZONTAL);
	}

	public CHostsInfo(IIdentityBean idBean, SQLExecutor executor, Long groupid, Integer style) {
		super(null, "hosts_info");
		this.idBean = idBean;
		this.executor = executor;
		this.groupid = groupid;
		this.style = null; 
		setOrientation(style);
	}
	
	public void setOrientation(Integer value) {
		if (value != STYLE_HORIZONTAL && value != STYLE_VERTICAL) {
			error("Incorrect value for SetOrientation \""+value+"\".");
			return;
		}
		this.style = value;
	}

	@Override
	public StringBuilder bodyToString() {
		cleanItems();
		int total = 0;

		// fetch accessible host ids
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid"});
		hoptions.setPreserveKeys(true);
		hoptions.setEditable(true);
		CArray<Map> hosts = API.Host(this.idBean, this.executor).get(hoptions);
		CArray hostIds = array_keys(hosts);

		String cond_from = null;
		String cond_where = null;
		SqlBuilder sqlParts = new SqlBuilder();
		if (groupid != 0L) {
			cond_from = ",hosts_groups hg";
			cond_where = " AND hg.hostid=h.hostid AND hg.groupid="+sqlParts.marshalParam(groupid);
		} else {
			cond_from = "";
			cond_where = "";
		}
		String cond_hostids = sqlParts.where.dbConditionInt("h.hostid", hostIds.valuesAsLong());

		CArray<Map> db_host_cnt = DBselect(executor,
			"SELECT COUNT(DISTINCT h.hostid) AS cnt"+
			" FROM hosts h"+cond_from+
			" WHERE h.available="+HOST_AVAILABLE_TRUE+
				" AND h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")"+
				" AND "+cond_hostids+
				cond_where,
			sqlParts.getNamedParams()
		);

		Map host_cnt = DBfetch(db_host_cnt);
		int avail = Nest.value(host_cnt,"cnt").asInteger();
		total += avail;

		db_host_cnt = DBselect(executor,
			"SELECT COUNT(DISTINCT h.hostid) AS cnt"+
			" FROM hosts h"+cond_from+
			" WHERE h.available="+HOST_AVAILABLE_FALSE+
				" AND h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")"+
				" AND "+cond_hostids+
				cond_where,
			sqlParts.getNamedParams()
		);

		host_cnt = DBfetch(db_host_cnt);
		int notav = Nest.value(host_cnt,"cnt").asInteger();
		total += notav;

		db_host_cnt = DBselect(executor,
			"SELECT COUNT(DISTINCT h.hostid) AS cnt"+
			" FROM hosts h"+cond_from+
			" WHERE h.available="+HOST_AVAILABLE_UNKNOWN+
				" AND h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")"+
				" AND "+cond_hostids+
				cond_where,
			sqlParts.getNamedParams()
		);

		host_cnt = DBfetch(db_host_cnt);
		int uncn = Nest.value(host_cnt,"cnt").asInteger();
		total += uncn;

		String header_str = _("Hosts info")+_("the available of agent")+SPACE;
		if (groupid != 0) {
			Map group = get_hostgroup_by_groupid(this.idBean, executor, Nest.as(groupid).asString());
			header_str += _("Group")+SPACE+"&quot;"+Nest.value(group,"name").$()+"&quot;";
		} else {
			header_str += _("All groups");
		}

		CCol header = new CCol(header_str, "header");
		if (style == STYLE_HORIZONTAL) {
			header.setColSpan(4);
		}

		addRow(header);

		CCol availCol = new CCol(avail+"  "+_("Available"), "avail");
		CCol notavCol = new CCol(notav+"  "+_("Not available"), "notav");
		CCol uncnCol = new CCol(uncn+"  "+_("Unknown"), "uncn");
		CCol totalCol = new CCol(total+"  "+_("Total"), "total");

		if (style == STYLE_HORIZONTAL) {
			addRow(array(availCol, notavCol, uncnCol, totalCol));
		} else {
			addRow(availCol);
			addRow(notavCol);
			addRow(uncnCol);
			addRow(totalCol);
		}

		return super.bodyToString();
	}	

}
