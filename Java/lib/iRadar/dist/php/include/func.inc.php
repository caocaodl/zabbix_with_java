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


/**
 * Verify that function exists and can be called as a function.
 *
 * @param array		$names
 *
 * @return bool
 */
function zbx_is_callable(array $names) {
	for($names as $name) {
		if (!is_callable($name)) {
			return false;
		}
	}

	return true;
}

/************ REQUEST ************/
function redirect($url) {
	$curl = new Curl($url);
	$curl.setArgument("sid", null);
	header("Location: ".$curl.getUrl());
	exit;
}

function jsRedirect($url, $timeout = null) {
	$script = is_numeric($timeout)
		? "setTimeout(\"window.location=\"".$url."\"\", ".($timeout * 1000).")"
		: "window.location.replace(\"".$url."\");";

	insert_js($script);
}

/**
 * Check if request exist.
 *
 * @param string	$name
 *
 * @return bool
 */
function hasRequest($name) {
	return isset(_REQUEST[$name]);
}

/**
 * Check request, if exist request - return request value, else return default value.
 *
 * @param string	$name
 * @param mixed		$def
 *
 * @return mixed
 */
function getRequest($name, $def = null) {
	return hasRequest($name) ? _REQUEST[$name] : $def;
}

/**
 * Check request, if exist request - return request value, else return default value.
 *
 * @deprecated function, use getRequest() instead
 *
 * @param string	$name
 * @param mixed		$def
 *
 * @return mixed
 */
function get_request($name, $def = null) {
	return getRequest($name, $def);
}

function countRequest($str = null) {
	if (!empty($str)) {
		$count = 0;
		for(_REQUEST as $name => $value) {
			if (strstr($name, $str)) {
				$count++;
			}
		}
		return $count;
	}
	else {
		return count(_REQUEST);
	}
}

/************ COOKIES ************/
function get_cookie($name, $default_value = null) {
	if (isset($_COOKIE[$name])) {
		return $_COOKIE[$name];
	}

	return $default_value;
}

function zbx_setcookie($name, $value, $time = null) {
	setcookie($name, $value, isset($time) ? $time : 0, null, null, HTTPS);
	$_COOKIE[$name] = $value;
}

function zbx_unsetcookie($name) {
	zbx_setcookie($name, null, -99999);
	unset($_COOKIE[$name]);
}

/************* DATE *************/
function getMonthCaption($num) {
	switch ($num) {
		case 1: return _("January");
		case 2: return _("February");
		case 3: return _("March");
		case 4: return _("April");
		case 5: return _("May");
		case 6: return _("June");
		case 7: return _("July");
		case 8: return _("August");
		case 9: return _("September");
		case 10: return _("October");
		case 11: return _("November");
		case 12: return _("December");
	}

	return _s("[Wrong value for month: \"%s\" ]", $num);
}

function getDayOfWeekCaption($num) {
	switch ($num) {
		case 1: return _("Monday");
		case 2: return _("Tuesday");
		case 3: return _("Wednesday");
		case 4: return _("Thursday");
		case 5: return _("Friday");
		case 6: return _("Saturday");
		case 0:
		case 7: return _("Sunday");
	}

	return _s("[Wrong value for day: \"%s\" ]", $num);
}

// Convert seconds (0..SEC_PER_WEEK) to string representation. For example, 212400 -> "Tuesday 11:00"
function dowHrMinToStr($value, $display24Hours = false) {
	$dow = $value - $value % SEC_PER_DAY;
	$hr = $value - $dow;
	$hr -= $hr % SEC_PER_HOUR;
	$min = $value - $dow - $hr;
	$min -= $min % SEC_PER_MIN;

	$dow /= SEC_PER_DAY;
	$hr /= SEC_PER_HOUR;
	$min /= SEC_PER_MIN;

	if ($display24Hours && $hr == 0 && $min == 0) {
		$dow--;
		$hr = 24;
	}

	return sprintf("%s %02d:%02d", getDayOfWeekCaption($dow), $hr, $min);
}

// Convert Day Of Week, Hours and Minutes to seconds representation. For example, 2 11:00 -> 212400. false if error occurred
function dowHrMinToSec($dow, $hr, $min) {
	if (zbx_empty($dow) || zbx_empty($hr) || zbx_empty($min) || !zbx_ctype_digit($dow) || !zbx_ctype_digit($hr) || !zbx_ctype_digit($min)) {
		return false;
	}

	if ($dow == 7) {
		$dow = 0;
	}

	if ($dow < 0 || $dow > 6) {
		return false;
	}

	if ($hr < 0 || $hr > 24) {
		return false;
	}

	if ($min < 0 || $min > 59) {
		return false;
	}

	return $dow * SEC_PER_DAY + $hr * SEC_PER_HOUR + $min * SEC_PER_MIN;
}

// Convert timestamp to string representation. Return "Never" if 0.
function zbx_date2str($format, $value = null) {
	static $weekdaynames, $weekdaynameslong, $months, $monthslong;

	$prefix = "";

	if ($value === null) {
		$value = time();
	}
	elseif ($value > ZBX_MAX_DATE) {
		$prefix = "> ";
		$value = ZBX_MAX_DATE;
	}
	elseif (!$value) {
		return _("Never");
	}

	if (!is_array($weekdaynames)) {
		$weekdaynames = CArray.array(
			0 => _("Sun"),
			1 => _("Mon"),
			2 => _("Tue"),
			3 => _("Wed"),
			4 => _("Thu"),
			5 => _("Fri"),
			6 => _("Sat")
		);
	}

	if (!is_array($weekdaynameslong)) {
		$weekdaynameslong = CArray.array(
			0 => _("Sunday"),
			1 => _("Monday"),
			2 => _("Tuesday"),
			3 => _("Wednesday"),
			4 => _("Thursday"),
			5 => _("Friday"),
			6 => _("Saturday")
		);
	}

	if (!is_array($months)) {
		$months = CArray.array(
			1 => _("Jan"),
			2 => _("Feb"),
			3 => _("Mar"),
			4 => _("Apr"),
			5 => _x("May", "May short"),
			6 => _("Jun"),
			7 => _("Jul"),
			8 => _("Aug"),
			9 => _("Sep"),
			10 => _("Oct"),
			11 => _("Nov"),
			12 => _("Dec")
		);
	}

	if (!is_array($monthslong)) {
		$monthslong = CArray.array(
			1 => _("January"),
			2 => _("February"),
			3 => _("March"),
			4 => _("April"),
			5 => _("May"),
			6 => _("June"),
			7 => _("July"),
			8 => _("August"),
			9 => _("September"),
			10 => _("October"),
			11 => _("November"),
			12 => _("December")
		);
	}

	$rplcs = CArray.array(
		"l" => $weekdaynameslong[date("w", $value)],
		"F" => $monthslong[date("n", $value)],
		"D" => $weekdaynames[date("w", $value)],
		"M" => $months[date("n", $value)]
	);

	$output = $part = "";
	$length = zbx_strlen($format);

	for ($i = 0; $i < $length; $i++) {
		$pchar = ($i > 0) ? zbx_substr($format, $i - 1, 1) : "";
		$char = zbx_substr($format, $i, 1);

		if ($pchar != "\\" && isset($rplcs[$char])) {
			$output .= (zbx_strlen($part) ? date($part, $value) : "").$rplcs[$char];
			$part = "";
		}
		else {
			$part .= $char;
		}
	}

	$output .= (zbx_strlen($part) > 0) ? date($part, $value) : "";

	return $prefix.$output;
}

// calculate and convert timestamp to string representation
function zbx_date2age($startDate, $endDate = 0, $utime = false) {
	if (!$utime) {
		$startDate = date("U", $startDate);
		$endDate = $endDate ? date("U", $endDate) : time();
	}

	return convertUnitsS(abs($endDate - $startDate));
}

function zbxDateToTime($strdate) {
	if (6 == sscanf($strdate, "%04d%02d%02d%02d%02d%02d", $year, $month, $date, $hours, $minutes, $seconds)) {
		return mktime($hours, $minutes, $seconds, $month, $date, $year);
	}
	elseif (5 == sscanf($strdate, "%04d%02d%02d%02d%02d", $year, $month, $date, $hours, $minutes)) {
		return mktime($hours, $minutes, 0, $month, $date, $year);
	}
	else {
		return ($strdate && is_numeric($strdate)) ? $strdate : time();
	}
}

/**
 * Correcting adding one unix timestamp to another.
 *
 * @param int		$sec
 * @param mixed		$unixtime	Can accept values:
 *									1) int - unix timestamp,
 *									2) string - date in YmdHis or YmdHi formats,
 *									3) null - current unixtime stamp will be used
 *
 * @return int
 */
function zbxAddSecondsToUnixtime($sec, $unixtime) {
	return strtotime("+".$sec." seconds", zbxDateToTime($unixtime));
}

/*************** CONVERTING ******************/
function rgb2hex($color) {
	$HEX = CArray.array(
		dechex($color[0]),
		dechex($color[1]),
		dechex($color[2])
	);
	for($HEX as $id => $value) {
		if (zbx_strlen($value) != 2) {
			$HEX[$id] = "0".$value;
		}
	}

	return $HEX[0].$HEX[1].$HEX[2];
}

function hex2rgb($color) {
	if ($color[0] == "#") {
		$color = substr($color, 1);
	}

	if (zbx_strlen($color) == 6) {
		list($r, $g, $b) = CArray.array($color[0].$color[1], $color[2].$color[3], $color[4].$color[5]);
	}
	elseif (zbx_strlen($color) == 3) {
		list($r, $g, $b) = CArray.array($color[0].$color[0], $color[1].$color[1], $color[2].$color[2]);
	}
	else {
		return false;
	}

	return CArray.array(hexdec($r), hexdec($g), hexdec($b));
}

function zbx_num2bitstr($num, $rev = false) {
	if (!is_numeric($num)) {
		return 0;
	}

	$sbin = 0;
	$strbin = "";

	$len = 32;
	if ($num > 2147483647) {
		$len = 64;
	}

	for ($i = 0; $i < $len; $i++) {
		$sbin = 1 << $i;
		$bit = ($sbin & $num) ? "1" : "0";
		if ($rev) {
			$strbin .= $bit;
		}
		else {
			$strbin = $bit.$strbin;
		}
	}

	return $strbin;
}

/**
 * Converts strings like 2M or 5k to bytes
 *
 * @param string $val
 *
 * @return int
 */
function str2mem($val) {
	$val = trim($val);
	$last = strtolower(substr($val, -1));

	switch ($last) {
		case "g":
			$val *= 1024;
			/* falls through */
		case "m":
			$val *= 1024;
			/* falls through */
		case "k":
			$val *= 1024;
	}

	return $val;
}

