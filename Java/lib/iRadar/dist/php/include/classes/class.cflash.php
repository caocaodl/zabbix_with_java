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

class CFlash extends CTag {

	public $srcParam;
	public $embededFlash;

	public function __construct($src = null, $width = null, $height = null) {
		parent::__construct("object", "yes");
		Nest.value(attributes,"classid").$() = "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000";
		Nest.value(attributes,"codebase").$() = "http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0";
		Nest.value(attributes,"align").$() = "middle";

		addItem(new CParam("allowScriptAccess", "sameDomain"));
		addItem(new CParam("quality", "high"));
		addItem(new CParam("wmode", "opaque"));

		srcParam = new CParam("movie", $src);
		embededFlash = new CFlashEmbed();

		setWidth($width);
		setHeight($height);
		setSrc($src);
	}

	public function setWidth($value) {
		Nest.value(attributes,"width").$() = $value;
		embededFlash->Nest.value(attributes,"width").$() = $value;
	}

	public function setHeight($value) {
		Nest.value(attributes,"height").$() = $value;
		embededFlash->Nest.value(attributes,"height").$() = $value;
	}

	public function setSrc($value) {
		srcParam->Nest.value(attributes,"value").$() = $value;
		embededFlash->Nest.value(attributes,"src").$() = $value;
	}

	public function bodyToString() {
		$ret = parent::bodyToString();
		$ret .= srcParam->toString();
		$ret .= embededFlash->toString();
		return $ret;
	}
}
?>
