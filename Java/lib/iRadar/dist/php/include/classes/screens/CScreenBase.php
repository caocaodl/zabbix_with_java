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


class CScreenBase {

	/**
	 * @see CScreenBuilder::isFlickerfree
	 */
	public $isFlickerfree;

	/**
	 * Page file.
	 *
	 * @var string
	 */
	public $pageFile;

	/**
	 * @see CScreenBuilder::mode
	 */
	public $mode;

	/**
	 * @see CScreenBuilder::timestamp
	 */
	public $timestamp;

	/**
	 * Resource (screen) type
	 *
	 * @var int
	 */
	public $resourcetype;

	/**
	 * Screen id
	 *
	 * @var int
	 */
	public $screenid;

	/**
	 * Screen item data
	 *
	 * @var array
	 */
	public $screenitem;

	/**
	 * Action
	 *
	 * @var string
	 */
	public $action;

	/**
	 * Group id
	 *
	 * @var int
	 */
	public $groupid;

	/**
	 * Host id
	 *
	 * @var int
	 */
	public $hostid;

	/**
	 * Time control timeline
	 *
	 * @var array
	 */
	public $timeline;

	/**
	 * @see CScreenBuilder::profileIdx
	 */
	public $profileIdx;

	/**
	 * @see CScreenBuilder::profileIdx2
	 */
	public $profileIdx2;

	/**
	 * @see CScreenBuilder::updateProfile
	 */
	public $updateProfile;

	/**
	 * Time control dom element id
	 *
	 * @var string
	 */
	public $dataId;

	/**
	 * Init screen data.
	 *
	 * @param array		$options
	 * @param boolean	$options["isFlickerfree"]
	 * @param string	$options["pageFile"]
	 * @param int		$options["mode"]
	 * @param int		$options["timestamp"]
	 * @param int		$options["resourcetype"]
	 * @param int		$options["screenid"]
	 * @param array		$options["screenitem"]
	 * @param string	$options["action"]
	 * @param int		$options["groupid"]
	 * @param int		$options["hostid"]
	 * @param int		$options["period"]
	 * @param int		$options["stime"]
	 * @param string	$options["profileIdx"]
	 * @param int		$options["profileIdx2"]
	 * @param boolean	$options["updateProfile"]
	 * @param array		$options["timeline"]
	 * @param string	$options["dataId"]
	 */
	public function __construct(array $options = CArray.array()) {
		isFlickerfree = isset(Nest.value($options,"isFlickerfree").$()) ? Nest.value($options,"isFlickerfree").$() : true;
		mode = isset(Nest.value($options,"mode").$()) ? Nest.value($options,"mode").$() : SCREEN_MODE_SLIDESHOW;
		timestamp = !empty(Nest.value($options,"timestamp").$()) ? Nest.value($options,"timestamp").$() : time();
		resourcetype = isset(Nest.value($options,"resourcetype").$()) ? Nest.value($options,"resourcetype").$() : null;
		screenid = !empty(Nest.value($options,"screenid").$()) ? Nest.value($options,"screenid").$() : null;
		action = !empty(Nest.value($options,"action").$()) ? Nest.value($options,"action").$() : null;
		groupid = !empty(Nest.value($options,"groupid").$()) ? Nest.value($options,"groupid").$() : null;
		hostid = !empty(Nest.value($options,"hostid").$()) ? Nest.value($options,"hostid").$() : null;
		dataId = !empty(Nest.value($options,"dataId").$()) ? Nest.value($options,"dataId").$() : null;

		// get page file
		if (!empty(Nest.value($options,"pageFile").$())) {
			pageFile = Nest.value($options,"pageFile").$();
		}
		else {
			global $page;
			pageFile = Nest.value($page,"file").$();
		}

		// calculate timeline
		profileIdx = !empty(Nest.value($options,"profileIdx").$()) ? Nest.value($options,"profileIdx").$() : "";
		profileIdx2 = !empty(Nest.value($options,"profileIdx2").$()) ? Nest.value($options,"profileIdx2").$() : null;
		updateProfile = isset(Nest.value($options,"updateProfile").$()) ? Nest.value($options,"updateProfile").$() : true;
		timeline = !empty(Nest.value($options,"timeline").$()) ? Nest.value($options,"timeline").$() : null;
		if (empty(timeline)) {
			timeline = calculateTime(CArray.array(
				"profileIdx" => profileIdx,
				"profileIdx2" => profileIdx2,
				"updateProfile" => updateProfile,
				"period" => !empty(Nest.value($options,"period").$()) ? Nest.value($options,"period").$() : null,
				"stime" => !empty(Nest.value($options,"stime").$()) ? Nest.value($options,"stime").$() : null
			));
		}

		// get screenitem
		if (!empty(Nest.value($options,"screenitem").$())) {
			screenitem = Nest.value($options,"screenitem").$();
		}
		elseif (!empty(Nest.value($options,"screenitemid").$())) {
			if (!empty(hostid)) {
				screenitem = API.TemplateScreenItem()->get(CArray.array(
					"screenitemids" => Nest.value($options,"screenitemid").$(),
					"hostids" => hostid,
					"output" => API_OUTPUT_EXTEND
				));
			}
			else {
				screenitem = API.ScreenItem()->get(CArray.array(
					"screenitemids" => Nest.value($options,"screenitemid").$(),
					"output" => API_OUTPUT_EXTEND
				));
			}

			screenitem = reset(screenitem);
		}

		// get screenid
		if (empty(screenid) && !empty(screenitem)) {
			screenid = Nest.value(screenitem,"screenid").$();
		}

		// get resourcetype
		if (is_null(resourcetype) && !empty(Nest.value(screenitem,"resourcetype").$())) {
			resourcetype = Nest.value(screenitem,"resourcetype").$();
		}

		// create action url
		if (empty(action)) {
			action = "screenedit.php?form=update&screenid=".screenid."&screenitemid=".Nest.value(screenitem,"screenitemid").$();
		}
	}

