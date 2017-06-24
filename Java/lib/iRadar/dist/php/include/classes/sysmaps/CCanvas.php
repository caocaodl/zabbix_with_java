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


class CCanvas {

	protected $canvas;
	protected $width;
	protected $height;
	protected $colors = CArray.array();

	public function __construct($w, $h) {
		width = $w;
		height = $h;

		if (function_exists("imagecreatetruecolor") && @imagecreatetruecolor(1, 1)) {
			canvas = imagecreatetruecolor(width, height);
		}
		else {
			canvas = imagecreate(width, height);
		}

		allocateColors();
	}

	public function getWidth() {
		return width;
	}

	public function getHeight() {
		return height;
	}

	public function fill($color) {
		imagefilledrectangle(canvas, 0, 0, width, height, getColor($color));
	}

	public function setBgImage($image) {
		$bg = imagecreatefromstring($image);
		imagecopy(canvas, $bg, 0, 0, 0, 0, imagesx($bg), imagesy($bg));
	}

	public function drawTitle($text, $color) {
		$x = width / 2 - imagefontwidth(4) * zbx_strlen($text) / 2;
		imagetext(canvas, 10, 0, $x, 25, getColor($color), $text);
	}

	public function drawBorder($color) {
		imagerectangle(canvas, 0, 0, width - 1, height - 1, getColor($color));
	}

	public function getCanvas() {
		$date = zbx_date2str(MAPS_DATE_FORMAT);
		imagestring(canvas, 0, width - 120, height - 12, $date, getColor("gray"));
		imagestringup(canvas, 0, width - 10, height - 50, ZABBIX_HOMEPAGE, getColor("gray"));

		return canvas;
	}

	public function drawLine($x1, $y1, $x2, $y2, $color, $drawtype) {
		myDrawLine(canvas, $x1, $y1, $x2, $y2, getColor($color), $drawtype);
	}

	public function drawText($fontsize, $angle, $x, $y, $color, $string) {
		imageText(canvas, $fontsize, $angle, $x, $y, getColor($color), $string);
	}

	public function drawRectangle($x1, $y1, $x2, $y2, $color) {
		imagerectangle(canvas, $x1, $y1, $x2, $y2, getColor($color));
	}

	public function drawRoundedRectangle($x1, $y1, $x2, $y2, $radius, $color) {
		$color = getColor($color);
		$arcRadius = $radius * 2;
		imagearc(canvas, $x1 + $radius, $y1 + $radius, $arcRadius, $arcRadius, 180, 270, $color);
		imagearc(canvas, $x1 + $radius, $y2 - $radius, $arcRadius, $arcRadius, 90, 180, $color);
		imagearc(canvas, $x2 - $radius, $y1 + $radius, $arcRadius, $arcRadius, 270, 0, $color);
		imagearc(canvas, $x2 - $radius, $y2 - $radius, $arcRadius, $arcRadius, 0, 90, $color);

		zbx_imageline(canvas, $x1 + $radius, $y1, $x2 - $radius, $y1, $color);
		zbx_imageline(canvas, $x1 + $radius, $y2, $x2 - $radius, $y2, $color);
		zbx_imageline(canvas, $x1, $y1 + $radius, $x1, $y2 - $radius, $color);
		zbx_imageline(canvas, $x2, $y1 + $radius, $x2, $y2 - $radius, $color);
	}

	protected function getColor($color) {
		if (!isset(colors[$color])) {
			throw new Exception("Color \"".$color."\" is not allocated.");
		}
		return colors[$color];
	}

	protected function allocateColors() {
		Nest.value(colors,"red").$() = imagecolorallocate(canvas, 255, 0, 0);
		Nest.value(colors,"darkred").$() = imagecolorallocate(canvas, 150, 0, 0);
		Nest.value(colors,"green").$() = imagecolorallocate(canvas, 0, 255, 0);
		Nest.value(colors,"darkgreen").$() = imagecolorallocate(canvas, 0, 150, 0);
		Nest.value(colors,"blue").$() = imagecolorallocate(canvas, 0, 0, 255);
		Nest.value(colors,"darkblue").$() = imagecolorallocate(canvas, 0, 0, 150);
		Nest.value(colors,"yellow").$() = imagecolorallocate(canvas, 255, 255, 0);
		Nest.value(colors,"darkyellow").$() = imagecolorallocate(canvas, 150, 150, 0);
		Nest.value(colors,"cyan").$() = imagecolorallocate(canvas, 0, 255, 255);
		Nest.value(colors,"black").$() = imagecolorallocate(canvas, 0, 0, 0);
		Nest.value(colors,"gray").$() = imagecolorallocate(canvas, 150, 150, 150);
		Nest.value(colors,"gray1").$() = imagecolorallocate(canvas, 180, 180, 180);
		Nest.value(colors,"gray2").$() = imagecolorallocate(canvas, 210, 210, 210);
		Nest.value(colors,"gray3").$() = imagecolorallocate(canvas, 240, 240, 240);
		Nest.value(colors,"white").$() = imagecolorallocate(canvas, 255, 255, 255);
		Nest.value(colors,"orange").$() = imagecolorallocate(canvas, 238, 96, 0);
	}
}
