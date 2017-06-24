package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.SCREEN_DYNAMIC_ITEM;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_JS;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.GraphsUtil.getGraphDims;
import static com.isoft.iradar.inc.ItemsUtil.get_same_item_for_host;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTag;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenSimpleGraph extends CScreenBase {

	public CScreenSimpleGraph(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenSimpleGraph(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public Object get() {
		dataId = "graph_"+Nest.value(screenitem,"screenitemid").$()+"_"+Nest.value(screenitem,"screenid").$();
		Long resourceid = !empty(Nest.value(screenitem,"real_resourceid").$()) ? Nest.value(screenitem,"real_resourceid").asLong() : Nest.value(screenitem,"resourceid").asLong();
		String containerid = "graph_container_"+Nest.value(screenitem,"screenitemid").asString()+"_"+Nest.value(screenitem,"screenid").asString();
		Map graphDims = getGraphDims(this.idBean, executor);
		Nest.value(graphDims,"graphHeight").$(Nest.value(screenitem,"height").$());
		Nest.value(graphDims,"width").$(Nest.value(screenitem,"width").$());

		// get time control
		Map timeControlData = map(
			"id", getDataId(),
			"containerid", containerid,
			"objDims", graphDims,
			"loadImage", 1,
			"periodFixed", CProfile.get(this.idBean, this.executor, "web.screens.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);

		// host feature
		if (Nest.value(screenitem,"dynamic").asInteger() == SCREEN_DYNAMIC_ITEM && !empty(hostid)) {
			Long newitemid = get_same_item_for_host(executor, resourceid, hostid);
			resourceid = !empty(newitemid) ? newitemid : null;
		}

		if (this.mode == SCREEN_MODE_PREVIEW && !empty(resourceid)) {
			this.action = "history.action?action=showgraph&itemid="+resourceid+"&period="+Nest.value(timeline,"period").$()+
					"&stime="+Nest.value(timeline,"stimeNow").$()+getProfileUrlParams();
		}

		if (!rda_empty(resourceid) && mode != SCREEN_MODE_EDIT) {
			if (this.mode == SCREEN_MODE_PREVIEW) {
				Nest.value(timeControlData,"loadSBox").$(1);
			}
		}

		Nest.value(timeControlData,"src").$(rda_empty(resourceid)
			? "chart3.action?"
			: "chart.action?itemid="+resourceid+"&"+Nest.value(screenitem,"url").$()+"&width="+Nest.value(screenitem,"width").$()+"&height="+Nest.value(screenitem,"height").$());

		Nest.value(timeControlData,"src").$(Nest.value(timeControlData,"src").asString()+((this.mode == SCREEN_MODE_EDIT)
			? "&period=3600&stime="+date(TIMESTAMP_FORMAT, time())
			: "&period="+Nest.value(timeline,"period").$()+"&stime="+Nest.value(timeline,"stimeNow").$()));

		Nest.value(timeControlData,"src").$(Nest.value(timeControlData,"src").asString()+getProfileUrlParams());

		// output
		if (this.mode == SCREEN_MODE_JS) {
			return "timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+")";
		} else {
			if (this.mode == SCREEN_MODE_SLIDESHOW) {
				insert_js("timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");");
			} else {
				rda_add_post_js("timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");");
			}

			CTag item = null;
			if (mode == SCREEN_MODE_EDIT || mode == SCREEN_MODE_SLIDESHOW) {
				item = new CDiv();
			} else if (mode == SCREEN_MODE_PREVIEW) {
				item = new CLink(null, "history.action?action=showgraph&itemid="+resourceid+"&period="+Nest.value(timeline,"period").$()+
						"&stime="+Nest.value(timeline,"stimeNow").$());
			}
			item.setAttribute("id", containerid);

			return getOutput(item);
		}
	}

}
