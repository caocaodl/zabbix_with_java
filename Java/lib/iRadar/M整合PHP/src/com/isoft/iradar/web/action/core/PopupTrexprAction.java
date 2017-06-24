package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PARAM_TYPE_COUNTS;
import static com.isoft.iradar.inc.Defines.PARAM_TYPE_TIME;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasErrorMesssages;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.TriggersUtil.quoteFunctionParam;
import static com.isoft.iradar.inc.TriggersUtil.utf8RawUrlDecode;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.iradar.validators.CTriggerFunctionValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PopupTrexprAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		Nest.value(page, "title").$(_("Condition"));
		Nest.value(page, "file").$(_("popup_trexpr.action"));
		
		define("RDA_PAGE_NO_MENU", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"dstfrm" ,						array(T_RDA_STR, O_MAND, P_SYS, NOT_EMPTY,	null),
			"dstfld1" ,						array(T_RDA_STR, O_MAND, P_SYS, NOT_EMPTY,	null),
			"expression" ,				array(T_RDA_STR, O_OPT, null,	null,		null),
			"itemid" ,						array(T_RDA_INT, O_OPT, null,	null,		"isset({insert})"),
			"parent_discoveryid" ,	array(T_RDA_INT, O_OPT, null,	null,		null),
			"expr_type",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({insert})"),
			"param" ,						array(T_RDA_STR, O_OPT, null,	0,			"isset({insert})"),
			"paramtype" ,				array(T_RDA_INT, O_OPT, null,	IN(PARAM_TYPE_TIME+","+PARAM_TYPE_COUNTS), "isset({insert})"),
			"value" ,						array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({insert})"),
			// action
			"insert" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,						array(T_RDA_STR, O_OPT, P_SYS,	null,		null)
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
		CArray operators = map(
			"<" , "<",
			">" , ">",
			"=" , "=",
			"#" , "NOT"
		);
		@SuppressWarnings("unused")
		CArray limitedOperators = map(
			"=" , "=",
			"#" , "NOT"
		);
		CArray metrics = map(
			PARAM_TYPE_TIME , _("Time"),
			PARAM_TYPE_COUNTS , _("Count")
		);
		CArray param1SecCount = array(
			map(
				"C" , _("Last of")+" (T)",// caption
				"T" , T_RDA_INT, // type
				"M" , metrics // metrcis
			),
			map(
				"C" , _("Time shift")+" ", // caption
				"T" , T_RDA_INT // type
			)
		);
		CArray param1Sec = array(
			map(
				"C" , _("Last of")+" (T)", // caption
				"T" , T_RDA_INT // type
			)
		);
		CArray param1Str = array(
			map(
				"C" , "T", // caption
				"T" , T_RDA_STR
			)
		);
		CArray param2SecCount = array(
			map(
				"C" , "V", // caption
				"T" , T_RDA_STR
			),
			map(
				"C" , _("Last of")+" (T)", // caption
				"T" , T_RDA_INT, // type
				"M" , metrics // metrcis
			)
		);
		CArray param3SecVal = array(
			map(
				"C" , _("Last of")+" (T)", // caption
				"T" , T_RDA_INT, // type
				"M" , metrics // metrcis
			),
			map(
				"C" , "V", // caption
				"T" , T_RDA_STR
			),
			map(
				"C" , "O", // caption
				"T" , T_RDA_STR
			),
			map(
				"C" , _("Time shift")+" ", // caption
				"T" , T_RDA_INT // type
			)
		);
		CArray paramSecIntCount = array(
			map(
				"C" , _("Last of")+" (T)", // caption
				"T" , T_RDA_INT, // type
				"M" , metrics // metrics
			),
			map(
				"C" , _("Mask"), // caption
				"T" , T_RDA_STR
			),
			map(
				"C" , _("Time shift")+" ", // caption
				"T" , T_RDA_INT // type
			)
		);
		CArray<Integer> allowedTypesAny = map(
			ITEM_VALUE_TYPE_FLOAT , 1,
			ITEM_VALUE_TYPE_STR , 1,
			ITEM_VALUE_TYPE_LOG , 1,
			ITEM_VALUE_TYPE_UINT64 , 1,
			ITEM_VALUE_TYPE_TEXT , 1
		);
		CArray<Integer> allowedTypesNumeric = map(
			ITEM_VALUE_TYPE_FLOAT , 1,
			ITEM_VALUE_TYPE_UINT64 , 1
		);
		CArray<Integer> allowedTypesStr = map(
			ITEM_VALUE_TYPE_STR , 1,
			ITEM_VALUE_TYPE_LOG , 1,
			ITEM_VALUE_TYPE_TEXT , 1
		);
		CArray<Integer> allowedTypesLog = map(
			ITEM_VALUE_TYPE_LOG , 1
		);
		CArray<Integer> allowedTypesInt = map(
			ITEM_VALUE_TYPE_UINT64 , 1
		);

		CArray<Map> functions = map(
			"abschange[<]" , map(
				"description" ,  _("Absolute difference between last and previous value is < N"),
				"allowed_types" , allowedTypesAny
			),
			"abschange[>]" , map(
				"description" ,  _("Absolute difference between last and previous value is > N"),
				"allowed_types" , allowedTypesAny
			),
			"abschange[=]" , map(
				"description" ,  _("Absolute difference between last and previous value is = N"),
				"allowed_types" , allowedTypesAny
			),
			"abschange[#]" , map(
				"description" ,  _("Absolute difference between last and previous value is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"avg[<]" , map(
				"description" ,  _("Average value of a period T is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"avg[>]" , map(
				"description" ,  _("Average value of a period T is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"avg[=]" , map(
				"description" ,  _("Average value of a period T is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"avg[#]" , map(
				"description" ,  _("Average value of a period T is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"delta[<]" , map(
				"description" ,  _("Difference between MAX and MIN value of a period T is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"delta[>]" , map(
				"description" ,  _("Difference between MAX and MIN value of a period T is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"delta[=]" , map(
				"description" ,  _("Difference between MAX and MIN value of a period T is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"delta[#]" , map(
				"description" ,  _("Difference between MAX and MIN value of a period T is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"change[<]" , map(
				"description" ,  _("Difference between last and previous value is < N"),
				"allowed_types" , allowedTypesAny
			),
			"change[>]" , map(
				"description" ,  _("Difference between last and previous value is > N"),
				"allowed_types" , allowedTypesAny
			),
			"change[=]" , map(
				"description" ,  _("Difference between last and previous value is = N"),
				"allowed_types" , allowedTypesAny
			),
			"change[#]" , map(
				"description" ,  _("Difference between last and previous value is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"count[<]" , map(
				"description" ,  _("Number of successfully retrieved values V (which fulfill operator O) for period T is < N"),
				"params" , param3SecVal,
				"allowed_types" , allowedTypesAny
			),
			"count[>]" , map(
				"description" ,  _("Number of successfully retrieved values V (which fulfill operator O) for period T is > N"),
				"params" , param3SecVal,
				"allowed_types" , allowedTypesAny
			),
			"count[=]" , map(
				"description" ,  _("Number of successfully retrieved values V (which fulfill operator O) for period T is = N"),
				"params" , param3SecVal,
				"allowed_types" , allowedTypesAny
			),
			"count[#]" , map(
				"description" ,  _("Number of successfully retrieved values V (which fulfill operator O) for period T is NOT N"),
				"params" , param3SecVal,
				"allowed_types" , allowedTypesAny
			),
			"diff[=]" , map(
				"description" ,  _("Difference between last and preceding values, then N = 1, 0 - otherwise"),
				"allowed_types" , allowedTypesAny
			),
			"diff[#]" , map(
				"description" ,  _("Difference between last and preceding values, then N NOT 1, 0 - otherwise"),
				"allowed_types" , allowedTypesAny
			),
			"last[<]" , map(
				"description" ,  _("Last (most recent) T value is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesAny
			),
			"last[>]" , map(
				"description" ,  _("Last (most recent) T value is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesAny
			),
			"last[=]" , map(
				"description" ,  _("Last (most recent) T value is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesAny
			),
			"last[#]" , map(
				"description" ,  _("Last (most recent) T value is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesAny
			),
			"max[<]" , map(
				"description" ,  _("Maximum value for period T is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"max[>]" , map(
				"description" ,  _("Maximum value for period T is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"max[=]" , map(
				"description" ,  _("Maximum value for period T is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"max[#]" , map(
				"description" ,  _("Maximum value for period T is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"min[<]" , map(
				"description" ,  _("Minimum value for period T is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
				),
			"min[>]" , map(
				"description" ,  _("Minimum value for period T is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
				),
			"min[=]" , map(
				"description" ,  _("Minimum value for period T is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
				),
			"min[#]" , map(
				"description" ,  _("Minimum value for period T is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
				),
			"prev[<]" , map(
				"description" ,  _("Previous value is < N"),
				"allowed_types" , allowedTypesAny
			),
			"prev[>]" , map(
				"description" ,  _("Previous value is > N"),
				"allowed_types" , allowedTypesAny
			),
			"prev[=]" , map(
				"description" ,  _("Previous value is = N"),
				"allowed_types" , allowedTypesAny
			),
			"prev[#]" , map(
				"description" ,  _("Previous value is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"str[=]" , map(
				"description" ,  _("Find string V in last (most recent) value. N = 1 - if found, 0 - otherwise"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"str[#]" , map(
				"description" ,  _("Find string V in last (most recent) value. N NOT 1 - if found, 0 - otherwise"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"strlen[<]" , map(
				"description" ,  _("Length of last (most recent) T value in characters is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesStr
			),
			"strlen[>]" , map(
				"description" ,  _("Length of last (most recent) T value in characters is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesStr
			),
			"strlen[=]" , map(
				"description" ,  _("Length of last (most recent) T value in characters is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesStr
			),
			"strlen[#]" , map(
				"description" ,  _("Length of last (most recent) T value in characters is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesStr
			),
			"sum[<]" , map(
				"description" ,  _("Sum of values of a period T is < N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"sum[>]" , map(
				"description" ,  _("Sum of values of a period T is > N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"sum[=]" , map(
				"description" ,  _("Sum of values of a period T is = N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"sum[#]" , map(
				"description" ,  _("Sum of values of a period T is NOT N"),
				"params" , param1SecCount,
				"allowed_types" , allowedTypesNumeric
			),
			"date[<]" , map(
				"description" ,  _("Current date is < N"),
				"allowed_types" , allowedTypesAny
			),
			"date[>]" , map(
				"description" ,  _("Current date is > N"),
				"allowed_types" , allowedTypesAny
			),
			"date[=]" , map(
				"description" ,  _("Current date is = N"),
				"allowed_types" , allowedTypesAny
			),
			"date[#]" , map(
				"description" ,  _("Current date is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofweek[<]" , map(
				"description" ,  _("Day of week is < N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofweek[>]" , map(
				"description" ,  _("Day of week is > N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofweek[=]" , map(
				"description" ,  _("Day of week is = N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofweek[#]" , map(
				"description" ,  _("Day of week is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofmonth[<]" , map(
				"description" ,  _("Day of month is < N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofmonth[>]" , map(
				"description" ,  _("Day of month is > N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofmonth[=]" , map(
				"description" ,  _("Day of month is = N"),
				"allowed_types" , allowedTypesAny
			),
			"dayofmonth[#]" , map(
				"description" ,  _("Day of month is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"fuzzytime[=]" , map(
				"description" ,  _("Timestamp not different from iRadar server time for more than T seconds, then N = 1, 0 - otherwise"),
				"params" , param1Sec,
				"allowed_types" , allowedTypesAny
			),
			"fuzzytime[#]" , map(
				"description" ,  _("Timestamp not different from iRadar server time for more than T seconds, then N NOT 1, 0 - otherwise"),
				"params" , param1Sec,
				"allowed_types" , allowedTypesAny
			),
			"regexp[=]" , map(
				"description" ,  _("Regular expression V matching last value in period T, then N = 1, 0 - otherwise"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"regexp[#]" , map(
				"description" ,  _("Regular expression V matching last value in period T, then N NOT 1, 0 - otherwise"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"iregexp[=]" , map(
				"description" ,  _("Regular expression V matching last value in period T, then N = 1, 0 - otherwise (non case-sensitive)"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"iregexp[#]" , map(
				"description" ,  _("Regular expression V matching last value in period T, then N NOT 1, 0 - otherwise (non case-sensitive)"),
				"params" , param2SecCount,
				"allowed_types" , allowedTypesAny
			),
			"logeventid[=]" , map(
				"description" ,  _("Event ID of last log entry matching regular expression T, then N = 1, 0 - otherwise"),
				"params" , param1Str,
				"allowed_types" , allowedTypesLog
			),
			"logeventid[#]" , map(
				"description" ,  _("Event ID of last log entry matching regular expression T, then N NOT 1, 0 - otherwise"),
				"params" , param1Str,
				"allowed_types" , allowedTypesLog
			),
			"logseverity[<]" , map(
				"description" ,  _("Log severity of the last log entry is < N"),
				"allowed_types" , allowedTypesLog
			),
			"logseverity[>]" , map(
				"description" ,  _("Log severity of the last log entry is > N"),
				"allowed_types" , allowedTypesLog
			),
			"logseverity[=]" , map(
				"description" ,  _("Log severity of the last log entry is = N"),
				"allowed_types" , allowedTypesLog
			),
			"logseverity[#]" , map(
				"description" ,  _("Log severity of the last log entry is NOT N"),
				"allowed_types" , allowedTypesLog
			),
			"logsource[=]" , map(
				"description" ,  _("Log source of the last log entry matching parameter T, then N = 1, 0 - otherwise"),
				"params" , param1Str,
				"allowed_types" , allowedTypesLog
			),
			"logsource[#]" , map(
				"description" ,  _("Log source of the last log entry matching parameter T, then N NOT 1, 0 - otherwise"),
				"params" , param1Str,
				"allowed_types" , allowedTypesLog
			),
			"now[<]" , map(
				"description" ,  _("Number of seconds since the Epoch is < N"),
				"allowed_types" , allowedTypesAny
			),
			"now[>]" , map(
				"description" ,  _("Number of seconds since the Epoch is > N"),
				"allowed_types" , allowedTypesAny
			),
			"now[=]" , map(
				"description" ,  _("Number of seconds since the Epoch is = N"),
				"allowed_types" , allowedTypesAny
			),
			"now[#]" , map(
				"description" ,  _("Number of seconds since the Epoch is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"time[<]" , map(
				"description" ,  _("Current time is < N"),
				"allowed_types" , allowedTypesAny
			),
			"time[>]" , map(
				"description" ,  _("Current time is > N"),
				"allowed_types" , allowedTypesAny
			),
			"time[=]" , map(
				"description" ,  _("Current time is = N"),
				"allowed_types" , allowedTypesAny
			),
			"time[#]" , map(
				"description" ,  _("Current time is NOT N"),
				"allowed_types" , allowedTypesAny
			),
			"nodata[=]" , map(
				"description" ,  _("No data received during period of time T, then N = 1, 0 - otherwise"),
				"params" , param1Sec,
				"allowed_types" , allowedTypesAny
			),
			"nodata[#]" , map(
				"description" ,  _("No data received during period of time T, then N NOT 1, 0 - otherwise"),
				"params" , param1Sec,
				"allowed_types" , allowedTypesAny
			),
			"band[=]" , map(
				"description" ,  _("Bitwise AND of last (most recent) T value and mask is = N"),
				"params" , paramSecIntCount,
				"allowed_types" , allowedTypesInt
			),
			"band[#]" , map(
				"description" ,  _("Bitwise AND of last (most recent) T value and mask is NOT N"),
				"params" , paramSecIntCount,
				"allowed_types" , allowedTypesInt
			)
		);
		
		order_result(functions, "description");
		
		if (isset(_REQUEST,"expression") && "expr_temp".equals(Nest.value(_REQUEST,"dstfld1").asString())) {
			Nest.value(_REQUEST,"expression").$(utf8RawUrlDecode(Nest.value(_REQUEST,"expression").asString()));

			CTriggerExpression expressionData = new CTriggerExpression();

			if (expressionData.parse(Nest.value(_REQUEST,"expression").asString()) && count(expressionData.expressions) == 1) {
				Map<String, Object> exprPart = reset(expressionData.expressions);

				CArray exprSymbols = array();
				preg_match("\\}([=><#]{1})([0-9]+)$", Nest.value(_REQUEST,"expression").asString(), exprSymbols);

				if (isset(exprSymbols,1)) {
					Nest.value(_REQUEST,"expr_type").$(Nest.value(exprPart,"functionName").asString()+"["+Nest.value(exprSymbols,1).asString()+"]");
				}

				Nest.value(_REQUEST,"description").$(Nest.value(exprPart,"host").asString()+":"+Nest.value(exprPart,"item").$());
				Nest.value(_REQUEST,"param").$(Nest.value(exprPart,"functionParamList").$());

				int paramNumber = in_array(Nest.value(exprPart,"functionName").asString(), new String[]{"regexp", "iregexp", "str"}) ? 1 : 0;

				if (!empty(Nest.value(_REQUEST,"param",paramNumber).asString()) && Nest.value(_REQUEST,"param",paramNumber).asString().charAt(0) == '#') {
					Nest.value(_REQUEST,"paramtype").$(PARAM_TYPE_COUNTS);
					Nest.value(_REQUEST,"param",paramNumber).$(substr(Nest.value(_REQUEST,"param",paramNumber).asString(), 1));
				} else {
					Nest.value(_REQUEST,"paramtype").$(PARAM_TYPE_TIME);
				}

				if (isset(exprSymbols,2)) {
					Nest.value(_REQUEST,"value").$(exprSymbols.get(2));
				}

				CItemGet ioptions = new CItemGet();
				ioptions.setFilter("host", Nest.value(exprPart,"host").asString());
				ioptions.setFilter("key_", Nest.value(exprPart,"item").asString());
				ioptions.setFilter("flags");
				ioptions.setOutput(new String[]{"itemid"});
				ioptions.setWebItems(true);
				CArray<Map> myItems = API.Item(getIdentityBean(), executor).get(ioptions);
				Map myItem = reset(myItems);

				if (isset(myItem,"itemid")) {
					Nest.value(_REQUEST,"itemid").$(Nest.value(myItem,"itemid").$());
				} else {
					error(_("Unknown host item, no such item in selected host"));
				}
			}
		}
		
		String exprType = get_request("expr_type", "last[=]");

		CArray exprRes = array();
		String function = null;
		Object operator = null;
		if (preg_match("^([a-z]+)\\[(["+implode("", array_keys(operators))+"])\\]$", exprType, exprRes)>0) {
			function = Nest.value(exprRes,1).asString();
			operator = Nest.value(exprRes,2).$();

			if (!isset(functions,exprType)) {
				function = null;
			}
		}

		String dstfrm = get_request("dstfrm",	"0");
		String dstfld1 = get_request("dstfld1", "");
		Long itemId = get_request("itemid", 0L);
		Integer value = get_request("value", 0);
		Object param = _REQUEST.get("param");
		if(param == null) param = "0";
		String paramType = get_request("paramtype");

		if (!isset(function)) {
			function = "last[=]";
			exprType = function;
		}

		String itemKey = null;
		String itemHost = null;
		String description = null;
		if (!empty(itemId)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_"});
			ioptions.setItemIds(itemId);
			ioptions.setWebItems(true);
			ioptions.setSelectHosts(new String[]{"host"});
			ioptions.setFilter("flags");
			CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);

			items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);

			Map item = reset(items);
			itemKey = Nest.value(item,"key_").asString();
			Map citemHost = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			itemHost = Nest.value(citemHost,"host").asString();
			description = itemHost+NAME_DELIMITER+Nest.value(item,"name_expanded").$();
		} else {
			itemKey = "";
			itemHost = "";
			description = "";
		}

		if (is_null(paramType) && isset(Nest.value(functions,exprType,"params","M").$())) {
			paramType = (Nest.value(functions,exprType,"params","M").$() instanceof Map) ? reset((CArray<String>)Nest.value(functions,exprType,"params","M").asCArray()) : Nest.value(functions,exprType,"params","M").asString();
		} else if (is_null(paramType)) {
			paramType = Nest.as(PARAM_TYPE_TIME).asString();
		}

		if (!isArray(param)) {
			if (isset(Nest.value(functions,exprType,"params").$())) {
				param = explode(",", Nest.as(param).asString(), count(Nest.value(functions,exprType,"params").asCArray()));
			} else {
				param = array(param);
			}
		}

		/* Display */
		CArray data = map(
			"parent_discoveryid" , get_request("parent_discoveryid", null),
			"dstfrm" , dstfrm,
			"dstfld1" , dstfld1,
			"itemid" , itemId,
			"value" , value,
			"param" , param,
			"paramtype" , paramType,
			"description" , description,
			"functions" , functions,
			"function" , function,
			"operator" , operator,
			"item_host" , itemHost,
			"item_key" , itemKey,
			"itemValueType" , null,
			"expr_type" , exprType,
			"insert" , get_request("insert", null),
			"cancel" , get_request("cancel", null)
		);

		// if user has already selected an item
		if (!empty(itemId)) {
			// get item value type
			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(itemId);
			ioptions.setOutput(new String[]{"value_type"});
			ioptions.setFilter("flags");
			ioptions.setWebItems(true);
			CArray<Map> selectedItems = API.Item(getIdentityBean(), executor).get(ioptions);

			Map selectedItem = reset(selectedItems);
			if (!empty(selectedItem)) {
				Nest.value(data,"itemValueType").$(Nest.value(selectedItem,"value_type").$());
			}
		}

		String submittedFunction = Nest.value(data,"function").asString()+"["+Nest.value(data,"operator").asString()+"]";
		Nest.value(data,"selectedFunction").$(null);

		// check if submitted function is usable with selected item
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"functions").asCArray()).entrySet()) {
		    String id = Nest.as(e.getKey()).asString();
		    Map f = e.getValue();
			if ((empty(Nest.value(data,"itemValueType").$()) || isset(Nest.value(f,"allowed_types",data.get("itemValueType")).$())) && id.equals(submittedFunction)) {
				Nest.value(data,"selectedFunction").$(id);
				break;
			}
		}

		if (Nest.value(data,"selectedFunction").$() == null) {
			error(_s("Function \"%1$s\" cannot be used with selected item \"%2$s\"",
					Nest.value(data,"functions",submittedFunction,"description").$(),
					Nest.value(data,"description").$()
			));
		}

		// remove functions that not correspond to chosen item
		for (Entry<Object, Map> e : Clone.deepcopy(functions).entrySet()) {
		    String id = Nest.as(e.getKey()).asString();
		    Map f = e.getValue();
			if (!empty(Nest.value(data,"itemValueType").$()) && !isset(Nest.value(f,"allowed_types",data.get("itemValueType")).$())) {
				unset(functions,id);
			}
		}

		// create and validate trigger expression
		if (isset(Nest.value(data,"insert").$())) {
			try {
				if (!empty(Nest.value(data,"description").$())) {
					if (Nest.value(data,"paramtype").asInteger() == PARAM_TYPE_COUNTS) {
						int paramNumber = in_array(Nest.value(data,"function").asString(), new String[]{"regexp", "iregexp", "str"}) ? 1 : 0;
						Nest.value(data,"param").asCArray().put(paramNumber, "#"+Nest.value(data,"param",paramNumber).asInteger());
					}

					if (Nest.value(data,"paramtype").asInteger() == PARAM_TYPE_TIME && in_array(Nest.value(data,"function").asString(), new String[]{"last", "band", "strlen"})) {
						Nest.value(data,"param").asCArray().put(0, "");
					}

					// quote function param
					CArray params = array();
					for(String cparam : (CArray<String>)Nest.value(data,"param").asCArray()) {
						params.add(quoteFunctionParam(cparam));
					}

					Nest.value(data,"expression").$(sprintf("{%s:%s.%s(%s)}%s%s",
						Nest.value(data,"item_host").$(),
						Nest.value(data,"item_key").$(),
						Nest.value(data,"function").$(),
						rtrim(implode(",", params), ","),
						Nest.value(data,"operator").$(),
						Nest.value(data,"value").$()
					));

					// validate trigger expression
					CTriggerExpression triggerExpression = new CTriggerExpression();

					if (triggerExpression.parse(Nest.value(data,"expression").asString())) {
						Map<String, Object> expressionData = reset(triggerExpression.expressions);

						// validate trigger function
						CTriggerFunctionValidator triggerFunctionValidator = CValidator.init(new CTriggerFunctionValidator(),map());
						boolean isValid = triggerFunctionValidator.validate(getIdentityBean(), map(
							"function" , Nest.value(expressionData,"function").$(),
							"functionName" , Nest.value(expressionData,"functionName").$(),
							"functionParamList" , Nest.value(expressionData,"functionParamList").$(),
							"valueType" , Nest.value(data,"itemValueType").$()
						));
						if (!isValid) {
							unset(data,"insert");
							throw new Exception(triggerFunctionValidator.getError());
						}
					} else {
						unset(data,"insert");
						throw new Exception(triggerExpression.error);
					}

					// quote function param
					if (isset(Nest.value(data,"insert").$())) {
						for (Entry<Object, String> e : ((CArray<String>)Nest.value(data,"param").asCArray()).entrySet()) {
						    Object pnum = e.getKey();
						    String cparam = e.getValue();
							Nest.value(data,"param").asCArray().put(pnum,quoteFunctionParam(cparam));
						}
					}
				} else {
					unset(data,"insert");
					throw new Exception(_("Item not selected"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				error(e.getMessage());
				show_error_message(_("Cannot insert trigger expression"));
			}
		} else if (hasErrorMesssages()) {
			show_messages();
		}

		// render view
		CView expressionView = new CView("configuration.triggers.expression", data);
		expressionView.render(getIdentityBean(), executor);
		expressionView.show();
	}

}
