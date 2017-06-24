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


class CScreenBuilder {

	/**
	 * Switch on/off flicker-free screens auto refresh.
	 *
	 * @var boolean
	 */
	public $isFlickerfree;

	/**
	 * Page file.
	 *
	 * @var string
	 */
	public $pageFile;

	/**
	 * Screen data
	 *
	 * @var array
	 */
	public $screen;

	/**
	 * Display mode
	 *
	 * @var int
	 */
	public $mode;

	/**
	 * @see Request timestamp
	 */
	public $timestamp;

	/**
	 * Host id
	 *
	 * @var string
	 */
	public $hostid;

	/**
	 * Profile table entity name #1
	 *
	 * @var string
	 */
	public $profileIdx;

	/**
	 * Profile table record id belongs to #1
	 *
	 * @var int
	 */
	public $profileIdx2;

	/**
	 * Is profile will be updated
	 *
	 * @var boolean
	 */
	public $updateProfile;

	/**
	 * Time control timeline
	 *
	 * @var array
	 */
	public $timeline;

	/**
	 * Init screen data.
	 *
	 * @param array		$options
	 * @param boolean	$options["isFlickerfree"]
	 * @param string	$options["pageFile"]
	 * @param int		$options["mode"]
	 * @param int		$options["timestamp"]
	 * @param int		$options["hostid"]
	 * @param int		$options["period"]
	 * @param int		$options["stime"]
	 * @param string	$options["profileIdx"]
	 * @param int		$options["profileIdx2"]
	 * @param boolean	$options["updateProfile"]
	 * @param array		$options["screen"]
	 */
	public function __construct(array $options = CArray.array()) {
		isFlickerfree = isset(Nest.value($options,"isFlickerfree").$()) ? Nest.value($options,"isFlickerfree").$() : true;
		mode = isset(Nest.value($options,"mode").$()) ? Nest.value($options,"mode").$() : SCREEN_MODE_SLIDESHOW;
		timestamp = !empty(Nest.value($options,"timestamp").$()) ? Nest.value($options,"timestamp").$() : time();
		hostid = !empty(Nest.value($options,"hostid").$()) ? Nest.value($options,"hostid").$() : null;

		// get page file
		if (!empty(Nest.value($options,"pageFile").$())) {
			pageFile = Nest.value($options,"pageFile").$();
		}
		else {
			global $page;
			pageFile = Nest.value($page,"file").$();
		}

		// get screen
		if (!empty(Nest.value($options,"screen").$())) {
			screen = Nest.value($options,"screen").$();
		}
		elseif (!empty(Nest.value($options,"screenid").$())) {
			screen = API.Screen()->get(CArray.array(
				"screenids" => Nest.value($options,"screenid").$(),
				"output" => API_OUTPUT_EXTEND,
				"selectScreenItems" => API_OUTPUT_EXTEND,
				"editable" => (mode == SCREEN_MODE_EDIT)
			));

			if (!empty(screen)) {
				screen = reset(screen);
			}
			else {
				access_deny();
			}
		}

		// calculate time
		profileIdx = !empty(Nest.value($options,"profileIdx").$()) ? Nest.value($options,"profileIdx").$() : "";
		profileIdx2 = !empty(Nest.value($options,"profileIdx2").$()) ? Nest.value($options,"profileIdx2").$() : null;
		updateProfile = isset(Nest.value($options,"updateProfile").$()) ? Nest.value($options,"updateProfile").$() : true;

		timeline = CScreenBase::calculateTime(CArray.array(
			"profileIdx" => profileIdx,
			"profileIdx2" => profileIdx2,
			"updateProfile" => updateProfile,
			"period" => !empty(Nest.value($options,"period").$()) ? Nest.value($options,"period").$() : null,
			"stime" => !empty(Nest.value($options,"stime").$()) ? Nest.value($options,"stime").$() : null
		));
	}

