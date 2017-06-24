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


class CTable extends CTag {

	public $headerClass;
	public $footerClass;
	protected $oddRowClass;
	protected $evenRowClass;
	protected $header;
	protected $footer;
	protected $colnum;
	protected $rownum;
	protected $message;

	public function __construct($message = null, $class = null) {
		parent::__construct("table", "yes");
		attr("class", $class);
		rownum = 0;
		oddRowClass = null;
		evenRowClass = null;
		header = "";
		headerClass = null;
		footer = "";
		footerClass = null;
		colnum = 1;
		message = $message;
	}

	public function setOddRowClass($value = null) {
		oddRowClass = $value;
	}

	public function setEvenRowClass($value = null) {
		evenRowClass = $value;
	}

	public function setAlign($value) {
		return Nest.value(attributes,"align").$() = $value;
	}

	public function setCellPadding($value) {
		return Nest.value(attributes,"cellpadding").$() = strval($value);
	}

	public function setCellSpacing($value) {
		return Nest.value(attributes,"cellspacing").$() = strval($value);
	}

	public function prepareRow($item, $class = null, $id = null) {
		if (is_null($item)) {
			return null;
		}
		if (is_object($item) && zbx_strtolower(get_class($item)) == "ccol") {
			if (isset(header) && !isset($item.Nest.value(attributes,"colspan").$())) {
				$item.Nest.value(attributes,"colspan").$() = colnum;
			}
			$item = new CRow($item, $class, $id);
		}

		if (is_object($item) && zbx_strtolower(get_class($item)) == "crow") {
			$item.attr("class", $class);
		}
		else {
			$item = new CRow($item, $class, $id);
		}

		if (!isset($item.Nest.value(attributes,"class").$()) || is_array($item.Nest.value(attributes,"class").$())) {
			$class = (rownum % 2) ? oddRowClass : evenRowClass;
			$item.attr("class", $class);
			$item.attr("origClass", $class);
		}
		return $item;
	}

	public function setHeader($value = null, $class = "header") {
		if (is_null($class)) {
			$class = headerClass;
		}
		if (is_object($value) && zbx_strtolower(get_class($value)) == "crow") {
			if (!is_null($class)) {
				$value.setAttribute("class", $class);
			}
		}
		else {
			$value = new CRow($value, $class);
		}
		colnum = $value.itemsCount();
		header = $value.toString();
	}

	public function setFooter($value = null, $class = "footer") {
		if (is_null($class)) {
			$class = footerClass;
		}
		footer = prepareRow($value, $class);
		footer = footer->toString();
	}

	public function addRow($item, $class = null, $id = null) {
		$item = addItem(prepareRow($item, $class, $id));
		++rownum;
		return $item;
	}

	public function showRow($item, $class = null, $id = null) {
		echo prepareRow($item, $class, $id).toString();
		++rownum;
	}

	public function getNumRows() {
		return rownum;
	}

	public function startToString() {
		$ret = parent::startToString();
		$ret .= header;
		return $ret;
	}

	public function endToString() {
		$ret = "";
		if (rownum == 0 && isset(message)) {
			$ret = prepareRow(new CCol(message, "message"));
			$ret = $ret.toString();
		}
		$ret .= footer;
		$ret .= parent::endToString();
		return $ret;
	}
}
