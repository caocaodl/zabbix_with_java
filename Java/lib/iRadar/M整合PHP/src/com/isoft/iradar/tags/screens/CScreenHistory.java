package com.isoft.iradar.tags.screens;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_HIDE;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_INVERT_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_MARK;
import static com.isoft.iradar.inc.Defines.FILTER_TASK_SHOW;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_BLUE;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_GREEN;
import static com.isoft.iradar.inc.Defines.MARK_COLOR_RED;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_JS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HISTORY;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.FuncsUtil.encode_log;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.FuncsUtil.rda_stristr;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.GraphsUtil.getGraphDims;
import static com.isoft.iradar.inc.GraphsUtil.get_min_itemclock_by_itemid;
import static com.isoft.iradar.inc.HtmlUtil.nbsp;
import static com.isoft.iradar.inc.ItemsUtil.get_item_logtype_description;
import static com.isoft.iradar.inc.ItemsUtil.get_item_logtype_style;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TranslateDefines.HISTORY_ITEM_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.HISTORY_LOG_ITEM_PLAINTEXT;
import static com.isoft.iradar.inc.TranslateDefines.HISTORY_LOG_LOCALTIME_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.HISTORY_PLAINTEXT_DATE_FORMAT;
import static com.isoft.iradar.inc.ValuemapUtil.applyValueMap;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHistoryGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.7")
public class CScreenHistory extends CScreenBase {
	
	/**
	 * Item ids
	 *
	 * @var array
	 */
	public Long[] itemids;

	/**
	 * Search string
	 *
	 * @var string
	 */
	public String filter;

	/**
	 * Filter show/hide
	 *
	 * @var int
	 */
	public Integer filterTask;

	/**
	 * Filter highlight color
	 *
	 * @var string
	 */
	public int markColor;

	/**
	 * Is plain text displayed
	 *
	 * @var boolean
	 */
	public boolean plaintext;

	/**
	 * Items data
	 *
	 * @var array
	 */
	public CArray<Map> items;
	
	/**
	 * Item data
	 *
	 * @var array
	 */
	public Map item;

	/**
	 * Init screen data.
	 *
	 * @param array		options
	 * @param array		options["itemids"]
	 * @param string	options["filter"]
	 * @param int		options["filterTask"]
	 * @param int		options["markColor"]
	 * @param boolean	options["plaintext"]
	 * @param array		options["items"]
	 * @param array		options["item"]
	 */
	public CScreenHistory(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}
	
