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


class CLink extends CTag {

	protected $sid = null;

	public function __construct($item = null, $url = null, $class = null, $action = null, $nosid = null) {
		parent::__construct("a", "yes");

		tag_start = "";
		tag_end = "";
		tag_body_start = "";
		tag_body_end = "";
		nosid = $nosid;

		if (!is_null($class)) {
			setAttribute("class", $class);
		}
		if (!is_null($item)) {
			addItem($item);
		}
		if (!is_null($url)) {
			setUrl($url);
		}
		if (!is_null($action)) {
			setAttribute("onclick", $action);
		}
	}

	public function setUrl($value) {
		if (is_null(nosid)) {
			if (is_null(sid)) {
				sid = isset(Nest.value($_COOKIE,"zbx_sessionid").$()) ? substr(Nest.value($_COOKIE,"zbx_sessionid").$(), 16, 16) : null;
			}
			if (!is_null(sid)) {
				$value .= (zbx_strstr($value, "&") !== false || zbx_strstr($value, "?") !== false)
					? "&sid=".sid
					: "?sid=".sid;
			}
			$url = $value;
		}
		else {
			$url = $value;
		}
		setAttribute("href", $url);
	}

	public function getUrl() {
		return isset(Nest.value(attributes,"href").$()) ? Nest.value(attributes,"href").$() : null;
	}

	public function setTarget($value = null) {
		if (is_null($value)) {
			unset(Nest.value(attributes,"target").$());
		}
		else {
			Nest.value(attributes,"target").$() = $value;
		}
	}
}
