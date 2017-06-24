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


class CVar {

	public $var_container;
	public $var_name;
	public $element_id;

	public function __construct($name, $value = null, $id = null) {
		var_container = CArray.array();
		var_name = $name;
		element_id = $id;
		setValue($value);
	}

	public function setValue($value) {
		var_container = CArray.array();
		if (is_null($value)) {
			return;
		}
		parseValue(var_name, $value);
	}

	public function parseValue($name, $value) {
		if (is_array($value)) {
			for($value as $key => $item) {
				if (is_null($item)) {
					continue;
				}
				parseValue($name."[".$key."]", $item);
			}
			return null;
		}
		if (strpos($value, \"\n\") === false) {
			$hiddenVar = new CInput("hidden", $name, $value, null, element_id);
			$hiddenVar.removeAttribute("class");
		}
		else {
			$hiddenVar = new CTextArea($name, $value);
			$hiddenVar.setAttribute("class", "hidden");
		}
		var_container[] = $hiddenVar;
	}

	public function toString() {
		$res = "";
		for(var_container as $item) {
			$res .= $item.toString();
		}
		return $res;
	}
}
