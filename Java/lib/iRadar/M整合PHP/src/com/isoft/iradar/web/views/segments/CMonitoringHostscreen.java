package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringHostscreen extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();
		screenWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor, "web.hostscreen.filter.state", 1)).asInteger());

		CForm form = new CForm("get");
		form.addVar("fullscreen", Nest.value(data,"fullscreen").$());
		screenWidget.addItem(form);

		if (empty(Nest.value(data,"screen").$()) || empty(Nest.value(data,"host").$())) {
			screenWidget.addPageHeader(_("SCREENS"));
			screenWidget.addItem(BR());
			screenWidget.addItem(new CTableInfo(_("No screens found.")));

			CScreenBuilder screenBuilder = new CScreenBuilder(idBean, executor);
			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline" , screenBuilder.timeline
			));
		} else {
			screenWidget.addPageHeader(_("SCREENS"), array(get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()))));
			screenWidget.addItem(BR());

			// host screen list
			if (!empty(Nest.value(data,"screens").$())) {
				CComboBox screenComboBox = new CComboBox(
					"screenList",
					"host_screen.action?hostid="+data.get("hostid")+"&screenid="+Nest.value(data,"screenid").$(),
					"javascript: redirect(this.options[this.selectedIndex].value);"
				);
				for(Map screen : (CArray<Map>)Nest.value(data,"screens").asCArray()) {
					screenComboBox.addItem("host_screen.action?hostid="+data.get("hostid")+"&screenid="+Nest.value(screen,"screenid").$(), Nest.value(screen,"name").asString());
				}

				screenWidget.addHeader(array(Nest.value(data,"screen","name").$(), SPACE, _("on"), SPACE, new CSpan(Nest.value(data,"host","name").$(), "parent-discovery")), screenComboBox);
			}

			// append screens to widget
			CScreenBuilder screenBuilder = new CScreenBuilder(idBean, executor, map(
				"screen", Nest.value(data,"screen").$(),
				"mode", SCREEN_MODE_PREVIEW,
				"hostid", Nest.value(data,"hostid").$(),
				"period", Nest.value(data,"period").$(),
				"stime", Nest.value(data,"stime").$(),
				"profileIdx", "web.screens",
				"profileIdx2", Nest.value(data,"screen","screenid").$()
			));
			screenWidget.addItem(screenBuilder.show(idBean));

			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline", screenBuilder.timeline,
				"profileIdx", screenBuilder.profileIdx
			));
		}

		return screenWidget;
	}

}