	/**
	 * Get particular screen object.
	 *
	 * @static
	 *
	 * @param array		$options
	 * @param int		$options["resourcetype"]
	 * @param int		$options["screenitemid"]
	 * @param int		$options["hostid"]
	 *
	 * @return CScreenBase
	 */
	public static function getScreen(array $options = CArray.array()) {
		// get resourcetype from screenitem
		if (empty(Nest.value($options,"screenitem").$()) && !empty(Nest.value($options,"screenitemid").$())) {
			if (!empty(Nest.value($options,"hostid").$())) {
				Nest.value($options,"screenitem").$() = API.TemplateScreenItem().get(CArray.array(
					"screenitemids" => Nest.value($options,"screenitemid").$(),
					"hostids" => Nest.value($options,"hostid").$(),
					"output" => API_OUTPUT_EXTEND
				));
			}
			else {
				Nest.value($options,"screenitem").$() = API.ScreenItem().get(CArray.array(
					"screenitemids" => Nest.value($options,"screenitemid").$(),
					"output" => API_OUTPUT_EXTEND
				));
			}
			Nest.value($options,"screenitem").$() = reset(Nest.value($options,"screenitem").$());
		}

		if (zbx_empty(Nest.value($options,"resourcetype").$()) && !zbx_empty(Nest.value($options,"screenitem","resourcetype").$())) {
			Nest.value($options,"resourcetype").$() = Nest.value($options,"screenitem","resourcetype").$();
		}

		if (zbx_empty(Nest.value($options,"resourcetype").$())) {
			return null;
		}

		// get screen
		switch (Nest.value($options,"resourcetype").$()) {
			case SCREEN_RESOURCE_GRAPH:
				return new CScreenGraph($options);

			case SCREEN_RESOURCE_SIMPLE_GRAPH:
				return new CScreenSimpleGraph($options);

			case SCREEN_RESOURCE_MAP:
				return new CScreenMap($options);

			case SCREEN_RESOURCE_PLAIN_TEXT:
				return new CScreenPlainText($options);

			case SCREEN_RESOURCE_HOSTS_INFO:
				return new CScreenHostsInfo($options);

			case SCREEN_RESOURCE_TRIGGERS_INFO:
				return new CScreenTriggersInfo($options);

			case SCREEN_RESOURCE_SERVER_INFO:
				return new CScreenServerInfo($options);

			case SCREEN_RESOURCE_CLOCK:
				return new CScreenClock($options);

			case SCREEN_RESOURCE_SCREEN:
				return new CScreenScreen($options);

			case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
				return new CScreenTriggersOverview($options);

			case SCREEN_RESOURCE_DATA_OVERVIEW:
				return new CScreenDataOverview($options);

			case SCREEN_RESOURCE_URL:
				return new CScreenUrl($options);

			case SCREEN_RESOURCE_ACTIONS:
				return new CScreenActions($options);

			case SCREEN_RESOURCE_EVENTS:
				return new CScreenEvents($options);

			case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
				return new CScreenHostgroupTriggers($options);

			case SCREEN_RESOURCE_SYSTEM_STATUS:
				return new CScreenSystemStatus($options);

			case SCREEN_RESOURCE_HOST_TRIGGERS:
				return new CScreenHostTriggers($options);

			case SCREEN_RESOURCE_HISTORY:
				return new CScreenHistory($options);

			case SCREEN_RESOURCE_CHART:
				return new CScreenChart($options);

			default:
				return null;
		}
	}

