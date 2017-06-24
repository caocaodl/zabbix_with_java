package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.add_doll_objects;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringSlides extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget slideWidget = new CWidget("hat_slides");

		// create header form
		CForm slideHeaderForm = new CForm("get");
		slideHeaderForm.setName("slideHeaderForm");

		CComboBox configComboBox = new CComboBox("config", "slides.action", "javascript: redirect(this.options[this.selectedIndex].value);");
		configComboBox.addItem("screens.action", _("Screens"));
		configComboBox.addItem("slides.action", _("Slide shows"));
		slideHeaderForm.addItem(configComboBox);

		if (empty(Nest.value(data,"slideshows").$())) {
			slideWidget.addPageHeader(
				_("SLIDE SHOWS"),
				array(
					slideHeaderForm,
					SPACE,
					get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()))
				)
			);
			slideWidget.addItem(BR());
			slideWidget.addItem(new CTableInfo(_("No slide shows found.")));
		} else {
			CIcon favouriteIcon = !empty(data.get("screen"))
				? get_icon(idBean, executor, "favourite", map("fav", "web.favorite.screenids", "elname", "slideshowid", "elid", Nest.value(data,"elementid").$()))
				: new CIcon(_("Favourites"), "iconplus");

			CIcon refreshIcon = new CIcon(_("Menu"), "iconmenu");
			if (!empty(Nest.value(data,"screen").$())) {
				refreshIcon.addAction("onclick", "javascript: create_page_menu(event, \"hat_slides\");");
			}

			slideWidget.addPageHeader(
				_("SLIDE SHOWS"),
				array(
					slideHeaderForm,
					SPACE,
					favouriteIcon,
					SPACE,
					refreshIcon,
					SPACE,
					get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()))
				)
			);

			CForm slideForm = new CForm("get");
			slideForm.setName("slideForm");
			slideForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());

			CComboBox elementsComboBox = new CComboBox("elementid", Nest.value(data,"elementid").$(), "submit()");
			for(Map slideshow : (CArray<Map>)Nest.value(data,"slideshows").asCArray()) {
				elementsComboBox.addItem(Nest.value(slideshow,"slideshowid").$(), Nest.value(slideshow,"name").asString());
			}
			slideForm.addItem(array(_("Slide show")+SPACE, elementsComboBox));

			slideWidget.addHeader(Nest.value(data,"slideshows",data.get("elementid"),"name").$(), slideForm);

			if (!empty(Nest.value(data,"screen").$())) {
				if (isset(Nest.value(data,"isDynamicItems").$())) {
					CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
					slideForm.addItem(array(SPACE, _("Group"), SPACE, pageFilter.getGroupsCB()));
					slideForm.addItem(array(SPACE, _("Host"), SPACE, pageFilter.getHostsCB()));
				}

				CDiv scrollDiv = new CDiv();
				scrollDiv.setAttribute("id", "scrollbar_cntr");
				slideWidget.addFlicker(scrollDiv, Nest.as(CProfile.get(idBean, executor,"web.slides.filter.state", 1)).asInteger());
				slideWidget.addFlicker(BR(), Nest.as(CProfile.get(idBean, executor,"web.slides.filter.state", 1)).asInteger());

				// js menu
				insert_js("var page_menu="+rda_jsvalue(Nest.value(data,"menu").$())+";\n"+"var page_submenu="+rda_jsvalue(Nest.value(data,"submenu").$())+";\n");

				add_doll_objects((CArray)array(map(
					"id", "hat_slides",
					"frequency", Nest.value(data,"element","delay").asLong() * Nest.value(data,"refresh_multiplier").asLong(),
					"url", "slides.action?elementid="+Nest.value(data,"elementid").asString()+url_param(idBean, "groupid")+url_param(idBean, "hostid"),
					"params", map("lastupdate", time())
				)));

				slideWidget.addItem(new CSpan(_("Loading..."), "textcolorstyles"));
			} else {
				slideWidget.addItem(new CTableInfo(_("No slides found.")));
			}
		}

		return slideWidget;
	}

}