	public CScreenHistory(IIdentityBean idBean, SQLExecutor executor, Map options) {
		super(idBean, executor, options);
		resourcetype = SCREEN_RESOURCE_HISTORY;
		
		// mandatory
		itemids = isset(options,"itemids") ? Nest.array(options,"itemids").asLong() : null;
		filter = isset(options,"filter") ? Nest.value(options,"filter").asString() : null;
		filterTask = isset(options,"filter_task") ? Nest.value(options,"filter_task").asInteger() : null;
		markColor = isset(options,"mark_color") ? Nest.value(options,"mark_color").asInteger() : MARK_COLOR_RED;

		// optional
		items = isset(options,"items") ? Nest.value(options,"items").asCArray() : null;
		item = isset(options,"item") ? Nest.value(options,"item").asCArray() : null;
		plaintext = isset(options,"plaintext") ? Nest.value(options,"plaintext").asBoolean() : false;

		if (empty(items)) {
			CItemGet ioptions = new CItemGet();
			if(itemids!=null && itemids.length>0){
			ioptions.setItemIds(itemids);
			}
			ioptions.setWebItems(true);
			ioptions.setSelectHosts(new String[]{"name"});
			ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_", "value_type", "valuemapid"});
			ioptions.setPreserveKeys(true);
			items = API.Item(idBean, executor).get(ioptions);
			items = CMacrosResolverHelper.resolveItemNames(idBean, executor,items);
			item = reset(items);
		}
	}
	
	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	@Override
	public Object get() {
		CArray output = array();

		long stime = rdaDateToTime(Nest.value(timeline,"stime").asString());

		Map<Integer,Integer> iv_string = (Map)map(
			ITEM_VALUE_TYPE_LOG, 1,
			ITEM_VALUE_TYPE_TEXT, 1
		);
				
		@SuppressWarnings("unused")
		Map<Integer,Integer>iv_numeric = (Map)map(
			ITEM_VALUE_TYPE_FLOAT, 1,
			ITEM_VALUE_TYPE_UINT64, 1
		);

		if ("showvalues".equals(action) || "showlatest".equals(action)) {
			CHistoryGet options = new CHistoryGet();
			options.setHistory(Nest.value(item,"value_type").asInteger());
			options.setItemIds(array_keys(items).valuesAsLong());
			options.setOutput(API_OUTPUT_EXTEND);
			options.setSortorder(RDA_SORT_DOWN);
			if ("showlatest".equals(action)) {
				options.setLimit(500);
			} else if ("showvalues".equals(action)) {
				Map<String, Object> config = select_config(this.idBean, this.executor);
				options.setTimeFrom(stime - 10);// some seconds to allow script to execute
				options.setTimeTill(stime + Nest.value(timeline,"period").asLong());
				options.setLimit(Nest.value(config,"search_limit").asInteger());
			}

			// text log
			CTableInfo historyTable = null;
			if (isset(iv_string,item.get("value_type"))) {
				boolean isManyItems = (count(items) > 1);
				boolean useLogItem = (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_LOG);
				boolean useEventLogItem = (strpos(Nest.value(item,"key_").asString(), "eventlog[") == 0);

				if (empty(plaintext)) {
					historyTable = new CTableInfo(_("No values found."));
					historyTable.setHeader(
						array(
							_("Timestamp"),
							isManyItems ? _("Item") : null,
							useLogItem ? _("Local time") : null,
							(useEventLogItem && useLogItem) ? _("Source") : null,
							(useEventLogItem && useLogItem) ? _("Severity") : null,
							(useEventLogItem && useLogItem) ? _("Event ID") : null,
							_("Value")
						),
						"header"
					);
				}

				if (!rda_empty(filter) && in_array(filterTask, new Integer[]{FILTER_TASK_SHOW, FILTER_TASK_HIDE})) {
					options.setSearch("value", filter);
					if (filterTask == FILTER_TASK_HIDE) {
						options.setExcludeSearch(true);
					}
				}
				options.setSortfield("id");

				CArray<Map> historyData = API.History(this.idBean, this.executor).get(options);

				for(Map data : historyData) {
					Nest.value(data,"value").$(encode_log(trim(Nest.value(data,"value").asString(), '\r', '\n')));

					if (empty(plaintext)) {
						Map item = items.get(data.get("itemid"));
						Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
						String color = null;

						if (isset(filter) && !rda_empty(filter)) {
							boolean contain = !empty(rda_stristr(Nest.value(data,"value").asString(), filter));

							int mcolor = 0;
							if (contain && filterTask == FILTER_TASK_MARK) {
								mcolor = markColor;
							}
							if (!contain && filterTask == FILTER_TASK_INVERT_MARK) {
								mcolor = markColor;
							}

								switch (mcolor) {
								case MARK_COLOR_RED:
									color = "red";
									break;
								case MARK_COLOR_GREEN:
									color = "green";
									break;
								case MARK_COLOR_BLUE:
									color = "blue";
									break;
							}
						}

						CArray row = array(nbsp(rda_date2str(_("Y.M.d H:i:s"), Nest.value(data,"clock").asLong())));

						if (isManyItems) {
							row.add(Nest.value(host,"name").asString()+NAME_DELIMITER+Nest.value(item,"name_expanded").$());
						}

						if (useLogItem) {
							row.add((Nest.value(data,"timestamp").asLong() == 0) ? "-" : rda_date2str(HISTORY_LOG_LOCALTIME_DATE_FORMAT, Nest.value(data,"timestamp").asLong()));

							// if this is a eventLog item, showing additional info
							if (useEventLogItem) {
								row.add(rda_empty(Nest.value(data,"source").$()) ? "-" : Nest.value(data,"source").$());
								row.add((Nest.value(data,"severity").asInteger() == 0)
								? "-"
								: new CCol(get_item_logtype_description(Nest.value(data,"severity").asInteger()), get_item_logtype_style(Nest.value(data,"severity").asInteger())));
								row.add((Nest.value(data,"logeventid").asLong() == 0) ? "-" : Nest.value(data,"logeventid").$());
							}
						}

						row.add(new CCol(Nest.value(data,"value").$(), "pre"));

						CRow newRow = new CRow(row);
						if (!is_null(color)) {
							newRow.setAttribute("class", color);
						}

						historyTable.addRow(newRow);
					} else {
						output.add(rda_date2str(HISTORY_LOG_ITEM_PLAINTEXT, Nest.value(data,"clock").asLong()));
						output.add("\t"+Nest.value(data,"clock").asString()+"\t"+htmlspecialchars(Nest.value(data,"value").asString())+"\n");
					}
				}

				if (empty(plaintext)) {
					output.add(historyTable);
				}
			} else {// numeric, float
				if (empty(plaintext)) {
					historyTable = new CTableInfo(_("No values found."));
					historyTable.setHeader(array(_("Timestamp"), _("Value")));
				}

				options.setSortfield("itemid", "clock");
				CArray<Map>historyData = API.History(this.idBean, this.executor).get(options);

				for(Map data : historyData) {
					Map item = items.get(data.get("itemid"));
					Object value = Nest.value(data,"value").$();

					// format the value as float
					if (Nest.value(item,"value_type").asInteger() == ITEM_VALUE_TYPE_FLOAT) {
						value = Nest.value(data,"value").asFloat();;
					}

					// html table
					if (empty(plaintext)) {
						if (!empty(Nest.value(item,"valuemapid").$())) {
							value = applyValueMap(this.idBean, this.executor,Nest.as(value).asString(), Nest.value(item,"valuemapid").asLong());
						}
						historyTable.addRow(array(
							rda_date2str(HISTORY_ITEM_DATE_FORMAT, Nest.value(data,"clock").asLong()),
							rda_nl2br(Nest.as(value).asString())
						));
					} else {// plain text
						output.add(rda_date2str(HISTORY_PLAINTEXT_DATE_FORMAT, Nest.value(data,"clock").asLong()));
						output.add("\t"+Nest.value(data,"clock").asString()+"\t"+htmlspecialchars(Nest.as(value).asString())+"\n");
					}
				}

				if (empty(plaintext)) {
					output.add(historyTable);
				}
			}
		}

		String containerId = null;
		String src = null;
		if ("showgraph".equals(action) && !isset(iv_string,item.get("value_type"))) {
			dataId = "historyGraph";
			containerId = "graph_cont1";
			src = "chart.action?itemid="+Nest.value(item,"itemid").asString()+"&period="+Nest.value(timeline,"period").asString()+"&stime="+Nest.value(timeline,"stime").asString()+getProfileUrlParams();
			output.add(new CDiv(null, "center", containerId));
		}

		// time control
		if (!plaintext && str_in_array(action, array("showvalues", "showgraph"))) {
			Map graphDims = getGraphDims(this.idBean, executor);

			Nest.value(timeline,"starttime").$(date(TIMESTAMP_FORMAT, get_min_itemclock_by_itemid(this.idBean, executor,Nest.value(item,"itemid").asString())));

			Map timeControlData = map(
				"periodFixed", CProfile.get(this.idBean, this.executor,"web.history.timelinefixed", 1),
				"sliderMaximumTimePeriod", RDA_MAX_PERIOD
			);

			if (!empty(dataId)) {
				Nest.value(timeControlData,"id").$(getDataId());
				Nest.value(timeControlData,"containerid").$(containerId);
				Nest.value(timeControlData,"src").$(src);
				Nest.value(timeControlData,"objDims").$(graphDims);
				Nest.value(timeControlData,"loadSBox").$(1);
				Nest.value(timeControlData,"loadImage").$(1);
				Nest.value(timeControlData,"dynamic").$(1);
			} else {
				dataId = "historyGraph";
				Nest.value(timeControlData,"id").$(getDataId());
				Nest.value(timeControlData,"mainObject").$(1);
			}

			if (mode == SCREEN_MODE_JS) {
				Nest.value(timeControlData,"dynamic").$(0);
				return "timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");";
			} else {
				rda_add_post_js("timeControl.addObject(\""+getDataId()+"\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(timeControlData)+");");
			}
		}

		if (!empty(plaintext)) {
			return output;
		} else {
			if (mode != SCREEN_MODE_JS) {
				Map flickerfreeData = map(
					"itemids", itemids,
					"action", action,
					"filter", filter,
					"filterTask", filterTask,
					"markColor", markColor
				);
				return getOutput(output, true, flickerfreeData);
			}
			return null;
		}
	}

}