function mem2str($size) {
	$prefix = _x("B", "Byte short");
	if ($size > 1048576) {
		$size = $size / 1048576;
		$prefix = _x("M", "Mega short");
	}
	elseif ($size > 1024) {
		$size = $size / 1024;
		$prefix = _x("K", "Kilo short");
	}

	return round($size, 6).$prefix;
}

function convertUnitsUptime($value) {
	if (($secs = round($value)) < 0) {
		$value = "-";
		$secs = -$secs;
	}
	else {
		$value = "";
	}

	$days = floor($secs / SEC_PER_DAY);
	$secs -= $days * SEC_PER_DAY;

	$hours = floor($secs / SEC_PER_HOUR);
	$secs -= $hours * SEC_PER_HOUR;

	$mins = floor($secs / SEC_PER_MIN);
	$secs -= $mins * SEC_PER_MIN;

	if ($days != 0) {
		$value .= _n("%1$d day", "%1$d days", $days).", ";
	}
	$value .= sprintf("%02d:%02d:%02d", $hours, $mins, $secs);

	return $value;
}

/**
 * Converts a time period to a human-readable format.
 *
 * The following units are used: years, months, days, hours, minutes, seconds and milliseconds.
 *
 * Only the three highest units are displayed: #y #m #d, #m #d #h, #d #h #mm and so on.
 *
 * If some value is equal to zero, it is omitted. For example, if the period is 1y 0m 4d, it will be displayed as
 * 1y 4d, not 1y 0m 4d or 1y 4d #h.
 *
 * @param int $value	time period in seconds
 * @param bool $ignoreMillisec	without ms (1s 200 ms = 1.2s)
 *
 * @return string
 */
function convertUnitsS($value, $ignoreMillisec = false) {
	if (($secs = round($value * 1000, ZBX_UNITS_ROUNDOFF_UPPER_LIMIT) / 1000) < 0) {
		$secs = -$secs;
		$str = "-";
	}
	else {
		$str = "";
	}

	$values = CArray.array("y" => null, "m" => null, "d" => null, "h" => null, "mm" => null, "s" => null, "ms" => null);
	$n_unit = 0;

	if (($n = floor($secs / SEC_PER_YEAR)) != 0) {
		$secs -= $n * SEC_PER_YEAR;
		if ($n_unit == 0) {
			$n_unit = 4;
		}
		Nest.value($values,"y").$() = $n;
	}

	if (($n = floor($secs / SEC_PER_MONTH)) != 0) {
		$secs -= $n * SEC_PER_MONTH;
		// due to imprecise calculations it is possible that the remainder contains 12 whole months but no whole years
		if ($n == 12) {
			$values["y"]++;
			Nest.value($values,"m").$() = null;
			if ($n_unit == 0) {
				$n_unit = 4;
			}
		}
		else {
			Nest.value($values,"m").$() = $n;
			if ($n_unit == 0) {
				$n_unit = 3;
			}
		}
	}

	if (($n = floor($secs / SEC_PER_DAY)) != 0) {
		$secs -= $n * SEC_PER_DAY;
		Nest.value($values,"d").$() = $n;
		if ($n_unit == 0) {
			$n_unit = 2;
		}
	}

	if ($n_unit < 4 && ($n = floor($secs / SEC_PER_HOUR)) != 0) {
		$secs -= $n * SEC_PER_HOUR;
		Nest.value($values,"h").$() = $n;
		if ($n_unit == 0) {
			$n_unit = 1;
		}
	}

	if ($n_unit < 3 && ($n = floor($secs / SEC_PER_MIN)) != 0) {
		$secs -= $n * SEC_PER_MIN;
		Nest.value($values,"mm").$() = $n;
	}

	if ($n_unit < 2 && ($n = floor($secs)) != 0) {
		$secs -= $n;
		Nest.value($values,"s").$() = $n;
	}

	if ($ignoreMillisec) {
		if ($n_unit < 1 && ($n = round($secs, ZBX_UNITS_ROUNDOFF_UPPER_LIMIT)) != 0) {
			Nest.value($values,"s").$() += $n;
		}
	}
	else {
		if ($n_unit < 1 && ($n = round($secs * 1000, ZBX_UNITS_ROUNDOFF_UPPER_LIMIT)) != 0) {
			Nest.value($values,"ms").$() = $n;
		}
	}

	$str .= isset(Nest.value($values,"y").$()) ? $values["y"]._x("y", "year short")." " : "";
	$str .= isset(Nest.value($values,"m").$()) ? $values["m"]._x("m", "month short")." " : "";
	$str .= isset(Nest.value($values,"d").$()) ? $values["d"]._x("d", "day short")." " : "";
	$str .= isset(Nest.value($values,"h").$()) ? $values["h"]._x("h", "hour short")." " : "";
	$str .= isset(Nest.value($values,"mm").$()) ? $values["mm"]._x("m", "minute short")." " : "";
	$str .= isset(Nest.value($values,"s").$()) ? $values["s"]._x("s", "second short")." " : "";
	$str .= isset(Nest.value($values,"ms").$()) ? $values["ms"]._x("ms", "millisecond short") : "";

	return $str ? rtrim($str) : 0;
}

/**
 * Converts value to actual value.
 * Example:
 * 	6442450944 B convert to 6 GB
 *
 * @param array  $options
 * @param string $options["value"]
 * @param string $options["units"]
 * @param string $options["convert"]
 * @param string $options["byteStep"]
 * @param string $options["pow"]
 * @param bool   $options["ignoreMillisec"]
 * @param string $options["length"]
 *
 * @return string
 */
function convert_units($options = CArray.array()) {
	$defOptions = CArray.array(
		"value" => null,
		"units" => null,
		"convert" => ITEM_CONVERT_WITH_UNITS,
		"byteStep" => false,
		"pow" => false,
		"ignoreMillisec" => false,
		"length" => false
	);

	$options = zbx_array_merge($defOptions, $options);

	// special processing for unix timestamps
	if (Nest.value($options,"units").$() == "unixtime") {
		return zbx_date2str(_("Y.m.d H:i:s"), Nest.value($options,"value").$());
	}

	// special processing of uptime
	if (Nest.value($options,"units").$() == "uptime") {
		return convertUnitsUptime(Nest.value($options,"value").$());
	}

	// special processing for seconds
	if (Nest.value($options,"units").$() == "s") {
		return convertUnitsS(Nest.value($options,"value").$(), Nest.value($options,"ignoreMillisec").$());
	}

	// any other unit
	// black list of units that should have no multiplier prefix (K, M, G etc) applied
	$blackList = CArray.array("%", "ms", "rpm", "RPM");

	if (in_CArray.array(Nest.value($options,"units").$(), $blackList) || (zbx_empty(Nest.value($options,"units").$())
			&& (Nest.value($options,"convert").$() == ITEM_CONVERT_WITH_UNITS))) {
		if (abs(Nest.value($options,"value").$()) >= ZBX_UNITS_ROUNDOFF_THRESHOLD) {
			Nest.value($options,"value").$() = round(Nest.value($options,"value").$(), ZBX_UNITS_ROUNDOFF_UPPER_LIMIT);
		}
		Nest.value($options,"value").$() = sprintf("%.".ZBX_UNITS_ROUNDOFF_LOWER_LIMIT."f", Nest.value($options,"value").$());
		Nest.value($options,"value").$() = preg_replace("/^([\-0-9]+)(\.)([0-9]*)[0]+$/U", "$1$2$3", Nest.value($options,"value").$());
		Nest.value($options,"value").$() = rtrim(Nest.value($options,"value").$(), ".");

		return trim($options["value"]." ".Nest.value($options,"units").$());
	}

	// if one or more items is B or Bps, then Y-scale use base 8 and calculated in bytes
	if (Nest.value($options,"byteStep").$()) {
		$step = 1024;
	}
	else {
		switch (Nest.value($options,"units").$()) {
			case "Bps":
			case "B":
				$step = 1024;
				Nest.value($options,"convert").$() = Nest.value($options,"convert").$() ? Nest.value($options,"convert").$() : ITEM_CONVERT_NO_UNITS;
				break;
			case "b":
			case "bps":
				Nest.value($options,"convert").$() = Nest.value($options,"convert").$() ? Nest.value($options,"convert").$() : ITEM_CONVERT_NO_UNITS;
			default:
				$step = 1000;
		}
	}

	if (Nest.value($options,"value").$() < 0) {
		$abs = bcmul(Nest.value($options,"value").$(), "-1");
	}
	else {
		$abs = Nest.value($options,"value").$();
	}

	if (bccomp($abs, 1) == -1) {
		Nest.value($options,"value").$() = round(Nest.value($options,"value").$(), ZBX_UNITS_ROUNDOFF_MIDDLE_LIMIT);
		Nest.value($options,"value").$() = (Nest.value($options,"length").$() && Nest.value($options,"value").$() != 0)
			? sprintf("%.".$options["length"]."f",Nest.value($options,"value").$()) : Nest.value($options,"value").$();

		return trim($options["value"]." ".Nest.value($options,"units").$());
	}

	// init intervals
	static $digitUnits;
	if (is_null($digitUnits)) {
		$digitUnits = CArray.array();
	}

	if (!isset($digitUnits[$step])) {
		$digitUnits[$step] = CArray.array(
			CArray.array("pow" => 0, "short" => "", "long" => ""),
			CArray.array("pow" => 1, "short" => _x("K", "Kilo short"), "long" => _("Kilo")),
			CArray.array("pow" => 2, "short" => _x("M", "Mega short"), "long" => _("Mega")),
			CArray.array("pow" => 3, "short" => _x("G", "Giga short"), "long" => _("Giga")),
			CArray.array("pow" => 4, "short" => _x("T", "Tera short"), "long" => _("Tera")),
			CArray.array("pow" => 5, "short" => _x("P", "Peta short"), "long" => _("Peta")),
			CArray.array("pow" => 6, "short" => _x("E", "Exa short"), "long" => _("Exa")),
			CArray.array("pow" => 7, "short" => _x("Z", "Zetta short"), "long" => _("Zetta")),
			CArray.array("pow" => 8, "short" => _x("Y", "Yotta short"), "long" => _("Yotta"))
		);

		for($digitUnits[$step] as $dunit => $data) {
			// skip milli & micro for values without units
			$digitUnits[$step][$dunit]["value"] = bcpow($step, Nest.value($data,"pow").$(), 9);
		}
	}


	$valUnit = CArray.array("pow" => 0, "short" => "", "long" => "", "value" => Nest.value($options,"value").$());

	if (Nest.value($options,"pow").$() === false || Nest.value($options,"value").$() == 0) {
		for($digitUnits[$step] as $dnum => $data) {
			if (bccomp($abs, Nest.value($data,"value").$()) > -1) {
				$valUnit = $data;
			}
			else {
				break;
			}
		}
	}
	else {
		for($digitUnits[$step] as $data) {
			if (Nest.value($options,"pow").$() == Nest.value($data,"pow").$()) {
				$valUnit = $data;
				break;
			}
		}
	}

	if (round(Nest.value($valUnit,"value").$(), ZBX_UNITS_ROUNDOFF_MIDDLE_LIMIT) > 0) {
		Nest.value($valUnit,"value").$() = bcdiv(sprintf("%.10f",Nest.value($options,"value").$()), sprintf("%.10f", Nest.value($valUnit,"value").$())
			, ZBX_PRECISION_10);
	}
	else {
		Nest.value($valUnit,"value").$() = 0;
	}

	switch (Nest.value($options,"convert").$()) {
		case 0: Nest.value($options,"units").$() = trim(Nest.value($options,"units").$());
		case 1: $desc = Nest.value($valUnit,"short").$(); break;
		case 2: $desc = Nest.value($valUnit,"long").$(); break;
	}

	Nest.value($options,"value").$() = preg_replace("/^([\-0-9]+)(\.)([0-9]*)[0]+$/U","$1$2$3", round(Nest.value($valUnit,"value").$(),
		ZBX_UNITS_ROUNDOFF_UPPER_LIMIT));

	Nest.value($options,"value").$() = rtrim(Nest.value($options,"value").$(), ".");

	// fix negative zero
	if (bccomp(Nest.value($options,"value").$(), 0) == 0) {
		Nest.value($options,"value").$() = 0;
	}

	return trim(sprintf("%s %s%s", $options["length"]
		? sprintf("%.".$options["length"]."f",Nest.value($options,"value").$())
		: Nest.value($options,"value").$(), $desc, Nest.value($options,"units").$()));
}

