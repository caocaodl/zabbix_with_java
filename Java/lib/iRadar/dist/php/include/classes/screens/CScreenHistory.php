<?php
/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/


class CScreenHistory extends CScreenBase {

	/**
	 * Item ids
	 *
	 * @var array
	 */
	public $itemids;

	/**
	 * Search string
	 *
	 * @var string
	 */
	public $filter;

	/**
	 * Filter show/hide
	 *
	 * @var int
	 */
	public $filterTask;

	/**
	 * Filter highlight color
	 *
	 * @var string
	 */
	public $markColor;

	/**
	 * Is plain text displayed
	 *
	 * @var boolean
	 */
	public $plaintext;

	/**
	 * Items data
	 *
	 * @var array
	 */
	public $items;

	/**
	 * Item data
	 *
	 * @var array
	 */
	public $item;

	/**
	 * Init screen data.
	 *
	 * @param array		$options
	 * @param array		$options["itemids"]
	 * @param string	$options["filter"]
	 * @param int		$options["filterTask"]
	 * @param int		$options["markColor"]
	 * @param boolean	$options["plaintext"]
	 * @param array		$options["items"]
	 * @param array		$options["item"]
	 */
	public function __construct(array $options = CArray.array()) {
		parent::__construct($options);

		resourcetype = SCREEN_RESOURCE_HISTORY;

		// mandatory
		itemids = isset(Nest.value($options,"itemids").$()) ? Nest.value($options,"itemids").$() : null;
		filter = isset(Nest.value($options,"filter").$()) ? Nest.value($options,"filter").$() : null;
		filterTask = isset(Nest.value($options,"filter_task").$()) ? Nest.value($options,"filter_task").$() : null;
		markColor = isset(Nest.value($options,"mark_color").$()) ? Nest.value($options,"mark_color").$() : MARK_COLOR_RED;

		// optional
		items = isset(Nest.value($options,"items").$()) ? Nest.value($options,"items").$() : null;
		item = isset(Nest.value($options,"item").$()) ? Nest.value($options,"item").$() : null;
		plaintext = isset(Nest.value($options,"plaintext").$()) ? Nest.value($options,"plaintext").$() : false;

		if (empty(items)) {
			items = API.Item()->get(CArray.array(
				"nodeids" => get_current_nodeid(),
				"itemids" => itemids,
				"webitems" => true,
				"selectHosts" => CArray.array("name"),
				"output" => CArray.array("itemid", "hostid", "name", "key_", "value_type", "valuemapid"),
				"preservekeys" => true
			));

			items = CMacrosResolverHelper::resolveItemNames(items);

			item = reset(items);
		}
	}

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		$output = CArray.array();

		$stime = zbxDateToTime(Nest.value(timeline,"stime").$());

		$iv_string = CArray.array(
			ITEM_VALUE_TYPE_LOG => 1,
			ITEM_VALUE_TYPE_TEXT => 1
		);
		$iv_numeric = CArray.array(
			ITEM_VALUE_TYPE_FLOAT => 1,
			ITEM_VALUE_TYPE_UINT64 => 1
		);

