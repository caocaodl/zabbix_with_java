package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_ITEM_DELAY_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.natksort;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ScreensUtil.add_slideshow;
import static com.isoft.iradar.inc.ScreensUtil.delete_slideshow;
import static com.isoft.iradar.inc.ScreensUtil.get_slideshow_by_slideshowid;
import static com.isoft.iradar.inc.ScreensUtil.slideshow_accessible;
import static com.isoft.iradar.inc.ScreensUtil.update_slideshow;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class SlideconfAction extends RadarBaseAction {
	
	private Map dbSlideshow;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of slide shows"));
		page("file", "slideconf.action");
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("hist_arg", new String[] {});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"shows" ,				array(T_RDA_INT, O_OPT,	P_SYS,		DB_ID,	null),
			"slideshowid" ,		array(T_RDA_INT, O_NO,	P_SYS,		DB_ID,	"(isset({form})&&({form}==\"update\"))"),
			"name" , 				array(T_RDA_STR, O_OPT, null, NOT_EMPTY, "isset({save})", _("Name")),
			"delay" , 				array(T_RDA_INT, O_OPT, null, BETWEEN(1, SEC_PER_DAY), "isset({save})",_("Default delay (in seconds)")),
			"slides" ,				array(null,		 O_OPT, null,		null,	null),
			// actions
			"go" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,				array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form" ,				array(T_RDA_STR, O_OPT, P_SYS,		null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,		null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		
		if (!empty(Nest.value(_REQUEST,"slides").$())) {
			natksort(Nest.value(_REQUEST,"slides").asCArray());
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/** Permissions */
		if (isset(_REQUEST,"slideshowid")) {
			if (!slideshow_accessible(this.getIdentityBean(), executor,Nest.value(_REQUEST,"slideshowid").asLong(), PERM_READ_WRITE)) {
				access_deny();
			}

			dbSlideshow = get_slideshow_by_slideshowid(getIdentityBean(), executor,Nest.as(get_request("slideshowid")).asLong());

			if (empty(dbSlideshow)) {
				access_deny();
			}
		}
		if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"shows") || !isArray(Nest.value(_REQUEST,"shows").$())) {
				access_deny();
			} else {
				SqlBuilder sqlParts = new SqlBuilder();
				String sql = "SELECT COUNT(*) AS cnt FROM slideshows s WHERE "+sqlParts.dual.dbConditionInt("s.slideshowid", Nest.array(_REQUEST,"shows").asLong());
				Map dbSlideshowCount = DBfetch(DBselect(executor,sql,sqlParts.getNamedParams()));
				if (Nest.value(dbSlideshowCount,"cnt").asInteger() != count(Nest.value(_REQUEST,"shows").$())) {
					access_deny();
				}
			}
		}

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		boolean result;
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"slideshowid")) {
			unset(_REQUEST,"slideshowid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			int audit_action;
			if (isset(_REQUEST,"slideshowid")) {
				DBstart(executor);
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return update_slideshow(getIdentityBean(), executor, Nest.value(_REQUEST,"slideshowid").asLong(), Nest.value(_REQUEST,"name").asString(), Nest.value(_REQUEST,"delay").asLong(), get_request("slides", array()));
					}
				});
				result = DBend(executor, result);
				
				audit_action = AUDIT_ACTION_UPDATE;
				show_messages(result, _("Slide show updated"), _("Cannot update slide show"));
			} else {
				DBstart(executor);
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return add_slideshow(getIdentityBean(), executor, Nest.value(_REQUEST,"name").asString(), Nest.value(_REQUEST,"delay").asLong(), get_request("slides", array())) != null;
					}
				});
				result = DBend(executor, result);
				
				audit_action = AUDIT_ACTION_ADD;
				show_messages(result, _("Slide show added"), _("Cannot add slide show"));
			}

			if (result) {
				add_audit(getIdentityBean(), executor, audit_action, AUDIT_RESOURCE_SLIDESHOW, " Name \""+Nest.value(_REQUEST,"name").asString()+"\" ");
				unset(_REQUEST,"form");
				unset(_REQUEST,"slideshowid");
				clearCookies(result);
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"slideshowid")) {
			DBstart(executor);
			result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return delete_slideshow(getIdentityBean(), executor,Nest.value(_REQUEST,"slideshowid").asLong());
				}
			});
			result = DBend(executor);

			show_messages(result, _("Slide show deleted"), _("Cannot delete slide show"));
			if (result) {
				add_audit(getIdentityBean(), executor, AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SLIDESHOW, " Name \""+Nest.value(dbSlideshow,"name").asString()+"\" ");
			}

			unset(_REQUEST,"form");
			unset(_REQUEST,"slideshowid");
			clearCookies(result);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			boolean goResult = true;
			CArray<String> shows = get_request("shows", array());
			DBstart(executor);

			for(final String showid : shows) {
				goResult &= Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return delete_slideshow(getIdentityBean(), executor, Nest.as(showid).asLong());
					}
				});
				if (!goResult) {
					break;
				}
			}
			
			goResult = DBend(executor, goResult);

			if (goResult) {
				unset(_REQUEST,"form");
			}

			show_messages(goResult, _("Slide show deleted"), _("Cannot delete slide show"));
			clearCookies(goResult);
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"form", get_request("form", null),
				"form_refresh", get_request("form_refresh", null),
				"slideshowid", get_request("slideshowid", null),
				"name", get_request("name", ""),
				"delay", get_request("delay", RDA_ITEM_DELAY_DEFAULT),
				"slides", get_request("slides", array())
			);

			if (isset(data,"slideshowid") && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"name").$(Nest.value(dbSlideshow,"name").$());
				Nest.value(data,"delay").$(Nest.value(dbSlideshow,"delay").$());

				// get slides
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> db_slides = DBselect(executor,
					 "SELECT s.* FROM slides s WHERE s.slideshowid="+sqlParts.marshalParam(Nest.value(data,"slideshowid").asString())+" ORDER BY s.step",
					 sqlParts.getNamedParams()
				 );
				for(Map slide : db_slides) {
					Nest.value(data, "slides",slide.get("step")).$(map(
						"slideid", Nest.value(slide,"slideid").$(),
						"screenid", Nest.value(slide,"screenid").$(),
						"delay", Nest.value(slide,"delay").$()
					));
				}
			}

			// get slides without delay
			Nest.value(data,"slides_without_delay").$(Clone.deepcopy(Nest.value(data,"slides").$()));
			for (int i = 0, size = count(Nest.value(data,"slides_without_delay").$()); i < size; i++) {
				unset((Map)Nest.value(data,"slides_without_delay",i).$(),"delay");
			}

			// render view
			CView slideshowView = new CView("configuration.slideconf.edit", data);
			slideshowView.render(getIdentityBean(), executor);
			slideshowView.show();
		} else {
			CArray data = array();
			
			CArray<Map> slides = DBselect(executor,
					"SELECT s.slideshowid,s.name,s.delay,COUNT(sl.slideshowid) AS cnt"+
					" FROM slideshows s"+
						" LEFT JOIN slides sl ON sl.slideshowid=s.slideshowid"+
					" GROUP BY s.slideshowid,s.name,s.delay"
			);
			 
		   Nest.value(data,"slides").$(slides);

		   for (Entry<Object, Map> e : slides.entrySet()) {
			    Object key = e.getKey();
			    Map slide = e.getValue();
				if (!slideshow_accessible(this.getIdentityBean(), executor, Nest.value(slide,"slideshowid").asLong(), PERM_READ_WRITE)) {
					unset(slides,key);
				}
			}

			order_result(slides, getPageSortField(getIdentityBean(), executor, "name"), getPageSortOrder(getIdentityBean(), executor));

			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, slides, array("slideshowid")));

			// render view
			CView slideshowView = new CView("configuration.slideconf.list", data);
			slideshowView.render(getIdentityBean(), executor);
			slideshowView.show();
		}
	}

}
