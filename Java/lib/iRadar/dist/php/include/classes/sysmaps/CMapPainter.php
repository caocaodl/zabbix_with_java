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


class CMapPainter {

	protected $canvas;
	protected $mapData;
	protected $options;

	public function __construct(array $mapData, array $options = CArray.array()) {
		options = CArray.array(
			"map" => CArray.array(
				"bgColor" => "white",
				"borderColor" => "black",
				"titleColor" => "darkred",
				"border" => true,
				"drawAreas" => true
			),
			"grid" => CArray.array(
				"size" => 50,
				"color" => "black"
			)
		);
		for($options as $key => $option) {
			options[$key] = array_merge(options[$key], $option);
		}

		canvas = new CCanvas(Nest.value($mapData,"width").$(), Nest.value($mapData,"height").$());

		mapData = $mapData;
	}

	public function paint() {
		paintBackground();
		paintTitle();
		paintGrid();

		if (Nest.value(options,"map","drawAreas").$()) {
			paintAreas();
		}

		paintBorder();

		return canvas->getCanvas();
	}

	protected function paintBorder() {
		if (Nest.value(options,"map","border").$()) {
			canvas->drawBorder(Nest.value(options,"map","borderColor").$());
		}
	}

	protected function paintBackground() {
		canvas->fill(Nest.value(options,"map","bgColor").$());
		if (Nest.value(mapData,"backgroundid").$() && ($bgImage = get_image_by_imageid(Nest.value(mapData,"backgroundid").$()))) {
			canvas->setBgImage(Nest.value($bgImage,"image").$());
		}
	}

	protected function paintTitle() {
		canvas->drawTitle(Nest.value(mapData,"name").$(), Nest.value(options,"map","titleColor").$());
	}

	protected function paintGrid() {
		$size = Nest.value(options,"grid","size").$();
		if (empty($size)) {
			return;
		}

		$width = canvas->getWidth();
		$height = canvas->getHeight();
		$maxSize = max($width, $height);

		$dims = imageTextSize(8, 0, "00");
		for ($xy = $size; $xy < $maxSize; $xy += $size) {
			if ($xy < $width) {
				canvas->drawLine($xy, 0, $xy, $height, Nest.value(options,"grid","color").$(), MAP_LINK_DRAWTYPE_DASHED_LINE);
				canvas->drawText(8, 0, $xy + 3, Nest.value($dims,"height").$() + 3, Nest.value(options,"grid","color").$(), $xy);
			}
			if ($xy < $height) {
				canvas->drawLine(0, $xy, $width, $xy, Nest.value(options,"grid","color").$(), MAP_LINK_DRAWTYPE_DASHED_LINE);
				canvas->drawText(8, 0, 3, $xy + Nest.value($dims,"height").$() + 3, Nest.value(options,"grid","color").$(), $xy);
			}
		}

		canvas->drawText(8, 0, 2, Nest.value($dims,"height").$() + 3, "black", "Y X:");

	}

	protected function paintAreas() {
		for(Nest.value(mapData,"selements").$() as $selement) {
			if (Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS
					&& Nest.value($selement,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				canvas->drawRectangle(
					Nest.value($selement,"x").$() + 1,
					Nest.value($selement,"y").$() + 1,
					Nest.value($selement,"x").$() + Nest.value($selement,"width").$() - 1,
					Nest.value($selement,"y").$() + Nest.value($selement,"height").$() - 1,
					"gray1"
				);
				canvas->drawRectangle(
					Nest.value($selement,"x").$(),
					Nest.value($selement,"y").$(),
					Nest.value($selement,"x").$() + Nest.value($selement,"width").$(),
					Nest.value($selement,"y").$() + Nest.value($selement,"height").$(),
					"gray2"
				);
				canvas->drawRectangle(
					Nest.value($selement,"x").$() - 1,
					Nest.value($selement,"y").$() - 1,
					Nest.value($selement,"x").$() + Nest.value($selement,"width").$() + 1,
					Nest.value($selement,"y").$() + Nest.value($selement,"height").$() + 1,
					"gray3"
				);
			}
		}
	}
}
