package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.eval;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.sscanf;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.RadarContext._REQUEST;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asLong;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.Defines.ACCESS_EXIT;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NO_TRIM;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_HAVE_IPV6;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PORT_NUMBER;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PORT_NUMBER;
import static com.isoft.iradar.inc.Defines.RDA_VALID_ERROR;
import static com.isoft.iradar.inc.Defines.RDA_VALID_OK;
import static com.isoft.iradar.inc.Defines.RDA_VALID_WARNING;
import static com.isoft.iradar.inc.Defines.T_RDA_CLR;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL_BIG;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL_STR;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT_RANGE;
import static com.isoft.iradar.inc.Defines.T_RDA_IP;
import static com.isoft.iradar.inc.Defines.T_RDA_IP_RANGE;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_is_int;
import static com.isoft.iradar.inc.FuncsUtil.rda_strstr;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.Var;
import com.isoft.iradar.core.g;
import com.isoft.iradar.exception.ExitException;
import com.isoft.iradar.model.CMessage;
import com.isoft.iradar.validators.CColorValidator;
import com.isoft.iradar.validators.CDecimalStringValidator;
import com.isoft.iradar.validators.CDecimalValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.web.views.CViewPageFooter;
import com.isoft.iradar.web.views.CViewPageHeader;
import com.isoft.jdk.util.regex.IPattern;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;
import com.isoft.utils.DebugUtil;

public abstract class ValidateUtil {
	public static void unset_request(Object _key) {
		RadarContext._REQUEST().remove(_key);
	}
	
