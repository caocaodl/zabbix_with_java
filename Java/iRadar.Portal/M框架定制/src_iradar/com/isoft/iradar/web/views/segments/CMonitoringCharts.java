package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_CHART;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CMonitoringCharts extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CPageFilter pageFilter= (CPageFilter)data.get("pageFilter");
		CWidget chartsWidget = new CWidget("hat_charts");
		CForm chartForm = new CForm("get");
		chartForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());
		chartForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		chartForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));
		chartForm.addItem(array(SPACE+_("Graph")+SPACE, pageFilter.getGraphsCB()));

		chartsWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(idBean, executor, "web.charts.filter.state", 1)).asInteger());

		if (!empty(Nest.value(data,"graphid").$())) {
			chartsWidget.addPageHeader(_("GRAPHS"), array(
				get_icon(idBean, executor, "favourite", map("fav" , "web.favorite.graphids", "elname" , "graphid", "elid" , Nest.value(data,"graphid").$())),
				SPACE,
				get_icon(idBean, executor, "reset", map("id", Nest.value(data,"graphid").$())),
				SPACE,
				get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()))
			));
		} else {
			chartsWidget.addPageHeader(_("GRAPHS"), array(get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()))));
		}
		
		chartsWidget.addHeader(
			isset(Nest.value(pageFilter.$("graphs").asCArray(), data.get("graphid"), "name").$())
				? Nest.value(pageFilter.$("graphs").asCArray(), data.get("graphid"), "name").$()
				: null,
			chartForm
		);
		chartsWidget.addItem(BR());

		if (!empty(Nest.value(data,"graphid").$())) {
			// append chart to widget
			CScreenBase screen = CScreenBuilder.getScreen(idBean, executor, map(
				"resourcetype" , SCREEN_RESOURCE_CHART,
				"graphid" , Nest.value(data,"graphid").$(),
				"profileIdx" , "web.screens",
				"profileIdx2" , data.get("graphid")
			));

			CTable chartTable = new CTable(null, "maxwidth");
			chartTable.addRow(screen.get());

			chartsWidget.addItem(chartTable);

			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline", screen.timeline,
				"profileIdx", screen.profileIdx
			));
		} else {
			CScreenBuilder screen = new CScreenBuilder(idBean, executor);
			CScreenBuilder.insertScreenStandardJs(idBean, executor, map(
				"timeline" , screen.timeline
			));

			chartsWidget.addItem(new CTableInfo(_("No graphs found.")));
		}

		return chartsWidget;
	}

}