	/**
	 * Process screen with particular screen objects.
	 *
	 * @return CTable
	 */
	public function show() {
		if (empty(screen)) {
			return new CTableInfo(_("No screens found."));
		}

		$skipedFields = CArray.array();
		$screenitems = CArray.array();
		$emptyScreenColumns = CArray.array();

		// calculate table columns and rows
		for(Nest.value(screen,"screenitems").$() as $screenitem) {
			$screenitems[] = $screenitem;

			for ($i = 0; $i < Nest.value($screenitem,"rowspan").$() || $i == 0; $i++) {
				for ($j = 0; $j < Nest.value($screenitem,"colspan").$() || $j == 0; $j++) {
					if ($i != 0 || $j != 0) {
						if (!isset($skipedFields[Nest.value($screenitem,"y").$() + $i])) {
							$skipedFields[Nest.value($screenitem,"y").$() + $i] = CArray.array();
						}
						$skipedFields[Nest.value($screenitem,"y").$() + $i][Nest.value($screenitem,"x").$() + $j] = 1;
					}
				}
			}
		}

		// create screen table
		$screenTable = new CTable();
		$screenTable.setAttribute("class",
			in_CArray.array(mode, CArray.array(SCREEN_MODE_PREVIEW, SCREEN_MODE_SLIDESHOW)) ? "screen_view" : "screen_edit"
		);
		$screenTable.setAttribute("id", "iframe");

		// action top row
		if (mode == SCREEN_MODE_EDIT) {
			$newColumns = CArray.array(new CCol(new CImg("images/general/zero.png", "zero", 1, 1)));

			for ($i = 0, $size = Nest.value(screen,"hsize").$() + 1; $i < $size; $i++) {
				$icon = new CImg("images/general/plus.png", null, null, null, "pointer");
				$icon.addAction("onclick", "javascript: location.href = \"screenedit.php?config=1&screenid=".screen["screenid"]."&add_col=".$i."\";");

				array_push($newColumns, new CCol($icon));
			}

			$screenTable.addRow($newColumns);
		}

		for ($r = 0; $r < Nest.value(screen,"vsize").$(); $r++) {
			$newColumns = CArray.array();
			$emptyScreenRow = true;

			// action left cell
			if (mode == SCREEN_MODE_EDIT) {
				$icon = new CImg("images/general/plus.png", null, null, null, "pointer");
				$icon.addAction("onclick", "javascript: location.href = \"screenedit.php?config=1&screenid=".screen["screenid"]."&add_row=".$r."\";");

				array_push($newColumns, new CCol($icon));
			}

			for ($c = 0; $c < Nest.value(screen,"hsize").$(); $c++) {
				if (isset($skipedFields[$r][$c])) {
					continue;
				}

				// screen item
				$isEditForm = false;
				$screenitem = CArray.array();

				for($screenitems as $tmprow) {
					if (Nest.value($tmprow,"x").$() == $c && Nest.value($tmprow,"y").$() == $r) {
						$screenitem = $tmprow;
						break;
					}
				}

				if (empty($screenitem)) {
					$screenitem = CArray.array(
						"screenitemid" => 0,
						"resourcetype" => 0,
						"resourceid" => 0,
						"width" => 0,
						"height" => 0,
						"colspan" => 1,
						"rowspan" => 1,
						"elements" => 0,
						"valign" => VALIGN_DEFAULT,
						"halign" => HALIGN_DEFAULT,
						"style" => 0,
						"url" => "",
						"dynamic" => 0,
						"sort_triggers" => SCREEN_SORT_TRIGGERS_DATE_DESC
					);
				}

				if (!empty(Nest.value($screenitem,"screenitemid").$())) {
					$emptyScreenRow = false;
					$emptyScreenColumns[$c] = 1;
				}

				// action
				if (mode == SCREEN_MODE_EDIT && Nest.value($screenitem,"screenitemid").$() != 0) {
					$action = "screenedit.php?form=update".url_param("screenid")."&screenitemid=".Nest.value($screenitem,"screenitemid").$();
				}
				elseif (mode == SCREEN_MODE_EDIT && Nest.value($screenitem,"screenitemid").$() == 0) {
					$action = "screenedit.php?form=update".url_param("screenid")."&x=".$c."&y=".$r;
				}
				else {
					$action = null;
				}

				// edit form cell
				if (mode == SCREEN_MODE_EDIT
						&& (isset(Nest.value(_REQUEST,"form").$()) && Nest.value(_REQUEST,"form").$() == "update")
						&& ((isset(Nest.value(_REQUEST,"x").$()) && Nest.value(_REQUEST,"x").$() == $c && isset(Nest.value(_REQUEST,"y").$()) && Nest.value(_REQUEST,"y").$() == $r)
								|| (isset(Nest.value(_REQUEST,"screenitemid").$()) && bccomp(Nest.value(_REQUEST,"screenitemid").$(), Nest.value($screenitem,"screenitemid").$()) == 0))) {
					$screenView = new CView("configuration.screen.constructor.edit", CArray.array("screen" => screen));
					$item = $screenView.render();
					$isEditForm = true;
				}
				// screen cell
				elseif (!empty(Nest.value($screenitem,"screenitemid").$()) && isset(Nest.value($screenitem,"resourcetype").$())) {
					$screenBase = CScreenBuilder::getScreen(CArray.array(
						"isFlickerfree" => isFlickerfree,
						"pageFile" => pageFile,
						"mode" => mode,
						"timestamp" => timestamp,
						"hostid" => hostid,
						"profileIdx" => profileIdx,
						"profileIdx2" => profileIdx2,
						"updateProfile" => updateProfile,
						"timeline" => timeline,
						"resourcetype" => Nest.value($screenitem,"resourcetype").$(),
						"screenitem" => $screenitem
					));

					if (!empty($screenBase)) {
						if (mode == SCREEN_MODE_EDIT && !empty(Nest.value($screenitem,"screenitemid").$())) {
							$screenBase.action = "screenedit.php?form=update".url_param("screenid")."&screenitemid=".Nest.value($screenitem,"screenitemid").$();
						}
						elseif (mode == SCREEN_MODE_EDIT && empty(Nest.value($screenitem,"screenitemid").$())) {
							$screenBase.action = "screenedit.php?form=update".url_param("screenid")."&x=".$c."&y=".$r;
						}

						$item = $screenBase.get();
					}
					else {
						$item = null;
					}
				}
				// change/empty cell
				else {
					$item = CArray.array(SPACE);
					if (mode == SCREEN_MODE_EDIT) {
						array_push($item, BR(), new CLink(_("Change"), $action, "empty_change_link"));
					}
				}

				// align
				$halign = "def";
				if (Nest.value($screenitem,"halign").$() == HALIGN_CENTER) {
					$halign = "cntr";
				}
				if (Nest.value($screenitem,"halign").$() == HALIGN_LEFT) {
					$halign = "left";
				}
				if (Nest.value($screenitem,"halign").$() == HALIGN_RIGHT) {
					$halign = "right";
				}

				$valign = "def";
				if (Nest.value($screenitem,"valign").$() == VALIGN_MIDDLE) {
					$valign = "mdl";
				}
				if (Nest.value($screenitem,"valign").$() == VALIGN_TOP) {
					$valign = "top";
				}
				if (Nest.value($screenitem,"valign").$() == VALIGN_BOTTOM) {
					$valign = "bttm";
				}

				if (mode == SCREEN_MODE_EDIT && !$isEditForm) {
					$item = new CDiv($item, "draggable");
					$item.setAttribute("id", "position_".$r."_".$c);
					$item.setAttribute("data-xcoord", $c);
					$item.setAttribute("data-ycoord", $r);
				}

				// colspan/rowspan
				$newColumn = new CCol($item, $halign."_".$valign." screenitem");
				if (!empty(Nest.value($screenitem,"colspan").$())) {
					$newColumn.setColSpan(Nest.value($screenitem,"colspan").$());
				}
				if (!empty(Nest.value($screenitem,"rowspan").$())) {
					$newColumn.setRowSpan(Nest.value($screenitem,"rowspan").$());
				}
				array_push($newColumns, $newColumn);
			}

			// action right cell
			if (mode == SCREEN_MODE_EDIT) {
				$icon = new CImg("images/general/minus.png", null, null, null, "pointer");
				if ($emptyScreenRow) {
					$removeRowLink = "javascript: location.href = \"screenedit.php?screenid=".screen["screenid"]."&rmv_row=".$r."\";";
				}
				else {
					$removeRowLink = "javascript: if (Confirm(\""._("This screen-row is not empty. Delete it?")."\")) {".
						" location.href = \"screenedit.php?screenid=".screen["screenid"]."&rmv_row=".$r."\"; }";
				}
				$icon.addAction("onclick", $removeRowLink);
				array_push($newColumns, new CCol($icon));
			}
			$screenTable.addRow(new CRow($newColumns));
		}

		// action bottom row
		if (mode == SCREEN_MODE_EDIT) {
			$icon = new CImg("images/general/plus.png", null, null, null, "pointer");
			$icon.addAction("onclick", "javascript: location.href = \"screenedit.php?screenid=".screen["screenid"]."&add_row=".screen["vsize"]."\";");
			$newColumns = CArray.array(new CCol($icon));

			for ($i = 0; $i < Nest.value(screen,"hsize").$(); $i++) {
				$icon = new CImg("images/general/minus.png", null, null, null, "pointer");
				if (isset($emptyScreenColumns[$i])) {
					$removeColumnLink = "javascript: if (Confirm(\""._("This screen-column is not empty. Delete it?")."\")) {".
						" location.href = \"screenedit.php?screenid=".screen["screenid"]."&rmv_col=".$i."\"; }";
				}
				else {
					$removeColumnLink = "javascript: location.href = \"screenedit.php?config=1&screenid=".screen["screenid"]."&rmv_col=".$i."\";";
				}
				$icon.addAction("onclick", $removeColumnLink);

				array_push($newColumns, new CCol($icon));
			}

			array_push($newColumns, new CCol(new CImg("images/general/zero.png", "zero", 1, 1)));
			$screenTable.addRow($newColumns);
		}

		return $screenTable;
	}

