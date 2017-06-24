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


class CRadioButtonList extends CDiv {

	const ORIENTATION_HORIZONTAL = "horizontal";
	const ORIENTATION_VERTICAL = "vertical";

	protected $count;
	protected $name;
	protected $value;
	protected $orientation;

	public function __construct($name = "radio", $value = "yes") {
		count = 0;
		name = $name;
		value = $value;
		orientation = self::ORIENTATION_HORIZONTAL;
		parent::__construct(null, null, $name);
	}

	public function addValue($name, $value, $checked = null, $id = null) {
		count++;

		if (is_null($id)) {
			$id = zbx_formatDomId(name)."_".count;
		}

		$radio = new CInput("radio", name, $value, null, $id);
		if (strcmp($value, value) == 0 || !is_null($checked) || $checked) {
			$radio.attr("checked", "checked");
		}

		$label = new CLabel($name, $id);

		$outerDiv = new CDiv(CArray.array($radio, $label));
		if (orientation == self::ORIENTATION_HORIZONTAL) {
			$outerDiv.addClass("inlineblock");
		}

		parent::addItem($outerDiv);
	}

	public function makeHorizaontal() {
		orientation = self::ORIENTATION_HORIZONTAL;
	}

	public function makeVertical() {
		orientation = self::ORIENTATION_VERTICAL;
	}
}
