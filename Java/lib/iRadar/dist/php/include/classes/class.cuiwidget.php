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


class CUIWidget extends CDiv {

	public $domid;
	public $state;
	public $css_class;
	private $_header;
	private $_body;
	private $_footer;

	public function __construct($id, $body = null, $state = null) {
		domid = $id;
		state = $state; // 0 - closed, 1 - opened
		css_class = "header";
		_header = null;
		_body = CArray.array($body);
		_footer = null;

		parent::__construct(null, "ui-widget ui-widget-content ui-helper-clearfix ui-corner-all widget");
		setAttribute("id", $id."_widget");
	}

	public function addItem($item) {
		if (!is_null($item)) {
			_body[] = $item;
		}
	}

	public function setHeader($caption = null, $icons = SPACE) {
		zbx_value2CArray.array($icons);
		if (is_null($caption) && !is_null($icons)) {
			$caption = SPACE;
		}
		_header = new CDiv(null, "nowrap ui-corner-all ui-widget-header ".css_class);

		if (!is_null(state)) {
			$icon = new CIcon(
				_("Show")."/"._("Hide"),
				state ? "arrowup" : "arrowdown",
				\"changeHatStateUI(this,"\".domid.\"");\"
			);
			$icon.setAttribute("id", domid."_icon");
			_header->addItem($icon);
		}
		_header->addItem($icons);
		_header->addItem($caption);
		return _header;
	}

	public function setDoubleHeader($left, $right) {
		$table = new CTable();
		$table.addStyle("width: 100%;");
		$lCol = new CCol($left);
		$lCol.addStyle("text-align: left; border: 0;");
		$rCol = new CCol($right);
		$rCol.addStyle("text-align: right; border: 0;");
		$table.addRow(CArray.array($lCol, $rCol));

		_header = new CDiv(null, "nowrap ui-corner-all ui-widget-header ".css_class);
		_header->addItem($table);
		return _header;
	}

	public function setFooter($footer, $right = false) {
		_footer = new CDiv($footer, "nowrap ui-corner-all ui-widget-header footer ".($right ? " right" : " left"));
		return _footer;
	}

	public function get() {
		cleanItems();
		parent::addItem(_header);

		if (is_null(state)) {
			state = true;
		}

		$div = new CDiv(_body, "body");
		$div.setAttribute("id", domid);

		if (!state) {
			$div.setAttribute("style", "display: none;");
			if (_footer) {
				_footer->setAttribute("style", "display: none;");
			}
		}

		parent::addItem($div);
		parent::addItem(_footer);
		return $this;
	}

	public function toString($destroy = true) {
		get();
		return parent::toString($destroy);
	}
}