	public static boolean is_int_range(String _value) {
		if (!empty(_value)) {
			for(Object _int_range: explode(",", _value)) {
				_int_range = explode("-", (String)_int_range);
				if (count(_int_range) > 2) {
					return false;
				}
				for(String _int_val: (String[])_int_range) {
					if (!is_numeric(_int_val)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static String BETWEEN(Object min, Object max) {
		return BETWEEN(min, max, "");
	}
	
	public static String BETWEEN(Object min, Object max, String var) {
		return "({"+var+"}>="+min+"&&{"+var+"}<="+max+")&&";
	}
	
	public static String  REGEXP(String regexp) {
		return REGEXP(regexp, "");
	}
	
	public static String  REGEXP(String regexp, String var) {
		return "(preg_match(\""+regexp+"\", {"+var+"}))&&";
	}
	
	public static String  GT(String value) {
		return GT(value, "");
	}
	
	public static String  GT(String value, String var) {
		return "({"+var+"}>="+value+")&&";
	}
	
	public static String  IN(Object array) {
		return IN(array, "");
	}
	
	public static String  IN(Object array, String var) {
		if (Cphp.isArray(array)) {
			array = Cphp.implode(",", CArray.valueOf(array).valuesAsString());
		}
		return "str_in_array({"+var+"},array("+array+"))&&";
	}
	
	public static String  HEX() {
		return HEX("");
	}
	
	public static String  HEX(String var) {
		return "preg_match(\"^([a-zA-Z0-9]+)$\",{"+var+"})&&";
	}
	
	public static String  KEY_PARAM() {
		return KEY_PARAM("");
	}
	
	public static String  KEY_PARAM(String var) {
		return "preg_match(\""+Defines.RDA_PREG_PARAMS+"\",{"+var+"})&&";
	}
	
	public static boolean validate_ipv4(String str, CArray arr) {
		if (preg_match("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$", str, arr) == 0) {
			return false;
		}

		for (int i = 1; i <= 4; i++) {
			if (!is_numeric(arr.get(i)) || Nest.value(arr, i).asInteger() > 255 || Nest.value(arr, i).asInteger() < 0 ) {
				return false;
			}
		}
		
		//校验第一组数字不能为：0、127、
		int slice = Nest.value(arr, 1).asInteger();
		if (slice == 0 || slice == 127) {
			return false;
		}
		
		// 校验第一组数字不能为：0、255、
		slice = Nest.value(arr, 4).asInteger();
		if (slice == 0 || slice == 255) {
			return false;
		}

		return true;
	}
	
	public static boolean validate_ipv6(String str, Object o) {
		return validate_ipv6(str);
	}
	
	public static boolean validate_ipv6(String str) {
		String pattern1 = "([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}";
		String pattern2 = ":(:[a-f0-9]{1,4}){1,7}";
		String pattern3 = "[a-f0-9]{1,4}::([a-f0-9]{1,4}:){0,5}[a-f0-9]{1,4}";
		String pattern4 = "([a-f0-9]{1,4}:){2}:([a-f0-9]{1,4}:){0,4}[a-f0-9]{1,4}";
		String pattern5 = "([a-f0-9]{1,4}:){3}:([a-f0-9]{1,4}:){0,3}[a-f0-9]{1,4}";
		String pattern6 = "([a-f0-9]{1,4}:){4}:([a-f0-9]{1,4}:){0,2}[a-f0-9]{1,4}";
		String pattern7 = "([a-f0-9]{1,4}:){5}:([a-f0-9]{1,4}:){0,1}[a-f0-9]{1,4}";
		String pattern8 = "([a-f0-9]{1,4}:){6}:[a-f0-9]{1,4}";
		String pattern9 = "([a-f0-9]{1,4}:){1,7}:";
		String pattern10 = "::";

		String full = "^("+pattern1+")$|^("+pattern2+")$|^("+pattern3+")$|^("+pattern4+")$|^("+pattern5+")$|^("+pattern6+")$|^("+pattern7+")$|^("+pattern8+")$|^("+pattern9+")$|^("+pattern10+")$";

		if (0 == preg_match(IPattern.CASE_INSENSITIVE, full, str)) {
			return false;
		}
		return true;
	}
	
	public static boolean validate_ip(String str, CArray arr) {
		if (validate_ipv4(str, arr)) {
			return true;
		}

		if (!empty(RDA_HAVE_IPV6)) {
			return validate_ipv6(str);
		}

		return false;
	}

	
	/**
	 * Validate IP mask. IP/bits.
	 * bits range for IPv4: 16 - 30
	 * bits range for IPv6: 112 - 128
	 *
	 * @param string ip_range
	 *
	 * @return bool
	 */
	public static boolean validate_ip_range_mask(String ip_range) {
		String[] parts = explode("/", ip_range);
	
		if (count(parts) != 2) {
			return false;
		}
		String ip = parts[0];
		String bits = parts[1];
	
		CArray arr= array();
		if (validate_ipv4(ip, arr)) {
			return preg_match("^\\d{1,2}$", bits)!=0 && asInteger(bits) >= 16 && asInteger(bits) <= 30;
		} else if (!empty(RDA_HAVE_IPV6) && validate_ipv6(ip, arr)) {
			return preg_match("^\\d{1,3}$", bits)!=-1 && asInteger(bits) >= 112 && asInteger(bits) <= 128;
		} else {
			return false;
		}
	}
	
	/*
	 * Validate IP range. ***.***.***.***[-***]
	 */
	public static boolean validate_ip_range_range(String ip_range) {
		String[] parts = explode("-", ip_range);
		int parts_count;
		if ((parts_count = count(parts)) > 2) {
			return false;
		}

		CArray arr = array();
		if (validate_ipv4(parts[0], arr)) {
			String[] ip_parts = explode("\\.", parts[0]);

			if (parts_count == 2) {
				if (0 == preg_match("^([0-9]{1,3})$", parts[1])) {
					return false;
				}
				int from_value = asInteger(sscanf(ip_parts[3], "%d").get(0));
				int to_value = asInteger(sscanf(parts[1], "%d").get(0));

				if ((to_value >= 255) || (from_value >= to_value)) {
					return false;
				}
			}
		}
		else if (!empty(RDA_HAVE_IPV6) && validate_ipv6(parts[0])) {
			String[] ip_parts = explode(":", parts[0]);
			int ip_parts_count = count(ip_parts);

			if (parts_count == 2) {
				if (-1 == preg_match(IPattern.CASE_INSENSITIVE, "^([a-f0-9]{1,4})$", parts[1])) {
					return false;
				}
				int from_value = asInteger(sscanf(ip_parts[ip_parts_count - 1], "%x").get(0));
				int to_value = asInteger(sscanf(parts[1], "%x").get(0));

				if (from_value > to_value) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	public static boolean validate_ip_range(String str) {
		for (String ip_range : Cphp.explode(",", str)) {
			if (FuncsUtil.rda_strpos(ip_range, "/")>-1) {
				if (!validate_ip_range_mask(ip_range)) {
					return false;
				}
			} else {
				if (!validate_ip_range_range(ip_range)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean validate_port_list(String str) {
		for(Object port_range: explode(",", str)) {
			port_range = explode("-", (String)port_range);
			if (count(port_range) > 2) {
				return false;
			}
			for(String port: (String[])port_range) {
				if (!validatePortNumber(port)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean calc_exp(CArray fields, String field, String expression) {
		if (!empty(rda_strstr(expression, "{}"))) {
			if (!isset(_REQUEST(), field)) {
				return false;
			}
			if (!is_array(_REQUEST().get(field))) {
				expression = str_replace("{}", "_REQUEST[\""+field+"\"]", expression);
			}
			if (is_array(_REQUEST().get(field))) {
				for(Entry<Object, ?> entry: CArray.valueOf(_REQUEST().get(field)).entrySet()) {
					Object key = entry.getKey();
					//Object _val = entry.getValue();
					
					if (0 == preg_match("^([a-zA-Z0-9_]+)$", asString(key))) {
						return false;
					}
					if (!calc_exp2(fields, str_replace("{}", "_REQUEST[\""+field+"\"][\""+key+"\"]", expression))) {
						return false;
					}
				}
				return true;
			}
		}
		return calc_exp2(fields, expression);
	}
	
	public static boolean calc_exp2(CArray<?> fields, String expression) {
		for(Entry<Object, ?> entry: fields.entrySet()) {
			Object f = entry.getKey();
			//Object checks = entry.getValue();
			
			expression = str_replace("{"+f+"}", "_REQUEST[\""+f+"\"]", expression);
		}
		return asBoolean(eval("return ("+trim(expression, '&', ' ')+") ? 1 : 0;"));
	}
	
	public static void unset_not_in_list(CArray fields) {
		for(Object key: RadarContext.getContext().getRequest().getParameterMap().keySet()) {
			if (!Cphp.isset(fields.get(key))) {
				unset_request(key);
			}
		}
	}

	public static void unset_if_zero(CArray<CArray> _fields) {
		for(Entry<Object, CArray> e: _fields.entrySet()) {
			Object _field = e.getKey();
			CArray _checks = e.getValue();
			
//			Integer _type = (Integer)_checks.get(0);
//			Integer _opt = (Integer)_checks.get(1);
			Integer _flags = Nest.as(_checks.get(2)).asInteger();
//			Object _validation = _checks.get(3);
//			Object _exception = _checks.get(4);

			Map request = RadarContext._REQUEST();
			
			if ( _flags!=null && (_flags&Defines.P_NZERO)!=0 
					&& Cphp.isset(request.get(_field)) && Cphp.is_numeric(request.get(_field)) && Nest.as(request.get(_field)).asLong() == 0) {
				unset_request(_field);
			}
		}
	}
	
	public static void unset_action_vars(CArray<CArray> _fields) {
		for(Entry<Object, CArray> e: _fields.entrySet()) {
			Object _field = e.getKey();
			CArray _checks = e.getValue();
			
//			Integer _type = (Integer)_checks.get(0);
//			Integer _opt = (Integer)_checks.get(1);
			Integer _flags = asInteger(_checks.get(2));
//			Object _validation = _checks.get(3);
//			Object _exception = _checks.get(4);

			Map request = RadarContext._REQUEST();
			if ((_flags & Defines.P_ACT)!=0 
					&& Cphp.isset(request.get(_field))) {
				unset_request(String.valueOf(_field));
			}
		}
	}
	
	public static void unset_all() {
		RadarContext._REQUEST().clear();
	}	

	public static int check_type(IIdentityBean idBean, TObj _field, int _flags, TObj _var, int _type) {
		return check_type(idBean, _field, _flags, _var, _type, null);
	}
	
	public static int check_type(IIdentityBean idBean, TObj<String> _field, int _flags, TObj _var, int _type, String _caption) {
		if (_caption == null) {
			_caption = _field.$();
		}
	
		int _err;
		if (is_array(_var.$()) && _type != T_RDA_IP) {
			_err = RDA_VALID_OK;
	
			for(Entry<Object, ?> entry: ((CArray<?>)_var.asCArray()).entrySet()) {
				Object key = entry.getKey();
//				Object v = entry.getValue();
				
				_err |= check_type(idBean, _field, _flags, Nest.value(_var.asCArray(), key), _type);
			}
	
			return _err;
		}
	
		boolean _error = false;
		String _message = "";
	
		CDecimalValidator _decimalValidator;
		if (_type == T_RDA_IP) {
			CArray _arr = array();
			if (!validate_ip(_var.asString(), _arr)) {
				_error = true;
				_message = _s("Field \"%1$s\" is not IP.", _caption);
			}
		}
		else if (_type == T_RDA_IP_RANGE) {
			if (!validate_ip_range(_var.asString())) {
				_error = true;
				_message = _s("Field \"%1$s\" is not IP range.", _caption);
			}
		}
		else if (_type == T_RDA_INT_RANGE) {
			if (!is_int_range(_var.asString())) {
				_error = true;
				_message = _s("Field \"%1$s\" is not integer list or range.", _caption);
			}
		}
		else if (_type == T_RDA_INT) {
			if (!rda_is_int(_var.$())) {
				_error = true;
				_message = _s("Field \"%1$s\" is not integer.", _caption);
			}
		}
		else if (_type == T_RDA_DBL) {
			_decimalValidator = CValidator.init(new CDecimalValidator(), map(
				"maxPrecision" , 16,
				"maxScale" , 4,
				"messageFormat" , _("Value \"%2$s\" of \"%1$s\" has incorrect decimal format."),
				"messagePrecision" , _(
					"Value \"%2$s\" of \"%1$s\" is too long: it cannot have more than %3$s digits before the decimal point "+
					"and more than %4$s digits after the decimal point."
				),
				"messageNatural" , _(
					"Value \"%2$s\" of \"%1$s\" has too many digits before the decimal point: "+
					"it cannot have more than %3$s digits."
				),
				"messageScale" , _(
					"Value \"%2$s\" of \"%1$s\" has too many digits after the decimal point: "+
					"it cannot have more than %3$s digits."
				)
			));
			_decimalValidator.setObjectName(_caption);
	
			if (!_decimalValidator.validate(idBean, _var.asString())) {
				_error = true;
				_message = _decimalValidator.getError();
			}
		}
		else if (_type == T_RDA_DBL_BIG) {
			_decimalValidator = CValidator.init(new CDecimalValidator(), map(
				"maxScale" , 4,
				"messageFormat" , _("Value \"%2$s\" of \"%1$s\" has incorrect decimal format."),
				"messageScale" , _(
					"Value \"%2$s\" of \"%1$s\" has too many digits after the decimal point: "+
					"it cannot have more than %3$s digits."
				)
			));
			_decimalValidator.setObjectName(_caption);
	
			if (!_decimalValidator.validate(idBean, _var.asString())) {
				_error = true;
				_message = _decimalValidator.getError();
			}
		} else if (_type == T_RDA_DBL_STR) {
			CDecimalStringValidator _decimalStringValidator = CValidator.init(new CDecimalStringValidator(), map(
				"messageInvalid", _("Value \"%2$s\" of \"%1$s\" has incorrect decimal format.")
			));
			_decimalStringValidator.setObjectName(_caption);

			if (!_decimalStringValidator.validate(idBean, _var.asString())) {
				_error = true;
				_message = _decimalStringValidator.getError();
			}
		}
		else if (_type == T_RDA_STR) {
			if (!is_string(_var.$())) {
				_error = true;
				_message = _s("Field \"%1$s\" is not string.", _caption);
			}
		}
		else if (_type == T_RDA_CLR) {
			CColorValidator _colorValidator = new CColorValidator();
	
			if (!_colorValidator.validate(idBean, _var.asString())) {
				_var.$("FFFFFF");
	
				_error = true;
				_message = _s("Colour \"%1$s\" is not correct: expecting hexadecimal colour code (6 symbols).", _caption);
			}
		}
	
		if (_error) {
			if (!empty(_flags & P_SYS)) {
				error(_message);
	
				return RDA_VALID_ERROR;
			}
			else {
				info(_message);
	
				return RDA_VALID_WARNING;
			}
		}
	
		return RDA_VALID_OK;
	}
	
	public static void check_trim(TObj _var) {
		if (is_string(_var.$())) {
			_var.$(trim(_var.asString()));
		}
		else if (is_array(_var.$())) {
			for(Entry<Object, ?> entry: ((CArray<?>)_var.asCArray()).entrySet()) {
				Object _key = entry.getKey();
				//Object _val = entry.getValue();
				
				check_trim(Nest.value((Map)_var.$(), _key));
			}
		}
	}
	
	public static int check_field(IIdentityBean idBean, CArray<CArray> fields, TObj field, CArray checks) {
		if (!Cphp.isset(checks.get(5))) {
			checks.put(5, field.$());
		}
		int type = Nest.value(checks, 0).asInteger();
		int opt = Nest.value(checks, 1).asInteger();
		Integer flags = Nest.as(checks.get(2)).asInteger();
		String validation = Nest.value(checks, 3).asString();
		String exception = Nest.value(checks, 4).asString();
		String caption = Nest.value(checks, 5).asString();
	
		Map REQUEST = RadarContext._REQUEST();		
		Map COOKIE = RadarContext._COOKIES();
		
		if (( flags!=null && (flags&Defines.P_UNSET_EMPTY) != 0) && Cphp.isset(REQUEST.get(field.$())) && "".equals(REQUEST.get(field.$()))) {
			unset_request(field.$());
		}
	
		boolean except = !is_null(exception) ? calc_exp(fields, field.asString(), exception) : false;
	
		if (except) {
			if (opt == O_MAND) {
				opt = O_NO;
			} else if (opt == O_OPT) {
				opt = O_MAND;
			} else if (opt == O_NO) {
				opt = O_MAND;
			}
		}
	
		if (opt == O_MAND) {
			if (!isset(REQUEST, field.$())) {
				info(_s("Field \"%1$s\" is mandatory.", caption));
	
				return !empty(flags & P_SYS) ? RDA_VALID_ERROR : RDA_VALID_WARNING;
			}
		} else if (opt == O_NO) {
			if (!isset(REQUEST, field.$())) {
				return RDA_VALID_OK;
			}
	
			unset_request(field.$());
	
			info(_s("Field \"%1$s\" must be missing.", caption));
	
			return !empty(flags & P_SYS) ? RDA_VALID_ERROR : RDA_VALID_WARNING;
		} else if (opt == O_OPT) {
			if (!isset(REQUEST, field.$())) {
				return RDA_VALID_OK;
			} else if( !empty(flags & P_ACT) ){
				if (!isset(REQUEST, "sid")
						|| (isset(COOKIE, "rda_sessionid")
								&& !Cphp.equals(Nest.value(REQUEST, "sid").asString(), Nest.value(COOKIE, "rda_sessionid").asString())
								&& !Cphp.equals(Nest.value(REQUEST, "sid").asString(), substr(Nest.value(COOKIE, "rda_sessionid").asString(), 16, 16))
								&& !Cphp.equals(Nest.value(REQUEST, "sid").asString(), RadarContext.sessionId())
								&& !Cphp.equals(Nest.value(REQUEST, "sid").asString(), substr(RadarContext.sessionId(), 16, 16)))) {
					info(_("Operation cannot be performed due to unauthorized request."));
					return RDA_VALID_ERROR;
				}
			}
		}
	
		if (empty(flags & asInteger(NO_TRIM))) {
			check_trim(Nest.value(REQUEST, field.$()));
		}
	
		int err = check_type(idBean, field, flags, Nest.value(REQUEST, field.$()), type, caption);
		if (err != RDA_VALID_OK) {
			return err;
		}
	
		if ((is_null(exception) || except) && !empty(validation) && !calc_exp(fields, field.asString(), validation)) {
			CArray result = array();
			if (validation == NOT_EMPTY) {
				info(_s("Incorrect value for field \"%1$s\": cannot be empty.", caption));
			}
			// check for BETWEEN() function pattern and extract numbers e.g. ({}>=0&&{}<=999)&&
			else if (0 != preg_match("\\(\\{\\}\\>=([0-9]*)\\&\\&\\{\\}\\<=([0-9]*)\\)\\&\\&", validation, result)) {
				info(_s("Incorrect value \"%1$s\" for \"%2$s\" field: must be between %3$s and %4$s.",
					REQUEST.get(field.$()), caption, result.get(1), result.get(2)));
			}
			else {
				info(_s("Incorrect value \"%1$s\" for \"%2$s\" field.", REQUEST.get(field.$()), caption));
			}
	
			return !empty(flags & P_SYS) ? RDA_VALID_ERROR : RDA_VALID_WARNING;
		}
	
		return Defines.RDA_VALID_OK;
	}

	public static void invalid_url(IIdentityBean idBean) {
		invalid_url(idBean, null);
	}
	public static void invalid_url(IIdentityBean idBean, String _msg) {
		if (Cphp.empty(_msg)) {
			_msg = Cphp._("iRadar has received an incorrect request.");
		}

		// required global parameters for correct including page_header.php
		Var<List<CMessage>> RDA_MESSAGES = g.RDA_MESSAGES;
		

		// backup messages before including page_header.php
		List _temp = RDA_MESSAGES.$();
		RDA_MESSAGES.$(null);

		CViewPageHeader.renderAndShow(idBean);

		// rollback reseted messages
		RDA_MESSAGES.$(_temp);

		unset_all();
		FuncsUtil.show_error_message(_msg);
		CViewPageFooter.renderAndShow(idBean);
		throw new ExitException(ACCESS_EXIT);
	}
	
	public static boolean check_fields(IIdentityBean idBean, CArray fields) {
		return check_fields(idBean, fields, true);
	}
	
	public static boolean check_fields(IIdentityBean idBean, CArray<CArray> _fields, boolean _show_messages) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray<CArray> _system_fields = map(
			"sid",			array(Defines.T_RDA_STR, Defines.O_OPT, Defines.P_SYS, HEX(),		null),
			"triggers_hash",array(Defines.T_RDA_STR, Defines.O_OPT, Defines.P_SYS, Defines.NOT_EMPTY,	null),
			"print",		array(Defines.T_RDA_INT, Defines.O_OPT, Defines.P_SYS, IN('1'),	null),
			"sort",			array(Defines.T_RDA_STR, Defines.O_OPT, Defines.P_SYS, null,		null),
			"sortorder",	array(Defines.T_RDA_STR, Defines.O_OPT, Defines.P_SYS, null,		null),
			"page",			array(Defines.T_RDA_INT, Defines.O_OPT, Defines.P_SYS, null,		null), // paging
			"ddreset",		array(Defines.T_RDA_INT, Defines.O_OPT, Defines.P_SYS, null,		null)
		);
		_fields = FuncsUtil.rda_array_merge(_system_fields, _fields);
	
		int _err = Defines.RDA_VALID_OK;
		for(Entry<Object, CArray> e: _fields.entrySet()) {
			Object _field = e.getKey();
			CArray _checks = e.getValue();
			
			//FIXME: becasue the "check_field" function need ptr argument so call by TObj, but won't check the CArray key 
			int r = check_field(idBean, _fields, Nest.as(_field), _checks);
			if(r!=RDA_VALID_OK && DebugUtil.isDebugEnabled()) {
				DebugUtil.debug(_field + " != " + _REQUEST().get(_field));
			}
			_err |= r;
		}
	
		unset_not_in_list(_fields);
		unset_if_zero(_fields);
	
		if (_err != Defines.RDA_VALID_OK) {
			unset_action_vars(_fields);
		}
	
		_fields = null;
	
		if ((_err & Defines.RDA_VALID_ERROR) !=0) {
			invalid_url(idBean);
		}
	
		if (_show_messages && _err != Defines.RDA_VALID_OK) {
			FuncsUtil.show_messages((_err == Defines.RDA_VALID_OK), null, Cphp._("Page received incorrect data"));
		}
	
		return (_err == Defines.RDA_VALID_OK);
	}
	
	public static boolean validatePortNumberOrMacro(String _port) {
		return (validatePortNumber(_port) || validateUserMacro(_port));
	}
	
	public static boolean  validatePortNumber(String _port) {
		return validateNumber(_port, RDA_MIN_PORT_NUMBER, RDA_MAX_PORT_NUMBER);
	}
	
	public static boolean validateNumber(Object value) {
		return validateNumber(value, null);
	}
	public static boolean validateNumber(Object value, Integer min) {
		return validateNumber(value, min, null);
	}
	public static boolean validateNumber(Object value, Integer min, Integer max) {
		if (!FuncsUtil.rda_is_int(value)) {
			return false;
		}

		if (min != null && Nest.as(value).asInteger() < min) {
			return false;
		}

		if (max != null && Nest.as(value).asInteger() > max) {
			return false;
		}

		return true;
	}
	
	public static boolean validateUserMacro(String value) {
		return Cphp.preg_match("^"+Defines.RDA_PREG_EXPRESSION_USER_MACROS+"$", value)>0;
	}
	
	/**
	 * Validate, if unix time in (2010.01.01 00:00:01 - 2038.01.01 00:00:00).
	 *
	 * @param int _time
	 *
	 * @return bool
	 */
	public static boolean validateUnixTime(Object _time) {
		return (is_numeric(_time) && asLong(_time) > 0 && asLong(_time) <= 2147464800);
	}
	
	
	/**
	 * Validate if date and time are in correct range, e.g. month is not greater than 12 etc.
	 *
	 * @param int _year
	 * @param int _month
	 * @param int _day
	 * @param int _minutes
	 * @param int _seconds
	 *
	 * @return bool
	 */
	public static boolean validateDateTime(int _year, int _month, int _day, int _hours, int _minutes, Integer _seconds) {
		return !(_month < 1 || _month > 12
				|| _day < 1  || _day > 31 || ((_month == 4 || _month == 6 || _month == 9 || _month == 11) && _day > 30)
				|| (_month == 2 && (((_year % 4) == 0 && _day > 29) || ((_year % 4) != 0 && _day > 28)))
				|| _hours < 0 || _hours > 23
				|| _minutes < 0 || _minutes > 59
				|| (!is_null(_seconds) && (_seconds < 0 || _seconds > 59)));
	}
	public static boolean validateDateTime(int _year, int _month, int _day, int _hours, int _minutes) {
		return validateDateTime(_year, _month, _day, _hours, _minutes, null);
	}
	
	/**
	 * Validate allowed date interval (2010.01.01-2038.01.01).
	 *
	 * @param int _year
	 * @param int _month
	 * @param int _day
	 *
	 * @return bool
	 */
	public static boolean  validateDateInterval(int _year, int _month, int _day) {
		return !(_year < 2010 || _year > 2038 || (_year == 2038 && ((_month > 1) || (_month == 1 && _day > 1))));
	}
}
