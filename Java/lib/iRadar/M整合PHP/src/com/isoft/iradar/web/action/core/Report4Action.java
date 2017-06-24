package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.EventsUtil.eventSourceObjects;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.TranslateDefines.REPORT4_ANNUALLY_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.REPORT4_DAILY_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.REPORT4_MONTHLY_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.REPORT4_WEEKLY_DATE_FORMAT;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.params.CAlertGet;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Report4Action extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Notification report"));
		page("file", "report4.action");
		page("hist_arg", new String[] { "media_type", "period", "year" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"year",				array(T_RDA_INT, O_OPT, P_SYS|P_NZERO,	null,	null),
			"period",			array(T_RDA_STR, O_OPT, P_SYS|P_NZERO,	IN("\"daily\",\"weekly\",\"monthly\",\"yearly\""), null),
			"media_type",	array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("media_type"))) {
			CMediaTypeGet options = new CMediaTypeGet();
			options.setMediaTypeIds(Nest.value(_REQUEST,"media_type").asLong());
			options.setCountOutput(true);
			Object mediaTypeData = API.MediaType(getIdentityBean(), executor).get(options);
			if (empty(mediaTypeData)) {
				access_deny ();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		int year = get_request("year", Nest.as(date("Y")).asInteger());
		String period = get_request("period", "weekly");
		int media_type = get_request("media_type", 0);

		Nest.value(_REQUEST,"year").$(year);
		Nest.value(_REQUEST,"period").$(period);
		Nest.value(_REQUEST,"media_type").$(media_type);

		String currentYear = date("Y");

		// fetch media types
		CArray<String> media_types = array();
		CArray<Map> db_media_types = DBselect(executor,
			"SELECT mt.*"+
			" FROM media_type mt WHERE mt.tenantid='-'"+
			" ORDER BY mt.description"
		);
		for(Map media_type_data : db_media_types) {
			Nest.value(media_types,media_type_data.get("mediatypeid")).$(Nest.value(media_type_data,"description").$());
		}

		// if no media types were defined, we have nothing to show
		CTableInfo table;
		if (rda_empty(media_types)) {
			show_table_header(_("Notifications"));
			table = new CTableInfo(_("No notifications found."));
			table.show();
		} else {
			table = new CTableInfo();
			table.makeVerticalRotation();

			// fetch the year of the first alert
			Map firstAlert = DBfetch(DBselect(executor,"SELECT MIN(a.clock) AS clock FROM alerts a"));
			String minYear;
			if ((!empty(firstAlert)) && !empty(Nest.value(firstAlert,"clock").$())) {
				minYear = date("Y", Nest.value(firstAlert,"clock").asLong());
			} else {// if no alerts exist, use the current year
				minYear = date("Y");
			}

			CForm form = new CForm();
			form.setMethod("get");

			form.addItem(SPACE+_("Media type")+SPACE);
			CComboBox cmbMedia = new CComboBox("media_type", media_type, "submit();");
			cmbMedia.addItem(0, _("all"));

			for (Entry<Object, String> e : Clone.deepcopy(media_types).entrySet()) {
			    int media_type_id = Nest.as(e.getKey()).asInteger();
			    String media_type_description = e.getValue();
				cmbMedia.addItem(media_type_id, media_type_description);

				// we won't need other media types in the future, if only one was selected
				if (media_type > 0 && media_type != media_type_id) {
					unset(media_types,media_type_id);
				}
			}
			form.addItem(cmbMedia);

			form.addItem(SPACE+_("Period")+SPACE);
			CComboBox cmbPeriod = new CComboBox("period", period, "submit();");
			cmbPeriod.addItem("daily", _("Daily"));
			cmbPeriod.addItem("weekly", _("Weekly"));
			cmbPeriod.addItem("monthly", _("Monthly"));
			cmbPeriod.addItem("yearly", _("Yearly"));
			form.addItem(cmbPeriod);

			if (!"yearly".equals(period)) {
				form.addItem(SPACE+_("Year")+SPACE);
				CComboBox cmbYear = new CComboBox("year", year, "submit();");
				for (int y = Nest.as(minYear).asInteger(); y <= Nest.as(date("Y")).asInteger(); y++) {
					cmbYear.addItem(y, Nest.as(y).asString());
				}
				form.addItem(cmbYear);
			}

			show_table_header(_("Notifications"), form);

			CArray header = array();
			CArray<Map> db_users = DBselect(executor,
					"SELECT u.*"+
					" FROM users u"+
					" ORDER BY u.alias,u.userid"
			);
			Map<Long,String> users = new HashMap();
			for(Map user_data : db_users) {
				header.add(new CCol(Nest.value(user_data,"alias").$(), "vertical_rotation"));
				Nest.value(users,user_data.get("userid")).$(Nest.value(user_data,"alias").$());
			}

			Map<Long,Long> intervals = array();
			Long minTime = null;
			String dateFormat = null;
			if("yearly".equals(period)){
				minTime = mktime(0, 0, 0, 1, 1, Nest.as(minYear).asInteger());
				dateFormat = REPORT4_ANNUALLY_DATE_FORMAT;
				array_unshift(header, new CCol(_("Year"), "center"));
				for (int i = Nest.as(minYear).asInteger(); i <= Nest.as(date("Y")).asInteger(); i++) {
					Nest.value(intervals,mktime(0, 0, 0, 1, 1, i)).$(mktime(0, 0, 0, 1, 1, i + 1));
				}
			} else if("monthly".equals(period)){
				minTime = mktime(0, 0, 0, 1, 1, year);
				dateFormat = REPORT4_MONTHLY_DATE_FORMAT;
				array_unshift(header, new CCol(_("Month"),"center"));
				int max = (year == Nest.as(currentYear).asInteger()) ? Nest.as(date("n")).asInteger() : 12;
				for (int i = 1; i <= max; i++) {
					Nest.value(intervals,mktime(0, 0, 0, i, 1, year)).$(mktime(0, 0, 0, i + 1, 1, year));
				}
			} else if("daily".equals(period)){
				minTime = mktime(0, 0, 0, 1, 1, year);
				dateFormat = REPORT4_DAILY_DATE_FORMAT;
				array_unshift(header, new CCol(_("Day"),"center"));
				int max = (year == Nest.as(currentYear).asInteger()) ? Nest.as(date("z")).asInteger() : DAY_IN_YEAR;
				for (int i = 1; i <= max; i++) {
					Nest.value(intervals,mktime(0, 0, 0, 1, i, year)).$(mktime(0, 0, 0, 1, i + 1, year));
				}
			} else if("weekly".equals(period)){
				long time = mktime(0, 0, 0, 1, 1, year);
				int wd = Nest.as(date("w", time)).asInteger();
				wd = (wd == 0) ? 6 : wd - 1;
				minTime = time - wd * SEC_PER_DAY;

				dateFormat = REPORT4_WEEKLY_DATE_FORMAT;
				array_unshift(header, new CCol(_("From"), "center"), new CCol(_("Till"), "center"));

				int max = (year == Nest.as(currentYear).asInteger()) ? Nest.as(date("W")).asInteger() - 1 : 52;
				for (int i = 0; i <= max; i++) {
					Nest.value(intervals,strtotime("+"+i+" week", minTime)).$(strtotime("+"+(i + 1)+" week", minTime));
				}
			}

			// time till
			long maxTime = (year == Nest.as(currentYear).asInteger()) ? time() : mktime(0, 0, 0, 1, 1, year + 1);

			// fetch alerts
			CArray<Map> alerts = array();
			for(CArray<Integer> sourceObject : eventSourceObjects()) {
				CAlertGet aoptions = new CAlertGet();
				aoptions.setOutput(new String[]{"mediatypeid", "userid", "clock"});
				aoptions.setEventSource(sourceObject.get("source"));
				aoptions.setEventObject(sourceObject.get("object"));
				if(!empty(get_request("media_type"))){
					aoptions.setMediaTypeIds(get_request_asLong("media_type"));
				}
				aoptions.setTimeFrom(minTime);
				aoptions.setTimeTill(maxTime);
				alerts = array_merge(alerts, (CArray<Map>)API.Alert(getIdentityBean(), executor).get(aoptions));
			}
			// sort alerts in chronological order so we could easily iterate through them later
			CArrayHelper.sort(alerts, array("clock"));
			table.setHeader(header, "vertical_header");
			for (Entry<Long, Long> e : intervals.entrySet()) {
			    long from = e.getKey();
			    long till = e.getValue();
				// interval start
			    CArray row = array(rda_date2str(dateFormat, from));

				// interval end, displayed only for week intervals
				if ("weekly".equals(period)) {
					row.add(rda_date2str(dateFormat, min(till, time()).longValue()));
				}

				// counting alert count for each user and media type
				CArray<Map> summary = array();
				for (Entry<Long, String> ee : users.entrySet()) {
					Long userid = ee.getKey();
					//String alias = ee.getValue();
					Nest.value(summary,userid).$(map());
					Nest.value(summary,userid,"total").$(0);
					Nest.value(summary,userid,"medias").$(array());
					for (Entry<Object, String> eee : media_types.entrySet()) {
					    Object media_type_nr = eee.getKey();
					    //String mt = eee.getValue();
						Nest.value(summary,userid,"medias", media_type_nr).$(0);
					}
				}

				// loop through alerts until we reach an alert from the next interval
				for(Map alert : alerts) {
					if (Nest.value(alert,"clock").asLong() >= till) {
						break;
					}

					if (isset(summary,alert.get("userid"))) {
						Nest.value(summary,alert.get("userid"),"total").$(Nest.value(summary,alert.get("userid"),"total").asInteger()+1);
						if (isset(Nest.value(summary,alert.get("userid"),"medias",alert.get("mediatypeid")).$())) {
							Nest.value(summary,alert.get("userid"),"medias",alert.get("mediatypeid")).$(
									Nest.value(summary,alert.get("userid"),"medias",alert.get("mediatypeid")).asInteger()+1
							);
						} else {
							Nest.value(summary,alert.get("userid"),"medias",alert.get("mediatypeid")).$(1);
						}
					}
				}

				for(Map user_data : db_users) {
					Object userid = user_data.get("userid");
					Map s = summary.get(userid);
					array_push(row, array(Nest.value(s,"total").$(), (media_type == 0) ? SPACE+"("+implode("/", Nest.value(s,"medias").asCArray())+")" : ""));
				}

				table.addRow(row);
			}
			table.show();

			if (media_type == 0) {
				echo(SBR);

				CArray links = array();
				for (Entry<Object, String> e : media_types.entrySet()) {
				    Object id = e.getKey();
				    String description = e.getValue();
					links.add(new CLink(description, "media_types.action?form=edit&mediatypeid="+id));
					links.add(SPACE+"/"+SPACE);
				}
				array_pop(links);

				CDiv linksDiv = new CDiv(array(SPACE+_("all")+SPACE+"("+SPACE, links, SPACE+")"));
				linksDiv.show();
			}
		}
	}

}