/**
 * Converts value with suffix to actual value.
 * Supported time suffixes: s, m, h, d, w
 * Supported metric suffixes: K, M, G, T
 *
 * @param string $value
 *
 * @return string
 */
function convertFunctionValue($value) {
	$suffix = $value[strlen($value) - 1];
	if (!ctype_digit($suffix)) {
		$value = substr($value, 0, strlen($value) - 1);

		switch ($suffix) {
			case "s":
				break;
			case "m":
				$value = bcmul($value, "60");
				break;
			case "h":
				$value = bcmul($value, "3600");
				break;
			case "d":
				$value = bcmul($value, "86400");
				break;
			case "w":
				$value = bcmul($value, "604800");
				break;
			case "K":
				$value = bcmul($value, "1024");
				break;
			case "M":
				$value = bcmul($value, "1048576");
				break;
			case "G":
				$value = bcmul($value, "1073741824");
				break;
			case "T":
				$value = bcmul($value, "1099511627776");
				break;
		}
	}

	return $value;
}

/************* ZBX MISC *************/

/**
 * Swap two values.
 *
 * @param mixed $a first value
 * @param mixed $b second value
 */
function zbx_swap(&$a, &$b) {
	$tmp = $a;
	$a = $b;
	$b = $tmp;
}

function zbx_avg($values) {
	zbx_value2CArray.array($values);
	$sum = 0;
	for($values as $value) {
		$sum = bcadd($sum, $value);
	}

	return bcdiv($sum, count($values));
}

// accepts parameter as integer either
function zbx_ctype_digit($x) {
	return ctype_digit(strval($x));
}

function zbx_empty($value) {
	if ($value === null) {
		return true;
	}
	if (is_array($value) && empty($value)) {
		return true;
	}
	if (is_string($value) && $value === "") {
		return true;
	}

	return false;
}

function zbx_is_int($var) {
	if (is_int($var)) {
		return true;
	}

	if (is_string($var)) {
		if (function_exists("ctype_digit") && ctype_digit($var) || strcmp(intval($var), $var) == 0) {
			return true;
		}
	}
	else {
		if ($var > 0 && zbx_ctype_digit($var)) {
			return true;
		}
	}

	return preg_match(\"/^\-?\d{1,20}+$/\", $var);
}

/**
 * Look for two arrays field value and create 3 array lists, one with arrays where field value exists only in first array
 * second with arrays where field values are only in second array and both where fiel values are in both arrays.
 *
 * @param array  $primary
 * @param array  $secondary
 * @param string $field field that is searched in arrays
 *
 * @return array
 */
function zbx_array_diff(array $primary, array $secondary, $field) {
	$fields1 = zbx_objectValues($primary, $field);
	$fields2 = zbx_objectValues($secondary, $field);

	$first = array_diff($fields1, $fields2);
	$first = zbx_toHash($first);

	$second = array_diff($fields2, $fields1);
	$second = zbx_toHash($second);

	$result = CArray.array(
		"first" => CArray.array(),
		"second" => CArray.array(),
		"both" => CArray.array()
	);

	for($primary as $array) {
		if (!isset($array[$field])) {
			$result["first"][] = $array;
		}
		elseif (isset($first[$array[$field]])) {
			$result["first"][] = $array;
		}
		else {
			$result["both"][$array[$field]] = $array;
		}
	}

	for($secondary as $array) {
		if (!isset($array[$field])) {
			$result["second"][] = $array;
		}
		elseif (isset($second[$array[$field]])) {
			$result["second"][] = $array;
		}
	}

	return $result;
}

function zbx_array_push(&$array, $add) {
	for($array as $key => $value) {
		for($add as $newKey => $newValue) {
			$array[$key][$newKey] = $newValue;
		}
	}
}

/**
 * Find if array has any duplicate values and return an array with info about them.
 * In case of no duplicates, empty array is returned.
 * Example of usage:
 *     $result = zbx_arrayFindDuplicates(
 *         CArray.array("a", "b", "c", "c", "d", "d", "d", "e")
 *     );
 *     CArray.array(
 *         "d" => 3,
 *         "c" => 2,
 *     )
 *
 * @param array $array
 *
 * @return array
 */
function zbx_arrayFindDuplicates(array $array) {
	$countValues = array_count_values($array); // counting occurrences of every value in array
	for($countValues as $value => $count) {
		if ($count <= 1) {
			unset($countValues[$value]);
		}
	}
	arsort($countValues); // sorting, so that the most duplicates would be at the top

	return $countValues;
}

/************* STRING *************/
function zbx_nl2br($str) {
	$str_res = CArray.array();
	$str_arr = explode(\"\n\", $str);
	for($str_arr as $id => $str_line) {
		array_push($str_res, $str_line, BR());
	}

	return $str_res;
}

function zbx_formatDomId($value) {
	return str_replace(CArray.array("[", "]"), CArray.array("_", ""), $value);
}

function zbx_strlen($str) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		return mb_strlen($str);
	}
	else {
		return strlen($str);
	}
}

function zbx_strstr($haystack, $needle) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		$pos = mb_strpos($haystack, $needle);
		if ($pos !== false) {
			return mb_substr($haystack, $pos);
		}
		else {
			return false;
		}
	}
	else {
		return strstr($haystack, $needle);
	}
}

function zbx_stristr($haystack, $needle) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		$haystack_B = mb_strtoupper($haystack);
		$needle = mb_strtoupper($needle);

		$pos = mb_strpos($haystack_B, $needle);
		if ($pos !== false) {
			$pos = mb_substr($haystack, $pos);
		}
		return $pos;
	}
	else {
		return stristr($haystack, $needle);
	}
}

function zbx_substring($haystack, $start, $end = null) {
	if (!is_null($end) && $end < $start) {
		return "";
	}

	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		if (is_null($end)) {
			$result = mb_substr($haystack, $start);
		}
		else {
			$result = mb_substr($haystack, $start, ($end - $start));
		}
	}
	else {
		if (is_null($end)) {
			$result = substr($haystack, $start);
		}
		else {
			$result = substr($haystack, $start, ($end - $start));
		}
	}

	return $result;
}

function zbx_substr($string, $start, $length = null) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		if (is_null($length)) {
			$result = mb_substr($string, $start);
		}
		else {
			$result = mb_substr($string, $start, $length);
		}
	}
	else {
		if (is_null($length)) {
			$result = substr($string, $start);
		}
		else {
			$result = substr($string, $start, $length);
		}
	}

	return $result;
}

function zbx_str_revert($str) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		$result = "";
		$stop = mb_strlen($str);
		for ($idx = 0; $idx < $stop; $idx++) {
			$result = mb_substr($str, $idx, 1).$result;
		}
	}
	else {
		$result = strrev($str);
	}

	return $result;
}

function zbx_strtoupper($str) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		return mb_strtoupper($str);
	}
	else {
		return strtoupper($str);
	}
}

function zbx_strtolower($str) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		return mb_strtolower($str);
	}
	else {
		return strtolower($str);
	}
}

function zbx_strpos($haystack, $needle, $offset = 0) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		return mb_strpos($haystack, $needle, $offset);
	}
	else {
		return strpos($haystack, $needle, $offset);
	}
}

function zbx_stripos($haystack, $needle, $offset = 0) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		$haystack = mb_convert_case($haystack, MB_CASE_LOWER);
		$needle = mb_convert_case($needle, MB_CASE_LOWER);
		return mb_strpos($haystack, $needle, $offset);
	}
	else {
		return stripos($haystack, $needle, $offset);
	}
}

function zbx_strrpos($haystack, $needle) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		return mb_strrpos($haystack, $needle);
	}
	else {
		return strrpos($haystack, $needle);
	}
}

function zbx_substr_replace($string, $replacement, $start, $length = null) {
	if (defined("ZBX_MBSTRINGS_ENABLED")) {
		$string_length = mb_strlen($string);

		if ($start < 0) {
			$start = max(0, $string_length + $start);
		}
		elseif ($start > $string_length) {
			$start = $string_length;
		}

		if ($length < 0) {
			$length = max(0, $string_length - $start + $length);
		}
		elseif ($length === null || $length > $string_length) {
			$length = $string_length;
		}

		if (($start + $length) > $string_length) {
			$length = $string_length - $start;
		}

		return mb_substr($string, 0, $start) . $replacement . mb_substr($string, $start + $length, $string_length - $start - $length);
	}
	else {
		return substr_replace($string, $replacement, $start, $length);
	}
}

function str_replace_first($search, $replace, $subject) {
	$pos = zbx_strpos($subject, $search);
	if ($pos !== false) {
		$subject = zbx_substr_replace($subject, $replace, $pos, zbx_strlen($search));
	}
	return $subject;
}

/************* SELECT *************/
function selectByPattern(&$table, $column, $pattern, $limit) {
	$chunk_size = $limit;

	$rsTable = CArray.array();
	for($table as $num => $row) {
		if (zbx_strtoupper($row[$column]) == zbx_strtoupper($pattern)) {
			$rsTable = CArray.array($num => $row) + $rsTable;
		}
		elseif ($limit > 0) {
			$rsTable[$num] = $row;
		}
		else {
			continue;
		}
		$limit--;
	}

	if (!empty($rsTable)) {
		$rsTable = array_chunk($rsTable, $chunk_size, true);
		$rsTable = $rsTable[0];
	}

	return $rsTable;
}

