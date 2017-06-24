package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit_details;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCREEN;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HALIGN_CENTER;
import static com.isoft.iradar.inc.Defines.HALIGN_RIGHT;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SCREEN_SIMPLE_ITEM;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_DATE_DESC;
import static com.isoft.iradar.inc.Defines.SCREEN_SORT_TRIGGERS_RECIPIENT_DESC;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.VALIGN_BOTTOM;
import static com.isoft.iradar.inc.Defines.VALIGN_MIDDLE;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.ScreensUtil.getResourceNameByType;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ScreeneditAction extends RadarBaseAction {
	
	private Map screen;
	private CScreenGet soptions;
	private CTemplateScreenGet tsoptions;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of screens"));
		page("file", "screenedit.action");
		page("hist_arg", new String[] {"screenid"});
		page("scripts", new String[] {"class.cscreen.js", "class.calendar.js", "gtlc.js", "flickerfreescreen.js", "multiselect.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"screenid",		array(T_RDA_INT, O_MAND, P_SYS,	DB_ID,			null),
			"screenitemid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"resourcetype",	array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 16),	"isset({save})"),
			"caption",			array(T_RDA_STR, O_OPT, null,	null,			null),
			"resourceid",		array(T_RDA_INT, O_OPT, null,	DB_ID,			"isset({save})", isset(Nest.value(_REQUEST,"save").$()) ? getResourceNameByType(Nest.value(_REQUEST,"resourcetype").asInteger()) : null),
			"templateid",	array(T_RDA_INT, O_OPT, null,	DB_ID,			null),
			"width",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), null, _("Width")),
			"height",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65535), null, _("Height")),
			"colspan",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null, _("Column span")),
			"rowspan",		array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null, _("Row span")),
			"elements",		array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 100), null, _("Show lines")),
			"sort_triggers",	array(T_RDA_INT, O_OPT, null,	BETWEEN(SCREEN_SORT_TRIGGERS_DATE_DESC, SCREEN_SORT_TRIGGERS_RECIPIENT_DESC), null),
			"valign",			array(T_RDA_INT, O_OPT, null,	BETWEEN(VALIGN_MIDDLE, VALIGN_BOTTOM), null),
			"halign",			array(T_RDA_INT, O_OPT, null,	BETWEEN(HALIGN_CENTER, HALIGN_RIGHT), null),
			"style",				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 2),	"isset({save})"),
			"url",					array(T_RDA_STR, O_OPT, null,	null,			"isset({save})"),
			"dynamic",		array(T_RDA_INT, O_OPT, null,	null,			null),
			"x",					array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 100), "isset({save})&&(isset({form})&&({form}!=\"update\"))"),
			"y",					array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 100), "isset({save})&&(isset({form})&&({form}!=\"update\"))"),
			"screen_type",	array(T_RDA_INT, O_OPT, null,	null,			null),
			"tr_groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"tr_hostid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,			null),
			"application",	array(T_RDA_STR, O_OPT, null,	null,			null),
			// actions
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,		null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,			null),
			"form_refresh",	array(T_RDA_INT, O_OPT, null,	null,			null),
			"add_row",		array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null),
			"add_col",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null),
			"rmv_row",		array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null),
			"rmv_col",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null),
			"sw_pos",			array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 100), null),
			"ajaxAction",		array(T_RDA_STR, O_OPT, P_ACT,	null,			null)
		);
		check_fields(getIdentityBean(), fields);
		Nest.value(_REQUEST,"dynamic").$(get_request("dynamic", SCREEN_SIMPLE_ITEM));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		soptions = new CScreenGet();
		soptions.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
		soptions.setEditable(true);
		soptions.setOutput(API_OUTPUT_EXTEND);
		soptions.setSelectScreenItems(API_OUTPUT_EXTEND);
		CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);
		if (empty(screens)) {
			tsoptions = new CTemplateScreenGet();
			tsoptions.setScreenIds(Nest.value(_REQUEST,"screenid").asLong());
			tsoptions.setEditable(true);
			tsoptions.setOutput(API_OUTPUT_EXTEND);
			tsoptions.setSelectScreenItems(API_OUTPUT_EXTEND);
			screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
			if (empty(screens)) {
				access_deny();
			}
		}
		screen = reset(screens);
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (!empty(Nest.value(_REQUEST,"ajaxAction").$()) && "sw_pos".equals(Nest.value(_REQUEST,"ajaxAction").asString())) {
			CArray sw_pos = get_request("sw_pos", array());
			if (count(sw_pos) > 3) {
				Map params = new HashMap();
				params.put("y", sw_pos.get(0));
				params.put("x", sw_pos.get(1));
				params.put("screenid", Nest.value(screen,"screenid").$());
				Map fitem = DBfetch(DBselect(
					executor,
					"SELECT s.screenitemid,s.colspan,s.rowspan"+
					" FROM screens_items s"+
					" WHERE s.y=#{y}"+
						" AND s.x=#{x}"+
						" AND s.screenid=#{screenid}",
					params
				));

				params = new HashMap();
				params.put("y", sw_pos.get(2));
				params.put("x", sw_pos.get(3));
				Map sitem = DBfetch(DBselect(
					executor,
					"SELECT s.screenitemid,s.colspan,s.rowspan"+
					" FROM screens_items s"+
					" WHERE s.y=#{y}"+
						" AND s.x=#{x}"+
						" AND s.screenid=#{screenid}",
					params
				));

				if (!empty(fitem)) {
					params = new HashMap();
					params.put("pos0", sw_pos.get(0));
					params.put("pos1", sw_pos.get(1));
					params.put("pos2", sw_pos.get(2));
					params.put("pos3", sw_pos.get(3));
					params.put("colspan", (isset(sitem,"colspan") ? Nest.value(sitem,"colspan").$() : 1));
					params.put("rowspan", (isset(sitem,"rowspan") ? Nest.value(sitem,"rowspan").$() : 1));
					params.put("screenid", Nest.value(screen,"screenid").$());
					params.put("screenitemid", Nest.value(fitem,"screenitemid").$());
					DBexecute(executor,
							   "UPDATE screens_items"+
								" SET y=#{pos2},x=#{pos3}"+
								",colspan=#{colspan}"+
								",rowspan=#{rowspan}"+
								" WHERE y=#{pos0}"+
									" AND x=#{pos1}"+
									" AND screenid=#{screenid}"+
									" AND screenitemid=#{screenitemid}",
								params
					);
				}
				if (!empty(sitem)) {
					params = new HashMap();
					params.put("pos0", sw_pos.get(0));
					params.put("pos1", sw_pos.get(1));
					params.put("pos2", sw_pos.get(2));
					params.put("pos3", sw_pos.get(3));
					params.put("colspan", (isset(fitem,"colspan") ? Nest.value(fitem,"colspan").$() : 1));
					params.put("rowspan", (isset(fitem,"rowspan") ? Nest.value(fitem,"rowspan").$() : 1));
					params.put("screenid", Nest.value(screen,"screenid").$());
					params.put("screenitemid", Nest.value(sitem,"screenitemid").$());
					DBexecute(executor,
							    "UPDATE screens_items "+
								" SET y=#{pos0},x=#{pos1}"+
								",colspan=#{colspan}"+
								",rowspan=#{rowspan}"+
								" WHERE y=#{pos2}"+
									" AND x=#{pos3}"+
									" AND screenid=#{screenid}"+
									" AND screenitemid=#{screenitemid}",
								params
					);
				}
				add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Screen items switched");
			}
			echo("{\"result\": true}");
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"save")) {
			final Map screenItem = map(
				"screenid", get_request("screenid"),
				"resourceid", get_request("resourceid"),
				"resourcetype", get_request("resourcetype"),
				"caption", get_request("caption"),
				"style", get_request("style"),
				"url", get_request("url"),
				"width", get_request("width"),
				"height", get_request("height"),
				"halign", get_request("halign"),
				"valign", get_request("valign"),
				"colspan", get_request("colspan"),
				"rowspan", get_request("rowspan"),
				"dynamic", get_request("dynamic"),
				"elements", get_request("elements", 0),
				"sort_triggers", get_request("sort_triggers", SCREEN_SORT_TRIGGERS_DATE_DESC),
				"application", get_request("application", "")
			);

			DBstart(executor);
			boolean result;
			if (!empty(Nest.value(_REQUEST,"screenitemid").$())) {
				Nest.value(screenItem,"screenitemid").$(Nest.value(_REQUEST,"screenitemid").$());

				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.ScreenItem(getIdentityBean(), executor).update(array(screenItem)));
					}
				});
				
				show_messages(result, _("Item updated"), _("Cannot update item"));
			} else {
				Nest.value(screenItem,"x").$(get_request("x"));
				Nest.value(screenItem,"y").$(get_request("y"));

				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.ScreenItem(getIdentityBean(), executor).create(array(screenItem)));
					}
				});
				
				show_messages(result, _("Item added"), _("Cannot add item"));
			}
			DBend(executor, result);

			if (result) {
				add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Cell changed "+
					(isset(_REQUEST,"screenitemid") ? "screen itemid \""+Nest.value(_REQUEST,"screenitemid").asString()+"\"" : "")+
					(isset(_REQUEST,"x") && isset(Nest.value(_REQUEST,"y").$()) ? " coordinates \""+Nest.value(_REQUEST,"x").asString()+","+Nest.value(_REQUEST,"y").asString()+"\"" : "")+
					(isset(_REQUEST,"resourcetype") ? " resource type \""+Nest.value(_REQUEST,"resourcetype").asString()+"\"" : "")
				);
				unset(_REQUEST,"form");
			}
		} else if (isset(_REQUEST,"delete")) {
			DBstart(executor);
			CArray<Long[]> screenitemids = Call(new Wrapper<CArray<Long[]>>() {
				@Override
				protected CArray<Long[]> doCall() throws Throwable {
					return API.ScreenItem(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"screenitemid").asLong());
				}
			}, null);
			boolean result = !empty(screenitemids);
			DBend(executor, result);

			show_messages(result, _("Item deleted"), _("Cannot delete item"));
			if (result) {
				Long screenitemid = reset(screenitemids)[0];
				add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Screen itemid \""+screenitemid+"\"");
			}
			unset(_REQUEST,"x");
		} else if (isset(Nest.value(_REQUEST,"add_row").$())) {
			Map params =new HashMap();
			params.put("screenid", Nest.value(screen,"screenid").$());
			DBexecute(executor,"UPDATE screens SET vsize=(vsize+1) WHERE screenid=#{screenid}",params);

			Integer add_row = get_request("add_row", 0);
			if (Nest.value(screen,"vsize").asInteger() > add_row) {
				params.put("screenid", Nest.value(screen,"screenid").$());
				params.put("y", add_row);
				DBexecute(executor,"UPDATE screens_items SET y=(y+1) WHERE screenid=#{screenid} AND y>=#{y}",params);
			}
			add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Row added");
		} else if (isset(_REQUEST,"add_col")) {
			Map params =new HashMap();
			params.put("screenid", Nest.value(screen,"screenid").$());
			DBexecute(executor,"UPDATE screens SET hsize=(hsize+1) WHERE screenid=#{screenid}",params);

			Integer add_col = get_request("add_col", 0);
			if (Nest.value(screen,"hsize").asInteger() > add_col) {
				params.put("screenid", Nest.value(screen,"screenid").$());
				params.put("x", add_col);
				DBexecute(executor,"UPDATE screens_items SET x=(x+1) WHERE screenid=#{screenid} AND x>=#{x}",params);
			}
			add_audit_details(getIdentityBean(), executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Column added");
		} else if (isset(_REQUEST,"rmv_row")) {
			if (Nest.value(screen,"vsize").asInteger() > 1) {
				Integer rmv_row = get_request("rmv_row", 0);

				Map params =new HashMap();
				params.put("screenid", Nest.value(screen,"screenid").$());
				params.put("y", rmv_row);
				params.put("yrowspan", rmv_row);
				DBexecute(executor,"UPDATE screens SET vsize=(vsize-1) WHERE screenid=#{screenid}",params);
				DBexecute(executor,"DELETE FROM screens_items WHERE screenid=#{screenid} AND y=#{y}",params);
				DBexecute(executor,"UPDATE screens_items SET y=(y-1) WHERE screenid=#{screenid} AND y>#{y}",params);
				// reduce the rowspan of the items that are displayed in the removed row
				DBexecute(
					executor,
					"UPDATE screens_items"+
						" SET rowspan=(rowspan-1)"+
					" WHERE screenid=#{screenid}"+
						" AND y+rowspan>#{yrowspan}"+
						" AND y<#{y}",
					params
				);

				add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Row deleted");
			} else {
				error(_("Screen should contain at least one row and column."));
				show_error_message(_("Impossible to remove last row and column."));
			}
		} else if (isset(_REQUEST,"rmv_col")) {
			if (Nest.value(screen,"hsize").asInteger() > 1) {
				Integer rmv_col = get_request("rmv_col", 0);

				Map params =new HashMap();
				params.put("screenid", Nest.value(screen,"screenid").$());
				params.put("x", rmv_col);
				params.put("xcolspan", rmv_col);
				DBexecute(executor,"UPDATE screens SET hsize=(hsize-1) WHERE screenid=#{screenid}",params);
				DBexecute(executor,"DELETE FROM screens_items WHERE screenid=#{screenid} AND x=#{x}",params);
				DBexecute(executor,"UPDATE screens_items SET x=(x-1) WHERE screenid=#{screenid} AND x>#{x}",params);
				// reduce the colspan of the items that are displayed in the removed column
				DBexecute(
					executor,
					"UPDATE screens_items"+
						" SET colspan=(colspan-1)"+
					" WHERE screenid=#{screenid}"+
						" AND x+colspan>#{xcolspan}"+
						" AND x<#{x}",
					params
				);

				add_audit_details(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCREEN, Nest.value(screen,"screenid").asLong(), Nest.value(screen,"name").asString(), "Column deleted");
			} else {
				error(_("Screen should contain at least one row and column."));
				show_error_message(_("Impossible to remove last row and column."));
			}
		}

		/* Display */
		Map data = map(
			"screenid", get_request("screenid", 0)
		);

		// getting updated screen, so we wont have to refresh the page to see changes
		Nest.value(data,"screen").$(API.Screen(getIdentityBean(), executor).get(soptions));
		if (empty(Nest.value(data,"screen").$())) {
			Nest.value(data,"screen").$(API.TemplateScreen(getIdentityBean(), executor).get(tsoptions));
			if (empty(Nest.value(data,"screen").$())) {
				access_deny();
			}
		}
		Nest.value(data,"screen").$(reset(Nest.value(data,"screen").asCArray()));

		// render view
		CView screenView = new CView("configuration.screen.constructor.list", data);
		screenView.render(getIdentityBean(), executor);
		screenView.show();
	}
}
