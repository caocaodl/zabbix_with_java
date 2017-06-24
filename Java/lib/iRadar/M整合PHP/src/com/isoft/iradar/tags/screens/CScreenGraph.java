package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_ITEM_VALUE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SCREEN_DYNAMIC_ITEM;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_EDIT;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_JS;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_SLIDESHOW;
import static com.isoft.iradar.inc.Defines.SCREEN_SIMPLE_ITEM;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.GraphsUtil.getGraphByGraphId;
import static com.isoft.iradar.inc.GraphsUtil.getGraphDims;
import static com.isoft.iradar.inc.GraphsUtil.getSameGraphItemsForHost;
import static com.isoft.iradar.inc.GraphsUtil.get_min_itemclock_by_graphid;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.utils.CJs.encodeJson;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.Curl;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenGraph extends CScreenBase {

	public CScreenGraph(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	public CScreenGraph(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
	}

	/**
	 * Process screen.
	 * @return CDiv (screen inside container)
	 */
	@Override
	public Object get() {
		dataId = "graph_"+Nest.value(screenitem,"screenitemid").$()+"_"+Nest.value(screenitem,"screenid").$();
		long resourceId = isset(Nest.value(screenitem,"real_resourceid").$())
			? Nest.value(screenitem,"real_resourceid").asLong()
			: Nest.value(screenitem,"resourceid").asLong();
		String containerId = "graph_container_"+Nest.value(screenitem,"screenitemid").$()+"_"+Nest.value(screenitem,"screenid").$();
		Map graphDims = getGraphDims(this.idBean, executor,resourceId);
		Nest.value(graphDims,"graphHeight").$(Nest.value(screenitem,"height").$());
		Nest.value(graphDims,"width").$(Nest.value(screenitem,"width").$());
		Map graph = getGraphByGraphId(this.idBean, executor,resourceId);
		long graphId = Nest.value(graph,"graphid").asLong();
		Object legend = Nest.value(graph,"show_legend").$();
		Object graph3d = Nest.value(graph,"show_3d").$();

		if (Nest.value(screenitem,"dynamic").asInteger() == SCREEN_DYNAMIC_ITEM && !empty(hostid)) {
			// get host
			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hostid);
			hoptions.setOutput(new String[]{"hostid", "name"});
			CArray<Map> hosts = API.Host(this.idBean, executor).get(hoptions);
			Map host = reset(hosts);

			// get graph
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(resourceId);
			goptions.setOutput(API_OUTPUT_EXTEND);
			goptions.setSelectHosts(API_OUTPUT_REFER);
			goptions.setSelectGraphItems(API_OUTPUT_EXTEND);
			CArray<Map> graphs = API.Graph(this.idBean, executor).get(goptions);
			graph = reset(graphs);

			// if items from one host we change them, or set calculated if not exist on that host
			if (count(Nest.value(graph,"hosts").$()) == 1) {
				if (Nest.value(graph,"ymax_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE && !empty(Nest.value(graph,"ymax_itemid").$())) {
					CArray<Map> newDynamics = getSameGraphItemsForHost(this.idBean, executor,
						array((Map)map("itemid", Nest.value(graph,"ymax_itemid").$())),
						Nest.as(hostid).asLong(),
						false
					);
					Map newDynamic = reset(newDynamics);

					if (isset(newDynamic,"itemid") && Nest.value(newDynamic,"itemid").asLong() > 0) {
						Nest.value(graph,"ymax_itemid").$(Nest.value(newDynamic,"itemid").$());
					} else {
						Nest.value(graph,"ymax_type").$(GRAPH_YAXIS_TYPE_CALCULATED);
					}
				}

				if (Nest.value(graph,"ymin_type").asInteger() == GRAPH_YAXIS_TYPE_ITEM_VALUE && !empty(Nest.value(graph,"ymin_itemid").$())) {
					CArray<Map> newDynamics = getSameGraphItemsForHost(this.idBean, executor,
						array((Map)array("itemid", Nest.value(graph,"ymin_itemid").$())),
						hostid,
						false
					);
					Map newDynamic = reset(newDynamics);

					if (isset(newDynamic,"itemid") && Nest.value(newDynamic,"itemid").asLong() > 0) {
						Nest.value(graph,"ymin_itemid").$(Nest.value(newDynamic,"itemid").$());
					} else {
						Nest.value(graph,"ymin_type").$(GRAPH_YAXIS_TYPE_CALCULATED);
					}
				}
			}

			// get url
			Nest.value(screenitem,"url").$((Nest.value(graph,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(graph,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED)
				? "chart7.action"
				: "chart3.action");
			Nest.value(screenitem,"url").$(new Curl(Nest.value(screenitem,"url").asString()));

			for (Entry<String, Object> e : ((Map<String, Object>)graph).entrySet()) {
				String name = e.getKey();
			    Object value = e.getValue();
				if ("width".equals(name) || "height".equals(name)) {
					continue;
				}
				((Curl)screenitem.get("url")).setArgument(name, value);
			}

			CArray<Map> newGraphItems = getSameGraphItemsForHost(this.idBean, executor,Nest.value(graph,"gitems").asCArray(), hostid, false);
			for(Map newGraphItem : newGraphItems) {
				unset(newGraphItem,"gitemid");
				unset(newGraphItem,"graphid");

				for (Entry<String, Object> e : ((Map<String, Object>)newGraphItem).entrySet()) {
					String name = e.getKey();
				    Object value = e.getValue();
				    ((Curl)screenitem.get("url")).setArgument("items["+Nest.value(newGraphItem,"itemid").$()+"]["+name+"]", value);
				}
			}

			((Curl)screenitem.get("url")).setArgument("name", Nest.value(host,"name").$()+NAME_DELIMITER+Nest.value(graph,"name").$());
			Nest.value(screenitem,"url").$(((Curl)screenitem.get("url")).getUrl());
		}

		// get time control
		Map timeControlData = map(
			"id", getDataId(),
			"containerid", containerId,
			"objDims", graphDims,
			"loadSBox", 0,
			"loadImage", 1,
			"periodFixed", CProfile.get(this.idBean, this.executor,"web.screens.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);

		boolean isDefault = false;
		if (Nest.value(graphDims,"graphtype").asInteger() == GRAPH_TYPE_PIE || Nest.value(graphDims,"graphtype").asInteger() == GRAPH_TYPE_EXPLODED) {
			if (Nest.value(screenitem,"dynamic").asInteger() == SCREEN_SIMPLE_ITEM || empty(Nest.value(screenitem,"url").$())) {
				Nest.value(screenitem,"url").$("chart6.action?graphid="+resourceId+"&screenid="+Nest.value(screenitem,"screenid").$());
				isDefault = true;
			}

			Nest.value(timeline,"starttime").$(date(TIMESTAMP_FORMAT, get_min_itemclock_by_graphid(this.idBean, executor,resourceId)));

			Nest.value(timeControlData,"src").$(Nest.value(screenitem,"url").$()+"&width="+Nest.value(screenitem,"width").$()
				+"&height="+Nest.value(screenitem,"height").$()+"&legend="+legend
				+"&graph3d="+graph3d+getProfileUrlParams());
			Nest.value(timeControlData,"src").$(Nest.value(timeControlData,"src").$()+((mode == SCREEN_MODE_EDIT)
				? "&period=3600&stime="+date(TIMESTAMP_FORMAT, time())
				: "&period="+Nest.value(timeline,"period").$()+"&stime="+Nest.value(timeline,"stimeNow").$()));
		} else {
			if (Nest.value(screenitem,"dynamic").asInteger() == SCREEN_SIMPLE_ITEM || empty(Nest.value(screenitem,"url").$())) {
				Nest.value(screenitem,"url").$("chart2.action?graphid="+resourceId+"&screenid="+Nest.value(screenitem,"screenid").$());
				isDefault = true;
			}

			if (mode != SCREEN_MODE_EDIT && !empty(graphId)) {
				if (mode == SCREEN_MODE_PREVIEW) {
					Nest.value(timeControlData,"loadSBox").$(1);
				}
			}

			Nest.value(timeControlData,"src").$(Nest.value(screenitem,"url").$()+"&width="+Nest.value(screenitem,"width").$()
				+"&height="+Nest.value(screenitem,"height").$()+"&legend="+legend+getProfileUrlParams());
			Nest.value(timeControlData,"src").$(Nest.value(timeControlData,"src").$() + ((mode == SCREEN_MODE_EDIT)
				? "&period=3600&stime="+date(TIMESTAMP_FORMAT, time())
				: "&period="+Nest.value(timeline,"period").$()+"&stime="+Nest.value(timeline,"stimeNow").$()));
		}

		// output
		if (mode == SCREEN_MODE_JS) {
			return "timeControl.addObject(\""+getDataId()+"\", "+encodeJson(timeline)+", "
				+encodeJson(timeControlData)+")";
		} else {
			if (mode == SCREEN_MODE_SLIDESHOW) {
				insert_js("timeControl.addObject(\""+getDataId()+"\", "+encodeJson(timeline)+", "
					+encodeJson(timeControlData)+");"
				);
			} else {
				rda_add_post_js("timeControl.addObject(\""+getDataId()+"\", "+encodeJson(timeline)+", "
					+encodeJson(timeControlData)+");"
				);
			}

			CTag item = null;
			if ((mode == SCREEN_MODE_EDIT || mode == SCREEN_MODE_SLIDESHOW) || !isDefault) {
				item = new CDiv();
			} else if (mode == SCREEN_MODE_PREVIEW) {
				item = new CLink(null, "charts.action?graphid="+resourceId+"&period="+Nest.value(timeline,"period").$()+
						"&stime="+Nest.value(timeline,"stimeNow").$());
			}
			item.setAttribute("id", containerId);
			return getOutput(item);
		}
	}

}