	/**
	 * Create and get unique screen id for time control.
	 *
	 * @return string
	 */
	public function getDataId() {
		if (empty(dataId)) {
			dataId = !empty(screenitem) ? screenitem["screenitemid"]."_".Nest.value(screenitem,"screenid").$() : 1;
		}

		return dataId;
	}

	/**
	 * Get unique screen container id.
	 *
	 * @return string
	 */
	public function getScreenId() {
		return "flickerfreescreen_".getDataId();
	}

	/**
	 * Get profile url params.
	 *
	 * @return string
	 */
	public function getProfileUrlParams() {
		return "&updateProfile=".(int) updateProfile."&profileIdx=".profileIdx."&profileIdx2=".profileIdx2;
	}

	/**
	 * Get enveloped screen inside container.
	 *
	 * @param object	$item
	 * @param boolean	$insertFlickerfreeJs
	 * @param array		$flickerfreeData
	 *
	 * @return CDiv
	 */
	public function getOutput($item = null, $insertFlickerfreeJs = true, $flickerfreeData = CArray.array()) {
		if ($insertFlickerfreeJs) {
			insertFlickerfreeJs($flickerfreeData);
		}

		if (mode == SCREEN_MODE_EDIT) {
			$div = new CDiv(CArray.array($item, BR(), new CLink(_("Change"), action)), "flickerfreescreen", getScreenId());
		}
		else {
			$div = new CDiv($item, "flickerfreescreen", getScreenId());
		}

		$div.setAttribute("data-timestamp", timestamp);
		$div.addStyle("position: relative;");

		return $div;
	}

