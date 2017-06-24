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
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
**/


class CScreenScreen extends CScreenBase {

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		$screen = API.Screen().get(CArray.array(
			"screenids" => Nest.value(screenitem,"resourceid").$(),
			"output" => API_OUTPUT_EXTEND,
			"selectScreenItems" => API_OUTPUT_EXTEND
		));
		$screen = reset($screen);

		$screenBuilder = new CScreenBuilder(CArray.array(
			"isFlickerfree" => isFlickerfree,
			"mode" => (mode == SCREEN_MODE_EDIT || mode == SCREEN_MODE_SLIDESHOW) ? SCREEN_MODE_SLIDESHOW : SCREEN_MODE_PREVIEW,
			"timestamp" => timestamp,
			"screen" => $screen,
			"period" => Nest.value(timeline,"period").$(),
			"stime" => Nest.value(timeline,"stimeNow").$(),
			"profileIdx" => profileIdx,
			"updateProfile" => false
		));

		return getOutput($screenBuilder.show(), true);
	}
}