/************* SORT *************/
function natksort(&$array) {
	$keys = array_keys($array);
	natcasesort($keys);

	$new_array = CArray.array();

	for($keys as $k) {
		$new_array[$k] = $array[$k];
	}

	$array = $new_array;

	return true;
}

function asort_by_key(&$array, $key) {
	if (!is_array($array)) {
		error(_("Incorrect type of asort_by_key."));
		return CArray.array();
	}
	$key = htmlspecialchars($key);
	uasort($array, create_function("$a,$b", "return $a[\"".$key."\"] - $b[\"".$key."\"];"));

	return $array;
}

// recursively sort an array by key
function zbx_rksort(&$array, $flags = null) {
	if (is_array($array)) {
		for($array as $id => $data) {
			zbx_rksort($array[$id]);
		}
		ksort($array, $flags);
	}

	return $array;
}

/**
 * Sorts the data using a natural sort algorithm.
 *
 * Not suitable for sorting macros, use order_macros() instead.
 *
 * @param $data
 * @param null $sortfield
 * @param string $sortorder
 *
 * @return bool
 *
 * @see order_macros()
 */
function order_result(&$data, $sortfield = null, $sortorder = ZBX_SORT_UP) {
	if (empty($data)) {
		return false;
	}

	if (is_null($sortfield)) {
		natcasesort($data);
		if ($sortorder != ZBX_SORT_UP) {
			$data = array_reverse($data, true);
		}
		return true;
	}

	$sort = CArray.array();
	for($data as $key => $arr) {
		if (!isset($arr[$sortfield])) {
			return false;
		}
		$sort[$key] = $arr[$sortfield];
	}
	natcasesort($sort);

	if ($sortorder != ZBX_SORT_UP) {
		$sort = array_reverse($sort, true);
	}

	$tmp = $data;
	$data = CArray.array();
	for($sort as $key => $val) {
		$data[$key] = $tmp[$key];
	}

	return true;
}

function order_by($def, $allways = "") {
	$orderString = "";

	$sortField = getPageSortField();
	$sortable = explode(",", $def);
	if (!str_in_array($sortField, $sortable)) {
		$sortField = null;
	}
	if ($sortField !== null) {
		$sortOrder = getPageSortOrder();
		$orderString .= $sortField." ".$sortOrder;
	}
	if (!empty($allways)) {
		$orderString .= ($sortField === null) ? "" : ",";
		$orderString .= $allways;
	}

	return empty($orderString) ? "" : " ORDER BY ".$orderString;
}

/**
 * Sorts the macros in the given order.
 *
 * order_result() is not suitable for sorting macros, because it treats the \"}\" as a symbol with a lower priority
 * then any alphanumeric character, and the result will be invalid.
 *
 * E.g: order_result() will sort CArray.array("{$DD}", "{$D}", "{$D1}") as
 * CArray.array("{$D1}", "{$DD}", "{$D}") while the correct result is CArray.array("{$D}", "{$D1}", "{$DD}").
 *
 * @param array $macros
 * @param string $sortfield
 * @param string $order
 *
 * @return array
 */
function order_macros(array $macros, $sortfield, $order = ZBX_SORT_UP) {
	$temp = CArray.array();
	for($macros as $key => $macro) {
		$temp[$key] = preg_replace(ZBX_PREG_EXPRESSION_USER_MACROS, "$1", $macro[$sortfield]);
	}
	order_result($temp, null, $order);

	$rs = CArray.array();
	for($temp as $key => $macroLabel) {
		$rs[$key] = $macros[$key];
	}

	return $rs;
}

function unsetExcept(&$array, $allowedFields) {
	for($array as $key => $value) {
		if (!isset($allowedFields[$key])) {
			unset($array[$key]);
		}
	}
}

function zbx_implodeHash($glue1, $glue2, $hash) {
	if (is_null($glue2)) {
		$glue2 = $glue1;
	}

	$str = "";
	for($hash as $key => $value) {
		if (!empty($str)) {
			$str .= $glue2;
		}
		$str .= $key.$glue1.$value;
	}

	return $str;
}

// preserve keys
function zbx_array_merge() {
	$args = func_get_args();
	$result = CArray.array();
	for($args as &$array) {
		if (!is_array($array)) {
			return false;
		}
		for($array as $key => $value) {
			$result[$key] = $value;
		}
	}

	return $result;
}

function uint_in_CArray.array($needle, $haystack) {
	for($haystack as $value) {
		if (bccomp($needle, $value) == 0) {
			return true;
		}
	}

	return false;
}

function zbx_uint_array_intersect(&$array1, &$array2) {
	$result = CArray.array();
	for($array1 as $key => $value) {
		if (uint_in_CArray.array($value, $array2)) {
			$result[$key] = $value;
		}
	}

	return $result;
}

function str_in_array($needle, $haystack, $strict = false) {
	if (is_array($needle)) {
		return in_CArray.array($needle, $haystack, $strict);
	}
	elseif ($strict) {
		for($haystack as $value) {
			if ($needle === $value) {
				return true;
			}
		}
	}
	else {
		for($haystack as $value) {
			if (strcmp($needle, $value) == 0) {
				return true;
			}
		}
	}

	return false;
}

function zbx_value2CArray.array(&$values) {
	if (!is_array($values) && !is_null($values)) {
		$tmp = CArray.array();
		if (is_object($values)) {
			$tmp[] = $values;
		}
		else {
			$tmp[$values] = $values;
		}
		$values = $tmp;
	}
}

// creates chain of relation parent -> childs, for all chain levels
function createParentToChildRelation(&$chain, $link, $parentField, $childField) {
	if (!isset($chain[$link[$parentField]])) {
		$chain[$link[$parentField]] = CArray.array();
	}

	$chain[$link[$parentField]][$link[$childField]] = $link[$childField];
	if (isset($chain[$link[$childField]])) {
		$chain[$link[$parentField]] = zbx_array_merge($chain[$link[$parentField]], $chain[$link[$childField]]);
	}
}

// object or array of objects to hash
function zbx_toHash($value, $field = null) {
	if (is_null($value)) {
		return $value;
	}
	$result = CArray.array();

	if (!is_array($value)) {
		$result = CArray.array($value => $value);
	}
	elseif (isset($value[$field])) {
		$result[$value[$field]] = $value;
	}
	else {
		for($value as $val) {
			if (!is_array($val)) {
				$result[$val] = $val;
			}
			elseif (isset($val[$field])) {
				$result[$val[$field]] = $val;
			}
		}
	}

	return $result;
}

/**
 * Transforms a single or an array of values to an array of objects, where the values are stored under the $field
 * key.
 *
 * E.g:
 * zbx_toObject(CArray.array(1, 2), "hostid")  // returns CArray.array(CArray.array("hostid" => 1), CArray.array("hostid" => 2))
 * zbx_toObject(3, "hostid")            // returns CArray.array(CArray.array("hostid" => 3))
 *
 * @param $value
 * @param $field
 *
 * @return array
 */
function zbx_toObject($value, $field) {
	if (is_null($value)) {
		return $value;
	}
	$result = CArray.array();

	// Value or Array to Object or Array of objects
	if (!is_array($value)) {
		$result = CArray.array(CArray.array($field => $value));
	}
	elseif (!isset($value[$field])) {
		for($value as $val) {
			if (!is_array($val)) {
				$result[] = CArray.array($field => $val);
			}
		}
	}

	return $result;
}

/**
 * Converts the given value to a numeric array:
 * - a scalar value will be converted to an array and added as the only element;
 * - an array with first element key containing only numeric characters will be converted to plain zero-based numeric array.
 * This is used for reseting nonsequential numeric arrays;
 * - an associative array will be returned in an array as the only element, except if first element key contains only numeric characters.
 *
 * @param mixed $value
 *
 * @return array
 */
function zbx_toArray($value) {
	if ($value === null) {
		return $value;
	}

	$result = CArray.array();
	if (is_array($value)) {
		// reset() is needed to move internal array pointer to the beginning of the array
		reset($value);

		if (zbx_ctype_digit(key($value))) {
			$result = array_values($value);
		}
		elseif (!empty($value)) {
			$result = CArray.array($value);
		}
	}
	else {
		$result = CArray.array($value);
	}

	return $result;
}

// value OR object OR array of objects TO an array
function zbx_objectValues($value, $field) {
	if (is_null($value)) {
		return $value;
	}
	$result = CArray.array();

	if (!is_array($value)) {
		$result = CArray.array($value);
	}
	elseif (isset($value[$field])) {
		$result = CArray.array($value[$field]);
	}
	else {
		for($value as $val) {
			if (!is_array($val)) {
				$result[] = $val;
			}
			elseif (isset($val[$field])) {
				$result[] = $val[$field];
			}
		}
	}

	return $result;
}

function zbx_cleanHashes(&$value) {
	if (is_array($value)) {
		// reset() is needed to move internal array pointer to the beginning of the array
		reset($value);
		if (zbx_ctype_digit(key($value))) {
			$value = array_values($value);
		}
	}

	return $value;
}

function zbx_toCSV($values) {
	$csv = "";
	$glue = "\",\"";
	for($values as $row) {
		if (!is_array($row)) {
			$row = CArray.array($row);
		}
		for($row as $num => $value) {
			if (is_null($value)) {
				unset($row[$num]);
			}
			else {
				$row[$num] = str_replace("\"", "\"\"", $value);
			}
		}
		$csv .= "\"".implode($glue, $row)."\"".\"\n\";
	}

	return $csv;
}

function zbx_array_mintersect($keys, $array) {
	$result = CArray.array();

	for($keys as $field) {
		if (is_array($field)) {
			for($field as $sub_field) {
				if (isset($array[$sub_field])) {
					$result[$sub_field] = $array[$sub_field];
					break;
				}
			}
		}
		elseif (isset($array[$field])) {
			$result[$field] = $array[$field];
		}
	}

	return $result;
}

function zbx_str2links($text) {
	$result = CArray.array();
	if (zbx_empty($text)) {
		return $result;
	}
	preg_match_all("#https?://[^\n\t\r ]+#u", $text, $matches, PREG_OFFSET_CAPTURE);

	$start = 0;
	for($matches[0] as $match) {
		$result[] = zbx_substr($text, $start, $match[1] - $start);
		$result[] = new CLink($match[0], $match[0], null, null, true);
		$start = $match[1] + zbx_strlen($match[0]);
	}
	$result[] = zbx_substr($text, $start, zbx_strlen($text));

	return $result;
}

function zbx_subarray_push(&$mainArray, $sIndex, $element = null, $key = null) {
	if (!isset($mainArray[$sIndex])) {
		$mainArray[$sIndex] = CArray.array();
	}
	if ($key) {
		$mainArray[$sIndex][$key] = is_null($element) ? $sIndex : $element;
	}
	else {
		$mainArray[$sIndex][] = is_null($element) ? $sIndex : $element;
	}
}

