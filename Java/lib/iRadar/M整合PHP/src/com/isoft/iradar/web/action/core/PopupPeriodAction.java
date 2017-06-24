package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.Defines.T_RDA_CLR;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.insert_js_function;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.TranslateDefines.POPUP_PERIOD_CAPTION_DATE_FORMAT;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CColor;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVisibilityBox;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupPeriodAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Period"));
		Nest.value(page, "file").$(_("popup_period.action"));
		Nest.value(page, "scripts").$(new String[]{"class.calendar.js"});
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields=map(
			"dstfrm",			array(T_RDA_STR, O_MAND,P_SYS,	NOT_EMPTY,			null),
			"config",			array(T_RDA_INT, O_OPT,	P_SYS,	IN("0,1,2,3"),		null),
			"period_id",			array(T_RDA_INT, O_OPT,  null,	null,			null),
			"caption",				array(T_RDA_STR, O_OPT,  null,	null,			null),
			"report_timesince",	array(T_RDA_STR, O_OPT,  null,	null,		"isset({save})"),
			"report_timetill",		array(T_RDA_STR, O_OPT,  null,	null,		"isset({save})"),
			"color",				array(T_RDA_CLR, O_OPT,  null,	null,		"isset({save})"),
			/* actions */
			"save",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			/* other */
			"form",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"form_refresh",	array(T_RDA_STR, O_OPT, null,	null,	null)
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
		insert_js_function("add_period");
		insert_js_function("update_period");
		
		Nest.value(_REQUEST,"report_timesince").$(rdaDateToTime(get_request("report_timesince", date(TIMESTAMP_FORMAT_ZERO_TIME, time() - SEC_PER_DAY))));
		Nest.value(_REQUEST,"report_timetill").$(rdaDateToTime(get_request("report_timetill", date(TIMESTAMP_FORMAT_ZERO_TIME))));

		Nest.value(_REQUEST,"caption").$(get_request("caption",""));
		if(rda_empty(Nest.value(_REQUEST,"caption").$()) && isset(_REQUEST,"report_timesince") && isset(_REQUEST,"report_timetill")){
			Nest.value(_REQUEST,"caption").$(rda_date2str(POPUP_PERIOD_CAPTION_DATE_FORMAT,  Nest.value(_REQUEST,"report_timesince").asLong())+" - "+
									rda_date2str(POPUP_PERIOD_CAPTION_DATE_FORMAT, Nest.value(_REQUEST,"report_timetill").asLong()));
		}
		
		if(isset(Nest.value(_REQUEST,"save").$())){
			if(isset(Nest.value(_REQUEST,"period_id").$())){
				insert_js("update_period('"+
						Nest.value(_REQUEST,"period_id").asString()+"',"+
						rda_jsvalue(Nest.value(_REQUEST,"dstfrm").asString())+","+
						rda_jsvalue(Nest.value(_REQUEST,"caption").asString())+",'"+
						Nest.value(_REQUEST,"report_timesince").asString()+"','"+
						Nest.value(_REQUEST,"report_timetill").asString()+"','"+
						Nest.value(_REQUEST,"color").asString()+"');\n");
			} else{
				insert_js("add_period("+
						rda_jsvalue(Nest.value(_REQUEST,"dstfrm").asString())+","+
						rda_jsvalue(Nest.value(_REQUEST,"caption").asString())+",'"+
						Nest.value(_REQUEST,"report_timesince").asString()+"','"+
						Nest.value(_REQUEST,"report_timetill").asString()+"','"+
						Nest.value(_REQUEST,"color").asString()+"');\n");
			}
		} else {
			echo(SBR);

			CFormTable frmPd = new CFormTable(_("Period"));
			frmPd.setName("period");

			frmPd.addVar("dstfrm",Nest.value(_REQUEST,"dstfrm").$());

			Integer config = get_request("config", 1);

			String caption = get_request("caption", "");
			String color = get_request("color", "009900");

			Long report_timesince = get_request("report_timesince", time() - SEC_PER_DAY);
			Long report_timetill = get_request("report_timetill", time());

			frmPd.addVar("config",config);
			frmPd.addVar("report_timesince", date(TIMESTAMP_FORMAT_ZERO_TIME, report_timesince));
			frmPd.addVar("report_timetill", date(TIMESTAMP_FORMAT_ZERO_TIME, report_timetill));

			if(isset(Nest.value(_REQUEST,"period_id").$()))
				frmPd.addVar("period_id",Nest.value(_REQUEST,"period_id").$());


			frmPd.addRow(array(CVisibilityBox.instance("caption_visible", !rda_empty(caption), "caption", _("Default")),
				_("Caption")), new CTextBox("caption",caption,42));

			CTable reporttimetab = new CTable(null, "calendar");

			CArray timeSinceRow = createDateSelector("report_timesince", report_timesince, "report_timetill");
			array_unshift(timeSinceRow, _("From"));
			reporttimetab.addRow(timeSinceRow);

			CArray timeTillRow = createDateSelector("report_timetill", report_timetill, "report_timesince");
			array_unshift(timeTillRow, _("Till"));
			reporttimetab.addRow(timeTillRow);

			frmPd.addRow(_("Period"), reporttimetab);

			if(config != 1)
				frmPd.addRow(_("Colour"), new CColor("color",color));
			else
				frmPd.addVar("color",color);


			frmPd.addItemToBottomRow(new CSubmit("save", isset(Nest.value(_REQUEST,"period_id").$()) ? _("Update") : _("Add")));

			frmPd.addItemToBottomRow(new CButtonCancel(null,"close_window();"));
			frmPd.show();
		}
	}

}
