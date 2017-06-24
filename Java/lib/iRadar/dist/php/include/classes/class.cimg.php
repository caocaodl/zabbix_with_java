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


class CImg extends CTag {

	public $preloader;

	public function __construct($src, $name = null, $width = null, $height = null, $class = null) {
		if (is_null($name)) {
			$name = "image";
		}

		parent::__construct("img", "no");
		tag_start = "";
		tag_end = "";
		tag_body_start = "";
		tag_body_end = "";
		setAttribute("border", 0);
		setName($name);
		setAltText($name);
		setSrc($src);
		setWidth($width);
		setHeight($height);
		attr("class", $class);
	}

	public function setSrc($value) {
		if (!is_string($value)) {
			return error("Incorrect value for SetSrc \"".$value."\".");
		}
		return setAttribute("src", $value);
	}

	public function setAltText($value = null) {
		if (!is_string($value)) {
			return error("Incorrect value for SetText \"".$value."\".");
		}
		return setAttribute("alt", $value);
	}

	public function setMap($value = null) {
		if (is_null($value)) {
			deleteOption("usemap");
		}
		if (!is_string($value)) {
			return error("Incorrect value for SetMap \"".$value."\".");
		}
		$value = "#".ltrim($value, "#");
		return setAttribute("usemap", $value);
	}

	public function setWidth($value = null) {
		if (is_null($value)) {
			return removeAttribute("width");
		}
		elseif (is_numeric($value) || is_int($value)) {
			return setAttribute("width", $value);
		}
		else {
			return error("Incorrect value for SetWidth \"".$value."\".");
		}
	}

	public function setHeight($value = null) {
		if (is_null($value)) {
			return removeAttribute("height");
		}
		elseif (is_numeric($value) || is_int($value)) {
			return setAttribute("height", $value);
		}
		else {
			return error("Incorrect value for SetHeight \"".$value."\".");
		}
	}

	public function preload() {
		$id = getAttribute("id");
		if (empty($id)) {
			$id = "img".uniqid();
			setAttribute("id", $id);
		}

		insert_js(
			"jQuery(".CJs::encodeJson(toString()).').load(function() {
				var parent = jQuery(\"#".$id."preloader\").parent();
				jQuery(\"#".$id."preloader\").remove();
				jQuery(parent).append(jQuery(this));
			});',
			true
		);

		addClass("preloader");
		setAttribute("id", $id."preloader");
		setAttribute("src", "styles/themes/".getUserTheme(CWebUser::$data)."/images/preloader.gif");
	}
}
