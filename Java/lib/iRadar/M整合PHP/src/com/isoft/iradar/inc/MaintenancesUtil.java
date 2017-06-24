package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.FuncsUtil.getDayOfWeekCaption;
import static com.isoft.iradar.inc.FuncsUtil.getMonthCaption;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_num2bitstr;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class MaintenancesUtil {
	
	private MaintenancesUtil() {
	}
	
	public static Map get_maintenance_by_maintenanceid(IIdentityBean idBean, SQLExecutor executor, String maintenanceid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor,
				"SELECT m.*"+
				" FROM maintenances m"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "maintenances", "m")+
				    " AND m.maintenanceid="+sqlParts.marshalParam(maintenanceid),
				sqlParts.getNamedParams()
		));
	}

	public static String timeperiod_type2str(int timeperiod_type) {
		switch (timeperiod_type) {
			case TIMEPERIOD_TYPE_ONETIME:
				return _("One time only");
			case TIMEPERIOD_TYPE_DAILY:
				return _("Daily");
			case TIMEPERIOD_TYPE_WEEKLY:
				return _("Weekly");
			case TIMEPERIOD_TYPE_MONTHLY:
				return _("Monthly");
		}
		return _("Unknown");
	}

	public static String shedule2str(Map timeperiod) {
		Nest.value(timeperiod,"hour").$(floor(Nest.value(timeperiod,"start_time").asLong() / SEC_PER_HOUR));
		Nest.value(timeperiod,"minute").$(floor((Nest.value(timeperiod,"start_time").asLong() - (Nest.value(timeperiod,"hour").asLong() * SEC_PER_HOUR)) / SEC_PER_MIN));
		if (Nest.value(timeperiod,"hour").asLong() < 10) {
			Nest.value(timeperiod,"hour").$("0"+Nest.value(timeperiod,"hour").$());
		}
		if (Nest.value(timeperiod,"minute").asLong() < 10) {
			Nest.value(timeperiod,"minute").$("0"+Nest.value(timeperiod,"minute").$());
		}

		String str = null;
		if (Nest.value(timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_ONETIME) {
			str = _s("At %1$s:%2$s on %3$s",
				date("H", Nest.value(timeperiod,"start_date").asLong()),
				date("i", Nest.value(timeperiod,"start_date").asLong()),
				rda_date2str(_("d M Y"), Nest.value(timeperiod,"start_date").asLong())
			);
		} else if (Nest.value(timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_DAILY) {
			str = _n("At %1$s:%2$s on every day",
				"At %1$s:%2$s on every %3$s days",
				Nest.value(timeperiod,"hour").$(),
				Nest.value(timeperiod,"minute").$(),
				Nest.value(timeperiod,"every").$()
			);
		} else if (Nest.value(timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_WEEKLY) {
			StringBuilder days = new StringBuilder();
			byte[] dayofweek = rda_num2bitstr(Nest.value(timeperiod,"dayofweek").asLong(), true).getBytes();
			int length = dayofweek.length;
			for (int i = 0; i < length; i++) {
				if (dayofweek[i] == '1') {
					if (days.length()>0) {
						days.append(", ");
					}
					days.append(getDayOfWeekCaption(i + 1));
				}
			}

			str = _n("At %1$s:%2$s on every %3$s of every week",
				"At %1$s:%2$s on every %3$s of every %4$s weeks",
				Nest.value(timeperiod,"hour").$(),
				Nest.value(timeperiod,"minute").$(),
				days.toString(),
				Nest.value(timeperiod,"every").$()
			);
		} else if (Nest.value(timeperiod,"timeperiod_type").asInteger() == TIMEPERIOD_TYPE_MONTHLY) {
			StringBuilder months = new StringBuilder();
			byte[] month = rda_num2bitstr(Nest.value(timeperiod,"month").asLong(), true).getBytes();
			int length = month.length;
			for (int i = 0; i < length; i++) {
				if (month[i] == '1') {
					if (months.length()>0) {
						months.append(", ");
					}
					months.append(getMonthCaption(i + 1));
				}
			}
			if (Nest.value(timeperiod,"dayofweek").asLong() > 0) {
				StringBuilder days = new StringBuilder();
				byte[] dayofweek = rda_num2bitstr(Nest.value(timeperiod,"dayofweek").asLong(), true).getBytes();
				length = dayofweek.length;
				for (int i = 0; i < length; i++) {
					if (dayofweek[i] == '1') {
						if (days.length()>0) {
							days.append(", ");
						}
						days.append(getDayOfWeekCaption(i + 1));
					}
				}

				String every = "";
				switch (Nest.value(timeperiod,"every").asInteger()) {
					case 1: every = _("First"); break;
					case 2: every = _("Second"); break;
					case 3: every = _("Third"); break;
					case 4: every = _("Fourth"); break;
					case 5: every = _("Last"); break;
				}

				str = _s("At %1$s:%2$s on %3$s %4$s of every %5$s",
					Nest.value(timeperiod,"hour").$(),
					Nest.value(timeperiod,"minute").$(),
					every,
					days,
					months
				);
			} else {
				str = _s("At %1$s:%2$s on day %3$s of every %4$s",
					Nest.value(timeperiod,"hour").$(),
					Nest.value(timeperiod,"minute").$(),
					Nest.value(timeperiod,"day").$(),
					months
				);
			}
		}
		return str;
	}
}