		if (action == "showvalues" || action == "showlatest") {
			$options = CArray.array(
				"history" => Nest.value(item,"value_type").$(),
				"itemids" => array_keys(items),
				"output" => API_OUTPUT_EXTEND,
				"sortorder" => ZBX_SORT_DOWN
			);
			if (action == "showlatest") {
				Nest.value($options,"limit").$() = 500;
			}
			elseif (action == "showvalues") {
				$config = select_config();

				Nest.value($options,"time_from").$() = $stime - 10; // some seconds to allow script to execute
				Nest.value($options,"time_till").$() = $stime + Nest.value(timeline,"period").$();
				Nest.value($options,"limit").$() = Nest.value($config,"search_limit").$();
			}

			// text log
			if (isset($iv_string[item["value_type"]])) {
				$isManyItems = (count(items) > 1);
				$useLogItem = (Nest.value(item,"value_type").$() == ITEM_VALUE_TYPE_LOG);
				$useEventLogItem = (strpos(Nest.value(item,"key_").$(), "eventlog[") === 0);

				if (empty(plaintext)) {
					$historyTable = new CTableInfo(_("No values found."));
					$historyTable.setHeader(
						CArray.array(
							_("Timestamp"),
							$isManyItems ? _("Item") : null,
							$useLogItem ? _("Local time") : null,
							($useEventLogItem && $useLogItem) ? _("Source") : null,
							($useEventLogItem && $useLogItem) ? _("Severity") : null,
							($useEventLogItem && $useLogItem) ? _("Event ID") : null,
							_("Value")
						),
						"header"
					);
				}

				if (!zbx_empty(filter) && in_CArray.array(filterTask, CArray.array(FILTER_TASK_SHOW, FILTER_TASK_HIDE))) {
					Nest.value($options,"search").$() = CArray.array("value" => filter);
					if (filterTask == FILTER_TASK_HIDE) {
						Nest.value($options,"excludeSearch").$() = 1;
					}
				}
				Nest.value($options,"sortfield").$() = "id";

				$historyData = API.History().get($options);

				for($historyData as $data) {
					Nest.value($data,"value").$() = encode_log(trim(Nest.value($data,"value").$(), \"\r\n\"));

					if (empty(plaintext)) {
						$item = items[$data["itemid"]];
						$host = reset(Nest.value($item,"hosts").$());
						$color = null;

						if (isset(filter) && !zbx_empty(filter)) {
							$contain = zbx_stristr(Nest.value($data,"value").$(), filter);

							if ($contain && filterTask == FILTER_TASK_MARK) {
								$color = markColor;
							}
							if (!$contain && filterTask == FILTER_TASK_INVERT_MARK) {
								$color = markColor;
							}

							switch ($color) {
								case MARK_COLOR_RED:
									$color = "red";
									break;
								case MARK_COLOR_GREEN:
									$color = "green";
									break;
								case MARK_COLOR_BLUE:
									$color = "blue";
									break;
							}
						}

						$row = CArray.array(nbsp(zbx_date2str(_("Y.M.d H:i:s"), Nest.value($data,"clock").$())));

						if ($isManyItems) {
							$row[] = $host["name"].NAME_DELIMITER.Nest.value($item,"name_expanded").$();
						}

						if ($useLogItem) {
							$row[] = (Nest.value($data,"timestamp").$() == 0) ? "-" : zbx_date2str(HISTORY_LOG_LOCALTIME_DATE_FORMAT, Nest.value($data,"timestamp").$());

							// if this is a eventLog item, showing additional info
							if ($useEventLogItem) {
								$row[] = zbx_empty(Nest.value($data,"source").$()) ? "-" : Nest.value($data,"source").$();
								$row[] = (Nest.value($data,"severity").$() == 0)
								? "-"
								: new CCol(get_item_logtype_description(Nest.value($data,"severity").$()), get_item_logtype_style(Nest.value($data,"severity").$()));
								$row[] = (Nest.value($data,"logeventid").$() == 0) ? "-" : Nest.value($data,"logeventid").$();
							}
						}

						$row[] = new CCol(Nest.value($data,"value").$(), "pre");

						$newRow = new CRow($row);
						if (is_null($color)) {
							$min_color = 0x98;
							$max_color = 0xF8;
							$int_color = ($max_color - $min_color) / count(itemids);
							$int_color *= array_search(Nest.value($data,"itemid").$(), itemids);
							$int_color += $min_color;
							$newRow.setAttribute("style", "background-color: ".sprintf(\"#%X%X%X\", $int_color, $int_color, $int_color));
						}
						elseif (!is_null($color)) {
							$newRow.setAttribute("class", $color);
						}

						$historyTable.addRow($newRow);
					}
					else {
						$output[] = zbx_date2str(HISTORY_LOG_ITEM_PLAINTEXT, Nest.value($data,"clock").$());
						$output[] = \"\t\".$data["clock"].\"\t\".htmlspecialchars(Nest.value($data,"value").$()).\"\n\";
					}
				}

				if (empty(plaintext)) {
					$output[] = $historyTable;
				}
			}

			// numeric, float
			else {
				if (empty(plaintext)) {
					$historyTable = new CTableInfo(_("No values found."));
					$historyTable.setHeader(CArray.array(_("Timestamp"), _("Value")));
				}

				Nest.value($options,"sortfield").$() = CArray.array("itemid", "clock");
				$historyData = API.History().get($options);

				for($historyData as $data) {
					$item = items[$data["itemid"]];
					$value = Nest.value($data,"value").$();

					// format the value as float
					if (Nest.value($item,"value_type").$() == ITEM_VALUE_TYPE_FLOAT) {
						sscanf(Nest.value($data,"value").$(), "%f", $value);
					}

					// html table
					if (empty(plaintext)) {
						if (Nest.value($item,"valuemapid").$()) {
							$value = applyValueMap($value, Nest.value($item,"valuemapid").$());
						}

						$historyTable.addRow(CArray.array(
							zbx_date2str(HISTORY_ITEM_DATE_FORMAT, Nest.value($data,"clock").$()),
							zbx_nl2br($value)
						));
					}
					// plain text
					else {
						$output[] = zbx_date2str(HISTORY_PLAINTEXT_DATE_FORMAT, Nest.value($data,"clock").$());
						$output[] = \"\t\".$data["clock"].\"\t\".htmlspecialchars($value).\"\n\";
					}
				}

				if (empty(plaintext)) {
					$output[] = $historyTable;
				}
			}
		}

		if (action == "showgraph" && !isset($iv_string[item["value_type"]])) {
			dataId = "historyGraph";
			$containerId = "graph_cont1";
			$src = "chart.php?itemid=".item["itemid"]."&period=".timeline["period"]."&stime=".timeline["stime"].getProfileUrlParams();

			$output[] = new CDiv(null, "center", $containerId);
		}

		// time control
		if (!plaintext && str_in_array(action, CArray.array("showvalues", "showgraph"))) {
			$graphDims = getGraphDims();

			Nest.value(timeline,"starttime").$() = date(TIMESTAMP_FORMAT, get_min_itemclock_by_itemid(Nest.value(item,"itemid").$()));

			$timeControlData = CArray.array(
				"periodFixed" => CProfile::get("web.history.timelinefixed", 1),
				"sliderMaximumTimePeriod" => ZBX_MAX_PERIOD
			);

			if (!empty(dataId)) {
				Nest.value($timeControlData,"id").$() = getDataId();
				Nest.value($timeControlData,"containerid").$() = $containerId;
				Nest.value($timeControlData,"src").$() = $src;
				Nest.value($timeControlData,"objDims").$() = $graphDims;
				Nest.value($timeControlData,"loadSBox").$() = 1;
				Nest.value($timeControlData,"loadImage").$() = 1;
				Nest.value($timeControlData,"dynamic").$() = 1;
			}
			else {
				dataId = "historyGraph";
				Nest.value($timeControlData,"id").$() = getDataId();
				Nest.value($timeControlData,"mainObject").$() = 1;
			}

			if (mode == SCREEN_MODE_JS) {
				Nest.value($timeControlData,"dynamic").$() = 0;

				return "timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");";
			}
			else {
				zbx_add_post_js("timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");");
			}
		}

		if (!empty(plaintext)) {
			return $output;
		}
		else {
			if (mode != SCREEN_MODE_JS) {
				$flickerfreeData = CArray.array(
					"itemids" => itemids,
					"action" => action,
					"filter" => filter,
					"filterTask" => filterTask,
					"markColor" => markColor
				);

				return getOutput($output, true, $flickerfreeData);
			}
		}
	}
}
