package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_ANY_INCLUDED;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_FALSE;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_NOT_INCLUDED;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_TRUE;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.types.CArray.array;
import static org.apache.commons.lang.StringUtils.indexOf;
import static org.apache.commons.lang.StringUtils.upperCase;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.Var;
import com.isoft.iradar.core.g;
import com.isoft.iradar.exception.RegexpException;
import com.isoft.jdk.util.regex.IPattern;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class for regular expressions and iRadar global expressions.
 * Any string that begins with '@' is treated as iRadar expression.
 * Data from iRadar expressions is taken from DB, and cached in static variable.
 *
 * @throws Exception
 */
@CodeConfirmed("benne.2.2.6")
public class GlobalRegExp {
	
	private final static int ERROR_REGEXP_EMPTY = 1;
	private final static int ERROR_REGEXP_NOT_EXISTS = 2;
	
	/**
	 * Determine if it's iRadar expression.
	 *
	 * @var bool
	 */
	private boolean isRadarRegexp;
	
	/**
	 * If we create simple regular expression this contains itself as a string,
	 * if we create iRadar expression this contains array of expressions taken from DB.
	 *
	 * @var array
	 */
	private CArray<Map> expression;
	
	/**
	 * If we create simple regular expression this contains itself as a string,
	 * if we create iRadar expression this contains array of expressions taken from DB.
	 *
	 * @var string
	 */
	private String expression_str;
	
	/**
	 * Cache for iRadar expressions.
	 *
	 * @var array
	 */
	private Var<CArray<CArray<Map>>> _cachedExpressions = g._cachedExpressions;
	
	/**
	 * Initialize expression, gets data from db for iRadar expressions.
	 *
	 * @param string regExp
	 *
	 * @throws Exception
	 */
	public GlobalRegExp(SQLExecutor executor, String regExp){
		if (regExp !=null && regExp.length()>0 && regExp.charAt(0) == '@') {
			isRadarRegexp = true;
			regExp = substr(regExp, 1);

			if (!isset(_cachedExpressions.$(),regExp)) {
				Nest.value(_cachedExpressions.$(),regExp).$(array());
				Map params = new HashMap();
				params.put("name", regExp);
				CArray<Map> dbRegExps = DBselect(executor,
					"SELECT e.regexpid,e.expression,e.expression_type,e.exp_delimiter,e.case_sensitive"+
					" FROM expressions e,regexps r"+
					" WHERE e.regexpid=r.regexpid"+
						" AND r.name=#{name}",
					params
				);
				for(Map expression : dbRegExps) {
					_cachedExpressions.$().get(regExp).add(expression);
				}

				if (empty(_cachedExpressions.$().get(regExp))) {
					unset(_cachedExpressions.$(),regExp);
					throw new RegexpException("Does not exist", ERROR_REGEXP_NOT_EXISTS);
				}
			}
			expression = _cachedExpressions.$().get(regExp);
		} else {
			isRadarRegexp = false;
			expression_str = regExp;
		}
	}
	
	/**
	 * Checks if expression is valid.
	 *
	 * @static
	 *
	 * @param regExp
	 *
	 * @throws Exception
	 * @return bool
	 */
	public static boolean isValid(SQLExecutor executor, String regExp) {
		if (rda_empty(regExp)) {
			throw new RegexpException("Empty expression", ERROR_REGEXP_EMPTY);
		}
		if (regExp.charAt(0) == '@') {
			regExp = substr(regExp, 1);
			Map params = new HashMap();
			params.put("name", regExp);
			String sql = "SELECT r.regexpid"+
					" FROM regexps r"+
					" WHERE r.name=#{name}";
			if (empty(DBfetch(DBselect(executor, sql, params)))) {
				throw new RegexpException(_("Global expression does not exist."), ERROR_REGEXP_NOT_EXISTS);
			}
		}
		return true;
	}
	
	/**
	 * @param string str
	 *
	 * @return bool
	 */
	public boolean match(String str) {
		boolean result;
		if (isRadarRegexp) {
			result = true;
			for(Map expr : expression) {
				result = matchExpression(expr, str);
				if (!result) {
					break;
				}
			}
		} else {
			result = preg_match(expression_str, str)==0;
		}
		return result;
	}
	
	public static boolean matchExpression(Map expr, String str) {
		boolean result;
		int type = Nest.value(expr,"expression_type").asInteger();
		if (type == EXPRESSION_TYPE_TRUE || type == EXPRESSION_TYPE_FALSE) {
			result = _matchRegular(expr, str);
		} else {
			result = _matchString(expr, str);
		}
		return result;
	}
	
	/**
	 * Matches expression as regular expression.
	 *
	 * @static
	 *
	 * @param array expr
	 * @param string str
	 *
	 * @return bool
	 */
	private static boolean _matchRegular(Map expr, String str) {
		String pattern = Nest.value(expr,"expression").asString();
		int expectedResult = (Nest.value(expr,"expression_type").asInteger() == EXPRESSION_TYPE_TRUE) ? 1 : 0;
		if (!Nest.value(expr,"case_sensitive").asBoolean()) {
			return preg_match(IPattern.CASE_INSENSITIVE, pattern, str) == expectedResult;
		} else {
			return preg_match(pattern, str) == expectedResult;
		}
	}
	
	/**
	 * Matches expression as string.
	 *
	 * @static
	 *
	 * @param array expr
	 * @param string str
	 *
	 * @return bool
	 */
	private static boolean _matchString(Map expr, String str) {
		boolean result = true;
		String[] paterns;
		if (Nest.value(expr,"expression_type").asInteger() == EXPRESSION_TYPE_ANY_INCLUDED) {
			paterns = explode(Nest.value(expr,"exp_delimiter").asString(), Nest.value(expr,"expression").asString());
		} else {
			paterns = Nest.array(expr,"expression").asString();
		}
		boolean expectedResult = (Nest.value(expr,"expression_type").asInteger() != EXPRESSION_TYPE_NOT_INCLUDED);		
		boolean tmp;		
		for(String patern : paterns) {
			if (Nest.value(expr,"case_sensitive").asBoolean()) {
				tmp = ((indexOf(str, patern) !=-1) == expectedResult);
			} else {
				tmp = ((indexOf(upperCase(str), upperCase(patern)) !=-1) == expectedResult);
			}
			if (Nest.value(expr,"expression_type").asInteger() == EXPRESSION_TYPE_ANY_INCLUDED && tmp) {
				return true;
			} else {
				result = (result && tmp);
			}
		}
		return result;
	}

}
