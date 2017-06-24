package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_REGEXP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.RegexpUtil.addRegexp;
import static com.isoft.iradar.inc.RegexpUtil.getRegexp;
import static com.isoft.iradar.inc.RegexpUtil.updateRegexp;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.GlobalRegExp;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AdmRegexpsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of regular expressions"));
		page("file", "adm.regexps.action");
		page("hist_arg", new String[] {});
		page("type", detect_page_type());
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"regexpids",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	null),
			"regexpid",			array(T_RDA_INT, O_OPT, P_SYS,		DB_ID,	"isset({form})&&{form}==\"update\""),
			"name",				array(T_RDA_STR, O_OPT, null,		NOT_EMPTY, "isset({save})", _("Name")),
			"test_string",			array(T_RDA_STR, O_OPT, null,		null,	"isset({save})", _("Test string")),
			"expressions",		array(T_RDA_STR, O_OPT, null,		null,	"isset({save})"),
			"save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",				array(T_RDA_STR, O_OPT, null,		null,	null),
			"go",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"form",					array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form_refresh",		array(T_RDA_INT, O_OPT, null,		null,	null),
			// ajax
			"output",				array(T_RDA_STR, O_OPT, P_ACT,		null,	null),
			"ajaxaction",			array(T_RDA_STR, O_OPT, P_ACT,		null,	null),
			"ajaxdata",			array(T_RDA_STR, O_OPT, P_ACT,		null,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST,"regexpid")) {
			Map params = new HashMap();
			params.put("regexpid", get_request("regexpid"));
			Map regExp = DBfetch(DBselect(executor,"SELECT re.regexpid FROM regexps re WHERE re.regexpid=#{regexpid}", params));
			if (empty(regExp)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go") && !isset(_REQUEST,"regexpid")) {
			if (!isset(_REQUEST,"regexpids") || !is_array(Nest.value(_REQUEST,"regexpids").$())) {
				access_deny();
			} else {
				SqlBuilder sqlParts = new SqlBuilder();
				Map regExpChk = DBfetch(DBselect(executor,
					"SELECT COUNT(*) AS cnt FROM regexps re WHERE "+sqlParts.dual.dbConditionInt("re.regexpid", Nest.array(_REQUEST,"regexpids").asLong()),
					sqlParts.getNamedParams()
				));
				if (Nest.value(regExpChk,"cnt").asInteger() != count(Nest.value(_REQUEST,"regexpids").$())) {
					access_deny();
				}
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"output") && "ajax".equals(Nest.value(_REQUEST,"output").asString())) {
			AjaxResponse ajaxResponse = new AjaxResponse();
			Map ajaxData = get_request("ajaxdata", array());

			if (isset(_REQUEST,"ajaxaction") && "test".equals(Nest.value(_REQUEST,"ajaxaction").asString())) {
				Map result = map(
					"expressions", array(),
					"final", true
				);
				String testString = Nest.value(ajaxData,"testString").asString();

				for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(ajaxData,"expressions").asCArray()).entrySet()) {
				    Object id = e.getKey();
				    Map expression = e.getValue();
					boolean match = GlobalRegExp.matchExpression(expression, testString);

					Nest.value(result,"expressions",id).$(match);
					Nest.value(result,"final").$(Nest.value(result,"final").asBoolean() && match);
				}

				ajaxResponse.success(result);
			}

			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"regexpid")) {
			unset(_REQUEST,"regexpid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			final Map regExp = map(
				"name", Nest.value(_REQUEST,"name").$(),
				"test_string", Nest.value(_REQUEST,"test_string").$()
			);
			final CArray<Map> expressions = get_request("expressions", array());

			DBstart(executor);
			
			boolean result;
			String msg1, msg2;
			if (isset(_REQUEST,"regexpid")) {
				Nest.value(regExp,"regexpid").$(Nest.value(_REQUEST,"regexpid").$());
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return updateRegexp(getIdentityBean(), executor, regExp, expressions);
					}
				});
				msg1 = _("Regular expression updated");
				msg2 = _("Cannot update regular expression");
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return addRegexp(getIdentityBean(), executor, regExp, expressions);
					}
				});
				msg1 = _("Regular expression added");
				msg2 = _("Cannot add regular expression");
			}

			show_messages(result, msg1, msg2);

			if (result) {
				add_audit(getIdentityBean(), executor, !isset(_REQUEST,"regexpid") ? AUDIT_ACTION_ADD : AUDIT_ACTION_UPDATE,
					AUDIT_RESOURCE_REGEXP, _("Name")+NAME_DELIMITER+Nest.value(_REQUEST,"name").$());
				unset(_REQUEST,"form");
			}
			
			DBend(executor);
			
			clearCookies(result);
		} else if (isset(_REQUEST,"go")) {
			if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
				CArray regExpIds = get_request("regexpid", array());

				if (isset(_REQUEST,"regexpids")) {
					regExpIds = Nest.value(_REQUEST,"regexpids").asCArray();
				}

				CArray<Map> regExps = array();
				for(Object regExpId : regExpIds) {
					Nest.value(regExps,regExpId).$(getRegexp(getIdentityBean(), executor, Nest.as(regExpId).asLong()));
				}
				
				DBstart(executor);

				SqlBuilder sqlParts = new SqlBuilder();
				boolean result = DBexecute(executor, 
						"DELETE FROM regexps WHERE "+sqlParts.dual.dbConditionInt("regexpid", regExpIds.valuesAsLong()),
						sqlParts.getNamedParams());

				int regExpCount = count(regExpIds);

				if (result) {
					for (Entry<Object, Map> e : regExps.entrySet()) {
					    Object regExpId = e.getKey();
					    Map regExp = e.getValue();
						add_audit(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_REGEXP, "Id ["+regExpId+"] "+_("Name")+" ["+Nest.value(regExp,"name").$()+"]");
					}

					unset(_REQUEST,"form");
					unset(_REQUEST,"regexpid");
				}

				result = DBend(executor, result);
				
				show_messages(result,
					_n("Regular expression deleted", "Regular expressions deleted", regExpCount),
					_n("Cannot delete regular expression", "Cannot delete regular expressions", regExpCount)
				);

				clearCookies(result);
			}
		}

		/* Display */
		CView regExpView;
		CComboBox generalComboBox = new CComboBox("configDropDown", "adm.regexps.action", "redirect(this.options[this.selectedIndex].value);");
		generalComboBox.addItems((CArray)map(
			"adm.gui.action", _("GUI"),
			"adm.housekeeper.action", _("Housekeeping"),
//FIXME
//			"adm.images.action", _("Images"),
//			"adm.iconmapping.action", _("Icon mapping"),
			"adm.regexps.action", _("Regular expressions"),
			"adm.macros.action", _("Macros"),
			"adm.valuemapping.action", _("Value mapping"),
			"adm.workingtime.action", _("Working time"),
			"adm.triggerseverities.action", _("Trigger severities"),
			"adm.triggerdisplayoptions.action", _("Trigger displaying options"),
			"adm.other.action", _("Other")
		));
		CForm regExpForm = new CForm();
		regExpForm.cleanItems();
		regExpForm.addItem(generalComboBox);
		if (!isset(_REQUEST,"form")) {
			regExpForm.addItem(new CSubmit("form", _("New regular expression")));
		}

		CWidget regExpWidget = new CWidget();
		regExpWidget.addPageHeader(_("CONFIGURATION OF REGULAR EXPRESSIONS"), regExpForm);

		if (isset(_REQUEST,"form")) {
			Map data = map(
				"form_refresh", get_request("form_refresh"),
				"regexpid", get_request("regexpid")
			);

			if (isset(_REQUEST,"regexpid") && !isset(_REQUEST,"form_refresh")) {
				Map params = new HashMap();
				params.put("regexpid", Nest.value(_REQUEST,"regexpid").$());
				Map regExp = DBfetch(DBselect(executor,
					"SELECT re.name,re.test_string"+
					" FROM regexps re"+
					" WHERE re.regexpid=#{regexpid}",
					params
				));

				Nest.value(data,"name").$(Nest.value(regExp,"name").$());
				Nest.value(data,"test_string").$(Nest.value(regExp,"test_string").$());

				CArray<Map> dbExpressions = DBselect(executor,
					"SELECT e.expressionid,e.expression,e.expression_type,e.exp_delimiter,e.case_sensitive"+
					" FROM expressions e"+
					" WHERE e.regexpid=#{regexpid}"+
					" ORDER BY e.expression_type",
					params
				);
				Nest.value(data,"expressions").$(dbExpressions);
			} else {
				Nest.value(data,"name").$(get_request("name", ""));
				Nest.value(data,"test_string").$(get_request("test_string", ""));
				Nest.value(data,"expressions").$(get_request("expressions", array()));
			}

			regExpView = new CView("administration.general.regularexpressions.edit", data);
		} else {
			Map data =map(
				"cnf_wdgt", regExpWidget,
				"regexps", array(),
				"regexpids", array()
			);

			CArray<Map> dbRegExp = DBselect(executor, "SELECT re.* FROM regexps re ");
			for(Map regExp : dbRegExp) {
				Nest.value(regExp,"expressions").$(array());
				Nest.value(data,"regexps",regExp.get("regexpid")).$(regExp);
				Nest.value(data,"regexpids",regExp.get("regexpid")).$(Nest.value(regExp,"regexpid").$());
			}

			order_result(Nest.value(data,"regexps").asCArray(), "name");

			SqlBuilder sqlParts = new SqlBuilder();
			Nest.value(data,"db_exps").$(DBselect(executor,
				"SELECT e.*"+
				" FROM expressions e"+
				" WHERE "+sqlParts.dual.dbConditionInt("e.regexpid", Nest.array(data,"regexpids").asLong())+
				" ORDER BY e.expression_type",
				sqlParts.getNamedParams()
			));

			regExpView = new CView("administration.general.regularexpressions.list", data);
		}

		regExpWidget.addItem(regExpView.render(getIdentityBean(), executor));
		regExpWidget.show();
	}
}
