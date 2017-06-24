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


class CTweenBox {

	public function __construct(&$form, $name, $value = null, $size = 10) {
		zbx_add_post_js("if (IE7) $$(\"select option[disabled]\").each(function(e) { e.setStyle({color: \"gray\"}); });");

		form = &$form;
		name = $name."_tweenbox";
		varname = $name;
		value = zbx_toHash($value);
		id_l = varname."_left";
		id_r = varname."_right";
		lbox = new CListBox(id_l, null, $size);
		rbox = new CListBox(id_r, null, $size);
		lbox->setAttribute("style", "width: 280px;");
		rbox->setAttribute("style", "width: 280px;");
	}

	public function setName($name = null) {
		if (is_string($name)) {
			name = $name;
		}
	}

	public function getName() {
		return name;
	}

	public function addItem($value, $caption, $selected = null, $enabled = "yes") {
		if (is_null($selected)) {
			if (is_array(value)) {
				if (isset(value[$value])) {
					$selected = 1;
				}
			}
			elseif (strcmp($value, value) == 0) {
				$selected = 1;
			}
		}
		if ((is_bool($selected) && $selected)
				|| (is_int($selected) && $selected != 0)
				|| (is_string($selected) && ($selected == "yes" || $selected == "selected" || $selected == "on"))) {
			lbox->addItem($value, $caption, null, $enabled);
			form->addVar(varname."[".$value."]", $value);
		}
		else {
			rbox->addItem($value, $caption, null, $enabled);
		}
	}

	public function get($caption_l = null, $caption_r = null) {
		if (empty($caption_l)) {
			$caption_l = _("In");
		}
		if (empty($caption_r)) {
			$caption_r = _("Other");
		}

		$grp_tab = new CTable(null, "tweenBoxTable");
		$grp_tab.attr("name", name);
		$grp_tab.attr("id", zbx_formatDomId(name));
		$grp_tab.setCellSpacing(0);
		$grp_tab.setCellPadding(0);

		if (!is_null($caption_l) || !is_null($caption_r)) {
			$grp_tab.addRow(CArray.array($caption_l, SPACE, $caption_r));
		}

		$add_btn = new CButton("add", "  &laquo;  ", null, "formlist");
		$add_btn.setAttribute("onclick", "moveListBoxSelectedItem(\"".form->getName()."\", \"".varname."\", \"".id_r."\", \"".id_l."\", \"add\");");
		$rmv_btn = new CButton("remove", "  &raquo;  ", null, "formlist");
		$rmv_btn.setAttribute("onclick", "moveListBoxSelectedItem(\"".form->getName()."\", \"".varname."\", \"".id_l."\", \"".id_r."\", \"rmv\");");

		$grp_tab.addRow(CArray.array(lbox, new CCol(CArray.array($add_btn, BR(), $rmv_btn)), rbox));
		return $grp_tab;
	}

	public function show($caption_l = null, $caption_r = null) {
		if (empty($caption_l)) {
			$caption_l = _("In");
		}
		if (empty($caption_r)) {
			$caption_r = _("Other");
		}
		$tab = get($caption_l, $caption_r);
		$tab.show();
	}

	public function toString() {
		$tab = get();
		return $tab.toString();
	}
}
