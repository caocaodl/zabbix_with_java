package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationScreenConstructorList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget screenWidget = new CWidget();
		screenWidget.addHeader(_("CONFIGURATION OF SCREENS") + "： " +Nest.value(data,"screen","name").$());
		/*if (!empty(Nest.value(data,"screen","templateid").$())) {
			screenWidget.addItem(get_header_host_table("screens", Nest.value(data,"screen","templateid").asString()));
		}*/
		screenWidget.addItem(BR());

		CScreenBuilder screenBuilder = new CScreenBuilder(idBean, executor, (CArray)map(
			"isFlickerfree", false,
			"screen", Nest.value(data,"screen").$(),
			"mode", SCREEN_MODE_EDIT,
			"updateProfile", false
		));
		screenWidget.addItem(screenBuilder.show(idBean));

		CScreenBuilder.insertInitScreenJs(Nest.value(data,"screenid").asString());
		CScreenBuilder.insertProcessObjectsJs();

		return screenWidget;
	}

}
