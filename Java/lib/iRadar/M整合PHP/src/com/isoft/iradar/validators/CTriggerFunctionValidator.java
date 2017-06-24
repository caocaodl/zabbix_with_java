package com.isoft.iradar.validators;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.RDA_TIME_SUFFIXES;
import static com.isoft.iradar.inc.ItemsUtil.itemValueTypeString;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTriggerFunctionValidator extends CValidator<CArray> {
	
	/**
	 * The array containing valid functions and parameters to them.
	 *
	 * Structure: array(
	 *   "<function>" , array(
	 *     "args" , array(
	 *       array("type" , "<parameter_type>"[, "mandat" , bool]),
	 *       ...
	 *     ),
	 *     "value_types" , array(<value_type>, <value_type>, ...)
	 *   )
	 * )
	 *
	 * <parameter_type> can be "sec", "sec_num" or "str"
	 * <value_type> can be one of ITEM_VALUE_TYPE_*
	 *
	 * @var array
	 */
	private CArray<CArray<Object>> allowed;
	
	@Override
	protected void initValidator(Map options) {
		super.initValidator(options);
		init();
	}

	private void init() {
		CArray<Boolean> valueTypesAll = map(
				ITEM_VALUE_TYPE_FLOAT , true,
				ITEM_VALUE_TYPE_UINT64 , true,
				ITEM_VALUE_TYPE_STR , true,
				ITEM_VALUE_TYPE_TEXT , true,
				ITEM_VALUE_TYPE_LOG , true
			);
		CArray<Boolean> valueTypesNum = map(
			ITEM_VALUE_TYPE_FLOAT , true,
			ITEM_VALUE_TYPE_UINT64 , true
		);
		CArray<Boolean> valueTypesChar = map(
			ITEM_VALUE_TYPE_STR , true,
			ITEM_VALUE_TYPE_TEXT , true,
			ITEM_VALUE_TYPE_LOG , true
		);
		CArray<Boolean> valueTypesLog = map(
			ITEM_VALUE_TYPE_LOG , true
		);
		CArray<Boolean> valueTypesInt = map(
			ITEM_VALUE_TYPE_UINT64 , true
		);

		CArray<CArray<Object>> argsIgnored = array(map("type" , "str"));

		this.allowed = map(
			"abschange" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"avg" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesNum
			),
			"band" , map(
				"args" , array(
					map("type" , "sec_num_zero", "mandat" , true, "can_be_empty" , true),
					map("type" , "num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesInt
			),
			"change" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"count" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "str"),
					map("type" , "operation"),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesAll
			),
			"date" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"dayofmonth" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"dayofweek" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"delta" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesNum
			),
			"diff" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"fuzzytime" , map(
				"args" , array(
					map("type" , "sec_zero", "mandat" , true)
				),
				"value_types" , valueTypesNum
			),
			"iregexp" , map(
				"args" , array(
					map("type" , "str", "mandat" , true),
					map("type" , "sec_num", "can_be_empty" , true)
				),
				"value_types" , valueTypesChar
			),
			"last" , map(
				"args" , array(
					map("type" , "sec_num_zero", "mandat" , true, "can_be_empty" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesAll
			),
			"logeventid" , map(
				"args" , array(
					map("type" , "str", "mandat" , true)
				),
				"value_types" , valueTypesLog
			),
			"logseverity" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesLog
			),
			"logsource" , map(
				"args" , array(
					map("type" , "str", "mandat" , true)
				),
				"value_types" , valueTypesLog
			),
			"max" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesNum
			),
			"min" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesNum
			),
			"nodata", map(
				"args" , array(
					map("type" , "sec_zero", "mandat" , true)
				),
				"value_types" , valueTypesAll
			),
			"now" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"prev" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			),
			"regexp" , map(
				"args" , array(
					map("type" , "str", "mandat" , true),
					map("type" , "sec_num", "can_be_empty" , true)
				),
				"value_types" , valueTypesChar
			),
			"str" , map(
				"args" , array(
					map("type" , "str", "mandat" , true),
					map("type" , "sec_num", "can_be_empty" , true)
				),
				"value_types" , valueTypesChar
			),
			"strlen" , map(
				"args" , array(
					map("type" , "sec_num_zero", "mandat" , true, "can_be_empty" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesChar
			),
			"sum" , map(
				"args" , array(
					map("type" , "sec_num", "mandat" , true),
					map("type" , "sec_zero", "can_be_empty" , true)
				),
				"value_types" , valueTypesNum
			),
			"time" , map(
				"args" , argsIgnored,
				"value_types" , valueTypesAll
			)
		);
	}

	/**
	 * Validate trigger function like last(0), time(), etc.
	 * Examples:
	 *	array(
	 *		"function" => last(\"#15\"),
	 *		"functionName" => "last",
	 *		"functionParamList" => array(0 => "#15"),
	 *		"valueType" => 3
	 *	)
	 *
	 * @param string value["function"]
	 * @param string value["functionName"]
	 * @param array  value["functionParamList"]
	 * @param int      value["valueType"]
	 *
	 * @return bool
	 */
	@Override
	public boolean validate(IIdentityBean idBean, CArray value) {
		setError("");

		if (!isset(allowed,value.get("functionName"))) {
			setError(_s("Incorrect trigger function \"%1$s\" provided in expression.", Nest.value(value,"function").$())+" "+
				_("Unknown function."));
			return false;
		}

		if (!isset(Nest.value(allowed,value.get("functionName"),"value_types",value.get("valueType")).$())) {
			setError(_s("Incorrect item value type \"%1$s\" provided for trigger function \"%2$s\".",
				itemValueTypeString(Nest.value(value,"valueType").asInteger()), Nest.value(value,"function").$()));
			return false;
		}

		if (count(Nest.value(allowed,value.get("functionName"),"args").$()) < count(Nest.value(value,"functionParamList").$())) {
			setError(_s("Incorrect trigger function \"%1$s\" provided in expression.", Nest.value(value,"function").$())+" "+
				_("Invalid number of parameters."));
			return false;
		}

		CArray paramLabels = array(
			_("Invalid first parameter."),
			_("Invalid second parameter."),
			_("Invalid third parameter."),
			_("Invalid fourth parameter.")
		);

		CArray<Map> args = Nest.value(allowed,value.get("functionName"),"args").asCArray();
		for (Entry<Object, Map> e : args.entrySet()) {
		    Object aNum = e.getKey();
		    Map arg = e.getValue();
			// mandatory check
			if (isset(arg,"mandat") && Nest.value(arg,"mandat").asBoolean() && !isset(Nest.value(value,"functionParamList",aNum).$())) {
				setError(_s("Incorrect trigger function \"%1$s\" provided in expression.", Nest.value(value,"function").$())+" "+
					_("Mandatory parameter is missing."));
				return false;
			}

			if (!isset(Nest.value(value,"functionParamList",aNum).$())) {
				continue;
			}

			if (isset(arg,"can_be_empty") && "".equals(Nest.value(value,"functionParamList",aNum).$())) {
				continue;
			}

			// user macro
			if (preg_match("^"+RDA_PREG_EXPRESSION_USER_MACROS+"$", Nest.value(value,"functionParamList",aNum).asString())>0) {
				continue;
			}

			if (!validateParameter(Nest.value(value,"functionParamList",aNum).asString(), Nest.value(arg,"type").asString())) {
				setError(_s("Incorrect trigger function \"%1$s\" provided in expression.",
					Nest.value(value,"function").$())+" "+paramLabels.get(aNum));
				return false;
			}
		}

		return true;
	}

	/**
	 * Validate trigger function parameter.
	 *
	 * @param string param
	 * @param string type  a type of the parameter ("sec_zero", "sec_num", "sec_num_zero", "num", "operation")
	 *
	 * @return bool
	 */
	private boolean validateParameter(String param, String type) {
		if ("sec_zero".equals(type)){
			return validateSecZero(param);
		}
		if ("sec_num".equals(type)){
			return validateSecNum(param);
		}
		if ("sec_num_zero".equals(type)){
			return validateSecNumZero(param);
		}
		if ("num".equals(type)){
			return is_numeric(param);
		}
		if ("operation".equals(type)){
			return validateOperation(param);
		}
		return true;
	}

	/**
	 * Validate trigger function parameter seconds value.
	 *
	 * @param string param
	 *
	 * @return bool
	 */
	private boolean validateSecValue(String param) {
		return preg_match("^\\d+["+RDA_TIME_SUFFIXES+"]{0,1}$", param)>0;
	}

	/**
	 * Validate trigger function parameter which can contain only seconds or zero.
	 * Examples: 0, 1, 5w
	 *
	 * @param string param
	 *
	 * @return bool
	 */
	private boolean validateSecZero(String param) {
		return validateSecValue(param);
	}

	/**
	 * Validate trigger function parameter which can contain seconds greater zero or count.
	 * Examples: 1, 5w, #1
	 *
	 * @param string param
	 *
	 * @return bool
	 */
	private boolean validateSecNum(String param) {
		if (preg_match("^#\\d+$", param)>0) {
			return (Nest.as(substr(param, 1)).asLong() > 0L);
		}
		return (validateSecValue(param) && Nest.as(trim(param, RDA_TIME_SUFFIXES.toCharArray())).asLong() > 0L);
	}

	/**
	 * Validate trigger function parameter which can contain seconds or count.
	 * Examples: 0, 1, 5w, #1
	 *
	 * @param string param
	 *
	 * @return bool
	 */
	private boolean validateSecNumZero(String param) {
		if (preg_match("^#\\d+$", param)>0) {
			return (Nest.as(substr(param, 1)).asLong() > 0L);
		}
		return validateSecValue(param);
	}

	/**
	 * Validate trigger function parameter which can contain operation (band, eq, ge, gt, le, like, lt, ne) or
	 * an empty value.
	 *
	 * @param string param
	 *
	 * @return bool
	 */
	private boolean validateOperation(String param) {
		return preg_match("^(eq|ne|gt|ge|lt|le|like|band|)$", param)>0;
	}
}
