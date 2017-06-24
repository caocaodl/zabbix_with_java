package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.MapsUtil;
import com.isoft.iradar.tags.CAreaMap;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CMonitoringMaps extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget mapWidget = new CWidget("hat_maps");
		CTable mapTable = new CTable(_("No maps found."), "map map-container");
		mapTable.setAttribute("style", "margin-top: 4px;");

		CPageFilter pageFilter = (CPageFilter)data.get("pageFilter");
		CIcon icon = null;
		CIcon fsIcon = null;

		if (!empty(Nest.value(data,"maps").$())) {
			CComboBox mapComboBox = new CComboBox("sysmapid", get_request("sysmapid", 0), "submit()");
			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"maps").asCArray()).entrySet()) {
			    Object sysmapId = e.getKey();
			    Map map = e.getValue();
				mapComboBox.addItem(sysmapId, Nest.value(map,"name").asString());
			}

			CForm headerMapForm = new CForm("get");
			headerMapForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());
			headerMapForm.addItem(array(_("Maps"), SPACE, mapComboBox));

			CForm headerSeverityMinForm = new CForm("get");
			headerSeverityMinForm.addVar("fullscreen", Nest.value(data,"fullscreen").$());
		//	headerSeverityMinForm.addItem(array(SPACE, _("Minimum severity"), SPACE, pageFilter.getSeveritiesMinCB()));

			mapWidget.addHeader(Nest.value(data,"map","name").$(), array(headerMapForm, headerSeverityMinForm));

			// get map parent maps
			CArray parentMaps = array();
//			for(Map parent : (CArray<Map>)MapsUtil.getParentMaps(Nest.value(data,"sysmapid").$())) {
//				// check for permissions
//				if (isset(Nest.value(data,"maps",parent.get("sysmapid")).$())) {
//					parentMaps.add(SPACE+SPACE);
//					parentMaps.add(new CLink(Nest.value(parent,"name").$(), "maps.action?sysmapid="+Nest.value(parent,"sysmapid").asString()+"&fullscreen="+Nest.value(data,"fullscreen").$()+"&severity_min="+Nest.value(data,"severity_min").$()));
//				}
//			}
			if (!empty(parentMaps)) {
				array_unshift(parentMaps, _("Upper level maps")+":");
				mapWidget.addHeader(parentMaps);
			}

			CAreaMap actionMap = MapsUtil.getActionMapBySysmap(Nest.value(data,"map").$(), map("severity_min" , Nest.value(data,"severity_min").$()));

			mapTable.addRow(actionMap);

			CImg imgMap = new CImg("map.action?sysmapid="+Nest.value(data,"sysmapid").$()+"&severity_min="+Nest.value(data,"severity_min").$());
			imgMap.setMap(actionMap.getName());
			mapTable.addRow(imgMap);

			icon = get_icon(idBean, executor, "favourite", map(
				"fav", "web.favorite.sysmapids",
				"elname", "sysmapid",
				"elid", Nest.value(data,"sysmapid").$()
			));
			fsIcon = get_icon(idBean, executor, "fullscreen", map("fullscreen", Nest.value(data,"fullscreen").$()));
		}

		mapWidget.addItem(mapTable);
		mapWidget.addPageHeader(_("NETWORK MAPS"), array(icon, SPACE, fsIcon));

		return mapWidget;
	}

}