/**
 * Check if two arrays have same values.
 *
 * @param array $a
 * @param array $b
 * @param bool $strict
 *
 * @return bool
 */
function array_equal(array $a, array $b, $strict=false) {
	if (count($a) !== count($b)) {
		return false;
	}

	sort($a);
	sort($b);

	return $strict ? $a === $b : $a == $b;
}

/*************** PAGE SORTING ******************/

/**
 * Get the sort and sort order parameters for the current page and save it into profiles.
 *
 * @param string $sort
 * @param string $sortorder
 *
 * @return void
 */
function validate_sort_and_sortorder($sort = null, $sortorder = ZBX_SORT_UP) {
	global $page;

	Nest.value(_REQUEST,"sort").$() = getPageSortField($sort);
	Nest.value(_REQUEST,"sortorder").$() = getPageSortOrder($sortorder);

	if (!is_null(Nest.value(_REQUEST,"sort").$())) {
		Nest.value(_REQUEST,"sort").$() = preg_replace("/[^a-z\.\_]/i", "", Nest.value(_REQUEST,"sort").$());
		CProfile::update("web.".$page["file"].".sort", Nest.value(_REQUEST,"sort").$(), PROFILE_TYPE_STR);
	}

	if (!str_in_array(Nest.value(_REQUEST,"sortorder").$(), CArray.array(ZBX_SORT_DOWN, ZBX_SORT_UP))) {
		Nest.value(_REQUEST,"sortorder").$() = ZBX_SORT_UP;
	}

	CProfile::update("web.".$page["file"].".sortorder", Nest.value(_REQUEST,"sortorder").$(), PROFILE_TYPE_STR);
}

// creates header col for sorting in table header
function make_sorting_header($obj, $tabfield, $url = "") {
	global $page;

	$sortorder = (Nest.value(_REQUEST,"sort").$() == $tabfield && Nest.value(_REQUEST,"sortorder").$() == ZBX_SORT_UP) ? ZBX_SORT_DOWN : ZBX_SORT_UP;

	$link = new Curl($url);
	if (empty($url)) {
		$link.formatGetArguments();
	}
	$link.setArgument("sort", $tabfield);
	$link.setArgument("sortorder", $sortorder);

	$url = $link.getUrl();

	if (Nest.value($page,"type").$() != PAGE_TYPE_HTML && defined("ZBX_PAGE_MAIN_HAT")) {
		$script = \"javascript: return updater.onetime_update("\".ZBX_PAGE_MAIN_HAT.\"", "\".$url.\"");\";
	}
	else {
		$script = "javascript: redirect(\"".$url."\");";
	}

	zbx_value2CArray.array($obj);
	$cont = new CSpan();

	for($obj as $el) {
		if (is_object($el) || $el === SPACE) {
			$cont.addItem($el);
		}
		else {
			$cont.addItem(new CSpan($el, "underline"));
		}
	}
	$cont.addItem(SPACE);

	$img = null;
	if (isset(Nest.value(_REQUEST,"sort").$()) && $tabfield == Nest.value(_REQUEST,"sort").$()) {
		if ($sortorder == ZBX_SORT_UP) {
			$img = new CSpan(SPACE, "icon_sortdown");
		}
		else {
			$img = new CSpan(SPACE, "icon_sortup");
		}
	}
	$col = new CCol(CArray.array($cont, $img), "nowrap hover_grey");
	$col.setAttribute("onclick", $script);

	return $col;
}

/**
 * Returns the sort field for the current page.
 *
 * @param string $default
 *
 * @return string
 */
function getPageSortField($default = null) {
	global $page;

	$sort = get_request("sort", CProfile::get("web.".$page["file"].".sort"));

	return ($sort) ? $sort : $default;
}

/**
 * Returns the sort order for the current page.
 *
 * @param string $default
 *
 * @return string
 */
function getPageSortOrder($default = ZBX_SORT_UP) {
	global $page;

	$sortorder = get_request("sortorder", CProfile::get("web.".$page["file"].".sortorder", $default));

	return ($sortorder) ? $sortorder : $default;
}

/**
 * Returns the list page number for the current page.
 *
 * The functions first looks for a page number in the HTTP request. If no number is given, falls back to the profile.
 * Defaults to 1.
 *
 * @return int
 */
function getPageNumber() {
	global $page;

	$pageNumber = get_request("page");
	if (!$pageNumber) {
		$lastPage = CProfile::get("web.paging.lastpage");
		$pageNumber = ($lastPage == Nest.value($page,"file").$()) ? CProfile::get("web.paging.page", 1) : 1;
	}

	return $pageNumber;
}

/**
 * Returns paging line.
 *
 * @param array $items				list of items
 * @param array $removeUrlParams	params to remove from URL
 * @param array $urlParams			params to add in URL
 *
 * @return CTable
 */
function getPagingLine(&$items, array $removeUrlParams = CArray.array(), array $urlParams = CArray.array()) {
	global $page;

	$config = select_config();

	$searchLimit = "";
	if (Nest.value($config,"search_limit").$() < count($items)) {
		array_pop($items);
		$searchLimit = "+";
	}

	$rowsPerPage = CWebUser::Nest.value($data,"rows_per_page").$();
	$itemsCount = count($items);
	$pagesCount = ($itemsCount > 0) ? ceil($itemsCount / $rowsPerPage) : 1;

	$currentPage = getPageNumber();
	if ($currentPage < 1) {
		$currentPage = 1;
	}

	if ($itemsCount < (($currentPage - 1) * $rowsPerPage)) {
		$currentPage = $pagesCount;
	}

	$start = ($currentPage - 1) * $rowsPerPage;

	CProfile::update("web.paging.lastpage", Nest.value($page,"file").$(), PROFILE_TYPE_STR);
	CProfile::update("web.paging.page", $currentPage, PROFILE_TYPE_INT);

	// trim array with items to contain items for current page
	$items = array_slice($items, $start, $rowsPerPage, true);

	// viewed pages (better to use not odd)
	$pagingNavRange = 11;

	$endPage = $currentPage + floor($pagingNavRange / 2);
	if ($endPage < $pagingNavRange) {
		$endPage = $pagingNavRange;
	}
	if ($endPage > $pagesCount) {
		$endPage = $pagesCount;
	}

	$startPage = ($endPage > $pagingNavRange) ? $endPage - $pagingNavRange + 1 : 1;

	$pageLine = CArray.array();

	$table = null;

	if ($pagesCount > 1) {
		$url = new Curl();

		if (is_array($urlParams) && $urlParams) {
			for($urlParams as $key => $value) {
				$url.setArgument($key, $value);
			}
		}

		$removeUrlParams = array_merge($removeUrlParams, CArray.array("go", "form", "delete", "cancel"));
		for($removeUrlParams as $param) {
			$url.removeArgument($param);
		}

		if ($startPage > 1) {
			$url.setArgument("page", 1);
			$pageLine[] = new CLink("<< "._x("First", "page navigation"), $url.getUrl(), null, null, true);
			$pageLine[] = "&nbsp;&nbsp;";
		}

		if ($currentPage > 1) {
			$url.setArgument("page", $currentPage - 1);
			$pageLine[] = new CLink("< "._x("Previous", "page navigation"), $url.getUrl(), null, null, true);
			$pageLine[] = " | ";
		}

		for ($p = $startPage; $p <= $pagesCount; $p++) {
			if ($p > $endPage) {
				break;
			}

			if ($p == $currentPage) {
				$pagespan = new CSpan($p, "bold textcolorstyles");
			}
			else {
				$url.setArgument("page", $p);
				$pagespan = new CLink($p, $url.getUrl(), null, null, true);
			}

			$pageLine[] = $pagespan;
			$pageLine[] = " | ";
		}

		array_pop($pageLine);

		if ($currentPage < $pagesCount) {
			$pageLine[] = " | ";

			$url.setArgument("page", $currentPage + 1);
			$pageLine[] = new CLink(_x("Next", "page navigation")." >", $url.getUrl(), null, null, true);
		}

		if ($p < $pagesCount) {
			$pageLine[] = "&nbsp;&nbsp;";

			$url.setArgument("page", $pagesCount);
			$pageLine[] = new CLink(_x("Last", "page navigation")." >>", $url.getUrl(), null, null, true);
		}

		$table = new CTable(null, "paging");
		$table.addRow(new CCol($pageLine));
	}

	$viewFromPage = ($currentPage - 1) * $rowsPerPage + 1;

	$viewTillPage = $currentPage * $rowsPerPage;
	if ($viewTillPage > $itemsCount) {
		$viewTillPage = $itemsCount;
	}

	$pageView = CArray.array();
	$pageView[] = _("Displaying").SPACE;
	if ($itemsCount > 0) {
		$pageView[] = new CSpan($viewFromPage, "info");
		$pageView[] = SPACE._("to").SPACE;
	}

	$pageView[] = new CSpan($viewTillPage, "info");
	$pageView[] = SPACE._("of").SPACE;
	$pageView[] = new CSpan($itemsCount, "info");
	$pageView[] = $searchLimit;
	$pageView[] = SPACE._("found");

	$pageView = new CSpan($pageView);

	zbx_add_post_js("insertInElement(\"numrows\", ".zbx_jsvalue($pageView.toString()).", \"div\");");

	return $table;
}

/************* DYNAMIC REFRESH *************/
function add_doll_objects($ref_tab, $pmid = "mainpage") {
	$upd_script = CArray.array();
	for($ref_tab as $id => $doll) {
		$upd_script[$doll["id"]] = format_doll_init($doll);
	}
	zbx_add_post_js("initPMaster(".zbx_jsvalue($pmid).", ".zbx_jsvalue($upd_script).");");
}

function format_doll_init($doll) {
	$args = CArray.array(
		"frequency" => 60,
		"url" => "",
		"counter" => 0,
		"darken" => 0,
		"params" => CArray.array()
	);
	for($args as $key => $def) {
		if (isset($doll[$key])) {
			$obj[$key] = $doll[$key];
		}
		else {
			$obj[$key] = $def;
		}
	}
	Nest.value($obj,"url").$() .= (zbx_empty(Nest.value($obj,"url").$()) ? "?" : "&")."output=html";
	Nest.value($obj,"params","favobj").$() = "hat";
	Nest.value($obj,"params","favref").$() = Nest.value($doll,"id").$();
	Nest.value($obj,"params","favaction").$() = "refresh";

	return $obj;
}

function get_update_doll_script($pmasterid, $dollid, $key, $value = "") {
	return "PMasters[".zbx_jsvalue($pmasterid)."].dolls[".zbx_jsvalue($dollid)."].".$key."(".zbx_jsvalue($value).");";
}

