package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.MapsUtil.getActionMapBySysmap;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.tags.CAreaMap;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenMap extends CScreenBase {

	public CScreenMap(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenMap(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public CDiv get() {
		CImg image = new CImg("map.action?noedit=1&sysmapid="+Nest.value(screenitem,"resourceid").asString()+"&width="+Nest.value(screenitem,"width").asString()
			+"&height="+Nest.value(screenitem,"height").asString()+"&curtime="+time());
		image.setAttribute("id", "map_"+Nest.value(screenitem,"screenitemid").asString());

		Map output = null;
		if (this.mode == SCREEN_MODE_PREVIEW) {
			CMapGet moptions = new CMapGet();
			moptions.setSysmapIds(Nest.value(screenitem,"resourceid").asLong());
			moptions.setOutput(API_OUTPUT_EXTEND);
			moptions.setSelectSelements(API_OUTPUT_EXTEND);
			moptions.setSelectLinks(API_OUTPUT_EXTEND);
			moptions.setExpandUrls(true);
			moptions.setNopermissions(true);
			moptions.setPreserveKeys(true);
			CArray<Map> sysmaps = API.Map(this.idBean, this.executor).get(moptions);
			Map sysmap = reset(sysmaps);

			image.setSrc(image.getAttribute("src")+"&severity_min="+Nest.value(sysmap,"severity_min").asString());

			CAreaMap actionMap = getActionMapBySysmap(sysmap, map("severity_min", Nest.value(sysmap,"severity_min").$()));
			image.setMap(actionMap.getName());

			output = array(actionMap, image);
		} else if (this.mode == SCREEN_MODE_EDIT) {
			CImg setting = new CImg("icons/setting.ico");
			output = array(image, BR(), new CLink(setting, action));
		} else {
			output = array(image);
		}

		insertFlickerfreeJs();

		CDiv div = new CDiv(output, "map-container flickerfreescreen", getScreenId());
		div.setAttribute("data-timestamp", timestamp);
		div.addStyle("position: relative;");

		return div;
	}

}
