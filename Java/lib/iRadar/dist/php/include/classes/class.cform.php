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


class CForm extends CTag {

	public function __construct($method = "post", $action = null, $enctype = null) {
		parent::__construct("form", "yes");
		setMethod($method);
		setAction($action);
		setEnctype($enctype);
		setAttribute("accept-charset", "utf-8");

		if (isset(Nest.value($_COOKIE,"zbx_sessionid").$())) {
			addVar("sid", substr(Nest.value($_COOKIE,"zbx_sessionid").$(), 16, 16));
		}
		addVar("form_refresh", get_request("form_refresh", 0) + 1);
	}

	public function setMethod($value = "post") {
		return Nest.value(attributes,"method").$() = $value;
	}

	public function setAction($value) {
		global $page;

		if (is_null($value)) {
			$value = isset(Nest.value($page,"file").$()) ? Nest.value($page,"file").$() : "#";
		}
		return Nest.value(attributes,"action").$() = $value;
	}

	public function setEnctype($value = null) {
		if (is_null($value)) {
			return removeAttribute("enctype");
		}
		elseif (!is_string($value)) {
			return error("Incorrect value for SetEnctype \"".$value."\".");
		}
		return setAttribute("enctype", $value);
	}

	public function addVar($name, $value, $id = null) {
		if (!is_null($value)) {
			addItem(new CVar($name, $value, $id));
		}
	}
}
