package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CServiceGet;
import com.isoft.iradar.model.params.CSlaGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Report3Action extends RadarBaseAction {
	
	private int YEAR_LEFT_SHIFT;
	
	@Override
	protected void doInitPage() {
		page("title", _("IT services availability report"));
		page("file", "report3.action");
		page("hist_arg", new String[] {});
		
		YEAR_LEFT_SHIFT = 5;
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"serviceid",	map(T_RDA_INT, O_MAND,	P_SYS,	DB_ID,										null),
			"period",		map(T_RDA_STR, O_OPT,		null,	IN("\"daily\",\"weekly\",\"monthly\",\"yearly\""),	null),
			"year",			map(T_RDA_INT, O_OPT,		null,	null,										null)
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
	public void doAction(SQLExecutor executor) {
		String period = get_request("period", "weekly");
		final String year = get_request("year", date("Y"));

		CServiceGet soptions = new CServiceGet();
		soptions.setOutput(new String[]{"serviceid", "name", "showsla", "goodsla"});
		soptions.setServiceIds(Nest.value(_REQUEST,"serviceid").asLong());
		CArray<Map> services = API.Service(getIdentityBean(), executor).get(soptions);
		Map service = reset(services);
		if (empty(service)) {
			access_deny();
		}

		CForm form = new CForm();
		form.setMethod("get");
		form.addVar("serviceid", Nest.value(_REQUEST,"serviceid").$());

		CComboBox cmbPeriod = new CComboBox("period", period, "submit();");
		cmbPeriod.addItem("daily", _("Daily"));
		cmbPeriod.addItem("weekly", _("Weekly"));
		cmbPeriod.addItem("monthly", _("Monthly"));
		cmbPeriod.addItem("yearly", _("Yearly"));
		form.addItem(array(SPACE+_("Period")+SPACE, cmbPeriod));

		if (!"yearly".equals(period)) {
			CComboBox cmbYear = new CComboBox("year", year, "submit();");

			for (int y = (Nest.as(date("Y")).asInteger() - YEAR_LEFT_SHIFT); y <= Nest.as(date("Y")).asInteger(); y++) {
				cmbYear.addItem(y, Nest.as(y).asString());
			}
			form.addItem(array(SPACE+_("Year")+SPACE, cmbYear));
		}

		show_table_header(array(
			_("IT SERVICES AVAILABILITY REPORT"),
			SPACE+"\"",
			new CLink(Nest.value(service,"name").$(), "srv_status.action?showgraph=1&serviceid="+Nest.value(service,"serviceid").$()),
			"\""
			), form
		);

		CTableInfo table = new CTableInfo();

		CArray<String> header = array(_("Ok"), _("Problems"), _("Downtime"), _("SLA"), _("Acceptable SLA"));
		
		int from;
		int to;
		Funcer funcer = null;
		if("yearly".equals(period)){
			from = Nest.as(date("Y")).asInteger() - YEAR_LEFT_SHIFT;
			to = Nest.as(date("Y")).asInteger();
			array_unshift(header, new CCol(_("Year"), "center"));
			
			funcer = new Funcer() {
				@Override
				public long get_time(int y) {
					return mktime(0, 0, 0, 1, 1, y);
				}

				@Override
				public String format_time(int t) {
					return rda_date2str(_("Y"), (long)t);
				}

				@Override
				public String format_time2(int t) {
					return null;
				}
			};
		} else if("monthly".equals(period)){
			from = 1;
			to = 12;
			array_unshift(header, new CCol(_("Month"), "center"));
			
			funcer = new Funcer() {
				@Override
				public long get_time(int m) {
					return mktime(0, 0, 0, m, 1, Nest.as(year).asInteger());
				}

				@Override
				public String format_time(int t) {
					return rda_date2str(_("M Y"), (long)t);
				}

				@Override
				public String format_time2(int t) {
					return null;
				}
			};
		} else if("daily".equals(period)){
			from = 1;
			to = DAY_IN_YEAR;
			array_unshift(header, new CCol(_("Day"), "center"));
			
			funcer = new Funcer() {
				@Override
				public long get_time(int d) {
					return mktime(0, 0, 0, 1, d, Nest.as(year).asInteger());
				}

				@Override
				public String format_time(int t) {
					return rda_date2str(_("d M Y"), (long)t);
				}

				@Override
				public String format_time2(int t) {
					return null;
				}
			};
		} else {
			from = 0;
			to = 52;
			array_unshift(header, new CCol(_("From"), "center"), new CCol(_("Till"), "center"));
			
			funcer = new Funcer() {
				private Long beg = null;
				@Override
				public long get_time(int w) {
					if (!isset(beg)) {
						long time = mktime(0,0,0,1, 1, Nest.as(year).asInteger());
						long wd = Nest.as(date("w", time)).asLong();
						wd = wd == 0 ? 6 : wd - 1;
						beg =  time - wd * SEC_PER_DAY;
					}
					return strtotime("+" + w + " week", beg);
				}

				@Override
				public String format_time(int t) {
					return rda_date2str(_("d M Y H:i"), (long)t);
				}

				@Override
				public String format_time2(int t) {
					return format_time(t);
				}
			};
		}

		table.setHeader(header);

		CArray<Map> intervals = array();
		long start, end;
		for (int t = from; t <= to; t++) {
			start = funcer.get_time(t);
			if (start > time()) {
				break;
			}

			end = funcer.get_time(t + 1);
			if (end > time()) {
				end = time();
			}

			intervals.add(map(
				"from", start,
				"to", end
			));
		}

		CSlaGet slaoptions = new CSlaGet();
		slaoptions.setServiceIds(Nest.value(service,"serviceid").asLong());
		slaoptions.setIntervals(intervals);
		CArray<Map> slas = API.Service(getIdentityBean(), executor).getSla(slaoptions);
		Map sla = reset(slas);

		CSpan ok = null, problems = null, percentage = null;
		String downtime = null;
		for(Map intervalSla : (CArray<Map>)Nest.value(sla,"sla").asCArray()) {
			ok = new CSpan(
				sprintf("%dd %dh %dm",
					Nest.value(intervalSla,"okTime").asLong() / SEC_PER_DAY,
					(Nest.value(intervalSla,"okTime").asLong() % SEC_PER_DAY) / SEC_PER_HOUR,
					(Nest.value(intervalSla,"okTime").asLong() % SEC_PER_HOUR) / SEC_PER_MIN
				), "off"
			);

			problems = new CSpan(
				sprintf("%dd %dh %dm",
					Nest.value(intervalSla,"problemTime").asLong() / SEC_PER_DAY,
					(Nest.value(intervalSla,"problemTime").asLong() % SEC_PER_DAY) / SEC_PER_HOUR,
					(Nest.value(intervalSla,"problemTime").asLong() % SEC_PER_HOUR) /SEC_PER_MIN
				), "on"
			);

			downtime = sprintf("%dd %dh %dm",
				Nest.value(intervalSla,"downtimeTime").asLong() / SEC_PER_DAY,
				(Nest.value(intervalSla,"downtimeTime").asLong() % SEC_PER_DAY) / SEC_PER_HOUR,
				(Nest.value(intervalSla,"downtimeTime").asLong() % SEC_PER_HOUR) / SEC_PER_MIN
			);

			percentage = new CSpan(sprintf("%2.4f", Nest.value(intervalSla,"sla").asFloat()), (Nest.value(intervalSla,"sla").asFloat() >= Nest.value(service,"goodsla").asFloat() ? "off" : "on"));

			table.addRow(array(
				funcer.format_time(Nest.value(intervalSla,"from").asInteger()),
				funcer.format_time2(Nest.value(intervalSla,"to").asInteger()),
				ok,
				problems,
				downtime,
				(Nest.value(service,"showsla").asBoolean()) ? percentage : "-",
				(Nest.value(service,"showsla").asBoolean()) ? new CSpan(Nest.value(service,"goodsla").$()) : "-"
			));
		}
		table.show();
	}

	private interface Funcer {
		long get_time(int t);
		String format_time(int t);
		String format_time2(int t);
	}
}
