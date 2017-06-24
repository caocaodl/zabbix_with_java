package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;

import com.isoft.lang.CodeConfirmed;

@CodeConfirmed("benne.2.2.6")
public class TranslateDefines {
	
	private TranslateDefines() {
	}

	/**
	 * String that is used to substitute macro when it cannot be resolved.
	 */
	public static final String UNRESOLVED_MACRO_STRING = "*"+_("UNKNOWN")+"*";

	// date formats
	public static final String HISTORY_OF_ACTIONS_DATE_FORMAT = _("d M Y H:i:s");
	public static final String EVENT_ACTION_MESSAGES_DATE_FORMAT = _("d M Y H:i:s");
	public static final String EVENT_ACTION_CMDS_DATE_FORMAT = _("Y.M.d H:i:s");
	public static final String HISTORY_LOG_LOCALTIME_DATE_FORMAT = _("Y.M.d H:i:s");
	public static final String HISTORY_LOG_ITEM_PLAINTEXT = _("Y-m-d H:i:s");
	public static final String HISTORY_PLAINTEXT_DATE_FORMAT = _("Y-m-d H:i:s");
	public static final String HISTORY_ITEM_DATE_FORMAT = _("Y.M.d H:i:s");
	public static final String EVENTS_DISCOVERY_TIME_FORMAT = _("d M Y H:i:s");
	public static final String EVENTS_ACTION_TIME_FORMAT = _("d M Y H:i:s");
	public static final String QUEUE_DATE_FORMAT = _("d M Y H:i:s");
	public static final String CHARTBAR_HOURLY_DATE_FORMAT = _("Y.m.d H:i");
	public static final String CHARTBAR_DAILY_DATE_FORMAT = _("Y.m.d");
	// GETTEXT: Date format (year). Do not translate.
	public static final String REPORT4_ANNUALLY_DATE_FORMAT = _x("Y", "date format");
	public static final String REPORT4_MONTHLY_DATE_FORMAT = _("M Y");
	public static final String REPORT4_DAILY_DATE_FORMAT = _("d M Y");
	public static final String REPORT4_WEEKLY_DATE_FORMAT = _("d M Y H:i");
	public static final String FILTER_TIMEBAR_DATE_FORMAT = _("d M Y H:i");
	public static final String REPORTS_BAR_REPORT_DATE_FORMAT = _("d M Y H:i:s");
	public static final String POPUP_PERIOD_CAPTION_DATE_FORMAT = _("d M Y H:i:s");
	public static final String MAPS_DATE_FORMAT = _("Y.m.d H:i:s");
	public static final String SERVER_INFO_DATE_FORMAT = _("D, d M Y H:i:s O");
	public static final String XML_DATE_DATE_FORMAT = _("d.m.y");
	public static final String XML_TIME_DATE_FORMAT = _("H.i");
}
