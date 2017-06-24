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


class CObject {

	public $items;

	public function __construct($items = null) {
		items = CArray.array();
		if (isset($items)) {
			addItem($items);
		}
	}

	public function toString($destroy = true) {
		$res = implode("", items);
		if ($destroy) {
			destroy();
		}
		return $res;
	}

	public function __toString() {
		return toString();
	}

	public function show($destroy = true) {
		echo toString($destroy);
	}

	public function destroy() {
		cleanItems();
	}

	public function cleanItems() {
		items = CArray.array();
	}

	public function itemsCount() {
		return count(items);
	}

	public function addItem($value) {
		if (is_object($value)) {
			array_push(items, unpack_object($value));
		}
		elseif (is_string($value)) {
			array_push(items, $value);
		}
		elseif (is_array($value)) {
			for($value as $item) {
				addItem($item); // attention, recursion !!!
			}
		}
		elseif (!is_null($value)) {
			array_push(items, unpack_object($value));
		}
		return $this;
	}
}

function unpack_object(&$item) {
	$res = "";
	if (is_object($item)) {
		$res = $item.toString(false);
	}
	elseif (is_array($item)) {
		for($item as $id => $dat) {
			$res .= unpack_object($item[$id]); // attention, recursion !!!
		}
	}
	elseif (!is_null($item)) {
		$res = strval($item);
		unset($item);
	}
	return $res;
}