function make_refresh_menu($pmid, $dollid, $cur_interval, $params = null, &$menu, &$submenu, $menu_type = 1) {
	if ($menu_type == 1) {
		$intervals = CArray.array("10" => 10, "30" => 30, "60" => 60, "120" => 120, "600" => 600, "900" => 900);
		$title = _("Refresh time in seconds");
	}
	elseif ($menu_type == 2) {
		$intervals = CArray.array("x0.25" => 0.25, "x0.5" => 0.5, "x1" => 1, "x1.5" => 1.5, "x2" => 2, "x3" => 3, "x4" => 4, "x5" => 5);
		$title = _("Refresh time multiplier");
	}

	$menu["menu_".$dollid][] = CArray.array($title, null, null, CArray.array("outer" => CArray.array("pum_oheader"), "inner" => CArray.array("pum_iheader")));

	for($intervals as $key => $value) {
		$menu["menu_".$dollid][] = CArray.array(
			$key,
			"javascript: setRefreshRate(".zbx_jsvalue($pmid).", ".zbx_jsvalue($dollid).", ".$value.", ".zbx_jsvalue($params).");".
			"void(0);",
			null,
			CArray.array("outer" => ($value == $cur_interval) ? "pum_b_submenu" : "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")
		));
	}
	$submenu["menu_".$dollid][] = CArray.array();
}

/************* MATH *************/
function bcfloor($number) {
	if (strpos($number, ".") !== false) {
		if (($tmp = preg_replace("/\.0+$/", "", $number)) !== $number) {
			$number = $tmp;
		}
		elseif ($number[0] != "-") {
			$number = bcadd($number, 0, 0);
		}
		else {
			$number = bcsub($number, 1, 0);
		}
	}

	return $number == "-0" ? "0" : $number;
}

function bcceil($number) {
	if (strpos($number, ".") !== false) {
		if (($tmp = preg_replace("/\.0+$/", "", $number)) !== $number) {
			$number = $tmp;
		}
		elseif ($number[0] != "-") {
			$number = bcadd($number, 1, 0);
		}
		else {
			$number = bcsub($number, 0, 0);
		}
	}

	return $number == "-0" ? "0" : $number;
}

function bcround($number, $precision = 0) {
	if (strpos($number, ".") !== false) {
		if ($number[0] != "-") {
			$number = bcadd($number, "0." . str_repeat("0", $precision) . "5", $precision);
		}
		else {
			$number = bcsub($number, "0." . str_repeat("0", $precision) . "5", $precision);
		}
	}
	elseif ($precision != 0) {
		$number .= "." . str_repeat("0", $precision);
	}

	// according to bccomp(), "-0.0" does not equal "-0". However, "0.0" and "0" are equal.
	$zero = ($number[0] != "-" ? bccomp($number, "0") == 0 : bccomp(substr($number, 1), "0") == 0);

	return $zero ? ($precision == 0 ? "0" : "0." . str_repeat("0", $precision)) : $number;
}

/**
 * Calculates the modulus for float numbers.
 *
 * @param string $number
 * @param string $modulus
 *
 * @return string
 */
function bcfmod($number, $modulus) {
	return bcsub($number, bcmul($modulus, bcfloor(bcdiv($number, $modulus))));
}

/**
 * Converts number to letter representation.
 * From A to Z, then from AA to ZZ etc.
 * Example: 0 => A, 25 => Z, 26 => AA, 27 => AB, 52 => BA, ...
 *
 * @param int $number
 *
 * @return string
 */
function num2letter($number) {
	$start = ord("A");
	$base = 26;
	$str = "";
	$level = 0;

	do {
		if ($level++ > 0) {
			$number--;
		}
		$remainder = $number % $base;
		$number = ($number - $remainder) / $base;
		$str = chr($start + $remainder).$str;
	} while (0 != $number);

	return $str;
}

/**
 * Renders an \"access denied\" message and stops the execution of the script.
 *
 * The $mode parameters controls the layout of the message:
 * - ACCESS_DENY_OBJECT     - render the message when denying access to a specific object
 * - ACCESS_DENY_PAGE       - render a complete access denied page
 *
 * @param int $mode
 */
function access_deny($mode = ACCESS_DENY_OBJECT) {
	// deny access to an object
	if ($mode == ACCESS_DENY_OBJECT) {
		require_once dirname(__FILE__)."/page_header.php";
		show_error_message(_("No permissions to referred object or it does not exist!"));
		require_once dirname(__FILE__)."/page_footer.php";
	}
	// deny access to a page
	else {
		// url to redirect the user to after he loggs in
		$url = new CUrl(!empty(Nest.value(_REQUEST,"request").$()) ? Nest.value(_REQUEST,"request").$() : "");
		$url.setArgument("sid", null);
		$url = urlencode($url.toString());

		// if the user is logged in - render the access denied message
		if (CWebUser::isLoggedIn()) {
			$header = _("Access denied.");
			$message = CArray.array(
				_("Your are logged in as"),
				" ",
				bold(CWebUser::Nest.value($data,"alias").$()),
				". ",
				_("You have no permissions to access this page."),
				BR(),
				_("If you think this message is wrong, please consult your administrators about getting the necessary permissions.")
			);

			$buttons = CArray.array();
			// display the login button only for guest users
			if (CWebUser::isGuest()) {
				$buttons[] = new CButton("login", _("Login"),
					"javascript: document.location = \"index.php?request=".$url."\";", "formlist"
				);
			}
			$buttons[] = new CButton("back", _("Go to dashboard"),
				"javascript: document.location = \"dashboard.php\"", "formlist"
			);
		}
		// if the user is not logged in - offer to login
		else {
			$header = _("You are not logged in.");
			$message = CArray.array(
				_("You must login to view this page."),
				BR(),
				_("If you think this message is wrong, please consult your administrators about getting the necessary permissions.")
			);
			$buttons = CArray.array(
				new CButton("login", _("Login"), "javascript: document.location = \"index.php?request=".$url."\";", "formlist")
			);
		}

		$warning = new CWarning($header, $message);
		$warning.setButtons($buttons);

		$warningView = new CView("general.warning", CArray.array(
			"warning" => $warning
		));
		$warningView.render();
		exit;
	}
}

function detect_page_type($default = PAGE_TYPE_HTML) {
	if (isset(Nest.value(_REQUEST,"output").$())) {
		switch (strtolower(Nest.value(_REQUEST,"output").$())) {
			case "text":
				return PAGE_TYPE_TEXT;
			case "ajax":
				return PAGE_TYPE_JS;
			case "json":
				return PAGE_TYPE_JSON;
			case "json-rpc":
				return PAGE_TYPE_JSON_RPC;
			case "html":
				return PAGE_TYPE_HTML_BLOCK;
			case "img":
				return PAGE_TYPE_IMAGE;
			case "css":
				return PAGE_TYPE_CSS;
		}
	}

	return $default;
}

function show_messages($bool = true, $okmsg = null, $errmsg = null) {
	global $page, $ZBX_MESSAGES;

	if (!defined("PAGE_HEADER_LOADED")) {
		return null;
	}
	if (defined("ZBX_API_REQUEST")) {
		return null;
	}
	if (!isset(Nest.value($page,"type").$())) {
		Nest.value($page,"type").$() = PAGE_TYPE_HTML;
	}

	$imageMessages = CArray.array();

	if (!$bool && !is_null($errmsg)) {
		$msg = _("ERROR").": ".$errmsg;
	}
	elseif ($bool && !is_null($okmsg)) {
		$msg = $okmsg;
	}

	if (isset($msg)) {
		switch (Nest.value($page,"type").$()) {
			case PAGE_TYPE_IMAGE:
				// save all of the messages in an array to display them later in an image
				$imageMessages[] = CArray.array(
					"text" => $msg,
					"color" => (!$bool) ? CArray.array("R" => 255, "G" => 0, "B" => 0) : CArray.array("R" => 34, "G" => 51, "B" => 68)
				);
				break;
			case PAGE_TYPE_XML:
				echo htmlspecialchars($msg).\"\n\";
				break;
			case PAGE_TYPE_HTML:
			default:
				$msg_tab = new CTable($msg, ($bool ? "msgok" : "msgerr"));
				$msg_tab.setCellPadding(0);
				$msg_tab.setCellSpacing(0);

				$row = CArray.array();

				$msg_col = new CCol(bold($msg), "msg_main msg");
				$msg_col.setAttribute("id", "page_msg");
				$row[] = $msg_col;

				if (isset($ZBX_MESSAGES) && !empty($ZBX_MESSAGES)) {
					$msg_details = new CDiv(_("Details"), "blacklink");
					$msg_details.setAttribute("onclick", "javascript: showHide(\"msg_messages\", IE ? \"block\" : \"table\");");
					$msg_details.setAttribute("title", _("Maximize")."/"._("Minimize"));
					array_unshift($row, new CCol($msg_details, "clr"));
				}
				$msg_tab.addRow($row);
				$msg_tab.show();
				break;
		}
	}

	if (isset($ZBX_MESSAGES) && !empty($ZBX_MESSAGES)) {
		if (Nest.value($page,"type").$() == PAGE_TYPE_IMAGE) {
			for($ZBX_MESSAGES as $msg) {
				// save all of the messages in an array to display them later in an image
				if (Nest.value($msg,"type").$() == "error") {
					$imageMessages[] = CArray.array(
						"text" => Nest.value($msg,"message").$(),
						"color" => CArray.array("R" => 255, "G" => 55, "B" => 55)
					);
				}
				else {
					$imageMessages[] = CArray.array(
						"text" => Nest.value($msg,"message").$(),
						"color" => CArray.array("R" => 155, "G" => 155, "B" => 55)
					);
				}
			}
		}
		elseif (Nest.value($page,"type").$() == PAGE_TYPE_XML) {
			for($ZBX_MESSAGES as $msg) {
				echo "[".$msg["type"]."] ".$msg["message"].\"\n\";
			}
		}
		else {
			$lst_error = new CList(null,"messages");
			for($ZBX_MESSAGES as $msg) {
				$lst_error.addItem(Nest.value($msg,"message").$(), Nest.value($msg,"type").$());
				$bool = ($bool && "error" != zbx_strtolower(Nest.value($msg,"type").$()));
			}
			$msg_show = 6;
			$msg_count = count($ZBX_MESSAGES);
			if ($msg_count > $msg_show) {
				$msg_count = $msg_show * 16;
				$lst_error.setAttribute("style", "height: ".$msg_count."px;");
			}
			$tab = new CTable(null, ($bool ? "msgok" : "msgerr"));
			$tab.setCellPadding(0);
			$tab.setCellSpacing(0);
			$tab.setAttribute("id", "msg_messages");
			$tab.setAttribute("style", "width: 100%;");
			if (isset($msg_tab) && $bool) {
				$tab.setAttribute("style", "display: none;");
			}
			$tab.addRow(new CCol($lst_error, "msg"));
			$tab.show();
		}
		$ZBX_MESSAGES = null;
	}

	// draw an image with the messages
	if (Nest.value($page,"type").$() == PAGE_TYPE_IMAGE && count($imageMessages) > 0) {
		$imageFontSize = 8;

		// calculate the size of the text
		$imageWidth = 0;
		$imageHeight = 0;
		for($imageMessages as &$msg) {
			$size = imageTextSize($imageFontSize, 0, Nest.value($msg,"text").$());
			Nest.value($msg,"height").$() = Nest.value($size,"height").$() - Nest.value($size,"baseline").$();

			// calculate the total size of the image
			$imageWidth = max($imageWidth, Nest.value($size,"width").$());
			$imageHeight += Nest.value($size,"height").$() + 1;
		}
		unset($msg);

		// additional padding
		$imageWidth += 2;
		$imageHeight += 2;

		// create the image
		$canvas = imagecreate($imageWidth, $imageHeight);
		imagefilledrectangle($canvas, 0, 0, $imageWidth, $imageHeight, imagecolorallocate($canvas, 255, 255, 255));

		// draw each message
		$y = 1;
		for($imageMessages as $msg) {
			$y += Nest.value($msg,"height").$();
			imageText($canvas, $imageFontSize, 0, 1, $y,
				imagecolorallocate($canvas, Nest.value($msg,"color","R").$(), Nest.value($msg,"color","G").$(), Nest.value($msg,"color","B").$()),
				$msg["text"]
			);
		}
		imageOut($canvas);
		imagedestroy($canvas);
	}
}

