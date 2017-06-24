package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.ScreensUtil.check_dynamic_items;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringScreen extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();
		screenWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor, "web.screens.filter.state", 1)).asInteger());

		// header form
		CForm headerForm = new CForm();
		if (empty(Nest.value(data,"screens").$())) {
			screenWidget.addPageHeader(_("SCREENS"), headerForm);
			screenWidget.addItem(BR());
			screenWidget.addItem(new CTableInfo(_("No screens found.")));

			CScreenBuilder screenBuilder = new CScreenBuilder(idBean, executor);
			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline", screenBuilder.timeline
			));
		} else {
			Map screen = null;
			if (!isset(Nest.value(data,"screens",data.get("elementIdentifier")).$())) {
				// this means id was fetched from profile and this screen does not exist
				// in this case we need to show the first one
				screen  = reset((CArray<Map>)Nest.value(data,"screens").asCArray());
			} else {
				screen = Nest.value(data,"screens",data.get("elementIdentifier")).asCArray();
			}

			// if elementid is used to fetch an element, saving it in profile
			if (empty(Nest.value(data,"use_screen_name").$())) {
				CProfile.update(idBean, executor, "web.screens.elementid", Nest.value(screen,"screenid").$() , PROFILE_TYPE_ID);
			}

			// append screens combobox to page header
			headerForm = new CForm("get");
			headerForm.setName("headerForm");
			headerForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());

			CComboBox elementsComboBox = new CComboBox("elementid", Nest.value(screen,"screenid").$(), "submit()");
			for(Map dbScreen : (CArray<Map>)Nest.value(data,"screens").asCArray()) {
				elementsComboBox.addItem(Nest.value(dbScreen,"screenid").$(), htmlspecialchars(Nest.value(dbScreen,"name").asString()));
			}
			headerForm.addItem(array(_("Screens")+SPACE, elementsComboBox));

			if (!empty(check_dynamic_items(idBean, executor, Nest.value(screen,"screenid").asLong(), 0))) {
				CPageFilter pageFilter = new CPageFilter(idBean, executor, map(
					"groups", map(
						"monitored_hosts", true,
						"with_items", true
					),
					"hosts", map(
						"monitored_hosts", true,
						"with_items", true,
						"DDFirstLabel", _("Default")
					),
					"hostid", get_request("hostid", null),
					"groupid", get_request("groupid", null)
				));
				Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());
				Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").$());

				headerForm.addItem(array(SPACE, _("Group"), SPACE, pageFilter.getGroupsCB()));
				headerForm.addItem(array(SPACE, _("Host"), SPACE, pageFilter.getHostsCB()));
			}

			screenWidget.addHeader(_("SCREENS")+"： "+Nest.value(screen,"name").$(), headerForm);

			// append screens to widget
			CScreenBuilder screenBuilder = new CScreenBuilder(idBean, executor, map(
				"screenid", Nest.value(screen,"screenid").$(),
				"mode", SCREEN_MODE_PREVIEW,
				"profileIdx", "web.screens",
				"profileIdx2", Nest.value(screen,"screenid").$(),
				"groupid", get_request("groupid"),
				"hostid", get_request("hostid"),
				"period", Nest.value(data,"period").$(),
				"stime", Nest.value(data,"stime").$()
			));
			screenWidget.addItem(screenBuilder.show(idBean));

			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline", screenBuilder.timeline,
				"profileIdx", screenBuilder.profileIdx
			));

			screenWidget.addItem(BR());
		}

		return screenWidget;
	}

}
