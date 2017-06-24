package com.isoft.iradar.web.views;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.IRADAR_COPYRIGHT_FROM;
import static com.isoft.iradar.inc.Defines.IRADAR_COPYRIGHT_TO;
import static com.isoft.iradar.inc.Defines.IRADAR_HOMEPAGE;
import static com.isoft.iradar.inc.Defines.IRADAR_VERSION;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.SCREEN_REFRESH_RESPONSIVENESS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.ProfilesUtil.add_user_history;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.g;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CViewPageFooter extends CView {
	
	private CViewPageFooter() {
	}
	
	public static void renderAndShow(IIdentityBean idBean){
		renderAndShow(idBean, null);
	}
	
	public static void renderAndShow(IIdentityBean idBean, SQLExecutor executor){
		RadarContext ctx = RadarContext.getContext();
		if (!defined("PAGE_HEADER_LOADED")) {
			define("PAGE_HEADER_LOADED", 1);
		}

		if(executor != null) {
			Map page = RadarContext.page();
			
			// history
			if (isset(page,"hist_arg") && !RDA_GUEST_USER.equals(CWebUser.get("alias")) && Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML && !defined("RDA_PAGE_NO_MENU")) {
				add_user_history(idBean, executor, page);
			}

			// last page
			if (!defined("RDA_PAGE_NO_MENU") && !"profile.action".equals(Nest.value(page,"file").$())) {
				CProfile.update(idBean, executor, "web.paging.lastpage", Nest.value(page,"file").asString(), PROFILE_TYPE_STR);
			}

			CProfile.flush(idBean, executor);

			// end transactions if they have not been closed already
//			if (isset($DB) && isset(Nest.value($DB,"TRANSACTIONS").$()) && Nest.value($DB,"TRANSACTIONS").$() != 0) {
//				error(_("Transaction has not been closed. Aborting..."));
//				DBend(false);
//			}
		}
		
//		show_messages();
		
		int type = (Integer) ctx.getPage().get("type");
		if (type == PAGE_TYPE_HTML) {
			StringBuilder post_script = new StringBuilder();
			post_script.append("var page_refresh = null;").append('\n');
			post_script.append("jQuery(function() {").append('\n');
			
			CArray<String> rda_page_post_js = g.RDA_PAGE_POST_JS.$();
			if (isset(rda_page_post_js)) {
				for (String script : rda_page_post_js) {
					post_script.append(script).append('\n');
				}
			}
			
			if(isset(ctx.getPage(), "scripts") && inArray("flickerfreescreen.js", (String[])ctx.getPage().get("scripts"))){
				post_script.append("window.flickerfreeScreenShadow.timeout =")
				.append("5")
				.append(" * 1000;").append('\n');
				post_script.append("window.flickerfreeScreenShadow.responsiveness =")
						.append(SCREEN_REFRESH_RESPONSIVENESS)
						.append(" * 1000;").append('\n');
			}
			
			post_script.append("cookie.init();").append('\n');
			post_script.append("chkbxRange.init();").append('\n');
			
			post_script.append("});").append('\n');
			
			if(!defined("RDA_PAGE_NO_MENU") && !defined("RDA_PAGE_NO_FOOTER")){
				CTable table = new CTable(null, "textwhite bold maxwidth ui-widget-header ui-corner-all page_footer");
				
				String conString = null;
				if (Nest.as(CWebUser.get("userid")).asInteger() == 0) {
					conString  = _("Not connected");
				} else {
					conString = _s("Connected as '%1$s'", CWebUser.get("alias"));
				}
				
				table.addRow(new CCol[]{
						new CCol(
								new CLink(
										_s("iRadar %1$s Copyright %2$s-%3$s by iRadar SIA",
												IRADAR_VERSION,
												IRADAR_COPYRIGHT_FROM, 
												IRADAR_COPYRIGHT_TO),
										IRADAR_HOMEPAGE,
										"highlight", 
										null, 
										true), 
								"center"
						),
						new CCol(
								new CSpan[]{
										new CSpan(SPACE+SPACE+"|"+SPACE+SPACE, "divider"),
										new CSpan(conString, "footer_sign")
								}, 
								"right"
						)
				});
				table.show();
			}
			
			insert_js(post_script.toString());
			
			if(defined("RDA_PAGE_NO_MENU")) {
				ctx.write("</div>"); //content_body
				ctx.write("</div>"); //main_content
				ctx.write("</div>"); //container
			}
			
			ctx.write("</body>");
			ctx.write("</html>");
		}
	}
}
