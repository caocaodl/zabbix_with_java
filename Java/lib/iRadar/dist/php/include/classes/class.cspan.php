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


class CSpan extends CTag {

	public function __construct($items = null, $class = null, $id = null) {
		parent::__construct("span", "yes");
		attr("class", $class);
		attr("id", $id);
		addItem($items);
		tag_body_start = "";
		tag_start = "";
		tag_end = "";
		tag_body_start = "";
		tag_body_end = "";
	}
}
