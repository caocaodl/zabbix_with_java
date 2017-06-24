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


class CWarning extends CTable {

	protected $header;
	protected $message;
	protected $alignment;
	protected $paddings;
	protected $buttons;

	public function __construct($header, $message = null) {
		parent::__construct(null, "warningTable");
		setAlign("center");
		header = $header;
		message = $message;
		alignment = null;
		paddings = null;
		buttons = CArray.array();
	}

	public function setAlignment($alignment) {
		alignment = $alignment;
	}

	public function setPaddings($padding) {
		paddings = $padding;
	}

	public function setButtons($buttons = CArray.array()) {
		buttons = is_array($buttons) ? $buttons : CArray.array($buttons);
	}

	public function show($destroy = true) {
		setHeader(header, "header");

		$cssClass = "content";
		if (!empty(alignment)) {
			$cssClass .= " ".alignment;
		}

		if (!empty(paddings)) {
			addRow(paddings);
			addRow(new CSpan(message), $cssClass);
			addRow(paddings);
		}
		else {
			addRow(new CSpan(message), $cssClass);
		}

		setFooter(new CDiv(buttons, "buttons"), "footer");

		parent::show($destroy);
	}
}