	/**
	 * Insert javascript flicker-free screen data.
	 *
	 * @param array $data
	 */
	public function insertFlickerfreeJs($data = CArray.array()) {
		$jsData = CArray.array(
			"id" => getDataId(),
			"isFlickerfree" => isFlickerfree,
			"pageFile" => pageFile,
			"resourcetype" => resourcetype,
			"mode" => mode,
			"timestamp" => timestamp,
			"interval" => CWebUser::Nest.value($data,"refresh").$(),
			"screenitemid" => !empty(Nest.value(screenitem,"screenitemid").$()) ? Nest.value(screenitem,"screenitemid").$() : null,
			"screenid" => !empty(Nest.value(screenitem,"screenid").$()) ? Nest.value(screenitem,"screenid").$() : screenid,
			"groupid" => groupid,
			"hostid" => hostid,
			"timeline" => timeline,
			"profileIdx" => profileIdx,
			"profileIdx2" => profileIdx2,
			"updateProfile" => updateProfile,
			"data" => !empty($data) ? $data : null
		);

		zbx_add_post_js("window.flickerfreeScreen.add(".zbx_jsvalue($jsData).");");
	}

	/**
	 * Insert javascript flicker-free screen data.
	 *
	 * @static
	 *
	 * @param array		$options
	 * @param string	$options["profileIdx"]
	 * @param int		$options["profileIdx2"]
	 * @param boolean	$options["updateProfile"]
	 * @param int		$options["period"]
	 * @param string	$options["stime"]
	 *
	 * @return array
	 */
	public static function calculateTime(array $options = CArray.array()) {
		if (!array_key_exists("updateProfile", $options)) {
			Nest.value($options,"updateProfile").$() = true;
		}
		if (empty(Nest.value($options,"profileIdx2").$())) {
			Nest.value($options,"profileIdx2").$() = 0;
		}

		// show only latest data without update is set only period
		if (!empty(Nest.value($options,"period").$()) && empty(Nest.value($options,"stime").$())) {
			Nest.value($options,"updateProfile").$() = false;
			Nest.value($options,"profileIdx").$() = "";
		}

		// period
		if (empty(Nest.value($options,"period").$())) {
			Nest.value($options,"period").$() = !empty(Nest.value($options,"profileIdx").$())
				? CProfile::get($options["profileIdx"].".period", ZBX_PERIOD_DEFAULT, Nest.value($options,"profileIdx2").$())
				: ZBX_PERIOD_DEFAULT;
		}
		else {
			if (Nest.value($options,"period").$() < ZBX_MIN_PERIOD) {
				show_message(_n("Minimum time period to display is %1$s hour.",
						"Minimum time period to display is %1$s hours.", (int) ZBX_MIN_PERIOD / SEC_PER_HOUR));
				Nest.value($options,"period").$() = ZBX_MIN_PERIOD;
			}
			elseif (Nest.value($options,"period").$() > ZBX_MAX_PERIOD) {
				show_message(_n("Maximum time period to display is %1$s day.",
						"Maximum time period to display is %1$s days.", (int) ZBX_MAX_PERIOD / SEC_PER_DAY));
				Nest.value($options,"period").$() = ZBX_MAX_PERIOD;
			}
		}
		if (Nest.value($options,"updateProfile").$() && !empty(Nest.value($options,"profileIdx").$())) {
			CProfile::update($options["profileIdx"].".period", Nest.value($options,"period").$(), PROFILE_TYPE_INT, Nest.value($options,"profileIdx2").$());
		}

		// stime
		$time = time();
		$usertime = null;
		$stimeNow = null;
		$isNow = 0;

		if (!empty(Nest.value($options,"stime").$())) {
			$stimeUnix = zbxDateToTime(Nest.value($options,"stime").$());

			if ($stimeUnix > $time || zbxAddSecondsToUnixtime(Nest.value($options,"period").$(), $stimeUnix) > $time) {
				$stimeNow = Nest.value($options,"stime").$();
				Nest.value($options,"stime").$() = date(TIMESTAMP_FORMAT, $time - Nest.value($options,"period").$());
				$usertime = date(TIMESTAMP_FORMAT, $time);
				$isNow = 1;
			}
			else {
				$usertime = date(TIMESTAMP_FORMAT, zbxAddSecondsToUnixtime(Nest.value($options,"period").$(), $stimeUnix));
				$isNow = 0;
			}

			if (Nest.value($options,"updateProfile").$() && !empty(Nest.value($options,"profileIdx").$())) {
				CProfile::update($options["profileIdx"].".stime", Nest.value($options,"stime").$(), PROFILE_TYPE_STR, Nest.value($options,"profileIdx2").$());
				CProfile::update($options["profileIdx"].".isnow", $isNow, PROFILE_TYPE_INT, Nest.value($options,"profileIdx2").$());
			}
		}
		else {
			if (!empty(Nest.value($options,"profileIdx").$())) {
				$isNow = CProfile::get($options["profileIdx"].".isnow", null, Nest.value($options,"profileIdx2").$());
				if ($isNow) {
					Nest.value($options,"stime").$() = date(TIMESTAMP_FORMAT, $time - Nest.value($options,"period").$());
					$usertime = date(TIMESTAMP_FORMAT, $time);
					$stimeNow = date(TIMESTAMP_FORMAT, zbxAddSecondsToUnixtime(SEC_PER_YEAR, Nest.value($options,"stime").$()));

					if (Nest.value($options,"updateProfile").$()) {
						CProfile::update($options["profileIdx"].".stime", Nest.value($options,"stime").$(), PROFILE_TYPE_STR, Nest.value($options,"profileIdx2").$());
					}
				}
				else {
					Nest.value($options,"stime").$() = CProfile::get($options["profileIdx"].".stime", null, Nest.value($options,"profileIdx2").$());
					$usertime = date(TIMESTAMP_FORMAT, zbxAddSecondsToUnixtime(Nest.value($options,"period").$(), Nest.value($options,"stime").$()));
				}
			}

			if (empty(Nest.value($options,"stime").$())) {
				Nest.value($options,"stime").$() = date(TIMESTAMP_FORMAT, $time - Nest.value($options,"period").$());
				$usertime = date(TIMESTAMP_FORMAT, $time);
				$stimeNow = date(TIMESTAMP_FORMAT, zbxAddSecondsToUnixtime(SEC_PER_YEAR, Nest.value($options,"stime").$()));
				$isNow = 1;

				if (Nest.value($options,"updateProfile").$() && !empty(Nest.value($options,"profileIdx").$())) {
					CProfile::update($options["profileIdx"].".stime", Nest.value($options,"stime").$(), PROFILE_TYPE_STR, Nest.value($options,"profileIdx2").$());
					CProfile::update($options["profileIdx"].".isnow", $isNow, PROFILE_TYPE_INT, Nest.value($options,"profileIdx2").$());
				}
			}
		}

		return CArray.array(
			"period" => Nest.value($options,"period").$(),
			"stime" => Nest.value($options,"stime").$(),
			"stimeNow" => !empty($stimeNow) ? $stimeNow : Nest.value($options,"stime").$(),
			"starttime" => date(TIMESTAMP_FORMAT, $time - ZBX_MAX_PERIOD),
			"usertime" => $usertime,
			"isNow" => $isNow
		);
	}

	/**
	 * Easy way to view time data.
	 *
	 * @static
	 *
	 * @param array		$options
	 * @param int		$options["period"]
	 * @param string	$options["stime"]
	 * @param string	$options["stimeNow"]
	 * @param string	$options["starttime"]
	 * @param string	$options["usertime"]
	 * @param int		$options["isNow"]
	 */
	public static function debugTime(array $time = CArray.array()) {
		return "period=".zbx_date2age(0, Nest.value($time,"period").$()).", (".$time["period"].")<br/>".
				"starttime=".date("F j, Y, g:i a", zbxDateToTime(Nest.value($time,"starttime").$())).", (".$time["starttime"].")<br/>".
				"stime=".date("F j, Y, g:i a", zbxDateToTime(Nest.value($time,"stime").$())).", (".$time["stime"].")<br/>".
				"stimeNow=".date("F j, Y, g:i a", zbxDateToTime(Nest.value($time,"stimeNow").$())).", (".$time["stimeNow"].")<br/>".
				"usertime=".date("F j, Y, g:i a", zbxDateToTime(Nest.value($time,"usertime").$())).", (".$time["usertime"].")<br/>".
				"isnow=".$time["isNow"]."<br/>";
	}
}