function show_message($msg) {
	show_messages(true, $msg, "");
}

function show_error_message($msg) {
	show_messages(false, "", $msg);
}

function info($msgs) {
	global $ZBX_MESSAGES;

	zbx_value2CArray.array($msgs);
	if (is_null($ZBX_MESSAGES)) {
		$ZBX_MESSAGES = CArray.array();
	}
	for($msgs as $msg) {
		array_push($ZBX_MESSAGES, CArray.array("type" => "info", "message" => $msg));
	}
}

function error($msgs) {
	global $ZBX_MESSAGES;

	if (is_null($ZBX_MESSAGES)) {
		$ZBX_MESSAGES = CArray.array();
	}

	$msgs = zbx_toArray($msgs);
	for($msgs as $msg) {
		if (isset(CWebUser::Nest.value($data,"debug_mode").$()) && !is_object($msg) && !CWebUser::Nest.value($data,"debug_mode").$()) {
			$msg = preg_replace("/^\[.+?::.+?\]/", "", $msg);
		}
		array_push($ZBX_MESSAGES, CArray.array("type" => "error", "message" => $msg));
	}
}

function clear_messages($count = null) {
	global $ZBX_MESSAGES;

	$result = CArray.array();
	if (!is_null($count)) {
		while ($count-- > 0) {
			array_unshift($result, array_pop($ZBX_MESSAGES));
		}
	}
	else {
		$result = $ZBX_MESSAGES;
		$ZBX_MESSAGES = null;
	}
	return $result;
}

function fatal_error($msg) {
	require_once dirname(__FILE__)."/page_header.php";
	show_error_message($msg);
	require_once dirname(__FILE__)."/page_footer.php";
}

function get_tree_by_parentid($parentid, &$tree, $parent_field, $level = 0) {
	if (empty($tree)) {
		return $tree;
	}

	$level++;
	if ($level > 32) {
		return CArray.array();
	}

	$result = CArray.array();
	if (isset($tree[$parentid])) {
		$result[$parentid] = $tree[$parentid];
	}

	$tree_ids = array_keys($tree);

	for($tree_ids as $key => $id) {
		$child = $tree[$id];
		if (bccomp($child[$parent_field], $parentid) == 0) {
			$result[$id] = $child;
			$childs = get_tree_by_parentid($id, $tree, $parent_field, $level); // attention recursion !!!
			$result += $childs;
		}
	}
	return $result;
}

function parse_period($str) {
	$out = null;
	$str = trim($str, ";");
	$periods = explode(";", $str);
	for($periods as $period) {
		if (!preg_match("/^([1-7])-([1-7]),([0-9]{1,2}):([0-9]{1,2})-([0-9]{1,2}):([0-9]{1,2})$/", $period, $arr)) {
			return null;
		}

		for ($i = $arr[1]; $i <= $arr[2]; $i++) {
			if (!isset($out[$i])) {
				$out[$i] = CArray.array();
			}
			array_push($out[$i], CArray.array(
				"start_h" => $arr[3],
				"start_m" => $arr[4],
				"end_h" => $arr[5],
				"end_m" => $arr[6]
			));
		}
	}
	return $out;
}

function get_status() {
	global $ZBX_SERVER, $ZBX_SERVER_PORT;

	$status = CArray.array(
		"triggers_count" => 0,
		"triggers_count_enabled" => 0,
		"triggers_count_disabled" => 0,
		"triggers_count_off" => 0,
		"triggers_count_on" => 0,
		"items_count" => 0,
		"items_count_monitored" => 0,
		"items_count_disabled" => 0,
		"items_count_not_supported" => 0,
		"hosts_count" => 0,
		"hosts_count_monitored" => 0,
		"hosts_count_not_monitored" => 0,
		"hosts_count_template" => 0,
		"users_online" => 0,
		"qps_total" => 0
	);

	// server
	$zabbixServer = new CZabbixServer($ZBX_SERVER, $ZBX_SERVER_PORT, ZBX_SOCKET_TIMEOUT, 0);
	Nest.value($status,"zabbix_server").$() = $zabbixServer.isRunning() ? _("Yes") : _("No");

	// triggers
	$dbTriggers = DBselect(
		"SELECT COUNT(DISTINCT t.triggerid) AS cnt,t.status,t.value".
			" FROM triggers t".
			" WHERE NOT EXISTS (".
				"SELECT f.functionid FROM functions f".
					" JOIN items i ON f.itemid=i.itemid".
					" JOIN hosts h ON i.hostid=h.hostid".
					" WHERE f.triggerid=t.triggerid AND (i.status<>".ITEM_STATUS_ACTIVE." OR h.status<>".HOST_STATUS_MONITORED.")".
				")".
			" AND t.flags IN (".ZBX_FLAG_DISCOVERY_NORMAL.",".ZBX_FLAG_DISCOVERY_CREATED.")".
			" GROUP BY t.status,t.value"
		);
	while ($dbTrigger = DBfetch($dbTriggers)) {
		switch (Nest.value($dbTrigger,"status").$()) {
			case TRIGGER_STATUS_ENABLED:
				switch (Nest.value($dbTrigger,"value").$()) {
					case TRIGGER_VALUE_FALSE:
						Nest.value($status,"triggers_count_off").$() = Nest.value($dbTrigger,"cnt").$();
						break;
					case TRIGGER_VALUE_TRUE:
						Nest.value($status,"triggers_count_on").$() = Nest.value($dbTrigger,"cnt").$();
						break;
				}
				break;
			case TRIGGER_STATUS_DISABLED:
				Nest.value($status,"triggers_count_disabled").$() += Nest.value($dbTrigger,"cnt").$();
				break;
		}
	}
	Nest.value($status,"triggers_count_enabled").$() = Nest.value($status,"triggers_count_off").$() + Nest.value($status,"triggers_count_on").$();
	Nest.value($status,"triggers_count").$() = Nest.value($status,"triggers_count_enabled").$() + Nest.value($status,"triggers_count_disabled").$();

	// items
	$dbItems = DBselect(
		"SELECT COUNT(i.itemid) AS cnt,i.status,i.state".
				" FROM items i".
				" INNER JOIN hosts h ON i.hostid=h.hostid".
				" WHERE h.status=".HOST_STATUS_MONITORED.
					" AND i.flags IN (".ZBX_FLAG_DISCOVERY_NORMAL.",".ZBX_FLAG_DISCOVERY_CREATED.")".
					" AND i.type<>".ITEM_TYPE_HTTPTEST.
				" GROUP BY i.status,i.state");
	while ($dbItem = DBfetch($dbItems)) {
		if (Nest.value($dbItem,"status").$() == ITEM_STATUS_ACTIVE) {
			if (Nest.value($dbItem,"state").$() == ITEM_STATE_NORMAL) {
				Nest.value($status,"items_count_monitored").$() = Nest.value($dbItem,"cnt").$();
			}
			else {
				Nest.value($status,"items_count_not_supported").$() = Nest.value($dbItem,"cnt").$();
			}
		}
		elseif (Nest.value($dbItem,"status").$() == ITEM_STATUS_DISABLED) {
			Nest.value($status,"items_count_disabled").$() += Nest.value($dbItem,"cnt").$();
		}
	}
	Nest.value($status,"items_count").$() = Nest.value($status,"items_count_monitored").$() + $status["items_count_disabled"]
			+ Nest.value($status,"items_count_not_supported").$();

	// hosts
	$dbHosts = DBselect(
		"SELECT COUNT(*) AS cnt,h.status".
		" FROM hosts h".
		" WHERE ".dbConditionInt("h.status", CArray.array(
				HOST_STATUS_MONITORED, HOST_STATUS_NOT_MONITORED, HOST_STATUS_TEMPLATE
			)).
			" AND ".dbConditionInt("h.flags", CArray.array(ZBX_FLAG_DISCOVERY_NORMAL, ZBX_FLAG_DISCOVERY_CREATED)).
		" GROUP BY h.status");
	while ($dbHost = DBfetch($dbHosts)) {
		switch (Nest.value($dbHost,"status").$()) {
			case HOST_STATUS_MONITORED:
				Nest.value($status,"hosts_count_monitored").$() = Nest.value($dbHost,"cnt").$();
				break;
			case HOST_STATUS_NOT_MONITORED:
				Nest.value($status,"hosts_count_not_monitored").$() = Nest.value($dbHost,"cnt").$();
				break;
			case HOST_STATUS_TEMPLATE:
				Nest.value($status,"hosts_count_template").$() = Nest.value($dbHost,"cnt").$();
				break;
		}
	}
	Nest.value($status,"hosts_count").$() = Nest.value($status,"hosts_count_monitored").$() + $status["hosts_count_not_monitored"]
			+ Nest.value($status,"hosts_count_template").$();

	// users
	$row = DBfetch(DBselect(
			"SELECT COUNT(*) AS usr_cnt".
			" FROM users u".
			whereDbNode("u.userid")
	));
	Nest.value($status,"users_count").$() = Nest.value($row,"usr_cnt").$();
	Nest.value($status,"users_online").$() = 0;

	$db_sessions = DBselect(
			"SELECT s.userid,s.status,MAX(s.lastaccess) AS lastaccess".
			" FROM sessions s".
			" WHERE s.status=".ZBX_SESSION_ACTIVE.
				andDbNode("s.userid").
			" GROUP BY s.userid,s.status"
	);
	while ($session = DBfetch($db_sessions)) {
		if ((Nest.value($session,"lastaccess").$() + ZBX_USER_ONLINE_TIME) >= time()) {
			$status["users_online"]++;
		}
	}

	// comments: !!! Don't forget sync code with C !!!
	$row = DBfetch(DBselect(
		"SELECT SUM(1.0/i.delay) AS qps".
				" FROM items i,hosts h".
				" WHERE i.status=".ITEM_STATUS_ACTIVE.
				" AND i.hostid=h.hostid".
				" AND h.status=".HOST_STATUS_MONITORED.
				" AND i.delay<>0".
				" AND i.flags<>".ZBX_FLAG_DISCOVERY_PROTOTYPE
	));
	Nest.value($status,"qps_total").$() = round(Nest.value($row,"qps").$(), 2);

	return $status;
}

