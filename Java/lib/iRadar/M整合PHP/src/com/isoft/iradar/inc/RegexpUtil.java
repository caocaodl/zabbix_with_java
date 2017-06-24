package com.isoft.iradar.inc;

import static com.isoft.biz.daoimpl.radar.CDB.delete;
import static com.isoft.biz.daoimpl.radar.CDB.insert;
import static com.isoft.biz.daoimpl.radar.CDB.update;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_ANY_INCLUDED;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_FALSE;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_INCLUDED;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_NOT_INCLUDED;
import static com.isoft.iradar.inc.Defines.EXPRESSION_TYPE_TRUE;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class RegexpUtil {
	
	public static Map getRegexp(IIdentityBean idBean, SQLExecutor executor, long regexpId) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor,
			"SELECT re.* FROM regexps re"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "regexps", "re")+
			" AND regexpid=#{regexpid}"+sqlParts.marshalParam(regexpId),
			sqlParts.getNamedParams()
		));
	}
	
	public static CArray<Map> getRegexpExpressions(IIdentityBean idBean, SQLExecutor executor, long regexpId) {
		CArray<Map> expressions = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbExpressions = DBselect(executor,
			"SELECT e.expressionid,e.expression,e.expression_type,e.exp_delimiter,e.case_sensitive"+
			" FROM expressions e"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "expressions", "e")+
			" AND regexpid=#{regexpid}"+sqlParts.marshalParam(regexpId),
			sqlParts.getNamedParams()
		);
		for (Map expression : dbExpressions) {
			Nest.value(expressions,expression.get("expressionid")).$(expression);
		}
		return expressions;
	}
	
	public static boolean addRegexp(IIdentityBean idBean, SQLExecutor executor, Map regexp, CArray<Map> expressions) {
		try {
			// check required fields
			CArray dbFields = map("name", null, "test_string", "");

			if (!check_db_fields(dbFields, regexp)) {
				throw new Exception(_("Incorrect arguments passed to function")+" [addRegexp]");
			}

			// check duplicate name
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT re.regexpid"+
					" FROM regexps re"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "regexps", "re")+
					" AND re.name="+sqlParts.marshalParam(Nest.value(regexp,"name").$());
			if (!DBselect(executor,sql,sqlParts.getNamedParams()).isEmpty()) {
				throw new Exception(_s("Regular expression \"%s\" already exists.", Nest.value(regexp,"name").$()));
			}

			CArray<Long> regexpIds = insert(idBean, executor,"regexps", (CArray)array(regexp));
			long regexpId = reset(regexpIds);

			addRegexpExpressions(idBean, executor, regexpId, expressions);
		} catch (Exception $e) {
			error($e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean updateRegexp(IIdentityBean idBean, SQLExecutor executor, Map regexp, CArray<Map> expressions) {
		try {
			long regexpId = Nest.value(regexp,"regexpid").asLong();
			unset(regexp,"regexpid");

			// check existence
			if (empty(getRegexp(idBean, executor, regexpId))) {
				throw new Exception(_("Regular expression does not exist."));
			}

			// check required fields
			CArray dbFields = map("name", null);
			if (!check_db_fields(dbFields, regexp)) {
				throw new Exception(_("Incorrect arguments passed to function")+" [updateRegexp]");
			}

			// check duplicate name
			SqlBuilder sqlParts = new SqlBuilder();
			Map dbRegexp = DBfetch(DBselect(executor,
				"SELECT re.regexpid"+
				" FROM regexps re"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "regexps", "re")+
				    " AND re.name=#{name}"+sqlParts.marshalParam(Nest.value(regexp,"name").$()),
				sqlParts.getNamedParams()
			));
			if (!empty(dbRegexp) && bccomp(regexpId, Nest.value(dbRegexp,"regexpid").$()) != 0) {
				throw new Exception(_s("Regular expression \"%s\" already exists.", Nest.value(regexp,"name").$()));
			}

			rewriteRegexpExpressions(idBean, executor, regexpId, expressions);

			update(idBean, executor,"regexps", array((Map)map(
				"values", regexp,
				"where", map("regexpid", regexpId)
			)));
		} catch (Exception e) {
			e.printStackTrace();
			error(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Rewrite iRadar regexp expressions.
	 * If all fields are equal to existing expression, that expression is not touched.
	 * Other expressions are removed and new ones created.
	 *
	 * @param string _regexpId
	 * @param array  _expressions
	 */
	public static void rewriteRegexpExpressions(IIdentityBean idBean, SQLExecutor executor, long regexpId, CArray<Map> expressions) {
		CArray<Map> dbExpressions = getRegexpExpressions(idBean, executor,regexpId);

		CArray<Map> expressionsToAdd = array();
		CArray<Map> expressionsToUpdate = array();

		for(Map expression : expressions) {
			if (!isset(expression,"expressionid")) {
				expressionsToAdd.add(expression);
			} else if (isset(dbExpressions,expression.get("expressionid"))) {
				expressionsToUpdate.add(expression);
				unset(dbExpressions,expression.get("expressionid"));
			}
		}

		if (!empty(dbExpressions)) {
			Long[] dbExpressionIds = rda_objectValues(dbExpressions, "expressionid").valuesAsLong();
			deleteRegexpExpressions(idBean, executor, dbExpressionIds);
		}

		if (!empty(expressionsToAdd)) {
			addRegexpExpressions(idBean, executor, regexpId, expressionsToAdd);
		}

		if (!empty(expressionsToUpdate)) {
			updateRegexpExpressions(idBean, executor, expressionsToUpdate);
		}
	}
	
	public static void addRegexpExpressions(IIdentityBean idBean, SQLExecutor executor, long regexpId, CArray<Map> expressions) {
		CArray dbFields = map("expression", null, "expression_type", null);
		for(Map expression : expressions) {
			if (!check_db_fields(dbFields, expression)) {
				throw new RuntimeException(_("Incorrect arguments passed to function")+" [add_expression]");
			}

			Nest.value(expression,"regexpid").$(regexpId);
		}
		insert(idBean, executor, "expressions", expressions);
	}
	
	public static void updateRegexpExpressions(IIdentityBean idBean, SQLExecutor executor, CArray<Map> expressions) {
		for(Map expression : expressions) {
			long expressionId = Nest.value(expression,"expressionid").asLong();
			unset(expression,"expressionid");
			update(idBean, executor, "expressions", array((Map)map(
				"values", expression,
				"where" , map("expressionid", expressionId)
			)));
		}
	}
	
	public static void deleteRegexpExpressions(IIdentityBean idBean, SQLExecutor executor, Long[] expressionIds) {
		delete(idBean, executor, "expressions", (Map)map("expressionid", expressionIds));
	}
	
	public static CArray<String> expression_type2str() {
		CArray<String> types = map(
			EXPRESSION_TYPE_INCLUDED, _("Character string included"),
			EXPRESSION_TYPE_ANY_INCLUDED, _("Any character string included"),
			EXPRESSION_TYPE_NOT_INCLUDED, _("Character string not included"),
			EXPRESSION_TYPE_TRUE, _("Result is TRUE"),
			EXPRESSION_TYPE_FALSE, _("Result is FALSE")
		);
		return types;
	}
	
	public static String expression_type2str(int type) {
		CArray<String> types = map(
			EXPRESSION_TYPE_INCLUDED, _("Character string included"),
			EXPRESSION_TYPE_ANY_INCLUDED, _("Any character string included"),
			EXPRESSION_TYPE_NOT_INCLUDED, _("Character string not included"),
			EXPRESSION_TYPE_TRUE, _("Result is TRUE"),
			EXPRESSION_TYPE_FALSE, _("Result is FALSE")
		);
		if (isset(types, type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}

	public static CArray<String> expressionDelimiters() {
		return map(
				",", ",",
				".", ".",
				"/", "/"
			);
	}

}
