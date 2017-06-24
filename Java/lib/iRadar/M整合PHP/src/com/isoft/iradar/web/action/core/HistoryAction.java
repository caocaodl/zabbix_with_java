package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_HIDE;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_INVERT_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_SHOW;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_BLUE;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_GREEN;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_RED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HistoryAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("History"));
		page("file", "history.action");
		page("hist_arg", new String[] {"itemid", "hostid", "groupid", "graphid", "period", "dec", "inc", "left", "right", "stime", "action"});
		page("scripts", new String[] {"class.calendar.js", "gtlc.js", "flickerfreescreen.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
		
		if (isset(_REQUEST,"plaintext")) {
			define("RDA_PAGE_NO_MENU", 1);
		}
		define("RDA_PAGE_DO_JS_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"itemid",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,	"!isset({favobj})"),
			"period",				array(T_RDA_INT, O_OPT, null,	null,	null),
			"dec",					array(T_RDA_INT, O_OPT, null,	null,	null),
			"inc",					array(T_RDA_INT, O_OPT, null,	null,	null),
			"left",					array(T_RDA_INT, O_OPT, null,	null,	null),
			"right",					array(T_RDA_INT, O_OPT, null,	null,	null),
			"stime",				array(T_RDA_STR, O_OPT, null,	null,	null),
			"filter_task",			array(T_RDA_STR, O_OPT, null,	IN(FILTER_TASK_SHOW+","+FILTER_TASK_HIDE+","+FILTER_TASK_MARK+","+FILTER_TASK_INVERT_MARK), null),
			"filter",					array(T_RDA_STR, O_OPT, null,	null,	null),
			"mark_color",		array(T_RDA_STR, O_OPT, null,	IN(MARK_COLOR_RED+","+MARK_COLOR_GREEN+","+MARK_COLOR_BLUE), null),
			"cmbitemlist",		array(T_RDA_INT, O_OPT, null,	DB_ID,	null),
			"plaintext",			array(T_RDA_STR, O_OPT, null,	null,	null),
			"action",				array(T_RDA_STR, O_OPT, P_SYS,	IN("\"showgraph\",\"showvalues\",\"showlatest\",\"add\",\"remove\""), null),
			// ajax
			"favobj",				array(T_RDA_STR, O_OPT, P_ACT,	null,	null),
			"favref",				array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY, null),
			"favid",					array(T_RDA_INT, O_OPT, P_ACT,	null,	null),
			"favaction",			array(T_RDA_STR, O_OPT, P_ACT,	IN("'add','remove','flop'"), null),
			"favstate",			array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY, null),
			// actions
			"reset",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"cancel",				array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"form",					array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"form_copy_to",	array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"form_refresh",		array(T_RDA_INT, O_OPT, null,	null,	null),
			"fullscreen",			array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("timeline".equals(Nest.value(_REQUEST,"favobj").asString())) {
				navigation_bar_calc(getIdentityBean(), executor, "web.item.graph", Nest.value(_REQUEST,"favid").asLong(), true);
			}
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.history.filter.state", Nest.value(_REQUEST,"favstate").asInteger(), PROFILE_TYPE_INT);
			}
			if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("itemid", "graphid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor,"web.favorite.graphids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { rm4favorites(\"itemid\", \""+Nest.value(_REQUEST,"favid").asString()+"\", 0); }\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.graphids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());

					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { add2favorites(\"itemid\", \""+Nest.value(_REQUEST,"favid").asInteger()+"\"); }\n");
					}
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
				}
			}

			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor, "web.history.timelinefixed", Nest.value(_REQUEST,"favid").asInteger(), PROFILE_TYPE_INT);
				}
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		Nest.value(_REQUEST,"action").$(get_request("action", "showgraph"));
		Nest.value(_REQUEST,"itemid").$(array_unique(rda_toArray(Nest.value(_REQUEST,"itemid").$())));

		/* Display */
		CItemGet ioptions = new CItemGet();
		ioptions.setItemIds(Nest.array(_REQUEST,"itemid").asLong());
		ioptions.setWebItems(true);
		ioptions.setSelectHosts(new String[]{"name"});
		ioptions.setOutput(new String[]{"itemid", "key_", "name", "value_type", "hostid", "valuemapid"});
		ioptions.setPreserveKeys(true);
		CArray<Map> items = API.Item(getIdentityBean(), executor).get(ioptions);

		for(Object itemid : Nest.value(_REQUEST,"itemid").asCArray()) {
			if (!isset(items,itemid)) {
				access_deny();
			}
		}

		items = CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, items);

		Map item = reset(items);
		Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
		Nest.value(item,"hostname").$(Nest.value(host,"name").$());

		CArray data = map(
			"itemids", get_request("itemid"),
			"items", items,
			"item", item,
			"action", get_request("action"),
			"period", get_request("period"),
			"stime", get_request("stime"),
			"plaintext", isset(_REQUEST,"plaintext"),
			"iv_string", map(ITEM_VALUE_TYPE_LOG, 1, ITEM_VALUE_TYPE_TEXT, 1),
			"iv_numeric", map(ITEM_VALUE_TYPE_FLOAT, 1, ITEM_VALUE_TYPE_UINT64, 1),
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$()
		);

		// render view
		CView historyView = new CView("monitoring.history", data);
		historyView.render(getIdentityBean(), executor);
		historyView.show();
	}

}
