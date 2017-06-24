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


class CWidget {

	public $state;
	public $flicker_state;
	private $css_class;
	private $pageHeaders;
	private $headers;
	private $flicker = CArray.array();

	/**
	 * The contents of the body of the widget.
	 *
	 * @var array
	 */
	protected $body = CArray.array();

	/**
	 * The class of the root div element.
	 *
	 * @var string
	 */
	protected $rootClass;

	/**
	 * The ID of the div, containing the body of the widget.
	 *
	 * @var string
	 */
	protected $bodyId;

	public function __construct($bodyId = null, $rootClass = null) {
		if (is_null($bodyId)) {
			list($usec, $sec) = explode(" ", microtime());
			$bodyId = "widget_".(int)($sec % 10).(int)($usec * 1000);
		}
		bodyId = $bodyId;
		flicker_state = 1; // 0 - closed, 1 - opened
		css_class = is_null(state) ? "header_wide" : "header";
		setRootClass($rootClass);
	}

	public function setClass($class = null) {
		if (is_string($class)) {
			css_class = $class;
		}
	}

	public function addPageHeader($left = SPACE, $right = SPACE) {
		zbx_value2CArray.array($right);

		pageHeaders[] = CArray.array("left" => $left, "right" => $right);
	}

	public function addHeader($left = SPACE, $right = SPACE) {
		zbx_value2CArray.array($right);

		headers[] = CArray.array("left" => $left, "right" => $right);
	}

	public function addHeaderRowNumber($right = SPACE) {
		$numRows = new CDiv();
		$numRows.setAttribute("name", "numrows");
		addHeader($numRows, $right);
	}

	public function addFlicker($items = null, $state = 0) {
		if (!is_null($items)) {
			flicker[] = $items;
		}
		flicker_state = $state;
	}

	public function addItem($items = null) {
		if (!is_null($items)) {
			body[] = $items;
		}
	}

	public function get() {
		$widget = CArray.array();
		if (!empty(pageHeaders)) {
			$widget[] = createPageHeader();
		}
		if (!empty(headers)) {
			$widget[] = createHeader();
		}
		if (is_null(state)) {
			state = true;
		}
		if (!empty(flicker)) {
			$flicker_domid = "flicker_".bodyId;
			$flicker_tab = new CTable();
			$flicker_tab.setAttribute("width", "100%");
			$flicker_tab.setCellPadding(0);
			$flicker_tab.setCellSpacing(0);

			$div = new CDiv(flicker, null, $flicker_domid);
			if (!flicker_state) {
				$div.setAttribute("style", "display: none;");
			}

			$icon_l = new CDiv(SPACE.SPACE, (flicker_state ? "dbl_arrow_up" : "dbl_arrow_down"), "flicker_icon_l");
			$icon_l.setAttribute("title", _("Maximize")."/"._("Minimize"));

			$icon_r = new CDiv(SPACE.SPACE, (flicker_state ? "dbl_arrow_up" : "dbl_arrow_down"), "flicker_icon_r");
			$icon_r.setAttribute("title", _("Maximize")."/"._("Minimize"));

			$icons_row = new CTable(null, "textwhite");
			$icons_row.addRow(CArray.array($icon_l, new CSpan(SPACE._("Filter").SPACE), $icon_r));

			$thin_tab = createFlicker($icons_row);
			$thin_tab.attr("id", "filter_icon");
			$thin_tab.addAction("onclick", \"javascript: changeFlickerState("\".$flicker_domid.\"");\");

			$flicker_tab.addRow($thin_tab, "textcolorstyles pointer");
			$flicker_tab.addRow($div);

			$widget[] = $flicker_tab;
		}
		$div = new CDiv(body, "w");
		$div.setAttribute("id", bodyId);
		if (!state) {
			$div.setAttribute("style", "display: none;");
		}
		$widget[] = $div;

		return new CDiv($widget, getRootClass());
	}

	public function show() {
		echo toString();
	}

	public function toString() {
		$tab = get();

		return unpack_object($tab);
	}

	private function createPageHeader() {
		$pageHeader = CArray.array();

		for(pageHeaders as $header) {
			$pageHeader[] = get_table_header(Nest.value($header,"left").$(), Nest.value($header,"right").$());
		}

		return new CDiv($pageHeader);
	}

	private function createHeader() {
		$header = reset(headers);

		$columnRights = CArray.array();

		if (!is_null(Nest.value($header,"right").$())) {
			for(Nest.value($header,"right").$() as $right) {
				$columnRights[] = new CDiv($right, "floatright");
			}
		}

		if (!is_null(state)) {
			$icon = new CIcon(_("Show")."/"._("Hide"), (state ? "arrowup" : "arrowdown"), \"change_hat_state(this, "\".bodyId.\"");\");
			$icon.setAttribute("id", bodyId."_icon");
			$columnRights[] = $icon;
		}

		if ($columnRights) {
			$columnRights = array_reverse($columnRights);
		}

		// header table
		$table = new CTable(null, css_class." maxwidth");
		$table.setCellSpacing(0);
		$table.setCellPadding(1);
		$table.addRow(createHeaderRow(Nest.value($header,"left").$(), $columnRights), "first");

		if (css_class != "header_wide") {
			$table.addClass("ui-widget-header ui-corner-all");
		}

		for(headers as $num => $header) {
			if ($num > 0) {
				$table.addRow(createHeaderRow(Nest.value($header,"left").$(), Nest.value($header,"right").$()), "next");
			}
		}

		return new CDiv($table);
	}

	private function createHeaderRow($col1, $col2 = SPACE) {
		$td_r = new CCol($col2, "header_r right");
		$row = CArray.array(new CCol($col1, "header_l left"), $td_r);
		return $row;
	}

	private function createFlicker($col1, $col2 = null) {
		$table = new CTable(null, "textwhite maxwidth middle flicker");
		$table.setCellSpacing(0);
		$table.setCellPadding(1);
		if (!is_null($col2)) {
			$td_r = new CCol($col2, "flicker_r");
			$td_r.setAttribute("align","right");
			$table.addRow(CArray.array(new CCol($col1,"flicker_l"), $td_r));
		}
		else {
			$td_c = new CCol($col1, "flicker_c");
			$td_c.setAttribute("align", "center");
			$table.addRow($td_c);
		}
		return $table;
	}

	public function setRootClass($rootClass) {
		rootClass = $rootClass;
	}

	public function getRootClass() {
		return rootClass;
	}
}
