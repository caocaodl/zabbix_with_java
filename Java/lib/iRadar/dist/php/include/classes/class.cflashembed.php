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
?>
<?php

class CFlashEmbed extends CTag {

	public function __construct($src = null, $width = null, $height = null) {
		parent::__construct("embed");
		Nest.value(attributes,"allowScriptAccess").$() = "sameDomain";
		Nest.value(attributes,"type").$() = "application/x-shockwave-flash";
		Nest.value(attributes,"pluginspage").$() = "http://www.macromedia.com/go/getflashplayer";
		Nest.value(attributes,"align").$() = "middle";
		Nest.value(attributes,"quality").$() = "high";
		Nest.value(attributes,"wmode").$() = "opaque";
		Nest.value(attributes,"width").$() = $width;
		Nest.value(attributes,"height").$() = $height;
		Nest.value(attributes,"src").$() = $src;
	}

	public function setWidth($value) {
		Nest.value(attributes,"width").$()  = $value;
	}

	public function setHeight($value) {
		Nest.value(attributes,"height").$() = $value;
	}

	public function setSrc($value) {
		Nest.value(attributes,"src").$() = $value;
	}
}
?>