function set_image_header($format = null) {
	global $IMAGE_FORMAT_DEFAULT;

	if (is_null($format)) {
		$format = $IMAGE_FORMAT_DEFAULT;
	}

	if (IMAGE_FORMAT_JPEG == $format) {
		header("Content-type:  image/jpeg");
	}
	if (IMAGE_FORMAT_TEXT == $format) {
		header("Content-type:  text/html");
	}
	else {
		header("Content-type:  image/png");
	}

	header("Expires: Mon, 17 Aug 1998 12:51:50 GMT");
}

function imageOut(&$image, $format = null) {
	global $page, $IMAGE_FORMAT_DEFAULT;

	if (is_null($format)) {
		$format = $IMAGE_FORMAT_DEFAULT;
	}

	ob_start();

	if (IMAGE_FORMAT_JPEG == $format) {
		imagejpeg($image);
	}
	else {
		imagepng($image);
	}

	$imageSource = ob_get_contents();
	ob_end_clean();

	if (Nest.value($page,"type").$() != PAGE_TYPE_IMAGE) {
		session_start();
		$imageId = md5(strlen($imageSource));
		Nest.value($_SESSION,"image_id").$() = CArray.array();
		$_SESSION["image_id"][$imageId] = $imageSource;
		session_write_close();
	}

	switch (Nest.value($page,"type").$()) {
		case PAGE_TYPE_IMAGE:
			echo $imageSource;
			break;
		case PAGE_TYPE_JSON:
			$json = new CJSON();
			echo $json.encode(CArray.array("result" => $imageId));
			break;
		case PAGE_TYPE_TEXT:
		default:
			echo $imageId;
	}
}

function encode_log($data) {
	return (defined("ZBX_LOG_ENCODING_DEFAULT") && function_exists("mb_convert_encoding"))
			? mb_convert_encoding($data, _("UTF-8"), ZBX_LOG_ENCODING_DEFAULT)
			: $data;
}

/**
 * Check if we have error messages to display.
 *
 * @global array $ZBX_MESSAGES
 *
 * @return bool
 */
function hasErrorMesssages() {
	global $ZBX_MESSAGES;

	if ($ZBX_MESSAGES !== null) {
		for($ZBX_MESSAGES as $message) {
			if (Nest.value($message,"type").$() === "error") {
				return true;
			}
		}
	}

	return false;
}

/**
 * Check if all keys from $keys exist in $array.
 * If some keys are missing return array of missing keys, true otherwise.
 *
 * @param array $array
 * @param array $keys
 *
 * @return array|bool
 */
function checkRequiredKeys(array $array, array $keys) {
	return array_diff($keys, array_keys($array));
}

/**
 * Clear page cookies on action.
 *
 * @param bool   $clear
 * @param string $id	parent id, is used as cookie prefix
 */
function clearCookies($clear = false, $id = null) {
	if ($clear) {
		insert_js("cookie.eraseArray(\"".basename(Nest.value($_SERVER,"SCRIPT_NAME").$(), ".php").($id ? "_".$id : "")."\")");
	}
}

/**
 * Prepare data for host menu popup.
 *
 * @param array  $host						host data
 * @param string $host["hostid"]			host id
 * @param array  $host["screens"]			host screens (optional)
 * @param array  $scripts					host scripts (optional)
 * @param string $scripts[]["name"]			script name
 * @param string $scripts[]["scriptid"]		script id
 * @param string $scripts[]["confirmation"]	confirmation text
 * @param bool   $hasGoTo					\"Go to\" block in popup
 *
 * @return array
 */
function getMenuPopupHost(array $host, array $scripts = null, $hasGoTo = true) {
	$data = CArray.array(
		"type" => "host",
		"hostid" => Nest.value($host,"hostid").$(),
		"hasScreens" => (isset(Nest.value($host,"screens").$()) && Nest.value($host,"screens").$()),
		"hasGoTo" => $hasGoTo
	);

	if ($scripts) {
		CArrayHelper::sort($scripts, CArray.array("name"));

		for(array_values($scripts) as $script) {
			$data["scripts"][] = CArray.array(
				"name" => Nest.value($script,"name").$(),
				"scriptid" => Nest.value($script,"scriptid").$(),
				"confirmation" => $script["confirmation"]
			);
		}
	}

	return $data;
}

/**
 * Prepare data for map menu popup.
 *
 * @param string $hostId					host id
 * @param array  $scripts					host scripts (optional)
 * @param string $scripts[]["name"]			script name
 * @param string $scripts[]["scriptid"]		script id
 * @param string $scripts[]["confirmation"]	confirmation text
 * @param array  $gotos						goto links (optional)
 * @param array  $gotos["screens"]			link to host screen page with url parameters (\"name\" => \"value\") (optional)
 * @param array  $gotos["triggerStatus"]	link to trigger status page with url parameters (\"name\" => \"value\") (optional)
 * @param array  $gotos["submap"]			link to submap page with url parameters (\"name\" => \"value\") (optional)
 * @param array  $gotos["events"]			link to events page with url parameters (\"name\" => \"value\") (optional)
 * @param array  $urls						local and global map urls (optional)
 * @param string $urls[]["name"]			url name
 * @param string $urls[]["url"]				url
 *
 * @return array
 */
function getMenuPopupMap($hostId, array $scripts = null, array $gotos = null, array $urls = null) {
	$data = CArray.array(
		"type" => "map"
	);

	if ($scripts) {
		CArrayHelper::sort($scripts, CArray.array("name"));

		Nest.value($data,"hostid").$() = $hostId;

		for(array_values($scripts) as $script) {
			$data["scripts"][] = CArray.array(
				"name" => Nest.value($script,"name").$(),
				"scriptid" => Nest.value($script,"scriptid").$(),
				"confirmation" => $script["confirmation"]
			);
		}
	}

	if ($gotos) {
		Nest.value($data,"gotos").$() = $gotos;
	}

	if ($urls) {
		for($urls as $url) {
			$data["urls"][] = CArray.array(
				"label" => Nest.value($url,"name").$(),
				"url" => $url["url"]
			);
		}
	}

	return $data;
}

/**
 * Prepare data for item history menu popup.
 *
 * @param array $item				item data
 * @param int   $item["itemid"]		item id
 * @param int   $item["value_type"]	item value type
 *
 * @return array
 */
function getMenuPopupHistory(array $item) {
	return CArray.array(
		"type" => "history",
		"itemid" => Nest.value($item,"itemid").$(),
		"hasLatestGraphs" => in_CArray.array(Nest.value($item,"value_type").$(), CArray.array(ITEM_VALUE_TYPE_UINT64, ITEM_VALUE_TYPE_FLOAT))
	);
}

/**
 * Prepare data for trigger menu popup.
 *
 * @param array  $trigger						trigger data
 * @param string $trigger["triggerid"]			trigger id
 * @param int    $trigger["flags"]				trigger flags (TRIGGER_FLAG_DISCOVERY*)
 * @param array  $trigger["hosts"]				hosts, used by trigger expression
 * @param string $trigger["hosts"][]["hostid"]	host id
 * @param string $trigger["url"]				url
 * @param array  $items							trigger items (optional)
 * @param string $items[]["name"]				item name
 * @param array  $items[]["params"]				item url parameters (\"name\" => \"value\")
 * @param array  $acknowledge					acknowledge link parameters (optional)
 * @param string $acknowledge["eventid"]		event id
 * @param string $acknowledge["screenid"]		screen id (optional)
 * @param string $acknowledge["backurl"]		return url (optional)
 * @param string $eventTime						event navigation time parameter (optional)
 *
 * @return array
 */
function getMenuPopupTrigger(array $trigger, array $items = null, array $acknowledge = null, $eventTime = null) {
	if ($items) {
		CArrayHelper::sort($items, CArray.array("name"));
	}

	$data = CArray.array(
		"type" => "trigger",
		"triggerid" => Nest.value($trigger,"triggerid").$(),
		"items" => $items,
		"acknowledge" => $acknowledge,
		"eventTime" => $eventTime,
		"configuration" => null,
		"url" => resolveTriggerUrl($trigger)
	);

	if ((CWebUser::Nest.value($data,"type").$() == USER_TYPE_ZABBIX_ADMIN || CWebUser::Nest.value($data,"type").$() == USER_TYPE_SUPER_ADMIN)
			&& Nest.value($trigger,"flags").$() == ZBX_FLAG_DISCOVERY_NORMAL) {
		$host = reset(Nest.value($trigger,"hosts").$());

		Nest.value($data,"configuration").$() = CArray.array(
			"hostid" => Nest.value($host,"hostid").$(),
			"switchNode" => id2nodeid(Nest.value($trigger,"triggerid").$())
		);
	}

	return $data;
}

/**
 * Splitting string using slashes with escape backslash support.
 *
 * @param string $path				string path to parse
 * @param bool   $stripSlashes		remove escaped slashes from the path pieces
 *
 * @return array
 */
function splitPath($path, $stripSlashes = true) {
	$items = CArray.array();
	$s = $escapes = "";

	for ($i = 0, $size = strlen($path); $i < $size; $i++) {
		if ($path[$i] === "/") {
			if ($escapes === "") {
				$items[] = $s;
				$s = "";
			}
			else {
				if (strlen($escapes) % 2 == 0) {
					$s .= $stripSlashes ? stripslashes($escapes) : $escapes;
					$items[] = $s;
					$s = $escapes = "";
				}
				else {
					$s .= $stripSlashes ? stripslashes($escapes).$path[$i] : $escapes.$path[$i];
					$escapes = "";
				}
			}
		}
		elseif ($path[$i] === "\\") {
			$escapes .= $path[$i];
		}
		else {
			$s .= $stripSlashes ? stripslashes($escapes).$path[$i] : $escapes.$path[$i];
			$escapes = "";
		}
	}

	if ($escapes !== "") {
		$s .= $stripSlashes ? stripslashes($escapes) : $escapes;
	}

	$items[] = $s;

	return $items;
}