	/**
	 * Insert javascript to create scroll in time control.
	 *
	 * @static
	 *
	 * @param array $options
	 * @param array $options["timeline"]
	 * @param string $options["profileIdx"]
	 */
	public static function insertScreenScrollJs(array $options = CArray.array()) {
		Nest.value($options,"timeline").$() = empty(Nest.value($options,"timeline").$()) ? "" : Nest.value($options,"timeline").$();
		Nest.value($options,"profileIdx").$() = empty(Nest.value($options,"profileIdx").$()) ? "" : Nest.value($options,"profileIdx").$();

		$timeControlData = CArray.array(
			"id" => "scrollbar",
			"loadScroll" => 1,
			"mainObject" => 1,
			"periodFixed" => CProfile::get($options["profileIdx"].".timelinefixed", 1),
			"sliderMaximumTimePeriod" => ZBX_MAX_PERIOD
		);

		zbx_add_post_js("timeControl.addObject(\"scrollbar\", ".zbx_jsvalue(Nest.value($options,"timeline").$()).", ".zbx_jsvalue($timeControlData).");");
	}

	/**
	 * Insert javascript to make time control synchronizes with NOW!
	 *
	 * @static
	 */
	public static function insertScreenRefreshTimeJs() {
		zbx_add_post_js("timeControl.useTimeRefresh(".CWebUser::$data["refresh"].");");
	}

	/**
	 * Insert javascript to init screens.
	 *
	 * @static
	 *
	 * @param string $screenid
	 */
	public static function insertInitScreenJs($screenid) {
		zbx_add_post_js("init_screen(\"".$screenid."\", \"iframe\", \"".$screenid."\");");
	}

	/**
	 * Insert javascript to start time control rendering.
	 *
	 * @static
	 */
	public static function insertProcessObjectsJs() {
		zbx_add_post_js("timeControl.processObjects();");
	}

	/**
	 * Insert javascript to clean all screen items.
	 *
	 * @static
	 */
	public static function insertScreenCleanJs() {
		zbx_add_post_js("window.flickerfreeScreen.cleanAll();");
	}

	/**
	 * Insert javascript for standard screens.
	 *
	 * @param array $options
	 * @param array $options["timeline"]
	 * @param string $options["profileIdx"]
	 *
	 * @static
	 */
	public static function insertScreenStandardJs(array $options = CArray.array()) {
		CScreenBuilder::insertScreenScrollJs($options);
		CScreenBuilder::insertScreenRefreshTimeJs();
		CScreenBuilder::insertProcessObjectsJs();
	}
}
