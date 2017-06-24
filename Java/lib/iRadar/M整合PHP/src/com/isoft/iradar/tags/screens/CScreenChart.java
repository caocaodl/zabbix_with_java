package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_JS;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.GraphsUtil.getGraphDims;
import static com.isoft.iradar.inc.GraphsUtil.get_min_itemclock_by_graphid;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CDiv;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenChart extends CScreenBase {
	
	/**
	 * Graph id
	 * @var int
	 */
	public Integer graphid;
	
	public CScreenChart(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	/**
	 * Init screen data.
	 * @param array	options
	 * @param int		options['graphid']
	 */
	public CScreenChart(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
		this.graphid = isset(options,"graphid") ? Nest.value(options,"graphid").asInteger() : null;
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public Object get() {
		dataId = "graph_full";
		String containerId = "graph_container";

		int loadSBox;
		String src;
		
		// time control
		Map graphDims = getGraphDims(this.idBean, executor, graphid);
		if (Nest.value(graphDims,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(graphDims,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED) {
			loadSBox = 0;
			src = "chart6.action";
		} else {
			loadSBox = 1;
			src = "chart2.action";
		}
		src += "?graphid="+graphid+"&period="+timeline.get("period")+"&stime="+timeline.get("stimeNow")+getProfileUrlParams();

		Nest.value(timeline,"starttime").$(date(TIMESTAMP_FORMAT, get_min_itemclock_by_graphid(this.idBean, executor, graphid)));

		Map timeControlData = map(
			"id", getDataId(),
			"containerid", containerId,
			"src", src,
			"objDims", graphDims,
			"loadSBox", loadSBox,
			"loadImage", 1,
			"dynamic", 1,
			"periodFixed", CProfile.get(idBean, executor, profileIdx+".timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);

		// output
		if (this.mode == SCREEN_MODE_JS) {
			Nest.value(timeControlData,"dynamic").$(0);
			Nest.value(timeControlData,"loadSBox").$(0);
			return "timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+")";
		} else {
			if (this.mode == SCREEN_MODE_SLIDESHOW) {
				insert_js("timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");");
			}else {
				rda_add_post_js("timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");");
			}

			return getOutput(new CDiv(null, "center", containerId), true, map("graphid", graphid));
		}
	}
}
