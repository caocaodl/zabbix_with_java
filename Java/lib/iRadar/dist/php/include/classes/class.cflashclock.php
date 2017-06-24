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


class CFlashClock extends CFlash {

	public $timetype;
	public $src;

	public function __construct($width = 200, $height = 200, $url = null) {
		timetype = null;

		if (!is_numeric($width) || $width < 24) {
			$width = 200;
		}
		if (!is_numeric($height) || $height < 24) {
			$height = 200;
		}

		src = "images/flash/zbxclock.swf?analog=1&smooth=1";
		if (!is_null($url)) {
			src .= "&url=".urlencode($url);
		}
		timeError = null;
		timeType = null;
		timeZone = null;
		timeOffset = null;

		parent::__construct(src, $width, $height);
	}

	public function setTimeType($value) {
		timeType = $value;
	}

	public function setTimeZone($value) {
		timeZone = $value;
	}

	public function setTimeOffset($value) {
		timeOffset = $value;
	}

	public function setTimeError($value) {
		timeError = $value;
	}

	public function bodyToString() {
		$src = src;
		if (!empty(timeError)) {
			$src .= "&timeerror=".timeError;
		}
		if (!empty(timeType)) {
			$src .= "&timetype=".urlencode(timeType);
		}
		if (!is_null(timeZone)) {
			$src .= "&timezone=".urlencode(timeZone);
		}
		if (!is_null(timeOffset)) {
			$src .= "&timeoffset=".timeOffset;
		}
		setSrc($src);

		return parent::bodyToString();
	}
}
